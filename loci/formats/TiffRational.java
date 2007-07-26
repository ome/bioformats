//
// TiffRational.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats;

/**
 * A rational number (numerator over denominator).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/TiffRational.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/TiffRational.java">SVN</a></dd></dl>
 */
public class TiffRational extends Number implements Comparable {

  // -- Fields --

  /** Components of the rational's fractional representation. */
  protected long numer, denom;

  // -- Constructor --

  /** Constructs a rational number. */
  public TiffRational(long numer, long denom) {
    this.numer = numer;
    this.denom = denom;
  }

  // -- TiffRational API methods --

  /** Gets this number's numerator. */
  public long getNumerator() { return numer; }

  /** Gets this number's denominator. */
  public long getDenominator() { return denom; }

  /** Reduces this rational's fraction to lowest terms. */
  public void reduce() {
    long sqrt1 = (long) Math.sqrt(numer);
    long sqrt2 = (long) Math.sqrt(denom);
    long gcdMax = sqrt1 < sqrt2 ? sqrt1 : sqrt2;
    // search for greatest common divisor
    for (long i=gcdMax; i>=2; i--) {
      if (numer % i == 0 && denom % i == 0) {
        numer /= i;
        denom /= i;
        reduce();
        break;
      }
    }
  }

  // -- Number API methods --

  /** Returns the value of the specified number as a byte. */
  public byte byteValue() { return (byte) longValue(); }

  /** Returns the value of the specified number as a double. */
  public double doubleValue() { return (double) longValue(); }

  /** Returns the value of the specified number as a float. */
  public float floatValue() { return (float) longValue(); }

  /** Returns the value of the specified number as an int. */
  public int intValue() { return (int) longValue(); }

  /** Returns the value of the specified number as a long. */
  public long longValue() {
    return denom == 0 ? Long.MAX_VALUE : (numer / denom);
  }

  /** Returns the value of the specified number as a short. */
  public short shortValue() { return (short) longValue(); }

  // -- Object API methods --

  /** Indicates whether some other object is "equal to" this one. */
  public boolean equals(Object o) { return compareTo(o) == 0; }

  /** Reasonable hash value for use with hashtables. */
  public int hashCode() { return (int) (numer - denom); }

  /** Returns a string representation of the object. */
  public String toString() { return numer + "/" + denom; }

  // -- Comparable API methods --

  /**
   * Compares this object with the specified object for order.
   * Returns a negative integer, zero, or a positive integer as this object
   * is less than, equal to, or greater than the specified object.
   */
  public int compareTo(Object o) {
    TiffRational q = (TiffRational) o;
    long diff = (numer * q.denom - q.numer * denom);
    if (diff > Integer.MAX_VALUE) diff = Integer.MAX_VALUE;
    else if (diff < Integer.MIN_VALUE) diff = Integer.MIN_VALUE;
    return (int) diff;
  }

}
