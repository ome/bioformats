/*
 * #%L
 * OME Metadata Editor application for exploration and editing of OME-XML and
 * OME-TIFF metadata.
 * %%
 * Copyright (C) 2006 - 2012 Open Microscopy Environment:
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

package loci.ome.viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import loci.common.DataTools;
import loci.common.RandomAccessInputStream;
import loci.formats.tiff.TiffParser;

import ome.xml.DOMUtil;

import org.openmicroscopy.xml.OMENode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * MetadataPane is a panel that displays OME-XML metadata.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/legacy/ome-editor/src/loci/ome/viewer/MetadataPane.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/legacy/ome-editor/src/loci/ome/viewer/MetadataPane.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class MetadataPane extends JPanel
  implements Runnable, TreeSelectionListener
{

  // -- Constants --

  /** Column headings for OME-XML attributes table. */
  protected static final String[] TREE_COLUMNS = {"Attribute", "Value"};

  // -- Fields - XML tree --

  /** Pane containing XML tree. */
  protected JSplitPane xmlTree;

  /** Tree displaying metadata in OME-XML format. */
  protected JTree tree;

  /** Field listing CDATA for the given XML element. */
  protected JTextArea cdata;

  /** Table listing OME-XML attributes. */
  protected JTable treeTable;

  /** Table model backing OME-XML attributes table. */
  protected DefaultTableModel treeTableModel;

  // -- Fields - raw panel --

  /** Panel containing raw XML dump. */
  protected JPanel rawPanel;

  /** Text area displaying raw XML. */
  protected JTextArea rawText;

  /** Whether XML is being displayed in raw form. */
  protected boolean raw;

  // -- Constructor --

  /** Constructs widget for displaying OME-XML metadata. */
  public MetadataPane() {
    // -- XML tree --

    // OME-XML tree
    tree = new JTree(new DefaultMutableTreeNode());
    JScrollPane scrollTree = new JScrollPane(tree);
    scrollTree.setPreferredSize(new Dimension(250, 0));
    //SwingUtil.configureScrollPane(scrollTree);
    tree.setRootVisible(false);
    tree.setShowsRootHandles(true);
    DefaultTreeSelectionModel treeSelModel = new DefaultTreeSelectionModel();
    treeSelModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.setSelectionModel(treeSelModel);
    tree.addTreeSelectionListener(this);

    // attributes pane
    JPanel attributesPane = new JPanel();
    attributesPane.setLayout(new BoxLayout(attributesPane, BoxLayout.Y_AXIS));

    // CDATA text field
    cdata = new JTextArea();
    cdata.setRows(4);
    cdata.setEditable(false);
    JScrollPane cdataScroll = new JScrollPane(cdata);
    int height = cdata.getPreferredSize().height;
    cdataScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
    attributesPane.add(cdataScroll);
    attributesPane.add(Box.createVerticalStrut(5));

    // OME-XML attributes table
    treeTableModel = new DefaultTableModel(TREE_COLUMNS, 0) {
      public boolean isCellEditable(int row, int col) { return false; }
    };
    treeTable = new JTable(treeTableModel);
    treeTable.getColumnModel().getColumn(0).setPreferredWidth(100);
    treeTable.getColumnModel().getColumn(1).setPreferredWidth(300);
    JScrollPane scrollTreeTable = new JScrollPane(treeTable);
    //SwingUtil.configureScrollPane(scrollTreeTable);
    treeTable.setPreferredScrollableViewportSize(new Dimension(400, 0));
    attributesPane.add(scrollTreeTable);

    // OME-XML split pane
    xmlTree = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
      scrollTree, attributesPane);

    // lay out components
    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    add(xmlTree);

    // -- Raw panel --

    rawPanel = new JPanel();
    rawPanel.setLayout(new BorderLayout());

    // label explaining what happened
    JLabel rawLabel = new JLabel("Metadata parsing failed. " +
      "Here is the raw info. Good luck!");
    rawLabel.setBorder(new EmptyBorder(5, 0, 5, 0));
    rawPanel.add(rawLabel, BorderLayout.NORTH);

    // text area for displaying raw XML
    rawText = new JTextArea();
    rawText.setLineWrap(true);
    rawText.setColumns(50);
    rawText.setRows(30);
    rawText.setEditable(false);
    rawPanel.add(new JScrollPane(rawText), BorderLayout.CENTER);
    rawPanel.setVisible(false);
    add(rawPanel);
  }

  // -- MetadataPane API methods --

  /**
   * Sets the displayed OME-XML metadata to correspond
   * to the given character string of XML.
   */
  public void setOMEXML(String xml) {
    OMENode ome = null;
    try { ome = new OMENode(xml); }
    catch (Exception exc) { }
    raw = ome == null;
    if (raw) rawText.setText(xml);
    else setOMEXML(ome);
    SwingUtilities.invokeLater(this);
  }

  /**
   * Sets the displayed OME-XML metadata to correspond
   * to the given OME-XML or OME-TIFF file.
   * @return true if the operation was successful
   */
  public boolean setOMEXML(File file) {
    try {
      RandomAccessInputStream in =
        new RandomAccessInputStream(file.getAbsolutePath());
      TiffParser parser = new TiffParser(in);
      if (parser.isValidHeader()) {
        // TIFF file
        String xml = parser.getComment();
        in.close();
        if (xml == null) return false;
        setOMEXML(xml);
      }
      else {
        String xml = DataTools.readFile(file.getAbsolutePath());
        if (xml.startsWith("<?xml") || xml.startsWith("<OME")) {
          setOMEXML(xml);
        }
        else return false;
      }
      return true;
    }
    catch (IOException exc) { return false; }
  }

  /** Sets the displayed OME-XML metadata. */
  public void setOMEXML(OMENode ome) {
    // populate OME-XML tree
    Document doc = null;
    try { doc = ome == null ? null : ome.getOMEDocument(false); }
    catch (Exception exc) { }
    DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode();
    ((DefaultTreeModel) tree.getModel()).setRoot(treeRoot);
    if (doc != null) {
      buildTree(doc.getDocumentElement(), treeRoot);
      expandTree(tree, treeRoot);
    }
  }

  // -- Component API methods --

  /** Sets the initial size of the metadata pane to be reasonable. */
  public Dimension getPreferredSize() { return new Dimension(700, 500); }

  // -- Runnable API methods --

  /** Shows or hides the proper subpanes. */
  public void run() {
    xmlTree.setVisible(!raw);
    rawPanel.setVisible(raw);
    validate();
    repaint();
  }

  // -- TreeSelectionListener API methods --

  /** Called when the OME-XML tree selection changes. */
  public void valueChanged(TreeSelectionEvent e) {
    DefaultMutableTreeNode node =
      (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
    if (node == null) {
      treeTableModel.setRowCount(0);
      return;
    }

    // update CDATA text area
    Element el = ((ElementWrapper) node.getUserObject()).el;
    String text = DOMUtil.getCharacterData(el);
    cdata.setText(text == null ? "" : text);

    // update OME-XML attributes table
    String[] names = DOMUtil.getAttributeNames(el);
    String[] values = DOMUtil.getAttributeValues(el);
    treeTableModel.setRowCount(names.length);
    for (int i=0; i<names.length; i++) {
      treeTableModel.setValueAt(names[i], i, 0);
      treeTableModel.setValueAt(values[i], i, 1);
    }
  }

  // -- Helper methods --

  /** Builds a tree by wrapping XML elements with JTree nodes. */
  protected void buildTree(Element el, DefaultMutableTreeNode node) {
    DefaultMutableTreeNode child =
      new DefaultMutableTreeNode(new ElementWrapper(el));
    node.add(child);
    NodeList nodeList = el.getChildNodes();
    for (int i=0; i<nodeList.getLength(); i++) {
      Node n = (Node) nodeList.item(i);
      if (n instanceof Element) buildTree((Element) n, child);
    }
  }

  // -- Utility methods --

  /** Fully expands the given JTree from the specified node. */
  public static void expandTree(JTree tree, DefaultMutableTreeNode node) {
    if (node.isLeaf()) return;
    tree.expandPath(new TreePath(node.getPath()));
    Enumeration e = node.children();
    while (e.hasMoreElements()) {
      expandTree(tree, (DefaultMutableTreeNode) e.nextElement());
    }
  }

  // -- Helper classes --

  /** Helper class for OME-XML metadata tree view. */
  public class ElementWrapper {
    public Element el;
    public ElementWrapper(Element el) { this.el = el; }
    public String toString() { return el == null ? "null" : el.getTagName(); }
  }

}
