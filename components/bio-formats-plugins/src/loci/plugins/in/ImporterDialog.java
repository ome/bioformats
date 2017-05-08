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

package loci.plugins.in;

import ij.gui.GenericDialog;
import loci.plugins.BF;
import loci.plugins.prefs.OptionsDialog;

/**
 * Abstract superclass of importer dialogs.
 */
public abstract class ImporterDialog extends OptionsDialog {

  // -- Fields --

  protected ImportProcess process;
  protected ImporterOptions options;

  // -- Constructor --

  public ImporterDialog(ImportProcess process) {
    super(process.getOptions());
    this.process = process;
    this.options = process.getOptions();
  }

  // -- ImporterDialog methods --

  protected abstract boolean needPrompt();

  protected abstract GenericDialog constructDialog();

  /** Displays the dialog, or grabs values from macro options. */
  protected boolean displayDialog(GenericDialog gd) {
    gd.showDialog();
    return !gd.wasCanceled();
  }

  protected abstract boolean harvestResults(GenericDialog gd);

  // -- OptionsDialog methods --

  @Override
  public int showDialog() {
    // verify whether prompt is necessary
    if (!needPrompt()) {
      BF.debug(getClass().getName() + ": skip");
      return STATUS_OK;
    }
    BF.debug(getClass().getName() + ": prompt");

    GenericDialog gd = constructDialog();
    if (!displayDialog(gd)) return STATUS_CANCELED;
    if (!harvestResults(gd)) return STATUS_CANCELED;

    return STATUS_OK;
  }

}
