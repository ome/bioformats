//
// StackHandler.java
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

import java.rmi.RemoteException;

import java.util.Vector;

import loci.visbio.data.DataTransform;
import loci.visbio.data.ImageTransform;

import visad.Display;
import visad.RealType;
import visad.ScalarMap;
import visad.VisADException;

/**
 * Provides logic for linking data transforms
 * to a 3D display as a stack of images.
 */
public class StackHandler extends TransformHandler {

  // -- Constants --

  /** Starting FPS for animation for image stacks. */
  public static final int STACK_ANIMATION_RATE = 2;

  public static final RealType ZBOX = RealType.getRealType("zbox");



  // -- Fields --

  /** Dimensional positions. */
  protected Vector positions;

  /**
   * Whether each transform is currently displaying thumbnails
   * vs full-resolution data.
   */
  protected Vector thumbnails;

  /** Maximum resolution of stack images. */
  protected int maxResolution;


  // -- Constructor --

  /** Creates a 3D display transform handler. */
  public StackHandler(DisplayWindow dw) {
    super(dw);
    positions = new Vector();
    thumbnails = new Vector();
    maxResolution = 192;
  }


  // -- StackHandler API methods --

  /** Sets maximum resolution per axis of stacked images. */
  public void setMaximumResolution(int res) { maxResolution = res; }

  /** Gets maximum resolution per axis of stacked images. */
  public int getMaximumResolution() { return maxResolution; }

  /** Gets custom Z axis mapping. */
  public RealType getZType() { return ZBOX; }


  // -- TransformHandler API methods --

  /** Links the given data transform to the display. */
  public void addTransform(DataTransform trans) {
    TransformLink link;
    if (trans.isValidDimension(3) || !(trans instanceof ImageTransform)) {
      link = new TransformLink(this, trans);
    }
    else link = new StackLink(this, trans);
    links.add(link);
    rebuild();
    panel.addTransform(trans);
  }


  // -- Internal TransformHandler API methods --

  /** Constructs GUI controls for the stack handler. */
  protected void makePanel() {
    fps = STACK_ANIMATION_RATE;
    panel = new StackPanel(this);
  }

  /** Adds any required custom mappings to the display. */
  protected void doCustomMaps() throws VisADException, RemoteException {
    // create a default Z axis mapping for use with yellow bounding boxes
    ScalarMap zmap = new ScalarMap(ZBOX, Display.ZAxis);
    zmap.setRange(-1, 1);
    window.getDisplay().addMap(zmap);
  }

}
