/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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
import java.util.HashMap;

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
import ome.units.quantity.Time;
import ome.units.unit.Unit;
import ome.xml.model.enums.AcquisitionMode;
import ome.xml.model.primitives.Color;
import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

import org.xml.sax.Attributes;

/**
 * OperettaReader is the file format reader for PerkinElmer Operetta data.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class OperettaReader extends FormatReader {

  // -- Constants --

  private static final String[] XML_FILES = new String[] {"Index.idx.xml", "Index.ref.xml", "Index.xml"};
  private static final int XML_TAG = 65500;
  private static final String HARMONY_MAGIC = "Harmony";
  // sometimes Operette, sometimes Operetta
  private static final String OPERETTA_MAGIC = "Operett";

  // -- Fields --

  private Plane[][] planes;
  private MinimalTiffReader reader;
  private ArrayList<String> metadataFiles = new ArrayList<String>();

  // -- Constructor --

  /** Constructs a new Operetta reader. */
  public OperettaReader() {
    super("PerkinElmer Operetta", new String[] {"tif", "tiff", "xml"});
    domains = new String[] {FormatTools.HCS_DOMAIN};
    suffixSufficient = false;
    hasCompanionFiles = true;
    datasetDescription =
      "Directory with XML file and one .tif/.tiff file per plane";
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

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String name, boolean open) {
    String localName = new Location(name).getName();
    boolean exists = false;
    for (String XML_FILE : XML_FILES) {
      if (localName.equals(XML_FILE)) {
        exists = true;
        break;
      }
    }
    if (!exists) {
      Location parent = new Location(name).getAbsoluteFile().getParentFile();
      for (String XML_FILE : XML_FILES) {
        Location xml = new Location(parent, XML_FILE);
        if (xml.exists()) {
          exists = true;
          break;
        }
      }
    }
    if (!exists) {
      return false;
    }

    return super.isThisType(name, open);
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    String xml = stream.readString(1024);
    if (xml.indexOf(HARMONY_MAGIC) > 0 || xml.indexOf(OPERETTA_MAGIC) > 0) {
      return true;
    }
    stream.seek(0);
    TiffParser p = new TiffParser(stream);
    IFD ifd = p.getFirstIFD();
    if (ifd == null) return false;

    xml = ifd.getIFDTextValue(XML_TAG);
    if (xml == null) return false;
    int harmonyIndex = xml.indexOf(HARMONY_MAGIC);
    int operettaIndex = xml.indexOf(OPERETTA_MAGIC);
    return (harmonyIndex >= 0 && harmonyIndex < 1024) ||
      (operettaIndex >= 0 && operettaIndex < 1024);
  }

  /* @see loci.formats.IFormatReader#getUsedFiles(boolean) */
  @Override
  public String[] getUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);

    ArrayList<String> files = new ArrayList<String>();
    files.addAll(metadataFiles);
    if (!noPixels) {
      for (Plane[] well : planes) {
        for (Plane p : well) {
          if (p != null && p.filename != null &&
            new Location(p.filename).exists())
          {
            files.add(p.filename);
          }
        }
      }
    }
    return files.toArray(new String[files.size()]);
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);

    ArrayList<String> files = new ArrayList<String>();
    files.addAll(metadataFiles);
    for (Plane p : planes[getSeries()]) {
      if (p != null && p.filename != null &&
        new Location(p.filename).exists())
      {
        files.add(p.filename);
      }
    }

    return files.toArray(new String[files.size()]);
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
      planes = null;
      metadataFiles.clear();
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
    if (getSeries() < planes.length && no < planes[getSeries()].length) {
      Plane p = planes[getSeries()][no];

      if (p != null && p.filename != null && new Location(p.filename).exists()) {
        if (reader == null) {
          reader = new MinimalTiffReader();
        }
        reader.setId(p.filename);
        if (reader.getSizeX() >= getSizeX() && reader.getSizeY() >= getSizeY()) {
          reader.openBytes(0, buf, x, y, w, h);
        }
        else {
          LOGGER.warn("Image dimension mismatch in {}", p.filename);

          // the XY dimensions of this TIFF are smaller than expected,
          // so read the stored image into the upper left corner
          // the bottom and right side will have a black border
          if (x < reader.getSizeX() && y < reader.getSizeY()) {
            int realWidth = (int) Math.min(w, reader.getSizeX() - x);
            int realHeight = (int) Math.min(h, reader.getSizeY() - y);
            byte[] realPixels =
              reader.openBytes(0, x, y, realWidth, realHeight);

            int bpp = FormatTools.getBytesPerPixel(getPixelType());
            int row = realWidth * bpp;
            int outputRow = w * bpp;
            for (int yy=0; yy<realHeight; yy++) {
              System.arraycopy(realPixels, yy * row, buf, yy * outputRow, row);
            }
          }
        }
        reader.close();
      }
    }

    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    // make sure that we have the XML file and not a TIFF file

    if (!checkSuffix(id, "xml")) {
      Location parent = new Location(id).getAbsoluteFile().getParentFile();
      Location xml = new Location(parent, XML_FILES[0]);
      if (!xml.exists()) {
        throw new FormatException("Could not find XML file " +
          xml.getAbsolutePath());
      }
      initFile(xml.getAbsolutePath());
      return;
    }
    else {
      super.initFile(id);
    }

    // assemble list of other metadata/analysis results files
    Location currentFile = new Location(currentId).getAbsoluteFile();
    metadataFiles.add(currentFile.getAbsolutePath());
    Location parent = currentFile.getParentFile().getParentFile();
    String[] list = parent.list(true);
    Arrays.sort(list);
    for (String f : list) {
      Location path = new Location(parent, f);
      if (path.isDirectory()) {
        // the current file's parent directory will usually be "Images",
        // but may have been renamed especially if there are no
        // analysis results
        if (f.equals(currentFile.getParentFile().getName())) {
          LOGGER.trace("Skipping current directory {}", f);
          continue;
        }
        // Skipping other directories containing Operetta metadata files
        for (String XML_FILE : XML_FILES) {
          if (new Location(path, XML_FILE).exists()) {
            LOGGER.trace("Skipping {} containing {}", f, XML_FILE);
            continue;
          }
        }
        String[] companionFolders = path.list(true);
        Arrays.sort(companionFolders);
        for (String folder : companionFolders) {
          LOGGER.trace("Found folder {}", folder);
          if (!checkSuffix(folder, "tiff"))
          {
            String metadataFile = new Location(path, folder).getAbsolutePath();
            if (!metadataFile.equals(currentFile.getAbsolutePath())) {
              metadataFiles.add(metadataFile);
              LOGGER.trace("Adding metadata file {}", metadataFile);
            }
          }
        }
      }
      else {
        metadataFiles.add(path.getAbsolutePath());
        LOGGER.trace("Adding metadata file {}", path.getAbsolutePath());
      }
    }

    // parse plate layout and image dimensions from the XML file

    String xmlData = DataTools.readFile(id);
    OperettaHandler handler = new OperettaHandler();
    XMLTools.parseXML(xmlData, handler);

    // sort the list of images by well and field indices

    ArrayList<Plane> planeList = handler.getPlanes();

    ArrayList<Integer> uniqueRows = new ArrayList<Integer>();
    ArrayList<Integer> uniqueCols = new ArrayList<Integer>();
    ArrayList<Integer> uniqueFields = new ArrayList<Integer>();
    ArrayList<Integer> uniqueZs = new ArrayList<Integer>();
    ArrayList<Integer> uniqueTs = new ArrayList<Integer>();
    ArrayList<Integer> uniqueCs = new ArrayList<Integer>();

    for (Plane p : planeList) {
      if (!uniqueRows.contains(p.row)) {
        uniqueRows.add(p.row);
      }
      if (!uniqueCols.contains(p.col)) {
        uniqueCols.add(p.col);
      }
      if (!uniqueFields.contains(p.field)) {
        uniqueFields.add(p.field);
      }
      if (!uniqueZs.contains(p.z)) {
        uniqueZs.add(p.z);
      }
      if (!uniqueCs.contains(p.c)) {
        uniqueCs.add(p.c);
      }
      if (!uniqueTs.contains(p.t)) {
        uniqueTs.add(p.t);
      }
    }

    Integer[] rows = uniqueRows.toArray(new Integer[uniqueRows.size()]);
    Integer[] cols = uniqueCols.toArray(new Integer[uniqueCols.size()]);
    Integer[] fields = uniqueFields.toArray(new Integer[uniqueFields.size()]);
    Integer[] zs = uniqueZs.toArray(new Integer[uniqueZs.size()]);
    Integer[] cs = uniqueCs.toArray(new Integer[uniqueCs.size()]);
    Integer[] ts = uniqueTs.toArray(new Integer[uniqueTs.size()]);

    Arrays.sort(rows);
    Arrays.sort(cols);
    Arrays.sort(fields);
    Arrays.sort(zs);
    Arrays.sort(ts);
    Arrays.sort(cs);

    int seriesCount = rows.length * cols.length * fields.length;
    core.clear();

    planes = new Plane[seriesCount][zs.length * cs.length * ts.length];

    int nextSeries = 0;
    for (int row=0; row<rows.length; row++) {
      for (int col=0; col<cols.length; col++) {
        for (int field=0; field<fields.length; field++) {
          int nextPlane = 0;
          for (int t=0; t<ts.length; t++) {
            for (int z=0; z<zs.length; z++) {
              for (int c=0; c<cs.length; c++) {
                for (Plane p : planeList) {
                  if (p.row == rows[row] && p.col == cols[col] &&
                    p.field == fields[field] && p.t == ts[t] && p.z == zs[z] &&
                    p.c == cs[c])
                  {
                    planes[nextSeries][nextPlane] = p;
                    break;
                  }
                }
                nextPlane++;
              }
            }
          }
          nextSeries++;
        }
      }
    }

    reader = new MinimalTiffReader();

    for (int i=0; i<seriesCount; i++) {
      CoreMetadata ms = new CoreMetadata();
      core.add(ms);
      ms.sizeZ = uniqueZs.size();
      ms.sizeC = uniqueCs.size();
      ms.sizeT = uniqueTs.size();
      ms.dimensionOrder = "XYCZT";
      ms.rgb = false;
      ms.imageCount = getSizeZ() * getSizeC() * getSizeT();

      int planeIndex = 0;
      while (planeIndex < planes[i].length && planes[i][planeIndex] == null) {
        LOGGER.debug("skipping null plane series = {}, plane = {}", i, planeIndex);
        planeIndex++;
      }
      if (planeIndex >= planes[i].length) {
        if (i > 0) {
          ms.sizeX = core.get(i - 1).sizeX;
          ms.sizeY = core.get(i - 1).sizeY;
          ms.pixelType = core.get(i - 1).pixelType;
          ms.littleEndian = core.get(i - 1).littleEndian;
        }
        else {
          LOGGER.warn("Could not find valid plane for series 0");
        }
      }
      else {
        ms.sizeX = planes[i][planeIndex].x;
        ms.sizeY = planes[i][planeIndex].y;
        String filename = planes[i][planeIndex].filename;
        while ((filename == null || !new Location(filename).exists()) &&
          planeIndex < planes[i].length - 1)
        {
          LOGGER.debug("Missing TIFF file: {}", filename);
          planeIndex++;
          filename = planes[i][planeIndex].filename;
        }

        if (filename != null && new Location(filename).exists()) {
          try (RandomAccessInputStream s =
            new RandomAccessInputStream(filename, 16)) {
              TiffParser parser = new TiffParser(s);
              parser.setDoCaching(false);

              IFD firstIFD = parser.getFirstIFD();
              ms.littleEndian = firstIFD.isLittleEndian();
              ms.pixelType = firstIFD.getPixelType();
          }
        }
        else if (i > 0) {
          LOGGER.warn("Could not find valid TIFF file for series {}", i);
          ms.littleEndian = core.get(0).littleEndian;
          ms.pixelType = core.get(0).pixelType;
        }
        else {
          LOGGER.warn("Could not find valid TIFF file for series 0; pixel type may be wrong");
        }
      }
    }

    // some series may not have valid XY dimensions,
    // particularly if the first series was not recorded in the Index.idx.xml
    // look for the first series that has valid dimensions and copy as needed
    // this will allow blank planes to be returned instead of throwing an exception
    //
    // unrecorded series are kept so that fields can be compared across wells
    int firstValidSeries = -1;
    for (int i=0; i<seriesCount; i++) {
      if (core.get(i).sizeX > 0 && core.get(i).sizeY > 0) {
        firstValidSeries = i;
        break;
      }
    }
    if (firstValidSeries < 0) {
      throw new FormatException("No valid images found");
    }
    for (int i=0; i<seriesCount; i++) {
      if (core.get(i).sizeX == 0 || core.get(i).sizeY == 0) {
        core.get(i).sizeX = core.get(firstValidSeries).sizeX;
        core.get(i).sizeY = core.get(firstValidSeries).sizeY;
        core.get(i).pixelType = core.get(firstValidSeries).pixelType;
        core.get(i).littleEndian = core.get(firstValidSeries).littleEndian;
      }
    }

    addGlobalMeta("Plate name", handler.getPlateName());
    addGlobalMeta("Plate description", handler.getPlateDescription());
    addGlobalMeta("Plate ID", handler.getPlateIdentifier());
    addGlobalMeta("Measurement ID", handler.getMeasurementID());

    // populate the MetadataStore

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    String instrument = MetadataTools.createLSID("Instrument", 0);
    store.setInstrumentID(instrument, 0);
    String objective = MetadataTools.createLSID("Objective", 0, 0);
    store.setObjectiveID(objective, 0, 0);
    if (planes[0][0] != null) {
      store.setObjectiveNominalMagnification(planes[0][0].magnification, 0, 0);
      store.setObjectiveLensNA(planes[0][0].lensNA, 0, 0);
    }

    store.setPlateID(MetadataTools.createLSID("Plate", 0), 0);
    store.setPlateRows(new PositiveInteger(handler.getPlateRows()), 0);
    store.setPlateColumns(new PositiveInteger(handler.getPlateColumns()), 0);

    String plateAcqID = MetadataTools.createLSID("PlateAcquisition", 0, 0);
    store.setPlateAcquisitionID(plateAcqID, 0, 0);

    PositiveInteger fieldCount = FormatTools.getMaxFieldCount(fields.length);
    if (fieldCount != null) {
      store.setPlateAcquisitionMaximumFieldCount(fieldCount, 0, 0);
    }

    String startTime = handler.getMeasurementTime();
    if (startTime != null) {
      store.setPlateAcquisitionStartTime(new Timestamp(startTime), 0, 0);
    }

    for (int row=0; row<rows.length; row++) {
      for (int col=0; col<cols.length; col++) {
        int well = row * cols.length + col;
        store.setWellID(MetadataTools.createLSID("Well", 0, well), 0, well);
        store.setWellRow(new NonNegativeInteger(rows[row]), 0, well);
        store.setWellColumn(new NonNegativeInteger(cols[col]), 0, well);

        for (int field=0; field<fields.length; field++) {
          int imageIndex = well * fields.length + field;
          String wellSampleID =
            MetadataTools.createLSID("WellSample", 0, well, field);
          store.setWellSampleID(wellSampleID, 0, well, field);
          store.setWellSampleIndex(
            new NonNegativeInteger(imageIndex), 0, well, field);
          String imageID = MetadataTools.createLSID("Image", imageIndex);
          store.setImageID(imageID, imageIndex);
          store.setWellSampleImageRef(imageID, 0, well, field);
          store.setImageInstrumentRef(instrument, imageIndex);
          store.setObjectiveSettingsID(objective, imageIndex);

          String name = "Well " + (well + 1) + ", Field " + (field + 1);
          store.setImageName(name, imageIndex);
          store.setPlateAcquisitionWellSampleRef(
            wellSampleID, 0, 0, imageIndex);

          if (planes[imageIndex][0] != null && planes[imageIndex][0].absoluteTime != null) {
            store.setImageAcquisitionDate(planes[imageIndex][0].absoluteTime, imageIndex);
            store.setWellSamplePositionX(planes[imageIndex][0].positionX, 0, well, field);
            store.setWellSamplePositionY(planes[imageIndex][0].positionY, 0, well, field);
          }
        }
      }
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      store.setPlateName(handler.getPlateName(), 0);
      store.setPlateDescription(handler.getPlateDescription(), 0);
      store.setPlateExternalIdentifier(handler.getPlateIdentifier(), 0);

      String experimenterID = MetadataTools.createLSID("Experimenter", 0);
      store.setExperimenterID(experimenterID, 0);
      store.setExperimenterLastName(handler.getExperimenterName(), 0);

      for (int i=0; i<getSeriesCount(); i++) {
        store.setImageExperimenterRef(experimenterID, i);

        for (int c=0; c<getSizeC(); c++) {
          if (planes[i][c] != null && planes[i][c].channelName != null) {
            store.setChannelName(planes[i][c].channelName, i, c);
          }
          if (planes[i][c] != null) {
            if (planes[i][c].acqType != null) {
              store.setChannelAcquisitionMode(
                MetadataTools.getAcquisitionMode(planes[i][c].acqType), i, c);
            }
            if (planes[i][c].channelType != null) {
              store.setChannelContrastMethod(
                MetadataTools.getContrastMethod(planes[i][c].channelType), i, c);
            }
            store.setChannelColor(getColor(planes[i][c].emWavelength), i, c);
            store.setChannelEmissionWavelength(
              FormatTools.getEmissionWavelength(planes[i][c].emWavelength), i, c);
            store.setChannelExcitationWavelength(
              FormatTools.getExcitationWavelength(planes[i][c].exWavelength), i, c);
          }
        }

        if (planes[i][0] != null) {
          store.setPixelsPhysicalSizeX(
            FormatTools.getPhysicalSizeX(planes[i][0].resolutionX), i);
          store.setPixelsPhysicalSizeY(
            FormatTools.getPhysicalSizeY(planes[i][0].resolutionY), i);

          if (getSizeZ() > 1) {
            Unit<Length> firstZUnit = planes[i][0].positionZ.unit();
            double firstZ = planes[i][0].positionZ.value().doubleValue();
            double lastZ = planes[i][planes[i].length - 1].positionZ.value(firstZUnit).doubleValue();
            double averageStep = (lastZ - firstZ) / (getSizeZ() - 1);
            store.setPixelsPhysicalSizeZ(FormatTools.getPhysicalSizeZ(averageStep, firstZUnit), i);
          }
        }

        for (int p=0; p<getImageCount(); p++) {
          if (planes[i][p] != null) {
            store.setPlanePositionX(planes[i][p].positionX, i, p);
            store.setPlanePositionY(planes[i][p].positionY, i, p);
            store.setPlanePositionZ(planes[i][p].positionZ, i, p);
            store.setPlaneExposureTime(planes[i][p].exposureTime, i, p);
            store.setPlaneDeltaT(planes[i][p].deltaT, i, p);
          }
        }
      }

    }
  }

  // -- Helper classes --

  class OperettaHandler extends BaseHandler {
    // -- Fields --

    private String currentName;
    private Plane activePlane;
    private Channel activeChannel;

    private String displayName;
    private String plateID;
    private String measurementID;
    private String measurementTime;
    private String plateName;
    private String plateDescription;
    private int plateRows, plateCols;
    private ArrayList<Plane> planes = new ArrayList<Plane>();
    private HashMap<Integer, Channel> channels = new HashMap<Integer, Channel>();

    private final StringBuilder currentValue = new StringBuilder();

    private boolean isHarmony = false;

    // -- OperettaHandler API methods --

    public ArrayList<Plane> getPlanes() {
      return planes;
    }

    public String getExperimenterName() {
      return displayName;
    }

    public String getPlateIdentifier() {
      return plateID;
    }

    public String getMeasurementID() {
      return measurementID;
    }

    public String getMeasurementTime() {
      return measurementTime;
    }

    public String getPlateName() {
      return plateName;
    }

    public String getPlateDescription() {
      return plateDescription;
    }

    public int getPlateRows() {
      return plateRows;
    }

    public int getPlateColumns() {
      return plateCols;
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
      currentName = qName;

      if (qName.equals("Image") && attributes.getValue("id") == null) {
        activePlane = new Plane();
      }
      else if (qName.equals("Entry") && attributes.getValue("ChannelID") != null) {
        int channel = Integer.parseInt(attributes.getValue("ChannelID"));
        activeChannel = new Channel();
        channels.put(channel, activeChannel);
      }
      else if (qName.equals("EvaluationInputData")) {
        isHarmony = attributes.getValue("xmlns").indexOf(HARMONY_MAGIC) > 0;
      }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
      String value = currentValue.toString();
      if ("User".equals(currentName)) {
        displayName = value;
      }
      else if ("PlateID".equals(currentName)) {
        plateID = value;
      }
      else if ("MeasurementID".equals(currentName)) {
        measurementID = value;
      }
      else if ("MeasurementStartTime".equals(currentName)) {
        measurementTime = value;
      }
      else if ("Name".equals(currentName)) {
        plateName = value;
      }
      else if ("PlateTypeName".equals(currentName)) {
        plateDescription = value;
      }
      else if ("PlateRows".equals(currentName)) {
        plateRows = Integer.parseInt(value);
      }
      else if ("PlateColumns".equals(currentName)) {
        plateCols = Integer.parseInt(value);
      }
      else if (activePlane != null || activeChannel != null) {
        if ("ImageSizeX".equals(currentName)) {
          int x = Integer.parseInt(value);
          if (activePlane != null) {
            activePlane.x = x;
          }
          else if (activeChannel != null) {
            activeChannel.x = x;
          }
        }
        else if ("ImageSizeY".equals(currentName)) {
          int y = Integer.parseInt(value);
          if (activePlane != null) {
            activePlane.y = y;
          }
          else if (activeChannel != null) {
            activeChannel.y = y;
          }
        }
        else if ("ChannelName".equals(currentName)) {
          if (activePlane != null) {
            activePlane.channelName = value;
          }
          else if (activeChannel != null) {
            activeChannel.channelName = value;
          }
        }
        else if ("ImageResolutionX".equals(currentName)) {
          // resolution stored in meters
          Double resolution = Double.parseDouble(value) * 1000000;
          if (activePlane != null) {
            activePlane.resolutionX = resolution;
          }
          else if (activeChannel != null) {
            activeChannel.resolutionX = resolution;
          }
        }
        else if ("ImageResolutionY".equals(currentName)) {
          // resolution stored in meters
          Double resolution = Double.parseDouble(value) * 1000000;
          if (activePlane != null) {
            activePlane.resolutionY = resolution;
          }
          else if (activeChannel != null) {
            activeChannel.resolutionY = resolution;
          }
        }
        else if ("ObjectiveMagnification".equals(currentName)) {
          Double mag = Double.parseDouble(value);
          if (activePlane != null) {
            activePlane.magnification = mag;
          }
          else if (activeChannel != null) {
            activeChannel.magnification = mag;
          }
        }
        else if ("ObjectiveNA".equals(currentName)) {
          Double na = Double.parseDouble(value);
          if (activePlane != null) {
            activePlane.lensNA = na;
          }
          else if (activeChannel != null) {
            activeChannel.lensNA = na;
          }
        }
        else if ("MainEmissionWavelength".equals(currentName)) {
          Double wavelength = Double.parseDouble(value);
          if (activePlane != null) {
            activePlane.emWavelength = wavelength;
          }
          else if (activeChannel != null) {
            activeChannel.emWavelength = wavelength;
          }
        }
        else if ("MainExcitationWavelength".equals(currentName)) {
          Double wavelength = Double.parseDouble(value);
          if (activePlane != null) {
            activePlane.exWavelength = wavelength;
          }
          else if (activeChannel != null) {
            activeChannel.exWavelength = wavelength;
          }
        }
        else if ("ExposureTime".equals(currentName)) {
          Time time = new Time(Double.parseDouble(value), UNITS.SECOND);
          if (activePlane != null) {
            activePlane.exposureTime = time;
          }
          else if (activeChannel != null) {
            activeChannel.exposureTime = time;
          }
        }
        else if ("AcquisitionType".equals(currentName)) {
          if (activePlane != null) {
            activePlane.acqType = value;
          }
          else if (activeChannel != null) {
            activeChannel.acqType = value;
          }
        }
        else if ("ChannelType".equals(currentName)) {
          if (activePlane != null) {
            activePlane.channelType = value;
          }
          else if (activeChannel != null) {
            activeChannel.channelType = value;
          }
        }
        else if ("OrientationMatrix".equals(currentName)) {
          // matrix that indicates how to interpret plane position values
          String[] rows = value.split("]");
          Double[][] matrix = new Double[rows.length][];
          for (int i=0; i<rows.length; i++) {
            rows[i] = rows[i].replaceAll("\\[", "").replaceAll(",", " ");
            String[] values = rows[i].trim().split(" ");
            matrix[i] = new Double[values.length];
            for (int j=0; j<matrix[i].length; j++) {
              matrix[i][j] = DataTools.parseDouble(values[j]);
            }
          }
          if (matrix.length > 2 && matrix[0].length > 0 &&
            matrix[1].length > 1 && matrix[2].length > 2)
          {
            if (activePlane != null) {
              activePlane.orientationMatrix = matrix;
            }
            else if (activeChannel != null) {
              activeChannel.orientationMatrix = matrix;
            }
          }
        }
      }
      if (activePlane != null) {
        if ("URL".equals(currentName)) {
          if (value.length() > 0) {
            if (value.startsWith("http")) {
              activePlane.filename = value;
            }
            else {
              Location parent =
                new Location(currentId).getAbsoluteFile().getParentFile();
              activePlane.filename = new Location(parent, value).getAbsolutePath();
            }
          }
        }
        else if ("Row".equals(currentName)) {
          activePlane.row = Integer.parseInt(value) - 1;
        }
        else if ("Col".equals(currentName)) {
          activePlane.col = Integer.parseInt(value) - 1;
        }
        else if ("FieldID".equals(currentName)) {
          activePlane.field = Integer.parseInt(value);
        }
        else if ("PlaneID".equals(currentName)) {
          activePlane.z = Integer.parseInt(value);
        }
        else if ("TimepointID".equals(currentName)) {
          activePlane.t = Integer.parseInt(value);
        }
        else if ("ChannelID".equals(currentName)) {
          activePlane.c = Integer.parseInt(value);
        }
        else if ("PositionX".equals(currentName)) {
          // position stored in meters
          final double meters = Double.parseDouble(value);
          // see "OrientationMatrix" below
          activePlane.positionX = new Length(meters, UNITS.METRE);
        }
        else if ("PositionY".equals(currentName)) {
          // position stored in meters
          final double meters = Double.parseDouble(value);
          // see "OrientationMatrix" below
          activePlane.positionY = new Length(meters, UNITS.METRE);
        }
        else if (("AbsPositionZ".equals(currentName) && !isHarmony) ||
          ("PositionZ".equals(currentName) && isHarmony))
        {
          // position stored in meters
          final double meters = Double.parseDouble(value);
          // see "OrientationMatrix" below
          activePlane.positionZ = new Length(meters, UNITS.METRE);
        }
        else if ("MeasurementTimeOffset".equals(currentName)) {
          activePlane.deltaT = new Time(Double.parseDouble(value), UNITS.SECOND);
        }
        else if ("AbsTime".equals(currentName)) {
          activePlane.absoluteTime = new Timestamp(value);
        }
      }

      currentName = null;

      if (qName.equals("Image") && activePlane != null) {
        Channel c = channels.get(activePlane.c);
        if (c != null) {
          c.copy(activePlane);
        }

        activePlane.applyMatrix();
        planes.add(activePlane);
      }
      else if (qName.equals("Entry")) {
        activeChannel = null;
      }
    }

  }

  // V6 data stores common metadata once per channel
  class Channel {
    public int channelID;
    public String channelName;
    public String acqType;
    public String channelType;
    public double resolutionX;
    public double resolutionY;
    public int x;
    public int y;
    public double emWavelength;
    public double exWavelength;
    public double magnification;
    public double lensNA;
    public Time exposureTime;
    public Double[][] orientationMatrix;

    /**
     * Copy data from this Channel to the given Plane.
     */
    public void copy(Plane p) {
      // don't copy if it looks like this is an empty channel
      if (channelID < 0 || x == 0 || y == 0) {
        return;
      }
      p.channelName = channelName;
      p.acqType = acqType;
      p.channelType = channelType;
      p.resolutionX = resolutionX;
      p.resolutionY = resolutionY;
      p.x = x;
      p.y = y;
      p.emWavelength = emWavelength;
      p.exWavelength = exWavelength;
      p.magnification = magnification;
      p.lensNA = lensNA;
      p.exposureTime = exposureTime;
      p.orientationMatrix = orientationMatrix;
    }
  }

  class Plane {
    public String filename;
    public int row;
    public int col;
    public int field;
    public int x;
    public int y;
    public int z;
    public int t;
    public int c;
    public String channelName;
    public double resolutionX;
    public double resolutionY;
    public Length positionX;
    public Length positionY;
    public Length positionZ;
    public double emWavelength;
    public double exWavelength;
    public double magnification;
    public double lensNA;
    public Time exposureTime;
    public Time deltaT;
    public Timestamp absoluteTime;
    public String acqType;
    public String channelType;
    public Double[][] orientationMatrix;

    /**
     * Applies the orientationMatrix to positionX, positionY, and positionZ.
     */
    public void applyMatrix() {
      if (positionX == null || positionY == null ||
        positionZ == null || orientationMatrix == null)
      {
        return;
      }
      double[] v = new double[] {positionX.value().doubleValue(),
        positionY.value().doubleValue(), positionZ.value().doubleValue()};
      double[] newValues = new double[] {0, 0, 0};
      for (int row=0; row<orientationMatrix.length; row++) {
        for (int col=0; col<orientationMatrix[row].length; col++) {
          if (col < v.length) {
            newValues[row] += orientationMatrix[row][col] * v[col];
          }
          else {
            newValues[row] += orientationMatrix[row][col];
          }
        }
      }
      positionX = new Length(newValues[0], positionX.unit());
      positionY = new Length(newValues[1], positionY.unit());
      positionZ = new Length(newValues[2], positionZ.unit());
    }
  }

  /**
   * Translate emission wavelength to color, based upon:
   * https://www.perkinelmer.com/CMSResources/Images/44-6551APP_PhotocellApplicationNotes.pdf
   */
  private Color getColor(Double emWavelength) {
    if (emWavelength == null) {
      return null;
    }
    if (emWavelength < 450) {
      // magenta (== violet)
      return new Color(255, 0, 255, 255);
    }
    else if (emWavelength < 500) {
      // blue
      return new Color(0, 0, 255, 255);
    }
    else if (emWavelength < 570) {
      // green
      return new Color(0, 255, 0, 255);
    }
    else if (emWavelength < 590) {
      // yellow
      return new Color(255, 255, 0, 255);
    }
    else if (emWavelength < 610) {
      // orange
      return new Color(255, 127, 0, 255);
    }
    // red
    return new Color(255, 0, 0, 255);
  }

  @Override
  protected AcquisitionMode getAcquisitionMode(String mode) throws FormatException {
    if (mode == null) {
      return null;
    }
    if (mode.equalsIgnoreCase("nipkowconfocal")) {
      return AcquisitionMode.SPINNINGDISKCONFOCAL;
    }
    else if (mode.equalsIgnoreCase("confocal")) {
      return AcquisitionMode.LASERSCANNINGCONFOCALMICROSCOPY;
    }
    else if (mode.equalsIgnoreCase("nonconfocal")) {
      return AcquisitionMode.WIDEFIELD;
    }
    return MetadataTools.getAcquisitionMode(mode);
  }

}
