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
 * Tests for reading shorts from a loci.common.IRandomAccess.
 *
 * @see loci.common.IRandomAccess
 */
@Test(groups={"readTests", "readLineTest"})
public class ReadLineTest {

  private static final byte[] PAGE = new byte[] {
    // a, b, c, \n
    (byte) 0x61, (byte) 0x62, (byte) 0x63, (byte) 0x0A,
    // d, e, f, \n
    (byte) 0x64, (byte) 0x65, (byte) 0x66, (byte) 0x0A,
    // g, h, i, \n
    (byte) 0x67, (byte) 0x68, (byte) 0x69, (byte) 0x0A,
    // j, k, l, \n
    (byte) 0x6A, (byte) 0x6B, (byte) 0x6C, (byte) 0x0A
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
    assertEquals(16, fileHandle.length());
  }

  @Test
  public void testSequential() throws IOException {
    assertEquals("abc", fileHandle.readLine());
    assertEquals(4, fileHandle.getFilePointer());
    assertEquals("def", fileHandle.readLine());
    assertEquals(8, fileHandle.getFilePointer());
    assertEquals("ghi", fileHandle.readLine());
    assertEquals(12, fileHandle.getFilePointer());
    assertEquals("jkl", fileHandle.readLine());
    assertEquals(16, fileHandle.getFilePointer());
  }

  @Test
  public void testSeekForward() throws IOException {
    fileHandle.seek(8);
    assertEquals("ghi", fileHandle.readLine());
    assertEquals(12, fileHandle.getFilePointer());
  }

  @Test
  public void testReset() throws IOException {
    assertEquals("abc", fileHandle.readLine());
    assertEquals(4, fileHandle.getFilePointer());
    assertEquals("def", fileHandle.readLine());
    assertEquals(8, fileHandle.getFilePointer());
    fileHandle.seek(0);
    assertEquals("abc", fileHandle.readLine());
    assertEquals(4, fileHandle.getFilePointer());
    assertEquals("def", fileHandle.readLine());
    assertEquals(8, fileHandle.getFilePointer());
  }

  @Test
  public void testSeekBack() throws IOException {
    fileHandle.seek(16);
    fileHandle.seek(8);
    assertEquals("ghi", fileHandle.readLine());
    assertEquals(12, fileHandle.getFilePointer());
    assertEquals("jkl", fileHandle.readLine());
    assertEquals(16, fileHandle.getFilePointer());
  }

  @Test
  public void testPartial() throws IOException {
    assertEquals((byte) 0x61, fileHandle.readByte());
    assertEquals(1, fileHandle.getFilePointer());
    assertEquals("bc", fileHandle.readLine());
    assertEquals((byte) 0x64, fileHandle.readByte());
  }

  @Test
  public void testRandomAccess() throws IOException {
    testSeekForward();
    testSeekBack();
    // The test relies on a "new" file or reset file pointer
    fileHandle.seek(0);
    testPartial();
    // The test relies on a "new" file or reset file pointer
    fileHandle.seek(0);
    testReset();
  }

}
