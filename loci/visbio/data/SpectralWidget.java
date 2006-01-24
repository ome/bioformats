//
// SpectralWidget.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2006 Curtis Rueden.

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
import java.util.Hashtable;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import loci.visbio.util.LAFUtil;
import visad.RealType;
import visad.browser.Convert;

/**
 * SpectralWidget is a set of GUI controls for a spectral mapping transform.
 */
public class SpectralWidget extends JPanel implements ActionListener {

  // -- Constants --

  /** Granularity of the weighted sliders. */
  protected static final int PRECISION = 100;


  // -- Fields --

  /** Associated spectral mapping. */
  protected SpectralTransform mapping;

  /** Weighted sliders for spectral transform. */
  protected JSlider[][] weights;

  /** Labels indicating current slider values. */
  protected JLabel[][] labels;


  // -- Constructor --

  /** Creates a new spectral mapping widget with weighted sliders. */
  public SpectralWidget(SpectralTransform mapping, String[] text) {
    super();
    this.mapping = mapping;

    // create sliders, labels and auto-set buttons
    RealType[] range = ((ImageTransform) mapping.getParent()).getRangeTypes();
    int in = range.length;
    int out = mapping.getRangeCount();
    weights = new JSlider[out][in];
    labels = new JLabel[out][in];

    Hashtable labelHash = new Hashtable();
    labelHash.put(new Integer(-PRECISION), new JLabel("-1"));
    labelHash.put(new Integer(0), new JLabel("0"));
    labelHash.put(new Integer(PRECISION), new JLabel("1"));

    JButton[] negOnes = new JButton[out];
    JButton[] zeroes = new JButton[out];
    JButton[] ones = new JButton[out];

    double[][] w = mapping.getWeights();

    for (int o=0; o<out; o++) {
      for (int i=0; i<in; i++) {
        int value = (int) (PRECISION * w[o][i]);
        JSlider s = new JSlider(-PRECISION, PRECISION, value);
        if (i == in - 1) {
          s.setMajorTickSpacing(PRECISION);
          s.setMinorTickSpacing(PRECISION / 10);
          s.setLabelTable(labelHash);
          s.setPaintTicks(true);
          s.setPaintLabels(true);
        }

        final JLabel l = new JLabel(shortString(value));
        s.addChangeListener(new ChangeListener() {
          public void stateChanged(ChangeEvent e) {
            l.setText(shortString(((JSlider) e.getSource()).getValue()));
          }
        });

        weights[o][i] = s;
        labels[o][i] = l;
      }

      negOnes[o] = new JButton("-1");
      negOnes[o].setActionCommand("-1:" + o);
      negOnes[o].addActionListener(this);

      zeroes[o] = new JButton("0");
      zeroes[o].setActionCommand("0:" + o);
      zeroes[o].addActionListener(this);

      ones[o] = new JButton("1");
      ones[o].setActionCommand("1:" + o);
      ones[o].addActionListener(this);
    }

    // apply button
    JButton apply = new JButton("Apply");
    if (!LAFUtil.isMacLookAndFeel()) apply.setMnemonic('a');
    apply.setActionCommand("apply");
    apply.addActionListener(this);

    // lay out components
    String s = "pref, 3dlu:grow, pref, 3dlu:grow, pref";
    StringBuffer cols = new StringBuffer("pref, 3dlu, " + s + ", 3dlu, pref");
    for (int o=1; o<out; o++) {
      cols.append(", 9dlu, " + s + ", 3dlu, pref");
    }

    StringBuffer rows = new StringBuffer("pref, 3dlu, pref");
    for (int i=1; i<in; i++) rows.append(", 3dlu, pref");
    rows.append(", 3dlu, pref, 5dlu, pref");

    PanelBuilder builder = new PanelBuilder(
      new FormLayout(cols.toString(), rows.toString()));
    CellConstraints cc = new CellConstraints();

    for (int i=0; i<in; i++) {
      int row = 2 * i + 3;
      builder.addLabel(range[i].getName(),
        cc.xy(1, row, CellConstraints.RIGHT, CellConstraints.TOP));
    }

    for (int o=0; o<out; o++) {
      int col = 8 * o + 3;
      builder.addLabel(text[o],
        cc.xyw(col, 1, 5, CellConstraints.CENTER, CellConstraints.DEFAULT));
      for (int i=0; i<in; i++) {
        int row = 2 * i + 3;
        builder.add(weights[o][i], cc.xyw(col, row, 5));
        builder.add(labels[o][i],
          cc.xy(col + 6, row, CellConstraints.DEFAULT, CellConstraints.TOP));
      }

      int row = 2 * in + 3;
      builder.add(negOnes[o], cc.xy(col, row));
      builder.add(zeroes[o], cc.xy(col + 2, row));
      builder.add(ones[o], cc.xy(col + 4, row));
    }

    builder.add(ButtonBarFactory.buildCenteredBar(apply),
      cc.xyw(1, 2 * in + 5, 8 * out + 1));

    setLayout(new BorderLayout());
    add(builder.getPanel());
  }


  // -- ActionListener API methods --

  /** Applies changes to this spectral mapping's parameters. */
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if (cmd.equals("apply")) {
      int out = weights.length;
      int in = weights[0].length;
      double[][] w = new double[out][in];
      for (int o=0; o<out; o++) {
        for (int i=0; i<in; i++) {
          w[o][i] = (double) weights[o][i].getValue() / PRECISION;
        }
      }
      mapping.setParameters(w);
    }
    else {
      int colon = cmd.indexOf(":");
      if (colon < 0) return;
      try {
        double val = Double.parseDouble(cmd.substring(0, colon));
        int ndx = Integer.parseInt(cmd.substring(colon + 1));
        for (int i=0; i<weights[ndx].length; i++) {
          weights[ndx][i].setValue((int) (PRECISION * val));
        }
      }
      catch (NumberFormatException exc) { return; }
    }
  }


  // -- Helper methods --

  /**
   * Converts a number between -1000 and 1000 into a number between -1.000 and
   * 1.000, with exactly three digits after the decimal point.
   */
  protected static String shortString(int value) {
    String s = Convert.shortString((double) value / PRECISION);
    int ndx = s.indexOf(".");
    if (ndx < 0) s += ".000";
    return s;
  }

}
