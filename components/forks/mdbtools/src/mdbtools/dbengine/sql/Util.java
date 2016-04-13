/*
 * #%L
 * Fork of MDB Tools (Java port).
 * %%
 * Copyright (C) 2008 - 2016 Open Microscopy Environment:
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

package mdbtools.dbengine.sql;

/**
 * Helper methods
 */
public class Util
{
  /**
   * easy way to call toString(Select) (if needed) on any object
   * also quotes strings and expands out arrays
   * @param sql
   * @param o
   * @return string
   */
  public static String toString(Select sql,Object o)
  {
    String s;
    if (o instanceof Equation)
      s = ((Equation)o).toString(sql);
    else if (o instanceof Condition)
      s = ((Condition)o).toString(sql);
    else if (o instanceof String)
      s = "'" + (String)o + '"';
    else if (o instanceof Join)
      s = ((Join)o).toString(sql);
    else if (o instanceof FQColumn)
      s = ((FQColumn)o).toString(sql);
    else if (o instanceof FunctionDef)
      s = ((FunctionDef)o).toString(sql);
    else if (o instanceof OrderBy)
      s = ((OrderBy)o).toString(sql);
    else if (o.getClass().isArray())
    {
      Object[] oa = (Object[])o;
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < oa.length; i++)
      {
        if (i != 0)
          sb.append(',');
        sb.append(toString(sql,oa[i]));
      }
      s = sb.toString();
    }
    else
      s = o.toString();
    return s;
  }
}
