/*
 * #%L
 * OME Metakit package for reading Metakit database files.
 * %%
 * Copyright (C) 2011 - 2015 Open Microscopy Environment:
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

package ome.metakit.utests;

import java.io.IOException;

import ome.metakit.MetakitException;
import ome.metakit.MetakitReader;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class TableTest {

  private static final String FILENAME = "test.mk";
  private MetakitReader reader;

  @BeforeMethod
  public void setUp() throws IOException, MetakitException {
    String defaultFile = this.getClass().getResource(FILENAME).getPath();
    reader = new MetakitReader(System.getProperty("filename", defaultFile));
  }

  @Test
  public void testTableCount() {
    int tableCount = reader.getTableCount();
    assertTrue(tableCount >= 0);
    assertEquals(tableCount, reader.getTableNames().length);
  }

  @Test
  public void testValidTableNames() {
    String[] tables = reader.getTableNames();
    assertNotNull(tables);
    for (String tableName : tables) {
      assertNotNull(tableName);
    }
  }

}
