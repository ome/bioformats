//
// PointerTool.java
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

package loci.visbio.overlays;

import java.awt.event.InputEvent;
import loci.visbio.data.TransformEvent;

/** PointerTool is the tool for manipulating existing overlays. */
public class PointerTool extends OverlayTool {

  // -- Fields --

  /** Index of object currently being grabbed. */
  protected int grabIndex = -1;

  /** Location where an object was first "grabbed" with a mouse press. */
  protected float grabX, grabY;


  // -- Constructor --

  /** Constructs an overlay manipulation tool. */
  public PointerTool(OverlayTransform overlay) {
    super(overlay, "Pointer", "Pointer", "pointer.png");
  }


  // -- OverlayTool API methods --

  /** Instructs this tool to respond to a mouse press. */
  public void mouseDown(float x, float y, int[] pos, int mods) {
    boolean shift = (mods & InputEvent.SHIFT_MASK) != 0;
    boolean ctrl = (mods & InputEvent.CTRL_MASK) != 0;

    // pick nearest object
    OverlayObject[] obj = overlay.getObjects(pos);
    double dist = Double.POSITIVE_INFINITY;
    int ndx = -1;
    for (int i=0; i<obj.length; i++) {
      double d = obj[i].getDistance(x, y);
      if (d < dist) {
        dist = d;
        ndx = i;
      }
    }

    double threshold = 0.02 * overlay.getScalingValue();
    boolean selected = dist < threshold && obj[ndx].isSelected();

    if (!shift && !ctrl) {
      // deselect all previously selected objects
      for (int i=0; i<obj.length; i++) obj[i].setSelected(false);
    }

    if (dist < threshold) {
      if (selected && !ctrl && !shift) {
        // grab object if it is already selected
        grabIndex = ndx;
        grabX = x;
        grabY = y;
      }
      // select (or deselect) picked object
      obj[ndx].setSelected(ctrl ? !selected : true);
    }

    ((OverlayWidget) overlay.getControls()).refreshListSelection();
    if (grabIndex >= 0) overlay.setTextDrawn(false);
    overlay.notifyListeners(new TransformEvent(overlay));
  }

  /** Instructs this tool to respond to a mouse release. */
  public void mouseUp(float x, float y, int[] pos, int mods) {
    boolean shift = (mods & InputEvent.SHIFT_MASK) != 0;
    boolean ctrl = (mods & InputEvent.CTRL_MASK) != 0;

    // release any grabbed objects
    if (grabIndex >= 0) {
      grabIndex = -1;
      overlay.setTextDrawn(true);
      overlay.notifyListeners(new TransformEvent(overlay));
    }
  }

  /** Instructs this tool to respond to a mouse drag. */
  public void mouseDrag(float x, float y, int[] pos, int mods) {
    boolean shift = (mods & InputEvent.SHIFT_MASK) != 0;
    boolean ctrl = (mods & InputEvent.CTRL_MASK) != 0;

    // move grabbed object, if any
    if (grabIndex >= 0) {
      OverlayObject obj = overlay.getObjects(pos)[grabIndex];
      float moveX = x - grabX;
      float moveY = y - grabY;
      obj.setCoords(obj.getX() + moveX, obj.getY() + moveY);
      obj.setCoords2(obj.getX2() + moveX, obj.getY2() + moveY);
      grabX = x;
      grabY = y;
      overlay.notifyListeners(new TransformEvent(overlay));
    }
  }

}
