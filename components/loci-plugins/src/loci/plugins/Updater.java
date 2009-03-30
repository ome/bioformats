//
// Updater.java
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

package loci.plugins;

import ij.*;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import java.io.*;
import loci.common.*;
import loci.formats.*;

/**
 * A plugin for updating the LOCI plugins.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/Updater.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/Updater.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class Updater implements PlugIn {

  // -- Constants --

  protected static final String TRUNK_BUILD =
    "http://hudson.openmicroscopy.org.uk/job/LOCI/lastSuccessfulBuild/" +
    "artifact/trunk/artifacts/loci_tools.jar";
  protected static final String TODAYS_BUILD =
    "http://loci.wisc.edu/software/daily/loci_tools.jar";
  protected static final String YESTERDAYS_BUILD =
    "http://loci.wisc.edu/software/daily.old/loci_tools.jar";
  protected static final String STABLE_BUILD =
    "http://loci.wisc.edu/software/loci_tools.jar";

  protected static final String STABLE_VERSION = "29 December 2008";

  // -- Fields --

  /** Flag indicating whether last operation was canceled. */
  public boolean canceled;

  /** Plugin options. */
  private String arg;

  // -- PlugIn API methods --

  public void run(String arg) {
    GenericDialog upgradeDialog = new GenericDialog("Update LOCI Plugins");
    String[] options = new String[] {"Trunk build", "Daily build (today)",
      "Daily build (yesterday)", "Stable build (" + STABLE_VERSION + ")"};
    upgradeDialog.addChoice("Release", options, options[0]);
    upgradeDialog.showDialog();

    if (upgradeDialog.wasCanceled()) {
      canceled = true;
      return;
    }

    String release = upgradeDialog.getNextChoice();
    if (release.equals(options[0])) install(TRUNK_BUILD);
    else if (release.equals(options[1])) install(TODAYS_BUILD);
    else if (release.equals(options[2])) install(YESTERDAYS_BUILD);
    else install(STABLE_BUILD);
  }

  // -- Helper methods --

  /** Download and install a JAR file from the given URL. */
  protected static void install(String url) {
    String pluginsDirectory = IJ.getDirectory("plugins");
    String jarPath = pluginsDirectory + File.separator + "loci_tools.jar";
    String downloadPath = jarPath + ".tmp";

    // if an old version exists, remove it
    File plugin = new File(downloadPath);
    if (plugin.exists()) plugin.delete();

    // download new version to plugins directory
    IJ.showStatus("Downloading loci_tools.jar...");
    try {
      RandomAccessStream in = new RandomAccessStream(url);
      byte[] buf = new byte[(int) in.length()];
      in.read(buf);
      in.close();

      FileOutputStream out = new FileOutputStream(plugin);
      out.write(buf);
      out.close();

      plugin.renameTo(new File(jarPath));
    }
    catch (IOException e) {
      IJ.showMessage("An error occurred while downloading the LOCI plugins");
      e.printStackTrace();
      return;
    }
    IJ.showStatus("");
    IJ.showMessage("The LOCI plugins have been downloaded.\n" +
      "Please restart ImageJ to complete the upgrade process.");
  }

}
