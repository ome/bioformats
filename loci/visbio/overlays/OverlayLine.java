//
// OverlayLine.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2005 Curtis Rueden.

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
import loci.visbio.util.MathUtil;
import visad.*;

/** OverlayLine is a measurement line overlay. */
public class OverlayLine extends OverlayObject {

  // -- Constructors --

  /** Constructs an uninitialized measurement line. */
  public OverlayLine(OverlayTransform overlay) { super(overlay); }

  /** Constructs a measurement line. */
  public OverlayLine(OverlayTransform overlay,
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
    RealTupleType domain = overlay.getDomainType();
    TupleType range = overlay.getRangeType();

    float[][] setSamples = {{x1, x2}, {y1, y2}};
    float r = color.getRed() / 255f;
    float g = color.getGreen() / 255f;
    float b = color.getBlue() / 255f;
    float[][] rangeSamples = new float[3][setSamples[0].length];
    Arrays.fill(rangeSamples[0], r);
    Arrays.fill(rangeSamples[1], g);
    Arrays.fill(rangeSamples[2], b);

    FlatField field = null;
    try {
      GriddedSet fieldSet = new Gridded2DSet(domain,
        setSamples, setSamples[0].length, null, null, null, false);
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

  /** Retrieves useful statistics about this overlay. */
  public String getStatistics() {
    float xx = x2 - x1;
    float yy = y2 - y1;
    float length = (float) Math.sqrt(xx * xx + yy * yy);

    return "Line coordinates = (" + x1 + ", " + y1 +
      ")-(" + x2 + ", " + y2 + ")\n" +
      "Length = " + length;
  }

  /** True iff this overlay has an endpoint coordinate pair. */
  public boolean hasEndpoint() { return true; }

  /** True iff this overlay has a second endpoint coordinate pair. */
  public boolean hasEndpoint2() { return true; }


  // -- Internal OverlayObject API methods --

  /** Computes parameters needed for selection grid computation. */
  protected void computeGridParameters() {
    float padding = 0.02f * overlay.getScalingValue();
    float[] corners1 = computeCorners(x1, y1, x2, y2, padding, 1);
    float[] corners2 = computeCorners(x2, y2, x1, y1, padding, 1);

    xGrid1 = corners1[0]; yGrid1 = corners1[1];
    xGrid2 = corners1[2]; yGrid2 = corners1[3];
    xGrid3 = corners2[2]; yGrid3 = corners2[3];
    xGrid4 = corners2[0]; yGrid4 = corners2[1];
    horizGridCount = 3; vertGridCount = 2;
  }


  // -- Object API methods --

  /** Gets a short string representation of this measurement line. */
  public String toString() { return "Line"; }


  // -- Helper methods --

  /**
   * Helper method for computing coordinates of two corner points of the
   * rectangular selection grid for a line or arrow overlay.
   */
  protected static float[] computeCorners(float x1, float y1,
    float x2, float y2, float padding, float multiplier)
  {
    // multiplier is used to widen the distance between corner points
    // appropriately for the "wide" end of the arrow overlay; for lines,
    // multiplier is 1 (no widening required)

    double xx = x2 - x1;
    double yy = y2 - y1;
    double dist = Math.sqrt(xx * xx + yy * yy);
    double mult = padding / dist;
    float qx = (float) (mult * xx);
    float qy = (float) (mult * yy);

    return new float[] {
      x2 + qx + multiplier * qy, y2 + qy - multiplier * qx,
      x2 + qx - multiplier * qy, y2 + qy + multiplier * qx
    };
  }

}
