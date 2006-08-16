//
// ExtManager.java
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

package loci.visbio.ext;

import loci.visbio.*;
import loci.visbio.data.DataManager;
import loci.visbio.help.HelpManager;

/**
 * ExtManager is the manager encapsulating VisBio's support for
 * external software interfaces (e.g., external programs or MATLAB).
 */
public class ExtManager extends LogicManager {


  // -- Constructor --

  /** Constructs an external interface manager. */
  public ExtManager(VisBioFrame bio) { super(bio); }


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


  // -- Helper methods --

  /** Adds external interface-related GUI components to VisBio. */
  private void doGUI() {
    // external program transform registration
    bio.setSplashStatus("Initializing external interfaces");
    DataManager dm = (DataManager) bio.getManager(DataManager.class);
    dm.registerDataType(ExternalProgram.class, "External program");

    // MATLAB transform registration
    bio.setSplashStatus(null);
    if (MatlabUtil.getMatlabVersion() != null) {
      dm.registerDataType(MatlabFunction.class, "MATLAB function");
    }

    // help window
    bio.setSplashStatus(null);
    HelpManager hm = (HelpManager) bio.getManager(HelpManager.class);
    hm.addHelpTopic("Data transforms/External programs", "external.html");
    hm.addHelpTopic("Data transforms/MATLAB functions", "matlab.html");
  }

}
