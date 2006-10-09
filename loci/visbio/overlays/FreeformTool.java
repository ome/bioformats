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

/** FreeformTool is the tool for creating freeform objects. */
public class FreeformTool extends OverlayTool {

  // -- Fields --

  /** Curve currently being drawn. */
  protected OverlayFreeform freeform;
  
  /** Freeform currently being modified. */
  protected OverlayFreeform activeFreeform;

  protected boolean editing;
  
  protected static final double CLICKTHRESH = 5.0;
  protected static final double STRETCHTHRESH = 2.0; // ACS TODO this should correspond to node drawing threshhold
  protected static final double DRAWTHRESH = 2.0;
  // -- Constructor --

  /** Constructs a freeform creation tool. */
  public FreeformTool(OverlayTransform overlay) {
    super(overlay, "Freeform", "Freeform", "freeform.png");
    editing = false;
  }


  // -- OverlayTool API methods --

  /** Instructs this tool to respond to a mouse press. */
  public void mouseDown(float x, float y, int[] pos, int mods) {
  //System.out.println("FreeformTool::mouseDown(" + x + "," + y +")"); // TEMP  
    /* two cases: editing an existing freeform or drawing a new one.
    A) editing an exiting
       1) compute distance of click to nearby freeforms
         a) see if mouse click is within a CLICKTHRESH distance of the bounding box of each freef.
         b) if mouse click is within multiple bounding boxes, pick the nearest freeform
            i) for each freeform, compute distance to nearest node.
            ii) compare distances and take minimum
            iii) if this distance is less than $tolerance, then you're A) Editing!
       2) edit the nearest freeform (stick this in a new method)
         a) compute distance to nearest node
         b) insert a new node before? after? nearest node
         c) perform some sort of smoothing.  Try to figure out how CurveManipulation renderer does it.
    B) drawing a new one : as below*/
    
    // determine if editing.
    OverlayFreeform target = getClosestFreeform(x, y);
    
    if (target == null) {
      //System.out.println("target freeform for editing == null");// TEMP
      deselectAll();
      freeform = new OverlayFreeform(overlay, x, y, x, y);
      configureOverlay(freeform);
      overlay.addObject(freeform, pos);
    } else {
      //System.out.println("FreeformTool::You're editing " + target); // TEMP
      // set some flag to true, in order to respond properly to mouseDrag
      // ACS TODO setSelected for freeform under edit
      edit(target);
      // closestFreeform.setSelected(true);
      // another option: new FreeformEditor (closestFreeform)
    }
  }

  private OverlayFreeform getClosestFreeform(float x, float y) {
    OverlayObject[] objects = overlay.getObjects(); 
    //Q: Hey, are all of these OverlayFreeforms?  A: No, it returns OverlayObjects of all types
    
    //System.out.println("FreeformTool: Grabbed an array of " + objects.length + " OverlayObjects."); // TEMP
    double minDistance = Double.MAX_VALUE;
    OverlayFreeform closestFreeform = null;
    for (int i = 0; i < objects.length; i++) {
      OverlayObject currentObject = objects[i];
      if (currentObject instanceof OverlayFreeform) {
        OverlayFreeform currentFreeform = (OverlayFreeform) currentObject;
        //System.out.println("FreeformTool: Yep, object" + i + " is a freeform.  Computing distance..."); // TEMP
        // rough check: is point within CLICKTHRESH of bounding box
        if (currentFreeform.getDistance(x, y) < CLICKTHRESH) {
          //System.out.println("\tMouseDown at (" + x + "," + y + ") was w/in bounding box of currentFreeform, object" + i);
          // fine check: actually compute minimum distance to freeform
          double distance = currentFreeform.getTrueDistance(x, y);
          if (distance < minDistance) {
            minDistance = distance;
            closestFreeform = currentFreeform;
          }
        } else {
          //System.out.println("\tNot within threshhold or not closer than current closest freeform"); // TEMP
        } // end if/else
      } // end if
    } // end for 
    return closestFreeform;
  }

  private void edit(OverlayFreeform of) {
    editing = true;
    activeFreeform = of;
  }

  private void unEdit() {
    editing = false;
    if (activeFreeform != null) {
      activeFreeform.truncateNodeArray();
      activeFreeform.computeGridParameters();
    }
    activeFreeform = null;
  }

  /** Instructs this tool to respond to a mouse release. */
  public void mouseUp(float x, float y, int[] pos, int mods) {
    unEdit();
    
    //System.out.println("FreeformTool::mouseUp(...)"); // TEMP
    deselectAll();
    if (freeform == null) return;
    freeform.truncateNodeArray();
    freeform.computeGridParameters();
    freeform.setDrawing(false);
    freeform = null;
    overlay.notifyListeners(new TransformEvent(overlay));
  }

  /** Instructs this tool to respond to a mouse drag. */
  public void mouseDrag(float x, float y, int[] pos, int mods) {
    if (editing) {
      stretch (activeFreeform, x, y);
    } else {
      deselectAll();
      print("mouseDrag", "computing distance from mouse position to last node"); // TEMP
      // 
      float dx = x - freeform.getLastNodeX();
      float dy = y - freeform.getLastNodeY();
      if (Math.sqrt(dx*dx + dy*dy) > DRAWTHRESH) {
        print("mouseDrag", ("distance sufficiently large. New node created at (" + x + "," + y + ")")); // TEMP
        freeform.setNextNode(x, y);
        freeform.setBoundaries(x, y);
      }
    } // end else
      overlay.notifyListeners(new TransformEvent(overlay));
  }// end mouseDrag

  /** inserts nodes as the cursor moves */
  private void stretch(OverlayFreeform activeFreeform, float x, float y) {
    print("stretch", ("stretch called at (" + x + "," + y + ")"));
    // cases:
    // endpoint is nearest
    // intermediate point is nearest
    //print("mouseDrag", ("--We're editing now at (" + x + "," + y + ")")); // TEMP
    float[] nearestNode  = activeFreeform.computeNearestNode(x, y);
    print("stretch", ("nearest node is index (" + nearestNode[0] + ") and distance (" + nearestNode[1] + ").  Moving currrent node to position of mouse."));
    int index = (int) nearestNode[0];
    float distance = nearestNode[1];
    activeFreeform.setNodeCoords(index, x, y); 
    if (index == 0) {
      // ACS TODO what to do if pulling on first node
      // go into elongation mode;
      // drawing the freeform is really a special case of pulling on the 
      // curve at some interior point.  In both cases, new nodes are inserted
      // based on the distance the cursor is dragged.
      // Merge draw and stretch routines?  Except when drawing, you know the
      // nearest node automatically--it's the last node.
    } else if (index == activeFreeform.getLastNodeIndex()) {
      // ACS TODO what to do if pulling on last node
      // continue drawing 
    } else {
      // nearest node node is an intermediate node, i.e., 0 < index < lastNodeIndex
      float[] prev = activeFreeform.getNodeCoords(index - 1);
      float[] next = activeFreeform.getNodeCoords(index + 1);
      print("stretch", ("prev = (" + prev[0] + "," + prev[1] + ")"));
      print("stretch", ("next = (" + next[0] + "," + next[1] + ")"));
      // ACS TODO use a MathUtil method here
      float pDist = (float) Math.sqrt ((x - prev[0]) * (x - prev[0]) + (y - prev[1]) * (y - prev[1]));
      float nDist = (float) Math.sqrt ((x - next[0]) * (x - next[0]) + (y - next[1]) * (y - next[1]));
      print("stretch", ("pDist = " + pDist));
      print("stretch", ("nDist = " + nDist));
      boolean insertedBefore = false;
      if (pDist > STRETCHTHRESH) {
        float xx = Math.max (x, prev[0])/2.0f + Math.min(x, prev[0])/2.0f;
        float yy = Math.max (y, prev[1])/2.0f + Math.min(y, prev[1])/2.0f;
        print("stretch", "inserting a node before at (" + xx + "," + yy + ")");
        activeFreeform.insertNode(index, xx, yy);
        insertedBefore = true;
      } 
      if (nDist > STRETCHTHRESH) {
        float xx = Math.max (x, next[0])/2.0f + Math.min(x, next[0])/2.0f;
        float yy = Math.max (y, next[1])/2.0f + Math.min(y, next[1])/2.0f;
        print("stretch", "inserting a node after at (" + xx + "," + yy + ")");
        if (insertedBefore) {
          activeFreeform.insertNode(index + 2, xx, yy);
        } else {
          activeFreeform.insertNode(index + 1, xx, yy);
        }// end else
      }// end if nDist...       
    }//end else
  }

  /** prints a message for debugging */
  public void print(String methodName, String message) { 
    boolean toggle = true;
    if (toggle) {
      String header = "FreeformTool.";
      String out = header + methodName + ": " + message;
      //System.out.println(out);
    }
  } 
      
} // end class OverlayTool
