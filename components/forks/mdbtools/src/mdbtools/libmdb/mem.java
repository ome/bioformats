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

package mdbtools.libmdb;

import java.io.IOException;
import java.util.ArrayList;

public class mem
{
  public static void mdb_init()
  {
    backend.mdb_init_backends();
  }

  public static MdbHandle mdb_alloc_handle()
  {
    MdbHandle mdb;

    mdb = new MdbHandle();
    backend.mdb_set_default_backend(mdb, "access");

    return mdb;
  }

  public static MdbFile mdb_alloc_file()
  {
    MdbFile f;

    f = new MdbFile();

    return f;
  }

  public static void mdb_free_catalog(MdbHandle mdb)
  {
    //g_ptr_array_free(mdb->catalog, FALSE);
    mdb.catalog = null;
  }

  public static void mdb_alloc_catalog(MdbHandle mdb)
  {
    mdb.catalog = new ArrayList();
  }

  public static MdbTableDef mdb_alloc_tabledef(MdbCatalogEntry entry)
  {
    MdbTableDef table;

    table = new MdbTableDef();
    table.entry = entry;
    table.name = entry.object_name;

    return table;
  }

  public static void mdb_free_handle(MdbHandle mdb)
    throws java.io.IOException
  {
    if (mdb == null)
      return;
//System.out.println("mdb_free_handle called ");
//    if (mdb->stats) mdb_free_stats(mdb);
//    if (mdb->catalog) mdb_free_catalog(mdb);
//System.out.println("refs: " + mdb.f.refs);
//    if (mdb.f != null && mdb.f.refs <= 0)
      mdb_free_file(mdb.f);
//    if (mdb->backend_name) free(mdb->backend_name);
//    free(mdb);
  }

  private static void mdb_free_file(MdbFile f)
    throws java.io.IOException
  {
//System.out.println("mdb_free_file called");
    if (f == null)
      return;
    if (f.fd != null)
    {
//      System.out.println("closing file");
      f.fd.close();
//      System.out.println("file closed");
    }
  }
}
