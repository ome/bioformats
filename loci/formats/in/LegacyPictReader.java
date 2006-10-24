//
// LegacyPictReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan
and Eric Kjellman.

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
import java.io.*;
import loci.formats.*;

/**
 * LegacyPictReader is the old file format reader for Apple PICT files.
 * To use it, QuickTime for Java must be installed.
 */
public class LegacyPictReader extends FormatReader {

  // -- Static fields --

  /** Helper for reading PICT data with QTJava library. */
  private static LegacyQTTools qtTools = new LegacyQTTools();

  // -- Constructor --

  /** Constructs a new PICT reader. */
  public LegacyPictReader() { super("PICT", "pict"); }

  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for a PICT file. */
  public boolean isThisType(byte[] block) {
    return false;
  }

  /** Determines the number of images in the given PICT file. */
  public int getImageCount(String id) throws FormatException, IOException {
    return 1;
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    return true;
  }

  /** Return true if the data is in little-endian format. */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    return false;
  }

  /** Returns whether or not the channels are interleaved. */
  public boolean isInterleaved(String id) throws FormatException, IOException {
    return false;
  }

  /** Obtains the specified image from the given file as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    return ImageTools.getBytes(openImage(id, no), false, 3);
  }

  /** Obtains the specified image from the given PICT file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    // read in PICT data
    File file = new File(getMappedId(id));
    int len = (int) (file.length() - 512);
    byte[] bytes = new byte[len];
    FileInputStream fin = new FileInputStream(file);
    fin.skip(512);  // skip 512 byte PICT header
    int read = 0;
    int left = len;
    while (left > 0) {
      int r = fin.read(bytes, read, left);
      read += r;
      left -= r;
    }
    fin.close();
    return ImageTools.makeBuffered(qtTools.pictToImage(bytes));
  }

  /** Initializes the given PICT file. */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    sizeX[0] = openImage(id, 0).getWidth();
    sizeY[0] = openImage(id, 0).getHeight();
    sizeZ[0] = 1;
    sizeC[0] = 3;
    sizeT[0] = 1;
    pixelType[0] = FormatReader.INT8;
    currentOrder[0] = "XYCZT";
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException { }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new LegacyPictReader().testRead(args);
  }

}
