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

package loci.formats.in;

import java.io.IOException;
import java.util.StringTokenizer;
import loci.formats.*;

/**
 * TiffReader is the file format reader for TIFF files.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at cs.wisc.edu
 */

public class TiffReader extends BaseTiffReader {

  /** Number of optical sections in the file */
  private int sizeZ = 1;
  
  // -- Constructor --

  /** Constructs a new Tiff reader. */
  public TiffReader() {
    super("Tagged Image File Format", new String[] {"tif", "tiff"});
  }


  // -- Internal BaseTiffForm API methods --

  /** Parses standard metadata. */
  protected void initStandardMetadata() {
    super.initStandardMetadata();
    String comment = (String) metadata.get("Comment");

    // check for ImageJ-style TIFF comment
    boolean ij = comment != null && comment.startsWith("ImageJ=");
    if (ij) {
      int nl = comment.indexOf("\n");
      put("ImageJ", nl < 0 ? comment.substring(7) : comment.substring(7, nl));
      metadata.remove("Comment");
    }

    // check for Improvision-style TIFF comment
    boolean iv = comment != null && comment.startsWith("[Improvision Data]\n");
    put("Improvision", iv ? "yes" : "no");
    if (iv) {
      // parse key/value pairs
      StringTokenizer st = new StringTokenizer(comment, "\n");
      while (st.hasMoreTokens()) {
        String line = st.nextToken();
        int equals = line.indexOf("=");
        if (equals < 0) continue;
        String key = line.substring(0, equals);
        String value = line.substring(equals + 1);
        metadata.put(key, value);
      }
      metadata.remove("Comment");
    }
  }

  /** Parses OME-XML metadata. */
  protected void initMetadataStore() {
    // check for OME-XML in TIFF comment (OME-TIFF format)
    // we need an extra check to make sure that any XML we find is indeed
    // OME-XML (and not some other XML variant)
    MetadataStore store = getMetadataStore();
    String comment = (String) metadata.get("Comment");
    if (getMetadataStore() instanceof OMEXMLMetadataStore
        && comment.indexOf("ome.xsd") != -1) {
      OMEXMLMetadataStore xmlStore = (OMEXMLMetadataStore) store;
      xmlStore.createRoot(comment);
    }
  }

  /* (non-Javadoc)
   * @see loci.formats.BaseTiffReader#getSizeZ()
   */
  protected Integer getSizeZ() {
    return new Integer(sizeZ);
  }
  
  /**
   * Allows a class which is delegating parsing responsibility to
   * <code>TiffReader</code> the ability to affect the <code>sizeZ</code> value
   * that is inserted into the metadata store.
   * @param sizeZ the number of optical sections to use when making a call to
   * {@link MetadataStore#setPixels()}.
   */
  void setInitialSizeZ(int sizeZ) {
    this.sizeZ = sizeZ;
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new TiffReader().testRead(args);
  }

}
