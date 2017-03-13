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

import ome.xml.model.primitives.Timestamp;

import loci.common.DateTools;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

/**
 * OpenlabRawReader is the file format reader for Openlab RAW files.
 * Specifications available at
 * http://www.improvision.com/support/tech_notes/detail.php?id=344
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class OpenlabRawReader extends FormatReader {

  // -- Constants --

  public static final String OPENLAB_RAW_MAGIC_STRING = "OLRW";

  private static final int HEADER_SIZE = 288;

  // -- Fields --

  /** Offset to each image's pixel data. */
  protected int[] offsets;

  /** Number of bytes per pixel. */
  private int bytesPerPixel;

  // -- Constructor --

  /** Constructs a new RAW reader. */
  public OpenlabRawReader() {
    super("Openlab RAW", "raw");
    suffixSufficient = false;
    domains = new String[] {FormatTools.UNKNOWN_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = OPENLAB_RAW_MAGIC_STRING.length();
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readString(blockLen).startsWith(OPENLAB_RAW_MAGIC_STRING);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    in.seek(offsets[no / getSizeC()] + HEADER_SIZE);
    readPlane(in, x, y, w, h, buf);

    if (FormatTools.getBytesPerPixel(getPixelType()) == 1) {
      // need to invert the pixels
      for (int i=0; i<buf.length; i++) {
        buf[i] = (byte) (255 - buf[i]);
      }
    }
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      offsets = null;
      bytesPerPixel = 0;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    // read the 12 byte file header

    LOGGER.info("Verifying Openlab RAW format");

    if (!in.readString(4).equals("OLRW")) {
      throw new FormatException("Openlab RAW magic string not found.");
    }

    LOGGER.info("Populating metadata");

    CoreMetadata m = core.get(0);

    int version = in.readInt();

    m.imageCount = in.readInt();
    offsets = new int[getImageCount()];
    offsets[0] = 12;

    in.skipBytes(8);
    m.sizeX = in.readInt();
    m.sizeY = in.readInt();
    in.skipBytes(1);
    m.sizeC = in.read();
    bytesPerPixel = in.read();
    in.skipBytes(1);

    long stampMs = in.readLong();
    if (stampMs > 0) {
      stampMs /= 1000000;
      stampMs -= (67 * 365.25 * 24 * 60 * 60);
    }
    else stampMs = System.currentTimeMillis();

    String stamp = DateTools.convertDate(stampMs, DateTools.UNIX);

    in.skipBytes(4);
    int len = in.read() & 0xff;
    String imageName = in.readString(len - 1).trim();

    if (getSizeC() <= 1) m.sizeC = 1;
    else m.sizeC = 3;

    int plane = getSizeX() * getSizeY() * bytesPerPixel;
    for (int i=1; i<getImageCount(); i++) {
      offsets[i] = offsets[i - 1] + HEADER_SIZE + plane;
    }

    m.sizeZ = getImageCount();
    m.sizeT = 1;
    m.rgb = getSizeC() > 1;
    m.dimensionOrder = isRGB() ? "XYCZT" : "XYZTC";
    m.interleaved = false;
    m.littleEndian = false;
    m.metadataComplete = true;
    m.indexed = false;
    m.falseColor = false;

    switch (bytesPerPixel) {
      case 1:
      case 3:
        m.pixelType = FormatTools.UINT8;
        break;
      case 2:
        m.pixelType = FormatTools.UINT16;
        break;
      default:
        m.pixelType = FormatTools.FLOAT;
    }

    addGlobalMeta("Width", getSizeX());
    addGlobalMeta("Height", getSizeY());
    addGlobalMeta("Bytes per pixel", bytesPerPixel);
    addGlobalMeta("Image name", imageName);
    addGlobalMeta("Timestamp", stamp);
    addGlobalMeta("Version", version);

    // The metadata store we're working with.
    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
    if (stamp != null) {
      store.setImageAcquisitionDate(new Timestamp(stamp), 0);
    }
    store.setImageName(imageName, 0);
  }

}
