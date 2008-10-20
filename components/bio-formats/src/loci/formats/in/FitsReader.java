//
// FitsReader.java
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

import java.io.IOException;
import loci.common.*;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * FitsReader is the file format reader for
 * Flexible Image Transport System (FITS) images.
 *
 * Much of this code was adapted from ImageJ (http://rsb.info.nih.gov/ij).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/FitsReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/FitsReader.java">SVN</a></dd></dl>
 */
public class FitsReader extends FormatReader {

  // -- Fields --

  /** Number of lines in the header. */
  private int count;

  // -- Constructor --

  /** Constructs a new FitsReader. */
  public FitsReader() { super("Flexible Image Transport System", "fits"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessStream) */
  public boolean isThisType(RandomAccessStream stream) throws IOException {
    return true;
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

    in.seek(2880 * ((((count * 80) - 1) / 2880) + 1));
    readPlane(in, x, y, w, h, buf);
    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    count = 0;
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

    String key = "", value = "";
    while (true) {
      count++;
      line = in.readString(80);

      // parse key/value pair
      int ndx = line.indexOf("=");
      int comment = line.indexOf("/", ndx);
      if (comment < 0) comment = line.length();

      if (ndx >= 0) {
        key = line.substring(0, ndx).trim();
        value = line.substring(ndx + 1, comment).trim();
      }
      else key = line.trim();

      if (key.equals("END")) break;

      if (key.equals("BITPIX")) {
        int bits = Integer.parseInt(value);
        switch (bits) {
          case 8:
            core[0].pixelType = FormatTools.UINT8;
            break;
          case 16:
            core[0].pixelType = FormatTools.INT16;
            break;
          case 32:
            core[0].pixelType = FormatTools.INT32;
            break;
          case -32:
            core[0].pixelType = FormatTools.FLOAT;
            break;
          default: throw new FormatException("Unsupported pixel type: " + bits);
        }
      }
      else if (key.equals("NAXIS1")) core[0].sizeX = Integer.parseInt(value);
      else if (key.equals("NAXIS2")) core[0].sizeY = Integer.parseInt(value);
      else if (key.equals("NAXIS3")) core[0].sizeZ = Integer.parseInt(value);

      addMeta(key, value);
    }

    core[0].sizeC = 1;
    core[0].sizeT = 1;
    if (getSizeZ() == 0) core[0].sizeZ = 1;
    core[0].imageCount = core[0].sizeZ;
    core[0].rgb = false;
    core[0].littleEndian = false;
    core[0].interleaved = false;
    core[0].dimensionOrder = "XYZCT";
    core[0].indexed = false;
    core[0].falseColor = false;
    core[0].metadataComplete = true;

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    store.setImageName("", 0);
    MetadataTools.setDefaultCreationDate(store, id, 0);
    MetadataTools.populatePixels(store, this);
  }

}
