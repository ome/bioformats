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
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Tests for writing bytes to a loci.common.IRandomAccess.
 *
 * @see loci.common.IRandomAccess
 */
@Test(groups="writeTests")
public class WriteBytesTest {

  private static final byte[] PAGE = new byte[] {
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
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
    assertEquals(8, fileHandle.length());
  }
  
  @Test
  public void testWriteSequential() throws IOException {
    fileHandle.writeBytes("ab");
    if (checkGrowth) {
      assertEquals(2, fileHandle.length());
    }
    fileHandle.writeBytes("cd");
    if (checkGrowth) {
      assertEquals(4, fileHandle.length());
    }
    fileHandle.writeBytes("ef");
    if (checkGrowth) {
      assertEquals(6, fileHandle.length());
    }
    fileHandle.writeBytes("gh");
    assertEquals(8, fileHandle.length());
    fileHandle.seek(0);
    for (byte i = (byte) 0x61; i < 0x69; i++) {
      assertEquals(i, fileHandle.readByte());
    }
  }

  @Test
  public void testWrite() throws IOException {
    fileHandle.writeBytes("ab");
    assertEquals(2, fileHandle.getFilePointer());
    if (checkGrowth) {
      assertEquals(2, fileHandle.length());
    }
    fileHandle.seek(0);
    assertEquals((byte) 0x61, fileHandle.readByte());
    assertEquals((byte) 0x62, fileHandle.readByte());
  }

  @Test
  public void testWriteOffEnd() throws IOException {
    fileHandle.seek(8);
    fileHandle.writeBytes("wx");
    assertEquals(10, fileHandle.getFilePointer());
    assertEquals(10, fileHandle.length());
    fileHandle.seek(8);
    assertEquals((byte) 0x77, fileHandle.readByte());
    assertEquals((byte) 0x78, fileHandle.readByte());
  }

  @Test
  public void testWriteTwiceOffEnd() throws IOException {
    fileHandle.seek(8);
    fileHandle.writeBytes("wx");
    fileHandle.writeBytes("yz");
    assertEquals(12, fileHandle.getFilePointer());
    assertEquals(12, fileHandle.length());
    fileHandle.seek(8);
    assertEquals((byte) 0x77, fileHandle.readByte());
    assertEquals((byte) 0x78, fileHandle.readByte());
    assertEquals((byte) 0x79, fileHandle.readByte());
    assertEquals((byte) 0x7A, fileHandle.readByte());
  }

}
