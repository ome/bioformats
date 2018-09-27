/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.formats.in;

import java.io.IOException;

import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

import ome.units.quantity.ElectricPotential;
import ome.units.quantity.Length;
import ome.units.UNITS;


/**
 * AliconaReader is the file format reader for Alicona AL3D files.
 */
public class AliconaReader extends FormatReader {

  // -- Constants --

  public static final String AL3D_MAGIC_STRING = "Alicona";

  // -- Fields --

  /** Image offset. */
  private int textureOffset;

  /** Number of bytes per pixel (either 1 or 2). */
  private int numBytes;

  // -- Constructor --

  /** Constructs a new Alicona reader. */
  public AliconaReader() {
    super("Alicona AL3D", "al3d");
    domains = new String[] {FormatTools.SEM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 16;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return (stream.readString(blockLen)).indexOf(AL3D_MAGIC_STRING) >= 0;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int pad = (8 - (getSizeX() % 8)) % 8;

    int planeSize = (getSizeX() + pad) * getSizeY();

    if (getPixelType() == FormatTools.FLOAT) {
      in.seek(textureOffset);
      readPlane(in, x, y, w, h, buf);
      return buf;
    }

    // 16-bit images are stored in a non-standard format:
    // all of the LSBs are stored together, followed by all of the MSBs
    // so instead of LMLMLM... storage, we have LLLLL...MMMMM...
    for (int i=0; i<numBytes; i++) {
      in.seek(textureOffset + (no * planeSize * (i + 1)));
      in.skipBytes(y * (getSizeX() + pad));
      if (getSizeX() == w) {
        in.read(buf, i * w * h, w * h);
      }
      else {
        for (int row=0; row<h; row++) {
          in.skipBytes(x);
          in.read(buf, i * w * h + row * w, w);
          in.skipBytes(getSizeX() + pad - x - w);
        }
      }
    }

    if (numBytes > 1) {
      byte[] tmp = new byte[buf.length];
      for (int i=0; i<planeSize; i++) {
        for (int j=0; j<numBytes; j++) {
          tmp[i*numBytes + j] = buf[planeSize*j + i];
        }
      }
      System.arraycopy(tmp, 0, buf, 0, tmp.length);
      tmp = null;
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      textureOffset = numBytes = 0;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    CoreMetadata m = core.get(0);

    // check that this is a valid AL3D file
    LOGGER.info("Verifying Alicona format");
    String magicString = in.readString(17);
    if (!magicString.trim().equals("AliconaImaging")) {
      throw new FormatException("Invalid magic string : " +
        "expected 'AliconaImaging', got " + magicString);
    }

    // now we read a series of tags
    // each one is 52 bytes - 20 byte key + 30 byte value + 2 byte CRLF

    LOGGER.info("Reading tags");

    int count = 2;

    boolean hasC = false;
    String voltage = null, magnification = null, workingDistance = null;
    String pntX = null, pntY = null;
    int depthOffset = 0;

    for (int i=0; i<count; i++) {
      String key = in.readString(20).trim();
      String value = in.readString(30).trim();

      addGlobalMeta(key, value);
      in.skipBytes(2);

      if (key.equals("TagCount")) count += Integer.parseInt(value);
      else if (key.equals("Rows")) m.sizeY = Integer.parseInt(value);
      else if (key.equals("Cols")) m.sizeX = Integer.parseInt(value);
      else if (key.equals("NumberOfPlanes")) {
        m.imageCount = Integer.parseInt(value);
      }
      else if (key.equals("TextureImageOffset")) {
        textureOffset = Integer.parseInt(value);
      }
      else if (key.equals("TexturePtr") && !value.equals("7")) hasC = true;
      else if (key.equals("Voltage")) voltage = value;
      else if (key.equals("Magnification")) magnification = value;
      else if (key.equals("PixelSizeXMeter")) pntX = value;
      else if (key.equals("PixelSizeYMeter")) pntY = value;
      else if (key.equals("WorkingDistance")) workingDistance = value;
      else if (key.equals("DepthImageOffset")) {
        depthOffset = Integer.parseInt(value);
      }
    }

    LOGGER.info("Populating metadata");

    if (textureOffset != 0) {
      numBytes = (int) (in.length() - textureOffset) /
        (getSizeX() * getSizeY() * getImageCount());

      m.sizeC = hasC ? 3 : 1;
      m.sizeZ = 1;
      m.sizeT = getImageCount() / getSizeC();

      m.pixelType =
        FormatTools.pixelTypeFromBytes(numBytes, false, false);
    }
    else {
      textureOffset = depthOffset;
      m.pixelType = FormatTools.FLOAT;
      m.sizeC = 1;
      m.sizeZ = 1;
      m.sizeT = 1;
      m.imageCount = 1;
    }

    m.rgb = false;
    m.interleaved = false;
    m.littleEndian = true;
    m.dimensionOrder = "XYCTZ";
    m.metadataComplete = true;
    m.indexed = false;
    m.falseColor = false;

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    // populate Image data

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      // link Image and Instrument
      String instrumentID = MetadataTools.createLSID("Instrument", 0);
      store.setInstrumentID(instrumentID, 0);
      store.setImageInstrumentRef(instrumentID, 0);

      // populate Detector data

      // According to the spec, the voltage and magnification values are those
      // used when the dataset was acquired, i.e. detector settings.
      if (voltage != null) {
        store.setDetectorSettingsVoltage(
                new ElectricPotential(new Double(voltage), UNITS.VOLT), 0, 0);

        // link DetectorSettings to an actual Detector
        String detectorID = MetadataTools.createLSID("Detector", 0, 0);
        store.setDetectorID(detectorID, 0, 0);
        store.setDetectorSettingsID(detectorID, 0, 0);

        // set required Detector type
        store.setDetectorType(MetadataTools.getDetectorType("Other"), 0, 0);
      }

      // populate Objective data

      if (magnification != null) {
        store.setObjectiveCalibratedMagnification(
          new Double(magnification), 0, 0);
      }

      if (workingDistance != null) {
        store.setObjectiveWorkingDistance(new Length(new Double(workingDistance), UNITS.MICROMETER), 0, 0);
      }

      store.setObjectiveCorrection(MetadataTools.getCorrection("Other"), 0, 0);
      store.setObjectiveImmersion(MetadataTools.getImmersion("Other"), 0, 0);

      // link Objective to an Image using ObjectiveSettings
      String objectiveID = MetadataTools.createLSID("Objective", 0, 0);
      store.setObjectiveID(objectiveID, 0, 0);
      store.setObjectiveSettingsID(objectiveID, 0);

      // populate Dimensions data

      if (pntX != null && pntY != null) {
        double pixelSizeX = Double.parseDouble(pntX);
        double pixelSizeY = Double.parseDouble(pntY);

        Length sizeX = FormatTools.getPhysicalSizeX(pixelSizeX, UNITS.METER);
        Length sizeY = FormatTools.getPhysicalSizeY(pixelSizeY, UNITS.METER);

        if (sizeX != null) {
          store.setPixelsPhysicalSizeX(sizeX, 0);
        }
        if (sizeY != null) {
          store.setPixelsPhysicalSizeY(sizeY, 0);
        }
      }
    }
  }

}
