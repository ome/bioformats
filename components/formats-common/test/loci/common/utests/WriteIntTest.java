/*
 * #%L
 * Common package for I/O and related utilities
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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
 * Tests for reading ints from a loci.common.IRandomAccess.
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
