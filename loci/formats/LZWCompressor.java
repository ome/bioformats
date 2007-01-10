//
// LZWCompressor.java
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
 * Implements basic LZW compression and decompression, as outlined in the
 * TIFF 6.0 Specification at
 * http://partners.adobe.com/asn/developer/pdfs/tn/TIFF6.pdf (page 61).
 *
 */
public class LZWCompressor implements Compressor {

  // LZW compression codes
  protected static final int CLEAR_CODE = 256;
  protected static final int EOI_CODE = 257;

  /**
   * Compresses a block of data using LZW compression. If input is null or of
   * 0 length, simply returns input.
   *
   * @param input the data to be compressed
   * @param dims ignored for LZW.
   * @param options ignored for LZW.
   * @return The compressed data
   */
  public byte[] compress(byte[] input, int[] dims, Object options) {

    if (input == null || input.length == 0) return input;

    // initialize symbol table
    LZWTreeNode symbols = new LZWTreeNode(-1);
    symbols.initialize();
    int nextCode = 258;
    int numBits = 9;

    BitWriter out = new BitWriter();
    out.write(CLEAR_CODE, numBits);
    ByteVector omega = new ByteVector();
    for (int i=0; i<input.length; i++) {
      byte k = input[i];
      LZWTreeNode omegaNode = symbols.nodeFromString(omega);
      LZWTreeNode omegaKNode = omegaNode.getChild(k);
      if (omegaKNode != null) {
        // omega+k is in the symbol table
        omega.add(k);
      }
      else {
        out.write(omegaNode.getCode(), numBits);
        omega.add(k);
        symbols.addTableEntry(omega, nextCode++);
        omega.clear();
        omega.add(k);
        if (nextCode == 512) numBits = 10;
        else if (nextCode == 1024) numBits = 11;
        else if (nextCode == 2048) numBits = 12;
        else if (nextCode == 4096) {
          out.write(CLEAR_CODE, numBits);
          symbols.initialize();
          nextCode = 258;
          numBits = 9;
        }
      }
    }
    out.write(symbols.codeFromString(omega), numBits);
    out.write(EOI_CODE, numBits);

    return out.toByteArray();
  }

  /**
   * Compresses a block of data using LZW compression.
   * This method simply concatenates data[0] + data[1] + ... + data[i] into
   * a 1D block of data, then calls the 1D version of compress.
   *
   * @param data the data to be compressed
   * @param dims ignored for LZW.
   * @param options ignored for LZW.
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
   * Decodes an LZW-compressed data block.
   * This method simply concatenates data[0] + data[1] + ... + data[i] into
   * a 1D block of data, then calls the 1D version of decompress.
   *
   * @param data the data to be decompressed
   * @return The decompressed data
   * @throws FormatException If input is not an LZW-compressed data block.
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
   * Decodes an LZW-compressed data block.
   *
   * @param input the data to be decompressed
   * @return The decompressed data
   * @throws FormatException If input is not an LZW-compressed data block.
   */
  public byte[] decompress(byte[] input) throws FormatException {
    // Written by Eric Kjellman egkjellman at wisc.edu
    // Modified by Wayne Rasband wsr at nih.gov
    if (input == null || input.length == 0) return input;

    byte[][] symbolTable = new byte[4096][1];
    int bitsToRead = 9;
    int nextSymbol = 258;
    int code;
    int oldCode = -1;
    ByteVector out = new ByteVector(8192);
    BitBuffer bb = new BitBuffer(input);
    byte[] byteBuffer1 = new byte[16];
    byte[] byteBuffer2 = new byte[16];

    while (true) {
      code = bb.getBits(bitsToRead);
      if (code == EOI_CODE || code == -1) break;
      if (code == CLEAR_CODE) {
        // initialize symbol table
        for (int i = 0; i < 256; i++) symbolTable[i][0] = (byte) i;
        nextSymbol = 258;
        bitsToRead = 9;
        code = bb.getBits(bitsToRead);
        if (code == EOI_CODE || code == -1) break;
        out.add(symbolTable[code]);
        oldCode = code;
      }
      else {
        if (code < nextSymbol) {
          // code is in table
          out.add(symbolTable[code]);
          // add string to table
          ByteVector symbol = new ByteVector(byteBuffer1);
          try {
            symbol.add(symbolTable[oldCode]);
          }
          catch (ArrayIndexOutOfBoundsException a) {
            throw new FormatException("Sorry, old LZW codes not supported");
          }
          symbol.add(symbolTable[code][0]);
          symbolTable[nextSymbol] = symbol.toByteArray(); //**
          oldCode = code;
          nextSymbol++;
        }
        else {
          // out of table
          ByteVector symbol = new ByteVector(byteBuffer2);
          symbol.add(symbolTable[oldCode]);
          symbol.add(symbolTable[oldCode][0]);
          byte[] outString = symbol.toByteArray();
          out.add(outString);
          symbolTable[nextSymbol] = outString; //**
          oldCode = code;
          nextSymbol++;
        }
        if (nextSymbol == 511) bitsToRead = 10;
        if (nextSymbol == 1023) bitsToRead = 11;
        if (nextSymbol == 2047) bitsToRead = 12;
      }
    }
    return out.toByteArray();
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
    LZWCompressor c = new LZWCompressor();
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
