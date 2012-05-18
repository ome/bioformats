//
// PrimitiveType.java
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
 * A primitive type from an XSD definition with a given set of constraints.
 *
 * @author callan
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/PrimitiveType.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/PrimitiveType.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public abstract class PrimitiveType<T> {

  /** The delegate value. */
  T value;

  /**
   * Default constructor.
   * @param value The delegate value to use.
   */
  PrimitiveType(T value) {
    this.value = value;
  }

  /**
   * Default constructor.
   * @param value The delegate value to use.
   */
  PrimitiveType() {
  }

  /**
   * Retrieves the concrete delegate value.
   * @return See above.
   */
  public T getValue() {
    return value;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return value.toString();
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof PrimitiveType<?>) {
      return value.equals(((PrimitiveType<?>) obj).getValue());
    }
    return value.equals(obj);
  }
}
