//
// PanelManager.java
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

import java.util.Vector;
import javax.swing.*;
import loci.visbio.help.HelpManager;
import loci.visbio.util.SwingUtil;

/** PanelManager is the manager encapsulating VisBio's control panel logic. */
public class PanelManager extends LogicManager {

  // -- GUI components --

  /** Control panels. */
  private Vector panels;

  /** Tabbed pane containing control panels. */
  private JTabbedPane tabs;


  // -- Constructor --

  /** Constructs a panel manager. */
  public PanelManager(VisBioFrame bio) { super(bio); }


  // -- PanelManager API methods --

  /** Adds a new control panel. */
  public void addPanel(ControlPanel cpl) {
    String name = cpl.getName();

    // create control panel containers
    JFrame w = new JFrame(name);
    WindowManager wm = (WindowManager) bio.getManager(WindowManager.class);
    wm.addWindow(w);
    panels.add(cpl);

    tabs.addTab(name, null, cpl, cpl.getTip());

    bio.generateEvent(this, "add " + name + " panel", false);
  }

  /**
   * Enlarges a control panel to its preferred width
   * and/or height if it is too small.
   */
  public void repack(ControlPanel cpl) {
    int ndx = panels.indexOf(cpl);
    if (ndx < 0) return;
    SwingUtil.repack(bio);
  }

  /** Gets the control panel with the given name. */
  public ControlPanel getPanel(String name) {
    for (int i=0; i<panels.size(); i++) {
      ControlPanel cpl = (ControlPanel) panels.elementAt(i);
      if (cpl.getName().equals(name)) return cpl;
    }
    return null;
  }


  // -- LogicManager API methods --

  /** Called to notify the logic manager of a VisBio event. */
  public void doEvent(VisBioEvent evt) {
    int eventType = evt.getEventType();
    if (eventType == VisBioEvent.LOGIC_ADDED) {
      Object src = evt.getSource();
      if (src == this) doGUI();
    }
  }

  /** Gets the number of tasks required to initialize this logic manager. */
  public int getTasks() { return 3; }


  // -- Helper methods --

  /** Adds base control panel GUI components to VisBio. */
  private void doGUI() {
    bio.setSplashStatus("Initializing control panel logic");
    panels = new Vector();

    // control panel containers
    tabs = new JTabbedPane();
    bio.getContentPane().add(tabs);

    // help window
    bio.setSplashStatus(null);
    HelpManager hm = (HelpManager) bio.getManager(HelpManager.class);
    hm.addHelpTopic("Control panels", "control_panels.html");
  }

}
