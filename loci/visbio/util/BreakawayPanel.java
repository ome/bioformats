//
// BreakawayPanel.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-@year@ Curtis Rueden.

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

package loci.visbio.util;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * BreakawayPanel is a container with arrows for controlling which edge of
 * its parent container it borders, or whether it floats in a separate window.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/visbio/util/BreakawayPanel.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/visbio/util/BreakawayPanel.java">SVN</a></dd></dl>
 */
public class BreakawayPanel extends JPanel implements ActionListener {

  // -- Fields --

  /** Parent container. */
  protected Container parent;

  /** Owner window for breakaway panel and its parent. */
  protected Window owner;

  /** Floating window containing the breakaway panel. */
  protected JFrame window;

  /** Pane to which GUI components can be added to the breakaway panel. */
  protected Container pane;

  /** Scroll pane (if any). */
  protected JScrollPane scrollPane;

  /** The edge upon which the breakaway panel lies. */
  protected String edge;

  /** Flag indicating whether owner window gets intelligently resized. */
  protected boolean autoSize;

  /** Arrow buttons. */
  protected BioArrowButton top, bottom, left, right;

  // -- Constructor --

  /**
   * Creates a new breakaway panel.
   * @param parent Panel's parent container, which uses BorderLayout
   * @param name Title to display when panel is floating in a separate window
   * @param scroll Whether to display scroll bars when needed
   */
  public BreakawayPanel(Container parent, String name, boolean scroll) {
    this.parent = parent;
    owner = SwingUtil.getWindow(parent);
    window = new JFrame(name);
    pane = new JPanel();
    edge = null;
    autoSize = true;

    window.getContentPane().setLayout(new BorderLayout());

    // up arrow button
    Dimension tallArrow = new Dimension(12, Integer.MAX_VALUE);
    top = new BioArrowButton(BioArrowButton.NORTH);
    top.setActionCommand("Top");
    top.addActionListener(this);
    top.setPreferredSize(tallArrow);
    top.setMaximumSize(tallArrow);

    // down arrow button
    bottom = new BioArrowButton(BioArrowButton.SOUTH);
    bottom.setActionCommand("Bottom");
    bottom.addActionListener(this);
    bottom.setPreferredSize(tallArrow);
    bottom.setMaximumSize(tallArrow);

    // left arrow button
    Dimension wideArrow = new Dimension(Integer.MAX_VALUE, 12);
    left = new BioArrowButton(BioArrowButton.WEST);
    left.setActionCommand("Left");
    left.addActionListener(this);
    left.setPreferredSize(wideArrow);
    left.setMaximumSize(wideArrow);

    // right arrow button
    right = new BioArrowButton(BioArrowButton.EAST);
    right.setActionCommand("Right");
    right.addActionListener(this);
    right.setPreferredSize(wideArrow);
    right.setMaximumSize(wideArrow);

    // lay out components
    setLayout(new BorderLayout());
    add(top, BorderLayout.NORTH);
    add(bottom, BorderLayout.SOUTH);
    add(left, BorderLayout.WEST);
    add(right, BorderLayout.EAST);
    if (scroll) {
      scrollPane = new JScrollPane(pane);
      SwingUtil.configureScrollPane(scrollPane);
      add(scrollPane, BorderLayout.CENTER);
    }
    else add(pane, BorderLayout.CENTER);

    window.getContentPane().add(this);
  }

  // -- BreakawayPanel API methods --

  /** Sets pane containing GUI components. */
  public void setContentPane(Container c) {
    if (scrollPane == null) {
      remove(pane);
      add(c, BorderLayout.CENTER);
    }
    else scrollPane.setViewportView(c);
    pane = c;
  }

  /** Gets pane to which GUI components can be added. */
  public Container getContentPane() { return pane; }

  /** Gets floating window used when panel breaks away from its parent. */
  public JFrame getWindow() { return window; }

  /**
   * Sets the edge upon which the breakaway panel lies. Automatically adds
   * the breakaway panel to its parent container in the proper location.
   * @param e Valid edge values are BorderLayout.NORTH, BorderLayout.SOUTH,
   *          BorderLayout.EAST, BorderLayout.WEST, and null for floating.
   */
  public void setEdge(String e) {
    if (edge == e) {
      if (edge == null) return; // nothing to do: (null -> null)
      e = null;
    }

    // remove panel from old location
    Point pos;
    try { pos = getLocationOnScreen(); }
    catch (IllegalComponentStateException exc) { pos = null; }
    if (edge == null) window.getContentPane().remove(this);
    else parent.remove(this);

    // add breakaway panel to new location
    if (e == null) {
      window.getContentPane().add(this, BorderLayout.CENTER);
      window.pack();
      window.setLocation(pos.x, pos.y);
      window.setVisible(true);
    }
    else {
      window.setVisible(false);
      window.getContentPane().remove(this);
      parent.add(this, e);
    }

    // intelligently resize owner window
    if (autoSize) {
      boolean oNS = edge == BorderLayout.NORTH || edge == BorderLayout.SOUTH;
      boolean oEW = edge == BorderLayout.EAST || edge == BorderLayout.WEST;
      boolean nNS = e == BorderLayout.NORTH || e == BorderLayout.SOUTH;
      boolean nEW = e == BorderLayout.EAST || e == BorderLayout.WEST;

      boolean wup = false, wdn = false, hup = false, hdn = false;
      if (oNS) {
        wup = nEW;  // w+: (ns -> ew)
        hdn = !nNS; // h-: (ns -> ew) or (ns -> null)
      }
      else if (oEW) {
        wdn = !nEW; // w-: (ew -> ns) or (ew -> null)
        hup = nNS;  // h+: (ew -> ns)
      }
      else {
        wup = nEW; // w+: (null -> ew)
        hup = nNS; // h+: (null -> ns)
      }

      Dimension size = owner.getSize();
      Dimension upSize = getPreferredSize();
      Dimension dnSize = getSize();
      int w = size.width;
      int h = size.height;
      if (wup) w += upSize.width;
      if (wdn) w -= dnSize.width;
      if (hup) h += upSize.height;
      if (hdn) h -= dnSize.height;

      Point loc = owner.getLocation();
      int x = loc.x;
      int y = loc.y;
      int xmod = wup ? upSize.width : dnSize.width;
      int ymod = hup ? upSize.height: dnSize.height;
      if (e == BorderLayout.WEST) x -= xmod;
      if (e == BorderLayout.NORTH) y -= ymod;
      if (edge == BorderLayout.WEST) x += xmod;
      if (edge == BorderLayout.NORTH) y += ymod;

      owner.setBounds(x, y, w, h);
      owner.validate();
      owner.repaint();
    }

    edge = e;
  }

  /** Gets the edge upon which the breakaway panel lies. */
  public String getEdge() { return edge; }

  /** Sets whether changing the edge intelligently resizes the owner window. */
  public void setAutoSize(boolean autoSize) { this.autoSize = autoSize; }

  /** Gets whether changing the edge intelligently resizes the owner window. */
  public boolean isAutoSize() { return autoSize; }

  /** If breakaway panel is in a separate window, ensures it is visible. */
  public void reshow() {
    if (edge == null) window.setVisible(true);
  }

  /** Enlarges the window to its preferred size if it is too small. */
  public void repack() {
    if (edge == null) window.setSize(SwingUtil.getRepackSize(window));
  }

  /** Toggles availability of up arrow button. */
  public void setUpEnabled(boolean enabled) {
    top.setVisible(enabled);
  }

  /** Toggles availability of down arrow button. */
  public void setDownEnabled(boolean enabled) {
    bottom.setVisible(enabled);
  }

  /** Toggles availability of left arrow button. */
  public void setLeftEnabled(boolean enabled) {
    left.setVisible(enabled);
  }

  /** Toggles availability of right arrow button. */
  public void setRightEnabled(boolean enabled) {
    right.setVisible(enabled);
  }

  // -- ActionListener API methods --

  /** Handles button presses. */
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if (cmd.equals("Top")) setEdge(BorderLayout.NORTH);
    else if (cmd.equals("Bottom")) setEdge(BorderLayout.SOUTH);
    else if (cmd.equals("Left")) setEdge(BorderLayout.WEST);
    else if (cmd.equals("Right")) setEdge(BorderLayout.EAST);
  }

}
