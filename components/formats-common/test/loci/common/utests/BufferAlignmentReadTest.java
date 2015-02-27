/*
 * #%L
 * Common package for I/O and related utilities
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.common.utests;

import static org.testng.AssertJUnit.assertEquals;

import java.io.EOFException;
import java.io.IOException;

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
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/test/loci/common/utests/BufferAlignmentReadTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/test/loci/common/utests/BufferAlignmentReadTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see loci.common.IRandomAccess
 */
@Test(groups="readTests")
public class BufferAlignmentReadTest {

  private static final byte[] PAGE = new byte[] {
    // 64-byte page
    (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04,
    (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08,
    (byte) 0x09, (byte) 0x0A, (byte) 0x0B, (byte) 0x0C,
    (byte) 0x0D, (byte) 0x0E, (byte) 0x0F, (byte) 0x10,
    (byte) 0x11, (byte) 0x12, (byte) 0x13, (byte) 0x14,
    (byte) 0x15, (byte) 0x16, (byte) 0x17, (byte) 0x18,
    (byte) 0x19, (byte) 0x1A, (byte) 0x1B, (byte) 0x1C,
    (byte) 0x1D, (byte) 0x1E, (byte) 0x1F, (byte) 0x20,
    (byte) 0x21, (byte) 0x22, (byte) 0x23, (byte) 0x24,
    (byte) 0x25, (byte) 0x26, (byte) 0x27, (byte) 0x28,
    (byte) 0x29, (byte) 0x2A, (byte) 0x2B, (byte) 0x2C,
    (byte) 0x2D, (byte) 0x2E, (byte) 0x2F, (byte) 0x30,
    (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34,
    (byte) 0x35, (byte) 0x36, (byte) 0x37, (byte) 0x38,
    (byte) 0x39, (byte) 0x3A, (byte) 0x3B, (byte) 0x3C,
    (byte) 0x3D, (byte) 0x3E, (byte) 0x3F, (byte) 0x40,
  };

  private static final String MODE = "r";

  private static final int BUFFER_SIZE = 2;

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
    assertEquals(64, fileHandle.length());
  }

  @Test
  public void testSequential() throws IOException {
    assertEquals(1, fileHandle.readByte());
    assertEquals(2, fileHandle.readByte());
    assertEquals(3, fileHandle.readByte());
    assertEquals(4, fileHandle.readByte());
    assertEquals(5, fileHandle.readByte());
    assertEquals(6, fileHandle.readByte());
    assertEquals(7, fileHandle.readByte());
    assertEquals(8, fileHandle.readByte());
    assertEquals(9, fileHandle.readByte());
    assertEquals(10, fileHandle.readByte());
    assertEquals(11, fileHandle.readByte());
    assertEquals(12, fileHandle.readByte());
    assertEquals(13, fileHandle.readByte());
    assertEquals(14, fileHandle.readByte());
    assertEquals(15, fileHandle.readByte());
    assertEquals(16, fileHandle.readByte());
    assertEquals(17, fileHandle.readByte());
    assertEquals(18, fileHandle.readByte());
    assertEquals(19, fileHandle.readByte());
    assertEquals(20, fileHandle.readByte());
    assertEquals(21, fileHandle.readByte());
    assertEquals(22, fileHandle.readByte());
    assertEquals(23, fileHandle.readByte());
    assertEquals(24, fileHandle.readByte());
    assertEquals(25, fileHandle.readByte());
    assertEquals(26, fileHandle.readByte());
    assertEquals(27, fileHandle.readByte());
    assertEquals(28, fileHandle.readByte());
    assertEquals(29, fileHandle.readByte());
    assertEquals(30, fileHandle.readByte());
    assertEquals(31, fileHandle.readByte());
    assertEquals(32, fileHandle.readByte());
    assertEquals(33, fileHandle.readByte());
    assertEquals(34, fileHandle.readByte());
    assertEquals(35, fileHandle.readByte());
    assertEquals(36, fileHandle.readByte());
    assertEquals(37, fileHandle.readByte());
    assertEquals(38, fileHandle.readByte());
    assertEquals(39, fileHandle.readByte());
    assertEquals(40, fileHandle.readByte());
    assertEquals(41, fileHandle.readByte());
    assertEquals(42, fileHandle.readByte());
    assertEquals(43, fileHandle.readByte());
    assertEquals(44, fileHandle.readByte());
    assertEquals(45, fileHandle.readByte());
    assertEquals(46, fileHandle.readByte());
    assertEquals(47, fileHandle.readByte());
    assertEquals(48, fileHandle.readByte());
    assertEquals(49, fileHandle.readByte());
    assertEquals(50, fileHandle.readByte());
    assertEquals(51, fileHandle.readByte());
    assertEquals(52, fileHandle.readByte());
    assertEquals(53, fileHandle.readByte());
    assertEquals(54, fileHandle.readByte());
    assertEquals(55, fileHandle.readByte());
    assertEquals(56, fileHandle.readByte());
    assertEquals(57, fileHandle.readByte());
    assertEquals(58, fileHandle.readByte());
    assertEquals(59, fileHandle.readByte());
    assertEquals(60, fileHandle.readByte());
    assertEquals(61, fileHandle.readByte());
    assertEquals(62, fileHandle.readByte());
    assertEquals(63, fileHandle.readByte());
    assertEquals(64, fileHandle.readByte());
  }

  @Test
  public void testSeekForward() throws IOException {
    fileHandle.seek(32);
    assertEquals(33, fileHandle.readByte());
    assertEquals(34, fileHandle.readByte());
    assertEquals(35, fileHandle.readByte());
    assertEquals(36, fileHandle.readByte());
  }

  @Test
  public void testReset() throws IOException {
    assertEquals(1, fileHandle.readByte());
    assertEquals(2, fileHandle.readByte());
    assertEquals(3, fileHandle.readByte());
    assertEquals(4, fileHandle.readByte());
    fileHandle.seek(0);
    assertEquals(1, fileHandle.readByte());
    assertEquals(2, fileHandle.readByte());
  }

  @Test
  public void testSeekBack() throws IOException {
    fileHandle.seek(48);
    fileHandle.seek(32);
    assertEquals(33, fileHandle.readByte());
    assertEquals(34, fileHandle.readByte());
    assertEquals(35, fileHandle.readByte());
    assertEquals(36, fileHandle.readByte());
  }

  @Test
  public void testBufferBoundry() throws IOException {
    fileHandle.seek(32);
    assertEquals(33, fileHandle.readByte());
    assertEquals(34, fileHandle.readByte());
    assertEquals(35, fileHandle.readByte());
    fileHandle.seek(31);
    assertEquals(32, fileHandle.readByte());
    assertEquals(33, fileHandle.readByte());
    assertEquals(34, fileHandle.readByte());
    fileHandle.seek(28);
    assertEquals(29, fileHandle.readByte());
    assertEquals(30, fileHandle.readByte());
    assertEquals(31, fileHandle.readByte());
  }

  @Test
  public void testReadOverBufferBoundary() throws IOException {
    fileHandle.seek(1);
    assertEquals(515, fileHandle.readShort());
  }

  @Test
  public void testRandomAccessReadByte() throws IOException {
    testSeekForward();
    testSeekBack();
    testBufferBoundry();
    // The test relies on a "new" file or reset file pointer
    fileHandle.seek(0);
    testReset();
  }

  @Test(expectedExceptions={EOFException.class})
  public void testReadExplicitlyOffEnd() throws IOException {
    fileHandle.seek(64);
    fileHandle.readByte();
  }

  @Test(expectedExceptions={EOFException.class})
  public void testReadPartiallyOffEnd() throws IOException {
    fileHandle.seek(63);
    fileHandle.readShort();
  }

  public void testReadExplicitlyArrayOffEnd() throws IOException {
    fileHandle.seek(64);
    byte[] buf = new byte[1];
    assertEquals(0, fileHandle.read(buf));
    assertEquals(0, buf[0]);
  }

  public void testReadPartiallyArrayOffEnd() throws IOException {
    fileHandle.seek(63);
    byte[] buf = new byte[2];
    assertEquals(1, fileHandle.read(buf));
    assertEquals(64, buf[0]);
    assertEquals(0, buf[1]);
  }

  public void testReadExplicitlySubArrayOffEnd() throws IOException {
    fileHandle.seek(64);
    byte[] buf = new byte[3];
    assertEquals(0, fileHandle.read(buf, 1, 1));
    assertEquals(0, buf[0]);
    assertEquals(0, buf[1]);
    assertEquals(0, buf[2]);
  }

  public void testReadPartiallySubArrayOffEnd() throws IOException {
    fileHandle.seek(63);
    byte[] buf = new byte[6];
    assertEquals(1, fileHandle.read(buf, 2, 2));
    assertEquals(0, buf[0]);
    assertEquals(0, buf[1]);
    assertEquals(64, buf[2]);
    assertEquals(0, buf[3]);
    assertEquals(0, buf[4]);
    assertEquals(0, buf[5]);
  }
}
