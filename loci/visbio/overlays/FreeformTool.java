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

  protected static final double DRAWTHRESH = 2.0; // When drawing, how far mouse must be dragged before a new node is added
  protected static final double DRAGTHRESH = 2.0; // When editing, how far mouse must be dragged before a new node is added 
  //DRAWTHRESH and DRAGTHRESH ought to be the same
  protected static final double EDITTHRESH = 5.0; // Threshhold within which click must occur to invoke edit mode

  // -- Fields --

  /** Curve currently being drawn or modified. */
  protected OverlayFreeform freeform; 

  protected boolean editing; // whether the tool is in edit mode 
  protected int prevNodeIndex, nextNodeIndex; // used in editing to track changes to the curve 
  
  // -- Constructor --

  /** Constructs a freeform creation tool. */
  public FreeformTool(OverlayTransform overlay) {
    super(overlay, "Freeform", "Freeform", "freeform.png");
    editing = false;
    prevNodeIndex = nextNodeIndex = 0;
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
    
    if (target == null) {
      //System.out.println("target freeform for editing == null");// TEMP
      deselectAll();
      freeform = new OverlayFreeform(overlay, x, y, x, y);
      configureOverlay(freeform);
      overlay.addObject(freeform, pos);
    } else {
      // ACS TODO setSelected for freeform under edit
      // closestFreeform.setSelected(true);
      freeform = target;
      //print("mouseDown", "Entering edit mode"); // TEMP
      editing = true;

      double[] distSegWt = freeform.getDistanceEtc(x, y);

      double dist = distSegWt[0];
      int seg = (int) distSegWt[1];
      double weight = distSegWt[2];
      
      // criteria for entering extend mode or edit mode
      if (seg + 2 == freeform.getNumNodes() && weight == 1.0) { // extend
        //print("mouseDown", "case 1: nearest point is last node"); // TEMP
        editing = false;
      } else if (seg == 0 && weight == 0.0) { // extend
        //print("mouseDown", "case 2: nearest point is first node"); // TEMP
        freeform.reverseNodes();
        editing = false;
      } else { // edit
        //print("mouseDown", "case 3: nearest point is interior"); // TEMP
        // project click onto curve and insert a new node there, 
        // unless nearest point on curve is a node itself
        // Register this node as previous node
        
        // TEMP print the node array
        /*
        float[][] nodes = freeform.getNodes(); // TEMP
        //print the nodes 
        for (int i=0; i<nodes[0].length; i++) // TEMP
          System.out.println ("(" + nodes[0][0]+ "," + nodes[1][i]+")"); // TEMP
        */
        //print ("mouseDown", ("x = " + x + " y = " + y + " seg = " + seg)); // TEMP
        if (weight == 0.0) prevNodeIndex = seg; // nearest point on seg is the start node
        else if (weight == 1.0) prevNodeIndex = seg + 1; // nearest point on seg is the end node
        else {
          float[] a = freeform.getNodeCoords(seg); // determine projection on seg.
          float[] b = freeform.getNodeCoords(seg + 1);        
          float dx = b[0] - a[0];
          float dy = b[1] - a[1];
          float newX = (float) (a[0] + dx * weight);
          float newY = (float) (a[1] + dy * weight);
          //print ("mouseDown", ("seg ("+a[0]+","+a[1]+")->("+b[0]+","+b[1]+")"));// TEMP
          //print ("mouseDown", ("insert prevNode at ("+newX+","+newY+")"));// TEMP
          freeform.insertNode(seg + 1, newX, newY);
          prevNodeIndex = seg + 1;
        }
      }
    }
  }

  /** Instructs this tool to respond to a mouse release. */
  public void mouseUp(float x, float y, int[] pos, int mods) {
    //prevNodeIndex = -1;
    //cleanup
    deselectAll();
    if (freeform != null) { 
      editing = false;
      freeform.truncateNodeArray();
      freeform.updateBoundingBox();
      freeform.computeGridParameters();
      freeform.setDrawing(false);
      freeform = null;
      overlay.notifyListeners(new TransformEvent(overlay));
    }
  }

  /** Instructs this tool to respond to a mouse drag. */
  public void mouseDrag(float x, float y, int[] pos, int mods) {
    boolean shift = (mods & InputEvent.SHIFT_MASK)!=0; 
    // deactivates tendril-like behavior when dragging perpendicular to curve
    
    /* 
    // WARNING: causes array out of bounds errors down the line
    // try this to check for entering extend mode
    if (freeform.getNumNodes() > 3){
      if (prevNodeIndex + 2 == freeform.getNumNodes()) {
        editing = false;
        freeform.deleteNode(prevNodeIndex + 1);
      } else if (prevNodeIndex == 1) {
        editing = false;
        freeform.deleteNode(prevNodeIndex - 1);
        prevNodeIndex--;
      }
    }
    */

    if (editing) {
      // get coords of prev node
      float[] pcoordsf = freeform.getNodeCoords(prevNodeIndex);
      double[] pcoordsd = {(double) pcoordsf[0], (double) pcoordsf[1]};
      double[] thiscoords = {(double) x, (double) y}; // coords of this mouseDrag event
      
      // if mouseDrag went far enough
      if (MathUtil.getDistance(pcoordsd, thiscoords) > DRAGTHRESH) { 
        // get distance to curve, nearest segment, weight (to determine nearest point on seg)
        double[] distSegWt = freeform.getDistanceEtc(x, y);
        double dist = distSegWt[0];
        int seg = (int) distSegWt[1];
        double weight = distSegWt[2];

        //print ("mouseDrag", ("nearest seg = " +seg+ " to " + (seg+1))); // TEMP
        
        // Determine whether to switch into extend mode
        if (seg + 2 == freeform.getNumNodes() && weight == 1.0) { // extend
          //print("mouseDrag", "case 1: nearest point is last node"); // TEMP
          // nearest node is last node
          //freeform.setNodeCoords(seg+2, x, y);
          editing = false;
          //return;
        } else if (seg == 0 && weight == 0.0) { // reverse and extend
          //print("mouseDrag", "case 2: nearest point is first node"); // TEMP
          // nearest node is first node
          //freeform.setNodeCoords(0, x, y);
          freeform.reverseNodes();
          editing = false;
          //return;
        } else {
          //print("mouseDrag", "case 3: nearest point is an interior node"); // TEMP
          if (weight == 0.0) {
            //print ("mouseDrag", "nearest point was node " + seg ); // TEMP
            nextNodeIndex = seg; // nearest point on seg is the start node
          } else if (weight == 1.0) {
            nextNodeIndex = seg + 1; // nearest point on seg is the end node
            //print ("mouseDrag", "nearest point was node " + (seg+1)); // TEMP
          } else {
            //print("mouseDrag", "determining projection coords.");
            float[] a = freeform.getNodeCoords(seg); // determine projection on seg.
            float[] b = freeform.getNodeCoords(seg + 1);        
            float dx = b[0] - a[0];
            float dy = b[1] - a[1];
            float newX = (float) (a[0] + dx * weight);
            float newY = (float) (a[1] + dy * weight);
            //print ("mouseDrag", ("seg ("+a[0]+","+a[1]+")->("+b[0]+","+b[1]+")"));// TEMP
            //print ("mouseDrag", ("insert nextNode at ("+newX+","+newY+")"));// TEMP
            freeform.insertNode(seg + 1, newX, newY);
            nextNodeIndex = seg + 1;
            if (seg + 1 <= prevNodeIndex) prevNodeIndex++; // increment since insert occurs before
          }
        
          //debug(x, y); // TEMP
          //print ("mouseDrag", ("prevNodeIndex=" + prevNodeIndex + " nextNodeIndex=" + nextNodeIndex)); // TEMP
          if (prevNodeIndex == nextNodeIndex && !shift) {
            // replace prevnode with two colocational nodes, insert new node at drag point
            float[] prev = freeform.getNodeCoords(prevNodeIndex);
            freeform.insertNode(prevNodeIndex+1, prev[0], prev[1]);
            freeform.insertNode(prevNodeIndex+1, x, y);
            prevNodeIndex++;
            } else if (prevNodeIndex > nextNodeIndex) {
            //print ("mouseDrag", "prevNode > nextNode. Deleting between nextNode and prevNode"); // TEMP
            //debug(x,y); // TEMP
            freeform.deleteBetween(nextNodeIndex, prevNodeIndex); 
            freeform.insertNode(nextNodeIndex + 1, x, y); // insert drag point just after next node/just before prevNode
            prevNodeIndex = nextNodeIndex + 1;
            //print ("mouseDrag", ("inserted node at nextNodeIndex + 1, prevNodeIndex = nextNodeIndex + 1 = " + prevNodeIndex)); // TEMP
            //debug(x, y); // TEMP
          } else if (nextNodeIndex > prevNodeIndex) {
            //print ("mouseDrag", "prevNode < nextNode. Deleting between nextNode and prevNode"); // TEMP
            int victims = Math.abs(nextNodeIndex - prevNodeIndex) - 1;
            freeform.deleteBetween(prevNodeIndex, nextNodeIndex);
            freeform.insertNode(nextNodeIndex - victims, x, y); // insert before next node (now shifted)
            prevNodeIndex = nextNodeIndex - victims;
          }
        } // distance < DRAGTHRESH, keep waiting. 
      } // if block corresponding to check whether extend
    } else { // end if editing....
      //print ("mouseDrag", "extend mode"); // TEMP
      deselectAll();
      float dx = x - freeform.getLastNodeX();
      float dy = y - freeform.getLastNodeY();
      double dist = Math.sqrt (dx*dx + dy*dy);

      if (dist > DRAWTHRESH) {
        //print("mouseDrag", ("distance sufficiently large. New node created at (" + x + "," + y + ")")); // TEMP
        freeform.setNextNode(x, y);
      }
    } // end else
      overlay.notifyListeners(new TransformEvent(overlay));
  }// end mouseDrag

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
          // rough check: is point within CLICKTHRESH of bounding box (fast)
          if (currentFreeform.getDistance(x, y) < EDITTHRESH) {
            // fine check: actually compute minimum distance to freeform (slower)
            double[] distSegWt = currentFreeform.getDistanceEtc(x, y);
            double distance = distSegWt[0];
            if (distance < EDITTHRESH && distance < minDistance) {
              minDistance = distance;
              closestFreeform = currentFreeform;
            }
          } // end (.. < EDITTHRESH)
        } // end if
      } // end for 
    } // end if
    return closestFreeform;
  }

  /** Print some helpful debugging info */
  private void debug(float x, float y) {
    System.out.println ("Current drag coords: (" + x + "," + y + ")");
    System.out.println ("PrevNodeIndex = " + prevNodeIndex);
    System.out.println ("NextNodeIndex = " + nextNodeIndex);
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
