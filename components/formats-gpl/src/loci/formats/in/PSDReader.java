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
import loci.formats.codec.CodecOptions;
import loci.formats.codec.PackbitsCodec;
import loci.formats.meta.MetadataStore;

/**
 * PSDReader is the file format reader for Photoshop PSD files.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class PSDReader extends FormatReader {

  // -- Constants --

  public static final String PSD_MAGIC_STRING = "8BPS";

  // -- Fields --

  /** Lookup table. */
  private byte[][] lut;

  /** Offset to pixel data. */
  private long offset;

  private int[][] lens;
  private boolean compressed = false;

  // -- Constructor --

  /** Constructs a new PSD reader. */
  public PSDReader() {
    super("Adobe Photoshop", "psd");
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
    suffixNecessary = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = PSD_MAGIC_STRING.length();
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readString(blockLen).startsWith(PSD_MAGIC_STRING);
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable(int) */
  @Override
  public byte[][] get8BitLookupTable(int no) {
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

    in.seek(offset);

    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int plane = getSizeX() * getSizeY() * bpp;

    if (compressed) {
      PackbitsCodec codec = new PackbitsCodec();
      CodecOptions options = new CodecOptions();
      options.maxBytes = getSizeX() * bpp;

      byte[] b = null;

      int index = 0;
      for (int c=0; c<getSizeC(); c++) {
        for (int row=0; row<getSizeY(); row++) {
          if (row < y || row >= y + h) {
            in.skipBytes(lens[c][row]);
          }
          else {
            b = new byte[lens[c][row]];
            in.read(b);
            b = codec.decompress(b, options);
            System.arraycopy(b, x * bpp, buf, index, w * bpp);
            index += w * bpp;
          }
        }
      }
    }
    else {
      readPlane(in, x, y, w, h, buf);
    }
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      lut = null;
      offset = 0;
      compressed = false;
      lens = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    CoreMetadata m = core.get(0);
    m.littleEndian = false;

    if (!in.readString(4).equals("8BPS")) {
      throw new FormatException("Not a valid Photoshop file.");
    }

    addGlobalMeta("Version", in.readShort());

    in.skipBytes(6); // reserved, set to 0
    m.sizeC = in.readShort();
    m.sizeY = in.readInt();
    m.sizeX = in.readInt();

    int bits = in.readShort();
    addGlobalMeta("Bits per pixel", bits);
    m.pixelType = FormatTools.pixelTypeFromBytes(bits / 8, false, false);

    int colorMode = in.readShort();
    String modeString = null;
    switch (colorMode) {
      case 0:
        modeString = "monochrome";
        break;
      case 1:
        modeString = "gray-scale";
        break;
      case 2:
        modeString = "palette color";
        break;
      case 3:
        modeString = "RGB";
        break;
      case 4:
        modeString = "CMYK";
        break;
      case 6:
        modeString = "Duotone";
        break;
      case 7:
        modeString = "Multichannel color";
        break;
      case 8:
        modeString = "Duotone";
        break;
      case 9:
        modeString = "LAB color";
        break;
    }
    addGlobalMeta("Color mode", modeString);

    // read color mode block, if present

    int modeDataLength = in.readInt();
    long fp = in.getFilePointer();
    if (modeDataLength != 0) {
      if (colorMode == 2) {
        lut = new byte[3][256];
        for (int i=0; i<lut.length; i++) {
          in.read(lut[i]);
        }
      }
      in.seek(fp + modeDataLength);
    }

    // read image resources block

    in.skipBytes(4);

    while (in.readString(4).equals("8BIM")) {
      int tag = in.readShort();
      int read = 1;
      while (in.read() != 0) read++;
      if (read % 2 == 1) in.skipBytes(1);

      int size = in.readInt();
      if (size % 2 == 1) size++;

      in.skipBytes(size);
    }

    in.seek(in.getFilePointer() - 4);

    int blockLen = in.readInt();

    if (blockLen == 0) {
      offset = in.getFilePointer();
    }
    else {
      int layerLen = in.readInt();
      int layerCount = in.readShort();

      if (layerCount < 0) {
        throw new FormatException("Vector data is not supported.");
      }

      if (layerLen == 0 && layerCount == 0) {
        in.skipBytes(2);
        int check = in.readShort();
        in.seek(in.getFilePointer() - (check == 0 ? 4 : 2));
      }

      int[] w = new int[layerCount];
      int[] h = new int[layerCount];
      int[] c = new int[layerCount];
      for (int i=0; i<layerCount; i++) {
        int top = in.readInt();
        int left = in.readInt();
        int bottom = in.readInt();
        int right = in.readInt();
        w[i] = right - left;
        h[i] = bottom - top;
        c[i] = in.readShort();
        in.skipBytes(c[i] * 6 + 12);
        int len = in.readInt();
        if (len % 2 == 1) len++;
        in.skipBytes(len);
      }

      // skip over pixel data for each layer
      for (int i=0; i<layerCount; i++) {
        if (h[i] < 0) {
          continue;
        }
        int[] lens = new int[h[i]];
        for (int cc=0; cc<c[i]; cc++) {
          boolean compressed = in.readShort() == 1;
          if (!compressed) in.skipBytes(w[i] * h[i]);
          else {
            for (int y=0; y<h[i]; y++) {
              lens[y] = in.readShort();
            }
            for (int y=0; y<h[i]; y++) {
              in.skipBytes(lens[y]);
            }
          }
        }
      }
      long start = in.getFilePointer();
      while (in.read() != '8');
      in.skipBytes(7);
      if (in.getFilePointer() - start > 1024) {
        in.seek(start);
      }
      int len = in.readInt();
      if ((len % 4) != 0) len += 4 - (len % 4);
      if (len > in.length() - in.getFilePointer() || (len & 0xff0000) >> 16 == 1) {
        in.seek(start);
        len = 0;
      }
      in.skipBytes(len);

      String s = in.readString(4);
      while (s.equals("8BIM")) {
        in.skipBytes(4);
        len = in.readInt();
        if ((len % 4) != 0) len += 4 - (len % 4);
        in.skipBytes(len);
        s = in.readString(4);
      }

      offset = in.getFilePointer() - 4;
    }

    m.sizeZ = 1;
    m.sizeT = 1;
    m.rgb = modeString.equals("RGB") || modeString.equals("CMYK");
    m.imageCount = getSizeC() / (isRGB() ? 3 : 1);
    m.indexed = modeString.equals("palette color");
    m.falseColor = false;
    m.dimensionOrder = "XYCZT";
    m.interleaved = false;
    m.metadataComplete = true;

    in.seek(offset);

    compressed = in.readShort() == 1;
    lens = new int[getSizeC()][getSizeY()];

    if (compressed) {
      for (int c=0; c<getSizeC(); c++) {
        for (int row=0; row<getSizeY(); row++) {
          lens[c][row] = in.readShort();
        }
      }
    }
    offset = in.getFilePointer();

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
  }

}
