/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.formats.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import loci.formats.cache.ByteArraySource;
import loci.formats.cache.Cache;
import loci.formats.cache.CacheEvent;
import loci.formats.cache.CacheException;
import loci.formats.cache.CacheListener;
import loci.formats.cache.CrosshairStrategy;
import loci.formats.cache.ICacheSource;
import loci.formats.cache.ICacheStrategy;
import loci.formats.cache.RectangleStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * GUI component for managing a cache.
 */
public class CacheComponent extends JPanel
  implements ActionListener, CacheListener, ChangeListener
{

  // -- Constants --

  private static final Logger LOGGER =
    LoggerFactory.getLogger(CacheComponent.class);

  protected static final String[] SOURCES = {"Byte arrays", "BufferedImages"};
  protected static final Class[] SOURCE_VALUES = {
    ByteArraySource.class,
    BufferedImageSource.class
  };
  protected static final Class[] SOURCE_PARAMS = {String.class};
  protected static final String[] STRATEGIES = {"Crosshair", "Rectangle"};
  protected static final Class[] STRATEGY_VALUES = {
    CrosshairStrategy.class,
    RectangleStrategy.class
  };
  protected static final Class[] STRATEGY_PARAMS = {int[].class};

  protected static final String[] PRIORITIES =
    {"Maximum", "High", "Normal", "Low", "Minimum"};
  protected static final int[] PRIORITY_VALUES = {
    ICacheStrategy.MAX_PRIORITY, ICacheStrategy.HIGH_PRIORITY,
    ICacheStrategy.NORMAL_PRIORITY, ICacheStrategy.LOW_PRIORITY,
    ICacheStrategy.MIN_PRIORITY
  };
  protected static final String[] ORDERS = {"Centered", "Forward", "Backward"};
  protected static final int[] ORDER_VALUES = {
    ICacheStrategy.CENTERED_ORDER,
    ICacheStrategy.FORWARD_ORDER,
    ICacheStrategy.BACKWARD_ORDER
  };

  // -- Fields --

  /** The cache that this component controls. */
  private Cache cache;

  /** Combo box for choosing cache source. */
  private JComboBox sourceChooser;

  /** Combo box for choosing cache strategy. */
  private JComboBox strategyChooser;

  /** Spinners for choosing range of slices to cache. */
  private JSpinner[] range;

  /** Combo boxes for choosing axis priority. */
  private JComboBox[] priority;

  /** Combo boxes for choosing planar ordering. */
  private JComboBox[] order;

  /** File name that the cache is working with (debugging only). */
  private String id;

  /** Length of each dimensional axis, obtained from cache strategy. */
  private int[] lengths;

  // -- Constructors --

  /** Creates a cache GUI component. */
  public CacheComponent(Cache cache, String[] axisLabels) {
    this(cache, axisLabels, null);
  }

  /**
   * Creates a cache GUI component with the ability to change between the
   * various source types (mainly for debugging purposes).
   */
  public CacheComponent(Cache cache, String[] axisLabels, String id) {
    super();
    this.cache = cache;
    this.id = id;
    lengths = cache.getStrategy().getLengths();

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    CellConstraints cc = new CellConstraints();

    JPanel top = new JPanel();
    FormLayout layout = new FormLayout("pref,3dlu,pref:grow",
      id == null ? "pref:grow" : "pref:grow,3dlu,pref:grow");
    top.setLayout(layout);

    int col = 1, row = 1;

    // add source choices, if desired
    if (id != null) {
      JLabel label = new JLabel("Objects to cache: ");
      sourceChooser = new JComboBox(SOURCES);
      sourceChooser.setSelectedIndex(sourceIndex(cache.getSource()));
      sourceChooser.setActionCommand("source");
      sourceChooser.addActionListener(this);

      col = 1;
      top.add(label, cc.xy(col, row));
      col += 2;
      top.add(sourceChooser, cc.xy(col, row));
      row += 2;
    }

    ICacheStrategy strategy = cache.getStrategy();

    // add strategy choices
    JLabel label = new JLabel("Caching strategy: ");
    strategyChooser = new JComboBox(STRATEGIES);
    strategyChooser.setSelectedIndex(strategyIndex(strategy));
    strategyChooser.setActionCommand("strategy");
    strategyChooser.addActionListener(this);

    col = 1;
    top.add(label, cc.xy(col, row));
    col += 2;
    top.add(strategyChooser, cc.xy(col, row));
    row += 2;

    JPanel bottom = new JPanel();
    StringBuffer rows = new StringBuffer();
    rows.append("pref:grow");
    for (int i=0; i<axisLabels.length; i++) rows.append(",3dlu,pref:grow");
    layout = new FormLayout(
      "pref:grow,3dlu,pref:grow,3dlu,pref:grow,3dlu,pref:grow",
      rows.toString());
    bottom.setLayout(layout);

    // add strategy parameter choices
    col = row = 1;
    bottom.add(new JLabel("Axis"), cc.xy(col, row));
    col += 2;
    bottom.add(new JLabel("Range"), cc.xy(col, row));
    col += 2;
    bottom.add(new JLabel("Priority"), cc.xy(col, row));
    col += 2;
    bottom.add(new JLabel("Order"), cc.xy(col, row));
    row += 2;

    range = new JSpinner[lengths.length];
    priority = new JComboBox[lengths.length];
    order = new JComboBox[lengths.length];

    int[] rng = strategy.getRange();
    int[] prio = strategy.getPriorities();
    int[] ord = strategy.getOrder();
    for (int i=0; i<axisLabels.length; i++) {
      JLabel l = new JLabel(axisLabels[i]);
      range[i] = new JSpinner(new SpinnerNumberModel(rng[i], 0, lengths[i], 1));
      priority[i] = new JComboBox(PRIORITIES);
      priority[i].setSelectedIndex(priorityIndex(prio[i]));
      order[i] = new JComboBox(ORDERS);
      order[i].setSelectedIndex(orderIndex(ord[i]));

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
  @Override
  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if ("source".equals(cmd)) updateSource();
    else if ("strategy".equals(cmd)) updateStrategy();
    else { // priority or order change
      Object src = e.getSource();
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

  /** Updates GUI to match latest cache state. */
  @Override
  public void cacheUpdated(CacheEvent e) {
    int type = e.getType();
    ICacheStrategy strategy = cache.getStrategy();
    switch (type) {
      case CacheEvent.SOURCE_CHANGED:
        sourceChooser.removeActionListener(this);
        sourceChooser.setSelectedIndex(sourceIndex(cache.getSource()));
        sourceChooser.addActionListener(this);
        break;
      case CacheEvent.STRATEGY_CHANGED:
        strategyChooser.removeActionListener(this);
        strategyChooser.setSelectedIndex(strategyIndex(strategy));
        strategyChooser.addActionListener(this);
        break;
      case CacheEvent.PRIORITIES_CHANGED:
        int[] prio = strategy.getPriorities();
        for (int i=0; i<prio.length; i++) {
          priority[i].removeActionListener(this);
          priority[i].setSelectedIndex(priorityIndex(prio[i]));
          priority[i].addActionListener(this);
        }
        break;
      case CacheEvent.ORDER_CHANGED:
        int[] ord = strategy.getOrder();
        for (int i=0; i<ord.length; i++) {
          order[i].removeActionListener(this);
          order[i].setSelectedIndex(orderIndex(ord[i]));
          order[i].addActionListener(this);
        }
        break;
      case CacheEvent.RANGE_CHANGED:
        int[] rng = strategy.getRange();
        for (int i=0; i<rng.length; i++) {
          range[i].removeChangeListener(this);
          range[i].setValue(new Integer(rng[i]));
          range[i].addChangeListener(this);
        }
        break;
    }
  }

  // -- ChangeListener API methods --

  /** Handles range spinner changes. */
  @Override
  public void stateChanged(ChangeEvent e) {
    Object src = e.getSource();
    for (int i=0; i<range.length; i++) {
      if (src == range[i]) {
        updateRange(i);
        return;
      }
    }
  }

  // -- Helper methods - GUI component update --

  /** Updates cache source to match the state of the GUI. */
  private void updateSource() {
    try {
      ICacheSource source = sourceValue(sourceChooser.getSelectedIndex());
      if (source != null) cache.setSource(source);
    }
    catch (CacheException exc) { LOGGER.info("", exc); }
  }

  /** Updates cache strategy to match the state of the GUI. */
  private void updateStrategy() {
    try {
      ICacheStrategy strategy =
        strategyValue(strategyChooser.getSelectedIndex());
      if (strategy != null) cache.setStrategy(strategy);
    }
    catch (CacheException exc) { LOGGER.info("", exc); }
  }

  /** Updates cache range to match the state of the GUI. */
  private void updateRange(int index) {
    int rng = ((Integer) range[index].getValue()).intValue();

    ICacheStrategy strategy = cache.getStrategy();
    int[] ranges = strategy.getRange();
    if (rng != ranges[index]) strategy.setRange(rng, index);
  }

  /** Updates cache priority to match the state of the GUI. */
  private void updatePriority(int index) {
    int prio = priorityValue(priority[index].getSelectedIndex());
    ICacheStrategy strategy = cache.getStrategy();
    int[] priorities = strategy.getPriorities();
    if (prio != priorities[index]) strategy.setPriority(prio, index);
  }

  /** Updates cache order to match the state of the GUI. */
  private void updateOrder(int index) {
    int ord = orderValue(order[index].getSelectedIndex());
    ICacheStrategy strategy = cache.getStrategy();
    int[] orders = strategy.getOrder();
    if (ord != orders[index]) strategy.setOrder(ord, index);
  }

  // -- Helper methods - data conversion --

  /** Converts cache source to source chooser index. */
  private int sourceIndex(ICacheSource s) {
    Class c = s.getClass();
    for (int i=0; i<SOURCE_VALUES.length; i++) {
      if (SOURCE_VALUES[i] == c) return i;
    }
    return -1;
  }

  /** Generates a new cache source matching the source chooser index. */
  private ICacheSource sourceValue(int index) {
    Class c = SOURCE_VALUES[index];
    if (c == cache.getSource().getClass()) return null;
    try {
      Constructor con = c.getConstructor(SOURCE_PARAMS);
      return (ICacheSource) con.newInstance(new Object[] {id});
    }
    catch (NoSuchMethodException exc) { LOGGER.info("", exc); }
    catch (InstantiationException exc) { LOGGER.info("", exc); }
    catch (IllegalAccessException exc) { LOGGER.info("", exc); }
    catch (IllegalArgumentException exc) { LOGGER.info("", exc); }
    catch (InvocationTargetException exc) { LOGGER.info("", exc); }
    return null;
  }

  /** Converts cache strategy to strategy chooser index. */
  private int strategyIndex(ICacheStrategy s) {
    Class c = s.getClass();
    for (int i=0; i<STRATEGY_VALUES.length; i++) {
      if (STRATEGY_VALUES[i] == c) return i;
    }
    return -1;
  }

  /** Generates a new cache strategy matching the strategy chooser index. */
  private ICacheStrategy strategyValue(int index) {
    Class c = STRATEGY_VALUES[index];
    if (c == cache.getStrategy().getClass()) return null;
    try {
      Constructor con = c.getConstructor(STRATEGY_PARAMS);
      return (ICacheStrategy) con.newInstance(new Object[] {lengths});
    }
    catch (NoSuchMethodException exc) { LOGGER.info("", exc); }
    catch (InstantiationException exc) { LOGGER.info("", exc); }
    catch (IllegalAccessException exc) { LOGGER.info("", exc); }
    catch (IllegalArgumentException exc) { LOGGER.info("", exc); }
    catch (InvocationTargetException exc) { LOGGER.info("", exc); }
    return null;
  }

  /** Converts enumerated priority value to priority chooser index. */
  private int priorityIndex(int prio) {
    for (int i=0; i<PRIORITY_VALUES.length; i++) {
      if (PRIORITY_VALUES[i] == prio) return i;
    }
    return -1;
  }

  /** Converts priority chooser index to enumerated priority value. */
  private int priorityValue(int index) { return PRIORITY_VALUES[index]; }

  /** Converts enumerated order value to order chooser index. */
  private int orderIndex(int ord) {
    for (int i=0; i<ORDER_VALUES.length; i++) {
      if (ORDER_VALUES[i] == ord) return i;
    }
    return -1;
  }

  /** Converts order chooser index to enumerated order value. */
  private int orderValue(int index) { return ORDER_VALUES[index]; }

}
