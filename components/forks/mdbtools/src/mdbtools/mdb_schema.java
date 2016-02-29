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
