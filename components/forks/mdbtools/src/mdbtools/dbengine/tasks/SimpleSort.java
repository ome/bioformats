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

package mdbtools.dbengine.tasks;

import mdbtools.dbengine.MemoryData;
import mdbtools.dbengine.sql.FQColumn;
import mdbtools.dbengine.sql.OrderBy;

import java.sql.SQLException;

/**
 * Sort the given data by the given columns
 */
public class SimpleSort implements Task
{
  private MemoryData result;
  private Task task;
  private OrderBy[] sortBy;
  private int[] tableMap;

  public SimpleSort(Task task, OrderBy[] sortBy, int[] tableMap)
    throws SQLException
  {
    this.task = task;
    this.sortBy = sortBy;
    this.tableMap = tableMap;
  }

  public void run()
    throws SQLException
  {
    MemoryData data = (MemoryData)task.getResult();
//((MemoryData)data).dump();

    int[] sb = new int[sortBy.length];
    boolean[] ascending = new boolean[sb.length];
    for (int i = 0; i < sortBy.length; i++)
    {
      sb[i] = getColumnToSort(sortBy[i].getSort());
      ascending[i] = sortBy[i].isAscending();
    }

    ((MemoryData)data).sort(sb,ascending);
//((MemoryData)data).dump();
    result = data;
  }

  public Object getResult()
  {
    return result;
  }

  private int getColumnToSort(Object column)
    throws SQLException
  {
    if (column instanceof Integer)
      return ((Integer)column).intValue() - 1;
    else if (column instanceof FQColumn)
    {
      FQColumn c = (FQColumn)column;
      return tableMap[c.getTable()]+c.getColumn();
    }
    else
      throw new SQLException("I don't know how to sort this");
  }
}
