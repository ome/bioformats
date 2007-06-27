//
// OverlayOval.java
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

/** OverlayOval is a bounding oval overlay. */
public class OverlayOval extends OverlayObject {

  // -- Static Fields --

  /** The names of the statistics this object reports. */
  public static final String COORDS = "Coordinates";
  public static final String CTR = "Center";
  public static final String RAD = "Radius";
  public static final String MAJ = "Major Axis Length";
  public static final String MIN = "Minor Axis Length";
  public static final String AREA = "Area";
  public static final String ECC = "Eccentricity";
  public static final String CIRC = "Circumference (approximate)";
  protected static final String[] STAT_TYPES =  {COORDS, CTR, RAD, MAJ, MIN,
    AREA, ECC, CIRC};

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

  // -- Constructors --

  /** Constructs an uninitialized bounding oval. */
  public OverlayOval(OverlayTransform overlay) { super(overlay); }

  /** Constructs an bounding oval. */
  public OverlayOval(OverlayTransform overlay,
    float x1, float y1, float x2, float y2)
  {
    super(overlay);
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
  }

  // -- Static methods --

  /** Returns the names of the statistics this object reports. */
  public static String[] getStatTypes() { return STAT_TYPES; }

  // -- OverlayObject API methods --

  /** Returns whether this object is drawable, i.e., is of nonzero
   *  size, area, length, etc.
   */
  public boolean hasData() { return (x1 != x2 && y1 != y2); }

  /** Gets VisAD data object representing this overlay. */
  public DataImpl getData() {
    if (!hasData()) return null;

    RealTupleType domain = overlay.getDomainType();
    TupleType range = overlay.getRangeType();

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
    double xdist = 0;
    if (x < x1 && x < x2) xdist = Math.min(x1, x2) - x;
    else if (x > x1 && x > x2) xdist = x - Math.max(x1, x2);
    double ydist = 0;
    if (y < y1 && y < y2) ydist = Math.min(y1, y2) - y;
    else if (y > y1 && y > y2) ydist = y - Math.max(y1, y2);
    return Math.sqrt(xdist * xdist + ydist * ydist);
  }

  /** Returns a specific statistic of this object. */
  public String getStat(String name) {
    float xx = x2 - x1;
    float yy = y2 - y1;
    float centerX = x1 + xx / 2;
    float centerY = y1 + yy / 2;
    float radiusX = (xx < 0 ? -xx : xx) / 2;
    float radiusY = (yy < 0 ? -yy : yy) / 2;
    float major, minor;
    if (radiusX > radiusY) {
      major = radiusX;
      minor = radiusY;
    }
    else {
      major = radiusY;
      minor = radiusX;
    }
    float eccen = (float) Math.sqrt(1 - (minor * minor) / (major * major));
    float area = (float) (Math.PI * major * minor);

    // ellipse circumference approximation algorithm due to Ramanujan found at
    // http://mathforum.org/dr.math/faq/formulas/faq.ellipse.circumference.html
    float mm = (major - minor) / (major + minor);
    float q = 3 * mm * mm;
    float circum = (float) (Math.PI *
      (major + minor) * (1 + q / (10 + Math.sqrt(4 - q))));

    if (name.equals(COORDS)) {
      return "(" + x1 + ", " + y1 + ")-(" + x2 + ", " + y2 + ")";
    }
    else if (name.equals(CTR)) {
      return "(" + centerX + ", " + centerY + ")";
    }
    else if (name.equals(RAD)) {
      return "(" + radiusX + ", " + radiusY + ")";
    }
    else if (name.equals(MAJ)) {
      return "" + major;
    }
    else if (name.equals(MIN)) {
      return "" + minor;
    }
    else if (name.equals(AREA)) {
      return "" + area;
    }
    else if (name.equals(ECC)) {
      return "" + eccen;
    }
    else if (name.equals(CIRC)) {
      return "" + circum;
    }
    else return "No such statistic for this overlay type";
  }

  /** Retrieves useful statistics about this overlay. */
  public String getStatistics() {
    float xx = x2 - x1;
    float yy = y2 - y1;
    float centerX = x1 + xx / 2;
    float centerY = y1 + yy / 2;
    float radiusX = (xx < 0 ? -xx : xx) / 2;
    float radiusY = (yy < 0 ? -yy : yy) / 2;
    float major, minor;
    if (radiusX > radiusY) {
      major = radiusX;
      minor = radiusY;
    }
    else {
      major = radiusY;
      minor = radiusX;
    }
    float eccen = (float) Math.sqrt(1 - (minor * minor) / (major * major));
    float area = (float) (Math.PI * major * minor);

    // ellipse circumference approximation algorithm due to Ramanujan found at
    // http://mathforum.org/dr.math/faq/formulas/faq.ellipse.circumference.html
    float mm = (major - minor) / (major + minor);
    float q = 3 * mm * mm;
    float circum = (float) (Math.PI *
      (major + minor) * (1 + q / (10 + Math.sqrt(4 - q))));

    return "Oval " + COORDS + " = (" + x1 + ", " + y1 + "), " +
      CTR + " = (" + centerX + ", " + centerY + "), " +
      RAD + " = (" + radiusX + ", " + radiusY + ")\n" +
      MAJ + " = " + major + "; " + MIN + " = " + minor + "\n" +
      AREA + " = " + area + "; " + ECC + " = " + eccen + "\n" +
      CIRC + " = " + circum;
  }

  /** True iff this overlay has an endpoint coordinate pair. */
  public boolean hasEndpoint() { return true; }

  /** True iff this overlay has a second endpoint coordinate pair. */
  public boolean hasEndpoint2() { return true; }

  /** True iff this overlay supports the filled parameter. */
  public boolean canBeFilled() { return true; }

  // -- Object API methods --

  /** Gets a short string representation of this overlay oval. */
  public String toString() { return "Oval"; }

}
