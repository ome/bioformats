//
// ImporterPrompter.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
Data Browser, Stack Colorizer and Stack Slicer. Copyright (C) 2005-@year@
Melissa Linkert, Curtis Rueden and Christopher Peterson.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.plugins.in;

import java.io.IOException;

import loci.formats.FormatException;
import loci.plugins.prefs.OptionsDialog;

/**
 * Helper class for presenting the user with dialog boxes
 * for configuring importer options.
 *
 * If running as a macro, gets parameter values from macro options;
 * if not, get parameter values from user input from dialog boxes.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/in/ImporterPrompter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/in/ImporterPrompter.java">SVN</a></dd></dl>
 */
public class ImporterPrompter {

  // -- Fields --

  private ImportProcess process;

  // -- Constructor --

  public ImporterPrompter(ImportProcess process) {
    this.process = process;
  }

  // -- ImporterPrompter methods --

  /**
   * Displays dialog boxes prompting for additional configuration details.
   *
   * Which dialogs are shown depends on a variety of factors, including the
   * current configuration (i.e., which options are enabled), whether quiet or
   * windowless mode is set, and whether the method is being called from within
   * a macro.
   *
   * After calling this method, derived field values will also be populated.
   *
   * @return true if harvesting went OK, or false if something went wrong
   *   (e.g., the user canceled a dialog box)
   *
   * @see ij.gui.GenericDialog
   */
  public boolean showDialogs() throws FormatException, IOException {
    if (!promptUpgrade()) return false;

    if (!promptLocation()) return false;
    if (!promptId()) return false;

    process.go();

    if (!promptMain()) return false;

    process.saveDefaults();

    process.prepareStuff();

    if (!promptFilePattern()) return false;

    process.initializeReader();

    if (!promptSeries()) return false;
    if (!promptSwap()) return false;
    if (!promptRange()) return false;
    if (!promptCrop()) return false;

    process.initializeMetadata();

    return true;
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

}
