/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.formats.tiff;

/**
 * A rational number (numerator over denominator).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/tiff/TiffRational.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/tiff/TiffRational.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class TiffRational extends Number implements Comparable<TiffRational> {

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
  public double doubleValue() {
    return denom == 0 ? Double.MAX_VALUE : ((double) numer / (double) denom);
  }

  /** Returns the value of the specified number as a float. */
  public float floatValue() { return (float) doubleValue(); }

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
  public boolean equals(Object o) {
    return o != null && o instanceof TiffRational &&
      compareTo((TiffRational) o) == 0;
  }

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
  public int compareTo(TiffRational q) {
    long diff = (numer * q.denom - q.numer * denom);
    if (diff > Integer.MAX_VALUE) diff = Integer.MAX_VALUE;
    else if (diff < Integer.MIN_VALUE) diff = Integer.MIN_VALUE;
    return (int) diff;
  }

}
