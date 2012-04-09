//
// ValidationSAXHandler.java
//

/*
LOCI Common package: utilities for I/O, reflection and miscellaneous tasks.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden and Chris Allan.

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

package loci.common.xml;

import java.util.StringTokenizer;

import org.xml.sax.Attributes;


/**
 * Used by validateXML to parse the XML block's schema path using SAX.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/xml/ValidationSAXHandler.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/xml/ValidationSAXHandler.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Chris Allan callan at blackcat.ca
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
/**  */
class ValidationSAXHandler extends BaseHandler {
  private String schemaPath;
  private boolean first;
  public String getSchemaPath() { return schemaPath; }
  public void startDocument() {
    schemaPath = null;
    first = true;
  }
  public void startElement(String uri,
    String localName, String qName, Attributes attributes)
  {
    if (!first) return;
    first = false;

    int len = attributes.getLength();
    String xmlns = null, xsiSchemaLocation = null;
    for (int i=0; i<len; i++) {
      String name = attributes.getQName(i);
      if (name.equals("xmlns")) xmlns = attributes.getValue(i);
      else if (name.equals("schemaLocation") ||
        name.endsWith(":schemaLocation"))
      {
        xsiSchemaLocation = attributes.getValue(i);
      }
    }
    if (xmlns == null || xsiSchemaLocation == null) return; // not found

    StringTokenizer st = new StringTokenizer(xsiSchemaLocation);
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      if (xmlns.equals(token)) {
        // next token is the actual schema path
        if (st.hasMoreTokens()) schemaPath = st.nextToken();
        break;
      }
    }
  }
}
