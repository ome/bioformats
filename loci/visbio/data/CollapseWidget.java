//
// CollapseWidget.java
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

package loci.visbio.data;

import com.jgoodies.forms.factories.ButtonBarFactory;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import loci.visbio.util.*;

/**
 * CollapseWidget is a set of GUI controls
 * for a dimensional collapse transform.
 */
public class CollapseWidget extends JPanel implements ActionListener {

  // -- Fields --

  /** Associated dimensional collapse transform. */
  protected CollapseTransform collapse;

  /** Dropdown combo box listing available dimensions for collapse. */
  protected BioComboBox axes;


  // -- Constructor --

  /** Creates a new dimensional collapse widget. */
  public CollapseWidget(CollapseTransform collapse) {
    super();
    this.collapse = collapse;

    DataTransform parent = collapse.getParent();
    String[] types = parent.getDimTypes();

    // create combo box for selecting which axis to collapse
    String[] names = new String[types.length];
    for (int i=0; i<names.length; i++) names[i] = (i + 1) + ": " + types[i];
    axes = new BioComboBox(names);

    // apply button
    JButton apply = new JButton("Apply");
    if (!LAFUtil.isMacLookAndFeel()) apply.setMnemonic('a');
    apply.addActionListener(this);

    // lay out components
    JPanel row1 = FormsUtil.makeRow(
      new Object[] {"&Dimension to collapse", axes},
      new boolean[] {false, true});
    JPanel row2 = ButtonBarFactory.buildCenteredBar(apply);

    setLayout(new BorderLayout());
    add(FormsUtil.makeColumn(row1, row2));
  }


  // -- ActionListener API methods --

  /** Applies changes to this dimensional collapse transform's parameters. */
  public void actionPerformed(ActionEvent e) {
    int index = axes.getSelectedIndex();
    collapse.setParameters(index);
  }

}
