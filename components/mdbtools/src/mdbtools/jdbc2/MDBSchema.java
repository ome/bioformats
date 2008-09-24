package mdbtools.jdbc2;

import mdbtools.libmdb.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * MDBSchema caches the schema of an mdb file
 */
public class MDBSchema
{
  private ArrayList tableNames = new ArrayList();
  // key = table name all lowercase, value = arraylist of columns
  private HashMap tables = new HashMap();

  public MDBSchema(MdbHandle mdb)
    throws IOException
  {
    /* loop over each entry in the catalog */
    for (int i=0; i < mdb.num_catalog; i++)
    {
      MdbCatalogEntry entry = (MdbCatalogEntry) mdb.catalog.get(i);

      /* if it's a table */
      if (entry.object_type == Constants.MDB_TABLE)
      {
        /* skip the MSys tables */
        if (entry.object_name.startsWith("MSys") == false)
        {
          String tableName = entry.object_name;
          tableNames.add(tableName);
          ArrayList columns = new ArrayList();
          tables.put(tableName.toLowerCase(), columns);

          MdbTableDef table = Table.mdb_read_table(entry);

          /* get the columns */
          Table.mdb_read_columns(table);

          /* loop over the columns, dumping the names and types */
          for (int j = 0; j < table.num_cols; j++)
          {
            MdbColumn col = (MdbColumn) table.columns.get(j);
            columns.add(col.name);
          }
        }
      }
    }
  }

  public ArrayList getColumns(String tableName)
  {
    return (ArrayList)tables.get(tableName.toLowerCase());
  }

  public ArrayList getTableNames()
  {
    return tableNames;
  }
}
