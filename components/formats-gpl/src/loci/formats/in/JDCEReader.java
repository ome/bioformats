/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2024 Open Microscopy Environment:
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.internal.WellContainer;
import loci.formats.meta.MetadataStore;

import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.xml.model.enums.NamingConvention;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * JDCEReader is the file format reader for Molecular Devices JDCE plates.
 */
public class JDCEReader extends FormatReader {

  // -- Constants --

  // -- Fields --

  private List<JDCEWell> wells = new ArrayList<JDCEWell>();
  private String imageFileCSV = null;
  private transient MinimalTiffReader helper = new MinimalTiffReader();

  // -- Constructor --

  /** Constructs a new JDCE reader. */
  public JDCEReader() {
    super("Molecular Devices JDCE", new String[] {"jdce"});
    suffixSufficient = true;
    domains = new String[] {FormatTools.HCS_DOMAIN};
    hasCompanionFiles = true;
    datasetDescription = "One .jdce (JSON) file with at least one .tif/.tiff file";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
    Arrays.fill(buf, getFillColor());

    String file = getFile(no);
    if (file != null) {
      try {
        if (helper == null) {
          helper = new MinimalTiffReader();
        }
        helper.setId(file);
        return helper.openBytes(0, buf, x, y, w, h);
      }
      catch (FormatException | IOException e) {
        LOGGER.error("Invalid file " + file, e);
      }
    }
    return buf;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    ArrayList<String> imageFiles = new ArrayList<String>();
    imageFiles.add(currentId);
    imageFiles.add(imageFileCSV);
    if (!noPixels) {
      int index = 0;
      for (JDCEWell well : wells) {
        if (well.getFieldCount() + index > getSeries()) {
          String[] f = well.getFiles(getSeries() - index);
          for (String file : f) {
            if (file != null && !imageFiles.contains(file)) {
              imageFiles.add(file);
            }
          }
          break;
        }
        index += well.getFieldCount();
      }
    }

    return imageFiles.toArray(new String[imageFiles.size()]);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (helper != null) {
      helper.close(fileOnly);
    }
    if (!fileOnly) {
      imageFileCSV = null;
      if (wells != null) {
        wells.clear();
      }
    }
  }

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);

    try {
      String file = getFile(0);
      if (file != null) {
        if (helper == null) {
          helper = new MinimalTiffReader();
        }
        helper.setId(file);
        return helper.getOptimalTileWidth();
      }
    }
    catch (FormatException | IOException e) {
      LOGGER.debug("Could not get optimal tile width", e);
    }
    return super.getOptimalTileWidth();
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);

    try {
      String file = getFile(0);
      if (file != null) {
        if (helper == null) {
          helper = new MinimalTiffReader();
        }
        helper.setId(file);
        return helper.getOptimalTileHeight();
      }
    }
    catch (FormatException | IOException e) {
      LOGGER.debug("Could not get optimal tile height", e);
    }
    return super.getOptimalTileHeight();
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    Location parentDir = new Location(getCurrentFile()).getAbsoluteFile().getParentFile();

    String plateName = null;
    Length physicalSizeX = null;
    Length physicalSizeY = null;
    String[] channelNames = null;
    Length[] emissionWavelengths = null;
    Length[] excitationWavelengths = null;

    int plateRows = 0;
    int plateColumns = 0;
    String creationTimestamp = null;

    CoreMetadata ms0 = core.get(0);
    try {
      String jsonText = DataTools.readFile(id);
      if (!jsonText.startsWith("{")) {
        jsonText = jsonText.substring(jsonText.indexOf("{"));
      }
      JSONObject root = new JSONObject(jsonText);

      JSONObject imageStack = root.getJSONObject("ImageStack");
      if (imageStack == null) {
        throw new FormatException("Could not find image stack definition");
      }

      String imageFormat = imageStack.getString("ImageFormat");
      if (!"TIFF".equalsIgnoreCase(imageFormat)) {
        throw new FormatException("Unsupported image format " + imageFormat);
      }

      JSONObject creation = imageStack.getJSONObject("Creation");
      if (creation != null) {
        String date = creation.getString("Date");
        String time = creation.getString("Time");
        String timezone = creation.getString("TimeZoneOffset");

        if (timezone == null || timezone.isEmpty()) {
          LOGGER.warn("Timezone not defined; plate acquisition time may be wrong");
          creationTimestamp = DateTools.formatDate(date + " " + time, "yyyy-MM-dd HH:mm:ss");
        }
        else {
          creationTimestamp = DateTools.formatDate(date + " " + time + " " + timezone, "yyyy-MM-dd HH:mm:ss Z");
        }
      }
      else {
        LOGGER.debug("Could not find plate creation time");
      }

      JSONObject acquisition = imageStack.getJSONObject("AutoLeadAcquisitionProtocol");
      if (acquisition == null) {
        throw new FormatException("Could not find acquisition definition");
      }

      JSONObject objective = acquisition.getJSONObject("ObjectiveCalibration");
      String unit = objective.getString("Unit");
      double pixelWidth = objective.getDouble("PixelWidth");
      double pixelHeight = objective.getDouble("PixelHeight");
      physicalSizeX = FormatTools.getPhysicalSize(pixelWidth, unit);
      physicalSizeY = FormatTools.getPhysicalSize(pixelHeight, unit);

      JSONObject plate = acquisition.getJSONObject("Plate");
      plateName = plate.getString("Name");
      plateRows = plate.getInt("Rows");
      plateColumns = plate.getInt("Columns");

      JSONObject plateMap = acquisition.getJSONObject("PlateMap");
      if (plateMap == null) {
        throw new FormatException("Could not find plate map, cannot determine dimensions");
      }
      JSONObject timeSchedule = plateMap.getJSONObject("TimeSchedule");
      if (timeSchedule == null) {
        throw new FormatException("Could not find time schedule, cannot determine SizeT");
      }
      JSONArray timepoints = timeSchedule.getJSONArray("Times");
      int timepointCount = timeSchedule.getInt("NumberOfTimepoints");
      if (timepoints.length() != timepointCount) {
        LOGGER.warn("Mismatched timepoint count; using {}, also found {}",
          timepoints.length(), timepointCount);
      }
      ms0.sizeT = timepoints.length();

      JSONObject zDimension = plateMap.getJSONObject("ZDimensionParameters");
      if (zDimension == null) {
        throw new FormatException("Could not find Z dimension parameters, cannot determine SizeZ");
      }
      ms0.sizeZ = zDimension.getInt("NumberOfSlices");

      JSONArray wavelengths = acquisition.getJSONArray("Wavelengths");
      if (wavelengths == null) {
        throw new FormatException("Could not find wavelength array, cannot determine SizeC");
      }
      ms0.sizeC = wavelengths.length();
      channelNames = new String[ms0.sizeC];
      emissionWavelengths = new Length[ms0.sizeC];
      excitationWavelengths = new Length[ms0.sizeC];
      String[] imagingMode = new String[ms0.sizeC];

      boolean singleZ = true;
      for (int c=0; c<ms0.sizeC; c++) {
        JSONObject wavelength = wavelengths.getJSONObject(c);
        int channelIndex = wavelength.getInt("Index");

        JSONObject emission = wavelength.getJSONObject("EmissionFilter");
        JSONObject excitation = wavelength.getJSONObject("ExcitationFilter");

        channelNames[channelIndex] = emission.getString("Name");
        String emUnit = emission.getString("Unit");
        double emWave = emission.getDouble("Wavelength");
        emissionWavelengths[channelIndex] = FormatTools.getWavelength(emWave, emUnit);

        String exUnit = excitation.getString("Unit");
        double exWave = excitation.getDouble("Wavelength");
        excitationWavelengths[channelIndex] = FormatTools.getWavelength(exWave, exUnit);

        // if all of the imaging modes are "Max Intensity Projection",
        // the Z size will be reset to 1 since only one Z slice is present
        imagingMode[c] = wavelength.getString("ImagingMode");
        if (imagingMode[c] == null || !imagingMode[c].equals("Max Intensity Projection") || !imagingMode[c].equals(imagingMode[0])) {
          singleZ = false;
        }
      }
      if (singleZ) {
        LOGGER.debug("Ignoring Z stack size {} based on wavelength definition", ms0.sizeZ);
        ms0.sizeZ = 1;
      }

      JSONArray metadataFiles = imageStack.getJSONArray("ImageMetadataFiles");
      if (metadataFiles == null || metadataFiles.length() == 0) {
        throw new FormatException("Could not find image metadata CSV, cannot get list of TIFF files");
      }
      imageFileCSV = new Location(parentDir, metadataFiles.getString(0)).getAbsolutePath();
    }
    catch (JSONException e) {
      throw new FormatException("Could not parse .jdce file", e);
    }
    ms0.imageCount = getSizeZ() * getSizeC() * getSizeT();
    ms0.dimensionOrder = "XYCZT";

    if (imageFileCSV == null) {
      throw new FormatException("Image metadata CSV not found, cannot get list of TIFF files");
    }

    String[] csvLines = DataTools.readFile(imageFileCSV).split("\r\n");
    List<String> columns = Arrays.asList(csvLines[0].split(","));
    int wellRowIndex = columns.indexOf("Row");
    int wellColIndex = columns.indexOf("Column");
    int fieldIndex = columns.indexOf("Field");
    int wavelengthIndex = columns.indexOf("Wavelength");
    int timepointIndex = columns.indexOf("Timepoint");
    int zIndex = columns.indexOf("ZIndex");
    int subfolderIndex = columns.indexOf("ImageSubFolderPath");
    int fileNameIndex = columns.indexOf("ImageFileName");

    int timestampIndex = columns.indexOf("TimeStampSec");
    int exposureTimeIndex = columns.indexOf("ExposureTimeMs");
    int positionXIndex = columns.indexOf("PositionXUm");
    int positionYIndex = columns.indexOf("PositionYUm");
    int positionZIndex = columns.indexOf("PositionZUm");

    int imageWidthIndex = columns.indexOf("ImageSizeXPx");
    int imageHeightIndex = columns.indexOf("ImageSizeYPx");

    Double smallestTimestamp = null;

    JDCEWell currentWell = null;
    boolean firstFile = true;
    for (int i=1; i<csvLines.length; i++) {
      String[] line = csvLines[i].split(",");

      int[] position = new int[4];
      position[0] = Integer.parseInt(line[fieldIndex]);
      position[1] = Integer.parseInt(line[zIndex]);
      position[2] = Integer.parseInt(line[wavelengthIndex]);
      position[3] = Integer.parseInt(line[timepointIndex]);

      String subfolder = line[subfolderIndex];
      String filename = line[fileNameIndex];
      Location subfolderFile = new Location(parentDir, subfolder);
      String imagePath = new Location(subfolderFile, filename).getAbsolutePath();

      int wellRow = Integer.parseInt(line[wellRowIndex]) - 1;
      int wellCol = Integer.parseInt(line[wellColIndex]) - 1;
      if (currentWell == null || currentWell.getRowIndex() != wellRow ||
        currentWell.getColumnIndex() != wellCol)
      {
        currentWell = lookupWell(wellRow, wellCol, ms0.imageCount);
      }
      currentWell.addFile(imagePath, position[0], getIndex(position[1], position[2], position[3]));
      currentWell.setFieldCount((int) Math.max(currentWell.getFieldCount(), position[0] + 1));

      PlaneMetadata p = new PlaneMetadata();

      // plane times are stored in seconds since Jan. 1 1970
      // the smallest absolute timestamp will be subtracted later to get
      // a readable relative timestamp
      p.timestamp = DataTools.parseDouble(line[timestampIndex]);
      if (p.timestamp != null && (smallestTimestamp == null || p.timestamp < smallestTimestamp)) {
        smallestTimestamp = p.timestamp;
      }

      p.exposureTime = FormatTools.createTime(DataTools.parseDouble(line[exposureTimeIndex]), UNITS.MILLISECOND);
      p.positionX = FormatTools.getStagePosition(DataTools.parseDouble(line[positionXIndex]), UNITS.MICROMETER);
      p.positionY = FormatTools.getStagePosition(DataTools.parseDouble(line[positionYIndex]), UNITS.MICROMETER);
      p.positionZ = FormatTools.getStagePosition(DataTools.parseDouble(line[positionZIndex]), UNITS.MICROMETER);
      p.sizeX = Integer.parseInt(line[imageWidthIndex]);
      p.sizeY = Integer.parseInt(line[imageHeightIndex]);
      currentWell.addPlaneMetadata(p, position);

      if (firstFile) {
        try {
          if (helper == null) {
            helper = new MinimalTiffReader();
          }
          helper.setId(imagePath);
          CoreMetadata m = helper.getCoreMetadataList().get(0);
          if (p.sizeX == 0) {
            ms0.sizeX = m.sizeX;
            LOGGER.warn("Found image width 0 in CSV; using {} from TIFF", ms0.sizeX);
          }
          if (p.sizeY == 0) {
            ms0.sizeY = m.sizeY;
            LOGGER.warn("Found image height 0 in CSV; using {} from TIFF", ms0.sizeY);
          }
          ms0.pixelType = m.pixelType;
          ms0.littleEndian = m.littleEndian;
          ms0.sizeC *= m.sizeC;
          ms0.rgb = m.rgb;

          firstFile = false;
        }
        catch (FormatException | IOException e) {
          LOGGER.debug("Could not read " + imagePath, e);
        }
      }
    }
    for (JDCEWell well : wells) {
      for (int f=0; f<well.getFieldCount(); f++) {
        CoreMetadata m = new CoreMetadata(ms0);
        // an XY size is stored for every plane
        // different wells/fields in particular may have different sizes
        // for each field, the largest plane dimension is selected
        for (int plane=0; plane<m.imageCount; plane++) {
          PlaneMetadata p = well.getPlaneMetadata(f, getZCTCoords(plane));
          if (p != null) {
            m.sizeX = (int) Math.max(m.sizeX, p.sizeX);
            m.sizeY = (int) Math.max(m.sizeY, p.sizeY);
          }
        }
        core.add(m);
      }
    }
    core.remove(0);

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    store.setPlateID(MetadataTools.createLSID("Plate", 0), 0);
    store.setPlateName(plateName, 0);
    store.setPlateRows(new PositiveInteger(plateRows), 0);
    store.setPlateColumns(new PositiveInteger(plateColumns), 0);
    store.setPlateRowNamingConvention(NamingConvention.LETTER, 0);
    store.setPlateColumnNamingConvention(NamingConvention.NUMBER, 0);

    store.setPlateAcquisitionID(MetadataTools.createLSID("PlateAcquisition", 0, 0), 0, 0);
    if (creationTimestamp != null) {
      store.setPlateAcquisitionStartTime(new Timestamp(creationTimestamp), 0, 0);
    }

    wells.sort(null);

    int imageIndex = 0;
    for (int w=0; w<wells.size(); w++) {
      JDCEWell well = wells.get(w);
      well.fillMetadataStore(store, 0, 0, w, 0, imageIndex);

      int fieldCount = well.getFieldCount();
      String wellName = FormatTools.getWellName(well.getRowIndex(), well.getColumnIndex());
      for (int f=0; f<fieldCount; f++, imageIndex++) {
        store.setImageName(wellName + ", Field #" + (f + 1), imageIndex);
        store.setPixelsPhysicalSizeX(physicalSizeX, imageIndex);
        store.setPixelsPhysicalSizeY(physicalSizeY, imageIndex);

        for (int c=0; c<channelNames.length; c++) {
          store.setChannelName(channelNames[c], imageIndex, c);
          store.setChannelEmissionWavelength(emissionWavelengths[c], imageIndex, c);
          store.setChannelExcitationWavelength(excitationWavelengths[c], imageIndex, c);
        }

        boolean firstPlane = true;
        for (int plane=0; plane<getImageCount(); plane++) {
          PlaneMetadata p = well.getPlaneMetadata(f, getZCTCoords(plane));
          if (p != null) {
            store.setPlanePositionX(p.positionX, imageIndex, plane);
            store.setPlanePositionY(p.positionY, imageIndex, plane);
            store.setPlanePositionZ(p.positionZ, imageIndex, plane);

            if (firstPlane) {
              firstPlane = false;
              store.setWellSamplePositionX(p.positionX, 0, w, f);
              store.setWellSamplePositionY(p.positionY, 0, w, f);
            }

            if (p.timestamp != null && smallestTimestamp != null) {
              Time stamp = FormatTools.createTime(p.timestamp - smallestTimestamp, UNITS.SECOND);
              store.setPlaneDeltaT(stamp, imageIndex, plane);
            }

            store.setPlaneExposureTime(p.exposureTime, imageIndex, plane);
          }
        }
      }
    }
  }

  private JDCEWell lookupWell(int row, int col, int planeCount) {
    for (JDCEWell well : wells) {
      if (well.getRowIndex() == row && well.getColumnIndex() == col) {
        return well;
      }
    }
    JDCEWell well = new JDCEWell(row, col, planeCount);
    wells.add(well);
    return well;
  }

  private String getFile(int no) {
    int index = 0;
    for (JDCEWell well : wells) {
      if (well.getFieldCount() + index > getSeries()) {
        return well.getFile(getSeries() - index, no);
      }
      index += well.getFieldCount();
    }
    return null;
  }

  class JDCEWell extends WellContainer {
    HashMap<String, Integer> posMap = new HashMap<String, Integer>();
    HashMap<String, PlaneMetadata> metadataMap = new HashMap<String, PlaneMetadata>();
    int planeCount = 0;

    public JDCEWell(int row, int col, int planeCount) {
      super(0, row, col, 1);
      this.planeCount = planeCount;
    }

    /**
     * Add file for the given field and plane indexes.
     */
    public void addFile(String file, int fieldIndex, int planeIndex) {
      super.addFile(file);
      posMap.put(fieldIndex + "-" + planeIndex, getAllFiles().size() - 1);
    }

    private int[] getIndex(String key) {
      String[] k = key.split("-");
      int[] v = new int[k.length];
      for (int i=0; i<v.length; i++) {
        v[i] = Integer.parseInt(k[i]);
      }
      return v;
    }

    public String[] getFiles(int fieldIndex) {
      List<String> allFiles = getAllFiles();
      String[] fieldFiles = new String[planeCount];
      for (String key : posMap.keySet()) {
        int[] keyIndex = getIndex(key);
        if (keyIndex[0] == fieldIndex) {
          fieldFiles[keyIndex[1]] = allFiles.get(posMap.get(key));
        }
      }
      return fieldFiles;
    }

    public void addPlaneMetadata(PlaneMetadata p, int[] position) {
      StringJoiner joiner = new StringJoiner("-");
      for (int pos : position) {
        joiner.add(String.valueOf(pos));
      }
      metadataMap.put(joiner.toString(), p);
    }

    public PlaneMetadata getPlaneMetadata(int field, int[] position) {
      StringJoiner joiner = new StringJoiner("-");
      joiner.add(String.valueOf(field));
      for (int pos : position) {
        joiner.add(String.valueOf(pos));
      }
      return metadataMap.get(joiner.toString());
    }
  }

  class PlaneMetadata {
    public Double timestamp;
    public Time exposureTime;
    public Length positionX;
    public Length positionY;
    public Length positionZ;

    public int sizeX;
    public int sizeY;
  }
}
