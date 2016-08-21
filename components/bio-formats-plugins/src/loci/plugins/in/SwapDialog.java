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

import java.awt.Choice;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import loci.plugins.util.ImageProcessorReader;
import loci.plugins.util.WindowTools;

/**
 * Bio-Formats Importer dimension swapper dialog box.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats-plugins/src/loci/plugins/in/SwapDialog.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats-plugins/src/loci/plugins/in/SwapDialog.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class SwapDialog extends ImporterDialog implements ItemListener {

  // -- Fields --

  private Choice zChoice, cChoice, tChoice;

  // -- Constructor --

  /** Creates a dimension swapper dialog for the Bio-Formats Importer. */
  public SwapDialog(ImportProcess process) {
    super(process);
  }

  // -- ImporterDialog methods --

  @Override
  protected boolean needPrompt() {
    return !process.isWindowless() && options.isSwapDimensions();
  }

  @Override
  protected GenericDialog constructDialog() {
    ImageProcessorReader reader = process.getReader();
    int seriesCount = process.getSeriesCount();

    GenericDialog gd = new GenericDialog("Dimension swapping options");

    String[] labels = {"Z", "C", "T"};
    int[] sizes = new int[] {
      reader.getSizeZ(), reader.getSizeC(), reader.getSizeT()
    };
    for (int s=0; s<seriesCount; s++) {
      if (!options.isSeriesOn(s)) continue;
      reader.setSeries(s);

      gd.addMessage("Series " + (s + 1) + ":\n");

      for (int i=0; i<labels.length; i++) {
        gd.addChoice(sizes[i] + "_planes", labels, labels[i]);
      }
    }

    List<Choice> choices = WindowTools.getChoices(gd);
    zChoice = choices.get(0);
    cChoice = choices.get(1);
    tChoice = choices.get(2);
    zChoice.addItemListener(this);
    cChoice.addItemListener(this);
    tChoice.addItemListener(this);

    WindowTools.addScrollBars(gd);

    return gd;
  }

  @Override
  protected boolean harvestResults(GenericDialog gd) {
    ImageProcessorReader reader = process.getReader();
    int seriesCount = process.getSeriesCount();

    for (int s=0; s<seriesCount; s++) {
      if (!options.isSeriesOn(s)) continue;
      reader.setSeries(s);
      String z = gd.getNextChoice();
      String c = gd.getNextChoice();
      String t = gd.getNextChoice();

      if (z.equals(t) || z.equals(c) || c.equals(t)) {
        // should never occur... ;-)
        throw new IllegalStateException(
          "Invalid swapping options - each axis can be used only once.");
      }

      String originalOrder = reader.getDimensionOrder();
      StringBuffer sb = new StringBuffer();
      sb.append("XY");
      for (int i=2; i<originalOrder.length(); i++) {
        if (originalOrder.charAt(i) == 'Z') sb.append(z);
        else if (originalOrder.charAt(i) == 'C') sb.append(c);
        else if (originalOrder.charAt(i) == 'T') sb.append(t);
      }

      options.setInputOrder(s, sb.toString());
    }
    return true;
  }

  // -- ItemListener methods --

  public void itemStateChanged(ItemEvent e) {
    final Object src = e.getSource();
    final int zIndex = zChoice.getSelectedIndex();
    final int cIndex = cChoice.getSelectedIndex();
    final int tIndex = tChoice.getSelectedIndex();
    if (src == zChoice) {
      if (zIndex == cIndex) cChoice.select(firstAvailable(zIndex, tIndex));
      else if (zIndex == tIndex) tChoice.select(firstAvailable(zIndex, cIndex));
    }
    else if (src == cChoice) {
      if (cIndex == zIndex) zChoice.select(firstAvailable(cIndex, tIndex));
      else if (cIndex == tIndex) tChoice.select(firstAvailable(zIndex, cIndex));
    }
    else if (src == tChoice) {
      if (tIndex == zIndex) zChoice.select(firstAvailable(cIndex, tIndex));
      else if (tIndex == cIndex) cChoice.select(firstAvailable(zIndex, tIndex));
    }
  }

  // -- Helper methods --

  private int firstAvailable(int... index) {
    final int minValue = 0, maxValue = 2;
    for (int v=minValue; v<=maxValue; v++) {
      boolean taken = false;
      for (int i : index) {
        if (v == i) {
          taken = true;
          break;
        }
      }
      if (!taken) return v;
    }
    return -1;
  }

}
