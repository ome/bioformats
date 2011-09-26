//
// FitsReader.java
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
 * FitsReader is the file format reader for
 * Flexible Image Transport System (FITS) images.
 *
 * Much of this code was adapted from ImageJ (http://rsb.info.nih.gov/ij).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/FitsReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/FitsReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class FitsReader extends FormatReader {

  // -- Constants --

  private static final int LINE_LENGTH = 80;

  // -- Fields --

  private long pixelOffset;

  // -- Constructor --

  /** Constructs a new FitsReader. */
  public FitsReader() {
    super("Flexible Image Transport System", new String[] {"fits", "fts"});
    domains =
      new String[] {FormatTools.ASTRONOMY_DOMAIN, FormatTools.UNKNOWN_DOMAIN};
  }

  // -- IFormatReader API methods --

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    in.seek(pixelOffset + no * FormatTools.getPlaneSize(this));
    readPlane(in, x, y, w, h, buf);
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) pixelOffset = 0;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    String line = in.readString(LINE_LENGTH);
    if (!line.startsWith("SIMPLE")) {
      throw new FormatException("Unsupported FITS file.");
    }

    String key = "", value = "";
    while (true) {
      line = in.readString(LINE_LENGTH);

      // parse key/value pair
      int ndx = line.indexOf("=");
      int comment = line.indexOf("/", ndx);
      if (comment < 0) comment = line.length();

      if (ndx >= 0) {
        key = line.substring(0, ndx).trim();
        value = line.substring(ndx + 1, comment).trim();
      }
      else key = line.trim();

      // if the file has an extended header, "END" will appear twice
      // the first time marks the end of the extended header
      // the second time marks the end of the standard header
      // image dimensions are only populated by the standard header
      if (key.equals("END") && getSizeX() > 0) break;

      if (key.equals("BITPIX")) {
        int bits = Integer.parseInt(value);
        boolean fp = bits < 0;
        boolean signed = bits != 8;
        bits = Math.abs(bits) / 8;
        core[0].pixelType = FormatTools.pixelTypeFromBytes(bits, signed, fp);
      }
      else if (key.equals("NAXIS1")) core[0].sizeX = Integer.parseInt(value);
      else if (key.equals("NAXIS2")) core[0].sizeY = Integer.parseInt(value);
      else if (key.equals("NAXIS3")) core[0].sizeZ = Integer.parseInt(value);

      addGlobalMeta(key, value);
    }
    while (in.read() == 0x20);
    pixelOffset = in.getFilePointer() - 1;

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

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);
    MetadataTools.setDefaultCreationDate(store, id, 0);
  }

}
