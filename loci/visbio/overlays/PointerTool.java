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

  /** Location where mouseDown occurs */
  protected float downX, downY; 

  /** The selection box which may be created by this tool */
  protected TransientSelectBox select;

  /** Array of OverlayObjects at current dimensional position */
  protected OverlayObject[] objs;

  /** Array of the bounds of the OverlayObjects at the current dimensional position */
  protected float[][] bounds;

  // -- Constructor --

  /** Constructs an overlay manipulation tool. */
  public PointerTool(OverlayTransform overlay) {
    super(overlay, "Pointer", "Pointer", "pointer.png");
    bounds = null;
  }

  // -- OverlayTool API methods --

  /** Instructs this tool to respond to a mouse press. */
  public void mouseDown(float x, float y, int[] pos, int mods) {
    boolean shift = (mods & InputEvent.SHIFT_MASK) != 0;
    boolean ctrl = (mods & InputEvent.CTRL_MASK) != 0;

    // pick nearest object
    objs = overlay.getObjects(pos);
    bounds = new float[objs.length][4];

    double dist = Double.POSITIVE_INFINITY;
    int ndx = -1;
    for (int i=0; i<objs.length; i++) {
      double d = objs[i].getDistance(x, y);
      if (d < dist) {
        dist = d;
        ndx = i;
      }

      // assemble array of distances
      float[] bound = new float[4];
      bound[0] = objs[i].getX();
      bound[1] = objs[i].getY();
      if (objs[i].hasEndpoint2()) {
        bound[2] = objs[i].getX2();
        bound[3] = objs[i].getY2();
      } else {
        bound[2] = bound[0];
        bound[3] = bound[1];
      }
      bounds[i] = bound; 
    }

    double threshold = 0.02 * overlay.getScalingValue();
    boolean selected = dist < threshold && objs[ndx].isSelected();
    
    if (!shift && !ctrl) {
      // deselect all previously selected objects
      for (int i=0; i<objs.length; i++) objs[i].setSelected(false);
    }

    if (dist < threshold) {
      if (selected && !ctrl && !shift) {
        // grab object if it is already selected
        grabIndex = ndx;
        grabX = x;
        grabY = y;
      }
      // select (or deselect) picked object
      objs[ndx].setSelected(ctrl ? !selected : true);
    } else {
      // record location of click
      downX = x;
      downY = y;

      // instantiate selection box
      select = new TransientSelectBox(overlay, downX, downY);
      overlay.addTSB (select);
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
    } else if (select != null) {
      select = null;
      overlay.removeTSB();
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
    } else if (select != null) {
      // extend selection box
      select.setCorner (x, y);

      // select objects inside the box
      for (int i=0; i<bounds.length; i++) {
        float bx1, bx2, by1, by2;
        float tx1, tx2, ty1, ty2;
        float ox1, ox2, oy1, oy2;
        
        ox1 = bounds[i][0];
        oy1 = bounds[i][1];
        ox2 = bounds[i][2];
        oy2 = bounds[i][3];

        // un-oriented (disoriented?) box coordinates
        tx1 = select.getX1();
        tx2 = select.getX2();
        ty1 = select.getY1();
        ty2 = select.getY2();

        // assign oriented coordinates of seletion box
        if (tx1 < tx2) { bx1 = tx1; bx2 = tx2; }
        else { bx1 = tx2; bx2 = tx1; }
        if (ty1 < ty2) { by1 = ty1; by2 = ty2; }
        else { by1 = ty2; by2 = ty1; }

        System.out.println ("TSB coords: ("+tx1+","+ty1+"),("+tx2+","+ty2+")");
        System.out.println ("TSB adjust: ("+bx1+","+by1+"),("+bx2+","+by2+")");
        System.out.println ("Object coords: ("+ox1+","+oy1+"),("+ox2+","+oy2+")");

        // determine whether object i is inside or outside of selection box 
        if (bx1 < ox1 && ox1 < bx2 && 
            bx1 < ox2 && ox2 < bx2 &&
            by1 < oy1 && oy1 < by2 &&
            by1 < oy2 && oy2 < by2 ) objs[i].setSelected(true);
        else objs[i].setSelected (false);
      }
      overlay.notifyListeners(new TransformEvent(overlay));
    }
  }
}
