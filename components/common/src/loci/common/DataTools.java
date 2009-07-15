//
// DataTools.java
//

/*
LOCI Common package: utilities for I/O, reflection and miscellaneous tasks.
Copyright (C) 2005-@year@ Melissa Linkert and Curtis Rueden.

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

package loci.common;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A utility class with convenience methods for
 * reading, writing and decoding words.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/common/src/loci/common/DataTools.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/common/src/loci/common/DataTools.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Chris Allan callan at blackcat.ca
 * @author Melissa Linkert linkert at wisc.edu
 */
public final class DataTools {

  // -- Constants --

  /** Timestamp formats. */
  public static final int UNIX = 0;  // January 1, 1970
  public static final int COBOL = 1;  // January 1, 1601
  public static final int MICROSOFT = 2; // December 30, 1899
  public static final int ZVI = 3;

  /** Milliseconds until UNIX epoch. */
  public static final long UNIX_EPOCH = 0;
  public static final long COBOL_EPOCH = 11644444800000L;
  public static final long MICROSOFT_EPOCH = 2272060800000L;
  public static final long ZVI_EPOCH = 2921084975759000L;

  /** ISO 8601 date format string. */
  public static final String ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

  /** Factory for generating SAX parsers. */
  public static final SAXParserFactory SAX_FACTORY =
    SAXParserFactory.newInstance();

  // -- Static fields --

  /**
   * Persistent byte array for calling
   * {@link java.io.DataInput#readFully(byte[], int, int)} efficiently.
   */
  private static ThreadLocal eightBytes = new ThreadLocal() {
    protected synchronized Object initialValue() {
      return new byte[8];
    }
  };

  // -- Constructor --

  private DataTools() { }

  // -- Data reading --

  /** Reads 1 signed byte [-128, 127]. */
  public static byte readSignedByte(DataInput in) throws IOException {
    byte[] b = (byte[]) eightBytes.get();
    in.readFully(b, 0, 1);
    return b[0];
  }

  /** Reads 1 unsigned byte [0, 255]. */
  public static short readUnsignedByte(DataInput in) throws IOException {
    short q = readSignedByte(in);
    if (q < 0) q += 256;
    return q;
  }

  /** Reads 2 signed bytes [-32768, 32767]. */
  public static short read2SignedBytes(DataInput in, boolean little)
    throws IOException
  {
    byte[] b = (byte[]) eightBytes.get();
    in.readFully(b, 0, 2);
    return bytesToShort(b, little);
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
    byte[] b = (byte[]) eightBytes.get();
    in.readFully(b, 0, 4);
    return bytesToInt(b, little);
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
    byte[] b = (byte[]) eightBytes.get();
    in.readFully(b, 0, 8);
    return bytesToLong(b, little);
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
    byte[] b = s.getBytes("UTF-8");
    out.write(b);
  }

  /** Writes a double to the given data output destination. */
  public static void writeDouble(DataOutput out, double v, boolean little)
    throws IOException
  {
    writeLong(out, Double.doubleToLongBits(v), little);
  }

  /** Writes a long to the given data output destination. */
  public static void writeLong(DataOutput out, long v, boolean little)
    throws IOException
  {
    for (int i=0; i<8; i++) {
      int shift = little ? i * 8 : 64 - (i + 1) * 8;
      out.write((int) ((v >>> shift) & 0xff));
    }
  }

  /** Writes a float to the given data output destination. */
  public static void writeFloat(DataOutput out, float v, boolean little)
    throws IOException
  {
    writeInt(out, Float.floatToIntBits(v), little);
  }

  /** Writes an integer to the given data output destination. */
  public static void writeInt(DataOutput out, int v, boolean little)
    throws IOException
  {
    for (int i=0; i<4; i++) {
      int shift = little ? i * 8 : 32 - (i + 1) * 8;
      out.write((int) ((v >>> shift) & 0xff));
    }
  }

  /** Writes a short to the given data output destination. */
  public static void writeShort(DataOutput out, int v, boolean little)
    throws IOException
  {
    for (int i=0; i<2; i++) {
      int shift = little ? i * 8 : 16 - (i + 1) * 8;
      out.write((int) ((v >>> shift) & 0xff));
    }
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
   * Translates up to the first 4 bytes of a byte array to a float.
   * If there are fewer than 4 bytes in the array, the MSBs are all
   * assumed to be zero (regardless of endianness).
   */
  public static float bytesToFloat(byte[] bytes, int off, int len,
    boolean little)
  {
    return Float.intBitsToFloat(bytesToInt(bytes, off, len, little));
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
    for (int i=0, ndx=off; i<len; i++, ndx++) {
      total |= (bytes[ndx] < 0 ? 256L + bytes[ndx] :
        (long) bytes[ndx]) << ((little ? i : len - i - 1) * 8);
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

  /**
   * Translates the specified number of bytes of a byte array to a double.
   * If there are fewer than 8 bytes in the array, the MSBs are all assumed
   * to be zero (regardless of endianness).
   */
  public static double bytesToDouble(byte[] bytes, int offset, int len,
    boolean little)
  {
    return Double.longBitsToDouble(bytesToLong(bytes, offset, len, little));
  }

  /** Translates the short value into an array of two bytes. */
  public static byte[] shortToBytes(short value, boolean little) {
    byte[] v = new byte[2];
    unpackShort(value, v, 0, little);
    return v;
  }

  /** Translates the int value into an array of four bytes. */
  public static byte[] intToBytes(int value, boolean little) {
    byte[] v = new byte[4];
    unpackBytes(value, v, 0, 4, little);
    return v;
  }

  /** Translates an array of short values into an array of byte values. */
  public static byte[] shortsToBytes(short[] values, boolean little) {
    byte[] v = new byte[values.length * 2];
    for (int i=0; i<values.length; i++) {
      unpackShort(values[i], v, i * 2, little);
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

  /**
   * Translates the short value into two bytes, and places them in a byte
   * array at the given index.
   */
  public static void unpackShort(short value, byte[] buf, int ndx,
    boolean little)
  {
    if (little) {
      buf[ndx] = (byte) (value & 0xff);
      buf[ndx + 1] = (byte) ((value >> 8) & 0xff);
    }
    else {
      buf[ndx + 1] = (byte) (value & 0xff);
      buf[ndx] = (byte) ((value >> 8) & 0xff);
    }
  }

  /**
   * Translates nBytes of the given long and places the result in the
   * given byte array.
   */
  public static void unpackBytes(long value, byte[] buf, int ndx,
    int nBytes, boolean little)
  {
    if (little) {
      for (int i=0; i<nBytes; i++) {
        buf[ndx + i] = (byte) ((value >> (8*i)) & 0xff);
      }
    }
    else {
      for (int i=0; i<nBytes; i++) {
        buf[ndx + i] = (byte) ((value >> (8*(nBytes - i - 1))) & 0xff);
      }
    }
  }

  /**
   * Convert a byte array to the appropriate primitive type array.
   * @param b Byte array to convert.
   * @param bpp Denotes the number of bytes in the returned primitive type
   *   (e.g. if bpp == 2, we should return an array of type short).
   * @param fp If set and bpp == 4 or bpp == 8, then return floats or doubles.
   * @param little Whether byte array is in little-endian order.
   */
  public static Object makeDataArray(byte[] b, int bpp, boolean fp,
    boolean little)
  {
    if (bpp == 1) {
      return b;
    }
    else if (bpp == 2) {
      short[] s = new short[b.length / 2];
      for (int i=0; i<s.length; i++) {
        s[i] = bytesToShort(b, i*2, 2, little);
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
        i[j] = bytesToInt(b, j*4, 4, little);
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
        l[i] = bytesToLong(b, i*8, 8, little);
      }
      return l;
    }
    return null;
  }

  /**
   * @deprecated Use {@link #makeDataArray(byte[], int, boolean, boolean)}
   *   regardless of signedness.
   */
  public static Object makeDataArray(byte[] b,
    int bpp, boolean fp, boolean little, boolean signed)
  {
    return makeDataArray(b, bpp, fp, little);
  }

  // -- Byte swapping --

  public static short swap(short x) {
    return (short) ((x << 8) | ((x >> 8) & 0xFF));
  }

  public static char swap(char x) {
    return (char) ((x << 8) | ((x >> 8) & 0xFF));
  }

  public static int swap(int x) {
    return (int) ((swap((short) x) << 16) | (swap((short) (x >> 16)) & 0xFFFF));
  }

  public static long swap(long x) {
    return (long) (((long) swap((int) x) << 32) |
      ((long) swap((int) (x >> 32)) & 0xFFFFFFFFL));
  }

  public static float swap(float x) {
    return Float.intBitsToFloat(swap(Float.floatToIntBits(x)));
  }

  public static double swap(double x) {
    return Double.longBitsToDouble(swap(Double.doubleToLongBits(x)));
  }

  // -- Miscellaneous --

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
      if (c != '\t' && c != '\n' && (c < ' ' || c > '~')) {
        buf = buf.deleteCharAt(i--);
      }
    }
    return buf.toString();
  }

  /** Remove invalid characters from an XML string. */
  public static String sanitizeXML(String s) {
    for (int i=0; i<s.length(); i++) {
      char c = s.charAt(i);
      if (Character.isISOControl(c) || !Character.isDefined(c) || c > '~') {
        s = s.replace(c, ' ');
      }
    }
    return s;
  }

  /**
   * Normalize the given float array so that the minimum value maps to 0.0
   * and the maximum value maps to 1.0.
   */
  public static float[] normalizeFloats(float[] data) {
    float[] rtn = new float[data.length];

    // make a quick pass through to determine the real min and max values

    float min = Float.MAX_VALUE;
    float max = Float.MIN_VALUE;
    float range = max - min;

    for (int i=0; i<data.length; i++) {
      if (data[i] < min) min = data[i];
      if (data[i] > max) max = data[i];
    }

    // now normalize; min => 0.0, max => 1.0

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

    double min = Double.MAX_VALUE;
    double max = Double.MIN_VALUE;
    double range = max - min;

    for (int i=0; i<data.length; i++) {
      if (data[i] < min) min = data[i];
      if (data[i] > max) max = data[i];
    }

    for (int i=0; i<rtn.length; i++) {
      rtn[i] = (data[i] - min) / range;
    }
    return rtn;
  }

  public static void parseXML(String xml, DefaultHandler handler)
    throws IOException
  {
    parseXML(xml.getBytes(), handler);
  }

  public static void parseXML(RandomAccessInputStream stream,
    DefaultHandler handler) throws IOException
  {
    byte[] b = new byte[(int) (stream.length() - stream.getFilePointer())];
    stream.read(b);
    parseXML(b, handler);
    b = null;
  }

  public static void parseXML(byte[] xml, DefaultHandler handler)
    throws IOException
  {
    try {
      SAXParser parser = SAX_FACTORY.newSAXParser();
      parser.parse(new ByteArrayInputStream(xml), handler);
    }
    catch (ParserConfigurationException exc) {
      IOException e = new IOException();
      e.initCause(exc);
      throw e;
    }
    catch (SAXException exc) {
      IOException e = new IOException();
      e.initCause(exc);
      throw e;

    }
  }

  // -- Date handling --

  /** Converts the given timestamp into an ISO 8601 date. */
  public static String convertDate(long stamp, int format) {
    // see http://www.merlyn.demon.co.uk/critdate.htm for more information on
    // dates than you will ever need (or want)

    long ms = stamp;

    switch (format) {
      case UNIX:
        ms -= UNIX_EPOCH;
        break;
      case COBOL:
        ms -= COBOL_EPOCH;
        break;
      case MICROSOFT:
        ms -= MICROSOFT_EPOCH;
        break;
      case ZVI:
        ms -= ZVI_EPOCH;
        break;
    }

    SimpleDateFormat fmt = new SimpleDateFormat(ISO8601_FORMAT);
    StringBuffer sb = new StringBuffer();

    Date d = new Date(ms);

    fmt.format(d, sb, new FieldPosition(0));
    return sb.toString();
  }

  /** Return given date in ISO 8601 format. */
  public static String formatDate(String date, String format) {
    SimpleDateFormat f = new SimpleDateFormat(format);
    Date d = f.parse(date, new ParsePosition(0));
    if (d == null) return null;
    f = new SimpleDateFormat(ISO8601_FORMAT);
    return f.format(d);
  }

  /**
   * Converts a string date in the given format to a long timestamp
   * (in Unix format: milliseconds since January 1, 1970).
   */
  public static long getTime(String date, String format) {
    SimpleDateFormat f = new SimpleDateFormat(format);
    Date d = f.parse(date, new ParsePosition(0));
    if (d == null) return -1;
    return d.getTime();
  }

  // -- Array handling --

  /** Returns true if the given value is contained in the given array. */
  public static boolean containsValue(int[] array, int value) {
    return indexOf(array, value) != -1;
  }

  /**
   * Returns the index of the first occurence of the given value in the given
   * array.  If the value is not in the array, returns -1.
   */
  public static int indexOf(int[] array, int value) {
    for (int i=0; i<array.length; i++) {
      if (array[i] == value) return i;
    }
    return -1;
  }

  /**
   * Returns the index of the first occurence of the given value in the given
   * Object array.  If the value is not in the array, returns -1.
   */
  public static int indexOf(Object[] array, Object value) {
    for (int i=0; i<array.length; i++) {
      if (value.equals(array[i])) return i;
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

}
