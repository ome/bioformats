//
// ReadUnsignedShortTest.java
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
 * Tests for reading unsigned shorts from a loci.common.IRandomAccess.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/common/test/loci/common/utests/ReadUnsignedShortTest.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/common/test/loci/common/utests/ReadUnsignedShortTest.java">SVN</a></dd></dl>
 *
 * @see loci.common.IRandomAccess
 */
@Test(groups="readTests")
public class ReadUnsignedShortTest {

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
  public void testSequentialReadUnsignedShort() throws IOException {
    assertEquals(1, fileHandle.readUnsignedShort());
    assertEquals(3842, fileHandle.readUnsignedShort());
    assertEquals(3, fileHandle.readUnsignedShort());
    assertEquals(3844, fileHandle.readUnsignedShort());
    assertEquals(5, fileHandle.readUnsignedShort());
    assertEquals(3846, fileHandle.readUnsignedShort());
    assertEquals(7, fileHandle.readUnsignedShort());
    assertEquals(3848, fileHandle.readUnsignedShort());
    assertEquals(9, fileHandle.readUnsignedShort());
    assertEquals(3850, fileHandle.readUnsignedShort());
    assertEquals(11, fileHandle.readUnsignedShort());
    assertEquals(3852, fileHandle.readUnsignedShort());
    assertEquals(13, fileHandle.readUnsignedShort());
    assertEquals(65535, fileHandle.readUnsignedShort());
    assertEquals(15, fileHandle.readUnsignedShort());
    assertEquals(65534, fileHandle.readUnsignedShort());
  }

  @Test
  public void testSeekForwardReadUnsignedShort() throws IOException {
    fileHandle.seek(8);
    assertEquals(5, fileHandle.readUnsignedShort());
    assertEquals(3846, fileHandle.readUnsignedShort());
  }

  @Test
  public void testResetReadUnsignedShort() throws IOException {
    assertEquals(1, fileHandle.readUnsignedShort());
    assertEquals(3842, fileHandle.readUnsignedShort());
    fileHandle.seek(0);
    assertEquals(1, fileHandle.readUnsignedShort());
    assertEquals(3842, fileHandle.readUnsignedShort());
  }

  @Test
  public void testSeekBackReadUnsignedShort() throws IOException {
    fileHandle.seek(16);
    fileHandle.seek(8);
    assertEquals(5, fileHandle.readUnsignedShort());
    assertEquals(3846, fileHandle.readUnsignedShort());
  }

  @Test
  public void testRandomAccessReadUnsignedShort() throws IOException {
    testSeekForwardReadUnsignedShort();
    testSeekBackReadUnsignedShort();
    // The test relies on a "new" file or reset file pointer
    fileHandle.seek(0);
    testResetReadUnsignedShort();
  }

}
