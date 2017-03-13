/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

package loci.formats.tiff;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;

import loci.common.DebugTools;
import loci.formats.FormatException;
import loci.formats.FormatTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data structure for working with TIFF Image File Directories (IFDs).
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Eric Kjellman egkjellman at wisc.edu
 * @author Melissa Linkert melissa at glencoesoftware.com
 * @author Chris Allan callan at blackcat.ca
 */
public class IFD extends HashMap<Integer, Object> {

  // -- Constants --

  private static final Logger LOGGER = LoggerFactory.getLogger(IFD.class);

  // non-IFD tags (for internal use)
  public static final int LITTLE_ENDIAN = 0;
  public static final int BIG_TIFF = 1;
  public static final int REUSE = 3;

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
  public static final int SUB_IFD = 330;
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

  /** EXIF tags. */
  public static final int EXPOSURE_TIME = 33434;
  public static final int F_NUMBER = 33437;
  public static final int EXPOSURE_PROGRAM = 34850;
  public static final int SPECTRAL_SENSITIVITY = 34852;
  public static final int ISO_SPEED_RATINGS = 34855;
  public static final int OECF = 34856;
  public static final int EXIF_VERSION = 36864;
  public static final int DATE_TIME_ORIGINAL = 36867;
  public static final int DATE_TIME_DIGITIZED = 36868;
  public static final int COMPONENTS_CONFIGURATION = 37121;
  public static final int COMPRESSED_BITS_PER_PIXEL = 37122;
  public static final int SHUTTER_SPEED_VALUE = 37377;
  public static final int APERTURE_VALUE = 37378;
  public static final int BRIGHTNESS_VALUE = 37379;
  public static final int EXPOSURE_BIAS_VALUE = 37380;
  public static final int MAX_APERTURE_VALUE = 37381;
  public static final int SUBJECT_DISTANCE = 37382;
  public static final int METERING_MODE = 37383;
  public static final int LIGHT_SOURCE = 37384;
  public static final int FLASH = 37385;
  public static final int FOCAL_LENGTH = 37386;
  public static final int MAKER_NOTE = 37500;
  public static final int USER_COMMENT = 37510;
  public static final int SUB_SEC_TIME = 37520;
  public static final int SUB_SEC_TIME_ORIGINAL = 37521;
  public static final int SUB_SEC_TIME_DIGITIZED = 37522;
  public static final int FLASH_PIX_VERSION = 40960;
  public static final int COLOR_SPACE = 40961;
  public static final int PIXEL_X_DIMENSION = 40962;
  public static final int PIXEL_Y_DIMENSION = 40963;
  public static final int RELATED_SOUND_FILE = 40964;
  public static final int FLASH_ENERGY = 41483;
  public static final int SPATIAL_FREQUENCY_RESPONSE = 41484;
  public static final int FOCAL_PLANE_X_RESOLUTION = 41486;
  public static final int FOCAL_PLANE_Y_RESOLUTION = 41487;
  public static final int FOCAL_PLANE_RESOLUTION_UNIT = 41488;
  public static final int SUBJECT_LOCATION = 41492;
  public static final int EXPOSURE_INDEX = 41493;
  public static final int SENSING_METHOD = 41495;
  public static final int FILE_SOURCE = 41728;
  public static final int SCENE_TYPE = 41729;
  public static final int CFA_PATTERN = 41730;
  public static final int CUSTOM_RENDERED = 41985;
  public static final int EXPOSURE_MODE = 41986;
  public static final int WHITE_BALANCE = 41987;
  public static final int DIGITAL_ZOOM_RATIO = 41988;
  public static final int FOCAL_LENGTH_35MM_FILM = 41989;
  public static final int SCENE_CAPTURE_TYPE = 41990;
  public static final int GAIN_CONTROL = 41991;
  public static final int CONTRAST = 41992;
  public static final int SATURATION = 41993;
  public static final int SHARPNESS = 41994;
  public static final int SUBJECT_DISTANCE_RANGE = 41996;

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
    return ((Boolean) getIFDValue(BIG_TIFF, Boolean.class)).booleanValue();
  }

  /** Gets whether the TIFF information in this IFD is little-endian. */
  public boolean isLittleEndian() throws FormatException {
    return ((Boolean) getIFDValue(LITTLE_ENDIAN, Boolean.class)).booleanValue();
  }

  /** Gets the given directory entry value from this IFD. */
  public Object getIFDValue(int tag) {
    return get(new Integer(tag));
  }

  /**
   * Gets the given directory entry value from this IFD,
   * performing some error checking.
   */
  public Object getIFDValue(int tag, Class checkClass) throws FormatException {
    Object value = get(new Integer(tag));
    if (checkClass != null && value != null && !checkClass.isInstance(value)) {
      // wrap object in array of length 1, if appropriate
      Class cType = checkClass.getComponentType();
      Object array =
        Array.newInstance(cType == null ? value.getClass() : cType, 1);
      if (cType == value.getClass()) {
        Array.set(array, 0, value);
      }
      else if (cType == boolean.class && value instanceof Boolean) {
        Array.setBoolean(array, 0, ((Boolean) value).booleanValue());
      }
      else if (cType == byte.class && value instanceof Byte) {
        Array.setByte(array, 0, ((Byte) value).byteValue());
      }
      else if (cType == char.class && value instanceof Character) {
        Array.setChar(array, 0, ((Character) value).charValue());
      }
      else if (cType == double.class && value instanceof Double) {
        Array.setDouble(array, 0, ((Double) value).doubleValue());
      }
      else if (cType == float.class && value instanceof Float) {
        Array.setFloat(array, 0, ((Float) value).floatValue());
      }
      else if (cType == int.class && value instanceof Integer) {
        Array.setInt(array, 0, ((Integer) value).intValue());
      }
      else if (cType == long.class && value instanceof Long) {
        Array.setLong(array, 0, ((Long) value).longValue());
      }
      else if (cType == short.class && value instanceof Short) {
        Array.setShort(array, 0, ((Short) value).shortValue());
      }
      else {
        try {
          if (checkClass.equals(String.class)) {
            StringBuilder sb = new StringBuilder();
            int l = Array.getLength(value);
            for (int i = 0; i < l; i++) {
              sb.append(Array.get(value, i));
              if (i < l - 1)
                sb.append(" ");
            }
            return sb.toString();
          } else {
            value = Array.get(value, 0);
            if (checkClass.isInstance(value))
              return value;
          }
        }
        catch (IllegalArgumentException exc) { }
        catch (ArrayIndexOutOfBoundsException exc) {
          // some files misbehave and report an array size of 1
          // when it is actually 0
          return null;
        }

        throw new FormatException(getIFDTagName(tag) +
          " directory entry is the wrong type (got " +
          value.getClass().getName() + ", expected " + checkClass.getName());
      }

      return array;
    }
    return value;
  }

  /**
   * Gets the given directory entry value in long format from this IFD,
   * performing some error checking.
   */
  public long getIFDLongValue(int tag, long defaultValue) throws FormatException
  {
    long value = defaultValue;
    Number number = (Number) getIFDValue(tag, Number.class);
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
      value = getIFDIntValue(tag,  -1);
    }
    catch (FormatException exc) { }
    return value;
  }

  /**
   * Gets the given directory entry value in int format from this IFD,
   * performing some error checking.
   */
  public int getIFDIntValue(int tag, int defaultValue) throws FormatException {
    int value = defaultValue;
    Number number = (Number) getIFDValue(tag, Number.class);
    if (number != null) value = number.intValue();
    return value;
  }

  /**
   * Gets the given directory entry value in rational format from this IFD,
   * performing some error checking.
   */
  public TiffRational getIFDRationalValue(int tag) throws FormatException {
    return (TiffRational) getIFDValue(tag, TiffRational.class);
  }

  /**
   * Gets the given directory entry value as a string from this IFD,
   * performing some error checking.
   */
  public String getIFDStringValue(int tag) throws FormatException {
    return (String) getIFDValue(tag, String.class);
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
      final StringBuilder sb = new StringBuilder();
      for (short s : ((short[]) o)) {
        if (!Character.isISOControl((char) s)) {
          sb.append((char) s);
        }
        else if (s != 0) sb.append("\n");
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
  public long[] getIFDLongArray(int tag) throws FormatException {
    Object value = getIFDValue(tag);
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
    else if (value instanceof OnDemandLongArray) {
      try {
        results = ((OnDemandLongArray) value).toArray();
      }
      catch (IOException e) {
        throw new FormatException(e);
      }
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
  public int[] getIFDIntArray(int tag) throws FormatException {
    Object value = getIFDValue(tag);
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
  public short[] getIFDShortArray(int tag) throws FormatException {
    Object value = getIFDValue(tag);
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
    long tileWidth = getIFDLongValue(TILE_WIDTH, 0);
    return tileWidth == 0 ? getImageWidth() : tileWidth;
  }

  /** Returns the length of an image tile. */
  public long getTileLength() throws FormatException {
    long tileLength = getIFDLongValue(TILE_LENGTH, 0);
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
    return offsets == null && tileWidth != null;
  }

  /**
   * Retrieves the image's width (TIFF tag ImageWidth) from a given TIFF IFD.
   * @return the image's width.
   * @throws FormatException if there is a problem parsing the IFD metadata.
   */
  public long getImageWidth() throws FormatException {
    long width = getIFDLongValue(IMAGE_WIDTH, 0);
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
    long length = getIFDLongValue(IMAGE_LENGTH, 0);
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
   * @see #getSamplesPerPixel()
   */
  public int[] getBitsPerSample() throws FormatException {
    int[] bitsPerSample = getIFDIntArray(BITS_PER_SAMPLE);
    if (bitsPerSample == null) bitsPerSample = new int[] {1};

    int samplesPerPixel = getSamplesPerPixel();
    if (bitsPerSample.length < samplesPerPixel) {
      LOGGER.debug("BitsPerSample length ({}) does not match " +
        "SamplesPerPixel ({})", bitsPerSample.length, samplesPerPixel);
      int bits = bitsPerSample[0];
      bitsPerSample = new int[samplesPerPixel];
      Arrays.fill(bitsPerSample, bits);
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
        return FormatTools.FLOAT;
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
    if (getCompression() == TiffCompression.OLD_JPEG) {
      return 3; // always RGB
    }
    return getIFDIntValue(SAMPLES_PER_PIXEL, 1);
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
  public TiffCompression getCompression() throws FormatException {
    return TiffCompression.get(getIFDIntValue(
        COMPRESSION, TiffCompression.UNCOMPRESSED.getCode()));
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
  public PhotoInterp getPhotometricInterpretation() throws FormatException {
    Object photo = getIFDValue(PHOTOMETRIC_INTERPRETATION);
    if (photo instanceof PhotoInterp) return (PhotoInterp) photo;
    if (photo == null && getCompression() == TiffCompression.OLD_JPEG) {
      return PhotoInterp.RGB;
    }
    int pi = photo instanceof Number ? ((Number) photo).intValue() :
      ((int[]) photo)[0];
    return PhotoInterp.get(pi);
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
    int planarConfig = getIFDIntValue(PLANAR_CONFIGURATION, 1);
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
    long[] offsets = null;
    OnDemandLongArray compressedOffsets = getOnDemandStripOffsets();
    if (compressedOffsets != null) {
      offsets = new long[(int) compressedOffsets.size()];
      try {
        for (int q=0; q<offsets.length; q++) {
          offsets[q] = compressedOffsets.get(q);
        }
      }
      catch (IOException e) {
        throw new FormatException("Failed to retrieve offset", e);
      }
    }
    else {
      offsets = getIFDLongArray(tag);
    }
    if (isTiled() && offsets == null) {
      offsets = getIFDLongArray(STRIP_OFFSETS);
    }
    if (offsets == null) return null;

    for (int i=0; i<offsets.length; i++) {
      if (offsets[i] < 0) {
        offsets[i] += 0x100000000L;
      }
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

  public OnDemandLongArray getOnDemandStripOffsets() throws FormatException
  {
    int tag = isTiled() ? TILE_OFFSETS : STRIP_OFFSETS;
    Object offsets = getIFDValue(tag);
    if (offsets instanceof OnDemandLongArray) {
      return (OnDemandLongArray) offsets;
    }
    return null;
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
    long[] byteCounts = getIFDLongArray(tag);
    if (isTiled() && byteCounts == null) {
      byteCounts = getIFDLongArray(STRIP_BYTE_COUNTS);
    }
    long imageLength = getImageLength();
    if (byteCounts == null) {
      // technically speaking, this shouldn't happen (since TIFF writers are
      // required to write the StripByteCounts tag), but we'll support it
      // anyway

      // don't rely on RowsPerStrip, since it's likely that if the file doesn't
      // have the StripByteCounts tag, it also won't have the RowsPerStrip tag
      long[] offsets = getStripOffsets();
      if (offsets == null) return null;
      int bytesPerSample = getBytesPerSample()[0];
      long imageWidth = getImageWidth();
      byteCounts = new long[offsets.length];
      int samples = getSamplesPerPixel();
      long imageSize = imageWidth * imageLength * bytesPerSample *
        (getPlanarConfiguration() == 2 ? 1 : samples);
      long count = imageSize / byteCounts.length;
      Arrays.fill(byteCounts, count);
    }

    long[] counts = new long[byteCounts.length];

    if (getCompression() == TiffCompression.LZW &&
      (!containsKey(ROWS_PER_STRIP) ||
      ((imageLength % getRowsPerStrip()[0])) != 0))
    {
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
    long[] rowsPerStrip = getIFDLongArray(ROWS_PER_STRIP);
    if (rowsPerStrip == null) {
      // create a fake RowsPerStrip entry if one is not present 
      long tileLength = getIFDLongValue(TILE_LENGTH, 0);
      return new long[] {tileLength == 0 ? getImageLength() : tileLength};
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

  /**
   * Retrieve the value by which to multiply the X and Y resolution so that
   * the resolutions are in microns per pixel.
   */
  public int getResolutionMultiplier() {
    int resolutionUnit = getIFDIntValue(RESOLUTION_UNIT);
    int multiplier = 1;
    if (resolutionUnit == 2) {
      multiplier = 25400;
    }
    else if (resolutionUnit == 3) {
      multiplier = 10000;
    }
    return multiplier;
  }

  /**
   * Retrieve the X resolution (TIFF tag XResolution) from this IFD.
   * The resolution will be normalized to microns per pixel.
   *
   * @return the X resolution, in microns per pixel
   * @throws FormatException if there is a problem parsing the IFD metadata.
   */
  public double getXResolution() throws FormatException {
    TiffRational xResolution = getIFDRationalValue(X_RESOLUTION);
    double x = xResolution == null ? 0 : 1 / xResolution.doubleValue();

    int multiplier = getResolutionMultiplier();
    return x * multiplier;
  }

  /**
   * Retrieve the Y resolution (TIFF tag YResolution) from this IFD.
   * The resolution will be normalized to microns per pixel.
   *
   * @return the Y resolution, in microns per pixel
   * @throws FormatException if there is a problem parsing the IFD metadata.
   */
  public double getYResolution() throws FormatException {
    TiffRational yResolution = getIFDRationalValue(Y_RESOLUTION);
    double y = yResolution == null ? 0 : 1 / yResolution.doubleValue();

    int resolutionUnit = getIFDIntValue(RESOLUTION_UNIT);
    int multiplier = getResolutionMultiplier();
    return y * multiplier;
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
    LOGGER.trace("IFD directory entry values:");

    for (Integer tag : keySet()) {
      Object value = get(tag);
      String v = null;
      if (value == null) {
        LOGGER.trace("\t{}=null", getIFDTagName(tag.intValue()));
      }
      else if ((value instanceof Boolean) || (value instanceof Number) ||
        (value instanceof String) || (value instanceof PhotoInterp) ||
        (value instanceof TiffCompression) || (value instanceof TiffIFDEntry))
      {
        v = value.toString();
        LOGGER.trace("\t{}={}", getIFDTagName(tag.intValue()), v);
      }
      else {
        // this is an array of primitive types, Strings, or TiffRationals
        LOGGER.trace("\t{}=", getIFDTagName(tag.intValue()));
        int nElements = Array.getLength(value);
        for (int i=0; i<nElements; i++) {
          LOGGER.trace("\t\t{}", Array.get(value, i));
        }
      }
    }
  }

  // -- Utility methods --

  /** Gets the name of the IFD tag encoded by the given number. */
  public static String getIFDTagName(int tag) { return getFieldName(tag); }

  /**
   * This method uses reflection to scan the values of this class's
   * static fields, returning the first matching field's name. It is
   * probably not very efficient, and is mainly intended for debugging.
   */
  public static String getFieldName(int value) {
    return DebugTools.getFieldName(IFD.class, value);
  }

}
