//
// TransformPanel.java
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

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.builder.ButtonStackBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.DefaultMutableTreeNode;
import loci.visbio.data.*;
import loci.visbio.util.*;

/** Provides GUI controls for a display transform handler. */
public class TransformPanel extends JPanel
  implements ActionListener, ChangeListener, ListSelectionListener
{

  // -- Fields --

  /** Transform handler upon which GUI controls operate. */
  protected TransformHandler handler;

  /** List of linked data transforms. */
  protected JList transformList;

  /** List model for linked data transform list. */
  protected DefaultListModel transformModel;

  /** Checkbox for toggling data transform visibility. */
  protected JCheckBox visible;

  /** Button for removing selected data transform. */
  protected JButton removeTransform;

  /** Button for editing colors for the selected data transform. */
  protected JButton dataColors;

  /** Button for showing controls for the selected data transform. */
  protected JButton dataControls;

  /** Button for moving data objects upward in the list. */
  protected JButton moveUp;

  /** Button for moving data objects downward in the list. */
  protected JButton moveDown;

  /** Spinner for adjusting burn-in delay. */
  protected JSpinner delayTime;

  /** List of axes for left/right arrow mapping. */
  protected JComboBox leftRightBox;

  /** List of axes for up/down arrow mapping. */
  protected JComboBox upDownBox;

  /** Button for toggling status of animation. */
  protected JButton animate;

  /** Spinner for adjusting animation rate. */
  protected JSpinner fps;

  /** List of axes for animation. */
  protected JComboBox animBox;


  // -- Constructor --

  /** Creates a panel containing view handler GUI controls. */
  public TransformPanel(TransformHandler h) {
    super();
    handler = h;

    // lay out components
    setLayout(new BorderLayout());
    add(FormsUtil.makeColumn(doDataPanel(), "Position", doAnimationPanel()),
      BorderLayout.CENTER);
  }


  // -- TransformPanel API methods --

  /** Adds the given data transform to the list. */
  public void addTransform(DataTransform trans) {
    transformModel.addElement(trans);
  }

  /** Removes the given data transform from the list. */
  public void removeTransform(DataTransform trans) {
    transformModel.removeElement(trans);
  }

  /** Removes all data transforms from the list. */
  public void removeAllTransforms() { transformModel.removeAllElements(); }

  /** Moves the given transform up in the list. */
  public void moveTransformUp(DataTransform trans) {
    int index = transformModel.indexOf(trans);
    if (index >= 0) {
      transformModel.removeElementAt(index);
      transformModel.insertElementAt(trans, index - 1);
    }
  }

  /** Moves the given transform down in the list. */
  public void moveTransformDown(DataTransform trans) {
    int index = transformModel.indexOf(trans);
    if (index >= 0) {
      transformModel.removeElementAt(index);
      transformModel.insertElementAt(trans, index + 1);
    }
  }

  /** Gets whether the given transform is currently linked to the display. */
  public boolean hasTransform(DataTransform trans) {
    return transformModel.contains(trans);
  }

  /** Adds a dimensional axis. */
  public void addAxis(String axis) {
    int count = animBox.getItemCount();
    String s = "<" + count + "> " + axis;
    leftRightBox.addItem(s);
    upDownBox.addItem(s);
    animBox.addItem(s);
    if (axis.equals("Time")) {
      // time axis is a good default for left/right and animation
      leftRightBox.setSelectedItem(s);
      animBox.setSelectedItem(s);
    }
    else if (axis.equals("Slice")) {
      // slice axis is a good default for up/down
      upDownBox.setSelectedItem(s);
    }
  }

  /** Removes all dimensional axes. */
  public void removeAllAxes() {
    while (animBox.getItemCount() > 1) {
      leftRightBox.removeItemAt(1);
      upDownBox.removeItemAt(1);
      animBox.removeItemAt(1);
    }
  }

  /** Gets number of registered dimensional axes. */
  public int getAxisCount() { return animBox.getItemCount() - 1; }

  /** Gets axis mapped to left/right arrow keys. */
  public int getLeftRightAxis() { return leftRightBox.getSelectedIndex() - 1; }

  /** Gets axis mapped to up/down arrow keys. */
  public int getUpDownAxis() { return upDownBox.getSelectedIndex() - 1; }

  /** Updates controls to reflect current handler status. */
  public void updateControls() {
    DataTransform data = (DataTransform) transformList.getSelectedValue();
    int index = transformList.getSelectedIndex();
    boolean b = data != null;
    removeTransform.setEnabled(b);
    dataColors.setEnabled(b && data instanceof ImageTransform);
    dataControls.setEnabled(b && data.getControls() != null);
    moveUp.setEnabled(b && index > 0);
    moveDown.setEnabled(b && index < transformModel.getSize() - 1);
    visible.setEnabled(b);
    if (b) {
      TransformLink link = handler.getLink(data);

      // update "visible" checkbox
      visible.setSelected(link.isVisible());
    }
  }


  // -- ActionListener API methods --

  /** Handles button presses and combo box selections. */
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if (cmd.equals("delayBurn")) {
      boolean delay = ((JCheckBox) e.getSource()).isSelected();
      delayTime.setEnabled(delay);
      handler.setBurnDelay(delay ?  1000 *
        ((Number) delayTime.getValue()).intValue() : 0);
    }
    else if (cmd.equals("visible")) {
      DataTransform data = (DataTransform) transformList.getSelectedValue();
      if (data != null) {
        JCheckBox vis = (JCheckBox) e.getSource();
        handler.getLink(data).setVisible(vis.isSelected());
      }
      updateControls();
    }
    else if (cmd.equals("addTransform")) {
      // get root node of data tree
      DataManager dm = (DataManager)
        handler.getWindow().getVisBio().getManager(DataManager.class);
      DefaultMutableTreeNode root = dm.getDataRoot();
      int children = root.getChildCount();

      // build popup menu from data tree
      JPopupMenu menu = new JPopupMenu();
      for (int i=0; i<children; i++) {
        if (i > 0) menu.addSeparator();
        JMenuItem[] items = buildMenuItems(
          (DefaultMutableTreeNode) root.getChildAt(i));
        for (int j=0; j<items.length; j++) menu.add(items[j]);
      }

      // display popup menu
      JButton source = (JButton) e.getSource();
      menu.show(source, source.getWidth(), 0);
    }
    else if (cmd.equals("removeTransform")) {
      DataTransform data = (DataTransform) transformList.getSelectedValue();
      if (data != null) handler.getWindow().removeTransform(data);
    }
    else if (cmd.equals("dataControls")) {
      DataManager dm = (DataManager)
        handler.getWindow().getVisBio().getManager(DataManager.class);
      DataTransform data = (DataTransform) transformList.getSelectedValue();
      if (data != null) dm.showControls(data);
    }
    else if (cmd.equals("dataColors")) {
      DataTransform data = (DataTransform) transformList.getSelectedValue();
      if (data != null) {
        TransformLink link = handler.getLink(data);
        link.getColorHandler().showColorDialog();
      }
    }
    else if (cmd.equals("animate")) {
      boolean on = animate.getText().equals("Animate");
      animate.setText(on ? "Stop" : "Animate");
      handler.setAnimating(on);
    }
    else if (cmd.equals("animBox")) {
      int ndx = animBox.getSelectedIndex();
      animate.setEnabled(ndx > 0);
      handler.setAnimationAxis(ndx - 1);
    }
    else if (cmd.equals("moveUp")) {
      DataTransform data = (DataTransform) transformList.getSelectedValue();
      if (data != null) handler.moveTransformUp(data);
    }
    else if (cmd.equals("moveDown")) {
      DataTransform data = (DataTransform) transformList.getSelectedValue();
      if (data != null) handler.moveTransformDown(data);
    }
  }


  // -- ChangeListener API methods --

  /** Handles spinner changes. */
  public void stateChanged(ChangeEvent e) {
    Object src = e.getSource();
    if (src == fps) {
      handler.setAnimationRate(((Integer) fps.getValue()).intValue());
    }
    else if (src == delayTime) {
      handler.setBurnDelay(1000 * ((Number) delayTime.getValue()).intValue());
    }
  }


  // -- ListSelectionListener API methods --

  /** Handles list selection changes. */
  public void valueChanged(ListSelectionEvent e) {
    DataTransform data = (DataTransform) transformList.getSelectedValue();
    updateControls();
  }


  // -- Helper methods --

  /** Creates a panel with data transform-related components. */
  protected JPanel doDataPanel() {
    // linked data transforms
    transformModel = new DefaultListModel();
    transformList = new JList(transformModel);
    transformList.setFixedCellWidth(120);
    transformList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    transformList.addListSelectionListener(this);
    transformList.setToolTipText("Data linked to this display");
    JScrollPane listPane = new JScrollPane(transformList);
    SwingUtil.configureScrollPane(listPane);

    // button for adding a data transform
    JButton addTransform = new JButton("Add >");
    addTransform.setActionCommand("addTransform");
    addTransform.addActionListener(this);
    if (!LAFUtil.isMacLookAndFeel()) addTransform.setMnemonic('a');
    addTransform.setToolTipText("Links data to this display");

    // button for removing a data transform
    removeTransform = new JButton("Remove");
    removeTransform.setActionCommand("removeTransform");
    removeTransform.addActionListener(this);
    if (!LAFUtil.isMacLookAndFeel()) removeTransform.setMnemonic('m');
    removeTransform.setToolTipText("Removes data from this display");
    removeTransform.setEnabled(false);

    // button for displaying a data transform's controls
    dataControls = new JButton("Edit");
    dataControls.setActionCommand("dataControls");
    dataControls.addActionListener(this);
    if (!LAFUtil.isMacLookAndFeel()) dataControls.setMnemonic('e');
    dataControls.setToolTipText("Shows data's controls");
    dataControls.setEnabled(false);

    // button for editing a data transform's colors
    dataColors = new JButton("Colors");
    dataColors.setActionCommand("dataColors");
    dataColors.addActionListener(this);
    if (!LAFUtil.isMacLookAndFeel()) dataColors.setMnemonic('c');
    dataColors.setToolTipText("Edits data's color scheme");
    dataColors.setEnabled(false);

    // button for shifting a data transform up in the list
    moveUp = new JButton("Up");
    moveUp.setActionCommand("moveUp");
    moveUp.addActionListener(this);
    if (!LAFUtil.isMacLookAndFeel()) moveUp.setMnemonic('u');
    moveUp.setToolTipText("Moves selected data up in the list");
    moveUp.setEnabled(false);

    // button for shifting a data transform down in the list
    moveDown = new JButton("Down");
    moveDown.setActionCommand("moveDown");
    moveDown.addActionListener(this);
    if (!LAFUtil.isMacLookAndFeel()) moveDown.setMnemonic('d');
    moveDown.setToolTipText("Moves selected data down in the list");
    moveDown.setEnabled(false);

    // transform visibility checkbox
    visible = new JCheckBox("Visible");
    if (!LAFUtil.isMacLookAndFeel()) visible.setMnemonic('v');
    visible.setToolTipText("Toggles visibility of the selected transform");
    visible.setActionCommand("visible");
    visible.addActionListener(this);
    visible.setEnabled(false);

    // burn-in checkbox
    JCheckBox delayBurn = new JCheckBox("Delay burn-in", true);
    delayBurn.setActionCommand("delayBurn");
    delayBurn.addActionListener(this);
    if (!LAFUtil.isMacLookAndFeel()) delayBurn.setMnemonic('n');
    delayBurn.setToolTipText(
      "Toggles whether full-resolution burn-in is delayed");

    // burn-in delay
    delayTime = new JSpinner(new SpinnerNumberModel(
      handler.getBurnDelay() / 1000, 1, 99, 1));
    delayTime.addChangeListener(this);
    delayTime.setToolTipText(
      "Adjusts the time before full-resolution burn-in");

    // lay out buttons
    ButtonStackBuilder bsb = new ButtonStackBuilder();
    bsb.addGridded(addTransform);
    bsb.addRelatedGap();
    bsb.addGridded(removeTransform);
    bsb.addRelatedGap();
    bsb.addGridded(dataControls);
    bsb.addRelatedGap();
    bsb.addGridded(dataColors);
    bsb.addRelatedGap();
    bsb.addGridded(moveUp);
    bsb.addRelatedGap();
    bsb.addGridded(moveDown);
    JPanel buttons = bsb.getPanel();

    // lay out components
    return FormsUtil.makeColumn(
      FormsUtil.makeRow(new Object[] {listPane, buttons,
        doDataProperties()}, "fill:pref:grow", false),
      FormsUtil.makeRow(delayBurn, delayTime, "seconds")
    );
  }

  /** Creates a panel with animation-related components. */
  protected JPanel doAnimationPanel() {
    // left/right combo box
    leftRightBox = new JComboBox(new String[] {"None"});
    SwingUtil.configureComboBox(leftRightBox);
    leftRightBox.setToolTipText("The axis " +
      "traversed by the left and right arrow keys");

    // up/down combo box
    upDownBox = new JComboBox(new String[] {"None"});
    SwingUtil.configureComboBox(upDownBox);
    upDownBox.setToolTipText("The axis " +
      "traversed by the up and down arrow keys");

    // animate button
    animate = new JButton("Animate");
    if (!LAFUtil.isMacLookAndFeel()) animate.setMnemonic('t');
    animate.setToolTipText("Toggles animation across the linked axis");
    animate.setActionCommand("animate");
    animate.addActionListener(this);
    animate.setEnabled(false);
    // prevent size changes when animation button text changes
    animate.setPreferredSize(animate.getPreferredSize());

    // FPS adjuster
    fps = new JSpinner(new SpinnerNumberModel(
      handler.getAnimationRate(), 1, 60, 1));
    fps.setToolTipText("Adjusts the animation speed (frames per second)");
    fps.addChangeListener(this);

    // animation axis combo box
    animBox = new JComboBox(new String[] {"None"});
    SwingUtil.configureComboBox(animBox);
    animBox.setToolTipText("The axis over which to animate");
    animBox.setActionCommand("animBox");
    animBox.addActionListener(this);

    // lay out components
    PanelBuilder builder = new PanelBuilder(new FormLayout(
      "pref:grow", "pref, 3dlu, pref, 9dlu"));
    CellConstraints cc = new CellConstraints();
    builder.add(FormsUtil.makeRow(new Object[] {"Arrow keys:",
      "Le&ft/right", leftRightBox, "Up/do&wn", upDownBox}), cc.xy(1, 1));
    builder.add(FormsUtil.makeRow(new Object[]
      {animate, "&FPS", fps, animBox}), cc.xy(1, 3));
    return builder.getPanel();
  }

  /** Creates a panel for controls pertaining to the selected data object. */
  protected JPanel doDataProperties() {
    JPanel p = new JPanel();
    p.setLayout(new BorderLayout());
    p.add(visible);
    return p;
  }

  /** Builds a JMenu from the children of the given tree node. */
  protected JMenuItem[] buildMenuItems(DefaultMutableTreeNode node) {
    final DataTransform data = (DataTransform) node.getUserObject();
    String name = data.toString();
    int children = node.getChildCount();

    JMenuItem[] items = new JMenuItem[children == 0 ? 1 : 2];
    items[0] = new JMenuItem(name);
    items[0].setEnabled(!hasTransform(data));
    items[0].addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        handler.getWindow().addTransform(data);
      }
    });
    if (children > 0) items[1] = new JMenu(name);
    for (int i=0; i<children; i++) {
      if (i > 0) ((JMenu) items[1]).addSeparator();
      JMenuItem[] jmi = buildMenuItems(
        (DefaultMutableTreeNode) node.getChildAt(i));
      for (int j=0; j<jmi.length; j++) items[1].add(jmi[j]);
    }
    return items;
  }

}
