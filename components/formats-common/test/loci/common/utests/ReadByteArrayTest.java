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
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/test/loci/common/utests/ReadByteArrayTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/test/loci/common/utests/ReadByteArrayTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see loci.common.IRandomAccess
 */
@Test(groups="readTests")
public class ReadByteArrayTest {

  private static final byte[] PAGE = new byte[] {
    (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04,
    (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08,
    (byte) 0x09, (byte) 0x0A, (byte) 0x0B, (byte) 0x0C,
    (byte) 0x0D, (byte) 0x0E, (byte) 0xFF, (byte) 0xFE
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
    assertEquals(16, fileHandle.length());
  }

  @Test
  public void testSequentialReadByte() throws IOException {
    byte[] b = new byte[16];
    int length = fileHandle.read(b);
    assertEquals(16, fileHandle.getFilePointer());
    assertEquals(16, length);
    assertEquals(0x01, b[0]);
    assertEquals(0x02, b[1]);
    assertEquals(0x03, b[2]);
    assertEquals(0x04, b[3]);
    assertEquals(0x05, b[4]);
    assertEquals(0x06, b[5]);
    assertEquals(0x07, b[6]);
    assertEquals(0x08, b[7]);
    assertEquals(0x09, b[8]);
    assertEquals(0x0A, b[9]);
    assertEquals(0x0B, b[10]);
    assertEquals(0x0C, b[11]);
    assertEquals(0x0D, b[12]);
    assertEquals(0x0E, b[13]);
    assertEquals((byte) 0xFF, b[14]);
    assertEquals((byte) 0xFE, b[15]);
  }

  @Test
  public void testSeekForwardReadByte() throws IOException {
    fileHandle.seek(7);
    byte[] b = new byte[2];
    int length = fileHandle.read(b);
    assertEquals(9, fileHandle.getFilePointer());
    assertEquals(2, length);
    assertEquals(0x08, b[0]);
    assertEquals(0x09, b[1]);
  }

  @Test
  public void testResetReadByte() throws IOException {
    byte[] b = new byte[2];
    int length = fileHandle.read(b);
    assertEquals(2, fileHandle.getFilePointer());
    assertEquals(0x02, length);
    assertEquals(0x01, b[0]);
    assertEquals(0x02, b[1]);
    fileHandle.seek(0);
    b = new byte[2];
    length = fileHandle.read(b);
    assertEquals(0x02, length);
    assertEquals(0x01, b[0]);
    assertEquals(0x02, b[1]);
  }

  @Test
  public void testSeekBackReadByte() throws IOException {
    fileHandle.seek(15);
    fileHandle.seek(7);
    byte[] b = new byte[2];
    int length = fileHandle.read(b);
    assertEquals(9, fileHandle.getFilePointer());
    assertEquals(2, length);
    assertEquals(0x08, b[0]);
    assertEquals(0x09, b[1]);
  }

  @Test
  public void testRandomAccessReadByte() throws IOException {
    testSeekForwardReadByte();
    testSeekBackReadByte();
    // The test relies on a "new" file or reset file pointer
    fileHandle.seek(0);
    testResetReadByte();
  }

}
