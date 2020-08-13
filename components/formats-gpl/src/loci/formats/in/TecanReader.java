/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2019 Open Microscopy Environment:
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
import java.io.FileInputStream;
import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.parsers.ParserConfigurationException;

import loci.common.Constants;
import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.ZipHandle;
import loci.common.xml.XMLTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteConfig;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 */
public class TecanReader extends FormatReader {

  // -- Constants --

  private static final Logger LOGGER =
    LoggerFactory.getLogger(TecanReader.class);

  private static final String[] SPREADSHEET_KEYS = new String[] {
    "Application:", "Device", "Firmware",
    "System", "User", "Smooth mode", "Part of Plate"
  };

  // -- Fields --

  private int plateRows = 0;
  private int plateColumns = 0;
  private transient String plateName = null;
  private ArrayList<Image> images = new ArrayList<Image>();
  private transient MinimalTiffReader helperReader = null;
  private transient ArrayList<Channel> channels = new ArrayList<Channel>();
  private String imageDirectory = null;
  private ArrayList<String> extraFiles = new ArrayList<String>();
  private Integer maxField = 1;
  private Integer maxCycle = 1;

  // -- Constructor --

  /** Constructs a new Tecan Spark Cyto reader. */
  public TecanReader() {
    super("Tecan Spark Cyto", new String[] {"db"});
    hasCompanionFiles = true;
    domains = new String[] {FormatTools.HCS_DOMAIN};
    datasetDescription =
      "SQLite database, TIFF files, optional analysis output";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getRequiredDirectories(String[]) */
  @Override
  public int getRequiredDirectories(String[] files)
    throws FormatException, IOException
  {
    return 3;
  }

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

  /* @see loci.formats.IFormatReader#getUsedFiles(boolean) */
  @Override
  public String[] getUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);

    ArrayList<String> files = new ArrayList<String>();
    files.add(getCurrentFile());
    files.addAll(extraFiles);
    for (Image img : images) {
      if (img.result || img.overlay || !noPixels) {
        files.add(getImageFile(img.file));
      }
    }
    String[] rtn = files.toArray(new String[files.size()]);
    Arrays.sort(rtn);
    return rtn;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);

    ArrayList<String> files = new ArrayList<String>();
    files.add(getCurrentFile());
    files.addAll(extraFiles);
    for (Image img : images) {
      if (img.series == getSeries()) {
        if (img.result || img.overlay || !noPixels) {
          files.add(getImageFile(img.file));
        }
      }
    }
    String[] rtn = files.toArray(new String[files.size()]);
    Arrays.sort(rtn);
    return rtn;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (helperReader != null) {
      helperReader.close(fileOnly);
    }
    if (!fileOnly) {
      images.clear();
      channels.clear();
      imageDirectory = null;
      plateRows = 0;
      plateColumns = 0;
      plateName = null;
      helperReader = null;
      extraFiles.clear();
      maxField = 1;
      maxCycle = 1;
    }
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    Arrays.fill(buf, (byte) 0);
    Image img = lookupImage(getSeries(), no, false, false);
    if (img != null && img.file != null) {
      if (helperReader == null) {
        helperReader = new MinimalTiffReader();
      }
      String filePath = getImageFile(img.file);
      LOGGER.debug("Reading plane {} in series {} from {}",
        no, getSeries(), filePath);
      helperReader.setId(filePath);
      if (helperReader.getImageCount() > 1) {
        LOGGER.warn("File {} has {} planes",
          img.file, helperReader.getImageCount());
      }
      helperReader.openBytes(0, buf, x, y, w, h);
    }
    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    // find the parent directory for image data
    Location parent = new Location(currentId).getAbsoluteFile().getParentFile();
    parent = parent.getParentFile();
    Location imageDir = new Location(parent, "Images");
    if (!imageDir.exists() || !imageDir.isDirectory()) {
      throw new IOException("Cannot find expected 'Images' directory");
    }
    imageDirectory = imageDir.getAbsolutePath();

    // look for extra files in the "Export" directory
    Location export = new Location(parent, "Export");
    if (export.exists() && export.isDirectory()) {
      findAllFiles(export, extraFiles);
    }

    Connection conn = openConnection();
    HashMap<Integer, String> wellLabels = null;

    try {
      findPlateDimensions(conn);

      wellLabels = getWellLabels(conn);
      findImages(conn, wellLabels);
    }
    catch (SQLException e) {
      throw new IOException("Could not assemble plate", e);
    }
    finally {
      if (conn != null) {
        try {
          conn.close();
        }
        catch (SQLException e) {
          LOGGER.warn("Could not close database connection", e);
        }
      }
    }

    // update series and plane index for each Image to account for fields/timepoints
    for (Image img : images) {
      img.series *= maxField;
      img.series += (img.field - 1);
      img.plane += (img.cycle - 1) * channels.size();
    }

    core.clear();
    helperReader = new MinimalTiffReader();
    for (int w=0; w<wellLabels.size(); w++) {
      Image firstImage = lookupImage(w * maxField, 0, false, false);
      if (firstImage != null) {
        helperReader.setId(getImageFile(firstImage.file));
      }
      else {
        throw new FormatException("Could not find first file");
      }
      for (int f=0; f<maxField; f++) {
        core.add(helperReader.getCoreMetadataList().get(0));
        core.get(core.size() - 1).sizeC *= channels.size();
        core.get(core.size() - 1).sizeT = maxCycle;
        core.get(core.size() - 1).imageCount *= (channels.size() * maxCycle);
      }
    }

    findExtraMetadata();

    // populate the MetadataStore

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    store.setPlateID(MetadataTools.createLSID("Plate", 0), 0);
    store.setPlateName(plateName, 0);
    store.setPlateRows(new PositiveInteger(plateRows), 0);
    store.setPlateColumns(new PositiveInteger(plateColumns), 0);

    String plateAcqID = MetadataTools.createLSID("PlateAcquisition", 0, 0);
    store.setPlateAcquisitionID(plateAcqID, 0, 0);
    store.setPlateAcquisitionMaximumFieldCount(
      new PositiveInteger(maxField), 0, 0);

    int nextWell = 0;
    int nextImage = 0;
    for (int row=0; row<plateRows; row++) {
      for (int col=0; col<plateColumns; col++) {
        store.setWellID(
          MetadataTools.createLSID("Well", 0, nextWell), 0, nextWell);
        store.setWellRow(new NonNegativeInteger(row), 0, nextWell);
        store.setWellColumn(new NonNegativeInteger(col), 0, nextWell);

        String label = makeWellLabel(row, col);
        if (!wellLabels.containsValue(label)) {
          nextWell++;
          continue;
        }

        for (int field=0; field<maxField; field++) {
          String wellSampleID =
            MetadataTools.createLSID("WellSample", 0, nextWell, field);
          store.setWellSampleID(wellSampleID, 0, nextWell, field);
          store.setWellSampleIndex(
            new NonNegativeInteger(nextImage), 0, nextWell, field);
          String imageID = MetadataTools.createLSID("Image", nextImage);
          store.setImageID(imageID, nextImage);
          store.setWellSampleImageRef(imageID, 0, nextWell, field);
          store.setImageName(
            "Well " + label + " Field " + (field + 1), nextImage);
          store.setPlateAcquisitionWellSampleRef(wellSampleID, 0, 0, nextImage);
          nextImage++;
        }

        nextWell++;
      }
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      for (int i=0; i<getSeriesCount(); i++) {
        if (channels != null) {
          for (int c=0; c<getEffectiveSizeC(); c++) {
            store.setChannelName(channels.get(c).name, i, c);
          }
        }

        Image firstImage = lookupImage(i, 0, false, false);
        if (firstImage.pixelSize != null) {
          store.setPixelsPhysicalSizeX(firstImage.pixelSize, i);
          store.setPixelsPhysicalSizeY(firstImage.pixelSize, i);
        }
        if (firstImage.timestamp != null) {
          store.setImageAcquisitionDate(new Timestamp(
            DateTools.formatDate(firstImage.timestamp,
            "yyyy-MM-dd HH:mm:ss.SSSSSS")), i);
        }

        for (int p=0; p<getImageCount(); p++) {
          Image img = lookupImage(i, p, false, false);
          if (img.exposureTime != null) {
            store.setPlaneExposureTime(img.exposureTime, i, p);
          }
        }
      }
    }
  }

  private Connection openConnection() throws IOException {
    Connection conn = null;
    try {
      // see https://github.com/xerial/sqlite-jdbc/issues/247
      SQLiteConfig config = new SQLiteConfig();
      config.setReadOnly(true);
      conn = config.createConnection("jdbc:sqlite:" +
        new Location(getCurrentFile()).getAbsolutePath());
    }
    catch (SQLException e) {
      LOGGER.warn("Could not read from database");
      throw new IOException(e);
    }
    return conn;
  }

  private void findExtraMetadata() {
    Connection conn = null;
    try {
      conn = openConnection();

      PreparedStatement instrument = conn.prepareStatement(
        "SELECT InstrumentSerial FROM InstrumentConfig ORDER BY Id");
      try (ResultSet instruments = instrument.executeQuery()) {
        if (instruments.next()) {
          String serialNumber = instruments.getString(1);
          addGlobalMeta("Serial number", serialNumber);
        }
      }

      PreparedStatement completionTime = conn.prepareStatement(
        "SELECT CompletedAt FROM Workspace ORDER BY Id"
      );
      try (ResultSet workspace = completionTime.executeQuery()) {
        if (workspace.next()) {
          addGlobalMeta("Date/Time", workspace.getString(1));
        }
      }

      PreparedStatement acquisitionSetting = conn.prepareStatement(
        "SELECT ObjectiveTypeId, IntrawellPatternId, SettleTimeInMs " +
        "FROM AcquisitionSetting ORDER BY Id"
      );
      try (ResultSet acquisition = acquisitionSetting.executeQuery()) {
        if (acquisition.next()) {
          int objectiveTypeId = acquisition.getInt(1);
          int intrawellPatternId = acquisition.getInt(2);

          addGlobalMeta("Settle time [ms]", acquisition.getDouble(3));

          PreparedStatement objectiveType = conn.prepareStatement(
            "SELECT Name FROM ObjectiveType WHERE Id=?"
          );
          objectiveType.setInt(1, objectiveTypeId);
          try (ResultSet objective = objectiveType.executeQuery()) {
            if (objective.next()) {
              addGlobalMeta("Objective", objective.getString(1));
            }
          }

          PreparedStatement wellPattern = conn.prepareStatement(
            "SELECT Name FROM IntrawellPattern INNER JOIN IntrawellPatternType " +
            "ON IntrawellPattern.IntrawellPatternTypeId=IntrawellPatternType.Id " +
            "WHERE IntrawellPattern.Id=?"
          );
          wellPattern.setInt(1, intrawellPatternId);
          try (ResultSet wellPatternType = wellPattern.executeQuery()) {
            if (wellPatternType.next()) {
              addGlobalMeta("Pattern", wellPatternType.getString(1));
            }
          }
        }
      }

      PreparedStatement application = conn.prepareStatement(
        "SELECT Name FROM AnalysisSetting " +
        "INNER JOIN ApplicationType " +
        "ON AnalysisSetting.ApplicationTypeId=ApplicationType.Id"
      );
      try (ResultSet applicationType = application.executeQuery()) {
        if (applicationType.next()) {
          addGlobalMeta("Application type", applicationType.getString(1));
        }
      }

      PreparedStatement labels = conn.prepareStatement(
        "SELECT DISTINCT OutputName FROM ImagingResult " +
        "INNER JOIN DataLabel " +
        "ON ImagingResult.DataLabelId=DataLabel.Id"
      );
      try (ResultSet labelNames = labels.executeQuery()) {
        while (labelNames.next()) {
          addGlobalMetaList("Label Name", labelNames.getString(1));
        }
      }

      PreparedStatement method = conn.prepareStatement(
        "SELECT MethodName, SerializedMethod FROM MethodSnapshot ORDER BY Id"
      );
      try (ResultSet methods = method.executeQuery()) {
        if (methods.next()) {
          String methodName = methods.getString(1);
          String methodXML = methods.getString(2);

          addGlobalMeta("Method name", methodName);

          try {
            methodXML = methodXML.substring(methodXML.indexOf("<MethodStrip"));
            Element root = XMLTools.parseDOM(methodXML).getDocumentElement();
            NodeList channelSettings = root.getElementsByTagName("tadodssdf:FluorescenceImagingChannelSetting");
            NodeList plateStrips = root.getElementsByTagName("PlateStrip");
            NodeList plateDefinitions = root.getElementsByTagName("MicroplateDefinition");

            if (plateStrips.getLength() > 0) {
              Element plateStrip = (Element) plateStrips.item(0);
              addGlobalMeta("Lid lifter", plateStrip.getAttribute("LidType"));
              addGlobalMeta("Humidity Cassette", plateStrip.getAttribute("HumidityCassetteType"));
            }

            if (plateDefinitions.getLength() > 0) {
              Element plateDef = (Element) plateDefinitions.item(0);
              addGlobalMeta("Plate",
                plateDef.getAttribute("DisplayName") + " " + plateDef.getAttribute("Comment"));
            }

            StringBuffer channelNames = new StringBuffer();
            for (int i=0; i<channelSettings.getLength(); i++) {
              Element channel = (Element) channelSettings.item(i);
              String enabled = channel.getAttribute("IsEnabled");
              String name = channel.getAttribute("Name");

              if (Boolean.parseBoolean(enabled)) {
                if (channelNames.length() > 0) {
                  channelNames.append(", ");
                }
                channelNames.append(name);

                int index = lookupChannelIndex(name);
                if (index >= 0) {
                  NodeList crosstalkCorrections = channel.getElementsByTagName("tadodssdf:CrosstalkSettings.CrosstalkCorrectionDict");
                  if (crosstalkCorrections.getLength() > 0) {
                    Element crosstalkCorrection = (Element) crosstalkCorrections.item(0);
                    NodeList dict = crosstalkCorrection.getElementsByTagName("x:Int16");
                    StringBuffer crosstalk = new StringBuffer();

                    for (int d=0; d<dict.getLength(); d++) {
                      if (crosstalk.length() > 0) {
                        crosstalk.append(", ");
                      }
                      Element component = (Element) dict.item(d);
                      crosstalk.append(component.getAttribute("x:Key"));
                      crosstalk.append(": ");
                      crosstalk.append(component.getTextContent());
                      crosstalk.append("%");
                    }

                    addGlobalMeta("Channel #" + (index + 1) + " Cross-talk settings",
                      crosstalk.toString());
                  }
                }
              }
            }
            addGlobalMeta("Channels", channelNames.toString());
          }
          catch (ParserConfigurationException|SAXException e) {
            LOGGER.debug("Could not parse XML", e);
          }
        }
      }
    }
    catch (IOException|SQLException e) {
      LOGGER.warn("Could not read all extra metadata", e);
    }
    finally {
      if (conn != null) {
        try {
          conn.close();
        }
        catch (SQLException ex) {
          LOGGER.warn("Could not close database connection", ex);
        }
      }
    }

    for (int c=0; c<channels.size(); c++) {
      String prefix = "Channel #" + (c + 1) + " ";
      Channel channel = channels.get(c);

      addGlobalMeta(prefix + "Name", channel.originalName);
      addGlobalMeta(prefix + "LED Intensity [%]", channel.intensity);
      addGlobalMeta(prefix + "Focus offset [µm]", channel.focusOffset);
      addGlobalMeta(prefix + "Exposure time [µs]", channel.exposureTime);
    }

    for (int s=0; s<getSeriesCount(); s++) {
      setSeries(s);

      for (int p=0; p<getImageCount(); p++) {
        Image img = lookupImage(s, p, false, false);

        String prefix = "Plane #" + (p + 1) + " ";

        addSeriesMeta(prefix + "Position on Micro Plate",
          makeWellLabel(img.wellRow, img.wellColumn));
        addSeriesMeta(prefix + "File creation time", img.timestamp);

        String[] fileTokens = img.file.split("_");
        addSeriesMeta(prefix + "Data", fileTokens[fileTokens.length - 2]);
      }
    }
    setSeries(0);

    parseSpreadsheet();
  }

  private void parseSpreadsheet() {
    try {
      String spreadsheet = null;
      for (String file : extraFiles) {
        if (checkSuffix(file, "xlsx")) {
          spreadsheet = file;
          break;
        }
      }
      if (spreadsheet == null) {
        return;
      }

      String sharedStringsXML = null;
      String firstSheetXML = null;

      try (ZipInputStream z = new ZipInputStream(new FileInputStream(spreadsheet))) {
        while (true) {
          ZipEntry ze = z.getNextEntry();
          if (ze == null) {
            break;
          }
          if (ze.getName().equals("xl/sharedStrings.xml")) {
            ZipHandle handle = new ZipHandle(spreadsheet, ze);
            try {
              byte[] b = new byte[(int) handle.length()];
              handle.read(b);
              sharedStringsXML = new String(b, Constants.ENCODING).trim();
            }
            finally {
              handle.close();
            }
          }
          else if (ze.getName().equals("xl/worksheets/sheet1.xml")) {
            ZipHandle handle = new ZipHandle(spreadsheet, ze);
            try {
              byte[] b = new byte[(int) handle.length()];
              handle.read(b);
              firstSheetXML = new String(b, Constants.ENCODING).trim();
            }
            finally {
              handle.close();
            }
          }
          if (sharedStringsXML != null && firstSheetXML != null) {
            break;
          }
        }
      }

      if (sharedStringsXML != null && firstSheetXML != null) {
        Element sharedStrings = XMLTools.parseDOM(sharedStringsXML).getDocumentElement();
        NodeList strings = sharedStrings.getElementsByTagName("t");

        Element worksheet = XMLTools.parseDOM(firstSheetXML).getDocumentElement();
        NodeList cells = worksheet.getElementsByTagName("c");

        String key = null;
        String value = "";
        for (int i=0; i<cells.getLength(); i++) {
          Element cell = (Element) cells.item(i);

          if (cell.getFirstChild() != null) {
            int cellValueIndex = Integer.parseInt(cell.getFirstChild().getTextContent());
            if (cellValueIndex < 0 || cellValueIndex >= strings.getLength()) {
              continue;
            }
            String cellValue = strings.item(cellValueIndex).getTextContent();

            if (DataTools.indexOf(SPREADSHEET_KEYS, cellValue) >= 0) {
              key = cellValue;
            }
            else if (key != null) {
              addGlobalMeta(key, value + " " + cellValue);
              key = null;
              value = "";
            }
            else {
              for (String knownKey : SPREADSHEET_KEYS) {
                if (cellValue.startsWith(knownKey) ||
                  cellValue.startsWith(knownKey + ":"))
                {
                  int index = cellValue.indexOf(":");
                  if (index > 0) {
                    key = cellValue.substring(0, index);
                    value = cellValue.substring(index + 1).trim();
                  }
                }
              }
            }
          }
        }
      }
    }
    catch (IOException|ParserConfigurationException|SAXException e) {
      LOGGER.debug("Could not parse spreadsheet", e);
    }
  }

  private String makeWellLabel(int row, int col) {
    return (char) (row + 'A') + String.valueOf(col + 1);
  }

  private void findPlateDimensions(Connection conn) throws SQLException {
    // find the basic plate dimensions
    // expect only one plate to be defined
    PreparedStatement statement = conn.prepareStatement(
      "SELECT Name, Rows, Columns FROM PlateDefinition ORDER BY Id");
    try (ResultSet plates = statement.executeQuery()) {
      if (plates.next()) {
        plateName = plates.getString(1);
        plateRows = plates.getInt(2);
        plateColumns = plates.getInt(3);
      }

      if (plates.next()) {
        LOGGER.warn("Found more than one plate; only using the first one");
      }
    }
  }

  private HashMap<Integer, String> getWellLabels(Connection conn)
    throws SQLException
  {
    HashMap<Integer, String> labels = new HashMap<Integer, String>();

    PreparedStatement statement = conn.prepareStatement(
      "SELECT Id, AlphanumericCoordinate FROM SelectedWell");
    try (ResultSet wells = statement.executeQuery()) {
      while (wells.next()) {
        labels.put(wells.getInt(1), wells.getString(2));
      }
    }
    return labels;
  }

  private void findImages(Connection conn, HashMap<Integer, String> wells)
    throws SQLException
  {
    PreparedStatement imageQuery = conn.prepareStatement(
      "SELECT ImageTypeId, ImagingResultId, RelativePath, PixelSizeInNm " +
      "FROM Image ORDER BY Id");

    // not clear if CycleIndex is a field or timepoint index
    // treating as a field for now (since that's more difficult),
    // but easy to switch to timepoint if needed
    PreparedStatement wellLinkQuery = conn.prepareStatement(
      "SELECT SelectedWellId, CycleIndex FROM ImagingResult " +
      "INNER JOIN ResultContext ON " +
      "ImagingResult.ResultContextId=ResultContext.Id WHERE " +
      "ImagingResult.Id=?");
    PreparedStatement typeQuery = conn.prepareStatement(
      "SELECT ChannelTypeId, IsResult, IsOverlay, Name " +
      "FROM ImageType WHERE Id = ?");
    PreparedStatement acquisitionType = conn.prepareStatement(
      "SELECT Name, CreatedAt FROM ImagingResultType " +
      "INNER JOIN ImagingResult " +
      "ON ImagingResultType.Id=ImagingResult.ImagingResultTypeId " +
      "WHERE ImagingResult.Id=?");
    PreparedStatement channelQuery = conn.prepareStatement(
      "SELECT Name, ExposureTimeInUs, FocusOffsetInUm, LedIntensityInPercent FROM ChannelType " +
      "INNER JOIN AcquisitionChannelSetting " +
      "ON AcquisitionChannelSetting.ChannelTypeId=ChannelType.Id " +
      "WHERE ChannelType.Id=?");

    try (ResultSet allImages = imageQuery.executeQuery()) {
      while (allImages.next()) {
        Image img = new Image();
        img.file = allImages.getString(3);
        img.pixelSize =
          FormatTools.getPhysicalSize(allImages.getDouble(4), "nm");
        LOGGER.debug("processing image file = {}", img.file);

        String imageTypeId = allImages.getString(1);
        String resultId = allImages.getString(2);

        typeQuery.setInt(1, Integer.parseInt(imageTypeId));
        int channelTypeId = 0;
        try (ResultSet imageType = typeQuery.executeQuery()) {
          imageType.next();

          channelTypeId = imageType.getInt(1);
          img.result = imageType.getBoolean(2);
          img.overlay = imageType.getBoolean(3);
          img.channelName = imageType.getString(4);
        }

        // make sure the image is "Raw" and not "Processed"
        // without this check, the channel and image counts will be wrong
        // as processed files will be included
        acquisitionType.setInt(1, Integer.parseInt(resultId));

        String acqTypeName = null;
        String acqTimestamp = null;

        try (ResultSet acqType = acquisitionType.executeQuery()) {
          acqType.next();
          acqTypeName = acqType.getString(1);
          acqTimestamp = acqType.getString(2);
        }

        if ("Raw".equals(acqTypeName) && !img.result && !img.overlay) {
          channelQuery.setInt(1, channelTypeId);
          try (ResultSet channel = channelQuery.executeQuery()) {
            // might return 0 rows for overlay/result images
            if (channel.next()) {
              // a single ChannelType (e.g. brightfield) can map
              // to multiple ImageTypes in the same well
              img.channelName += " " + channel.getString(1);

              if (lookupChannelIndex(img.channelName) < 0) {
                Channel c = new Channel(img.channelName);
                c.originalName = channel.getString(1);
                c.exposureTime = channel.getDouble(2);
                c.focusOffset = channel.getDouble(3);
                c.intensity = channel.getDouble(4);
                channels.add(c);
              }
              img.plane = lookupChannelIndex(img.channelName);
              img.exposureTime = FormatTools.getTime(channel.getDouble(2), "µs");
              img.timestamp = acqTimestamp;
            }
          }
        }

        // now map the image to a well

        Integer[] ids = getWellLink(wellLinkQuery, resultId);
        String wellLabel = wells.get(ids[0]);
        img.wellRow = wellLabel.charAt(0) - 'A';
        img.wellColumn = Integer.parseInt(wellLabel.substring(1)) - 1;
        img.series = ids[0] - 1;
        // always set a 1-based index
        img.cycle = (int) Math.max(1, ids[1]);

        maxCycle = (int) Math.max(img.cycle, maxCycle);
        maxField = (int) Math.max(img.field, maxField);

        images.add(img);
      }
    }

    // list of SVG objects is stored separately
    // each row in ObjectList has a file which needs to be attached to a well
    PreparedStatement objectQuery = conn.prepareStatement(
      "SELECT ImagingResultId, Path FROM ObjectList ORDER BY Id");
    try (ResultSet objects = objectQuery.executeQuery()) {
      while (objects.next()) {
        String resultId = objects.getString(1);
        String path = objects.getString(2);
        LOGGER.debug("processing object {}", path);

        Integer[] ids = getWellLink(wellLinkQuery, resultId);
        if (ids != null) {
          String wellLabel = wells.get(ids[0]);
          Image img = new Image();
          img.wellRow = wellLabel.charAt(0) - 'A';
          img.wellColumn = Integer.parseInt(wellLabel.substring(1)) - 1;
          img.series = ids[0] - 1;
          // always set a 1-based index
          img.cycle = (int) Math.max(1, ids[1]);
          img.result = true;
          img.file = path;
          images.add(img);
        }
      }
    }
  }

  /**
   * Get the well identifier and field indexfor the given Id in the
   * ImagingResult table.
   * @param imagingResultID
   * @return well ID
   */
  private Integer[] getWellLink(PreparedStatement linkQuery,
    String imagingResultID)
    throws SQLException
  {
    linkQuery.setInt(1, Integer.parseInt(imagingResultID));
    try (ResultSet wellLink = linkQuery.executeQuery()) {
      wellLink.next();
      return new Integer[] {wellLink.getInt(1), wellLink.getInt(2)};
    }
  }

  private Image lookupImage(int series, int plane,
    boolean overlay, boolean result)
  {
    for (Image img : images) {
      if (img.series == series && img.plane == plane &&
        img.overlay == overlay && img.result == result)
      {
        return img;
      }
    }
    return null;
  }

  private int lookupChannelIndex(String name) {
    for (int i=0; i<channels.size(); i++) {
      if (channels.get(i).name.equals(name) ||
        channels.get(i).originalName.equals(name))
      {
        return i;
      }
    }
    return -1;
  }

  /**
   * Turn a relative image file path into an absolute path.
   * Storing relative paths and one copy of the parent directory
   * saves some space in the memo file, especially for large plates.
   *
   * @param file relative path to an image file
   * @return absolute path to the image file
   */
  private String getImageFile(String file) {
    // sanitize the relative path first
    // files might be stored in a subdirectory of "Images",
    // in which case the database may store a "\" to separate the path
    String sanitized = File.separator + file.replaceAll("\\\\", File.separator);
    return new Location(imageDirectory + sanitized).getAbsolutePath();
  }

  private void findAllFiles(Location root, ArrayList<String> files) {
    if (root.isDirectory()) {
      String[] list = root.list(true);
      for (String file : list) {
        Location path = new Location(root, file);
        findAllFiles(path, files);
      }
    }
    else {
      files.add(root.getAbsolutePath());
    }
  }

  class Image {
    public String file;
    public int wellRow = -1;
    public int wellColumn = -1;
    public int field = 1;
    public int cycle = -1;
    public int series = -1;
    public int plane = -1;
    public Length pixelSize;
    public Time exposureTime;
    public String channelName;
    public boolean overlay = false;
    public boolean result = false;
    public String timestamp;
  }

  class Channel {
    public String name;
    public String originalName;
    public int index = -1;
    public double intensity;
    public double focusOffset;
    public double exposureTime;

    public Channel(String name) {
      this.name = name;
    }
  }

}
