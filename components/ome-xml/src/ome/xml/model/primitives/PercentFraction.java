//
// PercentFraction.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

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

package ome.xml.model.primitives;

/**
 * A floating point value whose constraints are bound to Java's 32-bit floating
 * point type and a further 0.0f minimum and 1.0f maximum.
 *
 * @author callan
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/PercentFraction.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/PercentFraction.java">SVN</a></dd></dl>
 */
public class PercentFraction extends PrimitiveType<Float> {

  public PercentFraction(Float value) {
    super(value);
    if (value == null || value.floatValue() < 0.0 || value.floatValue() > 1.0) {
      throw new IllegalArgumentException(
          value + " must not be null and between 0.0 and 1.0.");
    }
  }

  /**
   * Returns an <code>PercentFraction</code> object holding the value of
   * the specified string.
   * @param s The string to be parsed.
   * @return See above.
   */
  public static PercentFraction valueOf(String s) {
    return new PercentFraction(Float.valueOf(s));
  }
}
