//
// StackPanel.java
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

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import loci.visbio.data.DataTransform;

import loci.visbio.util.FormsUtil;
import loci.visbio.util.LAFUtil;

/** Provides GUI controls for a display stack handler. */
public class StackPanel extends TransformPanel {

  // -- Fields --

  /** Label for stack axis combo box. */
  protected JLabel stackLabel;

  /** List of axes for stacking. */
  protected JComboBox stackBox;

  /** Checkbox for choosing whether current slice is highlighted. */
  protected JCheckBox highlight;

  /** Dialog box for toggling individual slices. */
  protected SliceToggler sliceToggler;

  /** Button for bringing up slice toggler dialog box. */
  protected JButton toggleSlices;

  /** Checkbox for choosing whether current slice is visible. */
  protected JCheckBox sliceVisible;


  // -- Constructor --

  /** Creates a panel containing view handler GUI controls. */
  public StackPanel(StackHandler h) { super(h); }


  // -- TransformPanel API methods --

  /** Updates controls to reflect current handler status. */
  public void updateControls() {
    super.updateControls();

    DataTransform trans = (DataTransform) transformList.getSelectedValue();
    boolean b = trans != null;
    stackLabel.setEnabled(b);
    stackBox.setEnabled(b);
    highlight.setEnabled(b);
    toggleSlices.setEnabled(b);
    sliceVisible.setEnabled(b);
    if (b) {
      StackLink link = (StackLink) handler.getLink(trans);

      // update "stack axis" combo box
      String[] dims = trans.getDimTypes();
      stackBox.removeActionListener(this);
      String[] items = new String[dims.length + 1];
      items[0] = "None";
      for (int i=0; i<dims.length; i++) {
        items[i + 1] = "<" + (i + 1) + "> " + dims[i];
      }
      boolean same = items.length == stackBox.getItemCount();
      for (int i=0; i<items.length && same; i++) {
        same = items[i].equals(stackBox.getItemAt(i));
      }
      if (!same) {
        stackBox.removeAllItems();
        for (int i=0; i<items.length; i++) stackBox.addItem(items[i]);
      }
      int stackAxis = link.getStackAxis();
      stackBox.setSelectedIndex(stackAxis + 1);
      stackBox.addActionListener(this);

      // update "highlight current slice" checkbox
      highlight.setSelected(link.isBoundingBoxVisible());

      // update "current slice visible" checkbox
      int slice = link.getCurrentSlice();
      sliceVisible.setSelected(link.isSliceVisible(slice));
    }
  }


  // -- ActionListener API methods --

  /** Handles button presses and combo box selections. */
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if (cmd.equals("stackBox")) {
      DataTransform trans = (DataTransform) transformList.getSelectedValue();
      int axis = stackBox.getSelectedIndex() - 1;
      StackLink link = (StackLink) handler.getLink(trans);
      link.setStackAxis(axis);
      handler.rebuild();
    }
    else if (cmd.equals("highlight")) {
      DataTransform trans = (DataTransform) transformList.getSelectedValue();
      boolean vis = highlight.isSelected();
      StackLink link = (StackLink) handler.getLink(trans);
      link.setBoundingBoxVisible(vis);
    }
    else if (cmd.equals("toggleSlices")) {
      DisplayWindow window = handler.getWindow();
      DataTransform trans = (DataTransform) transformList.getSelectedValue();
      sliceToggler.setTransform(trans);
      int rval = sliceToggler.showDialog(window);
      if (rval != SliceToggler.APPROVE_OPTION) return;
      updateControls();
    }
    else if (cmd.equals("sliceVisible")) {
      DataTransform trans = (DataTransform) transformList.getSelectedValue();
      StackLink link = (StackLink) handler.getLink(trans);
      int slice = link.getCurrentSlice();
      boolean vis = sliceVisible.isSelected();
      link.setSliceVisible(slice, vis);
      updateControls();
    }
    else {
      if (cmd.equals("transformBox")) updateControls();
      super.actionPerformed(e);
    }
  }


  // -- Helper methods --

  /** Creates a panel for controls pertaining to the selected data object. */
  protected JPanel doDataProperties() {
    // stack axis combo box
    stackBox = new JComboBox(new String[] {"None"});
    stackBox.setToolTipText("The axis over which to stack images in 3D");
    stackBox.setActionCommand("stackBox");
    stackBox.addActionListener(this);
    stackBox.setEnabled(false);

    // stack axis label
    stackLabel = new JLabel("Stack axis");
    if (!LAFUtil.isMacLookAndFeel()) stackLabel.setDisplayedMnemonic('k');
    stackLabel.setLabelFor(stackBox);
    stackLabel.setEnabled(false);

    // highlight current slice checkbox
    highlight = new JCheckBox("Highlight current slice");
    if (!LAFUtil.isMacLookAndFeel()) highlight.setMnemonic('h');
    highlight.setToolTipText("Toggles yellow highlight around current slice");
    highlight.setActionCommand("highlight");
    highlight.addActionListener(this);
    highlight.setEnabled(false);

    // slice toggler dialog box
    sliceToggler = new SliceToggler((StackHandler) handler);

    // toggle slices button
    toggleSlices = new JButton("Toggle slices");
    if (!LAFUtil.isMacLookAndFeel()) toggleSlices.setMnemonic('g');
    toggleSlices.setToolTipText(
      "Provides options to toggle slices in the stack");
    toggleSlices.setActionCommand("toggleSlices");
    toggleSlices.addActionListener(this);
    toggleSlices.setEnabled(false);

    // current slice visible checkbox
    sliceVisible = new JCheckBox("Current slice visible");
    if (!LAFUtil.isMacLookAndFeel()) sliceVisible.setMnemonic('u');
    sliceVisible.setToolTipText("Toggles visibility of the current slice");
    sliceVisible.setActionCommand("sliceVisible");
    sliceVisible.addActionListener(this);
    sliceVisible.setEnabled(false);

    // lay out components
    return FormsUtil.makeColumn(new Object[] {visible,
      FormsUtil.makeRow(stackLabel, stackBox),
      toggleSlices, sliceVisible, highlight});
  }

}
