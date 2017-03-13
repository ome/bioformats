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

import ij.IJ;
import ij.Prefs;
import ij.gui.GenericDialog;

import loci.formats.UpgradeChecker;
import loci.plugins.BF;
import loci.plugins.Updater;
import loci.plugins.prefs.Option;

/**
 * Bio-Formats Importer upgrade checker dialog box.
 */
public class UpgradeDialog extends ImporterDialog {

  // -- Static fields --

  /** Whether an upgrade check has already been performed this session. */
  private static boolean checkPerformed = false;

  // -- Constructor --

  /** Creates an upgrade checker dialog for the Bio-Formats Importer. */
  public UpgradeDialog(ImportProcess process) {
    super(process);
  }

  // -- ImporterDialog methods --

  @Override
  protected boolean needPrompt() {
    return !options.isQuiet() && !process.isWindowless();
  }

  @Override
  protected GenericDialog constructDialog() { return null; }

  /**
   * Asks user whether Bio-Formats should automatically check for upgrades,
   * and if so, checks for an upgrade and prompts user to install it.
   *
   * @return status of operation
   */
  @Override
  protected boolean displayDialog(GenericDialog gd) {
    if (checkPerformed) return true;

    if (!options.isQuiet() && options.isFirstTime()) {
      // present user with one-time dialog box
      gd = new GenericDialog("Bio-Formats Upgrade Checker");
      gd.addMessage("One-time notice: The Bio-Formats plugins for ImageJ can " +
        "automatically check for upgrades\neach time they are run. If you " +
        "wish to disable this feature, uncheck the box below.\nYou can " +
        "toggle this behavior later in the Bio-Formats Plugins Configuration's " +
        "\"Upgrade\" tab.");
      addCheckbox(gd, ImporterOptions.KEY_UPGRADE_CHECK);
      gd.showDialog();
      if (gd.wasCanceled()) return false;

      // save choice
      final boolean checkForUpgrades = gd.getNextBoolean();
      options.setUpgradeCheck(checkForUpgrades);
      if (!checkForUpgrades) return true;
    }

    if (options.doUpgradeCheck()) {
      UpgradeChecker checker = new UpgradeChecker();
      checkPerformed = true;
      BF.status(false, "Checking for new stable version...");
      // check for Fiji here instead of earlier in the method so that we
      // still have a chance of keeping OME.registry up to date
      if (checker.newVersionAvailable("ImageJ") && !Updater.isFiji()) {
        boolean doUpgrade = IJ.showMessageWithCancel("",
          "A new stable version of Bio-Formats is available.\n" +
          "Click 'OK' to upgrade now, or 'Cancel' to skip for now.");
        if (doUpgrade) {
          Updater.install(UpgradeChecker.STABLE_BUILD + UpgradeChecker.TOOLS);
        }
      }
    }

    return true;
  }

  @Override
  protected boolean harvestResults(GenericDialog gd) { return true; }

}
