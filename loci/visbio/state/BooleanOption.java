//
// BooleanOption.java
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

import loci.visbio.util.LAFUtil;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.PrintWriter;
import javax.swing.JCheckBox;

/** BooleanOption is a true-or-false option in the VisBio Options dialog. */
public class BooleanOption extends BioOption {

  // -- Fields --

  /** Check box GUI component. */
  private JCheckBox box;


  // -- Constructor --

  /** Constructs a new option. */
  public BooleanOption(String text, char mnemonic, String tip, boolean value) {
    super(text);
    box = new JCheckBox(text, value);
    if (!LAFUtil.isMacLookAndFeel()) box.setMnemonic(mnemonic);
    box.setToolTipText(tip);
  }


  // -- BooleanOption API methods --

  /** Gets this option's current setting. */
  public boolean getValue() { return box.isSelected(); }


  // -- BioOption API methods --

  /** Gets a GUI component representing this option. */
  public Component getComponent() { return box; }

  /** Sets the GUI component to reflect the given value. */
  public void setValue(String value) {
    box.setSelected(value.equalsIgnoreCase("true"));
  }


  // -- Saveable API methods --

  /** Writes the current state to the given writer. */
  public void saveState(PrintWriter out) throws SaveException {
    /* CTR TODO for v3.00 final
    CAElement custom = ome.getCustomAttr();
    custom.createElement(BOOLEAN_OPTION);
    custom.setAttribute("name", text);
    custom.setAttribute("value", box.isSelected() ? "true" : "false");
    */
  }

  /** Restores the current state from the given reader. */
  public void restoreState(BufferedReader in) throws SaveException {
    /* CTR TODO for v3.00 final
    CAElement custom = ome.getCustomAttr();
    String[] names = custom.getAttributes(BOOLEAN_OPTION, "name");
    String[] values = custom.getAttributes(BOOLEAN_OPTION, "value");
    for (int i=0; i<names.length; i++) {
      if (names[i].equals(text)) {
        box.setSelected(values[i].equalsIgnoreCase("true"));
        break;
      }
    }
    */
  }

}
