/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2017 Open Microscopy Environment:
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

import loci.formats.in.NativeND2Reader;
import loci.plugins.util.LociPrefs;

/**
 * Custom widgets for configuring Bio-Formats ND2 support.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class ND2Widgets implements IFormatWidgets, ItemListener {

  // -- Fields --

  private String[] labels;
  private Component[] widgets;

  // -- Constructor --

  public ND2Widgets() {
    boolean nikon = Prefs.get(LociPrefs.PREF_ND2_NIKON, false);

    String legacyLabel = "Nikon";
    JCheckBox legacyBox = new JCheckBox(
      "Use Nikon's ND2 library instead of native ND2 support", nikon);
    legacyBox.addItemListener(this);

    boolean chunkmap = Prefs.get(LociPrefs.PREF_ND2_CHUNKMAP,
      NativeND2Reader.USE_CHUNKMAP_DEFAULT);

    String chunkmapLabel = "Chunkmap";
    JCheckBox chunkmapBox = new JCheckBox(
      "Use chunkmap table to read image offsets", chunkmap);
    chunkmapBox.addItemListener(this);

    labels = new String[] {legacyLabel, chunkmapLabel};
    widgets = new Component[] {legacyBox, chunkmapBox};
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
    if (box.equals(getWidgets()[0])) {
      Prefs.set(LociPrefs.PREF_ND2_NIKON, box.isSelected());
    }
    else if (box.equals(getWidgets()[1])) {
      Prefs.set(LociPrefs.PREF_ND2_CHUNKMAP, box.isSelected());
    }
  }

}
