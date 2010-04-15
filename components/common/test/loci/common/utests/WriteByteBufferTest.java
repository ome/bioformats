//
// WriteByteBufferTest.java
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
import static org.testng.AssertJUnit.fail;

import java.io.IOException;
import java.nio.ByteBuffer;

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
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/common/test/loci/common/utests/WriteByteBufferTest.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/common/test/loci/common/utests/WriteByteBufferTest.java">SVN</a></dd></dl>
 *
 * @see loci.common.IRandomAccess
 */
@Test(groups="writeTests")
public class WriteByteBufferTest {

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
      ByteBuffer b = ByteBuffer.allocate(1).put((byte) (i + 1));
      fileHandle.write(b);
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
    ByteBuffer b = ByteBuffer.allocate(1).put((byte) 1);
    fileHandle.write(b);
    if (checkGrowth) {
      assertEquals(1, fileHandle.length());
    }
    assertEquals(1, fileHandle.getFilePointer());
    fileHandle.seek(0);
    assertEquals(1, fileHandle.readByte());
  }

  @Test
  public void testWriteSequentialSubBuffer() throws IOException {
    for (int i = 0; i < 16; i++) {
      ByteBuffer b = ByteBuffer.allocate(2);
      b.put((byte) 0);
      b.put((byte) (i + 1));
      fileHandle.write(b, 1, 1);
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
  public void testWriteSubBuffer() throws IOException {
    ByteBuffer b = ByteBuffer.allocate(2);
    b.put((byte) 0);
    b.put((byte) 1);
    fileHandle.write(b, 1, 1);
    assertEquals(1, fileHandle.getFilePointer());
    fileHandle.seek(0);
    assertEquals(1, fileHandle.readByte());
  }

  @Test
  public void testWriteTwoByteSubBuffer() throws IOException {
    ByteBuffer b = ByteBuffer.allocate(4);
    b.put((byte) 1);
    b.put((byte) 2);
    b.put((byte) 3);
    b.put((byte) 4);
    fileHandle.write(b, 1, 2);
    assertEquals(2, fileHandle.getFilePointer());
    fileHandle.seek(0);
    assertEquals(2, fileHandle.readByte());
    assertEquals(3, fileHandle.readByte());
    if (fileHandle.length() > 2 && (fileHandle.readByte() == 4)) {
      fail("Incorrect length or trailing bytes.");
    }
  }

}
