//
// Compression.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

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

import java.io.PipedInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * A utility class for handling various compression types.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Eric Kjellman egkjellman at wisc.edu
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public abstract class Compression {

  // -- Constants --

  // LZW compression codes
  protected static final int CLEAR_CODE = 256;
  protected static final int EOI_CODE = 257;

  // ThunderScan compression codes
  protected static final int DATA = 0x3f;
  protected static final int CODE = 0xc0;
  protected static final int RUN = 0x00;
  protected static final int TWO_BIT_DELTAS = 0x40;
  protected static final int SKIP_TWO = 2;
  protected static final int THREE_BIT_DELTAS = 0x80;
  protected static final int SKIP_THREE = 4;
  protected static final int RAW = 0xc0;


  // -- Compression methods --

  /**
   * Encodes an image strip using the LZW compression method.
   * Adapted from the TIFF 6.0 Specification:
   * http://partners.adobe.com/asn/developer/pdfs/tn/TIFF6.pdf (page 61)
   */
  public static byte[] lzwCompress(byte[] input) {
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


  // -- Decompression methods --

  /** Decodes an Adobe Deflate (Zip) compressed image strip. */
  public static byte[] deflateUncompress(byte[] input) throws FormatException {
    try {
      Inflater inf = new Inflater(false);
      inf.setInput(input);
      InflaterInputStream i =
        new InflaterInputStream(new PipedInputStream(), inf);
      ByteVector bytes = new ByteVector();
      byte[] buf = new byte[8192];
      while (true) {
        int r = i.read(buf, 0, buf.length);
        if (r == -1) break; // eof
        bytes.add(buf, 0, r);
      }
      return bytes.toByteArray();
    }
    catch (Exception e) {
      throw new FormatException("Error uncompressing " +
        "Adobe Deflate (ZLIB) compressed image strip.", e);
    }
  }

  /**
   * Decodes a PackBits (Macintosh RLE) compressed image.
   * Adapted from the TIFF 6.0 specification, page 42.
   * @author Melissa Linkert linkert at cs.wisc.edu
   */
  public static byte[] packBitsUncompress(byte[] input) {
    ByteVector output = new ByteVector(input.length);
    int pt = 0;
    while (pt < input.length) {
      byte n = input[pt++];
      if (n >= 0) { // 0 <= n <= 127
        int len = pt + n + 1 > input.length ? (input.length - pt) : (n + 1);
        output.add(input, pt, len);
        pt += len;
      }
      else if (n != -128) { // -127 <= n <= -1
        if (pt >= input.length) break;
        int len = -n + 1;
        byte inp = input[pt++];
        for (int i=0; i<len; i++) output.add(inp);
      }
    }
    return output.toByteArray();
  }

  /**
   * Decodes an LZW-compressed image strip.
   * Adapted from the TIFF 6.0 Specification:
   * http://partners.adobe.com/asn/developer/pdfs/tn/TIFF6.pdf (page 61)
   * @author Eric Kjellman egkjellman at wisc.edu
   * @author Wayne Rasband wsr at nih.gov
   */
  public static byte[] lzwUncompress(byte[] input) throws FormatException {
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

}
