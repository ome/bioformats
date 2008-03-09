//
// OMEPlugin.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Data Browser, Stack Colorizer,
Stack Slicer, and OME plugins. Copyright (C) 2005-@year@ Melissa Linkert,
Curtis Rueden, Christopher Peterson, Philip Huettl and Francis Wong.

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
  private String arg;

  // -- PlugIn API methods --

  /** Executes the plugin. */
  public void run(String arg) {
    if (IJ.debugMode) IJ.log("Downloading from OME or OMERO server");
    if (!Checker.checkJava() || !Checker.checkImageJ()) return;
    HashSet missing = new HashSet();
    Checker.checkLibrary(Checker.BIO_FORMATS, missing);
    Checker.checkLibrary(Checker.OME_JAVA_XML, missing);
    Checker.checkLibrary(Checker.OME_JAVA_DS, missing);
    Checker.checkLibrary(Checker.FORMS, missing);
    if (!Checker.checkMissing(missing)) {
      if (IJ.debugMode) IJ.log("Required libraries are missing, exiting.");
      return;
    }
    this.arg = arg;
    runPlugin();
  }

  // -- OMEPlugin API methods --

  /**
   * The getInput method prompts and receives user input to determine
   * the OME login fields and whether the stack is in the time or space domain
   */
  private void getInput() {
    String server = Prefs.get("downloader.server", "");
    String user = Prefs.get("downloader.user", "");
    String port = Prefs.get("downloader.port", "");
    String type = Prefs.get("downloader.type", "OME");
    String pass = null;
    if (arg == null || arg.trim().equals("")) {
      GenericDialog gd = new GenericDialog("OME Login");

      server = formatServerName(server);

      gd.addChoice("Server_type: ", new String[] {"OME", "OMERO"}, type);
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
      port = gd.getNextString();
      user = gd.getNextString();
      pass = gd.getNextString();
    }
    else {
      server = Macro.getValue(arg, "server", server);
      user = Macro.getValue(arg, "username", user);
      port = Macro.getValue(arg, "port", port);
      type = Macro.getValue(arg, "server_type", type);
      pass = Macro.getValue(arg, "password", "");
    }

    // do sanity check on server name
    if (type.equals("OME")) {
      server = "http://" + formatServerName(server) + "/shoola/";
    }

    cred = new OMECredentials();
    cred.server = server;
    cred.port = port;
    cred.username = user;
    cred.password = pass;
    cred.isOMERO = type.equals("OMERO");
    if (IJ.debugMode) IJ.log("Attempting to log in to " + server + ":" + port);
    try {
      OMEUtils.login(cred);
    }
    catch (ReflectException e) {
      IJ.error("Login failed");
      e.printStackTrace();
      getInput();
    }

    Prefs.set("downloader.server", server);
    Prefs.set("downloader.user", user);
    Prefs.set("downloader.port", port);
    Prefs.set("downloader.type", type);
    if (IJ.debugMode) IJ.log("Login successful!");
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
    if (IJ.debugMode) IJ.log("Logged out.");
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

      if (IJ.debugMode) IJ.log("Prompting for search criteria...");

      String name = null, firstName = null, lastName = null, iname = null,
        created = null, id = null;
      if (arg == null || arg.trim().equals("")) {
        GenericDialog searchBox = new GenericDialog("Image search...");
        if (!cred.isOMERO) {
          String[] names = OMEUtils.getAllExperimenters(cred.isOMERO);
          String[] tmp = names;
          names = new String[tmp.length + 1];
          names[0] = "";
          System.arraycopy(tmp, 0, names, 1, tmp.length);
          searchBox.addChoice("Experimenter: ", names, names[0]);
        }
        searchBox.addStringField("Image_name: ", "");
        searchBox.addStringField("Creation date (yyyy-mm-dd): ", "");
        searchBox.addStringField("Image_ID: ", "");
        searchBox.showDialog();

        if (searchBox.wasCanceled()) {
          if (IJ.debugMode) IJ.log("Search cancelled, returning.");
          cancelPlugin = true;
          return;
        }

        name = cred.isOMERO ? null : searchBox.getNextChoice();
        iname = searchBox.getNextString();
        created = searchBox.getNextString();
        id = searchBox.getNextString();
      }
      else {
        name = Macro.getValue(arg, "owner_name", "");
        iname = Macro.getValue(arg, "image_name", "");
        created = Macro.getValue(arg, "creation", "");
        id = Macro.getValue(arg, "image_id", "");
      }
      if (name != null && name.indexOf(",") != -1) {
        lastName = name.substring(0, name.indexOf(",")).trim();
        firstName = name.substring(name.indexOf(",") + 2).trim();
      }

      if (firstName != null && firstName.trim().equals("")) firstName = null;
      if (lastName != null && lastName.trim().equals("")) lastName = null;
      if (iname != null && iname.trim().equals("")) iname = null;
      if (created != null && created.trim().equals("")) created = null;
      if (id != null && id.trim().equals("")) id = null;

      if (IJ.debugMode) IJ.log("Search for matching images...");

      // filter images based on search terms
      OMEUtils.filterPixels(firstName, lastName, iname, created, id,
        cred.isOMERO);

      // retrieve image selection(s)

      long[] images = null;
      if (arg == null || arg.trim().equals("")) {
        if (IJ.debugMode) IJ.log("Found images...");
        images = new OMEUtils().showTable(cred);
      }

      if (images == null || images.length == 0) {
        if (IJ.debugMode) IJ.log("No images found!");
        logout();
        return;
      }

      // download into ImageJ
      for (int i=0; i<images.length; i++) {
        if (IJ.debugMode) IJ.log("Downloading image ID " + images[i]);
        String type = cred.isOMERO ? "OMERO server" : "OME server";
        String file = "location=[" + type + "] open=[" + cred.server +
          (cred.isOMERO ? ":" + cred.port : "") + "?user=" + cred.username +
          "&password=" + cred.password + "&id=" + images[i] + "] "  + arg;
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

  // -- Helper methods --

  private static String formatServerName(String server) {
    String rtn = server;
    if (rtn.startsWith("http:")) rtn = rtn.substring(5);
    while (rtn.startsWith("/")) rtn = rtn.substring(1);
    int slash = rtn.indexOf("/");
    if (slash >= 0) rtn = rtn.substring(0, slash);
    int colon = rtn.indexOf(":");
    if (colon >= 0) rtn = rtn.substring(0, colon);
    return rtn;
  }

}
