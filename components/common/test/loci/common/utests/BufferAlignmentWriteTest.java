//
// BufferAlignmentWriteTest.java
//

/*
LOCI Common package: utilities for I/O, reflection and miscellaneous tasks.
Copyright (C) 2005-@year@ Melissa Linkert and Curtis Rueden.

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
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/test/loci/common/utests/BufferAlignmentWriteTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/test/loci/common/utests/BufferAlignmentWriteTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see loci.common.IRandomAccess
 */
@Test(groups="writeTests")
public class BufferAlignmentWriteTest {

  private static final byte[] PAGE = new byte[] {
    // 16-byte page
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
  };

  private static final String MODE = "rw";

  private static final int BUFFER_SIZE = 1024;

  private IRandomAccess fileHandle;

  @Parameters({"provider"})
  @BeforeMethod
  public void setUp(String provider) throws IOException {
    IRandomAccessProviderFactory factory = new IRandomAccessProviderFactory();
    IRandomAccessProvider instance = factory.getInstance(provider);
    fileHandle = instance.createMock(PAGE, MODE, BUFFER_SIZE);
  }

  @Test(groups="initialLengthTest")
  public void testLength() throws IOException {
    assertEquals(16, fileHandle.length());
  }

  @Test
  public void testWriteArrayOffEnd() throws IOException {
    fileHandle.seek(16);
    fileHandle.write(new byte[] { (byte) 1 });
    assertEquals(17, fileHandle.getFilePointer());
    assertEquals(17, fileHandle.length());
    fileHandle.seek(16);
    assertEquals(1, fileHandle.readByte());
  }

  @Test
  public void testWriteArrayTwiceOffEnd() throws IOException {
    fileHandle.seek(16);
    fileHandle.write(new byte[] { (byte) 1 });
    assertEquals(17, fileHandle.getFilePointer());
    assertEquals(17, fileHandle.length());
    fileHandle.write(new byte[] { (byte) 1 });
    assertEquals(18, fileHandle.getFilePointer());
    assertEquals(18, fileHandle.length());
    fileHandle.seek(16);
    assertEquals(1, fileHandle.readByte());
    assertEquals(1, fileHandle.readByte());
  }
  @Test
  public void testWriteBufferOffEnd() throws IOException {
    fileHandle.seek(16);
    ByteBuffer b = ByteBuffer.allocate(1).put((byte) 1);
    fileHandle.write(b);
    assertEquals(17, fileHandle.getFilePointer());
    assertEquals(17, fileHandle.length());
    fileHandle.seek(16);
    assertEquals(1, fileHandle.readByte());
  }

  @Test
  public void testWriteBufferTwiceOffEnd() throws IOException {
    fileHandle.seek(16);
    ByteBuffer b = ByteBuffer.allocate(1).put((byte) 1);
    fileHandle.write(b);
    assertEquals(17, fileHandle.getFilePointer());
    assertEquals(17, fileHandle.length());
    b.position(0);
    fileHandle.write(b);
    assertEquals(18, fileHandle.getFilePointer());
    assertEquals(18, fileHandle.length());
    fileHandle.seek(16);
    assertEquals(1, fileHandle.readByte());
    assertEquals(1, fileHandle.readByte());
  }

  @Test
  public void testWriteOffEndSubArray() throws IOException {
    fileHandle.seek(16);
    fileHandle.write(new byte[] { (byte) (0), (byte) 1 }, 1, 1);
    assertEquals(17, fileHandle.getFilePointer());
    assertEquals(17, fileHandle.length());
    fileHandle.seek(16);
    assertEquals(1, fileHandle.readByte());
  }

  @Test
  public void testWriteTwiceOffEndSubArray() throws IOException {
    fileHandle.seek(16);
    fileHandle.write(new byte[] { (byte) (0), (byte) 1 }, 1, 1);
    assertEquals(17, fileHandle.getFilePointer());
    assertEquals(17, fileHandle.length());
    fileHandle.write(new byte[] { (byte) (0), (byte) 1 }, 1, 1);
    assertEquals(18, fileHandle.getFilePointer());
    assertEquals(18, fileHandle.length());
    fileHandle.seek(16);
    assertEquals(1, fileHandle.readByte());
    assertEquals(1, fileHandle.readByte());
  }

  @Test
  public void testWriteOffEndSubBuffer() throws IOException {
    fileHandle.seek(16);
    ByteBuffer b = ByteBuffer.allocate(2);
    b.put((byte) 0);
    b.put((byte) 1);
    fileHandle.write(b, 1, 1);
    assertEquals(17, fileHandle.getFilePointer());
    assertEquals(17, fileHandle.length());
    fileHandle.seek(16);
    assertEquals(1, fileHandle.readByte());
  }

  @Test
  public void testWriteTwiceOffEndSubBuffer() throws IOException {
    fileHandle.seek(16);
    ByteBuffer b = ByteBuffer.allocate(2);
    b.put((byte) 0);
    b.put((byte) 1);
    fileHandle.write(b, 1, 1);
    assertEquals(17, fileHandle.getFilePointer());
    assertEquals(17, fileHandle.length());
    fileHandle.write(b, 1, 1);
    assertEquals(18, fileHandle.getFilePointer());
    assertEquals(18, fileHandle.length());
    fileHandle.seek(16);
    assertEquals(1, fileHandle.readByte());
    assertEquals(1, fileHandle.readByte());
  }
}
