
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
 * Tests for reading unsigned bytes from a loci.common.IRandomAccess.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/test/loci/common/utests/ReadUnsignedByteTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/test/loci/common/utests/ReadUnsignedByteTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see loci.common.IRandomAccess
 */
@Test(groups="readTests")
public class ReadUnsignedByteTest {

  private static final byte[] PAGE = new byte[] {
    (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04,
    (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08,
    (byte) 0x09, (byte) 0x0A, (byte) 0x0B, (byte) 0x0C,
    (byte) 0x0D, (byte) 0x0E, (byte) 0xFF, (byte) 0xFE
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
  public void testSequentialReadUnsignedByte() throws IOException {
    assertEquals(1, fileHandle.readUnsignedByte());
    assertEquals(2, fileHandle.readUnsignedByte());
    assertEquals(3, fileHandle.readUnsignedByte());
    assertEquals(4, fileHandle.readUnsignedByte());
    assertEquals(5, fileHandle.readUnsignedByte());
    assertEquals(6, fileHandle.readUnsignedByte());
    assertEquals(7, fileHandle.readUnsignedByte());
    assertEquals(8, fileHandle.readUnsignedByte());
    assertEquals(9, fileHandle.readUnsignedByte());
    assertEquals(10, fileHandle.readUnsignedByte());
    assertEquals(11, fileHandle.readUnsignedByte());
    assertEquals(12, fileHandle.readUnsignedByte());
    assertEquals(13, fileHandle.readUnsignedByte());
    assertEquals(14, fileHandle.readUnsignedByte());
    assertEquals(255, fileHandle.readUnsignedByte());
    assertEquals(254, fileHandle.readUnsignedByte());
  }

  @Test
  public void testSeekForwardReadUnsignedByte() throws IOException {
    fileHandle.seek(7);
    assertEquals(8, fileHandle.readUnsignedByte());
    assertEquals(9, fileHandle.readUnsignedByte());
  }

  @Test
  public void testResetReadUnsignedByte() throws IOException {
    assertEquals(1, fileHandle.readUnsignedByte());
    assertEquals(2, fileHandle.readUnsignedByte());
    fileHandle.seek(0);
    assertEquals(1, fileHandle.readUnsignedByte());
    assertEquals(2, fileHandle.readUnsignedByte());
  }

  @Test
  public void testSeekBackReadUnsignedByte() throws IOException {
    fileHandle.seek(15);
    fileHandle.seek(7);
    assertEquals(8, fileHandle.readUnsignedByte());
    assertEquals(9, fileHandle.readUnsignedByte());
  }

  @Test
  public void testRandomAccessReadUnsignedByte() throws IOException {
    testSeekForwardReadUnsignedByte();
    testSeekBackReadUnsignedByte();
    // The test relies on a "new" file or reset file pointer
    fileHandle.seek(0);
    testResetReadUnsignedByte();
  }

}
