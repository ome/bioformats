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
import java.awt.Toolkit;
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
  private static final int DEFAULT_WIDTH = 950;

  /** Default height of help window in pixels. */
  private static final int DEFAULT_HEIGHT = 700;

  /** Minimum width of tree pane. */
  private static final int MIN_TREE_WIDTH = 250;


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
    topics.setRootVisible(false);
    topics.setShowsRootHandles(true);
    topics.getSelectionModel().setSelectionMode(
       TreeSelectionModel.SINGLE_TREE_SELECTION);
    topics.addTreeSelectionListener(this);
    pane = new JEditorPane("text/html", "");
    pane.addHyperlinkListener(this);
    pane.setEditable(false);

    // lay out components
    JScrollPane topicsScroll = new JScrollPane(topics);
    topicsScroll.setMinimumSize(new Dimension(MIN_TREE_WIDTH, 0));
    SwingUtil.configureScrollPane(topicsScroll);
    JScrollPane paneScroll = new JScrollPane(pane);
    SwingUtil.configureScrollPane(paneScroll);
    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
       topicsScroll, paneScroll);
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int width = screenSize.width - 25, height = screenSize.height - 50;
    if (width > DEFAULT_WIDTH) width = DEFAULT_WIDTH;
    if (height > DEFAULT_HEIGHT) height = DEFAULT_HEIGHT;
    split.setPreferredSize(new Dimension(width, height));
    setContentPane(split);
  }


  // -- HelpWindow API methods --

  /** Adds the given topic to the list, from the given source file. */
  public void addTopic(String topic, String source) {
    addTopic(root, topic, source);
  }


  // -- JFrame API methods --

  /** Expands tree fully before packing help window. */
  public void pack() {
    // HACK - Expanding nodes as they are added to the tree results in bizarre
    // behavior (nodes permanently missing from the tree). Better to expand
    // everything after the tree has been completely built.
    Enumeration e = root.breadthFirstEnumeration();
    topics.expandPath(new TreePath(root.getPath()));
    while (e.hasMoreElements()) {
      HelpTopic node = (HelpTopic) e.nextElement();
      if (node.isLeaf()) continue;
      topics.expandPath(new TreePath(node.getPath()));
    }
    // select first child node
    HelpTopic firstChild = (HelpTopic) root.getChildAt(0);
    topics.setSelectionPath(new TreePath(firstChild.getPath()));
    super.pack();
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
          TreePath path = new TreePath(node.getPath());
          topics.setSelectionPath(path);
          topics.scrollPathToVisible(path);
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
    TreePath path = e.getNewLeadSelectionPath();
    HelpTopic node = path == null ? null :
      (HelpTopic) path.getLastPathComponent();
    final String source = node == null ? null : node.getSource();
    if (source == null) {
      pane.setText(node == null ? "" : node.getName());
      return;
    }
    pane.setText("<h2>" + node.getName() + "</h2>");
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        try { pane.setPage(getClass().getResource(source)); }
        catch (IOException exc) {
          StringWriter sw = new StringWriter();
          exc.printStackTrace(new PrintWriter(sw));
          pane.setText(source + "<pre>" + sw.toString() + "</pre>");
        }
        // HACK - JEditorPane.setPage(URL) throws a RuntimeException
        // ("Must insert new content into body element-")
        // when editor pane is successively updated too rapidly.
        // This 50ms delay seems sufficient to prevent the exception.
        try { Thread.sleep(50); }
        catch (InterruptedException exc) { }
      }
    });
  }

  // -- Helper methods --

  /** Recursively adds the given topic to the tree at the given position. */
  private void addTopic(HelpTopic parent, String topic, String source) {
    //topics.expandPath(new TreePath(parent.getPath()));
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
      if (child == null) {
        child = new HelpTopic(pre, null);
        parent.add(child);
      }
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
      // HACK - since URL strings have multiple possible structures, this
      // search just compares the end of the search string. If a link points to
      // an external URL that happens to end with the same string as one of the
      // help topics, this method will erroneously flag that topic anyway.
      if (source.endsWith(nodeSource)) return node;
    }
    return null;
  }

}
