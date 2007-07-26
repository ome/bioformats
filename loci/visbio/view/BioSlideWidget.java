//
// BioSlideWidget.java
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

package loci.visbio.view;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import loci.visbio.data.DataTransform;
import loci.visbio.util.BioArrowButton;
import loci.visbio.util.ObjectUtil;

/**
 * BioSlideWidget is a widget for exploring one dimension of a dataset.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/visbio/view/BioSlideWidget.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/visbio/view/BioSlideWidget.java">SVN</a></dd></dl>
 */
public class BioSlideWidget extends JPanel
  implements ActionListener, ChangeListener
{

  // -- GUI components --

  /** Label indicating current slider value. */
  protected JLabel current;

  /** Main slider component. */
  protected JSlider slider;

  /** Previous step button. */
  protected JButton previous;

  /** Next step button. */
  protected JButton next;

  // -- Other fields --

  /** Slider name. */
  protected String name;

  /** Slider labels. */
  protected String[] numbers;

  /** Number of ticks this slider has. */
  protected int count;

  /** Transforms affected by this widget. */
  protected Vector transforms;

  /** Indices into affected transforms. */
  protected Vector indices;

  // -- Constructor --

  /** Constructs a new slider widget from the given transform and index. */
  public BioSlideWidget(DataTransform trans, int ndx) {
    transforms = new Vector();
    indices = new Vector();
    addTransform(trans, ndx);

    // slider
    slider = new JSlider(0, count - 1, 0);
    slider.setMinorTickSpacing(1);
    slider.setMajorTickSpacing(count - 1);
    Hashtable labelTable = new Hashtable();
    int[] keys = {0, count / 4, count / 2, 3 * count / 4, count - 1};
    for (int i=0; i<keys.length; i++) {
      labelTable.put(new Integer(keys[i]), new JLabel(numbers[keys[i]]));
    }
    slider.setLabelTable(labelTable);
    slider.setSnapToTicks(true);
    slider.setPaintLabels(true);
    slider.setPaintTicks(true);
    slider.addChangeListener(this);

    // previous button
    previous = new BioArrowButton(BioArrowButton.WEST);
    previous.setActionCommand("previous");
    previous.addActionListener(this);

    // next button
    next = new BioArrowButton(BioArrowButton.EAST);
    next.setActionCommand("next");
    next.addActionListener(this);

    // lay out components
    PanelBuilder builder = new PanelBuilder(new FormLayout(
      "center:40dlu, pref, 1dlu, pref:grow, 1dlu, pref", "top:pref"));
    CellConstraints cc = new CellConstraints();
    current = builder.addLabel(name + ": " + numbers[0], cc.xy(1, 1));
    builder.add(previous, cc.xy(2, 1));
    builder.add(slider, cc.xy(4, 1));
    builder.add(next, cc.xy(6, 1));
    setLayout(new BorderLayout());
    add(builder.getPanel());
  }

  // -- BioSlideWidget API methods --

  /**
   * Adds a transform to the list affected by this slider.
   * @return true if the transform is compatible and successfully linked
   */
  public boolean addTransform(DataTransform trans, int ndx) {
    if (transforms.contains(trans)) return false; // already linked

    String[] types = trans.getDimTypes();
    if (ndx < 0 || ndx >= types.length) return false; // invalid index

    String type = types[ndx];
    String[] labels = trans.getLabels()[ndx];
    int num = labels.length;

    if (transforms.size() == 0) {
      name = type;
      numbers = labels;
      count = num;
    }
    else if (!type.equals(name) || num != count ||
      !ObjectUtil.arraysEqual(numbers, labels))
    {
      return false; // does not match already linked transforms
    }

    transforms.add(trans);
    indices.add(new Integer(ndx));
    return true;
  }

  /** Steps the slider in the given direction. */
  public void step(boolean dir) {
    int val = getValue();
    val += dir ? 1 : -1;
    if (val >= count) val = 0;
    else if (val < 0) val = count - 1;
    slider.setValue(val);
  }

  /** Gets transforms affected by this slider. */
  public DataTransform[] getTransforms() {
    DataTransform[] trans = new DataTransform[transforms.size()];
    transforms.copyInto(trans);
    return trans;
  }

  /** Gets indices into transforms affected by this slider. */
  public int[] getIndices() {
    int[] ndx = new int[indices.size()];
    for (int i=0; i<indices.size(); i++) {
      ndx[i] = ((Integer) indices.elementAt(i)).intValue();
    }
    return ndx;
  }

  /** Gets slider name. */
  public String getName() { return name; }

  /** Gets the current slider value. */
  public int getValue() { return slider.getValue(); }

  /** Gets the slider component. */
  public JSlider getSlider() { return slider; }

  // -- Component API methods --

  /** Enables or disables this dimensional slider widget. */
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    current.setEnabled(enabled);
    slider.setEnabled(enabled);
    previous.setEnabled(enabled);
    next.setEnabled(enabled);
  }

  // -- ActionListener API methods --

  /** Called when previous or next button is pushed. */
  public void actionPerformed(ActionEvent e) {
    step(e.getActionCommand().equals("next"));
  }

  // -- ChangeListener API methods --

  /** Called when the linked dimensional slider changes. */
  public void stateChanged(ChangeEvent e) {
    String s = name + ": " + numbers[getValue()];
    if (!current.getText().equals(s)) current.setText(s);
  }

}
