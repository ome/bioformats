//
// OverlayArrow.java
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

/** OverlayArrow is an arrow wedge overlay. */
public class OverlayArrow extends OverlayObject {

  // -- Constructors --

  /** Constructs an uninitialized arrow wedge overlay. */
  public OverlayArrow(OverlayTransform overlay) { super(overlay); }

  /** Constructs an arrow wedge overlay. */
  public OverlayArrow(OverlayTransform overlay,
    float x1, float y1, float x2, float y2)
  {
    super(overlay);
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
    computeGridParameters();
  }

  // -- OverlayObject API methods --

  /** Gets VisAD data object representing this overlay. */
  public DataImpl getData() {
    float padding = 0.02f * overlay.getScalingValue();
    double xx = x2 - x1;
    double yy = y2 - y1;
    double dist = Math.sqrt(xx * xx + yy * yy);
    double mult = padding / dist;
    float qx = (float) (mult * xx);
    float qy = (float) (mult * yy);

    RealTupleType domain = overlay.getDomainType();
    TupleType range = overlay.getRangeType();

    float[][] setSamples = {
      {x1, x2 - qy, x2 + qy, x1}, {y1, y2 + qx, y2 - qx, y1}
    };

    boolean missing = false;
    for (int i=0; i<setSamples.length; i++) {
      for (int j=0; j<setSamples[i].length; j++) {
        if (Float.isNaN(setSamples[i][j])) missing = true;
      }
    }

    GriddedSet fieldSet = null;
    try {
      if (filled && !missing) {
        fieldSet = new Gridded2DSet(domain,
          setSamples, 2, 2, null, null, null, false, false);
      }
      else {
        fieldSet = new Gridded2DSet(domain,
          setSamples, setSamples[0].length, null, null, null, false);
      }
    }
    catch (VisADException exc) { exc.printStackTrace(); }

    Color col = selected ? GLOW_COLOR : color; 
    float r = col.getRed() / 255f;
    float g = col.getGreen() / 255f;
    float b = col.getBlue() / 255f;
    
    float[][] rangeSamples = new float[4][setSamples[0].length];
    Arrays.fill(rangeSamples[0], r);
    Arrays.fill(rangeSamples[1], g);
    Arrays.fill(rangeSamples[2], b);
    Arrays.fill(rangeSamples[3], 1.0f);

    FlatField field = null;
    try {
      FunctionType fieldType = new FunctionType(domain, range);
      field = new FlatField(fieldType, fieldSet);
      field.setSamples(rangeSamples);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return field;
  }

  /** Computes the shortest distance from this object to the given point. */
  public double getDistance(double x, double y) {
    return MathUtil.getDistance(new double[] {x1, y1},
      new double[] {x2, y2}, new double[] {x, y}, true);
  }

  /** Gets a selection grid for this object */
  public DataImpl getSelectionGrid() { return getSelectionGrid(false); }

  /** Gets a selection grid for this object */
  public DataImpl getSelectionGrid(boolean outline) {
    if (outline) return super.getSelectionGrid(true);

    RealTupleType domain = overlay.getDomainType();
    TupleType range = overlay.getRangeType();

    // compute corners of arrow tail
    float padding = 0.02f * overlay.getScalingValue();
    double xx = x2 - x1;
    double yy = y2 - y1;
    double dist = Math.sqrt(xx * xx + yy * yy);
    double mult = padding / dist;
    float qx = (float) (mult * xx);
    float qy = (float) (mult * yy);

    // determine internal coordinate sys. basis vectors
    // assuming arrow is oriented like this:
    //
    // (x1, y1) <--------{{{ (x2, y2)
    //
    //
    double[] x = {xx / dist, yy / dist};
    double[] y = {-yy / dist, xx /dist};

    // arrow lengths:
    double a, b, c;
    a = Math.sqrt(qx * qx + qy * qy);
    b = dist;
    c = Math.sqrt(a * a + b * b);

    double scl = 1; // for now
    double d = GLOW_WIDTH * scl;

    // compute four corners of highlighted zone
    float c1x = (float) (x1 - x[0] * d + y[0] * (d * c) / b);
    float c1y = (float) (y1 - x[1] * d + y[1] * (d * c) / b);
    float c2x = (float) (x2 + x[0] * d + y[0] * (a + d * (a + c) / b));
    float c2y = (float) (y2 + x[1] * d + y[1] * (a + d * (a + c) / b));
    float c3x = (float) (x2 + x[0] * d - y[0] * (a + d * (a + c) / b));
    float c3y = (float) (y2 + x[1] * d - y[1] * (a + d * (a + c) / b));
    float c4x = (float) (x1 - x[0] * d - y[0] * (d * c) / b);
    float c4y = (float) (y1 - x[1] * d - y[1] * (d * c) / b);

    // order and combine the coordinates for a Gridded2DSet [2 2]
    float[][] setSamples = {
      {c1x, c2x, c4x, c3x},
      {c1y, c2y, c4y, c3y}
    };
    
    // construct range samples
    float r = GLOW_COLOR.getRed() / 255f;
    float g = GLOW_COLOR.getGreen() / 255f;
    float bl = GLOW_COLOR.getBlue() / 255f;
    
    float[][] rangeSamples = new float[4][setSamples[0].length];
    Arrays.fill(rangeSamples[0], r);
    Arrays.fill(rangeSamples[1], g);
    Arrays.fill(rangeSamples[2], bl);
    Arrays.fill(rangeSamples[3], GLOW_ALPHA);

    // construct Field
    Gridded2DSet domainSet = null;
    FlatField field = null;
    try {
      domainSet = new Gridded2DSet(domain, setSamples,
        2, 2, null, null, null, false);
      FunctionType fieldType = new FunctionType(domain, range);
      field = new FlatField(fieldType, domainSet);
      field.setSamples(rangeSamples);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return field;
  }

  /** Retrieves useful statistics about this overlay. */
  public String getStatistics() {
    float xx = x2 - x1;
    float yy = y2 - y1;
    float angle = (float) (180 * Math.atan(xx / yy) / Math.PI);
    if (yy < 0) angle += 180;
    if (angle < 0) angle += 360;
    float length = (float) Math.sqrt(xx * xx + yy * yy);

    return "Arrow coordinates = (" + x1 + ", " + y1 + ")\n" +
      "Angle = " + angle + "; Length = " + length;
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
    float[] corners1 = OverlayLine.computeCorners(x1, y1, x2, y2, padding, 2);
    float[] corners2 = OverlayLine.computeCorners(x2, y2, x1, y1, padding, 1);

    xGrid1 = corners1[0]; yGrid1 = corners1[1];
    xGrid2 = corners1[2]; yGrid2 = corners1[3];
    xGrid3 = corners2[2]; yGrid3 = corners2[3];
    xGrid4 = corners2[0]; yGrid4 = corners2[1];
    horizGridCount = 3; vertGridCount = 2;
  }

  // -- Object API methods --

  /** Gets a short string representation of this overlay arrow. */
  public String toString() { return "Arrow"; }

}
