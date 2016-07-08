/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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
import java.util.Arrays;
import java.util.Vector;

import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

/**
 * GIFReader is the file format reader for Graphics Interchange Format
 * (GIF) files.  Much of this code was adapted from the Animated GIF Reader
 * plugin for ImageJ (http://rsb.info.nih.gov/ij).
 */
public class GIFReader extends FormatReader {

  // -- Constants --

  public static final String GIF_MAGIC_STRING = "GIF";

  /** Maximum buffer size. */
  private static final int MAX_STACK_SIZE = 4096;

  private static final int IMAGE_SEPARATOR = 0x2c;
  private static final int EXTENSION = 0x21;
  private static final int END = 0x3b;
  private static final int GRAPHICS = 0xf9;

  // -- Fields --

  /** Global color table. */
  private int[] gct;

  /** Active color table. */
  private int[] act;

  /** Interlace flag. */
  private boolean interlace;

  /** Current image rectangle. */
  private int ix, iy, iw, ih;

  /** Current data block. */
  private byte[] dBlock = new byte[256];

  /** Block size. */
  private int blockSize = 0;

  private int dispose = 0;
  private int lastDispose = 0;

  /** Use transparent color. */
  private boolean transparency = false;

  /** Transparent color index. */
  private int transIndex;

  // LZW working arrays
  private short[] prefix;
  private byte[] suffix;
  private byte[] pixelStack;
  private byte[] pixels;

  private Vector<byte[]> images;
  private Vector<int[]> colorTables;

  // -- Constructor --

  /** Constructs a new GIF reader. */
  public GIFReader() {
    super("Graphics Interchange Format", "gif");
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = GIF_MAGIC_STRING.length();
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readString(blockLen).startsWith(GIF_MAGIC_STRING);
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable(int) */
  @Override
  public byte[][] get8BitLookupTable(int no) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    byte[][] table = new byte[3][act.length];
    for (int i=0; i<act.length; i++) {
      table[0][i] = (byte) ((act[i] >> 16) & 0xff);
      table[1][i] = (byte) ((act[i] >> 8) & 0xff);
      table[2][i] = (byte) (act[i] & 0xff);
    }
    return table;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    act = colorTables.get(no);

    byte[] b = images.get(no);
    if (no > 0 && transparency) {
      byte[] prev = images.get(no - 1);
      int idx = transIndex;
      if (idx >= 127) idx = 0;
      for (int i=0; i<b.length; i++) {
        if ((act[b[i] & 0xff] & 0xffffff) == idx) {
          b[i] = prev[i];
        }
      }
      images.setElementAt(b, no);
    }

    for (int row=0; row<h; row++) {
      System.arraycopy(b, (row + y) * getSizeX() + x, buf, row*w, w);
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      interlace = transparency = false;
      ix = iy = iw = ih = blockSize = 0;
      dispose = lastDispose = transIndex = 0;
      gct = act;
      prefix = null;
      suffix = pixelStack = pixels = null;
      images = null;
      colorTables = null;
      Arrays.fill(dBlock, (byte) 0);
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    LOGGER.info("Verifying GIF format");

    in = new RandomAccessInputStream(id);
    in.order(true);
    images = new Vector<byte[]>();
    colorTables = new Vector<int[]>();

    String ident = in.readString(6);

    if (!ident.startsWith(GIF_MAGIC_STRING)) {
      throw new FormatException("Not a valid GIF file.");
    }

    LOGGER.info("Reading dimensions");

    CoreMetadata m = core.get(0);

    m.sizeX = in.readShort();
    m.sizeY = in.readShort();

    int packed = in.read() & 0xff;
    boolean gctFlag = (packed & 0x80) != 0;
    int gctSize = 2 << (packed & 7);
    in.skipBytes(2);
    addGlobalMeta("Global lookup table size", gctSize);

    if (gctFlag) {
      gct = readLut(gctSize);
    }

    LOGGER.info("Reading data blocks");

    boolean done = false;
    while (!done) {
      int code = in.read() & 0xff;
      switch (code) {
        case IMAGE_SEPARATOR:
          readImageBlock();
          break;
        case EXTENSION:
          code = in.read() & 0xff;
          switch (code) {
            case GRAPHICS:
              in.skipBytes(1);
              packed = in.read() & 0xff;
              dispose = (packed & 0x1c) >> 1;
              transparency = (packed & 1) != 0;
              in.skipBytes(2);
              transIndex = in.read() & 0xff;
              in.skipBytes(1);
              break;
            default:
              if (readBlock() == -1) {
                done = true;
                break;
              }
              skipBlocks();
          }
          break;
        case END:
          done = true;
          break;
      }
    }

    act = colorTables.get(0);

    LOGGER.info("Populating metadata");

    addGlobalMeta("Use transparency", transparency);
    addGlobalMeta("Transparency index", transIndex);
    addGlobalMeta("Interlace", interlace);
    addGlobalMeta("Block size", blockSize);

    m.sizeZ = 1;
    m.sizeC = 1;
    m.sizeT = getImageCount();
    m.dimensionOrder = "XYCTZ";
    m.rgb = false;
    m.littleEndian = true;
    m.interleaved = true;
    m.metadataComplete = true;
    m.indexed = true;
    m.falseColor = false;
    m.pixelType = FormatTools.UINT8;

    // populate metadata store

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
  }

  // -- Helper methods --

  /** Reads the next variable length block. */
  private int readBlock() throws IOException {
    if (in.getFilePointer() == in.length()) return -1;
    blockSize = in.read() & 0xff;
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
      catch (IOException e) {
        LOGGER.trace("Truncated block", e);
      }
    }
    return n;
  }

  /** Decodes LZW image data into a pixel array.  Adapted from ImageMagick. */
  private void decodeImageData() throws IOException {
    int nullCode = -1;
    int npix = iw * ih;

    if (pixels == null || pixels.length < npix) pixels = new byte[npix];

    if (prefix == null) prefix = new short[MAX_STACK_SIZE];
    if (suffix == null) suffix = new byte[MAX_STACK_SIZE];
    if (pixelStack == null) pixelStack = new byte[MAX_STACK_SIZE + 1];

    // initialize GIF data stream decoder

    int dataSize = in.read() & 0xff;

    int clear = 1 << dataSize;
    int eoi = clear + 1;
    int available = clear + 2;
    int oldCode = nullCode;
    int codeSize = dataSize + 1;
    int codeMask = (1 << codeSize) - 1;
    int code = 0, inCode = 0;
    for (code=0; code<clear; code++) {
      prefix[code] = 0;
      suffix[code] = (byte) code;
    }

    // decode GIF pixel stream

    int datum = 0, first = 0, top = 0, pi = 0, bi = 0, bits = 0, count = 0;
    int i = 0;

    for (i=0; i<npix;) {
      if (top == 0) {
        if (bits < codeSize) {
          if (count == 0) {
            count = readBlock();
            if (count <= 0) break;
            bi = 0;
          }
          datum += (dBlock[bi] & 0xff) << bits;
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
        first = suffix[code] & 0xff;

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
    byte[] dest = new byte[getSizeX() * getSizeY()];
    int lastImage = -1;

    // fill in starting image contents based on last image's dispose code
    if (lastDispose > 0) {
      if (lastDispose == 3) { // use image before last
        int n = getImageCount() - 2;
        if (n > 0) lastImage = n - 1;
      }

      if (lastImage != -1) {
        byte[] prev = images.get(lastImage);
        System.arraycopy(prev, 0, dest, 0, getSizeX() * getSizeY());
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
      if (line < getSizeY()) {
        int k = line * getSizeX();
        int dx = k + ix; // start of line in dest
        int dlim = dx + iw; // end of dest line
        if ((k + getSizeX()) < dlim) dlim = k + getSizeX();
        int sx = i * iw; // start of line in source
        while (dx < dlim) {
          // map color and insert in destination
          int index = pixels[sx++] & 0xff;
          dest[dx++] = (byte) index;
        }
      }
    }
    colorTables.add(act);
    images.add(dest);
  }

  private void skipBlocks() throws IOException {
    int check = 0;
    do { check = readBlock(); }
    while (blockSize > 0 && check != -1);
  }

  private void readImageBlock() throws FormatException, IOException {
    ix = in.readShort();
    iy = in.readShort();
    iw = in.readShort();
    ih = in.readShort();

    int packed = in.read();
    boolean lctFlag = (packed & 0x80) != 0;
    interlace = (packed & 0x40) != 0;
    int lctSize = 2 << (packed & 7);

    act = lctFlag ? readLut(lctSize) : gct;

    int save = 0;

    if (transparency) {
      save = act[transIndex];
      act[transIndex] = 0;
    }

    if (act == null) throw new FormatException("Color table not found.");

    decodeImageData();
    skipBlocks();

    core.get(0).imageCount++;

    if (transparency) act[transIndex] = save;

    lastDispose = dispose;
  }

  /** Read a color lookup table of the specified size. */
  private int[] readLut(int size) throws FormatException {
    int nbytes = 3 * size;
    byte[] c = new byte[nbytes];
    int n = 0;
    try { n = in.read(c); }
    catch (IOException e) { }

    if (n < nbytes) {
      throw new FormatException("Color table not found");
    }

    int[] lut = new int[256];
    int j = 0;
    for (int i=0; i<size; i++) {
      int r = c[j++] & 0xff;
      int g = c[j++] & 0xff;
      int b = c[j++] & 0xff;
      lut[i] = 0xff000000 | (r << 16) | (g << 8) | b;
    }
    return lut;
  }

}
