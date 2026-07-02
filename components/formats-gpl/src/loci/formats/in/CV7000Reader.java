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
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import loci.common.DataTools;
import loci.common.Location;
import loci.common.xml.BaseHandler;
import loci.common.xml.XMLTools;
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
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;
import ome.xml.model.enums.AcquisitionMode;
import ome.xml.model.enums.ContrastMethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;

/**
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class CV7000Reader extends FormatReader {

  // -- Constants --

  public static final String DUPLICATE_PLANES_KEY = "cv7000.duplicate_missing_planes";
  public static final boolean DUPLICATE_PLANES_DEFAULT = false;

  private static final Logger LOGGER = LoggerFactory.getLogger(CV7000Reader.class);

  private static final String MEASUREMENT_FILE = "MeasurementData.mlf";
  private static final String MEASUREMENT_DETAIL = "MeasurementDetail.mrf";
  private static final String POST_PROCESS = "PostProcess.ppf";
  private static final String BRIGHTFIELD = "Brightfield";
  private static final int RAW_XML_CHUNK_SIZE = 7600;

  // -- Fields --

  private List<String> allFiles = new ArrayList<String>();
  private MinimalTiffReader reader;
  private String wppPath;
  private String detailPath;
  private String measurementPath;
  private String settingsPath;
  private ArrayList<Plane> planeData;
  private int[][] reversePlaneLookup;
  private ArrayList<LightSource> lightSources;
  private ArrayList<Channel> channels;
  private String startTime, endTime;
  private ArrayList<String> extraFiles;
  private MeasurementDataHandler measurementHandler;

  private transient Map<String, Boolean> acquiredWells = new HashMap<String, Boolean>();

  // -- Constructor --

  /** Constructs a new Yokogawa CV7000 reader. */
  public CV7000Reader() {
    super("Yokogawa CV7000", new String[] {"wpi"});
    hasCompanionFiles = true;
    domains = new String[] {FormatTools.HCS_DOMAIN};
    datasetDescription = "Directory with XML files and one .tif/.tiff file per plane";
  }

  // -- CV7000Reader API methods --

  public boolean duplicatePlanes() {
    MetadataOptions options = getMetadataOptions();
    if (options instanceof DynamicMetadataOptions) {
      return ((DynamicMetadataOptions) options).getBoolean(
       DUPLICATE_PLANES_KEY, DUPLICATE_PLANES_DEFAULT);
    }
    return DUPLICATE_PLANES_DEFAULT;
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
    ArrayList<String> files = new ArrayList<String>();
    files.add(new Location(currentId).getAbsolutePath());
    for (String file : allFiles) {
      if (file != null && !files.contains(file) && (!noPixels || !checkSuffix(file, "tif"))) {
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
        if (c != null && c.correctionFile != null &&
          new Location(c.correctionFile).exists())
        {
          files.add(c.correctionFile);
        }
      }
    }
    files.addAll(extraFiles);
    for (String file : allFiles) {
      if (file != null && !checkSuffix(file, "tif") && !(new Location(file).isDirectory())) {
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
      planeData = null;
      lightSources = null;
      channels = null;
      startTime = null;
      endTime = null;
      measurementHandler = null;
      reversePlaneLookup = null;
      extraFiles = null;
      acquiredWells.clear();
      if (allFiles != null) {
          allFiles.clear();
      } else {
          allFiles = new ArrayList<String>();
      }
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
    else if (duplicatePlanes() && no > 0) {
      int[] zct = getZCTCoords(no);
      // pick the first plane in the same channel
      int dupPlane = getIndex(0, zct[1], 0);

      // very unlikely to happen, but catching the case
      // where no is the first plane in the channel prevents
      // a potential infinite loop
      if (dupPlane == no) {
        dupPlane = 0;
      }
      Plane duplicate = lookupPlane(getSeries(), dupPlane);
      if (duplicate != null && duplicate.file != null) {
        return openBytes(dupPlane, buf, x, y, w, h);
      }
    }
    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#getAvailableOptions() */
  @Override
  protected ArrayList<String> getAvailableOptions() {
    ArrayList<String> optionsList = super.getAvailableOptions();
    optionsList.add(DUPLICATE_PLANES_KEY);
    return optionsList;
  }

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    DatasetPaths paths = getDatasetPaths(id);
    WPIHandler plate = parsePlate(paths.wpiPath);

    collectDatasetFiles(paths.parent);
    parseMeasurementData(paths);
    parseOptionalSidecars(paths);
    normalizeChannels(paths.parent);

    SeriesLayout layout = buildSeriesLayout();
    initializeCoreMetadata(layout);
    populateReversePlaneLookup(layout);
    populateMetadataStore(plate, layout);
    setSeries(0);
  }

  private DatasetPaths getDatasetPaths(String id) {
    DatasetPaths paths = new DatasetPaths();
    Location wpi = new Location(id).getAbsoluteFile();
    paths.wpiPath = wpi.getAbsolutePath();
    paths.parent = wpi.getParentFile();
    paths.measurementData = new Location(paths.parent, MEASUREMENT_FILE);
    paths.measurementDetail = new Location(paths.parent, MEASUREMENT_DETAIL);
    return paths;
  }

  /** Parse the .wpi file, which defines the plate dimensions and identity. */
  private WPIHandler parsePlate(String wpiPath) throws IOException {
    WPIHandler plate = new WPIHandler();
    XMLTools.parseXML(readSanitizedXML(wpiPath), plate);
    return plate;
  }

  /** Keep a dataset-level inventory for getUsedFiles and original metadata. */
  private void collectDatasetFiles(Location parent) {
    String[] listedFiles = parent.list(true);
    Arrays.sort(listedFiles);
    for (int i=0; i<listedFiles.length; i++) {
      Location file = new Location(parent, listedFiles[i]);
      if (!file.isDirectory() && file.canRead()) {
        allFiles.add(file.getAbsolutePath());
      }
    }
  }

  /** Parse the required plane manifest and resolve readable TIFF plane paths. */
  private void parseMeasurementData(DatasetPaths paths)
    throws FormatException, IOException
  {
    if (!paths.measurementData.exists()) {
      throw new FormatException("Missing " + MEASUREMENT_FILE + " file");
    }

    measurementPath = paths.measurementData.getAbsolutePath();
    measurementHandler = new MeasurementDataHandler(paths.parent.getAbsolutePath());
    XMLTools.parseXML(readSanitizedXML(measurementPath), measurementHandler);
    planeData = measurementHandler.getPlanes();
  }

  /** Parse optional XML sidecars that enrich channel, instrument, and raw metadata. */
  private void parseOptionalSidecars(DatasetPaths paths) throws IOException {
    parseMeasurementDetail(paths);
    parseWellPlateProduct();
    parseMeasurementSettings();
  }

  /** MeasurementDetail links the acquisition settings sidecars and seeds channels. */
  private void parseMeasurementDetail(DatasetPaths paths) throws IOException {
    if (!paths.measurementDetail.exists()) {
      LOGGER.warn("Missing " + MEASUREMENT_DETAIL + " file");
      return;
    }

    channels = new ArrayList<Channel>();
    detailPath = paths.measurementDetail.getAbsolutePath();
    MeasurementDetailHandler detailHandler = new MeasurementDetailHandler();
    XMLTools.parseXML(readSanitizedXML(detailPath), detailHandler);
    if (wppPath != null) {
      wppPath = new Location(paths.parent, wppPath).getAbsolutePath();
    }
    if (settingsPath != null) {
      settingsPath = new Location(paths.parent, settingsPath).getAbsolutePath();
    }
  }

  /** WPP is optional plate-product metadata; missing files preserve legacy behavior. */
  private void parseWellPlateProduct() throws IOException {
    if (wppPath != null && new Location(wppPath).exists()) {
      XMLTools.parseXML(readSanitizedXML(wppPath), new WPPHandler());
    }
  }

  /** Measurement settings attach light sources, actions, filters, and channel modes. */
  private void parseMeasurementSettings() throws IOException {
    if (settingsPath != null && new Location(settingsPath).exists()) {
      lightSources = new ArrayList<LightSource>();
      MeasurementSettingsHandler settingsHandler = new MeasurementSettingsHandler();
      String xml = readSanitizedXML(settingsPath);
      if (xml.length() > 0) {
        XMLTools.parseXML(xml, settingsHandler);
      }
    }
  }

  /** Normalize channel order and paths after all optional channel sources are parsed. */
  private void normalizeChannels(Location parent) {
    if (channels == null) {
      return;
    }
    sortChannelsByActionThenIndex();
    resolveCorrectionFiles(parent);
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
  private void resolveCorrectionFiles(Location parent) {
    for (Channel ch : channels) {
      if (ch.correctionFile != null) {
        ch.correctionFile = new Location(parent, ch.correctionFile).getAbsolutePath();
      }
    }
  }

  /**
   * Build the field/channel/time/z layout before touching Bio-Formats core
   * metadata.  This keeps Yokogawa indexing separate from reader indexing.
   */
  private SeriesLayout buildSeriesLayout() throws FormatException {
    SeriesLayout layout = new SeriesLayout();
    HashSet<Field> acquiredFieldSet = collectAcquiredFields(layout);

    if (layout.firstFile == null) {
      throw new FormatException("No readable TIFF planes found in " + measurementPath);
    }

    sortAcquiredFields(layout.acquiredFields);
    collectPlaneExtents(layout, acquiredFieldSet);
    layout.channelIndexes = layout.uniqueChannels.toArray(
      new Integer[layout.uniqueChannels.size()]);
    Arrays.sort(layout.channelIndexes);
    return layout;
  }

  /** Only fields with at least one readable plane become Bio-Formats series. */
  private HashSet<Field> collectAcquiredFields(SeriesLayout layout) {
    HashSet<Field> acquiredFieldSet = new HashSet<Field>();
    for (Plane p : planeData) {
      if (p != null && p.file != null) {
        if (!allFiles.contains(p.file)) {
          allFiles.add(p.file);
        }
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

  /** Determine SizeZ/SizeT/channel coverage for every acquired field. */
  private void collectPlaneExtents(SeriesLayout layout, HashSet<Field> acquiredFieldSet) {
    for (Plane p : planeData) {
      if (p != null && acquiredFieldSet.contains(p.field)) {
        p.channelIndex = getLogicalChannelIndex(p);

        if (!layout.minMax.containsKey(p.field)) {
          layout.minMax.put(p.field, new MinMax());
        }
        MinMax m = layout.minMax.get(p.field);
        m.update(p);
        layout.uniqueChannels.add(p.channelIndex);
      }
    }
  }

  /** Use detailed action/channel mapping when present; fall back to raw channel IDs. */
  private int getLogicalChannelIndex(Plane p) {
    if (channels == null) {
      return p.channel;
    }
    return getChannelIndex(p);
  }

  /** Initialize the Bio-Formats core metadata once the logical layout is known. */
  private void initializeCoreMetadata(SeriesLayout layout) throws FormatException, IOException {
    reader = new MinimalTiffReader();
    reader.setId(layout.firstFile);
    core.clear();
    core.add(new CoreMetadata(reader.getCoreMetadataList().get(0)));

    core.get(0).dimensionOrder = "XYCZT";
    reversePlaneLookup = new int[layout.acquiredFields.size()][];

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
  private void populateReversePlaneLookup(SeriesLayout layout) {
    int[] planeLengths = new int[] {getSizeC(), getSizeZ(), getSizeT()};

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

      // Reindex from Yokogawa's channel/action numbering into the compact
      // channel list exposed by this reader for the current dataset.
      p.channelIndex = Arrays.binarySearch(layout.channelIndexes, p.channelIndex);

      p.series = series.intValue();
      MinMax m = layout.minMax.get(p.field);

      planeLengths[0] = core.get(p.series).sizeC / reader.getSizeC();
      planeLengths[1] = core.get(p.series).sizeZ;
      planeLengths[2] = core.get(p.series).sizeT;

      p.no = FormatTools.positionToRaster(planeLengths,
        new int[] {p.channelIndex, p.z - m.minZ, p.timepoint - m.minT});
      assignPlaneLookup(i, p);
    }
  }

  /** Prefer a real plane over metadata-only duplicate records for each position. */
  private void assignPlaneLookup(int planeIndex, Plane p) {
    if (reversePlaneLookup[p.series][p.no] < 0) {
      reversePlaneLookup[p.series][p.no] = planeIndex;
    }
    else {
      Plane existing = planeData.get(reversePlaneLookup[p.series][p.no]);
      if ((existing == null || existing.file == null) && p.file != null) {
        reversePlaneLookup[p.series][p.no] = planeIndex;
      }
      else if (p.file != null) {
        LOGGER.warn("Ignoring file {}", p.file);
        extraFiles.add(p.file);
      }
    }
  }

  /** Populate OME metadata after core metadata and plane lookup are complete. */
  private void populateMetadataStore(WPIHandler plate, SeriesLayout layout)
    throws FormatException
  {
    SeriesTiming[] timings = buildSeriesTimings();

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    populatePlateMetadata(store, plate, layout.acquiredFields, timings);
    setSeries(0);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      store.setPlateName(plate.getPlateName(), 0);
      store.setPlateDescription(plate.getPlateDescription(), 0);
      store.setPlateExternalIdentifier(plate.getPlateID(), 0);

      InstrumentMetadataIndexes indexes = populateInstrumentMetadata(store);
      populateSeriesMetadata(store, indexes.instrument, indexes.lightSourceIndexes,
        indexes.detectorIndexes, indexes.filterIndexes, indexes.usedObjectiveIDs, timings);
      setSeries(0);
    }
  }

  /** Build reusable instrument indexes before linking each Image/channel to them. */
  private InstrumentMetadataIndexes populateInstrumentMetadata(MetadataStore store)
    throws FormatException
  {
    InstrumentMetadataIndexes indexes = new InstrumentMetadataIndexes();
    if ((lightSources != null && lightSources.size() > 0) ||
      (channels != null && channels.size() > 0))
    {
      indexes.instrument = MetadataTools.createLSID("Instrument", 0);

      store.setInstrumentID(indexes.instrument, 0);
      populateLightSources(store, indexes.lightSourceIndexes);
      populateObjectives(store, indexes.usedObjectiveIDs);
      populateDetectors(store, indexes.detectorIndexes);
      populateFilters(store, indexes.filterIndexes);
      addYokogawaOriginalMetadata();
    }
    return indexes;
  }

  private void populatePlateMetadata(MetadataStore store, WPIHandler plate,
    ArrayList<Field> acquiredFields, SeriesTiming[] timings)
  {
    store.setPlateID(MetadataTools.createLSID("Plate", 0), 0);
    store.setPlateName(plate.getPlateName(), 0);
    store.setPlateDescription(plate.getPlateDescription(), 0);
    store.setPlateExternalIdentifier(plate.getPlateID(), 0);
    store.setPlateRows(new PositiveInteger(plate.getPlateRows()), 0);
    store.setPlateColumns(new PositiveInteger(plate.getPlateColumns()), 0);

    String plateAcqID = MetadataTools.createLSID("PlateAcquisition", 0, 0);
    store.setPlateAcquisitionID(plateAcqID, 0, 0);

    HashMap<Integer, Integer> wellFieldCounts = new HashMap<Integer, Integer>();
    int maxFieldCount = 0;
    for (Field field : acquiredFields) {
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

        if (!isWellAcquired(row, col)) {
          nextWell++;
          continue;
        }

        int wellSample = 0;
        for (Field field : acquiredFields) {
          if (field.row != row || field.column != col) {
            continue;
          }

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
            store.setImageAcquisitionDate(
              new Timestamp(timings[nextImage].startTimestamp), nextImage);
          }
          store.setPlateAcquisitionWellSampleRef(wellSampleID, 0, 0, nextImage);

          setSeries(nextImage);

          Plane p = lookupFirstBackedPlane(nextImage);
          if (p != null) {
            store.setWellSamplePositionX(
              FormatTools.createLength(p.xpos, UNITS.REFERENCEFRAME),
              0, nextWell, wellSample);
            store.setWellSamplePositionY(
              FormatTools.createLength(p.ypos, UNITS.REFERENCEFRAME),
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
    List<String> usedObjectiveIDs, SeriesTiming[] timings)
  {
    for (int i=0; i<getSeriesCount(); i++) {
      setSeries(i);
      if (instrument != null) {
        store.setImageInstrumentRef(instrument, i);
      }
      populateChannelMetadata(store, i, lightSourceIndexes, detectorIndexes,
        filterIndexes, usedObjectiveIDs);
      populatePlaneMetadata(store, i, timings[i]);
    }
  }

  private void populateChannelMetadata(MetadataStore store, int series,
    HashMap<Integer, Integer> lightSourceIndexes,
    HashMap<Integer, Integer> detectorIndexes,
    HashMap<FilterKey, Integer> filterIndexes,
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

      store.setChannelName("Action #" + (p.actionIndex + 1) +
        ", Channel #" + (channel.index + 1) + ", Camera #" + channel.cameraNumber, series, c);

      AcquisitionMode acquisitionMode = getYokogawaAcquisitionMode(channel);
      if (acquisitionMode != null) {
        store.setChannelAcquisitionMode(acquisitionMode, series, c);
      }
      ContrastMethod contrastMethod = getYokogawaContrastMethod(channel);
      if (contrastMethod != null) {
        store.setChannelContrastMethod(contrastMethod, series, c);
      }

      if (channel.color != null) {
        store.setChannelColor(channel.color, series, c);
      }
      if (channel.fluor != null && !channel.fluor.isEmpty()) {
        store.setChannelFluor(channel.fluor, series, c);
      }

      populateChannelLightSourceSettings(store, series, c, channel, lightSourceIndexes);
      populateChannelFilterSettings(store, series, c, channel, filterIndexes);
      populateDetectorSettings(store, series, c, channel, detectorIndexes);
      populateExposureTime(store, series, c, channel);
    }
  }

  private void populateChannelLightSourceSettings(MetadataStore store, int series,
    int channelIndex, Channel channel, HashMap<Integer, Integer> lightSourceIndexes)
  {
    Integer lightSource = getLinkedLaser(channel);
    if (lightSource == null || !lightSourceIndexes.containsKey(lightSource)) {
      return;
    }

    LightSource source = lightSources.get(lightSource);
    if (source.wavelength != null && source.wavelength > 0) {
      int index = lightSourceIndexes.get(lightSource);
      store.setChannelLightSourceSettingsID(
        MetadataTools.createLSID("LightSource", 0, index), series, channelIndex);
      // Yokogawa BP labels are detection filters; excitation comes from the
      // linked laser light-source wavelength.
      store.setChannelExcitationWavelength(
        new Length(source.wavelength, UNITS.NANOMETER), series, channelIndex);
    }
  }

  private void populateChannelFilterSettings(MetadataStore store, int series,
    int channelIndex, Channel channel, HashMap<FilterKey, Integer> filterIndexes)
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
    String binning = getBinningValue(channel.binning);
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
        int plane = getIndex(z, channelIndex, t);
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
      store.setPlanePositionX(FormatTools.createLength(plane.xpos, UNITS.REFERENCEFRAME), series, p);
      store.setPlanePositionY(FormatTools.createLength(plane.ypos, UNITS.REFERENCEFRAME), series, p);
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
    return null;
  }

  private boolean isConfocalFluorescence(Channel channel) {
    return "ConfocalFluorescence".equals(channel.kind);
  }

  private ContrastMethod getYokogawaContrastMethod(Channel channel) {
    if (channel == null || !channel.isBrightfield()) {
      return null;
    }
    try {
      return MetadataTools.getContrastMethod(BRIGHTFIELD);
    }
    catch (FormatException e) {
      LOGGER.debug("Ignoring unsupported CV7000 contrast method {}", BRIGHTFIELD, e);
    }
    return null;
  }

  private void populateLightSources(MetadataStore store,
    HashMap<Integer, Integer> lightSourceIndexes)
  {
    if (lightSources == null) {
      return;
    }
    int nextLightSource = 0;
    for (int i=0; i<lightSources.size(); i++) {
      LightSource l = lightSources.get(i);
      if ("Laser".equals(l.type)) {
        String laserID = MetadataTools.createLSID("LightSource", 0, nextLightSource);
        store.setLaserID(laserID, 0, nextLightSource);
        if (l.wavelength != null) {
          store.setLaserWavelength(
            new Length(l.wavelength, UNITS.NANOMETER), 0, nextLightSource);
        }
        if (l.power != null) {
          store.setLaserPower(new Power(l.power, UNITS.MILLIWATT), 0, nextLightSource);
        }
        lightSourceIndexes.put(i, nextLightSource);
        nextLightSource++;
      }
    }
  }

  private void populateObjectives(MetadataStore store, List<String> usedObjectiveIDs) {
    if (channels == null) {
      return;
    }
    for (Channel c : channels) {
      if (c.objectiveID != null && !usedObjectiveIDs.contains(c.objectiveID)) {
        int index = usedObjectiveIDs.size();
        String objectiveID = MetadataTools.createLSID("Objective", 0, index);
        store.setObjectiveID(objectiveID, 0, index);
        store.setObjectiveModel(c.objective, 0, index);
        usedObjectiveIDs.add(c.objectiveID);
      }
    }
  }

  private void populateDetectors(MetadataStore store,
    HashMap<Integer, Integer> detectorIndexes)
    throws FormatException
  {
    if (channels == null) {
      return;
    }
    for (Channel c : channels) {
      if (!detectorIndexes.containsKey(c.cameraNumber)) {
        int detector = detectorIndexes.size();
        detectorIndexes.put(c.cameraNumber, detector);
        String detectorID = MetadataTools.createLSID("Detector", 0, detector);
        store.setDetectorID(detectorID, 0, detector);
        if (c.cameraType != null && c.cameraType.trim().length() > 0) {
          store.setDetectorModel(c.cameraType, 0, detector);
        }
        store.setDetectorType(MetadataTools.getDetectorType("Other"), 0, detector);
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
      if (key.detectionFilter != null) {
        Length cutIn = key.detectionFilter.cutIn == null ?
          null : FormatTools.getCutIn(key.detectionFilter.cutIn);
        Length cutOut = key.detectionFilter.cutOut == null ?
          null : FormatTools.getCutOut(key.detectionFilter.cutOut);
        if (cutIn != null) {
          store.setTransmittanceRangeCutIn(cutIn, 0, filter);
        }
        if (cutOut != null) {
          store.setTransmittanceRangeCutOut(cutOut, 0, filter);
        }
      }
    }
  }

  private void addYokogawaOriginalMetadata() {
    if (allFiles != null) {
      for (String file : allFiles) {
        if (file != null && !checkSuffix(file, "tif")) {
          addRawSidecarMetadata(file);
          addYokogawaMetaList("Yokogawa Sidecar ", "File",
            new Location(file).getName());
        }
      }
    }
    addMeasurementDataOriginalMetadata();
    if (lightSources != null) {
      for (LightSource source : lightSources) {
        String prefix = "Yokogawa LightSource " + source.name + " ";
        addYokogawaMeta(prefix, "Type", source.type);
        addYokogawaMeta(prefix, "WaveLength", source.wavelength);
        addYokogawaMeta(prefix, "Power", source.power);
      }
    }
    if (channels == null) {
      return;
    }
    HashSet<String> seen = new HashSet<String>();
    for (Channel c : channels) {
      String key = c.timelineIndex + ":" + c.actionIndex + ":" + c.index;
      if (!seen.add(key)) {
        continue;
      }
      String prefix = "Yokogawa Timeline " + (c.timelineIndex + 1) +
        " Action " + (c.actionIndex + 1) + " Channel " + (c.index + 1) + " ";
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
  }

  private void addRawSidecarMetadata(String file) {
    Location location = new Location(file);
    String name = location.getName();
    if (name == null || MEASUREMENT_FILE.equals(name) || !isRawXMLSidecar(file)) {
      return;
    }

    try {
      String xml = DataTools.readFile(file);
      byte[] bytes = xml.getBytes(StandardCharsets.UTF_8);
      String encoded = Base64.getEncoder().encodeToString(bytes);
      int chunkCount = (encoded.length() + RAW_XML_CHUNK_SIZE - 1) / RAW_XML_CHUNK_SIZE;
      String prefix = "Yokogawa Raw XML " + name + " ";

      addYokogawaMeta(prefix, "Encoding", "base64; charset=UTF-8");
      addYokogawaMeta(prefix, "ByteLength", bytes.length);
      addYokogawaMeta(prefix, "SHA-256", sha256(bytes));
      addYokogawaMeta(prefix, "ChunkCount", chunkCount);
      for (int chunk=0; chunk<chunkCount; chunk++) {
        int start = chunk * RAW_XML_CHUNK_SIZE;
        int end = Math.min(start + RAW_XML_CHUNK_SIZE, encoded.length());
        addYokogawaMeta(prefix, "Chunk " + zeroPad(chunk + 1, 4),
          encoded.substring(start, end));
      }
    }
    catch (IOException e) {
      LOGGER.debug("Could not preserve raw CV7000 sidecar {}", file, e);
    }
  }

  private boolean isRawXMLSidecar(String file) {
    return checkSuffix(file, "wpi") || checkSuffix(file, "mrf") ||
      checkSuffix(file, "mes") || checkSuffix(file, "wpp") ||
      checkSuffix(file, "xml");
  }

  private void addMeasurementDataOriginalMetadata() {
    if (measurementPath == null) {
      return;
    }

    try {
      String xml = DataTools.readFile(measurementPath);
      byte[] bytes = xml.getBytes(StandardCharsets.UTF_8);
      addYokogawaMeta("Yokogawa MLF ", "File", new Location(measurementPath).getName());
      addYokogawaMeta("Yokogawa MLF ", "Encoding", "UTF-8");
      addYokogawaMeta("Yokogawa MLF ", "ByteLength", bytes.length);
      addYokogawaMeta("Yokogawa MLF ", "SHA-256", sha256(bytes));
    }
    catch (IOException e) {
      LOGGER.debug("Could not summarize CV7000 measurement data {}", measurementPath, e);
    }

    if (measurementHandler != null) {
      addYokogawaMeta("Yokogawa MLF ", "IMGRecordCount",
        measurementHandler.getImageRecordCount());
      addYokogawaMeta("Yokogawa MLF ", "FirstPlaneTime",
        measurementHandler.getFirstTimestamp());
      addYokogawaMeta("Yokogawa MLF ", "LastPlaneTime",
        measurementHandler.getLastTimestamp());
      addYokogawaMeta("Yokogawa MLF ", "FirstPlaneAction",
        measurementHandler.getFirstAction());
      addYokogawaMeta("Yokogawa MLF ", "LastPlaneAction",
        measurementHandler.getLastAction());
    }
  }

  private String sha256(byte[] bytes) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(bytes);
      StringBuilder hex = new StringBuilder(hash.length * 2);
      for (byte b : hash) {
        String value = Integer.toHexString(b & 0xff);
        if (value.length() == 1) {
          hex.append('0');
        }
        hex.append(value);
      }
      return hex.toString();
    }
    catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-256 not available", e);
    }
  }

  private String zeroPad(int value, int width) {
    String s = String.valueOf(value);
    while (s.length() < width) {
      s = "0" + s;
    }
    return s;
  }

  private void addYokogawaMeta(String prefix, String name, Object value) {
    addGlobalMeta(prefix + name, value);
  }

  private void addYokogawaMetaList(String prefix, String name, Object value) {
    addGlobalMetaList(prefix + name, value);
  }

  private void addYokogawaAttributes(String prefix, Attributes attributes) {
    if (attributes == null) {
      return;
    }
    for (int i=0; i<attributes.getLength(); i++) {
      addYokogawaMeta(prefix, getYokogawaAttributeName(attributes.getQName(i)),
        attributes.getValue(i));
    }
  }

  private String getYokogawaAttributeName(String qName) {
    if (qName == null) {
      return "";
    }
    int colon = qName.indexOf(":");
    return colon < 0 ? qName : qName.substring(colon + 1);
  }

  private Integer getLinkedLaser(Channel channel) {
    if (channel == null || channel.isBrightfield() ||
      channel.lightSourceRefs == null || lightSources == null)
    {
      return null;
    }
    for (Integer lightSource : channel.lightSourceRefs) {
      if (lightSource != null && lightSource >= 0 && lightSource < lightSources.size() &&
        "Laser".equals(lightSources.get(lightSource).type))
      {
        return lightSource;
      }
    }
    return null;
  }

  private String getBinningValue(String binning) {
    if (binning == null || binning.trim().length() == 0) {
      return null;
    }
    if (binning.indexOf('x') >= 0 || binning.indexOf('X') >= 0) {
      return binning;
    }
    return binning + "x" + binning;
  }

  private DetectionFilter parseDetectionFilter(String acquisition) {
    if (acquisition == null) {
      return null;
    }
    String trimmed = acquisition.trim();
    try {
      if (trimmed.startsWith("BP") && trimmed.indexOf("/") > 2) {
        String center = trimmed.substring(2, trimmed.indexOf("/"));
        String width = trimmed.substring(trimmed.indexOf("/") + 1);
        Double parsedCenter = DataTools.parseDouble(center);
        Double parsedWidth = DataTools.parseDouble(width);
        if (parsedCenter != null && parsedWidth != null) {
          return DetectionFilter.bandPass(parsedCenter, parsedWidth);
        }
      }
      else if (trimmed.startsWith("LP") && trimmed.length() > 2) {
        Double cutIn = DataTools.parseDouble(trimmed.substring(2));
        if (cutIn != null) {
          return DetectionFilter.longPass(cutIn);
        }
      }
      else if (trimmed.startsWith("SP") && trimmed.length() > 2) {
        Double cutOut = DataTools.parseDouble(trimmed.substring(2));
        if (cutOut != null) {
          return DetectionFilter.shortPass(cutOut);
        }
      }
    }
    catch (RuntimeException e) {
      LOGGER.debug("Ignoring invalid CV7000 detection filter value {}", acquisition, e);
    }
    return null;
  }

  private Double parseYokogawaGain(String value) {
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
    return DataTools.parseDouble(trimmed.substring(start, end));
  }

  private Integer parseInteger(String value) {
    if (value == null || value.trim().length() == 0) {
      return null;
    }
    return Integer.valueOf(value);
  }

  private int getChannelIndex(Plane p) {
    int index = -1;
    for (int action=0; action<=p.actionIndex; action++) {
      for (Channel ch : channels) {
        if (ch.timelineIndex == p.timelineIndex &&
          ch.actionIndex == action)
        {
          index++;
          if (ch.index == p.channel && ch.actionIndex == p.actionIndex) {
            return index;
          }
        }
      }
    }
    return index;
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
      if (channel == null || channel.physicalSizeZ == null) {
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

  private Channel lookupChannel(Plane p) {
    Channel rawChannel = null;
    Channel populatedRawChannel = null;
    for (Channel ch : channels) {
      if (ch.index == p.channel &&
        ch.timelineIndex == p.timelineIndex &&
        ch.actionIndex == p.actionIndex)
      {
        return ch;
      }
      if (ch.index == p.channel) {
        if (rawChannel == null) {
          rawChannel = ch;
        }
        if (populatedRawChannel == null && ch.hasChannelSettings()) {
          populatedRawChannel = ch;
        }
      }
    }
    return populatedRawChannel == null ? rawChannel : populatedRawChannel;
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
    String xml = DataTools.readFile(filename).trim();
    if (xml.endsWith(">>")) {
      xml = xml.substring(0, xml.length() - 1);
    }
    return xml;
  }

  private boolean isWellAcquired(int row, int col) {
    String key = row + "-" + col;
    if (acquiredWells.containsKey(key)) {
      return acquiredWells.get(key);
    }
    if (planeData != null) {
      for (Plane p : planeData) {
        if (p != null && p.file != null && p.field.row == row && p.field.column == col) {
          acquiredWells.put(key, true);
          return true;
        }
      }
    }
    acquiredWells.put(key, false);
    return false;
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

  // -- Helper classes --

  /** Resolved dataset paths used during the parsing phase. */
  class DatasetPaths {
    public Location parent;
    public String wpiPath;
    public Location measurementData;
    public Location measurementDetail;
  }

  /** Intermediate layout data used to translate Yokogawa records into series. */
  class SeriesLayout {
    public String firstFile;
    public ArrayList<Field> acquiredFields = new ArrayList<Field>();
    public HashMap<Field, MinMax> minMax = new HashMap<Field, MinMax>();
    public HashSet<Integer> uniqueChannels = new HashSet<Integer>();
    public Integer[] channelIndexes;
    public HashMap<Field, Integer> fieldToSeries = new HashMap<Field, Integer>();
  }

  /** OME instrument indexes shared by channel metadata population. */
  class InstrumentMetadataIndexes {
    public String instrument;
    public HashMap<Integer, Integer> lightSourceIndexes =
      new HashMap<Integer, Integer>();
    public HashMap<Integer, Integer> detectorIndexes =
      new HashMap<Integer, Integer>();
    public HashMap<FilterKey, Integer> filterIndexes =
      new HashMap<FilterKey, Integer>();
    public List<String> usedObjectiveIDs = new ArrayList<String>();
  }

  class SeriesTiming {
    public Long startMillis;
    public String startTimestamp;
  }

  class WPIHandler extends BaseHandler {
    private int plateRows;
    private int plateColumns;
    private String name;
    private String plateID;
    private String description;

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

    public String getPlateDescription() {
      return description;
    }

    @Override
    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      if (qName.equals("bts:WellPlate")) {
        name = attributes.getValue("bts:Name");
        plateID = attributes.getValue("bts:ProductID");
        plateRows = Integer.parseInt(attributes.getValue("bts:Rows"));
        plateColumns = Integer.parseInt(attributes.getValue("bts:Columns"));
      }
    }

  }

  class MeasurementDataHandler extends BaseHandler {
    private StringBuffer currentValue = new StringBuffer();
    private String btsType;
    private ArrayList<Plane> planes = new ArrayList<Plane>();
    private String parentDir;
    private int imageRecordCount;
    private String firstTimestamp;
    private String lastTimestamp;
    private String firstAction;
    private String lastAction;

    private int currentField = -1;

    public MeasurementDataHandler(String parentDir) {
      super();
      this.parentDir = parentDir;
    }

    public ArrayList<Plane> getPlanes() {
      return planes;
    }

    public int getImageRecordCount() {
      return imageRecordCount;
    }

    public String getFirstTimestamp() {
      return firstTimestamp;
    }

    public String getLastTimestamp() {
      return lastTimestamp;
    }

    public String getFirstAction() {
      return firstAction;
    }

    public String getLastAction() {
      return lastAction;
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
        btsType = attributes.getValue("bts:Type");
        if (qName.equals("bts:MeasurementRecord") && btsType.equals("IMG")) {
          // When the instrument is recording an acquisition error the "type"
          // will be "ERR" so we can skip those.
          Plane p = new Plane();
          p.field = new Field();
          p.field.row = Integer.parseInt(attributes.getValue("bts:Row")) - 1;
          p.field.column = Integer.parseInt(attributes.getValue("bts:Column")) - 1;
          p.timepoint = Integer.parseInt(attributes.getValue("bts:TimePoint")) - 1;
          p.field.field = Integer.parseInt(attributes.getValue("bts:FieldIndex")) - 1;
          p.z = Integer.parseInt(attributes.getValue("bts:ZIndex")) - 1;
          p.channel = Integer.parseInt(attributes.getValue("bts:Ch")) - 1;
          p.actionIndex = Integer.parseInt(attributes.getValue("bts:ActionIndex")) - 1;
          p.timelineIndex = Integer.parseInt(attributes.getValue("bts:TimelineIndex")) - 1;

          if (p.field.field != currentField) {
            currentField = p.field.field;
          }

          p.xpos = DataTools.parseDouble(attributes.getValue("bts:X"));
          p.ypos = DataTools.parseDouble(attributes.getValue("bts:Y"));
          p.zpos = DataTools.parseDouble(attributes.getValue("bts:Z"));
          p.timestamp = attributes.getValue("bts:Time");
          p.actionName = attributes.getValue("bts:Action");
          imageRecordCount++;
          if (firstTimestamp == null) {
            firstTimestamp = p.timestamp;
            firstAction = p.actionName;
          }
          lastTimestamp = p.timestamp;
          lastAction = p.actionName;
          planes.add(p);
        }
      }
      catch (RuntimeException e) {
        if (LOGGER.isErrorEnabled()) {
          Map<String, String> attributeMap = new HashMap<String, String>();
          for (int i = 0; i < attributes.getLength(); i++) {
            attributeMap.put(
                attributes.getQName(i), attributes.getValue(i));
          }
          LOGGER.error("Error parsing attributes: {}", attributeMap, e);
        }
        throw e;
      }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
      String value = currentValue.toString();
      if (qName.equals("bts:MeasurementRecord") && btsType.equals("IMG") &&
        value.trim().length() > 0) {
        Location imgFile = new Location(parentDir, value);
        if (imgFile.exists()) {
          planes.get(planes.size() - 1).file = imgFile.getAbsolutePath();
        }
      }
    }

  }

  class MeasurementDetailHandler extends BaseHandler {

    // -- DefaultHandler API methods --

    @Override
    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      if (qName.equals("bts:MeasurementSamplePlate")) {
        addYokogawaAttributes("Yokogawa MRF MeasurementSamplePlate ", attributes);
        wppPath = attributes.getValue("bts:WellPlateProductFileName");
        if (wppPath != null && wppPath.trim().length() == 0) {
          wppPath = null;
        }
      }
      else if (qName.equals("bts:MeasurementChannel")) {
        Channel c = new Channel();
        c.index = Integer.parseInt(attributes.getValue("bts:Ch")) - 1;
        addYokogawaAttributes(
          "Yokogawa MRF Channel " + (c.index + 1) + " ", attributes);
        c.xSize = DataTools.parseDouble(attributes.getValue("bts:HorizontalPixelDimension"));
        c.ySize = DataTools.parseDouble(attributes.getValue("bts:VerticalPixelDimension"));
        c.cameraNumber = Integer.parseInt(attributes.getValue("bts:CameraNumber"));
        c.inputBitDepth = parseInteger(attributes.getValue("bts:InputBitDepth"));
        c.inputLevel = parseInteger(attributes.getValue("bts:InputLevel"));
        c.horizontalPixels = parseInteger(attributes.getValue("bts:HorizontalPixels"));
        c.verticalPixels = parseInteger(attributes.getValue("bts:VerticalPixels"));
        c.filterWheelPosition = parseInteger(attributes.getValue("bts:FilterWheelPosition"));
        c.filterPosition = parseInteger(attributes.getValue("bts:FilterPosition"));
        c.correctionFile = attributes.getValue("bts:ShadingCorrectionSource");
        if (c.correctionFile != null && c.correctionFile.trim().length() == 0) {
          c.correctionFile = null;
        }
        channels.add(c);
      }
      else if (qName.equals("bts:MeasurementDetail")) {
        addYokogawaAttributes("Yokogawa MRF MeasurementDetail ", attributes);
        startTime = attributes.getValue("bts:BeginTime");
        endTime = attributes.getValue("bts:EndTime");
        settingsPath = attributes.getValue("bts:MeasurementSettingFileName");

        String system = attributes.getValue("bts:TargetSystem");
        addGlobalMeta("Acquisition system", system);
        if (!system.toLowerCase().startsWith("cv7000")) {
          LOGGER.warn("Found data from {}; this is not well-supported", system);
        }
      }
    }

  }

  class WPPHandler extends BaseHandler {

    @Override
    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      if (qName.equals("bts:WellPlateProduct")) {
        addYokogawaAttributes("Yokogawa WPP ", attributes);
      }
    }

  }

  class MeasurementSettingsHandler extends BaseHandler {
    private StringBuffer currentValue = new StringBuffer();
    private int currentChannelIndex = -1;
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
      if (qName.equals("bts:MeasurementSetting")) {
        addYokogawaAttributes("Yokogawa MES MeasurementSetting ", attributes);
      }
      else if (qName.equals("bts:LightSource")) {
        LightSource l = new LightSource();
        l.name = attributes.getValue("bts:Name");
        l.type = attributes.getValue("bts:Type");
        addYokogawaAttributes("Yokogawa MES LightSource " + l.name + " ", attributes);

        String wavelength = attributes.getValue("bts:WaveLength");
        String power = attributes.getValue("bts:Power");

        l.wavelength = DataTools.parseDouble(wavelength);
        l.power = DataTools.parseDouble(power);

        lightSources.add(l);
      }
      else if (qName.equals("bts:Channel")) {
        currentChannelIndex = -1;
        String ch = attributes.getValue("bts:Ch");
        if (ch != null) {
          int index = Integer.parseInt(ch) - 1;
          if (index >= 0 && index < channels.size()) {
            currentChannelIndex = index;

            Channel template = new Channel();
            template.index = index;
            template.target = attributes.getValue("bts:Target");
            addYokogawaAttributes(
              "Yokogawa MES Channel " + (template.index + 1) + " ", attributes);
            template.objectiveID = attributes.getValue("bts:ObjectiveID");
            template.objective = attributes.getValue("bts:Objective");
            template.binning = attributes.getValue("bts:Binning");
            template.methodID = attributes.getValue("bts:MethodID");
            template.method = attributes.getValue("bts:Method");
            template.filterID = attributes.getValue("bts:FilterID");
            template.kind = attributes.getValue("bts:Kind");
            template.andorParameterID = attributes.getValue("bts:AndorParameterID");
            template.andorParameter = attributes.getValue("bts:AndorParameter");
            template.detectorGain = parseYokogawaGain(template.andorParameter);
            template.cameraType = attributes.getValue("bts:CameraType");
            template.inputLevel = parseInteger(attributes.getValue("bts:InputLevel"));

            String mag = attributes.getValue("bts:Magnification");
            template.magnification = DataTools.parseDouble(mag);

            String exposure = attributes.getValue("bts:ExposureTime");
            template.exposureTime = DataTools.parseDouble(exposure);

            String color = attributes.getValue("bts:Color");
            if (color != null) {
              color = color.replaceAll("#", "");
              // ignore unless at least R, G, B are defined
              if (color.length() >= 6) {
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
            }

            template.acquisition = attributes.getValue("bts:Acquisition");
            // Yokogawa Acquisition values such as BP676/29 identify detection
            // filters.  Excitation comes from the LightSourceName link.
            template.detectionFilter = parseDetectionFilter(template.acquisition);

            template.fluor = attributes.getValue("bts:Fluorophore");
            applyChannelSettings(template);
          }
        }
      }
      else if (qName.equals("bts:Timeline")) {
        timelineIndex++;
        actionIndex = -1;
        targetWellIndex = -1;
        pointIndex = -1;
        currentPhysicalSizeZ = null;
        actionRunMode = null;
        actionAFSearch = null;
        addYokogawaAttributes(
          "Yokogawa MES Timeline " + (timelineIndex + 1) + " ", attributes);
      }
      else if (qName.equals("bts:TargetWell")) {
        targetWellIndex++;
        addYokogawaAttributes(
          "Yokogawa MES Timeline " + (timelineIndex + 1) +
          " TargetWell " + (targetWellIndex + 1) + " ", attributes);
      }
      else if (qName.equals("bts:PointSequence")) {
        addYokogawaAttributes(
          "Yokogawa MES Timeline " + (timelineIndex + 1) +
          " PointSequence ", attributes);
      }
      else if (qName.equals("bts:FixedPosition")) {
        addYokogawaAttributes(
          "Yokogawa MES Timeline " + (timelineIndex + 1) +
          " FixedPosition ", attributes);
      }
      else if (qName.equals("bts:Point")) {
        pointIndex++;
        addYokogawaAttributes(
          "Yokogawa MES Timeline " + (timelineIndex + 1) +
          " Point " + (pointIndex + 1) + " ", attributes);
      }
      else if (qName.equals("bts:ActionList")) {
        actionRunMode = attributes.getValue("bts:RunMode");
        actionAFSearch = attributes.getValue("bts:AFSearch");
        addYokogawaAttributes(
          "Yokogawa MES Timeline " + (timelineIndex + 1) +
          " ActionList ", attributes);
      }
      else if (qName.startsWith("bts:ActionAcquire")) {
        actionIndex++;
        currentActionType = getYokogawaAttributeName(qName);
        currentActionXOffset = attributes.getValue("bts:XOffset");
        currentActionYOffset = attributes.getValue("bts:YOffset");
        currentActionAFShiftBase = attributes.getValue("bts:AFShiftBase");
        currentActionTopDistance = attributes.getValue("bts:TopDistance");
        currentActionBottomDistance = attributes.getValue("bts:BottomDistance");
        currentActionSliceLength = attributes.getValue("bts:SliceLength");
        currentActionUseSoftFocus = attributes.getValue("bts:UseSoftFocus");
        addYokogawaAttributes(
          "Yokogawa MES Timeline " + (timelineIndex + 1) +
          " Action " + (actionIndex + 1) + " ", attributes);
        currentPhysicalSizeZ = DataTools.parseDouble(
          attributes.getValue("bts:SliceLength"));
      }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
      String value = currentValue.toString();

      if (qName.equals("bts:LightSourceName") && currentChannelIndex >= 0) {
        addYokogawaMeta(
          "Yokogawa MES Channel " + (currentChannelIndex + 1) + " ",
          "LightSourceName", value);
        int index = -1;
        for (int i=0; i<lightSources.size(); i++) {
          if (lightSources.get(i).name.equals(value)) {
            index = i;
          }
        }
        if (index >= 0) {
          addLightSourceRef(currentChannelIndex, index);
        }
      }
      else if (qName.equals("bts:Channel")) {
        currentChannelIndex = -1;
      }
      else if (qName.equals("bts:Ch")) {
        int channelIndex = Integer.parseInt(value) - 1;
        if (channelIndex >= 0 && channelIndex < channels.size()) {
          // the same channel may be acquired multiple times
          // if this is the first time the channel is acquired, set the indexes
          // if this is the second (or more) time the channel is acquired,
          // duplicate the channel so that each action has its own copy with
          // the correct indexes
          Channel ch = channels.get(channelIndex);
          if (ch.timelineIndex == -1 && ch.actionIndex == -1) {
            ch.timelineIndex = timelineIndex;
            ch.actionIndex = actionIndex;
            ch.physicalSizeZ = currentPhysicalSizeZ;
            ch.copyActionSettings(actionRunMode, actionAFSearch, currentActionType,
              currentActionXOffset, currentActionYOffset, currentActionAFShiftBase,
              currentActionTopDistance, currentActionBottomDistance,
              currentActionSliceLength, currentActionUseSoftFocus);
          }
          else {
            Channel duplicate = new Channel(ch);
            duplicate.timelineIndex = timelineIndex;
            duplicate.actionIndex = actionIndex;
            duplicate.physicalSizeZ = currentPhysicalSizeZ;
            duplicate.copyActionSettings(actionRunMode, actionAFSearch, currentActionType,
              currentActionXOffset, currentActionYOffset, currentActionAFShiftBase,
              currentActionTopDistance, currentActionBottomDistance,
              currentActionSliceLength, currentActionUseSoftFocus);
            channels.add(duplicate);
          }
        }
      }
      else if (qName.startsWith("bts:ActionAcquire")) {
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
    }

    private void applyChannelSettings(Channel template) {
      for (Channel ch : channels) {
        if (ch.index == template.index) {
          ch.copyChannelSettings(template);
        }
      }
    }

    private void addLightSourceRef(int rawChannelIndex, int lightSourceIndex) {
      for (Channel ch : channels) {
        if (ch.index == rawChannelIndex &&
          !ch.lightSourceRefs.contains(lightSourceIndex))
        {
          ch.lightSourceRefs.add(lightSourceIndex);
        }
      }
    }

  }

  class LightSource {
    public String name;
    public String type;
    public Double wavelength;
    public Double power;
  }

  static class DetectionFilter {
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

    public static DetectionFilter longPass(Double cutIn) {
      DetectionFilter filter = new DetectionFilter("LongPass");
      filter.cutIn = cutIn;
      return filter;
    }

    public static DetectionFilter shortPass(Double cutOut) {
      DetectionFilter filter = new DetectionFilter("ShortPass");
      filter.cutOut = cutOut;
      return filter;
    }

    private DetectionFilter(String filterType) {
      this.filterType = filterType;
    }
  }

  class FilterKey {
    public String filterID;
    public String acquisition;
    public Integer filterWheelPosition;
    public Integer filterPosition;
    public DetectionFilter detectionFilter;

    public FilterKey(Channel ch) {
      filterID = ch.filterID;
      acquisition = ch.acquisition;
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
        same(filterWheelPosition, key.filterWheelPosition) &&
        same(filterPosition, key.filterPosition);
    }

    @Override
    public int hashCode() {
      int code = 17;
      code = 31 * code + hash(filterID);
      code = 31 * code + hash(acquisition);
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

  class Channel {
    public int timelineIndex = -1;
    public int actionIndex = -1;
    public int index;
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

  class Plane {
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
    public double xpos;
    public double ypos;
    public double zpos;
    public int series;
    public int no;
    public int actionIndex;
    public int timelineIndex;
  }

  class Field {
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

  class MinMax {
    public int minZ = Integer.MAX_VALUE;
    public int maxZ = 0;
    public int minC = Integer.MAX_VALUE;
    public int maxC = 0;
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

      // Min and max channel indexes are not currently used, but keeping them
      // complete makes future CV7000/8000 channel-layout work less fragile.
      if (p.channelIndex > maxC) {
        maxC = p.channelIndex;
      }
      if (p.channelIndex < minC) {
        minC = p.channelIndex;
      }
    }
  }

}
