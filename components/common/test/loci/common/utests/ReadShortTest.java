//
// ReadShortTest.java
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
 * Tests for reading shorts from a loci.common.IRandomAccess.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/common/test/loci/common/utests/ReadShortTest.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/common/test/loci/common/utests/ReadShortTest.java">SVN</a></dd></dl>
 *
 * @see loci.common.IRandomAccess
 */
@Test(groups="readTests")
public class ReadShortTest {

  private static final byte[] PAGE = new byte[] {
    // 16-bit shorts
    (byte) 0x00, (byte) 0x01, (byte) 0x0F, (byte) 0x02,
    (byte) 0x00, (byte) 0x03, (byte) 0x0F, (byte) 0x04,
    (byte) 0x00, (byte) 0x05, (byte) 0x0F, (byte) 0x06,
    (byte) 0x00, (byte) 0x07, (byte) 0x0F, (byte) 0x08,
    (byte) 0x00, (byte) 0x09, (byte) 0x0F, (byte) 0x0A,
    (byte) 0x00, (byte) 0x0B, (byte) 0x0F, (byte) 0x0C,
    (byte) 0x00, (byte) 0x0D, (byte) 0xFF, (byte) 0xFF,
    (byte) 0x00, (byte) 0x0F, (byte) 0xFF, (byte) 0xFE
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
  public void testSequentialReadShort() throws IOException {
    assertEquals(1, fileHandle.readShort());
    assertEquals(3842, fileHandle.readShort());
    assertEquals(3, fileHandle.readShort());
    assertEquals(3844, fileHandle.readShort());
    assertEquals(5, fileHandle.readShort());
    assertEquals(3846, fileHandle.readShort());
    assertEquals(7, fileHandle.readShort());
    assertEquals(3848, fileHandle.readShort());
    assertEquals(9, fileHandle.readShort());
    assertEquals(3850, fileHandle.readShort());
    assertEquals(11, fileHandle.readShort());
    assertEquals(3852, fileHandle.readShort());
    assertEquals(13, fileHandle.readShort());
    assertEquals(-1, fileHandle.readShort());
    assertEquals(15, fileHandle.readShort());
    assertEquals(-2, fileHandle.readShort());
  }

  @Test
  public void testSeekForwardReadShort() throws IOException {
    fileHandle.seek(8);
    assertEquals(5, fileHandle.readShort());
    assertEquals(3846, fileHandle.readShort());
  }

  @Test
  public void testResetReadShort() throws IOException {
    assertEquals(1, fileHandle.readShort());
    assertEquals(3842, fileHandle.readShort());
    fileHandle.seek(0);
    assertEquals(1, fileHandle.readShort());
    assertEquals(3842, fileHandle.readShort());
  }

  @Test
  public void testSeekBackReadShort() throws IOException {
    fileHandle.seek(16);
    fileHandle.seek(8);
    assertEquals(5, fileHandle.readShort());
    assertEquals(3846, fileHandle.readShort());
  }

  @Test
  public void testRandomAccessReadShort() throws IOException {
    testSeekForwardReadShort();
    testSeekBackReadShort();
    // The test relies on a "new" file or reset file pointer
    fileHandle.seek(0);
    testResetReadShort();
  }

}
