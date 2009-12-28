//
// FlexReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import loci.common.DataTools;
import loci.common.FileHandle;
import loci.common.Location;
import loci.common.LogTools;
import loci.common.RandomAccessInputStream;
import loci.common.XMLTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.IFDList;
import loci.formats.tiff.TiffCompression;
import loci.formats.tiff.TiffParser;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * FlexReader is a file format reader for Evotec Flex files.
 * To use it, the LuraWave decoder library, lwf_jsdk2.6.jar, must be available,
 * and a LuraWave license key must be specified in the lurawave.license system
 * property (e.g., <code>-Dlurawave.license=XXXX</code> on the command line).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/FlexReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/FlexReader.java">SVN</a></dd></dl>
 */
public class FlexReader extends FormatReader {

  // -- Constants --

  /** Custom IFD entry for Flex XML. */
  public static final int FLEX = 65200;

  public static final String FLEX_SUFFIX = "flex";
  public static final String MEA_SUFFIX = "mea";
  public static final String RES_SUFFIX = "res";
  public static final String[] MEASUREMENT_SUFFIXES =
    new String[] {MEA_SUFFIX, RES_SUFFIX};
  public static final String[] SUFFIXES =
    new String[] {FLEX_SUFFIX, MEA_SUFFIX, RES_SUFFIX};

  public static final String SCREENING = "Screening";
  public static final String ARCHIVE = "Archive";

  // -- Static fields --

  /**
   * Mapping from server names stored in the .mea file to actual server names.
   */
  private static HashMap<String, String[]> serverMap =
    new HashMap<String, String[]>();

  // -- Fields --

  /** Scale factor for each image. */
  protected double[][][] factors;

  /** Camera binning values. */
  private int binX, binY;

  private int plateCount;
  private int wellCount;
  private int fieldCount;

  private int wellRows, wellColumns;

  private Vector<String> channelNames;
  private Vector<Double> xPositions, yPositions;
  private Vector<Double> xSizes, ySizes;
  private Vector<String> cameraIDs, objectiveIDs, lightSourceIDs;
  private HashMap<String, Vector<String>> lightSourceCombinationIDs;
  private Vector<String> cameraRefs, binnings, objectiveRefs;
  private Vector<String> lightSourceCombinationRefs;
  private Vector<String> filterSets;
  private HashMap<String, String> filterSetMap;

  private Vector<String> measurementFiles;

  private String plateName, plateBarcode;
  private int nRows = 0, nCols = 0;
  private RandomAccessInputStream firstStream;

  /**
   * List of .flex files belonging to this dataset.
   * Indices into the array are the well row and well column.
   */
  private String[][] flexFiles;

  private IFDList[][] ifds;
  private long[][][] offsets;

  /** Specifies the row and column index into 'flexFiles' for a given well. */
  private int[][] wellNumber;

  // -- Constructor --

  /** Constructs a new Flex reader. */
  public FlexReader() {
    super("Evotec Flex", SUFFIXES);
    domains = new String[] {FormatTools.HCS_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  public boolean isSingleFile(String id) throws FormatException, IOException {
    if (!checkSuffix(id, FLEX_SUFFIX)) return false;
    return serverMap.size() == 0 || !isGroupFiles();
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    Vector<String> files = new Vector<String>();
    files.addAll(measurementFiles);
    if (!noPixels) {
      if (fieldCount > 0 && wellCount > 0 && plateCount > 0) {
        int[] lengths = new int[] {fieldCount, wellCount, plateCount};
        int[] pos = FormatTools.rasterToPosition(lengths, getSeries());
        if (pos[1] >= 0 && pos[1] < wellNumber.length) {
          int row = wellCount == 1 ? 0 : wellNumber[pos[1]][0];
          int col = wellCount == 1 ? 0 : wellNumber[pos[1]][1];
          if (row < flexFiles.length && col < flexFiles[row].length) {
            files.add(flexFiles[row][col]);
          }
        }
      }
    }
    return files.toArray(new String[files.size()]);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int[] lengths = new int[] {fieldCount, wellCount, plateCount};
    int[] pos = FormatTools.rasterToPosition(lengths, getSeries());

    int wellRow = wellNumber[pos[1]][0];
    int wellCol = wellNumber[pos[1]][1];
    if (wellCount == 1) {
      wellRow = 0;
      wellCol = 0;
    }

    int imageNumber = offsets[wellRow][wellCol] == null ?
      getImageCount() * pos[0] + no : 0;

    IFD ifd = ifds[wellRow][wellCol].get(imageNumber);
    RandomAccessInputStream s = (wellRow == 0 && wellCol == 0) ? firstStream :
      new RandomAccessInputStream(
        new FileHandle(flexFiles[wellRow][wellCol], "r"));
    TiffParser tp = new TiffParser(s);

    int nBytes = ifd.getBitsPerSample()[0] / 8;
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int planeSize = getSizeX() * getSizeY() * getRGBChannelCount() * nBytes;

    // read pixels from the file
    if (ifd.getCompression() != TiffCompression.UNCOMPRESSED || nBytes != bpp) {
      tp.getSamples(ifd, buf, x, y, w, h);
    }
    else {
      int index = getImageCount() * pos[0] + no;
      long offset = index == offsets[wellRow][wellCol].length - 1 ?
        s.length() : offsets[wellRow][wellCol][index + 1];
      s.seek(offset - planeSize);
      readPlane(s, x, y, w, h, buf);
    }
    if (wellRow != 0 || wellCol != 0) s.close();

    // expand pixel values with multiplication by factor[no]
    int num = buf.length / bpp;

    double factor = factors[wellRow][wellCol][imageNumber];

    if (factor != 1d || nBytes != bpp) {
      for (int i=num-1; i>=0; i--) {
        int q = nBytes == 1 ? buf[i] & 0xff :
          DataTools.bytesToInt(buf, i * bpp, bpp, isLittleEndian());
        if (q != 0) {
          q = (int) (q * factor);
          DataTools.unpackBytes(q, buf, i * bpp, bpp, isLittleEndian());
        }
      }
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      factors = null;
      binX = binY = 0;
      plateCount = wellCount = fieldCount = 0;
      channelNames = null;
      measurementFiles = null;
      xSizes = ySizes = null;
      cameraIDs = objectiveIDs = lightSourceIDs = null;
      lightSourceCombinationIDs = null;
      lightSourceCombinationRefs = null;
      cameraRefs = objectiveRefs = binnings = null;
      wellRows = wellColumns = 0;
      xPositions = yPositions = null;
      filterSets = null;
      filterSetMap = null;
      plateName = plateBarcode = null;
      nRows = nCols = 0;
      flexFiles = null;
      ifds = null;
      offsets = null;
      wellNumber = null;
      if (firstStream != null) firstStream.close();
      firstStream = null;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    debug("FlexReader.initFile(" + id + ")");
    super.initFile(id);

    measurementFiles = new Vector<String>();

    if (checkSuffix(id, FLEX_SUFFIX)) {
      initFlexFile(id);
    }
    else if (checkSuffix(id, RES_SUFFIX)) {
      initResFile(id);
    }
    else initMeaFile(id);
  }

  // -- Helper methods --

  /** Initialize the dataset from a .res file. */
  private void initResFile(String id) throws FormatException, IOException {
    debug("initResFile(" + id + ")");
    status("Initializing .res file");
    Location thisFile = new Location(id).getAbsoluteFile();
    Location parent = thisFile.getParentFile();
    debug("  Looking for an .mea file in " + parent.getAbsolutePath());
    status("Looking for corresponding .mea file");
    String[] list = parent.list();
    for (String file : list) {
      if (checkSuffix(file, MEA_SUFFIX)) {
        String mea = new Location(parent, file).getAbsolutePath();
        debug("  Found .mea file " + mea);
        initMeaFile(mea);
        if (!measurementFiles.contains(thisFile.getAbsolutePath())) {
          measurementFiles.add(thisFile.getAbsolutePath());
        }
        return;
      }
    }
    throw new FormatException("Could not find an .mea file.");
  }

  /** Initialize the dataset from a .mea file. */
  private void initMeaFile(String id) throws FormatException, IOException {
    debug("initMeaFile(" + id + ")");
    status("Initializing .mea file");
    Location file = new Location(id).getAbsoluteFile();
    if (!measurementFiles.contains(file.getAbsolutePath())) {
      measurementFiles.add(file.getAbsolutePath());
    }

    // parse the .mea file to get a list of .flex files
    MeaHandler handler = new MeaHandler();
    status("Reading contents of .mea file");
    String xml = DataTools.readFile(id);
    status("Parsing XML from .mea file");
    XMLTools.parseXML(xml, handler);

    Vector<String> flex = handler.getFlexFiles();
    if (flex.size() == 0) {
      debug("Could not build .flex list from .mea.");
      status("Building list of valid .flex files");
      String[] files = findFiles(file);
      if (files != null) {
        for (String f : files) {
          if (checkSuffix(f, FLEX_SUFFIX)) flex.add(f);
        }
      }
      if (flex.size() == 0) {
        throw new FormatException(".flex files were not found. " +
          "Did you forget to specify the server names?");
      }
    }
    else {
      status("Looking for corresponding .res file");
      String[] files = findFiles(file, new String[] {RES_SUFFIX});
      if (files != null) {
        for (String f : files) {
          if (!measurementFiles.contains(f)) {
            measurementFiles.add(f);
          }
        }
      }
    }

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());

    groupFiles(flex.toArray(new String[flex.size()]), store);
    populateMetadataStore(store);
  }

  private void initFlexFile(String id) throws FormatException, IOException {
    debug("initFlexFile(" + id + ")");
    status("Initializing .flex file");
    boolean doGrouping = true;

    Location currentFile = new Location(id).getAbsoluteFile();

    status("Storing well indices");
    try {
      String name = currentFile.getName();
      int[] well = getWell(name);
      if (well[0] > nRows) nRows = well[0];
      if (well[1] > nCols) nCols = well[1];
    }
    catch (NumberFormatException e) {
      traceDebug(e);
      doGrouping = false;
    }

    status("Looking for other .flex files");
    if (!isGroupFiles()) doGrouping = false;

    if (isGroupFiles()) {
      debug("Attempting to find files in the same dataset.");
      try {
        findFiles(currentFile);
      }
      catch (NullPointerException e) {
        traceDebug(e);
      }
      catch (IOException e) {
        traceDebug(e);
      }
      if (measurementFiles.size() == 0) {
        warnDebug("Measurement files not found.");
      }
    }

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());

    status("Making sure that all .flex files are valid");
    Vector<String> flex = new Vector<String>();
    if (doGrouping) {
      // group together .flex files that are in the same directory

      Location dir = currentFile.getParentFile();
      String[] files = dir.list(true);

      for (String file : files) {
        // file names should be nnnnnnnnn.flex, where 'n' is 0-9
        debug("Checking if " + file + " belongs in the same dataset.");
        if (file.endsWith(".flex") && file.length() == 14) {
          flex.add(new Location(dir, file).getAbsolutePath());
          debug("Added " + flex.get(flex.size() - 1) + " to dataset.");
        }
        else {
          doGrouping = false;
          break;
        }
      }
    }

    String[] files = doGrouping ? flex.toArray(new String[flex.size()]) :
      new String[] {currentFile.getAbsolutePath()};
    debug("Determined that " + files.length + " .flex files belong together.");

    groupFiles(files, store);
    populateMetadataStore(store);
  }

  private void populateMetadataStore(MetadataStore store) {
    status("Populating MetadataStore");
    MetadataTools.populatePixels(store, this, true);
    String instrumentID = MetadataTools.createLSID("Instrument", 0);
    store.setInstrumentID(instrumentID, 0);

    Location currentFile = new Location(getCurrentFile()).getAbsoluteFile();

    if (plateName == null) plateName = currentFile.getParentFile().getName();
    if (plateBarcode != null) plateName = plateBarcode + " " + plateName;
    store.setPlateName(plateName, 0);
    store.setPlateRowNamingConvention("A", 0);
    store.setPlateColumnNamingConvention("1", 0);

    int[] lengths = new int[] {fieldCount, wellCount, plateCount};

    for (int row=0; row<wellRows; row++) {
      for (int col=0; col<wellColumns; col++) {
        store.setWellRow(new Integer(row), 0, row * wellColumns + col);
        store.setWellColumn(new Integer(col), 0, row * wellColumns + col);
      }
    }

    for (int i=0; i<getSeriesCount(); i++) {
      int[] pos = FormatTools.rasterToPosition(lengths, i);

      String imageID = MetadataTools.createLSID("Image", i);
      store.setImageID(imageID, i);
      store.setImageInstrumentRef(instrumentID, i);
      char wellRow = (char) ('A' + wellNumber[pos[1]][0]);
      store.setImageName("Well " + wellRow + "-" + (wellNumber[pos[1]][1] + 1) +
        "; Field #" + (pos[0] + 1), i);

      int seriesIndex = i * getImageCount();
      if (seriesIndex < objectiveRefs.size()) {
        store.setObjectiveSettingsObjective(objectiveRefs.get(seriesIndex), i);
      }

      if (seriesIndex < lightSourceCombinationRefs.size()) {
        String lightSourceCombo = lightSourceCombinationRefs.get(seriesIndex);
        Vector<String> lightSources =
          lightSourceCombinationIDs.get(lightSourceCombo);

        for (int c=0; c<getEffectiveSizeC(); c++) {
          int index = i * getImageCount() + c;
          if (index < cameraRefs.size()) {
            store.setDetectorSettingsDetector(cameraRefs.get(index), i, c);
          }
          if (index < binnings.size()) {
            store.setDetectorSettingsBinning(binnings.get(index), i, c);
          }
          if (lightSources != null && c < lightSources.size()) {
            store.setLightSourceSettingsLightSource(lightSources.get(c), i, c);
          }
          else if (c > 0 && lightSources != null && lightSources.size() == 1) {
            store.setLightSourceSettingsLightSource(lightSources.get(0), i, c);
          }
          if (index < filterSets.size()) {
            String filterSetID = filterSetMap.get(filterSets.get(index));
            store.setLogicalChannelFilterSet(filterSetID, i, c);
          }
        }
      }

      int sizeIndex = i * getImageCount();
      if (sizeIndex < xSizes.size()) {
        store.setDimensionsPhysicalSizeX(xSizes.get(sizeIndex), i, 0);
      }
      if (sizeIndex < ySizes.size()) {
        store.setDimensionsPhysicalSizeY(ySizes.get(sizeIndex), i, 0);
      }

      int well = wellNumber[pos[1]][0] * wellColumns + wellNumber[pos[1]][1];
      if (wellRows == 0 && wellColumns == 0) {
        well = pos[1];
        store.setWellRow(new Integer(wellNumber[pos[1]][0]), pos[2], pos[1]);
        store.setWellColumn(new Integer(wellNumber[pos[1]][1]), pos[2], pos[1]);
      }

      store.setWellSampleIndex(new Integer(i), pos[2], well, pos[0]);
      store.setWellSampleImageRef(imageID, pos[2], well, pos[0]);
      if (pos[0] < xPositions.size()) {
        store.setWellSamplePosX(xPositions.get(pos[0]), pos[2], well, pos[0]);
      }
      if (pos[0] < yPositions.size()) {
        store.setWellSamplePosY(yPositions.get(pos[0]), pos[2], well, pos[0]);
      }
    }
  }

  /**
   * Returns a two-element array containing the well row and well column
   * corresponding to the given file.
   */
  private int[] getWell(String file) {
    String name = file.substring(file.lastIndexOf(File.separator) + 1);
    if (name.length() == 14) {
      // expect nnnnnnnnn.flex
      try {
        int row = Integer.parseInt(name.substring(0, 3)) - 1;
        int col = Integer.parseInt(name.substring(3, 6)) - 1;
        return new int[] {row, col};
      }
      catch (NumberFormatException e) { }
    }
    return new int[] {0, 0};
  }

  /**
   * Returns the number of planes in the first well that has data. May not be
   * <code>[0][0]</code> as the acquisition may have been column or row offset.
   */
  private int firstWellPlanes() {
    for (int i = 0; i < ifds.length; i++) {
      for (int j = 0; j < ifds[i].length; j++) {
        if (ifds[i][j] != null) {
          if (offsets[i][j] != null) {
            return offsets[i][j].length;
          }
          return ifds[i][j].size();
        }
      }
    }
    return 0;
  }

  /**
   * Parses XML metadata from the Flex file corresponding to the given well.
   * If the 'firstFile' flag is set, then the core metadata is also
   * populated.
   */
  private void parseFlexFile(int currentWell, int wellRow, int wellCol,
    boolean firstFile, MetadataStore store)
    throws FormatException, IOException
  {
    status("Parsing .flex file (well " + (wellRow + 'A') + (wellCol + 1) + ")");
    debug("Parsing .flex file associated with well row " + wellRow +
      ", column " + wellCol);
    if (flexFiles[wellRow][wellCol] == null) return;

    if (channelNames == null) channelNames = new Vector<String>();
    if (xPositions == null) xPositions = new Vector<Double>();
    if (yPositions == null) yPositions = new Vector<Double>();
    if (xSizes == null) xSizes = new Vector<Double>();
    if (ySizes == null) ySizes = new Vector<Double>();
    if (cameraIDs == null) cameraIDs = new Vector<String>();
    if (lightSourceIDs == null) lightSourceIDs = new Vector<String>();
    if (objectiveIDs == null) objectiveIDs = new Vector<String>();
    if (lightSourceCombinationIDs == null) {
      lightSourceCombinationIDs = new HashMap<String, Vector<String>>();
    }
    if (lightSourceCombinationRefs == null) {
      lightSourceCombinationRefs = new Vector<String>();
    }
    if (cameraRefs == null) cameraRefs = new Vector<String>();
    if (objectiveRefs == null) objectiveRefs = new Vector<String>();
    if (binnings == null) binnings = new Vector<String>();
    if (filterSets == null) filterSets = new Vector<String>();
    if (filterSetMap == null) filterSetMap = new HashMap<String, String>();

    // parse factors from XML
    debug("Parsing XML from " + flexFiles[wellRow][wellCol]);
    IFD ifd = ifds[wellRow][wellCol].get(0);
    String xml = XMLTools.sanitizeXML(ifd.getIFDStringValue(FLEX, true));

    Vector<String> n = new Vector<String>();
    Vector<String> f = new Vector<String>();
    DefaultHandler handler =
      new FlexHandler(n, f, store, firstFile, currentWell);
    status("Parsing XML in .flex file");
    XMLTools.parseXML(xml.getBytes(), handler);

    if (firstFile) populateCoreMetadata(wellRow, wellCol, n);

    int totalPlanes = getSeriesCount() * getImageCount();

    status("Populating pixel scaling factors");

    // verify factor count
    int nsize = n.size();
    int fsize = f.size();
    if (nsize != fsize || nsize != totalPlanes) {
      warnDebug("mismatch between image count, " +
        "names and factors (count=" + totalPlanes +
        ", names=" + nsize + ", factors=" + fsize + ")");
    }
    for (int i=0; i<nsize; i++) addGlobalMeta("Name " + i, n.get(i));
    for (int i=0; i<fsize; i++) addGlobalMeta("Factor " + i, f.get(i));

    // parse factor values
    factors[wellRow][wellCol] = new double[totalPlanes];
    int max = 0;
    for (int i=0; i<fsize; i++) {
      String factor = f.get(i);
      double q = 1;
      try {
        q = Double.parseDouble(factor);
      }
      catch (NumberFormatException exc) {
        warnDebug("invalid factor #" + i + ": " + factor);
      }
      if (i < factors[wellRow][wellCol].length) {
        factors[wellRow][wellCol][i] = q;
        if (q > factors[wellRow][wellCol][max]) max = i;
      }
    }
    if (fsize < factors[wellRow][wellCol].length) {
      Arrays.fill(factors[wellRow][wellCol], fsize,
        factors[wellRow][wellCol].length, 1);
    }

    // determine pixel type
    if (factors[wellRow][wellCol][max] > 256) {
      core[0].pixelType = FormatTools.UINT32;
    }
    else if (factors[wellRow][wellCol][max] > 1) {
      core[0].pixelType = FormatTools.UINT16;
    }
    for (int i=1; i<core.length; i++) {
      core[i].pixelType = getPixelType();
    }
  }

  /** Populate core metadata using the given list of image names. */
  private void populateCoreMetadata(int wellRow, int wellCol,
    Vector<String> imageNames)
    throws FormatException
  {
    status("Populating core metadata");
    debug("Populating core metadata for well row " + wellRow + ", column " +
      wellCol);
    if (getSizeC() == 0 && getSizeT() == 0) {
      Vector<String> uniqueChannels = new Vector<String>();
      for (int i=0; i<imageNames.size(); i++) {
        String name = imageNames.get(i);
        String[] tokens = name.split("_");
        if (tokens.length > 1) {
          // fields are indexed from 1
          int fieldIndex = Integer.parseInt(tokens[0]);
          if (fieldIndex > fieldCount) fieldCount = fieldIndex;
        }
        else tokens = name.split(":");
        String channel = tokens[tokens.length - 1];
        if (!uniqueChannels.contains(channel)) uniqueChannels.add(channel);
      }
      if (fieldCount == 0) fieldCount = 1;
      core[0].sizeC = (int) Math.max(uniqueChannels.size(), 1);
      if (getSizeZ() == 0) core[0].sizeZ = 1;
      core[0].sizeT =
        imageNames.size() / (fieldCount * getSizeC() * getSizeZ());
    }

    if (getSizeC() == 0) {
      core[0].sizeC = (int) Math.max(channelNames.size(), 1);
    }

    if (getSizeZ() == 0) core[0].sizeZ = 1;
    if (getSizeT() == 0) core[0].sizeT = 1;
    if (plateCount == 0) plateCount = 1;
    if (wellCount == 0) wellCount = 1;
    if (fieldCount == 0) fieldCount = 1;

    // adjust dimensions if the number of IFDs doesn't match the number
    // of reported images

    IFDList ifdList = ifds[wellRow][wellCol];
    IFD ifd = ifdList.get(0);
    int nPlanes = ifdList.size();
    if (offsets[wellRow][wellCol] != null) {
      nPlanes = offsets[wellRow][wellCol].length;
    }

    core[0].imageCount = getSizeZ() * getSizeC() * getSizeT();
    if (getImageCount() * fieldCount != nPlanes) {
      core[0].imageCount = nPlanes / fieldCount;
      core[0].sizeZ = 1;
      core[0].sizeC = 1;
      core[0].sizeT = nPlanes / fieldCount;
    }
    core[0].sizeX = (int) ifd.getImageWidth();
    core[0].sizeY = (int) ifd.getImageLength();
    core[0].dimensionOrder = "XYCZT";
    core[0].rgb = false;
    core[0].interleaved = false;
    core[0].indexed = false;
    core[0].littleEndian = ifd.isLittleEndian();
    core[0].pixelType = ifd.getPixelType();

    int seriesCount = plateCount * wellCount * fieldCount;
    if (seriesCount > 1) {
      CoreMetadata oldCore = core[0];
      core = new CoreMetadata[seriesCount];
      Arrays.fill(core, oldCore);
    }
  }

  /**
   * Search for files that correspond to the given file.
   * If the given file is a .mea file, then the corresponding files will be
   * .res and .flex files.
   * If the given file is a .flex file, then the corresponding files will be
   * .res and .mea files.
   */
  private String[] findFiles(Location baseFile) throws IOException {
    String[] suffixes = new String[0];
    if (checkSuffix(baseFile.getName(), FLEX_SUFFIX)) {
      suffixes = new String[] {MEA_SUFFIX, RES_SUFFIX};
      debug("Looking for files with the suffix '" + MEA_SUFFIX + "' or '" +
        RES_SUFFIX + "'.");
    }
    else if (checkSuffix(baseFile.getName(), MEA_SUFFIX)) {
      suffixes = new String[] {FLEX_SUFFIX, RES_SUFFIX};
      debug("Looking for files with the suffix '" + FLEX_SUFFIX + "' or '" +
        RES_SUFFIX + "'.");
    }

    return findFiles(baseFile, suffixes);
  }

  private String[] findFiles(Location baseFile, String[] suffixes)
    throws IOException
  {
    // we're assuming that the directory structure looks something like this:
    //
    //                        top level directory
    //                         /              \
    //           top level flex dir       top level measurement dir
    //              /     |    \                 /       |     \
    //        plate #0   ...   plate #n     plate #0    ...    plate #n
    //       /   |  \                        /   \
    //    .flex ... .flex                 .mea   .res
    //
    // or like this:
    //
    //                       top level directory
    //                       /  |  \      /  |  \
    //          Flex plate #0  ... #n    #0 ... Measurement plate #n
    //
    // or that the .mea and .res are in the same directory as the .flex files

    debug("findFiles(" + baseFile.getAbsolutePath() + ")");

    status("Looking for files that are in the same dataset as " +
      baseFile.getAbsolutePath());
    Vector<String> fileList = new Vector<String>();

    Location plateDir = baseFile.getParentFile();
    String[] files = plateDir.list(true);

    // check if the measurement files are in the same directory
    debug("Looking for files in " + plateDir.getAbsolutePath());
    for (String file : files) {
      String lfile = file.toLowerCase();
      String path = new Location(plateDir, file).getAbsolutePath();
      if (checkSuffix(file, suffixes)) {
        fileList.add(path);
        debug("Found file " + path);
      }
    }

    // file list is valid (i.e. can be returned) if there is at least
    // one file with each of the desired suffixes
    debug("Checking to see if at least one file with each suffix was found...");
    boolean validList = true;
    for (String suffix : suffixes) {
      boolean foundSuffix = false;
      for (String file : fileList) {
        if (checkSuffix(file, suffix)) {
          foundSuffix = true;
          break;
        }
      }
      if (!foundSuffix) {
        validList = false;
        break;
      }
    }
    debug((validList ? "Found" : "Did not find") + " required files.");

    if (validList) {
      debug("Returning file list:");
      for (String file : fileList) {
        debug("  " + file);
        if (checkSuffix(file, MEASUREMENT_SUFFIXES) &&
          !measurementFiles.contains(file))
        {
          measurementFiles.add(file);
        }
      }
      return fileList.toArray(new String[fileList.size()]);
    }

    Location flexDir = null;
    try {
      flexDir = plateDir.getParentFile();
    }
    catch (NullPointerException e) { }
    debug("Looking for files in " + flexDir);
    if (flexDir == null) return null;

    // check if the measurement directory and the Flex directory
    // have the same parent

    Location measurementDir = null;
    String[] flexDirList = flexDir.list(true);
    if (flexDirList.length > 1) {
      String plateName = plateDir.getName();
      for (String file : flexDirList) {
        if (!file.equals(plateName) &&
          (plateName.startsWith(file) || file.startsWith(plateName)))
        {
          measurementDir = new Location(flexDir, file);
          debug("Expect measurement files to be in " +
            measurementDir.getAbsolutePath());
          break;
        }
      }
    }

    // check if Flex directories and measurement directories have
    // a different parent

    if (measurementDir == null) {
      Location topDir = flexDir.getParentFile();
      debug("First attempt at finding measurement file directory failed. " +
        "Looking for an appropriate measurement directory in " +
        topDir.getAbsolutePath() + ".");

      String[] topDirList = topDir.list(true);
      for (String file : topDirList) {
        if (!flexDir.getAbsolutePath().endsWith(file)) {
          measurementDir = new Location(topDir, file);
          debug("Expect measurement files to be in " +
            measurementDir.getAbsolutePath());
          break;
        }
      }

      if (measurementDir == null) {
        debug("Failed to find measurement file directory.");
        return null;
      }
    }
    else plateDir = measurementDir;

    if (!plateDir.getAbsolutePath().equals(measurementDir.getAbsolutePath())) {
      debug("Measurement files are in a subdirectory of " +
        measurementDir.getAbsolutePath());
      String[] measurementPlates = measurementDir.list(true);
      String plate = plateDir.getName();
      debug("Determining which subdirectory contains the measurements for" +
        " plate " + plate);
      plateDir = null;
      if (measurementPlates != null) {
        for (String file : measurementPlates) {
          debug("Checking " + file);
          if (file.indexOf(plate) != -1 || plate.indexOf(file) != -1) {
            plateDir = new Location(measurementDir, file);
            debug("Measurement files are in " + plateDir.getAbsolutePath());
            break;
          }
        }
      }
    }

    if (plateDir == null) {
      debug("Could not find appropriate subdirectory.");
      return null;
    }

    files = plateDir.list(true);
    for (String file : files) {
      fileList.add(new Location(plateDir, file).getAbsolutePath());
    }

    debug("Returning file list:");
    for (String file : fileList) {
      debug("  " + file);
      if (checkSuffix(file, MEASUREMENT_SUFFIXES) &&
        !measurementFiles.contains(file))
      {
        measurementFiles.add(file);
      }
    }
    return fileList.toArray(new String[fileList.size()]);
  }

  private void groupFiles(String[] fileList, MetadataStore store)
    throws FormatException, IOException
  {
    status("Grouping together files in the same dataset");
    HashMap<String, String> v = new HashMap<String, String>();
    for (String file : fileList) {
      int[] well = getWell(file);
      if (well[0] > nRows) nRows = well[0];
      if (well[1] > nCols) nCols = well[1];
      if (fileList.length == 1) {
        well[0] = 0;
        well[1] = 0;
      }
      v.put(well[0] + "," + well[1], file);
    }

    nRows++;
    nCols++;

    if (fileList.length == 1) {
      nRows = 1;
      nCols = 1;
    }

    debug("Determined that there are " + nRows + " rows and " + nCols +
      " columns of wells.");

    flexFiles = new String[nRows][nCols];
    ifds = new IFDList[nRows][nCols];
    factors = new double[nRows][nCols][];
    offsets = new long[nRows][nCols][];
    wellCount = v.size();
    wellNumber = new int[wellCount][2];

    RandomAccessInputStream s = null;
    boolean firstFile = true;

    int currentWell = 0;
    for (int row=0; row<nRows; row++) {
      for (int col=0; col<nCols; col++) {
        flexFiles[row][col] = v.get(row + "," + col);
        if (flexFiles[row][col] == null) continue;

        wellNumber[currentWell][0] = row;
        wellNumber[currentWell][1] = col;

        s =
          new RandomAccessInputStream(new FileHandle(flexFiles[row][col], "r"));
        if (currentWell == 0) firstStream = s;
        status("Parsing IFDs for well " + (row + 'A') + (col + 1));
        TiffParser tp = new TiffParser(s);
        IFD firstIFD = tp.getFirstIFD();
        if (firstIFD.getCompression() != TiffCompression.UNCOMPRESSED) {
          ifds[row][col] = tp.getIFDs(false, false);
          ifds[row][col].set(0, firstIFD);
        }
        else {
          offsets[row][col] = tp.getIFDOffsets();
          ifds[row][col] = new IFDList();
          ifds[row][col].add(firstIFD);
        }

        if (currentWell != 0) s.close();

        parseFlexFile(currentWell, row, col, firstFile, store);
        if (firstFile) firstFile = false;
        currentWell++;
      }
    }
  }

  // -- Helper classes --

  /** SAX handler for parsing XML. */
  public class FlexHandler extends DefaultHandler {
    private Vector<String> names, factors;
    private MetadataStore store;

    private int nextLaser = -1;
    private int nextCamera = 0;
    private int nextObjective = -1;
    private int nextImage = 0;
    private int nextPlate = 0;

    private String parentQName;
    private String lightSourceID;

    private String sliderName;
    private int nextFilter;
    private int nextDichroic;
    private int nextFilterSet;
    private int nextSliderRef;

    private boolean populateCore = true;
    private int well = 0;

    private HashMap<String, String> filterMap;
    private HashMap<String, String> dichroicMap;

    private StringBuffer charData = new StringBuffer();

    public FlexHandler(Vector<String> names, Vector<String> factors,
      MetadataStore store, boolean populateCore, int well)
    {
      this.names = names;
      this.factors = factors;
      this.store = store;
      this.populateCore = populateCore;
      this.well = well;
      filterMap = new HashMap<String, String>();
      dichroicMap = new HashMap<String, String>();
    }

    public void characters(char[] ch, int start, int length) {
      charData.append(new String(ch, start, length));
    }

    public void endElement(String uri, String localName, String qName) {
      String value = charData.toString();
      charData = new StringBuffer();

      if (qName.equals("Image")) {
        binnings.add(binX + "x" + binY);
      }
      else if (qName.equals("PlateName")) {
        if (plateName == null) plateName = value;
      }
      else if (qName.equals("Barcode")) {
        if (plateBarcode == null) plateBarcode = value;
        store.setPlateExternalIdentifier(value, nextPlate - 1);
      }
      else if (qName.equals("Wavelength")) {
        String lsid = MetadataTools.createLSID("LightSource", 0, nextLaser);
        store.setLightSourceID(lsid, 0, nextLaser);
        store.setLaserWavelength(new Integer(value), 0, nextLaser);
        store.setLaserType("Unknown", 0, nextLaser);
        store.setLaserLaserMedium("Unknown", 0, nextLaser);
      }
      else if (qName.equals("Magnification")) {
        store.setObjectiveCalibratedMagnification(new Double(value), 0,
          nextObjective);
      }
      else if (qName.equals("NumAperture")) {
        store.setObjectiveLensNA(new Double(value), 0, nextObjective);
      }
      else if (qName.equals("Immersion")) {
        if (value.equals("1.33")) value = "Water";
        else if (value.equals("1.00")) value = "Air";
        else warnDebug("Unknown immersion medium: " + value);
        store.setObjectiveImmersion(value, 0, nextObjective);
      }
      else if (qName.equals("OffsetX") || qName.equals("OffsetY")) {
        Double offset = new Double(Double.parseDouble(value) * 1000000);
        if (qName.equals("OffsetX")) xPositions.add(offset);
        else yPositions.add(offset);
      }
      else if (qName.equals("XSize") && "Plate".equals(parentQName)) {
        wellRows = Integer.parseInt(value);
      }
      else if (qName.equals("YSize") && "Plate".equals(parentQName)) {
        wellColumns = Integer.parseInt(value);
      }
      else if ("Image".equals(parentQName)) {
        if (fieldCount == 0) fieldCount = 1;
        int nImages = firstWellPlanes() / fieldCount;
        if (nImages == 0) nImages = 1; // probably a manually altered dataset
        int currentSeries = (nextImage - 1) / nImages;
        currentSeries += well * fieldCount;
        int currentImage = (nextImage - 1) % nImages;

        int seriesCount = 1;
        if (plateCount > 0) seriesCount *= plateCount;
        if (wellCount > 0) seriesCount *= wellCount;
        if (fieldCount > 0) seriesCount *= fieldCount;
        if (currentSeries >= seriesCount) return;

        if (qName.equals("DateTime")) {
          store.setImageCreationDate(value, currentSeries);
        }
        else if (qName.equals("CameraBinningX")) {
          binX = Integer.parseInt(value);
        }
        else if (qName.equals("CameraBinningY")) {
          binY = Integer.parseInt(value);
        }
        else if (qName.equals("ObjectiveRef")) {
          String objectiveID = MetadataTools.createLSID(
            "Objective", 0, objectiveIDs.indexOf(value));
          objectiveRefs.add(objectiveID);
        }
        else if (qName.equals("CameraRef")) {
          String detectorID =
            MetadataTools.createLSID("Detector", 0, cameraIDs.indexOf(value));
          cameraRefs.add(detectorID);
        }
        else if (qName.equals("ImageResolutionX")) {
          double v = Double.parseDouble(value) * 1000000;
          xSizes.add(new Double(v));
        }
        else if (qName.equals("ImageResolutionY")) {
          double v = Double.parseDouble(value) * 1000000;
          ySizes.add(new Double(v));
        }
        else if (qName.equals("PositionX")) {
          Double v = new Double(Double.parseDouble(value) * 1000000);
          store.setStagePositionPositionX(v, currentSeries, 0, currentImage);
        }
        else if (qName.equals("PositionY")) {
          Double v = new Double(Double.parseDouble(value) * 1000000);
          store.setStagePositionPositionY(v, currentSeries, 0, currentImage);
        }
        else if (qName.equals("PositionZ")) {
          Double v = new Double(Double.parseDouble(value) * 1000000);
          store.setStagePositionPositionZ(v, currentSeries, 0, currentImage);
        }
        else if (qName.equals("TimepointOffsetUsed")) {
          store.setPlaneTimingDeltaT(new Double(value), currentSeries, 0,
            currentImage);
        }
        else if (qName.equals("CameraExposureTime")) {
          store.setPlaneTimingExposureTime(new Double(value), currentSeries, 0,
            currentImage);
        }
        else if (qName.equals("LightSourceCombinationRef")) {
          lightSourceCombinationRefs.add(value);
        }
        else if (qName.equals("FilterCombinationRef")) {
          filterSets.add("FilterSet:" + value);
        }
      }
      else if (qName.equals("FilterCombination")) {
        nextFilterSet++;
        nextSliderRef = 0;
      }
    }

    public void startElement(String uri,
      String localName, String qName, Attributes attributes)
    {
      if (qName.equals("Array")) {
        int len = attributes.getLength();
        for (int i=0; i<len; i++) {
          String name = attributes.getQName(i);
          if (name.equals("Name")) {
            names.add(attributes.getValue(i));
          }
          else if (name.equals("Factor")) factors.add(attributes.getValue(i));
        }
      }
      else if (qName.equals("LightSource")) {
        parentQName = qName;
        String type = attributes.getValue("LightSourceType");

        lightSourceIDs.add(attributes.getValue("ID"));
        nextLaser++;
      }
      else if (qName.equals("LightSourceCombination")) {
        lightSourceID = attributes.getValue("ID");
        lightSourceCombinationIDs.put(lightSourceID, new Vector<String>());
      }
      else if (qName.equals("LightSourceRef")) {
        Vector<String> v = lightSourceCombinationIDs.get(lightSourceID);
        if (v != null) {
          int id = lightSourceIDs.indexOf(attributes.getValue("ID"));
          String lightSourceID = MetadataTools.createLSID("LightSource", 0, id);
          v.add(lightSourceID);
          lightSourceCombinationIDs.put(lightSourceID, v);
        }
      }
      else if (qName.equals("Camera")) {
        parentQName = qName;
        String detectorID = MetadataTools.createLSID("Detector", 0, nextCamera);
        store.setDetectorID(detectorID, 0, nextCamera);
        store.setDetectorType(attributes.getValue("CameraType"), 0, nextCamera);
        cameraIDs.add(attributes.getValue("ID"));
        nextCamera++;
      }
      else if (qName.equals("Objective")) {
        parentQName = qName;
        nextObjective++;

        String objectiveID =
          MetadataTools.createLSID("Objective", 0, nextObjective);
        store.setObjectiveID(objectiveID, 0, nextObjective);
        store.setObjectiveCorrection("Unknown", 0, nextObjective);
        objectiveIDs.add(attributes.getValue("ID"));
      }
      else if (qName.equals("Field")) {
        parentQName = qName;
        int fieldNo = Integer.parseInt(attributes.getValue("No"));
        if (fieldNo > fieldCount && fieldCount < firstWellPlanes()) {
          fieldCount++;
        }
      }
      else if (qName.equals("Plane")) {
        parentQName = qName;
        int planeNo = Integer.parseInt(attributes.getValue("No"));
        if (planeNo > getSizeZ() && populateCore) core[0].sizeZ++;
      }
      else if (qName.equals("WellShape")) {
        parentQName = qName;
      }
      else if (qName.equals("Image")) {
        parentQName = qName;
        nextImage++;

        //Implemented for FLEX v1.7 and below
        String x = attributes.getValue("CameraBinningX");
        String y = attributes.getValue("CameraBinningY");
        if (x != null) binX = Integer.parseInt(x);
        if (y != null) binY = Integer.parseInt(y);
      }
      else if (qName.equals("Plate")) {
        parentQName = qName;
        if (qName.equals("Plate")) {
          nextPlate++;
          plateCount++;
        }
      }
      else if (qName.equals("WellCoordinate")) {
        if (wellNumber.length == 1) {
          wellNumber[0][0] = Integer.parseInt(attributes.getValue("Row")) - 1;
          wellNumber[0][1] = Integer.parseInt(attributes.getValue("Col")) - 1;
        }
      }
      else if (qName.equals("Slider")) {
        sliderName = attributes.getValue("Name");
      }
      else if (qName.equals("Filter")) {
        String id = attributes.getValue("ID");
        if (sliderName.endsWith("Dichro")) {
          String dichroicID =
            MetadataTools.createLSID("Dichroic", 0, nextDichroic);
          dichroicMap.put(id, dichroicID);
          store.setDichroicID(dichroicID, 0, nextDichroic);
          store.setDichroicModel(id, 0, nextDichroic);
          nextDichroic++;
        }
        else {
          String filterID = MetadataTools.createLSID("Filter", 0, nextFilter);
          filterMap.put(id, filterID);
          store.setFilterID(filterID, 0, nextFilter);
          store.setFilterModel(id, 0, nextFilter);
          store.setFilterFilterWheel(sliderName, 0, nextFilter);
          nextFilter++;
        }
      }
      else if (qName.equals("FilterCombination")) {
        String filterSetID =
          MetadataTools.createLSID("FilterSet", 0, nextFilterSet);
        store.setFilterSetID(filterSetID, 0, nextFilterSet);
        filterSetMap.put("FilterSet:" + attributes.getValue("ID"), filterSetID);
      }
      else if (qName.equals("SliderRef")) {
        String filterName = attributes.getValue("Filter");
        String filterID = filterMap.get(filterName);
        String dichroicID = dichroicMap.get(filterName);
        String slider = attributes.getValue("ID");
        if (nextSliderRef == 0 && slider.startsWith("Camera")) {
          store.setFilterSetEmFilter(filterID, 0, nextFilterSet);
        }
        else if (nextSliderRef == 1 && slider.startsWith("Camera")) {
          store.setFilterSetExFilter(filterID, 0, nextFilterSet);
        }
        else if (slider.equals("Primary_Dichro")) {
          store.setFilterSetDichroic(dichroicID, 0, nextFilterSet);
        }
        String lname = filterName.toLowerCase();
        if (!lname.startsWith("empty") && !lname.startsWith("blocked")) {
          nextSliderRef++;
        }
      }
    }
  }

  /** SAX handler for parsing XML from .mea files. */
  public class MeaHandler extends DefaultHandler {
    private Vector<String> flex = new Vector<String>();
    private String[] hostnames = null;

    // -- MeaHandler API methods --

    public Vector<String> getFlexFiles() { return flex; }

    // -- DefaultHandler API methods --

    public void startElement(String uri,
      String localName, String qName, Attributes attributes)
    {
      if (qName.equals("Host")) {
        String hostname = attributes.getValue("name");
        debug("FlexHandler: found hostname '" + hostname + "'");
        hostnames = serverMap.get(hostname);
        if (hostnames != null) {
          debug("Sanitizing hostnames...");
          for (int i=0; i<hostnames.length; i++) {
            String host = hostnames[i];
            hostnames[i] = hostnames[i].replace('/', File.separatorChar);
            hostnames[i] = hostnames[i].replace('\\', File.separatorChar);
            debug("Hostname #" + i + " was " + host + ", is now " +
              hostnames[i]);
          }
        }
      }
      else if (qName.equals("Picture")) {
        String path = attributes.getValue("path");
        if (!path.endsWith(".flex")) path += ".flex";
        path = path.replace('/', File.separatorChar);
        path = path.replace('\\', File.separatorChar);
        debug("Found .flex in .mea: " + path);
        if (hostnames != null) {
          int numberOfFlexFiles = flex.size();
          for (String hostname : hostnames) {
            String filename = hostname + File.separator + path;
            if (new Location(filename).exists()) {
              flex.add(filename);
            }
          }
          if (flex.size() == numberOfFlexFiles) {
            warnDebug(path + " was in .mea, but does not actually exist.");
          }
        }
      }
    }
  }

  // -- FlexReader API methods --

  /**
   * Add the path 'realName' to the mapping for the server named 'alias'.
   * @throws FormatException if 'realName' does not exist
   */
  public static void appendServerMap(String alias, String realName)
    throws FormatException
  {
    LogTools.debug("appendServerMap(" + alias + ", " + realName + ")");

    if (alias != null) {
      if (realName == null) {
        LogTools.debug("removing mapping for " + alias);
        serverMap.remove(alias);
      }
      else {
        // verify that 'realName' exists
        Location server = new Location(realName);
        if (!server.exists()) {
          throw new FormatException("Server " + realName + " was not found.");
        }
        String[] names = serverMap.get(alias);
        if (names == null) {
          serverMap.put(alias, new String[] {realName});
        }
        else {
          String[] tmpNames = new String[names.length + 1];
          System.arraycopy(names, 0, tmpNames, 0, names.length);
          tmpNames[tmpNames.length - 1] = realName;
          serverMap.put(alias, tmpNames);
        }
      }
    }
  }

  /**
   * Map the server named 'alias' to the path 'realName'.
   * If other paths were mapped to 'alias', they will be overwritten.
   */
  public static void mapServer(String alias, String realName) {
    LogTools.debug("mapSever(" + alias + ", " + realName + ")");
    if (alias != null) {
      if (realName == null) {
        LogTools.debug("removing mapping for " + alias);
        serverMap.remove(alias);
      }
      else {
        Location server = new Location(realName);

        LogTools.debug("Finding base server name...");
        if (realName.endsWith(File.separator)) {
          realName = realName.substring(0, realName.length() - 1);
        }
        String baseName = realName;
        if (baseName.endsWith(SCREENING)) {
          baseName = baseName.substring(0, baseName.lastIndexOf(SCREENING));
        }
        else if (baseName.endsWith(ARCHIVE)) {
          baseName = baseName.substring(0, baseName.lastIndexOf(ARCHIVE));
        }
        LogTools.debug("Base server name is " + baseName);

        Vector<String> names = new Vector<String>();
        names.add(realName);
        Location screening =
          new Location(baseName + File.separator + SCREENING);
        Location archive = new Location(baseName + File.separator + ARCHIVE);

        if (screening.exists()) names.add(screening.getAbsolutePath());
        if (archive.exists()) names.add(archive.getAbsolutePath());

        LogTools.debug("Server names for " + alias + ":");
        for (String name : names) {
          LogTools.debug("  " + name);
        }

        mapServer(alias, names.toArray(new String[names.size()]));
      }
    }
  }

  /**
   * Map the server named 'alias' to the paths in 'realNames'.
   * If other paths were mapped to 'alias', they will be overwritten.
   */
  public static void mapServer(String alias, String[] realNames) {
    StringBuffer msg = new StringBuffer("mapServer(");
    msg.append(alias);
    if (realNames != null) {
      msg.append(", [");
      for (String name : realNames) {
        msg.append(name);
        msg.append(", ");
      }
      msg.append("])");
    }
    else msg.append(", null)");
    LogTools.debug(msg.toString());

    if (alias != null) {
      if (realNames == null) {
        LogTools.debug("Removing mapping for " + alias);
        serverMap.remove(alias);
      }
      else {
        for (String server : realNames) {
          try {
            appendServerMap(alias, server);
          }
          catch (FormatException e) {
            LogTools.traceDebug(e);
          }
        }
      }
    }
  }

  /**
   * Read a configuration file with lines of the form:
   *
   * &lt;server alias&gt;=&lt;real server name&gt;
   *
   * and call mapServer(String, String) accordingly.
   *
   * @throw FormatException if configFile does not exist.
   * @see mapServer(String, String)
   */
  public static void mapServersFromConfigurationFile(String configFile)
    throws FormatException, IOException
  {
    LogTools.debug("mapServersFromConfigurationFile(" + configFile + ")");
    Location file = new Location(configFile);
    if (!file.exists()) {
      throw new FormatException(
        "Configuration file " + configFile + " does not exist.");
    }

    String[] lines = DataTools.readFile(configFile).split("[\r\n]");
    for (String line : lines) {
      LogTools.debug(line, 3);
      int eq = line.indexOf("=");
      if (eq == -1 || line.startsWith("#")) continue;
      String alias = line.substring(0, eq).trim();
      String[] servers = line.substring(eq + 1).trim().split(";");
      mapServer(alias, servers);
    }
  }

}
