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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import loci.visbio.state.BooleanOption;
import loci.visbio.state.OptionManager;

import loci.visbio.util.SwingUtil;

/** PanelManager is the manager encapsulating VisBio's control panel logic. */
public class PanelManager extends LogicManager implements ActionListener {

  // -- Constants --

  /** String for floating control windows option. */
  public static final String FLOATING = "Floating control windows";


  // -- GUI components --

  /** Control panels. */
  private Vector panels;

  /** Floating control panel windows. */
  private Vector floatWindows;

  /** Tabbed pane containing control panels. */
  private JTabbedPane tabs;

  /** Shortcut buttons for bringing up floating control panel windows. */
  private Vector buttons;

  /** Pane containing shortcut buttons. */
  private JPanel buttonPane;


  // -- Other fields --

  /** Whether control panels are located in separate, floating windows. */
  private boolean floating;


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
    floatWindows.add(w);

    // create shortcut button
    JButton b = new JButton(name);
    b.addActionListener(this);
    String tip = "Shows " + name + " panel";
    b.setToolTipText(tip);
    buttons.add(b);
    buttonPane.add(b);

    if (!floating) tabs.addTab(name, null, cpl, cpl.getTip());

    bio.addMenuItem("Window", name,
      "loci.visbio.PanelManager.windowShow(" + name + ")", name.charAt(0));

    bio.generateEvent(this, "add " + name + " panel", false);
  }

  /**
   * Enlarges a control panel to its preferred width
   * and/or height if it is too small.
   */
  public void repack(ControlPanel cpl) {
    int ndx = panels.indexOf(cpl);
    if (ndx < 0) return;
    SwingUtil.repack((JFrame) floatWindows.elementAt(ndx));
    if (!floating) SwingUtil.repack(bio);
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
    else if (eventType == VisBioEvent.STATE_CHANGED) {
      if (evt.getMessage().equals("add option")) return;
      Object src = evt.getSource();
      if (src instanceof OptionManager) {
        OptionManager om = (OptionManager) src;
        BooleanOption option = (BooleanOption) om.getOption(FLOATING);
        setFloating(option.getValue());
      }
    }
  }

  /** Gets the number of tasks required to initialize this logic manager. */
  public int getTasks() { return 2; }


  // -- ActionListener API methods --

  /** Handles shortcut button presses. */
  public void actionPerformed(ActionEvent e) {
    windowShow(((JButton) e.getSource()).getText());
  }


  // -- Helper methods --

  /** Adds base control panel GUI components to VisBio. */
  private void doGUI() {
    bio.setSplashStatus("Initializing control panel logic");
    panels = new Vector();
    floatWindows = new Vector();

    // control panel containers
    tabs = new JTabbedPane();
    bio.getContentPane().add(tabs);

    // shortcut buttons for floating control panel windows
    buttons = new Vector();
    buttonPane = new JPanel();

    // options menu
    bio.setSplashStatus(null);
    OptionManager om = (OptionManager) bio.getManager(OptionManager.class);
    om.addBooleanOption("General", FLOATING, 'f',
      "Toggles whether each control panel has its own window", false);
  }

  /** Sets whether control panels are separate, floating windows. */
  private void setFloating(boolean floating) {
    if (this.floating == floating) return;
    this.floating = floating;

    JPanel pane = (JPanel) bio.getContentPane();
    if (floating) {
      pane.remove(tabs);
      tabs.removeAll();
      for (int i=0; i<panels.size(); i++) {
        ControlPanel cpl = (ControlPanel) panels.elementAt(i);
        JFrame w = (JFrame) floatWindows.elementAt(i);
        w.setContentPane(cpl);
      }
      pane.add(buttonPane);
    }
    else {
      pane.remove(buttonPane);
      for (int i=0; i<panels.size(); i++) {
        ControlPanel cpl = (ControlPanel) panels.elementAt(i);
        JFrame w = (JFrame) floatWindows.elementAt(i);
        w.hide();
        tabs.addTab(cpl.getName(), null, cpl, cpl.getTip());
      }
      pane.add(tabs);
    }
    bio.pack();
  }


  // -- Menu commands --

  /** Displays or switches to the given control panel. */
  public void windowShow(String name) {
    ControlPanel cpl = (ControlPanel) getPanel(name);
    int ndx = panels.indexOf(cpl);
    if (floating) {
      WindowManager wm = (WindowManager) bio.getManager(WindowManager.class);
      wm.showWindow((JFrame) floatWindows.elementAt(ndx));
    }
    else tabs.setSelectedIndex(ndx);
  }

}
