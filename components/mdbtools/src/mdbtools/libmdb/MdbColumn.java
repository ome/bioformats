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
