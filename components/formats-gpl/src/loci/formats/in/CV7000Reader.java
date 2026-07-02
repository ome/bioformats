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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
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
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffParser;

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
    WPIHandler plate = new WPIHandler();
    String wpiXML = readSanitizedXML(id);
    XMLTools.parseXML(wpiXML, plate);

    Location parent = new Location(id).getAbsoluteFile().getParentFile();
    String[] listedFiles = parent.list(true);
    Arrays.sort(listedFiles);
    for (int i=0; i<listedFiles.length; i++) {
      Location file = new Location(parent, listedFiles[i]);
      if (!file.isDirectory() && file.canRead()) {
        allFiles.add(file.getAbsolutePath());
      }
    }
    Location measurementData = new Location(parent, MEASUREMENT_FILE);

    if (!measurementData.exists()) {
      throw new FormatException("Missing " + MEASUREMENT_FILE + " file");
    }

    measurementPath = measurementData.getAbsolutePath();
    MeasurementDataHandler measurementHandler = new MeasurementDataHandler(parent.getAbsolutePath());
    XMLTools.parseXML(readSanitizedXML(measurementPath), measurementHandler);

    planeData = measurementHandler.getPlanes();

    Location measurementDetail = new Location(parent, MEASUREMENT_DETAIL);
    if (!measurementDetail.exists()) {
      LOGGER.warn("Missing " + MEASUREMENT_DETAIL + " file");
    }
    else {
      channels = new ArrayList<Channel>();
      detailPath = measurementDetail.getAbsolutePath();
      MeasurementDetailHandler detailHandler = new MeasurementDetailHandler();
      XMLTools.parseXML(readSanitizedXML(detailPath), detailHandler);
      if (wppPath != null) {
        wppPath = new Location(parent, wppPath).getAbsolutePath();
      }
      if (settingsPath != null) {
        settingsPath = new Location(parent, settingsPath).getAbsolutePath();
      }
    }

    if (settingsPath != null && new Location(settingsPath).exists()) {
      lightSources = new ArrayList<LightSource>();
      MeasurementSettingsHandler settingsHandler = new MeasurementSettingsHandler();
      String xml = readSanitizedXML(settingsPath);
      if (xml.length() > 0) {
        XMLTools.parseXML(xml, settingsHandler);
      }
    }

    channels.sort(new Comparator<Channel>() {
      @Override
      public int compare(Channel c1, Channel c2) {
        if (c1.actionIndex != c2.actionIndex) {
          return c1.actionIndex - c2.actionIndex;
        }
        return c1.index - c2.index;
      }
    });

    for (Channel ch : channels) {
      if (ch.correctionFile != null) {
        ch.correctionFile = new Location(parent, ch.correctionFile).getAbsolutePath();
      }
    }

    String firstFile = null;
    HashMap<Field, MinMax> minMax = new HashMap<Field, MinMax>();

    ArrayList<Field> acquiredFields = new ArrayList<Field>();
    HashSet<Field> acquiredFieldSet = new HashSet<Field>();
    HashSet<Integer> uniqueChannels = new HashSet<Integer>();

    for (Plane p : planeData) {
      if (p != null && p.file != null) {
        if (!allFiles.contains(p.file)) {
          allFiles.add(p.file);
        }
        if (firstFile == null) {
          firstFile = p.file;
        }
        if (acquiredFieldSet.add(p.field)) {
          acquiredFields.add(p.field);
        }
      }
    }

    if (firstFile == null) {
      throw new FormatException("No readable TIFF planes found in " + measurementPath);
    }

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

    for (Plane p : planeData) {
      if (p != null && acquiredFieldSet.contains(p.field)) {
        p.channelIndex = getChannelIndex(p);

        if (!minMax.containsKey(p.field)) {
          minMax.put(p.field, new MinMax());
        }
        MinMax m = minMax.get(p.field);

        if (p.timepoint > m.maxT) {
          m.maxT = p.timepoint;
        }
        if (p.timepoint < m.minT) {
          m.minT = p.timepoint;
        }
        if (p.z > m.maxZ) {
          m.maxZ = p.z;
        }
        if (p.z < m.minZ) {
          m.minZ = p.z;
        }

        // min and max channel indexes not currently used,
        // but continue to calculate for completeness
        // they may be needed in future CV7000/8000 work
        if (p.channelIndex > m.maxC) {
          m.maxC = p.channelIndex;
        }
        if (p.channelIndex < m.minC) {
          m.minC = p.channelIndex;
        }
        uniqueChannels.add(p.channelIndex);
      }
    }

    reader = new MinimalTiffReader();
    reader.setId(firstFile);
    core.clear();
    core.add(new CoreMetadata(reader.getCoreMetadataList().get(0)));

    core.get(0).dimensionOrder = "XYCZT";

    reversePlaneLookup = new int[acquiredFields.size()][];

    Integer[] channelIndexes = uniqueChannels.toArray(new Integer[uniqueChannels.size()]);
    Arrays.sort(channelIndexes);

    HashMap<Field, Integer> fieldToSeries = new HashMap<Field, Integer>();
    for (int i=0; i<acquiredFields.size(); i++) {
      if (i > 0) {
        core.add(new CoreMetadata(core.get(0)));
      }

      Field field = acquiredFields.get(i);
      fieldToSeries.put(field, i);
      MinMax m = minMax.get(field);
      core.get(i).sizeZ = (m.maxZ - m.minZ) + 1;
      core.get(i).sizeT = (m.maxT - m.minT) + 1;
      core.get(i).sizeC = reader.getSizeC() * uniqueChannels.size();
      core.get(i).imageCount = core.get(i).sizeZ * core.get(i).sizeT *
        (core.get(i).sizeC / reader.getSizeC());
      reversePlaneLookup[i] = new int[core.get(i).imageCount];
      Arrays.fill(reversePlaneLookup[i], -1);
    }

    int[] planeLengths = new int[] {getSizeC(), getSizeZ(), getSizeT()};

    extraFiles = new ArrayList<String>();
    for (int i=0; i<planeData.size(); i++) {
      Plane p = planeData.get(i);
      if (p == null) {
        continue;
      }

      Integer series = fieldToSeries.get(p.field);
      if (series == null) {
        continue;
      }

      // reindex so that the plane's channel index is into
      // the list of unique acquired channels, not the list of all channels
      // that might have been acquired
      // this eliminates the need to correct for the minimum C index later on
      p.channelIndex = Arrays.binarySearch(channelIndexes, p.channelIndex);

      p.series = series.intValue();
      MinMax m = minMax.get(p.field);

      planeLengths[0] = core.get(p.series).sizeC / reader.getSizeC();
      planeLengths[1] = core.get(p.series).sizeZ;
      planeLengths[2] = core.get(p.series).sizeT;

      p.no = FormatTools.positionToRaster(planeLengths,
        new int[] {p.channelIndex, p.z - m.minZ, p.timepoint - m.minT});

      if (reversePlaneLookup[p.series][p.no] < 0) {
        reversePlaneLookup[p.series][p.no] = i;
      }
      else {
        Plane existing = planeData.get(reversePlaneLookup[p.series][p.no]);
        if ((existing == null || existing.file == null) && p.file != null) {
          reversePlaneLookup[p.series][p.no] = i;
        }
        else if (p.file != null) {
          LOGGER.warn("Ignoring file {}", p.file);
          extraFiles.add(p.file);
        }
      }
    }

    // populate the MetadataStore

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

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
          store.setPlateAcquisitionWellSampleRef(wellSampleID, 0, 0, nextImage);

          setSeries(nextImage);

          // find the first plane with pixels to set WellSample positions
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
    setSeries(0);

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      store.setPlateName(plate.getPlateName(), 0);
      store.setPlateDescription(plate.getPlateDescription(), 0);
      store.setPlateExternalIdentifier(plate.getPlateID(), 0);

      String instrument = null;
      HashMap<Integer, Integer> lightSourceIndexes = new HashMap<Integer, Integer>();
      HashMap<Integer, Integer> detectorIndexes = new HashMap<Integer, Integer>();
      HashMap<FilterKey, Integer> filterIndexes = new HashMap<FilterKey, Integer>();
      List<String> usedObjectiveIDs = new ArrayList<String>();
      if ((lightSources != null && lightSources.size() > 0) ||
        (channels != null && channels.size() > 0))
      {
        instrument = MetadataTools.createLSID("Instrument", 0);

        store.setInstrumentID(instrument, 0);
        populateLightSources(store, lightSourceIndexes);
        populateObjectives(store, usedObjectiveIDs);
        populateDetectors(store, detectorIndexes);
        populateFilters(store, filterIndexes);
        addYokogawaOriginalMetadata();
      }

      for (int i=0; i<getSeriesCount(); i++) {
        setSeries(i);
        if (instrument != null) {
          store.setImageInstrumentRef(instrument, i);
        }
        if (channels != null) {
          Length physicalSizeZ = getPhysicalSizeZ(i);
          if (physicalSizeZ != null) {
            store.setPixelsPhysicalSizeZ(physicalSizeZ, i);
          }

          boolean physicalSizeSet = false;
          for (int c=0; c<getSizeC(); c++) {
            Plane p = lookupRepresentativePlane(i, c);
            if (p == null) {
              // There was likely an error during acquisition for this
              // particular channel.  Skip it.
              continue;
            }
            Channel channel = lookupChannel(p);
            if (channel == null) {
              continue;
            }

            if (!physicalSizeSet) {
              store.setPixelsPhysicalSizeX(FormatTools.getPhysicalSizeX(channel.xSize), i);
              store.setPixelsPhysicalSizeY(FormatTools.getPhysicalSizeY(channel.ySize), i);
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
              store.setObjectiveSettingsID(objectiveID, i);
            }

            // the index here is the original bts:Ch index in
            // the *.mes and *.mrf files
            store.setChannelName("Action #" + (p.actionIndex + 1) +
              ", Channel #" + (channel.index + 1) + ", Camera #" + channel.cameraNumber, i, c);

            if (channel.color != null) {
              store.setChannelColor(channel.color, i, c);
            }
            if (channel.fluor != null && !channel.fluor.isEmpty()) {
              store.setChannelFluor(channel.fluor, i, c);
            }

            Integer lightSource = getLinkedLaser(channel);
            if (lightSource != null && lightSourceIndexes.containsKey(lightSource)) {
              LightSource source = lightSources.get(lightSource);
              if (source.wavelength != null && source.wavelength > 0) {
                int index = lightSourceIndexes.get(lightSource);
                store.setChannelLightSourceSettingsID(
                  MetadataTools.createLSID("LightSource", 0, index), i, c);
                store.setChannelExcitationWavelength(
                  new Length(source.wavelength, UNITS.NANOMETER), i, c);
              }
            }

            if (!channel.isBrightfield() && channel.detectionFilter != null &&
              channel.detectionFilter.center != null)
            {
              store.setChannelEmissionWavelength(
                new Length(channel.detectionFilter.center, UNITS.NANOMETER), i, c);
            }

            FilterKey filter = new FilterKey(channel);
            if (filterIndexes.containsKey(filter)) {
              store.setLightPathEmissionFilterRef(
                MetadataTools.createLSID("Filter", 0, filterIndexes.get(filter)), i, c, 0);
            }

            if (detectorIndexes.containsKey(channel.cameraNumber)) {
              String detectorID = MetadataTools.createLSID(
                "Detector", 0, detectorIndexes.get(channel.cameraNumber));
              store.setDetectorSettingsID(detectorID, i, c);
              String binning = getBinningValue(channel.binning);
              if (binning != null) {
                try {
                  store.setDetectorSettingsBinning(
                    MetadataTools.getBinning(binning), i, c);
                }
                catch (FormatException e) {
                  LOGGER.debug("Ignoring invalid CV7000 binning value {}", binning, e);
                }
              }
            }

            if (channel.exposureTime != null) {
              Time exposure = new Time(channel.exposureTime, UNITS.MILLISECOND);
              for (int z=0; z<getSizeZ(); z++) {
                for (int t=0; t<getSizeT(); t++) {
                  int plane = getIndex(z, c, t);
                  store.setPlaneExposureTime(exposure, i, plane);
                }
              }
            }
          }
        }

        for (int p=0; p<getImageCount(); p++) {
          Plane plane = lookupPlane(i, p);
          if (plane == null) {
            continue;
          }
          store.setPlanePositionX(FormatTools.createLength(plane.xpos, UNITS.REFERENCEFRAME), i, p);
          store.setPlanePositionY(FormatTools.createLength(plane.ypos, UNITS.REFERENCEFRAME), i, p);
          store.setPlanePositionZ(FormatTools.createLength(plane.zpos, UNITS.REFERENCEFRAME), i, p);
        }
      }
      setSeries(0);
    }
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
    if (lightSources != null) {
      for (LightSource source : lightSources) {
        String prefix = "Yokogawa LightSource " + source.name + " ";
        addGlobalMeta(prefix + "Type", source.type);
        addGlobalMeta(prefix + "WaveLength", source.wavelength);
        addGlobalMeta(prefix + "Power", source.power);
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
      addGlobalMeta(prefix + "Target", c.target);
      addGlobalMeta(prefix + "Kind", c.kind);
      addGlobalMeta(prefix + "MethodID", c.methodID);
      addGlobalMeta(prefix + "Method", c.method);
      addGlobalMeta(prefix + "FilterID", c.filterID);
      addGlobalMeta(prefix + "Acquisition", c.acquisition);
      if (c.detectionFilter != null) {
        addGlobalMeta(prefix + "DetectionFilterType", c.detectionFilter.filterType);
        addGlobalMeta(prefix + "DetectionFilterCenter", c.detectionFilter.center);
        addGlobalMeta(prefix + "DetectionFilterWidth", c.detectionFilter.width);
        addGlobalMeta(prefix + "DetectionFilterCutIn", c.detectionFilter.cutIn);
        addGlobalMeta(prefix + "DetectionFilterCutOut", c.detectionFilter.cutOut);
      }
      addGlobalMeta(prefix + "CameraNumber", c.cameraNumber);
      addGlobalMeta(prefix + "CameraType", c.cameraType);
      addGlobalMeta(prefix + "Binning", c.binning);
      addGlobalMeta(prefix + "AndorParameterID", c.andorParameterID);
      addGlobalMeta(prefix + "AndorParameter", c.andorParameter);
      addGlobalMeta(prefix + "InputBitDepth", c.inputBitDepth);
      addGlobalMeta(prefix + "InputLevel", c.inputLevel);
      addGlobalMeta(prefix + "HorizontalPixels", c.horizontalPixels);
      addGlobalMeta(prefix + "VerticalPixels", c.verticalPixels);
      addGlobalMeta(prefix + "FilterWheelPosition", c.filterWheelPosition);
      addGlobalMeta(prefix + "FilterPosition", c.filterPosition);
      addGlobalMeta(prefix + "ShadingCorrectionSource", c.correctionFile);
    }
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

    private int currentField = -1;

    public MeasurementDataHandler(String parentDir) {
      super();
      this.parentDir = parentDir;
    }

    public ArrayList<Plane> getPlanes() {
      return planes;
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
        wppPath = attributes.getValue("bts:WellPlateProductFileName");
        if (wppPath != null && wppPath.trim().length() == 0) {
          wppPath = null;
        }
      }
      else if (qName.equals("bts:MeasurementChannel")) {
        Channel c = new Channel();
        c.index = Integer.parseInt(attributes.getValue("bts:Ch")) - 1;
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

  class MeasurementSettingsHandler extends BaseHandler {
    private StringBuffer currentValue = new StringBuffer();
    private int currentChannelIndex = -1;
    private int timelineIndex = -1;
    private int actionIndex = -1;
    private Double currentPhysicalSizeZ;

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
      if (qName.equals("bts:LightSource")) {
        LightSource l = new LightSource();
        l.name = attributes.getValue("bts:Name");
        l.type = attributes.getValue("bts:Type");

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
            template.objectiveID = attributes.getValue("bts:ObjectiveID");
            template.objective = attributes.getValue("bts:Objective");
            template.binning = attributes.getValue("bts:Binning");
            template.methodID = attributes.getValue("bts:MethodID");
            template.method = attributes.getValue("bts:Method");
            template.filterID = attributes.getValue("bts:FilterID");
            template.kind = attributes.getValue("bts:Kind");
            template.andorParameterID = attributes.getValue("bts:AndorParameterID");
            template.andorParameter = attributes.getValue("bts:AndorParameter");
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
        currentPhysicalSizeZ = null;
      }
      else if (qName.startsWith("bts:ActionAcquire")) {
        actionIndex++;
        currentPhysicalSizeZ = DataTools.parseDouble(
          attributes.getValue("bts:SliceLength"));
      }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
      String value = currentValue.toString();

      if (qName.equals("bts:LightSourceName") && currentChannelIndex >= 0) {
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
          }
          else {
            Channel duplicate = new Channel(ch);
            duplicate.timelineIndex = timelineIndex;
            duplicate.actionIndex = actionIndex;
            duplicate.physicalSizeZ = currentPhysicalSizeZ;
            channels.add(duplicate);
          }
        }
      }
      else if (qName.startsWith("bts:ActionAcquire")) {
        currentPhysicalSizeZ = null;
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
    public String objectiveID;
    public String objective;
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
      objectiveID = ch.objectiveID;
      objective = ch.objective;
      magnification = ch.magnification;
      exposureTime = ch.exposureTime;
      physicalSizeZ = ch.physicalSizeZ;
      binning = ch.binning;
      color = ch.color;
      fluor = ch.fluor;
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
        exposureTime != null || binning != null || color != null ||
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
  }

}
