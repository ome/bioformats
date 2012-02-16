//
// ZeissCZIReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.in;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import loci.common.Constants;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.xml.XMLTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.JPEGCodec;
import loci.formats.codec.LZWCodec;
import loci.formats.meta.MetadataStore;

import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.PercentFraction;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.PositiveInteger;

import org.xml.sax.SAXException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * ZeissCZIReader is the file format reader for Zeiss .czi files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/ZeissCZIReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/ZeissCZIReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class ZeissCZIReader extends FormatReader {

  // -- Constants --

  private static final int ALIGNMENT = 32;
  private static final int HEADER_SIZE = 32;
  private static final String CZI_MAGIC_STRING = "ZISRAWFILE";

  /** Compression constants. */
  private static final int UNCOMPRESSED = 0;
  private static final int JPEG = 1;
  private static final int LZW = 2;

  /** Pixel type constants. */
  private static final int GRAY8 = 0;
  private static final int GRAY16 = 1;
  private static final int GRAY_FLOAT = 2;
  private static final int BGR_24 = 3;
  private static final int BGR_48 = 4;
  private static final int BGR_FLOAT = 8;
  private static final int BGRA_8 = 9;
  private static final int COMPLEX = 10;
  private static final int COMPLEX_FLOAT = 11;
  private static final int GRAY32 = 12;
  private static final int GRAY_DOUBLE = 13;

  // -- Fields --

  private MetadataStore store;

  private ArrayList<SubBlock> planes;
  private int rotations = 1;
  private int positions = 1;
  private int illuminations = 1;
  private int acquisitions = 1;
  private int mosaics = 1;
  private int phases = 1;

  private String acquiredDate;
  private String userDisplayName, userName;
  private String userFirstName, userLastName, userMiddleName;
  private String userEmail;
  private String userInstitution;
  private String temperature, airPressure, humidity, co2Percent;
  private String correctionCollar, medium, refractiveIndex;

  private ArrayList<String> emissionWavelengths = new ArrayList<String>();
  private ArrayList<String> excitationWavelengths = new ArrayList<String>();
  private ArrayList<String> pinholeSizes = new ArrayList<String>();
  private ArrayList<String> channelNames = new ArrayList<String>();
  private ArrayList<String> channelColors = new ArrayList<String>();
  private ArrayList<String> binnings = new ArrayList<String>();
  private ArrayList<String> detectorRefs = new ArrayList<String>();
  private ArrayList<String> objectiveIDs = new ArrayList<String>();

  private Double[] positionsX;
  private Double[] positionsY;
  private Double[] positionsZ;

  private int previousChannel = 0;

  // -- Constructor --

  /** Constructs a new Zeiss .czi reader. */
  public ZeissCZIReader() {
    super("Zeiss CZI", "czi");
    domains = new String[] {FormatTools.LM_DOMAIN};
    suffixSufficient = true;
    suffixNecessary = false;
  }

  // -- IFormatReader API methods --

  /**
   * @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream)
   */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 10;
    if (!FormatTools.validStream(stream, blockLen, true)) return false;
    String check = stream.readString(blockLen);
    return check.equals(CZI_MAGIC_STRING);
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    if ((getPixelType() != FormatTools.INT8 &&
      getPixelType() != FormatTools.UINT8) || previousChannel == -1)
    {
      return null;
    }

    byte[][] lut = new byte[3][256];

    String color = channelColors.get(previousChannel);
    if (color != null) {
      color = color.replaceAll("#", "");
      try {
        int colorValue = Integer.parseInt(color, 16);

        int redMax = (colorValue & 0xff0000) >> 16;
        int greenMax = (colorValue & 0xff00) >> 8;
        int blueMax = colorValue & 0xff;

        for (int i=0; i<lut[0].length; i++) {
          lut[0][i] = (byte) (redMax * (i / 255.0));
          lut[1][i] = (byte) (greenMax * (i / 255.0));
          lut[2][i] = (byte) (blueMax * (i / 255.0));
        }

        return lut;
      }
      catch (NumberFormatException e) {
        return null;
      }
    }
    else return null;
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    if ((getPixelType() != FormatTools.INT16 &&
      getPixelType() != FormatTools.UINT16) || previousChannel == -1)
    {
      return null;
    }

    short[][] lut = new short[3][65536];

    String color = channelColors.get(previousChannel);
    if (color != null) {
      color = color.replaceAll("#", "");
      try {
        int colorValue = Integer.parseInt(color, 16);

        int redMax = (colorValue & 0xff0000) >> 16;
        int greenMax = (colorValue & 0xff00) >> 8;
        int blueMax = colorValue & 0xff;

        redMax = (int) (65535 * (redMax / 255.0));
        greenMax = (int) (65535 * (greenMax / 255.0));
        blueMax = (int) (65535 * (blueMax / 255.0));

        for (int i=0; i<lut[0].length; i++) {
          lut[0][i] = (short) ((int) (redMax * (i / 65535.0)) & 0xffff);
          lut[1][i] = (short) ((int) (greenMax * (i / 65535.0)) & 0xffff);
          lut[2][i] = (short) ((int) (blueMax * (i / 65535.0)) & 0xffff);
        }

        return lut;
      }
      catch (NumberFormatException e) {
        return null;
      }
    }
    else return null;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    previousChannel = getZCTCoords(no)[1];

    int currentSeries = getSeries();

    for (SubBlock plane : planes) {
      if (plane.seriesIndex == currentSeries && plane.planeIndex == no) {
        byte[] rawData = plane.readPixelData();
        RandomAccessInputStream s = new RandomAccessInputStream(rawData);
        readPlane(s, x, y, w, h, buf);
        s.close();
        break;
      }
    }
    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      planes = null;
      rotations = 1;
      positions = 1;
      illuminations = 1;
      acquisitions = 1;
      mosaics = 1;
      phases = 1;
      store = null;

      acquiredDate = null;
      userDisplayName = null;
      userName = null;
      userFirstName = null;
      userLastName = null;
      userMiddleName = null;
      userEmail = null;
      userInstitution = null;
      temperature = null;
      airPressure = null;
      humidity = null;
      co2Percent = null;
      correctionCollar = null;
      medium = null;
      refractiveIndex = null;
      positionsX = null;
      positionsY = null;
      positionsZ = null;

      emissionWavelengths.clear();
      excitationWavelengths.clear();
      pinholeSizes.clear();
      channelNames.clear();
      channelColors.clear();
      binnings.clear();
      detectorRefs.clear();
      objectiveIDs.clear();

      previousChannel = 0;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);

    core[0].littleEndian = true;
    in.order(isLittleEndian());

    ArrayList<Segment> segments = new ArrayList<Segment>();
    planes = new ArrayList<SubBlock>();

    while (in.getFilePointer() < in.length()) {
      Segment segment = readSegment();
      segments.add(segment);

      if (segment instanceof SubBlock) {
        planes.add((SubBlock) segment);
      }
    }

    calculateDimensions();
    convertPixelType(planes.get(0).directoryEntry.pixelType);

    // remove any invalid SubBlocks

    int planeSize =
      getSizeX() * getSizeY() * FormatTools.getBytesPerPixel(getPixelType());
    for (int i=0; i<planes.size(); i++) {
      byte[] pixels = planes.get(i).readPixelData();
      if (pixels.length < planeSize) {
        planes.remove(i);
        i--;
      }
    }

    if (getSizeZ() == 0) {
      core[0].sizeZ = 1;
    }
    if (getSizeC() == 0) {
      core[0].sizeC = 1;
    }
    if (getSizeT() == 0) {
      core[0].sizeT = 1;
    }

    // finish populating the core metadata

    int seriesCount = rotations * positions * illuminations * acquisitions *
      mosaics * phases;

    core[0].imageCount = planes.size() / seriesCount;

    if (seriesCount > 1) {
      CoreMetadata firstSeries = core[0];

      core = new CoreMetadata[seriesCount];

      for (int i=0; i<seriesCount; i++) {
        core[i] = firstSeries;
      }
    }

    core[0].dimensionOrder = "XYCZT";

    assignPlaneIndices();

    // populate the OME metadata

    store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    for (Segment segment : segments) {
      if (segment instanceof Metadata) {
        String xml = ((Metadata) segment).xml;
        xml = XMLTools.sanitizeXML(xml);
        translateMetadata(xml);
      }
    }

    if (channelColors.size() > 0) {
      for (int i=0; i<seriesCount; i++) {
        core[i].indexed = true;
      }
    }

    String experimenterID = MetadataTools.createLSID("Experimenter", 0);
    store.setExperimenterID(experimenterID, 0);
    store.setExperimenterDisplayName(userDisplayName, 0);
    store.setExperimenterEmail(userEmail, 0);
    store.setExperimenterFirstName(userFirstName, 0);
    store.setExperimenterInstitution(userInstitution, 0);
    store.setExperimenterLastName(userLastName, 0);
    store.setExperimenterMiddleName(userMiddleName, 0);
    store.setExperimenterUserName(userName, 0);

    String name = new Location(getCurrentFile()).getName();

    for (int i=0; i<getSeriesCount(); i++) {
      store.setImageAcquiredDate(acquiredDate, i);
      store.setImageExperimenterRef(experimenterID, i);
      store.setImageName(name + " #" + (i + 1), i);

      if (airPressure != null) {
        store.setImagingEnvironmentAirPressure(new Double(airPressure), i);
      }
      if (co2Percent != null) {
        store.setImagingEnvironmentCO2Percent(
          PercentFraction.valueOf(co2Percent), i);
      }
      if (humidity != null) {
        store.setImagingEnvironmentHumidity(
          PercentFraction.valueOf(humidity), i);
      }
      if (temperature != null) {
        store.setImagingEnvironmentTemperature(new Double(temperature), i);
      }

      store.setImageObjectiveSettingsID(objectiveIDs.get(0), i);
      if (correctionCollar != null) {
        store.setImageObjectiveSettingsCorrectionCollar(
          new Double(correctionCollar), i);
      }
      store.setImageObjectiveSettingsMedium(getMedium(medium), i);
      if (refractiveIndex != null) {
        store.setImageObjectiveSettingsRefractiveIndex(
          new Double(refractiveIndex), i);
      }

      Double startTime = null;
      if (acquiredDate != null) {
        startTime =
          DateTools.getTime(acquiredDate, DateTools.ISO8601_FORMAT) / 1000d;
      }
      for (int plane=0; plane<getImageCount(); plane++) {
        for (SubBlock p : planes) {
          if (p.seriesIndex == i && p.planeIndex == plane) {
            if (startTime == null) {
              startTime = p.timestamp;
            }

            if (p.stageX != null) {
              store.setPlanePositionX(p.stageX, i, plane);
            }
            else if (positionsX != null && i < positionsX.length) {
              store.setPlanePositionX(positionsX[i], i, plane);
            }

            if (p.stageY != null) {
              store.setPlanePositionY(p.stageY, i, plane);
            }
            else if (positionsY != null && i < positionsY.length) {
              store.setPlanePositionY(positionsY[i], i, plane);
            }

            if (positionsZ != null && i < positionsZ.length) {
              store.setPlanePositionZ(positionsZ[i], i, plane);
            }

            if (p.timestamp != null) {
              store.setPlaneDeltaT(p.timestamp - startTime, i, plane);
            }
            if (p.exposureTime != null) {
              store.setPlaneExposureTime(p.exposureTime, i, plane);
            }
          }
        }
      }

      for (int c=0; c<getEffectiveSizeC(); c++) {
        if (c < channelNames.size()) {
          store.setChannelName(channelNames.get(c), i, c);
        }

        if (c < channelColors.size()) {
          String color = channelColors.get(c);
          if (color != null) {
            color = color.replaceAll("#", "");
            try {
              store.setChannelColor(
                (Integer.parseInt(color, 16) << 8) | 0xff, i, c);
            }
            catch (NumberFormatException e) { }
          }
        }

        if (c < emissionWavelengths.size()) {
          String emWave = emissionWavelengths.get(c);
          if (emWave != null) {
            Double wave = new Double(emWave);
            if (wave.intValue() > 0) {
              store.setChannelEmissionWavelength(
                new PositiveInteger(wave.intValue()), i, c);
            }
            else {
              LOGGER.warn(
                "Expected positive value for EmissionWavelength; got {}", wave);
            }
          }
        }
        if (c < excitationWavelengths.size()) {
          String exWave = excitationWavelengths.get(c);
          if (exWave != null) {
            Double wave = new Double(exWave);
            if (wave.intValue() > 0) {
              store.setChannelExcitationWavelength(
                new PositiveInteger(wave.intValue()), i, c);
            }
            else {
              LOGGER.warn(
                "Expected positive value for ExcitationWavelength; got {}",
                wave);
            }
          }
        }
        if (c < pinholeSizes.size() && pinholeSizes.get(c) != null) {
          store.setChannelPinholeSize(new Double(pinholeSizes.get(c)), i, c);
        }

        if (c < detectorRefs.size()) {
          String detector = detectorRefs.get(c);
          store.setDetectorSettingsID(detector, i, c);

          if (c < binnings.size()) {
            store.setDetectorSettingsBinning(getBinning(binnings.get(c)), i, c);
          }
        }
      }
    }
  }

  // -- Helper methods --

  private void calculateDimensions() {
    // calculate the dimensions

    for (SubBlock plane : planes) {
      for (DimensionEntry dimension : plane.directoryEntry.dimensionEntries) {
        switch (dimension.dimension.charAt(0)) {
          case 'X':
            core[0].sizeX = dimension.size;
            break;
          case 'Y':
            core[0].sizeY = dimension.size;
            break;
          case 'C':
            if (dimension.start >= getSizeC()) {
              core[0].sizeC = dimension.start + 1;
            }
            break;
          case 'Z':
            if (dimension.start >= getSizeZ()) {
              core[0].sizeZ = dimension.start + 1;
            }
            break;
          case 'T':
            if (dimension.start >= getSizeT()) {
              core[0].sizeT = dimension.start + 1;
            }
            break;
          case 'R':
            if (dimension.start >= rotations) {
              rotations = dimension.start + 1;
            }
            break;
          case 'S':
            if (dimension.start >= positions) {
              positions = dimension.start + 1;
            }
            break;
          case 'I':
            if (dimension.start >= illuminations) {
              illuminations = dimension.start + 1;
            }
            break;
          case 'B':
            if (dimension.start >= acquisitions) {
              acquisitions = dimension.start + 1;
            }
            break;
          case 'M':
            if (dimension.start >= mosaics) {
              mosaics = dimension.start + 1;
            }
            break;
          case 'H':
            if (dimension.start >= phases) {
              phases = dimension.start + 1;
            }
            break;
        }
      }
    }
  }

  private void assignPlaneIndices() {
    // assign plane and series indices to each SubBlock
    int[] extraLengths =
      {rotations, positions, illuminations, acquisitions, mosaics, phases};
    for (SubBlock plane : planes) {
      int z = 0;
      int c = 0;
      int t = 0;
      int[] extra = new int[6];

      for (DimensionEntry dimension : plane.directoryEntry.dimensionEntries) {
        switch (dimension.dimension.charAt(0)) {
          case 'C':
            c = dimension.start;
            break;
          case 'Z':
            z = dimension.start;
            break;
          case 'T':
            t = dimension.start;
            break;
          case 'R':
            extra[0] = dimension.start;
            break;
          case 'S':
            extra[1] = dimension.start;
            break;
          case 'I':
            extra[2] = dimension.start;
            break;
          case 'B':
            extra[3] = dimension.start;
            break;
          case 'M':
            extra[4] = dimension.start;
            break;
          case 'H':
            extra[5] = dimension.start;
            break;
        }
      }

      plane.planeIndex = getIndex(z, c, t);
      plane.seriesIndex = FormatTools.positionToRaster(extraLengths, extra);
    }
  }

  private void translateMetadata(String xml) throws FormatException, IOException
  {
    Element root = null;
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder parser = factory.newDocumentBuilder();
      ByteArrayInputStream s =
        new ByteArrayInputStream(xml.getBytes(Constants.ENCODING));
      root = parser.parse(s).getDocumentElement();
      s.close();
    }
    catch (ParserConfigurationException e) {
      throw new FormatException(e);
    }
    catch (SAXException e) {
      throw new FormatException(e);
    }

    if (root == null) {
      throw new FormatException("Could not parse the XML metadata.");
    }

    NodeList children = root.getChildNodes();
    Element realRoot = null;
    for (int i=0; i<children.getLength(); i++) {
      if (children.item(i) instanceof Element) {
        realRoot = (Element) children.item(i);
        break;
      }
    }

    translateInformation(realRoot);
    translateScaling(realRoot);
    translateDisplaySettings(realRoot);
    translateLayers(realRoot);
    translateExperiment(realRoot);

    Stack<String> nameStack = new Stack<String>();
    HashMap<String, Integer> indexes = new HashMap<String, Integer>();
    populateOriginalMetadata(realRoot, nameStack, indexes);
  }

  private void translateInformation(Element root) throws FormatException {
    NodeList informations = root.getElementsByTagName("Information");
    if (informations.getLength() == 0) {
      return;
    }

    Element information = (Element) informations.item(0);
    Element image = getFirstNode(information, "Image");
    Element user = getFirstNode(information, "User");
    Element environment = getFirstNode(information, "Environment");
    Element instrument = getFirstNode(information, "Instrument");

    if (image != null) {
      String bitCount = getFirstNodeValue(image, "ComponentBitCount");
      if (bitCount != null) {
        core[0].bitsPerPixel = Integer.parseInt(bitCount);
      }

      acquiredDate = getFirstNodeValue(image, "AcquisitionDateAndTime");

      Element objectiveSettings = getFirstNode(image, "ObjectiveSettings");
      correctionCollar =
        getFirstNodeValue(objectiveSettings, "CorrectionCollar");
      medium = getFirstNodeValue(objectiveSettings, "Medium");
      refractiveIndex = getFirstNodeValue(objectiveSettings, "RefractiveIndex");

      Element dimensions = getFirstNode(image, "Dimensions");

      NodeList channels = getGrandchildren(dimensions, "Channel");

      if (channels != null) {
        for (int i=0; i<channels.getLength(); i++) {
          Element channel = (Element) channels.item(i);

          emissionWavelengths.add(
            getFirstNodeValue(channel, "EmissionWavelength"));
          excitationWavelengths.add(
            getFirstNodeValue(channel, "ExcitationWavelength"));
          pinholeSizes.add(getFirstNodeValue(channel, "PinholeSize"));
          channelNames.add(channel.getAttribute("Name"));

          Element detectorSettings = getFirstNode(channel, "DetectorSettings");
          binnings.add(getFirstNodeValue(detectorSettings, "Binning"));

          Element detector = getFirstNode(detectorSettings, "Detector");
          if (detector != null) {
            detectorRefs.add(detector.getAttribute("Id"));
          }
        }
      }
    }

    if (user != null) {
      userDisplayName = getFirstNodeValue(user, "DisplayName");
      userFirstName = getFirstNodeValue(user, "FirstName");
      userLastName = getFirstNodeValue(user, "LastName");
      userMiddleName = getFirstNodeValue(user, "MiddleName");
      userEmail = getFirstNodeValue(user, "Email");
      userInstitution = getFirstNodeValue(user, "Institution");
      userName = getFirstNodeValue(user, "UserName");
    }

    if (environment != null) {
      temperature = getFirstNodeValue(environment, "Temperature");
      airPressure = getFirstNodeValue(environment, "AirPressure");
      humidity = getFirstNodeValue(environment, "Humidity");
      co2Percent = getFirstNodeValue(environment, "CO2Percent");
    }

    if (instrument != null) {
      NodeList microscopes = getGrandchildren(instrument, "Microscope");
      Element manufacturerNode = null;

      store.setInstrumentID(MetadataTools.createLSID("Instrument", 0), 0);

      if (microscopes != null) {
        Element microscope = (Element) microscopes.item(0);
        manufacturerNode = getFirstNode(microscope, "Manufacturer");

        store.setMicroscopeManufacturer(
          getFirstNodeValue(manufacturerNode, "Manufacturer"), 0);
        store.setMicroscopeModel(
          getFirstNodeValue(manufacturerNode, "Model"), 0);
        store.setMicroscopeSerialNumber(
          getFirstNodeValue(manufacturerNode, "SerialNumber"), 0);
        store.setMicroscopeLotNumber(
          getFirstNodeValue(manufacturerNode, "LotNumber"), 0);
        store.setMicroscopeType(
          getMicroscopeType(getFirstNodeValue(microscope, "Type")), 0);
      }

      NodeList lightSources = getGrandchildren(instrument, "LightSource");
      if (lightSources != null) {
        for (int i=0; i<lightSources.getLength(); i++) {
          Element lightSource = (Element) lightSources.item(i);
          manufacturerNode = getFirstNode(lightSource, "Manufacturer");

          String manufacturer =
            getFirstNodeValue(manufacturerNode, "Manufacturer");
          String model = getFirstNodeValue(manufacturerNode, "Model");
          String serialNumber =
            getFirstNodeValue(manufacturerNode, "SerialNumber");
          String lotNumber = getFirstNodeValue(manufacturerNode, "LotNumber");

          String type = getFirstNodeValue(lightSource, "LightSourceType");
          String power = getFirstNodeValue(lightSource, "Power");
          if ("Laser".equals(type)) {
            store.setLaserPower(new Double(power), 0, i);
            store.setLaserLotNumber(lotNumber, 0, i);
            store.setLaserManufacturer(manufacturer, 0, i);
            store.setLaserModel(model, 0, i);
            store.setLaserSerialNumber(serialNumber, 0, i);
          }
          else if ("Arc".equals(type)) {
            store.setArcPower(new Double(power), 0, i);
            store.setArcLotNumber(lotNumber, 0, i);
            store.setArcManufacturer(manufacturer, 0, i);
            store.setArcModel(model, 0, i);
            store.setArcSerialNumber(serialNumber, 0, i);
          }
          else if ("LightEmittingDiode".equals(type)) {
            store.setLightEmittingDiodePower(new Double(power), 0, i);
            store.setLightEmittingDiodeLotNumber(lotNumber, 0, i);
            store.setLightEmittingDiodeManufacturer(manufacturer, 0, i);
            store.setLightEmittingDiodeModel(model, 0, i);
            store.setLightEmittingDiodeSerialNumber(serialNumber, 0, i);
          }
          else if ("Filament".equals(type)) {
            store.setFilamentPower(new Double(power), 0, i);
            store.setFilamentLotNumber(lotNumber, 0, i);
            store.setFilamentManufacturer(manufacturer, 0, i);
            store.setFilamentModel(model, 0, i);
            store.setFilamentSerialNumber(serialNumber, 0, i);
          }
        }
      }

      NodeList detectors = getGrandchildren(instrument, "Detector");
      if (detectors != null) {
        for (int i=0; i<detectors.getLength(); i++) {
          Element detector = (Element) detectors.item(i);

          manufacturerNode = getFirstNode(detector, "Manufacturer");
          String manufacturer =
            getFirstNodeValue(manufacturerNode, "Manufacturer");
          String model = getFirstNodeValue(manufacturerNode, "Model");
          String serialNumber =
            getFirstNodeValue(manufacturerNode, "SerialNumber");
          String lotNumber = getFirstNodeValue(manufacturerNode, "LotNumber");

          store.setDetectorID(detector.getAttribute("Id"), 0, i);
          store.setDetectorManufacturer(manufacturer, 0, i);
          store.setDetectorModel(model, 0, i);
          store.setDetectorSerialNumber(serialNumber, 0, i);
          store.setDetectorLotNumber(lotNumber, 0, i);

          String gain = getFirstNodeValue(detector, "Gain");
          if (gain != null) {
            store.setDetectorGain(new Double(gain), 0, i);
          }

          String voltage = getFirstNodeValue(detector, "Voltage");
          if (voltage != null) {
            store.setDetectorVoltage(new Double(voltage), 0, i);
          }

          String offset = getFirstNodeValue(detector, "Offset");
          if (offset != null) {
            store.setDetectorOffset(new Double(offset), 0, i);
          }

          String zoom = getFirstNodeValue(detector, "Zoom");
          if (zoom != null) {
            store.setDetectorZoom(new Double(zoom), 0, i);
          }

          String ampGain = getFirstNodeValue(detector, "AmplificationGain");
          if (ampGain != null) {
            store.setDetectorAmplificationGain(new Double(ampGain), 0, i);
          }

          store.setDetectorType(
            getDetectorType(getFirstNodeValue(detector, "Type")), 0, i);
        }
      }

      NodeList objectives = getGrandchildren(instrument, "Objective");
      if (objectives != null) {
        for (int i=0; i<objectives.getLength(); i++) {
          Element objective = (Element) objectives.item(i);
          manufacturerNode = getFirstNode(objective, "Manufacturer");

          String manufacturer =
            getFirstNodeValue(manufacturerNode, "Manufacturer");
          String model = getFirstNodeValue(manufacturerNode, "Model");
          String serialNumber =
            getFirstNodeValue(manufacturerNode, "SerialNumber");
          String lotNumber = getFirstNodeValue(manufacturerNode, "LotNumber");

          objectiveIDs.add(objective.getAttribute("Id"));
          store.setObjectiveID(objective.getAttribute("Id"), 0, i);
          store.setObjectiveManufacturer(manufacturer, 0, i);
          store.setObjectiveModel(model, 0, i);
          store.setObjectiveSerialNumber(serialNumber, 0, i);
          store.setObjectiveLotNumber(lotNumber, 0, i);
          store.setObjectiveCorrection(
            getCorrection(getFirstNodeValue(objective, "Correction")), 0, i);
          store.setObjectiveImmersion(
            getImmersion(getFirstNodeValue(objective, "Immersion")), 0, i);
          store.setObjectiveLensNA(
            new Double(getFirstNodeValue(objective, "LensNA")), 0, i);

          String magnification =
            getFirstNodeValue(objective, "NominalMagnification");
          Integer mag = new Integer(magnification);

          if (mag > 0) {
            store.setObjectiveNominalMagnification(
              new PositiveInteger(mag), 0, i);
          }
          else {
            LOGGER.warn(
              "Expected positive value for NominalMagnification; got {}", mag);
          }
          String calibratedMag =
            getFirstNodeValue(objective, "CalibratedMagnification");
          if (calibratedMag != null) {
            store.setObjectiveCalibratedMagnification(
              new Double(calibratedMag), 0, i);
          }
          String wd = getFirstNodeValue(objective, "WorkingDistance");
          if (wd != null) {
            store.setObjectiveWorkingDistance(new Double(wd), 0, i);
          }
          String iris = getFirstNodeValue(objective, "Iris");
          if (iris != null) {
            store.setObjectiveIris(new Boolean(iris), 0, i);
          }
        }
      }

      NodeList filterSets = getGrandchildren(instrument, "FilterSet");
      if (filterSets != null) {
        for (int i=0; i<filterSets.getLength(); i++) {
          Element filterSet = (Element) filterSets.item(i);
          manufacturerNode = getFirstNode(filterSet, "Manufacturer");

          String manufacturer =
            getFirstNodeValue(manufacturerNode, "Manufacturer");
          String model = getFirstNodeValue(manufacturerNode, "Model");
          String serialNumber =
            getFirstNodeValue(manufacturerNode, "SerialNumber");
          String lotNumber = getFirstNodeValue(manufacturerNode, "LotNumber");

          store.setFilterSetID(filterSet.getAttribute("Id"), 0, i);
          store.setFilterSetManufacturer(manufacturer, 0, i);
          store.setFilterSetModel(model, 0, i);
          store.setFilterSetSerialNumber(serialNumber, 0, i);
          store.setFilterSetLotNumber(lotNumber, 0, i);

          store.setFilterSetDichroicRef(
            getFirstNodeValue(filterSet, "DichroicRef"), 0, i);

          NodeList excitations = getGrandchildren(
            filterSet, "ExcitationFilters", "ExcitationFilterRef");
          NodeList emissions = getGrandchildren(filterSet, "EmissionFilters",
            "EmissionFilterRef");

          for (int ex=0; ex<excitations.getLength(); ex++) {
            store.setFilterSetExcitationFilterRef(
              excitations.item(ex).getTextContent(), 0, i, ex);
          }
          for (int em=0; em<emissions.getLength(); em++) {
            store.setFilterSetEmissionFilterRef(
              emissions.item(em).getTextContent(), 0, i, em);
          }
        }
      }

      NodeList filters = getGrandchildren(instrument, "Filter");
      if (filters != null) {
        for (int i=0; i<filters.getLength(); i++) {
          Element filter = (Element) filters.item(i);
          manufacturerNode = getFirstNode(filter, "Manufacturer");

          String manufacturer =
            getFirstNodeValue(manufacturerNode, "Manufacturer");
          String model = getFirstNodeValue(manufacturerNode, "Model");
          String serialNumber =
            getFirstNodeValue(manufacturerNode, "SerialNumber");
          String lotNumber = getFirstNodeValue(manufacturerNode, "LotNumber");

          store.setFilterID(filter.getAttribute("Id"), 0, i);
          store.setFilterManufacturer(manufacturer, 0, i);
          store.setFilterModel(model, 0, i);
          store.setFilterSerialNumber(serialNumber, 0, i);
          store.setFilterLotNumber(lotNumber, 0, i);

          store.setFilterType(
            getFilterType(getFirstNodeValue(filter, "Type")), 0, i);
          store.setFilterFilterWheel(
            getFirstNodeValue(filter, "FilterWheel"), 0, i);

          Element transmittance = getFirstNode(filter, "TransmittanceRange");

          String cutIn = getFirstNodeValue(transmittance, "CutIn");
          String cutOut = getFirstNodeValue(transmittance, "CutOut");
          Integer inWave = new Integer(cutIn);
          Integer outWave = new Integer(cutOut);

          if (inWave > 0) {
            store.setTransmittanceRangeCutIn(new PositiveInteger(inWave), 0, i);
          }
          else {
            LOGGER.warn("Expected positive value for CutIn; got {}", inWave);
          }
          if (outWave > 0) {
            store.setTransmittanceRangeCutOut(
              new PositiveInteger(outWave), 0, i);
          }
          else {
            LOGGER.warn("Expected positive value for CutOut; got {}", outWave);
          }

          String inTolerance =
            getFirstNodeValue(transmittance, "CutInTolerance");
          String outTolerance =
            getFirstNodeValue(transmittance, "CutOutTolerance");
          Integer cutInTolerance = new Integer(inTolerance);
          Integer cutOutTolerance = new Integer(outTolerance);

          store.setTransmittanceRangeCutInTolerance(
            new NonNegativeInteger(cutInTolerance), 0, i);
          store.setTransmittanceRangeCutOutTolerance(
            new NonNegativeInteger(cutOutTolerance), 0, i);
          store.setTransmittanceRangeTransmittance(PercentFraction.valueOf(
            getFirstNodeValue(transmittance, "Transmittance")), 0, i);
        }
      }

      NodeList dichroics = getGrandchildren(instrument, "Dichroic");
      if (dichroics != null) {
        for (int i=0; i<dichroics.getLength(); i++) {
          Element dichroic = (Element) dichroics.item(i);
          manufacturerNode = getFirstNode(dichroic, "Manufacturer");

          String manufacturer =
            getFirstNodeValue(manufacturerNode, "Manufacturer");
          String model = getFirstNodeValue(manufacturerNode, "Model");
          String serialNumber =
            getFirstNodeValue(manufacturerNode, "SerialNumber");
          String lotNumber = getFirstNodeValue(manufacturerNode, "LotNumber");

          store.setDichroicID(dichroic.getAttribute("Id"), 0, i);
          store.setDichroicManufacturer(manufacturer, 0, i);
          store.setDichroicModel(model, 0, i);
          store.setDichroicSerialNumber(serialNumber, 0, i);
          store.setDichroicLotNumber(lotNumber, 0, i);
        }
      }
    }
  }

  private void translateScaling(Element root) {
    NodeList scalings = root.getElementsByTagName("Scaling");
    if (scalings.getLength() == 0) {
      return;
    }

    Element scaling = (Element) scalings.item(0);
    NodeList distances = getGrandchildren(scaling, "Items", "Distance");

    for (int i=0; i<distances.getLength(); i++) {
      Element distance = (Element) distances.item(i);
      String id = distance.getAttribute("Id");
      Double value = new Double(getFirstNodeValue(distance, "Value")) * 1000000;
      if (value > 0) {
        PositiveFloat size = new PositiveFloat(value);

        if (id.equals("X")) {
          for (int series=0; series<getSeriesCount(); series++) {
            store.setPixelsPhysicalSizeX(size, series);
          }
        }
        else if (id.equals("Y")) {
          for (int series=0; series<getSeriesCount(); series++) {
            store.setPixelsPhysicalSizeY(size, series);
          }
        }
        else if (id.equals("Z")) {
          for (int series=0; series<getSeriesCount(); series++) {
            store.setPixelsPhysicalSizeZ(size, series);
          }
        }
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSize; got {}", value);
      }
    }
  }

  private void translateDisplaySettings(Element root) {
    NodeList displaySettings = root.getElementsByTagName("DisplaySetting");
    if (displaySettings.getLength() == 0) {
      return;
    }

    Element displaySetting = (Element) displaySettings.item(0);
    NodeList channels = getGrandchildren(displaySetting, "Channel");

    for (int i=0; i<channels.getLength(); i++) {
      Element channel = (Element) channels.item(i);
      channelColors.add(getFirstNodeValue(channel, "Color"));
    }
  }

  private void translateLayers(Element root) {
    NodeList layerses = root.getElementsByTagName("Layers");
    if (layerses.getLength() == 0) {
      return;
    }

    Element layersNode = (Element) layerses.item(0);
    NodeList layers = layersNode.getElementsByTagName("Layer");

    for (int i=0; i<layers.getLength(); i++) {
      Element layer = (Element) layers.item(i);

      String roiID = MetadataTools.createLSID("ROI", i);
      store.setROIID(roiID, i);
      store.setROIName(layer.getAttribute("Name"), i);
      store.setROIDescription(getFirstNodeValue(layer, "Usage"), i);

      for (int series=0; series<getSeriesCount(); series++) {
        store.setImageROIRef(roiID, series, i);
      }

      int shape = 0;

      NodeList lines = getGrandchildren(layer, "Elements", "Line");
      for (int s=0; s<lines.getLength(); s++, shape++) {
        Element line = (Element) lines.item(s);

        Element geometry = getFirstNode(line, "Geometry");
        Element textElements = getFirstNode(line, "TextElements");
        Element attributes = getFirstNode(line, "Attributes");

        store.setLineX1(
          new Double(getFirstNodeValue(geometry, "X1")), i, shape);
        store.setLineX2(
          new Double(getFirstNodeValue(geometry, "X2")), i, shape);
        store.setLineY1(
          new Double(getFirstNodeValue(geometry, "Y1")), i, shape);
        store.setLineY2(
          new Double(getFirstNodeValue(geometry, "Y2")), i, shape);
        store.setLineName(getFirstNodeValue(attributes, "Name"), i, shape);
        store.setLineLabel(getFirstNodeValue(textElements, "Text"), i, shape);
      }

      NodeList rectangles = getGrandchildren(layer, "Elements", "Rectangle");
      shape = populateRectangles(rectangles, i, shape);

      NodeList ellipses = getGrandchildren(layer, "Elements", "Ellipse");
      for (int s=0; s<ellipses.getLength(); s++, shape++) {
        Element ellipse = (Element) ellipses.item(s);

        Element geometry = getFirstNode(ellipse, "Geometry");
        Element textElements = getFirstNode(ellipse, "TextElements");
        Element attributes = getFirstNode(ellipse, "Attributes");

        store.setEllipseID(
          MetadataTools.createLSID("Shape", i, shape), i, shape);
        store.setEllipseRadiusX(
          new Double(getFirstNodeValue(geometry, "RadiusX")), i, shape);
        store.setEllipseRadiusY(
          new Double(getFirstNodeValue(geometry, "RadiusY")), i, shape);
        store.setEllipseX(
          new Double(getFirstNodeValue(geometry, "CenterX")), i, shape);
        store.setEllipseY(
          new Double(getFirstNodeValue(geometry, "CenterY")), i, shape);
        store.setEllipseName(getFirstNodeValue(attributes, "Name"), i, shape);
        store.setEllipseLabel(
          getFirstNodeValue(textElements, "Text"), i, shape);
      }

      // translate all of the circle ROIs
      NodeList circles = getGrandchildren(layer, "Elements", "Circle");
      shape = populateCircles(circles, i, shape);
      NodeList inOutCircles =
        getGrandchildren(layer, "Elements", "InOutCircle");
      shape = populateCircles(inOutCircles, i, shape);
      NodeList outInCircles =
        getGrandchildren(layer, "Elements", "OutInCircle");
      shape = populateCircles(outInCircles, i, shape);
      NodeList pointsCircles =
        getGrandchildren(layer, "Elements", "PointsCircle");
      shape = populateCircles(pointsCircles, i, shape);

      NodeList polygons = getGrandchildren(layer, "Elements", "Polygon");
      shape = populatePolylines(polygons, i, shape, true);

      NodeList polylines = getGrandchildren(layer, "Elements", "Polyline");
      shape = populatePolylines(polylines, i, shape, false);

      NodeList openPolylines =
        getGrandchildren(layer, "Elements", "OpenPolyline");
      shape = populatePolylines(polylines, i, shape, false);

      NodeList closedPolylines =
        getGrandchildren(layer, "Elements", "ClosedPolyline");
      shape = populatePolylines(polygons, i, shape, true);

      NodeList rectRoi = getGrandchildren(layer, "Elements", "RectRoi");
      shape = populateRectangles(rectRoi, i, shape);
      NodeList textBoxes = getGrandchildren(layer, "Elements", "TextBox");
      shape = populateRectangles(textBoxes, i, shape);
      NodeList text = getGrandchildren(layer, "Elements", "Text");
      shape = populateRectangles(text, i, shape);
    }
  }

  private int populateRectangles(NodeList rectangles, int roi, int shape) {
    for (int s=0; s<rectangles.getLength(); s++, shape++) {
      Element rectangle = (Element) rectangles.item(s);

      Element geometry = getFirstNode(rectangle, "Geometry");
      Element textElements = getFirstNode(rectangle, "TextElements");
      Element attributes = getFirstNode(rectangle, "Attributes");

      String left = getFirstNodeValue(geometry, "Left");
      String top = getFirstNodeValue(geometry, "Top");
      String width = getFirstNodeValue(geometry, "Width");
      String height = getFirstNodeValue(geometry, "Height");

      if (left != null && top != null && width != null && height != null) {
        store.setRectangleID(
          MetadataTools.createLSID("Shape", roi, shape), roi, shape);
        store.setRectangleX(new Double(left), roi, shape);
        store.setRectangleY(
          new Double(getSizeY() - Double.parseDouble(top)), roi, shape);
        store.setRectangleWidth(new Double(width), roi, shape);
        store.setRectangleHeight(new Double(height), roi, shape);

        String name = getFirstNodeValue(attributes, "Name");
        String label = getFirstNodeValue(textElements, "Text");

        if (name != null) {
          store.setRectangleName(name, roi, shape);
        }
        if (label != null) {
          store.setRectangleLabel(label, roi, shape);
        }
      }
    }
    return shape;
  }

  private int populatePolylines(NodeList polylines, int roi, int shape,
    boolean closed)
  {
    for (int s=0; s<polylines.getLength(); s++, shape++) {
      Element polyline = (Element) polylines.item(s);
      Element geometry = getFirstNode(polyline, "Geometry");
      Element textElements = getFirstNode(polyline, "TextElements");
      Element attributes = getFirstNode(polyline, "Attributes");

      store.setPolylineID(
        MetadataTools.createLSID("Shape", roi, shape), roi, shape);
      store.setPolylinePoints(
        getFirstNodeValue(geometry, "Points"), roi, shape);
      store.setPolylineClosed(closed, roi, shape);
      store.setPolylineName(getFirstNodeValue(attributes, "Name"), roi, shape);
      store.setPolylineLabel(
        getFirstNodeValue(textElements, "Text"), roi, shape);
    }
    return shape;
  }

  private int populateCircles(NodeList circles, int roi, int shape) {
    for (int s=0; s<circles.getLength(); s++, shape++) {
      Element circle = (Element) circles.item(s);
      Element geometry = getFirstNode(circle, "Geometry");
      Element textElements = getFirstNode(circle, "TextElements");
      Element attributes = getFirstNode(circle, "Attributes");

      store.setEllipseID(
        MetadataTools.createLSID("Shape", roi, shape), roi, shape);
      store.setEllipseRadiusX(
        new Double(getFirstNodeValue(geometry, "Radius")), roi, shape);
      store.setEllipseRadiusY(
        new Double(getFirstNodeValue(geometry, "Radius")), roi, shape);
      store.setEllipseX(
        new Double(getFirstNodeValue(geometry, "CenterX")), roi, shape);
      store.setEllipseY(
        new Double(getFirstNodeValue(geometry, "CenterY")), roi, shape);
      store.setEllipseName(getFirstNodeValue(attributes, "Name"), roi, shape);
      store.setEllipseLabel(
        getFirstNodeValue(textElements, "Text"), roi, shape);
    }
    return shape;
  }

  private void translateExperiment(Element root) {
    NodeList experiments = root.getElementsByTagName("Experiment");
    if (experiments.getLength() == 0) {
      return;
    }

    Element experimentBlock =
      getFirstNode((Element) experiments.item(0), "ExperimentBlocks");
    Element acquisition = getFirstNode(experimentBlock, "AcquisitionBlock");
    Element tilesSetup = getFirstNode(acquisition, "TilesSetup");
    NodeList groups = getGrandchildren(tilesSetup, "PositionGroup");

    positionsX = new Double[core.length];
    positionsY = new Double[core.length];
    positionsZ = new Double[core.length];

    if (groups == null) {
      return;
    }

    for (int i=0; i<groups.getLength(); i++) {
      Element group = (Element) groups.item(i);

      int tilesX = Integer.parseInt(getFirstNodeValue(group, "TilesX"));
      int tilesY = Integer.parseInt(getFirstNodeValue(group, "TilesY"));

      Element position = getFirstNode(group, "Position");
      Double xPos = new Double(position.getAttribute("X"));
      Double yPos = new Double(position.getAttribute("Y"));
      Double zPos = new Double(position.getAttribute("Z"));

      Double overlap =
        new Double(getFirstNodeValue(group, "TileAcquisitionOverlap"));

      for (int tile=0; tile<tilesX * tilesY; tile++) {
        int index = i * tilesX * tilesY + tile;
        if (index < positionsX.length) {
          positionsX[index] = xPos;
          positionsY[index] = yPos;
          positionsZ[index] = zPos;
        }
      }
    }
  }

  private Element getFirstNode(Element root, String name) {
    if (root == null) {
      return null;
    }
    NodeList list = root.getElementsByTagName(name);
    if (list == null) {
      return null;
    }
    return (Element) list.item(0);
  }

  private NodeList getGrandchildren(Element root, String name) {
    return getGrandchildren(root, name + "s", name);
  }

  private NodeList getGrandchildren(Element root, String child, String name) {
    if (root == null) {
      return null;
    }
    NodeList children = root.getElementsByTagName(child);
    if (children.getLength() > 0) {
      Element childNode = (Element) children.item(0);
      return childNode.getElementsByTagName(name);
    }
    return null;
  }

  private String getFirstNodeValue(Element root, String name) {
    if (root == null) {
      return null;
    }
    NodeList nodes = root.getElementsByTagName(name);
    if (nodes.getLength() > 0) {
      return nodes.item(0).getTextContent();
    }
    return null;
  }

  private void populateOriginalMetadata(Element root, Stack<String> nameStack,
    HashMap<String, Integer> indexes)
  {
    String name = root.getNodeName();
    nameStack.push(name);
    if (root.getChildNodes().getLength() == 1) {
      String value = root.getTextContent();
      StringBuffer key = new StringBuffer();
      for (String k : nameStack) {
        key.append(k);
        key.append(" ");
      }
      if (value != null && key.length() > 0) {
        Integer i = indexes.get(key.toString());
        String storedKey = key.toString() + (i == null ? 0 : i);
        indexes.put(key.toString(), i == null ? 1 : i + 1);
        addGlobalMeta(storedKey, value);
      }
    }

    NodeList children = root.getChildNodes();
    for (int i=0; i<children.getLength(); i++) {
      Object child = children.item(i);
      if (child instanceof Element) {
        populateOriginalMetadata((Element) child, nameStack, indexes);
      }
    }

    nameStack.pop();
  }

  private Segment readSegment() throws IOException {
    // align the stream to a multiple of 32 bytes
    int skip =
      (ALIGNMENT - (int) (in.getFilePointer() % ALIGNMENT)) % ALIGNMENT;
    in.skipBytes(skip);
    long startingPosition = in.getFilePointer();

    // instantiate a Segment subclass based upon the segment ID
    String segmentID = in.readString(16).trim();
    Segment segment = null;

    if (segmentID.equals("ZISRAWFILE")) {
      segment = new FileHeader();
    }
    else if (segmentID.equals("ZISRAWMETADATA")) {
      segment = new Metadata();
    }
    else if (segmentID.equals("ZISRAWSUBBLOCK")) {
      segment = new SubBlock();
    }
    else if (segmentID.equals("ZISRAWATTACH")) {
      segment = new Attachment();
    }
    else {
      LOGGER.info("Unknown segment type: " + segmentID);
      segment = new Segment();
    }
    segment.startingPosition = startingPosition;
    segment.id = segmentID;

    segment.fillInData();
    in.seek(segment.startingPosition + segment.allocatedSize + HEADER_SIZE);
    return segment;
  }

  private void convertPixelType(int pixelType) throws FormatException {
    switch (pixelType) {
      case GRAY8:
        core[0].pixelType = FormatTools.UINT8;
        break;
      case GRAY16:
        core[0].pixelType = FormatTools.UINT16;
        break;
      case GRAY32:
        core[0].pixelType = FormatTools.UINT32;
        break;
      case GRAY_FLOAT:
        core[0].pixelType = FormatTools.FLOAT;
        break;
      case GRAY_DOUBLE:
        core[0].pixelType = FormatTools.DOUBLE;
        break;
      case BGR_24:
        core[0].pixelType = FormatTools.UINT8;
        core[0].sizeC *= 3;
        core[0].rgb = true;
        break;
      case BGR_48:
        core[0].pixelType = FormatTools.UINT16;
        core[0].sizeC *= 3;
        core[0].rgb = true;
        break;
      case BGRA_8:
        core[0].pixelType = FormatTools.UINT8;
        core[0].sizeC *= 4;
        core[0].rgb = true;
        break;
      case BGR_FLOAT:
        core[0].pixelType = FormatTools.FLOAT;
        core[0].sizeC *= 3;
        core[0].rgb = true;
        break;
      case COMPLEX:
      case COMPLEX_FLOAT:
        throw new FormatException("Sorry, complex pixel data not supported.");
      default:
        throw new FormatException("Unknown pixel type: " + pixelType);
    }
  }

  // -- Helper classes --

  /** Top-level class that implements logic common to all types of Segment. */
  class Segment {
    public long startingPosition;
    public String id;
    public long allocatedSize;
    public long usedSize;

    public void fillInData() throws IOException {
      // read the segment header
      allocatedSize = in.readLong();
      usedSize = in.readLong();

      if (usedSize == 0) {
        usedSize = allocatedSize;
      }
    }
  }

  /** Segment with ID "ZISRAWFILE". */
  class FileHeader extends Segment {
    public int majorVersion;
    public int minorVersion;
    public long primaryFileGUID;
    public long fileGUID;
    public int filePart;
    public long directoryPosition;
    public long metadataPosition;
    public boolean updatePending;
    public long attachmentDirectoryPosition;

    public void fillInData() throws IOException {
      super.fillInData();

      majorVersion = in.readInt();
      minorVersion = in.readInt();
      in.skipBytes(4); // reserved 1
      in.skipBytes(4); // reserved 2
      primaryFileGUID = in.readLong();
      fileGUID = in.readLong();
      filePart = in.readInt();
      directoryPosition = in.readLong();
      metadataPosition = in.readLong();
      updatePending = in.readInt() != 0;
      attachmentDirectoryPosition = in.readLong();
    }
  }

  /** Segment with ID "ZISRAWMETADATA". */
  class Metadata extends Segment {
    public String xml;
    public byte[] attachment;

    public void fillInData() throws IOException {
      super.fillInData();

      int xmlSize = in.readInt();
      int attachmentSize = in.readInt();

      in.skipBytes(248);

      xml = in.readString(xmlSize);
      attachment = new byte[attachmentSize];
      in.read(attachment);
    }
  }

  /** Segment with ID "ZISRAWSUBBLOCK". */
  class SubBlock extends Segment {
    public int metadataSize;
    public int attachmentSize;
    public long dataSize;
    public DirectoryEntry directoryEntry;
    public String metadata;

    public int seriesIndex;
    public int planeIndex;

    private long dataOffset;

    private Double stageX, stageY, timestamp, exposureTime;

    public void fillInData() throws IOException {
      super.fillInData();

      long fp = in.getFilePointer();
      metadataSize = in.readInt();
      attachmentSize = in.readInt();
      dataSize = in.readLong();
      directoryEntry = new DirectoryEntry();
      in.skipBytes((int) Math.max(256 - (in.getFilePointer() - fp), 0));

      metadata = in.readString(metadataSize).trim();
      dataOffset = in.getFilePointer();
      in.seek(in.getFilePointer() + dataSize + attachmentSize);

      parseMetadata();
    }

    // -- SubBlock API methods --

    public byte[] readPixelData() throws FormatException, IOException {
      in.seek(dataOffset);
      byte[] data = new byte[(int) dataSize];
      in.read(data);

      CodecOptions options = new CodecOptions();
      options.interleaved = isInterleaved();
      options.littleEndian = isLittleEndian();
      options.maxBytes = getSizeX() * getSizeY() * getRGBChannelCount() *
        FormatTools.getBytesPerPixel(getPixelType());

      switch (directoryEntry.compression) {
        case JPEG:
          data = new JPEGCodec().decompress(data, options);
          break;
        case LZW:
          data = new LZWCodec().decompress(data, options);
          break;
      }

      return data;
    }

    // -- Helper methods --

    private void parseMetadata() throws IOException {
      if (metadata.length() == 0) {
        return;
      }

      Element root = null;
      try {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder parser = factory.newDocumentBuilder();
        ByteArrayInputStream s =
          new ByteArrayInputStream(metadata.getBytes(Constants.ENCODING));
        root = parser.parse(s).getDocumentElement();
        s.close();
      }
      catch (ParserConfigurationException e) {
        return;
      }
      catch (SAXException e) {
        return;
      }

      if (root == null) {
        return;
      }

      NodeList children = root.getChildNodes();

      for (int i=0; i<children.getLength(); i++) {
        if (!(children.item(i) instanceof Element)) {
          continue;
        }
        Element child = (Element) children.item(i);

        if (child.getNodeName().equals("Tags")) {
          NodeList tags = child.getChildNodes();

          for (int tag=0; tag<tags.getLength(); tag++) {
            if (!(tags.item(tag) instanceof Element)) {
              continue;
            }
            Element tagNode = (Element) tags.item(tag);
            if (tagNode.getNodeName().equals("StageXPosition")) {
              stageX = new Double(tagNode.getTextContent());
            }
            else if (tagNode.getNodeName().equals("StageYPosition")) {
              stageY = new Double(tagNode.getTextContent());
            }
            else if (tagNode.getNodeName().equals("AcquisitionTime")) {
              timestamp = DateTools.getTime(
                tagNode.getTextContent(), DateTools.ISO8601_FORMAT) / 1000d;
            }
            else if (tagNode.getNodeName().equals("ExposureTime")) {
              exposureTime = new Double(tagNode.getTextContent());
            }
          }
        }
      }
    }
  }

  /** Segment with ID "ZISRAWATTACH". */
  class Attachment extends Segment {
    public int dataSize;
    public AttachmentEntry attachment;
    public byte[] attachmentData;

    public void fillInData() throws IOException {
      super.fillInData();

      dataSize = in.readInt();
      in.skipBytes(12); // reserved
      attachment = new AttachmentEntry();
      in.skipBytes(112); // reserved
      attachmentData = new byte[dataSize];
      in.read(attachmentData);
    }
  }

  class DirectoryEntry {
    public String schemaType;
    public int pixelType;
    public long filePosition;
    public int filePart;
    public int compression;
    public byte pyramidType;
    public int dimensionCount;
    public DimensionEntry[] dimensionEntries;

    public DirectoryEntry() throws IOException {
      schemaType = in.readString(2);
      pixelType = in.readInt();
      filePosition = in.readLong();
      filePart = in.readInt();
      compression = in.readInt();
      pyramidType = in.readByte();
      in.skipBytes(1); // reserved
      in.skipBytes(4); // reserved
      dimensionCount = in.readInt();

      dimensionEntries = new DimensionEntry[dimensionCount];
      for (int i=0; i<dimensionEntries.length; i++) {
        dimensionEntries[i] = new DimensionEntry();
      }
    }
  }

  class DimensionEntry {
    public String dimension;
    public int start;
    public int size;
    public float startCoordinate;
    public int storedSize;

    public DimensionEntry() throws IOException {
      dimension = in.readString(4).trim();
      start = in.readInt();
      size = in.readInt();
      startCoordinate = in.readFloat();
      storedSize = in.readInt();
    }
  }

  class AttachmentEntry {
    public String schemaType;
    public long filePosition;
    public int filePart;
    public String contentGUID;
    public String contentFileType;
    public String name;

    public AttachmentEntry() throws IOException {
      schemaType = in.readString(2);
      in.skipBytes(10); // reserved
      filePosition = in.readLong();
      filePart = in.readInt();
      contentGUID = in.readString(16);
      contentFileType = in.readString(8);
      name = in.readString(80);
    }
  }

}
