//
// PolylineTool.java
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

import java.awt.event.InputEvent;
import java.awt.Color;
import java.util.Vector;
import loci.visbio.data.TransformEvent;
import loci.visbio.util.MathUtil;
import visad.DisplayEvent;
import visad.DisplayImpl;
import visad.util.CursorUtil;

/** FreeformTool is the tool for creating freeform objects. */
public class PolylineTool extends OverlayTool {

  // -- Constants --
  protected static final int ERASE = -1;
  protected static final int WAIT = 0;
  protected static final int EXTEND = 1;
  protected static final int ADJUST = 2;
  protected static final int SELECT = 3;
  protected static final int PLACE = 4;
  protected static final int ADJUST_TAIL = 5;
  protected static final int CLOSE_LOOP = 6;
  protected static final int SELECTED_TAIL = 7;
  protected static final int EXTEND_ON_TAIL = 8;
  protected static final int BEG_EXTEND = 9;

  /** Maximum distance (in pixels) mouse can be from a node to be considered
   *  pointing to it. */
  protected static final double THRESH = 5.0;

  /** Color for highlighting head or tail node of polyline when 'connecting'
   *  free end to fixed end */
  protected static final Color CON = Color.GREEN;

  /** Color for highlighting head or tail node when the mouse is nearby or
   *  when dragging the node. */
  protected static final Color SEL = Color.YELLOW;

  // -- Fields --

  /** Curve currently being drawn or modified. */
  protected OverlayPolyline line;

  /** Curve close to the mouse when drawing is not occurring. */
  protected OverlayPolyline selectedPln;

  /** Nearest node on selected PolyLine. */
  protected int selectedNode;

  /**
   * The current mode of the state machine represented in/by the
   * mouse methods of this tool.
   */
  protected int mode;

  // -- Constructor --

  /** Constructs a creation tool. */
  public PolylineTool(OverlayTransform overlay) {
    super(overlay, "Polyline", "Polyline", "polyline.png");
    line = null;
    mode = WAIT;
    unselect();
  }

  // -- OverlayTool API methods --

  /** Instructs this tool to respond to a mouse press. */
  public void mouseDown(DisplayEvent e, int px, int py,
    float dx, float dy, int[] pos, int mods)
  {
    //System.out.println("down mode = " + mode);
    boolean ctl = (mods & InputEvent.CTRL_MASK) != 0;
    DisplayImpl display = (DisplayImpl) e.getDisplay();

    //printMode("mouseDown");

    if (overlay.hasToolChanged()) {
      releaseLine();
      mode = WAIT;
    }

    if (mode == WAIT) {
      deselectAll();
      line =  new OverlayPolyline(overlay, dx, dy, dx, dy);
      line.setDrawing(true);
      line.setSelected(true);
      configureOverlay(line);
      overlay.addObject(line, pos);
      mode = PLACE;
    }
    else if (mode == PLACE) {
      if (line.getNumNodes() > 1) {
        releaseLine();
      }
      else { 
        overlay.removeObject(line);
        line = null;
      }
      mode = WAIT;
    }
    else if (mode == SELECT) {
      if (!ctl) {
        // which node are you near?
        if (selectedNode == line.getNumNodes() - 1) {
          mode = SELECTED_TAIL;
        }
        else if (selectedNode == 0) {
          // you're near the head node
          line.reverseNodes();
          selectNode(display, line, line.getNumNodes() - 1);
          mode = SELECTED_TAIL;
        }
        else {
          mode = ADJUST;
        }
        line.setDrawing(true);
      }
      else {
        // if node interior, create two new polylines
        if (selectedNode > 0 && selectedNode < line.getNumNodes() - 1) {
          split(line, selectedNode);
          overlay.removeObject(line);
          unselect();
          mode = WAIT;
        }
        else {
          // else delete node
          line.deleteNode(selectedNode);
          releaseLine();
          unselect(); // TODO these are redundant 
          mode = WAIT;
        }
      }
    }
    else if (mode == EXTEND || mode == BEG_EXTEND) {
      line.setLastNode(dx, dy);
      mode = PLACE;
    }
    else if (mode == EXTEND_ON_TAIL) {
      line.deleteNode(line.getNumNodes()-1);
      releaseLine();
      unselect();
      mode = WAIT;
    }

    overlay.notifyListeners(new TransformEvent(overlay));
  } // end mouseDown

  /** Instructs this tool to respond to a mouse drag. */
  public void mouseDrag(DisplayEvent e, int px, int py,
    float dx, float dy, int[] pos, int mods) {
    //System.out.println("mode = " + mode);
    DisplayImpl display = (DisplayImpl) e.getDisplay();

    //printMode("mouseDrag");

    if (overlay.hasToolChanged()) {
      releaseLine();
      mode = WAIT;
    }
    if (mode == ADJUST) {
      line.setNodeCoords(selectedNode, dx, dy);
      overlay.notifyListeners(new TransformEvent(overlay));
    } 
    else if (mode == SELECTED_TAIL) {
      mode = ADJUST_TAIL;
      mouseDrag(e, px, py, dx, dy, pos, mods);
    }
    else if (mode == ADJUST_TAIL || mode == CLOSE_LOOP) {
      line.setNodeCoords(selectedNode, dx, dy);

      // determine if near head
      double dist = getDistanceToNode(0, px, py, display);
      
      // if near, highlight head node 
      if (dist < THRESH) {
        line.setHighlightNode(selectedNode, CON);
        mode = CLOSE_LOOP;
      }
      else {
        line.setHighlightNode(selectedNode, SEL);
        mode = ADJUST_TAIL;
      }

      overlay.notifyListeners(new TransformEvent(overlay));
    }
    else if (mode == PLACE || mode == EXTEND || mode == BEG_EXTEND ||
        mode == EXTEND_ON_TAIL) {
      mouseMoved(e, px, py, dx, dy, pos, mods);
    }
  } 

  /** Instructs this tool to respond to a mouse release. */
  public void mouseUp(DisplayEvent e, int px, int py, 
      float dx, float dy, int[] pos, int mods) {
    //System.out.println("up mode = " + mode);
    DisplayImpl display = (DisplayImpl) e.getDisplay();
    //printMode("mouseUp"); // TEMP

    if (overlay.hasToolChanged()) {
      releaseLine();
      mode = WAIT;
    }
    if (mode == ADJUST) {
      line.updateBoundingBox();
      line.computeGridParameters();
      line.computeLength();
      line.setDrawing(false);
      mode = SELECT;
    }
    else if (mode == ADJUST_TAIL) {
      line.updateBoundingBox();
      line.computeGridParameters();
      line.computeLength();
      line.setDrawing(false);
      mode = SELECT;
    }
    else if (mode == SELECTED_TAIL) { 
      line.turnOffHighlighting();
      mode = PLACE;
    }
    else if (mode == CLOSE_LOOP) {
      float[] c = line.getNodeCoords(0);
      line.setLastNode(c[0], c[1]); 
      line.updateBoundingBox();
      line.computeGridParameters();
      line.computeLength();
      line.setDrawing(false);
      selectNode(display, line, line.getNumNodes() - 1);
      mode = SELECT;
    }
    else if (mode == EXTEND_ON_TAIL) {
      mouseDown(e, px, py, dx, dy, pos, mods);
      // basically delete last node and end line
    }
  }

  /** Instructs this tool to respond to a mouse movement. */
  public void mouseMoved(DisplayEvent e, int px, int py, 
      float dx, float dy, int[] pos, int mods) {
    //System.out.println("moved mode = " + mode);
    DisplayImpl display = (DisplayImpl) e.getDisplay();
    double[] movePxl = {(double) px, (double) py}; 

    //printMode("mouseMoved");

    if (overlay.hasToolChanged()) {
      releaseLine();
      mode = WAIT;
    }
    if (mode == WAIT) {
      OverlayObject[] objects = overlay.getObjects();
      int[] ndxNode =  getNearestNode(display, objects, px, py, THRESH);

      if (ndxNode != null) {
        int ndx = ndxNode[0];
        int node = ndxNode[1];
        //System.out.println("near node " + node + " of object " + obj); // TEMP 
        deselectAll();
        line = (OverlayPolyline) objects[ndx];
        selectNode(display, line, node);
        mode = SELECT;
      }
    }
    else if (mode == PLACE) {
      line.setNextNode(dx, dy);
      
      // keep track of curve length
      // using frequent updates to curvelength in EXTEND, etc. instead
      float[] c = line.getNodeCoords(line.getNumNodes()-2);
      double[] cdub = {(double) c[0], (double) c[1]};
      double oldLen = line.getCurveLength();
      line.setCurveLength(oldLen + MathUtil.getDistance(cdub,
            new double[]{dx, dy}));

      mode = BEG_EXTEND;
    }
    else if (mode == EXTEND || mode == EXTEND_ON_TAIL) {
      // update curve 
      adjustLastNode(line, dx, dy);
            
      // determine if near head 
      double hdist = getDistanceToNode(0, px, py, display);
      // determine if near last node placed 
      double ldist = getDistanceToNode(line.getNumNodes() - 2, px, py, display);
      
      // if near ndx, highlight selected node differently 
      int flag = -1; 
      if (ldist < THRESH) 
        if (hdist < ldist) flag = 0;
        else if (hdist > ldist) flag = 1;
        else ;
      else if (hdist < THRESH) flag = 0;

      if (flag == 0) {
        line.setHighlightNode(0, CON);
        mode = CLOSE_LOOP;
      }
      else if (flag == 1) {
        line.setHighlightNode(line.getNumNodes()-1, SEL);
        mode = EXTEND_ON_TAIL;
      }
      else if (flag == -1) {
        line.turnOffHighlighting();
        mode = EXTEND;
      }
    }
    else if (mode == BEG_EXTEND) {
      // update curve length
      adjustLastNode(line, dx, dy);
      
      // determine if near head 
      double hdist = getDistanceToNode(0, px, py, display);
      // determine if near last node placed 
      double ldist = getDistanceToNode(line.getNumNodes() - 2, px, py, display);

      // highlight first or last visible node if near
      if (hdist < THRESH) {
        line.setHighlightNode(line.getNumNodes()-1, CON);
        mode = CLOSE_LOOP;
      }
     
      // switch modes if you've dragged far enough from last node placed
      if (ldist > 10.0) {
        mode = EXTEND;
      }
    }
    else if (mode == CLOSE_LOOP) {
      line.setLastNode(dx, dy);
      // determine if near head: 
      double dist = getDistanceToNode(0, px, py, display);
     
      // if not, turn off highlighting 
      if (dist > THRESH) {
        line.turnOffHighlighting();
        mode = EXTEND; 
      }
    }
    else if (mode == SELECT) {
      // get distance btw. pointer and selectedNode
      float[] nodeFlt = line.getNodeCoords (selectedNode); 
      double[] nodeDbl = {(double) nodeFlt[0], (double) nodeFlt[1]};
      int[] nodePxl = CursorUtil.domainToPixel(display, nodeDbl); 
      double[] nodePxlDbl = {(double) nodePxl[0], (double) nodePxl[1]};
      double dist = MathUtil.getDistance(movePxl, nodePxlDbl); 

      double threshold = 2.0;
      if (dist > threshold) {
        line.turnOffHighlighting();
        unselect();
        mode = WAIT;
      }
    }
    else if (mode == ADJUST) {
    }
    overlay.notifyListeners(new TransformEvent(overlay));
  }

  // -- Helper methods -- 
  
  /** Adjusts last node and curve length */
  private void adjustLastNode (OverlayPolyline line, float dx, float dy) {
    double lastSegLength = getLastSegmentLength();
    line.setLastNode(dx, dy);
    double newLastSegLength = getLastSegmentLength();
    double delta = newLastSegLength - lastSegLength;
    line.setCurveLength(line.getCurveLength() + delta);     
  }
  
  /** Splits an overlay polyline in two */
  private void split (OverlayPolyline line, int selectedNode) {
    float[][] nodes = line.getNodes();
    OverlayPolyline l1, l2;

    int numNodes = line.getNumNodes();

    int numNodes1 = selectedNode;
    int numNodes2 = numNodes - selectedNode - 1;
    // selectedNode is an index into the node array;
    float[][] n1 = new float[2][numNodes1];
    float[][] n2 = new float[2][numNodes2];

    // if a non-trivial polyline remains 'left' of deleted node
    if (selectedNode > 1) {
      for (int i=0; i<2; i++) 
        System.arraycopy(nodes[i], 0, n1[i], 0, numNodes1);

      l1 = new OverlayPolyline(overlay, n1);
      configureOverlay(l1);
      overlay.addObject(l1);
      l1.setDrawing(false);
      l1.setSelected(false);
    }

    // if a non-trivial polyline remains 'right' of deleted node
    if (selectedNode < numNodes - 1) {
      for (int i=0; i<2; i++) {
        System.arraycopy(nodes[i], numNodes1 + 1, n2[i], 0, 
          numNodes2);
      }
      l2 = new OverlayPolyline(overlay, n2);
      configureOverlay(l2);
      overlay.addObject(l2);
      l2.setDrawing(false);
      l2.setSelected(false);
    }
  }


  /** Prints current mode and mouse event type: helpful for debugging */
  private void printMode(String method) {
    String m;
    switch (mode) {
      case ERASE          : m = "erase"; break;
      case WAIT           : m = "wait"; break;
      case EXTEND         : m = "extend"; break;
      case ADJUST         : m = "adjust"; break;
      case SELECT         : m = "select"; break;
      case PLACE          : m = "place"; break;
      case ADJUST_TAIL    : m = "adjust tail"; break;
      case CLOSE_LOOP     : m = "close loop"; break;
      case SELECTED_TAIL  : m = "selected tail"; break;
      case EXTEND_ON_TAIL : m = "extend on tail"; break;
      case BEG_EXTEND     : m = "begin extend"; break;
      default             : m = "unknown mode"; break;
    }

    System.out.println(method + "\t" + m);
  }

  /** Gets distance to the node specified, handling awkward casts. */
  private double getDistanceToNode(int ndx, int px, int py, 
    DisplayImpl display) {
    double[] dPxlDbl = {(double) px, (double) py};
    float[] nDom = line.getNodeCoords(ndx);
    double[] nDomDbl = {(double) nDom[0], (double) nDom[1]};
    int[] nPxl = CursorUtil.domainToPixel(display, nDomDbl);
    double[] nPxlDbl = {(double) nPxl[0], (double) nPxl[1]};
    double dist = MathUtil.getDistance (nPxlDbl, dPxlDbl);
    return dist;
  }

  /** Determines length of last line segment. */ 
  private double getLastSegmentLength() {
    float[][] lastSeg = {line.getNodeCoords(line.getNumNodes() -1), 
      line.getNodeCoords(line.getNumNodes() - 2)};
    double[][] lastSegD = {{(double) lastSeg[0][0], (double) lastSeg[0][1]},
      {(double) lastSeg[1][0], (double) lastSeg[1][1]}};
    return MathUtil.getDistance(lastSegD[0], lastSegD[1]);
  }

  /** Ends drawing of the current line */
  private void releaseLine() {
    if (line != null) {
      line.turnOffHighlighting();
      line.updateBoundingBox();
      line.computeGridParameters();
      line.computeLength();
      line.setDrawing(false);
      line.setSelected(true);
      line = null;
      selectedNode = -1;
    }
  }

  /** Releases a selected node */
  private void unselect() {
    line = null;
    selectedNode = -1;
  }
  
  /** Selects a particular node */
  private void selectNode(DisplayImpl display, OverlayPolyline pln, int node) {
    line.setDrawing(false);
    line.setSelected(true);
    selectedNode = node;
    pln.setHighlightNode(node, SEL);
  }
  
  /** Unhighlights all polylines in a list of OverlayObjects */
  private void unHighlightAllPolylines(OverlayObject[] objects) {
    for (int i=0; i<objects.length; i++) {
      if (objects[i] instanceof OverlayPolyline) 
        ((OverlayPolyline) objects[i]).turnOffHighlighting();
    }
  }

  /** Finds nearest (subject to a threshold) node of all polylines
   *  in a list of OverlayObjects.
   *  @param objects An array of OverlayObjects on this display
   *  Returns an array int[2], with item 0 the index of the nearest polyline
   *  in the objects array, item 1 the index of the nearest node in the nearest
   *  polyline */
  private int[] getNearestNode(DisplayImpl display,
      OverlayObject[] objects, int px, int py, double threshold) {
    Vector polylines = new Vector();
    Vector indices = new Vector();
    double[] p = {(double) px, (double) py};

    for (int i=0; i<objects.length; i++) {
      if (objects[i] instanceof OverlayPolyline) {
        polylines.add(objects[i]);
        indices.add(new Integer(i));
      }
    }

    int nearestPline = -1;
    int nearestNode = -1;
    double minDist = Double.POSITIVE_INFINITY;
    for (int i=0; i<polylines.size(); i++) {
      OverlayPolyline pln = (OverlayPolyline) polylines.get(i);
      for (int j=0; j<pln.getNumNodes(); j++) {
        float[] c = pln.getNodeCoords(j);
        double[] cDbl = {c[0], c[1]}; // auto cast
        int[] cPxl = CursorUtil.domainToPixel(display, cDbl);
        double[] cPxlDbl = {(double) cPxl[0], (double) cPxl[1]};
        double dist = MathUtil.getDistance (cPxlDbl, p);
        if (dist < minDist && dist < threshold) {
          minDist = dist;
          nearestPline = ((Integer) indices.get(i)).intValue();
          nearestNode = j;
        }
      }
    }

    if (nearestPline == -1) return null;
    else return new int[]{nearestPline, nearestNode};
  }
 
  /** Casts an array of floats to doubles */
  private double[][] floatsToPixelDoubles(DisplayImpl d, float[][] nodes) {
    double[][] nodesDbl = new double[nodes.length][nodes[0].length];
    for (int j=0; j<nodes[0].length; j++) {
      int[] c = CursorUtil.domainToPixel(d, new double[]{
        (double) nodes[0][j], (double) nodes[1][j]});
      nodesDbl[0][j] = (double) c[0];
      nodesDbl[1][j] = (double) c[1];
    }
    return nodesDbl;
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
    if (line!=null){
      float[][] nodes = line.getNodes();
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
