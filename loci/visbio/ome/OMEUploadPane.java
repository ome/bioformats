//
// OMEUploadPane.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2004 Curtis Rueden.

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

import com.jgoodies.forms.builder.PanelBuilder;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import loci.visbio.VisBioFrame;

import loci.visbio.data.DataManager;
import loci.visbio.data.DataTransform;
import loci.visbio.data.Dataset;

import loci.visbio.util.DialogPane;

/** OMEUploadPane is the control panel for uploading data to OME. */
public class OMEUploadPane extends DialogPane {

  // -- Fields --

  /** Associated OME logic manager. */
  protected OMEManager om;


  // -- GUI components --

  /** Text field for OME server. */
  protected JTextField serv;

  /** Text field for OME username. */
  protected JTextField user;

  /** Text field for OME password. */
  protected JPasswordField pass;

  /** Progress bar for current upload operation. */
  protected JProgressBar progress;


  // -- Constructor --

  /** Constructs a tool panel for uploading data to OME. */
  public OMEUploadPane(OMEManager ome, JProgressBar progress) {
    super("Upload to OME");
    om = ome;
    this.progress = progress;

    // server URL
    serv = new JTextField(20);
    serv.setToolTipText("The OME server address for data upload");

    // username
    user = new JTextField(8);
    user.setToolTipText("The OME username under which to upload");

    // password
    pass = new JPasswordField(8);
    pass.setToolTipText("Password for the specified OME username");

    // lay out components
    PanelBuilder builder = new PanelBuilder(new FormLayout(
      "right:pref, 3dlu, pref:grow, 5dlu, pref, 3dlu, pref:grow",
      "pref, 3dlu, pref"));
    CellConstraints cc = new CellConstraints();

    builder.addLabel("&Server", cc.xy(1, 1)).setLabelFor(serv);
    builder.add(serv, cc.xyw(3, 1, 5));

    builder.addLabel("&Username", cc.xy(1, 3)).setLabelFor(user);
    builder.add(user, cc.xy(3, 3));
    builder.addLabel("&Password", cc.xy(5, 3)).setLabelFor(pass);
    builder.add(pass, cc.xy(7, 3));

    add(builder.getPanel());
  }


  // -- OMEUploadPane API methods --

  /** Sets the displayed progress bar maximum value. */
  public void setProgressMaximum(int max) { progress.setMaximum(max); }

  /** Sets the displayed progress bar value. */
  public void setProgressValue(int val) { progress.setValue(val); }

  /** Sets the displayed progress bar string. */
  public void setStatus(String s) { progress.setString(s); }


  // -- ActionListener API methods --

  /** Handles button presses. */
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("ok")) {
      VisBioFrame bio = om.getVisBio();

      DataManager dm = (DataManager) bio.getManager(DataManager.class);
      DataTransform dt = dm.getSelectedData();

      if (dt == null || !(dt instanceof Dataset)) {
        JOptionPane.showMessageDialog(bio,
          "Please select a dataset to upload.",
          "VisBio", JOptionPane.ERROR_MESSAGE);
        return;
      }

      String server = serv.getText();
      if (server.startsWith("http:")) {
        server = server.substring(5);
      }
      while (server.startsWith("/")) server = server.substring(1);
      int slash = server.indexOf("/");
      if (slash >= 0) server = server.substring(0, slash);
      int colon = server.indexOf(":");
      if (colon >= 0) server = server.substring(0, colon);

      server = "http://" + server + "/shoola/";

      String username = user.getText();
      String password = new String(pass.getPassword());

      Dataset data = (Dataset) dt;
      int val = JOptionPane.showConfirmDialog(bio, "Upload " +
        data.getName() + " to server " + server + " as user " +
        username + "?", "VisBio", JOptionPane.YES_NO_OPTION);
      if (val == JOptionPane.YES_OPTION) {
        om.upload(data, server, username, password);
      }
    }
    super.actionPerformed(e);
  }

}
