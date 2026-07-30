/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2006 - 2017 Open Microscopy Environment:
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

import loci.formats.IFormatWriter;
import loci.formats.ImageWriter;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Asserts that {@link ImageWriter} discovery on a BSD-only classpath does
 * not load the GPL Hamamatsu NDPI writer (loci.formats.out.NDPIWriter), and
 * that BSD-only writers such as the TIFF writer are still discovered.
 */
public class WriterDiscoveryTest {

  private static final String NDPI_WRITER_CLASS = "loci.formats.out.NDPIWriter";
  private static final String TIFF_WRITER_CLASS = "loci.formats.out.TiffWriter";

  @Test
  public void testNDPIWriterUnavailableOnBSDClasspath() throws Exception {
    ImageWriter writer = new ImageWriter();
    try {
      int ndpiWriterCount = 0;
      for (IFormatWriter candidate : writer.getWriters()) {
        if (candidate.getClass().getName().equals(NDPI_WRITER_CLASS)) {
          ndpiWriterCount++;
        }
      }
      Assert.assertEquals(ndpiWriterCount, 0,
        "BSD-only writer discovery must not load the GPL NDPI writer");
    }
    finally {
      writer.close();
    }
  }

  @Test
  public void testBSDWritersRemainDiscoverable() throws Exception {
    ImageWriter writer = new ImageWriter();
    try {
      boolean foundTiffWriter = false;
      for (IFormatWriter candidate : writer.getWriters()) {
        if (candidate.getClass().getName().equals(TIFF_WRITER_CLASS)) {
          foundTiffWriter = true;
        }
      }
      Assert.assertTrue(foundTiffWriter,
        "BSD-only writer discovery must retain available writers");
    }
    finally {
      writer.close();
    }
  }

}
