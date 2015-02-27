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
 * Tests for reading doubles from a loci.common.IRandomAccess.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/test/loci/common/utests/WriteDoubleTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/test/loci/common/utests/WriteDoubleTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see loci.common.IRandomAccess
 */
@Test(groups="writeTests")
public class WriteDoubleTest {

  private static final byte[] PAGE = new byte[] {
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
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
    assertEquals(56, fileHandle.length());
  }

  @Test
  public void testSequential() throws IOException {
    fileHandle.writeDouble(0.0d);
    if (checkGrowth) {
      assertEquals(8, fileHandle.length());
    }
    fileHandle.writeDouble(1.0d);
    if (checkGrowth) {
      assertEquals(16, fileHandle.length());
    }
    fileHandle.writeDouble(-1.0d);
    if (checkGrowth) {
      assertEquals(24, fileHandle.length());
    }
    fileHandle.writeDouble(3.1415926535897930d);
    if (checkGrowth) {
      assertEquals(32, fileHandle.length());
    }
    fileHandle.writeDouble(Double.MAX_VALUE);
    if (checkGrowth) {
      assertEquals(40, fileHandle.length());
    }
    fileHandle.writeDouble(Double.NEGATIVE_INFINITY);
    if (checkGrowth) {
      assertEquals(48, fileHandle.length());
    }
    fileHandle.writeDouble(Double.NaN);
    if (checkGrowth) {
      assertEquals(56, fileHandle.length());
    }
    fileHandle.seek(0);
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
    fileHandle.writeDouble(-1.0d);
    if (checkGrowth) {
      assertEquals(24, fileHandle.length());
    }
    fileHandle.writeDouble(3.1415926535897930d);
    if (checkGrowth) {
      assertEquals(32, fileHandle.length());
    }
    fileHandle.seek(16);
    assertEquals(-1.0d, fileHandle.readDouble());
    assertEquals(3.1415926535897930d, fileHandle.readDouble());
  }

  @Test
  public void testReset() throws IOException {
    fileHandle.writeDouble(0.0d);
    if (checkGrowth) {
      assertEquals(8, fileHandle.length());
    }
    fileHandle.writeDouble(1.0d);
    if (checkGrowth) {
      assertEquals(16, fileHandle.length());
    }
    fileHandle.seek(0);
    assertEquals(0.0d, fileHandle.readDouble());
    assertEquals(1.0d, fileHandle.readDouble());
    fileHandle.seek(0);
    fileHandle.writeDouble(-1.0d);
    fileHandle.writeDouble(3.1415926535897930d);
    fileHandle.seek(0);
    fileHandle.writeDouble(-1.0d);
    fileHandle.writeDouble(3.1415926535897930d);
  }

}
