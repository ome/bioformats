//
// EndiannessTest.java
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
import java.nio.ByteOrder;

import loci.common.IRandomAccess;
import loci.common.utests.providers.IRandomAccessProvider;
import loci.common.utests.providers.IRandomAccessProviderFactory;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Tests swapping the endianness of a loci.common.IRandomAccess.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/test/loci/common/utests/EndiannessTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/test/loci/common/utests/EndiannessTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see loci.common.IRandomAccess
 */
@Test(groups="readTests")
public class EndiannessTest {

  private static final byte[] PAGE = new byte[] {
    (byte) 0x0F, (byte) 0x0E, (byte) 0x0F, (byte) 0x0E,
    (byte) 0x0F, (byte) 0x0E, (byte) 0x0F, (byte) 0x0E
  };

  private static final String MODE = "r";

  private static final int BUFFER_SIZE = 4;

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
    assertEquals(8, fileHandle.length());
  }

  @Test
  public void testSeekBigEndian() throws IOException {
    fileHandle.seek(6);
    assertEquals(3854, fileHandle.readShort());
  }

  @Test
  public void testSeekLittleEndian() throws IOException {
    fileHandle.setOrder(ByteOrder.LITTLE_ENDIAN);
    fileHandle.seek(6);
    assertEquals(3599, fileHandle.readShort());
  }

  @Test
  public void testReadShortBigEndian() throws IOException {
    assertEquals(3854, fileHandle.readShort());
  }

  @Test
  public void testReadShortLittleEndian() throws IOException {
    fileHandle.setOrder(ByteOrder.LITTLE_ENDIAN);
    assertEquals(3599, fileHandle.readShort());
  }

  @Test
  public void testReadIntBigEndian() throws IOException {
    assertEquals(252579598, fileHandle.readInt());
  }

  @Test
  public void testReadIntLittleEndian() throws IOException {
    fileHandle.setOrder(ByteOrder.LITTLE_ENDIAN);
    assertEquals(235867663, fileHandle.readInt());
  }

  @Test
  public void testReadLongBigEndian() throws IOException {
    assertEquals(1084821113299406606L, fileHandle.readLong());
  }

  @Test
  public void testReadLongLittleEndian() throws IOException {
    fileHandle.setOrder(ByteOrder.LITTLE_ENDIAN);
    assertEquals(1013043899004816911L, fileHandle.readLong());
  }

}
