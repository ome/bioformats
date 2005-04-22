//
// NumericOption.java
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
import javax.swing.JPanel;
import javax.swing.JTextField;
//import loci.ome.xml.CAElement;
//import loci.ome.xml.OMEElement;
import loci.visbio.util.FormsUtil;

/** NumericOption is an integer option in the VisBio Options dialog. */
public class NumericOption extends BioOption {

  // -- Fields --

  /** Panel containing GUI components. */
  private JPanel panel;

  /** Text field GUI component. */
  private JTextField field;


  // -- Constructor --

  /** Constructs a new option. */
  public NumericOption(String text, String unit, String tip, int value) {
    super(text);

    // text field
    field = new JTextField(4);
    field.setText("" + value);
    field.setToolTipText(tip);

    // lay out components
    panel = FormsUtil.makeRow(text, field, unit);
  }


  // -- NumericOption API methods --

  /** Gets this option's current setting. */
  public int getValue() {
    int value;
    try { value = Integer.parseInt(field.getText()); }
    catch (NumberFormatException exc) { value = -1; }
    return value;
  }


  // -- BioOption API methods --

  /** Gets a GUI component representing this option. */
  public Component getComponent() { return panel; }

  /** Sets the GUI component to reflect the given value. */
  public void setValue(String value) { field.setText(value); }


  // -- Saveable API methods --

  protected static final String NUMERIC_OPTION = "VisBio_NumericOption";

  /** Writes the current state to the given XML object. */
  /*public void saveState(OMEElement ome) throws SaveException {
    CAElement custom = ome.getCustomAttr();
    custom.createElement(NUMERIC_OPTION);
    custom.setAttribute("name", text);
    custom.setAttribute("value", field.getText());
  }*/

  /** Restores the current state from the given XML object. */
  /*public void restoreState(OMEElement ome) throws SaveException {
    CAElement custom = ome.getCustomAttr();
    String[] names = custom.getAttributes(NUMERIC_OPTION, "name");
    String[] values = custom.getAttributes(NUMERIC_OPTION, "value");
    for (int i=0; i<names.length; i++) {
      if (names[i].equals(text)) {
        field.setText(values[i]);
        break;
      }
    }
  }*/

}
