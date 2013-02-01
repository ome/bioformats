/*
 * #%L
 * OME Notes library for flexible organization and presentation of OME-XML
 * metadata.
 * %%
 * Copyright (C) 2007 - 2013 Open Microscopy Environment:
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

package loci.ome.notes.editor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Window for choosing an OME-XML mapping.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/legacy/ome-notes/src/loci/ome/notes/editor/MappingWindow.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/legacy/ome-notes/src/loci/ome/notes/editor/MappingWindow.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class MappingWindow extends JPopupMenu implements ActionListener {

  // -- Constants --

  private static final CellConstraints CC = new CellConstraints();

  // -- Fields --

  protected TemplateEditor parent;
  protected boolean nameMap;
  protected JTabbedPane tabPane;

  // -- Constructor --

  public MappingWindow(TemplateEditor parent, boolean nameMap) {
    super();

    this.parent = parent;
    this.nameMap = nameMap;

    tabPane = new JTabbedPane();

    // set up search tab

    /* TODO
    FormLayout layout = new FormLayout("pref,pref:grow,pref,pref:grow,pref",
      "pref,pref:grow,pref,pref:grow,pref");
    JPanel p = new JPanel(layout);

    JTextField searchField = new JTextField();
    p.add(searchField, CC.xy(2, 2));
    JButton search = new JButton("Search");
    search.setActionCommand("search");
    search.addActionListener(this);
    p.add(search, CC.xy(4, 2));
    JButton ok = new JButton("OK");
    ok.setActionCommand("chooseMapping");
    ok.addActionListener(parent);
    p.add(ok, CC.xywh(2, 4, 3, 1));

    tabPane.add(p, "Search");
    */

    // set up schema tab

    /* TODO
    layout = new FormLayout("pref,pref:grow,pref",
      "pref,pref:grow,pref,pref:grow,pref");
    p = new JPanel(layout);

    JPanel schemaPanel = new JPanel();
    JScrollPane scroll = new JScrollPane();
    JTree tree = new JTree(new DefaultMutableTreeNode());

    try {
      InputStream is = getClass().getResourceAsStream("schema.ome");
      byte[] b = new byte[is.available()];
      is.read(b);
      OMENode node = new OMENode(new String(b));
      Document doc = node.getOMEDocument(false);
      DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode();
      ((DefaultTreeModel) tree.getModel()).setRoot(treeRoot);
      if (doc != null) {
        buildTree(doc.getDocumentElement(), treeRoot);
        expandTree(tree, treeRoot);
      }
    }
    catch (Exception e) {
      ((TemplateEditor) getParent()).error("Failed to load schema file", e);
    }

    tree.setPreferredSize(new Dimension(512, 256));
    schemaPanel.add(tree);
    scroll.add(schemaPanel);
    p.add(scroll, CC.xy(2, 2));
    ok = new JButton("OK");
    ok.setActionCommand("chooseMapping");
    ok.addActionListener(parent);
    p.add(ok, CC.xy(2, 4));

    tabPane.add(p, "Choose from schema");
    */

    // set up custom mapping tab

    FormLayout layout = new FormLayout("pref,pref:grow,pref,pref:grow,pref",
      "pref,pref:grow,pref,pref:grow,pref,pref:grow,pref");
    JPanel p = new JPanel(layout);

    //JLabel msg =
    //  new JLabel("A mapping is a colon-separated path to an attribute.");
    //p.add(msg, CC.xywh(2, 2, 3, 1));
    JLabel label = new JLabel("Mapping:");
    p.add(label, CC.xy(2, 4));
    JTextField field = new JTextField();
    p.add(field, CC.xy(4, 4));
    JButton ok = new JButton("OK");
    ok.setActionCommand("chooseMapping");
    ok.addActionListener(parent);
    p.add(ok, CC.xy(3, 6));

    tabPane.add(p, "Custom");

    add(tabPane);
    setPreferredSize(new Dimension(512, 256));
    pack();
  }

  // -- ActionListener API methods --

  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
  }

  // -- Helper methods --

  private void buildTree(Element el, DefaultMutableTreeNode node) {
    DefaultMutableTreeNode child =
      new DefaultMutableTreeNode(new ElementWrapper(el));
    node.add(child);
    NodeList nodeList = el.getChildNodes();
    for (int i=0; i<nodeList.getLength(); i++) {
      Node n = (Node) nodeList.item(i);
      if (n instanceof Element) buildTree((Element) n, child);
    }
  }

  private void expandTree(JTree tree, DefaultMutableTreeNode node) {
    if (node.isLeaf()) return;
    tree.expandPath(new TreePath(node.getPath()));
    Enumeration e = node.children();
    while (e.hasMoreElements()) {
      expandTree(tree, (DefaultMutableTreeNode) e.nextElement());
    }
  }

  // -- Helper class --

  public class ElementWrapper {
    public Element el;
    public ElementWrapper(Element el) { this.el = el; }
    public String toString() { return el == null ? "null" : el.getTagName(); }
  }

}
