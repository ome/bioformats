//
// DataTools.java
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

import java.io.*;

/**
 * A utility class with convenience methods for
 * reading, writing and decoding words.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public abstract class DataTools {

  // -- Data reading --

  /** Reads 1 signed byte [-128, 127]. */
  public static byte readSignedByte(DataInput in) throws IOException {
    byte[] b = new byte[1];
    in.readFully(b);
    return b[0];
  }

  /** Reads 1 unsigned byte [0, 255]. */
  public static short readUnsignedByte(DataInput in)
    throws IOException
  {
    short q = readSignedByte(in);
    if (q < 0) q += 256;
    return q;
  }

  /** Reads 2 signed bytes [-32768, 32767]. */
  public static short read2SignedBytes(DataInput in, boolean little)
    throws IOException
  {
    byte[] bytes = new byte[2];
    in.readFully(bytes);
    return bytesToShort(bytes, little);
  }

  /** Reads 2 unsigned bytes [0, 65535]. */
  public static int read2UnsignedBytes(DataInput in, boolean little)
    throws IOException
  {
    int q = read2SignedBytes(in, little);
    if (q < 0) q += 65536;
    return q;
  }

  /** Reads 4 signed bytes [-2147483648, 2147483647]. */
  public static int read4SignedBytes(DataInput in, boolean little)
    throws IOException
  {
    byte[] bytes = new byte[4];
    in.readFully(bytes);
    return bytesToInt(bytes, little);
  }

  /** Reads 4 unsigned bytes [0, 4294967296]. */
  public static long read4UnsignedBytes(DataInput in, boolean little)
    throws IOException
  {
    long q = read4SignedBytes(in, little);
    if (q < 0) q += 4294967296L;
    return q;
  }

  /** Reads 8 signed bytes [-9223372036854775808, 9223372036854775807]. */
  public static long read8SignedBytes(DataInput in, boolean little)
    throws IOException
  {
    byte[] bytes = new byte[8];
    in.readFully(bytes);
    return bytesToLong(bytes, little);
  }

  /** Reads 4 bytes in single precision IEEE format. */
  public static float readFloat(DataInput in, boolean little)
    throws IOException
  {
    return Float.intBitsToFloat(read4SignedBytes(in, little));
  }

  /** Reads 8 bytes in double precision IEEE format. */
  public static double readDouble(DataInput in, boolean little)
    throws IOException
  {
    return Double.longBitsToDouble(read8SignedBytes(in, little));
  }


  // -- Data writing --

  /** Writes a string to the given data output destination. */
  public static void writeString(DataOutput out, String s)
    throws IOException
  {
    byte[] bytes =  s.getBytes("UTF-8");
    out.write(bytes);
  }

  /** Writes an integer to the given data output destination. */
  public static void writeInt(DataOutput out, int v, boolean little)
    throws IOException
  {
    if (little) {
      out.write(v & 0xFF);
      out.write((v >>> 8) & 0xFF);
      out.write((v >>> 16) & 0xFF);
      out.write((v >>> 24) & 0xFF);
    }
    else {
      out.write((v >>> 24) & 0xFF);
      out.write((v >>> 16) & 0xFF);
      out.write((v >>> 8) & 0xFF);
      out.write(v & 0xFF);
    }
  }

  /** Writes a short to the given data output destination. */
  public static void writeShort(DataOutput out, int v, boolean little)
    throws IOException
  {
    if (little) {
      out.write(v & 0xFF);
      out.write((v >>> 8) & 0xFF);
    }
    else {
      out.write((v >>> 8) & 0xFF);
      out.write(v & 0xFF);
    }
  }

  /** Get a pair of bytes representing a short value. */
  public static byte[] shortToBytes(short v, boolean little) throws IOException 
  {
    byte[] rtn = new byte[2];
    if (little) {
      rtn[0] = (byte) (v & 0xff);
      rtn[1] = (byte) ((v >>> 8) & 0xff);
    }        
    else {
      rtn[0] = (byte) ((v >>> 8) & 0xff);         
      rtn[1] = (byte) (v & 0xff);
    }
    return rtn;
  }        

  // -- Word decoding --

  /**
   * Translates up to the first len bytes of a byte array beyond the given
   * offset to a short. If there are fewer than 2 bytes in the array,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static short bytesToShort(byte[] bytes, int off, int len,
    boolean little)
  {
    if (bytes.length - off < len) len = bytes.length - off;
    short total = 0;
    for (int i=0, ndx=off; i<len; i++, ndx++) {
      total |= (bytes[ndx] < 0 ? 256 + bytes[ndx] :
        (int) bytes[ndx]) << ((little ? i : len - i - 1) * 8);
    }
    return total;
  }

  /**
   * Translates up to the first 2 bytes of a byte array beyond the given
   * offset to a short. If there are fewer than 2 bytes in the array,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static short bytesToShort(byte[] bytes, int off, boolean little) {
    return bytesToShort(bytes, off, 2, little);
  }

  /**
   * Translates up to the first 2 bytes of a byte array to a short.
   * If there are fewer than 2 bytes in the array, the MSBs are all
   * assumed to be zero (regardless of endianness).
   */
  public static short bytesToShort(byte[] bytes, boolean little) {
    return bytesToShort(bytes, 0, 2, little);
  }

  /**
   * Translates up to the first len bytes of a byte array byond the given
   * offset to a short. If there are fewer than 2 bytes in the array,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static short bytesToShort(short[] bytes, int off, int len,
    boolean little)
  {
    if (bytes.length - off < len) len = bytes.length - off;
    short total = 0;
    for (int i=0, ndx=off; i<len; i++, ndx++) {
      total |= ((int) bytes[ndx]) << ((little ? i : len - i - 1) * 8);
    }
    return total;
  }

  /**
   * Translates up to the first 2 bytes of a byte array byond the given
   * offset to a short. If there are fewer than 2 bytes in the array,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static short bytesToShort(short[] bytes, int off, boolean little) {
    return bytesToShort(bytes, off, 2, little);
  }

  /**
   * Translates up to the first 2 bytes of a byte array to a short.
   * If there are fewer than 2 bytes in the array, the MSBs are all
   * assumed to be zero (regardless of endianness).
   */
  public static short bytesToShort(short[] bytes, boolean little) {
    return bytesToShort(bytes, 0, 2, little);
  }

  /**
   * Translates up to the first len bytes of a byte array beyond the given
   * offset to an int. If there are fewer than 4 bytes in the array,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static int bytesToInt(byte[] bytes, int off, int len,
    boolean little)
  {
    if (bytes.length - off < len) len = bytes.length - off;
    int total = 0;
    for (int i=0, ndx=off; i<len; i++, ndx++) {
      total |= (bytes[ndx] < 0 ? 256 + bytes[ndx] :
        (int) bytes[ndx]) << ((little ? i : len - i - 1) * 8);
    }
    return total;
  }

  /**
   * Translates up to the first 4 bytes of a byte array beyond the given
   * offset to an int. If there are fewer than 4 bytes in the array,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static int bytesToInt(byte[] bytes, int off, boolean little) {
    return bytesToInt(bytes, off, 4, little);
  }

  /**
   * Translates up to the first 4 bytes of a byte array to an int.
   * If there are fewer than 4 bytes in the array, the MSBs are all
   * assumed to be zero (regardless of endianness).
   */
  public static int bytesToInt(byte[] bytes, boolean little) {
    return bytesToInt(bytes, 0, 4, little);
  }

  /**
   * Translates up to the first len bytes of a byte array beyond the given
   * offset to an int. If there are fewer than 4 bytes in the array,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static int bytesToInt(short[] bytes, int off, int len,
    boolean little)
  {
    if (bytes.length - off < len) len = bytes.length - off;
    int total = 0;
    for (int i=0, ndx=off; i<len; i++, ndx++) {
      total |= ((int) bytes[ndx]) << ((little ? i : len - i - 1) * 8);
    }
    return total;
  }

  /**
   * Translates up to the first 4 bytes of a byte array beyond the given
   * offset to an int. If there are fewer than 4 bytes in the array,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static int bytesToInt(short[] bytes, int off, boolean little) {
    return bytesToInt(bytes, off, 4, little);
  }

  /**
   * Translates up to the first 4 bytes of a byte array to an int.
   * If there are fewer than 4 bytes in the array, the MSBs are all
   * assumed to be zero (regardless of endianness).
   */
  public static int bytesToInt(short[] bytes, boolean little) {
    return bytesToInt(bytes, 0, 4, little);
  }

  /**
   * Translates up to the first len bytes of a byte array beyond the given
   * offset to a long. If there are fewer than 8 bytes in the array,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static long bytesToLong(byte[] bytes, int off, int len,
    boolean little)
  {
    if (bytes.length - off < len) len = bytes.length - off;
    long total = 0;
    for (int i=0; i<len; i++) {
      total |= (bytes[i] < 0 ? 256L + bytes[i] :
        (long) bytes[i]) << ((little ? i : len - i - 1) * 8);
    }
    return total;
  }

  /**
   * Translates up to the first 8 bytes of a byte array beyond the given
   * offset to a long. If there are fewer than 8 bytes in the array,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static long bytesToLong(byte[] bytes, int off, boolean little) {
    return bytesToLong(bytes, off, 8, little);
  }

  /**
   * Translates up to the first 8 bytes of a byte array to a long.
   * If there are fewer than 8 bytes in the array, the MSBs are all
   * assumed to be zero (regardless of endianness).
   */
  public static long bytesToLong(byte[] bytes, boolean little) {
    return bytesToLong(bytes, 0, 8, little);
  }

  /**
   * Translates up to the first len bytes of a byte array beyond the given
   * offset to a long. If there are fewer than 8 bytes to be translated,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static long bytesToLong(short[] bytes, int off, int len,
    boolean little)
  {
    if (bytes.length - off < len) len = bytes.length - off;
    long total = 0;
    for (int i=0, ndx=off; i<len; i++, ndx++) {
      total |= ((long) bytes[ndx]) << ((little ? i : len - i - 1) * 8);
    }
    return total;
  }

  /**
   * Translates up to the first 8 bytes of a byte array beyond the given
   * offset to a long. If there are fewer than 8 bytes to be translated,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static long bytesToLong(short[] bytes, int off, boolean little) {
    return bytesToLong(bytes, off, 8, little);
  }

  /**
   * Translates up to the first 8 bytes of a byte array to a long.
   * If there are fewer than 8 bytes in the array, the MSBs are all
   * assumed to be zero (regardless of endianness).
   */
  public static long bytesToLong(short[] bytes, boolean little) {
    return bytesToLong(bytes, 0, 8, little);
  }

  /** Translates bytes from the given array into a string. */
  public static String bytesToString(short[] bytes, int off, int len) {
    if (bytes.length - off < len) len = bytes.length - off;
    for (int i=0; i<len; i++) {
      if (bytes[off + i] == 0) {
        len = i;
        break;
      }
    }
    byte[] b = new byte[len];
    for (int i=0; i<b.length; i++) b[i] = (byte) bytes[off + i];
    return new String(b);
  }

  /** Translates bytes from the given array into a string. */
  public static String bytesToString(short[] bytes, int off) {
    return bytesToString(bytes, off, bytes.length - off);
  }

  /** Translates bytes from the given array into a string. */
  public static String bytesToString(short[] bytes) {
    return bytesToString(bytes, 0, bytes.length);
  }

  /** Remove null bytes from a string. */
  public static String stripString(String toStrip) {
    char[] toRtn = new char[toStrip.length()];
    int counter = 0;
    for (int i=0; i<toRtn.length; i++) {
      if ((toStrip.charAt(i) != 0) && (toStrip.charAt(i) != ' ')) {
        toRtn[counter] = toStrip.charAt(i);
        counter++;
      }
    }
    toStrip = new String(toRtn);
    toStrip = toStrip.trim();
    return toStrip;
  }

  /** Check if two filenames have the same prefix. */
  public static boolean samePrefix(String s1, String s2) {
    if (s1 == null || s2 == null) return false;
    int n1 = s1.indexOf(".");
    int n2 = s2.indexOf(".");
    if ((n1 == -1) || (n2 == -1)) return false;

    int slash1 = s1.lastIndexOf(File.pathSeparator);
    int slash2 = s2.lastIndexOf(File.pathSeparator);
    
    String sub1 = s1.substring((slash1 == -1) ? 0 : slash1 + 1, n1);
    String sub2 = s2.substring((slash2 == -1) ? 0 : slash2 + 1, n2);
    return sub1.equals(sub2) || sub1.startsWith(sub2) || sub2.startsWith(sub1);
  }
  
}
