//
// UpgradeDialog.java
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

import ij.IJ;
import ij.gui.GenericDialog;
import loci.plugins.Updater;
import loci.plugins.prefs.OptionsDialog;

/**
 * Bio-Formats Importer upgrade checker dialog box.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/in/UpgradeDialog.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/in/UpgradeDialog.java">SVN</a></dd></dl>
 */
public class UpgradeDialog extends OptionsDialog {

  // -- Fields --

  /** LOCI plugins configuration. */
  protected ImporterOptions options;

  // -- Constructor --

  /** Creates an upgrade checker dialog for the Bio-Formats Importer. */
  public UpgradeDialog(ImporterOptions options) {
    super(options);
    this.options = options;
  }

  // -- OptionsDialog methods --

  /**
   * Asks user whether Bio-Formats should automatically check for upgrades,
   * and if so, checks for an upgrade and prompts user to install it.
   *
   * @return status of operation
   */
  public int showDialog() {
    if (options.isQuiet()) return STATUS_OK;

    if (options.isFirstTime()) {
      // present user with one-time dialog box
      GenericDialog gd = new GenericDialog("Bio-Formats Upgrade Checker");
      gd.addMessage("One-time notice: The LOCI plugins for ImageJ can " +
        "automatically check for upgrades\neach time they are run. If you " +
        "wish to disable this feature, uncheck the box below.\nYou can " +
        "toggle this behavior later in the LOCI Plugins Configuration's " +
        "\"Upgrade\" tab.");
      addCheckbox(gd, ImporterOptions.KEY_UPGRADE_CHECK);
      gd.showDialog();
      if (gd.wasCanceled()) return STATUS_CANCELED;
    }

    if (options.doUpgradeCheck()) {
      IJ.showStatus("Checking for new version...");
      if (Updater.newVersionAvailable()) {
        boolean doUpgrade = IJ.showMessageWithCancel("",
          "A new stable version of Bio-Formats is available.\n" +
          "Click 'OK' to upgrade now, or 'Cancel' to skip for now.");
        if (doUpgrade) Updater.install(Updater.STABLE_BUILD);
      }
    }

    return STATUS_OK;
  }

}
