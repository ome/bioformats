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
 * Tests for reading floats from a loci.common.IRandomAccess.
 *
 * @see loci.common.IRandomAccess
 */
@Test(groups="readTests")
public class ReadFloatTest {

  private static final byte[] PAGE = new byte[] {
    // 0.0 (0x00000000)
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    // 1.0 (0x3F800000)
    (byte) 0x3F, (byte) 0x80, (byte) 0x00, (byte) 0x00,
    // -1.0 (0xBF800000)
    (byte) 0xBF, (byte) 0x80, (byte) 0x00, (byte) 0x00,
    // 3.1415927 (0x40490FDB)
    (byte) 0x40, (byte) 0x49, (byte) 0x0F, (byte) 0xDB,
    // MAX_VALUE (0x7F7FFFFF)
    (byte) 0x7F, (byte) 0x7F, (byte) 0xFF, (byte) 0xFF,
    // NEGATIVE_INFINITY (0xFF800000)
    (byte) 0xFF, (byte) 0x80, (byte) 0x00, (byte) 0x00,
    // NaN (0x7FC00000)
    (byte) 0x7F, (byte) 0xC0, (byte) 0x00, (byte) 0x00
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
    assertEquals(28, fileHandle.length());
  }

  @Test
  public void testSequential() throws IOException {
    assertEquals(0.0f, fileHandle.readFloat());
    assertEquals(1.0f, fileHandle.readFloat());
    assertEquals(-1.0f, fileHandle.readFloat());
    assertEquals(3.1415927f, fileHandle.readFloat());
    assertEquals(Float.MAX_VALUE, fileHandle.readFloat());
    assertEquals(Float.NEGATIVE_INFINITY, fileHandle.readFloat());
    assertEquals(Float.NaN, fileHandle.readFloat());
  }

  @Test
  public void testSeekForward() throws IOException {
    fileHandle.seek(8);
    assertEquals(-1.0f, fileHandle.readFloat());
    assertEquals(3.1415927f, fileHandle.readFloat());
  }

  @Test
  public void testReset() throws IOException {
    assertEquals(0.0f, fileHandle.readFloat());
    assertEquals(1.0f, fileHandle.readFloat());
    fileHandle.seek(0);
    assertEquals(0.0f, fileHandle.readFloat());
    assertEquals(1.0f, fileHandle.readFloat());
  }

  @Test
  public void testSeekBack() throws IOException {
    fileHandle.seek(16);
    fileHandle.seek(8);
    assertEquals(-1.0f, fileHandle.readFloat());
    assertEquals(3.1415927f, fileHandle.readFloat());
  }

  @Test
  public void testRandomAccess() throws IOException {
    testSeekForward();
    testSeekBack();
    // The test relies on a "new" file or reset file pointer
    fileHandle.seek(0);
    testReset();
  }

}
