//
// SendToIJPane.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-@year@ Curtis Rueden.

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

package loci.visbio.data;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import loci.visbio.*;
import loci.visbio.view.BioSlideWidget;
import loci.visbio.util.*;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import visad.FlatField;
import visad.VisADException;

/**
 * SendToIJPane provides options for exporting part of a
 * multidimensional data series directly to an ImageJ instance.
 */
public class SendToIJPane extends DialogPane {

  // -- Fields --

  /** Associated VisBio frame (for displaying status). */
  private VisBioFrame bio;

  /** Data object from which exportable data will be derived. */
  private ImageTransform trans;

  /** Slider widgets specifying which dimensional position to send. */
  private BioSlideWidget[] bsw;

  /** Axis to use for ImageJ stack. */
  private BioComboBox stackAxis;

  // -- Constructor --

  /** Creates a dialog for specifying ImageJ export parameters. */
  public SendToIJPane(VisBioFrame bio) {
    super("Send data to ImageJ");
    this.bio = bio;
    bsw = new BioSlideWidget[0];
    stackAxis = new BioComboBox();
    stackAxis.setActionCommand("stackAxis");
  }

  // -- SendToIJPane API methods --

  /** Associates the given data object with the export pane. */
  public void setData(ImageTransform trans) { this.trans = trans; }

  /** Sends the data to ImageJ according to the current input parameters. */
  public void send() {
    final int[] lengths = trans.getLengths();
    final int[] pos = new int[bsw.length];
    for (int i=0; i<bsw.length; i++) pos[i] = bsw[i].getValue();
    TaskManager tm = (TaskManager) bio.getManager(TaskManager.class);
    final BioTask task = tm.createTask("Export " +
      trans.getName() + " to ImageJ");

    new Thread() {
      public void run() {
        try {
          // collect images to send to ImageJ
          task.setStatus(0, 1, "Sending data to ImageJ");
          int axis = stackAxis.getSelectedIndex() - 1;
          FlatField[] data;
          if (axis < 0) {
            task.setStatus("Reading image");
            data = new FlatField[1];
            data[0] = (FlatField) trans.getData(null, pos, 2, null);
            task.setStatus(1, 1);
          }
          else {
            int len = lengths[axis];
            data = new FlatField[len];
            for (int i=0; i<len; i++) {
              task.setStatus(i, len, "Reading image #" + (i + 1) + "/" + len);
              pos[axis] = i;
              data[i] = (FlatField) trans.getData(null, pos, 2, null);
            }
            task.setStatus(len, len);
          }

          // send FlatFields to ImageJ
          task.setStatus("Sending data to ImageJ");
          String title = trans.getName() + " (from VisBio)";
          ImageJUtil.sendToImageJ(title, data, bio);
          task.setCompleted();
        }
        catch (VisADException exc) {
          exc.printStackTrace();
          JOptionPane.showMessageDialog(dialog,
            "Error sending data to ImageJ: " + exc.getMessage(),
            "VisBio", JOptionPane.ERROR_MESSAGE);
        }
      }
    }.start();
  }

  // -- DialogPane API methods --

  /** Lays out components before displaying the dialog. */
  protected int showDialog() {
    if (trans == null) return DialogPane.CANCEL_OPTION;
    removeAll();
    stackAxis.removeActionListener(this);
    stackAxis.removeAllItems();
    stackAxis.addItem("None");
    int[] lengths = trans.getLengths();
    String[] dims = trans.getDimTypes();
    bsw = new BioSlideWidget[lengths.length];
    if (bsw.length > 0) {
      StringBuffer sb = new StringBuffer();
      for (int i=0; i<bsw.length; i++) {
        bsw[i] = new BioSlideWidget(trans, i);
        stackAxis.addItem("<" + (i + 1) + "> " + dims[i]);
        if (i > 0) sb.append(", 3dlu, ");
        sb.append("pref");
      }
      stackAxis.addActionListener(this);
      sb.append(", 3dlu, pref");
      PanelBuilder builder = new PanelBuilder(new FormLayout(
        "pref:grow, 3dlu, pref", sb.toString()));
      CellConstraints cc = new CellConstraints();
      for (int i=0; i<bsw.length; i++) {
        builder.add(bsw[i], cc.xyw(1, 2 * i + 1, 3));
      }
      int row = 2 * bsw.length + 1;
      builder.addLabel("Stack &axis",
        cc.xy(1, row, "right, center")).setLabelFor(stackAxis);
      builder.add(stackAxis, cc.xy(3, row));
      add(builder.getPanel());
    }
    return super.showDialog();
  }

  // -- ActionListener API methods --

  public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if (cmd.equals("stackAxis")) {
      int axis = stackAxis.getSelectedIndex() - 1;
      for (int i=0; i<bsw.length; i++) bsw[i].setEnabled(i != axis);
    }
    else super.actionPerformed(e);
  }

}
