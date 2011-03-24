//
// MetakitReader.java
//

/*
OME Metakit package for reading Metakit database files.
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

package ome.metakit;

import java.io.IOException;

import loci.common.RandomAccessInputStream;

/**
 * Top-level reader for Metakit database files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/metakit/src/ome/metakit/MetakitReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/metakit/src/ome/metakit/MetakitReader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class MetakitReader {

  // -- Fields --

  private RandomAccessInputStream stream;

  // -- Constructors --

  public MetakitReader(String file) throws IOException {
    this(new RandomAccessInputStream(file));
  }

  public MetakitReader(RandomAccessInputStream stream) {
    this.stream = stream;

  }

  // -- MetakitReader API methods --

  /**
   * Retrieve the number of tables in this database file.
   */
  public int getTableCount() {
    // TODO
    return -1;
  }

  /**
   * Retrieve the name of every table in this database file.
   * The length of the returned array is equivalent to {@link getTableCount()}.
   */
  public String[] getTableNames() {
    // TODO
    return null;
  }

  /**
   * Retrieve the name of every column in the table with the given index.
   * Tables are indexed from 0 to <code>{@link getTableCount()} - 1</code>.
   */
  public String[] getColumnNames(int tableIndex) {
    // TODO
    return null;
  }

  /**
   * Retrieve the name of every column in the named table.
   */
  public String[] getColumnNames(String tableName) {
    // TODO
    return null;
  }

  /**
   * Retrieve the type for every column in the table with the given index.
   * Tables are indexed from 0 to <code>{@link getTableCount()} - 1</code>.
   *
   * Every Object in the arrays returned by {@link getTableData(int)} and
   * {@link getTableData(String)} will be an instance of the corresponding
   * Class in the Class[] returned by this method.
   */
  public Class[] getColumnTypes(int tableIndex) {
    // TODO
    return null;
  }

  /**
   * Retrieve the type for every column in the named table.
   *
   * Every Object in the arrays returned by {@link getTableData(int)} and
   * {@link getTableData(String)} will be an instance of the corresponding
   * Class in the Class[] returned by this method.
   */
  public Class[] getColumnTypes(String tableName) {
    // TODO
    return null;
  }

  /**
   * Retrieve the number of rows in the table with the given index.
   * Tables are indexed from 0 to <code>{@link getTableCount()} - 1</code>.
   */
  public int getRowCount(int tableIndex) {
    return -1;
  }

  /**
   * Retrieve the number of rows in the named table.
   */
  public int getRowCount(String tableName) {
    return -1;
  }

  /**
   * Retrieve all of the tabular data for the table with the given index.
   * Tables are indexed from 0 to <code>{@link getTableCount()} - 1</code>.
   *
   * @see getColumnTypes(int)
   */
  public Object[][] getTableData(int tableIndex) {
    // TODO
    return null;
  }

  /**
   * Retrieve all of the tabular data for the named table.
   *
   * @see getColumnTypes(String)
   */
  public Object[][] getTableData(String tableName) {
    // TODO
    return null;
  }

  /**
   * Retrieve the given row of data from the table with the given index.
   * Tables are indexed from 0 to <code>{@link getTableCount()} - 1</code>.
   *
   * @see getColumnTypes(int)
   */
  public Object[] getRowData(int rowIndex, int tableIndex) {
    // TODO
    return null;
  }

  /**
   * Retrieve the given row of data from the named table.
   *
   * @see getColumnTypes(String)
   */
  public Object[] getRowData(int rowIndex, String tableName) {
    // TODO
    return null;
  }

}
