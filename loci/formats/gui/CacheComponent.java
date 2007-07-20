//
// CacheComponent.java
//

package loci.formats.gui;

import com.jgoodies.forms.layout.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.*;
import loci.formats.*;
import loci.formats.cache.*;

/** GUI component for managing a cache. */
public class CacheComponent extends JPanel
  implements ActionListener, ChangeListener
{

  // -- Constants --

  protected static final String[] STRATEGIES = {"Crosshair", "Rectangle"};
  protected static final String[] PRIORITIES =
    {"maximum", "high", "normal", "low", "minimum"};
  protected static final String[] ORDER = {"centered", "forward", "backward"};

  // -- Fields --

  /** The cache that this component controls. */
  private Cache cache;

  /** File that the cache is working with. */
  private String file;

  /** Spinners for choosing range of slices to cache. */
  private Vector range;

  /** Buttons for choosing axis priority. */
  private JRadioButton[][] priority;

  // -- Constructor --

  public CacheComponent(Cache cache, boolean doSource, String[] axisLabels,
    String file)
  {
    super();
    this.cache = cache;
    this.file = file;

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    CellConstraints cc = new CellConstraints();

    range = new Vector();

    JPanel top = new JPanel();
    FormLayout layout = new FormLayout("pref,3dlu,pref:grow",
      doSource ? "pref:grow,3dlu,pref:grow" : "pref:grow");
    top.setLayout(layout);

    // add source choices, if desired
    JComboBox sourceChooser = null;
    if (doSource) {
      JLabel label = new JLabel("Objects to cache: ");
      top.add(label, cc.xy(1, 1));
      String[] sources =
        new String[] {"byte arrays", "BufferedImages", "ImageProcessors"};
      sourceChooser = new JComboBox(sources);
      sourceChooser.setActionCommand("source");
      sourceChooser.addActionListener(this);
      top.add(sourceChooser, cc.xy(3, 1));
    }

    // add strategy choices
    JLabel label = new JLabel("Caching strategy: ");
    top.add(label, cc.xy(1, doSource ? 3 : 1));
    JComboBox strategyChooser = new JComboBox(STRATEGIES);
    strategyChooser.setActionCommand("strategy");
    strategyChooser.addActionListener(this);
    top.add(strategyChooser, cc.xy(3, doSource ? 3 : 1));
    add(top);

    // add cache size choices

    JPanel middle = new JPanel();
    StringBuffer rows = new StringBuffer();
    rows.append("pref:grow");
    for (int i=0; i<axisLabels.length; i++) rows.append(",3dlu,pref:grow");
    layout = new FormLayout(
      "pref:grow,3dlu,pref:grow,3dlu,pref:grow,3dlu,pref:grow",
      rows.toString());
    middle.setLayout(layout);

    middle.add(new JLabel("Axis"), cc.xy(1, 1));
    middle.add(new JLabel("Range"), cc.xy(3, 1));
    middle.add(new JLabel("Priority"), cc.xy(5, 1));
    middle.add(new JLabel("Order"), cc.xy(7, 1));

    int[] lengths = cache.getStrategy().getLengths();
    for (int i=0; i<axisLabels.length; i++) {
      JLabel l = new JLabel(axisLabels[i]);
      middle.add(l, cc.xy(1, i*2 + 3));
      JSpinner r = new JSpinner(new SpinnerNumberModel(1, 0, lengths[i], 1));
      middle.add(r, cc.xy(3, i*2 + 3));
      range.add(r);
      JComboBox prio = new JComboBox(PRIORITIES);
      prio.setSelectedIndex(PRIORITIES.length / 2);
      middle.add(prio, cc.xy(5, i*2 + 3));
      JComboBox ord = new JComboBox(ORDER);
      middle.add(ord, cc.xy(7, i*2 + 3));
    }

    add(middle);

    if (sourceChooser != null) updateSource(sourceChooser);
    updateStrategy(strategyChooser);

    JButton reset = new JButton("Reset");
    reset.setActionCommand("reset");
    reset.addActionListener(this);
    add(reset);
  }

  // -- CacheComponent API methods --

  public Cache getCache() { return cache; }

  // -- ActionListener API methods --

  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();

    if (cmd.equals("reset")) {
      // TODO - reset to reasonable defaults
    }
    else if (cmd.equals("source")) {
      updateSource((JComboBox) e.getSource());
    }
    else if (cmd.equals("strategy")) {
      updateStrategy((JComboBox) e.getSource());
    }
  }

  // -- ChangeListener API methods --

  public void stateChanged(ChangeEvent e) {
    // make sure each axis is chosen only once
    for (int ct=0; ct<priority.length; ct++) {
      for (int col=0; col<priority.length; col++) {
        int chosenNdx = -1;

        for (int row=0; row<priority.length; row++) {
          if (priority[row][col].equals(e.getSource()) &&
            priority[row][col].isSelected())
          {
            chosenNdx = row;
            break;
          }
          else if (priority[row][col].isSelected() && chosenNdx == -1) {
            chosenNdx = row;
          }
        }

        for (int row=0; row<priority.length; row++) {
          if (priority[row][col].isSelected() && row != chosenNdx) {
            priority[row][(col + 1) % priority.length].setSelected(true);
          }
        }
      }
    }

    // reset the cache's priorities
    ICacheStrategy strategy = cache.getStrategy();

    for (int row=0; row<priority.length; row++) {
      for (int col=0; col<priority.length; col++) {
        if (priority[row][col].isSelected()) {
          strategy.setPriority(row, col);
        }
      }
    }

    for (int i=0; i<range.size(); i++) {
      JSpinner r = (JSpinner) range.get(i);
      strategy.setRange(((Integer) r.getValue()).intValue(), i);
    }
  }

  // -- Helper methods --

  private int[] getRange() {
    int[] n = new int[range.size()];
    for (int i=0; i<n.length; i++) {
      n[i] = ((Integer) ((JSpinner) range.get(i)).getValue()).intValue();
    }
    return n;
  }

  private void updateSource(JComboBox box) {
    String s = (String) box.getSelectedItem();

    CacheSource source = null;
    try {
      if (s.equals("byte arrays")) {
        source = new ByteArraySource(file);
      }
      else if (s.equals("BufferedImages")) {
        source = new BufferedImageSource(file);
      }
      else if (s.equals("ImageProcessors")) {
        source = new ImageProcessorSource(file);
      }
      cache.setSource(source);
    }
    catch (CacheException exc) {
      LogTools.trace(exc);
    }
  }

  private void updateStrategy(JComboBox box) {
    String s = (String) box.getSelectedItem();
    CacheStrategy strategy = null;

    boolean forwardFirst = s.indexOf("forward") != -1;

    try {
      int[] lengths = cache.getStrategy().getLengths();
      if (s.startsWith("Crosshair")) {
        strategy = new CrosshairStrategy(lengths);
      }
      else if (s.startsWith("Rectangle")) {
        strategy = new RectangleStrategy(lengths);
      }
      int[] rng = getRange();
      for (int i=0; i<rng.length; i++) strategy.setRange(rng[i], i);
      cache.setStrategy(strategy);
    }
    catch (CacheException exc) {
      LogTools.trace(exc);
    }
  }

}
