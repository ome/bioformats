//
// StringOption.java
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
import java.lang.Math;
import javax.swing.JPanel;
import javax.swing.JTextField;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.*;
import loci.visbio.util.XMLUtil;
import org.w3c.dom.Element;

/** StringOption is a user-supplied string option in the VisBio Options 
 * dialog. */
public class StringOption extends BioOption {

  // -- Fields --

  /** Text Field containing user's text */
  private JTextField textField;

  /** Combined text field and label component */
  private JPanel component;

  // -- Constructor --

  /** Constructs a new option. */
  public StringOption(String text, String tip, String value, String label) {
    super(text);
    textField = new JTextField(value, Math.max(value.length(), 25));
    textField.setToolTipText(tip);
    component = makePanelFrom(label, textField);
  }

  // -- StringOption API methods --

  /** Gets this option's current setting. */
  public String getValue() { return textField.getText(); }

  // -- BioOption API methods --

  /** Gets a GUI component representing this option. */
  public Component getComponent() { return component; }

  // -- Saveable API methods --

  /** Writes the current state to the given DOM element ("Options"). */
  public void saveState(Element el) throws SaveException {
    Element e = XMLUtil.createChild(el, "String");
    e.setAttribute("name", text);
    e.setAttribute("value", textField.getText());
  }

  /** Restores the current state from the given DOM element ("Options"). */
  public void restoreState(Element el) throws SaveException {
    Element[] e = XMLUtil.getChildren(el, "String");
    for (int i=0; i<e.length; i++) {
      String name = e[i].getAttribute("name");
      if (!name.equals(text)) continue;
      String value = e[i].getAttribute("value");
      textField.setText(value);
      break;
    }
  }

  // -- Helper Methods --

  /** Constructs a JPanel containing a label and a JTextField */
  private JPanel makePanelFrom(String label, JTextField textField) {
    FormLayout fl = new FormLayout("pref, 3dlu, pref", "pref");

    PanelBuilder builder = new PanelBuilder(fl);
    CellConstraints cc = new CellConstraints();

    builder.addLabel(label,   cc.xy(1, 1));
    builder.add(textField,    cc.xy(3, 1));

    return builder.getPanel();
  }
}
