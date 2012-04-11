
package loci.common.utests;

import static org.testng.AssertJUnit.assertEquals;

import java.io.IOException;

import loci.common.IRandomAccess;
import loci.common.utests.providers.IRandomAccessProvider;
import loci.common.utests.providers.IRandomAccessProviderFactory;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Tests for reading floats from a loci.common.IRandomAccess.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/test/loci/common/utests/ReadFloatTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/test/loci/common/utests/ReadFloatTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see loci.common.IRandomAccess
 */
@Test(groups="readTests")
public class ReadFloatTest {

  private static final byte[] PAGE = new byte[] {
    // 0.0 (0x00000000)
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    // 1.0 (0x3F800000)
    (byte) 0x3F, (byte) 0x80, (byte) 0x00, (byte) 0x00,
    // -1.0 (0xBF800000)
    (byte) 0xBF, (byte) 0x80, (byte) 0x00, (byte) 0x00,
    // 3.1415927 (0x40490FDB)
    (byte) 0x40, (byte) 0x49, (byte) 0x0F, (byte) 0xDB,
    // MAX_VALUE (0x7F7FFFFF)
    (byte) 0x7F, (byte) 0x7F, (byte) 0xFF, (byte) 0xFF,
    // NEGATIVE_INFINITY (0xFF800000)
    (byte) 0xFF, (byte) 0x80, (byte) 0x00, (byte) 0x00,
    // NaN (0x7FC00000)
    (byte) 0x7F, (byte) 0xC0, (byte) 0x00, (byte) 0x00
  };

  private static final String MODE = "r";

  private static final int BUFFER_SIZE = 1024;

  private IRandomAccess fileHandle;

  @Parameters({"provider"})
  @BeforeMethod
  public void setUp(String provider) throws IOException {
    IRandomAccessProviderFactory factory = new IRandomAccessProviderFactory();
    IRandomAccessProvider instance = factory.getInstance(provider);
    fileHandle = instance.createMock(PAGE, MODE, BUFFER_SIZE);
  }

  @Test
  public void testLength() throws IOException {
    assertEquals(28, fileHandle.length());
  }

  @Test
  public void testSequential() throws IOException {
    assertEquals(0.0f, fileHandle.readFloat());
    assertEquals(1.0f, fileHandle.readFloat());
    assertEquals(-1.0f, fileHandle.readFloat());
    assertEquals(3.1415927f, fileHandle.readFloat());
    assertEquals(Float.MAX_VALUE, fileHandle.readFloat());
    assertEquals(Float.NEGATIVE_INFINITY, fileHandle.readFloat());
    assertEquals(Float.NaN, fileHandle.readFloat());
  }

  @Test
  public void testSeekForward() throws IOException {
    fileHandle.seek(8);
    assertEquals(-1.0f, fileHandle.readFloat());
    assertEquals(3.1415927f, fileHandle.readFloat());
  }

  @Test
  public void testReset() throws IOException {
    assertEquals(0.0f, fileHandle.readFloat());
    assertEquals(1.0f, fileHandle.readFloat());
    fileHandle.seek(0);
    assertEquals(0.0f, fileHandle.readFloat());
    assertEquals(1.0f, fileHandle.readFloat());
  }

  @Test
  public void testSeekBack() throws IOException {
    fileHandle.seek(16);
    fileHandle.seek(8);
    assertEquals(-1.0f, fileHandle.readFloat());
    assertEquals(3.1415927f, fileHandle.readFloat());
  }

  @Test
  public void testRandomAccess() throws IOException {
    testSeekForward();
    testSeekBack();
    // The test relies on a "new" file or reset file pointer
    fileHandle.seek(0);
    testReset();
  }

}
