//
// HelpManager.java
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

package loci.visbio.help;

import java.awt.event.KeyEvent;
import javax.swing.*;
import loci.visbio.*;
import loci.visbio.data.*;
import loci.visbio.state.BooleanOption;
import loci.visbio.state.OptionManager;
import loci.visbio.util.SwingUtil;

/** HelpManager is the manager encapsulating VisBio's help window logic. */
public class HelpManager extends LogicManager {

  // -- Constants --

  /** String for displaying new data. */
  public static final String DISPLAY_DATA =
    "Ask about displaying new data objects";


  // -- Fields --

  /** Help dialog for detailing basic program usage. */
  private HelpWindow helpWindow;


  // -- Constructor --

  /** Constructs an exit manager. */
  public HelpManager(VisBioFrame bio) { super(bio); }


  // -- HelpManager API methods --

  /** Adds a new help topic. */
  public void addHelpTopic(String name, String source) {
    helpWindow.addTopic(name, source);
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
      String msg = evt.getMessage();
      if ("add data".equals(msg)) {
        OptionManager om = (OptionManager) bio.getManager(OptionManager.class);
        BooleanOption option = (BooleanOption) om.getOption(DISPLAY_DATA);
        if (option.getValue()) { // ask about displaying new data objects
          DataManager dm = (DataManager) bio.getManager(DataManager.class);
          DataTransform data = (DataTransform) dm.getSelectedData();

          // determine whether data can be displayed in 2D and/or 3D
          boolean isData = data != null;
          boolean canDisplay2D = data != null && data.canDisplay2D();
          boolean canDisplay3D = data != null && data.canDisplay3D();
          boolean canDisplay = canDisplay2D || canDisplay3D;

          if (canDisplay) {
            DataControls dc = dm.getControlPanel();
            String twoD = "Visualize in 2D", threeD = "Visualize in 3D";
            String no = "Do not visualize now";
            String[] options = canDisplay2D && canDisplay3D ?
              new String[] {twoD, threeD, no} :
              new String[] {canDisplay2D ? twoD : threeD, no};
            String initial = canDisplay3D ? threeD : twoD;
            String rval = (String) JOptionPane.showInputDialog(dc,
              "Would you like to visualize the \"" + data +
              "\" object immediately?", "VisBio", JOptionPane.QUESTION_MESSAGE,
              null, options, initial);
            if (rval != null && !rval.equals(no)) {
              dc.doNewDisplay(rval.equals(threeD));
            }
          }
        }
      }
    }
  }

  /** Gets the number of tasks required to initialize this logic manager. */
  public int getTasks() { return 4; }


  // -- Helper methods --

  /** Adds help-related GUI components to VisBio. */
  private void doGUI() {
    bio.setSplashStatus("Initializing help logic");
    helpWindow = new HelpWindow();
    WindowManager wm = (WindowManager) bio.getManager(WindowManager.class);
    wm.addWindow(helpWindow);

    // help menu
    bio.setSplashStatus(null);

    JMenuItem help = bio.addMenuItem("Help", "VisBio Help",
      "loci.visbio.help.HelpManager.helpHelp", 'h');
    KeyStroke helpStroke = VisBioFrame.MAC_OS_X ? KeyStroke.getKeyStroke(
      new Character('?'), SwingUtil.MENU_MASK) :
      KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0);
    help.setAccelerator(helpStroke);

    if (!VisBioFrame.MAC_OS_X) {
      bio.addMenuSeparator("Help");
      bio.addMenuItem("Help", "About",
        "loci.visbio.help.HelpManager.helpAbout", 'a');
    }

    // options
    bio.setSplashStatus(null);
    OptionManager om = (OptionManager) bio.getManager(OptionManager.class);
    om.addBooleanOption("General", DISPLAY_DATA, 'd',
      "Toggles whether VisBio asks about automatically " +
      "displaying new data objects", true);

    // help topics
    bio.setSplashStatus(null);
    addHelpTopic("Introduction", "introduction.html");
  }


  // -- Menu commands --

  /** Brings up a window detailing basic program usage. */
  public void helpHelp() {
    WindowManager wm = (WindowManager) bio.getManager(WindowManager.class);
    wm.showWindow(helpWindow);
  }

  /** Brings up VisBio's about dialog. */
  public void helpAbout() {
    String about = VisBio.TITLE + " " + VisBio.VERSION +
      ", built " + VisBio.DATE + "\nWritten by " + VisBio.AUTHOR + "\n" +
      "http://www.loci.wisc.edu/visbio/";
    JOptionPane.showMessageDialog(bio, about,
      "About VisBio", JOptionPane.INFORMATION_MESSAGE);
  }

}
