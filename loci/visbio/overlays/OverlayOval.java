//
// OverlayOval.java
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

import java.util.Arrays;

import visad.*;

/** OverlayOval is an oval overlay. */
public class OverlayOval extends OverlayObject {

  // -- Constants --

  /** Computed (X, Y) pairs for top 1/2 of a unit circle. */
  protected static final float[][] ARC = arc();

  /** Computes the top 1/2 of a unit circle. */
  private static float[][] arc() {
    int res = 16; // resolution for 1/8 of circle
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


  // -- Fields --

  /** Endpoint coordinates. */
  protected float x1, y1, x2, y2;

  /** Flag indicating overlay is solid. */
  protected boolean filled;


  // -- Constructor --

  /** Constructs an oval. */
  public OverlayOval(OverlayTransform overlay,
    float x1, float y1, float x2, float y2)
  {
    super(overlay);
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
  }


  // -- OverlayOval API methods --

  /** Changes coordinates of the oval's bounding box's upper left corner. */
  public void setCoords1(float x1, float y1) {
    this.x1 = x1;
    this.y1 = y1;
  }

  /** Changes coordinates of the oval's bounding box's lower right corner. */
  public void setCoords2(float x2, float y2) {
    this.x2 = x2;
    this.y2 = y2;
  }

  /** Sets whether overlay is solid. */
  public void setFilled(boolean filled) { this.filled = filled; }

  /** Gets whether overlay is solid. */
  public boolean isFilled() { return filled; }


  // -- OverlayObject API methods --

  /** Gets VisAD data object representing this overlay. */
  public DataImpl getData() {
    RealTupleType domain = overlay.getDomainType();
    RealTupleType range = overlay.getRangeType();

    float cx = (x1 + x2) / 2;
    float cy = (y1 + y2) / 2;
    float rx = cx > x1 ? cx - x1 : cx - x2;
    float ry = cy > y1 ? cy - y1 : cy - y2;

    int arcLen = ARC[0].length;
    int len = 2 * arcLen;
    float[][] setSamples = new float[2][filled ? len : len + 1];

    // top half of circle
    for (int i=0; i<arcLen; i++) {
      setSamples[0][i] = cx + rx * ARC[0][i];
      setSamples[1][i] = cy + ry * ARC[1][i];
    }

    // bottom half of circle
    for (int i=0; i<arcLen; i++) {
      int ndx = filled ? arcLen + i : len - i - 1;
      setSamples[0][ndx] = cx + rx * ARC[0][i];
      setSamples[1][ndx] = cy - ry * ARC[1][i];
    }

    GriddedSet fieldSet = null;
    try {
      if (filled) {
        fieldSet = new Gridded2DSet(domain,
          setSamples, arcLen, 2, null, null, null, false);
      }
      else {
        setSamples[0][len] = setSamples[0][0];
        setSamples[1][len] = setSamples[1][0];
        fieldSet = new Gridded2DSet(domain,
          setSamples, setSamples[0].length, null, null, null, false);
      }
    }
    catch (VisADException exc) { exc.printStackTrace(); }

    float r = color.getRed() / 255f;
    float g = color.getGreen() / 255f;
    float b = color.getBlue() / 255f;
    float[][] fieldSamples = new float[3][setSamples[0].length];
    Arrays.fill(fieldSamples[0], r);
    Arrays.fill(fieldSamples[1], g);
    Arrays.fill(fieldSamples[2], b);

    FlatField field = null;
    try {
      FunctionType fieldType = new FunctionType(domain, range);
      field = new FlatField(fieldType, fieldSet);
      field.setSamples(fieldSamples, false);
    }
    catch (VisADException exc) { exc.printStackTrace(); }
    catch (RemoteException exc) { exc.printStackTrace(); }
    return field;
  }

}
