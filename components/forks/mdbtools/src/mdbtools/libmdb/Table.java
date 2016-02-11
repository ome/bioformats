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
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class Table
{
  public static MdbTableDef mdb_read_table(MdbCatalogEntry entry)
    throws IOException
  {
    MdbTableDef table = null;
    MdbHandle mdb = entry.mdb;
    MdbFormatConstants fmt = mdb.fmt;
    int len;
    int rownum, row_start, row_end;

//    System.out.println("start of mdb_read_table");

    table = mem.mdb_alloc_tabledef(entry);

    file.mdb_read_pg(mdb, entry.table_pg);
    len = file.mdb_get_int16(mdb,8);

    table.num_rows = file.mdb_get_int32(mdb, fmt.tab_num_rows_offset);
    table.num_cols = file.mdb_get_int16(mdb, fmt.tab_num_cols_offset);
    table.num_idxs = file.mdb_get_int32(mdb, fmt.tab_num_idxs_offset);
    table.num_real_idxs = file.mdb_get_int32(mdb, fmt.tab_num_ridxs_offset);

    /* grab a copy of the usage map */
    rownum = mdb.pg_buf[fmt.tab_usage_map_offset];
//System.out.println("tab_usage_map_offset: " + fmt.tab_usage_map_offset);
//System.out.println("====alt offset: " + file.mdb_get_int24(mdb, fmt.tab_usage_map_offset + 1));
    file.mdb_read_alt_pg(mdb, file.mdb_get_int24(mdb, fmt.tab_usage_map_offset + 1));
    file.mdb_swap_pgbuf(mdb);
//int offset = ((fmt.row_count_offset + 2) + (rownum*2));
//System.out.println("====: offset: " + offset);
//System.out.println("[offset]: " + file.unsign(mdb.pg_buf[offset]));
//System.out.println("[offset+1]: " + file.unsign(mdb.pg_buf[offset+1]));
    row_start = file.mdb_get_int16(mdb, (fmt.row_count_offset + 2) + (rownum*2));
    row_end = Data.mdb_find_end_of_row(mdb, rownum);
    table.map_sz = row_end - row_start + 1;
    table.usage_map = new byte[table.map_sz];
//    System.out.println("==== row start: " + row_start);
//    System.out.println("====: " + mdb.pg_buf[row_start]);
    System.arraycopy(mdb.pg_buf,row_start,table.usage_map,0,table.map_sz);
    /* swap back */
    file.mdb_swap_pgbuf(mdb);
    table.first_data_pg = file.mdb_get_int16(mdb, fmt.tab_first_dpg_offset);

//System.out.println("end of mdb_read_table");
    return table;
  }

  public static ArrayList mdb_read_columns(MdbTableDef table)
    throws IOException
  {
    MdbHandle mdb = table.entry.mdb;
    MdbFormatConstants fmt = mdb.fmt;
    MdbColumn col, pcol;
    int len, i,j;
    byte low_byte, high_byte;
    int cur_col, cur_name;
    int name_sz;
    LinkedList  slist = new LinkedList();
    Holder pm = new Holder();

    table.columns = new ArrayList();

    cur_col = fmt.tab_cols_start_offset +
              (table.num_real_idxs * fmt.tab_ridx_entry_size);

    /* new code based on patch submitted by Tim Nelson 2000.09.27 */

    /*
    ** column attributes
    */
    for (i = 0 ;i < table.num_cols;i++)
    {
      col = new MdbColumn();
      col.col_num = mdb.pg_buf[cur_col + fmt.col_num_offset];

      pm.i = cur_col;
      read_pg_if(mdb, pm, 0);
      cur_col = pm.i;

      col.col_type = mdb.pg_buf[cur_col];

      if (col.col_type == Constants.MDB_NUMERIC)
      {
        col.col_prec = mdb.pg_buf[cur_col + 11];
        col.col_scale = mdb.pg_buf[cur_col + 12];
      }

      pm.i = cur_col;
      read_pg_if(mdb, pm, 13);
      cur_col = pm.i;
      col.is_fixed = (mdb.pg_buf[cur_col + fmt.col_fixed_offset] & 0x01) != 0 ? true : false;
      if (col.col_type != Constants.MDB_BOOL)
      {
        pm.i = cur_col;
        read_pg_if(mdb, pm, 17);
        cur_col = pm.i;

        low_byte = mdb.pg_buf[cur_col + fmt.col_size_offset];

        pm.i = cur_col;
        read_pg_if(mdb, pm, 18);
        cur_col = pm.i;

        high_byte = mdb.pg_buf[cur_col + fmt.col_size_offset + 1];
        col.col_size += file.unsign(high_byte) * 256 + file.unsign(low_byte);
//        col.col_size += file.unsign(low_byte) * 256 + file.unsign(high_byte);
      }
      else
        col.col_size=0;

      pcol = col;
//      pcol = (MdbColumn)col.clone();
//      System.out.println("add pcol: col_size: "+pcol.col_size);
//      slist = g_slist_insert_sorted(slist,pcol,(GCompareFunc)mdb_col_comparer);
      slist.add(pcol);  // +++ crs +++ just add will sort the whole thing later
      cur_col += fmt.tab_col_entry_size;
    }

    // +++ crs +++ sort the list === this is done so as to not
    // have to compare during the insert - how does java do that anyway?
    Collections.sort(slist,new Comparator()
      {
        public int compare(Object o1, Object o2)
        {
          MdbColumn a = (MdbColumn)o1;
          MdbColumn b = (MdbColumn)o2;

          if (a.col_num > b.col_num)
            return 1;
          else if (a.col_num < b.col_num)
            return -1;
          else
            return 0;
        }

        public boolean equals(Object obj)
        {
          throw new RuntimeException("equals is not implemented");
        }
      });

    cur_name = cur_col;

    /*
    ** column names
    */
    for (i = 0;i < table.num_cols;i++)
    {
      /* fetch the column */
      pcol = (MdbColumn)slist.get(i);

      /* we have reached the end of page */
      pm.i = cur_col;
      read_pg_if(mdb, pm, 0);
      cur_col = pm.i;

      name_sz = mdb.pg_buf[cur_name];

      if (macros.IS_JET4(mdb))
      {
//        if (cur_name < 0)
//          System.out.println("hi");
        /* FIX ME - for now just skip the high order byte */
        cur_name += 2;
        /* determine amount of name on this page */
        len = ((cur_name + name_sz) > fmt.pg_size) ?
        fmt.pg_size - cur_name :
        name_sz;

        /** @todo +++CRS+++ since the name is unicode and java is unicode
         *        support the unicode instead of stripping it
         */
        char[] name = new char[Constants.MDB_MAX_OBJ_NAME+1];
        int nameIndex = 0;
        /* strip high order (second) byte from unicode string */
        for (j = 0; j < len;j += 2)
          name[nameIndex++] = (char)mdb.pg_buf[cur_name + j];
        /* name wrapped over page */
        if (len < name_sz)
        {
//          /* read the next pg */
//          file.mdb_read_pg(mdb, file.mdb_get_int32(mdb,4));
//          cur_name = 8 - (fmt.pg_size - cur_name);
//          if (len % 2 != 0)
//            cur_name++;
//          /* get the rest of the name */
//          for (j = 0;j < (name_sz - len);j++)
//          {
//            if (cur_name < 0)
//              System.out.println("hi");
//            name[nameIndex++] = (char)mdb.pg_buf[cur_name+j];
//          }

                        file.mdb_read_pg(mdb, file.mdb_get_int32(mdb,4));
                        cur_name = 8 - (fmt.pg_size - cur_name);
                        /* get the rest of the name */
                        for (j=len;j<name_sz;j++)
                        {
                            if ((j%2)==0)
                              name[nameIndex++] = (char)file.unsign(mdb.pg_buf[cur_name + j]);
                        }

        }
        pcol.name = new String(name,0,nameIndex);

        cur_name += name_sz;
      }
      else if (macros.IS_JET3(mdb))
      {
        /* determine amount of name on this page */
        len = ((cur_name + name_sz) > fmt.pg_size) ? fmt.pg_size - cur_name : name_sz;

        if (len > 0)
        {
          pcol.name = new String(mdb.pg_buf,cur_name+1,len);
//            memcpy(pcol->name, &mdb->pg_buf[cur_name+1], len);
        }
        else
          pcol.name = "";
        /* name wrapped over page */
        if (len < name_sz)
        {
          /* read the next pg */
          file.mdb_read_pg(mdb, file.mdb_get_int32(mdb,4));
          cur_name = 8 - (fmt.pg_size - cur_name);
          /* get the rest of the name */
          int strlen = name_sz - len;
          if (cur_name < 0) cur_name = 0;
          if (strlen < 0) strlen = 0;
          if (strlen + cur_name >= mdb.pg_buf.length) {
            strlen = mdb.pg_buf.length - cur_name;
          }
          pcol.name += new String(mdb.pg_buf,cur_name,strlen);
//          memcpy(&pcol.name[len], &mdb->pg_buf[cur_name], name_sz - len);
        }
//        pcol->name[name_sz]='\0';

        cur_name += name_sz + 1;
      }
      else
      {
        throw new RuntimeException("Unknown MDB version");
      }
    }

    /* turn this list into an array */
    for (i = 0;i < table.num_cols;i++)
    {
      pcol = (MdbColumn)slist.get(i);
      table.columns.add(pcol);
    }

    table.index_start = cur_name;
    return table.columns;
  }

  /*
  ** read the next page if offset is > pg_size
  ** return true if page was read
  */
  private static int read_pg_if(MdbHandle mdb, Holder cur_pos, int offset)
    throws IOException
  {
    if (cur_pos.i + offset >= mdb.fmt.pg_size)
    {
      file.mdb_read_pg(mdb, file.mdb_get_int32(mdb,4));
      cur_pos.i = 8 - (mdb.fmt.pg_size - cur_pos.i);
      return 1;
    }
    return 0;
  }
}
