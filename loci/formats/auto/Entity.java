//
// Entity.java
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
 * An entity is an object that contains a bundle of {@link Property} objects.
 * It has a list of default attributes (key/value pairs),
 * as well as a list of version overrides for those attributes.
 *
 * @see {EntityList}
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/auto/Entity.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/auto/Entity.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class Entity {

  // -- Fields --

  /** The entity's attributes. */
  protected Hashtable<String, String> attrs;

  /** The entity's properties. */
  protected Hashtable<String, Property> props =
    new Hashtable<String, Property>();

  /** The entity's version overrides. */
  protected Hashtable<String, Hashtable<String, String>> versions =
    new Hashtable<String, Hashtable<String, String>>();

  // -- Constructor --

  /** Creates a new entity with the given attributes. */
  public Entity(Hashtable<String, String> attrs) {
    this.attrs = attrs;
  }

}
