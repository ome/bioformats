//
// ColorPanel.java
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

package loci.visbio.view;

import com.jgoodies.forms.factories.ButtonBarFactory;

import java.awt.BorderLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JPanel;

import loci.visbio.data.DataTransform;
import loci.visbio.data.ImageTransform;
import loci.visbio.data.ThumbnailHandler;

import loci.visbio.util.VisUtil;

import visad.DisplayImpl;
import visad.VisADException;

/** Provides GUI controls for a display color handler. */
public class ColorPanel extends JPanel implements ActionListener {

  // -- Fields --

  /** Color handler upon which GUI controls operate. */
  protected ColorHandler handler;

  /** Color dialog pane. */
  protected ColorPane colorPane;


  // -- Constructor --

  /** Creates a panel containing color handler GUI controls. */
  public ColorPanel(ColorHandler h) {
    super();
    handler = h;
    colorPane = new ColorPane(handler);

    // edit colors button
    JButton colors = new JButton("Colors");
    colors.setMnemonic('c');
    colors.setToolTipText("Edits the displayed data's color scheme");
    colors.addActionListener(this);

    // lay out components
    setLayout(new BorderLayout());
    add(ButtonBarFactory.buildLeftAlignedBar(colors));
  }


  // -- ColorPanel API methods --

  /**
   * Displays the color dialog pane onscreen and
   * alters the color scheme as requested.
   */
  public void showColorDialog() {
    refreshPreviewData();
    DisplayDialog dialog = handler.getDialog();
    if (colorPane.showDialog(dialog) == ColorPane.APPROVE_OPTION) {
      DisplayImpl d = dialog.getDisplay();
      VisUtil.setDisplayDisabled(d, true);
      handler.setParameters(colorPane.getBrightness(),
        colorPane.getContrast(), colorPane.getModel(), colorPane.getRed(),
        colorPane.getGreen(), colorPane.getBlue(), false);
      handler.setRanges(colorPane.getLo(), colorPane.getHi(),
        colorPane.getFixed());
      handler.setTables(colorPane.getTables());
      VisUtil.setDisplayDisabled(d, false);
    }
  }

  /** Refreshes preview data from the transform handler. */
  public void refreshPreviewData() {
    TransformHandler th = handler.getDialog().getTransformHandler();
    DataTransform[] dt = th.getTransforms();
    ImageTransform it = null;
    ThumbnailHandler thumbs = null;
    int[] pos = null;
    for (int i=0; i<dt.length; i++) {
      if (dt[i] instanceof ImageTransform) {
        it = (ImageTransform) dt[i];
        thumbs = dt[i].getThumbHandler();
        pos = th.getPos(dt[i]);
        break;
      }
    }

    if (it != null && thumbs != null) {
      try { colorPane.setPreviewData(thumbs.getThumb(pos)); }
      catch (VisADException exc) { exc.printStackTrace(); }
      catch (RemoteException exc) { exc.printStackTrace(); }
    }
  }

  /** Gets the color pane. */
  public ColorPane getColorPane() { return colorPane; }


  // -- ActionListener API methods --

  /** Handles button presses and combo box selections. */
  public void actionPerformed(ActionEvent e) { showColorDialog(); }

}
