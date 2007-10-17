//
// TiffReader.java
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
import java.util.*;
import loci.formats.*;

/**
 * TiffReader is the file format reader for TIFF files, including OME-TIFF.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/TiffReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/TiffReader.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 */
public class TiffReader extends BaseTiffReader {

  // -- Constructor --

  /** Constructs a new Tiff reader. */
  public TiffReader() {
    super("Tagged Image File Format", new String[] {"tif", "tiff"});
  }

  // -- Internal TiffReader API methods --

  /**
   * Allows a class which is delegating parsing responsibility to
   * <code>TiffReader</code> the ability to affect the <code>sizeZ</code> value
   * that is inserted into the metadata store.
   * @param zSize the number of optical sections to use when making a call to
   * {@link loci.formats.MetadataStore#setPixels(Integer, Integer, Integer,
   *   Integer, Integer, Integer, Boolean, String, Integer, Integer)}.
   */
  protected void setSizeZ(int zSize) {
    if (core.sizeZ == null) core.sizeZ = new int[1];
    core.sizeZ[0] = zSize;
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#initStandardMetadata() */
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();
    String comment = (String) getMeta("Comment");

    status("Checking comment style");

    if (ifds.length > 1) core.orderCertain[0] = false;

    // check for ImageJ-style TIFF comment
    boolean ij = comment != null && comment.startsWith("ImageJ=");
    if (ij) {
      int nl = comment.indexOf("\n");
      put("ImageJ", nl < 0 ? comment.substring(7) : comment.substring(7, nl));
      metadata.remove("Comment");
    }

    // check for MetaMorph-style TIFF comment
    boolean metamorph = comment != null && getMeta("Software") != null &&
      ((String) getMeta("Software")).indexOf("MetaMorph") != -1;
    put("MetaMorph", metamorph ? "yes" : "no");

    if (metamorph) {
      // parse key/value pairs
      StringTokenizer st = new StringTokenizer(comment, "\n");
      while (st.hasMoreTokens()) {
        String line = st.nextToken();
        int colon = line.indexOf(":");
        if (colon < 0) {
          addMeta("Comment", line);
          continue;
        }
        String key = line.substring(0, colon);
        String value = line.substring(colon + 1);
        addMeta(key, value);
      }
    }
  }

}
