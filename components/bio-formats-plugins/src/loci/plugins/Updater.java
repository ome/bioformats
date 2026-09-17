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

package loci.plugins;

import ij.IJ;
import ij.ImageJ;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;

import java.io.File;

import loci.formats.UpgradeChecker;

/**
 * A plugin for updating the Bio-Formats plugins.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class Updater implements PlugIn {

  // -- Fields --

  /** Flag indicating whether last operation was canceled. */
  public boolean canceled;

  /** Path to bioformats_package.jar uber-jar. */
  private String urlPath;

  // -- PlugIn API methods --

  @Override
  public void run(String arg) {
    if (isFiji()) {
      IJ.showMessage("Please use 'Help > Update...' to update.");
      return;
    }

    urlPath = UpgradeChecker.STABLE_BUILD;
    urlPath += UpgradeChecker.TOOLS;
    install(urlPath);
  }

  /**
   * Returns true if the current ImageJ instance is actually a Fiji instance.
   */
  public static boolean isFiji() {
    ImageJ ij = IJ.getInstance();
    if (ij != null) {
      String title = ij.getTitle();
      if (title != null) {
        return title.indexOf("Fiji") >= 0;
      }
    }
    return false;
  }

  /**
   * Install the tools bundle which can be retrieved from the specified path.
   */
  public static void install(String urlPath) {
    String pluginsDirectory = IJ.getDirectory("plugins");
    String jarPath = pluginsDirectory;
    if (!isFiji()) {
      jarPath += UpgradeChecker.TOOLS;
    }

    BF.status(false, "Downloading...");
    boolean success = false;
    if (isFiji()) {
      return;
    }
    else {
      success = new UpgradeChecker().install(urlPath, jarPath);
    }

    BF.status(false, "");
    if (!success) {
      IJ.showMessage("An error occurred while downloading the Bio-Formats plugins");
    }
    else {
      IJ.showMessage("The Bio-Formats plugins have been downloaded.\n" +
        "Please restart ImageJ to complete the upgrade process.");
    }
  }

}
