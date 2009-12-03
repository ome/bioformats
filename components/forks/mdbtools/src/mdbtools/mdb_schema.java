package mdbtools;

import mdbtools.libmdb.*;

public class mdb_schema
{

  public static void main(String[] args)
  {
//    args = new String[1];
//    args[0] = "/home/whoever/Downloads/test.mdb";

    int   i, j, k;
    MdbHandle mdb;
    MdbCatalogEntry entry;
    MdbTableDef table;
    MdbColumn col;
    char the_relation;
//    char tabname;
    int opt;

    if (args.length < 1)
    {
      System.out.println ("Usage: <file> [<backend>]");
      return;
    }

  // +++crs+++ not sure what this does
//    while ((opt=getopt(argc, argv, "T:"))!=-1) {
//    switch (opt) {
//    case 'T':
//     tabname = (char *) malloc(strlen(optarg)+1);
//     strcpy(tabname, optarg);
//     break;
//    }
//    }

    try
    {
      mem.mdb_init();

      /* open the database */
      mdb = file.mdb_open(new mdbtools.jdbc2.File(args[0]));

    // +++crs+++ not sure what this does
//    if (argc - optind >2) {
//    if (!mdb_set_default_backend(mdb, argv[optind + 1])) {
//    fprintf(stderr,"Invalid backend type\n");
//    mdb_exit();
//    exit(1);
//    }
//    }

      /* read the catalog */
      Catalog.mdb_read_catalog (mdb, Constants.MDB_TABLE);

      /* loop over each entry in the catalog */
      for (i=0; i < mdb.num_catalog; i++)
      {
        entry = (MdbCatalogEntry)mdb.catalog.get(i);

        /* if it's a table */
        if (entry.object_type == Constants.MDB_TABLE)
        {
          /* skip the MSys tables */
          if (entry.object_name.startsWith("MSys") == false)
          {
  //	   /* make sure it's a table (may be redundant) */  // +++crs+++ it is
  //	   if (!strcmp (mdb_get_objtype_string (entry->object_type), "Table"))
  //	     {
            /* drop the table if it exists */
            System.out.println("DROP TABLE "+entry.object_name+";");

            /* create the table */
            System.out.println("CREATE TABLE "+entry.object_name);
            System.out.println(" (");

            table = Table.mdb_read_table(entry);

            /* get the columns */
            Table.mdb_read_columns (table);

            /* loop over the columns, dumping the names and types */
            for (k = 0; k < table.num_cols; k++)
            {
              col = (MdbColumn)table.columns.get(k);

              System.out.print("\t"+col.name+"\t\t\t"+
              backend.mdb_get_coltype_string (mdb.default_backend, col.col_type));

              if (col.col_size != 0)
                System.out.print(" ("+col.col_size+")");

              if (k < table.num_cols - 1)
                System.out.println(", ");
              else
                System.out.println("");
            }

            System.out.println("\n");
            System.out.println(");");
            System.out.println("");
//            System.out.println("-- CREATE ANY INDEXES ...");
//            System.out.println("");
  //	     }
          }
       }
     }

    // +++crs+++ this always segfaults on me - no sense porting broken code
  //  fprintf (stdout, "\n\n");
  //  fprintf (stdout, "-- CREATE ANY Relationships ...\n");
  //  fprintf (stdout, "\n");
  //  the_relation=mdb_get_relationships(mdb);
  //  while (the_relation[0] != '\0') {
  //  fprintf(stdout,"%s\n",the_relation);
  //  the_relation=mdb_get_relationships(mdb);
  //  }

  //   mdb_free_handle (mdb);
  //   mdb_exit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }
}
