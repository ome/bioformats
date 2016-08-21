/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import loci.common.Constants;
import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.IniList;
import loci.common.IniParser;
import loci.common.IniTable;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffIFDEntry;
import loci.formats.tiff.TiffParser;

import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

/**
 * BDReader is the file format reader for BD Pathway datasets.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/BDReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/BDReader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Shawn Garbett  Shawn.Garbett a t Vanderbilt.edu
 */
public class BDReader extends FormatReader {

  // -- Constants --
  private static final String EXPERIMENT_FILE = "Experiment.exp";
  private static final String[] META_EXT =
    {"drt", "dye", "exp", "plt", "txt", "geo", "ltp", "ffc", "afc", "mon",
    "xyz", "mac", "bmp", "roi", "adf"};

  // -- Fields --
  private Vector<String> metadataFiles = new Vector<String>();
  private Vector<String> channelNames = new Vector<String>();
  private Vector<String> wellLabels = new Vector<String>();
  private String plateName, plateDescription;
  private String[][] tiffs;
  private MinimalTiffReader reader;

  private String roiFile;
  private int[] emWave, exWave;
  private double[] gain, offset, exposure;
  private String binning, objective;

  private int wellRows, wellCols;
  private int fieldRows;
  private int fieldCols;

  // -- Constructor --

  /** Constructs a new ScanR reader. */
  public BDReader() {
    super("BD Pathway", new String[] {"exp", "tif"});
    domains = new String[] {FormatTools.HCS_DOMAIN};
    hasCompanionFiles = true;
    suffixSufficient = false;
    suffixNecessary = false;
    datasetDescription = "Multiple files (.exp, .dye, .ltp, …) plus " +
      "one or more directories containing .tif and .bmp files";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    if (name.endsWith(EXPERIMENT_FILE)) return true;
    if (!open) return false;

    String id = new Location(name).getAbsolutePath();
    try {
      id = locateExperimentFile(id);
    }
    catch (FormatException f) {
      return false;
    }
    catch (IOException f) {
      return false;
    }
    catch (NullPointerException e) {
      return false;
    }

    if (id.endsWith(EXPERIMENT_FILE)) { return true; }

    return super.isThisType(name, open);
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    TiffParser p = new TiffParser(stream);
    IFD ifd = p.getFirstIFD();
    if (ifd == null) return false;

    String software = ifd.getIFDTextValue(IFD.SOFTWARE);
    if (software == null) return false;

    return software.trim().startsWith("MATROX Imaging Library");
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);

    Vector<String> files = new Vector<String>();
    for (String file : metadataFiles) {
      if (file != null) files.add(file);
    }

    if (!noPixels && tiffs != null) {
      int well = getSeries() / (fieldRows * fieldCols);
      for (int i = 0; i<tiffs[well].length; i++) {
        files.add(tiffs[well][i]);
      }
    }

    return files.toArray(new String[files.size()]);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      if (reader != null) reader.close();
      reader = null;
      tiffs = null;
      plateName = null;
      plateDescription = null;
      channelNames.clear();
      metadataFiles.clear();
      wellLabels.clear();
      wellRows = 0;
      wellCols = 0;
      fieldRows = 0;
      fieldCols = 0;
    }
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* see loci.formats.IFormatReader#isSingleFile(String) */
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    String file = getFilename(getSeries(), no);
    int field = getSeries() % (fieldRows * fieldCols);
    int fieldRow = field / fieldCols;
    int fieldCol = field % fieldCols;

    if (file != null) {
      try {
        reader.setId(file);
        if (fieldRows * fieldCols == 1) {
          reader.openBytes(0, buf, x, y, w, h);
        }
        else {
          // fields are stored together in a single image,
          // so we need to split them up
          int fx = x + (fieldCol * getSizeX());
          int fy = y + (fieldRow * getSizeY());
          reader.openBytes(0, buf, fx, fy, w, h);
        }
      }
      catch (FormatException e) {
        LOGGER.debug("Could not read file " + file, e);
        return buf;
      }
      catch (IOException e) {
        LOGGER.debug("Could not read file " + file, e);
        return buf;
      }
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    return reader.getOptimalTileWidth() / fieldCols;
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    return (int) Math.max(1, reader.getOptimalTileHeight() / fieldRows);
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    // make sure we have the experiment file
    id = locateExperimentFile(id);
    super.initFile(id);
    Location dir = new Location(id).getAbsoluteFile().getParentFile();

    for (String file : dir.list(true)) {
      Location f = new Location(dir, file);
      if (!f.isDirectory()) {
        if (checkSuffix(file, META_EXT)) {
          metadataFiles.add(f.getAbsolutePath());
        }
      }
      else {
        for (String well : f.list(true)) {
          Location wellFile = new Location(f, well);
          if (!wellFile.isDirectory()) {
            if (checkSuffix(well, META_EXT)) {
              metadataFiles.add(wellFile.getAbsolutePath());
            }
          }
        }
      }
    }

    // parse Experiment metadata
    IniList experiment = readMetaData(id);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      objective = experiment.getTable("Geometry").get("Name");
      IniTable camera = experiment.getTable("Camera");
      binning = camera.get("BinX") + "x" + camera.get("BinY");

      parseChannelData(dir);

      addGlobalMeta("Objective", objective);
      addGlobalMeta("Camera binning", binning);
    }

    Vector<String> uniqueRows = new Vector<String>();
    Vector<String> uniqueColumns = new Vector<String>();

    for (String well : wellLabels) {
      String row = well.substring(0, 1).trim();
      String column = well.substring(1).trim();

      if (!uniqueRows.contains(row) && row.length() > 0) uniqueRows.add(row);
      if (!uniqueColumns.contains(column) && column.length() > 0) {
        uniqueColumns.add(column);
      }
    }

    int nSlices = getSizeZ() == 0 ? 1 : getSizeZ();
    int nTimepoints = getSizeT();
    int nWells = wellLabels.size();
    int nChannels = getSizeC() == 0 ? channelNames.size() : getSizeC();
    if (nChannels == 0) nChannels = 1;

    tiffs = getTiffs(dir.getAbsolutePath());

    reader = new MinimalTiffReader();
    reader.setId(tiffs[0][0]);

    int sizeX = reader.getSizeX();
    int sizeY = reader.getSizeY();
    int pixelType = reader.getPixelType();
    boolean rgb = reader.isRGB();
    boolean interleaved = reader.isInterleaved();
    boolean indexed = reader.isIndexed();
    boolean littleEndian = reader.isLittleEndian();

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      IniParser parser = new IniParser();
      for (String metadataFile : metadataFiles) {
        String filename = new Location(metadataFile).getName();
        if (!checkSuffix(metadataFile,
          new String[] {"txt", "bmp", "adf", "roi"}))
        {
          String data = DataTools.readFile(metadataFile);
          IniList ini =
            parser.parseINI(new BufferedReader(new StringReader(data)));
          HashMap<String, String> h = ini.flattenIntoHashMap();
          for (String key : h.keySet()) {
            addGlobalMeta(filename + " " + key, h.get(key));
          }
        }
      }
    }

    int coresize = core.size();
    core.clear();
    for (int i=0; i<coresize; i++) {
      CoreMetadata ms = new CoreMetadata();
      core.add(ms);
      ms.sizeC = nChannels;
      ms.sizeZ = nSlices;
      ms.sizeT = nTimepoints;
      ms.sizeX = sizeX / fieldCols;
      ms.sizeY = sizeY / fieldRows;
      ms.pixelType = pixelType;
      ms.rgb = rgb;
      ms.interleaved = interleaved;
      ms.indexed = indexed;
      ms.littleEndian = littleEndian;
      ms.dimensionOrder = "XYZTC";
      ms.imageCount = nSlices * nTimepoints * nChannels;
    }

    MetadataStore store = makeFilterMetadata();
    boolean populatePlanes =
      getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM;
    MetadataTools.populatePixels(store, this, populatePlanes);

    String plateAcqID = MetadataTools.createLSID("PlateAcquisition", 0, 0);
    store.setPlateAcquisitionID(plateAcqID, 0, 0);

    PositiveInteger fieldCount =
      FormatTools.getMaxFieldCount(fieldRows * fieldCols);
    if (fieldCount != null) {
      store.setPlateAcquisitionMaximumFieldCount(fieldCount, 0, 0);
    }

    for (int row=0; row<wellRows; row++) {
      for (int col=0; col<wellCols; col++) {
        int index = row * wellCols + col;

        store.setWellID(MetadataTools.createLSID("Well", 0, index), 0, index);
        store.setWellRow(new NonNegativeInteger(row), 0, index);
        store.setWellColumn(new NonNegativeInteger(col), 0, index);
      }
    }

    for (int i=0; i<getSeriesCount(); i++) {
      int well = i / (fieldRows * fieldCols);
      int field = i % (fieldRows * fieldCols);

      MetadataTools.setDefaultCreationDate(store, tiffs[well][0], i);

      String name = wellLabels.get(well);
      String row = name.substring(0, 1);
      Integer col = Integer.parseInt(name.substring(1));

      int index = (row.charAt(0) - 'A') * wellCols + col - 1;

      String wellSampleID =
        MetadataTools.createLSID("WellSample", 0, index, field);
      store.setWellSampleID(wellSampleID, 0, index, field);
      store.setWellSampleIndex(new NonNegativeInteger(i), 0, index, field);

      String imageID = MetadataTools.createLSID("Image", i);
      store.setWellSampleImageRef(imageID, 0, index, field);
      store.setImageID(imageID, i);
      store.setImageName(name + " Field #" + (field + 1), i);

      store.setPlateAcquisitionWellSampleRef(wellSampleID, 0, 0, i);
    }

    MetadataLevel level = getMetadataOptions().getMetadataLevel();
    if (level != MetadataLevel.MINIMUM) {
      String instrumentID = MetadataTools.createLSID("Instrument", 0);
      store.setInstrumentID(instrumentID, 0);

      String objectiveID = MetadataTools.createLSID("Objective", 0, 0);
      store.setObjectiveID(objectiveID, 0, 0);
      if (objective != null) {
        String[] tokens = objective.split(" ");
        String mag = tokens[0].replaceAll("[xX]", "");
        String na = null;
        int naIndex = 0;
        for (int i=0; i<tokens.length; i++) {
          if (tokens[i].equals("NA")) {
            naIndex = i + 1;
            na = tokens[naIndex];
            break;
          }
        }

        Double magnification = new Double(mag);
        store.setObjectiveNominalMagnification(magnification, 0, 0);
        if (na != null) {
          na = na.substring(0, 1) + "." + na.substring(1);
          store.setObjectiveLensNA(new Double(na), 0, 0);
        }
        if (naIndex + 1 < tokens.length) {
          store.setObjectiveManufacturer(tokens[naIndex + 1], 0, 0);
        }
      }

      // populate LogicalChannel data
      for (int i=0; i<getSeriesCount(); i++) {
        store.setImageInstrumentRef(instrumentID, i);
        store.setObjectiveSettingsID(objectiveID, i);

        for (int c=0; c<getSizeC(); c++) {
          store.setChannelName(channelNames.get(c), i, c);

          PositiveInteger emission =
            FormatTools.getEmissionWavelength(emWave[c]);
          PositiveInteger excitation =
            FormatTools.getExcitationWavelength(exWave[c]);

          if (emission != null) {
            store.setChannelEmissionWavelength(emission, i, c);
          }
          if (excitation != null) {
            store.setChannelExcitationWavelength(excitation, i, c);
          }

          String detectorID = MetadataTools.createLSID("Detector", 0, c);
          store.setDetectorID(detectorID, 0, c);
          store.setDetectorSettingsID(detectorID, i, c);
          store.setDetectorSettingsGain(gain[c], i, c);
          store.setDetectorSettingsOffset(offset[c], i, c);
          store.setDetectorSettingsBinning(getBinning(binning), i, c);
        }

        long firstPlane = 0;
        for (int p=0; p<getImageCount(); p++) {
          int[] zct = getZCTCoords(p);
          store.setPlaneExposureTime(exposure[zct[1]], i, p);
          String file = getFilename(i, p);
          if (file != null) {
            long plane = getTimestamp(file);
            if (p == 0) {
              firstPlane = plane;
            }
            double timestamp = (plane - firstPlane) / 1000.0;
            store.setPlaneDeltaT(timestamp, i, p);
          }
        }
      }

      store.setPlateID(MetadataTools.createLSID("Plate", 0), 0);
      store.setPlateRowNamingConvention(getNamingConvention("Letter"), 0);
      store.setPlateColumnNamingConvention(getNamingConvention("Number"), 0);
      store.setPlateName(plateName, 0);
      store.setPlateDescription(plateDescription, 0);

      if (level != MetadataLevel.NO_OVERLAYS) {
        parseROIs(store);
      }
    }
  }

  // -- Helper methods --

  /* Locate the experiment file given any file in set */
  private String locateExperimentFile(String id)
    throws FormatException, IOException
  {
    if (!checkSuffix(id, "exp")) {
      Location parent = new Location(id).getAbsoluteFile().getParentFile();
      if (checkSuffix(id, "tif")) parent = parent.getParentFile();
      Location expFile = new Location(parent, EXPERIMENT_FILE);
      if (expFile.exists()) {
        return expFile.getAbsolutePath();
      }
      throw new FormatException("Could not find " + EXPERIMENT_FILE +
        " in " + parent.getAbsolutePath());
    }
    return id;
  }

  private IniList readMetaData(String id) throws IOException {
    IniParser parser = new IniParser();
    FileInputStream idStream = new FileInputStream(id);
    IniList exp = parser.parseINI(new BufferedReader(new InputStreamReader(
      idStream, Constants.ENCODING)));
    IniList plate = null;
    IniList xyz = null;

    // Read Plate File
    for (String filename : metadataFiles) {
      if (checkSuffix(filename, "plt")) {
        FileInputStream stream = new FileInputStream(filename);
        plate = parser.parseINI(new BufferedReader(new InputStreamReader(
          stream, Constants.ENCODING)));
        stream.close();
      }
      else if (checkSuffix(filename, "xyz")) {
        FileInputStream stream = new FileInputStream(filename);
        xyz = parser.parseINI(new BufferedReader(new InputStreamReader(
          stream, Constants.ENCODING)));
        stream.close();
      }
      else if (filename.endsWith("RoiSummary.txt")) {
        roiFile = filename;
        if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
          RandomAccessInputStream s = new RandomAccessInputStream(filename);
          String line = s.readLine().trim();
          while (!line.endsWith(".adf\"")) {
            line = s.readLine().trim();
          }
          plateName = line.substring(line.indexOf(":")).trim();
          plateName = plateName.replace('/', File.separatorChar);
          plateName = plateName.replace('\\', File.separatorChar);
          for (int i=0; i<3; i++) {
            plateName =
              plateName.substring(0, plateName.lastIndexOf(File.separator));
          }
          plateName =
            plateName.substring(plateName.lastIndexOf(File.separator) + 1);

          s.close();
        }
      }
    }
    if (plate == null) throw new IOException("No Plate File");

    IniTable plateType = plate.getTable("PlateType");

    if (plateName == null) {
      plateName = plateType.get("Brand");
    }
    plateDescription =
      plateType.get("Brand") + " " + plateType.get("Description");

    int nWells = Integer.parseInt(plateType.get("Wells"));
    if (nWells == 96) {
      wellRows = 8;
      wellCols = 12;
    }
    else if (nWells == 384) {
      wellRows = 16;
      wellCols = 24;
    }

    Location dir = new Location(id).getAbsoluteFile().getParentFile();
    String[] wellList = dir.list();
    Arrays.sort(wellList);
    for (String filename : wellList) {
      if (filename.startsWith("Well ")) {
        wellLabels.add(filename.split("\\s|\\.")[1]);
      }
    }

    IniTable imageTable = exp.getTable("Image");
    boolean montage = imageTable.get("Montaged").equals("1");
    if (montage) {
      fieldRows = Integer.parseInt(imageTable.get("TilesY"));
      fieldCols = Integer.parseInt(imageTable.get("TilesX"));
    }
    else {
      fieldRows = 1;
      fieldCols = 1;
    }

    core.clear();
    int coresize = wellLabels.size() * fieldRows * fieldCols;
    CoreMetadata ms0 = new CoreMetadata();
    core.add(ms0);
    for (int i=1; i<coresize; i++) {
      core.add(new CoreMetadata());
    }

    ms0.sizeC = Integer.parseInt(exp.getTable("General").get("Dyes"));
    ms0.bitsPerPixel =
      Integer.parseInt(exp.getTable("Camera").get("BitdepthUsed"));

    IniTable dyeTable = exp.getTable("Dyes");
    for (int i=1; i<=getSizeC(); i++) {
      channelNames.add(dyeTable.get(Integer.toString(i)));
    }

    if (xyz != null) {
      IniTable zTable = xyz.getTable("Z1Axis");
      boolean zEnabled = "1".equals(zTable.get("Z1AxisEnabled")) &&
        "1".equals(zTable.get("Z1AxisMode"));
      if (zEnabled) {
        ms0.sizeZ = (int) Double.parseDouble(zTable.get("Z1AxisValue")) + 1;
      }
      else {
        ms0.sizeZ = 1;
      }
    }
    else {
      ms0.sizeZ = 1;
    }

    // Count Images
    ms0.sizeT = 0;
    Location well = new Location(dir.getAbsolutePath(),
      "Well " + wellLabels.get(1));
    for (String channelName : channelNames) {
      int images = 0;
      for (String filename : well.list()) {
        if (filename.startsWith(channelName) && filename.endsWith(".tif")) {
          images++;
        }
      }
      if (images > getImageCount()) {
        ms0.sizeT = images / getSizeZ();
        ms0.imageCount = getSizeZ() * getSizeT() * channelNames.size();
      }
    }

    idStream.close();

    return exp;
  }

  private void parseChannelData(Location dir) throws IOException {
    emWave = new int[channelNames.size()];
    exWave = new int[channelNames.size()];
    exposure = new double[channelNames.size()];
    gain = new double[channelNames.size()];
    offset = new double[channelNames.size()];

    for (int c=0; c<channelNames.size(); c++) {
      Location dyeFile = new Location(dir, channelNames.get(c) + ".dye");
      FileInputStream stream = new FileInputStream(dyeFile.getAbsolutePath());
      IniList dye = new IniParser().parseINI(new BufferedReader(
        new InputStreamReader(stream, Constants.ENCODING)));

      IniTable numerator = dye.getTable("Numerator");
      String em = numerator.get("Emission");
      em = em.substring(0, em.indexOf(" "));
      emWave[c] = Integer.parseInt(em);

      String ex = numerator.get("Excitation");
      ex = ex.substring(0, ex.lastIndexOf(" "));
      if (ex.indexOf(" ") != -1) {
        ex = ex.substring(ex.lastIndexOf(" ") + 1);
      }
      exWave[c] = Integer.parseInt(ex);

      exposure[c] = Double.parseDouble(numerator.get("Exposure"));
      gain[c] = Double.parseDouble(numerator.get("Gain"));
      offset[c] = Double.parseDouble(numerator.get("Offset"));

      stream.close();
    }
  }

  private String[][] getTiffs(String dir) {
    Location f = new Location(dir);
    Vector<Vector<String>> files = new Vector<Vector<String>>();

    String[] wells = f.list(true);
    Arrays.sort(wells);

    for (String filename : wells) {
      Location file = new Location(f, filename).getAbsoluteFile();
      if (file.isDirectory() && filename.startsWith("Well ")) {
        String[] list = file.list(true);
        Vector<String> tiffList = new Vector<String>();
        Arrays.sort(list);
        for (String tiff : list) {
          if (tiff.matches(".* - n\\d\\d\\d\\d\\d\\d\\.tif")) {
            tiffList.add(new Location(file, tiff).getAbsolutePath());
          }
        }
        files.add(tiffList);
      }
    }

    String[][] tiffFiles = new String[files.size()][];
    for (int i=0; i<tiffFiles.length; i++) {
      tiffFiles[i] = files.get(i).toArray(new String[0]);
    }
    return tiffFiles;
  }

  private void parseROIs(MetadataStore store) throws IOException {
    if (roiFile == null) return;
    String roiData = DataTools.readFile(roiFile);
    String[] lines = roiData.split("\r\n");

    int firstRow = 0;
    while (firstRow < lines.length && !lines[firstRow].startsWith("ROI")) {
      firstRow++;
    }
    firstRow += 2;
    if (firstRow >= lines.length) return;

    for (int i=firstRow; i<lines.length; i++) {
      String[] cols = lines[i].split("\t");
      if (cols.length < 6) break;

      if (cols[2].trim().length() > 0) {
        String rectangleID = MetadataTools.createLSID("Shape", i - firstRow, 0);
        store.setRectangleID(rectangleID, i - firstRow, 0);
        store.setRectangleX(new Double(cols[2]), i - firstRow, 0);
        store.setRectangleY(new Double(cols[3]), i - firstRow, 0);
        store.setRectangleWidth(new Double(cols[4]), i - firstRow, 0);
        store.setRectangleHeight(new Double(cols[5]), i - firstRow, 0);
        String roiID = MetadataTools.createLSID("ROI", i - firstRow);
        store.setROIID(roiID, i - firstRow);
        for (int s=0; s<getSeriesCount(); s++) {
          store.setImageROIRef(roiID, s, i - firstRow);
        }
      }
    }
  }

  private String getFilename(int series, int no) {
    int[] zct = getZCTCoords(no);
    String channel = channelNames.get(zct[1]);

    int well = series / (fieldRows * fieldCols);

    for (int i=0; i<tiffs[well].length; i++) {
      String name = tiffs[well][i];
      name = name.substring(name.lastIndexOf(File.separator) + 1);
      name = name.substring(0, name.lastIndexOf("."));

      String index = name.substring(name.lastIndexOf("n") + 1);

      int realIndex = getIndex(zct[0], 0, zct[2]);

      if (name.startsWith(channel) && Integer.parseInt(index) == realIndex) {
        return tiffs[well][i];
      }
    }
    return null;
  }

  private long getTimestamp(String file) throws FormatException, IOException {
    RandomAccessInputStream s = new RandomAccessInputStream(file);
    TiffParser parser = new TiffParser(s);
    parser.setDoCaching(false);
    IFD firstIFD = parser.getFirstIFD();
    if (firstIFD != null) {
      TiffIFDEntry timestamp = (TiffIFDEntry) firstIFD.get(IFD.DATE_TIME);
      if (timestamp != null) {
        String stamp = parser.getIFDValue(timestamp).toString();
        s.close();
        stamp = DateTools.formatDate(stamp, BaseTiffReader.DATE_FORMATS);
        Timestamp t = Timestamp.valueOf(stamp);
        return t.asInstant().getMillis(); // NPE if invalid input.
      }
    }
    s.close();
    return new Location(file).lastModified();
  }

}
