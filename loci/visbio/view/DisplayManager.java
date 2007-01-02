//
// DisplayManager.java
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

package loci.visbio.view;

import com.jgoodies.plaf.LookUtils;
import java.awt.Component;
import java.util.Vector;
import javax.swing.JOptionPane;
import loci.visbio.*;
import loci.visbio.help.HelpManager;
import loci.visbio.state.*;
import loci.visbio.util.XMLUtil;
import org.w3c.dom.Element;

/** DisplayManager is the manager encapsulating VisBio's 2D and 3D displays. */
public class DisplayManager extends LogicManager {

  // -- Constants --

  /** Maximum number of simultaneous displays. */
  public static final int MAX_DISPLAYS = 32;

  /** Default maximum resolution for images in a stack. */
  public static final int DEFAULT_STACK_RESOLUTION = 192;

  /** Default eye separation distance for stereo displays. */
  public static final double DEFAULT_EYE_SEPARATION = 0.002;

  /** String for image stack resolution limit option. */
  public static final String STACK_LIMIT = "Limit image stack resolution";

  /** String for nicest transparency option. */
  public static final String NICE_ALPHA = "Use nicest transparency";

  /** String for 3D texturing option. */
  public static final String TEXTURE3D = "Use 3D texturing";

  /** String for eye separation setting. */
  public static final String EYE_DISTANCE = "Stereo eye separation";

  /** String for ImageJ quit warning. */
  public static final String WARN_IMAGEJ =
    "Warn about problem where quitting VisBio also quits ImageJ";

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

  /** Gets maximum resolution of stacked images from VisBio options. */
  public int[] getStackResolution() {
    OptionManager om = (OptionManager) bio.getManager(OptionManager.class);
    ResolutionToggleOption opt = (ResolutionToggleOption)
      om.getOption(STACK_LIMIT);
    if (opt.getValue()) return new int[] {opt.getValueX(), opt.getValueY()};
    else return new int[] {Integer.MAX_VALUE, Integer.MAX_VALUE};
  }

  /** Gets whether to use nicest transparency mode from VisBio options. */
  public boolean isNiceTransparency() {
    OptionManager om = (OptionManager) bio.getManager(OptionManager.class);
    BooleanOption opt = (BooleanOption) om.getOption(NICE_ALPHA);
    return opt.getValue();
  }

  /** Gets whether to use 3D texturing from VisBio options. */
  public boolean is3DTextured() {
    OptionManager om = (OptionManager) bio.getManager(OptionManager.class);
    BooleanOption opt = (BooleanOption) om.getOption(TEXTURE3D);
    return opt.getValue();
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
    else if (eventType == VisBioEvent.STATE_CHANGED) {
      String msg = evt.getMessage();
      if ("tweak options".equals(msg)) {
        boolean nice = isNiceTransparency();
        boolean texture3d = is3DTextured();
        DisplayWindow[] dw = getDisplays();
        for (int i=0; i<dw.length; i++) {
          if (!LookUtils.IS_OS_MAC) dw[i].setTransparencyMode(nice);
          dw[i].set3DTexturing(texture3d);
        }
      }
    }
  }

  /** Gets the number of tasks required to initialize this logic manager. */
  public int getTasks() { return 3; }

  // -- Saveable API methods --

  /** Writes the current state to the given DOM element ("VisBio"). */
  public void saveState(Element el) throws SaveException {
    DisplayWindow[] windows = getDisplays();
    Element child = XMLUtil.createChild(el, "Displays");
    for (int i=0; i<windows.length; i++) windows[i].saveState(child);
  }

  /** Restores the current state from the given DOM element ("VisBio"). */
  public void restoreState(Element el) throws SaveException {
    Element child = XMLUtil.getFirstChild(el, "Displays");
    Element[] els = XMLUtil.getChildren(child, "Display");

    // create new display list
    Vector vn = new Vector();
    for (int i=0; i<els.length; i++) {
      // construct display
      DisplayWindow window = new DisplayWindow(this);

      // restore display state
      window.restoreState(els[i]);
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
    pm.addPanel(displayControls, 1, 0, 1, 1, "350:grow", "pref");

    // options
    bio.setSplashStatus(null);
    OptionManager om = (OptionManager) bio.getManager(OptionManager.class);
    int stackRes = DEFAULT_STACK_RESOLUTION;
    om.addOption("Visualization", new ResolutionToggleOption(STACK_LIMIT, 'l',
      "Adjusts resolution limit of image stacks", true, stackRes, stackRes));
    om.addBooleanOption("Visualization", NICE_ALPHA, 'n',
      "Toggles quality of transparency behavior", true);
    om.addBooleanOption("Visualization", TEXTURE3D, '3',
      "Toggles whether 3D textures are used for volume rendering", true);
    om.addNumericOption("Visualization", EYE_DISTANCE, null,
      "Adjusts eye separation for stereo displays", DEFAULT_EYE_SEPARATION);
    om.addBooleanOption("Warnings", WARN_IMAGEJ, 'i',
      "Toggles whether VisBio displays a warning about " +
      "how quitting VisBio also quits ImageJ", true);

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
