//
// PCXReader.java
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
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

/**
 * PCXReader is the file format reader for PCX files (originally used by
 * PC Paintbrush; now used in Zeiss' LSM Image Browser).
 * See http://www.qzx.com/pc-gpe/pcx.txt
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/PCXReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/PCXReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class PCXReader extends FormatReader {

  // -- Constants --

  public static final byte PCX_MAGIC_BYTE = 10;

  // -- Fields --

  /** Offset to pixel data. */
  private long offset;

  /** Number of bytes per scan line - may be different than image width. */
  private int bytesPerLine;

  private int nColorPlanes;
  private byte[][] lut;

  // -- Constructor --

  /** Constructs a new PCX reader. */
  public PCXReader() {
    super("PCX", "pcx");
    domains = new String[] {FormatTools.GRAPHICS_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 1;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.read() == PCX_MAGIC_BYTE;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() {
    FormatTools.assertId(currentId, true, 1);
    return lut;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    in.seek(offset);

    // PCX uses a simple RLE compression algorithm

    byte[] b = new byte[bytesPerLine * getSizeY() * nColorPlanes];
    int pt = 0;
    while (pt < b.length) {
      int val = in.read() & 0xff;
      if (((val & 0xc0) >> 6) == 3) {
        int len = val & 0x3f;
        val = in.read() & 0xff;
        for (int q=0; q<len; q++) {
          b[pt++] = (byte) val;
          if ((pt % bytesPerLine) == 0) {
            break;
          }
        }
      }
      else b[pt++] = (byte) (val & 0xff);
    }

    int src = y * nColorPlanes * bytesPerLine;
    for (int row=0; row<h; row++) {
      int rowOffset = row * nColorPlanes * bytesPerLine;
      for (int c=0; c<nColorPlanes; c++) {
        System.arraycopy(b, src + rowOffset + x, buf, c * w * h + row * w, w);
        rowOffset += bytesPerLine;
      }
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      offset = 0;
      bytesPerLine = 0;
      nColorPlanes = 0;
      lut = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    LOGGER.info("Reading file header");

    core[0].littleEndian = true;
    in.order(isLittleEndian());
    in.seek(1);
    int version = in.read();
    in.skipBytes(1);
    int bitsPerPixel = in.read();
    int xMin = in.readShort();
    int yMin = in.readShort();
    int xMax = in.readShort();
    int yMax = in.readShort();

    core[0].sizeX = xMax - xMin;
    core[0].sizeY = yMax - yMin;

    in.skipBytes(version == 5 ? 53 : 51);

    nColorPlanes = in.read();
    bytesPerLine = in.readShort();
    int paletteType = in.readShort();

    offset = in.getFilePointer() + 58;

    if (version == 5 && nColorPlanes == 1) {
      in.seek(in.length() - 768);
      lut = new byte[3][256];
      for (int i=0; i<lut[0].length; i++) {
        for (int j=0; j<lut.length; j++) {
          lut[j][i] = in.readByte();
        }
      }
      core[0].indexed = true;
    }

    addGlobalMeta("Palette type", paletteType);

    core[0].sizeZ = 1;
    core[0].sizeT = 1;
    core[0].sizeC = nColorPlanes;
    core[0].rgb = nColorPlanes > 1;
    core[0].imageCount = 1;
    core[0].pixelType = FormatTools.UINT8;
    core[0].dimensionOrder = "XYCZT";
    core[0].interleaved = false;

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
  }

}
