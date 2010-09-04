//
// WriteCharTest.java
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
 * Tests for reading characters from a loci.common.IRandomAccess.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://dev.loci.wisc.edu/trac/java/browser/trunk/components/common/test/loci/common/utests/WriteCharTest.java">Trac</a>,
 * <a href="http://dev.loci.wisc.edu/svn/java/trunk/components/common/test/loci/common/utests/WriteCharTest.java">SVN</a></dd></dl>
 *
 * @see loci.common.IRandomAccess
 */
@Test(groups="writeTests")
public class WriteCharTest {

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
    fileHandle.seek(0);
    fileHandle.writeChar('a');
    if (checkGrowth) {
      assertEquals(2, fileHandle.length());
    }
    fileHandle.writeChar('b');
    if (checkGrowth) {
      assertEquals(4, fileHandle.length());
    }
    fileHandle.writeChar('c');
    if (checkGrowth) {
      assertEquals(6, fileHandle.length());
    }
    fileHandle.writeChar('d');
    if (checkGrowth) {
      assertEquals(8, fileHandle.length());
    }
    fileHandle.seek(0);
    assertEquals('a', fileHandle.readChar());
    assertEquals('b', fileHandle.readChar());
    assertEquals('c', fileHandle.readChar());
    assertEquals('d', fileHandle.readChar());
  }

  @Test
  public void testSeekForward() throws IOException {
    fileHandle.seek(8);
    fileHandle.writeChar('e');
    fileHandle.writeChar('f');
    if (checkGrowth) {
      assertEquals(12, fileHandle.length());
    }
    fileHandle.seek(8);
    assertEquals('e', fileHandle.readChar());
    assertEquals('f', fileHandle.readChar());
  }

  @Test
  public void testReset() throws IOException {
    fileHandle.seek(0);
    fileHandle.writeChar('a');
    if (checkGrowth) {
      assertEquals(2, fileHandle.length());
    }
    fileHandle.writeChar('b');
    if (checkGrowth) {
      assertEquals(4, fileHandle.length());
    }
    fileHandle.seek(0);
    assertEquals('a', fileHandle.readChar());
    assertEquals('b', fileHandle.readChar());
    fileHandle.seek(0);
    fileHandle.writeChar('c');
    fileHandle.writeChar('d');
    fileHandle.seek(0);
    assertEquals('c', fileHandle.readChar());
    assertEquals('d', fileHandle.readChar());
  }

}
