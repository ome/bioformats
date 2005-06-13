//
// BooleanOption.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2005 Curtis Rueden.

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
import javax.swing.JCheckBox;
import loci.visbio.util.LAFUtil;
import loci.visbio.util.XMLUtil;
import org.w3c.dom.Element;

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


  // -- Saveable API methods --

  /** Writes the current state to the given DOM element ("Options"). */
  public void saveState(Element el) throws SaveException {
    Element e = XMLUtil.createChild(el, "Boolean");
    e.setAttribute("name", text);
    e.setAttribute("value", box.isSelected() ? "true" : "false");
  }

  /** Restores the current state from the given DOM element ("Options"). */
  public void restoreState(Element el) throws SaveException {
    Element[] e = XMLUtil.getChildren(el, "Boolean");
    for (int i=0; i<e.length; i++) {
      String name = e[i].getAttribute("name");
      if (!name.equals(text)) continue;
      boolean value = e[i].getAttribute("value").equalsIgnoreCase("true");
      box.setSelected(value);
      break;
    }
  }

}
