/*
 * #%L
 * Fork of MDB Tools (Java port).
 * %%
 * Copyright (C) 2008 - 2013 Open Microscopy Environment:
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
import mdbtools.dbengine.Table;

import java.sql.SQLException;

/**
 * Load any data from a table into data
 */
public class LoadData implements Task
{
  private Table table;
  private MemoryData result;

  public LoadData(Table table)
  {
    this.table = table;
  }

  public void run()
    throws SQLException
  {
    result = new MemoryData(table.getData(),table.getColumnCount());
  }

  public Object getResult()
  {
    return result;
  }
}
