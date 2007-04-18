//
// EPSReader.java
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
import java.util.StringTokenizer;
import loci.formats.*;

/**
 * Reader is the file format reader for Encapsulated PostScript (EPS) files.
 * Some regular PostScript files are also supported.
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class EPSReader extends FormatReader {

  // -- Fields --

  /** Bits per sample. */
  private int bps;

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

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return false;
  }

  /* @see loci.formats.IFormatRaeder#openBytes(int) */
  public byte[] openBytes(int no) throws FormatException, IOException {
    byte[] buf =
      new byte[core.sizeX[0] * core.sizeY[0] * core.sizeC[0] * (bps / 8)];
    return openBytes(no, buf);
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    if (no < 0 || no >= getImageCount()) {
      throw new FormatException("Invalid image number: " + no);
    }
    if (buf.length < core.sizeX[0]*core.sizeY[0] * core.sizeC[0] * (bps / 8)) {
      throw new FormatException("Buffer too small.");
    }

    RandomAccessStream ras = new RandomAccessStream(currentId);
    int line = 0;

    while (line <= start) {
      ras.readLine();
      line++;
    }

    if (binary) {
      ras.read(buf, 0, buf.length);
    }
    else {
      long pos = ras.getFilePointer();
      String len = ras.readLine();
      ras.seek(pos);
      int numLines = buf.length / len.trim().length();

      char[] chars = new char[2];

      for (int i=0; i<buf.length; i++) {
        chars[0] = (char) ras.read();
        while (chars[0] == '\n') chars[0] = (char) ras.read();
        chars[1] = (char) ras.read();
        while (chars[1] == '\n') chars[1] = (char) ras.read();
        String s = new String(chars);
        buf[i] = (byte) Integer.parseInt(s, 16);
      }
    }
    ras.close();
    return buf;
  }

  /* @see loci.formats.IFormatReader#openImage(int) */
  public BufferedImage openImage(int no) throws FormatException, IOException {
    return ImageTools.makeImage(openBytes(no), core.sizeX[0], core.sizeY[0],
      isRGB() ? 3 : 1, true);
  }

  /** Initializes the given EPS file. */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("EPSReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    status("Verifying EPS format");

    String line = in.readLine();
    if (!line.trim().startsWith("%!PS")) {
      throw new FormatException("Invalid EPS file.");
    }

    status("Finding image data");

    binary = false;

    String image = "image";
    int lineNum = 1;

    line = in.readLine();

    while (line != null) {
      if (line.trim().equals(image) || line.trim().endsWith(image)) {
        if (line.trim().endsWith(image) && !line.trim().startsWith(image)) {
          if (line.indexOf("colorimage") != -1) core.sizeC[0] = 3;
          StringTokenizer t = new StringTokenizer(line, " ");
          try {
            core.sizeX[0] = Integer.parseInt(t.nextToken());
            core.sizeY[0] = Integer.parseInt(t.nextToken());
            bps = Integer.parseInt(t.nextToken());
          }
          catch (Exception exc) {
            // CTR TODO - eliminate catch-all exception handling
            if (debug) exc.printStackTrace();
            core.sizeC[0] = Integer.parseInt(t.nextToken());
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
          core.sizeX[0] = Integer.parseInt(t.nextToken()) - originX;
          core.sizeY[0] = Integer.parseInt(t.nextToken()) - originY;

          addMeta("X-coordinate of origin", new Integer(originX));
          addMeta("Y-coordinate of origin", new Integer(originY));
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
            addMeta(key, value);
          }
        }
      }
      else if (line.startsWith("%ImageData:")) {
        line = line.substring(11);
        StringTokenizer t = new StringTokenizer(line, " ");
        core.sizeX[0] = Integer.parseInt(t.nextToken());
        core.sizeY[0] = Integer.parseInt(t.nextToken());
        bps = Integer.parseInt(t.nextToken());
        core.sizeC[0] = Integer.parseInt(t.nextToken());
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

    status("Populating metadata");

    if (bps == 0) bps = 8;

    core.sizeZ[0] = 1;
    core.sizeT[0] = 1;
    core.currentOrder[0] = "XYCZT";
    core.pixelType[0] = FormatTools.UINT8;
    core.rgb[0] = core.sizeC[0] == 3;
    core.interleaved[0] = true;
    core.littleEndian[0] = true;
    core.imageCount[0] = 1;

    // Populate metadata store

    // The metadata store we're working with.
    MetadataStore store = getMetadataStore();

    store.setPixels(
      new Integer(core.sizeX[0]),
      new Integer(core.sizeY[0]),
      new Integer(core.sizeZ[0]),
      new Integer(core.sizeC[0]),
      new Integer(core.sizeT[0]),
      new Integer(core.pixelType[0]),
      Boolean.FALSE,
      core.currentOrder[0],
      null,
      null);
    for (int i=0; i<core.sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null);
    }
  }

}
