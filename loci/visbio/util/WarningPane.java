//
// WarningPane.java
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

package loci.visbio.util;

import com.jgoodies.forms.builder.PanelBuilder;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import java.util.StringTokenizer;

import javax.swing.JCheckBox;

/** WarningPane provides a dialog box for displaying a warning to the user. */
public class WarningPane extends DialogPane {

  // -- Fields --

  /** Checkbox within warning pane. */
  private JCheckBox always;


  // -- Constructor --

  /** Creates a new warning pane linked to the given warning checkbox. */
  public WarningPane(String text, boolean allowCancel) {
    super("VisBio Warning", allowCancel);

    always = new JCheckBox("Always display this warning", true);
    always.setMnemonic('a');

    StringTokenizer st = new StringTokenizer(text, "\n\r");
    int count = st.countTokens();
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<count; i++) sb.append("pref, ");
    sb.append("3dlu, pref");

    // lay out components
    FormLayout layout = new FormLayout("pref", sb.toString());
    PanelBuilder builder = new PanelBuilder(layout);
    CellConstraints cc = new CellConstraints();
    for (int y=1; y<=count; y++) builder.addLabel(st.nextToken(), cc.xy(1, y));
    builder.add(always, cc.xy(1, count + 2));
    add(builder.getPanel());
  }


  // -- WarningPane API methods --

  /** Gets whether this warning pane should always be displayed. */
  public boolean isAlwaysDisplayed() { return always.isSelected(); }

}
