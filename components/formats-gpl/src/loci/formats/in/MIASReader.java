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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.Region;
import loci.formats.CoreMetadata;
import loci.formats.FilePattern;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.ImageTools;
import loci.formats.MetadataTools;
import loci.formats.codec.BitWriter;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffParser;

import ome.xml.model.primitives.Color;
import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.UNITS;

/**
 * MIASReader is the file format reader for Maia Scientific MIAS-2 datasets.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class MIASReader extends FormatReader {

  // -- Fields --

  /** TIFF files - indexed by well and file. */
  private String[][] tiffs;

  /** Delegate readers. */
  private MinimalTiffReader[][] readers;

  /** Path to file containing analysis results for all plates. */
  private String resultFile = null;

  private List<AnalysisFile> analysisFiles;
  private List<AnalysisFile> roiFiles;

  private int[] wellNumber;

  private int tileRows, tileCols;
  private int tileWidth, tileHeight;

  private int wellColumns;
  private int[] bpp;

  private String templateFile;

  private final Map<String, String> overlayFiles =
    new HashMap<String, String>();
  private final Map<String, Integer> overlayPlanes =
    new HashMap<String, Integer>();

  /** Whether or not mask pixel data should be parsed in setId. */
  private boolean parseMasks = false;

  /** Cached tile buffer to avoid re-allocations when reading tiles. */
  private byte[] cachedTileBuffer;

  // -- Constructor --

  /** Constructs a new MIAS reader. */
  public MIASReader() {
    super("MIAS", new String[] {"tif", "tiff", "txt"});
    suffixSufficient = false;
    domains = new String[] {FormatTools.HCS_DOMAIN};
    hasCompanionFiles = true;
    datasetDescription = "One directory per plate containing one directory " +
      "per well, each with one or more .tif/.tiff files";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getRequiredDirectories(String[]) */
  @Override
  public int getRequiredDirectories(String[] files)
    throws FormatException, IOException
  {
    final StringBuilder commonParent = new StringBuilder();

    int dirCount = 0;

    String[] dirs = files[0].split(File.separatorChar == '/' ? "/" : "\\\\");
    for (String dir : dirs) {
      boolean canAppend = true;
      for (String f : files) {
        if (!f.startsWith(commonParent.toString() + dir)) {
          canAppend = false;
          break;
        }
      }

      if (canAppend) {
        commonParent.append(dir);
        commonParent.append(File.separator);
        dirCount++;
      }
    }

    int maxDirCount = 0;
    for (String f : files) {
      int parentDirCount =
        f.split(File.separatorChar == '/' ? "/" : "\\\\").length - 1;
      if (parentDirCount > maxDirCount) {
        maxDirCount = parentDirCount;
      }
    }

    return (int) Math.max(3 - (maxDirCount - dirCount), 0);
  }

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String filename, boolean open) {
    if (!open) return super.isThisType(filename, open); // no file system access

    Location baseFile = new Location(filename).getAbsoluteFile();
    Location wellDir = baseFile.getParentFile();
    String wellName = wellDir.getName();

    if (checkSuffix(filename, "txt")) {
      String name = baseFile.getName();
      return wellName.equals("results") || wellName.equals("Batchresults") ||
        name.equals("Nugenesistemplate.txt") || name.startsWith("mode");
    }

    Location experiment = null;
    try {
      experiment = wellDir.getParentFile().getParentFile();
    }
    catch (NullPointerException e) { }

    if (experiment == null) return false;

    boolean validName =
      wellName.startsWith("Well") || wellName.equals("results") ||
      (wellName.length() == 1 && wellName.replaceAll("\\d", "").length() == 0);
    return validName && super.isThisType(filename, open);
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    TiffParser tp = new TiffParser(stream);
    IFD ifd = tp.getFirstIFD();
    if (ifd == null) return false;

    Object s = ifd.getIFDValue(IFD.SOFTWARE);
    if (s == null) return false;
    String software = null;
    if (s instanceof String[]) software = ((String[]) s)[0];
    else software = s.toString();

    return software.startsWith("eaZYX") || software.startsWith("SCIL_Image") ||
      software.startsWith("IDL");
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  @Override
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    if (readers == null || readers[0][0].getCurrentFile() == null) {
      return null;
    }
    return readers[0][0].get8BitLookupTable();
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  @Override
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    if (readers == null || readers[0][0].getCurrentFile() == null) {
      return null;
    }
    return readers[0][0].get16BitLookupTable();
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    if (tileRows == 1 && tileCols == 1) {
      readers[getSeries()][no].setId(tiffs[getSeries()][no]);
      readers[getSeries()][no].openBytes(0, buf, x, y, w, h);
      readers[getSeries()][no].close();
      return buf;
    }

    int outputRowLen = w * bpp[getSeries()];

    Region image = new Region(x, y, w, h);
    int outputRow = 0, outputCol = 0;
    Region intersection = null;

    byte[] tileBuf = null;

    for (int row=0; row<tileRows; row++) {
      for (int col=0; col<tileCols; col++) {
        Region tile =
          new Region(col * tileWidth, row * tileHeight, tileWidth, tileHeight);
        if (!tile.intersects(image)) continue;

        intersection = tile.intersection(image);

        int tileIndex = (no * tileRows + row) * tileCols + col;
        tileBuf = getTile(getSeries(), no, row, col, intersection);

        int rowLen = tileBuf.length / intersection.height;

        // copy tile into output image
        int outputOffset = outputRow * outputRowLen + outputCol;
        for (int trow=0; trow<intersection.height; trow++) {
          System.arraycopy(tileBuf, trow * rowLen, buf, outputOffset, rowLen);
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

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    final List<String> files = new ArrayList<String>();

    if (!noPixels && tiffs != null) {
      String[] f = new String[tiffs[getSeries()].length];
      System.arraycopy(tiffs[getSeries()], 0, f, 0, f.length);
      Arrays.sort(f);
      files.addAll(Arrays.asList(f));
    }

    if (analysisFiles != null) {
      for (AnalysisFile file : analysisFiles) {
        if (file.plate <= 0 && (file.well == getSeries() || file.well < 0 ||
          wellNumber[getSeries()] == file.well))
        {
          files.add(file.filename);
        }
      }
    }

    if (templateFile != null) files.add(templateFile);
    return files.toArray(new String[files.size()]);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (readers != null) {
      for (MinimalTiffReader[] images : readers) {
        for (MinimalTiffReader r : images) {
          if (r != null) r.close(fileOnly);
        }
      }
    }
    if (!fileOnly) {
      readers = null;
      tiffs = null;
      tileRows = tileCols = 0;
      resultFile = null;
      analysisFiles = null;
      wellNumber = null;
      tileWidth = tileHeight = 0;
      wellColumns = 0;
      bpp = null;
      cachedTileBuffer = null;
      templateFile = null;
      overlayFiles.clear();
      overlayPlanes.clear();
      roiFiles = null;
    }
  }

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    try {
      readers[0][0].setId(tiffs[0][0]);
      return readers[0][0].getOptimalTileWidth();
    }
    catch (FormatException e) {
      LOGGER.debug("", e);
    }
    catch (IOException e) {
      LOGGER.debug("", e);
    }
    return super.getOptimalTileWidth();
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    try {
      readers[0][0].setId(tiffs[0][0]);
      return readers[0][0].getOptimalTileHeight();
    }
    catch (FormatException e) {
      LOGGER.debug("", e);
    }
    catch (IOException e) {
      LOGGER.debug("", e);
    }
    return super.getOptimalTileHeight();
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    if (checkSuffix(id, "txt")) {
      // first need to find a relevant TIFF file

      Location base = new Location(id).getAbsoluteFile();
      Location plate = null;
      if (base.getParentFile().getName().equals("Batchresults")) {
        Location experiment = base.getParentFile().getParentFile();
        String[] plates = experiment.list(true);
        Arrays.sort(plates);
        plate = new Location(experiment, plates[0]);
      }
      else {
        plate = base.getParentFile();
        if (plate.getName().equals("results")) plate = plate.getParentFile();
      }

      String[] list = plate.list(true);
      for (String f : list) {
        if (f.startsWith("Well")) {
          Location well = new Location(plate, f);
          String[] wellList = well.list(true);
          for (String file : wellList) {
            String path = new Location(well, file).getAbsolutePath();
            if (isThisType(path) &&
              checkSuffix(path, new String[] {"tif", "tiff"}))
            {
              initFile(path);
              return;
            }
          }
        }
      }

      throw new FormatException("Could not locate an appropriate TIFF file.");
    }

    if (!isGroupFiles()) {
      tiffs = new String[][] {{id}};
      readers = new MinimalTiffReader[1][1];
      readers[0][0] = new MinimalTiffReader();

      TiffReader r = new TiffReader();
      r.setMetadataStore(getMetadataStore());
      r.setId(tiffs[0][0]);
      core = new ArrayList<CoreMetadata>(r.getCoreMetadataList());
      metadataStore = r.getMetadataStore();

      final Map<String, Object> globalMetadata = r.getGlobalMetadata();
      for (final Map.Entry<String, Object> entry : globalMetadata.entrySet()) {
        addGlobalMeta(entry.getKey(), entry.getValue());
      }

      r.close();

      tileRows = 1;
      tileCols = 1;

      return;
    }

    analysisFiles = new ArrayList<AnalysisFile>();

    // MIAS is a high content screening format which supports multiple plates,
    // wells and fields.
    // Most of the metadata comes from the directory hierarchy, as very little
    // metadata is present in the actual files.
    //
    // The directory hierarchy is either:
    //
    // <experiment name>                        top level experiment directory
    //   Batchresults                           analysis results for experiment
    //   <plate number>_<plate barcode>         one directory for each plate
    //     results                              analysis results for plate
    //     Well<xxxx>                           one directory for each well
    //       mode<x>_z<xxx>_t<xxx>_im<x>_<x>.tif
    //
    // or:
    //
    // <experiment name>                        top level experiment directory
    //   <plate number>                         plate directory (3 digits)
    //     <well number>                        well directory (4 digits)
    //       <channel number>                   channel directory (1 digit)
    //         <tile row>_<tile col>_<Z>_<T>.tif
    //
    // Each TIFF file contains a single grayscale plane.  The "mode" block
    // refers to the channel number; the "z" and "t" blocks refer to the
    // Z section and timepoint, respectively.  The "im<x>_<x>" block gives
    // the row and column coordinates of the image within a mosaic.
    //
    // We are initially given one of these TIFF files; from there, we need
    // to find the top level experiment directory and work our way down to
    // determine how many plates and wells are present.

    LOGGER.info("Building list of TIFF files");

    Location baseFile = new Location(id).getAbsoluteFile();
    Location plate = baseFile.getParentFile().getParentFile();

    String plateName = plate.getName();
    if (!(plateName.length() == 3 || (plateName.length() > 3 &&
      plateName.replaceAll("\\d", "").startsWith("-"))))
    {
      plate = plate.getParentFile();
      plateName = plate.getName();
    }

    int plateNumber = Integer.parseInt(plateName.substring(0, 3));

    Location experiment = plate.getParentFile();
    String[] directories = experiment.list(true);
    Arrays.sort(directories);
    for (String dir : directories) {
      Location f = new Location(experiment, dir);
      if (dir.equals("Batchresults")) {
        String[] results = f.list(true);
        for (String result : results) {
          Location file = new Location(f, result);
          if (result.startsWith("NEO_Results")) {
            resultFile = file.getAbsolutePath();
            AnalysisFile af = new AnalysisFile();
            af.filename = resultFile;
            analysisFiles.add(af);
          }
          else if (result.startsWith("NEO_PlateOutput_")) {
            int plateIndex = Integer.parseInt(result.substring(16, 19));
            if (plateIndex == plateNumber) {
              AnalysisFile af = new AnalysisFile();
              af.filename = file.getAbsolutePath();
              af.plate = 0;
              analysisFiles.add(af);
            }
          }
        }
      }
    }

    String[] list = plate.list(true);
    Arrays.sort(list);
    final List<String> wellDirectories = new ArrayList<String>();
    for (String dir : list) {
      Location f = new Location(plate, dir);
      if (f.getName().startsWith("Well") || f.getName().length() == 4) {
        // directory name is valid, but we need to make sure that the
        // directory contains a TIFF or a subdirectory
        String[] wellList = f.list(true);
        if (wellList != null) {
          boolean validWell = false;
          for (String potentialTIFF : wellList) {
            if (potentialTIFF.toLowerCase().endsWith(".tif") ||
              new Location(f, potentialTIFF).isDirectory()) {
              validWell = true;
              break;
            }
          }
          if (validWell) wellDirectories.add(f.getAbsolutePath());
        }
      }
      else if (f.getName().equals("results")) {
        String[] resultsList = f.list(true);
        for (String result : resultsList) {
          // exclude proprietary program state files
          if (!result.endsWith(".sav") && !result.endsWith(".dsv") &&
            !result.endsWith(".dat"))
          {
            Location r = new Location(f, result);
            AnalysisFile af = new AnalysisFile();
            af.filename = r.getAbsolutePath();
            af.plate = 0;
            if (result.toLowerCase().startsWith("well")) {
              af.well = Integer.parseInt(result.substring(4, 8)) - 1;
            }
            analysisFiles.add(af);
          }
        }
      }
      else if (f.getName().equals("Nugenesistemplate.txt")) {
        templateFile = f.getAbsolutePath();
      }
    }

    int nWells = wellDirectories.size();

    LOGGER.debug("Found {} wells.", nWells);

    readers = new MinimalTiffReader[nWells][];
    tiffs = new String[nWells][];
    int[] zCount = new int[nWells];
    int[] cCount = new int[nWells];
    int[] tCount = new int[nWells];
    String[] order = new String[nWells];
    wellNumber = new int[nWells];

    String[] wells = wellDirectories.toArray(new String[nWells]);
    Arrays.sort(wells);

    for (int j=0; j<nWells; j++) {
      Location well = new Location(wells[j]);
      String wellName = well.getName().replaceAll("Well", "");
      wellNumber[j] = Integer.parseInt(wellName) - 1;

      String[] tiffFiles = well.list(true);
      final List<String> tmpFiles = new ArrayList<String>();
      for (String tiff : tiffFiles) {
        String name = tiff.toLowerCase();
        if (name.endsWith(".tif") || name.endsWith(".tiff")) {
          tmpFiles.add(new Location(well, tiff).getAbsolutePath());
        }
      }

      if (tmpFiles.size() == 0) {
        LOGGER.debug("No TIFFs in well directory {}", wells[j]);
        // no TIFFs in the well directory, so there are probably channel
        // directories which contain the TIFFs
        for (String dir : tiffFiles) {
          Location file = new Location(well, dir);
          if (dir.length() == 1 && file.isDirectory()) {
            cCount[j]++;

            String[] tiffs = file.list(true);
            for (String tiff : tiffs) {
              String name = tiff.toLowerCase();
              if (name.endsWith(".tif") || name.endsWith(".tiff")) {
                tmpFiles.add(new Location(file, tiff).getAbsolutePath());
              }
            }
          }
        }
      }

      tiffFiles = tmpFiles.toArray(new String[0]);

      Location firstTiff = new Location(tiffFiles[0]);

      List<String> names = new ArrayList<String>();
      for (Location f: firstTiff.getParentFile().listFiles()) {
        names.add(f.getName());
      }

      FilePattern fp = new FilePattern(FilePattern.findPattern(
        firstTiff.getName(), null, names.toArray(new String[names.size()])));
      String[] blocks = fp.getPrefixes();

      order[j] = "XY";

      int[] count = fp.getCount();

      for (int block=blocks.length - 1; block>=0; block--) {
        blocks[block] = blocks[block].toLowerCase();
        blocks[block] =
          blocks[block].substring(blocks[block].lastIndexOf("_") + 1);

        if (blocks[block].equals("z")) {
          zCount[j] = count[block];
          order[j] += 'Z';
        }
        else if (blocks[block].equals("t")) {
          tCount[j] = count[block];
          order[j] += 'T';
        }
        else if (blocks[block].equals("mode")) {
          cCount[j] = count[block];
          order[j] += 'C';
        }
        else if (blocks[block].equals("im")) tileRows = count[block];
        else if (blocks[block].equals("")) tileCols = count[block];
        else if (blocks[block].replaceAll("\\d", "").length() == 0) {
          if (block == 3) tileRows = count[block];
          else if (block == 2) tileCols = count[block];
          else if (block == 0) {
            zCount[j] = count[block];
            order[j] += 'Z';
          }
          else if (block == 1) {
            tCount[j] = count[block];
            order[j] += 'T';
          }
        }
        else {
          throw new FormatException("Unsupported block '" + blocks[block]);
        }
      }

      Arrays.sort(tiffFiles);
      tiffs[j] = tiffFiles;
      LOGGER.debug("Well {} has {} files.", j, tiffFiles.length);
      readers[j] = new MinimalTiffReader[tiffFiles.length];
      for (int k=0; k<tiffFiles.length; k++) {
        readers[j][k] = new MinimalTiffReader();
      }
    }

    // Populate core metadata

    LOGGER.info("Populating core metadata");

    int nSeries = tiffs.length;

    bpp = new int[nSeries];

    if (readers.length == 0) {
      throw new FormatException("No wells were found.");
    }

    // assume that all wells have the same width, height, and pixel type
    readers[0][0].setId(tiffs[0][0]);
    tileWidth = readers[0][0].getSizeX();
    tileHeight = readers[0][0].getSizeY();

    if (tileCols == 0) tileCols = 1;
    if (tileRows == 0) tileRows = 1;

    core.clear();
    for (int i=0; i<nSeries; i++) {
      CoreMetadata ms = new CoreMetadata();
      core.add(ms);

      ms.sizeZ = zCount[i];
      ms.sizeC = cCount[i];
      ms.sizeT = tCount[i];

      if (ms.sizeZ == 0) ms.sizeZ = 1;
      if (ms.sizeC == 0) ms.sizeC = 1;
      if (ms.sizeT == 0) ms.sizeT = 1;

      ms.sizeX = tileWidth * tileCols;
      ms.sizeY = tileHeight * tileRows;
      ms.pixelType = readers[0][0].getPixelType();
      ms.sizeC *= readers[0][0].getSizeC();
      ms.rgb = readers[0][0].isRGB();
      ms.littleEndian = readers[0][0].isLittleEndian();
      ms.interleaved = readers[0][0].isInterleaved();
      ms.indexed = readers[0][0].isIndexed();
      ms.falseColor = readers[0][0].isFalseColor();
      ms.dimensionOrder = order[i];

      if (ms.dimensionOrder.indexOf('Z') == -1) {
        ms.dimensionOrder += 'Z';
      }
      if (ms.dimensionOrder.indexOf('C') == -1) {
        ms.dimensionOrder += 'C';
      }
      if (ms.dimensionOrder.indexOf('T') == -1) {
        ms.dimensionOrder += 'T';
      }

      ms.imageCount = ms.sizeZ * ms.sizeT * cCount[i];
      if (ms.imageCount == 0) {
        ms.imageCount = 1;
      }
      bpp[i] = FormatTools.getBytesPerPixel(ms.pixelType);
    }

    // Populate metadata hashtable

    LOGGER.info("Populating metadata hashtable");

    if (resultFile != null &&
      getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM)
    {
      String[] cols = null;
      final List<String> rows = new ArrayList<String>();

      boolean doKeyValue = true;
      int nStarLines = 0;

      String analysisResults = DataTools.readFile(resultFile);
      String[] lines = analysisResults.split("\n");

      for (String line : lines) {
        line = line.trim();
        if (line.length() == 0) continue;
        if (line.startsWith("******") && line.endsWith("******")) nStarLines++;

        if (doKeyValue) {
          String[] n = line.split("\t");
          if (n[0].endsWith(":")) n[0] = n[0].substring(0, n[0].length() - 1);
          if (n.length >= 2) addGlobalMeta(n[0], n[1]);
        }
        else {
          if (cols == null) cols = line.split("\t");
          else rows.add(line);
        }
        if (nStarLines == 2) doKeyValue = false;
      }

      for (String row : rows) {
        String[] d = row.split("\t");
        for (int col=3; col<cols.length; col++) {
          addGlobalMeta("Plate " + d[0] + ", Well " + d[2] + " " + cols[col],
            d[col]);

          if (cols[col].equals("AreaCode")) {
            String wellID = d[col].replaceAll("\\D", "");
            wellColumns = Integer.parseInt(wellID);
          }
        }
      }
    }

    // Populate MetadataStore

    LOGGER.info("Populating MetadataStore");

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    // HACK: if we don't have the analysis file, we don't how many
    // rows/columns are in the plate
    //
    // assume that a 96 well plate is 8x12, and a 384 well plate is 16x24
    if (wellColumns == 0) {
      if (nWells == 96) {
        wellColumns = 12;
      }
      else if (nWells == 384) {
        wellColumns = 24;
      }
      else {
        LOGGER.warn("Could not determine the plate dimensions.");
        wellColumns = 24;
      }
    }

    store.setPlateID(MetadataTools.createLSID("Plate", 0), 0);

    String plateAcqId = MetadataTools.createLSID("PlateAcquisition", 0, 0);
    store.setPlateAcquisitionID(plateAcqId, 0, 0);
    store.setPlateAcquisitionMaximumFieldCount(new PositiveInteger(1), 0, 0);

    for (int well=0; well<nWells; well++) {
      int wellIndex = wellNumber[well];

      int row = wellIndex / wellColumns;
      int wellCol = (wellIndex % wellColumns) + 1;
      char wellRow = (char) ('A' + row);

      store.setWellID(MetadataTools.createLSID("Well", 0, well), 0, well);
      store.setWellRow(new NonNegativeInteger(row), 0, well);
      store.setWellColumn(new NonNegativeInteger(wellCol - 1), 0, well);

      String imageID = MetadataTools.createLSID("Image", well);
      String wellSampleID = MetadataTools.createLSID("WellSample", 0, well, 0);
      store.setWellSampleID(wellSampleID, 0, well, 0);
      store.setWellSampleIndex(new NonNegativeInteger(well), 0, well, 0);

      store.setImageID(imageID, well);
      store.setImageName("Well " + wellRow + wellCol, well);
      store.setWellSampleImageRef(imageID, 0, well, 0);

      store.setPlateAcquisitionWellSampleRef(wellSampleID, 0, 0, well);
    }

    MetadataLevel level = getMetadataOptions().getMetadataLevel();

    if (level != MetadataLevel.MINIMUM) {
      String experimentID = MetadataTools.createLSID("Experiment", 0);
      store.setExperimentID(experimentID, 0);
      store.setExperimentType(getExperimentType("Other"), 0);
      store.setExperimentDescription(experiment.getName(), 0);

      // populate SPW metadata
      store.setPlateColumnNamingConvention(getNamingConvention("Number"), 0);
      store.setPlateRowNamingConvention(getNamingConvention("Letter"), 0);

      parseTemplateFile(store);

      plateName = plateName.substring(plateName.indexOf('-') + 1);
      store.setPlateName(plateName, 0);
      store.setPlateExternalIdentifier(plateName, 0);

      for (int well=0; well<nWells; well++) {
        // populate Image/Pixels metadata
        store.setImageExperimentRef(experimentID, well);

        String instrumentID = MetadataTools.createLSID("Instrument", 0);
        store.setInstrumentID(instrumentID, 0);
        store.setImageInstrumentRef(instrumentID, well);
      }

      roiFiles = new ArrayList<AnalysisFile>();
      for (AnalysisFile af : analysisFiles) {
        String file = af.filename;
        String name = new Location(file).getName();
        if (!name.startsWith("Well")) continue;

        if (name.endsWith("AllModesOverlay.tif")) {
          roiFiles.add(af);
        }
        else if (name.endsWith("overlay.tif")) {
          roiFiles.add(af);
        }
      }

      if (level != MetadataLevel.NO_OVERLAYS) {
        // populate image-level ROIs
        Color[] colors = new Color[getSizeC()];

        int nextROI = 0;
        for (AnalysisFile af : analysisFiles) {
          String file = af.filename;
          String name = new Location(file).getName();
          if (!name.startsWith("Well")) continue;

          int[] position = getPositionFromFile(file);
          int well = position[0];

          if (name.endsWith("detail.txt")) {
            String data = DataTools.readFile(file);
            String[] lines = data.split("\n");
            int start = 0;
            while (start < lines.length && !lines[start].startsWith("Label")) {
              start++;
            }
            if (start >= lines.length) continue;

            String[] columns = lines[start].split("\t");
            List<String> columnNames = Arrays.asList(columns);

            for (int j=start+1; j<lines.length; j++) {
              populateROI(columnNames, lines[j].split("\t"), well,
                nextROI++, position[1], position[2], store);
            }
          }
          else if (name.endsWith("AllModesOverlay.tif")) {
            // original color for each channel is stored in
            // results/Well<nnnn>_mode<n>_z<nnn>_t<nnn>_AllModesOverlay.tif
            if (colors[position[3]] != null) continue;
            try {
              colors[position[3]] = getChannelColorFromFile(file);
            }
            catch (IOException e) { }
            if (colors[position[3]] == null) continue;

            for (int s=0; s<getSeriesCount(); s++) {
              store.setChannelColor(colors[position[3]], s, position[3]);
            }
            if (position[3] == 0) {
              nextROI += parseMasks(store, well, nextROI, file);
            }
          }
          else if (name.endsWith("overlay.tif")) {
            nextROI += parseMasks(store, well, nextROI, file);
          }
        }
      }
    }
  }

  // -- Helper methods --

  /**
   * Get the color associated with the given file's channel.
   * The file must be one of the
   * Well<nnnn>_mode<n>_z<nnn>_t<nnn>_AllModesOverlay.tif
   * files in <experiment>/<plate>/results/
   */
  private Color getChannelColorFromFile(String file)
    throws FormatException, IOException
  {
    RandomAccessInputStream s = new RandomAccessInputStream(file, 16);
    TiffParser tp = new TiffParser(s);
    IFD ifd = tp.getFirstIFD();
    s.close();
    if (ifd == null) return null;
    int[] colorMap = tp.getColorMap(ifd);
    if (colorMap == null) return null;

    int nEntries = colorMap.length / 3;
    int max = Integer.MIN_VALUE;
    int maxIndex = -1;
    for (int c=0; c<3; c++) {
      int v = (colorMap[c * nEntries] >> 8) & 0xff;
      if (v > max) {
        max = v;
        maxIndex = c;
      }
      else if (v == max) {
        return new Color(0, 0, 0, 255);
      }
    }

    switch (maxIndex) {
      case 0: // red
        return new Color(255, 0, 0, 255);
      case 1: // green
        return new Color(0, 255, 0, 255);
      case 2: // blue
        return new Color(0, 0, 255, 255);
    }
    return null;
  }

  /**
   * Returns an array of length 5 that contains the well, time point,
   * Z and channel indices corresponding to the given analysis file.
   */
  private int[] getPositionFromFile(String file) {
    int[] position = new int[4];

    file = file.substring(file.lastIndexOf(File.separator) + 1);
    String wellIndex = file.substring(4, file.indexOf('_'));
    position[0] = Integer.parseInt(wellIndex) - 1;

    int tIndex = file.indexOf("_t") + 2;
    String t = file.substring(tIndex, file.indexOf("_", tIndex));
    position[1] = Integer.parseInt(t);

    int zIndex = file.indexOf("_z") + 2;
    String zValue = file.substring(zIndex, file.indexOf("_", zIndex));
    position[2] = Integer.parseInt(zValue);

    int cIndex = file.indexOf("mode") + 4;
    String cValue = file.substring(cIndex, file.indexOf("_", cIndex));
    position[3] = Integer.parseInt(cValue) - 1;

    return position;
  }

  private void populateROI(List<String> columns, String[] data, int series,
    int roi, int time, int z, MetadataStore store)
  {
    String roiID = MetadataTools.createLSID("ROI", roi, 0);
    store.setROIID(roiID, roi);
    store.setImageROIRef(roiID, series, roi);

    store.setEllipseID(MetadataTools.createLSID("Shape", roi, 0), roi, 0);
    store.setEllipseTheT(new NonNegativeInteger(time), roi, 0);
    store.setEllipseTheZ(new NonNegativeInteger(z), roi, 0);
    store.setEllipseX(new Double(data[columns.indexOf("Col")]), roi, 0);
    store.setEllipseY(new Double(data[columns.indexOf("Row")]), roi, 0);
    store.setEllipseText(data[columns.indexOf("Label")], roi, 0);

    double diam = Double.parseDouble(data[columns.indexOf("Cell Diam.")]);
    double radius = diam / 2;

    store.setEllipseRadiusX(radius, roi, 0);
    store.setEllipseRadiusY(radius, roi, 0);

    // NB: other attributes are "Nucleus Area", "Cell Type", and
    // "Mean Nucleus Intens."
  }

  private byte[] getTile(int well, int no, int row, int col,
    Region intersection) throws FormatException, IOException
  {
    intersection.x %= tileWidth;
    intersection.y %= tileHeight;

    int tileIndex = (no * tileRows + row) * tileCols + col;

    readers[well][tileIndex].setId(tiffs[well][tileIndex]);
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int ch = getRGBChannelCount();
    int bufferSize = intersection.width * intersection.height * ch * bpp;
    if (cachedTileBuffer == null || cachedTileBuffer.length != bufferSize) {
      cachedTileBuffer = new byte[bufferSize];
    }
    byte[] buf = readers[well][tileIndex].openBytes(0, cachedTileBuffer,
      intersection.x, intersection.y, intersection.width, intersection.height);
    readers[well][tileIndex].close();
    return buf;
  }

  /** Parse metadata from the Nugenesistemplate.txt file. */
  private void parseTemplateFile(MetadataStore store) throws IOException {
    if (templateFile == null) return;

    Double physicalSizeX = null, physicalSizeY = null, exposure = null;
    final List<String> channelNames = new ArrayList<String>();
    String date = null;

    String data = DataTools.readFile(templateFile);
    String[] lines = data.split("\r\n");

    for (String line : lines) {
      int eq = line.indexOf('=');
      if (eq != -1) {
        String key = line.substring(0, eq);
        String value = line.substring(eq + 1);

        if (key.equals("Barcode")) {
          store.setPlateExternalIdentifier(value, 0);
        }
        else if (key.equals("Carrier")) {
          store.setPlateName(value, 0);
        }
        else if (key.equals("Pixel_X")) {
          physicalSizeX = new Double(value);
        }
        else if (key.equals("Pixel_Y")) {
          physicalSizeY = new Double(value);
        }
        else if (key.equals("Objective_ID")) {
          store.setObjectiveID(
            MetadataTools.createLSID("Objective", 0, 0), 0, 0);
          store.setObjectiveModel(value, 0, 0);
        }
        else if (key.equals("Magnification")) {
          Double mag = Double.parseDouble(value);
          store.setObjectiveNominalMagnification(mag, 0, 0);
        }
        else if (key.startsWith("Mode_")) {
          channelNames.add(value);
        }
        else if (key.equals("Date")) {
          date = value;
        }
        else if (key.equals("Time")) {
          date += " " + value;
        }
        else if (key.equals("Exposure")) {
          exposure = new Double(value);
        }
      }
    }

    for (int well=0; well<tiffs.length; well++) {
      Length sizeX = FormatTools.getPhysicalSizeX(physicalSizeX);
      Length sizeY = FormatTools.getPhysicalSizeY(physicalSizeY);

      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, well);
      }
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, well);
      }
      for (int c=0; c<channelNames.size(); c++) {
        if (c < getEffectiveSizeC()) {
          store.setChannelName(channelNames.get(c), well, c);
        }
      }
      date = DateTools.formatDate(date, "dd/MM/yyyy HH:mm:ss");
      if (date != null) {
        store.setImageAcquisitionDate(new Timestamp(date), well);
      }

      if (exposure != null) {
        for (int i=0; i<getImageCount(); i++) {
          store.setPlaneExposureTime(new Time(exposure, UNITS.SECOND), well, i);
        }
      }
    }
  }

  /**
   * Parse masks into a separate overlay-specific MetadataStore.
   */
  public void parseMasks(MetadataStore overlayStore)
    throws FormatException, IOException
  {
    boolean originalMaskParsing = parseMasks;
    int roi = 0;
    parseMasks = true;
    for (AnalysisFile roiFile : roiFiles) {
      roi += parseMasks(overlayStore, roiFile.well, roi, roiFile.filename);
    }
    parseMasks = originalMaskParsing;
  }

  /**
   * Parse Mask ROIs from the given TIFF and place them in the given
   * MetadataStore.
   * @return the number of masks parsed
   */
  private int parseMasks(MetadataStore store, int series, int roi,
    String overlayTiff) throws FormatException, IOException
  {
    if (!parseMasks || series >= getSeriesCount()) return 0;
    int nOverlays = 0;
    for (int i=0; i<3; i++) {
      String roiId = MetadataTools.createLSID("ROI", series, roi + nOverlays);
      String maskId =
        MetadataTools.createLSID("Mask", series, roi + nOverlays, 0);
      overlayFiles.put(maskId, overlayTiff);
      overlayPlanes.put(maskId, i);

      boolean validMask = populateMaskPixels(series, roi + nOverlays, 0, store);
      if (validMask) {
        store.setROIID(roiId, roi + nOverlays);

        String maskID = MetadataTools.createLSID("Shape", roi + nOverlays, 0);
        store.setMaskID(maskID, roi + nOverlays, 0);
        store.setMaskX(0d, roi + nOverlays, 0);
        store.setMaskY(0d, roi + nOverlays, 0);
        store.setMaskWidth((double) getSizeX(), roi + nOverlays, 0);
        store.setMaskHeight((double) getSizeY(), roi + nOverlays, 0);

        int color = 0xff000000 | (0xff << (8 * (2 - i)));
        store.setMaskStrokeColor(new Color(color), roi + nOverlays, 0);
        store.setMaskFillColor(new Color(color), roi + nOverlays, 0);
        store.setImageROIRef(roiId, series, roi + nOverlays);
        nOverlays++;
      }
    }
    return nOverlays;
  }

  // -- MIASReader API methods --

  /**
   * Populate the MaskPixels.BinData attribute for the Mask identified by the
   * given Image index, ROI index, and Shape index.
   * @return true if the mask was populated successfully.
   */
  public boolean populateMaskPixels(int imageIndex, int roiIndex,
    int shapeIndex, MetadataStore store)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);

    String id =
      MetadataTools.createLSID("Mask", imageIndex, roiIndex, shapeIndex);
    String maskFile = overlayFiles.get(id);
    if (maskFile == null) {
      LOGGER.warn("Could not find an overlay file matching {}", id);
      return false;
    }

    MinimalTiffReader r = new MinimalTiffReader();
    r.setId(maskFile);

    int index = overlayPlanes.get(id).intValue();
    byte[] plane = r.openBytes(0);
    byte[][] planes = null;

    if (r.isIndexed()) {
      planes = ImageTools.indexedToRGB(r.get8BitLookupTable(), plane);
    }
    else {
      int bpp = FormatTools.getBytesPerPixel(r.getPixelType());
      planes = new byte[r.getRGBChannelCount()][];
      for (int c=0; c<planes.length; c++) {
        planes[c] = ImageTools.splitChannels(plane, c, r.getRGBChannelCount(),
          bpp, false, r.isInterleaved());
      }
    }
    r.close();

    for (int i=0; i<planes[0].length; i++) {
      boolean channelsEqual = true;
      for (int c=1; c<planes.length; c++) {
        if (planes[c][i] != planes[0][i]) {
          channelsEqual = false;
          break;
        }
      }
      if (channelsEqual) {
        for (int c=0; c<planes.length; c++) {
          planes[c][i] = 0;
        }
      }
    }

    // threshold and binary encode the pixel data

    boolean validMask = false;
    BitWriter bits = null;
    if (planes.length > index) {
      bits = new BitWriter(planes[index].length / 8);
      for (int p=0; p<planes[index].length; p++) {
        bits.write(planes[index][p] == 0 ? 0 : 1, 1);
        if (planes[index][p] != 0) {
          validMask = true;
        }
      }
    }

    if (validMask) {
      store.setMaskBinData(bits.toByteArray(), roiIndex, shapeIndex);
    }
    else LOGGER.debug("Did not populate MaskPixels.BinData for {}", id);

    return validMask;
  }

  /**
   * Toggle whether or not Mask pixel data should be parsed in setId.
   * By default, it is not parsed.
   */
  public void setAutomaticallyParseMasks(boolean parse) throws FormatException {
    FormatTools.assertId(currentId, false, 1);
    this.parseMasks = parse;
  }

  // -- Helper class --

  class AnalysisFile {
    public String filename;
    public int plate = -1, well = -1;
  }

}
