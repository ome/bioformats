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

package mdbtools.dbengine.sql;

import mdbtools.dbengine.Table;

import java.util.ArrayList;

/**
 * Select is the parse tree for a select sql statement.
 *
 * A simple select is handled by:
 *   converting a from clause to Data
 *   applying the where clause
 *   returning Data as the result
 *
 * In the case of an aggregate the above is done to get the data
 * then the aggregate functions are exectuted to filter the data
 */
public class Select implements SQL
{
  private ArrayList columns = new ArrayList();
  private ArrayList tables = new ArrayList();
  private Object where;
  private ArrayList groupBy = new ArrayList(); // list of FQColumn
  private ArrayList orderBy = new ArrayList();

  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append("SELECT ");
    for (int i = 0; i < columns.size(); i++)
    {
      if (i != 0)
        sb.append(',');
      sb.append(Util.toString(this,columns.get(i)));
    }
    sb.append(" FROM ");
    for (int i = 0; i < tables.size(); i++)
    {
      if (i != 0)
        sb.append(',');
      sb.append(Util.toString(this,tables.get(i)));
    }
    if (where != null)
    {
      sb.append(" WHERE ");
      sb.append(Util.toString(this,where));
    }
    if (groupBy.size() != 0)
    {
      sb.append(" GROUP BY ");
      for (int i = 0; i < groupBy.size(); i++)
      {
        if (i != 0)
          sb.append(',');
        sb.append(Util.toString(this,groupBy.get(i)));
      }
    }
    if (orderBy.size() != 0)
    {
      sb.append(" ORDER BY ");
      for (int i = 0; i < orderBy.size(); i++)
      {
        if (i != 0)
          sb.append(',');
        sb.append(Util.toString(this,orderBy.get(i)));
      }
    }
    return sb.toString();
  }

  public int getColumnCount()
  {
    return columns.size();
  }

  public Object getColumn(int index)
  {
    return columns.get(index);
  }

  public int getTableCount()
  {
    return tables.size();
  }

  public Object getTable(int index)
  {
    return tables.get(index);
  }

  public void addColumn(Object column)
  {
    columns.add(column);
  }

  public void addTable(Object table)
  {
    tables.add(table);
  }

  public void addGroupBy(FQColumn column)
  {
    groupBy.add(column);
  }

  public int getGroupByCount()
  {
    return groupBy.size();
  }

  public Object getGroupBy(int index)
  {
    return groupBy.get(index);
  }

  public void addOrderBy(OrderBy o)
  {
    orderBy.add(o);
  }

  public int getOrderByCount()
  {
    return orderBy.size();
  }

  public OrderBy getOrderBy(int index)
  {
    return (OrderBy)orderBy.get(index);
  }

  public void setWhere(Object where)
  {
    this.where = where;
  }

  public Object getWhere()
  {
    return where;
  }

  Table resolveTable(int index)
  {
    int numTables = -1;
    for (int i = 0; i < getTableCount(); i++)
    {
      Object table = getTable(i);
      if (table instanceof Table)
      {
        numTables++;
        if (numTables == index)
          return (Table)table;
      }
      else if (table instanceof Join)
      {
        Object o = table;
        while (o instanceof Join)
        {
          Join join = (Join)o;
          o = join.getLeft();
          if (o instanceof Table)
          {
            numTables++;
            if (numTables == index)
              return (Table)o;
            o = join.getRight();
          }
        }
        numTables++;
        if (numTables == index)
          return (Table)o;
      }
      else
        throw new RuntimeException("unknown table type");
    }
    throw new RuntimeException("can't resolve table");
  }
}
