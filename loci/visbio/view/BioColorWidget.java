//
// BioColorWidget.java
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

import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import loci.visbio.util.ColorUtil;

import visad.RealType;

/**
 * BioColorWidget is a widget for controlling mappings
 * from range scalars to color scalars.
 */
public class BioColorWidget extends JPanel {

  // -- Constants --

  /** List of color component names. */
  protected static final String[][] COLOR_NAMES = {
    {"Red", "Green", "Blue"},
    {"Hue", "Saturation", "Value"}
  };


  // -- GUI components --

  /** Label naming color component. */
  protected JLabel color;

  /** Combo box listing available range components. */
  protected JComboBox scalars;


  // -- Other fields --

  /** Color model: RGB or HSV. */
  protected int model;

  /** Color type: 1=Red/Hue, 2=Green/Saturation, 3=Blue/Value. */
  protected int type;


  // -- Constructor --

  /** Constructs a new color widget. */
  public BioColorWidget(int colorType) {
    model = ColorUtil.RGB_MODEL;
    type = colorType;

    // create components
    color = new JLabel(COLOR_NAMES[model][type]);
    scalars = new JComboBox();
    scalars.addItem(ColorUtil.CLEAR);
    scalars.addItem(ColorUtil.SOLID);
    color.setLabelFor(scalars);

    // lay out components
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    JPanel p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
    p.add(Box.createHorizontalGlue());
    p.add(color);
    p.add(Box.createHorizontalGlue());
    add(p);
    add(scalars);
  }


  // -- API methods --

  /** Gets the currently selected RealType, or null if none. */
  public RealType getSelectedItem() {
    return (RealType) scalars.getSelectedItem();
  }

  /** Gets the widget's color model (RGB, HSV or COMPOSITE). */
  public int getModel() { return model; }

  /** Sets the currently selected RealType. */
  public void setSelectedItem(RealType rt) {
    if (rt == null) scalars.setSelectedIndex(0);
    else scalars.setSelectedItem(rt);
  }

  /** Sets the widget's color model (RGB, HSV or COMPOSITE). */
  public void setModel(int model) {
    this.model = model;
    if (model == ColorUtil.COMPOSITE_MODEL) {
      color.setText(COLOR_NAMES[ColorUtil.RGB_MODEL][type]);
      color.setEnabled(false);
      scalars.setEnabled(false);
    }
    else {
      color.setText(COLOR_NAMES[model][type]);
      color.setEnabled(true);
      scalars.setEnabled(true);
    }
  }

  /** Adds an item listener to this widget. */
  public void addItemListener(ItemListener l) { scalars.addItemListener(l); }

  /** Removes an item listener from this widget. */
  public void removeItemListener(ItemListener l) {
    scalars.removeItemListener(l);
  }

  /** Sets the mnemonic for this widget. */
  public void setMnemonic(char c) { color.setDisplayedMnemonic(c); }

  /** Sets the tool tip for this widget. */
  public void setToolTipText(String text) {
    color.setToolTipText(text);
    scalars.setToolTipText(text);
  }

  /** Chooses most desirable range type for this widget's color. */
  public void guessType(RealType[] rt) {
    scalars.removeAllItems();
    scalars.addItem(ColorUtil.CLEAR);
    scalars.addItem(ColorUtil.SOLID);
    if (rt != null) for (int i=0; i<rt.length; i++) scalars.addItem(rt[i]);

    // Autodetect types

    if (model == ColorUtil.RGB_MODEL || model == ColorUtil.COMPOSITE_MODEL) {
      // Case 0: no rtypes
      //   R -> none
      //   G -> none
      //   B -> none

      if (rt == null || rt.length == 0) scalars.setSelectedIndex(0); // none

      // Case 1: rtypes.length == 1
      //   R -> rtypes[0]
      //   G -> rtypes[0]
      //   B -> rtypes[0]

      else if (rt.length == 1) scalars.setSelectedItem(rt[0]);

      // Case 2: rtypes.length == 2
      //   R -> rtypes[0]
      //   G -> rtypes[1]
      //   B -> none

      // Case 3: rtypes.length >= 3
      //   R -> rtypes[0]
      //   G -> rtypes[1]
      //   B -> rtypes[2]

      else {
        if (type == 0) scalars.setSelectedItem(rt[0]);
        else if (type == 1) scalars.setSelectedItem(rt[1]);
        else if (type == 2 && rt.length >= 3) scalars.setSelectedItem(rt[2]);
        else scalars.setSelectedIndex(0); // none
      }
    }
    else { // model == ColorUtil.HSV_MODEL
      // Case 0: no rtypes
      //   H -> none
      //   S -> none
      //   V -> none

      if (rt == null || rt.length == 0) scalars.setSelectedIndex(0); // none

      // Case 1: rtypes.length >= 1
      //   H -> rtypes[0]
      //   S -> full
      //   V -> full

      else {
        if (type == 0) scalars.setSelectedItem(rt[0]);
        else scalars.setSelectedIndex(1); // full
      }
    }
  }

}
