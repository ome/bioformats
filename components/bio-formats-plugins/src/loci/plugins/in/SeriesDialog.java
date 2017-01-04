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

package loci.plugins.in;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import ij.Macro;
import ij.gui.GenericDialog;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JPanel;

import loci.formats.gui.BufferedImageReader;
import loci.plugins.util.WindowTools;

/**
 * Bio-Formats Importer series chooser dialog box.
 */
public class SeriesDialog extends ImporterDialog implements ActionListener {

  // -- Constants --

  public static final int MAX_COMPONENTS = 256;
  public static final int MAX_SERIES_THUMBS = 200;
  public static final int MAX_SERIES_TOGGLES = MAX_SERIES_THUMBS;

  // -- Fields --

  private BufferedImageReader thumbReader;
  private Panel[] p;
  private Checkbox[] boxes;

  // -- Constructor --

  /** Creates a series chooser dialog for the Bio-Formats Importer. */
  public SeriesDialog(ImportProcess process) {
    super(process);
  }

  // -- ImporterDialog methods --

  @Override
  protected boolean needPrompt() {
    return !process.isWindowless() && process.getSeriesCount() > 1 &&
      !options.openAllSeries() && !options.isViewNone();
  }

  @Override
  protected GenericDialog constructDialog() {
    final int seriesCount = process.getSeriesCount();

    // NB: Load thumbnails only when series count is modest.
    if (seriesCount < MAX_SERIES_THUMBS) {
      // construct thumbnail reader
      thumbReader = new BufferedImageReader(process.getReader());

      // set up the thumbnail panels
      p = new Panel[seriesCount];
      for (int i=0; i<seriesCount; i++) {
        thumbReader.setSeries(i);
        int sx = thumbReader.getThumbSizeX() + 10; // a little extra padding
        int sy = thumbReader.getThumbSizeY();
        p[i] = new Panel();
        p[i].add(Box.createRigidArea(new Dimension(sx, sy)));
        if (options.isForceThumbnails()) {
          // load thumbnail immediately
          ThumbLoader.loadThumb(thumbReader, i, p[i], options.isQuiet());
        }
      }
    }

    GenericDialog gd = new GenericDialog("Bio-Formats Series Options");

    // NB: Provide individual checkboxes only when series count is manageable.
    if (seriesCount < MAX_SERIES_TOGGLES) {
      // NB: We need to add the checkboxes in groups, to prevent an
      // exception from being thrown if there are more than 512 series.
      // See also:
      //   http://dev.loci.wisc.edu/trac/java/ticket/408 and
      //   http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5107980

      final int nGroups = (seriesCount + MAX_COMPONENTS - 1) / MAX_COMPONENTS;
      int nextSeries = 0;
      for (int i=0; i<nGroups; i++) {
        final int nRows = Math.min(MAX_COMPONENTS, seriesCount - nextSeries);
        final String[] labels = new String[nRows];
        final boolean[] defaultValues = new boolean[nRows];
        for (int row=0; row<nRows; row++) {
          labels[row] = process.getSeriesLabel(nextSeries);
          defaultValues[row] = options.isSeriesOn(nextSeries);
          nextSeries++;
        }
        gd.addCheckboxGroup(nRows, 1, labels, defaultValues);
      }

      // extract checkboxes, for "Select All" and "Deselect All" functions
      boxes = WindowTools.getCheckboxes(gd).toArray(new Checkbox[0]);

      // rebuild dialog so that the thumbnails and checkboxes line up correctly
      rebuildDialog(gd, nGroups);
    }
    else {
      // too many series; display a simple text field for specifying series
      gd.addMessage(
        "Please specify the image series you wish to import.\n" +
        "Use commas to list multiple series. You can also use\n" +
        "a dash to represent a range of series. For example,\n" +
        "to import series 1, 3, 4, 5, 7, 8, 9, 12, 15 & 16,\n" +
        "you could write: 1, 3-5, 7-9, 12, 15-16\n \n" +
        "There are " + seriesCount + " total series.");
      gd.addStringField("Series_list: ", "1");
    }

    return gd;
  }

  @Override
  protected boolean displayDialog(GenericDialog gd) {
    ThumbLoader loader = null;
    if (thumbReader != null && !options.isForceThumbnails() && Macro.getOptions() == null) {
      // spawn background thumbnail loader
      loader = new ThumbLoader(thumbReader, p, gd);
    }
    gd.showDialog();
    if (loader != null) loader.stop();
    return !gd.wasCanceled();
  }

  @Override
  protected boolean harvestResults(GenericDialog gd) {
    final int seriesCount = process.getSeriesCount();
    options.clearSeries();

    // examine series key regardless of number of series
    final String macroOptions = Macro.getOptions();
    final String macroSeriesList = macroOptions == null ? null :
      Macro.getValue(macroOptions, "series_list", null);
    if (macroSeriesList != null) process.setSeriesList(macroSeriesList);

    if (seriesCount < MAX_SERIES_TOGGLES) {
      // harvest individual checkbox values
      for (int i=0; i<seriesCount; i++) {
        final boolean on = gd.getNextBoolean();
        if (on) options.setSeriesOn(i, on);
      }
    }
    else {
      // harvest series string
      final String seriesList = gd.getNextString();
      process.setSeriesList(seriesList);
    }

    // examine series_XX keys regardless of number of series
    if (macroOptions != null) {
      for (int i=0; i<seriesCount; i++) {
        final String seriesKey = "series_" + (i + 1);
        final boolean on = options.checkKey(macroOptions, seriesKey);
        if (on) options.setSeriesOn(i, on);
      }
    }

    return true;
  }

  // -- ActionListener methods --

  @Override
  public void actionPerformed(ActionEvent e) {
    final String cmd = e.getActionCommand();
    if ("select".equals(cmd)) {
      for (int i=0; i<boxes.length; i++) boxes[i].setState(true);
      updateIfGlitched();
    }
    else if ("deselect".equals(cmd)) {
      for (int i=0; i<boxes.length; i++) boxes[i].setState(false);
      updateIfGlitched();
    }
  }

  // -- Helper methods --

  private void updateIfGlitched() {
    if (IS_GLITCHED) {
      // HACK - work around for Mac OS X AWT bug
      sleep(200);
      for (int i=0; i<boxes.length; i++) boxes[i].repaint();
    }
  }

  private void rebuildDialog(GenericDialog gd, int buttonRow) {
    // rebuild dialog to organize things more nicely

    final String cols = p == null ? "pref" : "pref, 3dlu, pref";

    final StringBuilder sb = new StringBuilder("pref");
    for (int s=1; s<boxes.length; s++) sb.append(", 3dlu, pref");
    final String rows = sb.toString();

    final PanelBuilder builder = new PanelBuilder(new FormLayout(cols, rows));
    final CellConstraints cc = new CellConstraints();

    int row = 1;
    for (int s=0; s<boxes.length; s++) {
      builder.add(boxes[s], cc.xy(1, row));
      if (p != null) builder.add(p[s], cc.xy(3, row));
      row += 2;
    }

    final JPanel masterPanel = builder.getPanel();

    gd.removeAll();

    GridBagLayout gdl = (GridBagLayout) gd.getLayout();
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gdl.setConstraints(masterPanel, gbc);

    gd.add(masterPanel);

    WindowTools.addScrollBars(gd);
    gd.setBackground(Color.white); // HACK: workaround for JPanel in a Dialog

    // add Select All and Deselect All buttons

    Panel buttons = new Panel();

    Button select = new Button("Select All");
    select.setActionCommand("select");
    select.addActionListener(this);
    Button deselect = new Button("Deselect All");
    deselect.setActionCommand("deselect");
    deselect.addActionListener(this);

    buttons.add(select);
    buttons.add(deselect);

    gbc.gridx = 2;
    gbc.gridy = buttonRow;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.insets = new Insets(15, 0, 0, 0);
    gdl.setConstraints(buttons, gbc);
    gd.add(buttons);
  }

}
