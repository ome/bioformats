
package loci.common.utests;

import static org.testng.AssertJUnit.assertEquals;

import java.io.IOException;
import java.nio.ByteBuffer;

import loci.common.IRandomAccess;
import loci.common.utests.providers.IRandomAccessProvider;
import loci.common.utests.providers.IRandomAccessProviderFactory;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Tests for reading bytes from a loci.common.IRandomAccess.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/test/loci/common/utests/ReadByteBufferTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/test/loci/common/utests/ReadByteBufferTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see loci.common.IRandomAccess
 */
@Test(groups="readTests")
public class ReadByteBufferTest {

  private static final byte[] PAGE = new byte[] {
    (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04,
    (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08,
    (byte) 0x09, (byte) 0x0A, (byte) 0x0B, (byte) 0x0C,
    (byte) 0x0D, (byte) 0x0E, (byte) 0xFF, (byte) 0xFE
  };

  private static final String MODE = "r";

  private static final int BUFFER_SIZE = 2;

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
  public void testSequentialReadByte() throws IOException {
    ByteBuffer b = ByteBuffer.allocate(16);
    int length = fileHandle.read(b);
    b.position(0);
    assertEquals(16, fileHandle.getFilePointer());
    assertEquals(16, length);
    assertEquals(0x01, b.get());
    assertEquals(0x02, b.get());
    assertEquals(0x03, b.get());
    assertEquals(0x04, b.get());
    assertEquals(0x05, b.get());
    assertEquals(0x06, b.get());
    assertEquals(0x07, b.get());
    assertEquals(0x08, b.get());
    assertEquals(0x09, b.get());
    assertEquals(0x0A, b.get());
    assertEquals(0x0B, b.get());
    assertEquals(0x0C, b.get());
    assertEquals(0x0D, b.get());
    assertEquals(0x0E, b.get());
    assertEquals((byte) 0xFF, b.get());
    assertEquals((byte) 0xFE, b.get());
  }

  @Test
  public void testSeekForwardReadByte() throws IOException {
    fileHandle.seek(7);
    ByteBuffer b = ByteBuffer.allocate(2);
    int length = fileHandle.read(b);
    b.position(0);
    assertEquals(9, fileHandle.getFilePointer());
    assertEquals(2, length);
    assertEquals(0x08, b.get());
    assertEquals(0x09, b.get());
  }

  @Test
  public void testResetReadByte() throws IOException {
    ByteBuffer b = ByteBuffer.allocate(2);
    int length = fileHandle.read(b);
    b.position(0);
    assertEquals(2, fileHandle.getFilePointer());
    assertEquals(0x02, length);
    assertEquals(0x01, b.get());
    assertEquals(0x02, b.get());
    fileHandle.seek(0);
    b.position(0);
    length = fileHandle.read(b);
    b.position(0);
    assertEquals(0x02, length);
    assertEquals(0x01, b.get());
    assertEquals(0x02, b.get());
  }

  @Test
  public void testSeekBackReadByte() throws IOException {
    fileHandle.seek(15);
    fileHandle.seek(7);
    ByteBuffer b = ByteBuffer.allocate(2);
    int length = fileHandle.read(b);
    b.position(0);
    assertEquals(9, fileHandle.getFilePointer());
    assertEquals(2, length);
    assertEquals(0x08, b.get());
    assertEquals(0x09, b.get());
  }

  @Test
  public void testRandomAccessReadByte() throws IOException {
    testSeekForwardReadByte();
    testSeekBackReadByte();
    // The test relies on a "new" file or reset file pointer
    fileHandle.seek(0);
    testResetReadByte();
  }

}
