//
// Color.java
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
 * Primitive type that represents an RGBA color.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/ome-xml/src/ome/xml/model/primitives/Color.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/ome-xml/src/ome/xml/model/primitives/Color.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class Color extends PrimitiveType<Integer> {

  public Color(Integer value) {
    super(value);
  }

  public Color(int r, int g, int b, int a) {
    this(r << 24 | g << 16 | b << 8 | a);
  }

  /**
   * Returns an <code>Color</code> object holding the value of
   * the specified string.
   * @param s The string to be parsed.
   * @return See above.
   */
  public static Color valueOf(String s) {
    return new Color(Integer.valueOf(s));
  }

  /**
   * Returns the red component of this color.
   *
   * @return See above.
   */
  public int getRed() {
    return (getValue() >> 24) & 0xff;
  }

  /**
   * Returns the green component of this color.
   *
   * @return See above.
   */
  public int getGreen() {
    return (getValue() >> 16) & 0xff;
  }

  /**
   * Returns the blue component of this color.
   *
   * @return See above.
   */
  public int getBlue() {
    return (getValue() >> 8) & 0xff;
  }

  /**
   * Returns the alpha component of this color.
   *
   * @return See above.
   */
  public int getAlpha() {
    return getValue() & 0xff;
  }

}
