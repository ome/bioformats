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

package mdbtools.tests;

import mdbtools.libmdb.*;

public class ColumnTest
{
  public static void main(String[] args)
  {
    (new ColumnTest()).go();
  }

  private void go()
  {
    try
    {
      // initialize the library
      mem.mdb_init();

      // open the database
      String filename = "/home/melissa/data/cellr/colin/07-06-21_d.mtb";
      MdbHandle mdb = null; // file.mdb_open(filename);
      // read the catalog
      Catalog.mdb_read_catalog(mdb,Constants.MDB_TABLE);
      // find our table
      MdbTableDef table = null;
      for (int i=0; i < mdb.num_catalog; i++)
      {
        MdbCatalogEntry entry = (MdbCatalogEntry)mdb.catalog.get(i);
        /* is this our table */
        if (entry.object_type == Constants.MDB_TABLE &&
            entry.object_name.equals(tableName))
        {
          table = Table.mdb_read_table(entry);
          break;
        }
      }
      if (table == null)
        throw new RuntimeException("table not found");
      Table.mdb_read_columns(table);
      Data.mdb_rewind_table(table);

      // check the column names
      if (table.columns.size() != columns.length)
        throw new RuntimeException("wrong number of columns");
      for (int i = 0;i < table.num_cols;i++)
      {
        MdbColumn col = (MdbColumn)table.columns.get(i);
        if (findColumnName(col.name) == false)
          throw new RuntimeException("column name " + col.name + " not found");
      }
      System.out.println("column names check out");

      // check the column types
      for (int i = 0;i < table.num_cols;i++)
      {
        MdbColumn col = (MdbColumn)table.columns.get(i);
        String col_type = backend.mdb_get_coltype_string(mdb.default_backend, col.col_type);
        String real_type = findColumnType(col.name,col_type);
        if (real_type.equals(col_type) == false)
          throw new RuntimeException("column " + col.name + " should have a " +
                                     "type of " + real_type + " " +
                                     "instead it's " + col_type);
      }
      System.out.println("column types check out");

      // check the data as string
      Holder[] bound_values = new Holder[table.num_cols];
      for (int i = 0; i < table.num_cols;i++)
      {
        bound_values[i] = new Holder();
        Data.mdb_bind_column(table, i + 1, bound_values[i]);
      }
      int row = 0;
      while(Data.mdb_fetch_row(table))
      {
        for (int i = 0; i < table.num_cols;i++)
        {
          MdbColumn col = (MdbColumn)table.columns.get(i);
          String value = findStringValue(row,col.name);
          String foundValue;
          if (col.col_type == Constants.MDB_OLE)
          {
            // ole still needs more work
            continue;
//            foundValue = toHex(bound_values[i].ba);
          }
          else if (col.name.equals("aReplicationId"))
            continue;  // replication needs more work
          else
            foundValue = bound_values[i].s;
          if (value.equals(foundValue) == false)
            throw new RuntimeException("wrong data for row: " + i +
                                       " column: " + col.name +
                                       " found " + bound_values[i].s +
                                       " should be: " + value);
        }
        row++;
        System.out.println("first row worked");
        break;
      }
      System.out.println("string data checks out");
      System.out.println("done");
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  private boolean findColumnName(String name)
  {
    for (int i = 0; i < columns.length; i++)
      if (columns[i][0].equals(name))
        return true;
    return false;
  }

  private String findColumnType(String name,String type)
  {
    for (int i = 0; i < columns.length; i++)
      if (columns[i][0].equals(name))
        return columns[i][1];
    throw new RuntimeException("column " + name + " not found");
  }

  private String findStringValue(int row,String name)
  {
    for (int i = 0; i < columns.length; i++)
      if (columns[i][0].equals(name))
        return dataAsString[row][i];
    throw new RuntimeException("column " + name + " not found");
  }

  // convert a string to a hex value
  private String toHex(byte[] ba)
  {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < ba.length; i++)
    {
      sb.append(Integer.toHexString(file.unsign(ba[i])));
    }
    return sb.toString();
  }

  private static final String[][] columns =
    { {"aText","Text"},
      {"aMemo","Memo/Hyperlink"},
      {"aLongInteger","Long Integer"},
      {"aDateTime","DateTime (Short)"},
      {"aCurrency","Currency"},
      {"aAutoNumber","Long Integer"},
      {"aYesNo","Boolean"},
      {"aOleObject","OLE"},
      {"aHyperlink","Memo/Hyperlink"},
      {"aDouble","Double"},
      {"aByte","Byte"},
      {"aInteger","Integer"},
      {"aSingle","Single"},
      {"aReplicationId","Replication ID"},
      {"aDecimal","Decimal"}
    };

  private static final String tableName = "aTable";

  private static final String[][] dataAsString = new String[][]
    {
      {
        "My Text", // aText
        "This is a story about a boy who lived a very long life",
        "892",
        "2/8/2005 0:0:0",
        "8.9800",  // aCurrency
        "1",
        "0",
        "+++ole+++",
        new String(new byte[]{-3,-3})+"My Link#http://www.yahoo.com#", // aHyperlink
        "23.4456",
        "253",
        "23432",
        "677.333",
        "+++replication+++",
        "2143"
      }
    };
}
