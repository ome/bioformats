//
// FreeformExtension.java
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

import loci.visbio.util.MathUtil;
import visad.DisplayImpl;
import visad.util.CursorUtil;

/** 
 * The FreeformExtension class wraps information for the temporary section of a 
 * freeform, which appears like a tendril on screen, created during an edit
 * of an existing freeform. 
 * The FreeformTool uses this information to modify the freeform if the
 * edit is successful, or to remove the freeform extension if the edit is
 * aborted.
 *
 * The section of the freeform represented by a freeform extension is actually a
 * loop, but it's thin (both sides overlap exactly) so it appears like a single
 * line. 
 * The loop has two parts, between start and tip and between tip and stop.
 * If an edit is completed, half of the freeform extension is deleted along
 * with the subtended section of the original freeform.  As a result, the 
 * tendril appears to be spliced into the old freeform. 
 */
public class FreeformExtension {
  
  // -- Static Fields --

  /** Smoothing factor for "single exponential smoothing" */
  protected static final float SMOOTHING_FACTOR = (float)
    OverlayNumericStrategy.getSmoothingFactor();

  /**
   * When drawing or editing, how far mouse
   * must be dragged before new node is added.
   */
  protected static final double DRAW_THRESH =
    OverlayNumericStrategy.getDrawThreshold();

  /**
   * Threshhold within which click must occur to
   * invoke extend mode or reconnect a freeformExtension.
   */
  protected static final double RECONNECT_THRESH =
    OverlayNumericStrategy.getReconnectThreshold();

  // -- Fields --

  /** The freeform to which this extension is attached. */
  protected OverlayFreeform freeform;

  /** The index of the last node of the freeform extension. */
  protected int start;

  /** The index of the last node of the freeform extension. */
  protected int stop; 

  /** The index of the tip node of the freeform extension. */
  protected int tip;

  /** Chunks of curve before and after the extension. */
  protected float[][] pre, post;

  /** 
   * Whether this freeform extension began on a node or in the middle of a
   * segment. 
   */
  protected boolean nodal;

  // -- Constructor --

  /** Constructs a new freeform extension. */
  public FreeformExtension(OverlayFreeform freeform, int start, int stop,
      boolean nodal) {
    this.freeform = freeform;
    this.start = start;
    this.stop = stop;
    this.nodal = nodal;
    // initial value of tip is nonsensical.  Use as a check.
    this.tip = -1;

    if (nodal) splitNodes (freeform, start - 1, stop + 1);
    else splitNodes (freeform, start, stop + 1);
  }

  // -- Object API Methods --
  
  /** Whether this extension has begun or remains in the initial state. */
  public boolean hasBegun() { return (tip >=0); }

  /** Extends the extension, reconnecting it with the curve proper if 
   *  appropriate. */
  public boolean extendOrReconnect(DisplayImpl display, float dx, float dy, int
      px, int py, boolean shift) {
    boolean reconnected = false;

    double dpx = (double) px;
    double dpy = (double) py;
    double[] distSegWtPre = getDistanceToSection(pre, display, dpx, dpy);
    double[] distSegWtPost = getDistanceToSection(post, display, dpx, dpy);

    boolean equalsCase = false;
    if (distSegWtPre[0] == distSegWtPost[0]) equalsCase = true;
    // Drag is equally close to both segments, wait for next drag.
    // This case is extremely unlikely.

    boolean closerToPost = (distSegWtPre[0] > distSegWtPost[0]);
    double[] distSegWt = (closerToPost) ? distSegWtPost : distSegWtPre;

    double minDist = distSegWt[0];
    int seg = (int) distSegWt[1];
    double weight = distSegWt[2];

    double dragDist = computeDragDist(display, px, py);

    // if not close to curve insert node, else reconnect
    if (minDist > RECONNECT_THRESH) {
      // insert a node at the drag point if drag went far enough
      if (dragDist > DRAW_THRESH) {
        float[] prev = getTipCoords();
        float[] s = OverlayUtil.smooth (new float[] {dx, dy}, prev,
            SMOOTHING_FACTOR);
        extend(s);
      }
    }
    else if (!shift && !equalsCase) {
      // reconnect with curve and delete nodes;
      if (hasBegun()){
        //if (dragDist > DRAW_THRESH) {
          // insert a node first at drag point, then reconnect
          // skip this case:  There's the possibility that this drag point
          // is across the curve from the previous drag point, which might
          // result in a unintended zig-zag.
        //}

        reconnect(seg, weight, closerToPost);
        reconnected = true;
      }
    } // end reconnect logic
    return reconnected;
  }

  /** Computes distance between a point and a section of the freeform. */
  public double[] getDistanceToSection(float[][] section, DisplayImpl display,
      double dpx, double dpy) {
    double[][] dbl = OverlayUtil.floatsToPixelDoubles(display, section);
    double[] distSegWt = MathUtil.getDistSegWt(dbl, dpx, dpy);
    return distSegWt;
  }

  /** Computes distance between mouse drag and tip of extension. **/
  public double computeDragDist(DisplayImpl display, int px, int py) {
    // coords of this mouseDrag event
    float[] prvCrdsFlt = getTipCoords();
    double[] prvCrdsDbl= {(double) prvCrdsFlt[0], (double) prvCrdsFlt[1]};
    int[] prvCrdsPxl = CursorUtil.domainToPixel(display, prvCrdsDbl);
    double[] prvCrdsPxlDbl = {(double) prvCrdsPxl[0], (double) prvCrdsPxl[1]};

    double[] drag = {(double) px, (double) py};
    double dragDist = MathUtil.getDistance(drag, prvCrdsPxlDbl);
    return dragDist;
  }

  /** Extends the tip of the freeform extension by "one" node. */
  public void extend(float[] c) {
    if (tip < 0) {
      freeform.insertNode(stop, c[0], c[1], true);
      stop++;
      tip = start + 1;
    }
    else { // later drags
      float[] prev = getTipCoords();
      freeform.insertNode(tip+1, prev[0], prev[1], true);
      freeform.insertNode(tip+1, c[0], c[1], true);
      tip++;
      stop += 2;
    }
  }

  /** Reconnects the extension with the freeform. */
  public void reconnect(int seg, double weight, boolean closerToPost) {
    // insert node at nearest point
    // offset points to either post[0] or pre[0]
    int offset = closerToPost ? (stop + 1) : 0;

    int endIndex;
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
      float[] newXY = MathUtil.computePtOnSegment(a, b, (float) weight);
      int insertIndex = seg + 1 + offset;
      freeform.insertNode(insertIndex, newXY[0], newXY[1], true);
      if (!closerToPost) incrementAll();
      endIndex = insertIndex;
    }

    if (tip < endIndex) {
      freeform.deleteBetween(tip, endIndex);
    }
    else {
      freeform.deleteBetween(endIndex, tip);
    }
  }

  /** Returns the coordinates of the extension's tip or start node. */
  public float[] getTipCoords() { 
    float[] prev;
    if (tip >= 0) {
      // previous node is tendril.tip
      prev = freeform.getNodeCoords(tip);
    }
    else {
      // previous node is tendril.start
      prev = freeform.getNodeCoords(start);
    }
    return prev;
  }
  
  /** 
   * Simultaneously increments all extension pointers into node array, 
   * to adjust for the insertion of a node before the base of the extension. 
   */
  public void incrementAll() {
    tip++;
    start++;
    stop++;
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
}
