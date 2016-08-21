/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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
import loci.formats.codec.BitBuffer;
import loci.formats.meta.MetadataStore;

import ome.xml.model.primitives.PositiveFloat;

/**
 * BMPReader is the file format reader for Microsoft Bitmap (BMP) files.
 * See http://astronomy.swin.edu.au/~pbourke/dataformats/bmp/ for a nice
 * description of the BMP file format.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/BMPReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/BMPReader.java;hb=HEAD">Gitweb</a></dd></dl>
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
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 2;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.readString(blockLen).startsWith(BMP_MAGIC_STRING);
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    return palette;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    if (compression != RAW && in.length() < FormatTools.getPlaneSize(this)) {
      throw new UnsupportedCompressionException(compression + " not supported");
    }

    int rowsToSkip = invertY ? y : getSizeY() - (h + y);
    int rowLength = getSizeX() * (isIndexed() ? 1 : getSizeC());
    in.seek(global + rowsToSkip * rowLength);

    int pad = ((rowLength * bpp) / 8) % 2;
    if (pad == 0) pad = ((rowLength * bpp) / 8) % 4;
    else pad *= getSizeC();
    int planeSize = getSizeX() * getSizeC() * h;
    if (bpp >= 8) planeSize *= (bpp / 8);
    else planeSize /= (8 / bpp);
    planeSize += pad * h;
    if (planeSize + in.getFilePointer() > in.length()) {
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

    byte[] rawPlane = new byte[planeSize];
    in.read(rawPlane);

    BitBuffer bb = new BitBuffer(rawPlane);

    int effectiveC = palette != null && palette[0].length > 0 ? 1 : getSizeC();
    for (int row=h-1; row>=0; row--) {
      int rowIndex = invertY ? h - 1 - row : row;
      bb.skipBits(x * bpp * effectiveC);
      for (int i=0; i<w*effectiveC; i++) {
        if (bpp <= 8) {
          buf[rowIndex * w * effectiveC + i] = (byte) (bb.getBits(bpp) & 0xff);
        }
        else {
          for (int b=0; b<bpp/8; b++) {
            buf[(bpp / 8) * (rowIndex * w * effectiveC + i) + b] =
              (byte) (bb.getBits(8) & 0xff);
          }
        }
      }
      if (row > 0) {
        bb.skipBits((getSizeX() - w - x) * bpp * effectiveC + pad*8);
      }
    }

    if (getRGBChannelCount() > 1) {
      ImageTools.bgrToRgb(buf, isInterleaved(), 1, getRGBChannelCount());
    }
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
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

      PositiveFloat sizeX = FormatTools.getPhysicalSizeX(correctedX);
      PositiveFloat sizeY = FormatTools.getPhysicalSizeY(correctedY);
      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, 0);
      }
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, 0);
      }
    }
  }

}
