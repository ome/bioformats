//
// OMEManager.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2005 Curtis Rueden.

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
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import loci.visbio.*;
import loci.visbio.data.*;

/**
 * OMEManager is the manager for interaction between
 * VisBio and the Open Microscopy Environment.
 */
public class OMEManager extends LogicManager implements TaskListener {

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

    int val = JOptionPane.showConfirmDialog(c, "Upload " +
      data.getName() + " to server " + server + " as user " +
      user + "?", "VisBio", JOptionPane.YES_NO_OPTION);
    if (val != JOptionPane.YES_OPTION) return;

    // upload data to OME in a new thread
    final ImageUploader uploader = new ImageUploader();
    uploader.addTaskListener(this);
    new Thread(new Runnable() {
      public void run() { uploader.upload(data, server, user, password); }
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
  public int getTasks() { return 2; }


  // -- TaskListener API methods --

  /** Called when an OME-related task is updated. */
  public void taskUpdated(TaskEvent e) {
    // update VisBio progress bar with latest database communication status
    final JProgressBar progress = bio.getProgressBar();
    final int value = e.getProgressValue();
    final int max = e.getProgressMaximum();
    final String message = e.getStatusMessage();
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        progress.setMaximum(max);
        progress.setValue(value);
        progress.setString(message);
      }
    });
  }


  // -- Helper methods --

  /** Adds OME-related GUI components to VisBio. */
  private void doGUI() {
    // upload pane
    bio.setSplashStatus("Initializing OME logic");
    loginPane = new OMELoginPane();

    // OME image transform registration
    bio.setSplashStatus(null);
    DataManager dm = (DataManager) bio.getManager(DataManager.class);
    dm.registerDataType(OMEImage.class, "OME image");
  }

}
