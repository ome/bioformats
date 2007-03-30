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
import loci.visbio.util.DisplayUtil;
import visad.*;

/**
 * OverlayNodedObject is an overlay object built of nodes likely
 * connected by lines.
 */

public abstract class OverlayNodedObject extends OverlayObject {

  // -- Constants --
  
  /** Computed (X, Y) pairs for top 1/2 of a unit circle. */
  protected static final float[][] ARC = arc();

  /** Computes the top 1/2 of a unit circle. */
  private static float[][] arc() {
    int res = 16; // resolution for 1/8 of circle
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

  /** Alpha value for highlighting */
  protected static final float HLT_ALPHA = 0.5f;
  
  // -- Fields --

  /** Node array and associated tracking variables */
  protected float[][] nodes;

  /** Number of real nodes in the node array */
  protected int numNodes;

  /** Total number of nodes (real + buffer) nodes in the node array */
  protected int maxNodes;

  /** Length of curve of a noded object */
  protected double curveLength;

  /** Whether the head node should be highlighted */
  protected boolean highlightHead;

  /** Whether the tail node should be highlighted */
  protected boolean highlightTail;

  /** Whether there is a higlighted node */
  protected boolean highlightNode;

  /** Index of the highlighted node */
  protected int highlightIndex;

  /** Color to highlight highlighted node */
  protected Color highlightColor;

  /** The active display, used by the getData method */
  protected DisplayImpl display;

  // -- Constructors --

  /** Constructs an uninitialized noded object. */
  public OverlayNodedObject(OverlayTransform overlay) { 
    super(overlay); 
    setHighlightHead(false);
    setHighlightTail(false);
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
    maxNodes = 100;
    nodes = new float[2][maxNodes];
    Arrays.fill(nodes[0], x1);
    Arrays.fill(nodes[1], y1);
    numNodes = 1;
    computeGridParameters();
    computeLength();
    setHighlightHead(false);
    setHighlightTail(false);
    turnOffHighlighting();
  }

  /** Constructs a noded object from an array of nodes. */
  public OverlayNodedObject(OverlayTransform overlay, float[][] nodes) {
    super(overlay);
    x1=x2=y1=y2=0f;
    this.nodes = nodes;
    numNodes = nodes[0].length;
    maxNodes = nodes[0].length;
    updateBoundingBox();
    computeGridParameters();
    computeLength();
    setHighlightHead(false);
    setHighlightTail(false);
    turnOffHighlighting();
  }

  // -- OverlayObject API methods --
  
  /** Gets VisAD data object representing this overlay. */
  public DataImpl getData() {
    RealTupleType domain = overlay.getDomainType();
    TupleType range = overlay.getRangeType();

    FlatField field = null;
    Set fieldSet = null;
      
    // determine number of samples
    boolean circleFilled = false; // only works if nodes are also filled
    // (all sets in a union set must have same manifold dimension)
    int arcLen = ARC[0].length;
    int len = 2 * arcLen;
    int hlen = circleFilled ? len : len + 1;
    int totalSamples = maxNodes;
    if (isHighlightNode()) totalSamples += hlen;

    float[][] rangeSamples = new float[4][totalSamples];

    // ******************************************************************
    // build nodes set and assign nodes range samples
    // ******************************************************************
    SampledSet nodesSet = null;
    try {
      nodesSet = new Gridded2DSet(domain,
        nodes, maxNodes, null, null, null, false);
      
      // I've written !isDrawing() to prevent a manifold dimension mismatch
      // that occurs when drawing filled polylines
      if (filled && !isDrawing()) {  
        Irregular2DSet roiSet =
          DelaunayCustom.fillCheck((Gridded2DSet) nodesSet, false);
        if (roiSet != null)  nodesSet = roiSet;
      }

      fieldSet = nodesSet;
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    
    // fill nodes range samples
    float r = color.getRed() / 255f;
    float g = color.getGreen() / 255f;
    float b = color.getBlue() / 255f;

    if (selected) {
      r = 1.0f;
      g = 1.0f;
      b = 0.0f;
    }

    Arrays.fill(rangeSamples[0], 0, maxNodes, r);
    Arrays.fill(rangeSamples[1], 0, maxNodes, g);
    Arrays.fill(rangeSamples[2], 0, maxNodes, b);
    Arrays.fill(rangeSamples[3], 0, maxNodes, 1.0f);
    
    // **************************************************************
    // make highlight set and fill highlight range samples
    // **************************************************************
    if (isHighlightNode()) {
      SampledSet highlightSet = null;

      // scale cirlce radius
      float scale;
      if (display != null) scale = getScalingValue(display);
      else scale = 1f;
      float rad = 10.0f * scale; // 10.0 pixels wide per active display

      // assemble highlight set samples
      float[][] highlightSetSamples = new float[2][hlen];
      float[] c = getNodeCoords(getHighlightedNodeIndex());

      // top half of circle
      for (int i=0; i<arcLen; i++) {
        highlightSetSamples[0][i] = c[0] + rad * ARC[0][i];
        highlightSetSamples[1][i] = c[1] + rad * ARC[1][i];
      }

      // bottom half of circle
      for (int i=0; i<arcLen; i++) {
        int ndx = circleFilled? arcLen + i : len - i - 1;
        highlightSetSamples[0][ndx] = c[0] + rad * ARC[0][i];
        highlightSetSamples[1][ndx] = c[1] - rad * ARC[1][i];
      }

      try {
        // build highlight set 
        if (circleFilled) {
          highlightSet = new Gridded2DSet(domain, highlightSetSamples,
            arcLen, 2, null, null, null, false);
        }
        else {
          highlightSetSamples[0][len] = highlightSetSamples[0][0];
          highlightSetSamples[1][len] = highlightSetSamples[1][0];
          highlightSet = new Gridded2DSet(domain, highlightSetSamples,
              highlightSetSamples[0].length, null, null, null, false);
        }
      }
      catch (VisADException exc) { exc.printStackTrace(); }
      
      // fill highlight range samples
      float hltR = highlightColor.getRed() / 255f;
      float hltG = highlightColor.getGreen() / 255f;
      float hltB = highlightColor.getBlue() / 255f;
      float hltA = HLT_ALPHA;

      Arrays.fill(rangeSamples[0], maxNodes, totalSamples, hltR);
      Arrays.fill(rangeSamples[1], maxNodes, totalSamples, hltG);
      Arrays.fill(rangeSamples[2], maxNodes, totalSamples, hltB);
      Arrays.fill(rangeSamples[3], maxNodes, totalSamples, hltA);

      try {
        // assemble a UnionSet of nodes and circle
        SampledSet[] sets = new SampledSet[]{nodesSet, highlightSet};  
        fieldSet = new UnionSet(domain, sets);
      }
      catch (VisADException exc) { exc.printStackTrace(); }
    }
    
    // **********************************************************
    // assemble ultimate field
    // **********************************************************
    try {
      FunctionType fieldType = new FunctionType(domain, range);
      field = new FlatField(fieldType, fieldSet);
      field.setSamples(rangeSamples);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }

    return field; 
  }

  /**
   * Computes a grid to be superimposed on this overlay
   * to indicate it is currently selected.
   */
  public DataImpl getSelectionGrid() { return getSelectionGrid(false); }

  /**
   * Computes a grid to be superimposed on this overlay to
   * indicate it is currently selected.
   * Computes only an outline regardless of the flag 'outline' 
   */
  public DataImpl getSelectionGrid(boolean outline) {
    //return super.getSelectionGrid(true);

    RealTupleType domain = overlay.getDomainType();
    TupleType range = overlay.getRangeType();

    float scl = display == null ? 1.0f : getScalingValue(display);
    float delta = GLOW_WIDTH * scl;

    Gridded2DSet[] segments = new Gridded2DSet[numNodes-1];
    for (int i=0; i<numNodes - 1; i++) {
      float[] v = new float[]{nodes[0][i+1] - nodes[0][i], nodes[1][i+1] -
          nodes[1][i]};
      // angle of vector perpendicular to line
      double theta =  Math.PI / 2 + Math.atan2(v[1], v[0]); 

      float dx = (float) (delta * Math.cos(theta));
      float dy = (float) (delta * Math.sin(theta));

      float[] p1 = {nodes[0][i] + dx, nodes[1][i] + dy};
      float[] p2 = {nodes[0][i+1] + dx, nodes[1][i+1] + dy};
      float[] p3 = {nodes[0][i] - dx, nodes[1][i] - dy};
      float[] p4 = {nodes[0][i+1] - dx, nodes[1][i+1] - dy};

      float[][] setSamples = {{p1[0], p2[0], p3[0], p4[0]},
                              {p1[1], p2[1], p3[1], p4[1]}};

      try {
        segments[i] = new Gridded2DSet(domain, setSamples,
            2, 2, null, null, null, false);
      }
      catch (VisADException exc) { exc.printStackTrace(); }
    }

    // construct range samples;
    Color col = Color.YELLOW;

    float r = col.getRed() / 255f;
    float g = col.getGreen() / 255f;
    float b = col.getBlue() / 255f;


    float[][] rangeSamples = new float[4][4*(numNodes-1)];
    Arrays.fill(rangeSamples[0], r);
    Arrays.fill(rangeSamples[1], g);
    Arrays.fill(rangeSamples[2], b);
    Arrays.fill(rangeSamples[3], GLOW_ALPHA);

    FlatField field = null;
    UnionSet fieldSet = null;
    try {
      fieldSet = new UnionSet (domain, segments);

      FunctionType fieldType = new FunctionType(domain, range);
      field = new FlatField(fieldType, fieldSet);
      field.setSamples(rangeSamples);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }

    return field;

  }

  /**
   * Computes the shortest distance from this
   * object's bounding box to the given point.
   */
  public double getDistanceToBoundingBox(double x, double y) {
    double xdist = 0;
    if (x < x1 && x < x2) xdist = Math.min(x1, x2) - x;
    else if (x > x1 && x > x2) xdist = x - Math.max(x1, x2);
    double ydist = 0;
    if (y < y1 && y < y2) ydist = Math.min(y1, y2) - y;
    else if (y > y1 && y > y2) ydist = y - Math.max(y1, y2);
    return Math.sqrt(xdist * xdist + ydist * ydist);
  }

  /** Compute the shortest distance from this object to the given point */
  public double getDistance (double x, double y) {
     double[] distSegWt = MathUtil.getDistSegWt(nodes, (float) x, (float) y);
     return distSegWt[0];
  }

  /** Retrieves useful statistics about this overlay. */
  public String getStatistics() {
    return "Bounds = (" + x1 + ", " + y1 + "), (" + x2 + ", " + y2 + ")\n" +
      "Number of Nodes = " + numNodes + "\n" +
      "Curve Length = " + (float) curveLength + "\n";
  }

  /** True iff this overlay has an endpoint coordinate pair. */
  public boolean hasEndpoint() { return true; }

  /** True iff this overlay has a second endpoint coordinate pair. */
  public boolean hasEndpoint2() { return true; }

  /** True iff the head node is highlighted. */
  public boolean isHighlightHead() { return highlightHead; }
  // TODO get rid of this ACS

  /** True iff the tail node is highlighted. */
  public boolean isHighlightTail() { return highlightTail; }
  // TODO get rid of this ACS

  /** True iff there is a highlighted node. */
  public boolean isHighlightNode() { return highlightNode; }

  /** Returns index of highlighted node. */ 
  public int getHighlightedNodeIndex() { return highlightIndex; }

  /** True iff this overlay supports the filled parameter. */
  public boolean canBeFilled() { return true; }

  /** True iff this overlay can be resized using X1, X2, Y1, Y2 entry boxes */
  public boolean areBoundsEditable() { return false; }
  // currently, only non-noded objects can be resized this way.
  // (Actually could perform some rad scaling on all nodes)

  // -- Internal OverlayObject API methods --

  /** Computes parameters needed for selection grid computation. */
  protected void computeGridParameters() {
    float padding = 0.02f * overlay.getScalingValue();
    boolean flipX = x2 < x1;
    float xx1 = flipX ? (x1 + padding) : (x1 - padding);
    float xx2 = flipX ? (x2 - padding) : (x2 + padding);
    boolean flipY = y2 < y1;
    float yy1 = flipY ? (y1 + padding) : (y1 - padding);
    float yy2 = flipY ? (y2 - padding) : (y2 + padding);

    xGrid1 = xx1; yGrid1 = yy1;
    xGrid2 = xx2; yGrid2 = yy1;
    xGrid3 = xx1; yGrid3 = yy2;
    xGrid4 = xx2; yGrid4 = yy2;
    horizGridCount = 3; vertGridCount = 3;
  }

  // -- Object API methods --

  /** Makes this object aware of active display. 
   *  The display is required to convert back and forth from pixel
   *  to domain coordinates
   */
  public void setActiveDisplay (DisplayImpl d) { display = d; }
  
  /** Toggle highlight head of polyline. */
  public void setHighlightHead(boolean b) { highlightHead = b; }

  /** Toggle highlight tail of polyline. */
  public void setHighlightTail(boolean b) { highlightTail = b; }

  /** Highlight a node. */ 
  public void setHighlightNode(int i, Color c) {
    highlightNode = true;
    highlightIndex = i;
    highlightColor = c;
  }

  /** Turn off node highlighting */
  public void turnOffHighlighting() { 
    highlightNode = false;
    highlightIndex = -1; 
    highlightColor = null;
  }

  /** Returns coordinates of node at given index in the node array */
  public float[] getNodeCoords (int index) {
    float[] retvals = new float[2];
    if (index < numNodes && index >= 0) {
      retvals[0] = nodes[0][index];
      retvals[1] = nodes[1][index];
    } else {
      retvals[0] = -1f;
      retvals[1] = -1f;
    }
    return retvals;
  }

  /** Returns the node array */
  public float[][] getNodes() { return nodes; }

  /** Returns the number of real nodes in the array */
  public int getNumNodes() { return numNodes; }

  /** Returns total number of nodes in array */
  public int getMaxNodes() { return maxNodes; }

  /** Gets most recent x-coordinate in node array. */
  public float getLastNodeX() {
    return nodes[0][numNodes-1];
  }

  /** Gets most recent y-coordinate in node array. */
  public float getLastNodeY() {
    return nodes[1][numNodes-1];
  }
  
  /** Changes coordinates of the overlay's first endpoint. */
  public void setCoords(float x1, float y1) {
    // different from super:
    float dx = x1-this.x1;
    float dy = y1-this.y1;
    for (int i=0; i<numNodes; i++) {
      nodes[0][i] = nodes[0][i]+dx;
      nodes[1][i] = nodes[1][i]+dy;
    }

    // same as super
    this.x1 = x1;
    this.y1 = y1;

    computeGridParameters();
  }

  /** Sets the node array to that provided--for loading from saved */
  public void setNodes(float[][] nodes) {
    this.nodes = nodes;
    numNodes = nodes[0].length;
    maxNodes = numNodes;
    computeLength();
    updateBoundingBox();
    computeGridParameters();
  }

  /** Updates the coordinates of the bounding box of a noded object */
  public void updateBoundingBox() {
    if (numNodes == 0) return;
    float xmin, xmax, ymin, ymax;
    xmin = xmax = nodes[0][0];
    ymin = ymax = nodes[1][0];
    for (int i=1; i < numNodes; i++) {
      if (nodes[0][i] < xmin) xmin = nodes[0][i];
      if (nodes[0][i] > xmax) xmax = nodes[0][i];
      if (nodes[1][i] < ymin) ymin = nodes[1][i];
      if (nodes[1][i] > ymax) ymax = nodes[1][i];
    }
    this.x1 = xmin;
    this.y1 = ymin;
    this.x2 = xmax;
    this.y2 = ymax;
  }

  /** Gets length of curve */
  public double getCurveLength() { return curveLength; }

  /** Sets length of curve */
  public void setCurveLength(double len) { curveLength = len; }

  /** Computes length of curve */
  public void computeLength() {
    if (maxNodes != numNodes) truncateNodeArray();
    double length = 0;
    for (int i=0; i<numNodes-1; i++) {
      double[] a = {(double) nodes[0][i], (double)nodes[1][i]};
      double[] b = {(double) nodes[0][i+1], (double) nodes[1][i+1]};
      length += MathUtil.getDistance(a, b);
    }
    this.curveLength = length;
  }

  // -- OverlayNodedObject API Methods: node array mutators --
  // note: call updateBoundingBox() after a series of changes to the node array

  /** Sets coordinates of an existing node */
  public void setNodeCoords(int nodeIndex, float newX, float newY) {
    if (nodeIndex >= 0 && nodeIndex < numNodes) {
      nodes[0][nodeIndex] = newX;
      nodes[1][nodeIndex] = newY;
    }
    else {
      //TEMP:
      //System.out.println("Out of bounds error. Can't reset node coordinates");
    }
  }

  /** Sets coordinates of last node. */
  public void setLastNode(float x, float y) {
    setNodeCoords(numNodes-1, x, y);
    if (numNodes < maxNodes) {
      Arrays.fill(nodes[0], numNodes, maxNodes, x);
      Arrays.fill(nodes[1], numNodes, maxNodes, y);
    }
  }
    
  /** Prints node array of current freeform; for debugging */
  private void printNodes(float[][] nodes) {
    System.out.println("Printing nodes...");
    for (int i = 0; i < nodes[0].length; i++){
      System.out.println(i+":("+nodes[0][i]+","+nodes[1][i]+")");
    }
  }

  /** Sets next node coordinates. */
  public void setNextNode(float x, float y) {
    if (numNodes >= maxNodes) {
      maxNodes *= 2;
      nodes = resizeNodeArray(nodes, maxNodes);
    }
    Arrays.fill(nodes[0], numNodes, maxNodes, x);
    Arrays.fill(nodes[1], numNodes++, maxNodes, y);
    // i.e., set all remaining nodes (as per maxNodes) to next node coords
  }

  /**
   * Inserts a node at coordinates provided
   * before the node at the index provided.
   */
  public void insertNode(int index, float x, float y) {
    if (index >= 0 && index < numNodes) {
      // if array is full, make some more room.
      if (numNodes >= maxNodes) { // numNodes should never exceed maxNodes but..
        maxNodes *= 2;
        nodes = resizeNodeArray(nodes, maxNodes);
      }
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
    else {
      //System.out.println("index not in range (0, numNodes). " +
      //  "No node inserted");
    }
  }

  /** Deletes a range of nodes from the node array */
  public void deleteBetween (int i1, int i2) {
    // assumes i1 < i2, both in bounds (less than numNodes)
    // checks whether i1 + 1 < i2, i.e., is there a non-zero
    // number of nodes to delete
    if (0 <= i1 && i2 < numNodes && i1 + 1 < i2 ) {
      int victims = i2 - i1 - 1;
      float[][] newNodes = new float[2][maxNodes - victims];
      System.arraycopy(nodes[0], 0, newNodes[0], 0, i1 + 1);
      System.arraycopy(nodes[1], 0, newNodes[1], 0, i1 + 1);
      System.arraycopy(nodes[0], i2, newNodes[0], i1+1, maxNodes - i2);
      System.arraycopy(nodes[1], i2, newNodes[1], i1+1, maxNodes - i2);
      numNodes -= victims;
      maxNodes -= victims;
      nodes = newNodes;
      if (numNodes == 0) overlay.removeObject(this);
      if (numNodes <= 1) overlay.removeObject(this);
    } else {
      //System.out.println("deleteBetween(int, int) out of bounds error");
    }
  }

  /** Deletes a node from the node array */
  public void deleteNode(int index) {
    if (index >=0 && index < numNodes) {
      // built-in truncation
      //System.out.println("OverlayObject.deleteNode(" + index +") called. " +
      //  "numNodes = " + numNodes + ", maxNodes = " + maxNodes);
      float [][] newNodes =  new float[2][numNodes-1];
      System.arraycopy(nodes[0], 0, newNodes[0], 0, index);
      System.arraycopy(nodes[0], index+1, newNodes[0], index, numNodes-index-1);
      System.arraycopy(nodes[1], 0, newNodes[1], 0, index);
      System.arraycopy(nodes[1], index+1, newNodes[1], index, numNodes-index-1);
      numNodes--;
      maxNodes = numNodes;
      nodes = newNodes;

      if (numNodes == 0) {
        //System.out.println("destroying " + this);
        overlay.removeObject(this);
      }
      if (numNodes <= 1) overlay.removeObject(this);
    }
  }

  /** Reverses the node array (and therefore its internal orientation) */
  public void reverseNodes() {
    truncateNodeArray();
    // Q: Is the above call ever necessary?
    // truncateNodeArray() is called at mouseUp in FreeformTool.
    // Will there ever be an extension
    // of the node array s.t. maxNodes > numNodes during an interior edit before
    // an extension
    // A: Yes, if extend mode is entered directly from edit mode
    float[][] temp = new float[2][maxNodes];
    for (int j = 0; j < 2; j++) {
      for (int i = 0; i < maxNodes; i++) {
        temp[j][maxNodes-i-1] = nodes[j][i];
      }
    }
    nodes = temp;
  }

  /** Deletes buffer nodes from the tail of the node array */
  public void truncateNodeArray() {
    nodes = resizeNodeArray(nodes, numNodes);
  }

  /** Resizes the node array, truncating if necessary. */
  protected float[][] resizeNodeArray(float[][] a, int newLength) {
    //System.out.println("resizing node array to "+ newLength); // TEMP
    int loopMax = Math.min(a[0].length, newLength);
    float[][] a2 = new float[2][newLength];
    for (int j=0; j<2; j++) { //manually copy a to a2
      for (int i=0; i<loopMax; i++) {
        a2[j][i] = a[j][i];
      }
      // case where newLength > a.length:
      // fills rest of new array with nodes co-locational with last node
      for (int i=loopMax; i < a2[0].length; i++) {
        a2[j][i] = a[j][loopMax-1];
      }
    }
    maxNodes = newLength;
    //System.out.println("resize completed. maxNodes = " +
    //  maxNodes + " numNodes =  " + numNodes);
    return a2;
  }

  /** Returns a scaling value [domain length units/pixel] */
  protected float getScalingValue(DisplayImpl d) {
    double[] oDom = DisplayUtil.pixelToDomain(d, 0, 0);
    double[] pDom = DisplayUtil.pixelToDomain(d, 1, 0);
    double scl = MathUtil.getDistance(oDom, pDom); 
    return (float) scl;
  }
}
