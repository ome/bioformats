
package loci.common.utests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.fail;

import java.io.IOException;

import loci.common.IRandomAccess;
import loci.common.utests.providers.IRandomAccessProvider;
import loci.common.utests.providers.IRandomAccessProviderFactory;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Tests for writing bytes to a loci.common.IRandomAccess.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/test/loci/common/utests/WriteByteArrayTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/test/loci/common/utests/WriteByteArrayTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see loci.common.IRandomAccess
 */
@Test(groups="writeTests")
public class WriteByteArrayTest {

  private static final byte[] PAGE = new byte[] {
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
  };

  private static final String MODE = "rw";

  private static final int BUFFER_SIZE = 1024;

  private IRandomAccess fileHandle;
  
  private boolean checkGrowth;

  @Parameters({"provider", "checkGrowth"})
  @BeforeMethod
  public void setUp(String provider, @Optional("false") String checkGrowth)
    throws IOException {
    this.checkGrowth = Boolean.parseBoolean(checkGrowth);
    IRandomAccessProviderFactory factory = new IRandomAccessProviderFactory();
    IRandomAccessProvider instance = factory.getInstance(provider);
    fileHandle = instance.createMock(PAGE, MODE, BUFFER_SIZE);
  }

  @Test(groups="initialLengthTest")
  public void testLength() throws IOException {
    assertEquals(16, fileHandle.length());
  }
  
  @Test
  public void testWriteSequential() throws IOException {
    for (int i = 0; i < 16; i++) {
      fileHandle.write(new byte[] { (byte) (i + 1) });
      if (checkGrowth) {
        assertEquals(i + 1, fileHandle.length());
      }
    }
    assertEquals(16, fileHandle.length());
    fileHandle.seek(0);
    for (int i = 0; i < 16; i++) {
      assertEquals(i + 1, fileHandle.readByte());
    }
  }

  @Test
  public void testWrite() throws IOException {
    fileHandle.write(new byte[] { (byte) 1 });
    assertEquals(1, fileHandle.getFilePointer());
    if (checkGrowth) {
      assertEquals(1, fileHandle.length());
    }
    fileHandle.seek(0);
    assertEquals(1, fileHandle.readByte());
  }

  @Test
  public void testWriteSequentialSubArray() throws IOException {
    for (int i = 0; i < 16; i++) {
      fileHandle.write(new byte[] { (byte) (0), (byte) (i + 1) }, 1, 1);
      if (checkGrowth) {
        assertEquals(i + 1, fileHandle.length());
      }
    }
    assertEquals(16, fileHandle.length());
    fileHandle.seek(0);
    for (int i = 0; i < 16; i++) {
      assertEquals(i + 1, fileHandle.readByte());
    }
  }

  @Test
  public void testWriteSubArray() throws IOException {
    fileHandle.write(new byte[] { (byte) (0), (byte) 1 }, 1, 1);
    assertEquals(1, fileHandle.getFilePointer());
    if (checkGrowth) {
      assertEquals(1, fileHandle.length());
    }
    fileHandle.seek(0);
    assertEquals(1, fileHandle.readByte());
  }

  @Test
  public void testWriteTwoByteSubArray() throws IOException {
    byte[] buf = new byte[] { 1, 2, 3, 4 };
    fileHandle.write(buf, 1, 2);
    assertEquals(2, fileHandle.getFilePointer());
    if (checkGrowth) {
      assertEquals(2, fileHandle.length());
    }
    fileHandle.seek(0);
    assertEquals(2, fileHandle.readByte());
    assertEquals(3, fileHandle.readByte());
    if (fileHandle.length() > 2 && fileHandle.readByte() == 4) {
      fail("Incorrect length or trailing bytes.");
    }
  }

}
