
package loci.common.utests;

import static org.testng.AssertJUnit.assertEquals;

import java.io.EOFException;
import java.io.IOException;

import loci.common.HandleException;
import loci.common.URLHandle;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Unit tests for the loci.common.URLHandle class.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/test/loci/common/utests/URLHandleTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/test/loci/common/utests/URLHandleTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see loci.common.URLHandle
 */
public class URLHandleTest {

  // -- Constants --

  /** The contents are "hello, world!". */
  private static final String WEBSITE =
    "http://dev.loci.wisc.edu/hello-world";

  // -- Fields --

  private URLHandle fileHandle;

  // -- Setup methods --

  @BeforeMethod
  public void setup() throws IOException {
    fileHandle = new URLHandle(WEBSITE);
  }

  // -- Test methods --

  @Test
  public void testLength() throws IOException {
    assertEquals(14, fileHandle.length());
  }

  @Test
  public void testSequentialReadByte() throws IOException {
    fileHandle.seek(0);
    assertEquals(0x68, fileHandle.readByte());
    assertEquals(0x65, fileHandle.readByte());
    assertEquals(0x6c, fileHandle.readByte());
    assertEquals(0x6c, fileHandle.readByte());
    assertEquals(0x6f, fileHandle.readByte());
    assertEquals(0x2c, fileHandle.readByte());
    assertEquals(0x20, fileHandle.readByte());
    assertEquals(0x77, fileHandle.readByte());
    assertEquals(0x6f, fileHandle.readByte());
    assertEquals(0x72, fileHandle.readByte());
    assertEquals(0x6c, fileHandle.readByte());
    assertEquals(0x64, fileHandle.readByte());
    assertEquals(0x21, fileHandle.readByte());
    assertEquals(0x0a, fileHandle.readByte());
  }

  @Test
  public void testSequentialReadShort() throws IOException {
    fileHandle.seek(0);
    assertEquals(0x6865, fileHandle.readShort());
    assertEquals(0x6c6c, fileHandle.readShort());
    assertEquals(0x6f2c, fileHandle.readShort());
    assertEquals(0x2077, fileHandle.readShort());
    assertEquals(0x6f72, fileHandle.readShort());
    assertEquals(0x6c64, fileHandle.readShort());
    assertEquals(0x210a, fileHandle.readShort());
  }

  @Test
  public void testSequentialReadInt() throws IOException {
    fileHandle.seek(0);
    assertEquals(0x68656c6c, fileHandle.readInt());
    assertEquals(0x6f2c2077, fileHandle.readInt());
    assertEquals(0x6f726c64, fileHandle.readInt());
  }

  @Test
  public void testSequentialReadLong() throws IOException {
    fileHandle.seek(0);
    assertEquals(0x68656c6c6f2c2077L, fileHandle.readLong());
  }

  @Test
  public void testSeekForwardReadByte() throws IOException {
    fileHandle.seek(5);
    assertEquals(0x2c, fileHandle.readByte());
  }

  @Test
  public void testSeekForwardReadShort() throws IOException {
    fileHandle.seek(5);
    assertEquals(0x2c20, fileHandle.readShort());
  }

  @Test
  public void testSeekForwardReadInt() throws IOException {
    fileHandle.seek(5);
    assertEquals(0x2c20776f, fileHandle.readInt());
  }

  @Test
  public void testSeekForwardReadLong() throws IOException {
    fileHandle.seek(5);
    assertEquals(0x2c20776f726c6421L, fileHandle.readLong());
  }

  @Test
  public void testSeekBackReadByte() throws IOException {
    fileHandle.seek(13);
    fileHandle.seek(7);
    assertEquals(0x77, fileHandle.readByte());
  }

  @Test
  public void testSeekBackReadShort() throws IOException {
    fileHandle.seek(13);
    fileHandle.seek(7);
    assertEquals(0x776f, fileHandle.readShort());
  }

  @Test
  public void testSeekBackReadInt() throws IOException {
    fileHandle.seek(13);
    fileHandle.seek(7);
    assertEquals(0x776f726c, fileHandle.readInt());
  }

  @Test
  public void testSeekBackReadLong() throws IOException {
    fileHandle.seek(13);
    fileHandle.seek(5);
    assertEquals(0x2c20776f726c6421L, fileHandle.readLong());
  }

  @Test (expectedExceptions = {EOFException.class})
  public void testEOF() throws IOException {
    fileHandle.seek(16);
    fileHandle.readByte();
  }

  @Test (expectedExceptions = {HandleException.class})
  public void testWrite() throws IOException {
    fileHandle.write(0);
  }

}
