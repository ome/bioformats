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

import java.util.ArrayList;

public class MdbTableDef
{
  public MdbCatalogEntry entry;
  public String name;
  public int	num_cols;
  public ArrayList columns;
  public int	num_rows;
  public int	index_start;
  public int	num_real_idxs;
  public int	num_idxs;
//  public GPtrArray	*indices;
  public int	first_data_pg;
  public int	cur_pg_num;
  public int	cur_phys_pg;
  public int	cur_row;
  public int  noskip_del;  /* don't skip deleted rows */
  /* object allocation map */
  public int  map_base_pg;
  public int  map_sz;
  public byte[] usage_map;
  public int  idxmap_base_pg;
  public int  idxmap_sz;
//  public unsigned char *idx_usage_map;
}
