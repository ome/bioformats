//
// OverlayManager.java
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

package loci.visbio.overlays;

import loci.visbio.*;
import loci.visbio.data.DataManager;
import loci.visbio.help.HelpManager;
import loci.visbio.state.OptionManager;
import loci.visbio.state.SpreadsheetLaunchOption;

/**
 * OverlayManager is the manager encapsulating VisBio's overlay logic.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/visbio/overlays/OverlayManager.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/visbio/overlays/OverlayManager.java">SVN</a></dd></dl>
 */
public class OverlayManager extends LogicManager {

  // -- Constructor --

  /** Constructs a window manager. */
  public OverlayManager(VisBioFrame bio) { super(bio); }

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

  /** Adds overlay-related GUI components to VisBio. */
  protected void doGUI() {
    // overlay transform registration
    bio.setSplashStatus("Initializing overlay logic");
    DataManager dm = (DataManager) bio.getManager(DataManager.class);
    dm.registerDataType(OverlayTransform.class, "Overlays");

    // register Overlay options
    OptionManager om = (OptionManager) bio.getManager(OptionManager.class);
    String[] overlayTypes = OverlayUtil.getOverlayTypes();
    for (int i=0; i<overlayTypes.length; i++) {
      String[] statTypes = OverlayUtil.getStatTypes(overlayTypes[i]);
      for (int j=0; j<statTypes.length; j++) {
        String name = overlayTypes[i] + "." + statTypes[j];
        om.addBooleanOption("Overlays", name, '|',
            "Toggles whether the " + name + " statistic is exported or saved",
            true);
      }
    }

    // add option for launching spreadsheet automatically
    String path = "";
    try {
      path = SpreadsheetLauncher.getDefaultApplicationPath();
    }
    catch (SpreadsheetLaunchException ex) {}
    om.addOption("General", new SpreadsheetLaunchOption('s', path, true));

    // help window
    bio.setSplashStatus(null);
    HelpManager hm = (HelpManager) bio.getManager(HelpManager.class);
    hm.addHelpTopic("Data transforms/Overlays", "overlays.html");
  }

}
