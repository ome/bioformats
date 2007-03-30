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

import java.awt.Color;
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

  /** Gets a DataImpl overlay indicating that this object is selected */
  public DataImpl getSelectionGrid() { return getSelectionGrid(false); }

  /** Gets a DataImpl overlay indicating that this object is selected */
  public DataImpl getSelectionGrid(boolean outline) {
    if (outline) return super.getSelectionGrid(outline);

    RealTupleType domain = overlay.getDomainType();
    TupleType range = overlay.getRangeType();

    float size = 0.02f * overlay.getScalingValue();
    float scl = .1f; // for now
    float delta = GLOW_WIDTH * scl;

    //System.out.println("x1 - size = " + (x1 - size)); // TEMP
    //System.out.println("x1 + size = " + (x1 + size)); // TEMP
    //System.out.println("y1 - size = " + (y1 - size)); // TEMP
    //System.out.println("y1 + size = " + (y1 + size)); // TEMP

    float xx1 = x1 - size - delta;
    float xx2 = x1 + size + delta;
    float yy1 = y1 + size + delta;
    float yy2 = y1 - size - delta;

    float dx = 0.0001f; // TEMP

    float[][] setSamples;
    if (true || 2 * delta > size) {
      // return box
      setSamples = new float[][]{
        {xx1, xx2, xx1, xx2},
        {yy1, yy1, yy2, yy2}
      };
    }
    else {
      // return cross
      /*
      setSamples = new float[][]{
        {xx1, x1 - delta, x1 - delta + dx, x1 + delta - dx, x1 + delta, xx2,
          xx1, x1 - delta, x1 - delta + dx, x1 + delta - dx, x1 + delta, xx2},
        {y1 + delta, y1 + delta, yy1, yy1, y1 + delta, y1 + delta,
          y1 - delta, y1 - delta, yy2, yy2, y1 - delta, y1 - delta}
      };*/
      setSamples = new float[][] {
        {xx1, x1 - delta, x1 - delta + dx,
          xx1, x1 - delta, x1 -delta + dx},
        {y1 + delta, y1 + delta, y1 + size + delta, 
          y1 - delta, y1 - delta, y1 - size - delta}
      };
    }

    //System.out.println("coords ==========================="); // TEMP
    /*
    for (int i=0; i<setSamples[0].length; i++) {
      System.out.println("(" + setSamples[0][i] + "," + setSamples[1][i] + ")");
    }
    */

    // construct range samples
    float r = GLOW_COLOR.getRed() / 255f;
    float g = GLOW_COLOR.getGreen() / 255f;
    float b = GLOW_COLOR.getBlue() / 255f;

    float[][] rangeSamples = new float[4][setSamples[0].length];
    Arrays.fill(rangeSamples[0], r);
    Arrays.fill(rangeSamples[1], g);
    Arrays.fill(rangeSamples[2], b);
    Arrays.fill(rangeSamples[3], GLOW_ALPHA);

    // construct field
    Gridded2DSet domainSet = null;
    FlatField field = null;
    try {
      domainSet = new Gridded2DSet(domain, setSamples, setSamples[0].length /
          2, 2, null, null, null, false);
      FunctionType fieldType = new FunctionType (domain, range);
      field = new FlatField(fieldType, domainSet);
      field.setSamples(rangeSamples);
    }
    catch (VisADException ex) { ex.printStackTrace(); }
    catch (RemoteException ex) { ex.printStackTrace(); }
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
