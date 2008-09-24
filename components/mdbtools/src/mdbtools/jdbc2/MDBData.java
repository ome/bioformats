package mdbtools.jdbc2;

import mdbtools.libmdb.Constants;
import mdbtools.libmdb.Data;
import mdbtools.libmdb.Holder;
import mdbtools.libmdb.MdbCatalogEntry;
import mdbtools.libmdb.MdbHandle;
import mdbtools.libmdb.MdbTableDef;
import mdbtools.libmdb.Table;

import java.io.IOException;
import java.sql.SQLException;

public class MDBData implements mdbtools.dbengine.Data
{
  private MdbHandle mdb;
  private MdbTableDef table;
  private Holder[] bound_values = new Holder[256];

  MDBData(MdbHandle mdb, String tableName)
    throws SQLException
  {
    try
    {
      this.mdb = mdb;
      table = null;
      for (int i = 0;i < mdb.num_catalog && table == null;i++)
      {
        MdbCatalogEntry entry = (MdbCatalogEntry)mdb.catalog.get(i);
        if (entry.object_type == Constants.MDB_TABLE &&
            entry.object_name.equalsIgnoreCase(tableName))
        {
          table = Table.mdb_read_table(entry);
        }
      }
      Table.mdb_read_columns(table);
      Data.mdb_rewind_table(table);
      for (int i = 0; i < table.num_cols;i++)
      {
        bound_values[i] = new Holder();
        Data.mdb_bind_column(table, i + 1, bound_values[i]);
      }
    }
    catch(IOException e)
    {
      throw new SQLException(e.getMessage());
    }
  }

  /**
   * goto the next (or first) row
   * @return true if row exits, false if not
   */
  public boolean next()
    throws SQLException
  {
    try
    {
      return Data.mdb_fetch_row(table);
    }
    catch(IOException e)
    {
      throw new SQLException(e.getMessage());
    }
  }

  /**
   * get the data at a certain column
   * @param index the column to get
   */
  public Object get(int index)
    throws SQLException
  {
    return bound_values[index].s;
  }
}
