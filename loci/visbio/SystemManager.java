//
// SystemManager.java
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

import loci.visbio.help.HelpManager;

/**
 * SystemManager is the manager encapsulating
 * VisBio's system information report logic.
 */
public class SystemManager extends LogicManager {

  // -- Control panel --

  /** System control panel. */
  private SystemControls systemControls;


  // -- Constructor --

  /** Constructs a system manager. */
  public SystemManager(VisBioFrame bio) { super(bio); }


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
  public int getTasks() { return 2; }


  // -- Helper methods --

  /** Adds system-related GUI components to VisBio. */
  private void doGUI() {
    // control panel
    bio.setStatus("Initializing system information logic");
    systemControls = new SystemControls(this);
    PanelManager pm = (PanelManager) bio.getManager(PanelManager.class);
    pm.addPanel(systemControls);

    // help window
    bio.setStatus(null);
    HelpManager hm = (HelpManager) bio.getManager(HelpManager.class);
    hm.addHelpTopic("System", "system.html");
  }


  // -- Utility methods --

  /** Does some garbage collection, to free up memory. */
  public static void gc() {
    try {
      System.gc();
      Thread.sleep(100);
      System.runFinalization();
      Thread.sleep(100);
      System.gc();
      Thread.sleep(100);
      System.runFinalization();
      Thread.sleep(100);
    }
    catch (InterruptedException exc) { exc.printStackTrace(); }
  }

}
