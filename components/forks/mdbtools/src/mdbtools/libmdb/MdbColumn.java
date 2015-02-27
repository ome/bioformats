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

public class MdbColumn implements Cloneable
{
  public String name;
  public int		col_type;
  public int		col_size;
  public Holder bind_ptr;
//  public int len_ptr;  // +++crs+++ length of bind_ptr.s
//  public GHashTable	*properties;
  public int		num_sargs;
  public ArrayList sargs;
//  public GPtrArray	*idx_sarg_cache;
  public boolean is_fixed;
  public int query_order;
  public int col_num;
  public int cur_value_start;
  public int cur_value_len;
  /* numerics only */
  public int col_prec;
  public int col_scale;

  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch(CloneNotSupportedException e)
    {
      // should never happen
      e.printStackTrace();
      System.exit(1);
      return null;
    }
  }
}
