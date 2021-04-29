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

import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.ImageTools;
import loci.formats.MetadataTools;
import loci.formats.UnsupportedCompressionException;
import loci.formats.meta.MetadataStore;

import ome.units.quantity.Length;

/**
 * BMPReader is the file format reader for Microsoft Bitmap (BMP) files.
 * See http://astronomy.swin.edu.au/~pbourke/dataformats/bmp/ for a nice
 * description of the BMP file format.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class BMPReader extends FormatReader {

  // -- Constants --

  public static final String BMP_MAGIC_STRING = "BM";

  /** Compression types. */
  private static final int RAW = 0;
  private static final int RLE_8 = 1;
  private static final int RLE_4 = 2;
  private static final int RGB_MASK = 3;

  // -- Fields --

  /** Number of bits per pixel. */
  private int bpp;

  /** The palette for indexed color images. */
  private byte[][] palette;

  /** Compression type */
  private int compression;

  /** Offset to image data. */
  private long global;

  private boolean invertY = false;

  // -- Constructor --

  /** Constructs a new BMP reader. */
  public BMPReader() {
    super("Windows Bitmap", "bmp");
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 2;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readString(blockLen).startsWith(BMP_MAGIC_STRING);
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  @Override
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    return palette;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    if (compression != RAW && in.length() < FormatTools.getPlaneSize(this)) {
      throw new UnsupportedCompressionException(compression + " not supported");
    }

    long rowsToSkip = invertY ? y : getSizeY() - (h + y);
    int rowLength = (getSizeX() * (isIndexed() ? 1 : getSizeC()) * bpp) / 8;

    in.seek(global + rowsToSkip * rowLength);

    int pad = ((rowLength * bpp) / 8) % 2;
    if (pad == 0) pad = ((rowLength * bpp) / 8) % 4;
    else pad *= getSizeC();
    int planeSize = getSizeX() * getSizeC() * h;
    if (bpp >= 8) planeSize *= (bpp / 8);
    else planeSize /= (8 / bpp);
    planeSize += pad * h;
    if (planeSize + in.getFilePointer() + rowsToSkip * pad > in.length()) {
      planeSize -= (pad * h);

      // sometimes we have RGB images with a single padding byte
      if (planeSize + getSizeY() + in.getFilePointer() <= in.length()) {
        pad = 1;
        planeSize += h;
      }
      else {
        pad = 0;
      }
    }

    in.skipBytes(rowsToSkip * pad);

    int effectiveC = palette != null && palette[0].length > 0 ? 1 : getSizeC();

    if (compression == RAW) {
      for (int row=h-1; row>=0; row--) {
        int rowIndex = invertY ? h - 1 - row : row;
        in.skipBits(x * bpp * effectiveC);
        for (int i=0; i<w*effectiveC; i++) {
          if (bpp <= 8) {
            buf[rowIndex * w * effectiveC + i] = (byte) (in.readBits(bpp) & 0xff);
          }
          else {
            for (int b=0; b<bpp/8; b++) {
              buf[(bpp / 8) * (rowIndex * w * effectiveC + i) + b] =
                (byte) (in.readBits(8) & 0xff);
            }
          }
        }
        if (row > 0) {
          int nBits = (getSizeX() - w - x) * bpp * effectiveC + pad * 8;

          if (in.getFilePointer() + (nBits / 8) < in.length()) {
            in.skipBits(nBits);
          }
          else {
            break;
          }
        }
      }
    }
    else if (compression == RLE_8 || compression == RLE_4) {
      boolean endOfFile = false;
      int index = 0;
      byte[] plane = new byte[getSizeX() * getSizeY() * getRGBChannelCount()];
      while(!endOfFile) {
        byte firstByte = (byte) (in.readBits(bpp) & 0xff);
        byte secondByte = (byte) (in.readBits(bpp) & 0xff);
        if (firstByte == 0) {
          if (secondByte == 1) {
            endOfFile = true;
          }
          else if (secondByte == 2) {
            byte xDelta = (byte) (in.readBits(bpp) & 0xff);
            byte yDelta = (byte) (in.readBits(bpp) & 0xff);
            index += (yDelta * rowLength) + xDelta;
          }
          else if (secondByte > 2) {
            // Absolute mode
            if (compression == RLE_8) {
              for (int i = 0; i < secondByte; i++) {
                  byte absoluteByte = (byte) (in.readBits(bpp) & 0xff);
                  plane[index] = absoluteByte;
                  index++;
              }
              // In absolute mode, each run must be aligned on a word boundary
              if (secondByte % 2 == 1) in.skipBytes(1);
            }
            else if (compression == RLE_4) {
              for (int i = 0; i < secondByte; i+=2) {
                byte absoluteByte = (byte) (in.readBits(bpp) & 0xff);
                byte firstNibble = (byte)(absoluteByte & 0xf);
                byte secondNibble = (byte)((byte)(absoluteByte >> 4) & 0xf);
                plane[index] = firstNibble;
                index++;
                if (i + 1 < secondByte) {
                  plane[index] = secondNibble;
                  index++;
                }
              }
              // In absolute mode, each run must be aligned on a word boundary
              if (secondByte % 4 == 2) in.skipBytes(1);
            }
          }
        }
        else {
          if (compression == RLE_8) {
            for (int i = 0; i < firstByte; i++) {
              plane[index] = secondByte;
              index++;
            }
          }
          else if (compression == RLE_4) {
            byte firstNibble = (byte)(secondByte & 0xf);
            byte secondNibble = (byte)((byte)(secondByte >> 4) & 0xf);
            for (int i = 0; i < firstByte; i++) {
              if (i % 2 == 0) {
                plane[index] = firstNibble;
              }
              else {
                plane[index] = secondNibble;
              }
              index++;
            }
          }
        }
      }
      try (RandomAccessInputStream s = new RandomAccessInputStream(plane)) {
        readPlane(s, x, y, w, h, buf);
      }
    }

    if (getRGBChannelCount() > 1) {
      ImageTools.bgrToRgb(buf, isInterleaved(), 1, getRGBChannelCount());
    }
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      bpp = compression = 0;
      global = 0;
      palette = null;
      invertY = false;
    }
  }

  // -- Internel FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    CoreMetadata m = core.get(0);

    LOGGER.info("Reading bitmap header");

    in.order(true);

    // read the first header - 14 bytes

    addGlobalMeta("Magic identifier", in.readString(2));

    addGlobalMeta("File size (in bytes)", in.readInt());
    in.skipBytes(4);
    global = in.readInt();

    // read the second header - 40 bytes

    in.skipBytes(4);

    // get the dimensions

    m.sizeX = in.readInt();
    m.sizeY = in.readInt();

    if (getSizeX() < 1) {
      LOGGER.trace("Invalid width: {}; using the absolute value", getSizeX());
      m.sizeX = Math.abs(getSizeX());
    }
    if (getSizeY() < 1) {
      LOGGER.trace("Invalid height: {}; using the absolute value", getSizeY());
      m.sizeY = Math.abs(getSizeY());
      invertY = true;
    }

    addGlobalMeta("Color planes", in.readShort());
    bpp = in.readShort();

    compression = in.readInt();

    in.skipBytes(4);
    int pixelSizeX = in.readInt();
    int pixelSizeY = in.readInt();
    int nColors = in.readInt();
    if (nColors == 0 && bpp != 32 && bpp != 24) {
      nColors = bpp < 8 ? 1 << bpp : 256;
    }
    in.skipBytes(4);

    // read the palette, if it exists

    if (nColors != 0 && bpp == 8) {
      palette = new byte[3][256];

      for (int i=0; i<nColors; i++) {
        for (int j=palette.length-1; j>=0; j--) {
          palette[j][i] = in.readByte();
        }
        in.skipBytes(1);
      }
    }
    else if (nColors != 0) in.skipBytes(nColors * 4);

    LOGGER.info("Populating metadata");

    m.sizeC = bpp != 24 ? 1 : 3;
    if (bpp == 32) m.sizeC = 4;
    if (bpp > 8) bpp /= getSizeC();

    switch (bpp) {
      case 16:
        m.pixelType = FormatTools.UINT16;
        break;
      case 32:
        m.pixelType = FormatTools.UINT32;
        break;
      default:
        m.pixelType = FormatTools.UINT8;
    }

    m.rgb = getSizeC() > 1;
    m.littleEndian = true;
    m.interleaved = true;
    m.imageCount = 1;
    m.sizeZ = 1;
    m.sizeT = 1;
    m.dimensionOrder = "XYCTZ";
    m.metadataComplete = true;
    m.indexed = palette != null;
    if (isIndexed()) {
      m.sizeC = 1;
      m.rgb = false;
    }
    m.falseColor = false;

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      addGlobalMeta("Indexed color", palette != null);
      addGlobalMeta("Image width", getSizeX());
      addGlobalMeta("Image height", getSizeY());
      addGlobalMeta("Bits per pixel", bpp);
      String comp = "invalid";

      switch (compression) {
        case RAW:
          comp = "None";
          break;
        case RLE_8:
          comp = "8 bit run length encoding";
          break;
        case RLE_4:
          comp = "4 bit run length encoding";
          break;
        case RGB_MASK:
          comp = "RGB bitmap with mask";
          break;
      }

      addGlobalMeta("Compression type", comp);
      addGlobalMeta("X resolution", pixelSizeX);
      addGlobalMeta("Y resolution", pixelSizeY);
    }

    // Populate metadata store.

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      // resolution is stored as pixels per meter; we want to convert to
      // microns per pixel

      double correctedX = pixelSizeX == 0 ? 0.0 : 1000000.0 / pixelSizeX;
      double correctedY = pixelSizeY == 0 ? 0.0 : 1000000.0 / pixelSizeY;

      Length sizeX = FormatTools.getPhysicalSizeX(correctedX);
      Length sizeY = FormatTools.getPhysicalSizeY(correctedY);
      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, 0);
      }
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, 0);
      }
    }
  }

}
