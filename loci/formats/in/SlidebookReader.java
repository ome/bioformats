//
// SlidebookReader.java
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
 * SlidebookReader is the file format reader for 3I Slidebook files.
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class SlidebookReader extends FormatReader {

  // -- Constants --


  // -- Fields --

  /** Current file. */
  protected RandomAccessStream in;

  /** Number of image planes in the file. */
  protected int numImages = 0;

  /** Offset to pixel data. */
  private int offset = 1792;

  /** Image dimensions. */
  private int width, height;

  /** Number of bytes per pixel. */
  private int bpp;

  /** Flag is true if the data is in little-endian order. */
  private boolean little;

  // -- Constructor --

  /** Constructs a new Slidebook reader. */
  public SlidebookReader() { super("Intelligent Imaging Slidebook", "sld"); }


  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for an Slidebook file. */
  public boolean isThisType(byte[] block) {
    if (block.length < 8) return false;
    return block[0] == 0x6c && block[1] == 0 && block[2] == 0 &&
      block[3] == 1 && block[4] == 0x49 && block[5] == 0x49 && block[6] == 0;
  }

  /** Determines the number of images in the given Slidebook file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return numImages;
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    return false;
  }

  /** Return true if the data is in little-endian format. */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return little;
  }

  /** Returns whether or not the channels are interleaved. */
  public boolean isInterleaved(String id) throws FormatException, IOException {
    return false;
  }

  /**
   * Obtains the specified image from the
   * given Slidebook file as a byte array.
   */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    in.seek(offset + (no * width * height * 2));
    byte[] b = new byte[width * height * 2];
    in.read(b);
    return b;
  }

  /** Obtains the specified image from the given Slidebook file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    return ImageTools.makeImage(openBytes(id, no), width, height, 1, true,
      bpp, true);
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  /** Initializes the given Slidebook file. */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessStream(id);

    in.skipBytes(4);
    little = in.read() == 0x49;

    bpp = 2; // this is a major assumption

    // pixel data always begins at 0x6b0

    in.seek(1792);

    // determine the number of images

    byte[] buf = new byte[8192];
    boolean found = false;

    int count = 0;
    int n = in.read(buf);

    while (!found && in.getFilePointer() < in.length()) {
      count += n;
      for (int i=0; i<n-3; i++) {
        found = buf[i] == 0x68 && buf[i+1] == 0x00 && buf[i+2] == 0x00 &&
          buf[i+3] == 0x01;
        if (found) {
          count -= (n - i);
          i = n;
        }
      }
      byte[] tmp = buf;
      buf = new byte[8192];
      System.arraycopy(tmp, tmp.length - 20, buf, 0, 20);
      n = in.read(buf, 20, buf.length - 20);
    }

    int numC = 0;
    in.seek(1792 + count - 20);

    int check = in.read();
    while (check == 'h') {
      in.skipBytes(255);
      numC++;
      check = in.read();
    }

    // scan the remaining bytes for the "CTimelapseAnnotation" tag

    in.seek(1792 + count);
    buf = new byte[8192];
    n = in.read(buf);
    int numT = 0;
    while (n > 0) {
      String t = new String(buf);
      t.trim();
      while (t.indexOf("CTimelapseAnnotation") != -1) {
        t = t.substring(t.indexOf("CTimelapseAnnotation") + 20);
        numT++;
      }
      byte[] tmp = buf;
      buf = new byte[8192];
      System.arraycopy(tmp, tmp.length - 20, buf, 0, 20);
      n = in.read(buf, 20, buf.length - 20);
    }

    // this looks right, but I'm not confident that it will work in general
    in.seek(0x106);
    width = DataTools.read2UnsignedBytes(in, true);
    in.seek(0x306);
    height = DataTools.read2UnsignedBytes(in, true);

    numImages = count / (width * height * bpp);

    sizeX[0] = width;
    sizeY[0] = height;
    sizeZ[0] = numImages / (numT * numC);
    sizeC[0] = numC;
    sizeT[0] = numT;
    pixelType[0] = FormatReader.INT16;
    currentOrder[0] = "XY";

    int[] dims = {sizeZ[0], sizeC[0], sizeT[0]};
    String[] names = {"Z", "C", "T"};
    int max = 0, min = Integer.MAX_VALUE;
    int maxNdx = 0, minNdx = 0, medNdx = 0;
    for (int i=0; i<dims.length; i++) {
      if (dims[i] > max) {
        max = dims[i];
        maxNdx = i;
      }
      else if (dims[i] < min) {
        min = dims[i];
        minNdx = i;
      }
    }

    for (int i=0; i<dims.length; i++) {
      if (maxNdx != i && minNdx != i) medNdx = i;
    }

    currentOrder[0] += names[maxNdx];
    currentOrder[0] += names[medNdx];
    currentOrder[0] += names[minNdx];
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new SlidebookReader().testRead(args);
  }

}
