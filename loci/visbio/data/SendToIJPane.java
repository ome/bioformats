//
// SendToIJPane.java
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

package loci.visbio.data;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;
import loci.visbio.VisBioFrame;
import loci.visbio.view.BioSlideWidget;
import loci.visbio.util.ImageJUtil;
import loci.visbio.util.DialogPane;
import javax.swing.JProgressBar;
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


  // -- Constructor --

  /** Creates a dialog for specifying ImageJ export parameters. */
  public SendToIJPane(VisBioFrame bio) {
    super("Send data to ImageJ");
    this.bio = bio;
    bsw = new BioSlideWidget[0];
  }


  // -- SendToIJPane API methods --

  /** Associates the given data object with the export pane. */
  public void setData(ImageTransform trans) { this.trans = trans; }

  /** Sends the data to ImageJ according to the current input parameters. */
  public void send() {
    final int[] lengths = trans.getLengths();
    final int[] pos = new int[bsw.length];
    for (int i=0; i<bsw.length; i++) pos[i] = bsw[i].getValue();
    final JProgressBar progress = bio.getProgressBar();
    progress.setString("Sending data to ImageJ");
    progress.setValue(0);
    progress.setMaximum(1);

    Thread t = new Thread(new Runnable() {
      public void run() {
        try {
          progress.setString("Reading image #1/1");
          FlatField[] data = new FlatField[1];
          data[0] = (FlatField) trans.getData(pos, 2, null);
          progress.setValue(1);
          ImagePlus image;
          String name = trans.getName() + " (from VisBio)";
          if (data.length > 1) {
            // create image stack
            ImageStack is = null;
            for (int i=0; i<data.length; i++) {
              ImageProcessor ips = ImageJUtil.extractImage(data[i]);
              if (is == null) {
                is = new ImageStack(ips.getWidth(), ips.getHeight(),
                  ips.getColorModel());
              }
              is.addSlice("" + i, ips);
            }
            image = new ImagePlus(name, is);
          }
          else {
            // create single image
            image = new ImagePlus(name, ImageJUtil.extractImage(data[0]));
          }
          ImageJUtil.sendToImageJ(image, bio);
          bio.resetStatus();
        }
        catch (VisADException exc) {
          bio.resetStatus();
          exc.printStackTrace();
          JOptionPane.showMessageDialog(dialog,
            "Error sending data to ImageJ: " + exc.getMessage(),
            "VisBio", JOptionPane.ERROR_MESSAGE);
        }
      }
    });
    t.start();
  }


  // -- DialogPane API methods --

  /** Lays out components before displaying the dialog. */
  protected int showDialog() {
    pane.removeAll();
    if (trans == null) return DialogPane.CANCEL_OPTION;
    int[] lengths = trans.getLengths();
    bsw = new BioSlideWidget[lengths.length];
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<lengths.length; i++) {
      bsw[i] = new BioSlideWidget(trans, i);
      if (i > 0) sb.append(", 3dlu, ");
      sb.append("pref");
    }
    if (bsw.length > 0) {
      PanelBuilder builder = new PanelBuilder(new FormLayout(
        "pref", sb.toString()));
      CellConstraints cc = new CellConstraints();
      for (int i=0; i<bsw.length; i++) {
        builder.add(bsw[i], cc.xy(1, 2 * i + 1));
      }
      pane.add(builder.getPanel());
    }
    return super.showDialog();
  }

}
