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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import loci.common.Constants;
import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.Region;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.UnsupportedCompressionException;
import loci.formats.codec.Codec;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.JPEGCodec;
import loci.formats.codec.JPEGXRCodec;
import loci.formats.codec.PassthroughCodec;
import loci.formats.meta.MetadataStore;

import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.xml.model.enums.AcquisitionMode;
import ome.xml.model.primitives.Color;
import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

import org.json.JSONException;
import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.SQLiteConfig;

/**
 * Reader for TissueFAXS format from TissueGnostics.
 */
public class TissueFAXSReader extends FormatReader {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(TissueFAXSReader.class);

  // per specification, wavelengths outside this range should be ignored
  private static final int WAVE_MIN = 300;
  private static final int WAVE_MAX = 800;

  private List<String> pixelsFiles = new ArrayList<String>();
  private List<ScanRegion> regions = new ArrayList<ScanRegion>();

  // -- Constructor --

  /** Constructs a new TissueGnostics TissueFAXS reader.*/
  public TissueFAXSReader() {
    super("TissueFAXS", new String[] {"aqproj", "tfcyto"});
    hasCompanionFiles = false;
    domains = new String[] {FormatTools.HISTOLOGY_DOMAIN};
    datasetDescription = "An .aqproj file with one or more .tfcyto database files";
    suffixSufficient = true;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      pixelsFiles.clear();
      regions.clear();
    }
  }

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    try {
      ScanRegion region = getRegion();
      return region.tileSizeX / (int) Math.pow(region.scaleFactor, getLevel());
    }
    catch (FormatException e) {
      LOGGER.warn("Could not get optimal tile width", e);
    }
    return super.getOptimalTileWidth();
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    try {
      ScanRegion region = getRegion();
      return region.tileSizeY / (int) Math.pow(region.scaleFactor, getLevel());
    }
    catch (FormatException e) {
      LOGGER.warn("Could not get optimal tile height", e);
    }
    return super.getOptimalTileHeight();
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);

    ArrayList<String> files = new ArrayList<String>();
    files.add(new Location(getCurrentFile()).getAbsolutePath());
    if (!noPixels) {
      try {
        String regionFile = getRegion().file;
        if (!files.contains(regionFile)) {
          files.add(regionFile);
        }
      }
      catch (FormatException e) {
        LOGGER.warn("Could not get file", e);
      }
    }
    return files.toArray(new String[files.size()]);
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

    int[] zct = getZCTCoords(no);
    List<ScanRegion> planeRegions = getAllRegions(zct[2]);
    int level = getLevel();

    Region dest = new Region(x, y, w, h);

    // most datasets will have a single region per plane/resolution
    // TMA data will have multiple regions for each full resolution plane
    for (ScanRegion region : planeRegions) {
      if (isCorrectionImage(region)) {
        copyCorrectionImageToBuffer(region, dest, zct, buf);
      }
      else {
        copyRegionToBuffer(region, level, dest, zct, buf);
      }
    }

    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    findDBFiles();

    core.clear();

    for (int i=0; i<pixelsFiles.size(); i++) {
      String file = pixelsFiles.get(i);
      CoreMetadata m = new CoreMetadata();
      m.pixelType = FormatTools.UINT8;

      int startRegionIndex = regions.size();

      Connection conn = openConnection(file);
      try {
        PreparedStatement regionQuery = conn.prepareStatement(
          "SELECT id, data, is_timelapse FROM region ORDER BY id"
        );
        ResultSet regionData = regionQuery.executeQuery();

        // expect one row per timepoint
        while (regionData.next()) {
          int regionID = regionData.getInt(1);
          String json = regionData.getString(2);
          boolean isTimelapse = regionData.getBoolean(3);

          LOGGER.trace("{}", json);

          ScanRegion region = new ScanRegion();
          region.id = regionID;
          region.file = file;

          try {
            // expect trailing whitespace and line breaks
            // in the value for "AcquisitionSettings",
            // which will prevent parsing
            json = json.trim();
            json = json.replaceAll("\r\n", "_");
            region.regionMetadata = new JSONObject(json);
          }
          catch (JSONException je) {
            throw new FormatException("Could not read metadata for region in " + file, je);
          }

          region.parseJSON();
          if (isTimelapse) {
            region.timepoint = regions.size() - startRegionIndex;
          }
          else if (regions.size() != startRegionIndex) {
            // found region that represents part of the full resolution in a TMA
            region.resolutions.add(0);
          }

          regions.add(region);
        }
        int timepoints = regions.size() - startRegionIndex;

        for (int regionIndex=startRegionIndex; regionIndex<regions.size(); regionIndex++) {
          ScanRegion currentRegion = regions.get(regionIndex);

          PreparedStatement fovQuery = conn.prepareStatement(
            "SELECT row, column, stitch_rectangle_x, stitch_rectangle_y, stitch_rectangle_w, stitch_rectangle_h FROM fovs WHERE region_id=?"
          );
          fovQuery.setInt(1, regions.get(regionIndex).id);

          ResultSet fovs = fovQuery.executeQuery();
          while (fovs.next()) {
            int row = fovs.getInt(1);
            int col = fovs.getInt(2);
            double x = fovs.getDouble(3);
            double y = fovs.getDouble(4);
            double w = fovs.getDouble(5);
            double h = fovs.getDouble(6);
            currentRegion.tileRangeY[0] = (int) Math.min(currentRegion.tileRangeY[0], row);
            currentRegion.tileRangeY[1] = (int) Math.max(currentRegion.tileRangeY[1], row);
            currentRegion.tileRangeX[0] = (int) Math.min(currentRegion.tileRangeX[0], col);
            currentRegion.tileRangeX[1] = (int) Math.max(currentRegion.tileRangeX[1], col);

            Region fov = new Region((int) x, (int) y, (int) w, (int) h);
            currentRegion.fovs.put(row + "-" + col, fov);
          }

          PreparedStatement zQuery = conn.prepareStatement(
            "SELECT DISTINCT is_zstack,z_position FROM images WHERE region=? ORDER BY is_zstack,z_position"
          );
          zQuery.setInt(1, currentRegion.id);

          ResultSet zs = zQuery.executeQuery();
          ArrayList<Integer> tmpZ = new ArrayList<Integer>();
          while (zs.next()) {
            boolean isZ = zs.getBoolean(1);
            int zPos = zs.getInt(2);

            tmpZ.add(zPos);
          }
          currentRegion.zSteps = tmpZ.toArray(new Integer[tmpZ.size()]);
          currentRegion.fullResolutionCoreIndex = core.size();

          if (currentRegion.resolutions.size() > 0) {
            continue;
          }

          int xTiles = currentRegion.tileRangeX[1] - currentRegion.tileRangeX[0] + 1;
          int yTiles = currentRegion.tileRangeY[1] - currentRegion.tileRangeY[0] + 1;
          m.sizeX = xTiles * (currentRegion.tileSizeX - currentRegion.overlapX);
          m.sizeY = yTiles * (currentRegion.tileSizeY - currentRegion.overlapY);

          PreparedStatement maxLevelQuery = conn.prepareStatement(
            "SELECT level FROM images WHERE region=? ORDER BY level DESC"
          );
          maxLevelQuery.setInt(1, currentRegion.id);
          ResultSet maxLevel = maxLevelQuery.executeQuery();
          int max = 0;
          int min = Integer.MAX_VALUE;
          while (maxLevel.next()) {
            int level = maxLevel.getInt(1);
            if (max == 0) {
              max = level;
            }
            if (level >= m.resolutionCount) {
              m.resolutionCount = level + 1;
            }
            min = level;
          }
          for (int r=min; r<=max; r++) {
            currentRegion.resolutions.add(r);
          }

          // older TissueFAXS data stored correction images differently,
          // and did not include the 'channel_zstack' table
          // catch any exceptions with the correction image queries separately,
          // so that the rest of the data can be read even if the correction
          // image can't be found
          try {
            PreparedStatement correctionQuery = conn.prepareStatement(
              "SELECT correction_images.id, channel_zstack.channel_id, channel_zstack.position " +
              "FROM correction_images JOIN channel_zstack ON correction_images.id = channel_zstack.cor_img_id "+
              "WHERE channel_zstack.region_id=?"
            );
            correctionQuery.setInt(1, currentRegion.id);
            ResultSet correctionImgs = correctionQuery.executeQuery();
            while (correctionImgs.next()) {
              int correctionID = correctionImgs.getInt(1);
              int channel = correctionImgs.getInt(2);
              int z = correctionImgs.getInt(3);

              currentRegion.correctionImageCoreIndex = currentRegion.fullResolutionCoreIndex + m.resolutionCount;
              currentRegion.correctionImageIDs.put((channel - 1) + "-" + (z - 1), correctionID);
            }
            correctionQuery = conn.prepareStatement(
              "SELECT correction_images.id, channels.id " +
              "FROM correction_images JOIN channels ON correction_images.id = channels.cor_img_id "+
              "WHERE channels.region_id=?"
            );
            correctionQuery.setInt(1, currentRegion.id);
            correctionImgs = correctionQuery.executeQuery();
            while (correctionImgs.next()) {
              int correctionID = correctionImgs.getInt(1);
              int channelID = correctionImgs.getInt(2);
              currentRegion.correctionImageCoreIndex = currentRegion.fullResolutionCoreIndex + m.resolutionCount;
              currentRegion.correctionImageIDs.put((channelID - 1) + "-0", correctionID);
            }
          }
          catch (SQLException e) {
            LOGGER.warn("Failed to find correction image", e);
          }
        }

        PreparedStatement channelQuery = conn.prepareStatement(
          "SELECT DISTINCT id, name, color, save_16bit, excitation_wavelength, emission_wavelength FROM channels ORDER BY id"
        );
        ResultSet channels = channelQuery.executeQuery();
        while (channels.next()) {
          m.sizeC++;

          Channel ch = new Channel();
          ch.id = channels.getInt(1);
          ch.name = channels.getString(2);
          ch.color = channels.getInt(3);

          boolean save16 = channels.getBoolean(4);
          if (save16) {
            m.pixelType = FormatTools.UINT16;
          }

          ch.exWave = channels.getInt(5);
          ch.emWave = channels.getInt(6);

          regions.get(startRegionIndex).channels.add(ch);
        }
      }
      catch (SQLException e) {
        LOGGER.warn("Failed to initialize", e);
      }
      finally {
        try {
          conn.close();
        }
        catch (SQLException e) {
          LOGGER.warn("Failed to close connection", e);
        }
      }

      m.sizeZ = regions.get(startRegionIndex).zSteps.length;

      for (int r=startRegionIndex; r<regions.size(); r++) {
        m.sizeT = (int) Math.max(m.sizeT, regions.get(r).timepoint + 1);
      }

      m.imageCount = m.sizeZ * m.sizeC * m.sizeT;

      // TODO: bad assumption in general?
      if (m.sizeC == 1 && m.pixelType == FormatTools.UINT8) {
        m.sizeC = 3;
        m.rgb = true;
        m.interleaved = true;
      }

      m.dimensionOrder = "XYCZT";
      m.littleEndian = true;

      core.add(m);
      ScanRegion start = regions.get(startRegionIndex);
      for (int r=1; r<m.resolutionCount; r++) {
        CoreMetadata res = new CoreMetadata(m);
        int scale = (int) Math.pow(start.scaleFactor, r);
        res.sizeX /= scale;
        res.sizeY /= scale;
        res.resolutionCount = 1;
        core.add(res);
      }

      if (start.correctionImageCoreIndex != null) {
        CoreMetadata correction = new CoreMetadata(m);
        correction.sizeX = start.tileSizeX;
        correction.sizeY = start.tileSizeY;
        correction.pixelType = FormatTools.FLOAT;
        correction.resolutionCount = 1;
        correction.littleEndian = true;

        core.add(correction);
      }
    }

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    String instrument = MetadataTools.createLSID("Instrument", 0);
    store.setInstrumentID(instrument, 0);

    ArrayList<Integer> populatedCoreIndexes = new ArrayList<Integer>();
    int nextImage = 0;
    for (int i=0, index=0; i<regions.size(); index++) {
      ScanRegion region = regions.get(i);

      // skip over remaining TMA regions
      if (populatedCoreIndexes.contains(region.fullResolutionCoreIndex)) {
        i++;
        index--;
        continue;
      }
      populatedCoreIndexes.add(region.fullResolutionCoreIndex);
      int imageIndex = hasFlattenedResolutions() ? region.fullResolutionCoreIndex : nextImage;
      nextImage++;

      String objectiveID = MetadataTools.createLSID("Objective", 0, index);
      store.setObjectiveID(objectiveID, 0, index);

      Double lensNA = region.regionMetadata.getDouble("ObjectiveLensNA");
      store.setObjectiveLensNA(lensNA, 0, index);

      String immersion = region.regionMetadata.getString("ObjectiveImmersion");
      store.setObjectiveImmersion(getImmersion(immersion), 0, index);

      Double magnification = region.regionMetadata.getDouble("ObjectiveNominalMagnification");
      store.setObjectiveNominalMagnification(magnification, 0, index);

      String objectiveName = region.regionMetadata.getString("ObjectiveName");
      store.setObjectiveModel(objectiveName, 0, index);

      store.setImageName(region.regionMetadata.getString("Name"), imageIndex);
      store.setObjectiveSettingsID(objectiveID, imageIndex);

      Double physicalX = region.regionMetadata.getDouble("PhysicalSizeX");
      Double physicalY = region.regionMetadata.getDouble("PhysicalSizeY");

      store.setPixelsPhysicalSizeX(FormatTools.getPhysicalSizeX(physicalX), imageIndex);
      store.setPixelsPhysicalSizeY(FormatTools.getPhysicalSizeY(physicalY), imageIndex);

      String acquisitionMode = region.regionMetadata.getString("AcquisitionMode");
      AcquisitionMode mode = getAcquisitionMode(acquisitionMode);

      for (int c=0; c<region.channels.size(); c++) {
        Channel ch = region.channels.get(c);
        store.setChannelName(ch.name, imageIndex, c);

        if (mode != null) {
          store.setChannelAcquisitionMode(mode, imageIndex, c);
        }

        if (ch.emWave >= WAVE_MIN && ch.emWave <= WAVE_MAX) {
          store.setChannelEmissionWavelength(FormatTools.getWavelength((double) ch.emWave), imageIndex, c);
        }
        if (ch.exWave >= WAVE_MIN && ch.exWave <= WAVE_MAX) {
          store.setChannelExcitationWavelength(FormatTools.getWavelength((double) ch.exWave), imageIndex, c);
        }
        // don't set a channel color for brightfield data
        // the channel color is expected to be white in that case
        if (!core.get(region.fullResolutionCoreIndex).rgb) {
          store.setChannelColor(ch.getColor(), imageIndex, c);
        }
      }

      if (region.correctionImageCoreIndex != null) {
        int corrImage = hasFlattenedResolutions() ? region.correctionImageCoreIndex : nextImage;
        nextImage++;

        store.setImageName(region.regionMetadata.getString("Name") + " Correction Image", corrImage);
        store.setObjectiveSettingsID(objectiveID, corrImage);
      }

      i += core.get(region.fullResolutionCoreIndex).sizeT;
    }
  }

  // -- Helper methods --

  /**
   * If a .tfcyto file was provided, then that is the only file that should be read.
   * If an .aqproj file was provided, look for all associated .tfcyto files.
   */
  private void findDBFiles() {
    if (checkSuffix(getCurrentFile(), "tfcyto")) {
      pixelsFiles.add(new Location(getCurrentFile()).getAbsolutePath());
      return;
    }

    Location dir = new Location(getCurrentFile()).getAbsoluteFile().getParentFile();
    String[] list = dir.list(true);
    Arrays.sort(list);
    for (String f : list) {
      Location slide = new Location(dir, f);
      if (f.startsWith("Slide ") && slide.isDirectory()) {
        String[] dbFiles = slide.list(true);
        Arrays.sort(dbFiles);
        for (String db : dbFiles) {
          if (checkSuffix(db, "tfcyto")) {
            pixelsFiles.add(new Location(slide, db).getAbsolutePath());
          }
        }
      }
    }
  }

  private List<ScanRegion> getAllRegions(int t) throws FormatException {
    ArrayList<ScanRegion> planeRegions = new ArrayList<ScanRegion>();
    int index = getCoreIndex();
    for (int i=regions.size()-1; i>=0; i--) {
      ScanRegion r = regions.get(i);
      if (r.timepoint == t && r.correctionImageCoreIndex != null &&
        r.correctionImageCoreIndex == index)
      {
        planeRegions.add(r);
        continue;
     }
      if (r.timepoint == t && r.fullResolutionCoreIndex <= index) {
        int res = index - r.fullResolutionCoreIndex;
        if (r.resolutions.contains(res)) {
          planeRegions.add(r);
        }
      }
    }
    return planeRegions;
  }

  private ScanRegion getRegion() throws FormatException {
    return getRegion(0);
  }

  private ScanRegion getRegion(int t) throws FormatException {
    int index = getCoreIndex();
    for (int i=regions.size()-1; i>=0; i--) {
      ScanRegion r = regions.get(i);
      if (r.timepoint == t && r.correctionImageCoreIndex != null &&
        r.correctionImageCoreIndex == index)
      {
        return r;
      }
      if (r.timepoint == t && r.fullResolutionCoreIndex <= index) {
        int res = index - r.fullResolutionCoreIndex;
        if (r.resolutions.contains(res)) {
          return r;
        }
      }
    }
    throw new FormatException("Could not find ScanRegion (core index " + index + ", t=" + t + ")");
  }

  private boolean isCorrectionImage(ScanRegion r) {
    if (r.correctionImageCoreIndex == null) {
      return false;
    }
    return getCoreIndex() == r.correctionImageCoreIndex;
  }

  private int getLevel() throws FormatException {
    if (hasFlattenedResolutions()) {
      ScanRegion region = getRegion();
      return getCoreIndex() - region.fullResolutionCoreIndex;
    }
    return getResolution();
  }

  private Codec getCodec(int compression) throws UnsupportedCompressionException {
    switch (compression) {
      case 0:
      case 1:
        return new PassthroughCodec();
      case 6:
        return new JPEGXRCodec();
      case 7:
        return new JPEGCodec();
      default:
        throw new UnsupportedCompressionException("Unsupported compression: " + compression);
    }
  }

  private CodecOptions getCodecOptions(ScanRegion region) {
    CodecOptions options = new CodecOptions();
    options.bitsPerSample = FormatTools.getBytesPerPixel(getPixelType()) * 8;
    options.width = region.tileSizeX;
    options.height = region.tileSizeY;
    options.interleaved = isInterleaved();
    options.littleEndian = isLittleEndian();
    return options;
  }

  /**
   * The largest resolution stores one field of view (FOV) per tile.
   * Each sub-resolution has the same tile size as the largest resolution,
   * so the number of FOVs stored in one tile increases as the resolutions
   * get smaller.
   *
   * For example, with a tile size of 2048x2048 and a downsample factor of 4,
   * resolution 1 will have 16 (4x4) FOVs per tile, with each FOV being 512x512.
   *
   * However, none of the FOV positions or overlaps are taken into account.
   * So the first step in assembling a sub-resolution is to split each tile
   * into its component FOVs. Then each FOV can be repositioned according to
   * its associated stitching rectangle and overlap.
   *
   * This method only splits the given tile into component FOVs.
   * This is not necessary for the largest resolution, only sub-resolutions.
   */
  private byte[][] splitFOVs(ScanRegion region, byte[] tile, int scale) {
    byte[][] fovs = new byte[scale * scale][];
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int channels = getRGBChannelCount();
    int pixel = bpp * channels;

    int srcWidth = region.tileSizeX * pixel;
    int destWidth = (region.tileSizeX / scale) * pixel;
    int srcHeight = region.tileSizeY;
    int destHeight = region.tileSizeY / scale;

    for (int fov=0; fov<fovs.length; fov++) {
      int fovRow = fov / scale;
      int fovCol = fov % scale;

      fovs[fov] = new byte[destWidth * destHeight * pixel];

      for (int row=0; row<destHeight; row++) {
        int srcOffset = (((fovRow * destHeight) + row) * srcWidth) + (fovCol * destWidth);
        int destOffset = row * destWidth;

        System.arraycopy(tile, srcOffset, fovs[fov], destOffset, destWidth);
      }
    }

    return fovs;
  }

  private void copyRegionToBuffer(ScanRegion region, int level, Region dest, int[] zct, byte[] buf)
    throws FormatException, IOException
  {
    int scale = (int) Math.pow(region.scaleFactor, level);

    int scaledOverlapX = region.overlapX / scale;
    int scaledOverlapY = region.overlapY / scale;

    Connection conn = openConnection(region.file);
    try {
      PreparedStatement tiles = conn.prepareStatement(
        "SELECT row, column FROM images WHERE region=? AND level=? AND " +
        "channel=? AND is_zstack=? AND z_position=? ORDER BY row,column"
      );

      tiles.setInt(1, region.id);
      tiles.setInt(2, level);
      tiles.setInt(3, zct[1] + 1);
      tiles.setInt(4, zct[0] > 0 ? 1 : 0);
      tiles.setInt(5, region.zSteps[zct[0]]);

      CodecOptions options = getCodecOptions(region);

      ResultSet subsetTiles = tiles.executeQuery();
      while (subsetTiles.next()) {
        int row = subsetTiles.getInt(1);
        int column = subsetTiles.getInt(2);

        int regionRow = row;
        int regionColumn = column;
        if (region.tmaX != null) {
          regionColumn += (region.tmaX * region.scaleFactor);
        }
        if (region.tmaY != null) {
          regionRow += (region.tmaY * region.scaleFactor);
        }

        int relativeColumn = regionColumn - (int) Math.floor((double) region.tileRangeX[0] / scale);
        int relativeRow = regionRow - (int) Math.floor((double) region.tileRangeY[0] / scale);

        int pixelColumn = relativeColumn * options.width;
        int pixelRow = relativeRow * options.height;

        // overlap handling
        Region[] fovPositions = new Region[scale * scale];
        if (level == 0) {
          Region fov = region.fovs.get(row + "-" + column);
          pixelRow -= fov.y;
          pixelColumn -= fov.x;

          if (region.tmaX == null || region.tmaY == null) {
            pixelRow -= (relativeRow * region.overlapY);
            pixelColumn -= (relativeColumn * region.overlapX);
          }
          else {
            pixelRow -= (regionRow * region.overlapY);
            pixelColumn -= (regionColumn * region.overlapX);
          }

          fovPositions[0] = new Region(pixelColumn, pixelRow, options.width, options.height);
        }
        else {
          for (int f=0; f<fovPositions.length; f++) {
            // row and column are relative to the current resolution level
            // need to convert to row and column indexes relative to the full resolution (level 0)
            int fovRow = row*scale + (f / scale);
            int fovColumn = column*scale + (f % scale);
            Region fov = region.fovs.get(fovRow + "-" + fovColumn);
            if (fov != null) {
              // correct the row and column indexes so that the first acquired row/column
              // relative to the full resolution lines up with (0,0) in the current resolution
              // failing to correct for the first acquired row/column means a black border
              // will appear at the top and/or left of the current resolutin image
              int relativeFOVRow = fovRow - region.tileRangeY[0];
              int relativeFOVColumn = fovColumn - region.tileRangeX[0];
              int xx = (relativeFOVColumn * options.width / scale) - (fov.x / scale) - (relativeFOVColumn * scaledOverlapX);
              int yy = (relativeFOVRow * options.height / scale) - (fov.y / scale) - (relativeFOVRow * scaledOverlapY);
              fovPositions[f] = new Region(xx, yy, options.width / scale, options.height / scale);
            }
          }
        }

        byte[][] fovs = null;
        for (int f=0; f<fovPositions.length; f++) {
          if (fovPositions[f] == null) {
            continue;
          }
          Region intersection = fovPositions[f].intersection(dest);

          if (intersection.width > 0 && intersection.height > 0) {
            // if we actually want to copy data from this FOV, fetch and decompress it
            if (fovs == null) {
              PreparedStatement readTile = conn.prepareStatement(
                "SELECT data, compression FROM images WHERE region=? AND level=? AND " +
                "channel=? AND is_zstack=? AND z_position=? AND row=? AND column=?"
              );

              readTile.setInt(1, region.id);
              readTile.setInt(2, level);
              readTile.setInt(3, zct[1] + 1);
              readTile.setInt(4, zct[0] > 0 ? 1 : 0);
              readTile.setInt(5, region.zSteps[zct[0]]);
              readTile.setInt(6, row);
              readTile.setInt(7, column);

              ResultSet resultTile = readTile.executeQuery();
              if (resultTile.next()) {
                byte[] data = resultTile.getBytes(1);
                int compression = resultTile.getInt(2);

                Codec codec = getCodec(compression);
                byte[] tile = codec.decompress(data, options);
                tile = applyTransformation(tile, region.id, level, zct[1] + 1, region.zSteps[0], row, column);
                if (level == 0) {
                  fovs = new byte[1][];
                  fovs[0] = tile;
                }
                else {
                  fovs = splitFOVs(region, tile, scale);
                }
              }
              else {
                throw new FormatException(
                  "Could not get tile for row=" + row + ", column=" + column);
              }
            }

            copyRegion(fovPositions[f], fovs[f], dest, buf, region.tileSizeX / scale);
          }
        }
      }
    }
    catch (SQLException e) {
      LOGGER.warn("Failed to query tiles", e);
    }
    finally {
      try {
        conn.close();
      }
      catch (SQLException e) {
        LOGGER.warn("Failed to close connection", e);
      }
    }
  }

  private void copyRegion(Region srcRegion, byte[] src, Region destRegion, byte[] dest, int tileWidth) {
    Region intersection = srcRegion.intersection(destRegion);
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int pixel = bpp * getRGBChannelCount();
    int outputRowLen = destRegion.width * pixel;
    int intersectionX = (int) Math.max(0, destRegion.x - srcRegion.x);
    int rowLen = pixel * (int) Math.min(intersection.width, srcRegion.width);

    int outputRow = intersection.y - destRegion.y;
    int outputCol = intersection.x - destRegion.x;
    int outputOffset = outputRow * outputRowLen + outputCol * pixel;

    for (int copyRow=0; copyRow<intersection.height; copyRow++) {
      int realRow = copyRow + intersection.y - srcRegion.y;
      int inputOffset = pixel * (realRow * tileWidth + intersectionX);
      System.arraycopy(src, inputOffset,
        dest, outputOffset + copyRow*outputRowLen, rowLen);
    }
  }

  private void copyCorrectionImageToBuffer(ScanRegion region, Region dest, int[] zct, byte[] buf)
    throws FormatException, IOException
  {
    if (region.timepoint != zct[2]) {
      return;
    }

    Connection conn = openConnection(region.file);
    try {
      PreparedStatement correctionQuery = conn.prepareStatement(
        "SELECT compression, data FROM correction_images WHERE id=?"
      );
      Integer correctionID = region.correctionImageIDs.get(zct[1] + "-" + zct[0]);
      if (correctionID == null) {
        correctionID = region.correctionImageIDs.get(zct[1] + "-0");
      }
      if (correctionID == null) {
        return;
      }
      correctionQuery.setInt(1, correctionID);
      ResultSet correctionImgs = correctionQuery.executeQuery();
      if (correctionImgs.next()) {
        int compression = correctionImgs.getInt(1);
        byte[] data = correctionImgs.getBytes(2);

        CodecOptions options = getCodecOptions(region);

        data = getCodec(compression).decompress(data, options);
        int bpp = FormatTools.getBytesPerPixel(getPixelType());

        // found 4 channels, need to remove extras to match channel count
        if (data.length == options.width * options.height * bpp * 4) {
          int srcStride = bpp * 4;
          int destStride = bpp * getRGBChannelCount();
          byte[] tmp = new byte[options.width * options.height * destStride];

          if (isInterleaved()) {
            for (int pix=0; pix<options.width*options.height; pix++) {
              System.arraycopy(data, pix * srcStride, tmp, pix * destStride, destStride);
            }
          }
          else {
            System.arraycopy(data, 0, tmp, 0, tmp.length);
          }
          data = tmp;
        }

        Region correction = new Region(0, 0, options.width, options.height);

        copyRegion(correction, data, dest, buf, options.width);
      }
    }
    catch (SQLException e) {
      LOGGER.warn("Failed to query tiles", e);
    }
    finally {
      try {
        conn.close();
      }
      catch (SQLException e) {
        LOGGER.warn("Failed to close connection", e);
      }
    }
  }

  private Connection openConnection(String file) throws IOException {
    Connection conn = null;
    try {
      // see https://github.com/xerial/sqlite-jdbc/issues/247
      SQLiteConfig config = new SQLiteConfig();
      config.setReadOnly(true);
      conn = config.createConnection("jdbc:sqlite:" +
        new Location(file).getAbsolutePath());
    }
    catch (SQLException e) {
      LOGGER.warn("Could not read from database");
      throw new IOException(e);
    }
    return conn;
  }

  class ScanRegion {
    public String file;
    public JSONObject regionMetadata;
    public int id;

    // record the core index of the full resolution for the pyramid
    // to which this region belongs
    // separately, record the indexes of the resolutions in the pyramid
    // for which this region applies
    //
    // for most data types, the resolutions list will include every resolution
    // in the pyramid; there will be one region per timepoint
    //
    // for each TMA pyramid, though, expect one region whose resolutions list
    // includes everything except resolution 0 (full resolution),
    // and one or more regions with the same fullResolutionCoreIndex
    // whose resolutions list contains only 0
    public int fullResolutionCoreIndex;
    public Integer correctionImageCoreIndex = null;
    public List<Integer> resolutions = new ArrayList<Integer>();

    public int tileSizeX;
    public int tileSizeY;
    public int overlapX;
    public int overlapY;
    public int scaleFactor;
    public int[] tileRangeX = new int[] {Integer.MAX_VALUE, 0};
    public int[] tileRangeY = new int[] {Integer.MAX_VALUE, 0};
    public Integer[] zSteps;
    public int timepoint;
    public List<Channel> channels = new ArrayList<Channel>();
    public HashMap<String, Region> fovs = new HashMap<String, Region>();
    public HashMap<String, Integer> correctionImageIDs = new HashMap<String, Integer>();

    public Integer tmaX;
    public Integer tmaY;

    public void parseJSON() {
      // "Rows" and "Columns" in the JSON metadata here
      // reflect the canvas size, not the area (FOVs) actually acquired
      tileSizeX = regionMetadata.getInt("ImageWidth");
      tileSizeY = regionMetadata.getInt("ImageHeight");
      overlapX = regionMetadata.getInt("OverlapWidth");
      overlapY = regionMetadata.getInt("OverlapHeight");
      scaleFactor = regionMetadata.getInt("CacheStep");

      if (regionMetadata.has("LocationOnTMABlockX")) {
        tmaX = regionMetadata.getInt("LocationOnTMABlockX");
      }
      if (regionMetadata.has("LocationOnTMABlockY")) {
        tmaY = regionMetadata.getInt("LocationOnTMABlockY");
      }
    }

  }

  class Channel {
    public int id;
    public String name;
    public int color;
    public int exWave;
    public int emWave;

    /** Color returned by DB is ARGB, OME model is RGBA. */
    public Color getColor() {
      int alpha = (color >> 24) & 0xff;
      int red = (color >> 16) & 0xff;
      int green = (color >> 8) & 0xff;
      int blue = color & 0xff;
      return new Color(red, green, blue, alpha);
    }
  }

  /**
   * Extension point for doing something to an FOV immediately
   * after decompression, before any stitching or cropping.
   * Overriding this in a subclass could be used to apply a correction image.
   * Currently this method is a no-op.
   */
  public byte[] applyTransformation(byte[] tile, int regionID, int level,
    int channel, int zIndex, int fovRow, int fovColumn)
  {
    return tile;
  }

}
