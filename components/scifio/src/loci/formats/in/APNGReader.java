/*
 * #%L
 * OME SCIFIO package for reading and converting scientific file formats.
 * %%
 * Copyright (C) 2005 - 2013 Open Microscopy Environment:
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package loci.formats.in;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/APNGReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/APNGReader.java;hb=HEAD">Gitweb</a></dd></dl>
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

  // -- Fields --

  private Vector<PNGBlock> blocks;
  private Vector<int[]> frameCoordinates;

  private byte[][] lut;

  private byte[] lastImage;
  private int lastImageIndex = -1;

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

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() {
    FormatTools.assertId(currentId, true, 1);
    return lut;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    if (no == lastImageIndex && lastImage != null) {
      RandomAccessInputStream s = new RandomAccessInputStream(lastImage);
      readPlane(s, x, y, w, h, buf);
      s.close();
      return buf;
    }

    if (no == 0) {
      ByteArrayOutputStream s = new ByteArrayOutputStream();
      int readIDATs = 0;

      for (PNGBlock block : blocks) {
        if (block.type.equals("IDAT")) {
          in.seek(block.offset);
          byte[] tmp = new byte[block.length];
          in.read(tmp);
          s.write(tmp);
          tmp = null;

          readIDATs++;
        }
        if (readIDATs == idatCount) {
          break;
        }
      }

      s.close();

      lastImage = decode(s.toByteArray());
      lastImageIndex = 0;

      RandomAccessInputStream pix = new RandomAccessInputStream(lastImage);
      readPlane(pix, x, y, w, h, buf);
      pix.close();
      return buf;
    }

    ByteArrayOutputStream s = new ByteArrayOutputStream();
    int readIDATs = 0;

    for (PNGBlock block : blocks) {
      if (block.type.equals("IDAT")) {
        in.seek(block.offset);
        byte[] tmp = new byte[block.length];
        in.read(tmp);
        s.write(tmp);
        tmp = null;

        readIDATs++;
      }
      if (readIDATs == idatCount) {
        break;
      }
    }

    boolean fdatValid = false;
    int fctlCount = 0;
    int[] coords = frameCoordinates.get(no);

    s = new ByteArrayOutputStream();

    for (PNGBlock block : blocks) {
      if (block.type.equals("fcTL")) {
        fdatValid = fctlCount == no;
        fctlCount++;
      }
      else if (block.type.equals("fdAT")) {
        in.seek(block.offset + 4);
        if (fdatValid) {
          byte[] tmp = new byte[block.length - 4];
          in.read(tmp);
          s.write(tmp);
          tmp = null;
        }
      }
    }

    s.close();
    lastImage = openBytes(0);
    byte[] newImage = decode(s.toByteArray(), coords[2], coords[3]);

    // paste current image onto first image

    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int len = coords[2] * bpp;
    int plane = getSizeX() * getSizeY() * bpp;
    int newPlane = len * coords[3];
    for (int c=0; c<getRGBChannelCount(); c++) {
      for (int row=0; row<coords[3]; row++) {
        System.arraycopy(newImage, c * newPlane + row * len, lastImage,
          c * plane + (coords[1] + row) * getSizeX() * bpp + coords[0] * bpp,
          len);
      }
    }

    lastImageIndex = no;

    RandomAccessInputStream pix = new RandomAccessInputStream(lastImage);
    readPlane(pix, x, y, w, h, buf);
    pix.close();
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      lut = null;
      frameCoordinates = null;
      blocks = null;
      lastImage = null;
      lastImageIndex = -1;
    }
  }

  // -- Internal FormatReader methods --

  /* @see loci.formats.FormatReader#initFile(String) */
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
    m.interleaved = false;
    m.falseColor = false;

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
  }

  private byte[] decode(byte[] bytes) throws FormatException, IOException {
    return decode(bytes, getSizeX(), getSizeY());
  }

  private byte[] decode(byte[] bytes, int width, int height)
    throws FormatException, IOException
  {
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int rowLen = width * getRGBChannelCount() * bpp;

    byte[] image = null;

    if (compression == 0 && interlace == 0) {
      // decompress the image
      byte[] filters = new byte[height];
      image = new byte[FormatTools.getPlaneSize(this)];

      InflaterInputStream decompressor =
        new InflaterInputStream(new ByteArrayInputStream(bytes));
      try {
        for (int row=0; row<height; row++) {
          int n = 0;
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
      }

      // perform any necessary unfiltering

      bpp *= getRGBChannelCount();
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
          }
        }
      }
    }
    else if (compression == 0) {
      throw new UnsupportedCompressionException("Interlacing not supported");
    }
    else {
      throw new UnsupportedCompressionException(
        "Compression type " + compression + " not supported");
    }

    // de-interleave

    bpp /= getRGBChannelCount();
    byte[] deinterleave = new byte[image.length];

    for (int c=0; c<getRGBChannelCount(); c++) {
      int plane = c * width * height * bpp;
      for (int row=0; row<height; row++) {
        int srcRow = row * width * getRGBChannelCount() * bpp;
        int destRow = row * width * bpp;

        for (int col=0; col<width; col++) {
          int srcCol = col * getRGBChannelCount() * bpp;
          int destCol = col * bpp;

          for (int b=0; b<bpp; b++) {
            deinterleave[plane + destRow + destCol + b] =
              image[srcRow + srcCol + c * bpp + b];
          }
        }
      }
    }

    return deinterleave;
  }

  // -- Helper class --

  class PNGBlock {
    public long offset;
    public int length;
    public String type;
  }

}
