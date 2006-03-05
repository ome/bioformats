//
// OMETools.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

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

package loci.formats;

import java.lang.reflect.Method;
import java.util.Vector;

/**
 * A utility class for constructing and manipulating OME-XML DOMs. It uses
 * reflection to access the org.w3c.dom and loci.ome.xml packages so that the
 * class compiles with Java 1.2, or if the loci.ome.xml package is unavailable.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public abstract class OMETools {

  // -- Constants --

  private static final String HEADER = "<?xml version = \"1.0\"?>\n" +
    "<OME xmlns = \"http://www.openmicroscopy.org/XMLschemas/OME/FC/" +
    "ome.xsd\"\n" +
    "xmlns:STD = \"http://www.openmicroscopy.org/XMLschemas/STD/RC2/" +
    "STD.xsd\"\n" +
    "xmlns:Bin = \"http://www.openmicroscopy.org/XMLschemas/BinaryFile/RC1/" +
    "BinaryFile.xsd\"\n" +
    "xmlns:xsi = \"http://www.w3.org/2001/XMLSchema-instance\"\n" +
    "xsi:schemaLocation = \"http://www.openmicroscopy.org/XMLschemas/OME/FC/" +
    "ome.xsd http://www.openmicroscopy.org/XMLschemas/OME/FC/ome.xsd " +
    "http://www.openmicroscopy.org/XMLschemas/STD/RC2/STD.xsd " +
    "http://www.openmicroscopy.org/XMLschemas/STD/RC2/STD.xsd\">\n" +
    "  <Experimenter/>\n" +
    "  <Group/>\n" +
    "  <Instrument/>\n" +
    "  <Image>\n" +
    "    <Pixels>\n" +
    "      <DisplayOptions/>\n" +
    "    </Pixels>\n" +
    "    <ChannelInfo/>\n" +
    "    <StageLabel/>\n" +
    "  </Image>\n" +
    "</OME>\n";

  private static final ReflectedUniverse R = createUniverse();

  private static ReflectedUniverse createUniverse() {
    ReflectedUniverse r = new ReflectedUniverse();
    try {
      r.exec("import loci.ome.xml.OMENode");
      r.exec("import loci.ome.xml.OMEXMLNode");
      r.exec("import loci.ome.xml.DOMUtil");
      r.setVar("FALSE", false);
    }
    catch (Throwable t) { r = null; }
    return r;
  }


  // -- Static fields --

  private static int lsid = 1;


  // -- OMETools API methods --

  /** Constructs a new OME-XML root node. */
  public static Object createRoot() {
    return createRoot(HEADER);
  }

  /** Constructs a new OME-XML root node with the given XML block. */
  public static Object createRoot(String xml) {
    if (R == null || xml == null) return null;
    try {
      R.setVar("xml", xml);
      return R.exec("new OMENode(xml)");
    }
    catch (ReflectException exc) { }
    return null;
  }

  /** Get number of nodes with the specified name. */
  public static int getNumNodes(Object root, String nodeName) {
    if (R == null || root == null || nodeName == null) return 0;
    try {
      Vector el = getNodes(root, nodeName);
      if (el != null) return el.size();
      return 0;
    }
    catch (Exception exc) {
      exc.printStackTrace();
    }
    return 0;
  }

  /**
   * Gets the value of the specified attribute in the first occurence of the
   * specified node.
   * @return the value of the attribute.
   */
  public static String getAttribute(Object root,
    String nodeName, String name)
  {
    return getAttribute(root, nodeName, name, 0);
  }

  /**
   * Gets the value of the specified attribute in the specified node, where n
   * specifies which occurence of the node to look at.
   * @return the value of the attribute.
   */
  public static String getAttribute(Object root, String nodeName,
    String name, int n)
  {
    if (R == null || root == null) return null;
    R.setVar("root", root);
    try {
      // get the node
      Object node = findNode(root, nodeName, n);
      if (node == null) return null;

      // get the attribute
      R.setVar("node", node);
      R.setVar("name", name);
      R.exec("attr = node.getAttribute(name)");
      String attr = ((String) R.getVar("attr"));
      return attr;
    }
    catch (ReflectException exc) { exc.printStackTrace(); }
    return null;
  }

  /**
   * Sets the value of the specified attribute in the specified node.
   * @return True if the operation was successful.
   */
  public static boolean setAttribute(Object root,
    String nodeName, String name, String value)
  {
    if (value == null || value.equals("null")) value = "";
    if (R == null || root == null) return false;
    R.setVar("root", root);
    try {
      // get the node
      Object node = findNode(root, nodeName);
      if (node == null) return false;

      // set the LSID of the node
      R.setVar("node", node);
      R.setVar("lsid", "" + lsid++);
      R.exec("node.setLSID(lsid)");

      // set the attribute
      R.setVar("name", name);
      R.setVar("value", value);
      R.exec("node.setAttribute(name, value)");

      return true;
    }
    catch (ReflectException exc) { exc.printStackTrace(); }
    return false;
  }

  /** Dumps the given OME-XML DOM tree to a string. */
  public static String dumpXML(Object root) {
    if (root == null) return null;
    R.setVar("root", root);
    try {
      Object s = R.exec("root.writeOME(FALSE)");
      if (s instanceof String) return (String) s;
    }
    catch (ReflectException exc) { }
    return null;
  }


  // -- Helper methods --

  /** Retrieves the first node associated with the given DOM element name. */
  private static Object findNode(Object root, String name)
    throws ReflectException
  {
    return findNode(root, name, 0);
  }

  /** Retrieves the nth node associated with the given DOM element name. */
  private static Object findNode(Object root, String name, int n)
    throws ReflectException
  {
    Vector el = getNodes(root, name);
    if (el == null || n >= el.size()) return null;
    R.setVar("el", el.get(n));
    return R.exec("OMEXMLNode.createNode(el)");
  }

  /**
   * Retrieves a Vector of nodes associated with the given DOM element name.
   */
  private static Vector getNodes(Object root, String name)
    throws ReflectException
  {
    if (R == null || root == null || name == null) return null;
    R.setVar("root", root);
    R.setVar("name", name);
    R.exec("rel = root.getDOMElement()");

    // HACK - We cannot call getOwnerDocument even though it is public:
    //
    //   java.lang.IllegalAccessException: Class loci.formats.ReflectedUniverse
    //   can not access a member of class org.apache.crimson.tree.NodeBase with
    //   modifiers "public"
    //
    // It seems the getOwnerDocument method of
    // org.apache.crimson.tree.NodeBase, which implements org.w3c.dom.Element,
    // is not accessible for some reason. So we have to grab the method
    // directly from org.w3c.dom.Element using reflection the hard way.
    Object rel = R.getVar("rel");
    try {
      Class c = Class.forName("org.w3c.dom.Element");
      Method m = c.getMethod("getOwnerDocument", null);
      R.setVar("doc", m.invoke(rel, null));
    }
    catch (Exception exc) { exc.printStackTrace(); }

    R.exec("el = DOMUtil.findElementList(name, doc)");
    return ((Vector) R.getVar("el"));
  }
}
