//
// LegacyPictReader.java
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
    if (!id.equals(currentId)) initFile(id);
    return sizeC[0] > 1;
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
    byte[] b = ImageTools.getBytes(openImage(id, no), false, 3);
    updateMinMax(b, no);
    return b;
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
    RandomAccessStream fin = new RandomAccessStream(id);
    int len = (int) (fin.length() - 512);
    byte[] bytes = new byte[len];
    fin.skip(512);  // skip 512 byte PICT header
    int read = 0;
    int left = len;
    while (left > 0) {
      int r = fin.read(bytes, read, left);
      read += r;
      left -= r;
    }
    fin.close();
    BufferedImage b = ImageTools.makeBuffered(qtTools.pictToImage(bytes));
    updateMinMax(b, no);
    return b;
  }

  /** Initializes the given PICT file. */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("LegacyPictReader.initFile(" + id + ")");
    super.initFile(id);
    BufferedImage img = openImage(id, 0);
    sizeX[0] = img.getWidth();
    sizeY[0] = img.getHeight();
    sizeZ[0] = 1;
    sizeC[0] = img.getRaster().getNumBands();
    sizeT[0] = 1;
    pixelType[0] = FormatTools.INT8;
    currentOrder[0] = "XYCZT";

    MetadataStore store = getMetadataStore(id);
    store.setPixels(new Integer(sizeX[0]), new Integer(sizeY[0]),
      new Integer(1), new Integer(sizeC[0]), new Integer(1),
      new Integer(pixelType[0]), Boolean.TRUE, currentOrder[0], null, null);

    for (int i=0; i<sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null);
    }
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws FormatException, IOException { }

  /** Closes any open files. */
  public void close() throws FormatException, IOException { }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new LegacyPictReader().testRead(args);
  }

}
