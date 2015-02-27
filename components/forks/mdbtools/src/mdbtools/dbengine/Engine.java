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

import mdbtools.dbengine.sql.Select;
import mdbtools.dbengine.sql.SQL;

import java.sql.SQLException;

/**
 * Engine is the entry point into a java based database backend
 * The datastore for the data is un-important since the engine communiates
 * with the data via a set of defined interfaces.
 * A sql parser is not included here instead the caller must parse the sql and
 * pass in a parse tree.
 *
 */
public class Engine
{
  public Engine()
  {
  }

  /**
   * execute is used to tell the engine to do something
   */
  public Data execute(SQL sql)
    throws SQLException
  {
    if (sql instanceof Select)
      return execute((Select)sql);
    return null;  // unknown query type
  }

  /**
   * Execute a sql select query
   * @param sql
   */
  private Data execute(Select select)
    throws SQLException
  {
    SelectEngine se = new SelectEngine(select);
    return se.execute();
  }
}
