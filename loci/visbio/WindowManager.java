//
// WindowManager.java
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

package loci.visbio;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.*;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import loci.visbio.state.*;
import loci.visbio.util.*;
import org.w3c.dom.Element;

/**
 * WindowManager is the manager encapsulating VisBio's window logic,
 * including docking, resizing, minimization and cursor control.
 */
public class WindowManager extends LogicManager implements WindowListener {

  // -- Constants --

  /** String for window docking option. */
  public static final String DOCKING = "Window docking (buggy)";

  /** String for global minimization option. */
  public static final String HIDE_ALL =
    "Hide all windows when main window is minimized";


  // -- Fields --

  /** Table for keeping track of registered windows. */
  protected Hashtable windows = new Hashtable();

  /** List of windows that were visible before VisBio was minimized. */
  protected Vector visible = new Vector();

  /** Number of queued wait cursors. */
  protected int waiting = 0;

  /** Object enabling docking between windows. */
  protected Docker docker;

  /** Whether window docking features are enabled. */
  protected boolean docking = false;

  /** Whether minimizing main VisBio window hides all other windows. */
  protected boolean hideAll = false;

  /** Whether to distribute the menu bar across all registered frames. */
  protected boolean distributed;


  // -- Fields - initial state --

  /** Table of window states read during state restore. */
  protected Hashtable windowStates = new Hashtable();


  // -- Constructor --

  /** Constructs a window manager. */
  public WindowManager(VisBioFrame bio) { super(bio); }


  // -- WindowManager API methods --

  /** Registers a window with the window manager. */
  public void addWindow(Window w) { addWindow(w, true); }

  /**
   * Registers a window with the window manager. The pack flag indicates that
   * the window should be packed prior to being shown for the first time.
   */
  public void addWindow(Window w, boolean pack) {
    if (w instanceof Frame) ((Frame) w).setIconImage(bio.getIcon());
    WindowInfo winfo = new WindowInfo(w, pack);
    String wname = SwingUtil.getWindowTitle(w);
    WindowState ws = (WindowState) windowStates.get(wname);
    if (ws != null) {
      winfo.setState(ws);
      windowStates.remove(wname);
    }
    windows.put(w, winfo);
    if (distributed && w instanceof JFrame) {
      JFrame f = (JFrame) w;
      if (f.getJMenuBar() == null) {
        f.setJMenuBar(SwingUtil.cloneMenuBar(bio.getJMenuBar()));
      }
    }
    docker.addWindow(w);
  }

  /** Removes the window from the window manager and disposes of it. */
  public void disposeWindow(Window w) {
    if (w == null) return;
    w.setVisible(false);
    String wname = SwingUtil.getWindowTitle(w);
    windowStates.remove(wname);
    windows.remove(w);
    docker.removeWindow(w);
    w.dispose();
  }

  /** Gets a list of windows being handled by the window manager. */
  public Window[] getWindows() {
    Enumeration e = windows.keys();
    Window[] w = new Window[windows.size()];
    for (int i=0; i<w.length; i++) {
      w[i] = (Window) e.nextElement();
    }
    return w;
  }

  /** Toggles the cursor between hourglass and normal pointer mode. */
  public void setWaitCursor(boolean wait) {
    boolean doCursor = false;
    if (wait) {
      // set wait cursor
      if (waiting == 0) doCursor = true;
      waiting++;
    }
    else {
      waiting--;
      // set normal cursor
      if (waiting == 0) doCursor = true;
    }
    if (doCursor) {
      // apply cursor to all windows
      Enumeration en = windows.keys();
      while (en.hasMoreElements()) {
        Window w = (Window) en.nextElement();
        SwingUtil.setWaitCursor(w, wait);
      }
    }
  }

  /**
   * Shows the given window. If this is the first time the window has been
   * shown, or the current window position is off the screen, it is packed
   * and placed in a cascading position.
   */
  public void showWindow(Window w) {
    WindowInfo winfo = (WindowInfo) windows.get(w);
    if (winfo == null) return;
    winfo.showWindow();
  }

  /** Hides all windows. */
  public void hideWindows() {
    Enumeration en = windows.keys();
    while (en.hasMoreElements()) {
      Window w = (Window) en.nextElement();
      if (w.isVisible() && w != bio) {
        visible.add(w);
        w.setVisible(false);
      }
    }
  }

  /** Restores all previously hidden windows. */
  public void restoreWindows() {
    for (int i=0; i<visible.size(); i++) {
      Window w = (Window) visible.elementAt(i);
      w.setVisible(true);
    }
    visible.removeAllElements();
    bio.toFront();
  }

  /** Disposes all windows, prior to program exit. */
  public void disposeWindows() {
    Enumeration en = windows.keys();
    while (en.hasMoreElements()) {
      Window w = (Window) en.nextElement();
      w.dispose();
    }
  }

  /**
   * Sets whether all registered frames should have a duplicate of the main
   * VisBio menu bar. This feature is only here to support the Macintosh
   * screen menu bar.
   */
  public void setDistributedMenus(boolean dist) {
    if (distributed == dist) return;
    distributed = dist;
    doMenuBars(dist ? bio.getJMenuBar() : null);
  }


  // -- LogicManager API methods --

  /** Called to notify the logic manager of a VisBio event. */
  public void doEvent(VisBioEvent evt) {
    int eventType = evt.getEventType();
    if (eventType == VisBioEvent.LOGIC_ADDED) {
      LogicManager lm = (LogicManager) evt.getSource();
      if (lm == this) doGUI();
    }
    else if (eventType == VisBioEvent.STATE_CHANGED) {
      Object src = evt.getSource();
      if (src instanceof OptionManager) {
        OptionManager om = (OptionManager) src;
// Window docking is too buggy; disable it
//        BooleanOption option = (BooleanOption) om.getOption(DOCKING);
//        if (option != null) setDocking(option.getValue());
        BooleanOption option = (BooleanOption) om.getOption(HIDE_ALL);
        if (option != null) setHideAll(option.getValue());
      }
    }
  }

  /** Gets the number of tasks required to initialize this logic manager. */
  public int getTasks() { return 2; }


  // -- WindowListener API methods --

  public void windowDeiconified(WindowEvent e) {
    if (hideAll) {
      // program has been restored; show all previously visible windows
      restoreWindows();
    }
  }

  public void windowIconified(WindowEvent e) {
    if (hideAll) {
      // program has been minimized; hide currently visible windows
      hideWindows();
    }
  }

  public void windowActivated(WindowEvent e) { }
  public void windowClosed(WindowEvent e) { }
  public void windowClosing(WindowEvent e) { }
  public void windowDeactivated(WindowEvent e) { }
  public void windowOpened(WindowEvent e) { }


  // -- Saveable API methods --

  /** Writes the current state to the given DOM element ("VisBio"). */
  public void saveState(Element el) throws SaveException {
    Element container = XMLUtil.createChild(el, "Windows");
    Enumeration en = windows.keys();
    while (en.hasMoreElements()) {
      Window w = (Window) en.nextElement();
      String name = SwingUtil.getWindowTitle(w);
      Element e = XMLUtil.createChild(container, "Window");
      e.setAttribute("name", name);
      e.setAttribute("visible", "" + w.isVisible());
      Rectangle r = w.getBounds();
      e.setAttribute("x", "" + r.x);
      e.setAttribute("y", "" + r.y);
      e.setAttribute("width", "" + r.width);
      e.setAttribute("height", "" + r.height);
    }
  }

  /** Restores the current state from the given DOM element ("VisBio"). */
  public void restoreState(Element el) throws SaveException {
    Element container = XMLUtil.getFirstChild(el, "Windows");
    windowStates.clear();
    Element[] e = XMLUtil.getChildren(container, "Window");
    for (int i=0; i<e.length; i++) {
      String name = e[i].getAttribute("name");
      String vis = e[i].getAttribute("visible");
      int x = Integer.parseInt(e[i].getAttribute("x"));
      int y = Integer.parseInt(e[i].getAttribute("y"));
      int w = Integer.parseInt(e[i].getAttribute("width"));
      int h = Integer.parseInt(e[i].getAttribute("height"));
      WindowState ws = new WindowState(name,
        vis.equalsIgnoreCase("true"), x, y, w, h);
      WindowInfo winfo = getWindowByTitle(name);
      if (winfo == null) windowStates.put(name, ws); // remember position
      else winfo.setState(ws); // window already exists; set position
    }
  }


  // -- Helper methods --

  /** Adds window-related GUI components to VisBio. */
  protected void doGUI() {
    // window listener
    bio.setSplashStatus("Initializing windowing logic");
    docker = new Docker();
    docker.setEnabled(docking);
    addWindow(bio);
    bio.addWindowListener(this);

    // options menu
    bio.setSplashStatus(null);
    OptionManager om = (OptionManager) bio.getManager(OptionManager.class);
    // window docking is too buggy; disable it
    //om.addBooleanOption("General", DOCKING, 'd',
    //  "Toggles whether window docking features are enabled", docking);
    om.addBooleanOption("General", HIDE_ALL, 'h', "Toggles whether all " +
      "VisBio windows disappear when main window is minimized", hideAll);
  }

  /** Sets whether window docking features are enabled. */
  protected void setDocking(boolean docking) {
    if (this.docking == docking) return;
    this.docking = docking;
    docker.setEnabled(docking);
  }

  /**
   * Sets whether minimizing the main VisBio window
   * hides the other VisBio windows.
   */
  protected void setHideAll(boolean hideAll) {
    if (this.hideAll == hideAll) return;
    this.hideAll = hideAll;
  }

  /** Propagates the given menu bar across all registered frames. */
  protected void doMenuBars(JMenuBar master) {
    Enumeration en = windows.keys();
    while (en.hasMoreElements()) {
      Window w = (Window) en.nextElement();
      if (!(w instanceof JFrame)) continue;
      JFrame f = (JFrame) w;
      if (f.getJMenuBar() != master) {
        f.setJMenuBar(SwingUtil.cloneMenuBar(master));
      }
    }
  }

  /**
   * Gets window information about the first window
   * matching the specified window title.
   */
  protected WindowInfo getWindowByTitle(String name) {
    Enumeration en = windows.elements();
    while (en.hasMoreElements()) {
      WindowInfo winfo = (WindowInfo) en.nextElement();
      Window w = winfo.getWindow();
      if (name.equals(SwingUtil.getWindowTitle(w))) return winfo;
    }
    return null;
  }

}
