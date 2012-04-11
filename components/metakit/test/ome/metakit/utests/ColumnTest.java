
package ome.metakit.utests;

import java.io.IOException;

import ome.metakit.MetakitException;
import ome.metakit.MetakitReader;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/metakit/test/ome/metakit/utests/ColumnTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/metakit/test/ome/metakit/utests/ColumnTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class ColumnTest {

  private static final String FILENAME = "test.mk";
  private static final String INVALID_TABLE = "this cannot be a valid table";

  private MetakitReader reader;

  @BeforeMethod
  public void setUp() throws IOException, MetakitException {
    String defaultFile = this.getClass().getResource(FILENAME).getPath();
    reader = new MetakitReader(System.getProperty("filename", defaultFile));
  }

  @Test
  public void testValidColumnNames() {
    String[] tableNames = reader.getTableNames();
    for (int i=0; i<tableNames.length; i++) {
      String[] namesByIndex = reader.getColumnNames(i);
      String[] namesByName = reader.getColumnNames(tableNames[i]);

      assertNotNull(namesByIndex);
      assertNotNull(namesByName);
      assertEquals(namesByIndex.length, namesByName.length);
      for (int j=0; j<namesByIndex.length; j++) {
        assertNotNull(namesByIndex[j]);
        assertEquals(namesByIndex[j], namesByName[j]);
      }
    }
  }

  @Test
  public void testValidColumnTypes() {
    String[] tableNames = reader.getTableNames();
    for (int i=0; i<tableNames.length; i++) {
      Class[] typesByIndex = reader.getColumnTypes(i);
      Class[] typesByName = reader.getColumnTypes(tableNames[i]);

      assertNotNull(typesByIndex);
      assertNotNull(typesByName);
      assertEquals(typesByIndex.length, typesByName.length);
      for (int j=0; j<typesByIndex.length; j++) {
        assertNotNull(typesByIndex[j]);
        assertEquals(typesByIndex[j], typesByName[j]);
      }
    }
  }

  @Test
  public void testColumnCounts() {
    for (int i=0; i<reader.getTableCount(); i++) {
      String[] names = reader.getColumnNames(i);
      Class[] types = reader.getColumnTypes(i);

      assertNotNull(names);
      assertNotNull(types);
      assertEquals(names.length, types.length);
    }
  }

  @Test(expectedExceptions={ArrayIndexOutOfBoundsException.class})
  public void testColumnNameTableIndexTooSmall() {
    reader.getColumnNames(-1);
  }

  @Test(expectedExceptions={ArrayIndexOutOfBoundsException.class})
  public void testColumnTypeTableIndexTooSmall() {
    reader.getColumnTypes(-1);
  }


  @Test(expectedExceptions={ArrayIndexOutOfBoundsException.class})
  public void testColumnNameTableIndexTooLarge() {
    reader.getColumnNames(reader.getTableCount());
  }

  @Test(expectedExceptions={ArrayIndexOutOfBoundsException.class})
  public void testColumnTypeTableIndexTooLarge() {
    reader.getColumnTypes(reader.getTableCount());
  }

  @Test
  public void testColumnNameInvalidTableNames() {
    assertNull(reader.getColumnNames(null));
    assertNull(reader.getColumnNames(INVALID_TABLE));
  }

  @Test
  public void testColumnTypeInvalidTableNames() {
    assertNull(reader.getColumnTypes(null));
    assertNull(reader.getColumnTypes(INVALID_TABLE));
  }

}
