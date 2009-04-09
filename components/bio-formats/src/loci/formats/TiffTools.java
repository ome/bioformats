//
// TiffTools.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

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

package loci.formats;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;
import loci.common.*;
import loci.formats.codec.*;

/**
 * A utility class for manipulating TIFF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/TiffTools.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/TiffTools.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Eric Kjellman egkjellman at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 * @author Chris Allan callan at blackcat.ca
 */
public final class TiffTools {

  // -- Constants --

  /** The number of bytes in each IFD entry. */
  public static final int BYTES_PER_ENTRY = 12;

  /** The number of bytes in each IFD entry of a BigTIFF file. */
  public static final int BIG_TIFF_BYTES_PER_ENTRY = 20;

  // non-IFD tags (for internal use)
  public static final int LITTLE_ENDIAN = 0;
  public static final int BIG_TIFF = 1;

  // IFD types
  public static final int BYTE = 1;
  public static final int ASCII = 2;
  public static final int SHORT = 3;
  public static final int LONG = 4;
  public static final int RATIONAL = 5;
  public static final int SBYTE = 6;
  public static final int UNDEFINED = 7;
  public static final int SSHORT = 8;
  public static final int SLONG = 9;
  public static final int SRATIONAL = 10;
  public static final int FLOAT = 11;
  public static final int DOUBLE = 12;
  public static final int LONG8 = 16;
  public static final int SLONG8 = 17;
  public static final int IFD8 = 18;

  public static final int[] BYTES_PER_ELEMENT = {
    -1, // invalid type
    1, // BYTE
    1, // ASCII
    2, // SHORT
    4, // LONG
    8, // RATIONAL
    1, // SBYTE
    1, // UNDEFINED
    2, // SSHORT
    4, // SLONG
    8, // SRATIONAL
    4, // FLOAT
    8, // DOUBLE
    -1, // invalid type
    -1, // invalid type
    -1, // invalid type
    8, // LONG8
    8, // SLONG8
    8 // IFD8
  };

  // IFD tags
  public static final int NEW_SUBFILE_TYPE = 254;
  public static final int SUBFILE_TYPE = 255;
  public static final int IMAGE_WIDTH = 256;
  public static final int IMAGE_LENGTH = 257;
  public static final int BITS_PER_SAMPLE = 258;
  public static final int COMPRESSION = 259;
  public static final int PHOTOMETRIC_INTERPRETATION = 262;
  public static final int THRESHHOLDING = 263;
  public static final int CELL_WIDTH = 264;
  public static final int CELL_LENGTH = 265;
  public static final int FILL_ORDER = 266;
  public static final int DOCUMENT_NAME = 269;
  public static final int IMAGE_DESCRIPTION = 270;
  public static final int MAKE = 271;
  public static final int MODEL = 272;
  public static final int STRIP_OFFSETS = 273;
  public static final int ORIENTATION = 274;
  public static final int SAMPLES_PER_PIXEL = 277;
  public static final int ROWS_PER_STRIP = 278;
  public static final int STRIP_BYTE_COUNTS = 279;
  public static final int MIN_SAMPLE_VALUE = 280;
  public static final int MAX_SAMPLE_VALUE = 281;
  public static final int X_RESOLUTION = 282;
  public static final int Y_RESOLUTION = 283;
  public static final int PLANAR_CONFIGURATION = 284;
  public static final int PAGE_NAME = 285;
  public static final int X_POSITION = 286;
  public static final int Y_POSITION = 287;
  public static final int FREE_OFFSETS = 288;
  public static final int FREE_BYTE_COUNTS = 289;
  public static final int GRAY_RESPONSE_UNIT = 290;
  public static final int GRAY_RESPONSE_CURVE = 291;
  public static final int T4_OPTIONS = 292;
  public static final int T6_OPTIONS = 293;
  public static final int RESOLUTION_UNIT = 296;
  public static final int PAGE_NUMBER = 297;
  public static final int TRANSFER_FUNCTION = 301;
  public static final int SOFTWARE = 305;
  public static final int DATE_TIME = 306;
  public static final int ARTIST = 315;
  public static final int HOST_COMPUTER = 316;
  public static final int PREDICTOR = 317;
  public static final int WHITE_POINT = 318;
  public static final int PRIMARY_CHROMATICITIES = 319;
  public static final int COLOR_MAP = 320;
  public static final int HALFTONE_HINTS = 321;
  public static final int TILE_WIDTH = 322;
  public static final int TILE_LENGTH = 323;
  public static final int TILE_OFFSETS = 324;
  public static final int TILE_BYTE_COUNTS = 325;
  public static final int INK_SET = 332;
  public static final int INK_NAMES = 333;
  public static final int NUMBER_OF_INKS = 334;
  public static final int DOT_RANGE = 336;
  public static final int TARGET_PRINTER = 337;
  public static final int EXTRA_SAMPLES = 338;
  public static final int SAMPLE_FORMAT = 339;
  public static final int S_MIN_SAMPLE_VALUE = 340;
  public static final int S_MAX_SAMPLE_VALUE = 341;
  public static final int TRANSFER_RANGE = 342;
  public static final int JPEG_TABLES = 347;
  public static final int JPEG_PROC = 512;
  public static final int JPEG_INTERCHANGE_FORMAT = 513;
  public static final int JPEG_INTERCHANGE_FORMAT_LENGTH = 514;
  public static final int JPEG_RESTART_INTERVAL = 515;
  public static final int JPEG_LOSSLESS_PREDICTORS = 517;
  public static final int JPEG_POINT_TRANSFORMS = 518;
  public static final int JPEG_Q_TABLES = 519;
  public static final int JPEG_DC_TABLES = 520;
  public static final int JPEG_AC_TABLES = 521;
  public static final int Y_CB_CR_COEFFICIENTS = 529;
  public static final int Y_CB_CR_SUB_SAMPLING = 530;
  public static final int Y_CB_CR_POSITIONING = 531;
  public static final int REFERENCE_BLACK_WHITE = 532;
  public static final int COPYRIGHT = 33432;
  public static final int EXIF = 34665;

  // compression types
  public static final int UNCOMPRESSED = 1;
  public static final int CCITT_1D = 2;
  public static final int GROUP_3_FAX = 3;
  public static final int GROUP_4_FAX = 4;
  public static final int LZW = 5;
  //public static final int JPEG = 6;
  public static final int JPEG = 7;
  public static final int PACK_BITS = 32773;
  public static final int PROPRIETARY_DEFLATE = 32946;
  public static final int DEFLATE = 8;
  public static final int THUNDERSCAN = 32809;
  public static final int JPEG_2000 = 33003;
  public static final int ALT_JPEG = 33007;
  public static final int NIKON = 34713;
  public static final int LURAWAVE = 65535;

  // photometric interpretation types
  public static final int WHITE_IS_ZERO = 0;
  public static final int BLACK_IS_ZERO = 1;
  public static final int RGB = 2;
  public static final int RGB_PALETTE = 3;
  public static final int TRANSPARENCY_MASK = 4;
  public static final int CMYK = 5;
  public static final int Y_CB_CR = 6;
  public static final int CIE_LAB = 8;
  public static final int CFA_ARRAY = 32803;

  // TIFF header constants
  public static final int MAGIC_NUMBER = 42;
  public static final int BIG_TIFF_MAGIC_NUMBER = 43;
  public static final int LITTLE = 0x49;
  public static final int BIG = 0x4d;

  // -- Constructor --

  private TiffTools() { }

  // -- TiffTools API methods --

  /**
   * Tests the given data block to see if it represents
   * the first few bytes of a TIFF file.
   */
  public static boolean isValidHeader(byte[] block) {
    return checkHeader(block) != null;
  }

  /**
   * Tests the given stream to see if it represents
   * a TIFF file.
   */
  public static boolean isValidHeader(RandomAccessStream stream) {
    try {
      return checkHeader(stream) != null;
    }
    catch (IOException e) {
      return false;
    }
  }

  /**
   * Checks the TIFF header.
   * @return true if little-endian,
   *         false if big-endian,
   *         or null if not a TIFF.
   */
  public static Boolean checkHeader(byte[] block) {
    try {
      RandomAccessStream s = new RandomAccessStream(block);
      Boolean result = checkHeader(s);
      s.close();
      return result;
    }
    catch (IOException e) {
      return null;
    }
  }

  /**
   * Checks the TIFF header.
   * @return true if little-endian,
   *         false if big-endian,
   *         or null if not a TIFF.
   */
  public static Boolean checkHeader(RandomAccessStream stream)
    throws IOException
  {
    if (stream.length() < 4) return null;

    // byte order must be II or MM
    stream.seek(0);
    int endianOne = stream.read();
    int endianTwo = stream.read();
    boolean littleEndian = endianOne == LITTLE && endianTwo == LITTLE; // II
    boolean bigEndian = endianOne == BIG && endianTwo == BIG; // MM
    if (!littleEndian && !bigEndian) return null;

    // check magic number (42)
    stream.order(littleEndian);
    short magic = stream.readShort();
    if (magic != MAGIC_NUMBER && magic != BIG_TIFF_MAGIC_NUMBER) return null;

    return new Boolean(littleEndian);
  }

  /** Gets whether this is a BigTIFF IFD. */
  public static boolean isBigTiff(Hashtable ifd) throws FormatException {
    return ((Boolean)
      getIFDValue(ifd, BIG_TIFF, false, Boolean.class)).booleanValue();
  }

  /** Gets whether the TIFF information in the given IFD is little-endian. */
  public static boolean isLittleEndian(Hashtable ifd) throws FormatException {
    return ((Boolean)
      getIFDValue(ifd, LITTLE_ENDIAN, true, Boolean.class)).booleanValue();
  }

  // --------------------------- Reading TIFF files ---------------------------

  // -- IFD parsing methods --

  /**
   * Gets all IFDs within the given TIFF file, or null
   * if the given file is not a valid TIFF file.
   */
  public static Hashtable[] getIFDs(RandomAccessStream in) throws IOException {
    // check TIFF header
    Boolean result = checkHeader(in);
    if (result == null) return null;

    in.seek(2);
    boolean bigTiff = in.readShort() == BIG_TIFF_MAGIC_NUMBER;

    long offset = getFirstOffset(in, bigTiff);

    // compute maximum possible number of IFDs, for loop safety
    // each IFD must have at least one directory entry, which means that
    // each IFD must be at least 2 + 12 + 4 = 18 bytes in length
    long ifdMax = (in.length() - 8) / 18;

    // read in IFDs
    Vector v = new Vector();
    for (long ifdNum=0; ifdNum<ifdMax; ifdNum++) {
      Hashtable ifd = getIFD(in, ifdNum, offset, bigTiff);
      if (ifd == null || ifd.size() <= 1) break;
      v.add(ifd);
      offset = bigTiff ? in.readLong() : (long) (in.readInt() & 0xffffffffL);
      if (offset <= 0 || offset >= in.length()) break;
    }

    Hashtable[] ifds = new Hashtable[v.size()];
    v.copyInto(ifds);
    return ifds;
  }

  /**
   * Gets the first IFD within the given TIFF file, or null
   * if the given file is not a valid TIFF file.
   */
  public static Hashtable getFirstIFD(RandomAccessStream in) throws IOException
  {
    // check TIFF header
    Boolean result = checkHeader(in);
    if (result == null) return null;

    in.seek(2);
    boolean bigTiff = in.readShort() == BIG_TIFF_MAGIC_NUMBER;

    long offset = getFirstOffset(in, bigTiff);

    Hashtable ifd = getIFD(in, 0, offset, bigTiff);
    ifd.put(new Integer(BIG_TIFF), new Boolean(bigTiff));
    return ifd;
  }

  /**
   * Retrieve a given entry from the first IFD in a stream.
   *
   * @param in the stream to retrieve the entry from.
   * @param tag the tag of the entry to be retrieved.
   * @return an object representing the entry's fields.
   * @throws IOException when there is an error accessing the stream <i>in</i>.
   */
  public static TiffIFDEntry getFirstIFDEntry(RandomAccessStream in, int tag)
    throws IOException
  {
    // First lets re-position the file pointer by checking the TIFF header
    Boolean result = checkHeader(in);
    if (result == null) return null;

    in.seek(2);
    boolean bigTiff = in.readShort() == BIG_TIFF_MAGIC_NUMBER;

    // Get the offset of the first IFD
    long offset = getFirstOffset(in, bigTiff);

    // The following loosely resembles the logic of getIFD()...
    in.seek(offset);
    long numEntries = bigTiff ? in.readLong() : in.readShort() & 0xffff;

    for (int i = 0; i < numEntries; i++) {
      in.seek(offset + // The beginning of the IFD
        2 + // The width of the initial numEntries field
        (bigTiff ? BIG_TIFF_BYTES_PER_ENTRY : BYTES_PER_ENTRY) * i);

      int entryTag = in.readShort() & 0xffff;

      // Skip this tag unless it matches the one we want
      if (entryTag != tag) continue;

      // Parse the entry's "Type"
      int entryType = in.readShort() & 0xffff;

      // Parse the entry's "ValueCount"
      int valueCount =
        bigTiff ? (int) (in.readLong() & 0xffffffff) : in.readInt();
      if (valueCount < 0) {
        throw new RuntimeException("Count of '" + valueCount + "' unexpected.");
      }

      // Parse the entry's "ValueOffset"
      long valueOffset = bigTiff ? in.readLong() : in.readInt();

      return new TiffIFDEntry(entryTag, entryType, valueCount, valueOffset);
    }
    throw new UnknownTagException();
  }

  /**
   * Gets offset to the first IFD, or -1 if stream is not TIFF.
   * Assumes the stream is positioned properly (checkHeader just called).
   */
  public static long getFirstOffset(RandomAccessStream in)
    throws IOException
  {
    return getFirstOffset(in, false);
  }

  /**
   * Gets offset to the first IFD, or -1 if stream is not TIFF.
   * Assumes the stream is positioned properly (checkHeader just called).
   *
   * @param bigTiff true if this is a BigTIFF file (8 byte pointers).
   */
  public static long getFirstOffset(RandomAccessStream in, boolean bigTiff)
    throws IOException
  {
    if (bigTiff) in.skipBytes(4);
    return bigTiff ? in.readLong() : in.readInt();
  }

  /** Gets the IFD stored at the given offset. */
  public static Hashtable getIFD(RandomAccessStream in, long ifdNum,
    long offset) throws IOException
  {
    return getIFD(in, ifdNum, offset, false);
  }

  /** Gets the IFD stored at the given offset. */
  public static Hashtable getIFD(RandomAccessStream in,
    long ifdNum, long offset, boolean bigTiff) throws IOException
  {
    Hashtable ifd = new Hashtable();

    // save little-endian flag to internal LITTLE_ENDIAN tag
    ifd.put(new Integer(LITTLE_ENDIAN), new Boolean(in.isLittleEndian()));
    ifd.put(new Integer(BIG_TIFF), new Boolean(bigTiff));

    // read in directory entries for this IFD
    debug("getIFDs: seeking IFD #" + ifdNum + " at " + offset);
    in.seek(offset);
    long numEntries = bigTiff ? in.readLong() : in.readShort() & 0xffff;
    debug("getIFDs: " + numEntries + " directory entries to read");
    if (numEntries == 0 || numEntries == 1) return ifd;

    int bytesPerEntry = bigTiff ? BIG_TIFF_BYTES_PER_ENTRY : BYTES_PER_ENTRY;
    int baseOffset = bigTiff ? 8 : 2;
    int threshhold = bigTiff ? 8 : 4;

    for (int i=0; i<numEntries; i++) {
      in.seek(offset + baseOffset + bytesPerEntry * i);
      int tag = in.readShort() & 0xffff;
      int type = in.readShort() & 0xffff;
      // BigTIFF case is a slight hack
      int count = bigTiff ? (int) (in.readLong() & 0xffffffff) : in.readInt();

      debug("getIFDs: read " + getIFDTagName(tag) +
        " (type=" + getIFDTypeName(type) + "; count=" + count + ")");
      if (count < 0) return null; // invalid data
      Object value = null;

      if (type < 0 || type >= BYTES_PER_ELEMENT.length) {
        // invalid data
        return null;
      }

      if (count > threshhold / BYTES_PER_ELEMENT[type]) {
        long pointer = bigTiff ? in.readLong() :
          (long) (in.readInt() & 0xffffffffL);
        in.seek(pointer);
      }

      if (type == BYTE) {
        // 8-bit unsigned integer
        if (count == 1) value = new Short(in.readByte());
        else {
          byte[] bytes = new byte[count];
          in.readFully(bytes);
          // bytes are unsigned, so use shorts
          short[] shorts = new short[count];
          for (int j=0; j<count; j++) shorts[j] = (short) (bytes[j] & 0xff);
          value = shorts;
        }
      }
      else if (type == ASCII) {
        // 8-bit byte that contain a 7-bit ASCII code;
        // the last byte must be NUL (binary zero)
        byte[] ascii = new byte[count];
        in.read(ascii);

        // count number of null terminators
        int nullCount = 0;
        for (int j=0; j<count; j++) {
          if (ascii[j] == 0 || j == count - 1) nullCount++;
        }

        // convert character array to array of strings
        String[] strings = nullCount == 1 ? null : new String[nullCount];
        String s = null;
        int c = 0, ndx = -1;
        for (int j=0; j<count; j++) {
          if (ascii[j] == 0) {
            s = new String(ascii, ndx + 1, j - ndx - 1);
            ndx = j;
          }
          else if (j == count - 1) {
            // handle non-null-terminated strings
            s = new String(ascii, ndx + 1, j - ndx);
          }
          else s = null;
          if (strings != null && s != null) strings[c++] = s;
        }
        value = strings == null ? (Object) s : strings;
      }
      else if (type == SHORT) {
        // 16-bit (2-byte) unsigned integer
        if (count == 1) value = new Integer(in.readShort() & 0xffff);
        else {
          int[] shorts = new int[count];
          for (int j=0; j<count; j++) {
            shorts[j] = in.readShort() & 0xffff;
          }
          value = shorts;
        }
      }
      else if (type == LONG) {
        // 32-bit (4-byte) unsigned integer
        if (count == 1) value = new Long(in.readInt());
        else {
          long[] longs = new long[count];
          for (int j=0; j<count; j++) longs[j] = in.readInt();
          value = longs;
        }
      }
      else if (type == LONG8 || type == SLONG8 || type == IFD8) {
        if (count == 1) value = new Long(in.readLong());
        else {
          long[] longs = new long[count];
          for (int j=0; j<count; j++) longs[j] = in.readLong();
          value = longs;
        }
      }
      else if (type == RATIONAL || type == SRATIONAL) {
        // Two LONGs: the first represents the numerator of a fraction;
        // the second, the denominator
        // Two SLONG's: the first represents the numerator of a fraction,
        // the second the denominator
        if (count == 1) value = new TiffRational(in.readInt(), in.readInt());
        else {
          TiffRational[] rationals = new TiffRational[count];
          for (int j=0; j<count; j++) {
            rationals[j] = new TiffRational(in.readInt(), in.readInt());
          }
          value = rationals;
        }
      }
      else if (type == SBYTE || type == UNDEFINED) {
        // SBYTE: An 8-bit signed (twos-complement) integer
        // UNDEFINED: An 8-bit byte that may contain anything,
        // depending on the definition of the field
        if (count == 1) value = new Byte(in.readByte());
        else {
          byte[] sbytes = new byte[count];
          in.read(sbytes);
          value = sbytes;
        }
      }
      else if (type == SSHORT) {
        // A 16-bit (2-byte) signed (twos-complement) integer
        if (count == 1) value = new Short(in.readShort());
        else {
          short[] sshorts = new short[count];
          for (int j=0; j<count; j++) sshorts[j] = in.readShort();
          value = sshorts;
        }
      }
      else if (type == SLONG) {
        // A 32-bit (4-byte) signed (twos-complement) integer
        if (count == 1) value = new Integer(in.readInt());
        else {
          int[] slongs = new int[count];
          for (int j=0; j<count; j++) slongs[j] = in.readInt();
          value = slongs;
        }
      }
      else if (type == FLOAT) {
        // Single precision (4-byte) IEEE format
        if (count == 1) value = new Float(in.readFloat());
        else {
          float[] floats = new float[count];
          for (int j=0; j<count; j++) floats[j] = in.readFloat();
          value = floats;
        }
      }
      else if (type == DOUBLE) {
        // Double precision (8-byte) IEEE format
        if (count == 1) value = new Double(in.readDouble());
        else {
          double[] doubles = new double[count];
          for (int j=0; j<count; j++) {
            doubles[j] = in.readDouble();
          }
          value = doubles;
        }
      }
      if (value != null) ifd.put(new Integer(tag), value);
    }
    in.seek(offset + baseOffset + bytesPerEntry * numEntries);

    return ifd;
  }

  /** Gets the name of the IFD tag encoded by the given number. */
  public static String getIFDTagName(int tag) { return getFieldName(tag); }

  /** Gets the name of the IFD type encoded by the given number. */
  public static String getIFDTypeName(int type) { return getFieldName(type); }

  /**
   * This method uses reflection to scan the values of this class's
   * static fields, returning the first matching field's name. It is
   * probably not very efficient, and is mainly intended for debugging.
   */
  public static String getFieldName(int value) {
    Field[] fields = TiffTools.class.getFields();
    for (int i=0; i<fields.length; i++) {
      try {
        if (fields[i].getInt(null) == value) return fields[i].getName();
      }
      catch (IllegalAccessException exc) { }
      catch (IllegalArgumentException exc) { }
    }
    return "" + value;
  }

  /** Gets the given directory entry value from the specified IFD. */
  public static Object getIFDValue(Hashtable ifd, int tag) {
    return ifd.get(new Integer(tag));
  }

  /**
   * Gets the given directory entry value from the specified IFD,
   * performing some error checking.
   */
  public static Object getIFDValue(Hashtable ifd,
    int tag, boolean checkNull, Class checkClass) throws FormatException
  {
    Object value = ifd.get(new Integer(tag));
    if (checkNull && value == null) {
      throw new FormatException(
        getIFDTagName(tag) + " directory entry not found");
    }
    if (checkClass != null && value != null &&
      !checkClass.isInstance(value))
    {
      // wrap object in array of length 1, if appropriate
      Class cType = checkClass.getComponentType();
      Object array = null;
      if (cType == value.getClass()) {
        array = Array.newInstance(value.getClass(), 1);
        Array.set(array, 0, value);
      }
      if (cType == boolean.class && value instanceof Boolean) {
        array = Array.newInstance(boolean.class, 1);
        Array.setBoolean(array, 0, ((Boolean) value).booleanValue());
      }
      else if (cType == byte.class && value instanceof Byte) {
        array = Array.newInstance(byte.class, 1);
        Array.setByte(array, 0, ((Byte) value).byteValue());
      }
      else if (cType == char.class && value instanceof Character) {
        array = Array.newInstance(char.class, 1);
        Array.setChar(array, 0, ((Character) value).charValue());
      }
      else if (cType == double.class && value instanceof Double) {
        array = Array.newInstance(double.class, 1);
        Array.setDouble(array, 0, ((Double) value).doubleValue());
      }
      else if (cType == float.class && value instanceof Float) {
        array = Array.newInstance(float.class, 1);
        Array.setFloat(array, 0, ((Float) value).floatValue());
      }
      else if (cType == int.class && value instanceof Integer) {
        array = Array.newInstance(int.class, 1);
        Array.setInt(array, 0, ((Integer) value).intValue());
      }
      else if (cType == long.class && value instanceof Long) {
        array = Array.newInstance(long.class, 1);
        Array.setLong(array, 0, ((Long) value).longValue());
      }
      else if (cType == short.class && value instanceof Short) {
        array = Array.newInstance(short.class, 1);
        Array.setShort(array, 0, ((Short) value).shortValue());
      }
      if (array != null) return array;

      throw new FormatException(getIFDTagName(tag) +
        " directory entry is the wrong type (got " +
        value.getClass().getName() + ", expected " + checkClass.getName());
    }
    return value;
  }

  /**
   * Gets the given directory entry value in long format from the
   * specified IFD, performing some error checking.
   */
  public static long getIFDLongValue(Hashtable ifd, int tag,
    boolean checkNull, long defaultValue) throws FormatException
  {
    long value = defaultValue;
    Number number = (Number) getIFDValue(ifd, tag, checkNull, Number.class);
    if (number != null) value = number.longValue();
    return value;
  }

  /**
   * Gets the given directory entry value in int format from the
   * specified IFD, or -1 if the given directory does not exist.
   */
  public static int getIFDIntValue(Hashtable ifd, int tag) {
    int value = -1;
    try {
      value = getIFDIntValue(ifd, tag, false, -1);
    }
    catch (FormatException exc) { }
    return value;
  }

  /**
   * Gets the given directory entry value in int format from the
   * specified IFD, performing some error checking.
   */
  public static int getIFDIntValue(Hashtable ifd, int tag,
    boolean checkNull, int defaultValue) throws FormatException
  {
    int value = defaultValue;
    Number number = (Number) getIFDValue(ifd, tag, checkNull, Number.class);
    if (number != null) value = number.intValue();
    return value;
  }

  /**
   * Gets the given directory entry value in rational format from the
   * specified IFD, performing some error checking.
   */
  public static TiffRational getIFDRationalValue(Hashtable ifd, int tag,
    boolean checkNull) throws FormatException
  {
    return (TiffRational) getIFDValue(ifd, tag, checkNull, TiffRational.class);
  }

  /**
   * Gets the given directory entry values in long format
   * from the specified IFD, performing some error checking.
   */
  public static long[] getIFDLongArray(Hashtable ifd,
    int tag, boolean checkNull) throws FormatException
  {
    Object value = getIFDValue(ifd, tag, checkNull, null);
    long[] results = null;
    if (value instanceof long[]) results = (long[]) value;
    else if (value instanceof Number) {
      results = new long[] {((Number) value).longValue()};
    }
    else if (value instanceof Number[]) {
      Number[] numbers = (Number[]) value;
      results = new long[numbers.length];
      for (int i=0; i<results.length; i++) results[i] = numbers[i].longValue();
    }
    else if (value instanceof int[]) { // convert int[] to long[]
      int[] integers = (int[]) value;
      results = new long[integers.length];
      for (int i=0; i<integers.length; i++) results[i] = integers[i];
    }
    else if (value != null) {
      throw new FormatException(getIFDTagName(tag) +
        " directory entry is the wrong type (got " +
        value.getClass().getName() +
        ", expected Number, long[], Number[] or int[])");
    }
    return results;
  }

  /**
   * Gets the given directory entry values in int format
   * from the specified IFD, performing some error checking.
   */
  public static int[] getIFDIntArray(Hashtable ifd,
    int tag, boolean checkNull) throws FormatException
  {
    Object value = getIFDValue(ifd, tag, checkNull, null);
    int[] results = null;
    if (value instanceof int[]) results = (int[]) value;
    else if (value instanceof Number) {
      results = new int[] {((Number) value).intValue()};
    }
    else if (value instanceof Number[]) {
      Number[] numbers = (Number[]) value;
      results = new int[numbers.length];
      for (int i=0; i<results.length; i++) results[i] = numbers[i].intValue();
    }
    else if (value != null) {
      throw new FormatException(getIFDTagName(tag) +
        " directory entry is the wrong type (got " +
        value.getClass().getName() + ", expected Number, int[] or Number[])");
    }
    return results;
  }

  /**
   * Gets the given directory entry values in short format
   * from the specified IFD, performing some error checking.
   */
  public static short[] getIFDShortArray(Hashtable ifd,
    int tag, boolean checkNull) throws FormatException
  {
    Object value = getIFDValue(ifd, tag, checkNull, null);
    short[] results = null;
    if (value instanceof short[]) results = (short[]) value;
    else if (value instanceof Number) {
      results = new short[] {((Number) value).shortValue()};
    }
    else if (value instanceof Number[]) {
      Number[] numbers = (Number[]) value;
      results = new short[numbers.length];
      for (int i=0; i<results.length; i++) {
        results[i] = numbers[i].shortValue();
      }
    }
    else if (value != null) {
      throw new FormatException(getIFDTagName(tag) +
        " directory entry is the wrong type (got " +
        value.getClass().getName() +
        ", expected Number, short[] or Number[])");
    }
    return results;
  }

  /** Convenience method for obtaining the ImageDescription from an IFD. */
  public static String getComment(Hashtable ifd) {
    if (ifd == null) return null;

    // extract comment
    Object o = TiffTools.getIFDValue(ifd, TiffTools.IMAGE_DESCRIPTION);
    String comment = null;
    if (o instanceof String) comment = (String) o;
    else if (o instanceof String[]) {
      String[] s = (String[]) o;
      if (s.length > 0) comment = s[0];
    }
    else if (o != null) comment = o.toString();

    if (comment != null) {
      // sanitize line feeds
      comment = comment.replaceAll("\r\n", "\n");
      comment = comment.replaceAll("\r", "\n");
    }
    return comment;
  }

  /** Convenience method for obtaining a file's first ImageDescription. */
  public static String getComment(String id)
    throws FormatException, IOException
  {
    // read first IFD
    RandomAccessStream in = new RandomAccessStream(id);
    Hashtable ifd = TiffTools.getFirstIFD(in);
    in.close();
    return getComment(ifd);
  }

  /** Returns the width of an image tile. */
  public static long getTileWidth(Hashtable ifd) throws FormatException {
    long tileWidth = getIFDLongValue(ifd, TILE_WIDTH, false, 0);
    return tileWidth == 0 ? getImageWidth(ifd) : tileWidth;
  }

  /** Returns the length of an image tile. */
  public static long getTileLength(Hashtable ifd) throws FormatException {
    long tileLength = getIFDLongValue(ifd, TILE_LENGTH, false, 0);
    return tileLength == 0 ? getRowsPerStrip(ifd)[0] : tileLength;
  }

  /** Returns the number of image tiles per row. */
  public static long getTilesPerRow(Hashtable ifd) throws FormatException {
    long tileWidth = getTileWidth(ifd);
    long imageWidth = getImageWidth(ifd);
    long nTiles = imageWidth / tileWidth;
    if (nTiles * tileWidth < imageWidth) nTiles++;
    return nTiles;
  }

  /** Returns the number of image tiles per column. */
  public static long getTilesPerColumn(Hashtable ifd) throws FormatException {
    long tileLength = getTileLength(ifd);
    long imageLength = getImageLength(ifd);
    long nTiles = imageLength / tileLength;
    if (nTiles * tileLength < imageLength) nTiles++;
    return nTiles;
  }

  // -- Image reading methods --

  public static byte[] getTile(Hashtable ifd, RandomAccessStream in,
    int row, int col)
    throws FormatException, IOException
  {
    int samplesPerPixel = getSamplesPerPixel(ifd);
    if (getPlanarConfiguration(ifd) == 2) samplesPerPixel = 1;
    int bpp = getBytesPerSample(ifd)[0];
    int width = (int) getTileWidth(ifd);
    int height = (int) getTileLength(ifd);
    byte[] buf = new byte[width * height * samplesPerPixel * bpp];

    return getTile(ifd, in, buf, row, col);
  }

  public static byte[] getTile(Hashtable ifd, RandomAccessStream in,
    byte[] buf, int row, int col)
    throws FormatException, IOException
  {
    byte[] jpegTable =
      (byte[]) TiffTools.getIFDValue(ifd, JPEG_TABLES, false, null);

    CodecOptions options = new CodecOptions();
    options.interleaved = true;
    options.littleEndian = isLittleEndian(ifd);

    long tileWidth = getTileWidth(ifd);
    long tileLength = getTileLength(ifd);
    int samplesPerPixel = getSamplesPerPixel(ifd);
    int planarConfig = getPlanarConfiguration(ifd);
    int compression = getCompression(ifd);

    long numTileCols = getTilesPerRow(ifd);

    int pixel = getBytesPerSample(ifd)[0];
    int effectiveChannels = planarConfig == 2 ? 1 : samplesPerPixel;

    long[] stripOffsets = getStripOffsets(ifd);
    long[] stripByteCounts = getStripByteCounts(ifd);

    int tileNumber = (int) (row * numTileCols + col);
    byte[] tile = new byte[(int) stripByteCounts[tileNumber]];
    in.seek(stripOffsets[tileNumber] & 0xffffffffL);
    in.read(tile);

    int size = (int) (tileWidth * tileLength * pixel * effectiveChannels);
    options.maxBytes = size;

    if (jpegTable != null) {
      byte[] q = new byte[jpegTable.length + tile.length - 4];
      System.arraycopy(jpegTable, 0, q, 0, jpegTable.length - 2);
      System.arraycopy(tile, 2, q, jpegTable.length - 2, tile.length - 2);
      tile = uncompress(q, compression, options);
    }
    else tile = uncompress(tile, compression, options);

    undifference(tile, ifd);
    unpackBytes(buf, 0, tile, ifd);

    return buf;
  }

  /** Reads the image defined in the given IFD from the specified file. */
  public static byte[][] getSamples(Hashtable ifd, RandomAccessStream in)
    throws FormatException, IOException
  {
    int samplesPerPixel = getSamplesPerPixel(ifd);
    int bpp = getBytesPerSample(ifd)[0];
    long width = getImageWidth(ifd);
    long length = getImageLength(ifd);
    byte[] b = new byte[(int) (width * length * samplesPerPixel * bpp)];

    getSamples(ifd, in, b);
    byte[][] samples = new byte[samplesPerPixel][(int) (width * length * bpp)];
    for (int i=0; i<samplesPerPixel; i++) {
      System.arraycopy(b, (int) (i*width*length*bpp), samples[i], 0,
        samples[i].length);
    }
    b = null;
    return samples;
  }

  public static byte[] getSamples(Hashtable ifd, RandomAccessStream in,
    byte[] buf) throws FormatException, IOException
  {
    long width = getImageWidth(ifd);
    long length = getImageLength(ifd);
    return getSamples(ifd, in, buf, 0, 0, width, length);
  }

  public static byte[] getSamples(Hashtable ifd, RandomAccessStream in,
    byte[] buf, int x, int y, long width, long height)
    throws FormatException, IOException
  {
    debug("parsing IFD entries");

    // get internal non-IFD entries
    boolean littleEndian = isLittleEndian(ifd);
    in.order(littleEndian);

    // get relevant IFD entries
    int samplesPerPixel = getSamplesPerPixel(ifd);
    long tileWidth = getTileWidth(ifd);
    long tileLength = getTileLength(ifd);
    long numTileRows = getTilesPerColumn(ifd);
    long numTileCols = getTilesPerRow(ifd);

    int planarConfig = getPlanarConfiguration(ifd);

    printIFD(ifd);

    if (width * height > Integer.MAX_VALUE) {
      throw new FormatException("Sorry, ImageWidth x ImageLength > " +
        Integer.MAX_VALUE + " is not supported (" +
        width + " x " + height + ")");
    }
    int numSamples = (int) (width * height);

    // read in image strips
    debug("reading image data (samplesPerPixel=" +
      samplesPerPixel + "; numSamples=" + numSamples + ")");

    int pixel = getBytesPerSample(ifd)[0];
    int effectiveChannels = planarConfig == 2 ? 1 : samplesPerPixel;
    long nrows = numTileRows;
    if (planarConfig == 2) numTileRows *= samplesPerPixel;

    Rectangle imageBounds = new Rectangle(x, y, (int) width,
      (int) (height * (samplesPerPixel / effectiveChannels)));

    int endX = (int) width + x;
    int endY = (int) height + y;

    for (int row=0; row<numTileRows; row++) {
      for (int col=0; col<numTileCols; col++) {
        Rectangle tileBounds = new Rectangle(col * (int) tileWidth,
          (int) (row * tileLength), (int) tileWidth,
          (int) tileLength);

        if (!imageBounds.intersects(tileBounds)) continue;

        if (planarConfig == 2) {
          tileBounds.y = (int) ((row % nrows) * tileLength);
        }

        byte[] tile = getTile(ifd, in, row, col);

        // adjust tile bounds, if necessary

        int tileX = (int) Math.max(tileBounds.x, x);
        int tileY = (int) Math.max(tileBounds.y, y);
        int realX = tileX % (int) tileWidth;
        int realY = tileY % (int) tileLength;

        int twidth = (int) Math.min(endX - tileX, tileWidth - realX);
        int theight = (int) Math.min(endY - tileY, tileLength - realY);

        // copy appropriate portion of the tile to the output buffer

        int rowLen = pixel * (int) tileWidth;
        int copy = pixel * twidth;
        int tileSize = (int) (tileWidth * tileLength * pixel);
        int planeSize = (int) (width * height * pixel);
        int outputRowLen = (int) (pixel * width);

        realX *= pixel;
        realY *= rowLen;

        for (int q=0; q<effectiveChannels; q++) {
          int src = (int) (q * tileSize) + realX + realY;
          int dest = (int) (q * planeSize) + pixel * (tileX - x) +
            outputRowLen * (tileY - y);
          if (planarConfig == 2) dest += (planeSize * (row / nrows));
          for (int tileRow=0; tileRow<theight; tileRow++) {
            System.arraycopy(tile, src, buf, dest, copy);
            src += rowLen;
            dest += outputRowLen;
          }
        }
      }
    }

    return buf;
  }

  /**
   * Extracts pixel information from the given byte array according to the
   * bits per sample, photometric interpretation, and the specified byte
   * ordering.
   * No error checking is performed.
   * This method is tailored specifically for planar (separated) images.
   */
  public static void planarUnpack(byte[] samples, int startIndex,
    byte[] bytes, Hashtable ifd)
    throws FormatException
  {
    BitBuffer bb = new BitBuffer(bytes);

    int numBytes = getBytesPerSample(ifd)[0];
    int realBytes = numBytes;
    if (numBytes == 3) numBytes++;

    int bitsPerSample = getBitsPerSample(ifd)[0];
    boolean littleEndian = isLittleEndian(ifd);
    int photoInterp = getPhotometricInterpretation(ifd);

    for (int j=0; j<bytes.length / realBytes; j++) {
      int value = bb.getBits(bitsPerSample);

      if (photoInterp == WHITE_IS_ZERO) {
        value = (int) (Math.pow(2, bitsPerSample) - 1 - value);
      }
      else if (photoInterp == CMYK) {
        value = Integer.MAX_VALUE - value;
      }

      if (numBytes*(startIndex + j) < samples.length) {
        DataTools.unpackBytes(value, samples, numBytes*(startIndex + j),
          numBytes, littleEndian);
      }
    }
  }

  /**
   * Extracts pixel information from the given byte array according to the
   * bits per sample, photometric interpretation and color map IFD directory
   * entry values, and the specified byte ordering.
   * No error checking is performed.
   */
  public static void unpackBytes(byte[] samples, int startIndex,
    byte[] bytes, Hashtable ifd)
    throws FormatException
  {
    if (getPlanarConfiguration(ifd) == 2) {
      planarUnpack(samples, startIndex, bytes, ifd);
      return;
    }

    int compression = getCompression(ifd);
    int photoInterp = getPhotometricInterpretation(ifd);
    if (compression == JPEG) photoInterp = RGB;

    int[] bitsPerSample = getBitsPerSample(ifd);
    int nChannels = bitsPerSample.length;
    int nSamples = samples.length / nChannels;

    int totalBits = 0;
    for (int i=0; i<nChannels; i++) totalBits += bitsPerSample[i];
    int sampleCount = 8 * bytes.length / totalBits;
    if (photoInterp == Y_CB_CR) sampleCount *= 3;

    debug("unpacking " + sampleCount + " samples (startIndex=" + startIndex +
      "; totalBits=" + totalBits + "; numBytes=" + bytes.length + ")");

    long imageWidth = getImageWidth(ifd);

    int bps0 = bitsPerSample[0];
    int numBytes = getBytesPerSample(ifd)[0];

    boolean noDiv8 = bps0 % 8 != 0;
    boolean bps8 = bps0 == 8;

    int row = startIndex / (int) imageWidth;
    int col = 0;

    int cw = 0, ch = 0;

    boolean littleEndian = isLittleEndian(ifd);

    int[] reference = getIFDIntArray(ifd, REFERENCE_BLACK_WHITE, false);
    int[] subsampling = getIFDIntArray(ifd, Y_CB_CR_SUB_SAMPLING, false);
    TiffRational[] coefficients =
      (TiffRational[]) getIFDValue(ifd, Y_CB_CR_COEFFICIENTS);

    int count = 0;

    BitBuffer bb = new BitBuffer(bytes);

    for (int j=0; j<sampleCount; j++) {
      for (int i=0; i<nChannels; i++) {
        int index = numBytes * (j * nChannels + i);
        int ndx = startIndex + j;
        if (ndx >= nSamples) {
          break;
        }
        int outputIndex = i * nSamples + ndx * numBytes;

        if (noDiv8) {
          // bits per sample is not a multiple of 8

          short s = 0;
          if ((i == 0 && photoInterp == RGB_PALETTE) ||
            (photoInterp != CFA_ARRAY && photoInterp != RGB_PALETTE))
          {
            s = (short) (bb.getBits(bps0) & 0xffff);
            if ((ndx % imageWidth) == imageWidth - 1 && bps0 < 8) {
              bb.skipBits((imageWidth * bps0 * sampleCount) % 8);
            }
          }

          if (photoInterp == WHITE_IS_ZERO || photoInterp == CMYK) {
            // invert colors
            s = (short) (Math.pow(2, bitsPerSample[0]) - 1 - s);
          }

          if (outputIndex + numBytes <= samples.length) {
            DataTools.unpackBytes(s, samples, outputIndex, numBytes,
              littleEndian);
          }
        }
        else if (bps8) {
          // special case handles 8-bit data more quickly

          if (outputIndex >= samples.length) break;

          if (photoInterp != Y_CB_CR) {
            samples[outputIndex] = (byte) (bytes[index] & 0xff);
          }

          if (photoInterp == WHITE_IS_ZERO) { // invert color value
            samples[outputIndex] = (byte) (255 - samples[outputIndex]);
          }
          else if (photoInterp == CMYK) {
            samples[outputIndex] =
              (byte) (Integer.MAX_VALUE - samples[outputIndex]);
          }
          else if (photoInterp == Y_CB_CR) {
            if (i == bitsPerSample.length - 1) {
              float lumaRed = 0.299f;
              float lumaGreen = 0.587f;
              float lumaBlue = 0.114f;
              if (coefficients != null) {
                lumaRed = coefficients[0].floatValue();
                lumaGreen = coefficients[1].floatValue();
                lumaBlue = coefficients[2].floatValue();
              }

              int subX = subsampling == null ? 2 : subsampling[0];
              int subY = subsampling == null ? 2 : subsampling[1];

              int block = subX * subY;
              int lumaIndex = j + (2 * (j / block));
              int chromaIndex = (j / block) * (block + 2) + block;

              if (chromaIndex + 1 >= bytes.length) break;

              int tile = ndx / block;
              int pixel = ndx % block;
              int nTiles = (int) (imageWidth / subX);
              long r = subY * (tile / nTiles) + (pixel / subX);
              long c = subX * (tile % nTiles) + (pixel % subX);

              int idx = (int) (r * imageWidth + c);

              if (idx < nSamples) {
                int y = (bytes[lumaIndex] & 0xff) - reference[0];
                int cb = (bytes[chromaIndex] & 0xff) - reference[2];
                int cr = (bytes[chromaIndex + 1] & 0xff) - reference[4];

                int red = (int) (cr * (2 - 2 * lumaRed) + y);
                int blue = (int) (cb * (2 - 2 * lumaBlue) + y);
                int green = (int)
                  ((y - lumaBlue * blue - lumaRed * red) / lumaGreen);

                samples[idx] = (byte) red;
                samples[nSamples + idx] = (byte) green;
                samples[2*nSamples + idx] = (byte) blue;
              }
            }
          }
        }  // End if (bps8)
        else {
          int offset = numBytes + index < bytes.length ?
            index : bytes.length - numBytes;
          long v = DataTools.bytesToLong(bytes, offset, numBytes, littleEndian);

          if (photoInterp == WHITE_IS_ZERO) { // invert color value
            long max = (long) Math.pow(2, numBytes * 8) - 1;
            v = max - v;
          }
          else if (photoInterp == CMYK) {
            v = Integer.MAX_VALUE - v;
          }
          if (ndx*numBytes >= nSamples) break;
          DataTools.unpackBytes(v, samples, i*nSamples + ndx*numBytes,
            numBytes, littleEndian);
        } // end else
      }
    }
  }

  // -- Decompression methods --

  /** Returns true if the given decompression scheme is supported. */
  public static boolean isSupportedDecompression(int decompression) {
    return decompression == UNCOMPRESSED || decompression == LZW ||
      decompression == JPEG || decompression == ALT_JPEG ||
      decompression == JPEG_2000 || decompression == PACK_BITS ||
      decompression == PROPRIETARY_DEFLATE || decompression == DEFLATE ||
      decompression == NIKON || decompression == LURAWAVE;
  }

  /** Decodes a strip of data compressed with the given compression scheme. */
  public static byte[] uncompress(byte[] input, int compression,
    CodecOptions options)
    throws FormatException, IOException
  {
    if (compression < 0) compression += 65536;

    if (!isSupportedDecompression(compression)) {
      String compressionName = getCodecName(compression);
      String message = null;
      if (compressionName != null) {
        message =
          "Sorry, " + compressionName + " compression mode is not supported";
      }
      else message = "Unknown Compression type (" + compression + ")";
      throw new FormatException(message);
    }

    Codec codec = null;

    if (compression == UNCOMPRESSED) return input;
    else if (compression == LZW) codec = new LZWCodec();
    else if (compression == JPEG || compression == ALT_JPEG) {
      codec = new JPEGCodec();
    }
    else if (compression == JPEG_2000) codec = new JPEG2000Codec();
    else if (compression == PACK_BITS) codec = new PackbitsCodec();
    else if (compression == PROPRIETARY_DEFLATE || compression == DEFLATE) {
      codec = new ZlibCodec();
    }
    else if (compression == NIKON) codec = new NikonCodec();
    else if (compression == LURAWAVE) codec = new LuraWaveCodec();
    if (codec != null) return codec.decompress(input, options);
    throw new FormatException("Unhandled compression (" + compression + ")");
  }

  /** Undoes in-place differencing according to the given predictor value. */
  public static void undifference(byte[] input, Hashtable ifd)
    throws FormatException
  {
    int predictor = getIFDIntValue(ifd, PREDICTOR, false, 1);
    if (predictor == 2) {
      debug("reversing horizontal differencing");
      int[] bitsPerSample = getBitsPerSample(ifd);
      int len = bitsPerSample.length;
      long width = getImageWidth(ifd);
      boolean little = isLittleEndian(ifd);
      int planarConfig = getPlanarConfiguration(ifd);

      if (planarConfig == 2 || bitsPerSample[len - 1] == 0) len = 1;
      if (bitsPerSample[0] <= 8) {
        for (int b=0; b<input.length; b++) {
          if (b / len % width == 0) continue;
          input[b] += input[b - len];
        }
      }
      else if (bitsPerSample[0] <= 16) {
        short[] s = (short[]) DataTools.makeDataArray(input, 2, false, little);
        for (int b=0; b<s.length; b++) {
          if (b / len % width == 0) continue;
          s[b] += s[b - len];
        }
        for (int i=0; i<s.length; i++) {
          DataTools.unpackShort(s[i], input, i*2, little);
        }
      }
    }
    else if (predictor != 1) {
      throw new FormatException("Unknown Predictor (" + predictor + ")");
    }
  }

  // --------------------------- Writing TIFF files ---------------------------

  // -- IFD population methods --

  /** Adds a directory entry to an IFD. */
  public static void putIFDValue(Hashtable ifd, int tag, Object value) {
    ifd.put(new Integer(tag), value);
  }

  /** Adds a directory entry of type BYTE to an IFD. */
  public static void putIFDValue(Hashtable ifd, int tag, short value) {
    putIFDValue(ifd, tag, new Short(value));
  }

  /** Adds a directory entry of type SHORT to an IFD. */
  public static void putIFDValue(Hashtable ifd, int tag, int value) {
    putIFDValue(ifd, tag, new Integer(value));
  }

  /** Adds a directory entry of type LONG to an IFD. */
  public static void putIFDValue(Hashtable ifd, int tag, long value) {
    putIFDValue(ifd, tag, new Long(value));
  }

  // -- IFD writing methods --

  /**
   * Writes the given IFD value to the given output object.
   * @param ifdOut output object for writing IFD stream
   * @param extraBuf buffer to which "extra" IFD information should be written
   * @param extraOut data output wrapper for extraBuf (passed for efficiency)
   * @param offset global offset to use for IFD offset values
   * @param tag IFD tag to write
   * @param value IFD value to write
   */
  public static void writeIFDValue(DataOutput ifdOut,
    ByteArrayOutputStream extraBuf, DataOutputStream extraOut, long offset,
    int tag, Object value, boolean bigTiff, boolean littleEndian)
    throws FormatException, IOException
  {
    // convert singleton objects into arrays, for simplicity
    if (value instanceof Short) {
      value = new short[] {((Short) value).shortValue()};
    }
    else if (value instanceof Integer) {
      value = new int[] {((Integer) value).intValue()};
    }
    else if (value instanceof Long) {
      value = new long[] {((Long) value).longValue()};
    }
    else if (value instanceof TiffRational) {
      value = new TiffRational[] {(TiffRational) value};
    }
    else if (value instanceof Float) {
      value = new float[] {((Float) value).floatValue()};
    }
    else if (value instanceof Double) {
      value = new double[] {((Double) value).doubleValue()};
    }

    int dataLength = bigTiff ? 8 : 4;

    // write directory entry to output buffers
    DataTools.writeShort(ifdOut, tag, littleEndian); // tag
    if (value instanceof short[]) { // BYTE
      short[] q = (short[]) value;
      DataTools.writeShort(ifdOut, BYTE, littleEndian); // type
      if (bigTiff) DataTools.writeLong(ifdOut, q.length, littleEndian);
      else DataTools.writeInt(ifdOut, q.length, littleEndian);
      if (q.length <= dataLength) {
        for (int i=0; i<q.length; i++) ifdOut.writeByte(q[i]); // value(s)
        for (int i=q.length; i<dataLength; i++) ifdOut.writeByte(0); // padding
      }
      else {
        if (bigTiff) {
          DataTools.writeLong(ifdOut, offset + extraBuf.size(), littleEndian);
        }
        else {
          // offset
          DataTools.writeInt(ifdOut, (int) (offset + extraBuf.size()),
            littleEndian);
        }
        for (int i=0; i<q.length; i++) extraOut.writeByte(q[i]); // values
      }
    }
    else if (value instanceof String) { // ASCII
      char[] q = ((String) value).toCharArray();
      DataTools.writeShort(ifdOut, ASCII, littleEndian); // type
      if (bigTiff) DataTools.writeLong(ifdOut, q.length + 1, littleEndian);
      else DataTools.writeInt(ifdOut, q.length + 1, littleEndian);
      if (q.length < dataLength) {
        for (int i=0; i<q.length; i++) ifdOut.writeByte(q[i]); // value(s)
        for (int i=q.length; i<dataLength; i++) ifdOut.writeByte(0); // padding
      }
      else {
        if (bigTiff) {
          DataTools.writeLong(ifdOut, offset + extraBuf.size(), littleEndian);
        }
        else {
          // offset
          DataTools.writeInt(ifdOut, (int) (offset + extraBuf.size()),
            littleEndian);
        }
        for (int i=0; i<q.length; i++) extraOut.writeByte(q[i]); // values
        extraOut.writeByte(0); // concluding NULL byte
      }
    }
    else if (value instanceof int[]) { // SHORT
      int[] q = (int[]) value;
      DataTools.writeShort(ifdOut, SHORT, littleEndian); // type
      if (bigTiff) DataTools.writeLong(ifdOut, q.length, littleEndian);
      else DataTools.writeInt(ifdOut, q.length, littleEndian);
      if (q.length <= dataLength / 2) {
        for (int i=0; i<q.length; i++) {
          DataTools.writeShort(ifdOut, q[i], littleEndian); // value(s)
        }
        for (int i=q.length; i<dataLength / 2; i++) {
          DataTools.writeShort(ifdOut, 0, littleEndian); // padding
        }
      }
      else {
        if (bigTiff) {
          DataTools.writeLong(ifdOut, offset + extraBuf.size(), littleEndian);
        }
        else {
          // offset
          DataTools.writeInt(ifdOut, (int) (offset + extraBuf.size()),
            littleEndian);
        }
        for (int i=0; i<q.length; i++) {
          DataTools.writeShort(extraOut, q[i], littleEndian); // values
        }
      }
    }
    else if (value instanceof long[]) { // LONG
      long[] q = (long[]) value;

      if (bigTiff) {
        DataTools.writeShort(ifdOut, LONG8, littleEndian);
        DataTools.writeLong(ifdOut, q.length, littleEndian);

        if (q.length <= dataLength / 4) {
          for (int i=0; i<q.length; i++) {
            DataTools.writeLong(ifdOut, q[0], littleEndian);
          }
          for (int i=q.length; i<dataLength / 4; i++) {
            DataTools.writeLong(ifdOut, 0, littleEndian);
          }
        }
        else {
          DataTools.writeLong(ifdOut, offset + extraBuf.size(), littleEndian);
          for (int i=0; i<q.length; i++) {
            DataTools.writeLong(extraOut, q[i], littleEndian);
          }
        }
      }
      else {
        DataTools.writeShort(ifdOut, LONG, littleEndian);
        DataTools.writeInt(ifdOut, q.length, littleEndian);
        if (q.length <= dataLength / 4) {
          for (int i=0; i<q.length; i++) {
            DataTools.writeInt(ifdOut, (int) q[0], littleEndian);
          }
          for (int i=q.length; i<dataLength / 4; i++) {
            DataTools.writeInt(ifdOut, 0, littleEndian); // padding
          }
        }
        else {
          DataTools.writeInt(ifdOut, (int) (offset + extraBuf.size()),
            littleEndian);
          for (int i=0; i<q.length; i++) {
            DataTools.writeInt(extraOut, (int) q[i], littleEndian);
          }
        }
      }
    }
    else if (value instanceof TiffRational[]) { // RATIONAL
      TiffRational[] q = (TiffRational[]) value;
      DataTools.writeShort(ifdOut, RATIONAL, littleEndian); // type
      if (bigTiff) DataTools.writeLong(ifdOut, q.length, littleEndian);
      else DataTools.writeInt(ifdOut, q.length, littleEndian);
      if (bigTiff && q.length == 1) {
        DataTools.writeInt(ifdOut, (int) q[0].getNumerator(), littleEndian);
        DataTools.writeInt(ifdOut, (int) q[0].getDenominator(), littleEndian);
      }
      else {
        if (bigTiff) {
          DataTools.writeLong(ifdOut, offset + extraBuf.size(), littleEndian);
        }
        else {
          // offset
          DataTools.writeInt(ifdOut, (int) (offset + extraBuf.size()),
            littleEndian);
        }
        for (int i=0; i<q.length; i++) {
          DataTools.writeInt(extraOut, (int) q[i].getNumerator(), littleEndian);
          DataTools.writeInt(extraOut, (int) q[i].getDenominator(),
            littleEndian);
        }
      }
    }
    else if (value instanceof float[]) { // FLOAT
      float[] q = (float[]) value;
      DataTools.writeShort(ifdOut, FLOAT, littleEndian); // type
      if (bigTiff) DataTools.writeLong(ifdOut, q.length, littleEndian);
      else DataTools.writeInt(ifdOut, q.length, littleEndian);
      if (q.length <= dataLength / 4) {
        for (int i=0; i<q.length; i++) {
          DataTools.writeFloat(ifdOut, q[0], littleEndian); // value
        }
        for (int i=q.length; i<dataLength / 4; i++) {
          DataTools.writeInt(ifdOut, 0, littleEndian); // padding
        }
      }
      else {
        if (bigTiff) {
          DataTools.writeLong(ifdOut, offset + extraBuf.size(), littleEndian);
        }
        else {
          DataTools.writeInt(ifdOut, (int) (offset + extraBuf.size()),
            littleEndian);
        }
        for (int i=0; i<q.length; i++) {
          DataTools.writeFloat(extraOut, q[i], littleEndian); // values
        }
      }
    }
    else if (value instanceof double[]) { // DOUBLE
      double[] q = (double[]) value;
      DataTools.writeShort(ifdOut, DOUBLE, littleEndian); // type
      if (bigTiff) DataTools.writeLong(ifdOut, q.length, littleEndian);
      else DataTools.writeInt(ifdOut, q.length, littleEndian);
      if (bigTiff) {
        DataTools.writeLong(ifdOut, offset + extraBuf.size(), littleEndian);
      }
      else {
        DataTools.writeInt(ifdOut, (int) (offset + extraBuf.size()),
          littleEndian);
      }
      for (int i=0; i<q.length; i++) {
        DataTools.writeDouble(extraOut, q[i], littleEndian); // values
      }
    }
    else {
      throw new FormatException("Unknown IFD value type (" +
        value.getClass().getName() + "): " + value);
    }
  }

  /**
   * Surgically overwrites an existing IFD value with the given one. This
   * method requires that the IFD directory entry already exist. It
   * intelligently updates the count field of the entry to match the new
   * length. If the new length is longer than the old length, it appends the
   * new data to the end of the file and updates the offset field; if not, or
   * if the old data is already at the end of the file, it overwrites the old
   * data in place.
   */
  public static void overwriteIFDValue(String file,
    int ifd, int tag, Object value) throws FormatException, IOException
  {
    debug("overwriteIFDValue (ifd=" + ifd + "; tag=" + tag + "; value=" +
      value + ")");
    byte[] header = new byte[4];

    RandomAccessStream raf = new RandomAccessStream(file);
    raf.seek(0);
    raf.readFully(header);
    if (!isValidHeader(header)) {
      throw new FormatException("Invalid TIFF header");
    }
    boolean little = header[0] == LITTLE && header[1] == LITTLE; // II
    boolean bigTiff = header[2] == 0x2b || header[3] == 0x2b;
    long offset = bigTiff ? 8 : 4; // offset to the IFD
    long num = 0; // number of directory entries

    int baseOffset = bigTiff ? 8 : 2;
    int bytesPerEntry = bigTiff ? BIG_TIFF_BYTES_PER_ENTRY : BYTES_PER_ENTRY;

    raf.seek(offset);

    // skip to the correct IFD
    for (int i=0; i<=ifd; i++) {
      offset = bigTiff ? DataTools.read8SignedBytes(raf, little) :
        DataTools.read4UnsignedBytes(raf, little);
      if (offset <= 0) {
        throw new FormatException("No such IFD (" + ifd + " of " + i + ")");
      }
      raf.seek(offset);
      num = bigTiff ? DataTools.read8SignedBytes(raf, little) :
        DataTools.read2UnsignedBytes(raf, little);
      if (i < ifd) raf.seek(offset + baseOffset + bytesPerEntry * num);
    }

    // search directory entries for proper tag
    for (int i=0; i<num; i++) {
      int oldTag = DataTools.read2UnsignedBytes(raf, little);
      int oldType = DataTools.read2UnsignedBytes(raf, little);
      int oldCount =
        bigTiff ? (int) (DataTools.read8SignedBytes(raf, little) & 0xffffffff) :
        DataTools.read4SignedBytes(raf, little);
      long oldOffset = bigTiff ? DataTools.read8SignedBytes(raf, little) :
        DataTools.read4SignedBytes(raf, little);
      if (oldTag == tag) {
        // write new value to buffers
        ByteArrayOutputStream ifdBuf = new ByteArrayOutputStream(bytesPerEntry);
        DataOutputStream ifdOut = new DataOutputStream(ifdBuf);
        ByteArrayOutputStream extraBuf = new ByteArrayOutputStream();
        DataOutputStream extraOut = new DataOutputStream(extraBuf);
        writeIFDValue(ifdOut, extraBuf, extraOut, oldOffset, tag, value,
          bigTiff, little);
        byte[] bytes = ifdBuf.toByteArray();
        byte[] extra = extraBuf.toByteArray();

        // extract new directory entry parameters
        int newTag = DataTools.bytesToInt(bytes, 0, 2, little);
        int newType = DataTools.bytesToInt(bytes, 2, 2, little);
        int newCount;
        long newOffset;
        if (bigTiff) {
          newCount =
            (int) (DataTools.bytesToLong(bytes, 4, little) & 0xffffffff);
          newOffset = DataTools.bytesToLong(bytes, 12, little);
        }
        else {
          newCount = DataTools.bytesToInt(bytes, 4, little);
          newOffset = DataTools.bytesToInt(bytes, 8, little);
        }
        boolean terminate = false;
        debug("overwriteIFDValue:\n\told: (tag=" + oldTag + "; type=" +
          oldType + "; count=" + oldCount + "; offset=" + oldOffset +
          ");\n\tnew: (tag=" + newTag + "; type=" + newType + "; count=" +
          newCount + "; offset=" + newOffset + ")");

        // determine the best way to overwrite the old entry
        if (extra.length == 0) {
          // new entry is inline; if old entry wasn't, old data is orphaned
          // do not override new offset value since data is inline
          debug("overwriteIFDValue: new entry is inline");
        }
        else if (oldOffset +
          oldCount * BYTES_PER_ELEMENT[oldType] == raf.length())
        {
          // old entry was already at EOF; overwrite it
          newOffset = oldOffset;
          terminate = true;
          debug("overwriteIFDValue: old entry is at EOF");
        }
        else if (newCount <= oldCount) {
          // new entry is as small or smaller than old entry; overwrite it
          newOffset = oldOffset;
          debug("overwriteIFDValue: new entry is <= old entry");
        }
        else {
          // old entry was elsewhere; append to EOF, orphaning old entry
          newOffset = raf.length();
          debug("overwriteIFDValue: old entry will be orphaned");
        }

        long filePointer = raf.getFilePointer();
        raf.close();

        RandomAccessOutputStream out = new RandomAccessOutputStream(file);

        // overwrite old entry
        out.seek(filePointer - (bigTiff ? 18 : 10)); // jump back
        DataTools.writeShort(out, newType, little);
        if (bigTiff) DataTools.writeLong(out, newCount, little);
        else DataTools.writeInt(out, newCount, little);
        if (bigTiff) DataTools.writeLong(out, newOffset, little);
        else DataTools.writeInt(out, (int) newOffset, little);
        if (extra.length > 0) {
          out.seek(newOffset);
          out.write(extra);
        }
        return;
      }
    }

    throw new FormatException("Tag not found (" + getIFDTagName(tag) + ")");
  }

  /** Convenience method for overwriting a file's first ImageDescription. */
  public static void overwriteComment(String id, Object value)
    throws FormatException, IOException
  {
    overwriteIFDValue(id, 0, TiffTools.IMAGE_DESCRIPTION, value);
  }

  // -- Image writing methods --

  /**
   * Writes the given field to the specified output stream using the given
   * byte offset and IFD, in big-endian format.
   *
   * @param img The field to write
   * @param ifd Hashtable representing the TIFF IFD; can be null
   * @param out The output stream to which the TIFF data should be written
   * @param offset The value to use for specifying byte offsets
   * @param last Whether this image is the final IFD entry of the TIFF data
   * @param bigTiff Whether this image should be written as BigTIFF
   * @return total number of bytes written
   */
  public static long writeImage(BufferedImage img, Hashtable ifd,
    RandomAccessOutputStream out, long offset, boolean last, boolean bigTiff)
    throws FormatException, IOException
  {
    if (img == null) throw new FormatException("Image is null");
    debug("writeImage (offset=" + offset + "; last=" + last + ")");

    boolean little = isLittleEndian(ifd);

    byte[][] values = AWTImageTools.getPixelBytes(img, little);

    int width = img.getWidth();
    int height = img.getHeight();

    int bytesPerPixel = values[0].length / (width * height);

    // populate required IFD directory entries (except strip information)
    if (ifd == null) ifd = new Hashtable();
    putIFDValue(ifd, IMAGE_WIDTH, width);
    putIFDValue(ifd, IMAGE_LENGTH, height);
    if (getIFDValue(ifd, BITS_PER_SAMPLE) == null) {
      int bps = 8 * bytesPerPixel;
      int[] bpsArray = new int[values.length];
      Arrays.fill(bpsArray, bps);
      putIFDValue(ifd, BITS_PER_SAMPLE, bpsArray);
    }
    if (img.getRaster().getTransferType() == DataBuffer.TYPE_FLOAT) {
      putIFDValue(ifd, SAMPLE_FORMAT, 3);
    }
    if (getIFDValue(ifd, COMPRESSION) == null) {
      putIFDValue(ifd, COMPRESSION, UNCOMPRESSED);
    }
    if (getIFDValue(ifd, PHOTOMETRIC_INTERPRETATION) == null) {
      putIFDValue(ifd, PHOTOMETRIC_INTERPRETATION, values.length == 1 ? 1 : 2);
    }
    if (getIFDValue(ifd, SAMPLES_PER_PIXEL) == null) {
      putIFDValue(ifd, SAMPLES_PER_PIXEL, values.length);
    }
    if (getIFDValue(ifd, X_RESOLUTION) == null) {
      putIFDValue(ifd, X_RESOLUTION, new TiffRational(1, 1)); // no unit
    }
    if (getIFDValue(ifd, Y_RESOLUTION) == null) {
      putIFDValue(ifd, Y_RESOLUTION, new TiffRational(1, 1)); // no unit
    }
    if (getIFDValue(ifd, RESOLUTION_UNIT) == null) {
      putIFDValue(ifd, RESOLUTION_UNIT, 1); // no unit
    }
    if (getIFDValue(ifd, SOFTWARE) == null) {
      putIFDValue(ifd, SOFTWARE, "LOCI Bio-Formats");
    }
    if (getIFDValue(ifd, IMAGE_DESCRIPTION) == null) {
      putIFDValue(ifd, IMAGE_DESCRIPTION, "");
    }

    // create pixel output buffers
    int compression = getIFDIntValue(ifd, COMPRESSION, false, UNCOMPRESSED);
    boolean fullImageCompression = false;
    if (compression == JPEG_2000 || compression == JPEG) {
      fullImageCompression = true;
    }
    int pixels = fullImageCompression ? width * height : width;
    int stripSize = Math.max(8192, pixels * bytesPerPixel * values.length);
    int rowsPerStrip = stripSize / (width * bytesPerPixel * values.length);
    int stripsPerImage = (height + rowsPerStrip - 1) / rowsPerStrip;
    int[] bps = (int[]) getIFDValue(ifd, BITS_PER_SAMPLE, true, int[].class);
    ByteArrayOutputStream[] stripBuf =
      new ByteArrayOutputStream[stripsPerImage];
    DataOutputStream[] stripOut = new DataOutputStream[stripsPerImage];
    for (int i=0; i<stripsPerImage; i++) {
      stripBuf[i] = new ByteArrayOutputStream(stripSize);
      stripOut[i] = new DataOutputStream(stripBuf[i]);
    }

    // write pixel strips to output buffers
    for (int y=0; y<height; y++) {
      int strip = y / rowsPerStrip;
      for (int x=0; x<width; x++) {
        int ndx = y * width * bytesPerPixel + x * bytesPerPixel;
        for (int c=0; c<values.length; c++) {
          for (int n=0; n<bps[c]/8; n++) {
            stripOut[strip].writeByte(values[c][ndx + n]);
          }
        }
      }
    }

    // compress strips according to given differencing and compression schemes
    int planarConfig = getPlanarConfiguration(ifd);
    int predictor = getIFDIntValue(ifd, PREDICTOR, false, 1);

    byte[][] strips = new byte[stripsPerImage][];
    for (int i=0; i<stripsPerImage; i++) {
      strips[i] = stripBuf[i].toByteArray();
      difference(strips[i], bps, width, planarConfig, predictor);
      strips[i] = compress(strips[i], ifd);
    }

    // record strip byte counts and offsets
    long[] stripByteCounts = new long[stripsPerImage];
    long[] stripOffsets = new long[stripsPerImage];
    putIFDValue(ifd, STRIP_OFFSETS, stripOffsets);
    putIFDValue(ifd, ROWS_PER_STRIP, rowsPerStrip);
    putIFDValue(ifd, STRIP_BYTE_COUNTS, stripByteCounts);

    Object[] keys = ifd.keySet().toArray();
    Arrays.sort(keys); // sort IFD tags in ascending order

    int keyCount = keys.length;
    if (ifd.containsKey(new Integer(LITTLE_ENDIAN))) keyCount--;
    if (ifd.containsKey(new Integer(BIG_TIFF))) keyCount--;

    int bytesPerEntry = bigTiff ? BIG_TIFF_BYTES_PER_ENTRY : BYTES_PER_ENTRY;
    int ifdBytes = (bigTiff ? 16 : 6) + bytesPerEntry * keyCount;

    long pixelBytes = 0;
    for (int i=0; i<stripsPerImage; i++) {
      stripByteCounts[i] = strips[i].length;
      stripOffsets[i] = pixelBytes + offset + ifdBytes;
      pixelBytes += stripByteCounts[i];
    }

    // create IFD output buffers
    ByteArrayOutputStream ifdBuf = new ByteArrayOutputStream(ifdBytes);
    DataOutputStream ifdOut = new DataOutputStream(ifdBuf);
    ByteArrayOutputStream extraBuf = new ByteArrayOutputStream();
    DataOutputStream extraOut = new DataOutputStream(extraBuf);

    offset += ifdBytes + pixelBytes;

    // write IFD to output buffers

    // number of directory entries
    if (bigTiff) DataTools.writeLong(ifdOut, keyCount, little);
    else DataTools.writeShort(ifdOut, keyCount, little);
    for (int k=0; k<keys.length; k++) {
      Object key = keys[k];
      if (!(key instanceof Integer)) {
        throw new FormatException("Malformed IFD tag (" + key + ")");
      }
      if (((Integer) key).intValue() == LITTLE_ENDIAN) continue;
      if (((Integer) key).intValue() == BIG_TIFF) continue;
      Object value = ifd.get(key);
      String sk = getIFDTagName(((Integer) key).intValue());
      String sv = value instanceof int[] ?
        ("int[" + ((int[]) value).length + "]") : value.toString();
      debug("writeImage: writing " + sk + " (value=" + sv + ")");
      writeIFDValue(ifdOut, extraBuf, extraOut, offset,
        ((Integer) key).intValue(), value, bigTiff, little);
    }
    // offset to next IFD
    if (bigTiff) {
      DataTools.writeLong(ifdOut, last ? 0 : offset + extraBuf.size(), little);
    }
    else {
      DataTools.writeInt(ifdOut, last ? 0 : (int) (offset + extraBuf.size()),
        little);
    }

    // flush buffers to output stream
    byte[] ifdArray = ifdBuf.toByteArray();
    byte[] extraArray = extraBuf.toByteArray();
    long numBytes = ifdArray.length + extraArray.length;
    out.write(ifdArray);
    for (int i=0; i<strips.length; i++) {
      out.write(strips[i]);
      numBytes += strips[i].length;
    }
    out.write(extraArray);
    return numBytes;
  }

  // -- Tag retrieval methods --

  public static boolean isTiled(Hashtable ifd) throws FormatException {
    Object offsets = ifd.get(new Integer(STRIP_OFFSETS));
    Object tileWidth = ifd.get(new Integer(TILE_WIDTH));
    return offsets == null || tileWidth != null;
  }

  /**
   * Retrieves the image's width (TIFF tag ImageWidth) from a given TIFF IFD.
   * @param ifd a TIFF IFD hashtable.
   * @return the image's width.
   * @throws FormatException if there is a problem parsing the IFD metadata.
   */
  public static long getImageWidth(Hashtable ifd) throws FormatException {
    long width = getIFDLongValue(ifd, IMAGE_WIDTH, true, 0);
    if (width > Integer.MAX_VALUE) {
      throw new FormatException("Sorry, ImageWidth > " + Integer.MAX_VALUE +
        " is not supported.");
    }
    return width;
  }

  /**
   * Retrieves the image's length (TIFF tag ImageLength) from a given TIFF IFD.
   * @param ifd a TIFF IFD hashtable.
   * @return the image's length.
   * @throws FormatException if there is a problem parsing the IFD metadata.
   */
  public static long getImageLength(Hashtable ifd) throws FormatException {
    long length = getIFDLongValue(ifd, IMAGE_LENGTH, true, 0);
    if (length > Integer.MAX_VALUE) {
      throw new FormatException("Sorry, ImageLength > " + Integer.MAX_VALUE +
        " is not supported.");
    }
    return length;
  }

  /**
   * Retrieves the image's bits per sample (TIFF tag BitsPerSample) from a given
   * TIFF IFD.
   * @param ifd a TIFF IFD hashtable.
   * @return the image's bits per sample. The length of the array is equal to
   *   the number of samples per pixel.
   * @throws FormatException if there is a problem parsing the IFD metadata.
   * @see #getSamplesPerPixel(Hashtable)
   */
  public static int[] getBitsPerSample(Hashtable ifd) throws FormatException {
    int[] bitsPerSample = getIFDIntArray(ifd, BITS_PER_SAMPLE, false);
    if (bitsPerSample == null) bitsPerSample = new int[] {1};

    int samplesPerPixel = getSamplesPerPixel(ifd);
    if (bitsPerSample.length < samplesPerPixel) {
      throw new FormatException("BitsPerSample length (" +
        bitsPerSample.length + ") does not match SamplesPerPixel (" +
        samplesPerPixel + ")");
    }
    int nSamples = (int) Math.min(bitsPerSample.length, samplesPerPixel);
    for (int i=0; i<nSamples; i++) {
      if (bitsPerSample[i] < 1) {
        throw new FormatException("Illegal BitsPerSample (" +
          bitsPerSample[i] + ")");
      }
    }

    return bitsPerSample;
  }

  /**
   * Retrieves the image's pixel type based on the BitsPerSample tag.
   * @param ifd a TIFF IFD hashtable.
   * @return the pixel type.  This is one of:
   *  <li>FormatTools.INT8</li>
   *  <li>FormatTools.UINT8</li>
   *  <li>FormatTools.INT16</li>
   *  <li>FormatTools.UINT16</li>
   *  <li>FormatTools.INT32</li>
   *  <li>FormatTools.UINT32</li>
   *  <li>FormatTools.FLOAT</li>
   *  <li>FormatTools.DOUBLE</li>
   * @throws FormatException if there is a problem parsing the IFD metadata.
   * @see #getBitsPerSample(Hashtable)
   */
  public static int getPixelType(Hashtable ifd) throws FormatException {
    int bps = getBitsPerSample(ifd)[0];
    int bitFormat = getIFDIntValue(ifd, SAMPLE_FORMAT);

    while (bps % 8 != 0) bps++;
    if (bps == 24) bps = 32;

    if (bitFormat == 3) return FormatTools.FLOAT;
    switch (bps) {
      case 16:
        return bitFormat == 2 ? FormatTools.INT16 : FormatTools.UINT16;
      case 32:
        return bitFormat == 2 ? FormatTools.INT32 : FormatTools.UINT32;
      default:
        return bitFormat == 2 ? FormatTools.INT8 : FormatTools.UINT8;
    }
  }

  /**
   * Retrieves the image's bytes per sample (derived from tag BitsPerSample)
   * from a given TIFF IFD.
   * @param ifd a TIFF IFD hashtable.
   * @return the image's bytes per sample.  The length of the array is equal to
   *   the number of samples per pixel.
   * @throws FormatException if there is a problem parsing the IFD metadata.
   * @see #getSamplesPerPixel(Hashtable)
   * @see #getBitsPerSample(Hashtable)
   */
  public static int[] getBytesPerSample(Hashtable ifd) throws FormatException {
    int[] bitsPerSample = getBitsPerSample(ifd);
    int[] bps = new int[bitsPerSample.length];
    for (int i=0; i<bitsPerSample.length; i++) {
      bps[i] = bitsPerSample[i];
      while ((bps[i] % 8) != 0) bps[i]++;
      bps[i] /= 8;
      if (bps[i] == 0) bps[i] = 1;
    }
    return bps;
  }

  /**
   * Retrieves the number of samples per pixel for the image (TIFF tag
   * SamplesPerPixel) from a given TIFF IFD.
   * @param ifd a TIFF IFD hashtable.
   * @return the number of samples per pixel.
   * @throws FormatException if there is a problem parsing the IFD metadata.
   */
  public static int getSamplesPerPixel(Hashtable ifd) throws FormatException {
    return getIFDIntValue(ifd, SAMPLES_PER_PIXEL, false, 1);
  }

  /**
   * Retrieves the image's compression type (TIFF tag Compression) from a
   * given TIFF IFD.
   * @param ifd a TIFF IFD hashtable.
   * @return the image's compression type. As of TIFF 6.0 this is one of:
   * <ul>
   *  <li>Uncompressed (1)</li>
   *  <li>CCITT 1D (2)</li>
   *  <li>Group 3 Fax (3)</li>
   *  <li>Group 4 Fax (4)</li>
   *  <li>LZW (5)</li>
   *  <li>JPEG (6)</li>
   *  <li>PackBits (32773)</li>
   * </ul>
   * @throws FormatException if there is a problem parsing the IFD metadata.
   */
  public static int getCompression(Hashtable ifd) throws FormatException {
    return getIFDIntValue(ifd, COMPRESSION, false, UNCOMPRESSED);
  }

  /**
   * Retrieves the image's photometric interpretation (TIFF tag
   * PhotometricInterpretation) from a given TIFF IFD.
   * @param ifd a TIFF IFD hashtable.
   * @return the image's photometric interpretation. As of TIFF 6.0 this is one
   * of:
   * <ul>
   *  <li>WhiteIsZero (0)</li>
   *  <li>BlackIsZero (1)</li>
   *  <li>RGB (2)</li>
   *  <li>RGB Palette (3)</li>
   *  <li>Transparency mask (4)</li>
   *  <li>CMYK (5)</li>
   *  <li>YbCbCr (6)</li>
   *  <li>CIELab (8)</li>
   * </ul>
   *
   * @throws FormatException if there is a problem parsing the IFD metadata.
   */
  public static int getPhotometricInterpretation(Hashtable ifd)
    throws FormatException
  {
    int photoInterp = getIFDIntValue(ifd, PHOTOMETRIC_INTERPRETATION, true, 0);
    if (photoInterp == TRANSPARENCY_MASK) {
      throw new FormatException(
        "Sorry, Transparency Mask PhotometricInterpretation is not supported");
    }
    else if (photoInterp == CIE_LAB) {
      throw new FormatException(
        "Sorry, CIELAB PhotometricInterpretation is not supported");
    }
    else if (photoInterp != WHITE_IS_ZERO &&
      photoInterp != BLACK_IS_ZERO && photoInterp != RGB &&
      photoInterp != RGB_PALETTE && photoInterp != CMYK &&
      photoInterp != Y_CB_CR && photoInterp != CFA_ARRAY)
    {
      throw new FormatException("Unknown PhotometricInterpretation (" +
        photoInterp + ")");
    }
    return photoInterp;
  }

  /**
   * Retrieves the image's planar configuration (TIFF tag PlanarConfiguration)
   * from a given TIFF IFD.
   * @param ifd a TIFF IFD hashtable.
   * @return the image's planar configuration.  As of TIFF 6.0 this is one of:
   * <ul>
   *  <li>Chunky (1)</li>
   *  <li>Planar (2)</li>
   * </ul>
   *
   * @throws FormatException if there is a problem parsing the IFD metadata.
   */
  public static int getPlanarConfiguration(Hashtable ifd) throws FormatException
  {
    int planarConfig = getIFDIntValue(ifd, PLANAR_CONFIGURATION, false, 1);
    if (planarConfig != 1 && planarConfig != 2) {
      throw new FormatException("Sorry, PlanarConfiguration (" + planarConfig +
        ") not supported.");
    }
    return planarConfig;
  }

  /**
   * Retrieves the strip offsets for the image (TIFF tag StripOffsets) from a
   * given TIFF IFD.
   * @param ifd a TIFF IFD hashtable.
   * @return the strip offsets for the image. The length of the array is equal
   *   to the number of strips per image. <i>StripsPerImage =
   *   floor ((ImageLength + RowsPerStrip - 1) / RowsPerStrip)</i>.
   * @throws FormatException if there is a problem parsing the IFD metadata.
   * @see #getStripByteCounts(Hashtable)
   * @see #getRowsPerStrip(Hashtable)
   */
  public static long[] getStripOffsets(Hashtable ifd) throws FormatException {
    int tag = isTiled(ifd) ? TILE_OFFSETS : STRIP_OFFSETS;
    long[] offsets = getIFDLongArray(ifd, tag, false);
    if (isTiled(ifd) && offsets == null) {
      offsets = getIFDLongArray(ifd, STRIP_OFFSETS, false);
    }

    if (isTiled(ifd)) return offsets;
    long rowsPerStrip = getRowsPerStrip(ifd)[0];
    long numStrips = (getImageLength(ifd) + rowsPerStrip - 1) / rowsPerStrip;
    if (getPlanarConfiguration(ifd) == 2) numStrips *= getSamplesPerPixel(ifd);
    if (offsets.length < numStrips) {
      throw new FormatException("StripOffsets length (" + offsets.length +
        ") does not match expected " + "number of strips (" + numStrips + ")");
    }
    return offsets;
  }

  /**
   * Retrieves strip byte counts for the image (TIFF tag StripByteCounts) from
   * a given TIFF IFD.
   * @param ifd a TIFF IFD hashtable.
   * @return the byte counts for each strip. The length of the array is equal
   *   to the number of strips per image. <i>StripsPerImage =
   *   floor((ImageLength + RowsPerStrip - 1) / RowsPerStrip)</i>.
   * @throws FormatException if there is a problem parsing the IFD metadata.
   * @see #getStripOffsets(Hashtable)
   */
  public static long[] getStripByteCounts(Hashtable ifd) throws FormatException
  {
    int tag = isTiled(ifd) ? TILE_BYTE_COUNTS : STRIP_BYTE_COUNTS;
    long[] byteCounts = getIFDLongArray(ifd, tag, false);
    if (isTiled(ifd) && byteCounts == null) {
      byteCounts = getIFDLongArray(ifd, STRIP_BYTE_COUNTS, false);
    }
    if (byteCounts == null) {
      // technically speaking, this shouldn't happen (since TIFF writers are
      // required to write the StripByteCounts tag), but we'll support it
      // anyway

      // don't rely on RowsPerStrip, since it's likely that if the file doesn't
      // have the StripByteCounts tag, it also won't have the RowsPerStrip tag
      long[] offsets = getStripOffsets(ifd);
      int bytesPerSample = getBytesPerSample(ifd)[0];
      long imageWidth = getImageWidth(ifd);
      long imageLength = getImageLength(ifd);
      byteCounts = new long[offsets.length];
      int samples = getSamplesPerPixel(ifd);
      long imageSize = imageWidth * imageLength * bytesPerSample *
        (getPlanarConfiguration(ifd) == 2 ? 1 : samples);
      long count = imageSize / byteCounts.length;
      Arrays.fill(byteCounts, count);
    }

    long[] counts = new long[byteCounts.length];

    if (getCompression(ifd) == LZW) {
      for (int i=0; i<byteCounts.length; i++) {
        counts[i] = byteCounts[i] * 2;
      }
    }
    else System.arraycopy(byteCounts, 0, counts, 0, counts.length);

    if (isTiled(ifd)) return counts;

    long rowsPerStrip = getRowsPerStrip(ifd)[0];
    long numStrips = (getImageLength(ifd) + rowsPerStrip - 1) / rowsPerStrip;
    if (getPlanarConfiguration(ifd) == 2) numStrips *= getSamplesPerPixel(ifd);

    if (counts.length < numStrips) {
      throw new FormatException("StripByteCounts length (" + counts.length +
        ") does not match expected " + "number of strips (" + numStrips + ")");
    }

    return counts;
  }

  /**
   * Retrieves the number of rows per strip for image (TIFF tag RowsPerStrip)
   * from a given TIFF IFD.
   * @param ifd a TIFF IFD hashtable.
   * @return the number of rows per strip.
   * @throws FormatException if there is a problem parsing the IFD metadata.
   */
  public static long[] getRowsPerStrip(Hashtable ifd) throws FormatException {
    if (isTiled(ifd)) {
      return new long[] {getImageLength(ifd)};
    }
    long[] rowsPerStrip = getIFDLongArray(ifd, ROWS_PER_STRIP, false);
    if (rowsPerStrip == null) {
      // create a fake RowsPerStrip entry if one is not present
      return new long[] {getImageLength(ifd)};
    }

    // rowsPerStrip should never be more than the total number of rows
    long imageLength = getImageLength(ifd);
    for (int i=0; i<rowsPerStrip.length; i++) {
      rowsPerStrip[i] = (long) Math.min(rowsPerStrip[i], imageLength);
    }

    long rows = rowsPerStrip[0];
    for (int i=1; i<rowsPerStrip.length; i++) {
      if (rows != rowsPerStrip[i]) {
        throw new FormatException(
          "Sorry, non-uniform RowsPerStrip is not supported");
      }
    }

    return rowsPerStrip;
  }

  // -- Compression methods --

  /** Returns true if the given compression scheme is supported. */
  public static boolean isSupportedCompression(int compression) {
    return compression == UNCOMPRESSED || compression == LZW ||
      compression == JPEG || compression == JPEG_2000;
  }

  /** Encodes a strip of data with the given compression scheme. */
  public static byte[] compress(byte[] input, Hashtable ifd)
    throws FormatException, IOException
  {
    int compression = getIFDIntValue(ifd, COMPRESSION, false, UNCOMPRESSED);

    if (!isSupportedCompression(compression)) {
      String compressionName = getCodecName(compression);
      if (compressionName != null) {
        throw new FormatException("Sorry, " + compressionName +
          " compression mode is not supported");
      }
      else {
        throw new FormatException(
          "Unknown Compression type (" + compression + ")");
      }
    }

    CodecOptions options = new CodecOptions();
    options.width = (int) getImageWidth(ifd);
    options.height = (int) getImageLength(ifd);
    options.bitsPerSample = getBitsPerSample(ifd)[0];
    options.channels = getSamplesPerPixel(ifd);
    options.littleEndian = isLittleEndian(ifd);
    options.interleaved = true;
    options.signed = false;

    if (compression == UNCOMPRESSED) return input;
    else if (compression == LZW) {
      return new LZWCodec().compress(input, options);
    }
    else if (compression == JPEG) {
      return new JPEGCodec().compress(input, options);
    }
    else if (compression == JPEG_2000) {
      return new JPEG2000Codec().compress(input, options);
    }
    throw new FormatException("Unhandled compression (" + compression + ")");
  }

  /** Performs in-place differencing according to the given predictor value. */
  public static void difference(byte[] input, int[] bitsPerSample,
    long width, int planarConfig, int predictor) throws FormatException
  {
    if (predictor == 2) {
      debug("performing horizontal differencing");
      for (int b=input.length-1; b>=0; b--) {
        if (b / bitsPerSample.length % width == 0) continue;
        input[b] -= input[b - bitsPerSample.length];
      }
    }
    else if (predictor != 1) {
      throw new FormatException("Unknown Predictor (" + predictor + ")");
    }
  }

  // -- Debugging --

  /** Prints a debugging message with current time. */
  public static void debug(String message) {
    if (FormatHandler.debug && FormatHandler.debugLevel >= 3) {
      LogTools.println(System.currentTimeMillis() + ": " + message);
    }
  }

  /** Prints the contents of an IFD. */
  public static void printIFD(Hashtable ifd) {
    StringBuffer sb = new StringBuffer();
    sb.append("IFD directory entry values:");

    Integer[] tags = (Integer[]) ifd.keySet().toArray(new Integer[0]);
    for (int entry=0; entry<tags.length; entry++) {
      sb.append("\n\t");
      sb.append(getIFDTagName(tags[entry].intValue()));
      sb.append("=");
      Object value = ifd.get(tags[entry]);
      if ((value instanceof Boolean) || (value instanceof Number) ||
        (value instanceof String))
      {
        sb.append(value);
      }
      else {
        // this is an array of primitive types, Strings, or TiffRationals
        ReflectedUniverse r = new ReflectedUniverse();
        r.setVar("value", value);
        try {
          int nElements = ((Integer) r.exec("value.length")).intValue();
          for (int i=0; i<nElements; i++) {
            r.setVar("index", i);
            sb.append(r.exec("value[index]"));
            if (i < nElements - 1) sb.append(",");
          }
        }
        catch (ReflectException re) { }
      }
    }
    debug(sb.toString());
  }

  /** Returns the name of the given codec. */
  public static String getCodecName(int codec) {
    switch (codec) {
      case UNCOMPRESSED: return "Uncompressed";
      case CCITT_1D: return "CCITT Group 3 1-Dimensional Modified Huffman";
      case GROUP_3_FAX: return "CCITT T.4 bi-level encoding (Group 3 Fax)";
      case GROUP_4_FAX: return "CCITT T.6 bi-level encoding (Group 4 Fax)";
      case LZW: return "LZW";
      case JPEG:
      case ALT_JPEG:
        return "JPEG";
      case PACK_BITS: return "PackBits";
      case DEFLATE:
      case PROPRIETARY_DEFLATE:
        return "Deflate (Zlib)";
      case THUNDERSCAN: return "Thunderscan";
      case JPEG_2000: return "JPEG-2000";
      case NIKON: return "Nikon";
      case LURAWAVE: return "LuraWave";
    }
    return null;
  }

}
