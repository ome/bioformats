//
// SliceWidget.java
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

package loci.visbio.data;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.BorderLayout;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import loci.visbio.util.FormsUtil;

/** SliceWidget is a set of GUI controls for an arbitrary slice. */
public class SliceWidget extends JPanel implements ChangeListener {

  // -- Constants --

  /** Character representing degrees symbol. */
  protected static final char DEGREES = 0x00b0;

  // -- Fields --

  /** Associated arbitrary slice. */
  protected ArbitrarySlice slice;

  /** Dropdown combo box listing available dimensions to slice through. */
  protected JComboBox axes;

  protected JSlider yaw;
  protected JLabel yawValue;
  protected JSlider pitch;
  protected JLabel pitchValue;
  protected JSlider location;
  protected JLabel locationValue;
  protected JSlider res;
  protected JLabel resValue;
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
    axes = new JComboBox(names);

    // create yaw slider
    yaw = new JSlider(-90, 90, 0);
    yaw.addChangeListener(this);
    yawValue = new JLabel("0" + DEGREES);

    // create pitch slider
    pitch = new JSlider(-90, 90, 0);
    pitch.setMajorTickSpacing(45);
    pitch.setMinorTickSpacing(5);
    pitch.setPaintTicks(true);
    pitch.addChangeListener(this);
    pitchValue = new JLabel("0°");

    // create location slider
    location = new JSlider(0, 100, 50);
    location.setMajorTickSpacing(25);
    location.setMinorTickSpacing(5);
    location.setPaintTicks(true);
    location.addChangeListener(this);
    locationValue = new JLabel("50%");

    // create resolution slider
    int max = 512; // TEMP
    res = new JSlider(0, max, 64);
    res.setMajorTickSpacing(max / 4);
    res.setMinorTickSpacing(max / 16);
    res.setPaintTicks(true);
    res.addChangeListener(this);
    resValue = new JLabel("64 x 64");

    // create on-the-fly slice recompution checkbox
    recompute = new JCheckBox("Recompute slice on the fly", true);
    recompute.setMnemonic('f');

    // lay out components
    PanelBuilder builder = new PanelBuilder(new FormLayout(
      "pref, 3dlu, pref:grow, 3dlu, pref",
      "pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref, 3dlu, pref"
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

    builder.add(recompute, cc.xyw(1, 11, 3));

    setLayout(new BorderLayout());
    add(builder.getPanel());
  }


  // -- ChangeListener API methods --

  /** Handles slider updates. */
  public void stateChanged(ChangeEvent e) {
    Object src = e.getSource();
    if (src == yaw) {
      yawValue.setText("" + yaw.getValue() + DEGREES);
    }
    else if (src == pitch) {
      pitchValue.setText("" + pitch.getValue() + DEGREES);
    }
    else if (src == location) {
      locationValue.setText(location.getValue() + "%");
    }
    else if (src == res) {
      int value = res.getValue();
      resValue.setText(value + " x " + value);
    }
  }

}
