/*
 * #%L
 * OME Bio-Formats manual and automated test suite.
 * %%
 * Copyright (C) 2006 - 2017 Open Microscopy Environment:
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

package loci.tests.testng;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.IOException;

import loci.common.Constants;
import loci.common.DataTools;
import loci.common.IniList;
import loci.common.IniParser;
import loci.common.IniTable;
import loci.common.IniWriter;
import loci.common.Location;
import loci.formats.FileStitcher;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.ReaderWrapper;
import loci.formats.meta.IMetadata;

import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.UnitsLength;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.Timestamp;

import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.UNITS;

/**
 */
public class Configuration {

  // -- Constants --

  public static final int TILE_SIZE = 512;

  private static final String ACCESS_TIME = "access_ms";
  private static final String MEMORY = "mem_mb";
  private static final String TEST = "test";
  private static final String HAS_VALID_XML = "hasValidXML";
  private static final String READER = "reader";
  private static final String SERIES = " series_";
  private static final String RESOLUTION = "resolution_";

  private static final String SIZE_X = "SizeX";
  private static final String SIZE_Y = "SizeY";
  private static final String SIZE_Z = "SizeZ";
  private static final String SIZE_C = "SizeC";
  private static final String SIZE_T = "SizeT";
  private static final String DIMENSION_ORDER = "DimensionOrder";
  private static final String IS_INDEXED = "Indexed";
  private static final String IS_INTERLEAVED = "Interleaved";
  private static final String IS_FALSE_COLOR = "FalseColor";
  private static final String IS_RGB = "RGB";
  private static final String THUMB_SIZE_X = "ThumbSizeX";
  private static final String THUMB_SIZE_Y = "ThumbSizeY";
  private static final String PIXEL_TYPE = "PixelType";
  private static final String IS_LITTLE_ENDIAN = "LittleEndian";
  private static final String MD5 = "MD5";
  private static final String ALTERNATE_MD5 = "Alternate_MD5";
  private static final String TILE_MD5 = "Tile_MD5";
  private static final String TILE_ALTERNATE_MD5 = "Tile_Alternate_MD5";
  private static final String PHYSICAL_SIZE_X = "PhysicalSizeX";
  private static final String PHYSICAL_SIZE_X_UNIT = "PhysicalSizeXUnit";
  private static final String PHYSICAL_SIZE_Y = "PhysicalSizeY";
  private static final String PHYSICAL_SIZE_Y_UNIT = "PhysicalSizeYUnit";
  private static final String PHYSICAL_SIZE_Z = "PhysicalSizeZ";
  private static final String PHYSICAL_SIZE_Z_UNIT = "PhysicalSizeZUnit";
  private static final String TIME_INCREMENT = "TimeIncrement";
  private static final String TIME_INCREMENT_UNIT = "TimeIncrementUnit";
  private static final String LIGHT_SOURCE = "LightSource_";
  private static final String CHANNEL_NAME = "ChannelName_";
  private static final String EXPOSURE_TIME = "ExposureTime_";
  private static final String EXPOSURE_TIME_UNIT = "ExposureTimeUnit_";
  private static final String EMISSION_WAVELENGTH = "EmissionWavelength_";
  private static final String EMISSION_WAVELENGTH_UNIT = "EmissionWavelengthUnit_";
  private static final String EXCITATION_WAVELENGTH = "ExcitationWavelength_";
  private static final String EXCITATION_WAVELENGTH_UNIT = "ExcitationWavelengthUnit_";
  private static final String DETECTOR = "Detector_";
  private static final String NAME = "Name";
  private static final String DESCRIPTION = "Description";
  private static final String SERIES_COUNT = "series_count";
  private static final String RESOLUTION_COUNT = "resolution_count";
  private static final String CHANNEL_COUNT = "channel_count";
  private static final String DATE = "Date";
  private static final String DELTA_T = "DeltaT_";
  private static final String X_POSITION = "PositionX_";
  private static final String X_POSITION_UNIT = "PositionXUnit_";
  private static final String Y_POSITION = "PositionY_";
  private static final String Y_POSITION_UNIT = "PositionYUnit_";
  private static final String Z_POSITION = "PositionZ_";
  private static final String Z_POSITION_UNIT = "PositionZUnit_";

  private static final String PLATE = "Plate";
  private static final String PLATE_ACQUISITION = "PlateAcquisition";
  private static final String WELL_ROW = "WellRow";
  private static final String WELL_COLUMN = "WellColumn";
  private static final String WELL_SAMPLE = "WellSample";
  private static final String WELL_SAMPLE_POSITION_X = "WellSamplePositionX";
  private static final String WELL_SAMPLE_POSITION_X_UNIT = "WellSamplePositionXUnit";
  private static final String WELL_SAMPLE_POSITION_Y = "WellSamplePositionY";
  private static final String WELL_SAMPLE_POSITION_Y_UNIT = "WellSamplePositionYUnit";

  // -- Fields --

  private String dataFile;
  private String configFile;
  private IniList ini;

  private IniTable currentTable;
  private IniTable globalTable;

  // flattened series count for this dataset
  private Integer flattenedCount = null;

  // -- Constructors --

  public Configuration(String dataFile, String configFile) throws IOException {
    this.dataFile = dataFile;
    this.configFile = configFile;

    BufferedReader reader = new BufferedReader(new InputStreamReader(
      new FileInputStream(this.configFile), Constants.ENCODING));
    IniParser parser = new IniParser();
    parser.setCommentDelimiter(null);
    ini = parser.parseINI(reader);
    pruneINI();
  }

  public Configuration(IFormatReader reader, String configFile) {
    this.dataFile = reader.getCurrentFile();
    this.configFile = configFile;
    populateINI(reader);
  }

  // -- Configuration API methods --

  // -- Global metadata --

  public String getFile() {
    return dataFile;
  }

  public long getAccessTimeMillis() {
    String millis = globalTable.get(ACCESS_TIME);
    if (millis == null) return -1;
    return Long.parseLong(millis);
  }

  public int getMemory() {
    String memory = globalTable.get(MEMORY);
    if (memory == null) return -1;
    return Integer.parseInt(memory);
  }

  public boolean doTest() {
    return new Boolean(globalTable.get(TEST)).booleanValue();
  }

  public boolean hasValidXML() {
    if (globalTable.get(HAS_VALID_XML) == null) return true;
    return new Boolean(globalTable.get(HAS_VALID_XML)).booleanValue();
  }

  public String getReader() {
    return globalTable.get(READER);
  }

  public int getSeriesCount() {
    return getSeriesCount(true);
  }

  public int getSeriesCount(boolean flattened) {
    int tableCount = Integer.parseInt(globalTable.get(SERIES_COUNT));
    if (flattened) {
      if (flattenedCount == null) {
        int count = 0;
        for (int i=0; i<tableCount; i++) {
          setSeries(i, false);
          count += getResolutionCount();
        }
        flattenedCount = count;
      }
      return flattenedCount;
    }
    return tableCount;
  }

  // -- Per-series metadata --

  public int getSizeX() {
    return Integer.parseInt(currentTable.get(SIZE_X));
  }

  public int getSizeY() {
    return Integer.parseInt(currentTable.get(SIZE_Y));
  }

  public int getSizeZ() {
    return Integer.parseInt(currentTable.get(SIZE_Z));
  }

  public int getSizeC() {
    return Integer.parseInt(currentTable.get(SIZE_C));
  }

  public int getSizeT() {
    return Integer.parseInt(currentTable.get(SIZE_T));
  }

  public String getDimensionOrder() {
    return currentTable.get(DIMENSION_ORDER);
  }

  public boolean isInterleaved() {
    return new Boolean(currentTable.get(IS_INTERLEAVED)).booleanValue();
  }

  public boolean isIndexed() {
    return new Boolean(currentTable.get(IS_INDEXED)).booleanValue();
  }

  public boolean isFalseColor() {
    return new Boolean(currentTable.get(IS_FALSE_COLOR)).booleanValue();
  }

  public boolean isRGB() {
    return new Boolean(currentTable.get(IS_RGB)).booleanValue();
  }

  public int getThumbSizeX() {
    return Integer.parseInt(currentTable.get(THUMB_SIZE_X));
  }

  public int getThumbSizeY() {
    return Integer.parseInt(currentTable.get(THUMB_SIZE_Y));
  }

  public String getPixelType() {
    return currentTable.get(PIXEL_TYPE);
  }

  public boolean isLittleEndian() {
    return new Boolean(currentTable.get(IS_LITTLE_ENDIAN)).booleanValue();
  }

  public String getMD5() {
    return currentTable.get(MD5);
  }

  public String getAlternateMD5() {
    return currentTable.get(ALTERNATE_MD5);
  }

  public String getTileMD5() {
    return currentTable.get(TILE_MD5);
  }

  public String getTileAlternateMD5() {
    return currentTable.get(TILE_ALTERNATE_MD5);
  }

  public Length getPhysicalSizeX() {
    return getPhysicalSize(PHYSICAL_SIZE_X, PHYSICAL_SIZE_X_UNIT);
  }

  public Length getPhysicalSizeY() {
    return getPhysicalSize(PHYSICAL_SIZE_Y, PHYSICAL_SIZE_Y_UNIT);
  }

  public Length getPhysicalSizeZ() {
    return getPhysicalSize(PHYSICAL_SIZE_Z, PHYSICAL_SIZE_Z_UNIT);
  }

  public Time getTimeIncrement() {
    String timeIncrement = currentTable.get(TIME_INCREMENT);
    String timeIncrementUnits = currentTable.get(TIME_INCREMENT_UNIT);
    try {
      return timeIncrement == null ? null : FormatTools.getTime(new Double(timeIncrement), timeIncrementUnits);
    }
    catch (NumberFormatException e) { 
      return null; 
    }
  }

  public int getChannelCount() {
    return Integer.parseInt(currentTable.get(CHANNEL_COUNT));
  }

  public String getLightSource(int channel) {
    return currentTable.get(LIGHT_SOURCE + channel);
  }

  public String getChannelName(int channel) {
    return currentTable.get(CHANNEL_NAME + channel);
  }

  public boolean hasExposureTime(int channel) {
    return currentTable.containsKey(EXPOSURE_TIME + channel);
  }

  public Time getExposureTime(int channel) {
    String exposure = currentTable.get(EXPOSURE_TIME + channel);
    String exposureUnits = currentTable.get(EXPOSURE_TIME_UNIT + channel);
    try {
      return exposure == null ? null : FormatTools.getTime(new Double(exposure), exposureUnits);
    }
    catch (NumberFormatException e) { 
      return null; 
    }
  }

  public Double getDeltaT(int plane) {
    String deltaT = currentTable.get(DELTA_T + plane);
    return deltaT == null ? null : new Double(deltaT);
  }

  public Double getPositionX(int plane) {
    String pos = currentTable.get(X_POSITION + plane);
    return pos == null ? null : new Double(pos);
  }
  
  public String getPositionXUnit(int plane) {
    return currentTable.get(X_POSITION_UNIT + plane);
  }

  public Double getPositionY(int plane) {
    String pos = currentTable.get(Y_POSITION + plane);
    return pos == null ? null : new Double(pos);
  }
  
  public String getPositionYUnit(int plane) {
    return currentTable.get(Y_POSITION_UNIT + plane);
  }

  public Double getPositionZ(int plane) {
    String pos = currentTable.get(Z_POSITION + plane);
    return pos == null ? null : new Double(pos);
  }

  public String getPositionZUnit(int plane) {
    return currentTable.get(Z_POSITION_UNIT + plane);
  }
  
  public Length getEmissionWavelength(int channel) {
    String wavelength = currentTable.get(EMISSION_WAVELENGTH + channel);
    String emissionUnits = currentTable.get(EMISSION_WAVELENGTH_UNIT + channel);
    try {
      return wavelength == null ? null : FormatTools.getWavelength(new Double(wavelength), emissionUnits);
    }
    catch (NumberFormatException e) { 
      return null;
    } 
  }

  public Length getExcitationWavelength(int channel) {
    String wavelength = currentTable.get(EXCITATION_WAVELENGTH + channel);
    String excitationUnits = currentTable.get(EXCITATION_WAVELENGTH_UNIT + channel);
    try {
      return wavelength == null ? null : FormatTools.getWavelength(new Double(wavelength), excitationUnits);
    }
    catch (NumberFormatException e) { 
      return null;
    } 
  }

  public String getDetector(int channel) {
    return currentTable.get(DETECTOR + channel);
  }

  public String getImageName() {
    return currentTable.get(NAME);
  }

  public boolean hasImageDescription() {
    return currentTable.containsKey(DESCRIPTION);
  }

  public String getImageDescription() {
    return currentTable.get(DESCRIPTION);
  }

  public String getDate() {
    return currentTable.get(DATE);
  }

  public int getResolutionCount() {
    return getInt(RESOLUTION_COUNT, 1);
  }

  public int getPlate() {
    return getInt(PLATE, -1);
  }

  public int getPlateAcquisition() {
    return getInt(PLATE_ACQUISITION, -1);
  }

  public int getWellRow() {
    return getInt(WELL_ROW, -1);
  }

  public int getWellColumn() {
    return getInt(WELL_COLUMN, -1);
  }

  public int getWellSample() {
    return getInt(WELL_SAMPLE, -1);
  }

  public Length getWellSamplePositionX() {
    return getPhysicalSize(WELL_SAMPLE_POSITION_X, WELL_SAMPLE_POSITION_X_UNIT);
  }

  public Length getWellSamplePositionY() {
    return getPhysicalSize(WELL_SAMPLE_POSITION_Y, WELL_SAMPLE_POSITION_Y_UNIT);
  }

  public void setSeries(int series) throws IndexOutOfBoundsException {
    setSeries(series, true);
  }

  public void setSeries(int series, boolean flattened) throws IndexOutOfBoundsException {
    int s = series, r = 0;
    int index = series;

    if (flattened && getSeriesCount(true) != getSeriesCount(false)) {
      index = 0;
      s = 0;
      while (index < series) {
        setSeries(s, false);
        int resolutionCount = getResolutionCount();
        if (resolutionCount + index <= series) {
          index += resolutionCount;
          s++;
        }
        else {
          r = series - index;
          index += r;
        }
      }
    }

    try {
      setResolution(s, r);
    }
    catch (IndexOutOfBoundsException e) {
      Location file = new Location(dataFile);
      String tableName = file.getName() + SERIES + index;
      currentTable = ini.getTable(tableName);
      if (currentTable == null) {
        throw new IndexOutOfBoundsException("Invalid table name: " + tableName);
      }
    }
  }

  public void setResolution(int series, int resolution) throws IndexOutOfBoundsException {
    Location file = new Location(dataFile);
    String tableName = file.getName() + SERIES + series + " " + RESOLUTION + resolution;
    currentTable = ini.getTable(tableName);
    if (currentTable == null && resolution == 0) {
      // don't require the resolution key for single-resolution series
      currentTable = ini.getTable(file.getName() + SERIES + series);
    }
    if (currentTable == null) {
      throw new IndexOutOfBoundsException("Invalid table name: " + tableName);
    }
  }

  public void saveToFile() throws IOException {
    IniWriter writer = new IniWriter();
    writer.saveINI(ini, configFile, true, true);
  }

  public IniList getINI() {
    return ini;
  }

  // -- Object API methods --

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Configuration)) return false;

    Configuration thatConfig = (Configuration) o;
    return this.getINI().equals(thatConfig.getINI());
  }

  @Override
  public int hashCode() {
    return this.getINI().hashCode();
  }

  // -- Helper methods --

  private void populateINI(IFormatReader reader) {
    IMetadata retrieve = (IMetadata) reader.getMetadataStore();

    ini = new IniList();

    IniTable globalTable = new IniTable();
    putTableName(globalTable, reader, " global");

    int seriesCount = reader.getSeriesCount();
    IFormatReader unflattenedReader = reader;
    if (seriesCount > 1) {
      unflattenedReader = new FileStitcher();
      unflattenedReader.setFlattenedResolutions(false);
      try {
        unflattenedReader.setId(reader.getCurrentFile());
      }
      catch (FormatException | IOException e) { }
      seriesCount = unflattenedReader.getSeriesCount();
    }

    globalTable.put(SERIES_COUNT, String.valueOf(seriesCount));

    IFormatReader r = reader;
    if (r instanceof ImageReader) {
      r = ((ImageReader) r).getReader();
    }
    else if (r instanceof ReaderWrapper) {
      try {
        r = ((ReaderWrapper) r).unwrap();
      }
      catch (FormatException e) { }
      catch (IOException e) { }
    }

    globalTable.put(READER, TestTools.shortClassName(r));
    globalTable.put(TEST, "true");
    globalTable.put(MEMORY, String.valueOf(TestTools.getUsedMemory()));

    long planeSize = (long) FormatTools.getPlaneSize(reader) * 3;
    boolean canOpenImages =
      planeSize > 0 && TestTools.canFitInMemory(planeSize);

    long t0 = System.currentTimeMillis();
    if (canOpenImages) {
      try {
        reader.openBytes(0);
      }
      catch (FormatException e) { }
      catch (IOException e) { }
    }
    long t1 = System.currentTimeMillis();

    globalTable.put(ACCESS_TIME, String.valueOf(t1 - t0));

    ini.add(globalTable);

    for (int series=0; series<seriesCount; series++) {
      unflattenedReader.setSeries(series);
      int resolutionCount = unflattenedReader.getResolutionCount();
      for (int resolution = 0; resolution < resolutionCount; resolution++) {
        unflattenedReader.setResolution(resolution);
        int index = unflattenedReader.getCoreIndex();
        reader.setCoreIndex(index);

        IniTable seriesTable = new IniTable();
        putTableName(seriesTable, unflattenedReader, SERIES + series + " " + RESOLUTION + resolution);

        if (resolution == 0) {
          seriesTable.put(RESOLUTION_COUNT, String.valueOf(resolutionCount));
        }
        seriesTable.put(SIZE_X, String.valueOf(reader.getSizeX()));
        seriesTable.put(SIZE_Y, String.valueOf(reader.getSizeY()));
        seriesTable.put(SIZE_Z, String.valueOf(reader.getSizeZ()));
        seriesTable.put(SIZE_C, String.valueOf(reader.getSizeC()));
        seriesTable.put(SIZE_T, String.valueOf(reader.getSizeT()));
        seriesTable.put(DIMENSION_ORDER, reader.getDimensionOrder());
        seriesTable.put(IS_INTERLEAVED, String.valueOf(reader.isInterleaved()));
        seriesTable.put(IS_INDEXED, String.valueOf(reader.isIndexed()));
        seriesTable.put(IS_FALSE_COLOR, String.valueOf(reader.isFalseColor()));
        seriesTable.put(IS_RGB, String.valueOf(reader.isRGB()));
        seriesTable.put(THUMB_SIZE_X, String.valueOf(reader.getThumbSizeX()));
        seriesTable.put(THUMB_SIZE_Y, String.valueOf(reader.getThumbSizeY()));
        seriesTable.put(PIXEL_TYPE,
          FormatTools.getPixelTypeString(reader.getPixelType()));
        seriesTable.put(IS_LITTLE_ENDIAN,
          String.valueOf(reader.isLittleEndian()));

        seriesTable.put(CHANNEL_COUNT,
          String.valueOf(retrieve.getChannelCount(index)));

        try {
          planeSize = DataTools.safeMultiply32(reader.getSizeX(),
            reader.getSizeY(), reader.getEffectiveSizeC(),
            reader.getRGBChannelCount(),
            FormatTools.getBytesPerPixel(reader.getPixelType()));
          canOpenImages = planeSize > 0 && TestTools.canFitInMemory(planeSize);
        } catch (IllegalArgumentException e) {
          canOpenImages = false;
        }

        if (canOpenImages) {
          try {
            byte[] plane = reader.openBytes(0);
            seriesTable.put(MD5, TestTools.md5(plane));
          } catch (FormatException e) {
            // TODO
          } catch (IOException e) {
            // TODO
          }
        }

        try {
          int w = (int) Math.min(TILE_SIZE, reader.getSizeX());
          int h = (int) Math.min(TILE_SIZE, reader.getSizeY());

          byte[] tile = reader.openBytes(0, 0, 0, w, h);
          seriesTable.put(TILE_MD5, TestTools.md5(tile));
        } catch (FormatException e) {
          // TODO
        } catch (IOException e) {
          // TODO
        }

        seriesTable.put(NAME, retrieve.getImageName(index));
        seriesTable.put(DESCRIPTION, retrieve.getImageDescription(index));

        Length physicalX = retrieve.getPixelsPhysicalSizeX(index);
        putLength(
          seriesTable, physicalX, PHYSICAL_SIZE_X, PHYSICAL_SIZE_X_UNIT);
        Length physicalY = retrieve.getPixelsPhysicalSizeY(index);
        putLength(
          seriesTable, physicalY, PHYSICAL_SIZE_Y, PHYSICAL_SIZE_Y_UNIT);
        Length physicalZ = retrieve.getPixelsPhysicalSizeZ(index);
        putLength(
          seriesTable, physicalZ, PHYSICAL_SIZE_Z, PHYSICAL_SIZE_Z_UNIT);
        Time timeIncrement = retrieve.getPixelsTimeIncrement(index);
        if (timeIncrement != null) {
          seriesTable.put(TIME_INCREMENT, timeIncrement.value().toString());
          seriesTable.put(TIME_INCREMENT_UNIT, timeIncrement.unit().getSymbol());
        }

        Timestamp acquisition = retrieve.getImageAcquisitionDate(index);
        if (acquisition != null) {
          String date = acquisition.getValue();
          if (date != null) {
            seriesTable.put(DATE, date);
          }
        }

        for (int c = 0; c < retrieve.getChannelCount(index); c++) {
          seriesTable.put(CHANNEL_NAME + c, retrieve.getChannelName(index, c));
          try {
            seriesTable.put(LIGHT_SOURCE + c,
              retrieve.getChannelLightSourceSettingsID(index, c));
          } catch (NullPointerException e) {
          }

          try {
            int plane = reader.getIndex(0, c, 0);
            if (plane < retrieve.getPlaneCount(index)) {
              seriesTable.put(EXPOSURE_TIME + c,
                retrieve.getPlaneExposureTime(index, plane).value().toString());
              seriesTable.put(EXPOSURE_TIME_UNIT + c,
                retrieve.getPlaneExposureTime(index, plane).unit().getSymbol());
            }
          } catch (NullPointerException e) {
          }

          Length emWavelength = retrieve.getChannelEmissionWavelength(index, c);
          putLength(seriesTable, emWavelength,
            EMISSION_WAVELENGTH + c, EMISSION_WAVELENGTH_UNIT + c);
          Length exWavelength =
            retrieve.getChannelExcitationWavelength(index, c);
          putLength(seriesTable, exWavelength,
            EXCITATION_WAVELENGTH + c, EXCITATION_WAVELENGTH_UNIT + c);
          try {
            seriesTable.put(DETECTOR + c,
              retrieve.getDetectorSettingsID(index, c));
          } catch (NullPointerException e) {
          }
        }

        for (int p = 0; p < reader.getImageCount(); p++) {
          try {
            Time deltaT = retrieve.getPlaneDeltaT(index, p);
            if (deltaT != null) {
              seriesTable.put(DELTA_T + p, deltaT.value(UNITS.SECOND).toString());
            }
            Length xPos = retrieve.getPlanePositionX(index, p);
            putLength(seriesTable, xPos, X_POSITION + p, X_POSITION_UNIT + p);
            Length yPos = retrieve.getPlanePositionY(index, p);
            putLength(seriesTable, yPos, Y_POSITION + p, Y_POSITION_UNIT + p);
            Length zPos = retrieve.getPlanePositionZ(index, p);
            putLength(seriesTable, zPos, Z_POSITION + p, Z_POSITION_UNIT + p);
          } catch (IndexOutOfBoundsException e) {
            // only happens if no Plane elements were populated
          }
        }

        // look for HCS metadata

        String imageID = retrieve.getImageID(index);
        boolean foundWellSample = false;
        for (int p=0; !foundWellSample && p<retrieve.getPlateCount(); p++) {
          for (int w=0; !foundWellSample && w<retrieve.getWellCount(p); w++) {
            for (int ws=0; ws<retrieve.getWellSampleCount(p, w); ws++) {
              String imageRef = retrieve.getWellSampleImageRef(p, w, ws);
              if (imageID.equals(imageRef)) {
                seriesTable.put(PLATE, String.valueOf(p));
                seriesTable.put(WELL_ROW, retrieve.getWellRow(p, w).toString());
                seriesTable.put(WELL_COLUMN, retrieve.getWellColumn(p, w).toString());
                seriesTable.put(WELL_SAMPLE, String.valueOf(ws));

                Length positionX = retrieve.getWellSamplePositionX(p, w, ws);
                Length positionY = retrieve.getWellSamplePositionY(p, w, ws);
                putLength(seriesTable, positionX, WELL_SAMPLE_POSITION_X, WELL_SAMPLE_POSITION_X_UNIT);
                putLength(seriesTable, positionY, WELL_SAMPLE_POSITION_Y, WELL_SAMPLE_POSITION_Y_UNIT);

                String wellSampleID = retrieve.getWellSampleID(p, w, ws);
                boolean foundPA = false;
                for (int pa=0; !foundPA && pa<retrieve.getPlateAcquisitionCount(p); pa++) {
                  for (int wsRef=0; wsRef<retrieve.getWellSampleRefCount(p, pa); wsRef++) {
                    String wellSampleRef = retrieve.getPlateAcquisitionWellSampleRef(p, pa, wsRef);
                    if (wellSampleID.equals(wellSampleRef)) {
                      seriesTable.put(PLATE_ACQUISITION, String.valueOf(pa));
                      foundPA = true;
                      break;
                    }
                  }
                }

                break;
              }
            }
          }
        }

        ini.add(seriesTable);
      }
    }

  }

  private void putTableName(IniTable table, IFormatReader reader, String suffix)
  {
    Location file = new Location(reader.getCurrentFile());
    table.put(IniTable.HEADER_KEY, file.getName() + suffix);
  }

  private void putLength(IniTable table, Length value, String valueKey, String unitKey) {
    if (value != null) {
      table.put(valueKey, String.valueOf(value.value().doubleValue()));
      table.put(unitKey, value.unit().getSymbol());
    }
  }

  private void pruneINI() {
    IniList newIni = new IniList();
    for (IniTable table : ini) {
      String tableName = table.get(IniTable.HEADER_KEY);
      Location file = new Location(dataFile);
      if (tableName.startsWith(file.getName() + " ")) {
        newIni.add(table);

        if (tableName.endsWith("global")) {
          globalTable = table;
        }
      }
    }
    ini = newIni;
  }

  private int getInt(String key, int defaultValue) {
    int rtn = defaultValue;
    if (currentTable.get(key) != null) {
      rtn = Integer.parseInt(currentTable.get(key));
    }
    return rtn;
  }

  private Length getPhysicalSize(String valueKey, String unitKey) {
    String physicalSize = currentTable.get(valueKey);
    String units = currentTable.get(unitKey);
    try {
      UnitsLength unit = units == null ? UnitsLength.MICROMETER : UnitsLength.fromString(units);
      return physicalSize == null ? null : UnitsLength.create(new Double(physicalSize), unit);
    }
    catch (NumberFormatException e) { }
    catch (EnumerationException e) { }
    return null;
  }
}
