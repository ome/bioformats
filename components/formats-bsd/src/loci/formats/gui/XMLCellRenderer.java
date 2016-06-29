/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
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
 * #L%
 */

package loci.formats.gui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * A tree cell renderer for displaying syntax highlighted XML in a JTree.
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
  @Override
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
    @Override
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
