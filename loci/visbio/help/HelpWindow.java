//
// HelpWindow.java
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

package loci.visbio.help;

import java.awt.Dimension;
import java.io.*;
import java.util.Enumeration;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.text.html.HTMLDocument;
import javax.swing.tree.*;
import loci.visbio.util.BrowserLauncher;
import loci.visbio.util.SwingUtil;

/** HelpWindow details basic VisBio program usage. */
public class HelpWindow extends JFrame
  implements HyperlinkListener, TreeSelectionListener
{

  // -- Constants --

  /** Default width of help window in pixels. */
  private static final int DEFAULT_WIDTH = 650;

  /** Default height of help window in pixels. */
  private static final int DEFAULT_HEIGHT = 600;


  // -- Fields --

  /** Help topic tree root node. */
  private HelpTopic root;

  /** Tree of help topics. */
  private JTree topics;

  /** Pane containing the current help topic. */
  private JEditorPane pane;


  // -- Constructor --

  /** Creates a VisBio help window. */
  public HelpWindow() {
    super("VisBio Help");

    // create components
    root = new HelpTopic("VisBio Help Topics", null);
    topics = new JTree(root);
    //topics.setRootVisible(false);
    TreeSelectionModel treeModel = new DefaultTreeSelectionModel();
    treeModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    //topics.setVisibleRowCount(8);
    topics.setSelectionModel(treeModel);
    topics.addTreeSelectionListener(this);
    pane = new JEditorPane();
    pane.addHyperlinkListener(this);
    pane.setEditable(false);

    // lay out components
    JScrollPane topicsScroll = new JScrollPane(topics);
    SwingUtil.configureScrollPane(topicsScroll);
    JScrollPane paneScroll = new JScrollPane(pane);
    SwingUtil.configureScrollPane(paneScroll);
    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
       topicsScroll, paneScroll);
    split.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    setContentPane(split);
  }


  // -- HelpWindow API methods --

  /** Adds the given topic to the list, from the given source file. */
  public void addTopic(String topic, String source) {
    addTopic(root, topic, source);
  }


  // -- HyperlinkListener API methods --

  /** Handles hyperlinks. */
  public void hyperlinkUpdate(HyperlinkEvent e) {
    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
      if (e instanceof HTMLFrameHyperlinkEvent) {
        HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
        HTMLDocument doc = (HTMLDocument) pane.getDocument();
        doc.processHTMLFrameHyperlinkEvent(evt);
      }
      else {
        String source = e.getURL().toString();
        HelpTopic node = findTopic(source);
        if (node != null) {
          TreePath path = new TreePath(node);
          topics.setSelectionPath(path);
          topics.scrollPathToVisible(path);
          // CTR START HERE figure out why this doesn't visibly select a topic
        }
        else {
          // launch external browser to handle the link
          try { BrowserLauncher.openURL(source); }
          catch (IOException exc) { exc.printStackTrace(); }
        }
      }
    }
  }


  // -- TreeSelectionListener API methods --

  /** Updates help topic based on user selection. */
  public void valueChanged(TreeSelectionEvent e) {
    HelpTopic node = (HelpTopic)
      e.getNewLeadSelectionPath().getLastPathComponent();
    final String source = node.getSource();
    if (source == null) {
      pane.setText(node.getName());
      return;
    }
    pane.setText("Loading " + source + "...");
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        try { pane.setPage(getClass().getResource(source)); }
        catch (IOException exc) {
          StringWriter sw = new StringWriter();
          exc.printStackTrace(new PrintWriter(sw));
          pane.setText(sw.toString());
        }
      }
    });
  }

  // -- Helper methods --

  /** Recursively adds the given topic to the tree at the given position. */
  private void addTopic(HelpTopic parent, String topic, String source) {
    int slash = topic.indexOf("/");
    if (slash < 0) parent.add(new HelpTopic(topic, source));
    else {
      String pre = topic.substring(0, slash);
      String post = topic.substring(slash + 1);
      HelpTopic child = null;
      Enumeration e = parent.children();
      while (e.hasMoreElements()) {
        HelpTopic node = (HelpTopic) e.nextElement();
        if (node.getName().equals(pre)) {
          child = node;
          break;
        }
      }
      if (child == null) child = new HelpTopic(pre, null);
      addTopic(child, post, source);
    }
  }

  /** Locates the first node with the given source. */
  private HelpTopic findTopic(String source) {
    Enumeration e = root.breadthFirstEnumeration();
    while (e.hasMoreElements()) {
      HelpTopic node = (HelpTopic) e.nextElement();
      String nodeSource = node.getSource();
      if (nodeSource == null) continue;
      if (source.endsWith(nodeSource)) return node;
    }
    return null;
  }

}
