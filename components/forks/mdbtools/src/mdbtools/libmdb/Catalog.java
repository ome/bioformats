package mdbtools.libmdb;

import java.io.IOException;
import java.util.ArrayList;

public class Catalog
{
  public static ArrayList mdb_read_catalog (MdbHandle mdb, int objtype)
    throws IOException
  {
    MdbCatalogEntry entry, msysobj;
    MdbTableDef table;
    Holder parentid = new Holder();
    Holder objname = new Holder();
    Holder tobjtype = new Holder();
    int type;

   mem.mdb_free_catalog(mdb);
   mem.mdb_alloc_catalog(mdb);
   mdb.num_catalog = 0;

//   /* dummy up a catalog entry so we may read the table def */
   msysobj = new MdbCatalogEntry();
   msysobj.mdb = mdb;
   msysobj.object_type = Constants.MDB_TABLE;
   msysobj.table_pg = 2;
   msysobj.object_name = "MSysObjects";

   /* mdb_table_dump(&msysobj); */

   table = Table.mdb_read_table(msysobj);
   Table.mdb_read_columns(table);

   Data.mdb_bind_column(table, 1, parentid);
   Data.mdb_bind_column(table, 3, objname);
   Data.mdb_bind_column(table, 4, tobjtype);

   Data.mdb_rewind_table(table);

int whileCounter = 0;
   while (Data.mdb_fetch_row(table))
   {
      whileCounter++;
      type = Integer.parseInt(tobjtype.s);
//System.out.println("while: " + objname.s + " objtype: " + objtype + " type: " + type);
      if (objtype==Constants.MDB_ANY || type == objtype)
      {
        entry = new MdbCatalogEntry();
        entry.mdb = mdb;
        entry.object_name = objname.s;
        entry.object_type = (type & 0x7F);
        entry.table_pg = Integer.parseInt(parentid.s) & 0x00FFFFFF;
        mdb.num_catalog++;
        mdb.catalog.add(entry);
      }
    }
//    System.out.println("whileCounter: " + whileCounter);
    return mdb.catalog;
  }
}
