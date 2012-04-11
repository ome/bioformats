
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
