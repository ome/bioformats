//
// PictReader.java
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
import java.awt.Dimension;
import java.io.*;
import java.util.*;

/**
 * PictReader is the file format reader for Apple PICT files.
 * Most of this code was adapted from the PICT readers in JIMI
 * (http://java.sun.com/products/jimi/index.html), ImageMagick
 * (http://www.imagemagick.org), and Java QuickDraw.
 */
public class PictReader extends FormatReader {

  // -- Constants --

  // opcodes that we need
  private static final int PICT_CLIP_RGN = 0x1;
  private static final int PICT_BITSRECT = 0x90;
  private static final int PICT_BITSRGN = 0x91;
  private static final int PICT_PACKBITSRECT = 0x98;
  private static final int PICT_PACKBITSRGN = 0x99;
  private static final int PICT_9A = 0x9a;
  private static final int PICT_HEADER = 0xc00;
  private static final int PICT_END = 0xff;
  private static final int PICT_LONGCOMMENT = 0xa1;

  // possible image states
  private static final int INITIAL = 1;
  private static final int STATE2 = 2;

  // other stuff?
  private static final int INFOAVAIL = 0x0002;
  private static final int IMAGEAVAIL = 0x0004;

  /** Table used in expanding pixels that use less than 8 bits. */
  private static final byte[] EXPANSION_TABLE = new byte[256 * 8];

  static {
    int index = 0;
    for (int i = 0; i < 256; i++) {
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

  /** Current file. */
  protected RandomAccessFile in;

  /** Flag indicating whether current file is little endian. */
  protected boolean little;

  /** Pixel bytes. */
  protected byte[] bytes;

  /** Width of the image. */
  protected int width;

  /** Height of the image. */
  protected int height;

  /** Number of bytes in a row of pixel data (variable). */
  protected int rowBytes;

  /** Decoder state. */
  protected int state;

  /** Image state. */
  protected int pictState;

  /** Pointer into the array of bytes representing the file. */
  protected int pt;

  /** Vector of byte arrays representing individual rows. */
  protected Vector strips;

  /** Whether or not the file is PICT v1. */
  protected boolean versionOne;

  /** Color lookup table for palette color images. */
  protected short[][] lookup;

  /** Helper reader in case this one fails. */
  protected LegacyQTReader qtReader = new LegacyQTReader();


  // -- Constructor --

  /** Constructs a new PICT reader. */
  public PictReader() { super("PICT", new String[] {"pict", "pct"}); }


  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for a PICT file. */
  public boolean isThisType(byte[] block) {
    try {
      if (in.length() < 528) return false;
      return true;
    }
    catch (IOException e) {
      return false;
    }
  }

  /** Determines the number of images in the given PICT file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return 1;
  }

  /** Obtains the specified image from the given PICT file. */
  public BufferedImage open(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (no != 0) throw new FormatException("Invalid image number: " + no);
    return openBytes(bytes);
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  /** Initializes the given PICT file. */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessFile(id, "r");
    little = false;

    // skip the header and read in the remaining bytes
    int len = (int) (in.length() - 512);
    bytes = new byte[len];
    in.skipBytes(512);
    in.read(bytes);
  }


  // -- PictReader API methods --

  /** Get the dimensions of a PICT file from the first 4 bytes after header. */
  public Dimension getDimensions(byte[] stuff) throws FormatException {
    if (stuff.length < 10) {
      throw new FormatException("Need 10 bytes to calculate dimension");
    }
    int w = DataTools.bytesToInt(stuff, 6, 2, little);
    int h = DataTools.bytesToInt(stuff, 8, 2, little);
    if (DEBUG) {
      System.out.println("PictReader.getDimensions: " + w + " x " + h);
    }
    return new Dimension(h, w);
  }

  /** Open a PICT image from an array of bytes (used by OpenlabReader). */
  public BufferedImage openBytes(byte[] pix) throws FormatException {
    // handles case when we call this method directly, instead of
    // through initFile(String)
    if (DEBUG) System.out.println("PictReader.openBytes");

    strips = new Vector();

    state = 0;
    pictState = INITIAL;
    bytes = pix;
    pt = 0;

    try {
      while (driveDecoder());
    }
    catch (Exception e) {
      e.printStackTrace();
      return ImageTools.makeBuffered(qtReader.pictToImage(pix));
    }

    // combine everything in the strips Vector

    if ((height*4 < strips.size()) && (((strips.size() / 3) % height) != 0)) {
      height = strips.size();
    }

    if (strips.size() == 0) {
      return ImageTools.makeBuffered(qtReader.pictToImage(pix));
    }

    if (lookup != null) {
      // 8 bit data
      short[][] data = new short[3][height * width];

      byte[] row;

      for (int i=0; i<height; i++) {
        row = (byte[]) strips.get(i);

        for (int j=0; j<row.length; j++) {
          if (j < width) {
            int ndx = row[j];
            if (ndx < 0) ndx += lookup[0].length;
            ndx = ndx % lookup[0].length;

            int outIndex = i*width + j;
            if (outIndex >= data[0].length) outIndex = data[0].length - 1;

            data[0][outIndex] = lookup[0][ndx];
            data[1][outIndex] = lookup[1][ndx];
            data[2][outIndex] = lookup[2][ndx];
          }
          else j = row.length;
        }
      }

      if (DEBUG) {
        System.out.println("PictReader.openBytes: 8-bit data, " + width +
          " x " + height + ", length=" + data.length + "x" + data[0].length);
      }
      return ImageTools.makeImage(data, width, height);
    }
    else if (height*3 == strips.size()) {
      // 24 bit data
      byte[][] data = new byte[3][width * height];

      int outIndex = 0;
      for (int i=0; i<3*height; i+=3) {
        byte[] c0 = (byte[]) strips.get(i);
        byte[] c1 = (byte[]) strips.get(i+1);
        byte[] c2 = (byte[]) strips.get(i+2);
        System.arraycopy(c0, 0, data[0], outIndex, c0.length);
        System.arraycopy(c1, 0, data[1], outIndex, c1.length);
        System.arraycopy(c2, 0, data[2], outIndex, c2.length);
        outIndex += width;
      }

      if (DEBUG) {
        System.out.println("PictReader.openBytes: 24-bit data, " + width +
          " x " + height + ", length=" + data.length + "x" + data[0].length);
      }
      return ImageTools.makeImage(data, width, height);
    }
    else if (height*4 == strips.size()) {
      // 32 bit data
      byte[][] data = new byte[3][width * height];

      int outIndex = 0;
      for (int i=0; i<4*height; i+=4) {
        //byte[] a = (byte[]) strips.get(i);
        byte[] r = (byte[]) strips.get(i+1);
        byte[] g = (byte[]) strips.get(i+2);
        byte[] b = (byte[]) strips.get(i+3);
        System.arraycopy(r, 0, data[0], outIndex, r.length);
        System.arraycopy(g, 0, data[1], outIndex, g.length);
        System.arraycopy(b, 0, data[2], outIndex, b.length);
        //System.arraycopy(a, 0, data[3], outIndex, a.length);
        outIndex += width;
      }

      if (DEBUG) {
        System.out.println("PictReader.openBytes: 32-bit data, " + width +
          " x " + height + ", length=" + data.length + "x" + data[0].length);
      }
      return ImageTools.makeImage(data, width, height);
    }
    else {
      // 16 bit data
      short[] data = new short[3 * height * width];

      int outIndex = 0;
      for (int i=0; i<height; i++) {
        int[] row = (int[]) strips.get(i);

        for (int j=0; j<row.length; j++, outIndex+=3) {
          if (j < width) {
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

      if (DEBUG) {
        System.out.println("PictReader.openBytes: 16-bit data, " +
          width + " x " + height + ", length=" + data.length);
      }
      return ImageTools.makeImage(data, width, height, 3, true);
    }
  }


  // -- Helper methods --

  /** Loop through the remainder of the file and find relevant opcodes. */
  private boolean driveDecoder() throws FormatException {
    if (DEBUG) System.out.println("PictReader.driveDecoder");
    int opcode, version;

    switch (pictState) {
      case INITIAL:
        int ret;
        int fileSize = DataTools.bytesToInt(bytes, pt, 2, little);
        pt += 2;
        // skip over frame
        pt += 8;
        int verOpcode = DataTools.bytesToInt(bytes, pt, 1, little);
        pt++;
        int verNumber = DataTools.bytesToInt(bytes, pt, 1, little);
        pt++;

        if (verOpcode == 0x11 && verNumber == 0x01) versionOne = true;
        else if (verOpcode == 0x00 && verNumber == 0x11) {
          versionOne = false;
          int verOpcode2 = 0x0011;
          int verNumber2 = DataTools.bytesToInt(bytes, pt, 2, little);
          pt += 2;

          if (verNumber2 != 0x02ff) {
            throw new FormatException("Invalid PICT file : " + verNumber2);
          }

          // skip over v2 header -- don't need it here
          pt += 26;
        }
        else throw new FormatException("Invalid PICT file");

        pictState = STATE2;
        state |= INFOAVAIL;
        return true;

      case STATE2:
        if (versionOne) {
          opcode = DataTools.bytesToInt(bytes, pt, 1, little);
          pt++;
        }
        else {
          // if at odd boundary skip a byte for opcode in PICT v2

          if ((pt & 0x1L) != 0) {
            pt++;
          }
          opcode = DataTools.bytesToInt(bytes, pt, 2, little);
          pt += 2;
        }
        return drivePictDecoder(opcode);
    }
    return true;
  }

  /** Handles the opcodes in the PICT file. */
  private boolean drivePictDecoder(int opcode) throws FormatException {
    if (DEBUG) System.out.println("PictReader.drivePictDecoder");
    short size, kind;

    switch (opcode) {
      case PICT_BITSRGN:  // rowBytes must be < 8
        handlePackBits(opcode);
        break;
      case PICT_PACKBITSRGN: // rowBytes must be < 8
        handlePackBits(opcode);
        break;
      case PICT_BITSRECT: // rowBytes must be < 8
        handlePackBits(opcode);
        break;
      case PICT_PACKBITSRECT:
        handlePackBits(opcode);
        break;
      case PICT_9A:
        handlePackBits(opcode);
        break;
      case PICT_CLIP_RGN:
        int x = DataTools.bytesToInt(bytes, pt, 2, little);
        pt += x;
        break;
      case PICT_LONGCOMMENT:
        pt += 2;
        x = DataTools.bytesToInt(bytes, pt, 2, little);
        pt += x + 2;
        break;
      case PICT_END: // end of PICT
        state |= IMAGEAVAIL;
        return false;
    }

    return (pt < bytes.length);
  }

  /** Handles bitmap and pixmap opcodes of PICT format. */
  private void handlePackBits(int opcode) throws FormatException {
    if (DEBUG) System.out.println("PictReader.handlePackBits(" + opcode + ")");
    if (opcode == PICT_9A) {
      // special case
      handlePixmap(opcode);
    }
    else {
      rowBytes = DataTools.bytesToInt(bytes, pt, 2, little);
      pt += 2;
      if (versionOne || (rowBytes & 0x8000) == 0) handleBitmap(opcode);
      else handlePixmap(opcode);
    }
  }

  /** Extract the image data in a PICT bitmap structure. */
  private void handleBitmap(int opcode) throws FormatException {
    if (DEBUG) System.out.println("PictReader.handleBitmap(" + opcode + ")");
    int row;
    byte[] buf;  // raw byte buffer for data from file
    byte[] uBuf; // uncompressed data -- possibly still pixel packed
    byte[] outBuf; // expanded pixel data

    rowBytes &= 0x3fff;  // mask off flags

    // read the bitmap data -- 3 rectangles + mode

    int tlY = DataTools.bytesToInt(bytes, pt, 2, little);
    pt += 2;
    int tlX = DataTools.bytesToInt(bytes, pt, 2, little);
    pt += 2;
    int brY = DataTools.bytesToInt(bytes, pt, 2, little);
    pt += 2;
    int brX = DataTools.bytesToInt(bytes, pt, 2, little);
    pt += 2;

    // skip next two rectangles
    pt += 16;
    int mode = DataTools.bytesToInt(bytes, pt, 2, little);
    pt += 2;

    width = brX - tlX;
    height = brY - tlY;

    // allocate enough space to handle compressed data length for rowBytes

    try {
      buf = new byte[rowBytes + 1 + rowBytes/128];
      uBuf = new byte[rowBytes];
      outBuf = new byte[width];
    }
    catch (NegativeArraySizeException n) {
      throw new FormatException("Sorry, vector data not supported.");
    }

    for (row=0; row < height; ++row) {
      if (rowBytes < 8) {  // data is not compressed
        System.arraycopy(bytes, pt, buf, 0, rowBytes);

        for (int j=buf.length; --j >= 0; ) {
          buf[j] = (byte) ~buf[j];
        }
        expandPixels(1, buf, outBuf, outBuf.length);
      }
      else {
        int rawLen;
        if (rowBytes > 250) {
          rawLen = DataTools.bytesToInt(bytes, pt, 2, little);
          pt += 2;
        }
        else {
          rawLen = DataTools.bytesToInt(bytes, pt, 1, little);
          pt++;
        }

        try {
          System.arraycopy(bytes, pt, buf, 0, rawLen);
          pt += rawLen;
        }
        catch (ArrayIndexOutOfBoundsException e) {
          throw new FormatException("Sorry, vector data not supported.");
        }

        uBuf = Compression.packBitsUncompress(buf);

        // invert the pixels -- PICT images map zero to white
        for (int j=0; j<uBuf.length; j++) uBuf[j] = (byte) ~uBuf[j];

        expandPixels(1, uBuf, outBuf, outBuf.length);
      }
      strips.add(outBuf);
    }
  }

  /** Extracts the image data in a PICT pixmap structure. */
  private void handlePixmap(int opcode) throws FormatException {
    if (DEBUG) System.out.println("PictReader.handlePixmap(" + opcode + ")");
    int pixelSize;
    int compCount;

    // handle 9A variation
    if (opcode == PICT_9A) {
      // this is the only opcode that holds 16, 24, and 32 bit data

      // read the pixmap (9A)

      pt += 4; // skip fake length and fake EOF

      int version = DataTools.bytesToInt(bytes, pt, 2, little);
      pt += 2;

      // read the bounding box
      int tlY = DataTools.bytesToInt(bytes, pt, 2, little);
      pt += 2;
      int tlX = DataTools.bytesToInt(bytes, pt, 2, little);
      pt += 2;
      int brY = DataTools.bytesToInt(bytes, pt, 2, little);
      pt += 2;
      int brX = DataTools.bytesToInt(bytes, pt, 2, little);
      pt += 2;

      pt += 2; // undocumented extra short in the data structure

      int packType = DataTools.bytesToInt(bytes, pt, 2, little);
      pt += 2;
      int packSize = DataTools.bytesToInt(bytes, pt, 4, little);
      pt += 4;
      int hRes = DataTools.bytesToInt(bytes, pt, 4, little);
      pt += 4;
      int vRes = DataTools.bytesToInt(bytes, pt, 4, little);
      pt += 4;

      int pixelType = DataTools.bytesToInt(bytes, pt, 2, little);
      pt += 2;
      pixelSize = DataTools.bytesToInt(bytes, pt, 2, little);
      pt += 2;
      compCount = DataTools.bytesToInt(bytes, pt, 2, little);
      pt += 2;
      int compSize = DataTools.bytesToInt(bytes, pt, 2, little);
      pt += 2;

      int planeBytes = DataTools.bytesToInt(bytes, pt, 4, little);
      pt += 4;
      int pmTable = DataTools.bytesToInt(bytes, pt, 4, little);
      pt += 4;

      pt += 4; // reserved

      width = brX - tlX;
      height = brY - tlY;

      // rowBytes doesn't exist, so set it to its logical value
      switch (pixelSize) {
        case 32:
          rowBytes = width * compCount;
          break;
        case 16:
          rowBytes = width * 2;
          break;
        default:
          throw new FormatException("Sorry, vector data not supported.");
      }
    }
    else {
      rowBytes &= 0x3fff;  // mask off flags

      int tlY = DataTools.bytesToInt(bytes, pt, 2, little);
      pt += 2;
      int tlX = DataTools.bytesToInt(bytes, pt, 2, little);
      pt += 2;
      int brY = DataTools.bytesToInt(bytes, pt, 2, little);
      pt += 2;
      int brX = DataTools.bytesToInt(bytes, pt, 2, little);
      pt += 2;

      // unnecessary data
      pt += 18;

      pixelSize = DataTools.bytesToInt(bytes, pt, 2, little);
      pt += 2;
      compCount = DataTools.bytesToInt(bytes, pt, 2, little);
      pt += 2;

      // unnecessary data
      pt += 14;

      // read the lookup table

      pt += 2;
      int id = DataTools.bytesToInt(bytes, pt, 2, little);
      pt += 2;
      int flags = DataTools.bytesToInt(bytes, pt, 2, little);
      pt += 2;
      int count = DataTools.bytesToInt(bytes, pt, 2, little);
      pt += 2;

      count++;
      lookup = new short[3][count];

      for (int i=0; i<count; i++) {
        int index = DataTools.bytesToInt(bytes, pt, 2, little);
        pt += 2;
        if ((flags & 0x8000) != 0) index = i;
        lookup[0][index] = DataTools.bytesToShort(bytes, pt, 2, little);
        pt += 2;
        lookup[1][index] = DataTools.bytesToShort(bytes, pt, 2, little);
        pt += 2;
        lookup[2][index] = DataTools.bytesToShort(bytes, pt, 2, little);
        pt += 2;
      }

      width = brX - tlX;
      height = brY - tlY;
    }

    // skip over two rectangles
    pt += 16;

    int mode = DataTools.bytesToInt(bytes, pt, 2, little);
    pt += 2;

    if (opcode == PICT_BITSRGN || opcode == PICT_PACKBITSRGN) {
      int x = DataTools.bytesToInt(bytes, pt, 2, little);
      pt += 2;
    }

    handlePixmap(rowBytes, pixelSize, compCount);
  }

  /** Handles the unpacking of the image data. */
  private void handlePixmap(int rowBytes, int pixelSize, int compCount)
    throws FormatException
  {
    if (DEBUG) {
      System.out.println("PictReader.handlePixmap(" +
        rowBytes + ", " + pixelSize + ", " + compCount + ")");
    }
    int rawLen;
    byte[] buf;  // row raw bytes
    byte[] uBuf = null;  // row uncompressed data
    int[] uBufI = null;  // row uncompressed data - 16+ bit pixels
    int bufSize;
    int outBufSize;
    byte[] outBuf = null;  // used to expand pixel data

    boolean compressed = (rowBytes >= 8) || (pixelSize == 32);

    bufSize = rowBytes;

    outBufSize = width;

    // allocate buffers

    switch (pixelSize) {
      case 32:
        if (!compressed) uBufI = new int[width];
        else uBuf = new byte[bufSize];
        break;
      case 16:
        uBufI = new int[width];
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
      if (DEBUG) {
        System.out.println("Pixel data is uncompressed (pixelSize=" +
          pixelSize + ").");
      }
      buf = new byte[bufSize];
      for (int row=0; row<height; row++) {
        System.arraycopy(bytes, pt, buf, 0, rowBytes);

        switch (pixelSize) {
          case 16:
            for (int i=0; i<width; i++) {
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
      if (DEBUG) {
        System.out.println("Pixel data is compressed (pixelSize=" +
          pixelSize + "; compCount=" + compCount + ").");
      }
      buf = new byte[bufSize + 1 + bufSize / 128];
      for (int row=0; row<height; row++) {
        if (rowBytes > 250) {
          rawLen = DataTools.bytesToInt(bytes, pt, 2, little);
          pt += 2;
        }
        else {
          rawLen = DataTools.bytesToInt(bytes, pt, 1, little);
          pt++;
        }

        if (rawLen > buf.length) rawLen = buf.length;

        if ((bytes.length - pt) <= rawLen) {
          rawLen = bytes.length - pt - 1;
        }

        if (rawLen < 0) {
          rawLen = 0;
          pt = bytes.length - 1;
        }

        System.arraycopy(bytes, pt, buf, 0, rawLen);
        pt += rawLen;

        if (pixelSize == 16) {
          uBufI = new int[width];
          unpackBits(buf, uBufI);
          strips.add(uBufI);
        }
        else uBuf = Compression.packBitsUncompress(buf);

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
            offset += width;
          }

          // red channel
          newBuf = new byte[width];
          System.arraycopy(uBuf, offset, newBuf, 0, width);
          strips.add(newBuf);
          offset += width;

          // green channel
          newBuf = new byte[width];
          System.arraycopy(uBuf, offset, newBuf, 0, width);
          strips.add(newBuf);
          offset += width;

          // blue channel
          newBuf = new byte[width];
          System.arraycopy(uBuf, offset, newBuf, 0, width);
          strips.add(newBuf);
        }
      }
    }
  }

  /** Expand an array of bytes. */
  private void expandPixels(int bitSize, byte[] in, byte[] out, int outLen)
    throws FormatException
  {
    if (DEBUG) {
      System.out.println("PictReader.expandPixels(" + bitSize + ", " +
        in.length + ", " + out.length + ", " + outLen + ")");
    }
    if (bitSize == 1) {
      int remainder = outLen % 8;
      int max = outLen / 8;
      for (int i=0; i<max; i++) {
        if (i < in.length) {
          int look = (in[i] & 0xff) * 8;
          System.arraycopy(EXPANSION_TABLE, look, out, i*8, 8);
        }
        else i = max;
      }

      if (remainder != 0) {
        if (max < in.length) {
          System.arraycopy(EXPANSION_TABLE, (in[max] & 0xff) * 8, out,
            max*8, remainder);
        }
      }

      return;
    }

    int i;
    int o;
    int t;
    byte v;
    int count = 0; // number of pixels in a byte
    int maskshift = 1; // num bits to shift mask
    int pixelshift = 0; // num bits to shift pixel
    int tpixelshift = 0;
    int pixelshiftdelta = 0;
    int mask = 0;
    int tmask; // temp mask

    if (bitSize != 1 && bitSize != 2 && bitSize != 4) {
      throw new FormatException("Can only expand 1, 2, and 4 bit values");
    }

    switch (bitSize)
    {
      case 1:
        mask = 0x80;
        maskshift = 1;
        count = 8;
        pixelshift = 7;
        pixelshiftdelta = 1;
        break;
      case 2:
        mask = 0xC0;
        maskshift = 2;
        count = 4;
        pixelshift = 6;
        pixelshiftdelta = 2;
        break;
      case 4:
        mask = 0xF0;
        maskshift = 4;
        count = 2;
        pixelshift = 4;
        pixelshiftdelta = 4;
        break;
    }

    i = 0;
    for (o = 0; o < out.length;) {
      tmask = mask;
      tpixelshift = pixelshift;
      v = in[i];
      for (t = 0; t < count && o < out.length; ++t, ++o) {
        out[o] = (byte) (((v & tmask) >>> tpixelshift) & 0xff);
        tmask = (byte) ((tmask & 0xff) >>> maskshift);
        tpixelshift -= pixelshiftdelta;
      }
      ++i;
    }
  }

  /** PackBits variant that outputs an int array. */
  private void unpackBits(byte[] in, int[] out) {
    if (DEBUG) {
      System.out.println("PictReader.unpackBits(" + in + ", " + out + ")");
    }
    int i = 0;
    int o = 0;
    int b;
    int rep;
    int end;

    for (o=0; o<out.length;) {
      if ((i+1) < in.length) {
        b = in[i++];
        if (b >= 0) {
          b++;
          end = o + b;
          for(; o < end; o++, i+=2) {
            if ((o < out.length) && ((i+1) < in.length)) {
              out[o] = (((in[i] & 0xff) << 8) + (in[i+1] & 0xff)) & 0xffff;
            }
            else o = end;
          }
        }
        else if (b != -128) {
          rep = (((in[i] & 0xff) << 8) + (in[i+1] & 0xff)) & 0xffff;
          i += 2;
          end = o - b + 1;
          for (; o < end; o++) {
            if (o < out.length) {
              out[o] = rep;
            }
            else o = end;
          }
        }
      }
      else o = out.length;
    }
  }


  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new PictReader().testRead(args);
  }

}
