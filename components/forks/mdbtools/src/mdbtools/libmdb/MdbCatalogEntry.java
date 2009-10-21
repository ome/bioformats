package mdbtools.libmdb;

public class MdbCatalogEntry
{
  public MdbHandle mdb;
  public String object_name;
  public int object_type;
  public int table_pg; /* misnomer since object may not be a table */
  public int kkd_pg;
  public int kkd_rowid;
  public int num_props;
//  GArray		*props;
//  GArray		*columns;
}
