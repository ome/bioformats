//
// ReadIntTest.java
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

import java.io.IOException;

import loci.common.IRandomAccess;
import loci.common.utests.providers.IRandomAccessProvider;
import loci.common.utests.providers.IRandomAccessProviderFactory;

import static org.testng.AssertJUnit.*;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Tests for reading ints from a loci.common.IRandomAccess.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/common/test/loci/common/utests/ReadIntTest.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/common/test/loci/common/utests/ReadIntTest.java">SVN</a></dd></dl>
 *
 * @see loci.common.IRandomAccess
 */
@Test(groups="readTests")
public class ReadIntTest {

  private static final byte[] PAGE = new byte[] {
    // 32-bit long
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01,
    // 32-bit long
    (byte) 0x0F, (byte) 0xFF, (byte) 0xFF, (byte) 0x02,
    // 32-bit long
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03,
    // 32-bit long
    (byte) 0x0F, (byte) 0xFF, (byte) 0xFF, (byte) 0x04,
    // 32-bit long
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x05,
    // 32-bit long (-1)
    (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
    // 32-bit long
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x07,
    // 32-bit long (-2)
    (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFE
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
    assertEquals(32, fileHandle.length());
  }

  @Test
  public void testSequentialReadInt() throws IOException {
    assertEquals(1, fileHandle.readInt());
    assertEquals(268435202, fileHandle.readInt());
    assertEquals(3, fileHandle.readInt());
    assertEquals(268435204, fileHandle.readInt());
    assertEquals(5, fileHandle.readInt());
    assertEquals(-1, fileHandle.readInt());
    assertEquals(7, fileHandle.readInt());
    assertEquals(-2, fileHandle.readInt());
  }

  @Test
  public void testSeekForwardReadInt() throws IOException {
    fileHandle.seek(8);
    assertEquals(3, fileHandle.readInt());
    assertEquals(268435204, fileHandle.readInt());
  }

  @Test
  public void testResetReadInt() throws IOException {
    assertEquals(1, fileHandle.readInt());
    assertEquals(268435202, fileHandle.readInt());
    fileHandle.seek(0);
    assertEquals(1, fileHandle.readInt());
    assertEquals(268435202, fileHandle.readInt());
  }

  @Test
  public void testSeekBackReadInt() throws IOException {
    fileHandle.seek(16);
    fileHandle.seek(8);
    assertEquals(3, fileHandle.readInt());
    assertEquals(268435204, fileHandle.readInt());
  }

  @Test
  public void testRandomAccessReadInt() throws IOException {
    testSeekForwardReadInt();
    testSeekBackReadInt();
    // The test relies on a "new" file or reset file pointer
    fileHandle.seek(0);
    testResetReadInt();
  }

}
