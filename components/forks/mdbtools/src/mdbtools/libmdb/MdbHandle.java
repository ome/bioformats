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

package mdbtools.libmdb;

import java.util.ArrayList;

public class MdbHandle
{
  public static final int MDB_PGSIZE = 4096;

  public MdbFile f;
  public short cur_pg;
  public int row_num;
  public long  cur_pos;
  public byte[] pg_buf = new byte[MDB_PGSIZE];
  public byte[] alt_pg_buf = new byte[MDB_PGSIZE];
  public int num_catalog;
  public ArrayList catalog;
  public MdbBackend default_backend;
  public String backend_name;
  public MdbFormatConstants fmt;
  public MdbStatistics stats;

  public void close() {
    f.close();
  }
}
