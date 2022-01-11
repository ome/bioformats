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

package loci.formats.in.LeicaMicrosystemsMetadata;

import org.w3c.dom.Node;

/**
 * This class loads and represents a Leica Microsystems XML document that has been extracted from a LOF file
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class LofXmlDocument extends LMSImageXmlDocument {
  private String name;

  public LofXmlDocument(String xml, String name) {
    super(xml);
    this.name = name;
  }

  @Override
  public Node getImageNode() {
    Node child = GetChildWithName(doc.getDocumentElement(), "Image");
    if (child != null) return child;
    child = GetChildWithName(doc.getDocumentElement(), "Element");
    if (child != null){
      Node elementChild = GetChildWithName(child, "Image");
      if (elementChild != null) return elementChild;
      elementChild = GetChildWithName(child, "Data");
      if (elementChild != null){
        Node dataChild = GetChildWithName(elementChild, "Image");
        if (dataChild != null) return dataChild;
      }
    }
    return null;
  }

  @Override
  public String getImageName(){
    return name;
  }
}
