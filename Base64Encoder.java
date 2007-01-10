//
// Base64Encoder.java
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

import java.util.Random;

/**
 * Implements encoding (compress) and decoding (decompress) methods for
 * Base64.
 */
public class Base64Encoder implements Compressor {

  // Base64 alphabet and codes

  private static final int FOURBYTE = 4;
  private static final byte PAD = (byte) '=';

  private static byte[] base64Alphabet = new byte[255];
  private static byte[] lookupBase64Alphabet = new byte[255];

  static {
    for (int i=0; i<255; i++) {
      base64Alphabet[i] = (byte) -1;
    }
    for (int i = 'Z'; i >= 'A'; i--) {
      base64Alphabet[i] = (byte) (i - 'A');
    }
    for (int i = 'z'; i >= 'a'; i--) {
      base64Alphabet[i] = (byte) (i - 'a' + 26);
    }
    for (int i = '9'; i >= '0'; i--) {
      base64Alphabet[i] = (byte) (i - '0' + 52);
    }

    base64Alphabet['+'] = 62;
    base64Alphabet['/'] = 63;

    for (int i=0; i<=25; i++) {
      lookupBase64Alphabet[i] = (byte) ('A' + i);
    }

    for (int i=26, j=0; i<=51; i++, j++) {
      lookupBase64Alphabet[i] = (byte) ('a' + j);
    }

    for (int i=52, j=0; i<=61; i++, j++) {
      lookupBase64Alphabet[i] = (byte) ('0' + j);
    }

    lookupBase64Alphabet[62] = (byte) '+';
    lookupBase64Alphabet[63] = (byte) '/';
  }

  /**
   * Encodes a block of data into Base64 format.
   *
   * @param input the data to be encoded
   * @param dims ignored
   * @param options ignored
   * @return The encoded data
   */
  public byte[] compress(byte[] input, int[] dims, Object options) {
    int dataBits = input.length * 8;
    int fewerThan24 = dataBits % 24;
    int numTriples = dataBits / 24;
    byte[] encoded = null;
    int encodedLength = 0;

    if (fewerThan24 != 0) encodedLength = (numTriples + 1) * 4;
    else encodedLength = numTriples * 4;

    encoded = new byte[encodedLength];

    byte k, l, b1, b2, b3;

    int encodedIndex = 0;
    int dataIndex = 0;

    for (int i=0; i<numTriples; i++) {
      dataIndex = i * 3;
      b1 = input[dataIndex];
      b2 = input[dataIndex + 1];
      b3 = input[dataIndex + 2];

      l = (byte) (b2 & 0x0f);
      k = (byte) (b1 & 0x03);

      byte v1 = ((b1 & -128) == 0) ? (byte) (b1 >> 2) :
        (byte) ((b1) >> 2 ^ 0xc0);
      byte v2 = ((b2 & -128) == 0) ? (byte) (b2 >> 4) :
        (byte) ((b2) >> 4 ^ 0xf0);
      byte v3 = ((b3 & -128) == 0) ? (byte) (b3 >> 6) :
        (byte) ((b3) >> 6 ^ 0xfc);

      encoded[encodedIndex] = lookupBase64Alphabet[v1];
      encoded[encodedIndex + 1] = lookupBase64Alphabet[v2 | (k << 4)];
      encoded[encodedIndex + 2] = lookupBase64Alphabet[(l << 2) | v3];
      encoded[encodedIndex + 3] = lookupBase64Alphabet[b3 & 0x3f];
      encodedIndex += 4;
    }

    dataIndex = numTriples * 3;

    if (fewerThan24 == 8) {
      b1 = input[dataIndex];
      k = (byte) (b1 & 0x03);
      byte v = ((b1 & -128) == 0) ? (byte) (b1 >> 2) :
        (byte) ((b1) >> 2 ^ 0xc0);
      encoded[encodedIndex] = lookupBase64Alphabet[v];
      encoded[encodedIndex + 1] = lookupBase64Alphabet[k << 4];
      encoded[encodedIndex + 2] = (byte) '=';
      encoded[encodedIndex + 3] = (byte) '=';
    }
    else if (fewerThan24 == 16) {
      b1 = input[dataIndex];
      b2 = input[dataIndex + 1];
      l = (byte) (b2 & 0x0f);
      k = (byte) (b1 & 0x03);

      byte v1 = ((b1 & -128) == 0) ? (byte) (b1 >> 2) :
        (byte) ((b1) >> 2 ^ 0xc0);
      byte v2 = ((b2 & -128) == 0) ? (byte) (b2 >> 4) :
        (byte) ((b2) >> 4 ^ 0xf0);

      encoded[encodedIndex] = lookupBase64Alphabet[v1];
      encoded[encodedIndex + 1] = lookupBase64Alphabet[v2 | (k << 4)];
      encoded[encodedIndex + 2] = lookupBase64Alphabet[l << 2];
      encoded[encodedIndex + 3] = (byte) '=';
    }

    return encoded;
  }

  /**
   * Encodes a block of data into Base64 format.
   * This method simply concatenates data[0] + data[1] + ... + data[i] into
   * a 1D block of data, then calls the 1D version of compress.
   *
   * @param data the data to be encoded
   * @param dims ignored
   * @param options ignored
   * @return The compressed data
   */
  public byte[] compress(byte[][] data, int[] dims, Object options) {
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
    return compress(toCompress, dims, options);
  }

  /**
   * Decompresses a block of data.
   *
   * @param base64Data the data to be decoded
   * @return The decoded data
   * @throws FormatException if data is not valid Base64 data
   */
  public byte[] decompress(byte[] base64Data) throws FormatException {
    // TODO: Add checks for invalid data.
    if (base64Data.length == 0) return new byte[0];

    int numberQuadruple = base64Data.length / FOURBYTE;
    byte[] decodedData = null;
    byte b1 = 0, b2 = 0, b3 = 0, b4 = 0, marker0 = 0, marker1 = 0;

    int encodedIndex = 0;
    int dataIndex = 0;

    int lastData = base64Data.length;
    while (base64Data[lastData - 1] == PAD) {
      if (--lastData == 0) {
        return new byte[0];
      }
    }
    decodedData = new byte[lastData - numberQuadruple];

    for (int i=0; i<numberQuadruple; i++) {
      dataIndex = i * 4;
      marker0 = base64Data[dataIndex + 2];
      marker1 = base64Data[dataIndex + 3];

      b1 = base64Alphabet[base64Data[dataIndex]];
      b2 = base64Alphabet[base64Data[dataIndex + 1]];

      if (marker0 != PAD && marker1 != PAD) {
        b3 = base64Alphabet[marker0];
        b4 = base64Alphabet[marker1];

        decodedData[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
        decodedData[encodedIndex + 1] =
          (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
        decodedData[encodedIndex + 2] = (byte) (b3 << 6 | b4);
      }
      else if (marker0 == PAD) {
        decodedData[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
      }
      else if (marker1 == PAD) {
        b3 = base64Alphabet[marker0];

        decodedData[encodedIndex] = (byte) (b1 << 2 | b2 >> 4);
        decodedData[encodedIndex + 1] =
          (byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf));
      }
      encodedIndex += 3;
    }
    return decodedData;
  }

  /**
   * Decodes a Base64 data block.
   * This method simply concatenates data[0] + data[1] + ... + data[i] into
   * a 1D block of data, then calls the 1D version of decompress.
   *
   * @param data the data to be decoded
   * @return The decoded data
   * @throws FormatException If input is not a valid Base64 data block.
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
   * Decodes a Base64 String by converting to bytes and passing to the
   * decompress method.
   *
   * @param s the String to be decoded
   * @return The decoded data
   * @throws FormatException if s is not valid Base64 data.
   */
  public byte[] base64Decode(String s) throws FormatException {
    byte[] base64Data = s.getBytes();
    return decompress(base64Data);
  }
  
    /**
   * Main testing method.
   *
   * @param args ignored
   * @throws FormatException Can only occur if there is a bug in the
   *                         compress method.
   */
  public static void main(String[] args) throws FormatException {
    byte[] testdata = new byte[50000];
    Random r = new Random();
    System.out.println("Generating random data");
    r.nextBytes(testdata);
    Base64Encoder c = new Base64Encoder();
    System.out.println("Compressing data");
    byte[] compressed = c.compress(testdata, null, null);
    System.out.println("Decompressing data");
    byte[] decompressed = c.decompress(compressed);
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
    byte[] twoDcompressed = c.compress(twoDtest, null, null);
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
    System.out.println("Test complete");
  }
  
}
