//
// GIFReader.java
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

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Vector;
import loci.formats.*;

/**
 * GIFReader is the file format reader for Graphics Interchange Format
 * (GIF) files.  Much of this code was adapted from the Animated GIF Reader
 * plugin for ImageJ (http://rsb.info.nih.gov/ij).
 */
public class GIFReader extends FormatReader {

  // -- Constants --

  /** Status codes. */
  private static final int STATUS_OK = 0;
  private static final int STATUS_FORMAT_ERROR = 1;
  private static final int STATUS_OPEN_ERROR = 2;

  /** Maximum buffer size. */
  private static final int MAX_STACK_SIZE = 4096;

  // -- Fields --

  private int status;

  /** Global color table used. */
  private boolean gctFlag;

  /** Size of global color table. */
  private int gctSize;

  /** Iterations; 0 = repeat forever. */
  private int loopCount;

  /** Global color table. */
  private int[] gct;

  /** Local color table. */
  private int[] lct;

  /** Active color table. */
  private int[] act;

  /** Background color index. */
  private int bgIndex;

  /** Background color. */
  private int bgColor;

  /** Previous bg color. */
  private int lastBgColor;

  /** Pixel aspect ratio. */
  private int pixelAspect;

  /** Local color table flag. */
  private boolean lctFlag;

  /** Interlace flag. */
  private boolean interlace;

  /** Local color table size. */
  private int lctSize;

  /** Current image rectangle. */
  private int ix, iy, iw, ih;

  /** Last image rectangle. */
  private Rectangle lastRect;

  /** Current data block. */
  private byte[] dBlock = new byte[256];

  /** Block size. */
  private int blockSize = 0;

  private int dispose = 0;
  private int lastDispose = 0;

  /** Use transparent color. */
  private boolean transparency = false;

  /** Delay in ms. */
  private int delay = 0;

  /** Transparent color index. */
  private int transIndex;

  // LZW working arrays
  private short[] prefix;
  private byte[] suffix;
  private byte[] pixelStack;
  private byte[] pixels;

  private Vector images;

  // -- Constructor --

  /** Constructs a new GIF reader. */
  public GIFReader() {
    super("Graphics Interchange Format (GIF)", "gif");
  }

  // -- FormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */ 
  public boolean isThisType(byte[] block) { return false; }

  /* @see loci.formats.IFormatReader#openBytes(int) */ 
  public byte[] openBytes(int no) throws FormatException, IOException {
    byte[] buf = new byte[core.sizeX[0] * core.sizeY[0] * core.sizeC[0]];
    return openBytes(no, buf);
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    if (no < 0 || no >= getImageCount()) {
      throw new FormatException("Invalid image number: " + no);
    }
    if (buf.length < core.sizeX[0] * core.sizeY[0] * core.sizeC[0]) {
      throw new FormatException("Buffer too small.");
    }

    int[] ints = (int[]) images.get(no);

    for (int i=0; i<ints.length; i++) {
      buf[i] = (byte) ((ints[i] & 0xff0000) >> 16);
      buf[i + ints.length] = (byte) ((ints[i] & 0xff00) >> 8);
      buf[i + 2*ints.length] = (byte) (ints[i] & 0xff);
    }
    return buf;
  }

  /* @see loci.formats.IFormatReader#openImage(int) */ 
  public BufferedImage openImage(int no)
    throws FormatException, IOException
  {
    byte[] bytes = openBytes(no);
    return ImageTools.makeImage(bytes, core.sizeX[0], core.sizeY[0],
      bytes.length / (core.sizeX[0] * core.sizeY[0]), false, 1, true);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws FormatException, IOException {
    if (fileOnly && in != null) in.close();
    else if (!fileOnly) close();
  }

  /** Initializes the given GIF file. */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("GIFReader.initFile(" + id + ")");
    super.initFile(id);

    status("Verifying GIF format");

    status = STATUS_OK;
    in = new RandomAccessStream(id);
    images = new Vector();

    byte[] buf = new byte[6];
    in.read(buf);
    String ident = new String(buf);

    if (!ident.startsWith("GIF")) {
      throw new FormatException("Not a valid GIF file.");
    }

    status("Reading dimensions");

    core.sizeX[0] = DataTools.read2UnsignedBytes(in, true);
    core.sizeY[0] = DataTools.read2UnsignedBytes(in, true);

    int packed = DataTools.readUnsignedByte(in);
    gctFlag = (packed & 0x80) != 0;
    gctSize = 2 << (packed & 7);
    bgIndex = DataTools.readUnsignedByte(in);
    pixelAspect = DataTools.readUnsignedByte(in);

    if (gctFlag) {
      int nbytes = 3 * gctSize;
      byte[] c = new byte[nbytes];
      int n = in.read(c);

      gct = new int[256];
      int i = 0;
      int j = 0;
      int r, g, b; 
      while (i < gctSize) {
        r = ((int) c[j++]) & 0xff;
        g = ((int) c[j++]) & 0xff;
        b = ((int) c[j++]) & 0xff;
        gct[i++] = 0xff000000 | (r << 16) | (g << 8) | b;
      }
    }

    bgColor = gct[bgIndex];

    status("Reading data blocks");

    boolean done = false;
    while (!done) {
      int code = DataTools.readUnsignedByte(in);
      switch (code) {
        case 0x2c: // image separator:
          ix = DataTools.read2UnsignedBytes(in, true);
          iy = DataTools.read2UnsignedBytes(in, true);
          iw = DataTools.read2UnsignedBytes(in, true);
          ih = DataTools.read2UnsignedBytes(in, true);

          packed = DataTools.readUnsignedByte(in);
          lctFlag = (packed & 0x80) != 0;
          interlace = (packed & 0x40) != 0;
          lctSize = 2 << (packed & 7);

          if (lctFlag) {
            int nbytes = 3 * lctSize;
            byte[] c = new byte[nbytes];
            int n = 0;
            try { n = in.read(c); }
            catch (IOException e) { }

            if (n < nbytes) {
              throw new FormatException("Local color table not found");
            }

            lct = new int[256];
            int i = 0;
            int j = 0;
            while (i < lctSize) {
              int r = ((int) c[j++]) & 0xff;
              int g = ((int) c[j++]) & 0xff;
              int b = ((int) c[j++]) & 0xff;
              lct[i++] = 0xff000000 | (r << 16) + (g << 8) | b;
            }

            act = lct;
          }
          else {
            act = gct;
            if (bgIndex == transIndex) bgColor = 0;
          }

          int save = 0;

          if (transparency) {
            save = act[transIndex];
            act[transIndex] = 0;
          }

          if (act == null) throw new FormatException("Color table not found.");

          decodeImageData();

          int check = 0;
          do { check = readBlock(); }
          while (blockSize > 0 && check != -1);

          core.imageCount[0]++;

          if (transparency) act[transIndex] = save;

          lastDispose = dispose;
          lastRect = new Rectangle(ix, iy, iw, ih);
          lastBgColor = bgColor;
          lct = null;

          break;
        case 0x21: // extension
          code = DataTools.readUnsignedByte(in);
          switch (code) {
            case 0xf9: // graphics control extension
              in.read();
              packed = DataTools.readUnsignedByte(in);
              dispose = (packed & 0x1c) >> 1;
              transparency = (packed & 1) != 0;
              delay = DataTools.read2UnsignedBytes(in, true) * 10;
              transIndex = DataTools.readUnsignedByte(in);
              in.read();
              break;
            case 0xff:  // application extension
              if (readBlock() == -1) {
                done = true;
                break;
              }

              String app = new String(dBlock, 0, 11);
              if (app.equals("NETSCAPE2.0")) {
                do {
                  check = readBlock();
                  if (dBlock[0] == 0x03) {
                    int b1 = ((int) dBlock[1]) & 0xff;
                    int b2 = ((int) dBlock[2]) & 0xff;
                    loopCount = (b2 << 8) | b1;
                  }
                }
                while (blockSize > 0 && check != -1);
              }
              else {
                do {
                  check = readBlock();
                }
                while (blockSize > 0 && check != -1);
              }
              break;
            default:
              do {
                check = readBlock();
              }
              while (blockSize > 0 && check != -1);
          }
          break;
        case 0x3b: // terminator
          done = true;
          break;
      }
    }

    status("Populating metadata");

    core.sizeZ[0] = 1;
    core.sizeC[0] = 3;
    core.sizeT[0] = core.imageCount[0];
    core.currentOrder[0] = "XYCTZ";
    core.rgb[0] = true;
    core.littleEndian[0] = true;
    core.interleaved[0] = true;

    // populate metadata store

    MetadataStore store = getMetadataStore();

    core.pixelType[0] = FormatTools.UINT8;
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

  // -- Helper methods --

  /** Reads the next variable length block. */
  private int readBlock() throws IOException {
    if (in.getFilePointer() == in.length()) return -1;
    blockSize = DataTools.readUnsignedByte(in);
    int n = 0;
    int count;

    if (blockSize > 0) {
      try {
        while (n < blockSize) {
          count = in.read(dBlock, n, blockSize - n);
          if (count == -1) break;
          n += count;
        }
      }
      catch (IOException e) { }
    }
    return n;
  }

  /** Decodes LZW image data into a pixel array.  Adapted from ImageMagick. */
  private void decodeImageData() throws IOException {
    int nullCode = -1;
    int npix = iw * ih;

    int available, clear, codeMask, codeSize, eoi, inCode, oldCode, bits, code,
      count, i, datum, dataSize, first, top, bi, pi;

    if (pixels == null || pixels.length < npix) pixels = new byte[npix];

    if (prefix == null) prefix = new short[MAX_STACK_SIZE];
    if (suffix == null) suffix = new byte[MAX_STACK_SIZE];
    if (pixelStack == null) pixelStack = new byte[MAX_STACK_SIZE + 1];

    // initialize GIF data stream decoder

    dataSize = DataTools.readUnsignedByte(in);

    clear = 1 << dataSize;
    eoi = clear + 1;
    available = clear + 2;
    oldCode = nullCode;
    codeSize = dataSize + 1;
    codeMask = (1 << codeSize) - 1;
    for (code=0; code < clear; code++) {
      prefix[code] = 0;
      suffix[code] = (byte) code;
    }

    // decode GIF pixel stream

    datum = bits = count = first = top = pi = bi = 0;

    for (i=0; i<npix;) {
      if (top == 0) {
        if (bits < codeSize) {
          if (count == 0) {
            count = readBlock();
            if (count <= 0) break;
            bi = 0;
          }
          datum += (((int) dBlock[bi]) & 0xff) << bits;
          bits += 8;
          bi++;
          count--;
          continue;
        }

        // get the next code
        code = datum & codeMask;
        datum >>= codeSize;
        bits -= codeSize;

        // interpret the code

        if ((code > available) || (code == eoi)) {
          break;
        }
        if (code == clear) {
          // reset the decoder
          codeSize = dataSize + 1;
          codeMask = (1 << codeSize) - 1;
          available = clear + 2;
          oldCode = nullCode;
          continue;
        }

        if (oldCode == nullCode) {
          pixelStack[top++] = suffix[code];
          oldCode = code;
          first = code;
          continue;
        }

        inCode = code;
        if (code == available) {
          pixelStack[top++] = (byte) first;
          code = oldCode;
        }

        while (code > clear) {
          pixelStack[top++] = suffix[code];
          code = prefix[code];
        }
        first = ((int) suffix[code]) & 0xff;

        if (available >= MAX_STACK_SIZE) break;
        pixelStack[top++] = (byte) first;
        prefix[available] = (short) oldCode;
        suffix[available] = (byte) first;
        available++;

        if (((available & codeMask) == 0) && (available < MAX_STACK_SIZE)) {
          codeSize++;
          codeMask += available;
        }
        oldCode = inCode;
      }
      top--;
      pixels[pi++] = pixelStack[top];
      i++;
    }

    for (i=pi; i<npix; i++) pixels[i] = 0;
    setPixels();
  }

  private void setPixels() {
    // expose destination image's pixels as an int array
    int[] dest = new int[core.sizeX[0] * core.sizeY[0]];
    int lastImage = -1;

    // fill in starting image contents based on last image's dispose code
    if (lastDispose > 0) {
      if (lastDispose == 3) { // use image before last
        int n = core.imageCount[0] - 2;
        if (n > 0) lastImage = n - 1;
      }

      if (lastImage != -1) {
        int[] prev = (int[]) images.get(lastImage);
        System.arraycopy(prev, 0, dest, 0, core.sizeX[0] * core.sizeY[0]);
      }
    }

    // copy each source line to the appropriate place in the destination

    int pass = 1;
    int inc = 8;
    int iline = 0;
    for (int i=0; i<ih; i++) {
      int line = i;
      if (interlace) {
        if (iline >= ih) {
          pass++;
          switch (pass) {
            case 2:
              iline = 4;
              break;
            case 3:
              iline = 2;
              inc = 4;
              break;
            case 4:
              iline = 1;
              inc = 2;
              break;
          }
        }
        line = iline;
        iline += inc;
      }
      line += iy;
      if (line < core.sizeY[0]) {
        int k = line * core.sizeX[0];
        int dx = k + ix; // start of line in dest
        int dlim = dx + iw; // end of dest line
        if ((k + core.sizeX[0]) < dlim) dlim = k + core.sizeX[0];
        int sx = i * iw; // start of line in source
        while (dx < dlim) {
          // map color and insert in destination
          int index = ((int) pixels[sx++]) & 0xff;
          dest[dx++] = act[index];
        }
      }
    }
    images.add(dest);
  }

}
