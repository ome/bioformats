//
// CompressDecompressTest
//

/*
LOCI software automated test suite for TestNG. Copyright (C) 2007-@year@
Jean-Marie Burel. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
  * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of the UW-Madison LOCI nor the names of its
    contributors may be used to endorse or promote products derived from
    this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE UW-MADISON LOCI ``AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/test-suite/src/loci/tests/testng/CompressDecompressTest.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/test-suite/src/loci/tests/testng/CompressDecompressTest.java;hb=HEAD">Gitweb</a></dd></dl>
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
