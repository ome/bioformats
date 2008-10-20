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
