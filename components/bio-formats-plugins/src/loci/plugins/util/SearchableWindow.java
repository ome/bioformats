/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.plugins.util;

import ij.text.TextPanel;
import ij.text.TextWindow;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Scrollbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Text panel with search capabilities.
 */
public class SearchableWindow extends TextWindow {

  protected TextPanel panel;
  protected int index;

  // -- Constructor --

  public SearchableWindow(String title, String headings, String data,
    int w, int h)
  {
    super(title, headings, data, w, h);
    index = -1;
    panel = getTextPanel();

    MenuBar menubar = getMenuBar();
    Menu menu = menubar.getMenu(0);
    MenuItem search = new MenuItem("Search...");
    search.setActionCommand("search");
    search.addActionListener(this);
    menu.add(search);
  }

  // -- ActionListener API methods --

  @Override
  public void actionPerformed(ActionEvent e) {
    if ("search".equals(e.getActionCommand())) {
      new SearchBox(this);
    }
    else panel.actionPerformed(e);
  }

  // -- SearchableWindow API methods --

  public void selectLine(int index) {
    //int ys = panel.getFontMetrics(getFont()).getHeight() + 2;
    //int y = ys * (index + 1) + 2;  // absolute y coordinate
    //int totalHeight = ys * panel.getLineCount();

    Scrollbar ss = null;
    Component[] components = panel.getComponents();
    for (int i=0; i<components.length; i++) {
      if (components[i] instanceof Scrollbar) {
        Scrollbar s = (Scrollbar) components[i];
        if (s.getOrientation() == Scrollbar.VERTICAL) {
          ss = s;
        }
      }
    }

    //int height = panel.getHeight();

    // convert absolute y value to scrollbar and relative y coordinates
    int min = ss.getMinimum();
    int scrollValue = min + index;
    ss.setValue(scrollValue);
    panel.adjustmentValueChanged(null);
  }

  // -- Helper class --

  class SearchBox extends JDialog implements ActionListener, ChangeListener {
    private JTextField searchBox;
    private JCheckBox ignore;
    private boolean ignoreCase;
    private SearchableWindow searchPane;

    public SearchBox(SearchableWindow searchPane) {
      setTitle("Search...");
      this.searchPane = searchPane;
      FormLayout layout = new FormLayout("pref,pref:grow,pref,pref:grow,pref",
        "pref,pref:grow,pref,pref:grow,pref,pref:grow,pref");
      JPanel panel = new JPanel(layout);
      CellConstraints cc = new CellConstraints();

      searchBox = new JTextField();

      ignore = new JCheckBox("Ignore Case", false);
      ignore.addChangeListener(this);
      JButton next = new JButton("Find Next");
      next.setActionCommand("next");
      next.addActionListener(this);
      JButton previous = new JButton("Find Previous");
      previous.setActionCommand("previous");
      previous.addActionListener(this);
      JButton cancel = new JButton("Cancel");
      cancel.setActionCommand("cancel");
      cancel.addActionListener(this);

      panel.add(searchBox, cc.xy(2, 2));
      panel.add(ignore, cc.xy(2, 4));
      panel.add(next, cc.xy(4, 2));
      panel.add(previous, cc.xy(4, 4));
      panel.add(cancel, cc.xywh(2, 6, 3, 1));

      panel.setSize(new Dimension(350, 200));
      setContentPane(panel);
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      setSize(new Dimension(350, 200));
      setVisible(true);
    }

    // -- ActionListener API methods --

    @Override
    public void actionPerformed(ActionEvent e) {
      String cmd = e.getActionCommand();
      if (cmd.equals("next")) {
        searchPane.panel.resetSelection();
        String text = searchBox.getText();
        boolean found = false;
        int original = searchPane.index;
        while (!found) {
          searchPane.index++;
          if (searchPane.index >= searchPane.panel.getLineCount()) {
            searchPane.index = 0;
          }
          if (searchPane.index == original) return;
          String line = searchPane.panel.getLine(searchPane.index);
          found = ignoreCase ? line.toLowerCase().indexOf(
            text.toLowerCase()) >= 0 : line.indexOf(text) >= 0;
        }
        searchPane.selectLine(searchPane.index);
      }
      else if (cmd.equals("previous")) {
        searchPane.panel.resetSelection();
        String text = searchBox.getText();
        boolean found = false;
        int original = searchPane.index;
        while (!found) {
          searchPane.index--;
          if (searchPane.index < 0) {
            searchPane.index = searchPane.panel.getLineCount() - 1;
          }
          if (searchPane.index == original) return;
          String line = searchPane.panel.getLine(searchPane.index);
          found = ignoreCase ? line.toLowerCase().indexOf(
            text.toLowerCase()) >= 0 : line.indexOf(text) >= 0;
        }
        searchPane.selectLine(searchPane.index);
      }
      else if (cmd.equals("cancel")) {
        dispose();
      }
    }

    // -- ChangeListener API methods --

    @Override
    public void stateChanged(ChangeEvent e) {
      if (e.getSource().equals(ignore)) {
        ignoreCase = ignore.isSelected();
      }
    }

  }

}
