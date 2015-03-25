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

import mdbtools.dbengine.Data;
import mdbtools.dbengine.MemoryData;
import mdbtools.dbengine.functions.Function;
import mdbtools.dbengine.sql.FQColumn;
import mdbtools.dbengine.sql.FunctionDef;
import mdbtools.dbengine.sql.Select;

import java.sql.SQLException;

/**
 * resolve the columns, num rows stays same
 */
public class NonAggregateQuery implements Task
{
  private MemoryData result;
  private Task task;
  private Select sql;
  private int[] tableMap;

  public NonAggregateQuery(Task task, Select sql, int[] tableMap)
  {
    this.task = task;
    this.sql = sql;
    this.tableMap = tableMap;
  }

  public void run()
    throws SQLException
  {
    result = new MemoryData();
    Data data = (Data)task.getResult();
    int numColumns = sql.getColumnCount();
    while (data.next())
    {
      Object[] row = new Object[numColumns];
      for (int i = 0; i < numColumns; i++)
      {
        row[i] = resolveColumn(data,sql.getColumn(i));
      }
      result.addRow(row);
    }
  }

  public Object getResult()
  {
    return result;
  }

  private Object resolveColumn(Data data,Object column)
    throws SQLException
  {
    if (column instanceof FQColumn)
    {
      FQColumn c = (FQColumn)column;
      return data.get(tableMap[c.getTable()]+c.getColumn());
    }
    else if (column instanceof FunctionDef)
    {
      FunctionDef fdef = (FunctionDef)column;
      Function f = (Function)fdef.getFunction();
      Object argument = fdef.getArgument();
      return f.execute(resolveColumn(data,argument));
    }
    else if (column.getClass().isArray())
    {
      // resolve each element
      Object[] oa = (Object[])column;
      Object[] result = new Object[oa.length];
      for (int i = 0; i < result.length;i++)
      {
        result[i] = resolveColumn(data,oa[i]);
      }
      return result;
    }
    else
      return column;  // static value
  }
}
