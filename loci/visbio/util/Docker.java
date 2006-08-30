//
// Docker.java
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
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.*;
import javax.swing.JButton;
import javax.swing.JFrame;

/**
 * This class is used to specify a group of windows able to be docked to one
 * another. When a window is moved close enough to another window, it snaps
 * next to it, becoming docked. When the nearby window is subsequently moved,
 * it drags all docked windows along with it.
 *
 * Docker also enables windows to snap to the edges of the screen, if desired.
 */
public class Docker implements ComponentListener {

  // -- Constants --

  /** Default window docking priority. */
  public static final int DEFAULT_PRIORITY = 5;

  /** A docking priority lower than the default. */
  public static final int LOW_PRIORITY = 0;

  /** A docking priority higher than the default. */
  public static final int HIGH_PRIORITY = 10;


  // -- Fields --

  /** List of windows able to be docked to one another. */
  protected Vector windows = new Vector();

  /** List of window locations. */
  protected Vector locations = new Vector();

  /** For each window, the set of windows docked to it. */
  protected Vector docked = new Vector();

  /** List of window docking priorities. */
  protected Vector priorities = new Vector();

  /** Whether to perform docking operations at all. */
  protected boolean enabled = true;

  /** Pixel threshold for windows to snap next to one another. */
  protected int thresh = 10;

  /** Whether to snap to the edges of the screen. */
  protected boolean edges = true;


  // -- Docker API methods --

  /** Adds a window to the dockable group. */
  public void addWindow(Window w) { addWindow(w, DEFAULT_PRIORITY); }

  /** Adds a window to the dockable group, at the given docking priority. */
  public void addWindow(Window w, int priority) {
    w.addComponentListener(this);
    windows.add(w);
    locations.add(w.getLocation());
    docked.add(new HashSet());
    priorities.add(new Integer(priority));
  }

  /** Removes a window from the dockable group. */
  public void removeWindow(Window w) {
    int ndx = windows.indexOf(w);
    if (ndx < 0) return;

    // remove window from lists
    windows.removeElementAt(ndx);
    locations.removeElementAt(ndx);
    docked.removeElementAt(ndx);
    priorities.removeElementAt(ndx);

    // undock window from remaining windows
    for (int i=0; i<windows.size(); i++) {
      HashSet set = (HashSet) docked.elementAt(i);
      set.remove(w);
    }
  }

  /** Removes all windows from the dockable group. */
  public void removeAllWindows() {
    windows.removeAllElements();
    locations.removeAllElements();
    docked.removeAllElements();
    priorities.removeAllElements();
  }

  /** Sets whether docker should perform docking operations. */
  public void setEnabled(boolean enabled) { this.enabled = enabled; }

  /** Sets the docking threshold in pixels. */
  public void setSnapThreshold(int pixels) { thresh = pixels; }

  /** Sets whether windows should snap to the screen edges when close. */
  public void setSnapToScreenEdges(boolean snap) { edges = snap; }


  // -- ComponentListener API methods --

  /** Called when a window is resized. */
  public void componentResized(ComponentEvent e) { }

  /** Called when a window is moved. */
  public void componentMoved(ComponentEvent e) {
    Window w = (Window) e.getSource();
    int ndx = getIndex(w);

    // build docked group
    Vector group = new Vector();
    buildGroup(ndx, group);

    // build list of windows not in docked group
    Vector non = new Vector();
    for (int i=0; i<windows.size(); i++) {
      Window wi = getWindow(i);
      if (wi.isVisible() && group.indexOf(wi) < 0) non.add(wi);
    }

    // undock all windows in the docked group from windows not in the group
    for (int i=0; i<group.size(); i++) {
      Window wi = (Window) group.elementAt(i);
      for (int j=0; j<non.size(); j++) undock(wi, (Window) non.elementAt(j));
    }

    // compute docked group's X and Y snapping distance
    Point p = computeSnap(w, non);

    // compute distance moved and record new location
    Point oloc = getLocation(ndx);
    Point nloc = w.getLocation();
    nloc.x += p.x;
    nloc.y += p.y;
    Point shift = new Point(nloc.x - oloc.x, nloc.y - oloc.y);

    // snap all windows in docked group to appropriate location
    for (int i=0; i<group.size(); i++) {
      Window wi = (Window) group.elementAt(i);
      if (wi == w) shiftLocation(wi, p.x, p.y);
      else shiftLocation(wi, shift.x, shift.y);
    }
    //if (p.x == 0 && p.y == 0) return;

    // dock newly adjoining windows to appropriate windows within the group
    for (int i=0; i<group.size(); i++) {
      Window wi = (Window) group.elementAt(i);
      for (int j=0; j<non.size(); j++) {
        Window wj = (Window) non.elementAt(j);
        Point pij = computeDistance(wi, wj);
        if (pij.x == 0 || pij.y == 0) {
          // windows are adjacent; dock them
          int pi = getPriority(wi);
          int pj = getPriority(wj);
          if (pi > pj) dock(wj, wi);
          else dock(wi, wj);
        }
      }
    }
  }

  /** Called when a window is shown. */
  public void componentShown(ComponentEvent e) { }

  /** Called when a window is hidden. */
  public void componentHidden(ComponentEvent e) {
    Window w = (Window) e.getSource();
    int ndx = getIndex(w);

    // undock all other windows from hidden window
    getDockSet(ndx).clear();

    // undock hidden window from all other windows
    for (int i=0; i<windows.size(); i++) getDockSet(i).remove(w);
  }


  // -- Internal methods - get window characteristics from an index --

  /** Gets the window at the specified index. */
  protected Window getWindow(int ndx) {
    return (Window) windows.elementAt(ndx);
  }

  /** Gets the last recorded location of the window at the specified index. */
  protected Point getLocation(int ndx) {
    return (Point) locations.elementAt(ndx);
  }

  /** Gets the set of docked windows for the window at the specified index. */
  protected HashSet getDockSet(int ndx) {
    return (HashSet) docked.elementAt(ndx);
  }

  /** Gets the docking priority for the window at the specified index. */
  protected int getPriority(int ndx) {
    return ((Integer) priorities.elementAt(ndx)).intValue();
  }


  // -- Internal methods - get window characteristics from a window --

  /** Gets the given window's index. */
  protected int getIndex(Window w) { return windows.indexOf(w); }

  /** Gets the given window's last recorded location. */
  protected Point getLocation(Window w) { return getLocation(getIndex(w)); }

  /** Gets the set of docked windows for the given window. */
  protected HashSet getDockSet(Window w) { return getDockSet(getIndex(w)); }

  /** Gets the docking priority for the given window. */
  protected int getPriority(Window w) { return getPriority(getIndex(w)); }


  // -- Internal methods - dock/undock windows to/from each other --

  /** Docks window A to window B. */
  protected void dock(Window a, Window b) { getDockSet(b).add(a); }

  /** Undocks window A from window B. */
  protected void undock(Window a, Window b) { getDockSet(b).remove(a); }


  // -- Internal methods - edge comparison/snapping --

  /** Integer absolute value function. */
  protected static int abs(int value) { return value < 0 ? -value : value; }

  /** Computes the distance rectangle A must snap to adjoin rectangle B. */
  protected Point computeDistance(Rectangle a, Rectangle b) {
    Point p = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);

    // compute horizontal distance
    if (b.y <= a.y + a.height + thresh && a.y <= b.y + b.height + thresh) {
      int ll = b.x - a.x;
      if (abs(ll) < abs(p.x)) p.x = ll;
      int lr = b.x - (a.x + a.width);
      if (abs(lr) < abs(p.x)) p.x = lr;
      int rl = b.x + b.width - a.x;
      if (abs(rl) < abs(p.x)) p.x = rl;
      int rr = b.x + b.width - (a.x + a.width);
      if (abs(rr) < abs(p.x)) p.x = rr;
    }

    // compute vertical distance
    if (b.x <= a.x + a.width + thresh && a.x <= b.x + b.width + thresh) {
      int uu = b.y - a.y;
      if (abs(uu) < abs(p.y)) p.y = uu;
      int ud = b.y - (a.y + a.height);
      if (abs(ud) < abs(p.y)) p.y = ud;
      int du = b.y + b.height - a.y;
      if (abs(du) < abs(p.y)) p.y = du;
      int dd = b.y + b.height - (a.y + a.height);
      if (abs(dd) < abs(p.y)) p.y = dd;
    }

    return p;
  }

  /** Computes the distance window A must snap to adjoin window B. */
  protected Point computeDistance(Window a, Window b) {
    return computeDistance(a.getBounds(), b.getBounds());
  }

  /** Computes the X and Y distances the docked group should snap. */
  protected Point computeSnap(Window w, Vector non) {
    Point p = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
    Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
    Rectangle screen = new Rectangle(0, 0, ss.width, ss.height);

    Rectangle r = w.getBounds();
    for (int i=0; i<non.size(); i++) {
      Rectangle ri = ((Window) non.elementAt(i)).getBounds();
      Point pi = computeDistance(r, ri);
      if (abs(pi.x) < abs(p.x)) p.x = pi.x;
      if (abs(pi.y) < abs(p.y)) p.y = pi.y;
    }
    if (edges) {
      Point ps = computeDistance(r, screen);
      if (abs(ps.x) < abs(p.x)) p.x = ps.x;
      if (abs(ps.y) < abs(p.y)) p.y = ps.y;
    }

    if (abs(p.x) > thresh) p.x = 0;
    if (abs(p.y) > thresh) p.y = 0;
    return p;
  }

  /**
   * Shifts the given window's location by the given amount,
   * without generating a ComponentEvent.
   */
  protected void shiftLocation(Window w, int px, int py) {
    if (enabled) {
      w.removeComponentListener(this);
      Point l = w.getLocation();
      w.setLocation(l.x + px, l.y + py);
    }
    locations.setElementAt(w.getLocation(), getIndex(w));
    if (enabled) w.addComponentListener(this);
  }


  // -- Helper methods --

  /** Recursively builds a list of windows docked together into a group. */
  protected void buildGroup(int ndx, Vector group) {
    group.add(windows.elementAt(ndx));
    HashSet set = (HashSet) docked.elementAt(ndx);
    Iterator it = set.iterator();
    while (it.hasNext()) {
      Window w = (Window) it.next();
      if (group.indexOf(w) < 0) buildGroup(getIndex(w), group);
    }
  }


  // -- Main method --

  /** Tests the Docker class. */
  public static void main(String[] args) {
    Docker docker = new Docker();
    for (int i=0; i<5; i++) {
      JFrame frame = new JFrame(i == 0 ? "Main" : "Frame" + i);
      frame.getContentPane().add(new JButton(i == 0 ? "Main" : "Button" + i));
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.pack();
      frame.setBounds(300, 80 * i + 200, 200, 60);
      if (i == 0) docker.addWindow(frame, HIGH_PRIORITY);
      else docker.addWindow(frame);
      frame.setVisible(true);
    }
  }

}
