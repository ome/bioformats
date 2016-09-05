/*
 * #%L
 * OME Metakit package for reading Metakit database files.
 * %%
 * Copyright (C) 2011 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package ome.metakit;

import java.io.IOException;

import loci.common.DataTools;
import loci.common.RandomAccessInputStream;

/**
 * Top-level reader for Metakit database files.
 * See http://equi4.com/metakit/metakit-ff.html for basic documentation on the
 * Metakit file format.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class MetakitReader {

  // -- Fields --

  private RandomAccessInputStream stream;

  private String[] tableNames;
  private Column[][] columns;
  private int[] rowCount;

  private Object[][][] data;

  private boolean littleEndian = false;

  // -- Constructors --

  public MetakitReader(String file) throws IOException, MetakitException {
    this(new RandomAccessInputStream(file));
  }

  public MetakitReader(RandomAccessInputStream stream) throws MetakitException {
    this.stream = stream;
    try {
      initialize();
    }
    catch (IOException e) {
      throw new MetakitException(e);
    }
  }

  // -- MetakitReader API methods --

  /**
   * Close the reader and release any resources in use.
   */
  public void close() {
    try {
      if (stream != null) {
        stream.close();
      }
      stream = null;
      tableNames = null;
      columns = null;
      rowCount = null;
      data = null;
      littleEndian = false;
    }
    catch (IOException e) { }
  }

  /**
   * Retrieve the number of tables in this database file.
   */
  public int getTableCount() {
    return tableNames.length;
  }

  /**
   * Retrieve the name of every table in this database file.
   * The length of the returned array is equivalent to
   * {@link #getTableCount()}.
   */
  public String[] getTableNames() {
    return tableNames;
  }

  /**
   * Retrieve the name of every column in the table with the given index.
   * Tables are indexed from 0 to <code>{@link #getTableCount()} - 1</code>.
   */
  public String[] getColumnNames(int tableIndex) {
    String[] columnNames = new String[columns[tableIndex].length];
    for (int i=0; i<columnNames.length; i++) {
      columnNames[i] = columns[tableIndex][i].getName();
    }
    return columnNames;
  }

  /**
   * Retrieve the name of every column in the named table.
   */
  public String[] getColumnNames(String tableName) {
    int index = DataTools.indexOf(tableNames, tableName);
    if (index < 0) {
      return null;
    }
    return getColumnNames(index);
  }

  /**
   * Retrieve the type for every column in the table with the given index.
   * Tables are indexed from 0 to <code>{@link #getTableCount()} - 1</code>.
   *
   * Every Object in the arrays returned by {@link #getTableData(int)} and
   * {@link #getTableData(String)} will be an instance of the corresponding
   * Class in the Class[] returned by this method.
   */
  public Class[] getColumnTypes(int tableIndex) {
    Class[] types = new Class[columns[tableIndex].length];
    for (int i=0; i<types.length; i++) {
      types[i] = columns[tableIndex][i].getType();
    }
    return types;
  }

  /**
   * Retrieve the type for every column in the named table.
   *
   * Every Object in the arrays returned by {@link #getTableData(int)} and
   * {@link #getTableData(String)} will be an instance of the corresponding
   * Class in the Class[] returned by this method.
   */
  public Class[] getColumnTypes(String tableName) {
    int index = DataTools.indexOf(tableNames, tableName);
    if (index < 0) {
      return null;
    }
    return getColumnTypes(index);
  }

  /**
   * Retrieve the number of rows in the table with the given index.
   * Tables are indexed from 0 to <code>{@link #getTableCount()} - 1</code>.
   */
  public int getRowCount(int tableIndex) {
    return rowCount[tableIndex];
  }

  /**
   * Retrieve the number of rows in the named table.
   */
  public int getRowCount(String tableName) {
    return getRowCount(DataTools.indexOf(tableNames, tableName));
  }

  /**
   * Retrieve all of the tabular data for the table with the given index.
   * Tables are indexed from 0 to <code>{@link #getTableCount()} - 1</code>.
   *
   * @see #getColumnTypes(int)
   */
  public Object[][] getTableData(int tableIndex) {
    Object[][] table = data[tableIndex];
    if (table == null) return null;

    // table is stored in [column][row] order; reverse it for convenience
    int rowCount = table[0] == null ? 0 : table[0].length;
    Object[][] newTable = new Object[rowCount][table.length];

    for (int row=0; row<newTable.length; row++) {
      for (int col=0; col<newTable[row].length; col++) {
        if (col < table.length && row < table[col].length) {
          newTable[row][col] = table[col][row];
        }
      }
    }

    return newTable;
  }

  /**
   * Retrieve all of the tabular data for the named table.
   *
   * @see #getColumnTypes(String)
   */
  public Object[][] getTableData(String tableName) {
    int index = DataTools.indexOf(tableNames, tableName);
    if (index < 0) {
      return null;
    }
    return getTableData(index);
  }

  /**
   * Retrieve the given row of data from the table with the given index.
   * Tables are indexed from 0 to <code>{@link #getTableCount()} - 1</code>.
   *
   * @see #getColumnTypes(int)
   */
  public Object[] getRowData(int rowIndex, int tableIndex) {
    Object[] row = new Object[data[tableIndex].length];
    for (int col=0; col<data[tableIndex].length; col++) {
      row[col] = data[tableIndex][col][rowIndex];
    }
    return row;
  }

  /**
   * Retrieve the given row of data from the named table.
   *
   * @see #getColumnTypes(String)
   */
  public Object[] getRowData(int rowIndex, String tableName) {
    int index = DataTools.indexOf(tableNames, tableName);
    if (index < 0) {
      return null;
    }
    return getRowData(rowIndex, index);
  }

  // -- Helper methods --

  /**
   * Read the tables for the current database file.
   * @throws IOException if the file could not be read
   * @throws MetakitException if the file is not valid for the Metakit format
   */
  private void initialize() throws IOException, MetakitException {
    String magic = stream.readString(2);

    if (magic.equals("JL")) {
      littleEndian = true;
    }
    else if (!magic.equals("LJ")) {
      throw new MetakitException("Invalid magic string; got " + magic);
    }

    boolean valid = stream.read() == 26;
    if (!valid) {
      throw new MetakitException("'valid' flag was set to 'false'");
    }

    int headerType = stream.read();
    if (headerType != 0) {
      throw new MetakitException(
        "Header type " + headerType + " is not valid.");
    }

    long footerPointer = stream.readInt() - 16;

    stream.seek(footerPointer);
    readFooter();
  }

  /**
   * Read the footer data for the current database file.
   * @throws IOException if the footer cannot be read from the file
   * @throws MetakitException if the footer is not valid for the Metakit format
   */
  private void readFooter() throws IOException, MetakitException {
    stream.skipBytes(4);

    long headerLocation = stream.readInt();
    stream.skipBytes(4);

    long tocLocation = stream.readInt();

    stream.seek(tocLocation);
    readTOC();
  }

  /**
   * Reads the table of contents (TOC) for the current database file.
   * @throws IOException if the footer cannot be read from the file
   * @throws MetakitException if the footer is not valid for the Metakit format
   */
  private void readTOC() throws IOException, MetakitException {
    int tocMarker = MetakitTools.readBpInt(stream);
    String structureDefinition = MetakitTools.readPString(stream);

    String[] tables = structureDefinition.split("],");
    tableNames = new String[tables.length];

    columns = new Column[tables.length][];
    boolean[] hasSubviews = new boolean[tables.length];

    for (int i=0; i<tables.length; i++) {
      String table = tables[i];
      int openBracket = table.indexOf('[');
      tableNames[i] = table.substring(0, openBracket);
      String columnList = table.substring(openBracket + 1);

      openBracket = columnList.indexOf('[');
      hasSubviews[i] = openBracket >= 0;

      columnList = columnList.substring(openBracket + 1);
      String[] cols = columnList.split(",");
      columns[i] = new Column[cols.length];

      for (int col=0; col<cols.length; col++) {
        columns[i][col] = new Column(cols[col]);
      }
    }

    rowCount = new int[tables.length];

    MetakitTools.readBpInt(stream);

    data = new Object[tables.length][][];

    for (int table=0; table<tables.length; table++) {
      MetakitTools.readBpInt(stream);
      int pointer = MetakitTools.readBpInt(stream);
      long fp = stream.getFilePointer();
      stream.seek(pointer + 1);

      rowCount[table] = MetakitTools.readBpInt(stream);
      if (hasSubviews[table]) {
        int subviewCount = rowCount[table];

        long base = stream.getFilePointer();
        rowCount[table] = 0;
        Object[][][] subviewTable =
          new Object[subviewCount][columns[table].length][];

        for (int subview=0; subview<subviewCount; subview++) {
          // read an IVecRef

          if (subview == 0) {
            int size = MetakitTools.readBpInt(stream);
            long subviewPointer = MetakitTools.readBpInt(stream);
            base = stream.getFilePointer();

            stream.seek(subviewPointer);
          }

          MetakitTools.readBpInt(stream); // 0x80

          int count = MetakitTools.readBpInt(stream);

          if (count > 1) {
            rowCount[table] += count;
            for (int col=0; col<columns[table].length; col++) {
              stream.order(littleEndian);
              ColumnMap map = new ColumnMap(columns[table][col], stream, count);
              subviewTable[subview][col] = map.getValues();
            }
          }
        }

        data[table] = new Object[columns[table].length][rowCount[table]];
        int index = 0;
        for (int subview=0; subview<subviewCount; subview++) {
          if (subviewTable[subview][0] != null) {
            for (int col=0; col<columns[table].length; col++) {
              System.arraycopy(subviewTable[subview][col], 0, data[table][col],
                index, subviewTable[subview][col].length);
            }
            index += subviewTable[subview][0].length;
          }
        }
      }
      else {
        data[table] = new Object[columns[table].length][];
        if (rowCount[table] > 0) {
          for (int col=0; col<columns[table].length; col++) {
            stream.order(littleEndian);
            ColumnMap map =
              new ColumnMap(columns[table][col], stream, rowCount[table]);
            data[table][col] = map.getValues();
          }
        }
      }
      stream.seek(fp);
    }
  }

}
