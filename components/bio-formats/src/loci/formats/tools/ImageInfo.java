//
// ImageInfo.java
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

package loci.formats.tools;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;

import loci.common.ByteArrayHandle;
import loci.common.DataTools;
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
import loci.formats.MetadataTools;
import loci.formats.MinMaxCalculator;
import loci.formats.MissingLibraryException;
import loci.formats.gui.AWTImageTools;
import loci.formats.gui.BufferedImageReader;
import loci.formats.gui.ImageViewer;
import loci.formats.in.OMETiffReader;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.formats.services.OMEXMLService;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ImageInfo is a utility class for reading a file
 * and reporting information about it.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/tools/ImageInfo.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/tools/ImageInfo.java">SVN</a></dd></dl>
 */
public class ImageInfo {

  // -- Constants --

  private static final Logger LOGGER = LoggerFactory.getLogger(ImageInfo.class);

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
  private boolean separate = false;
  private boolean expand = false;
  private boolean omexml = false;
  private boolean normalize = false;
  private boolean fastBlit = false;
  private boolean autoscale = false;
  private boolean preload = false;
  private String omexmlVersion = null;
  private int start = 0;
  private int end = Integer.MAX_VALUE;
  private int series = 0;
  private int xCoordinate = 0, yCoordinate = 0, width = 0, height = 0;
  private String swapOrder = null, shuffleOrder = null;
  private String map = null;
  private String format = null;

  private IFormatReader reader;
  private MinMaxCalculator minMaxCalc;
  private DimensionSwapper dimSwapper;
  private BufferedImageReader biReader;

  private String seriesLabel = null;

  private Double[] preGlobalMin = null, preGlobalMax = null;
  private Double[] preKnownMin = null, preKnownMax = null;
  private Double[] prePlaneMin = null, prePlaneMax = null;
  private boolean preIsMinMaxPop = false;

  private ConsoleAppender appender;
  private PatternLayout originalLayout;
  private PatternLayout altLayout = new PatternLayout("%m");

  // -- ImageInfo methods --

  public void parseArgs(String[] args) {
    org.apache.log4j.Logger root = org.apache.log4j.Logger.getRootLogger();
    root.setLevel(Level.INFO);
    originalLayout = new PatternLayout("%m%n");
    appender = new ConsoleAppender(originalLayout);
    root.addAppender(appender);

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
    separate = false;
    expand = false;
    omexml = false;
    normalize = false;
    fastBlit = false;
    autoscale = false;
    preload = false;
    omexmlVersion = null;
    start = 0;
    end = Integer.MAX_VALUE;
    series = 0;
    xCoordinate = 0;
    yCoordinate = 0;
    width = 0;
    height = 0;
    swapOrder = null;
    shuffleOrder = null;
    map = null;
    if (args == null) return;
    for (int i=0; i<args.length; i++) {
      if (args[i].startsWith("-")) {
        if (args[i].equals("-nopix")) pixels = false;
        else if (args[i].equals("-version")) printVersion = true;
        else if (args[i].equals("-nocore")) doCore = false;
        else if (args[i].equals("-nometa")) doMeta = false;
        else if (args[i].equals("-nofilter")) filter = false;
        else if (args[i].equals("-thumbs")) thumbs = true;
        else if (args[i].equals("-minmax")) minmax = true;
        else if (args[i].equals("-merge")) merge = true;
        else if (args[i].equals("-stitch")) stitch = true;
        else if (args[i].equals("-separate")) separate = true;
        else if (args[i].equals("-expand")) expand = true;
        else if (args[i].equals("-omexml")) omexml = true;
        else if (args[i].equals("-normalize")) normalize = true;
        else if (args[i].equals("-fast")) fastBlit = true;
        else if (args[i].equals("-autoscale")) autoscale = true;
        else if (args[i].equals("-debug")) {
          root.setLevel(Level.DEBUG);
        }
        else if (args[i].equals("-preload")) preload = true;
        else if (args[i].equals("-xmlversion")) omexmlVersion = args[++i];
        else if (args[i].equals("-crop")) {
          StringTokenizer st = new StringTokenizer(args[++i], ",");
          xCoordinate = Integer.parseInt(st.nextToken());
          yCoordinate = Integer.parseInt(st.nextToken());
          width = Integer.parseInt(st.nextToken());
          height = Integer.parseInt(st.nextToken());
        }
        else if (args[i].equals("-level")) {
          root.setLevel(Level.TRACE);
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
        else if (args[i].equals("-swap")) {
          swapOrder = args[++i].toUpperCase();
        }
        else if (args[i].equals("-shuffle")) {
          shuffleOrder = args[++i].toUpperCase();
        }
        else if (args[i].equals("-map")) map = args[++i];
        else if (args[i].equals("-format")) format = args[++i];
        else LOGGER.warn("Ignoring unknown command flag: {}", args[i]);
      }
      else {
        if (id == null) id = args[i];
        else LOGGER.warn("Ignoring unknown argument: {}", args[i]);
      }
    }
  }

  public void printUsage() {
    String className = reader.getClass().getName();
    String fmt = reader instanceof ImageReader ? "any" : reader.getFormat();
    String[] s = {
      "To test read a file in " + fmt + " format, run:",
      "  showinf file [-nopix] [-nocore] [-nometa] [-thumbs] [-minmax] ",
      "    [-merge] [-stitch] [-separate] [-expand] [-omexml]",
      "    [-normalize] [-fast] [-debug] [-range start end] [-series num]",
      "    [-swap inputOrder] [-shuffle outputOrder] [-map id] [-preload]",
      "    [-xmlversion v] [-crop x,y,w,h] [-autoscale] [-format Format]",
      "",
      "   -version: print the library version and exit",
      "       file: the image file to read",
      "     -nopix: read metadata only, not pixels",
      "    -nocore: do not output core metadata",
      "    -nometa: do not parse format-specific metadata table",
      "  -nofilter: do not filter metadata fields",
      "    -thumbs: read thumbnails instead of normal pixels",
      "    -minmax: compute min/max statistics",
      "     -merge: combine separate channels into RGB image",
      "    -stitch: stitch files with similar names",
      "  -separate: split RGB image into separate channels",
      "    -expand: expand indexed color to RGB",
      "    -omexml: populate OME-XML metadata",
      " -normalize: normalize floating point images*",
      "      -fast: paint RGB images as quickly as possible*",
      "     -debug: turn on debugging output",
      "     -range: specify range of planes to read (inclusive)",
      "    -series: specify which image series to read",
      "      -swap: override the default input dimension order",
      "   -shuffle: override the default output dimension order",
      "       -map: specify file on disk to which name should be mapped",
      "   -preload: pre-read entire file into a buffer; significantly",
      "             reduces the time required to read the images, but",
      "             requires more memory",
      "-xmlversion: specify which OME-XML version should be generated",
      "      -crop: crop images before displaying; argument is 'x,y,w,h'",
      " -autoscale: used in combination with '-fast' to automatically adjust",
      "             brightness and contrast",
      "    -format: read file with a particular reader (e.g., ZeissZVI)",
      "",
      "* = may result in loss of precision",
      ""
    };
    for (int i=0; i<s.length; i++) LOGGER.info(s[i]);
  }

  public void createReader() {
    if (format != null) {
      // create reader of a specific format type
      try {
        Class c = Class.forName("loci.formats.in." + format + "Reader");
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
  }

  public void mapLocation() throws IOException {
    if (map != null) Location.mapId(id, map);
    else if (preload) {
      RandomAccessInputStream f = new RandomAccessInputStream(id);
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
      reader.setOriginalMetadataPopulated(true);
      try {
        ServiceFactory factory = new ServiceFactory();
        OMEXMLService service = factory.getInstance(OMEXMLService.class);
        reader.setMetadataStore(
            service.createOMEXMLMetadata(null, omexmlVersion));
      }
      catch (DependencyException de) {
        throw new MissingLibraryException(OMETiffReader.NO_OME_XML_MSG, de);
      }
      catch (ServiceException se) {
        throw new FormatException(se);
      }
    }

    // check file format
    if (reader instanceof ImageReader) {
      // determine format
      ImageReader ir = (ImageReader) reader;
      LOGGER.info("Checking file format [{}]", ir.getFormat(id));
    }
    else {
      // verify format
      LOGGER.info("Checking {} format [{}]", reader.getFormat(),
        reader.isThisType(id) ? "yes" : "no");
    }

    LOGGER.info("Initializing reader");
    if (stitch) {
      reader = new FileStitcher(reader, true);
      String pat = FilePattern.findPattern(new Location(id));
      if (pat != null) id = pat;
    }
    if (expand) reader = new ChannelFiller(reader);
    if (separate) reader = new ChannelSeparator(reader);
    if (merge) reader = new ChannelMerger(reader);
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
    reader.setMetadataCollected(doMeta);
  }

  public void configureReaderPostInit() {
    if (swapOrder != null) dimSwapper.swapDimensions(swapOrder);
    if (shuffleOrder != null) dimSwapper.setOutputOrder(shuffleOrder);
  }

  public void checkWarnings() {
    if (!normalize && (reader.getPixelType() == FormatTools.FLOAT ||
      reader.getPixelType() == FormatTools.DOUBLE))
    {
      LOGGER.warn("Java does not support " +
        "display of unnormalized floating point data.");
      LOGGER.warn("Please use the '-normalize' option " +
        "to avoid receiving a cryptic exception.");
    }

    if (reader.isRGB() && reader.getRGBChannelCount() > 4) {
      LOGGER.warn("Java does not support merging more than 4 channels.");
      LOGGER.warn("Please use the '-separate' option " +
        "to avoid receiving a cryptic exception.");
    }
  }

  public void readCoreMetadata() throws FormatException, IOException {
    if (!doCore) return; // skip core metadata printout

    // read basic metadata
    LOGGER.info("\nReading core metadata");
    LOGGER.info("{} = {}", stitch ? "File pattern" : "Filename",
      stitch ? id : reader.getCurrentFile());
    if (map != null) LOGGER.info("Mapped filename = {}", map);
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
    int seriesCount = reader.getSeriesCount();
    LOGGER.info("Series count = {}", seriesCount);
    MetadataStore ms = reader.getMetadataStore();
    MetadataRetrieve mr = ms instanceof MetadataRetrieve? (MetadataRetrieve) ms
        : null;
    for (int j=0; j<seriesCount; j++) {
      reader.setSeries(j);

      // read basic metadata for series #i
      int imageCount = reader.getImageCount();
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
      int[] cLengths = reader.getChannelDimLengths();
      String[] cTypes = reader.getChannelDimTypes();
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
      LOGGER.info("\tImage count = {}", imageCount);
      LOGGER.info("\tRGB = {} ({}) {}", new Object[] {rgb, rgbChanCount,
        merge ? "(merged)" : separate ? "(separated)" : ""});
      if (rgb != (rgbChanCount != 1)) {
        LOGGER.warn("\t************ RGB mismatch ************");
      }
      LOGGER.info("\tInterleaved = {}", interleaved);

      appender.setLayout(altLayout);

      LOGGER.info("\tIndexed = {} ({} color", indexed, !falseColor);
      if (table8 != null) {
        LOGGER.info(", 8-bit LUT: {} x ", table8.length);
        LOGGER.info(table8[0] == null ? "null" : "" + table8[0].length);
      }
      if (table16 != null) {
        LOGGER.info(", 16-bit LUT: {} x ", table16.length);
        LOGGER.info(table16[0] == null ? "null" : "" + table16[0].length);
      }

      appender.setLayout(originalLayout);
      LOGGER.info(")");
      if (table8 != null && table16 != null) {
        LOGGER.warn("\t************ multiple LUTs ************");
      }
      LOGGER.info("\tWidth = {}", sizeX);
      LOGGER.info("\tHeight = {}", sizeY);
      LOGGER.info("\tSizeZ = {}", sizeZ);
      LOGGER.info("\tSizeT = {}", sizeT);

      appender.setLayout(altLayout);
      LOGGER.info("\tSizeC = {}", sizeC);
      if (sizeC != effSizeC) {
        LOGGER.info(" (effectively {})", effSizeC);
      }
      int cProduct = 1;
      if (cLengths.length == 1 && FormatTools.CHANNEL.equals(cTypes[0])) {
        cProduct = cLengths[0];
      }
      else {
        LOGGER.info(" (");
        for (int i=0; i<cLengths.length; i++) {
          if (i > 0) LOGGER.info(" x ");
          LOGGER.info("{} {}", cLengths[i], cTypes[i]);
          cProduct *= cLengths[i];
        }
        LOGGER.info(")");
      }
      appender.setLayout(originalLayout);
      LOGGER.info("");
      if (cLengths.length == 0 || cProduct != sizeC) {
        LOGGER.warn("\t************ C dimension mismatch ************");
      }
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
        appender.setLayout(altLayout);
        for (int i=0; i<indices.length; i++) {
          zct[i] = reader.getZCTCoords(indices[i]);
          indices2[i] = reader.getIndex(zct[i][0], zct[i][1], zct[i][2]);
          LOGGER.info("\tPlane #{} <=> Z {}, C {}, T {}",
            new Object[] {indices[i], zct[i][0], zct[i][1], zct[i][2]});
          if (indices[i] != indices2[i]) {
            LOGGER.info(" [mismatch: {}]\r\n", indices2[i]);
          }
          else LOGGER.info("\r\n");
        }
        appender.setLayout(originalLayout);
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
    LOGGER.info("\nMin/max values:");
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
    appender.setLayout(altLayout);
    LOGGER.info("\tFirst plane minimum(s) =");
    if (planeMin == null) LOGGER.info(" none");
    else {
      for (int subC=0; subC<planeMin.length; subC++) {
        LOGGER.info(" {}", planeMin[subC]);
      }
    }
    LOGGER.info(" (initially");
    if (prePlaneMin == null) LOGGER.info(" none");
    else {
      for (int subC=0; subC<prePlaneMin.length; subC++) {
        LOGGER.info(" {}", prePlaneMin[subC]);
      }
    }
    appender.setLayout(originalLayout);
    LOGGER.info(")");
    appender.setLayout(altLayout);
    LOGGER.info("\tFirst plane maximum(s) =");
    if (planeMax == null) LOGGER.info(" none");
    else {
      for (int subC=0; subC<planeMax.length; subC++) {
        LOGGER.info(" {}", planeMax[subC]);
      }
    }
    LOGGER.info(" (initially");
    if (prePlaneMax == null) LOGGER.info(" none");
    else {
      for (int subC=0; subC<prePlaneMax.length; subC++) {
        LOGGER.info(" {}", prePlaneMax[subC]);
      }
    }
    appender.setLayout(originalLayout);
    LOGGER.info(")");
    LOGGER.info("\tMin/max populated = {} (initially {})",
      isMinMaxPop, preIsMinMaxPop);
  }

  public void readPixels() throws FormatException, IOException {
    String seriesLabel = reader.getSeriesCount() > 1 ?
      (" series #" + series) : "";
    LOGGER.info("");
    appender.setLayout(altLayout);
    LOGGER.info("Reading {} pixel data ", seriesLabel);
    int num = reader.getImageCount();
    if (start < 0) start = 0;
    if (start >= num) start = num - 1;
    if (end < 0) end = 0;
    if (end >= num) end = num - 1;
    if (end < start) end = start;

    int sizeX = reader.getSizeX();
    int sizeY = reader.getSizeY();
    int sizeC = reader.getSizeC();

    if (width == 0) width = sizeX;
    if (height == 0) height = sizeY;

    int pixelType = reader.getPixelType();

    LOGGER.info("({}-{}) ", start, end);
    BufferedImage[] images = new BufferedImage[end - start + 1];
    long s2 = System.currentTimeMillis();
    boolean mismatch = false;
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
          min = new Double(0);
          max = new Double(1);
        }

        if (normalize) {
          if (pix instanceof float[]) {
            pix = DataTools.normalizeFloats((float[]) pix);
          }
          else if (pix instanceof double[]) {
            pix = DataTools.normalizeDoubles((double[]) pix);
          }
        }
        images[i - start] = AWTImageTools.makeImage(
          ImageTools.make24Bits(pix, sizeX, sizeY, false, false, min, max),
          sizeX, sizeY, FormatTools.isSigned(pixelType));
      }

      if (reader.isIndexed() && reader.get8BitLookupTable() == null &&
        reader.get16BitLookupTable() == null)
      {
        LOGGER.info("\r\n");
        LOGGER.warn("************ no LUT for plane #{} ************", i);
      }

      // check for pixel type mismatch
      int pixType = AWTImageTools.getPixelType(images[i - start]);
      if (pixType != pixelType && pixType != pixelType + 1 && !fastBlit) {
        if (!mismatch) {
          LOGGER.info("\r\n");
          mismatch = true;
        }
        LOGGER.info("\tPlane #{}: pixel type mismatch: {}/{}",
          new Object[] {i, FormatTools.getPixelTypeString(pixType),
          FormatTools.getPixelTypeString(pixelType)});
      }
      else {
        mismatch = false;
        LOGGER.info(".");
      }
    }
    long e2 = System.currentTimeMillis();
    if (!mismatch) LOGGER.info(" ");
    appender.setLayout(originalLayout);
    LOGGER.info("[done]");

    // output timing results
    float sec2 = (e2 - s2) / 1000f;
    float avg = (float) (e2 - s2) / images.length;
    LOGGER.info("{}s elapsed ({}ms per plane)", sec2, avg);

    if (minmax) printMinMaxValues();

    // display pixels in image viewer
    LOGGER.info("");
    LOGGER.info("Launching image viewer");
    ImageViewer viewer = new ImageViewer();
    viewer.setImages(reader, images);
    viewer.setVisible(true);
  }

  public void printGlobalMetadata() {
    LOGGER.info("");
    LOGGER.info("Reading global metadata");
    Hashtable meta = reader.getGlobalMetadata();
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
    Hashtable meta = reader.getSeriesMetadata();
    String[] keys = MetadataTools.keys(meta);
    for (int i=0; i<keys.length; i++) {
      LOGGER.info("{}: {}", keys[i], meta.get(keys[i]));
    }
  }

  public void printOMEXML() throws MissingLibraryException, ServiceException {
    LOGGER.info("");
    MetadataStore ms = reader.getMetadataStore();
    OMEXMLService service;
    try {
      ServiceFactory factory = new ServiceFactory();
      service = factory.getInstance(OMEXMLService.class);
    }
    catch (DependencyException de) {
      throw new MissingLibraryException(OMETiffReader.NO_OME_XML_MSG, de);
    }
    String version = service.getOMEXMLVersion(ms);
    if (version == null) LOGGER.info("Generating OME-XML");
    else {
      LOGGER.info("Generating OME-XML (schema version {})", version);
    }
    if (ms instanceof MetadataRetrieve) {
      String xml = service.getOMEXML((MetadataRetrieve) ms);
      LOGGER.info("{}", XMLTools.indentXML(xml, true));
      service.validateOMEXML(xml, true);
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
    throws FormatException, ServiceException, IOException {
    parseArgs(args);
    if (printVersion) {
      LOGGER.info("Version: {}", FormatTools.VERSION);
      LOGGER.info("SVN revision: {}", FormatTools.SVN_REVISION);
      LOGGER.info("Build date: {}", FormatTools.DATE);
      return true;
    }

    createReader();

    if (id == null) {
      printUsage();
      return false;
    }

    mapLocation();
    configureReaderPreInit();

    // initialize reader
    long s1 = System.currentTimeMillis();
    reader.setId(id);
    long e1 = System.currentTimeMillis();
    float sec1 = (e1 - s1) / 1000f;
    LOGGER.info("Initialization took {}s", sec1);

    configureReaderPostInit();
    checkWarnings();
    readCoreMetadata();
    reader.setSeries(series);
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

    return true;
  }

  // -- Main method --

  public static void main(String[] args) throws Exception {
    if (!new ImageInfo().testRead(args)) System.exit(1);
  }

}
