/*
 * #%L
 * Fork of MDB Tools (Java port).
 * %%
 * Copyright (C) 2008 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

package mdbtools.dbengine;

/**
 * Compare the values from a column in two rows
 */
public class ColumnComparator
{
/*
  public static int compare(Object c1,Object c2)
  {
    if (c1 instanceof Comparable == false || c2 )
      throw new SQLException("argument to max must implement comparable");
    if (c1 instanceof String)
    {
      String s1 = (String)c1;
      String s2 = (String)c2;
      return s1.compareTo(s2);
    }
    else if (c1 instanceof Integer)
    {
      Integer i1 = (Integer)c1;
      Integer i2 = (Integer)c2;
      if (i1.intValue() == i2.intValue())
        return 0;
      else if (i1.intValue() < i2.intValue())
        return -1;
      else
        return 1;  // i1 > i2
    }
    else
      throw new RuntimeException("unknown column type: " + c1.getClass().getName());
  }
*/
}
