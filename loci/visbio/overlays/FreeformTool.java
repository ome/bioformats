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
import java.util.Vector;
import visad.DisplayEvent;
import visad.DisplayImpl;
import visad.util.CursorUtil;

//goals:
// eliminate errors in code
// make code readable
// consolidate code where possible

/** FreeformTool is the tool for creating freeform objects. */
public class FreeformTool extends OverlayTool {

  // -- Constants --

  /**
   * When drawing or editing, how far mouse
   * must be dragged before new node is added.
   */
  protected static final double DRAW_THRESH = 2.0;

  /**
   * How close a mouseDrag event must be to a node
   * in order to erase it
   */
  protected static final double ERASE_THRESH = 10.0;

  /** Threshhold within which click must occur to invoke edit mode. */
  protected static final double EDIT_THRESH = 6.0;

  /**
   * Threshhold within which click must occur to
   * invoke extend mode or reconnect a tendril.
   */
  protected static final double RECONNECT_THRESH = 1.0;

  /** How close mouse must be to end node to resume drawing */
  protected static final double RESUME_THRESH = 10.0;

  /** Smoothing factor for "single exponential smoothing" */
  protected static final float S = 0.35f;

  /** Constant for "erase" mode. */
  protected static final int ERASE = -1;

  /** Constant for "wait" mode. */
  protected static final int CHILL = 0; // could call this WAIT...

  /** Consant for "draw" mode. */
  protected static final int DRAW = 1;

  /** Constant for "edit" mode. */
  protected static final int EDIT = 2;

  /** Constant for "init" mode */
  protected static final int INIT = 3;

  // -- Fields --

  /** Curve currently being drawn or modified. */
  protected OverlayFreeform freeform;

  /** Other freeforms on the canvas */
  protected Vector otherFreefs;

  /** Tendril wraps info about an edit to a curve */
  protected Tendril tendril;

  /** Stores the current mode (ERASE, CHILL, DRAW, EDIT) */
  protected int mode;

  /** Chunks of curve tracked during EDIT */
  protected float[][] pre, post;

  /** Point at which mouseDown occurs. */
  protected float downX, downY;

  // -- Constructor --

  /** Constructs a freeform creation tool. */
  public FreeformTool(OverlayTransform overlay) {
    super(overlay, "Freeform", "Freeform", "freeform.png");
    tendril = null;
    otherFreefs = new Vector();
    setMode(CHILL);
  }

  // -- OverlayTool API methods --

  /** Instructs this tool to respond to a mouse press. */
  public void mouseDown(DisplayEvent e, int px, int py,
    float dx, float dy, int[] pos, int mods)
  {
    boolean ctl = (mods & InputEvent.CTRL_MASK) != 0;
    DisplayImpl display = (DisplayImpl) e.getDisplay();

    double dpx = (double) px;
    double dpy = (double) py;

    // find closest freeform
    double maxThresh = ERASE_THRESH;
    DistanceQuery distanceQuery = getClosestFreeform(display, dpx,
        dpy, maxThresh);
    OverlayFreeform target = distanceQuery.freeform;

    // operate on closest freeform
    if (target != null) {
      // editing operations on closest freeform
      freeform = target;

      double dist = distanceQuery.dist;
      int seg = distanceQuery.seg;
      double weight = distanceQuery.wt;

      if (distanceQuery.isNearEndNode())
      {
        // near an end node
        if (ctl && dist < ERASE_THRESH) {
          // enter erase mode
          setMode(ERASE);
        }
        else if (dist < RESUME_THRESH) {
          // resume drawing
          if (seg == 0) freeform.reverseNodes();
          setMode(DRAW);
        }
      }
      else {
        // near an interior node
        if (ctl && dist < ERASE_THRESH) {
          // split and enter erase mode
          slice(freeform, dist, seg, weight);
          freeform = null;
        }
        else if (dist < EDIT_THRESH) {
          // begin editing
          // project click onto curve and insert a new node there,
          // unless nearest point on curve is a node itself
          //
          // note: because of the way getDistSegWt() works, weight can
          // never be 0.0 except if seg = 0.
          if (weight == 1.0) { // nearest point on seg is the end node
            // determine projection on seg.
            float[] a = freeform.getNodeCoords(seg+1);
            freeform.insertNode(seg+2, a[0], a[1]);
            tendril = new Tendril (seg+1, seg+2, true);
            splitNodes (freeform, seg, seg+3);
          }
          else {
            // determine projection on seg.
            // insert a pair of nodes there, starting a tendril
            float[] a = freeform.getNodeCoords(seg);
            float[] b = freeform.getNodeCoords(seg + 1);
            float[] newXY = computePtOnSegment(a, b, (float) weight);
            freeform.insertNode(seg + 1, newXY[0], newXY[1]);
            freeform.insertNode(seg + 1, newXY[0], newXY[1]);
            tendril = new Tendril(seg+1, seg+2, false);
            splitNodes (freeform, seg+1, seg+3);
          }
          setMode(EDIT);
        }
      }
    }
    else { // no freeform was sufficiently close
      // drawing operations on new freeform
      if (!ctl) {
        // record initial coordinates of freeform
        downX = dx;
        downY = dy;
        setMode(INIT);
      }
    }

    // stuff you have to do if there's been a change
    overlay.notifyListeners(new TransformEvent(overlay));
    ((OverlayWidget) overlay.getControls()).refreshListSelection();
  } // end mouseDown

  /** Instructs this tool to respond to a mouse drag. */
  public void mouseDrag(DisplayEvent e, int px, int py,
    float dx, float dy, int[] pos, int mods)
  {
    boolean shift = (mods & InputEvent.SHIFT_MASK) != 0;
    boolean ctl = (mods & InputEvent.CTRL_MASK) != 0;

    DisplayImpl display = (DisplayImpl) e.getDisplay();

    double dpx = (double) px;
    double dpy = (double) py;

    if (mode == INIT) {
      freeform = new OverlayFreeform(overlay, downX, downY, downX, downY);
      configureOverlay(freeform);
      overlay.addObject(freeform, pos);
      setMode(DRAW);
    }

    if (ctl && mode == DRAW) {
        freeform.truncateNodeArray();
        setMode(ERASE);
    }

    if (mode == DRAW) {
      // compute distance to endpoints of nearby freeforms
      int index = -1;
      boolean closerToHead = false;
      double minDist = Double.MAX_VALUE;

      for (int i=0; i<otherFreefs.size(); i++) {
        OverlayFreeform f = (OverlayFreeform) otherFreefs.get(i);
        float[] head = f.getNodeCoords(0);
        float[] tail = f.getNodeCoords(f.getNumNodes()-1);

        int[] drag = new int[]{px, py};
        double hdist = getPixelDistanceToClick(display, head, drag);
        double tdist = getPixelDistanceToClick(display, tail, drag);

        boolean isHead = hdist < tdist ? true : false;
        double dist = isHead ? hdist : tdist;

        if (dist < minDist) {
          minDist = dist;
          index = i;
          closerToHead = isHead;
        }
      }

      if (minDist < DRAW_THRESH) {
        connectFreeforms(freeform,
          (OverlayFreeform) otherFreefs.get(index), closerToHead);
        setMode(CHILL);
      }
      else {
        // compute distance from last node
        // DISTANCE COMPUTATION: compare floats and ints
        float[] last = {freeform.getLastNodeX(), freeform.getLastNodeY()};
        double distPxl = getPixelDistanceToClick(display, last, 
            new int[]{px, py});

        if (distPxl > DRAW_THRESH) {
          float[] s = smooth(new float[]{dx, dy}, last);
          freeform.setNextNode(s);
          double len = freeform.getCurveLength();
          double delta = MathUtil.getDistance(s, last);
          freeform.setCurveLength(len + delta);
          // I debated whether to call setBoundaries with every mouseDrag.
          // This is an efficient
          // method, but corresponding update when erasing
          // erasing requires an O(n)
          // scan of the nodes every time a node is deleted.
          freeform.setBoundaries(s[0], s[1]);
        }
        // mode remains DRAW
      }
    }
    else if (mode == ERASE) {
      if (freeform == null) {
        // DISTANCE COMPUTATION compare floats with floats
        DistanceQuery distanceQuery = getClosestFreeform(display,
            dpx, dpy, ERASE_THRESH);
        OverlayFreeform target = distanceQuery.freeform;
        if (target != null) freeform = target;
      }

      if (freeform != null) {
        // delete an end node if you're near enough
        float[] beg = freeform.getNodeCoords(0);
        float[] end = freeform.getNodeCoords(freeform.getNumNodes() - 1);

        int[] dragPxl = {px, py};

        double bdist = getPixelDistanceToClick(display, beg, dragPxl);
        double edist = getPixelDistanceToClick(display, beg, dragPxl); 

        boolean closerToEnd = edist < bdist ? true : false;
        double mdist = closerToEnd ? edist : bdist;

        if (mdist < ERASE_THRESH) {
          if (!closerToEnd) freeform.reverseNodes();
          if (ctl) {
            float[] nearest = new float[2];
            if (closerToEnd) nearest = end;
            else nearest = beg;

            // adjust curve length
            int index = freeform.getNumNodes()-1; // last node in freef
            float[] last = freeform.getNodeCoords(index);
            double delta = MathUtil.getDistance(nearest, last);
            freeform.setCurveLength(freeform.getCurveLength() - delta);

            // delete last node
            freeform.deleteNode(index);
            if (!freeform.hasData()) {
             setMode(CHILL);
            }
          }
          else setMode(DRAW);
        }
      }
      // do nothing if too far from curve
    }
    else if (mode == EDIT) {
      // extend tendril and or reconnect
      // get coords of prev node
      float[] prvCrdsFlt;
      if (tendril.tip >= 0) {
        // previous node is tendril.tip
        prvCrdsFlt = freeform.getNodeCoords(tendril.tip);
      }
      else {
        // previous node is tendril.start
        prvCrdsFlt = freeform.getNodeCoords(tendril.start);
      }

      double[] prvCrdsDbl= {(double) prvCrdsFlt[0], (double) prvCrdsFlt[1]};
      int[] prvCrdsPxl = CursorUtil.domainToPixel(display, prvCrdsDbl);
      double[] prvCrdsPxlDbl = {(double) prvCrdsPxl[0], (double) prvCrdsPxl[1]};

      // coords of this mouseDrag event
      double[] drag = {dpx, dpy};
      double dragDist = MathUtil.getDistance(drag, prvCrdsPxlDbl);

      // compute distance to pre, post chunks of curve
      // floats and ints
      double[][] preDbl = floatsToPixelDoubles(display, pre);
      double[][] postDbl = floatsToPixelDoubles(display, post);

      double[] distSegWtPre = MathUtil.getDistSegWt(preDbl, dpx, dpy);
      double[] distSegWtPost = MathUtil.getDistSegWt(postDbl, dpx, dpy);

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
          float[] s = smooth (new float[] {dx, dy}, prvCrdsFlt);
          if (tendril.tip < 0) {
            freeform.insertNode(tendril.stop, s[0], s[1]);
            tendril.stop++;
            tendril.tip = tendril.start + 1;
          }
          else { // later drags
            freeform.insertNode(tendril.tip+1, prvCrdsFlt[0], prvCrdsFlt[1]);
            freeform.insertNode(tendril.tip+1, s[0], s[1]);
            tendril.tip++;
            tendril.stop += 2;
          }
        }
        // tendril.tip always points to a lone node (except for before first
        // drag, where it doesn't point to a node at all)
      }
      else if (!shift && !equalsCase) {
        // reconnect with curve and delete nodes;
        if (tendril.tip < 0) {
          // tendril hasn't begun.  Do nothing.
        }
        else {
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
          }
          else if (weight == 1.0) {
            endIndex = seg + 1 + offset;
          }
          else {
            // determine projection on seg.
            float[] a = freeform.getNodeCoords(seg + offset);
            float[] b = freeform.getNodeCoords(seg + 1 + offset);
            float[] newXY = computePtOnSegment(a, b, (float) weight);
            int insertIndex = seg + 1 + offset;
            freeform.insertNode(insertIndex, newXY[0], newXY[1]);
            if (!closerToPost) {
              tendril.increment();
            }
            endIndex = insertIndex;
          }

          if (tendril.tip < endIndex) {
            freeform.deleteBetween(tendril.tip, endIndex);
          }
          else {
            freeform.deleteBetween(endIndex, tendril.tip);
          }
          freeform.computeLength();
          setMode(CHILL);
        }
      } // end reconnect logic

    }
    overlay.notifyListeners(new TransformEvent(overlay));
  } // end mouseDrag

  /** Instructs this tool to respond to a mouse release. */
  public void mouseUp(DisplayEvent e, int px, int py,
      float dx, float dy, int[] pos, int mods) {
    //print ("mouseUp", "mouseUp begun. mode = " + mode);
    if (mode == DRAW) {
      freeform.truncateNodeArray();
    }
    else if (mode == EDIT) {
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

  // -- Helper methods for mouse methods

  /** Calculates smoothed coordinates using "single exponential smoothing"
   *  as described in Littlewood and Inman, _Computer-assisted DNA length
   *  measurements..._. Nucleic Acids Research, V 10 No. 5. (1982) p. 1694
   */
  private float[] smooth (float[] un, float[] cn1) {
    float[] cn = new float[2];
    for (int i=0; i<2; i++) {
      cn[i] = S * un[i] + (1 - S) * cn1[i];
    }
    return cn;
  }

  /** Slices a freeform in two. */
  private void slice(OverlayFreeform freef, double dist, int seg, double weight)
  {
    int f1Start, f2Start, f1Stop, f2Stop;
    OverlayFreeform f1, f2;

    f1Start = 0;
    f1Stop = seg;
    f2Start = seg + 1;
    f2Stop = freef.getNumNodes() - 1;

    // if nearest point is a node itself, exclude it from both halves
    if (weight == 0.0) f1Stop = seg - 1;
    else if (weight == 1.0) f2Start = seg + 2;

    float[][] oldNodes = freef.getNodes();
    int numNodes1 = f1Stop + 1;
    int numNodes2 = f2Stop - f2Start + 1;

    if (numNodes1 > 1) {
      float[][] f1Nodes = new float[2][numNodes1];

      for (int i=0; i<2; i++) {
        System.arraycopy(oldNodes[i], 0, f1Nodes[i], 0, numNodes1);
      }

      f1 = new OverlayFreeform(overlay, f1Nodes);
      configureOverlay(f1);
      overlay.addObject(f1);
      f1.setSelected(false);
      f1.setDrawing(false);
    }

    if (numNodes2 > 1) {
      float[][] f2Nodes = new float[2][numNodes2];

      for (int i = 0; i<2; i++) {
        System.arraycopy(oldNodes[i], f2Start, f2Nodes[i], 0, numNodes2);
      }

      f2 = new OverlayFreeform(overlay, f2Nodes);
      configureOverlay(f2);
      overlay.addObject(f2);
      f2.setSelected(false);
      f2.setDrawing(false);
    }

    // quietly dispose of the original freeform.
    overlay.removeObject(freef);
  }

  /** Wraps distance and address of a freeform object. */
  protected class DistanceQuery {
    public double dist, wt;
    public int seg;
    public OverlayFreeform freeform;

    /** Constructs a DistanceQuery object. */
    public DistanceQuery(double[] distSegWt, OverlayFreeform f) {
      this.freeform = f;
      this.dist = distSegWt[0];
      this.seg = (int) distSegWt[1];
      this.wt = distSegWt[2];
    }

    /** Whether the nearest node is an end node. */
    public boolean isNearEndNode() {
      return (seg == 0 && wt == 0.0) ||
        (seg == freeform.getNumNodes()-2 && wt == 1.0);
    }
  }

  /** Wraps information for redrawing a curve
   *  Appears like a 'tendril' on screen */
  protected class Tendril {
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

  /**
   * Splits the node array into two parts.  The first part goes from a[0] to
   * a[index-1], the second from a[index2] to a[a.length -1].
   */
  private void splitNodes(OverlayFreeform freeform, int index, int index2) {
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

  /**
   * Changes the edit mode. Depending on the new mode, some properties may
   * change: e.g., whether a freeform is selected in the list of overlays.
   */
  private void setMode(int newMode) {
    mode = newMode;

    if (mode == INIT) {
    }
    else if (mode == DRAW) {
      deselectAll();
      freeform.setDrawing(true);
      freeform.setSelected(true);

      // create list of freeforms
      OverlayObject[] objs = overlay.getObjects();
      for (int i=0; i<objs.length; i++) {
        if (objs[i] instanceof OverlayFreeform && objs[i] != freeform) {
          otherFreefs.add(objs[i]);
        }
      }
    }
    else if (mode == EDIT) {
      deselectAll();
      freeform.setDrawing(true);
      freeform.setSelected(true);
      otherFreefs.removeAllElements();
    }
    else if (mode == ERASE) {
      deselectAll();
      freeform.setDrawing(true);
      freeform.setSelected(true);
      otherFreefs.removeAllElements();
    }
    else if (mode == CHILL) {
      otherFreefs.removeAllElements();
      if (freeform != null) {
        freeform.setDrawing(false);
        if (!freeform.hasData()) {
          overlay.removeObject(freeform);
        }
        else {
          freeform.computeLength();
          freeform.updateBoundingBox();
          freeform.setSelected(true);
          freeform = null;
        }
      }
    }
  }

  /**
   * Connects a pair of freeforms.
   * @param f1 the freeform being drawn.
   * @param f2 the freeform to be appended to f2.
   * @param head whether the tail of f1 should be connected to the head
   *   (or if not, the tail) of f2.
   */
  private void connectFreeforms(OverlayFreeform f1, OverlayFreeform f2,
    boolean head)
  {
    if (!head) f2.reverseNodes();
    float[][] newNodes = new float[2][f1.getNumNodes()+f2.getNumNodes()];

    for (int i=0; i<2; i++) {
      System.arraycopy(f1.getNodes()[i], 0, newNodes[i], 0, f1.getNumNodes());
      System.arraycopy(f2.getNodes()[i], 0, newNodes[i],
        f1.getNumNodes(), f2.getNumNodes());
    }

    OverlayFreeform f3 = new OverlayFreeform (overlay, newNodes);
    overlay.removeObject(f1);
    overlay.removeObject(f2);
    configureOverlay(f3);
    overlay.addObject(f3);
    freeform = f3; // store the new freeform
  }

  /** Returns the closest (subject to a threshhold) OverlayFreeform object to
   *  the given point
   */
  protected DistanceQuery getClosestFreeform(DisplayImpl display, double dpx,
      double dpy, double thresh) {
    // returns only objects at the current dimensional position
    OverlayObject[] objects = overlay.getObjects();
    // Q: Hey, are all of these OverlayFreeforms?
    // A: No, it returns OverlayObjects of all types

    OverlayFreeform closestFreeform = null;
    double[] min = {Double.MAX_VALUE, 0.0, 0.0};
    if (objects != null) {
      for (int i = 0; i < objects.length; i++) {
        OverlayObject currentObject = objects[i];
        if (currentObject instanceof OverlayFreeform) {
          OverlayFreeform currentFreeform = (OverlayFreeform) currentObject;
          // performance opportunity:
          // I removed the check on the bounding box
          // Should the getDistance method return a distance in
          // pixel coordinates?
          double[][] nodesDbl = floatsToPixelDoubles(display,
              currentFreeform.getNodes());
          double[] distSegWt =
            // DISTANCE COMPUTATION
            // compare float[][] with ints
            MathUtil.getDistSegWt(nodesDbl, dpx, dpy);
          if (distSegWt[0] < thresh && distSegWt[0] < min[0]) {
            min = distSegWt;
            closestFreeform = currentFreeform;
          }
        } // end if
      } // end for
    } // end if
    return new DistanceQuery(min, closestFreeform);
  }

  /**
   * Computes a point along the line segment
   * a[]-b[] (2D) based on parameter weight.
   */
  private float[] computePtOnSegment(float[] a, float[] b, float weight) {
    float dx = b[0] - a[0];
    float dy = b[1] - a[1];
    float newX = (float) (a[0] + dx * weight);
    float newY = (float) (a[1] + dy * weight);
    float[] retvals = {newX, newY};
    return retvals;
  }

  /** Gets distance in pixels between a click and a point in domain coords. */
  private double getPixelDistanceToClick(DisplayImpl display, float[] p, int[]
      click) {
    // TODO Make this work for N-D arguments (not just 2D)
    double[] pDbl = new double[] {(double) p[0], (double) p[1]};
    int[] pPxl = CursorUtil.domainToPixel(display, pDbl);
    double[] pPxlDbl = new double[] {(double) pPxl[0],
      (double) pPxl[1]};
    double[] clickDbl = new double[] {(double) click[0],
      (double) click[1]};
    return MathUtil.getDistance(clickDbl, pPxlDbl);
  }

  /** Casts an array of floats to doubles. */
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

  /** Prints node array of current freeform; for debugging. */
  private void printNodes(float[][] nodes) {
    System.out.println("Printing nodes...");
    for (int i = 0; i < nodes[0].length; i++){
      System.out.println(i+":("+nodes[0][i]+","+nodes[1][i]+")");
    }
  }

  /** Prints node array of current freeform; for debugging. */
  private void printNodes() {
    if (freeform!=null){
      float[][] nodes = freeform.getNodes();
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
} // end class FreeformTool
