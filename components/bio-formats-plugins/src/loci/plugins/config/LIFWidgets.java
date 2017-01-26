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

package loci.plugins.config;

import ij.Prefs;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;

import loci.formats.in.LIFReader;

import loci.plugins.util.LociPrefs;

/**
 * Custom widgets for configuring Bio-Formats Leica LIF support.
 *
 */
public class LIFWidgets implements IFormatWidgets, ItemListener {

  // -- Fields --

  private String[] labels;
  private Component[] widgets;

  // -- Constructor --

  public LIFWidgets() {
    boolean physicalSizeBackwardsCompatibility =
      Prefs.get(LociPrefs.PREF_LEICA_LIF_PHYSICAL_SIZE, LIFReader.OLD_PHYSICAL_SIZE_DEFAULT);

    String physicalSizeLabel = "Physical size";
    JCheckBox physicalSizeBox = new JCheckBox(
      "Ensure physical pixel sizes are compatible with versions <= 5.3.2", physicalSizeBackwardsCompatibility);
    physicalSizeBox.addItemListener(this);

    labels = new String[] {physicalSizeLabel};
    widgets = new Component[] {physicalSizeBox};
  }

  // -- IFormatWidgets API methods --

  @Override
  public String[] getLabels() {
    return labels;
  }

  @Override
  public Component[] getWidgets() {
    return widgets;
  }

  // -- ItemListener API methods --

  @Override
  public void itemStateChanged(ItemEvent e) {
    JCheckBox box = (JCheckBox) e.getSource();
    if (box.equals(widgets[0])) {
      Prefs.set(LociPrefs.PREF_LEICA_LIF_PHYSICAL_SIZE, box.isSelected());
    }
  }

}
