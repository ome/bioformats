//
// AliconaReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.in;

import java.io.IOException;

import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import ome.xml.model.primitives.PositiveFloat;

/**
 * AliconaReader is the file format reader for Alicona AL3D files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/AliconaReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/AliconaReader.java;hb=HEAD">Gitweb</a></dd></dl>
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
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 16;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return (stream.readString(blockLen)).indexOf(AL3D_MAGIC_STRING) >= 0;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
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
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      textureOffset = numBytes = 0;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

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
      else if (key.equals("Rows")) core[0].sizeY = Integer.parseInt(value);
      else if (key.equals("Cols")) core[0].sizeX = Integer.parseInt(value);
      else if (key.equals("NumberOfPlanes")) {
        core[0].imageCount = Integer.parseInt(value);
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

      core[0].sizeC = hasC ? 3 : 1;
      core[0].sizeZ = 1;
      core[0].sizeT = getImageCount() / getSizeC();

      core[0].pixelType =
        FormatTools.pixelTypeFromBytes(numBytes, false, false);
    }
    else {
      textureOffset = depthOffset;
      core[0].pixelType = FormatTools.FLOAT;
      core[0].sizeC = 1;
      core[0].sizeZ = 1;
      core[0].sizeT = 1;
      core[0].imageCount = 1;
    }

    core[0].rgb = false;
    core[0].interleaved = false;
    core[0].littleEndian = true;
    core[0].dimensionOrder = "XYCTZ";
    core[0].metadataComplete = true;
    core[0].indexed = false;
    core[0].falseColor = false;

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
        store.setDetectorSettingsVoltage(new Double(voltage), 0, 0);

        // link DetectorSettings to an actual Detector
        String detectorID = MetadataTools.createLSID("Detector", 0, 0);
        store.setDetectorID(detectorID, 0, 0);
        store.setDetectorSettingsID(detectorID, 0, 0);

        // set required Detector type
        store.setDetectorType(getDetectorType("Other"), 0, 0);
      }

      // populate Objective data

      if (magnification != null) {
        store.setObjectiveCalibratedMagnification(
          new Double(magnification), 0, 0);
      }

      if (workingDistance != null) {
        store.setObjectiveWorkingDistance(new Double(workingDistance), 0, 0);
      }

      store.setObjectiveCorrection(getCorrection("Other"), 0, 0);
      store.setObjectiveImmersion(getImmersion("Other"), 0, 0);

      // link Objective to an Image using ObjectiveSettings
      String objectiveID = MetadataTools.createLSID("Objective", 0, 0);
      store.setObjectiveID(objectiveID, 0, 0);
      store.setObjectiveSettingsID(objectiveID, 0);

      // populate Dimensions data

      if (pntX != null && pntY != null) {
        double pixelSizeX = Double.parseDouble(pntX) * 1000000;
        double pixelSizeY = Double.parseDouble(pntY) * 1000000;

        if (pixelSizeX > 0) {
          store.setPixelsPhysicalSizeX(new PositiveFloat(pixelSizeX), 0);
        }
        else {
          LOGGER.warn("Expected positive value for PhysicalSizeX; got {}",
            pixelSizeX);
        }
        if (pixelSizeY > 0) {
          store.setPixelsPhysicalSizeY(new PositiveFloat(pixelSizeY), 0);
        }
        else {
          LOGGER.warn("Expected positive value for PhysicalSizeY; got {}",
            pixelSizeY);
        }
      }
    }
  }

}
