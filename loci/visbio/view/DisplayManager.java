//
// DisplayManager.java
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

package loci.visbio.view;

import java.awt.Component;
import java.util.Vector;
import javax.swing.JOptionPane;
import loci.ome.xml.CAElement;
import loci.ome.xml.OMEElement;
import loci.visbio.*;
import loci.visbio.help.HelpManager;
import loci.visbio.state.*;

/** DisplayManager is the manager encapsulating VisBio's 2D and 3D displays. */
public class DisplayManager extends LogicManager {

  // -- Constants --

  /** Maximum number of simultaneous displays. */
  public static final int MAX_DISPLAYS = 32;

  /** String for ImageJ quit warning. */
  public static final String WARN_IMAGEJ =
    "Warn about problem where quitting ImageJ also quits VisBio";


  // -- Fields --

  /** Counter for display names. */
  protected int nextId;


  // -- Control panel --

  /** Displays control panel. */
  protected DisplayControls displayControls;


  // -- Constructor --

  /** Constructs a display manager. */
  public DisplayManager(VisBioFrame bio) { super(bio); }


  // -- DisplayManager API methods --

  /** Pops up a dialog allowing the user to create a new display. */
  public DisplayWindow createDisplay(Component parent, boolean threeD) {
    nextId++;
    DisplayWindow window = createDisplay(parent, "display" + nextId, threeD);
    if (window == null) nextId--;
    return window;
  }

  /**
   * Pops up a dialog allowing the user to create a new display,
   * with the given default name.
   */
  public DisplayWindow createDisplay(Component parent,
    String defaultName, boolean threeD)
  {
    DisplayWindow window = null;
    if (getDisplays().length < MAX_DISPLAYS) {
      String name = (String) JOptionPane.showInputDialog(parent,
        "Display name:", "Add " + (threeD ? "3D" : "2D") + " display",
        JOptionPane.INFORMATION_MESSAGE, null, null, defaultName);
      if (name != null) {
        window = new DisplayWindow(this, name, threeD);
        addDisplay(window);
      }
    }
    else {
      JOptionPane.showMessageDialog(parent,
        "Sorry, but there is a limit of " + MAX_DISPLAYS +
        " displays maximum.\nPlease reuse or delete one of your existing " +
        "displays.", "Cannot create display", JOptionPane.ERROR_MESSAGE);
    }
    return window;
  }

  /** Adds a display to the list of current displays. */
  public void addDisplay(DisplayWindow d) {
    displayControls.addDisplay(d);
    WindowManager wm = (WindowManager) bio.getManager(WindowManager.class);
    wm.addWindow(d, false);
    bio.generateEvent(this, "create new display: " + d.getName(), true);
  }

  /** Removes a display from the list of current displays. */
  public void removeDisplay(DisplayWindow d) {
    displayControls.removeDisplay(d);
    bio.generateEvent(this, "remove display: " + d.getName(), true);
  }

  /** Gets the current list of displays. */
  public DisplayWindow[] getDisplays() {
    return displayControls.getDisplays();
  }

  /** Gets associated control panel. */
  public DisplayControls getControls() { return displayControls; }


  // -- LogicManager API methods --

  /** Called to notify the logic manager of a VisBio event. */
  public void doEvent(VisBioEvent evt) {
    int eventType = evt.getEventType();
    if (eventType == VisBioEvent.LOGIC_ADDED) {
      LogicManager lm = (LogicManager) evt.getSource();
      if (lm == this) doGUI();
    }
  }

  /** Gets the number of tasks required to initialize this logic manager. */
  public int getTasks() { return 3; }


  // -- Saveable API methods --

  protected static final String DISPLAY_MANAGER = "VisBio_DisplayManager";

  /** Writes the current state to the given XML object. */
  public void saveState(OMEElement ome) throws SaveException {
    DisplayWindow[] windows = getDisplays();

    // save number of displays
    CAElement custom = ome.getCustomAttr();
    custom.createElement(DISPLAY_MANAGER);
    custom.setAttribute("count", "" + windows.length);

    // save all displays
    for (int i=0; i<windows.length; i++) windows[i].saveState(ome, i);
  }

  /** Restores the current state from the given XML object. */
  public void restoreState(OMEElement ome) throws SaveException {
    CAElement custom = ome.getCustomAttr();

    // read number of displays
    String[] dCount = custom.getAttributes(DISPLAY_MANAGER, "count");
    int count = -1;
    if (dCount != null && dCount.length > 0) {
      try { count = Integer.parseInt(dCount[0]); }
      catch (NumberFormatException exc) { }
    }
    if (count < 0) {
      System.err.println("Failed to restore display count.");
      count = 0;
    }

    Vector vn = new Vector();
    for (int i=0; i<count; i++) {
      // construct display
      DisplayWindow window = new DisplayWindow(this);

      // restore display state
      window.restoreState(ome, i);
      vn.add(window);
    }

    // merge old and new display lists
    DisplayWindow[] windows = getDisplays();
    Vector vo = new Vector(windows.length);
    for (int i=0; i<windows.length; i++) vo.add(windows[i]);
    StateManager.mergeStates(vo, vn);

    // add new displays to display list
    int nlen = vn.size();
    for (int i=0; i<nlen; i++) {
      DisplayWindow display = (DisplayWindow) vn.elementAt(i);
      if (!vo.contains(display)) addDisplay(display);
    }

    // purge old displays from display list
    int olen = vo.size();
    for (int i=0; i<olen; i++) {
      DisplayWindow display = (DisplayWindow) vo.elementAt(i);
      if (!vn.contains(display)) removeDisplay(display);
    }
  }


  // -- Helper methods --

  /** Adds display-related GUI components to VisBio. */
  protected void doGUI() {
    // control panel
    bio.setSplashStatus("Initializing display logic");
    displayControls = new DisplayControls(this);
    PanelManager pm = (PanelManager) bio.getManager(PanelManager.class);
    pm.addPanel(displayControls);

    // options
    bio.setSplashStatus(null);
    OptionManager om = (OptionManager) bio.getManager(OptionManager.class);
    om.addBooleanOption("Warnings", WARN_IMAGEJ, 'i',
      "Toggles whether VisBio displays a warning about " +
      "how quitting ImageJ also quits VisBio", true);


    // help topics
    bio.setSplashStatus(null);
    HelpManager hm = (HelpManager) bio.getManager(HelpManager.class);
    hm.addHelpTopic("Control panels/Displays panel", "displays_panel.html");
    String s = "Display windows";
    hm.addHelpTopic(s, "display_windows.html");
    hm.addHelpTopic(s + "/Appearance settings", "appearance.html");
    hm.addHelpTopic(s + "/Appearance settings/Capture window", "capture.html");
    hm.addHelpTopic(s + "/Data settings", "data_settings.html");
    hm.addHelpTopic(s + "/Data settings/Colors dialog box", "colors.html");
    hm.addHelpTopic(s + "/Position settings", "position.html");
  }

}
