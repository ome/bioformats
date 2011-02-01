package loci.tests.testng;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import loci.common.IniList;
import loci.common.IniParser;
import loci.common.IniTable;
import loci.common.IniWriter;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import loci.formats.meta.IMetadata;

public class Configuration {

  // -- Constants --

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
  private static final String PHYSICAL_SIZE_X = "PhysicalSizeX";
  private static final String PHYSICAL_SIZE_Y = "PhysicalSizeY";
  private static final String PHYSICAL_SIZE_Z = "PhysicalSizeZ";
  private static final String TIME_INCREMENT = "TimeIncrement";
  private static final String LIGHT_SOURCE = "LightSource_";
  private static final String CHANNEL_NAME = "ChannelName_";
  private static final String EMISSION_WAVELENGTH = "EmissionWavelength_";
  private static final String EXCITATION_WAVELENGTH = "ExcitationWavelength_";
  private static final String DETECTOR = "Detector_";
  private static final String NAME = "Name";
  private static final String SERIES_COUNT = "series_count";
  private static final String CHANNEL_COUNT = "channel_count";

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

    BufferedReader reader = new BufferedReader(new FileReader(this.configFile));
    IniParser parser = new IniParser();
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

  public long getAccessTimeMillis() {
    return Long.parseLong(globalTable.get(ACCESS_TIME));
  }

  public int getMemory() {
    return Integer.parseInt(globalTable.get(MEMORY));
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

  public Double getPhysicalSizeX() {
    return new Double(currentTable.get(PHYSICAL_SIZE_X));
  }

  public Double getPhysicalSizeY() {
    return new Double(currentTable.get(PHYSICAL_SIZE_Y));
  }

  public Double getPhysicalSizeZ() {
    return new Double(currentTable.get(PHYSICAL_SIZE_Z));
  }

  public Double getTimeIncrement() {
    return new Double(currentTable.get(TIME_INCREMENT));
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

  public int getEmissionWavelength(int channel) {
    return Integer.parseInt(currentTable.get(EMISSION_WAVELENGTH + channel));
  }

  public int getExcitationWavelength(int channel) {
    return Integer.parseInt(currentTable.get(EXCITATION_WAVELENGTH + channel));
  }

  public String getDetector(int channel) {
    return currentTable.get(DETECTOR + channel);
  }

  public String getImageName() {
    return currentTable.get(IMAGE_NAME);
  }

  public void setSeries(int series) {
    currentTable = ini.getTable(dataFile + SERIES + series);
  }

  public void saveToFile() throws IOException {
    IniWriter writer = new IniWriter();
    writer.saveINI(ini, configFile);
  }

  public IniList getINI() {
    return ini;
  }

  // -- Object API methods --

  public boolean equals(Object o) {
    if (!(o instanceof Configuration)) return false;

    Configuration thatConfig = (Configuration) o;
    return this.getINI().equals(thatConfig.getINI());
  }

  // -- Helper methods --

  private void populateINI(IFormatReader reader) {
    IMetadata retrieve = (IMetadata) reader.getMetadataStore();

    ini = new IniList();

    IniTable globalTable = new IniTable();
    putTableName(globalTable, reader, " global");

    int seriesCount = reader.getSeriesCount();

    globalTable.put(SERIES_COUNT, String.valueOf(seriesCount));
    globalTable.put("reader", "");
    globalTable.put("test", "true");
    globalTable.put("mem_mb", "");
    globalTable.put("access_ms", "");

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
        byte[] plane = reader.openBytes(0);
        seriesTable.put(MD5, TestTools.md5(plane));
      }
      catch (FormatException e) {
        // TODO
      }
      catch (IOException e) {
        // TODO
      }

      seriesTable.put(NAME, retrieve.getImageName(series));

      String physicalX = retrieve.getPixelsPhysicalSizeX(series).toString();
      seriesTable.put(PHYSICAL_SIZE_X, physicalX);
      String physicalY = retrieve.getPixelsPhysicalSizeY(series).toString();
      seriesTable.put(PHYSICAL_SIZE_Y, physicalY);
      String physicalZ = retrieve.getPixelsPhysicalSizeZ(series).toString();
      seriesTable.put(PHYSICAL_SIZE_Z, physicalZ);
      String timeIncrement = retrieve.getPixelsTimeIncrement(series).toString();
      seriesTable.put(TIME_INCREMENT, timeIncrement);

      for (int c=0; c<retrieve.getChannelCount(series); c++) {
        seriesTable.put(CHANNEL_NAME + c, retrieve.getChannelName(series, c));
        seriesTable.put(LIGHT_SOURCE + c,
          retrieve.getChannelLightSourceSettingsID(series, c));

        String emWavelength =
          retrieve.getChannelEmissionWavelength(series, c).toString();
        seriesTable.put(EMISSION_WAVELENGTH + c, emWavelength);
        String exWavelength =
          retrieve.getChannelExcitationWavelength(series, c).toString();
        seriesTable.put(EXCITATION_WAVELENGTH + c, exWavelength);
        seriesTable.put(DETECTOR + c,
          retrieve.getDetectorSettingsID(series, c));
      }

      ini.add(seriesTable);
    }

  }

  private void putTableName(IniTable table, IFormatReader reader, String suffix)
  {
    table.put(IniTable.HEADER_KEY, reader.getCurrentFile() + suffix);
  }

  private void pruneINI() {
    IniList newIni = new IniList();
    for (IniTable table : ini) {
      String tableName = table.get(IniTable.HEADER_KEY);
      if (tableName.startsWith(dataFile)) {
        newIni.add(table);
      }
    }
    ini = newIni;
  }

}
