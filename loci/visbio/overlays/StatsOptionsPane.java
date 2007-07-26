//
// StatsOptionsPane.java
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

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import loci.visbio.VisBioFrame;
import loci.visbio.state.BioOption;
import loci.visbio.state.OptionManager;

/**
 * A tabbed pane full of checkboxes.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/visbio/overlays/StatsOptionsPane.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/visbio/overlays/StatsOptionsPane.java">SVN</a></dd></dl>
 */
public class StatsOptionsPane extends JPanel implements ActionListener {

  // -- Constants --

  /** Action command for 'Toggle Current Tab' button. */
  private static final String CURRENT = "current";

  /** Action command for 'Toggle All Tabs' button. */
  private static final String ALL = "all";

  // -- Fields --

  /** All of the check boxes contained in all tabs. */
  protected JCheckBox[][] checkBoxes;

  /** The tabbed pane contained in this object. */
  protected JTabbedPane tabs;

  // -- Constructor --

  /** Creates an StatsOptionsPane object. */
  public StatsOptionsPane() {
    tabs = makeTabs();
    tabs.setPreferredSize(new Dimension(190, 230)); // trial and error
    JPanel buttons = makeButtons();
    JPanel pane = makePane(tabs, buttons);
    this.add(pane);
  }

  // -- StatsOptionsPane API methods --

  /** Saves current pane selections to OptionManager. */
  public void saveSettings() {
    OptionManager om = (OptionManager)
      VisBioFrame.getVisBio().getManager(OptionManager.class);

    String[] overlayTypes = OverlayUtil.getOverlayTypes();

    for (int type=0; type<overlayTypes.length; type++) {
      String[] statTypes = OverlayUtil.getStatTypes(overlayTypes[type]);
      for (int i=0; i<statTypes.length; i++) {
        String name = overlayTypes[type] + "." + statTypes[i];
        BioOption opt = om.getOption(name);
        JCheckBox optBox = (JCheckBox) opt.getComponent();
        JCheckBox proxyBox = checkBoxes[type][i];
        optBox.setSelected(proxyBox.isSelected());
      }
    }
  }

  /** Loads current pane selections from OptionManager. */
  public void loadSettings() {
    OptionManager om = (OptionManager)
      VisBioFrame.getVisBio().getManager(OptionManager.class);
    String[] overlayTypes = OverlayUtil.getOverlayTypes();

    for (int type=0; type<overlayTypes.length; type++) {
      String[] statTypes = OverlayUtil.getStatTypes(overlayTypes[type]);
      for (int i=0; i<statTypes.length; i++) {
        String name = overlayTypes[type] + "." + statTypes[i];
        BioOption opt = om.getOption(name);
        JCheckBox optBox = (JCheckBox) opt.getComponent();
        checkBoxes[type][i].setSelected(optBox.isSelected());
      }
    }
  }

  // -- ActionListener interface methods --

  /** Change selection state of check boxes depending on button pressed. */
  public void actionPerformed(ActionEvent e) {
    if (ALL.equals(e.getActionCommand())) toggleAllTabs();
    else if (CURRENT.equals(e.getActionCommand())) toggleCurrentTab();
  }

  // -- Helper Methods --

  /** Makes the tabs of this ExportOptionsPane object. */
  private JTabbedPane makeTabs() {
    OptionManager om = (OptionManager)
      VisBioFrame.getVisBio().getManager(OptionManager.class);

    String[] overlayTypes = OverlayUtil.getOverlayTypes();
    checkBoxes = new JCheckBox[overlayTypes.length][];

    // populate checkbox array
    for (int type=0; type<overlayTypes.length; type++) {
      String[] statTypes = OverlayUtil.getStatTypes(overlayTypes[type]);
      checkBoxes[type] = new JCheckBox[statTypes.length];
      for (int i=0; i<statTypes.length; i++) {
        checkBoxes[type][i] = new JCheckBox(statTypes[i]);
      }
    }

    this.loadSettings();

    JTabbedPane jtp = new JTabbedPane();
    // make a tab for each overlay type
    for (int type=0; type<overlayTypes.length; type++) {
      // build a string representing the row heights for this tab
      String rowString = "3dlu, ";
      for (int i=0; i<checkBoxes[type].length - 1; i++) {
        rowString += "pref, 3dlu, ";
      }
      rowString += "pref, 3dlu, pref";

      // initialize JGoodies stuff
      FormLayout fl = new FormLayout("15dlu, pref, 15dlu",
        rowString);

      PanelBuilder builder = new PanelBuilder(fl);
      CellConstraints cc = new CellConstraints();

      // populate panel with the appropriate checkboxes
      for (int i=0; i<checkBoxes[type].length; i++) {
        builder.add(checkBoxes[type][i],  cc.xy(2, 2*(i + 1)));
      }

      // add a tab to the instance
      JPanel panel = builder.getPanel();
      jtp.addTab(overlayTypes[type], null, panel,
          overlayTypes[type] + " statistics");
    }

    return jtp;
  }

  /** Makes a button bar with 2 buttons to toggle options. */
  private JPanel makeButtons() {
    JButton toggleCurrent = new JButton("Toggle Current Tab");
    toggleCurrent.setActionCommand(CURRENT);
    toggleCurrent.addActionListener(this);
    toggleCurrent.setMnemonic('c');
    JButton toggleAll = new JButton("Toggle All Tabs");
    toggleAll.setActionCommand(ALL);
    toggleAll.addActionListener(this);
    toggleAll.setMnemonic('a');

    ButtonBarBuilder builder = new ButtonBarBuilder();
    builder.addGridded(toggleCurrent);
    builder.addRelatedGap();
    builder.addGlue();
    builder.addGridded(toggleAll);
    JPanel panel = builder.getPanel();
    return panel;
  }

  private JPanel makePane(JTabbedPane jtp, JPanel buttons) {
    FormLayout fl = new FormLayout("3dlu, pref, 3dlu",
        "pref, 3dlu, pref, 3dlu, pref");
    PanelBuilder builder = new PanelBuilder(fl);
    CellConstraints cc = new CellConstraints();

    builder.addLabel("Select statistics to save/export:", cc.xy(2, 1));
    builder.add(jtp, cc.xy(2, 3));
    builder.add(buttons, cc.xy(2, 5));
    JPanel panel = builder.getPanel();
    return panel;
  }

  /**
   *  Selects or deselects all checkboxes in current tab.
   *  Guesses which way to toggle based on number of selected
   *  check boxes in current tab--aims to toggle as many
   *  check boxes as possible
   */
  protected void toggleCurrentTab() {
    int ndx = tabs.getSelectedIndex();
    JCheckBox[] currentTabCheckBoxes = checkBoxes[ndx];
    int netSelected = 0;
    for (int i=0; i<currentTabCheckBoxes.length; i++) {
      JCheckBox box = currentTabCheckBoxes[i];
      if (box.isSelected()) netSelected++;
      else netSelected--;
    }

    boolean moreSelected = netSelected < 0 ? false : true;

    for (int i=0; i<currentTabCheckBoxes.length; i++) {
      JCheckBox box = currentTabCheckBoxes[i];
      box.setSelected(!moreSelected);
    }
  }

  /** Selects or deselects all checkboxes in current tab.
   *  Guesses which way to toggle based on number of selected
   *  check boxes in _current_ tab only, aiming to toggle as many
   *  check boxes as possible in this tab
   */
  protected void toggleAllTabs() {
    int ndx = tabs.getSelectedIndex();
    JCheckBox[] currentTabCheckBoxes = checkBoxes[ndx];
    int netSelected = 0;

    // poll current tab only
    for (int i=0; i<currentTabCheckBoxes.length; i++) {
      JCheckBox box = currentTabCheckBoxes[i];
      if (box.isSelected()) netSelected++;
      else netSelected--;
    }

    boolean moreSelected = netSelected < 0 ? false : true;

    // loop thru all check boxes and toggle.
    for (int type=0; type<checkBoxes.length; type++) {
      for (int i=0; i<checkBoxes[type].length; i++) {
        JCheckBox box = checkBoxes[type][i];
        box.setSelected(!moreSelected);
      }
    }
  }
}

