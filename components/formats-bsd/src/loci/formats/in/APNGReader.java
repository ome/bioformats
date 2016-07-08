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

import java.io.EOFException;
import java.io.InputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.zip.InflaterInputStream;

import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.UnsupportedCompressionException;
import loci.formats.meta.MetadataStore;

/**
 * APNGReader is the file format reader for
 * Animated Portable Network Graphics (APNG) images.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class APNGReader extends FormatReader {

  // -- Constants --

  /** Color types. */
  private static final int GRAYSCALE = 0;
  private static final int TRUE_COLOR = 2;
  private static final int INDEXED = 3;
  private static final int GRAY_ALPHA = 4;
  private static final int TRUE_ALPHA = 6;

  /** Filter types. */
  private static final int NONE = 0;
  private static final int SUB = 1;
  private static final int UP = 2;
  private static final int AVERAGE = 3;
  private static final int PAETH = 4;

  /** Interlacing pass dimensions. */
  private static final int[] PASS_WIDTHS = {1, 1, 2, 2, 4, 4, 8};
  private static final int[] PASS_HEIGHTS = {1, 1, 1, 2, 2, 4, 4};

  // -- Fields --

  private Vector<PNGBlock> blocks;
  private Vector<int[]> frameCoordinates;

  private byte[][] lut;

  private byte[] lastImage;
  private int lastImageIndex = -1;
  private int lastImageRow = -1;

  private int compression;
  private int interlace;
  private int idatCount = 0;

  // -- Constructor --

  /** Constructs a new APNGReader. */
  public APNGReader() {
    super("Animated PNG", "png");
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
    suffixNecessary = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 8;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;

    byte[] signature = new byte[blockLen];
    stream.read(signature);

    if (signature[0] != (byte) 0x89 || signature[1] != 0x50 ||
      signature[2] != 0x4e || signature[3] != 0x47 || signature[4] != 0x0d ||
      signature[5] != 0x0a || signature[6] != 0x1a || signature[7] != 0x0a)
    {
      return false;
    }
    return true;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable(int) */
  @Override
  public byte[][] get8BitLookupTable(int no) {
    FormatTools.assertId(currentId, true, 1);
    return lut;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    if (no == lastImageIndex && lastImage != null && y + h <= lastImageRow) {
      RandomAccessInputStream s = new RandomAccessInputStream(lastImage);
      readPlane(s, x, y, w, h, buf);
      s.close();
      s = null;
      return buf;
    }

    if (no == 0) {
      lastImage = null;

      PNGInputStream stream = new PNGInputStream("IDAT");
      int decodeHeight = y + h;
      if (decodeHeight < getSizeY() && decodeHeight % 8 != 0) {
        decodeHeight += 8 - (decodeHeight % 8);
      }
      lastImage = decode(stream, getSizeX(), decodeHeight);
      stream.close();
      lastImageIndex = 0;
      lastImageRow = y + h;

      RandomAccessInputStream pix = new RandomAccessInputStream(lastImage);
      readPlane(pix, x, y, w, h, buf);
      pix.close();
      pix = null;

      if (y + h < getSizeY()) {
        lastImage = null;
      }
      return buf;
    }


    int[] coords = frameCoordinates.get(no);

    lastImage = openBytes(0);
    lastImageRow = getSizeY();
    PNGInputStream stream = new PNGInputStream("fdAT", no);
    byte[] newImage = decode(stream, coords[2], coords[3]);
    stream.close();

    // paste current image onto first image

    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int len = coords[2] * bpp;
    int plane = getSizeX() * getSizeY() * bpp;
    int newPlane = len * coords[3];
    if (!isInterleaved()) {
      for (int c=0; c<getRGBChannelCount(); c++) {
        for (int row=0; row<coords[3]; row++) {
          System.arraycopy(newImage, c * newPlane + row * len, lastImage,
            c * plane + (coords[1] + row) * getSizeX() * bpp + coords[0] * bpp,
            len);
        }
      }
    }
    else {
      len *= getRGBChannelCount();
      for (int row=0; row<coords[3]; row++) {
        System.arraycopy(newImage, row * len, lastImage,
          (coords[1] + row) * getSizeX() * bpp * getRGBChannelCount() +
          coords[0] * bpp * getRGBChannelCount(), len);
      }
    }

    lastImageIndex = no;

    RandomAccessInputStream pix = new RandomAccessInputStream(lastImage);
    readPlane(pix, x, y, w, h, buf);
    pix.close();
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      lut = null;
      frameCoordinates = null;
      blocks = null;
      lastImage = null;
      lastImageIndex = -1;
      lastImageRow = -1;
      compression = 0;
      interlace = 0;
      idatCount = 0;
    }
  }

  // -- Internal FormatReader methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    CoreMetadata m = core.get(0);

    // check that this is a valid PNG file
    byte[] signature = new byte[8];
    in.read(signature);

    if (signature[0] != (byte) 0x89 || signature[1] != 0x50 ||
      signature[2] != 0x4e || signature[3] != 0x47 || signature[4] != 0x0d ||
      signature[5] != 0x0a || signature[6] != 0x1a || signature[7] != 0x0a)
    {
      throw new FormatException("Invalid PNG signature.");
    }

    // read data chunks - each chunk consists of the following:
    // 1) 32 bit length
    // 2) 4 char type
    // 3) 'length' bytes of data
    // 4) 32 bit CRC

    blocks = new Vector<PNGBlock>();
    frameCoordinates = new Vector<int[]>();

    while (in.getFilePointer() < in.length()) {
      int length = in.readInt();
      String type = in.readString(4);

      PNGBlock block = new PNGBlock();
      block.length = length;
      block.type = type;
      block.offset = in.getFilePointer();
      blocks.add(block);

      if (type.equals("acTL")) {
        // APNG-specific chunk
        m.imageCount = in.readInt();
        int loop = in.readInt();
        addGlobalMeta("Loop count", loop);
      }
      else if (type.equals("fcTL")) {
        in.skipBytes(4);
        int w = in.readInt();
        int h = in.readInt();
        int x = in.readInt();
        int y = in.readInt();
        frameCoordinates.add(new int[] {x, y, w, h});
        in.skipBytes(length - 20);
      }
      else if (type.equals("IDAT")) {
        idatCount++;
      }
      else if (type.equals("PLTE")) {
        // lookup table

        m.indexed = true;
        lut = new byte[3][256];

        for (int i=0; i<length/3; i++) {
          for (int c=0; c<3; c++) {
            lut[c][i] = in.readByte();
          }
        }
      }
      else if (type.equals("IHDR")) {
        m.sizeX = in.readInt();
        m.sizeY = in.readInt();
        m.bitsPerPixel = in.read();
        int colorType = in.read();
        compression = in.read();
        int filter = in.read();
        interlace = in.read();

        if (filter != 0) {
          throw new FormatException("Invalid filter mode: " + filter);
        }

        switch (colorType) {
          case GRAYSCALE:
          case INDEXED:
            m.sizeC = 1;
            break;
          case GRAY_ALPHA:
            m.sizeC = 2;
            break;
          case TRUE_COLOR:
            m.sizeC = 3;
            break;
          case TRUE_ALPHA:
            m.sizeC = 4;
            break;
        }

        m.pixelType = getBitsPerPixel() <= 8 ?
          FormatTools.UINT8 : FormatTools.UINT16;
        m.rgb = getSizeC() > 1;
      }
      else if (type.equals("IEND")) {
        break;
      }
      in.seek(block.offset + length);

      if (in.getFilePointer() < in.length() - 4) {
        in.skipBytes(4); // skip the CRC
      }
    }

    if (m.imageCount == 0) m.imageCount = 1;
    m.sizeZ = 1;
    m.sizeT = getImageCount();

    m.dimensionOrder = "XYCTZ";
    m.interleaved = isRGB();
    m.falseColor = false;

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
  }

  private byte[] decode(PNGInputStream bytes) throws FormatException, IOException {
    return decode(bytes, getSizeX(), getSizeY());
  }

  private byte[] decode(PNGInputStream bytes, int width, int height)
    throws FormatException, IOException
  {
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int rowLen = width * getRGBChannelCount() * bpp;

    if (getBitsPerPixel() < bpp * 8) {
      int div = (bpp * 8) / getBitsPerPixel();
      if (div < rowLen) {
        int originalRowLen = rowLen;
        rowLen /= div;
        if (rowLen * div < originalRowLen) {
          rowLen++;
        }
      }
      else {
        rowLen = 1;
      }
    }

    byte[] image = null;

    if (compression == 0 && interlace == 0) {
      // decompress the image
      byte[] filters = new byte[height];
      image = new byte[rowLen * height];

      InflaterInputStream decompressor = new InflaterInputStream(bytes);
      try {
        int n = 0;
        for (int row=0; row<height; row++) {
          n = 0;
          while (n < 1) {
            n = decompressor.read(filters, row, 1);
          }
          n = 0;
          while (n < rowLen) {
            n += decompressor.read(image, row * rowLen + n, rowLen - n);
          }
        }
      }
      finally {
        decompressor.close();
        decompressor = null;
      }

      // perform any necessary unfiltering

      unfilter(filters, image, width, height);
    }
    else if (compression == 0) {
      // see: http://www.w3.org/TR/PNG/#8Interlace
      int byteCount = 0;

      byte[][] passImages = new byte[7][];

      int nRowBlocks = getSizeY() / 8;
      int nColBlocks = getSizeX() / 8;

      // row and column counts have to be adjusted when the image
      // dimensions are not multiples of 8

      if (8 * nRowBlocks != getSizeY()) {
        nRowBlocks++;
      }
      if (8 * nColBlocks != getSizeX()) {
        nColBlocks++;
      }

      if (nRowBlocks <= 0) {
        nRowBlocks = 1;
      }
      if (nColBlocks <= 0) {
        nColBlocks = 1;
      }

      image = new byte[FormatTools.getPlaneSize(this)];

      InflaterInputStream decompressor = new InflaterInputStream(bytes);
      try {
        for (int i=0; i<passImages.length; i++) {
          int passWidth = PASS_WIDTHS[i] * nColBlocks;
          int passHeight = PASS_HEIGHTS[i] * nRowBlocks;

          // see http://www.libpng.org/pub/png/spec/1.2/PNG-DataRep.html#DR.Interlaced-data-order
          // for clarification of the pass width / pass row adjustments

          if (nColBlocks * 8 != width) {
            int extraCols = getSizeX() - (nColBlocks - 1) * 8;
            switch (extraCols) {
              case 1:
                if (i == 1 || i == 3 || i == 5) {
                  passWidth -= PASS_WIDTHS[i];
                }
                if (i == 2 || i == 4 || i == 6) {
                  passWidth -= (PASS_WIDTHS[i] - 1);
                }
                break;
              case 2:
                if (i == 1 || i == 3) {
                  passWidth -= PASS_WIDTHS[i];
                }
                if (i == 2 || i == 4 || i == 5) {
                  passWidth -= (PASS_WIDTHS[i] - 1);
                }
                if (i == 6) {
                  passWidth -= (PASS_WIDTHS[i] - 2);
                }
                break;
              case 3:
                if (i == 1) {
                  passWidth -= PASS_WIDTHS[i];
                }
                if (i == 2 || i == 3 || i == 5) {
                  passWidth -= (PASS_WIDTHS[i] - 1);
                }
                if (i == 4) {
                  passWidth -= (PASS_WIDTHS[i] - 2);
                }
                if (i == 6) {
                  passWidth -= (PASS_WIDTHS[i] - 3);
                }
                break;
              case 4:
                if (i == 1) {
                  passWidth -= PASS_WIDTHS[i];
                }
                if (i == 2 || i == 3) {
                  passWidth -= (PASS_WIDTHS[i] - 1);
                }
                if (i == 4 || i == 5) {
                  passWidth -= (PASS_WIDTHS[i] - 2);
                }
                if (i == 6) {
                  passWidth -= (PASS_WIDTHS[i] - 4);
                }
                break;
              case 5:
                if (i == 3) {
                  passWidth -= (PASS_WIDTHS[i] - 1);
                }
                if (i == 5) {
                  passWidth -= (PASS_WIDTHS[i] - 2);
                }
                if (i == 4) {
                  passWidth -= (PASS_WIDTHS[i] - 3);
                }
                if (i == 6) {
                  passWidth -= (PASS_WIDTHS[i] - 5);
                }
                break;
              case 6:
                if (i == 3) {
                  passWidth -= (PASS_WIDTHS[i] - 1);
                }
                if (i == 4 || i == 5) {
                  passWidth -= (PASS_WIDTHS[i] - 3);
                }
                if (i == 6) {
                  passWidth -= (PASS_WIDTHS[i] - 6);
                }
                break;
              case 7:
                if (i == 5 || i == 6) {
                  passWidth--;
                }
                break;
            }
          }

          int rowSize = passWidth * bpp * getRGBChannelCount();

          byte[] filters = new byte[passHeight];
          passImages[i] = new byte[rowSize * passHeight];

          for (int row=0; row<passHeight; row++) {
            if (passWidth == 0) {
              continue;
            }
            if (nRowBlocks * 8 != getSizeY() && row >= PASS_HEIGHTS[i] * (nRowBlocks - 1)) {
              int extraRows = getSizeY() - (nRowBlocks - 1) * 8;
              switch (extraRows) {
                case 1:
                  if (i == 2 || i == 4 || i == 6) {
                    continue;
                  }
                  if ((i == 3 || i == 5) && (row % PASS_HEIGHTS[i]) > 0) {
                    continue;
                  }
                  break;
                case 2:
                  if (i == 4 || i == 2) {
                    continue;
                  }
                  if ((i == 3 || i == 5 || i == 6) && (row % PASS_HEIGHTS[i]) > 0) {
                    continue;
                  }
                  break;
                case 3:
                  if (i == 2) {
                    continue;
                  }
                  if ((i == 3 || i == 4 || i == 6) && (row % PASS_HEIGHTS[i]) > 0) {
                    continue;
                  }
                  if (i == 5 && (row % PASS_HEIGHTS[i] > 1)) {
                    continue;
                  }
                  break;
                case 4:
                  if (i == 2) {
                    continue;
                  }
                  if ((i == 3 || i == 4) && (row % PASS_HEIGHTS[i]) > 0) {
                    continue;
                  }
                  if ((i == 5 || i == 6) && (row % PASS_HEIGHTS[i]) > 1) {
                    continue;
                  }
                  break;
                case 5:
                  if ((i == 4) && (row % PASS_HEIGHTS[i]) > 0) {
                    continue;
                  }
                  if ((i == 6) && (row % PASS_HEIGHTS[i]) > 1) {
                    continue;
                  }
                  if ((i == 5) && (row % PASS_HEIGHTS[i]) > 2) {
                    continue;
                  }
                  break;
                case 6:
                  if ((i == 4) && (row % PASS_HEIGHTS[i]) > 0) {
                    continue;
                  }
                  if ((i == 5 || i == 6) && (row % PASS_HEIGHTS[i]) > 2) {
                    continue;
                  }
                  break;
                case 7:
                  if (i == 6 && (row % PASS_HEIGHTS[i]) > 2) {
                    continue;
                  }
                  break;
              }
            }
            int n = 0;
            while (n < 1) {
              n = decompressor.read(filters, row, 1);
            }
            n = 0;
            while (n < rowSize) {
              n += decompressor.read(passImages[i],
                row * rowSize + n, rowSize - n);
            }
          }

          unfilter(filters, passImages[i], passWidth, passHeight);
        }
      }
      finally {
        decompressor.close();
        decompressor = null;
      }

      int chunk = bpp * getRGBChannelCount();
      int[] passOffset = new int[7];

      for (int row=0; row<8 * (height / 8); row++) {
        int rowOffset = row * width * chunk;
        for (int col=0; col<width; col++) {
          int blockRow = row % 8;
          int blockCol = col % 8;

          int pass = -1;

          if ((blockRow % 2) == 1) {
            pass = 6;
          }
          else if (blockRow == 0 || blockRow == 4) {
            if ((blockCol % 2) == 1) {
              pass = 5;
            }
            else if (blockCol == 0) {
              pass = blockRow == 0 ? 0 : 2;
            }
            else if (blockCol == 4) {
              pass = blockRow == 0 ? 1 : 2;
            }
            else {
              pass = 3;
            }
          }
          else {
            pass = 4 + (blockCol % 2);
          }

          int colOffset = col * chunk;
          for (int c=0; c<chunk; c++) {
            if (passOffset[pass] < passImages[pass].length) {
              image[rowOffset + colOffset + c] =
                passImages[pass][passOffset[pass]++];
            }
          }
        }
      }
    }
    else {
      throw new UnsupportedCompressionException(
        "Compression type " + compression + " not supported");
    }

    // de-interleave

    if (getBitsPerPixel() < 8) {
      byte[] expandedImage = new byte[FormatTools.getPlaneSize(this)];
      RandomAccessInputStream bits = new RandomAccessInputStream(image);

      int skipBits = rowLen * 8 - getSizeX() * getBitsPerPixel();
      for (int row=0; row<getSizeY(); row++) {
        for (int col=0; col<getSizeX(); col++) {
          int index = row * getSizeX() + col;
          expandedImage[index] =
            (byte) (bits.readBits(getBitsPerPixel()) & 0xff);
        }
        bits.skipBits(skipBits);
      }
      bits.close();
      bits = null;

      image = expandedImage;
    }

    return image;
  }

  /** See http://www.w3.org/TR/PNG/#9Filters. */
  private void unfilter(byte[] filters, byte[] image, int width, int height)
    throws FormatException
  {
    int bpp =
      getRGBChannelCount() * FormatTools.getBytesPerPixel(getPixelType());
    int rowLen = width * bpp;
    for (int row=0; row<height; row++) {
      int filter = filters[row];

      if (filter == NONE) {
        continue;
      }

      for (int col=0; col<rowLen; col++) {
        int q = row * rowLen + col;

        int xx = image[q] & 0xff;
        int a = col >= bpp ? image[q - bpp] & 0xff : 0;
        int b = row > 0 ? image[q - bpp * width] & 0xff : 0;
        int c = row > 0 && col >= bpp ?
          image[q - bpp * (width + 1)] & 0xff : 0;

        switch (filter) {
          case SUB:
            image[q] = (byte) ((xx + a) & 0xff);
            break;
          case UP:
            image[q] = (byte) ((xx + b) & 0xff);
            break;
          case AVERAGE:
            image[q] = (byte) ((xx + ((int) Math.floor(a + b) / 2)) & 0xff);
            break;
          case PAETH:
            int p = a + b - c;
            int pa = (int) Math.abs(p - a);
            int pb = (int) Math.abs(p - b);
            int pc = (int) Math.abs(p - c);

            if (pa <= pb && pa <= pc) {
              image[q] = (byte) ((xx + a) & 0xff);
            }
            else if (pb <= pc) {
              image[q] = (byte) ((xx + b) & 0xff);
            }
            else {
              image[q] = (byte) ((xx + c) & 0xff);
            }
            break;
          default:
            throw new FormatException("Unknown filter: " + filter);
        }
      }
    }
  }

  // -- Helper class --

  class PNGBlock {
    public long offset;
    public int length;
    public String type;
  }

  /**
   * InputStream implementation that stitches together IDAT blocks into
   * a seamless zlib stream.  This allows us to decompress the image data
   * without reading the entire compressed stream into a byte array.
   */
  class PNGInputStream extends InputStream {
    private int currentBlock = -1;
    private int blockPointer = 0;
    private int blockLength = 0;
    private String blockType;
    private int imageNumber;
    private int fctlCount = 0;
    private boolean fdatValid = false;

    public PNGInputStream(String blockType) throws IOException {
      this(blockType, 0);
    }

    public PNGInputStream(String blockType, int imageNumber) throws IOException {
      this.imageNumber = imageNumber;
      this.blockType = blockType;
      fctlCount = 0;
      fdatValid = false;
      advanceBlock();
    }

    @Override
    public int available() throws IOException {
      if (blockPointer == blockLength) {
        advanceBlock();
      }
      if (currentBlock < 0 || in.getFilePointer() == in.length()) {
        return -1;
      }
      return (int) Math.min(blockLength - blockPointer, in.length() - in.getFilePointer());
    }

    @Override
    public int read() throws IOException {
      if (blockPointer < blockLength) {
        blockPointer++;
        return in.read();
      }
      advanceBlock();
      if (currentBlock < 0) {
        throw new EOFException();
      }
      blockPointer++;
      return in.read();
    }

    public byte readByte() throws IOException {
      if (blockPointer < blockLength) {
        blockPointer++;
        return in.readByte();
      }
      advanceBlock();
      if (currentBlock < 0) {
        throw new EOFException();
      }
      blockPointer++;
      return in.readByte();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
      int read = 0;
      for (int i=0; i<len; i++) {
        if (available() > 0) {
          b[off + i] = readByte();
          read++;
        }
        else {
          break;
        }
      }
      return read;
    }

    private void advanceBlock() throws IOException {
      if (currentBlock < blocks.size() - 1) {
        while (currentBlock < blocks.size()) {
          currentBlock++;
          if (currentBlock == blocks.size()) {
            currentBlock = -1;
            break;
          }
          if (blockType.equals("fdAT") && blocks.get(currentBlock).type.equals("fcTL")) {
            fdatValid = fctlCount == imageNumber;
            fctlCount++;
            if (fctlCount > imageNumber + 1) {
              currentBlock = -1;
              break;
            }
          }
          else if (blockType.equals(blocks.get(currentBlock).type)) {
            if (fdatValid || !blockType.equals("fdAT")) {
              break;
            }
          }
        }
        if (currentBlock >= 0) {
          blockPointer = 0;
          blockLength = blocks.get(currentBlock).length;
          in.seek(blocks.get(currentBlock).offset);
          if (blocks.get(currentBlock).type.equals("fdAT")) {
            blockLength -= 4;
            in.skipBytes(4);
          }
        }
      }
      else {
        currentBlock = -1;
        blockPointer = 0;
        blockLength = 0;
      }
    }

  }

}
