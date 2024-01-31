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

package loci.formats.in.LeicaMicrosystemsMetadata.extract;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import loci.common.DataTools;
import ome.units.quantity.Length;
import ome.units.unit.Unit;

/**
 * Extractor is a helper class for navigating, reading, parsing and printing XML node contents.
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class Extractor {

  /**
   * Returns the first direct child node with passed name that is found
   */
  public static Node getChildNodeWithName(Node node, String nodeName) {
    if (node == null || nodeName == null || nodeName.isEmpty()) return null;

    NodeList children = node.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      if (children.item(i).getNodeName().equals(nodeName))
        return children.item(i);
    }
    return null;
  }

  /**
   * Returns the first direct child node with passed name that is found and tries to cast it to Element
   */
  public static Element getChildNodeWithNameAsElement(Node node, String nodeName){
    Node child = getChildNodeWithName(node, nodeName);
    Element element = null;
    try {
      element = (Element)child;
    } catch (Exception e){}

    return element;
  }

  /**
   * Returns all direct child nodes with passed name and tries to cast them to Element
   */
  public static List<Element> getChildNodesWithNameAsElement(Node node, String nodeName){
    List<Element> children = new ArrayList<Element>();
    NodeList childNodes = node.getChildNodes();

    for (int i = 0; i < childNodes.getLength(); i++){
      Element element = null;
      try {
        element = (Element)childNodes.item(i);
        children.add(element);
      } catch (Exception e){}
    }

    return children;
  }

  /**
   * Returns the first node of a NodeList that has an attribute with a certain value 
   */
  public static Node getNodeWithAttribute(NodeList nodes, String attributeName, String attributeValue) {
    if (nodes == null) return null;
    
    for (int i = 0; i < nodes.getLength(); i++) {
      Node node = nodes.item(i);
      Node attribute = node.getAttributes().getNamedItem(attributeName);
      if (attribute != null && attribute.getTextContent().equals(attributeValue))
        return node;
    }
    return null;
  }

  /**
   * Returns the value of an attribute of a node
   */
  public static String getAttributeValue(Node node, String attributeName) {
    Node attribute = node.getAttributes().getNamedItem(attributeName);
    if (attribute != null)
      return attribute.getTextContent();
    else
      return "";
  }

  /**
   * Returns all (grand*n)children nodes with given node name
   * 
   * @param root
   *          root node
   * @param nodeName
   *          name of children that shall be searched
   * @return list of child nodes with given name
   */
  public static NodeList getDescendantNodesWithName(Element root, String nodeName) {
    NodeList nodes = root.getElementsByTagName(nodeName);
    if (nodes.getLength() == 0) {
      NodeList children = root.getChildNodes();
      for (int i = 0; i < children.getLength(); i++) {
        Object child = children.item(i);
        if (child instanceof Element) {
          NodeList childNodes = getDescendantNodesWithName((Element) child, nodeName);
          if (childNodes != null) {
            return childNodes;
          }
        }
      }
      return null;
    } else
      return nodes;
  }
  
  public static long parseLong(String value) {
    return value == null || value.trim().isEmpty() ? 0 : Long.parseLong(value.trim());
  }

  public static int parseInt(String value) {
    return value == null || value.trim().isEmpty() ? 0 : Integer.parseInt(value.trim());
  }

  public static double parseDouble(String value) {
    return value == null || value.trim().isEmpty() ? 0d : DataTools.parseDouble(value.trim());
  }

  public static Length parseLength(String value, Unit<Length> unit){
    double valueD = parseDouble(value);
    return new Length(valueD, unit);
  }

  /**
   * Prints the complete XML of a node
   */
  public static void printNode(Node node) {
    try {
      // Set up the output transformer
      TransformerFactory transfac = TransformerFactory.newInstance();
      Transformer trans = transfac.newTransformer();
      trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      trans.setOutputProperty(OutputKeys.INDENT, "yes");

      // Print the DOM node

      StringWriter sw = new StringWriter();
      StreamResult result = new StreamResult(sw);
      DOMSource source = new DOMSource(node);
      trans.transform(source, result);
      String xmlString = sw.toString();

      System.out.println(xmlString);
    } catch (TransformerException e) {
      e.printStackTrace();
    }
  }
}
