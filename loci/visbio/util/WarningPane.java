//
// WarningPane.java
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

package loci.visbio.util;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.util.StringTokenizer;
import javax.swing.JPanel;

/**
 * WarningPane provides a dialog box for displaying a warning to the user.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/visbio/util/WarningPane.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/visbio/util/WarningPane.java">SVN</a></dd></dl>
 */
public class WarningPane extends MessagePane {

  // -- Constructor --

  /** Creates a new warning pane linked to the given warning checkbox. */
  public WarningPane(String text, boolean allowCancel) {
    super("VisBio Warning", makePanel(text), allowCancel);
  }

  // -- Helper methods --

  /** Creates a panel containing the given text. */
  private static JPanel makePanel(String text) {
    StringTokenizer st = new StringTokenizer(text, "\n\r");
    int count = st.countTokens();
    StringBuffer sb = new StringBuffer("pref");
    for (int i=1; i<count; i++) sb.append(", pref");

    // lay out components
    PanelBuilder builder = new PanelBuilder(
      new FormLayout("pref", sb.toString()));
    CellConstraints cc = new CellConstraints();
    for (int y=1; y<=count; y++) builder.addLabel(st.nextToken(), cc.xy(1, y));
    return builder.getPanel();
  }

}
