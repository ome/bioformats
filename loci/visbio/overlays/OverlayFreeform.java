//
// OverlayFreeform.java
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

import java.rmi.RemoteException;
import java.util.Arrays;
import visad.*;
import loci.visbio.util.MathUtil;

/**
 * OverlayFreeform is a freeform overlay.
 * @author Greg Meyer
 */

public class OverlayFreeform extends OverlayObject {

  // -- Constructors --

  /** Constructs an uninitialized freeform. */
  public OverlayFreeform(OverlayTransform overlay) { super(overlay); }

  /** Constructs a freeform. */
  public OverlayFreeform(OverlayTransform overlay,
    float x1, float y1, float x2, float y2)
  {
    super(overlay);
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
    hasNodes = true;
    maxNodes = 100;
    nodes = new float[2][maxNodes];
    Arrays.fill(nodes[0], x1);
    Arrays.fill(nodes[1], y1);
    numNodes = 1;
    computeGridParameters();
    computeLength();
  }
  
  public OverlayFreeform(OverlayTransform overlay, float[][] nodes) {
    super(overlay);
    x1=x2=y1=y2=0f;
    hasNodes = true;
    this.nodes = nodes;
    numNodes = nodes[0].length;
    maxNodes = nodes[0].length;
    updateBoundingBox();
    computeGridParameters();
    computeLength();
  }

  // -- OverlayObject API methods --

  /** Gets VisAD data object representing this overlay. */
  public DataImpl getData() {
    RealTupleType domain = overlay.getDomainType();
    TupleType range = overlay.getRangeType();

    float[][] setSamples = nodes;
    float r = color.getRed() / 255f;
    float g = color.getGreen() / 255f;
    float b = color.getBlue() / 255f;

    FlatField field = null;
    try {
      SampledSet fieldSet = new Gridded2DSet(domain,
        setSamples, setSamples[0].length, null, null, null, false);
      if (filled) {
        Irregular2DSet roiSet =
          DelaunayCustom.fillCheck((Gridded2DSet) fieldSet, false);
        if (roiSet != null) fieldSet = roiSet;
      }

      int len = fieldSet.getSamples(false)[0].length;
      float[][] rangeSamples = new float[3][len];
      Arrays.fill(rangeSamples[0], r);
      Arrays.fill(rangeSamples[1], g);
      Arrays.fill(rangeSamples[2], b);

      FunctionType fieldType = new FunctionType(domain, range);
      field = new FlatField(fieldType, fieldSet);
      field.setSamples(rangeSamples);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return field;
  }

  /** Computes the shortest distance from this object's bounding box to the given point. */
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

  /** True iff this overlay supports the filled parameter. */
  public boolean canBeFilled() { return true; }

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

  /** Gets a short string representation of this freeform. */
  public String toString() { return "Freeform"; }

}
