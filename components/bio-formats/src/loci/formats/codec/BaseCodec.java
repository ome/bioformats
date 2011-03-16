//
// BaseCodec.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.codec;

import java.io.IOException;
import java.util.Random;

import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BaseCodec contains default implementation and testing for classes
 * implementing the Codec interface, and acts as a base class for any
 * of the compression classes.
 * Base 1D compression and decompression methods are not implemented here, and
 * are left as abstract. 2D methods do simple concatenation and call to the 1D
 * methods
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/codec/BaseCodec.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/codec/BaseCodec.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Eric Kjellman egkjellman at wisc.edu
 */
public abstract class BaseCodec implements Codec {

  // -- Constants --

  protected static final Logger LOGGER =
    LoggerFactory.getLogger(BaseCodec.class);

  // -- BaseCodec API methods --

  /**
   * Main testing method default implementation.
   *
   * This method tests whether the data is the same after compressing and
   * decompressing, as well as doing a basic test of the 2D methods.
   *
   * @throws FormatException Can only occur if there is a bug in the
   *   compress method.
   */
  public void test() throws FormatException {
    byte[] testdata = new byte[50000];
    Random r = new Random();
    LOGGER.info("Testing {}", this.getClass().getName());
    LOGGER.info("Generating random data");
    r.nextBytes(testdata);
    LOGGER.info("Compressing data");
    byte[] compressed = compress(testdata, null);
    LOGGER.info("Compressed size: {}", compressed.length);
    LOGGER.info("Decompressing data");
    byte[] decompressed = decompress(compressed);
    LOGGER.info("Comparing data...");
    if (testdata.length != decompressed.length) {
      LOGGER.info("Test data differs in length from uncompressed data");
      LOGGER.info("Exiting...");
      System.exit(-1);
    }
    else {
      boolean equalsFlag = true;
      for (int i = 0; i < testdata.length; i++) {
        if (testdata[i] != decompressed[i]) {
          LOGGER.info("Test data and uncompressed data differ at byte {}", i);
          equalsFlag = false;
        }
      }
      if (!equalsFlag) {
        LOGGER.info("Comparison failed. Exiting...");
        System.exit(-1);
      }
    }
    LOGGER.info("Success.");
    LOGGER.info("Generating 2D byte array test");
    byte[][] twoDtest = new byte[100][500];
    for (int i = 0; i < 100; i++) {
      System.arraycopy(testdata, 500*i, twoDtest[i], 0, 500);
    }
    byte[] twoDcompressed = compress(twoDtest, null);
    LOGGER.info("Comparing compressed data...");
    if (twoDcompressed.length != compressed.length) {
      LOGGER.info("1D and 2D compressed data not same length");
      LOGGER.info("Exiting...");
      System.exit(-1);
    }
    boolean equalsFlag = true;
    for (int i = 0; i < twoDcompressed.length; i++) {
      if (twoDcompressed[i] != compressed[i]) {
        LOGGER.info("1D data and 2D compressed data differs at byte {}", i);
        equalsFlag = false;
      }
      if (!equalsFlag) {
        LOGGER.info("Comparison failed. Exiting...");
        System.exit(-1);
      }
    }
    LOGGER.info("Success.");
    LOGGER.info("Test complete.");
  }

  // -- Codec API methods --

  /**
   * 2D data block encoding default implementation.
   * This method simply concatenates data[0] + data[1] + ... + data[i] into
   * a 1D block of data, then calls the 1D version of compress.
   *
   * @param data The data to be compressed.
   * @param options Options to be used during compression, if appropriate.
   * @return The compressed data.
   * @throws FormatException If input is not a compressed data block of the
   *   appropriate type.
   */
  public byte[] compress(byte[][] data, CodecOptions options)
    throws FormatException
  {
    int len = 0;
    for (int i = 0; i < data.length; i++) {
      len += data[i].length;
    }
    byte[] toCompress = new byte[len];
    int curPos = 0;
    for (int i = 0; i < data.length; i++) {
      System.arraycopy(data[i], 0, toCompress, curPos, data[i].length);
      curPos += data[i].length;
    }
    return compress(toCompress, options);
  }

  /* @see Codec#decompress(byte[]) */
  public byte[] decompress(byte[] data) throws FormatException {
    return decompress(data, null);
  }

  /* @see Codec#decompress(byte[][]) */
  public byte[] decompress(byte[][] data) throws FormatException {
    return decompress(data, null);
  }

  /* @see Codec#decompress(byte[], CodecOptions) */
  public byte[] decompress(byte[] data, CodecOptions options)
    throws FormatException
  {
    try {
      RandomAccessInputStream r = new RandomAccessInputStream(data);
      byte[] t = decompress(r, options);
      r.close();
      return t;
    }
    catch (IOException e) {
      throw new FormatException(e);
    }
  }

  /* @see Codec#decompress(RandomAccessInputStream, CodecOptions) */
  public abstract byte[] decompress(RandomAccessInputStream in,
    CodecOptions options) throws FormatException, IOException;

  /**
   * 2D data block decoding default implementation.
   * This method simply concatenates data[0] + data[1] + ... + data[i] into
   * a 1D block of data, then calls the 1D version of decompress.
   *
   * @param data The data to be decompressed.
   * @return The decompressed data.
   * @throws FormatException If input is not a compressed data block of the
   *   appropriate type.
   */
  public byte[] decompress(byte[][] data, CodecOptions options)
    throws FormatException
  {
    if (data == null)
      throw new IllegalArgumentException("No data to decompress.");
    int len = 0;
    for (int i = 0; i < data.length; i++) {
      len += data[i].length;
    }
    byte[] toDecompress = new byte[len];
    int curPos = 0;
    for (int i = 0; i < data.length; i++) {
      System.arraycopy(data[i], 0, toDecompress, curPos, data[i].length);
      curPos += data[i].length;
    }
    return decompress(toDecompress, options);
  }

}
