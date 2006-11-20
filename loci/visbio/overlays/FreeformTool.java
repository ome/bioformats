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

  /** 4 constants describing edit modes */
  // There are four edit modes.  The method setMode(int) is called to change the mode, in order to
  // properties related to the current mode; e.g., whether a freeform is selected in the list of 
  // overlays
  protected static final int ERASE = -1;
  protected static final int CHILL = 0; // could call this WAIT...
  protected static final int DRAW = 1;
  protected static final int EDIT = 2;

  // -- Fields --

  /** Curve currently being drawn or modified. */
  protected OverlayFreeform freeform; 
  
  /** Tendril wraps info about an edit to a curve */
  protected Tendril tendril;
  protected int mode;

  protected float[][] pre, post; // chunks of curve tracked in redrawing 

  // -- Constructor --

  /** Constructs a freeform creation tool. */
  public FreeformTool(OverlayTransform overlay) {
    super(overlay, "Freeform", "Freeform", "freeform.png");
    setMode(CHILL);
    tendril = null;
  }

  // -- OverlayTool API methods --

  /** Instructs this tool to respond to a mouse press. */
  public void mouseDown(float x, float y, int[] pos, int mods) {
    boolean ctl = (mods & InputEvent.CTRL_MASK) != 0; 
    OverlayFreeform target = getClosestFreeform(x, y);
    if (target != null) {
      freeform = target;
      double[] distSegWt = MathUtil.getDistSegWt(freeform.getNodes(), x, y);
      double dist = distSegWt[0];
      int seg = (int) distSegWt[1];
      double weight = distSegWt[2];

      if (ctl) {
        setMode(ERASE);
        // test for not terminal node
        if (!(seg == 0 && weight == 0.0) && !(seg == freeform.getNumNodes()-2 && weight == 1.0)) {
          erase(freeform, x, y, dist, seg, weight);
        }
      } else {
        // test for not terminal node
        if (!(seg == 0 && weight == 0.0) && !(seg == freeform.getNumNodes()-2 && weight == 1.0)) {
          // if interior node
          // project click onto curve and insert a new node there, 
          // unless nearest point on curve is a node itself
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
          } 
          setMode(EDIT);
        } else {
          if (seg == 0) freeform.reverseNodes();
          setMode(DRAW);
        }
      }// end else
    } else { 
      deselectAll();
      freeform = new OverlayFreeform(overlay, x, y, x, y);
      configureOverlay(freeform);
      overlay.addObject(freeform, pos);
      setMode(DRAW);
    } 

    overlay.notifyListeners(new TransformEvent(overlay));
    ((OverlayWidget) overlay.getControls()).refreshListSelection();
  } // end mouseDown

  /** Erases or slices a freeform */
  private void erase(OverlayFreeform freef, float x, float y, double dist, int seg, double weight) {
    //TODO: don't need to check all of exterior cases
    int f1Start, f2Start, f1Stop, f2Stop;
    OverlayFreeform f1, f2;
    boolean doNothing = false;

    f1Start = 0;
    f1Stop = seg;
    f2Start = seg + 1;
    f2Stop = freef.getNumNodes() - 1;
        
    boolean splitFreeform = false;   
    if (weight == 0.0) { 
      // case: beginning node
      freef.deleteNode(0);
      //print("erase", "weight == 0: click occurred nearest start node");
    } else if (weight == 1.0) {
      // case end node of segment
      if (seg == freef.getNumNodes() - 2) {
        freef.deleteNode(seg + 1);
        //print("erase", "weight == 1: click occurred nearest end node");
      } else {
        //print("erase", "weight == 1: click occurred nearest node " + (seg + 1));
        splitFreeform = true;
        // reassign this guy
        f2Start = seg + 2; 
      }
    } else {
      // interior segment
      //print("erase", "0 < weight < 1: seg = " + seg);
      splitFreeform = true;
    }
    
    if (splitFreeform) {
      //TODO debug weird 'elementAt()' exception that pops up.  Wrong number of object.
      float[][] oldNodes = freef.getNodes();
      float[][] f1Nodes = new float[2][f1Stop+1];
      float[][] f2Nodes = new float[2][f2Stop-f2Start + 1];

      for (int i = 0; i<2; i++) {
        System.arraycopy(oldNodes[i], 0, f1Nodes[i], 0, f1Stop+1);
        System.arraycopy(oldNodes[i], f2Start, f2Nodes[i], 0, f2Stop-f2Start+1);
      }
      f1 = new OverlayFreeform(overlay, f1Nodes);
      f2 = new OverlayFreeform(overlay, f2Nodes);
      
      //System.out.println("printing nodes of original freeform");
      //printNodes(freef.getNodes());
      //System.out.println("printing nodes of new sub freeforms");
      //printNodes(f1.getNodes());
      //printNodes(f2.getNodes());
      configureOverlay(f1);
      configureOverlay(f2);
      overlay.addObject(f1);
      overlay.addObject(f2);
      f1.setSelected(false);
      f2.setSelected(false);
      f1.setDrawing(false);
      f2.setDrawing(false);
      
      overlay.removeObject(freef);
    }
  }

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
    //print ("mouseUp", "mouseUp begun. mode = " + mode);
    if (mode == DRAW) {
      freeform.truncateNodeArray();
    } else if (mode == EDIT) {
      // save coords of tendril root
      float[] c = freeform.getNodeCoords(tendril.start);
      freeform.deleteBetween(tendril.start-1, tendril.stop+1);
      if (tendril.nodal) {
        freeform.insertNode(tendril.start, c[0], c[1]);
      }
    }

    if (mode != CHILL) setMode(CHILL);
    overlay.notifyListeners(new TransformEvent(overlay));
    ((OverlayWidget) overlay.getControls()).refreshListSelection();
  }

  /** Sets variables related to edit mode */
  private void setMode(int newMode) {
    mode = newMode;
    if (mode == DRAW || mode == EDIT || mode == ERASE) {
      freeform.setDrawing(true);
      freeform.setSelected(true);
    } else if (mode == CHILL && freeform != null) {
      freeform.computeLength();
      freeform.updateBoundingBox();
      freeform.computeGridParameters();
      freeform.setDrawing(false);
      freeform = null;
    }
  }

  /** Instructs this tool to respond to a mouse drag. */
  public void mouseDrag(float x, float y, int[] pos, int mods) {
    boolean shift = (mods & InputEvent.SHIFT_MASK) != 0; 
    boolean ctl = (mods & InputEvent.CTRL_MASK) != 0; 

    if (mode == DRAW) {
      float lastX = freeform.getLastNodeX();
      float lastY = freeform.getLastNodeY();
      float dx = x - lastX; 
      float dy = y - lastY; 
      // compute distance
      double dist = Math.sqrt (dx*dx + dy*dy);
    
      if (dist > DRAW_THRESH) {
        freeform.setNextNode(x, y);
        double len = freeform.getCurveLength();
        freeform.setCurveLength(len + dist);
        freeform.setBoundaries(x,y); // I debated whether to update this realtime.  This is an efficient method, 
        // buy updating realtime for erasing requires an O(n) operation every time a node is deleted.
      }
      // mode remains DRAW
    } else if (mode == ERASE) {
      if (freeform == null) {
        OverlayFreeform target = getClosestFreeform(x, y);
        if (target != null) freeform = target;
      }

      if (!ctl) {
        float lastX = freeform.getLastNodeX();
        float lastY = freeform.getLastNodeY();
        float dx = x - lastX; 
        float dy = y - lastY; 
        // compute distance
        double dist = Math.sqrt (dx*dx + dy*dy);
      
        if (dist > DRAW_THRESH) {
          freeform.setNextNode(x, y);
          double len = freeform.getCurveLength();
          freeform.setCurveLength(len + dist);
        }

        setMode(DRAW);
      } else {
        // delete an end node if you're near enough
        float[] beg = freeform.getNodeCoords (0);
        float[] end = freeform.getNodeCoords (freeform.getNumNodes() - 1);

        double[] drag = {(double) x, (double) y};
        double[] begd = {(double) beg[0], (double) beg[1]};
        double[] endd = {(double) end[0], (double) end[1]};
        
        double bdist = MathUtil.getDistance (drag, begd);
        double edist = MathUtil.getDistance (drag, endd);
        
        boolean closerToEnd = edist < bdist ? true : false;
        double mdist = closerToEnd ? edist : bdist;
        
        if (mdist < DRAW_THRESH) {
          double[] nearest;
          int index, offset;

          if (closerToEnd) {
            index = freeform.getNumNodes()-1;
            offset = -1;
            nearest = endd;
          } else {
            index = 0;
            offset = 1;
            nearest = begd;
          }
          // adjust curve length
          float[] p = freeform.getNodeCoords(index + offset);
          double[] pd = {(double) p[0], (double) p[1]};

          double delta = MathUtil.getDistance (nearest, pd);
          freeform.setCurveLength(freeform.getCurveLength() - delta);

          // delete appropriate node
          freeform.deleteNode(index);
          freeform.updateBoundingBox(); // WARNING this is O(n) expensive.  Maybe remove it and just update at
                                        // mouseUp?
        }

        if (freeform.getNumNodes() == 0) {
          setMode(CHILL);
        }
      }
    } else if (mode == EDIT) {
      // extend tendril and or reconnect
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
          freeform.computeLength();
          setMode(CHILL);
        }           
      } // end reconnect logic
  
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
  private void printNodes(float[][] nodes) {
    System.out.println("Printing nodes...");
    for (int i = 0; i < nodes[0].length; i++){
      System.out.println(i+":("+nodes[0][i]+","+nodes[1][i]+")");
    }
  }

  /** Prints node array of current freeform; for debugging */
  private void printNodes() {
    if (freeform!=null){ 
      float[][] nodes = freeform.getNodes();
      printNodes(nodes);
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
