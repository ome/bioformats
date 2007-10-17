//
// FitsReader.java
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

import java.io.IOException;
import loci.formats.*;

/**
 * FitsReader is the file format reader for
 * Flexible Image Transport System (FITS) images.
 *
 * Much of this code was adapted from ImageJ (http://rsb.info.nih.gov/ij).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/FitsReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/FitsReader.java">SVN</a></dd></dl>
 */
public class FitsReader extends FormatReader {

  // -- Fields --

  /** Number of lines in the header. */
  private int count;

  // -- Constructor --

  /** Constructs a new FitsReader. */
  public FitsReader() { super("Flexible Image Transport System", "fits"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return true;
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length);

    in.seek(2880 + 2880 * (((count * 80) - 1) / 2880));
    int line = core.sizeX[0] * FormatTools.getBytesPerPixel(core.pixelType[0]);
    for (int y=core.sizeY[0]-1; y>=0; y--) {
      in.read(buf, y*line, line);
    }
    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessStream(id);
    count = 1;

    String line = in.readString(80);
    if (!line.startsWith("SIMPLE")) {
      throw new FormatException("Unsupported FITS file.");
    }

    while (true) {
      count++;
      line = in.readString(80);

      // parse key/value pair
      int ndx = line.indexOf("=");
      int comment = line.indexOf("/", ndx);
      if (comment < 0) comment = line.length();

      String key = "", value = "";

      if (ndx >= 0) {
        key = line.substring(0, ndx).trim();
        value = line.substring(ndx + 1, comment).trim();
      }
      else key = line.trim();

      if (key.equals("END")) break;

      if (key.equals("BITPIX")) {
        int bits = Integer.parseInt(value);
        switch (bits) {
          case 8: core.pixelType[0] = FormatTools.UINT8; break;
          case 16: core.pixelType[0] = FormatTools.UINT16; break;
          case 32: core.pixelType[0] = FormatTools.UINT32; break;
          case -32: core.pixelType[0] = FormatTools.FLOAT; break;
          default: throw new FormatException("Unsupported pixel type: " + bits);
        }
      }
      else if (key.equals("NAXIS1")) core.sizeX[0] = Integer.parseInt(value);
      else if (key.equals("NAXIS2")) core.sizeY[0] = Integer.parseInt(value);
      else if (key.equals("NAXIS3")) core.sizeZ[0] = Integer.parseInt(value);

      addMeta(key, value);
    }

    core.sizeC[0] = 1;
    core.sizeT[0] = 1;
    if (core.sizeZ[0] == 0) core.sizeZ[0] = 1;
    core.imageCount[0] = core.sizeZ[0];
    core.rgb[0] = false;
    core.littleEndian[0] = false;
    core.interleaved[0] = false;
    core.currentOrder[0] = "XYZCT";
    core.indexed[0] = false;
    core.falseColor[0] = false;
    core.metadataComplete[0] = true;

    MetadataStore store = getMetadataStore();

    store.setImage(currentId, null, null, null);
    FormatTools.populatePixels(store, this);

    store.setLogicalChannel(0, null, null, null, null, null, null, null, null,
      null, null, null, null, null, null, null, null, null, null, null,
      null, null, null, null, null);
  }

}
