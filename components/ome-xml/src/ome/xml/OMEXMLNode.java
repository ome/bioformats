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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.Vector;
import org.w3c.dom.*;

// HACK: for scan-deps.pl: The following packages are not actually "optional":
// optional org.apache.log4j, optional org.slf4j.impl

/**
 * OMEXMLNode is the superclass of all OME-XML nodes. These nodes are
 * similar to, but more sophisticated than, the nodes obtained from a direct
 * DOM parse of an OME-XML file. Every OME-XML node is backed by a
 * corresponding DOM element from the directly parsed XML, but not all DOM
 * elements have a corresponding OME-XML node; some (such as FirstName within
 * Experimenter) are implicit within more significant OME-XML nodes (e.g.,
 * ExperimenterNode). In general, it is not guaranteed that all DOM elements
 * and attributes be exposed through subclasses of OMEXMLNode, though the
 * intent is for the OMEXMLNode infrastructure to be as complete as possible.
 *
 * Subclasses of OMEXMLNode provide OME-specific functionality such as more
 * intuitive traversal of OME structures.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-xml/src/ome/xml/OMEXMLNode.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-xml/src/ome/xml/OMEXMLNode.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public abstract class OMEXMLNode {

  // -- Constants --

  protected static final String[] NODE_PACKAGES = {".ome", ".spw", ".st", ""};

  protected static final String LEGACY_VERSION = "2003-FC";

  // -- Static fields --

  /** Next free ID numbers for generating internal ID attribute values. */
  protected static Hashtable nextIds = new Hashtable();

  /** Prefix used for generated internal ID values. */
  protected static String idPrefix = "openmicroscopy.org";

  /**
   * Table of existing nodes corresponding to known elements.
   * This table allows us to have one node instance per element object, and
   * reuse this node on demand, rather than creating a new one every time.
   */
  protected static Hashtable nodeHash = new Hashtable();

  /**
   * Table of existing classes for known node types.
   * This table allows us to avoid searching for a Class object corresponding
   * to a particular node type string within each schema version's package.
   */
  protected static Hashtable classHash = new Hashtable();

  /**
   * Table of existing constructor signatures corresponding to known
   * OMEXMLNode subclasses. This table allows us to avoid searching the
   * Class object for the appropriate constructor every time we need it.
   */
  protected static Hashtable constructorHash = new Hashtable();

  // -- Fields --

  /** Associated DOM element for this node. */
  protected Element element;

  /** Base package of this node. */
  protected String basePackage;

  // -- Constructor --

  /** Constructs an OME-XML node with the given associated DOM element. */
  public OMEXMLNode(Element element) {
    this.element = element;
    if (hasID() && getNodeID() == null) setNodeID(makeID(getElementName()));

    // determine base package

    //String pack = getClass().getPackage().getName();
    // NB: getPackage() returns null within ImageJ for some reason
    String className = getClass().getName();
    int dot = className.lastIndexOf(".");
    if (dot < 0) dot = 0;
    String pack = className.substring(0, dot);

    // strip off sub-package suffix, if any
    for (int i=0; i<NODE_PACKAGES.length; i++) {
      if (pack.endsWith(NODE_PACKAGES[i])) {
        pack = pack.substring(0, pack.length() - NODE_PACKAGES[i].length());
        break;
      }
    }
    basePackage = pack;
  }

  // -- Static utility methods --

  /** Gets the prefix used when generating internal ID values. */
  public static String getIDPrefix() { return idPrefix; }

  /** Sets the prefix used when generating internal ID values. */
  public static void setIDPrefix(String prefix) {
    String regex = "(\\S+\\.\\S+)+";
    if (!prefix.matches(regex)) {
      throw new IllegalArgumentException(
        "Prefix does not match regular expression: " + regex);
    }
    idPrefix = prefix;
  }

  /** Clears the cached nodes and resets node indices. */
  public static void clearCaches() {
    nextIds.clear();
    classHash.clear();
    nodeHash.clear();
    constructorHash.clear();
  }

  // -- OMEXMLNode API methods --

  /** Gets the DOM element backing this OME-XML node. */
  public Element getDOMElement() { return element; }

  /** Gets the name of the DOM element. */
  public String getElementName() { return DOMUtil.getName(element); }

  /** Gets whether this type of node should have an ID. */
  public abstract boolean hasID();

  /** Gets the ID for this node, or null if none. */
  public String getNodeID() { return getAttribute("ID"); }

  /** Sets the ID for this node. */
  public void setNodeID(String id) { setAttribute("ID", id); }

  /**
   * Gets the next available internal ID for use
   * within this OME-XML node's tree structure.
   */
  public String makeID(String nodeType) {
    Integer id = (Integer) nextIds.get(nodeType);
    int q = id == null ? 0 : id.intValue();
    nextIds.put(nodeType, new Integer(q + 1));
    String s;
    if (isLegacy()) s = idPrefix + ":" + nodeType + ":" + q;
    else s = nodeType + ":" + q;
    return s;
  }

  /** Gets the node's character data. */
  public String getCData() { return DOMUtil.getCharacterData(element); }

  /** Sets the node's character data. */
  public void setCData(String value) {
    DOMUtil.setCharacterData(value, element);
  }

  /** Gets whether the current OME-XML hierarchy is a legacy version. */
  public boolean isLegacy() {
    return getVersion().equals(LEGACY_VERSION);
  }

  /**
   * Gets the OME-XML schema version ("2003-FC", "2007-06", etc.)
   * for the current OME-XML hierarchy.
   */
  public String getVersion() {
    String name = getClass().getName();
    if (name.startsWith("org.openmicroscopy.xml.")) return LEGACY_VERSION;
    String prefix = "ome.xml.r";
    if (name.startsWith(prefix)) {
      int dot = name.indexOf(".", prefix.length());
      if (dot >= 0) {
        String numbers = name.substring(prefix.length(), dot);
        if (numbers.length() == 6) {
          return numbers.substring(0, 4) + "-" +
            numbers.substring(4, 6).toUpperCase();
        }
      }
    }
    return null; // unknown package
  }

  // -- Internal OMEXMLNode API methods --

  /** Gets the number of child elements with the given name. */
  protected int getChildCount(String name) {
    return getSize(DOMUtil.getChildElements(name, element));
  }

  /**
   * Gets an OME-XML node representing the first child with the given name.
   *
   * <b>NB: This method has public access only for legacy reasons,
   * and direct usage is discouraged.</b>
   */
  public OMEXMLNode getChildNode(String name) {
    return getNode(DOMUtil.getChildElement(name, element));
  }

  /**
   * Gets an OME-XML node of the specified type
   * representing the first child with the given name.
   */
  protected OMEXMLNode getChildNode(String nodeType, String name) {
    return createNode(nodeType, DOMUtil.getChildElement(name, element));
  }

  /**
   * Gets a list of OME-XML node children with the given name.
   *
   * <b>NB: This method has public access only for legacy reasons,
   * and direct usage is discouraged.</b>
   */
  public Vector getChildNodes(String name) {
    return getNodes(DOMUtil.getChildElements(name, element));
  }

  /** Gets the index-th OME-XML node child with the given name. */
  protected OMEXMLNode getChildNode(String name, int index) {
    return getNode(DOMUtil.getChildElement(name, element, index));
  }

  /**
   * Gets an OME-XML node representing the
   * first ancestor element with the given name.
   */
  protected OMEXMLNode getAncestorNode(String name) {
    return getNode(getAncestorElement(name));
  }

  /**
   * Gets an OME-XML node of the given type representing the first
   * element referenced by a child element with the given name.
   *
   * For example, if this node is an ImageNode,
   * getReferencedNode("Pixels", "AcquiredPixelsRef") will return a
   * PixelsNode for the Pixels element whose ID matches the one given
   * by the AcquiredPixelsRef child element.
   */
  protected OMEXMLNode getReferencedNode(String nodeType, String refName) {
    Element ref = getChildElement(refName);
    if (ref == null) return null;
    Element el = findElement(nodeType, DOMUtil.getAttribute("ID", ref));
    return getNode(el);
  }

  /**
   * Gets a list of all OME-XML nodes of the given type representing
   * the elements referenced by the child elements with the given name.
   *
   * For example, if this node is an ImageNode,
   * getReferencedNodes("Dataset", "DatasetRef") will return a list of
   * DatasetNode objects for the Dataset elements whose IDs match the
   * ones given by the DatasetRef child elements.
   */
  protected Vector getReferencedNodes(String nodeType, String refName) {
    Vector refs = getChildElements(refName);
    if (refs == null) return null;
    Vector els = new Vector();
    for (int i=0; i<refs.size(); i++) {
      Element ref = (Element) refs.get(i);
      Element el = findElement(nodeType, DOMUtil.getAttribute("ID", ref));
      els.add(el);
    }
    return getNodes(els);
  }

  /**
   * Gets an OME-XML node of the given type representing the first
   * element referenced by an attribute with the given name.
   *
   * For example, if this node is an ImageNode,
   * getAttrReferencedNode("Pixels", "DefaultPixels") will return a
   * PixelsNode for the Pixels element whose ID matches the one given
   * by the DefaultPixels attribute.
   */
  protected OMEXMLNode getAttrReferencedNode(String nodeType, String attrName) {
    Element el = findElement(nodeType, getAttribute(attrName));
    return getNode(el);
  }

  /**
   * Sets the OME-XML referenced by the attribute with the given name.
   *
   * For example, if this node is an ImageNode,
   * setAttrReferencedNode(pixelsNode, "DefaultPixels") will set the
   * DefaultPixels attribute value to match the given PixelsNode's ID.
   */
  protected void setAttrReferencedNode(OMEXMLNode node, String attrName) {
    if (node == null || attrName == null) return;
    String id = DOMUtil.getAttribute("ID", node.getDOMElement());
    if (id != null) setAttribute(attrName, id);
  }

  /**
   * Gets the number of elements of a certain type (with the given name)
   * that reference this OME-XML node using a *Ref child element.
   */
  protected int getReferringCount(String name) {
    return getReferringCount(name, getElementName() + "Ref");
  }

  /**
   * Gets the number of elements of a certain type (with the given name)
   * that reference this OME-XML node using a child element with name refName.
   */
  protected int getReferringCount(String name, String refName) {
    return getSize(findReferringElements(name, refName, getNodeID()));
  }

  /**
   * Gets a list of nodes of a certain type (with the given name)
   * that reference this OME-XML node using a *Ref child element.
   *
   * For example, if this node is a Project node and
   * getReferringNodes("Dataset") is called, it will search the DOM structure
   * for Datasets with child ProjectRefs element whose IDs match this Project
   * node's ID value.
   */
  protected Vector getReferringNodes(String name) {
    return getReferringNodes(name, getElementName() + "Ref");
  }

  /**
   * Gets a list of nodes of a certain type (with the given name)
   * that refer to this OME-XML node using a child element with name refName.
   *
   * For example, if this node is an Experimenter node and
   * getReferringNodes("ExperimenterGroup", "Contact") is called, it will
   * search the DOM structure for ExperimenterGroups with child Contact
   * elements whose IDs match this Experimenter node's ID value.
   */
  protected Vector getReferringNodes(String name, String refName) {
    return getNodes(findReferringElements(name, refName, getNodeID()));
  }

  /**
   * Gets the number of elements of a certain type (with the given name)
   * that reference this OME-XML node using an attribute.
   */
  protected int getAttrReferringCount(String name, String attrName) {
    return getSize(DOMUtil.findElementList(name,
      attrName, getNodeID(), element.getOwnerDocument()));
  }

  /**
   * Gets a list of nodes of a certain type (with the given name)
   * that refer to this OME-XML node using an attribute.
   *
   * For example, if this node is a PixelsNode and
   * getAttrReferringNodes("ChannelComponent", "Pixels") is called, it will
   * search the DOM structure for ChannelComponents with Pixels
   * attributes whose values match this Pixels node's ID value.
   */
  protected Vector getAttrReferringNodes(String name, String attrName) {
    return getNodes(DOMUtil.findElementList(name,
      attrName, getNodeID(), element.getOwnerDocument()));
  }

  /**
   * Creates a reference element beneath this node.
   *
   * For example, if this node is a DatasetNode and "project" is a ProjectNode,
   * createReference(project) references the Project from this Dataset by
   * adding a child XML element called ProjectRef with an ID attribute matching
   * the ID of the referenced Project.
   */
  protected void createReference(OMEXMLNode node) {
    Element ref = DOMUtil.createChild(element, node.getElementName() + "Ref");
    DOMUtil.setAttribute("ID", node.getNodeID(), ref);
  }

  /** Gets the given child node's character data. */
  protected String getCData(String name) {
    return DOMUtil.getCharacterData(getChildElement(name));
  }

  /**
   * Gets the given child node's character data.
   * @see #getCData(String)
   */
  protected String getStringCData(String name) {
    return getCData(name);
  }

  /**
   * Sets the given child node's character data to the specified value,
   * creating the child node if necessary.
   */
  protected void setCData(String name, String value) {
    DOMUtil.setCharacterData(value, getChildElement(name, true));
  }

  /**
   * Sets the given child node's character data
   * to the specified Object's string representation,
   * creating the child node if necessary.
   */
  protected void setCData(String name, Object value) {
    DOMUtil.setCharacterData(value, getChildElement(name, true));
  }

  /**
   * Gets the given child node's character data as a Boolean,
   * or null if the value is not a boolean.
   */
  protected Boolean getBooleanCData(String name) {
    return DOMUtil.getBooleanCharacterData(getChildElement(name));
  }

  /**
   * Gets the given child node's character data as a Double,
   * or null if the value is not a double.
   */
  protected Double getDoubleCData(String name) {
    return DOMUtil.getDoubleCharacterData(getChildElement(name));
  }

  /**
   * Gets the given child node's character data as a Float,
   * or null if the value is not a float.
   */
  protected Float getFloatCData(String name) {
    return DOMUtil.getFloatCharacterData(getChildElement(name));
  }

  /**
   * Gets the given child node's character data as a Integer,
   * or null if the value is not an integer.
   */
  protected Integer getIntegerCData(String name) {
    return DOMUtil.getIntegerCharacterData(getChildElement(name));
  }

  /**
   * Gets the given child node's character data as a Long,
   * or null if the value is not a long.
   */
  protected Long getLongCData(String name) {
    return DOMUtil.getLongCharacterData(getChildElement(name));
  }

  /** Gets a list of all DOM element attribute names. */
  protected String[] getAttributeNames() {
    return DOMUtil.getAttributeNames(element);
  }

  /** Gets a list of all DOM element attribute values. */
  protected String[] getAttributeValues() {
    return DOMUtil.getAttributeValues(element);
  }

  /**
   * Sets the value of the DOM element's attribute with the given name
   * to the specified value.
   *
   * <b>NB: This method has public access only for legacy reasons,
   * and direct usage is discouraged.</b>
   */
  public void setAttribute(String name, String value) {
    DOMUtil.setAttribute(name, value, element);
  }

  /**
   * Sets the value of the DOM element's attribute with the
   * given name to the specified Object's string representation.
   */
  protected void setAttribute(String name, Object value) {
    DOMUtil.setAttribute(name, value, element);
  }

  /**
   * Gets the value of the DOM element's attribute with the given name.
   *
   * <b>NB: This method has public access only for legacy reasons,
   * and direct usage is discouraged.</b>
   */
  public String getAttribute(String name) {
    return DOMUtil.getAttribute(name, element);
  }

  /**
   * Gets the value of the DOM element's attribute with the given name
   * as a Boolean, or null if the value is not a boolean.
   */
  protected Boolean getBooleanAttribute(String name) {
    return DOMUtil.getBooleanAttribute(name, element);
  }

  /**
   * Gets the value of the DOM element's attribute with the given name
   * as a Double, or null if the value is not a double.
   */
  protected Double getDoubleAttribute(String name) {
    return DOMUtil.getDoubleAttribute(name, element);
  }

  /**
   * Gets the value of the DOM element's attribute with the given name
   * as a Float, or null if the value is not a float.
   */
  protected Float getFloatAttribute(String name) {
    return DOMUtil.getFloatAttribute(name, element);
  }

  /**
   * Gets the value of the DOM element's attribute with the given name
   * as an Integer, or null if the value is not an integer.
   */
  protected Integer getIntegerAttribute(String name) {
    return DOMUtil.getIntegerAttribute(name, element);
  }

  /**
   * Gets the value of the DOM element's attribute with the given name
   * as a Long, or null if the value is not a long.
   */
  protected Long getLongAttribute(String name) {
    return DOMUtil.getLongAttribute(name, element);
  }

  /** Gets the value of the DOM element's attribute with the given name. */
  protected String getStringAttribute(String name) {
    return DOMUtil.getAttribute(name, element);
  }

  /** Gets the OME-XML node corresponding to the specified DOM element. */
  protected OMEXMLNode getNode(Element el) {
    if (el == null) return null;
    OMEXMLNode node = (OMEXMLNode) nodeHash.get(el);
    if (node == null) {
      node = createNode(DOMUtil.getName(el), el);
      nodeHash.put(el, node);
    }
    return node;
  }

  // -- Helper methods --

  /**
   * Creates an OME-XML node of the given type,
   * using the specified DOM element as a source.
   */
  private OMEXMLNode createNode(String nodeType, Element el) {
    if (nodeType == null || el == null) return null;
    Class c = getClass(nodeType);
    if (c == null) return new CustomNode(el);
    return createNode(c, el);
  }

  /**
   * Creates an OME-XML node of the given type,
   * using the specified DOM element as a source.
   */
  private OMEXMLNode createNode(Class nodeType, Element el) {
    if (nodeType == null || el == null) return null;

    // construct a new instance of the given OMEXMLNode subclass
    try {
      Constructor con = (Constructor) constructorHash.get(nodeType);
      if (con == null) {
        con = nodeType.getConstructor(new Class[] {Element.class});
        constructorHash.put(nodeType, con);
      }
      return (OMEXMLNode) con.newInstance(new Object[] {el});
    }
    catch (IllegalAccessException exc) { }
    catch (InstantiationException exc) { }
    catch (InvocationTargetException exc) { }
    catch (NoSuchMethodException exc) { }
    return null;
  }

  /**
   * Gets a list of OME-XML nodes of the proper types,
   * using the specified list of DOM elements as a source.
   */
  private Vector getNodes(Vector els) {
    if (els == null) return null;
    int size = els.size();
    Vector nodes = new Vector(size);
    for (int i=0; i<size; i++) {
      Object o = (Object) els.elementAt(i);
      OMEXMLNode node = null;
      if (o instanceof Element) {
        Element el = (Element) o;
        node = getNode(el);
      }
      nodes.add(node);
    }
    return nodes;
  }

  /** Gets the first child DOM element with the specified name. */
  private Element getChildElement(String name) {
    return getChildElement(name, false);
  }

  /**
   * Gets the first child DOM element with the specified name,
   * creating it if it does not exist and the create flag is set.
   */
  private Element getChildElement(String name, boolean create) {
    Element el = DOMUtil.getChildElement(name, element);
    if (el == null && create) el = DOMUtil.createChild(element, name);
    return el;
  }

  /** Gets a list of child DOM elements with the specified name. */
  private Vector getChildElements(String name) {
    return DOMUtil.getChildElements(name, element);
  }

  /** Gets the first ancestor DOM element with the specified name. */
  protected Element getAncestorElement(String name) {
    return DOMUtil.getAncestorElement(name, element);
  }

  /** Finds the DOM element with the specified name and ID attribute value. */
  private Element findElement(String name, String id) {
    if (name == null || id == null) return null;
    return DOMUtil.findElement(name, "ID", id, element.getOwnerDocument());
  }

  /**
   * Gets a list of elements of a certain type (with the given name)
   * that refer to the element with the specified ID using a child element
   * (with name refName).
   */
  private Vector findReferringElements(String name, String refName, String id) {
    if (name == null || refName == null || id == null) return null;
    Vector possible = DOMUtil.findElementList(name, element.getOwnerDocument());
    if (possible == null) return null;

    Vector v = new Vector();
    int psize = possible.size();
    for (int i=0; i<psize; i++) {
      Element el = (Element) possible.elementAt(i);
      Vector refs = DOMUtil.getChildElements(refName, el);
      int rsize = refs.size();
      boolean match = false;
      for (int j=0; j<rsize; j++) {
        Element ref = (Element) refs.elementAt(j);
        if (id.equals(DOMUtil.getAttribute("ID", ref))) {
          match = true;
          break;
        }
      }
      if (match) v.add(el);
    }
    return v;
  }

  /**
   * Gets the node class based on the given name from the class loader.
   * @return null if the class could not be loaded.
   */
  private Class getClass(String nodeName) {
    String hashName = basePackage + ":" + nodeName;
    Class c = (Class) classHash.get(hashName);
    if (c != null) return c;

    // find class among sub-packages
    for (int i=0; i<NODE_PACKAGES.length; i++) {
      String subPack = basePackage + NODE_PACKAGES[i];
      String name = subPack + "." + nodeName + "Node";
      try {
        c = Class.forName(name);
        classHash.put(hashName, c);
        return c;
      }
      catch (ClassNotFoundException exc) { }
      catch (NoClassDefFoundError err) { }
      catch (RuntimeException exc) {
        // HACK: workaround for bug in Apache Axis2
        String msg = exc.getMessage();
        if (msg != null && msg.indexOf("ClassNotFound") < 0) throw exc;
      }
    }
    return null;
  }

  /** Gets the size of the vector. Returns 0 if the vector is null. */
  private int getSize(Vector v) { return v == null ? 0 : v.size(); }

  // -- Object API methods --

  /**
   * Tests whether two OME-XML nodes are equal. Nodes are considered equal if
   * they are instances of the same class with the same backing DOM element.
   */
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (!obj.getClass().equals(getClass())) return false;
    return element.equals(((OMEXMLNode) obj).element);
  }

  /** Gets a string representation of this node. */
  public String toString() { return element.toString(); }

  // -- Deprecated methods --

  /**
   * @deprecated <b>NB: This method exists only for legacy reasons,
   *   and usage is discouraged.</b>
   */
  public Vector getChildNodes() {
    return getNodes(DOMUtil.getChildElements(null, element));
  }

}
