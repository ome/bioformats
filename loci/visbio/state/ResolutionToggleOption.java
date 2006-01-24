//
// ResolutionToggleOption.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2006 Curtis Rueden.

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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import loci.visbio.util.*;
import org.w3c.dom.Element;

/**
 * ResolutionToggleOption is an option for toggling image resolution
 * (two positive integers) in the VisBio Options dialog.
 */
public class ResolutionToggleOption extends BioOption
  implements ActionListener
{

  // -- Fields --

  /** Panel containing GUI components. */
  private JPanel panel;

  /** Check box for toggling whether resolution values are used. */
  private JCheckBox box;

  /** X resolution text field GUI component. */
  private JTextField resX;

  /** Y resolution text field GUI component. */
  private JTextField resY;


  // -- Constructor --

  /** Constructs a new option. */
  public ResolutionToggleOption(String text, char mnemonic,
    String tip, boolean value, int valueX, int valueY)
  {
    super(text);

    // resolution toggle checkbox
    box = new JCheckBox(text + ":", value);
    if (!LAFUtil.isMacLookAndFeel()) box.setMnemonic(mnemonic);
    box.addActionListener(this);

    // X resolution text field
    resX = new JTextField(4);
    resX.setText("" + valueX);
    resX.setToolTipText(tip);
    resX.setEnabled(value);

    // Y resolution text field
    resY = new JTextField(4);
    resY.setText("" + valueY);
    resY.setToolTipText(tip);
    resY.setEnabled(value);

    // lay out components
    panel = FormsUtil.makeRow(new Object[] {box, resX, new JLabel("x"), resY});
  }


  // -- ResolutionToggleOption API methods --

  /** Gets whether resolution toggle is on. */
  public boolean getValue() { return box.isSelected(); }

  /** Gets this option's current X resolution. */
  public int getValueX() {
    int valueX;
    try { valueX = Integer.parseInt(resX.getText()); }
    catch (NumberFormatException exc) { valueX = -1; }
    return valueX;
  }

  /** Gets this option's current Y resolution. */
  public int getValueY() {
    int valueY;
    try { valueY = Integer.parseInt(resY.getText()); }
    catch (NumberFormatException exc) { valueY = -1; }
    return valueY;
  }


  // -- BioOption API methods --

  /** Gets a GUI component representing this option. */
  public Component getComponent() { return panel; }


  // -- ActionListener API methods --

  /** Handles checkbox toggles. */
  public void actionPerformed(ActionEvent e) {
    boolean b = getValue();
    resX.setEnabled(b);
    resY.setEnabled(b);
  }


  // -- Saveable API methods --

  /** Writes the current state to the given DOM element ("Options"). */
  public void saveState(Element el) throws SaveException {
    Element e = XMLUtil.createChild(el, "ResolutionToggle");
    e.setAttribute("name", text);
    e.setAttribute("value", "" + getValue());
    e.setAttribute("resX", "" + getValueX());
    e.setAttribute("resY", "" + getValueY());
  }

  /** Restores the current state from the given DOM element ("Options"). */
  public void restoreState(Element el) throws SaveException {
    Element[] e = XMLUtil.getChildren(el, "ResolutionToggle");
    for (int i=0; i<e.length; i++) {
      String name = e[i].getAttribute("name");
      if (!name.equals(text)) continue;
      boolean value = e[i].getAttribute("value").equalsIgnoreCase("true");
      box.setSelected(value);
      String valueX = e[i].getAttribute("resX");
      resX.setText(valueX);
      String valueY = e[i].getAttribute("resY");
      resY.setText(valueY);
      break;
    }
  }

}
