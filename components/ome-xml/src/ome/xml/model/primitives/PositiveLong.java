//
// PositiveLong.java
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
 * and a further positive restriction.
 *
 * @author callan
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/PositiveLong.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/PositiveLong.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class PositiveLong extends NonNegativeLong {

  public PositiveLong(Long value) {
    super(value);
    if (value == null || value.longValue() < 1) {
      throw new IllegalArgumentException(
          value + " must not be null and positive.");
    }
  }

  /**
   * Returns an <code>PositiveLong</code> object holding the value of
   * the specified string.
   * @param s The string to be parsed.
   * @return See above.
   */
  public static PositiveLong valueOf(String s) {
    return new PositiveLong(Long.valueOf(s));
  }
}
