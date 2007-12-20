//
// OMEPlugin.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
4D Data Browser, Bio-Formats Importer, Bio-Formats Exporter and OME plugins.
Copyright (C) 2006-@year@ Melissa Linkert, Christopher Peterson,
Curtis Rueden, Philip Huettl and Francis Wong.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.plugins;

import ij.*;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import java.awt.TextField;
import java.util.*;
import loci.formats.ReflectException;
import loci.formats.ome.OMECredentials;
import loci.formats.ome.OMEUtils;

/**
 * OMEPlugin is the ImageJ Plugin that allows image import and exports from
 * OME and OMERO servers.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/plugins/OMEPlugin.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/plugins/OMEPlugin.java">SVN</a></dd></dl>
 *
 * @author Philip Huettl pmhuettl at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 */
public class OMEPlugin implements PlugIn {

  // -- Fields --

  private OMECredentials cred;
  private static boolean cancelPlugin;

  // -- PlugIn API methods --

  /** Executes the plugin. */
  public void run(String arg) {
    if (!Checker.checkVersion()) return;
    HashSet missing = new HashSet();
    Checker.checkLibrary(Checker.BIO_FORMATS, missing);
    Checker.checkLibrary(Checker.OME_JAVA_XML, missing);
    Checker.checkLibrary(Checker.OME_JAVA_DS, missing);
    Checker.checkLibrary(Checker.FORMS, missing);
    if (!Checker.checkMissing(missing)) return;
    runPlugin();
  }

  // -- OMEPlugin API methods --

  /**
   * The getInput method prompts and receives user input to determine
   * the OME login fields and whether the stack is in the time or space domain
   */
  private void getInput() {
    GenericDialog gd = new GenericDialog("OME Login");

    String server = Prefs.get("downloader.server", "");
    String user = Prefs.get("downloader.user", "");
    String port = Prefs.get("downloader.port", "");
    String type = Prefs.get("downloader.type", "OME");

    if (server.startsWith("http:")) server = server.substring(5);
    while (server.startsWith("/")) server = server.substring(1);
    int slash = server.indexOf("/");
    if (slash >= 0) server = server.substring(0, slash);
    int colon = server.indexOf(":");
    if (colon >= 0) server = server.substring(0, colon);

    gd.addChoice("Server type: ", new String[] {"OME", "OMERO"}, type);
    gd.addStringField("Server:      ", server, 30);
    gd.addStringField("Port:        ", port, 30);
    gd.addStringField("Username:    ", user, 30);
    gd.addStringField("Password:    ", "", 30);

    // star out the password field

    Vector v = gd.getStringFields();
    ((TextField) v.get(3)).setEchoChar('*');

    gd.showDialog();
    if (gd.wasCanceled()) {
      cancelPlugin = true;
      return;
    }

    type = gd.getNextChoice();
    server = gd.getNextString();

    // do sanity check on server name
    if (type.equals("OME")) {
      if (server.startsWith("http:")) {
        server = server.substring(5);
      }
      while (server.startsWith("/")) server = server.substring(1);
      slash = server.indexOf("/");
      if (slash >= 0) server = server.substring(0, slash);
      colon = server.indexOf(":");
      if (colon >= 0) server = server.substring(0, colon);
      server = "http://" + server + "/shoola/";
    }

    cred = new OMECredentials();
    cred.server = server;
    cred.port = gd.getNextString();
    cred.username = gd.getNextString();
    cred.password = gd.getNextString();
    cred.isOMERO = type.equals("OMERO");
    try {
      OMEUtils.login(cred);
    }
    catch (ReflectException e) {
      IJ.error("Login failed");
      e.printStackTrace();
      getInput();
    }

    Prefs.set("downloader.server", server);
    Prefs.set("downloader.user", cred.username);
    Prefs.set("downloader.port", cred.port);
    Prefs.set("downloader.type", type);
  }

  public static void setPlugin(boolean isCancelled) {
    cancelPlugin = isCancelled;
  }

  /** Logout of OME database */
  public void logout() {
    IJ.showStatus("OME: Logging out...");
    IJ.showProgress(.99);
    OMEUtils.logout(cred.isOMERO);
    IJ.showStatus("OME: Completed");
  }

  // -- Download methods --

  /** Does the work for downloading data from OME. */
  public void runPlugin() {
    try {
      getInput();
      if (cancelPlugin) {
        cancelPlugin = false;
        return;
      }

      // prompt for search criteria

      GenericDialog searchBox = new GenericDialog("Image search...");
      if (!cred.isOMERO) {
        String[] names = OMEUtils.getAllExperimenters(cred.isOMERO);
        String[] tmp = names;
        names = new String[tmp.length + 1];
        names[0] = "";
        System.arraycopy(tmp, 0, names, 1, tmp.length);
        searchBox.addChoice("Experimenter: ", names, names[0]);
      }
      searchBox.addStringField("Creation date (yyyy-mm-dd): ", "");
      searchBox.addStringField("Image ID: ", "");
      searchBox.showDialog();

      if (searchBox.wasCanceled()) {
        cancelPlugin = true;
        return;
      }

      String name = cred.isOMERO ? null : searchBox.getNextChoice();
      String created = searchBox.getNextString();
      String id = searchBox.getNextString();

      String firstName = null, lastName = null;
      if (name != null && name.indexOf(",") != -1) {
        lastName = name.substring(0, name.indexOf(",")).trim();
        firstName = name.substring(name.indexOf(",") + 2).trim();
      }
      if (firstName != null && firstName.trim().equals("")) firstName = null;
      if (lastName != null && lastName.trim().equals("")) lastName = null;
      if (created != null && created.trim().equals("")) created = null;
      if (id != null && id.trim().equals("")) id = null;

      // filter images based on search terms
      OMEUtils.filterPixels(firstName, lastName, created, id, cred.isOMERO);

      // retrieve image selection(s)

      long[] images = new OMEUtils().showTable(cred);
      if (images == null || images.length == 0) {
        logout();
        return;
      }

      // download into ImageJ
      for (int i=0; i<images.length; i++) {
        String type = cred.isOMERO ? "OMERO server" : "OME server";
        String file = "location=[" + type + "] open=[" + cred.server +
          (cred.isOMERO ? ":" + cred.port : "") + "?user=" + cred.username +
          "&password=" + cred.password + "&id=" + images[i] + "]";
        IJ.runPlugIn("loci.plugins.LociImporter", file);

        if (cancelPlugin) return;
      }
      logout();
    }
    catch (NullPointerException e) {
      e.printStackTrace();
    }
    catch (IllegalArgumentException f) {
      // do nothing; this means that the user cancelled the login procedure
      f.printStackTrace();
    }
    catch (Exception exc) {
      IJ.setColumnHeadings("Errors");
      IJ.write("An exception has occurred:  \n" + exc.toString());
      IJ.showStatus("Error uploading (see error console for details)");
      exc.printStackTrace();
    }

    IJ.showProgress(1);
  }

}
