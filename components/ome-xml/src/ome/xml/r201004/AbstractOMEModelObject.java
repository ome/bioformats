//
// AbstractOMEModelObject.java
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

package ome.xml.r201004;

import ome.xml.r201004.enums.EnumerationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author callan
 *
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/AbstractOMEModelObject.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/AbstractOMEModelObject.java">SVN</a></dd></dl>
 */
public abstract class AbstractOMEModelObject implements OMEModelObject {

  public AbstractOMEModelObject() {
    super();
  }

  public AbstractOMEModelObject(Element element) throws EnumerationException {
    update(element);
  }

  /* (non-Javadoc)
   * @see ome.xml.r201004.OMEModelObject#update(org.w3c.dom.Element)
   */
  public void update(Element element) throws EnumerationException {
    // Nothing to update.
  }

  /* (non-Javadoc)
   * @see ome.xml.r201004.OMEModelObject#update(org.w3c.dom.Element, ome.xml.r201004.OMEModel)
   */
  public void update(Element element, OMEModel model)
  throws EnumerationException {
    // Nothing to update.
  }

  /* (non-Javadoc)
   * @see ome.xml.r201004.OMEModelObject#asXMLElement(org.w3c.dom.Document)
   */
  public abstract Element asXMLElement(Document document);
  
  /**
   * Takes the entire object hierarchy and produced an XML DOM tree taking
   * into account class hierarchy.
   * @param document Destination document for element creation, etc.
   * @param element Element from the subclass. If </code>null</code> a new
   * element will be created of this class.
   * @return <code>element</code> populated with properties from this class.
   */
  protected Element asXMLElement(Document document, Element element) {
    return element;
  }

  /* (non-Javadoc)
   * @see ome.xml.r201004.OMEModelObject#link(ome.xml.r201004.Reference, ome.xml.r201004.OMEModelObject)
   */
  public abstract void link(Reference reference, OMEModelObject o);
}
