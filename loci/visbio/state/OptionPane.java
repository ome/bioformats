//
// OptionPane.java
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

package loci.visbio.state;

import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import loci.visbio.util.DialogPane;

/** OptionPane provides a dialog box for adjusting VisBio's options. */
public class OptionPane extends DialogPane {

  // -- Fields --

  /** Options manager. */
  private OptionManager om;

  /** Tabbed pane. */
  private JTabbedPane tabs;


  // -- Constructor --

  /** Creates a new options pane. */
  public OptionPane(OptionManager om) {
    super("VisBio Options");
    this.om = om;
    tabs = new JTabbedPane();
    add(tabs);
  }


  // -- OptionPane API methods --

  /** Adds an option with a boolean value to the specified tab. */
  public void addOption(String tab, BioOption option) {
    JPanel tabPanel = getTab(tab);
    Component c = option.getComponent();
    // CTR TODO options layout needs to be standardized to use FormLayout
    if (c instanceof JComponent) {
      JComponent jc = (JComponent) c;
      jc.setAlignmentX(JComponent.LEFT_ALIGNMENT);
    }
    tabPanel.add(c);
  }

  /** Initializes a tab with the given name. */
  public void addTab(String tab) { getTab(tab); }


  // -- DialogPane API methods --

  /** Resets the dialog pane's components to their default states. */
  public void resetComponents() { tabs.setSelectedIndex(0); }


  // -- Helper methods --

  /** Gets the tab with the given name, initializing it if necessary. */
  protected JPanel getTab(String tab) {
    int ndx = tabs.indexOfTab(tab);
    if (ndx < 0) {
      JPanel p = new JPanel();
      p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
      tabs.addTab(tab, p);
      ndx = tabs.getTabCount() - 1;
    }
    return (JPanel) tabs.getComponentAt(ndx);
  }
}
