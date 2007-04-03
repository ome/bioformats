//
// BMPReader.java
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

package loci.formats.in;

import java.awt.image.BufferedImage;
import java.io.IOException;
import loci.formats.*;

/**
 * BMPReader is the file format reader for Microsoft Bitmap (BMP) files.
 * See http://astronomy.swin.edu.au/~pbourke/dataformats/bmp/ for a nice
 * description of the BMP file format.
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class BMPReader extends FormatReader {

  // -- Fields --

  /** Current file. */
  protected RandomAccessStream in;

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
   * 0 = no compression,
   * 1 = 8 bit run length encoding,
   * 2 = 4 bit run length encoding,
   * 3 = RGB bitmap with mask.
   */
  protected int compression;

  /** Offset to image data. */
  private int global;

  /** The pixel type. */
  private int pixType;

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

  /* @see loci.formats.IFormatReader#isRGB(String) */
  public boolean isRGB(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return bpp > 8;
  }

  /** Return true if the data is in little-endian format. */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return littleEndian;
  }

  /** Returns whether or not the channels are interleaved. */
  public boolean isInterleaved(String id, int subC)
    throws FormatException, IOException
  {
    return true;
  }

  public byte[] openBytes(String id, int no, byte[] buf)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    if (width % 2 == 1) width++;

    if (buf.length < width * height * (bpp / 8)) {
      throw new FormatException("Buffer too small.");
    }

    if (compression != 0) {
      throw new FormatException("Compression type " + compression +
        " not supported");
    }

    in.seek(global);
    int pixels = width * height;

    if (palette != null && palette[0].length > 0 && !ignoreColorTable) {
      for (int y=height-1; y>=0; y--) {
        for (int x=0; x<width; x++) {
          int val = in.read();
          if (val < 0) val += 127;
          buf[y*width + x] = palette[0][val];
          buf[y*width + x + pixels] = palette[1][val];
          buf[y*width + x + 2*pixels] = palette[2][val];
        }
      }
    }
    else {
      if (bpp <= 8) {
        for (int y=height-1; y>=0; y--) {
          for (int x=0; x<width; x++) {
            buf[y*width + x] = (byte) in.read();
          }
        }
      }
      else {
        for (int y=height-1; y>=0; y--) {
          for (int x=0; x<width; x++) {
            buf[y*width + x + 2*pixels] = (byte) in.read();
            buf[y*width + x + pixels] = (byte) in.read();
            buf[y*width + x] = (byte) in.read();
            for (int j=0; j<(bpp - 24) / 8; j++) in.read();
          }
        }
      }
    }
    return buf;
  }

  /** Obtains the specified image from the given BMP file as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (width % 2 == 1) width++;
    byte[] buf = new byte[width * height * (bpp / 8)];
    return openBytes(id, no, buf);
  }

  /** Obtains the specified image from the given BMP file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    BufferedImage b = ImageTools.makeImage(openBytes(id, no), width, height,
      !isRGB(id) ? 1 : 3, false);
    return b;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws FormatException, IOException {
    if (fileOnly && in != null) in.close();
    else if (!fileOnly) close();
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  /** Initializes the given BMP file. */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("BMPReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    status("Reading bitmap header");

    littleEndian = true;
    in.order(littleEndian);

    // read the first header - 14 bytes

    byte[] two = new byte[2];
    in.read(two);
    addMeta("Magic identifier", new String(two));

    addMeta("File size (in bytes)", "" + in.readInt());
    in.skipBytes(4); // reserved

    // read the offset to the image data
    offset = in.readInt();

    // read the second header - 40 bytes

    in.skipBytes(4);

    // get the dimensions

    width = in.readInt();
    height = in.readInt();

    if (width < 1 || height < 1) {
      throw new FormatException("Invalid image dimensions: " +
        width + " x " + height);
    }
    addMeta("Image width", "" + width);
    addMeta("Image height", "" + height);

    addMeta("Color planes", "" + in.readShort());
    bpp = in.readShort();
    addMeta("Bits per pixel", "" + bpp);

    compression = in.readInt();
    String comp = "invalid";

    switch (compression) {
      case 0:
        comp = "None";
        break;
      case 1:
        comp = "8 bit run length encoding";
        break;
      case 2:
        comp = "4 bit run length encoding";
        break;
      case 3:
        comp = "RGB bitmap with mask";
        break;
    }

    addMeta("Compression type", comp);

    in.skipBytes(4);
    addMeta("X resolution", "" + in.readInt());
    addMeta("Y resolution", "" + in.readInt());
    int nColors = in.readInt();
    in.skipBytes(4);

    // read the palette, if it exists

    if (offset != in.getFilePointer()) {
      palette = new byte[3][nColors];

      for (int i=0; i<nColors; i++) {
        for (int j=palette.length; j>0; j--) {
          palette[j][i] = (byte) in.read();
        }
        in.read();
      }
    }

    global = in.getFilePointer();
    addMeta("Indexed color", palette == null ? "false" : "true");

    status("Populating metadata");

    int c = (palette == null & bpp == 8) ? 1 : 3;
    int tbpp = bpp;
    if (bpp > 8) tbpp /= 3;
    while (tbpp % 8 != 0) tbpp++;

    switch (tbpp) {
      case 8:
        pixType = FormatTools.UINT8;
        break;
      case 16:
        pixType = FormatTools.UINT16;
        break;
      case 32:
        pixType = FormatTools.UINT32;
        break;
    }

    sizeX[0] = (width % 2 == 1) ? width + 1 : width;
    sizeY[0] = height;
    sizeZ[0] = 1;
    sizeC[0] = isRGB(id) ? 3 : 1;
    sizeT[0] = 1;
    pixelType[0] = pixType;
    currentOrder[0] = "XYCTZ";

    // Populate metadata store.

    // The metadata store we're working with.
    MetadataStore store = getMetadataStore(id);

    store.setPixels(
      new Integer(width),  // sizeX
      new Integer(height), // sizeY
      new Integer(1), // sizeZ
      new Integer(c), // sizeC
      new Integer(1), // sizeT
      new Integer(pixType),
      new Boolean(!littleEndian), // BigEndian
      "XYCTZ", // Dimension order
      null, // Use image index 0
      null); // Use pixels index 0

    // resolution is stored as pixels per meter; we want to convert to
    // microns per pixel

    int pixSizeX = Integer.parseInt((String) getMeta("X resolution"));
    int pixSizeY = Integer.parseInt((String) getMeta("Y resolution"));

    float correctedX = (1 / (float) pixSizeX) * 1000000;
    float correctedY = (1 / (float) pixSizeY) * 1000000;

    store.setDimensions(new Float(correctedX), new Float(correctedY), null,
      null, null, null);

    for (int i=0; i<sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null);
    }
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new BMPReader().testRead(args);
  }

}
