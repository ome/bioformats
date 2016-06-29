/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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

package loci.formats.services;

import java.io.IOException;

import loci.common.RandomAccessInputStream;
import loci.common.services.Service;

/**
 * Interface defining methods for interacting with the OME Metakit library.
 */
public interface MetakitService extends Service {

  /* @see ome.metakit.MetakitReader */
  public void initialize(String file) throws IOException;

  /* @see ome.metakit.MetakitReader */
  public void initialize(RandomAccessInputStream file) throws IOException;

  /* @see ome.metakit.MetakitReader#getTableCount() */
  public int getTableCount();

  /* @see ome.metakit.MetakitReader#getTableNames() */
  public String[] getTableNames();

  /* @see ome.metakit.MetakitReader#getColumnNames(String) */
  public String[] getColumnNames(String table);

  /* @see ome.metakit.MetakitReader#getColumnNames(int) */
  public String[] getColumNames(int table);

  /* @see ome.metakit.MetakitReader#getTableData(String) */
  public Object[][] getTableData(String table);

  /* @see ome.metakit.MetakitReader#getTableData(int) */
  public Object[][] getTableData(int table);

  /* @see ome.metakit.MetakitReader#getRowData(int, String) */
  public Object[] getRowData(int row, String table);

  /* @see ome.metakit.MetakitReader#getRowData(int, int) */
  public Object[] getRowData(int row, int table);

  /* @see ome.metakit.MetakitReader#getRowCount(int) */
  public int getRowCount(int table);

  /* @see ome.metakit.MetakitReader#getRowCount(String) */
  public int getRowCount(String table);

  /* @see ome.metakit.MetakitReader#getColumnTypes(int) */
  public Class[] getColumnTypes(int table);

  /* @see ome.metakit.MetakitReader#getColumnTypes(String) */
  public Class[] getColumnTypes(String table);

  /* @see ome.metakit.MetakitReader#close() */
  public void close();

}
