//
// ListOption.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-@year@ Curtis Rueden.

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
import loci.visbio.util.*;
import org.w3c.dom.Element;

/** ListOption is an option from a list in the VisBio Options dialog. */
public class ListOption extends BioOption {

  // -- Fields --

  /** Panel containing GUI components. */
  private JPanel panel;

  /** Text box GUI component. */
  private BioComboBox box;

  // -- Constructor --

  /** Constructs a new option. */
  public ListOption(String text, String tip, String[] choices) {
    super(text);

    // combo box
    box = new BioComboBox(choices);
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

  // -- Saveable API methods --

  /** Writes the current state to the given DOM element ("Options"). */
  public void saveState(Element el) throws SaveException {
    Element e = XMLUtil.createChild(el, "List");
    e.setAttribute("name", text);
    e.setAttribute("value", getValue());
  }

  /** Restores the current state from the given DOM element ("Options"). */
  public void restoreState(Element el) throws SaveException {
    Element[] e = XMLUtil.getChildren(el, "List");
    for (int i=0; i<e.length; i++) {
      String name = e[i].getAttribute("name");
      if (!name.equals(text)) continue;
      String value = e[i].getAttribute("value");
      box.setSelectedItem(value);
      break;
    }
  }

}
