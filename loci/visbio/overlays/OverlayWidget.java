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

import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Vector;

import javax.swing.*;

import javax.swing.border.EtchedBorder;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import loci.visbio.data.*;

import loci.visbio.util.*;

/** OverlayWidget is a set of GUI controls for an overlay transform. */
public class OverlayWidget extends JPanel
  implements ActionListener, ListSelectionListener, TransformListener
{

  // -- Fields --

  /** Associated overlay object. */
  protected OverlayTransform overlay;

  /** Button group ensuring only one tool can be selected at a time. */
  protected ButtonGroup buttonGroup;

  /** List of overlay tools. */
  protected Vector tools;

  /** List of buttons for each overlay tool. */
  protected Vector buttons;


  // -- GUI components - global --

  /** Text field indicating current font. */
  protected JTextField currentFont;

  /** Button for bringing up font chooser. */
  protected JButton chooseFont;

  /** List of overlays. */
  protected JList overlayList;

  /** List model for overlay list. */
  protected DefaultListModel overlayListModel;

  /** Button for removing selected overlays. */
  protected JButton remove;


  // -- GUI components - overlay-specific --

  /** Text fields for (X, Y) coordinate pairs. */
  protected JTextField x1, y1, x2, y2;

  /** Text field for text labels. */
  protected JTextField text;

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
  public OverlayWidget(OverlayTransform overlay) {
    super();
    this.overlay = overlay;
    buttonGroup = new ButtonGroup();

    // list of tools
    OverlayTool[] toolList = {
      new PointerTool(overlay),
      new LineTool(overlay),
      new MarkerTool(overlay),
      new TextTool(overlay),
      new OvalTool(overlay),
      new BoxTool(overlay),
      new ArrowTool(overlay)
    };
    tools = new Vector(toolList.length);

    // tool buttons
    buttons = new Vector(toolList.length);
    for (int i=0; i<toolList.length; i++) addTool(toolList[i]);
    Object[] buttonList = new Object[buttons.size()];
    buttons.copyInto(buttonList);
    JPanel toolsRow = FormsUtil.makeRow(buttonList);

    // current font text field
    currentFont = new JTextField("Default 11");
    currentFont.setEditable(false);

    // font chooser button
    chooseFont = new JButton("Change...");
    chooseFont.addActionListener(this);
    if (!LAFUtil.isMacLookAndFeel()) chooseFont.setMnemonic('c');
    chooseFont.setToolTipText("Configures font used for text overlays");
    JPanel fontRow = FormsUtil.makeRow(new Object[] {"Font",
      currentFont, chooseFont}, new boolean[] {false, true, false});

    // overlay list
    overlayListModel = new DefaultListModel();
    overlayList = new JList(overlayListModel);
    overlayList.addListSelectionListener(this);
    JScrollPane overlayScroll = new JScrollPane(overlayList);
    SwingUtil.configureScrollPane(overlayScroll);
    overlayScroll.setPreferredSize(new Dimension(100, 0));

    // overlay removal button
    remove = new JButton("Remove");
    remove.addActionListener(this);
    if (!LAFUtil.isMacLookAndFeel()) remove.setMnemonic('r');

    // text fields for (X, Y) coordinate pairs
    x1 = new JTextField(4);
    y1 = new JTextField(4);
    x2 = new JTextField(4);
    y2 = new JTextField(4);

    // text text field ;-)
    text = new JTextField(6);

    // color chooser
    color = new JButton();
    color.addActionListener(this);
    color.setBackground(Color.white);

    // filled checkbox
    filled = new JCheckBox("Filled");
    if (!LAFUtil.isMacLookAndFeel()) filled.setMnemonic('f');

    // group selector
    groupList = new JComboBox(new Object[] {"None"});

    // new group button
    newGroup = new JButton("New...");
    newGroup.addActionListener(this);
    if (!LAFUtil.isMacLookAndFeel()) newGroup.setMnemonic('n');
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
      "pref, 5dlu, pref, 3dlu, pref:grow, 5dlu, pref, 3dlu, pref:grow",
      "pref, 3dlu, pref, 9dlu, pref, 3dlu, pref, 9dlu, pref, 3dlu, pref, " +
      "3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 9dlu, pref, 3dlu, " +
      "fill:pref:grow");
    PanelBuilder builder = new PanelBuilder(layout);
    CellConstraints cc = new CellConstraints();

    builder.addSeparator("Tools", cc.xyw(1, 1, 9));
    builder.add(toolsRow, cc.xyw(1, 3, 9));

    builder.addSeparator("Overlays", cc.xyw(1, 5, 9));
    builder.add(fontRow, cc.xyw(1, 7, 9));

    builder.add(overlayScroll, cc.xywh(1, 9, 1, 9));
    builder.add(remove, cc.xy(1, 19, "center, center"));

    builder.addLabel("X1", cc.xy(3, 9));
    builder.add(x1, cc.xy(5, 9));
    builder.addLabel("Y1", cc.xy(7, 9));
    builder.add(y1, cc.xy(9, 9));
    builder.addLabel("X2", cc.xy(3, 11));
    builder.add(x2, cc.xy(5, 11));
    builder.addLabel("Y2", cc.xy(7, 11));
    builder.add(y2, cc.xy(9, 11));
    builder.addLabel("Text", cc.xy(3, 13));
    builder.add(text, cc.xyw(5, 13, 5));
    builder.addLabel("Color", cc.xy(3, 15));
    builder.add(color, cc.xy(5, 15, "fill, fill"));
    builder.add(filled, cc.xyw(7, 15, 3));
    builder.addLabel("Group", cc.xy(3, 17));
    builder.add(groupList, cc.xy(5, 17));
    builder.add(newGroup, cc.xyw(7, 17, 3, "left, center"));
    builder.addLabel("Notes", cc.xy(3, 19));
    builder.add(notes, cc.xyw(5, 19, 5));

    builder.addSeparator("Statistics", cc.xyw(1, 21, 9));
    builder.add(stats, cc.xyw(1, 23, 9));

    layout.setColumnGroups(new int[][] {{5, 9}});
    add(builder.getPanel());

    overlay.addTransformListener(this);
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

  public float getX1() { return Float.NaN; }
  public float getY1() { return Float.NaN; }
  public float getX2() { return Float.NaN; }
  public float getY2() { return Float.NaN; }
  public String getText() { return text.getText(); }

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

  /** Sets notes for current overlay. */
  public void setNotes(String text) {
    notes.setText(text);
    // CTR TODO fire overlay parameter change event
  }

  /** Gets notes for current overlay. */
  public String getNotes() { return notes.getText(); }

  /** Sets statistics for current overlay. */
  public void setStatistics(String text) {
    stats.setText(text);
    // CTR TODO fire overlay parameter change event
  }

  /** Gets statistics for current overlay. */
  public String getStatistics() { return stats.getText(); }

  /** Updates items on overlay list based on current transform state. */
  public void refreshListObjects() {
    OverlayObject[] obj = overlay.getObjects();
    overlayListModel.clear();
    overlayListModel.ensureCapacity(obj.length);
    for (int i=0; i<obj.length; i++) overlayListModel.addElement(obj[i]);
  }

  /** Updates overlay list's selection based on current transform state. */
  public void refreshListSelection() {
    OverlayObject[] obj = overlay.getObjects();
    int sel = 0;
    for (int i=0; i<obj.length; i++) {
      if (obj[i].isSelected()) sel++;
    }
    int[] indices = new int[sel];
    int c = 0;
    for (int i=0; i<obj.length && c<sel; i++) {
      if (obj[i].isSelected()) indices[c++] = i;
    }
    overlayList.setSelectedIndices(indices);
  }


  // -- ActionListener API methods --

  /** Handles button presses. */
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    if (src == chooseFont) {
      FontChooserPane fcp = new FontChooserPane(overlay.getFont());
      int rval = fcp.showDialog(this);
      if (rval == DialogPane.APPROVE_OPTION) {
        Font font = fcp.getSelectedFont();
        if (font != null) overlay.setFont(font);
      }
    }
    else if (src == remove) overlay.removeSelectedObjects();
    else if (src == color) {
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


  // -- ListSelectionListener API methods --

  /** Handles list selection changes. */
  public void valueChanged(ListSelectionEvent e) {
    OverlayObject[] obj = overlay.getObjects();

    // deselect all previously selected overlays
    for (int i=0; i<obj.length; i++) obj[i].setSelected(false);

    // select highlighted overlays
    Object[] sel = overlayList.getSelectedValues();
    for (int i=0; i<sel.length; i++) {
      ((OverlayObject) sel[i]).setSelected(true);
    }

    overlay.notifyListeners(new TransformEvent(overlay));
  }


  // -- TransformListener API methods --

  /** Handles font changes. */
  public void transformChanged(TransformEvent e) {
    int id = e.getId();
    if (id == TransformEvent.FONT_CHANGED) {
      Font font = overlay.getFont();
      String s = font.getFamily() + " " + font.getSize();
      if (font.isBold()) s += " Bold";
      if (font.isItalic()) s += " Italic";
      currentFont.setText(s);
    }
  }

}
