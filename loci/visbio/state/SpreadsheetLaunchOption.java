//
// SpreadsheetLaunchOption.java
//

/*
VisBio application for visualization of multidimensional biological
image data. Copyright (C) 2002-@year@ Curtis Rueden and Abraham Sorber.

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

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.*;
import java.awt.Component;
import java.awt.event.*;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import loci.visbio.overlays.SpreadsheetLaunchException;
import loci.visbio.overlays.SpreadsheetLauncher;
import loci.visbio.util.LAFUtil;
import loci.visbio.util.XMLUtil;
import org.w3c.dom.Element;

/** Option to toggle whether spreadsheet automatically launches when overlays
 *  are exported.  Also allows user to specify path to spreadsheet application.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/visbio/state/SpreadsheetLaunchOption.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/visbio/state/SpreadsheetLaunchOption.java">SVN</a></dd></dl>
 */
public class SpreadsheetLaunchOption extends BioOption
  implements ActionListener
{

  // -- Fields --

  /** Text Field containing user's text. */
  private JTextField textField;

  /** Combined text field and label component. */
  private JPanel component;

  /** Should the spreadsheet launch automatcially? */
  private JCheckBox box;

  /** Button to restore default path. */
  private JButton button;

  // -- Constructor --

  /** Constructs a new option. */
  public SpreadsheetLaunchOption(char mnemonic, String textValue, boolean
      boxValue)
  {
    super(SpreadsheetOptionStrategy.getText());

    box = new JCheckBox(SpreadsheetOptionStrategy.getText(), boxValue);
    if (!LAFUtil.isMacLookAndFeel()) box.setMnemonic(mnemonic);
    box.setToolTipText(SpreadsheetOptionStrategy.getBoxTip());
    box.addActionListener(this);
    box.setActionCommand("setSelected");

    textField = new JTextField(textValue, Math.max(textValue.length(), 25));
    textField.setToolTipText(SpreadsheetOptionStrategy.getTextTip());
    textField.setEnabled(box.isSelected());

    button = new JButton("Restore default path");
    button.setToolTipText(SpreadsheetOptionStrategy.getButtonTip());
    button.addActionListener(this);
    button.setActionCommand("restoreDefaultPath");
    button.setEnabled(box.isSelected());

    component = makePanelFrom(SpreadsheetOptionStrategy.getLabel(), textField,
        box);
  }

  // -- SpreadsheetLaunchOption API methods --

  /** Gets this option's current setting. */
  public String getValue() {
    return textField.getText();
  }

  /** Gets whether the checkbox is selected. */
  public boolean getSelected() { return box.isSelected(); }

  // -- BioOption API methods --

  /** Gets a GUI component representing this option. */
  public Component getComponent() { return component; }

  // -- Saveable API methods --

  /** Writes the current state to the given DOM element ("Options"). */
  public void saveState(Element el) throws SaveException {
    Element e = XMLUtil.createChild(el, "String");
    e.setAttribute("name", text);
    e.setAttribute("value", textField.getText());
    e.setAttribute("selected", box.isSelected() ? "true" : "false");
  }

  /** Restores the current state from the given DOM element ("Options"). */
  public void restoreState(Element el) throws SaveException {
    Element[] e = XMLUtil.getChildren(el, "String");
    for (int i=0; i<e.length; i++) {
      String name = e[i].getAttribute("name");
      if (!name.equals(text)) continue;
      String value = e[i].getAttribute("value");
      boolean selected = e[i].getAttribute("selected").equalsIgnoreCase("true");
      textField.setText(value);
      box.setSelected(selected);
      break;
    }
  }

  // -- ActionListener Interface methods --

  /** Responds to changes in checkbox state. */
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("setSelected")) {
      textField.setEnabled(box.isSelected());
      button.setEnabled(box.isSelected());
    }
    else if (e.getActionCommand().equals("restoreDefaultPath")) {
      String s = "";
      try {
        s = SpreadsheetLauncher.getDefaultApplicationPath();
      }
      catch (SpreadsheetLaunchException ex) {}
      textField.setText(s);
    }
  }

  // -- Helper Methods --

  /** Constructs a JPanel containing a label and a JTextField */
  private JPanel makePanelFrom(String label, JTextField field, JCheckBox bx) {
    FormLayout fl = new FormLayout("pref, 3dlu, pref ",
        "3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu");

    PanelBuilder builder = new PanelBuilder(fl);
    CellConstraints cc = new CellConstraints();

    builder.add(bx,          cc.xyw(1, 2, 3));
    builder.addLabel(label,   cc.xy(1, 4));
    builder.add(field,        cc.xy(3, 4));
    builder.add(button,       cc.xy(1, 6));

    return builder.getPanel();
  }
}
