//
// DisplayControls.java
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

package loci.visbio.view;

import com.jgoodies.forms.builder.ButtonStackBuilder;
import com.jgoodies.forms.builder.PanelBuilder;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import loci.visbio.ControlPanel;
import loci.visbio.LogicManager;
import loci.visbio.WindowManager;

import loci.visbio.util.SwingUtil;
import loci.visbio.util.VisUtil;

/** DisplayControls is the control panel for managing displays. */
public class DisplayControls extends ControlPanel
  implements ActionListener, ListSelectionListener
{

  // -- GUI components --

  /** List of displays. */
  protected JList displayList;

  /** Display list model */
  protected DefaultListModel listModel;

  /** Button for adding a 2D display to the list. */
  protected JButton add2D;

  /** Button for adding a 3D display to the list. */
  protected JButton add3D;

  /** Button for showing a display onscreen. */
  protected JButton show;

  /** Button for capturing display screenshots and movies. */
  protected JButton capture;

  /** Button for removing a display from the list. */
  protected JButton remove;


  // -- Constructor --

  /** Constructs a tool panel for adjusting data parameters. */
  public DisplayControls(LogicManager logic) {
    super(logic, "Displays", "Controls for managing displays");
    controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

    // list of displays
    listModel = new DefaultListModel();
    displayList = new JList(listModel);
    displayList.setFixedCellWidth(120);
    displayList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    displayList.addListSelectionListener(this);
    JScrollPane listPane = new JScrollPane(displayList);
    SwingUtil.configureScrollPane(listPane);

    // add 2D button
    add2D = new JButton("Add 2D");
    add2D.addActionListener(this);
    add2D.setMnemonic('2');
    add2D.setToolTipText("Creates a new 2D display");

    // add 3D button
    add3D = new JButton("Add 3D");
    add3D.addActionListener(this);
    add3D.setMnemonic('3');
    add3D.setToolTipText("Creates a new 3D display");
    add3D.setEnabled(VisUtil.canDo3D());

    // show button
    show = new JButton("Show");
    show.addActionListener(this);
    show.setMnemonic('s');
    show.setToolTipText("Displays the selected display onscreen");
    show.setEnabled(false);

    // capture button
    capture = new JButton("Capture");
    capture.addActionListener(this);
    capture.setMnemonic('p');
    capture.setToolTipText(
      "Creates screenshots and movies for the selected display");
    capture.setEnabled(false);

    // remove data button
    remove = new JButton("Remove");
    remove.addActionListener(this);
    remove.setMnemonic('r');
    remove.setToolTipText("Deletes the selected display");
    remove.setEnabled(false);

    // lay out buttons
    ButtonStackBuilder bsb = new ButtonStackBuilder();
    bsb.addGridded(add2D);
    bsb.addRelatedGap();
    bsb.addGridded(add3D);
    bsb.addUnrelatedGap();
    bsb.addGridded(show);
    bsb.addRelatedGap();
    bsb.addGridded(capture);
    bsb.addRelatedGap();
    bsb.addGridded(remove);
    JPanel buttons = bsb.getPanel();

    // lay out components
    PanelBuilder builder = new PanelBuilder(new FormLayout(
      "default:grow, 3dlu, pref",
      "fill:pref:grow"));
    builder.setDefaultDialogBorder();
    CellConstraints cc = new CellConstraints();
    builder.add(listPane, cc.xy(1, 1));
    builder.add(buttons, cc.xy(3, 1));
    controls.add(builder.getPanel());
  }


  // -- DisplayControls API methods --

  /** Adds a display to the list of current displays. */
  public void addDisplay(DisplayWindow d) {
    listModel.addElement(d);
    displayList.setSelectedValue(d, true);
  }

  /** Removes a display from the list of current displays. */
  public void removeDisplay(DisplayWindow d) {
    if (d == null) return;
    listModel.removeElement(d);
    d.setVisible(false);
    d.dispose();
  }

  /** Gets the current list of displays. */
  public DisplayWindow[] getDisplays() {
    DisplayWindow[] d = new DisplayWindow[listModel.size()];
    listModel.copyInto(d);
    return d;
  }

  /** Refreshes GUI components based on current selection. */
  public void refresh() {
    DisplayWindow d = (DisplayWindow) displayList.getSelectedValue();
    show.setEnabled(isShowable(d));
    capture.setEnabled(isShowable(d));
    remove.setEnabled(d != null);
  }


  // -- ActionListener API methods --

  /** Handles button presses. */
  public void actionPerformed(ActionEvent e) {
    DisplayManager dm = (DisplayManager) lm;
    Object src = e.getSource();
    if (src == add2D) dm.createDisplay(false);
    else if (src == add3D) dm.createDisplay(true);
    else if (src == show) showDisplay();
    else if (src == capture) {
      DisplayWindow d = (DisplayWindow) displayList.getSelectedValue();
      d.getCaptureHandler().getPanel().showCaptureWindow();
    }
    else if (src == remove) {
      dm.removeDisplay((DisplayWindow) displayList.getSelectedValue());
    }
  }


  // -- ListSelectionListener API methods --

  /** Handles list selection changes. */
  public void valueChanged(ListSelectionEvent e) {
    refresh();
    showDisplay();
  }


  // -- Helper methods --

  /** Shows the selected display onscreen. */
  protected void showDisplay() {
    DisplayWindow d = (DisplayWindow) displayList.getSelectedValue();
    if (isShowable(d)) {
      WindowManager wm = (WindowManager)
        lm.getVisBio().getManager(WindowManager.class);
      wm.showWindow(d);
    }
  }

  /** Gets whether the given display should be allowed to be shown onscreen. */
  protected boolean isShowable(DisplayWindow d) {
    return d != null && d.getTransformHandler().getTransformCount() > 0;
  }

}
