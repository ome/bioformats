/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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

import ome.metakit.MetakitException;
import ome.metakit.MetakitReader;

/**
 * Implementation of MetakitService for interacting with the
 * OME Metakit library.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/services/MetakitServiceImpl.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/services/MetakitServiceImpl.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class MetakitServiceImpl implements MetakitService {

  // -- Fields --

  private MetakitReader reader;

  // -- MetakitService API methods --

  /* @see loci.formats.services.MetakitException */
  public void initialize(String file) throws IOException {
    try {
      reader = new MetakitReader(file);
    }
    catch (MetakitException e) { }
  }

  /* @see loci.formats.services.MetakitService */
  public void initialize(RandomAccessInputStream file) {
    try {
      reader = new MetakitReader(file);
    }
    catch (MetakitException e) { }
  }

  /* @see loci.formats.services.MetakitService#getTableCount() */
  public int getTableCount() {
    return reader.getTableCount();
  }

  /* @see loci.formats.services.MetakitService#getTableNames() */
  public String[] getTableNames() {
    return reader.getTableNames();
  }

  /* @see loci.formats.services.MetakitService#getColumnNames(String) */
  public String[] getColumnNames(String table) {
    return reader.getColumnNames(table);
  }

  /* @see loci.formats.services.MetakitService#getColumnNames(int) */
  public String[] getColumNames(int table) {
    return reader.getColumnNames(table);
  }

  /* @see loci.formats.services.MetakitService#getTableData(String) */
  public Object[][] getTableData(String table) {
    return reader.getTableData(table);
  }

  /* @see loci.formats.services.MetakitService#getTableData(int) */
  public Object[][] getTableData(int table) {
    return reader.getTableData(table);
  }

  /* @see loci.formats.services.MetakitService#getRowData(int, String) */
  public Object[] getRowData(int row, String table) {
    return reader.getRowData(row, table);
  }

  /* @see loci.formats.services.MetakitService#getRowData(int, int) */
  public Object[] getRowData(int row, int table) {
    return reader.getRowData(row, table);
  }

  /* @see loci.formats.services.MetakitService#getRowCount(int) */
  public int getRowCount(int table) {
    return reader.getRowCount(table);
  }

  /* @see loci.formats.services.MetakitService#getRowCount(String) */
  public int getRowCount(String table) {
    return reader.getRowCount(table);
  }

  /* @see loci.formats.services.MetakitService#getColumnTypes(int) */
  public Class[] getColumnTypes(int table) {
    return reader.getColumnTypes(table);
  }

  /* @see loci.formats.services.MetakitService#getColumnTypes(String) */
  public Class[] getColumnTypes(String table) {
    return reader.getColumnTypes(table);
  }

  /* @see loci.formats.services.MetakitService#close() */
  public void close() {
    reader.close();
  }

}
