//
// PictReader.java
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

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import loci.formats.*;
import loci.formats.codec.PackbitsCodec;

/**
 * PictReader is the file format reader for Apple PICT files.
 * Most of this code was adapted from the PICT readers in JIMI
 * (http://java.sun.com/products/jimi/index.html), ImageMagick
 * (http://www.imagemagick.org), and Java QuickDraw.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/PictReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/PictReader.java">SVN</a></dd></dl>
 */
public class PictReader extends FormatReader {

  // -- Constants --

  // opcodes that we need
  private static final int PICT_CLIP_RGN = 1;
  private static final int PICT_BITSRECT = 0x90;
  private static final int PICT_BITSRGN = 0x91;
  private static final int PICT_PACKBITSRECT = 0x98;
  private static final int PICT_PACKBITSRGN = 0x99;
  private static final int PICT_9A = 0x9a;
  private static final int PICT_END = 0xff;
  private static final int PICT_LONGCOMMENT = 0xa1;

  // possible image states
  private static final int INITIAL = 1;
  private static final int STATE2 = 2;

  // other stuff?
  private static final int INFOAVAIL = 2;
  private static final int IMAGEAVAIL = 4;

  /** Table used in expanding pixels that use less than 8 bits. */
  private static final byte[] EXPANSION_TABLE = new byte[256 * 8];

  static {
    int index = 0;
    for (int i=0; i<256; i++) {
      EXPANSION_TABLE[index++] = (i & 128) == 0 ? (byte) 0 : (byte) 1;
      EXPANSION_TABLE[index++] = (i & 64) == 0 ? (byte) 0 : (byte) 1;
      EXPANSION_TABLE[index++] = (i & 32) == 0 ? (byte) 0 : (byte) 1;
      EXPANSION_TABLE[index++] = (i & 16) == 0 ? (byte) 0 : (byte) 1;
      EXPANSION_TABLE[index++] = (i & 8) == 0 ? (byte) 0 : (byte) 1;
      EXPANSION_TABLE[index++] = (i & 4) == 0 ? (byte) 0 : (byte) 1;
      EXPANSION_TABLE[index++] = (i & 2) == 0 ? (byte) 0 : (byte) 1;
      EXPANSION_TABLE[index++] = (i & 1) == 0 ? (byte) 0 : (byte) 1;
    }
  }

  // -- Fields --

  /** Stream for reading pixel data. */
  protected RandomAccessStream ras;

  /** Pixel bytes. */
  protected byte[] bytes;

  /** Number of bytes in a row of pixel data (variable). */
  protected int rowBytes;

  /** Decoder state. */
  protected int state;

  /** Image state. */
  protected int pictState;

  /** Vector of byte arrays representing individual rows. */
  protected Vector strips;

  /** Whether or not the file is PICT v1. */
  protected boolean versionOne;

  /** Color lookup table for palette color images. */
  protected short[][] lookup;

  /** Helper reader in case this one fails. */
  protected LegacyQTTools qtTools = new LegacyQTTools();

  // -- Constructor --

  /** Constructs a new PICT reader. */
  public PictReader() { super("PICT", new String[] {"pict", "pct"}); }

  // -- PictReader API methods --

  /** Get the dimensions of a PICT file from the first 4 bytes after header. */
  public Dimension getDimensions(byte[] stuff) throws FormatException {
    if (stuff.length < 10) {
      throw new FormatException("Need 10 bytes to calculate dimension");
    }
    int w = DataTools.bytesToInt(stuff, 6, 2, core.littleEndian[0]);
    int h = DataTools.bytesToInt(stuff, 8, 2, core.littleEndian[0]);
    if (debug) debug("getDimensions: " + w + " x " + h);
    return new Dimension(h, w);
  }

  /** Open a PICT image from an array of bytes (used by OpenlabReader). */
  public BufferedImage open(byte[] pix) throws FormatException, IOException {
    // handles case when we call this method directly, instead of
    // through initFile(String)
    if (debug) debug("open");

    if (core == null) core = new CoreMetadata(1);

    strips = new Vector();

    state = 0;
    pictState = INITIAL;
    bytes = pix;
    ras = new RandomAccessStream(bytes);
    ras.order(false);

    try {
      while (driveDecoder()) { }
    }
    catch (FormatException exc) {
      trace(exc);
      return ImageTools.makeBuffered(qtTools.pictToImage(pix));
    }

    // combine everything in the strips Vector

    if ((core.sizeY[0]*4 < strips.size()) && (((strips.size() / 3) %
      core.sizeY[0]) != 0))
    {
      core.sizeY[0] = strips.size();
    }

    if (strips.size() == 0) {
      return ImageTools.makeBuffered(qtTools.pictToImage(pix));
    }

    if (lookup != null) {
      // 8 bit data
      short[][] data = new short[3][core.sizeY[0] * core.sizeX[0]];

      byte[] row;

      for (int i=0; i<core.sizeY[0]; i++) {
        row = (byte[]) strips.get(i);

        for (int j=0; j<row.length; j++) {
          if (j < core.sizeX[0]) {
            int ndx = row[j];
            if (ndx < 0) ndx += lookup[0].length;
            ndx = ndx % lookup[0].length;

            int outIndex = i*core.sizeX[0] + j;
            if (outIndex >= data[0].length) outIndex = data[0].length - 1;

            data[0][outIndex] = lookup[0][ndx];
            data[1][outIndex] = lookup[1][ndx];
            data[2][outIndex] = lookup[2][ndx];
          }
          else j = row.length;
        }
      }

      if (debug) {
        debug("openBytes: 8-bit data, " + core.sizeX[0] + " x " +
          core.sizeY[0] + ", length=" + data.length + "x" + data[0].length);
      }
      return ImageTools.makeImage(data, core.sizeX[0], core.sizeY[0]);
    }
    else if (core.sizeY[0]*3 == strips.size()) {
      // 24 bit data
      byte[][] data = new byte[3][core.sizeX[0] * core.sizeY[0]];

      int outIndex = 0;
      for (int i=0; i<3*core.sizeY[0]; i+=3) {
        byte[] c0 = (byte[]) strips.get(i);
        byte[] c1 = (byte[]) strips.get(i+1);
        byte[] c2 = (byte[]) strips.get(i+2);
        System.arraycopy(c0, 0, data[0], outIndex, c0.length);
        System.arraycopy(c1, 0, data[1], outIndex, c1.length);
        System.arraycopy(c2, 0, data[2], outIndex, c2.length);
        outIndex += core.sizeX[0];
      }

      if (debug) {
        debug("openBytes: 24-bit data, " + core.sizeX[0] + " x " +
          core.sizeY[0] + ", length=" + data.length + "x" + data[0].length);
      }
      return ImageTools.makeImage(data, core.sizeX[0], core.sizeY[0]);
    }
    else if (core.sizeY[0]*4 == strips.size()) {
      // 32 bit data
      byte[][] data = new byte[3][core.sizeX[0] * core.sizeY[0]];

      int outIndex = 0;
      for (int i=0; i<4*core.sizeY[0]; i+=4) {
        //byte[] a = (byte[]) strips.get(i);
        byte[] r = (byte[]) strips.get(i+1);
        byte[] g = (byte[]) strips.get(i+2);
        byte[] b = (byte[]) strips.get(i+3);
        System.arraycopy(r, 0, data[0], outIndex, r.length);
        System.arraycopy(g, 0, data[1], outIndex, g.length);
        System.arraycopy(b, 0, data[2], outIndex, b.length);
        //System.arraycopy(a, 0, data[3], outIndex, a.length);
        outIndex += core.sizeX[0];
      }

      if (debug) {
        debug("openBytes: 32-bit data, " + core.sizeX[0] + " x " +
          core.sizeY[0] + ", length=" + data.length + "x" + data[0].length);
      }
      return ImageTools.makeImage(data, core.sizeX[0], core.sizeY[0]);
    }
    else {
      // 16 bit data
      short[] data = new short[3 * core.sizeY[0] * core.sizeX[0]];

      int outIndex = 0;
      for (int i=0; i<core.sizeY[0]; i++) {
        int[] row = (int[]) strips.get(i);

        for (int j=0; j<row.length; j++, outIndex+=3) {
          if (j < core.sizeX[0]) {
            if (outIndex >= data.length - 2) break;
            int s0 = (row[j] & 0x1f);
            int s1 = (row[j] & 0x3e0) >> 5; // 0x1f << 5;
            int s2 = (row[j] & 0x7c00) >> 10; // 0x1f << 10;
            data[outIndex] = (short) s2;
            data[outIndex+1] = (short) s1;
            data[outIndex+2] = (short) s0;
          }
          else j = row.length;
        }
      }

      if (debug) {
        debug("openBytes: 16-bit data, " + core.sizeX[0] + " x " +
          core.sizeY[0] + ", length=" + data.length);
      }
      return ImageTools.makeImage(data, core.sizeX[0], core.sizeY[0], 3, true);
    }
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    if (block.length < 528) return false;
    return true;
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    buf = ImageTools.getBytes(openImage(no), false, no % 3);
    return buf;
  }

  /* @see loci.formats.IFormatReader#openImage(int) */
  public BufferedImage openImage(int no) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);

    return open(bytes);
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("PictReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    status("Populating metadata");

    core.littleEndian[0] = false;

    // skip the header and read in the remaining bytes
    int len = (int) (in.length() - 512);
    bytes = new byte[len];
    in.seek(512);
    in.read(bytes);

    byte[] b = new byte[20];
    in.seek(512);
    in.read(b);
    Dimension d = getDimensions(b);

    core.sizeX[0] = d.width;
    while (core.sizeX[0] % 8 != 0) core.sizeX[0]++;
    core.sizeY[0] = d.height;
    core.sizeZ[0] = 1;
    core.sizeC[0] = 3;
    core.sizeT[0] = 1;
    core.currentOrder[0] = "XYCZT";
    core.rgb[0] = true;
    core.interleaved[0] = false;
    core.imageCount[0] = 1;
    core.indexed[0] = false;
    core.falseColor[0] = false;
    core.metadataComplete[0] = true;

    // The metadata store we're working with.
    MetadataStore store = getMetadataStore();
    store.setImage(currentId, null, null, null);

    core.pixelType[0] = ImageTools.getPixelType(openImage(0));

    FormatTools.populatePixels(store, this);
    for (int i=0; i<core.sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null);
    }
  }

  // -- Helper methods --

  /** Loop through the remainder of the file and find relevant opcodes. */
  private boolean driveDecoder() throws FormatException, IOException {
    if (debug) debug("driveDecoder");
    int opcode;

    switch (pictState) {
      case INITIAL:
        ras.skipBytes(10);
        int verOpcode = ras.read();
        int verNumber = ras.read();

        if (verOpcode == 0x11 && verNumber == 0x01) versionOne = true;
        else if (verOpcode == 0x00 && verNumber == 0x11) {
          versionOne = false;
          int verNumber2 = ras.readShort();

          if (verNumber2 != 0x02ff) {
            throw new FormatException("Invalid PICT file : " + verNumber2);
          }

          // skip over v2 header -- don't need it here
          ras.skipBytes(26);
        }
        else throw new FormatException("Invalid PICT file");

        pictState = STATE2;
        state |= INFOAVAIL;
        return true;

      case STATE2:
        if (versionOne) opcode = ras.read();
        else {
          // if at odd boundary skip a byte for opcode in PICT v2

          if ((ras.getFilePointer() & 0x1L) != 0) {
            ras.skipBytes(1);
          }
          opcode = ras.readShort();
        }
        return drivePictDecoder(opcode);
    }
    return true;
  }

  /** Handles the opcodes in the PICT file. */
  private boolean drivePictDecoder(int opcode)
    throws FormatException, IOException
  {
    if (debug) debug("drivePictDecoder");

    switch (opcode) {
      case PICT_BITSRGN:  // rowBytes must be < 8
      case PICT_PACKBITSRGN: // rowBytes must be < 8
      case PICT_BITSRECT: // rowBytes must be < 8
      case PICT_PACKBITSRECT:
      case PICT_9A:
        handlePackBits(opcode);
        break;
      case PICT_CLIP_RGN:
        int x = ras.readShort();
        ras.skipBytes(x - 2);
        break;
      case PICT_LONGCOMMENT:
        ras.skipBytes(2);
        x = ras.readShort();
        ras.skipBytes(x);
        break;
      case PICT_END: // end of PICT
        state |= IMAGEAVAIL;
        return false;
    }

    return ras.getFilePointer() < ras.length();
  }

  /** Handles bitmap and pixmap opcodes of PICT format. */
  private void handlePackBits(int opcode)
    throws FormatException, IOException
  {
    if (debug) debug("handlePackBits(" + opcode + ")");
    if (opcode == PICT_9A) {
      // special case
      handlePixmap(opcode);
    }
    else {
      rowBytes = ras.readShort();
      if (versionOne || (rowBytes & 0x8000) == 0) handleBitmap(opcode);
      else handlePixmap(opcode);
    }
  }

  /** Extract the image data in a PICT bitmap structure. */
  private void handleBitmap(int opcode)
    throws FormatException, IOException
  {
    if (debug) debug("handleBitmap(" + opcode + ")");
    int row;
    byte[] buf;  // raw byte buffer for data from file
    byte[] uBuf; // uncompressed data -- possibly still pixel packed
    byte[] outBuf; // expanded pixel data

    rowBytes &= 0x3fff;  // mask off flags

    // read the bitmap data -- 3 rectangles + mode

    int tlY = ras.readShort();
    int tlX = ras.readShort();
    int brY = ras.readShort();
    int brX = ras.readShort();

    // skip next two rectangles
    ras.skipBytes(18);

    core.sizeX[0] = brX - tlX;
    core.sizeY[0] = brY - tlY;

    // allocate enough space to handle compressed data length for rowBytes

    try {
      buf = new byte[rowBytes + 1 + rowBytes/128];
      uBuf = new byte[rowBytes];
      outBuf = new byte[core.sizeX[0]];
    }
    catch (NegativeArraySizeException n) {
      throw new FormatException("Sorry, vector data not supported.");
    }

    for (row=0; row < core.sizeY[0]; ++row) {
      if (rowBytes < 8) {  // data is not compressed
        ras.read(buf, 0, rowBytes);

        for (int j=buf.length; --j >= 0;) {
          buf[j] = (byte) ~buf[j];
        }
        expandPixels(1, buf, outBuf, outBuf.length);
      }
      else {
        int rawLen;
        if (rowBytes > 250) rawLen = ras.readShort();
        else rawLen = ras.read();

        try {
          ras.read(buf, 0, rawLen);
        }
        catch (ArrayIndexOutOfBoundsException e) {
          throw new FormatException("Sorry, vector data not supported.");
        }

        PackbitsCodec c = new PackbitsCodec();
        uBuf = c.decompress(buf);
        //uBuf = Compression.packBitsUncompress(buf);

        // invert the pixels -- PICT images map zero to white
        for (int j=0; j<uBuf.length; j++) uBuf[j] = (byte) ~uBuf[j];

        expandPixels(1, uBuf, outBuf, outBuf.length);
      }
      strips.add(outBuf);
    }
  }

  /** Extracts the image data in a PICT pixmap structure. */
  private void handlePixmap(int opcode)
    throws FormatException, IOException
  {
    if (debug) debug("handlePixmap(" + opcode + ")");
    int pixelSize;
    int compCount;

    // handle 9A variation
    if (opcode == PICT_9A) {
      // this is the only opcode that holds 16, 24, and 32 bit data

      // read the pixmap (9A)

      ras.skipBytes(6);

      // read the bounding box
      int tlY = ras.readShort();
      int tlX = ras.readShort();
      int brY = ras.readShort();
      int brX = ras.readShort();

      ras.skipBytes(18);

      pixelSize = ras.readShort();
      compCount = ras.readShort();
      ras.skipBytes(14);

      core.sizeX[0] = brX - tlX;
      core.sizeY[0] = brY - tlY;

      // rowBytes doesn't exist, so set it to its logical value
      switch (pixelSize) {
        case 32:
          rowBytes = core.sizeX[0] * compCount;
          break;
        case 16:
          rowBytes = core.sizeX[0] * 2;
          break;
        default:
          throw new FormatException("Sorry, vector data not supported.");
      }
    }
    else {
      rowBytes &= 0x3fff;  // mask off flags

      int tlY = ras.readShort();
      int tlX = ras.readShort();
      int brY = ras.readShort();
      int brX = ras.readShort();

      ras.skipBytes(18);

      pixelSize = ras.readShort();
      compCount = ras.readShort();

      ras.skipBytes(14);

      // read the lookup table

      ras.skipBytes(4);
      int flags = ras.readShort();
      int count = ras.readShort();

      count++;
      lookup = new short[3][count];

      for (int i=0; i<count; i++) {
        int index = ras.readShort();
        if ((flags & 0x8000) != 0) index = i;
        lookup[0][index] = ras.readShort();
        lookup[1][index] = ras.readShort();
        lookup[2][index] = ras.readShort();
      }

      core.sizeX[0] = brX - tlX;
      core.sizeY[0] = brY - tlY;
    }

    // skip over two rectangles
    ras.skipBytes(18);

    if (opcode == PICT_BITSRGN || opcode == PICT_PACKBITSRGN) ras.skipBytes(2);

    handlePixmap(rowBytes, pixelSize, compCount);
  }

  /** Handles the unpacking of the image data. */
  private void handlePixmap(int rBytes, int pixelSize, int compCount)
    throws FormatException, IOException
  {
    if (debug) {
      debug("handlePixmap(" + rBytes + ", " +
        pixelSize + ", " + compCount + ")");
    }
    int rawLen;
    byte[] buf;  // row raw bytes
    byte[] uBuf = null;  // row uncompressed data
    int[] uBufI = null;  // row uncompressed data - 16+ bit pixels
    int bufSize;
    int outBufSize;
    byte[] outBuf = null;  // used to expand pixel data

    boolean compressed = (rBytes >= 8) || (pixelSize == 32);

    bufSize = rBytes;

    outBufSize = core.sizeX[0];

    // allocate buffers

    switch (pixelSize) {
      case 32:
        if (!compressed) uBufI = new int[core.sizeX[0]];
        else uBuf = new byte[bufSize];
        break;
      case 16:
        uBufI = new int[core.sizeX[0]];
        break;
      case 8:
        uBuf = new byte[bufSize];
        break;
      default:
        outBuf = new byte[outBufSize];
        uBuf = new byte[bufSize];
        break;
    }

    if (!compressed) {
      if (debug) {
        debug("Pixel data is uncompressed (pixelSize=" + pixelSize + ").");
      }
      buf = new byte[bufSize];
      for (int row=0; row<core.sizeY[0]; row++) {
        ras.read(buf, 0, rBytes);

        switch (pixelSize) {
          case 16:
            for (int i=0; i<core.sizeX[0]; i++) {
              uBufI[i] = ((buf[i*2] & 0xff) << 8) + (buf[i*2+1] & 0xff);
            }
            strips.add(uBufI);
            break;
          case 8:
            strips.add(buf);
            break;
          default: // pixel size < 8
            expandPixels(pixelSize, buf, outBuf, outBuf.length);
            strips.add(outBuf);
        }
      }
    }
    else {
      if (debug) {
        debug("Pixel data is compressed (pixelSize=" +
          pixelSize + "; compCount=" + compCount + ").");
      }
      buf = new byte[bufSize + 1 + bufSize / 128];
      for (int row=0; row<core.sizeY[0]; row++) {
        if (rBytes > 250) rawLen = ras.readShort();
        else rawLen = ras.read();

        if (rawLen > buf.length) rawLen = buf.length;

        if ((ras.length() - ras.getFilePointer()) <= rawLen) {
          rawLen = (int) (ras.length() - ras.getFilePointer() - 1);
        }

        if (rawLen < 0) {
          rawLen = 0;
          ras.seek(ras.length() - 1);
        }

        ras.read(buf, 0, rawLen);

        if (pixelSize == 16) {
          uBufI = new int[core.sizeX[0]];
          unpackBits(buf, uBufI);
          strips.add(uBufI);
        }
        else {
          PackbitsCodec c = new PackbitsCodec();
          uBuf = c.decompress(buf);
          //uBuf = Compression.packBitsUncompress(buf);
        }

        if (pixelSize < 8) {
          expandPixels(pixelSize, uBuf, outBuf, outBuf.length);
          strips.add(outBuf);
        }
        else if (pixelSize == 8) strips.add(uBuf);
        else if (pixelSize == 24 || pixelSize == 32) {
          byte[] newBuf = null;
          int offset = 0;

          if (compCount == 4) {
            // alpha channel
            //newBuf = new byte[width];
            //System.arraycopy(uBuf, offset, newBuf, 0, width);
            strips.add(newBuf);
            offset += core.sizeX[0];
          }

          // red channel
          newBuf = new byte[core.sizeX[0]];
          System.arraycopy(uBuf, offset, newBuf, 0, core.sizeX[0]);
          strips.add(newBuf);
          offset += core.sizeX[0];

          // green channel
          newBuf = new byte[core.sizeX[0]];
          System.arraycopy(uBuf, offset, newBuf, 0, core.sizeX[0]);
          strips.add(newBuf);
          offset += core.sizeX[0];

          // blue channel
          newBuf = new byte[core.sizeX[0]];
          System.arraycopy(uBuf, offset, newBuf, 0, core.sizeX[0]);
          strips.add(newBuf);
        }
      }
    }
  }

  /** Expand an array of bytes. */
  private void expandPixels(int bitSize, byte[] ib, byte[] ob, int outLen)
    throws FormatException
  {
    if (debug) {
      debug("expandPixels(" + bitSize + ", " +
        ib.length + ", " + ob.length + ", " + outLen + ")");
    }
    if (bitSize == 1) {
      int remainder = outLen % 8;
      int max = outLen / 8;
      for (int i=0; i<max; i++) {
        if (i < ib.length) {
          int look = (ib[i] & 0xff) * 8;
          System.arraycopy(EXPANSION_TABLE, look, ob, i*8, 8);
        }
        else i = max;
      }

      if (remainder != 0) {
        if (max < ib.length) {
          System.arraycopy(EXPANSION_TABLE, (ib[max] & 0xff) * 8, ob,
            max*8, remainder);
        }
      }

      return;
    }

    int i;
    int o;
    int t;
    byte v;
    int count = 8 / bitSize; // number of pixels in a byte
    int maskshift = bitSize; // num bits to shift mask
    int pixelshift = 8 - bitSize; // num bits to shift pixel
    int tpixelshift = 0;
    int pixelshiftdelta = bitSize;
    int mask = 0;
    int tmask; // temp mask

    if (bitSize != 1 && bitSize != 2 && bitSize != 4) {
      throw new FormatException("Can only expand 1, 2, and 4 bit values");
    }

    switch (bitSize) {
      case 1:
        mask = 0x80;
        break;
      case 2:
        mask = 0xC0;
        break;
      case 4:
        mask = 0xF0;
        break;
    }

    i = 0;
    for (o = 0; o < ob.length;) {
      tmask = mask;
      tpixelshift = pixelshift;
      v = ib[i];
      for (t = 0; t < count && o < ob.length; ++t, ++o) {
        ob[o] = (byte) (((v & tmask) >>> tpixelshift) & 0xff);
        tmask = (byte) ((tmask & 0xff) >>> maskshift);
        tpixelshift -= pixelshiftdelta;
      }
      ++i;
    }
  }

  /** PackBits variant that outputs an int array. */
  private void unpackBits(byte[] ib, int[] ob) {
    if (debug) debug("unpackBits(" + ib + ", " + ob + ")");
    int i = 0;
    int o = 0;
    int b;
    int rep;
    int end;

    for (o=0; o<ob.length;) {
      if (i+1 < ib.length) {
        b = ib[i++];
        if (b >= 0) {
          b++;
          end = o + b;
          for(; o < end; o++, i+=2) {
            if (o < ob.length && (i+1) < ib.length) {
              ob[o] = (((ib[i] & 0xff) << 8) + (ib[i+1] & 0xff)) & 0xffff;
            }
            else o = end;
          }
        }
        else if (b != -128) {
          rep = (((ib[i] & 0xff) << 8) + (ib[i+1] & 0xff)) & 0xffff;
          i += 2;
          end = o - b + 1;
          for (; o < end; o++) {
            if (o < ob.length) ob[o] = rep;
            else o = end;
          }
        }
      }
      else o = ob.length;
    }
  }

}
