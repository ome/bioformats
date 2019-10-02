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

import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import loci.common.DataTools;
import loci.common.Location;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.units.quantity.Time;
import ome.xml.model.primitives.Color;
import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteConfig;

/**
 *
 */
public class TecanReader extends FormatReader {

  // -- Constants --

  private static final Logger LOGGER = LoggerFactory.getLogger(TecanReader.class);

  // -- Fields --

  private int plateRows = 0;
  private int plateColumns = 0;
  private transient String plateName = null;
  private ArrayList<Image> images = new ArrayList<Image>();
  private transient MinimalTiffReader helperReader = null;
  private transient ArrayList<String> channels = new ArrayList<String>();
  private String imageDirectory = null;

  // -- Constructor --

  /** Constructs a new Tecan Spark Cyto reader. */
  public TecanReader() {
    super("Tecan Spark Cyto", new String[] {"db"});
    hasCompanionFiles = true;
    domains = new String[] {FormatTools.HCS_DOMAIN};
    datasetDescription = "SQLite database, TIFF files, optional analysis output";
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

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);

    ArrayList<String> files = new ArrayList<String>();
    files.add(getCurrentFile());
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
    Location images = new Location(parent, "Images");
    if (!images.exists() || !images.isDirectory()) {
      throw new IOException("Cannot find expected 'Images' directory");
    }
    imageDirectory = images.getAbsolutePath();

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

    core.clear();
    helperReader = new MinimalTiffReader();
    for (int i=0; i<wellLabels.size(); i++) {
      Image img = lookupImage(i, 0, false, false);

      if (img != null) {
        helperReader.setId(getImageFile(img.file));
        core.add(helperReader.getCoreMetadataList().get(0));
        core.get(core.size() - 1).sizeC *= channels.size();
        core.get(core.size() - 1).imageCount *= channels.size();
      }
    }

    // populate the MetadataStore

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    store.setPlateID(MetadataTools.createLSID("Plate", 0), 0);
    store.setPlateName(plateName, 0);
    store.setPlateRows(new PositiveInteger(plateRows), 0);
    store.setPlateColumns(new PositiveInteger(plateColumns), 0);

    String plateAcqID = MetadataTools.createLSID("PlateAcquisition", 0, 0);
    store.setPlateAcquisitionID(plateAcqID, 0, 0);

    int nextWell = 0;
    int nextImage = 0;
    for (int row=0; row<plateRows; row++) {
      for (int col=0; col<plateColumns; col++) {
        store.setWellID(MetadataTools.createLSID("Well", 0, nextWell), 0, nextWell);
        store.setWellRow(new NonNegativeInteger(row), 0, nextWell);
        store.setWellColumn(new NonNegativeInteger(col), 0, nextWell);

        String label = (char) (row + 'A') + String.valueOf(col + 1);
        if (!wellLabels.containsValue(label)) {
          nextWell++;
          continue;
        }

        int field = 0;
        String wellSampleID =
          MetadataTools.createLSID("WellSample", 0, nextWell, field);
        store.setWellSampleID(wellSampleID, 0, nextWell, field);
        store.setWellSampleIndex(
          new NonNegativeInteger(nextImage), 0, nextWell, field);
        String imageID = MetadataTools.createLSID("Image", nextImage);
        store.setImageID(imageID, nextImage);
        store.setWellSampleImageRef(imageID, 0, nextWell, field);
        store.setImageName("Well " + label, nextImage);
        store.setPlateAcquisitionWellSampleRef(wellSampleID, 0, 0, nextImage);

        nextWell++;
        nextImage++;
      }
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      for (int i=0; i<getSeriesCount(); i++) {
        if (channels != null) {
          for (int c=0; c<getEffectiveSizeC(); c++) {
            store.setChannelName(channels.get(c), i, c);
          }
        }

        Image firstImage = lookupImage(i, 0, false, false);
        if (firstImage.pixelSize != null) {
          store.setPixelsPhysicalSizeX(firstImage.pixelSize, i);
          store.setPixelsPhysicalSizeY(firstImage.pixelSize, i);
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
      conn = config.createConnection("jdbc:sqlite:" + getCurrentFile());
    }
    catch (SQLException e) {
      LOGGER.warn("Could not read from database");
      throw new IOException(e);
    }
    return conn;
  }

  private void findPlateDimensions(Connection conn) throws SQLException {
    // find the basic plate dimensions
    // expect only one plate to be defined
    PreparedStatement statement = conn.prepareStatement(
      "SELECT Name, Rows, Columns FROM PlateDefinition ORDER BY Id");
    ResultSet plates = statement.executeQuery();

    if (plates.next()) {
      plateName = plates.getString(1);
      plateRows = plates.getInt(2);
      plateColumns = plates.getInt(3);
    }

    if (plates.next()) {
      LOGGER.warn("Found more than one plate; only using the first one");
    }
  }

  private HashMap<Integer, String> getWellLabels(Connection conn) throws SQLException {
    HashMap<Integer, String> labels = new HashMap<Integer, String>();

    PreparedStatement statement = conn.prepareStatement(
      "SELECT Id, AlphanumericCoordinate FROM SelectedWell");
    ResultSet wells = statement.executeQuery();

    while (wells.next()) {
      labels.put(wells.getInt(1), wells.getString(2));
    }
    return labels;
  }

  private void findImages(Connection conn, HashMap<Integer, String> wells) throws SQLException {
    PreparedStatement imageQuery = conn.prepareStatement(
      "SELECT ImageTypeId, ImagingResultId, RelativePath, PixelSizeInNm " +
      "FROM Image ORDER BY Id");
    ResultSet allImages = imageQuery.executeQuery();
    while (allImages.next()) {
      Image img = new Image();
      img.file = allImages.getString(3);
      img.pixelSize = FormatTools.getPhysicalSize(allImages.getDouble(4), "nm");

      String imageTypeId = allImages.getString(1);
      String resultId = allImages.getString(2);

      PreparedStatement typeQuery = conn.prepareStatement(
        "SELECT ChannelTypeId, IsResult, IsOverlay, Name " +
        "FROM ImageType WHERE Id = ?");
      typeQuery.setInt(1, Integer.parseInt(imageTypeId));
      ResultSet imageType = typeQuery.executeQuery();
      imageType.next();

      img.result = imageType.getBoolean(2);
      img.overlay = imageType.getBoolean(3);
      img.channelName = imageType.getString(4);

      // make sure the image is "Raw" and not "Processed"
      // without this check, the channel and image counts will be wrong
      // as processed files will be included
      PreparedStatement acquisitionType = conn.prepareStatement(
        "SELECT Name FROM ImagingResultType INNER JOIN ImagingResult " +
        "ON ImagingResultType.Id=ImagingResult.ImagingResultTypeId " +
        "WHERE ImagingResult.Id=?");
      acquisitionType.setInt(1, Integer.parseInt(resultId));
      ResultSet acqType = acquisitionType.executeQuery();
      acqType.next();

      if ("Raw".equals(acqType.getString(1)) && !img.result && !img.overlay) {
        int channelTypeId = imageType.getInt(1);
        PreparedStatement channelQuery = conn.prepareStatement(
          "SELECT Name FROM ChannelType WHERE Id=?");
        // TODO: join on AcquisitionChannelSetting (ChannelTypeId)
        channelQuery.setInt(1, channelTypeId);
        ResultSet channel = channelQuery.executeQuery();
        // might return 0 rows for overlay/result images
        if (channel.next()) {
          // a single ChannelType (e.g. brightfield) can map
          // to multiple ImageTypes in the same well
          img.channelName += " " + channel.getString(1);

          if (!channels.contains(img.channelName)) {
            channels.add(img.channelName);
          }
          img.plane = channels.indexOf(img.channelName);
        }
      }

      // now map the image to a well

      PreparedStatement linkQuery = conn.prepareStatement(
        "SELECT SelectedWellId FROM ImagingResult " +
        "INNER JOIN ResultContext ON " +
        "ImagingResult.ResultContextId=ResultContext.Id WHERE " +
        "ImagingResult.Id=?");
      linkQuery.setInt(1, Integer.parseInt(resultId));
      ResultSet wellLink = linkQuery.executeQuery();
      wellLink.next();

      Integer wellId = wellLink.getInt(1);
      String wellLabel = wells.get(wellId);
      img.wellRow = wellLabel.charAt(0) - 'A';
      img.wellColumn = Integer.parseInt(wellLabel.substring(1)) - 1;
      img.series = wellId - 1;

      images.add(img);
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

  /**
   * Turn a relative image file path into an absolute path.
   * Storing relative paths and one copy of the parent directory
   * saves some space in the memo file, especially for large plates.
   *
   * @param file relative path to an image file
   * @return absolute path to the image file
   */
  private String getImageFile(String file) {
    return new Location(imageDirectory, file).getAbsolutePath();
  }

  class Image {
    public String file;
    public int wellRow = -1;
    public int wellColumn = -1;
    public int field = -1;
    public int series = -1;
    public int plane = -1;
    public Length pixelSize;
    public String channelName;
    public boolean overlay = false;
    public boolean result = false;
  }

}
