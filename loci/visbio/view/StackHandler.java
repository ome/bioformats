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
import visad.*;

/**
 * Provides logic for linking data transforms
 * to a 3D display as a stack of images.
 */
public class StackHandler extends TransformHandler {

  // -- Constants --

  /** Default maximum resolution for images in a stack. */
  public static final int DEFAULT_STACK_RESOLUTION = 192;

  /** Starting FPS for animation for image stacks. */
  public static final int STACK_ANIMATION_RATE = 2;

  /** Minimum length of each volume rendered spatial axis. */
  public static final int MIN_VOLUME_RESOLUTION = 2;

  /** Maximum length of each volume rendered spatial axis. */
  public static final int MAX_VOLUME_RESOLUTION = 160;

  /** Default length of each volume rendered spatial axis. */
  public static final int DEFAULT_VOLUME_RESOLUTION = 96;


  // -- Fields --

  /** Dimensional positions. */
  protected Vector positions;

  /**
   * Whether each transform is currently displaying thumbnails
   * vs full-resolution data.
   */
  protected Vector thumbnails;

  /** Resolution of stack images. */
  protected int stackRes;


  // -- Constructor --

  /** Creates a 3D display transform handler. */
  public StackHandler(DisplayWindow dw) {
    super(dw);
    positions = new Vector();
    thumbnails = new Vector();
    stackRes = DEFAULT_STACK_RESOLUTION;
  }


  // -- StackHandler API methods --

  /** Sets maximum resolution per axis of stacked images. */
  public void setStackResolution(int res) { stackRes = res; }

  /** Gets maximum resolution per axis of stacked images. */
  public int getStackResolution() { return stackRes; }


  // -- TransformHandler API methods --

  /** Links the given data transform to the display. */
  public void addTransform(DataTransform trans) {
    TransformLink link;
    if (trans instanceof ImageTransform && !trans.isValidDimension(3)) {
      link = new StackLink(this, trans);
    }
    else link = new TransformLink(this, trans);
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
    // create default Z axis mappings for use with yellow bounding boxes
    Display display = window.getDisplay();
    int size = links.size();
    for (int i=0; i<size; i++) {
      TransformLink link = (TransformLink) links.elementAt(i);
      DataTransform trans = link.getTransform();
      if (!(trans instanceof ImageTransform)) continue;
      RealType zType = ((ImageTransform) trans).getZType();
      ScalarMap zMap = new ScalarMap(zType, Display.ZAxis);
      zMap.setRange(-1, 1);
      display.addMap(zMap);
    }
  }

}
