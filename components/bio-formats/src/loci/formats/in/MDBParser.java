//
// MDBParser.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.in;

import java.util.List;
import java.util.Vector;

import loci.common.LogTools;
import loci.common.ReflectException;
import loci.common.ReflectedUniverse;
import loci.formats.FormatException;
import loci.formats.MissingLibraryException;

/**
 * Utility class for parsing MDB database files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/MDBParser.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/MDBParser.java">SVN</a></dd></dl>
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
      LogTools.traceDebug(t);
    }
    return r;
  }

  // -- Constructor --

  private MDBParser() { }

  // -- Utility methods --

  /** Parses table structure for a specified MDB file. */
  public static Vector<String[]>[] parseDatabase(String filename)
    throws FormatException
  {
    if (noMDB) throw new MissingLibraryException(NO_MDB_MSG);

    try {
      // initialize

      r.exec("boundValues = new Vector()");

      r.exec("mem.mdb_init()");

      // print out all data

      r.setVar("filename", filename);
      r.exec("dbfile = new File(filename)");
      r.exec("mdb = file.mdb_open(dbfile)");
      r.exec("Catalog.mdb_read_catalog(mdb, Constants.MDB_TABLE)");

      int num = ((Integer) r.getVar("mdb.num_catalog")).intValue();

      r.setVar("c", (List) r.getVar("mdb.catalog"));
      int realCount = num;
      for (int i=0; i<num; i++) {
        r.setVar("i", i);
        r.exec("entry = c.get(i)");
        int objType = ((Integer) r.getVar("entry.object_type")).intValue();
        int tableType = ((Integer) r.getVar("Constants.MDB_TABLE")).intValue();
        String objName = (String) r.getVar("entry.object_name");
        if (objType != tableType || objName.startsWith("MSys")) {
          realCount--;
        }
      }
      Vector<String[]>[] rtn = new Vector[realCount];

      int previousColumnCount = 0;

      int index = 0;
      for (int i=0; i<num; i++) {
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
          rtn[index++] = new Vector<String[]>();
          r.exec("table = Table.mdb_read_table(entry)");
          try {
            r.exec("Table.mdb_read_columns(table)");
          }
          catch (ReflectException e) {
            LogTools.traceDebug(e);
            break;
          }

          int numCols = ((Integer) r.getVar("table.num_cols")).intValue();

          for (int j=0; j<numCols; j++) {
            r.exec("blah = new Holder()");
            r.setVar("l", j + 1);
            r.exec("Data.mdb_bind_column(table, l, blah)");
            r.exec("boundValues.add(blah)");
          }

          boolean moreRows = true;
          try {
            r.exec("moreRows = Data.mdb_fetch_row(table)");
            moreRows = ((Boolean) r.getVar("moreRows")).booleanValue();
          }
          catch (ReflectException e) {
            moreRows = false;
            LogTools.traceDebug(e);
          }

          while (moreRows) {
            String[] row = new String[numCols];
            for (int j=0; j<numCols; j++) {
              r.setVar("j", j + previousColumnCount + 1);
              try {
                r.exec("blah = boundValues.get(j)");
                row[j] = (String) r.getVar("blah.s");
              }
              catch (ReflectException e) { }
            }
            rtn[index - 1].add(row);
            try {
              r.exec("moreRows = Data.mdb_fetch_row(table)");
              moreRows = ((Boolean) r.getVar("moreRows")).booleanValue();
            }
            catch (ReflectException e) {
              moreRows = false;
            }
          }

          // place column of data in the hashtable
          // key is table name + column name, value is each value in the
          // column, separated by commas

          r.setVar("columns", (List) r.getVar("table.columns"));

          String[] columnNames = new String[numCols + 1];
          columnNames[0] = objName;
          for (int j=1; j<numCols; j++) {
            r.setVar("j", j);
            r.exec("col = columns.get(j)");
            columnNames[j] = (String) r.getVar("col.name");
          }
          rtn[index - 1].insertElementAt(columnNames, 0);
          previousColumnCount += numCols;
        }
      }
      return rtn;
    }
    catch (ReflectException exc) {
      LogTools.trace(exc);
    }
    return null;
  }

}
