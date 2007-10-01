//
// MDBParser.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.in;

import java.util.*;
import loci.formats.*;

/**
 * Utility class for parsing MDB database files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/MDBParser.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/MDBParser.java">SVN</a></dd></dl>
 */
public final class MDBParser {

  // -- Constants --

  private static final String NO_MDB_MSG =
    "The Java port of MDB tools is required to read MDB files. Please " +
    "obtain mdbtools-java.jar from http://loci.wisc.edu/ome/formats.html";

  // -- Static fields --

  private static boolean noMDB = false;
  private static ReflectedUniverse r = createReflectedUniverse();

  private static ReflectedUniverse createReflectedUniverse() {
    r = null;
    try {
      r = new ReflectedUniverse();
      r.exec("import java.util.Vector");
      r.exec("import mdbtools.jdbc2.File");
      r.exec("import mdbtools.libmdb.Catalog");
      r.exec("import mdbtools.libmdb.Constants");
      r.exec("import mdbtools.libmdb.Data");
      r.exec("import mdbtools.libmdb.Holder");
      r.exec("import mdbtools.libmdb.MdbCatalogEntry");
      r.exec("import mdbtools.libmdb.MdbColumn");
      r.exec("import mdbtools.libmdb.MdbHandle");
      r.exec("import mdbtools.libmdb.MdbTableDef");
      r.exec("import mdbtools.libmdb.Table");
      r.exec("import mdbtools.libmdb.file");
      r.exec("import mdbtools.libmdb.mem");
    }
    catch (Throwable t) {
      noMDB = true;
      if (FormatHandler.debug) LogTools.trace(t);
    }
    return r;
  }

  // -- Constructor --

  private MDBParser() { }

  // -- Utility methods --

  /** Parses table structure for a specified MDB file. */
  public static void parseDatabase(String filename, Hashtable h)
    throws FormatException
  {
    if (noMDB) throw new FormatException(NO_MDB_MSG);

    try {
      // initialize

      r.setVar("twoFiveSix", 256);
      r.exec("boundValues = new Vector()");
      r.setVar("delimiter", ",");

      r.exec("mem.mdb_init()");

      // print out all data

      r.setVar("filename", filename);
      r.exec("dbfile = new File(filename)");
      r.exec("mdb = file.mdb_open(dbfile)");
      r.exec("Catalog.mdb_read_catalog(mdb, Constants.MDB_TABLE)");

      int num = ((Integer) r.getVar("mdb.num_catalog")).intValue();

      for (int i=0; i<num; i++) {
        r.setVar("c", (List) r.getVar("mdb.catalog"));
        r.setVar("i", i);
        r.exec("entry = c.get(i)");
        r.setVar("objType",
          ((Integer) r.getVar("entry.object_type")).intValue());
        r.setVar("objName", (String) r.getVar("entry.object_name"));

        int objType = ((Integer) r.getVar("objType")).intValue();
        int tableType = ((Integer) r.getVar("Constants.MDB_TABLE")).intValue();
        boolean isTable = objType == tableType;

        String objName = (String) r.getVar("objName");

        if (isTable && !objName.startsWith("MSys")) {
          r.exec("table = Table.mdb_read_table(entry)");
          try {
            r.exec("Table.mdb_read_columns(table)");
          }
          catch (ReflectException e) { break; }
          r.exec("Data.mdb_rewind_table(table)");

          r.setVar("numCols",
            ((Integer) r.getVar("table.num_cols")).intValue());

          int numCols = ((Integer) r.getVar("numCols")).intValue();

          for (int j=0; j<numCols; j++) {
            r.setVar("j", j);
            r.exec("blah = new Holder()");
            r.setVar("l", j + 1);
            r.exec("Data.mdb_bind_column(table, l, blah)");
            r.exec("boundValues.add(blah)");
          }

          StringBuffer[] sbs = new StringBuffer[numCols];
          for (int j=0; j<sbs.length; j++) sbs[j] = new StringBuffer();

          boolean moreRows = true;
          try {
            r.exec("moreRows = Data.mdb_fetch_row(table)");
            moreRows = ((Boolean) r.getVar("moreRows")).booleanValue();
          }
          catch (ReflectException e) { moreRows = false; }

          while (moreRows) {
            for (int j=0; j<numCols; j++) {
              r.setVar("j", j);
              r.setVar("columns", (List) r.getVar("table.columns"));
              r.exec("col = columns.get(j)");
              if (sbs[j].length() > 0) sbs[j].append(",");
              r.exec("blah = boundValues.get(j)");
              sbs[j].append((String) r.getVar("blah.s"));
            }
            try {
              r.exec("moreRows = Data.mdb_fetch_row(table)");
              moreRows = ((Boolean) r.getVar("moreRows")).booleanValue();
            }
            catch (ReflectException e) { moreRows = false; }
          }

          // place column of data in the hashtable
          // key is table name + column name, value is each value in the
          // column, separated by commas

          for (int j=0; j<sbs.length; j++) {
            r.setVar("j", j);
            r.setVar("columns", (List) r.getVar("table.columns"));
            r.exec("col = columns.get(j)");
            h.put(objName + " - " + (String) r.getVar("col.name"),
              sbs[j].toString());
          }
        }
      }
    }
    catch (ReflectException exc) {
      LogTools.trace(exc);
    }
  }

}
