//
// ResolutionOption.java
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

import java.awt.Component;
import javax.swing.*;
import loci.visbio.util.FormsUtil;
import loci.visbio.util.XMLUtil;
import org.w3c.dom.Element;

/**
 * ResolutionOption is an option for changing image resolution
 * (two positive integers) in the VisBio Options dialog.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/visbio/state/ResolutionOption.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/visbio/state/ResolutionOption.java">SVN</a></dd></dl>
 */
public class ResolutionOption extends BioOption {

  // -- Fields --

  /** Panel containing GUI components. */
  private JPanel panel;

  /** X resolution text field GUI component. */
  private JTextField resX;

  /** Y resolution text field GUI component. */
  private JTextField resY;

  // -- Constructor --

  /** Constructs a new option. */
  public ResolutionOption(String text, String tip, int valueX, int valueY) {
    super(text);

    // X resolution text field
    resX = new JTextField(4);
    resX.setText("" + valueX);
    resX.setToolTipText(tip);

    // Y resolution text field
    resY = new JTextField(4);
    resY.setText("" + valueY);
    resY.setToolTipText(tip);

    // lay out components
    panel = FormsUtil.makeRow(new Object[]
      {text + ":", resX, new JLabel("x"), resY});
  }

  // -- ResolutionOption API methods --

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

  // -- Saveable API methods --

  /** Writes the current state to the given DOM element ("Options"). */
  public void saveState(Element el) throws SaveException {
    Element e = XMLUtil.createChild(el, "Resolution");
    e.setAttribute("name", text);
    e.setAttribute("resX", "" + getValueX());
    e.setAttribute("resY", "" + getValueY());
  }

  /** Restores the current state from the given DOM element ("Options"). */
  public void restoreState(Element el) throws SaveException {
    Element[] e = XMLUtil.getChildren(el, "Resolution");
    for (int i=0; i<e.length; i++) {
      String name = e[i].getAttribute("name");
      if (!name.equals(text)) continue;
      String valueX = e[i].getAttribute("resX");
      resX.setText(valueX);
      String valueY = e[i].getAttribute("resY");
      resY.setText(valueY);
      break;
    }
  }

}
