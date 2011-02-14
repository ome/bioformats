//
// Entity.java
//

/*
LOCI autogen package for programmatically generating source code.
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

import java.util.HashMap;

/**
 * An entity is an object that contains a bundle of {@link Property} objects.
 * It has a list of default attributes (key/value pairs),
 * as well as a list of version overrides for those attributes.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/autogen/src/Entity.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/autogen/src/Entity.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see {EntityList}
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class Entity {

  // -- Fields --

  /** The entity's attributes. */
  protected HashMap<String, String> attrs;

  /** The entity's properties. */
  protected HashMap<String, Property> props =
    new HashMap<String, Property>();

  /** The entity's version overrides. */
  protected HashMap<String, HashMap<String, String>> versions =
    new HashMap<String, HashMap<String, String>>();

  // -- Constructor --

  /** Creates a new entity with the given attributes. */
  public Entity(HashMap<String, String> attrs) {
    this.attrs = attrs;
  }

}
