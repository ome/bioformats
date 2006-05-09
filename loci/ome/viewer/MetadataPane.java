//
// MetadataPane.java
//

package loci.ome.viewer;

import java.awt.BorderLayout;
import java.io.File;
import java.util.Enumeration;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.*;
import org.openmicroscopy.xml.DOMUtil;
import org.openmicroscopy.xml.OMENode;
import org.w3c.dom.*;

/** MetadataPane is a panel that displays OME-XML metadata. */
public class MetadataPane extends JPanel implements TreeSelectionListener {

  // -- Constants --

  /** Column headings for OME-XML attributes table. */
  protected static final String[] TREE_COLUMNS = {"Attribute", "Value"};

  /** Column headings for metadata table. */
  protected static final String[] META_COLUMNS = {"Name", "Value"};


  // -- Fields - XML tree --

  /** Pane containing XML tree. */
  protected JSplitPane xmlTree;

  /** Tree displaying metadata in OME-XML format. */
  protected JTree tree;

  /** Root of tree displaying OME-XML metadata. */
  protected DefaultMutableTreeNode treeRoot;

  /** Table listing OME-XML attributes. */
  protected JTable treeTable;

  /** Table model backing OME-XML attributes table. */
  protected DefaultTableModel treeTableModel;


  // -- Fields - raw panel --

  /** Panel containing raw XML dump. */
  protected JPanel rawPanel;

  /** Text area displaying raw XML. */
  protected JTextArea rawText;


  // -- Constructor --

  /** Constructs widget for displaying OME-XML metadata. */
  public MetadataPane() {
    // -- XML tree --

    // OME-XML tree
    treeRoot = new DefaultMutableTreeNode();
    tree = new JTree(treeRoot);
    JScrollPane scrollTree = new JScrollPane(tree);
    //SwingUtil.configureScrollPane(scrollTree);
    tree.setRootVisible(false);
    tree.setShowsRootHandles(true);
    DefaultTreeSelectionModel treeSelModel = new DefaultTreeSelectionModel();
    treeSelModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.setSelectionModel(treeSelModel);
    tree.addTreeSelectionListener(this);

    // OME-XML attributes table
    treeTableModel = new DefaultTableModel(TREE_COLUMNS, 0);
    treeTable = new JTable(treeTableModel);
    JScrollPane scrollTreeTable = new JScrollPane(treeTable);
    //SwingUtil.configureScrollPane(scrollTreeTable);

    // OME-XML split pane
    xmlTree = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
      scrollTree, scrollTreeTable);

    // lay out components
    setLayout(new BorderLayout());
    add(xmlTree, BorderLayout.CENTER);

    // -- Raw panel --

    rawPanel = new JPanel();
    rawPanel.setLayout(new BorderLayout());

    // label explaining what happened
    rawPanel.add(new JLabel("Metadata parsing failed. " +
      "Here is the raw info (good luck):"), BorderLayout.NORTH);

    // text area for displaying raw XML
    rawText = new JTextArea();
    rawText.setLineWrap(true);
    rawText.setColumns(50);
    rawText.setRows(30);
    rawText.setEditable(false);
    JScrollPane rawScroll = new JScrollPane(rawText);
    rawScroll.setBorder(new EmptyBorder(5, 0, 5, 0));
    rawPanel.add(rawScroll, BorderLayout.CENTER);
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
    if (ome == null) {
      rawText.setText(xml);
      if (getComponent(0) instanceof JSplitPane) {
        removeAll();
        add(rawPanel, BorderLayout.CENTER);
      }
    }
    else {
      setOMEXML(ome);
      if (getComponent(0) instanceof JPanel) {
        removeAll();
        add(xmlTree, BorderLayout.CENTER);
      }
    }
  }

  /**
   * Sets the displayed OME-XML metadata to correspond
   * to the given OME-XML or OME-TIFF file.
   */
  public void setOMEXML(File file) {
    // TODO
  }

  /** Sets the displayed OME-XML metadata. */
  public void setOMEXML(OMENode ome) {
    // populate OME-XML tree
    Document doc = null;
    try { doc = ome == null ? null : ome.getOMEDocument(false); }
    catch (Exception exc) { }
    if (doc != null) {
      buildTree(doc.getDocumentElement(), treeRoot);
      expandTree(tree, treeRoot);
    }
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

    // update OME-XML attributes table
    Element el = ((ElementWrapper) node.getUserObject()).el;
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
