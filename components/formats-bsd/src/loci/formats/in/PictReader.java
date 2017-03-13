/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.formats.in;

import java.io.IOException;
import java.util.Vector;

import loci.common.ByteArrayHandle;
import loci.common.DataTools;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.JPEGCodec;
import loci.formats.codec.PackbitsCodec;
import loci.formats.gui.AWTImageTools;
import loci.formats.gui.LegacyQTTools;
import loci.formats.meta.MetadataStore;

/**
 * PictReader is the file format reader for Apple PICT files.
 * Most of this code was adapted from the PICT readers in JIMI
 * (http://java.sun.com/products/jimi/index.html), ImageMagick
 * (http://www.imagemagick.org), and Java QuickDraw.
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
  private static final int PICT_JPEG = 0x18;
  private static final int PICT_TYPE_1 = 0xa9f;
  private static final int PICT_TYPE_2 = 0x9190;

  /** Table used in expanding pixels that use less than 8 bits. */
  private static final byte[] EXPANSION_TABLE = new byte[256 * 8];

  static {
    for (int i=0; i<256; i++) {
      for (int j=0; j<8; j++) {
        EXPANSION_TABLE[i*8 + j] =
          (byte) ((i & (int) Math.pow(2, 7 - j)) >> 7 - j);
      }
    }
  }

  // -- Fields --

  /** Number of bytes in a row of pixel data (variable). */
  protected int rowBytes;

  /** Vector of byte arrays representing individual rows. */
  protected Vector strips;

  /** Whether or not the file is PICT v1. */
  protected boolean versionOne;

  /** Color lookup table for palette color images. */
  protected byte[][] lookup;

  /** Helper reader in case this one fails. */
  protected LegacyQTTools qtTools = new LegacyQTTools();

  private boolean legacy = false;
  private Vector<Long> jpegOffsets = new Vector<Long>();

  // -- Constructor --

  /** Constructs a new PICT reader. */
  public PictReader() {
    super("PICT", new String[] {"pict", "pct"});
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
  }

  // -- PictReader API methods --

  /** Control whether or not legacy reader (QT Java) is used. */
  public void setLegacy(boolean legacy) {
    this.legacy = legacy;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  @Override
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    return lookup;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    if (jpegOffsets.size() > 0) {
      ByteArrayHandle v = new ByteArrayHandle();
      in.seek(jpegOffsets.get(0));
      byte[] b = new byte[(int) (in.length() - in.getFilePointer())];
      in.read(b);
      RandomAccessInputStream s = new RandomAccessInputStream(b);
      for (long jpegOffset : jpegOffsets) {
        s.seek(jpegOffset - jpegOffsets.get(0));

        CodecOptions options = new CodecOptions();
        options.interleaved = isInterleaved();
        options.littleEndian = isLittleEndian();

        v.write(new JPEGCodec().decompress(s, options));
      }

      s = new RandomAccessInputStream(v);
      s.seek(0);
      readPlane(s, x, y, w, h, buf);
      s.close();

      return buf;
    }

    if (legacy || strips.size() == 0) {
      in.seek(512);
      byte[] pix = new byte[(int) (in.length() - in.getFilePointer())];
      in.read(pix);
      byte[][] b = AWTImageTools.getBytes(
        AWTImageTools.makeBuffered(qtTools.pictToImage(pix)));
      pix = null;
      for (int i=0; i<b.length; i++) {
        System.arraycopy(b[i], 0, buf, i*b[i].length, b[i].length);
      }
      b = null;
      return buf;
    }

    // combine everything in the strips Vector

    if ((getSizeY()*4 < strips.size()) && (((strips.size() / 3) %
      getSizeY()) != 0))
    {
      core.get(0).sizeY = strips.size();
    }

    int plane = w * h;

    if (lookup != null) {
      // 8 bit data

      byte[] row;

      for (int i=y; i<y+h; i++) {
        row = (byte[]) strips.get(i);
        int len = (int) Math.min(row.length, w);
        System.arraycopy(row, x, buf, (i - y) * w, len);
      }
    }
    else if (getSizeY()*3 == strips.size() || getSizeY()*4 == strips.size()) {
      // 24 or 32 bit data

      int nc = strips.size() / getSizeY();

      byte[] c0 = null;
      byte[] c1 = null;
      byte[] c2 = null;

      for (int i=y; i<h + y; i++) {
        c0 = (byte[]) strips.get(i * nc + nc - 3);
        c1 = (byte[]) strips.get(i * nc + nc - 2);
        c2 = (byte[]) strips.get(i * nc + nc - 1);
        int baseOffset = (i - y) * w;
        System.arraycopy(c0, x, buf, baseOffset, w);
        System.arraycopy(c1, x, buf, plane + baseOffset, w);
        System.arraycopy(c2, x, buf, 2*plane + baseOffset, w);
      }
    }
    else {
      // RGB value is packed into a single short: xRRR RRGG GGGB BBBB
      int[] row = null;
      for (int i=y; i<h + y; i++) {
        row = (int[]) strips.get(i);

        for (int j=x; j<w + x; j++) {
          int base = (i - y) * w + (j - x);
          buf[base] = (byte) ((row[j] & 0x7c00) >> 10);
          buf[plane + base] = (byte) ((row[j] & 0x3e0) >> 5);
          buf[2 * plane + base] = (byte) (row[j] & 0x1f);
        }
      }
    }
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      rowBytes = 0;
      strips = null;
      versionOne = false;
      lookup = null;
      legacy = false;
      jpegOffsets.clear();
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    CoreMetadata m = core.get(0);

    m.littleEndian = false;

    in.seek(518);

    m.sizeY = in.readShort();
    m.sizeX = in.readShort();
    m.sizeZ = 1;
    m.sizeC = 1;
    m.sizeT = 1;
    m.dimensionOrder = "XYCZT";
    m.imageCount = 1;
    m.falseColor = false;
    m.metadataComplete = true;
    m.interleaved = false;
    m.pixelType = FormatTools.UINT8;

    strips = new Vector();
    rowBytes = 0;
    lookup = null;

    int opcode;

    int verOpcode = in.read();
    int verNumber = in.read();

    if (verOpcode == 0x11 && verNumber == 0x01) versionOne = true;
    else if (verOpcode == 0x00 && verNumber == 0x11) {
      versionOne = false;
      int verNumber2 = in.readShort();

      if (verNumber2 != 0x02ff) {
        throw new FormatException("Invalid PICT file : " + verNumber2);
      }

      // skip over v2 header -- don't need it here
      //in.skipBytes(26);
      in.skipBytes(6);
      int pixelsPerInchX = in.readInt();
      int pixelsPerInchY = in.readInt();
      in.skipBytes(4);
      int y = in.readShort();
      int x = in.readShort();
      if (y > 0) m.sizeY = y;
      if (x > 0) m.sizeX = x;
      in.skipBytes(4);
    }
    else throw new FormatException("Invalid PICT file");

    addGlobalMeta("Version", versionOne ? 1 : 2);

    do {
      if (versionOne) opcode = in.read();
      else {
        // if at odd boundary skip a byte for opcode in PICT v2

        if ((in.getFilePointer() & 0x1L) != 0) {
          in.skipBytes(1);
        }
        if (in.getFilePointer() + 2 >= in.length()) {
          break;
        }
        opcode = in.readShort() & 0xffff;
      }
    }
    while (drivePictDecoder(opcode));

    m.rgb = getSizeC() > 1;
    m.indexed = !isRGB() && lookup != null;

    // The metadata store we're working with.
    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
  }

  // -- Helper methods --

  /** Handles the opcodes in the PICT file. */
  private boolean drivePictDecoder(int opcode)
    throws FormatException, IOException
  {
    LOGGER.debug("drivePictDecoder({}) @ {}", opcode, in.getFilePointer());

    switch (opcode) {
      case PICT_BITSRGN:  // rowBytes must be < 8
      case PICT_PACKBITSRGN: // rowBytes must be < 8
      case PICT_BITSRECT: // rowBytes must be < 8
      case PICT_PACKBITSRECT:
        rowBytes = in.readShort();
        if (versionOne || (rowBytes & 0x8000) == 0) handleBitmap(opcode);
        else handlePixmap(opcode);
        break;
      case PICT_9A:
        handlePixmap(opcode);
        break;
      case PICT_CLIP_RGN:
        int x = in.readShort();
        in.skipBytes(x - 2);
        break;
      case PICT_LONGCOMMENT:
        in.skipBytes(2);
        x = in.readShort();
        in.skipBytes(x);
        break;
      case PICT_END: // end of PICT
        return false;
      case PICT_TYPE_1:
      case PICT_TYPE_2:
        x = in.read();
        in.skipBytes(x);
        break;
      case PICT_JPEG:
        jpegOffsets.add(in.getFilePointer() + 2);
        core.get(0).sizeC = 3;
        core.get(0).rgb = true;
        while ((in.readShort() & 0xffff) != 0xffd9 &&
          in.getFilePointer() < in.length());
        while (in.getFilePointer() < in.length()) {
          while ((in.readShort() & 0xffff) != 0xffd8 &&
            in.getFilePointer() < in.length());
          if (in.getFilePointer() < in.length()) {
            jpegOffsets.add(in.getFilePointer() - 2);
          }
        }
        core.get(0).interleaved = true;
        break;
      default:
        if (opcode < 0) {
          //throw new FormatException("Invalid opcode: " + opcode);
          LOGGER.warn("Invalid opcode: {}", opcode);
        }
    }

    return in.getFilePointer() < in.length();
  }

  private void readImageHeader(int opcode) throws IOException {
    if (opcode == PICT_9A) in.skipBytes(6);
    else rowBytes &= 0x3fff;

    int tlY = in.readShort();
    int tlX = in.readShort();
    int brY = in.readShort();
    int brX = in.readShort();

    if (brX - tlX > 0) core.get(0).sizeX = brX - tlX;
    if (brY - tlY > 0) core.get(0).sizeY = brY - tlY;

    in.skipBytes(18);
  }

  /** Extract the image data in a PICT bitmap structure. */
  private void handleBitmap(int opcode) throws FormatException, IOException {
    readImageHeader(opcode);
    handlePixmap(1, 1);
  }

  /** Extracts the image data in a PICT pixmap structure. */
  private void handlePixmap(int opcode) throws FormatException, IOException {
    readImageHeader(opcode);
    LOGGER.debug("handlePixmap({})", opcode);

    int pixelSize = in.readShort();
    int compCount = in.readShort();
    in.skipBytes(14);

    if (opcode == PICT_9A) {
      // rowBytes doesn't exist, so set it to its logical value
      switch (pixelSize) {
        case 32:
          rowBytes = getSizeX() * compCount;
          break;
        case 16:
          rowBytes = getSizeX() * 2;
          break;
        default:
          throw new FormatException("Sorry, vector data not supported.");
      }
    }
    else {
      // read the lookup table

      in.skipBytes(4);
      int flags = in.readShort();
      int count = in.readShort();

      count++;
      lookup = new byte[3][count];

      for (int i=0; i<count; i++) {
        in.skipBytes(2);
        lookup[0][i] = in.readByte();
        in.skipBytes(1);
        lookup[1][i] = in.readByte();
        in.skipBytes(1);
        lookup[2][i] = in.readByte();
        in.skipBytes(1);
      }
    }

    // skip over two rectangles
    in.skipBytes(18);

    if (opcode == PICT_BITSRGN || opcode == PICT_PACKBITSRGN) in.skipBytes(2);

    handlePixmap(pixelSize, compCount);
  }

  /** Handles the unpacking of the image data. */
  private void handlePixmap(int pixelSize, int compCount)
    throws FormatException, IOException
  {
    LOGGER.debug("handlePixmap({}, {}, {})",
      new Object[] {rowBytes, pixelSize, compCount});
    int rawLen;
    byte[] buf;  // row raw bytes
    byte[] uBuf = null;  // row uncompressed data
    int[] uBufI = null;  // row uncompressed data - 16+ bit pixels
    int bufSize = rowBytes;
    int outBufSize = getSizeX();
    byte[] outBuf = null;  // used to expand pixel data

    boolean compressed = (rowBytes >= 8) || (pixelSize == 32);

    // allocate buffers

    switch (pixelSize) {
      case 32:
        if (!compressed) uBufI = new int[getSizeX()];
        else uBuf = new byte[bufSize];
        break;
      case 16:
        uBufI = new int[getSizeX()];
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
      LOGGER.debug("Pixel data is uncompressed (pixelSize={}).", pixelSize);
      buf = new byte[bufSize];
      for (int row=0; row<getSizeY(); row++) {
        in.read(buf, 0, rowBytes);

        switch (pixelSize) {
          case 16:
            for (int i=0; i<getSizeX(); i++) {
              uBufI[i] = DataTools.bytesToShort(buf, i*2, 2, false);
            }
            strips.add(uBufI);
            buf = null;
            core.get(0).sizeC = 3;
            break;
          case 8:
            strips.add(buf);
            break;
          default: // pixel size < 8
            expandPixels(pixelSize, buf, outBuf, outBuf.length);
            strips.add(outBuf);
            buf = null;
        }
      }
    }
    else {
      LOGGER.debug("Pixel data is compressed (pixelSize={}; compCount={}).",
        pixelSize, compCount);
      buf = new byte[bufSize + 1 + bufSize / 128];
      for (int row=0; row<getSizeY(); row++) {
        if (rowBytes > 250) rawLen = in.readShort();
        else rawLen = in.read();

        if (rawLen > buf.length) rawLen = buf.length;

        if ((in.length() - in.getFilePointer()) <= rawLen) {
          rawLen = (int) (in.length() - in.getFilePointer() - 1);
        }

        if (rawLen < 0) {
          rawLen = 0;
          in.seek(in.length() - 1);
        }

        in.read(buf, 0, rawLen);

        if (pixelSize == 16) {
          uBufI = new int[getSizeX()];
          unpackBits(buf, uBufI);
          strips.add(uBufI);
          core.get(0).sizeC = 3;
        }
        else {
          PackbitsCodec c = new PackbitsCodec();
          CodecOptions options = new CodecOptions();
          options.maxBytes = getSizeX() * 4;
          uBuf = c.decompress(buf, options);
        }

        if (pixelSize < 8) {
          expandPixels(pixelSize, uBuf, outBuf, outBuf.length);
          strips.add(outBuf);
        }
        else if (pixelSize == 8) {
          strips.add(uBuf);
        }
        else if (pixelSize == 24 || pixelSize == 32) {
          byte[] newBuf = null;

          for (int q=0; q<compCount; q++) {
            int offset = q * getSizeX();
            int len = (int) Math.min(getSizeX(), uBuf.length - offset);
            newBuf = new byte[getSizeX()];
            if (offset < uBuf.length) {
              System.arraycopy(uBuf, offset, newBuf, 0, len);
            }
            strips.add(newBuf);
          }
          core.get(0).sizeC = 3;
        }
      }
    }
  }

  /** Expand an array of bytes. */
  private void expandPixels(int bitSize, byte[] ib, byte[] ob, int outLen)
    throws FormatException
  {
    LOGGER.debug("expandPixels({}, {}, {}, {})",
      new Object[] {bitSize, ib.length, ob.length, outLen});
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

    byte v;
    int count = 8 / bitSize; // number of pixels in a byte
    int maskshift = bitSize; // num bits to shift mask
    int pixelshift = 8 - bitSize; // num bits to shift pixel
    int tpixelshift = 0;
    int pixelshiftdelta = bitSize;
    int tmask; // temp mask

    if (bitSize != 1 && bitSize != 2 && bitSize != 4) {
      throw new FormatException("Can only expand 1, 2, and 4 bit values");
    }

    int mask = ((int) Math.pow(2, bitSize) - 1) << (8 - bitSize);

    int i = 0;
    for (int o = 0; o < ob.length; i++) {
      tmask = mask;
      tpixelshift = pixelshift;
      v = ib[i];
      for (int t = 0; t < count && o < ob.length; t++, o++) {
        ob[o] = (byte) (((v & tmask) >>> tpixelshift) & 0xff);
        tmask = (byte) ((tmask & 0xff) >>> maskshift);
        tpixelshift -= pixelshiftdelta;
      }
    }
  }

  /** PackBits variant that outputs an int array. */
  private void unpackBits(byte[] ib, int[] ob) {
    LOGGER.debug("unpackBits(...)");
    int i = 0;
    int b;
    int rep;
    int end;

    for (int o=0; o<ob.length;) {
      if (i+1 < ib.length) {
        b = ib[i++];
        if (b >= 0) {
          end = o + b + 1;
          while (o < end && o < ob.length && (i + 1) < ib.length) {
            ob[o++] = DataTools.bytesToShort(ib, i, 2, false);
            i += 2;
          }
        }
        else if (b != -128) {
          rep = DataTools.bytesToShort(ib, i, 2, false);
          i += 2;
          end = o - b + 1;
          while (o < end && o < ob.length) {
            ob[o++] = rep;
          }
        }
      }
      else o = ob.length;
    }
  }

}
