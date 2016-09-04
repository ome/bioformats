/*
 * #%L
 * Common package for I/O and related utilities
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.common;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * A utility class with convenience methods for
 * reading, writing and decoding words.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Chris Allan callan at blackcat.ca
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public final class DataTools {

  // -- Constants --

  // -- Static fields --
  private static final Logger LOGGER = LoggerFactory.getLogger(DataTools.class);

  private static final ThreadLocal<NumberFormat> nf =
      new ThreadLocal<NumberFormat>() {
        @Override protected NumberFormat initialValue() {
            return DecimalFormat.getInstance(Locale.ENGLISH);
        }
    };
  // -- Constructor --

  private DataTools() { }

  /** Reads the contents of the given file into a string. */
  public static String readFile(String id) throws IOException {
    RandomAccessInputStream in = new RandomAccessInputStream(id);
    long idLen = in.length();
    if (idLen > Integer.MAX_VALUE) {
      throw new IOException("File too large");
    }
    int len = (int) idLen;
    String data = in.readString(len);
    in.close();
    return data;
  }

  // -- Word decoding - bytes to primitive types --

  /**
   * Translates up to the first len bytes of a byte array beyond the given
   * offset to a short. If there are fewer than len bytes available,
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
   * offset to a short. If there are fewer than 2 bytes available
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static short bytesToShort(byte[] bytes, int off, boolean little) {
    return bytesToShort(bytes, off, 2, little);
  }

  /**
   * Translates up to the first 2 bytes of a byte array to a short.
   * If there are fewer than 2 bytes available, the MSBs are all
   * assumed to be zero (regardless of endianness).
   */
  public static short bytesToShort(byte[] bytes, boolean little) {
    return bytesToShort(bytes, 0, 2, little);
  }

  /**
   * Translates up to the first len bytes of a byte array byond the given
   * offset to a short. If there are fewer than len bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static short bytesToShort(short[] bytes, int off, int len,
    boolean little)
  {
    if (bytes.length - off < len) len = bytes.length - off;
    short total = 0;
    for (int i=0, ndx=off; i<len; i++, ndx++) {
      total |= bytes[ndx] << ((little ? i : len - i - 1) * 8);
    }
    return total;
  }

  /**
   * Translates up to the first 2 bytes of a byte array byond the given
   * offset to a short. If there are fewer than 2 bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static short bytesToShort(short[] bytes, int off, boolean little) {
    return bytesToShort(bytes, off, 2, little);
  }

  /**
   * Translates up to the first 2 bytes of a byte array to a short.
   * If there are fewer than 2 bytes available, the MSBs are all
   * assumed to be zero (regardless of endianness).
   */
  public static short bytesToShort(short[] bytes, boolean little) {
    return bytesToShort(bytes, 0, 2, little);
  }

  /**
   * Translates up to the first len bytes of a byte array beyond the given
   * offset to an int. If there are fewer than len bytes available,
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
   * offset to an int. If there are fewer than 4 bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static int bytesToInt(byte[] bytes, int off, boolean little) {
    return bytesToInt(bytes, off, 4, little);
  }

  /**
   * Translates up to the first 4 bytes of a byte array to an int.
   * If there are fewer than 4 bytes available, the MSBs are all
   * assumed to be zero (regardless of endianness).
   */
  public static int bytesToInt(byte[] bytes, boolean little) {
    return bytesToInt(bytes, 0, 4, little);
  }

  /**
   * Translates up to the first len bytes of a byte array beyond the given
   * offset to an int. If there are fewer than len bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static int bytesToInt(short[] bytes, int off, int len,
    boolean little)
  {
    if (bytes.length - off < len) len = bytes.length - off;
    int total = 0;
    for (int i=0, ndx=off; i<len; i++, ndx++) {
      total |= bytes[ndx] << ((little ? i : len - i - 1) * 8);
    }
    return total;
  }

  /**
   * Translates up to the first 4 bytes of a byte array beyond the given
   * offset to an int. If there are fewer than 4 bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static int bytesToInt(short[] bytes, int off, boolean little) {
    return bytesToInt(bytes, off, 4, little);
  }

  /**
   * Translates up to the first 4 bytes of a byte array to an int.
   * If there are fewer than 4 bytes available, the MSBs are all
   * assumed to be zero (regardless of endianness).
   */
  public static int bytesToInt(short[] bytes, boolean little) {
    return bytesToInt(bytes, 0, 4, little);
  }

  /**
   * Translates up to the first len bytes of a byte array beyond the given
   * offset to a float. If there are fewer than len bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static float bytesToFloat(byte[] bytes, int off, int len,
    boolean little)
  {
    return Float.intBitsToFloat(bytesToInt(bytes, off, len, little));
  }

  /**
   * Translates up to the first 4 bytes of a byte array beyond a given
   * offset to a float. If there are fewer than 4 bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static float bytesToFloat(byte[] bytes, int off, boolean little) {
    return bytesToFloat(bytes, off, 4, little);
  }

  /**
   * Translates up to the first 4 bytes of a byte array to a float.
   * If there are fewer than 4 bytes available, the MSBs are all
   * assumed to be zero (regardless of endianness).
   */
  public static float bytesToFloat(byte[] bytes, boolean little) {
    return bytesToFloat(bytes, 0, 4, little);
  }

  /**
   * Translates up to the first len bytes of a byte array beyond a given
   * offset to a float. If there are fewer than len bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static float bytesToFloat(short[] bytes, int off, int len,
    boolean little)
  {
    return Float.intBitsToFloat(bytesToInt(bytes, off, len, little));
  }

  /**
   * Translates up to the first 4 bytes of a byte array beyond a given
   * offset to a float. If there are fewer than 4 bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static float bytesToFloat(short[] bytes, int off, boolean little) {
    return bytesToFloat(bytes, off, 4, little);
  }

  /**
   * Translates up to the first 4 bytes of a byte array to a float.
   * If there are fewer than 4 bytes available, the MSBs are all
   * assumed to be zero (regardless of endianness).
   */
  public static float bytesToFloat(short[] bytes, boolean little) {
    return bytesToFloat(bytes, 0, 4, little);
  }

  /**
   * Translates up to the first len bytes of a byte array beyond the given
   * offset to a long. If there are fewer than len bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static long bytesToLong(byte[] bytes, int off, int len,
    boolean little)
  {
    if (bytes.length - off < len) len = bytes.length - off;
    long total = 0;
    for (int i=0, ndx=off; i<len; i++, ndx++) {
      total |= (bytes[ndx] < 0 ? 256L + bytes[ndx] :
        (long) bytes[ndx]) << ((little ? i : len - i - 1) * 8);
    }
    return total;
  }

  /**
   * Translates up to the first 8 bytes of a byte array beyond the given
   * offset to a long. If there are fewer than 8 bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static long bytesToLong(byte[] bytes, int off, boolean little) {
    return bytesToLong(bytes, off, 8, little);
  }

  /**
   * Translates up to the first 8 bytes of a byte array to a long.
   * If there are fewer than 8 bytes available, the MSBs are all
   * assumed to be zero (regardless of endianness).
   */
  public static long bytesToLong(byte[] bytes, boolean little) {
    return bytesToLong(bytes, 0, 8, little);
  }

  /**
   * Translates up to the first len bytes of a byte array beyond the given
   * offset to a long. If there are fewer than len bytes available,
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
   * offset to a long. If there are fewer than 8 bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static long bytesToLong(short[] bytes, int off, boolean little) {
    return bytesToLong(bytes, off, 8, little);
  }

  /**
   * Translates up to the first 8 bytes of a byte array to a long.
   * If there are fewer than 8 bytes available, the MSBs are all
   * assumed to be zero (regardless of endianness).
   */
  public static long bytesToLong(short[] bytes, boolean little) {
    return bytesToLong(bytes, 0, 8, little);
  }

  /**
   * Translates up to the first len bytes of a byte array beyond the given
   * offset to a double. If there are fewer than len bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static double bytesToDouble(byte[] bytes, int off, int len,
    boolean little)
  {
    return Double.longBitsToDouble(bytesToLong(bytes, off, len, little));
  }

  /**
   * Translates up to the first 8 bytes of a byte array beyond the given
   * offset to a double. If there are fewer than 8 bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static double bytesToDouble(byte[] bytes, int off,
    boolean little)
  {
    return bytesToDouble(bytes, off, 8, little);
  }

  /**
   * Translates up to the first 8 bytes of a byte array to a double.
   * If there are fewer than 8 bytes available, the MSBs are all
   * assumed to be zero (regardless of endianness).
   */
  public static double bytesToDouble(byte[] bytes, boolean little) {
    return bytesToDouble(bytes, 0, 8, little);
  }

  /**
   * Translates up to the first len bytes of a byte array beyond the given
   * offset to a double. If there are fewer than len bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static double bytesToDouble(short[] bytes, int off, int len,
    boolean little)
  {
    return Double.longBitsToDouble(bytesToLong(bytes, off, len, little));
  }

  /**
   * Translates up to the first 8 bytes of a byte array beyond the given
   * offset to a double. If there are fewer than 8 bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static double bytesToDouble(short[] bytes, int off,
    boolean little)
  {
    return bytesToDouble(bytes, off, 8, little);
  }

  /**
   * Translates up to the first 8 bytes of a byte array to a double.
   * If there are fewer than 8 bytes available, the MSBs are all
   * assumed to be zero (regardless of endianness).
   */
  public static double bytesToDouble(short[] bytes, boolean little) {
    return bytesToDouble(bytes, 0, 8, little);
  }

  /** Translates the given byte array into a String of hexadecimal digits. */
  public static String bytesToHex(byte[] b) {
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<b.length; i++) {
      String a = Integer.toHexString(b[i] & 0xff);
      if (a.length() == 1) sb.append("0");
      sb.append(a);
    }
    return sb.toString();
  }

  /**
   * Parses the input string into a short
   * @return {@code null} if the string could not be parsed
   */
  public static Short parseShort(String value) {
    try {
      return Short.valueOf(value);
    } catch (NumberFormatException e) {
      LOGGER.debug("Could not parse integer value", e);
    }
    return null;
  }

  /**
   * Parses the input string into a byte
   * @return {@code null} if the string could not be parsed
   */
  public static Byte parseByte(String value) {
    try {
      return Byte.valueOf(value);
    } catch (NumberFormatException e) {
      LOGGER.debug("Could not parse integer value", e);
    }
    return null;
  }

  /**
   * Parses the input string into an integer
   * @return {@code null} if the string could not be parsed
   */
  public static Integer parseInteger(String value) {
    try {
      return Integer.valueOf(value);
    } catch (NumberFormatException e) {
      LOGGER.debug("Could not parse integer value", e);
    }
    return null;
  }

  /**
   * Parses the input string into a long
   * @return {@code null} if the string could not be parsed
   */
  public static Long parseLong(String value) {
    try {
      return Long.valueOf(value);
    } catch (NumberFormatException e) {
      LOGGER.debug("Could not parse integer value", e);
    }
    return null;
  }

  /**
   * Parses the input string into a float accounting for the locale decimal
   * separator
   * @return {@code null} if the string could not be parsed
   */
  public static Float parseFloat(String value) {
    if (value == null) return null;
    try {
      return nf.get().parse(value.replaceAll(",", ".")).floatValue();
    } catch (ParseException e) {
      LOGGER.debug("Could not parse float value", e);
    }
    return null;
  }

  /**
   * Parses the input string into a double accounting for the locale decimal
   * separator
   * @return {@code null} if the string could not be parsed
   */
  public static Double parseDouble(String value) {
    if (value == null) return null;
    try {
      return nf.get().parse(value.replaceAll(",", ".")).doubleValue();
    } catch (ParseException e) {
      LOGGER.debug("Could not parse double value", e);
    }
    return null;
  }

  /**
   * Normalizes the decimal separator for the user's locale.
   * @deprecated Use {@link #parseDouble(String)} instead
   */
  @Deprecated
  public static String sanitizeDouble(String value) {
    value = value.replaceAll("[^0-9,\\.]", "");
    char separator = new DecimalFormatSymbols().getDecimalSeparator();
    char usedSeparator = separator == '.' ? ',' : '.';
    value = value.replace(usedSeparator, separator);
    try {
      Double.parseDouble(value);
    }
    catch (Exception e) {
      value = value.replace(separator, usedSeparator);
    }
    return value;
  }

  // -- Word decoding - primitive types to bytes --

  /** Translates the short value into an array of two bytes. */
  public static byte[] shortToBytes(short value, boolean little) {
    byte[] v = new byte[2];
    unpackBytes(value, v, 0, 2, little);
    return v;
  }

  /** Translates the int value into an array of four bytes. */
  public static byte[] intToBytes(int value, boolean little) {
    byte[] v = new byte[4];
    unpackBytes(value, v, 0, 4, little);
    return v;
  }

  /** Translates the float value into an array of four bytes. */
  public static byte[] floatToBytes(float value, boolean little) {
    byte[] v = new byte[4];
    unpackBytes(Float.floatToIntBits(value), v, 0, 4, little);
    return v;
  }

  /** Translates the long value into an array of eight bytes. */
  public static byte[] longToBytes(long value, boolean little) {
    byte[] v = new byte[8];
    unpackBytes(value, v, 0, 8, little);
    return v;
  }

  /** Translates the double value into an array of eight bytes. */
  public static byte[] doubleToBytes(double value, boolean little) {
    byte[] v = new byte[8];
    unpackBytes(Double.doubleToLongBits(value), v, 0, 8, little);
    return v;
  }

  /** Translates an array of short values into an array of byte values. */
  public static byte[] shortsToBytes(short[] values, boolean little) {
    byte[] v = new byte[values.length * 2];
    for (int i=0; i<values.length; i++) {
      unpackBytes(values[i], v, i * 2, 2, little);
    }
    return v;
  }

  /** Translates an array of int values into an array of byte values. */
  public static byte[] intsToBytes(int[] values, boolean little) {
    byte[] v = new byte[values.length * 4];
    for (int i=0; i<values.length; i++) {
      unpackBytes(values[i], v, i * 4, 4, little);
    }
    return v;
  }

  /** Translates an array of float values into an array of byte values. */
  public static byte[] floatsToBytes(float[] values, boolean little) {
    byte[] v = new byte[values.length * 4];
    for (int i=0; i<values.length; i++) {
      unpackBytes(Float.floatToIntBits(values[i]), v, i * 4, 4, little);
    }
    return v;
  }

  /** Translates an array of long values into an array of byte values. */
  public static byte[] longsToBytes(long[] values, boolean little) {
    byte[] v = new byte[values.length * 8];
    for (int i=0; i<values.length; i++) {
      unpackBytes(values[i], v, i * 8, 8, little);
    }
    return v;
  }

  /** Translates an array of double values into an array of byte values. */
  public static byte[] doublesToBytes(double[] values, boolean little) {
    byte[] v = new byte[values.length * 8];
    for (int i=0; i<values.length; i++) {
      unpackBytes(Double.doubleToLongBits(values[i]), v, i * 8, 8, little);
    }
    return v;
  }

  /**
   * Translates nBytes of the given long and places the result in the
   * given byte array.
   *
   * @throws IllegalArgumentException
   *   if the specified indices fall outside the buffer
   */
  public static void unpackBytes(long value, byte[] buf, int ndx,
    int nBytes, boolean little)
  {
    if (buf.length < ndx + nBytes) {
      throw new IllegalArgumentException("Invalid indices: buf.length=" +
        buf.length + ", ndx=" + ndx + ", nBytes=" + nBytes);
    }
    if (little) {
      for (int i=0; i<nBytes; i++) {
        buf[ndx + i] = (byte) ((value >> (8 * i)) & 0xff);
      }
    }
    else {
      for (int i=0; i<nBytes; i++) {
        buf[ndx + i] = (byte) ((value >> (8 * (nBytes - i - 1))) & 0xff);
      }
    }
  }

  /**
   * Convert a byte array to the appropriate 1D primitive type array.
   *
   * @param b Byte array to convert.
   * @param bpp Denotes the number of bytes in the returned primitive type
   *   (e.g. if bpp == 2, we should return an array of type short).
   * @param fp If set and bpp == 4 or bpp == 8, then return floats or doubles.
   * @param little Whether byte array is in little-endian order.
   */
  public static Object makeDataArray(byte[] b,
    int bpp, boolean fp, boolean little)
  {
    if (bpp == 1) {
      return b;
    }
    else if (bpp == 2) {
      short[] s = new short[b.length / 2];
      for (int i=0; i<s.length; i++) {
        s[i] = bytesToShort(b, i * 2, 2, little);
      }
      return s;
    }
    else if (bpp == 4 && fp) {
      float[] f = new float[b.length / 4];
      for (int i=0; i<f.length; i++) {
        f[i] = bytesToFloat(b, i * 4, 4, little);
      }
      return f;
    }
    else if (bpp == 4) {
      int[] i = new int[b.length / 4];
      for (int j=0; j<i.length; j++) {
        i[j] = bytesToInt(b, j * 4, 4, little);
      }
      return i;
    }
    else if (bpp == 8 && fp) {
      double[] d = new double[b.length / 8];
      for (int i=0; i<d.length; i++) {
        d[i] = bytesToDouble(b, i * 8, 8, little);
      }
      return d;
    }
    else if (bpp == 8) {
      long[] l = new long[b.length / 8];
      for (int i=0; i<l.length; i++) {
        l[i] = bytesToLong(b, i * 8, 8, little);
      }
      return l;
    }
    return null;
  }

  /**
   * Convert a byte array to the appropriate 2D primitive type array.
   *
   * @param b Byte array to convert.
   * @param bpp Denotes the number of bytes in the returned primitive type
   *   (e.g. if bpp == 2, we should return an array of type short).
   * @param fp If set and bpp == 4 or bpp == 8, then return floats or doubles.
   * @param little Whether byte array is in little-endian order.
   * @param height The height of the output primitive array (2nd dim length).
   *
   * @return a 2D primitive array of appropriate type,
   *   dimensioned [height][b.length / (bpp * height)]
   *
   * @throws IllegalArgumentException if input byte array does not divide
   *   evenly into height pieces
   */
  public static Object makeDataArray2D(byte[] b,
    int bpp, boolean fp, boolean little, int height)
  {
    if (b.length % (bpp * height) != 0) {

    }
    final int width = b.length / (bpp * height);
    if (bpp == 1) {
      byte[][] b2 = new byte[height][width];
      for (int y=0; y<height; y++) {
        int index = width * y;
        System.arraycopy(b, index, b2[y], 0, width);
      }
      return b2;
    }
    else if (bpp == 2) {
      short[][] s = new short[height][width];
      for (int y=0; y<height; y++) {
        for (int x=0; x<width; x++) {
          int index = 2 * (width * y + x);
          s[y][x] = bytesToShort(b, index, 2, little);
        }
      }
      return s;
    }
    else if (bpp == 4 && fp) {
      float[][] f = new float[height][width];
      for (int y=0; y<height; y++) {
        for (int x=0; x<width; x++) {
          int index = 4 * (width * y + x);
          f[y][x] = bytesToFloat(b, index, 4, little);
        }
      }
      return f;
    }
    else if (bpp == 4) {
      int[][] i = new int[height][width];
      for (int y=0; y<height; y++) {
        for (int x=0; x<width; x++) {
          int index = 4 * (width * y + x);
          i[y][x] = bytesToInt(b, index, 4, little);
        }
      }
      return i;
    }
    else if (bpp == 8 && fp) {
      double[][] d = new double[height][width];
      for (int y=0; y<height; y++) {
        for (int x=0; x<width; x++) {
          int index = 8 * (width * y + x);
          d[y][x] = bytesToDouble(b, index, 8, little);
        }
      }
      return d;
    }
    else if (bpp == 8) {
      long[][] l = new long[height][width];
      for (int y=0; y<height; y++) {
        for (int x=0; x<width; x++) {
          int index = 8 * (width * y + x);
          l[y][x] = bytesToLong(b, index, 8, little);
        }
      }
      return l;
    }
    return null;
  }

  // -- Byte swapping --

  public static short swap(short x) {
    return (short) ((x << 8) | ((x >> 8) & 0xFF));
  }

  public static char swap(char x) {
    return (char) ((x << 8) | ((x >> 8) & 0xFF));
  }

  public static int swap(int x) {
    return (swap((short) x) << 16) | (swap((short) (x >> 16)) & 0xFFFF);
  }

  public static long swap(long x) {
    return ((long) swap((int) x) << 32) | (swap((int) (x >> 32)) & 0xFFFFFFFFL);
  }

  public static float swap(float x) {
    return Float.intBitsToFloat(swap(Float.floatToIntBits(x)));
  }

  public static double swap(double x) {
    return Double.longBitsToDouble(swap(Double.doubleToLongBits(x)));
  }

  // -- Strings --

  /** Remove null bytes from a string. */
  public static String stripString(String toStrip) {
    StringBuffer s = new StringBuffer();
    for (int i=0; i<toStrip.length(); i++) {
      if (toStrip.charAt(i) != 0) {
        s.append(toStrip.charAt(i));
      }
    }
    return s.toString().trim();
  }

  /** Check if two filenames have the same prefix. */
  public static boolean samePrefix(String s1, String s2) {
    if (s1 == null || s2 == null) return false;
    int n1 = s1.indexOf(".");
    int n2 = s2.indexOf(".");
    if ((n1 == -1) || (n2 == -1)) return false;

    int slash1 = s1.lastIndexOf(File.pathSeparator);
    int slash2 = s2.lastIndexOf(File.pathSeparator);

    String sub1 = s1.substring(slash1 + 1, n1);
    String sub2 = s2.substring(slash2 + 1, n2);
    return sub1.equals(sub2) || sub1.startsWith(sub2) || sub2.startsWith(sub1);
  }

  /** Remove unprintable characters from the given string. */
  public static String sanitize(String s) {
    if (s == null) return null;
    StringBuffer buf = new StringBuffer(s);
    for (int i=0; i<buf.length(); i++) {
      char c = buf.charAt(i);
      if (c != '\t' && c != '\n' && Character.isISOControl(c)) {
        buf = buf.deleteCharAt(i--);
      }
    }
    return buf.toString();
  }

  // -- Normalization --

  /**
   * Normalize the given float array so that the minimum value maps to 0.0
   * and the maximum value maps to 1.0.
   */
  public static float[] normalizeFloats(float[] data) {
    float[] rtn = new float[data.length];

    // determine the finite min and max values
    float min = Float.MAX_VALUE;
    float max = Float.MIN_VALUE;
    for (int i=0; i<data.length; i++) {
      if (data[i] == Float.POSITIVE_INFINITY ||
        data[i] == Float.NEGATIVE_INFINITY)
      {
        continue;
      }
      if (data[i] < min) min = data[i];
      if (data[i] > max) max = data[i];
    }

    // normalize infinity values
    for (int i=0; i<data.length; i++) {
      if (data[i] == Float.POSITIVE_INFINITY) data[i] = max;
      else if (data[i] == Float.NEGATIVE_INFINITY) data[i] = min;
    }

    // now normalize; min => 0.0, max => 1.0
    float range = max - min;
    for (int i=0; i<rtn.length; i++) {
      rtn[i] = (data[i] - min) / range;
    }
    return rtn;
  }

  /**
   * Normalize the given double array so that the minimum value maps to 0.0
   * and the maximum value maps to 1.0.
   */
  public static double[] normalizeDoubles(double[] data) {
    double[] rtn = new double[data.length];

    // determine the finite min and max values
    double min = Double.MAX_VALUE;
    double max = Double.MIN_VALUE;
    for (int i=0; i<data.length; i++) {
      if (data[i] == Double.POSITIVE_INFINITY ||
        data[i] == Double.NEGATIVE_INFINITY)
      {
        continue;
      }
      if (data[i] < min) min = data[i];
      if (data[i] > max) max = data[i];
    }

    // normalize infinity values
    for (int i=0; i<data.length; i++) {
      if (data[i] == Double.POSITIVE_INFINITY) data[i] = max;
      else if (data[i] == Double.NEGATIVE_INFINITY) data[i] = min;
    }

    // now normalize; min => 0.0, max => 1.0
    double range = max - min;
    for (int i=0; i<rtn.length; i++) {
      rtn[i] = (data[i] - min) / range;
    }
    return rtn;
  }

  // -- Array handling --

  /**
   * Allocates a 1-dimensional byte array matching the product of the given
   * sizes.
   * 
   * @param sizes list of sizes from which to allocate the array
   * @return a byte array of the appropriate size
   * @throws IllegalArgumentException if the total size exceeds 2GB, which is
   *           the maximum size of an array in Java; or if any size argument is
   *           zero or negative
   */
  public static byte[] allocate(int... sizes) throws IllegalArgumentException {
    if (sizes == null) return null;
    if (sizes.length == 0) return new byte[0];
    int total = safeMultiply32(sizes);
    return new byte[total];
  }

  /**
   * Checks that the product of the given sizes does not exceed the 32-bit
   * integer limit (i.e., {@link Integer#MAX_VALUE}).
   * 
   * @param sizes list of sizes from which to compute the product
   * @return the product of the given sizes
   * @throws IllegalArgumentException if the total size exceeds 2GiB, which is
   *           the maximum size of an int in Java; or if any size argument is
   *           zero or negative
   */
  public static int safeMultiply32(int... sizes)
    throws IllegalArgumentException
  {
    if (sizes.length == 0) return 0;
    long total = 1;
    for (int size : sizes) {
      if (size < 1) {
        throw new IllegalArgumentException("Invalid array size: " +
          sizeAsProduct(sizes));
      }
      total *= size;
      if (total > Integer.MAX_VALUE) {
        throw new IllegalArgumentException("Array size too large: " +
          sizeAsProduct(sizes));
      }
    }
    // NB: The downcast to int is safe here, due to the checks above.
    return (int) total;
  }

  /**
   * Checks that the product of the given sizes does not exceed the 64-bit
   * integer limit (i.e., {@link Long#MAX_VALUE}).
   * 
   * @param sizes list of sizes from which to compute the product
   * @return the product of the given sizes
   * @throws IllegalArgumentException if the total size exceeds 8EiB, which is
   *           the maximum size of a long in Java; or if any size argument is
   *           zero or negative
   */
  public static long safeMultiply64(long... sizes)
    throws IllegalArgumentException
  {
    if (sizes.length == 0) return 0;
    long total = 1;
    for (long size : sizes) {
      if (size < 1) {
        throw new IllegalArgumentException("Invalid array size: " +
          sizeAsProduct(sizes));
      }
      if (willOverflow(total, size)) {
        throw new IllegalArgumentException("Array size too large: " +
          sizeAsProduct(sizes));
      }
      total *= size;
    }
    return total;
  }

  /** Returns true if the given value is contained in the given array. */
  public static boolean containsValue(int[] array, int value) {
    return indexOf(array, value) != -1;
  }

  /**
   * Returns the index of the first occurrence of the given value in the given
   * array. If the value is not in the array, returns -1.
   */
  public static int indexOf(int[] array, int value) {
    for (int i=0; i<array.length; i++) {
      if (array[i] == value) return i;
    }
    return -1;
  }

  /**
   * Returns the index of the first occurrence of the given value in the given
   * Object array. If the value is not in the array, returns -1.
   */
  public static int indexOf(Object[] array, Object value) {
    for (int i=0; i<array.length; i++) {
      if (value == null) {
        if (array[i] == null) return i;
      }
      else if (value.equals(array[i])) return i;
    }
    return -1;
  }

  // -- Signed data conversion --

  public static byte[] makeSigned(byte[] b) {
    for (int i=0; i<b.length; i++) {
      b[i] = (byte) (b[i] + 128);
    }
    return b;
  }

  public static short[] makeSigned(short[] s) {
    for (int i=0; i<s.length; i++) {
      s[i] = (short) (s[i] + 32768);
    }
    return s;
  }

  public static int[] makeSigned(int[] i) {
    for (int j=0; j<i.length; j++) {
      i[j] = (int) (i[j] + 2147483648L);
    }
    return i;
  }

  // -- Helper methods --

  private static String sizeAsProduct(int... sizes) {
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (int size : sizes) {
      if (first) first = false;
      else sb.append(" x ");
      sb.append(size);
    }
    return sb.toString();
  }

  private static String sizeAsProduct(long... sizes) {
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (long size : sizes) {
      if (first) first = false;
      else sb.append(" x ");
      sb.append(size);
    }
    return sb.toString();
  }

  private static boolean willOverflow(long v1, long v2) {
    return Long.MAX_VALUE / v1 < v2;
  }

}
