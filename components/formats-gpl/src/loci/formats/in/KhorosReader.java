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

/**
 * Reader for Khoros XV files.
 */
public class KhorosReader extends FormatReader {

  // -- Constants --

  public static final int KHOROS_MAGIC_BYTES = 0xab01;

  // -- Fields --

  /** Global lookup table. */
  private byte[][] lut;

  /** Image offset. */
  private long offset;

  // -- Constructor --

  /** Constructs a new Khoros reader. */
  public KhorosReader() {
    super("Khoros XV", "xv");
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 2;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readShort() == KHOROS_MAGIC_BYTES;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  @Override
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    return lut;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int bufSize = FormatTools.getPlaneSize(this);
    in.seek(offset + no * bufSize);
    readPlane(in, x, y, w, h, buf);

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      lut = null;
      offset = 0;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    in.skipBytes(4);
    in.order(true);
    int dependency = in.readInt();

    addGlobalMeta("Comment", in.readString(512));

    in.order(dependency == 4 || dependency == 8);

    CoreMetadata m = core.get(0);

    m.sizeX = in.readInt();
    m.sizeY = in.readInt();
    in.skipBytes(28);
    m.imageCount = in.readInt();
    if (getImageCount() == 0) m.imageCount = 1;
    m.sizeC = in.readInt();

    int type = in.readInt();

    switch (type) {
      case 0:
        m.pixelType = FormatTools.INT8;
        break;
      case 1:
        m.pixelType = FormatTools.UINT8;
        break;
      case 2:
        m.pixelType = FormatTools.UINT16;
        break;
      case 4:
        m.pixelType = FormatTools.INT32;
        break;
      case 5:
        m.pixelType = FormatTools.FLOAT;
        break;
      case 9:
        m.pixelType = FormatTools.DOUBLE;
        break;
      default: throw new FormatException("Unsupported pixel type : " + type);
    }

    // read lookup table

    in.skipBytes(12);
    int c = in.readInt();
    if (c > 1) {
      m.sizeC = c;
      int n = in.readInt();
      lut = new byte[c][n];
      in.skipBytes(436);

      for (int i=0; i<lut.length; i++) {
        for (int j=0; j<lut[0].length; j++) {
          lut[i][j] = in.readByte();
        }
      }
    }
    else in.skipBytes(440);
    offset = in.getFilePointer();

    m.sizeZ = getImageCount();
    m.sizeT = 1;
    m.rgb = getSizeC() > 1;
    m.interleaved = false;
    m.littleEndian = dependency == 4 || dependency == 8;
    m.dimensionOrder = "XYCZT";
    m.indexed = lut != null;
    m.falseColor = false;
    m.metadataComplete = true;

    if (isIndexed()) {
      m.sizeC = 1;
      m.rgb = false;
    }

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
  }

}
