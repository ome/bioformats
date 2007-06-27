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
import java.util.Vector;
import loci.visbio.data.TransformEvent;
import loci.visbio.util.MathUtil;
import visad.DisplayEvent;
import visad.DisplayImpl;
import visad.util.CursorUtil;

/** FreeformTool is the tool for creating freeform objects. */
public class PolylineTool extends OverlayTool {

  // -- Constants --
  /** The Different modes of this tool. */
  protected static final int WAIT = 0;
  protected static final int EXTEND = 1;
  protected static final int ADJUST = 2;
  protected static final int SELECT = 3;
  protected static final int PLACE = 4;
  protected static final int ADJUST_TAIL = 5;
  protected static final int CLOSE_LOOP = 6;
  protected static final int SELECTED_TAIL = 7;
  protected static final int EXTEND_ON_TAIL = 8;
  protected static final int BEGIN_EXTEND = 9;

  /** Maximum distance (in pixels) mouse can be from a node to be considered
   *  pointing to it. */
  protected static final double THRESH =
    OverlayNumericStrategy.getPolylineThreshold();

  // -- Fields --

  /** Curve currently being drawn or modified. */
  protected OverlayPolyline line;

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
    mode = WAIT;
    unselect();
  }

  // -- OverlayTool API methods --

  /** Instructs this tool to respond to a mouse press. */
  public void mouseDown(DisplayEvent e, int px, int py,
    float dx, float dy, int[] pos, int mods)
  {
    // printMode("mouseDown"); // use printMode() to help debug this tool.
    boolean ctl = (mods & InputEvent.CTRL_MASK) != 0;
    DisplayImpl display = (DisplayImpl) e.getDisplay();

    if (overlay.hasToolChanged()) {
      releaseLine();
      mode = WAIT;
    }

    if (mode == WAIT) {
      deselectAll();
      line =  new OverlayPolyline(overlay, dx, dy, dx, dy);
      line.setDrawing(true);
      line.setSelected(true);
      overlay.addObject(line, pos);
      mode = PLACE;
    }
    else if (mode == PLACE) {
      if (line.getNumNodes() > 1) {
        releaseLine();
      }
      else {
        overlay.removeObject(line);
        unselect();
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
          // you're near the head node.
          // flip nodes around in case user opts to extend polyline
          line.reverseNodes();
          selectNode(line, line.getNumNodes() - 1);
          mode = SELECTED_TAIL;
        }
        else {
          // you're near some other node
          mode = ADJUST;
        }
        line.setDrawing(true);
      }
      else { // erase
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
          mode = WAIT;
        }
      }
    }
    else if (mode == EXTEND || mode == BEGIN_EXTEND) {
      // line.setLastNode(dx, dy);
      adjustLastNode(line, dx, dy);
      mode = PLACE;
    }
    else if (mode == EXTEND_ON_TAIL) {
      line.deleteNode(line.getNumNodes()-1);
      releaseLine();
      mode = WAIT;
    }

    overlay.notifyListeners(new TransformEvent(overlay));
  } // end mouseDown

  /** Instructs this tool to respond to a mouse drag. */
  public void mouseDrag(DisplayEvent e, int px, int py,
    float dx, float dy, int[] pos, int mods)
  {
    // printMode("mouseDrag");
    DisplayImpl display = (DisplayImpl) e.getDisplay();

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
        line.setHighlightNode(selectedNode);
        mode = CLOSE_LOOP;
      }
      else {
        line.setHighlightNode(selectedNode);
        mode = ADJUST_TAIL;
      }
      overlay.notifyListeners(new TransformEvent(overlay));
    }
    else if (mode == PLACE || mode == EXTEND || mode == BEGIN_EXTEND ||
      mode == EXTEND_ON_TAIL)
    {
      mouseMoved(e, px, py, dx, dy, pos, mods);
    }
  }

  /** Instructs this tool to respond to a mouse release. */
  public void mouseUp(DisplayEvent e, int px, int py,
      float dx, float dy, int[] pos, int mods)
  {
    // printMode("mouseUp");
    DisplayImpl display = (DisplayImpl) e.getDisplay();

    if (overlay.hasToolChanged()) {
      releaseLine();
      mode = WAIT;
    }
    if (mode == ADJUST) {
      line.updateBoundingBox();
      line.computeLength();
      line.setDrawing(false);
      mode = SELECT;
    }
    else if (mode == ADJUST_TAIL) {
      line.updateBoundingBox();
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
      line.computeLength();
      line.setDrawing(false);
      //selectNode(line, line.getNumNodes() - 1);
      mode = SELECT;
    }
    else if (mode == EXTEND_ON_TAIL) {
      mouseDown(e, px, py, dx, dy, pos, mods);
      // basically delete last node and end line
    }
  }

  /** Instructs this tool to respond to a mouse movement. */
  public void mouseMoved(DisplayEvent e, int px, int py,
      float dx, float dy, int[] pos, int mods)
  {
    DisplayImpl display = (DisplayImpl) e.getDisplay();
    // printMode("mouseMoved");

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
        deselectAll();
        line = (OverlayPolyline) objects[ndx];
        selectNode(line, node);
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

      mode = BEGIN_EXTEND;
    }
    else if (mode == EXTEND || mode == EXTEND_ON_TAIL) {
      // change coords of last node to reflect mouse motion and update length
      adjustLastNode(line, dx, dy);

      if (line.getNumNodes() > 2) {
        // determine if near head
        double hdist = getDistanceToNode(0, px, py, display);

        // determine if near last node placed
        double ldist =
          getDistanceToNode(line.getNumNodes() - 2, px, py, display);

        // if near ndx, highlight selected node differently
        int flag = -1;
        if (ldist < THRESH)
          if (hdist < ldist) flag = 0;
          else if (hdist > ldist) flag = 1;
          else ;
        else if (hdist < THRESH) flag = 0;

        if (flag == 0) {
          line.setHighlightNode(0);
          mode = CLOSE_LOOP;
        }
        else if (flag == 1) {
          line.setHighlightNode(line.getNumNodes()-1);
          mode = EXTEND_ON_TAIL;
        }
        else if (flag == -1) {
          line.turnOffHighlighting();
          mode = EXTEND;
        }
      }
    }
    else if (mode == BEGIN_EXTEND) {
      // change coords of last node to reflect mouse motion and update length
      adjustLastNode(line, dx, dy);

      // determine if near head
      double hdist = getDistanceToNode(0, px, py, display);

      // determine if near last node placed
      double ldist =
        getDistanceToNode(line.getNumNodes() - 2, px, py, display);

      // highlight last visible node if near head
      if (line.getNumNodes() >= 3 && hdist < THRESH) {
        line.setHighlightNode(line.getNumNodes()-1);
        mode = CLOSE_LOOP;
      }

      // switch modes if you've dragged far enough from last node placed
      if (ldist > 10.0) {
        mode = EXTEND;
      }
    }
    else if (mode == CLOSE_LOOP) {
      //line.setLastNode(dx, dy);
      adjustLastNode(line, dx, dy);
      // determine if near head:
      double dist = getDistanceToNode(0, px, py, display);

      // if not, turn off highlighting
      if (dist > THRESH) {
        line.turnOffHighlighting();
        mode = EXTEND;
      }
      else { // Turn it on
        selectNode(line, line.getNumNodes() - 1);
      }
    }
    else if (mode == SELECT) {
      // get distance btw. pointer and selectedNode
      double dist = getDistanceToNode(selectedNode, px, py, display);

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
  private void adjustLastNode(OverlayPolyline pln, float dx, float dy) {
    int numNodes = pln.getNumNodes();
    boolean adjust = false;
    if (numNodes > 1) {
      float[] d = {dx, dy};
      float[] p = pln.getNodeCoords(line.getNumNodes()-2);
      if (MathUtil.areDifferent(d, p)) adjust = true;
    }
    else {
      adjust = true;
    }
    if (adjust) {
      double lastSegLength = pln.getLastSegmentLength();
      pln.setLastNode(dx, dy);
      double newLastSegLength = pln.getLastSegmentLength();
      double delta = newLastSegLength - lastSegLength;
      pln.setCurveLength(line.getCurveLength() + delta);
    }
  }

  // TODO -- combine this method with the similar one for freeforms,
  // currently located in OverlayNodedObject.java
  // Determine some way of combining the two.
  /** Splits an overlay polyline in two */
  private void split(OverlayPolyline line, int index) {
    float[][] nodes = line.getNodes();
    OverlayPolyline l1, l2;

    int numNodes = line.getNumNodes();

    int numNodes1 = index;
    int numNodes2 = numNodes - index - 1;
    // index is an index into the node array;
    float[][] n1 = new float[2][numNodes1];
    float[][] n2 = new float[2][numNodes2];

    // if a non-trivial polyline remains 'left' of deleted node
    if (index > 1) {
      for (int i=0; i<2; i++)
        System.arraycopy(nodes[i], 0, n1[i], 0, numNodes1);

      l1 = new OverlayPolyline(overlay, n1);
      overlay.addObject(l1);
      l1.setDrawing(false);
      l1.setSelected(false);
    }

    // if a non-trivial polyline remains 'right' of deleted node
    if (index < numNodes - 2) {
      for (int i=0; i<2; i++) {
        System.arraycopy(nodes[i], numNodes1 + 1, n2[i], 0,
          numNodes2);
      }
      l2 = new OverlayPolyline(overlay, n2);
      overlay.addObject(l2);
      l2.setDrawing(false);
      l2.setSelected(false);
    }
  }

  /**
   * Gets distance to the node specified in pixel coordinates, handling
   * awkward casts.
   */
  private double getDistanceToNode(int ndx, int px, int py,
    DisplayImpl display)
  {
    double[] dPxlDbl = {(double) px, (double) py};
    float[] nDom = line.getNodeCoords(ndx);
    double[] nDomDbl = {(double) nDom[0], (double) nDom[1]};
    int[] nPxl = CursorUtil.domainToPixel(display, nDomDbl);
    double[] nPxlDbl = {(double) nPxl[0], (double) nPxl[1]};
    double dist = MathUtil.getDistance(nPxlDbl, dPxlDbl);
    return dist;
  }

  /** Ends drawing of the current line */
  private void releaseLine() {
    if (line != null) {
      if (line.getNumNodes() > 1) {
        line.turnOffHighlighting();
        line.updateBoundingBox();
        line.computeLength();
        line.setDrawing(false);
        line.setSelected(true);
        unselect();
      }
      else {
        // remove if too few nodes
        overlay.removeObject(line);
      }
    }
  }

  /** Releases a selected node */
  private void unselect() {
    line = null;
    selectedNode = -1;
  }

  /** Selects a particular node */
  private void selectNode(OverlayPolyline pln, int node) {
    line.setDrawing(false);
    line.setSelected(true);
    selectedNode = node;
    pln.setHighlightNode(node);
  }

  /** Finds nearest (subject to a threshold) node of all polylines
   *  in a list of OverlayObjects.
   *  @param objects An array of OverlayObjects on this display
   *  Returns an array int[2], with item 0 the index of the nearest polyline
   *  in the objects array, item 1 the index of the nearest node in the nearest
   *  polyline */
  // TODO can this make use of or be replaced by the OverlayNodedObject instance
  // method with the same name?
  private int[] getNearestNode(DisplayImpl display,
      OverlayObject[] objects, int px, int py, double threshold)
  {
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
        double dist = MathUtil.getDistance(cPxlDbl, p);
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

  // -- Helper methods for debugging --

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

  /** Prints a message for debugging. */
  public void print(String methodName, String message) {
    boolean toggle = true;
    if (toggle) {
      String header = "FreeformTool.";
      String out = header + methodName + ": " + message;
      System.out.println(out);
    }
  }

  /** Prints current mode and mouse event type: helpful for debugging. */
  private void printMode(String method) {
    String m;
    switch (mode) {
      case WAIT           : m = "wait"; break;
      case EXTEND         : m = "extend"; break;
      case ADJUST         : m = "adjust"; break;
      case SELECT         : m = "select"; break;
      case PLACE          : m = "place"; break;
      case ADJUST_TAIL    : m = "adjust tail"; break;
      case CLOSE_LOOP     : m = "close loop"; break;
      case SELECTED_TAIL  : m = "selected tail"; break;
      case EXTEND_ON_TAIL : m = "extend on tail"; break;
      case BEGIN_EXTEND   : m = "begin extend"; break;
      default             : m = "unknown mode"; break;
    }

    System.out.println(method + "\t" + m);
  }
} // end class PolylineTool
