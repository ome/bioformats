/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2015 Open Microscopy Environment:
 *   - Massachusetts Institute of Technology
 *   - National Institutes of Health
 *   - University of Dundee
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
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
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/AbstractOMEModelObject.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/AbstractOMEModelObject.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public abstract class AbstractOMEModelObject implements OMEModelObject {

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
