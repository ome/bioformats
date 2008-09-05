//
// GIFReader.java
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

import java.io.*;
import java.util.Vector;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * GIFReader is the file format reader for Graphics Interchange Format
 * (GIF) files.  Much of this code was adapted from the Animated GIF Reader
 * plugin for ImageJ (http://rsb.info.nih.gov/ij).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/GIFReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/GIFReader.java">SVN</a></dd></dl>
 */
public class GIFReader extends FormatReader {

  // -- Constants --

  /** Maximum buffer size. */
  private static final int MAX_STACK_SIZE = 4096;

  // -- Fields --

  /** Global color table used. */
  private boolean gctFlag;

  /** Size of global color table. */
  private int gctSize;

  /** Global color table. */
  private int[] gct;

  /** Local color table. */
  private int[] lct;

  /** Active color table. */
  private int[] act;

  /** Local color table flag. */
  private boolean lctFlag;

  /** Interlace flag. */
  private boolean interlace;

  /** Local color table size. */
  private int lctSize;

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

  private Vector images;
  private Vector colorTables;

  // -- Constructor --

  /** Constructs a new GIF reader. */
  public GIFReader() {
    super("Graphics Interchange Format", "gif");
    blockCheckLen = 6;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessStream) */
  public boolean isThisType(RandomAccessStream stream) throws IOException {
    if (!FormatTools.validStream(stream, blockCheckLen, false)) return false;
    return stream.readString(blockCheckLen).startsWith("GIF");
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
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
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length, w, h);

    act = (int[]) colorTables.get(no);

    byte[] b = (byte[]) images.get(no);
    if (no > 0 && transparency) {
      byte[] prev = (byte[]) images.get(no - 1);
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

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    gctFlag = lctFlag = interlace = transparency = false;
    gctSize = lctSize = ix = iy = iw = ih = blockSize = 0;
    dispose = lastDispose = transIndex = 0;
    gct = lct = act;
    prefix = null;
    suffix = pixelStack = pixels = null;
    images = colorTables = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("GIFReader.initFile(" + id + ")");
    super.initFile(id);

    status("Verifying GIF format");

    in = new RandomAccessStream(id);
    in.order(true);
    images = new Vector();
    colorTables = new Vector();

    String ident = in.readString(6);

    if (!ident.startsWith("GIF")) {
      throw new FormatException("Not a valid GIF file.");
    }

    status("Reading dimensions");

    core[0].sizeX = in.readShort();
    core[0].sizeY = in.readShort();

    int packed = in.read() & 0xff;
    gctFlag = (packed & 0x80) != 0;
    gctSize = 2 << (packed & 7);
    in.skipBytes(1);
    in.skipBytes(1);

    if (gctFlag) {
      int nbytes = 3 * gctSize;
      byte[] c = new byte[nbytes];
      in.read(c);

      gct = new int[256];
      int i = 0;
      int j = 0;
      int r, g, b;
      while (i < gctSize) {
        r = c[j++] & 0xff;
        g = c[j++] & 0xff;
        b = c[j++] & 0xff;
        gct[i++] = 0xff000000 | (r << 16) | (g << 8) | b;
      }
    }

    status("Reading data blocks");

    boolean done = false;
    while (!done) {
      int code = in.read() & 0xff;
      switch (code) {
        case 0x2c: // image separator:
          ix = in.readShort();
          iy = in.readShort();
          iw = in.readShort();
          ih = in.readShort();

          packed = in.read();
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
              int r = c[j++] & 0xff;
              int g = c[j++] & 0xff;
              int b = c[j++] & 0xff;
              lct[i++] = 0xff000000 | (r << 16) + (g << 8) | b;
            }

            act = lct;
          }
          else {
            act = gct;
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

          core[0].imageCount++;

          if (transparency) act[transIndex] = save;

          lastDispose = dispose;
          lct = null;

          break;
        case 0x21: // extension
          code = in.read() & 0xff;
          switch (code) {
            case 0xf9: // graphics control extension
              in.skipBytes(1);
              packed = in.read() & 0xff;
              dispose = (packed & 0x1c) >> 1;
              transparency = (packed & 1) != 0;
              in.skipBytes(2);
              transIndex = in.read() & 0xff;
              in.skipBytes(1);
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

    core[0].sizeZ = 1;
    core[0].sizeC = 1;
    core[0].sizeT = getImageCount();
    core[0].dimensionOrder = "XYCTZ";
    core[0].rgb = false;
    core[0].littleEndian = true;
    core[0].interleaved = true;
    core[0].metadataComplete = true;
    core[0].indexed = true;
    core[0].falseColor = false;

    // populate metadata store

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    store.setImageName("", 0);
    MetadataTools.setDefaultCreationDate(store, id, 0);
    core[0].pixelType = FormatTools.UINT8;
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

    dataSize = in.read() & 0xff;

    clear = 1 << dataSize;
    eoi = clear + 1;
    available = clear + 2;
    oldCode = nullCode;
    codeSize = dataSize + 1;
    codeMask = (1 << codeSize) - 1;
    for (code=0; code<clear; code++) {
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
    byte[] dest = new byte[getSizeX() * getSizeY()];
    int lastImage = -1;

    // fill in starting image contents based on last image's dispose code
    if (lastDispose > 0) {
      if (lastDispose == 3) { // use image before last
        int n = getImageCount() - 2;
        if (n > 0) lastImage = n - 1;
      }

      if (lastImage != -1) {
        byte[] prev = (byte[]) images.get(lastImage);
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
          int index = ((int) pixels[sx++]) & 0xff;
          dest[dx++] = (byte) index;
        }
      }
    }
    colorTables.add(act);
    images.add(dest);
  }

}
