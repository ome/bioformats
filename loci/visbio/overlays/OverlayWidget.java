//
// OverlayWidget.java
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

package loci.visbio.overlays;

import java.awt.BorderLayout;
import java.awt.Color;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Vector;

import javax.swing.*;

import loci.visbio.util.FormsUtil;
import loci.visbio.util.SwingUtil;

/** OverlayWidget is a set of GUI controls for an overlay transform. */
public class OverlayWidget extends JPanel implements ActionListener {

  // -- Fields --

  /** Associated overlay object. */
  protected OverlayTransform ann;

  /** Button group ensuring only one tool can be selected at a time. */
  protected ButtonGroup buttonGroup;

  /** List of overlay tools. */
  protected Vector tools;

  /** List of buttons for each overlay tool. */
  protected Vector buttons;


  // -- GUI components --

  /** Button for choosing overlay color. */
  protected JButton color;

  /** Check box indicating whether overlay should be filled or hollow. */
  protected JCheckBox filled;

  /** Combo box for selecting an overlay group. */
  protected JComboBox groupList;

  /** Button for creating a new overlay group. */
  protected JButton newGroup;

  /** Text area for overlay description. */
  protected JTextArea descriptionBox;


  // -- Constructor --

  /** Creates overlay GUI controls. */
  public OverlayWidget(OverlayTransform ann) {
    super();
    this.ann = ann;
    buttonGroup = new ButtonGroup();

    // create list of tools
    OverlayTool[] toolList = {
      new PointerTool(ann),
      new LineTool(ann),
      new MarkerTool(ann),
      new TextTool(ann),
      new OvalTool(ann),
      new BoxTool(ann),
      new ArrowTool(ann)
    };
    tools = new Vector(toolList.length);

    // create tool buttons
    buttons = new Vector(toolList.length);
    for (int i=0; i<toolList.length; i++) addTool(toolList[i]);
    Object[] buttonList = new Object[buttons.size()];
    buttons.copyInto(buttonList);
    JPanel toolsRow = FormsUtil.makeRow(buttonList);

    // create color chooser
    color = new JButton(" ");
    color.setBackground(Color.white);
    color.addActionListener(this);
    filled = new JCheckBox("Filled");
    filled.setMnemonic('f');
    filled.setEnabled(false);
    JPanel colorRow = FormsUtil.makeRow("&Color", color, filled);

    // create group selector
    groupList = new JComboBox(new Object[] {"None"});
    newGroup = new JButton("New");
    newGroup.addActionListener(this);
    newGroup.setMnemonic('n');
    newGroup.setToolTipText("Creates a new measurement group");
 
    JPanel groupRow = FormsUtil.makeRow("&Group", groupList, newGroup);

    // create description text area
    descriptionBox = new JTextArea();
    descriptionBox.setRows(4);
    descriptionBox.setLineWrap(true);
    descriptionBox.setWrapStyleWord(true);
    JScrollPane descriptionScroll = new JScrollPane(descriptionBox);
    SwingUtil.configureScrollPane(descriptionScroll);

    // lay out components
    setLayout(new BorderLayout());
    add(FormsUtil.makeColumn(new Object[] {"Tools", toolsRow,
      "Controls", colorRow, groupRow, "Description", descriptionScroll}));
  }


  // -- OverlayWidget API methods --

  /** Adds the given overlay tool to the overlay widget. */
  public void addTool(OverlayTool tool) {
    JToggleButton b = SwingUtil.makeToggleButton(
      this, tool.getIcon(), tool.getName(), 6, 6);
    b.setToolTipText(tool.getTip());
    buttonGroup.add(b);
    buttons.add(b);
    tools.add(tool);
  }

  /** Gets the currently selected overlay tool. */
  public OverlayTool getActiveTool() {
    int size = buttons.size();
    for (int i=0; i<size; i++) {
      JToggleButton b = (JToggleButton) buttons.elementAt(i);
      if (b.isSelected()) return (OverlayTool) tools.elementAt(i);
    }
    return null;
  }

  /** Sets currently active overlay color. */
  public void setActiveColor(Color c) {
    color.setBackground(c);
    // CTR TODO fire overlay parameter change event
  }

  /** Gets currently active overlay color. */
  public Color getActiveColor() { return color.getBackground(); }

  /** Sets whether current overlay is filled. */
  public void setFilled(boolean fill) {
    filled.setSelected(fill);
    // CTR TODO fire overlay parameter change event
  }

  /** Gets whether current overlay is filled. */
  public boolean isFilled() { return filled.isSelected(); }

  /** Sets currently active overlay group. */
  public void setActiveGroup(String group) {
    DefaultComboBoxModel model = (DefaultComboBoxModel) groupList.getModel();
    if (model.getIndexOf(group) < 0) groupList.addItem(group);
    groupList.setSelectedItem(group);
    // CTR TODO fire overlay parameter change event
  }

  /** Gets currently active overlay group. */
  public String getActiveGroup() {
    return (String) groupList.getSelectedItem();
  }

  /** Sets description for current overlay. */
  public void setDescription(String text) {
    descriptionBox.setText(text);
    // CTR TODO fire overlay parameter change event
  }

  /** Gets description for current overlay. */
  public String getDescription() { return descriptionBox.getText(); }

  // -- ActionListener API methods --

  /** Handles button presses. */
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    if (src == color) {
      Color c = getActiveColor();
      c = JColorChooser.showDialog(this, "Select a color", c);
      if (c != null) setActiveColor(c);
    }
    else if (src == newGroup) {
      String nextGroup = "group" + groupList.getItemCount();
      String group = (String) JOptionPane.showInputDialog(this,
        "Group name:", "Create measurement group",
        JOptionPane.INFORMATION_MESSAGE, null, null, nextGroup);
      if (group != null) setActiveGroup(group);
    }
  }

}
