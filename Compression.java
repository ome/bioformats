//
// Compression.java
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
public final class Compression {

  // -- Constants --

  // LZW compression codes
  protected static final int CLEAR_CODE = 256;
  protected static final int EOI_CODE = 257;

  // LZO compression codes
  private static final int LZO_OVERRUN = -6;

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

  // -- Constructor --

  private Compression() { }

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

  /**
   * Encodes a byte array using Base64 encoding.
   * Much of this code was adapted from the Apache Commons Codec source.
   */
  public static byte[] base64Encode(byte[] input) {
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

  // -- Decompression methods --

  /**
   * Decodes an LZO-compressed array.
   * Adapted from LZO for Java, available at
   * http://www.oberhumer.com/opensource/lzo/
   */
  public static void lzoUncompress(byte[] src, int size, byte[] dst)
    throws FormatException
  {
    int ip = 0;
    int op = 0;
    int t = src[ip++] & 0xff;
    int mPos;

    if (t > 17) {
      t -= 17;
      do dst[op++] = src[ip++]; while (--t > 0);
      t = src[ip++] & 0xff;
      if (t < 16) return;
    }

  loop:
    for (;; t = src[ip++] & 0xff) {
      if (t < 16) {
        if (t == 0) {
          while (src[ip] == 0) {
            t += 255;
            ip++;
          }
          t += 15 + (src[ip++] & 0xff);
        }
        t += 3;
        do dst[op++] = src[ip++]; while (--t > 0);
        t = src[ip++] & 0xff;
        if (t < 16) {
          mPos = op - 0x801 - (t >> 2) - ((src[ip++] & 0xff) << 2);
          if (mPos < 0) {
            t = LZO_OVERRUN;
            break loop;
          }
          t = 3;
          do dst[op++] = dst[mPos++]; while (--t > 0);
          t = src[ip - 2] & 3;
          if (t == 0) continue;
          do dst[op++] = src[ip++]; while (--t > 0);
          t = src[ip++] & 0xff;
        }
      }
      for (;; t = src[ip++] & 0xff) {
        if (t >= 64) {
          mPos = op - 1 - ((t >> 2) & 7) - ((src[ip++] & 0xff) << 3);
          t = (t >> 5) - 1;
        }
        else if (t >= 32) {
          t &= 31;
          if (t == 0) {
            while (src[ip] == 0) {
              t += 255;
              ip++;
            }
            t += 31 + (src[ip++] & 0xff);
          }
          mPos = op - 1 - ((src[ip++] & 0xff) >> 2);
          mPos -= ((src[ip++] & 0xff) << 6);
        }
        else if (t >= 16) {
          mPos = op - ((t & 8) << 11);
          t &= 7;
          if (t == 0) {
            while (src[ip] == 0) {
              t += 255;
              ip++;
            }
            t += 7 + (src[ip++] & 0xff);
          }
          mPos -= ((src[ip++] & 0xff) >> 2);
          mPos -= ((src[ip++] & 0xff) << 6);
          if (mPos == op) break loop;
          mPos -= 0x4000;
        }
        else {
          mPos = op - 1 - (t >> 2) - ((src[ip++] & 0xff) << 2);
          t = 0;
        }

        if (mPos < 0) {
          t = LZO_OVERRUN;
          break loop;
        }

        t += 2;
        do dst[op++] = dst[mPos++]; while (--t > 0);
        t = src[ip - 2] & 3;
        if (t == 0) break;
        do dst[op++] = src[ip++]; while (--t > 0);
      }
    }
  }

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

  /**
   * Decodes a Base64 encoded String.
   * Much of this code was adapted from the Apache Commons Codec source.
   */
  public static byte[] decode(String s) throws FormatException {
    byte[] base64Data = s.getBytes();

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
}
