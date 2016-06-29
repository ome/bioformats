/*
 * #%L
 * OME Bio-Formats manual and automated test suite.
 * %%
 * Copyright (C) 2006 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.tests.testng;

import static org.testng.AssertJUnit.fail;

import org.testng.annotations.Test;

import loci.formats.codec.CodecOptions;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffCompression;

/**
 * Tests the various codec used to compress and decompress data.
 * Not yet supported:
 * <code>Nikon</code>
 * <code>PackBits</code>
 * <code>LuraWave</code>
 *
 * @author Jean-Marie Burel <j dot burel at dundee dot ac dot uk>
 */
public class CompressDecompressTest {

  /**
   * Tests the writing of the tiles.
   * @param compression The compression to use.
   * @param lossy whether or not this is a lossy compression type
   */
  private void assertCompression(TiffCompression compression, boolean lossy)
    throws Exception
  {
    IFD ifd = new IFD();
    int w = 64;
    int h = 64;
    int bpp = 8;
    ifd.put(IFD.IMAGE_WIDTH, w);
    ifd.put(IFD.IMAGE_LENGTH, h);
    ifd.put(IFD.BITS_PER_SAMPLE, new int[] { bpp });
    ifd.put(IFD.SAMPLES_PER_PIXEL, 1);
    ifd.put(IFD.LITTLE_ENDIAN, Boolean.TRUE);


    byte[] plane = new byte[w * h * (bpp / 8)];
    for (int i=0; i<plane.length; i++) {
      plane[i] = (byte) i;
    }

    String beforeCompression, afterCompression, afterDecompression;
    CodecOptions options = compression.getCompressionCodecOptions(ifd);
    byte[] compressed;
    beforeCompression = TestTools.md5(plane);
    compressed = compression.compress(plane, options);
    afterCompression = TestTools.md5(compressed);
    if (compression.equals(TiffCompression.UNCOMPRESSED)) {
      if (!beforeCompression.equals(afterCompression)) {
        fail("Compression: "+compression.getCodecName()+" "+
            String.format("Compression MD5 %s != %s",
            beforeCompression, afterCompression));
      }
      afterDecompression = TestTools.md5(
          compression.decompress(compressed, options));
      if (!beforeCompression.equals(afterDecompression)) {
        fail("Compression: "+compression.getCodecName()+" "+
            String.format("Decompression MD5 %s != %s",
            beforeCompression, afterDecompression));
      }
    } else {
      if (beforeCompression.equals(afterCompression)) {
        fail("Compression: "+compression.getCodecName()+" "+
            String.format("Compression MD5 %s != %s",
            beforeCompression, afterCompression));
      }
      afterDecompression = TestTools.md5(
          compression.decompress(compressed, options));
      if (!lossy && !beforeCompression.equals(afterDecompression)) {
        fail("Compression: "+compression.getCodecName()+" "+
            String.format("Decompression MD5 %s != %s",
            beforeCompression, afterDecompression));
      }
    }
  }

  /**
   * Tests the compression and decompression using <code>JPEG2000</code>.
   * @throws Exception Throw if an error occurred while writing.
   */
  @Test
  public void testCompressDecompressedJ2KLossless() throws Exception {
    assertCompression( TiffCompression.JPEG_2000, false);
  }

  /**
   * Tests the compression and decompression using <code>JPEG2000-lossy</code>.
   * @throws Exception Throw if an error occurred while writing.
   */
  @Test
  public void testCompressDecompressedJ2KLossy() throws Exception {
    assertCompression( TiffCompression.JPEG_2000_LOSSY, true);
  }

  /**
   * Tests the compression and decompression using <code>JPEG</code>.
   * @throws Exception Throw if an error occurred while writing.
   */
  @Test
  public void testCompressDecompressedJPEG() throws Exception {
    assertCompression( TiffCompression.JPEG, true);
  }

  /**
   * Tests the compression and decompression using <code>Deflate</code>.
   * @throws Exception Throw if an error occurred while writing.
   */
  @Test
  public void testCompressDecompressedDeflate() throws Exception {
    assertCompression( TiffCompression.DEFLATE, false);
  }

  /**
   * Tests the compression and decompression using <code>Uncompressed</code>.
   * @throws Exception Throw if an error occurred while writing.
   */
  @Test
  public void testCompressDecompressedUncompressed() throws Exception {
    assertCompression( TiffCompression.UNCOMPRESSED, false);
  }

}
