/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2012 Open Microscopy Environment:
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

/*-----------------------------------------------------------------------------
 *
 * Written by:    Curtis Rueden <ctrueden@wisc.edu>
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml;

import java.io.*;
import java.util.Vector;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.*;

/**
 * DOMUtil contains useful functions for traversing and manipulating a DOM.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-xml/src/ome/xml/DOMUtil.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-xml/src/ome/xml/DOMUtil.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public final class DOMUtil {

  // -- Constructor --

  private DOMUtil() { }

  // -- Static fields --

  /** Factory for generating transformers. */
  public static final TransformerFactory TRANS_FACT =
    TransformerFactory.newInstance();

  /** Factory for generating document builders. */
  public static final DocumentBuilderFactory DOC_FACT =
    DocumentBuilderFactory.newInstance();

  /** Factory for generating SAX parsers. */
  public static final SAXParserFactory SAX_FACT =
    SAXParserFactory.newInstance();

  // -- I/O and XSLT methods --

  /** Writes the specified DOM to the given output stream. */
  public static void writeXML(OutputStream os, Document doc)
    throws TransformerException
  {
    Transformer idTransform = TRANS_FACT.newTransformer();
    Source input = new DOMSource(doc);
    Result output = new StreamResult(os);
    idTransform.transform(input, output);
  }

  // -- Node methods --

  /** Gets the local (sans namespace) name of the given node. */
  public static String getName(Node node) {
    // NB: The node.getLocalName() method does not work.
    String name = node.getNodeName();
    int colon = name.lastIndexOf(":");
    return colon < 0 ? name : name.substring(colon + 1);
  }

  /** Gets the namespace of the given node. */
  public static String getNamespace(Node node) {
    String name = node.getNodeName();
    int colon = name.lastIndexOf(":");
    return colon < 0 ? null : name.substring(0, colon);
  }

  // -- Element methods --

  /** Gets the character data corresponding to the given DOM element. */
  public static String getCharacterData(Element el) {
    Text text = getChildTextNode(el);
    return text == null ? null : text.getData();
  }

  /** Sets the character data corresponding to the given DOM element. */
  public static void setCharacterData(String data, Element el) {
    Text text = getChildTextNode(el);
    if (text == null) {
      text = el.getOwnerDocument().createTextNode(data);
      el.appendChild(text);
    }
    else text.setData(data);
  }

  /**
   * Sets the character data corresponding to the given DOM element
   * to the specified Object's string representation.
   */
  public static void setCharacterData(Object data, Element el) {
    setCharacterData(data.toString(), el);
  }

  /**
   * Gets the character data corresponding to the given DOM element as
   * a Boolean, or null if the value is not a boolean.
   */
  public static Boolean getBooleanCharacterData(Element el) {
    return stringToBoolean(getCharacterData(el));
  }

  /**
   * Gets the character data corresponding to the given DOM element as
   * a Double, or null if the value is not a double.
   */
  public static Double getDoubleCharacterData(Element el) {
    return stringToDouble(getCharacterData(el));
  }

  /**
   * Gets the character data corresponding to the given DOM element as
   * a Float, or null if the value is not a float.
   */
  public static Float getFloatCharacterData(Element el) {
    return stringToFloat(getCharacterData(el));
  }

  /**
   * Gets the character data corresponding to the given DOM element as
   * a Integer, or null if the value is not a integer.
   */
  public static Integer getIntegerCharacterData(Element el) {
    return stringToInteger(getCharacterData(el));
  }

  /**
   * Gets the character data corresponding to the given DOM element as
   * a Long, or null if the value is not a long.
   */
  public static Long getLongCharacterData(Element el) {
    return stringToLong(getCharacterData(el));
  }

  /**
   * Gets the child text node containing character data
   * for the given DOM element.
   */
  public static Text getChildTextNode(Element el) {
    if (el == null) return null;
    NodeList list = el.getChildNodes();
    int size = list.getLength();
    for (int i=0; i<size; i++) {
      Node node = list.item(i);
      if (!(node instanceof Text)) continue;
      return (Text) node;
    }
    return null;
  }

  /**
   * Gets the given element's first child DOM element with the specified name.
   */
  public static Element getChildElement(String name, Element el) {
    if (name == null || el == null) return null;
    NodeList list = el.getChildNodes();
    int size = list.getLength();
    for (int i=0; i<size; i++) {
      Node node = list.item(i);
      if (!(node instanceof Element)) continue;
      if (name.equals(getName(node))) return (Element) node;
    }
    return null;
  }

  /** Gets a list of the given element's child DOM elements. */
  public static Vector getChildElements(Element el) {
    return getChildElements(null, el);
  }

  /**
   * Gets a list of the given element's child DOM elements
   * with the specified name, or all child elements if name is null.
   */
  public static Vector getChildElements(String name, Element el) {
    if (el == null) return null;
    Vector v = new Vector();
    NodeList list = el.getChildNodes();
    int size = list.getLength();
    String cName = ":" + name;
    for (int i=0; i<size; i++) {
      Node node = list.item(i);
      if (!(node instanceof Element)) continue;
      String nodeName = node.getNodeName();
      //if (name == null || name.equals(getName(node))) v.add(node);
      if (name == null || nodeName.equals(name) || nodeName.endsWith(cName)) {
        v.add(node);
      }
    }
    return v;
  }

  /**
   * Gets the given element's index-th child DOM element with the specified
   * name, or the index-th child element overall if name is null.
   */
  public static Element getChildElement(String name, Element el, int index) {
    if (el == null) return null;
    NodeList list = el.getChildNodes();
    int size = list.getLength();
    String cName = ":" + name;
    int q = 0;
    for (int i=0; i<size; i++) {
      Node node = list.item(i);
      if (!(node instanceof Element)) continue;
      String nodeName = node.getNodeName();
      if (name == null || nodeName.equals(name) || nodeName.endsWith(cName)) {
        if (q == index) return (Element) node;
        q++;
      }
    }
    return null;
  }

  /**
   * Gets the given element's first ancestor DOM element
   * with the specified name.
   */
  public static Element getAncestorElement(String name, Element el) {
    if (name == null || el == null) return null;
    Node parent = el.getParentNode();
    while (parent != null && !name.equals(getName(parent))) {
      parent = parent.getParentNode();
    }
    if (parent == null || (!(parent instanceof Element))) return null;
    return (Element) parent;
  }

  /** Finds the first (breadth first) DOM element with the specified name. */
  public static Element findElement(String name, Document doc) {
    return findElement(name, null, null, doc);
  }

  /**
   * Finds the first (breadth first) DOM element with the specified
   * name that has an attribute with the given name and value.
   */
  public static Element findElement(String name, String attrName,
    String attrValue, Document doc)
  {
    if (name == null) return null;
    NodeList list = doc.getElementsByTagName(name);
    int size = list.getLength();
    for (int i=0; i<size; i++) {
      Node node = list.item(i);
      if (!(node instanceof Element)) continue;
      Element el = (Element) node;
      if (attrName == null || attrValue == null ||
        attrValue.equals(getAttribute(attrName, el)))
      {
        return el;
      }
    }
    return null;
  }

  /**
   * Gets a list of DOM elements with the specified name throughout
   * the document (not just children of a specific element).
   */
  public static Vector findElementList(String name, Document doc) {
    return findElementList(name, null, null, doc);
  }

  /**
   * Gets a list of DOM elements with the specified name
   * that have an attribute with the given name and value.
   */
  public static Vector findElementList(String name, String attrName,
    String attrValue, Document doc)
  {
    if (name == null) return null;
    Vector v = new Vector();
    NodeList list = doc.getElementsByTagName(name);
    int size = list.getLength();
    for (int i=0; i<size; i++) {
      Node node = list.item(i);
      if (!(node instanceof Element)) continue;
      Element el = (Element) node;
      if (attrName == null || attrValue == null ||
        attrValue.equals(getAttribute(attrName, el)))
      {
        v.add(el);
      }
    }
    return v;
  }

  /**
   * Creates a child element with the given name beneath the specified element.
   */
  public static Element createChild(Element el, String name) {
    return createChild(el, name, true);
  }

  /**
   * Creates an element with the given name in the specified element's DOM,
   * inserting it into the tree structure as a child of that element
   * if the attach flag is set.
   */
  public static Element createChild(Element el, String name, boolean attach) {
    Element child = el.getOwnerDocument().createElement(name);
    if (attach) el.appendChild(child);
    return child;
  }

  // -- Attribute methods --

  /** Gets a list of all attribute names for the given DOM element. */
  public static String[] getAttributeNames(Element el) {
    NamedNodeMap map = el.getAttributes();
    int len = map.getLength();
    String[] attrNames = new String[len];
    for (int i=0; i<len; i++) {
      Attr attr = (Attr) map.item(i);
      attrNames[i] = attr == null ? null : attr.getName();
    }
    return attrNames;
  }

  /** Gets a list of all attribute values for the given DOM element. */
  public static String[] getAttributeValues(Element el) {
    NamedNodeMap map = el.getAttributes();
    int len = map.getLength();
    String[] attrValues = new String[len];
    for (int i=0; i<len; i++) {
      Attr attr = (Attr) map.item(i);
      attrValues[i] = attr == null ? null : attr.getValue();
    }
    return attrValues;
  }

  /**
   * Gets the value of the given DOM element's attribute
   * with the specified name.
   */
  public static String getAttribute(String name, Element el) {
    if (name == null || el == null) return null;
    if (!el.hasAttribute(name)) return null;
    return el.getAttribute(name);
  }

  /**
   * Sets the value of the given DOM element's attribute
   * with the specified name to the given value.
   */
  public static void setAttribute(String name, String value, Element el) {
    if (name == null || value == null || el == null) return;

    // strip out invalid characters from the value
    char[] v = value.toCharArray();
    int count = 0;
    for (int i=0; i<v.length; i++) {
      if (!Character.isISOControl(v[i])) count++;
    }
    if (count < v.length) {
      char[] nv = new char[count];
      count = 0;
      for (int i=0; i<v.length; i++) {
        if (!Character.isISOControl(v[i])) nv[count++] = v[i];
      }
      value = new String(nv);
    }

    el.setAttribute(name, value);
  }

  /**
   * Sets the value of the given DOM element's attribute with the
   * specified name to the given value's string representation.
   */
  public static void setAttribute(String name, Object value, Element el) {
    setAttribute(name, value == null ? null : value.toString(), el);
  }

  /**
   * Gets the value of the DOM element's attribute with the given name
   * as a Boolean, or null if the value is not a boolean.
   */
  public static Boolean getBooleanAttribute(String name, Element el) {
    return stringToBoolean(getAttribute(name, el));
  }

  /**
   * Gets the value of the DOM element's attribute with the given name
   * as a Double, or null if the value is not a double.
   */
  public static Double getDoubleAttribute(String name, Element el) {
    return stringToDouble(getAttribute(name, el));
  }

  /**
   * Gets the value of the DOM element's attribute with the given name
   * as a Float, or null if the value is not a float.
   */
  public static Float getFloatAttribute(String name, Element el) {
    return stringToFloat(getAttribute(name, el));
  }

  /**
   * Gets the value of the DOM element's attribute with the given name
   * as an Integer, or null if the value is not an integer.
   */
  public static Integer getIntegerAttribute(String name, Element el) {
    return stringToInteger(getAttribute(name, el));
  }

  /**
   * Gets the value of the DOM element's attribute with the given name
   * as a Long, or null if the value is not a long.
   */
  public static Long getLongAttribute(String name, Element el) {
    return stringToLong(getAttribute(name, el));
  }

  // -- Helper methods --

  private static Boolean stringToBoolean(String value) {
    if (value == null) return null;
    if (value.equalsIgnoreCase("true")) return Boolean.TRUE;
    else if (value.equalsIgnoreCase("false")) return Boolean.FALSE;
    else return null;
  }

  private static Double stringToDouble(String value) {
    if (value == null) return null;
    try { return new Double(value); }
    catch (NumberFormatException exc) { return null; }
  }

  private static Float stringToFloat(String value) {
    if (value == null) return null;
    try { return new Float(value); }
    catch (NumberFormatException exc) { return null; }
  }

  private static Integer stringToInteger(String value) {
    if (value == null) return null;
    try { return new Integer(value); }
    catch (NumberFormatException exc) { return null; }
  }

  private static Long stringToLong(String value) {
    if (value == null) return null;
    try { return new Long(value); }
    catch (NumberFormatException exc) { return null; }
  }

}
