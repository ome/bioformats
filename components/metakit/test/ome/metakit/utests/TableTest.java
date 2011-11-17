//
// TableTest.java
//

/*
OME Metakit package for reading Metakit database files.
Copyright (C) 2011-@year@ UW-Madison LOCI and Glencoe Software, Inc.

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
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/metakit/test/ome/metakit/utests/TableTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/metakit/test/ome/metakit/utests/TableTest.java;hb=HEAD">Gitweb</a></dd></dl>
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
