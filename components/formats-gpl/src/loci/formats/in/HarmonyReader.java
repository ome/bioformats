/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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
import ome.xml.model.enums.EnumerationException;
import ome.xml.model.enums.UnitsLength;
import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

import org.xml.sax.Attributes;

/**
 * HarmonyReader is the file format reader for PerkinElmer Harmony data.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class HarmonyReader extends FormatReader {

  // -- Constants --

  private static final String[] XML_FILES = {"Index.idx.xml", "Index.ref.xml"};
  private static final String MAGIC = "Harmony";
  private static final int XML_TAG = 65500;

  // -- Fields --

  private Plane[][] planes;
  private MinimalTiffReader reader;
  private ArrayList<String> metadataFiles = new ArrayList<String>();

  // -- Constructor --

  /** Constructs a new Harmony reader. */
  public HarmonyReader() {
    super("PerkinElmer Harmony", new String[] {"xml"});
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
    TiffParser p = new TiffParser(stream);
    IFD ifd = p.getFirstIFD();
    if (ifd != null) {
      Object s = ifd.getIFDValue(XML_TAG);
      if (s == null) return false;
      String xml = s instanceof String[] ? ((String[]) s)[0] : s.toString();
      return xml.indexOf(MAGIC) < 1024;
    }
    stream.seek(0);
    String xml = stream.readString(1024);
    return xml.indexOf(MAGIC) > 0;
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
          files.add(p.filename);
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
    if (!noPixels) {
      for (Plane p : planes[getSeries()]) {
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

    if (getSeries() < planes.length && no < planes[getSeries()].length) {
      Plane p = planes[getSeries()][no];

      if (new Location(p.filename).exists()) {
        if (reader == null) {
          reader = new MinimalTiffReader();
        }
        reader.setId(p.filename);
        reader.openBytes(0, buf, x, y, w, h);
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

    Location parent = new Location(currentId).getAbsoluteFile().getParentFile().getParentFile();
    String[] list = parent.list();
    for (String f : list) {
      Location path = new Location(parent, f);
      if (path.isDirectory()) {
        String[] companionFolders = path.list();
        for (String folder : companionFolders) {
          if (!f.equals("Images") || !checkSuffix(folder, "tiff")) {
            metadataFiles.add(new Location(path, folder).getAbsolutePath());
          }
        }
      }
      else {
        metadataFiles.add(path.getAbsolutePath());
      }
    }

    // parse plate layout and image dimensions from the XML file

    String xmlData = DataTools.readFile(id);
    HarmonyHandler handler = new HarmonyHandler();
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
      ms.sizeX = planes[i][0].x;
      ms.sizeY = planes[i][0].y;
      ms.sizeZ = uniqueZs.size();
      ms.sizeC = uniqueCs.size();
      ms.sizeT = uniqueTs.size();
      ms.dimensionOrder = "XYCZT";
      ms.rgb = false;
      ms.imageCount = getSizeZ() * getSizeC() * getSizeT();

      RandomAccessInputStream s =
        new RandomAccessInputStream(planes[i][0].filename, 16);
      TiffParser parser = new TiffParser(s);
      parser.setDoCaching(false);

      IFD firstIFD = parser.getFirstIFD();
      ms.littleEndian = firstIFD.isLittleEndian();
      ms.pixelType = firstIFD.getPixelType();
      s.close();
    }

    HashMap<String, String> xmlMetadata = handler.getMetadataMap();
    for (String key : xmlMetadata.keySet()) {
      addGlobalMeta(key, xmlMetadata.get(key));
    }

    // populate the MetadataStore

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

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

          String name = "Well Row " + rows[row] + ", Column " + cols[col] + ", Field " + (field + 1);
          store.setImageName(name, imageIndex);
          store.setPlateAcquisitionWellSampleRef(
            wellSampleID, 0, 0, imageIndex);
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
        if (planes[i][0].acqTime != null) {
          store.setImageAcquisitionDate(new Timestamp(planes[i][0].acqTime), i);
        }

        for (int c=0; c<getSizeC(); c++) {
          store.setChannelName(planes[i][c].channelName, i, c);
          store.setChannelAcquisitionMode(getAcquisitionMode(planes[i][c].acqType), i, c);
          if (planes[i][c].channelType != null) {
            store.setChannelContrastMethod(getContrastMethod(planes[i][c].channelType), i, c);
          }
          if (planes[i][c].emWavelength != null) {
            store.setChannelEmissionWavelength(planes[i][c].emWavelength, i, c);
          }
          if (planes[i][c].exWavelength != null) {
            store.setChannelExcitationWavelength(planes[i][c].exWavelength, i, c);
          }
        }

        store.setPixelsPhysicalSizeX(planes[i][0].resolutionX, i);
        store.setPixelsPhysicalSizeY(planes[i][0].resolutionY, i);

        if (getSizeZ() > 1) {
          Unit<Length> firstZUnit = planes[i][0].positionZ.unit();
          double firstZ = planes[i][0].positionZ.value().doubleValue();
          double lastZ = planes[i][planes[i].length - 1].positionZ.value(firstZUnit).doubleValue();
          double averageStep = (lastZ - firstZ) / (getSizeZ() - 1);
          store.setPixelsPhysicalSizeZ(FormatTools.getPhysicalSizeZ(averageStep, firstZUnit), i);
        }

        for (int p=0; p<getImageCount(); p++) {
          store.setPlanePositionX(planes[i][p].positionX, i, p);
          store.setPlanePositionY(planes[i][p].positionY, i, p);
          store.setPlanePositionZ(planes[i][p].positionZ, i, p);
          store.setPlaneDeltaT(planes[i][p].deltaT, i, p);
        }
      }

    }
  }

  // -- Helper classes --

  class HarmonyHandler extends BaseHandler {
    private static final String ROOT_ELEMENT = "EvaluationInputData";

    // -- Fields --

    private String currentName;
    private Plane activePlane;
    private String currentUnit;

    private String displayName;
    private String plateID;
    private String measurementTime;
    private String plateName;
    private String plateDescription;
    private int plateRows, plateCols;
    private ArrayList<Plane> planes = new ArrayList<Plane>();

    private StringBuffer currentValue = new StringBuffer();

    private ArrayList<String> elementNames = new ArrayList<String>();

    private HashMap<String, String> metadata = new HashMap<String, String>();
    private HashMap<String, Integer> keyCounter = new HashMap<String, Integer>();

    // -- HarmonyHandler API methods --

    public HashMap<String, String> getMetadataMap() {
      return metadata;
    }

    public ArrayList<Plane> getPlanes() {
      return planes;
    }

    public String getExperimenterName() {
      return displayName;
    }

    public String getPlateIdentifier() {
      return plateID;
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
      if (!qName.equals(ROOT_ELEMENT)) {
        elementNames.add(qName);
      }
      currentValue.setLength(0);

      if (qName.equals("Image") && attributes.getValue("id") == null) {
        activePlane = new Plane();
      }

      currentUnit = attributes.getValue("Unit");
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
      String value = currentValue.toString();

      int elementCount = elementNames.size();
      String currentName = null;
      if (elementCount > 0) {
        currentName = elementNames.get(elementCount - 1);
      }
      String parentName = null;
      if (elementCount > 1) {
        parentName = elementNames.get(elementCount - 2);
      }

      if (parentName == null) {
        metadata.put(currentName, value);
      }
      else {
        int keyCount = 1;
        if (keyCounter.containsKey(parentName)) {
          keyCount = keyCounter.get(parentName);
        }

        metadata.put(parentName + " #" + keyCount + " " + currentName, value);
      }

      if (keyCounter.containsKey(currentName) || elementNames.size() == 2) {
        int keyCount = 1;
        if (keyCounter.containsKey(currentName)) {
          keyCount = keyCounter.get(currentName);
        }
        keyCounter.put(currentName, keyCount + 1);
      }

      if ("Plate".equals(parentName)) {
        if ("Name".equals(currentName)) {
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
        else if ("PlateID".equals(currentName)) {
          plateID = value;
        }
        else if ("MeasurementStartTime".equals(currentName)) {
          measurementTime = value;
        }
      }

      if ("User".equals(currentName)) {
        displayName = value;
      }
      else if (activePlane != null && "Image".equals(parentName)) {
        if ("URL".equals(currentName)) {
          Location parent =
            new Location(currentId).getAbsoluteFile().getParentFile();
          activePlane.filename = new Location(parent, value).getAbsolutePath();
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
        else if ("ImageSizeX".equals(currentName)) {
          activePlane.x = Integer.parseInt(value);
        }
        else if ("ImageSizeY".equals(currentName)) {
          activePlane.y = Integer.parseInt(value);
        }
        else if ("TimepointID".equals(currentName)) {
          activePlane.t = Integer.parseInt(value);
        }
        else if ("ChannelID".equals(currentName)) {
          activePlane.c = Integer.parseInt(value);
        }
        else if ("ChannelName".equals(currentName)) {
          activePlane.channelName = value;
        }
        else if ("ImageResolutionX".equals(currentName)) {
          activePlane.resolutionX =
            FormatTools.getPhysicalSizeX(Double.parseDouble(value), currentUnit);
        }
        else if ("ImageResolutionY".equals(currentName)) {
          activePlane.resolutionY =
            FormatTools.getPhysicalSizeY(Double.parseDouble(value), currentUnit);
        }
        else if ("PositionX".equals(currentName)) {
          final double x = Double.parseDouble(value);
          try {
            UnitsLength ul = UnitsLength.fromString(currentUnit);
            activePlane.positionX = UnitsLength.create(x, ul);
          }
          catch (EnumerationException e) {
          }
        }
        else if ("PositionY".equals(currentName)) {
          final double y = Double.parseDouble(value);
          try {
            UnitsLength ul = UnitsLength.fromString(currentUnit);
            activePlane.positionY = UnitsLength.create(y, ul);
          }
          catch (EnumerationException e) {
          }
        }
        else if ("PositionZ".equals(currentName)) {
          final double z = Double.parseDouble(value);
          try {
            UnitsLength ul = UnitsLength.fromString(currentUnit);
            activePlane.positionZ = UnitsLength.create(z, ul);
          }
          catch (EnumerationException e) {
          }
        }
        else if ("MeasurementTimeOffset".equals(currentName)) {
          final double t = Double.parseDouble(value);
          activePlane.deltaT = FormatTools.getTime(t, currentUnit);
        }
        else if ("ObjectiveMagnification".equals(currentName)) {
          activePlane.magnification = Double.parseDouble(value);
        }
        else if ("ObjectiveNA".equals(currentName)) {
          activePlane.lensNA = Double.parseDouble(value);
        }
        else if ("MainEmissionWavelength".equals(currentName)) {
          final double wave = Double.parseDouble(value);
          if (wave > 0) {
            activePlane.emWavelength = FormatTools.getWavelength(wave, currentUnit);
          }
        }
        else if ("MainExcitationWavelength".equals(currentName)) {
          final double wave = Double.parseDouble(value);
          if (wave > 0) {
            activePlane.exWavelength = FormatTools.getWavelength(wave, currentUnit);
          }
        }
        else if ("AcquisitionType".equals(currentName)) {
          activePlane.acqType = value;
        }
        else if ("ChannelType".equals(currentName)) {
          activePlane.channelType = value;
        }
        else if ("AbsTime".equals(currentName)) {
          activePlane.acqTime = value;
        }
      }

      if (qName.equals("Image") && activePlane != null) {
        planes.add(activePlane);
      }

      if (!qName.equals(ROOT_ELEMENT)) {
        elementNames.remove(elementNames.size() - 1);
      }
      currentUnit = null;
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
    public Length resolutionX;
    public Length resolutionY;
    public Length positionX;
    public Length positionY;
    public Length positionZ;
    public Time deltaT;
    public Length emWavelength;
    public Length exWavelength;
    public double magnification;
    public double lensNA;
    public String acqType;
    public String channelType;
    public String acqTime;
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
    return super.getAcquisitionMode(mode);
  }

}
