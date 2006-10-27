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
   * @param v Coordinates of the standalone endpoint
   * @param segment Whether distance computation should be constrained to the given line segment
   *
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
   *
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
   * Computes the projection of the point v onto the line segment a-b.
   *
   * @param a Coordinates of the segment's first endpoint
   * @param b Coordinates of the segment's second endpoint
   * @param v Coordinates of the point to be projected 
   * @param segment Whether the projection should be constrained to the given line segment 
   *
   */

  public static double[] getProjection (double[] a, double[] b, double[] v, boolean segment) 
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

  /**
   * Computes a unique 1-D index corresponding to the multidimensional
   * position given in the pos array, using the specified lengths array
   * as the maximum value at each positional dimension.
   */
  public static int positionToRaster(int[] lengths, int[] pos) {
    int[] offsets = new int[lengths.length];
    if (offsets.length > 0) offsets[0] = 1;
    for (int i=1; i<offsets.length; i++) {
      offsets[i] = offsets[i - 1] * lengths[i - 1];
    }
    int raster = 0;
    for (int i=0; i<pos.length; i++) raster += offsets[i] * pos[i];
    return raster;
  }

  /**
   * Computes a unique 3-D position corresponding to the given raster
   * value, using the specified lengths array as the maximum value at
   * each positional dimension.
   */
  public static int[] rasterToPosition(int[] lengths, int raster) {
    int[] offsets = new int[lengths.length];
    if (offsets.length > 0) offsets[0] = 1;
    for (int i=1; i<offsets.length; i++) {
      offsets[i] = offsets[i - 1] * lengths[i - 1];
    }
    int[] pos = new int[lengths.length];
    for (int i=0; i<pos.length; i++) {
      int q = i < pos.length - 1 ? raster % offsets[i + 1] : raster;
      pos[i] = q / offsets[i];
      raster -= q;
    }
    return pos;
  }

  /**
   * Computes the maximum raster value of a positional array with
   * the given maximum values.
   */
  public static int getRasterLength(int[] lengths) {
    int len = 1;
    for (int i=0; i<lengths.length; i++) len *= lengths[i];
    return len;
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

}
