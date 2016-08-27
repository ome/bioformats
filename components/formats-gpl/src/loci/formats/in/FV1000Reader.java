/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import loci.common.ByteArrayHandle;
import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.IniList;
import loci.common.IniParser;
import loci.common.IniTable;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceFactory;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import loci.formats.services.POIService;
import loci.formats.tiff.IFD;
import loci.formats.tiff.IFDList;
import loci.formats.tiff.TiffParser;
import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;
import ome.units.quantity.ElectricPotential;
import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.UNITS;

/**
 * FV1000Reader is the file format reader for Fluoview FV 1000 OIB and
 * Fluoview FV 1000 OIF files.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class FV1000Reader extends FormatReader {

  // -- Constants --

  public static final String FV1000_MAGIC_STRING_1 = "FileInformation";
  public static final String FV1000_MAGIC_STRING_2 = "Acquisition Parameters";

  public static final String[] OIB_SUFFIX = {"oib"};
  public static final String[] OIF_SUFFIX = {"oif"};
  public static final String[] FV1000_SUFFIXES = {"oib", "oif"};

  public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

  private static final int NUM_DIMENSIONS = 9;

  /** ROI types. */
  private static final int POINT = 2;
  private static final int LINE = 3;
  private static final int POLYLINE = 4;
  private static final int RECTANGLE = 5;
  private static final int CIRCLE = 6;
  private static final int ELLIPSE = 7;
  private static final int POLYGON = 8;
  private static final int FREE_SHAPE = 9;
  private static final int FREE_LINE = 10;
  private static final int GRID = 11;
  private static final int ARROW = 12;
  private static final int COLOR_BAR = 13;
  private static final int SCALE = 15;

  private static final String ROTATION = "rotate(%d %f %f)";

  // -- Fields --

  private IniParser parser = new IniParser();

  /** Names of every TIFF file to open. */
  private List<String> tiffs;

  /** Name of thumbnail file. */
  private String thumbId;

  /** Helper reader for thumbnail. */
  private BMPReader thumbReader;

  /** Used file list. */
  private List<String> usedFiles;

  /** Flag indicating this is an OIB dataset. */
  private boolean isOIB;

  /** File mappings for OIB file. */
  private Map<String, String> oibMapping;

  private String[] code, size;
  private Double[] pixelSize;
  private int imageDepth;
  private List<String> previewNames;

  private String pixelSizeX, pixelSizeY;
  private int validBits;
  private List<String> illuminations;
  private List<Double> wavelengths;
  private String pinholeSize;
  private String magnification, lensNA, objectiveName, workingDistance;
  private String creationDate;

  private List<ChannelData> channels;
  private List<String> lutNames = new ArrayList<String>();
  private Map<Integer, String> filenames =
    new HashMap<Integer, String>();
  private Map<Integer, String> roiFilenames =
    new HashMap<Integer, String>();
  private List<PlaneData> planes;

  private transient POIService poi;

  private short[][][] lut;
  private int lastChannel = 0;
  private double pixelSizeZ = 1, pixelSizeT = 1;

  private String ptyStart = null, ptyEnd = null, ptyPattern = null, line = null;

  private ArrayList<IFDList> ifds = new ArrayList<IFDList>();

  // -- Constructor --

  /** Constructs a new FV1000 reader. */
  public FV1000Reader() {
    super("Olympus FV1000", new String[] {"oib", "oif", "pty", "lut"});
    domains = new String[] {FormatTools.LM_DOMAIN};
    hasCompanionFiles = true;
    datasetDescription = "Single .oib file or one .oif file and a " +
      "similarly-named directory containing .tif/.tiff files";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    RandomAccessInputStream plane = getPlane(getSeries(), 0);
    if (plane == null) return super.getOptimalTileWidth();
    try {
      TiffParser tp = new TiffParser(plane);
      IFD ifd = tp.getFirstIFD();
      plane.close();
      return (int) ifd.getTileWidth();
    }
    catch (FormatException e) {
      LOGGER.debug("Could not retrieve tile width", e);
    }
    catch (IOException e) {
      LOGGER.debug("Could not retrieve tile width", e);
    }
    return super.getOptimalTileWidth();
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    RandomAccessInputStream plane = getPlane(getSeries(), 0);
    if (plane == null) return super.getOptimalTileHeight();
    try {
      TiffParser tp = new TiffParser(plane);
      IFD ifd = tp.getFirstIFD();
      plane.close();
      return (int) ifd.getTileLength();
    }
    catch (FormatException e) {
      LOGGER.debug("Could not retrieve tile height", e);
    }
    catch (IOException e) {
      LOGGER.debug("Could not retrieve tile height", e);
    }
    return super.getOptimalTileHeight();
  }

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return checkSuffix(id, OIB_SUFFIX);
  }

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String name, boolean open) {
    if (checkSuffix(name, FV1000_SUFFIXES)) return true;

    if (!open) return false; // not allowed to touch the file system

    try {
      Location oif = new Location(findOIFFile(name));
      return oif.exists() && !oif.isDirectory() &&
        checkSuffix(oif.getAbsolutePath(), "oif") && !checkSuffix(name, "bmp");
    }
    catch (IndexOutOfBoundsException e) { }
    catch (NullPointerException e) { }
    catch (FormatException e) { }
    return false;
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 1024;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    String s = DataTools.stripString(stream.readString(blockLen));
    return s.indexOf(FV1000_MAGIC_STRING_1) >= 0 ||
      s.indexOf(FV1000_MAGIC_STRING_2) >= 0;
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    if (checkSuffix(id, OIB_SUFFIX)) {
      return FormatTools.CANNOT_GROUP;
    }
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  @Override
  public short[][] get16BitLookupTable() {
    FormatTools.assertId(currentId, true, 1);
    return lut == null || !isIndexed() ? null : lut[lastChannel];
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int nFiles = getSeries() == 0 ? tiffs.size() : previewNames.size();
    int image = no % (getImageCount() / nFiles);
    int file = no / (getImageCount() / nFiles);

    int[] coords = getZCTCoords(image);
    lastChannel = coords[1];

    RandomAccessInputStream plane = getPlane(getSeries(), no);

    if (plane == null) return buf;
    TiffParser tp = new TiffParser(plane);
    int index = getSeries() == 0 ? file : tiffs.size() + file;
    IFDList ifdList = ifds.get(index);
    if (image >= ifdList.size()) return buf;

    IFD ifd = ifdList.get(image);
    if (getSizeY() != ifd.getImageLength()) {
      tp.getSamples(ifd, buf, x,
        getIndex(coords[0], 0, coords[2]), w, 1);
    }
    else tp.getSamples(ifd, buf, x, y, w, h);

    plane.close();
    plane = null;
    tp = null;
    return buf;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    if (isOIB) {
      return noPixels ? null : new String[] {currentId};
    }

    final List<String> files = new ArrayList<String>();
    if (usedFiles != null) {
      for (String file : usedFiles) {
        String f = file.toLowerCase();
        if (!f.endsWith(".tif") && !f.endsWith(".tiff") && !f.endsWith(".bmp"))
        {
          if (!files.contains(file)) {
            files.add(file);
          }
        }
      }
    }
    if (!noPixels) {
      if (getSeries() == 0 && tiffs != null) {
        files.addAll(tiffs);
      }
      else if (getSeries() == 1 && previewNames != null) {
        files.addAll(previewNames);
      }
    }

    return files.toArray(new String[0]);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (thumbReader != null) thumbReader.close(fileOnly);

    if (!fileOnly) {
      tiffs = usedFiles = null;
      filenames.clear();
      roiFilenames.clear();
      thumbReader = null;
      thumbId = null;
      isOIB = false;
      previewNames = null;
      if (poi != null) poi.close();
      poi = null;
      lastChannel = 0;
      wavelengths = null;
      illuminations = null;
      oibMapping = null;
      code = size = null;
      pixelSize = null;
      imageDepth = 0;
      pixelSizeX = pixelSizeY = null;
      validBits = 0;
      pinholeSize = null;
      magnification = lensNA = objectiveName = workingDistance = null;
      creationDate = null;
      lut = null;
      channels = null;
      planes = null;
      if (lutNames != null) {
        lutNames.clear();
      }
      ifds.clear();
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    parser.setCommentDelimiter(null);

    isOIB = checkSuffix(id, OIB_SUFFIX);

    if (isOIB) {
      initPOIService();
    }

    // mappedOIF is used to distinguish between datasets that are being read
    // directly (e.g. using ImageJ or showinf), and datasets that are being
    // imported through omebf. In the latter case, the necessary directory
    // structure is not preserved (only relative file names are stored in
    // OMEIS), so we will need to use slightly different logic to build the
    // list of associated files.
    boolean mappedOIF = !isOIB && !new File(id).getAbsoluteFile().exists();

    wavelengths = new ArrayList<Double>();
    illuminations = new ArrayList<String>();
    channels = new ArrayList<ChannelData>();
    planes = new ArrayList<PlaneData>();

    String oifName = null;

    if (isOIB) {
      oifName = mapOIBFiles();
    }
    else {
      // make sure we have the OIF file, not a TIFF
      if (!checkSuffix(id, OIF_SUFFIX)) {
        currentId = findOIFFile(id);
        initFile(currentId);
      }
      oifName = currentId;
    }

    String oifPath = new Location(oifName).getAbsoluteFile().getAbsolutePath();
    if (mappedOIF) {
      oifPath = oifName.substring(0, oifName.lastIndexOf(File.separator) + 1);
    }
    String path = isOIB ? "" :
      oifPath.substring(0, oifPath.lastIndexOf(File.separator) + 1);

    try {
      RandomAccessInputStream s = getFile(oifName);
      s.close();
    }
    catch (IOException e) {
      oifName = oifName.replaceAll(".oif", ".OIF");
    }

    // parse key/value pairs from the OIF file

    code = new String[NUM_DIMENSIONS];
    size = new String[NUM_DIMENSIONS];
    pixelSize = new Double[NUM_DIMENSIONS];

    previewNames = new ArrayList<String>();
    Map<Integer, String> previewFileNames = new HashMap<Integer, String>();
    boolean laserEnabled = true;

    IniList f = getIniFile(oifName);

    IniTable saveInfo = f.getTable("ProfileSaveInfo");
    String[] saveKeys = saveInfo.keySet().toArray(new String[saveInfo.size()]);
    for (String key : saveKeys) {
      String value = saveInfo.get(key).toString();
      value = sanitizeValue(value).trim();

      if (key.startsWith("IniFileName") && key.indexOf("Thumb") == -1 &&
        !isPreviewName(value))
      {
        filenames.put(new Integer(key.substring(11)), value);
      }
      else if (key.startsWith("RoiFileName") && key.indexOf("Thumb") == -1 &&
        !isPreviewName(value))
      {
        try {
          roiFilenames.put(new Integer(key.substring(11)), value);
        }
        catch (NumberFormatException e) { }
      }
      else if (key.equals("PtyFileNameS")) ptyStart = value;
      else if (key.equals("PtyFileNameE")) ptyEnd = value;
      else if (key.equals("PtyFileNameT2")) ptyPattern = value;
      else if (key.indexOf("Thumb") != -1) {
        if (thumbId == null) thumbId = value.trim();
      }
      else if (key.startsWith("LutFileName")) {
        lutNames.add(path + value);
      }
      else if (isPreviewName(value)) {
        try {
          RandomAccessInputStream s = getFile(path + value.trim());
          if (s != null) {
            s.close();
            previewFileNames.put(Integer.valueOf(key.substring(11)),
              path + value.trim());
          }
        }
        catch (FormatException e) {
          LOGGER.debug("Preview file not found", e);
        }
        catch (IOException e) {
          LOGGER.debug("Preview file not found", e);
        }
      }
    }

    // Store sorted list of preview names
    for (Integer key : previewFileNames.keySet()) {
      previewNames.add(previewFileNames.get(key));
    }

    if (filenames.isEmpty()) addPtyFiles();

    for (int i=0; i<NUM_DIMENSIONS; i++) {
      IniTable commonParams = f.getTable("Axis " + i + " Parameters Common");
      code[i] = commonParams.get("AxisCode");
      size[i] = commonParams.get("MaxSize");

      double end = Double.parseDouble(commonParams.get("EndPosition"));
      double start = Double.parseDouble(commonParams.get("StartPosition"));
      pixelSize[i] = end - start;
    }

    IniTable referenceParams = f.getTable("Reference Image Parameter");
    imageDepth = Integer.parseInt(referenceParams.get("ImageDepth"));
    pixelSizeX = referenceParams.get("WidthConvertValue");
    pixelSizeY = referenceParams.get("HeightConvertValue");
    String ripValidBitCounts = referenceParams.get("ValidBitCounts");
    if (ripValidBitCounts != null) {
      validBits = Integer.parseInt(ripValidBitCounts);
    }

    int index = 0;

    IniTable laser = f.getTable("Laser " + index + " Parameters");
    while (laser != null) {
      laserEnabled = laser.get("Laser Enable").equals("1");
      if (laserEnabled) {
        wavelengths.add(new Double(laser.get("LaserWavelength")));
      }

      creationDate = laser.get("ImageCaputreDate");
      if (creationDate == null) {
        creationDate = laser.get("ImageCaptureDate");
      }

      index++;
      laser = f.getTable("Laser " + index + " Parameters");
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      index = 1;
      IniTable guiChannel = f.getTable("GUI Channel " + index + " Parameters");
      while (guiChannel != null) {
        ChannelData channel = new ChannelData();
        String gain = guiChannel.get("AnalogPMTGain");
        if (gain != null) channel.gain = new Double(gain);
        String voltage = guiChannel.get("AnalogPMTVoltage");
        if (voltage != null) channel.voltage = new Double(voltage);
        channel.barrierFilter = guiChannel.get("BF Name");
        channel.active = Integer.parseInt(guiChannel.get("CH Activate")) != 0;
        channel.name = guiChannel.get("CH Name");
        channel.dyeName = guiChannel.get("DyeName");
        channel.emissionFilter = guiChannel.get("EmissionDM Name");
        String emWave = guiChannel.get("EmissionWavelength");
        if (emWave != null) channel.emWave = new Double(emWave);
        channel.excitationFilter = guiChannel.get("ExcitationDM Name");
        String exWave = guiChannel.get("ExcitationWavelength");
        if (emWave != null) channel.exWave = new Double(exWave);
        channels.add(channel);
        index++;
        guiChannel = f.getTable("GUI Channel " + index + " Parameters");
      }

      index = 1;
      IniTable channel = f.getTable("Channel " + index + " Parameters");
      while (channel != null) {
        String illumination = channel.get("LightType");
        if (illumination != null) illumination = illumination.toLowerCase();
        if (illumination == null) {
          // Ignored
        }
        else if (illumination.indexOf("fluorescence") != -1) {
          illumination = "Epifluorescence";
        }
        else if (illumination.indexOf("transmitted") != -1) {
          illumination = "Transmitted";
        }
        else illumination = null;
        illuminations.add(illumination);
        index++;
        channel = f.getTable("Channel " + index + " Parameters");
      }

      HashMap<String, String> iniMap = f.flattenIntoHashMap();
      metadata.putAll(iniMap);
    }

    LOGGER.info("Initializing helper readers");

    // populate core metadata for preview series

    if (previewNames.size() > 0) {
      final List<String> v = new ArrayList<String>();
      for (int i=0; i<previewNames.size(); i++) {
        String ss = previewNames.get(i);
        ss = replaceExtension(ss, "pty", "tif");
        if (ss.endsWith(".tif")) v.add(ss);
      }
      previewNames = v;
      if (previewNames.size() > 0) {
        core.clear();
        core.add(new CoreMetadata());
        core.add(new CoreMetadata());
        IFDList ifds = null;
        CoreMetadata ms1 = core.get(1);
        for (String previewName : previewNames) {
          RandomAccessInputStream preview = getFile(previewName);
          TiffParser tp = new TiffParser(preview);
          ifds = tp.getIFDs();
          preview.close();
          tp = null;
          ms1.imageCount += ifds.size();
        }
        ms1.sizeX = (int) ifds.get(0).getImageWidth();
        ms1.sizeY = (int) ifds.get(0).getImageLength();
        ms1.sizeZ = 1;
        ms1.sizeT = 1;
        ms1.sizeC = ms1.imageCount;
        ms1.rgb = false;
        int bits = ifds.get(0).getBitsPerSample()[0];
        while ((bits % 8) != 0) bits++;
        bits /= 8;
        ms1.pixelType = FormatTools.pixelTypeFromBytes(bits, false, false);
        ms1.dimensionOrder = "XYCZT";
        ms1.indexed = false;
      }
    }
    CoreMetadata ms0 = core.get(0);
    ms0.imageCount = filenames.size();
    tiffs = new ArrayList<String>(getImageCount());

    thumbReader = new BMPReader();
    if (thumbId != null) {
      thumbId = replaceExtension(thumbId, "pty", "bmp");
      thumbId = sanitizeFile(thumbId, path);
    }

    LOGGER.info("Reading additional metadata");

    // open each INI file (.pty extension) and build list of TIFF files

    String tiffPath = null;

    ms0.dimensionOrder = "XY";

    final Map<String, String> values = new HashMap<String, String>();
    final List<String> baseKeys = new ArrayList<String>();

    for (int i=0, ii=0; ii<getImageCount(); i++, ii++) {
      String file = filenames.get(i);
      while (file == null) file = filenames.get(++i);
      file = sanitizeFile(file, path);

      if (file.indexOf(File.separator) != -1) {
        tiffPath = file.substring(0, file.lastIndexOf(File.separator));
      }
      else tiffPath = file;

      Location ptyFile = new Location(file);
      if (!isOIB && !ptyFile.exists()) {
        LOGGER.warn("Could not find .pty file ({}); guessing at the " +
          "corresponding TIFF file.", file);
        String tiff = replaceExtension(file, "pty", "tif");
        Location tiffFile = new Location(tiff);
        if (tiffFile.exists()) {
          tiffs.add(ii, tiffFile.getAbsolutePath());
          continue;
        }
        else {
          if (!tiffFile.getParentFile().exists()) {
            String realOIFName = new Location(currentId).getName();
            String basePath = tiffFile.getParentFile().getParent();

            if (mappedOIF) {
              tiffPath = basePath + File.separator + realOIFName + ".files";
              ptyFile = new Location(tiffPath, ptyFile.getName());
              file = ptyFile.getAbsolutePath();
            }
            else {
              Location newFile = new Location(basePath, realOIFName + ".files");
              ptyFile = new Location(newFile, ptyFile.getName());
              file = ptyFile.getAbsolutePath();
              tiffPath = newFile.getAbsolutePath();
            }
          }
        }
      }
      else if (!isOIB) {
        file = ptyFile.getAbsolutePath();
      }

      IniList pty = getIniFile(file);

      IniTable fileInfo = pty.getTable("File Info");
      file = sanitizeValue(fileInfo.get("DataName"));
      if (!isPreviewName(file)) {
        while (file.indexOf("GST") != -1) {
          file = removeGST(file);
        }
        if (isOIB) {
          file = tiffPath + File.separator + file;
        }
        else file = new Location(tiffPath, file).getAbsolutePath();
        file = replaceExtension(file, "pty", "tif");
        tiffs.add(ii, file);
      }

      PlaneData plane = new PlaneData();
      for (int dim=0; dim<NUM_DIMENSIONS; dim++) {
        IniTable axis = pty.getTable("Axis " + dim + " Parameters");
        if (axis == null) break;
        boolean addAxis = Integer.parseInt(axis.get("Number")) > 1;
        if (dim == 2) {
          if (addAxis && getDimensionOrder().indexOf("C") == -1) {
            ms0.dimensionOrder += "C";
          }
        }
        else if (dim == 3) {
          if (addAxis && getDimensionOrder().indexOf("Z") == -1) {
            ms0.dimensionOrder += "Z";
          }
          final Double number = Double.valueOf(axis.get("AbsPositionValue"));
          plane.positionZ = new Length(number, UNITS.REFERENCEFRAME);
        }
        else if (dim == 4) {
          if (addAxis && getDimensionOrder().indexOf("T") == -1) {
            ms0.dimensionOrder += "T";
          }
          // divide by 1000, as the position is in milliseconds
          // and DeltaT is in seconds
          plane.deltaT =
            Double.parseDouble(axis.get("AbsPositionValue")) / 1000;
        }
        else if (dim == 7) {
          try {
            String xPos = axis.get("AbsPositionValueX");
            if (xPos != null) {
              final Double number = Double.valueOf(xPos);
              plane.positionX = new Length(number, UNITS.REFERENCEFRAME);
            }
          }
          catch (NumberFormatException e) { }
          try {
            String yPos = axis.get("AbsPositionValueY");
            if (yPos != null) {
              final Double number = Double.valueOf(yPos);
              plane.positionY = new Length(number, UNITS.REFERENCEFRAME);
            }
          }
          catch (NumberFormatException e) { }
        }
      }
      ms0.bitsPerPixel = validBits;
      planes.add(plane);

      IniTable acquisition = pty.getTable("Acquisition Parameters Common");
      if (acquisition != null) {
        magnification = acquisition.get("Magnification");
        lensNA = acquisition.get("ObjectiveLens NAValue");
        objectiveName = acquisition.get("ObjectiveLens Name");
        workingDistance = acquisition.get("ObjectiveLens WDValue");
        pinholeSize = acquisition.get("PinholeDiameter");
        String validBitCounts = acquisition.get("ValidBitCounts");
        if (validBitCounts != null) {
          ms0.bitsPerPixel = Integer.parseInt(validBitCounts);
        }
      }

      if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
        for (IniTable table : pty) {
          String[] keys = table.keySet().toArray(new String[table.size()]);
          for (String key : keys) {
            values.put("Image " + ii + " : " + key, table.get(key));
            if (!baseKeys.contains(key)) baseKeys.add(key);
          }
        }
      }
    }

    for (String key : baseKeys) {
      if (key.equals("DataName") || key.indexOf("FileName") >= 0) break;
      boolean equal = true;
      String first = values.get("Image 0 : " + key);
      for (int i=1; i<getImageCount(); i++) {
        if (!first.equals(values.get("Image " + i + " : " + key))) {
          equal = false;
          break;
        }
      }
      if (equal) {
        addGlobalMeta(key, first);
      }
      else {
        for (int i=0; i<getImageCount(); i++) {
          String k = "Image " + i + " : " + key;
          addGlobalMeta(k, values.get(k));
        }
      }
    }

    if (tiffs.size() != getImageCount()) {
      ms0.imageCount = tiffs.size();
    }

    usedFiles = new ArrayList<String>();

    if (tiffPath != null) {
      usedFiles.add(isOIB ? id : oifName);
      if (!isOIB) {
        Location dir = new Location(tiffPath);
        if (!mappedOIF && !dir.exists()) {
          throw new FormatException(
            "Required directory " + tiffPath + " was not found.");
        }
        String[] list = mappedOIF ?
          Location.getIdMap().keySet().toArray(new String[0]) : dir.list(true);
        for (int i=0; i<list.length; i++) {
          if (mappedOIF) usedFiles.add(list[i]);
          else {
            String p = new Location(tiffPath, list[i]).getAbsolutePath();
            String check = p.toLowerCase();
            if (!check.endsWith(".tif") && !check.endsWith(".pty") &&
              !check.endsWith(".roi") && !check.endsWith(".lut") &&
              !check.endsWith(".bmp"))
            {
              continue;
            }
            usedFiles.add(p);
          }
        }
      }
    }

    LOGGER.info("Populating metadata");

    // calculate axis sizes

    int realChannels = 0;
    for (int i=0; i<NUM_DIMENSIONS; i++) {
      int ss = Integer.parseInt(size[i]);
      if (pixelSize[i] == null) pixelSize[i] = 1.0;
      if (code[i].equals("X")) ms0.sizeX = ss;
      else if (code[i].equals("Y") && ss > 1) ms0.sizeY = ss;
      else if (code[i].equals("Z")) {
        if (getSizeY() == 0) {
          ms0.sizeY = ss;
        }
        else {
          ms0.sizeZ = ss;
          // Z size stored in nm
          pixelSizeZ =
            Math.abs((pixelSize[i].doubleValue() / (getSizeZ() - 1)) / 1000);
        }
      }
      else if (code[i].equals("T")) {
        if (getSizeY() == 0) {
          ms0.sizeY = ss;
        }
        else {
          ms0.sizeT = ss;
          pixelSizeT =
            Math.abs((pixelSize[i].doubleValue() / (getSizeT() - 1)) / 1000);
        }
      }
      else if (ss > 0) {
        if (getSizeC() == 0) ms0.sizeC = ss;
        else ms0.sizeC *= ss;
        if (code[i].equals("C")) realChannels = ss;
      }
    }

    if (getSizeZ() == 0) ms0.sizeZ = 1;
    if (getSizeC() == 0) ms0.sizeC = 1;
    if (getSizeT() == 0) ms0.sizeT = 1;

    if (getImageCount() == getSizeC() && getSizeY() == 1) {
      ms0.imageCount *= getSizeZ() * getSizeT();
    }
    else if (getImageCount() == getSizeC()) {
      ms0.sizeZ = 1;
      ms0.sizeT = 1;
    }

    if (getSizeZ() * getSizeT() * getSizeC() != getImageCount()) {
      int diff = (getSizeZ() * getSizeC() * getSizeT()) - getImageCount();
      if (diff == previewNames.size() || diff < 0) {
        diff /= getSizeC();
        if (getSizeT() > 1 && getSizeZ() == 1) ms0.sizeT -= diff;
        else if (getSizeZ() > 1 && getSizeT() == 1) ms0.sizeZ -= diff;
      }
      else ms0.imageCount += diff;
    }

    if (getSizeC() > 1 && getSizeZ() == 1 && getSizeT() == 1) {
      if (getDimensionOrder().indexOf("C") == -1) ms0.dimensionOrder += "C";
    }

    if (getDimensionOrder().indexOf("Z") == -1) ms0.dimensionOrder += "Z";
    if (getDimensionOrder().indexOf("C") == -1) ms0.dimensionOrder += "C";
    if (getDimensionOrder().indexOf("T") == -1) ms0.dimensionOrder += "T";

    ms0.pixelType =
      FormatTools.pixelTypeFromBytes(imageDepth, false, false);

    // set up thumbnail file mapping

    try {
      RandomAccessInputStream thumb = getFile(thumbId);
      byte[] b = new byte[(int) thumb.length()];
      thumb.read(b);
      thumb.close();
      Location.mapFile("thumbnail.bmp", new ByteArrayHandle(b));
      thumbReader.setId("thumbnail.bmp");
      for (int i=0; i<getSeriesCount(); i++) {
        core.get(i).thumbSizeX = thumbReader.getSizeX();
        core.get(i).thumbSizeY = thumbReader.getSizeY();
      }
      thumbReader.close();
      Location.mapFile("thumbnail.bmp", null);
    }
    catch (IOException e) {
      LOGGER.debug("Could not read thumbnail", e);
    }
    catch (FormatException e) {
      LOGGER.debug("Could not read thumbnail", e);
    }

    // initialize lookup table

    lut = new short[getSizeC()][3][65536];
    byte[] buffer = new byte[65536 * 4];
    int count = (int) Math.min(getSizeC(), lutNames.size());
    for (int c=0; c<count; c++) {
      Exception exc = null;
      try {
        RandomAccessInputStream stream = getFile(lutNames.get(c));
        stream.seek(stream.length() - 65536 * 4);
        stream.read(buffer);
        stream.close();
        for (int q=0; q<buffer.length; q+=4) {
          lut[c][0][q / 4] = (short) ((buffer[q + 2] & 0xff) * 257);
          lut[c][1][q / 4] = (short) ((buffer[q + 1] & 0xff) * 257);
          lut[c][2][q / 4] = (short) ((buffer[q] & 0xff) * 257);
        }
      }
      catch (IOException e) { exc = e; }
      catch (FormatException e) { exc = e; }
      if (exc != null) {
        LOGGER.debug("Could not read LUT", exc);
        lut = null;
        break;
      }
    }

    for (int i=0; i<getSeriesCount(); i++) {
      CoreMetadata ms = core.get(i);
      ms.rgb = false;
      ms.littleEndian = true;
      ms.interleaved = false;
      ms.metadataComplete = true;
      ms.indexed = lut != null;
      ms.falseColor = true;

      int nFiles = i == 0 ? tiffs.size() : previewNames.size();
      for (int file=0; file<nFiles; file++) {
        RandomAccessInputStream plane =
          getFile(i == 0 ? tiffs.get(file) : previewNames.get(file));
        if (plane == null) {
          ifds.add(null);
          continue;
        }
        try {
          TiffParser tp = new TiffParser(plane);
          IFDList ifd = tp.getIFDs();
          ifds.add(ifd);
        }
        finally {
          plane.close();
        }
      }
    }

    // populate MetadataStore

    MetadataStore store = makeFilterMetadata();

    MetadataTools.populatePixels(store, this, true);

    if (creationDate != null) {
      creationDate = creationDate.replaceAll("'", "");
      creationDate = DateTools.formatDate(creationDate, DATE_FORMAT);
    }

    for (int i=0; i<getSeriesCount(); i++) {
      // populate Image data
      store.setImageName("Series " + (i + 1), i);
      if (creationDate != null) store.setImageAcquisitionDate(
          new Timestamp(creationDate), i);
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      populateMetadataStore(store, path);
    }
  }

  private void populateMetadataStore(MetadataStore store, String path)
    throws FormatException, IOException
  {
    String instrumentID = MetadataTools.createLSID("Instrument", 0);
    store.setInstrumentID(instrumentID, 0);

    for (int i=0; i<getSeriesCount(); i++) {
      // link Instrument and Image
      store.setImageInstrumentRef(instrumentID, i);

      // populate Dimensions data

      if (pixelSizeX != null) {
        Double sizeX = new Double(pixelSizeX);
        Length size = FormatTools.getPhysicalSizeX(sizeX);
        if (size != null) {
          store.setPixelsPhysicalSizeX(size, i);
        }
      }
      if (pixelSizeY != null) {
        Double sizeY = new Double(pixelSizeY);
        Length size = FormatTools.getPhysicalSizeY(sizeY);
        if (size != null) {
          store.setPixelsPhysicalSizeY(size, i);
        }
      }
      if (pixelSizeZ == Double.NEGATIVE_INFINITY ||
        pixelSizeZ == Double.POSITIVE_INFINITY || getSizeZ() == 1)
      {
        pixelSizeZ = 1d;
      }
      if (pixelSizeT == Double.NEGATIVE_INFINITY ||
        pixelSizeT == Double.POSITIVE_INFINITY || getSizeT() == 1)
      {
        pixelSizeT = 1d;
      }

      Length sizeZ = FormatTools.getPhysicalSizeZ(pixelSizeZ);
      if (sizeZ != null) {
        store.setPixelsPhysicalSizeZ(sizeZ, i);
      }
      store.setPixelsTimeIncrement(new Time(pixelSizeT, UNITS.SECOND), i);

      for (int p=0; p<core.get(i).imageCount; p++) {
        store.setPlaneDeltaT(new Time(pixelSizeT * p, UNITS.SECOND), i, p);
      }

      // populate LogicalChannel data

      for (int c=0; c<core.get(i).sizeC; c++) {
        if (c < illuminations.size()) {
          store.setChannelIlluminationType(
            getIlluminationType(illuminations.get(c)), i, c);
        }
      }
    }

    int channelIndex = 0;
    for (ChannelData channel : channels) {
      if (!channel.active) continue;
      if (channelIndex >= getEffectiveSizeC()) break;

      // populate Detector data
      String detectorID = MetadataTools.createLSID("Detector", 0, channelIndex);
      store.setDetectorID(detectorID, 0, channelIndex);
      store.setDetectorSettingsID(detectorID, 0, channelIndex);

      store.setDetectorGain(channel.gain, 0, channelIndex);
      ElectricPotential theVoltage = FormatTools.createElectricPotential(channel.voltage, UNITS.VOLT);
      if (theVoltage != null) {
        store.setDetectorVoltage(
              theVoltage, 0, channelIndex);
      }
      store.setDetectorType(getDetectorType("PMT"), 0, channelIndex);

      // populate LogicalChannel data

      store.setChannelName(channel.name, 0, channelIndex);
      String lightSourceID =
        MetadataTools.createLSID("LightSource", 0, channelIndex);
      store.setChannelLightSourceSettingsID(lightSourceID, 0, channelIndex);

      Length wavelength = FormatTools.getWavelength(channel.exWave);
      Length emission = FormatTools.getEmissionWavelength(channel.emWave);
      Length excitation = FormatTools.getExcitationWavelength(channel.exWave);

      if (emission != null) {
        store.setChannelEmissionWavelength(emission, 0, channelIndex);
      }
      if (excitation != null) {
        store.setChannelExcitationWavelength(excitation, 0, channelIndex);
      }
      if (wavelength != null) {
        store.setChannelLightSourceSettingsWavelength(
          wavelength, 0, channelIndex);
      }

      // populate Filter data
      if (channel.barrierFilter != null) {
        String filterID = MetadataTools.createLSID("Filter", 0, channelIndex);
        store.setFilterID(filterID, 0, channelIndex);
        store.setFilterModel(channel.barrierFilter, 0, channelIndex);

        if (channel.barrierFilter.indexOf("-") != -1) {
          String[] emValues = channel.barrierFilter.split("-");
          for (int i=0; i<emValues.length; i++) {
            emValues[i] = emValues[i].replaceAll("\\D", "");
          }
          try {
            Double cutIn = new Double(emValues[0]);
            Double cutOut = new Double(emValues[1]);

            Length in = FormatTools.getCutIn(cutIn);
            Length out = FormatTools.getCutOut(cutOut);

            if (in != null) {
              store.setTransmittanceRangeCutIn(in, 0, channelIndex);
            }
            if (out != null) {
              store.setTransmittanceRangeCutOut(out, 0, channelIndex);
            }
          }
          catch (NumberFormatException e) { }
        }
        store.setLightPathEmissionFilterRef(filterID, 0, channelIndex, 0);
      }

      // populate FilterSet data
      int emIndex = channelIndex * 2;
      int exIndex = channelIndex * 2 + 1;
      String emFilter = MetadataTools.createLSID("Dichroic", 0, emIndex);
      String exFilter = MetadataTools.createLSID("Dichroic", 0, exIndex);

      // populate Dichroic data
      store.setDichroicID(emFilter, 0, emIndex);
      store.setDichroicModel(channel.emissionFilter, 0, emIndex);

      store.setDichroicID(exFilter, 0, exIndex);
      store.setDichroicModel(channel.excitationFilter, 0, exIndex);

      store.setLightPathDichroicRef(exFilter, 0, channelIndex);

      // populate Laser data
      store.setLaserID(lightSourceID, 0, channelIndex);
      store.setLaserLaserMedium(getLaserMedium(channel.dyeName),
        0, channelIndex);
      if (channelIndex < wavelengths.size()) {
          Length wave =
          FormatTools.getWavelength(wavelengths.get(channelIndex));
        if (wave != null) {
          store.setLaserWavelength(wave, 0, channelIndex);
        }
      }
      store.setLaserType(getLaserType("Other"), 0, channelIndex);

      channelIndex++;
    }

    // populate Objective data

    if (lensNA != null) store.setObjectiveLensNA(new Double(lensNA), 0, 0);
    store.setObjectiveModel(objectiveName, 0, 0);
    if (magnification != null) {
      Double mag = Double.parseDouble(magnification);
      store.setObjectiveNominalMagnification(mag, 0, 0);
    }
    if (workingDistance != null) {
      store.setObjectiveWorkingDistance(new Length(new Double(workingDistance), UNITS.MICROMETER), 0, 0);
    }
    store.setObjectiveCorrection(getCorrection("Other"), 0, 0);
    store.setObjectiveImmersion(getImmersion("Other"), 0, 0);

    // link Objective to Image using ObjectiveSettings
    String objectiveID = MetadataTools.createLSID("Objective", 0, 0);
    store.setObjectiveID(objectiveID, 0, 0);
    store.setObjectiveSettingsID(objectiveID, 0);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.NO_OVERLAYS) {
      int nextROI = -1;

      // populate ROI data - there is one ROI file per plane
      for (int i=0; i<roiFilenames.size(); i++) {
        if (i >= getImageCount()) break;
        String filename = roiFilenames.get(i);
        filename = sanitizeFile(filename, path);
        nextROI = parseROIFile(filename, store, nextROI, i);
      }
    }

    // Populate metadata for the planes
    for (int i=0; i<planes.size(); i++) {
      PlaneData plane = planes.get(i);
      if (plane.deltaT != null) {
        store.setPlaneDeltaT(new Time(plane.deltaT, UNITS.SECOND), 0, i);
      }
      store.setPlanePositionX(plane.positionX, 0, i);
      store.setPlanePositionY(plane.positionY, 0, i);
      store.setPlanePositionZ(plane.positionZ, 0, i);
    }
  }

  private int parseROIFile(String filename, MetadataStore store, int nextROI,
    int plane) throws FormatException, IOException
  {
    int[] coordinates = getZCTCoords(plane);
    IniList roiFile = null;
    try {
      roiFile = getIniFile(filename);
    }
    catch (FormatException e) {
      LOGGER.debug("Could not parse ROI file {}", filename, e);
      return nextROI;
    }
    catch (IOException e) {
      LOGGER.debug("Could not parse ROI file {}", filename, e);
      return nextROI;
    }

    boolean validROI = false;
    int shape = -1;
    int shapeType = -1;

    String[] xc = null, yc = null;
    int divide = 0;
    int fontSize = 0, lineWidth = 0, angle = 0;
    String fontName = null, name = null;

    for (IniTable table : roiFile) {
      String tableName = table.get(IniTable.HEADER_KEY);
      if (tableName.equals("ROIBase FileInformation")) {
        try {
          String roiName = table.get("Name").replaceAll("\"", "");
          validROI = Integer.parseInt(roiName) > 1;
        }
        catch (NumberFormatException e) { validROI = false; }
        if (!validROI) continue;
      }
      else if (tableName.equals("ROIBase Body")) {
        shapeType = Integer.parseInt(table.get("SHAPE"));
        divide = Integer.parseInt(table.get("DIVIDE"));
        String[] fontAttributes = table.get("FONT").split(",");
        fontName = fontAttributes[0];
        fontSize = Integer.parseInt(fontAttributes[1]);

        Length font = FormatTools.getFontSize(fontSize);

        lineWidth = Integer.parseInt(table.get("LINEWIDTH"));
        name = table.get("NAME");
        angle = Integer.parseInt(table.get("ANGLE"));
        xc = table.get("X").split(",");
        yc = table.get("Y").split(",");

        int x = Integer.parseInt(xc[0]);
        int width = xc.length > 1 ? Integer.parseInt(xc[1]) - x : 0;
        int y = Integer.parseInt(yc[0]);
        int height = yc.length > 1 ? Integer.parseInt(yc[1]) - y : 0;

        if (width + x <= getSizeX() && height + y <= getSizeY()) {
          shape++;

          final Integer zIndex = coordinates[0];
          final Integer tIndex = coordinates[2];

          if (shape == 0) {
            nextROI++;

            if (shapeType == POINT || shapeType == GRID ||
              shapeType == RECTANGLE || shapeType == LINE ||
              shapeType == CIRCLE || shapeType == ELLIPSE ||
              shapeType == POLYGON || shapeType == FREE_SHAPE ||
              shapeType == POLYLINE || shapeType == FREE_LINE)
            {
              String roiID = MetadataTools.createLSID("ROI", nextROI);
              store.setROIID(roiID, nextROI);
              store.setImageROIRef(roiID, 0, nextROI);
            }
          }

          String shapeID = MetadataTools.createLSID("Shape", nextROI, shape);

          if (shapeType == POINT) {
            store.setPointID(shapeID, nextROI, shape);
            store.setPointTheZ(new NonNegativeInteger(zIndex), nextROI, shape);
            store.setPointTheT(new NonNegativeInteger(tIndex), nextROI, shape);
            if (font != null) {
              store.setPointFontSize(font, nextROI, shape);
            }
            Length l = new Length((double) lineWidth, UNITS.PIXEL);
            store.setPointStrokeWidth(l, nextROI, shape);

            store.setPointX(new Double(xc[0]), nextROI, shape);
            store.setPointY(new Double(yc[0]), nextROI, shape);
          }
          else if (shapeType == GRID || shapeType == RECTANGLE) {
            if (shapeType == RECTANGLE) divide = 1;
            width /= divide;
            height /= divide;
            for (int row=0; row<divide; row++) {
              for (int col=0; col<divide; col++) {
                double realX = x + col * width;
                double realY = y + row * height;

                shapeID = MetadataTools.createLSID("Shape", nextROI, shape);
                store.setRectangleID(shapeID, nextROI, shape);
                store.setRectangleX(realX, nextROI, shape);
                store.setRectangleY(realY, nextROI, shape);
                store.setRectangleWidth((double) width, nextROI, shape);
                store.setRectangleHeight((double) height, nextROI, shape);

                store.setRectangleTheZ(
                  new NonNegativeInteger(zIndex), nextROI, shape);
                store.setRectangleTheT(
                  new NonNegativeInteger(tIndex), nextROI, shape);
                if (font != null) {
                  store.setRectangleFontSize(font, nextROI, shape);
                }
                Length l = new Length((double) lineWidth, UNITS.PIXEL);
                store.setRectangleStrokeWidth(l, nextROI, shape);

                double centerX = realX + (width / 2);
                double centerY = realY + (height / 2);

                store.setRectangleTransform(
                  getRotationTransform(angle), nextROI, shape);

                if (row < divide - 1 || col < divide - 1) shape++;
              }
            }
          }
          else if (shapeType == LINE) {
            store.setLineID(shapeID, nextROI, shape);
            store.setLineX1((double) x, nextROI, shape);
            store.setLineY1((double) y, nextROI, shape);
            store.setLineX2((double) (x + width), nextROI, shape);
            store.setLineY2((double) (y + height), nextROI, shape);

            store.setLineTheZ(new NonNegativeInteger(zIndex), nextROI, shape);
            store.setLineTheT(new NonNegativeInteger(tIndex), nextROI, shape);
            if (font != null) {
              store.setLineFontSize(font, nextROI, shape);
            }
            Length l = new Length((double) lineWidth, UNITS.PIXEL);
            store.setLineStrokeWidth(l, nextROI, shape);

            int centerX = x + (width / 2);
            int centerY = y + (height / 2);

            store.setLineTransform(getRotationTransform(angle), nextROI, shape);
          }
          else if (shapeType == CIRCLE || shapeType == ELLIPSE) {
            double rx = width / 2;
            double ry = shapeType == CIRCLE ? rx : height / 2;
            store.setEllipseID(shapeID, nextROI, shape);
            store.setEllipseX(x + rx, nextROI, shape);
            store.setEllipseY(y + ry, nextROI, shape);
            store.setEllipseRadiusX(rx, nextROI, shape);
            store.setEllipseRadiusY(ry, nextROI, shape);

            store.setEllipseTheZ(
                new NonNegativeInteger(zIndex), nextROI, shape);
            store.setEllipseTheT(
                new NonNegativeInteger(tIndex), nextROI, shape);
            if (font != null) {
              store.setEllipseFontSize(font, nextROI, shape);
            }
            Length l = new Length((double) lineWidth, UNITS.PIXEL);
            store.setEllipseStrokeWidth(l, nextROI, shape);
            store.setEllipseTransform(
              getRotationTransform(angle), nextROI, shape);
          }
          else if (shapeType == POLYGON || shapeType == FREE_SHAPE ||
            shapeType == POLYLINE || shapeType == FREE_LINE)
          {
            StringBuffer points = new StringBuffer();
            for (int point=0; point<xc.length; point++) {
              points.append(xc[point]);
              points.append(",");
              points.append(yc[point]);
              if (point < xc.length - 1) points.append(" ");
            }
            if (shapeType == POLYLINE || shapeType == FREE_LINE) {
              store.setPolylineID(shapeID, nextROI, shape);
              store.setPolylinePoints(points.toString(), nextROI, shape);
              store.setPolylineTransform(
                getRotationTransform(angle), nextROI, shape);
              store.setPolylineTheZ(
                  new NonNegativeInteger(zIndex), nextROI, shape);
              store.setPolylineTheT(
                  new NonNegativeInteger(tIndex), nextROI, shape);
              if (font != null) {
                store.setPolylineFontSize(font, nextROI, shape);
              }
              Length l = new Length((double) lineWidth, UNITS.PIXEL);
              store.setPolylineStrokeWidth(l, nextROI, shape);
            }
            else {
              store.setPolygonID(shapeID, nextROI, shape);
              store.setPolygonPoints(points.toString(), nextROI, shape);
              store.setPolygonTransform(
                getRotationTransform(angle), nextROI, shape);
              store.setPolygonTheZ(
                  new NonNegativeInteger(zIndex), nextROI, shape);
              store.setPolygonTheT(
                  new NonNegativeInteger(tIndex), nextROI, shape);
              if (font != null) {
                store.setPolygonFontSize(font, nextROI, shape);
              }
              Length l = new Length((double) lineWidth, UNITS.PIXEL);
              store.setPolygonStrokeWidth(l, nextROI, shape);
            }
          }
          else {
            if (shape == 0) nextROI--;
            shape--;
          }
        }
      }
    }

    return nextROI;
  }

  private void addPtyFiles() throws FormatException {
    if (ptyStart != null && ptyEnd != null) {
      // FV1000 version 2 gives the first .pty file, the last .pty and
      // the file name pattern.  Version 1 lists each .pty file individually.

      // pattern is typically 's_C%03dT%03d.pty'

      // build list of block indexes

      if (ptyPattern == null) {
        String dir =
          ptyStart.substring(0, ptyStart.indexOf(File.separator) + 1);
        ptyPattern = dir + "s_C%03dT%03d.pty";
      }

      String[] prefixes = ptyPattern.split("%03d");

      // get first and last numbers for each block
      int[] first = scanFormat(ptyPattern, ptyStart);
      int[] last = scanFormat(ptyPattern, ptyEnd);
      int[] lengths = new int[prefixes.length - 1];
      int totalFiles = 1;
      for (int i=0; i<first.length; i++) {
        lengths[i] = last[i] - first[i] + 1;
        totalFiles *= lengths[i];
      }

      // add each .pty file

      for (int file=0; file<totalFiles; file++) {
        int[] pos = FormatTools.rasterToPosition(lengths, file);
        StringBuffer pty = new StringBuffer();
        for (int block=0; block<prefixes.length; block++) {
          pty.append(prefixes[block]);

          if (block < pos.length) {
            String num = String.valueOf(pos[block] + 1);
            for (int q=0; q<3 - num.length(); q++) {
              pty.append("0");
            }
            pty.append(num);
          }
        }
        filenames.put(file, pty.toString());
      }
    }
  }

  // -- Helper methods --

  private String findOIFFile(String baseFile) throws FormatException {
    Location current = new Location(baseFile).getAbsoluteFile();
    String parent = current.getParent();
    Location tmp = new Location(parent).getParentFile();
    parent = tmp.getAbsolutePath();

    baseFile = current.getName();
    if (baseFile == null || baseFile.indexOf("_") == -1) return null;
    baseFile = baseFile.substring(0, baseFile.lastIndexOf("_"));
    if (checkSuffix(current.getName(), new String[] {"roi", "lut"})) {
      if (!new Location(tmp, baseFile + ".oif").exists() &&
        !new Location(tmp, baseFile + ".OIF").exists() &&
        baseFile.indexOf("_") >= 0)
      {
        // some metadata files have an extra underscore
        baseFile = baseFile.substring(0, baseFile.lastIndexOf("_"));
      }
    }
    baseFile += ".oif";
    tmp = new Location(tmp, baseFile);
    String oifFile = tmp.getAbsolutePath();

    if (!tmp.exists()) {
      oifFile = oifFile.substring(0, oifFile.lastIndexOf(".")) + ".OIF";
      tmp = new Location(oifFile);
      if (!tmp.exists()) {
        baseFile = current.getParent();
        baseFile = baseFile.substring(0, baseFile.lastIndexOf("."));
        baseFile = baseFile.substring(0, baseFile.lastIndexOf("."));
        tmp = new Location(baseFile + ".oif");
        oifFile = tmp.getAbsolutePath();

        if (!tmp.exists()) {
          tmp = new Location(tmp.getParent(), tmp.getName().toUpperCase());
          oifFile = tmp.getAbsolutePath();

          if (!tmp.exists()) {
            // check in parent directory
             if (parent.endsWith(File.separator)) {
              parent = parent.substring(0, parent.length() - 1);
            }
            String dir = parent.substring(parent.lastIndexOf(File.separator));
            dir = dir.substring(0, dir.lastIndexOf("."));
            tmp = new Location(parent);
            oifFile = new Location(tmp, dir).getAbsolutePath();
            if (!new Location(oifFile).exists()) {
              throw new FormatException("OIF file not found");
            }
          }
        }
      }
    }
    return oifFile;
  }

  private String mapOIBFiles() throws FormatException, IOException {
    String oifName = null;
    String infoFile = null;
    final List<String> list = poi.getDocumentList();
    for (String name : list) {
      if (name.endsWith("OibInfo.txt")) {
        infoFile = name;
        break;
      }
    }
    if (infoFile == null) {
      throw new FormatException("OibInfo.txt not found in " + currentId);
    }
    RandomAccessInputStream ras = poi.getDocumentStream(infoFile);

    oibMapping = new HashMap<String, String>();

    // set up file name mappings

    String s = DataTools.stripString(ras.readString((int) ras.length()));
    ras.close();
    String[] lines = s.split("\n");

    // sort the lines to ensure that the
    // directory key is before the file names
    Arrays.sort(lines);

    String directoryKey = null, directoryValue = null, key = null, value = null;
    for (String line : lines) {
      line = line.trim();
      if (line.indexOf("=") != -1) {
        key = line.substring(0, line.indexOf("="));
        value = line.substring(line.indexOf("=") + 1);

        if (directoryKey != null && directoryValue != null) {
          value = value.replaceAll(directoryKey, directoryValue);
        }

        value = removeGST(value);

        if (key.startsWith("Stream")) {
          value = sanitizeFile(value, "");
          if (checkSuffix(value, OIF_SUFFIX)) oifName = value;
          if (directoryKey != null && value.startsWith(directoryValue)) {
            oibMapping.put(value, "Root Entry" + File.separator +
              directoryKey + File.separator + key);
          }
          else {
            oibMapping.put(value, "Root Entry" + File.separator + key);
          }
        }
        else if (key.startsWith("Storage")) {
          directoryKey = key;
          directoryValue = value;
        }
      }
    }
    s = null;
    return oifName;
  }

  private String sanitizeValue(String value) {
    String f = value.replaceAll("\"", "");
    f = f.replace('\\', File.separatorChar);
    f = f.replace('/', File.separatorChar);
    while (f.indexOf("GST") != -1) {
      f = removeGST(f);
    }
    return f;
  }

  private String sanitizeFile(String file, String path) {
    String f = sanitizeValue(file);
    if (path.equals("")) return f;
    if (path.endsWith(File.separator)) {
      return path + f;
    }
    return path + File.separator + f;
  }

  private String removeGST(String s) {
    if (s.indexOf("GST") != -1) {
      String first = s.substring(0, s.indexOf("GST"));
      int ndx = s.indexOf(File.separator) < s.indexOf("GST") ?
        s.length() : s.indexOf(File.separator);
      String last = s.substring(s.lastIndexOf("=", ndx) + 1);
      return first + last;
    }
    return s;
  }

  private void initPOIService() throws FormatException, IOException {
    try {
      ServiceFactory factory = new ServiceFactory();
      poi = factory.getInstance(POIService.class);
    }
    catch (DependencyException de) {
      throw new FormatException("POI library not found", de);
    }

    poi.initialize(Location.getMappedId(getCurrentFile()));
  }

  private RandomAccessInputStream getFile(String name)
    throws FormatException, IOException
  {
    if (isOIB) {
      if (poi == null) {
        initPOIService();
      }

      name = name.replace('\\', File.separatorChar);
      name = name.replace('/', File.separatorChar);
      String realName = oibMapping.get(name);
      if (realName == null) {
        throw new FormatException("File " + name + " not found.");
      }
      return poi.getDocumentStream(realName);
    }
    return new RandomAccessInputStream(name, 16);
  }

  private RandomAccessInputStream getPlane(int seriesIndex, int planeIndex) {
    int file = planeIndex;
    if (seriesIndex == 0) {
      file = planeIndex / (getImageCount() / tiffs.size());
    }
    else file = planeIndex / (getImageCount() / previewNames.size());

    String filename =
      seriesIndex == 0 ? tiffs.get(file) : previewNames.get(file);
    RandomAccessInputStream plane = null;
    try {
      plane = getFile(filename);
    }
    catch (FormatException e) { }
    catch (IOException e) { }
    return plane;
  }

  private boolean isPreviewName(String name) {
    // "-R" in the file name indicates that this is a preview image
    int index = name.indexOf("-R");
    return index == name.length() - 9;
  }

  private String replaceExtension(String name, String oldExt, String newExt) {
    if (!name.endsWith("." + oldExt)) {
      return name;
    }
    return name.substring(0, name.length() - oldExt.length()) + newExt;
  }

  /* Return the numbers in the given string matching %..d style patterns */
  private static int[] scanFormat(String pattern, String string)
    throws FormatException
  {
    final List<Integer> percentOffsets = new ArrayList<Integer>();
    int offset = -1;
    for (;;) {
      offset = pattern.indexOf('%', offset + 1);
      if (offset < 0 || offset + 1 >= pattern.length()) {
        break;
      }
      if (pattern.charAt(offset + 1) != '0') {
        continue;
      }
      percentOffsets.add(offset);
    }

    int[] result = new int[percentOffsets.size()];
    int patternOffset = 0;
    offset = 0;
    for (int i=0; i<result.length; i++) {
      int percent = percentOffsets.get(i).intValue();
      if (!string.regionMatches(offset, pattern, patternOffset,
        percent - patternOffset))
      {
        throw new FormatException("String '" + string +
          "' does not match format '" + pattern + "'");
      }

      offset += percent - patternOffset;
      patternOffset = percent;

      int endOffset = offset;
      while (endOffset < string.length() &&
        Character.isDigit(string.charAt(endOffset)))
      {
        endOffset++;
      }
      result[i] = Integer.parseInt(string.substring(offset, endOffset));
      offset = endOffset;

      while (++patternOffset < pattern.length() &&
        pattern.charAt(patternOffset - 1) != 'd')
      {
        ; /* do nothing */
      }
    }

    int remaining = pattern.length() - patternOffset;

    if (string.length() - offset != remaining ||
      !string.regionMatches(offset, pattern, patternOffset, remaining))
    {
      throw new FormatException("String '" + string +
        "' does not match format '" + pattern + "'");
    }

    return result;
  }

  private IniList getIniFile(String filename)
    throws FormatException, IOException
  {
    LOGGER.debug("getIniFile procession: {}", filename);
    RandomAccessInputStream stream = getFile(filename);
    String data = stream.readString((int) stream.length());
    if (!data.startsWith("[")) {
      data = data.substring(data.indexOf("["), data.length());
    }
    data = DataTools.stripString(data);
    BufferedReader reader = new BufferedReader(new StringReader(data));
    stream.close();
    IniList list = parser.parseINI(reader);

    // most of the values will be wrapped in double quotes
    for (IniTable table : list) {
      LOGGER.debug("");
      LOGGER.debug("[" + table.get(IniTable.HEADER_KEY) + "]");
      String[] keys = table.keySet().toArray(new String[table.size()]);
      for (String key : keys) {
        String value = sanitizeValue(table.get(key));
        LOGGER.debug(key + " = " + value);
        table.put(key, value);
      }
    }
    reader.close();
    return list;
  }

  // -- Helper classes --

  class ChannelData {
    public boolean active;
    public Double gain;
    public Double voltage;
    public String name;
    public String emissionFilter;
    public String excitationFilter;
    public Double emWave;
    public Double exWave;
    public String dyeName;
    public String barrierFilter;
  }

  class PlaneData {
    public Double deltaT;
    public Length positionX;
    public Length positionY;
    public Length positionZ;
  }

}

