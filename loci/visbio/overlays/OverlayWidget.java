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

import com.jgoodies.forms.builder.PanelBuilder;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import java.awt.BorderLayout;
import java.awt.Color;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Vector;

import javax.swing.*;

import javax.swing.border.EtchedBorder;

import loci.visbio.util.FormsUtil;
import loci.visbio.util.SwingUtil;

/** OverlayWidget is a set of GUI controls for an overlay transform. */
public class OverlayWidget extends JPanel implements ActionListener {

  // -- Constants --

  /** List of available fonts. */
  protected static final String[] FONT_NAMES = {
    "cursive", "futuram", "rowmans", "timesr", "wmo",
    "futural", "meteorology", "rowmant", "timesrb"
  };
  // CTR START HERE
  // Get the new widgets below actually working; concentrate on fonts.
  // More info on Hershey fonts online at: http://batbox.org/font.html
  // Check out "DisplayTest 69" for code example of using Hershey fonts.
  //
  // Also, forgot to add Remove/Delete button. Do that.


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

  /** Text fields for (X, Y) coordinate pairs. */
  protected JTextField x1, y1, x2, y2;

  /** Text field for text labels. */
  protected JTextField text;

  /** Combo box for selecting font name. */
  protected JComboBox fontName;

  /** Spinner for selecting font size. */
  protected JSpinner fontSize;

  /** Button for choosing overlay color. */
  protected JButton color;

  /** Check box indicating whether overlay should be filled or hollow. */
  protected JCheckBox filled;

  /** Combo box for selecting an overlay group. */
  protected JComboBox groupList;

  /** Button for creating a new overlay group. */
  protected JButton newGroup;

  /** Text field for miscellaneous notes. */
  protected JTextField notes;

  /** Text area for overlay statistics. */
  protected JTextArea stats;


  // -- Constructor --

  /** Creates overlay GUI controls. */
  public OverlayWidget(OverlayTransform ann) {
    super();
    this.ann = ann;
    buttonGroup = new ButtonGroup();

    // list of tools
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

    // tool buttons
    buttons = new Vector(toolList.length);
    for (int i=0; i<toolList.length; i++) addTool(toolList[i]);
    Object[] buttonList = new Object[buttons.size()];
    buttons.copyInto(buttonList);
    JPanel toolsRow = FormsUtil.makeRow(buttonList);

    // text fields for (X, Y) coordinate pairs
    x1 = new JTextField(4);
    y1 = new JTextField(4);
    x2 = new JTextField(4);
    y2 = new JTextField(4);

    // text text field ;-)
    text = new JTextField(8);

    // font name selector
    fontName = new JComboBox(FONT_NAMES);

    // font size selector
    fontSize = new JSpinner();

    // color chooser
    color = new JButton();
    color.setForeground(Color.white);
    color.setBackground(Color.white);
    color.addActionListener(this);

    // filled checkbox
    filled = new JCheckBox("Filled");
    filled.setMnemonic('f');

    // group selector
    groupList = new JComboBox(new Object[] {"None"});

    // new group button
    newGroup = new JButton("New");
    newGroup.addActionListener(this);
    newGroup.setMnemonic('n');
    newGroup.setToolTipText("Creates a new overlay group");

    // notes text field
    notes = new JTextField(8);

    // stats text area
    stats = new JTextArea(3, 8);
    stats.setEditable(false);
    stats.setBorder(new EtchedBorder());

    // lay out components
    setLayout(new BorderLayout());
    FormLayout layout = new FormLayout(
      "pref, 3dlu, pref:grow, 5dlu, pref, 3dlu, pref:grow",
      "pref, 3dlu, pref, 9dlu, " +
      "pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, " +
      "pref, 3dlu, pref, 3dlu, pref, 9dlu, pref, 3dlu, fill:pref:grow");
    PanelBuilder builder = new PanelBuilder(layout);
    CellConstraints cc = new CellConstraints();

    builder.addSeparator("Tools", cc.xyw(1, 1, 7));
    builder.add(toolsRow, cc.xyw(1, 3, 7));

    builder.addSeparator("Controls", cc.xyw(1, 5, 7));
    builder.addLabel("X1", cc.xy(1, 7));
    builder.add(x1, cc.xy(3, 7));
    builder.addLabel("Y1", cc.xy(5, 7));
    builder.add(y1, cc.xy(7, 7));
    builder.addLabel("X2", cc.xy(1, 9));
    builder.add(x2, cc.xy(3, 9));
    builder.addLabel("Y2", cc.xy(5, 9));
    builder.add(y2, cc.xy(7, 9));
    builder.addLabel("Text", cc.xy(1, 11));
    builder.add(text, cc.xyw(3, 11, 5));
    builder.addLabel("Font", cc.xy(1, 13));
    builder.add(fontName, cc.xy(3, 13));
    builder.addLabel("Size", cc.xy(5, 13));
    builder.add(fontSize, cc.xy(7, 13));
    builder.addLabel("Color", cc.xy(1, 15));
    builder.add(color, cc.xy(3, 15, "fill, fill"));
    builder.add(filled, cc.xyw(5, 15, 3));
    builder.addLabel("Group", cc.xy(1, 17));
    builder.add(groupList, cc.xy(3, 17));
    builder.add(newGroup, cc.xyw(5, 17, 3, "left, center"));
    builder.addLabel("Notes", cc.xy(1, 19));
    builder.add(notes, cc.xyw(3, 19, 5));

    builder.addSeparator("Statistics", cc.xyw(1, 21, 7));
    builder.add(stats, cc.xyw(1, 23, 7));

    //layout.setColumnGroups(new int[][] {{3, 7}});
    add(builder.getPanel());
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
    color.setForeground(c);
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
    stats.setText(text);
    // CTR TODO fire overlay parameter change event
  }

  /** Gets description for current overlay. */
  public String getDescription() { return stats.getText(); }

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
        "Group name:", "Create overlay group",
        JOptionPane.INFORMATION_MESSAGE, null, null, nextGroup);
      if (group != null) setActiveGroup(group);
    }
  }

}
