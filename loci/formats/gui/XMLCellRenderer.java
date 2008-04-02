//
// XMLCellRenderer.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;

/**
 * A tree cell renderer for display syntax highlighted XML in a JTree.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/gui/XMLCellRenderer.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/gui/XMLCellRenderer.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class XMLCellRenderer extends DefaultTreeCellRenderer {

  private static final String ELEMENT_STYLE_START =
    "<font color=\"#7f007f\"><b>";
  private static final String ELEMENT_STYLE_END = "</b></font>";
  private static final String ATTR_NAME_STYLE_START = "<b>";
  private static final String ATTR_NAME_STYLE_END = "</b>";
  private static final String ATTR_VALUE_STYLE_START = "<font color=\"blue\">";
  private static final String ATTR_VALUE_STYLE_END = "</font>";
  private static final String COMMENT_STYLE_START = "<font color=\"green\"><i>";
  private static final String COMMENT_STYLE_END = "</i></font>";

  // -- XMLCellRenderer API methods --

  /** Constructs a Swing JTree that displays the given XML DOM document. */
  public static JTree makeJTree(Document doc) {
    Element rootNode = doc.getDocumentElement();
    DefaultMutableTreeNode rootTreeNode = makeTreeNode(rootNode);
    JTree tree = new JTree(rootTreeNode);
    tree.setCellRenderer(new XMLCellRenderer());
    tree.setRowHeight(0);
    return tree;
  }

  // -- TreeCellRenderer API methods --

  /*
   * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(JTree,
   *   Object, boolean, boolean, boolean, int, boolean
   */
  public Component getTreeCellRendererComponent(JTree tree, Object value,
    boolean selected, boolean expanded, boolean leaf, int row,
    boolean hasFocus)
  {
    Component c = super.getTreeCellRendererComponent(tree,
      value, selected, expanded, leaf, row, hasFocus);

    // generate syntax highlighted text, modeled after Mozilla Firefox
    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
    XMLItem item = (XMLItem) treeNode.getUserObject();
    JLabel l = (JLabel) c;
    l.setText(item.toString(true));
    return c;
  }

  // -- Main method --

  public static void main(String[] args) throws Exception {
    String file = args[0];

    // parse XML into DOM structure
    DocumentBuilderFactory docFact = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = docFact.newDocumentBuilder();
    Document doc = db.parse(new File(file));

    // display DOM in a JTree on screen
    JTree tree = makeJTree(doc);
    JFrame frame = new JFrame("XMLCellRenderer");
    JPanel pane = new JPanel();
    pane.setLayout(new BorderLayout());
    pane.add(tree, BorderLayout.CENTER);
    frame.setContentPane(new JScrollPane(pane));
    frame.setBounds(200, 200, 750, 500);
    frame.setVisible(true);
  }

  // -- Helper methods --

  /** Recursively builds JTree node structure from DOM node structure. */
  private static DefaultMutableTreeNode makeTreeNode(Node node) {
    DefaultMutableTreeNode treeNode =
      new DefaultMutableTreeNode(new XMLItem(node));
    NodeList nodeList = node.getChildNodes();
    for (int i=0; i<nodeList.getLength(); i++) {
      Node n = nodeList.item(i);
      if (!(n instanceof Text)) treeNode.add(makeTreeNode(n));
    }
    return treeNode;
  }

  // -- Helper classes --

  /** Cell renderer item for use as DefaultMutableTreeNode user object. */
  private static class XMLItem {
    private Node node;
    public XMLItem(Node node) {
      this.node = node;
    }
    public String toString(boolean html) {
      StringBuffer sb = new StringBuffer();
      if (node instanceof Element) {
        if (html) sb.append("<html>");
        sb.append(html ? "&lt;" : "<");
        if (html) sb.append(ELEMENT_STYLE_START);
        sb.append(node.getNodeName());
        if (html) sb.append(ELEMENT_STYLE_END);
        // append attributes
        NamedNodeMap attr = node.getAttributes();
        for (int i=0; i<attr.getLength(); i++) {
          Node attrNode = attr.item(i);
          sb.append(" ");
          if (html) sb.append(ATTR_NAME_STYLE_START);
          sb.append(attrNode.getNodeName());
          sb.append("=");
          if (html) sb.append(ATTR_NAME_STYLE_END);
          if (html) sb.append(ATTR_VALUE_STYLE_START);
          sb.append(html ? "&quot;" : "\"");
          sb.append(attrNode.getNodeValue());
          sb.append(html ? "&quot;" : "\"");
          if (html) sb.append(ATTR_VALUE_STYLE_END);
        }
        int numChildren = node.getChildNodes().getLength();
        if (numChildren == 0) sb.append("/");
        sb.append(html ? "&gt;" : ">");
        // check for single child text node
        if (numChildren == 1) {
          Node n = node.getFirstChild();
          if (n instanceof Text) {
            sb.append(sanitize(n.getNodeValue(), html));
            sb.append(html ? "&lt;" : "<");
            sb.append("/");
            if (html) sb.append(ELEMENT_STYLE_START);
            sb.append(node.getNodeName());
            if (html) sb.append(ELEMENT_STYLE_END);
            sb.append(html ? "&gt;" : ">");
          }
        }
      }
      else if (node instanceof Comment) {
        if (html) sb.append("<html>");
        if (html) sb.append(COMMENT_STYLE_START);
        sb.append(html ? "&lt;" : "<");
        sb.append("!--");
        sb.append(html ? "<br>" : "\n");
        sb.append(sanitize(node.getNodeValue(), html));
        sb.append(html ? "<br>" : "\n");
        sb.append("--");
        sb.append(html ? "&gt;" : ">");
        if (html) sb.append(COMMENT_STYLE_END);
      }
      else sb.append(node.getNodeValue());
      return sb.toString();
    }
    /** Allows copy and paste from tree component. */
    public String toString() {
      return toString(false);
    }
    /** Sanitizes strings to appear correctly in HTML. */
    private String sanitize(String s, boolean html) {
      if (!html) return s;
      s = s.replaceAll("&", "&amp;");
      s = s.replaceAll("\"", "&quot;");
      s = s.replaceAll("<", "&lt;");
      s = s.replaceAll(">", "&gt;");
      s = s.replaceAll("[\n\r]", "<br>");
      return s;
    }
  }

}
