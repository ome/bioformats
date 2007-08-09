//
// SamplingWidget.java
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

package loci.visbio.data;

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
 * SamplingWidget is a set of GUI controls for a DataSampling transform.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/visbio/data/SamplingWidget.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/visbio/data/SamplingWidget.java">SVN</a></dd></dl>
 */
public class SamplingWidget extends JPanel implements ActionListener {

  // -- Fields --

  /** Associated data sampling. */
  protected DataSampling sampling;

  /** Text field for image width. */
  protected JTextField widthField;

  /** Text field for image height. */
  protected JTextField heightField;

  /** Text fields for minimum dimensional values. */
  protected JTextField[] minFields;

  /** Text fields for maximum dimensional values. */
  protected JTextField[] maxFields;

  /** Text fields for dimensional step values. */
  protected JTextField[] stepFields;

  /** Check boxes for included range components. */
  protected JCheckBox[] rangeBoxes;

  // -- Constructor --

  public SamplingWidget(DataSampling sampling) {
    super();
    this.sampling = sampling;

    int[] lengths = sampling.getLengths();
    String[] dims = sampling.getDimTypes();
    int[] min = sampling.getMin();
    int[] max = sampling.getMax();
    int[] step = sampling.getStep();
    int resX = sampling.getImageWidth();
    int resY = sampling.getImageHeight();
    boolean[] range = sampling.getRange();

    widthField = new JTextField("" + resX, 4);
    heightField = new JTextField("" + resY, 4);

    minFields = new JTextField[min.length];
    maxFields = new JTextField[max.length];
    stepFields = new JTextField[step.length];
    for (int i=0; i<lengths.length; i++) {
      minFields[i] = new JTextField("" + min[i], 4);
      maxFields[i] = new JTextField("" + max[i], 4);
      stepFields[i] = new JTextField("" + step[i], 4);
    }

    JPanel rangePanel = new JPanel();
    rangePanel.add(new JLabel("Range components"));
    rangeBoxes = new JCheckBox[range.length];
    for (int i=0; i<range.length; i++) {
      rangeBoxes[i] = new JCheckBox("" + (i + 1), range[i]);
      if (i < 9 && !LAFUtil.isMacLookAndFeel()) {
        rangeBoxes[i].setMnemonic('1' + i);
      }
      rangePanel.add(rangeBoxes[i]);
    }

    JButton apply = new JButton("Apply");
    if (!LAFUtil.isMacLookAndFeel()) apply.setMnemonic('a');
    apply.addActionListener(this);

    // lay out components
    StringBuffer sb = new StringBuffer("pref, 3dlu, ");
    for (int i=0; i<lengths.length; i++) sb.append("pref, 3dlu, ");
    sb.append("pref, 3dlu, pref");
    FormLayout layout = new FormLayout(
      "pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref",
      sb.toString());
    PanelBuilder builder = new PanelBuilder(layout);
    CellConstraints cc = new CellConstraints();
    builder.addLabel("Image &resolution", cc.xyw(1, 1, 5));
    builder.add(widthField, cc.xy(7, 1));
    builder.addLabel("&by", cc.xy(9, 1));
    builder.add(heightField, cc.xy(11, 1));
    for (int i=0; i<lengths.length; i++) {
      int row = 2 * i + 3;
      builder.addLabel("<" + (i + 1) + "> " + dims[i], cc.xy(1, row));
      builder.add(minFields[i], cc.xy(3, row));
      builder.addLabel("to", cc.xy(5, row));
      builder.add(maxFields[i], cc.xy(7, row));
      builder.addLabel("step", cc.xy(9, row));
      builder.add(stepFields[i], cc.xy(11, row));
    }
    builder.add(rangePanel, cc.xyw(1, 2 * lengths.length + 3, 11));
    builder.add(ButtonBarFactory.buildCenteredBar(apply),
      cc.xyw(1, 2 * lengths.length + 5, 11));

    setLayout(new BorderLayout());
    add(builder.getPanel());
  }

  // -- ActionListener API methods --

  /** Applies changes to this data sampling's parameters. */
  public void actionPerformed(ActionEvent e) {
    String msg = null;
    int resX = -1, resY = -1;
    int[] min = null, max = null, step = null;
    try {
      resX = Integer.parseInt(widthField.getText());
      resY = Integer.parseInt(heightField.getText());
      min = parseInts(minFields);
      max = parseInts(maxFields);
      step = parseInts(stepFields);
    }
    catch (NumberFormatException exc) { }
    boolean[] range = new boolean[rangeBoxes.length];
    int numRange = 0;
    for (int i=0; i<rangeBoxes.length; i++) {
      range[i] = rangeBoxes[i].isSelected();
      if (range[i]) numRange++;
    }

    // check parameters for validity
    if (resX <= 0 || resY <= 0) msg = "Invalid image resolution.";
    else {
      DataTransform parent = sampling.getParent();
      int[] len = parent.getLengths();
      String[] types = parent.getDimTypes();
      for (int i=0; i<minFields.length; i++) {
        String dim = "<" + (i + 1) + "> " + types[i];
        if (min[i] < 1 || min[i] > len[i]) msg = "Invalid minimum for " + dim;
        else if (max[i] < 1 || max[i] > len[i]) {
          msg = "Invalid maximum for " + dim;
        }
        else if (step[i] < 1) msg = "Invalid step size for " + dim;
        else if (min[i] > max[i]) {
          msg = "Minimum cannot be greater than maximum for " + dim;
        }
        if (msg != null) break;
      }
    }
    if (numRange == 0) {
      msg = "Sampling must include at least one range component.";
    }

    if (msg != null) {
      JOptionPane.showMessageDialog(this, msg, "VisBio",
        JOptionPane.ERROR_MESSAGE);
      return;
    }

    sampling.setParameters(min, max, step, resX, resY, range);
  }

  // -- Helper methods --

  /** Parses integer values from the given list of text fields. */
  private int[] parseInts(JTextField[] fields) {
    int[] vals = new int[fields.length];
    for (int i=0; i<fields.length; i++) {
      vals[i] = Integer.parseInt(fields[i].getText());
    }
    return vals;
  }

}
