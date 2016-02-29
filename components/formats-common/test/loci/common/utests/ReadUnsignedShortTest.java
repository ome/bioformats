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
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Tests for reading unsigned shorts from a loci.common.IRandomAccess.
 *
 * @see loci.common.IRandomAccess
 */
@Test(groups="readTests")
public class ReadUnsignedShortTest {

  private static final byte[] PAGE = new byte[] {
    // 16-bit shorts
    (byte) 0x00, (byte) 0x01, (byte) 0x0F, (byte) 0x02,
    (byte) 0x00, (byte) 0x03, (byte) 0x0F, (byte) 0x04,
    (byte) 0x00, (byte) 0x05, (byte) 0x0F, (byte) 0x06,
    (byte) 0x00, (byte) 0x07, (byte) 0x0F, (byte) 0x08,
    (byte) 0x00, (byte) 0x09, (byte) 0x0F, (byte) 0x0A,
    (byte) 0x00, (byte) 0x0B, (byte) 0x0F, (byte) 0x0C,
    (byte) 0x00, (byte) 0x0D, (byte) 0xFF, (byte) 0xFF,
    (byte) 0x00, (byte) 0x0F, (byte) 0xFF, (byte) 0xFE
  };

  private static final String MODE = "r";

  private static final int BUFFER_SIZE = 1024;

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
    assertEquals(32, fileHandle.length());
  }

  @Test
  public void testSequentialReadUnsignedShort() throws IOException {
    assertEquals(1, fileHandle.readUnsignedShort());
    assertEquals(3842, fileHandle.readUnsignedShort());
    assertEquals(3, fileHandle.readUnsignedShort());
    assertEquals(3844, fileHandle.readUnsignedShort());
    assertEquals(5, fileHandle.readUnsignedShort());
    assertEquals(3846, fileHandle.readUnsignedShort());
    assertEquals(7, fileHandle.readUnsignedShort());
    assertEquals(3848, fileHandle.readUnsignedShort());
    assertEquals(9, fileHandle.readUnsignedShort());
    assertEquals(3850, fileHandle.readUnsignedShort());
    assertEquals(11, fileHandle.readUnsignedShort());
    assertEquals(3852, fileHandle.readUnsignedShort());
    assertEquals(13, fileHandle.readUnsignedShort());
    assertEquals(65535, fileHandle.readUnsignedShort());
    assertEquals(15, fileHandle.readUnsignedShort());
    assertEquals(65534, fileHandle.readUnsignedShort());
  }

  @Test
  public void testSeekForwardReadUnsignedShort() throws IOException {
    fileHandle.seek(8);
    assertEquals(5, fileHandle.readUnsignedShort());
    assertEquals(3846, fileHandle.readUnsignedShort());
  }

  @Test
  public void testResetReadUnsignedShort() throws IOException {
    assertEquals(1, fileHandle.readUnsignedShort());
    assertEquals(3842, fileHandle.readUnsignedShort());
    fileHandle.seek(0);
    assertEquals(1, fileHandle.readUnsignedShort());
    assertEquals(3842, fileHandle.readUnsignedShort());
  }

  @Test
  public void testSeekBackReadUnsignedShort() throws IOException {
    fileHandle.seek(16);
    fileHandle.seek(8);
    assertEquals(5, fileHandle.readUnsignedShort());
    assertEquals(3846, fileHandle.readUnsignedShort());
  }

  @Test
  public void testRandomAccessReadUnsignedShort() throws IOException {
    testSeekForwardReadUnsignedShort();
    testSeekBackReadUnsignedShort();
    // The test relies on a "new" file or reset file pointer
    fileHandle.seek(0);
    testResetReadUnsignedShort();
  }

}
