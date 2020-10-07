/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.formats.in;

import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import loci.common.ByteArrayHandle;
import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.Region;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.Memoizer;
import loci.formats.MetadataTools;
import loci.formats.codec.Codec;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.JPEGCodec;
import loci.formats.codec.LosslessJPEGCodec;
import loci.formats.codec.JPEG2000Codec;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.IFDList;
import loci.formats.tiff.PhotoInterp;
import loci.formats.tiff.TiffParser;

import ome.units.UNITS;
import ome.xml.model.primitives.Timestamp;

/**
 * CellSensReader is the file format reader for cellSens .vsi files.
 */
public class CellSensReader extends FormatReader {

  // -- Constants --

  public static final String FAIL_ON_MISSING_KEY = "cellsens.fail_on_missing_ets";
  public static final boolean FAIL_ON_MISSING_DEFAULT = false;

  // Compression types
  private static final int RAW = 0;
  private static final int JPEG = 2;
  private static final int JPEG_2000 = 3;
  private static final int JPEG_LOSSLESS = 5;
  private static final int PNG = 8;
  private static final int BMP = 9;

  // Pixel types
  private static final int CHAR = 1;
  private static final int UCHAR = 2;
  private static final int SHORT = 3;
  private static final int USHORT = 4;
  private static final int INT = 5;
  private static final int UINT = 6;
  private static final int LONG = 7;
  private static final int ULONG = 8;
  private static final int FLOAT = 9;
  private static final int DOUBLE = 10;

  // Simple field types
  private static final int COMPLEX = 11;
  private static final int BOOLEAN = 12;
  private static final int TCHAR = 13;
  private static final int DWORD = 14;
  private static final int TIMESTAMP = 17;
  private static final int DATE = 18;
  private static final int INT_2 = 256;
  private static final int INT_3 = 257;
  private static final int INT_4 = 258;
  private static final int INT_RECT = 259;
  private static final int DOUBLE_2 = 260;
  private static final int DOUBLE_3 = 261;
  private static final int DOUBLE_4 = 262;
  private static final int DOUBLE_RECT = 263;
  private static final int DOUBLE_2_2 = 264;
  private static final int DOUBLE_3_3 = 265;
  private static final int DOUBLE_4_4 = 266;
  private static final int INT_INTERVAL = 267;
  private static final int DOUBLE_INTERVAL = 268;
  private static final int RGB = 269;
  private static final int BGR = 270;
  private static final int FIELD_TYPE = 271;
  private static final int MEM_MODEL = 272;
  private static final int COLOR_SPACE = 273;
  private static final int INT_ARRAY_2 = 274;
  private static final int INT_ARRAY_3 = 275;
  private static final int INT_ARRAY_4 = 276;
  private static final int INT_ARRAY_5 = 277;
  private static final int DOUBLE_ARRAY_2 = 279;
  private static final int DOUBLE_ARRAY_3 = 280;
  private static final int UNICODE_TCHAR = 8192;
  private static final int DIM_INDEX_1 = 8195;
  private static final int DIM_INDEX_2 = 8199;
  private static final int VOLUME_INDEX = 8200;
  private static final int PIXEL_INFO_TYPE = 8470;

  // Extended field types
  private static final int NEW_VOLUME_HEADER = 0;
  private static final int PROPERTY_SET_VOLUME = 1;
  private static final int NEW_MDIM_VOLUME_HEADER = 2;
  private static final int TIFF_IFD = 10;
  private static final int VECTOR_DATA = 11;

  // Tag values

  private static final int COLLECTION_VOLUME = 2000;
  private static final int MULTIDIM_IMAGE_VOLUME = 2001;
  private static final int IMAGE_FRAME_VOLUME = 2002;
  private static final int DIMENSION_SIZE = 2003;
  private static final int IMAGE_COLLECTION_PROPERTIES = 2004;
  private static final int MULTIDIM_STACK_PROPERTIES = 2005;
  private static final int FRAME_PROPERTIES = 2006;
  private static final int DIMENSION_DESCRIPTION_VOLUME = 2007;
  private static final int CHANNEL_PROPERTIES = 2008;
  private static final int DISPLAY_MAPPING_VOLUME = 2011;
  private static final int LAYER_INFO_PROPERTIES = 2012;
  private static final int CHANNEL_INFO_PROPERTIES = 2013;
  private static final int DEFAULT_SAMPLE_IFD = 2016;
  private static final int VECTOR_LAYER_VOLUME = 2017;
  private static final int EXTERNAL_FILE_PROPERTIES = 2018;
  private static final int COARSE_FRAME_IFD = 2019;
  private static final int COARSE_PYRAMID_LEVEL = 2022;
  private static final int SERIALIZED_MASK = 2023;
  private static final int UNKNOWN_BLOBS_VOLUME = 2024;
  private static final int SAMPLE_ID_LIST = 2027;
  private static final int EXTRA_SAMPLES = 2028;
  private static final int EXTRA_SAMPLES_PROPERTIES = 2029;
  private static final int SAMPLE_FLAGS_LIST = 2030;
  private static final int FRAME_TYPE = 2031;
  private static final int DEFAULT_BACKGROUND_COLOR = 2034;
  private static final int VERSION_NUMBER = 2035;

  private static final int DOCUMENT_PROPERTIES = 2109;
  private static final int DOCUMENT_NAME = 11;
  private static final int DOCUMENT_NOTE = 13;
  private static final int DOCUMENT_TIME = 14;
  private static final int DOCUMENT_AUTHOR = 15;
  private static final int DOCUMENT_COMPANY = 16;
  private static final int DOCUMENT_CREATOR_NAME = 17;
  private static final int DOCUMENT_CREATOR_MAJOR_VERSION = 18;
  private static final int DOCUMENT_CREATOR_MINOR_VERSION = 19;
  private static final int DOCUMENT_CREATOR_SUB_VERSION = 20;
  private static final int DOCUMENT_CREATOR_BUILD_NUMBER = 21;
  private static final int DOCUMENT_CREATOR_PACKAGE = 22;
  private static final int DOCUMENT_PRODUCT = 23;
  private static final int DOCUMENT_PRODUCT_NAME = 24;
  private static final int DOCUMENT_PRODUCT_VERSION = 25;
  private static final int DOCUMENT_TYPE_HINT = 27;
  private static final int DOCUMENT_THUMB = 28;

  private static final int SLIDE_PROPERTIES = 2062;
  private static final int SLIDE_SPECIMEN = 2055;
  private static final int SLIDE_TISSUE = 2057;
  private static final int SLIDE_PREPARATION = 2058;
  private static final int SLIDE_STAINING = 2059;
  private static final int SLIDE_INFO = 2060;
  private static final int SLIDE_NAME = 2061;

  private static final int DYNAMIC_PROPERTIES = 27300;

  private static final int MAGNIFICATION = 1073741824;

  // External Raw Data properties (stored under EXTERNAL_FILE_PROPERTIES)
  private static final int IMAGE_BOUNDARY = 2053;
  private static final int TILE_SYSTEM = 20004;
  private static final int HAS_EXTERNAL_FILE = 20005;
  private static final int EXTERNAL_DATA_VOLUME = 20025;
  private static final int TILE_ORIGIN = 2410;

  // Camera property tags
  private static final int EXPOSURE_TIME = 100002;
  private static final int CAMERA_GAIN = 100003;
  private static final int CAMERA_OFFSET = 100004;
  private static final int CAMERA_GAMMA = 100005;
  private static final int SHARPNESS = 100006;
  private static final int RED_GAIN = 100007;
  private static final int GREEN_GAIN = 100008;
  private static final int BLUE_GAIN = 100009;
  private static final int RED_OFFSET = 100010;
  private static final int GREEN_OFFSET = 100011;
  private static final int BLUE_OFFSET = 100012;
  private static final int SHADING_SUB = 100013;
  private static final int SHADING_MUL = 100014;
  private static final int X_BINNING = 100015;
  private static final int Y_BINNING = 100016;
  private static final int CLIPPING = 100017;
  private static final int MIRROR_H = 100023;
  private static final int MIRROR_V = 100024;
  private static final int CLIPPING_STATE = 100025;
  private static final int ICC_ENABLED = 100030;
  private static final int BRIGHTNESS = 100031;
  private static final int CONTRAST = 100032;
  private static final int CONTRAST_TARGET = 100033;
  private static final int ACCUMULATION = 100034;
  private static final int AVERAGING = 100035;
  private static final int ISO_SENSITIVITY = 100038;
  private static final int ACCUMULATION_MODE = 100039;
  private static final int AUTOEXPOSURE = 100043;
  private static final int EXPOSURE_METERING_MODE = 100044;
  private static final int FRAME_SIZE = 100048;
  private static final int BIT_DEPTH = 100049;
  private static final int HDRI_ON = 100055;
  private static final int HDRI_FRAMES = 100056;
  private static final int HDRI_EXPOSURE_RANGE = 100057;
  private static final int HDRI_MAP_MODE = 100058;
  private static final int CUSTOM_GRAYSCALE = 100059;
  private static final int SATURATION = 100060;
  private static final int WB_PRESET_ID = 100061;
  private static final int WB_PRESET_NAME = 100062;
  private static final int WB_MODE = 100063;
  private static final int CCD_SENSITIVITY = 100064;
  private static final int ENHANCED_DYNAMIC_RANGE = 100065;
  private static final int PIXEL_CLOCK = 100066;
  private static final int COLORSPACE = 100067;
  private static final int COOLING_ON = 100068;
  private static final int FAN_SPEED = 100069;
  private static final int TEMPERATURE_TARGET = 100070;
  private static final int GAIN_UNIT = 100071;
  private static final int EM_GAIN = 100072;
  private static final int PHOTON_IMAGING_MODE = 100073;
  private static final int FRAME_TRANSFER = 100074;
  private static final int ANDOR_SHIFT_SPEED = 100075;
  private static final int VCLOCK_AMPLITUDE = 100076;
  private static final int SPURIOUS_NOISE_REMOVAL = 100077;
  private static final int SIGNAL_OUTPUT = 100078;
  private static final int BASELINE_OFFSET_CLAMP = 100079;
  private static final int DP80_FRAME_CENTERING = 100080;
  private static final int HOT_PIXEL_CORRECTION = 100081;
  private static final int NOISE_REDUCTION = 100082;
  private static final int WIDER = 100083;
  private static final int PHOTOBLEACHING = 100084;
  private static final int PREAMP_GAIN_VALUE = 100085;
  private static final int WIDER_ENABLED = 100086;

  // Dimension properties
  private static final int Z_START = 2012;
  private static final int Z_INCREMENT = 2013;
  private static final int Z_VALUE = 2014;
  private static final int TIME_START = 2100;
  private static final int TIME_INCREMENT = 2016;
  private static final int TIME_VALUE = 2017;
  private static final int LAMBDA_START = 2039;
  private static final int LAMBDA_INCREMENT = 2040;
  private static final int LAMBDA_VALUE = 2041;
  private static final int DIMENSION_NAME = 2021;
  private static final int DIMENSION_MEANING = 2023;
  private static final int DIMENSION_START_ID = 2025;
  private static final int DIMENSION_INCREMENT_ID = 2026;
  private static final int DIMENSION_VALUE_ID = 2027;

  private static final int CHANNEL_NAME = 2419;

  // Dimension types
  private static final int Z = 1;
  private static final int T = 2;
  private static final int LAMBDA = 3;
  private static final int C = 4;
  private static final int UNKNOWN = 5;
  private static final int PHASE = 9;

  // Stack properties
  private static final int DISPLAY_LIMITS = 2003;
  private static final int STACK_DISPLAY_LUT = 2004;
  private static final int GAMMA_CORRECTION = 2005;
  private static final int FRAME_ORIGIN = 2006;
  private static final int FRAME_SCALE = 2007;
  private static final int DISPLAY_COLOR = 2008;
  private static final int CREATION_TIME = 2015;
  private static final int RWC_FRAME_ORIGIN = 2018;
  private static final int RWC_FRAME_SCALE = 2019;
  private static final int RWC_FRAME_UNIT = 2020;
  private static final int STACK_NAME = 2030;
  private static final int CHANNEL_DIM = 2031;
  private static final int OPTICAL_PATH = 2043;
  private static final int STACK_TYPE = 2074;
  private static final int LIVE_OVERFLOW = 2076;
  private static final int IS_TRANSMISSION = 20035;
  private static final int CONTRAST_BRIGHTNESS = 10047;
  private static final int ACQUISITION_PROPERTIES = 10048;
  private static final int GRADIENT_LUT = 10065;

  // Stack types
  private static final int DEFAULT_IMAGE = 0;
  private static final int OVERVIEW_IMAGE = 1;
  private static final int SAMPLE_MASK = 2;
  private static final int FOCUS_IMAGE = 4;
  private static final int EFI_SHARPNESS_MAP = 8;
  private static final int EFI_HEIGHT_MAP = 16;
  private static final int EFI_TEXTURE_MAP = 32;
  private static final int EFI_STACK = 64;
  private static final int MACRO_IMAGE = 256;

  // Display mapping tags
  private static final int DISPLAY_PROCESSOR_TYPE = 10000;
  private static final int RENDER_OPERATION_ID = 10001;
  private static final int DISPLAY_STACK_ID = 10005;
  private static final int TRANSPARENCY_ID = 10006;
  private static final int THIRD_ID = 10007;
  private static final int DISPLAY_VISIBLE = 10008;
  private static final int TRANSPARENCY_VALUE = 10009;
  private static final int DISPLAY_LUT = 10013;
  private static final int DISPLAY_STACK_INDEX = 10014;
  private static final int CHANNEL_TRANSPARENCY_VALUE = 10018;
  private static final int CHANNEL_VISIBLE = 10025;
  private static final int SELECTED_CHANNELS = 10028;
  private static final int DISPLAY_GAMMA_CORRECTION = 10032;
  private static final int CHANNEL_GAMMA_CORRECTION = 10033;
  private static final int DISPLAY_CONTRAST_BRIGHTNESS = 10045;
  private static final int CHANNEL_CONTRAST_BRIGHTNESS = 10046;
  private static final int ACTIVE_STACK_DIMENSION = 10049;
  private static final int SELECTED_FRAMES = 10050;
  private static final int DISPLAYED_LUT_ID = 10054;
  private static final int HIDDEN_LAYER = 10056;
  private static final int LAYER_XY_FIXED = 10057;
  private static final int ACTIVE_LAYER_VECTOR = 10060;
  private static final int ACTIVE_LAYER_INDEX_VECTOR = 10061;
  private static final int CHAINED_LAYERS = 10062;
  private static final int LAYER_SELECTION = 10063;
  private static final int LAYER_SELECTION_INDEX = 10064;
  private static final int CANVAS_COLOR_1 = 10066;
  private static final int CANVAS_COLOR_2 = 10067;
  private static final int ORIGINAL_FRAME_RATE = 10069;
  private static final int USE_ORIGINAL_FRAME_RATE = 10070;
  private static final int ACTIVE_CHANNEL = 10071;
  private static final int PLANE_UNIT = 2011;
  private static final int Y_PLANE_DIMENSION_UNIT = 2063;
  private static final int Y_DIMENSION_UNIT = 2064;
  private static final int PLANE_ORIGIN_RWC = 20006;
  private static final int PLANE_SCALE_RWC = 20007;
  private static final int CHANNEL_OVERFLOW = 2073;

  // Objective data
  private static final int OBJECTIVE_MAG = 120060;
  private static final int NUMERICAL_APERTURE = 120061;
  private static final int WORKING_DISTANCE = 120062;
  private static final int OBJECTIVE_NAME = 120063;
  private static final int OBJECTIVE_TYPE = 120064;
  private static final int REFRACTIVE_INDEX = 120079;

  private static final int DEVICE_NAME = 120116;
  private static final int DEVICE_ID = 120129;
  private static final int DEVICE_SUBTYPE = 120130;
  private static final int DEVICE_MANUFACTURER = 120133;
  private static final int VALUE = 268435458;

  // -- Fields --

  private String[] usedFiles;
  private HashMap<Integer, String> fileMap = new HashMap<Integer, String>();

  private TiffParser parser;
  private IFDList ifds;

  private ArrayList<Long[]> tileOffsets = new ArrayList<Long[]>();
  private boolean jpeg = false;

  private ArrayList<Integer> rows = new ArrayList<Integer>();
  private ArrayList<Integer> cols = new ArrayList<Integer>();
  private ArrayList<Integer> compressionType = new ArrayList<Integer>();
  private ArrayList<Integer> tileX = new ArrayList<Integer>();
  private ArrayList<Integer> tileY = new ArrayList<Integer>();

  private ArrayList<ArrayList<TileCoordinate>> tileMap =
    new ArrayList<ArrayList<TileCoordinate>>();
  private ArrayList<Integer> nDimensions = new ArrayList<Integer>();
  private boolean inDimensionProperties = false;
  private boolean foundChannelTag = false;
  private int dimensionTag;

  private HashMap<Integer, byte[]> backgroundColor = new HashMap<Integer, byte[]>();

  private int metadataIndex = -1;
  private int previousTag = 0;

  private ArrayList<Pyramid> pyramids = new ArrayList<Pyramid>();

  private transient boolean expectETS = false;
  private transient int channelCount = 0;
  private transient int zCount = 0;

  // -- Constructor --

  /** Constructs a new cellSens reader. */
  public CellSensReader() {
    super("CellSens VSI", new String[] {"vsi", "ets"});
    domains = new String[] {FormatTools.HISTOLOGY_DOMAIN};
    suffixSufficient = true;
    datasetDescription = "One .vsi file and an optional directory with a " +
      "similar name that contains at least one subdirectory with .ets files";
  }

  // -- CellSensReader API methods --


  public boolean failOnMissingETS() {
    MetadataOptions options = getMetadataOptions();
    if (options instanceof DynamicMetadataOptions) {
      return ((DynamicMetadataOptions) options).getBoolean(
       FAIL_ON_MISSING_KEY, FAIL_ON_MISSING_DEFAULT);
    }
    return FAIL_ON_MISSING_DEFAULT;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);

    // all files contain pixels
    return noPixels ? null : usedFiles;
  }

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    if (getCoreIndex() < core.size() - 1 && getCoreIndex() < tileX.size()) {
      return tileX.get(getCoreIndex());
    }
    try {
      return (int) ifds.get(getIFDIndex()).getTileWidth();
    }
    catch (FormatException e) {
      LOGGER.debug("Could not retrieve tile width", e);
    }
    return super.getOptimalTileWidth();
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    if (getCoreIndex() < core.size() - 1 && getCoreIndex() < tileY.size()) {
      return tileY.get(getCoreIndex());
    }
    try {
      return (int) ifds.get(getIFDIndex()).getTileLength();
    }
    catch (FormatException e) {
      LOGGER.debug("Could not retrieve tile height", e);
    }
    return super.getOptimalTileHeight();
  }

  /* @see loci.formats.IFormatHandler#openThumbBytes(int) */
  @Override
  public byte[] openThumbBytes(int no) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);

    int currentIndex = getCoreIndex();
    int thumbSize = getThumbSizeX() * getThumbSizeY() *
      FormatTools.getBytesPerPixel(getPixelType()) * getRGBChannelCount();

    if (getCoreIndex() >= fileMap.size() || usedFiles.length >= core.size()) {
      return super.openThumbBytes(no);
    }

    setCoreIndex(fileMap.size());
    byte[] thumb = FormatTools.openThumbBytes(this, 0);
    setCoreIndex(currentIndex);
    if (thumb.length == thumbSize) {
      return thumb;
    }
    return super.openThumbBytes(no);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    if (getCoreIndex() < core.size() - 1 && getCoreIndex() < rows.size()) {
      int tileRows = rows.get(getCoreIndex());
      int tileCols = cols.get(getCoreIndex());

      Region image = new Region(x, y, w, h);
      int outputRow = 0, outputCol = 0;
      Region intersection = null;

      byte[] tileBuf = null;
      int pixel =
        getRGBChannelCount() * FormatTools.getBytesPerPixel(getPixelType());
      int outputRowLen = w * pixel;

      for (int row=0; row<tileRows; row++) {
        for (int col=0; col<tileCols; col++) {
          int width = tileX.get(getCoreIndex());
          int height = tileY.get(getCoreIndex());
          Region tile = new Region(col * width, row * height, width, height);
          if (!tile.intersects(image)) {
            continue;
          }

          intersection = tile.intersection(image);
          int intersectionX = 0;

          if (tile.x < image.x) {
            intersectionX = image.x - tile.x;
          }

          tileBuf = decodeTile(no, row, col);

          int rowLen = pixel * (int) Math.min(intersection.width, width);

          int outputOffset = outputRow * outputRowLen + outputCol;
          for (int trow=0; trow<intersection.height; trow++) {
            int realRow = trow + intersection.y - tile.y;
            int inputOffset = pixel * (realRow * width + intersectionX);
            System.arraycopy(tileBuf, inputOffset, buf, outputOffset, rowLen);
            outputOffset += outputRowLen;
          }

          outputCol += rowLen;
        }

        if (intersection != null) {
          outputRow += intersection.height;
          outputCol = 0;
        }
      }

      return buf;
    }
    else {
      return parser.getSamples(ifds.get(getIFDIndex() + no), buf, x, y, w, h);
    }
  }

  /* @see loci.formats.IFormatReader#reopenFile() */
  public void reopenFile() throws IOException {
    super.reopenFile();
    parser = new TiffParser(currentId);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      if (parser != null && parser.getStream() != null) {
        parser.getStream().close();
      }
      parser = null;
      ifds = null;
      usedFiles = null;
      fileMap.clear();
      tileOffsets.clear();
      jpeg = false;
      rows.clear();
      cols.clear();
      compressionType.clear();
      tileX.clear();
      tileY.clear();
      tileMap.clear();
      nDimensions.clear();
      inDimensionProperties = false;
      foundChannelTag = false;
      dimensionTag = 0;
      backgroundColor.clear();
      metadataIndex = -1;
      previousTag = 0;
      expectETS = false;
      pyramids.clear();
      channelCount = 0;
      zCount = 0;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    if (!checkSuffix(id, "vsi")) {
      Location current = new Location(id).getAbsoluteFile();
      Location parent = current.getParentFile();
      parent = parent.getParentFile();
      Location grandparent = parent.getParentFile();
      String vsi = parent.getName();
      vsi = vsi.substring(1, vsi.length() - 1) + ".vsi";

      Location vsiFile = new Location(grandparent, vsi);
      if (!vsiFile.exists()) {
        throw new FormatException("Could not find .vsi file.");
      }
      else {
        id = vsiFile.getAbsolutePath();
      }
    }

    parser = new TiffParser(id);
    ifds = parser.getMainIFDs();

    try (RandomAccessInputStream vsi = new RandomAccessInputStream(id)) {
      vsi.order(parser.getStream().isLittleEndian());
      vsi.seek(8);
      readTags(vsi, false, "");
    }

    ArrayList<String> files = new ArrayList<String>();
    Location file = new Location(id).getAbsoluteFile();

    Location dir = file.getParentFile();

    String name = file.getName();
    name = name.substring(0, name.lastIndexOf("."));

    Location pixelsDir = new Location(dir, "_" + name + "_");
    String[] stackDirs = pixelsDir.list(true);
    if (stackDirs != null) {
      Arrays.sort(stackDirs);
      for (String f : stackDirs) {
        Location stackDir = new Location(pixelsDir, f);
        String[] pixelsFiles = stackDir.list(true);
        if (pixelsFiles != null) {
          Arrays.sort(pixelsFiles);
          for (String pixelsFile : pixelsFiles) {
            if (checkSuffix(pixelsFile, "ets")) {
              files.add(new Location(stackDir, pixelsFile).getAbsolutePath());
            }
          }
        }
      }
    }
    files.add(file.getAbsolutePath());
    usedFiles = files.toArray(new String[files.size()]);

    if (expectETS && files.size() == 1) {
      String message = "Missing expected .ets files in " + pixelsDir.getAbsolutePath();
      if (failOnMissingETS()) {
        throw new FormatException(message);
      }
      else {
        LOGGER.warn(message);
      }
    }
    else if (!expectETS && files.size() > 1) {
      LOGGER.warn(".ets files present but not expected");
    }

    int seriesCount = files.size();
    core.clear();
    int ignoredPyramids = 0;
    if (files.size() == 1) {
      for (Pyramid pyramid : pyramids) {
        if (!pyramid.name.equalsIgnoreCase("Overview")) {
          ignoredPyramids++;
        }
      }

      seriesCount = ifds.size();

      if (ifds.size() > 1) {
        if (ifds.get(1).getSamplesPerPixel() == 1) {
          seriesCount = 2;
          if (channelCount == 0 && zCount == 0) {
            channelCount = ifds.size() - 1;
          }
          else if (zCount > 0) {
            zCount /= 2;
            channelCount = (ifds.size() - 1) / zCount;
          }
        }
        else {
          if (ifds.size() > 2) {
            ifds.remove(2);
          }
          seriesCount = (int) Math.min(3, ifds.size());
        }
      }
    }

    IFDList exifs = parser.getExifIFDs();

    int index = 0;
    for (int s=0; s<seriesCount; s++) {
      CoreMetadata ms = new CoreMetadata();
      core.add(ms);

      if (s < files.size() - 1) {
        setCoreIndex(index);
        String ff = files.get(s);
        try (RandomAccessInputStream stream = new RandomAccessInputStream(ff)) {
            parseETSFile(stream, ff, s);
        }

        ms.littleEndian = compressionType.get(index) == RAW;
        ms.interleaved = ms.rgb;

        for (int q=1; q<ms.resolutionCount; q++) {
          int res = core.size() - ms.resolutionCount + q;
          core.get(res).littleEndian = ms.littleEndian;
          core.get(res).interleaved = ms.interleaved;
        }

        if (s == 0 && exifs.size() > 0) {
          IFD exif = exifs.get(0);

          int newX = exif.getIFDIntValue(IFD.PIXEL_X_DIMENSION);
          int newY = exif.getIFDIntValue(IFD.PIXEL_Y_DIMENSION);

          if (getSizeX() > newX || getSizeY() > newY) {
            ms.sizeX = newX;
            ms.sizeY = newY;
          }
        }
        index += ms.resolutionCount;

        if (s < pyramids.size()) {
          ms.seriesMetadata = pyramids.get(s).originalMetadata;
        }

        setCoreIndex(0);
        ms.dimensionOrder = "XYCZT";
      }
      else {
        IFD ifd = ifds.get(s - files.size() + 1);
        PhotoInterp p = ifd.getPhotometricInterpretation();
        int samples = ifd.getSamplesPerPixel();
        ms.rgb = samples > 1 || p == PhotoInterp.RGB;
        ms.sizeX = (int) ifd.getImageWidth();
        ms.sizeY = (int) ifd.getImageLength();
        ms.sizeT = 1;
        ms.sizeC = ms.rgb ? samples : 1;
        if (files.size() == 1 && channelCount > 0 &&
          channelCount < ifds.size() && s > 0)
        {
          ms.sizeC *= channelCount;
          ms.sizeZ = (ifds.size() - 1) / channelCount;
          ms.imageCount = ifds.size() - 1;
          ms.dimensionOrder = "XYZCT";
        }
        else {
          ms.sizeZ = 1;
          ms.imageCount = 1;
          ms.dimensionOrder = "XYCZT";
        }
        ms.littleEndian = ifd.isLittleEndian();
        ms.indexed = p == PhotoInterp.RGB_PALETTE &&
          (get8BitLookupTable() != null || get16BitLookupTable() != null);
        ms.pixelType = ifd.getPixelType();
        ms.interleaved = false;
        ms.falseColor = false;
        ms.thumbnail = s != 0;
        index++;
      }
      ms.metadataComplete = true;
    }

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    String instrument = MetadataTools.createLSID("Instrument", 0);
    store.setInstrumentID(instrument, 0);

    for (int i=0; i<pyramids.size(); i++) {
      Pyramid pyramid = pyramids.get(i);
      store.setObjectiveID(MetadataTools.createLSID("Objective", 0, i), 0, i);
      store.setObjectiveNominalMagnification(pyramid.magnification, 0, i);
      store.setObjectiveWorkingDistance(
        FormatTools.createLength(pyramid.workingDistance, UNITS.MICROMETER), 0, i);

      for (int q=0; q<pyramid.objectiveTypes.size(); q++) {
        if (pyramid.objectiveTypes.get(q) == 1) {
          store.setObjectiveModel(pyramid.objectiveNames.get(q), 0, i);
          break;
        }
      }
      store.setObjectiveLensNA(pyramid.numericalAperture, 0, i);

      store.setDetectorID(MetadataTools.createLSID("Detector", 0, i), 0, i);
      store.setDetectorOffset(pyramid.offset, 0, i);
      store.setDetectorGain(pyramid.gain, 0, i);

      for (int q=0; q<pyramid.deviceTypes.size(); q++) {
        if (pyramid.deviceTypes.get(q).equals("Camera")) {
          if (q < pyramid.deviceNames.size()) {
            store.setDetectorModel(pyramid.deviceNames.get(q), 0, i);
          }
          if (q < pyramid.deviceIDs.size()) {
            store.setDetectorSerialNumber(pyramid.deviceIDs.get(q), 0, i);
          }
          if (q < pyramid.deviceManufacturers.size()) {
            store.setDetectorManufacturer(pyramid.deviceManufacturers.get(q), 0, i);
          }
          store.setDetectorType(MetadataTools.getDetectorType("CCD"), 0, i);
          break;
        }
      }
    }

    int nextPyramid = 0;
    for (int i=0; i<core.size();) {
      setCoreIndex(i);
      Pyramid pyramid = null;
      if (!(ignoredPyramids > 0 &&
        i < (core.size() - (pyramids.size() - ignoredPyramids))) &&
        nextPyramid < pyramids.size())
      {
        pyramid = pyramids.get(nextPyramid++);
      }
      int ii = coreIndexToSeries(i);

      if (pyramid != null) {
        int nextPlane = 0;
        int effectiveSizeC = core.get(i).rgb ? 1 : core.get(i).sizeC;
        for (int c=0; c<effectiveSizeC; c++) {
          store.setDetectorSettingsID(
            MetadataTools.createLSID("Detector", 0, nextPyramid - 1), ii, c);
          store.setDetectorSettingsBinning(
            MetadataTools.getBinning(pyramid.binningX + "x" + pyramid.binningY), ii, c);

          if (c == 0) {
            store.setDetectorSettingsGain(pyramid.redGain, ii, c);
            store.setDetectorSettingsOffset(pyramid.redOffset, ii, c);
          }
          else if (c == 1) {
            store.setDetectorSettingsGain(pyramid.greenGain, ii, c);
            store.setDetectorSettingsOffset(pyramid.greenOffset, ii, c);
          }
          else if (c == 2) {
            store.setDetectorSettingsGain(pyramid.blueGain, ii, c);
            store.setDetectorSettingsOffset(pyramid.blueOffset, ii, c);
          }

          if (c < pyramid.channelNames.size()) {
            store.setChannelName(pyramid.channelNames.get(c), ii, c);
          }
          if (c < pyramid.channelWavelengths.size()) {
            int wave = pyramid.channelWavelengths.get(c).intValue();
            if (wave > 0) {
              store.setChannelEmissionWavelength(
                FormatTools.getEmissionWavelength((double) wave), ii, c);
            }
          }
          for (int z=0; z<core.get(i).sizeZ; z++) {
            for (int t=0; t<core.get(i).sizeT; t++) {
              nextPlane = getIndex(z, c, t);

              Long exp = pyramid.defaultExposureTime;
              if (c < pyramid.exposureTimes.size()) {
                exp = pyramid.exposureTimes.get(c);
              }
              if (exp != null) {
                store.setPlaneExposureTime(
                  FormatTools.createTime(exp / 1000000.0, UNITS.SECOND), ii, nextPlane);
              }
              store.setPlanePositionX(
                FormatTools.createLength(pyramid.originX, UNITS.MICROMETER), ii, nextPlane);
              store.setPlanePositionY(
                FormatTools.createLength(pyramid.originY, UNITS.MICROMETER), ii, nextPlane);
              if (z < pyramid.zValues.size()) {
                store.setPlanePositionZ(
                  FormatTools.createLength(pyramid.zValues.get(z),
                  UNITS.MICROMETER), ii, nextPlane);
              }
              else if (pyramid.zStart != null && pyramid.zIncrement != null) {
                store.setPlanePositionZ(
                  FormatTools.createLength(pyramid.zStart + (z * pyramid.zIncrement),
                  UNITS.MICROMETER), ii, nextPlane);
              }
            }
          }
        }
      }

      store.setImageInstrumentRef(instrument, ii);

      if (pyramid != null) {
        String imageName = pyramid.name;
        boolean duplicate = false;
        for (int q=0; q<pyramids.size(); q++) {
          if (q != (nextPyramid - 1) &&
            imageName.equals(pyramids.get(q).name))
          {
            duplicate = true;
            break;
          }
        }

        if (!imageName.equals("Overview") && !imageName.equals("Label") && duplicate) {
          imageName += " #" + ii;
        }
        if (imageName.equals("Overview") || imageName.equals("Label")) {
          imageName = imageName.toLowerCase();
        }
        store.setImageName(imageName, ii);
        store.setObjectiveSettingsID(MetadataTools.createLSID("Objective", 0, nextPyramid - 1), ii);
        store.setObjectiveSettingsRefractiveIndex(pyramid.refractiveIndex, ii);

        if (pyramid.physicalSizeX > 0) {
          store.setPixelsPhysicalSizeX(FormatTools.getPhysicalSizeX(pyramid.physicalSizeX), ii);
        }
        if (pyramid.physicalSizeY > 0) {
          store.setPixelsPhysicalSizeY(FormatTools.getPhysicalSizeY(pyramid.physicalSizeY), ii);
        }

        if (pyramid.acquisitionTime != null) {
          // acquisition time is stored in seconds
          store.setImageAcquisitionDate(new Timestamp(DateTools.convertDate(
            pyramid.acquisitionTime * 1000, DateTools.UNIX)), ii);
        }
      }
      else {
        store.setImageName("macro image", ii);
      }

      i += core.get(i).resolutionCount;
    }
    setCoreIndex(0);
  }

  // -- Helper methods --

  private int getTileSize() {
    int channels = getRGBChannelCount();
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int index = getCoreIndex();
    return bpp * channels * tileX.get(index) * tileY.get(index);
  }

  private byte[] decodeTile(int no, int row, int col)
    throws FormatException, IOException
  {
    if (tileMap.get(getCoreIndex()) == null) {
      return new byte[getTileSize()];
    }

    int[] zct = getZCTCoords(no);
    TileCoordinate t = new TileCoordinate(nDimensions.get(getCoreIndex()));
    t.coordinate[0] = col;
    t.coordinate[1] = row;

    int resIndex = getResolution();
    int pyramidIndex = getSeries();
    if (hasFlattenedResolutions()) {
      int index = 0;
      pyramidIndex = 0;
      for (int i=0; i<core.size(); ) {
        if (index + core.get(i).resolutionCount <= getSeries()) {
          index += core.get(i).resolutionCount;
          i += core.get(i).resolutionCount;
          pyramidIndex++;
        }
        else {
          resIndex = getSeries() - index;
          break;
        }
      }
    }

    Pyramid pyramid = pyramids.get(pyramidIndex);
    for (String dim : pyramid.dimensionOrdering.keySet()) {
      int index = pyramid.dimensionOrdering.get(dim) + 2;

      if (dim.equals("Z")) {
        t.coordinate[index] = zct[0];
      }
      else if (dim.equals("C")) {
        t.coordinate[index] = zct[1];
      }
      else if (dim.equals("T")) {
        t.coordinate[index] = zct[2];
      }
    }

    if (resIndex > 0) {
      t.coordinate[t.coordinate.length - 1] = resIndex;
    }

    ArrayList<TileCoordinate> map = tileMap.get(getCoreIndex());
    Integer index = map.indexOf(t);
    if (index == null || index < 0) {
      // fill in the tile with the stored background color
      // usually this is either black or white
      byte[] tile = new byte[getTileSize()];
      byte[] color = backgroundColor.get(getCoreIndex());
      if (color != null) {
        for (int q=0; q<getTileSize(); q+=color.length) {
          for (int i=0; i<color.length; i++) {
            tile[q + i] = color[i];
          }
        }
      }
      return tile;
    }

    Long offset = tileOffsets.get(getCoreIndex())[index];
    byte[] buf = null;
    IFormatReader reader = null;
    try (RandomAccessInputStream ets =
      new RandomAccessInputStream(fileMap.get(getCoreIndex()))) {
      ets.seek(offset);
      CodecOptions options = new CodecOptions();
      options.interleaved = isInterleaved();
      options.littleEndian = isLittleEndian();
      int tileSize = getTileSize();
      if (tileSize == 0) {
        tileSize = tileX.get(getCoreIndex()) * tileY.get(getCoreIndex()) * 10;
      }
      options.maxBytes = (int) (offset + tileSize);

      
      long end = index < tileOffsets.get(getCoreIndex()).length - 1 ?
        tileOffsets.get(getCoreIndex())[index + 1] : ets.length();

      String file = null;

      switch (compressionType.get(getCoreIndex())) {
        case RAW:
          buf = new byte[tileSize];
          ets.read(buf);
          break;
        case JPEG:
          Codec codec = new JPEGCodec();
          buf = codec.decompress(ets, options);
          break;
        case JPEG_2000:
          codec = new JPEG2000Codec();
          buf = codec.decompress(ets, options);
          break;
        case JPEG_LOSSLESS:
          codec = new LosslessJPEGCodec();
          buf = codec.decompress(ets, options);
          break;
        case PNG:
          file = "tile.png";
          reader = new APNGReader();
          reader = Memoizer.wrap(getMetadataOptions(), reader);
        case BMP:
          if (reader == null) {
            file = "tile.bmp";
            reader = new BMPReader();
            reader = Memoizer.wrap(getMetadataOptions(), reader);
          }
          byte[] b = new byte[(int) (end - offset)];
          ets.read(b);
          Location.mapFile(file, new ByteArrayHandle(b));
          reader.setId(file);
          buf = reader.openBytes(0);
          Location.mapFile(file, null);
          break;
      }
    } finally {
      if (reader != null) {
        reader.close();
      }
    }
    return buf;
  }

  private void parseETSFile(RandomAccessInputStream etsFile, String file, int s)
    throws FormatException, IOException
  {
    fileMap.put(core.size() - 1, file);

    etsFile.order(true);

    CoreMetadata ms = core.get(getCoreIndex());

    // read the volume header
    String magic = etsFile.readString(4).trim();
    if (!magic.equals("SIS")) {
      throw new FormatException("Unknown magic bytes: " + magic);
    }

    int headerSize = etsFile.readInt();
    int version = etsFile.readInt();
    nDimensions.add(etsFile.readInt());
    long additionalHeaderOffset = etsFile.readLong();
    int additionalHeaderSize = etsFile.readInt();
    etsFile.skipBytes(4); // reserved
    long usedChunkOffset = etsFile.readLong();
    int nUsedChunks = etsFile.readInt();
    etsFile.skipBytes(4); // reserved

    // read the additional header
    etsFile.seek(additionalHeaderOffset);

    String moreMagic = etsFile.readString(4).trim();
    if (!moreMagic.equals("ETS")) {
      throw new FormatException("Unknown magic bytes: " + moreMagic);
    }

    etsFile.skipBytes(4); // extra version number

    int pixelType = etsFile.readInt();
    ms.sizeC = etsFile.readInt();
    int colorspace = etsFile.readInt();
    compressionType.add(etsFile.readInt());
    int compressionQuality = etsFile.readInt();
    tileX.add(etsFile.readInt());
    tileY.add(etsFile.readInt());
    int tileZ = etsFile.readInt();
    etsFile.skipBytes(4 * 17); // pixel info hints

    byte[] color = new byte[
      ms.sizeC * FormatTools.getBytesPerPixel(convertPixelType(pixelType))];
    etsFile.read(color);

    backgroundColor.put(getCoreIndex(), color);

    etsFile.skipBytes(4 * 10 - color.length); // background color
    etsFile.skipBytes(4); // component order
    boolean usePyramid = etsFile.readInt() != 0;

    ms.rgb = ms.sizeC > 1;

    // read the used chunks

    etsFile.seek(usedChunkOffset);

    tileOffsets.add(new Long[nUsedChunks]);

    ArrayList<TileCoordinate> tmpTiles = new ArrayList<TileCoordinate>();

    for (int chunk=0; chunk<nUsedChunks; chunk++) {
      etsFile.skipBytes(4);
      int dimensions = nDimensions.get(nDimensions.size() - 1);
      TileCoordinate t = new TileCoordinate(dimensions);
      for (int i=0; i<dimensions; i++) {
        t.coordinate[i] = etsFile.readInt();
      }
      tileOffsets.get(tileOffsets.size() - 1)[chunk] = etsFile.readLong();
      int nBytes = etsFile.readInt();
      etsFile.skipBytes(4);

      tmpTiles.add(t);
    }

    int maxResolution = 0;

    if (usePyramid) {
      for (TileCoordinate t : tmpTiles) {
        if (t.coordinate[t.coordinate.length - 1] > maxResolution) {
          maxResolution = t.coordinate[t.coordinate.length - 1];
        }
      }
    }

    maxResolution++;

    int[] maxX = new int[maxResolution];
    int[] maxY = new int[maxResolution];
    int[] maxZ = new int[maxResolution];
    int[] maxC = new int[maxResolution];
    int[] maxT = new int[maxResolution];

    HashMap<String, Integer> dimOrder = pyramids.get(s).dimensionOrdering;

    for (TileCoordinate t : tmpTiles) {
      int resolution = usePyramid ? t.coordinate[t.coordinate.length - 1] : 0;

      Integer tv = dimOrder.get("T");
      Integer zv = dimOrder.get("Z");
      Integer cv = dimOrder.get("C");

      int tIndex = tv == null ? -1 : tv + 2;
      int zIndex = zv == null ? -1 : zv + 2;
      int cIndex = cv == null ? -1 : cv + 2;

      if (usePyramid && tIndex == t.coordinate.length - 1) {
        tv = null;
        tIndex = -1;
      }
      if (usePyramid && zIndex == t.coordinate.length - 1) {
        zv = null;
        zIndex = -1;
      }

      int upperLimit = usePyramid ? t.coordinate.length - 1 : t.coordinate.length;
      if ((tIndex < 0 || tIndex >= upperLimit) &&
        (zIndex < 0 || zIndex >= upperLimit) &&
        (cIndex < 0 || cIndex >= upperLimit))
      {
        tIndex--;
        zIndex--;
        cIndex--;
        if (dimOrder.containsKey("T")) {
          dimOrder.put("T", tIndex - 2);
        }
        if (dimOrder.containsKey("Z")) {
          dimOrder.put("Z", zIndex - 2);
        }
        if (dimOrder.containsKey("C")) {
          dimOrder.put("C", cIndex - 2);
        }
      }

      if (tv == null && zv == null) {
        if (t.coordinate.length > 4 && cv == null) {
          cIndex = 2;
          dimOrder.put("C", cIndex - 2);
        }

        if (t.coordinate.length > 4) {
          if (cv == null) {
            tIndex = 3;
          }
          else {
            tIndex = cIndex + 2;
          }
          if (tIndex < t.coordinate.length) {
            dimOrder.put("T", tIndex - 2);
          }
          else {
            tIndex = -1;
          }
        }

        if (t.coordinate.length > 5) {
          if (cv == null) {
            zIndex = 4;
          }
          else {
            zIndex = cIndex + 1;
          }
          if (zIndex < t.coordinate.length) {
            dimOrder.put("Z", zIndex - 2);
          }
          else {
            zIndex = -1;
          }
        }
      }

      if (t.coordinate[0] > maxX[resolution]) {
        maxX[resolution] = t.coordinate[0];
      }
      if (t.coordinate[1] > maxY[resolution]) {
        maxY[resolution] = t.coordinate[1];
      }

      if (tIndex >= 0 && t.coordinate[tIndex] > maxT[resolution]) {
        maxT[resolution] = t.coordinate[tIndex];
      }
      if (zIndex >= 0 && t.coordinate[zIndex] > maxZ[resolution]) {
        maxZ[resolution] = t.coordinate[zIndex];
      }
      if (cIndex >= 0 && t.coordinate[cIndex] > maxC[resolution]) {
        maxC[resolution] = t.coordinate[cIndex];
      }
    }

    if (pyramids.get(s).width != null) {
      ms.sizeX = pyramids.get(s).width;
    }
    if (pyramids.get(s).height != null) {
      ms.sizeY = pyramids.get(s).height;
    }
    ms.sizeZ = maxZ[0] + 1;
    if (maxC[0] > 0) {
      ms.sizeC *= (maxC[0] + 1);
    }
    ms.sizeT = maxT[0] + 1;
    if (ms.sizeZ == 0) {
      ms.sizeZ = 1;
    }
    ms.imageCount = ms.sizeZ * ms.sizeT;
    if (maxC[0] > 0) {
      ms.imageCount *= (maxC[0] + 1);
    }

    if (maxY[0] >= 1) {
      rows.add(maxY[0] + 1);
    }
    else {
      rows.add(1);
    }
    if (maxX[0] >= 1) {
      cols.add(maxX[0] + 1);
    }
    else {
      cols.add(1);
    }

    ArrayList<TileCoordinate> map = new ArrayList<TileCoordinate>();
    for (int i=0; i<tmpTiles.size(); i++) {
      map.add(tmpTiles.get(i));
    }
    tileMap.add(map);

    ms.pixelType = convertPixelType(pixelType);
    if (usePyramid) {
      int finalResolution = 1;
      int initialCoreSize = core.size();
      for (int i=1; i<maxResolution; i++) {
        CoreMetadata newResolution = new CoreMetadata(ms);

        int previousX = core.get(core.size() - 1).sizeX;
        int previousY = core.get(core.size() - 1).sizeY;
        int maxSizeX = tileX.get(tileX.size() - 1) * (maxX[i] < 1 ? 1 : maxX[i] + 1);
        int maxSizeY = tileY.get(tileY.size() - 1) * (maxY[i] < 1 ? 1 : maxY[i] + 1);

        newResolution.sizeX = previousX / 2;
        if (previousX % 2 == 1 && newResolution.sizeX < maxSizeX) {
          newResolution.sizeX++;
        }
        else if (newResolution.sizeX > maxSizeX) {
          newResolution.sizeX = maxSizeX;
        }
        newResolution.sizeY = previousY / 2;
        if (previousY % 2 == 1 && newResolution.sizeY < maxSizeY) {
          newResolution.sizeY++;
        }
        else if (newResolution.sizeY > maxSizeY) {
          newResolution.sizeY = maxSizeY;
        }
        newResolution.sizeZ = maxZ[i] + 1;
        if (maxC[i] > 0 && newResolution.sizeC != (maxC[i] + 1)) {
          newResolution.sizeC *= (maxC[i] + 1);
        }
        newResolution.sizeT = maxT[i] + 1;
        if (newResolution.sizeZ == 0) {
          newResolution.sizeZ = 1;
        }
        newResolution.imageCount = newResolution.sizeZ * newResolution.sizeT;
        if (maxC[i] > 0) {
          newResolution.imageCount *= (maxC[i] + 1);
        }

        newResolution.metadataComplete = true;
        newResolution.dimensionOrder = "XYCZT";

        core.add(newResolution);

        rows.add(maxY[i] >= 1 ? maxY[i] + 1 : 1);
        cols.add(maxX[i] >= 1 ? maxX[i] + 1 : 1);

        fileMap.put(core.size() - 1, file);
        finalResolution = core.size() - initialCoreSize + 1;

        tileX.add(tileX.get(tileX.size() - 1));
        tileY.add(tileY.get(tileY.size() - 1));
        compressionType.add(compressionType.get(compressionType.size() - 1));
        tileMap.add(map);
        nDimensions.add(nDimensions.get(nDimensions.size() - 1));
        tileOffsets.add(tileOffsets.get(tileOffsets.size() - 1));
        backgroundColor.put(core.size() - 1, color);
      }

      ms.resolutionCount = finalResolution;
    }
  }

  private int convertPixelType(int pixelType) throws FormatException {
    switch (pixelType) {
      case CHAR:
        return FormatTools.INT8;
      case UCHAR:
        return FormatTools.UINT8;
      case SHORT:
        return FormatTools.INT16;
      case USHORT:
        return FormatTools.UINT16;
      case INT:
        return FormatTools.INT32;
      case UINT:
        return FormatTools.UINT32;
      case LONG:
        throw new FormatException("Unsupported pixel type: long");
      case ULONG:
        throw new FormatException("Unsupported pixel type: unsigned long");
      case FLOAT:
        return FormatTools.FLOAT;
      case DOUBLE:
        return FormatTools.DOUBLE;
      default:
        throw new FormatException("Unsupported pixel type: " + pixelType);
    }
  }

  private void readTags(RandomAccessInputStream vsi, boolean populateMetadata, String tagPrefix) {
    try {
      // read the VSI header
      long fp = vsi.getFilePointer();
      if (fp + 24 >= vsi.length()) {
        return;
      }
      LOGGER.debug("reading tag container data from {}", vsi.getFilePointer());
      int headerSize = vsi.readShort(); // should always be 24
      int version = vsi.readShort(); // always 21321
      int volumeVersion = vsi.readInt();
      long dataFieldOffset = vsi.readLong();
      int flags = vsi.readInt();
      vsi.skipBytes(4);
      LOGGER.debug("  headerSize = {}", headerSize);
      LOGGER.debug("  version = {}", version);
      LOGGER.debug("  volumeVersion = {}", volumeVersion);
      LOGGER.debug("  dataFieldOffset = {}", dataFieldOffset);
      LOGGER.debug("  flags = {}", flags);

      int tagCount = flags & 0xfffffff;

      if (fp + dataFieldOffset < 0) {
        return;
      }

      vsi.seek(fp + dataFieldOffset);
      if (vsi.getFilePointer() >= vsi.length()) {
        return;
      }

      LOGGER.debug("parsing {} tags from {}", tagCount, vsi.getFilePointer());

      if (tagCount > vsi.length()) {
        return;
      }

      for (int i=0; i<tagCount; i++) {
        if (vsi.getFilePointer() + 16 >= vsi.length()) {
          break;
        }

        // read the data field

        int fieldType = vsi.readInt();
        int tag = vsi.readInt();
        long nextField = vsi.readInt() & 0xffffffffL;
        int dataSize = vsi.readInt();
        String storedValue = null;

        LOGGER.debug("  tag #{}: fieldType={}, tag={}, nextField={}, dataSize={}",
          new Object[] {i, fieldType, tag, nextField, dataSize});

        boolean extraTag = ((fieldType & 0x8000000) >> 27) == 1;
        boolean extendedField = ((fieldType & 0x10000000) >> 28) == 1;
        boolean inlineData = ((fieldType & 0x40000000) >> 30) == 1;
        boolean array = (!inlineData && !extendedField) &&
          ((fieldType & 0x20000000) >> 29) == 1;
        boolean newVolume = ((fieldType & 0x80000000) >> 31) == 1;

        int realType = fieldType & 0xffffff;
        int secondTag = -1;

        if (extraTag) {
          secondTag = vsi.readInt();
        }
        LOGGER.debug("  inlineData = {}", inlineData);
        LOGGER.debug("  extraTag = {}", extraTag);
        LOGGER.debug("  extendedField = {}", extendedField);
        LOGGER.debug("  realType = {}", realType);

        if (tag < 0) {
          if (!inlineData && dataSize + vsi.getFilePointer() < vsi.length()) {
            vsi.skipBytes(dataSize);
          }
          return;
        }

        if (tag == EXTERNAL_FILE_PROPERTIES && previousTag == IMAGE_FRAME_VOLUME) {
          metadataIndex++;
        }
        else if (tag == DOCUMENT_PROPERTIES || tag == SLIDE_PROPERTIES) {
          metadataIndex = -1;
        }

        previousTag = tag;

        while (metadataIndex >= pyramids.size()) {
          pyramids.add(new Pyramid());
        }

        if (extendedField && realType == NEW_VOLUME_HEADER) {
          if (tag == DIMENSION_DESCRIPTION_VOLUME) {
            dimensionTag = secondTag;
            inDimensionProperties = true;
          }
          long endPointer = vsi.getFilePointer() + dataSize;
          while (vsi.getFilePointer() < endPointer &&
            vsi.getFilePointer() < vsi.length())
          {
            long start = vsi.getFilePointer();
            readTags(vsi, populateMetadata || inDimensionProperties, getVolumeName(tag));
            long end = vsi.getFilePointer();
            if (start >= end) {
              break;
            }
          }
          if (tag == DIMENSION_DESCRIPTION_VOLUME) {
            inDimensionProperties = false;
            foundChannelTag = false;
          }
        }
        else if (extendedField && (realType == PROPERTY_SET_VOLUME ||
          realType == NEW_MDIM_VOLUME_HEADER))
        {
          long start = vsi.getFilePointer();
          String tagName = realType == NEW_MDIM_VOLUME_HEADER ?
            getVolumeName(tag) : tagPrefix;
          if (tagName.isEmpty() && realType == NEW_MDIM_VOLUME_HEADER) {
            switch (tag) {
              case Z_START:
                tagName = "Z start position";
                break;
              case Z_INCREMENT:
                tagName = "Z increment";
                break;
              case Z_VALUE:
                tagName = "Z value";
                break;
            }
          }
          readTags(vsi, tag != 2037, tagName);
        }
        else {
          String tagName = getTagName(tag);
          String value = inlineData ? String.valueOf(dataSize) : " ";

          Pyramid pyramid =
            metadataIndex < 0 ? null : pyramids.get(metadataIndex);

          if (!inlineData && dataSize > 0) {
            switch (realType) {
              case CHAR:
              case UCHAR:
                value = String.valueOf(vsi.read());
                break;
              case SHORT:
              case USHORT:
                value = String.valueOf(vsi.readShort());
                break;
              case INT:
              case UINT:
              case DWORD:
              case FIELD_TYPE:
              case MEM_MODEL:
              case COLOR_SPACE:
                int intValue = vsi.readInt();
                value = String.valueOf(intValue);
                break;
              case LONG:
              case ULONG:
              case TIMESTAMP:
                long longValue = vsi.readLong();
                value = String.valueOf(longValue);
                break;
              case FLOAT:
                value = String.valueOf(vsi.readFloat());
                break;
              case DOUBLE:
              case DATE:
                value = String.valueOf(vsi.readDouble());
                break;
              case BOOLEAN:
                value = new Boolean(vsi.readBoolean()).toString();
                break;
              case TCHAR:
              case UNICODE_TCHAR:
                value = vsi.readString(dataSize);
                value = DataTools.stripString(value);

                if (tag == CHANNEL_NAME) {
                  if (pyramid != null) {
                    pyramid.channelNames.add(value);
                  }
                }
                else if (tag == STACK_NAME && !value.equals("0")) {
                  if (pyramid != null && pyramid.name == null) {
                    pyramid.name = value;
                  }
                }
                break;
              case INT_2:
              case INT_INTERVAL:
              case INT_ARRAY_2:
              case INT_3:
              case INT_ARRAY_3:
              case INT_4:
              case INT_RECT:
              case INT_ARRAY_4:
              case INT_ARRAY_5:
              case DIM_INDEX_1:
              case DIM_INDEX_2:
              case VOLUME_INDEX:
              case PIXEL_INFO_TYPE:
                int nIntValues = dataSize / 4;
                int[] intValues = new int[nIntValues];
                StringBuilder sb = new StringBuilder();
                if (nIntValues > 1) {
                  sb.append("(");
                }
                for (int v=0; v<nIntValues; v++) {
                  intValues[v] = vsi.readInt();
                  sb.append(intValues[v]);
                  if (v < nIntValues - 1) {
                    sb.append(", ");
                  }
                }
                if (nIntValues > 1) {
                  sb.append(")");
                }
                value = sb.toString();

                if (tag == IMAGE_BOUNDARY) {
                  if (pyramid != null && pyramid.width == null) {
                    pyramid.width = intValues[2];
                    pyramid.height = intValues[3];
                  }
                }
                break;
              case COMPLEX:
              case DOUBLE_2:
              case DOUBLE_INTERVAL:
              case DOUBLE_ARRAY_2:
              case DOUBLE_3:
              case DOUBLE_ARRAY_3:
              case DOUBLE_4:
              case DOUBLE_RECT:
              case DOUBLE_2_2:
              case DOUBLE_3_3:
              case DOUBLE_4_4:
                int nDoubleValues = dataSize / 8;
                double[] doubleValues = new double[nDoubleValues];
                sb = new StringBuilder();
                if (nDoubleValues > 1) {
                  sb.append("(");
                }
                for (int v=0; v<nDoubleValues; v++) {
                  doubleValues[v] = vsi.readDouble();
                  sb.append(doubleValues[v]);
                  if (v < nDoubleValues - 1) {
                    sb.append(", ");
                  }
                }
                if (nDoubleValues > 1) {
                  sb.append(')');
                }
                value = sb.toString();

                if (tag == RWC_FRAME_SCALE) {
                  if (pyramid != null && pyramid.physicalSizeX == null) {
                    pyramid.physicalSizeX = doubleValues[0];
                    pyramid.physicalSizeY = doubleValues[1];
                  }
                }
                else if (tag == RWC_FRAME_ORIGIN) {
                  if (pyramid != null && pyramid.originX == null) {
                    pyramid.originX = doubleValues[0];
                    pyramid.originY = doubleValues[1];
                  }
                }
                break;
              case RGB:
                int red = vsi.read();
                int green = vsi.read();
                int blue = vsi.read();
                value = "red = " + red + ", green = " + green + ", blue = " + blue;
                break;
              case BGR:
                blue = vsi.read();
                green = vsi.read();
                red = vsi.read();
                value = "red = " + red + ", green = " + green + ", blue = " + blue;
                break;
            }
          }

          if (metadataIndex >= 0) {
            try {
              if (tag == STACK_TYPE) {
                value = getStackType(value);
              }
              else if (tag == DEVICE_SUBTYPE) {
                value = getDeviceSubtype(value);
                pyramid.deviceTypes.add(value);
              }
              else if (tag == DEVICE_ID) {
                pyramid.deviceIDs.add(value);
              }
              else if (tag == DEVICE_NAME) {
                pyramid.deviceNames.add(value);
              }
              else if (tag == DEVICE_MANUFACTURER) {
                pyramid.deviceManufacturers.add(value);
              }
              else if (tag == EXPOSURE_TIME && tagPrefix.length() == 0) {
                pyramid.exposureTimes.add(new Long(value));
              }
              else if (tag == EXPOSURE_TIME) {
                pyramid.defaultExposureTime = new Long(value);
              }
              else if (tag == CREATION_TIME && pyramid.acquisitionTime == null) {
                pyramid.acquisitionTime = new Long(value);
              }
              else if (tag == REFRACTIVE_INDEX) {
                pyramid.refractiveIndex = new Double(value);
              }
              else if (tag == OBJECTIVE_MAG) {
                pyramid.magnification = new Double(value);
              }
              else if (tag == NUMERICAL_APERTURE) {
                pyramid.numericalAperture = new Double(value);
              }
              else if (tag == WORKING_DISTANCE) {
                pyramid.workingDistance = new Double(value);
              }
              else if (tag == OBJECTIVE_NAME) {
                pyramid.objectiveNames.add(value);
              }
              else if (tag == OBJECTIVE_TYPE) {
                pyramid.objectiveTypes.add(new Integer(value));
              }
              else if (tag == BIT_DEPTH) {
                pyramid.bitDepth = new Integer(value);
              }
              else if (tag == X_BINNING) {
                pyramid.binningX = new Integer(value);
              }
              else if (tag == Y_BINNING) {
                pyramid.binningY = new Integer(value);
              }
              else if (tag == CAMERA_GAIN) {
                pyramid.gain = new Double(value);
              }
              else if (tag == CAMERA_OFFSET) {
                pyramid.offset = new Double(value);
              }
              else if (tag == RED_GAIN) {
                pyramid.redGain = new Double(value);
              }
              else if (tag == GREEN_GAIN) {
                pyramid.greenGain = new Double(value);
              }
              else if (tag == BLUE_GAIN) {
                pyramid.blueGain = new Double(value);
              }
              else if (tag == RED_OFFSET) {
                pyramid.redOffset = new Double(value);
              }
              else if (tag == GREEN_OFFSET) {
                pyramid.greenOffset = new Double(value);
              }
              else if (tag == BLUE_OFFSET) {
                pyramid.blueOffset = new Double(value);
              }
              else if (tag == VALUE) {
                if (tagPrefix.equals("Channel Wavelength ")) {
                  pyramid.channelWavelengths.add(new Double(value));
                }
                else if (tagPrefix.startsWith("Objective Working Distance")) {
                  pyramid.workingDistance = new Double(value);
                }
                else if (tagPrefix.equals("Z start position")) {
                  pyramid.zStart = DataTools.parseDouble(value);
                }
                else if (tagPrefix.equals("Z increment")) {
                  pyramid.zIncrement = DataTools.parseDouble(value);
                }
                else if (tagPrefix.equals("Z value")) {
                  pyramid.zValues.add(DataTools.parseDouble(value));
                }
              }
            }
            catch (NumberFormatException e) {
              LOGGER.debug("Could not parse tag " + tag, e);
            }
          }

          if (tag == DOCUMENT_TIME || tag == CREATION_TIME) {
            value = DateTools.convertDate(
              Long.parseLong(value) * 1000, DateTools.UNIX);
          }

          if (tag == HAS_EXTERNAL_FILE) {
            expectETS = Integer.parseInt(value) == 1;
          }

          if (tagName != null && populateMetadata) {
            if (metadataIndex >= 0) {
              addMetaList(tagPrefix + tagName, value,
                pyramids.get(metadataIndex).originalMetadata);
            }
            else if (tag != VALUE || tagPrefix.length() > 0) {
              addGlobalMetaList(tagPrefix + tagName, value);
            }
            if ("Channel Wavelength Value".equals(tagPrefix + tagName)) {
              channelCount++;
            }
            else if ("Z valueValue".equals(tagPrefix + tagName)) {
              zCount++;
            }
          }
          storedValue = value;
        }

        if (inDimensionProperties) {
          Pyramid p = pyramids.get(metadataIndex);
          if (tag == Z_START && !p.dimensionOrdering.containsValue(dimensionTag)) {
            p.dimensionOrdering.put("Z", dimensionTag);
          }
          else if ((tag == TIME_START || tag == DIMENSION_VALUE_ID) &&
            !p.dimensionOrdering.containsValue(dimensionTag))
          {
            p.dimensionOrdering.put("T", dimensionTag);
          }
          else if (tag == LAMBDA_START &&
            !p.dimensionOrdering.containsValue(dimensionTag))
          {
            p.dimensionOrdering.put("L", dimensionTag);
          }
          else if (tag == CHANNEL_PROPERTIES && foundChannelTag &&
            !p.dimensionOrdering.containsValue(dimensionTag))
          {
            p.dimensionOrdering.put("C", dimensionTag);
          }
          else if (tag == CHANNEL_PROPERTIES) {
            foundChannelTag = true;
          }
          else if (tag == DIMENSION_MEANING && storedValue != null) {
            int dimension = -1;
            try {
              dimension = Integer.parseInt(storedValue);
            }
            catch (NumberFormatException e) { }
            switch (dimension) {
              case Z:
                p.dimensionOrdering.put("Z", dimensionTag);
                break;
              case T:
                p.dimensionOrdering.put("T", dimensionTag);
                break;
              case LAMBDA:
                p.dimensionOrdering.put("L", dimensionTag);
                break;
              case C:
                p.dimensionOrdering.put("C", dimensionTag);
                break;
              case PHASE:
                p.dimensionOrdering.put("P", dimensionTag);
              default:
                throw new FormatException("Invalid dimension: " + dimension);
            }
          }
        }

        if (nextField == 0 || tag == -494804095) {
          if (fp + dataSize < vsi.length() && fp + dataSize >= 0) {
            vsi.seek(fp + dataSize + 32);
          }
          return;
        }

        if (fp + nextField < vsi.length() && fp + nextField >= 0) {
          vsi.seek(fp + nextField);
        }
        else break;
      }
    }
    catch (Exception e) {
      LOGGER.debug("Failed to read all tags", e);
    }
  }

  private String getVolumeName(int tag) {
    switch (tag) {
      case COLLECTION_VOLUME:
      case MULTIDIM_IMAGE_VOLUME:
      case IMAGE_FRAME_VOLUME:
      case DIMENSION_SIZE:
      case IMAGE_COLLECTION_PROPERTIES:
      case MULTIDIM_STACK_PROPERTIES:
      case FRAME_PROPERTIES:
      case DIMENSION_DESCRIPTION_VOLUME:
      case CHANNEL_PROPERTIES:
      case DISPLAY_MAPPING_VOLUME:
      case LAYER_INFO_PROPERTIES:
        return "";
      case OPTICAL_PATH:
        return "Microscope ";
      case 2417:
        return "Channel Wavelength ";
      case WORKING_DISTANCE:
        return "Objective Working Distance ";
    }
    LOGGER.debug("Unhandled volume {}", tag);
    return "";
  }

  private String getTagName(int tag) {
    switch (tag) {
      case Y_PLANE_DIMENSION_UNIT:
        return "Image plane rectangle unit (Y dimension)";
      case Y_DIMENSION_UNIT:
        return "Y dimension unit";
      case CHANNEL_OVERFLOW:
        return "Channel under/overflow";
      case SLIDE_SPECIMEN:
        return "Specimen";
      case SLIDE_TISSUE:
        return "Tissue";
      case SLIDE_PREPARATION:
        return "Preparation";
      case SLIDE_STAINING:
        return "Staining";
      case SLIDE_INFO:
        return "Slide Info";
      case SLIDE_NAME:
        return "Slide Name";
      case EXPOSURE_TIME:
        return "Exposure time (microseconds)";
      case CAMERA_GAIN:
        return "Camera gain";
      case CAMERA_OFFSET:
        return "Camera offset";
      case CAMERA_GAMMA:
        return "Gamma";
      case SHARPNESS:
        return "Sharpness";
      case RED_GAIN:
        return "Red channel gain";
      case GREEN_GAIN:
        return "Green channel gain";
      case BLUE_GAIN:
        return "Blue channel gain";
      case RED_OFFSET:
        return "Red channel offset";
      case GREEN_OFFSET:
        return "Green channel offset";
      case BLUE_OFFSET:
        return "Blue channel offset";
      case SHADING_SUB:
        return "Shading sub";
      case SHADING_MUL:
        return "Shading mul";
      case X_BINNING:
        return "Binning (X)";
      case Y_BINNING:
        return "Binning (Y)";
      case CLIPPING:
        return "Clipping";
      case MIRROR_H:
        return "Mirror (horizontal)";
      case MIRROR_V:
        return "Mirror (vertical)";
      case CLIPPING_STATE:
        return "Clipping state";
      case ICC_ENABLED:
        return "ICC enabled";
      case BRIGHTNESS:
        return "Brightness";
      case CONTRAST:
        return "Contrast";
      case CONTRAST_TARGET:
        return "Contrast reference";
      case ACCUMULATION:
        return "Camera accumulation";
      case AVERAGING:
        return "Camera averaging";
      case ISO_SENSITIVITY:
        return "ISO sensitivity";
      case ACCUMULATION_MODE:
        return "Camera accumulation mode";
      case AUTOEXPOSURE:
        return "Autoexposure enabled";
      case EXPOSURE_METERING_MODE:
        return "Autoexposure metering mode";
      case Z_START:
        return "Z stack start";
      case Z_INCREMENT:
        return "Z stack increment";
      case Z_VALUE:
        return "Z position";
      case TIME_START:
        return "Timelapse start";
      case TIME_INCREMENT:
        return "Timelapse increment";
      case TIME_VALUE:
        return "Timestamp";
      case LAMBDA_START:
        return "Lambda start";
      case LAMBDA_INCREMENT:
        return "Lambda increment";
      case LAMBDA_VALUE:
        return "Lambda value";
      case DIMENSION_NAME:
        return "Dimension name";
      case DIMENSION_MEANING:
        return "Dimension description";
      case DIMENSION_START_ID:
        return "Dimension start ID";
      case DIMENSION_INCREMENT_ID:
        return "Dimension increment ID";
      case DIMENSION_VALUE_ID:
        return "Dimension value ID";
      case IMAGE_BOUNDARY:
        return "Image size";
      case TILE_SYSTEM:
        return "Tile system";
      case HAS_EXTERNAL_FILE:
        return "External file present";
      case EXTERNAL_DATA_VOLUME:
        return "External file volume";
      case TILE_ORIGIN:
        return "Origin of tile coordinate system";
      case DISPLAY_LIMITS:
        return "Display limits";
      case STACK_DISPLAY_LUT:
        return "Stack display LUT";
      case GAMMA_CORRECTION:
        return "Gamma correction";
      case FRAME_ORIGIN:
        return "Frame origin (plane coordinates)";
      case FRAME_SCALE:
        return "Frame scale (plane coordinates)";
      case DISPLAY_COLOR:
        return "Display color";
      case CREATION_TIME:
        return "Creation time (UTC)";
      case RWC_FRAME_ORIGIN:
        return "Origin";
      case RWC_FRAME_SCALE:
        return "Calibration";
      case RWC_FRAME_UNIT:
        return "Calibration units";
      case STACK_NAME:
        return "Layer";
      case CHANNEL_DIM:
        return "Channel dimension";
      case STACK_TYPE:
        return "Image Type";
      case LIVE_OVERFLOW:
        return "Live overflow";
      case IS_TRANSMISSION:
        return "IS transmission mask";
      case CONTRAST_BRIGHTNESS:
        return "Contrast and brightness";
      case ACQUISITION_PROPERTIES:
        return "Acquisition properties";
      case GRADIENT_LUT:
        return "Gradient LUT";
      case DISPLAY_PROCESSOR_TYPE:
        return "Display processor type";
      case RENDER_OPERATION_ID:
        return "Render operation ID";
      case DISPLAY_STACK_ID:
        return "Displayed stack ID";
      case TRANSPARENCY_ID:
        return "Transparency ID";
      case THIRD_ID:
        return "Display third ID";
      case DISPLAY_VISIBLE:
        return "Display visible";
      case TRANSPARENCY_VALUE:
        return "Transparency value";
      case DISPLAY_LUT:
        return "Display LUT";
      case DISPLAY_STACK_INDEX:
        return "Display stack index";
      case CHANNEL_TRANSPARENCY_VALUE:
        return "Channel transparency value";
      case CHANNEL_VISIBLE:
        return "Channel visible";
      case SELECTED_CHANNELS:
        return "List of selected channels";
      case DISPLAY_GAMMA_CORRECTION:
        return "Display gamma correction";
      case CHANNEL_GAMMA_CORRECTION:
        return "Channel gamma correction";
      case DISPLAY_CONTRAST_BRIGHTNESS:
        return "Display contrast and brightness";
      case CHANNEL_CONTRAST_BRIGHTNESS:
        return "Channel contrast and brightness";
      case ACTIVE_STACK_DIMENSION:
        return "Active stack dimension";
      case SELECTED_FRAMES:
        return "Selected frames";
      case DISPLAYED_LUT_ID:
        return "Displayed LUT ID";
      case HIDDEN_LAYER:
        return "Hidden layer";
      case LAYER_XY_FIXED:
        return "Layer fixed in XY";
      case ACTIVE_LAYER_VECTOR:
        return "Active layer vector";
      case ACTIVE_LAYER_INDEX_VECTOR:
        return "Active layer index vector";
      case CHAINED_LAYERS:
        return "Chained layers";
      case LAYER_SELECTION:
        return "Layer selection";
      case LAYER_SELECTION_INDEX:
        return "Layer selection index";
      case CANVAS_COLOR_1:
        return "Canvas background color 1";
      case CANVAS_COLOR_2:
        return "Canvas background color 2";
      case ORIGINAL_FRAME_RATE:
        return "Original frame rate (ms)";
      case USE_ORIGINAL_FRAME_RATE:
        return "Use original frame rate";
      case ACTIVE_CHANNEL:
        return "Active channel";
      case PLANE_UNIT:
        return "Plane unit";
      case PLANE_ORIGIN_RWC:
        return "Origin";
      case PLANE_SCALE_RWC:
        return "Physical pixel size";
      case MAGNIFICATION:
        return "Original magnification";
      case DOCUMENT_NAME:
        return "Document Name";
      case DOCUMENT_NOTE:
        return "Document Note";
      case DOCUMENT_TIME:
        return "Document Creation Time";
      case DOCUMENT_AUTHOR:
        return "Document Author";
      case DOCUMENT_COMPANY:
        return "Document Company";
      case DOCUMENT_CREATOR_NAME:
        return "Document creator name";
      case DOCUMENT_CREATOR_MAJOR_VERSION:
        return "Document creator major version";
      case DOCUMENT_CREATOR_MINOR_VERSION:
        return "Document creator minor version";
      case DOCUMENT_CREATOR_SUB_VERSION:
        return "Document creator sub version";
      case DOCUMENT_CREATOR_BUILD_NUMBER:
        return "Product Build Number";
      case DOCUMENT_CREATOR_PACKAGE:
        return "Document creator package";
      case DOCUMENT_PRODUCT:
        return "Document product";
      case DOCUMENT_PRODUCT_NAME:
        return "Document product name";
      case DOCUMENT_PRODUCT_VERSION:
        return "Document product version";
      case DOCUMENT_TYPE_HINT:
        return "Document type hint";
      case DOCUMENT_THUMB:
        return "Document thumbnail";
      case COARSE_PYRAMID_LEVEL:
        return "Coarse pyramid level";
      case EXTRA_SAMPLES:
        return "Extra samples";
      case DEFAULT_BACKGROUND_COLOR:
        return "Default background color";
      case VERSION_NUMBER:
        return "Version number";
      case CHANNEL_NAME:
        return "Channel name";
      case OBJECTIVE_MAG:
        return "Magnification";
      case NUMERICAL_APERTURE:
        return "Numerical Aperture";
      case WORKING_DISTANCE:
        return "Objective Working Distance";
      case OBJECTIVE_NAME:
        return "Objective Name";
      case OBJECTIVE_TYPE:
        return "Objective Type";
      case 120065:
        return "Objective Description";
      case 120066:
        return "Objective Subtype";
      case 120069:
        return "Brightness Correction";
      case 120070:
        return "Objective Lens";
      case 120075:
        return "Objective X Shift";
      case 120076:
        return "Objective Y Shift";
      case 120077:
        return "Objective Z Shift";
      case 120078:
        return "Objective Gear Setting";
      case 120635:
        return "Slide Bar Code";
      case 120638:
        return "Tray No.";
      case 120637:
        return "Slide No.";
      case 34:
        return "Product Name";
      case 35:
        return "Product Version";
      case DEVICE_NAME:
        return "Device Name";
      case BIT_DEPTH:
        return "Camera Actual Bit Depth";
      case 120001:
        return "Device Position";
      case 120050:
        return "TV Adapter Magnification";
      case REFRACTIVE_INDEX:
        return "Objective Refractive Index";
      case 120117:
        return "Device Type";
      case DEVICE_ID:
        return "Device Unit ID";
      case DEVICE_SUBTYPE:
        return "Device Subtype";
      case 120132:
        return "Device Model";
      case DEVICE_MANUFACTURER:
        return "Device Manufacturer";
      case 121102:
        return "Stage Insert Position";
      case 121131:
        return "Laser/Lamp Intensity";
      case 268435456:
        return "Units";
      case VALUE:
        return "Value";
      case 175208:
        return "Snapshot Count";
      case 175209:
        return "Scanning Time (seconds)";
      case 120210:
        return "Device Configuration Position";
      case 120211:
        return "Device Configuration Index";
      case 124000:
        return "Aperture Max Mode";
      case FRAME_SIZE:
        return "Camera Maximum Frame Size";
      case HDRI_ON:
        return "Camera HDRI Enabled";
      case HDRI_FRAMES:
        return "Camera Images per HDRI image";
      case HDRI_EXPOSURE_RANGE:
        return "Camera HDRI Exposure Ratio";
      case HDRI_MAP_MODE:
        return "Camera HDRI Mapping Mode";
      case CUSTOM_GRAYSCALE:
        return "Camera Custom Grayscale Value";
      case SATURATION:
        return "Camera Saturation";
      case WB_PRESET_ID:
        return "Camera White Balance Preset ID";
      case WB_PRESET_NAME:
        return "Camera White Balance Preset Name";
      case WB_MODE:
        return "Camera White Balance Mode";
      case CCD_SENSITIVITY:
        return "Camera CCD Sensitivity";
      case ENHANCED_DYNAMIC_RANGE:
        return "Camera Enhanced Dynamic Range";
      case PIXEL_CLOCK:
        return "Camera Pixel Clock (MHz)";
      case COLORSPACE:
        return "Camera Colorspace";
      case COOLING_ON:
        return "Camera Cooling Enabled";
      case FAN_SPEED:
        return "Camera Cooling Fan Speed";
      case TEMPERATURE_TARGET:
        return "Camera Cooling Temperature Target";
      case GAIN_UNIT:
        return "Camera Gain Unit";
      case EM_GAIN:
        return "Camera EM Gain";
      case PHOTON_IMAGING_MODE:
        return "Camera Photon Imaging Mode";
      case FRAME_TRANSFER:
        return "Camera Frame Transfer Enabled";
      case ANDOR_SHIFT_SPEED:
        return "Camera iXon Shift Speed";
      case VCLOCK_AMPLITUDE:
        return "Camera Vertical Clock Amplitude";
      case SPURIOUS_NOISE_REMOVAL:
        return "Camera Spurious Noise Removal Enabled";
      case SIGNAL_OUTPUT:
        return "Camera Signal Output";
      case BASELINE_OFFSET_CLAMP:
        return "Camera Baseline Offset Clamp";
      case DP80_FRAME_CENTERING:
        return "Camera DP80 Frame Centering";
      case HOT_PIXEL_CORRECTION:
        return "Camera Hot Pixel Correction Enabled";
      case NOISE_REDUCTION:
        return "Camera Noise Reduction";
      case WIDER:
        return "Camera WiDER";
      case PHOTOBLEACHING:
        return "Camera Photobleaching Enabled";
      case PREAMP_GAIN_VALUE:
        return "Camera Preamp Gain";
      case WIDER_ENABLED:
        return "Camera WiDER Enabled";
    }
    LOGGER.debug("Unhandled tag {}", tag);
    return null;
  }

  private String getDeviceSubtype(String type) {
    int deviceType = Integer.parseInt(type);
    switch (deviceType) {
      case 0:
        return "Camera";
      case 10000:
        return "Stage";
      case 20000:
        return "Objective revolver";
      case 20001:
        return "TV Adapter";
      case 20002:
        return "Filter Wheel";
      case 20003:
        return "Lamp";
      case 20004:
        return "Aperture Stop";
      case 20005:
        return "Shutter";
      case 20006:
        return "Objective";
      case 20007:
        return "Objective Changer";
      case 20008:
        return "TopLens";
      case 20009:
        return "Prism";
      case 20010:
        return "Zoom";
      case 20011:
        return "DSU";
      case 20012:
        return "ZDC";
      case 20050:
        return "Stage Insert";
      case 30000:
        return "Slide Loader";
      case 40000:
        return "Manual Control";
      case 40500:
        return "Microscope Frame";
    }
    return type;
  }

  private String getStackType(String type) {
    int stackType = Integer.parseInt(type);
    switch (stackType) {
      case DEFAULT_IMAGE:
        return "Default image";
      case OVERVIEW_IMAGE:
        return "Overview image";
      case SAMPLE_MASK:
        return "Sample mask";
      case FOCUS_IMAGE:
        return "Focus image";
      case EFI_SHARPNESS_MAP:
        return "EFI sharpness map";
      case EFI_HEIGHT_MAP:
        return "EFI height map";
      case EFI_TEXTURE_MAP:
        return "EFI texture map";
      case EFI_STACK:
        return "EFI stack";
      case MACRO_IMAGE:
        return "Macro image";
    }
    return type;
  }

  private int getIFDIndex() {
    if (usedFiles.length == 1) {
      return getCoreIndex();
    }
    return 1 - (core.size() - getCoreIndex());
  }

  // -- Helper class --

  class TileCoordinate {
    public int[] coordinate;

    public TileCoordinate(int nDimensions) {
      coordinate = new int[nDimensions];
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof TileCoordinate)) {
        return false;
      }

      TileCoordinate t = (TileCoordinate) o;
      if (coordinate.length != t.coordinate.length) {
        return false;
      }

      for (int i=0; i<coordinate.length; i++) {
        if (coordinate[i] != t.coordinate[i]) {
          return false;
        }
      }
      return true;
    }

    @Override
    public String toString() {
      final StringBuilder b = new StringBuilder("{");
      for (int p : coordinate) {
        b.append(p);
        b.append(", ");
      }
      b.append("}");
      return b.toString();
    }
  }

  class Pyramid {
    public String name;

    public Double magnification;
    public Double numericalAperture;
    public String objectiveName;
    public Double refractiveIndex;
    public Double workingDistance;

    public Integer width;
    public Integer height;
    public Double originX;
    public Double originY;
    public Double physicalSizeX;
    public Double physicalSizeY;
    public Long acquisitionTime;
    public Integer bitDepth;

    public Integer binningX;
    public Integer binningY;
    public Double gain;
    public Double offset;

    public Double redGain;
    public Double greenGain;
    public Double blueGain;
    public Double redOffset;
    public Double greenOffset;
    public Double blueOffset;

    public ArrayList<String> channelNames = new ArrayList<String>();
    public ArrayList<Double> channelWavelengths = new ArrayList<Double>();
    public ArrayList<Long> exposureTimes = new ArrayList<Long>();
    public Long defaultExposureTime;

    public ArrayList<String> objectiveNames = new ArrayList<String>();
    public ArrayList<Integer> objectiveTypes = new ArrayList<Integer>();

    public ArrayList<String> deviceNames = new ArrayList<String>();
    public ArrayList<String> deviceTypes = new ArrayList<String>();
    public ArrayList<String> deviceIDs = new ArrayList<String>();
    public ArrayList<String> deviceManufacturers = new ArrayList<String>();

    public Hashtable<String, Object> originalMetadata =
      new Hashtable<String, Object>();

    public HashMap<String, Integer> dimensionOrdering =
      new HashMap<String, Integer>();

    public transient Double zStart;
    public transient Double zIncrement;
    public transient ArrayList<Double> zValues = new ArrayList<Double>();
  }

}
