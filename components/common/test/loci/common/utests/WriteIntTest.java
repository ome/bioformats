//
// WriteIntTest.java
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

import loci.common.IRandomAccess;
import loci.common.utests.providers.IRandomAccessProvider;
import loci.common.utests.providers.IRandomAccessProviderFactory;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Tests for reading ints from a loci.common.IRandomAccess.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://dev.loci.wisc.edu/trac/java/browser/trunk/components/common/test/loci/common/utests/WriteIntTest.java">Trac</a>,
 * <a href="http://dev.loci.wisc.edu/svn/java/trunk/components/common/test/loci/common/utests/WriteIntTest.java">SVN</a></dd></dl>
 *
 * @see loci.common.IRandomAccess
 */
@Test(groups="writeTests")
public class WriteIntTest {

  private static final byte[] PAGE = new byte[] {
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
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
    assertEquals(32, fileHandle.length());
  }

  @Test
  public void testSequential() throws IOException {
    fileHandle.writeInt(1);
    if (checkGrowth) {
      assertEquals(4, fileHandle.length());
    }
    fileHandle.writeInt(268435202);
    if (checkGrowth) {
      assertEquals(8, fileHandle.length());
    }
    fileHandle.writeInt(3);
    if (checkGrowth) {
      assertEquals(12, fileHandle.length());
    }
    fileHandle.writeInt(268435204);
    if (checkGrowth) {
      assertEquals(16, fileHandle.length());
    }
    fileHandle.writeInt(5);
    if (checkGrowth) {
      assertEquals(20, fileHandle.length());
    }
    fileHandle.writeInt(-1);
    if (checkGrowth) {
      assertEquals(24, fileHandle.length());
    }
    fileHandle.writeInt(7);
    if (checkGrowth) {
      assertEquals(28, fileHandle.length());
    }
    fileHandle.writeInt(-2);
    if (checkGrowth) {
      assertEquals(32, fileHandle.length());
    }
    fileHandle.seek(0);
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
  public void testSeekForward() throws IOException {
    fileHandle.seek(8);
    fileHandle.writeInt(3);
    if (checkGrowth) {
      assertEquals(12, fileHandle.length());
    }
    fileHandle.writeInt(268435204);
    if (checkGrowth) {
      assertEquals(16, fileHandle.length());
    }
    fileHandle.seek(8);
    assertEquals(3, fileHandle.readInt());
    assertEquals(268435204, fileHandle.readInt());
  }

  @Test
  public void testReset() throws IOException {
    fileHandle.writeInt(1);
    if (checkGrowth) {
      assertEquals(4, fileHandle.length());
    }
    fileHandle.writeInt(268435202);
    if (checkGrowth) {
      assertEquals(8, fileHandle.length());
    }
    fileHandle.seek(0);
    assertEquals(1, fileHandle.readInt());
    assertEquals(268435202, fileHandle.readInt());
    fileHandle.seek(0);
    fileHandle.writeInt(3);
    fileHandle.writeInt(268435204);
    fileHandle.seek(0);
    assertEquals(3, fileHandle.readInt());
    assertEquals(268435204, fileHandle.readInt());
  }

}
