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
import static org.testng.AssertJUnit.fail;

import java.io.IOException;
import java.nio.ByteBuffer;

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
public class WriteByteBufferTest {

  private static final byte[] PAGE = new byte[] {
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
    assertEquals(16, fileHandle.length());
  }
  
  @Test
  public void testWriteSequential() throws IOException {
    for (int i = 0; i < 16; i++) {
      ByteBuffer b = ByteBuffer.allocate(1).put((byte) (i + 1));
      fileHandle.write(b);
      if (checkGrowth) {
        assertEquals(i + 1, fileHandle.length());
      }
    }
    assertEquals(16, fileHandle.length());
    fileHandle.seek(0);
    for (int i = 0; i < 16; i++) {
      assertEquals(i + 1, fileHandle.readByte());
    }
  }

  @Test
  public void testWrite() throws IOException {
    ByteBuffer b = ByteBuffer.allocate(1).put((byte) 1);
    fileHandle.write(b);
    if (checkGrowth) {
      assertEquals(1, fileHandle.length());
    }
    assertEquals(1, fileHandle.getFilePointer());
    fileHandle.seek(0);
    assertEquals(1, fileHandle.readByte());
  }

  @Test
  public void testWriteSequentialSubBuffer() throws IOException {
    for (int i = 0; i < 16; i++) {
      ByteBuffer b = ByteBuffer.allocate(2);
      b.put((byte) 0);
      b.put((byte) (i + 1));
      fileHandle.write(b, 1, 1);
      if (checkGrowth) {
        assertEquals(i + 1, fileHandle.length());
      }
    }
    assertEquals(16, fileHandle.length());
    fileHandle.seek(0);
    for (int i = 0; i < 16; i++) {
      assertEquals(i + 1, fileHandle.readByte());
    }
  }

  @Test
  public void testWriteSubBuffer() throws IOException {
    ByteBuffer b = ByteBuffer.allocate(2);
    b.put((byte) 0);
    b.put((byte) 1);
    fileHandle.write(b, 1, 1);
    assertEquals(1, fileHandle.getFilePointer());
    fileHandle.seek(0);
    assertEquals(1, fileHandle.readByte());
  }

  @Test
  public void testWriteTwoByteSubBuffer() throws IOException {
    ByteBuffer b = ByteBuffer.allocate(4);
    b.put((byte) 1);
    b.put((byte) 2);
    b.put((byte) 3);
    b.put((byte) 4);
    fileHandle.write(b, 1, 2);
    assertEquals(2, fileHandle.getFilePointer());
    fileHandle.seek(0);
    assertEquals(2, fileHandle.readByte());
    assertEquals(3, fileHandle.readByte());
    if (fileHandle.length() > 2 && (fileHandle.readByte() == 4)) {
      fail("Incorrect length or trailing bytes.");
    }
  }

}
