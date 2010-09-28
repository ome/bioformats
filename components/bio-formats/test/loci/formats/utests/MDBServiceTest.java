//
// MDBServiceTest.java
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

package loci.formats.utests;

import static org.testng.AssertJUnit.assertEquals;

import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import loci.common.services.DependencyException;
import loci.common.services.ServiceFactory;
import loci.formats.services.MDBService;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author callan
 *
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://dev.loci.wisc.edu/trac/java/browser/trunk/components/bio-formats/test/loci/formats/utests/MDBServiceTest.java">Trac</a>,
 * <a href="http://dev.loci.wisc.edu/svn/java/trunk/components/bio-formats/test/loci/formats/utests/MDBServiceTest.java">SVN</a></dd></dl>
 */
public class MDBServiceTest {

  private static final String[] COLUMNS = new String[] {
    "ID", "text column", "number column", "date column", "currency column",
    "boolean column", "OLE column", "memo column"
  };

  private static final String[][] ROWS = new String[][] {
    {"1", "row 1, column 1", "1", "3/14/1999 0:0:0", "6.6600", "1", null, "foo"},
    {"2", "row 2, column 1", "2", "8/1/2008 0:0:0", "0.3700", "0", null, "bar"},
    {"3", "row 3, column 1", "3", "9/21/2001 0:0:0", "1000000.0000", "1", null, "baz"},
  };

  private static final String TEST_FILE = "test.mdb";

  private MDBService service;

  @BeforeMethod
  public void setUp() throws DependencyException, IOException {
    ServiceFactory sf = new ServiceFactory();
    service = sf.getInstance(MDBService.class);
    URL file = this.getClass().getResource(TEST_FILE);
    service.initialize(file.getPath());
  }

  @Test
  public void testData() throws IOException {
    Vector<Vector<String[]>> data = service.parseDatabase();

    assertEquals(1, data.size());

    Vector<String[]> table = data.get(0);
    assertEquals(4, table.size());

    String[] columnNames = table.get(0);
    assertEquals(COLUMNS.length + 1, columnNames.length);
    assertEquals("test table", columnNames[0]);

    for (int i=1; i<columnNames.length; i++) {
      assertEquals(columnNames[i], COLUMNS[i - 1]);
    }

    for (int i=1; i<table.size(); i++) {
      String[] row = table.get(i);
      for (int col=0; col<row.length; col++) {
        assertEquals(ROWS[i - 1][col], row[col]);
      }
    }
  }

}
