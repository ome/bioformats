//
// CacheComponent.java
//

package loci.formats.gui;

import com.jgoodies.forms.layout.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import loci.formats.*;
import loci.formats.cache.*;

/** GUI component for managing a cache. */
public class CacheComponent extends JPanel
  implements ActionListener, CacheListener, ChangeListener
{

  // -- Constants --

  protected static final String[] SOURCES =
    {"Byte arrays", "BufferedImages", "ImageProcessors"};
  protected static final String[] STRATEGIES = {"Crosshair", "Rectangle"};
  protected static final String[] PRIORITIES =
    {"Maximum", "High", "Normal", "Low", "Minimum"};
  protected static final String[] ORDERS = {"Centered", "Forward", "Backward"};

  // -- Fields --

  /** The cache that this component controls. */
  private Cache cache;

  /** Spinners for choosing range of slices to cache. */
  private JSpinner[] range;

  /** Combo boxes for choosing axis priority. */
  private JComboBox[] priority;

  /** Combo boxes for choosing planar ordering. */
  private JComboBox[] order;

  /** File that the cache is working with (debugging only). */
  private String file;

  // -- Constructors --

  public CacheComponent(Cache cache, String[] axisLabels) {
    this(cache, axisLabels, null);
  }

  public CacheComponent(Cache cache, String[] axisLabels, String file) {
    super();
    this.cache = cache;
    this.file = file;

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    CellConstraints cc = new CellConstraints();

    JPanel top = new JPanel();
    FormLayout layout = new FormLayout("pref,3dlu,pref:grow",
      file == null ? "pref:grow" : "pref:grow,3dlu,pref:grow");
    top.setLayout(layout);

    int col = 1, row = 1;

    // add source choices, if desired
    JComboBox sourceChooser = null;
    if (file != null) {
      JLabel label = new JLabel("Objects to cache: ");
      sourceChooser = new JComboBox(SOURCES);
      sourceChooser.setActionCommand("source");
      sourceChooser.addActionListener(this);

      col = 1;
      top.add(label, cc.xy(col, row));
      col += 2;
      top.add(sourceChooser, cc.xy(col, row));
      row += 2;
    }

    // add strategy choices
    JLabel label = new JLabel("Caching strategy: ");
    JComboBox strategyChooser = new JComboBox(STRATEGIES);
    strategyChooser.setActionCommand("strategy");
    strategyChooser.addActionListener(this);

    col = 1;
    top.add(label, cc.xy(col, row));
    col += 2;
    top.add(strategyChooser, cc.xy(col, row));
    row += 2;

    // add cache size choices

    JPanel bottom = new JPanel();
    StringBuffer rows = new StringBuffer();
    rows.append("pref:grow");
    for (int i=0; i<axisLabels.length; i++) rows.append(",3dlu,pref:grow");
    layout = new FormLayout(
      "pref:grow,3dlu,pref:grow,3dlu,pref:grow,3dlu,pref:grow",
      rows.toString());
    bottom.setLayout(layout);

    col = row = 1;
    bottom.add(new JLabel("Axis"), cc.xy(col, row));
    col += 2;
    bottom.add(new JLabel("Range"), cc.xy(col, row));
    col += 2;
    bottom.add(new JLabel("Priority"), cc.xy(col, row));
    col += 2;
    bottom.add(new JLabel("Order"), cc.xy(col, row));
    row += 2;

    int[] lengths = cache.getStrategy().getLengths();

    range = new JSpinner[lengths.length];
    priority = new JComboBox[lengths.length];
    order = new JComboBox[lengths.length];

    for (int i=0; i<axisLabels.length; i++) {
      JLabel l = new JLabel(axisLabels[i]);
      range[i] = new JSpinner(new SpinnerNumberModel(0, 0, lengths[i], 1));
      priority[i] = new JComboBox(PRIORITIES);
      order[i] = new JComboBox(ORDERS);

      col = 1;
      bottom.add(l, cc.xy(col, row));
      col += 2;
      bottom.add(range[i], cc.xy(col, row));
      col += 2;
      bottom.add(priority[i], cc.xy(col, row));
      col += 2;
      bottom.add(order[i], cc.xy(col, row));
      row += 2;

      range[i].addChangeListener(this);
      priority[i].addActionListener(this);
      order[i].addActionListener(this);
    }

    add(top);
    add(Box.createVerticalStrut(9));
    add(bottom);

    cache.addCacheListener(this);
  }

  // -- CacheComponent API methods --

  public Cache getCache() { return cache; }

  public void dispose() {
    cache.removeCacheListener(this);
  }

  // -- ActionListener API methods --

  /** Handles combo box changes. */
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    Object src = e.getSource();

    if ("source".equals(cmd)) updateSource((JComboBox) src);
    else if ("strategy".equals(cmd)) updateStrategy((JComboBox) src);
    else { // priority or order change
      for (int i=0; i<priority.length; i++) {
        if (src == priority[i]) {
          updatePriority(i);
          return;
        }
      }
      for (int i=0; i<order.length; i++) {
        if (src == order[i]) {
          updateOrder(i);
          return;
        }
      }
    }
  }

  // -- CacheListener API methods --

  public void cacheUpdated(CacheEvent e) {
    //TODO
  }

  // -- ChangeListener API methods --

  /** Handles range spinner changes. */
  public void stateChanged(ChangeEvent e) {
    Object src = e.getSource();
    for (int i=0; i<range.length; i++) {
      if (src == range[i]) {
        updateRange(i);
        return;
      }
    }
  }

  // -- Helper methods --

  private void updateSource(JComboBox box) {
    String s = (String) box.getSelectedItem();

    ICacheSource source = null;
    try {
      if (s.equals(SOURCES[0])) { // byte arrays
        if (cache.getSource().getClass() == ByteArraySource.class) return;
        source = new ByteArraySource(file);
      }
      else if (s.equals(SOURCES[1])) { // BufferedImages
        if (cache.getSource().getClass() == BufferedImageSource.class) return;
        source = new BufferedImageSource(file);
      }
      else if (s.equals(SOURCES[2])) { // ImageProcessors
        if (cache.getSource().getClass() == ImageProcessorSource.class) return;
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
    ICacheStrategy strategy = null;

    try {
      int[] lengths = cache.getStrategy().getLengths();
      if (s.equals(STRATEGIES[0])) { // Crosshair
        if (cache.getStrategy().getClass() == CrosshairStrategy.class) return;
        strategy = new CrosshairStrategy(lengths);
      }
      else if (s.equals(STRATEGIES[1])) { // Rectangle
        if (cache.getStrategy().getClass() == RectangleStrategy.class) return;
        strategy = new RectangleStrategy(lengths);
      }
      cache.setStrategy(strategy);
    }
    catch (CacheException exc) {
      LogTools.trace(exc);
    }
  }

  private void updateRange(int index) {
    int rng = ((Integer) range[index].getValue()).intValue();

    ICacheStrategy strategy = cache.getStrategy();
    int[] ranges = strategy.getRange();
    if (rng != ranges[index]) strategy.setRange(rng, index);
  }

  private void updatePriority(int index) {
    String s = (String) priority[index].getSelectedItem();

    int prio = 0;
    if (s.equals(PRIORITIES[0])) { // Maximum
      prio = ICacheStrategy.MAX_PRIORITY;
    }
    else if (s.equals(PRIORITIES[1])) { // High
      prio = ICacheStrategy.HIGH_PRIORITY;
    }
    else if (s.equals(PRIORITIES[2])) { // Normal
      prio = ICacheStrategy.NORMAL_PRIORITY;
    }
    else if (s.equals(PRIORITIES[3])) { // Low
      prio = ICacheStrategy.LOW_PRIORITY;
    }
    else if (s.equals(PRIORITIES[4])) { // Minimum
      prio = ICacheStrategy.MIN_PRIORITY;
    }

    ICacheStrategy strategy = cache.getStrategy();
    int[] priorities = strategy.getPriorities();
    if (prio != priorities[index]) strategy.setPriority(prio, index);
  }

  private void updateOrder(int index) {
    String s = (String) order[index].getSelectedItem();

    int ord = 0;
    if (s.equals(ORDERS[0])) { // Centered
      ord = ICacheStrategy.CENTERED_ORDER;
    }
    else if (s.equals(ORDERS[1])) { // Forward
      ord = ICacheStrategy.FORWARD_ORDER;
    }
    else if (s.equals(ORDERS[2])) { // Backward
      ord = ICacheStrategy.BACKWARD_ORDER;
    }

    ICacheStrategy strategy = cache.getStrategy();
    int[] orders = strategy.getOrder();
    if (ord != orders[index]) strategy.setOrder(ord, index);
  }

}
