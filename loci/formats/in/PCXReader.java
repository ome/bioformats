//
// PCXReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.in;

import java.io.*;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * PCXReader is the file format reader for PCX files (originally used by
 * PC Paintbrush; now used in Zeiss' LSM Image Browser).
 * See http://courses.ece.uiuc.edu/ece390/books/labmanual/graphics-pcx.html
 * for an outline of the format.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/PCXReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/PCXReader.java">SVN</a></dd></dl>
 */
public class PCXReader extends FormatReader {

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
    blockCheckLen = 1;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return block.length >= blockCheckLen && block[0] == 10;
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

    in.seek(offset);

    // PCX uses a simple RLE compression algorithm

    byte[] b = new byte[bytesPerLine * core.sizeY[0] * nColorPlanes];
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

  /* @see loci.formats.IFormatReader#close() */
  public void close() throws IOException {
    super.close();
    offset = 0;
    bytesPerLine = 0;
    nColorPlanes = 0;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("PCXReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    status("Reading file header");

    core.littleEndian[0] = true;
    in.order(true);
    in.seek(1);
    int version = in.read();
    in.skipBytes(1);
    int bitsPerPixel = in.read();
    int xMin = in.readShort();
    int yMin = in.readShort();
    int xMax = in.readShort();
    int yMax = in.readShort();

    core.sizeX[0] = xMax - xMin;
    core.sizeY[0] = yMax - yMin;

    int vertDPI = in.readShort();
    int horizDPI = version == 5 ? in.readShort() : 1;

    in.skipBytes(48);

    in.skipBytes(1);

    nColorPlanes = in.read();
    bytesPerLine = in.readShort();
    int paletteType = in.readShort();

    offset = in.getFilePointer() + 58;

    core.sizeZ[0] = 1;
    core.sizeT[0] = 1;
    core.sizeC[0] = 1;
    core.rgb[0] = false;
    core.imageCount[0] = 1;
    core.pixelType[0] = FormatTools.UINT8;
    core.currentOrder[0] = "XYCZT";
  }

}
