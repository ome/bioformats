/*
 * #%L
 * OME Plugins for ImageJ: a collection of ImageJ plugins
 * including the Download from OME and Upload to OME plugins.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
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

package loci.plugins.ome;

import ij.IJ;
import ij.Macro;
import ij.Prefs;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import loci.common.ReflectException;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.gui.AWTImageTools;
import loci.formats.gui.BufferedImageReader;
import loci.ome.io.OMECredentials;
import loci.ome.io.OMEUtils;
import loci.ome.io.OmeroReader;
import loci.plugins.util.LibraryChecker;
import loci.plugins.util.WindowTools;

/**
 * OMEPlugin is the ImageJ Plugin that allows image import and exports from
 * OME and OMERO servers.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-plugins/src/loci/plugins/ome/OMEPlugin.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-plugins/src/loci/plugins/ome/OMEPlugin.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Philip Huettl pmhuettl at wisc.edu
 * @author Melissa Linkert melissa at glencoesoftware.com
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
    if (!LibraryChecker.checkJava() || !LibraryChecker.checkImageJ()) return;
    HashSet missing = new HashSet();
    LibraryChecker.checkLibrary(LibraryChecker.Library.BIO_FORMATS, missing);
    LibraryChecker.checkLibrary(LibraryChecker.Library.OME_JAVA_XML, missing);
    LibraryChecker.checkLibrary(LibraryChecker.Library.OME_JAVA_DS, missing);
    LibraryChecker.checkLibrary(LibraryChecker.Library.FORMS, missing);
    if (!LibraryChecker.checkMissing(missing)) {
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
    String port = Prefs.get("downloader.port", "1099");
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

    cred = new OMECredentials(server, user, pass);
    cred.port = port;
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
        images = showTable(cred);
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

  private long[] showTable(OMECredentials cred) throws ReflectException {
    long[] ids = OMEUtils.getAllIDs(cred.isOMERO);
    int[] x = OMEUtils.getAllWidths(cred.isOMERO);
    int[] y = OMEUtils.getAllHeights(cred.isOMERO);
    int[] z = OMEUtils.getAllZs(cred.isOMERO);
    int[] c = OMEUtils.getAllChannels(cred.isOMERO);
    int[] t = OMEUtils.getAllTs(cred.isOMERO);
    String[] types = OMEUtils.getAllTypes(cred.isOMERO);
    String[] names = OMEUtils.getAllNames(cred.isOMERO);
    String[] descr = OMEUtils.getAllDescriptions(cred.isOMERO);
    String[] created = OMEUtils.getAllDates(cred.isOMERO);
    BufferedImage[] thumbs = OMEUtils.getAllThumbnails(cred.isOMERO);

    if (ids.length == 0) {
      IJ.error("No images found!");
      return ids;
    }

    GenericDialog gd = new GenericDialog("OME/OMERO Plugin");
    GridBagLayout gdl = (GridBagLayout) gd.getLayout();
    GridBagConstraints gbc = new GridBagConstraints();

    gbc.gridx = 2;
    gbc.gridwidth = GridBagConstraints.REMAINDER;

    Panel[] p = new Panel[ids.length];
    String[] tips = new String[ids.length];
    for (int i=0; i<ids.length; i++) {
      if (names[i] == null) names[i] = "";

      StringBuffer tip = new StringBuffer();
      tip.append("<HTML>Name: ");
      tip.append(names[i]);
      tip.append("<BR>ID: ");
      tip.append(ids[i]);
      tip.append("<BR>Date Created: ");
      tip.append(created[i]);
      tip.append("<BR>Pixel type: ");
      tip.append(types[i]);
      tip.append("<BR>SizeX: ");
      tip.append(x[i]);
      tip.append("<BR>SizeY: ");
      tip.append(y[i]);
      tip.append("<BR>SizeZ: ");
      tip.append(z[i]);
      tip.append("<BR>SizeC: ");
      tip.append(c[i]);
      tip.append("<BR>SizeT: ");
      tip.append(t[i]);
      tip.append("<BR>Description: ");
      tip.append(descr[i]);
      tip.append("</HTML>");

      tips[i] = tip.toString();

      if (names[i].indexOf(File.separator) != -1) {
        names[i] = names[i].substring(names[i].lastIndexOf(File.separator) + 1);
      }

      gd.addCheckbox(names[i] + " (" + ids[i] + ")", false);
      p[i] = new Panel();
      if (cred.isOMERO) {
        p[i].add(Box.createRigidArea(new Dimension(128, 128)));
        gbc.gridy = i;
      }
      else {
        gbc.gridy = i;
        if (thumbs[i] == null) {
          thumbs[i] = AWTImageTools.blankImage(64, 64, 1, FormatTools.UINT8);
        }
        JLabel label = new JLabel(new ImageIcon(thumbs[i]));
        label.setToolTipText(tips[i]);
        p[i].add(label);
      }
      gdl.setConstraints(p[i], gbc);
      gd.add(p[i]);
    }
    WindowTools.addScrollBars(gd);
    if (cred.isOMERO) {
      OMEROLoader l = new OMEROLoader(ids, cred, p, gd, tips);
      gd.showDialog();
      l.stop();
    }
    else gd.showDialog();
    if (gd.wasCanceled()) return null;

    boolean[] checked = new boolean[ids.length];
    int numChecked = 0;
    for (int i=0; i<ids.length; i++) {
      checked[i] = gd.getNextBoolean();
      if (checked[i]) numChecked++;
    }

    long[] results = new long[numChecked];
    int n = 0;
    for (int i=0; i<ids.length; i++) {
      if (checked[i]) results[n++] = ids[i];
    }
    return results;
  }

  // -- Helper class --

  class OMEROLoader implements Runnable {
    private long[] ids;
    private Panel[] p;
    private GenericDialog gd;
    private boolean stop;
    private Thread loader;
    private OMECredentials cred;
    private String[] tips;

    public OMEROLoader(long[] ids, OMECredentials cred, Panel[] p,
      GenericDialog gd, String[] tips)
    {
      this.ids = ids;
      this.p = p;
      this.gd = gd;
      this.cred = cred;
      this.tips = tips;

      loader = new Thread(this, "OMERO-ThumbLoader");
      loader.start();
    }

    public void stop() {
      if (loader == null) return;
      stop = true;
      try {
        loader.join();
        loader = null;
      }
      catch (InterruptedException exc) {
        exc.printStackTrace();
      }
    }

    public void run() {
      try {
        OmeroReader r = new OmeroReader();
        BufferedImageReader bir = new BufferedImageReader(r);
        for (int i=0; i<ids.length; i++) {
          bir.setId("server=" + cred.server + "\nusername=" + cred.username +
            "\npassword=" + cred.password + "\nport=" + cred.port + "\nid=" +
            ids[i]);
          BufferedImage thumb = bir.openThumbImage(0);
          ImageIcon icon = new ImageIcon(thumb);
          p[i].removeAll();
          JLabel label = new JLabel(icon);
          label.setToolTipText(tips[i]);
          p[i].add(label);
          if (gd != null) gd.validate();
        }
      }
      catch (FormatException exc) {
        exc.printStackTrace();
      }
      catch (IOException exc) {
        exc.printStackTrace();
      }
    }

  }

}
