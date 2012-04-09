//
// MetadataHandler.java
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

import java.util.Hashtable;

import org.xml.sax.Attributes;

/**
 * Used to retrieve key/value pairs from XML.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/xml/MetadataHandler.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/xml/MetadataHandler.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Chris Allan callan at blackcat.ca
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
class MetadataHandler extends BaseHandler {
  private String currentQName;
  private Hashtable<String, String> metadata =
    new Hashtable<String, String>();

  // -- MetadataHandler API methods --

  public Hashtable<String, String> getMetadata() {
    return metadata;
  }

  // -- DefaultHandler API methods --

  public void characters(char[] data, int start, int len) {
    metadata.put(currentQName, new String(data, start, len));
  }

  public void startElement(String uri, String localName, String qName,
    Attributes attributes)
  {
    if (attributes.getLength() == 0) currentQName += " - " + qName;
    else currentQName = qName;
    for (int i=0; i<attributes.getLength(); i++) {
      metadata.put(qName + " - " + attributes.getQName(i),
        attributes.getValue(i));
    }
  }
}
