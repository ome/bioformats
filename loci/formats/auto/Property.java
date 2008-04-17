//
// Property.java
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

package loci.formats.auto;

import java.util.Hashtable;

/**
 * A property is an object that belongs to a particular {@link Entity}.
 * It has a list of default attributes (key/value pairs),
 * as well as a list of version overrides for those attributes.
 *
 * @see {EntityList}
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/auto/Property.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/auto/Property.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class Property {

  // -- Fields --

  /** The property's attributes. */
  protected Hashtable<String, String> attrs;

  /** The property's version overrides. */
  protected Hashtable<String, Hashtable<String, String>> versions =
    new Hashtable<String, Hashtable<String, String>>();

  // -- Constructor --

  /** Creates a new property with the given attributes. */
  public Property(Hashtable<String, String> attrs) {
    this.attrs = attrs;
  }

}
