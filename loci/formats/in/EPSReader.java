//
// EPSReader.java
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
import java.util.StringTokenizer;
import loci.formats.*;

/**
 * Reader is the file format reader for Encapsulated PostScript (EPS) files.
 * Some regular PostScript files are also supported.
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class EPSReader extends FormatReader {

  // -- Fields --

  /** Current file. */
  protected BufferedReader in;

  /** Image width. */
  private int width;

  /** Image height. */
  private int height;

  /** Bits per sample. */
  private int bps;

  /** Number of channels. */
  private int channels = 1;

  /** Starting line of pixel data. */
  private int start;

  /** Flag indicating binary data. */
  private boolean binary;

  // -- Constructor --

  /** Constructs a new EPS reader. */
  public EPSReader() {
    super("Encapsulated PostScript", new String[] {"eps", "epsi", "ps"});
  }

  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for an EPS file. */
  public boolean isThisType(byte[] block) {
    return false;
  }

  /** Determines the number of images in the given EPS file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return 1;
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return channels == 3;
  }

  /** Return true if the data is in little-endian format. */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    return true;
  }

  /** Returns whether or not the channels are interleaved. */
  public boolean isInterleaved(String id) throws FormatException, IOException {
    return true;
  }

  /**
   * Obtains the specified image from the given EPS file as a byte array.
   */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    close();

    byte[] p = new byte[width * height * channels * (bps / 8)];

    RandomAccessStream ras = new RandomAccessStream(getMappedId(id));
    int line = 0;

    while (line <= start) {
      ras.readLine();
      line++;
    }

    if (binary) {
      ras.read(p, 0, p.length);
    }
    else {
      int pos = ras.getFilePointer();
      String len = ras.readLine();
      ras.seek(pos);
      int numLines = p.length / len.trim().length();

      byte[] tmp = new byte[p.length * 2 + 3*numLines];
      ras.read(tmp, 0, tmp.length);
      String pixels = new String(tmp);
      pixels = pixels.replaceAll("\n", "");

      // every 2 characters in the pixels string represents 1 byte

      for (int i=0; i<p.length; i++) {
        if (i < p.length - 1) {
          String s = pixels.substring(i*2, (i+1)*2);
          p[i] = (byte) Integer.parseInt(s, 16);
        }
        else {
          String s = pixels.substring(i*2);
          if (s.length() > 2) s = s.substring(0, 2);
          p[i] = (byte) Integer.parseInt(s, 16);
        }
      }
    }

    return p;
  }

  /** Obtains the specified image from the given EPS file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    return ImageTools.makeImage(openBytes(id, no), width, height,
      !isRGB(id) ? 1 : 3, true);
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  /** Initializes the given EPS file. */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new BufferedReader(new FileReader(getMappedId(id)));
    String line = in.readLine();
    if (!line.trim().startsWith("%!PS")) {
      throw new FormatException("Invalid EPS file.");
    }

    binary = false;

    String image = "image";
    int lineNum = 1;

    line = in.readLine();

    while (line != null) {
      if (line.trim().equals(image) || line.trim().endsWith(image)) {
        if (line.trim().endsWith(image) && !line.trim().startsWith(image)) {
          if (line.indexOf("colorimage") != -1) channels = 3;
          StringTokenizer t = new StringTokenizer(line, " ");
          try {
            width = Integer.parseInt(t.nextToken());
            height = Integer.parseInt(t.nextToken());
            bps = Integer.parseInt(t.nextToken());
          }
          catch (Exception exc) {
            channels = Integer.parseInt(t.nextToken());
          }
        }

        start = lineNum;
        break;
      }
      else if (line.startsWith("%%")) {
        if (line.startsWith("%%BoundingBox:")) {
          line = line.substring(14);
          StringTokenizer t = new StringTokenizer(line, " ");
          int originX = Integer.parseInt(t.nextToken());
          int originY = Integer.parseInt(t.nextToken());
          width = Integer.parseInt(t.nextToken()) - originX;
          height = Integer.parseInt(t.nextToken()) - originY;

          metadata.put("X-coordinate of origin", new Integer(originX));
          metadata.put("Y-coordinate of origin", new Integer(originY));
        }
        else if (line.startsWith("%%BeginBinary")) {
          binary = true;
        }
        else {
          // parse key/value pairs

          int ndx = line.indexOf(":");
          if (ndx != -1) {
            String key = line.substring(0, ndx);
            String value = line.substring(ndx + 1);
            metadata.put(key, value);
          }
        }
      }
      else if (line.startsWith("%ImageData:")) {
        line = line.substring(11);
        StringTokenizer t = new StringTokenizer(line, " ");
        width = Integer.parseInt(t.nextToken());
        height = Integer.parseInt(t.nextToken());
        bps = Integer.parseInt(t.nextToken());
        channels = Integer.parseInt(t.nextToken());
        while (t.hasMoreTokens()) {
          image = t.nextToken().trim();
          if (image.length() > 1) {
            image = image.substring(1, image.length() - 1);
          }
        }
      }
      lineNum++;
      line = in.readLine();
    }

    if (bps == 0) bps = 8;

    sizeX[0] = width;
    sizeY[0] = height;
    sizeZ[0] = 1;
    sizeC[0] = isRGB(id) ? 3 : 1;
    sizeT[0] = 1;
    currentOrder[0] = "XYCZT";

    // Populate metadata store

    // The metadata store we're working with.
    MetadataStore store = getMetadataStore(id);

    pixelType[0] = FormatReader.INT8;
    store.setPixels(
      new Integer(width),
      new Integer(height),
      new Integer(1),
      new Integer(channels),
      new Integer(1),
      new Integer(pixelType[0]),
      new Boolean(false),
      "XYCZT",
      null);
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new EPSReader().testRead(args);
  }

}
