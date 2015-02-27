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
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/test/loci/common/utests/WriteUTFTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/test/loci/common/utests/WriteUTFTest.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see loci.common.IRandomAccess
 */
@Test(groups="writeTests")
public class WriteUTFTest {

  private static final byte[] PAGE = new byte[] {
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
    (byte) 0x00, (byte) 0x00
  };

  private static final String MODE = "rw";

  private static final int BUFFER_SIZE = 1024;

  private IRandomAccess fileHandle;

  // Unused right now.
  //private boolean checkGrowth;

  @Parameters({"provider", "checkGrowth"})
  @BeforeMethod
  public void setUp(String provider, @Optional("false") String checkGrowth)
    throws IOException {
    // Unused right now.
    //this.checkGrowth = Boolean.parseBoolean(checkGrowth);
    IRandomAccessProviderFactory factory = new IRandomAccessProviderFactory();
    IRandomAccessProvider instance = factory.getInstance(provider);
    fileHandle = instance.createMock(PAGE, MODE, BUFFER_SIZE);
  }

  @Test(groups="initialLengthTest")
  public void testLength() throws IOException {
    assertEquals(10, fileHandle.length());
  }
  
  @Test
  public void testWriteSequential() throws IOException {
    fileHandle.writeUTF("\u00A9");  // Copyright sign (2 bytes)
    fileHandle.writeUTF("\u2260");  // Not equal to (3 bytes)
    fileHandle.writeUTF("\u00A9");  // Copyright sign (2 bytes)
    fileHandle.writeUTF("\u2260");  // Not equal to (3 bytes)
    fileHandle.seek(0);
    assertEquals((byte) 0x00, fileHandle.readByte());
    assertEquals((byte) 0x02, fileHandle.readByte());
    assertEquals((byte) 0xC2, fileHandle.readByte());
    assertEquals((byte) 0xA9, fileHandle.readByte());
    assertEquals((byte) 0x00, fileHandle.readByte());
    assertEquals((byte) 0x03, fileHandle.readByte());
    assertEquals((byte) 0xE2, fileHandle.readByte());
    assertEquals((byte) 0x89, fileHandle.readByte());
    assertEquals((byte) 0xA0, fileHandle.readByte());
    assertEquals((byte) 0x00, fileHandle.readByte());
    assertEquals((byte) 0x02, fileHandle.readByte());
    assertEquals((byte) 0xC2, fileHandle.readByte());
    assertEquals((byte) 0xA9, fileHandle.readByte());
    assertEquals((byte) 0x00, fileHandle.readByte());
    assertEquals((byte) 0x03, fileHandle.readByte());
    assertEquals((byte) 0xE2, fileHandle.readByte());
    assertEquals((byte) 0x89, fileHandle.readByte());
    assertEquals((byte) 0xA0, fileHandle.readByte());
    fileHandle.seek(0);
    assertEquals("\u00A9", fileHandle.readUTF());
    assertEquals("\u2260", fileHandle.readUTF());
    assertEquals("\u00A9", fileHandle.readUTF());
    assertEquals("\u2260", fileHandle.readUTF());
  }

  @Test
  public void testWrite() throws IOException {
    fileHandle.writeUTF("\u00A9\n");  // Copyright sign (2 bytes)
    assertEquals(5, fileHandle.getFilePointer());
    fileHandle.seek(0);
    assertEquals((byte) 0x00, fileHandle.readByte());
    assertEquals((byte) 0x03, fileHandle.readByte());
    assertEquals((byte) 0xC2, fileHandle.readByte());
    assertEquals((byte) 0xA9, fileHandle.readByte());
    fileHandle.seek(0);
    assertEquals("\u00A9\n", fileHandle.readUTF());
  }

  @Test
  public void testWriteTwoCharacters() throws IOException {
    fileHandle.seek(0);
    fileHandle.writeUTF("\u00A9\u2260");
    assertEquals(7, fileHandle.getFilePointer());
    fileHandle.seek(0);
    assertEquals((byte) 0x00, fileHandle.readByte());
    assertEquals((byte) 0x05, fileHandle.readByte());
    assertEquals((byte) 0xC2, fileHandle.readByte());
    assertEquals((byte) 0xA9, fileHandle.readByte());
    fileHandle.seek(0);
    assertEquals("\u00A9\u2260", fileHandle.readUTF());
  }

  @Test
  public void testWriteOffEnd() throws IOException {
    fileHandle.seek(10);
    fileHandle.writeUTF("\u00A9");  // Copyright sign (2 bytes)
    assertEquals(14, fileHandle.getFilePointer());
    assertEquals(14, fileHandle.length());
    fileHandle.seek(10);
    assertEquals((byte) 0x00, fileHandle.readByte());
    assertEquals((byte) 0x02, fileHandle.readByte());
    assertEquals((byte) 0xC2, fileHandle.readByte());
    assertEquals((byte) 0xA9, fileHandle.readByte());
    fileHandle.seek(10);
    assertEquals("\u00A9", fileHandle.readUTF());
  }

  @Test
  public void testWriteTwiceOffEnd() throws IOException {
    fileHandle.seek(10);
    fileHandle.writeUTF("\u00A9");  // Copyright sign (2 bytes)
    fileHandle.writeUTF("\u2260");  // Not equal to (3 bytes)
    assertEquals(19, fileHandle.getFilePointer());
    assertEquals(19, fileHandle.length());
    fileHandle.seek(10);
    assertEquals((byte) 0x00, fileHandle.readByte());
    assertEquals((byte) 0x02, fileHandle.readByte());
    assertEquals((byte) 0xC2, fileHandle.readByte());
    assertEquals((byte) 0xA9, fileHandle.readByte());
    assertEquals((byte) 0x00, fileHandle.readByte());
    assertEquals((byte) 0x03, fileHandle.readByte());
    assertEquals((byte) 0xE2, fileHandle.readByte());
    assertEquals((byte) 0x89, fileHandle.readByte());
    assertEquals((byte) 0xA0, fileHandle.readByte());
    fileHandle.seek(10);
    assertEquals("\u00A9", fileHandle.readUTF());
    assertEquals("\u2260", fileHandle.readUTF());
  }

}
