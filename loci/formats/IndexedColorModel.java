//
// IndexedColorModel.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats;

import java.awt.image.*;
import java.io.IOException;

/**
 * TODO - IndexedColorModel javadoc.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/IndexedColorModel.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/IndexedColorModel.java">SVN</a></dd></dl>
 */
public class IndexedColorModel extends ColorModel {

  // -- Fields --

  /** Lookup tables. */
  private byte[] redByte, greenByte, blueByte, alphaByte;
  private short[] redShort, greenShort, blueShort, alphaShort;
  private int[] redInt, greenInt, blueInt, alphaInt;

  /** Length of lookup table. */
  private int tableSize;

  private int pixelBits;

  // -- Constructors --

  public IndexedColorModel(int bits, int size, byte[][] table)
    throws IOException
  {
    super(bits);

    if (table == null) throw new IOException("LUT cannot be null");
    for (int i=0; i<table.length; i++) {
      if (table[i].length < size) {
        throw new IOException("LUT " + i + " too small");
      }
    }

    if (table.length > 0) redByte = table[0];
    if (table.length > 1) greenByte = table[1];
    if (table.length > 2) blueByte = table[2];
    if (table.length > 3) alphaByte = table[3];
    tableSize = size;
    pixelBits = bits;
  }

  public IndexedColorModel(int bits, int size, short[][] table)
    throws IOException
  {
    super(bits);

    if (table == null) throw new IOException("LUT cannot be null");
    for (int i=0; i<table.length; i++) {
      if (table[i].length < size) {
        throw new IOException("LUT " + i + " too small");
      }
    }

    if (table.length > 0) redShort = table[0];
    if (table.length > 1) greenShort = table[1];
    if (table.length > 2) blueShort = table[2];
    if (table.length > 3) alphaShort = table[3];
    tableSize = size;
    pixelBits = bits;
  }

  public IndexedColorModel(int bits, int size, int[][] table)
    throws IOException
  {
    super(bits);

    if (table == null) throw new IOException("LUT cannot be null");
    for (int i=0; i<table.length; i++) {
      if (table[i].length < size) {
        throw new IOException("LUT " + i + " too small");
      }
    }

    if (table.length > 0) redInt = table[0];
    if (table.length > 1) greenInt = table[1];
    if (table.length > 2) blueInt = table[2];
    if (table.length > 3) alphaInt = table[3];
    tableSize = size;
    pixelBits = bits;
  }

  // -- ColorModel API methods --

  /* @see java.awt.image.ColorModel#getDataElements(int, Object) */
  public synchronized Object getDataElements(int rgb, Object pixel) {
    int red = (rgb >> 16) & 0xff;
    int green = (rgb >> 8) & 0xff;
    int blue = rgb & 0xff;
    int alpha = (rgb >>> 24);

    if (redByte != null) {
      byte[] p = pixel == null ? new byte[3] : (byte[]) pixel;
      p[0] = (byte) red;
      p[1] = (byte) green;
      p[2] = (byte) blue;

      return p;
    }
    if (redShort != null) {
      short[] p = pixel == null ? new short[3] : (short[]) pixel;
      p[0] = (short) red;
      p[1] = (short) green;
      p[2] = (short) blue;
      return p;
    }
    if (redInt != null) {
      int[] p = pixel == null ? new int[3] : (int[]) pixel;
      p[0] = red;
      p[1] = green;
      p[2] = blue;
      return p;
    }
    throw new UnsupportedOperationException("Invalid transfer type");
  }

  /* @see java.awt.image.ColorModel#isCompatibleRaster(Raster) */
  public boolean isCompatibleRaster(Raster raster) {
    return raster.getNumBands() == 1;
  }

  /* @see java.awt.image.ColorModel#createCompatibleWritableRaster(int, int) */
  public WritableRaster createCompatibleWritableRaster(int w, int h) {
    WritableRaster raster;

    if (pixelBits == 1 || pixelBits == 2 || pixelBits == 4) {
      raster = Raster.createPackedRaster(DataBuffer.TYPE_BYTE, w, h, 1,
        pixelBits, null);
    }
    else if (pixelBits <= 8) {
      raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, w, h, 1,
        null);
    }
    else if (pixelBits <= 16) {
      raster = Raster.createInterleavedRaster(DataBuffer.TYPE_USHORT, w, h, 1,
        null);
    }
    else {
      throw new UnsupportedOperationException("Pixel bits > 16 not supported");
    }
    return raster;
  }

  /* @see java.awt.image.ColorModel#getAlpha(int) */
  public int getAlpha(int pixel) {
    if (alphaByte != null) return alphaByte[pixel] & 0xff;
    if (alphaShort != null) return alphaShort[pixel] & 0xffff;
    if (alphaInt != null) return alphaInt[pixel];
    return 255;
  }

  /* @see java.awt.image.ColorModel#getBlue(int) */
  public int getBlue(int pixel) {
    if (blueByte != null) return blueByte[pixel] & 0xff;
    if (blueShort != null) return blueShort[pixel] & 0xffff;
    if (blueInt != null) return blueInt[pixel];
    return 0;
  }

  /* @see java.awt.image.ColorModel#getGreen(int) */
  public int getGreen(int pixel) {
    if (greenByte != null) return greenByte[pixel] & 0xff;
    if (greenShort != null) return greenShort[pixel] & 0xffff;
    if (greenInt != null) return greenInt[pixel];
    return 0;
  }

  /* @see java.awt.image.ColorModel#getRed(int) */
  public int getRed(int pixel) {
    if (redByte != null) return redByte[pixel] & 0xff;
    if (redShort != null) return redShort[pixel] & 0xffff;
    if (redInt != null) return redInt[pixel];
    return 0;
  }

}
