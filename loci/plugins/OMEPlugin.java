//
// OMEPlugin.java
//

/*
OME Plugin for ImageJ plugin for transferring images to and from an OME
database. Copyright (C) 2004-@year@ Philip Huettl and Melissa Linkert.

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
import ij.process.*;
import java.awt.TextField;
import java.util.*;
import loci.ome.util.OMEUtils;
import loci.plugins.Util;
import org.openmicroscopy.ds.*;
import org.openmicroscopy.ds.dto.*;
import org.openmicroscopy.ds.st.*;
import org.openmicroscopy.is.*;

/**
 * OMEPlugin is the ImageJ Plugin that allows image import and exports from
 * the OME database.
 *
 * @author Philip Huettl pmhuettl at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 */
public class OMEPlugin implements PlugIn {


  // -- Fields --

  /** Current OME thread. */
  private Thread upThread;

  /** Current OME data. */
  private ImageProcessor data;
  private ImagePlus imageP;

  /** Current OME server. */
  private String server;

  /** Current OME username. */
  private String username;

  /** Current OME password. */
  private String password;

  /** Image Stack space or time domain.*/
  private int domainIndex;

  /** Signals exit of plugin */
  private static boolean cancelPlugin;

  /** Whether or not we're uploading. */
  private boolean upload;

  /** Data for the current image. */
  private DataFactory df;
  private PixelsFactory pf;
  private RemoteCaller rc;
  private DataServices rs;

  // -- PlugIn API methods --

  /** Executes the plugin. */
  public void run(String arg) {
    if (!Util.checkVersion()) return;
    if (!Util.checkLibraries(true, true, true, true)) return;
    runPlugin();
  }

  // -- OMEPlugin API methods --

  /**
   * The getInput method prompts and receives user input to determine
   * the OME login fields and whether the stack is in the time or space domain
   */
  private void getInput() {
    GenericDialog gd = new GenericDialog("OME Login");
    
    String s = Prefs.get("downloader.server", "");
    if (s.startsWith("http:")) s = s.substring(5);
    while (s.startsWith("/")) s = s.substring(1);
    int slash = s.indexOf("/");
    if (slash >= 0) s = s.substring(0, slash);
    int colon = s.indexOf(":");
    if (colon >= 0) s = s.substring(0, colon);
    
    gd.addStringField("Server:   ", s, 30);
    gd.addStringField("Username: ", Prefs.get("downloader.user", ""), 30);
    gd.addStringField("Password: ", "", 30);

    // star out the password field

    Vector v = gd.getStringFields();
    ((TextField) v.get(2)).setEchoChar('*');

    gd.showDialog();
    if (gd.wasCanceled()) {
      cancelPlugin = true;
      return;
    }

    server = gd.getNextString();
  
    // do sanity check on server name
    if (server.startsWith("http:")) {
      server = server.substring(5);
    }
    while (server.startsWith("/")) server = server.substring(1);
    slash = server.indexOf("/");
    if (slash >= 0) server = server.substring(0, slash);
    colon = server.indexOf(":");
    if (colon >= 0) server = server.substring(0, colon);

    Prefs.set("downloader.server", server);

    server = "http://" + server + "/shoola/";
    
    username = gd.getNextString();
    password = gd.getNextString();

    Prefs.set("downloader.user", username);
  }

  public static void setPlugin(boolean isCancelled) {
    cancelPlugin = isCancelled;
  }

  /**
   * Login to the OME database.
   * This code has been adapted from Doug Creager's TestImport example.
   */
  public void login() {
    boolean error = false;
    boolean loggedIn = false;
    try {
      IJ.showProgress(0);
      getInput();
      if (cancelPlugin) {
        return;
      }
      rs = DataServer.getDefaultServices(server);
      rc = rs.getRemoteCaller();
      while(!loggedIn) {
        if(cancelPlugin) {
          return;
        }
        if(error) {
          rs = DataServer.getDefaultServices(server);
          rc = rs.getRemoteCaller();
        }
        rc.login(username, password);
        loggedIn = true;
      }
    }
    catch(Exception e) {
      IJ.error("Input Error", "The login information is not valid.");
      login();
    }
  }

  // Get helper classes.
  public void getHelpers() {
    try {
      IJ.showStatus("OME: Getting image information...");
      IJ.showProgress(.1);
      if(cancelPlugin) {
        return;
      }
      df = (DataFactory) rs.getService(DataFactory.class);
      pf = (PixelsFactory) rs.getService(PixelsFactory.class);
    }
    catch(NullPointerException e) {
      cancelPlugin = true;
      getHelpers();
    }
  }

  /** Logout of OME database */
  public void logout() {
    IJ.showStatus("OME: Logging out...");
    IJ.showProgress(.99);
    rc.logout();
    IJ.showStatus("OME: Completed");
    IJ.showMessage("OME", "OME Transaction Completed");
  }

  // -- Download methods --

  /** returns a list of images that the user chooses */
  private Image[] getDownPicks(Image[] ima) {
    if (cancelPlugin) return null;
   
    try {
      OMEUtils.login(server, username, password);
      int[] results = OMEUtils.showTable(ima);
      if (results == null) {
        cancelPlugin = true;
        return null;
      }
      Image[] returns = new Image[results.length];
      for (int i=0; i<returns.length; i++) {
        Criteria c = new Criteria();
        OMEUtils.setImageCriteria(c);
        c.addFilter("id", "=", "" + results[i]);
        returns[i] = (Image) df.retrieve(Image.class, c);
      }
      return returns;
    }
    catch (Exception e) {
      IJ.setColumnHeadings("Errors");
      IJ.write("An exception has occurred:  \n" + e.toString());
      IJ.showStatus("Error downloading (see error console for details)");
      e.printStackTrace();
    }
    return null;
  }

  /** Does the work for downloading data from OME. */
  public void runPlugin() {
    try {
      login();
      if (cancelPlugin) {
        cancelPlugin = false;
        return;
      }
      getHelpers();

      //get database info to use in search
      IJ.showStatus("Getting database info..");

      Criteria criteria = new Criteria();
      criteria.addWantedField("FirstName");
      criteria.addWantedField("LastName");
      criteria.addOrderBy("LastName");

      List l = df.retrieveList("Experimenter", criteria);
      String[][] owners = new String[2][l.size() + 1];
      owners[0][0] = "All";
      owners[1][0] = "0";
      for (int i=1; i<=l.size(); i++) {
        Experimenter e = (Experimenter) l.get(i - 1);
        owners[0][i] = e.getFirstName() + " " + e.getLastName();
        owners[1][i] = "" + e.getID();
      }

      criteria = new Criteria();
      criteria.addWantedField("id");
      criteria.addWantedField("name");
      criteria.addWantedField("datasets");
      criteria.addWantedField("datasets", "id");
      criteria.addWantedField("datasets", "name");
      criteria.addWantedField("datasets", "images");

      FieldsSpecification fs = new FieldsSpecification();
      fs.addWantedField("id");
      fs.addWantedField("experimenter");
      fs.addWantedField("experimenter", "id");

      Experimenter user = df.getUserState(fs).getExperimenter();

      criteria.addFilter("owner_id", new Integer(user.getID()));
      criteria.addOrderBy("name");
      l = df.retrieveList(Project.class, criteria);
      Project[] projects = (Project[]) l.toArray(new Project[0]);
      String[] projectNames = new String[projects.length + 1];
      projectNames[0] = "All";
      for (int i=1; i<projectNames.length; i++) {
        projectNames[i] = projects[i - 1].getName();
      }

      //create search panel
      IJ.showStatus("Creating search panel...");
      GenericDialog gd = new GenericDialog("OME Download Search");
      gd.addChoice("Project:    ", projectNames, "All");
      gd.addChoice("Owner:      ", owners[0], "All");
      gd.addStringField("Image ID:   ", "", 30);
      gd.addStringField("Image Name: ", "", 30);
      gd.showDialog();
      if (gd.wasCanceled()) {
        return;
      }

      Image[] images = new Image[0];

      //do the image search
      IJ.showStatus("Searching for images...");
     
      String project = gd.getNextChoice();
      String owner = gd.getNextChoice();
      String img = gd.getNextString();
      String imageName = gd.getNextString();
     
      while (images.length == 0) {
        //get search results

        if (img == null || img.length() == 0) img = "0";
        int imageId = new Integer(img).intValue();

        Criteria c = new Criteria();
        OMEUtils.setImageCriteria(c);

        if (!owner.equals("All")) {
          // TODO : add filters
        }

        if (!project.equals("All")) {
          // TODO : add filters
        }

        l = df.retrieveList(Image.class, c);
        images = (Image[]) l.toArray(new Image[0]);

        if(images == null) images = new Image[0];
        if (images.length == 0) {
          IJ.showMessage("OME Download",
            "No images matched the specified criteria.");
          return;
        }
        else {
          //pick from results
          images = getDownPicks(images);
          if (cancelPlugin) {
            return;
          }
        }
        if (images == null) {
          logout();
          return;
        }
      }
      //download into ImageJ
      for (int i=0; i<images.length; i++) {
        String file = server + "?user=" + username + "&password=" + password +
          "&id=" + images[i].getID();
        IJ.runPlugIn("loci.plugins.LociImporter", file);

        if (cancelPlugin) {
          return;
        }
      }
      logout();
    }
    catch(NullPointerException e) {
      e.printStackTrace();
    }
    catch(IllegalArgumentException f) {
      // do nothing; this means that the user cancelled the login procedure
    }
    catch (Exception exc) {
      IJ.setColumnHeadings("Errors");
      IJ.write("An exception has occurred:  \n" + exc.toString());
      IJ.showStatus("Error uploading (see error console for details)");
      exc.printStackTrace();
    }

    upThread = null;
    IJ.showProgress(1);
  }


}
