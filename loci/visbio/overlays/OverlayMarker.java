//
// OverlayMarker.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2004 Curtis Rueden.

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

import visad.*;

/** OverlayMarker is a marker crosshairs overlay. */
public class OverlayMarker extends OverlayObject {

  // -- Fields --

  /** Endpoint coordinates. */
  protected float x, y;


  // -- Constructor --

  /** Constructs a measurement marker. */
  public OverlayMarker(OverlayTransform overlay, float x, float y) {
    super(overlay);
    this.x = x;
    this.y = y;
  }


  // -- OverlayMarker API methods --

  /** Changes coordinates of the marker. */
  public void setCoords(float x, float y) {
    this.x = x;
    this.y = y;
  }


  // -- OverlayObject API methods --

  /** Gets VisAD data object representing this overlay. */
  public DataImpl getData() {
    RealTupleType domain = overlay.getDomainType();
    RealTupleType range = overlay.getRangeType();
    float size = 0.02f * overlay.getScalingValue();

    float[][] setSamples = {
      {x, x, x, x + size, x - size},
      {y + size, y - size, y, y, y}
    };
    float r = color.getRed() / 255f;
    float g = color.getGreen() / 255f;
    float b = color.getBlue() / 255f;
    float[][] fieldSamples = {
      {r, r, r, r, r},
      {g, g, g, g, g},
      {b, b, b, b, b}
    };

    FlatField field = null;
    try {
      GriddedSet fieldSet = new Gridded2DSet(domain,
        setSamples, 5, null, null, null, false);
      FunctionType fieldType = new FunctionType(domain, range);
      field = new FlatField(fieldType, fieldSet);
      field.setSamples(fieldSamples, false);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return field;
  }

  /** Computes the shortest distance from this object to the given point. */
  public double getDistance(double x, double y) {
    double xx = this.x - x;
    double yy = this.y - y;
    return Math.sqrt(xx * xx + yy * yy);
  }

}
