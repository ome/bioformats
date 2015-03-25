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

public class MdbFormatConstants
{
  public static final MdbFormatConstants MdbJet4Constants;
  public static final MdbFormatConstants MdbJet3Constants;

  static
  {
    MdbJet3Constants = new MdbFormatConstants();
    MdbJet3Constants.pg_size = 2048;
    MdbJet3Constants.row_count_offset = 0x08;
    MdbJet3Constants.tab_num_rows_offset = 12;
    MdbJet3Constants.tab_num_cols_offset = 25;
    MdbJet3Constants.tab_num_idxs_offset = 27;
    MdbJet3Constants.tab_num_ridxs_offset = 31;
    MdbJet3Constants.tab_usage_map_offset = 35;
    MdbJet3Constants.tab_first_dpg_offset = 36;
    MdbJet3Constants.tab_cols_start_offset = 43;
    MdbJet3Constants.tab_ridx_entry_size = 8;
    MdbJet3Constants.col_fixed_offset = 13;
    MdbJet3Constants.col_size_offset = 16;
    MdbJet3Constants.col_num_offset = 1;
    MdbJet3Constants.tab_col_entry_size = 18;
//    2048 , 0x08, 12, 25, 27, 31, 35, 36, 43, 8, 13, 16, 1, 18

    MdbJet4Constants = new MdbFormatConstants();
    MdbJet4Constants.pg_size = 4096;
    MdbJet4Constants.row_count_offset = 0x0c;
    MdbJet4Constants.tab_num_rows_offset = 12;
    MdbJet4Constants.tab_num_cols_offset = 45;
    MdbJet4Constants.tab_num_idxs_offset = 47;
    MdbJet4Constants.tab_num_ridxs_offset = 51;
    MdbJet4Constants.tab_usage_map_offset = 55;
    MdbJet4Constants.tab_first_dpg_offset = 56;
    MdbJet4Constants.tab_cols_start_offset = 63;
    MdbJet4Constants.tab_ridx_entry_size = 12;
    MdbJet4Constants.col_fixed_offset = 15;
    MdbJet4Constants.col_size_offset = 23;
    MdbJet4Constants.col_num_offset = 5;
    MdbJet4Constants.tab_col_entry_size = 25;
//    4096 // , 0x0c, 12, 45, 47, 51, 55, 56, 63, 12, 15, 23, 5, 25
  };


/* offset to row count on data pages...version dependant */
  public int pg_size;
  public int row_count_offset;
  public int tab_num_rows_offset;
  public int tab_num_cols_offset;
  public int tab_num_idxs_offset;
  public int tab_num_ridxs_offset;
  public int tab_usage_map_offset;
  public int tab_first_dpg_offset;
  public int tab_cols_start_offset;
  public int tab_ridx_entry_size;
  public int col_fixed_offset;
  public int col_size_offset;
  public int col_num_offset;
  public int tab_col_entry_size;
}
