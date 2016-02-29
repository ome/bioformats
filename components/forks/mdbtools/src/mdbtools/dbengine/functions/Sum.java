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

package mdbtools.dbengine.functions;

import java.sql.SQLException;

public class Sum implements Aggregate
{
  private int sum = -1;

  public void execute(Object column)
    throws SQLException
  {
    if (column != null)
    {
      if (column instanceof Integer == false)
        throw new RuntimeException("sum can only operate on integers");
      int value = ((Integer)column).intValue();
      if (sum == -1)
        sum = value;
      else
        sum += value;
    }
  }

  public Object getResult()
  {
    int result = sum;
    sum = -1;
    if (result == -1)
      return null;
    return new Integer(result);
  }
}
