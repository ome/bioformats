/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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

import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.UNITS;

/**
 * ImarisReader is the file format reader for Bitplane Imaris files.
 * Specifications available at
 * http://flash.bitplane.com/wda/interfaces/public/faqs/faqsview.cfm?inCat=0&inQuestionID=104
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class ImarisReader extends FormatReader {

  // -- Constants --

  /** Magic number; present in all files. */
  private static final int IMARIS_MAGIC_BYTES = 5021964;

  /** Specifies endianness. */
  private static final boolean IS_LITTLE = false;

  // -- Fields --

  /** Offsets to each image. */
  private int[] offsets;

  // -- Constructor --

  /** Constructs a new Imaris reader. */
  public ImarisReader() {
    super("Bitplane Imaris", "ims");
    suffixSufficient = false;
    domains = new String[] {FormatTools.UNKNOWN_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 4;
    if (!FormatTools.validStream(stream, blockLen, IS_LITTLE)) {
      return false;
    }
    return stream.readInt() == IMARIS_MAGIC_BYTES;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    in.seek(offsets[no] + getSizeX() * (getSizeY() - y - h));

    for (int row=h-1; row>=0; row--) {
      in.skipBytes(x);
      in.read(buf, row*w, w);
      in.skipBytes(getSizeX() - w - x);
    }
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) offsets = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    LOGGER.info("Verifying Imaris RAW format");

    in.order(IS_LITTLE);

    long magic = in.readInt();
    if (magic != IMARIS_MAGIC_BYTES) {
      throw new FormatException("Imaris magic number not found.");
    }

    LOGGER.info("Reading header");

    int version = in.readInt();
    in.skipBytes(4);

    String imageName = in.readString(128);

    CoreMetadata m = core.get(0);

    m.sizeX = in.readShort();
    m.sizeY = in.readShort();
    m.sizeZ = in.readShort();

    in.skipBytes(2);

    m.sizeC = in.readInt();
    in.skipBytes(2);

    String date = in.readString(32);

    float dx = in.readFloat();
    float dy = in.readFloat();
    float dz = in.readFloat();
    int mag = in.readShort();

    String description = in.readString(128);
    int isSurvey = in.readInt();

    LOGGER.info("Calculating image offsets");

    m.imageCount = getSizeZ() * getSizeC();
    offsets = new int[getImageCount()];

    float[] gains = new float[getSizeC()];
    float[] detectorOffsets = new float[getSizeC()];
    float[] pinholes = new float[getSizeC()];

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      for (int i=0; i<getSizeC(); i++) {
        addGlobalMeta("Channel #" + i + " Comment", in.readString(128));
        gains[i] = in.readFloat();
        detectorOffsets[i] = in.readFloat();
        pinholes[i] = in.readFloat();
        in.skipBytes(24);
      }
    }

    int offset = 336 + (164 * getSizeC());
    for (int i=0; i<getSizeC(); i++) {
      for (int j=0; j<getSizeZ(); j++) {
        offsets[i*getSizeZ() + j] = offset + (j * getSizeX() * getSizeY());
      }
      offset += getSizeX() * getSizeY() * getSizeZ();
    }

    addGlobalMeta("Version", version);
    addGlobalMeta("Image name", imageName);
    addGlobalMeta("Image comment", description);
    addGlobalMeta("Survey performed", isSurvey == 0);
    addGlobalMeta("Original date", date);

    LOGGER.info("Populating metadata");

    m.sizeT = getImageCount() / (getSizeC() * getSizeZ());
    m.dimensionOrder = "XYZCT";
    m.rgb = false;
    m.interleaved = false;
    m.littleEndian = IS_LITTLE;
    m.indexed = false;
    m.falseColor = false;
    m.metadataComplete = true;
    m.pixelType = FormatTools.UINT8;

    // The metadata store we're working with.
    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    // populate Image data

    store.setImageName(imageName, 0);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      store.setImageDescription(description, 0);

      // link Instrument and Image
      String instrumentID = MetadataTools.createLSID("Instrument", 0);
      store.setInstrumentID(instrumentID, 0);
      store.setImageInstrumentRef(instrumentID, 0);

      // populate Dimensions data

      Length sizeX = FormatTools.getPhysicalSizeX((double) dx);
      Length sizeY = FormatTools.getPhysicalSizeY((double) dy);
      Length sizeZ = FormatTools.getPhysicalSizeZ((double) dz);

      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, 0);
      }
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, 0);
      }
      if (sizeZ != null) {
        store.setPixelsPhysicalSizeZ(sizeZ, 0);
      }
      store.setPixelsTimeIncrement(new Time(1.0, UNITS.SECOND), 0);

      // populate LogicalChannel data

      for (int i=0; i<getSizeC(); i++) {
        if (pinholes[i] > 0) {
          store.setChannelPinholeSize(new Length(pinholes[i], UNITS.MICROMETER), 0, i);
        }
      }

      // populate Detector data

      for (int i=0; i<getSizeC(); i++) {
        if (gains[i] > 0) {
          store.setDetectorSettingsGain((double) gains[i], 0, i);
        }
        store.setDetectorSettingsOffset((double) offsets[i], i, 0);

        // link DetectorSettings to an actual Detector
        String detectorID = MetadataTools.createLSID("Detector", 0, i);
        store.setDetectorID(detectorID, 0, i);
        store.setDetectorType(getDetectorType("Other"), 0, i);
        store.setDetectorSettingsID(detectorID, 0, i);
      }
    }
  }

}
