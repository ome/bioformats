//
// BMPReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
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

import java.io.IOException;

import loci.common.RandomAccessInputStream;
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

    if (compression != RAW) {
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
      pad = 0;
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
        buf[rowIndex * w * effectiveC + i] = (byte) (bb.getBits(bpp) & 0xff);
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

    core[0].sizeX = in.readInt();
    core[0].sizeY = in.readInt();

    if (getSizeX() < 1) {
      LOGGER.trace("Invalid width: {}; using the absolute value", getSizeX());
      core[0].sizeX = Math.abs(getSizeX());
    }
    if (getSizeY() < 1) {
      LOGGER.trace("Invalid height: {}; using the absolute value", getSizeY());
      core[0].sizeY = Math.abs(getSizeY());
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

    core[0].sizeC = bpp != 24 ? 1 : 3;
    if (bpp == 32) core[0].sizeC = 4;
    if (bpp > 8) bpp /= getSizeC();

    switch (bpp) {
      case 16:
        core[0].pixelType = FormatTools.UINT16;
        break;
      case 32:
        core[0].pixelType = FormatTools.UINT32;
        break;
      default:
        core[0].pixelType = FormatTools.UINT8;
    }

    core[0].rgb = getSizeC() > 1;
    core[0].littleEndian = true;
    core[0].interleaved = true;
    core[0].imageCount = 1;
    core[0].sizeZ = 1;
    core[0].sizeT = 1;
    core[0].dimensionOrder = "XYCTZ";
    core[0].metadataComplete = true;
    core[0].indexed = palette != null;
    if (isIndexed()) {
      core[0].sizeC = 1;
      core[0].rgb = false;
    }
    core[0].falseColor = false;

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
    MetadataTools.setDefaultCreationDate(store, id, 0);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      // resolution is stored as pixels per meter; we want to convert to
      // microns per pixel

      double correctedX = pixelSizeX == 0 ? 0.0 : 1000000.0 / pixelSizeX;
      double correctedY = pixelSizeY == 0 ? 0.0 : 1000000.0 / pixelSizeY;

      if (correctedX > 0) {
        store.setPixelsPhysicalSizeX(new PositiveFloat(correctedX), 0);
      }
      if (correctedY > 0) {
        store.setPixelsPhysicalSizeY(new PositiveFloat(correctedY), 0);
      }
    }
  }

}
