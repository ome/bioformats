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

package ome.xml.model;

import java.util.ArrayList;
import java.util.List;

import ome.xml.model.enums.EnumerationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
  public boolean link(Reference reference, OMEModelObject o) {
    return false;
  }

  /**
   * Retrieves all the children of an element that have a given tag name. If a
   * tag has a namespace prefix it will be stripped prior to attempting a
   * name match.
   * @param parent DOM element to retrieve tags based upon.
   * @param name Name of the tags to retrieve.
   * @return List of elements which have the tag <code>name</code>.
   */
  public static List<Element> getChildrenByTagName(
      Element parent, String name) {
    List<Element> toReturn = new ArrayList<Element>();
    NodeList children = parent.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      if (child.getNodeType() == Node.ELEMENT_NODE
          && name.equals(stripNamespacePrefix(child.getNodeName()))) {
        toReturn.add((Element) child);
      }
    }
    return toReturn;
  }

  /**
   * Strips the namespace prefix off of a given tag name.
   * @param v Tag name to strip the prefix from if it has one.
   * @return <code>v</code> with the namespace prefix stripped or <code>v</code>
   * if it has none.
   */
  public static String stripNamespacePrefix(String v) {
    int beginIndex = v.lastIndexOf(':');
    if (beginIndex != -1) {
      v = v.substring(beginIndex + 1);
    }
    return v;
  }
}
