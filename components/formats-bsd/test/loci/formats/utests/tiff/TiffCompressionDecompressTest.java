/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
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

package loci.formats.utests.tiff;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.fail;

import java.io.EOFException;
import java.io.IOException;
import java.util.zip.ZipException;

import loci.formats.FormatException;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.NikonCodecOptions;
import loci.formats.tiff.TiffCompression;

import org.testng.annotations.Test;

/**
 * Tests the various TIFF decompression schemes.
 */
public class TiffCompressionDecompressTest {

  private static final byte[] DATA = new byte[64];
  
  private static final CodecOptions OPTIONS = new CodecOptions();

  static {
    OPTIONS.width = 8;
    OPTIONS.height = 8;
    OPTIONS.bitsPerSample = 8;
    OPTIONS.interleaved = true;
    OPTIONS.littleEndian = true;
    OPTIONS.maxBytes = DATA.length;
  }

  @Test
  public void testUNCOMPRESSED() throws FormatException, IOException {
    TiffCompression compression = TiffCompression.UNCOMPRESSED;
    assertNotNull(compression.decompress(DATA, OPTIONS));
  }

  @Test(expectedExceptions={ FormatException.class })
  public void testCCITT_1D() throws FormatException, IOException {
    TiffCompression compression = TiffCompression.CCITT_1D;
    assertNotNull(compression.decompress(DATA, OPTIONS));
  }

  @Test(expectedExceptions={ FormatException.class })
  public void testGROUP_3_FAX() throws FormatException, IOException {
    TiffCompression compression = TiffCompression.GROUP_3_FAX;
    assertNotNull(compression.decompress(DATA, OPTIONS));
  }

  @Test(expectedExceptions={ FormatException.class })
  public void testGROUP_4_FAX() throws FormatException, IOException {
    TiffCompression compression = TiffCompression.GROUP_3_FAX;
    assertNotNull(compression.decompress(DATA, OPTIONS));
  }

  @Test
  public void testLZW() throws FormatException, IOException {
    TiffCompression compression = TiffCompression.LZW;
    try {
      assertNotNull(compression.decompress(DATA, OPTIONS));
    }
    catch (FormatException e) {
      if (!(e.getCause() instanceof EOFException)) {
        fail("Unexpected exception: " + e);
      }
    }
  }

  // Needs to have a "properly" encoded JPEG, throws an NPE otherwise
  @Test(expectedExceptions={ NullPointerException.class })
  public void testJPEG() throws FormatException, IOException {
    TiffCompression compression = TiffCompression.JPEG;
    assertNotNull(compression.decompress(DATA, OPTIONS));
  }

  @Test
  public void testPACK_BITS() throws FormatException, IOException {
    TiffCompression compression = TiffCompression.PACK_BITS;
    assertNotNull(compression.decompress(DATA, OPTIONS));
  }

  @Test
  public void testPROPRIETARY_DEFLATE() throws FormatException, IOException {
    TiffCompression compression = TiffCompression.PROPRIETARY_DEFLATE;
    try {
      assertNotNull(compression.decompress(DATA, OPTIONS));
    }
    catch (FormatException e) {
      if (!(e.getCause() instanceof ZipException)) {
        fail("Unexpected exception: " + e);
      }
    }
  }

  @Test
  public void testDEFLATE() throws FormatException, IOException {
    TiffCompression compression = TiffCompression.DEFLATE;
    try {
      assertNotNull(compression.decompress(DATA, OPTIONS));
    }
    catch (FormatException e) {
      if (!(e.getCause() instanceof ZipException)) {
        fail("Unexpected exception: " + e);
      }
    }
  }

  @Test(expectedExceptions={ FormatException.class })
  public void testTHUNDERSCAN() throws FormatException, IOException {
    TiffCompression compression = TiffCompression.THUNDERSCAN;
    assertNotNull(compression.decompress(DATA, OPTIONS));
  }

  // Needs to have "properly" encoded JP2, throws a runtime exception otherwise
  @Test(expectedExceptions={ RuntimeException.class })
  public void testJPEG_2000() throws FormatException, IOException {
    TiffCompression compression = TiffCompression.JPEG_2000;
    assertNotNull(compression.decompress(DATA, OPTIONS));
  }

  // Needs to have "properly" encoded JP2, throws a runtime exception otherwise
  @Test(expectedExceptions={ RuntimeException.class })
  public void testJPEG_2000_LOSSY() throws FormatException, IOException {
    TiffCompression compression = TiffCompression.JPEG_2000_LOSSY;
    assertNotNull(compression.decompress(DATA, OPTIONS));
  }

  // Needs to have a "properly" encoded JPEG, throws an NPE otherwise
  @Test(expectedExceptions={ NullPointerException.class })
  public void testALT_JPEG() throws FormatException, IOException {
    TiffCompression compression = TiffCompression.ALT_JPEG;
    assertNotNull(compression.decompress(DATA, OPTIONS));
  }

  @Test
  public void testNIKON() throws FormatException, IOException {
    TiffCompression compression = TiffCompression.NIKON;
    NikonCodecOptions options = new NikonCodecOptions();
    options.interleaved = OPTIONS.interleaved;
    options.littleEndian = OPTIONS.littleEndian;
    options.maxBytes = OPTIONS.maxBytes;
    options.curve = new int[] { 1 };
    options.vPredictor = new int[] { 1 };
    options.lossless = true;
    options.split = 1;
    assertNotNull(compression.decompress(DATA, options));
  }

  @Test(expectedExceptions={ FormatException.class })
  public void testLURAWAVE() throws FormatException, IOException {
    TiffCompression compression = TiffCompression.LURAWAVE;
    assertNotNull(compression.decompress(DATA, OPTIONS));
  }
}
