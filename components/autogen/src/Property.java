/*
 * #%L
 * Bio-Formats autogen package for programmatically generating source code.
 * %%
 * Copyright (C) 2007 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

import java.util.HashMap;

/**
 * A property is an object that belongs to a particular {@link Entity}.
 * It has a list of default attributes (key/value pairs),
 * as well as a list of version overrides for those attributes.
 *
 * @see {EntityList}
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class Property {

  // -- Fields --

  /** The property's attributes. */
  protected HashMap<String, String> attrs;

  /** The property's version overrides. */
  protected HashMap<String, HashMap<String, String>> versions =
    new HashMap<String, HashMap<String, String>>();

  // -- Constructor --

  /** Creates a new property with the given attributes. */
  public Property(HashMap<String, String> attrs) {
    this.attrs = attrs;
  }

}
