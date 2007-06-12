//
// MathUtil.java
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

import java.math.BigInteger;

/** MathUtil contains useful mathematical functions. */
public final class MathUtil {

  // -- Constructor --

  private MathUtil() { }

  // -- Utility methods --

  /**
   * Gets the distance between the points p and q, using
   * the given conversion values between pixels and microns.
   *
   * @param p Coordinates of the first endpoint
   * @param q Coordinates of the second endpoint
   * @param m Conversion values between microns and pixels
   */
  public static double getDistance(double[] p, double[] q, double[] m) {
    int len = p.length;
    double sum = 0;
    for (int i=0; i<len; i++) {
      double dist = m[i] * (q[i] - p[i]);
      sum += dist * dist;
    }
    return Math.sqrt(sum);
  }

  /**
   * Computes the minimum distance between the point v and the line a-b.
   *
   * @param a Coordinates of the line's first endpoint
   * @param b Coordinates of the line's second endpoint
   * @param v Coordinates of the standalone point
   * @param segment Whether distance computation should be constrained
   *   to the given line segment
   */
  public static double getDistance(double[] a, double[] b, double[] v,
    boolean segment)
  {
    double[] p = getProjection(a, b, v, segment);
    return getDistance(p, v);
  }

  /**
   * Computes the minimum distance between the point p and the point v.
   *
   * @param p Coordinates of the first point
   * @param v Coordinates of the second point
   */
  public static double getDistance (double[] p, double[] v) {
    int len = p.length;  // redundant with getProjection
    double sum = 0;
    for (int i=0; i<len; i++) {
      double dist = p[i] - v[i];
      sum += dist * dist;
    }
    return Math.sqrt(sum);
  }

  /**
   * Computes the minimum distance between the point p and the point v in
   * double precision.
   *
   * @param p Coordinates of the first point
   * @param v Coordinates of the second point
   */
  public static double getDistance (float[] p, float[] v) {
    double[] pp = new double[p.length];
    double[] vv = new double[v.length];
    return getDistance(pp, vv);
  }

  /**
   * Computes the projection of the point v onto the line segment a-b.
   *
   * @param a Coordinates of the segment's first endpoint
   * @param b Coordinates of the segment's second endpoint
   * @param v Coordinates of the point to be projected
   * @param segment Whether the projection should be constrained
   *   to the given line segment
   */
  public static double[] getProjection(double[] a,
    double[] b, double[] v, boolean segment)
  {
    int len = a.length;
    // vectors
    double[] ab = new double[len];
    double[] va = new double[len];
    for (int i=0; i<len; i++) {
      ab[i] = a[i] - b[i];
      va[i] = v[i] - a[i];
    }

    // project v onto (a, b)
    double numer = 0;
    double denom = 0;
    for (int i=0; i<len; i++) {
      numer += va[i] * ab[i];
      denom += ab[i] * ab[i];
    }
    double c = numer / denom;
    double[] p = new double[len];
    for (int i=0; i<len; i++) p[i] = c * ab[i] + a[i];

    int flag = 0;
    if (segment) {
      for (int i=0; i<len; i++) {
        if (p[i] > a[i] && p[i] > b[i]) flag = a[i] > b[i] ? 1 : 2;
        else if (p[i] < a[i] && p[i] < b[i]) flag = a[i] < b[i] ? 1 : 2;
        else continue;
        break;
      }
    }

    if (flag == 0) return p;
    else if (flag == 1) return a;
    else return b;
  }

  /** Gets distance to a curve of points connected by straight line segments.
   *  Also returns a segment index and wieght describing the location of the
   *  nearest point on the curve (see below).
   *
   *  @param x x coordinate of point in question
   *  @param y y coordinate of point in question
   *  @return an array double[3] with element 0 the distance to the nearest
   *  point on the curve, element 1 the node index i of one end of
   *  closest line segment (the other end being i+1), and element 2 the weight
   *  (between zero and one) for determining the location of the closest point
   *  by interpolation along the segment (i, i+1).
   */
  public static double[] getDistSegWt(double[][] nodes, double x, double y) {
    // assumes a non-ragged array of float[2][numNodes]
    double minDist = Double.MAX_VALUE;
    int seg = 0;
    double weight = 0;

    int numNodes = nodes[0].length;

    // toss out the trivial case
    if (numNodes == 1) {
      double xdist = x - nodes[0][0];
      double ydist = y - nodes[1][0];
      minDist = Math.sqrt(xdist * xdist + ydist * ydist);
    } else {

      for (int i=0; i<numNodes-1; i++) {
         double[] a = {nodes[0][i], nodes[1][i]};
         double[] b = {nodes[0][i+1], nodes[1][i+1]};
         double[] p = {x, y};

         double[] proj = getProjection(a, b, p, true);
         double dist = getDistance(p, proj);

         if (dist < minDist) {
           minDist = dist;
           seg = i;
           double segDist = getDistance (a, b);
           double fracDist = getDistance (a, proj);
           weight = fracDist / segDist;
         }
       }
    }

    // 'seg' is always an integer;
    // cast down to int in functions using this method.
    double[] retvals = {minDist, (double) seg, weight};
    return retvals;
  }

  /** Gets distance to a curve of points connected by straight line segments.
   *  Also returns a segment index and wieght describing the location of the
   *  nearest point on the curve (see below).
   *  All math takes place in double precision (floats upcast).
   *
   *  @param x x coordinate of point in question
   *  @param y y coordinate of point in question
   *  @return an array double[3] with element 0 the distance to the nearest
   *  point on the curve, element 1 the node index i of one end of
   *  closest line segment (the other end being i+1), and element 2 the weight
   *  (between zero and one) for determining the location of the closest point
   *  by interpolation along the segment (i, i+1).
   */
  public static double[] getDistSegWt(float[][] nodes, float x, float y) {
    // assumes a non-ragged array of float[2][numNodes]
    double minDist = Double.MAX_VALUE;
    int seg = 0;
    double weight = 0;

    int numNodes = nodes[0].length;

    // toss out the trivial case
    if (numNodes == 1) {
      double xdist = x - nodes[0][0];
      double ydist = y - nodes[1][0];
      minDist = Math.sqrt(xdist * xdist + ydist * ydist);
    } else {

      for (int i=0; i<numNodes-1; i++) {
         double[] a = {(double) nodes[0][i], (double) nodes[1][i]};
         double[] b = {(double) nodes[0][i+1], (double) nodes[1][i+1]};
         double[] p = {(double) x, (double) y};

         double[] proj = getProjection(a, b, p, true);
         double dist = getDistance(p, proj);

         if (dist < minDist) {
           minDist = dist;
           seg = i;
           double segDist = getDistance (a, b);
           double fracDist = getDistance (a, proj);
           weight = fracDist / segDist;
         }
       }
    }
    // 'seg' is always an integer;
    // cast down to int in functions using this method.
    double[] retvals = {minDist, (double) seg, weight};
    return retvals;
  }

  /** Rounds the value to nearest value along the given progression. */
  public static int getNearest(double val, int min, int max, int step) {
    int lo = (int) ((val - min) / step);
    int hi = lo + step;
    int v = (val - lo < hi - val) ? lo : hi;
    if (v < min) v = min;
    else if (v > max) {
      int q = (max - min) / step;
      v = min + q * step;
    }
    return v;
  }

  /** Units for use with getProductWithUnit method. */
  private static final String[] UNITS = {
    "", "kilo", "mega", "giga", "tera", "peta", "exa", "zetta", "yotta",
    "xona", "weka", "vunda", "uda", "treda", "sorta", "rinta", "quexa",
    "pepta", "ocha", "nena", "minga", "luma"
  };

  /**
   * Returns the given number as a string complete with unit
   * (e.g., kilo, mega, etc.), to the specififed number of decimal places.
   */
  public static String getValueWithUnit(BigInteger value, int places) {
    if (value == null) return null;
    String s = "1";
    for (int i=0; i<places; i++) s += "0";
    BigInteger scale = new BigInteger(s);
    value = value.multiply(scale);

    int unit = 0;
    BigInteger kilo = new BigInteger("1024");
    BigInteger half = new BigInteger("512");
    BigInteger stop = kilo.multiply(scale);
    while (value.compareTo(stop) >= 0) {
      BigInteger[] bi = value.divideAndRemainder(kilo);
      value = bi[0];
      if (bi[1].compareTo(half) >= 0) value = value.add(BigInteger.ONE);
      unit++;
    }
    BigInteger[] bi = value.divideAndRemainder(scale);
    String dec = bi[1].toString();
    while (dec.length() < places) dec = "0" + dec;
    String u = unit < UNITS.length ? UNITS[unit] : ("(2^" + (3 * unit) + ")");
    String result = bi[0].toString();
    if (places > 0) result += "." + dec;
    result += " " + u;
    return result;
  }
  
  // -- Vector Math Methods --

  /** Computes the cross product of two 2D vectors. */
  public static float cross2D(float[] v1, float[] v2) {
    return v1[0] * v2[1] - v1[1] * v2[0];
  }

  /** Computes the N-D unit vector in the direction as the vector supplied. */
  public static float[] unit(float[] v) {
    float mag = mag(v);
    float[] vHat = new float[v.length];
    for (int i=0; i<v.length; i++) {
      vHat[i] = v[i] / mag;
    }
    return vHat;
  }

  /** Computes the dot product of two N-D vectors */ 
  public static float dot(float[] a, float[] b) {
    float sum = 0f;
    for (int i=0; i<a.length; i++) {
      sum += a[i] * b[i]; 
    }
    return sum;
  }

  /** Computes the magnitude of an N-D vector. */
  public static float mag(float[] a) {
    return (float) Math.sqrt(dot(a, a));
  }

  /** Creates the vector p2-(minus) p1. */
  public static float[] vector(float[] p1, float[] p2) {
    // assumes p1, p2 have same lengths
    if (p1.length != p2.length) return null;
    int len = p1.length;
    float[] v = new float[len];
    for (int i=0; i<len; i++) {
      v[i] = p1[i] - p2[i];
    }
    return v;
  }

  /** Whether the point a is inside the N-D box implied by points
   *  b2 and b2 (i.e., in 2D, whether a is inside the box with diagonal
   *  b1-b2; in 3D, whether a is inside the cube with diagonal b1-b2).
   */ 
  public static boolean between(float[] a, float[] b1, float[] b2) {
    // assumes a, b1, b2 have same lengths
    boolean between = true;
    for (int i=0; i<a.length; i++) {
      boolean flip = b1[i] < b2[i] ? false : true;
      float lo = flip ? b2[i] : b1[i];
      float hi = flip ? b1[i] : b2[i];
      if (a[i] < lo || a[i] > hi) {
        between = false; 
        break;
      }
    }
    return between;
  }

  /** Gets a unit vector which bisects (p1 - p2) and (p3 - p2).  */
  public static float[] getBisectorVector2D(float[] p1, float[] p2, float[] p3)
  { 
    System.out.println("entering getBisectorVector2D ..."); //TEMP
    float[] v1 = vector(p1, p2);
    float[] v2 = vector(p3, p2);
    float[] v1Hat = unit(v1);
    float[] v2Hat = unit(v2);
    float[] vAvg = {(v1Hat[0] + v2Hat[0]) / 2f, (v1Hat[1] + v2Hat[1]) / 2f};
    
    float[] bisector;
    if (vAvg[0] == 0 && vAvg[1] == 0) {
      float[] aBisector = new float[]{-v1[1], v1[0]};
      bisector = unit(aBisector);
    }
    else {
      bisector = unit(vAvg);
    }
      
    // System.out.println("v1 = " + v1[0] + " " + v1[1]);
    // System.out.println("v2 = " + v2[0] + " " + v2[1]);
    // System.out.println("v1Hat = " + v1Hat[0] + " " + v1Hat[1]);
    // System.out.println("v2Hat = " + v2Hat[0] + " " + v2Hat[1]);
    // System.out.println("vAvg = " + vAvg[0] + " " + vAvg[1]);
    // System.out.println("bisector = " + bisector[0] + " " + bisector[1]);
    // System.out.println("p1a = " + p1a[0] + " " + p1a[1]);
    // System.out.println("p2a = " + p2a[0] + " " + p2a[1]);
    // System.out.println("p4a = " + p4a[0] + " " + p4a[1]);
    // System.out.println("v42 = " + v42[0] + " " + v42[1]);
    // System.out.println("v42Hat = " +v42Hat[0] + " " + v42Hat[1]);
    return bisector;
  }

  /** Adds two N-D vectors. */
  public static float[] add(float[] v1, float[] v2) {
    // v1 and v2 should have same lengths
    if (v1.length != v2.length) return null;
    int len = v1.length;
    float[] r = new float[v1.length];
    for (int i=0; i<v1.length; i++) {
      r[i] = v1[i] + v2[i];
    }
    return r;
  }

  /** Multiplies an N-D vector by a scalar. */
  public static float[] scalarMultiply(float[] v, float s) {
    int len = v.length;
    float[] r = new float[len];
    for (int i=0; i<len; i++) {
      r[i] = v[i] * s;
    }
    return r;
  }

}
