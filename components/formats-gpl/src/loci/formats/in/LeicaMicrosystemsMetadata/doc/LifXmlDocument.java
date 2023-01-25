/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.formats.in.LeicaMicrosystemsMetadata.doc;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class loads and represents a Leica Microsystems XML document that has
 * been extracted from a LIF file
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class LifXmlDocument extends LMSXmlDocument {

  public LifXmlDocument(String xml) {
    super(xml);
  }

  public List<LifImageXmlDocument> getImageXmlDocuments() {
    List<LifImageXmlDocument> imageXmlDocs = new ArrayList<LifImageXmlDocument>();
    
    if (doc == null)
      return null;

    Node rootElement = GetChildWithName(doc.getDocumentElement(), "Element");
    Node children = GetChildWithName(rootElement, "Children");
    for (int i = 0; i < children.getChildNodes().getLength(); i++) {
      Node child = children.getChildNodes().item(i);
      if (child.getNodeName().equals("Element")) {
        LifImageXmlDocument imageXml = new LifImageXmlDocument(child);
        imageXmlDocs.add(imageXml);
      }
    }

    return imageXmlDocs;
  }
}
