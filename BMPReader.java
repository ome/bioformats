//
// BMPReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

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

package loci.formats;

import java.awt.Image;
import java.io.*;

/**
 * BMPReader is the file format reader for Microsoft Bitmap (BMP) files.
 * See http://astronomy.swin.edu.au/~pbourke/dataformats/bmp/ for a nice
 * description of the BMP file format.
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class BMPReader extends FormatReader {

  // -- Fields --

  /** Current file. */
  protected RandomAccessFile in;

  /** Flag indicating whether current file is little endian. */
  protected boolean littleEndian;

  /** Offset to the image data. */
  protected int offset;

  /** Image width. */
  protected int width;

  /** Image height. */
  protected int height;

  /** Number of bits per pixel. */
  protected int bpp;

  /** The palette for indexed color images. */
  protected byte[][] palette;

  /**
   * Compression type:
   * 0 = no compression
   * 1 = 8 bit run length encoding
   * 2 = 4 bit run length encoding
   * 3 = RGB bitmap with mask
   */
  protected int compression;


  // -- Constructor --

  /** Constructs a new BMP reader. */
  public BMPReader() { super("Windows Bitmap", "bmp"); }


  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for a BMP file. */
  public boolean isThisType(byte[] block) {
    if (block.length != 14) {
      return false;
    }
    if (block[0] != 'B' || block[1] != 'M') return false;
    return true;
  }

  /** Determines the number of images in the given BMP file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return 1;
  }

  /** Obtains the specified image from the given BMP file. */
  public Image open(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    in.seek(offset);
    if (width % 2 == 1) width++;
    byte[] pix = new byte[width * height * (bpp / 8)];
    in.read(pix);

    byte[][] pixels = new byte[0][0];

    // check to make sure that the pixel data is uncompressed
    // we aren't going to support compressed data, since only crazy people
    // compress bitmaps :-)

    if (compression != 0) {
      throw new FormatException("Compression type " + compression +
        " not supported");
    }

    if (palette == null) palette = new byte[1][0];

    if (palette[0].length != 0) {
      pixels = new byte[3][pix.length];
      int pt = pix.length;
      for (int i=0; i<pix.length; i++) {
        for (int j=0; j<3; j++) {
          if (pix[i] < 0) pix[i] += 127;
          pixels[j][pt] = palette[j][pix[i]];
        }
        pt--;
      }
    }
    else {
      if (bpp <= 8) {
        pixels = new byte[3][pix.length];

        int pt = pix.length - 1;
        for (int i=0; i<pix.length; i++) {
          pixels[0][i] = pix[pt];
          pixels[1][i] = pix[pt];
          pixels[2][i] = pix[pt];
          pt--;
        }
      }
      else if (bpp == 24) {
        pixels = new byte[3][pix.length / 3];
        int pt = pix.length - 1;
        for (int j=0; j<pixels[0].length; j++) {
          for (int i=0; i < 3; i++) {
            pixels[i][j] = pix[pt];
            pt--;
          }
        }
      }
    }

    // need to reverse each row

    byte[][] tempPx = new byte[pixels.length][pixels[0].length];

    for (int i=0; i<pixels.length; i++) {
      for (int j=0; j < height; j++) {
        for (int k=0; k < width; k++) {
          tempPx[i][(width*j) + k] = pixels[i][(width*(j+1)) - 1 - k];
        }
      }
    }

    return ImageTools.makeImage(tempPx, width, height);
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  /** Initializes the given BMP file. */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessFile(id, "r");

    littleEndian = true;

    // read the first header - 14 bytes

    byte[] two = new byte[2];
    in.read(two);
    metadata.put("Magic identifier", new String(two));

    byte[] four = new byte[4];
    in.read(four);
    metadata.put("File size (in bytes)",
      "" + DataTools.bytesToInt(four, littleEndian));

    in.skipBytes(4);  // reserved

    // read the offset to the image data
    in.read(four);
    offset = DataTools.bytesToInt(four, littleEndian);

    // read the second header - 40 bytes

    in.skipBytes(4);

    // get the dimensions

    in.read(four);
    width = DataTools.bytesToInt(four, littleEndian);
    in.read(four);
    height = DataTools.bytesToInt(four, littleEndian);
    metadata.put("Image width", "" + width);
    metadata.put("Image height", "" + height);

    in.read(two);
    metadata.put("Color planes", "" + DataTools.bytesToInt(two, littleEndian));

    in.read(two);
    bpp = DataTools.bytesToInt(two, littleEndian);
    metadata.put("Bits per pixel", "" + bpp);

    in.read(four);
    compression = DataTools.bytesToInt(four, littleEndian);
    String comp = "invalid";

    switch (compression) {
      case 0: comp = "None"; break;
      case 1: comp = "8 bit run length encoding"; break;
      case 2: comp = "4 bit run length encoding"; break;
      case 3: comp = "RGB bitmap with mask"; break;
    }

    metadata.put("Compression type", comp);

    in.skipBytes(4);

    in.read(four);
    metadata.put("X resolution", "" +
      DataTools.bytesToInt(four, littleEndian));
    in.read(four);
    metadata.put("Y resolution", "" +
      DataTools.bytesToInt(four, littleEndian));

    in.read(four);
    int nColors = DataTools.bytesToInt(four, littleEndian);

    in.skipBytes(4);

    // read the palette, if it exists

    if (offset != (int) in.getFilePointer()) {
      palette = new byte[3][nColors];

      for (int i=0; i<nColors; i++) {
        for (int j=palette.length; j>0; j--) {
          palette[j][i] = (byte) in.read();
        }
        in.read();
      }
    }

    metadata.put("Indexed color", palette == null ? "false" : "true");
  }


  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new BMPReader().testRead(args);
  }

}
