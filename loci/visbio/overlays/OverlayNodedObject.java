//
// OverlayNodedObject.java
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

import java.awt.Color;
import java.rmi.RemoteException;
import java.util.Arrays;
import loci.visbio.util.MathUtil;
import visad.*;

/**
 * OverlayNodedObject is an overlay object built of nodes likely
 * connected by lines.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/visbio/overlays/OverlayNodedObject.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/visbio/overlays/OverlayNodedObject.java">SVN</a></dd></dl>
 */

public abstract class OverlayNodedObject extends OverlayObject {

  // -- Static Fields --

  /** The names of the statistics this object reports. */
  protected static final String BOUNDS = "Bounds";
  protected static final String NODES = "Number of Nodes";
  protected static final String LENGTH = "Length";
  protected static final String[] STAT_TYPES =  {BOUNDS, NODES, LENGTH};

  // -- Constants --

  /** Computed (X, Y) pairs for top 1/2 of a unit circle. */
  protected static final float[][] ARC = arc();

  /** Computes the top 1/2 of a unit circle. */
  private static float[][] arc() {
    int res = 8; // resolution for 1/8 of circle
    float[][] arc = new float[2][4 * res];
    for (int i=0; i<res; i++) {
      float t = 0.5f * (i + 0.5f) / res;
      float x = (float) Math.sqrt(t);
      float y = (float) Math.sqrt(1 - t);

      arc[0][i] = -y;
      arc[1][i] = x;

      int i1 = 2 * res - i - 1;
      arc[0][i1] = -x;
      arc[1][i1] = y;

      int i2 = 2 * res + i;
      arc[0][i2] = x;
      arc[1][i2] = y;

      int i3 = 4 * res - i - 1;
      arc[0][i3] = y;
      arc[1][i3] = x;
    }
    return arc;
  }

  /** Alpha value for highlighting. */
  protected static final float HLT_ALPHA = 0.5f;

  /** Radius in pixels of circle indicating a node is selected. */
  protected static final float RADIUS = 3.0f;

  // -- Fields --

  /** Synchronization object for nodes array. */
  protected Object nodesSync = new Object();

  /** Node array and associated tracking variables. */
  protected float[][] nodes;

  /** Number of real nodes in the node array. */
  protected int numNodes;

  /** Total number of nodes (real + buffer) nodes in the node array. */
  protected int maxNodes;

  /** Length of curve of a noded object. */
  protected double curveLength;

  /** Whether there is a higlighted node. */
  protected boolean highlightNode;

  /** Index of the highlighted node. */
  protected int highlightIndex;

  // -- Constructors --

  /** Constructs an uninitialized noded object. */
  public OverlayNodedObject(OverlayTransform overlay) {
    super(overlay);
    turnOffHighlighting();
  }

  /** Constructs a noded object. */
  public OverlayNodedObject(OverlayTransform overlay,
    float x1, float y1, float x2, float y2)
  {
    super(overlay);
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
    synchronized (nodesSync) {
      maxNodes = 100;
      nodes = new float[2][maxNodes];
      Arrays.fill(nodes[0], x1);
      Arrays.fill(nodes[1], y1);
      numNodes = 1;
    }
    computeLength();
    turnOffHighlighting();
  }

  /** Constructs a noded object from an array of nodes. */
  public OverlayNodedObject(OverlayTransform overlay, float[][] nodes) {
    super(overlay);
    x1=x2=y1=y2=0f;
    synchronized (nodesSync) {
      this.nodes = nodes;
      numNodes = nodes[0].length;
      maxNodes = nodes[0].length;
    }
    updateBoundingBox();
    computeLength();
    turnOffHighlighting();
  }

  // -- Static methods --

  /** Returns the names of the statistics this object reports. */
  public static String[] getStatTypes() { return STAT_TYPES; }

  // -- OverlayObject API methods --

  /** Returns whether this object is drawable, i.e., is of nonzero
   *  size, area, length, etc.  */
  public boolean hasData() {
    synchronized (nodesSync) {
      if (isDrawing()) return (numNodes > 0);
      else return (numNodes > 1);
    }
    // NOTE: Not exactly consistent with the other overlay objects.
    // You want to see 1-node objects while drawing, but
    // want to delete them after drawing is done.
  }

  /** Gets VisAD data object representing this overlay. */
  public DataImpl getData() {
    if (!hasData()) return null;

    RealTupleType domain = overlay.getDomainType();
    TupleType range = overlay.getRangeType();

    // ******************************************************************
    // build nodes set and assign nodes range samples
    // ******************************************************************
    SampledSet fieldSet = null;
    try {
      synchronized (nodesSync) {
        fieldSet = new Gridded2DSet(domain,
          nodes, maxNodes, null, null, null, false);
      }

      // I've written !isDrawing() to prevent a manifold dimension mismatch
      // that occurs when drawing filled polylines
      if (filled && !isDrawing()) {
        Irregular2DSet roiSet =
          DelaunayCustom.fillCheck((Gridded2DSet) fieldSet, false);
        if (roiSet != null) fieldSet = roiSet;
      }
    }
    catch (VisADException exc) { exc.printStackTrace(); }

    // fill nodes range samples
    Color col = selected ? GLOW_COLOR : color;
    float r = col.getRed() / 255f;
    float g = col.getGreen() / 255f;
    float b = col.getBlue() / 255f;

    if (fieldSet == null) System.out.println("yow!");
    FlatField field = null;
    try {
      float[][] rangeSamples = new float[4][fieldSet.getLength()];
      Arrays.fill(rangeSamples[0], r);
      Arrays.fill(rangeSamples[1], g);
      Arrays.fill(rangeSamples[2], b);
      Arrays.fill(rangeSamples[3], 1.0f);

      FunctionType fieldType = new FunctionType(domain, range);
      field = new FlatField(fieldType, fieldSet);
      field.setSamples(rangeSamples);
    }
    catch (VisADException exc) {
      exc.printStackTrace();
      System.out.println("isHighlightNode() = " + isHighlightNode());
      System.out.println("filled  = " + filled);
      System.out.println("Thread.currentThread()" + Thread.currentThread());
      System.out.println("maxNodes = " + maxNodes);
      System.out.println("numNodes = " + numNodes);
      try {
        System.out.println("fieldSet.getLength = " + fieldSet.getLength());
      }
      catch (VisADException exc2) { exc2.printStackTrace(); }
    }
    catch (RemoteException exc) { exc.printStackTrace(); }

    return field;
  }

  /** Compute the shortest distance from this object to the given point. */
  public double getDistance(double x, double y) {
    synchronized (nodesSync) {
      double[] distSegWt = MathUtil.getDistSegWt(nodes, (float) x, (float) y);
      return distSegWt[0];
    }
  }

  /** Returns a specific statistic of this object. */
  public String getStat(String name) {
    if (name.equals(BOUNDS)) {
      return "(" + x1 + ", " + y1 + "), (" + x2 + ", " + y2 + ")";
    }
    else if (name.equals(NODES)) {
      return "" + numNodes;
    }
    else if (name.equals(LENGTH)) {
      return "" + (float) curveLength;
    }
    else return "No such statistic for this overlay type";
  }

  /** Retrieves useful statistics about this overlay. */
  public String getStatistics() {
    return BOUNDS + " = (" + x1 + ", " + y1 + "), (" + x2 + ", " + y2 + ")\n" +
      NODES +" = " + numNodes + "\n" +
      LENGTH + " = " + (float) curveLength + "\n";
  }

  /** True iff this overlay has an endpoint coordinate pair. */
  public boolean hasEndpoint() { return true; }

  /** True iff this overlay has a second endpoint coordinate pair. */
  public boolean hasEndpoint2() { return true; }

  /** True iff this overlay supports the filled parameter. */
  public boolean canBeFilled() { return true; }

  /** True iff this overlay can be resized using X1, X2, Y1, Y2 entry boxes. */
  public boolean areBoundsEditable() { return false; }
  // currently, only non-noded objects can be resized this way.
  // (Actually could perform scaling on all nodes)

  // -- OverlayNodedObject API methods --

  /** True iff there is a highlighted node. */
  public boolean isHighlightNode() { return highlightNode; }

  /** Returns index of highlighted node. */
  public int getHighlightedNodeIndex() { return highlightIndex; }

  /** Gets the nearest node to the given point. */
  protected double[] getNearestNode(float x, float y) {
    int minIndex = -1;
    double minDist = Double.POSITIVE_INFINITY;
    float[] p = new float[]{x, y};

    // check distance to all nodes
    synchronized (nodesSync) {
      for (int i=0; i<numNodes; i++) {
        float[] c = {nodes[0][i], nodes[1][i]};
        double dist = MathUtil.getDistance(c, p);
        if (dist < minDist) {
          minIndex = i;
          minDist = dist;
        }
      }
    }

    return new double[]{minDist, (double) minIndex};
  }
  /** Computes the shortest distance from this object's bounding box to the
   * given point. */
  public double getDistanceToBoundingBox(double x, double y) {
    double xdist = 0;
    if (x < x1 && x < x2) xdist = Math.min(x1, x2) - x;
    else if (x > x1 && x > x2) xdist = x - Math.max(x1, x2);
    double ydist = 0;
    if (y < y1 && y < y2) ydist = Math.min(y1, y2) - y;
    else if (y > y1 && y > y2) ydist = y - Math.max(y1, y2);
    return Math.sqrt(xdist * xdist + ydist * ydist);
  }

  /** Highlight a node. */
  public void setHighlightNode(int i) {
    // if (Thread.currentThread().getName().indexOf("ComputeDataThread") < 0)
    // new Exception().printStackTrace();
    highlightNode = true;
    highlightIndex = i;
  }

  /** Turn off node highlighting. */
  public void turnOffHighlighting() {
    highlightNode = false;
    highlightIndex = -1;
  }

  /** Returns coordinates of node at given index in the node array. */
  public float[] getNodeCoords(int index) {
    float[] retvals = new float[2];
    synchronized(nodesSync) {
      if (index < numNodes && index >= 0) {
        retvals[0] = nodes[0][index];
        retvals[1] = nodes[1][index];
      }
      else {
        retvals[0] = -1f;
        retvals[1] = -1f;
      }
    }
    return retvals;
  }

  /** Returns a copy of the node array. */
  public float[][] getNodes() {
    synchronized(nodesSync) {
      float[][] copy = new float[2][numNodes];
      for (int i=0; i<2; i++) {
        System.arraycopy(nodes[i], 0, copy[i], 0, numNodes);
      }
      return copy;
    }
  }

  /** Returns the number of real nodes in the array. */
  public int getNumNodes() { synchronized(nodesSync) { return numNodes; } }

  /** Returns total number of nodes in array. */
  public int getMaxNodes() { synchronized(nodesSync) { return maxNodes; } }

  /** Gets most recent x-coordinate in node array. */
  public float getLastNodeX() {
    synchronized (nodesSync) { return nodes[0][numNodes-1]; }
  }

  /** Gets most recent y-coordinate in node array. */
  public float getLastNodeY() {
    synchronized(nodesSync) { return nodes[1][numNodes-1]; }
  }

  /** Changes coordinates of the overlay's first endpoint. */
  public void setCoords(float x1, float y1) {
    // different from super:
    float dx = x1-this.x1;
    float dy = y1-this.y1;
    synchronized (nodesSync) {
      for (int i=0; i<numNodes; i++) {
        nodes[0][i] = nodes[0][i]+dx;
        nodes[1][i] = nodes[1][i]+dy;
      }
    }

    // same as super
    this.x1 = x1;
    this.y1 = y1;
  }

  /** Sets the node array to that provided--for loading from saved. */
  public void setNodes(float[][] nodes) {
    synchronized (nodesSync) {
      this.nodes = nodes;
      numNodes = nodes[0].length;
      maxNodes = numNodes;
    }
    computeLength();
    updateBoundingBox();
  }

  /**
   * Updates the coordinates of the bounding box of a noded object by
   * checking the entire node array.
   */
  public void updateBoundingBox() {
    if (numNodes == 0) return;
    float xmin, xmax, ymin, ymax;
    synchronized (nodesSync) {
      xmin = xmax = nodes[0][0];
      ymin = ymax = nodes[1][0];
      for (int i=1; i < numNodes; i++) {
        if (nodes[0][i] < xmin) xmin = nodes[0][i];
        if (nodes[0][i] > xmax) xmax = nodes[0][i];
        if (nodes[1][i] < ymin) ymin = nodes[1][i];
        if (nodes[1][i] > ymax) ymax = nodes[1][i];
      }
    }
    this.x1 = xmin;
    this.y1 = ymin;
    this.x2 = xmax;
    this.y2 = ymax;
  }

  /** Gets length of curve. */
  public double getCurveLength() { return curveLength; }

  /** Determines length of last line segment in this curve. */
  public double getLastSegmentLength() {
    float[][] lastSeg = {getNodeCoords(getNumNodes()-1),
      getNodeCoords(getNumNodes() - 2)};
    double[][] lastSegD = {{(double) lastSeg[0][0], (double) lastSeg[0][1]},
      {(double) lastSeg[1][0], (double) lastSeg[1][1]}};
    return MathUtil.getDistance(lastSegD[0], lastSegD[1]);
  }

  /** Sets length of curve. */
  public void setCurveLength(double len) { curveLength = len; }

  /** Computes length of curve. */
  public void computeLength() {
    boolean notFull = false;
    synchronized (nodesSync) {
      if (maxNodes != numNodes) notFull = true;
    }
    if (notFull) truncateNodeArray();

    double length = 0;
    synchronized (nodesSync) {
      for (int i=0; i<numNodes-1; i++) {
        double[] a = {(double) nodes[0][i], (double) nodes[1][i]};
        double[] b = {(double) nodes[0][i+1], (double) nodes[1][i+1]};
        length += MathUtil.getDistance(a, b);
      }
    }
    this.curveLength = length;
  }

  // -- OverlayNodedObject API Methods: node array mutators --
  // note: call updateBoundingBox() after a series of changes to the node array

  /** Sets coordinates of an existing node. */
  public void setNodeCoords(int ndx, float x, float y) {
    // Outline of this method:
    // make sure the node isn't the same as previous or next node
    // if yes,
      // delete new node
      // make sure previous and next aren't the same
      // if they are, delete one of them
    // if no,
      // change the coordinates
    boolean sameAsPrev = false;
    boolean sameAsNext = false;
    synchronized (nodesSync) {
      if (ndx > 0 && (x == nodes[0][ndx-1] && y == nodes[1][ndx-1]))
        sameAsPrev = true;
      if (ndx < numNodes-1 && (x == nodes[0][ndx+1] && y == nodes[1][ndx+1]))
        sameAsNext = true;
    }

    if (sameAsPrev || sameAsNext) {
      if (sameAsPrev && sameAsNext) {
        deleteNode(ndx+1);
        deleteNode(ndx);
      }
      else {
        deleteNode(ndx);
      }
    }
    else {
      synchronized (nodesSync) {
        nodes[0][ndx] = x;
        nodes[1][ndx] = y;
        if (ndx == numNodes-1 && numNodes < maxNodes) {
          Arrays.fill(nodes[0], numNodes, maxNodes, x);
          Arrays.fill(nodes[1], numNodes, maxNodes, y);
        }
      }
    }
  }

  /** Sets coordinates of last node. */
  public void setLastNode(float x, float y) {
    setNodeCoords(numNodes-1, x, y);
  }

  /** Sets coordinates of last node. */
  public void setLastNode(float[] c) {
    setLastNode(c[0], c[1]);
  }

  /** Sets next node coordinates. */
  public void setNextNode(float x, float y) {
    synchronized (nodesSync) {
      if (numNodes > 0 && (x == nodes[0][numNodes-1] && y ==
            nodes[1][numNodes-1]))
      {
        // same as last node, do nothing
      }
      else {
        if (numNodes >= maxNodes) {
          maxNodes *= 2;
          resizeNodeArray(maxNodes);
        }
        Arrays.fill(nodes[0], numNodes, maxNodes, x);
        Arrays.fill(nodes[1], numNodes++, maxNodes, y);
        // i.e., set all remaining nodes (as per maxNodes) to next node coords
      }
    }
  }

  /** Sets next node coordinates. */
  public void setNextNode(float[] c) {
    setNextNode(c[0], c[1]);
  }

  /**
   * Inserts a node at coordinates provided
   * before the node at the index provided.
   */
  public void insertNode(int index, float x, float y, boolean colocationalOK) {
    synchronized (nodesSync) {
      if (index >= 0 && index < numNodes) {
        // if array is full, make some more room.
        if (numNodes >= maxNodes) {
          // numNodes should never exceed maxNodes but I threw the > in anyway.
          maxNodes *= 2;
          resizeNodeArray(maxNodes);
        }

        if (colocationalOK) {
          insert(index, x, y);
        }
        else {
          boolean differentFromNext = false;
          boolean differentFromPrev = false;
          boolean notFirst = false;
          float[] c = new float[]{x, y};
          if (index < numNodes) {
            differentFromNext = MathUtil.areDifferent(getNodeCoords(index), c);
          }
          if (index > 0) {
            differentFromPrev = MathUtil.areDifferent(
                getNodeCoords(index-1), c);
            notFirst = true;
          }

          /*
           cases where you insert the node
           -index = 0 and different from next
           -index interior and different from next and different from previous
          */
          if ((!notFirst && differentFromNext) ||
              (notFirst && differentFromPrev && differentFromNext))
          {
            insert(index, x, y);

          }
        }
      }
      else {
        //System.out.println("index not in range (0, numNodes). " +
        //  "No node inserted");
      }
    }
  }

  /** Actually inserts a node. */
  private void insert(int index, float x, float y) {
    for (int j = 0; j < 2; j++) {
      for (int i = numNodes; i > index; i--) {
        // right shift every node right of index by 1
        nodes[j][i] = nodes[j][i-1];
      }
    }
    nodes[0][index] = x;
    nodes[1][index] = y;
    numNodes++;
  }

  /** Deletes a range of nodes from the node array. */
  public void deleteBetween(int i1, int i2) {
    // assumes i1 < i2, both in bounds (less than numNodes)
    // checks whether i1 + 1 < i2, i.e., is there a non-zero
    // number of nodes to delete
    synchronized(nodesSync) {
      if (0 <= i1 && i2 < numNodes && i1 + 1 < i2) {
        int ii2 = i2;
        // if adjacent-nodes-to-be are colococational,
        // delete one of them
        if (MathUtil.areSame(getNodeCoords(i1), getNodeCoords(i2)))
          ii2 += 1;
        int victims = ii2 - i1 - 1;
        float[][] newNodes = new float[2][maxNodes - victims];
        System.arraycopy(nodes[0], 0, newNodes[0], 0, i1 + 1);
        System.arraycopy(nodes[1], 0, newNodes[1], 0, i1 + 1);
        System.arraycopy(nodes[0], ii2, newNodes[0], i1+1, maxNodes - ii2);
        System.arraycopy(nodes[1], ii2, newNodes[1], i1+1, maxNodes - ii2);
        numNodes -= victims;
        maxNodes -= victims;
        nodes = newNodes;
      }
      else {
        //System.out.println("deleteBetween(int, int) out of bounds error");
      }
    }
  }

  /** Deletes a node from the node array. */
  public void deleteNode(int index) {
    synchronized (nodesSync) {
      if (index >=0 && index < numNodes) {
        // This method includes built-in truncation: it doesn't bother to
        // copy the extra nodes in the node array.

        // Check if this delete operation will result in two colocational nodes
        // becoming adjacent in the node array.  If so, also delete one of
        // these nodes.
        int offset;
        if (index > 0 && index < numNodes - 1 &&
            MathUtil.areSame(getNodeCoords(index-1), getNodeCoords(index+1)))
          offset = 1;
        else offset = 0;
        float[][] newNodes =  new float[2][numNodes-1-offset];
        System.arraycopy(nodes[0], 0, newNodes[0], 0, index-offset);
        System.arraycopy(nodes[0], index+1, newNodes[0], index-offset,
            numNodes-index-1);
        System.arraycopy(nodes[1], 0, newNodes[1], 0, index-offset);
        System.arraycopy(nodes[1], index+1, newNodes[1], index-offset,
            numNodes-index-1);
        numNodes -= 1 + offset;
        maxNodes = numNodes;
        nodes = newNodes;
      }
    }
  }

  // NOTE: Right now this method returns Freeforms only, though it could be used
  // on Polylines too.
  /** Deletes a node from the freeform object, creating two new freeforms
   *  if the node deleted is an interior node.
   *  Returns resulting new freeforms if any. */
  public OverlayFreeform[] removeNode(int index) {
    OverlayFreeform[] children = {null, null};
    if (index == 0 || index == numNodes - 1) {
      deleteNode(index);
    }
    else {
      children = slice(index, 0.0);
    }
    return children;
  }

  /**
   * Creates a new freeform by connecting the tail of this freeform to the
   * head of the freeform supplied.
   * @param f2 The freeform to connect to this freeform
   */
  public OverlayFreeform connectTo(OverlayFreeform f2) {
    float[][] f1Nodes = this.getNodes();
    float[][] f2Nodes = f2.getNodes();
    int len1 = f1Nodes[0].length;
    int len2 = f2Nodes[0].length;

    // Obtain coordinates of last node of f1 and first of f2.
    float[] f1End = {f1Nodes[0][len1-1], f1Nodes[1][len1-1]};
    float[] f2Beg = {f2Nodes[0][0], f2Nodes[1][0]};
    // If the last node in f1 and the first node in f2 are the same,
    // this method won't copy the last node of f1 into the node array used to
    // construct f3.
    int offset;
    if (MathUtil.areSame(f1End, f2Beg)) offset = 1;
    else offset = 0;

    float[][] newNodes = new float[2][len1 + len2 - offset];
    for (int i=0; i<2; i++) {
      System.arraycopy(f1Nodes[i], 0, newNodes[i], 0, len1-offset);
      System.arraycopy(f2Nodes[i], 0, newNodes[i], len1-offset, len2);
    }

    OverlayFreeform f3 = new OverlayFreeform(overlay, newNodes);
    return f3;
  }

  /** Reverses the node array (and therefore its internal orientation). */
  public void reverseNodes() {
    truncateNodeArray();
    // Q: Is the above call ever necessary?
    // truncateNodeArray() is called at mouseUp in FreeformTool.
    // Will there ever be an extension
    // of the node array s.t. maxNodes > numNodes during an interior edit before
    // an extension
    // A: Yes, if extend mode is entered directly from edit mode
    synchronized (nodesSync) {
      float[][] temp = new float[2][maxNodes];
      for (int j = 0; j < 2; j++) {
        for (int i = 0; i < maxNodes; i++) {
          temp[j][maxNodes-i-1] = nodes[j][i];
        }
      }
      nodes = temp;
    }
  }

  /** Deletes buffer nodes from the tail of the node array. */
  public void truncateNodeArray() {
    resizeNodeArray(numNodes);
  }

  /** Resizes the node array, truncating if necessary. */
  // TODO is it possible to use System.arraycopy here?
  protected void resizeNodeArray(int newLength) {
    synchronized (nodesSync) {
      int loopMax = Math.min(nodes[0].length, newLength);
      float[][] a2 = new float[2][newLength];
      for (int j=0; j<2; j++) { //manually copy nodes to a2
        for (int i=0; i<loopMax; i++) {
          a2[j][i] = nodes[j][i];
        }
        // case where newLength > a.length:
        // fills rest of new array with nodes co-locational with last node
        for (int i=loopMax; i < a2[0].length; i++) {
          a2[j][i] = nodes[j][loopMax-1];
        }
      }
      nodes = a2;
      maxNodes = newLength;
    }
  }

  // -- Helper methods --

  // NOTE: Right now this method returns Freeforms only, though it could be used
  // on Polylines too.
  /**
   * Slices a noded object in two.
   * @param seg the index of the segment on which to slice
   * @param weight the relative distance along the segment to slice
   * The parameters seg and weight indicate a 'cut point' on the freeform.
   * If weight is strictly between 0 and 1, the cut point is actually between
   * two nodes, and all nodes of the original object are transferred to
   * the child objects.
   */
  // TODO -- combine this with 'split' method in polyline tool.
  private OverlayFreeform[] slice(int seg, double weight) {
    // create two new freeforms from the remainder of this freeform
    OverlayFreeform f1 = null, f2 = null;

    float[][] f1Nodes = null, f2Nodes = null;

    synchronized (nodesSync) {
      // compute indices into the node array of this freeform
      int f1Start, f2Start, f1Stop, f2Stop;
      f1Start = 0;
      f1Stop = seg;
      f2Start = seg + 1;
      f2Stop = numNodes - 1;

      // if the cut point is a node itself, exclude that node from both halves
      if (weight == 0.0) f1Stop = seg - 1;
      else if (weight == 1.0) f2Start = seg + 2;

      int numNodes1 = f1Stop + 1;
      int numNodes2 = f2Stop - f2Start + 1;

      // create new object if number of nodes in object > 1
      if (numNodes1 > 1) {
        f1Nodes = new float[2][numNodes1];

        for (int i=0; i<2; i++) {
          System.arraycopy(nodes[i], 0, f1Nodes[i], 0, numNodes1);
        }
      }

      // create new object if number of nodes in object > 1
      if (numNodes2 > 1) {
        f2Nodes = new float[2][numNodes2];

        for (int i = 0; i<2; i++) {
          System.arraycopy(nodes[i], f2Start, f2Nodes[i], 0, numNodes2);
        }
      }
    } // end synchronized

    if (f1Nodes != null) {
      f1 = new OverlayFreeform(overlay, f1Nodes);
      overlay.addObject(f1);
      f1.setSelected(false);
      f1.setDrawing(false);
    }
    if (f2Nodes != null) {
      f2 = new OverlayFreeform(overlay, f2Nodes);
      overlay.addObject(f2);
      f2.setSelected(false);
      f2.setDrawing(false);
    }

    // dispose of original freeform
    overlay.removeObject(this);

    return new OverlayFreeform[]{f1, f2};
  }

  // -- Helper Methods for Debugging --

  /** Prints node array of current freeform; for debugging */
  private void printNodes(float[][] n) {
    System.out.println("Printing n...");
    synchronized (nodesSync) {
      for (int i = 0; i < n[0].length; i++){
        System.out.println(i+":("+n[0][i]+","+n[1][i]+")");
      }
    }
  }

  /** Prints node array of current freeform.  For debugging. */
  public void printNodes() {
    printNodes(nodes);
  }

  /** Prints current thread plus method name if provided. */
  public static void printThread(String methodName) {
    System.out.println(methodName + ": currentThread()= " +
        Thread.currentThread());
  }
}
