//
// BaseCodec.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.codec;

import java.util.*;
import loci.formats.FormatException;
import loci.formats.LogTools;

/**
 * BaseCodec contains default implementation and testing for classes
 * implementing the Codec interface, and acts as a base class for any
 * of the compression classes.
 * Base 1D compression and decompression methods are not implemented here, and
 * are left as abstract. 2D methods do simple concatenation and call to the 1D
 * methods
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/codec/BaseCodec.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/codec/BaseCodec.java">SVN</a></dd></dl>
 *
 * @author Eric Kjellman egkjellman at wisc.edu
 */
public abstract class BaseCodec implements Codec {

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
    LogTools.println("Testing " + this.getClass().getName());
    LogTools.println("Generating random data");
    r.nextBytes(testdata);
    LogTools.println("Compressing data");
    byte[] compressed = compress(testdata, 0, 0, null, null);
    LogTools.println("Compressed size: " + compressed.length);
    LogTools.println("Decompressing data");
    byte[] decompressed = decompress(compressed);
    LogTools.print("Comparing data... ");
    if (testdata.length != decompressed.length) {
      LogTools.println("Test data differs in length from uncompressed data");
      LogTools.println("Exiting...");
      System.exit(-1);
    }
    else {
      boolean equalsFlag = true;
      for (int i = 0; i < testdata.length; i++) {
        if (testdata[i] != decompressed[i]) {
          LogTools.println("Test data and uncompressed data differs at byte" +
                             i);
          equalsFlag = false;
        }
      }
      if (!equalsFlag) {
        LogTools.println("Comparison failed. \nExiting...");
        System.exit(-1);
      }
    }
    LogTools.println("Success.");
    LogTools.println("Generating 2D byte array test");
    byte[][] twoDtest = new byte[100][500];
    for (int i = 0; i < 100; i++) {
      System.arraycopy(testdata, 500*i, twoDtest[i], 0, 500);
    }
    byte[] twoDcompressed = compress(twoDtest, 0, 0, null, null);
    LogTools.print("Comparing compressed data... ");
    if (twoDcompressed.length != compressed.length) {
      LogTools.println("1D and 2D compressed data not same length");
      LogTools.println("Exiting...");
      System.exit(-1);
    }
    boolean equalsFlag = true;
    for (int i = 0; i < twoDcompressed.length; i++) {
      if (twoDcompressed[i] != compressed[i]) {
        LogTools.println("1D data and 2D compressed data differs at byte" +
                           i);
        equalsFlag = false;
      }
      if (!equalsFlag) {
        LogTools.println("Comparison failed. \nExiting...");
        System.exit(-1);
      }
    }
    LogTools.println("Success.");
    LogTools.println("Test complete.");
  }

  // -- Codec API methods --

  /**
   * 2D data block encoding default implementation.
   * This method simply concatenates data[0] + data[1] + ... + data[i] into
   * a 1D block of data, then calls the 1D version of compress.
   *
   * @param data The data to be compressed.
   * @param x Length of the x dimension of the image data, if appropriate.
   * @param y Length of the y dimension of the image data, if appropriate.
   * @param dims The dimensions of the image data, if appropriate.
   * @param options Options to be used during compression, if appropriate.
   * @return The compressed data.
   * @throws FormatException If input is not a compressed data block of the
   *   appropriate type.
   */
  public byte[] compress(byte[][] data, int x, int y,
    int[] dims, Object options) throws FormatException
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
    return compress(toCompress, x, y, dims, options);
  }

  /* @see Codec#decompress(byte[]) */
  public byte[] decompress(byte[] data) throws FormatException {
    return decompress(data, null);
  }

  /* @see Codec#decompress(byte[][]) */
  public byte[] decompress(byte[][] data) throws FormatException {
    return decompress(data, null);
  }

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
  public byte[] decompress(byte[][] data, Object options)
    throws FormatException
  {
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
