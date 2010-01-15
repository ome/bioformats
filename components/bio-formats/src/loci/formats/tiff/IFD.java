//
// IFD.java
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

package loci.formats.tiff;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;

import loci.common.LogTools;
import loci.formats.FormatException;
import loci.formats.FormatTools;

/**
 * Data structure for working with TIFF Image File Directories (IFDs).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/tiff/IFD.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/tiff/IFD.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Eric Kjellman egkjellman at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 * @author Chris Allan callan at blackcat.ca
 */
public class IFD extends HashMap<Integer, Object> {

  // -- Constants --

  // TODO: Investigate using Java 1.5 enums instead of int enumerations.
  //       http://javahowto.blogspot.com/2008/04/java-enum-examples.html

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
  public static final int IFD = 13;
  public static final int LONG8 = 16;
  public static final int SLONG8 = 17;
  public static final int IFD8 = 18;

  private static final int[] BYTES_PER_ELEMENT = {
    -1, //  0: invalid type
    1,  //  1: BYTE
    1,  //  2: ASCII
    2,  //  3: SHORT
    4,  //  4: LONG
    8,  //  5: RATIONAL
    1,  //  6: SBYTE
    1,  //  7: UNDEFINED
    2,  //  8: SSHORT
    4,  //  9: SLONG
    8,  // 10: SRATIONAL
    4,  // 11: FLOAT
    8,  // 12: DOUBLE
    4,  // 13: IFD
    -1, // 14: invalid type
    -1, // 15: invalid type
    8,  // 16: LONG8
    8,  // 17: SLONG8
    8   // 18: IFD8
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

  // -- Constructors --

  public IFD() {
    super();
  }

  public IFD(IFD ifd) {
    super(ifd);
  }

  // -- Tag retrieval methods --

  /** Gets whether this is a BigTIFF IFD. */
  public boolean isBigTiff() throws FormatException {
    return ((Boolean)
      getIFDValue(BIG_TIFF, false, Boolean.class)).booleanValue();
  }

  /** Gets whether the TIFF information in this IFD is little-endian. */
  public boolean isLittleEndian() throws FormatException {
    return ((Boolean)
      getIFDValue(LITTLE_ENDIAN, true, Boolean.class)).booleanValue();
  }

  /** Gets the given directory entry value from this IFD. */
  public Object getIFDValue(int tag) {
    return get(new Integer(tag));
  }

  /**
   * Gets the given directory entry value from this IFD,
   * performing some error checking.
   */
  public Object getIFDValue(int tag, boolean checkNull,
    Class checkClass) throws FormatException
  {
    Object value = get(new Integer(tag));
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
   * Gets the given directory entry value in long format from this IFD,
   * performing some error checking.
   */
  public long getIFDLongValue(int tag,
    boolean checkNull, long defaultValue) throws FormatException
  {
    long value = defaultValue;
    Number number = (Number) getIFDValue(tag, checkNull, Number.class);
    if (number != null) value = number.longValue();
    return value;
  }

  /**
   * Gets the given directory entry value in int format from this IFD,
   * or -1 if the given directory does not exist.
   */
  public int getIFDIntValue(int tag) {
    int value = -1;
    try {
      value = getIFDIntValue(tag, false, -1);
    }
    catch (FormatException exc) { }
    return value;
  }

  /**
   * Gets the given directory entry value in int format from this IFD,
   * performing some error checking.
   */
  public int getIFDIntValue(int tag,
    boolean checkNull, int defaultValue) throws FormatException
  {
    int value = defaultValue;
    Number number = (Number) getIFDValue(tag, checkNull, Number.class);
    if (number != null) value = number.intValue();
    return value;
  }

  /**
   * Gets the given directory entry value in rational format from this IFD,
   * performing some error checking.
   */
  public TiffRational getIFDRationalValue(int tag, boolean checkNull)
    throws FormatException
  {
    return (TiffRational) getIFDValue(tag, checkNull, TiffRational.class);
  }

  /**
   * Gets the given directory entry value as a string from this IFD,
   * performing some error checking.
   */
  public String getIFDStringValue(int tag, boolean checkNull)
    throws FormatException
  {
    return (String) getIFDValue(tag, checkNull, String.class);
  }

  /** Gets the given directory entry value as a string (regardless of type). */
  public String getIFDTextValue(int tag) {
    String value = null;
    Object o = getIFDValue(tag);
    if (o instanceof String[]) {
      StringBuilder sb = new StringBuilder();
      String[] s = (String[]) o;
      for (int i=0; i<s.length; i++) {
        sb.append(s[i]);
        if (i < s.length - 1) sb.append("\n");
      }
      value = sb.toString();
    }
    else if (o instanceof short[]) {
      StringBuffer sb = new StringBuffer();
      for (short s : ((short[]) o)) {
        if (!Character.isISOControl((char) s)) {
          sb.append((char) s);
        }
        else sb.append("\n");
      }
      value = sb.toString();
    }
    else if (o != null) value = o.toString();

    // sanitize line feeds
    if (value != null) {
      value = value.replaceAll("\r\n", "\n"); // CR-LF to LF
      value = value.replaceAll("\r", "\n"); // CR to LF
    }

    return value;
  }

  /**
   * Gets the given directory entry values in long format
   * from this IFD, performing some error checking.
   */
  public long[] getIFDLongArray(int tag,
    boolean checkNull) throws FormatException
  {
    Object value = getIFDValue(tag, checkNull, null);
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
   * from this IFD, performing some error checking.
   */
  public int[] getIFDIntArray(int tag,
    boolean checkNull) throws FormatException
  {
    Object value = getIFDValue(tag, checkNull, null);
    int[] results = null;
    if (value instanceof int[]) results = (int[]) value;
    else if (value instanceof long[]) {
      long[] v = (long[]) value;
      results = new int[v.length];
      for (int i=0; i<v.length; i++) {
        results[i] = (int) v[i];
      }
    }
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
   * from this IFD, performing some error checking.
   */
  public short[] getIFDShortArray(int tag,
    boolean checkNull) throws FormatException
  {
    Object value = getIFDValue(tag, checkNull, null);
    short[] results = null;
    if (value instanceof short[]) results = (short[]) value;
    else if (value instanceof int[]) {
      int[] v = (int[]) value;
      results = new short[v.length];
      for (int i=0; i<v.length; i++) {
        results[i] = (short) v[i];
      }
    }
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

  /** Convenience method for obtaining the ImageDescription from this IFD. */
  public String getComment() {
    return getIFDTextValue(IMAGE_DESCRIPTION);
  }

  /** Returns the width of an image tile. */
  public long getTileWidth() throws FormatException {
    long tileWidth = getIFDLongValue(TILE_WIDTH, false, 0);
    return tileWidth == 0 ? getImageWidth() : tileWidth;
  }

  /** Returns the length of an image tile. */
  public long getTileLength()
    throws FormatException
  {
    long tileLength = getIFDLongValue(TILE_LENGTH, false, 0);
    return tileLength == 0 ? getRowsPerStrip()[0] : tileLength;
  }

  /** Returns the number of image tiles per row. */
  public long getTilesPerRow() throws FormatException {
    long tileWidth = getTileWidth();
    long imageWidth = getImageWidth();
    long nTiles = imageWidth / tileWidth;
    if (nTiles * tileWidth < imageWidth) nTiles++;
    return nTiles;
  }

  /** Returns the number of image tiles per column. */
  public long getTilesPerColumn()
    throws FormatException
  {
    long tileLength = getTileLength();
    long imageLength = getImageLength();
    long nTiles = imageLength / tileLength;
    if (nTiles * tileLength < imageLength) nTiles++;
    return nTiles;
  }

  public boolean isTiled() throws FormatException {
    Object offsets = get(new Integer(STRIP_OFFSETS));
    Object tileWidth = get(new Integer(TILE_WIDTH));
    return offsets == null || tileWidth != null;
  }

  /**
   * Retrieves the image's width (TIFF tag ImageWidth) from a given TIFF IFD.
   * @return the image's width.
   * @throws FormatException if there is a problem parsing the IFD metadata.
   */
  public long getImageWidth() throws FormatException {
    long width = getIFDLongValue(IMAGE_WIDTH, true, 0);
    if (width > Integer.MAX_VALUE) {
      throw new FormatException("Sorry, ImageWidth > " + Integer.MAX_VALUE +
        " is not supported.");
    }
    return width;
  }

  /**
   * Retrieves the image's length (TIFF tag ImageLength) from a given TIFF IFD.
   * @return the image's length.
   * @throws FormatException if there is a problem parsing the IFD metadata.
   */
  public long getImageLength() throws FormatException {
    long length = getIFDLongValue(IMAGE_LENGTH, true, 0);
    if (length > Integer.MAX_VALUE) {
      throw new FormatException("Sorry, ImageLength > " + Integer.MAX_VALUE +
        " is not supported.");
    }
    return length;
  }

  /**
   * Retrieves the image's bits per sample (TIFF tag BitsPerSample) from a given
   * TIFF IFD.
   * @return the image's bits per sample. The length of the array is equal to
   *   the number of samples per pixel.
   * @throws FormatException if there is a problem parsing the IFD metadata.
   * @see #getSamplesPerPixel(IFD)
   */
  public int[] getBitsPerSample() throws FormatException {
    int[] bitsPerSample = getIFDIntArray(BITS_PER_SAMPLE, false);
    if (bitsPerSample == null) bitsPerSample = new int[] {1};

    int samplesPerPixel = getSamplesPerPixel();
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
   * @see #getBitsPerSample()
   */
  public int getPixelType() throws FormatException {
    int bps = getBitsPerSample()[0];
    int bitFormat = getIFDIntValue(SAMPLE_FORMAT);

    while (bps % 8 != 0) bps++;
    if (bps == 24 && bitFormat != 3) bps = 32;

    switch (bps) {
      case 16:
        if (bitFormat == 3) return FormatTools.FLOAT;
        return bitFormat == 2 ? FormatTools.INT16 : FormatTools.UINT16;
      case 24:
      case 64:
        return FormatTools.DOUBLE;
      case 32:
        if (bitFormat == 3) return FormatTools.FLOAT;
        return bitFormat == 2 ? FormatTools.INT32 : FormatTools.UINT32;
      default:
        return bitFormat == 2 ? FormatTools.INT8 : FormatTools.UINT8;
    }
  }

  /**
   * Retrieves the image's bytes per sample (derived from tag BitsPerSample)
   * from this IFD.
   * @return the image's bytes per sample.  The length of the array is equal to
   *   the number of samples per pixel.
   * @throws FormatException if there is a problem parsing the IFD metadata.
   * @see #getSamplesPerPixel()
   * @see #getBitsPerSample()
   */
  public int[] getBytesPerSample() throws FormatException {
    int[] bitsPerSample = getBitsPerSample();
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
   * SamplesPerPixel) from this IFD.
   * @return the number of samples per pixel.
   * @throws FormatException if there is a problem parsing the IFD metadata.
   */
  public int getSamplesPerPixel() throws FormatException {
    return getIFDIntValue(SAMPLES_PER_PIXEL, false, 1);
  }

  /**
   * Retrieves the image's compression type (TIFF tag Compression) from
   * this IFD.
   *
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
   * Other proprietary compression types are also possible;
   * see {@link TiffCompression} for more details.
   *
   * @throws FormatException if there is a problem parsing the IFD metadata.
   */
  public int getCompression() throws FormatException {
    int compression = getIFDIntValue(COMPRESSION,
      false, TiffCompression.UNCOMPRESSED);

    // HACK - Some TIFFs erroneously use Compression=0
    // instead of Compression=1 for uncompressed data.
    if (compression == 0) compression = TiffCompression.UNCOMPRESSED;

    return compression;
  }

  /**
   * Retrieves the image's photometric interpretation (TIFF tag
   * PhotometricInterpretation) from this IFD.
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
  public int getPhotometricInterpretation() throws FormatException {
    int photoInterp = getIFDIntValue(PHOTOMETRIC_INTERPRETATION, true, 0);
    PhotoInterp.checkPI(photoInterp);
    return photoInterp;
  }

  /**
   * Retrieves the image's planar configuration (TIFF tag PlanarConfiguration)
   * from this IFD.
   * @return the image's planar configuration.  As of TIFF 6.0 this is one of:
   * <ul>
   *  <li>Chunky (1)</li>
   *  <li>Planar (2)</li>
   * </ul>
   *
   * @throws FormatException if there is a problem parsing the IFD metadata.
   */
  public int getPlanarConfiguration() throws FormatException {
    int planarConfig = getIFDIntValue(PLANAR_CONFIGURATION, false, 1);
    if (planarConfig != 1 && planarConfig != 2) {
      throw new FormatException("Sorry, PlanarConfiguration (" + planarConfig +
        ") not supported.");
    }
    return planarConfig;
  }

  /**
   * Retrieves the strip offsets for the image (TIFF tag StripOffsets) from
   * this IFD.
   * @return the strip offsets for the image. The length of the array is equal
   *   to the number of strips per image. <i>StripsPerImage =
   *   floor ((ImageLength + RowsPerStrip - 1) / RowsPerStrip)</i>.
   * @throws FormatException if there is a problem parsing the IFD metadata.
   * @see #getStripByteCounts()
   * @see #getRowsPerStrip()
   */
  public long[] getStripOffsets() throws FormatException {
    int tag = isTiled() ? TILE_OFFSETS : STRIP_OFFSETS;
    long[] offsets = getIFDLongArray(tag, false);
    if (isTiled() && offsets == null) {
      offsets = getIFDLongArray(STRIP_OFFSETS, false);
    }

    if (isTiled()) return offsets;
    long rowsPerStrip = getRowsPerStrip()[0];
    long numStrips = (getImageLength() + rowsPerStrip - 1) / rowsPerStrip;
    if (getPlanarConfiguration() == 2) numStrips *= getSamplesPerPixel();
    if (offsets.length < numStrips) {
      throw new FormatException("StripOffsets length (" + offsets.length +
        ") does not match expected " + "number of strips (" + numStrips + ")");
    }
    return offsets;
  }

  /**
   * Retrieves strip byte counts for the image (TIFF tag StripByteCounts) from
   * this IFD.
   * @return the byte counts for each strip. The length of the array is equal
   *   to the number of strips per image. <i>StripsPerImage =
   *   floor((ImageLength + RowsPerStrip - 1) / RowsPerStrip)</i>.
   * @throws FormatException if there is a problem parsing the IFD metadata.
   * @see #getStripOffsets()
   */
  public long[] getStripByteCounts() throws FormatException {
    int tag = isTiled() ? TILE_BYTE_COUNTS : STRIP_BYTE_COUNTS;
    long[] byteCounts = getIFDLongArray(tag, false);
    if (isTiled() && byteCounts == null) {
      byteCounts = getIFDLongArray(STRIP_BYTE_COUNTS, false);
    }
    if (byteCounts == null) {
      // technically speaking, this shouldn't happen (since TIFF writers are
      // required to write the StripByteCounts tag), but we'll support it
      // anyway

      // don't rely on RowsPerStrip, since it's likely that if the file doesn't
      // have the StripByteCounts tag, it also won't have the RowsPerStrip tag
      long[] offsets = getStripOffsets();
      int bytesPerSample = getBytesPerSample()[0];
      long imageWidth = getImageWidth();
      long imageLength = getImageLength();
      byteCounts = new long[offsets.length];
      int samples = getSamplesPerPixel();
      long imageSize = imageWidth * imageLength * bytesPerSample *
        (getPlanarConfiguration() == 2 ? 1 : samples);
      long count = imageSize / byteCounts.length;
      Arrays.fill(byteCounts, count);
    }

    long[] counts = new long[byteCounts.length];

    if (getCompression() == TiffCompression.LZW) {
      for (int i=0; i<byteCounts.length; i++) {
        counts[i] = byteCounts[i] * 2;
      }
    }
    else System.arraycopy(byteCounts, 0, counts, 0, counts.length);

    if (isTiled()) return counts;

    long rowsPerStrip = getRowsPerStrip()[0];
    long numStrips = (getImageLength() + rowsPerStrip - 1) / rowsPerStrip;
    if (getPlanarConfiguration() == 2) numStrips *= getSamplesPerPixel();

    if (counts.length < numStrips) {
      throw new FormatException("StripByteCounts length (" + counts.length +
        ") does not match expected " + "number of strips (" + numStrips + ")");
    }

    return counts;
  }

  /**
   * Retrieves the number of rows per strip for image (TIFF tag RowsPerStrip)
   * from this IFD.
   * @return the number of rows per strip.
   * @throws FormatException if there is a problem parsing the IFD metadata.
   */
  public long[] getRowsPerStrip() throws FormatException {
    if (isTiled()) {
      return new long[] {getImageLength()};
    }
    long[] rowsPerStrip = getIFDLongArray(ROWS_PER_STRIP, false);
    if (rowsPerStrip == null) {
      // create a fake RowsPerStrip entry if one is not present
      return new long[] {getImageLength()};
    }

    // rowsPerStrip should never be more than the total number of rows
    long imageLength = getImageLength();
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

  // -- IFD population methods --

  /** Adds a directory entry to this IFD. */
  public void putIFDValue(int tag, Object value) {
    put(new Integer(tag), value);
  }

  /** Adds a directory entry of type BYTE to this IFD. */
  public void putIFDValue(int tag, short value) {
    putIFDValue(tag, new Short(value));
  }

  /** Adds a directory entry of type SHORT to this IFD. */
  public void putIFDValue(int tag, int value) {
    putIFDValue(tag, new Integer(value));
  }

  /** Adds a directory entry of type LONG to this IFD. */
  public void putIFDValue(int tag, long value) {
    putIFDValue(tag, new Long(value));
  }

  // -- Debugging --

  /** Prints the contents of this IFD. */
  public void printIFD() {
    if (!LogTools.isDebug()) return;
    StringBuffer sb = new StringBuffer();
    sb.append("IFD directory entry values:");

    Integer[] tags = (Integer[]) keySet().toArray(new Integer[0]);
    for (int entry=0; entry<tags.length; entry++) {
      sb.append("\n\t");
      sb.append(getIFDTagName(tags[entry].intValue()));
      sb.append("=");
      Object value = get(tags[entry]);
      if ((value instanceof Boolean) || (value instanceof Number) ||
        (value instanceof String))
      {
        sb.append(value);
      }
      else {
        // this is an array of primitive types, Strings, or TiffRationals
        int nElements = Array.getLength(value);
        for (int i=0; i<nElements; i++) {
          sb.append(Array.get(value, i));
          if (i < nElements - 1) sb.append(",");
        }
      }
    }
    LogTools.debug(sb.toString());
  }

  // -- Utility methods --

  /** Gets the name of the IFD tag encoded by the given number. */
  public static String getIFDTagName(int tag) { return getFieldName(tag); }

  /** Gets the name of the IFD type encoded by the given number. */
  public static String getIFDTypeName(int type) { return getFieldName(type); }

  /**
   * Gets the length in bytes of the IFD type encoded by the given number.
   * Returns -1 if the type is unknown.
   */
  public static int getIFDTypeLength(int type) {
    if (type < 0 || type >= BYTES_PER_ELEMENT.length) return -1;
    return BYTES_PER_ELEMENT[type];
  }

  /**
   * This method uses reflection to scan the values of this class's
   * static fields, returning the first matching field's name. It is
   * probably not very efficient, and is mainly intended for debugging.
   */
  public static String getFieldName(int value) {
    Field[] fields = IFD.class.getFields();
    for (int i=0; i<fields.length; i++) {
      try {
        if (fields[i].getInt(null) == value) return fields[i].getName();
      }
      catch (IllegalAccessException exc) { }
      catch (IllegalArgumentException exc) { }
    }
    return "" + value;
  }

}
