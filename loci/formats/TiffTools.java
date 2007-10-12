//
// TiffTools.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

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

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.*;
import java.util.*;
import loci.formats.codec.*;

/**
 * A utility class for manipulating TIFF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/TiffTools.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/TiffTools.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Eric Kjellman egkjellman at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 * @author Chris Allan callan at blackcat.ca
 */
public final class TiffTools {

  // -- Constants --

  private static final boolean DEBUG = false;

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
  public static final int NIKON = 34713;
  public static final int LURAWAVE = -1;

  // photometric interpretation types
  public static final int WHITE_IS_ZERO = 0;
  public static final int BLACK_IS_ZERO = 1;
  public static final int RGB = 2;
  public static final int RGB_PALETTE = 3;
  public static final int TRANSPARENCY_MASK = 4;
  public static final int CMYK = 5;
  public static final int Y_CB_CR = 6;
  public static final int CIE_LAB = 8;
  public static final int CFA_ARRAY = -32733;

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
   * Checks the TIFF header.
   * @return true if little-endian,
   *         false if big-endian,
   *         or null if not a TIFF.
   */
  public static Boolean checkHeader(byte[] block) {
    if (block.length < 4) return null;

    // byte order must be II or MM
    boolean littleEndian = block[0] == LITTLE && block[1] == LITTLE; // II
    boolean bigEndian = block[0] == BIG && block[1] == BIG; // MM
    if (!littleEndian && !bigEndian) return null;

    // check magic number (42)
    short magic = DataTools.bytesToShort(block, 2, littleEndian);
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
      offset = bigTiff ? in.readLong() : in.readInt();
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

    long offset = getFirstOffset(in);

    return getIFD(in, 0, offset);
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

    // Get the offset of the first IFD
    long offset = getFirstOffset(in);

    // The following loosely resembles the logic of getIFD()...
    in.seek(offset);
    int numEntries = in.readShort() & 0xffff;

    for (int i = 0; i < numEntries; i++) {
      in.seek(offset + // The beginning of the IFD
        2 + // The width of the initial numEntries field
        BYTES_PER_ENTRY * i);

      int entryTag = in.readShort() & 0xffff;

      // Skip this tag unless it matches the one we want
      if (entryTag != tag) continue;

      // Parse the entry's "Type"
      int entryType = in.readShort() & 0xffff;

      // Parse the entry's "ValueCount"
      int valueCount = in.readInt();
      if (valueCount < 0) {
        throw new RuntimeException("Count of '" + valueCount + "' unexpected.");
      }

      // Parse the entry's "ValueOffset"
      int valueOffset = in.readInt();

      return new TiffIFDEntry(entryTag, entryType, valueCount, valueOffset);
    }
    throw new UnknownTagException();
  }

  /**
   * Checks the TIFF header.
   * @return true if little-endian,
   *         false if big-endian,
   *         or null if not a TIFF.
   */
  public static Boolean checkHeader(RandomAccessStream in) throws IOException {
    if (DEBUG) debug("getIFDs: reading IFD entries");

    // start at the beginning of the file
    in.seek(0);

    byte[] header = new byte[4];
    in.readFully(header);
    Boolean b = checkHeader(header);
    if (b != null) in.order(b.booleanValue());
    return b;
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
    if (DEBUG) {
      debug("getIFDs: seeking IFD #" + ifdNum + " at " + offset);
    }
    in.seek(offset);
    long numEntries = bigTiff ? in.readLong() : in.readShort() & 0xffff;
    if (DEBUG) debug("getIFDs: " + numEntries + " directory entries to read");
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

      if (DEBUG) {
        debug("getIFDs: read " + getIFDTagName(tag) +
          " (type=" + getIFDTypeName(type) + "; count=" + count + ")");
      }
      if (count < 0) return null; // invalid data
      Object value = null;

      if (type == BYTE) {
        // 8-bit unsigned integer
        if (count > threshhold) {
          long pointer = bigTiff ? in.readLong() : in.readInt();
          in.seek(pointer);
        }
        if (count == 1) value = new Short(in.readByte());
        else {
          short[] bytes = new short[count];
          for (int j=0; j<count; j++) {
            bytes[j] = in.readByte();
            if (bytes[j] < 0) bytes[j] += 255;
          }
          value = bytes;
        }
      }
      else if (type == ASCII) {
        // 8-bit byte that contain a 7-bit ASCII code;
        // the last byte must be NUL (binary zero)
        byte[] ascii = new byte[count];
        if (count > threshhold) {
          long pointer = bigTiff ? in.readLong() : in.readInt();
          in.seek(pointer);
        }
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
        if (count > threshhold / 2) {
          long pointer = bigTiff ? in.readLong() : in.readInt();
          in.seek(pointer);
        }
        if (count == 1) value = new Integer(in.readShort());
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
        if (count > threshhold / 4) {
          long pointer = bigTiff ? in.readLong() : in.readInt();
          in.seek(pointer);
        }
        if (count == 1) value = new Long(in.readInt());
        else {
          long[] longs = new long[count];
          for (int j=0; j<count; j++) longs[j] = in.readInt();
          value = longs;
        }
      }
      else if (type == LONG8 || type == SLONG8 || type == IFD8) {
        if (count > threshhold / 8) {
          long pointer = bigTiff ? in.readLong() : in.readInt();
          in.seek(pointer);
        }
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
        long pointer = bigTiff ? in.readLong() : in.readInt();
        if (count > threshhold / 8) in.seek(pointer);
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
        if (count > threshhold) {
          long pointer = bigTiff ? in.readLong() : in.readInt();
          in.seek(pointer);
        }
        if (count == 1) value = new Byte(in.readByte());
        else {
          byte[] sbytes = new byte[count];
          in.readFully(sbytes);
          value = sbytes;
        }
      }
      else if (type == SSHORT) {
        // A 16-bit (2-byte) signed (twos-complement) integer
        if (count > threshhold / 2) {
          long pointer = bigTiff ? in.readLong() : in.readInt();
          in.seek(pointer);
        }
        if (count == 1) value = new Short(in.readShort());
        else {
          short[] sshorts = new short[count];
          for (int j=0; j<count; j++) sshorts[j] = in.readShort();
          value = sshorts;
        }
      }
      else if (type == SLONG) {
        // A 32-bit (4-byte) signed (twos-complement) integer
        if (count > threshhold / 4) {
          long pointer = bigTiff ? in.readLong() : in.readInt();
          in.seek(pointer);
        }
        if (count == 1) value = new Integer(in.readInt());
        else {
          int[] slongs = new int[count];
          for (int j=0; j<count; j++) slongs[j] = in.readInt();
          value = slongs;
        }
      }
      else if (type == FLOAT) {
        // Single precision (4-byte) IEEE format
        if (count > threshhold / 4) {
          long pointer = bigTiff ? in.readLong() : in.readInt();
          in.seek(pointer);
        }
        if (count == 1) value = new Float(in.readFloat());
        else {
          float[] floats = new float[count];
          for (int j=0; j<count; j++) floats[j] = in.readFloat();
          value = floats;
        }
      }
      else if (type == DOUBLE) {
        // Double precision (8-byte) IEEE format
        long pointer = bigTiff ? in.readLong() : in.readInt();
        in.seek(pointer);
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

  /** Convenience method for obtaining a file's first ImageDescription. */
  public static String getComment(String id)
    throws FormatException, IOException
  {
    // read first IFD
    RandomAccessStream in = new RandomAccessStream(id);
    Hashtable ifd = TiffTools.getFirstIFD(in);
    in.close();

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

  // -- Image reading methods --

  /** Reads the image defined in the given IFD from the specified file. */
  public static byte[][] getSamples(Hashtable ifd, RandomAccessStream in)
    throws FormatException, IOException
  {
    int samplesPerPixel = getSamplesPerPixel(ifd);
    int photoInterp = getPhotometricInterpretation(ifd);
    int bpp = getBitsPerSample(ifd)[0];
    while ((bpp % 8) != 0) bpp++;
    bpp /= 8;
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
    if (DEBUG) debug("parsing IFD entries");

    // get internal non-IFD entries
    boolean littleEndian = isLittleEndian(ifd);
    in.order(littleEndian);

    // get relevant IFD entries
    long imageWidth = getImageWidth(ifd);
    long imageLength = getImageLength(ifd);
    int[] bitsPerSample = getBitsPerSample(ifd);
    int samplesPerPixel = getSamplesPerPixel(ifd);
    int compression = getCompression(ifd);
    int photoInterp = getPhotometricInterpretation(ifd);
    long[] stripOffsets = getStripOffsets(ifd);
    long[] stripByteCounts = getStripByteCounts(ifd);
    long[] rowsPerStripArray = getRowsPerStrip(ifd);

    boolean fakeByteCounts = stripByteCounts == null;
    boolean fakeRPS = rowsPerStripArray == null;
    boolean isTiled = stripOffsets == null;

    long[] maxes = getIFDLongArray(ifd, MAX_SAMPLE_VALUE, false);
    long maxValue = maxes == null ? 0 : maxes[0];

    if (isTiled) {
      stripOffsets = getIFDLongArray(ifd, TILE_OFFSETS, true);
      stripByteCounts = getIFDLongArray(ifd, TILE_BYTE_COUNTS, true);
      rowsPerStripArray = new long[] {imageLength};
    }
    else if (fakeByteCounts) {
      // technically speaking, this shouldn't happen (since TIFF writers are
      // required to write the StripByteCounts tag), but we'll support it
      // anyway

      // don't rely on RowsPerStrip, since it's likely that if the file doesn't
      // have the StripByteCounts tag, it also won't have the RowsPerStrip tag
      stripByteCounts = new long[stripOffsets.length];
      if (stripByteCounts.length == 1) {
        stripByteCounts[0] = imageWidth * imageLength * (bitsPerSample[0] / 8);
      }
      else {
        stripByteCounts[0] = stripOffsets[0];
        for (int i=1; i<stripByteCounts.length; i++) {
          stripByteCounts[i] = stripOffsets[i] - stripByteCounts[i-1];
        }
      }
    }

    boolean lastBitsZero = bitsPerSample[bitsPerSample.length - 1] == 0;

    if (fakeRPS && !isTiled) {
      // create a false rowsPerStripArray if one is not present
      // it's sort of a cheap hack, but here's how it's done:
      // RowsPerStrip = stripByteCounts / (imageLength * bitsPerSample)
      // since stripByteCounts and bitsPerSample are arrays, we have to
      // iterate through each item

      rowsPerStripArray = new long[bitsPerSample.length];

      long temp = stripByteCounts[0];
      stripByteCounts = new long[bitsPerSample.length];
      for (int i=0; i<stripByteCounts.length; i++) stripByteCounts[i] = temp;
      temp = bitsPerSample[0];
      if (temp == 0) temp = 8;
      bitsPerSample = new int[bitsPerSample.length];
      for (int i=0; i<bitsPerSample.length; i++) bitsPerSample[i] = (int) temp;
      temp = stripOffsets[0];
      /*
      stripOffsets = new long[bitsPerSample.length];
      for (int i=0; i<bitsPerSample.length; i++) {
        stripOffsets[i] = i == 0 ? temp :
          stripOffsets[i - 1] + stripByteCounts[i];
      }
      */

      // we have two files that reverse the endianness for BitsPerSample,
      // StripOffsets, and StripByteCounts

      if (bitsPerSample[0] > 64) {
        byte[] bps = new byte[2];
        byte[] stripOffs = new byte[4];
        byte[] byteCounts = new byte[4];
        if (littleEndian) {
          bps[0] = (byte) (bitsPerSample[0] & 0xff);
          bps[1] = (byte) ((bitsPerSample[0] >>> 8) & 0xff);

          int ndx = stripOffsets.length - 1;

          stripOffs[0] = (byte) (stripOffsets[ndx] & 0xff);
          stripOffs[1] = (byte) ((stripOffsets[ndx] >>> 8) & 0xff);
          stripOffs[2] = (byte) ((stripOffsets[ndx] >>> 16) & 0xff);
          stripOffs[3] = (byte) ((stripOffsets[ndx] >>> 24) & 0xff);

          ndx = stripByteCounts.length - 1;

          byteCounts[0] = (byte) (stripByteCounts[ndx] & 0xff);
          byteCounts[1] = (byte) ((stripByteCounts[ndx] >>> 8) & 0xff);
          byteCounts[2] = (byte) ((stripByteCounts[ndx] >>> 16) & 0xff);
          byteCounts[3] = (byte) ((stripByteCounts[ndx] >>> 24) & 0xff);
        }
        else {
          bps[1] = (byte) ((bitsPerSample[0] >>> 16) & 0xff);
          bps[0] = (byte) ((bitsPerSample[0] >>> 24) & 0xff);

          stripOffs[3] = (byte) (stripOffsets[0] & 0xff);
          stripOffs[2] = (byte) ((stripOffsets[0] >>> 8) & 0xff);
          stripOffs[1] = (byte) ((stripOffsets[0] >>> 16) & 0xff);
          stripOffs[0] = (byte) ((stripOffsets[0] >>> 24) & 0xff);

          byteCounts[3] = (byte) (stripByteCounts[0] & 0xff);
          byteCounts[2] = (byte) ((stripByteCounts[0] >>> 8) & 0xff);
          byteCounts[1] = (byte) ((stripByteCounts[0] >>> 16) & 0xff);
          byteCounts[0] = (byte) ((stripByteCounts[0] >>> 24) & 0xff);
        }

        bitsPerSample[0] = DataTools.bytesToInt(bps, !littleEndian);
        stripOffsets[0] = DataTools.bytesToInt(stripOffs, !littleEndian);
        stripByteCounts[0] = DataTools.bytesToInt(byteCounts, !littleEndian);
      }

      if (rowsPerStripArray.length == 1 && stripByteCounts[0] !=
        (imageWidth * imageLength * (bitsPerSample[0] / 8)) &&
        compression == UNCOMPRESSED)
      {
        for (int i=0; i<stripByteCounts.length; i++) {
          stripByteCounts[i] =
            imageWidth * imageLength * (bitsPerSample[i] / 8);
          stripOffsets[0] = (int) (in.length() - stripByteCounts[0] -
            48 * imageWidth);
          if (i != 0) {
            stripOffsets[i] = stripOffsets[i - 1] + stripByteCounts[i];
          }

          in.seek((int) stripOffsets[i]);
          in.read(buf, (int) (i*imageWidth), (int) imageWidth);
          boolean isZero = true;
          for (int j=0; j<imageWidth; j++) {
            if (buf[(int) (i*imageWidth + j)] != 0) {
              isZero = false;
              break;
            }
          }

          while (isZero) {
            stripOffsets[i] -= imageWidth;
            in.seek((int) stripOffsets[i]);
            in.read(buf, (int) (i*imageWidth), (int) imageWidth);
            for (int j=0; j<imageWidth; j++) {
              if (buf[(int) (i*imageWidth + j)] != 0) {
                isZero = false;
                stripOffsets[i] -= (stripByteCounts[i] - imageWidth);
                break;
              }
            }
          }
        }
      }

      for (int i=0; i<bitsPerSample.length; i++) {
        // case 1: we're still within bitsPerSample array bounds
        if (i < bitsPerSample.length) {
          if (i == samplesPerPixel) {
            bitsPerSample[i] = 0;
            lastBitsZero = true;
          }

          // remember that the universe collapses when we divide by 0
          if (bitsPerSample[i] != 0) {
            rowsPerStripArray[i] = (long) stripByteCounts[i] /
              (imageWidth * (bitsPerSample[i] / 8));
          }
          else if (bitsPerSample[i] == 0 && i > 0) {
            rowsPerStripArray[i] = (long) stripByteCounts[i] /
              (imageWidth * (bitsPerSample[i - 1] / 8));
            bitsPerSample[i] = bitsPerSample[i - 1];
          }
          else {
            throw new FormatException("BitsPerSample is 0");
          }
        }
        // case 2: we're outside bitsPerSample array bounds
        else if (i >= bitsPerSample.length) {
          rowsPerStripArray[i] = (long) stripByteCounts[i] /
            (imageWidth * (bitsPerSample[bitsPerSample.length - 1] / 8));
        }
      }

      //samplesPerPixel = stripOffsets.length;
    }

    if (lastBitsZero) {
      bitsPerSample[bitsPerSample.length - 1] = 0;
      //samplesPerPixel--;
    }

    TiffRational xResolution = getIFDRationalValue(ifd, X_RESOLUTION, false);
    TiffRational yResolution = getIFDRationalValue(ifd, Y_RESOLUTION, false);
    int planarConfig = getIFDIntValue(ifd, PLANAR_CONFIGURATION, false, 1);
    int resolutionUnit = getIFDIntValue(ifd, RESOLUTION_UNIT, false, 2);
    if (xResolution == null || yResolution == null) resolutionUnit = 0;
    int[] colorMap = getIFDIntArray(ifd, COLOR_MAP, false);
    int predictor = getIFDIntValue(ifd, PREDICTOR, false, 1);

    // If the subsequent color maps are empty, use the first IFD's color map
    //if (colorMap == null) {
    //  colorMap = getIFDIntArray(getFirstIFD(in), COLOR_MAP, false);
    //}

    // use special color map for YCbCr
    if (photoInterp == Y_CB_CR) {
      int[] tempColorMap = getIFDIntArray(ifd, Y_CB_CR_COEFFICIENTS, false);
      int[] refBlackWhite = getIFDIntArray(ifd, REFERENCE_BLACK_WHITE, false);
      colorMap = new int[tempColorMap.length + refBlackWhite.length];
      System.arraycopy(tempColorMap, 0, colorMap, 0, tempColorMap.length);
      System.arraycopy(refBlackWhite, 0, colorMap, tempColorMap.length,
        refBlackWhite.length);
    }

    if (DEBUG) {
      StringBuffer sb = new StringBuffer();
      sb.append("IFD directory entry values:");
      sb.append("\n\tLittleEndian=");
      sb.append(littleEndian);
      sb.append("\n\tImageWidth=");
      sb.append(imageWidth);
      sb.append("\n\tImageLength=");
      sb.append(imageLength);
      sb.append("\n\tBitsPerSample=");
      sb.append(bitsPerSample[0]);
      for (int i=1; i<bitsPerSample.length; i++) {
        sb.append(",");
        sb.append(bitsPerSample[i]);
      }
      sb.append("\n\tSamplesPerPixel=");
      sb.append(samplesPerPixel);
      sb.append("\n\tCompression=");
      sb.append(compression);
      sb.append("\n\tPhotometricInterpretation=");
      sb.append(photoInterp);
      sb.append("\n\tStripOffsets=");
      sb.append(stripOffsets[0]);
      for (int i=1; i<stripOffsets.length; i++) {
        sb.append(",");
        sb.append(stripOffsets[i]);
      }
      sb.append("\n\tRowsPerStrip=");
      sb.append(rowsPerStripArray[0]);
      for (int i=1; i<rowsPerStripArray.length; i++) {
        sb.append(",");
        sb.append(rowsPerStripArray[i]);
      }
      sb.append("\n\tStripByteCounts=");
      sb.append(stripByteCounts[0]);
      for (int i=1; i<stripByteCounts.length; i++) {
        sb.append(",");
        sb.append(stripByteCounts[i]);
      }
      sb.append("\n\tXResolution=");
      sb.append(xResolution);
      sb.append("\n\tYResolution=");
      sb.append(yResolution);
      sb.append("\n\tPlanarConfiguration=");
      sb.append(planarConfig);
      sb.append("\n\tResolutionUnit=");
      sb.append(resolutionUnit);
      sb.append("\n\tColorMap=");
      if (colorMap == null) sb.append("null");
      else {
        sb.append(colorMap[0]);
        for (int i=1; i<colorMap.length; i++) {
          sb.append(",");
          sb.append(colorMap[i]);
        }
      }
      sb.append("\n\tPredictor=");
      sb.append(predictor);
      debug(sb.toString());
    }

    for (int i=0; i<samplesPerPixel; i++) {
      if (bitsPerSample[i] < 1) {
        throw new FormatException("Illegal BitsPerSample (" +
          bitsPerSample[i] + ")");
      }
      // don't support odd numbers of bits (except for 1)
      else if (bitsPerSample[i] % 2 != 0 && bitsPerSample[i] != 1) {
        throw new FormatException("Sorry, unsupported BitsPerSample (" +
          bitsPerSample[i] + ")");
      }
    }

    if (bitsPerSample.length < samplesPerPixel) {
      throw new FormatException("BitsPerSample length (" +
        bitsPerSample.length + ") does not match SamplesPerPixel (" +
        samplesPerPixel + ")");
    }
    else if (photoInterp == TRANSPARENCY_MASK) {
      throw new FormatException(
        "Sorry, Transparency Mask PhotometricInterpretation is not supported");
    }
    else if (photoInterp == Y_CB_CR) {
      throw new FormatException(
        "Sorry, YCbCr PhotometricInterpretation is not supported");
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

    long rowsPerStrip = rowsPerStripArray[0];
    for (int i=1; i<rowsPerStripArray.length; i++) {
      if (rowsPerStrip != rowsPerStripArray[i]) {
        throw new FormatException(
          "Sorry, non-uniform RowsPerStrip is not supported");
      }
    }

    long numStrips = (imageLength + rowsPerStrip - 1) / rowsPerStrip;

    if (isTiled || fakeRPS) numStrips = stripOffsets.length;
    if (planarConfig == 2) numStrips *= samplesPerPixel;

    if (stripOffsets.length < numStrips && !fakeRPS) {
      throw new FormatException("StripOffsets length (" +
        stripOffsets.length + ") does not match expected " +
        "number of strips (" + numStrips + ")");
    }
    else if (fakeRPS) numStrips = stripOffsets.length;

    if (stripByteCounts.length < numStrips) {
      throw new FormatException("StripByteCounts length (" +
        stripByteCounts.length + ") does not match expected " +
        "number of strips (" + numStrips + ")");
    }

    if (imageWidth > Integer.MAX_VALUE || imageLength > Integer.MAX_VALUE ||
      imageWidth * imageLength > Integer.MAX_VALUE)
    {
      throw new FormatException("Sorry, ImageWidth x ImageLength > " +
        Integer.MAX_VALUE + " is not supported (" +
        imageWidth + " x " + imageLength + ")");
    }
    int numSamples = (int) (imageWidth * imageLength);

    if (planarConfig != 1 && planarConfig != 2) {
      throw new FormatException(
        "Unknown PlanarConfiguration (" + planarConfig + ")");
    }

    // read in image strips
    if (DEBUG) {
      debug("reading image data (samplesPerPixel=" +
        samplesPerPixel + "; numSamples=" + numSamples + ")");
    }

    if (photoInterp == CFA_ARRAY) {
      int[] tempMap = new int[colorMap.length + 2];
      System.arraycopy(colorMap, 0, tempMap, 0, colorMap.length);
      tempMap[tempMap.length - 2] = (int) imageWidth;
      tempMap[tempMap.length - 1] = (int) imageLength;
      colorMap = tempMap;
    }

    if (stripOffsets.length > 1 && (stripOffsets[stripOffsets.length - 1] ==
      stripOffsets[stripOffsets.length - 2]))
    {
      long[] tmp = stripOffsets;
      stripOffsets = new long[tmp.length - 1];
      System.arraycopy(tmp, 0, stripOffsets, 0, stripOffsets.length);
      numStrips--;
    }

    short[][] samples = new short[samplesPerPixel][numSamples];
    byte[] altBytes = new byte[0];

    if (bitsPerSample[0] == 16) littleEndian = !littleEndian;

    if (isTiled) {
      long tileWidth = getIFDLongValue(ifd, TILE_WIDTH, true, 0);
      long tileLength = getIFDLongValue(ifd, TILE_LENGTH, true, 0);

      byte[] data = new byte[(int) (imageWidth * imageLength *
        samplesPerPixel * (bitsPerSample[0] / 8))];

      int row = 0;
      int col = 0;

      int bytes = bitsPerSample[0] / 8;

      for (int i=0; i<stripOffsets.length; i++) {
        byte[] b = new byte[(int) stripByteCounts[i]];
        in.seek(stripOffsets[i]);
        in.read(b);

        b = uncompress(b, compression);

        int ext = (int) (b.length / (tileWidth * tileLength));
        int rowBytes = (int) (tileWidth * ext);
        if (tileWidth + col > imageWidth) {
          rowBytes = (int) ((imageWidth - col) * ext);
        }

        for (int j=0; j<tileLength; j++) {
          if (row + j < imageLength) {
            System.arraycopy(b, rowBytes*j, data,
              (int) ((row + j)*imageWidth*ext + ext*col), rowBytes);
          }
          else break;
        }

        // update row and column

        col += (int) tileWidth;
        if (col >= imageWidth) {
          row += (int) tileLength;
          col = 0;
        }
      }

      undifference(data, bitsPerSample, imageWidth, planarConfig, predictor);
      unpackBytes(samples, 0, data, bitsPerSample, photoInterp, colorMap,
        littleEndian, maxValue, planarConfig, 0, 1, imageWidth);
    }
    else {
      int overallOffset = 0;

      for (int strip=0, row=0; strip<numStrips; strip++, row+=rowsPerStrip) {
        try {
          if (DEBUG) debug("reading image strip #" + strip);
          in.seek((int) stripOffsets[strip]);

          if (stripByteCounts[strip] > Integer.MAX_VALUE) {
            throw new FormatException("Sorry, StripByteCounts > " +
              Integer.MAX_VALUE + " is not supported");
          }
          byte[] bytes = new byte[(int) stripByteCounts[strip]];
          in.read(bytes);
          if (compression != PACK_BITS) {
            bytes = uncompress(bytes, compression);
            undifference(bytes, bitsPerSample,
              imageWidth, planarConfig, predictor);
            int offset = (int) (imageWidth * row);
            if (planarConfig == 2) {
              offset = overallOffset / samplesPerPixel;
            }
            unpackBytes(samples, offset, bytes, bitsPerSample,
              photoInterp, colorMap, littleEndian, maxValue, planarConfig,
              strip, (int) numStrips, imageWidth);
            overallOffset += bytes.length / bitsPerSample.length;
          }
          else {
            // concatenate contents of bytes to altBytes
            byte[] tempPackBits = new byte[altBytes.length];
            System.arraycopy(altBytes, 0, tempPackBits, 0, altBytes.length);
            altBytes = new byte[altBytes.length + bytes.length];
            System.arraycopy(tempPackBits, 0, altBytes, 0, tempPackBits.length);
            System.arraycopy(bytes, 0, altBytes,
              tempPackBits.length, bytes.length);
          }
        }
        catch (Exception e) {
          // CTR TODO - eliminate catch-all exception handling
          if (strip == 0) {
            if (e instanceof FormatException) throw (FormatException) e;
            else throw new FormatException(e);
          }
          byte[] bytes = new byte[samples[0].length];
          undifference(bytes, bitsPerSample, imageWidth, planarConfig,
            predictor);
          int offset = (int) (imageWidth * row);
          if (planarConfig == 2) offset = overallOffset / samplesPerPixel;
          unpackBytes(samples, offset, bytes, bitsPerSample, photoInterp,
            colorMap, littleEndian, maxValue, planarConfig,
            strip, (int) numStrips, imageWidth);
          overallOffset += bytes.length / bitsPerSample.length;
        }
      }
    }

    // only do this if the image uses PackBits compression
    if (altBytes.length != 0) {
      altBytes = uncompress(altBytes, compression);
      undifference(altBytes, bitsPerSample,
        imageWidth, planarConfig, predictor);
      unpackBytes(samples, (int) imageWidth, altBytes, bitsPerSample,
        photoInterp, colorMap, littleEndian, maxValue, planarConfig, 0, 1,
        imageWidth);
    }

    // construct field
    if (DEBUG) debug("constructing image");

    // Since the lowest common denominator for all pixel operations is "byte"
    // we're going to normalize everything to byte.

    if (bitsPerSample[0] == 12) bitsPerSample[0] = 16;

    if (photoInterp == CFA_ARRAY) {
      samples =
        ImageTools.demosaic(samples, (int) imageWidth, (int) imageLength);
    }

    if (bitsPerSample[0] == 16) {
      int pt = 0;
      for (int i = 0; i < samplesPerPixel; i++) {
        for (int j = 0; j < numSamples; j++) {
          buf[pt++] = (byte) ((samples[i][j] & 0xff00) >> 8);
          buf[pt++] = (byte) (samples[i][j] & 0xff);
        }
      }
    }
    else if (bitsPerSample[0] == 32) {
      int pt = 0;
      for (int i=0; i<samplesPerPixel; i++) {
        for (int j=0; j<numSamples; j++) {
          buf[pt++] = (byte) ((samples[i][j] & 0xff000000) >> 24);
          buf[pt++] = (byte) ((samples[i][j] & 0xff0000) >> 16);
          buf[pt++] = (byte) ((samples[i][j] & 0xff00) >> 8);
          buf[pt++] = (byte) (samples[i][j] & 0xff);
        }
      }
    }
    else {
      for (int i=0; i<samplesPerPixel; i++) {
        for (int j=0; j<numSamples; j++) {
          buf[j + i*numSamples] = (byte) samples[i][j];
        }
      }
    }

    return buf;
  }

  /** Reads the image defined in the given IFD from the specified file. */
  public static BufferedImage getImage(Hashtable ifd, RandomAccessStream in)
    throws FormatException, IOException
  {
    // construct field
    if (DEBUG) debug("constructing image");

    byte[][] samples = getSamples(ifd, in);
    int[] bitsPerSample = getBitsPerSample(ifd);
    long imageWidth = getImageWidth(ifd);
    long imageLength = getImageLength(ifd);
    int samplesPerPixel = getSamplesPerPixel(ifd);
    int photoInterp = getPhotometricInterpretation(ifd);

    if (bitsPerSample[0] == 16 || bitsPerSample[0] == 12) {
      // First wrap the byte arrays and then use the features of the
      // ByteBuffer to transform to a ShortBuffer. Finally, use the ShortBuffer
      // bulk get method to copy the data into a usable form for makeImage().
      int len = samples.length == 2 ? 3 : samples.length;

      short[][] sampleData = new short[len][samples[0].length / 2];
      for (int i = 0; i < samplesPerPixel; i++) {
        ShortBuffer sampleBuf = ByteBuffer.wrap(samples[i]).asShortBuffer();
        sampleBuf.get(sampleData[i]);
      }

      // Now make our image.
      return ImageTools.makeImage(sampleData,
        (int) imageWidth, (int) imageLength);
    }
    else if (bitsPerSample[0] == 24) {
      int[][] intData = new int[samplesPerPixel][samples[0].length / 3];
      for (int i=0; i<samplesPerPixel; i++) {
        for (int j=0; j<intData[i].length; j++) {
          intData[i][j] = DataTools.bytesToInt(samples[i], j*3, 3,
            isLittleEndian(ifd));
        }
      }
      return ImageTools.makeImage(intData, (int) imageWidth, (int) imageLength);
    }
    else if (bitsPerSample[0] == 32) {
      int type = getIFDIntValue(ifd, SAMPLE_FORMAT);
      if (type == 3) {
        // float data
        float[][] floatData = new float[samplesPerPixel][samples[0].length / 4];
        for (int i=0; i<samplesPerPixel; i++) {
          floatData[i] = (float[]) DataTools.makeDataArray(samples[i], 4, true,
            isLittleEndian(ifd));
        }
        return ImageTools.makeImage(floatData,
          (int) imageWidth, (int) imageLength);
      }
      else {
        // int data
        int[][] intData = new int[samplesPerPixel][samples[0].length / 4];
        for (int i=0; i<samplesPerPixel; i++) {
          IntBuffer sampleBuf = ByteBuffer.wrap(samples[i]).asIntBuffer();
          sampleBuf.get(intData[i]);
        }

        byte[][] shortData = new byte[samplesPerPixel][intData[0].length];
        for (int i=0; i<samplesPerPixel; i++) {
          for (int j=0; j<shortData[0].length; j++) {
            shortData[i][j] = (byte) intData[i][j];
          }
        }

        return ImageTools.makeImage(shortData,
          (int) imageWidth, (int) imageLength);
      }
    }
    if (samplesPerPixel == 1) {
      return ImageTools.makeImage(samples[0], (int) imageWidth,
        (int) imageLength, 1, false);
    }

    return ImageTools.makeImage(samples, (int) imageWidth, (int) imageLength);
  }

  /**
   * Extracts pixel information from the given byte array according to the
   * bits per sample, photometric interpretation, and the specified byte
   * ordering.
   * No error checking is performed.
   * This method is tailored specifically for planar (separated) images.
   */
  public static void planarUnpack(short[][] samples, int startIndex,
    byte[] bytes, int[] bitsPerSample, int photoInterp, boolean littleEndian,
    int strip, int numStrips) throws FormatException
  {
    int numChannels = bitsPerSample.length;  // this should always be 3
    if (bitsPerSample[bitsPerSample.length - 1] == 0) numChannels--;

    // determine which channel the strip belongs to

    int channelNum = strip / (numStrips / numChannels);

    startIndex = (strip % (numStrips / numChannels)) * bytes.length;

    int index = 0;
    int counter = 0;
    for (int j=0; j<bytes.length; j++) {
      int numBytes = bitsPerSample[0] / 8;

      if (bitsPerSample[0] % 8 != 0) {
        // bits per sample is not a multiple of 8
        //
        // while images in this category are not in violation of baseline TIFF
        // specs, it's a bad idea to write bits per sample values that aren't
        // divisible by 8

        if (index == bytes.length) {
          //throw new FormatException("bad index : i = " + i + ", j = " + j);
          index--;
        }

        short b = bytes[index];
        index++;

        int offset = (bitsPerSample[0] * (samples.length*j + channelNum)) % 8;
        if (offset <= (8 - (bitsPerSample[0] % 8))) {
          index--;
        }

        if (channelNum == 0) counter++;
        if (counter % 4 == 0 && channelNum == 0) {
          index++;
        }

        int ndx = startIndex + j;
        if (ndx >= samples[channelNum].length) {
          ndx = samples[channelNum].length - 1;
        }
        samples[channelNum][ndx] = (short) (b < 0 ? 256 + b : b);

        if (photoInterp == WHITE_IS_ZERO || photoInterp == CMYK) {
          samples[channelNum][ndx] =
            (short) (Integer.MAX_VALUE - samples[channelNum][ndx]);
        }
      }
      else if (numBytes == 1) {
        float b = bytes[index];
        index++;

        int ndx = startIndex + j;
        if (ndx < samples[channelNum].length) {
          samples[channelNum][ndx] = (short) (b < 0 ? 256 + b : b);

          if (photoInterp == WHITE_IS_ZERO) { // invert color value
            samples[channelNum][ndx] =
              (short) ((65535 - samples[channelNum][ndx]) & 0xffff);
          }
          else if (photoInterp == CMYK) {
            samples[channelNum][ndx] =
              (short) (Integer.MAX_VALUE - samples[channelNum][ndx]);
          }
        }
      }
      else {
        byte[] b = new byte[numBytes];
        if (numBytes + index < bytes.length) {
          System.arraycopy(bytes, index, b, 0, numBytes);
        }
        else {
          System.arraycopy(bytes, bytes.length - numBytes, b, 0, numBytes);
        }
        index += numBytes;
        int ndx = startIndex + j;
        if (ndx >= samples[0].length) ndx = samples[0].length - 1;
        samples[channelNum][ndx] =
          (short) DataTools.bytesToLong(b, !littleEndian);

        if (photoInterp == WHITE_IS_ZERO) { // invert color value
          long max = 1;
          for (int q=0; q<numBytes; q++) max *= 8;
          samples[channelNum][ndx] = (short) (max - samples[channelNum][ndx]);
        }
        else if (photoInterp == CMYK) {
          samples[channelNum][ndx] =
            (short) (Integer.MAX_VALUE - samples[channelNum][ndx]);
        }
      }
    }
  }

  /**
   * Extracts pixel information from the given byte array according to the
   * bits per sample, photometric interpretation and color map IFD directory
   * entry values, and the specified byte ordering.
   * No error checking is performed.
   */
  public static void unpackBytes(short[][] samples, int startIndex,
    byte[] bytes, int[] bitsPerSample, int photoInterp, int[] colorMap,
    boolean littleEndian, long maxValue, int planar, int strip, int numStrips,
    long imageWidth) throws FormatException
  {
    if (planar == 2) {
      planarUnpack(samples, startIndex, bytes, bitsPerSample, photoInterp,
        littleEndian, strip, numStrips);
      return;
    }

    int totalBits = 0;
    for (int i=0; i<bitsPerSample.length; i++) totalBits += bitsPerSample[i];
    int sampleCount = 8 * bytes.length / totalBits;

    if (DEBUG) {
      debug("unpacking " + sampleCount + " samples (startIndex=" + startIndex +
        "; totalBits=" + totalBits + "; numBytes=" + bytes.length + ")");
    }
    if (startIndex + sampleCount > samples[0].length) {
      int trunc = startIndex + sampleCount - samples[0].length;
      if (DEBUG) debug("WARNING: truncated " + trunc + " extra samples");
      sampleCount -= trunc;
    }

    // rules on incrementing the index:
    // 1) if the planar configuration is set to 1 (interleaved), then add one
    //    to the index
    // 2) if the planar configuration is set to 2 (separated), then go to
    //    j + (i*(bytes.length / sampleCount))

    int bps0 = bitsPerSample[0];
    int bpsPow = (int) Math.pow(2, bps0);
    int numBytes = bps0 / 8;
    boolean noDiv8 = bps0 % 8 != 0;
    boolean bps8 = bps0 == 8;
    boolean bps16 = bps0 == 16;

    if (photoInterp == CFA_ARRAY) {
      imageWidth = colorMap[colorMap.length - 2];
    }

    int row = 0, col = 0;

    if (imageWidth != 0) row = startIndex / (int) imageWidth;

    int cw = 0;
    int ch = 0;

    if (photoInterp == CFA_ARRAY) {
      byte[] c = new byte[2];
      c[0] = (byte) colorMap[0];
      c[1] = (byte) colorMap[1];

      cw = DataTools.bytesToInt(c, littleEndian);
      c[0] = (byte) colorMap[2];
      c[1] = (byte) colorMap[3];
      ch = DataTools.bytesToInt(c, littleEndian);

      int[] tmp = colorMap;
      colorMap = new int[tmp.length - 6];
      System.arraycopy(tmp, 4, colorMap, 0, colorMap.length);
    }

    int index = 0;
    int count = 0;

    BitBuffer bb = new BitBuffer(bytes);

    byte[] copyByteArray = new byte[numBytes];
    ByteBuffer nioBytes = MappedByteBuffer.wrap(bytes);
    if (!littleEndian)
      nioBytes.order(ByteOrder.LITTLE_ENDIAN);

    for (int j=0; j<sampleCount; j++) {
      for (int i=0; i<samples.length; i++) {
        if (noDiv8) {
          // bits per sample is not a multiple of 8

          int ndx = startIndex + j;
          short s = 0;
          if ((i == 0 && (photoInterp == CFA_ARRAY ||
            photoInterp == RGB_PALETTE) || (photoInterp != CFA_ARRAY &&
            photoInterp != RGB_PALETTE)))
          {
            s = (short) bb.getBits(bps0);
            if ((ndx % imageWidth) == imageWidth - 1) {
              bb.skipBits((imageWidth * bps0 * sampleCount) % 8);
            }
          }
          short b = s;
          if (photoInterp != CFA_ARRAY) samples[i][ndx] = s;

          if (photoInterp == WHITE_IS_ZERO || photoInterp == CMYK) {
            samples[i][ndx] =
              (short) (Integer.MAX_VALUE - samples[i][ndx]); // invert colors
          }
          else if (photoInterp == CFA_ARRAY) {
            if (i == 0) {
              int pixelIndex = (int) ((row + (count / cw))*imageWidth + col +
                (count % cw));

              samples[colorMap[count]][pixelIndex] = s;
              count++;

              if (count == colorMap.length) {
                count = 0;
                col += cw*ch;
                if (col == imageWidth) col = cw;
                else if (col > imageWidth) {
                  row += ch;
                  col = 0;
                }
              }
            }
          }
        }
        else if (bps8) {
          // special case handles 8-bit data more quickly
          //if (planar == 2) { index = j+(i*(bytes.length / samples.length)); }
          short b = (short) (bytes[index] & 0xff);
          index++;

          int ndx = startIndex + j;
          samples[i][ndx] = (short) (b < 0 ? Integer.MAX_VALUE + b : b);

          if (photoInterp == WHITE_IS_ZERO) { // invert color value
            samples[i][ndx] = (short) ((65535 - samples[i][ndx]) & 0xffff);
          }
          else if (photoInterp == CMYK) {
            samples[i][ndx] = (short) (Integer.MAX_VALUE - samples[i][ndx]);
          }
          else if (photoInterp == Y_CB_CR) {
            if (i == bitsPerSample.length - 1) {
              int lumaRed = colorMap[0];
              int lumaGreen = colorMap[1];
              int lumaBlue = colorMap[2];
              int red = (int)
                (samples[2][ndx]*(2 - 2*lumaRed) + samples[0][ndx]);
              int blue = (int)
                (samples[1][ndx]*(2 - 2*lumaBlue) + samples[0][ndx]);
              int green = (int)
                (samples[0][ndx] - lumaBlue*blue - lumaRed*red);
              if (lumaGreen != 0) green = green / lumaGreen;
              samples[0][ndx] = (short) (red - colorMap[4]);
              samples[1][ndx] = (short) (green - colorMap[6]);
              samples[2][ndx] = (short) (blue - colorMap[8]);
            }
          }
        }  // End if (bps8)
        else if (bps16) {
          int ndx = startIndex + j;
          if (numBytes + index < bytes.length) {
            samples[i][ndx] = nioBytes.getShort(index);
          } else {
            samples[i][ndx] = nioBytes.getShort(bytes.length - numBytes);
          }
          index += numBytes;

          if (photoInterp == WHITE_IS_ZERO) { // invert color value
            long max = 1;
            for (int q=0; q<numBytes; q++) max *= 8;
            samples[i][ndx] = (short) (max - samples[i][ndx]);
          }
          else if (photoInterp == CMYK) {
            samples[i][ndx] = (short) (Integer.MAX_VALUE - samples[i][ndx]);
          }
        }  // End if (bps16)
        else {
          if (numBytes + index < bytes.length) {
            System.arraycopy(bytes, index, copyByteArray, 0, numBytes);
          }
          else {
            System.arraycopy(bytes, bytes.length - numBytes, copyByteArray,
              0, numBytes);
          }
          index += numBytes;
          int ndx = startIndex + j;
          samples[i][ndx] =
              (short) DataTools.bytesToLong(copyByteArray, !littleEndian);

          if (photoInterp == WHITE_IS_ZERO) { // invert color value
            long max = 1;
            for (int q=0; q<numBytes; q++) max *= 8;
            samples[i][ndx] = (short) (max - samples[i][ndx]);
          }
          else if (photoInterp == CMYK) {
            samples[i][ndx] = (short) (Integer.MAX_VALUE - samples[i][ndx]);
          }
        } // end else
      }
    }
  }

  // -- Decompression methods --

  /** Decodes a strip of data compressed with the given compression scheme. */
  public static byte[] uncompress(byte[] input, int compression)
    throws FormatException, IOException
  {
    if (compression < 0) compression += 65536;
    if (compression == UNCOMPRESSED) return input;
    else if (compression == CCITT_1D) {
      throw new FormatException(
        "Sorry, CCITT Group 3 1-Dimensional Modified Huffman " +
        "run length encoding compression mode is not supported");
    }
    else if (compression == GROUP_3_FAX) {
      throw new FormatException("Sorry, CCITT T.4 bi-level encoding " +
        "(Group 3 Fax) compression mode is not supported");
    }
    else if (compression == GROUP_4_FAX) {
      throw new FormatException("Sorry, CCITT T.6 bi-level encoding " +
        "(Group 4 Fax) compression mode is not supported");
    }
    else if (compression == LZW) {
      return new LZWCodec().decompress(input);
    }
    else if (compression == JPEG) {
      throw new FormatException(
        "Sorry, JPEG compression mode is not supported");
    }
    else if (compression == PACK_BITS) {
      return new PackbitsCodec().decompress(input);
    }
    else if (compression == PROPRIETARY_DEFLATE || compression == DEFLATE) {
      return new AdobeDeflateCodec().decompress(input);
    }
    else if (compression == THUNDERSCAN) {
      throw new FormatException("Sorry, " +
        "Thunderscan compression mode is not supported");
    }
    else if (compression == NIKON) {
      //return new NikonCodec().decompress(input);
      throw new FormatException("Sorry, Nikon compression mode is not " +
        "supported; we hope to support it in the future");
    }
    else if (compression == LURAWAVE) {
      return new LuraWaveCodec().decompress(input);
    }
    else {
      throw new FormatException(
        "Unknown Compression type (" + compression + ")");
    }
  }

  /** Undoes in-place differencing according to the given predictor value. */
  public static void undifference(byte[] input, int[] bitsPerSample,
    long width, int planarConfig, int predictor) throws FormatException
  {
    if (predictor == 2) {
      if (DEBUG) debug("reversing horizontal differencing");
      int len = bitsPerSample.length;
      if (bitsPerSample[len - 1] == 0) len = 1;
      for (int b=0; b<input.length; b++) {
        if (b / len % width == 0) continue;
        input[b] += input[b - len];
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
    ByteArrayOutputStream extraBuf, DataOutputStream extraOut, int offset,
    int tag, Object value) throws FormatException, IOException
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

    // write directory entry to output buffers
    ifdOut.writeShort(tag); // tag
    if (value instanceof short[]) { // BYTE
      short[] q = (short[]) value;
      ifdOut.writeShort(BYTE); // type
      ifdOut.writeInt(q.length); // count
      if (q.length <= 4) {
        for (int i=0; i<q.length; i++) ifdOut.writeByte(q[i]); // value(s)
        for (int i=q.length; i<4; i++) ifdOut.writeByte(0); // padding
      }
      else {
        ifdOut.writeInt(offset + extraBuf.size()); // offset
        for (int i=0; i<q.length; i++) extraOut.writeByte(q[i]); // values
      }
    }
    else if (value instanceof String) { // ASCII
      char[] q = ((String) value).toCharArray();
      ifdOut.writeShort(ASCII); // type
      ifdOut.writeInt(q.length + 1); // count
      if (q.length < 4) {
        for (int i=0; i<q.length; i++) ifdOut.writeByte(q[i]); // value(s)
        for (int i=q.length; i<4; i++) ifdOut.writeByte(0); // padding
      }
      else {
        ifdOut.writeInt(offset + extraBuf.size()); // offset
        for (int i=0; i<q.length; i++) extraOut.writeByte(q[i]); // values
        extraOut.writeByte(0); // concluding NULL byte
      }
    }
    else if (value instanceof int[]) { // SHORT
      int[] q = (int[]) value;
      ifdOut.writeShort(SHORT); // type
      ifdOut.writeInt(q.length); // count
      if (q.length <= 2) {
        for (int i=0; i<q.length; i++) ifdOut.writeShort(q[i]); // value(s)
        for (int i=q.length; i<2; i++) ifdOut.writeShort(0); // padding
      }
      else {
        ifdOut.writeInt(offset + extraBuf.size()); // offset
        for (int i=0; i<q.length; i++) extraOut.writeShort(q[i]); // values
      }
    }
    else if (value instanceof long[]) { // LONG
      long[] q = (long[]) value;
      ifdOut.writeShort(LONG); // type
      ifdOut.writeInt(q.length); // count
      if (q.length <= 1) {
        if (q.length == 1) ifdOut.writeInt((int) q[0]); // value
        else ifdOut.writeInt(0); // padding
      }
      else {
        ifdOut.writeInt(offset + extraBuf.size()); // offset
        for (int i=0; i<q.length; i++) {
          extraOut.writeInt((int) q[i]); // values
        }
      }
    }
    else if (value instanceof TiffRational[]) { // RATIONAL
      TiffRational[] q = (TiffRational[]) value;
      ifdOut.writeShort(RATIONAL); // type
      ifdOut.writeInt(q.length); // count
      ifdOut.writeInt(offset + extraBuf.size()); // offset
      for (int i=0; i<q.length; i++) {
        extraOut.writeInt((int) q[i].getNumerator()); // values
        extraOut.writeInt((int) q[i].getDenominator()); // values
      }
    }
    else if (value instanceof float[]) { // FLOAT
      float[] q = (float[]) value;
      ifdOut.writeShort(FLOAT); // type
      ifdOut.writeInt(q.length); // count
      if (q.length <= 1) {
        if (q.length == 1) ifdOut.writeFloat(q[0]); // value
        else ifdOut.writeInt(0); // padding
      }
      else {
        ifdOut.writeInt(offset + extraBuf.size()); // offset
        for (int i=0; i<q.length; i++) extraOut.writeFloat(q[i]); // values
      }
    }
    else if (value instanceof double[]) { // DOUBLE
      double[] q = (double[]) value;
      ifdOut.writeShort(DOUBLE); // type
      ifdOut.writeInt(q.length); // count
      ifdOut.writeInt(offset + extraBuf.size()); // offset
      for (int i=0; i<q.length; i++) extraOut.writeDouble(q[i]); // values
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
  public static void overwriteIFDValue(RandomAccessFile raf,
    int ifd, int tag, Object value) throws FormatException, IOException
  {
    if (DEBUG) {
      debug("overwriteIFDValue (ifd=" + ifd + "; tag=" + tag + "; value=" +
        value + ")");
    }
    byte[] header = new byte[4];
    raf.seek(0);
    raf.readFully(header);
    if (!isValidHeader(header)) {
      throw new FormatException("Invalid TIFF header");
    }
    boolean little = header[0] == LITTLE && header[1] == LITTLE; // II
    long offset = 4; // offset to the IFD
    int num = 0; // number of directory entries

    // skip to the correct IFD
    for (int i=0; i<=ifd; i++) {
      offset = DataTools.read4UnsignedBytes(raf, little);
      if (offset <= 0) {
        throw new FormatException("No such IFD (" + ifd + " of " + i + ")");
      }
      raf.seek(offset);
      num = DataTools.read2UnsignedBytes(raf, little);
      if (i < ifd) raf.seek(offset + 2 + BYTES_PER_ENTRY * num);
    }

    // search directory entries for proper tag
    for (int i=0; i<num; i++) {
      int oldTag = DataTools.read2UnsignedBytes(raf, little);
      int oldType = DataTools.read2UnsignedBytes(raf, little);
      int oldCount = DataTools.read4SignedBytes(raf, little);
      int oldOffset = DataTools.read4SignedBytes(raf, little);
      if (oldTag == tag) {
        // write new value to buffers
        ByteArrayOutputStream ifdBuf = new ByteArrayOutputStream(14);
        DataOutputStream ifdOut = new DataOutputStream(ifdBuf);
        ByteArrayOutputStream extraBuf = new ByteArrayOutputStream();
        DataOutputStream extraOut = new DataOutputStream(extraBuf);
        writeIFDValue(ifdOut, extraBuf, extraOut, oldOffset, tag, value);
        byte[] bytes = ifdBuf.toByteArray();
        byte[] extra = extraBuf.toByteArray();

        // extract new directory entry parameters
        int newTag = DataTools.bytesToInt(bytes, 0, 2, false);
        int newType = DataTools.bytesToInt(bytes, 2, 2, false);
        int newCount = DataTools.bytesToInt(bytes, 4, false);
        int newOffset = DataTools.bytesToInt(bytes, 8, false);
        boolean terminate = false;
        if (DEBUG) {
          debug("overwriteIFDValue:\n\told: (tag=" + oldTag + "; type=" +
            oldType + "; count=" + oldCount + "; offset=" + oldOffset +
            ");\n\tnew: (tag=" + newTag + "; type=" + newType + "; count=" +
            newCount + "; offset=" + newOffset + ")");
        }

        // determine the best way to overwrite the old entry
        if (extra.length == 0) {
          // new entry is inline; if old entry wasn't, old data is orphaned
          // do not override new offset value since data is inline
          if (DEBUG) debug("overwriteIFDValue: new entry is inline");
        }
        else if (oldOffset +
          oldCount * BYTES_PER_ELEMENT[oldType] == raf.length())
        {
          // old entry was already at EOF; overwrite it
          newOffset = oldOffset;
          terminate = true;
          if (DEBUG) debug("overwriteIFDValue: old entry is at EOF");
        }
        else if (newCount <= oldCount) {
          // new entry is as small or smaller than old entry; overwrite it
          newOffset = oldOffset;
          if (DEBUG) debug("overwriteIFDValue: new entry is <= old entry");
        }
        else {
          // old entry was elsewhere; append to EOF, orphaning old entry
          newOffset = (int) raf.length();
          if (DEBUG) debug("overwriteIFDValue: old entry will be orphaned");
        }

        // overwrite old entry
        raf.seek(raf.getFilePointer() - 10); // jump back
        DataTools.writeShort(raf, newType, little);
        DataTools.writeInt(raf, newCount, little);
        DataTools.writeInt(raf, newOffset, little);
        if (extra.length > 0) {
          raf.seek(newOffset);
          raf.write(extra);
        }
        if (terminate) raf.setLength(raf.getFilePointer());
        return;
      }
    }

    throw new FormatException("Tag not found (" + getIFDTagName(tag) + ")");
  }

  /** Convenience method for overwriting a file's first ImageDescription. */
  public static void overwriteComment(String id, Object value)
    throws FormatException, IOException
  {
    RandomAccessFile raf = new RandomAccessFile(id, "rw");
    overwriteIFDValue(raf, 0, TiffTools.IMAGE_DESCRIPTION, value);
    raf.close();
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
   * @return total number of bytes written
   */
  public static long writeImage(BufferedImage img, Hashtable ifd,
    OutputStream out, int offset, boolean last)
    throws FormatException, IOException
  {
    if (img == null) throw new FormatException("Image is null");
    if (DEBUG) debug("writeImage (offset=" + offset + "; last=" + last + ")");

    byte[][] values = ImageTools.getPixelBytes(img, false);

    int width = img.getWidth();
    int height = img.getHeight();

    if (values.length < 1 || values.length > 3) {
      throw new FormatException("Image has an unsupported " +
        "number of range components (" + values.length + ")");
    }
    if (values.length == 2) {
      // pad values with extra set of zeroes
      values = new byte[][] {
        values[0], values[1], new byte[values[0].length]
      };
    }

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
    int stripSize = 8192;
    int rowsPerStrip = stripSize / (width * bytesPerPixel);
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
          int q = values[c][ndx];
          if (bps[c] == 8) stripOut[strip].writeByte(q);
          else if (bps[c] == 16) {
            stripOut[strip].writeByte(q);
            stripOut[strip].writeByte(values[c][ndx+1]);
          }
          else if (bps[c] == 32) {
            for (int i=0; i<4; i++) {
              stripOut[strip].writeByte(values[c][ndx + i]);
            }
          }
          else {
            throw new FormatException("Unsupported bits per sample value (" +
              bps[c] + ")");
          }
        }
      }
    }

    // compress strips according to given differencing and compression schemes
    int planarConfig = getIFDIntValue(ifd, PLANAR_CONFIGURATION, false, 1);
    int predictor = getIFDIntValue(ifd, PREDICTOR, false, 1);
    int compression = getIFDIntValue(ifd, COMPRESSION, false, UNCOMPRESSED);
    byte[][] strips = new byte[stripsPerImage][];
    for (int i=0; i<stripsPerImage; i++) {
      strips[i] = stripBuf[i].toByteArray();
      difference(strips[i], bps, width, planarConfig, predictor);
      strips[i] = compress(strips[i], compression);
    }

    // record strip byte counts and offsets
    long[] stripByteCounts = new long[stripsPerImage];
    long[] stripOffsets = new long[stripsPerImage];
    putIFDValue(ifd, STRIP_OFFSETS, stripOffsets);
    putIFDValue(ifd, ROWS_PER_STRIP, rowsPerStrip);
    putIFDValue(ifd, STRIP_BYTE_COUNTS, stripByteCounts);

    Object[] keys = ifd.keySet().toArray();
    Arrays.sort(keys); // sort IFD tags in ascending order
    int ifdBytes = 2 + BYTES_PER_ENTRY * keys.length + 4;
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
    ifdOut.writeShort(keys.length); // number of directory entries
    for (int k=0; k<keys.length; k++) {
      Object key = keys[k];
      if (!(key instanceof Integer)) {
        throw new FormatException("Malformed IFD tag (" + key + ")");
      }
      if (((Integer) key).intValue() == LITTLE_ENDIAN) continue;
      Object value = ifd.get(key);
      if (DEBUG) {
        String sk = getIFDTagName(((Integer) key).intValue());
        String sv = value instanceof int[] ?
          ("int[" + ((int[]) value).length + "]") : value.toString();
        debug("writeImage: writing " + sk + " (value=" + sv + ")");
      }
      writeIFDValue(ifdOut, extraBuf, extraOut, offset,
        ((Integer) key).intValue(), value);
    }
    ifdOut.writeInt(last ? 0 : offset + extraBuf.size()); // offset to next IFD

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

  /**
   * Retrieves the image's width (TIFF tag ImageWidth) from a given TIFF IFD.
   * @param ifd a TIFF IFD hashtable.
   * @return the image's width.
   * @throws FormatException if there is a problem parsing the IFD metadata.
   */
  public static long getImageWidth(Hashtable ifd) throws FormatException {
    return getIFDLongValue(ifd, IMAGE_WIDTH, true, 0);
  }

  /**
   * Retrieves the image's length (TIFF tag ImageLength) from a given TIFF IFD.
   * @param ifd a TIFF IFD hashtable.
   * @return the image's length.
   * @throws FormatException if there is a problem parsing the IFD metadata.
   */
  public static long getImageLength(Hashtable ifd) throws FormatException {
    return getIFDLongValue(ifd, IMAGE_LENGTH, true, 0);
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
    return bitsPerSample;
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
    return getIFDIntValue(ifd, PHOTOMETRIC_INTERPRETATION, true, 0);
  }

  /**
   * Retrieves the strip offsets for the image (TIFF tag StripOffsets) from a
   * given TIFF IFD.
   * @param ifd a TIFF IFD hashtable.
   * @return the strip offsets for the image. The lenght of the array is equal
   *   to the number of strips per image. <i>StripsPerImage =
   *   floor ((ImageLength + RowsPerStrip - 1) / RowsPerStrip)</i>.
   * @throws FormatException if there is a problem parsing the IFD metadata.
   * @see #getStripByteCounts(Hashtable)
   * @see #getRowsPerStrip(Hashtable)
   */
  public static long[] getStripOffsets(Hashtable ifd) throws FormatException {
    return getIFDLongArray(ifd, STRIP_OFFSETS, false);
  }

  /**
   * Retrieves strip byte counts for the image (TIFF tag StripByteCounts) from a
   * given TIFF IFD.
   * @param ifd a TIFF IFD hashtable.
   * @return the byte counts for each strip. The length of the array is equal to
   *   the number of strips per image. <i>StripsPerImage =
   *   floor((ImageLength + RowsPerStrip - 1) / RowsPerStrip)</i>.
   * @throws FormatException if there is a problem parsing the IFD metadata.
   * @see #getStripOffsets(Hashtable)
   */
  public static long[] getStripByteCounts(Hashtable ifd) throws FormatException
  {
    return getIFDLongArray(ifd, STRIP_BYTE_COUNTS, false);
  }

  /**
   * Retrieves the number of rows per strip for image (TIFF tag RowsPerStrip)
   * from a given TIFF IFD.
   * @param ifd a TIFF IFD hashtable.
   * @return the number of rows per strip.
   * @throws FormatException if there is a problem parsing the IFD metadata.
   */
  public static long[] getRowsPerStrip(Hashtable ifd) throws FormatException {
    return getIFDLongArray(ifd, ROWS_PER_STRIP, false);
  }

  // -- Compression methods --

  /** Encodes a strip of data with the given compression scheme. */
  public static byte[] compress(byte[] input, int compression)
    throws FormatException, IOException
  {
    if (compression == UNCOMPRESSED) return input;
    else if (compression == CCITT_1D) {
      throw new FormatException(
        "Sorry, CCITT Group 3 1-Dimensional Modified Huffman " +
        "run length encoding compression mode is not supported");
    }
    else if (compression == GROUP_3_FAX) {
      throw new FormatException("Sorry, CCITT T.4 bi-level encoding " +
        "(Group 3 Fax) compression mode is not supported");
    }
    else if (compression == GROUP_4_FAX) {
      throw new FormatException("Sorry, CCITT T.6 bi-level encoding " +
        "(Group 4 Fax) compression mode is not supported");
    }
    else if (compression == LZW) {
      LZWCodec c = new LZWCodec();
      return c.compress(input, 0, 0, null, null);
      // return Compression.lzwCompress(input);
    }

    else if (compression == JPEG) {
      throw new FormatException(
        "Sorry, JPEG compression mode is not supported");
    }
    else if (compression == PACK_BITS) {
      throw new FormatException(
        "Sorry, PackBits compression mode is not supported");
    }
    else {
      throw new FormatException(
        "Unknown Compression type (" + compression + ")");
    }
  }

  /** Performs in-place differencing according to the given predictor value. */
  public static void difference(byte[] input, int[] bitsPerSample,
    long width, int planarConfig, int predictor) throws FormatException
  {
    if (predictor == 2) {
      if (DEBUG) debug("performing horizontal differencing");
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
    LogTools.println(System.currentTimeMillis() + ": " + message);
  }

}
