
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
 * Tests for reading shorts from a loci.common.IRandomAccess.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/test/loci/common/utests/ReadLineTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/test/loci/common/utests/ReadLineTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see loci.common.IRandomAccess
 */
@Test(groups={"readTests", "readLineTest"})
public class ReadLineTest {

  private static final byte[] PAGE = new byte[] {
    // a, b, c, \n
    (byte) 0x61, (byte) 0x62, (byte) 0x63, (byte) 0x0A,
    // d, e, f, \n
    (byte) 0x64, (byte) 0x65, (byte) 0x66, (byte) 0x0A,
    // g, h, i, \n
    (byte) 0x67, (byte) 0x68, (byte) 0x69, (byte) 0x0A,
    // j, k, l, \n
    (byte) 0x6A, (byte) 0x6B, (byte) 0x6C, (byte) 0x0A
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
    assertEquals(16, fileHandle.length());
  }

  @Test
  public void testSequential() throws IOException {
    assertEquals("abc", fileHandle.readLine());
    assertEquals(4, fileHandle.getFilePointer());
    assertEquals("def", fileHandle.readLine());
    assertEquals(8, fileHandle.getFilePointer());
    assertEquals("ghi", fileHandle.readLine());
    assertEquals(12, fileHandle.getFilePointer());
    assertEquals("jkl", fileHandle.readLine());
    assertEquals(16, fileHandle.getFilePointer());
  }

  @Test
  public void testSeekForward() throws IOException {
    fileHandle.seek(8);
    assertEquals("ghi", fileHandle.readLine());
    assertEquals(12, fileHandle.getFilePointer());
  }

  @Test
  public void testReset() throws IOException {
    assertEquals("abc", fileHandle.readLine());
    assertEquals(4, fileHandle.getFilePointer());
    assertEquals("def", fileHandle.readLine());
    assertEquals(8, fileHandle.getFilePointer());
    fileHandle.seek(0);
    assertEquals("abc", fileHandle.readLine());
    assertEquals(4, fileHandle.getFilePointer());
    assertEquals("def", fileHandle.readLine());
    assertEquals(8, fileHandle.getFilePointer());
  }

  @Test
  public void testSeekBack() throws IOException {
    fileHandle.seek(16);
    fileHandle.seek(8);
    assertEquals("ghi", fileHandle.readLine());
    assertEquals(12, fileHandle.getFilePointer());
    assertEquals("jkl", fileHandle.readLine());
    assertEquals(16, fileHandle.getFilePointer());
  }

  @Test
  public void testPartial() throws IOException {
    assertEquals((byte) 0x61, fileHandle.readByte());
    assertEquals(1, fileHandle.getFilePointer());
    assertEquals("bc", fileHandle.readLine());
    assertEquals((byte) 0x64, fileHandle.readByte());
  }

  @Test
  public void testRandomAccess() throws IOException {
    testSeekForward();
    testSeekBack();
    // The test relies on a "new" file or reset file pointer
    fileHandle.seek(0);
    testPartial();
    // The test relies on a "new" file or reset file pointer
    fileHandle.seek(0);
    testReset();
  }

}
