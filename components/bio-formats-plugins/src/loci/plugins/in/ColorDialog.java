/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2015 Open Microscopy Environment:
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

import ij.gui.GenericDialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.util.ArrayList;
import java.util.List;

import loci.plugins.util.ImageProcessorReader;
import loci.plugins.util.WindowTools;

/**
 * Bio-Formats Importer custom color chooser dialog box.
 *
 * Heavily adapted from {@link ij.gui.ColorChooser}.
 * ColorChooser is not used because there is no way to change the slider
 * labels&mdash;this means that we can't record macros in which custom colors
 * are chosen for multiple channels.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class ColorDialog extends ImporterDialog {

  // -- Constants --

  private static final Dimension SWATCH_SIZE = new Dimension(100, 50);

  // -- Constructor --

  public ColorDialog(ImportProcess process) {
    super(process);
  }

  // -- ImporterDialog methods --

  @Override
  protected boolean needPrompt() {
    return !process.isWindowless() && options.isColorModeCustom();
  }

  @Override
  protected GenericDialog constructDialog() {
    GenericDialog gd = new GenericDialog("Bio-Formats Custom Colorization");

    // CTR FIXME - avoid problem with MAX_SLIDERS in GenericDialog
    final ImageProcessorReader reader = process.getReader();
    final List<Panel> swatches = new ArrayList<Panel>();
    for (int s=0; s<process.getSeriesCount(); s++) {
      if (!options.isSeriesOn(s)) continue;
      reader.setSeries(s);
      for (int c=0; c<reader.getSizeC(); c++) {
        Color color = options.getCustomColor(s, c);
        if (color == null) color = options.getDefaultCustomColor(c);
        gd.addSlider(makeLabel("Red:", s, c), 0, 255, color.getRed());
        gd.addSlider(makeLabel("Green:", s, c), 0, 255, color.getGreen());
        gd.addSlider(makeLabel("Blue:", s, c), 0, 255, color.getBlue());

        Panel swatch = createSwatch(color);
        gd.addPanel(swatch, GridBagConstraints.CENTER, new Insets(5, 0, 5, 0));
        swatches.add(swatch);
      }
    }

    // change swatch colors when sliders move
    List<TextField> colorFields = getNumericFields(gd);
    attachListeners(colorFields, swatches);

    WindowTools.addScrollBars(gd);

    return gd;
  }

  @Override
  protected boolean harvestResults(GenericDialog gd) {
    final ImageProcessorReader reader = process.getReader();
    for (int s=0; s<process.getSeriesCount(); s++) {
      if (!options.isSeriesOn(s)) continue;
      reader.setSeries(s);
      for (int c=0; c<reader.getSizeC(); c++) {
        int red = (int) gd.getNextNumber();
        int green = (int) gd.getNextNumber();
        int blue = (int) gd.getNextNumber();
        Color color = new Color(red, green, blue);
        options.setCustomColor(s, c, color);
      }
    }
    return true;
  }

  // -- Helper methods --

  private String makeLabel(String baseLabel, int s, int c) {
    return "Series_" + s + "_Channel_" + c + "_" + baseLabel;
  }

  private Panel createSwatch(Color color) {
    Panel swatch = new Panel();
    swatch.setPreferredSize(SWATCH_SIZE);
    swatch.setMinimumSize(SWATCH_SIZE);
    swatch.setMaximumSize(SWATCH_SIZE);
    swatch.setBackground(color);
    return swatch;
  }

  private void attachListeners(List<TextField> colors, List<Panel> swatches) {
    int colorIndex = 0, swatchIndex = 0;
    while (colorIndex < colors.size()) {
      final TextField redField = colors.get(colorIndex++);
      final TextField greenField = colors.get(colorIndex++);
      final TextField blueField = colors.get(colorIndex++);
      final Panel swatch = swatches.get(swatchIndex++);
      TextListener textListener = new TextListener() {
        @Override
        public void textValueChanged(TextEvent e) {
          int red = getColorValue(redField);
          int green = getColorValue(greenField);
          int blue = getColorValue(blueField);
          swatch.setBackground(new Color(red, green, blue));
          swatch.repaint();
        }
      };
      redField.addTextListener(textListener);
      greenField.addTextListener(textListener);
      blueField.addTextListener(textListener);
    }
  }

  private int getColorValue(TextField colorField) {
    int color = 0;
    try {
      color = Integer.parseInt(colorField.getText());
    }
    catch (NumberFormatException exc) { }
    if (color < 0) color = 0;
    if (color > 255) color = 255;
    return color;
  }

  @SuppressWarnings("unchecked")
  private List<TextField> getNumericFields(GenericDialog gd) {
    return gd.getNumericFields();
  }

}
