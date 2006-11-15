//
// FreeformTool.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-@year@ Greg Meyer.

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

import loci.visbio.data.TransformEvent;
import loci.visbio.util.MathUtil;
import java.awt.event.InputEvent;

/** FreeformTool is the tool for creating freeform objects. */
public class FreeformTool extends OverlayTool {
   
  // -- Constants --

  /** When drawing or editing, how far mouse must be dragged before new node is added */
  protected static final double DRAW_THRESH = 2.0; 
  /** Threshhold within which click must occur to invoke edit mode */
  protected static final double EDIT_THRESH = 5.0; 
  /** Threshhold within which click must occur to invoke extend mode or reconnect a tendril */
  protected static final double RECONNECT_THRESH = 0.5; 

  // -- Fields --

  /** Curve currently being drawn or modified. */
  protected OverlayFreeform freeform; 
  
  /** Tendril wraps info about an edit to a curve */
  protected Tendril tendril;

  protected boolean editing, extending; // whether the tool is in edit mode or extend mode;
  protected int prevNodeIndex, inserted; // used in editing to track changes to the curve 
  protected float[][] pre, post; // chunks of curve tracked in redrawing 

  // -- Constructor --

  /** Constructs a freeform creation tool. */
  public FreeformTool(OverlayTransform overlay) {
    super(overlay, "Freeform", "Freeform", "freeform.png");
    editing = extending = false;
    prevNodeIndex = inserted = 0;
    tendril = null;
  }

  // -- OverlayTool API methods --

  /** Instructs this tool to respond to a mouse press. */
  public void mouseDown(float x, float y, int[] pos, int mods) {
    /*
     * When mouseDown occurs, either
     * 1) a new freeform is being drawn, or
     * 2) an existing freeform is being edited
     *
     * 1) occurs if the mouseDown is sufficiently close to an existing freeform.  
     * In this case boolean editing = true.
     * else 2) occurs (editing = false) an new freeform is created
     */

    // determine if mouseDown was close enough to an existing freeform to invoke editing
    OverlayFreeform target = getClosestFreeform(x, y);
    
    if (target == null) { // not close to any freeforms
      deselectAll();
      freeform = new OverlayFreeform(overlay, x, y, x, y);
      configureOverlay(freeform);
      overlay.addObject(freeform, pos);
      extending = true;
    } else {
      // ACS TODO set selected in Overlays window for freeform under edit
      freeform = target;
      editing = true; // enter edit mode

      double[] distSegWt = MathUtil.getDistSegWt(freeform.getNodes(), x, y);

      double dist = distSegWt[0];
      int seg = (int) distSegWt[1];
      double weight = distSegWt[2];
      
      // criteria for entering extend mode or edit mode
      if (seg + 2 == freeform.getNumNodes() && weight == 1.0) { // extend
        editing = false;
        extending = true;
      } else if (seg == 0 && weight == 0.0) { // extend
        freeform.reverseNodes();
        editing = false;
        extending = true;
      } else { // edit
        // project click onto curve and insert a new node there, 
        // unless nearest point on curve is a node itself
        // Register this node as previous node
        
        if (weight == 0.0) {
          // nearest point on seg is the start node
          float[] a = freeform.getNodeCoords(seg); // determine projection on seg.
          freeform.insertNode (seg+1, a[0], a[1]);
          tendril = new Tendril (seg, seg+1, true);
          splitNodes (seg-1, seg+2);
        } else if (weight == 1.0) {
          // nearest point on  seg is the end node
          float[] a = freeform.getNodeCoords(seg+1); // determine projection on seg.
          freeform.insertNode(seg+2, a[0], a[1]);
          tendril = new Tendril (seg+1, seg+2, true);
          splitNodes (seg, seg+3);
        } else {
          float[] a = freeform.getNodeCoords(seg); // determine projection on seg.
          float[] b = freeform.getNodeCoords(seg + 1);        
          float[] newXY = computePtOnSegment(a, b, (float) weight);
          freeform.insertNode(seg + 1, newXY[0], newXY[1]);
          freeform.insertNode(seg + 1, newXY[0], newXY[1]);
          tendril = new Tendril(seg+1, seg+2, false);
          splitNodes (seg+1, seg+3);
        } // end else
      } // end else  
    } // end else
  } // end mouseDown

  /** Wraps info for redrawing a curve.  Appears like a 'tendril' on screen */
  private class Tendril {
    public int start, stop, tip;
    public boolean nodal;

    public Tendril (int start, int stop, boolean nodal) {
      this.start = start;
      this.stop = stop;
      this.nodal = nodal;
      // initial value of tip is nonsensical.  Use as a check. 
      this.tip = -1;
    }

    public void increment() {
      tendril.tip++;
      tendril.start++;
      tendril.stop++;
    }
  }

  /** Splits the node array into two parts.  The first part goes from a[0] to a[index-1], the second
    * from a[index2] to a[a.length -1] */
  private void splitNodes (int index, int index2) {
    // splits the array a into two (before the index specified)
    float[][] a = freeform.getNodes();
    // print these guys
    int depth = a.length;
    int len = a[0].length; // assumes non-ragged array
    pre =  new float[depth][index];
    post = new float[depth][len-index2];
    for (int i=0; i < depth; i++) {
      System.arraycopy(a[i], 0, pre[i], 0, index);
      System.arraycopy(a[i], index2, post[i], 0, len-index2);
    }
  }

  /** Instructs this tool to respond to a mouse release. */
  public void mouseUp(float x, float y, int[] pos, int mods) {
    if (editing && tendril !=null) {
      // save coords of tendril root
      float[] c = freeform.getNodeCoords(tendril.start);
      freeform.deleteBetween(tendril.start-1, tendril.stop+1);
      if (tendril.nodal) {
        freeform.insertNode(tendril.start, c[0], c[1]);
      }
    }

    extending = editing = false;
    deselectAll();
    if (freeform != null) { 
      inserted = 0;
      editing = false;
      freeform.truncateNodeArray();
      freeform.computeLength();
      freeform.updateBoundingBox();
      freeform.computeGridParameters();
      freeform.setDrawing(false);
      freeform = null;
      overlay.notifyListeners(new TransformEvent(overlay));
    }
  }

  /** Instructs this tool to respond to a mouse drag. */
  public void mouseDrag(float x, float y, int[] pos, int mods) {
    boolean shift = (mods & InputEvent.SHIFT_MASK) != 0; 
    // NEW!! holding shift suppresses reconnect routine, so you can redraw close to the curve. 
    // Why not use the wacom pen's click too? TODO
    // I think the pen switch is mapped to Right Click by default, which is already grabbed by VisAD

    if (editing) {
      
      // get coords of prev node
      float[] pcoordsf;
      if (tendril.tip >= 0) {
        // previous node is tendril.tip
        pcoordsf = freeform.getNodeCoords(tendril.tip);
      } else {
        // previous node is tendril.start
        pcoordsf = freeform.getNodeCoords(tendril.start);
      }

      double[] pcoordsd = {(double) pcoordsf[0], (double) pcoordsf[1]};
      double[] thiscoords = {(double) x, (double) y}; // coords of this mouseDrag event
      double dragDist = MathUtil.getDistance(pcoordsd, thiscoords);
      
      // compute distance to pre, post chunks of curve
      double[] distSegWtPre = MathUtil.getDistSegWt(pre, x, y);
      double[] distSegWtPost = MathUtil.getDistSegWt(post, x, y);
            
      boolean equalsCase = false;
      if (distSegWtPre[0] == distSegWtPost[0]) equalsCase = true;
      // Drag is equally close to both segments, wait for next drag.
      // This case is extremely unlikely.

      boolean closerToPost = (distSegWtPre[0] > distSegWtPost[0]);
      double[] distSegWt = (closerToPost) ? distSegWtPost : distSegWtPre;
      double minDist = distSegWt[0];
      int seg = (int) distSegWt[1];
      double weight = distSegWt[2];
      // ACS TODO what about equals case? wait for next drag?
   
      // if not close to curve insert node, else reconnect
      if (minDist > RECONNECT_THRESH) {
        // insert a node at the drag point if drag went far enough
        if (dragDist > DRAW_THRESH) { 
          // first drag only
          if (tendril.tip < 0) {
            freeform.insertNode(tendril.stop, x, y);
            tendril.stop++;
            tendril.tip = tendril.start + 1;
          } else { // later drags
            freeform.insertNode(tendril.tip+1, pcoordsf[0], pcoordsf[1]);  
            freeform.insertNode(tendril.tip+1, x, y);
            tendril.tip++;
            tendril.stop += 2;
          }
        }
        // tendril.tip always points to a lone node (except for before first drag, where it doesn't point to a 
        // node at all

      } else if (!shift && !equalsCase) { // reconnect with curve and delete nodes;
        if (tendril.tip < 0) {
          // tendril hasn't begun.  Do nothing.
        } else {
          //if (dragDist > DRAW_THRESH) {
            // insert a node first at drag point, then reconnect
            // skip this case:  There's the possibility that this drag point
            // is across the curve from the previous drag point, which might 
            // result in a unintended zig-zag.
          //}

          // insert node at nearest point
          int offset = (closerToPost) ? 1 + tendril.stop : 0;
          // offset points to either post[0] or pre[0]
          int endIndex = 0; // lame initial value.
          if (weight == 0.0) {
            endIndex = seg + offset;
          } else if (weight == 1.0) {
            endIndex = seg + 1 + offset;
          } else {
            float[] a = freeform.getNodeCoords(seg + offset); // determine projection on seg.
            float[] b = freeform.getNodeCoords(seg + 1 + offset);        
            float[] newXY = computePtOnSegment (a, b, (float) weight);
            int insertIndex = seg + 1 + offset; 
            freeform.insertNode(insertIndex, newXY[0], newXY[1]); 
            if (!closerToPost) {
              tendril.increment();
            }
            endIndex = insertIndex;
          }
         
          if (tendril.tip < endIndex) {
            freeform.deleteBetween(tendril.tip, endIndex); 
          } else {
            freeform.deleteBetween(endIndex, tendril.tip);
          }
          editing = false;
          freeform.computeLength();
        }           
      } // end reconnect logic
    } else if (extending) { 
      deselectAll();
      float dx = x - freeform.getLastNodeX();
      float dy = y - freeform.getLastNodeY();
      double dist = Math.sqrt (dx*dx + dy*dy);

      if (dist > RECONNECT_THRESH) {
        freeform.setNextNode(x, y);
        float len = freeform.getCurveLength();
        freeform.setCurveLength(len + (float) dist);
      }
    } else {
      // case !extending !editing
      // Q: can you be both not extending and not editing?
      // A: yes, after succesfully redrawing a segment of the curve.  Further drags do nothing for now.
    }

    overlay.notifyListeners(new TransformEvent(overlay)); 
  } // end mouseDrag
  
  //-- Additional methods

  /** Returns the closest (subject to a threshhold) OverlayFreeform object to 
   *  the given point 
   */
  private OverlayFreeform getClosestFreeform(float x, float y) {
    OverlayObject[] objects = overlay.getObjects(); 
    //Q: Hey, are all of these OverlayFreeforms?  A: No, it returns OverlayObjects of all types
    OverlayFreeform closestFreeform = null;
    if (objects != null) {
      double minDistance = Double.MAX_VALUE;
      for (int i = 0; i < objects.length; i++) {
        OverlayObject currentObject = objects[i];
        if (currentObject instanceof OverlayFreeform) {
          OverlayFreeform currentFreeform = (OverlayFreeform) currentObject;
          // rough check: is point within EDIT_THRESH of bounding box (fast)
          if (currentFreeform.getDistance(x, y) < EDIT_THRESH) {
            // fine check: actually compute minimum distance to freeform (slower)
            double[] distSegWt = MathUtil.getDistSegWt(currentFreeform.getNodes(), x, y);
            double distance = distSegWt[0];
            if (distance < EDIT_THRESH && distance < minDistance) {
              minDistance = distance;
              closestFreeform = currentFreeform;
            }
          } // end (.. < EDIT_THRESH)
        } // end if
      } // end for 
    } // end if
    return closestFreeform;
  }

  /** Computes a point along the line segment a[]-b[] (2D) based on parameter weight */
  private float[] computePtOnSegment (float[] a, float[] b, float weight) {
    float dx = b[0] - a[0];
    float dy = b[1] - a[1];
    float newX = (float) (a[0] + dx * weight);
    float newY = (float) (a[1] + dy * weight);
    float[] retvals = {newX, newY};
    return retvals;
  }

  /** Prints node array of current freeform; for debugging */
  private void printNodes() {
    if (freeform!=null){ 
      System.out.println("Printing nodes...");
      float[][]nodes = freeform.getNodes();
      for (int i = 0; i < nodes[0].length; i++){
        System.out.println(i+":("+nodes[0][i]+","+nodes[1][i]+")");
      }
    }
  }

  /** Prints a message for debugging */
  public void print(String methodName, String message) { 
    boolean toggle = true;
    if (toggle) {
      String header = "FreeformTool.";
      String out = header + methodName + ": " + message;
      System.out.println(out);
    }
  } 
} // end class FreeformTool 
