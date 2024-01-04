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

import loci.common.ByteArrayHandle;
import loci.common.Constants;
import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.Region;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
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
import loci.formats.in.DynamicMetadataOptions;
import loci.formats.in.JPEGReader;
import loci.formats.in.MetadataOptions;
import loci.formats.in.libczi.LibCZI;
import loci.formats.meta.MetadataStore;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.services.OMEXMLService;
import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.units.quantity.Pressure;
import ome.units.quantity.Temperature;
import ome.units.quantity.Time;
import ome.units.unit.Unit;
import ome.xml.model.enums.AcquisitionMode;
import ome.xml.model.enums.Binning;
import ome.xml.model.enums.IlluminationType;
import ome.xml.model.primitives.Color;
import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.PercentFraction;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.PositiveInteger;
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
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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
 * <p>
 * Essentially, all data is stored into subblocks where each subblock location is specified by its dimension indices.
 * There are standard spatial and time dimensions, as well as extra ones necessary to describe channel, scenes,
 * acquisition modalities, etc:
 * <p>
 *  X,Y,Z, // 3 spaces dimension
 *         T, Time
 *         M, Mosaic but why is there no trace of it in libczi documentation ???
 *         C, Channel
 *         R, Rotation
 *         I, Illumination
 *         H, Phase
 *         V, View
 *         B, Block = deprecated
 *         S  Scene
 * <p>
 *
 * A subblock may represent a lower resolution level. How to know this ? Because its stored size (x or y) is lower
 * than its size (x or y). Its downscaling factor can thus be computed the ratio between stored size and size.
 * For convenience, this reader adds the downscaling factor as an extra dimension named 'PY'
 * <p>
 * A CZI file consists of several segments. The majority of segments are data subblocks, as described before. But other
 * segments are present. Essentially this reader reads the {@link LibCZI.FileHeaderSegment} that
 * contains some metadata as well as the location of the {@link LibCZI.SubBlockDirectorySegment}
 *
 * The SubBlockDirectorySegment is a critical segment because it contains the dimension indices and file location of all
 * data subblocks. Thus, by reading this segment only, there is no need to go through all file segments while
 * initializing the reader.
 * <p>
 * Using this initial reading of the directory segment, all dimensions and all dimension ranges are known in advance.
 * This is used to compute the number of core series of the reader, as well as the resolution levels. This is done
 * by creating a core series signature {@link CoreSignature} where the dimension are sorted according to a priority
 * {@link ZeissCZIReader#dimensionPriority(String)}. If autostitching is true, all mosaics belong to the same core
 * series. If autostitching is false, each mosaic is split into different core series.
 * <p>
 * (core series (or core index) = series + resolution level)
 * <p>
 * Notes:
 * 1. It is assumed that all subblocks from a single core index
 * have the same compression type {@link ZeissCZIReader#coreIndexToCompression}
 *
 * 2. This reader is not thread safe, you can use memoization or {@link ZeissCZIReader#copy()}
 * to get a new reader and perform parallel reading.
 * <p>
 * 3. This reader is optimized for low memory footprint. It has been tested to work on Tb
 * czi size files. To save memory, the data structures used for reading are trimmed to the minimal amount of data
 * necessary for the reading after the reader has been initialized To illustrate this point, for a 6Tb dataset, each 'int'
 * saved per block saves 7Mb (in RAM and in memo file). Trimming down libczi dimension entries
 * to {@link MinDimEntry} leads to a memo file of around 100Mb for a 4Tb czi file. Its initialisation
 * takes below a minute, with memo building. Then a few seconds to generate a new reader from a memo file is sufficient.
 * <p>
 * 4. Even with memoization, at runtime, a reader for a multi Tb file will take around 300Mb on the heap. While this
 * is reasonable for a single reader, it becomes an issue to create multiple readers for parallel reading: 10 readers
 * will take 3 Gb. Thus the method {@link ZeissCZIReader#copy()} exist in order to create a new reader from an
 * existing one, which saves memory because it reuses all fields from the previous reader. Using this method, 10
 * readers can be created to read in parallel en single czi file, but it will use only the memory of one reader.
 * WARNING: calling {@link ZeissCZIReader#close()} on one of these readers will prevent the use
 * of all the other readers created with the copy method!
 * <p>
 * 5. This reader has an optimisation for lattice-light sheet fast reader initialisation: the metadata for each subblock
 * is not read for each plane, but only for the first plane of each time point, and then the timestamp of each plane is
 * linearly interpolated. This prevents a lot of random accesses to the file when it is initialized (up to a factor 1000
 * if there are 1000 planes). This a small cost associated to this choice: the linear interpolation is not alway exact,
 * and timestamps shift up to 10 ms may exist.
 * <p>
 * The annotation {@link CopyByRef} is used to annotate the fields that should be initialized in the duplicated reader
 * using the reference of the model one, see the constructor with the reader in argument.
 * <p>
 * 5. This reader uses the class {@link LibCZI} which contains the czi data structure translated to Java and which
 * contains very no logic related to the reader itself.
 * <p>
 * TODO:
 *  - get optimal tile size should vary depending on compression: on raw data it's easy to partially read planes,
 *  but for compressed data that's much harder so it would be better to read the whole block rather that decompressing
 *  it multiple times the same block to extract a partial region.
 *  Missing features:
 *  - add two methods that map forth and back czi dimension indices to bio-formats series
 *  - add a method that returns a 3D matrix per series (for lattice skewed dataset?) take care with version
 *  Issues:
 *  - some absolute path are stored in the reader, thus the memo fails if the file is moved
 *  - improve: slide preview and label image are stored directly in the reader as a byte array. That does not look optimal
 *  but loading these bytes on demand is quite tedious: hard to explain, but a reader is created inside the reader and
 *  maps the file 'temporarily' to a fake file. That's pretty clever and convenient, but prevents (most probably)
 *  lazy loading AND memoization functionality.
 *
 *  TODO: test PALM file
 *  TODO: ask how to get rid of absolute file path in memo that do not crash the reader when the file is moved
 *
 */

public class ZeissCZIReader extends FormatReader {

  final static Logger logger = LoggerFactory.getLogger(ZeissCZIReader.class);

  // -- Constants --
  public static final String ALLOW_AUTOSTITCHING_KEY = "zeissczi.autostitch";
  public static final boolean ALLOW_AUTOSTITCHING_DEFAULT = true;
  public static final String INCLUDE_ATTACHMENTS_KEY = "zeissczi.attachments";
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

  // Maps file part to the filename, in case of multipart file
  @CopyByRef
  private List<String> filePartToFileName = new ArrayList<>(); // TODO: Find a way to not store the absolutepath

  @CopyByRef
  private Map<Integer, Integer> coreIndexToSeries = new HashMap<>();

  // This array has to be taken out of the metadata initializer because of the way IFormatReader#get8BitLookupTable
  // and IFormatReader#get16BitLookupTable work
  @CopyByRef
  ArrayList<MetadataInitializer.Channel> channels = new ArrayList<>();

  // streamCurrentSeries is a temp field that should maybe be changed when setSeries is called
  transient int streamCurrentPart = -1;

  // previous channel has a value set by the last bytes being called, this is a weird behaviour IMO
  // but it behaves as expected to make the methods IFormatReader#get8BitLookupTable and
  // IFormatReader#get16BitLookupTable work
  transient int previousChannel = -1;

  // Core map structure for fast access to blocks:
  // - first key: bio-formats core index
  // - second key: czt index
  @CopyByRef
  private List< // CoreIndex
          HashMap<CZTKey, // CZT
                  List<MinDimEntry>>>
          coreIndexToTZCToMinimalBlocks = new ArrayList<>();

  @CopyByRef
  int nIlluminations, nRotations, nPhases;

  @CopyByRef
  boolean hasPyramid = false;

  // ------------------------ METADATA FIELDS
  @CopyByRef
  private MetadataStore store;

  @CopyByRef
  private ArrayList<byte[]> extraImages = new ArrayList<>();

  @CopyByRef
  int maxBlockSizeX = -1;
  @CopyByRef
  int maxBlockSizeY = -1;

  //----------------- CACHE
  /**
   * While the reader is not thread safe, the cache should be, because
   * it is shared between multiple readers which can coexist in different threads
   * if using the {@link ZeissCZIReader#copy()} method to duplicate the reader
   */
  @CopyByRef
  transient SubBlockLRUCache subBlockLRUCache = new SubBlockLRUCache(10 * 1024 * 1024, 400 * 1024 * 1024 );

  @CopyByRef
  transient Lock cacheLock = new ReentrantLock();

  @CopyByRef
  transient Set<MinDimEntry> subBlocksCurrentlyLoading = new HashSet<>();

  @CopyByRef
  transient boolean useCache = true;

  // -- Constructor --

  final static String FORMAT = "Zeiss CZI (Quick Start)";
  final static String SUFFIX = "czi";

  /** Constructs a new Zeiss .czi reader. */
  public ZeissCZIReader() {
    super(FORMAT, SUFFIX);
    domains = new String[] {FormatTools.LM_DOMAIN, FormatTools.HISTOLOGY_DOMAIN};
    suffixSufficient = false;
    suffixNecessary = false;
  }

  /** Duplicates 'that' reader for parallel reading.
   * Creating a reader with this constructor allows to keep a very low memory footprint
   * because all immutable objects are re-used by reference.
   * WARNING: calling {@link ZeissCZIReader#close()} on this or that reader will prevent the use
   * of the other reader created with this constructor */
  public ZeissCZIReader(ZeissCZIReader that) {
    super(FORMAT, SUFFIX);
    domains = new String[] {FormatTools.LM_DOMAIN, FormatTools.HISTOLOGY_DOMAIN};
    suffixSufficient = false;
    suffixNecessary = false;

    this.streamCurrentPart = -1;

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

    // Fields of the super class
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
      coreIndexToTZCToMinimalBlocks.clear(); // ZE big one! Hum, problem if another reader uses it... But ok
      store = null;
      // getStream().close(); done in the super method call
      coreIndexToOx.clear();
      coreIndexToOy.clear();
      coreIndexToCompression.clear();
      coreIndexToSignature.clear();
      coreIndexToDownscaleFactor.clear();
      filePartToFileName.clear();
      coreIndexToSeries.clear();
      coreIndexToTZCToMinimalBlocks.clear(); // The big one!
      extraImages.clear(); // Can be big as well
      cacheLock.lock();
      subBlockLRUCache.clear();
      subBlocksCurrentlyLoading.clear();
      cacheLock.unlock();
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

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  @Override
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    if ((getPixelType() != FormatTools.INT8 &&
            getPixelType() != FormatTools.UINT8) || previousChannel == -1 ||
            previousChannel >= channels.size())
    {
      return null;
    }

    byte[][] lut = new byte[3][256];

    String color = channels.get(previousChannel).color;
    if (color != null) {
      color = normalizeColor(color);
      try {
        int colorValue = Integer.parseInt(color, 16);

        int redMax = (colorValue & 0xff0000) >> 16;
        int greenMax = (colorValue & 0xff00) >> 8;
        int blueMax = colorValue & 0xff;

        for (int i=0; i<lut[0].length; i++) {
          lut[0][i] = (byte) (redMax * (i / 255.0));
          lut[1][i] = (byte) (greenMax * (i / 255.0));
          lut[2][i] = (byte) (blueMax * (i / 255.0));
        }

        return lut;
      }
      catch (NumberFormatException e) {
        return null;
      }
    }
    else return null;
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  @Override
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    if ((getPixelType() != FormatTools.INT16 &&
            getPixelType() != FormatTools.UINT16) || previousChannel == -1 ||
            previousChannel >= channels.size())
    {
      return null;
    }

    short[][] lut = new short[3][65536];

    String color = channels.get(previousChannel).color;
    if (color != null) {
      color = normalizeColor(color);
      try {
        int colorValue = Integer.parseInt(color, 16);

        int redMax = (colorValue & 0xff0000) >> 16;
        int greenMax = (colorValue & 0xff00) >> 8;
        int blueMax = colorValue & 0xff;

        redMax = (int) (65535 * (redMax / 255.0));
        greenMax = (int) (65535 * (greenMax / 255.0));
        blueMax = (int) (65535 * (blueMax / 255.0));

        for (int i=0; i<lut[0].length; i++) {
          lut[0][i] = (short) ((int) (redMax * (i / 65535.0)) & 0xffff);
          lut[1][i] = (short) ((int) (greenMax * (i / 65535.0)) & 0xffff);
          lut[2][i] = (short) ((int) (blueMax * (i / 65535.0)) & 0xffff);
        }

        return lut;
      }
      catch (NumberFormatException e) {
        return null;
      }
    }
    else return null;
  }

  /**
   * @see loci.formats.FormatReader#getFillColor()
   *
   * If the fill value was set explicitly, use that.
   * Otherwise, return 255 (white) for RGB data with a pyramid,
   * and 0 in all other cases. RGB data with a pyramid can
   * reasonably be assumed to be a brightfield slide.
   */
  @Override
  public Byte getFillColor() {
    if (fillColor != null) {
      return fillColor;
    }

    byte fill = (byte) 0;
    if (isRGB() && (hasPyramid)) {
      fill = (byte) 255;
    }
    return fill;
  }

  private void swapRGBIfnecessary(byte[] buf, int compression, int bpp, int pixel) {
    if (isRGB() /*&& !emptyTile*/ && compression != JPEGXR) { // TODO: case emptytile
      // channels are stored in BGR order; red and blue channels need switching
      // JPEG-XR data has already been reversed during decompression
      int redOffset = bpp * 2;
      int index = 0;
      int nloops=buf.length/pixel;
      for (int i=0; i<nloops; i++) {
        //index = i * pixel;
        for (int b=0; b<bpp; b++) {
          int blueIndex = index + b;
          int redIndex = index + redOffset + b;
          byte red = buf[redIndex];
          buf[redIndex] = buf[blueIndex];
          buf[blueIndex] = red;
        }
        index+=pixel;
      }
    }
  }

  private byte[] readRawPixelData(MinDimEntry block,
                                  int compression,
                                  int storedSizeX,
                                  int storedSizeY,
                                  RandomAccessInputStream s, Region tile, byte[] buf,
                                  int bpp, int totalBpp) throws FormatException, IOException {
    //s.order(isLittleEndian()); -> it is already set when calling the method

    if ((useCache)&&(compression!=UNCOMPRESSED)) {
      cacheLock.lock();
      if (subBlockLRUCache.containsKey(block)) {
        byte[] bytes = subBlockLRUCache.get(block).get();
        if (bytes!=null) {
          // - cache hit for block
          subBlockLRUCache.touch(block, bytes); // Updates order
          cacheLock.unlock();
          return bytes;
        }
      }

      // - block not in cache
      if (subBlocksCurrentlyLoading.contains(block)) {
        // There's already, in a different thread, a reader instance
        // computing the same block. We need to wait for it to finish
        try {
          do {
            // - waiting for block to be computed
            cacheLock.unlock();
            synchronized (subBlocksCurrentlyLoading) {
              subBlocksCurrentlyLoading.wait();
            }
            cacheLock.lock();
            // - is the right block being computed ?
          } while (subBlocksCurrentlyLoading.contains(block));
          // Yes -> the right block has been computed
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        byte[] bytes = subBlockLRUCache.get(block).get();
        if (bytes!=null) {
          // - the block has been computed
          subBlockLRUCache.touch(block, bytes); // put on top, for LRU cache
          cacheLock.unlock();
          return bytes;
        } else {
          // weird: the block is not there in the end... maybe it's been removed from the cache
          synchronized (subBlocksCurrentlyLoading) {
            subBlocksCurrentlyLoading.add(block);
          }
        }
      } else {
        // This thread will compute this block
        synchronized (subBlocksCurrentlyLoading) {
          subBlocksCurrentlyLoading.add(block);
        }
      }
      cacheLock.unlock();
    }

    LibCZI.SubBlockSegment subBlock = LibCZI.getBlock(s, block.filePosition);
    long blockDataOffset = subBlock.dataOffset;
    long blockDataSize = subBlock.data.dataSize;

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
      swapRGBIfnecessary(buf, UNCOMPRESSED, bpp, totalBpp);
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
            if (chunkID == 1) {
              int payload = stream.read();
              highLowUnpacking = (payload & 1) == 1;
            } else {
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
      swapRGBIfnecessary(buf, compression, bpp, totalBpp);
      if (useCache) {
        cacheLock.lock();
        // Block just computed
        subBlockLRUCache.touch(block, buf);
        // Put in cache
        subBlocksCurrentlyLoading.remove(block);
        cacheLock.unlock();
        synchronized (subBlocksCurrentlyLoading) {
          subBlocksCurrentlyLoading.notifyAll(); // Wake the threads waiting for this block
        }
      }
      return buf;
    }
    swapRGBIfnecessary(data, compression, bpp, totalBpp);
    if (useCache) {
      cacheLock.lock();
      subBlockLRUCache.touch(block, data);
      subBlocksCurrentlyLoading.remove(block);
      cacheLock.unlock();
      synchronized (subBlocksCurrentlyLoading) {
        subBlocksCurrentlyLoading.notifyAll();
      }
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
  public void reopenFile() {
    streamCurrentPart = -1;
  }

  private synchronized RandomAccessInputStream getStream(int filePart) throws IOException { // TODO : remove synchronized which is useless, a single reader is not multithreaded
    if ((in != null)&&(streamCurrentPart == filePart)) {
      return in;
    }
    streamCurrentPart = filePart;
    RandomAccessInputStream ris = new RandomAccessInputStream(filePartToFileName.get(filePart), BUFFER_SIZE);
    in = ris;
    ris.order(isLittleEndian());
    return ris;
  }

  // TODO: make this depend on compression -> it's preferable to avoid decompressing multiple times the same block
  // but with overlapping, anyway, and without caching, multiple decompression of the same block is hard to avoid
  @Override
  public int getOptimalTileWidth() {
    if (maxBlockSizeX>0) {
      return Math.min(2048, maxBlockSizeX);
    } else {
      return Math.min(2048, getSizeX());
    }
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    if (maxBlockSizeY>0) {
      return Math.min(2048, maxBlockSizeY);
    } else {
      return Math.min(2048, getSizeY());
    }
  }

  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h) throws FormatException, IOException {

    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    if (isThumbnailSeries()) {
      // thumbnail, label, or preview image stored as an attachment
      int index = getCoreIndex() - (core.size() - extraImages.size());
      byte[] fullPlane = extraImages.get(index);
      try (RandomAccessInputStream s = new RandomAccessInputStream(fullPlane)) {
        readPlane(s, x, y, w, h, buf);
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
    previousChannel = czt[1];
    CZTKey key = new CZTKey(czt[1], czt[0], czt[2]);

    while (baseResolution > 0 &&
            coreIndexToDownscaleFactor.get(baseResolution) >
                    coreIndexToDownscaleFactor.get(baseResolution-1)) {
      baseResolution--;
    }

    Arrays.fill(buf, getFillColor());

    // The data is somewhere in these blocks
    List<MinDimEntry> blocks = coreIndexToTZCToMinimalBlocks.get(currentIndex).get(key);

    if (blocks == null) return buf; // No block found -> empty image. TODO : black or white ?

    for (MinDimEntry block : blocks) {
      Region blockRegion = new Region(
              block.dimensionStartX/ coreIndexToDownscaleFactor.get(coreIndex) - coreIndexToOx.get(coreIndex),
              block.dimensionStartY/ coreIndexToDownscaleFactor.get(coreIndex) - coreIndexToOy.get(coreIndex),
              block.storedSizeX,
              block.storedSizeY
      );

      if (image.intersects(blockRegion)) {
        RandomAccessInputStream stream = getStream(block.filePart);

        if (image.equals(blockRegion) && blocks.size()==1) { // THE SECOND TEST IS NECESSARY BECAUSE OTHER BLOCKS CAN INTERSECT!
          // Best case scenario: reads and returns full subblock
          return readRawPixelData(
                  block,
                  coreIndexToCompression.get(coreIndex),
                  block.storedSizeX,
                  block.storedSizeY,
                  stream,null,buf, bpp, bytesPerPixel);
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
                  block,
                  compression,
                  block.storedSizeX,
                  block.storedSizeY,
                  stream, compression==UNCOMPRESSED? tileInBlock: null, // can't really optimize with compressed block
                  compression==UNCOMPRESSED? DataTools.allocate(tileInBlock.width, tileInBlock.height, nCh, bpp): null,
                  bpp, bytesPerPixel);

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
          int skipLinesEnd = Math.max(blockEndY, 0);
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
    filePartToFileName.add(id);
    // And we the additional parts.
    Location parent = file.getParentFile();
    String[] list = parent.list(true);
    for (String f : list) {
      if (f.startsWith(base + "(") || f.startsWith(base + " (")) {
        String part = f.substring(f.lastIndexOf("(") + 1, f.lastIndexOf(")"));
        try {
          String filePartPath = new Location(parent, f).getAbsolutePath();
          cziPartToSegments.put(Integer.parseInt(part),
                  new CZISegments(filePartPath, isLittleEndian()));
          filePartToFileName.add(filePartPath);
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
    // - Merge according to C, except if pixels types are different (unimplemented, case not found in tested images. But is it possible?)

    // Adding the extra dimension in each subblock : part, and resolution level
    // For the series order, each dimension has a priority, set by the method dimensionPriority

    // What do I want to do ?
    // I want to find the number of series. For that, I make a unique signature
    // of each subblock with its dimension signature, then count the number of
    // signature in the signature set.

    // The signature alphabetical order will be used for the ordering of the series
    // Here's some example signatures:
    // H0S04PY00001
    // H0S03PY00001
    // H0S02PY00001 -> phase 0, scene 2, pyramidal level 1 (highest resolution)
    // H0S01PY00001
    // H0S00PY00001
    // PY is always last, that's how bio-formats work: resolution levels of a series always follow each other (core indices)

    Map<String, Integer> maxValuePerDimension = new HashMap<>();

    // Then we look at the max value in each dimension, to know how many digits are needed to write the signature
    // and proper alphabetical ordering
    cziPartToSegments.forEach((part, cziSegments) -> { // For each part
      Arrays.asList(cziSegments.subBlockDirectory.data.entries).forEach( // and each entry
              entry -> {
                for (LibCZI.SubBlockSegment.SubBlockSegmentData.SubBlockDirectoryEntryDV.DimensionEntry dimEntry: entry.getDimensionEntries()) {
                  //int nDigits = String.valueOf(dimEntry.start).length(); // TODO: Can this be negative ?
                  int val = dimEntry.start;
                  if (!maxValuePerDimension.containsKey(dimEntry.dimension)) {
                    maxValuePerDimension.put(dimEntry.dimension, dimEntry.start);
                  } else {
                    int curMax = maxValuePerDimension.get(dimEntry.dimension);
                    if (val>curMax) {
                      maxValuePerDimension.put(dimEntry.dimension, val);
                    }
                  }
                }
              }
      );
    });

    nIlluminations = maxValuePerDimension.containsKey("I")? maxValuePerDimension.get("I")+1:1;

    nRotations = maxValuePerDimension.containsKey("R")? maxValuePerDimension.get("R")+1:1;

    nPhases = maxValuePerDimension.containsKey("H")? maxValuePerDimension.get("H")+1:1;

    int nChannels = maxValuePerDimension.containsKey("C")? maxValuePerDimension.get("C")+1:1;

    int nSlices = maxValuePerDimension.containsKey("Z")? maxValuePerDimension.get("Z")+1:1;

    int nFrames = maxValuePerDimension.containsKey("T")? maxValuePerDimension.get("T")+1:1;

    Map<String, Integer> maxDigitPerDimension = new HashMap<>();
    maxValuePerDimension.keySet().forEach(dim -> {
      switch (dim) {
        case "C":
          // illuminations -> modulo C
          maxDigitPerDimension.put(dim, String.valueOf(maxValuePerDimension.get(dim)*nIlluminations).length());
          break;
        case "Z":
          // rotations -> modulo Z
          maxDigitPerDimension.put(dim, String.valueOf(maxValuePerDimension.get(dim)*nRotations).length());
          break;
        case "T":
          // phases -> modulo T
          maxDigitPerDimension.put(dim, String.valueOf(maxValuePerDimension.get(dim)*nPhases).length());
          break;
        default:
          maxDigitPerDimension.put(dim, String.valueOf(maxValuePerDimension.get(dim)).length());
      }
    });

    // Ready to build the signature
    Map<CoreSignature, List<ModuloDimensionEntries>> coreSignatureToBlocks = new HashMap<>();
    maxDigitPerDimension.put(RESOLUTION_LEVEL_DIMENSION,5); // Let's hope that the downsampling ratio never exceeds 9999 TODO : improve
    maxDigitPerDimension.put(FILE_PART_DIMENSION, String.valueOf(cziPartToSegments.size()).length());

    // Write all signatures
    cziPartToSegments.forEach((part, cziSegments) -> { // For each part
      Arrays.asList(cziSegments.subBlockDirectory.data.entries).forEach( // and each entry
              entry -> {
                int downscalingFactor = (int) Math.round((double)(entry.getDimension("X").size)/(double)(entry.getDimension("X").storedSize));
                hasPyramid = hasPyramid || (downscalingFactor!=1);
                if ((downscalingFactor==1)||(allowAutostitching())) {
                  // Split by resolution level if flattenedResolutions is true
                  ModuloDimensionEntries moduloEntry = new ModuloDimensionEntries(entry,
                          nRotations, nIlluminations, nPhases,
                          nChannels, nSlices, nFrames, part);

                  CoreSignature coreSignature = new CoreSignature(moduloEntry
                          , RESOLUTION_LEVEL_DIMENSION,
                          downscalingFactor,//getDownSampling(entry),
                          maxDigitPerDimension::get,
                          allowAutostitching(),
                          FILE_PART_DIMENSION, part);
                  if (!coreSignatureToBlocks.containsKey(coreSignature)) {
                    coreSignatureToBlocks.put(coreSignature, new ArrayList<>());
                  }
                  coreSignatureToBlocks.get(coreSignature).add(moduloEntry);
                }
              });
    });

    // Sort them
    List<CoreSignature> orderedCoreSignatureList = coreSignatureToBlocks.keySet().stream().sorted().collect(Collectors.toList());

    // We now know how many core index are present in the image... except for extra images!

    core = new ArrayList<>();
    int idxCoreResolutionLevelStart = -1;
    int idxSeries = -1;
    // These variables (idxCoreResolutionLevelStart and idxSeries) roles are to keep track of the previous highest
    // resolution level, and thus to abide by the rule nPixres0 = nPixres_i * downsampling_i
    int previousMinX_maxRes = -1, previousMinY_maxRes = -1, previousPixX_maxRes = -1, previousPixY_maxRes = -1;
    for (int iCore = 0; iCore<orderedCoreSignatureList.size(); iCore++) {
      CoreMetadata core_i = new CoreMetadata();

      //--------------- MODULO

      // set modulo annotations
      // rotations -> modulo Z
      // illuminations -> modulo C
      // phases -> modulo T

      core_i.moduloZ.step = nSlices;
      core_i.moduloZ.end = nSlices * (nRotations - 1);
      core_i.moduloZ.type = FormatTools.ROTATION;

      core_i.moduloC.step = nChannels;
      core_i.moduloC.end = nChannels * (nIlluminations - 1);
      core_i.moduloC.type = FormatTools.ILLUMINATION;
      core_i.moduloC.parentType = FormatTools.CHANNEL;

      core_i.moduloT.step = nFrames;
      core_i.moduloT.end = nFrames * (nPhases - 1);
      core_i.moduloT.type = FormatTools.PHASE;

      //--------------- END OF MODULO

      core.add(core_i);
      core_i.orderCertain = true;
      core_i.dimensionOrder = "XYCZT";
      core_i.littleEndian = true;
      CoreSignature coreSignature = orderedCoreSignatureList.get(iCore);
      ModuloDimensionEntries model = coreSignatureToBlocks.get(coreSignature).get(0);
      int[] coordsOrigin = setOriginAndSize(core_i,
              coreSignatureToBlocks.get(coreSignature),
              previousMinX_maxRes,
              previousMinY_maxRes,
              previousPixX_maxRes,
              previousPixY_maxRes);
      if (model.downSampling == 1) {
        previousMinX_maxRes = coordsOrigin[0];
        previousMinY_maxRes = coordsOrigin[1];
        previousPixX_maxRes = core_i.sizeX;
        previousPixY_maxRes = core_i.sizeY;
      }
      convertPixelType(core_i, model.getPixelType());
      coreIndexToCompression.add(model.getCompression());
      coreIndex = iCore;
      coreIndexToDownscaleFactor.add(model.getDownSampling());

      coreIndexToOx.add(coordsOrigin[0]);
      coreIndexToOy.add(coordsOrigin[1]);
      core_i.bitsPerPixel =
              core_i.imageCount = (core_i.rgb ? core_i.sizeC/3 : core_i.sizeC)*core_i.sizeT*core_i.sizeZ;

      // We assert that all sub blocks do have the same size pixel type
      // Series are ordered by dimension changes, and the (non-ignored) dimension
      // with the highest priority is the resolution level
      // So the downsampling will go : 1, 2, 4, 8, 1 (change!), 2, 4, 8, 1 (change), 2, 4, 1 (change), 1 (change)
      // When a change is noticed, all previous core indices belong to the same series (unless flat resolution == false.
      // see FormatReader#getSeriesToCoreIndex

      if (model.getDownSampling()==1) {
        if (idxCoreResolutionLevelStart==-1) {
          idxCoreResolutionLevelStart = iCore;
          if (!hasFlattenedResolutions()) idxSeries++;
        } else {
          for (int j = idxCoreResolutionLevelStart; j<iCore; j++) {
            core.get(j).resolutionCount = iCore-j;
          }
          idxCoreResolutionLevelStart = iCore;
          if (!hasFlattenedResolutions()) idxSeries++;
        }
      } else {
        // Let's close the loop
        if (iCore==orderedCoreSignatureList.size()-1) {
          // Let's finish
          for (int j = idxCoreResolutionLevelStart; j<orderedCoreSignatureList.size(); j++) {
            core.get(j).resolutionCount = orderedCoreSignatureList.size()-j;
          }
        }
      }

      if (hasFlattenedResolutions()) {
        coreIndexToSeries.put(iCore, iCore);
      } else {
        coreIndexToSeries.put(iCore, idxSeries);
      }

      coreIndexToSignature.add(coreSignature);
    }

    // Add extra images / thumbnails:
    // Label
    // SlidePreview
    // JPG Thumbnail -> not read for the sake of backward compatibility

    List<Integer> sortedFileParts = cziPartToSegments.keySet().stream().sorted().collect(Collectors.toList());

    if (canReadAttachments()) {
      try {
        addLabelIfExists(sortedFileParts, cziPartToSegments, id);//, allPositionsInformation);
        addSlidePreviewIfExists(sortedFileParts, cziPartToSegments, id);//, allPositionsInformation);
        //getJPGThumbnailIfExists(sortedFileParts, cziPartToSegments, id); //disabled for bwd compatibility
      } catch (DependencyException | ServiceException e) {
        throw new RuntimeException(e);
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
      } else {
        LOGGER.trace("Thumbnail");
      }
    }

    setCoreIndex(0); // Fresh start
    // Ok, now let's sort all subblocks from a core index into hashmaps
    // Idea: subblocks.get(coreIndex).get(key(c,z,t)) -> returns a list of blocks which
    // differs only by X and Y coordinates.
    // This structure will be trimmed down in size and stored in the mapCoreTZCToMinimalBlocks field
    List< // CoreIndex
            HashMap<CZTKey, // CZT
                    // List<LibCZI.SubBlockDirectorySegment.SubBlockDirectorySegmentData.SubBlockDirectoryEntry>>>
                    List<MinDimEntry>>>
            mapCoreTZCToBlocks = new ArrayList<>();

    for (int iCoreIndex = 0; iCoreIndex<core.size(); iCoreIndex++) {
      if (core.get(iCoreIndex).thumbnail) continue; // skips extra images
      CoreSignature coreSignature = orderedCoreSignatureList.get(iCoreIndex);
      mapCoreTZCToBlocks.add(iCoreIndex, new HashMap<>());
      coreIndexToTZCToMinimalBlocks.add(iCoreIndex, new HashMap<>());
      HashMap<CZTKey, List<MinDimEntry>> blocksInCore = mapCoreTZCToBlocks.get(iCoreIndex);
      HashMap<CZTKey, List<MinDimEntry>> minimalBlocksInCore = coreIndexToTZCToMinimalBlocks.get(iCoreIndex);
      for (ModuloDimensionEntries block: coreSignatureToBlocks.get(coreSignature)) {
        int c = (block.hasDimension("C"))? block.getDimension("C").start: 0;
        int z = (block.hasDimension("Z"))? block.getDimension("Z").start: 0;
        int t = (block.hasDimension("T"))? block.getDimension("T").start: 0;
        CZTKey k = new CZTKey(c,z,t);
        if (!minimalBlocksInCore.containsKey(k)) {
          blocksInCore.put(k, new ArrayList<>());
          minimalBlocksInCore.put(k, new ArrayList<>());
        }
        MinDimEntry mde = new MinDimEntry(block); // Makes a trimmed down version of the block in order to reduce the reader memory footprint getMinimalEntry(block);//
        blocksInCore.get(k).add(mde);
        minimalBlocksInCore.get(k).add(mde);
      }
      //In the end, there are 'blocksInCore.values().size()' blocks in the core 'iCoreIndex'
    }

    // Initialize the reader store, and basically all metadata
    MetadataInitializer mi = new MetadataInitializer(this);
    mi.initializeMetadata(cziPartToSegments, mapCoreTZCToBlocks);
    for (MetadataInitializer.Channel channel: mi.channels) {
      channels.add(channel);
    }

  }

  private void addLabelIfExists(List<Integer> sortedFileParts, Map<Integer, CZISegments> cziPartToSegments, String id) throws IOException, FormatException, DependencyException, ServiceException {//}, AllPositionsInformation allPositionsInformation) throws IOException, FormatException, DependencyException, ServiceException {
    for (int filePart: sortedFileParts) {
      byte[] bytes = LibCZI.getLabelBytes(cziPartToSegments.get(filePart).attachmentDirectory, id, BUFFER_SIZE, isLittleEndian());
      if (bytes!=null) {
        int nSeries = getSeriesCount();
        ServiceFactory factory = new ServiceFactory();
        OMEXMLService service = factory.getInstance(OMEXMLService.class);
        OMEXMLMetadata omeXML = service.createOMEXMLMetadata();
        ZeissCZIReader labelReader = new ZeissCZIReader();
        String placeHolderName = "label.czi";
        // thumbReader.setMetadataOptions(getMetadataOptions());
        ByteArrayHandle stream = new ByteArrayHandle(bytes);
        Location.mapFile(placeHolderName, stream);
        labelReader.setMetadataStore(omeXML);
        labelReader.setId(placeHolderName);

        CoreMetadata c = labelReader.getCoreMetadataList().get(0);

        if (c.sizeZ > 1 || c.sizeT > 1) {
          return;
        }
        core.add(new CoreMetadata(c));
        core.get(core.size() - 1).thumbnail = true;
        extraImages.add(labelReader.openBytes(0));
        stream.close();
        coreIndexToSeries.put(core.size() - 1, nSeries);
        Location.mapFile(placeHolderName, null);

                /*allPositionsInformation.labelLocation = new XYZLength();
                allPositionsInformation.labelPixelSize = new XYZLength();
                allPositionsInformation.labelPixelSize.pX = omeXML.getPixelsPhysicalSizeX(0);
                allPositionsInformation.labelPixelSize.pY = omeXML.getPixelsPhysicalSizeY(0);
                allPositionsInformation.labelPixelSize.pZ = omeXML.getPixelsPhysicalSizeZ(0);
                allPositionsInformation.labelLocation.pX = omeXML.getPlanePositionX(0,0);
                allPositionsInformation.labelLocation.pY = omeXML.getPlanePositionY(0,0);
                allPositionsInformation.labelLocation.pZ = omeXML.getPlanePositionZ(0,0);*/
        labelReader.close();
      }
    }

  }

  private void addSlidePreviewIfExists(List<Integer> sortedFileParts, Map<Integer, CZISegments> cziPartToSegments, String id) throws IOException, FormatException, DependencyException, ServiceException {//, AllPositionsInformation allPositionsInformation) throws IOException, FormatException, DependencyException, ServiceException {
    for (int filePart: sortedFileParts) {
      byte[] bytes = LibCZI.getPreviewBytes(cziPartToSegments.get(filePart).attachmentDirectory, id, BUFFER_SIZE, isLittleEndian());
      if (bytes!=null) {
        int nSeries = getSeriesCount();
        ServiceFactory factory = new ServiceFactory();
        OMEXMLService service = factory.getInstance(OMEXMLService.class);
        OMEXMLMetadata omeXML = service.createOMEXMLMetadata();
        ZeissCZIReader labelReader = new ZeissCZIReader();
        String placeHolderName = "slide_preview.czi";
        labelReader.setMetadataOptions(getMetadataOptions());
        ByteArrayHandle stream = new ByteArrayHandle(bytes);
        Location.mapFile(placeHolderName, stream);
        labelReader.setMetadataStore(omeXML);
        labelReader.setId(placeHolderName);

        CoreMetadata c = labelReader.getCoreMetadataList().get(0);

        if (c.sizeZ > 1 || c.sizeT > 1) {
          return;
        }
        core.add(new CoreMetadata(c));
        core.get(core.size() - 1).thumbnail = true;
        extraImages.add(labelReader.openBytes(0));
        stream.close();
        coreIndexToSeries.put(core.size() - 1, nSeries);
        Location.mapFile(placeHolderName, null);

                /*allPositionsInformation.slidePreviewLocation = new XYZLength();
                allPositionsInformation.slidePreviewPixelSize = new XYZLength();
                allPositionsInformation.slidePreviewPixelSize.pX = omeXML.getPixelsPhysicalSizeX(0);
                allPositionsInformation.slidePreviewPixelSize.pY = omeXML.getPixelsPhysicalSizeY(0);
                allPositionsInformation.slidePreviewPixelSize.pZ = omeXML.getPixelsPhysicalSizeZ(0);
                allPositionsInformation.slidePreviewLocation.pX = omeXML.getPlanePositionX(0,0);
                allPositionsInformation.slidePreviewLocation.pY = omeXML.getPlanePositionY(0,0);
                allPositionsInformation.slidePreviewLocation.pZ = omeXML.getPlanePositionZ(0,0);*/
        labelReader.close();
      }
    }

  }

  // Unimplemented in the original reader. Thus, it is unused here as well
  private void getJPGThumbnailIfExists(List<Integer> sortedFileParts, Map<Integer, CZISegments> cziPartToSegments, String id) throws IOException, FormatException {
    for (int filePart: sortedFileParts) {
      byte[] jpegBytes = LibCZI.getJPGThumbNailBytes(cziPartToSegments.get(filePart).attachmentDirectory, id, BUFFER_SIZE, isLittleEndian());

      if (jpegBytes!=null) { // SKIPS THUMBNAIL FOR LEGACY COMPATIBILITY
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
  }

  // The parent method does not work well
  @Override
  public int coreIndexToSeries(int index){
    return coreIndexToSeries.get(index);
  }

  /*
    This method goes through all subblocks of a particular core index and finds the
    X, Y, Z, C, T bounds of these subblocks.
     */
  private int[] setOriginAndSize(CoreMetadata ms0,
                                 List<ModuloDimensionEntries> blocks,
                                 int minX_maxRes, int minY_maxRes, int nPixX_maxRes, int nPixY_maxRes) {
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
    int downScale = blocks.get(0).getDownSampling();

    for (ModuloDimensionEntries block: blocks) {
      int blockSizeX = block.getDimension("X").storedSize; // size in pixel
      int blockSizeY = block.getDimension("Y").storedSize;
      if (blockSizeX>maxBlockSizeX) maxBlockSizeX = blockSizeX;
      if (blockSizeX>maxBlockSizeY) maxBlockSizeY = blockSizeY;

      int x_min = block.getDimension("X").start/downScale;
      int x_max = x_min+blockSizeX; // size or stored size ?
      int y_min = block.getDimension("Y").start/downScale;
      int y_max = y_min+blockSizeY;
      int z_min = 0;
      int z_max = 1;
      if (block.hasDimension("Z")) {
        z_min = block.getDimension("Z").start;
        z_max = z_min + block.getDimension("Z").storedSize;
      }
      int c_min = 0;
      int c_max = 1;
      if (block.hasDimension("C")) {
        c_min = block.getDimension("C").start;
        c_max = c_min + block.getDimension("C").storedSize;
      }
      int t_min = 0;
      int t_max = 1;
      if (block.hasDimension("T")) {
        t_min = block.getDimension("T").start;
        t_max = t_min + block.getDimension("T").storedSize;
      }
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

    ms0.sizeZ = maxZ - minZ;
    ms0.sizeC = maxC - minC;
    ms0.sizeT = maxT - minT;

    if ((downScale!=1)&&(allowAutostitching())) {
      ms0.sizeX = nPixX_maxRes/downScale;
      ms0.sizeY = nPixY_maxRes/downScale;
      int[] originCoordinates = new int[2];
      originCoordinates[0] = minX_maxRes/downScale;
      originCoordinates[1] = minY_maxRes/downScale;
      return originCoordinates;
    } else {
      ms0.sizeX = maxX - minX;
      ms0.sizeY = maxY - minY;
      int[] originCoordinates = new int[2];
      originCoordinates[0] = minX;
      originCoordinates[1] = minY;
      return originCoordinates;
    }
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

  /*
   * This is a very important method which role is crucial to understand this reader.
   * It basically defines which dimensions matter for merging or splitting subblocks
   * between core series
   *
   * So, for instance, when the X or Y coordinates differ between 2 subblocks, they may still
   * belong to the same core index.
   *
   * The M dimension 'mosaic' will split subblocks between cores only if autostitch is false.
   *
   * Actually, transforming the reader to allow it to merge all scenes together is as simple as
   * returning true for the dimension "S" (and "M")
   */
  private static boolean ignoreDimForSeries(String dimension, boolean autostitch) {
    switch (dimension) {
      case "X"://
      case "Y":
      case "Z":
      case "T":
      case "C":
      case FILE_PART_DIMENSION:
        //case "S":
        return true;
      case "M":
        return autostitch;
      default:
        return false;
    }
  }

  /*
   * Defines how dimension are sorted for series / core
   * This sets in which order the series are created depending on their dimension
   * This order has been set to match the original ZeissCZIReader
   */
  private static int dimensionPriority(String dimension) {
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
      case "C": // Channel
        return 5;
      case "R": // Rotation
        return 6;
      case "I": // Illumination
        return 7;
      case "H": // Phase
        return 8;
      case "V": // View : = Angle
        return 9;
      case "B": // Block - deprecated
        return 10;
      case "S": // Scene // That's weird the priority between scene on mosaic, I need to understand a bit better
        return 11;
      case "M": // Mosaic
        return 12;
      case FILE_PART_DIMENSION: // File part : number one
        return 13;
      default:
        throw new UnsupportedOperationException("Unknown dimension "+dimension);
    }
  }

  // ------------- Extra classes

  /**
   * What is this class ? It is a class that builds a String signature,
   * that will be unique for each core index of the reader. This signature is built from
   * the subblock dimension entries. Essentially, because the XYZCT dimension belong to the same core,
   * these dimensions will be ignored in the signature -> this will make all sub-blocks belong to the
   * same series.
   * <p>
   * If auto-stitching is true, the mosaic dimension is also ignored, and this will fuse all mosaic blocks
   * into a single core series, effectively merging mosaic into a single image.
   * <p>
   * Also, the signature is made with ordering of the dimension, this will allow to sort series according
   * to the String signature possible.
   * <p>
   * To avoid issues with ordering, the maximal number of digits per dimension should be known in advance.
   */
  static class CoreSignature implements Comparable<CoreSignature> {
    final String signature;
    final int hashCode;

    final int filePart;

    public int getFilePart() {
      return filePart;
    }
    public CoreSignature(ModuloDimensionEntries entries, //LibCZI.SubBlockSegment.SubBlockSegmentData.SubBlockDirectoryEntryDV.DimensionEntry[] entries,
                         String pyramidLevelDimension, int pyramidLevelValue,
                         Function<String, Integer> maxDigitPerDimension, boolean autostitch,
                         String filePartDimension, int filePartValue) {
      this.filePart = filePartValue;
      final StringBuilder signatureBuilder = new StringBuilder();
      //signatureBuilder.append(filePartDimension);
      //String digitFormat = "%0"+maxDigitPerDimension.apply(filePartDimension)+"d";
      //signatureBuilder.append(String.format(digitFormat, filePartValue));
      entries.getList().stream()
              .sorted(Comparator.comparing(e -> dimensionPriority(e.getDimension())))
              .forEachOrdered(e -> {
                if (!ignoreDimForSeries(e.getDimension(), autostitch)) {
                  String digitFormat_inner = "%0"+maxDigitPerDimension.apply(e.getDimension())+"d";
                  signatureBuilder.append(e.getDimension())
                          .append(String.format(digitFormat_inner, e.getStart()));
                }
              });
      // TODO : put this as a dimension entry directly
      signatureBuilder.append(pyramidLevelDimension);
      String digitFormat = "%0"+maxDigitPerDimension.apply(pyramidLevelDimension)+"d";
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
   * A class that transforms dimension entries to account for the Modulo implementation
   * So it just keeps what's needed and apply modulo operations on C Z and T
   */
  static class ModuloDimensionEntries {
    /**
     // set modulo annotations
     // rotations -> modulo Z
     // illuminations -> modulo C
     // phases -> modulo T

     LOGGER.trace("rotations = {}", rotations);
     LOGGER.trace("illuminations = {}", illuminations);
     LOGGER.trace("phases = {}", phases);

     LOGGER.trace("positions = {}", positions);
     LOGGER.trace("acquisitions = {}", acquisitions);
     LOGGER.trace("mosaics = {}", mosaics);
     LOGGER.trace("angles = {}", angles);

     ms0.moduloZ.step = ms0.sizeZ;
     ms0.moduloZ.end = ms0.sizeZ * (rotations - 1);
     ms0.moduloZ.type = FormatTools.ROTATION;
     ms0.sizeZ *= rotations;

     ms0.moduloC.step = ms0.sizeC;
     ms0.moduloC.end = ms0.sizeC * (illuminations - 1);
     ms0.moduloC.type = FormatTools.ILLUMINATION;
     ms0.moduloC.parentType = FormatTools.CHANNEL;
     ms0.sizeC *= illuminations;

     ms0.moduloT.step = ms0.sizeT;
     ms0.moduloT.end = ms0.sizeT * (phases - 1);
     ms0.moduloT.type = FormatTools.PHASE;
     ms0.sizeT *= phases;
     */

    final List<ModuloDimensionEntry> entryList = new ArrayList<>();
    final int nRotations, nIlluminations, nPhases, filePart;

    public ModuloDimensionEntries(LibCZI.SubBlockDirectorySegment.SubBlockDirectorySegmentData.SubBlockDirectoryEntry entry,
                                  int nRotations, int nIlluminations, int nPhases, int nChannels, int nSlices, int nFrames, int filePart) {
      this.filePart = filePart;
      this.nRotations = nRotations;
      this.nIlluminations = nIlluminations;
      this.nPhases = nPhases;
      this.pixelType = entry.getPixelType();
      this.compression = entry.getCompression();
      this.downSampling = (int) Math.round((double)(entry.getDimension("X").size)/(double)(entry.getDimension("X").storedSize));
      this.filePosition = entry.getFilePosition();

      int iRotation = 0;
      int iIllumination = 0;
      int iPhase = 0;
      // Collect
      LibCZI.SubBlockSegment.SubBlockSegmentData.SubBlockDirectoryEntryDV.DimensionEntry[] entries = entry.getDimensionEntries();
      for (int i = 0; i<entries.length; i++) {
        switch (entries[i].dimension) {
          case "R": iRotation = entries[i].start; break;
          case "I": iIllumination = entries[i].start; break;
          case "H": iPhase = entries[i].start; break;
        }
      }

      for (int i = 0; i<entries.length; i++) {
        //
        switch (entries[i].dimension) {
          case "R": case "I": case "H": break; // no entry
          case "C":
            entryList.add(new ModuloDimensionEntry("C",
                    iIllumination * nChannels + entries[i].start, entries[i].storedSize));break;
          case "Z":
            entryList.add(new ModuloDimensionEntry("Z",
                    iRotation * nSlices + entries[i].start, entries[i].storedSize));break;
          case "T":
            entryList.add(new ModuloDimensionEntry("T",
                    iPhase * nFrames + entries[i].start, entries[i].storedSize));
          default:
            entryList.add(new ModuloDimensionEntry(entries[i].dimension, entries[i].start, entries[i].storedSize));
        }
      }

      // Update
    }

    public Collection<ModuloDimensionEntry> getList() {
      return entryList;
    }

    final int pixelType;

    public int getPixelType() {
      return pixelType;
    }

    final int compression;
    public Integer getCompression() {
      return compression;
    }

    final int downSampling;
    public int getDownSampling() {
      return downSampling;
    }

    public ModuloDimensionEntry getDimension(String dim) {
      for (ModuloDimensionEntry entry:getList()) {
        if (entry.dimension.equals(dim)) {
          return entry;
        }
      }
      throw new IllegalArgumentException("No dimension "+dim+" found");
    }

    final long filePosition;
    public long getFilePosition() {
      return filePosition;
    }

    public boolean hasDimension(String dim) {
      for (ModuloDimensionEntry entry:getList()) {
        if (entry.dimension.equals(dim)) {
          return true;
        }
      }
      return false;
    }

    static class ModuloDimensionEntry {
      //LibCZI.SubBlockSegment.SubBlockSegmentData.SubBlockDirectoryEntryDV.DimensionEntry entry;
      final String dimension;
      final int start;
      final int storedSize;
      public ModuloDimensionEntry(String dimension, int start, int storedSize) {
        this.dimension = dimension;
        this.start = start;
        this.storedSize = storedSize;
      }

      public String getDimension() {
        return dimension;
      }

      public int getStart() {
        return start;
      }
    }
  }

  /**
   * This is a class that wraps three numbers c,z,t and an object
   * can be used as a key in a hashmap.
   * <p>
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
  static class MinDimEntry {

    final int dimensionStartZ;

    public MinDimEntry(ModuloDimensionEntries entry) {
      filePosition = entry.getFilePosition();
      dimensionStartX = entry.getDimension("X").start;
      dimensionStartY = entry.getDimension("Y").start;
      if (entry.hasDimension("Z")) {
        dimensionStartZ = entry.getDimension("Z").start;
      } else {
        dimensionStartZ = 0;
      }
      storedSizeX = entry.getDimension("X").storedSize;
      storedSizeY = entry.getDimension("Y").storedSize;
      filePart = entry.filePart;
    }

    final long filePosition;
    final int dimensionStartX, dimensionStartY;
    final int storedSizeX, storedSizeY;

    final int filePart;


  }


  //MinimalDimensionEntry - for Java > 17
    /*public record MinDimEntry(int dimensionStartZ, long filePosition,

                              int dimensionStartX,int dimensionStartY, int storedSizeX, int storedSizeY, int filePart) {}

    public static MinDimEntry getMinimalEntry(ModuloDimensionEntries entry) {
        long filePosition = entry.getFilePosition();
        int dimensionStartX = entry.getDimension("X").start;
        int dimensionStartY = entry.getDimension("Y").start;
        int dimensionStartZ = 0;
        if (entry.hasDimension("Z")) {
            dimensionStartZ = entry.getDimension("Z").start;
        }
        int storedSizeX = entry.getDimension("X").storedSize;
        int storedSizeY = entry.getDimension("Y").storedSize;
        int filePart = entry.filePart;
        return new MinDimEntry(dimensionStartZ, filePosition, dimensionStartX, dimensionStartY, storedSizeX, storedSizeY, filePart);
    }*/

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
    final String fileName;
    public CZISegments(String id, boolean littleEndian) throws IOException {
      this.fileName = id;
      this.fileHeader = LibCZI.getFileHeaderSegment(id, BUFFER_SIZE, littleEndian);
      this.subBlockDirectory = LibCZI.getSubBlockDirectorySegment(this.fileHeader.data.subBlockDirectoryPosition, id, BUFFER_SIZE, littleEndian);
      this.attachmentDirectory = LibCZI.getAttachmentDirectorySegment(this.fileHeader.data.attachmentDirectoryPosition, id, BUFFER_SIZE, littleEndian);

      // For searching of blocks at the end of the file, in case the metadata subblock has been deleted
      long lastBlockPosition = LibCZI.getPositionLastBlock(subBlockDirectory);
      this.metadata = LibCZI.getMetaDataSegment(this.fileHeader.data.metadataPosition, id, BUFFER_SIZE, littleEndian, lastBlockPosition);

      if (attachmentDirectory!=null) {
        this.timeStamps = LibCZI.getTimeStamps(this.attachmentDirectory, id, BUFFER_SIZE, littleEndian);
        //System.out.println("#ts="+timeStamps.length);
                /*for (double timeStamp: timeStamps) {
                    System.out.println(timeStamp);
                }*/
      } else {
        this.timeStamps = new double[0];
      }
    }
  }

  static class SubBlockLRUCache extends
          LinkedHashMap<MinDimEntry, SoftReference<byte[]>>
  {

    private static final long serialVersionUID = 1L;

    private long maxCost;

    AtomicLong totalWeight = new AtomicLong();

    HashMap<MinDimEntry, Long> cost = new HashMap<>();

    public SubBlockLRUCache(final int iniSize, final long maxCost) {
      super(iniSize, 0.75f, true);
      this.maxCost = maxCost;
    }

    public void setMaxCost(long maxCost) {
      this.maxCost = maxCost;
    }

    public long getCost() {
      return totalWeight.get();
    }

    public long getMaxCost() {
      return maxCost;
    }

    @Override
    protected boolean removeEldestEntry(
            final Map.Entry<MinDimEntry, SoftReference<byte[]>> eldest)
    {
      if (totalWeight.get() > maxCost) {
        totalWeight.addAndGet(-cost.get(eldest.getKey()));
        cost.remove(eldest.getKey());
        eldest.getValue().clear();
        //System.out.println("Remove");
        return true;
      }
      else return false;
    }

    synchronized public void touch(final MinDimEntry key,
                                   final byte[] value)
    {
      final SoftReference<byte[]> ref = get(key);
      if (ref == null) {
        long costValue = value.length;//getWeight(value);
        totalWeight.addAndGet(costValue);
        cost.put(key, costValue);
        //System.out.println(totalWeight.get()/(1024*1024)+" Mb");
        put(key, new SoftReference<>(value));
      }
      else if (ref.get() == null) {
        put(key, new SoftReference<>(value));
      }
    }

    @Override
    public synchronized void clear() {
      for (final SoftReference<byte[]> ref : values()) {
        ref.clear();
      }
      totalWeight.set(0);
      cost.clear();
      super.clear();
    }
  }

  /**
   * This Zeiss Reader class was initially huge and contained many fields. One issue
   * is that these fields were only necessary during the reader initialisation (method initFile)
   * and were not needed after.
   * <p>
   * As a consequence, many of these fields were transient: they are not serialized. That was the original
   * design. However, this was creating a bit of confusion regarding the accessible and initialized fields that
   * you could access after the reader was initialized.
   * <p>
   * This creates a bit of confusion regarding the fields which are necessary during the initialisation only
   * and the fields which are necessary just when retrieving data.
   * <p>
   * So, in order to solve this issue and clarify a bit the structure of the reader, all these transient fields
   * required for initialisation and the methods containing the logic of the metadata reading / translating
   * have been moved into this inner Initializer class.
   * <p>
   * There should be a unique initializer object created in the initFile method, and its scope SHOULD NOT extend beyond
   * the initFile method.
   * <p>
   * This could also be a non-static class that do not need to pass the object in the constructor.
   */
  private static class MetadataInitializer {

    MetadataInitializer(ZeissCZIReader reader) {
      this.reader = reader;
    }

    void initializeMetadata(Map<Integer, CZISegments> cziPartToSegments,
                            List< // CoreIndex
                                    HashMap<CZTKey, // CZT
                                            // List<LibCZI.SubBlockDirectorySegment.SubBlockDirectorySegmentData.SubBlockDirectoryEntry>>>
                                            List<MinDimEntry>>>
                                    mapCoreTZCToBlocks) throws FormatException, IOException {
      // MetaData from xml file
      DocumentBuilder parser = XMLTools.createBuilder();

      // But is everything in the master file ??? TODO: verify whether all xml metadata are in the master file
      readXMLMetadata(cziPartToSegments.get(0).metadata, parser);

      // Timestamps are already read when creating the CZISegments objects
      MetadataTools.populatePixels(reader.store, reader, true);

      // Needs to set the instrument reference before calling the next method, assumed only one per czi file (or fileset for multi-series files)
      reader.store.setInstrumentID(MetadataTools.createLSID("Instrument", 0), 0);

      setExperimenterInformation();

      // TODO: is everything in the master file ?
      setSpaceAndTimeInformation(mapCoreTZCToBlocks, parser, cziPartToSegments.get(0));

      setImageNames();

      setAdditionalImageMetadata();

      setChannelMetadata();

      //setExtraImagesSpatialInformation();

      setPlateInformation();

      setModuloLabels(reader.core.get(0));
    }

    final ZeissCZIReader reader;

    private String userName,
            userFirstName,
            userLastName,
            userMiddleName,
            userEmail,
            userInstitution;

    private String temperature, airPressure, humidity, co2Percent;

    private String gain;

    private String imageName;

    private String acquiredDate;

    private String description;

    private String userDisplayName;

    private String correctionCollar, medium, refractiveIndex;

    private Time timeIncrement;

    final private ArrayList<String> gains = new ArrayList<>();

    private Length zStep;

    private String objectiveSettingsID;

    private int plateRows;

    private int plateColumns;

    final private ArrayList<String> platePositions = new ArrayList<>();

    final private ArrayList<String> fieldNames = new ArrayList<>();

    final private ArrayList<String> imageNames = new ArrayList<>();

    private Timestamp[] coreIndexTimeStamp;

    final private Map<Integer, Length> coreToPixSizeX = new HashMap<>();
    final private Map<Integer, Length> coreToPixSizeY = new HashMap<>();
    final private Map<Integer, Length> coreToPixSizeZ = new HashMap<>(); // Because I can't read from the store.... RAAHAAH

    final private ArrayList<Channel> channels = new ArrayList<>();

    final private ArrayList<String> binnings = new ArrayList<>();

    private String zoom;

    final private ArrayList<String> detectorRefs = new ArrayList<>();

    private boolean hasDetectorSettings = false;

    private String[] rotationLabels, phaseLabels, illuminationLabels;

    final AllPositionsInformation allPositionsInformation = new AllPositionsInformation();

    private void setExperimenterInformation() {
      // User information fields
      String experimenterID = MetadataTools.createLSID("Experimenter", 0);
      reader.store.setExperimenterID(experimenterID, 0);
      reader.store.setExperimenterEmail(userEmail, 0);
      reader.store.setExperimenterFirstName(userFirstName, 0);
      reader.store.setExperimenterInstitution(userInstitution, 0);
      reader.store.setExperimenterLastName(userLastName, 0);
      reader.store.setExperimenterMiddleName(userMiddleName, 0);
      reader.store.setExperimenterUserName(userName, 0);
      for (int iSeries=0; iSeries<reader.getSeriesCount();iSeries++) {
        reader.store.setImageExperimenterRef(experimenterID, iSeries);
      }
    }

    private void setPlateInformation() {

      if (plateRows > 0 && plateColumns > 0 && platePositions.size() > 0) {
        reader.store.setPlateID(MetadataTools.createLSID("Plate", 0), 0);
        reader.store.setPlateRows(new PositiveInteger(plateRows), 0);
        reader.store.setPlateColumns(new PositiveInteger(plateColumns), 0);

        int fieldsPerWell = fieldNames.size() / platePositions.size();
        if (fieldNames.size() == 0) {
          fieldsPerWell = 1;
        }

        int nextWell = 0;
        int nextField = 0;
        for (int i=0, img=0; img<reader.core.size(); i++, img+=reader.core.get(img).resolutionCount) {
          if (nextWell < platePositions.size() && platePositions.get(nextWell) != null) {
            String[] index = platePositions.get(nextWell).split("-");
            if (index.length != 2) {
              continue;
            }
            int row = -1;
            int column = -1;

            try {
              row = Integer.parseInt(index[0]) - 1;
              column = Integer.parseInt(index[1]) - 1;
            }
            catch (NumberFormatException e) {
              LOGGER.trace("Could not parse well position", e);
            }

            int field = 0;
            if (i < fieldNames.size()) {
              String fieldName = fieldNames.get(i);
              try {
                field = Integer.parseInt(fieldName.substring(1)) - 1; // name starts with "P"
              }
              catch (NumberFormatException e) {
                LOGGER.warn("Could not parse field name {}; plate layout may be incorrect", fieldName);
              }
            }

            if (row >= 0 && column >= 0) {
              int imageIndex = reader.coreIndexToSeries(img);
              reader.store.setWellID(MetadataTools.createLSID("Well", 0, nextWell), 0, nextWell);
              reader.store.setWellRow(new NonNegativeInteger(row), 0, nextWell);
              reader.store.setWellColumn(new NonNegativeInteger(column), 0, nextWell);
              reader.store.setWellSampleID(MetadataTools.createLSID("WellSample", 0, nextWell, nextField), 0, nextWell, nextField);
              reader.store.setWellSampleImageRef(MetadataTools.createLSID("Image", imageIndex), 0, nextWell, nextField);
              reader.store.setWellSampleIndex(new NonNegativeInteger(imageIndex), 0, nextWell, nextField);

              nextField++;
              if (nextField == fieldsPerWell) {
                nextField = 0;
                nextWell++;
              }
            }
          }
        }
      }
    }

    private void readXMLMetadata(LibCZI.MetaDataSegment metaDataSegment, DocumentBuilder parser) throws FormatException, IOException {
      String xml = metaDataSegment.data.xml;
      xml = XMLTools.sanitizeXML(xml);
      //System.out.println(xml);
      translateMetadata(xml, parser);
    }

    private void translateMetadata(String xml, DocumentBuilder parser) throws FormatException, IOException {
      Element root;
      try ( ByteArrayInputStream s = new ByteArrayInputStream(xml.getBytes(Constants.ENCODING))) {
        root = parser.parse(s).getDocumentElement();
      } catch (SAXException e) {
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

      if (realRoot == null) {
        throw new RuntimeException("The CZI XML root element is null");
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

    private void translateExperiment(Element root) throws FormatException {
      NodeList experiments = root.getElementsByTagName("Experiment");
      if (experiments.getLength() == 0) {
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

            reader.store.setDetectorID(id, 0, i);
            String model = detector.getAttribute("Id");
            reader.store.setDetectorModel(model, 0, i);

            String bin = getFirstNodeValue(detector, "Binning");
            if (bin != null) {
              bin = bin.replaceAll(",", "x");
              Binning binning = MetadataTools.getBinning(bin);

              if (model.equals(cameraModel)) {
                for (int image = 0; image < reader.getSeriesCount(); image++) {
                  for (int c = 0; c < reader.getEffectiveSizeC(); c++) {
                    reader.store.setDetectorSettingsID(id, image, c);
                    reader.store.setDetectorSettingsBinning(binning, image, c);
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

          if (detectors.getLength() > 0) {
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

        if (tracks.getLength() > 0) {
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
                channels.get(i).exposure = Double.valueOf(exposure);
              }
            } catch (NumberFormatException e) {
              LOGGER.debug("Could not parse exposure time", e);
            }
            try {
              if (gain != null) {
                channels.get(i).gain = Double.valueOf(gain);
              }
            } catch (NumberFormatException e) {
              LOGGER.debug("Could not parse gain", e);
            }
          }
        }
      }
    }

    private void translateInformation(Element root) throws FormatException {
      MetadataStore store = reader.store;

      NodeList informations = root.getElementsByTagName("Information");
      if (informations.getLength() == 0) {
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
          reader.core.get(0).bitsPerPixel = Integer.parseInt(bitCount);
        } // TODO: understand if the line above is necessary

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
              //if (nextPosition < positionsX.length && positionsX[nextPosition] == null) {

              XYZLength loc = new XYZLength();
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
              XYZLength loc = new XYZLength();

              loc.pX = new Length(DataTools.parseDouble(pos[0]), UNITS.MICROMETER);
              loc.pY = new Length(DataTools.parseDouble(pos[1]), UNITS.MICROMETER);

              currentSceneProps.pos.add(loc);
              //}
              //nextPosition++;
            }

          }
        }

        NodeList channelNodes = getGrandchildren(dimensions, "Channel");
        if (channelNodes == null) {
          channelNodes = image.getElementsByTagName("Channel");
        }

        for (int i = 0; i < channelNodes.getLength(); i++) {
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
            if (acquisition.equals("LatticeLightsheet")) {
              setLatticeLightSheet(true);
            }
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
              detectorID = detectorID.replaceAll("\\s", "");
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
        Element manufacturerNode;

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
                store.setLaserPower(new Power(Double.valueOf(power), UNITS.MILLIWATT), 0, i);
              }
              store.setLaserLotNumber(lotNumber, 0, i);
              store.setLaserManufacturer(manufacturer, 0, i);
              store.setLaserModel(model, 0, i);
              store.setLaserSerialNumber(serialNumber, 0, i);
            }
            else if ("Arc".equals(type)) {
              if (power != null) {
                store.setArcPower(new Power(Double.valueOf(power), UNITS.MILLIWATT), 0, i);
              }
              store.setArcLotNumber(lotNumber, 0, i);
              store.setArcManufacturer(manufacturer, 0, i);
              store.setArcModel(model, 0, i);
              store.setArcSerialNumber(serialNumber, 0, i);
            }
            else if ("LightEmittingDiode".equals(type)) {
              if (power != null) {
                store.setLightEmittingDiodePower(new Power(Double.valueOf(power), UNITS.MILLIWATT), 0, i);
              }
              store.setLightEmittingDiodeLotNumber(lotNumber, 0, i);
              store.setLightEmittingDiodeManufacturer(manufacturer, 0, i);
              store.setLightEmittingDiodeModel(model, 0, i);
              store.setLightEmittingDiodeSerialNumber(serialNumber, 0, i);
            }
            else if ("Filament".equals(type)) {
              if (power != null) {
                store.setFilamentPower(new Power(Double.valueOf(power), UNITS.MILLIWATT), 0, i);
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
          HashSet<String> uniqueDetectors = new HashSet<>();
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
              store.setDetectorOffset(Double.parseDouble(offset), 0, detectorIndex);
            }

            zoom = getFirstNodeValue(detector, "Zoom");
            if (zoom != null && !zoom.isEmpty()) {
              if (!zoom.equals("")) {
                if (zoom.indexOf(',') != -1) {
                  zoom = zoom.substring(0, zoom.indexOf(','));
                }
                store.setDetectorZoom(DataTools.parseDouble(zoom), 0, detectorIndex);
              }
            }

            String ampGain = getFirstNodeValue(detector, "AmplificationGain");
            if (ampGain != null && !ampGain.equals("")) {
              store.setDetectorAmplificationGain(Double.parseDouble(ampGain), 0, detectorIndex);
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

            store.setFilterSetID(filterSet.getAttribute("Id"), 0, i);
            store.setFilterSetManufacturer(manufacturer, 0, i);
            store.setFilterSetModel(model, 0, i);
            store.setFilterSetSerialNumber(serialNumber, 0, i);
            store.setFilterSetLotNumber(lotNumber, 0, i);

            if (dichroicRef != null && dichroicRef.length() > 0) {
              store.setFilterSetDichroicRef(dichroicRef, 0, i);
            }

            for (int ex = 0; ex < excitations.getLength(); ex++) {
              Element excitation = (Element) excitations.item(ex);
              String ref = excitation.getTextContent();
              if (ref == null || ref.length() <= 0) {
                ref = excitation.getAttribute("Id");
              }
              if (ref.length() > 0) {
                store.setFilterSetExcitationFilterRef(ref, 0, i, ex);
              }
            }
            for (int em = 0; em < emissions.getLength(); em++) {
              Element emission = (Element) emissions.item(em);
              String ref = emission.getTextContent();
              if (ref == null || ref.length() <= 0) {
                ref = emission.getAttribute("Id");
              }
              if (ref.length() > 0) {
                store.setFilterSetEmissionFilterRef(ref, 0, i, em);
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
            Double inWave = cutIn == null ? 0 : Double.parseDouble(cutIn);
            Double outWave = cutOut == null ? 0 : Double.parseDouble(cutOut);

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
              Double cutInTolerance = Double.parseDouble(inTolerance);
              store.setTransmittanceRangeCutInTolerance(
                      new Length(cutInTolerance, UNITS.NANOMETER), 0, i);
            }

            if (outTolerance != null) {
              Double cutOutTolerance = Double.parseDouble(outTolerance);
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

    private static String getFirstNodeValue(Element root, String name) {
      if (root == null) {
        return null;
      }
      NodeList nodes = root.getElementsByTagName(name);
      if (nodes.getLength() > 0) {
        return nodes.item(0).getTextContent();
      }
      return null;
    }

    private static NodeList getGrandchildren(Element root, String name) {
      return getGrandchildren(root, name + "s", name);
    }

    private static NodeList getGrandchildren(Element root, String child, String name) {
      if (root == null) {
        return null;
      }
      NodeList children = root.getElementsByTagName(child);
      if (children.getLength() > 0) {
        Element childNode = (Element) children.item(0);
        return childNode.getElementsByTagName(name);
      }
      return null;
    }

    private Element getFirstNode(Element root, String name) {
      if (root == null) {
        return null;
      }
      NodeList list = root.getElementsByTagName(name);
      if (list.getLength() == 0) {
        return null;
      }
      return (Element) list.item(0);
    }

    private void setAdditionalImageMetadata() throws FormatException {

      for (int iSeries=0; iSeries<reader.getSeriesCount(); iSeries++) {

        // Should not do the rest for extra images
        // remaining acquisition settings (esp. channels) do not apply to
        // label and macro images
        int extraIndex = iSeries - (reader.getSeriesCount() - reader.extraImages.size());
        if (extraIndex >= 0) {
          continue;
        }

        if (description != null && description.length() > 0) {
          reader.store.setImageDescription(description, iSeries);
        }

        if (airPressure != null) {
          reader.store.setImagingEnvironmentAirPressure(
                  new Pressure(Double.parseDouble(airPressure), UNITS.MILLIBAR), iSeries);
        }

        if (co2Percent != null) {
          reader.store.setImagingEnvironmentCO2Percent(
                  PercentFraction.valueOf(co2Percent), iSeries);
        }
        if (humidity != null) {
          reader.store.setImagingEnvironmentHumidity(
                  PercentFraction.valueOf(humidity), iSeries);
        }
        if (temperature != null) {
          reader.store.setImagingEnvironmentTemperature(new Temperature(
                  Double.valueOf(temperature), UNITS.CELSIUS), iSeries);
        }

        if (objectiveSettingsID != null) {
          reader.store.setObjectiveSettingsID(objectiveSettingsID, iSeries);
          if (correctionCollar != null) {
            reader.store.setObjectiveSettingsCorrectionCollar(
                    Double.parseDouble(correctionCollar), iSeries);
          }
          if (medium != null) {
            reader.store.setObjectiveSettingsMedium(MetadataTools.getMedium(medium), iSeries);
          }
          if (refractiveIndex != null) {
            reader.store.setObjectiveSettingsRefractiveIndex(
                    Double.parseDouble(refractiveIndex), iSeries);
          }
        }
      }
    }

    private void setImageNames() {
      // Forces recomputing
      reader.series = -1;
      int nSeries = reader.getSeriesCount();
      String name = new Location(reader.getCurrentFile()).getName();
      if (imageName != null && imageName.trim().length() > 0) {
        name = imageName;
      }

      int indexLength = String.valueOf(nSeries).length();

      for (int iSeries=0; iSeries<nSeries; iSeries++) {

        String imageIndex = String.valueOf(iSeries + 1);
        while (imageIndex.length() < indexLength) {
          imageIndex = "0" + imageIndex;
        }

        reader.series = -1;
        int extraIndex = iSeries - (nSeries - reader.extraImages.size());
        if (extraIndex < 0) {
          if (reader.hasFlattenedResolutions()) {
            reader.store.setImageName(name + " #" + imageIndex, iSeries);
          } else if (false) {// TODO fix! (positions == 1) {
            if (imageNames.size() == 1) {
              reader.store.setImageName(imageNames.get(0), iSeries);
            } else {
              reader.store.setImageName("", iSeries);
            }
          } else {
            if (iSeries < imageNames.size()) {
              String completeName = imageNames.get(iSeries);
              if (iSeries < fieldNames.size()) {
                completeName += " " + fieldNames.get(iSeries);
              }
              reader.store.setImageName(completeName, iSeries);
            } else {
              int paddingLength = ("" + nSeries).length();
              reader.store.setImageName("Scene #" + String.format("%0" + paddingLength + "d", (iSeries + 1)), iSeries);
            }
          }
        } else if (extraIndex == 0) {
          reader.store.setImageName("label image", iSeries);
        } else if (extraIndex == 1) {
          reader.store.setImageName("macro image", iSeries);
        } else {
          reader.store.setImageName("thumbnail image", iSeries);
        }
      }

    }

    private LibCZI.SubBlockMeta getSubBlockMeta(int c, int z, int t, int coreIdx,
                                                List< // CoreIndex
                                                        HashMap<CZTKey, // CZT
                                                                List<MinDimEntry>>>
                                                        mapCoreCZTToBlocks,
                                                DocumentBuilder parser) throws IOException {
      CZTKey czt = new CZTKey(c,z,t);
      List<MinDimEntry> blocks = mapCoreCZTToBlocks.get(coreIdx).get(czt);
      if ((blocks==null) || (blocks.size()==0)) return null;
      LibCZI.SubBlockSegment block = LibCZI.getBlock(reader.getStream(blocks.get(0).filePart), blocks.get(0).filePosition);
      return LibCZI.readSubBlockMeta(reader.getStream(blocks.get(0).filePart), block, parser);
    }

    private class Corner {
      Length x, y, z;

      Corner fromSubBlockMeta(Collection<MinDimEntry> blocks, int iCoreIndex, Unit<Length> unitLength, DocumentBuilder parser) {

        for (MinDimEntry iBlock : blocks) {
          try {
            LibCZI.SubBlockSegment block = LibCZI.getBlock(reader.getStream(iBlock.filePart), iBlock.filePosition);
            LibCZI.SubBlockMeta sbm = LibCZI.readSubBlockMeta(reader.getStream(iBlock.filePart), block, parser);

            if (sbm.stageX != null) {
              //  if true, the unit is micrometer
              unitLength = UNITS.MICROMETER;
              if ((x == null) || (x.value(unitLength).doubleValue() > sbm.stageX.value(unitLength).doubleValue())) {
                x = new Length(sbm.stageX.value(unitLength).doubleValue(), unitLength);
              }
            }

            if (sbm.stageY != null) {
              //  if true, the unit is micrometer
              unitLength = UNITS.MICROMETER;
              if ((y == null) || (y.value(unitLength).doubleValue() > sbm.stageY.value(unitLength).doubleValue())) {
                y = new Length(sbm.stageY.value(unitLength).doubleValue(), unitLength);
              }
            }
            if ((z == null) || (z.value(unitLength).doubleValue() > sbm.stageZ.value(unitLength).doubleValue())) {
              z = sbm.stageZ;
            }
          } catch (Exception e) {
            e.printStackTrace();
            return this;
          }
        }
        return this;
      }

      Corner fromBlocks(Collection<MinDimEntry> blocks, int iCoreIndex, Unit<Length> unitLength) {
        for (MinDimEntry iBlock : blocks) {
          Length posX = new Length((double) iBlock.dimensionStartX/(double) reader.coreIndexToDownscaleFactor.get(iCoreIndex)
                  *coreToPixSizeX.get(iCoreIndex).value(unitLength).doubleValue(), unitLength);
          if ((x == null)||(x.value().doubleValue()>posX.value(unitLength).doubleValue())) {
            x = posX;
          }
          Length posY = new Length((double) iBlock.dimensionStartY/(double) reader.coreIndexToDownscaleFactor.get(iCoreIndex)
                  *coreToPixSizeY.get(iCoreIndex).value(unitLength).doubleValue(), unitLength);
          if ((y == null)||(y.value().doubleValue()>posY.value(unitLength).doubleValue())) {
            y = posY;
          }
          if (coreToPixSizeZ.size()!=0) {
            if (coreToPixSizeZ.get(iCoreIndex).unit().equals(unitLength)) { // Sometimes, x and y are in um while z is undef
              Length posZ = new Length((double) iBlock.dimensionStartZ
                      * coreToPixSizeZ.get(iCoreIndex).value(unitLength).doubleValue(), unitLength);
              if ((z == null) || (z.value().doubleValue() > posZ.value(unitLength).doubleValue())) {
                z = posZ;
              }
            }
          }
        }
        return this;
      }

    }

    private void setSpaceAndTimeInformation( // of series and of planes
                                             List< // CoreIndex
                                                     HashMap<CZTKey, // CZT
                                                             List<MinDimEntry>>>
                                                     mapCoreCZTToBlocks,
                                             DocumentBuilder parser, CZISegments cziSegments) throws IOException {

      // Time : timestamp storing when each series was acquired
      coreIndexTimeStamp = new Timestamp[reader.core.size()];

      // Flags that prevents the reading of all metadata subblocks - there are way too many in LLS acquisition and
      // this slows down drastically the initialisation time
      boolean interpolatePlanePositionOverZ = isLatticeLightSheet();

      // SPACE : according to czi specs, all subblocks are located within a common 2D virtual plane
      // However, there's no easy way to know the physical coordinates of the origin of this virtual plane.
      // See thread https://forum.image.sc/t/czi-plane-position-what-to-pick/85484
      // This reading:
      // - uses the subblock coordinates to position each sub-block relative ot one another
      // - attempts to find the physical coordinate of the virtual plane by several ways:
      //      - Strategy 1: computing the physical coordinate from a single sub-block which contain the stage position in the subblock metadata
      //      - OR Strategy 2: getting the physical coordinate from the main document metadata (first xml segment)
      // - this offset is stored in the two variables below. If both methods fails, the corner pos is NaN
      // and is subsequently ignored
      double cornerXAllScenesMicrons = Double.NaN;
      double cornerYAllScenesMicrons = Double.NaN;

      // Attempt strategy 1:
      // - Let's pick the first subblock of the first core index
      int coreUsedForXYOffset = 0;
      MinDimEntry firstSubBlock = mapCoreCZTToBlocks.get(coreUsedForXYOffset).get(new CZTKey(0, 0, 0)).get(0);

      LibCZI.SubBlockSegment block = LibCZI.getBlock(reader.getStream(firstSubBlock.filePart), firstSubBlock.filePosition);
      LibCZI.SubBlockMeta sbm = LibCZI.readSubBlockMeta(reader.getStream(firstSubBlock.filePart), block, parser);
      // Here we assume the first core is not a special weird image like label or overview
      // - Is there any valid metadata tags for the stage position ?
      if (((sbm.stageX!=null)&&(sbm.stageX.unit().equals(UNITS.MICROMETER)))&&
              ((sbm.stageY!=null)&&(sbm.stageY.unit().equals(UNITS.MICROMETER)))&&
              (coreToPixSizeX.get(coreUsedForXYOffset).unit().equals(UNITS.MICROMETER))&&
              (coreToPixSizeY.get(coreUsedForXYOffset).unit().equals(UNITS.MICROMETER))) {
        // Yes! Let's apply Strategy 1
        // What is the block coordinates in pixel?
        int coordX = firstSubBlock.dimensionStartX;
        int coordY = firstSubBlock.dimensionStartY;
        // Let's compute this block coordinates in micrometer
        double pixSizeXMicrometer = coreToPixSizeX.get(coreUsedForXYOffset).value(UNITS.MICROMETER).doubleValue();
        double pixSizeYMicrometer = coreToPixSizeY.get(coreUsedForXYOffset).value(UNITS.MICROMETER).doubleValue();
        double coordXBlockMicrometer = coordX*pixSizeXMicrometer;
        double coordYBlockMicrometer = coordY*pixSizeYMicrometer;
        // Ok, now we have the correspondence between block dimension entry coordinates in micrometer:
        // (coordXMiddleBlockMicrometer, coordYMiddleBlockMicrometer)
        // and the subblock coordinates from the stage metadata:
        double metaStageCoordX = sbm.stageX.value(UNITS.MICROMETER).doubleValue();
        double metaStageCoordY = sbm.stageY.value(UNITS.MICROMETER).doubleValue();
        // We can thus deduce the physical coordinates at the origin of the 2D virtual plane
        cornerXAllScenesMicrons = metaStageCoordX-coordXBlockMicrometer;
        cornerYAllScenesMicrons = metaStageCoordY-coordYBlockMicrometer;
        LOGGER.debug("Global image origin X acquired from sub-block meta (micrometer) {}", cornerXAllScenesMicrons);
        LOGGER.debug("Global image origin Y acquired from sub-block meta (micrometer) {}", cornerYAllScenesMicrons);
      } else {
        // Let's attempt Strategy 2
        if ((allPositionsInformation.scenes.size()>0)&&(!coreToPixSizeX.get(0).unit().equals(UNITS.REFERENCEFRAME))) {
          // Get the coordinate of the most top left corner of all scenes
          Length minX = null, minY = null;
          for (SceneProperties scene:allPositionsInformation.scenes) {
            Length scenePosX = scene.getMinPosXInMicrons();
            Length scenePosY = scene.getMinPosYInMicrons();
            if ((scenePosX!=null)&&(scenePosX.unit().equals(UNITS.MICROMETER))) {
              if ((minX==null)||(minX.value(UNITS.MICROMETER).doubleValue()>scenePosX.value(UNITS.MICROMETER).doubleValue())) {
                minX = scenePosX;
              }
            }
            if ((scenePosY!=null)&&(scenePosY.unit().equals(UNITS.MICROMETER))) {
              if ((minY==null)||(minY.value(UNITS.MICROMETER).doubleValue()>scenePosY.value(UNITS.MICROMETER).doubleValue())) {
                minY = scenePosY;
              }
            }
          }
          if ((minX!=null)&&(minY!=null)) {
            // Applies strategy 2
            cornerXAllScenesMicrons = minX.value(UNITS.MICROMETER).doubleValue();
            cornerYAllScenesMicrons = minY.value(UNITS.MICROMETER).doubleValue();
            LOGGER.debug("Global image origin X acquired from global meta (micrometer) {}", cornerXAllScenesMicrons);
            LOGGER.debug("Global image origin Y acquired from global meta (micrometer) {}", cornerYAllScenesMicrons);
          }
        }
      }

      Map<Integer, double[]> timeStampsResolutionLevel0 = new HashMap<>();
      Length planePosXResolutionLevel0 = null;
      Length planePosYResolutionLevel0 = null;
      Length planePosZResolutionLevel0 = null;

      //  Set the space and time information for all core indices
      for (int iCoreIndex = 0; iCoreIndex<reader.core.size(); iCoreIndex++) {

        // Skips metadata for thumbnails and labels - it's been fetched, if it exists, in another reader
        int extraIndex = iCoreIndex - (reader.core.size() - reader.extraImages.size());
        if (extraIndex >= 0) {
          continue;
        }

        // Set properly series and corresponding core index
        // (the setCoreIndex methods behaves weirdly in the super class, that's why it is set as shown below)
        reader.coreIndex = iCoreIndex;
        reader.series = reader.coreIndexToSeries.get(reader.coreIndex);

        // A flag that's useful to know if the current core corresponds to a lower resolution level
        // useful because the metadata for lower resolution levels will be copied from the highest one
        boolean resolutionLevel0 = reader.coreIndexToDownscaleFactor.get(reader.coreIndex)==1;

        // This unit will be either ReferenceFrame for some czi images (for instance artificial dataset
        // not generated from a microscope) or, micrometers in the other cases
        Unit<Length> unitLength = coreToPixSizeX.get(iCoreIndex).unit();

        CoreSignature signature = reader.coreIndexToSignature.get(iCoreIndex);

        // Set stage name
        String sceneName;
        Length scenePosZ = null;
        if (signature.getDimensions().containsKey("S")) {
          int sceneIndex = signature.getDimensions().get("S");
          if (allPositionsInformation.scenes.size()>sceneIndex) {
            sceneName = allPositionsInformation.scenes.get(sceneIndex).name;
            if ((sceneName == null)||(sceneName.trim().equals(""))) sceneName = "Scene position #"+sceneIndex;
            scenePosZ = allPositionsInformation.scenes.get(sceneIndex).getMinPosZInMicrons();
          } else {
            sceneName = "Scene position #"+sceneIndex;
          }
        } else {
          // default name
          sceneName = "Scene position #"+0;
          if (allPositionsInformation.scenes.size()==1) {
            scenePosZ = allPositionsInformation.scenes.get(0).getMinPosZInMicrons();
          }
        }
        reader.store.setStageLabelName(sceneName, reader.series);

        // TIME: Let's set the acquisition date of this series : it's the same
        // for all series, but there will be an offset on the first plane

        Timestamp seriesT0 = null;
        if (acquiredDate!=null) {
          seriesT0 = new Timestamp(acquiredDate);
          reader.store.setImageAcquisitionDate(seriesT0, reader.series);
        }

        int nChannels = reader.getSizeC();
        List<MinDimEntry> blocks;

        Length planePosX, planePosY, planePosZ = null; // plane position of the current coreindex - do not vary over z and t, but that could happen

        blocks = mapCoreCZTToBlocks.get(iCoreIndex).get(new CZTKey(0,0,0));

        if (!resolutionLevel0) {
          // Use the same position as the higher resolution level
          // Keeping the last highest resolutio works because bio-formats forces the resolution
          // level series to be sorted according to the core series index:
          // res 0 series i / res 1 series i / res 2 series i / res 0 series i+1 / res 1 series i+1 etc.
          planePosX = planePosXResolutionLevel0;
          planePosY = planePosYResolutionLevel0;
          planePosZ = planePosZResolutionLevel0;
        } else {
          // The most complicated logic
          // ------ Here's the available data:

          // XYZ - Plane position according to the block position (should not be null - compulsory in czi file)
          Corner blocksCorner = new Corner().fromBlocks(blocks, iCoreIndex, unitLength);
          // XYZ - Plane position according to the subblock xml metadata (could be null)
          Corner subBlockMetaCorner = new Corner().fromSubBlockMeta(blocks, iCoreIndex, unitLength, parser);
          // XY - (offsetXInMicrons, offsetYInMicrons) : the top left corner, according to the xml czi header (could be NaN)
          // Z - scenePosZ : the z location of the current scene, according to the xml czi header (could be Null)
                    /*System.out.println("- Id = "+reader.currentId);
                    System.out.println("- CoreIndex = "+reader.coreIndex+" Res Lev 0 = "+resolutionLevel0);
                    System.out.println("Min XYZ location according to block start position:");
                    System.out.println("\tX="+blocksCorner.x);
                    System.out.println("\tY="+blocksCorner.y);
                    System.out.println("\tZ="+blocksCorner.z);
                    System.out.println("Min XYZ location according to subblock metadata position:");
                    System.out.println("\tX="+subBlockMetaCorner.x);
                    System.out.println("\tY="+subBlockMetaCorner.y);
                    System.out.println("\tZ="+subBlockMetaCorner.z);
                    System.out.println("XY Global offset according to main xml document:");
                    System.out.println("\tX="+offsetXInMicrons);
                    System.out.println("\tY="+offsetYInMicrons);
                    System.out.println("Z position of current scene according to main xml document:");
                    System.out.println("\tZ="+scenePosZ);*/

          // XY : keep blocks only
          planePosX = blocksCorner.x;
          planePosY = blocksCorner.y;

          if (planePosX.unit().equals(UNITS.MICROMETER) && (planePosY.unit().equals(UNITS.MICROMETER))) {
            // Let's add a global stage offset, if possible
            if ((!Double.isNaN(cornerXAllScenesMicrons))&&(!Double.isNaN(cornerXAllScenesMicrons))) {
              planePosX = new Length(planePosX.value(UNITS.MICROMETER).doubleValue()+cornerXAllScenesMicrons, UNITS.MICROMETER);
              planePosY = new Length(planePosY.value(UNITS.MICROMETER).doubleValue()+cornerYAllScenesMicrons, UNITS.MICROMETER);
            }
          }

          if ((blocksCorner.z!=null)&&(subBlockMetaCorner.z!=null)&&(blocksCorner.z.unit().equals(subBlockMetaCorner.z.unit()))) {
            Unit<Length> u = blocksCorner.z.unit();
            planePosZ = new Length(blocksCorner.z.value(u).doubleValue()+subBlockMetaCorner.z.value(u).doubleValue(), u);
          } else if ((blocksCorner.z!=null)&&(scenePosZ!=null)&&(scenePosZ.unit().equals(blocksCorner.z.unit()))) {
            Unit<Length> u = blocksCorner.z.unit();
            planePosZ = new Length(blocksCorner.z.value(u).doubleValue()+scenePosZ.value(u).doubleValue(), u);
          } else if (blocksCorner.z!=null) {
            planePosZ = blocksCorner.z;
          } else if (subBlockMetaCorner.z!=null) {
            planePosZ = subBlockMetaCorner.z;
          } else if (scenePosZ!=null) {
            planePosZ = scenePosZ;
          }

        }

        if (resolutionLevel0) {
          planePosXResolutionLevel0 = planePosX;
          planePosYResolutionLevel0 = planePosY;
          planePosZResolutionLevel0 = planePosZ;
        }

        loopChannel:
        for (int iChannel = 0; iChannel<nChannels; iChannel++) {

          LibCZI.SubBlockMeta sbmziti = getSubBlockMeta(
                  iChannel,
                  0,
                  0,
                  iCoreIndex, mapCoreCZTToBlocks, parser);
          LibCZI.SubBlockMeta sbmzfti = getSubBlockMeta(
                  iChannel,
                  reader.getSizeZ()/reader.nRotations-1,
                  0,
                  iCoreIndex, mapCoreCZTToBlocks, parser);
          LibCZI.SubBlockMeta sbmzitf = getSubBlockMeta(
                  iChannel,
                  0,
                  reader.getSizeT()/reader.nPhases-1,
                  iCoreIndex, mapCoreCZTToBlocks, parser);

          if ((sbmziti==null)||(sbmzfti==null)||(sbmzitf==null)) break loopChannel;

          if (iChannel==0) {
            if (sbmziti.timestamp!=0) { // The image was not taken on Jan 1st 1970...
              long timestamp = (long) (sbmziti.timestamp * 1000);
              String date =
                      DateTools.convertDate(timestamp, DateTools.UNIX);
              coreIndexTimeStamp[iCoreIndex] = new Timestamp(date);
            }
          }

          double incrementTimeOverZ = 0;
          if (reader.getSizeZ()>1) {
            incrementTimeOverZ = (sbmzfti.timestamp - sbmziti.timestamp) / (double) ((reader.getSizeZ()-1) / reader.nRotations);
          }
          double incrementTimeOverT = 0;
          if (reader.getSizeT()>1) {
            incrementTimeOverT = (sbmzitf.timestamp - sbmziti.timestamp) / (double) ((reader.getSizeT()-1) / reader.nPhases);
          }
          Time exposure = null;
          if (!Double.isNaN(sbmziti.exposureTime)) {
            exposure = new Time(sbmziti.exposureTime*1000, UNITS.SECOND);
          } else {
            if (channels.size()>iChannel) { // Could be an issue with illumination TODO check
              Double exposureFromChannel = channels.get(iChannel).exposure;
              if (exposureFromChannel != null) {
                exposure = new Time(exposureFromChannel, UNITS.SECOND);
              }
            }
          }

          double offsetT0;
          double realT0 = 0;
          if (!Double.isNaN(sbmziti.timestamp)) {
            if (seriesT0 == null) {
              offsetT0 = sbmziti.timestamp;
            } else {
              realT0 = seriesT0.asInstant().getMillis() / 1000.0;
              offsetT0 = sbmziti.timestamp - realT0;
            }
          } else {
            if (cziSegments.timeStamps.length>0) {
              offsetT0 = cziSegments.timeStamps[0]; // In seconds
              realT0 = offsetT0;
            } else {
              offsetT0 = 0;
            }
          }
          double offsetZ0;
          if (planePosZ==null) {
            offsetZ0 = Double.NaN;
          } else {
            if (planePosZ.value(unitLength) == null) {
              offsetZ0 = Double.NaN;
            } else {
              offsetZ0 = planePosZ.value(unitLength).doubleValue();
            }
          }
          double stepZ = (zStep==null)?0:zStep.value().doubleValue();

          if (resolutionLevel0) timeStampsResolutionLevel0.put(iChannel, new double[reader.getSizeT()*reader.getSizeZ()]); // Store the timestamps to be copied over resolution levels
          double[] currentTimeStamps = timeStampsResolutionLevel0.get(iChannel);
          int planeCounter = 0;
          if ((reader.flattenedResolutions)||(resolutionLevel0)) { // Avoid setting the metadata not for lower resolution levels, because this override proper metadata if flattenresolution = false
            for (int iTori = 0; iTori < reader.getSizeT(); iTori++) {
              int iT = iTori % (reader.getSizeT() / reader.nPhases);
              double timeStartCurrentFrame = 0;
              if (interpolatePlanePositionOverZ) {
                // We can afford to read the first block of the first timepoint, even for lattice
                LibCZI.SubBlockMeta sbmzt = getSubBlockMeta(
                        iChannel,
                        0,
                        iT,
                        iCoreIndex, mapCoreCZTToBlocks, parser);
                if (Double.isNaN(sbmzt.timestamp)) {
                  if (cziSegments.timeStamps.length>iT) {
                    timeStartCurrentFrame = cziSegments.timeStamps[iT];
                  }
                } else {
                  timeStartCurrentFrame = sbmzt.timestamp - realT0;
                }
              }

              for (int iZori = 0; iZori < reader.getSizeZ(); iZori++) {


                int planeIndex = reader.getIndex(iZori, iChannel, iTori);
                // rotations -> modulo Z
                // illuminations -> modulo C
                // phases -> modulo T

                reader.store.setPlanePositionX(planePosX, reader.series, planeIndex);
                reader.store.setPlanePositionY(planePosY, reader.series, planeIndex);

                if ((exposure != null)&&(!Double.isNaN(exposure.value().doubleValue()))) {
                  reader.store.setPlaneExposureTime(exposure, reader.series, planeIndex); // 0 exposure do not make sense
                }
                int iZ = iZori % (reader.getSizeZ() / reader.nRotations);

                Time dT = null;
                Length pZ = null;

                if (interpolatePlanePositionOverZ) {
                  if ((incrementTimeOverZ >= 0) || (incrementTimeOverT >= 0)) {
                    dT = new Time(timeStartCurrentFrame + incrementTimeOverZ * iZ,
                            UNITS.SECOND);
                  }
                  pZ = new Length(offsetZ0 + iZ * stepZ, unitLength);
                } else {
                  if (resolutionLevel0) {
                    LibCZI.SubBlockMeta sbmzt = getSubBlockMeta(
                            iChannel,
                            iZ,
                            iT,
                            iCoreIndex, mapCoreCZTToBlocks, parser);
                    if (Double.isNaN(sbmzt.timestamp)) {
                      if (cziSegments.timeStamps.length>iT) {
                        dT = new Time(cziSegments.timeStamps[iT],
                                UNITS.SECOND);
                      }
                    } else {
                      dT = new Time(sbmzt.timestamp - realT0,
                              UNITS.SECOND);
                    }
                  }

                  pZ = new Length(offsetZ0 + iZ * stepZ, unitLength);
                }
                if (resolutionLevel0) {
                  if ((dT != null) && (!Double.isNaN(dT.value().doubleValue()))) { // To fit the original reader. NaN -> Null
                    reader.store.setPlaneDeltaT(dT, reader.series, planeIndex);
                    currentTimeStamps[planeCounter] = dT.value().doubleValue();
                  }
                } else {
                  if ((!Double.isNaN(currentTimeStamps[planeCounter]))) { // To fit the original reader. NaN -> Null
                    reader.store.setPlaneDeltaT(new Time(currentTimeStamps[planeCounter], UNITS.SECOND), reader.series, planeIndex);
                  }
                }

                if ((!Double.isNaN(offsetZ0))&&(pZ!=null)&&(!pZ.unit().equals(UNITS.REFERENCEFRAME))) {
                  reader.store.setPlanePositionZ(pZ, reader.series, planeIndex);
                }

                if (planeIndex==0) {
                  reader.store.setStageLabelX(planePosX, reader.series);
                  reader.store.setStageLabelY(planePosY, reader.series);
                  if ((pZ!=null)&&(!Double.isNaN(pZ.value().doubleValue()))&&(!pZ.unit().equals(UNITS.REFERENCEFRAME))) {
                    reader.store.setStageLabelZ(pZ, reader.series);
                  }
                }
                planeCounter++;
              }
            }
          }
        }
      }

      for (int iCoreIndex = 0; iCoreIndex<reader.core.size(); iCoreIndex++) {
        int iSeries = reader.coreIndexToSeries.get(iCoreIndex);
        reader.store.setImageInstrumentRef(MetadataTools.createLSID("Instrument", 0), iSeries);
        if (timeIncrement != null) {
          reader.store.setPixelsTimeIncrement(timeIncrement, iSeries);
        }
      }
    }

    private void translateScaling(Element root) {
      NodeList scalings = root.getElementsByTagName("Scaling");
      if (scalings.getLength() == 0) {
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
          Double value = Double.parseDouble(originalValue) * 1_000_000;
          if (value > 0) {
            for (int iCoreIndex=0; iCoreIndex<reader.core.size(); iCoreIndex++) {
              reader.series = reader.coreIndexToSeries(iCoreIndex);
              reader.coreIndex = iCoreIndex;

              // Issue : the resolution level is not correctly set, it has to be set explicitely
              int extraIndex = iCoreIndex - (reader.core.size() - reader.extraImages.size());
              reader.series = reader.coreIndexToSeries(iCoreIndex);
              if (extraIndex >= 0) continue;

              // setCoreIndex(iCoreIndex); -> THIS JUST DOES NOT SET THE RIGHT RESOLUTION!! TODO: Post an issue
              // Hence this hack:
              int downscale = reader.coreIndexToDownscaleFactor.get(iCoreIndex);
              boolean resolutionZero = (downscale==1)||(reader.flattenedResolutions);// The OR test is there because you need to fill all series with a pixel size sequentially.

              PositiveFloat size = new PositiveFloat(value);

              switch (id) {
                case "X":
                  coreToPixSizeX.put(iCoreIndex, FormatTools.createLength(size.getValue() * downscale, UNITS.MICROMETER));
                  if (resolutionZero) {
                    reader.store.setPixelsPhysicalSizeX(coreToPixSizeX.get(iCoreIndex), reader.series);
                  } break;
                case "Y":
                  coreToPixSizeY.put(iCoreIndex, FormatTools.createLength(size.getValue() * downscale, UNITS.MICROMETER));
                  if (resolutionZero) {
                    reader.store.setPixelsPhysicalSizeY(coreToPixSizeY.get(iCoreIndex), reader.series);
                  } break;
                case "Z":
                  zStep = FormatTools.createLength(size, UNITS.MICROMETER);
                  coreToPixSizeZ.put(iCoreIndex, zStep);
                  if (resolutionZero) {
                    reader.store.setPixelsPhysicalSizeZ(zStep, reader.series);
                  } break;
              }
            }
          }
          else {
            LOGGER.debug(
                    "Expected positive value for PhysicalSize; got {}", value);
            for (int iCoreIndex=0; iCoreIndex<reader.core.size(); iCoreIndex++) {
              if (!coreToPixSizeX.containsKey(iCoreIndex))
                coreToPixSizeX.put(iCoreIndex, new Length(1, UNITS.REFERENCEFRAME));
              if (!coreToPixSizeY.containsKey(iCoreIndex))
                coreToPixSizeY.put(iCoreIndex, new Length(1, UNITS.REFERENCEFRAME));
              if (!coreToPixSizeZ.containsKey(iCoreIndex))
                coreToPixSizeZ.put(iCoreIndex, new Length(1, UNITS.REFERENCEFRAME));
            }
          }
        }
      }
    }

    private void translateLayers(Element root) {
      NodeList layerses = root.getElementsByTagName("Layers");
      if (layerses.getLength() == 0) {
        return;
      }

      Element layersNode = (Element) layerses.item(0);
      NodeList layers = layersNode.getElementsByTagName("Layer");

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

          reader.store.setLineID(
                  MetadataTools.createLSID("Shape", roiCount, shape), roiCount, shape);
          reader.store.setLineID(
                  MetadataTools.createLSID("Shape", roiCount, shape + 1), roiCount, shape + 1);

          String length = getFirstNodeValue(geometry, "Length");
          String centerX = getFirstNodeValue(geometry, "CenterX");
          String centerY = getFirstNodeValue(geometry, "CenterY");

          if (length != null) {
            double halfLen = Double.parseDouble(length) / 2.0;
            if (centerX != null) {
              reader.store.setLineX1(Double.parseDouble(centerX) - halfLen, roiCount, shape);
              reader.store.setLineX2(Double.parseDouble(centerX) + halfLen, roiCount, shape);

              reader.store.setLineX1(Double.valueOf(centerX), roiCount, shape + 1);
              reader.store.setLineX2(Double.valueOf(centerX), roiCount, shape + 1);
            }
            if (centerY != null) {
              reader.store.setLineY1(Double.valueOf(centerY), roiCount, shape);
              reader.store.setLineY2(Double.valueOf(centerY), roiCount, shape);

              reader.store.setLineY1(Double.parseDouble(centerY) - halfLen, roiCount, shape + 1);
              reader.store.setLineY2(Double.parseDouble(centerY) + halfLen, roiCount, shape + 1);
            }
          }
          reader.store.setLineText(getFirstNodeValue(textElements, "Text"), roiCount, shape);
          reader.store.setLineText(getFirstNodeValue(textElements, "Text"), roiCount, shape + 1);
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

            reader.store.setEllipseID(
                    MetadataTools.createLSID("Shape", roiCount, shape), roiCount, shape);

            String radiusX = getFirstNodeValue(geometry, "RadiusX");
            String radiusY = getFirstNodeValue(geometry, "RadiusY");
            String centerX = getFirstNodeValue(geometry, "CenterX");
            String centerY = getFirstNodeValue(geometry, "CenterY");

            if (radiusX != null) {
              reader.store.setEllipseRadiusX(Double.valueOf(radiusX), roiCount, shape);
            }
            if (radiusY != null) {
              reader.store.setEllipseRadiusY(Double.valueOf(radiusY), roiCount, shape);
            }
            if (centerX != null) {
              reader.store.setEllipseX(Double.valueOf(centerX), roiCount, shape);
            }
            if (centerY != null) {
              reader.store.setEllipseY(Double.valueOf(centerY), roiCount, shape);
            }
            reader.store.setEllipseText(
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
                  reader.store.setPointID(pointID, roiCount, shape);
                  reader.store.setPointX(
                          DataTools.parseDouble(xy[0]), roiCount, shape);
                  reader.store.setPointY(
                          DataTools.parseDouble(xy[1]), roiCount, shape);
                  shape++;
                }
              }
            }
          }
        }

        if (shape > 0) {
          String roiID = MetadataTools.createLSID("ROI", roiCount);
          reader.store.setROIID(roiID, roiCount);
          reader.store.setROIName(layer.getAttribute("Name"), roiCount);
          reader.store.setROIDescription(getFirstNodeValue(layer, "Usage"), roiCount);

          for (int series=0; series<reader.getSeriesCount(); series++) {
            reader.store.setImageROIRef(roiID, series, roiCount);
          }
          roiCount++;
        }
      }
    }

    private int populatePolylines(NodeList polylines, int roi, int shape, boolean closed) {
      for (int s=0; s<polylines.getLength(); s++, shape++) {
        Element polyline = (Element) polylines.item(s);
        Element geometry = getFirstNode(polyline, "Geometry");
        Element textElements = getFirstNode(polyline, "TextElements");
        Element attributes = getFirstNode(polyline, "Attributes");

        String shapeID = MetadataTools.createLSID("Shape", roi, shape);

        if (closed) {
          reader.store.setPolygonID(shapeID, roi, shape);
          reader.store.setPolygonPoints(
                  getFirstNodeValue(geometry, "Points"), roi, shape);
          reader.store.setPolygonText(
                  getFirstNodeValue(textElements, "Text"), roi, shape);
        }
        else {
          reader.store.setPolylineID(shapeID, roi, shape);
          reader.store.setPolylinePoints(
                  getFirstNodeValue(geometry, "Points"), roi, shape);
          reader.store.setPolylineText(
                  getFirstNodeValue(textElements, "Text"), roi, shape);
        }
      }
      return shape;
    }

    private void translateDisplaySettings(Element root) throws FormatException {
      NodeList displaySettings = root.getElementsByTagName("DisplaySetting");
      if (displaySettings.getLength() == 0) {
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

            channels.get(i).name = channel.getAttribute("Name");

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

    private void translateHardwareSettings(Element root) throws FormatException {
      NodeList hardwareSettings = root.getElementsByTagName("HardwareSetting");
      if (hardwareSettings.getLength() == 0) {
        return;
      }

      Element hardware = (Element) hardwareSettings.item(0);

      reader.store.setInstrumentID(MetadataTools.createLSID("Instrument", 0), 0);

      Element microscope = getFirstNode(hardware, "Microscope");
      if (microscope != null) {
        String model = microscope.getAttribute("Name");
        reader.store.setMicroscopeModel(model, 0);
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

        for (int i = 0; i < objectives.getLength(); i++) {
          Element objective = (Element) objectives.item(i);

          String objectiveID = MetadataTools.createLSID("Objective", 0, i);
          if (i == positionIndex ||
                  (objectives.getLength() == 1 && objectiveSettingsID != null)) {
            objectiveSettingsID = objectiveID;
          }

          reader.store.setObjectiveID(objectiveID, 0, i);
          reader.store.setObjectiveModel(objective.getAttribute("Model"), 0, i);
          reader.store.setObjectiveSerialNumber(
                  objective.getAttribute("UniqueName"), 0, i);

          String immersion = getFirstNodeValue(objective, "Immersions");
          reader.store.setObjectiveImmersion(MetadataTools.getImmersion(immersion), 0, i);
          reader.store.setObjectiveCorrection(MetadataTools.getCorrection("Other"), 0, i);

          String magnification = getFirstNodeValue(objective, "Magnification");
          String na = getFirstNodeValue(objective, "NumericalAperture");
          String wd = getFirstNodeValue(objective, "WorkingDistance");

          if (magnification != null) {
            try {
              reader.store.setObjectiveNominalMagnification(
                      Double.valueOf(magnification), 0, i);
            } catch (NumberFormatException e) {
              LOGGER.debug("Could not parse magnification", e);
            }
          }
          if (na != null) {
            try {
              reader.store.setObjectiveLensNA(Double.valueOf(na), 0, i);
            } catch (NumberFormatException e) {
              LOGGER.debug("Could not parse numerical aperture", e);
            }
          }
          if (wd != null) {
            try {
              reader.store.setObjectiveWorkingDistance(new Length(Double.valueOf(wd), UNITS.MICROMETER), 0, i);
            } catch (NumberFormatException e) {
              LOGGER.debug("Could not parse working distance", e);
            }
          }
        }
      }
    }

    private int populateCircles(NodeList circles, int roi, int shape) {
      for (int s=0; s<circles.getLength(); s++, shape++) {
        Element circle = (Element) circles.item(s);
        Element geometry = getFirstNode(circle, "Geometry");
        Element textElements = getFirstNode(circle, "TextElements");
        Element attributes = getFirstNode(circle, "Attributes");

        reader.store.setEllipseID(
                MetadataTools.createLSID("Shape", roi, shape), roi, shape);
        String radius = getFirstNodeValue(geometry, "Radius");
        String centerX = getFirstNodeValue(geometry, "CenterX");
        String centerY = getFirstNodeValue(geometry, "CenterY");

        if (radius != null) {
          reader.store.setEllipseRadiusX(Double.valueOf(radius), roi, shape);
          reader.store.setEllipseRadiusY(Double.valueOf(radius), roi, shape);
        }
        if (centerX != null) {
          reader.store.setEllipseX(Double.valueOf(centerX), roi, shape);
        }
        if (centerY != null) {
          reader.store.setEllipseY(Double.valueOf(centerY), roi, shape);
        }
        reader.store.setEllipseText(getFirstNodeValue(textElements, "Text"), roi, shape);
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
          reader.store.setRectangleID(
                  MetadataTools.createLSID("Shape", roi, shape), roi, shape);
          reader.store.setRectangleX(Double.valueOf(left), roi, shape);
          reader.store.setRectangleY(Double.valueOf(top), roi, shape);
          reader.store.setRectangleWidth(Double.valueOf(width), roi, shape);
          reader.store.setRectangleHeight(Double.valueOf(height), roi, shape);

          String name = getFirstNodeValue(attributes, "Name");
          String label = getFirstNodeValue(textElements, "Text");

          if (label != null) {
            reader.store.setRectangleText(label, roi, shape);
          }
          shape++;
        }
      }
      return shape;
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
            catch (NumberFormatException e) { logger.warn(e.getMessage()); }
            Length yPos = null;
            try {
              yPos = new Length(Double.valueOf(y), UNITS.METRE);
            }
            catch (NumberFormatException e) { logger.warn(e.getMessage()); }
            Length zPos = null;
            try {
              zPos = new Length(Double.valueOf(z), UNITS.METRE);
            }
            catch (NumberFormatException e) { logger.warn(e.getMessage()); }

            int numTiles = tilesX * tilesY;
            for (int tile=0; tile<numTiles; tile++) {
              int index = i * tilesX * tilesY + tile;
              if (groups.getLength() == reader.core.size()) {
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
              if (wells.getLength() == 0) {
                wells = sampleHolder.getElementsByTagName("TileRegion");
              }
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

            NodeList regionArrays = sampleHolder.getElementsByTagName("SingleTileRegionArray");
            int positionIndex = 0;

            allPositionsInformation.regions = new ArrayList<>();

            for (int r=0; r<regionArrays.getLength(); r++) {
              NodeList regions = ((Element) regionArrays.item(r)).getElementsByTagName("SingleTileRegion");
              for (int i = 0; i < regions.getLength(); i++, positionIndex++) {
                Element region = (Element) regions.item(i);

                String x = getFirstNode(region, "X").getTextContent();
                String y = getFirstNode(region, "Y").getTextContent();
                String z = getFirstNode(region, "Z").getTextContent();
                String name = region.getAttribute("Name");

                // safe to assume all 3 arrays have the same length
                //if (positionIndex < positionsX.length) {
                XYZLength loc = new XYZLength();
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

    private void addChannelMetadata(int iSeries, boolean isPALM) {
      reader.setSeries(iSeries);
      for (int c=0; c<reader.getEffectiveSizeC(); c++) {
        if (c < channels.size()) {
          if (isPALM && iSeries < channels.size()) {
            reader.store.setChannelName(channels.get(iSeries).name, iSeries, c);
          }
          else {
            reader.store.setChannelName(channels.get(c).name, iSeries, c);
          }
          reader.store.setChannelFluor(channels.get(c).fluor, iSeries, c);
          if (channels.get(c).filterSetRef != null) {
            reader.store.setChannelFilterSetRef(channels.get(c).filterSetRef, iSeries, c);
          }

          String color = channels.get(c).color;
          if (color != null && !reader.isRGB()) {
            color = normalizeColor(color);
            if (color.length() > 6) {
              color = color.substring(2);
            }
            try {
              // shift by 8 to allow alpha in the final byte
              reader.store.setChannelColor(
                      new Color((Integer.parseInt(color, 16) << 8) | 0xff), iSeries, c);
            }
            catch (NumberFormatException e) {
              LOGGER.warn("", e);
            }
          }

          String emWave = channels.get(c).emission;
          if (emWave != null) {
            Double wave = Double.parseDouble(emWave);
            Length em = FormatTools.getEmissionWavelength(wave);
            if (em != null) {
              reader.store.setChannelEmissionWavelength(em, iSeries, c);
            }
          }
          String exWave = channels.get(c).excitation;
          if (exWave != null) {
            Double wave = Double.valueOf(exWave);
            Length ex = FormatTools.getExcitationWavelength(wave);
            if (ex != null) {
              reader.store.setChannelExcitationWavelength(ex, iSeries, c);
            }
          }

          if (channels.get(c).illumination != null) {
            reader.store.setChannelIlluminationType(
                    channels.get(c).illumination, iSeries, c);
          }

          if (channels.get(c).pinhole != null) {
            reader.store.setChannelPinholeSize(
                    new Length(Double.valueOf(channels.get(c).pinhole), UNITS.MICROMETER), iSeries, c);
          }

          if (channels.get(c).acquisitionMode != null) {
            reader.store.setChannelAcquisitionMode(
                    channels.get(c).acquisitionMode, iSeries, c);
          }
        }

        if (c < detectorRefs.size()) {
          String detector = detectorRefs.get(c);
          reader.store.setDetectorSettingsID(detector, iSeries, c);

          if (c < binnings.size()) {
            try {
              reader.store.setDetectorSettingsBinning(MetadataTools.getBinning(binnings.get(c)), iSeries, c);
            } catch (FormatException e) {
              logger.error(e.getMessage());
              e.printStackTrace();
            }
          }
          if (c < channels.size()) {
            reader.store.setDetectorSettingsGain(channels.get(c).gain, iSeries, c);
          }
        }

        if (c < channels.size()) {
          if (hasDetectorSettings) {
            reader.store.setDetectorSettingsGain(channels.get(c).gain, iSeries, c);
          }
        }
      }
    }

    private void setChannelMetadata() {
      for (int iSeries=0; iSeries<reader.getSeriesCount(); iSeries++) {

        int extraIndex = iSeries - (reader.getSeriesCount() - reader.extraImages.size());
        if (extraIndex >= 0) {
          continue;
        }

        boolean isPALM = false; // TODO
        addChannelMetadata(iSeries, isPALM);
      }
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
          reader.store.setObjectiveID(objective.getAttribute("Id"), 0, i);
          reader.store.setObjectiveManufacturer(manufacturer, 0, i);
          reader.store.setObjectiveModel(model, 0, i);
          reader.store.setObjectiveSerialNumber(serialNumber, 0, i);
          reader.store.setObjectiveLotNumber(lotNumber, 0, i);

          String correction = getFirstNodeValue(objective, "Correction");
          if (correction != null) {
            reader.store.setObjectiveCorrection(MetadataTools.getCorrection(correction), 0, i);
          }
          reader.store.setObjectiveImmersion(
                  MetadataTools.getImmersion(getFirstNodeValue(objective, "Immersion")), 0, i);

          String lensNA = getFirstNodeValue(objective, "LensNA");
          if (lensNA != null) {
            reader.store.setObjectiveLensNA(Double.valueOf(lensNA), 0, i);
          }

          String magnification =
                  getFirstNodeValue(objective, "NominalMagnification");
          if (magnification == null) {
            magnification = getFirstNodeValue(objective, "Magnification");
          }
          Double mag = magnification == null ? null : Double.valueOf(magnification);

          if (mag != null) {
            reader.store.setObjectiveNominalMagnification(mag, 0, i);
          }
          String calibratedMag =
                  getFirstNodeValue(objective, "CalibratedMagnification");
          if (calibratedMag != null) {
            reader.store.setObjectiveCalibratedMagnification(
                    Double.valueOf(calibratedMag), 0, i);
          }
          String wd = getFirstNodeValue(objective, "WorkingDistance");
          if (wd != null) {
            reader.store.setObjectiveWorkingDistance(new Length(Double.valueOf(wd), UNITS.MICROMETER), 0, i);
          }
          String iris = getFirstNodeValue(objective, "Iris");
          if (iris != null) {
            reader.store.setObjectiveIris(Boolean.getBoolean(iris), 0, i);
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

        reader.store.setLineID(
                MetadataTools.createLSID("Shape", roi, shape), roi, shape);

        if (x1 != null) {
          reader.store.setLineX1(Double.valueOf(x1), roi, shape);
        }
        if (x2 != null) {
          reader.store.setLineX2(Double.valueOf(x2), roi, shape);
        }
        if (y1 != null) {
          reader.store.setLineY1(Double.valueOf(y1), roi, shape);
        }
        if (y2 != null) {
          reader.store.setLineY2(Double.valueOf(y2), roi, shape);
        }
        reader.store.setLineText(getFirstNodeValue(textElements, "Text"), roi, shape);
      }
      return shape;
    }

    private void setModuloLabels(CoreMetadata ms0) {
      if (rotationLabels != null) {
        ms0.moduloZ.labels = rotationLabels;
        ms0.moduloZ.end = ms0.moduloZ.start; // TODO: understand the role of this line...
      }
      if (illuminationLabels != null) {
        ms0.moduloC.labels = illuminationLabels;
        ms0.moduloC.end = ms0.moduloC.start; // TODO: understand the role of this line...
      }
      if (phaseLabels != null) {
        ms0.moduloT.labels = phaseLabels;
        ms0.moduloT.end = ms0.moduloT.start; // TODO: understand the role of this line...
      }
    }

        /*private void setExtraImagesSpatialInformation(){
            for (int iCoreIndex=0; iCoreIndex<core.size(); iCoreIndex++) {
                int extraIndex = getCoreIndex() - (core.size() - extraImages.size());
                if (extraIndex >= 0) {

                    int iSeries = coreIndexToSeries(iCoreIndex);
                    if (extraIndex == 0) {
                        // Label Image
                        if (allPositionsInformation.labelPixelSize!=null) {
                            store.setPixelsPhysicalSizeX(allPositionsInformation.labelPixelSize.pX,iSeries);
                            store.setPixelsPhysicalSizeY(allPositionsInformation.labelPixelSize.pY,iSeries);
                            store.setPixelsPhysicalSizeZ(allPositionsInformation.labelPixelSize.pZ,iSeries);
                        }
                        if (allPositionsInformation.labelLocation!=null) {
                            store.setPlanePositionX(allPositionsInformation.labelLocation.pX,iSeries,0);
                            store.setPlanePositionY(allPositionsInformation.labelLocation.pY,iSeries,0);
                            store.setPlanePositionZ(allPositionsInformation.labelLocation.pZ,iSeries,0);
                        }
                    } else if (extraIndex == 1) {
                        // Macro Image
                        if (allPositionsInformation.slidePreviewPixelSize!=null) {
                            store.setPixelsPhysicalSizeX(allPositionsInformation.slidePreviewPixelSize.pX,iSeries);
                            store.setPixelsPhysicalSizeY(allPositionsInformation.slidePreviewPixelSize.pY,iSeries);
                            store.setPixelsPhysicalSizeZ(allPositionsInformation.slidePreviewPixelSize.pZ,iSeries);
                        }
                        if (allPositionsInformation.slidePreviewLocation!=null) {
                            store.setPlanePositionX(allPositionsInformation.slidePreviewLocation.pX,iSeries,0);
                            store.setPlanePositionY(allPositionsInformation.slidePreviewLocation.pY,iSeries,0);
                            store.setPlanePositionZ(allPositionsInformation.slidePreviewLocation.pZ,iSeries,0);
                        }
                    }
                }
            }
        }*/

    private void populateOriginalMetadata(Element root, Deque<String> nameStack) {
      String name = root.getNodeName();
      nameStack.push(name);

      final StringBuilder key = new StringBuilder();
      String k;
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
            reader.addGlobalMeta(s, value);
          }
          else {
            reader.addGlobalMetaList(s, value);
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
          reader.addGlobalMeta(keyString + attrName, attrValue);
        }
        else {
          reader.addGlobalMetaList(keyString + attrName, attrValue);
        }
      }

      NodeList children = root.getChildNodes();

      for (int i=0; i<children.getLength(); i++) {
        Object child = children.item(i);
        if (child instanceof Element) {
          populateOriginalMetadata((Element) child, nameStack);
        }
      }

      nameStack.pop();
    }

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

    private static class SceneProperties {
      final List<XYZLength> pos = new ArrayList<>();
      String name;

      public Length getMinPosXInMicrons() {
        if (pos.size()==0) {
          return null;//new Length(0, UNITS.MICROMETER);
        }
        double minX = Double.MAX_VALUE;
        for (XYZLength positionLocation : pos) {
          if ((positionLocation.pX==null)||(positionLocation.pX.value()==null)) {
            return null;
          }
          if (positionLocation.pX.value().doubleValue()<minX) {
            minX = positionLocation.pX.value().doubleValue();
          }
        }
        return new Length(minX, pos.get(0).pX.unit());
      }
      public Length getMinPosYInMicrons() {
        if (pos.size()==0) {
          return null;//new Length(0, UNITS.MICROMETER);
        }
        double minY = Double.MAX_VALUE;
        for (XYZLength positionLocation : pos) {
          if ((positionLocation.pY==null)||(positionLocation.pY.value()==null)) {
            return null;
          }
          if (positionLocation.pY.value().doubleValue()<minY) {
            minY = positionLocation.pY.value().doubleValue();
          }
        }
        return new Length(minY, pos.get(0).pY.unit());
      }

      public Length getMinPosZInMicrons() {
        if (pos.size()==0) {
          return null;//new Length(0, UNITS.MICROMETER);
        }
        double minZ = Double.MAX_VALUE;

        for (XYZLength positionLocation : pos) {
          if ((positionLocation.pZ==null)||(positionLocation.pZ.value()==null)) {
            return null;
          }
          if (positionLocation.pZ.value().doubleValue()<minZ) {
            minZ = positionLocation.pZ.value().doubleValue();
          }
        }
        return new Length(minZ, pos.get(0).pZ.unit());
      }
    }

    private static class GroupProperties {
      final List<TileProperties> tiles = new ArrayList<>();

      int nTilesX, nTilesY;
    }

    private static class TileProperties {
      final XYZLength pos = new XYZLength();
      Integer iX;
      Integer iY;
    }

    private static class XYZLength {
      Length pX, pY, pZ;
    }

    private static class AllPositionsInformation {
      /**
       * We may have, group, tiles, or scenes
       */
      List<SceneProperties> scenes;
      List<GroupProperties> groups;
      //List<TileProperties> tiles;

      List<XYZLength> regions;

      XYZLength slidePreviewPixelSize, slidePreviewLocation, labelPixelSize, labelLocation;

    }

    private boolean isLatticeLightSheet = false;
    private void setLatticeLightSheet(boolean flag) {
      isLatticeLightSheet = flag;
    }

    private boolean isLatticeLightSheet() {
      return isLatticeLightSheet;
    }

  }

  static private String normalizeColor(String color) {
    String c = color.replaceAll("#", "");
    if (c.length() > 6) {
      c = c.substring(2, (int) Math.min(8, c.length()));
      LOGGER.debug("Replaced color {} with {}", color, c);
    }
    return c;
  }

}
