//
// PictReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
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
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

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
  protected short[][] lookup;

  /** Helper reader in case this one fails. */
  protected LegacyQTTools qtTools = new LegacyQTTools();

  private boolean legacy = false;

  // -- Constructor --

  /** Constructs a new PICT reader. */
  public PictReader() { super("PICT", new String[] {"pict", "pct"}); }

  // -- PictReader API methods --

  /** Control whether or not legacy reader (QT Java) is used. */
  public void setLegacy(boolean legacy) {
    this.legacy = legacy;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    if (legacy || strips.size() == 0) {
      in.seek(512);
      byte[] pix = new byte[(int) (in.length() - in.getFilePointer())];
      in.read(pix);
      byte[][] b = ImageTools.getPixelBytes(ImageTools.makeBuffered(
        qtTools.pictToImage(pix)), core.littleEndian[0]);
      for (int i=0; i<b.length; i++) {
        System.arraycopy(b[i], 0, buf, i*b[i].length, b[i].length);
      }
      return buf;
    }

    // combine everything in the strips Vector

    if ((core.sizeY[0]*4 < strips.size()) && (((strips.size() / 3) %
      core.sizeY[0]) != 0))
    {
      core.sizeY[0] = strips.size();
    }

    int plane = core.sizeX[0] * core.sizeY[0];

    if (lookup != null) {
      // 8 bit data

      byte[] row;

      plane *= 2;

      for (int i=0; i<core.sizeY[0]; i++) {
        row = (byte[]) strips.get(i);

        for (int j=0; j<row.length; j++) {
          if (j < core.sizeX[0]) {
            int ndx = row[j];
            if (ndx < 0) ndx += lookup[0].length;
            ndx = ndx % lookup[0].length;

            int outIndex = i*core.sizeX[0]*2 + j*2;

            if (2*plane + outIndex + 2 < buf.length) {
              DataTools.unpackShort(lookup[0][ndx], buf, outIndex, false);
              DataTools.unpackShort(lookup[1][ndx], buf, plane + outIndex,
                false);
              DataTools.unpackShort(lookup[2][ndx], buf, 2*plane + outIndex,
                false);
            }
          }
          else j = row.length;
        }
      }
    }
    else if (core.sizeY[0]*3 == strips.size() ||
      core.sizeY[0]*4 == strips.size())
    {
      // 24 or 32 bit data

      int nc = strips.size() / core.sizeY[0];

      for (int i=0; i<core.sizeY[0]; i++) {
        byte[] c0 = (byte[]) strips.get(i * nc + nc - 3);
        byte[] c1 = (byte[]) strips.get(i * nc + nc - 2);
        byte[] c2 = (byte[]) strips.get(i * nc + nc - 1);
        int baseOffset = i * core.sizeX[0];
        System.arraycopy(c0, 0, buf, baseOffset, core.sizeX[0]);
        System.arraycopy(c1, 0, buf, plane + baseOffset, core.sizeX[0]);
        System.arraycopy(c2, 0, buf, 2*plane + baseOffset, core.sizeX[0]);
      }
    }
    else {
      // RGB value is packed into a single short: xRRR RRGG GGGB BBBB
      for (int i=0; i<core.sizeY[0]; i++) {
        int[] row = (int[]) strips.get(i);

        for (int j=0; j<row.length; j++) {
          int base = i * row.length + j;
          buf[base] = (byte) ((row[j] & 0x7c00) >> 10);
          buf[plane + base] = (byte) ((row[j] & 0x3e0) >> 5);
          buf[2 * plane + base] = (byte) (row[j] & 0x1f);
        }
      }
    }
    return buf;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return block.length >= 528;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("PictReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    status("Populating metadata");

    core.littleEndian[0] = false;

    in.seek(518);

    core.sizeY[0] = in.readShort();
    core.sizeX[0] = in.readShort();
    core.sizeZ[0] = 1;
    core.sizeC[0] = 3;
    core.sizeT[0] = 1;
    core.currentOrder[0] = "XYCZT";
    core.rgb[0] = true;
    core.imageCount[0] = 1;
    core.indexed[0] = false;
    core.falseColor[0] = false;
    core.metadataComplete[0] = true;
    core.interleaved[0] = false;

    strips = new Vector();
    rowBytes = 0;
    versionOne = false;
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
      in.skipBytes(12);
    }
    else throw new FormatException("Invalid PICT file");

    do {
      if (versionOne) opcode = in.read();
      else {
        // if at odd boundary skip a byte for opcode in PICT v2

        if ((in.getFilePointer() & 0x1L) != 0) {
          in.skipBytes(1);
        }
        opcode = in.readShort();
      }
    }
    while (drivePictDecoder(opcode));

    core.pixelType[0] = lookup != null ? FormatTools.UINT16 : FormatTools.UINT8;

    // The metadata store we're working with.
    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    store.setImageName("", 0);
    store.setImageCreationDate(
      DataTools.convertDate(System.currentTimeMillis(), DataTools.UNIX), 0);

    MetadataTools.populatePixels(store, this);
  }

  // -- Helper methods --

  /** Handles the opcodes in the PICT file. */
  private boolean drivePictDecoder(int opcode)
    throws FormatException, IOException
  {
    if (debug) debug("drivePictDecoder(" + opcode + ") @ " + in.getFilePointer());

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
      default:
        if (opcode < 0) {
          throw new FormatException("Invalid opcode: " + opcode);
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

    core.sizeX[0] = brX - tlX;
    core.sizeY[0] = brY - tlY;

    in.skipBytes(18);
  }

  /** Extract the image data in a PICT bitmap structure. */
  private void handleBitmap(int opcode)
    throws FormatException, IOException
  {
    readImageHeader(opcode);
    handlePixmap(rowBytes, 1, 1);
  }

  /** Extracts the image data in a PICT pixmap structure. */
  private void handlePixmap(int opcode)
    throws FormatException, IOException
  {
    readImageHeader(opcode);
    if (debug) debug("handlePixmap(" + opcode + ")");

    int pixelSize = in.readShort();
    int compCount = in.readShort();
    in.skipBytes(14);

    if (opcode == PICT_9A) {
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
      // read the lookup table

      in.skipBytes(4);
      int flags = in.readShort();
      int count = in.readShort();

      count++;
      lookup = new short[3][count];

      for (int i=0; i<count; i++) {
        int index = in.readShort();
        if ((flags & 0x8000) != 0) index = i;
        lookup[0][index] = in.readShort();
        lookup[1][index] = in.readShort();
        lookup[2][index] = in.readShort();
      }
    }

    // skip over two rectangles
    in.skipBytes(18);

    if (opcode == PICT_BITSRGN || opcode == PICT_PACKBITSRGN) in.skipBytes(2);

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
        in.read(buf, 0, rBytes);

        switch (pixelSize) {
          case 16:
            for (int i=0; i<core.sizeX[0]; i++) {
              uBufI[i] = DataTools.bytesToShort(buf, i*2, 2, false);
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
        if (rBytes > 250) rawLen = in.readShort();
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
          uBufI = new int[core.sizeX[0]];
          unpackBits(buf, uBufI);
          strips.add(uBufI);
        }
        else {
          PackbitsCodec c = new PackbitsCodec();
          uBuf = c.decompress(buf, new Integer(core.sizeX[0] * 4));
        }

        if (pixelSize < 8) {
          expandPixels(pixelSize, uBuf, outBuf, outBuf.length);
          strips.add(outBuf);
        }
        else if (pixelSize == 8) strips.add(uBuf);
        else if (pixelSize == 24 || pixelSize == 32) {
          byte[] newBuf = null;

          for (int q=0; q<compCount; q++) {
            int offset = q * core.sizeX[0];
            int len = (int) Math.min(core.sizeX[0], uBuf.length - offset);
            newBuf = new byte[core.sizeX[0]];
            if (offset < uBuf.length) {
              System.arraycopy(uBuf, offset, newBuf, 0, len);
            }
            strips.add(newBuf);
          }
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
    if (debug) debug("unpackBits(" + ib + ", " + ob + ")");
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
