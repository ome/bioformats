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
import visad.util.CursorUtil;

/**
 * OverlayNodedObject is an overlay object built of nodes likely
 * connected by lines.
 */

public abstract class OverlayNodedObject extends OverlayObject {

  // -- Static Fields -- 
  
  /** The names of the statistics this object reports */
  protected static String[] statTypes =  {"Bounds", "Nodes",
    "Length"};

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

  /** Alpha value for highlighting */
  protected static final float HLT_ALPHA = 0.5f;

  /** Radius in pixels of circle indicating a node is selected */
  protected static final float RADIUS = 3.0f;
  
  // -- Fields --

  /** Node array and associated tracking variables */
  protected float[][] nodes;

  /** Number of real nodes in the node array */
  protected int numNodes;

  /** Total number of nodes (real + buffer) nodes in the node array */
  protected int maxNodes;

  /** Length of curve of a noded object */
  protected double curveLength;

  /** Whether there is a higlighted node */
  protected boolean highlightNode;

  /** Index of the highlighted node */
  protected int highlightIndex;

  /** Color to highlight highlighted node */
  protected Color highlightColor;

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
    maxNodes = 100;
    nodes = new float[2][maxNodes];
    Arrays.fill(nodes[0], x1);
    Arrays.fill(nodes[1], y1);
    numNodes = 1;
    computeLength();
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
    computeLength();
    turnOffHighlighting();
  }

  // -- Static methods --

  /** Returns the names of the statistics this object reports */
  public static String[] getStatTypes() { return statTypes; }

  // -- OverlayObject API methods --

  /** Returns whether this object is drawable, i.e., is of nonzero 
  *  size, area, length, etc. 
  */
  public boolean hasData() { 
    if (isDrawing()) return (numNodes > 0);
    else return (numNodes > 1);
    // NOTE: Not exactly consistent with the other overlay objects.
    // You want to see 1-node objects while drawing, but 
    // want to delete them if after drawing is done.
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
      fieldSet = new Gridded2DSet(domain,
        nodes, maxNodes, null, null, null, false);
      
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
      catch (VisADException exc2) {exc2.printStackTrace();}
    }
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

  /** Returns a specific statistic of this object */
  public String getStat(String name) {
    if (name.equals("Bounds")) {
      return "(" + x1 + ", " + y1 + "), (" + x2 + ", " + y2 + ")";
    } 
    else if (name.equals("Nodes")) {
      return "" + numNodes;
    }
    else if (name.equals("Length")) {
      return "" + (float) curveLength;
    }
    else return "No such statistic for this overlay type";
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

  // -- Object API methods --

  /** Highlight a node. */ 
  public void setHighlightNode(int i, Color c) {
    // if (Thread.currentThread().getName().indexOf("ComputeDataThread") < 0)
    // new Exception().printStackTrace();
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
  public float[][] getNodes() { 
    float[][] copy = new float[2][numNodes];
    for (int i=0; i<2; i++)
      System.arraycopy(nodes[i], 0, copy[i], 0, numNodes); 
    return copy;
  }

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
  }

  /** Sets the node array to that provided--for loading from saved */
  public void setNodes(float[][] nodes) {
    this.nodes = nodes;
    numNodes = nodes[0].length;
    maxNodes = numNodes;
    computeLength();
    updateBoundingBox();
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
    double[] oDom = CursorUtil.pixelToDomain(d, 0, 0);
    double[] pDom = CursorUtil.pixelToDomain(d, 1, 0);
    double scl = MathUtil.getDistance(oDom, pDom); 
    return (float) scl;
  }
}
