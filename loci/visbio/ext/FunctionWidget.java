//
// FunctionWidget.java
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

package loci.visbio.ext;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import loci.visbio.util.LAFUtil;

/**
 * FunctionWidget is a set of GUI controls
 * for an ExternalFunction transform.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/visbio/ext/FunctionWidget.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/visbio/ext/FunctionWidget.java">SVN</a></dd></dl>
 */
public class FunctionWidget extends JPanel implements ActionListener {

  // -- Fields --

  /** Associated external function transform. */
  protected ExternalFunction function;

  /** Fields corresponding to function parameters. */
  protected JTextField[] paramFields;

  // -- Constructor --

  public FunctionWidget(ExternalFunction function) {
    super();
    this.function = function;

    String[] names = function.getParameterNames();
    String[] params = function.getParameters();

    // create text fields
    paramFields = new JTextField[params.length];
    for (int i=0; i<params.length; i++) {
      paramFields[i] = new JTextField(8);
      paramFields[i].setText(params[i]);
    }

    // create apply button
    JButton apply = new JButton("Apply");
    if (!LAFUtil.isMacLookAndFeel()) apply.setMnemonic('a');
    apply.addActionListener(this);

    // lay out components
    StringBuffer sb = new StringBuffer("pref");
    for (int i=1; i<paramFields.length; i++) sb.append(", 3dlu, pref");
    sb.append(", 9dlu, pref");
    PanelBuilder builder = new PanelBuilder(
      new FormLayout("pref, 3dlu, pref:grow", sb.toString()));
    CellConstraints cc = new CellConstraints();
    for (int i=0; i<paramFields.length; i++) {
      int row = 2 * i + 1;
      builder.addLabel(names[i], cc.xy(1, row));
      builder.add(paramFields[i], cc.xy(3, row));
    }
    builder.add(ButtonBarFactory.buildCenteredBar(apply),
      cc.xyw(1, 2 * paramFields.length + 1, 3));

    setLayout(new BorderLayout());
    add(builder.getPanel());
  }

  // -- ActionListener API methods --

  /** Applies changes to this data sampling's parameters. */
  public void actionPerformed(ActionEvent e) {
    String[] params = new String[paramFields.length];
    for (int i=0; i<params.length; i++) params[i] = paramFields[i].getText();
    function.setParameters(params);
  }

}
