//
// RandomAccessInputStreamTest.java
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
import loci.common.RandomAccessInputStream;
import loci.common.utests.providers.IRandomAccessProvider;
import loci.common.utests.providers.IRandomAccessProviderFactory;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Tests for reading bytes from a loci.common.RandomAccessInputStream.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/test/loci/common/utests/RandomAccessInputStreamTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/test/loci/common/utests/RandomAccessInputStreamTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see loci.common.RandomAccessInputStream
 */
@Test(groups="readTests")
public class RandomAccessInputStreamTest {

  private static final byte[] PAGE = new byte[] {
    0, 4, 8, 12, 16, 20, 24, 28,
    32, 36, 40, 44, 48, 52, 56, 60,
    64, 68, 72, 76, 80, 84, 88, 92,
    96, 100, 104, 108, 112, 116, 120, 124,
    (byte) 128, (byte) 132, (byte) 136, (byte) 140, (byte) 144, (byte) 148,
    (byte) 152, (byte) 156, (byte) 160, (byte) 164, (byte) 168, (byte) 172,
    (byte) 176, (byte) 180, (byte) 184, (byte) 188, (byte) 192, (byte) 196,
    (byte) 200, (byte) 204, (byte) 208, (byte) 212, (byte) 216, (byte) 220,
    (byte) 224, (byte) 228, (byte) 232, (byte) 236, (byte) 240, (byte) 244,
    (byte) 248, (byte) 252
  };

  private static final String MODE = "r";
  private static final int BUFFER_SIZE = 2;

  private RandomAccessInputStream stream;
  private IRandomAccess fileHandle;

  @Parameters({"provider"})
  @BeforeMethod
  public void setUp(String provider) throws IOException {
    IRandomAccessProviderFactory factory = new IRandomAccessProviderFactory();
    IRandomAccessProvider instance = factory.getInstance(provider);
    fileHandle = instance.createMock(PAGE, MODE, BUFFER_SIZE);
    stream = new RandomAccessInputStream(fileHandle);
  }

  @Test
  public void testLength() throws IOException {
    assertEquals(PAGE.length, stream.length());
  }

  @Test
  public void testSequentialRead() throws IOException {
    stream.seek(0);
    for (int i=0; i<stream.length(); i++) {
      assertEquals(PAGE[i], stream.readByte());
    }
  }

  @Test
  public void testReverseSequentialRead() throws IOException {
    stream.seek(stream.length() - 1);
    for (int i=PAGE.length-1; i>=0; i--) {
      assertEquals(PAGE[i], stream.readByte());
      if (stream.getFilePointer() >= 2) {
        stream.seek(stream.getFilePointer() - 2);
      }
    }
  }

  @Test
  public void testArrayRead() throws IOException {
    stream.seek(0);
    byte[] buf = new byte[PAGE.length];
    stream.read(buf);
    for (int i=0; i<PAGE.length; i++) {
      assertEquals(PAGE[i], buf[i]);
    }
  }

  @Test
  public void testRandomRead() throws IOException {
    long fp = PAGE.length / 2;
    stream.seek(fp);
    for (int i=0; i<PAGE.length; i++) {
      boolean forward = (i % 2) == 0;
      int step = i / 2;
      if (forward) {
        stream.seek(fp + step);
      }
      else {
        stream.seek(fp - step);
      }
      assertEquals(PAGE[(int) stream.getFilePointer()], stream.readByte());
    }
  }

}
