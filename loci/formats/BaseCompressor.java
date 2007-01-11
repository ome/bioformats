//
// BaseCompressor.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan
and Eric Kjellman.

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

package loci.formats;

import java.io.*;
import java.util.*;

/**
 * BaseCompressor contains default implementation and testing for classes
 * implementing the Compressor interface, and acts as a base class for any
 * of the compression classes.
 * Base 1D compression and decompression methods are not implemented here, and
 * are left as abstract. 2D methods do simple concatenation and call to the 1D
 * methods
 */
public abstract class BaseCompressor implements Compressor {

  /**
   * Compresses a block of data.
   *
   * @param data the data to be compressed
   * @param x length of the x dimension of the image data, if appropriate
   * @param y length of the y dimension of the image data, if appropriate
   * @param dims the dimensions of the image data, if appropriate
   * @param options options to be used during compression, if appropriate
   * @return The compressed data
   * @throws FormatException If input is not an LZW-compressed data block.
   */
  public abstract byte[] compress(byte[] data, int x, int y,
      int[] dims, Object options) throws FormatException;

  /**
   * 2D data block encoding default implementation.
   * This method simply concatenates data[0] + data[1] + ... + data[i] into
   * a 1D block of data, then calls the 1D version of compress.
   *
   * @param data the data to be compressed
   * @param x length of the x dimension of the image data, if appropriate
   * @param y length of the y dimension of the image data, if appropriate
   * @param dims the dimensions of the image data, if appropriate
   * @param options options to be used during compression, if appropriate
   * @return The compressed data
   * @throws FormatException If input is not an LZW-compressed data block.
   */
  public byte[] compress(byte[][] data, int x, int y, int[] dims,
      Object options) throws FormatException
  {
    int len = 0;
    for(int i = 0; i < data.length; i++) {
      len += data[i].length;
    }
    byte[] toCompress = new byte[len];
    int curPos = 0;
    for(int i = 0; i < data.length; i++) {
      System.arraycopy(data[i], 0, toCompress, curPos, data[i].length);
      curPos += data[i].length;
    }
    return compress(toCompress, x, y, dims, options);
  }

  /**
   * Decompresses a block of data.
   *
   * @param data the data to be compressed
   * @return The compressed data
   * @throws FormatException If input is not an LZW-compressed data block.
   */
  public abstract byte[] decompress(byte[] data) throws FormatException;

  /**
   * 2D data block decoding default implementation.
   * This method simply concatenates data[0] + data[1] + ... + data[i] into
   * a 1D block of data, then calls the 1D version of decompress.
   *
   * @param data the data to be decompressed
   * @return The decompressed data
   * @throws FormatException If input is not an compressed data block of the
   *         appropriate type.
   */
  public byte[] decompress(byte[][] data) throws FormatException {
    int len = 0;
    for(int i = 0; i < data.length; i++) {
      len += data[i].length;
    }
    byte[] toDecompress = new byte[len];
    int curPos = 0;
    for(int i = 0; i < data.length; i++) {
      System.arraycopy(data[i], 0, toDecompress, curPos, data[i].length);
      curPos += data[i].length;
    }
    return decompress(toDecompress);
  }

  /**
   * Main testing method default implementation.
   *
   * This method tests whether the data is the same after compressing and
   * decompressing, as well as doing a basic test of the 2D methods.
   *
   * @param args ignored
   * @throws FormatException Can only occur if there is a bug in the
   *                         compress method.
   */
  public void test() throws FormatException {
    byte[] testdata = new byte[50000];
    Random r = new Random();
    System.out.println("Testing " + this.getClass().getName());
    System.out.println("Generating random data");
    r.nextBytes(testdata);
    System.out.println("Compressing data");
    byte[] compressed = compress(testdata, 0, 0, null, null);
    System.out.println("Compressed size: " + compressed.length);
    System.out.println("Decompressing data");
    byte[] decompressed = decompress(compressed);
    System.out.print("Comparing data... ");
    if(testdata.length != decompressed.length) {
      System.out.println("Test data differs in length from uncompressed data");
      System.out.println("Exiting...");
      System.exit(-1);
    }
    else {
      boolean equalsFlag = true;
      for(int i = 0; i < testdata.length; i++) {
        if(testdata[i] != decompressed[i]) {
          System.out.println("Test data and uncompressed data differs at byte" +
                             i);
          equalsFlag = false;
        }
      }
      if(!equalsFlag) {
        System.out.println("Comparison failed. \nExiting...");
        System.exit(-1);
      }
    }
    System.out.println("Success.");
    System.out.println("Generating 2D byte array test");
    byte[][] twoDtest = new byte[100][500];
    for(int i = 0; i < 100; i++) {
      System.arraycopy(testdata, 500*i, twoDtest[i], 0, 500);
    }
    byte[] twoDcompressed = compress(twoDtest, 0, 0, null, null);
    System.out.print("Comparing compressed data... ");
    if(twoDcompressed.length != compressed.length) {
      System.out.println("1D and 2D compressed data not same length");
      System.out.println("Exiting...");
      System.exit(-1);
    }
    boolean equalsFlag = true;
    for(int i = 0; i < twoDcompressed.length; i++) {
      if(twoDcompressed[i] != compressed[i]) {
        System.out.println("1D data and 2D compressed data differs at byte" +
                           i);
        equalsFlag = false;
      }
      if(!equalsFlag) {
        System.out.println("Comparison failed. \nExiting...");
        System.exit(-1);
      }
    }
    System.out.println("Success.");
    System.out.println("Test complete.");
  }
}
