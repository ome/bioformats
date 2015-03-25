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
 * Tests for reading doubles from a loci.common.IRandomAccess.
 *
 * @see loci.common.IRandomAccess
 */
@Test(groups="readTests")
public class ReadDoubleTest {

  private static final byte[] PAGE = new byte[] {
    // 0.0 (0x0000000000000000L)
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    // 1.0 (0x3FF0000000000000L)
    (byte) 0x3F, (byte) 0xF0, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    // -1.0 (0XBFF0000000000000L)
    (byte) 0xBF, (byte) 0xF0, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    // 3.1415926535897930 (0x400921FB54442D18L)
    (byte) 0x40, (byte) 0x09, (byte) 0x21, (byte) 0xFB,
    (byte) 0x54, (byte) 0x44, (byte) 0x2D, (byte) 0x18,
    // MAX_VALUE (0x7FEFFFFFFFFFFFFFL)
    (byte) 0x7F, (byte) 0xEF, (byte) 0xFF, (byte) 0xFF,
    (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
    // NEGATIVE_INFINITY (0xFFF0000000000000L)
    (byte) 0xFF, (byte) 0xF0, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    // NaN (0x7FF8000000000000L)
    (byte) 0x7F, (byte) 0xF8, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00
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
    assertEquals(56, fileHandle.length());
  }

  @Test
  public void testSequential() throws IOException {
    assertEquals(0.0d, fileHandle.readDouble());
    assertEquals(1.0d, fileHandle.readDouble());
    assertEquals(-1.0d, fileHandle.readDouble());
    assertEquals(3.1415926535897930d, fileHandle.readDouble());
    assertEquals(Double.MAX_VALUE, fileHandle.readDouble());
    assertEquals(Double.NEGATIVE_INFINITY, fileHandle.readDouble());
    assertEquals(Double.NaN, fileHandle.readDouble());
  }

  @Test
  public void testSeekForward() throws IOException {
    fileHandle.seek(16);
    assertEquals(-1.0d, fileHandle.readDouble());
    assertEquals(3.1415926535897930d, fileHandle.readDouble());
  }

  @Test
  public void testReset() throws IOException {
    assertEquals(0.0d, fileHandle.readDouble());
    assertEquals(1.0d, fileHandle.readDouble());
    fileHandle.seek(0);
    assertEquals(0.0d, fileHandle.readDouble());
    assertEquals(1.0d, fileHandle.readDouble());
  }

  @Test
  public void testSeekBack() throws IOException {
    fileHandle.seek(32);
    fileHandle.seek(16);
    assertEquals(-1.0d, fileHandle.readDouble());
    assertEquals(3.1415926535897930d, fileHandle.readDouble());
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
