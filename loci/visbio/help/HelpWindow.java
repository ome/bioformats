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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;

import java.io.IOException;

import java.net.URL;

import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.text.html.HTMLDocument;

import loci.visbio.util.BrowserLauncher;

/** HelpWindow details basic VisBio program usage. */
public class HelpWindow extends JDialog implements HyperlinkListener {

  // -- Constants --

  /** Default width of help window in pixels. */
  private static final int DEFAULT_WIDTH = 650;

  /** Default height of help window in pixels. */
  private static final int DEFAULT_HEIGHT = 600;


  // -- Fields --

  /** Information tabs. */
  private JTabbedPane tabs;

  /** List of URLs corresponding to tabs. */
  private Vector urls;


  // -- Constructor --

  /** Creates a VisBio help window. */
  public HelpWindow(Frame parent) {
    super(parent, "VisBio Help");

    // create components
    tabs = new JTabbedPane(JTabbedPane.LEFT);
    urls = new Vector();

    // lay out components
    JPanel p = new JPanel();
    p.setLayout(new BorderLayout());
    p.add(tabs, BorderLayout.CENTER);
    p.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    setContentPane(p);
  }


  // -- HelpWindow API methods --

  /**
   * Adds a tab to the help window with the given title,
   * with content from the specified file.
   */
  public void addTab(String title, String file) {
    JEditorPane editor = null;

    try {
      URL url = getClass().getResource(file);
      urls.add(url);
      editor = new JEditorPane(url);
    }
    catch (IOException exc) { exc.printStackTrace(); }
    editor.addHyperlinkListener(this);
    editor.setEditable(false);
    JScrollPane scroll = new JScrollPane(editor);
    tabs.addTab(title, scroll);
  }

  /** Sets the given tab in front. */
  public void setTab(int tab) { tabs.setSelectedIndex(tab); }


  // -- HyperlinkListener API methods --

  /** Handles hyperlinks. */
  public void hyperlinkUpdate(HyperlinkEvent e) {
    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
      JEditorPane pane = (JEditorPane) e.getSource();
      if (e instanceof HTMLFrameHyperlinkEvent) {
        HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
        HTMLDocument doc = (HTMLDocument) pane.getDocument();
        doc.processHTMLFrameHyperlinkEvent(evt);
      }
      else {
        URL url = e.getURL();
        int tab = -1;
        for (int i=0; i<urls.size(); i++) {
          URL u = (URL) urls.elementAt(i);
          if (u.equals(url)) {
            tab = i;
            break;
          }
        }
        if (tab < 0) {
          // launch external browser to handle the link
          try { BrowserLauncher.openURL(url.toString()); }
          catch (IOException exc) { exc.printStackTrace(); }
        }
        else {
          // switch to linked tab
          tabs.setSelectedIndex(tab);
        }
      }
    }
  }

}
