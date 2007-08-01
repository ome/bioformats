//
// OMETiffWriter.java
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

package loci.formats.out;

import java.io.IOException;
import java.lang.reflect.Method;
import loci.formats.*;

/**
 * OMETiffWriter is the file format writer for OME-TIFF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/out/OMETiffWriter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/out/OMETiffWriter.java">SVN</a></dd></dl>
 */
public class OMETiffWriter extends TiffWriter {

  // -- Constructor --

  public OMETiffWriter() {
    super("OME-TIFF", new String[] {"ome.tif", "ome.tiff"});
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    if (out != null) out.close();
    out = null;
    if (currentId != null && retrieve != null) {
      // write OME-XML to the first IFD's comment

      MetadataStore omexml = null;

      try {
        Class c = Class.forName("loci.formats.ome.OMEXMLMetadataStore");
        omexml = (MetadataStore) c.newInstance();

        if (c.isInstance(retrieve)) omexml = (MetadataStore) retrieve;
        else FormatTools.convertMetadata(retrieve, omexml);

        Method m = omexml.getClass().getMethod("dumpXML", (Class[]) null);
        String xml = (String) m.invoke(omexml, (Object[]) null);

        // insert TiffData element
        int pix = xml.indexOf("<Pixels ");
        int end = xml.indexOf("/>", pix);
        xml = xml.substring(0, end) + "><TiffData/></Pixels>" +
          xml.substring(end + 2);

        TiffTools.overwriteComment(currentId, xml);
      }
      catch (Throwable t) {
        LogTools.trace(t);
      }
    }
    super.close();
  }

}
