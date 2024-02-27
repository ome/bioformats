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

import loci.formats.in.LeicaMicrosystemsMetadata.extract.Extractor;

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
    imageXmlDocs.addAll(GetImageChildren(rootElement, ""));

    return imageXmlDocs;
  }

  /**
   * Creates LifImageXmlDocuments for the passed node (if it is an image) or for its descendants (if it is not)
   * @param elementNode
   * @return
   */
  private List<LifImageXmlDocument> GetImageChildren(Node elementNode, String parentPath){
    List<LifImageXmlDocument> imageXmlDocs = new ArrayList<LifImageXmlDocument>();
    
    Node data = GetChildWithName(elementNode, "Data");
    if (data != null){
      //Element is Experiment or Image Element
      Node image = GetChildWithName(data, "Image");
      if (image != null){
        //Element is Image Element
        imageXmlDocs.add(new LifImageXmlDocument(elementNode, parentPath));
      }
    } 
    
    Node children = GetChildWithName(elementNode, "Children");
    if (children != null){
      //Element is Experiment or Collection Element
      //only attach name to path if element is a collection
      Node experiment = GetChildWithName(data, "Experiment");
      if (experiment == null)
        parentPath += Extractor.getAttributeValue(elementNode, "Name") + "/";

      for (int i = 0; i < children.getChildNodes().getLength(); i++) {
        Node child = children.getChildNodes().item(i);
        if (child.getNodeName().equals("Element")) {
          imageXmlDocs.addAll(GetImageChildren(child, parentPath));
        }
      }
    }

    return imageXmlDocs;
  }
}
