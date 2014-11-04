/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2014 Open Microscopy Environment:
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

import loci.common.ByteArrayHandle;
import loci.common.Constants;
import loci.common.DataTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.Region;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.MetadataTools;
import loci.formats.codec.Codec;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.JPEGCodec;
import loci.formats.codec.JPEG2000Codec;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.IFDList;
import loci.formats.tiff.PhotoInterp;
import loci.formats.tiff.TiffParser;

import ome.xml.model.primitives.PositiveFloat;

/**
 * CellSensReader is the file format reader for cellSens .vsi files.
 */
public class CellSensReader extends FormatReader {

  // -- Constants --

  // Compression types
  private static final int RAW = 0;
  private static final int JPEG = 2;
  private static final int JPEG_2000 = 3;
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

  private ArrayList<HashMap<TileCoordinate, Integer>> tileMap =
    new ArrayList<HashMap<TileCoordinate, Integer>>();
  private ArrayList<Integer> nDimensions = new ArrayList<Integer>();
  private boolean inDimensionProperties = false;
  private boolean foundChannelTag = false;
  private int dimensionTag;

  private HashMap<String, Integer> dimensionOrdering =
    new HashMap<String, Integer>();

  private double physicalSizeX, physicalSizeY;
  private double originX, originY;
  private ArrayList<Double> magnifications = new ArrayList<Double>();
  private ArrayList<Integer> imageWidths = new ArrayList<Integer>();
  private ArrayList<Integer> imageHeights = new ArrayList<Integer>();

  // -- Constructor --

  /** Constructs a new cellSens reader. */
  public CellSensReader() {
    super("CellSens VSI", new String[] {"vsi", "ets"});
    domains = new String[] {FormatTools.HISTOLOGY_DOMAIN};
    suffixSufficient = true;
    datasetDescription = "One .vsi file and an optional directory with a " +
      "similar name that contains at least one subdirectory with .ets files";
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
    if (getCoreIndex() < core.size() - 1) {
      return tileX.get(getCoreIndex());
    }
    int ifdIndex = 1 - (core.size() - getCoreIndex());
    try {
      return (int) ifds.get(ifdIndex).getTileWidth();
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
    if (getCoreIndex() < core.size() - 1) {
      return tileY.get(getCoreIndex());
    }
    int ifdIndex = 1 - (core.size() - getCoreIndex());
    try {
      return (int) ifds.get(ifdIndex).getTileLength();
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

    if (getCoreIndex() < core.size() - 1) {
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
      int ifdIndex = 1 - (core.size() - getCoreIndex());
      return parser.getSamples(ifds.get(ifdIndex), buf, x, y, w, h);
    }
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
      dimensionOrdering.clear();
      physicalSizeX = 0;
      physicalSizeY = 0;
      originX = 0;
      originY = 0;
      magnifications.clear();
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
    ifds = parser.getIFDs();

    RandomAccessInputStream vsi = new RandomAccessInputStream(id);
    vsi.order(parser.getStream().isLittleEndian());
    vsi.seek(8);
    readTags(vsi);
    vsi.seek(parser.getStream().getFilePointer());

    vsi.skipBytes(273);

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

    int seriesCount = files.size();
    core.clear();

    IFDList exifs = parser.getExifIFDs();

    int index = 0;
    for (int s=0; s<seriesCount; s++) {
      CoreMetadata ms = new CoreMetadata();
      core.add(ms);

      if (s < files.size() - 1) {
        setCoreIndex(index);
        parseETSFile(files.get(s), s);

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

        setCoreIndex(0);
      }
      else {
        IFD ifd = ifds.get(s - files.size() + 1);
        PhotoInterp p = ifd.getPhotometricInterpretation();
        int samples = ifd.getSamplesPerPixel();
        ms.rgb = samples > 1 || p == PhotoInterp.RGB;
        ms.sizeX = (int) ifd.getImageWidth();
        ms.sizeY = (int) ifd.getImageLength();
        ms.sizeZ = 1;
        ms.sizeT = 1;
        ms.sizeC = ms.rgb ? samples : 1;
        ms.littleEndian = ifd.isLittleEndian();
        ms.indexed = p == PhotoInterp.RGB_PALETTE &&
          (get8BitLookupTable() != null || get16BitLookupTable() != null);
        ms.imageCount = 1;
        ms.pixelType = ifd.getPixelType();
        ms.interleaved = false;
        ms.falseColor = false;
        ms.thumbnail = s != 0;
        index++;
      }
      ms.metadataComplete = true;
      ms.dimensionOrder = "XYCZT";
    }
    vsi.close();

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    // make sure magnification order matches resolution order

    Double maxMag = magnifications.size() > 0 ? magnifications.get(0) : 0d;
    if (magnifications.size() > 1) {
      HashMap<Integer, Integer> xDims = new HashMap<Integer, Integer>();
      int dimIndex = 0;
      for (int i=0; i<core.size();) {
        int key = core.get(i).sizeX;
        while (xDims.containsKey(key)) {
          key++;
        }
        xDims.put(key, dimIndex);
        dimIndex++;
        i += core.get(i).resolutionCount;
        if (dimIndex >= files.size() - 1) {
          break;
        }
      }

      for (Double magnification : magnifications) {
        if (magnification > maxMag) {
          maxMag = magnification;
        }
      }

      Integer[] keys = xDims.keySet().toArray(new Integer[xDims.size()]);
      Arrays.sort(keys);
      Double[] newMags = magnifications.toArray(new Double[magnifications.size()]);
      Arrays.sort(newMags);

      magnifications.clear();

      for (int i=0; i<keys.length; i++) {
        int magIndex = xDims.get(keys[i]);
        if (magIndex < magnifications.size() && i < newMags.length) {
          magnifications.set(magIndex, newMags[i]);
        }
        else {
          while (magIndex > magnifications.size()) {
            magnifications.add(1.0);
          }
          Double newMag = i < newMags.length ? newMags[i] : maxMag;
          if (magIndex < magnifications.size()) {
            magnifications.set(magIndex, newMag);
          }
          else {
            magnifications.add(newMag);
          }
        }
      }

      for (Double magnification : magnifications) {
        addGlobalMetaList("Magnification", magnification);
      }
    }

    if (physicalSizeX > 0 && physicalSizeY > 0) {
      store.setPixelsPhysicalSizeX(new PositiveFloat(physicalSizeX), 0);
      store.setPixelsPhysicalSizeY(new PositiveFloat(physicalSizeY), 0);

      int nextMag = 0;
      for (int i=0; i<core.size();) {
        if (nextMag < magnifications.size()) {
          double mult = maxMag / magnifications.get(nextMag);
          int ii = coreIndexToSeries(i);
          store.setPixelsPhysicalSizeX(new PositiveFloat(physicalSizeX * mult), ii);
          store.setPixelsPhysicalSizeY(new PositiveFloat(physicalSizeY * mult), ii);
          nextMag++;
          i += core.get(i).resolutionCount;
        }
        else {
          if (i == 0) {
            store.setPixelsPhysicalSizeX(new PositiveFloat(physicalSizeX), i);
            store.setPixelsPhysicalSizeY(new PositiveFloat(physicalSizeY), i);
          }
          break;
        }
      }
    }
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

    for (String dim : dimensionOrdering.keySet()) {
      int index = dimensionOrdering.get(dim) + 2;

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

    int resIndex = getResolution();
    if (hasFlattenedResolutions()) {
      int index = 0;
      for (int i=0; i<core.size(); ) {
        if (index + core.get(i).resolutionCount <= getSeries()) {
          index += core.get(i).resolutionCount;
          i += core.get(i).resolutionCount;
        }
        else {
          resIndex = getSeries() - index;
          break;
        }
      }
    }

    if (resIndex > 0) {
      t.coordinate[t.coordinate.length - 1] = resIndex;
    }

    Integer index = (Integer) tileMap.get(getCoreIndex()).get(t);
    if (index == null) {
      return new byte[getTileSize()];
    }

    Long offset = tileOffsets.get(getCoreIndex())[index];
    RandomAccessInputStream ets =
      new RandomAccessInputStream(fileMap.get(getCoreIndex()));
    ets.seek(offset);

    CodecOptions options = new CodecOptions();
    options.interleaved = isInterleaved();
    options.littleEndian = isLittleEndian();
    int tileSize = getTileSize();
    if (tileSize == 0) {
      tileSize = tileX.get(getCoreIndex()) * tileY.get(getCoreIndex()) * 10;
    }
    options.maxBytes = (int) (offset + tileSize);

    byte[] buf = null;
    long end = index < tileOffsets.get(getCoreIndex()).length - 1 ?
      tileOffsets.get(getCoreIndex())[index + 1] : ets.length();

    IFormatReader reader = null;
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
      case PNG:
        file = "tile.png";
        reader = new APNGReader();
      case BMP:
        if (reader == null) {
          file = "tile.bmp";
          reader = new BMPReader();
        }

        byte[] b = new byte[(int) (end - offset)];
        ets.read(b);
        Location.mapFile(file, new ByteArrayHandle(b));
        reader.setId(file);
        buf = reader.openBytes(0);
        Location.mapFile(file, null);
        break;
    }

    if (reader != null) {
      reader.close();
    }

    ets.close();
    return buf;
  }

  private void parseETSFile(String file, int s)
    throws FormatException, IOException
  {
    fileMap.put(core.size() - 1, file);

    RandomAccessInputStream etsFile = new RandomAccessInputStream(file);
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
    etsFile.skipBytes(4 * 10); // background color
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

    for (TileCoordinate t : tmpTiles) {
      int resolution = usePyramid ? t.coordinate[t.coordinate.length - 1] : 0;

      Integer tv = dimensionOrdering.get("T");
      Integer zv = dimensionOrdering.get("Z");
      Integer cv = dimensionOrdering.get("C");

      int tIndex = tv == null ? -1 : tv + 2;
      int zIndex = zv == null ? -1 : zv + 2;
      int cIndex = cv == null ? -1 : cv + 2;

      if (tv == null && zv == null) {
        if (t.coordinate.length > 4 && cv == null) {
          cIndex = 2;
          dimensionOrdering.put("C", cIndex - 2);
        }

        if (t.coordinate.length > 4) {
          if (cv == null) {
            tIndex = 3;
          }
          else {
            tIndex = cIndex + 2;
          }
          if (tIndex < t.coordinate.length) {
            dimensionOrdering.put("T", tIndex - 2);
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
            dimensionOrdering.put("Z", zIndex - 2);
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

    ms.sizeX = imageWidths.get(s * (maxC[0] + 1) * (maxZ[0] + 1) * (maxT[0] + 1));
    ms.sizeY = imageHeights.get(s * (maxC[0] + 1) * (maxZ[0] + 1) * (maxT[0] + 1));
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

    HashMap<TileCoordinate, Integer> map =
      new HashMap<TileCoordinate, Integer>();
    for (int i=0; i<tmpTiles.size(); i++) {
      map.put(tmpTiles.get(i), i);
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
        int maxSizeX = tileX.get(tileX.size() - 1) * (maxX[i] <= 1 ? 1 : maxX[i] + 1);
        int maxSizeY = tileY.get(tileY.size() - 1) * (maxY[i] <= 1 ? 1 : maxY[i] + 1);

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
      }

      ms.resolutionCount = finalResolution;
    }
    etsFile.close();
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

  private void readTags(RandomAccessInputStream vsi) {
    try {
      // read the VSI header
      long fp = vsi.getFilePointer();
      if (fp + 24 >= vsi.length()) {
        return;
      }
      int headerSize = vsi.readShort(); // should always be 24
      int version = vsi.readShort(); // always 21321
      int volumeVersion = vsi.readInt();
      long dataFieldOffset = vsi.readLong();
      int flags = vsi.readInt();
      vsi.skipBytes(4);

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

        LOGGER.debug("tag #{}: fieldType={}, tag={}, nextField={}, dataSize={}",
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
          if (!inlineData) {
            vsi.skipBytes(dataSize);
          }
          return;
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
            readTags(vsi);
            long end = vsi.getFilePointer();
            if (start == end) {
              break;
            }
          }
          if (tag == DIMENSION_DESCRIPTION_VOLUME) {
            inDimensionProperties = false;
            foundChannelTag = false;
          }
        }
        else if (extendedField && realType == PROPERTY_SET_VOLUME) {
          long endPointer = vsi.getFilePointer() + nextField;
          while (vsi.getFilePointer() < endPointer &&
            vsi.getFilePointer() < vsi.length())
          {
            long start = vsi.getFilePointer();
            readTags(vsi);
            long end = vsi.getFilePointer();
            if (start == end) {
              break;
            }
          }
        }
        else {
          String tagName = getTagName(tag);
          String value = inlineData ? String.valueOf(dataSize) : null;
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
                value = String.valueOf(vsi.readInt());
                break;
              case LONG:
              case ULONG:
              case TIMESTAMP:
                value = String.valueOf(vsi.readLong());
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

                if (tag == 2011) {
                  // this puts everything in terms of micrometers
                  String v = value.substring(0, value.indexOf("m"));
                  v = v.replaceAll("\\^", "e");
                  double scale = Double.parseDouble(v);
                  scale *= 1000000;

                  physicalSizeX *= scale;
                  physicalSizeY *= scale;
                  originX *= scale;
                  originY *= scale;
                }
                else if (tag == MAGNIFICATION) {
                  magnifications.add(
                    new Double(value.substring(0, value.length() - 1)));
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
                value = nIntValues > 1 ? "(" : "";
                for (int v=0; v<nIntValues; v++) {
                  intValues[v] = vsi.readInt();
                  value += intValues[v];
                  if (v < nIntValues - 1) {
                    value += ", ";
                  }
                }
                if (nIntValues > 1) {
                  value += ")";
                }

                if (tag == IMAGE_BOUNDARY) {
                  imageWidths.add(intValues[2]);
                  imageHeights.add(intValues[3]);
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
                value = nDoubleValues > 1 ? "(" : "";
                for (int v=0; v<nDoubleValues; v++) {
                  doubleValues[v] = vsi.readDouble();
                  value += doubleValues[v];
                  if (v < nDoubleValues - 1) {
                    value += ", ";
                  }
                }
                if (nDoubleValues > 1) {
                  value += ")";
                }

                if (tag == PLANE_SCALE_RWC) {
                  physicalSizeX = doubleValues[0];
                  physicalSizeY = doubleValues[1];
                }
                else if (tag == PLANE_ORIGIN_RWC) {
                  originX = doubleValues[0];
                  originY = doubleValues[1];
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

          if (tag == STACK_TYPE) {
            value = getStackType(value);
          }

          addGlobalMetaList(tagName, value);

        }

        if (inDimensionProperties) {
          if (tag == 2012 && !dimensionOrdering.containsValue(dimensionTag)) {
            dimensionOrdering.put("Z", dimensionTag);
          }
          else if ((tag == 2100 || tag == DIMENSION_VALUE_ID) &&
            !dimensionOrdering.containsValue(dimensionTag))
          {
            dimensionOrdering.put("T", dimensionTag);
          }
          else if (tag == 2039 && !dimensionOrdering.containsValue(dimensionTag))
          {
            dimensionOrdering.put("L", dimensionTag);
          }
          else if (tag == 2008 && foundChannelTag &&
            !dimensionOrdering.containsValue(dimensionTag))
          {
            dimensionOrdering.put("C", dimensionTag);
          }
          else if (tag == 2008) {
            foundChannelTag = true;
          }
        }

        if (nextField == 0 || tag == -494804095) {
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
        return "Exposure time (ms)";
      case CAMERA_GAIN:
        return "Camera gain";
      case CAMERA_OFFSET:
        return "Camera offset";
      case CAMERA_GAMMA:
        return "Camera gamma";
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
        return "Horizontally mirrored";
      case MIRROR_V:
        return "Vertically mirrored";
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
        return "Frame origin (real-world coordinates)";
      case RWC_FRAME_SCALE:
        return "Frame scale (real-world coordinates)";
      case RWC_FRAME_UNIT:
        return "Frame units (real-world coordinates)";
      case STACK_NAME:
        return "Stack name";
      case CHANNEL_DIM:
        return "Channel dimension";
      case OPTICAL_PATH:
        return "Optical path";
      case STACK_TYPE:
        return "Stack type";
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
        return "Document name";
      case DOCUMENT_NOTE:
        return "Document note";
      case DOCUMENT_TIME:
        return "Document creation time";
      case DOCUMENT_AUTHOR:
        return "Document author";
      case DOCUMENT_COMPANY:
        return "Document company";
      case DOCUMENT_CREATOR_NAME:
        return "Document creator name";
      case DOCUMENT_CREATOR_MAJOR_VERSION:
        return "Document creator major version";
      case DOCUMENT_CREATOR_MINOR_VERSION:
        return "Document creator minor version";
      case DOCUMENT_CREATOR_SUB_VERSION:
        return "Document creator sub version";
      case DOCUMENT_CREATOR_BUILD_NUMBER:
        return "Document creator build number";
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
    }
    return "tag " + tag;
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
    public int hashCode() {
      int[] lengths = new int[coordinate.length];
      lengths[0] = 0;
      lengths[1] = 0;

      for (String dim : dimensionOrdering.keySet()) {
        int index = dimensionOrdering.get(dim) + 2;

        if (dim.equals("Z")) {
          lengths[index] = getSizeZ();
        }
        else if (dim.equals("C")) {
          lengths[index] = getEffectiveSizeC();
        }
        else if (dim.equals("T")) {
          lengths[index] = getSizeT();
        }
      }

      for (int i=0; i<lengths.length; i++) {
        if (lengths[i] == 0) {
          lengths[i] = 1;
        }
      }

      return FormatTools.positionToRaster(lengths, coordinate);
    }

    @Override
    public String toString() {
      StringBuffer b = new StringBuffer("{");
      for (int p : coordinate) {
        b.append(p);
        b.append(", ");
      }
      b.append("}");
      return b.toString();
    }
  }

}
