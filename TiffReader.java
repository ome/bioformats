//
// TiffReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

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

package loci.formats;

import java.io.IOException;

/**
 * TiffReader is the file format reader for TIFF files.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at cs.wisc.edu
 */

public class TiffReader extends BaseTiffReader {

  // -- Constructor --

  /** Constructs a new Tiff reader. */
  public TiffReader() {
    super("Tagged Image File Format", new String[] {"tif", "tiff"});
  }


  // -- Internal BaseTiffForm API methods --

  /** Parses OME-XML metadata. */
  protected void initOMEMetadata() {
    // check for OME-XML in TIFF comment (OME-TIFF format)
    Object o = TiffTools.getIFDValue(ifds[0], TiffTools.IMAGE_DESCRIPTION);
    Object root = o instanceof String ? OMETools.createRoot((String) o) : null;
    metadata.put("OME-TIFF", root == null ? "no" : "yes");
    if (root == null) super.initOMEMetadata();
    else ome = root;
  }


  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new TiffReader().testRead(args);
  }
}
