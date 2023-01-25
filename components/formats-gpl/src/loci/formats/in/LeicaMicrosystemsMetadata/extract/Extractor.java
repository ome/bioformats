package loci.formats.in.LeicaMicrosystemsMetadata.extract;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import loci.common.DataTools;

public class Extractor {
  public static Node getChildNodeWithName(Node node, String nodeName) {
    NodeList children = node.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      if (children.item(i).getNodeName().equals(nodeName))
        return children.item(i);
    }
    return null;
  }

  public static Node getNodeWithAttribute(NodeList nodes, String attributeName, String attributeValue) {
    for (int i = 0; i < nodes.getLength(); i++) {
      Node node = nodes.item(i);
      Node attribute = node.getAttributes().getNamedItem(attributeName);
      if (attribute != null && attribute.getTextContent().equals(attributeValue))
        return node;
    }
    return null;
  }

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
}
