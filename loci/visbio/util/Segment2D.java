//
// Segment2D.java
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

package loci.visbio.util;

import java.awt.geom.Point2D.Float;

/** A 2D line segment. */
public class Segment2D {
  
  /** Endpoints. */
  protected Float a, b;

  /** Constructor. */
  public Segment2D(float[] a, float[] b) {
    this.a = new Float(a[0], a[1]);
    this.b = new Float(b[0], b[1]);
  }

  public Float getPtA() { return a; }

  public Float getPtB() { return b; }

  /** Whether another segment intersects this one. */
  public boolean intersects(Segment2D y) {
    // first check bounding box, then straddle
    if (this.boundingBoxOverlapsBoundingBoxOf(y) && 
        this.straddles(y) && y.straddles(this)) return true;
    else return false;
 }

  /** Whether another segment meets the bounding box of this segment.
   *  True if either endpt of the supplied segment is in/on the bounding
   *  box. */
  public boolean boundingBoxOverlapsBoundingBoxOf(Segment2D y) {
    // collect all parameters
    float[] xa = {(float) this.a.getX(), (float) this.a.getY()};
    float[] xb = {(float) this.b.getX(), (float) this.b.getY()};
    float[] ya = {(float) y.getPtA().getX(), (float) y.getPtA().getY()};
    float[] yb = {(float) y.getPtB().getX(), (float) y.getPtB().getY()};

    // inside is not strict--a point on the boundary is considered inside 
    boolean meets = false;
    if ((MathUtil.inside(xa, ya, yb) || MathUtil.inside(xb, ya, yb)) ||
        (MathUtil.inside(ya, xa, xb) || MathUtil.inside(yb, xa, xb)))
      meets = true;
    return meets;
  }

  /** Whether another segment crosses the line defined by this segment */
  public boolean straddles(Segment2D y) {
    // collect all parameters
    double ax = this.a.getX(); double ay = this.a.getY();
    double bx = this.a.getX(); double by = this.b.getY();
    double yax = y.a.getX(); double yay = y.a.getY();
    double ybx = y.b.getX(); double yby = y.b.getY();

    float[] v1 = {(float) (ax - yax), (float) (ay - yay)};
    float[] v2 = {(float) (ybx - yax), (float) (yby - yay)};
    float[] v3 = {(float) (bx - yax), (float) (by - yay)};

    float cross1 = MathUtil.cross2D(v1, v2);
    float cross2 = MathUtil.cross2D(v3, v2);

    // return true if cross products have opposite signs
    if ((cross1 < 0 && cross2 > 0) ||
        (cross1 > 0 && cross2 < 0) ||
        cross1 == 0 || cross2 == 0) return true;
    else return false;
  }

  public String toString() {
    return "[" + a.getX() + "," + a.getY() + "]->[" + b.getX() + "," +
      b.getY() + "]";
  }
}
