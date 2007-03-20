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
import java.util.Vector;
import loci.visbio.data.TransformEvent;
import loci.visbio.util.MathUtil;
import visad.DisplayEvent;
import visad.DisplayImpl;

/** PointerTool is the tool for manipulating existing overlays. */
public class PointerTool extends OverlayTool {

  // -- Fields --

  /** Location where an object was first "grabbed" with a mouse press. */
  protected float grabX, grabY;

  /** Vector of "grabbed" objects */
  protected Vector grabbed;  // the grabees 

  /** The selection box which may be created by this tool */
  protected TransientSelectBox select;

  /** Array of OverlayObjects at current dimensional position */
  protected OverlayObject[] objs;

  /**
   * Array of the bounds of the OverlayObjects
   * at the current dimensional position.
   */
  protected float[][] bounds;

  /** Array of whether objects are selected at time of mouse press */
  protected boolean[] selected;
  
  // -- Constructor --

  /** Constructs an overlay manipulation tool. */
  public PointerTool(OverlayTransform overlay) {
    super(overlay, "Pointer", "Pointer", "pointer.png");
    bounds = null;
  }

  // -- OverlayTool API methods --

  /** Instructs this tool to respond to a mouse press. */
  public void mouseDown(DisplayEvent e, int px, int py,
    float dx, float dy, int[] pos, int mods)
  {
    boolean shift = (mods & InputEvent.SHIFT_MASK) != 0;
    boolean ctrl = (mods & InputEvent.CTRL_MASK) != 0;

    DisplayImpl display = (DisplayImpl) e.getDisplay();
    
    // pick nearest object
    objs = overlay.getObjects(pos);
    bounds = new float[objs.length][4];
    selected = new boolean[objs.length];
    grabbed = new Vector(objs.length);

    double dist = Double.POSITIVE_INFINITY;
    int ndx = -1;
    for (int i=0; i<objs.length; i++) {
      double d = objs[i].getDistance(dx, dy);
      if (d < dist) {
        dist = d;
        ndx = i;
      }
    }

    double threshold = 0.02 * overlay.getScalingValue();

    if (dist < threshold) {
      if (shift) objs[ndx].setSelected(true);
      else if (ctrl) objs[ndx].setSelected(ctrl ? !objs[ndx].isSelected() : true);
      else {
        if (objs[ndx].isSelected()) {
          // grab all selected objects
          for (int i=0; i<objs.length; i++) {
            if (objs[i].isSelected()) grabbed.add(objs[i]);
          }
          // record location of mouseDown
          grabX = dx;
          grabY = dy;
        }
        else {
          // select this object and deselect all others
          for (int i=0; i<objs.length; i++) objs[i].setSelected(false);
          objs[ndx].setSelected(true);
        }
      }
    }
    else {
      if (!ctrl && !shift) {
        // deselect all previously selected objects
        for (int i=0; i<objs.length; i++) objs[i].setSelected(false);
      }
    
      // assemble array of objects' bounding boxes 
      for (int i=0; i<objs.length; i++) {
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
    
      // compile array tracking initial selection state of all objs here 
      for (int i=0; i<objs.length; i++) { selected[i] = objs[i].isSelected(); }

      // instantiate selection box
      select = new TransientSelectBox(overlay, display, px, py);
      overlay.addTSB (select);
    }

    ((OverlayWidget) overlay.getControls()).refreshListSelection();
    if (!grabbed.isEmpty() || select != null) overlay.setTextDrawn(false);
    overlay.notifyListeners(new TransformEvent(overlay));
  }

  /** Instructs this tool to respond to a mouse release. */
  public void mouseUp(DisplayEvent e, int px, int py,
    float dx, float dy, int[] pos, int mods)
  {
    boolean shift = (mods & InputEvent.SHIFT_MASK) != 0;
    boolean ctrl = (mods & InputEvent.CTRL_MASK) != 0;

    // release any grabbed objects
    if (!grabbed.isEmpty()) {
      grabbed.clear();
      overlay.setTextDrawn(true);
      overlay.notifyListeners(new TransformEvent(overlay));
    }
    else if (select != null) {
      select = null;
      overlay.removeTSB();
      overlay.setTextDrawn(true);
      overlay.notifyListeners(new TransformEvent(overlay));
    }
  }

  /** Instructs this tool to respond to a mouse drag. */
  public void mouseDrag(DisplayEvent e, int px, int py,
    float dx, float dy, int[] pos, int mods)
  {
    boolean shift = (mods & InputEvent.SHIFT_MASK) != 0;
    boolean ctrl = (mods & InputEvent.CTRL_MASK) != 0;

    boolean selectionStateChanged = false;

    // move grabbed object, if any
    if (!grabbed.isEmpty()) {
      float moveX = dx - grabX;
      float moveY = dy - grabY;
      for (int i=0; i<grabbed.size(); i++) {
        OverlayObject obj = (OverlayObject) grabbed.elementAt(i);
        obj.setCoords(obj.getX() + moveX, obj.getY() + moveY);
        obj.setCoords2(obj.getX2() + moveX, obj.getY2() + moveY);
      }
      grabX = dx;
      grabY = dy;
      overlay.notifyListeners(new TransformEvent(overlay));
    } else if (select != null) {
      // extend selection box
      select.setCorner(px, py);

      // select objects inside the box
      for (int i=0; i<bounds.length; i++) {
        float bx1, bx2, by1, by2;
        float tx1, tx2, ty1, ty2;
        float ox1, ox2, oy1, oy2;

        // object corners
        ox1 = bounds[i][0];
        oy1 = bounds[i][1];
        ox2 = bounds[i][2];
        oy2 = bounds[i][3];

        // un-oriented (disoriented?) box coordinates
        double[][] c = select.getCornersDomain();

        // selection box corners 'c'
        /*
         * 0----1
         * |    |
         * |    |
         * 3----2
         */
        
        // express object corners in terms of TSB edge vectors
        // vector between corners 0 and 1
        double[] v1 = {c[1][0] - c[0][0], c[1][1] - c[0][1]};
        // vector between corners 0 and 3
        double[] v2 = {c[3][0] - c[0][0], c[3][1] - c[0][1]};

        // iterate through all 4 points of object bounding box
        // and check whether they're inside selection area
        boolean inside = true; 
        for (int j = 0; j<bounds[0].length && inside; j++) {
          int xndx = j < 2 ? 0 : 2; 
          int yndx = j % 2 == 0 ? 1 : 3; 
          double[] p = {bounds[i][xndx], bounds[i][yndx]};
          // the above three lines iterate through the pairs (0,1),
          // (0,3), (2,1), (2, 3) of bounds[i] over the 
          // duration of the loop
          
          double[] vp = {p[0] - c[0][0], p[1] - c[0][1]}; // vector from c(0)
          // to p

          // cos of angle btw. vectors v1 and vp
          double cos1 = (vp[0] * v1[0] + vp[1] * v1[1]) / 
            (Math.sqrt(vp[0]*vp[0] + vp[1]*vp[1] + v1[0]*v1[0] + v1[1]*v1[1]));
          
          // cos of angle btw. v2 and vp
          double cos2 = (vp[0] * v2[0] + vp[1] * v2[1]) / 
            (Math.sqrt(vp[0]*vp[0] + vp[1]*vp[1] + v2[0]*v2[0] + v2[1]*v2[1]));

          if (cos1 < 0 || cos2 < 0)  inside = false;
          else {
            // determine projection of point on edge vectors
            // if projection is longer than either edge vector,
            // point is outside selection area
            double[] proj1 = MathUtil.getProjection(c[0], c[3], p, false);
            double[] proj2 = MathUtil.getProjection(c[0], c[1], p, false);
             
            double d1 = MathUtil.getDistance(proj1, c[0]);
            double d2 = MathUtil.getDistance(proj2, c[0]);

            double dv1 = MathUtil.getDistance(c[0], c[3]);
            double dv2 = MathUtil.getDistance(c[0], c[1]);

            if (d1 > dv1 || d2 > dv2) inside = false;
          }
        } // end for 

        // code for dynamic list refresh
        if (inside) {
          if (ctrl && !shift) {
            if (objs[i].isSelected() == selected[i]) {
              objs[i].setSelected(!selected[i]);
              selectionStateChanged = true;
            }
          }
          else if (!objs[i].isSelected()) {
            objs[i].setSelected(true);
            selectionStateChanged = true;
          }
        }
        else {
          if (shift || ctrl) {
            if (objs[i].isSelected() != selected[i]) {
              objs[i].setSelected(selected[i]);
              selectionStateChanged = true;
            }
          }
          else {
            if (objs[i].isSelected()) {
              objs[i].setSelected(false);
              selectionStateChanged = true;
            }
          }
        }
      } // end for

      if (selectionStateChanged) ((OverlayWidget)
          overlay.getControls()).refreshListSelection();

      overlay.notifyListeners(new TransformEvent(overlay));
    }
  }
}
