//
// XMLUtil.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2004 Curtis Rueden.

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

package loci.visbio.util;

import java.io.*;
import java.util.Vector;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/** XMLUtil contains useful functions for manipulating DOMs. */
public abstract class XMLUtil {

  /** Document builder for creating DOMs. */
  protected static DocumentBuilder docBuilder;

  /** Creates a new DOM. */
  public static Document createDocument(String rootName) {
    if (docBuilder == null) {
      try {
        docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      }
      catch (ParserConfigurationException exc) {
        exc.printStackTrace();
        return null;
      }
    }
    Document doc = docBuilder.newDocument();
    if (rootName != null) doc.appendChild(doc.createElement(rootName));
    return doc;
  }

  /** Parses a DOM from the given XML file on disk. */
  public static Document parseXML(File file) {
    try { return parseXML(new FileInputStream(file)); }
    catch (FileNotFoundException exc) { exc.printStackTrace(); }
    return null;
  }

  /** Parses a DOM from the given XML string. */
  public static Document parseXML(String xml) {
    return parseXML(new ByteArrayInputStream(xml.getBytes()));
  }

  /** Parses a DOM from the given XML input stream. */
  public static Document parseXML(InputStream is) {
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      return db.parse(is);
    }
    catch (IOException exc) { exc.printStackTrace(); }
    catch (ParserConfigurationException exc) { exc.printStackTrace(); }
    catch (SAXException exc) { exc.printStackTrace(); }
    return null;
  }

  /** Writes the given DOM to the specified file on disk. */
  public static void writeXML(File file, Document doc) {
    try { writeXML(new FileOutputStream(file), doc); }
    catch (FileNotFoundException exc) { exc.printStackTrace(); }
  }

  /**
   * Writes the given DOM to a string.
   * @return The string to which the DOM was written.
   */
  public static String writeXML(Document doc) {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    writeXML(os, doc);
    return os.toString();
  }

  /** Writes the given DOM to the specified output stream. */
  public static void writeXML(OutputStream os, Document doc) {
    try {
      TransformerFactory transformFactory = TransformerFactory.newInstance();
      Transformer idTransform = transformFactory.newTransformer();
      Source input = new DOMSource(doc);
      Result output = new StreamResult(os);
      idTransform.transform(input, output);
    }
    catch (TransformerException exc) { exc.printStackTrace(); }
    // append newline to end of output
    try { os.write(System.getProperty("line.separator").getBytes()); }
    catch (IOException exc) { exc.printStackTrace(); }
  }

  /**
   * Appends a child element with the given name to the specified DOM element.
   */
  public static Element createChild(Element el, String name) {
    Element child = (Element) el.getOwnerDocument().createElement(name);
    el.appendChild(child);
    return child;
  }

  /**
   * Retrieves the given DOM element's first child element
   * with the specified name.
   */
  public static Element getFirstChild(Element el, String name) {
    NodeList nodes = el.getChildNodes();
    int len = nodes.getLength();
    for (int i=0; i<len; i++) {
      Node node = nodes.item(i);
      if (!(node instanceof Element)) continue;
      Element e = (Element) node;
      if (e.getTagName().equals(name)) return e;
    }
    return null;
  }

  /**
   * Retrieves the given DOM element's child elements
   * with the specified name.
   */
  public static Vector getChildren(Element el, String name) {
    Vector v = new Vector();
    NodeList nodes = el.getChildNodes();
    int len = nodes.getLength();
    for (int i=0; i<len; i++) {
      Node node = nodes.item(i);
      if (!(node instanceof Element)) continue;
      Element e = (Element) node;
      if (e.getTagName().equals(name)) v.add(e);
    }
    return v;
  }

}
