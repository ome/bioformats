//
// MIASReader.java
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

package loci.formats.in;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

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
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffParser;

/**
 * MIASReader is the file format reader for Maia Scientific MIAS-2 datasets.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/MIASReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/MIASReader.java">SVN</a></dd></dl>
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

  private Vector<AnalysisFile> analysisFiles;

  private int[] wellNumber;

  private int tileRows, tileCols;
  private int tileWidth, tileHeight;

  private int wellColumns;
  private int[] bpp;

  private String templateFile;

  private Hashtable<String, String> overlayFiles =
    new Hashtable<String, String>();
  private Hashtable<String, Integer> overlayPlanes =
    new Hashtable<String, Integer>();

  /** Whether or not mask pixel data should be parsed in setId. */
  private boolean parseMasks = false;

  /** Cached tile buffer to avoid re-allocations when reading tiles. */
  private byte[] cachedTileBuffer;

  // -- Constructor --

  /** Constructs a new MIAS reader. */
  public MIASReader() {
    super("MIAS", new String[] {"tif", "tiff"});
    suffixSufficient = false;
    domains = new String[] {FormatTools.HCS_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String filename, boolean open) {
    if (!open) return super.isThisType(filename, open); // no file system access

    Location baseFile = new Location(filename).getAbsoluteFile();
    Location wellDir = baseFile.getParentFile();
    Location experiment = null;
    try {
      experiment = wellDir.getParentFile().getParentFile();
    }
    catch (NullPointerException e) { }

    if (experiment == null) return false;

    String wellName = wellDir.getName();
    boolean validName =
      wellName.startsWith("Well") || wellName.equals("results") ||
      (wellName.length() == 1 && wellName.replaceAll("\\d", "").length() == 0);
    return validName && super.isThisType(filename, open);
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
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
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    if (readers == null || readers[0][0].getCurrentFile() == null) {
      return null;
    }
    return readers[0][0].get8BitLookupTable();
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
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
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    if (tileRows == 1 && tileCols == 1) {
      readers[getSeries()][no].setId(tiffs[getSeries()][no]);
      readers[getSeries()][no].openBytes(0, buf, x, y, w, h);
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
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    Vector<String> files = new Vector<String>();

    if (!noPixels) {
      String[] f = new String[tiffs[getSeries()].length];
      System.arraycopy(tiffs[getSeries()], 0, f, 0, f.length);
      Arrays.sort(f);
      files.addAll(Arrays.asList(f));
    }

    for (AnalysisFile file : analysisFiles) {
      if (file.plate <= 0 && (file.well == getSeries() || file.well < 0 ||
        wellNumber[getSeries()] == file.well))
      {
        files.add(file.filename);
      }
    }

    if (templateFile != null) files.add(templateFile);
    return files.toArray(new String[files.size()]);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
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
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    debug("MIASReader.initFile(" + id + ")");
    super.initFile(id);

    analysisFiles = new Vector<AnalysisFile>();

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

    status("Building list of TIFF files");

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
    Vector<String> wellDirectories = new Vector<String>();
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
          if (!result.endsWith(".sav") && !result.endsWith(".dsv")) {
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

    debug("Found " + nWells + " wells.");

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
      Vector<String> tmpFiles = new Vector<String>();
      for (String tiff : tiffFiles) {
        String name = tiff.toLowerCase();
        if (name.endsWith(".tif") || name.endsWith(".tiff")) {
          tmpFiles.add(new Location(well, tiff).getAbsolutePath());
        }
      }

      if (tmpFiles.size() == 0) {
        debug("No TIFFs in well directory " + wells[j]);
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

      FilePattern fp = new FilePattern(
        firstTiff.getName(), firstTiff.getParentFile().getAbsolutePath());
      String[] blocks = fp.getPrefixes();

      order[j] = "XY";

      int[] count = fp.getCount();

      for (int block=blocks.length - 1; block>=0; block--) {
        blocks[block] = blocks[block].toLowerCase();
        blocks[block] =
          blocks[block].substring(blocks[block].lastIndexOf("_") + 1);

        if (blocks[block].equals("z")) {
          zCount[j] = count[block];
          order[j] += "Z";
        }
        else if (blocks[block].equals("t")) {
          tCount[j] = count[block];
          order[j] += "T";
        }
        else if (blocks[block].equals("mode")) {
          cCount[j] = count[block];
          order[j] += "C";
        }
        else if (blocks[block].equals("im")) tileRows = count[block];
        else if (blocks[block].equals("")) tileCols = count[block];
        else if (blocks[block].replaceAll("\\d", "").length() == 0) {
          if (block == 3) tileRows = count[block];
          else if (block == 2) tileCols = count[block];
          else if (block == 0) {
            zCount[j] = count[block];
            order[j] += "Z";
          }
          else if (block == 1) {
            tCount[j] = count[block];
            order[j] += "T";
          }
        }
        else {
          throw new FormatException("Unsupported block '" + blocks[block]);
        }
      }

      Arrays.sort(tiffFiles);
      tiffs[j] = tiffFiles;
      debug("Well " + j + " has " + tiffFiles.length + " files.");
      readers[j] = new MinimalTiffReader[tiffFiles.length];
      for (int k=0; k<tiffFiles.length; k++) {
        readers[j][k] = new MinimalTiffReader();
      }
    }

    // Populate core metadata

    status("Populating core metadata");

    int nSeries = tiffs.length;

    core = new CoreMetadata[nSeries];
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

    for (int i=0; i<core.length; i++) {
      core[i] = new CoreMetadata();

      core[i].sizeZ = zCount[i];
      core[i].sizeC = cCount[i];
      core[i].sizeT = tCount[i];

      if (core[i].sizeZ == 0) core[i].sizeZ = 1;
      if (core[i].sizeC == 0) core[i].sizeC = 1;
      if (core[i].sizeT == 0) core[i].sizeT = 1;

      core[i].sizeX = tileWidth * tileCols;
      core[i].sizeY = tileHeight * tileRows;
      core[i].pixelType = readers[0][0].getPixelType();
      core[i].sizeC *= readers[0][0].getSizeC();
      core[i].rgb = readers[0][0].isRGB();
      core[i].littleEndian = readers[0][0].isLittleEndian();
      core[i].interleaved = readers[0][0].isInterleaved();
      core[i].indexed = readers[0][0].isIndexed();
      core[i].falseColor = readers[0][0].isFalseColor();
      core[i].dimensionOrder = order[i];

      if (core[i].dimensionOrder.indexOf("Z") == -1) {
        core[i].dimensionOrder += "Z";
      }
      if (core[i].dimensionOrder.indexOf("C") == -1) {
        core[i].dimensionOrder += "C";
      }
      if (core[i].dimensionOrder.indexOf("T") == -1) {
        core[i].dimensionOrder += "T";
      }

      core[i].imageCount = core[i].sizeZ * core[i].sizeT * cCount[i];
      if (core[i].imageCount == 0) {
        core[i].imageCount = 1;
      }
      bpp[i] = FormatTools.getBytesPerPixel(core[i].pixelType);
    }

    // Populate metadata hashtable

    status("Populating metadata hashtable");

    if (resultFile != null && isMetadataCollected()) {
      String[] cols = null;
      Vector<String> rows = new Vector<String>();

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

    if (isMetadataCollected()) {
      status("Populating MetadataStore");

      MetadataStore store =
        new FilterMetadata(getMetadataStore(), isMetadataFiltered());
      MetadataTools.populatePixels(store, this, true);

      store.setExperimentID("Experiment:" + experiment.getName(), 0);
      store.setExperimentType("Other", 0);

      // populate SPW metadata
      store.setPlateColumnNamingConvention("1", 0);
      store.setPlateRowNamingConvention("A", 0);

      parseTemplateFile(store);

      plateName = plateName.substring(plateName.indexOf("-") + 1);
      store.setPlateName(plateName, 0);
      store.setPlateExternalIdentifier(plateName, 0);

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
          warn("Could not determine the plate dimensions.");
          wellColumns = 24;
        }
      }

      for (int well=0; well<nWells; well++) {
        int wellIndex = wellNumber[well];

        int row = wellIndex / wellColumns;
        int wellCol = (wellIndex % wellColumns) + 1;

        store.setWellRow(new Integer(row), 0, well);
        store.setWellColumn(new Integer(wellCol - 1), 0, well);

        String imageID = MetadataTools.createLSID("Image", well);
        store.setWellSampleImageRef(imageID, 0, well, 0);
        store.setWellSampleIndex(new Integer(well), 0, well, 0);

        // populate Image/Pixels metadata
        store.setImageExperimentRef("Experiment:" + experiment.getName(), well);
        char wellRow = (char) ('A' + row);

        store.setImageID(imageID, well);
        store.setImageName("Well " + wellRow + wellCol, well);

        String instrumentID = MetadataTools.createLSID("Instrument", 0);
        store.setInstrumentID(instrumentID, 0);
        store.setImageInstrumentRef(instrumentID, well);

        MetadataTools.setDefaultCreationDate(store, id, well);
      }

      // populate image-level ROIs
      String[] colors = new String[getSizeC()];
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
            store.setChannelComponentColorDomain(
              colors[position[3]], s, position[3], 0);
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

  // -- Helper methods --

  /**
   * Get the color associated with the given file's channel.
   * The file must be one of the
   * Well<nnnn>_mode<n>_z<nnn>_t<nnn>_AllModesOverlay.tif
   * files in <experiment>/<plate>/results/
   */
  private String getChannelColorFromFile(String file)
    throws FormatException, IOException
  {
    RandomAccessInputStream s = new RandomAccessInputStream(file);
    TiffParser tp = new TiffParser(s);
    IFD ifd = tp.getFirstIFD();
    s.close();
    int[] colorMap = ifd.getIFDIntArray(IFD.COLOR_MAP, false);
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
        return "gray";
      }
    }

    switch (maxIndex) {
      case 0:
        return "R";
      case 1:
        return "G";
      case 2:
        return "B";
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
    String wellIndex = file.substring(4, file.indexOf("_"));
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
    double cx = Double.parseDouble(data[columns.indexOf("Col")]);
    double cy = Double.parseDouble(data[columns.indexOf("Row")]);

    Integer tv = new Integer(time);
    Integer zv = new Integer(z);

    store.setROIT0(tv, series, roi);
    store.setROIT1(tv, series, roi);
    store.setROIZ0(zv, series, roi);
    store.setROIZ1(zv, series, roi);

    store.setShapeText(data[columns.indexOf("Label")], series, roi, 0);
    store.setShapeTheT(tv, series, roi, 0);
    store.setShapeTheZ(zv, series, roi, 0);
    store.setCircleCx(data[columns.indexOf("Col")], series, roi, 0);
    store.setCircleCy(data[columns.indexOf("Row")], series, roi, 0);

    double diam = Double.parseDouble(data[columns.indexOf("Cell Diam.")]);
    String radius = String.valueOf(diam / 2);

    store.setCircleR(radius, series, roi, 0);

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
    return buf;
  }

  /** Parse metadata from the Nugenesistemplate.txt file. */
  private void parseTemplateFile(MetadataStore store) throws IOException {
    if (templateFile == null) return;

    Float physicalSizeX = null, physicalSizeY = null, exposure = null;
    Vector<String> channelNames = new Vector<String>();
    String date = null;

    String data = DataTools.readFile(templateFile);
    String[] lines = data.split("\r\n");

    for (String line : lines) {
      int eq = line.indexOf("=");
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
          physicalSizeX = new Float(value);
        }
        else if (key.equals("Pixel_Y")) {
          physicalSizeY = new Float(value);
        }
        else if (key.equals("Objective_ID")) {
          store.setObjectiveModel(value, 0, 0);
        }
        else if (key.equals("Magnification")) {
          int mag = (int) Double.parseDouble(value);
          store.setObjectiveNominalMagnification(new Integer(mag), 0, 0);
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
          exposure = new Float(value);
        }
      }
    }

    for (int well=0; well<tiffs.length; well++) {
      if (physicalSizeX != null) {
        store.setDimensionsPhysicalSizeX(physicalSizeX, well, 0);
      }
      if (physicalSizeY != null) {
        store.setDimensionsPhysicalSizeY(physicalSizeY, well, 0);
      }
      for (int c=0; c<channelNames.size(); c++) {
        if (c < getEffectiveSizeC()) {
          store.setLogicalChannelName(channelNames.get(c), well, c);
        }
      }
      date = DateTools.formatDate(date, "dd/MM/yyyy HH:mm:ss");
      store.setImageCreationDate(date, well);

      for (int i=0; i<getImageCount(); i++) {
        store.setPlaneTimingExposureTime(exposure, well, 0, i);
      }
    }
  }

  /**
   * Parse Mask ROIs from the given TIFF and place them in the given
   * MetadataStore.
   * @return the number of masks parsed
   */
  private int parseMasks(MetadataStore store, int series, int roi,
    String overlayTiff) throws FormatException, IOException
  {
    if (!parseMasks) return 0;
    int nOverlays = 0;
    for (int i=0; i<3; i++) {
      String id = MetadataTools.createLSID("Mask", series, roi + nOverlays, 0);
      overlayFiles.put(id, overlayTiff);
      overlayPlanes.put(id, new Integer(i));

      boolean validMask = populateMaskPixels(series, roi + nOverlays, 0);
      if (validMask) {
        store.setMaskX("0", series, roi + nOverlays, 0);
        store.setMaskY("0", series, roi + nOverlays, 0);
        store.setMaskWidth(
          String.valueOf(getSizeX()), series, roi + nOverlays, 0);
        store.setMaskHeight(
          String.valueOf(getSizeY()), series, roi + nOverlays, 0);
        store.setMaskPixelsBigEndian(
          new Boolean(!isLittleEndian()), series, roi + nOverlays, 0);
        store.setMaskPixelsSizeX(
          new Integer(getSizeX()), series, roi + nOverlays, 0);
        store.setMaskPixelsSizeY(
          new Integer(getSizeY()), series, roi + nOverlays, 0);
        store.setMaskPixelsExtendedPixelType("bit", series, roi + nOverlays, 0);

        String color = String.valueOf(0xff000000 | (0xff << (8 * (2 - i))));
        store.setShapeStrokeColor(color, series, roi + nOverlays, 0);
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
    int shapeIndex)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);

    String id =
      MetadataTools.createLSID("Mask", imageIndex, roiIndex, shapeIndex);
    String maskFile = overlayFiles.get(id);
    if (maskFile == null) {
      warnDebug("Could not find an overlay file matching " + id);
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
      MetadataStore store =
        new FilterMetadata(getMetadataStore(), isMetadataFiltered());
      store.setMaskPixelsBinData(bits.toByteArray(), imageIndex, roiIndex,
        shapeIndex);
    }
    else debug("Did not populate MaskPixels.BinData for " + id);

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
