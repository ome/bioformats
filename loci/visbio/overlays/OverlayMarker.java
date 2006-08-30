//
// OverlayMarker.java
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

/** OverlayMarker is a marker crosshairs overlay. */
public class OverlayMarker extends OverlayObject {

  // -- Constructors --

  /** Constructs an uninitialized measurement marker. */
  public OverlayMarker(OverlayTransform overlay) { super(overlay); }

  /** Constructs a measurement marker. */
  public OverlayMarker(OverlayTransform overlay, float x, float y) {
    super(overlay);
    x1 = x;
    y1 = y;
    computeGridParameters();
  }

  // -- OverlayObject API methods --

  /** Gets VisAD data object representing this overlay. */
  public DataImpl getData() {
    RealTupleType domain = overlay.getDomainType();
    TupleType range = overlay.getRangeType();
    float size = 0.02f * overlay.getScalingValue();

    float[][] setSamples = {
      {x1, x1, x1, x1 + size, x1 - size},
      {y1 + size, y1 - size, y1, y1, y1}
    };
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
    double xx = x1 - x;
    double yy = y1 - y;
    return Math.sqrt(xx * xx + yy * yy);
  }

  /** Retrieves useful statistics about this overlay. */
  public String getStatistics() {
    return "Marker coordinates = (" + x1 + ", " + y1 + ")";
  }

  /** True iff this overlay has an endpoint coordinate pair. */
  public boolean hasEndpoint() { return true; }

  // -- Internal OverlayObject API methods --

  /** Computes parameters needed for selection grid computation. */
  protected void computeGridParameters() {
    float padding = 0.02f * overlay.getScalingValue();
    float xx1 = x1 - padding;
    float xx2 = x1 + padding;
    float yy1 = y1 - padding;
    float yy2 = y1 + padding;

    xGrid1 = xx1; yGrid1 = yy1;
    xGrid2 = xx2; yGrid2 = yy1;
    xGrid3 = xx1; yGrid3 = yy2;
    xGrid4 = xx2; yGrid4 = yy2;
    horizGridCount = 2; vertGridCount = 2;
  }

  // -- Object API methods --

  /** Gets a short string representation of this measurement marker. */
  public String toString() { return "Marker"; }

}
