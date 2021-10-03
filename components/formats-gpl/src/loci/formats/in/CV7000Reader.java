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
import loci.formats.IFormatReader;
import loci.formats.Memoizer;
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

  private static final Logger LOGGER = LoggerFactory.getLogger(CV7000Reader.class);

  private static final String MEASUREMENT_FILE = "MeasurementData.mlf";
  private static final String MEASUREMENT_DETAIL = "MeasurementDetail.mrf";
  private static final String POST_PROCESS = "PostProcess.ppf";

  // -- Fields --

  private String[] allFiles;
  private Location parent;
  private IFormatReader reader;
  private String wppPath;
  private String detailPath;
  private String measurementPath;
  private String settingsPath;
  private ArrayList<Plane> planeData;
  private int[][] reversePlaneLookup;
  private ArrayList<LightSource> lightSources;
  private ArrayList<Channel> channels;
  private int fields;
  private String startTime, endTime;
  private ArrayList<String> extraFiles;

  // -- Constructor --

  /** Constructs a new Yokogawa CV7000 reader. */
  public CV7000Reader() {
    super("Yokogawa CV7000", new String[] {"wpi"});
    hasCompanionFiles = true;
    domains = new String[] {FormatTools.HCS_DOMAIN};
    datasetDescription = "Directory with XML files and one .tif/.tiff file per plane";
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
      if (!files.contains(file) && (!noPixels || !checkSuffix(file, "tif"))) {
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
      if (!checkSuffix(file, "tif") && !(new Location(file).isDirectory())) {
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
      fields = 0;
      lightSources = null;
      channels = null;
      startTime = null;
      endTime = null;
      reversePlaneLookup = null;
      extraFiles = null;
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
    Plane p = lookupPlane(getSeries(), no);
    LOGGER.trace("series = {}, no = {}, file = {}", series, no, p == null ? null : p.file);
    if (p != null && p.file != null) {
      reader.setId(p.file);
      return reader.openBytes(0, buf, x, y, w, h);
    }
    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    WPIHandler plate = new WPIHandler();
    String wpiXML = readSanitizedXML(id);
    XMLTools.parseXML(wpiXML, plate);

    parent = new Location(id).getAbsoluteFile().getParentFile();
    allFiles = parent.list(true);
    Arrays.sort(allFiles);
    for (int i=0; i<allFiles.length; i++) {
      allFiles[i] = new Location(parent, allFiles[i]).getAbsolutePath();
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

      channels.sort(new Comparator<Channel>() {
        @Override
        public int compare(Channel c1, Channel c2) {
          return c1.index - c2.index;
        }
      });
    }

    if (settingsPath != null && new Location(settingsPath).exists()) {
      lightSources = new ArrayList<LightSource>();
      MeasurementSettingsHandler settingsHandler = new MeasurementSettingsHandler();
      String xml = readSanitizedXML(settingsPath);
      if (xml.length() > 0) {
        XMLTools.parseXML(xml, settingsHandler);
      }
    }

    for (Channel ch : channels) {
      if (ch.correctionFile != null) {
        ch.correctionFile = new Location(parent, ch.correctionFile).getAbsolutePath();
      }
    }

    String firstFile = null;
    HashMap<Integer, MinMax> minMax = new HashMap<Integer, MinMax>();

    fields = 0;
    HashSet<Integer> uniqueWells = new HashSet<Integer>();

    for (Plane p : planeData) {
      if (p != null) {
        p.channelIndex = getChannelIndex(p);

        int wellIndex = p.field.row * plate.getPlateColumns() + p.field.column;
        if (!minMax.containsKey(wellIndex)) {
          minMax.put(wellIndex, new MinMax());
        }
        MinMax m = minMax.get(wellIndex);

        if (p.file != null && firstFile == null) {
          firstFile = p.file;
        }

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
        if (p.channelIndex > m.maxC) {
          m.maxC = p.channelIndex;
        }
        if (p.channelIndex < m.minC) {
          m.minC = p.channelIndex;
        }

        if (p.field.field >= fields) {
          fields = p.field.field + 1;
        }

        uniqueWells.add(wellIndex);
      }
    }

    reader = new MinimalTiffReader();
    reader = Memoizer.wrap(getMetadataOptions(), reader);
    reader.setId(firstFile);
    core.clear();
    core.add(new CoreMetadata(reader.getCoreMetadataList().get(0)));

    core.get(0).dimensionOrder = "XYCZT";

    int realWells = uniqueWells.size();
    Integer[] wells = uniqueWells.toArray(new Integer[realWells]);
    Arrays.sort(wells);
    reversePlaneLookup = new int[realWells * fields][];

    for (int i=0; i<realWells * fields; i++) {
      if (i > 0) {
        core.add(new CoreMetadata(core.get(0)));
      }

      int wellIndex = wells[i / fields];
      MinMax m = minMax.get(wellIndex);
      core.get(i).sizeZ = (m.maxZ - m.minZ) + 1;
      core.get(i).sizeT = (m.maxT - m.minT) + 1;
      core.get(i).sizeC = reader.getSizeC() * ((m.maxC - m.minC) + 1);
      core.get(i).imageCount = core.get(i).sizeZ * core.get(i).sizeT *
        (core.get(i).sizeC / reader.getSizeC());
      reversePlaneLookup[i] = new int[core.get(i).imageCount];
      Arrays.fill(reversePlaneLookup[i], -1);
    }

    int[] seriesLengths = new int[] {fields, realWells};
    int[] planeLengths = new int[] {getSizeC(), getSizeZ(), getSizeT()};

    extraFiles = new ArrayList<String>();
    for (int i=0; i<planeData.size(); i++) {
      Plane p = planeData.get(i);
      Field f = p.field;
      int wellNumber = f.row * plate.getPlateColumns() + f.column;
      int wellIndex = Arrays.binarySearch(wells, wellNumber);
      p.series = FormatTools.positionToRaster(seriesLengths,
        new int[] {f.field, wellIndex});
      MinMax m = minMax.get(wellNumber);

      planeLengths[0] = core.get(p.series).sizeC / reader.getSizeC();
      planeLengths[1] = core.get(p.series).sizeZ;
      planeLengths[2] = core.get(p.series).sizeT;

      p.no = FormatTools.positionToRaster(planeLengths,
        new int[] {p.channelIndex, p.z - m.minZ, p.timepoint - m.minT});

      if (reversePlaneLookup[p.series][p.no] < 0) {
        reversePlaneLookup[p.series][p.no] = i;
      }
      else {
        LOGGER.warn("Ignoring file {}", p.file);
        extraFiles.add(p.file);
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

    PositiveInteger fieldCount = FormatTools.getMaxFieldCount(fields);
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

        for (int field=0; field<fields; field++) {
          String wellSampleID =
            MetadataTools.createLSID("WellSample", 0, nextWell, field);
          store.setWellSampleID(wellSampleID, 0, nextWell, field);
          store.setWellSampleIndex(
            new NonNegativeInteger(nextImage), 0, nextWell, field);
          String imageID = MetadataTools.createLSID("Image", nextImage);
          store.setImageID(imageID, nextImage);
          store.setWellSampleImageRef(imageID, 0, nextWell, field);

          String name = "Well " + ((char) ('A' + row)) + (col + 1) + ", Field " + (field + 1);
          store.setImageName(name, nextImage);
          store.setPlateAcquisitionWellSampleRef(wellSampleID, 0, 0, nextImage);

          setSeries(nextImage);

          // find the first valid plane to set WellSample positions
          int no = 0;
          Plane p = lookupPlane(nextImage, no);
          while (p == null && no < getImageCount()) {
            p = lookupPlane(nextImage, no++);
          }
          if (p != null) {
            store.setWellSamplePositionX(
              FormatTools.createLength(p.xpos, UNITS.REFERENCEFRAME),
              0, nextWell, field);
            store.setWellSamplePositionY(
              FormatTools.createLength(p.ypos, UNITS.REFERENCEFRAME),
              0, nextWell, field);
          }

          nextImage++;
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
      List<String> usedObjectiveIDs = new ArrayList<String>();
      if (lightSources.size() > 0) {
        instrument = MetadataTools.createLSID("Instrument", 0);

        store.setInstrumentID(instrument, 0);

        int nextLightSource = 0;
        for (LightSource l : lightSources) {
          if ("Laser".equals(l.type)) {
            String laserID = MetadataTools.createLSID("LightSource", 0, nextLightSource);
            store.setLaserID(laserID, 0, nextLightSource);
            store.setLaserWavelength(
              new Length(l.wavelength, UNITS.NANOMETER), 0, nextLightSource);
            store.setLaserPower(new Power(l.power, UNITS.MILLIWATT), 0, nextLightSource);
            nextLightSource++;
          }
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

      for (int i=0; i<getSeriesCount(); i++) {
        setSeries(i);
        if (channels != null) {
          for (int c=0; c<getSizeC(); c++) {
            Plane p = lookupPlane(i, c);
            if (p == null) {
              // There was likely an error during acquisition for this
              // particular plane.  Skip it.
              continue;
            }
            Channel channel = lookupChannel(p.channel);
            if (channel == null) {
              continue;
            }

            if (c == 0) {
              store.setPixelsPhysicalSizeX(FormatTools.getPhysicalSizeX(channel.xSize), i);
              store.setPixelsPhysicalSizeY(FormatTools.getPhysicalSizeY(channel.ySize), i);
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
            store.setChannelName("Channel #" + (channel.index + 1) + ", Camera #" + channel.cameraNumber, i, c);

            if (channel.color != null) {
              store.setChannelColor(channel.color, i, c);
            }

            if (channel.excitation != null && channel.lightSourceRefs != null) {
              int index = -1;
              for (int ref=0; ref<channel.lightSourceRefs.size(); ref++) {
                int lightSource = channel.lightSourceRefs.get(ref);
                if ("Laser".equals(lightSources.get(lightSource).type) &&
                  lightSources.get(lightSource).wavelength < channel.excitation)
                {
                  index = lightSource;
                }
              }
              if (index >= 0) {
                store.setChannelLightSourceSettingsID(
                  MetadataTools.createLSID("LightSource", 0, index), i, c);
                store.setChannelExcitationWavelength(
                  new Length(channel.excitation, UNITS.NANOMETER), i, c);
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

  private int getChannelIndex(Plane p) {
    int index = -1;
    for (int action=0; action<=p.actionIndex; action++) {
      for (Channel ch : channels) {
        if (ch.timelineIndex == p.timelineIndex &&
          ch.actionIndex == action)
        {
          index++;
          if (ch.index == p.channel) {
            return index;
          }
        }
      }
    }
    return index;
  }

  private Channel lookupChannel(int index) {
    for (Channel ch : channels) {
      if (ch.index == index) {
        return ch;
      }
    }
    return null;
  }

  private String readSanitizedXML(String filename) throws IOException {
    String xml = DataTools.readFile(filename).trim();
    if (xml.endsWith(">>")) {
      xml = xml.substring(0, xml.length() - 1);
    }
    return xml;
  }

  private boolean isWellAcquired(int row, int col) {
    if (planeData != null) {
      for (Plane p : planeData) {
        if (p != null && p.file != null && p.field.row == row && p.field.column == col) {
          return true;
        }
      }
    }
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
      }
    }

  }

  class MeasurementSettingsHandler extends BaseHandler {
    private Channel currentChannel = null;
    private StringBuffer currentValue = new StringBuffer();
    private int timelineIndex = -1;
    private int actionIndex = -1;

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
        String ch = attributes.getValue("bts:Ch");
        if (ch != null) {
          int index = Integer.parseInt(ch) - 1;
          if (index >= 0 && index < channels.size()) {
            currentChannel = channels.get(index);

            currentChannel.objectiveID = attributes.getValue("bts:ObjectiveID");
            currentChannel.objective = attributes.getValue("bts:Objective");
            currentChannel.binning = attributes.getValue("bts:Binning");

            String mag = attributes.getValue("bts:Magnification");
            currentChannel.magnification = DataTools.parseDouble(mag);

            String exposure = attributes.getValue("bts:ExposureTime");
            currentChannel.exposureTime = DataTools.parseDouble(exposure);

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
                currentChannel.color = new Color(red, green, blue, alpha);
              }
            }

            String acquisition = attributes.getValue("bts:Acquisition");
            if (acquisition != null) {
              if (acquisition.indexOf("/") > 0) {
                acquisition = acquisition.replaceAll("BP", "");
                String wave = acquisition.substring(0, acquisition.indexOf("/"));
                currentChannel.excitation = DataTools.parseDouble(wave);
              }
            }
          }
        }
      }
      else if (qName.equals("bts:Timeline")) {
        timelineIndex++;
        actionIndex = -1;
      }
      else if (qName.startsWith("bts:ActionAcquire")) {
        actionIndex++;
      }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
      String value = currentValue.toString();

      if (qName.equals("bts:LightSourceName") && currentChannel != null) {
        int index = -1;
        for (int i=0; i<lightSources.size(); i++) {
          if (lightSources.get(i).name.equals(value)) {
            index = i;
          }
        }
        if (index >= 0) {
          currentChannel.lightSourceRefs.add(index);
        }
      }
      else if (qName.equals("bts:Ch")) {
        int channelIndex = Integer.parseInt(value) - 1;
        if (channelIndex >= 0 && channelIndex < channels.size()) {
          Channel ch = channels.get(channelIndex);
          ch.timelineIndex = timelineIndex;
          ch.actionIndex = actionIndex;
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

  class Channel {
    public int timelineIndex;
    public int actionIndex;
    public int index;
    public double xSize;
    public double ySize;
    public int cameraNumber;
    public String correctionFile;
    public List<Integer> lightSourceRefs = new ArrayList<Integer>();
    public Double excitation;

    public String objectiveID;
    public String objective;
    public Double magnification;
    public Double exposureTime;
    public String binning;
    public Color color;

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
