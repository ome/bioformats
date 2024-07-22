/*
 * #%L
 * Bio-Formats command line tools for reading and converting files
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

package loci.formats.tools;

import com.google.common.base.Joiner;

import java.awt.image.IndexColorModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.SortedMap;

import loci.common.Constants;
import loci.common.DataTools;
import loci.common.DebugTools;
import loci.common.Location;
import loci.common.image.IImageScaler;
import loci.common.image.SimpleImageScaler;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.ChannelFiller;
import loci.formats.ChannelMerger;
import loci.formats.ChannelSeparator;
import loci.formats.DimensionSwapper;
import loci.formats.FilePattern;
import loci.formats.FileStitcher;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.IFormatWriter;
import loci.formats.ImageReader;
import loci.formats.ImageTools;
import loci.formats.ImageWriter;
import loci.formats.Memoizer;
import loci.formats.MetadataTools;
import loci.formats.MinMaxCalculator;
import loci.formats.MissingLibraryException;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.JPEG2000CodecOptions;
import loci.formats.gui.Index16ColorModel;
import loci.formats.in.DynamicMetadataOptions;
import loci.formats.meta.IMetadata;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.formats.meta.IPyramidStore;
import loci.formats.out.DicomWriter;
import loci.formats.ome.OMEPyramidStore;
import loci.formats.out.IExtraMetadataWriter;
import loci.formats.out.TiffWriter;
import loci.formats.services.OMEXMLService;
import loci.formats.services.OMEXMLServiceImpl;

import ome.xml.meta.OMEXMLMetadataRoot;
import ome.xml.model.Channel;
import ome.xml.model.Image;
import ome.xml.model.Pixels;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.PositiveInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ImageConverter is a utility class for converting a file between formats.
 */
public final class ImageConverter {

  // -- Constants --

  private static final Logger LOGGER =
    LoggerFactory.getLogger(ImageConverter.class);

  // -- Fields --

  private String in = null, out = null;
  private String map = null;
  private String compression = null;
  private boolean stitch = false, separate = false, merge = false, fill = false;
  private boolean bigtiff = false, group = true;
  private boolean nobigtiff = false;
  private boolean printVersion = false;
  private boolean lookup = true;
  private boolean autoscale = false;
  private Boolean overwrite = null;
  private int series = -1;
  private int firstPlane = 0;
  private int lastPlane = Integer.MAX_VALUE;
  private int channel = -1, zSection = -1, timepoint = -1;
  private int xCoordinate = 0, yCoordinate = 0, width = 0, height = 0, width_crop = 0, height_crop = 0;
  private int saveTileWidth = 0, saveTileHeight = 0;
  private boolean validate = false;
  private boolean zeroPadding = false;
  private boolean flat = true;
  private int pyramidScale = 1, pyramidResolutions = 1;
  private boolean useMemoizer = false;
  private String cacheDir = null;
  private boolean originalMetadata = true;
  private boolean noSequential = false;
  private String swapOrder = null;
  private Byte fillColor = null;
  private boolean precompressed = false;
  private boolean tryPrecompressed = false;

  private Double compressionQuality = null;

  private String extraMetadata = null;

  private IFormatReader reader;
  private MinMaxCalculator minMax;
  private DimensionSwapper dimSwapper;

  private HashMap<String, Integer> nextOutputIndex = new HashMap<String, Integer>();
  private boolean firstTile = true;
  private DynamicMetadataOptions options = new DynamicMetadataOptions();

  // record paths that have been checked for overwriting
  // this is mostly useful when "out" is a file name pattern
  // that may be expanded into multiple actual files
  private HashMap<String, Boolean> checkedPaths = new HashMap<String, Boolean>();

  // -- Constructor --

  public ImageConverter() { }

  /**
   * Parse the given argument list to determine how to perform file conversion.
   * @param args the list of command line arguments
   * @return whether or not the argument list is valid
   */
  private boolean parseArgs(String[] args) {
    if (args == null) {
      return true;
    }
    for (int i=0; i<args.length; i++) {
      if (args[i].startsWith("-") && args.length > 1) {
        if (args[i].equals(CommandLineTools.VERSION)) {
          printVersion = true;
          return true;
        }
        else if (args[i].equals("-debug")) DebugTools.setRootLevel("DEBUG");
        else if (args[i].equals("-stitch")) stitch = true;
        else if (args[i].equals("-separate")) separate = true;
        else if (args[i].equals("-merge")) merge = true;
        else if (args[i].equals("-expand")) fill = true;
        else if (args[i].equals("-bigtiff")) bigtiff = true;
        else if (args[i].equals("-nobigtiff")) nobigtiff = true;
        else if (args[i].equals("-map")) map = args[++i];
        else if (args[i].equals("-compression")) compression = args[++i];
        else if (args[i].equals("-nogroup")) group = false;
        else if (args[i].equals("-nolookup")) lookup = false;
        else if (args[i].equals("-autoscale")) autoscale = true;
        else if (args[i].equals("-novalid")) validate = false;
        else if (args[i].equals("-validate")) validate = true;
        else if (args[i].equals("-padded")) zeroPadding = true;
        else if (args[i].equals("-noflat")) flat = false;
        else if (args[i].equals("-no-sas")) originalMetadata = false;
        else if (args[i].equals("-precompressed")) precompressed = true;
        else if (args[i].equals("-cache")) useMemoizer = true;
        else if (args[i].equals("-cache-dir")) {
          cacheDir = args[++i];
        }
        else if (args[i].equals("-option")) {
          options.set(args[++i], args[++i]);
        }
        else if (args[i].equals("-overwrite")) {
          overwrite = true;
        }
        else if (args[i].equals("-nooverwrite")) {
          overwrite = false;
        }
        else if (args[i].equals("-channel")) {
          channel = Integer.parseInt(args[++i]);
        }
        else if (args[i].equals("-z")) {
          zSection = Integer.parseInt(args[++i]);
        }
        else if (args[i].equals("-timepoint")) {
          timepoint = Integer.parseInt(args[++i]);
        }
        else if (args[i].equals("-series")) {
          try {
            series = Integer.parseInt(args[++i]);
          }
          catch (NumberFormatException exc) { }
        }
        else if (args[i].equals("-range")) {
          try {
            firstPlane = Integer.parseInt(args[++i]);
            lastPlane = Integer.parseInt(args[++i]) + 1;
          }
          catch (NumberFormatException exc) { }
        }
        else if (args[i].equals("-crop")) {
          String[] tokens = args[++i].split(",");
          xCoordinate = Integer.parseInt(tokens[0]);
          yCoordinate = Integer.parseInt(tokens[1]);
          width_crop = Integer.parseInt(tokens[2]);
          height_crop = Integer.parseInt(tokens[3]);
        }
        else if (args[i].equals("-tilex")) {
          try {
            saveTileWidth = Integer.parseInt(args[++i]);
          }
          catch (NumberFormatException e) { }
        }
        else if (args[i].equals("-tiley")) {
          try {
            saveTileHeight = Integer.parseInt(args[++i]);
          }
          catch (NumberFormatException e) { }
        }
        else if (args[i].equals("-pyramid-scale")) {
          try {
            pyramidScale = Integer.parseInt(args[++i]);
            if (pyramidScale <= 0) {
              LOGGER.error("Invalid pyramid scale: {}", pyramidScale);
              return false;
            }
          }
          catch (NumberFormatException e) { }
        }
        else if (args[i].equals("-pyramid-resolutions")) {
          try {
            pyramidResolutions = Integer.parseInt(args[++i]);
            if (pyramidResolutions <= 0) {
              LOGGER.error("Invalid pyramid resolution count: {}",
                pyramidResolutions);
              return false;
            }
          }
          catch (NumberFormatException e) { }
        }
        else if (args[i].equals("-no-sequential")) {
          noSequential = true;
        }
        else if (args[i].equals("-swap")) {
          swapOrder = args[++i].toUpperCase();
        }
        else if (args[i].equals("-fill")) {
          // allow specifying 0-255
          fillColor = (byte) Integer.parseInt(args[++i]);
        }
        else if (args[i].equals("-extra-metadata")) {
          extraMetadata = args[++i];
        }
        else if (args[i].equals("-quality")) {
          compressionQuality = DataTools.parseDouble(args[++i]);
        }
        else if (!args[i].equals(CommandLineTools.NO_UPGRADE_CHECK)) {
          LOGGER.error("Found unknown command flag: {}; exiting.", args[i]);
          return false;
        }
      }
      else {
        if (args[i].equals(CommandLineTools.VERSION)) printVersion = true;
        else if (in == null) in = args[i];
        else if (out == null) out = args[i];
        else {
          LOGGER.error("Found unknown argument: {}; exiting.", args[i]);
          LOGGER.error("You should specify exactly one input file and " +
            "exactly one output file.");
          return false;
        }
      }
    }

    if (bigtiff && nobigtiff) {
      LOGGER.error("Do not specify both -bigtiff and -nobigtiff");
      return false;
    }
    return true;
  }

  /* Return a sorted map of the available extensions per writer */
  private static SortedMap<String,String> getExtensions() {
    IFormatWriter[] writers = new ImageWriter().getWriters();
    SortedMap<String, String> extensions = new TreeMap<String, String>();
    for (int i=0; i<writers.length; i++) {
      extensions.put(writers[i].getFormat(),
        '.' + Joiner.on(", .").join(writers[i].getSuffixes()));
    }
    return extensions;
  }

  /* Return a sorted map of the available compressions per writer */
  private static SortedMap<String,String> getCompressions() {
    IFormatWriter[] writers = new ImageWriter().getWriters();
    SortedMap<String, String> compressions = new TreeMap<String, String>();
    for (int i=0; i<writers.length; i++) {
      String[] compressionTypes = writers[i].getCompressionTypes();
      if (compressionTypes != null) {
        compressions.put(writers[i].getFormat(),
          Joiner.on(", ").join(compressionTypes));
      }
    }
    return compressions;
  }

  /* Formats a sorted map into a string for the utility usage */
  private static String printList(SortedMap<String,String> map) {
    StringBuilder sb = new StringBuilder();
    Iterator it = map.entrySet().iterator();
    while (it.hasNext()) {
      SortedMap.Entry pair = (SortedMap.Entry) it.next();
      sb.append(" * " + pair.getKey() + ": " + pair.getValue() + '\n');
    }
    return sb.toString();
  }

  /**
   * Output usage information
   */
  private void printUsage() {
    String[] s = {
      "To convert a file between formats, run:",
      "  bfconvert [-debug] [-stitch] [-separate] [-merge] [-expand]",
      "    [-bigtiff] [-nobigtiff] [-compression codec] [-series series] [-noflat]",
      "    [-cache] [-cache-dir dir] [-no-sas]",
      "    [-map id] [-range start end] [-crop x,y,w,h]",
      "    [-channel channel] [-z Z] [-timepoint timepoint] [-nogroup]",
      "    [-nolookup] [-autoscale] [-version] [-no-upgrade] [-padded]",
      "    [-option key value] [-novalid] [-validate] [-tilex tileSizeX]", 
      "    [-tiley tileSizeY] [-pyramid-scale scale]", 
      "    [-swap dimensionsOrderString] [-fill color]",
      "    [-precompressed] [-quality compressionQuality]",
      "    [-pyramid-resolutions numResolutionLevels] in_file out_file",
      "",
      "            -version: print the library version and exit",
      "         -no-upgrade: do not perform the upgrade check",
      "              -debug: turn on debugging output",
      "             -stitch: stitch input files with similar names",
      "           -separate: split RGB images into separate channels",
      "              -merge: combine separate channels into RGB image",
      "             -expand: expand indexed color to RGB",
      "            -bigtiff: force BigTIFF files to be written",
      "          -nobigtiff: do not automatically switch to BigTIFF",
      "        -compression: specify the codec to use when saving images",
      "             -series: specify which image series to convert",
      "             -noflat: do not flatten subresolutions",
      "              -cache: cache the initialized reader",
      "          -cache-dir: use the specified directory to store the cached",
      "                      initialized reader. If unspecified, the cached reader",
      "                      will be stored under the same folder as the image file",
      "             -no-sas: do not preserve the OME-XML StructuredAnnotation elements",
      "                -map: specify file on disk to which name should be mapped",
      "              -range: specify range of planes to convert (inclusive)",
      "            -nogroup: force multi-file datasets to be read as individual" +
      "                      files",
      "           -nolookup: disable the conversion of lookup tables",
      "          -autoscale: automatically adjust brightness and contrast before",
      "                      converting; this may mean that the original pixel",
      "                      values are not preserved",
      "          -overwrite: always overwrite the output file, if it already exists",
      "        -nooverwrite: never overwrite the output file, if it already exists",
      "               -crop: crop images before converting; argument is 'x,y,w,h'",
      "            -channel: only convert the specified channel (indexed from 0)",
      "                  -z: only convert the specified Z section (indexed from 0)",
      "          -timepoint: only convert the specified timepoint (indexed from 0)",
      "             -padded: filename indexes for series, z, c and t will be zero padded",
      "             -option: add the specified key/value pair to the options list",
      "            -novalid: will not validate the OME-XML for the output file",
      "           -validate: will validate the generated OME-XML for the output file",
      "              -tilex: image will be converted one tile at a time using the given tile width",
      "              -tiley: image will be converted one tile at a time using the given tile height",
      "      -pyramid-scale: generates a pyramid image with each subsequent resolution level divided by scale",
      "-pyramid-resolutions: generates a pyramid image with the given number of resolution levels ",
      "      -no-sequential: do not assume that planes are written in sequential order",
      "               -swap: override the default input dimension order; argument is f.i. XYCTZ",
      "               -fill: byte value to use for undefined pixels (0-255)",
      "      -precompressed: transfer compressed bytes from input dataset directly to output.",
      "                      Most input and output formats do not support this option.",
      "                      Do not use -crop, -fill, or -autoscale, or pyramid generation options",
      "                      with this option.",
      "            -quality: double quality value for JPEG compression (0-1)",
      "",
      "The extension of the output file specifies the file format to use",
      "for the conversion. The list of available formats and extensions is:",
      "",
      printList(getExtensions()),
      "Some file formats offer multiple compression schemes that can be set",
      "using the -compression option. The list of available compressions is:",
      "",
      printList(getCompressions()),
      "If any of the following patterns are present in out_file, they will",
      "be replaced with the indicated metadata value from the input file.",
      "",
      "   Pattern:\tMetadata value:",
      "   ---------------------------",
      "   " + FormatTools.SERIES_NUM + "\t\tseries index",
      "   " + FormatTools.SERIES_NAME + "\t\tseries name",
      "   " + FormatTools.CHANNEL_NUM + "\t\tchannel index",
      "   " + FormatTools.CHANNEL_NAME +"\t\tchannel name",
      "   " + FormatTools.Z_NUM + "\t\tZ index",
      "   " + FormatTools.T_NUM + "\t\tT index",
      "   " + FormatTools.TIMESTAMP + "\t\tacquisition timestamp",
      "   " + FormatTools.TILE_X + "\t\trow index of the tile",
      "   " + FormatTools.TILE_Y + "\t\tcolumn index of the tile",
      "   " + FormatTools.TILE_NUM + "\t\toverall tile index",
      "",
      "If any of these patterns are present, then the images to be saved",
      "will be split into multiple files.  For example, if the input file",
      "contains 5 Z sections and 3 timepoints, and out_file is",
      "",
      "  converted_Z" + FormatTools.Z_NUM + "_T" +
      FormatTools.T_NUM + ".tiff",
      "",
      "then 15 files will be created, with the names",
      "",
      "  converted_Z0_T0.tiff",
      "  converted_Z0_T1.tiff",
      "  converted_Z0_T2.tiff",
      "  converted_Z1_T0.tiff",
      "  ...",
      "  converted_Z4_T2.tiff",
      "",
      "Each file would have a single image plane."
    };
    for (int i=0; i<s.length; i++) System.out.println(s[i]);
  }

  // -- Utility methods --

  /** A utility method for converting a file from the command line. */
  public boolean testConvert(IFormatWriter writer, String[] args)
    throws FormatException, IOException
  {
    nextOutputIndex.clear();
    options.setValidate(validate);
    writer.setMetadataOptions(options);
    firstTile = true;
    boolean success = parseArgs(args);
    if (!success) {
      return false;
    }

    if (printVersion) {
      CommandLineTools.printVersion();
      return true;
    }

    if (in == null || out == null) {
      printUsage();
      return false;
    }

    // TODO: there may be other options not compatible with -precompressed
    if (precompressed &&
      (width_crop > 0 || height_crop > 0 ||
      pyramidResolutions > 1 ||
      fillColor != null ||
      autoscale
      ))
    {
      throw new UnsupportedOperationException("-precompressed not supported with " +
        "-autoscale, -crop, -fill, -pyramid-scale, -pyramid-resolutions");
    }

    CommandLineTools.runUpgradeCheck(args);

    if (new Location(out).exists()) {
      boolean ok = overwriteCheck(out, false);
      if (!ok) {
        return false;
      }
    }

    if (map != null) Location.mapId(in, map);

    long start = System.currentTimeMillis();
    LOGGER.info(in);
    reader = new ImageReader();
    if (swapOrder != null) {
      reader = dimSwapper = new DimensionSwapper(reader);
    }
    if (stitch) {
      reader = new FileStitcher(reader);
      Location f = new Location(in);
      String pat = null;
      if (!f.exists()) {
        pat = in;
      }
      else {
        pat = FilePattern.findPattern(f);
      }
      if (pat != null) in = pat;
    }
    if (separate) reader = new ChannelSeparator(reader);
    if (merge) reader = new ChannelMerger(reader);
    if (fill) reader = new ChannelFiller(reader);
    if (useMemoizer) {
      if (cacheDir != null) {
        reader = new Memoizer(reader, 0, new File(cacheDir));
      }
      else {
        reader = new Memoizer(reader, 0);
      }
    }
    minMax = null;
    if (autoscale) {
      reader = new MinMaxCalculator(reader);
      minMax = (MinMaxCalculator) reader;
    }

    reader.setMetadataOptions(options);
    reader.setGroupFiles(group);
    reader.setMetadataFiltered(true);
    reader.setOriginalMetadataPopulated(originalMetadata);
    reader.setFlattenedResolutions(flat);
    reader.setFillColor(fillColor);
    OMEXMLService service = null;
    try {
      ServiceFactory factory = new ServiceFactory();
      service = factory.getInstance(OMEXMLService.class);
      reader.setMetadataStore(service.createOMEXMLMetadata());
    }
    catch (DependencyException de) {
      throw new MissingLibraryException(OMEXMLServiceImpl.NO_OME_XML_MSG, de);
    }
    catch (ServiceException se) {
      throw new FormatException(se);
    }

    reader.setId(in);

    if (swapOrder != null) {
       dimSwapper.swapDimensions(swapOrder);
    }

    MetadataStore store = reader.getMetadataStore();

    MetadataTools.populatePixels(store, reader, false, false);

    boolean dimensionsSet = true;

    // only switch series if the '-series' flag was used;
    // otherwise default to series 0
    if (series >= 0) {
      reader.setSeries(series);
    }

    if (width_crop == 0 || height_crop == 0) {
      width = reader.getSizeX();
      height = reader.getSizeY();
      dimensionsSet = false;
    } else {
      width = Math.min(reader.getSizeX(), width_crop);
      height = Math.min(reader.getSizeY(), height_crop);
    }

    if (channel >= reader.getEffectiveSizeC()) {
      throw new FormatException("Invalid channel '" + channel + "' (" +
        reader.getEffectiveSizeC() + " channels in source file)");
    }
    if (timepoint >= reader.getSizeT()) {
      throw new FormatException("Invalid timepoint '" + timepoint + "' (" +
        reader.getSizeT() + " timepoints in source file)");
    }
    if (zSection >= reader.getSizeZ()) {
      throw new FormatException("Invalid Z section '" + zSection + "' (" +
        reader.getSizeZ() + " Z sections in source file)");
    }

    if (store instanceof MetadataRetrieve) {
      try {
        String xml = service.getOMEXML(service.asRetrieve(store));
        OMEXMLMetadataRoot root = (OMEXMLMetadataRoot) store.getRoot();
        IMetadata meta = service.createOMEXMLMetadata(xml);
        OMEXMLMetadataRoot newRoot = (OMEXMLMetadataRoot) meta.getRoot();
        if (series >= 0) {
          Image exportImage = newRoot.getImage(series);
          Pixels exportPixels = newRoot.getImage(series).getPixels();

          if (channel >= 0) {
            List<Channel> channels = exportPixels.copyChannelList();

            for (int c=0; c<channels.size(); c++) {
              if (c != channel) {
                exportPixels.removeChannel(channels.get(c));
              }
            }
          }

          exportImage.setPixels(exportPixels);
          while (newRoot.sizeOfImageList() > 0) {
            newRoot.removeImage(newRoot.getImage(0));
          }
          while (newRoot.sizeOfPlateList() > 0) {
            newRoot.removePlate(newRoot.getPlate(0));
          }
          newRoot.addImage(exportImage);
          meta.setRoot(newRoot);
          meta.setPixelsSizeX(new PositiveInteger(width), 0);
          meta.setPixelsSizeY(new PositiveInteger(height), 0);

          if (autoscale) {
            store.setPixelsType(PixelType.UINT8, 0);
          }

          if (channel >= 0) {
            meta.setPixelsSizeC(new PositiveInteger(1), 0);
          }
          if (zSection >= 0) {
            meta.setPixelsSizeZ(new PositiveInteger(1), 0);
          }
          if (timepoint >= 0) {
            meta.setPixelsSizeT(new PositiveInteger(1), 0);
          }

          setupResolutions(meta);
          writer.setMetadataRetrieve((MetadataRetrieve) meta);
        }
        else {
          for (int i=0; i<reader.getSeriesCount(); i++) {
            reader.setSeries(i);
            width = reader.getSizeX();
            height = reader.getSizeY();
            if (width_crop != 0 || height_crop != 0) {
              width = Math.min(reader.getSizeX(), width_crop);
              height = Math.min(reader.getSizeY(), height_crop);
            }
            meta.setPixelsSizeX(new PositiveInteger(width), i);
            meta.setPixelsSizeY(new PositiveInteger(height), i);
            if (autoscale) {
              store.setPixelsType(PixelType.UINT8, i);
            }

            if (channel >= 0) {
              Pixels exportPixels = newRoot.getImage(i).getPixels();
              List<Channel> channels = exportPixels.copyChannelList();

              for (int c=0; c<channels.size(); c++) {
                if (c != channel) {
                  exportPixels.removeChannel(channels.get(c));
                }
              }
            }

            if (channel >= 0) {
              meta.setPixelsSizeC(new PositiveInteger(1), i);
            }
            if (zSection >= 0) {
              meta.setPixelsSizeZ(new PositiveInteger(1), i);
            }
            if (timepoint >= 0) {
              meta.setPixelsSizeT(new PositiveInteger(1), i);
            }
          }

          setupResolutions(meta);
          writer.setMetadataRetrieve((MetadataRetrieve) meta);
        }
      }
      catch (ServiceException e) {
        throw new FormatException(e);
      }
    }
    writer.setWriteSequentially(!noSequential);

    if (writer instanceof TiffWriter) {
      ((TiffWriter) writer).setBigTiff(bigtiff);
      ((TiffWriter) writer).setCanDetectBigTiff(!nobigtiff);
    }
    else if (writer instanceof DicomWriter) {
      ((DicomWriter) writer).setBigTiff(bigtiff);
    }
    else if (writer instanceof ImageWriter) {
      IFormatWriter w = ((ImageWriter) writer).getWriter(out);
      if (w instanceof TiffWriter) {
        ((TiffWriter) w).setBigTiff(bigtiff);
        ((TiffWriter) w).setCanDetectBigTiff(!nobigtiff);
      }
      else if (w instanceof DicomWriter) {
        ((DicomWriter) w).setBigTiff(bigtiff);
      }
    }
    if (writer instanceof IExtraMetadataWriter) {
      ((IExtraMetadataWriter) writer).setExtraMetadata(extraMetadata);
    }
    else if (writer instanceof ImageWriter) {
      IFormatWriter w = ((ImageWriter) writer).getWriter(out);
      if (w instanceof IExtraMetadataWriter) {
        ((IExtraMetadataWriter) w).setExtraMetadata(extraMetadata);
      }
    }

    String format = writer.getFormat();
    LOGGER.info("[{}] -> {} [{}]",
      new Object[] {reader.getFormat(), out, format});
    long mid = System.currentTimeMillis();

    int total = 0;
    int num = writer.canDoStacks() ? reader.getSeriesCount() : 1;
    long read = 0, write = 0;
    int first = series == -1 ? 0 : series;
    int last = series == -1 ? num : series + 1;
    long timeLastLogged = System.currentTimeMillis();
    for (int q=first; q<last; q++) {
      reader.setSeries(q);
      // OutputIndex should be reset at the start of a new series
      nextOutputIndex.clear();
      boolean generatePyramid = pyramidResolutions > reader.getResolutionCount();
      int resolutionCount = generatePyramid ? pyramidResolutions : reader.getResolutionCount();
      for (int res=0; res<resolutionCount; res++) {
        if (!generatePyramid) {
          reader.setResolution(res);
        }
        firstTile = true;

        if (!dimensionsSet) {
          width = reader.getSizeX();
          height = reader.getSizeY();

          if (generatePyramid && res > 0) {
            int scale = (int) Math.pow(pyramidScale, res);
            width /= scale;
            height /= scale;
          }
        } else {
          width = Math.min(reader.getSizeX(), width_crop);
          height = Math.min(reader.getSizeY(), height_crop);
        }

        int writerSeries = series == -1 ? q : 0;
        writer.setSeries(writerSeries);
        writer.setResolution(res);
        writer.setInterleaved(reader.isInterleaved() && !autoscale);
        writer.setValidBitsPerPixel(reader.getBitsPerPixel());
        int numImages = writer.canDoStacks() ? reader.getImageCount() : 1;

        int startPlane = (int) Math.max(0, firstPlane);
        int endPlane = (int) Math.min(numImages, lastPlane);
        numImages = endPlane - startPlane;

        if (channel >= 0) {
          numImages /= reader.getEffectiveSizeC();
        }
        if (zSection >= 0) {
          numImages /= reader.getSizeZ();
        }
        if (timepoint >= 0) {
          numImages /= reader.getSizeT();
        }

        total += numImages;

        if (precompressed) {
          writer.setTileSizeX(reader.getOptimalTileWidth());
          writer.setTileSizeY(reader.getOptimalTileHeight());
        }
        else if (saveTileWidth > 0 && saveTileHeight > 0) {
          writer.setTileSizeX(saveTileWidth);
          writer.setTileSizeY(saveTileHeight);
        }

        int count = 0;
        for (int i=startPlane; i<endPlane; i++) {
          int[] coords = reader.getZCTCoords(i);

          if ((zSection >= 0 && coords[0] != zSection) || (channel >= 0 &&
            coords[1] != channel) || (timepoint >= 0 && coords[2] != timepoint))
          {
            continue;
          }

          String outputName = FormatTools.getFilename(q, i, reader, out, zeroPadding);
          String tileName = FormatTools.getTileFilename(0, 0, 0, outputName);

          if (outputName.equals(tileName)) {
            boolean ok = overwriteCheck(outputName, false);
            if (!ok) {
              return false;
            }
            setCodecOptions(writer);
            writer.setId(outputName);
            if (compression != null) writer.setCompression(compression);
          }
          else {
            int tileNum = outputName.indexOf(FormatTools.TILE_NUM);
            int tileX = outputName.indexOf(FormatTools.TILE_X);
            int tileY = outputName.indexOf(FormatTools.TILE_Y);
            if (tileNum < 0 && (tileX < 0 || tileY < 0)) {
              throw new FormatException("Invalid file name pattern; " +
                FormatTools.TILE_NUM + " or both of " + FormatTools.TILE_X +
                " and " + FormatTools.TILE_Y + " must be specified.");
            }
            if (saveTileWidth == 0 && saveTileHeight == 0) {
              // Using tile output name but not tiled reading

              boolean ok = overwriteCheck(tileName, false);
              if (!ok) {
                return false;
              }
              setCodecOptions(writer);
              writer.setId(tileName);
              if (compression != null) writer.setCompression(compression);
            }
          }

          int outputIndex = 0;
          if (nextOutputIndex.containsKey(outputName)) {
            outputIndex = nextOutputIndex.get(outputName);
          }

          long s = System.currentTimeMillis();
          long m = convertPlane(writer, i, outputIndex, outputName);
          long e = System.currentTimeMillis();
          read += m - s;
          write += e - m;

          nextOutputIndex.put(outputName, outputIndex + 1);
          if (i == endPlane - 1) {
            nextOutputIndex.remove(outputName);
          }

          // log number of planes processed every second or so
          if (count == numImages - 1 || (e - timeLastLogged) / 1000 > 0) {
            int current = (count - startPlane) + 1;
            int percent = 100 * current / numImages;
            StringBuilder sb = new StringBuilder();
            sb.append("\t");
            int numSeries = last - first;
            if (numSeries > 1) {
              sb.append("Series ");
              sb.append(q);
              sb.append(": converted ");
            }
            else sb.append("Converted ");
            LOGGER.info(sb.toString() + "{}/{} planes ({}%)",
              new Object[] {current, numImages, percent});
            timeLastLogged = e;
          }
          count++;
        }
      }
    }
    writer.close();
    long end = System.currentTimeMillis();
    LOGGER.info("[done]");

    // output timing results
    float sec = (end - start) / 1000f;
    long initial = mid - start;
    float readAvg = (float) read / total;
    float writeAvg = (float) write / total;
    LOGGER.info("{}s elapsed ({}+{}ms per plane, {}ms overhead)",
      new Object[] {sec, readAvg, writeAvg, initial});

    return true;
  }

  // -- Helper methods --

  /**
   * Convert the specified plane using the given writer.
   * @param writer the {@link loci.formats.IFormatWriter} to use for writing the plane
   * @param index the index of the plane to convert in the input file
   * @param outputIndex the index of the plane to convert in the output file
   * @param currentFile the file name or pattern being written to
   * @return the time at which conversion started, in milliseconds
   * @throws FormatException
   * @throws IOException
   */
  private long convertPlane(IFormatWriter writer, int index, int outputIndex,
    String currentFile)
    throws FormatException, IOException
  {
    if (doTileConversion(writer, currentFile)) {
      // this is a "big image" or an output tile size was set, so we will attempt
      // to convert it one tile at a time

      if (isTiledWriter(writer, out)) {
        return convertTilePlane(writer, index, outputIndex, currentFile);
      }
    }

    tryPrecompressed = precompressed && FormatTools.canUsePrecompressedTiles(reader, writer, writer.getSeries(), writer.getResolution());

    byte[] buf = getTile(reader, writer.getResolution(), index,
      xCoordinate, yCoordinate, width, height);

    // if we asked for precompressed tiles, but that wasn't possible,
    // then log that decompression/recompression happened
    // TODO: decide if an exception is better here?
    if (precompressed && !tryPrecompressed) {
      LOGGER.warn("Decompressed tile: series={}, resolution={}, x={}, y={}",
        writer.getSeries(), writer.getResolution(), xCoordinate, yCoordinate);
    }

    autoscalePlane(buf, index);
    applyLUT(writer);
    long m = System.currentTimeMillis();
    if (tryPrecompressed) {
      writer.saveCompressedBytes(outputIndex, buf, 0, 0, reader.getSizeX(), reader.getSizeY());
    }
    else {
      writer.saveBytes(outputIndex, buf);
    }
    return m;
  }

 /**
   * Convert the specified plane as a set of tiles, using the specified writer.
   * @param writer the {@link loci.formats.IFormatWriter} to use for writing the plane
   * @param index the index of the plane to convert in the input file
   * @param outputIndex the index of the plane to convert in the output file
   * @param currentFile the file name or pattern being written to
   * @return the time at which conversion started, in milliseconds
   * @throws FormatException
   * @throws IOException
   */
  private long convertTilePlane(IFormatWriter writer, int index, int outputIndex,
    String currentFile)
    throws FormatException, IOException
  {
    int w = Math.min(reader.getOptimalTileWidth(), width);
    int h = Math.min(reader.getOptimalTileHeight(), height);
    if (saveTileWidth > 0 && saveTileWidth <= width) {
      w = saveTileWidth;
    }
    if (saveTileHeight > 0 && saveTileHeight <= height) {
      h = saveTileHeight;
    }

    IFormatWriter baseWriter = ((ImageWriter) writer).getWriter(out);
    w = baseWriter.setTileSizeX(w);
    h = baseWriter.setTileSizeY(h);

    if (firstTile) {
      LOGGER.info("Tile size = {} x {}", w, h);
      firstTile = false;
    }

    int nXTiles = width / w;
    int nYTiles = height / h;

    if (nXTiles * w != width) {
      nXTiles++;
    }
    if (nYTiles * h != height) {
      nYTiles++;
    }

    // only warn once if the whole resolution will need to
    // be decompressed and recompressed
    boolean canPrecompressResolution = precompressed && FormatTools.canUsePrecompressedTiles(reader, writer, writer.getSeries(), writer.getResolution());
    if (precompressed && !canPrecompressResolution) {
      LOGGER.warn("Decompressing resolution: series={}, resolution={}",
        writer.getSeries(), writer.getResolution());
      tryPrecompressed = false;
    }

    Long m = null;
    for (int y=0; y<nYTiles; y++) {
      for (int x=0; x<nXTiles; x++) {
        int tileX = xCoordinate + x * w;
        int tileY = yCoordinate + y * h;
        int tileWidth = x < nXTiles - 1 ? w : width - (w * x);
        int tileHeight = y < nYTiles - 1 ? h : height - (h * y);

        tryPrecompressed = precompressed && canPrecompressResolution &&
          FormatTools.canUsePrecompressedTiles(reader, writer, writer.getSeries(), writer.getResolution());
        byte[] buf = getTile(reader, writer.getResolution(),
          index, tileX, tileY, tileWidth, tileHeight);

        // if we asked for precompressed tiles, but that wasn't possible,
        // then log that decompression/recompression happened
        // this is mainly expected for edge tiles, which might be smaller than expected
        // TODO: decide if an exception is better here?
        if (precompressed && canPrecompressResolution && !tryPrecompressed) {
          LOGGER.warn("Decompressed tile: series={}, resolution={}, x={}, y={}",
            writer.getSeries(), writer.getResolution(), x, y);
        }

        String tileName =
          FormatTools.getTileFilename(x, y, y * nXTiles + x, currentFile);
        if (!currentFile.equals(tileName)) {
          int nTileRows = getTileRows(currentFile);
          int nTileCols = getTileColumns(currentFile);

          int sizeX = nTileCols == 1 ? width : tileWidth;
          int sizeY = nTileRows == 1 ? height : tileHeight;
          MetadataRetrieve retrieve = writer.getMetadataRetrieve();
          writer.close();
          int writerSeries = series == -1 ? reader.getSeries() : 0;
          if (retrieve instanceof MetadataStore) {
            ((MetadataStore) retrieve).setPixelsSizeX(
              new PositiveInteger(sizeX), writerSeries);
            ((MetadataStore) retrieve).setPixelsSizeY(
              new PositiveInteger(sizeY), writerSeries);
            setupResolutions((IMetadata) retrieve);
          }

          writer.setMetadataRetrieve(retrieve);

          overwriteCheck(tileName, true);
          setCodecOptions(writer);
          writer.setId(tileName);
          if (compression != null) writer.setCompression(compression);

          outputIndex = 0;
          if (nextOutputIndex.containsKey(tileName)) {
            outputIndex = nextOutputIndex.get(tileName);
          }
          nextOutputIndex.put(tileName, outputIndex + 1);

          if (nTileRows > 1) {
            tileY = 0;
            tileHeight = baseWriter.setTileSizeY(tileHeight);
          }
          if (nTileCols > 1) {
            tileX = 0;
            tileWidth = baseWriter.setTileSizeX(tileWidth);
          }
        }

        autoscalePlane(buf, index);
        applyLUT(writer);
        if (m == null) {
          m = System.currentTimeMillis();
        }

        // calculate the XY coordinate in the output image
        // don't use tileX and tileY, as they will be too large
        // if any cropping was performed
        int outputX = x * w;
        int outputY = y * h;

        if (currentFile.indexOf(FormatTools.TILE_NUM) >= 0 ||
            currentFile.indexOf(FormatTools.TILE_X) >= 0 ||
            currentFile.indexOf(FormatTools.TILE_Y) >= 0)
        {
          outputX = 0;
          outputY = 0;
        }

        if (tryPrecompressed) {
          writer.saveCompressedBytes(outputIndex, buf, outputX, outputY, tileWidth, tileHeight);
        }
        else {
          writer.saveBytes(outputIndex, buf, outputX, outputY, tileWidth, tileHeight);
        }
      }
    }
    return m;
  }

  /**
   * Calculate the number of vertical tiles represented by the given file name pattern.
   * @param outputName the output file name pattern
   * @return the number of vertical tiles (rows)
   */
  private int getTileRows(String outputName) {
    if (outputName.indexOf(FormatTools.TILE_Y) >= 0 ||
      outputName.indexOf(FormatTools.TILE_NUM) >= 0)
    {
      int h = reader.getOptimalTileHeight();
      if (saveTileHeight > 0 && saveTileHeight <= height) {
        h = saveTileHeight;
      }
      int nYTiles = height / h;
      if (nYTiles * h != height) {
        nYTiles++;
      }
      return nYTiles;
    }
    return 1;
  }

  /**
   * Calculate the number of horizontal tiles represented by the given file name pattern.
   * @param outputName the output file name pattern
   * @return the number of horizontal tiles (columns)
   */
  public int getTileColumns(String outputName) {
    if (outputName.indexOf(FormatTools.TILE_X) >= 0 ||
      outputName.indexOf(FormatTools.TILE_NUM) >= 0)
    {
      int w = reader.getOptimalTileWidth();
      if (saveTileWidth > 0 && saveTileWidth <= width) {
        w = saveTileWidth;
      }

      int nXTiles = width / w;
      if (nXTiles * w != width) {
        nXTiles++;
      }
      return nXTiles;
    }
    return 1;
  }

  /**
   * Perform in-place autoscaling on the given plane data.
   * @param buf the raw pixel data for the plane
   * @param index the index of the plane in the input file
   * @throws FormatException
   * @throws IOException
   */
  private void autoscalePlane(byte[] buf, int index)
    throws FormatException, IOException
  {
    if (autoscale && !tryPrecompressed) {
      Double min = null;
      Double max = null;

      Double[] planeMin = minMax.getPlaneMinimum(index);
      Double[] planeMax = minMax.getPlaneMaximum(index);

      if (planeMin != null && planeMax != null) {
        min = planeMin[0];
        max = planeMax[0];

        for (int j=1; j<planeMin.length; j++) {
          if (planeMin[j].doubleValue() < min.doubleValue()) {
            min = planeMin[j];
          }
          if (planeMax[j].doubleValue() < max.doubleValue()) {
            max = planeMax[j];
          }
        }
      }

      int pixelType = reader.getPixelType();
      int bpp = FormatTools.getBytesPerPixel(pixelType);
      boolean floatingPoint = FormatTools.isFloatingPoint(pixelType);
      Object pix = DataTools.makeDataArray(buf, bpp, floatingPoint,
        reader.isLittleEndian());
      byte[][] b = ImageTools.make24Bits(pix, width, height,
        reader.isInterleaved(), false, min, max);

      int channelCount = reader.getRGBChannelCount();
      int copyComponents = (int) Math.min(channelCount, b.length);

      buf = new byte[channelCount * b[0].length];
      for (int j=0; j<copyComponents; j++) {
        System.arraycopy(b[j], 0, buf, b[0].length * j, b[0].length);
      }
    }
  }

  /**
   * Use the lookup table from the reader (if present) to set
   * the color model in the given writer
   * @param writer the {@link loci.formats.IFormatWriter} on which to set a color model
   * @throws FormatException
   * @throws IOException
   */
  private void applyLUT(IFormatWriter writer)
    throws FormatException, IOException
  {
    if (lookup) {
      byte[][] lut = reader.get8BitLookupTable();
      if (lut != null) {
        IndexColorModel model = new IndexColorModel(8, lut[0].length,
          lut[0], lut[1], lut[2]);
        writer.setColorModel(model);
      }
      else {
        short[][] lut16 = reader.get16BitLookupTable();
        if (lut16 != null) {
          Index16ColorModel model = new Index16ColorModel(16, lut16[0].length,
            lut16, reader.isLittleEndian());
          writer.setColorModel(model);
        }
      }
    }
  }

  private void setupResolutions(IMetadata meta) {
    if (!(meta instanceof OMEPyramidStore)) {
      return;
    }
    for (int series=0; series<meta.getImageCount(); series++) {
      int width = meta.getPixelsSizeX(series).getValue();
      int height = meta.getPixelsSizeY(series).getValue();
      for (int i=1; i<pyramidResolutions; i++) {
        int scale = (int) Math.pow(pyramidScale, i);
        ((OMEPyramidStore) meta).setResolutionSizeX(
          new PositiveInteger(width / scale), series, i);
        ((OMEPyramidStore) meta).setResolutionSizeY(
          new PositiveInteger(height / scale), series, i);
      }
    }
  }

  private byte[] getTile(IFormatReader reader, int resolution,
    int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    if (resolution < reader.getResolutionCount()) {
      reader.setResolution(resolution);
      int optimalWidth = reader.getOptimalTileWidth();
      int optimalHeight = reader.getOptimalTileHeight();
      if (tryPrecompressed) {
        return reader.openCompressedBytes(no, x / optimalWidth, y / optimalHeight);
      }
      tryPrecompressed = false;
      return reader.openBytes(no, x, y, w, h);
    }
    if (tryPrecompressed) {
      throw new UnsupportedOperationException("Cannot generate resolutions with precompressed tiles");
    }
    reader.setResolution(0);
    IImageScaler scaler = new SimpleImageScaler();
    int scale = (int) Math.pow(pyramidScale, resolution);
    byte[] tile =
      reader.openBytes(no, x * scale, y * scale, w * scale, h * scale);
    int type = reader.getPixelType();
    return scaler.downsample(tile, w * scale, h * scale, scale,
      FormatTools.getBytesPerPixel(type), reader.isLittleEndian(),
      FormatTools.isFloatingPoint(type), reader.getRGBChannelCount(),
      reader.isInterleaved());
  }

  private boolean isTiledWriter(IFormatWriter writer, String outputFile)
    throws FormatException
  {
    if (writer instanceof ImageWriter) {
      return isTiledWriter(((ImageWriter) writer).getWriter(outputFile), outputFile);
    }
    return (writer instanceof TiffWriter) || (writer instanceof DicomWriter);
  }

  private boolean doTileConversion(IFormatWriter writer, String outputFile)
    throws FormatException
  {
    if (writer instanceof DicomWriter ||
      (writer instanceof ImageWriter && ((ImageWriter) writer).getWriter(outputFile) instanceof DicomWriter))
    {
      MetadataStore r = reader.getMetadataStore();
      return !(r instanceof IPyramidStore) || ((IPyramidStore) r).getResolutionCount(reader.getSeries()) > 1;
    }
    return DataTools.safeMultiply64(width, height) >= DataTools.safeMultiply64(4096, 4096) ||
      saveTileWidth > 0 || saveTileHeight > 0;
  }

  private boolean overwriteCheck(String path, boolean throwOnExist) throws IOException {
    if (checkedPaths.containsKey(path)) {
      return checkedPaths.get(path);
    }
    if (!new Location(path).exists()) {
      checkedPaths.put(path, true);
      return true;
    }
    if (overwrite == null) {
      LOGGER.warn("Output file {} exists.", path);
      LOGGER.warn("Do you want to overwrite it? ([y]/n)");
      BufferedReader r = new BufferedReader(
        new InputStreamReader(System.in, Constants.ENCODING));
      String choice = r.readLine().trim().toLowerCase();
      overwrite = !choice.startsWith("n");
    }
    if (!overwrite) {
      String msg = "Exiting; next time, please specify an output file that does not exist.";
      checkedPaths.put(path, false);
      if (throwOnExist) {
        throw new IOException(msg);
      }
      LOGGER.warn(msg);
      return false;
    }
    else {
      new Location(path).delete();
    }
    checkedPaths.put(path, true);
    return true;
  }

  private void setCodecOptions(IFormatWriter writer) {
    if (compressionQuality != null) {
      CodecOptions codecOptions = JPEG2000CodecOptions.getDefaultOptions();
      codecOptions.quality = compressionQuality;
      writer.setCodecOptions(codecOptions);
    }
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    DebugTools.enableLogging("INFO");
    ImageConverter converter = new ImageConverter();
    if (!converter.testConvert(new ImageWriter(), args)) System.exit(1);
    System.exit(0);
  }

}
