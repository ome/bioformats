//
// WindowManager.java
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

package loci.visbio;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.*;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import loci.ome.xml.CAElement;
import loci.ome.xml.OMEElement;
import loci.visbio.state.*;
import loci.visbio.util.Docker;
import loci.visbio.util.SwingUtil;

/**
 * WindowManager is the manager encapsulating VisBio's window logic,
 * including docking, resizing, minimization and cursor control.
 */
public class WindowManager extends LogicManager implements WindowListener {

  // -- Constants --

  /** String for window docking option. */
  public static final String DOCKING = "Window docking";


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
  protected boolean docking = true;

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
      Cursor c = wait ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : null;
      Enumeration en = windows.keys();
      while (en.hasMoreElements()) {
        Window w = (Window) en.nextElement();
        SwingUtil.setWaitCursor(w, c);
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

  /** Hides all visible windows. */
  public void hideWindows() {
    Enumeration en = windows.keys();
    while (en.hasMoreElements()) {
      Window w = (Window) en.nextElement();
      if (w.isVisible()) {
        visible.add(w);
        w.setVisible(false);
      }
    }
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
        BooleanOption option = (BooleanOption) om.getOption(DOCKING);
        setDocking(option.getValue());
      }
    }
  }

  /** Gets the number of tasks required to initialize this logic manager. */
  public int getTasks() { return 2; }


  // -- WindowListener API methods --

  public void windowDeiconified(WindowEvent e) {
    // program has been restored; show all previously visible windows
    for (int i=0; i<visible.size(); i++) {
      Window w = (Window) visible.elementAt(i);
      w.setVisible(true);
    }
    visible.removeAllElements();
    bio.toFront();
  }

  public void windowIconified(WindowEvent e) { hideWindows(); }

  public void windowActivated(WindowEvent e) { }
  public void windowClosed(WindowEvent e) { }
  public void windowClosing(WindowEvent e) { }
  public void windowDeactivated(WindowEvent e) { }
  public void windowOpened(WindowEvent e) { }


  // -- Saveable API methods --

  protected static final String WINDOW_PARAMS = "VisBio_WindowParams";

  /** Writes the current state to the given XML object. */
  public void saveState(OMEElement ome) throws SaveException {
    // save window positions
    Enumeration en = windows.keys();
    while (en.hasMoreElements()) {
      Window w = (Window) en.nextElement();
      String name = SwingUtil.getWindowTitle(w);
      CAElement custom = ome.getCustomAttr();
      custom.createElement(WINDOW_PARAMS);
      custom.setAttribute("name", name);
      custom.setAttribute("visible", "" + w.isVisible());
      Rectangle r = w.getBounds();
      custom.setAttribute("x", "" + r.x);
      custom.setAttribute("y", "" + r.y);
      custom.setAttribute("width", "" + r.width);
      custom.setAttribute("height", "" + r.height);
    }
  }

  /** Restores the current state from the given XML object. */
  public void restoreState(OMEElement ome) throws SaveException {
    // restore window positions
    CAElement custom = ome.getCustomAttr();
    String[] names = custom.getAttributes(WINDOW_PARAMS, "name");
    String[] vis = custom.getAttributes(WINDOW_PARAMS, "visible");
    String[] x = custom.getAttributes(WINDOW_PARAMS, "x");
    String[] y = custom.getAttributes(WINDOW_PARAMS, "y");
    String[] w = custom.getAttributes(WINDOW_PARAMS, "width");
    String[] h = custom.getAttributes(WINDOW_PARAMS, "height");

    // remember these positions for windows that have not yet been registered
    windowStates.clear();
    for (int i=0; i<names.length; i++) {
      WindowState ws = new WindowState(names[i],
        vis[i].equalsIgnoreCase("true"),
        Integer.parseInt(x[i]), Integer.parseInt(y[i]),
        Integer.parseInt(w[i]), Integer.parseInt(h[i]));
      WindowInfo winfo = getWindowByName(names[i]);
      if (winfo == null) windowStates.put(names[i], ws); // remember position
      else winfo.setState(ws); // window already exists; set position
    }
  }


  // -- Helper methods --

  /** Adds window-related GUI components to VisBio. */
  protected void doGUI() {
    // window listener
    bio.setSplashStatus("Initializing windowing logic");
    docker = new Docker();
    docker.addWindow(bio);
    bio.addWindowListener(this);

    // options menu
    bio.setSplashStatus(null);
    OptionManager om = (OptionManager) bio.getManager(OptionManager.class);
    om.addBooleanOption("General", DOCKING, 'd',
      "Toggles whether window docking features are enabled", docking);
  }

  /** Sets whether window docking features are enabled. */
  protected void setDocking(boolean docking) {
    if (this.docking == docking) return;
    this.docking = docking;

    docker.setEnabled(docking);
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
   * matching the specified name.
   */
  protected WindowInfo getWindowByName(String name) {
    Enumeration en = windows.elements();
    while (en.hasMoreElements()) {
      WindowInfo winfo = (WindowInfo) en.nextElement();
      Window w = winfo.getWindow();
      if (w.getName().equals(name)) return winfo;
    }
    return null;
  }

}
