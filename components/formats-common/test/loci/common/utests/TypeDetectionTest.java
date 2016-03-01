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

import java.io.File;
import java.io.IOException;

import loci.common.BZip2Handle;
import loci.common.GZipHandle;
import loci.common.ZipHandle;

import org.testng.annotations.Test;

/**
 * Tests compressed IRandomAccess implementation type detection.
 *
 * @see loci.common.IRandomAcess
 */
public class TypeDetectionTest {

  @Test
  public void testBZip2TypeDetection() throws IOException {
    File invalidFile = File.createTempFile("invalid", ".bz2");
    invalidFile.deleteOnExit();
    assertEquals(BZip2Handle.isBZip2File(invalidFile.getAbsolutePath()), false);
  }

  @Test
  public void testGZipTypeDetection() throws IOException {
    File invalidFile = File.createTempFile("invalid", ".gz");
    invalidFile.deleteOnExit();
    assertEquals(GZipHandle.isGZipFile(invalidFile.getAbsolutePath()), false);
  }

  @Test
  public void testZipTypeDetection() throws IOException {
    File invalidFile = File.createTempFile("invalid", ".zip");
    invalidFile.deleteOnExit();
    assertEquals(ZipHandle.isZipFile(invalidFile.getAbsolutePath()), false);
  }
}
