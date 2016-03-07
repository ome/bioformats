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

public class Data
{
  public static int mdb_find_end_of_row(MdbHandle mdb, int row)
  {
    MdbFormatConstants fmt = mdb.fmt;
    int row_end;

    /*
    * Search the previous "row start" values for the first non-deleted one.
    * If we don't find one, then the end of the page is the correct value.
    */
//   System.out.println("inside mdb_find_end_of_row");
    if (row==0)
    {
      row_end = fmt.pg_size - 1;
    }
    else
    {
//      System.out.println("inside mdb_find_end_of_row else");
      int offset = ((fmt.row_count_offset + 2) + (row - 1) * 2);
//      System.out.println("["+offset+"]: "+file.unsign(mdb.pg_buf[offset])+", ["+(offset+1)+"]:"+file.unsign(mdb.pg_buf[offset+1]));
      int ij = file.mdb_get_int16(mdb, offset);
      row_end = (ij & 0x0FFF) - 1;
//      System.out.println("offset: "+offset+",ij: "+ij+",row_end: "+row_end);
//      row_end = (file.mdb_get_int16(mdb, ((fmt.row_count_offset + 2) + (row - 1) * 2)) & 0x0FFF) - 1;
    }
    return row_end;
  }

  public static void mdb_bind_column(MdbTableDef table, int col_num, Holder bind_ptr)
  {
    MdbColumn col;

    /*
     * the column arrary is 0 based, so decrement to get 1 based parameter
     */
    col = (MdbColumn)table.columns.get(col_num - 1);
    col.bind_ptr = bind_ptr;
  }

  public static int mdb_rewind_table(MdbTableDef table)
  {
    table.cur_pg_num = 0;
    table.cur_phys_pg = 0;
    table.cur_row = 0;

    return 0;
  }

  public static boolean mdb_fetch_row(MdbTableDef table)
    throws IOException
  {
//System.out.println("enter: mdb_fetch_row");
    MdbHandle mdb = table.entry.mdb;
    MdbFormatConstants fmt = mdb.fmt;
    int rows;
    int rc;

    if (table.num_rows == 0)
      return false;

    /* initialize */
    if (table.cur_pg_num == 0)
    {
      table.cur_pg_num=1;
      table.cur_row=0;
      mdb_read_next_dpg(table);
    }

    do
    {
      rows = file.mdb_get_int16(mdb,fmt.row_count_offset);
//System.out.println(rows);
      /* if at end of page, find a new page */
      if (table.cur_row >= rows)
      {
        table.cur_row = 0;

        if (mdb_read_next_dpg(table) == 0)
        {
//System.out.println("die early");
          return false;
        }
      }

      /* printf("page %d row %d\n",table->cur_phys_pg, table->cur_row); */
      rc = mdb_read_row(table, table.cur_row);
//System.out.println("rc: " + rc);
      table.cur_row++;
    } while (rc == 0);

    return true;
  }

  public static int mdb_read_next_dpg_by_map0(MdbTableDef table)
    throws IOException
  {
    MdbCatalogEntry entry = table.entry;
    MdbHandle mdb = entry.mdb;
    int pgnum, i, bitn;

    pgnum = file._mdb_get_int32(table.usage_map,1);
    /* the first 5 bytes of the usage map mean something */
    for (i=5;i<table.map_sz;i++)
    {
      for (bitn=0;bitn<8;bitn++)
      {
        if ( ((table.usage_map[i] & 1 << bitn)!=0) && (pgnum > table.cur_phys_pg) )
        {
          table.cur_phys_pg = pgnum;
          if (file.mdb_read_pg(mdb, pgnum) == 0)
          {
            return 0;
          }
          else
          {
            return pgnum;
          }
        }
        pgnum++;
      }
    }
    /* didn't find anything */
    return 0;
  }

  public static int mdb_read_next_dpg(MdbTableDef table)
    throws IOException
  {
    MdbCatalogEntry entry = table.entry;
    MdbHandle mdb = entry.mdb;
    int map_type;

//  #ifndef SLOW_READ
//System.out.println("==inside of slow_read");
          map_type = table.usage_map[0];
          if (map_type==0)
          {
//System.out.println("==0");
                  return mdb_read_next_dpg_by_map0(table);
          }
          else if (map_type==1)
          {
//System.out.println("==1");
                  return mdb_read_next_dpg_by_map1(table);
          }
          else
          {
            // +++crs++ if this is thrown then the code below needs to be run
            throw new RuntimeException("Warning: unrecognized usage map type: "+table.usage_map[0]+", defaulting to brute force read\n");
          }
//  #endif
          /* can't do a fast read, go back to the old way */
//    do
//    {
//      if (file.mdb_read_pg(mdb, table.cur_phys_pg++) != 0)
//        return 0;
//    } while (mdb.pg_buf[0]!=0x01 || file.mdb_get_int32(mdb, 4)!=entry.table_pg);
//    /* fprintf(stderr,"returning new page %ld\n", table->cur_phys_pg);  */
//    return table.cur_phys_pg;
  }

  public static int mdb_read_row(MdbTableDef table, int row)
    throws IOException
  {
//System.out.println("enter: mdb_read_row");
    MdbHandle mdb = table.entry.mdb;
    MdbFormatConstants fmt = mdb.fmt;
    MdbColumn col;
    int i, j, rc;
    int num_cols, var_cols, fixed_cols;
    int row_start, row_end;
    int fixed_cols_found, var_cols_found;
    int col_start, len, next_col;
    int num_of_jumps=0, jumps_used=0;
    int eod; /* end of data */
    int delflag, lookupflag;
    int bitmask_sz;
    int col_ptr, deleted_columns=0;
    byte[] null_mask = new byte[33]; /* 256 columns max / 8 bits per byte */
    boolean isnull;

    row_start = file.mdb_get_int16(mdb, (fmt.row_count_offset + 2) + (row*2));
//System.out.println("row_start: " + row_start);
    row_end = mdb_find_end_of_row(mdb, row);

//System.out.println("row: " + row);
//System.out.println("row_end: "+row_end);

    delflag = lookupflag = 0;
    if ( (row_start & 0x8000) != 0) lookupflag++;
    if ( (row_start & 0x4000) != 0) delflag++;
    row_start &= 0x0FFF; /* remove flags */
/*
  #if MDB_DEBUG
          fprintf(stdout,"Row %d bytes %d to %d %s %s\n",
                  row, row_start, row_end,
                  lookupflag ? "[lookup]" : "",
                  delflag ? "[delflag]" : "");
  #endif
*/
    //if (!table->noskip_del && (delflag || lookupflag)) {
    if (table.noskip_del == 0 && delflag != 0)
    {
      row_end = row_start-1;
//System.out.println("die from mdb_read_row early1");
      return 0;
    }

/*
  #if MDB_DEBUG
          buffer_dump(mdb->pg_buf, row_start, row_end);
  #endif
*/
    /* find out all the important stuff about the row */
    if (macros.IS_JET4(mdb))
    {
      num_cols = file.mdb_get_int16(mdb, row_start);
    }
    else
    {
      num_cols = mdb.pg_buf[row_start];
    }

//System.out.println("num_cols: " + num_cols);
    var_cols = 0; /* mdb->pg_buf[row_end-1]; */
    fixed_cols = 0; /* num_cols - var_cols; */
    for (j = 0; j < table.num_cols; j++)
    {
      col = (MdbColumn)table.columns.get(j);
      if (mdb_is_fixed_col(col))
        fixed_cols++;
      else
        var_cols++;
    }
    bitmask_sz = (num_cols - 1) / 8 + 1;
    if (macros.IS_JET4(mdb))
    {
      eod = file.mdb_get_int16(mdb, row_end - 3 - var_cols*2 - bitmask_sz);
    }
    else
    {
     eod = mdb.pg_buf[row_end-1-var_cols-bitmask_sz] & 0xff;
    }

    for (i = 0; i < bitmask_sz; i++)
    {
      null_mask[i]=mdb.pg_buf[row_end - bitmask_sz + i + 1];
    }
/*
  #if MDB_DEBUG
          fprintf(stdout,"#cols: %-3d #varcols %-3d EOD %-3d\n",
                  num_cols, var_cols, eod);
  #endif
*/
    if (macros.IS_JET4(mdb))
    {
      col_start = 2;
    }
    else
    {
     /* data starts at 1 */
      col_start = 1;
    }
    fixed_cols_found = 0;
    var_cols_found = 0;

    /* fixed columns */
    for (j=0;j<table.num_cols;j++)
    {
//      System.out.println("col_start: " + col_start);
      col = (MdbColumn)table.columns.get(j);
//      System.out.println("col_size: " + col.col_size);
      if (mdb_is_fixed_col(col) && ++fixed_cols_found <= fixed_cols)
      {
        isnull = mdb_is_null(null_mask, j+1);
        rc = _mdb_attempt_bind(mdb, col, isnull,
        row_start + col_start, col.col_size);
        if (rc == 0)
        {
//System.out.println("die from mdb_read_row early2");
          return 0;
        }
        if (col.col_type != Constants.MDB_BOOL)
          col_start += col.col_size;
      }
    }

    int old_col_start = col_start;

    /* if fixed columns add up to more than 256, we need a jump */
    if (col_start >= 256)
    {
      num_of_jumps++;
      jumps_used++;
      row_start = row_start + col_start - (col_start % 256);
    }

    col_start = row_start;
    /*  */
    while (col_start+256 < row_end-bitmask_sz-1-var_cols-num_of_jumps)
    {
      col_start += 256;
      num_of_jumps++;
    }

    if (macros.IS_JET4(mdb))
    {
      col_ptr = row_end - 2 - bitmask_sz - 1;
      eod = file.mdb_get_int16(mdb, col_ptr - var_cols*2);
      col_start = file.mdb_get_int16(mdb, col_ptr);
    }
    else
    {
      col_ptr = row_end - bitmask_sz - num_of_jumps - 1;
      if (mdb.pg_buf[col_ptr]==0xFF)
      {
        col_ptr--;
        deleted_columns++;
      }
      eod = mdb.pg_buf[col_ptr - var_cols];
      col_start = mdb.pg_buf[col_ptr];
    }
/*
  #if MDB_DEBUG
          fprintf(stdout,"col_start %d num_of_jumps %d\n",
                  col_start, num_of_jumps);
  #endif
*/
          /* variable columns */
    if (col_start < old_col_start) col_start = old_col_start;
    for (j=0;j<table.num_cols;j++)
    {
      col = (MdbColumn)table.columns.get(j);
      if (!mdb_is_fixed_col(col) && ++var_cols_found <= var_cols)
      {
        /* col_start = mdb->pg_buf[row_end-bitmask_sz-var_cols_found]; */
        /* more code goes here but the diff is mangled */

        if (var_cols_found==var_cols)
        {
          len=eod - col_start;
        }
        else
        {
          if (macros.IS_JET4(mdb))
          {
            //next_col = mdb_get_int16(mdb, row_end - bitmask_sz - var_cols_found * 2 - 2 - 1) ;
            next_col = mdb.pg_buf[row_end - bitmask_sz - var_cols_found * 2 - 2] * 256 +
            mdb.pg_buf[row_end - bitmask_sz - var_cols_found * 2 - 2 - 1] ;
            len = next_col - col_start;
          }
          else
          {
           len = mdb.pg_buf[col_ptr - var_cols_found ] - col_start;
          }
          if (len<0)
          len+=256;
        }

        isnull = mdb_is_null(null_mask, j+1);
/*
  #if MDB_DEBUG
                          printf("binding len %d isnull %d col_start %d row_start %d row_end %d bitmask %d var_cols_found %d buf %d\n", len, isnull,col_start,row_start,row_end, bitmask_sz, var_cols_found, mdb->pg_buf[row_end - bitmask_sz - var_cols_found * 2 - 1 - num_of_jumps ]);
  #endif
*/
//System.out.println("99-row_start: " + row_start + " col_start: " + col_start);
        rc = _mdb_attempt_bind(mdb, col, isnull,
        row_start + col_start, len);
        if (rc == 0)
        {
//System.out.println("die from mdb_read_row early3");
          return 0;
        }
        col_start += len;
      }
    }
//System.out.println("return from mdb_read_row");
    return 1;
  }

  private static boolean mdb_is_fixed_col(MdbColumn col)
  {
    return col.is_fixed;
  }

  private static boolean mdb_is_null(byte[] null_mask, int col_num)
  {
    int byte_num = (col_num - 1) / 8;
    int bit_num = (col_num - 1) % 8;

    if ( ((1 << bit_num) & null_mask[byte_num]) != 0)
      return false;
    else
      return true;
  }

  private static int _mdb_attempt_bind(MdbHandle mdb,
          MdbColumn col,
          boolean isnull,
          int offset,
          int len)
    throws IOException
  {
    if (col.col_type == Constants.MDB_BOOL)
    {
      mdb_xfer_bound_bool(mdb, col, isnull);
    }
    else if (col.col_type == Constants.MDB_OLE)
    {
      mdb_xfer_bound_ole(mdb, offset, col, len);
    }
    else if (isnull)
    {
      mdb_xfer_bound_data(mdb, 0, col, 0);
    }
    else
    {
      if (!Sargs.mdb_test_sargs(mdb, col, offset, len))
      {
        return 0;
      }
      mdb_xfer_bound_data(mdb, offset, col, len);
    }
    return 1;
  }

  /*
   * bool has to be handled specially because it uses the null bit to store its
   * value
   */
  public static int mdb_xfer_bound_bool(MdbHandle mdb, MdbColumn col, boolean value)
  {
    col.cur_value_len = value?1:0;

    if (col.bind_ptr != null)
      col.bind_ptr.s = value ? "0" : "1";
    return 0;
  }

  public static int mdb_xfer_bound_ole(MdbHandle mdb, int start, MdbColumn col, int len)
    throws IOException
  {
    int ret = 0;

    if (len != 0)
    {
      col.cur_value_start = start;
      col.cur_value_len = len;
    }
    else
    {
      col.cur_value_start = 0;
      col.cur_value_len = 0;
    }
    if (col.bind_ptr != null)
    {
      ret = mdb_copy_ole(mdb, col.bind_ptr, start, len);
    }
//    if (col.len_ptr)
//    {
//      col.len_ptr = ret;
//    }
    return ret;
  }

  public static int mdb_xfer_bound_data(MdbHandle mdb, int start, MdbColumn col, int len)
    throws IOException
  {
    int ret;
    if (len != 0)
    {
      col.cur_value_start = start;
      col.cur_value_len = len;
    }
    else
    {
      col.cur_value_start = 0;
      col.cur_value_len = 0;
    }

    if (col.bind_ptr != null)
    {
      if (len != 0)
      {
        if (col.col_type == Constants.MDB_NUMERIC)
        {
          col.bind_ptr.s = mdb_num_to_string(mdb, start, col.col_type, col.col_prec, col.col_scale);
        }
        else
        {
          col.bind_ptr.s = mdb_col_to_string(mdb, start, col.col_type, len);
        }
      }
      else
      {
        col.bind_ptr.s = "";
      }
      ret = col.bind_ptr.s.length();
//      if (col.len_ptr)
//      {
//        *col->len_ptr = ret;
//      }
      return ret;
    }
    return 0;
  }

  public static String mdb_col_to_string(MdbHandle mdb, int start, int datatype, int size)
    throws IOException
  {
    String text;
    long t;
    int i,j;

    switch (datatype)
    {
      case Constants.MDB_BOOL:
        /* shouldn't happen.  bools are handled specially
         ** by mdb_xfer_bound_bool() */
        throw new RuntimeException("Should not get here - boolean");
      case Constants.MDB_BYTE:
        return Integer.toString(file.mdb_get_byte(mdb, start));
      case Constants.MDB_INT:
        return Integer.toString(file.mdb_get_int16(mdb, start));
      case Constants.MDB_LONGINT:
        return Integer.toString(file.mdb_get_int32(mdb, start));
      case Constants.MDB_FLOAT:
        return Float.toString(file.mdb_get_single(mdb, start));
      case Constants.MDB_DOUBLE:
        return Double.toString(file.mdb_get_double(mdb, start));
      case Constants.MDB_TEXT:
        if (size < 0)
        {
          return "";
        }
        if (macros.IS_JET4(mdb))
        {
          if (mdb.pg_buf[start] == 0xff &&
              mdb.pg_buf[start + 1] == 0xfe)
          {
            text = new String(mdb.pg_buf,start+2,size-2);
          }
          else
          {
            /* convert unicode to ascii, rather sloppily */
            int index = 0;
            byte[] ba = new byte[size];
            for (i = 0; i < size; i++)
              ba[index++] = (byte)mdb.pg_buf[start + i];
            text = Util.extractText(ba);
          }
        }
        else
        {
          text = new String(mdb.pg_buf,start,size);
        }
        return text;
      case Constants.MDB_SDATETIME:
        t = (int) ( (file.mdb_get_double(mdb, start) - 25569.0) * 86400.0);
        t *= 1000;
        java.util.Calendar cal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("GMT"));
        cal.setTimeInMillis(t);
        text = "" + (cal.get(java.util.Calendar.MONTH)+1) + '/' +
                     cal.get(java.util.Calendar.DAY_OF_MONTH) + '/' +
                     cal.get(java.util.Calendar.YEAR) + ' ' +
                     cal.get(java.util.Calendar.HOUR_OF_DAY) + ':' +
                     cal.get(java.util.Calendar.MINUTE) + ':' +
                     cal.get(java.util.Calendar.SECOND);
        return text;
      case Constants.MDB_MEMO:
        return mdb_memo_to_string(mdb, start, size);
      case Constants.MDB_MONEY:
        return Money.mdb_money_to_string(mdb, start);
      case Constants.MDB_NUMERIC:
        throw new RuntimeException("Should not get here - numeric");
      default:
        throw new RuntimeException("Should not get here - default");
    }
  }

  public static int  mdb_read_next_dpg_by_map1(MdbTableDef table)
    throws IOException
  {
    MdbCatalogEntry entry = table.entry;
    MdbHandle mdb = entry.mdb;
    int pgnum, i, j, bitn, map_pg;
    byte map_byte;

    pgnum = 0;
    //printf("map size %ld\n", table->map_sz);
    for (i = 1;i < table.map_sz-1;i += 4)
    {
      map_pg = file._mdb_get_int32(table.usage_map, i);
      //printf("loop %d pg %ld %02x%02x%02x%02x\n",i, map_pg,table->usage_map[i],table->usage_map[i+1],table->usage_map[i+2],table->usage_map[i+3]);

      if (map_pg == 0)
        continue;

      if(file.mdb_read_alt_pg(mdb, map_pg) != mdb.fmt.pg_size)
      {
        throw new RuntimeException("Oops! didn't get a full page at "+map_pg);
      }

      //printf("reading page %ld\n",map_pg);
      for (j = 4;j < mdb.fmt.pg_size;j++)
      {
        for (bitn=0;bitn<8;bitn++)
        {
          if ( (mdb.alt_pg_buf[j] & 1 << bitn) != 0 && pgnum > table.cur_phys_pg)
          {
            table.cur_phys_pg = pgnum;
            if (file.mdb_read_pg(mdb, pgnum) == 0)
            {
              return 0;
            }
            else
            {
              //printf("page found at %04x %d\n",pgnum, pgnum);
              return pgnum;
            }
          }
          pgnum++;
        }
      }
    }
    /* didn't find anything */
    //printf("returning 0\n");
    return 0;
  }

  public static String mdb_memo_to_string(MdbHandle mdb, int start, int size)
    throws IOException
  {
    MdbFormatConstants fmt = mdb.fmt;
    int memo_len;
    String text = "";
//    char[] text[MDB_BIND_SIZE];
    int memo_flags;
    int row_start, row_stop;
    int memo_row;
    int lval_pg;
    int len;
    int i;

    if (size < Constants.MDB_MEMO_OVERHEAD)
    {
      return "";
    }

//    #if MDB_DEBUG
//      buffer_dump(mdb->pg_buf, start, start + 12);
//    #endif

    memo_len = file.mdb_get_int16(mdb, start);
    memo_flags = file.mdb_get_int16(mdb, start+2);

    if ((memo_flags & 0x8000)!=0)
    {
      /* inline memo field */
      int begin = start + Constants.MDB_MEMO_OVERHEAD + 2;
      int end = size - Constants.MDB_MEMO_OVERHEAD - 2;
      if (begin >= 0 && begin < mdb.pg_buf.length && end > 0 &&
        begin + end <= mdb.pg_buf.length)
      {
        text = new String(mdb.pg_buf, begin, end);
      }
//      strncpy(text, &mdb->pg_buf[start + MDB_MEMO_OVERHEAD],size - MDB_MEMO_OVERHEAD);
//      text[size - MDB_MEMO_OVERHEAD]='\0';
      return text;
    }
    else if ((memo_flags & 0x4000)!=0)
    {
      /* The 16 bit integer at offset 0 is the length of the memo field.
       * The 24 bit integer at offset 5 is the page it is stored on.
      */
      memo_row = mdb.pg_buf[start+4];

      lval_pg = file.mdb_get_int24(mdb, start+5);
//      #if MDB_DEBUG
//        printf("Reading LVAL page %06x\n", lval_pg);
//      #endif
      if(file.mdb_read_alt_pg(mdb, lval_pg) != fmt.pg_size)
      {
        /* Failed to read */
        throw new IOException("failed to read");
      }
      /* swap the alt and regular page buffers, so we can call get_int16 */
      file.mdb_swap_pgbuf(mdb);
      if (memo_row != 0)
      {
        row_stop = file.mdb_get_int16(mdb, fmt.row_count_offset + 2 + (memo_row - 1) * 2) & 0x0FFF;
      }
      else
      {
        row_stop = fmt.pg_size - 1;
      }
      row_start = file.mdb_get_int16(mdb, fmt.row_count_offset + 2 + memo_row * 2);
//    #if MDB_DEBUG
//      printf("row num %d row start %d row stop %d\n", memo_row, row_start, row_stop);
//      buffer_dump(mdb->pg_buf,row_start, row_start + len);
//    #endif
      len = row_stop - row_start;
      if (macros.IS_JET3(mdb))
      {
        text = new String(mdb.pg_buf,row_start,len);
      }
      else
      {
        if (mdb.pg_buf[row_start]==(byte)0xff && mdb.pg_buf[row_start+1]==(byte)0xfe)
        {
          text = new String(mdb.pg_buf,row_start+2,len-2);
        }
        else
        {
          /* convert unicode to ascii, rather sloppily */
//          text = new String(mdb.pg_buf,row_start,len,"UTF16");
          /** @todo check this */
          char[] ca = new char[len];
          int j = 0;
          for (i = 0; i< len;i += 2)
            ca[j++] = (char)file.unsign(mdb.pg_buf[row_start + i]);
          text = new String(ca,0,j++);
        }
      }
      /* make sure to swap page back */
      file.mdb_swap_pgbuf(mdb);
      return text;
    }
    else
    {
      /* if (memo_flags == 0x0000) { */
      lval_pg = file.mdb_get_int32(mdb, start + 4);
      memo_row = mdb.pg_buf[start+4];
      //lval_pg = file.mdb_get_int24(mdb, start+5);
  //    #if MDB_DEBUG
  //      printf("Reading LVAL page %06x\n", lval_pg);
  //    #endif
      /* swap the alt and regular page buffers, so we can call get_int16 */
      file.mdb_swap_pgbuf(mdb);
      do
      {
        if(file.mdb_read_pg(mdb, lval_pg) != fmt.pg_size)
        {
          /* Failed to read */
          if (memo_len < fmt.pg_size) {
            file.mdb_swap_pgbuf(mdb);
          }
          return text;
        }
        if (memo_row != 0)
        {
          row_stop = file.mdb_get_int16(mdb, 10 + (memo_row - 1) * 2) & 0x0FFF;
        }
        else
        {
          row_stop = fmt.pg_size - 1;
        }
        row_start = file.mdb_get_int16(mdb, 10 + memo_row * 2);
  //      #if MDB_DEBUG
  //        printf("row num %d row start %d row stop %d\n", memo_row, row_start, row_stop);
  //      #endif
        len = row_stop - row_start;

        int strlen = text.length() + len - 4 > Constants.MDB_BIND_SIZE ?
          Constants.MDB_BIND_SIZE - text.length() : len - 4;
        if (strlen < 0) strlen = 0;
        if (row_start + 4 >= mdb.pg_buf.length) {
          row_start = mdb.pg_buf.length - strlen - 4;
        }

        text = text + new String(mdb.pg_buf,row_start+4, strlen);
//        strncat(text, &mdb->pg_buf[row_start+4],
//                strlen(text) + len - 4 > MDB_BIND_SIZE ?MDB_BIND_SIZE - strlen(text) : len - 4);

        /* find next lval page */
        memo_row = mdb.pg_buf[row_start];
        //lval_pg = file.mdb_get_int24(mdb, row_start+1);
        lval_pg = file.mdb_get_int32(mdb, row_start);
      }
      while (lval_pg != 0);
      /* make sure to swap page back */
      file.mdb_swap_pgbuf(mdb);
      return text;
    }
  }

  private static int mdb_copy_ole(MdbHandle mdb,Holder dest,
                                  int start, int size)
    throws IOException
  {
    int ole_len;
    int ole_flags;
    int row_start, row_stop;
    int ole_row;
    int lval_pg;
    int len, cur;

    if (size < Constants.MDB_MEMO_OVERHEAD)
    {
      return 0;
    }

    ole_len = file.mdb_get_int16(mdb, start);
    ole_flags = file.mdb_get_int16(mdb, start+2);

    if (ole_flags == 0x8000)
    {
      len = size - Constants.MDB_MEMO_OVERHEAD;
      /* inline ole field */
      if (dest != null)
      {
        dest.ba = new byte[len];
        System.arraycopy(mdb.pg_buf,start + Constants.MDB_MEMO_OVERHEAD,
                         dest.ba,0,len);
//        memcpy(dest, mdb.pg_buf[start + Constants.MDB_MEMO_OVERHEAD],
//               size - Constants.MDB_MEMO_OVERHEAD);
      }
      return len;
    }
    else if (ole_flags == 0x4000)
    {
      /* The 16 bit integer at offset 0 is the length of the memo field.
      * The 24 bit integer at offset 5 is the page it is stored on.
      */
      ole_row = mdb.pg_buf[start+4];

      lval_pg = file.mdb_get_int24(mdb, start+5);
//#if MDB_DEBUG_OLE
//  printf("Reading LVAL page %06x\n", lval_pg);
//#endif
      if(file.mdb_read_alt_pg(mdb, lval_pg) != mdb.fmt.pg_size)
      {
        /* Failed to read */
        return 0;
      }
      /* swap the alt and regular page buffers, so we can call get_int16 */
      file.mdb_swap_pgbuf(mdb);
      if (ole_row != 0)
        row_stop = file.mdb_get_int16(mdb, 10 + (ole_row - 1) * 2) & 0x0FFF;
      else
        row_stop = mdb.fmt.pg_size - 1;
      row_start = file.mdb_get_int16(mdb, 10 + ole_row * 2);
      len = row_stop - row_start;
      if (dest != null)
      {
        dest.ba = new byte[len];
        System.arraycopy(mdb.pg_buf,row_start,dest.ba,0,len);
//        memcpy(dest, mdb.pg_buf[row_start], len);
      }
      /* make sure to swap page back */
      file.mdb_swap_pgbuf(mdb);
      return len;
    }
    else if (ole_flags == 0x0000)
    {
      ole_row = mdb.pg_buf[start+4];
      lval_pg = file.mdb_get_int24(mdb, start+5);
//#if MDB_DEBUG_OLE
//  printf("Reading LVAL page %06x\n", lval_pg);
//#endif
      /* swap the alt and regular page buffers, so we can call get_int16 */
      file.mdb_swap_pgbuf(mdb);
      cur=0;
      do
      {
        if(file.mdb_read_pg(mdb, lval_pg) != mdb.fmt.pg_size)
        {
          /* Failed to read */
          return 0;
        }
        if (ole_row != 0)
        {
          row_stop = file.mdb_get_int16(mdb, 10 + (ole_row - 1) * 2) & 0x0FFF;
        }
        else
        {
          row_stop = mdb.fmt.pg_size - 1;
        }
        row_start = file.mdb_get_int16(mdb, 10 + ole_row * 2);
        if (row_start > row_stop) row_start = 0;
//#if MDB_DEBUG_OLE
//  printf("row num %d row start %d row stop %d\n", ole_row, row_start, row_stop);
//#endif
        len = row_stop - row_start;
        if (dest != null)
        {
          dest.ba = new byte[len];
          try {
            System.arraycopy(mdb.pg_buf,row_start+4,dest.ba,cur,len-4);
          }
          catch (ArrayIndexOutOfBoundsException e) {
            break;
          }
//          memcpy(dest[cur], mdb.pg_buf[row_start+4],len - 4);
        }
        cur += len - 4;

        /* find next lval page */
        ole_row = mdb.pg_buf[row_start];
        lval_pg = file.mdb_get_int24(mdb, row_start+1);
      } while (lval_pg != 0);
      /* make sure to swap page back */
      file.mdb_swap_pgbuf(mdb);
      return cur;
    }
    else
    {
      //System.out.print("Unhandled ole field flags = " + ole_flags);
//      throw new RuntimeException("Unhandled ole field flags = " + ole_flags);
//      fprintf(stderr,"Unhandled ole field flags = %04x\n", ole_flags);
      return 0;
    }
  }

  // a decimal number
  private static String mdb_num_to_string(MdbHandle mdb, int start,
                                          int datatype, int prec, int scale)
  {
//    char text[MDB_BIND_SIZE];
//    char tmpbuf[MDB_BIND_SIZE];
//    char mask[20];
    int l; //, whole, fraction;

    l = file.unsign(mdb.pg_buf[start+16]) * 256 * 256 * 256 +
        file.unsign(mdb.pg_buf[start+15]) * 256 * 256 +
        file.unsign(mdb.pg_buf[start+14]) * 256 +
        file.unsign(mdb.pg_buf[start+13]);

//    sprintf(mask,"%%0%ldld",prec);
//    sprintf(tmpbuf,mask,l);
    if (scale == 0)
    {
//      strcpy(text,tmpbuf);
      return Integer.toString(l);
    }
    else
    {
      throw new RuntimeException("not ported yet");
//      strncpy(text,tmpbuf,prec-scale);
//      strcat(text,".");
//      strcat(text,&tmpbuf[strlen(tmpbuf)-scale]);
    }
//    return text;
  }
}
