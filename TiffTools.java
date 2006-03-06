//
// TiffTools.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

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

import java.awt.Image;
import java.awt.image.*;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.zip.*;

/**
 * A utility class for manipulating TIFF files.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Eric Kjellman egkjellman at wisc.edu
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public abstract class TiffTools {

  // -- Constants --

  public static final boolean DEBUG = false;

  // non-IFD tags (for internal use)
  public static final int LITTLE_ENDIAN = 0;

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

  // photometric interpretation types
  public static final int WHITE_IS_ZERO = 0;
  public static final int BLACK_IS_ZERO = 1;
  public static final int RGB = 2;
  public static final int RGB_PALETTE = 3;
  public static final int TRANSPARENCY_MASK = 4;
  public static final int CMYK = 5;
  public static final int Y_CB_CR = 6;
  public static final int CIE_LAB = 8;

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


  // TIFF header constants
  public static final int MAGIC_NUMBER = 42;
  public static final int LITTLE = 0x49;
  public static final int BIG = 0x4d;

  // JPEG tables

  public static int[][] quantizationTables;
  public static short[][] acTableLengths;
  public static short[][] acTableValues;
  public static short[][] dcTableLengths;
  public static short[][] dcTableValues;

  // -- TiffTools API methods --

  /**
   * Tests the given data block to see if it represents
   * the first few bytes of a TIFF file.
   */
  public static boolean isValidHeader(byte[] block) {
    if (block.length < 4) { return false; }

    // byte order must be II or MM
    boolean little = block[0] == LITTLE && block[1] == LITTLE; // II
    boolean big = block[0] == BIG && block[1] == BIG; // MM
    if (!little && !big) return false;

    // check magic number (42)
    short magic = DataTools.bytesToShort(block, 2, little);
    return magic == MAGIC_NUMBER;
  }

  /** Gets whether the TIFF information in the given IFD is little endian. */
  public static boolean isLittleEndian(Hashtable ifd) throws FormatException {
    return ((Boolean) getIFDValue(ifd, LITTLE_ENDIAN,
      true, Boolean.class)).booleanValue();
  }


  // --------------------------- Reading TIFF files ---------------------------

  // -- IFD parsing methods --

  /**
   * Gets all IFDs within the given TIFF file, or null
   * if the given file is not a valid TIFF file.
   */
  public static Hashtable[] getIFDs(RandomAccessFile in) throws IOException {
    return getIFDs(in, 0);
  }

  /**
   * Gets all IFDs within the given TIFF file, or null
   * if the given file is not a valid TIFF file.
   */
  public static Hashtable[] getIFDs(RandomAccessFile in, int globalOffset)
    throws IOException
  {
    if (DEBUG) debug("getIFDs: reading IFD entries");

    // start at the beginning of the file
    in.seek(globalOffset);

    // determine byte order (II = little-endian, MM = big-endian)
    byte[] order = new byte[2];
    in.read(order);
    boolean littleEndian = order[0] == LITTLE && order[1] == LITTLE; // II
    boolean bigEndian = order[0] == BIG && order[1] == BIG; // MM
    if (!littleEndian && !bigEndian) return null;

    // check magic number (42)
    int magic = DataTools.read2UnsignedBytes(in, littleEndian);
    if (magic != MAGIC_NUMBER) return null;

    // get offset to first IFD
    long offset = DataTools.read4UnsignedBytes(in, littleEndian);

    // compute maximum possible number of IFDs, for loop safety
    // each IFD must have at least one directory entry, which means that
    // each IFD must be at least 2 + 12 + 4 = 18 bytes in length
    long ifdMax = (in.length() - 8) / 18;

    // read in IFDs
    Vector v = new Vector();
    for (long ifdNum=0; ifdNum<ifdMax; ifdNum++) {
      Hashtable ifd = new Hashtable();
      v.add(ifd);

      // save little endian flag to internal LITTLE_ENDIAN tag
      ifd.put(new Integer(LITTLE_ENDIAN), new Boolean(littleEndian));

      // read in directory entries for this IFD
      if (DEBUG) {
        debug("getIFDs: seeking IFD #" +
          ifdNum + " at " + (globalOffset + offset));
      }
      in.seek(globalOffset + offset);
      int numEntries = DataTools.read2UnsignedBytes(in, littleEndian);
      for (int i=0; i<numEntries; i++) {
        int tag = DataTools.read2UnsignedBytes(in, littleEndian);
        int type = DataTools.read2UnsignedBytes(in, littleEndian);
        int count = (int) DataTools.read4UnsignedBytes(in, littleEndian);
        if (DEBUG) {
          debug("getIFDs: read " + getIFDTagName(tag) +
            " (type=" + type + "; count=" + count + ")");
        }
        if (count < 0) return null; // invalid data
        Object value = null;
        long pos = in.getFilePointer() + 4;
        if (type == BYTE) {
          // 8-bit unsigned integer
          short[] bytes = new short[count];
          if (count > 4) {
            in.seek(globalOffset +
              DataTools.read4UnsignedBytes(in, littleEndian));
          }
          for (int j=0; j<count; j++) {
            bytes[j] = DataTools.readUnsignedByte(in);
          }
          if (bytes.length == 1) value = new Short(bytes[0]);
          else value = bytes;
        }
        else if (type == ASCII) {
          // 8-bit byte that contain a 7-bit ASCII code;
          // the last byte must be NUL (binary zero)
          byte[] ascii = new byte[count];
          if (count > 4) {
            in.seek(globalOffset +
              DataTools.read4UnsignedBytes(in, littleEndian));
          }
          DataTools.readFully(in, ascii);

          // count number of null terminators
          int nullCount = 0;
          for (int j=0; j<count; j++) if (ascii[j] == 0) nullCount++;

          // convert character array to array of strings
          String[] strings = new String[nullCount];
          int c = 0, ndx = -1;
          for (int j=0; j<count; j++) {
            if (ascii[j] == 0) {
              strings[c++] = new String(ascii, ndx + 1, j - ndx - 1);
              ndx = j;
            }
          }
          if (strings.length == 1) value = strings[0];
          else value = strings;
        }
        else if (type == SHORT) {
          // 16-bit (2-byte) unsigned integer
          int[] shorts = new int[count];
          if (count > 2) {
            in.seek(globalOffset +
              DataTools.read4UnsignedBytes(in, littleEndian));
          }
          for (int j=0; j<count; j++) {
            shorts[j] = DataTools.read2UnsignedBytes(in, littleEndian);
          }
          if (shorts.length == 1) value = new Integer(shorts[0]);
          else value = shorts;
        }
        else if (type == LONG) {
          // 32-bit (4-byte) unsigned integer
          long[] longs = new long[count];
          if (count > 1) {
            in.seek(globalOffset +
              DataTools.read4UnsignedBytes(in, littleEndian));
          }
          for (int j=0; j<count; j++) {
            longs[j] = DataTools.read4UnsignedBytes(in, littleEndian);
          }
          if (longs.length == 1) value = new Long(longs[0]);
          else value = longs;
        }
        else if (type == RATIONAL) {
          // Two LONGs: the first represents the numerator of a fraction;
          // the second, the denominator
          TiffRational[] rationals = new TiffRational[count];
          in.seek(globalOffset +
            DataTools.read4UnsignedBytes(in, littleEndian));
          for (int j=0; j<count; j++) {
            long numer = DataTools.read4UnsignedBytes(in, littleEndian);
            long denom = DataTools.read4UnsignedBytes(in, littleEndian);
            rationals[j] = new TiffRational(numer, denom);
          }
          if (rationals.length == 1) value = rationals[0];
          else value = rationals;
        }
        else if (type == SBYTE || type == UNDEFINED) {
          // SBYTE: An 8-bit signed (twos-complement) integer
          // UNDEFINED: An 8-bit byte that may contain anything,
          // depending on the definition of the field
          byte[] sbytes = new byte[count];
          if (count > 4) {
            in.seek(globalOffset +
              DataTools.read4UnsignedBytes(in, littleEndian));
          }
          DataTools.readFully(in, sbytes);
          if (sbytes.length == 1) value = new Byte(sbytes[0]);
          else value = sbytes;
        }
        else if (type == SSHORT) {
          // A 16-bit (2-byte) signed (twos-complement) integer
          short[] sshorts = new short[count];
          if (count > 2) {
            in.seek(globalOffset +
              DataTools.read4UnsignedBytes(in, littleEndian));
          }
          for (int j=0; j<count; j++) {
            sshorts[j] = DataTools.read2SignedBytes(in, littleEndian);
          }
          if (sshorts.length == 1) value = new Short(sshorts[0]);
          else value = sshorts;
        }
        else if (type == SLONG) {
          // A 32-bit (4-byte) signed (twos-complement) integer
          int[] slongs = new int[count];
          if (count > 1) {
            in.seek(globalOffset +
              DataTools.read4UnsignedBytes(in, littleEndian));
          }
          for (int j=0; j<count; j++) {
            slongs[j] = DataTools.read4SignedBytes(in, littleEndian);
          }
          if (slongs.length == 1) value = new Integer(slongs[0]);
          else value = slongs;
        }
        else if (type == SRATIONAL) {
          // Two SLONG's: the first represents the numerator of a fraction,
          // the second the denominator
          TiffRational[] srationals = new TiffRational[count];
          in.seek(globalOffset +
            DataTools.read4UnsignedBytes(in, littleEndian));
          for (int j=0; j<count; j++) {
            int numer = DataTools.read4SignedBytes(in, littleEndian);
            int denom = DataTools.read4SignedBytes(in, littleEndian);
            srationals[j] = new TiffRational(numer, denom);
          }
          if (srationals.length == 1) value = srationals[0];
          else value = srationals;
        }
        else if (type == FLOAT) {
          // Single precision (4-byte) IEEE format
          float[] floats = new float[count];
          if (count > 1) {
            in.seek(globalOffset +
              DataTools.read4UnsignedBytes(in, littleEndian));
          }
          for (int j=0; j<count; j++) {
            floats[j] = DataTools.readFloat(in, littleEndian);
          }
          if (floats.length == 1) value = new Float(floats[0]);
          else value = floats;
        }
        else if (type == DOUBLE) {
          // Double precision (8-byte) IEEE format
          double[] doubles = new double[count];
          in.seek(globalOffset +
            DataTools.read4UnsignedBytes(in, littleEndian));
          for (int j=0; j<count; j++) {
            doubles[j] = DataTools.readDouble(in, littleEndian);
          }
          if (doubles.length == 1) value = new Double(doubles[0]);
          else value = doubles;
        }
        in.seek(globalOffset + pos);
        if (value != null) ifd.put(new Integer(tag), value);
      }
      offset = DataTools.read4UnsignedBytes(in, littleEndian);
      if (offset == 0) break;
    }

    Hashtable[] ifds = new Hashtable[v.size()];
    v.copyInto(ifds);
    return ifds;
  }

  /** Gets the name of the IFD tag encoded by the given number. */
  public static String getIFDTagName(int tag) {
    // this method uses reflection to scan the values of this class's
    // static fields, returning the first matching field's name; it is
    // probably not very efficient, and is mainly intended for debugging
    Field[] fields = TiffTools.class.getFields();
    for (int i=0; i<fields.length; i++) {
      try {
        if (fields[i].getInt(null) == tag) return fields[i].getName();
      }
      catch (Exception exc) { }
    }
    return "" + tag;
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


  // -- Image reading methods --

  /** Reads the image defined in the given IFD from the specified file. */
  public static Image getImage(Hashtable ifd, RandomAccessFile in)
    throws FormatException, IOException
  {
    return getImage(ifd, in, 0);
  }

  /** Reads the image defined in the given IFD from the specified file. */
  public static Image getImage(Hashtable ifd, RandomAccessFile in,
    int globalOffset) throws FormatException, IOException
  {
    if (DEBUG) debug("parsing IFD entries");

    // get internal non-IFD entries
    boolean littleEndian = ((Boolean) getIFDValue(ifd,
      LITTLE_ENDIAN, true, Boolean.class)).booleanValue();

    // get relevant IFD entries
    long imageWidth = getIFDLongValue(ifd, IMAGE_WIDTH, true, 0);
    long imageLength = getIFDLongValue(ifd, IMAGE_LENGTH, true, 0);
    int[] bitsPerSample = getIFDIntArray(ifd, BITS_PER_SAMPLE, false);
    if (bitsPerSample == null) bitsPerSample = new int[] {1};
    int samplesPerPixel = getIFDIntValue(ifd, SAMPLES_PER_PIXEL, false, 1);
    int compression = getIFDIntValue(ifd, COMPRESSION, false, UNCOMPRESSED);
    int photoInterp = getIFDIntValue(ifd, PHOTOMETRIC_INTERPRETATION, true, 0);
    long[] stripOffsets = getIFDLongArray(ifd, STRIP_OFFSETS, false);
    long[] stripByteCounts = getIFDLongArray(ifd, STRIP_BYTE_COUNTS, false);
    long[] rowsPerStripArray = getIFDLongArray(ifd, ROWS_PER_STRIP, false);

    boolean fakeByteCounts = stripByteCounts == null;
    boolean fakeRPS = rowsPerStripArray == null;
    boolean isTiled = stripOffsets == null;

    long maxValue = 0;

    if (isTiled) {
    //  long tileWidth = getIFDLongValue(ifd, TILE_WIDTH, true, 0);
    //  long tileLength = getIFDLongValue(ifd, TILE_LENGTH, true, 0);
      stripOffsets = getIFDLongArray(ifd, TILE_OFFSETS, true);
      stripByteCounts = getIFDLongArray(ifd, TILE_BYTE_COUNTS, true);
      // TODO
      throw new FormatException("Sorry, tiled images are not supported");
    }
    else if (fakeByteCounts) {
      // technically speaking, this shouldn't happen (since TIFF writers are
      // required to write the StripByteCounts tag), but we'll support it
      // anyway

      // don't rely on RowsPerStrip, since it's likely that if the file doesn't
      // have the StripByteCounts tag, it also won't have the RowsPerStrip tag
      stripByteCounts = new long[stripOffsets.length];
      stripByteCounts[0] = stripOffsets[0];
      for (int i=1; i<stripByteCounts.length; i++) {
        stripByteCounts[i] = stripOffsets[i] - stripByteCounts[i-1];
      }
    }

    if (fakeRPS && !isTiled) {
      // create a false rowsPerStripArray if one is not present
      // it's sort of a cheap hack, but here's how it's done:
      // RowsPerStrip = stripByteCounts / (imageLength * bitsPerSample)
      // since stripByteCounts and bitsPerSample are arrays, we have to
      // iterate through each item

      // for now, each array should only have one entry
      // this is because we don't have support for planar formats, like
      // Zeiss LSM

      rowsPerStripArray = new long[1];
      long temp = stripByteCounts[0];
      stripByteCounts = new long[1];
      stripByteCounts[0] = temp;
      temp = bitsPerSample[0];
      bitsPerSample = new int[1];
      bitsPerSample[0] = (int) temp;
      temp = stripOffsets[0];
      stripOffsets = new long[1];
      stripOffsets[0] = temp;

      for (int i=0; i<stripByteCounts.length; i++) {
        // case 1: we're still within bitsPerSample array bounds
        if (i < bitsPerSample.length) {
          // remember that the universe collapses when we divide by 0
          if (bitsPerSample[i] != 0) {
            rowsPerStripArray[i] = (long) stripByteCounts[i] /
              (imageLength * (bitsPerSample[i] / 8));
          }
          else if (bitsPerSample[i] == 0 && i > 0) {
            rowsPerStripArray[i] = (long) stripByteCounts[i] /
              (imageLength * (bitsPerSample[i - 1] / 8));
          }
          else {
            throw new FormatException("BitsPerSample is 0");
          }
        }
        // case 2: we're outside bitsPerSample array bounds
        else if (i >= bitsPerSample.length) {
          rowsPerStripArray[i] = (long) stripByteCounts[i] /
            (imageLength * (bitsPerSample[bitsPerSample.length - 1] / 8));
        }
      }

      samplesPerPixel = 1;
    }

    TiffRational xResolution = getIFDRationalValue(ifd, X_RESOLUTION, false);
    TiffRational yResolution = getIFDRationalValue(ifd, Y_RESOLUTION, false);
    int planarConfig = getIFDIntValue(ifd, PLANAR_CONFIGURATION, false, 1);
    int resolutionUnit = getIFDIntValue(ifd, RESOLUTION_UNIT, false, 2);
    if (xResolution == null || yResolution == null) resolutionUnit = 0;
    int[] colorMap = getIFDIntArray(ifd, COLOR_MAP, false);
    int predictor = getIFDIntValue(ifd, PREDICTOR, false, 1);

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

    for (int i=0; i<bitsPerSample.length; i++) {
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
      photoInterp != Y_CB_CR)
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

    if (isTiled) numStrips = stripOffsets.length;
    if (planarConfig == 2) numStrips *= samplesPerPixel;

    if (stripOffsets.length != numStrips) {
        throw new FormatException("StripOffsets length (" +
          stripOffsets.length + ") does not match expected " +
          "number of strips (" + numStrips + ")");
    }

    if (stripByteCounts.length != numStrips) {
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

    if (samplesPerPixel == 1 && photoInterp == RGB_PALETTE) {
      samplesPerPixel = 3;
    }

    //if (planarConfig == 2) numSamples *= samplesPerPixel;

    byte[][] samples =
      new byte[samplesPerPixel][numSamples*bitsPerSample[0] / 8];
    byte[] altBytes = new byte[0];

    if (bitsPerSample[0] < 8) {
      samples = new byte[samplesPerPixel][numSamples];
    }

    int overallOffset = 0;
    for (int strip=0, row=0; strip<numStrips; strip++, row+=rowsPerStrip) {
      if (DEBUG) debug("reading image strip #" + strip);
      long actualRows = (row + rowsPerStrip > imageLength) ?
        imageLength - row : rowsPerStrip;
      in.seek(globalOffset + stripOffsets[strip]);
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
          photoInterp, colorMap, littleEndian, maxValue, planarConfig, strip,
          (int) numStrips);
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

    // only do this if the image uses PackBits compression
    if (altBytes.length != 0) {
      altBytes = uncompress(altBytes, compression);
      undifference(altBytes, bitsPerSample,
        imageWidth, planarConfig, predictor);
      unpackBytes(samples, (int) imageWidth, altBytes, bitsPerSample,
        photoInterp, colorMap, littleEndian, maxValue, planarConfig, 0,
        1);
    }


    // construct field
    if (DEBUG) debug("constructing image");

    return ImageTools.makeImage(samples, (int) imageWidth, (int) imageLength);
  }

  /**
   * Extracts pixel information from the given byte array according to the
   * bits per sample, photometric interpretation, and the specified byte
   * ordering.
   * No error checking is performed.
   * This method is tailored specifically for planar (separated) images.
   */
  public static void planarUnpack(byte[][] samples, int startIndex,
    byte[] bytes, int[] bitsPerSample, int photoInterp, boolean littleEndian,
    int strip, int numStrips) throws FormatException
  {
    int numChannels = bitsPerSample.length;  // this should always be 3

    // determine which channel the strip belongs to

    int channelNum = strip % numChannels;

    if (channelNum > 0) {
      startIndex = (strip % numChannels) * (strip / numChannels) *
        bytes.length / numChannels;
    }

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

        // -- MELISSA TODO -- improve this as time permits
        if (index == bytes.length) {
          //throw new FormatException("bad index : i = " + i + ", j = " + j);
          index--;
        }

        byte b = bytes[index];
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
        samples[channelNum][ndx] = (byte) (b < 0 ? 256 + b : b);

        if (photoInterp == WHITE_IS_ZERO || photoInterp == CMYK) {
           samples[channelNum][ndx] = (byte) (255 - samples[channelNum][ndx]);
        }
      }
      else if (numBytes == 1) {
        byte b = bytes[index];
        index++;

        int ndx = startIndex + j;
        samples[channelNum][ndx] = (byte) (b < 0 ? 256 + b : b);

        if (photoInterp == WHITE_IS_ZERO) { // invert color value
          samples[channelNum][ndx] = (byte) (255 - samples[channelNum][ndx]);
        }
        else if (photoInterp == CMYK) {
          samples[channelNum][ndx] = (byte) (255 - samples[channelNum][ndx]);
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
          (byte) DataTools.bytesToLong(b, !littleEndian);

        if (photoInterp == WHITE_IS_ZERO) { // invert color value
          long max = 1;
          for (int q=0; q<numBytes; q++) max *= 8;
          samples[channelNum][ndx] = (byte) (max - samples[channelNum][ndx]);
        }
        else if (photoInterp == CMYK) {
          samples[channelNum][ndx] = (byte) (255 - samples[channelNum][ndx]);
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
  public static void unpackBytes(byte[][] samples, int startIndex,
    byte[] bytes, int[] bitsPerSample, int photoInterp, int[] colorMap,
    boolean littleEndian, long maxValue, int planar, int strip, int numStrips)
    throws FormatException
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

    int counter = 0;

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

    int index = 0;
    for (int j=0; j<sampleCount; j++) {
      for (int i=0; i<samples.length; i++) {
        if (noDiv8) {
          // bits per sample is not a multiple of 8
          //
          // while images in this category are not in violation of baseline
          // TIFF specs, it's a bad idea to write bits per sample values that
          // aren't divisible by 8

          // -- MELISSA TODO -- improve this as time permits
          if (index == bytes.length) {
            //throw new FormatException("bad index : i = " + i + ", j = " + j);
            index--;
          }

          byte b = bytes[index];
          index++;
    //      if (planar == 2) index = j + (i*(bytes.length / samples.length));

          // index computed mod bitsPerSample.length to avoid problems with
          // RGB palette

          int offset = (bps0 * (samples.length*j + i)) % 8;
          if (offset <= (8 - (bps0 % 8))) index--;

          if (i == 0) counter++;
          if (counter % 4 == 0 && i == 0) index++;

          int ndx = startIndex + j;
          samples[i][ndx] = (byte) (b < 0 ? 256 + b : b);

          if (photoInterp == WHITE_IS_ZERO || photoInterp == CMYK) {
             samples[i][ndx] = (byte) (255 - samples[i][ndx]); // invert colors
          }
          else if (photoInterp == RGB_PALETTE) {
            int x = b < 0 ? 256 + b : b;
            int red = colorMap[x % colorMap.length];
            int green = colorMap[(x + bpsPow) %
              colorMap.length];
            int blue = colorMap[(x + 2*bpsPow) %
              colorMap.length];
            int[] components = {red, green, blue};
            samples[i][ndx] = (byte) components[i];
          }
        }
        else if (bps8) {
          // special case handles 8-bit data more quickly
          //if (planar == 2) { index = j+(i*(bytes.length / samples.length)); }
          byte b = bytes[index];
          index++;

          int ndx = startIndex + j;
          samples[i][ndx] = (byte) (b < 0 ? 256 + b : b);

          if (photoInterp == WHITE_IS_ZERO) { // invert color value
            samples[i][ndx] = (byte) (255 - samples[i][ndx]);
          }
          else if (photoInterp == RGB_PALETTE) {
            index--;
            int x = b < 0 ? 256 + b : b;
//            int red = colorMap[x];
//            int green = colorMap[x + bpsPow];
//            int blue = colorMap[x + 2*bpsPow];
//            int[] components = {red, green, blue};
//            samples[i][ndx] = (byte) components[i];
//            if (maxValue == 0) {
//              samples[i][ndx] = (byte) (components[i] % 255);
//            }
            int cndx = i == 0 ? x : (i == 1 ? (x + bpsPow) : (x + 2*bpsPow));
            int cm = colorMap[cndx];
            samples[i][ndx] = (byte) (maxValue == 0 ? (cm % 255) : cm);
          }
          else if (photoInterp == CMYK) {
            samples[i][ndx] = (byte) (255 - samples[i][ndx]);
          }
          else if (photoInterp == Y_CB_CR) {
            // 1) YCbCrSubSampling -- deal with varying chroma/luma dims
            // 2) YCbCrPositioning
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
              if (lumaGreen != 0) green = (int) (green / lumaGreen);
              samples[0][ndx] = (byte) (red - colorMap[4]);
              samples[1][ndx] = (byte) (green - colorMap[6]);
              samples[2][ndx] = (byte) (blue - colorMap[8]);
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
          samples[i][ndx] = (byte) DataTools.bytesToLong(b, !littleEndian);

          if (photoInterp == WHITE_IS_ZERO) { // invert color value
            long max = 1;
            for (int q=0; q<numBytes; q++) max *= 8;
            samples[i][ndx] = (byte) (max - samples[i][ndx]);
          }
          else if (photoInterp == RGB_PALETTE) {
            // will fix this later
            throw new FormatException("16 bit RGB palette not supported");
            /*
            int x = DataTools.bytesToInt(b, littleEndian) % colorMap.length;
            int red = colorMap[x];
            int green = colorMap[(x + bpsPow) % colorMap.length];
            int blue = colorMap[(x + 2*bpsPow) % colorMap.length];
            int[] components = {red, green, blue};
            samples[i][ndx] = (byte) components[i];
            if (samples[i][ndx] == 0) {
              samples[i][ndx] = (byte) (components[i] % 255);
            }
            */
          }
          else if (photoInterp == CMYK) {
            samples[i][ndx] = (byte) (255 - samples[i][ndx]);
          }
        }
      }
      if ((photoInterp == RGB_PALETTE) && (bps0 == 8)) index++;
    }
  }


  // -- Decompression methods --

  /** Decodes a strip of data compressed with the given compression scheme. */
  public static byte[] uncompress(byte[] input, int compression)
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
    else if (compression == LZW) return lzwUncompress(input);
    else if (compression == JPEG) {
      throw new FormatException(
        "Sorry, JPEG compression mode is not supported");
    }
    else if (compression == PACK_BITS) return packBitsUncompress(input);
    else if (compression == PROPRIETARY_DEFLATE || compression == DEFLATE) {
      return deflateUncompress(input);
    }
    else if (compression == THUNDERSCAN) {
      throw new FormatException("Sorry, Thunderscan compression mode is not " +
        "supported");
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
      for (int b=0; b<input.length; b++) {
        if (b / bitsPerSample.length % width == 0) continue;
        input[b] += input[b - bitsPerSample.length];
      }
    }
    else if (predictor != 1) {
      throw new FormatException("Unknown Predictor (" + predictor + ")");
    }
  }


  /** Decodes an Adobe Deflate (Zip) compressed image strip. */
  public static byte[] deflateUncompress(byte[] input) throws FormatException {
    // could have written this from scratch, but using the java.util.zip
    // package is much easier
    try {
      Inflater inf = new Inflater(false);
      inf.setInput(input);
      InflaterInputStream i =
        new InflaterInputStream(new PipedInputStream() , inf);
      int avail = i.available();
      Vector bytes = new Vector();
      while (avail != 0) {
        bytes.add(new Byte((byte) i.read()));
        avail = i.available();
      }

      // copy the Vector to a byte array

      byte[] toRtn = new byte[bytes.size()];
      for (int j=0; j<toRtn.length; j++) {
        toRtn[j] = ((Byte) bytes.get(j)).byteValue();
      }
      return toRtn;
    }
    catch (Exception e) {
      /* debug */ e.printStackTrace();
      throw new FormatException("Error uncompressing Adobe Deflate (ZLIB) " +
        "compressed image strip.");
    }
  }

  /**
   * Decodes a PackBits (Macintosh RLE) compressed image.
   * Adapted from the TIFF 6.0 specification, page 42.
   * @author Melissa Linkert, linkert at cs.wisc.edu
   */
  public static byte[] packBitsUncompress(byte[] input) {
    Vector output = new Vector();
    int pt = 0;
    byte n;
    while (pt < input.length) {
      n = input[pt];
      pt++;
      if ((0 <= n) && (n <= 127)) {
        for (int i=0; i<(int) n+1; i++) {
          if (pt < input.length) {
            output.add(new Byte(input[pt]));
            pt++;
          }
        }
      }
      else if ((-127 <= n) || (n <= -1)) {
        for (int i=0; i<(int) (-n+1); i++) {
          if (pt < input.length) {
            output.add(new Byte(input[pt]));
          }
        }
        pt++;
      }
      else { pt++; }
    }
    byte[] toRtn = new byte[output.size()];
    for (int i=0; i<toRtn.length; i++) {
      toRtn[i] = ((Byte) output.get(i)).byteValue();
    }
    return toRtn;
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
    if (DEBUG) debug("decompressing " + input.length + " bytes of LZW data");

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


  // -- Image writing methods --

  /**
   * Writes the given field to the specified output stream using the given
   * byte offset and IFD, in big-endian format.
   *
   * @param image The field to write
   * @param ifd Hashtable representing the TIFF IFD; can be null
   * @param out The output stream to which the TIFF data should be written
   * @param offset The value to use for specifying byte offsets
   * @param last Whether this image is the final IFD entry of the TIFF data
   * @return total number of bytes written
   */
  public static long writeImage(Image image, Hashtable ifd,
    OutputStream out, int offset, boolean last)
    throws FormatException, IOException
  {
    if (image == null) throw new FormatException("Image is null");
    if (DEBUG) debug("writeImage (offset=" + offset + "; last=" + last + ")");

    BufferedImage img = ImageTools.makeImage(image);
    DataBuffer buf = img.getRaster().getDataBuffer();

    // get pixels
    int[][] values = new int[0][0];

    if (buf instanceof DataBufferByte) {
      // originally 8 bit data
      byte[][] data = ((DataBufferByte) buf).getBankData();
      values = new int[data.length][data[0].length];
      for (int i=0; i<data.length; i++) {
        for (int j=0; j<data[i].length; j++) {
          values[i][j] = (int) data[i][j];
        }
      }
    }
    else if (buf instanceof DataBufferUShort) {
      // originally 16 bit data
      short[][] data = ((DataBufferUShort) buf).getBankData();
      values = new int[data.length][data[0].length];
      for (int i=0; i<data.length; i++) {
        for (int j=0; j<data[i].length; j++) {
          values[i][j] = (int) data[i][j];
        }
      }
    }
    else if (buf instanceof DataBufferInt) {
      // originally 32 bit data
      values = ((DataBufferInt) buf).getBankData();
    }

    int width = img.getWidth();
    int height = img.getHeight();

    if (values.length < 1 || values.length > 3) {
      throw new FormatException("Image has an unsupported " +
        "number of range components (" + values.length + ")");
    }
    if (values.length == 2) {
      // pad values with extra set of zeroes
      values = new int[][] {
        values[0], values[1], new int[values[0].length]
      };
    }

    // populate required IFD directory entries (except strip information)
    if (ifd == null) ifd = new Hashtable();
    putIFDValue(ifd, IMAGE_WIDTH, width);
    putIFDValue(ifd, IMAGE_LENGTH, height);
    if (getIFDValue(ifd, BITS_PER_SAMPLE) == null) {
      int max = 0;
      for (int c=0; c<values.length; c++) {
        for (int ndx=0; ndx<values[c].length; ndx++) {
          int v = values[c][ndx];
          int iv = v;
          if (v != iv) {
            throw new FormatException("Sample #" + ndx +
              " of range component #" + c + " is not an integer (" + v + ")");
          }
          if (iv > max) max = iv;
        }
      }
      int bps = max < 256 ? 8 : (max < 65536 ? 16 : 32);
      int[] bpsArray = new int[values.length];
      Arrays.fill(bpsArray, bps);
      putIFDValue(ifd, BITS_PER_SAMPLE, bpsArray);
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

    // create pixel output buffers
    int stripSize = 8192;
    int rowsPerStrip = stripSize / width;
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
        int ndx = y * width + x;
        for (int c=0; c<values.length; c++) {
          int q = values[c][ndx];
          if (bps[c] == 8) stripOut[strip].writeByte(q);
          else if (bps[c] == 16) stripOut[strip].writeShort(q);
          else if (bps[c] == 32) stripOut[strip].writeInt(q);
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
    int ifdBytes = 2 + 12 * keys.length + 4;
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
      Object value = ifd.get(key);
      if (DEBUG) {
        String sk = getIFDTagName(((Integer) key).intValue());
        String sv = value instanceof int[] ?
          ("int[" + ((int[]) value).length + "]") : value.toString();
        debug("writeImage: writing " + sk + " (value=" + sv + ")");
      }

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
      ifdOut.writeShort(((Integer) key).intValue()); // tag
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
          value.getClass().getName() + ")");
      }
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
    else if (compression == LZW) return lzwCompress(input);
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

  /**
   * Encodes an image strip using the LZW compression method.
   * Adapted from the TIFF 6.0 Specification:
   * http://partners.adobe.com/asn/developer/pdfs/tn/TIFF6.pdf (page 61)
   */
  public static byte[] lzwCompress(byte[] input) {
    if (input == null || input.length == 0) return input;
    if (DEBUG) debug("compressing " + input.length + " bytes of data to LZW");

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


  // -- Debugging --

  /** Prints a debugging message with current time. */
  public static void debug(String message) {
    System.out.println(System.currentTimeMillis() + ": " + message);
  }

}
