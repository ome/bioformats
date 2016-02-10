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
import java.util.Random;

import loci.common.ByteArrayHandle;
import loci.common.IRandomAccess;
import loci.common.RandomAccessInputStream;
import loci.common.RandomAccessOutputStream;
import loci.common.utests.providers.IRandomAccessProvider;
import loci.common.utests.providers.IRandomAccessProviderFactory;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Tests for reading bytes from a loci.common.RandomAccessInputStream.
 *
 * @see loci.common.RandomAccessInputStream
 */
@Test(groups="readTests")
public class RandomAccessInputStreamTest {

  private static final byte[] PAGE = new byte[] {
    0, 4, 8, 12, 16, 20, 24, 28,
    32, 36, 40, 44, 48, 52, 56, 60,
    64, 68, 72, 76, 80, 84, 88, 92,
    96, 100, 104, 108, 112, 116, 120, 124,
    (byte) 128, (byte) 132, (byte) 136, (byte) 140, (byte) 144, (byte) 148,
    (byte) 152, (byte) 156, (byte) 160, (byte) 164, (byte) 168, (byte) 172,
    (byte) 176, (byte) 180, (byte) 184, (byte) 188, (byte) 192, (byte) 196,
    (byte) 200, (byte) 204, (byte) 208, (byte) 212, (byte) 216, (byte) 220,
    (byte) 224, (byte) 228, (byte) 232, (byte) 236, (byte) 240, (byte) 244,
    (byte) 248, (byte) 252
  };

  private static final String MODE = "r";
  private static final int BUFFER_SIZE = 2;

  private RandomAccessInputStream stream;
  private IRandomAccess fileHandle;

  @Parameters({"provider"})
  @BeforeMethod
  public void setUp(String provider) throws IOException {
    IRandomAccessProviderFactory factory = new IRandomAccessProviderFactory();
    IRandomAccessProvider instance = factory.getInstance(provider);
    fileHandle = instance.createMock(PAGE, MODE, BUFFER_SIZE);
    stream = new RandomAccessInputStream(fileHandle);
  }

  @Test
  public void testLength() throws IOException {
    assertEquals(PAGE.length, stream.length());
  }

  @Test
  public void testSequentialRead() throws IOException {
    stream.seek(0);
    for (int i=0; i<stream.length(); i++) {
      assertEquals(PAGE[i], stream.readByte());
    }
  }

  @Test
  public void testReverseSequentialRead() throws IOException {
    stream.seek(stream.length() - 1);
    for (int i=PAGE.length-1; i>=0; i--) {
      assertEquals(PAGE[i], stream.readByte());
      if (stream.getFilePointer() >= 2) {
        stream.seek(stream.getFilePointer() - 2);
      }
    }
  }

  @Test
  public void testArrayRead() throws IOException {
    stream.seek(0);
    byte[] buf = new byte[PAGE.length];
    stream.read(buf);
    for (int i=0; i<PAGE.length; i++) {
      assertEquals(PAGE[i], buf[i]);
    }
  }

  @Test
  public void testRandomRead() throws IOException {
    long fp = PAGE.length / 2;
    stream.seek(fp);
    for (int i=0; i<PAGE.length; i++) {
      boolean forward = (i % 2) == 0;
      int step = i / 2;
      if (forward) {
        stream.seek(fp + step);
      }
      else {
        stream.seek(fp - step);
      }
      assertEquals(PAGE[(int) stream.getFilePointer()], stream.readByte());
    }
  }

  @Test
  public void testBitRead() throws IOException {
    int count = 50000;
    int[] nums = new int[count];
    int[] len = new int[count];
    ByteArrayHandle handle = new ByteArrayHandle();
    RandomAccessOutputStream out = new RandomAccessOutputStream(handle);
    Random r = new Random();

    for (int i=0; i<count; i++) {
      if (i % 32 == 31) {
        nums[i] = r.nextInt();
      }
      else {
        nums[i] = r.nextInt(1 << (i % 32));
      }

      len[i] = Integer.toBinaryString(nums[i]).length();
      out.writeBits(nums[i], len[i]);
    }
    out.close();

    RandomAccessInputStream in = new RandomAccessInputStream(handle);
    in.seek(0);
    try {
      for (int i=0; i<count; i++) {
        int c = r.nextInt(100);
        if (c >= 50) {
          int v = in.readBits(len[i]);
          assertEquals(v, nums[i]);
        }
        else {
          in.skipBits(len[i]);
        }
      }
    }
    finally {
      in.close();
    }
  }

}
