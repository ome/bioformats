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
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * PCXReader is the file format reader for PCX files (originally used by
 * PC Paintbrush; now used in Zeiss' LSM Image Browser).
 * See http://www.qzx.com/pc-gpe/pcx.txt
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/PCXReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/PCXReader.java">SVN</a></dd></dl>
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

  // -- Constructor --

  /** Constructs a new PCX reader. */
  public PCXReader() {
    super("PCX", "pcx");
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 1;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    return stream.read() == PCX_MAGIC_BYTE;
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

    for (int row=0; row<h; row++) {
      System.arraycopy(b, (row*nColorPlanes + y) * bytesPerLine + x,
        buf, row * w, w);
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
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    debug("PCXReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    status("Reading file header");

    core[0].littleEndian = true;
    in.order(true);
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

    int vertDPI = in.readShort();
    int horizDPI = version == 5 ? in.readShort() : 1;

    in.skipBytes(49);

    nColorPlanes = in.read();
    bytesPerLine = in.readShort();
    int paletteType = in.readShort();

    offset = in.getFilePointer() + 58;

    core[0].sizeZ = 1;
    core[0].sizeT = 1;
    core[0].sizeC = 1;
    core[0].rgb = false;
    core[0].imageCount = 1;
    core[0].pixelType = FormatTools.UINT8;
    core[0].dimensionOrder = "XYCZT";

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this);
    MetadataTools.setDefaultCreationDate(store, id, 0);
  }

}
