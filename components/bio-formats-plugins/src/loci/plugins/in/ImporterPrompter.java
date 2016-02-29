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

import ij.IJ;
import loci.common.StatusEvent;
import loci.common.StatusListener;
import loci.plugins.BF;
import loci.plugins.prefs.OptionsDialog;

/**
 * Helper class for presenting the user with dialog boxes
 * for configuring importer options.
 *
 * If running as a macro, gets parameter values from macro options;
 * if not, get parameter values from user input from dialog boxes.
 *
 * Which dialogs are shown depends on a variety of factors, including the
 * current configuration (i.e., which options are enabled), whether quiet or
 * windowless mode is set, and whether the method is being called from within
 * a macro.
 */
public class ImporterPrompter implements StatusListener {

  // -- Fields --

  private ImportProcess process;

  // -- Constructor --

  public ImporterPrompter(ImportProcess process) {
    this.process = process;
    process.addStatusListener(this);
  }

  // -- StatusListener methods --

  @Override
  public void statusUpdated(StatusEvent e) {
    final String message = e.getStatusMessage();
    final int value = e.getProgressValue();
    //final int max = e.getProgressMaximum();
    final ImportStep step = ImportStep.getStep(value);

    BF.status(!process.getOptions().isQuiet(), message);

    switch (step) {
      case READER:
        if (!promptUpgrade()) { process.cancel(); break; }
        if (!promptLocation()) { process.cancel(); break; }
        if (!promptId()) { process.cancel(); break; }
        break;
      case FILE:
        if (!promptMain()) process.cancel();
        break;
      case STACK:
        ImporterOptions options = process.getOptions();
        if (options != null && options.doMustGroup() && options.isGroupFiles()) {
          IJ.showMessage("Bio-Formats",
    				 "File Stitching Options are not available for files of this format.\n"
    				 + "Files will be grouped according to image format specifications.\n");
        }
        else if (!promptFilePattern()) process.cancel();
        break;
      case SERIES:
        if (!promptSeries()) process.cancel();
        break;
      case DIM_ORDER:
        if (!promptSwap()) process.cancel();
        break;
      case RANGE:
        if (!promptRange()) process.cancel();
        break;
      case CROP:
        if (!promptCrop()) process.cancel();
        break;
      case COLORS:
        if (!promptColors()) process.cancel();
        break;
      case METADATA:
        break;
      case COMPLETE:
        if (!promptMemory()) process.cancel();
        break;
      default:
    }
  }

  // -- Helper methods - dialog prompts --

  private boolean promptUpgrade() {
    UpgradeDialog dialog = new UpgradeDialog(process);
    return dialog.showDialog() == OptionsDialog.STATUS_OK;
  }

  private boolean promptLocation() {
    LocationDialog dialog = new LocationDialog(process);
    return dialog.showDialog() == OptionsDialog.STATUS_OK;
  }

  private boolean promptId() {
    IdDialog dialog = new IdDialog(process);
    return dialog.showDialog() == OptionsDialog.STATUS_OK;
  }

  private boolean promptMain() {
    MainDialog dialog = new MainDialog(process);
    return dialog.showDialog() == OptionsDialog.STATUS_OK;
  }

  /** Prompts for the file pattern, if necessary. May override id value. */
  private boolean promptFilePattern() {
    FilePatternDialog dialog = new FilePatternDialog(process);
    return dialog.showDialog() == OptionsDialog.STATUS_OK;
  }

  /** Prompts for which series to import, if necessary. */
  private boolean promptSeries() {
    SeriesDialog dialog = new SeriesDialog(process);
    return dialog.showDialog() == OptionsDialog.STATUS_OK;
  }

  /** Prompts for dimension swapping parameters, if necessary. */
  private boolean promptSwap() {
    SwapDialog dialog = new SwapDialog(process);
    return dialog.showDialog() == OptionsDialog.STATUS_OK;
  }

  /** Prompts for the range of planes to import, if necessary. */
  private boolean promptRange() {
    RangeDialog dialog = new RangeDialog(process);
    return dialog.showDialog() == OptionsDialog.STATUS_OK;
  }

  /** Prompts for cropping details, if necessary. */
  private boolean promptCrop() {
    CropDialog dialog = new CropDialog(process);
    return dialog.showDialog() == OptionsDialog.STATUS_OK;
  }

  /** Prompts for color details, if necessary. */
  private boolean promptColors() {
    ColorDialog dialog = new ColorDialog(process);
    return dialog.showDialog() == OptionsDialog.STATUS_OK;
  }

  /** Prompts for confirmation of memory usage, if necessary. */
  private boolean promptMemory() {
    MemoryDialog dialog = new MemoryDialog(process);
    return dialog.showDialog() == OptionsDialog.STATUS_OK;
  }

}
