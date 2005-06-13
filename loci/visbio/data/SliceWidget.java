//
// SliceWidget.java
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

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import loci.visbio.util.BioComboBox;
import loci.visbio.util.FormsUtil;

/** SliceWidget is a set of GUI controls for an arbitrary slice. */
public class SliceWidget extends JPanel
  implements ChangeListener, ItemListener
{

  // -- Constants --

  /** Character representing degrees symbol. */
  protected static final char DEGREES = 0x00b0;

  // -- Fields --

  /** Associated arbitrary slice. */
  protected ArbitrarySlice slice;

  /** Dropdown combo box listing available dimensions to slice through. */
  protected BioComboBox axes;

  /** Slider for adjusting yaw. */
  protected JSlider yaw;

  /** Label for current yaw value. */
  protected JLabel yawValue;

  /** Slider for adjusting pitch. */
  protected JSlider pitch;

  /** Label for current pitch value. */
  protected JLabel pitchValue;

  /** Slider for adjusting location. */
  protected JSlider location;

  /** Label for current location value. */
  protected JLabel locationValue;

  /** Slider for adjusting resolution. */
  protected JSlider res;

  /** Label for current resolution value. */
  protected JLabel resValue;

  /** Check box for whether slicing line is shown. */
  protected JCheckBox showLine;

  /** Check box for whether slice is recomputed on the fly. */
  protected JCheckBox recompute;


  // -- Constructor --

  /** Creates a new arbitrary slice widget. */
  public SliceWidget(ArbitrarySlice slice) {
    super();
    this.slice = slice;

    DataTransform parent = slice.getParent();
    String[] types = parent.getDimTypes();

    // create combo box for selecting which axis to slice through
    String[] names = new String[types.length];
    for (int i=0; i<names.length; i++) names[i] = (i + 1) + ": " + types[i];
    axes = new BioComboBox(names);
    axes.setSelectedIndex(slice.getAxis());
    axes.addItemListener(this);

    // create yaw slider
    int value = (int) slice.getYaw();
    yaw = new JSlider(-90, 90, value);
    yaw.addChangeListener(this);
    yawValue = new JLabel("" + value + DEGREES);

    // create pitch slider
    value = (int) slice.getPitch();
    pitch = new JSlider(-90, 90, value);
    pitch.setMajorTickSpacing(45);
    pitch.setMinorTickSpacing(5);
    pitch.setPaintTicks(true);
    pitch.addChangeListener(this);
    pitchValue = new JLabel("" + value + DEGREES);

    // create location slider
    value = (int) slice.getLocation();
    location = new JSlider(0, 100, value);
    location.setMajorTickSpacing(25);
    location.setMinorTickSpacing(5);
    location.setPaintTicks(true);
    location.addChangeListener(this);
    locationValue = new JLabel(value + "%");

    // create resolution slider
    int min = 64, max = 896;
    value = slice.getResolution();
    res = new JSlider(min, max, value);
    res.setMajorTickSpacing(max / 4);
    res.setMinorTickSpacing(max / 16);
    res.setPaintTicks(true);
    res.addChangeListener(this);
    resValue = new JLabel(value + " x " + value);

    // create slicing line visibility checkbox
    showLine = new JCheckBox("Show white slicing line", true);
    showLine.setMnemonic('l');
    showLine.addChangeListener(this);

    // create on-the-fly slice recompution checkbox
    recompute = new JCheckBox("Recompute slice on the fly", true);
    recompute.setMnemonic('f');
    recompute.addChangeListener(this);

    // lay out components
    PanelBuilder builder = new PanelBuilder(new FormLayout(
      "pref, 3dlu, pref:grow, 3dlu, pref", "pref, 3dlu, pref, 3dlu, " +
      "pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref"
    ));
    CellConstraints cc = new CellConstraints();
    builder.add(FormsUtil.makeRow("&Dimension to slice", axes),
      cc.xyw(1, 1, 5));

    builder.addLabel("&Yaw", cc.xy(1, 3)).setLabelFor(yaw);
    builder.add(yaw, cc.xy(3, 3));
    builder.add(yawValue, cc.xy(5, 3));

    builder.addLabel("&Pitch", cc.xy(1, 5)).setLabelFor(pitch);
    builder.add(pitch, cc.xy(3, 5));
    builder.add(pitchValue, cc.xy(5, 5));

    builder.addLabel("&Location", cc.xy(1, 7)).setLabelFor(location);
    builder.add(location, cc.xy(3, 7));
    builder.add(locationValue, cc.xy(5, 7));

    builder.addLabel("&Resolution", cc.xy(1, 9)).setLabelFor(res);
    builder.add(res, cc.xy(3, 9));
    builder.add(resValue, cc.xy(5, 9));

    builder.add(showLine, cc.xyw(1, 11, 3));

    builder.add(recompute, cc.xyw(1, 13, 3));

    setLayout(new BorderLayout());
    add(builder.getPanel());
  }


  // -- SliceWidget API methods --

  /** Updates the widget to reflect the arbitrary slice's current values. */
  public void refreshWidget() {
    int value = (int) slice.getYaw();
    if (yaw.getValue() != value) yaw.setValue(value);
    value = (int) slice.getPitch();
    if (pitch.getValue() != value) pitch.setValue(value);
    value = (int) slice.getLocation();
    if (location.getValue() != value) location.setValue(value);
    value = slice.getResolution();
    if (res.getValue() != value) res.setValue(value);
  }


  // -- ChangeListener API methods --

  /** Handles slider and check box updates. */
  public void stateChanged(ChangeEvent e) {
    Object src = e.getSource();
    if (src == yaw) yawValue.setText("" + yaw.getValue() + DEGREES);
    else if (src == pitch) pitchValue.setText("" + pitch.getValue() + DEGREES);
    else if (src == location) locationValue.setText(location.getValue() + "%");
    else if (src == res) {
      int value = res.getValue();
      resValue.setText(value + " x " + value);
    }
    updateSlice();
  }


  // -- ItemListener API methods --

  /** Handles combo box updates. */
  public void itemStateChanged(ItemEvent e) { updateSlice(); }


  // -- Helper methods --

  /** Updates arbitrary slice to match current user interface settings. */
  protected void updateSlice() {
    if (res.getValueIsAdjusting()) return;

    int axis = axes.getSelectedIndex();
    int yawVal = yaw.getValue();
    int pitchVal = pitch.getValue();
    int locVal = location.getValue();
    int resVal = res.getValue();
    boolean lineVal = showLine.isSelected();
    boolean onTheFly = recompute.isSelected();

    boolean yawAdj = yaw.getValueIsAdjusting();
    boolean pitchAdj = pitch.getValueIsAdjusting();
    boolean locAdj = location.getValueIsAdjusting();
    boolean compute = onTheFly || (!yawAdj && !pitchAdj && !locAdj);

    slice.setParameters(axis, yawVal, pitchVal,
      locVal, resVal, lineVal, compute);
  }

}
