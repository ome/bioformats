//
// ReadLongTest.java
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
 * Tests for reading longs from a loci.common.IRandomAccess.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://dev.loci.wisc.edu/trac/java/browser/trunk/components/common/test/loci/common/utests/WriteLongTest.java">Trac</a>,
 * <a href="http://dev.loci.wisc.edu/svn/java/trunk/components/common/test/loci/common/utests/WriteLongTest.java">SVN</a></dd></dl>
 *
 * @see loci.common.IRandomAccess
 */
@Test(groups="writeTests")
public class WriteLongTest {

  private static final byte[] PAGE = new byte[] {
    // 64-bit long
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
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
    assertEquals(64, fileHandle.length());
  }

  @Test
  public void testSequential() throws IOException {
    fileHandle.writeLong(1L);
    if (checkGrowth) {
      assertEquals(8, fileHandle.length());
    }
    fileHandle.writeLong(1152921504606846722L);
    if (checkGrowth) {
      assertEquals(16, fileHandle.length());
    }
    fileHandle.writeLong(3L);
    if (checkGrowth) {
      assertEquals(24, fileHandle.length());
    }
    fileHandle.writeLong(1152921504606846724L);
    if (checkGrowth) {
      assertEquals(32, fileHandle.length());
    }
    fileHandle.writeLong(5L);
    if (checkGrowth) {
      assertEquals(40, fileHandle.length());
    }
    fileHandle.writeLong(-1L);
    if (checkGrowth) {
      assertEquals(48, fileHandle.length());
    }
    fileHandle.writeLong(7L);
    if (checkGrowth) {
      assertEquals(56, fileHandle.length());
    }
    fileHandle.writeLong(-2L);
    if (checkGrowth) {
      assertEquals(64, fileHandle.length());
    }
    fileHandle.seek(0);
    assertEquals(1L, fileHandle.readLong());
    assertEquals(1152921504606846722L, fileHandle.readLong());
    assertEquals(3L, fileHandle.readLong());
    assertEquals(1152921504606846724L, fileHandle.readLong());
    assertEquals(5L, fileHandle.readLong());
    assertEquals(-1L, fileHandle.readLong());
    assertEquals(7L, fileHandle.readLong());
    assertEquals(-2L, fileHandle.readLong());
  }

  @Test
  public void testSeekForward() throws IOException {
    fileHandle.seek(8);
    fileHandle.writeLong(1152921504606846722L);
    if (checkGrowth) {
      assertEquals(16, fileHandle.length());
    }
    fileHandle.writeLong(3L);
    if (checkGrowth) {
      assertEquals(24, fileHandle.length());
    }
    fileHandle.seek(8);
    assertEquals(1152921504606846722L, fileHandle.readLong());
    assertEquals(3L, fileHandle.readLong());
  }

  @Test
  public void testReset() throws IOException {
    fileHandle.writeLong(1L);
    if (checkGrowth) {
      assertEquals(8, fileHandle.length());
    }
    fileHandle.writeLong(1152921504606846722L);
    if (checkGrowth) {
      assertEquals(16, fileHandle.length());
    }
    fileHandle.seek(0);
    assertEquals(1L, fileHandle.readLong());
    assertEquals(1152921504606846722L, fileHandle.readLong());
    fileHandle.seek(0);
    fileHandle.writeLong(1L);
    fileHandle.writeLong(1152921504606846722L);
    fileHandle.seek(0);
    assertEquals(1L, fileHandle.readLong());
    assertEquals(1152921504606846722L, fileHandle.readLong());
  }

}
