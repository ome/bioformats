//
// MNGReader.java
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
import java.util.Vector;
import javax.imageio.ImageIO;
import loci.formats.*;

/**
 * MNGReader is the file format reader for Multiple Network Graphics (MNG)
 * files.  Does not support JNG (JPEG Network Graphics).
 */
public class MNGReader extends FormatReader {

  // -- Fields --

  /** Offsets to each plane. */
  private Vector offsets;

  /** Length (in bytes) of each plane. */
  private Vector lengths;

  // -- Constructor --

  /** Constructs a new MNG reader. */
  public MNGReader() { super("Multiple Network Graphics (MNG)", "mng"); }

  // -- FormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    if (block.length < 8) return false;
    return block[0] == 0x8a && block[1] == 0x4d && block[2] == 0x4e &&
      block[3] == 0x47 && block[4] == 0x0d && block[5] == 0x0a &&
      block[6] == 0x1a && block[7] == 0x0a;
  }

  /* @see loci.formats.IFormatReader#openBytes(int) */
  public byte[] openBytes(int no) throws FormatException, IOException {
    return ImageTools.getBytes(openImage(no), true, core.sizeC[0]);
  }

  /* @see loci.formats.IFormatReader#openImage(int) */
  public BufferedImage openImage(int no) throws FormatException, IOException {
    if (no < 0 || no >= getImageCount()) {
      throw new FormatException("Invalid image number: " + no);
    }

    int offset = ((Integer) offsets.get(no)).intValue();
    in.seek(offset);
    int end = ((Integer) lengths.get(no)).intValue();
    byte[] b = new byte[end - offset + 8];
    in.read(b, 8, b.length - 8);
    b[0] = (byte) 0x89;
    b[1] = 0x50;
    b[2] = 0x4e;
    b[3] = 0x47;
    b[4] = 0x0d;
    b[5] = 0x0a;
    b[6] = 0x1a;
    b[7] = 0x0a;

    return ImageIO.read(new ByteArrayInputStream(b));
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws FormatException, IOException {
    if (fileOnly && in != null) in.close();
    else if (!fileOnly) close();
  }

  /** Initializes the given MNG file. */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("MNGReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    status("Verifying MNG format");

    offsets = new Vector();
    lengths = new Vector();

    in.skipBytes(8);

    in.skipBytes(4);
    byte[] b = new byte[4];
    in.read(b);
    if (!"MHDR".equals(new String(b))) {
      throw new FormatException("Invalid MNG file.");
    }

    status("Reading dimensions");

    core.sizeX[0] = (int) DataTools.read4UnsignedBytes(in, false);
    core.sizeY[0] = (int) DataTools.read4UnsignedBytes(in, false);
    long fps = DataTools.read4UnsignedBytes(in, false);
    long layerCounter = DataTools.read4UnsignedBytes(in, false);
    in.skipBytes(4);

    long playTime = DataTools.read4UnsignedBytes(in, false);
    long profile = DataTools.read4UnsignedBytes(in, false);

    in.skipBytes(4); // skip the CRC

    Vector stack = new Vector();
    int maxIterations = 0;
    int currentIteration = 0;

    status("Finding image offsets");

    while (in.getFilePointer() < in.length()) {
      long len = DataTools.read4UnsignedBytes(in, false);
      in.read(b);
      String code = new String(b);

      int fp = in.getFilePointer();

      if (code.equals("IHDR")) {
        offsets.add(new Integer((int) in.getFilePointer() - 8));
        core.imageCount[0]++;
      }
      else if (code.equals("IEND")) {
        lengths.add(new Integer(fp + (int) len + 4));
      }
      else if (code.equals("LOOP")) {
        stack.add(new Integer((int) (in.getFilePointer() + len + 4)));
        in.skipBytes(1);
        maxIterations = DataTools.read4SignedBytes(in, false);
      }
      else if (code.equals("ENDL")) {
        int seek = ((Integer) stack.get(stack.size() - 1)).intValue();
        if (currentIteration < maxIterations) {
          in.seek(seek);
          currentIteration++;
        }
        else {
          stack.remove(stack.size() - 1);
          maxIterations = 0;
          currentIteration = 0;
        }
      }

      in.seek(fp + (int) len + 4);
    }

    status("Populating metadata");

    core.sizeZ[0] = 1;
    core.sizeC[0] = openImage(0).getRaster().getNumBands();
    core.sizeT[0] = core.imageCount[0];
    core.currentOrder[0] = "XYCZT";
    core.pixelType[0] = FormatTools.UINT8;
    core.rgb[0] = core.sizeC[0] > 1;
    core.interleaved[0] = false;
    core.littleEndian[0] = false;

    MetadataStore store = getMetadataStore();

    store.setPixels(new Integer(core.sizeX[0]), new Integer(core.sizeY[0]),
      new Integer(core.sizeZ[0]), new Integer(core.sizeC[0]),
      new Integer(core.sizeT[0]), new Integer(core.pixelType[0]),
      Boolean.TRUE, core.currentOrder[0], null, null);
    for (int i=0; i<core.sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null);
    }
  }

}
