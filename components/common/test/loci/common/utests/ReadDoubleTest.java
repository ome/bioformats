
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
 * Tests for reading doubles from a loci.common.IRandomAccess.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/test/loci/common/utests/ReadDoubleTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/test/loci/common/utests/ReadDoubleTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see loci.common.IRandomAccess
 */
@Test(groups="readTests")
public class ReadDoubleTest {

  private static final byte[] PAGE = new byte[] {
    // 0.0 (0x0000000000000000L)
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    // 1.0 (0x3FF0000000000000L)
    (byte) 0x3F, (byte) 0xF0, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    // -1.0 (0XBFF0000000000000L)
    (byte) 0xBF, (byte) 0xF0, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    // 3.1415926535897930 (0x400921FB54442D18L)
    (byte) 0x40, (byte) 0x09, (byte) 0x21, (byte) 0xFB,
    (byte) 0x54, (byte) 0x44, (byte) 0x2D, (byte) 0x18,
    // MAX_VALUE (0x7FEFFFFFFFFFFFFFL)
    (byte) 0x7F, (byte) 0xEF, (byte) 0xFF, (byte) 0xFF,
    (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
    // NEGATIVE_INFINITY (0xFFF0000000000000L)
    (byte) 0xFF, (byte) 0xF0, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    // NaN (0x7FF8000000000000L)
    (byte) 0x7F, (byte) 0xF8, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
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
    assertEquals(56, fileHandle.length());
  }

  @Test
  public void testSequential() throws IOException {
    assertEquals(0.0d, fileHandle.readDouble());
    assertEquals(1.0d, fileHandle.readDouble());
    assertEquals(-1.0d, fileHandle.readDouble());
    assertEquals(3.1415926535897930d, fileHandle.readDouble());
    assertEquals(Double.MAX_VALUE, fileHandle.readDouble());
    assertEquals(Double.NEGATIVE_INFINITY, fileHandle.readDouble());
    assertEquals(Double.NaN, fileHandle.readDouble());
  }

  @Test
  public void testSeekForward() throws IOException {
    fileHandle.seek(16);
    assertEquals(-1.0d, fileHandle.readDouble());
    assertEquals(3.1415926535897930d, fileHandle.readDouble());
  }

  @Test
  public void testReset() throws IOException {
    assertEquals(0.0d, fileHandle.readDouble());
    assertEquals(1.0d, fileHandle.readDouble());
    fileHandle.seek(0);
    assertEquals(0.0d, fileHandle.readDouble());
    assertEquals(1.0d, fileHandle.readDouble());
  }

  @Test
  public void testSeekBack() throws IOException {
    fileHandle.seek(32);
    fileHandle.seek(16);
    assertEquals(-1.0d, fileHandle.readDouble());
    assertEquals(3.1415926535897930d, fileHandle.readDouble());
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
