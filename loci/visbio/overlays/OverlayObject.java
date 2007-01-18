
// OverlayObject.java
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
import visad.*;
import loci.visbio.util.MathUtil;

/** OverlayObject is the superclass of all overlay objects. */
public abstract class OverlayObject {

  // -- Fields --

  /** Associated overlay transform. */
  protected OverlayTransform overlay;

  /** Endpoint coordinates. */
  protected float x1, y1, x2, y2;

  /** Node array (for freeforms with intermediate points). */
  protected float[][] nodes;
  protected int numNodes, maxNodes;
  protected boolean hasNodes;

  /** Text string to render. */
  protected String text;

  /** Color of this overlay. */
  protected Color color;

  /** Flag indicating overlay is solid. */
  protected boolean filled;

  /** Group to which this overlay belongs. */
  protected String group;

  /** Notes associated with this overlay. */
  protected String notes;

  /** Flag indicating this overlay is currently selected. */
  protected boolean selected = true;

  /** Flag indicating this overlay is still being initially drawn. */
  protected boolean drawing = true;

  /** Top-left endpoint of selection grid rectangle. */
  protected float xGrid1, yGrid1;

  /** Top-right endpoint of selection grid rectangle. */
  protected float xGrid2, yGrid2;

  /** Bottom-left endpoint of selection grid rectangle. */
  protected float xGrid3, yGrid3;

  /** Bottom-right endpoint of selection grid rectangle. */
  protected float xGrid4, yGrid4;

  /** Number of horizontal and vertical dividing lines for selection grid. */
  protected int horizGridCount, vertGridCount;

  /** Length of curve of a noded object */
  protected double curveLength;

  // -- Constructor --

  /** Constructs an overlay. */
  public OverlayObject(OverlayTransform overlay) {
    this.overlay = overlay;
    overlay.setTextDrawn(false);
  }

  // -- OverlayObject API methods --

  /** Gets VisAD data object representing this overlay. */
  public abstract DataImpl getData();

  /** Computes the shortest distance from this overlay to the given point. */
  public abstract double getDistance(double x, double y);
  //ACS TODO make sure this checks out in OverlayFreeform

  /** Retrieves useful statistics about this overlay. */
  public String getStatistics() {
    String name = getClass().getName();
    String pack = getClass().getPackage().getName();
    if (name.startsWith(pack)) name = name.substring(pack.length() + 1);
    return "No statistics for " + name;
  }

  /** True iff this overlay has an endpoint coordinate pair. */
  public boolean hasEndpoint() { return false; }

  /** True iff this overlay has a second endpoint coordinate pair. */
  public boolean hasEndpoint2() { return false; }

  /** True iff this overlay supports the filled parameter. */
  public boolean canBeFilled() { return false; }

  /** True iff this overlay can be resized using X1, X2, Y1, Y2 entry boxes */
  public boolean areBoundsEditable() { return !hasNodes; }
  // currently, only noded objects can't be resized this way.
  // (Actually could perform some rad scaling on all nodes)

  /** True iff this overlay returns text to render. */
  public boolean hasText() { return false; }

  /**
   * Computes a grid to be superimposed on this overlay
   * to indicate it is currently selected.
   */
  public DataImpl getSelectionGrid() { return getSelectionGrid(false); }

  /**
   * Computes a grid to be superimposed on this overlay to
   * indicate it is currently selected.
   * If the outline flag is set, computes only an outline instead.
   */
  public DataImpl getSelectionGrid(boolean outline) {
    // Grid computation relies on parameters:
    //   (xGrid1, yGrid1): top-left endpoint of grid rectangle
    //   (xGrid2, yGrid2): top-right endpoint of grid rectangle
    //   (xGrid3, yGrid3): bottom-left endpoint of grid rectangle
    //   (xGrid4, yGrid4): bottom-right endpoint of grid rectangle
    //     horizGridCount: number of horizontal dividing lines
    //      vertGridCount: number of vertical dividing lines

    // compute (X, Y) values along grid left and right edges
    int numLR = outline ? 2 : (horizGridCount + 2);
    float[] xl = new float[numLR];
    float[] xr = new float[numLR];
    float[] yl = new float[numLR];
    float[] yr = new float[numLR];
    for (int i=0; i<numLR; i++) {
      float q = (float) i / (numLR - 1);
      xl[i] = q * (xGrid3 - xGrid1) + xGrid1;
      xr[i] = q * (xGrid4 - xGrid2) + xGrid2;
      yl[i] = q * (yGrid3 - yGrid1) + yGrid1;
      yr[i] = q * (yGrid4 - yGrid2) + yGrid2;
    }

    // compute (X, Y) values along grid top and bottom edges
    int numTB = outline ? 2 : (vertGridCount + 2);
    float[] xt = new float[numTB];
    float[] xb = new float[numTB];
    float[] yt = new float[numTB];
    float[] yb = new float[numTB];
    for (int i=0; i<numTB; i++) {
      float q = (float) i / (numTB - 1);
      xt[i] = q * (xGrid2 - xGrid1) + xGrid1;
      xb[i] = q * (xGrid4 - xGrid3) + xGrid3;
      yt[i] = q * (yGrid2 - yGrid1) + yGrid1;
      yb[i] = q * (yGrid4 - yGrid3) + yGrid3;
    }

    RealTupleType domain = overlay.getDomainType();
    TupleType range = overlay.getRangeType();

    // compute zig-zagging grid mesh values
    int count = 2 * (numLR + numTB) - 1;
    float[][] setSamples = new float[2][count];
    for (int i=0; i<numLR; i++) {
      int ndx = 2 * i;
      boolean dir = i % 2 == 0;
      setSamples[0][ndx] = dir ? xl[i] : xr[i];
      setSamples[1][ndx] = dir ? yl[i] : yr[i];
      setSamples[0][ndx + 1] = dir ? xr[i] : xl[i];
      setSamples[1][ndx + 1] = dir ? yr[i] : yl[i];
    }
    boolean leftToRight = numLR % 2 == 0;
    for (int i=0; i<numTB; i++) {
      int ndx = 2 * (numLR + i) - 1;
      boolean dir = i % 2 == 0;
      int ii = leftToRight ? i : (numTB - i - 1);
      setSamples[0][ndx] = dir ? xb[ii] : xt[ii];
      setSamples[1][ndx] = dir ? yb[ii] : yt[ii];
      setSamples[0][ndx + 1] = dir ? xt[ii] : xb[ii];
      setSamples[1][ndx + 1] = dir ? yt[ii] : yb[ii];
    }

    // populate grid color values
    Color c = outline ? Color.cyan : Color.yellow;
    float r = c.getRed() / 255f;
    float g = c.getGreen() / 255f;
    float b = c.getBlue() / 255f;
    float[][] fieldSamples = new float[4][count];
    for (int i=0; i<count; i++) {
      fieldSamples[0][i] = r;
      fieldSamples[1][i] = g;
      fieldSamples[2][i] = b;
      fieldSamples[3][i] = 1.0f;
    }

    // construct field
    FlatField field = null;
    try {
      GriddedSet fieldSet = new Gridded2DSet(domain,
        setSamples, setSamples[0].length, null, null, null, false);
      FunctionType fieldType = new FunctionType(domain, range);
      field = new FlatField(fieldType, fieldSet);
      field.setSamples(fieldSamples, false);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return field;
  }

  /** Changes X coordinate of the overlay's first endpoint. */
  public void setX(float x1) {
    if (!hasEndpoint()) return;
    this.x1 = x1;
    computeGridParameters();
  }

  /** Changes Y coordinate of the overlay's first endpoint. */
  public void setY(float y1) {
    if (!hasEndpoint()) return;
    this.y1 = y1;
    computeGridParameters();
  }

  /** Changes coordinates of the overlay's first endpoint. */
  public void setCoords(float x1, float y1) {
    if (!hasEndpoint()) return;
    float dx = x1-this.x1;
    float dy = y1-this.y1;
    this.x1 = x1;
    this.y1 = y1;
    if (hasNodes) {
      for (int i=0; i<numNodes; i++) {
        nodes[0][i] = nodes[0][i]+dx;
        nodes[1][i] = nodes[1][i]+dy;
      }
    }
    computeGridParameters();
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

  /** Gets X coordinate of the overlay's first endpoint. */
  public float getX() { return x1; }

  /** Gets Y coordinate of the overlay's first endpoint. */
  public float getY() { return y1; }

  /** Gets most recent x-coordinate in node array. */
  public float getLastNodeX() {
    return nodes[0][numNodes-1];
  }

  /** Gets most recent y-coordinate in node array. */
  public float getLastNodeY() {
    return nodes[1][numNodes-1];
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

  /** Changes X coordinate of the overlay's second endpoint. */
  public void setX2(float x2) {
    if (!hasEndpoint2()) return;
    this.x2 = x2;
    computeGridParameters();
  }

  /** Changes Y coordinate of the overlay's second endpoint. */
  public void setY2(float y2) {
    if (!hasEndpoint2()) return;
    this.y2 = y2;
    computeGridParameters();
  }

  /** Changes coordinates of the overlay's second endpoint. */
  public void setCoords2(float x2, float y2) {
    if (!hasEndpoint2()) return;
    this.x2 = x2;
    this.y2 = y2;
    computeGridParameters();
  }

  /** Gets X coordinate of the overlay's second endpoint. */
  public float getX2() { return x2; }

  /** Gets Y coordinate of the overlay's second endpoint. */
  public float getY2() { return y2; }

  /** Updates the coordinates of the bounding box of a noded object */
  public void updateBoundingBox() {
    if (!hasNodes || numNodes == 0) return;
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

  /** Changes text to render. */
  public void setText(String text) {
    if (!hasText()) return;
    this.text = text;
    computeGridParameters();
  }

  /** Gets text to render. */
  public String getText() { return text; }

  /** Sets color of this overlay. */
  public void setColor(Color c) { color = c; }

  /** Gets color of this overlay. */
  public Color getColor() { return color; }

  /** Sets whether overlay is solid. */
  public void setFilled(boolean filled) {
    if (canBeFilled()) this.filled = filled;
  }

  /** Gets whether overlay is solid. */
  public boolean isFilled() { return filled; }

  /** Sets group to which this overlay belongs. */
  public void setGroup(String group) { this.group = group; }

  /** Gets group to which this overlay belongs. */
  public String getGroup() { return group; }

  /** Sets notes for this overlay. */
  public void setNotes(String text) { notes = text; }

  /** Gets notes for this overlay. */
  public String getNotes() { return notes; }

  /** Sets whether this overlay is currently selected. */
  public void setSelected(boolean selected) { this.selected = selected; }

  /** Gets whether this overlay is currently selected. */
  public boolean isSelected() { return selected; }

  /** Sets whether this overlay is still being initially drawn. */
  public void setDrawing(boolean drawing) {
    this.drawing = drawing;
    overlay.setTextDrawn(!drawing);
  }

  /** Gets whether this overlay is still being initially drawn. */
  public boolean isDrawing() { return drawing; }

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

  // -- OverlayObject API Methods: node array mutators --
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

  /** Prints node array of current freeform; for debugging */
  private void printNodes(float[][] nodes) {
    System.out.println("Printing nodes...");
    for (int i = 0; i < nodes[0].length; i++){
      System.out.println(i+":("+nodes[0][i]+","+nodes[1][i]+")");
    }
  }

  /** Sets next node coordinates. */
  public void setNextNode(float x, float y) {
    //System.out.println("OverlayObject.setNextNode(...) called. " +
    //  "numNodes = " + numNodes + ", maxNodes = " + maxNodes);
    //printNodes(getNodes());
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

  // -- Internal OverlayObject API methods --

  /** Computes parameters needed for selection grid computation. */
  protected abstract void computeGridParameters();

  /** Sets value of largest and smallest x, y values. */
  protected void setBoundaries(float x, float y) {
    x1 = Math.min(x1, x);
    x2 = Math.max(x2, x);
    y1 = Math.min(y1, y);
    y2 = Math.max(y2, y);
  }

}// end class
