//
// Updater.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
Data Browser and Stack Slicer. Copyright (C) 2005-@year@ Melissa Linkert,
Curtis Rueden and Christopher Peterson.

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

import ij.IJ;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import loci.common.Constants;
import loci.formats.FormatTools;
import loci.plugins.util.WindowTools;

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

  public static final String TRUNK_BUILD =
    "http://dev.loci.wisc.edu:8080/job/Bio-Formats/lastSuccessfulBuild/" +
    "artifact/artifacts/loci_tools.jar";
  public static final String TODAYS_BUILD =
    "http://loci.wisc.edu/software/daily/loci_tools.jar";
  public static final String YESTERDAYS_BUILD =
    "http://loci.wisc.edu/software/daily.old/loci_tools.jar";
  public static final String STABLE_BUILD =
    "http://loci.wisc.edu/software/loci_tools.jar";

  public static final String STABLE_VERSION = "4.3.2";

  public static final String REGISTRY = "http://upgrade.openmicroscopy.org.uk";

  public static final String[] REGISTRY_PROPERTIES = new String[] {
    "version", "os.name", "os.version", "os.arch",
    "java.runtime.version", "java.vm.vendor"
  };

  public static final int CHUNK_SIZE = 8192;

  // -- Fields --

  /** Flag indicating whether last operation was canceled. */
  public boolean canceled;

  /** Path to loci_tools.jar. */
  private String urlPath;

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
    if (release.equals(options[0])) urlPath = TRUNK_BUILD;
    else if (release.equals(options[1])) urlPath = TODAYS_BUILD;
    else if (release.equals(options[2])) urlPath = YESTERDAYS_BUILD;
    else urlPath = STABLE_BUILD;

    install(urlPath);
  }

  // -- Utility methods --

  /** Check if a new stable version is available. */
  public static boolean newVersionAvailable() {
    // connect to the registry

    StringBuffer query = new StringBuffer(REGISTRY);
    for (int i=0; i<REGISTRY_PROPERTIES.length; i++) {
      if (i == 0) query.append("?");
      else query.append(";");
      query.append(REGISTRY_PROPERTIES[i]);
      query.append("=");
      if (i == 0) query.append(FormatTools.VERSION);
      else {
        try {
          query.append(URLEncoder.encode(
            System.getProperty(REGISTRY_PROPERTIES[i]), Constants.ENCODING));
        }
        catch (UnsupportedEncodingException e) { }
      }
    }

    try {
      URLConnection conn = new URL(query.toString()).openConnection();
      conn.setConnectTimeout(5000);
      conn.setUseCaches(false);
      conn.addRequestProperty("User-Agent", "OMERO.imagej");
      conn.connect();

      // retrieve latest version number from the registry

      InputStream in = conn.getInputStream();
      StringBuffer latestVersion = new StringBuffer();
      while (true) {
        int data = in.read();
        if (data == -1) break;
        latestVersion.append((char) data);
      }
      in.close();

      // check to see if version reported by registry is greater than
      // the current version - version number should be in "x.x.x" format

      String[] version = latestVersion.toString().split("\\.");
      String[] thisVersion = FormatTools.VERSION.split("\\.");
      for (int i=0; i<thisVersion.length; i++) {
        int subVersion = Integer.parseInt(thisVersion[i]);
        try {
          int registrySubVersion = Integer.parseInt(version[i]);
          if (registrySubVersion > subVersion) return true;
          if (registrySubVersion < subVersion) return false;
        }
        catch (NumberFormatException e) {
          return false;
        }
      }
    }
    catch (IOException e) { }
    return false;
  }

  /** Download and install a JAR file from the given URL. */
  public static void install(final String urlPath) {
    String pluginsDirectory = IJ.getDirectory("plugins");
    String jarPath = pluginsDirectory + File.separator + "loci_tools.jar";
    String downloadPath = jarPath + ".tmp";

    // if an old version exists, remove it
    File plugin = new File(downloadPath);
    if (plugin.exists()) {
      if (!plugin.delete()) {
        BF.status(false, "");
        BF.progress(false, 1, 1);
        WindowTools.reportException(
          new IOException("Could not delete " + downloadPath), false,
          "An error occurred while downloading the LOCI plugins");
        return;
      }
    }

    // download new version to plugins directory
    BF.status(false, "Connecting to download server...");
    try {
      URL url = new URL(urlPath);
      URLConnection urlConn = url.openConnection();
      int total = urlConn.getContentLength();
      byte[] buf = new byte[total];
      DataInputStream in = new DataInputStream(
        new BufferedInputStream(urlConn.getInputStream()));
      int off = 0;
      while (off < total) {
        BF.status(false, "Downloading loci_tools.jar...");
        BF.progress(false, off, total);
        int len = CHUNK_SIZE;
        if (off + len > total) len = total - off;
        int r = in.read(buf, off, len);
        if (r <= 0) {
          BF.status(false, "");
          IJ.showMessage("Error downloading the LOCI plugins.");
          return;
        }
        off += r;
      }
      in.close();
      BF.progress(false, 1, 1);
      BF.status(false, "Saving loci_tools.jar...");

      FileOutputStream out = new FileOutputStream(plugin);
      out.write(buf);
      out.close();

      boolean success = plugin.renameTo(new File(jarPath));
      if (!success) {
        BF.status(false, "");
        IJ.showMessage("The LOCI plugins have been downloaded to:\n" +
          downloadPath + "\n" +
          "The JAR file could not be renamed to loci_tools.jar.\n" +
          "Please rename " + downloadPath + " to\n" + jarPath + "\n" +
          "and restart ImageJ to complete the upgrade process.");
        return;
      }
    }
    catch (IOException e) {
      BF.status(false, "");
      BF.progress(false, 1, 1);
      WindowTools.reportException(e, false,
        "An error occurred while downloading the LOCI plugins");
      return;
    }
    BF.status(false, "");
    IJ.showMessage("The LOCI plugins have been downloaded.\n" +
      "Please restart ImageJ to complete the upgrade process.");
  }

}
