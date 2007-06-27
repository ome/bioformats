//
// Segment2DTest.java
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

package loci.visbio.util.test;

import junit.framework.TestCase;
import loci.visbio.util.MathUtil;
import loci.visbio.util.Segment2D;

/** Unit tests for the Segment2D class. */
public class Segment2DTest extends TestCase {

  public static final float DELTA = 0f;

  protected Segment2D a, b, c, d;

  /** Sets up the test fixture. */
  protected void setUp() {
    a = new Segment2D(new float[]{0f, 0f}, new float[]{1f, 1f});
    b = new Segment2D(new float[]{0f, 1f}, new float[]{1f, 0f});
    c = new Segment2D(new float[]{1f, 1f}, new float[]{2f, 2f});
    d = new Segment2D(new float[]{2f, 2f}, new float[]{3f, 1f});
  }

  /** Tears down the test fixture. */
  protected void tearDown() {
  }

  /** Tests the straddle function. */
  public void testStraddle() {
    // could include a test here for the cross product.  Not quite right, but
    // need to know!
    // intersecting segments
    assertEquals("a should straddle b", true, a.straddles(b));
    assertEquals("b should straddle a", true, b.straddles(a));

    // perpendicular but not intersecting
    assertEquals("b should straddle c", true, b.straddles(c));
    assertEquals("c should not straddle b", false, c.straddles(b));

    // These are collinear segments. Should probably return false.
    assertEquals("a should straddle c", true, a.straddles(c));
    assertEquals("c should not straddle a", true, a.straddles(c));
    assertEquals("a should straddle a", true, a.straddles(a));
  }

  /** Tests the intersects function. */
  public void testIntersect() {
    //intersecting segments
    assertEquals("a should intersect b", true, a.intersects(b));
    assertEquals("b should intersect a", true, b.intersects(a));

    // adjoining segments
    assertEquals("a should intersect c", true, a.intersects(c));
    assertEquals("c should intersect a", true, c.intersects(a));

    // disjoint sements
    assertEquals("c should not intersect b", false, c.intersects(b));
    assertEquals("b should not intersect c", false, b.intersects(c));
    assertEquals("a should not intersect d", false, a.intersects(d));

    // identical segments
    // not sure about this one
    assertEquals("a should intersect a", true, a.intersects(a));
  }

  /** Tests the bounding box function. */
  public void testBoundingBoxOverlapsBoundingBoxOf() {
    //intersecting segments
    assertEquals("a should overlap b", true,
        a.boundingBoxOverlapsBoundingBoxOf(b));
    assertEquals("b should overlap a", true,
        b.boundingBoxOverlapsBoundingBoxOf(a));

    // adjoining segments
    assertEquals("a should overlap c", true,
        a.boundingBoxOverlapsBoundingBoxOf(c));
    assertEquals("c should overlap a", true,
        c.boundingBoxOverlapsBoundingBoxOf(a));

    // disjoint segments whose boxes touch
    assertEquals("c should overlap b", true,
        c.boundingBoxOverlapsBoundingBoxOf(b));
    assertEquals("b should overlap c", true,
        b.boundingBoxOverlapsBoundingBoxOf(c));

    // identical segments
    // not sure about this one
    assertEquals("a should overlap a", true,
        a.boundingBoxOverlapsBoundingBoxOf(a));

    // disjoint bounding boxes
    assertEquals("a should not overlap d", false,
        a.boundingBoxOverlapsBoundingBoxOf(d));
  }

  public void testBetween() {
    float[] ca = {(float) c.getPtA().getX(), (float) c.getPtA().getY()};
    float[] cb = {(float) c.getPtB().getX(), (float) c.getPtB().getY()};
    float[] aa = {(float) a.getPtA().getX(), (float) a.getPtA().getY()};
    float[] ab = {(float) a.getPtB().getX(), (float) a.getPtB().getY()};

    assertEquals("ca should be inside aa and ab", true,
        MathUtil.inside(ca, aa, ab));
    assertEquals("cb should be inside aa and ab", false,
        MathUtil.inside(cb, aa, ab));
  }

} // @@@@@
