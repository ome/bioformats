//
// SlidebookReader.java
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
 * SlidebookReader is the file format reader for 3I Slidebook files.
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class SlidebookReader extends FormatReader {

  // -- Constants --

  // -- Fields --

  /** Offset to pixel data. */
  private long offset = 1792;

  /** Number of bytes per pixel. */
  private int bpp;

  // -- Constructor --

  /** Constructs a new Slidebook reader. */
  public SlidebookReader() { super("Intelligent Imaging Slidebook", "sld"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    if (block.length < 8) return false;
    return block[0] == 0x6c && block[1] == 0 && block[2] == 0 &&
      block[3] == 1 && block[4] == 0x49 && block[5] == 0x49 && block[6] == 0;
  }

  /* @see loci.formats.IFormatReader#openBytes(int) */
  public byte[] openBytes(int no) throws FormatException, IOException {
    byte[] buf = new byte[core.sizeX[0] * core.sizeY[0] * 2];
    return openBytes(no, buf);
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    if (no < 0 || no >= getImageCount()) {
      throw new FormatException("Invalid image number: " + no);
    }
    if (buf.length < core.sizeX[0] * core.sizeY[0] * 2) {
      throw new FormatException("Buffer too small.");
    }
    in.seek(offset + (no * core.sizeX[0] * core.sizeY[0] * 2));
    in.read(buf);
    return buf;
  }

  /* @see loci.formats.IFormatReader#openImage(int) */
  public BufferedImage openImage(int no) throws FormatException, IOException {
    return ImageTools.makeImage(openBytes(no), core.sizeX[0],
      core.sizeY[0], 1, true, bpp, true);
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("SlidebookReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    status("Determining series count");

    in.skipBytes(4);
    core.littleEndian[0] = in.read() == 0x49;

    bpp = 2; // this is a major assumption

    // check if there are multiple "series" - note that each series has the
    // same dimensions, so we can display each plane as part of the same series

    in.seek(160);
    boolean multiSeries = DataTools.read4UnsignedBytes(in, true) > 1;

    // pixel data always begins at 0x6b0

    in.seek(1792);

    // determine the number of images

    status("Determining image count");

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

    in.seek(1792 + count - 20);

    int check = in.read();
    long lastH = 0;
    while (check == 'h') {
      lastH = in.getFilePointer();
      in.skipBytes(255);
      core.sizeC[0]++;
      check = in.read();
    }

    // scan the remaining bytes for the "CTimelapseAnnotation" tag

    in.seek(1792 + count);
    buf = new byte[8192];
    n = in.read(buf);
    while (n > 0) {
      String t = new String(buf);
      t.trim();
      while (t.indexOf("CTimelapseAnnotation") != -1) {
        t = t.substring(t.indexOf("CTimelapseAnnotation") + 20);
        core.sizeT[0]++;
      }
      byte[] tmp = buf;
      buf = new byte[8192];
      System.arraycopy(tmp, tmp.length - 20, buf, 0, 20);
      n = in.read(buf, 20, buf.length - 20);
    }

    // look for the first "i...II" block - this will have the width and height

    status("Populating metadata");

    in.seek(lastH);
    in.skipBytes(335);

    core.sizeX[0] = DataTools.read2UnsignedBytes(in, true);
    core.sizeY[0] = DataTools.read2UnsignedBytes(in, true);

    if (multiSeries) {
      core.sizeX[0] /= core.sizeC[0];
      core.sizeY[0] /= core.sizeC[0];
    }

    core.imageCount[0] = count / (core.sizeX[0] * core.sizeY[0] * bpp);

    float planes = (float) count / (float) (core.sizeX[0]*core.sizeY[0] * bpp);
    core.imageCount[0] = (int) planes;

    core.sizeZ[0] = core.imageCount[0] / (core.sizeC[0] * core.sizeT[0]);

    core.pixelType[0] = FormatTools.UINT16;
    core.currentOrder[0] = "XY";
    core.rgb[0] = false;
    core.interleaved[0] = false;

    if (core.imageCount[0] != (core.sizeZ[0] * core.sizeC[0] * core.sizeT[0])) {
      core.sizeZ[0] = 1;
      core.sizeT[0] = core.imageCount[0] / core.sizeC[0];
    }

    int[] dims = {core.sizeZ[0], core.sizeC[0], core.sizeT[0]};
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

    core.currentOrder[0] += names[maxNdx];
    core.currentOrder[0] += names[medNdx];
    core.currentOrder[0] += names[minNdx];

    if (core.sizeZ[0] == 0) core.sizeZ[0] = 1;
    if (core.sizeC[0] == 0) core.sizeC[0] = 1;
    if (core.sizeT[0] == 0) core.sizeT[0] = 1;

    MetadataStore store = getMetadataStore();
    store.setPixels(new Integer(core.sizeX[0]), new Integer(core.sizeY[0]),
      new Integer(core.sizeZ[0]), new Integer(core.sizeC[0]),
      new Integer(core.sizeT[0]), new Integer(core.pixelType[0]),
      new Boolean(!core.littleEndian[0]), core.currentOrder[0], null, null);
    for (int i=0; i<core.sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null);
    }
  }

}
