/*
 * #%L
 * OME Bio-Formats manual and automated test suite.
 * %%
 * Copyright (C) 2006 - 2015 Open Microscopy Environment:
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
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.ReaderWrapper;
import loci.formats.meta.IMetadata;

import ome.xml.model.primitives.PositiveInteger;
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
  private static final String READER = "reader";
  private static final String SERIES = " series_";

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
  private static final String PHYSICAL_SIZE_Y = "PhysicalSizeY";
  private static final String PHYSICAL_SIZE_Z = "PhysicalSizeZ";
  private static final String TIME_INCREMENT = "TimeIncrement";
  private static final String LIGHT_SOURCE = "LightSource_";
  private static final String CHANNEL_NAME = "ChannelName_";
  private static final String EXPOSURE_TIME = "ExposureTime_";
  private static final String EMISSION_WAVELENGTH = "EmissionWavelength_";
  private static final String EXCITATION_WAVELENGTH = "ExcitationWavelength_";
  private static final String DETECTOR = "Detector_";
  private static final String NAME = "Name";
  private static final String DESCRIPTION = "Description";
  private static final String SERIES_COUNT = "series_count";
  private static final String CHANNEL_COUNT = "channel_count";
  private static final String DATE = "Date";
  private static final String DELTA_T = "DeltaT_";
  private static final String X_POSITION = "PositionX_";
  private static final String Y_POSITION = "PositionY_";
  private static final String Z_POSITION = "PositionZ_";

  // -- Fields --

  private String dataFile;
  private String configFile;
  private IniList ini;

  private IniTable currentTable;
  private IniTable globalTable;

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

  public String getReader() {
    return globalTable.get(READER);
  }

  public int getSeriesCount() {
    return Integer.parseInt(globalTable.get(SERIES_COUNT));
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

  public Double getPhysicalSizeX() {
    String physicalSize = currentTable.get(PHYSICAL_SIZE_X);
    if (physicalSize == null) return null;
    try {
      return new Double(physicalSize);
    }
    catch (NumberFormatException e) { }
    return null;
  }

  public Double getPhysicalSizeY() {
    String physicalSize = currentTable.get(PHYSICAL_SIZE_Y);
    if (physicalSize == null) return null;
    try {
      return new Double(physicalSize);
    }
    catch (NumberFormatException e) { }
    return null;
  }

  public Double getPhysicalSizeZ() {
    String physicalSize = currentTable.get(PHYSICAL_SIZE_Z);
    if (physicalSize == null) return null;
    try {
      return new Double(physicalSize);
    }
    catch (NumberFormatException e) { }
    return null;
  }

  public Time getTimeIncrement() {
    String physicalSize = currentTable.get(TIME_INCREMENT);
    return physicalSize == null ? null : new Time(new Double(physicalSize), UNITS.S);
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
    return exposure == null ? null : new Time(new Double(exposure), UNITS.S);
  }

  public Double getDeltaT(int plane) {
    String deltaT = currentTable.get(DELTA_T + plane);
    return deltaT == null ? null : new Double(deltaT);
  }

  public Double getPositionX(int plane) {
    String pos = currentTable.get(X_POSITION + plane);
    return pos == null ? null : new Double(pos);
  }

  public Double getPositionY(int plane) {
    String pos = currentTable.get(Y_POSITION + plane);
    return pos == null ? null : new Double(pos);
  }

  public Double getPositionZ(int plane) {
    String pos = currentTable.get(Z_POSITION + plane);
    return pos == null ? null : new Double(pos);
  }

  public Double getEmissionWavelength(int channel) {
    String wavelength = currentTable.get(EMISSION_WAVELENGTH + channel);
    return wavelength == null ? null : new Double(wavelength);
  }

  public Double getExcitationWavelength(int channel) {
    String wavelength = currentTable.get(EXCITATION_WAVELENGTH + channel);
    return wavelength == null ? null : new Double(wavelength);
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

  public void setSeries(int series) {
    Location file = new Location(dataFile);
    currentTable = ini.getTable(file.getName() + SERIES + series);
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
      reader.setSeries(series);

      IniTable seriesTable = new IniTable();
      putTableName(seriesTable, reader, SERIES + series);

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
        String.valueOf(retrieve.getChannelCount(series)));

      try {
        planeSize = DataTools.safeMultiply32(reader.getSizeX(),
          reader.getSizeY(), reader.getEffectiveSizeC(),
          FormatTools.getBytesPerPixel(reader.getPixelType()));
        canOpenImages = planeSize > 0 && TestTools.canFitInMemory(planeSize);
      }
      catch (IllegalArgumentException e) {
        canOpenImages = false;
      }

      if (canOpenImages) {
        try {
          byte[] plane = reader.openBytes(0);
          seriesTable.put(MD5, TestTools.md5(plane));
        }
        catch (FormatException e) {
          // TODO
        }
        catch (IOException e) {
          // TODO
        }
      }

      try {
        int w = (int) Math.min(TILE_SIZE, reader.getSizeX());
        int h = (int) Math.min(TILE_SIZE, reader.getSizeY());

        byte[] tile = reader.openBytes(0, 0, 0, w, h);
        seriesTable.put(TILE_MD5, TestTools.md5(tile));
      }
      catch (FormatException e) {
        // TODO
      }
      catch (IOException e) {
        // TODO
      }

      seriesTable.put(NAME, retrieve.getImageName(series));
      seriesTable.put(DESCRIPTION, retrieve.getImageDescription(series));

      Length physicalX = retrieve.getPixelsPhysicalSizeX(series);
      if (physicalX != null) {
        seriesTable.put(PHYSICAL_SIZE_X, physicalX.value(UNITS.MICROM).toString());
      }
      Length physicalY = retrieve.getPixelsPhysicalSizeY(series);
      if (physicalY != null) {
        seriesTable.put(PHYSICAL_SIZE_Y, physicalY.value(UNITS.MICROM).toString());
      }
      Length physicalZ = retrieve.getPixelsPhysicalSizeZ(series);
      if (physicalZ != null) {
        seriesTable.put(PHYSICAL_SIZE_Z, physicalZ.value(UNITS.MICROM).toString());
      }
      Time timeIncrement = retrieve.getPixelsTimeIncrement(series);
      if (timeIncrement != null) {
        seriesTable.put(TIME_INCREMENT, timeIncrement.value().toString());
      }

      Timestamp acquisition = retrieve.getImageAcquisitionDate(series);
      if (acquisition != null) {
        String date = acquisition.getValue();
        if (date != null) {
          seriesTable.put(DATE, date);
        }
      }

      for (int c=0; c<retrieve.getChannelCount(series); c++) {
        seriesTable.put(CHANNEL_NAME + c, retrieve.getChannelName(series, c));
        try {
          seriesTable.put(LIGHT_SOURCE + c,
            retrieve.getChannelLightSourceSettingsID(series, c));
        }
        catch (NullPointerException e) { }

        try {
          int plane = reader.getIndex(0, c, 0);
          if (plane < retrieve.getPlaneCount(series)) {
            seriesTable.put(EXPOSURE_TIME + c,
              retrieve.getPlaneExposureTime(series, plane).value().toString());
          }
        }
        catch (NullPointerException e) { }

        Length emWavelength = retrieve.getChannelEmissionWavelength(series, c);
        if (emWavelength != null) {
          seriesTable.put(EMISSION_WAVELENGTH + c, emWavelength.value(UNITS.NM).toString());
        }
        Length exWavelength =
          retrieve.getChannelExcitationWavelength(series, c);
        if (exWavelength != null) {
          seriesTable.put(EXCITATION_WAVELENGTH + c, exWavelength.value(UNITS.NM).toString());
        }
        try {
          seriesTable.put(DETECTOR + c,
            retrieve.getDetectorSettingsID(series, c));
        }
        catch (NullPointerException e) { }
      }

      for (int p=0; p<reader.getImageCount(); p++) {
        try {
          Time deltaT = retrieve.getPlaneDeltaT(series, p);
          if (deltaT != null) {
            seriesTable.put(DELTA_T + p, deltaT.value(UNITS.S).toString());
          }
          Length xPos = retrieve.getPlanePositionX(series, p);
          if (xPos != null) {
            seriesTable.put(X_POSITION + p, xPos.value(UNITS.REFERENCEFRAME).toString());
          }
          Length yPos = retrieve.getPlanePositionY(series, p);
          if (yPos != null) {
            seriesTable.put(Y_POSITION + p, yPos.value(UNITS.REFERENCEFRAME).toString());
          }
          Length zPos = retrieve.getPlanePositionZ(series, p);
          if (zPos != null) {
            seriesTable.put(Z_POSITION + p, zPos.value(UNITS.REFERENCEFRAME).toString());
          }
        }
        catch (IndexOutOfBoundsException e) {
          // only happens if no Plane elements were populated
        }
      }

      ini.add(seriesTable);
    }

  }

  private void putTableName(IniTable table, IFormatReader reader, String suffix)
  {
    Location file = new Location(reader.getCurrentFile());
    table.put(IniTable.HEADER_KEY, file.getName() + suffix);
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

}
