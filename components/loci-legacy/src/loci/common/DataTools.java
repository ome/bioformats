/*
 * #%L
 * OME SCIFIO package for reading and converting scientific file formats.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package loci.common;

import java.io.IOException;

/**
 * A legacy delegator class for ome.scifio.common.DataTools.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/DataTools.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/DataTools.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Chris Allan callan at blackcat.ca
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public final class DataTools {

  // -- Constants --

  // -- Static fields --
  
  // -- Constructor --

  private DataTools() { }

  // -- Data reading --

  /** Reads the contents of the given file into a string. */
  public static String readFile(String id) throws IOException {
    return ome.scifio.common.DataTools.readFile(id);
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
    return ome.scifio.common.DataTools.bytesToShort(bytes, off, little);
  }

  /**
   * Translates up to the first 2 bytes of a byte array beyond the given
   * offset to a short. If there are fewer than 2 bytes available
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static short bytesToShort(byte[] bytes, int off, boolean little) {
    return ome.scifio.common.DataTools.bytesToShort(bytes, off, little);
  }

  /**
   * Translates up to the first 2 bytes of a byte array to a short.
   * If there are fewer than 2 bytes available, the MSBs are all
   * assumed to be zero (regardless of endianness).
   */
  public static short bytesToShort(byte[] bytes, boolean little) {
    return ome.scifio.common.DataTools.bytesToShort(bytes, little);
  }

  /**
   * Translates up to the first len bytes of a byte array byond the given
   * offset to a short. If there are fewer than len bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static short bytesToShort(short[] bytes, int off, int len,
    boolean little)
  {
    return ome.scifio.common.DataTools.bytesToShort(bytes, off, len, little);
  }

  /**
   * Translates up to the first 2 bytes of a byte array byond the given
   * offset to a short. If there are fewer than 2 bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static short bytesToShort(short[] bytes, int off, boolean little) {
    return ome.scifio.common.DataTools.bytesToShort(bytes, off, little);
  }

  /**
   * Translates up to the first 2 bytes of a byte array to a short.
   * If there are fewer than 2 bytes available, the MSBs are all
   * assumed to be zero (regardless of endianness).
   */
  public static short bytesToShort(short[] bytes, boolean little) {
    return ome.scifio.common.DataTools.bytesToShort(bytes, little);
  }

  /**
   * Translates up to the first len bytes of a byte array beyond the given
   * offset to an int. If there are fewer than len bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static int bytesToInt(byte[] bytes, int off, int len,
    boolean little)
  {
    return ome.scifio.common.DataTools.bytesToInt(bytes, off, len, little);
  }

  /**
   * Translates up to the first 4 bytes of a byte array beyond the given
   * offset to an int. If there are fewer than 4 bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static int bytesToInt(byte[] bytes, int off, boolean little) {
    return ome.scifio.common.DataTools.bytesToInt(bytes, off, little);
  }

  /**
   * Translates up to the first 4 bytes of a byte array to an int.
   * If there are fewer than 4 bytes available, the MSBs are all
   * assumed to be zero (regardless of endianness).
   */
  public static int bytesToInt(byte[] bytes, boolean little) {
    return ome.scifio.common.DataTools.bytesToInt(bytes, little);
  }

  /**
   * Translates up to the first len bytes of a byte array beyond the given
   * offset to an int. If there are fewer than len bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static int bytesToInt(short[] bytes, int off, int len,
    boolean little)
  {
    return ome.scifio.common.DataTools.bytesToInt(bytes, off, len, little);
  }

  /**
   * Translates up to the first 4 bytes of a byte array beyond the given
   * offset to an int. If there are fewer than 4 bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static int bytesToInt(short[] bytes, int off, boolean little) {
    return ome.scifio.common.DataTools.bytesToInt(bytes, off, little);
  }

  /**
   * Translates up to the first 4 bytes of a byte array to an int.
   * If there are fewer than 4 bytes available, the MSBs are all
   * assumed to be zero (regardless of endianness).
   */
  public static int bytesToInt(short[] bytes, boolean little) {
    return ome.scifio.common.DataTools.bytesToInt(bytes, little);
  }

  /**
   * Translates up to the first len bytes of a byte array beyond the given
   * offset to a float. If there are fewer than len bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static float bytesToFloat(byte[] bytes, int off, int len,
    boolean little)
  {
    return ome.scifio.common.DataTools.bytesToFloat(bytes, off, len, little);
  }

  /**
   * Translates up to the first 4 bytes of a byte array beyond a given
   * offset to a float. If there are fewer than 4 bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static float bytesToFloat(byte[] bytes, int off, boolean little) {
    return ome.scifio.common.DataTools.bytesToFloat(bytes, off, little);
  }

  /**
   * Translates up to the first 4 bytes of a byte array to a float.
   * If there are fewer than 4 bytes available, the MSBs are all
   * assumed to be zero (regardless of endianness).
   */
  public static float bytesToFloat(byte[] bytes, boolean little) {
    return ome.scifio.common.DataTools.bytesToFloat(bytes, little);
  }

  /**
   * Translates up to the first len bytes of a byte array beyond a given
   * offset to a float. If there are fewer than len bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static float bytesToFloat(short[] bytes, int off, int len,
    boolean little)
  {
    return ome.scifio.common.DataTools.bytesToFloat(bytes, off, len, little);
  }

  /**
   * Translates up to the first 4 bytes of a byte array beyond a given
   * offset to a float. If there are fewer than 4 bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static float bytesToFloat(short[] bytes, int off, boolean little) {
    return ome.scifio.common.DataTools.bytesToFloat(bytes, off, little);
  }

  /**
   * Translates up to the first 4 bytes of a byte array to a float.
   * If there are fewer than 4 bytes available, the MSBs are all
   * assumed to be zero (regardless of endianness).
   */
  public static float bytesToFloat(short[] bytes, boolean little) {
    return ome.scifio.common.DataTools.bytesToFloat(bytes, little);
  }

  /**
   * Translates up to the first len bytes of a byte array beyond the given
   * offset to a long. If there are fewer than len bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static long bytesToLong(byte[] bytes, int off, int len,
    boolean little)
  {
    return ome.scifio.common.DataTools.bytesToLong(bytes, off, len, little);
  }

  /**
   * Translates up to the first 8 bytes of a byte array beyond the given
   * offset to a long. If there are fewer than 8 bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static long bytesToLong(byte[] bytes, int off, boolean little) {
    return ome.scifio.common.DataTools.bytesToLong(bytes, off, little);
  }

  /**
   * Translates up to the first 8 bytes of a byte array to a long.
   * If there are fewer than 8 bytes available, the MSBs are all
   * assumed to be zero (regardless of endianness).
   */
  public static long bytesToLong(byte[] bytes, boolean little) {
    return ome.scifio.common.DataTools.bytesToLong(bytes, little);
  }

  /**
   * Translates up to the first len bytes of a byte array beyond the given
   * offset to a long. If there are fewer than len bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static long bytesToLong(short[] bytes, int off, int len,
    boolean little)
  {
    return ome.scifio.common.DataTools.bytesToLong(bytes, off, len, little);
  }

  /**
   * Translates up to the first 8 bytes of a byte array beyond the given
   * offset to a long. If there are fewer than 8 bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static long bytesToLong(short[] bytes, int off, boolean little) {
    return ome.scifio.common.DataTools.bytesToLong(bytes, off, little);
  }

  /**
   * Translates up to the first 8 bytes of a byte array to a long.
   * If there are fewer than 8 bytes available, the MSBs are all
   * assumed to be zero (regardless of endianness).
   */
  public static long bytesToLong(short[] bytes, boolean little) {
    return ome.scifio.common.DataTools.bytesToLong(bytes, little);
  }

  /**
   * Translates up to the first len bytes of a byte array beyond the given
   * offset to a double. If there are fewer than len bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static double bytesToDouble(byte[] bytes, int off, int len,
    boolean little)
  {
    return ome.scifio.common.DataTools.bytesToDouble(bytes, off, len, little);
  }

  /**
   * Translates up to the first 8 bytes of a byte array beyond the given
   * offset to a double. If there are fewer than 8 bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static double bytesToDouble(byte[] bytes, int off,
    boolean little)
  {
    return ome.scifio.common.DataTools.bytesToDouble(bytes, off, little);
  }

  /**
   * Translates up to the first 8 bytes of a byte array to a double.
   * If there are fewer than 8 bytes available, the MSBs are all
   * assumed to be zero (regardless of endianness).
   */
  public static double bytesToDouble(byte[] bytes, boolean little) {
    return ome.scifio.common.DataTools.bytesToDouble(bytes, little);
  }

  /**
   * Translates up to the first len bytes of a byte array beyond the given
   * offset to a double. If there are fewer than len bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static double bytesToDouble(short[] bytes, int off, int len,
    boolean little)
  {
    return ome.scifio.common.DataTools.bytesToDouble(bytes, off, len, little);
  }

  /**
   * Translates up to the first 8 bytes of a byte array beyond the given
   * offset to a double. If there are fewer than 8 bytes available,
   * the MSBs are all assumed to be zero (regardless of endianness).
   */
  public static double bytesToDouble(short[] bytes, int off,
    boolean little)
  {
    return ome.scifio.common.DataTools.bytesToDouble(bytes, off, little);
  }

  /**
   * Translates up to the first 8 bytes of a byte array to a double.
   * If there are fewer than 8 bytes available, the MSBs are all
   * assumed to be zero (regardless of endianness).
   */
  public static double bytesToDouble(short[] bytes, boolean little) {
    return ome.scifio.common.DataTools.bytesToDouble(bytes, little);
  }

  /** Translates the given byte array into a String of hexadecimal digits. */
  public static String bytesToHex(byte[] b) {
    return ome.scifio.common.DataTools.bytesToHex(b);
  }

  /** Normalize the decimal separator for the user's locale. */
  public static String sanitizeDouble(String value) {
    return ome.scifio.common.DataTools.sanitizeDouble(value);
  }

  // -- Word decoding - primitive types to bytes --

  /** Translates the short value into an array of two bytes. */
  public static byte[] shortToBytes(short value, boolean little) {
    return ome.scifio.common.DataTools.shortToBytes(value, little);
  }

  /** Translates the int value into an array of four bytes. */
  public static byte[] intToBytes(int value, boolean little) {
    return ome.scifio.common.DataTools.intToBytes(value, little);
  }

  /** Translates the float value into an array of four bytes. */
  public static byte[] floatToBytes(float value, boolean little) {
    return ome.scifio.common.DataTools.floatToBytes(value, little);
  }

  /** Translates the long value into an array of eight bytes. */
  public static byte[] longToBytes(long value, boolean little) {
    return ome.scifio.common.DataTools.longToBytes(value, little);
  }

  /** Translates the double value into an array of eight bytes. */
  public static byte[] doubleToBytes(double value, boolean little) {
    return ome.scifio.common.DataTools.doubleToBytes(value, little);
  }

  /** Translates an array of short values into an array of byte values. */
  public static byte[] shortsToBytes(short[] values, boolean little) {
    return ome.scifio.common.DataTools.shortsToBytes(values, little);
  }

  /** Translates an array of int values into an array of byte values. */
  public static byte[] intsToBytes(int[] values, boolean little) {
    return ome.scifio.common.DataTools.intsToBytes(values, little);
  }

  /** Translates an array of float values into an array of byte values. */
  public static byte[] floatsToBytes(float[] values, boolean little) {
    return ome.scifio.common.DataTools.floatsToBytes(values, little);
  }

  /** Translates an array of long values into an array of byte values. */
  public static byte[] longsToBytes(long[] values, boolean little) {
    return ome.scifio.common.DataTools.longsToBytes(values, little);
  }

  /** Translates an array of double values into an array of byte values. */
  public static byte[] doublesToBytes(double[] values, boolean little) {
    return ome.scifio.common.DataTools.doublesToBytes(values, little);
  }

  /** @deprecated Use {@link #unpackBytes(long, byte[], int, int, boolean) */
  @Deprecated
  public static void unpackShort(short value, byte[] buf, int ndx,
    boolean little)
  {
    ome.scifio.common.DataTools.unpackShort(value, buf, ndx, little);
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
    ome.scifio.common.DataTools.unpackBytes(value, buf, ndx, nBytes, little);
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
    return ome.scifio.common.DataTools.makeDataArray(b, bpp, fp, little);
  }

  /**
   * @param signed The signed parameter is ignored.
   * @deprecated Use {@link #makeDataArray(byte[], int, boolean, boolean)}
   *   regardless of signedness.
   */
  @Deprecated
  public static Object makeDataArray(byte[] b,
    int bpp, boolean fp, boolean little, boolean signed)
  {
    return ome.scifio.common.DataTools.makeDataArray(b, bpp, fp, little, signed);
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
    return ome.scifio.common.DataTools.makeDataArray2D(b, bpp, fp, little, height);
  }

  // -- Byte swapping --

  public static short swap(short x) {
    return ome.scifio.common.DataTools.swap(x);
  }

  public static char swap(char x) {
    return ome.scifio.common.DataTools.swap(x);
  }

  public static int swap(int x) {
    return ome.scifio.common.DataTools.swap(x);
  }

  public static long swap(long x) {
    return ome.scifio.common.DataTools.swap(x);
  }

  public static float swap(float x) {
    return ome.scifio.common.DataTools.swap(x);
  }

  public static double swap(double x) {
    return ome.scifio.common.DataTools.swap(x);
  }

  // -- Strings --

  /**
   * Convert byte array to a hexadecimal string.
   * @deprecated Use {@link bytesToHex(byte[])} instead.
   */
  @Deprecated
  public static String getHexString(byte[] b) {
    return ome.scifio.common.DataTools.getHexString(b);
  }

  /** Remove null bytes from a string. */
  public static String stripString(String toStrip) {
    return ome.scifio.common.DataTools.stripString(toStrip);
  }

  /** Check if two filenames have the same prefix. */
  public static boolean samePrefix(String s1, String s2) {
    return ome.scifio.common.DataTools.samePrefix(s1, s2);
  }

  /** Remove unprintable characters from the given string. */
  public static String sanitize(String s) {
    return ome.scifio.common.DataTools.sanitize(s);
  }

  // -- Normalization --

  /**
   * Normalize the given float array so that the minimum value maps to 0.0
   * and the maximum value maps to 1.0.
   */
  public static float[] normalizeFloats(float[] data) {
    return ome.scifio.common.DataTools.normalizeFloats(data);
  }

  /**
   * Normalize the given double array so that the minimum value maps to 0.0
   * and the maximum value maps to 1.0.
   */
  public static double[] normalizeDoubles(double[] data) {
    return ome.scifio.common.DataTools.normalizeDoubles(data);
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
    return ome.scifio.common.DataTools.allocate(sizes);
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
    return ome.scifio.common.DataTools.safeMultiply32(sizes);
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
    return ome.scifio.common.DataTools.safeMultiply64(sizes);
  }

  /** Returns true if the given value is contained in the given array. */
  public static boolean containsValue(int[] array, int value) {
    return ome.scifio.common.DataTools.containsValue(array, value);
  }

  /**
   * Returns the index of the first occurrence of the given value in the given
   * array. If the value is not in the array, returns -1.
   */
  public static int indexOf(int[] array, int value) {
    return ome.scifio.common.DataTools.indexOf(array, value);
  }

  /**
   * Returns the index of the first occurrence of the given value in the given
   * Object array. If the value is not in the array, returns -1.
   */
  public static int indexOf(Object[] array, Object value) {
    return ome.scifio.common.DataTools.indexOf(array, value);
  }

  // -- Signed data conversion --

  public static byte[] makeSigned(byte[] b) {
    return ome.scifio.common.DataTools.makeSigned(b);
  }

  public static short[] makeSigned(short[] s) {
    return ome.scifio.common.DataTools.makeSigned(s);
  }

  public static int[] makeSigned(int[] i) {
    return ome.scifio.common.DataTools.makeSigned(i);
  }

}
