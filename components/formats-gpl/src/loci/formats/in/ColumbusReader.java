/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2014 Open Microscopy Environment:
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import javax.xml.parsers.ParserConfigurationException;

import loci.common.Constants;
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

import ome.xml.model.primitives.Color;
import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



/**
 * ColumbusReader is the file format reader for screens exported from PerkinElmer Columbus.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class ColumbusReader extends FormatReader {

  // -- Constants --

  private static final String XML_FILE = "MeasurementIndex.ColumbusIDX.xml";
  private static final String MAGIC = "ColumbusMeasurementIndex";

  // -- Fields --

  private ArrayList<String> metadataFiles = new ArrayList<String>();
  private ArrayList<Plane> planes = new ArrayList<Plane>();
  private MinimalTiffReader reader;

  private int nFields = 0;
  private String acquisitionDate;

  // -- Constructor --

  /** Constructs a new Columbus reader. */
  public ColumbusReader() {
    super("PerkinElmer Columbus", new String[] {"xml"});
    domains = new String[] {FormatTools.HCS_DOMAIN};
    suffixSufficient = false;
    datasetDescription =
      "Directory with XML file and one .tif/.tiff file per plane";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getRequiredDirectories(String[]) */
  public int getRequiredDirectories(String[] files)
    throws FormatException, IOException
  {
    return 2;
  }

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    String localName = new Location(name).getName();
    if (localName.equals(XML_FILE)) {
      return true;
    }
    Location parent = new Location(name).getAbsoluteFile().getParentFile();
    parent = parent.getParentFile();
    Location xml = new Location(parent, XML_FILE);
    if (!xml.exists()) {
      return false;
    }

    return super.isThisType(name, open);
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    String check = stream.readString(1024);
    return check.indexOf(MAGIC) > 0;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);

    ArrayList<String> files = new ArrayList<String>();
    files.add(currentId);
    for (String file : metadataFiles) {
      files.add(file);
    }

    if (!noPixels) {
      for (Plane p : planes) {
        if (p.series == getSeries() && !files.contains(p.file)) {
          files.add(p.file);
        }
      }
    }

    return files.toArray(new String[files.size()]);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      if (reader != null) {
        reader.close();
      }
      reader = null;
      metadataFiles.clear();
      planes.clear();
      nFields = 0;
      acquisitionDate = null;
    }
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int index = getSeries() * getImageCount() + no;
    Plane p = planes.get(index);

    reader.setId(p.file);
    reader.openBytes(p.fileIndex, buf, x, y, w, h);
    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    // make sure that we have the XML file and not a TIFF file

    if (!checkSuffix(id, "xml")) {
      Location parent = new Location(id).getAbsoluteFile().getParentFile();
      Location xml = new Location(parent, XML_FILE);
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

    Location parent = new Location(currentId).getAbsoluteFile().getParentFile();

    // parse plate layout and image dimensions from the XML files

    String xmlData = DataTools.readFile(id);
    MeasurementHandler handler = new MeasurementHandler();
    XMLTools.parseXML(xmlData, handler);

    for (String file : metadataFiles) {
      if (checkSuffix(file, "xml")) {
        parseImageXML(new Location(parent, file).getAbsolutePath());
      }
    }

    // process plane list to determine plate size

    Comparator<Plane> planeComp = new Comparator<Plane>() {
      public int compare(Plane p1, Plane p2) {
        if (p1.row != p2.row) {
          return p1.row - p2.row;
        }

        if (p1.col != p2.col) {
          return p1.col - p2.col;
        }

        if (p1.field != p2.field) {
          return p1.field - p2.field;
        }

        if (p1.timepoint != p2.timepoint) {
          return p1.timepoint - p2.timepoint;
        }

        if (p1.channel != p2.channel) {
          return p1.channel - p2.channel;
        }

        return 0;
      }
    };
    Plane[] tmpPlanes = planes.toArray(new Plane[planes.size()]);
    Arrays.sort(tmpPlanes, planeComp);
    planes.clear();

    reader = new MinimalTiffReader();
    reader.setId(tmpPlanes[0].file);
    core = reader.getCoreMetadataList();

    CoreMetadata m = core.get(0);

    m.sizeC = 0;
    m.sizeT = 0;

    ArrayList<Integer> uniqueSamples = new ArrayList<Integer>();
    for (Plane p : tmpPlanes) {
      planes.add(p);

      int sampleIndex = p.row * handler.getPlateColumns() + p.col;
      if (!uniqueSamples.contains(sampleIndex)) {
        uniqueSamples.add(sampleIndex);
      }

      // missing wells are allowed, but the field/channel/timepoint
      // counts are assumed to be non-sparse
      if (p.field >= nFields) {
        nFields = p.field + 1;
      }
      if (p.channel >= getSizeC()) {
        m.sizeC = p.channel + 1;
      }
      if (p.timepoint >= getSizeT()) {
        m.sizeT = p.timepoint + 1;
      }

    }

    m.sizeZ = 1;
    m.imageCount = getSizeZ() * getSizeC() * getSizeT();
    m.dimensionOrder = "XYCTZ";
    m.rgb = false;

    int seriesCount = uniqueSamples.size() * nFields;
    for (int i=1; i<seriesCount; i++) {
      core.add(m);
    }

    // populate the MetadataStore

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    store.setScreenID(MetadataTools.createLSID("Screen", 0), 0);
    store.setScreenName(handler.getScreenName(), 0);
    store.setPlateID(MetadataTools.createLSID("Plate", 0), 0);
    store.setPlateName(handler.getPlateName(), 0);
    store.setPlateRows(new PositiveInteger(handler.getPlateRows()), 0);
    store.setPlateColumns(new PositiveInteger(handler.getPlateColumns()), 0);

    String imagePrefix = handler.getPlateName() + " Well ";
    int planeIndex = 0;
    int wellSample = 0;

    int nextWell = -1;
    Timestamp date = new Timestamp(acquisitionDate);
    long timestampSeconds = date.asInstant().getMillis() / 1000;
    for (int plane=0; plane<planes.size(); plane++) {
      Plane p = planes.get(plane);

      if (plane % (getImageCount() * nFields) == 0) {
        nextWell++;
        store.setWellID(MetadataTools.createLSID("Well", 0, nextWell), 0, nextWell);
        store.setWellRow(new NonNegativeInteger(p.row), 0, nextWell);
        store.setWellColumn(new NonNegativeInteger(p.col), 0, nextWell);
      }

      if (plane % getImageCount() == 0) {
        String wellSampleID = MetadataTools.createLSID("WellSample", 0, nextWell, p.field);
        store.setWellSampleID(wellSampleID, 0, nextWell, p.field);
        store.setWellSampleIndex(new NonNegativeInteger(wellSample), 0, nextWell, p.field);
        store.setWellSamplePositionX(p.positionX, 0, nextWell, p.field);
        store.setWellSamplePositionY(p.positionY, 0, nextWell, p.field);

        String imageID = MetadataTools.createLSID("Image", wellSample);
        store.setImageID(imageID, wellSample);
        store.setWellSampleImageRef(imageID, 0, nextWell, p.field);

        store.setImageName(
          imagePrefix + (char) (p.row + 'A') + (p.col + 1) + " Field #" + (p.field + 1), wellSample);
        store.setImageAcquisitionDate(date, wellSample);

        p.series = wellSample;
        wellSample++;
      }

      store.setChannelName(p.channelName, p.series, p.channel);
      store.setChannelEmissionWavelength(
        new PositiveInteger((int) p.emWavelength), p.series, p.channel);
      store.setChannelExcitationWavelength(
        new PositiveInteger((int) p.exWavelength), p.series, p.channel);
      store.setChannelColor(p.channelColor, p.series, p.channel);
      store.setPixelsPhysicalSizeX(new PositiveFloat(p.sizeX), p.series);
      store.setPixelsPhysicalSizeY(new PositiveFloat(p.sizeY), p.series);
      store.setPlaneDeltaT(p.deltaT - timestampSeconds, p.series, getIndex(0, p.channel, p.timepoint));
    }
  }

  // -- Helper methods --

  private void parseImageXML(String filename) throws FormatException, IOException {
    String xml = DataTools.readFile(filename);
    Location parent = new Location(filename).getParentFile();

    Element root = null;
    try {
      ByteArrayInputStream s =
        new ByteArrayInputStream(xml.getBytes(Constants.ENCODING));
      root = XMLTools.parseDOM(s).getDocumentElement();
      s.close();
    }
    catch (ParserConfigurationException e) {
      throw new FormatException(e);
    }
    catch (SAXException e) {
      throw new FormatException(e);
    }

    NodeList plates = root.getElementsByTagName("Plates");
    plates = ((Element) plates.item(0)).getElementsByTagName("Plate");
    NodeList timestamps = ((Element) plates.item(0)).getElementsByTagName("MeasurementStartTime");
    acquisitionDate = ((Element) timestamps.item(0)).getTextContent();

    NodeList images = root.getElementsByTagName("Images");
    images = ((Element) images.item(0)).getElementsByTagName("Image");

    for (int i=0; i<images.getLength(); i++) {
      Element image = (Element) images.item(i);
      Plane p = new Plane();

      NodeList children = image.getChildNodes();
      for (int q=0; q<children.getLength(); q++) {
        Node child = children.item(q);
        String name = child.getNodeName();
        String value = child.getTextContent();
        NamedNodeMap attrs = child.getAttributes();
        if (name.equals("URL")) {
          p.file = new Location(parent, value).getAbsolutePath();

          String buffer = attrs.getNamedItem("BufferNo").getNodeValue();
          p.fileIndex = Integer.parseInt(buffer);
        }
        else if (name.equals("Row")) {
          p.row = Integer.parseInt(value) - 1;
        }
        else if (name.equals("Col")) {
          p.col = Integer.parseInt(value) - 1;
        }
        else if (name.equals("FieldID")) {
          p.field = Integer.parseInt(value) - 1;
        }
        else if (name.equals("TimepointID")) {
          p.timepoint = Integer.parseInt(value) - 1;
        }
        else if (name.equals("ChannelID")) {
          p.channel = Integer.parseInt(value) - 1;
        }
        else if (name.equals("ChannelName")) {
          p.channelName = value;
        }
        else if (name.equals("ChannelColor")) {
          Long color = new Long(value);
          int blue = (int) ((color >> 24) & 0xff);
          int green = (int) ((color >> 16) & 0xff);
          int red = (int) ((color >> 8) & 0xff);
          int alpha = (int) (color & 0xff);

          // not setting this yet, and deferring to the
          // emission wavelength to set the color
          // neither BGRA nor ARGB seems to make sense for all channels
          //p.channelColor = new Color(red, green, blue, alpha);
        }
        else if (name.equals("MeasurementTimeOffset")) {
          p.deltaT = new Double(value);
        }
        else if (name.equals("MainEmissionWavelength")) {
          p.emWavelength = new Double(value);
        }
        else if (name.equals("MainExcitationWavelength")) {
          p.exWavelength = new Double(value);
        }
        else if (name.equals("ImageResolutionX")) {
          String unit = attrs.getNamedItem("Unit").getNodeValue();
          p.sizeX = correctUnits(new Double(value), unit);
        }
        else if (name.equals("ImageResolutionY")) {
          String unit = attrs.getNamedItem("Unit").getNodeValue();
          p.sizeY = correctUnits(new Double(value), unit);
        }
        else if (name.equals("PositionX")) {
          String unit = attrs.getNamedItem("Unit").getNodeValue();
          p.positionX = correctUnits(new Double(value), unit);
        }
        else if (name.equals("PositionY")) {
          String unit = attrs.getNamedItem("Unit").getNodeValue();
          p.positionY = correctUnits(new Double(value), unit);
        }
        else if (name.equals("PositionZ")) {
          String unit = attrs.getNamedItem("Unit").getNodeValue();
          p.positionZ = correctUnits(new Double(value), unit);
        }
      }

      planes.add(p);
    }

  }

  private Double correctUnits(Double v, String unit) {
    if (unit == null) {
      return v;
    }

    if (unit.equals("m")) {
      return v * 1000000;
    }
    else if (unit.equals("cm")) {
      return v * 10000;
    }
    else if (unit.equals("nm")) {
      return v /= 1000;
    }
    return v;
  }

  // -- Helper classes --


  class MeasurementHandler extends BaseHandler {
    // -- Fields --

    private String currentName;

    private String screenName;
    private String plateName;
    private String plateType;
    private String measurementID;
    private String measurementName;
    private Integer plateRows;
    private Integer plateColumns;

    // -- MeasurementHandler API methods --

    public String getScreenName() {
      return screenName;
    }

    public String getPlateName() {
      return plateName;
    }

    public String getPlateType() {
      return plateType;
    }

    public String getMeasurementID() {
      return measurementID;
    }

    public String getMeasurementName() {
      return measurementName;
    }

    public Integer getPlateRows() {
      return plateRows;
    }

    public Integer getPlateColumns() {
      return plateColumns;
    }

    // -- DefaultHandler API methods --

    public void characters(char[] ch, int start, int length) {
      String value = new String(ch, start, length);
      addGlobalMeta(currentName, value);

      if (currentName.equals("ScreenName")) {
        screenName = value;
      }
      else if (currentName.equals("PlateName")) {
        plateName = value;
      }
      else if (currentName.equals("PlateType")) {
        plateType = value;
      }
      else if (currentName.equals("Measurement")) {
        measurementName = value;
      }
      else if (currentName.equals("Reference")) {
        metadataFiles.add(value);
      }
      else if (currentName.equals("PlateRows")) {
        plateRows = new Integer(value);
      }
      else if (currentName.equals("PlateColumns")) {
        plateColumns = new Integer(value);
      }
    }

    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      currentName = qName;

      for (int i=0; i<attributes.getLength(); i++) {
        String name = attributes.getQName(i);
        String value = attributes.getValue(i);
        if (currentName.equals("Measurement") && name.equals("MeasurementID")) {
          measurementID = value;
        }
      }
    }

    public void endElement(String uri, String localName, String qName) {
    }

  }

  class Plane {
    public String file;
    public int fileIndex;
    public int row;
    public int col;
    public int field;
    public int timepoint;
    public int channel;
    public double deltaT;
    public double emWavelength;
    public double exWavelength;
    public String channelName;
    public Color channelColor;
    public double sizeX;
    public double sizeY;
    public double positionX;
    public double positionY;
    public double positionZ;
    public int series;
  }

}
