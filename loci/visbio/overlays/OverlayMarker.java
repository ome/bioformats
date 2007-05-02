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

  // -- Static Fields --

  /** The names of the statistics this object reports */
  protected static String[] statTypes =  {"Coordinates"};

  // -- Constructors --

  /** Constructs an uninitialized measurement marker. */
  public OverlayMarker(OverlayTransform overlay) { super(overlay); }

  /** Constructs a measurement marker. */
  public OverlayMarker(OverlayTransform overlay, float x, float y) {
    super(overlay);
    x1 = x;
    y1 = y;
  }

  // -- Static methods --

  /** Returns the names of the statistics this object reports */
  public static String[] getStatTypes() {return statTypes;}

  // -- OverlayObject API methods --

  /** Gets VisAD data object representing this overlay. */
  public DataImpl getData() {
    if (!hasData()) return null;
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

  /** Returns whether this object is drawable, i.e., is of nonzero
   *  size, area, length, etc.
   */
  public boolean hasData() { return true; }

  /** Computes the shortest distance from this object to the given point. */
  public double getDistance(double x, double y) {
    double xx = x1 - x;
    double yy = y1 - y;
    return Math.sqrt(xx * xx + yy * yy);
  }

  /** Returns a specific statistic of this object*/
  public String getStat(String name) {
    if (name.equals("Coordinates")) {
      return "(" + x1 + ", " + y1 + ")";
    }
    else return "No such statistic for this overlay type";
  }

  /** Retrieves useful statistics about this overlay. */
  public String getStatistics() {
    return "Marker coordinates = (" + x1 + ", " + y1 + ")";
  }

  /** True iff this overlay has an endpoint coordinate pair. */
  public boolean hasEndpoint() { return true; }

  // -- Object API methods --

  /** Gets a short string representation of this measurement marker. */
  public String toString() { return "Marker"; }

}
