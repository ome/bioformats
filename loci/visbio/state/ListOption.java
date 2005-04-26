//
// ListOption.java
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
import java.io.BufferedReader;
import java.io.PrintWriter;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import loci.visbio.util.FormsUtil;

/** ListOption is an option from a list in the VisBio Options dialog. */
public class ListOption extends BioOption {

  // -- Fields --

  /** Panel containing GUI components. */
  private JPanel panel;

  /** Text box GUI component. */
  private JComboBox box;


  // -- Constructor --

  /** Constructs a new option. */
  public ListOption(String text, String tip, String[] choices) {
    super(text);

    // combo box
    box = new JComboBox(choices);
    box.setToolTipText(tip);
    box.setSelectedIndex(0);

    // lay out components
    panel = FormsUtil.makeRow(text, box);
  }


  // -- ListOption API methods --

  /** Gets this option's current setting. */
  public String getValue() { return (String) box.getSelectedItem(); }


  // -- BioOption API methods --

  /** Gets a GUI component representing this option. */
  public Component getComponent() { return panel; }

  /** Sets the GUI component to reflect the given value. */
  public void setValue(String value) { box.setSelectedItem(value); }


  // -- Saveable API methods --

  protected static final String LIST_OPTION = "VisBio_ListOption";

  /** Writes the current state to the given writer. */
  public void saveState(PrintWriter out) throws SaveException {
    /* CTR TODO for v3.00 final
    CAElement custom = ome.getCustomAttr();
    custom.createElement(LIST_OPTION);
    custom.setAttribute("name", text);
    custom.setAttribute("value", getValue());
    */
  }

  /** Restores the current state from the given reader. */
  public void restoreState(BufferedReader in) throws SaveException {
    /* CTR TODO for v3.00 final
    CAElement custom = ome.getCustomAttr();
    String[] names = custom.getAttributes(LIST_OPTION, "name");
    String[] values = custom.getAttributes(LIST_OPTION, "value");
    for (int i=0; i<names.length; i++) {
      if (names[i].equals(text)) {
        setValue(values[i]);
        break;
      }
    }
    */
  }

}
