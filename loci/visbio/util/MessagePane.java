//
// MessagePane.java
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

package loci.visbio.util;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 * MessagePane provides a dialog box for displaying a dialog to the user with
 * an "Always display this dialog" checkbox for toggling its appearance.
 */
public class MessagePane extends DialogPane {

  // -- Fields --

  /** Checkbox within dialog. */
  protected JCheckBox always;


  // -- Constructor --

  /** Creates a new message pane linked to the given checkbox. */
  public MessagePane(String title, JPanel panel, boolean allowCancel) {
    super(title, allowCancel);

    // create checkbox
    always = new JCheckBox("Always display this dialog", true);
    if (!LAFUtil.isMacLookAndFeel()) always.setMnemonic('a');

    // lay out components
    PanelBuilder builder = new PanelBuilder(new FormLayout(
      "fill:pref:grow", "fill:pref:grow, 3dlu, pref"));
    CellConstraints cc = new CellConstraints();
    builder.add(panel, cc.xy(1, 1));
    builder.add(always, cc.xy(1, 3));
    add(builder.getPanel());
  }


  // -- MessagePane API methods --

  /** Gets whether this warning pane should always be displayed. */
  public boolean isAlwaysDisplayed() { return always.isSelected(); }

}
