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

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import loci.common.DataTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.xml.BaseHandler;
import loci.common.xml.XMLTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.codec.ZlibCodec;
import loci.formats.meta.MetadataStore;

import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.xml.model.MapPair;
import ome.xml.model.primitives.Color;
import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.NonNegativeLong;
import ome.xml.model.primitives.PercentFraction;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;
import ome.xml.model.enums.AcquisitionMode;
import ome.xml.model.enums.Compression;
import ome.xml.model.enums.ContrastMethod;
import ome.xml.model.enums.IlluminationType;
import ome.xml.model.enums.NamingConvention;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;

/**
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class CV7000Reader extends FormatReader {

  // ###############
  // ## Module 1: Constants, Options, And Reader State
  // ###############

  public static final String DUPLICATE_PLANES_KEY = "cv7000.duplicate_missing_planes";
  public static final boolean DUPLICATE_PLANES_DEFAULT = false;
  public static final String PRESERVE_RAW_SIDECARS_KEY = "cv7000.preserve_raw_sidecars";
  public static final boolean PRESERVE_RAW_SIDECARS_DEFAULT = true;
  public static final String COMPRESS_RAW_SIDECARS_KEY =
    "cv7000.compress_raw_sidecars";
  public static final boolean COMPRESS_RAW_SIDECARS_DEFAULT = true;
  public static final String MAX_PROVENANCE_RECORDS_KEY =
    "cv7000.max_provenance_records";
  public static final int MAX_PROVENANCE_RECORDS_DEFAULT = 500;
  public static final String INFER_OBJECTIVE_LENS_NA_KEY =
    "cv7000.infer_objective_lens_na";
  public static final boolean INFER_OBJECTIVE_LENS_NA_DEFAULT = false;

  private static final Logger LOGGER = LoggerFactory.getLogger(CV7000Reader.class);
  private static final CV7000ChannelMapper CHANNEL_MAPPER =
    new CV7000ChannelMapper();

  private static final String MEASUREMENT_FILE = "MeasurementData.mlf";
  private static final String MEASUREMENT_DETAIL = "MeasurementDetail.mrf";
  private static final String POST_PROCESS = "PostProcess.ppf";
  private static final String OTF_CROSSTALK_PARAMETER = "OTF_crosstalk_parameter.xml";
  private static final String OTF_GEOMETRY_PARAMETER = "OTF_geometry_parameter.xml";
  private static final String BRIGHTFIELD = "Brightfield";
  private static final String RAW_SIDECAR_ANNOTATION_NAMESPACE =
    "openmicroscopy.org/OriginalMetadata/Yokogawa/CV7000/RawSidecar";
  private static final String YOKOGAWA_ANNOTATION_NAMESPACE =
    "openmicroscopy.org/OriginalMetadata/Yokogawa/CV7000";
  private static final String XML_MIME_TYPE = "application/xml";
  private static final String CV7000_DIMENSION_ORDER = "XYZCT";
  private static final int CHANNEL_NOT_FOUND = -1;
  // Manual objective table plus observed CV7000 objective labels in OTF geometry. These correctly resolve known objectives in local data and available data from public repositories.
  private static final KnownObjectiveSpec[] KNOWN_OBJECTIVES =
    new KnownObjectiveSpec[] {
      new KnownObjectiveSpec(4, false, false, "Air", 0.16),
      new KnownObjectiveSpec(10, false, false, "Air", 0.40),
      new KnownObjectiveSpec(20, false, false, "Air", 0.75),
      new KnownObjectiveSpec(40, false, false, "Air", 0.95),
      new KnownObjectiveSpec(60, false, false, "Water", 1.2),
      new KnownObjectiveSpec(20, false, true, "Air", 0.45),
      new KnownObjectiveSpec(10, true, false, "Air", 0.30),
      new KnownObjectiveSpec(20, true, false, "Air", 0.45),
      new KnownObjectiveSpec(20, true, true, "Air", 0.45)
    };

  // -- Fields --

  private LinkedHashSet<String> allFiles = new LinkedHashSet<String>();
  private LinkedHashSet<String> planeFiles = new LinkedHashSet<String>();
  private LinkedHashSet<String> correctionFiles = new LinkedHashSet<String>();
  private MinimalTiffReader reader;
  private String wppPath;
  private String detailPath;
  private String measurementPath;
  private String settingsPath;
  private CV7000RawModel rawModel = new CV7000RawModel();
  private CV7000SeriesLayout seriesLayout;
  private CV7000DatasetPaths datasetPaths;
  private ArrayList<Plane> planeData;
  private int[][] reversePlaneLookup;
  private ArrayList<LightSource> lightSources;
  private ArrayList<Channel> channels;
  private String startTime, endTime;
  private String measurementOperatorName;
  private String targetSystem;
  private ArrayList<String> extraFiles;
  private MeasurementDataSummary measurementDataSummary;
  private CrosstalkParameters crosstalkParameters;
  private OTFGeometryParameters otfGeometryParameters;
  private YokogawaParsing parsing = new YokogawaParsing();

  /** Constructs a new Yokogawa CV7000 reader. */
  public CV7000Reader() {
    super("Yokogawa CV7000", new String[] {"wpi"});
    hasCompanionFiles = true;
    domains = new String[] {FormatTools.HCS_DOMAIN};
    datasetDescription = "Directory with XML files and one .tif/.tiff file per plane";
  }

  // ###############
  // ## Module 2: Public Reader API And Pixel Access
  // ###############

  public boolean duplicatePlanes() {
    MetadataOptions options = getMetadataOptions();
    if (options instanceof DynamicMetadataOptions) {
      return ((DynamicMetadataOptions) options).getBoolean(
       DUPLICATE_PLANES_KEY, DUPLICATE_PLANES_DEFAULT);
    }
    return DUPLICATE_PLANES_DEFAULT;
  }

  public boolean preserveRawSidecars() {
    MetadataOptions options = getMetadataOptions();
    if (options instanceof DynamicMetadataOptions) {
      return ((DynamicMetadataOptions) options).getBoolean(
       PRESERVE_RAW_SIDECARS_KEY, PRESERVE_RAW_SIDECARS_DEFAULT);
    }
    return PRESERVE_RAW_SIDECARS_DEFAULT;
  }

  public boolean compressRawSidecars() {
    MetadataOptions options = getMetadataOptions();
    if (options instanceof DynamicMetadataOptions) {
      return ((DynamicMetadataOptions) options).getBoolean(
       COMPRESS_RAW_SIDECARS_KEY, COMPRESS_RAW_SIDECARS_DEFAULT);
    }
    return COMPRESS_RAW_SIDECARS_DEFAULT;
  }

  public int maxProvenanceRecords() {
    MetadataOptions options = getMetadataOptions();
    if (options instanceof DynamicMetadataOptions) {
      try {
        Integer value = ((DynamicMetadataOptions) options).getInteger(
          MAX_PROVENANCE_RECORDS_KEY, MAX_PROVENANCE_RECORDS_DEFAULT);
        if (value != null && value.intValue() >= 0) {
          return value.intValue();
        }
      }
      catch (RuntimeException e) {
        LOGGER.warn("Invalid {} value; using default {}",
          MAX_PROVENANCE_RECORDS_KEY, MAX_PROVENANCE_RECORDS_DEFAULT);
        return MAX_PROVENANCE_RECORDS_DEFAULT;
      }
      LOGGER.warn("Negative {} value; using default {}",
        MAX_PROVENANCE_RECORDS_KEY, MAX_PROVENANCE_RECORDS_DEFAULT);
    }
    return MAX_PROVENANCE_RECORDS_DEFAULT;
  }

  public boolean inferObjectiveLensNA() {
    MetadataOptions options = getMetadataOptions();
    if (options instanceof DynamicMetadataOptions) {
      return ((DynamicMetadataOptions) options).getBoolean(
       INFER_OBJECTIVE_LENS_NA_KEY, INFER_OBJECTIVE_LENS_NA_DEFAULT);
    }
    return INFER_OBJECTIVE_LENS_NA_DEFAULT;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getRequiredDirectories(String[]) */
  @Override
  public int getRequiredDirectories(String[] files)
    throws FormatException, IOException
  {
    return 1;
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
    LinkedHashSet<String> files = new LinkedHashSet<String>();
    files.add(new Location(currentId).getAbsolutePath());
    for (String file : allFiles) {
      if (file != null &&
        (!noPixels || !isPixelFile(classifyCV7000File(new Location(file)))))
      {
        files.add(file);
      }
    }
    return files.toArray(new String[files.size()]);
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);

    HashSet<String> files = new HashSet<String>();
    files.add(new Location(currentId).getAbsolutePath());
    files.add(measurementPath);
    if (detailPath != null) {
      files.add(detailPath);
    }
    if (settingsPath != null) {
      files.add(settingsPath);
    }
    if (wppPath != null) {
      files.add(wppPath);
    }
    if (!noPixels && planeData != null) {
      for (int index : reversePlaneLookup[getSeries()]) {
        if (index < 0) {
          continue;
        }
        Plane p = planeData.get(index);
        if (p != null && p.file != null) {
          files.add(p.file);
        }
      }
    }
    if (!noPixels && channels != null) {
      for (Channel c : channels) {
        if (c != null && c.resolvedCorrectionFile != null &&
          new Location(c.resolvedCorrectionFile).exists())
        {
          files.add(c.resolvedCorrectionFile);
        }
      }
    }
    if (extraFiles != null) {
      for (String file : extraFiles) {
        if (!noPixels ||
          !isPixelFile(classifyCV7000File(new Location(file))))
        {
          files.add(file);
        }
      }
    }
    for (String file : allFiles) {
      Location location = file == null ? null : new Location(file);
      if (location != null && !isPixelFile(classifyCV7000File(location)) &&
        !location.isDirectory())
      {
        files.add(file);
      }
    }
    String[] allFiles = files.toArray(new String[files.size()]);
    Arrays.sort(allFiles);
    return allFiles;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      if (reader != null) {
        reader.close();
      }
      reader = null;
      measurementPath = null;
      detailPath = null;
      wppPath = null;
      settingsPath = null;
      datasetPaths = null;
      planeData = null;
      lightSources = null;
      channels = null;
      startTime = null;
      endTime = null;
      measurementOperatorName = null;
      targetSystem = null;
      measurementDataSummary = null;
      crosstalkParameters = null;
      otfGeometryParameters = null;
      reversePlaneLookup = null;
      extraFiles = null;
      seriesLayout = null;
      rawModel = new CV7000RawModel();
      if (allFiles != null) {
          allFiles.clear();
      } else {
          allFiles = new LinkedHashSet<String>();
      }
      planeFiles.clear();
      correctionFiles.clear();
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

    Arrays.fill(buf, getFillColor());
    Plane p = lookupPlane(getSeries(), no);
    LOGGER.trace("series = {}, no = {}, file = {}", series, no, p == null ? null : p.file);
    if (p != null && p.file != null) {
      reader.setId(p.file);
      return reader.openBytes(0, buf, x, y, w, h);
    }
    else if (duplicatePlanes()) {
      Plane duplicate = getDuplicatePlane(getSeries(), no);
      if (duplicate != null && duplicate.file != null) {
        reader.setId(duplicate.file);
        return reader.openBytes(0, buf, x, y, w, h);
      }
    }
    return buf;
  }

  // ###############
  // ## Module 3: Dataset Path Resolution And Sidecar Parsing
  // ###############

  /* @see loci.formats.FormatReader#getAvailableOptions() */
  @Override
  protected ArrayList<String> getAvailableOptions() {
    ArrayList<String> optionsList = super.getAvailableOptions();
    optionsList.add(DUPLICATE_PLANES_KEY);
    optionsList.add(PRESERVE_RAW_SIDECARS_KEY);
    optionsList.add(COMPRESS_RAW_SIDECARS_KEY);
    optionsList.add(MAX_PROVENANCE_RECORDS_KEY);
    optionsList.add(INFER_OBJECTIVE_LENS_NA_KEY);
    return optionsList;
  }

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    resetParsedState();
    CV7000DatasetPaths paths = getDatasetPaths(id);
    datasetPaths = paths;
    WPIHandler plate = parsePlate(paths.wpiPath);

    parseMeasurementData(paths);
    parseOptionalSidecars(paths);
    normalizeChannels(paths.parentPath);
    collectDatasetFiles(paths.parentPath);

    CV7000SeriesLayout layout = buildSeriesLayout();
    seriesLayout = layout;
    initializeCoreMetadata(layout);
    populateReversePlaneLookup(layout);
    applyInputBitDepths();
    populateMetadataStore(plate, layout);
    setSeries(0);
  }

  /** Reset parsed CV7000 state before opening a dataset on a reused reader. */
  private void resetParsedState() {
    rawModel = new CV7000RawModel();
    seriesLayout = null;
    datasetPaths = null;
    planeData = null;
    reversePlaneLookup = null;
    lightSources = null;
    channels = null;
    startTime = null;
    endTime = null;
    measurementOperatorName = null;
    targetSystem = null;
    extraFiles = null;
    measurementDataSummary = null;
    crosstalkParameters = null;
    otfGeometryParameters = null;
    wppPath = null;
    detailPath = null;
    measurementPath = null;
    settingsPath = null;
    allFiles.clear();
    planeFiles.clear();
    correctionFiles.clear();
  }

  private CV7000DatasetPaths getDatasetPaths(String id) {
    CV7000DatasetPaths paths = new CV7000DatasetPaths();
    Location wpi = new Location(id).getAbsoluteFile();
    paths.wpiPath = wpi.getAbsolutePath();
    paths.parentPath = wpi.getParentFile().getAbsolutePath();
    paths.measurementDataPath =
      new Location(paths.parentPath, MEASUREMENT_FILE).getAbsolutePath();
    paths.measurementDetailPath =
      new Location(paths.parentPath, MEASUREMENT_DETAIL).getAbsolutePath();
    paths.postProcessPath =
      new Location(paths.parentPath, POST_PROCESS).getAbsolutePath();
    paths.otfCrosstalkPath =
      new Location(paths.parentPath, OTF_CROSSTALK_PARAMETER).getAbsolutePath();
    paths.otfGeometryPath =
      new Location(paths.parentPath, OTF_GEOMETRY_PARAMETER).getAbsolutePath();
    return paths;
  }

  /** Parse the .wpi file, which defines the plate dimensions and identity. */
  private WPIHandler parsePlate(String wpiPath) throws IOException {
    WPIHandler plate = new WPIHandler();
    XMLTools.parseXML(readSanitizedXML(wpiPath), plate);
    return plate;
  }

  /** Keep a dataset-level inventory for getUsedFiles and original metadata. */
  private void collectDatasetFiles(String parentPath) {
    Location parent = new Location(parentPath);
    String[] listedFiles = parent.list(true);
    Arrays.sort(listedFiles);
    for (int i=0; i<listedFiles.length; i++) {
      Location file = new Location(parent, listedFiles[i]);
      if (!file.isDirectory() && file.canRead() &&
        isDatasetFile(classifyCV7000File(file)))
      {
        allFiles.add(file.getAbsolutePath());
      }
    }
  }

  /** Parse the required plane manifest and resolve readable TIFF plane paths. */
  private void parseMeasurementData(CV7000DatasetPaths paths)
    throws FormatException, IOException
  {
    if (!new Location(paths.measurementDataPath).exists()) {
      throw new FormatException("Missing " + MEASUREMENT_FILE + " file");
    }

    measurementPath = paths.measurementDataPath;
    MeasurementDataHandler measurementHandler =
      new MeasurementDataHandler(paths.parentPath);
    XMLTools.parseXML(readSanitizedXML(measurementPath), measurementHandler);
    planeData = measurementHandler.getPlanes();
    measurementDataSummary = measurementHandler.getSummary();
  }

  /** Parse optional XML sidecars that enrich channel, instrument, and raw metadata. */
  private void parseOptionalSidecars(CV7000DatasetPaths paths) throws IOException {
    parseMeasurementDetail(paths);
    parseWellPlateProduct();
    parseMeasurementSettings();
    parseOTFGeometry(paths);
    parseOTFCrosstalk(paths);
  }

  /** MeasurementDetail links the acquisition settings sidecars and seeds channels. */
  private void parseMeasurementDetail(CV7000DatasetPaths paths) throws IOException {
    if (!new Location(paths.measurementDetailPath).exists()) {
      LOGGER.warn("Missing CV7000 measurement detail sidecar {}",
        paths.measurementDetailPath);
      return;
    }

    detailPath = paths.measurementDetailPath;
    MeasurementDetailHandler detailHandler = new MeasurementDetailHandler();
    XMLTools.parseXML(readSanitizedXML(detailPath), detailHandler);
    MeasurementDetailResult result = detailHandler.getResult();
    channels = result.channels;
    startTime = result.startTime;
    endTime = result.endTime;
    measurementOperatorName = result.measurementOperatorName;
    targetSystem = result.targetSystem;
    wppPath = result.wppPath;
    settingsPath = result.settingsPath;
    if (wppPath != null) {
      wppPath = new Location(paths.parentPath, wppPath).getAbsolutePath();
    }
    if (settingsPath != null) {
      settingsPath = new Location(paths.parentPath, settingsPath).getAbsolutePath();
    }
  }

  /** WPP is optional plate-product metadata; missing files preserve legacy behavior. */
  private void parseWellPlateProduct() throws IOException {
    if (wppPath == null) {
      return;
    }
    if (!new Location(wppPath).exists()) {
      LOGGER.warn("Missing referenced CV7000 well plate product sidecar {}",
        wppPath);
      return;
    }
    XMLTools.parseXML(readSanitizedXML(wppPath), new WPPHandler());
  }

  /** Measurement settings attach light sources, actions, filters, and channel modes. */
  private void parseMeasurementSettings() throws IOException {
    if (settingsPath == null) {
      return;
    }
    if (!new Location(settingsPath).exists()) {
      LOGGER.warn("Missing referenced CV7000 measurement settings sidecar {}",
        settingsPath);
      return;
    }
    MeasurementSettingsHandler settingsHandler =
      new MeasurementSettingsHandler(channels);
    String xml = readSanitizedXML(settingsPath);
    if (xml.length() == 0) {
      LOGGER.warn("Referenced CV7000 measurement settings sidecar is empty: {}",
        settingsPath);
      return;
    }
    XMLTools.parseXML(xml, settingsHandler);
    MeasurementSettingsResult result = settingsHandler.getResult();
    lightSources = result.lightSources;
    channels = result.channels;
  }

  /** OTF geometry stores objective catalog entries and affine calibration rows. */
  private void parseOTFGeometry(CV7000DatasetPaths paths) {
    String geometry = paths.otfGeometryPath;
    if (!new Location(geometry).exists()) {
      return;
    }

    try {
      OTFGeometryHandler handler = new OTFGeometryHandler();
      XMLTools.parseXML(readSanitizedXML(geometry), handler);
      otfGeometryParameters = handler.getParameters();
    }
    catch (Exception e) {
      otfGeometryParameters = null;
      LOGGER.warn("Could not parse CV7000 OTF geometry sidecar {}",
        geometry, e);
    }
  }

  /** OTF crosstalk stores exact emission filter bands and optional dichroics. */
  private void parseOTFCrosstalk(CV7000DatasetPaths paths) {
    String crosstalk = paths.otfCrosstalkPath;
    if (!new Location(crosstalk).exists()) {
      return;
    }

    try {
      CrosstalkParameterHandler handler = new CrosstalkParameterHandler();
      XMLTools.parseXML(readSanitizedXML(crosstalk), handler);
      crosstalkParameters = handler.getParameters();
    }
    catch (Exception e) {
      crosstalkParameters = null;
      LOGGER.warn("Could not parse CV7000 crosstalk sidecar {}", crosstalk, e);
    }
  }

  // ###############
  // ## Module 4: Channel Normalization And Channel Lookup
  // ###############

  /** Normalize channel order and paths after all optional channel sources are parsed. */
  private void normalizeChannels(String parentPath) {
    if (channels == null) {
      return;
    }
    sortChannelsByActionThenIndex();
    rawModel.indexChannels(channels);
    resolveCorrectionFiles(parentPath);
  }

  /** Yokogawa logical channels are ordered by action first, then channel index. */
  private void sortChannelsByActionThenIndex() {
    channels.sort(new Comparator<Channel>() {
      @Override
      public int compare(Channel c1, Channel c2) {
        if (c1.actionIndex != c2.actionIndex) {
          return c1.actionIndex - c2.actionIndex;
        }
        return c1.index - c2.index;
      }
    });
  }

  /** Shading correction paths are relative to the dataset directory in the XML. */
  private void resolveCorrectionFiles(String parentPath) {
    for (Channel ch : channels) {
      if (ch.correctionFile != null) {
        String resolved =
          new Location(parentPath, ch.correctionFile).getAbsolutePath();
        ch.resolvedCorrectionFile = resolved;
        ch.correctionFile = getRelativePath(parentPath, resolved);
        Location correction = new Location(resolved);
        if (correction.exists() && correction.canRead() &&
          !correction.isDirectory() && isTiffFile(correction.getName()))
        {
          correctionFiles.add(correction.getAbsolutePath());
        }
      }
    }
  }

  /**
   * Return a path relative to the WPI directory,
   * preserving the input on failure.
   */
  private String getRelativePath(String parent, String path) {
    try {
      Path parentPath =
        Paths.get(parent).toAbsolutePath().normalize();
      Path filePath = Paths.get(path).toAbsolutePath().normalize();
      return parentPath.relativize(filePath).toString();
    }
    catch (InvalidPathException e) {
      return path;
    }
    catch (IllegalArgumentException e) {
      return path;
    }
  }

  private int getLogicalChannelIndex(Plane p, CV7000ChannelMappingMode mode) {
    return CHANNEL_MAPPER.getLogicalChannelIndex(channels, p, mode);
  }

  private Channel lookupChannel(Plane p) {
    return CHANNEL_MAPPER.lookupChannel(rawModel, channels, p);
  }

  // ###############
  // ## Module 5: Series Layout And Plane Lookup Construction
  // ###############

  /**
   * Build the field/channel/time/z layout before touching Bio-Formats core
   * metadata.  This keeps Yokogawa indexing separate from reader indexing.
   */
  private CV7000SeriesLayout buildSeriesLayout() throws FormatException {
    CV7000SeriesLayout layout = new CV7000SeriesLayout();
    HashSet<Field> acquiredFieldSet = collectAcquiredFields(layout);

    if (layout.firstFile == null) {
      throw new FormatException("No readable TIFF planes found in " + measurementPath);
    }

    determineChannelMappingMode(layout, acquiredFieldSet);
    sortAcquiredFields(layout.acquiredFields);
    indexAcquiredFields(layout);
    assignLogicalChannelSlots(layout, acquiredFieldSet);
    collectPlaneExtents(layout, acquiredFieldSet);
    layout.channelIndexes = new Integer[layout.channelSlots.size()];
    for (int i=0; i<layout.channelIndexes.length; i++) {
      layout.channelIndexes[i] = Integer.valueOf(i);
    }
    return layout;
  }

  /** Only fields with at least one readable plane become Bio-Formats series. */
  private HashSet<Field> collectAcquiredFields(CV7000SeriesLayout layout) {
    HashSet<Field> acquiredFieldSet = new HashSet<Field>();
    for (Plane p : planeData) {
      if (p != null && p.file != null) {
        allFiles.add(p.file);
        if (layout.firstFile == null) {
          layout.firstFile = p.file;
        }
        if (acquiredFieldSet.add(p.field)) {
          layout.acquiredFields.add(p.field);
        }
      }
    }
    return acquiredFieldSet;
  }

  /** Choose action-aware channel indexing only when all acquired planes map. */
  private void determineChannelMappingMode(CV7000SeriesLayout layout,
    HashSet<Field> acquiredFieldSet)
  {
    if (channels == null || channels.size() == 0) {
      setRawMLFChannelMapping(layout, "NO_CHANNEL_SIDECAR_METADATA");
      LOGGER.warn("Falling back to raw CV7000 MLF channel indexes; " +
        "no channel sidecar metadata is available");
      return;
    }
    if (!hasActionChannelMapping()) {
      setRawMLFChannelMapping(layout, "NO_ACTION_CHANNEL_MAPPING");
      LOGGER.warn("Falling back to raw CV7000 MLF channel indexes; " +
        "channel sidecar metadata has no action/channel assignments");
      return;
    }

    for (Plane p : planeData) {
      if (p == null || !acquiredFieldSet.contains(p.field)) {
        continue;
      }
      int channel = CHANNEL_MAPPER.getActionMappedChannelIndex(channels, p);
      if (channel == CHANNEL_NOT_FOUND) {
        setRawMLFChannelMapping(layout, "INCOMPLETE_ACTION_CHANNEL_MAPPING");
        LOGGER.warn("Falling back to raw CV7000 MLF channel indexes; " +
          "no exact action/channel metadata for timeline {}, action {}, channel {}",
          p.timelineIndex + 1, p.actionIndex + 1, p.channel + 1);
        return;
      }
    }

    layout.channelMappingMode = CV7000ChannelMappingMode.ACTION_MAPPED;
  }

  /** MES action parsing assigns non-negative action and timeline indexes. */
  private boolean hasActionChannelMapping() {
    if (channels == null) {
      return false;
    }
    for (Channel channel : channels) {
      if (channel != null && channel.timelineIndex >= 0 &&
        channel.actionIndex >= 0)
      {
        return true;
      }
    }
    return false;
  }

  /** Record raw-channel fallback for later provenance annotation emission. */
  private void setRawMLFChannelMapping(CV7000SeriesLayout layout, String reason) {
    layout.channelMappingMode = CV7000ChannelMappingMode.RAW_MLF;
    addYokogawaMeta("Yokogawa Channel Mapping ", "Mode", layout.channelMappingMode);
    addYokogawaMeta("Yokogawa Channel Mapping ", "FallbackReason", reason);
  }

  /** Series order follows plate row, plate column, then Yokogawa field index. */
  private void sortAcquiredFields(ArrayList<Field> acquiredFields) {
    Collections.sort(acquiredFields, new Comparator<Field>() {
      @Override
      public int compare(Field f1, Field f2) {
        if (f1.row != f2.row) {
          return f1.row - f2.row;
        }
        if (f1.column != f2.column) {
          return f1.column - f2.column;
        }
        return f1.field - f2.field;
      }
    });
  }

  /** Pre-index acquired wells and fields for plate metadata population. */
  private void indexAcquiredFields(CV7000SeriesLayout layout) {
    for (Field field : layout.acquiredFields) {
      String key = getWellKey(field.row, field.column);
      layout.acquiredWells.put(key, Boolean.TRUE);
      ArrayList<Field> fields = layout.fieldsByWell.get(key);
      if (fields == null) {
        fields = new ArrayList<Field>();
        layout.fieldsByWell.put(key, fields);
      }
      fields.add(field);
    }
  }

  /** Determine SizeZ/SizeT/channel coverage for every acquired field. */
  private void collectPlaneExtents(CV7000SeriesLayout layout, HashSet<Field> acquiredFieldSet) {
    for (Plane p : planeData) {
      if (p != null && acquiredFieldSet.contains(p.field)) {
        if (!layout.minMax.containsKey(p.field)) {
          layout.minMax.put(p.field, new MinMax());
        }
        MinMax m = layout.minMax.get(p.field);
        m.update(p);
      }
    }
  }

  /** Assign stable channel slots and qualify only real cross-timeline collisions. */
  private void assignLogicalChannelSlots(CV7000SeriesLayout layout,
    HashSet<Field> acquiredFieldSet)
  {
    HashMap<String, HashSet<Integer>> timelinesByCoordinate =
      new HashMap<String, HashSet<Integer>>();

    for (Plane p : planeData) {
      if (p == null || !acquiredFieldSet.contains(p.field)) {
        continue;
      }
      ChannelSlotKey base = createBaseChannelSlot(p, layout.channelMappingMode);
      String coordinate = getChannelCollisionCoordinate(p, base);
      HashSet<Integer> timelines = timelinesByCoordinate.get(coordinate);
      if (timelines == null) {
        timelines = new HashSet<Integer>();
        timelinesByCoordinate.put(coordinate, timelines);
      }
      timelines.add(Integer.valueOf(p.timelineIndex));
    }

    HashSet<String> qualifiedAcquisitions = new HashSet<String>();
    for (Plane p : planeData) {
      if (p == null || !acquiredFieldSet.contains(p.field)) {
        continue;
      }
      ChannelSlotKey base = createBaseChannelSlot(p, layout.channelMappingMode);
      HashSet<Integer> timelines = timelinesByCoordinate.get(
        getChannelCollisionCoordinate(p, base));
      if (timelines != null && timelines.size() > 1 &&
        p.timelineIndex != Collections.min(timelines).intValue())
      {
        qualifiedAcquisitions.add(
          base.identity() + "|timeline=" + p.timelineIndex);
      }
    }

    HashSet<ChannelSlotKey> primarySlots = new HashSet<ChannelSlotKey>();
    HashSet<ChannelSlotKey> qualifiedSlots = new HashSet<ChannelSlotKey>();
    for (Plane p : planeData) {
      if (p == null || !acquiredFieldSet.contains(p.field)) {
        continue;
      }
      ChannelSlotKey base = createBaseChannelSlot(p, layout.channelMappingMode);
      ChannelSlotKey slot = base;
      if (qualifiedAcquisitions.contains(
        base.identity() + "|timeline=" + p.timelineIndex))
      {
        slot = new ChannelSlotKey(base);
        slot.timelineIndex = p.timelineIndex;
        slot.timelineQualified = true;
        qualifiedSlots.add(slot);
      }
      else {
        primarySlots.add(slot);
      }
      p.channelSlot = slot;
    }

    ArrayList<ChannelSlotKey> primary =
      new ArrayList<ChannelSlotKey>(primarySlots);
    Collections.sort(primary);
    layout.channelSlots.addAll(primary);

    ArrayList<ChannelSlotKey> overflow =
      new ArrayList<ChannelSlotKey>(qualifiedSlots);
    Collections.sort(overflow, new Comparator<ChannelSlotKey>() {
      @Override
      public int compare(ChannelSlotKey a, ChannelSlotKey b) {
        int timeline = Integer.compare(a.timelineIndex, b.timelineIndex);
        return timeline == 0 ? a.compareBase(b) : timeline;
      }
    });
    layout.channelSlots.addAll(overflow);
    for (ChannelSlotKey slot : overflow) {
      addYokogawaMetaList("Yokogawa Channel Mapping ",
        "TimelineQualifiedSlot", slot.toString());
    }

    for (int i=0; i<layout.channelSlots.size(); i++) {
      layout.channelSlotIndexes.put(layout.channelSlots.get(i), Integer.valueOf(i));
    }
    for (Plane p : planeData) {
      if (p != null && p.channelSlot != null) {
        p.channelIndex = layout.channelSlotIndexes.get(p.channelSlot).intValue();
      }
    }
  }

  private ChannelSlotKey createBaseChannelSlot(Plane plane,
    CV7000ChannelMappingMode mode)
  {
    ChannelSlotKey key = new ChannelSlotKey();
    key.mappingMode = mode;
    key.actionIndex = plane.actionIndex;
    key.rawChannel = plane.channel;
    key.mappedIndex = mode == CV7000ChannelMappingMode.ACTION_MAPPED ?
      getLogicalChannelIndex(plane, mode) : -1;
    return key;
  }

  private String getChannelCollisionCoordinate(Plane plane,
    ChannelSlotKey base)
  {
    return plane.field.row + ":" + plane.field.column + ":" +
      plane.field.field + ":" + plane.z + ":" + plane.timepoint + "|" +
      base.identity();
  }

  /** Initialize the Bio-Formats core metadata once the logical layout is known. */
  private void initializeCoreMetadata(CV7000SeriesLayout layout) throws FormatException, IOException {
    reader = new MinimalTiffReader();
    reader.setId(layout.firstFile);
    core.clear();
    core.add(new CoreMetadata(reader.getCoreMetadataList().get(0)));

    core.get(0).dimensionOrder = CV7000_DIMENSION_ORDER;
    layout.reversePlaneLookup = new int[layout.acquiredFields.size()][];
    reversePlaneLookup = layout.reversePlaneLookup;

    for (int i=0; i<layout.acquiredFields.size(); i++) {
      if (i > 0) {
        core.add(new CoreMetadata(core.get(0)));
      }

      Field field = layout.acquiredFields.get(i);
      layout.fieldToSeries.put(field, i);
      MinMax m = layout.minMax.get(field);
      core.get(i).sizeZ = (m.maxZ - m.minZ) + 1;
      core.get(i).sizeT = (m.maxT - m.minT) + 1;
      core.get(i).sizeC = reader.getSizeC() * layout.channelIndexes.length;
      core.get(i).imageCount = core.get(i).sizeZ * core.get(i).sizeT *
        (core.get(i).sizeC / reader.getSizeC());
      reversePlaneLookup[i] = new int[core.get(i).imageCount];
      Arrays.fill(reversePlaneLookup[i], -1);
    }
  }

  /** Map each Yokogawa plane record to the reader's series and plane index. */
  private void populateReversePlaneLookup(CV7000SeriesLayout layout) throws FormatException {
    extraFiles = new ArrayList<String>();
    for (int i=0; i<planeData.size(); i++) {
      Plane p = planeData.get(i);
      if (p == null) {
        continue;
      }

      Integer series = layout.fieldToSeries.get(p.field);
      if (series == null) {
        continue;
      }

      p.series = series.intValue();
      MinMax m = layout.minMax.get(p.field);

      p.no = getLogicalPlaneIndex(p.series, p.z - m.minZ, p.channelIndex,
        p.timepoint - m.minT);
      assignPlaneLookup(i, p, layout);
      layout.indexPlane(p);
    }
  }

  /** Prefer a real plane over metadata-only duplicate records for each position. */
  private void assignPlaneLookup(int planeIndex, Plane p,
    CV7000SeriesLayout layout)
  {
    if (reversePlaneLookup[p.series][p.no] < 0) {
      reversePlaneLookup[p.series][p.no] = planeIndex;
    }
    else {
      int existingIndex = reversePlaneLookup[p.series][p.no];
      Plane existing = planeData.get(existingIndex);
      if ((existing == null || existing.file == null) && p.file != null) {
        reversePlaneLookup[p.series][p.no] = planeIndex;
        layout.duplicateCandidates.add(new DuplicatePlaneCandidate(
          existing, p, "REPLACED_BY_TIFF"));
      }
      else if (p.file != null) {
        LOGGER.warn("Ignoring file {}", p.file);
        extraFiles.add(p.file);
        layout.duplicateCandidates.add(new DuplicatePlaneCandidate(
          p, existing, "IGNORED_TIFF_DUPLICATE"));
      }
      else {
        layout.duplicateCandidates.add(new DuplicatePlaneCandidate(
          p, existing, "IGNORED_METADATA_ONLY_DUPLICATE"));
      }
    }
  }

  /** Prefer consistent Yokogawa significant-bit metadata over TIFF defaults. */
  private void applyInputBitDepths() {
    for (int series=0; series<core.size(); series++) {
      CoreMetadata metadata = core.get(series);
      int tiffBits = metadata.bitsPerPixel;
      int storageBits = FormatTools.getBytesPerPixel(metadata.pixelType) * 8;
      LinkedHashSet<Integer> inputBitDepths = new LinkedHashSet<Integer>();
      String fallbackReason = null;
      boolean representedChannelFound = false;

      for (int channelIndex=0; channelIndex<metadata.sizeC; channelIndex++) {
        Plane plane = lookupRepresentativePlane(series, channelIndex);
        if (plane == null) {
          continue;
        }
        representedChannelFound = true;

        Channel channel = lookupChannel(plane);
        if (channel == null) {
          if (fallbackReason == null) {
            fallbackReason = "no matching channel metadata for logical channel " +
              channelIndex;
          }
          continue;
        }
        if (channel.metadataAmbiguous) {
          if (fallbackReason == null) {
            fallbackReason = "ambiguous channel metadata for logical channel " +
              channelIndex;
          }
          continue;
        }

        Integer inputBitDepth = channel.inputBitDepth;
        if (inputBitDepth == null) {
          if (fallbackReason == null) {
            fallbackReason = "missing InputBitDepth for logical channel " +
              channelIndex;
          }
        }
        else if (inputBitDepth.intValue() <= 0) {
          if (fallbackReason == null) {
            fallbackReason = "invalid InputBitDepth " + inputBitDepth +
              " for logical channel " + channelIndex;
          }
        }
        else if (inputBitDepth.intValue() > storageBits) {
          if (fallbackReason == null) {
            fallbackReason = "InputBitDepth " + inputBitDepth +
              " exceeds the " + storageBits + "-bit storage width for logical channel " +
              channelIndex;
          }
        }
        else {
          inputBitDepths.add(inputBitDepth);
        }
      }

      if (!representedChannelFound) {
        fallbackReason = "no represented channels have Yokogawa metadata";
      }
      else if (fallbackReason == null && inputBitDepths.size() > 1) {
        fallbackReason = "conflicting InputBitDepth values " + inputBitDepths;
      }
      else if (fallbackReason == null && inputBitDepths.isEmpty()) {
        fallbackReason = "no usable InputBitDepth values";
      }

      if (fallbackReason == null) {
        metadata.bitsPerPixel = inputBitDepths.iterator().next().intValue();
      }
      else {
        LOGGER.warn("Falling back to TIFF-derived bit depth {} for CV7000 " +
          "series {}: {}", tiffBits, series, fallbackReason);
      }
    }
  }

  // ###############
  // ## Module 6: OME Plate, Image, Pixels, And Plane Metadata
  // ###############

  /** Populate OME metadata after core metadata and plane lookup are complete. */
  private void populateMetadataStore(WPIHandler plate, CV7000SeriesLayout layout)
    throws FormatException
  {
    SeriesTiming[] timings = buildSeriesTimings();

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    populatePlateMetadata(store, plate, layout, timings);
    setSeries(0);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      store.setPlateName(plate.getPlateName(), 0);
      store.setPlateExternalIdentifier(plate.getPlateID(), 0);

      InstrumentMetadataIndexes indexes = populateInstrumentMetadata(store);
      String experimenter = populateExperimenterMetadata(store);
      populateSeriesMetadata(store, indexes.instrument, indexes.lightSourceIndexes,
        indexes.detectorIndexes, indexes.filterIndexes, indexes.dichroicIndexes,
        indexes.usedObjectiveIDs, experimenter, timings);
      addYokogawaOriginalMetadata(store, indexes.instrument != null);
      setSeries(0);
    }
  }

  /** Build reusable instrument indexes before linking each Image/channel to them. */
  private InstrumentMetadataIndexes populateInstrumentMetadata(MetadataStore store)
    throws FormatException
  {
    InstrumentMetadataIndexes indexes = new InstrumentMetadataIndexes();
    if ((lightSources != null && lightSources.size() > 0) ||
      (channels != null && channels.size() > 0) ||
      (otfGeometryParameters != null &&
        otfGeometryParameters.objectivesByID.size() > 0) ||
      targetSystem != null)
    {
      indexes.instrument = MetadataTools.createLSID("Instrument", 0);

      store.setInstrumentID(indexes.instrument, 0);
      populateMicroscope(store);
      populateLightSources(store, indexes.lightSourceIndexes);
      populateObjectives(store, indexes.usedObjectiveIDs);
      populateDetectors(store, indexes.detectorIndexes);
      populateDichroics(store, indexes.dichroicIndexes);
      populateFilters(store, indexes.filterIndexes);
    }
    return indexes;
  }

  // ###############
  // ## Module 7: OME Instrument, Channel, And Filter Metadata
  // ###############

  private void populateMicroscope(MetadataStore store) {
    if (targetSystem == null) {
      return;
    }
    store.setMicroscopeManufacturer("Yokogawa", 0);
    store.setMicroscopeModel(targetSystem, 0);
  }

  private String populateExperimenterMetadata(MetadataStore store) {
    if (measurementOperatorName == null) {
      return null;
    }
    String experimenter = MetadataTools.createLSID("Experimenter", 0);
    store.setExperimenterID(experimenter, 0);
    store.setExperimenterUserName(measurementOperatorName, 0);
    return experimenter;
  }

  private void populatePlateMetadata(MetadataStore store, WPIHandler plate,
    CV7000SeriesLayout layout, SeriesTiming[] timings)
  {
    store.setPlateID(MetadataTools.createLSID("Plate", 0), 0);
    store.setPlateName(plate.getPlateName(), 0);
    store.setPlateExternalIdentifier(plate.getPlateID(), 0);
    store.setPlateRows(new PositiveInteger(plate.getPlateRows()), 0);
    store.setPlateColumns(new PositiveInteger(plate.getPlateColumns()), 0);
    store.setPlateRowNamingConvention(NamingConvention.LETTER, 0);
    store.setPlateColumnNamingConvention(NamingConvention.NUMBER, 0);
    store.setPlateWellOriginX(FormatTools.createLength(0.0, UNITS.MICROMETER), 0);
    store.setPlateWellOriginY(FormatTools.createLength(0.0, UNITS.MICROMETER), 0);

    String plateAcqID = MetadataTools.createLSID("PlateAcquisition", 0, 0);
    store.setPlateAcquisitionID(plateAcqID, 0, 0);

    HashMap<Integer, Integer> wellFieldCounts = new HashMap<Integer, Integer>();
    int maxFieldCount = 0;
    for (Field field : layout.acquiredFields) {
      int wellIndex = field.row * plate.getPlateColumns() + field.column;
      Integer count = wellFieldCounts.get(wellIndex);
      count = count == null ? 1 : count + 1;
      wellFieldCounts.put(wellIndex, count);
      if (count > maxFieldCount) {
        maxFieldCount = count;
      }
    }

    PositiveInteger fieldCount = FormatTools.getMaxFieldCount(maxFieldCount);
    if (fieldCount != null) {
      store.setPlateAcquisitionMaximumFieldCount(fieldCount, 0, 0);
    }

    if (startTime != null) {
      store.setPlateAcquisitionStartTime(new Timestamp(startTime), 0, 0);
    }
    if (endTime != null) {
      store.setPlateAcquisitionEndTime(new Timestamp(endTime), 0, 0);
    }

    int nextWell = 0;
    int nextImage = 0;
    for (int row=0; row<plate.getPlateRows(); row++) {
      for (int col=0; col<plate.getPlateColumns(); col++) {
        store.setWellID(MetadataTools.createLSID("Well", 0, nextWell), 0, nextWell);
        store.setWellRow(new NonNegativeInteger(row), 0, nextWell);
        store.setWellColumn(new NonNegativeInteger(col), 0, nextWell);

        ArrayList<Field> wellFields = layout.fieldsByWell.get(getWellKey(row, col));
        if (wellFields == null || wellFields.size() == 0) {
          nextWell++;
          continue;
        }

        int wellSample = 0;
        for (Field field : wellFields) {
          String wellSampleID =
            MetadataTools.createLSID("WellSample", 0, nextWell, wellSample);
          store.setWellSampleID(wellSampleID, 0, nextWell, wellSample);
          store.setWellSampleIndex(
            new NonNegativeInteger(nextImage), 0, nextWell, wellSample);
          String imageID = MetadataTools.createLSID("Image", nextImage);
          store.setImageID(imageID, nextImage);
          store.setWellSampleImageRef(imageID, 0, nextWell, wellSample);

          String name = "Well " + FormatTools.getWellRowName(row) +
            (col + 1) + ", Field " + (field.field + 1);
          store.setImageName(name, nextImage);
          if (timings[nextImage].startTimestamp != null) {
            Timestamp timepoint = new Timestamp(timings[nextImage].startTimestamp);
            store.setImageAcquisitionDate(timepoint, nextImage);
            store.setWellSampleTimepoint(timepoint, 0, nextWell, wellSample);
          }
          store.setPlateAcquisitionWellSampleRef(wellSampleID, 0, 0, nextImage);

          setSeries(nextImage);

          Plane p = lookupFirstBackedPlane(nextImage);
          if (p != null) {
            // CV7000 sidecars mix coordinate systems. WPP plate dimensions and
            // pitch are in millimeters, while MRF pixel sizes are in um/pixel.
            // MLF X/Y ranges match field centers fitting inside the WPP well
            // bottom when interpreted as micrometers around 0,0; they would be
            // impossible as millimeters and do not shift by the 9000 um well
            // pitch. Treat them as well-center-relative field offsets, not
            // absolute stage positions.
            store.setWellSamplePositionX(
              FormatTools.createLength(p.xpos, UNITS.MICROMETER),
              0, nextWell, wellSample);
            store.setWellSamplePositionY(
              FormatTools.createLength(p.ypos, UNITS.MICROMETER),
              0, nextWell, wellSample);
          }

          nextImage++;
          wellSample++;
        }
        nextWell++;
      }
    }
  }

  private void populateSeriesMetadata(MetadataStore store, String instrument,
    HashMap<Integer, Integer> lightSourceIndexes,
    HashMap<Integer, Integer> detectorIndexes,
    HashMap<FilterKey, Integer> filterIndexes,
    HashMap<String, Integer> dichroicIndexes,
    List<String> usedObjectiveIDs, String experimenter, SeriesTiming[] timings)
  {
    for (int i=0; i<getSeriesCount(); i++) {
      setSeries(i);
      if (instrument != null) {
        store.setImageInstrumentRef(instrument, i);
      }
      if (experimenter != null) {
        store.setImageExperimenterRef(experimenter, i);
      }
      populateChannelMetadata(store, i, lightSourceIndexes, detectorIndexes,
        filterIndexes, dichroicIndexes, usedObjectiveIDs);
      populatePlaneMetadata(store, i, timings[i]);
    }
  }

  private void populateChannelMetadata(MetadataStore store, int series,
    HashMap<Integer, Integer> lightSourceIndexes,
    HashMap<Integer, Integer> detectorIndexes,
    HashMap<FilterKey, Integer> filterIndexes,
    HashMap<String, Integer> dichroicIndexes,
    List<String> usedObjectiveIDs)
  {
    if (channels == null) {
      return;
    }

    Length physicalSizeZ = getPhysicalSizeZ(series);
    if (physicalSizeZ != null) {
      store.setPixelsPhysicalSizeZ(physicalSizeZ, series);
    }

    boolean physicalSizeSet = false;
    for (int c=0; c<getSizeC(); c++) {
      Plane p = lookupRepresentativePlane(series, c);
      if (p == null) {
        // Acquisition errors can leave metadata-only logical channels.
        continue;
      }
      Channel channel = lookupChannel(p);
      if (channel == null) {
        continue;
      }
      if (channel.metadataAmbiguous) {
        store.setChannelName(getChannelProvenance(channel, p), series, c);
        continue;
      }

      if (!physicalSizeSet) {
        store.setPixelsPhysicalSizeX(FormatTools.getPhysicalSizeX(channel.xSize), series);
        store.setPixelsPhysicalSizeY(FormatTools.getPhysicalSizeY(channel.ySize), series);
        physicalSizeSet = true;
      }

      int objective = -1;
      if (channel.objectiveID != null) {
        objective = usedObjectiveIDs.indexOf(channel.objectiveID);
      }

      if (channel.magnification != null && objective >= 0) {
        store.setObjectiveNominalMagnification(channel.magnification, 0, objective);
      }
      if (objective >= 0) {
        String objectiveID = MetadataTools.createLSID("Objective", 0, objective);
        store.setObjectiveSettingsID(objectiveID, series);
      }

      store.setChannelName(getChannelDisplayName(channel, p), series, c);

      AcquisitionMode acquisitionMode = getYokogawaAcquisitionMode(channel);
      if (acquisitionMode != null) {
        store.setChannelAcquisitionMode(acquisitionMode, series, c);
      }
      ContrastMethod contrastMethod = getYokogawaContrastMethod(channel);
      if (contrastMethod != null) {
        store.setChannelContrastMethod(contrastMethod, series, c);
      }
      IlluminationType illuminationType = getYokogawaIlluminationType(channel);
      if (illuminationType != null) {
        store.setChannelIlluminationType(illuminationType, series, c);
      }

      if (channel.color != null) {
        store.setChannelColor(channel.color, series, c);
      }
      if (channel.fluor != null && !channel.fluor.isEmpty()) {
        store.setChannelFluor(channel.fluor, series, c);
      }

      populateChannelLightSourceSettings(store, series, c, channel, lightSourceIndexes);
      populateChannelFilterSettings(store, series, c, channel, filterIndexes, dichroicIndexes);
      populateDetectorSettings(store, series, c, channel, detectorIndexes);
      populateExposureTime(store, series, c, channel);
    }
  }

  private String getChannelDisplayName(Channel channel, Plane plane) {
    String acquisition = clean(channel.acquisition);
    if (channel.isBrightfield()) {
      return acquisition == null ? BRIGHTFIELD : "BF / " + acquisition;
    }

    String target = clean(channel.target);
    if (target != null && acquisition != null) {
      return target + " / " + acquisition;
    }
    if (target != null) {
      return target;
    }
    if (acquisition != null) {
      return acquisition;
    }
    return getChannelProvenance(channel, plane);
  }

  private String getChannelProvenance(Channel channel) {
    return getChannelProvenance(channel, null);
  }

  private String getChannelProvenance(Channel channel, Plane plane) {
    int action = plane == null ? channel.actionIndex : plane.actionIndex;
    return "Action #" + (action + 1) + ", Channel #" +
      (channel.index + 1) + ", Camera #" + channel.cameraNumber;
  }

  private String getYokogawaChannelScope(Channel channel) {
    return "Yokogawa Timeline " + (channel.timelineIndex + 1) +
      " Action " + (channel.actionIndex + 1) +
      " Channel " + (channel.index + 1) + " ";
  }

  private String clean(String value) {
    return parsing.clean(value);
  }

  private void populateChannelLightSourceSettings(MetadataStore store, int series,
    int channelIndex, Channel channel, HashMap<Integer, Integer> lightSourceIndexes)
  {
    Integer lightSource = getLinkedStandardLightSource(channel);
    if (lightSource == null || !lightSourceIndexes.containsKey(lightSource)) {
      return;
    }

    LightSource source = lightSources.get(lightSource);
    int index = lightSourceIndexes.get(lightSource);
    store.setChannelLightSourceSettingsID(
      MetadataTools.createLSID("LightSource", 0, index), series, channelIndex);

    PercentFraction attenuation = getLightSourceAttenuation(source);
    if (attenuation != null) {
      store.setChannelLightSourceSettingsAttenuation(
        attenuation, series, channelIndex);
    }

    if (isLaser(source) && source.wavelength != null && source.wavelength > 0) {
      // Yokogawa BP labels are detection filters; excitation comes from the
      // linked laser light-source wavelength.
      store.setChannelExcitationWavelength(
        new Length(source.wavelength, UNITS.NANOMETER), series, channelIndex);
    }
  }

  private void populateChannelFilterSettings(MetadataStore store, int series,
    int channelIndex, Channel channel, HashMap<FilterKey, Integer> filterIndexes,
    HashMap<String, Integer> dichroicIndexes)
  {
    if (!channel.isBrightfield() && channel.detectionFilter != null &&
      channel.detectionFilter.center != null)
    {
      store.setChannelEmissionWavelength(
        new Length(channel.detectionFilter.center, UNITS.NANOMETER), series, channelIndex);
    }

    FilterKey filter = new FilterKey(channel);
    if (filterIndexes.containsKey(filter)) {
      store.setLightPathEmissionFilterRef(
        MetadataTools.createLSID("Filter", 0, filterIndexes.get(filter)),
        series, channelIndex, 0);
    }

    CrosstalkFilter crosstalk = getCrosstalkFilter(channel);
    if (crosstalk != null && crosstalk.dichroics.size() == 1 &&
      dichroicIndexes.containsKey(crosstalk.dichroics.get(0).name))
    {
      store.setLightPathDichroicRef(
        MetadataTools.createLSID(
          "Dichroic", 0, dichroicIndexes.get(crosstalk.dichroics.get(0).name)),
        series, channelIndex);
    }
  }

  private void populateDetectorSettings(MetadataStore store, int series,
    int channelIndex, Channel channel, HashMap<Integer, Integer> detectorIndexes)
  {
    if (!detectorIndexes.containsKey(channel.cameraNumber)) {
      return;
    }

    String detectorID = MetadataTools.createLSID(
      "Detector", 0, detectorIndexes.get(channel.cameraNumber));
    store.setDetectorSettingsID(detectorID, series, channelIndex);
    if (channel.detectorGain != null) {
      store.setDetectorSettingsGain(channel.detectorGain, series, channelIndex);
    }
    String binning = parsing.getBinningValue(channel.binning);
    if (binning != null) {
      try {
        store.setDetectorSettingsBinning(
          MetadataTools.getBinning(binning), series, channelIndex);
      }
      catch (FormatException e) {
        LOGGER.debug("Ignoring invalid CV7000 binning value {}", binning, e);
      }
    }
  }

  private void populateExposureTime(MetadataStore store, int series,
    int channelIndex, Channel channel)
  {
    if (channel.exposureTime == null) {
      return;
    }

    Time exposure = new Time(channel.exposureTime, UNITS.MILLISECOND);
    for (int z=0; z<getSizeZ(); z++) {
      for (int t=0; t<getSizeT(); t++) {
        int plane = getLogicalPlaneIndex(series, z, channelIndex, t);
        store.setPlaneExposureTime(exposure, series, plane);
      }
    }
  }

  private void populatePlaneMetadata(MetadataStore store, int series, SeriesTiming timing) {
    for (int p=0; p<getImageCount(); p++) {
      Plane plane = lookupPlane(series, p);
      if (plane == null) {
        continue;
      }
      // MLF Z values match MES AFShiftBase + (ZIndex - 1) * SliceLength, but
      // AFSearch indicates the reference is the autofocus/base surface. Keep
      // per-plane Z as an AF-relative reference-frame coordinate; calibrated Z
      // spacing is reported separately via Pixels.PhysicalSizeZ when available.
      store.setPlanePositionZ(FormatTools.createLength(plane.zpos, UNITS.REFERENCEFRAME), series, p);
      Double deltaT = getPlaneDeltaTSeconds(plane, timing.startMillis);
      if (deltaT != null) {
        store.setPlaneDeltaT(new Time(deltaT, UNITS.SECOND), series, p);
      }
    }
  }

  private SeriesTiming[] buildSeriesTimings() {
    SeriesTiming[] timings = new SeriesTiming[getSeriesCount()];
    for (int i=0; i<timings.length; i++) {
      timings[i] = new SeriesTiming();
      if (reversePlaneLookup == null || i >= reversePlaneLookup.length) {
        continue;
      }
      for (int no=0; no<reversePlaneLookup[i].length; no++) {
        Plane p = lookupPlane(i, no);
        if (p == null) {
          continue;
        }
        Long timestamp = parseTimestampMillis(p.timestamp);
        if (timestamp != null &&
          (timings[i].startMillis == null || timestamp < timings[i].startMillis))
        {
          timings[i].startMillis = timestamp;
          timings[i].startTimestamp = p.timestamp;
        }
      }
    }
    return timings;
  }

  private Long parseTimestampMillis(String timestamp) {
    if (timestamp == null || timestamp.trim().length() == 0) {
      return null;
    }
    try {
      return new Timestamp(timestamp).asInstant().getMillis();
    }
    catch (RuntimeException e) {
      LOGGER.debug("Ignoring invalid CV7000 timestamp {}", timestamp, e);
    }
    return null;
  }

  private Double getPlaneDeltaTSeconds(Plane plane, Long seriesStart) {
    if (plane == null || seriesStart == null) {
      return null;
    }
    Long timestamp = parseTimestampMillis(plane.timestamp);
    if (timestamp == null) {
      return null;
    }
    // DeltaT describes elapsed time within this OME Image/series. Yokogawa
    // timeline and timepoint indexes are acquisition planning coordinates and
    // are not expanded into SizeT here.
    return (timestamp - seriesStart) / 1000.0;
  }

  private AcquisitionMode getYokogawaAcquisitionMode(Channel channel) {
    if (channel == null) {
      return null;
    }
    if (isConfocalFluorescence(channel)) {
      return AcquisitionMode.SPINNINGDISKCONFOCAL;
    }
    if (channel.isBrightfield()) {
      try {
        return MetadataTools.getAcquisitionMode("BrightField");
      }
      catch (FormatException e) {
        LOGGER.debug("Ignoring unsupported CV7000 acquisition mode BrightField", e);
      }
    }
    if (isEpifluorescence(channel)) {
      return AcquisitionMode.WIDEFIELD;
    }
    return null;
  }

  private boolean isConfocalFluorescence(Channel channel) {
    return "ConfocalFluorescence".equals(channel.kind);
  }

  private boolean isEpifluorescence(Channel channel) {
    return channel != null && channel.method != null &&
      channel.method.toLowerCase().startsWith("epifluorescence");
  }

  private ContrastMethod getYokogawaContrastMethod(Channel channel) {
    if (channel == null) {
      return null;
    }
    String method = null;
    if (channel.isBrightfield()) {
      method = BRIGHTFIELD;
    }
    else if (isConfocalFluorescence(channel) || isEpifluorescence(channel)) {
      method = "Fluorescence";
    }
    if (method == null) {
      return null;
    }
    try {
      return MetadataTools.getContrastMethod(method);
    }
    catch (FormatException e) {
      LOGGER.debug("Ignoring unsupported CV7000 contrast method {}", method, e);
    }
    return null;
  }

  private IlluminationType getYokogawaIlluminationType(Channel channel) {
    if (channel == null) {
      return null;
    }
    String illumination = null;
    if (channel.isBrightfield()) {
      illumination = "Transmitted";
    }
    else if (isConfocalFluorescence(channel) || isEpifluorescence(channel)) {
      illumination = "Epifluorescence";
    }
    if (illumination == null) {
      return null;
    }
    try {
      return MetadataTools.getIlluminationType(illumination);
    }
    catch (FormatException e) {
      LOGGER.debug("Ignoring unsupported CV7000 illumination type {}", illumination, e);
    }
    return null;
  }

  private void populateLightSources(MetadataStore store,
    HashMap<Integer, Integer> lightSourceIndexes)
    throws FormatException
  {
    if (lightSources == null) {
      return;
    }
    int nextLightSource = 0;
    for (int i=0; i<lightSources.size(); i++) {
      LightSource l = lightSources.get(i);
      if (isLaser(l)) {
        String laserID = MetadataTools.createLSID("LightSource", 0, nextLightSource);
        store.setLaserID(laserID, 0, nextLightSource);
        store.setLaserModel(getLightSourceModel(l), 0, nextLightSource);
        // The Yokogawa CV7000 user manual identifies the lasers as solid-state.
        store.setLaserType(MetadataTools.getLaserType("SolidState"), 0, nextLightSource);
        if (l.wavelength != null) {
          store.setLaserWavelength(
            new Length(l.wavelength, UNITS.NANOMETER), 0, nextLightSource);
        }
        lightSourceIndexes.put(i, nextLightSource);
        nextLightSource++;
      }
      else if (isLamp(l)) {
        String filamentID = MetadataTools.createLSID("LightSource", 0, nextLightSource);
        store.setFilamentID(filamentID, 0, nextLightSource);
        store.setFilamentModel(getLightSourceModel(l), 0, nextLightSource);
        // The CV7000 manual describes the white-light lamp as halogen, but
        // installed hardware may differ and the sidecars expose only generic
        // Type="Lamp", so keep the filament type as Other.
        store.setFilamentType(MetadataTools.getFilamentType("Other"),
          0, nextLightSource);
        lightSourceIndexes.put(i, nextLightSource);
        nextLightSource++;
      }
    }
  }

  private boolean isLaser(LightSource source) {
    return source != null && "Laser".equalsIgnoreCase(source.type);
  }

  private boolean isLamp(LightSource source) {
    return source != null && "Lamp".equalsIgnoreCase(source.type);
  }

  private PercentFraction getLightSourceAttenuation(LightSource source) {
    if (source == null || source.attenuation == null) {
      return null;
    }

    double attenuation = source.attenuation;
    if (Double.isNaN(attenuation) || Double.isInfinite(attenuation) ||
      attenuation < 0 || attenuation > 100)
    {
      LOGGER.warn("Could not store CV7000 light source '{}' attenuation value '{}' " +
        "in LightSourceSettings.Attenuation", source.name, source.attenuation);
      return null;
    }

    // Yokogawa bts:Power is not a physical power measurement. Values in real
    // sidecars are commonly 0..100, so treat them as percent-like attenuation
    // only when they can be represented in OME's 0..1 PercentFraction.
    if (attenuation > 1) {
      attenuation /= 100.0;
    }
    return new PercentFraction((float) attenuation);
  }

  private String getLightSourceModel(LightSource source) {
    String model = clean(source.name);
    if (model == null) {
      model = clean(source.type);
    }
    return model;
  }

  private void populateObjectives(MetadataStore store, List<String> usedObjectiveIDs) {
    if (channels != null) {
      for (Channel c : channels) {
        if (c.metadataAmbiguous) {
          continue;
        }
        if (c.objectiveID != null && !usedObjectiveIDs.contains(c.objectiveID)) {
          int index = usedObjectiveIDs.size();
          String objectiveID = MetadataTools.createLSID("Objective", 0, index);
          store.setObjectiveID(objectiveID, 0, index);
          populateObjectiveModel(store, index, c.objective, c.magnification);
          usedObjectiveIDs.add(c.objectiveID);
        }
      }
    }

    if (otfGeometryParameters != null) {
      for (OTFGeometryObjective objective :
        otfGeometryParameters.objectivesByID.values())
      {
        if (objective.objectiveID != null &&
          !usedObjectiveIDs.contains(objective.objectiveID))
        {
          int index = usedObjectiveIDs.size();
          String objectiveID = MetadataTools.createLSID("Objective", 0, index);
          store.setObjectiveID(objectiveID, 0, index);
          populateObjectiveModel(store, index, objective.objective,
            objective.magnification);
          usedObjectiveIDs.add(objective.objectiveID);
        }
      }
    }
  }

  private void populateObjectiveModel(MetadataStore store, int objectiveIndex,
    String model, Double magnification)
  {
    if (model != null) {
      store.setObjectiveModel(model, 0, objectiveIndex);
    }
    CV7000ObjectiveSpec spec = getObjectiveSpec(model);
    Double nominalMagnification = magnification;
    if (nominalMagnification == null && spec != null) {
      nominalMagnification = spec.magnification;
    }
    if (nominalMagnification != null) {
      store.setObjectiveNominalMagnification(
        nominalMagnification, 0, objectiveIndex);
    }
    if (spec == null) {
      return;
    }
    if (inferObjectiveLensNA() && spec.lensNA != null) {
      store.setObjectiveLensNA(spec.lensNA, 0, objectiveIndex);
    }
    if (spec.immersion != null) {
      try {
        store.setObjectiveImmersion(
          MetadataTools.getImmersion(spec.immersion), 0, objectiveIndex);
      }
      catch (FormatException e) {
        LOGGER.debug("Ignoring unsupported CV7000 objective immersion {}",
          spec.immersion, e);
      }
    }
  }

  private CV7000ObjectiveSpec getObjectiveSpec(String model) {
    ObjectiveModelTokens tokens = parseObjectiveModel(model);

    for (KnownObjectiveSpec known : KNOWN_OBJECTIVES) {
      if (known.matches(tokens)) {
        return known.toObjectiveSpec();
      }
    }

    if (tokens.magnification != null || tokens.immersion != null) {
      Double magnification = tokens.magnification == null ?
        null : Double.valueOf(tokens.magnification.doubleValue());
      return new CV7000ObjectiveSpec(magnification, null, tokens.immersion);
    }
    return null;
  }

  private ObjectiveModelTokens parseObjectiveModel(String model) {
    String cleaned = clean(model);
    ObjectiveModelTokens tokens = new ObjectiveModelTokens();
    if (cleaned == null) {
      return tokens;
    }
    String normalized = cleaned.toLowerCase().replace('\u00d7', 'x');
    normalized = normalized.replaceAll("[^a-z0-9]+", " ").trim();
    normalized = normalized.replaceAll("\\s+", " ");
    String[] parts = normalized.split(" ");
    for (int i=0; i<parts.length; i++) {
      String part = parts[i];
      if (part.endsWith("x") && part.length() > 1) {
        tokens.magnification = parsing.parseInteger(
          part.substring(0, part.length() - 1));
      }
      if ("ph".equals(part) || "phase".equals(part)) {
        tokens.phase = true;
      }
      if ("lwd".equals(part) || "wd".equals(part) || "long".equals(part)) {
        tokens.longWorkingDistance = true;
      }
      if ("w".equals(part) && i + 1 < parts.length && "d".equals(parts[i + 1])) {
        tokens.longWorkingDistance = true;
        i++;
      }
      else if ("w".equals(part)) {
        tokens.immersion = "Water";
      }
    }
    return tokens;
  }

  private void populateDetectors(MetadataStore store,
    HashMap<Integer, Integer> detectorIndexes)
    throws FormatException
  {
    if (channels == null) {
      return;
    }
    for (Channel c : channels) {
      if (c.metadataAmbiguous) {
        continue;
      }
      if (!detectorIndexes.containsKey(c.cameraNumber)) {
        int detector = detectorIndexes.size();
        detectorIndexes.put(c.cameraNumber, detector);
        String detectorID = MetadataTools.createLSID("Detector", 0, detector);
        store.setDetectorID(detectorID, 0, detector);
        populateDetectorCameraType(store, detector, c.cameraType);
        // The CV7000 manual describes the detector as sCMOS. OME-XML has
        // CMOS, but not literal sCMOS, in the Detector.Type enum. Manual
        // sensor pitch and nominal sensor dimensions are hardware reference
        // values, so do not emit them as image size or pixel calibration.
        store.setDetectorType(MetadataTools.getDetectorType("CMOS"), 0, detector);
      }
    }
  }

  private void populateDetectorCameraType(MetadataStore store, int detector,
    String cameraType)
  {
    String type = clean(cameraType);
    if (type == null) {
      return;
    }
    if ("Andor".equalsIgnoreCase(type)) {
      store.setDetectorManufacturer("Andor", 0, detector);
    }
    else {
      store.setDetectorModel(type, 0, detector);
    }
  }

  private void populateDichroics(MetadataStore store,
    HashMap<String, Integer> dichroicIndexes)
  {
    if (crosstalkParameters == null) {
      return;
    }

    for (CrosstalkFilter filter : crosstalkParameters.filters.values()) {
      for (CrosstalkDichroic dichroic : filter.dichroics) {
        if (dichroic.name == null || dichroic.name.trim().length() == 0 ||
          dichroicIndexes.containsKey(dichroic.name))
        {
          continue;
        }
        int index = dichroicIndexes.size();
        dichroicIndexes.put(dichroic.name, index);
        String dichroicID = MetadataTools.createLSID("Dichroic", 0, index);
        store.setDichroicID(dichroicID, 0, index);
        store.setDichroicModel(dichroic.name, 0, index);
      }
    }
  }

  private void populateFilters(MetadataStore store,
    HashMap<FilterKey, Integer> filterIndexes)
    throws FormatException
  {
    if (channels == null) {
      return;
    }
    for (Channel c : channels) {
      if (c.metadataAmbiguous) {
        continue;
      }
      FilterKey key = new FilterKey(c);
      if (!key.isValid() || filterIndexes.containsKey(key)) {
        continue;
      }
      int filter = filterIndexes.size();
      filterIndexes.put(key, filter);
      String filterID = MetadataTools.createLSID("Filter", 0, filter);
      store.setFilterID(filterID, 0, filter);
      store.setFilterModel(key.acquisition, 0, filter);
      store.setFilterType(MetadataTools.getFilterType(key.detectionFilter.filterType), 0, filter);
      String wheel = getYokogawaFilterWheelLabel(key);
      if (wheel != null) {
        store.setFilterFilterWheel(wheel, 0, filter);
      }
      CrosstalkFilter crosstalk = getCrosstalkFilter(c);
      if (key.detectionFilter != null) {
        Length cutIn = getCrosstalkCutIn(crosstalk);
        if (cutIn == null && key.detectionFilter.cutIn != null) {
          cutIn = FormatTools.getCutIn(key.detectionFilter.cutIn);
        }
        Length cutOut = getCrosstalkCutOut(crosstalk);
        if (cutOut == null && key.detectionFilter.cutOut != null) {
          cutOut = FormatTools.getCutOut(key.detectionFilter.cutOut);
        }
        if (cutIn != null) {
          store.setTransmittanceRangeCutIn(cutIn, 0, filter);
        }
        if (cutOut != null) {
          store.setTransmittanceRangeCutOut(cutOut, 0, filter);
        }
        PercentFraction transmittance = getCrosstalkTransmittance(crosstalk);
        if (transmittance != null) {
          store.setTransmittanceRangeTransmittance(transmittance, 0, filter);
        }
      }
    }
  }

  private String getYokogawaFilterWheelLabel(FilterKey key) {
    if (key == null || key.filterWheelPosition == null ||
      key.filterWheelPosition <= 0)
    {
      return null;
    }
    return "Yokogawa wheel " + key.filterWheelPosition;
  }

  private Length getCrosstalkCutIn(CrosstalkFilter filter) {
    return filter == null || filter.minWaveLength == null ?
      null : FormatTools.getCutIn(filter.minWaveLength);
  }

  private Length getCrosstalkCutOut(CrosstalkFilter filter) {
    return filter == null || filter.maxWaveLength == null ?
      null : FormatTools.getCutOut(filter.maxWaveLength);
  }

  private PercentFraction getCrosstalkTransmittance(CrosstalkFilter filter) {
    if (filter == null || filter.averageTransmittance == null) {
      return null;
    }
    double fraction = filter.averageTransmittance / 100.0;
    if (fraction < 0 || fraction > 1) {
      LOGGER.debug("Ignoring invalid CV7000 OTF transmittance {}",
        filter.averageTransmittance);
      return null;
    }
    return new PercentFraction(Float.valueOf((float) fraction));
  }

  // ###############
  // ## Module 8: Yokogawa Provenance And Raw Sidecar Annotations
  // ###############

  private void addYokogawaOriginalMetadata(MetadataStore store,
    boolean hasInstrument) throws FormatException
  {
    int rawSidecarAnnotation = 0;
    int plateAnnotationRef = 0;
    if (allFiles != null) {
      for (String file : allFiles) {
        if (file == null) {
          continue;
        }
        Location location = new Location(file);
        String name = location.getName();
        CV7000FileRole role = classifyCV7000File(location);
        if (isDatasetFile(role) && !isPixelFile(role)) {
          parseStructuredSidecarMetadata(file, role);
          addSidecarSummaryMetadata(file, role);
          if (preserveRawSidecars() && preserveRawSidecarAsFileAnnotation(role)) {
            String annotationID = addRawSidecarFileAnnotation(
              store, file, rawSidecarAnnotation);
            if (annotationID != null) {
              store.setPlateAnnotationRef(annotationID, 0, plateAnnotationRef);
              rawSidecarAnnotation++;
              plateAnnotationRef++;
            }
          }
          if (name != null) {
            addYokogawaMetaList("Yokogawa Sidecar ", "File", name);
          }
        }
      }
    }
    addMeasurementDataOriginalMetadata();
    if (lightSources != null) {
      for (LightSource source : lightSources) {
        String prefix = "Yokogawa LightSource " + source.name + " ";
        addYokogawaMeta(prefix, "Type", source.type);
        addYokogawaMeta(prefix, "WaveLength", source.wavelength);
        addYokogawaMeta(prefix, "Power", source.attenuation);
      }
    }
    addCrosstalkOriginalMetadata();
    addOTFGeometryOriginalMetadata();
    addPlaneProvenanceOriginalMetadata();
    if (channels == null) {
      emitYokogawaMapAnnotations(store, plateAnnotationRef, hasInstrument);
      return;
    }
    HashSet<String> seen = new HashSet<String>();
    for (Channel c : channels) {
      String key = c.timelineIndex + ":" + c.actionIndex + ":" + c.index;
      if (!seen.add(key)) {
        continue;
      }
      String prefix = getYokogawaChannelScope(c);
      // Raw Yokogawa planning fields are kept even when a subset is promoted
      // to core OME fields; this makes enum/wavelength decisions auditable.
      addYokogawaMeta(prefix, "Target", c.target);
      addYokogawaMeta(prefix, "Kind", c.kind);
      addYokogawaMeta(prefix, "MethodID", c.methodID);
      addYokogawaMeta(prefix, "Method", c.method);
      addYokogawaMeta(prefix, "ActionType", c.actionType);
      addYokogawaMeta(prefix, "ActionRunMode", c.actionRunMode);
      addYokogawaMeta(prefix, "ActionAFSearch", c.actionAFSearch);
      addYokogawaMeta(prefix, "ActionXOffset", c.actionXOffset);
      addYokogawaMeta(prefix, "ActionYOffset", c.actionYOffset);
      addYokogawaMeta(prefix, "ActionAFShiftBase", c.actionAFShiftBase);
      addYokogawaMeta(prefix, "ActionTopDistance", c.actionTopDistance);
      addYokogawaMeta(prefix, "ActionBottomDistance", c.actionBottomDistance);
      addYokogawaMeta(prefix, "ActionSliceLength", c.actionSliceLength);
      addYokogawaMeta(prefix, "ActionUseSoftFocus", c.actionUseSoftFocus);
      addYokogawaMeta(prefix, "FilterID", c.filterID);
      addYokogawaMeta(prefix, "Acquisition", c.acquisition);
      addYokogawaMeta(prefix, "ChannelProvenance", getChannelProvenance(c));
      if (c.detectionFilter != null) {
        addYokogawaMeta(prefix, "DetectionFilterType", c.detectionFilter.filterType);
        addYokogawaMeta(prefix, "DetectionFilterCenter", c.detectionFilter.center);
        addYokogawaMeta(prefix, "DetectionFilterWidth", c.detectionFilter.width);
        addYokogawaMeta(prefix, "DetectionFilterCutIn", c.detectionFilter.cutIn);
        addYokogawaMeta(prefix, "DetectionFilterCutOut", c.detectionFilter.cutOut);
      }
      addYokogawaMeta(prefix, "CameraNumber", c.cameraNumber);
      addYokogawaMeta(prefix, "CameraType", c.cameraType);
      addYokogawaMeta(prefix, "Binning", c.binning);
      addYokogawaMeta(prefix, "AndorParameterID", c.andorParameterID);
      addYokogawaMeta(prefix, "AndorParameter", c.andorParameter);
      addYokogawaMeta(prefix, "DetectorGain", c.detectorGain);
      addYokogawaMeta(prefix, "InputBitDepth", c.inputBitDepth);
      addYokogawaMeta(prefix, "InputLevel", c.inputLevel);
      addYokogawaMeta(prefix, "HorizontalPixels", c.horizontalPixels);
      addYokogawaMeta(prefix, "VerticalPixels", c.verticalPixels);
      addYokogawaMeta(prefix, "FilterWheelPosition", c.filterWheelPosition);
      addYokogawaMeta(prefix, "FilterPosition", c.filterPosition);
      addYokogawaMeta(prefix, "ShadingCorrectionSource", c.correctionFile);
    }
    emitYokogawaMapAnnotations(store, plateAnnotationRef, hasInstrument);
  }

  private void addCrosstalkOriginalMetadata() {
    if (crosstalkParameters == null) {
      return;
    }

    int filterIndex = 1;
    for (CrosstalkFilter filter : crosstalkParameters.filters.values()) {
      String prefix = "Yokogawa OTF Crosstalk EMFilter " + filterIndex + " ";
      addYokogawaMeta(prefix, "FilterID", filter.filterID);
      addYokogawaMeta(prefix, "CameraID", filter.cameraNumber);
      addYokogawaMeta(prefix, "Acquisition", filter.acquisition);
      addYokogawaMeta(prefix, "MinWaveLength", filter.minWaveLength);
      addYokogawaMeta(prefix, "MaxWaveLength", filter.maxWaveLength);
      addYokogawaMeta(prefix, "AverageTransmittance", filter.averageTransmittance);
      for (int i=0; i<filter.dichroics.size(); i++) {
        CrosstalkDichroic dichroic = filter.dichroics.get(i);
        String dichroicPrefix = prefix + "ISDM " + (i + 1) + " ";
        addYokogawaMeta(dichroicPrefix, "ID", dichroic.id);
        addYokogawaMeta(dichroicPrefix, "Name", dichroic.name);
        addYokogawaMeta(dichroicPrefix, "Reflection", dichroic.reflection);
        addYokogawaMeta(
          dichroicPrefix, "AverageTransmittance", dichroic.averageTransmittance);
      }
      filterIndex++;
    }

    for (CrosstalkFluorophore fluorophore : crosstalkParameters.fluorophores) {
      String prefix = "Yokogawa OTF Crosstalk Fluorophore " +
        fluorophore.name + " ";
      for (CrosstalkFluorophoreIntensity intensity : fluorophore.intensities) {
        addYokogawaMeta(prefix, "Filter " + intensity.filterID + " AverageIntensity",
          intensity.averageIntensity);
      }
    }
  }

  private void addOTFGeometryOriginalMetadata() {
    if (otfGeometryParameters == null) {
      return;
    }

    addYokogawaMeta("Yokogawa OTF Geometry ", "Mode",
      otfGeometryParameters.mode);

    int objectiveIndex = 1;
    for (OTFGeometryObjective objective :
      otfGeometryParameters.objectivesByID.values())
    {
      String prefix = "Yokogawa OTF Geometry Objective " + objectiveIndex + " ";
      addYokogawaMeta(prefix, "ObjectiveID", objective.objectiveID);
      addYokogawaMeta(prefix, "Objective", objective.objective);
      addYokogawaMeta(prefix, "Magnification", objective.magnification);
      CV7000ObjectiveSpec spec =
        getObjectiveSpec(objective.objective);
      if (spec != null) {
        if (inferObjectiveLensNA()) {
          addYokogawaMeta(prefix, "MappedLensNA", spec.lensNA);
        }
        addYokogawaMeta(prefix, "MappedImmersion", spec.immersion);
      }
      objectiveIndex++;
    }

    int affineIndex = 1;
    for (OTFGeometryAffine affine : otfGeometryParameters.affines) {
      String prefix = "Yokogawa OTF Geometry Affine " + affineIndex + " ";
      addOTFGeometryAffineMetadata(prefix, affine);

      if (channels != null) {
        for (Channel channel : channels) {
          if (matchesAffine(channel, affine)) {
            addOTFGeometryAffineMetadata(getYokogawaChannelScope(channel) +
              "OTFGeometry ", affine);
          }
        }
      }
      affineIndex++;
    }
  }

  private void addOTFGeometryAffineMetadata(String prefix,
    OTFGeometryAffine affine)
  {
    addYokogawaMeta(prefix, "MethodID", affine.methodID);
    addYokogawaMeta(prefix, "Method", affine.method);
    addYokogawaMeta(prefix, "ObjectiveID", affine.objectiveID);
    addYokogawaMeta(prefix, "Objective", affine.objective);
    addYokogawaMeta(prefix, "Magnification", affine.magnification);
    addYokogawaMeta(prefix, "FilterID", affine.filterID);
    addYokogawaMeta(prefix, "Acquisition", affine.acquisition);
    addYokogawaMeta(prefix, "Use", affine.use);
    addYokogawaMeta(prefix, "UpdateTime", affine.updateTime);
    addYokogawaMeta(prefix, "A", affine.a);
    addYokogawaMeta(prefix, "B", affine.b);
    addYokogawaMeta(prefix, "C", affine.c);
    addYokogawaMeta(prefix, "D", affine.d);
    addYokogawaMeta(prefix, "E", affine.e);
    addYokogawaMeta(prefix, "F", affine.f);
  }

  private boolean matchesAffine(Channel channel, OTFGeometryAffine affine) {
    if (channel == null || affine == null) {
      return false;
    }
    return sameYokogawaValue(affine.methodID, channel.methodID) &&
      sameYokogawaValue(affine.method, channel.method) &&
      sameYokogawaValue(affine.objectiveID, channel.objectiveID) &&
      sameYokogawaValue(affine.filterID, channel.filterID) &&
      sameYokogawaValue(affine.acquisition, channel.acquisition);
  }

  private boolean sameYokogawaValue(String a, String b) {
    String cleanA = clean(a);
    String cleanB = clean(b);
    return cleanA == null ? cleanB == null : cleanA.equals(cleanB);
  }

  private void addSidecarSummaryMetadata(String file, CV7000FileRole role) {
    Location location = new Location(file);
    String name = location.getName();
    if (name == null) {
      return;
    }

    try {
      SidecarSummary summary = summarizeSidecar(file);
      String prefix = "Yokogawa Sidecar " + name + " ";

      addYokogawaMeta(prefix, "Role", role.name());
      addYokogawaMeta(prefix, "ByteLength", summary.byteLength);
      addYokogawaMeta(prefix, "SHA-256", summary.sha256);
    }
    catch (IOException e) {
      LOGGER.debug("Could not summarize CV7000 sidecar {}", file, e);
    }
  }

  private String addRawSidecarFileAnnotation(MetadataStore store, String file,
    int index) throws FormatException
  {
    Location location = new Location(file);
    String name = location.getName();
    if (name == null) {
      return null;
    }

    try {
      byte[] bytes = readSidecarBytes(file);
      NonNegativeLong length = new NonNegativeLong(Long.valueOf(bytes.length));
      byte[] payload = bytes;
      Compression compression = Compression.NONE;
      if (compressRawSidecars()) {
        payload = new ZlibCodec().compress(bytes, null);
        compression = Compression.ZLIB;
      }
      long encodedLength = 4L * ((payload.length + 2L) / 3L);
      String annotationID = "Annotation:CV7000RawSidecar:" + index;

      store.setFileAnnotationID(annotationID, index);
      store.setFileAnnotationNamespace(RAW_SIDECAR_ANNOTATION_NAMESPACE, index);
      store.setFileAnnotationDescription(
        "Yokogawa CV7000 raw sidecar " + name, index);
      store.setBinaryFileFileName(name, index);
      store.setBinaryFileMIMEType(XML_MIME_TYPE, index);
      store.setBinaryFileSize(length, index);
      store.setBinaryFileBinData(payload, index);
      store.setBinaryFileBinDataCompression(compression, index);
      // XML sidecars are opaque byte streams, so host byte order is irrelevant.
      store.setBinaryFileBinDataBigEndian(Boolean.FALSE, index);
      store.setBinaryFileBinDataLength(
        new NonNegativeLong(Long.valueOf(encodedLength)), index);
      return annotationID;
    }
    catch (IOException e) {
      LOGGER.debug("Could not preserve raw CV7000 sidecar {}", file, e);
    }
    return null;
  }

  private SidecarSummary summarizeSidecar(String file) throws IOException {
    RandomAccessInputStream stream = new RandomAccessInputStream(file);
    try {
      long length = stream.length();
      MessageDigest digest = createSHA256Digest();
      byte[] buffer = new byte[8192];
      long remaining = length;
      while (remaining > 0) {
        int count = (int) Math.min(buffer.length, remaining);
        stream.readFully(buffer, 0, count);
        digest.update(buffer, 0, count);
        remaining -= count;
      }
      return new SidecarSummary(length, toHex(digest.digest()));
    }
    finally {
      stream.close();
    }
  }

  private byte[] readSidecarBytes(String file) throws IOException {
    RandomAccessInputStream stream = new RandomAccessInputStream(file);
    try {
      long length = stream.length();
      if (length > Integer.MAX_VALUE) {
        throw new IOException("CV7000 sidecar too large to embed: " + file);
      }
      byte[] bytes = new byte[(int) length];
      stream.readFully(bytes);
      return bytes;
    }
    finally {
      stream.close();
    }
  }

  private CV7000FileRole classifyCV7000File(Location file) {
    if (file == null) {
      return CV7000FileRole.UNKNOWN;
    }
    String name = file.getName();
    if (name == null) {
      return CV7000FileRole.UNKNOWN;
    }
    if (isPath(file, currentId)) {
      return CV7000FileRole.WPI;
    }
    if (isPath(file, measurementPath)) {
      return CV7000FileRole.MEASUREMENT_DATA;
    }
    if (isPath(file, detailPath)) {
      return CV7000FileRole.MEASUREMENT_DETAIL;
    }
    if (isPath(file, settingsPath)) {
      return CV7000FileRole.MEASUREMENT_SETTINGS;
    }
    if (isPath(file, wppPath)) {
      return CV7000FileRole.WELL_PLATE_PRODUCT;
    }
    if (datasetPaths != null) {
      if (isPath(file, datasetPaths.wpiPath)) {
        return CV7000FileRole.WPI;
      }
      if (isPath(file, datasetPaths.measurementDataPath)) {
        return CV7000FileRole.MEASUREMENT_DATA;
      }
      if (isPath(file, datasetPaths.measurementDetailPath)) {
        return CV7000FileRole.MEASUREMENT_DETAIL;
      }
      if (isPath(file, datasetPaths.postProcessPath)) {
        return CV7000FileRole.POST_PROCESS;
      }
      if (isPath(file, datasetPaths.otfCrosstalkPath)) {
        return CV7000FileRole.OTF_CROSSTALK;
      }
      if (isPath(file, datasetPaths.otfGeometryPath)) {
        return CV7000FileRole.OTF_GEOMETRY;
      }
    }
    if (planeFiles.contains(file.getAbsolutePath())) {
      return CV7000FileRole.TIFF_PLANE;
    }
    if (correctionFiles.contains(file.getAbsolutePath())) {
      return CV7000FileRole.SHADING_CORRECTION;
    }
    return CV7000FileRole.UNKNOWN;
  }

  // ###############
  // ## Module 9: File Classification, Utilities, And Plane Lookup Helpers
  // ###############

  private boolean isTiffFile(String name) {
    return name != null && checkSuffix(name, new String[] {"tif", "tiff"});
  }

  private boolean isDatasetFile(CV7000FileRole role) {
    return role != null && role != CV7000FileRole.UNKNOWN;
  }

  private boolean preserveRawSidecarAsFileAnnotation(CV7000FileRole role) {
    if (role == null) {
      return false;
    }
    switch (role) {
      case WPI:
      case MEASUREMENT_DATA:
      case MEASUREMENT_DETAIL:
      case MEASUREMENT_SETTINGS:
      case WELL_PLATE_PRODUCT:
      case POST_PROCESS:
      case OTF_CROSSTALK:
      case OTF_GEOMETRY:
        return true;
      default:
        return false;
    }
  }

  private boolean isPixelFile(CV7000FileRole role) {
    return role == CV7000FileRole.TIFF_PLANE ||
      role == CV7000FileRole.SHADING_CORRECTION;
  }

  private boolean isPath(Location file, String path) {
    if (path == null) {
      return false;
    }
    return file.getAbsolutePath().equals(new Location(path).getAbsolutePath());
  }

  private void parseStructuredSidecarMetadata(String file, CV7000FileRole role) {
    if (role == CV7000FileRole.POST_PROCESS) {
      addPostProcessOriginalMetadata(file);
    }
  }

  private void addPostProcessOriginalMetadata(String file) {
    try {
      String xml = readSanitizedXML(file);
      if (xml.length() > 0) {
        XMLTools.parseXML(xml, new PostProcessHandler());
      }
    }
    catch (IOException e) {
      LOGGER.warn("Could not parse CV7000 post-processing sidecar {}", file, e);
    }
  }

  private void emitPostProcessMetadata(PostProcessResult result,
    int unknownElementCount)
  {
    String rootPrefix = "Yokogawa PPF PostProcess ";
    addSanitizedPPFAttributes(rootPrefix, result.root);
    addYokogawaMeta(rootPrefix, "ActionCount", result.actions.size());
    addYokogawaMeta(rootPrefix, "LogCount", result.logs.size());
    addYokogawaMeta(rootPrefix, "UnknownElementCount", unknownElementCount);
    Long begin = parseTimestampMillis(result.root.get("BeginTime"));
    Long end = parseTimestampMillis(result.root.get("EndTime"));
    if (begin != null && end != null) {
      addYokogawaMeta(rootPrefix, "DurationSeconds", (end - begin) / 1000.0);
    }

    LinkedHashMap<String, Integer> actionTypes =
      new LinkedHashMap<String, Integer>();
    LinkedHashMap<String, Integer> actionStatuses =
      new LinkedHashMap<String, Integer>();
    for (int i=0; i<result.actions.size(); i++) {
      PostProcessAction action = result.actions.get(i);
      incrementCount(actionTypes, action.type);
      incrementCount(actionStatuses, clean(action.attributes.get("Status")));
      String prefix = "Yokogawa PPF Action " + (i + 1) + " ";
      addYokogawaMeta(prefix, "ActionType", action.type);
      addSanitizedPPFAttributes(prefix, action.attributes);
    }
    for (Map.Entry<String, Integer> entry : actionTypes.entrySet()) {
      addYokogawaMeta("Yokogawa PPF Action Summary ",
        "Type " + entry.getKey(), entry.getValue());
    }
    for (Map.Entry<String, Integer> entry : actionStatuses.entrySet()) {
      addYokogawaMeta("Yokogawa PPF Action Summary ",
        "Status " + entry.getKey(), entry.getValue());
    }

    LinkedHashMap<String, Integer> levels = new LinkedHashMap<String, Integer>();
    LinkedHashMap<String, Integer> titles = new LinkedHashMap<String, Integer>();
    ArrayList<PostProcessLog> nonRoutine = new ArrayList<PostProcessLog>();
    PostProcessLog firstRoutine = null;
    PostProcessLog lastRoutine = null;
    int warningCount = 0;
    int errorCount = 0;
    int fatalCount = 0;
    for (PostProcessLog log : result.logs) {
      String level = clean(log.level);
      incrementCount(levels, level == null ? "Missing" : level);
      if (level != null && level.equalsIgnoreCase("Warning")) {
        warningCount++;
      }
      else if (level != null && level.equalsIgnoreCase("Error")) {
        errorCount++;
      }
      else if (level != null && level.equalsIgnoreCase("Fatal")) {
        fatalCount++;
      }
      if (clean(log.title) != null) {
        incrementCount(titles, clean(log.title));
      }
      if (isRoutinePPFLog(log)) {
        if (firstRoutine == null) {
          firstRoutine = log;
        }
        lastRoutine = log;
      }
      else {
        nonRoutine.add(log);
      }
    }
    String logPrefix = "Yokogawa PPF Log Summary ";
    addYokogawaMeta(logPrefix, "TotalCount", result.logs.size());
    addYokogawaMeta(logPrefix, "NonRoutineCount", nonRoutine.size());
    addYokogawaMeta(logPrefix, "WarningCount", warningCount);
    addYokogawaMeta(logPrefix, "ErrorCount", errorCount);
    addYokogawaMeta(logPrefix, "FatalCount", fatalCount);
    if (!result.logs.isEmpty()) {
      addYokogawaMeta(logPrefix, "FirstTime", result.logs.get(0).timestamp);
      addYokogawaMeta(logPrefix, "LastTime",
        result.logs.get(result.logs.size() - 1).timestamp);
    }
    for (Map.Entry<String, Integer> entry : levels.entrySet()) {
      addYokogawaMeta(logPrefix, "Level " + entry.getKey(), entry.getValue());
    }
    for (Map.Entry<String, Integer> entry : titles.entrySet()) {
      addYokogawaMeta(logPrefix, "Title " + entry.getKey(), entry.getValue());
    }

    ArrayList<PostProcessLog> selected = selectPPFLogs(
      nonRoutine, firstRoutine, lastRoutine, maxProvenanceRecords());
    addYokogawaMeta(logPrefix, "EligibleDetailCount", result.logs.size());
    addYokogawaMeta(logPrefix, "EmittedDetailCount", selected.size());
    addYokogawaMeta(logPrefix, "OmittedDetailCount",
      result.logs.size() - selected.size());
    addYokogawaMeta(logPrefix, "DetailTruncated",
      selected.size() < result.logs.size());
    addYokogawaMeta(rootPrefix, "EmittedLogDetailCount", selected.size());
    addYokogawaMeta(rootPrefix, "OmittedLogDetailCount",
      result.logs.size() - selected.size());
    for (int i=0; i<selected.size(); i++) {
      PostProcessLog log = selected.get(i);
      String prefix = "Yokogawa PPF Log " + (i + 1) + " ";
      addYokogawaMeta(prefix, "DocumentIndex", log.documentIndex);
      addYokogawaMeta(prefix, "Time", log.timestamp);
      addYokogawaMeta(prefix, "Level", log.level);
      addYokogawaMeta(prefix, "Title", log.title);
      addYokogawaMeta(prefix, "Message", sanitizePPFText(log.message));
    }
  }

  private ArrayList<PostProcessLog> selectPPFLogs(
    ArrayList<PostProcessLog> nonRoutine, PostProcessLog firstRoutine,
    PostProcessLog lastRoutine, int limit)
  {
    ArrayList<PostProcessLog> selected = new ArrayList<PostProcessLog>();
    for (PostProcessLog log : nonRoutine) {
      if (selected.size() >= limit) {
        return selected;
      }
      selected.add(log);
    }
    if (selected.size() < limit && firstRoutine != null) {
      selected.add(firstRoutine);
    }
    if (selected.size() < limit && lastRoutine != null &&
      lastRoutine != firstRoutine)
    {
      selected.add(lastRoutine);
    }
    return selected;
  }

  private boolean isRoutinePPFLog(PostProcessLog log) {
    String level = log == null ? null : clean(log.level);
    return level != null && (level.equalsIgnoreCase("Information") ||
      level.equalsIgnoreCase("Info") || level.equalsIgnoreCase("Debug") ||
      level.equalsIgnoreCase("Trace"));
  }

  private void addSanitizedPPFAttributes(String prefix,
    LinkedHashMap<String, String> attributes)
  {
    for (Map.Entry<String, String> entry : attributes.entrySet()) {
      String name = entry.getKey();
      String value = entry.getValue();
      if (name.endsWith("Path")) {
        if (name.equals("ParameterPath")) {
          name = "ParameterFile";
        }
        else {
          name = name.substring(0, name.length() - 4) + "Name";
        }
        value = getPortableFileName(value);
      }
      else if (name.equals("LastErrorMessage")) {
        value = sanitizePPFText(value);
      }
      addYokogawaMeta(prefix, name, value);
    }
  }

  private String sanitizePPFText(String value) {
    String text = clean(value);
    if (text != null && (text.indexOf('\\') >= 0 || text.indexOf('/') >= 0)) {
      return getPortableFileName(text);
    }
    return text;
  }

  private String getPortableFileName(String path) {
    String value = clean(path);
    if (value == null) {
      return null;
    }
    while (value.endsWith("/") || value.endsWith("\\")) {
      value = value.substring(0, value.length() - 1);
    }
    int slash = Math.max(value.lastIndexOf('/'), value.lastIndexOf('\\'));
    return slash < 0 ? value : value.substring(slash + 1);
  }

  private String firstNonNull(String first, String second) {
    return first == null ? second : first;
  }

  private void addMeasurementDataOriginalMetadata() {
    if (measurementPath == null) {
      return;
    }

    try {
      SidecarSummary summary = summarizeSidecar(measurementPath);
      String prefix = "Yokogawa MLF MeasurementData ";
      addYokogawaMeta(prefix, "File", new Location(measurementPath).getName());
      addYokogawaMeta(prefix, "Encoding", "UTF-8");
      addYokogawaMeta(prefix, "ByteLength", summary.byteLength);
      addYokogawaMeta(prefix, "SHA-256", summary.sha256);
    }
    catch (IOException e) {
      LOGGER.debug("Could not summarize CV7000 measurement data {}", measurementPath, e);
    }

    if (measurementDataSummary != null) {
      String prefix = "Yokogawa MLF MeasurementData ";
      addYokogawaMeta(prefix, "Version", measurementDataSummary.version);
      addYokogawaMeta(prefix, "TotalRecordCount",
        measurementDataSummary.totalRecordCount);
      addYokogawaMeta(prefix, "IMGRecordCount",
        measurementDataSummary.imageRecordCount);
      addYokogawaMeta(prefix, "ERRRecordCount",
        measurementDataSummary.errorRecordCount);
      addYokogawaMeta(prefix, "UnknownRecordCount",
        getUnknownMLFRecordCount(measurementDataSummary));
      addYokogawaMeta(prefix, "MissingTypeCount",
        measurementDataSummary.missingTypeCount);
      addYokogawaMeta(prefix, "FirstPlaneTime",
        measurementDataSummary.firstTimestamp);
      addYokogawaMeta(prefix, "LastPlaneTime",
        measurementDataSummary.lastTimestamp);
      addYokogawaMeta(prefix, "FirstPlaneAction",
        measurementDataSummary.firstAction);
      addYokogawaMeta(prefix, "LastPlaneAction",
        measurementDataSummary.lastAction);
      addYokogawaMeta(prefix, "FirstErrorTime",
        measurementDataSummary.firstErrorTimestamp);
      addYokogawaMeta(prefix, "LastErrorTime",
        measurementDataSummary.lastErrorTimestamp);
      addMLFProvenanceMetadata(measurementDataSummary, prefix);
    }
  }

  private int getUnknownMLFRecordCount(MeasurementDataSummary summary) {
    return summary.totalRecordCount - summary.imageRecordCount -
      summary.errorRecordCount - summary.missingTypeCount;
  }

  private void addMLFProvenanceMetadata(MeasurementDataSummary summary,
    String measurementPrefix)
  {
    for (Map.Entry<String, Integer> entry : summary.recordTypeCounts.entrySet()) {
      addYokogawaMeta("Yokogawa MLF Record Types ", entry.getKey() + "Count",
        entry.getValue());
    }
    for (Map.Entry<String, Integer> entry : summary.errorClassCounts.entrySet()) {
      addYokogawaMeta("Yokogawa MLF Error Summary ",
        "Class " + entry.getKey(), entry.getValue());
    }
    for (Map.Entry<String, Integer> entry : summary.errorWellCounts.entrySet()) {
      addYokogawaMeta("Yokogawa MLF Error Summary ",
        "Well " + entry.getKey(), entry.getValue());
    }
    for (Map.Entry<String, Integer> entry :
      summary.errorWellFieldCounts.entrySet())
    {
      addYokogawaMeta("Yokogawa MLF Error Summary ", entry.getKey(),
        entry.getValue());
    }

    int eligible = summary.nonImageRecords.size();
    int emitted = Math.min(eligible, maxProvenanceRecords());
    addYokogawaMeta(measurementPrefix, "EligibleDetailCount", eligible);
    addYokogawaMeta(measurementPrefix, "EmittedDetailCount", emitted);
    addYokogawaMeta(measurementPrefix, "OmittedDetailCount", eligible - emitted);
    addYokogawaMeta(measurementPrefix, "DetailTruncated", emitted < eligible);
    for (int i=0; i<emitted; i++) {
      MeasurementRecord record = summary.nonImageRecords.get(i);
      String scope = "Yokogawa MLF Record " + (i + 1);
      addYokogawaMeta(scope, "Type", record.type);
      addYokogawaMeta(scope, "ErrorClass", record.errorClass);
      addYokogawaMeta(scope, "Time", record.timestamp);
      addYokogawaMeta(scope, "Well", getRecordWell(record));
      addYokogawaMeta(scope, "Row", record.row);
      addYokogawaMeta(scope, "Column", record.column);
      addYokogawaMeta(scope, "FieldIndex", record.field);
      addYokogawaMeta(scope, "TimePoint", record.timepoint);
      addYokogawaMeta(scope, "TimelineIndex", record.timelineIndex);
      addYokogawaMeta(scope, "ActionIndex", record.actionIndex);
      addYokogawaMeta(scope, "Channel", record.channel);
      addYokogawaMeta(scope, "ZIndex", record.zIndex);
      addYokogawaMeta(scope, "X", record.x);
      addYokogawaMeta(scope, "Y", record.y);
      addYokogawaMeta(scope, "Z", record.z);
      addYokogawaMeta(scope, "Action", record.action);
      addYokogawaMeta(scope, "Message", record.message);
    }
  }

  private MessageDigest createSHA256Digest() {
    try {
      return MessageDigest.getInstance("SHA-256");
    }
    catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-256 not available", e);
    }
  }

  private String toHex(byte[] bytes) {
    StringBuilder hex = new StringBuilder(bytes.length * 2);
    for (byte b : bytes) {
      String value = Integer.toHexString(b & 0xff);
      if (value.length() == 1) {
        hex.append('0');
      }
      hex.append(value);
    }
    return hex.toString();
  }

  private void emitYokogawaMapAnnotations(MetadataStore store,
    int plateAnnotationRefStart, boolean hasInstrument)
  {
    AnnotationRefIndexes refs = new AnnotationRefIndexes();
    refs.plate = plateAnnotationRefStart;

    int mapAnnotationIndex = 0;
    for (YokogawaAnnotationGroup group : rawModel.originalMetadata.groups.values()) {
      if (group.isEmpty()) {
        continue;
      }
      String annotationID = "Annotation:CV7000:YokogawaMap:" + mapAnnotationIndex;
      store.setMapAnnotationID(annotationID, mapAnnotationIndex);
      store.setMapAnnotationNamespace(YOKOGAWA_ANNOTATION_NAMESPACE, mapAnnotationIndex);
      store.setMapAnnotationDescription(group.scope, mapAnnotationIndex);
      store.setMapAnnotationValue(group.toMapPairs(), mapAnnotationIndex);
      linkYokogawaMapAnnotation(store, annotationID, group.scope, refs,
        hasInstrument);
      mapAnnotationIndex++;
    }
  }

  private void linkYokogawaMapAnnotation(MetadataStore store,
    String annotationID, String scope, AnnotationRefIndexes refs,
    boolean hasInstrument)
  {
    if (isInstrumentAnnotationScope(scope)) {
      if (hasInstrument) {
        store.setInstrumentAnnotationRef(annotationID, 0, refs.instrument++);
      }
      else {
        store.setPlateAnnotationRef(annotationID, 0, refs.plate++);
      }
      return;
    }

    if (scope != null && scope.startsWith("Yokogawa MLF Record ") &&
      linkMLFRecordAnnotation(store, annotationID, scope, refs))
    {
      return;
    }

    if (isPlaneProvenanceAnnotationScope(scope) &&
      linkPlaneProvenanceAnnotation(store, annotationID, scope, refs))
    {
      return;
    }

    if (isImageOrChannelAnnotationScope(scope) &&
      linkToMatchingSeriesAndChannels(store, annotationID, scope, refs))
    {
      return;
    }

    store.setPlateAnnotationRef(annotationID, 0, refs.plate++);
  }

  private boolean linkMLFRecordAnnotation(MetadataStore store,
    String annotationID, String scope, AnnotationRefIndexes refs)
  {
    Integer index = getIndexedScopeValue(scope, "Record");
    if (index == null || measurementDataSummary == null ||
      index.intValue() < 0 ||
      index.intValue() >= measurementDataSummary.nonImageRecords.size())
    {
      return false;
    }
    MeasurementRecord record =
      measurementDataSummary.nonImageRecords.get(index.intValue());
    if (record.row == null || record.column == null || record.field == null ||
      seriesLayout == null)
    {
      return false;
    }
    Field field = new Field();
    field.row = record.row.intValue() - 1;
    field.column = record.column.intValue() - 1;
    field.field = record.field.intValue() - 1;
    Integer series = seriesLayout.fieldToSeries.get(field);
    if (series == null) {
      return false;
    }
    store.setImageAnnotationRef(annotationID, series.intValue(),
      refs.nextImage(series.intValue()));
    return true;
  }

  private boolean isInstrumentAnnotationScope(String scope) {
    return scope != null && (scope.startsWith("Yokogawa MES LightSource ") ||
      scope.startsWith("Yokogawa LightSource ") ||
      scope.startsWith("Yokogawa OTF "));
  }

  private boolean isImageOrChannelAnnotationScope(String scope) {
    return scope != null && (scope.startsWith("Yokogawa MES Timeline ") ||
      scope.startsWith("Yokogawa MES Channel ") ||
      scope.startsWith("Yokogawa Timeline "));
  }

  private boolean isPlaneProvenanceAnnotationScope(String scope) {
    return scope != null && scope.startsWith("Yokogawa Plane Provenance Image ");
  }

  private boolean linkPlaneProvenanceAnnotation(MetadataStore store,
    String annotationID, String scope, AnnotationRefIndexes refs)
  {
    Integer image = getIndexedScopeValue(scope, "Image");
    if (image == null || image < 0 || image >= getSeriesCount()) {
      return false;
    }
    store.setImageAnnotationRef(annotationID, image, refs.nextImage(image));
    return true;
  }

  private boolean linkToMatchingSeriesAndChannels(MetadataStore store,
    String annotationID, String scope, AnnotationRefIndexes refs)
  {
    Integer timeline = getIndexedScopeValue(scope, "Timeline");
    Integer action = getIndexedScopeValue(scope, "Action");
    Integer channel = getIndexedScopeValue(scope, "Channel");
    boolean channelScoped = channel != null;
    boolean actionScoped = action != null;

    if (!channelScoped && !actionScoped) {
      return false;
    }

    HashSet<Integer> linkedImages = new HashSet<Integer>();
    HashSet<String> linkedChannels = new HashSet<String>();

    for (int series=0; series<getSeriesCount(); series++) {
      if (reversePlaneLookup == null || series >= reversePlaneLookup.length) {
        continue;
      }
      for (int no=0; no<reversePlaneLookup[series].length; no++) {
        Plane plane = lookupPlane(series, no);
        if (!matchesScope(plane, timeline, action, channel)) {
          continue;
        }
        if (linkedImages.add(Integer.valueOf(series))) {
          store.setImageAnnotationRef(annotationID, series,
            refs.nextImage(series));
        }
        if (channelScoped) {
          String key = series + ":" + plane.channelIndex;
          if (linkedChannels.add(key)) {
            store.setChannelAnnotationRef(annotationID, series,
              plane.channelIndex, refs.nextChannel(series, plane.channelIndex));
          }
        }
      }
    }

    return linkedImages.size() > 0 || linkedChannels.size() > 0;
  }

  private boolean matchesScope(Plane plane, Integer timeline, Integer action,
    Integer channel)
  {
    if (plane == null) {
      return false;
    }
    if (timeline != null && plane.timelineIndex != timeline.intValue()) {
      return false;
    }
    if (action != null && plane.actionIndex != action.intValue()) {
      return false;
    }
    return channel == null || plane.channel == channel.intValue();
  }

  private Integer getIndexedScopeValue(String scope, String label) {
    if (scope == null || label == null) {
      return null;
    }
    String token = label + " ";
    int start = scope.indexOf(token);
    if (start < 0) {
      return null;
    }
    start += token.length();
    int end = start;
    while (end < scope.length() && Character.isDigit(scope.charAt(end))) {
      end++;
    }
    if (end == start) {
      return null;
    }
    try {
      return Integer.valueOf(Integer.parseInt(scope.substring(start, end)) - 1);
    }
    catch (NumberFormatException e) {
      return null;
    }
  }

  private void addYokogawaMeta(String prefix, String name, Object value) {
    YokogawaAnnotationGroup group = getYokogawaAnnotationGroup(prefix);
    group.put(name, value);
  }

  private void addYokogawaMetaList(String prefix, String name, Object value) {
    YokogawaAnnotationGroup group = getYokogawaAnnotationGroup(prefix);
    group.putList(name, value);
  }

  private void addPlaneProvenanceOriginalMetadata() {
    if (seriesLayout == null || reversePlaneLookup == null) {
      return;
    }

    HashMap<Integer, ArrayList<DuplicatePlaneCandidate>> duplicateCandidates =
      getDuplicateCandidatesBySeries();
    for (int series=0; series<reversePlaneLookup.length; series++) {
      String prefix = "Yokogawa Plane Provenance Image " + (series + 1) + " ";
      PlaneProvenanceSummary summary = buildPlaneProvenanceSummary(
        series, duplicateCandidates.get(Integer.valueOf(series)));
      Field field = seriesLayout.getField(series);

      addYokogawaMeta(prefix, "SeriesIndex", series);
      addYokogawaMeta(prefix, "ImageIndex", series);
      if (field != null) {
        addYokogawaMeta(prefix, "Well", getWellName(field));
        addYokogawaMeta(prefix, "FieldIndex", field.field + 1);
      }
      addYokogawaMeta(prefix, "PlaneCount", summary.planeCount);
      addYokogawaMeta(prefix, "TiffBackedCount", summary.tiffBackedCount);
      addYokogawaMeta(prefix, "FilledCount", summary.filledCount);
      addYokogawaMeta(prefix, "DuplicatedCount", summary.duplicatedCount);
      addYokogawaMeta(prefix, "MetadataOnlyCount", summary.metadataOnlyCount);
      addYokogawaMeta(prefix, "NoMLFRecordCount", summary.noMLFRecordCount);
      addYokogawaMeta(prefix, "DuplicateCandidateCount",
        summary.duplicateCandidateCount);
      for (String anomaly : summary.anomalies) {
        addYokogawaMetaList(prefix, "Anomaly", anomaly);
      }
    }
  }

  private HashMap<Integer, ArrayList<DuplicatePlaneCandidate>>
    getDuplicateCandidatesBySeries()
  {
    HashMap<Integer, ArrayList<DuplicatePlaneCandidate>> bySeries =
      new HashMap<Integer, ArrayList<DuplicatePlaneCandidate>>();
    if (seriesLayout == null || seriesLayout.duplicateCandidates == null) {
      return bySeries;
    }

    for (DuplicatePlaneCandidate candidate : seriesLayout.duplicateCandidates) {
      if (candidate == null || candidate.candidate == null) {
        continue;
      }
      Integer series = Integer.valueOf(candidate.candidate.series);
      ArrayList<DuplicatePlaneCandidate> candidates = bySeries.get(series);
      if (candidates == null) {
        candidates = new ArrayList<DuplicatePlaneCandidate>();
        bySeries.put(series, candidates);
      }
      candidates.add(candidate);
    }
    return bySeries;
  }

  private PlaneProvenanceSummary buildPlaneProvenanceSummary(int series,
    ArrayList<DuplicatePlaneCandidate> duplicateCandidates)
  {
    PlaneProvenanceSummary summary = new PlaneProvenanceSummary();
    int planeCount = reversePlaneLookup[series].length;
    summary.planeCount = planeCount;

    for (int no=0; no<planeCount; no++) {
      Plane plane = lookupPlane(series, no);
      if (plane != null && plane.file != null) {
        summary.tiffBackedCount++;
        continue;
      }

      String reason;
      if (plane == null) {
        reason = "NO_MLF_RECORD";
        summary.noMLFRecordCount++;
      }
      else {
        reason = "MLF_METADATA_ONLY";
        summary.metadataOnlyCount++;
      }

      Plane duplicate = getDuplicatePlane(series, no);
      if (duplicate != null) {
        summary.duplicatedCount++;
        summary.anomalies.add(formatPlaneProvenanceAnomaly(
          series, no, "DUPLICATED", reason, duplicate, null));
      }
      else {
        summary.filledCount++;
        summary.anomalies.add(formatPlaneProvenanceAnomaly(
          series, no, "FILL", reason, null, null));
      }
    }

    if (duplicateCandidates != null) {
      summary.duplicateCandidateCount = duplicateCandidates.size();
      for (DuplicatePlaneCandidate candidate : duplicateCandidates) {
        if (candidate == null || candidate.candidate == null) {
          continue;
        }
        summary.anomalies.add(formatPlaneProvenanceAnomaly(
          series, candidate.candidate.no, "DUPLICATE_CANDIDATE_IGNORED",
          candidate.reason, candidate.selected, candidate.candidate));
      }
    }
    return summary;
  }

  private Plane getDuplicatePlane(int series, int no) {
    if (!duplicatePlanes()) {
      return null;
    }

    int[] zct = getLogicalZCTCoords(series, no);
    return findFirstBackedPlaneInChannel(
      planeData, reversePlaneLookup[series], series, zct[1], no);
  }

  /** Select the lowest-index backed plane from one logical channel. */
  static Plane findFirstBackedPlaneInChannel(ArrayList<Plane> planes,
    int[] lookup, int series, int channel, int excludedPlane)
  {
    if (planes == null || lookup == null) {
      return null;
    }
    for (int no=0; no<lookup.length; no++) {
      if (no == excludedPlane) {
        continue;
      }
      int index = lookup[no];
      if (index < 0 || index >= planes.size()) {
        continue;
      }
      Plane plane = planes.get(index);
      if (plane != null && plane.series == series && plane.no == no &&
        plane.channelIndex == channel && plane.file != null)
      {
        return plane;
      }
    }
    return null;
  }

  /** Match repeated raw-channel metadata by occurrence, reusing a sole row. */
  static Channel selectChannelDefinition(List<Channel> definitions,
    int occurrence)
  {
    if (definitions == null || definitions.size() == 0) {
      return null;
    }
    if (definitions.size() == 1) {
      return definitions.get(0);
    }
    return occurrence >= 0 && occurrence < definitions.size() ?
      definitions.get(occurrence) : null;
  }

  private int getLogicalChannelCount(int series) {
    return core.get(series).sizeC / reader.getSizeC();
  }

  private int[] getLogicalZCTCoords(int series, int no) {
    CoreMetadata metadata = core.get(series);
    return FormatTools.getZCTCoords(metadata.dimensionOrder,
      metadata.sizeZ, getLogicalChannelCount(series), metadata.sizeT,
      metadata.imageCount, no);
  }

  private int getLogicalPlaneIndex(int series, int z, int c, int t) {
    CoreMetadata metadata = core.get(series);
    return FormatTools.getIndex(metadata.dimensionOrder,
      metadata.sizeZ, getLogicalChannelCount(series), metadata.sizeT,
      metadata.imageCount, z, c, t);
  }

  private String formatPlaneProvenanceAnomaly(int series, int no, String status,
    String reason, Plane source, Plane anomalyPlane)
  {
    int[] zct = getLogicalZCTCoords(series, no);
    StringBuilder row = new StringBuilder();
    row.append("no=").append(no);
    row.append(";z=").append(zct[0]);
    row.append(";c=").append(zct[1]);
    row.append(";t=").append(zct[2]);
    row.append(";status=").append(status);
    row.append(";reason=").append(reason);
    if (source != null) {
      row.append(";sourceNo=").append(source.no);
      if (source.file != null) {
        row.append(";file=").append(new Location(source.file).getName());
      }
    }
    else if (anomalyPlane != null && anomalyPlane.file != null) {
      row.append(";file=").append(new Location(anomalyPlane.file).getName());
    }
    return row.toString();
  }

  private String getWellName(Field field) {
    if (field == null) {
      return null;
    }
    return getRowName(field.row) + String.format("%02d", field.column + 1);
  }

  private String getRowName(int row) {
    StringBuilder name = new StringBuilder();
    int value = row;
    do {
      name.insert(0, (char) ('A' + (value % 26)));
      value = (value / 26) - 1;
    }
    while (value >= 0);
    return name.toString();
  }

  private YokogawaAnnotationGroup getYokogawaAnnotationGroup(String prefix) {
    String scope = clean(prefix);
    if (scope == null) {
      scope = "Yokogawa";
    }
    YokogawaAnnotationGroup group = rawModel.originalMetadata.groups.get(scope);
    if (group == null) {
      group = new YokogawaAnnotationGroup(scope);
      rawModel.originalMetadata.groups.put(scope, group);
    }
    return group;
  }

  private void addYokogawaAttributes(String prefix, Attributes attributes) {
    if (attributes == null) {
      return;
    }
    for (int i=0; i<attributes.getLength(); i++) {
      addYokogawaMeta(prefix,
        parsing.getYokogawaAttributeName(attributes.getQName(i)),
        attributes.getValue(i));
    }
  }

  private Integer getLinkedStandardLightSource(Channel channel) {
    if (channel == null ||
      channel.lightSourceRefs == null || lightSources == null)
    {
      return null;
    }
    for (Integer lightSource : channel.lightSourceRefs) {
      if (lightSource == null || lightSource < 0 || lightSource >= lightSources.size()) {
        continue;
      }
      LightSource source = lightSources.get(lightSource);
      if ((!channel.isBrightfield() && isLaser(source)) ||
        (channel.isBrightfield() && isLamp(source)))
      {
        return lightSource;
      }
    }
    return null;
  }

  private CrosstalkFilter getCrosstalkFilter(Channel channel) {
    if (channel == null || crosstalkParameters == null) {
      return null;
    }
    return crosstalkParameters.filters.get(new CrosstalkFilterKey(
      channel.filterID, Integer.valueOf(channel.cameraNumber), channel.acquisition));
  }

  private <K> void incrementCount(LinkedHashMap<K, Integer> counts, K key) {
    if (counts == null || key == null) {
      return;
    }
    Integer count = counts.get(key);
    counts.put(key, Integer.valueOf(count == null ? 1 : count.intValue() + 1));
  }

  private String getErrorClass(MeasurementRecord record) {
    String message = record == null ? null : clean(record.message);
    if (message != null && message.regionMatches(true, 0, "AF Error", 0, 8)) {
      return "AF Error";
    }
    return "Other";
  }

  private String getRecordWell(MeasurementRecord record) {
    if (record == null || record.row == null || record.column == null ||
      record.row.intValue() <= 0 || record.column.intValue() <= 0)
    {
      return null;
    }
    return getRowName(record.row.intValue() - 1) +
      String.format("%02d", record.column.intValue());
  }

  private Length getPhysicalSizeZ(int series) {
    Double physicalSizeZ = null;
    boolean foundChannel = false;
    for (int c=0; c<getSizeC(); c++) {
      Plane p = lookupRepresentativePlane(series, c);
      if (p == null) {
        continue;
      }
      Channel channel = lookupChannel(p);
      if (channel == null || channel.physicalSizeZ == null ||
        channel.physicalSizeZ <= 0)
      {
        return null;
      }
      foundChannel = true;
      if (physicalSizeZ == null) {
        physicalSizeZ = channel.physicalSizeZ;
      }
      else if (Math.abs(physicalSizeZ - channel.physicalSizeZ) > 0.000001) {
        return null;
      }
    }
    return foundChannel ? FormatTools.getPhysicalSizeZ(physicalSizeZ) : null;
  }

  private Plane lookupFirstBackedPlane(int series) {
    for (int no=0; no<reversePlaneLookup[series].length; no++) {
      Plane p = lookupPlane(series, no);
      if (p != null && p.file != null) {
        return p;
      }
    }
    return null;
  }

  private Plane lookupRepresentativePlane(int series, int channel) {
    if (seriesLayout != null) {
      Plane indexed = seriesLayout.getRepresentativePlane(series, channel);
      if (indexed != null) {
        return indexed;
      }
    }

    Plane metadataPlane = null;
    for (int no=0; no<reversePlaneLookup[series].length; no++) {
      Plane p = lookupPlane(series, no);
      if (p != null && p.file != null && p.channelIndex == channel) {
        return p;
      }
      if (p != null && p.channelIndex == channel && metadataPlane == null) {
        metadataPlane = p;
      }
    }
    return metadataPlane;
  }

  private String readSanitizedXML(String filename) throws IOException {
    return parsing.readSanitizedXML(filename);
  }

  private String getWellKey(int row, int column) {
    return row + "-" + column;
  }

  private Plane lookupPlane(int series, int no) {
    int index = reversePlaneLookup[series][no];
    LOGGER.trace("lookupPlane(series={}, no={}), index = {}", series, no, index);
    if (index < 0 || index >= planeData.size()) {
      return null;
    }
    Plane p = planeData.get(index);
    if (p.series != series || p.no != no) {
      return null;
    }
    return p;
  }

  // ###############
  // ## Module 10: Data Model Classes
  // ###############

  private enum CV7000FileRole {
    TIFF_PLANE,
    SHADING_CORRECTION,
    WPI,
    MEASUREMENT_DATA,
    MEASUREMENT_DETAIL,
    MEASUREMENT_SETTINGS,
    WELL_PLATE_PRODUCT,
    POST_PROCESS,
    OTF_CROSSTALK,
    OTF_GEOMETRY,
    UNKNOWN
  }

  public enum CV7000ChannelMappingMode {
    ACTION_MAPPED,
    RAW_MLF
  }

  /** Centralizes Yokogawa action/channel matching and reader channel indexing. */
  private static class CV7000ChannelMapper {
    public int getLogicalChannelIndex(ArrayList<Channel> channels, Plane plane,
      CV7000ChannelMappingMode mode)
    {
      if (mode == CV7000ChannelMappingMode.RAW_MLF || channels == null) {
        return plane.channel;
      }
      return getActionMappedChannelIndex(channels, plane);
    }

    public int getActionMappedChannelIndex(ArrayList<Channel> channels, Plane plane) {
      int index = -1;
      for (int action=0; action<=plane.actionIndex; action++) {
        for (Channel channel : channels) {
          if (channel.timelineIndex == plane.timelineIndex &&
            channel.actionIndex == action)
          {
            index++;
            if (channel.index == plane.channel &&
              channel.actionIndex == plane.actionIndex)
            {
              return index;
            }
          }
        }
      }
      return CHANNEL_NOT_FOUND;
    }

    public Channel lookupChannel(CV7000RawModel rawModel,
      ArrayList<Channel> channels, Plane plane)
    {
      if (plane == null) {
        return null;
      }
      Channel matched = rawModel.getChannel(
        plane.timelineIndex, plane.actionIndex, plane.channel);
      if (matched != null) {
        return matched;
      }

      if (channels == null) {
        return null;
      }

      Channel rawChannel = null;
      Channel populatedRawChannel = null;
      for (Channel channel : channels) {
        if (channel.index == plane.channel &&
          channel.timelineIndex == plane.timelineIndex &&
          channel.actionIndex == plane.actionIndex)
        {
          return channel;
        }
        if (channel.index == plane.channel) {
          if (rawChannel == null) {
            rawChannel = channel;
          }
          if (populatedRawChannel == null && channel.hasChannelSettings()) {
            populatedRawChannel = channel;
          }
        }
      }
      Channel fallback =
        populatedRawChannel == null ? rawChannel : populatedRawChannel;
      if (fallback != null && rawModel.markRawChannelFallback(plane)) {
        LOGGER.warn("Falling back to CV7000 raw channel metadata for " +
          "timeline {}, action {}, raw channel {}; no exact acquisition " +
          "metadata is available", plane.timelineIndex + 1,
          plane.actionIndex + 1, plane.channel + 1);
      }
      return fallback;
    }
  }

  /** Resolved dataset paths used during the parsing phase. */
  public static class CV7000DatasetPaths {
    public String parentPath;
    public String wpiPath;
    public String measurementDataPath;
    public String measurementDetailPath;
    public String postProcessPath;
    public String otfCrosstalkPath;
    public String otfGeometryPath;
  }

  /** Parsed Yokogawa sidecars and reader-local provenance. */
  public static class CV7000RawModel {
    public YokogawaOriginalMetadata originalMetadata =
      new YokogawaOriginalMetadata();
    public HashMap<ChannelKey, Channel> channelsByAcquisition =
      new HashMap<ChannelKey, Channel>();
    private HashSet<ChannelKey> warnedRawChannelFallbacks =
      new HashSet<ChannelKey>();

    public void indexChannels(ArrayList<Channel> channels) {
      channelsByAcquisition.clear();
      warnedRawChannelFallbacks.clear();
      if (channels == null) {
        return;
      }
      for (Channel channel : channels) {
        ChannelKey key = new ChannelKey(
          channel.timelineIndex, channel.actionIndex, channel.index);
        if (!channelsByAcquisition.containsKey(key)) {
          channelsByAcquisition.put(key, channel);
        }
      }
    }

    public Channel getChannel(int timelineIndex, int actionIndex, int rawChannel) {
      return channelsByAcquisition.get(
        new ChannelKey(timelineIndex, actionIndex, rawChannel));
    }

    private boolean markRawChannelFallback(Plane plane) {
      return warnedRawChannelFallbacks.add(new ChannelKey(
        plane.timelineIndex, plane.actionIndex, plane.channel));
    }
  }

  /** Grouped Yokogawa metadata pending OME MapAnnotation emission. */
  public static class YokogawaOriginalMetadata {
    public LinkedHashMap<String, YokogawaAnnotationGroup> groups =
      new LinkedHashMap<String, YokogawaAnnotationGroup>();
  }

  /** Intermediate layout data used to translate Yokogawa records into series. */
  public static class CV7000SeriesLayout {
    public String firstFile;
    public ArrayList<Field> acquiredFields = new ArrayList<Field>();
    public HashMap<Field, MinMax> minMax = new HashMap<Field, MinMax>();
    public Integer[] channelIndexes;
    public ArrayList<ChannelSlotKey> channelSlots =
      new ArrayList<ChannelSlotKey>();
    public HashMap<ChannelSlotKey, Integer> channelSlotIndexes =
      new HashMap<ChannelSlotKey, Integer>();
    public HashMap<Field, Integer> fieldToSeries = new HashMap<Field, Integer>();
    public int[][] reversePlaneLookup;
    public CV7000ChannelMappingMode channelMappingMode =
      CV7000ChannelMappingMode.ACTION_MAPPED;
    public LinkedHashMap<String, ArrayList<Field>> fieldsByWell =
      new LinkedHashMap<String, ArrayList<Field>>();
    public HashMap<String, Boolean> acquiredWells =
      new HashMap<String, Boolean>();
    public ArrayList<DuplicatePlaneCandidate> duplicateCandidates =
      new ArrayList<DuplicatePlaneCandidate>();
    public HashMap<String, Plane> representativePlanes =
      new HashMap<String, Plane>();

    public void indexPlane(Plane plane) {
      if (plane == null) {
        return;
      }
      String key = getPlaneKey(plane.series, plane.channelIndex);
      Plane existing = representativePlanes.get(key);
      if (existing == null || (existing.file == null && plane.file != null)) {
        representativePlanes.put(key, plane);
      }
    }

    public Plane getRepresentativePlane(int series, int channel) {
      return representativePlanes.get(getPlaneKey(series, channel));
    }

    public Field getField(int series) {
      if (series < 0 || series >= acquiredFields.size()) {
        return null;
      }
      return acquiredFields.get(series);
    }

    private String getPlaneKey(int series, int channel) {
      return series + ":" + channel;
    }
  }

  /** Logical channel identity, with an optional timeline collision qualifier. */
  public static class ChannelSlotKey implements Comparable<ChannelSlotKey> {
    public CV7000ChannelMappingMode mappingMode;
    public int mappedIndex = -1;
    public int actionIndex = -1;
    public int rawChannel = -1;
    public int timelineIndex = -1;
    public boolean timelineQualified;

    public ChannelSlotKey() {
    }

    public ChannelSlotKey(ChannelSlotKey key) {
      mappingMode = key.mappingMode;
      mappedIndex = key.mappedIndex;
      actionIndex = key.actionIndex;
      rawChannel = key.rawChannel;
      timelineIndex = key.timelineIndex;
      timelineQualified = key.timelineQualified;
    }

    public String identity() {
      if (mappingMode == CV7000ChannelMappingMode.ACTION_MAPPED) {
        return "mapped=" + mappedIndex;
      }
      return "action=" + actionIndex + ":raw=" + rawChannel;
    }

    public int compareBase(ChannelSlotKey other) {
      if (mappingMode == CV7000ChannelMappingMode.ACTION_MAPPED &&
        other.mappingMode == CV7000ChannelMappingMode.ACTION_MAPPED)
      {
        return Integer.compare(mappedIndex, other.mappedIndex);
      }
      int action = Integer.compare(actionIndex, other.actionIndex);
      return action == 0 ? Integer.compare(rawChannel, other.rawChannel) : action;
    }

    @Override
    public int compareTo(ChannelSlotKey other) {
      return compareBase(other);
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof ChannelSlotKey)) {
        return false;
      }
      ChannelSlotKey key = (ChannelSlotKey) o;
      if (mappingMode != key.mappingMode ||
        timelineQualified != key.timelineQualified ||
        timelineIndex != key.timelineIndex)
      {
        return false;
      }
      if (mappingMode == CV7000ChannelMappingMode.ACTION_MAPPED) {
        return mappedIndex == key.mappedIndex;
      }
      return actionIndex == key.actionIndex && rawChannel == key.rawChannel;
    }

    @Override
    public int hashCode() {
      int code = mappingMode == null ? 0 : mappingMode.hashCode();
      if (mappingMode == CV7000ChannelMappingMode.ACTION_MAPPED) {
        code = 31 * code + mappedIndex;
      }
      else {
        code = 31 * code + actionIndex;
        code = 31 * code + rawChannel;
      }
      code = 31 * code + timelineIndex;
      return 31 * code + (timelineQualified ? 1 : 0);
    }

    @Override
    public String toString() {
      String value = identity();
      return timelineQualified ? value + ":timeline=" + timelineIndex : value;
    }
  }

  public static class ChannelKey {
    public int timelineIndex;
    public int actionIndex;
    public int channelIndex;

    public ChannelKey() {
    }

    public ChannelKey(int timelineIndex, int actionIndex, int channelIndex) {
      this.timelineIndex = timelineIndex;
      this.actionIndex = actionIndex;
      this.channelIndex = channelIndex;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof ChannelKey)) {
        return false;
      }
      ChannelKey key = (ChannelKey) o;
      return timelineIndex == key.timelineIndex &&
        actionIndex == key.actionIndex && channelIndex == key.channelIndex;
    }

    @Override
    public int hashCode() {
      int code = 17;
      code = 31 * code + timelineIndex;
      code = 31 * code + actionIndex;
      code = 31 * code + channelIndex;
      return code;
    }
  }

  /** Shared Yokogawa parsing and normalization helpers. */
  public static class YokogawaParsing {
    public String readSanitizedXML(String filename) throws IOException {
      String xml = DataTools.readFile(filename).trim();
      if (xml.endsWith(">>")) {
        xml = xml.substring(0, xml.length() - 1);
      }
      return xml;
    }

    public String clean(String value) {
      if (value == null) {
        return null;
      }
      String trimmed = value.trim();
      return trimmed.length() == 0 ? null : trimmed;
    }

    public String getYokogawaAttributeName(String qName) {
      if (qName == null) {
        return "";
      }
      int colon = qName.indexOf(":");
      return colon < 0 ? qName : qName.substring(colon + 1);
    }

    public boolean isElement(String qName, String name) {
      return name != null && name.equals(getYokogawaAttributeName(qName));
    }

    public boolean elementStartsWith(String qName, String prefix) {
      return prefix != null &&
        getYokogawaAttributeName(qName).startsWith(prefix);
    }

    public Integer parseInteger(String value) {
      if (value == null || value.trim().length() == 0) {
        return null;
      }
      return Integer.valueOf(value.trim());
    }

    public int parseRequiredInteger(String value) {
      Integer parsed = parseInteger(value);
      if (parsed == null) {
        throw new NumberFormatException("Missing required integer value");
      }
      return parsed.intValue();
    }

    public Double parseDouble(String value) {
      return DataTools.parseDouble(value == null ? null : value.trim());
    }

    public Double parseYokogawaGain(String value) {
      if (value == null) {
        return null;
      }
      String trimmed = value.trim();
      if (!trimmed.regionMatches(true, 0, "gain", 0, 4)) {
        return null;
      }
      int start = -1;
      for (int i=4; i<trimmed.length(); i++) {
        char ch = trimmed.charAt(i);
        if (Character.isDigit(ch) || ch == '+' || ch == '-' ||
          ch == '.' || ch == ',')
        {
          start = i;
          break;
        }
      }
      if (start < 0) {
        return null;
      }
      int end = start + 1;
      while (end < trimmed.length()) {
        char ch = trimmed.charAt(end);
        if (!(Character.isDigit(ch) || ch == '+' || ch == '-' ||
          ch == '.' || ch == ',' || ch == 'e' || ch == 'E'))
        {
          break;
        }
        end++;
      }
      return parseDouble(trimmed.substring(start, end));
    }

    public DetectionFilter parseDetectionFilter(String acquisition) {
      if (acquisition == null) {
        return null;
      }
      String trimmed = acquisition.trim();
      try {
        if (trimmed.startsWith("BP") && trimmed.indexOf("/") > 2) {
          String center = trimmed.substring(2, trimmed.indexOf("/"));
          String width = trimmed.substring(trimmed.indexOf("/") + 1);
          Double parsedCenter = parseDouble(center);
          Double parsedWidth = parseDouble(width);
          if (parsedCenter != null && parsedWidth != null) {
            return DetectionFilter.bandPass(parsedCenter, parsedWidth);
          }
        }
      }
      catch (RuntimeException e) {
        LOGGER.debug("Ignoring invalid CV7000 detection filter value {}", acquisition, e);
      }
      return null;
    }

    public String getBinningValue(String binning) {
      if (binning == null || binning.trim().length() == 0) {
        return null;
      }
      if (binning.indexOf('x') >= 0 || binning.indexOf('X') >= 0) {
        return binning;
      }
      return binning + "x" + binning;
    }

    public String getRawAttribute(Attributes attributes, String name) {
      if (attributes == null || name == null) {
        return null;
      }
      for (int i=0; i<attributes.getLength(); i++) {
        if (name.equals(getYokogawaAttributeName(attributes.getQName(i)))) {
          return attributes.getValue(i);
        }
      }
      return null;
    }

    public String getAttribute(Attributes attributes, String name) {
      return clean(getRawAttribute(attributes, name));
    }

    public void copyAttributes(Attributes attributes,
      Map<String, String> values)
    {
      if (attributes == null || values == null) {
        return;
      }
      for (int i=0; i<attributes.getLength(); i++) {
        values.put(getYokogawaAttributeName(attributes.getQName(i)),
          attributes.getValue(i));
      }
    }
  }

  /** OME instrument indexes shared by channel metadata population. */
  private static class InstrumentMetadataIndexes {
    public String instrument;
    public HashMap<Integer, Integer> lightSourceIndexes =
      new HashMap<Integer, Integer>();
    public HashMap<Integer, Integer> detectorIndexes =
      new HashMap<Integer, Integer>();
    public HashMap<FilterKey, Integer> filterIndexes =
      new HashMap<FilterKey, Integer>();
    public HashMap<String, Integer> dichroicIndexes =
      new HashMap<String, Integer>();
    public List<String> usedObjectiveIDs = new ArrayList<String>();
  }

  private static class AnnotationRefIndexes {
    public int plate;
    public int instrument;
    private HashMap<Integer, Integer> imageRefs =
      new HashMap<Integer, Integer>();
    private HashMap<String, Integer> channelRefs =
      new HashMap<String, Integer>();

    public int nextImage(int series) {
      Integer key = Integer.valueOf(series);
      Integer next = imageRefs.get(key);
      int value = next == null ? 0 : next.intValue();
      imageRefs.put(key, Integer.valueOf(value + 1));
      return value;
    }

    public int nextChannel(int series, int channel) {
      String key = series + ":" + channel;
      Integer next = channelRefs.get(key);
      int value = next == null ? 0 : next.intValue();
      channelRefs.put(key, Integer.valueOf(value + 1));
      return value;
    }
  }

  private static class PlaneProvenanceSummary {
    public int planeCount;
    public int tiffBackedCount;
    public int filledCount;
    public int duplicatedCount;
    public int metadataOnlyCount;
    public int noMLFRecordCount;
    public int duplicateCandidateCount;
    public ArrayList<String> anomalies = new ArrayList<String>();
  }

  public static class DuplicatePlaneCandidate {
    public Plane candidate;
    public Plane selected;
    public String reason;

    public DuplicatePlaneCandidate() {
    }

    public DuplicatePlaneCandidate(Plane candidate, Plane selected,
      String reason)
    {
      this.candidate = candidate;
      this.selected = selected;
      this.reason = reason;
    }
  }

  public static class YokogawaAnnotationGroup {
    public String scope;
    public LinkedHashMap<String, String> values =
      new LinkedHashMap<String, String>();
    public HashMap<String, Integer> listIndexes =
      new HashMap<String, Integer>();

    public YokogawaAnnotationGroup() {
    }

    public YokogawaAnnotationGroup(String scope) {
      this.scope = scope;
    }

    public boolean isEmpty() {
      return values.isEmpty();
    }

    public void put(String name, Object value) {
      String key = cleanText(name);
      String text = cleanValue(value);
      if (key == null || text == null) {
        return;
      }
      if (values.containsKey(key)) {
        putList(key, text);
      }
      else {
        values.put(key, text);
      }
    }

    public void putList(String name, Object value) {
      String key = cleanText(name);
      String text = cleanValue(value);
      if (key == null || text == null) {
        return;
      }
      Integer next = listIndexes.get(key);
      int index = next == null ? 1 : next.intValue() + 1;
      listIndexes.put(key, Integer.valueOf(index));
      values.put(key + "[" + index + "]", text);
    }

    public List<MapPair> toMapPairs() {
      List<MapPair> pairs = new ArrayList<MapPair>();
      pairs.add(new MapPair("Source", scope));
      for (Map.Entry<String, String> entry : values.entrySet()) {
        pairs.add(new MapPair(entry.getKey(), entry.getValue()));
      }
      return pairs;
    }

    private String cleanValue(Object value) {
      if (value == null) {
        return null;
      }
      String text = String.valueOf(value);
      return text.trim().length() == 0 ? null : text;
    }

    private String cleanText(String value) {
      if (value == null) {
        return null;
      }
      String trimmed = value.trim();
      return trimmed.length() == 0 ? null : trimmed;
    }
  }

  private static class SeriesTiming {
    public Long startMillis;
    public String startTimestamp;
  }

  private static class SidecarSummary {
    public long byteLength;
    public String sha256;

    public SidecarSummary(long byteLength, String sha256) {
      this.byteLength = byteLength;
      this.sha256 = sha256;
    }
  }

  private static class MeasurementDetailResult {
    public ArrayList<Channel> channels = new ArrayList<Channel>();
    public String wppPath;
    public String settingsPath;
    public String startTime;
    public String endTime;
    public String measurementOperatorName;
    public String targetSystem;
  }

  private static class MeasurementSettingsResult {
    public ArrayList<LightSource> lightSources = new ArrayList<LightSource>();
    public ArrayList<Channel> channels = new ArrayList<Channel>();
  }

  public static class MeasurementDataSummary {
    public String version;
    public int totalRecordCount;
    public int imageRecordCount;
    public int errorRecordCount;
    public int missingTypeCount;
    public LinkedHashMap<String, Integer> recordTypeCounts =
      new LinkedHashMap<String, Integer>();
    public LinkedHashMap<String, Integer> errorClassCounts =
      new LinkedHashMap<String, Integer>();
    public LinkedHashMap<String, Integer> errorWellCounts =
      new LinkedHashMap<String, Integer>();
    public LinkedHashMap<String, Integer> errorWellFieldCounts =
      new LinkedHashMap<String, Integer>();
    public ArrayList<MeasurementRecord> nonImageRecords =
      new ArrayList<MeasurementRecord>();
    public String firstTimestamp;
    public String lastTimestamp;
    public String firstAction;
    public String lastAction;
    public String firstErrorTimestamp;
    public String lastErrorTimestamp;
  }

  public static class MeasurementRecord {
    public String type;
    public String timestamp;
    public String action;
    public String message;
    public String errorClass;
    public Integer row;
    public Integer column;
    public Integer field;
    public Integer timepoint;
    public Integer timelineIndex;
    public Integer actionIndex;
    public Integer channel;
    public Integer zIndex;
    public Double x;
    public Double y;
    public Double z;
  }

  private static class PostProcessResult {
    public LinkedHashMap<String, String> root =
      new LinkedHashMap<String, String>();
    public ArrayList<PostProcessAction> actions =
      new ArrayList<PostProcessAction>();
    public ArrayList<PostProcessLog> logs = new ArrayList<PostProcessLog>();
  }

  private static class PostProcessAction {
    public String type;
    public LinkedHashMap<String, String> attributes =
      new LinkedHashMap<String, String>();
  }

  private static class PostProcessLog {
    public int documentIndex;
    public String timestamp;
    public String level;
    public String title;
    public String message;
    public LinkedHashMap<String, String> attributes =
      new LinkedHashMap<String, String>();
  }

  // ###############
  // ## Module 11: SAX Sidecar Handlers
  // ###############

  private class WPIHandler extends BaseHandler {
    private int plateRows;
    private int plateColumns;
    private String name;
    private String plateID;

    public int getPlateRows() {
      return plateRows;
    }

    public int getPlateColumns() {
      return plateColumns;
    }

    public String getPlateName() {
      return name;
    }

    public String getPlateID() {
      return plateID;
    }

    @Override
    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      if (parsing.isElement(qName, "WellPlate")) {
        addYokogawaAttributes("Yokogawa WPI WellPlate ", attributes);
        name = parsing.getRawAttribute(attributes, "Name");
        plateID = parsing.getRawAttribute(attributes, "ProductID");
        plateRows = parsing.parseRequiredInteger(
          parsing.getRawAttribute(attributes, "Rows"));
        plateColumns = parsing.parseRequiredInteger(
          parsing.getRawAttribute(attributes, "Columns"));
      }
    }

  }

  private class MeasurementDataHandler extends BaseHandler {
    private StringBuffer currentValue = new StringBuffer();
    private String btsType;
    private MeasurementRecord currentRecord;
    private ArrayList<Plane> planes = new ArrayList<Plane>();
    private String parentDir;
    private MeasurementDataSummary summary = new MeasurementDataSummary();

    public MeasurementDataHandler(String parentDir) {
      super();
      this.parentDir = parentDir;
    }

    public ArrayList<Plane> getPlanes() {
      return planes;
    }

    public MeasurementDataSummary getSummary() {
      return summary;
    }

    // -- DefaultHandler API methods --

    @Override
    public void characters(char[] ch, int start, int length) {
      String value = new String(ch, start, length);
      currentValue.append(value);
    }

    @Override
    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      currentValue.setLength(0);

      try {
        String element = parsing.getYokogawaAttributeName(qName);
        if (parsing.isElement(qName, "MeasurementData")) {
          summary.version = parsing.getAttribute(attributes, "Version");
          return;
        }
        if (!element.equals("MeasurementRecord")) {
          return;
        }
        btsType = parsing.getAttribute(attributes, "Type");
        summary.totalRecordCount++;
        if (btsType == null) {
          summary.missingTypeCount++;
          incrementCount(summary.recordTypeCounts, "Missing");
          currentRecord = parseNonImageRecord(attributes, null);
          return;
        }
        incrementCount(summary.recordTypeCounts, btsType);
        if (btsType.equals("IMG")) {
          // When the instrument is recording an acquisition error the "type"
          // will be "ERR" so we can skip those.
          Plane p = new Plane();
          p.field = new Field();
          p.field.row = parsing.parseRequiredInteger(
            parsing.getRawAttribute(attributes, "Row")) - 1;
          p.field.column = parsing.parseRequiredInteger(
            parsing.getRawAttribute(attributes, "Column")) - 1;
          p.timepoint = parsing.parseRequiredInteger(
            parsing.getRawAttribute(attributes, "TimePoint")) - 1;
          p.field.field = parsing.parseRequiredInteger(
            parsing.getRawAttribute(attributes, "FieldIndex")) - 1;
          p.z = parsing.parseRequiredInteger(
            parsing.getRawAttribute(attributes, "ZIndex")) - 1;
          p.channel = parsing.parseRequiredInteger(
            parsing.getRawAttribute(attributes, "Ch")) - 1;
          p.actionIndex = parsing.parseRequiredInteger(
            parsing.getRawAttribute(attributes, "ActionIndex")) - 1;
          p.timelineIndex = parsing.parseRequiredInteger(
            parsing.getRawAttribute(attributes, "TimelineIndex")) - 1;

          p.xpos = parsing.parseDouble(parsing.getRawAttribute(attributes, "X"));
          p.ypos = parsing.parseDouble(parsing.getRawAttribute(attributes, "Y"));
          p.zpos = parsing.parseDouble(parsing.getRawAttribute(attributes, "Z"));
          p.timestamp = parsing.getRawAttribute(attributes, "Time");
          p.actionName = parsing.getRawAttribute(attributes, "Action");
          summary.imageRecordCount++;
          if (summary.firstTimestamp == null) {
            summary.firstTimestamp = p.timestamp;
            summary.firstAction = p.actionName;
          }
          summary.lastTimestamp = p.timestamp;
          summary.lastAction = p.actionName;
          planes.add(p);
        }
        else {
          currentRecord = parseNonImageRecord(attributes, btsType);
          if (btsType.equals("ERR")) {
            summary.errorRecordCount++;
            if (summary.firstErrorTimestamp == null) {
              summary.firstErrorTimestamp = currentRecord.timestamp;
            }
            summary.lastErrorTimestamp = currentRecord.timestamp;
          }
        }
      }
      catch (RuntimeException e) {
        if (LOGGER.isErrorEnabled()) {
          Map<String, String> attributeMap = new HashMap<String, String>();
          for (int i = 0; i < attributes.getLength(); i++) {
            attributeMap.put(
                attributes.getQName(i), attributes.getValue(i));
          }
          LOGGER.error("Could not parse CV7000 {} MeasurementRecord " +
            "attributes: {}", MEASUREMENT_FILE, attributeMap, e);
        }
        throw e;
      }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
      String value = currentValue.toString();
      if (!parsing.isElement(qName, "MeasurementRecord")) {
        return;
      }
      if ("IMG".equals(btsType) &&
        value.trim().length() > 0) {
        Location imgFile = new Location(parentDir, value);
        if (imgFile.exists() && imgFile.canRead() && !imgFile.isDirectory() &&
          isTiffFile(imgFile.getName()))
        {
          String path = imgFile.getAbsolutePath();
          planes.get(planes.size() - 1).file = path;
          planeFiles.add(path);
        }
      }
      else if (currentRecord != null) {
        currentRecord.message = clean(value);
        currentRecord.errorClass = getErrorClass(currentRecord);
        summary.nonImageRecords.add(currentRecord);
        if ("ERR".equals(currentRecord.type)) {
          incrementCount(summary.errorClassCounts, currentRecord.errorClass);
          String well = getRecordWell(currentRecord);
          if (well != null) {
            incrementCount(summary.errorWellCounts, well);
            if (currentRecord.field != null) {
              incrementCount(summary.errorWellFieldCounts,
                well + "/Field " + currentRecord.field);
            }
          }
        }
      }
      currentRecord = null;
      btsType = null;
    }

    private MeasurementRecord parseNonImageRecord(Attributes attributes,
      String type)
    {
      MeasurementRecord record = new MeasurementRecord();
      record.type = type == null ? "Missing" : type;
      record.timestamp = parsing.getAttribute(attributes, "Time");
      record.action = parsing.getAttribute(attributes, "Action");
      record.row = parseOptionalOneBasedInteger(attributes, "Row");
      record.column = parseOptionalOneBasedInteger(attributes, "Column");
      record.field = parseOptionalOneBasedInteger(attributes, "FieldIndex");
      record.timepoint = parseOptionalOneBasedInteger(attributes, "TimePoint");
      record.timelineIndex = parseOptionalOneBasedInteger(attributes, "TimelineIndex");
      record.actionIndex = parseOptionalOneBasedInteger(attributes, "ActionIndex");
      record.channel = parseOptionalOneBasedInteger(attributes, "Ch");
      record.zIndex = parseOptionalOneBasedInteger(attributes, "ZIndex");
      record.x = parseOptionalDouble(attributes, "X");
      record.y = parseOptionalDouble(attributes, "Y");
      record.z = parseOptionalDouble(attributes, "Z");
      return record;
    }

    private Integer parseOptionalOneBasedInteger(Attributes attributes,
      String name)
    {
      try {
        Integer value = parsing.parseInteger(
          parsing.getAttribute(attributes, name));
        return value;
      }
      catch (RuntimeException e) {
        LOGGER.debug("Ignoring invalid CV7000 MLF {} value {}", name,
          parsing.getAttribute(attributes, name));
        return null;
      }
    }

    private Double parseOptionalDouble(Attributes attributes, String name) {
      try {
        return parsing.parseDouble(parsing.getAttribute(attributes, name));
      }
      catch (RuntimeException e) {
        LOGGER.debug("Ignoring invalid CV7000 MLF {} value {}", name,
          parsing.getAttribute(attributes, name));
        return null;
      }
    }

  }

  private class MeasurementDetailHandler extends BaseHandler {
    private MeasurementDetailResult result = new MeasurementDetailResult();

    public MeasurementDetailResult getResult() {
      return result;
    }

    // -- DefaultHandler API methods --

    @Override
    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      if (parsing.isElement(qName, "MeasurementSamplePlate")) {
        addYokogawaAttributes("Yokogawa MRF MeasurementSamplePlate ", attributes);
        result.wppPath = parsing.getRawAttribute(
          attributes, "WellPlateProductFileName");
        if (result.wppPath != null && result.wppPath.trim().length() == 0) {
          result.wppPath = null;
        }
      }
      else if (parsing.isElement(qName, "MeasurementChannel")) {
        Channel c = new Channel();
        c.index = parsing.parseRequiredInteger(
          parsing.getRawAttribute(attributes, "Ch")) - 1;
        addYokogawaAttributes(
          "Yokogawa MRF Channel " + (c.index + 1) + " ", attributes);
        c.xSize = parsing.parseDouble(parsing.getRawAttribute(
          attributes, "HorizontalPixelDimension"));
        c.ySize = parsing.parseDouble(parsing.getRawAttribute(
          attributes, "VerticalPixelDimension"));
        c.cameraNumber = parsing.parseRequiredInteger(
          parsing.getRawAttribute(attributes, "CameraNumber"));
        c.inputBitDepth = parsing.parseInteger(
          parsing.getRawAttribute(attributes, "InputBitDepth"));
        c.inputLevel = parsing.parseInteger(
          parsing.getRawAttribute(attributes, "InputLevel"));
        c.horizontalPixels = parsing.parseInteger(
          parsing.getRawAttribute(attributes, "HorizontalPixels"));
        c.verticalPixels = parsing.parseInteger(
          parsing.getRawAttribute(attributes, "VerticalPixels"));
        c.filterWheelPosition = parsing.parseInteger(
          parsing.getRawAttribute(attributes, "FilterWheelPosition"));
        c.filterPosition = parsing.parseInteger(
          parsing.getRawAttribute(attributes, "FilterPosition"));
        c.correctionFile = parsing.getRawAttribute(
          attributes, "ShadingCorrectionSource");
        if (c.correctionFile != null && c.correctionFile.trim().length() == 0) {
          c.correctionFile = null;
        }
        result.channels.add(c);
      }
      else if (parsing.isElement(qName, "MeasurementDetail")) {
        addYokogawaAttributes("Yokogawa MRF MeasurementDetail ", attributes);
        result.startTime = parsing.getRawAttribute(attributes, "BeginTime");
        result.endTime = parsing.getRawAttribute(attributes, "EndTime");
        result.settingsPath = parsing.getRawAttribute(
          attributes, "MeasurementSettingFileName");
        result.measurementOperatorName = parsing.getAttribute(
          attributes, "OperatorName");

        String system = parsing.getRawAttribute(attributes, "TargetSystem");
        result.targetSystem = clean(system);
        addYokogawaMeta(
          "Yokogawa MRF MeasurementDetail ", "AcquisitionSystem", system);
        if (result.targetSystem != null &&
          !result.targetSystem.toLowerCase().startsWith("cv7000"))
        {
          LOGGER.warn("Found data from {}; this is not well-supported",
            result.targetSystem);
        }
      }
    }

  }

  private class WPPHandler extends BaseHandler {

    @Override
    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      if (parsing.isElement(qName, "WellPlateProduct")) {
        addYokogawaAttributes("Yokogawa WPP ", attributes);
      }
    }

  }

  private class PostProcessHandler extends BaseHandler {
    private PostProcessResult result = new PostProcessResult();
    private StringBuffer currentValue = new StringBuffer();
    private PostProcessLog currentLog;
    private int unknownElementCount;

    @Override
    public void characters(char[] ch, int start, int length) {
      currentValue.append(ch, start, length);
    }

    @Override
    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      currentValue.setLength(0);
      String element = parsing.getYokogawaAttributeName(qName);
      if (element.equals("PostProcess")) {
        parsing.copyAttributes(attributes, result.root);
      }
      else if (element.endsWith("Action") && !element.endsWith("ActionList")) {
        PostProcessAction action = new PostProcessAction();
        action.type = element;
        parsing.copyAttributes(attributes, action.attributes);
        result.actions.add(action);
      }
      else if (element.equals("PostProcessLog")) {
        currentLog = new PostProcessLog();
        currentLog.documentIndex = result.logs.size() + 1;
        parsing.copyAttributes(attributes, currentLog.attributes);
        currentLog.timestamp = firstNonNull(
          currentLog.attributes.get("DateTime"),
          currentLog.attributes.get("Time"));
        currentLog.level = currentLog.attributes.get("Level");
        currentLog.title = currentLog.attributes.get("Title");
        currentLog.message = currentLog.attributes.get("Message");
      }
      else if (!element.equals("PostProcessActionList") &&
        !element.equals("PostProcessLogList"))
      {
        unknownElementCount++;
      }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
      if (parsing.isElement(qName, "PostProcessLog") &&
        currentLog != null)
      {
        String text = clean(currentValue.toString());
        if (currentLog.message == null) {
          currentLog.message = text;
        }
        result.logs.add(currentLog);
        currentLog = null;
      }
    }

    @Override
    public void endDocument() {
      emitPostProcessMetadata(result, unknownElementCount);
    }

  }

  private class MeasurementSettingsHandler extends BaseHandler {
    private MeasurementSettingsResult result = new MeasurementSettingsResult();
    private ArrayList<Channel> parsedChannels;
    private HashMap<Integer, ArrayList<Channel>> baseChannelsByRaw =
      new HashMap<Integer, ArrayList<Channel>>();
    private ArrayList<Channel> resolvedChannels = new ArrayList<Channel>();
    private HashSet<String> usedDefinitions = new HashSet<String>();
    private HashMap<Integer, Integer> actionOccurrencesByRaw =
      new HashMap<Integer, Integer>();
    private HashMap<Integer, Integer> templateOccurrencesByRaw =
      new HashMap<Integer, Integer>();
    private StringBuffer currentValue = new StringBuffer();
    private int currentChannelIndex = -1;
    private int currentDefinitionOccurrence = -1;
    private int timelineIndex = -1;
    private int actionIndex = -1;
    private int targetWellIndex = -1;
    private int pointIndex = -1;
    private Double currentPhysicalSizeZ;
    private String actionRunMode;
    private String actionAFSearch;
    private String currentActionType;
    private String currentActionXOffset;
    private String currentActionYOffset;
    private String currentActionAFShiftBase;
    private String currentActionTopDistance;
    private String currentActionBottomDistance;
    private String currentActionSliceLength;
    private String currentActionUseSoftFocus;

    public MeasurementSettingsHandler(ArrayList<Channel> channels) {
      parsedChannels = channels;
      for (Channel channel : parsedChannels) {
        Integer raw = Integer.valueOf(channel.index);
        ArrayList<Channel> definitions = baseChannelsByRaw.get(raw);
        if (definitions == null) {
          definitions = new ArrayList<Channel>();
          baseChannelsByRaw.put(raw, definitions);
        }
        channel.definitionOccurrence = definitions.size();
        definitions.add(channel);
      }
    }

    public MeasurementSettingsResult getResult() {
      result.channels.clear();
      if (resolvedChannels.size() == 0) {
        result.channels.addAll(parsedChannels);
        return result;
      }
      result.channels.addAll(resolvedChannels);
      for (Channel channel : parsedChannels) {
        String key = getDefinitionKey(channel.index, channel.definitionOccurrence);
        if (!usedDefinitions.contains(key)) {
          result.channels.add(channel);
        }
      }
      return result;
    }

    // -- DefaultHandler API methods --

    @Override
    public void characters(char[] ch, int start, int length) {
      String value = new String(ch, start, length);
      currentValue.append(value);
    }

    @Override
    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      currentValue.setLength(0);
      if (parsing.isElement(qName, "MeasurementSetting")) {
        addYokogawaAttributes("Yokogawa MES MeasurementSetting ", attributes);
      }
      else if (parsing.isElement(qName, "LightSource")) {
        parseLightSource(attributes);
      }
      else if (parsing.isElement(qName, "Channel")) {
        parseChannelTemplate(attributes);
      }
      else if (parsing.isElement(qName, "Timeline")) {
        startTimeline(attributes);
      }
      else if (parsing.isElement(qName, "TargetWell")) {
        startTargetWell(attributes);
      }
      else if (parsing.isElement(qName, "PointSequence")) {
        addYokogawaAttributes(
          "Yokogawa MES Timeline " + (timelineIndex + 1) +
          " PointSequence ", attributes);
      }
      else if (parsing.isElement(qName, "FixedPosition")) {
        addYokogawaAttributes(
          "Yokogawa MES Timeline " + (timelineIndex + 1) +
          " FixedPosition ", attributes);
      }
      else if (parsing.isElement(qName, "Point")) {
        pointIndex++;
        addYokogawaAttributes(
          "Yokogawa MES Timeline " + (timelineIndex + 1) +
          " Point " + (pointIndex + 1) + " ", attributes);
      }
      else if (parsing.isElement(qName, "ActionList")) {
        startActionList(attributes);
      }
      else if (parsing.elementStartsWith(qName, "ActionAcquire")) {
        startActionAcquire(qName, attributes);
      }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
      String value = currentValue.toString();

      if (parsing.isElement(qName, "LightSourceName") &&
        currentChannelIndex >= 0)
      {
        addYokogawaMeta(
          "Yokogawa MES Channel " + (currentChannelIndex + 1) + " ",
          "LightSourceName", value);
        int index = -1;
        for (int i=0; i<result.lightSources.size(); i++) {
          if (result.lightSources.get(i).name != null &&
            result.lightSources.get(i).name.equals(value))
          {
            index = i;
          }
        }
        if (index >= 0) {
          addLightSourceRef(
            currentChannelIndex, currentDefinitionOccurrence, index);
        }
      }
      else if (parsing.isElement(qName, "Channel")) {
        currentChannelIndex = -1;
        currentDefinitionOccurrence = -1;
      }
      else if (parsing.isElement(qName, "Ch")) {
        assignActionChannel(value);
      }
      else if (parsing.elementStartsWith(qName, "ActionAcquire")) {
        clearActionAcquireState();
      }
    }

    private void parseLightSource(Attributes attributes) {
      LightSource lightSource = new LightSource();
      lightSource.name = parsing.getRawAttribute(attributes, "Name");
      lightSource.type = parsing.getRawAttribute(attributes, "Type");
      addYokogawaAttributes(
        "Yokogawa MES LightSource " + lightSource.name + " ", attributes);

      String wavelength = parsing.getRawAttribute(attributes, "WaveLength");
      String power = parsing.getRawAttribute(attributes, "Power");

      lightSource.wavelength = parsing.parseDouble(wavelength);
      lightSource.attenuation = parsing.parseDouble(power);

      result.lightSources.add(lightSource);
    }

    private void parseChannelTemplate(Attributes attributes) {
      currentChannelIndex = -1;
      currentDefinitionOccurrence = -1;
      String ch = parsing.getRawAttribute(attributes, "Ch");
      if (ch == null) {
        return;
      }

      int index = parsing.parseRequiredInteger(ch) - 1;
      if (index < 0) {
        return;
      }

      currentChannelIndex = index;
      currentDefinitionOccurrence = nextOccurrence(
        templateOccurrencesByRaw, index);

      Channel template = new Channel();
      template.index = index;
      template.target = parsing.getRawAttribute(attributes, "Target");
      addYokogawaAttributes(
        "Yokogawa MES Channel " + (template.index + 1) + " ", attributes);
      template.objectiveID = parsing.getRawAttribute(attributes, "ObjectiveID");
      template.objective = parsing.getRawAttribute(attributes, "Objective");
      template.binning = parsing.getRawAttribute(attributes, "Binning");
      template.methodID = parsing.getRawAttribute(attributes, "MethodID");
      template.method = parsing.getRawAttribute(attributes, "Method");
      template.filterID = parsing.getRawAttribute(attributes, "FilterID");
      template.kind = parsing.getRawAttribute(attributes, "Kind");
      template.andorParameterID = parsing.getRawAttribute(
        attributes, "AndorParameterID");
      template.andorParameter = parsing.getRawAttribute(
        attributes, "AndorParameter");
      template.detectorGain = parsing.parseYokogawaGain(template.andorParameter);
      template.cameraType = parsing.getRawAttribute(attributes, "CameraType");
      template.inputLevel = parsing.parseInteger(
        parsing.getRawAttribute(attributes, "InputLevel"));

      String mag = parsing.getRawAttribute(attributes, "Magnification");
      template.magnification = parsing.parseDouble(mag);

      String exposure = parsing.getRawAttribute(attributes, "ExposureTime");
      template.exposureTime = parsing.parseDouble(exposure);

      populateChannelColor(template, parsing.getRawAttribute(attributes, "Color"));

      template.acquisition = parsing.getRawAttribute(attributes, "Acquisition");
      // Yokogawa Acquisition values such as BP676/29 identify detection
      // filters.  Excitation comes from the LightSourceName link.
      template.detectionFilter = parsing.parseDetectionFilter(template.acquisition);

      template.fluor = parsing.getRawAttribute(attributes, "Fluorophore");
      applyChannelSettings(template, currentDefinitionOccurrence);
    }

    private void populateChannelColor(Channel template, String color) {
      if (color == null) {
        return;
      }

      color = color.replaceAll("#", "");
      // ignore unless at least R, G, B are defined
      if (color.length() < 6) {
        return;
      }

      int[] colors = new int[color.length() / 2];
      for (int i=0; i<color.length(); i+=2) {
        colors[i / 2] = Integer.parseInt(color.substring(i, i + 2), 16);
      }
      int alpha = colors.length == 4 ? colors[0] : 255;
      int red = colors[colors.length - 3];
      int green = colors[colors.length - 2];
      int blue = colors[colors.length - 1];
      template.color = new Color(red, green, blue, alpha);
    }

    private void startTimeline(Attributes attributes) {
      timelineIndex++;
      actionIndex = -1;
      targetWellIndex = -1;
      pointIndex = -1;
      currentPhysicalSizeZ = null;
      actionRunMode = null;
      actionAFSearch = null;
      actionOccurrencesByRaw.clear();
      addYokogawaAttributes(
        "Yokogawa MES Timeline " + (timelineIndex + 1) + " ", attributes);
    }

    private void startTargetWell(Attributes attributes) {
      targetWellIndex++;
      addYokogawaAttributes(
        "Yokogawa MES Timeline " + (timelineIndex + 1) +
        " TargetWell " + (targetWellIndex + 1) + " ", attributes);
    }

    private void startActionList(Attributes attributes) {
      actionRunMode = parsing.getRawAttribute(attributes, "RunMode");
      actionAFSearch = parsing.getRawAttribute(attributes, "AFSearch");
      addYokogawaAttributes(
        "Yokogawa MES Timeline " + (timelineIndex + 1) +
        " ActionList ", attributes);
    }

    private void startActionAcquire(String qName, Attributes attributes) {
      actionIndex++;
      currentActionType = parsing.getYokogawaAttributeName(qName);
      currentActionXOffset = parsing.getRawAttribute(attributes, "XOffset");
      currentActionYOffset = parsing.getRawAttribute(attributes, "YOffset");
      currentActionAFShiftBase = parsing.getRawAttribute(
        attributes, "AFShiftBase");
      currentActionTopDistance = parsing.getRawAttribute(
        attributes, "TopDistance");
      currentActionBottomDistance = parsing.getRawAttribute(
        attributes, "BottomDistance");
      currentActionSliceLength = parsing.getRawAttribute(
        attributes, "SliceLength");
      currentActionUseSoftFocus = parsing.getRawAttribute(
        attributes, "UseSoftFocus");
      addYokogawaAttributes(
        "Yokogawa MES Timeline " + (timelineIndex + 1) +
        " Action " + (actionIndex + 1) + " ", attributes);
      currentPhysicalSizeZ = parsing.parseDouble(currentActionSliceLength);
    }

    private void assignActionChannel(String value) {
      int channelIndex = parsing.parseRequiredInteger(value) - 1;
      if (channelIndex < 0) {
        return;
      }
      int occurrence = nextOccurrence(actionOccurrencesByRaw, channelIndex);
      Channel definition = getChannelDefinition(channelIndex, occurrence);
      Channel channel;
      if (definition == null) {
        channel = new Channel();
        channel.index = channelIndex;
        channel.definitionOccurrence = -1;
        channel.metadataAmbiguous = true;
        LOGGER.warn("No unambiguous CV7000 MRF channel definition for " +
          "timeline {}, action {}, raw channel {}, occurrence {}",
          timelineIndex + 1, actionIndex + 1, channelIndex + 1, occurrence + 1);
      }
      else {
        channel = new Channel(definition);
        channel.definitionOccurrence = definition.definitionOccurrence;
        usedDefinitions.add(getDefinitionKey(
          channelIndex, definition.definitionOccurrence));
      }
      assignActionSettings(channel);
      resolvedChannels.add(channel);
    }

    private void assignActionSettings(Channel channel) {
      channel.timelineIndex = timelineIndex;
      channel.actionIndex = actionIndex;
      channel.physicalSizeZ = currentPhysicalSizeZ;
      channel.copyActionSettings(actionRunMode, actionAFSearch, currentActionType,
        currentActionXOffset, currentActionYOffset, currentActionAFShiftBase,
        currentActionTopDistance, currentActionBottomDistance,
        currentActionSliceLength, currentActionUseSoftFocus);
    }

    private void clearActionAcquireState() {
      currentPhysicalSizeZ = null;
      currentActionType = null;
      currentActionXOffset = null;
      currentActionYOffset = null;
      currentActionAFShiftBase = null;
      currentActionTopDistance = null;
      currentActionBottomDistance = null;
      currentActionSliceLength = null;
      currentActionUseSoftFocus = null;
    }

    private void applyChannelSettings(Channel template, int occurrence) {
      Channel definition = getChannelDefinition(template.index, occurrence);
      if (definition != null) {
        definition.copyChannelSettings(template);
      }
      else {
        LOGGER.warn("No unambiguous CV7000 MRF channel definition for " +
          "MES channel template {}, occurrence {}",
          template.index + 1, occurrence + 1);
        return;
      }
      int resolvedOccurrence = definition.definitionOccurrence;
      for (Channel channel : resolvedChannels) {
        if (channel.index == template.index &&
          channel.definitionOccurrence == resolvedOccurrence)
        {
          channel.copyChannelSettings(template);
        }
      }
    }

    private void addLightSourceRef(int rawChannelIndex, int definitionOccurrence,
      int lightSourceIndex)
    {
      for (Channel ch : parsedChannels) {
        if (ch.index == rawChannelIndex &&
          ch.definitionOccurrence == definitionOccurrence &&
          !ch.lightSourceRefs.contains(lightSourceIndex))
        {
          ch.lightSourceRefs.add(lightSourceIndex);
        }
      }
      for (Channel ch : resolvedChannels) {
        if (ch.index == rawChannelIndex &&
          ch.definitionOccurrence == definitionOccurrence &&
          !ch.lightSourceRefs.contains(lightSourceIndex))
        {
          ch.lightSourceRefs.add(lightSourceIndex);
        }
      }
    }

    private int nextOccurrence(HashMap<Integer, Integer> occurrences,
      int rawChannel)
    {
      Integer raw = Integer.valueOf(rawChannel);
      Integer next = occurrences.get(raw);
      int occurrence = next == null ? 0 : next.intValue();
      occurrences.put(raw, Integer.valueOf(occurrence + 1));
      return occurrence;
    }

    private Channel getChannelDefinition(int rawChannel, int occurrence) {
      ArrayList<Channel> definitions =
        baseChannelsByRaw.get(Integer.valueOf(rawChannel));
      return selectChannelDefinition(definitions, occurrence);
    }

    private String getDefinitionKey(int rawChannel, int occurrence) {
      return rawChannel + ":" + occurrence;
    }

  }

  private class CrosstalkParameterHandler extends BaseHandler {
    private CrosstalkParameters parameters = new CrosstalkParameters();
    private CrosstalkFilter currentFilter;
    private CrosstalkFluorophore currentFluorophore;

    public CrosstalkParameters getParameters() {
      return parameters;
    }

    @Override
    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      String name = parsing.getYokogawaAttributeName(qName);
      if ("EMFilter".equals(name)) {
        currentFilter = new CrosstalkFilter();
        currentFilter.filterID = parsing.getAttribute(attributes, "FilterID");
        currentFilter.cameraNumber = parsing.parseInteger(
          parsing.getAttribute(attributes, "CameraID"));
        currentFilter.acquisition = parsing.getAttribute(attributes, "Acquisition");
        currentFilter.averageTransmittance = parsing.parseDouble(
          parsing.getAttribute(attributes, "AverageTransmittance"));
        currentFilter.minWaveLength = parsing.parseDouble(
          parsing.getAttribute(attributes, "MinWaveLength"));
        currentFilter.maxWaveLength = parsing.parseDouble(
          parsing.getAttribute(attributes, "MaxWaveLength"));
        parameters.addFilter(currentFilter);
      }
      else if ("ISDM".equals(name) && currentFilter != null) {
        CrosstalkDichroic dichroic = new CrosstalkDichroic();
        dichroic.id = parsing.getAttribute(attributes, "ID");
        dichroic.name = parsing.getAttribute(attributes, "Name");
        dichroic.reflection = parsing.getAttribute(attributes, "Reflection");
        dichroic.averageTransmittance = parsing.parseDouble(
          parsing.getAttribute(attributes, "AverageTransmittance"));
        currentFilter.dichroics.add(dichroic);
      }
      else if ("Fluorophore".equals(name)) {
        currentFluorophore = new CrosstalkFluorophore();
        currentFluorophore.name = parsing.getAttribute(attributes, "Name");
        parameters.fluorophores.add(currentFluorophore);
      }
      else if ("FluorophoreIntensity".equals(name) && currentFluorophore != null) {
        CrosstalkFluorophoreIntensity intensity =
          new CrosstalkFluorophoreIntensity();
        intensity.filterID = parsing.getAttribute(attributes, "FilterID");
        intensity.averageIntensity = parsing.parseDouble(
          parsing.getAttribute(attributes, "AverageIntensity"));
        currentFluorophore.intensities.add(intensity);
      }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
      String name = parsing.getYokogawaAttributeName(qName);
      if ("EMFilter".equals(name)) {
        currentFilter = null;
      }
      else if ("Fluorophore".equals(name)) {
        currentFluorophore = null;
      }
    }

  }

  private class OTFGeometryHandler extends BaseHandler {
    private OTFGeometryParameters parameters = new OTFGeometryParameters();

    public OTFGeometryParameters getParameters() {
      return parameters;
    }

    @Override
    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      String name = parsing.getYokogawaAttributeName(qName);
      if ("GeometryParameter".equals(name)) {
        parameters.mode = parsing.getAttribute(attributes, "Mode");
        return;
      }
      if (!"AffineParameter".equals(name)) {
        return;
      }

      OTFGeometryAffine affine = new OTFGeometryAffine();
      affine.methodID = parsing.getAttribute(attributes, "MethodID");
      affine.method = parsing.getAttribute(attributes, "Method");
      affine.objectiveID = parsing.getAttribute(attributes, "ObjectiveID");
      affine.objective = parsing.getAttribute(attributes, "Objective");
      affine.magnification = parsing.parseDouble(
        parsing.getAttribute(attributes, "Magnification"));
      affine.filterID = parsing.getAttribute(attributes, "FilterID");
      affine.acquisition = parsing.getAttribute(attributes, "Acquisition");
      affine.use = parsing.getAttribute(attributes, "Use");
      affine.updateTime = parsing.getAttribute(attributes, "UpdateTime");
      affine.a = parsing.parseDouble(parsing.getAttribute(attributes, "A"));
      affine.b = parsing.parseDouble(parsing.getAttribute(attributes, "B"));
      affine.c = parsing.parseDouble(parsing.getAttribute(attributes, "C"));
      affine.d = parsing.parseDouble(parsing.getAttribute(attributes, "D"));
      affine.e = parsing.parseDouble(parsing.getAttribute(attributes, "E"));
      affine.f = parsing.parseDouble(parsing.getAttribute(attributes, "F"));
      parameters.addAffine(affine);
    }
  }

  // ###############
  // ## Module 12: OTF, Crosstalk, Objective, Channel, And Plane Models
  // ###############

  public static class OTFGeometryParameters {
    public String mode;
    public LinkedHashMap<String, OTFGeometryObjective> objectivesByID =
      new LinkedHashMap<String, OTFGeometryObjective>();
    public ArrayList<OTFGeometryAffine> affines =
      new ArrayList<OTFGeometryAffine>();

    public void addAffine(OTFGeometryAffine affine) {
      affines.add(affine);
      if (affine.objectiveID == null) {
        return;
      }
      OTFGeometryObjective objective = objectivesByID.get(affine.objectiveID);
      if (objective == null) {
        objective = new OTFGeometryObjective();
        objective.objectiveID = affine.objectiveID;
        objective.objective = affine.objective;
        objective.magnification = affine.magnification;
        objectivesByID.put(objective.objectiveID, objective);
      }
      else {
        if (objective.objective == null) {
          objective.objective = affine.objective;
        }
        if (objective.magnification == null) {
          objective.magnification = affine.magnification;
        }
      }
    }
  }

  public static class OTFGeometryObjective {
    public String objectiveID;
    public String objective;
    public Double magnification;
  }

  public static class OTFGeometryAffine {
    public String methodID;
    public String method;
    public String objectiveID;
    public String objective;
    public Double magnification;
    public String filterID;
    public String acquisition;
    public String use;
    public String updateTime;
    public Double a;
    public Double b;
    public Double c;
    public Double d;
    public Double e;
    public Double f;
  }

  public static class CrosstalkParameters {
    public LinkedHashMap<CrosstalkFilterKey, CrosstalkFilter> filters =
      new LinkedHashMap<CrosstalkFilterKey, CrosstalkFilter>();
    public ArrayList<CrosstalkFluorophore> fluorophores =
      new ArrayList<CrosstalkFluorophore>();

    public void addFilter(CrosstalkFilter filter) {
      filters.put(new CrosstalkFilterKey(
        filter.filterID, filter.cameraNumber, filter.acquisition), filter);
    }
  }

  public static class CrosstalkFilter {
    public String filterID;
    public Integer cameraNumber;
    public String acquisition;
    public Double averageTransmittance;
    public Double minWaveLength;
    public Double maxWaveLength;
    public ArrayList<CrosstalkDichroic> dichroics =
      new ArrayList<CrosstalkDichroic>();
  }

  public static class CrosstalkDichroic {
    public String id;
    public String name;
    public String reflection;
    public Double averageTransmittance;
  }

  public static class CrosstalkFluorophore {
    public String name;
    public ArrayList<CrosstalkFluorophoreIntensity> intensities =
      new ArrayList<CrosstalkFluorophoreIntensity>();
  }

  public static class CrosstalkFluorophoreIntensity {
    public String filterID;
    public Double averageIntensity;
  }

  public static class CrosstalkFilterKey {
    public String filterID;
    public Integer cameraNumber;
    public String acquisition;

    public CrosstalkFilterKey() {
    }

    public CrosstalkFilterKey(String filterID, Integer cameraNumber,
      String acquisition)
    {
      this.filterID = filterID;
      this.cameraNumber = cameraNumber;
      this.acquisition = acquisition;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof CrosstalkFilterKey)) {
        return false;
      }
      CrosstalkFilterKey key = (CrosstalkFilterKey) o;
      return same(filterID, key.filterID) &&
        same(cameraNumber, key.cameraNumber) &&
        same(acquisition, key.acquisition);
    }

    @Override
    public int hashCode() {
      int code = 17;
      code = 31 * code + hash(filterID);
      code = 31 * code + hash(cameraNumber);
      code = 31 * code + hash(acquisition);
      return code;
    }

    private boolean same(Object a, Object b) {
      return a == null ? b == null : a.equals(b);
    }

    private int hash(Object value) {
      return value == null ? 0 : value.hashCode();
    }
  }

  public static class LightSource {
    public String name;
    public String type;
    public Double wavelength;
    public Double attenuation;
  }

  private static class CV7000ObjectiveSpec {
    public Double magnification;
    public Double lensNA;
    public String immersion;

    public CV7000ObjectiveSpec(Double magnification, Double lensNA,
      String immersion)
    {
      this.magnification = magnification;
      this.lensNA = lensNA;
      this.immersion = immersion;
    }
  }

  private static class ObjectiveModelTokens {
    public Integer magnification;
    public boolean phase;
    public boolean longWorkingDistance;
    public String immersion;
  }

  private static class KnownObjectiveSpec {
    public int magnification;
    public boolean phase;
    public boolean longWorkingDistance;
    public String immersion;
    public Double lensNA;

    public KnownObjectiveSpec(int magnification, boolean phase,
      boolean longWorkingDistance, String immersion, Double lensNA)
    {
      this.magnification = magnification;
      this.phase = phase;
      this.longWorkingDistance = longWorkingDistance;
      this.immersion = immersion;
      this.lensNA = lensNA;
    }

    public boolean matches(ObjectiveModelTokens tokens) {
      if (tokens.magnification == null ||
        tokens.magnification.intValue() != magnification)
      {
        return false;
      }

      if (tokens.phase != phase ||
        tokens.longWorkingDistance != longWorkingDistance)
      {
        return false;
      }
      if (tokens.immersion != null) {
        return immersion.equals(tokens.immersion);
      }
      return !"Water".equals(immersion);
    }

    public CV7000ObjectiveSpec toObjectiveSpec() {
      return new CV7000ObjectiveSpec(
        Double.valueOf(magnification), lensNA, immersion);
    }
  }

  public static class DetectionFilter {
    public String filterType;
    public Double center;
    public Double width;
    public Double cutIn;
    public Double cutOut;

    public static DetectionFilter bandPass(Double center, Double width) {
      DetectionFilter filter = new DetectionFilter("BandPass");
      filter.center = center;
      filter.width = width;
      filter.cutIn = center - (width / 2);
      filter.cutOut = center + (width / 2);
      return filter;
    }

    public DetectionFilter() {
    }

    public DetectionFilter(String filterType) {
      this.filterType = filterType;
    }
  }

  private static class FilterKey {
    public String filterID;
    public String acquisition;
    public int cameraNumber;
    public Integer filterWheelPosition;
    public Integer filterPosition;
    public DetectionFilter detectionFilter;

    public FilterKey(Channel ch) {
      filterID = ch.filterID;
      acquisition = ch.acquisition;
      cameraNumber = ch.cameraNumber;
      filterWheelPosition = ch.filterWheelPosition;
      filterPosition = ch.filterPosition;
      detectionFilter = ch.detectionFilter;
    }

    public boolean isValid() {
      return acquisition != null && detectionFilter != null;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof FilterKey)) {
        return false;
      }
      FilterKey key = (FilterKey) o;
      return same(filterID, key.filterID) &&
        same(acquisition, key.acquisition) &&
        cameraNumber == key.cameraNumber &&
        same(filterWheelPosition, key.filterWheelPosition) &&
        same(filterPosition, key.filterPosition);
    }

    @Override
    public int hashCode() {
      int code = 17;
      code = 31 * code + hash(filterID);
      code = 31 * code + hash(acquisition);
      code = 31 * code + cameraNumber;
      code = 31 * code + hash(filterWheelPosition);
      code = 31 * code + hash(filterPosition);
      return code;
    }

    private boolean same(Object a, Object b) {
      return a == null ? b == null : a.equals(b);
    }

    private int hash(Object value) {
      return value == null ? 0 : value.hashCode();
    }
  }

  public static class Channel {
    public int timelineIndex = -1;
    public int actionIndex = -1;
    public int index;
    public int definitionOccurrence;
    public boolean metadataAmbiguous;
    public double xSize;
    public double ySize;
    public int cameraNumber;
    public Integer inputBitDepth;
    public Integer inputLevel;
    public Integer horizontalPixels;
    public Integer verticalPixels;
    public Integer filterWheelPosition;
    public Integer filterPosition;
    public String correctionFile;
    public String resolvedCorrectionFile;
    public List<Integer> lightSourceRefs = new ArrayList<Integer>();

    public String target;
    public String methodID;
    public String method;
    public String filterID;
    public String acquisition;
    public DetectionFilter detectionFilter;
    public String kind;
    public String cameraType;
    public String andorParameterID;
    public String andorParameter;
    public Double detectorGain;
    public String objectiveID;
    public String objective;
    public String actionType;
    public String actionRunMode;
    public String actionAFSearch;
    public String actionXOffset;
    public String actionYOffset;
    public String actionAFShiftBase;
    public String actionTopDistance;
    public String actionBottomDistance;
    public String actionSliceLength;
    public String actionUseSoftFocus;
    public Double magnification;
    public Double exposureTime;
    public Double physicalSizeZ;
    public String binning;
    public Color color;

    public String fluor;

    public Channel() {
    }

    public Channel(Channel ch) {
      index = ch.index;
      definitionOccurrence = ch.definitionOccurrence;
      metadataAmbiguous = ch.metadataAmbiguous;
      xSize = ch.xSize;
      ySize = ch.ySize;
      cameraNumber = ch.cameraNumber;
      inputBitDepth = ch.inputBitDepth;
      inputLevel = ch.inputLevel;
      horizontalPixels = ch.horizontalPixels;
      verticalPixels = ch.verticalPixels;
      filterWheelPosition = ch.filterWheelPosition;
      filterPosition = ch.filterPosition;
      correctionFile = ch.correctionFile;
      resolvedCorrectionFile = ch.resolvedCorrectionFile;
      lightSourceRefs = new ArrayList<Integer>(ch.lightSourceRefs);
      target = ch.target;
      methodID = ch.methodID;
      method = ch.method;
      filterID = ch.filterID;
      acquisition = ch.acquisition;
      detectionFilter = ch.detectionFilter;
      kind = ch.kind;
      cameraType = ch.cameraType;
      andorParameterID = ch.andorParameterID;
      andorParameter = ch.andorParameter;
      detectorGain = ch.detectorGain;
      objectiveID = ch.objectiveID;
      objective = ch.objective;
      actionType = ch.actionType;
      actionRunMode = ch.actionRunMode;
      actionAFSearch = ch.actionAFSearch;
      actionXOffset = ch.actionXOffset;
      actionYOffset = ch.actionYOffset;
      actionAFShiftBase = ch.actionAFShiftBase;
      actionTopDistance = ch.actionTopDistance;
      actionBottomDistance = ch.actionBottomDistance;
      actionSliceLength = ch.actionSliceLength;
      actionUseSoftFocus = ch.actionUseSoftFocus;
      magnification = ch.magnification;
      exposureTime = ch.exposureTime;
      physicalSizeZ = ch.physicalSizeZ;
      binning = ch.binning;
      color = ch.color;
      fluor = ch.fluor;
    }

    public void copyActionSettings(String runMode, String afSearch, String type,
      String xOffset, String yOffset, String afShiftBase, String topDistance,
      String bottomDistance, String sliceLength, String useSoftFocus)
    {
      actionRunMode = runMode;
      actionAFSearch = afSearch;
      actionType = type;
      actionXOffset = xOffset;
      actionYOffset = yOffset;
      actionAFShiftBase = afShiftBase;
      actionTopDistance = topDistance;
      actionBottomDistance = bottomDistance;
      actionSliceLength = sliceLength;
      actionUseSoftFocus = useSoftFocus;
    }

    public void copyChannelSettings(Channel ch) {
      target = ch.target;
      methodID = ch.methodID;
      method = ch.method;
      filterID = ch.filterID;
      acquisition = ch.acquisition;
      detectionFilter = ch.detectionFilter;
      kind = ch.kind;
      cameraType = ch.cameraType;
      andorParameterID = ch.andorParameterID;
      andorParameter = ch.andorParameter;
      detectorGain = ch.detectorGain;
      inputLevel = ch.inputLevel;
      objectiveID = ch.objectiveID;
      objective = ch.objective;
      magnification = ch.magnification;
      exposureTime = ch.exposureTime;
      binning = ch.binning;
      color = ch.color;
      fluor = ch.fluor;
    }

    public boolean hasChannelSettings() {
      return objectiveID != null || objective != null || magnification != null ||
        exposureTime != null || detectorGain != null || binning != null || color != null ||
        acquisition != null || detectionFilter != null || fluor != null ||
        (lightSourceRefs != null && lightSourceRefs.size() > 0);
    }

    public boolean isBrightfield() {
      return BRIGHTFIELD.equals(kind) ||
        (method != null && BRIGHTFIELD.equalsIgnoreCase(method));
    }

    @Override
    public String toString() {
      return "timelineIndex=" + timelineIndex + ", actionIndex=" + actionIndex + ", index=" + index;
    }
  }

  public static class Plane {
    public String file;
    public String timestamp;
    public String actionName;
    public Field field;
    public int timepoint;
    public int z;
    // this is the original channel value stored in the XML
    public int channel;
    // this is the calculated index from 0 to getSizeC() - 1
    public int channelIndex;
    public ChannelSlotKey channelSlot;
    public double xpos;
    public double ypos;
    public double zpos;
    public int series;
    public int no;
    public int actionIndex;
    public int timelineIndex;
  }

  public static class Field {
    public int row;
    public int column;
    public int field;

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof Field)) {
        return false;
      }
      Field f = (Field) o;
      return f.row == row && f.column == column && f.field == field;
    }

    @Override
    public int hashCode() {
      // allows up to 256 rows and columns, up to 65536 fields
      return (row & 0xff) << 24 | (column & 0xff) << 16 | (field & 0xffff);
    }
  }

  public static class MinMax {
    public int minZ = Integer.MAX_VALUE;
    public int maxZ = 0;
    public int minT = Integer.MAX_VALUE;
    public int maxT = 0;

    public void update(Plane p) {
      if (p.timepoint > maxT) {
        maxT = p.timepoint;
      }
      if (p.timepoint < minT) {
        minT = p.timepoint;
      }
      if (p.z > maxZ) {
        maxZ = p.z;
      }
      if (p.z < minZ) {
        minZ = p.z;
      }
    }
  }

}
