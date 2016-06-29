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

import loci.common.DataTools;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.TargaRLECodec;
import loci.formats.meta.MetadataStore;

/**
 * File format reader for Truevision Targa files.
 */
public class TargaReader extends FormatReader {

  // -- Fields --

  private byte[][] colorMap;
  private long offset;
  private boolean compressed;
  private int orientation;
  private int bits;

  // -- Constructor --

  /** Constructs a new Targa reader. */
  public TargaReader() {
    super("Truevision Targa", "tga");
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  @Override
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    return isRGB() ? null : colorMap;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    in.seek(offset);

    int planeSize = FormatTools.getPlaneSize(this);
    TargaRLECodec codec = new TargaRLECodec();
    CodecOptions options = new CodecOptions();
    options.maxBytes = planeSize;
    options.bitsPerSample = bits;

    RandomAccessInputStream s = in;

    if (compressed) {
      byte[] b = codec.decompress(in, options);
      s = new RandomAccessInputStream(b);
      s.order(isLittleEndian());
    }

    int bpp = bits;
    while (bpp % 8 != 0) bpp++;
    bpp /= 8;

    int rowSkip = orientation < 2 ? (getSizeY() - h - y) : y;
    int colSkip = (orientation % 2) == 1 ? (getSizeX() - w - x) : x;

    s.skipBytes(rowSkip * getSizeX() * bpp);
    for (int row=0; row<h; row++) {
      if (s.getFilePointer() >= s.length()) break;
      s.skipBytes(colSkip * bpp);
      for (int col=0; col<w; col++) {
        if (s.getFilePointer() >= s.length()) break;
        int rowIndex = orientation < 2 ? h - row - 1 : row;
        int colIndex = (orientation % 2) == 1 ? w - col - 1 : col;
        int index = getSizeC() * (rowIndex * w + colIndex);
        if (bpp == 2) {
          int v = s.readShort();
          buf[index] = (byte) ((v & 0x7c00) >> 10);
          buf[index + 1] = (byte) ((v & 0x3e0) >> 5);
          buf[index + 2] = (byte) (v & 0x1f);
        }
        else if (bpp == 4) {
          buf[index + 2] = s.readByte();
          buf[index + 1] = s.readByte();
          buf[index] = s.readByte();
          s.skipBytes(1);
        }
        else {
          for (int c=getSizeC() - 1; c>=0; c--) {
            buf[index + c] = s.readByte();
          }
        }
      }
      s.skipBytes(bpp * (getSizeX() - w - colSkip));
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      colorMap = null;
      offset = 0;
      compressed = false;
      orientation = 0;
      bits = 0;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    CoreMetadata m = core.get(0);
    m.littleEndian = true;
    in.order(isLittleEndian());

    int nIdentificationChars = in.readUnsignedByte();
    boolean hasColorMap = in.readByte() == 1;

    int imageType = in.readByte();
    compressed = imageType == 9 || imageType == 10 || imageType == 11;

    // color map definition
    int colorMapOrigin = in.readUnsignedShort();
    int colorMapLength = in.readUnsignedShort();
    int bitsPerEntry = in.readUnsignedByte();

    in.skipBytes(4);
    m.sizeX = in.readUnsignedShort();
    m.sizeY = in.readUnsignedShort();
    bits = in.readUnsignedByte();
    m.bitsPerPixel = bits;

    int imageDescriptor = in.readUnsignedByte();
    orientation = (imageDescriptor & 0x30) >> 4;

    String identification = in.readString(nIdentificationChars);

    if (colorMapLength > 0) {
      colorMap = new byte[3][colorMapLength];

      int bpc = bitsPerEntry == 32 ? 8 : bitsPerEntry / 3;

      byte[] v = new byte[bitsPerEntry / 8];
      for (int i=0; i<colorMapLength; i++) {
        in.read(v);

        if (v.length == 4 || v.length == 3) {
          colorMap[0][i] = v[2];
          colorMap[1][i] = v[1];
          colorMap[2][i] = v[0];
        }
        else if (v.length == 2) {
          int pixel = DataTools.bytesToShort(v, isLittleEndian());
          colorMap[0][i] = (byte) ((pixel & 0x7c00) >> 10);
          colorMap[1][i] = (byte) ((pixel & 0x3e0) >> 5);
          colorMap[2][i] = (byte) (pixel & 0x1f);
        }
      }
    }

    offset = in.getFilePointer();

    // populate metadata hashtable

    addGlobalMeta("Color map present", hasColorMap);
    addGlobalMeta("Image type", imageType);
    addGlobalMeta("Color map origin", colorMapOrigin);
    addGlobalMeta("Color map length", colorMapLength);
    addGlobalMeta("Bits per color map entry", bitsPerEntry);
    addGlobalMeta("Image width", getSizeX());
    addGlobalMeta("Image height", getSizeY());
    addGlobalMeta("Bits per pixel", getBitsPerPixel());
    addGlobalMeta("Identification", identification);
    addGlobalMeta("Image orientation", orientation);
    addGlobalMeta("Pixel offset", offset);

    // populate remainder of core metadata

    m.sizeZ = 1;
    m.sizeT = 1;
    m.rgb = imageType == 2 || imageType == 10;
    m.sizeC = isRGB() ? 3 : 1;
    m.dimensionOrder = "XYCZT";
    m.falseColor = false;
    m.pixelType = FormatTools.UINT8;
    m.bitsPerPixel =
      getBitsPerPixel() == 32 ? 8 : getBitsPerPixel() / getSizeC();
    m.imageCount = 1;
    m.interleaved = true;
    m.indexed = colorMap != null && !isRGB();

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    // populate Image data

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      store.setImageDescription(identification, 0);
    }
  }

}
