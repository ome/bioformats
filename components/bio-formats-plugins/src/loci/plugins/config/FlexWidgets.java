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

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import loci.common.services.DependencyException;
import loci.common.services.ServiceFactory;
import loci.formats.services.LuraWaveService;
import loci.formats.services.LuraWaveServiceImpl;

/**
 * Custom widgets for configuring Bio-Formats Flex support.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class FlexWidgets implements DocumentListener, IFormatWidgets {

  // -- Fields --

  private String[] labels;
  private Component[] widgets;

  private JTextField licenseBox;

  // -- Constructor --

  public FlexWidgets() {
    LuraWaveService service;
    try {
      ServiceFactory factory = new ServiceFactory();
      service = factory.getInstance(LuraWaveService.class);
    }
    catch (DependencyException e) {
      throw new RuntimeException(e);
    }

    // get license code from ImageJ preferences
    String prefCode = Prefs.get(LuraWaveServiceImpl.LICENSE_PROPERTY, null);
    String propCode = service.getLicenseCode();
    String code = "";
    if (prefCode != null) code = prefCode;
    else if (propCode != null) code = null; // hidden code

    String licenseLabel = "LuraWave license code";
    licenseBox = ConfigWindow.makeTextField();
    licenseBox.setText(code == null ? "(Licensed)" : code);
    licenseBox.setEditable(code != null);
    licenseBox.getDocument().addDocumentListener(this);

    labels = new String[] {licenseLabel};
    widgets = new Component[] {licenseBox};
  }

  // -- DocumentListener API methods --

  @Override
  public void changedUpdate(DocumentEvent e) {
    documentUpdate();
  }
  @Override
  public void removeUpdate(DocumentEvent e) {
    documentUpdate();
  }
  @Override
  public void insertUpdate(DocumentEvent e) {
    documentUpdate();
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

  // -- Helper methods --

  private void documentUpdate() {
    String code = licenseBox.getText();
    Prefs.set(LuraWaveServiceImpl.LICENSE_PROPERTY, code);
  }

}
