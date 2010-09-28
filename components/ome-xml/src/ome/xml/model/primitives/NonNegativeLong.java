//
// NonNegativeLong.java
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
 * An integer whose constraints are bound to Java's 64-bit signed integer type
 * and a further non-negative restriction.
 *
 * @author callan
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://dev.loci.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/NonNegativeLong.java">Trac</a>,
 * <a href="http://dev.loci.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/NonNegativeLong.java">SVN</a></dd></dl>
 */
public class NonNegativeLong extends PrimitiveType<Long> {

  public NonNegativeLong(Long value) {
    super(value);
    if (value == null || value.longValue() < 0) {
      throw new IllegalArgumentException(
          value + " must not be null or non-negative.");
    }
  }

  /**
   * Returns an <code>NonNegativeLong</code> object holding the value of
   * the specified string.
   * @param s The string to be parsed.
   * @return See above.
   */
  public static NonNegativeLong valueOf(String s) {
    return new NonNegativeLong(Long.valueOf(s));
  }
}
