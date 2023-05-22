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

import loci.common.ByteArrayHandle;
import loci.common.Constants;
import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.Region;
import loci.common.xml.XMLTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.JPEGCodec;
import loci.formats.codec.JPEGXRCodec;
import loci.formats.codec.LZWCodec;
import loci.formats.codec.ZstdCodec;
import loci.formats.in.libczi.LibCZI;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;
import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.units.quantity.Pressure;
import ome.units.quantity.Temperature;
import ome.units.quantity.Time;
import ome.xml.model.enums.AcquisitionMode;
import ome.xml.model.enums.Binning;
import ome.xml.model.enums.IlluminationType;
import ome.xml.model.primitives.Color;
import ome.xml.model.primitives.PercentFraction;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static loci.formats.in.libczi.LibCZI.JPEG;
import static loci.formats.in.libczi.LibCZI.JPEGXR;
import static loci.formats.in.libczi.LibCZI.LZW;
import static loci.formats.in.libczi.LibCZI.UNCOMPRESSED;
import static loci.formats.in.libczi.LibCZI.ZSTD_0;
import static loci.formats.in.libczi.LibCZI.ZSTD_1;

/**
 * ZeissCZIReader is the file format reader for Zeiss .czi files.
 * See  @see <a href="https://zeiss.github.io/">CZI reference documentation</a>
 *
 * Essentially, all data is stored into subblocks where each subblock location is specified by its dimension indices.
 * There are standard spatial and time dimensions, as well as extra ones necessary to describe channel, scenes,
 * acquisition modalities, etc:
 *
 *  X,Y,Z, // 3 spaces dimension
 *         T, Time
 *         M, Mosaic but why is there no trace of it in libczi ???
 *         C, Channel
 *         R, Rotation
 *         I, Illumination
 *         H, Phase
 *         V, View
 *         B, Block = deprecated
 *         S  Scene
 *
 *
 * A subblock may represent a lower resolution level. How to know this ? Because its stored size (x or y) is lower
 * than its size (x or y). Its downscaling factor can thus be computed the ratio between stored size and size.
 * For convenience, this reader adds the downscaling factor as an extra dimension named 'PY'
 *
 * A CZI file consists of several segments. The majority of segments are data subblocks, as described before. But other
 * segments are present. Essentially this reader reads the {@link loci.formats.in.libczi.LibCZI.FileHeaderSegment} that
 * contains some metadata as well as the location of the {@link loci.formats.in.libczi.LibCZI.SubBlockDirectorySegment}
 *
 * The SubBlockDirectorySegment is a critical segment because it contains the dimension indices and file location of all
 * data subblocks. Thus, by reading this segment only, there is no need to go through all file segments while
 * initializing the reader.
 *
 * Using this initial reading of the directory segment, all dimensions and all dimension ranges are known in advance.
 * This is used to compute the number of core series of the reader, as well as the resolution levels. This is done
 * by creating a core series signature {@link CoreSignature} where the dimension are sorted according to a priority
 * {@link ZeissCZIReader#dimensionPriority(String)}. If autostitching is true, all mosaics belong to the same core
 * series. If autostitching is false, each mosaic is split into different core series.
 *
 * (core series = series + resolution level)
 *
 * Notes:
 * 1. It is assumed that all subblocks from a single core index
 * have the same compression type {@link ZeissCZIReader#coreIndexToCompression}
 *
 * 2. This reader is not thread safe, you can use memoization or {@link ZeissCZIReader#copy()}
 * to get a new reader and perform parallel reading.
 *
 * 3. This reader is optimized for fast initialisation and low memory footprint. It has been tested to work on Tb
 * czi size files. To save memory, the data structures used for reading are trimmed to the minimal amount of data
 * necessary for the reading at runtime. To illustrate this point, for a 6Tb dataset, each 'int'
 * saved per block saves 7Mb (in RAM and in memo file). Trimmimg down libczi dimension entries
 * to {@link MinimalDimensionEntry} leads to a memo file of around 100Mb for a 4Tb czi file. Its initialisation
 * takes below a minute, with memo building. Then a few seconds to generate a new reader from a memo file is sufficient.
 *
 * 4. Even with memoization, at runtime, a reader for a multi Tb file will take around 300Mb on the heap. While this
 * is reasonable for a single reader, it becomes an issue to create multiple readers for parallel reading: 10 readers
 * will take 3 Gb. Thus the method {@link ZeissCZIReader#copy()} exist in order to create a new reader from an
 * existing one, which saves memory because it reuses all fields from the previous reader. Using this method, 10
 * readers can be created to read in parallel en single czi file, but it will use only the memory of one reader.
 * WARNING: calling {@link ZeissCZIReader#close()} on one of these readers will prevent the use
 * of all the other readers created with the copy method!
 *
 * The annotation {@link CopyByRef} is used to annotate the fields that should be initialized in the duplicated reader
 * using the reference of the model one, see the constructor with the reader in argument.
 *
 * 5. This reader uses the class {@link LibCZI} which contains the czi data structure translated to Java and which
 * contains very little logic related to the reader itself, which should be in this class.
 *
 * TODO:
 *  - test auto-stitching
 *  - exposure time read from subblock do not seem to work
 *  - use seriesToOx and seriesToOy for correct series positioning in XY (center or topleft?)
 *  - check camera orientation with regards to origin ?
 *  - add multi-part file support
 *  - test RGB file
 *  - test compressed files
 *  - position from subblock originx and originy works, but it does not seem to work with some other images
 *  - get optimal tile size should vary depending on compression: on raw data it's easy to partially read planes,
 *  - TODO: look at https://docs.openmicroscopy.org/latest/ome-model/developers/6d-7d-and-8d-storage.html and implement modulo
 *  but for compressed data that's much harder so it would be better to read the whole block rather that decompressing
 *  it multiple times the same block to extract a partial region
 *  Features:
 *  - add two methods that map forth and back czi dimension indices to bio-formats series
 *  - add a method that returns a 3D matrix per series (for lattice skewed dataset?)
 *  - add extraImages
 *
 */
public class ZeissCZIReader extends FormatReader {

  Logger logger = LoggerFactory.getLogger(ZeissCZIReader.class);

  // -- Constants --

  public static final String ALLOW_AUTOSTITCHING_KEY =
          "zeissczi.autostitch";
  public static final boolean ALLOW_AUTOSTITCHING_DEFAULT = true;
  public static final String INCLUDE_ATTACHMENTS_KEY =
          "zeissczi.attachments";
  public static final boolean INCLUDE_ATTACHMENTS_DEFAULT = true;
  public static final String TRIM_DIMENSIONS_KEY = "zeissczi.trim_dimensions";
  public static final boolean TRIM_DIMENSIONS_DEFAULT = false;
  public static final String RELATIVE_POSITIONS_KEY = "zeissczi.relative_positions";
  public static final boolean RELATIVE_POSITIONS_DEFAULT = false;

  private static final String CZI_MAGIC_STRING = "ZISRAWFILE";
  private static final int BUFFER_SIZE = 512;

  // A string identifier for an extra dimension: the resolution level. It's not directly part of the CZI format,
  // at least not written as a dimension entry
  private static final String RESOLUTION_LEVEL_DIMENSION = "PY";

  // A string identifier for an extra dimension: the file part. It's not directly part of the CZI format,
  // at least not written as a dimension entry
  private static final String FILE_PART_DIMENSION = "PA";

  // -- Fields --

  // bio-formats core index to x origin, in the Zeiss 2D coordinates system, common to all planes. Unit: pixel (highest resolution level)
  @CopyByRef
  private List<Integer> coreIndexToOx = new ArrayList<>();

  // bio-formats core index to y origin, in the Zeiss 2D coordinates system, common to all planes. Unit: pixel (highest resolution level)
  @CopyByRef
  private List<Integer> coreIndexToOy = new ArrayList<>();

  // bio-formats core index the compression factor of the series.
  @CopyByRef
  private List<Integer> coreIndexToCompression = new ArrayList<>();

  // bio-formats core index the compression factor of the series.
  @CopyByRef
  private List<CoreSignature> coreIndexToSignature = new ArrayList<>();

  // bio-formats core index the downscaling factor of the series.
  @CopyByRef
  private List<Integer> coreIndexToDownscaleFactor = new ArrayList<>();
  // Maps bio-formats series index to the filename, in case of multi-part file
  @CopyByRef
  private List<String> coreIndexToFileName = new ArrayList<>();

  // streamCurrentSeries is a temp field that should maybe be changed when setSeries is called
  transient int streamCurrentSeries = -1;

  // Core map structure for fast access to blocks:
  // - first key: bio-formats core index
  // - second key: czt index
  @CopyByRef
  private List< // CoreIndex
          HashMap<CZTKey, // CZT
                  List<MinimalDimensionEntry>>>
          mapCoreTZCToMinimalBlocks = new ArrayList<>();


  // ------------------------ METADATA FIELDS
  @CopyByRef
  private MetadataStore store;
  @CopyByRef
  private ArrayList<Channel> channels = new ArrayList<>();
  @CopyByRef
  private ArrayList<String> binnings = new ArrayList<>();
  @CopyByRef
  private String zoom;
  @CopyByRef
  private ArrayList<String> detectorRefs = new ArrayList<>();
  @CopyByRef
  private String userName,
          userFirstName,
          userLastName,
          userMiddleName,
          userEmail,
          userInstitution;
  @CopyByRef
  private String temperature, airPressure, humidity, co2Percent;
  @CopyByRef
  private String gain;
  @CopyByRef
  private String imageName;
  @CopyByRef
  private String acquiredDate;
  @CopyByRef
  private String description;
  @CopyByRef
  private String userDisplayName;
  @CopyByRef
  private String correctionCollar, medium, refractiveIndex;
  @CopyByRef
  private transient Time timeIncrement;
  @CopyByRef
  private transient ArrayList<String> gains = new ArrayList<>();
  @CopyByRef
  private transient Length zStep;
  @CopyByRef
  private String objectiveSettingsID;
  @CopyByRef
  private transient int plateRows;
  @CopyByRef
  private transient int plateColumns;
  @CopyByRef
  private transient ArrayList<String> platePositions = new ArrayList<>();
  @CopyByRef
  private transient ArrayList<String> fieldNames = new ArrayList<>();
  @CopyByRef
  private transient ArrayList<String> imageNames = new ArrayList<>();

  @CopyByRef
  private ArrayList<byte[]> extraImages = new ArrayList<>();

  @CopyByRef
  private boolean hasDetectorSettings = false;
  @CopyByRef
  private String[] rotationLabels, phaseLabels, illuminationLabels;
  @CopyByRef
  int maxBlockSizeX = -1;
  @CopyByRef
  int maxBlockSizeY = -1;

  //@CopyByRef
  //Length[] scenePosX, scenePosY;

  // -- Constructor --

  /** Constructs a new Zeiss .czi reader. */
  public ZeissCZIReader() {
    super("Zeiss CZI", "czi");
    domains = new String[] {FormatTools.LM_DOMAIN, FormatTools.HISTOLOGY_DOMAIN};
    suffixSufficient = false;
    suffixNecessary = false;
  }

  /** Duplicates 'that' reader for parallel reading.
   * Creating a reader with this constructor allows to keeping a very low memory footprint
   * because all immutable objects are re-used by reference.
   * WARNING: calling {@link ZeissCZIReader#close()} on one of these readers will prevent the use
   * of all the other readers created with this constructor */
  public ZeissCZIReader(ZeissCZIReader that) {
    super("Zeiss CZI", "fczi");
    domains = new String[] {FormatTools.LM_DOMAIN, FormatTools.HISTOLOGY_DOMAIN};
    suffixSufficient = false;
    suffixNecessary = false;

    this.streamCurrentSeries = -1;

    // Copy all annotated fields from this class (does not do anything with the inherited ones)
    Field[] fields = ZeissCZIReader.class.getDeclaredFields();
    for (Field field:fields) {
      if (field.isAnnotationPresent(CopyByRef.class)) {
        try {
          field.set(this,field.get(that));
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }

    this.flattenedResolutions = that.flattenedResolutions;
    this.metadataOptions = that.metadataOptions;
    this.currentId = that.currentId;
    this.core = that.core;
    this.metadataStore = that.metadataStore;
    this.filterMetadata = that.filterMetadata;
    this.datasetDescription = that.datasetDescription;
    this.group = that.group;
    this.hasCompanionFiles = that.hasCompanionFiles;
    this.indexedAsRGB = that.indexedAsRGB;
    this.normalizeData = that.normalizeData;

    // Set state, just in case
    this.setCoreIndex(that.getCoreIndex());

  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      mapCoreTZCToMinimalBlocks.clear(); // ZE big one! Hum, problem if another reader uses it... But ok
      store = null;
      userName = null;
      userFirstName = null;
      userLastName = null;
      userMiddleName = null;
      userEmail = null;
      userInstitution = null;
      temperature = null;
      airPressure = null;
      humidity = null;
      co2Percent = null;
      zoom = null;
      gain = null;

      channels.clear();
      binnings.clear();
      detectorRefs.clear();
      gains.clear();

      objectiveSettingsID = null;
      imageName = null;
      hasDetectorSettings = false;

      rotationLabels = null;
      illuminationLabels = null;
      phaseLabels = null;
      //parser = null;
      zStep = null;
      plateRows = 0;
      plateColumns = 0;
      platePositions.clear();
      fieldNames.clear();
      imageNames.clear();
    }
  }

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected ArrayList<String> getAvailableOptions() {
    ArrayList<String> optionsList = super.getAvailableOptions();
    optionsList.add(ALLOW_AUTOSTITCHING_KEY);
    optionsList.add(INCLUDE_ATTACHMENTS_KEY);
    optionsList.add(TRIM_DIMENSIONS_KEY);
    optionsList.add(RELATIVE_POSITIONS_KEY);
    return optionsList;
  }

  // -- ZeissCZI-specific methods --

  public boolean allowAutostitching() {
    MetadataOptions options = getMetadataOptions();
    if (options instanceof DynamicMetadataOptions) {
      return ((DynamicMetadataOptions) options).getBoolean(
              ALLOW_AUTOSTITCHING_KEY, ALLOW_AUTOSTITCHING_DEFAULT);
    }
    return ALLOW_AUTOSTITCHING_DEFAULT;
  }

  public boolean canReadAttachments() { // TODO : handle this method
    MetadataOptions options = getMetadataOptions();
    if (options instanceof DynamicMetadataOptions) {
      return ((DynamicMetadataOptions) options).getBoolean(
              INCLUDE_ATTACHMENTS_KEY, INCLUDE_ATTACHMENTS_DEFAULT);
    }
    return INCLUDE_ATTACHMENTS_DEFAULT;
  }

  public boolean trimDimensions() { // TODO : handle this method
    MetadataOptions options = getMetadataOptions();
    if (options instanceof DynamicMetadataOptions) {
      return ((DynamicMetadataOptions) options).getBoolean(
              TRIM_DIMENSIONS_KEY, TRIM_DIMENSIONS_DEFAULT);
    }
    return TRIM_DIMENSIONS_DEFAULT;
  }

  public boolean storeRelativePositions() { // TODO : handle this method
    MetadataOptions options = getMetadataOptions();
    if (options instanceof DynamicMetadataOptions) {
      return ((DynamicMetadataOptions) options).getBoolean(
              RELATIVE_POSITIONS_KEY, RELATIVE_POSITIONS_DEFAULT);
    }
    return RELATIVE_POSITIONS_DEFAULT;
  }

  // -- IFormatReader API methods --

  /**
   * @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream)
   */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 10;
    if (!FormatTools.validStream(stream, blockLen, true)) return false;
    String check = stream.readString(blockLen);
    return check.equals(CZI_MAGIC_STRING);
  }

  public byte[] readRawPixelData(long blockDataOffset,
                                 long blockDataSize, // TODO What is data size ? I think it's the number of bytes...
                                 int compression,
                                 int storedSizeX,
                                 int storedSizeY,
                                 RandomAccessInputStream s, Region tile, byte[] buf) throws FormatException, IOException {
    //s.order(isLittleEndian()); -> it should be already set when calling the method
    s.seek(blockDataOffset);

    if (compression == UNCOMPRESSED) {
      if (buf == null) {
        buf = new byte[(int) blockDataSize];
      }
      if (tile != null) {
        readPlane(s, tile.x, tile.y, tile.width, tile.height,0,storedSizeX,storedSizeY,buf);
      }
      else {
        s.readFully(buf);
      }
      return buf;
    }

    byte[] data = new byte[(int) blockDataSize];
    s.read(data);

    int bytesPerPixel = FormatTools.getBytesPerPixel(getPixelType());
    CodecOptions options = new CodecOptions();
    options.interleaved = isInterleaved();
    options.littleEndian = isLittleEndian();
    options.bitsPerSample = bytesPerPixel * 8;
    options.maxBytes = getSizeX() * getSizeY() * getRGBChannelCount() * bytesPerPixel;

    switch (compression) {
      case JPEG:
        data = new JPEGCodec().decompress(data, options);
        break;
      case LZW:
        data = new LZWCodec().decompress(data, options);
        break;
      case JPEGXR:
        options.width = storedSizeX;
        options.height = storedSizeY;
        options.maxBytes = options.width * options.height *
                getRGBChannelCount() * bytesPerPixel;
        try {
          data = new JPEGXRCodec().decompress(data, options);
        }
        catch (FormatException e) {
          if (data.length == options.maxBytes) {
            logger.debug("Invalid JPEG-XR compression flag");
          }
          else {
            logger.warn("Could not decompress block; some pixels may be 0", e);
            data = new byte[options.maxBytes];
          }
        }
        break;
      case ZSTD_0:
        data = new ZstdCodec().decompress(data);
        break;
      case ZSTD_1:
        boolean highLowUnpacking = false;
        int pointer = 0;
        try (RandomAccessInputStream stream = new RandomAccessInputStream(data)) {
          int sizeOfHeader = readVarint(stream);
          while (stream.getFilePointer() < sizeOfHeader) {
            int chunkID = readVarint(stream);
            // only one chunk ID defined so far
            switch (chunkID) {
              case 1:
                int payload = stream.read();
                highLowUnpacking = (payload & 1) == 1;
                break;
              default:
                throw new FormatException("Invalid chunk ID: " + chunkID);
            }
          }
          // safe cast because stream wraps a byte array
          pointer = (int) stream.getFilePointer();
        }

        byte[] decoded =  new ZstdCodec().decompress(data, pointer, data.length - pointer);
        // ZSTD_1 implies high/low byte unpacking, so it would be weird
        // if this flag were unset
        if (highLowUnpacking) {
          data = new byte[decoded.length];
          int secondHalf = decoded.length / 2;
          for (int i=0; i<decoded.length; i++) {
            boolean even = i % 2 == 0;
            int offset = i / 2;
            data[i] = even ? decoded[offset] : decoded[secondHalf + offset];
          }
        }
        else {
          logger.warn("ZSTD-1 compression used, but no high/low byte unpacking");
          data = decoded;
        }

        break;
      case 104: // camera-specific packed pixels
        data = decode12BitCamera(data, options.maxBytes);
        // reverse column ordering
        for (int row=0; row<getSizeY(); row++) {
          for (int col=0; col<getSizeX()/2; col++) {
            int left = row * getSizeX() * 2 + col * 2;
            int right = row * getSizeX() * 2 + (getSizeX() - col - 1) * 2;
            byte left1 = data[left];
            byte left2 = data[left + 1];
            data[left] = data[right];
            data[left + 1] = data[right + 1];
            data[right] = left1;
            data[right + 1] = left2;
          }
        }

        break;
      case 504: // camera-specific packed pixels
        data = decode12BitCamera(data, options.maxBytes);
        break;
    }
    if (buf != null && buf.length >= data.length) {
      System.arraycopy(data, 0, buf, 0, data.length);
      return buf;
    }
    return data;
  }

  private static int readVarint(RandomAccessInputStream stream) throws IOException {
    byte a = stream.readByte();
    // if high bit set, read next byte
    // at most 3 bytes read
    if ((a & 0x80) == 0x80) {
      byte b = stream.readByte();
      if ((b & 0x80) == 0x80) {
        byte c = stream.readByte();
        return (c << 14) | ((b & 0x7f) << 7) | (a & 0x7f);
      }
      return (b << 7) | (a & 0x7f);
    }
    return a & 0xff;
  }

  private static byte[] decode12BitCamera(byte[] data, int maxBytes) throws IOException {
    byte[] decoded = new byte[maxBytes];

    RandomAccessInputStream bb = new RandomAccessInputStream(
            new ByteArrayHandle(data));
    byte[] fourBits = new byte[(maxBytes / 2) * 3];
    int pt = 0;
    while (pt < fourBits.length) {
      fourBits[pt++] = (byte) bb.readBits(4);
    }
    bb.close();
    for (int index=0; index<fourBits.length-1; index++) {
      if ((index - 3) % 6 == 0) {
        byte middle = fourBits[index];
        byte last = fourBits[index + 1];
        byte first = fourBits[index - 1];
        fourBits[index + 1] = middle;
        fourBits[index] = first;
        fourBits[index - 1] = last;
      }
    }

    int currentByte = 0;
    for (int index=0; index<fourBits.length;) {
      if (index % 3 == 0) {
        decoded[currentByte++] = fourBits[index++];
      }
      else {
        decoded[currentByte++] =
                (byte) (fourBits[index++] << 4 | fourBits[index++]);
      }
    }

    return decoded;
  }

  @Override
  public void reopenFile() throws IOException {
    streamCurrentSeries = -1;
  }

  public synchronized RandomAccessInputStream getStream() throws IOException {
    if ((in != null)&&(streamCurrentSeries == getSeries())) {
      return in;
    }
    streamCurrentSeries = getSeries();
    RandomAccessInputStream ris = new RandomAccessInputStream(coreIndexToFileName.get(getCoreIndex()), BUFFER_SIZE);
    in = ris;
    ris.order(isLittleEndian());
    return ris;
  }

  @Override
  public int getOptimalTileWidth() {
    if (maxBlockSizeX>0) {
      return Math.min(512, maxBlockSizeX);
    } else {
      return Math.min(512, getSizeX());
    }
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    if (maxBlockSizeY>0) {
      return Math.min(512, maxBlockSizeY);
    } else {
      return Math.min(512, getSizeY());
    }
  }

  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h) throws FormatException, IOException {

    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    if (isThumbnailSeries()) {
      // thumbnail, label, or preview image stored as an attachment
      int index = getCoreIndex() - (core.size() - extraImages.size());
      byte[] fullPlane = extraImages.get(index);
      RandomAccessInputStream s = new RandomAccessInputStream(fullPlane);
      try {
        readPlane(s, x, y, w, h, buf);
      }
      finally {
        s.close();
      }
      return buf;
    }


    int currentIndex = getCoreIndex();
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int nCh = getRGBChannelCount();
    int bytesPerPixel = (isRGB()?nCh:1) * bpp;
    int baseResolution = currentIndex;

    Region image = new Region(x, y, w, h);

    // Because series are sorted along their resolution level, that's a way to find which
    // resolution level is the lowest one - but that looks very brittle - what if you have a very
    // while the downscaling is decreasing, let's decrement baseresolution
    // what this assumes is that the resolution level whereby downscaling = 1 is always present

    int[] czt = this.getZCTCoords(no);
    CZTKey key = new CZTKey(czt[1], czt[0], czt[2]);

    while (baseResolution > 0 &&
            coreIndexToDownscaleFactor.get(baseResolution) >
                    coreIndexToDownscaleFactor.get(baseResolution-1)) {
      baseResolution--;
    }

    // The data is somewhere in these blocks
    List<MinimalDimensionEntry> blocks = mapCoreTZCToMinimalBlocks.get(currentIndex).get(key);

    if (blocks == null) return buf; // No block found -> empty image. TODO : black or white ?

    for (MinimalDimensionEntry block : blocks) {
      Region blockRegion = new Region(
              block.dimensionStartX/ coreIndexToDownscaleFactor.get(coreIndex) - coreIndexToOx.get(coreIndex),
              block.dimensionStartY/ coreIndexToDownscaleFactor.get(coreIndex) - coreIndexToOy.get(coreIndex),
              block.storedSizeX,
              block.storedSizeY
      );

      if (image.intersects(blockRegion)) {
        RandomAccessInputStream stream = getStream();
        LibCZI.SubBlockSegment subBlock = LibCZI.getBlock(stream, block.filePosition);

        if (image.equals(blockRegion)) {
          // Best case scenario
          return readRawPixelData(
                  subBlock.dataOffset,
                  subBlock.data.dataSize, // TODO: what is data size ??
                  coreIndexToCompression.get(coreIndex),
                  block.storedSizeX,
                  block.storedSizeY,
                  stream,null,buf);
        } else {
          // We need to copy, taking in consideration the size taken by the image
          // We can potentially crop what's read

          int compression = coreIndexToCompression.get(coreIndex);

          Region regionRead;
          // If the data is uncompressed, we can skip reading some data
          if (compression == UNCOMPRESSED) {
            regionRead = image.intersection(blockRegion);
          } else {
            regionRead = blockRegion;
          }

          Region tileInBlock = new Region(regionRead.x-blockRegion.x, regionRead.y-blockRegion.y, regionRead.width, regionRead.height);

          byte[] rawData = readRawPixelData(
                  subBlock.dataOffset,
                  subBlock.data.dataSize,
                  compression,
                  block.storedSizeX,
                  block.storedSizeY,
                  stream, compression==UNCOMPRESSED? tileInBlock: null, // can't really optimize with compressed block
                  compression==UNCOMPRESSED? DataTools.allocate(tileInBlock.width, tileInBlock.height, nCh, bpp): null);

          // We need to basically crop a rectangle with a rectangle, of potentially different sizes
          // Let's find out the position of the block in the image referential
          int blockOriX = regionRead.x-image.x;
          int skipBytesStartX = 0;
          int skipBytesBufStartX = 0;
          if (blockOriX<0) {
            skipBytesStartX = -blockOriX*bytesPerPixel;
          } else {
            skipBytesBufStartX = blockOriX*bytesPerPixel;
          }
          int blockEndX = (regionRead.x+regionRead.width)-(image.x+image.width);
          int skipBytesEndX = 0;
          if (blockEndX>0) {
            skipBytesEndX = blockEndX*bytesPerPixel;
          }
          int nBytesToCopyPerLine = (regionRead.width*bytesPerPixel-skipBytesStartX-skipBytesEndX);
          int blockOriY = regionRead.y-image.y;
          int skipLinesRawDataStart = 0;
          int skipLinesBufStart = 0;
          if (blockOriY<0) {
            skipLinesRawDataStart = -blockOriY;
          } else {
            skipLinesBufStart = blockOriY;
          }
          int blockEndY = (regionRead.y+regionRead.height)-(image.y+image.height);
          int skipLinesEnd = 0;
          if (blockEndY>0) {
            skipLinesEnd = blockEndY;
          }
          int totalLines = regionRead.height-skipLinesRawDataStart-skipLinesEnd;
          int nBytesPerLineRawData = regionRead.width*bytesPerPixel;
          int nBytesPerLineBuf = image.width*bytesPerPixel;
          int offsetRawData = skipLinesRawDataStart*nBytesPerLineRawData+skipBytesStartX;
          int offsetBuf = skipLinesBufStart*nBytesPerLineBuf+skipBytesBufStartX;

          for (int i=0; i<totalLines;i++) { // TODO: totalines or totalines + 1 ?
            System.arraycopy(rawData,offsetRawData,buf,offsetBuf,nBytesToCopyPerLine);
            offsetRawData=offsetRawData+nBytesPerLineRawData;
            offsetBuf=offsetBuf+nBytesPerLineBuf;
          }

        }
      }
    }
    return buf;
  }

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    // Switch to the master file if this is part of a multi-file dataset
    int lastDot = id.lastIndexOf(".");
    String base = lastDot < 0 ? id : id.substring(0, lastDot);
    if (base.endsWith(")") && isGroupFiles()) {
      LOGGER.info("Checking for master file");
      int lastFileSeparator = base.lastIndexOf(File.separator);
      int end = base.lastIndexOf(" (");
      if (end < 0 || end < lastFileSeparator) {
        end = base.lastIndexOf("(");
      }
      if (end > 0 && end > lastFileSeparator) {
        base = base.substring(0, end) + ".czi";
        if (new Location(base).exists()) {
          LOGGER.info("Initializing master file {}", base);
          initFile(base);
          return;
        }
      }
    }

    // At this point of the initFile method, it's guaranteed that id is the id of the master file
    store = makeFilterMetadata(); // For metadata
    core.get(0).littleEndian = true; // We assume that CZI files are always little endian. Setting the value at this point in the method ensures that isLittleEndian() calls return true

    // Multiple czi files may exist
    // file names are not stored in the files; we have to rely on a
    // specific naming convention:
    //
    //  master_file.czi
    //  master_file (1).czi
    //  master_file (2).czi
    //  ...
    //
    // the number of files is also not stored, so we have to manually check
    // for all files with a matching name

    Location file = new Location(id).getAbsoluteFile();
    base = file.getName();
    lastDot = base.lastIndexOf(".");
    if (lastDot >= 0) {
      base = base.substring(0, lastDot);
    }

    // Map file part and CZI segments found in the file
    // but only the segments needed for the file initialisation...
    Map<Integer, CZISegments> cziPartToSegments = new HashMap<>();
    cziPartToSegments.put(0, new CZISegments(id, isLittleEndian())); // CZISegments constructor parses CZI file segments

    // And we the additional parts.
    Location parent = file.getParentFile();
    String[] list = parent.list(true);
    for (String f : list) {
      if (f.startsWith(base + "(") || f.startsWith(base + " (")) {
        String part = f.substring(f.lastIndexOf("(") + 1, f.lastIndexOf(")"));
        try {
          cziPartToSegments.put(Integer.parseInt(part),
                  new CZISegments(new Location(parent, f).getAbsolutePath(), isLittleEndian()));
        } catch (NumberFormatException e) {
          LOGGER.debug("{} not included in multi-file dataset", f);
        }
      }
    }

    // How many dimensions exist for CZI ? A lot
    //Z The Z-dimension.
    //C The C-dimension ("channel").
    //T The T-dimension ("time").
    //R The R-dimension ("rotation").
    //S The S-dimension ("scene").
    //I The I-dimension ("illumination").
    //H The H-dimension ("phase").
    //V The V-dimension ("view").
    //B The B-dimension ("block") - its use is deprecated.
    //M The M-dimension ("mosaic") -> why there's no trace of it in libczi ???

    // Then, we add two extra dimensions for convenience:
    // PY, which identifies the pyramidal level (RESOLUTION_LEVEL_DIMENSION)
    // PA, which identifies the file part (FILE_PART_DIMENSION)

    // To build a series, one should:
    // - Split according to view, phase, illumination, rotation, scene, and mosaic ?
    // - Merge according to Z, T
    // - Merge according to C, except if pixels types are different

    // Adding the extra dimension in each subblock : part, and resolution level
    // For the series order, each dimension has a priority, set by the method dimensionPriority

    // What do I want to do ?
    // I want to find the number of series. For that, I make a unique signature
    // of each subblock with its dimension signature, then count the number of
    // signature in the signature set.

    // The signature alphabetical order will be used for the ordering of the series
    // Here's some example signatures:
    // PA0H0S04PY00001
    // PA0H0S03PY00001
    // PA0H0S02PY00001 -> file part 0, phase 0, scene 2, pyramidal level 1 (highest resolution)
    // PA0H0S01PY00001
    // PA0H0S00PY00001
    // PA is always first, PY always last

    Set<String> allDimensions = new HashSet<>();
    Map<String, Integer> maxDigitPerDimension = new HashMap<>();

    // First we look at all the existing dimensions
    cziPartToSegments.forEach((part, cziSegments) -> { // For each part
      Arrays.asList(cziSegments.subBlockDirectory.data.entries).forEach( // and each entry
              entry -> {
                for (String dimension: entry.getDimensions()) {
                  allDimensions.add(dimension);
                }
              }
      );
    });

    // Then we look at the max value in each dimension, to know how many digits are needed to write the signature
    // and proper alphabetical ordering
    cziPartToSegments.forEach((part, cziSegments) -> { // For each part
      Arrays.asList(cziSegments.subBlockDirectory.data.entries).forEach( // and each entry
              entry -> {
                for (LibCZI.SubBlockSegment.SubBlockSegmentData.SubBlockDirectoryEntryDV.DimensionEntry dimEntry: entry.getDimensionEntries()) {
                  int nDigits = String.valueOf(dimEntry.start).length(); // TODO: Can this be negative ?
                  if (!maxDigitPerDimension.containsKey(dimEntry.dimension)) {
                    maxDigitPerDimension.put(dimEntry.dimension,nDigits);
                  } else {
                    int curMax = maxDigitPerDimension.get(dimEntry.dimension);
                    if (nDigits>curMax) {
                      maxDigitPerDimension.put(dimEntry.dimension,nDigits);
                    }
                  }
                }
              }
      );
    });

    // Ready to build the signature
    Map<CoreSignature, List<LibCZI.SubBlockDirectorySegment.SubBlockDirectorySegmentData.SubBlockDirectoryEntry>> coreSignatureToBlocks = new HashMap<>();
    maxDigitPerDimension.put(RESOLUTION_LEVEL_DIMENSION,5); // Let's hope that the downsampling ratio never exceeds 9999 TODO : improve
    maxDigitPerDimension.put(FILE_PART_DIMENSION, String.valueOf(cziPartToSegments.size()).length());

    // Write all signatures
    cziPartToSegments.forEach((part, cziSegments) -> { // For each part
      Arrays.asList(cziSegments.subBlockDirectory.data.entries).forEach( // and each entry
              entry -> {
                // Split by resolution level if flattenedResolutions is true
                CoreSignature coreSignature = new CoreSignature(entry.getDimensionEntries(), RESOLUTION_LEVEL_DIMENSION,
                        getDownSampling(entry), (dim) -> maxDigitPerDimension.get(dim), allowAutostitching(),
                        FILE_PART_DIMENSION, part);
                if (!coreSignatureToBlocks.containsKey(coreSignature)) {
                  coreSignatureToBlocks.put(coreSignature, new ArrayList<>());
                }
                coreSignatureToBlocks.get(coreSignature).add(entry);
              });
    });

    // Sort them
    List<CoreSignature> orderedCoreSignatureList = coreSignatureToBlocks.keySet().stream().sorted().collect(Collectors.toList());

    // We now know how many core index are present in the image... except for missing extra images!

    core = new ArrayList<>();
    int idxCoreResolutionLevelStart = -1;
    for (int i = 0; i<orderedCoreSignatureList.size(); i++) {
      CoreMetadata core_i = new CoreMetadata();
      core.add(core_i);
      core_i.orderCertain = true;
      core_i.dimensionOrder = "XYCZT";
      core_i.littleEndian = true;
      CoreSignature coreSignature = orderedCoreSignatureList.get(i);
      LibCZI.SubBlockDirectorySegment.SubBlockDirectorySegmentData.SubBlockDirectoryEntry model = coreSignatureToBlocks.get(coreSignature).get(0);
      convertPixelType(core_i, model.getPixelType());
      coreIndexToCompression.add(model.getCompression());
      coreIndex = i;
      coreIndexToDownscaleFactor.add(getDownSampling(model));

      int[] coordsOrigin = setOriginAndSize(core_i,coreSignatureToBlocks.get(coreSignature));
      coreIndexToOx.add(coordsOrigin[0]);
      coreIndexToOy.add(coordsOrigin[1]);

      core_i.imageCount = (core_i.rgb ? core_i.sizeC/3 : core_i.sizeC)*core_i.sizeT*core_i.sizeZ;

      // We assert that all sub blocks do have the same size pixel type
      // Series are ordered by dimension changes, and the (non-ignored) dimension
      // with the highest priority is the resolution level
      // So the downsampling will go : 1, 2, 4, 8, 1 (change!), 2, 4, 8, 1 (change), 2, 4, 1 (change), 1 (change)
      // When a change is noticed, all previous series should belong to the same series.
      // see FormatReader#getSeriesToCoreIndex
      if (getDownSampling(model)==1) {
        if (idxCoreResolutionLevelStart==-1) {
          idxCoreResolutionLevelStart = i;
        } else {
          for (int j = idxCoreResolutionLevelStart; j<i; j++) {
            core.get(j).resolutionCount = i-j;
          }
          idxCoreResolutionLevelStart = i;
        }
      } else {
        // Let's close the loop
        if (i==orderedCoreSignatureList.size()-1) {
          // Let's finish
          for (int j = idxCoreResolutionLevelStart; j<orderedCoreSignatureList.size(); j++) {
            core.get(j).resolutionCount = orderedCoreSignatureList.size()-j;
          }
        }
      }

      coreIndexToSignature.add(coreSignature);
    }

    // Handle extra images:
    // JPG
    // Thumbnail
    List<Integer> sortedFileParts = cziPartToSegments.keySet().stream().sorted().collect(Collectors.toList());

    for (int filePart: sortedFileParts) {
      byte[] jpegBytes = LibCZI.getJPGThumbNail(cziPartToSegments.get(filePart).attachmentDirectory, id, BUFFER_SIZE, isLittleEndian());
      if (jpegBytes!=null) {
        JPEGReader thumbReader = new JPEGReader();
        String placeHolderName = "image.jpg";
        //thumbReader.setMetadataOptions(getMetadataOptions());
        ByteArrayHandle stream = new ByteArrayHandle(jpegBytes);
        Location.mapFile(placeHolderName, stream);
        thumbReader.setId(placeHolderName);

        CoreMetadata c = thumbReader.getCoreMetadataList().get(0);

        if (c.sizeZ > 1 || c.sizeT > 1) {

        } else {
          if ((c.sizeX>1) && (c.sizeY>1)) { // Sometimes there's nothing in the thumbnail
            core.add(new CoreMetadata(c));
            core.get(core.size() - 1).thumbnail = true;
            extraImages.add(thumbReader.openBytes(0));
            thumbReader.close();
            stream.close();
          }
        }
        Location.mapFile(placeHolderName, null);
      }
    }

    LOGGER.trace("#CoreSeries = {}", core.size());

    // Logs all series info
    for (int i = 0; i<core.size(); i++) {
      setCoreIndex(i);
      LOGGER.trace("Series = {}", getSeries());
      LOGGER.trace("\tSize X = {}", getSizeX());
      LOGGER.trace("\tSize Y = {}", getSizeY());
      LOGGER.trace("\tSize Z = {}", getSizeZ());
      LOGGER.trace("\tSize C = {}", getSizeC());
      LOGGER.trace("\tSize T = {}", getSizeT());
      LOGGER.trace("\tis RGB = {}", isRGB());
      LOGGER.trace("\tcalculated image count = {}", getCurrentCore().imageCount);
      if (!core.get(i).thumbnail) {
        CoreSignature usl = orderedCoreSignatureList.get(i);
        LOGGER.trace("Core Series signature = "+usl);
        LOGGER.trace("\tnBlocks in CZI File = {}", coreSignatureToBlocks.get(usl).size());
      }
    }

    setCoreIndex(0); // Fresh start
    // Ok, now let's sort all subblocks from a series into hashmaps
    // Idea: subblocks.get(coreIndex).get(timepoint).get(z).get(c) -> returns a list of blocks which
    // differs only in X and Y coordinates.

    // This structure will be trimmed down in size and stored in the mapCoreTZCToMinimalBlocks field
    List< // CoreIndex
            HashMap<CZTKey, // CZT
                    List<LibCZI.SubBlockDirectorySegment.SubBlockDirectorySegmentData.SubBlockDirectoryEntry>>>
            mapCoreTZCToBlocks = new ArrayList<>();

    for (int iCoreIndex = 0; iCoreIndex<core.size(); iCoreIndex++) {
      if (core.get(iCoreIndex).thumbnail) continue; // skips extra images
      coreIndexToFileName.add(id); // TODO : improve and understand multi-series files
      CoreSignature coreSignature = orderedCoreSignatureList.get(iCoreIndex);
      System.out.println(coreSignature.signature);
      mapCoreTZCToBlocks.add(iCoreIndex, new HashMap<>());
      mapCoreTZCToMinimalBlocks.add(iCoreIndex, new HashMap<>());
      HashMap<CZTKey, List<LibCZI.SubBlockDirectorySegment.SubBlockDirectorySegmentData.SubBlockDirectoryEntry>> seriesMap = mapCoreTZCToBlocks.get(iCoreIndex);
      HashMap<CZTKey, List<MinimalDimensionEntry>> seriesMinMap = mapCoreTZCToMinimalBlocks.get(iCoreIndex);
      for (LibCZI.SubBlockDirectorySegment.SubBlockDirectorySegmentData.SubBlockDirectoryEntry block: coreSignatureToBlocks.get(coreSignature)) {
        int c = block.getDimension("C").start;
        int z = block.getDimension("Z").start;
        int t = block.getDimension("T").start;
        CZTKey k = new CZTKey(c,z,t);
        if (!seriesMap.containsKey(k)) {
          seriesMap.put(k, new ArrayList<>());
          seriesMinMap.put(k, new ArrayList<>());
        }
        seriesMap.get(k).add(block);
        seriesMinMap.get(k).add(new MinimalDimensionEntry(block));
      }
      //In the end, there are 'seriesMap.values().size()' blocks in the core 'Series'
    }

    // MetaData from xml file
    DocumentBuilder parser = XMLTools.createBuilder();

    // But is everything in the master file ???
    readXMLMetadata(cziPartToSegments.get(0).metadata, parser);
    // Timestamps are already read

    MetadataTools.populatePixels(store, this, true);

    // Needs to set the instrument reference before calling the next method
    store.setInstrumentID(MetadataTools.createLSID("Instrument", 0), 0);

    setExperimenterInformation();

    setPlaneAndSeriesTimeInformation(mapCoreTZCToBlocks, parser);

    setPlaneLocationLocationInformation();

    setImageNames();

    setAdditionalImageMetadata();

    setChannelMetadata();
  }


  private void setPlaneLocationLocationInformation() {

  }

  private void setAdditionalImageMetadata() throws FormatException {
    for (int iSeries=0; iSeries<getSeriesCount(); iSeries++) {

      // Should not do the rest for extra images
      // remaining acquisition settings (esp. channels) do not apply to
      // label and macro images
      int extraIndex = iSeries - (getSeriesCount() - extraImages.size());
      if (extraIndex >= 0) {
        continue;
      }

      if (description != null && description.length() > 0) {
        store.setImageDescription(description, iSeries);
      }

      if (airPressure != null) {
        store.setImagingEnvironmentAirPressure(
                new Pressure(new Double(airPressure), UNITS.MILLIBAR), iSeries);
      }

      if (co2Percent != null) {
        store.setImagingEnvironmentCO2Percent(
                PercentFraction.valueOf(co2Percent), iSeries);
      }
      if (humidity != null) {
        store.setImagingEnvironmentHumidity(
                PercentFraction.valueOf(humidity), iSeries);
      }
      if (temperature != null) {
        store.setImagingEnvironmentTemperature(new Temperature(
                new Double(temperature), UNITS.CELSIUS), iSeries);
      }

      if (objectiveSettingsID != null) {
        store.setObjectiveSettingsID(objectiveSettingsID, iSeries);
        if (correctionCollar != null) {
          store.setObjectiveSettingsCorrectionCollar(
                  new Double(correctionCollar), iSeries);
        }
        if (medium != null) {
          store.setObjectiveSettingsMedium(MetadataTools.getMedium(medium), iSeries);
        }
        if (refractiveIndex != null) {
          store.setObjectiveSettingsRefractiveIndex(
                  new Double(refractiveIndex), iSeries);
        }
      }
    }
  }

  private void setChannelMetadata() {
    for (int iSeries=0; iSeries<getSeriesCount(); iSeries++) {

      int extraIndex = iSeries - (getSeriesCount() - extraImages.size());
      if (extraIndex >= 0) {
        continue;
      }

      boolean isPALM = false; // TODO
      addChannelMetadata(iSeries, isPALM);
    }
  }

  private void setImageNames() {
    String name = new Location(getCurrentFile()).getName();
    if (imageName != null && imageName.trim().length() > 0) {
      name = imageName;
    }

    int indexLength = String.valueOf(getSeriesCount()).length();

    for (int iSeries=0; iSeries<getSeriesCount(); iSeries++) {

      String imageIndex = String.valueOf(iSeries + 1);
      while (imageIndex.length() < indexLength) {
        imageIndex = "0" + imageIndex;
      }

      int extraIndex = iSeries - (getSeriesCount() - extraImages.size());
      if (extraIndex < 0) {
        if (hasFlattenedResolutions()) {
          store.setImageName(name + " #" + imageIndex, iSeries);
        } else if (false) {// TODO fix! (positions == 1) {
          if (imageNames.size() == 1) {
            store.setImageName(imageNames.get(0), iSeries);
          } else {
            store.setImageName("", iSeries);
          }
        } else {
          if (iSeries < imageNames.size()) {
            String completeName = imageNames.get(iSeries);
            if (iSeries < fieldNames.size()) {
              completeName += " " + fieldNames.get(iSeries);
            }
            store.setImageName(completeName, iSeries);
          } else {
            int paddingLength = ("" + getSeriesCount()).length();
            store.setImageName("Scene #" + String.format("%0" + paddingLength + "d", (iSeries + 1)), iSeries);
          }
        }
      } else if (extraIndex == 0) {
        store.setImageName("label image", iSeries);
      } else if (extraIndex == 1) {
        store.setImageName("macro image", iSeries);
      } else {
        store.setImageName("thumbnail image", iSeries);
      }
    }

  }

  private void setExperimenterInformation() {
    // User information fields
    String experimenterID = MetadataTools.createLSID("Experimenter", 0);
    store.setExperimenterID(experimenterID, 0);
    store.setExperimenterEmail(userEmail, 0);
    store.setExperimenterFirstName(userFirstName, 0);
    store.setExperimenterInstitution(userInstitution, 0);
    store.setExperimenterLastName(userLastName, 0);
    store.setExperimenterMiddleName(userMiddleName, 0);
    store.setExperimenterUserName(userName, 0);
    for (int iSeries=0; iSeries<getSeriesCount();iSeries++) {
      if (experimenterID != null) {
        store.setImageExperimenterRef(experimenterID, iSeries);
      }
    }
  }

  transient Timestamp[] coreIndexTimeStamp;
  private void setPlaneAndSeriesTimeInformation(List<HashMap<CZTKey, List<LibCZI.SubBlockDirectorySegment.SubBlockDirectorySegmentData.SubBlockDirectoryEntry>>> mapCoreTZCToBlocks, DocumentBuilder parser) throws IOException {

    // MetaData from series -> read a few subblocks to locate the sample
    // We'll assume that everything is linearly interpolated from
    // Begin to start, along T and along Z
    coreIndexTimeStamp = new Timestamp[core.size()];

    for (int iCoreIndex = 0; iCoreIndex<core.size(); iCoreIndex++) {
      setCoreIndex(iCoreIndex);

      // Skips metadata for thumbnails
      int extraIndex = iCoreIndex - (core.size() - extraImages.size());
      if (extraIndex >= 0) {
        continue;
      }

      // Set stage label
      CoreSignature signature = coreIndexToSignature.get(iCoreIndex);
      if (signature.getDimensions().containsKey("S")) {
        int sceneIndex = signature.getDimensions().get("S");
        String sceneName = allPositionsInformation.scenes.get(sceneIndex).name;
        if (sceneName == null) sceneName = "Scene position #"+sceneIndex;
        store.setStageLabelName(sceneName, getSeries());
        store.setStageLabelX(allPositionsInformation.scenes.get(sceneIndex).pos.get(0).pX, getSeries());
        store.setStageLabelY(allPositionsInformation.scenes.get(sceneIndex).pos.get(0).pY, getSeries());
        store.setStageLabelZ(allPositionsInformation.scenes.get(sceneIndex).pos.get(0).pZ, getSeries());
      }

      int nChannels = getSizeC();

      List<LibCZI.SubBlockDirectorySegment.SubBlockDirectorySegmentData.SubBlockDirectoryEntry> blocks;
      LibCZI.SubBlockSegment block;

      // Let's gets the timestamps.
      loopChannel:
      for (int iChannel = 0; iChannel<nChannels; iChannel++) {
        CZTKey ziti = new CZTKey(iChannel,0,0);
        blocks = mapCoreTZCToBlocks.get(iCoreIndex).get(ziti); if ((blocks==null) || (blocks.size()==0)) break loopChannel;
        block = LibCZI.getBlock(getStream(), blocks.get(0).getFilePosition());
        LibCZI.SubBlockMeta sbmziti = LibCZI.readSubBlockMeta(getStream(), block, parser);

        Length stagePosX = null;
        Length stagePosY = null;
        Length stagePosZ = null;

        // Look for the min X and Y position over blocks
        for (LibCZI.SubBlockDirectorySegment.SubBlockDirectorySegmentData.SubBlockDirectoryEntry iBlock : blocks) {
          block = LibCZI.getBlock(getStream(), iBlock.getFilePosition());
          LibCZI.SubBlockMeta sbm = LibCZI.readSubBlockMeta(getStream(), block, parser);
          if ((stagePosX == null)||(stagePosX.value(UNITS.MICROMETER).doubleValue()>sbm.stageX.value(UNITS.MICROMETER).doubleValue())) {
            stagePosX = sbm.stageX;
          }
          if ((stagePosY == null)||(stagePosY.value(UNITS.MICROMETER).doubleValue()>sbm.stageY.value(UNITS.MICROMETER).doubleValue())) {
            stagePosY = sbm.stageY;
          }
          if ((stagePosZ == null)||(stagePosZ.value(UNITS.MICROMETER).doubleValue()>sbm.stageZ.value(UNITS.MICROMETER).doubleValue())) {
            stagePosZ = sbm.stageZ;
          }
        }


        // Read position from block
        if (stagePosX == null) {
          for (LibCZI.SubBlockDirectorySegment.SubBlockDirectorySegmentData.SubBlockDirectoryEntry iBlock : blocks) {

            Length posX = new Length(iBlock.getDimension("X").start/coreIndexToDownscaleFactor.get(iCoreIndex)
                    *this.coreToPixSizeX.get(iCoreIndex).value(UNITS.MICROMETER).doubleValue(), UNITS.MICROMETER);
            Length posY = new Length(iBlock.getDimension("Y").start/coreIndexToDownscaleFactor.get(iCoreIndex)
                    *this.coreToPixSizeY.get(iCoreIndex).value(UNITS.MICROMETER).doubleValue(), UNITS.MICROMETER);
            if ((stagePosX == null)||(stagePosX.value().doubleValue()>posX.value(UNITS.MICROMETER).doubleValue())) {
              stagePosX = posX;
            }
            if ((stagePosY == null)||(stagePosY.value().doubleValue()>posY.value(UNITS.MICROMETER).doubleValue())) {
              stagePosY = posY;
            }

            if (coreToPixSizeZ.size()!=0) {
              Length posZ = new Length(iBlock.getDimension("Z").start
                      * this.coreToPixSizeZ.get(iCoreIndex).value(UNITS.MICROMETER).doubleValue(), UNITS.MICROMETER);

              if ((stagePosZ == null) || (stagePosZ.value().doubleValue() > posZ.value(UNITS.MICROMETER).doubleValue())) {
                stagePosZ = posZ;
              }
            }
          }
        }

        CZTKey zfti = new CZTKey(iChannel,getSizeZ()-1,0);
        blocks = mapCoreTZCToBlocks.get(iCoreIndex).get(zfti); if ((blocks==null) || (blocks.size()==0)) break loopChannel;
        block = LibCZI.getBlock(getStream(), blocks.get(0).getFilePosition());
        LibCZI.SubBlockMeta sbmzfti = LibCZI.readSubBlockMeta(getStream(), block, parser);

        CZTKey zitf = new CZTKey(iChannel,0,getSizeT()-1);
        blocks = mapCoreTZCToBlocks.get(iCoreIndex).get(zitf); if ((blocks==null) || (blocks.size()==0)) break loopChannel;
        block = LibCZI.getBlock(getStream(), blocks.get(0).getFilePosition());
        LibCZI.SubBlockMeta sbmzitf = LibCZI.readSubBlockMeta(getStream(), block, parser);

        if (iChannel==0) {
          if (sbmziti.timestamp!=0) { // The image was not taken on Jan 1st 1970...
            long timestamp = (long) (sbmziti.timestamp * 1000);//planes.get(0).timestamp * 1000); TODO : do not work
            String date =
                    DateTools.convertDate(timestamp, DateTools.UNIX);
            coreIndexTimeStamp[iCoreIndex] = new Timestamp(date);
          }
        }


        double incrementTimeOverZ = (sbmzfti.timestamp - sbmziti.timestamp)/(double)getSizeZ();
        double incrementTimeOverT = (sbmzitf.timestamp - sbmziti.timestamp)/(double)getSizeT();

        Time exposure = new Time(sbmziti.exposureTime*1000, UNITS.SECOND);

        for (int iZ = 0; iZ < getSizeZ(); iZ++) {
          for (int iT = 0; iT < getSizeT(); iT++) {
            int planeIndex = getIndex(iZ, iChannel, iT);
            store.setPlanePositionX(stagePosX, getSeries(), planeIndex);
            store.setPlanePositionY(stagePosY, getSeries(), planeIndex);
            store.setPlanePositionZ(stagePosZ, getSeries(), planeIndex);
            if (sbmziti.exposureTime != 0) {
              store.setPlaneExposureTime(exposure, getSeries(), planeIndex); // 0 exposure do not make sense
            }
            if ((incrementTimeOverZ>0)||(incrementTimeOverT>0)) {
              store.setPlaneDeltaT(new Time(incrementTimeOverT * iT + incrementTimeOverZ * iZ,
                      UNITS.SECOND), getSeries(), planeIndex);
            }
          }
        }

        for (int iZ = 0; iZ < getSizeZ(); iZ++) {
          for (int iT = 0; iT < getSizeT(); iT++) {
            int planeIndex = getIndex(iZ, iChannel, iT);
            store.setPlanePositionX(stagePosX, getSeries(), planeIndex);
            store.setPlanePositionY(stagePosY, getSeries(), planeIndex);
            store.setPlanePositionZ(stagePosZ, getSeries(), planeIndex);
            if (sbmziti.exposureTime != 0) {
              store.setPlaneExposureTime(exposure, getSeries(), planeIndex); // 0 exposure do not make sense
            }
            if ((incrementTimeOverZ>0)||(incrementTimeOverT>0)) {
              store.setPlaneDeltaT(new Time(incrementTimeOverT * iT + incrementTimeOverZ * iZ,
                      UNITS.SECOND), getSeries(), planeIndex);
            }
          }
        }

      }

    }

    for (int iSeries = 0; iSeries<getSeriesCount(); iSeries++) {
      setSeries(iSeries);
      store.setImageInstrumentRef(MetadataTools.createLSID("Instrument", 0), iSeries);
      if (coreIndexTimeStamp[getCoreIndex()]!=null) {
        store.setImageAcquisitionDate(coreIndexTimeStamp[getCoreIndex()], iSeries);
      } else if (acquiredDate != null) {
        store.setImageAcquisitionDate(new Timestamp(acquiredDate), iSeries);
      }
      if (timeIncrement != null) {
        store.setPixelsTimeIncrement(timeIncrement, iSeries);
      }
    }

    Double startTime = null;
    if (acquiredDate != null) {
      Timestamp t = Timestamp.valueOf(acquiredDate);
      if (t != null)
        startTime = t.asInstant().getMillis() / 1000d;
    }

    boolean firstPlane = true;


    //addPlaneMetadata(iSeries);

  }



  /*
  private void addPlaneMetadata(int iSeries) {
      for (int plane=0; plane<getImageCount(); plane++) {
          Coordinate coordinate = new Coordinate(seriesToCoreIndex(iSeries), plane, getImageCount());
          ArrayList<Integer> index = indexIntoPlanes.get(coordinate);
          if (index == null) {
              continue;
          }

          SubBlock p = planes.get(index.get(0));
          if (startTime == null) {
              startTime = p.timestamp;
          }

          if (firstPlane) {
              if (!hasFlattenedResolutions()) {
                  positionIndex = i;
              }
              else if (p.resolutionIndex == 0) {
                  positionIndex++;
              }
              firstPlane = false;
          }

          Double minStageX = null;
          Double maxStageX = null;
          Double minStageY = null;
          Double maxStageY = null;
          for (Integer q : index) {
              SubBlock currentPlane = planes.get(q);
              if (currentPlane == null) continue;
              if (storeRelativePositions()) {
                  if (minStageX == null || currentPlane.col < minStageX) {
                      minStageX = (double) currentPlane.col;
                  }
                  if (maxStageX == null || currentPlane.col > maxStageX) {
                      maxStageX = (double) currentPlane.col;
                  }
              }
              else if (currentPlane.stageX != null) {
                  if (minStageX == null ||
                          currentPlane.stageX.value().doubleValue() < minStageX)
                  {
                      minStageX = currentPlane.stageX.value().doubleValue();
                  }
                  if (maxStageX == null ||
                          currentPlane.stageX.value().doubleValue() > maxStageX)
                  {
                      maxStageX = currentPlane.stageX.value().doubleValue();
                  }
              }
              if (storeRelativePositions()) {
                  if (minStageY == null || currentPlane.row < minStageY) {
                      minStageY = (double) currentPlane.row;
                  }
                  if (maxStageY == null || currentPlane.row > maxStageY) {
                      maxStageY = (double) currentPlane.row;
                  }
              }
              else if (currentPlane.stageY != null) {
                  if (minStageY == null ||
                          currentPlane.stageY.value().doubleValue() < minStageY)
                  {
                      minStageY = currentPlane.stageY.value().doubleValue();
                  }
                  if (maxStageY == null ||
                          currentPlane.stageY.value().doubleValue() > maxStageY)
                  {
                      maxStageY = currentPlane.stageY.value().doubleValue();
                  }
              }
          }

          // if the XML-defined positions are used,
          // assign the same position to each resolution in a pyramid

          Length x = null;
          if (storeRelativePositions()) {
              x = new Length(minStageX, UNITS.PIXEL);
          }
          else if (minStageX != null && maxStageX != null) {
              double diff = (maxStageX - minStageX) / 2;
              x = new Length(minStageX + diff, UNITS.MICROMETER);
              if (positionsX != null) {
                  positionsX[positionIndex] = x;
              }
          }
          else if (positionsX != null && positionIndex < positionsX.length &&
                  positionsX[positionIndex] != null)
          {
              x = positionsX[positionIndex];
          }
          else if (p.stageX != null) {
              x = p.stageX;
          }
          else {
              x = new Length(p.col, UNITS.REFERENCEFRAME);
          }
          if (x != null) {
              store.setPlanePositionX(x, i, plane);
              if (plane == 0) {
                  store.setStageLabelX(x, i);
              }
          }

          Length y = null;
          if (storeRelativePositions()) {
              y = new Length(minStageY, UNITS.PIXEL);
          }
          else if (minStageY != null && maxStageY != null) {
              double diff = (maxStageY - minStageY) / 2;
              y = new Length(minStageY + diff, UNITS.MICROMETER);
              if (positionsY != null) {
                  positionsY[positionIndex] = y;
              }
          }
          else if (positionsY != null && positionIndex < positionsY.length &&
                  positionsY[positionIndex] != null)
          {
              y = positionsY[positionIndex];
          }
          else if (p.stageY != null) {
              y = p.stageY;
          }
          else {
              y = new Length(p.row, UNITS.REFERENCEFRAME);
          }
          if (y != null) {
              store.setPlanePositionY(y, i, plane);
              if (plane == 0) {
                  store.setStageLabelY(y, i);
              }
          }

          Length z = null;
          if (p.stageZ != null) {
              z = p.stageZ;
          }
          else if (positionsZ != null && positionIndex < positionsZ.length) {
              int zIndex = getZCTCoords(plane)[0];
              if (positionsZ[positionIndex] != null) {
                  if (zStep != null) {
                      double value = positionsZ[positionIndex].value(zStep.unit()).doubleValue();
                      if (zStep != null) {
                          value += zIndex * zStep.value().doubleValue();
                      }
                      z = new Length(value, zStep.unit());
                  }
                  else {
                      z = positionsZ[positionIndex];
                  }
              }
          }
          if (z != null) {
              store.setPlanePositionZ(z, i, plane);
              if (plane == 0) {
                  store.setStageLabelZ(z, i);
              }
          }
          if (plane == 0 && (x != null || y != null || z != null)) {
              store.setStageLabelName("Scene position #" + i, i);
          }

          if (p.timestamp != null) {
              store.setPlaneDeltaT(new Time(p.timestamp - startTime, UNITS.SECOND), i, plane);
          }
          else if (plane < timestamps.size() && timestamps.size() == getImageCount()) {
              // only use the plane index if there is one timestamp per plane
              if (timestamps.get(plane) != null) {
                  store.setPlaneDeltaT(new Time(timestamps.get(plane), UNITS.SECOND), i, plane);
              }
          }
          else if (getZCTCoords(plane)[2] < timestamps.size()) {
              // otherwise use the timepoint index, to prevent incorrect timestamping of channels
              int t = getZCTCoords(plane)[2];
              if (timestamps.get(t) != null) {
                  store.setPlaneDeltaT(new Time(timestamps.get(t), UNITS.SECOND), i, plane);
              }
          }
          if (p.exposureTime != null) {
              store.setPlaneExposureTime(new Time(p.exposureTime, UNITS.SECOND), i, plane);
          }
          else {
              int channel = getZCTCoords(plane)[1];
              if (channel < channels.size() &&
                      channels.get(channel).exposure != null)
              {
                  store.setPlaneExposureTime(
                          new Time(channels.get(channel).exposure, UNITS.SECOND), i, plane);
              }
          }
      }
  }
    */
  private void addChannelMetadata(int iSeries, boolean isPALM) {
    setSeries(iSeries);
    for (int c=0; c<getEffectiveSizeC(); c++) {
      if (c < channels.size()) {
        if (isPALM && iSeries < channels.size()) {
          store.setChannelName(channels.get(iSeries).name, iSeries, c);
        }
        else {
          store.setChannelName(channels.get(c).name, iSeries, c);
        }
        store.setChannelFluor(channels.get(c).fluor, iSeries, c);
        if (channels.get(c).filterSetRef != null) {
          store.setChannelFilterSetRef(channels.get(c).filterSetRef, iSeries, c);
        }

        String color = channels.get(c).color;
        if (color != null && !isRGB()) {
          color = color.replaceAll("#", "");
          if (color.length() > 6) {
            color = color.substring(2, color.length());
          }
          try {
            // shift by 8 to allow alpha in the final byte
            store.setChannelColor(
                    new Color((Integer.parseInt(color, 16) << 8) | 0xff), iSeries, c);
          }
          catch (NumberFormatException e) {
            LOGGER.warn("", e);
          }
        }

        String emWave = channels.get(c).emission;
        if (emWave != null) {
          Double wave = new Double(emWave);
          Length em = FormatTools.getEmissionWavelength(wave);
          if (em != null) {
            store.setChannelEmissionWavelength(em, iSeries, c);
          }
        }
        String exWave = channels.get(c).excitation;
        if (exWave != null) {
          Double wave = new Double(exWave);
          Length ex = FormatTools.getExcitationWavelength(wave);
          if (ex != null) {
            store.setChannelExcitationWavelength(ex, iSeries, c);
          }
        }

        if (channels.get(c).illumination != null) {
          store.setChannelIlluminationType(
                  channels.get(c).illumination, iSeries, c);
        }

        if (channels.get(c).pinhole != null) {
          store.setChannelPinholeSize(
                  new Length(new Double(channels.get(c).pinhole), UNITS.MICROMETER), iSeries, c);
        }

        if (channels.get(c).acquisitionMode != null) {
          store.setChannelAcquisitionMode(
                  channels.get(c).acquisitionMode, iSeries, c);
        }
      }

      if (c < detectorRefs.size()) {
        String detector = detectorRefs.get(c);
        store.setDetectorSettingsID(detector, iSeries, c);

        if (c < binnings.size()) {
          try {
            store.setDetectorSettingsBinning(MetadataTools.getBinning(binnings.get(c)), iSeries, c);
          } catch (FormatException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
          }
        }
        if (c < channels.size()) {
          store.setDetectorSettingsGain(channels.get(c).gain, iSeries, c);
        }
      }

      if (c < channels.size()) {
        if (hasDetectorSettings) {
          store.setDetectorSettingsGain(channels.get(c).gain, iSeries, c);
        }
      }
    }
  }

  private static int getDownSampling(LibCZI.SubBlockDirectorySegment.SubBlockDirectorySegmentData.SubBlockDirectoryEntry entry) {
    return entry.getDimension("X").size/entry.getDimension("X").storedSize;
  }

  private int[] setOriginAndSize(CoreMetadata ms0,
                                 List<LibCZI.SubBlockDirectorySegment.SubBlockDirectorySegmentData.SubBlockDirectoryEntry> blocks) {
    int minX = Integer.MAX_VALUE;
    int minY = Integer.MAX_VALUE;
    int minZ = Integer.MAX_VALUE;
    int minC = Integer.MAX_VALUE;
    int minT = Integer.MAX_VALUE;
    int maxX = -Integer.MAX_VALUE;
    int maxY = -Integer.MAX_VALUE;
    int maxZ = -Integer.MAX_VALUE;
    int maxC = -Integer.MAX_VALUE;
    int maxT = -Integer.MAX_VALUE;
    int downScale = getDownSampling(blocks.get(0));

    for (LibCZI.SubBlockDirectorySegment.SubBlockDirectorySegmentData.SubBlockDirectoryEntry block: blocks) {
      int blockSizeX = block.getDimension("X").storedSize;
      int blockSizeY = block.getDimension("Y").storedSize;
      if (blockSizeX>maxBlockSizeX) maxBlockSizeX = blockSizeX;
      if (blockSizeX>maxBlockSizeY) maxBlockSizeY = blockSizeY;

      int x_min = block.getDimension("X").start/downScale;
      int x_max = x_min+blockSizeX; // size or stored size ?
      int y_min = block.getDimension("Y").start/downScale;
      int y_max = y_min+blockSizeY;
      int z_min = block.getDimension("Z").start;
      int z_max = z_min+block.getDimension("Z").size;
      int c_min = block.getDimension("C").start;
      int c_max = c_min+block.getDimension("C").size;
      int t_min = block.getDimension("T").start;
      int t_max = t_min+block.getDimension("T").size;
      if (maxX<x_max) maxX = x_max;
      if (maxY<y_max) maxY = y_max;
      if (maxZ<z_max) maxZ = z_max;
      if (maxC<c_max) maxC = c_max;
      if (maxT<t_max) maxT = t_max;
      if (minX>x_min) minX = x_min;
      if (minY>y_min) minY = y_min;
      if (minZ>z_min) minZ = z_min;
      if (minC>c_min) minC = c_min;
      if (minT>t_min) minT = t_min;
    }

    ms0.sizeX = maxX-minX;
    ms0.sizeY = maxY-minY;
    ms0.sizeZ = maxZ-minZ;
    ms0.sizeC = maxC-minC;
    ms0.sizeT = maxT-minT;
    int[] originCoordinates = new int[2];
    originCoordinates[0] = minX;
    originCoordinates[1] = minY;
    return originCoordinates;
  }

  private static void convertPixelType(CoreMetadata ms0, int pixelType) throws FormatException {
    switch (pixelType) {
      case LibCZI.GRAY8:
        ms0.pixelType = FormatTools.UINT8;
        break;
      case LibCZI.GRAY16:
        ms0.pixelType = FormatTools.UINT16;
        break;
      case LibCZI.GRAY32:
        ms0.pixelType = FormatTools.UINT32;
        break;
      case LibCZI.GRAY_FLOAT:
        ms0.pixelType = FormatTools.FLOAT;
        break;
      case LibCZI.GRAY_DOUBLE:
        ms0.pixelType = FormatTools.DOUBLE;
        break;
      case LibCZI.BGR_24:
        ms0.pixelType = FormatTools.UINT8;
        ms0.sizeC *= 3;
        ms0.rgb = true;
        ms0.interleaved = true;
        break;
      case LibCZI.BGR_48:
        ms0.pixelType = FormatTools.UINT16;
        ms0.sizeC *= 3;
        ms0.rgb = true;
        ms0.interleaved = true;
        break;
      case LibCZI.BGRA_8:
        ms0.pixelType = FormatTools.UINT8;
        ms0.sizeC *= 4;
        ms0.rgb = true;
        ms0.interleaved = true;
        break;
      case LibCZI.BGR_FLOAT:
        ms0.pixelType = FormatTools.FLOAT;
        ms0.sizeC *= 3;
        ms0.rgb = true;
        ms0.interleaved = true;
        break;
      case LibCZI.COMPLEX:
      case LibCZI.COMPLEX_FLOAT:
        throw new FormatException("Sorry, complex pixel data not supported.");
      default:
        throw new FormatException("Unknown pixel type: " + pixelType);
    }
    ms0.interleaved = ms0.rgb;
  }

  public static boolean ignoreDimForSeries(String dimension, boolean autostitch) {
    switch (dimension) {
      case "X"://
      case "Y":
      case "Z":
      case "T":
      case "C": // TODO: deal with different pixel types! And with RGB!
        return true;
      case "M":
        return autostitch;
      default:
        return false;
    }
  }
  public static int dimensionPriority(String dimension) {
    switch (dimension) {
      case "X":
        return 0;
      case "Y":
        return 1;
      case "Z":
        return 2;
      case "T":
        return 3;
      case RESOLUTION_LEVEL_DIMENSION:
        return 4;
      case "M": // Mosaic
        return 5;
      case "C": // Channel
        return 6;
      case "R": // Rotation
        return 7;
      case "I": // Illumination
        return 8;
      case "H": // Phase
        return 9;
      case "V": // View : = Angle
        return 10;
      case "B": // Block - deprecated
        return 11;
      case "S": // Scene
        return 12;
      case FILE_PART_DIMENSION: // Scene
        return 13;
      default:
        throw new UnsupportedOperationException("Unknown dimension "+dimension);
    }
  }

  // --------------- METADATA

  //protected MetadataStore makeFilterMetadata() {
  //  return new FilterMetadata(getMetadataStore(), isMetadataFiltered());
  //}

  private void readXMLMetadata(LibCZI.MetaDataSegment metaDataSegment, DocumentBuilder parser) throws FormatException, IOException {
    String xml = metaDataSegment.data.xml;
    xml = XMLTools.sanitizeXML(xml);
    translateMetadata(xml, parser);
  }

  private void translateMetadata(String xml, DocumentBuilder parser) throws FormatException, IOException
  {
    Element root;
    try {
      ByteArrayInputStream s =
              new ByteArrayInputStream(xml.getBytes(Constants.ENCODING));
      root = parser.parse(s).getDocumentElement();
      s.close();
    }
    catch (SAXException e) {
      throw new FormatException(e);
    }

    if (root == null) {
      throw new FormatException("Could not parse the XML metadata.");
    }

    NodeList children = root.getChildNodes();
    Element realRoot = null;
    for (int i=0; i<children.getLength(); i++) {
      if (children.item(i) instanceof Element) {
        realRoot = (Element) children.item(i);
        break;
      }
    }

    translateExperiment(realRoot);
    translateInformation(realRoot);
    translateScaling(realRoot);
    translateDisplaySettings(realRoot);
    translateLayers(realRoot);
    translateHardwareSettings(realRoot);

    final Deque<String> nameStack = new ArrayDeque<>();
    populateOriginalMetadata(realRoot, nameStack);
  }

  AllPositionsInformation allPositionsInformation = new AllPositionsInformation();
  private void translateInformation(Element root) throws FormatException {
    NodeList informations = root.getElementsByTagName("Information");
    if (informations == null || informations.getLength() == 0) {
      return;
    }

    Element information = (Element) informations.item(0);
    Element image = getFirstNode(information, "Image");
    Element user = getFirstNode(information, "User");
    Element environment = getFirstNode(information, "Environment");
    Element instrument = getFirstNode(information, "Instrument");
    Element document = getFirstNode(information, "Document");

    if (image != null) {
      String bitCount = getFirstNodeValue(image, "ComponentBitCount");
      if (bitCount != null) {
        core.get(0).bitsPerPixel = Integer.parseInt(bitCount);
      }

      acquiredDate = getFirstNodeValue(image, "AcquisitionDateAndTime");

      Element objectiveSettings = getFirstNode(image, "ObjectiveSettings");
      correctionCollar =
              getFirstNodeValue(objectiveSettings, "CorrectionCollar");
      medium = getFirstNodeValue(objectiveSettings, "Medium");
      refractiveIndex = getFirstNodeValue(objectiveSettings, "RefractiveIndex");

      Element dimensions = getFirstNode(image, "Dimensions");

      Element tNode = getFirstNode(dimensions, "T");
      if (tNode != null) {
        Element positions = getFirstNode(tNode, "Positions");
        if (positions != null) {
          Element interval = getFirstNode(positions, "Interval");
          if (interval != null) {
            Element incrementNode = getFirstNode(interval, "Increment");
            if (incrementNode != null) {
              String increment = incrementNode.getTextContent();
              timeIncrement = new Time(DataTools.parseDouble(increment), UNITS.SECOND);
            }
          }
        }
      }

      allPositionsInformation.scenes = new ArrayList<>();

      Element sNode = getFirstNode(dimensions, "S");
      if (sNode != null) {
        NodeList scenes = sNode.getElementsByTagName("Scene");
        //int nextPosition = 0;
        for (int i=0; i<scenes.getLength(); i++) {
          SceneProperties currentSceneProps = new SceneProperties();
          allPositionsInformation.scenes.add(currentSceneProps);

          Element scene = (Element) scenes.item(i);
          NodeList positions = scene.getElementsByTagName("Position"); // What is this ? Mosaic ? Scene ?

          currentSceneProps.name = scene.getAttribute("Name");

          for (int p=0; p<positions.getLength(); p++) {
            Element position = (Element) positions.item(p);
            String x = position.getAttribute("X");
            String y = position.getAttribute("Y");
            String z = position.getAttribute("Z");
            System.out.println("Scene "+i+" pos "+p+" X="+x+" Y="+y+" Z="+z);
            //if (nextPosition < positionsX.length && positionsX[nextPosition] == null) {

            PositionLocation loc = new PositionLocation();
            loc.pX = new Length(DataTools.parseDouble(x), UNITS.MICROMETER);
            loc.pY = new Length(DataTools.parseDouble(y), UNITS.MICROMETER);
            loc.pZ = new Length(DataTools.parseDouble(z), UNITS.MICROMETER);

            currentSceneProps.pos.add(loc);
          }

          if (positions.getLength() == 0) {// && (mosaics <= 1 || (prestitched != null && prestitched))) {
            positions = scene.getElementsByTagName("CenterPosition");
            //if (positions.getLength() > 0 && nextPosition < positionsX.length) {
            Element position = (Element) positions.item(0);
            String[] pos = position.getTextContent().split(",");
            PositionLocation loc = new PositionLocation();

            loc.pX = new Length(DataTools.parseDouble(pos[0]), UNITS.MICROMETER);
            loc.pY = new Length(DataTools.parseDouble(pos[1]), UNITS.MICROMETER);

            currentSceneProps.pos.add(loc);
            System.out.println("Scene "+i+" center X="+pos[0]+" Y="+pos[1]);
            //}
            //nextPosition++;
          }

        }
      }

      NodeList channelNodes = getGrandchildren(dimensions, "Channel");
      if (channelNodes == null) {
        channelNodes = image.getElementsByTagName("Channel");
      }

      if (channelNodes != null) {
        for (int i=0; i<channelNodes.getLength(); i++) {
          Element channel = (Element) channelNodes.item(i);

          while (channels.size() <= i) {
            channels.add(new Channel());
          }

          channels.get(i).emission =
                  getFirstNodeValue(channel, "EmissionWavelength");
          channels.get(i).excitation =
                  getFirstNodeValue(channel, "ExcitationWavelength");
          channels.get(i).pinhole = getFirstNodeValue(channel, "PinholeSize");

          channels.get(i).name = channel.getAttribute("Name");

          String illumination = getFirstNodeValue(channel, "IlluminationType");
          if (illumination != null) {
            channels.get(i).illumination = MetadataTools.getIlluminationType(illumination);
          }
          String acquisition = getFirstNodeValue(channel, "AcquisitionMode");
          if (acquisition != null) {
            channels.get(i).acquisitionMode = MetadataTools.getAcquisitionMode(acquisition);
          }

          Element detectorSettings = getFirstNode(channel, "DetectorSettings");

          String binning = getFirstNodeValue(detectorSettings, "Binning");
          if (binning != null) {
            binning = binning.replaceAll(",", "x");
            binnings.add(binning);
          }

          Element scanInfo = getFirstNode(channel, "LaserScanInfo");
          if (scanInfo != null) {
            zoom = getFirstNodeValue(scanInfo, "ZoomX");
          }

          Element detector = getFirstNode(detectorSettings, "Detector");
          if (detector != null) {
            String detectorID = detector.getAttribute("Id");
            if (detectorID.indexOf(' ') != -1) {
              detectorID = detectorID.replaceAll("\\s","");
            }
            if (!detectorID.startsWith("Detector:")) {
              detectorID = "Detector:" + detectorID;
            }
            detectorRefs.add(detectorID);
          }

          Element filterSet = getFirstNode(channel, "FilterSetRef");
          if (filterSet != null) {
            channels.get(i).filterSetRef = filterSet.getAttribute("Id");
          }
        }
      }
    }

    if (user != null) {
      userDisplayName = getFirstNodeValue(user, "DisplayName");
      userFirstName = getFirstNodeValue(user, "FirstName");
      userLastName = getFirstNodeValue(user, "LastName");
      userMiddleName = getFirstNodeValue(user, "MiddleName");
      userEmail = getFirstNodeValue(user, "Email");
      userInstitution = getFirstNodeValue(user, "Institution");
      userName = getFirstNodeValue(user, "UserName");
    }

    if (environment != null) {
      temperature = getFirstNodeValue(environment, "Temperature");
      airPressure = getFirstNodeValue(environment, "AirPressure");
      humidity = getFirstNodeValue(environment, "Humidity");
      co2Percent = getFirstNodeValue(environment, "CO2Percent");
    }

    if (instrument != null) {
      NodeList microscopes = getGrandchildren(instrument, "Microscope");
      Element manufacturerNode = null;

      store.setInstrumentID(MetadataTools.createLSID("Instrument", 0), 0);

      if (microscopes != null) {
        Element microscope = (Element) microscopes.item(0);
        manufacturerNode = getFirstNode(microscope, "Manufacturer");

        store.setMicroscopeManufacturer(
                getFirstNodeValue(manufacturerNode, "Manufacturer"), 0);
        store.setMicroscopeModel(
                getFirstNodeValue(manufacturerNode, "Model"), 0);
        store.setMicroscopeSerialNumber(
                getFirstNodeValue(manufacturerNode, "SerialNumber"), 0);
        store.setMicroscopeLotNumber(
                getFirstNodeValue(manufacturerNode, "LotNumber"), 0);

        String microscopeType = getFirstNodeValue(microscope, "Type");
        if (microscopeType != null) {
          store.setMicroscopeType(MetadataTools.getMicroscopeType(microscopeType), 0);
        }
      }

      NodeList lightSources = getGrandchildren(instrument, "LightSource");
      if (lightSources != null) {
        for (int i=0; i<lightSources.getLength(); i++) {
          Element lightSource = (Element) lightSources.item(i);
          manufacturerNode = getFirstNode(lightSource, "Manufacturer");

          String manufacturer =
                  getFirstNodeValue(manufacturerNode, "Manufacturer");
          String model = getFirstNodeValue(manufacturerNode, "Model");
          String serialNumber =
                  getFirstNodeValue(manufacturerNode, "SerialNumber");
          String lotNumber = getFirstNodeValue(manufacturerNode, "LotNumber");

          String type = getFirstNodeValue(lightSource, "LightSourceType");
          String power = getFirstNodeValue(lightSource, "Power");
          if ("Laser".equals(type)) {
            if (power != null) {
              store.setLaserPower(new Power(new Double(power), UNITS.MILLIWATT), 0, i);
            }
            store.setLaserLotNumber(lotNumber, 0, i);
            store.setLaserManufacturer(manufacturer, 0, i);
            store.setLaserModel(model, 0, i);
            store.setLaserSerialNumber(serialNumber, 0, i);
          }
          else if ("Arc".equals(type)) {
            if (power != null) {
              store.setArcPower(new Power(new Double(power), UNITS.MILLIWATT), 0, i);
            }
            store.setArcLotNumber(lotNumber, 0, i);
            store.setArcManufacturer(manufacturer, 0, i);
            store.setArcModel(model, 0, i);
            store.setArcSerialNumber(serialNumber, 0, i);
          }
          else if ("LightEmittingDiode".equals(type)) {
            if (power != null) {
              store.setLightEmittingDiodePower(new Power(new Double(power), UNITS.MILLIWATT), 0, i);
            }
            store.setLightEmittingDiodeLotNumber(lotNumber, 0, i);
            store.setLightEmittingDiodeManufacturer(manufacturer, 0, i);
            store.setLightEmittingDiodeModel(model, 0, i);
            store.setLightEmittingDiodeSerialNumber(serialNumber, 0, i);
          }
          else if ("Filament".equals(type)) {
            if (power != null) {
              store.setFilamentPower(new Power(new Double(power), UNITS.MILLIWATT), 0, i);
            }
            store.setFilamentLotNumber(lotNumber, 0, i);
            store.setFilamentManufacturer(manufacturer, 0, i);
            store.setFilamentModel(model, 0, i);
            store.setFilamentSerialNumber(serialNumber, 0, i);
          }
        }
      }

      NodeList detectors = getGrandchildren(instrument, "Detector");
      if (detectors != null) {
        HashSet<String> uniqueDetectors = new HashSet<String>();
        for (int i=0; i<detectors.getLength(); i++) {
          Element detector = (Element) detectors.item(i);

          manufacturerNode = getFirstNode(detector, "Manufacturer");
          String manufacturer =
                  getFirstNodeValue(manufacturerNode, "Manufacturer");
          String model = getFirstNodeValue(manufacturerNode, "Model");
          String serialNumber =
                  getFirstNodeValue(manufacturerNode, "SerialNumber");
          String lotNumber = getFirstNodeValue(manufacturerNode, "LotNumber");

          String detectorID = detector.getAttribute("Id");
          if (detectorID.indexOf(' ') != -1) {
            detectorID = detectorID.replaceAll("\\s","");
          }
          if (!detectorID.startsWith("Detector:")) {
            detectorID = "Detector:" + detectorID;
          }
          if (uniqueDetectors.contains(detectorID)) {
            continue;
          }
          uniqueDetectors.add(detectorID);
          int detectorIndex = uniqueDetectors.size() - 1;

          store.setDetectorID(detectorID, 0, detectorIndex);
          store.setDetectorManufacturer(manufacturer, 0, detectorIndex);
          store.setDetectorModel(model, 0, detectorIndex);
          store.setDetectorSerialNumber(serialNumber, 0, detectorIndex);
          store.setDetectorLotNumber(lotNumber, 0, detectorIndex);


          gain = getFirstNodeValue(detector, "Gain");
          if (gain != null && !gain.isEmpty()) {
            if (detectorIndex == 0 || detectorIndex >= gains.size()) {
              store.setDetectorGain(DataTools.parseDouble(gain), 0, detectorIndex);
            }
            else {
              store.setDetectorGain(
                      DataTools.parseDouble(gains.get(detectorIndex)), 0,
                      detectorIndex);
            }
          }

          String offset = getFirstNodeValue(detector, "Offset");
          if (offset != null && !offset.equals("")) {
            store.setDetectorOffset(new Double(offset), 0, detectorIndex);
          }

          zoom = getFirstNodeValue(detector, "Zoom");
          if (zoom != null && !zoom.isEmpty()) {
            if (zoom != null && !zoom.equals("")) {
              if (zoom.indexOf(',') != -1) {
                zoom = zoom.substring(0, zoom.indexOf(','));
              }
              store.setDetectorZoom(DataTools.parseDouble(zoom), 0, detectorIndex);
            }
          }

          String ampGain = getFirstNodeValue(detector, "AmplificationGain");
          if (ampGain != null && !ampGain.equals("")) {
            store.setDetectorAmplificationGain(new Double(ampGain), 0, detectorIndex);
          }

          String detectorType = getFirstNodeValue(detector, "Type");
          if (detectorType != null && !detectorType.equals("")) {
            store.setDetectorType(MetadataTools.getDetectorType(detectorType), 0, detectorIndex);
          }
        }
      }

      NodeList objectives = getGrandchildren(instrument, "Objective");
      parseObjectives(objectives);

      NodeList filterSets = getGrandchildren(instrument, "FilterSet");
      if (filterSets != null) {
        for (int i=0; i<filterSets.getLength(); i++) {
          Element filterSet = (Element) filterSets.item(i);
          manufacturerNode = getFirstNode(filterSet, "Manufacturer");

          String manufacturer =
                  getFirstNodeValue(manufacturerNode, "Manufacturer");
          String model = getFirstNodeValue(manufacturerNode, "Model");
          String serialNumber =
                  getFirstNodeValue(manufacturerNode, "SerialNumber");
          String lotNumber = getFirstNodeValue(manufacturerNode, "LotNumber");

          String dichroicRef = getFirstNodeValue(filterSet, "DichroicRef");
          NodeList excitations = getGrandchildren(
                  filterSet, "ExcitationFilters", "ExcitationFilterRef");
          NodeList emissions = getGrandchildren(filterSet, "EmissionFilters",
                  "EmissionFilterRef");

          if (dichroicRef == null || dichroicRef.length() <= 0) {
            Element ref = getFirstNode(filterSet, "DichroicRef");
            if (ref != null) {
              dichroicRef = ref.getAttribute("Id");
            }
          }

          if (excitations == null) {
            excitations = filterSet.getElementsByTagName("ExcitationFilterRef");
          }

          if (emissions == null) {
            emissions = filterSet.getElementsByTagName("EmissionFilterRef");
          }

          if (dichroicRef != null || excitations != null || emissions != null) {
            store.setFilterSetID(filterSet.getAttribute("Id"), 0, i);
            store.setFilterSetManufacturer(manufacturer, 0, i);
            store.setFilterSetModel(model, 0, i);
            store.setFilterSetSerialNumber(serialNumber, 0, i);
            store.setFilterSetLotNumber(lotNumber, 0, i);
          }

          if (dichroicRef != null && dichroicRef.length() > 0) {
            store.setFilterSetDichroicRef(dichroicRef, 0, i);
          }

          if (excitations != null) {
            for (int ex=0; ex<excitations.getLength(); ex++) {
              Element excitation = (Element) excitations.item(ex);
              String ref = excitation.getTextContent();
              if (ref == null || ref.length() <= 0) {
                ref = excitation.getAttribute("Id");
              }
              if (ref != null && ref.length() > 0) {
                store.setFilterSetExcitationFilterRef(ref, 0, i, ex);
              }
            }
          }
          if (emissions != null) {
            for (int em=0; em<emissions.getLength(); em++) {
              Element emission = (Element) emissions.item(em);
              String ref = emission.getTextContent();
              if (ref == null || ref.length() <= 0) {
                ref = emission.getAttribute("Id");
              }
              if (ref != null && ref.length() > 0) {
                store.setFilterSetEmissionFilterRef(ref, 0, i, em);
              }
            }
          }
        }
      }

      NodeList filters = getGrandchildren(instrument, "Filter");
      if (filters != null) {
        for (int i=0; i<filters.getLength(); i++) {
          Element filter = (Element) filters.item(i);
          manufacturerNode = getFirstNode(filter, "Manufacturer");

          String manufacturer =
                  getFirstNodeValue(manufacturerNode, "Manufacturer");
          String model = getFirstNodeValue(manufacturerNode, "Model");
          String serialNumber =
                  getFirstNodeValue(manufacturerNode, "SerialNumber");
          String lotNumber = getFirstNodeValue(manufacturerNode, "LotNumber");

          store.setFilterID(filter.getAttribute("Id"), 0, i);
          store.setFilterManufacturer(manufacturer, 0, i);
          store.setFilterModel(model, 0, i);
          store.setFilterSerialNumber(serialNumber, 0, i);
          store.setFilterLotNumber(lotNumber, 0, i);

          String filterType = getFirstNodeValue(filter, "Type");
          if (filterType != null) {
            store.setFilterType(MetadataTools.getFilterType(filterType), 0, i);
          }
          store.setFilterFilterWheel(
                  getFirstNodeValue(filter, "FilterWheel"), 0, i);

          Element transmittance = getFirstNode(filter, "TransmittanceRange");

          String cutIn = getFirstNodeValue(transmittance, "CutIn");
          String cutOut = getFirstNodeValue(transmittance, "CutOut");
          Double inWave = cutIn == null ? 0 : new Double(cutIn);
          Double outWave = cutOut == null ? 0 : new Double(cutOut);

          Length in = FormatTools.getCutIn(inWave);
          Length out = FormatTools.getCutOut(outWave);
          if (in != null) {
            store.setTransmittanceRangeCutIn(in, 0, i);
          }
          if (out != null) {
            store.setTransmittanceRangeCutOut(out, 0, i);
          }

          String inTolerance =
                  getFirstNodeValue(transmittance, "CutInTolerance");
          String outTolerance =
                  getFirstNodeValue(transmittance, "CutOutTolerance");

          if (inTolerance != null) {
            Double cutInTolerance = new Double(inTolerance);
            store.setTransmittanceRangeCutInTolerance(
                    new Length(cutInTolerance, UNITS.NANOMETER), 0, i);
          }

          if (outTolerance != null) {
            Double cutOutTolerance = new Double(outTolerance);
            store.setTransmittanceRangeCutOutTolerance(
                    new Length(cutOutTolerance, UNITS.NANOMETER), 0, i);
          }

          String transmittancePercent =
                  getFirstNodeValue(transmittance, "Transmittance");
          if (transmittancePercent != null) {
            store.setTransmittanceRangeTransmittance(
                    PercentFraction.valueOf(transmittancePercent), 0, i);
          }
        }
      }

      NodeList dichroics = getGrandchildren(instrument, "Dichroic");
      if (dichroics != null) {
        for (int i=0; i<dichroics.getLength(); i++) {
          Element dichroic = (Element) dichroics.item(i);
          manufacturerNode = getFirstNode(dichroic, "Manufacturer");

          String manufacturer =
                  getFirstNodeValue(manufacturerNode, "Manufacturer");
          String model = getFirstNodeValue(manufacturerNode, "Model");
          String serialNumber =
                  getFirstNodeValue(manufacturerNode, "SerialNumber");
          String lotNumber = getFirstNodeValue(manufacturerNode, "LotNumber");

          store.setDichroicID(dichroic.getAttribute("Id"), 0, i);
          store.setDichroicManufacturer(manufacturer, 0, i);
          store.setDichroicModel(model, 0, i);
          store.setDichroicSerialNumber(serialNumber, 0, i);
          store.setDichroicLotNumber(lotNumber, 0, i);
        }
      }
    }

    if (document != null) {
      description = getFirstNodeValue(document, "Description");

      if (userName == null) {
        userName = getFirstNodeValue(document, "UserName");
      }

      imageName = getFirstNodeValue(document, "Name");
    }
  }

  private void translateScaling(Element root) {
    NodeList scalings = root.getElementsByTagName("Scaling");
    if (scalings == null || scalings.getLength() == 0) {
      return;
    }

    Element scaling = (Element) scalings.item(0);
    NodeList distances = getGrandchildren(scaling, "Items", "Distance");

    if (distances != null) {

      for (int i=0; i<distances.getLength(); i++) {
        Element distance = (Element) distances.item(i);
        String id = distance.getAttribute("Id");
        String originalValue = getFirstNodeValue(distance, "Value");
        if (originalValue == null) {
          continue;
        }
        Double value = new Double(originalValue) * 1000000;
        if (value > 0) {
          int oldSerie = -1;
          for (int iCoreIndex=0; iCoreIndex<core.size(); iCoreIndex++) {
            /*coreIndex = iCoreIndex-1; // Forces recomputation on next line.... RAAAAaaaaah!
            series = coreIndexToSeries(iCoreIndex);
            coreIndex = iCoreIndex;
            resolution = iCoreIndex-series;*/
            // HACKY THING BECAUSE calling setCoreIndex(iCoreIndex); do not set the right resolution!!  TODO : post an issue
            setCoreIndex(iCoreIndex);  // THIS JUST DOES NOT SET THE RIGHT RESOLUTION!! TODO: Post an issue
            // Hence this hack:
            boolean resolutionZero = getSeries()!=oldSerie;
            oldSerie = getSeries();

            System.out.println("series = "+getSeries()+" core = "+getCoreIndex()+" resolutionZero = "+resolutionZero);

            // Issue : the resolution level is not correctly set, it has to be set explicitely

            int extraIndex = getCoreIndex() - (core.size() - extraImages.size());
            if (extraIndex >= 0) {
              continue;
            }

            PositiveFloat size = new PositiveFloat(value);
            // Assumption

            if (id.equals("X")) {
              System.out.println("Flattened Res = "+hasFlattenedResolutions());

              coreToPixSizeX.put(getCoreIndex(), FormatTools.createLength(size.getValue() * coreIndexToDownscaleFactor.get(getCoreIndex()), UNITS.MICROMETER));
              if (resolutionZero) {
                System.out.println("Resolution is 0!");
                store.setPixelsPhysicalSizeX(coreToPixSizeX.get(getCoreIndex()), getSeries());
              }
            } else if (id.equals("Y")) {
              coreToPixSizeY.put(getCoreIndex(), FormatTools.createLength(size.getValue() * coreIndexToDownscaleFactor.get(getCoreIndex()), UNITS.MICROMETER));
              if (resolutionZero) {
                store.setPixelsPhysicalSizeY(coreToPixSizeY.get(getCoreIndex()), getSeries());
              }
            } else if (id.equals("Z")) {
              zStep = FormatTools.createLength(size, UNITS.MICROMETER);
              coreToPixSizeZ.put(getCoreIndex(), zStep);
              if (resolutionZero) {
                store.setPixelsPhysicalSizeZ(zStep, getSeries());
              }
            }
          }
        }
        else {
          LOGGER.debug(
                  "Expected positive value for PhysicalSize; got {}", value);
        }
      }
    }
  }

  Map<Integer, Length> coreToPixSizeX = new HashMap<>(), coreToPixSizeY = new HashMap<>(), coreToPixSizeZ = new HashMap<>(); // Because I can't read from the store.... RAAHAAH

  private void translateDisplaySettings(Element root) throws FormatException {
    NodeList displaySettings = root.getElementsByTagName("DisplaySetting");
    if (displaySettings == null || displaySettings.getLength() == 0) {
      return;
    }

    for (int display=0; display<displaySettings.getLength(); display++) {
      Element displaySetting = (Element) displaySettings.item(display);
      NodeList channelNodes = getGrandchildren(displaySetting, "Channel");

      if (channelNodes != null) {
        for (int i=0; i<channelNodes.getLength(); i++) {
          Element channel = (Element) channelNodes.item(i);
          String color = getFirstNodeValue(channel, "Color");
          if (color == null) {
            color = getFirstNodeValue(channel, "OriginalColor");
          }

          while (channels.size() <= i) {
            channels.add(new Channel());
          }
          channels.get(i).color = color;

          String fluor = getFirstNodeValue(channel, "DyeName");
          if (fluor != null) {
            channels.get(i).fluor = fluor;
          }
          String name = channel.getAttribute("Name");
          if (name != null) {
            channels.get(i).name = name;
          }

          String emission = getFirstNodeValue(channel, "DyeMaxEmission");
          if (emission != null) {
            channels.get(i).emission = emission;
          }
          String excitation = getFirstNodeValue(channel, "DyeMaxExcitation");
          if (excitation != null) {
            channels.get(i).excitation = excitation;
          }

          String illumination = getFirstNodeValue(channel, "IlluminationType");

          if (illumination != null && (channels.get(i).illumination == null || channels.get(i).illumination == IlluminationType.OTHER)) {
            channels.get(i).illumination = MetadataTools.getIlluminationType(illumination);
          }
        }
      }
    }
  }

  private void translateLayers(Element root) {
    NodeList layerses = root.getElementsByTagName("Layers");
    if (layerses == null || layerses.getLength() == 0) {
      return;
    }

    Element layersNode = (Element) layerses.item(0);
    NodeList layers = layersNode.getElementsByTagName("Layer");

    if (layers != null) {
      int roiCount = 0;

      for (int i=0; i<layers.getLength(); i++) {
        Element layer = (Element) layers.item(i);

        NodeList elementses = layer.getElementsByTagName("Elements");
        if (elementses.getLength() == 0) {
          continue;
        }
        NodeList allGrandchildren = elementses.item(0).getChildNodes();

        int shape = 0;

        NodeList lines = getGrandchildren(layer, "Elements", "Line");
        shape = populateLines(lines, roiCount, shape);

        NodeList arrows = getGrandchildren(layer, "Elements", "OpenArrow");
        shape = populateLines(arrows, roiCount, shape);

        NodeList crosses = getGrandchildren(layer, "Elements", "Cross");
        for (int s=0; s<crosses.getLength(); s++, shape+=2) {
          Element cross = (Element) crosses.item(s);

          Element geometry = getFirstNode(cross, "Geometry");
          Element textElements = getFirstNode(cross, "TextElements");
          Element attributes = getFirstNode(cross, "Attributes");

          store.setLineID(
                  MetadataTools.createLSID("Shape", roiCount, shape), roiCount, shape);
          store.setLineID(
                  MetadataTools.createLSID("Shape", roiCount, shape + 1), roiCount, shape + 1);

          String length = getFirstNodeValue(geometry, "Length");
          String centerX = getFirstNodeValue(geometry, "CenterX");
          String centerY = getFirstNodeValue(geometry, "CenterY");

          if (length != null) {
            Double halfLen = new Double(length) / 2;
            if (centerX != null) {
              store.setLineX1(new Double(centerX) - halfLen, roiCount, shape);
              store.setLineX2(new Double(centerX) + halfLen, roiCount, shape);

              store.setLineX1(new Double(centerX), roiCount, shape + 1);
              store.setLineX2(new Double(centerX), roiCount, shape + 1);
            }
            if (centerY != null) {
              store.setLineY1(new Double(centerY), roiCount, shape);
              store.setLineY2(new Double(centerY), roiCount, shape);

              store.setLineY1(new Double(centerY) - halfLen, roiCount, shape + 1);
              store.setLineY2(new Double(centerY) + halfLen, roiCount, shape + 1);
            }
          }
          store.setLineText(getFirstNodeValue(textElements, "Text"), roiCount, shape);
          store.setLineText(getFirstNodeValue(textElements, "Text"), roiCount, shape + 1);
        }

        NodeList rectangles = getGrandchildren(layer, "Elements", "Rectangle");
        if (rectangles != null) {
          shape = populateRectangles(rectangles, roiCount, shape);
        }

        NodeList ellipses = getGrandchildren(layer, "Elements", "Ellipse");
        if (ellipses != null) {
          for (int s=0; s<ellipses.getLength(); s++, shape++) {
            Element ellipse = (Element) ellipses.item(s);

            Element geometry = getFirstNode(ellipse, "Geometry");
            Element textElements = getFirstNode(ellipse, "TextElements");
            Element attributes = getFirstNode(ellipse, "Attributes");

            store.setEllipseID(
                    MetadataTools.createLSID("Shape", roiCount, shape), roiCount, shape);

            String radiusX = getFirstNodeValue(geometry, "RadiusX");
            String radiusY = getFirstNodeValue(geometry, "RadiusY");
            String centerX = getFirstNodeValue(geometry, "CenterX");
            String centerY = getFirstNodeValue(geometry, "CenterY");

            if (radiusX != null) {
              store.setEllipseRadiusX(new Double(radiusX), roiCount, shape);
            }
            if (radiusY != null) {
              store.setEllipseRadiusY(new Double(radiusY), roiCount, shape);
            }
            if (centerX != null) {
              store.setEllipseX(new Double(centerX), roiCount, shape);
            }
            if (centerY != null) {
              store.setEllipseY(new Double(centerY), roiCount, shape);
            }
            store.setEllipseText(
                    getFirstNodeValue(textElements, "Text"), roiCount, shape);
          }
        }

        // translate all of the circle ROIs
        NodeList circles = getGrandchildren(layer, "Elements", "Circle");
        if (circles != null) {
          shape = populateCircles(circles, roiCount, shape);
        }
        NodeList inOutCircles =
                getGrandchildren(layer, "Elements", "InOutCircle");
        if (inOutCircles != null) {
          shape = populateCircles(inOutCircles, roiCount, shape);
        }
        NodeList outInCircles =
                getGrandchildren(layer, "Elements", "OutInCircle");
        if (outInCircles != null) {
          shape = populateCircles(outInCircles, roiCount, shape);
        }
        NodeList pointsCircles =
                getGrandchildren(layer, "Elements", "PointsCircle");
        if (pointsCircles != null) {
          shape = populateCircles(pointsCircles, roiCount, shape);
        }

        NodeList polygons = getGrandchildren(layer, "Elements", "Polygon");
        if (polygons != null) {
          shape = populatePolylines(polygons, roiCount, shape, true);
        }

        NodeList polylines = getGrandchildren(layer, "Elements", "Polyline");
        if (polylines != null) {
          shape = populatePolylines(polylines, roiCount, shape, false);
        }

        NodeList openPolylines =
                getGrandchildren(layer, "Elements", "OpenPolyline");
        if (openPolylines != null) {
          shape = populatePolylines(openPolylines, roiCount, shape, false);
        }

        NodeList closedPolylines =
                getGrandchildren(layer, "Elements", "ClosedPolyline");
        if (closedPolylines != null) {
          shape = populatePolylines(closedPolylines, roiCount, shape, true);
        }

        NodeList beziers =
                getGrandchildren(layer, "Elements", "Bezier");
        if (beziers != null) {
          shape = populatePolylines(beziers, roiCount, shape, true);
        }

        NodeList rectRoi = getGrandchildren(layer, "Elements", "RectRoi");
        if (rectRoi != null) {
          shape = populateRectangles(rectRoi, roiCount, shape);
        }
        NodeList textBoxes = getGrandchildren(layer, "Elements", "TextBox");
        if (textBoxes != null) {
          shape = populateRectangles(textBoxes, roiCount, shape);
        }
        NodeList text = getGrandchildren(layer, "Elements", "Text");
        if (text != null) {
          shape = populateRectangles(text, roiCount, shape);
        }

        NodeList events = getGrandchildren(layer, "Elements", "Events");
        if (events != null) {
          for (int s=0; s<events.getLength(); s++) {
            Element event = (Element) events.item(s);

            Element geometry = getFirstNode(event, "Geometry");
            Element textElements = getFirstNode(event, "TextElements");
            Element attributes = getFirstNode(event, "Attributes");
            Element features = getFirstNode(event, "Features");

            String points = getFirstNodeValue(geometry, "Points");
            if (points != null) {
              String[] coords = points.split(" ");

              for (String point : coords) {
                String[] xy = point.split(",");
                if (xy.length == 2) {
                  String pointID =
                          MetadataTools.createLSID("Shape", roiCount, shape);
                  store.setPointID(pointID, roiCount, shape);
                  store.setPointX(
                          DataTools.parseDouble(xy[0]), roiCount, shape);
                  store.setPointY(
                          DataTools.parseDouble(xy[1]), roiCount, shape);
                  shape++;
                }
              }
            }
          }
        }

        if (shape > 0) {
          String roiID = MetadataTools.createLSID("ROI", roiCount);
          store.setROIID(roiID, roiCount);
          store.setROIName(layer.getAttribute("Name"), roiCount);
          store.setROIDescription(getFirstNodeValue(layer, "Usage"), roiCount);

          for (int series=0; series<getSeriesCount(); series++) {
            store.setImageROIRef(roiID, series, roiCount);
          }
          roiCount++;
        }
      }
    }
  }

  private void translateHardwareSettings(Element root) throws FormatException {
    NodeList hardwareSettings = root.getElementsByTagName("HardwareSetting");
    if (hardwareSettings == null || hardwareSettings.getLength() == 0) {
      return;
    }

    Element hardware = (Element) hardwareSettings.item(0);

    store.setInstrumentID(MetadataTools.createLSID("Instrument", 0), 0);

    Element microscope = getFirstNode(hardware, "Microscope");
    if (microscope != null) {
      String model = microscope.getAttribute("Name");
      store.setMicroscopeModel(model, 0);
    }

    Element objectiveChanger = getFirstNode(hardware, "ObjectiveChanger");
    if (objectiveChanger != null) {
      String position = getFirstNodeValue(objectiveChanger, "Position");
      int positionIndex = -1;
      if (position != null) {
        try {
          positionIndex = Integer.parseInt(position) - 1;
        }
        catch (NumberFormatException e) {
          LOGGER.debug("Could not parse ObjectiveSettings", e);
        }
      }

      NodeList objectives = objectiveChanger.getElementsByTagName("Objective");

      if (objectives != null) {
        for (int i=0; i<objectives.getLength(); i++) {
          Element objective = (Element) objectives.item(i);

          String objectiveID = MetadataTools.createLSID("Objective", 0, i);
          if (i == positionIndex ||
                  (objectives.getLength() == 1 && objectiveSettingsID != null))
          {
            objectiveSettingsID = objectiveID;
          }

          store.setObjectiveID(objectiveID, 0, i);
          store.setObjectiveModel(objective.getAttribute("Model"), 0, i);
          store.setObjectiveSerialNumber(
                  objective.getAttribute("UniqueName"), 0, i);

          String immersion = getFirstNodeValue(objective, "Immersions");
          store.setObjectiveImmersion(MetadataTools.getImmersion(immersion), 0, i);
          store.setObjectiveCorrection(MetadataTools.getCorrection("Other"), 0, i);

          String magnification = getFirstNodeValue(objective, "Magnification");
          String na = getFirstNodeValue(objective, "NumericalAperture");
          String wd = getFirstNodeValue(objective, "WorkingDistance");

          if (magnification != null) {
            try {
              store.setObjectiveNominalMagnification(
                      new Double(magnification), 0, i);
            }
            catch (NumberFormatException e) {
              LOGGER.debug("Could not parse magnification", e);
            }
          }
          if (na != null) {
            try {
              store.setObjectiveLensNA(new Double(na), 0, i);
            }
            catch (NumberFormatException e) {
              LOGGER.debug("Could not parse numerical aperture", e);
            }
          }
          if (wd != null) {
            try {
              store.setObjectiveWorkingDistance(new Length(new Double(wd), UNITS.MICROMETER), 0, i);
            }
            catch (NumberFormatException e) {
              LOGGER.debug("Could not parse working distance", e);
            }
          }
        }
      }
    }
  }

  private void translateExperiment(Element root) throws FormatException {
    NodeList experiments = root.getElementsByTagName("Experiment");
    if (experiments == null || experiments.getLength() == 0) {
      return;
    }

    Element experimentBlock = getFirstNode((Element) experiments.item(0), "ExperimentBlocks");
    Element acquisition = getFirstNode(experimentBlock, "AcquisitionBlock");

    // ---------------- POSITIONS
    readPositions(acquisition);

    // ---------------- DETECTORS
    {
      NodeList detectors = getGrandchildren(acquisition, "Detector");

      Element setup = getFirstNode(acquisition, "AcquisitionModeSetup");
      String cameraModel = getFirstNodeValue(setup, "SelectedCamera");

      if (detectors != null) {
        for (int i = 0; i < detectors.getLength(); i++) {
          Element detector = (Element) detectors.item(i);
          String id = MetadataTools.createLSID("Detector", 0, i);

          store.setDetectorID(id, 0, i);
          String model = detector.getAttribute("Id");
          store.setDetectorModel(model, 0, i);

          String bin = getFirstNodeValue(detector, "Binning");
          if (bin != null) {
            bin = bin.replaceAll(",", "x");
            Binning binning = MetadataTools.getBinning(bin);

            if (model != null && model.equals(cameraModel)) {
              for (int image = 0; image < getSeriesCount(); image++) {
                for (int c = 0; c < getEffectiveSizeC(); c++) {
                  store.setDetectorSettingsID(id, image, c);
                  store.setDetectorSettingsBinning(binning, image, c);
                }
              }
              hasDetectorSettings = true;
            }
          }
        }
      }

      Element multiTrack = getFirstNode(acquisition, "MultiTrackSetup");

      if (multiTrack == null) {
        return;
      }

      NodeList detectorGroups = multiTrack.getElementsByTagName("Detectors");
      for (int d = 0; d < detectorGroups.getLength(); d++) {
        Element detectorGroup = (Element) detectorGroups.item(d);
        detectors = detectorGroup.getElementsByTagName("Detector");

        if (detectors != null && detectors.getLength() > 0) {
          for (int i = 0; i < detectors.getLength(); i++) {
            Element detector = (Element) detectors.item(i);
            String voltage = getFirstNodeValue(detector, "Voltage");
            if (i == 0 && d == 0) {
              gain = voltage;
            }
            gains.add(voltage);
          }
        }
      }

      NodeList tracks = multiTrack.getElementsByTagName("Track");

      if (tracks != null && tracks.getLength() > 0) {
        for (int i = 0; i < tracks.getLength(); i++) {
          Element track = (Element) tracks.item(i);
          Element channel = getFirstNode(track, "Channel");
          String exposure = getFirstNodeValue(channel, "ExposureTime");
          String gain = getFirstNodeValue(channel, "EMGain");

          while (channels.size() <= i) {
            channels.add(new Channel());
          }

          try {
            if (exposure != null) {
              channels.get(i).exposure = new Double(exposure);
            }
          } catch (NumberFormatException e) {
            LOGGER.debug("Could not parse exposure time", e);
          }
          try {
            if (gain != null) {
              channels.get(i).gain = new Double(gain);
            }
          } catch (NumberFormatException e) {
            LOGGER.debug("Could not parse gain", e);
          }
        }
      }
    }
  }

  private void readPositions(Element acquisition) {

    Element tilesSetup = getFirstNode(acquisition, "TilesSetup");
    NodeList groups = getGrandchildren(tilesSetup, "PositionGroup");

    if (groups != null) {

      allPositionsInformation.groups = new ArrayList<>();

      for (int i=0; i<groups.getLength(); i++) {
        Element group = (Element) groups.item(i);

        Element position = getFirstNode(group, "Position");
        String tilesXValue = getFirstNodeValue(group, "TilesX");
        String tilesYValue = getFirstNodeValue(group, "TilesY");

        GroupProperties groupProperties = new GroupProperties();
        allPositionsInformation.groups.add(groupProperties);

        if (position != null && tilesXValue != null && !tilesXValue.isEmpty() && tilesYValue != null && !tilesYValue.isEmpty()) {
          Integer tilesX = DataTools.parseInteger(tilesXValue);
          Integer tilesY = DataTools.parseInteger(tilesYValue);
          groupProperties.nTilesX = tilesX;
          groupProperties.nTilesY = tilesY;

          String x = position.getAttribute("X");
          String y = position.getAttribute("Y");
          String z = position.getAttribute("Z");

          Length xPos = null;
          try {
            xPos = new Length(Double.valueOf(x), UNITS.METRE);
          }
          catch (NumberFormatException e) { }
          Length yPos = null;
          try {
            yPos = new Length(Double.valueOf(y), UNITS.METRE);
          }
          catch (NumberFormatException e) { }
          Length zPos = null;
          try {
            zPos = new Length(Double.valueOf(z), UNITS.METRE);
          }
          catch (NumberFormatException e) { }

          int numTiles = (tilesX == null || tilesY == null) ? 0 : tilesX * tilesY;
          for (int tile=0; tile<numTiles; tile++) {
            int index = i * tilesX * tilesY + tile;
            if (groups.getLength() == core.size()) {
              index = i;
            }

            TileProperties tileProperties = new TileProperties();
            tileProperties.pos.pX = xPos;
            tileProperties.pos.pY = yPos;
            tileProperties.pos.pZ = zPos;
            groupProperties.tiles.add(tileProperties);

          }
        }
      }
    } else {
      Element regionsSetup = getFirstNode(acquisition, "RegionsSetup");

      if (regionsSetup != null) {
        Element sampleHolder = getFirstNode(regionsSetup, "SampleHolder");
        if (sampleHolder != null) {
          Element template = getFirstNode(sampleHolder, "Template");
          if (template != null) {
            Element templateRows = getFirstNode(template, "ShapeRows");
            Element templateColumns = getFirstNode(template, "ShapeColumns");
            try {
              if (templateRows != null) {
                plateRows = Integer.parseInt(templateRows.getTextContent());
              }
              if (templateColumns != null) {
                plateColumns = Integer.parseInt(templateColumns.getTextContent());
              }
            }
            catch (NumberFormatException e) {
              LOGGER.debug("Could not parse sample holder dimensions", e);
            }

            NodeList wells = sampleHolder.getElementsByTagName("SingleTileRegionArray");
            if (wells == null || wells.getLength() == 0) {
              wells = sampleHolder.getElementsByTagName("TileRegion");
            }
            if (wells != null) {
              for (int i=0; i<wells.getLength(); i++) {
                Element well = (Element) wells.item(i);
                String value = getFirstNodeValue(well, "TemplateShapeId");
                if (value != null && !value.isEmpty()) {
                  platePositions.add(value);
                }
                String name = well.getAttribute("Name");
                for (int f=0; f<well.getElementsByTagName("SingleTileRegion").getLength(); f++) {
                  imageNames.add(name);
                }
              }
            }
          }

          NodeList regionArrays = sampleHolder.getElementsByTagName("SingleTileRegionArray");
          if (regionArrays != null) {
            int positionIndex = 0;

            allPositionsInformation.regions = new ArrayList<>();

            for (int r=0; r<regionArrays.getLength(); r++) {
              NodeList regions = ((Element) regionArrays.item(r)).getElementsByTagName("SingleTileRegion");
              if (regions != null) {
                for (int i=0; i<regions.getLength(); i++, positionIndex++) {
                  Element region = (Element) regions.item(i);

                  String x = getFirstNode(region, "X").getTextContent();
                  String y = getFirstNode(region, "Y").getTextContent();
                  String z = getFirstNode(region, "Z").getTextContent();
                  String name = region.getAttribute("Name");

                  // safe to assume all 3 arrays have the same length
                  //if (positionIndex < positionsX.length) {
                  PositionLocation loc = new PositionLocation();
                  if (x == null) {
                    loc.pX = null; //positionsX[positionIndex] = null;
                  } else {
                    final Double number = Double.valueOf(x);
                    loc.pX = new Length(number, UNITS.MICROMETER);// positionsX[positionIndex] = new Length(number, UNITS.MICROMETER);
                  }
                  if (y == null) {
                    loc.pY = null;//positionsY[positionIndex] = null;
                  } else {
                    final Double number = Double.valueOf(y);
                    loc.pY = new Length(number, UNITS.MICROMETER);//positionsY[positionIndex] = new Length(number, UNITS.MICROMETER);
                  }
                  if (z == null) {
                    loc.pZ = null;//positionsZ[positionIndex] = null;
                  } else {
                    final Double number = Double.valueOf(z);
                    loc.pZ = new Length(number, UNITS.MICROMETER);//positionsZ[positionIndex] = new Length(number, UNITS.MICROMETER);
                  }
                  allPositionsInformation.regions.add(loc);

                  fieldNames.add(name);
                }
              }
            }
          }
        }
      }
    }
  }

  private Element getFirstNode(Element root, String name) {
    if (root == null) {
      return null;
    }
    NodeList list = root.getElementsByTagName(name);
    if (list == null) {
      return null;
    }
    return (Element) list.item(0);
  }

  private NodeList getGrandchildren(Element root, String name) {
    return getGrandchildren(root, name + "s", name);
  }

  private NodeList getGrandchildren(Element root, String child, String name) {
    if (root == null) {
      return null;
    }
    NodeList children = root.getElementsByTagName(child);
    if (children != null && children.getLength() > 0) {
      Element childNode = (Element) children.item(0);
      return childNode.getElementsByTagName(name);
    }
    return null;
  }

  private String getFirstNodeValue(Element root, String name) {
    if (root == null) {
      return null;
    }
    NodeList nodes = root.getElementsByTagName(name);
    if (nodes != null && nodes.getLength() > 0) {
      return nodes.item(0).getTextContent();
    }
    return null;
  }

  private void populateOriginalMetadata(Element root, Deque<String> nameStack) {
    String name = root.getNodeName();
    nameStack.push(name);

    final StringBuilder key = new StringBuilder();
    String k = null;
    Iterator<String> keys = nameStack.descendingIterator();
    while (keys.hasNext()) {
      k = keys.next();
      if (!k.equals("Metadata") && (!k.endsWith("s") || k.equals(name))) {
        key.append(k);
        key.append("|");
      }
    }

    if (root.getChildNodes().getLength() == 1) {
      String value = root.getTextContent();
      if (value != null && key.length() > 0) {
        String s = key.toString();
        if (s.endsWith("|")){
          s = s.substring(0, s.length() - 1);
        }
        if (s.startsWith("DisplaySetting")) {
          addGlobalMeta(s, value);
        }
        else {
          addGlobalMetaList(s, value);
        }

        if (key.toString().endsWith("|Rotations|")) {
          rotationLabels = value.split(" ");
        }
        else if (key.toString().endsWith("|Phases|")) {
          phaseLabels = value.split(" ");
        }
        else if (key.toString().endsWith("|Illuminations|")) {
          illuminationLabels = value.split(" ");
        }
      }
    }
    NamedNodeMap attributes = root.getAttributes();
    for (int i=0; i<attributes.getLength(); i++) {
      Node attr = attributes.item(i);

      String attrName = attr.getNodeName();
      String attrValue = attr.getNodeValue();

      String keyString = key.toString();
      if (attrName.endsWith("|")){
        attrName = attrName.substring(0, attrName.length() - 1);
      }
      else if(attrName.length() == 0 && keyString.endsWith("|")) {
        keyString = keyString.substring(0, keyString.length() - 1);
      }

      if (keyString.startsWith("DisplaySetting")) {
        addGlobalMeta(keyString + attrName, attrValue);
      }
      else {
        addGlobalMetaList(keyString + attrName, attrValue);
      }
    }

    NodeList children = root.getChildNodes();
    if (children != null) {
      for (int i=0; i<children.getLength(); i++) {
        Object child = children.item(i);
        if (child instanceof Element) {
          populateOriginalMetadata((Element) child, nameStack);
        }
      }
    }

    nameStack.pop();
  }

  private void parseObjectives(NodeList objectives) throws FormatException {
    if (objectives != null) {
      for (int i=0; i<objectives.getLength(); i++) {
        Element objective = (Element) objectives.item(i);
        Element manufacturerNode = getFirstNode(objective, "Manufacturer");

        String manufacturer =
                getFirstNodeValue(manufacturerNode, "Manufacturer");
        String model = getFirstNodeValue(manufacturerNode, "Model");
        String serialNumber =
                getFirstNodeValue(manufacturerNode, "SerialNumber");
        String lotNumber = getFirstNodeValue(manufacturerNode, "LotNumber");

        if (objectiveSettingsID == null) {
          objectiveSettingsID = objective.getAttribute("Id");
        }
        store.setObjectiveID(objective.getAttribute("Id"), 0, i);
        store.setObjectiveManufacturer(manufacturer, 0, i);
        store.setObjectiveModel(model, 0, i);
        store.setObjectiveSerialNumber(serialNumber, 0, i);
        store.setObjectiveLotNumber(lotNumber, 0, i);

        String correction = getFirstNodeValue(objective, "Correction");
        if (correction != null) {
          store.setObjectiveCorrection(MetadataTools.getCorrection(correction), 0, i);
        }
        store.setObjectiveImmersion(
                MetadataTools.getImmersion(getFirstNodeValue(objective, "Immersion")), 0, i);

        String lensNA = getFirstNodeValue(objective, "LensNA");
        if (lensNA != null) {
          store.setObjectiveLensNA(new Double(lensNA), 0, i);
        }

        String magnification =
                getFirstNodeValue(objective, "NominalMagnification");
        if (magnification == null) {
          magnification = getFirstNodeValue(objective, "Magnification");
        }
        Double mag = magnification == null ? null : new Double(magnification);

        if (mag != null) {
          store.setObjectiveNominalMagnification(mag, 0, i);
        }
        String calibratedMag =
                getFirstNodeValue(objective, "CalibratedMagnification");
        if (calibratedMag != null) {
          store.setObjectiveCalibratedMagnification(
                  new Double(calibratedMag), 0, i);
        }
        String wd = getFirstNodeValue(objective, "WorkingDistance");
        if (wd != null) {
          store.setObjectiveWorkingDistance(new Length(new Double(wd), UNITS.MICROMETER), 0, i);
        }
        String iris = getFirstNodeValue(objective, "Iris");
        if (iris != null) {
          store.setObjectiveIris(new Boolean(iris), 0, i);
        }
      }
    }
  }

  private int populateLines(NodeList lines, int roi, int shape) {
    for (int s=0; s<lines.getLength(); s++, shape++) {
      Element line = (Element) lines.item(s);

      Element geometry = getFirstNode(line, "Geometry");
      Element textElements = getFirstNode(line, "TextElements");
      Element attributes = getFirstNode(line, "Attributes");

      String x1 = getFirstNodeValue(geometry, "X1");
      String x2 = getFirstNodeValue(geometry, "X2");
      String y1 = getFirstNodeValue(geometry, "Y1");
      String y2 = getFirstNodeValue(geometry, "Y2");

      store.setLineID(
              MetadataTools.createLSID("Shape", roi, shape), roi, shape);

      if (x1 != null) {
        store.setLineX1(new Double(x1), roi, shape);
      }
      if (x2 != null) {
        store.setLineX2(new Double(x2), roi, shape);
      }
      if (y1 != null) {
        store.setLineY1(new Double(y1), roi, shape);
      }
      if (y2 != null) {
        store.setLineY2(new Double(y2), roi, shape);
      }
      store.setLineText(getFirstNodeValue(textElements, "Text"), roi, shape);
    }
    return shape;
  }

  private int populateCircles(NodeList circles, int roi, int shape) {
    for (int s=0; s<circles.getLength(); s++, shape++) {
      Element circle = (Element) circles.item(s);
      Element geometry = getFirstNode(circle, "Geometry");
      Element textElements = getFirstNode(circle, "TextElements");
      Element attributes = getFirstNode(circle, "Attributes");

      store.setEllipseID(
              MetadataTools.createLSID("Shape", roi, shape), roi, shape);
      String radius = getFirstNodeValue(geometry, "Radius");
      String centerX = getFirstNodeValue(geometry, "CenterX");
      String centerY = getFirstNodeValue(geometry, "CenterY");

      if (radius != null) {
        store.setEllipseRadiusX(new Double(radius), roi, shape);
        store.setEllipseRadiusY(new Double(radius), roi, shape);
      }
      if (centerX != null) {
        store.setEllipseX(new Double(centerX), roi, shape);
      }
      if (centerY != null) {
        store.setEllipseY(new Double(centerY), roi, shape);
      }
      store.setEllipseText(getFirstNodeValue(textElements, "Text"), roi, shape);
    }
    return shape;
  }

  private int populateRectangles(NodeList rectangles, int roi, int shape) {
    for (int s=0; s<rectangles.getLength(); s++) {
      Element rectangle = (Element) rectangles.item(s);

      Element geometry = getFirstNode(rectangle, "Geometry");
      Element textElements = getFirstNode(rectangle, "TextElements");
      Element attributes = getFirstNode(rectangle, "Attributes");

      String left = getFirstNodeValue(geometry, "Left");
      String top = getFirstNodeValue(geometry, "Top");
      String width = getFirstNodeValue(geometry, "Width");
      String height = getFirstNodeValue(geometry, "Height");

      if (left != null && top != null && width != null && height != null) {
        store.setRectangleID(
                MetadataTools.createLSID("Shape", roi, shape), roi, shape);
        store.setRectangleX(new Double(left), roi, shape);
        store.setRectangleY(new Double(top), roi, shape);
        store.setRectangleWidth(new Double(width), roi, shape);
        store.setRectangleHeight(new Double(height), roi, shape);

        String name = getFirstNodeValue(attributes, "Name");
        String label = getFirstNodeValue(textElements, "Text");

        if (label != null) {
          store.setRectangleText(label, roi, shape);
        }
        shape++;
      }
    }
    return shape;
  }

  private int populatePolylines(NodeList polylines, int roi, int shape,
                                boolean closed)
  {
    for (int s=0; s<polylines.getLength(); s++, shape++) {
      Element polyline = (Element) polylines.item(s);
      Element geometry = getFirstNode(polyline, "Geometry");
      Element textElements = getFirstNode(polyline, "TextElements");
      Element attributes = getFirstNode(polyline, "Attributes");

      String shapeID = MetadataTools.createLSID("Shape", roi, shape);

      if (closed) {
        store.setPolygonID(shapeID, roi, shape);
        store.setPolygonPoints(
                getFirstNodeValue(geometry, "Points"), roi, shape);
        store.setPolygonText(
                getFirstNodeValue(textElements, "Text"), roi, shape);
      }
      else {
        store.setPolylineID(shapeID, roi, shape);
        store.setPolylinePoints(
                getFirstNodeValue(geometry, "Points"), roi, shape);
        store.setPolylineText(
                getFirstNodeValue(textElements, "Text"), roi, shape);
      }
    }
    return shape;
  }

  // ------------- Extra classes

  static class Channel {
    public String name;
    public String color;
    public IlluminationType illumination;
    public AcquisitionMode acquisitionMode;
    public String emission;
    public String excitation;
    public String pinhole;
    public Double exposure;
    public Double gain;
    public String fluor;
    public String filterSetRef;
  }

  /**
   * What is this class ? It is a class that builds a unique signature, a String,
   * that will be unique for each core index of the reader. This signature is built from
   * the subblock dimension entries. Essentially, because the XYZCT dimension belong to the same core,
   * these dimensions will be ignored in the signature -> this will make all sub-blocks belong to the
   * same series.
   *
   * If auto-stitching is true, the mosaic dimension is also ignored, and this will fuse all mosaic blocks
   * into a single core series, effectively merging mosaic into a single image.
   *
   * Also, the signature is made with ordering of the dimension, this will allow to sort series according
   * to the String signature possible.
   *
   * To avoid issues with ordering, the maximal number of digits per dimension should be known in advance.
   */
  static class CoreSignature implements Comparable<CoreSignature> {
    final String signature;
    final int hashCode;
    public CoreSignature(LibCZI.SubBlockSegment.SubBlockSegmentData.SubBlockDirectoryEntryDV.DimensionEntry[] entries,
                         String pyramidLevelDimension, int pyramidLevelValue,
                         Function<String, Integer> maxDigitPerDimension, boolean autostitch,
                         String filePartDimension, int filePartValue) {
      final StringBuilder signatureBuilder = new StringBuilder();
      signatureBuilder.append(filePartDimension);
      String digitFormat = "%0"+maxDigitPerDimension.apply(filePartDimension)+"d";
      signatureBuilder.append(String.format(digitFormat, filePartValue));
      Arrays.asList(entries).stream()
              .sorted(Comparator.comparing(e -> dimensionPriority(e.dimension)))
              .forEachOrdered(e -> {
                if (!ignoreDimForSeries(e.dimension, autostitch)) {
                  String digitFormat_inner = "%0"+maxDigitPerDimension.apply(e.dimension)+"d";
                  signatureBuilder.append(e.dimension+String.format(digitFormat_inner, e.start));
                }
              });
      // TODO : put this as a dimension entry directly
      signatureBuilder.append(pyramidLevelDimension);
      digitFormat = "%0"+maxDigitPerDimension.apply(pyramidLevelDimension)+"d";
      signatureBuilder.append(String.format(digitFormat, pyramidLevelValue));
      signature = signatureBuilder.toString();
      hashCode = Objects.hash(signature); // final, so let's precompute the hashcode
    }

    public Map<String, Integer> getDimensions() {
      Map<String, Integer> resultMap = new HashMap<>();

      // Iterate over the characters in the input string
      int startIndex = 0;
      int endIndex = startIndex+1;
      //for (int i = 0; i < signature.length(); i++) {
      while (startIndex<signature.length()) {
        //char currentChar = signature.charAt(startIndex);
        // Check if the current character is alphabetical
        //assert Character.isLetter(currentChar);

        // Find the index of the digit character after the alphabetical characters
        while (endIndex < signature.length() && !Character.isDigit(signature.charAt(endIndex))) {
          endIndex++;
        }

        // Extract the dimension substring and convert it to an integer value
        String dimension = signature.substring(startIndex, endIndex);
        startIndex = endIndex;

        while (endIndex < signature.length() && Character.isDigit(signature.charAt(endIndex))) {
          endIndex++;
        }

        String numberString = signature.substring(startIndex, endIndex);
        int number = Integer.parseInt(numberString);

        startIndex = endIndex;

        // Add the alphabetical character and the associated integer to the map
        resultMap.put(dimension, number);

      }
      return resultMap;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      CoreSignature that = (CoreSignature) o;
      return signature.equals(that.signature);
    }

    @Override
    public int hashCode() {
      return hashCode;
    }

    @Override
    public int compareTo(CoreSignature o) {
      return this.signature.compareTo(o.signature); // Sort based on string
    }

    @Override
    public String toString() {
      return signature;
    }

  }

  /**
   * This is a class that wraps three numbers c,z,t and an object
   * can be used as a key in a hashmap.
   *
   * It's used to create a Map from CZTKey to Blocks instead of
   * Map from C to Map from Z to Map from T to Blocks
   */
  static class CZTKey {
    public final int c,z,t;
    public final int hashCode;
    public CZTKey(int c, int z, int t) {
      this.c = c;
      this.z = z;
      this.t = t;
      hashCode = Objects.hash(c,z,t);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      CZTKey that = (CZTKey) o;
      return (that.z == this.z)&&(that.c == this.c)&&(that.t == this.t);
    }
    @Override
    public int hashCode() {
      return hashCode;
    }

    @Override
    public String toString() {
      return "C:"+c+"; Z:"+z+"; T:"+t;
    }
  }

  /**
   * A stripped down version of
   * {@link LibCZI.SubBlockDirectorySegment.SubBlockDirectorySegmentData.SubBlockDirectoryEntry}
   * Because we have really many of these objects, and it's critical to keep these objects as small as possible
   */
  static class MinimalDimensionEntry {

    public MinimalDimensionEntry(LibCZI.SubBlockDirectorySegment.SubBlockDirectorySegmentData.SubBlockDirectoryEntry entry) {
      filePosition = entry.getFilePosition();
      dimensionStartX = entry.getDimension("X").start;
      dimensionStartY = entry.getDimension("Y").start;
      storedSizeX = entry.getDimension("X").storedSize;
      storedSizeY = entry.getDimension("Y").storedSize;
    }

    final long filePosition;
    final int dimensionStartX, dimensionStartY;
    final int storedSizeX, storedSizeY;

  }

  /** Duplicates this reader for parallel reading.
   * Creating a reader with this method allows to keep a very low memory footprint
   * because all immutable objects are re-used by reference.
   * WARNING: calling {@link ZeissCZIReader#close()} on one of these readers will prevent the use
   * of all the other readers created with this method */
  public ZeissCZIReader copy() {
    return new ZeissCZIReader(this);
  }

  // An annotation that's helpful to re-initialize all fields in the new reader copied
  // from a model one (with the copy method or with the constructor with the reader
  // in argument)
  @Retention(RetentionPolicy.RUNTIME)
  @interface CopyByRef {

  }

  public static class SceneProperties {
    List<PositionLocation> pos = new ArrayList<>();
    String name;
  }

  public static class GroupProperties {
    List<TileProperties> tiles = new ArrayList<>();

    int nTilesX, nTilesY;
  }

  public static class TileProperties {
    PositionLocation pos = new PositionLocation();
    Integer iX;
    Integer iY;
  }

  private static class PositionLocation {
    Length pX, pY, pZ;
  }

  private static class AllPositionsInformation {
    /**
     * We may have, group, tiles, or scenes
     */
    List<SceneProperties> scenes;
    List<GroupProperties> groups;
    //List<TileProperties> tiles;

    List<PositionLocation> regions;
  }

  /**
   * A structure that helps to group all CZI segments
   * that are used in the file initialization
   */
  private static class CZISegments {
    final LibCZI.FileHeaderSegment fileHeader;
    final LibCZI.SubBlockDirectorySegment subBlockDirectory;
    final LibCZI.AttachmentDirectorySegment attachmentDirectory;
    final LibCZI.MetaDataSegment metadata;
    final double[] timeStamps;

    public CZISegments(String id, boolean littleEndian) throws IOException {
      this.fileHeader = LibCZI.getFileHeaderSegment(id, BUFFER_SIZE, littleEndian);
      this.subBlockDirectory = LibCZI.getSubBlockDirectorySegment(this.fileHeader, id, BUFFER_SIZE, littleEndian);
      this.metadata = LibCZI.getMetaDataSegment(this.fileHeader, id, BUFFER_SIZE, littleEndian);
      this.attachmentDirectory = LibCZI.getAttachmentDirectorySegment(this.fileHeader, id, BUFFER_SIZE, littleEndian);
      this.timeStamps = LibCZI.getTimeStamps(this.attachmentDirectory, id, BUFFER_SIZE, littleEndian);
    }
  }


}
