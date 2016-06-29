/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
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

package loci.formats.utests;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import loci.formats.ImageReader;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests for checking the speed and accuracy of isThisType(String, boolean) in
 * IFormatReader.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class IsThisTypeTests {

  // The test file is 'C1979091.h5' from:
  // http://www.hdfgroup.org/training/other-ex5/sample-programs/convert/Conversion.html
  private static final String TEST_FILE = "test.h5";
  private static final long TIMEOUT = 2000;

  private ImageReader openReader;
  private ImageReader noOpenReader;

  @BeforeMethod
  public void setUp() {
    openReader = new ImageReader();
    noOpenReader = new ImageReader();
    noOpenReader.setAllowOpenFiles(false);
  }

  @Test
  public void testAccuracy() {
    boolean openReaderIsValid = openReader.isThisType(TEST_FILE);
    boolean noOpenReaderIsValid = noOpenReader.isThisType(TEST_FILE);

    assertEquals(openReaderIsValid, noOpenReaderIsValid);
    assertEquals(openReaderIsValid, false);
  }

  @Test
  public void testTypeCheckingSpeed() {
    long t0 = System.currentTimeMillis();
    openReader.isThisType(TEST_FILE);
    long t1 = System.currentTimeMillis();
    noOpenReader.isThisType(TEST_FILE);
    long t2 = System.currentTimeMillis();

    assertTrue((t1 - t0) < TIMEOUT);
    assertTrue((t2 - t1) < TIMEOUT);
  }

}
