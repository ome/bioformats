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

package loci.formats.in.LeicaMicrosystemsMetadata.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import loci.common.Location;
import loci.formats.in.LeicaMicrosystemsMetadata.LMSFileReader;

import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * This class loads and represents a Leica Microsystems XML document (from
 * file or xml string)
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public abstract class LMSXmlDocument {

  // -- Fields --
  private boolean isValid = false;
  protected Document doc;
  protected XPath xPath;
  protected String dir;
  protected String filepath;
  protected static final Logger LOGGER = LoggerFactory.getLogger(LMSXmlDocument.class);
  
  public enum InitFrom {
    XML,
    FILEPATH
  }
  LMSCollectionXmlDocument parent;

  // -- Constructors --
  public LMSXmlDocument(String xml) {
    initFromXmlString(xml);
  }

  public LMSXmlDocument(String filepath, LMSCollectionXmlDocument parent){
    initFromFilepath(filepath);
    this.parent = parent;
  }

  public LMSXmlDocument(Node root){
    initFromNode(root);
  }

  // -- Getters --
  public Document getDoc() {
    return this.doc;
  }

  public String getFilepath() {
    return filepath;
  }

  // -- Methods --
  private void initFromXmlString(String xml) {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.parse(new InputSource(new StringReader(xml)));
      doc.getDocumentElement().normalize();
      init(doc);
    } catch (ParserConfigurationException | SAXException | IOException e) {
      LMSFileReader.log.error("LMSXmlDocument: XML document could not be parsed.");
    }
  }

  private void initFromFilepath(String filepath) {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder db = dbf.newDocumentBuilder();
      FileInputStream fi = new FileInputStream(Location.getMappedId(filepath));
      Document doc = db.parse(fi);
      fi.close();
      doc.getDocumentElement().normalize();
      init(doc);
    } catch (ParserConfigurationException | SAXException | IOException e) {
      LMSFileReader.log.error("LMSXmlDocument: XML document could not be read.");
    }

    this.dir = Paths.get(filepath).getParent().toString();
    this.filepath = Paths.get(filepath).normalize().toString();
  }

  private void initFromNode(Node root){
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    try {
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.newDocument();
      Node importedNode = doc.importNode(root, true);
      doc.appendChild(importedNode);
      init(doc);
    } catch (Exception e){
      LMSFileReader.log.error("LMSXmlDocument: XML document could not be created from node.");
    }
  }

  private void init(Document doc){
    this.doc = doc;
    this.xPath = XPathFactory.newInstance().newXPath();
    isValid = true;
    LMSFileReader.log.trace(this.toString());
  }

  /**
   * Searches the XML document for expressions using xPath.
   * 
   * @param expression expression in xPath syntax
   * @return list of found nodes
   */
  public NodeList xPath(String expression) {
    try {
      return (NodeList) this.xPath.compile(expression).evaluate(this.doc, XPathConstants.NODESET);
    } catch (Exception e) {
      LMSFileReader.log.error(e.getMessage());
      return null;
    }
  }

  /**
   * Searches a node for expressions using xPath.
   * 
   * @param node       node in which to search
   * @param expression expression in xPath syntax
   * @return list of found nodes
   */
  public NodeList xPath(Node node, String expression) {
    try {
      return (NodeList) this.xPath.compile(expression).evaluate(node, XPathConstants.NODESET);
    } catch (Exception e) {
      LMSFileReader.log.error(e.getMessage());
      return null;
    }
  }

  public String toString() {
    StringWriter writer = new StringWriter();
    TransformerFactory tf = TransformerFactory.newInstance();
    try {
      Transformer transformer = tf.newTransformer();
      transformer.transform(new DOMSource(this.doc), new StreamResult(writer));
      return writer.getBuffer().toString();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  // -- Helper methods --
  /**
   * Returns the value of an attribute of a node, if existing
   * 
   * @param node     node in which to search for the attribute
   * @param attrName name of the attribute
   * @return
   */
  protected String getAttr(Node node, String attrName) {
    Node item = node.getAttributes().getNamedItem(attrName);
    if (item != null) {
      return item.getTextContent();
    }
    return null;
  }

  /**
   * Translates Leica XML file paths to absolute url decoded paths
   * 
   * @param refPath path string found in Leica XML reference
   * @return absolute, url decoded path
   */
  protected String parseFilePath(String refPath) {
    String urlDecoded = "";
    try {
      urlDecoded = java.net.URLDecoder.decode(refPath, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      urlDecoded = refPath.replace("%5C", File.separator).replace("%5c", File.separator).replace("%20", " ");
    }
    urlDecoded = urlDecoded.replace('\\', File.separatorChar);
    urlDecoded = urlDecoded.replaceAll("/", File.separator);
    String path = dir + File.separator + urlDecoded;
    path = Paths.get(path).normalize().toString();
    LOGGER.info("Parsed path: " + path);
    return Location.getMappedId(path);
  }

  public String nodeToString(Node node){
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer;
    try {
      transformer = tf.newTransformer();
      StringWriter writer = new StringWriter();
      transformer.transform(new DOMSource(node), new StreamResult(writer));
      String xmlString = writer.getBuffer().toString();   
      return (xmlString);
    } 
    catch (Exception e) 
    {
        e.printStackTrace();
    }
    return null;
  }

  protected Node GetChildWithName(Node node, String name){
    for (int i = 0; i < node.getChildNodes().getLength(); i++) {
      Node child = node.getChildNodes().item(i);
      if (child.getNodeName().equals(name)) {
        return child;
      }
    }
    return null;
  }

   /**
   * Returns all files which directly or indirectly reference this LMS xml file.
   * @return list of LMS xml documents (xlef and optionally xlcfs)
   */
  public List<String> getParentFiles(){
    List<String> parents = new ArrayList<>();
    if (parent != null){
      parents.add(parent.filepath);
      parents.addAll(parent.getParentFiles());
    }
    return parents;
  }

  public boolean isValid(){
    return isValid;
  }
  
  /**
   * Checks if file at path exists and corrects paths to be case sensitive
   * 
   * @param path whole file path
   */
  protected static String fileExists(String path) {
    HashMap<String, Object> idMap = Location.getIdMap();
    if (idMap != null) {
      for (String key: idMap.keySet()) {
        if (key.equalsIgnoreCase(path)) path = idMap.get(key).toString();
      }
    }
    if (path != null && !path.trim().isEmpty()) {
      try {
        path = Location.getMappedId(path);
        File f = new File(path);
        if (f.exists()) {
          return path;
        }
        String parent = fileExists(f.getParent());
        if (parent != null) {
         f = new File(parent + File.separator + f.getName());
         for (File file: f.getParentFile().listFiles()) {
           if (file.getName().toLowerCase().equals(f.getName().toLowerCase())) {
             return parent + File.separator + file.getName();
           }
         }
          
        }
      } catch (Exception e) {
        return null;
      }
    }
    return null;
  }
}
