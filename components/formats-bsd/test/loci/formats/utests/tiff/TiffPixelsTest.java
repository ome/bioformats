/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

import static org.testng.AssertJUnit.assertEquals;

import java.io.IOException;

import loci.common.ByteArrayHandle;
import loci.common.RandomAccessInputStream;
import loci.common.RandomAccessOutputStream;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffCompression;
import loci.formats.tiff.TiffParser;
import loci.formats.tiff.TiffSaver;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Tests saving and reading TIFF pixel data that has been compressed using
 * various schemes.
 */
public class TiffPixelsTest {

  private static final int IMAGE_WIDTH = 64;

  private static final int IMAGE_LENGTH = 64;

  private static final int BITS_PER_PIXEL = 16;

  private IFD ifd = new IFD();

  private byte[] data;

  @BeforeMethod
  public void setUp() {
    ifd.put(IFD.IMAGE_WIDTH, IMAGE_WIDTH);
    ifd.put(IFD.IMAGE_LENGTH, IMAGE_LENGTH);
    ifd.put(IFD.BITS_PER_SAMPLE, new int[] { BITS_PER_PIXEL });
    ifd.put(IFD.SAMPLES_PER_PIXEL, 1);
    ifd.put(IFD.LITTLE_ENDIAN, Boolean.TRUE);
    ifd.put(IFD.ROWS_PER_STRIP, new long[] {IMAGE_LENGTH});
    ifd.put(IFD.STRIP_OFFSETS, new long[] {0});
    data = new byte[IMAGE_WIDTH * IMAGE_LENGTH * (BITS_PER_PIXEL / 8)];
    for (int i=0; i<data.length; i++) {
      data[i] = (byte) i;
    }
    ifd.put(IFD.STRIP_BYTE_COUNTS, new long[] {data.length});
  }

  @Test
  public void testUNCOMPRESSED() throws FormatException, IOException {
    ifd.put(IFD.COMPRESSION, TiffCompression.UNCOMPRESSED.getCode());
    byte[] plane = readSavedPlane();
    for (int i=0; i<plane.length; i++) {
      assertEquals(plane[i], data[i]);
    }
  }

  @Test(expectedExceptions={ FormatException.class })
  public void testCCITT_1D() throws FormatException, IOException {
    ifd.put(IFD.COMPRESSION, TiffCompression.CCITT_1D.getCode());
    byte[] plane = readSavedPlane();
    for (int i=0; i<plane.length; i++) {
      assertEquals(plane[i], data[i]);
    }
  }

  @Test(expectedExceptions={ FormatException.class })
  public void testGROUP_3_FAX() throws FormatException, IOException {
    ifd.put(IFD.COMPRESSION, TiffCompression.GROUP_3_FAX.getCode());
    byte[] plane = readSavedPlane();
    for (int i=0; i<plane.length; i++) {
      assertEquals(plane[i], data[i]);
    }
  }

  @Test(expectedExceptions={ FormatException.class })
  public void testGROUP_4_FAX() throws FormatException, IOException {
    ifd.put(IFD.COMPRESSION, TiffCompression.GROUP_4_FAX.getCode());
    byte[] plane = readSavedPlane();
    for (int i=0; i<plane.length; i++) {
      assertEquals(plane[i], data[i]);
    }
  }

  @Test
  public void testLZW() throws FormatException, IOException {
    ifd.put(IFD.COMPRESSION, TiffCompression.LZW.getCode());
    byte[] plane = readSavedPlane();
    for (int i=0; i<plane.length; i++) {
      assertEquals(plane[i], data[i]);
    }
  }

  @Test(expectedExceptions={ FormatException.class })
  public void testPACK_BITS() throws FormatException, IOException {
    ifd.put(IFD.COMPRESSION, TiffCompression.PACK_BITS.getCode());
    byte[] plane = readSavedPlane();
    for (int i=0; i<plane.length; i++) {
      assertEquals(plane[i], data[i]);
    }
  }

  @Test
  public void testPROPRIETARY_DEFLATE() throws FormatException, IOException {
    ifd.put(IFD.COMPRESSION, TiffCompression.PROPRIETARY_DEFLATE.getCode());
    byte[] plane = readSavedPlane();
    for (int i=0; i<plane.length; i++) {
      assertEquals(plane[i], data[i]);
    }
  }

  @Test
  public void testDEFLATE() throws FormatException, IOException {
    ifd.put(IFD.COMPRESSION, TiffCompression.DEFLATE.getCode());
    byte[] plane = readSavedPlane();
    for (int i=0; i<plane.length; i++) {
      assertEquals(plane[i], data[i]);
    }
  }

  // -- Helper method --

  private byte[] readSavedPlane() throws FormatException, IOException {
    ByteArrayHandle savedData = new ByteArrayHandle();
    RandomAccessOutputStream out = new RandomAccessOutputStream(savedData);
    RandomAccessInputStream in = new RandomAccessInputStream(savedData);
    TiffSaver saver = new TiffSaver(out, savedData);
    //saver.setInputStream(in);
    saver.writeImage(data, ifd, 0, FormatTools.UINT16, false);
    out.close();
    TiffParser parser = new TiffParser(in);
    byte[] plane = new byte[data.length];
    parser.getSamples(ifd, plane);
    in.close();
    return plane;
  }

}
