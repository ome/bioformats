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

public class FQColumn
{
  private int table;
  private int column;

  public FQColumn(int table, int column)
  {
    this.table = table;
    this.column = column;
  }

  public boolean equals(Object o)
  {
    if (o instanceof FQColumn)
    {
      FQColumn fq = (FQColumn)o;
      return table == fq.table &&
             column == fq.column;
    }
    else
      return false;
  }

  public String toString()
  {
    return table + "." + column;
  }

  public String toString(Select sql)
  {
    Table t = sql.resolveTable(table);
    return t.getName() + "." + t.getColumnName(column);
  }

  public int getColumn()
  {
    return column;
  }

  public int getTable()
  {
    return table;
  }

  public void setColumn(int column)
  {
    this.column = column;
  }

  public void setTable(int table)
  {
    this.table = table;
  }
}
