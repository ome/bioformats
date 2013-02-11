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

package mdbtools.libmdb;

import mdbtools.publicapi.RandomAccess;

import java.io.IOException;

public class MdbFile
{
  public RandomAccess fd;
  public boolean writable;
  public String filename;
  int jet_version;
  int db_key;
  public char[] db_passwd = new char[14];
  public MdbBackend default_backend;
  public String backend_name;
//  MdbStatistics	stats;
  /* free map */
  public int  map_sz;
//  unsigned char *free_map;
  /* reference count */
  public int refs;

  public void close() {
    try {
      fd.close();
    }
    catch (IOException e) { }
  }
}
