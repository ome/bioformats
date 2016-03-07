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

// these are the macros that were originally defines
public class macros
{
  private static final int MDB_VER_JET3 = 0;
  private static final int MDB_VER_JET4 = 1;

  public static boolean IS_JET4(MdbHandle mdb)
  {
    return (mdb.f.jet_version == MDB_VER_JET4);
  }

  public static boolean IS_JET3(MdbHandle mdb)
  {
    return (mdb.f.jet_version == MDB_VER_JET3);
  }
}
