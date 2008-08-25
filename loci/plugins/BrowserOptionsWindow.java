//
// BrowserOptionsWindow.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
Data Browser, Stack Colorizer and Stack Slicer. Copyright (C) 2005-@year@
Melissa Linkert, Curtis Rueden and Christopher Peterson.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.plugins;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import loci.formats.cache.*;

/**
 * Extension of JFrame that allows the user to adjust caching settings.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/plugins/BrowserOptionsWindow.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/plugins/BrowserOptionsWindow.java">SVN</a></dd></dl>
 */
public class BrowserOptionsWindow extends JFrame
  implements ActionListener, ChangeListener
{

  // -- Fields --

  private Cache cache;

  private JSpinner cSpinner, zSpinner, tSpinner;

  // -- Constructor --

  public BrowserOptionsWindow(String title, Cache cache) {
    super(title);
    this.cache = cache;

    // construct the range spinners

    int[] range = cache.getStrategy().getRange();
    int[] lengths = cache.getStrategy().getLengths();

    cSpinner = new JSpinner(new SpinnerNumberModel(range[0], 0, lengths[0], 1));
    zSpinner = new JSpinner(new SpinnerNumberModel(range[1], 0, lengths[1], 1));
    tSpinner = new JSpinner(new SpinnerNumberModel(range[2], 0, lengths[2], 1));
    cSpinner.addChangeListener(this);
    zSpinner.addChangeListener(this);
    tSpinner.addChangeListener(this);

    // construct the radio buttons to select axis priority

    String[] priorities = new String[] {
      "ZCT", "ZTC", "CZT", "CTZ", "TCZ", "TZC"
    };
    JRadioButton[] priorityButtons = new JRadioButton[priorities.length];
    ButtonGroup priorityGroup = new ButtonGroup();

    for (int i=0; i<priorityButtons.length; i++) {
      priorityButtons[i] = new JRadioButton(priorities[i]);
      priorityButtons[i].setActionCommand(priorities[i]);
      priorityButtons[i].addActionListener(this);
      priorityGroup.add(priorityButtons[i]);
    }

    // construct the radio buttons to select caching strategy

    ButtonGroup strategyGroup = new ButtonGroup();

    JRadioButton crosshairStrategy = new JRadioButton("Crosshair",
      cache.getStrategy() instanceof CrosshairStrategy);
    crosshairStrategy.setActionCommand("crosshair");
    crosshairStrategy.addActionListener(this);
    strategyGroup.add(crosshairStrategy);

    JRadioButton rectangleStrategy = new JRadioButton("Rectangle",
      cache.getStrategy() instanceof RectangleStrategy);
    rectangleStrategy.setActionCommand("rectangle");
    rectangleStrategy.addActionListener(this);
    strategyGroup.add(rectangleStrategy);

    // construct the radio buttons to select caching order

    // add everything to the window

    JPanel panel = new JPanel();

    JLabel rangeHeader =
      new JLabel("Number of planes to cache along each axis");
    JLabel priorityHeader = new JLabel("Order in which to cache planes");
    JLabel strategyHeader = new JLabel("Caching strategy");
    JLabel zLabel = new JLabel("Z: ");
    JLabel cLabel = new JLabel("C: ");
    JLabel tLabel = new JLabel("T: ");

    JButton ok = new JButton("OK");
    ok.setActionCommand("ok");
    ok.addActionListener(this);

    GridLayout layout = new GridLayout(15, 2);
    panel.setLayout(layout);

    panel.add(rangeHeader);
    panel.add(new JLabel("")); // spacer

    panel.add(zLabel);
    panel.add(zSpinner);

    panel.add(cLabel);
    panel.add(cSpinner);

    panel.add(tLabel);
    panel.add(tSpinner);

    panel.add(priorityHeader);
    panel.add(new JLabel("")); // spacer
    for (int i=0; i<priorityButtons.length; i++) {
      panel.add(priorityButtons[i]);
      panel.add(new JLabel("")); // spacer
    }

    panel.add(strategyHeader);
    panel.add(new JLabel("")); // spacer

    panel.add(crosshairStrategy);
    panel.add(new JLabel("")); // spacer

    panel.add(rectangleStrategy);
    panel.add(new JLabel("")); // spacer

    panel.add(new JLabel("")); // spacer
    panel.add(ok);

    panel.setMinimumSize(new Dimension(300, 500));
    setContentPane(panel);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    pack();
  }

  // -- ActionListener API methods --

  public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();
    if (command == null) return;

    ICacheStrategy strategy = cache.getStrategy();

    if (command.equals("crosshair")) {
      // switch to crosshair caching strategy
      if (strategy instanceof CrosshairStrategy) return;

      int[] lengths = strategy.getLengths();
      int[] priorities = strategy.getPriorities();
      int[] order = strategy.getOrder();
      int[] range = strategy.getRange();

      strategy = new CrosshairStrategy(lengths);
      for (int i=0; i<lengths.length; i++) {
        strategy.setPriority(priorities[i], i);
        strategy.setOrder(order[i], i);
        strategy.setRange(range[i], i);
      }
    }
    else if (command.equals("rectangle")) {
      // switch to rectangle caching strategy
      if (strategy instanceof RectangleStrategy) return;

      int[] lengths = strategy.getLengths();
      int[] priorities = strategy.getPriorities();
      int[] order = strategy.getOrder();
      int[] range = strategy.getRange();

      strategy = new RectangleStrategy(lengths);
      for (int i=0; i<lengths.length; i++) {
        strategy.setPriority(priorities[i], i);
        strategy.setOrder(order[i], i);
        strategy.setRange(range[i], i);
      }
    }
    else if (command.equals("ok")) {
      dispose();
    }
    else {
      // adjust axis priorities
      int cPriority = getPriority(command.indexOf("C"));
      int zPriority = getPriority(command.indexOf("Z"));
      int tPriority = getPriority(command.indexOf("T"));

      strategy.setPriority(cPriority, 0);
      strategy.setPriority(zPriority, 1);
      strategy.setPriority(tPriority, 2);
    }
    try {
      cache.setStrategy(strategy);
    }
    catch (CacheException exc) {
      exc.printStackTrace();
    }
  }

  // -- ChangeListener API methods --

  public void stateChanged(ChangeEvent e) {
    Object source = e.getSource();
    if (source.equals(cSpinner)) {
      int value = ((Integer) cSpinner.getValue()).intValue();
      cache.getStrategy().setRange(value, 0);
    }
    else if (source.equals(zSpinner)) {
      int value = ((Integer) zSpinner.getValue()).intValue();
      cache.getStrategy().setRange(value, 1);
    }
    else if (source.equals(tSpinner)) {
      int value = ((Integer) tSpinner.getValue()).intValue();
      cache.getStrategy().setRange(value, 2);
    }
  }

  // -- Helper method --

  private int getPriority(int relativePriority) {
    switch (relativePriority) {
      case 0:
        return ICacheStrategy.MAX_PRIORITY;
      case 1:
        return ICacheStrategy.NORMAL_PRIORITY;
      case 2:
        return ICacheStrategy.MIN_PRIORITY;
    }
    return ICacheStrategy.MIN_PRIORITY;
  }

}
