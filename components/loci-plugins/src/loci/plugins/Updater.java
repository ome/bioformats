/*
 * #%L
 * LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
 * Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
 * Data Browser and Stack Slicer.
 * %%
 * Copyright (C) 2006 - 2012 Open Microscopy Environment:
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
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;

import java.io.File;

import loci.formats.UpgradeChecker;

/**
 * A plugin for updating the LOCI plugins.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/loci-plugins/src/loci/plugins/Updater.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/loci-plugins/src/loci/plugins/Updater.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class Updater implements PlugIn {

  // -- Constants --

  private static final String TRUNK = "Trunk build";
  private static final String DAILY = "Daily build";
  private static final String STABLE =
    "Stable build (" + UpgradeChecker.STABLE_VERSION + ")";

  private static final boolean IS_FIJI =
    IJ.getInstance().getTitle().equalsIgnoreCase("Fiji");

  // -- Fields --

  /** Flag indicating whether last operation was canceled. */
  public boolean canceled;

  /** Path to loci_tools.jar. */
  private String urlPath;

  // -- PlugIn API methods --

  public void run(String arg) {
    GenericDialog upgradeDialog = new GenericDialog("Update LOCI Plugins");
    String[] options = new String[] {TRUNK, DAILY, STABLE};
    upgradeDialog.addChoice("Release", options, options[0]);
    upgradeDialog.showDialog();

    if (upgradeDialog.wasCanceled()) {
      canceled = true;
      return;
    }

    String release = upgradeDialog.getNextChoice();

    if (release.equals(TRUNK)) {
      urlPath = UpgradeChecker.TRUNK_BUILD;
    }
    else if (release.equals(DAILY)) {
      urlPath = UpgradeChecker.DAILY_BUILD;
    }
    else if (release.equals(STABLE)) {
      urlPath = UpgradeChecker.STABLE_BUILD;
    }
    if (!IS_FIJI) {
      urlPath += UpgradeChecker.TOOLS;
    }
    install(urlPath);
  }

  /**
   * Install the tools bundle which can be retrieved from the specified path.
   */
  public static void install(String urlPath) {
    String pluginsDirectory = IJ.getDirectory("plugins");
    String jarPath = pluginsDirectory;
    if (!IS_FIJI) {
      jarPath += UpgradeChecker.TOOLS;
    }

    BF.status(false, "Downloading...");
    boolean success = false;
    if (IS_FIJI) {
      jarPath = new File(jarPath).getParent();

      success = true;
      UpgradeChecker upgrader = new UpgradeChecker();
      for (String file : UpgradeChecker.INDIVIDUAL_JARS) {
        BF.status(false, "Download " + file);
        String foundFile = find(jarPath, file);
        if (foundFile == null) {
          foundFile = jarPath + File.separator + "jars" + File.separator + file;
        }

        String url = urlPath + File.separator + file;
        boolean localSuccess = upgrader.install(url, foundFile);
        if (!localSuccess) {
          success = false;
        }
      }
    }
    else {
      success = new UpgradeChecker().install(urlPath, jarPath);
    }

    BF.status(false, "");
    if (!success) {
      IJ.showMessage("An error occurred while downloading the LOCI plugins");
    }
    else {
      IJ.showMessage("The LOCI plugins have been downloaded.\n" +
        "Please restart ImageJ to complete the upgrade process.");
    }
  }

  // -- Helper methods --

  private static String find(String dir, String filename) {
    File dirFile = new File(dir);
    String[] list = dirFile.list();
    for (String f : list) {
      File nextFile = new File(dirFile, f);
      if (nextFile.isDirectory()) {
        String result = find(nextFile.getAbsolutePath(), filename);
        if (result != null) {
          return result;
        }
      }
      else {
        int dot = filename.indexOf(".");
        if (f.startsWith(filename.substring(0, dot)) &&
          f.endsWith(filename.substring(dot)))
        {
          return nextFile.getAbsolutePath();
        }
      }
    }
    return null;
  }

}
