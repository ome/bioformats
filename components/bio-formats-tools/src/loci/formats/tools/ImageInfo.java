/*
 * #%L
 * Bio-Formats command line tools for reading and converting files
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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

package loci.formats.tools;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import java.util.Hashtable;
import java.util.StringTokenizer;

import loci.common.ByteArrayHandle;
import loci.common.DataTools;
import loci.common.DebugTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.common.xml.XMLTools;
import loci.formats.ChannelFiller;
import loci.formats.ChannelMerger;
import loci.formats.ChannelSeparator;
import loci.formats.DimensionSwapper;
import loci.formats.FilePattern;
import loci.formats.FileStitcher;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.ImageTools;
import loci.formats.Memoizer;
import loci.formats.MetadataTools;
import loci.formats.MinMaxCalculator;
import loci.formats.MissingLibraryException;
import loci.formats.Modulo;
import loci.formats.gui.AWTImageTools;
import loci.formats.gui.BufferedImageReader;
import loci.formats.gui.ImageViewer;
import loci.formats.in.DefaultMetadataOptions;
import loci.formats.in.MetadataLevel;
import loci.formats.in.MetadataOptions;
import loci.formats.in.OMETiffReader;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.formats.services.OMEXMLService;
import loci.formats.services.OMEXMLServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

/**
 * ImageInfo is a utility class for reading a file
 * and reporting information about it.
 */
public class ImageInfo {

  // -- Constants --

  private static final Logger LOGGER = LoggerFactory.getLogger(ImageInfo.class);
  private static final String NEWLINE = System.getProperty("line.separator");

  private static final ImmutableSet<String> HELP_ARGUMENTS =
      ImmutableSet.of("-h", "-help", "--help");

  // -- Fields --

  private String id = null;
  private boolean printVersion = false;
  private boolean pixels = true;
  private boolean doCore = true;
  private boolean doMeta = true;
  private boolean filter = true;
  private boolean thumbs = false;
  private boolean minmax = false;
  private boolean merge = false;
  private boolean stitch = false;
  private boolean group = true;
  private boolean separate = false;
  private boolean expand = false;
  private boolean omexml = false;
  private boolean cache = false;
  private boolean originalMetadata = true;
  private boolean normalize = false;
  private boolean fastBlit = false;
  private boolean autoscale = false;
  private boolean preload = false;
  private boolean ascii = false;
  private boolean usedFiles = true;
  private boolean omexmlOnly = false;
  private boolean validate = true;
  private boolean flat = true;
  private String omexmlVersion = null;
  private int start = 0;
  private int end = Integer.MAX_VALUE;
  private int series = 0;
  private int resolution = 0;
  private int xCoordinate = 0, yCoordinate = 0, width = 0, height = 0;
  private String swapOrder = null, shuffleOrder = null;
  private String map = null;
  private String format = null;
  private String cachedir = null;
  private int xmlSpaces = 3;

  private IFormatReader reader;
  private IFormatReader baseReader;
  private MinMaxCalculator minMaxCalc;
  private DimensionSwapper dimSwapper;
  private BufferedImageReader biReader;

  private Double[] preGlobalMin = null, preGlobalMax = null;
  private Double[] preKnownMin = null, preKnownMax = null;
  private Double[] prePlaneMin = null, prePlaneMax = null;
  private boolean preIsMinMaxPop = false;

  // -- ImageInfo methods --

  public boolean parseArgs(String[] args) {
    id = null;
    printVersion = false;
    pixels = true;
    doCore = true;
    doMeta = true;
    filter = true;
    thumbs = false;
    minmax = false;
    merge = false;
    stitch = false;
    group = true;
    separate = false;
    expand = false;
    omexml = false;
    cache = false;
    originalMetadata = true;
    normalize = false;
    fastBlit = false;
    autoscale = false;
    preload = false;
    usedFiles = true;
    omexmlOnly = false;
    validate = true;
    flat = true;
    omexmlVersion = null;
    xmlSpaces = 3;
    start = 0;
    end = Integer.MAX_VALUE;
    series = 0;
    resolution = 0;
    xCoordinate = 0;
    yCoordinate = 0;
    width = 0;
    height = 0;
    swapOrder = null;
    shuffleOrder = null;
    map = null;
    cachedir = null;
    if (args == null) return false;
    for (int i=0; i<args.length; i++) {
      if (args[i].startsWith("-")) {
        if (args[i].equals(CommandLineTools.VERSION)){
          printVersion = true;
          return true;
        }
        else if (args[i].equals("-nopix")) pixels = false;
        else if (args[i].equals("-nocore")) doCore = false;
        else if (args[i].equals("-nometa")) doMeta = false;
        else if (args[i].equals("-nofilter")) filter = false;
        else if (args[i].equals("-thumbs")) thumbs = true;
        else if (args[i].equals("-minmax")) minmax = true;
        else if (args[i].equals("-merge")) merge = true;
        else if (args[i].equals("-stitch")) stitch = true;
        else if (args[i].equals("-nogroup")) group = false;
        else if (args[i].equals("-separate")) separate = true;
        else if (args[i].equals("-expand")) expand = true;
        else if (args[i].equals("-cache")) cache = true;
        else if (args[i].equals("-omexml")) omexml = true;
        else if (args[i].equals("-no-sas")) originalMetadata = false;
        else if (args[i].equals("-normalize")) normalize = true;
        else if (args[i].equals("-fast")) fastBlit = true;
        else if (args[i].equals("-autoscale")) {
          fastBlit = true;
          autoscale = true;
        }
        else if (args[i].equals("-novalid")) validate = false;
        else if (args[i].equals("-noflat")) flat = false;
        else if (args[i].equals("-debug")) {
          DebugTools.setRootLevel("DEBUG");
        }
        else if (args[i].equals("-trace")) {
          DebugTools.setRootLevel("TRACE");
        }
        else if (args[i].equals("-omexml-only")) {
          omexmlOnly = true;
          omexml = true;
          DebugTools.setRootLevel("OFF");
        }
        else if (args[i].equals("-preload")) preload = true;
        else if (args[i].equals("-ascii")) ascii = true;
        else if (args[i].equals("-nousedfiles")) usedFiles = false;
        else if (args[i].equals("-xmlversion")) omexmlVersion = args[++i];
        else if (args[i].equals("-xmlspaces")) {
          xmlSpaces = Integer.parseInt(args[++i]);
        }
        else if (args[i].equals("-crop")) {
          StringTokenizer st = new StringTokenizer(args[++i], ",");
          xCoordinate = Integer.parseInt(st.nextToken());
          yCoordinate = Integer.parseInt(st.nextToken());
          width = Integer.parseInt(st.nextToken());
          height = Integer.parseInt(st.nextToken());
        }
        else if (args[i].equals("-range")) {
          try {
            start = Integer.parseInt(args[++i]);
            end = Integer.parseInt(args[++i]);
          }
          catch (NumberFormatException exc) { }
        }
        else if (args[i].equals("-series")) {
          try {
            series = Integer.parseInt(args[++i]);
          }
          catch (NumberFormatException exc) { }
        }
        else if (args[i].equals("-resolution")) {
          try {
            resolution = Integer.parseInt(args[++i]);
          }
          catch (NumberFormatException exc) { }
        }
        else if (args[i].equals("-swap")) {
          swapOrder = args[++i].toUpperCase();
        }
        else if (args[i].equals("-shuffle")) {
          shuffleOrder = args[++i].toUpperCase();
        }
        else if (args[i].equals("-map")) map = args[++i];
        else if (args[i].equals("-format")) format = args[++i];
        else if (args[i].equals("-cache-dir")) {
            cache = true;
            cachedir = args[++i];
        }
        else if (!args[i].equals(CommandLineTools.NO_UPGRADE_CHECK)) {
          LOGGER.error("Found unknown command flag: {}; exiting.", args[i]);
          return false;
        }
      }
      else {
        if (id == null) id = args[i];
        else {
          LOGGER.error("Found unknown argument: {}; exiting.", args[i]);
          return false;
        }
      }
    }
    return true;
  }

  public void printUsage() {
    String fmt = reader instanceof ImageReader ? "any" : reader.getFormat();
    String[] s = {
      "To test read a file in " + fmt + " format, run:",
      "  showinf file [-nopix] [-nocore] [-nometa] [-thumbs] [-minmax] ",
      "    [-merge] [-nogroup] [-stitch] [-separate] [-expand] [-omexml]",
      "    [-normalize] [-fast] [-debug] [-range start end] [-series num]",
      "    [-resolution num] [-swap inputOrder] [-shuffle outputOrder]",
      "    [-map id] [-preload] [-crop x,y,w,h] [-autoscale] [-novalid]",
      "    [-omexml-only] [-no-sas] [-no-upgrade] [-noflat] [-format Format]",
      "    [-cache] [-cache-dir dir]",
      "",
      "    -version: print the library version and exit",
      "        file: the image file to read",
      "      -nopix: read metadata only, not pixels",
      "     -nocore: do not output core metadata",
      "     -nometa: do not parse format-specific metadata table",
      "   -nofilter: do not filter metadata fields",
      "     -thumbs: read thumbnails instead of normal pixels",
      "     -minmax: compute min/max statistics",
      "      -merge: combine separate channels into RGB image",
      "    -nogroup: force multi-file datasets to be read as individual files",
      "     -stitch: stitch files with similar names",
      "   -separate: split RGB image into separate channels",
      "     -expand: expand indexed color to RGB",
      "     -omexml: populate OME-XML metadata",
      "  -normalize: normalize floating point images (*)",
      "       -fast: paint RGB images as quickly as possible (*)",
      "      -debug: turn on debugging output",
      "      -range: specify range of planes to read (inclusive)",
      "     -series: specify which image series to read",
      "     -noflat: do not flatten subresolutions",
      " -resolution: used in combination with -noflat to specify which",
      "              subresolution to read (for images with subresolutions)",
      "       -swap: override the default input dimension order",
      "    -shuffle: override the default output dimension order",
      "        -map: specify file on disk to which name should be mapped",
      "    -preload: pre-read entire file into a buffer; significantly",
      "              reduces the time required to read the images, but",
      "              requires more memory",
      "       -crop: crop images before displaying; argument is 'x,y,w,h'",
      "  -autoscale: automatically adjust brightness and contrast (*)",
      "    -novalid: do not perform validation of OME-XML",
      "-omexml-only: only output the generated OME-XML",
      "     -no-sas: do not output OME-XML StructuredAnnotation elements",
      " -no-upgrade: do not perform the upgrade check",
      "     -format: read file with a particular reader (e.g., ZeissZVI)",
      "      -cache: cache the initialized reader",
      "  -cache-dir: use the specified directory to store the cached",
      "              initialized reader. If unspecified, the cached reader",
      "              will be stored under the same folder as the image file",
      "",
      "* = may result in loss of precision",
      ""
    };
    for (int i=0; i<s.length; i++) LOGGER.info(s[i]);
  }

  public void setReader(IFormatReader reader) {
    this.reader = reader;
  }

  public void createReader() {
    if (reader != null) return; // reader was set programmatically
    if (format != null) {
      // create reader of a specific format type
      try {
        Class<?> c = Class.forName("loci.formats.in." + format + "Reader");
        reader = (IFormatReader) c.newInstance();
      }
      catch (ClassNotFoundException exc) {
        LOGGER.warn("Unknown reader: {}", format);
        LOGGER.debug("", exc);
      }
      catch (InstantiationException exc) {
        LOGGER.warn("Cannot instantiate reader: {}", format);
        LOGGER.debug("", exc);
      }
      catch (IllegalAccessException exc) {
        LOGGER.warn("Cannot access reader: {}", format);
        LOGGER.debug("", exc);
      }
    }
    if (reader == null) reader = new ImageReader();
    baseReader = reader;
  }

  public void mapLocation() throws IOException {
    if (map != null) Location.mapId(id, map);
    else if (preload) {
      RandomAccessInputStream f = new RandomAccessInputStream(id);
      if (!(reader instanceof ImageReader)) {
        // verify format
        LOGGER.info("Checking {} format [{}]", reader.getFormat(),
                    reader.isThisType(f) ? "yes" : "no");
        f.seek(0);
      }
      int len = (int) f.length();
      LOGGER.info("Caching {} bytes:", len);
      byte[] b = new byte[len];
      int blockSize = 8 * 1024 * 1024; // 8 MB
      int read = 0, left = len;
      while (left > 0) {
        int r = f.read(b, read, blockSize < left ? blockSize : left);
        read += r;
        left -= r;
        float ratio = (float) read / len;
        int p = (int) (100 * ratio);
        LOGGER.info("\tRead {} bytes ({}% complete)", read, p);
      }
      f.close();
      ByteArrayHandle file = new ByteArrayHandle(b);
      Location.mapFile(id, file);
    }
  }

  public void configureReaderPreInit() throws FormatException, IOException {
    if (omexml) {
      reader.setOriginalMetadataPopulated(originalMetadata);
      try {
        ServiceFactory factory = new ServiceFactory();
        OMEXMLService service = factory.getInstance(OMEXMLService.class);
        reader.setMetadataStore(
            service.createOMEXMLMetadata(null, omexmlVersion));
      }
      catch (DependencyException de) {
        throw new MissingLibraryException(OMEXMLServiceImpl.NO_OME_XML_MSG, de);
      }
      catch (ServiceException se) {
        throw new FormatException(se);
      }
    }

    // check file format
    if (reader instanceof ImageReader) {
      // determine format
      ImageReader ir = (ImageReader) reader;
      if (new Location(id).exists()) {
        LOGGER.info("Checking file format [{}]", ir.getFormat(id));
      }
    }
    else {
      // verify format
      LOGGER.info("Checking {} format [{}]", reader.getFormat(),
        reader.isThisType(id) ? "yes" : "no");
    }

    LOGGER.info("Initializing reader");
    if (stitch) {
      reader = new FileStitcher(reader, true);
      Location f = new Location(id);
      String pat = null;
      if (!f.exists()) {
        ((FileStitcher) reader).setUsingPatternIds(true);
        pat = id;
      }
      else {
        pat = FilePattern.findPattern(f);
      }
      if (pat != null) id = pat;
    }
    if (expand) reader = new ChannelFiller(reader);
    if (separate) reader = new ChannelSeparator(reader);
    if (merge) reader = new ChannelMerger(reader);
    if (cache) {
      if (cachedir != null) {
        reader  = new Memoizer(reader, 0, new File(cachedir));
      } else {
        reader = new Memoizer(reader, 0);
      }
    }
    minMaxCalc = null;
    if (minmax || autoscale) reader = minMaxCalc = new MinMaxCalculator(reader);
    dimSwapper = null;
    if (swapOrder != null || shuffleOrder != null) {
      reader = dimSwapper = new DimensionSwapper(reader);
    }
    reader = biReader = new BufferedImageReader(reader);

    reader.close();
    reader.setNormalized(normalize);
    reader.setMetadataFiltered(filter);
    reader.setGroupFiles(group);
    MetadataOptions metaOptions = new DefaultMetadataOptions(doMeta ?
      MetadataLevel.ALL : MetadataLevel.MINIMUM);
    reader.setMetadataOptions(metaOptions);
    reader.setFlattenedResolutions(flat);
  }

  public void configureReaderPostInit() {
    if (swapOrder != null) dimSwapper.swapDimensions(swapOrder);
    if (shuffleOrder != null) dimSwapper.setOutputOrder(shuffleOrder);
  }

  public void checkWarnings() {
    if (!normalize && (reader.getPixelType() == FormatTools.FLOAT ||
      reader.getPixelType() == FormatTools.DOUBLE))
    {
      LOGGER.warn("");
      LOGGER.warn("Java does not support " +
        "display of unnormalized floating point data.");
      LOGGER.warn("Please use the '-normalize' option " +
        "to avoid receiving a cryptic exception.");
    }

    if (reader.isRGB() && reader.getRGBChannelCount() > 4) {
      LOGGER.warn("");
      LOGGER.warn("Java does not support merging more than 4 channels.");
      LOGGER.warn("Please use the '-separate' option " +
        "to avoid losing channels beyond the 4th.");
    }
  }

  public void readCoreMetadata() throws FormatException, IOException {
    if (!doCore) return; // skip core metadata printout

    // read basic metadata
    LOGGER.info("");
    LOGGER.info("Reading core metadata");
    LOGGER.info("{} = {}", stitch ? "File pattern" : "filename",
      stitch ? id : reader.getCurrentFile());
    if (map != null) LOGGER.info("Mapped filename = {}", map);
    if (usedFiles) {
      String[] used = reader.getUsedFiles();
      boolean usedValid = used != null && used.length > 0;
      if (usedValid) {
        for (int u=0; u<used.length; u++) {
          if (used[u] == null) {
            usedValid = false;
            break;
          }
        }
      }
      if (!usedValid) {
        LOGGER.warn("************ invalid used files list ************");
      }
      if (used == null) {
        LOGGER.info("Used files = null");
      }
      else if (used.length == 0) {
       LOGGER.info("Used files = []");
      }
      else if (used.length > 1) {
        LOGGER.info("Used files:");
        for (int u=0; u<used.length; u++) LOGGER.info("\t{}", used[u]);
      }
      else if (!id.equals(used[0])) {
        LOGGER.info("Used files = [{}]", used[0]);
      }
    }
    int seriesCount = reader.getSeriesCount();
    LOGGER.info("Series count = {}", seriesCount);
    MetadataStore ms = reader.getMetadataStore();
    MetadataRetrieve mr = ms instanceof MetadataRetrieve ?
      (MetadataRetrieve) ms : null;
    for (int j=0; j<seriesCount; j++) {
      reader.setSeries(j);

      // read basic metadata for series #i
      int imageCount = reader.getImageCount();
      int resolutions = reader.getResolutionCount();
      boolean rgb = reader.isRGB();
      int sizeX = reader.getSizeX();
      int sizeY = reader.getSizeY();
      int sizeZ = reader.getSizeZ();
      int sizeC = reader.getSizeC();
      int sizeT = reader.getSizeT();
      int pixelType = reader.getPixelType();
      int validBits = reader.getBitsPerPixel();
      int effSizeC = reader.getEffectiveSizeC();
      int rgbChanCount = reader.getRGBChannelCount();
      boolean indexed = reader.isIndexed();
      boolean falseColor = reader.isFalseColor();
      byte[][] table8 = reader.get8BitLookupTable();
      short[][] table16 = reader.get16BitLookupTable();
      Modulo moduloZ = reader.getModuloZ();
      Modulo moduloC = reader.getModuloC();
      Modulo moduloT = reader.getModuloT();
      int thumbSizeX = reader.getThumbSizeX();
      int thumbSizeY = reader.getThumbSizeY();
      boolean little = reader.isLittleEndian();
      String dimOrder = reader.getDimensionOrder();
      boolean orderCertain = reader.isOrderCertain();
      boolean thumbnail = reader.isThumbnailSeries();
      boolean interleaved = reader.isInterleaved();
      boolean metadataComplete = reader.isMetadataComplete();

      // output basic metadata for series #i
      String seriesName = mr == null ? null : mr.getImageName(j);
      LOGGER.info("Series #{}{}{}:",
        new Object[] {j, seriesName == null ? " " : " -- ",
        seriesName == null ? "" : seriesName});

      if (flat == false && resolutions > 1) {
        LOGGER.info("\tResolutions = {}", resolutions);
        for (int i = 0; i < resolutions; i++) {
          reader.setResolution(i);
          LOGGER.info("\t\tsizeX[{}] = {}", i, reader.getSizeX());
        }
        reader.setResolution(0);
      }
      LOGGER.info("\tImage count = {}", imageCount);
      LOGGER.info("\tRGB = {} ({}) {}", new Object[] {rgb, rgbChanCount,
        merge ? "(merged)" : separate ? "(separated)" : ""});
      if (rgb != (rgbChanCount != 1)) {
        LOGGER.warn("\t************ RGB mismatch ************");
      }
      LOGGER.info("\tInterleaved = {}", interleaved);

      StringBuilder sb = new StringBuilder();
      sb.append("\tIndexed = ");
      sb.append(indexed);
      sb.append(" (");
      sb.append(!falseColor);
      sb.append(" color");
      if (table8 != null) {
        sb.append(", 8-bit LUT: ");
        sb.append(table8.length);
        sb.append(" x ");
        sb.append(table8[0] == null ? "null" : "" + table8[0].length);
      }
      if (table16 != null) {
        sb.append(", 16-bit LUT: ");
        sb.append(table16.length);
        sb.append(" x ");
        sb.append(table16[0] == null ? "null" : "" + table16[0].length);
      }
      sb.append(")");
      LOGGER.info(sb.toString());

      if (table8 != null && table16 != null) {
        LOGGER.warn("\t************ multiple LUTs ************");
      }
      LOGGER.info("\tWidth = {}", sizeX);
      LOGGER.info("\tHeight = {}", sizeY);

      printDimension("SizeZ", sizeZ, sizeZ, moduloZ);
      printDimension("SizeT", sizeT, sizeT, moduloT);
      printDimension("SizeC", sizeC, effSizeC, moduloC);

      if (imageCount != sizeZ * effSizeC * sizeT) {
        LOGGER.info("\t************ ZCT mismatch ************");
      }
      LOGGER.info("\tThumbnail size = {} x {}", thumbSizeX, thumbSizeY);
      LOGGER.info("\tEndianness = {}",
        little ? "intel (little)" : "motorola (big)");
      LOGGER.info("\tDimension order = {} ({})", dimOrder,
        orderCertain ? "certain" : "uncertain");
      LOGGER.info("\tPixel type = {}",
        FormatTools.getPixelTypeString(pixelType));
      LOGGER.info("\tValid bits per pixel = {}", validBits);
      LOGGER.info("\tMetadata complete = {}", metadataComplete);
      LOGGER.info("\tThumbnail series = {}", thumbnail);
      if (doMeta) {
        LOGGER.info("\t-----");
        int[] indices;
        if (imageCount > 6) {
          int q = imageCount / 2;
          indices = new int[] {
            0, q - 2, q - 1, q, q + 1, q + 2, imageCount - 1
          };
        }
        else if (imageCount > 2) {
          indices = new int[] {0, imageCount / 2, imageCount - 1};
        }
        else if (imageCount > 1) indices = new int[] {0, 1};
        else indices = new int[] {0};
        int[][] zct = new int[indices.length][];
        int[] indices2 = new int[indices.length];

        sb.setLength(0);
        for (int i=0; i<indices.length; i++) {
          zct[i] = reader.getZCTCoords(indices[i]);
          indices2[i] = reader.getIndex(zct[i][0], zct[i][1], zct[i][2]);
          sb.append("\tPlane #");
          sb.append(indices[i]);
          sb.append(" <=> Z ");
          sb.append(zct[i][0]);
          sb.append(", C ");
          sb.append(zct[i][1]);
          sb.append(", T ");
          sb.append(zct[i][2]);
          if (indices[i] != indices2[i]) {
            sb.append(" [mismatch: ");
            sb.append(indices2[i]);
            sb.append("]");
            sb.append(NEWLINE);
          }
          else sb.append(NEWLINE);
        }
        LOGGER.info(sb.toString());
      }
    }
  }

  public void initPreMinMaxValues() throws FormatException, IOException {
    // get a priori min/max values
    preGlobalMin = preGlobalMax = null;
    preKnownMin = preKnownMax = null;
    prePlaneMin = prePlaneMax = null;
    preIsMinMaxPop = false;
    if (minmax) {
      int sizeC = reader.getSizeC();
      preGlobalMin = new Double[sizeC];
      preGlobalMax = new Double[sizeC];
      preKnownMin = new Double[sizeC];
      preKnownMax = new Double[sizeC];
      for (int c=0; c<sizeC; c++) {
        preGlobalMin[c] = minMaxCalc.getChannelGlobalMinimum(c);
        preGlobalMax[c] = minMaxCalc.getChannelGlobalMaximum(c);
        preKnownMin[c] = minMaxCalc.getChannelKnownMinimum(c);
        preKnownMax[c] = minMaxCalc.getChannelKnownMaximum(c);
      }
      prePlaneMin = minMaxCalc.getPlaneMinimum(0);
      prePlaneMax = minMaxCalc.getPlaneMaximum(0);
      preIsMinMaxPop = minMaxCalc.isMinMaxPopulated();
    }
  }

  public void printMinMaxValues() throws FormatException, IOException {
    // get computed min/max values
    int sizeC = reader.getSizeC();
    Double[] globalMin = new Double[sizeC];
    Double[] globalMax = new Double[sizeC];
    Double[] knownMin = new Double[sizeC];
    Double[] knownMax = new Double[sizeC];
    for (int c=0; c<sizeC; c++) {
      globalMin[c] = minMaxCalc.getChannelGlobalMinimum(c);
      globalMax[c] = minMaxCalc.getChannelGlobalMaximum(c);
      knownMin[c] = minMaxCalc.getChannelKnownMinimum(c);
      knownMax[c] = minMaxCalc.getChannelKnownMaximum(c);
    }
    Double[] planeMin = minMaxCalc.getPlaneMinimum(0);
    Double[] planeMax = minMaxCalc.getPlaneMaximum(0);
    boolean isMinMaxPop = minMaxCalc.isMinMaxPopulated();

    // output min/max results
    LOGGER.info("");
    LOGGER.info("Min/max values:");
    for (int c=0; c<sizeC; c++) {
      LOGGER.info("\tChannel {}:", c);
      LOGGER.info("\t\tGlobal minimum = {} (initially {})",
        globalMin[c], preGlobalMin[c]);
      LOGGER.info("\t\tGlobal maximum = {} (initially {})",
        globalMax[c], preGlobalMax[c]);
      LOGGER.info("\t\tKnown minimum = {} (initially {})",
        knownMin[c], preKnownMin[c]);
      LOGGER.info("\t\tKnown maximum = {} (initially {})",
        knownMax[c], preKnownMax[c]);
    }
    StringBuilder sb = new StringBuilder();
    sb.append("\tFirst plane minimum(s) =");
    if (planeMin == null) sb.append(" none");
    else {
      for (int subC=0; subC<planeMin.length; subC++) {
        sb.append(" ");
        sb.append(planeMin[subC]);
      }
    }
    sb.append(" (initially");
    if (prePlaneMin == null) sb.append(" none");
    else {
      for (int subC=0; subC<prePlaneMin.length; subC++) {
        sb.append(" ");
        sb.append(prePlaneMin[subC]);
      }
    }
    sb.append(")");
    LOGGER.info(sb.toString());

    sb.setLength(0);
    sb.append("\tFirst plane maximum(s) =");
    if (planeMax == null) sb.append(" none");
    else {
      for (int subC=0; subC<planeMax.length; subC++) {
        sb.append(" ");
        sb.append(planeMax[subC]);
      }
    }
    sb.append(" (initially");
    if (prePlaneMax == null) sb.append(" none");
    else {
      for (int subC=0; subC<prePlaneMax.length; subC++) {
        sb.append(" ");
        sb.append(prePlaneMax[subC]);
      }
    }
    sb.append(")");
    LOGGER.info(sb.toString());

    LOGGER.info("\tMin/max populated = {} (initially {})",
      isMinMaxPop, preIsMinMaxPop);
  }

  public void readPixels() throws FormatException, IOException {
    String seriesLabel = reader.getSeriesCount() > 1 ?
      (" series #" + series) : "";
    LOGGER.info("");

    int num = reader.getImageCount();
    if (start < 0) start = 0;
    if (start >= num) start = num - 1;
    if (end < 0) end = 0;
    if (end >= num) end = num - 1;
    if (end < start) end = start;

    LOGGER.info("Reading{} pixel data ({}-{})",
      new Object[] {seriesLabel, start, end});

    int sizeX = reader.getSizeX();
    int sizeY = reader.getSizeY();
    if (width == 0) width = sizeX;
    if (height == 0) height = sizeY;

    int pixelType = reader.getPixelType();

    BufferedImage[] images = new BufferedImage[end - start + 1];
    long s = System.currentTimeMillis();
    long timeLastLogged = s;
    for (int i=start; i<=end; i++) {
      if (!fastBlit) {
        images[i - start] = thumbs ? biReader.openThumbImage(i) :
          biReader.openImage(i, xCoordinate, yCoordinate, width, height);
      }
      else {
        byte[] b = thumbs ? reader.openThumbBytes(i) :
          reader.openBytes(i, xCoordinate, yCoordinate, width, height);
        Object pix = DataTools.makeDataArray(b,
          FormatTools.getBytesPerPixel(pixelType),
          FormatTools.isFloatingPoint(pixelType),
          reader.isLittleEndian());
        Double min = null, max = null;

        if (autoscale) {
          Double[] planeMin = minMaxCalc.getPlaneMinimum(i);
          Double[] planeMax = minMaxCalc.getPlaneMaximum(i);
          if (planeMin != null && planeMax != null) {
            min = planeMin[0];
            max = planeMax[0];
            for (int j=1; j<planeMin.length; j++) {
              if (planeMin[j].doubleValue() < min.doubleValue()) {
                min = planeMin[j];
              }
              if (planeMax[j].doubleValue() > max.doubleValue()) {
                max = planeMax[j];
              }
            }
          }
        }
        else if (normalize) {
          min = Double.valueOf(0);
          max = Double.valueOf(1);
        }

        if (normalize) {
          if (pix instanceof float[]) {
            pix = DataTools.normalizeFloats((float[]) pix);
          }
          else if (pix instanceof double[]) {
            pix = DataTools.normalizeDoubles((double[]) pix);
          }
        }
        images[i - start] = AWTImageTools.makeImage(ImageTools.make24Bits(pix,
          sizeX, sizeY, reader.isInterleaved(), false, min, max),
          sizeX, sizeY, FormatTools.isSigned(pixelType));
      }
      if (images[i - start] == null) {
        LOGGER.warn("\t************ Failed to read plane #{} ************", i);
      }
      if (reader.isIndexed() && reader.get8BitLookupTable() == null &&
        reader.get16BitLookupTable() == null)
      {
        LOGGER.warn("\t************ no LUT for plane #{} ************", i);
      }

      // check for pixel type mismatch
      int pixType = AWTImageTools.getPixelType(images[i - start]);
      if (pixType != pixelType && pixType != pixelType + 1 && !fastBlit) {
        LOGGER.info("\tPlane #{}: pixel type mismatch: {}/{}",
          new Object[] {i, FormatTools.getPixelTypeString(pixType),
          FormatTools.getPixelTypeString(pixelType)});
      }
      else {
        // log number of planes read every second or so
        long t = System.currentTimeMillis();
        if (i == end || (t - timeLastLogged) / 1000 > 0) {
          int current = i - start + 1;
          int total = end - start + 1;
          int percent = 100 * current / total;
          LOGGER.info("\tRead {}/{} planes ({}%)", new Object[] {
            current, total, percent
          });
          timeLastLogged = t;
        }
      }
    }
    long e = System.currentTimeMillis();

    LOGGER.info("[done]");

    // output timing results
    float sec = (e - s) / 1000f;
    float avg = (float) (e - s) / images.length;
    LOGGER.info("{}s elapsed ({}ms per plane)", sec, avg);

    if (minmax) printMinMaxValues();

    // display pixels in image viewer
    if (ascii) {
      for (int i=0; i<images.length; i++) {
        final BufferedImage img = images[i];
        LOGGER.info("");
        LOGGER.info("Image #{}:", i);
        LOGGER.info(new AsciiImage(img).toString());
      }
    }
    else {
      LOGGER.info("");
      LOGGER.info("Launching image viewer");
      ImageViewer viewer = new ImageViewer(false);
      viewer.setImages(reader, images);
      viewer.setVisible(true);
    }
  }

  public void printGlobalMetadata() {
    LOGGER.info("");
    LOGGER.info("Reading global metadata");
    Hashtable<String, Object> meta = reader.getGlobalMetadata();
    String[] keys = MetadataTools.keys(meta);
    for (String key : keys) {
      LOGGER.info("{}: {}", key,  meta.get(key));
    }
  }

  public void printOriginalMetadata() {
    String seriesLabel = reader.getSeriesCount() > 1 ?
      (" series #" + series) : "";
    LOGGER.info("");
    LOGGER.info("Reading{} metadata", seriesLabel);
    Hashtable<String, Object> meta = reader.getSeriesMetadata();
    String[] keys = MetadataTools.keys(meta);
    for (int i=0; i<keys.length; i++) {
      LOGGER.info("{}: {}", keys[i], meta.get(keys[i]));
    }
  }

  public void printOMEXML() throws MissingLibraryException, ServiceException {
    LOGGER.info("");
    MetadataStore ms = reader.getMetadataStore();

    if (baseReader instanceof ImageReader) {
      baseReader = ((ImageReader) baseReader).getReader();
    }
    if (baseReader instanceof OMETiffReader) {
      ms = ((OMETiffReader) baseReader).getMetadataStoreForDisplay();
    }

    OMEXMLService service;
    try {
      ServiceFactory factory = new ServiceFactory();
      service = factory.getInstance(OMEXMLService.class);
    }
    catch (DependencyException de) {
      throw new MissingLibraryException(OMEXMLServiceImpl.NO_OME_XML_MSG, de);
    }
    String version = service.getOMEXMLVersion(ms);
    if (version == null) LOGGER.info("Generating OME-XML");
    else {
      LOGGER.info("Generating OME-XML (schema version {})", version);
    }
    if (ms instanceof MetadataRetrieve) {
      // adding MetadataOnly elements to an OME-TIFF's XML will cause
      // validation errors
      if (!(baseReader instanceof OMETiffReader)) {
        service.removeBinData(service.getOMEMetadata((MetadataRetrieve) ms));
        for (int i=0; i<reader.getSeriesCount(); i++) {
          service.addMetadataOnly(
            service.getOMEMetadata((MetadataRetrieve) ms), i);
        }
      }

      if (omexmlOnly) {
        DebugTools.setRootLevel("INFO");
      }
      String xml = service.getOMEXML((MetadataRetrieve) ms);
      LOGGER.info("{}", XMLTools.indentXML(xml, xmlSpaces, true));
      if (omexmlOnly) {
        DebugTools.setRootLevel("OFF");
      }
      if (validate) {
        service.validateOMEXML(xml);
      }
    }
    else {
      LOGGER.info("The metadata could not be converted to OME-XML.");
      if (omexmlVersion == null) {
        LOGGER.info("The OME-XML Java library is probably not available.");
      }
      else {
        LOGGER.info("{} is probably not a legal schema version.",
          omexmlVersion);
      }
    }
  }

  /**
   * A utility method for reading a file from the command line,
   * and displaying the results in a simple display.
   */
  public boolean testRead(String[] args)
    throws FormatException, ServiceException, IOException
  {

    for (final String arg : args) {
      if (HELP_ARGUMENTS.contains(arg)) {
        if (reader == null) {
          reader = new ImageReader();
        }
        printUsage();
        return false;
      }
    }
    boolean validArgs = parseArgs(args);
    if (!validArgs) return false;
    if (printVersion) {
      CommandLineTools.printVersion();
      return true;
    }
    CommandLineTools.runUpgradeCheck(args);

    createReader();

    if (id == null) {
      printUsage();
      return false;
    }

    mapLocation();
    configureReaderPreInit();

    // initialize reader
    long s = System.currentTimeMillis();
    reader.setId(id);
    long e = System.currentTimeMillis();
    float sec = (e - s) / 1000f;
    LOGGER.info("Initialization took {}s", sec);

    configureReaderPostInit();
    checkWarnings();
    readCoreMetadata();
    reader.setSeries(series);
    if (flat == false)
      reader.setResolution(resolution);
    initPreMinMaxValues();

    // read pixels
    if (pixels) readPixels();

    // read format-specific metadata table
    if (doMeta) {
      printGlobalMetadata();
      printOriginalMetadata();
    }

    // output and validate OME-XML
    if (omexml) printOMEXML();

    if (!pixels) {
      reader.close();
    }

    return true;
  }

  /**
   * Log the size of the given dimension, using log4j.
   * @param dim the name of the dimension to log
   * @param size the total size of the dimension
   * @param effectiveSize the effective size of the dimension (e.g. 1 for RGB channels)
   * @param modulo the {@link loci.formats.Modulo} object associated with this dimension
   */
  private void printDimension(String dim, int size, int effectiveSize,
    Modulo modulo)
  {
    StringBuffer sb = new StringBuffer("\t");
    sb.append(dim);
    sb.append(" = ");
    sb.append(size);
    if (size != effectiveSize) {
      sb.append(" (effectively ");
      sb.append(effectiveSize);
      sb.append(")");
    }

    int product = 1;
    if (modulo.length() == 1) {
      product = size;
    }
    else {
      sb.append(" (");
      sb.append(size / modulo.length());
      sb.append(" ");
      sb.append(modulo.parentType);
      sb.append(" x ");
      sb.append(modulo.length());
      sb.append(" ");
      sb.append(modulo.type);
      sb.append(")");

      product = (size / modulo.length()) * modulo.length();
    }
    LOGGER.info(sb.toString());

    if (product != size) {
      LOGGER.warn("\t************ {} dimension mismatch ************", dim);
    }
  }

  // -- Main method --

  public static void main(String[] args) throws Exception {
    DebugTools.enableLogging("INFO");
    if (!new ImageInfo().testRead(args)) System.exit(1);
  }

}
