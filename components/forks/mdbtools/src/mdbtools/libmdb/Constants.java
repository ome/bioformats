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

/**
 * copied from mdbtools.h
 */
public class Constants
{
  public static final int MDB_FORM = 0;
  public static final int MDB_TABLE = 1;
  public static final int MDB_MACRO = 2;
  public static final int MDB_SYSTEM_TABLE = 3;
  public static final int MDB_REPORT = 4;
  public static final int MDB_QUERY = 5;
  public static final int MDB_LINKED_TABLE = 6;
  public static final int MDB_MODULE = 7;
  public static final int MDB_RELATIONSHIP = 8;
  public static final int MDB_UNKNOWN_09 = 9;
  public static final int MDB_UNKNOWN_0A = 10;
  public static final int MDB_DATABASE_PROPERTY = 11;
  public static final int MDB_ANY = -1;

  public static final int MDB_BOOL = 0x01;
  public static final int MDB_BYTE = 0x02;
  public static final int MDB_INT = 0x03;
  public static final int MDB_LONGINT = 0x04;
  public static final int MDB_MONEY = 0x05;
  public static final int MDB_FLOAT = 0x06;
  public static final int MDB_DOUBLE = 0x07;
  public static final int MDB_SDATETIME = 0x08;
  public static final int MDB_TEXT = 0x0a;
  public static final int MDB_OLE = 0x0b;
  public static final int MDB_MEMO = 0x0c;
  public static final int MDB_REPID = 0x0f;
  public static final int MDB_NUMERIC = 0x10;

  public static final int MDB_EQUAL = 1;
  public static final int MDB_GT = 2;
  public static final int MDB_LT = 3;
  public static final int MDB_GTEQ = 4;
  public static final int MDB_LTEQ = 5;
  public static final int MDB_LIKE = 6;
  public static final int MDB_ISNULL = 7;
  public static final int MDB_NOTNULL = 8;

  public static final int MDB_PGSIZE = 4096;
  public static final int MDB_MAX_OBJ_NAME = 30;
  public static final int MDB_MAX_COLS = 256;
  public static final int MDB_MAX_IDX_COLS = 10;
  public static final int MDB_CATALOG_PG = 18;
  public static final int MDB_MEMO_OVERHEAD = 12;
  public static final int MDB_BIND_SIZE = 16384;
}
