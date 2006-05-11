//
// SDTReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * SDTReader is the file format reader for
 * Becker &amp; Hickl SPC-Image SDT files.
 *
 * This importer is based on MATLAB code originally by Long Yan.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class SDTReader extends FormatReader {

  // -- Fields --

  /** Current file. */
  protected RandomAccessFile in;

  /** Number of images in current SDT file. */
  protected int numImages;

  /** Offset to binary data. */
  protected int offset;

  /** Dimensions of the current SDT file's images. */
  protected int width = 128, height = 128;


  // -- Constructor --

  /** Constructs a new SDT reader. */
  public SDTReader() { super("SPC-Image SDT", "sdt"); }


  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for an SDT file. */
  public boolean isThisType(byte[] block) { return false; }

  /** Determines the number of images in the given SDT file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return numImages;
  }

  /** Obtains the specified image from the given SDT file as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    throw new FormatException("SDTReader.openBytes(String, int) " +
      "not implemented");
  }

  /** Obtains the specified image from the given SDT file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    //in.seek(offset);
    short[] data = new short[width * height];

    return ImageTools.makeImage(data, width, height);
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  /** Initializes the given SDT file. */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessFile(id, "r");

    // skip 14 byte header
    in.seek(14);

    // read offset
    offset = in.readInt() + 22;
    System.out.println("offset = " + offset);//TEMP

    // compute number of image planes
    numImages = (int) ((in.length() - offset) / (2 * 64 * width * height));
  }


  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new SDTReader().testRead(args);
  }

}
