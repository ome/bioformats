/*
 * #%L
 * Bio-Formats Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2014 Open Microscopy Environment:
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

/**
 * Bio-Formats Importer location chooser dialog box.
 */
public class LocationDialog extends ImporterDialog {

  // -- Constructor --

  /** Creates a location chooser dialog for the Bio-Formats Importer. */
  public LocationDialog(ImportProcess process) {
    super(process);
  }

  // -- ImporterDialog methods --

  @Override
  protected boolean needPrompt() {
    // NB: Prompt only if location wasn't already specified.
    return !process.isWindowless() && options.getLocation() == null;
  }

  @Override
  protected GenericDialog constructDialog() {
    GenericDialog gd = new GenericDialog("Bio-Formats Dataset Location");
    addChoice(gd, ImporterOptions.KEY_LOCATION);
    return gd;
  }

  @Override
  protected boolean harvestResults(GenericDialog gd) {
    String location = gd.getNextChoice();
    options.setLocation(location);
    return true;
  }

}
