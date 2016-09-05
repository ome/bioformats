/*
 * #%L
 * OME Metakit package for reading Metakit database files.
 * %%
 * Copyright (C) 2011 - 2016 Open Microscopy Environment:
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

package ome.metakit;

/**
 * Class representing a column in a Metakit database file.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class Column {

  // -- Fields --

  private String name = null;
  private String typeString = null;

  // -- Constructors --

  /**
   * Construct a new column from the given string representation.
   *
   * The string representation should take the form name:type, e.g.
   * "fieldID:I".
   */
  public Column(String definition) {
    int separator = definition.indexOf(':');
    name = definition.substring(0, separator);
    typeString = definition.substring(separator + 1);
  }

  // -- Column API methods --

  /** Retrieve the name of the column. */
  public String getName() {
    return name;
  }

  /**
   * Retrieve a string representation of the type.
   * Valid values are "I", "F", "B", "S", "D", and "L".
   */
  public String getTypeString() {
    return typeString;
  }

  /** Retrieve the Class corresponding to the column's type. */
  public Class getType() {
    char typeChar = typeString.charAt(0);

    switch (typeChar) {
      case 'S':
        return String.class;
      case 'I':
        return Integer.class;
      case 'F':
        return Float.class;
      case 'D':
        return Double.class;
      case 'B':
        return Byte.class;
      case 'L':
        return Long.class;
    }
    return null;
  }

}
