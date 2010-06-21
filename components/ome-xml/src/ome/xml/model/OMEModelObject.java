//
// OMEModelObject.java
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

import ome.xml.model.enums.EnumerationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author callan
 *
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/OMEModelObject.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/OMEModelObject.java">SVN</a></dd></dl>
 */
public interface OMEModelObject {

  /**
   * Takes the entire object hierarchy and produces an XML DOM tree.
   * @param document Destination document for element creation, etc.
   * @return XML DOM tree root element for this model object.
   */
  Element asXMLElement(Document document);

  /** 
   * Updates the object hierarchy recursively from an XML DOM tree.
   * <b>NOTE:</b> No properties are removed, only added or updated.
   * @param element Root of the XML DOM tree to construct a model object
   * graph from.
   * @param model Handler for the OME model which keeps track of instances
   * and references seen during object population.
   * @throws EnumerationException If there is an error instantiating an
   * enumeration during model object creation.
   */
  void update(Element element, OMEModel model) throws EnumerationException;

  /**
   * Link a given OME model object to this model object.
   * @param reference The <i>type</i> qualifier for the reference. This should
   * be the corresponding reference type for <code>o</code>. If, for example,
   * <code>o</code> is of type <code>Image</code>, <code>reference</code>
   * <b>MUST</b> be of type <code>ImageRef</code>.
   * @param o Model object to link to.
   * @return <code>true</code> if this model object was able to handle the
   * reference, <code>false</code> otherwise.
   */
  boolean link(Reference reference, OMEModelObject o);
}
