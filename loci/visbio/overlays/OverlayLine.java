//
// OverlayLine.java
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

/** OverlayLine is an overlay tool for drawing measurement lines. */
public class OverlayLine extends OverlayObject {

  // -- Fields --

  /** Endpoint coordinates. */
  protected float x1, y1, x2, y2;


  // -- Constructor --

  /** Constructs a measurement line. */
  public OverlayLine(OverlayTransform overlay,
    float x1, float y1, float x2, float y2)
  {
    super(overlay);
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
  }

  // -- OverlayLine API methods --

  /** Changes coordinates of the line's first endpoint. */
  public void setCoords1(float x1, float y1) {
    this.x1 = x1;
    this.y1 = y1;
  }

  /** Changes coordinates of the line's second endpoint. */
  public void setCoords2(float x2, float y2) {
    this.x2 = x2;
    this.y2 = y2;
  }


  // -- OverlayObject API methods --

  /** Gets VisAD data object representing this overlay. */
  public DataImpl getData() {
    RealTupleType domain = overlay.getDomainType();
    RealTupleType range = overlay.getRangeType();

    float[][] setSamples = {{x1, x2}, {y1, y2}};
    float[][] fieldSamples = {
      {1, 1}, // red
      {1, 1}, // green
      {1, 1}  // blue
    };

    FlatField field = null;
    try {
      GriddedSet fieldSet = new Gridded2DSet(domain,
        setSamples, 2, null, null, null, false);
      FunctionType fieldType = new FunctionType(domain, range);
      field = new FlatField(fieldType, fieldSet);
      field.setSamples(fieldSamples, false);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return field;
  }

}
