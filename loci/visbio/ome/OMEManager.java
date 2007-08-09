//
// OMEManager.java
//

/*
VisBio application for visualization of multidimensional biological
image data. Copyright (C) 2002-@year@ Curtis Rueden and Abraham Sorber.

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

package loci.visbio.ome;

import java.awt.Component;
import javax.swing.JOptionPane;
import loci.formats.StatusEvent;
import loci.formats.StatusListener;
import loci.visbio.*;
import loci.visbio.data.*;
import loci.visbio.help.HelpManager;

/**
 * OMEManager is the manager for interaction between
 * VisBio and the Open Microscopy Environment.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/visbio/ome/OMEManager.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/visbio/ome/OMEManager.java">SVN</a></dd></dl>
 */
public class OMEManager extends LogicManager {

  // -- Fields --

  /** OME login pane. */
  private OMELoginPane loginPane;

  // -- Constructor --

  /** Constructs an OME manager. */
  public OMEManager(VisBioFrame bio) { super(bio); }

  // -- OMEManager API methods --

  /** Brings up a dialog box allowing upload of the selected dataset. */
  public void upload(Component c) {
    // get currently selected dataset to upload
    DataManager dm = (DataManager) bio.getManager(DataManager.class);
    DataTransform dt = dm.getSelectedData();
    if (dt == null || !(dt instanceof Dataset)) {
      JOptionPane.showMessageDialog(bio, "Please select a dataset to upload.",
        "VisBio", JOptionPane.ERROR_MESSAGE);
      return;
    }
    final Dataset data = (Dataset) dt;

    // get OME server, username and password
    int rval = loginPane.showDialog(c);
    if (rval != OMELoginPane.APPROVE_OPTION) return;
    final String server = loginPane.getServer();
    final String user = loginPane.getUser();
    final String password = loginPane.getPassword();

    // get pixel type
    String int8 = "8-bit integer";
    String int16 = "16-bit integer";
    String int32 = "32-bit integer";
    String float32 = "32-bit floating point";
    String pixelType = (String) JOptionPane.showInputDialog(bio,
      "Please specify pixel type", "VisBio", JOptionPane.INFORMATION_MESSAGE,
      null, new Object[] {int8, int16, int32, float32}, int32);
    if (pixelType == null) return;
    final int bytesPerPix;
    final boolean isFloat;
    if (pixelType == int8) { bytesPerPix = 1; isFloat = false; }
    else if (pixelType == int16) { bytesPerPix = 2; isFloat = false; }
    else if (pixelType == float32) { bytesPerPix = 4; isFloat = true; }
    else { bytesPerPix = 4; isFloat = false; } // pixelType == int32

    int val = JOptionPane.showConfirmDialog(c, "Upload " +
      data.getName() + " as " + pixelType + " data to server " + server +
      " as user " + user + "?", "VisBio", JOptionPane.YES_NO_OPTION);
    if (val != JOptionPane.YES_OPTION) return;

    // upload data to OME in a new thread
    final ImageUploader uploader = new ImageUploader();
    TaskManager tm = (TaskManager) bio.getManager(TaskManager.class);
    final BioTask task = tm.createTask("Upload " + dt.getName() + " to OME");
    uploader.addStatusListener(new StatusListener() {
      public void statusUpdated(StatusEvent e) {
        // update task with latest database communication status
        int value = e.getProgressValue();
        int max = e.getProgressMaximum();
        String msg = e.getStatusMessage();
        if (msg.endsWith("...")) msg = msg.substring(0, msg.length() - 3);
        task.setStatus(value, max, msg);
      }
    });
    new Thread(new Runnable() {
      public void run() {
        uploader.upload(data, server, user, password);
        task.setCompleted();
      }
    }).start();
  }

  /** Gets pane for logging into an OME server. */
  public OMELoginPane getLoginPane() { return loginPane; }

  // -- Static OMEManager API methods --

  /** Gets a standardized server string for the given server. */
  public static String getProperServer(String server) {
    if (server.startsWith("http:")) {
      server = server.substring(5);
    }
    while (server.startsWith("/")) server = server.substring(1);
    int slash = server.indexOf("/");
    if (slash >= 0) server = server.substring(0, slash);
    int colon = server.indexOf(":");
    if (colon >= 0) server = server.substring(0, colon);

    return "http://" + server + "/shoola/";
  }

  // -- LogicManager API methods --

  /** Called to notify the logic manager of a VisBio event. */
  public void doEvent(VisBioEvent evt) {
    int eventType = evt.getEventType();
    if (eventType == VisBioEvent.LOGIC_ADDED) {
      LogicManager lm = (LogicManager) evt.getSource();
      if (lm == this) doGUI();
    }
  }

  /** Gets the number of tasks required to initialize this logic manager. */
  public int getTasks() { return 3; }

  // -- Helper methods --

  /** Adds OME-related GUI components to VisBio. */
  private void doGUI() {
    // login pane
    bio.setSplashStatus("Initializing OME logic");
    loginPane = new OMELoginPane();

    // OME image transform registration
    bio.setSplashStatus(null);
    DataManager dm = (DataManager) bio.getManager(DataManager.class);
    dm.registerDataType(OMEImage.class, "OME image");

    // help window
    bio.setSplashStatus(null);
    HelpManager hm = (HelpManager) bio.getManager(HelpManager.class);
    hm.addHelpTopic("Data transforms/OME images", "ome_image.html");
  }

}
