//
// HelpManager.java
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

package loci.visbio.help;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.plaf.LookUtils;
import java.awt.event.KeyEvent;
import javax.swing.*;
import loci.visbio.*;
import loci.visbio.data.*;
import loci.visbio.state.*;
import loci.visbio.util.*;
import visad.util.GUIFrame;

/** HelpManager is the manager encapsulating VisBio's help window logic. */
public class HelpManager extends LogicManager {

  // -- Constants --

  /** String for displaying new data. */
  public static final String DISPLAY_DATA =
    "Ask about displaying new data objects";

  // -- Fields --

  /** Help dialog for detailing basic program usage. */
  protected HelpWindow helpWindow;

  // -- Constructor --

  /** Constructs an exit manager. */
  public HelpManager(VisBioFrame bio) { super(bio); }

  // -- HelpManager API methods --

  /** Adds a new help topic. */
  public void addHelpTopic(String name, String source) {
    helpWindow.addTopic(name, source);
  }

  /**
   * Prompts user for whether to visualize
   * the currently selected data object.
   */
  public void checkVisualization() {
    DataManager dm = (DataManager) bio.getManager(DataManager.class);
    DataTransform data = (DataTransform) dm.getSelectedData();
    if (data.getParent() != null) return; // ask for top-level objects only

    // determine whether data can be displayed in 2D and/or 3D
    boolean isData = data != null;
    boolean canDisplay2D = data != null && data.canDisplay2D();
    boolean canDisplay3D = data != null &&
      data.canDisplay3D() && DisplayUtil.canDo3D();
    boolean canDisplay = canDisplay2D || canDisplay3D;

    if (canDisplay) {
      // create option for 3D visualization
      ButtonGroup buttons = new ButtonGroup();
      JRadioButton vis3D = new JRadioButton("In 3D", canDisplay3D);
      vis3D.setEnabled(canDisplay3D);
      if (!LAFUtil.isMacLookAndFeel()) vis3D.setMnemonic('3');
      buttons.add(vis3D);

      // create option for 2D visualization
      JRadioButton vis2D = new JRadioButton("In 2D", !canDisplay3D);
      vis2D.setEnabled(canDisplay2D);
      if (!LAFUtil.isMacLookAndFeel()) vis2D.setMnemonic('2');
      buttons.add(vis2D);

      // create option for 2D visualization w/ overlays
      JRadioButton visOver = new JRadioButton("In 2D w/ Overlays", 
        !canDisplay3D);
      visOver.setEnabled(canDisplay2D);
      if (!LAFUtil.isMacLookAndFeel()) visOver.setMnemonic('o');
      buttons.add(visOver);

      // create option for no visualization
      JRadioButton visNot = new JRadioButton("Not now");
      if (!LAFUtil.isMacLookAndFeel()) visNot.setMnemonic('n');
      buttons.add(visNot);

      // create panel for asking user about immediate visualization
      PanelBuilder builder = new PanelBuilder(new FormLayout(
        "15dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref:grow", 
        "pref, 3dlu, pref"
      ));
      CellConstraints cc = new CellConstraints();
      builder.addLabel("Would you like to visualize the \"" +
        data + "\" object now?", cc.xyw(1, 1, 8));
      builder.add(vis3D, cc.xy(2, 3));
      builder.add(vis2D, cc.xy(4, 3));
      builder.add(visOver, cc.xy(6, 3));
      builder.add(visNot, cc.xy(8, 3));

      JPanel visPanel = builder.getPanel();

      // display message pane
      DataControls dc = dm.getControls();
      OptionManager om = (OptionManager)
        bio.getManager(OptionManager.class);
      boolean success = om.checkMessage(dc,
        DISPLAY_DATA, false, visPanel, "VisBio");
      if (success && !visNot.isSelected()) { 
        if (!visOver.isSelected()) dc.doNewDisplay(vis3D.isSelected());
        else dc.doNewDisplayWithOverlays();
      }
    }
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
        StateManager sm = (StateManager) bio.getManager(StateManager.class);
        BooleanOption option = (BooleanOption) om.getOption(DISPLAY_DATA);
        if (option.getValue() && !sm.isRestoring()) {
          // ask about displaying new data objects
          checkVisualization();
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
    KeyStroke helpStroke = LookUtils.IS_OS_MAC ? KeyStroke.getKeyStroke(
      new Character('?'), GUIFrame.MENU_MASK) :
      KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0);
    help.setAccelerator(helpStroke);

    if (!LookUtils.IS_OS_MAC) {
      bio.addMenuSeparator("Help");
      bio.addMenuItem("Help", "About",
        "loci.visbio.help.HelpManager.helpAbout", 'a');
      bio.setMenuShortcut("Help", "About", KeyEvent.VK_A);
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

  private void makeVisPanel() {
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
      (VisBio.DATE.equals("@da" + "te@") ? "" : (", built " + VisBio.DATE)) +
      "\nWritten by " + VisBio.AUTHOR + "\n" +
      "http://www.loci.wisc.edu/visbio/";
    JOptionPane.showMessageDialog(bio, about,
      "About VisBio", JOptionPane.INFORMATION_MESSAGE);
  }

}
