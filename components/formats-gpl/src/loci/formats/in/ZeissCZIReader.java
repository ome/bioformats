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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import loci.common.ByteArrayHandle;
import loci.common.Constants;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.Region;
import loci.common.xml.XMLTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.UnsupportedCompressionException;
import loci.formats.codec.CodecOptions;
import loci.formats.codec.JPEGCodec;
import loci.formats.codec.LZWCodec;
import loci.formats.meta.MetadataStore;

import ome.xml.model.enums.AcquisitionMode;
import ome.xml.model.enums.Binning;
import ome.xml.model.enums.IlluminationType;
import ome.xml.model.primitives.Color;
import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.PercentFraction;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.units.quantity.Pressure;
import ome.units.quantity.Temperature;
import ome.units.quantity.Time;
import ome.units.UNITS;

import org.xml.sax.SAXException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * ZeissCZIReader is the file format reader for Zeiss .czi files.
 */
public class ZeissCZIReader extends FormatReader {

  // -- Constants --

  private static final int ALIGNMENT = 32;
  private static final int HEADER_SIZE = 32;
  private static final String CZI_MAGIC_STRING = "ZISRAWFILE";
  private static final int BUFFER_SIZE = 512;

  /** Compression constants. */
  private static final int UNCOMPRESSED = 0;
  private static final int JPEG = 1;
  private static final int LZW = 2;
  private static final int JPEGXR = 4;

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

  private HashMap<Integer, String> pixels;

  private ArrayList<Segment> segments;
  private ArrayList<SubBlock> planes;
  private HashMap<Coordinate, ArrayList<Integer>> indexIntoPlanes =
    new HashMap<Coordinate, ArrayList<Integer>>();
  private int rotations = 1;
  private int positions = 1;
  private int illuminations = 1;
  private int acquisitions = 1;
  private int mosaics = 1;
  private int phases = 1;
  private int angles = 1;

  private String imageName;
  private String acquiredDate;
  private String description;
  private String userDisplayName, userName;
  private String userFirstName, userLastName, userMiddleName;
  private String userEmail;
  private String userInstitution;
  private String temperature, airPressure, humidity, co2Percent;
  private String correctionCollar, medium, refractiveIndex;

  private String zoom;
  private String gain;

  private ArrayList<Channel> channels = new ArrayList<Channel>();
  private ArrayList<String> binnings = new ArrayList<String>();
  private ArrayList<String> detectorRefs = new ArrayList<String>();
  private ArrayList<Double> timestamps = new ArrayList<Double>();

  private Length[] positionsX;
  private Length[] positionsY;
  private Length[] positionsZ;

  private int previousChannel = 0;

  private Boolean prestitched = null;
  private String objectiveSettingsID;
  private boolean hasDetectorSettings = false;
  private int scanDim = 1;

  private String[] rotationLabels, phaseLabels, illuminationLabels;

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
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 10;
    if (!FormatTools.validStream(stream, blockLen, true)) return false;
    String check = stream.readString(blockLen);
    return check.equals(CZI_MAGIC_STRING);
  }

  /**
   * @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean)
   */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    if (pixels == null || pixels.size() == 0 && noPixels) {
      return null;
    }
    else if (noPixels) {
      return null;
    }
    String[] files = new String[pixels.size() + 1];
    files[0] = currentId;
    Integer[] keys = pixels.keySet().toArray(new Integer[pixels.size()]);
    Arrays.sort(keys);
    for (int i=0; i<keys.length; i++) {
      files[i + 1] = pixels.get(keys[i]);
    }
    return files;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  @Override
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    if ((getPixelType() != FormatTools.INT8 &&
      getPixelType() != FormatTools.UINT8) || previousChannel == -1 ||
      previousChannel >= channels.size())
    {
      return null;
    }

    byte[][] lut = new byte[3][256];

    String color = channels.get(previousChannel).color;
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
  @Override
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    if ((getPixelType() != FormatTools.INT16 &&
      getPixelType() != FormatTools.UINT16) || previousChannel == -1 ||
      previousChannel >= channels.size())
    {
      return null;
    }

    short[][] lut = new short[3][65536];

    String color = channels.get(previousChannel).color;
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
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    previousChannel = getZCTCoords(no)[1];

    int currentSeries = getSeries();

    Region image = new Region(x, y, w, h);

    int currentX = 0;
    int currentY = 0;

    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int pixel = getRGBChannelCount() * bpp;
    int outputRowLen = w * pixel;

    int outputRow = 0, outputCol = 0;

    boolean validScanDim =
      scanDim == (getImageCount() / getSizeC()) && scanDim > 1;
    if (planes.size() == getImageCount()) {
      validScanDim = false;
    }
    int previousHeight = 0;

    Arrays.fill(buf, (byte) 0);
    RandomAccessInputStream stream = new RandomAccessInputStream(currentId);
    try {
      for (SubBlock plane : planes) {
        if ((plane.seriesIndex == currentSeries && plane.planeIndex == no) ||
          (plane.planeIndex == previousChannel && validScanDim))
        {
          byte[] rawData = new SubBlock(plane).readPixelData();

          if ((prestitched != null && prestitched) || validScanDim) {
            int realX = plane.x;
            int realY = plane.y;

            if (prestitched == null) {
              currentY = 0;
            }

            Region tile = new Region(plane.col, plane.row, realX, realY);
            if (validScanDim) {
              tile.y += (no / getSizeC());
              image.height = scanDim;
            }

            if (tile.intersects(image)) {
              Region intersection = tile.intersection(image);
              int intersectionX = 0;

              if (tile.x < image.x) {
                intersectionX = image.x - tile.x;
              }

              if (tile.x == 0 && outputCol > 0) {
                outputCol = 0;
                outputRow += previousHeight;
              }

              int rowLen = pixel * (int) Math.min(intersection.width, realX);
              int outputOffset = outputRow * outputRowLen + outputCol;
              for (int trow=0; trow<intersection.height; trow++) {
                int realRow = trow + intersection.y - tile.y;
                if (validScanDim) {
                  realRow += tile.y;
                }
                int inputOffset = pixel * (realRow * realX + intersectionX);
                System.arraycopy(
                  rawData, inputOffset, buf, outputOffset, rowLen);
                outputOffset += outputRowLen;
              }

              outputCol += rowLen;
              if (outputCol >= w * pixel) {
                outputCol = 0;
                outputRow += intersection.height;
              }
              previousHeight = intersection.height;
            }

            currentX += realX;
            if (currentX >= getSizeX()) {
              currentX = 0;
              currentY += realY;
            }
          }
          else {
            RandomAccessInputStream s = new RandomAccessInputStream(rawData);
            try {
              readPlane(s, x, y, w, h, buf);
            }
            finally {
              s.close();
            }
            break;
          }
        }
      }
    } finally {
        stream.close();
    }

    if (isRGB()) {
      // channels are stored in BGR order; red and blue channels need switching
      for (int i=0; i<buf.length/(getRGBChannelCount()*bpp); i++) {
        for (int b=0; b<bpp; b++) {
          int blueIndex = i * getRGBChannelCount() * bpp + b;
          int redIndex = i * getRGBChannelCount() * bpp + bpp * 2 + b;
          byte red = buf[redIndex];
          buf[redIndex] = buf[blueIndex];
          buf[blueIndex] = red;
        }
      }
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      pixels = null;
      segments = null;
      planes = null;
      rotations = 1;
      positions = 1;
      illuminations = 1;
      acquisitions = 1;
      mosaics = 1;
      phases = 1;
      angles = 1;
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
      zoom = null;
      gain = null;

      channels.clear();
      binnings.clear();
      detectorRefs.clear();
      timestamps.clear();

      previousChannel = 0;
      prestitched = null;
      objectiveSettingsID = null;
      imageName = null;
      hasDetectorSettings = false;
      scanDim = 1;

      rotationLabels = null;
      illuminationLabels = null;
      phaseLabels = null;
      indexIntoPlanes.clear();
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    // switch to the master file if this is part of a multi-file dataset
    String base = id.substring(0, id.lastIndexOf("."));
    if (base.endsWith(")") && isGroupFiles()) {
      LOGGER.info("Checking for master file");
      int lastFileSeparator = base.lastIndexOf(File.separator);
      int end = base.lastIndexOf(" (");
      if (end < 0 || end < lastFileSeparator) {
        end = base.lastIndexOf("(");
      }
      if (end > 0 && end > lastFileSeparator) {
        base = base.substring(0, end) + ".czi";
        if (new Location(base).exists()) {
          LOGGER.info("Initializing master file {}", base);
          initFile(base);
          return;
        }
      }
    }

    CoreMetadata ms0 = core.get(0);

    ms0.littleEndian = true;

    pixels = new HashMap<Integer, String>();
    segments = new ArrayList<Segment>();
    planes = new ArrayList<SubBlock>();

    readSegments(id);

    // check if we have the master file in a multi-file dataset
    // file names are not stored in the files; we have to rely on a
    // specific naming convention:
    //
    //  master_file.czi
    //  master_file (1).czi
    //  master_file (2).czi
    //  ...
    //
    // the number of files is also not stored, so we have to manually check
    // for all files with a matching name

    Location file = new Location(id).getAbsoluteFile();
    base = file.getName();
    base = base.substring(0, base.lastIndexOf("."));

    Location parent = file.getParentFile();
    String[] list = parent.list(true);
    for (String f : list) {
      if (f.startsWith(base + "(") || f.startsWith(base + " (")) {
        String part = f.substring(f.lastIndexOf("(") + 1, f.lastIndexOf(")"));
        pixels.put(Integer.parseInt(part),
          new Location(parent, f).getAbsolutePath());
      }
    }

    Integer[] keys = pixels.keySet().toArray(new Integer[pixels.size()]);
    Arrays.sort(keys);
    for (Integer key : keys) {
      readSegments(pixels.get(key));
    }

    calculateDimensions();

    if (getSizeC() == 0) {
      ms0.sizeC = 1;
    }
    if (getSizeZ() == 0) {
      ms0.sizeZ = 1;
    }
    if (getSizeT() == 0) {
      ms0.sizeT = 1;
    }
    if (getImageCount() == 0) {
      ms0.imageCount = ms0.sizeZ * ms0.sizeT;
    }

    convertPixelType(planes.get(0).directoryEntry.pixelType);

    // remove any invalid SubBlocks

    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    for (int i=0; i<planes.size(); i++) {
      long planeSize = (long) planes.get(i).x * planes.get(i).y * bpp;
      if (planes.get(i).directoryEntry.compression == UNCOMPRESSED) {
        long size = planes.get(i).dataSize;
        if (size < planeSize || planeSize >= Integer.MAX_VALUE || size < 0) {
          LOGGER.trace(
            "removing block #{}; calculated size = {}, recorded size = {}",
            i, planeSize, size);
          planes.remove(i);
          i--;
        }
        else {
          scanDim = (int) (size / planeSize);
        }
      }
      else {
        byte[] pixels = planes.get(i).readPixelData();
        if (pixels.length < planeSize || planeSize >= Integer.MAX_VALUE) {
          LOGGER.trace(
            "removing block #{}; calculated size = {}, decoded size = {}",
            i, planeSize, pixels.length);
          planes.remove(i);
          i--;
        }
        else {
          scanDim = (int) (pixels.length / planeSize);
        }
      }
    }

    if (getSizeZ() == 0) {
      ms0.sizeZ = 1;
    }
    if (getSizeC() == 0) {
      ms0.sizeC = 1;
    }
    if (getSizeT() == 0) {
      ms0.sizeT = 1;
    }

    // set modulo annotations
    // rotations -> modulo Z
    // illuminations -> modulo C
    // phases -> modulo T

    LOGGER.trace("rotations = {}", rotations);
    LOGGER.trace("illuminations = {}", illuminations);
    LOGGER.trace("phases = {}", phases);

    LOGGER.trace("positions = {}", positions);
    LOGGER.trace("acquisitions = {}", acquisitions);
    LOGGER.trace("mosaics = {}", mosaics);
    LOGGER.trace("angles = {}", angles);

    ms0.moduloZ.step = ms0.sizeZ;
    ms0.moduloZ.end = ms0.sizeZ * (rotations - 1);
    ms0.moduloZ.type = FormatTools.ROTATION;
    ms0.sizeZ *= rotations;

    ms0.moduloC.step = ms0.sizeC;
    ms0.moduloC.end = ms0.sizeC * (illuminations - 1);
    ms0.moduloC.type = FormatTools.ILLUMINATION;
    ms0.moduloC.parentType = FormatTools.CHANNEL;
    ms0.sizeC *= illuminations;

    ms0.moduloT.step = ms0.sizeT;
    ms0.moduloT.end = ms0.sizeT * (phases - 1);
    ms0.moduloT.type = FormatTools.PHASE;
    ms0.sizeT *= phases;

    // finish populating the core metadata

    int seriesCount = positions * acquisitions * mosaics * angles;

    ms0.imageCount = getSizeZ() * (isRGB() ? 1 : getSizeC()) * getSizeT();

    LOGGER.trace("Size Z = {}", getSizeZ());
    LOGGER.trace("Size C = {}", getSizeC());
    LOGGER.trace("Size T = {}", getSizeT());
    LOGGER.trace("is RGB = {}", isRGB());
    LOGGER.trace("calculated image count = {}", ms0.imageCount);
    LOGGER.trace("number of available planes = {}", planes.size());
    LOGGER.trace("prestitched = {}", prestitched);
    LOGGER.trace("scanDim = {}", scanDim);

    if (mosaics == seriesCount &&
      seriesCount == (planes.size() / getImageCount()) &&
      prestitched != null && prestitched)
    {
      prestitched = false;
      ms0.sizeX = planes.get(planes.size() - 1).x;
      ms0.sizeY = planes.get(planes.size() - 1).y;
    }

    if (ms0.imageCount * seriesCount > planes.size() * scanDim &&
      planes.size() > 0)
    {
      if (planes.size() != ms0.imageCount && planes.size() != ms0.sizeT &&
        (planes.size() % (seriesCount * getSizeZ())) == 0)
      {
        if (!isGroupFiles() && planes.size() == (ms0.imageCount * seriesCount) / positions) {
          seriesCount /= positions;
          positions = 1;
        }
      }
      else if (planes.size() == ms0.sizeT || planes.size() == ms0.imageCount ||
        (!isGroupFiles() && positions > 1))
      {
        positions = 1;
        acquisitions = 1;
        mosaics = 1;
        angles = 1;
        seriesCount = 1;
      }
    }

    if (seriesCount > 1) {
      core.clear();
      for (int i=0; i<seriesCount; i++) {
        core.add(ms0);
      }
    }

    ms0.dimensionOrder = "XYCZT";

    // populate the OME metadata

    store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    String firstXML = null;
    boolean canSkipXML = true;
    String currentPath = new Location(currentId).getAbsolutePath();
    for (Segment segment : segments) {
      String path = new Location(segment.filename).getAbsolutePath();
      if (currentPath.equals(path) && segment instanceof Metadata) {
        segment.fillInData();
        String xml = ((Metadata) segment).xml;
        xml = XMLTools.sanitizeXML(xml);
        if (firstXML == null && canSkipXML) {
          firstXML = xml;
        }
        if (canSkipXML && firstXML.equals(xml)) {
          translateMetadata(xml);
        }
        else if (!firstXML.equals(xml)) {
          canSkipXML = false;
        }
        ((Metadata) segment).clearXML();
      }
      else if (segment instanceof Attachment) {
        AttachmentEntry entry = ((Attachment) segment).attachment;

        if (entry.name.trim().equals("TimeStamps")) {
          RandomAccessInputStream s =
            new RandomAccessInputStream(((Attachment) segment).attachmentData);
          try {
            s.order(isLittleEndian());
            s.seek(8);
            while (s.getFilePointer() + 8 <= s.length()) {
              timestamps.add(s.readDouble());
            }
          }
          finally {
            s.close();
          }
        }
      }
      segment.close();
    }

    if (rotationLabels != null) {
      ms0.moduloZ.labels = rotationLabels;
      ms0.moduloZ.end = ms0.moduloZ.start;
    }
    if (illuminationLabels != null) {
      ms0.moduloC.labels = illuminationLabels;
      ms0.moduloC.end = ms0.moduloC.start;
    }
    if (phaseLabels != null) {
      ms0.moduloT.labels = phaseLabels;
      ms0.moduloT.end = ms0.moduloT.start;
    }

    assignPlaneIndices();

    for (int i=0; i<planes.size(); i++) {
      SubBlock p = planes.get(i);
      Coordinate c = new Coordinate(p.seriesIndex, p.planeIndex, getImageCount());
      ArrayList<Integer> indices = new ArrayList<Integer>();
      if (indexIntoPlanes.containsKey(c)) {
        indices = indexIntoPlanes.get(c);
      }
      indices.add(i);
      indexIntoPlanes.put(c, indices);
    }

    if (channels.size() > 0 && channels.get(0).color != null) {
      for (int i=0; i<seriesCount; i++) {
        core.get(i).indexed = true;
      }
    }

    String experimenterID = MetadataTools.createLSID("Experimenter", 0);
    store.setExperimenterID(experimenterID, 0);
    store.setExperimenterEmail(userEmail, 0);
    store.setExperimenterFirstName(userFirstName, 0);
    store.setExperimenterInstitution(userInstitution, 0);
    store.setExperimenterLastName(userLastName, 0);
    store.setExperimenterMiddleName(userMiddleName, 0);
    store.setExperimenterUserName(userName, 0);

    String name = new Location(getCurrentFile()).getName();
    if (imageName != null && imageName.trim().length() > 0) {
      name = imageName;
    }

    int indexLength = String.valueOf(getSeriesCount()).length();
    for (int i=0; i<getSeriesCount(); i++) {
      store.setImageInstrumentRef(MetadataTools.createLSID("Instrument", 0), i);
      if (acquiredDate != null) {
        store.setImageAcquisitionDate(new Timestamp(acquiredDate), i);
      }
      else if (planes.get(0).timestamp != null) {
        long timestamp = (long) (planes.get(0).timestamp * 1000);
        String date =
          DateTools.convertDate(timestamp, DateTools.UNIX);
        store.setImageAcquisitionDate(new Timestamp(date), i);
      }
      if (experimenterID != null) {
        store.setImageExperimenterRef(experimenterID, i);
      }

      String imageIndex = String.valueOf(i + 1);
      while (imageIndex.length() < indexLength) {
        imageIndex = "0" + imageIndex;
      }
      store.setImageName(name + " #" + imageIndex, i);
      if (description != null && description.length() > 0) {
        store.setImageDescription(description, i);
      }

      if (airPressure != null) {
        store.setImagingEnvironmentAirPressure(
                new Pressure(new Double(airPressure), UNITS.MBAR), i);
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
        store.setImagingEnvironmentTemperature(new Temperature(
                new Double(temperature), UNITS.DEGREEC), i);
      }

      if (objectiveSettingsID != null) {
        store.setObjectiveSettingsID(objectiveSettingsID, i);
        if (correctionCollar != null) {
          store.setObjectiveSettingsCorrectionCollar(
            new Double(correctionCollar), i);
        }
        if (medium != null) {
          store.setObjectiveSettingsMedium(getMedium(medium), i);
        }
        if (refractiveIndex != null) {
          store.setObjectiveSettingsRefractiveIndex(
            new Double(refractiveIndex), i);
        }
      }

      Double startTime = null;
      if (acquiredDate != null) {
        Timestamp t = Timestamp.valueOf(acquiredDate);
        if (t != null)
          startTime = t.asInstant().getMillis() / 1000d;
      }

      for (int plane=0; plane<getImageCount(); plane++) {
        Coordinate coordinate = new Coordinate(i, plane, getImageCount());
        ArrayList<Integer> index = indexIntoPlanes.get(coordinate);
        if (index == null) {
          continue;
        }

        SubBlock p = planes.get(index.get(0));
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

        if (p.stageZ != null) {
          store.setPlanePositionZ(p.stageZ, i, plane);
        }
        else if (positionsZ != null && i < positionsZ.length) {
          store.setPlanePositionZ(positionsZ[i], i, plane);
        }

        if (p.timestamp != null) {
          store.setPlaneDeltaT(new Time(p.timestamp - startTime, UNITS.S), i, plane);
        }
        else if (plane < timestamps.size()) {
           if (timestamps.get(plane) != null) {
             store.setPlaneDeltaT(new Time(timestamps.get(plane), UNITS.S), i, plane);
           }
        }
        if (p.exposureTime != null) {
          store.setPlaneExposureTime(new Time(p.exposureTime, UNITS.S), i, plane);
        }
        else {
          int channel = getZCTCoords(plane)[1];
          if (channel < channels.size() &&
            channels.get(channel).exposure != null)
          {
            store.setPlaneExposureTime(
              new Time(channels.get(channel).exposure, UNITS.S), i, plane);
          }
        }
      }

      for (int c=0; c<getEffectiveSizeC(); c++) {
        if (c < channels.size()) {
          store.setChannelName(channels.get(c).name, i, c);
          store.setChannelFluor(channels.get(c).fluor, i, c);
          store.setChannelFilterSetRef(channels.get(c).filterSetRef, i, c);

          String color = channels.get(c).color;
          if (color != null) {
            color = color.replaceAll("#", "");
            color = color.substring(2, color.length());
            try {
              store.setChannelColor(
                new Color((Integer.parseInt(color, 16) << 8) | 0xff), i, c);
            }
            catch (NumberFormatException e) {
              LOGGER.warn("", e);
            }
          }

          String emWave = channels.get(c).emission;
          if (emWave != null) {
            Double wave = new Double(emWave);
            Length em = FormatTools.getEmissionWavelength(wave);
            if (em != null) {
              store.setChannelEmissionWavelength(em, i, c);
            }
          }
          String exWave = channels.get(c).excitation;
          if (exWave != null) {
            Double wave = new Double(exWave);
            Length ex = FormatTools.getExcitationWavelength(wave);
            if (ex != null) {
              store.setChannelExcitationWavelength(ex, i, c);
            }
          }

          if (channels.get(c).illumination != null) {
            store.setChannelIlluminationType(
              channels.get(c).illumination, i, c);
          }

          if (channels.get(c).pinhole != null) {
            store.setChannelPinholeSize(
              new Length(new Double(channels.get(c).pinhole), UNITS.MICROM), i, c);
          }

          if (channels.get(c).acquisitionMode != null) {
            store.setChannelAcquisitionMode(
              channels.get(c).acquisitionMode, i, c);
          }
        }

        if (c < detectorRefs.size()) {
          String detector = detectorRefs.get(c);
          store.setDetectorSettingsID(detector, i, c);

          if (c < binnings.size()) {
            store.setDetectorSettingsBinning(getBinning(binnings.get(c)), i, c);
          }
          if (c < channels.size()) {
            store.setDetectorSettingsGain(channels.get(c).gain, i, c);
          }
        }

        if (c < channels.size()) {
          if (hasDetectorSettings) {
            store.setDetectorSettingsGain(channels.get(c).gain, i, c);
          }
        }
      }
    }

    // not needed by further calls on the reader
    segments = null;
  }

  // -- Helper methods --

  private void readSegments(String id) throws IOException {
    if (in != null) {
      in.close();
    }
    in = new RandomAccessInputStream(id, BUFFER_SIZE);
    in.order(isLittleEndian());
    while (in.getFilePointer() < in.length()) {
      Segment segment = readSegment(id);
      if (segment == null) {
        break;
      }
      segments.add(segment);

      if (segment instanceof SubBlock) {
        planes.add((SubBlock) segment);
      }
      segment.close();
    }
  }

  private void calculateDimensions() {
    // calculate the dimensions
    CoreMetadata ms0 = core.get(0);

    ArrayList<Integer> uniqueT = new ArrayList<Integer>();

    for (SubBlock plane : planes) {
      for (DimensionEntry dimension : plane.directoryEntry.dimensionEntries) {
        if (dimension == null) {
          continue;
        }
        switch (dimension.dimension.charAt(0)) {
          case 'X':
            plane.x = dimension.size;
            plane.col = dimension.start;
            if ((prestitched == null || prestitched) &&
              getSizeX() > 0 && dimension.size != getSizeX())
            {
              prestitched = true;
              continue;
            }
            ms0.sizeX = dimension.size;
            break;
          case 'Y':
            plane.y = dimension.size;
            plane.row = dimension.start;
            if ((prestitched == null || prestitched) &&
              getSizeY() > 0 && dimension.size != getSizeY())
            {
              prestitched = true;
              continue;
            }
            ms0.sizeY = dimension.size;
            break;
          case 'C':
            if (dimension.start >= getSizeC()) {
              ms0.sizeC = dimension.start + 1;
            }
            break;
          case 'Z':
            if (dimension.start >= getSizeZ()) {
              ms0.sizeZ = dimension.start + 1;
            }
            else if (dimension.size > getSizeZ()) {
              ms0.sizeZ = dimension.size;
            }
            break;
          case 'T':
            if (!uniqueT.contains(dimension.start)) {
              uniqueT.add(dimension.start);
              ms0.sizeT = uniqueT.size();
            }
            if (dimension.size > getSizeT()) {
              ms0.sizeT = dimension.size;
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
          case 'V':
            if (dimension.start >= angles) {
              angles = dimension.start + 1;
            }
            break;
          default:
            LOGGER.warn("Unknown dimension '{}'", dimension.dimension);
        }
      }
    }
  }

  private void assignPlaneIndices() {
    LOGGER.trace("assignPlaneIndices:");
    // assign plane and series indices to each SubBlock

    if (getSeriesCount() == mosaics) {
      LOGGER.trace("  reset position, acquisition, and angle count");
      positions = 1;
      acquisitions = 1;
      angles = 1;
    }

    int[] extraLengths = {positions, acquisitions, mosaics, angles};

    for (int p=0; p<planes.size(); p++) {
      LOGGER.trace("  processing plane #{} of {}", p, planes.size());
      SubBlock plane = planes.get(p);
      int z = 0;
      int c = 0;
      int t = 0;
      int r = 0;
      int i = 0;
      int phase = 0;
      int[] extra = new int[4];

      boolean noAngle = true;
      for (DimensionEntry dimension : plane.directoryEntry.dimensionEntries) {
        LOGGER.trace("    procession dimension '{}'", dimension.dimension);
        LOGGER.trace("      dimension size = {}", dimension.size);
        LOGGER.trace("      dimension start = {}", dimension.start);
        if (dimension == null) {
          continue;
        }
        switch (dimension.dimension.charAt(0)) {
          case 'C':
            c = dimension.start;
            break;
          case 'Z':
            z = dimension.start;
            if (z >= getSizeZ()) {
              z = getSizeZ() - 1;
            }
            break;
          case 'T':
            t = dimension.start;
            if (t >= getSizeT()) {
              t = getSizeT() - 1;
            }
            break;
          case 'R':
            r = dimension.start;
            break;
          case 'S':
            extra[0] = dimension.start;
            if (extra[0] >= extraLengths[0]) {
              extra[0] = 0;
            }
            break;
          case 'I':
            i = dimension.start;
            break;
          case 'B':
            extra[1] = dimension.start;
            if (extra[1] >= extraLengths[1]) {
              extra[1] = 0;
            }
            break;
          case 'M':
            extra[2] = dimension.start;
            if (extra[2] >= extraLengths[2]) {
              extra[2] = 0;
            }
            break;
          case 'H':
            phase = dimension.start;
            break;
          case 'V':
            extra[3] = dimension.start;
            if (extra[3] >= extraLengths[3]) {
              extra[3] = 0;
            }
            noAngle = false;
            break;
        }
      }

      if (angles > 1 && noAngle) {
        extra[3] = p / (getImageCount() * (getSeriesCount() / angles));
      }

      if (rotations > 0) {
        z = r * (getSizeZ() / rotations) + z;
      }
      if (illuminations > 0) {
        c = i * (getSizeC() / illuminations) + c;
      }
      if (phases > 0) {
        t = phase * (getSizeT() / phases) + t;
      }

      plane.planeIndex = getIndex(z, c, t);
      plane.seriesIndex = FormatTools.positionToRaster(extraLengths, extra);
      LOGGER.trace("    assigned plane index = {}; series index = {}",
        plane.planeIndex, plane.seriesIndex);
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

    translateExperiment(realRoot);
    translateInformation(realRoot);
    translateScaling(realRoot);
    translateDisplaySettings(realRoot);
    translateLayers(realRoot);
    translateHardwareSettings(realRoot);

    Stack<String> nameStack = new Stack<String>();
    populateOriginalMetadata(realRoot, nameStack);
  }

  private void translateInformation(Element root) throws FormatException {
    NodeList informations = root.getElementsByTagName("Information");
    if (informations == null || informations.getLength() == 0) {
      return;
    }

    Element information = (Element) informations.item(0);
    Element image = getFirstNode(information, "Image");
    Element user = getFirstNode(information, "User");
    Element environment = getFirstNode(information, "Environment");
    Element instrument = getFirstNode(information, "Instrument");
    Element document = getFirstNode(information, "Document");

    if (image != null) {
      String bitCount = getFirstNodeValue(image, "ComponentBitCount");
      if (bitCount != null) {
        core.get(0).bitsPerPixel = Integer.parseInt(bitCount);
      }

      acquiredDate = getFirstNodeValue(image, "AcquisitionDateAndTime");

      Element objectiveSettings = getFirstNode(image, "ObjectiveSettings");
      correctionCollar =
        getFirstNodeValue(objectiveSettings, "CorrectionCollar");
      medium = getFirstNodeValue(objectiveSettings, "Medium");
      refractiveIndex = getFirstNodeValue(objectiveSettings, "RefractiveIndex");

      String sizeV = getFirstNodeValue(image, "SizeV");
      if (sizeV != null && angles == 1) {
        angles = Integer.parseInt(sizeV);
      }

      Element dimensions = getFirstNode(image, "Dimensions");

      NodeList channelNodes = getGrandchildren(dimensions, "Channel");
      if (channelNodes == null) {
        channelNodes = image.getElementsByTagName("Channel");
      }

      if (channelNodes != null) {
        for (int i=0; i<channelNodes.getLength(); i++) {
          Element channel = (Element) channelNodes.item(i);

          while (channels.size() <= i) {
            channels.add(new Channel());
          }

          channels.get(i).emission =
            getFirstNodeValue(channel, "EmissionWavelength");
          channels.get(i).excitation =
            getFirstNodeValue(channel, "ExcitationWavelength");
          channels.get(i).pinhole = getFirstNodeValue(channel, "PinholeSize");

          channels.get(i).name = channel.getAttribute("Name");

          String illumination = getFirstNodeValue(channel, "IlluminationType");
          if (illumination != null) {
            channels.get(i).illumination = getIlluminationType(illumination);
          }
          String acquisition = getFirstNodeValue(channel, "AcquisitionMode");
          if (acquisition != null) {
            channels.get(i).acquisitionMode = getAcquisitionMode(acquisition);
          }

          Element detectorSettings = getFirstNode(channel, "DetectorSettings");

          String binning = getFirstNodeValue(detectorSettings, "Binning");
          if (binning != null) {
            binning = binning.replaceAll(",", "x");
            binnings.add(binning);
          }

          Element scanInfo = getFirstNode(channel, "LaserScanInfo");
          if (scanInfo != null) {
            zoom = getFirstNodeValue(scanInfo, "ZoomX");
          }

          Element detector = getFirstNode(detectorSettings, "Detector");
          if (detector != null) {
            String detectorID = detector.getAttribute("Id");
            if (detectorID.indexOf(" ") != -1) {
              detectorID =
                detectorID.substring(detectorID.lastIndexOf(" ") + 1);
            }
            if (!detectorID.startsWith("Detector:")) {
              detectorID = "Detector:" + detectorID;
            }
            detectorRefs.add(detectorID);
          }

          Element filterSet = getFirstNode(channel, "FilterSetRef");
          if (filterSet != null) {
            channels.get(i).filterSetRef = filterSet.getAttribute("Id");
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

        String microscopeType = getFirstNodeValue(microscope, "Type");
        if (microscopeType != null) {
          store.setMicroscopeType(getMicroscopeType(microscopeType), 0);
        }
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
            if (power != null) {
              store.setLaserPower(new Power(new Double(power), UNITS.MW), 0, i);
            }
            store.setLaserLotNumber(lotNumber, 0, i);
            store.setLaserManufacturer(manufacturer, 0, i);
            store.setLaserModel(model, 0, i);
            store.setLaserSerialNumber(serialNumber, 0, i);
          }
          else if ("Arc".equals(type)) {
            if (power != null) {
              store.setArcPower(new Power(new Double(power), UNITS.MW), 0, i);
            }
            store.setArcLotNumber(lotNumber, 0, i);
            store.setArcManufacturer(manufacturer, 0, i);
            store.setArcModel(model, 0, i);
            store.setArcSerialNumber(serialNumber, 0, i);
          }
          else if ("LightEmittingDiode".equals(type)) {
            if (power != null) {
              store.setLightEmittingDiodePower(new Power(new Double(power), UNITS.MW), 0, i);
            }
            store.setLightEmittingDiodeLotNumber(lotNumber, 0, i);
            store.setLightEmittingDiodeManufacturer(manufacturer, 0, i);
            store.setLightEmittingDiodeModel(model, 0, i);
            store.setLightEmittingDiodeSerialNumber(serialNumber, 0, i);
          }
          else if ("Filament".equals(type)) {
            if (power != null) {
              store.setFilamentPower(new Power(new Double(power), UNITS.MW), 0, i);
            }
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

          String detectorID = detector.getAttribute("Id");
          if (detectorID.indexOf(" ") != -1) {
            detectorID = detectorID.substring(detectorID.lastIndexOf(" ") + 1);
          }
          if (!detectorID.startsWith("Detector:")) {
            detectorID = "Detector:" + detectorID;
          }

          store.setDetectorID(detectorID, 0, i);
          store.setDetectorManufacturer(manufacturer, 0, i);
          store.setDetectorModel(model, 0, i);
          store.setDetectorSerialNumber(serialNumber, 0, i);
          store.setDetectorLotNumber(lotNumber, 0, i);

          if (gain == null) {
            gain = getFirstNodeValue(detector, "Gain");
          }
          if (gain != null && !gain.equals("")) {
            store.setDetectorGain(new Double(gain), 0, i);
          }

          String offset = getFirstNodeValue(detector, "Offset");
          if (offset != null && !offset.equals("")) {
            store.setDetectorOffset(new Double(offset), 0, i);
          }

          if (zoom == null) {
            zoom = getFirstNodeValue(detector, "Zoom");
          }
          if (zoom != null && !zoom.equals("")) {
            store.setDetectorZoom(new Double(zoom), 0, i);
          }

          String ampGain = getFirstNodeValue(detector, "AmplificationGain");
          if (ampGain != null && !ampGain.equals("")) {
            store.setDetectorAmplificationGain(new Double(ampGain), 0, i);
          }

          String detectorType = getFirstNodeValue(detector, "Type");
          if (detectorType != null && !detectorType.equals("")) {
            store.setDetectorType(getDetectorType(detectorType), 0, i);
          }
        }
      }

      NodeList objectives = getGrandchildren(instrument, "Objective");
      parseObjectives(objectives);

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

          String dichroicRef = getFirstNodeValue(filterSet, "DichroicRef");
          NodeList excitations = getGrandchildren(
            filterSet, "ExcitationFilters", "ExcitationFilterRef");
          NodeList emissions = getGrandchildren(filterSet, "EmissionFilters",
            "EmissionFilterRef");

          if (dichroicRef == null || dichroicRef.length() <= 0) {
            Element ref = getFirstNode(filterSet, "DichroicRef");
            if (ref != null) {
              dichroicRef = ref.getAttribute("Id");
            }
          }

          if (excitations == null) {
            excitations = filterSet.getElementsByTagName("ExcitationFilterRef");
          }

          if (emissions == null) {
            emissions = filterSet.getElementsByTagName("EmissionFilterRef");
          }

          if (dichroicRef != null || excitations != null || emissions != null) {
            store.setFilterSetID(filterSet.getAttribute("Id"), 0, i);
            store.setFilterSetManufacturer(manufacturer, 0, i);
            store.setFilterSetModel(model, 0, i);
            store.setFilterSetSerialNumber(serialNumber, 0, i);
            store.setFilterSetLotNumber(lotNumber, 0, i);
          }

          if (dichroicRef != null && dichroicRef.length() > 0) {
            store.setFilterSetDichroicRef(dichroicRef, 0, i);
          }

          if (excitations != null) {
            for (int ex=0; ex<excitations.getLength(); ex++) {
              Element excitation = (Element) excitations.item(ex);
              String ref = excitation.getTextContent();
              if (ref == null || ref.length() <= 0) {
                ref = excitation.getAttribute("Id");
              }
              if (ref != null && ref.length() > 0) {
                store.setFilterSetExcitationFilterRef(ref, 0, i, ex);
              }
            }
          }
          if (emissions != null) {
            for (int em=0; em<emissions.getLength(); em++) {
              Element emission = (Element) emissions.item(em);
              String ref = emission.getTextContent();
              if (ref == null || ref.length() <= 0) {
                ref = emission.getAttribute("Id");
              }
              if (ref != null && ref.length() > 0) {
                store.setFilterSetEmissionFilterRef(ref, 0, i, em);
              }
            }
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

          String filterType = getFirstNodeValue(filter, "Type");
          if (filterType != null) {
            store.setFilterType(getFilterType(filterType), 0, i);
          }
          store.setFilterFilterWheel(
            getFirstNodeValue(filter, "FilterWheel"), 0, i);

          Element transmittance = getFirstNode(filter, "TransmittanceRange");

          String cutIn = getFirstNodeValue(transmittance, "CutIn");
          String cutOut = getFirstNodeValue(transmittance, "CutOut");
          Double inWave = cutIn == null ? 0 : new Double(cutIn);
          Double outWave = cutOut == null ? 0 : new Double(cutOut);

          Length in = FormatTools.getCutIn(inWave);
          Length out = FormatTools.getCutOut(outWave);
          if (in != null) {
            store.setTransmittanceRangeCutIn(in, 0, i);
          }
          if (out != null) {
            store.setTransmittanceRangeCutOut(out, 0, i);
          }

          String inTolerance =
            getFirstNodeValue(transmittance, "CutInTolerance");
          String outTolerance =
            getFirstNodeValue(transmittance, "CutOutTolerance");

          if (inTolerance != null) {
            Double cutInTolerance = new Double(inTolerance);
            store.setTransmittanceRangeCutInTolerance(
              new Length(cutInTolerance, UNITS.NM), 0, i);
          }

          if (outTolerance != null) {
            Double cutOutTolerance = new Double(outTolerance);
            store.setTransmittanceRangeCutOutTolerance(
              new Length(cutOutTolerance, UNITS.NM), 0, i);
          }

          String transmittancePercent =
            getFirstNodeValue(transmittance, "Transmittance");
          if (transmittancePercent != null) {
            store.setTransmittanceRangeTransmittance(
              PercentFraction.valueOf(transmittancePercent), 0, i);
          }
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

    if (document != null) {
      description = getFirstNodeValue(document, "Description");

      if (userName == null) {
        userName = getFirstNodeValue(document, "UserName");
      }

      imageName = getFirstNodeValue(document, "Name");
    }
  }

  private void translateScaling(Element root) {
    NodeList scalings = root.getElementsByTagName("Scaling");
    if (scalings == null || scalings.getLength() == 0) {
      return;
    }

    Element scaling = (Element) scalings.item(0);
    NodeList distances = getGrandchildren(scaling, "Items", "Distance");

    if (distances != null) {
      for (int i=0; i<distances.getLength(); i++) {
        Element distance = (Element) distances.item(i);
        String id = distance.getAttribute("Id");
        String originalValue = getFirstNodeValue(distance, "Value");
        if (originalValue == null) {
          continue;
        }
        Double value = new Double(originalValue) * 1000000;
        if (value > 0) {
          PositiveFloat size = new PositiveFloat(value);

          if (id.equals("X")) {
            for (int series=0; series<getSeriesCount(); series++) {
              store.setPixelsPhysicalSizeX(FormatTools.createLength(size, UNITS.MICROM), series);
            }
          }
          else if (id.equals("Y")) {
            for (int series=0; series<getSeriesCount(); series++) {
              store.setPixelsPhysicalSizeY(FormatTools.createLength(size, UNITS.MICROM), series);
            }
          }
          else if (id.equals("Z")) {
            for (int series=0; series<getSeriesCount(); series++) {
              store.setPixelsPhysicalSizeZ(FormatTools.createLength(size, UNITS.MICROM), series);
            }
          }
        }
        else {
          LOGGER.debug(
            "Expected positive value for PhysicalSize; got {}", value);
        }
      }
    }
  }

  private void translateDisplaySettings(Element root) throws FormatException {
    NodeList displaySettings = root.getElementsByTagName("DisplaySetting");
    if (displaySettings == null || displaySettings.getLength() == 0) {
      return;
    }

    Element displaySetting = (Element) displaySettings.item(0);
    NodeList channelNodes = getGrandchildren(displaySetting, "Channel");

    if (channelNodes != null) {
      for (int i=0; i<channelNodes.getLength(); i++) {
        Element channel = (Element) channelNodes.item(i);
        String color = getFirstNodeValue(channel, "Color");
        if (color == null) {
          color = getFirstNodeValue(channel, "OriginalColor");
        }

        while (channels.size() <= i) {
          channels.add(new Channel());
        }
        channels.get(i).color = color;

        String fluor = getFirstNodeValue(channel, "DyeName");
        if (fluor != null) {
          channels.get(i).fluor = fluor;
        }
        String name = channel.getAttribute("Name");
        if (name != null) {
          channels.get(i).name = name;
        }

        String emission = getFirstNodeValue(channel, "DyeMaxEmission");
        if (emission != null) {
          channels.get(i).emission = emission;
        }
        String excitation = getFirstNodeValue(channel, "DyeMaxExcitation");
        if (excitation != null) {
          channels.get(i).excitation = excitation;
        }

        String illumination = getFirstNodeValue(channel, "IlluminationType");

        if (illumination != null) {
          channels.get(i).illumination = getIlluminationType(illumination);
        }
      }
    }
  }

  private void translateLayers(Element root) {
    NodeList layerses = root.getElementsByTagName("Layers");
    if (layerses == null || layerses.getLength() == 0) {
      return;
    }

    Element layersNode = (Element) layerses.item(0);
    NodeList layers = layersNode.getElementsByTagName("Layer");

    if (layers != null) {
      for (int i=0; i<layers.getLength(); i++) {
        Element layer = (Element) layers.item(i);

        NodeList elementses = layer.getElementsByTagName("Elements");
        if (elementses.getLength() == 0) {
          continue;
        }
        NodeList allGrandchildren = elementses.item(0).getChildNodes();

        int shape = 0;

        NodeList lines = getGrandchildren(layer, "Elements", "Line");
        shape = populateLines(lines, i, shape);

        NodeList arrows = getGrandchildren(layer, "Elements", "OpenArrow");
        shape = populateLines(arrows, i, shape);

        NodeList crosses = getGrandchildren(layer, "Elements", "Cross");
        for (int s=0; s<crosses.getLength(); s++, shape+=2) {
          Element cross = (Element) crosses.item(s);

          Element geometry = getFirstNode(cross, "Geometry");
          Element textElements = getFirstNode(cross, "TextElements");
          Element attributes = getFirstNode(cross, "Attributes");

          store.setLineID(
            MetadataTools.createLSID("Shape", i, shape), i, shape);
          store.setLineID(
            MetadataTools.createLSID("Shape", i, shape + 1), i, shape + 1);

          String length = getFirstNodeValue(geometry, "Length");
          String centerX = getFirstNodeValue(geometry, "CenterX");
          String centerY = getFirstNodeValue(geometry, "CenterY");

          if (length != null) {
            Double halfLen = new Double(length) / 2;
            if (centerX != null) {
              store.setLineX1(new Double(centerX) - halfLen, i, shape);
              store.setLineX2(new Double(centerX) + halfLen, i, shape);

              store.setLineX1(new Double(centerX), i, shape + 1);
              store.setLineX2(new Double(centerX), i, shape + 1);
            }
            if (centerY != null) {
              store.setLineY1(new Double(centerY), i, shape);
              store.setLineY2(new Double(centerY), i, shape);

              store.setLineY1(new Double(centerY) - halfLen, i, shape + 1);
              store.setLineY2(new Double(centerY) + halfLen, i, shape + 1);
            }
          }
          store.setLineText(getFirstNodeValue(textElements, "Text"), i, shape);
          store.setLineText(getFirstNodeValue(textElements, "Text"), i, shape + 1);
        }

        NodeList rectangles = getGrandchildren(layer, "Elements", "Rectangle");
        if (rectangles != null) {
          shape = populateRectangles(rectangles, i, shape);
        }

        NodeList ellipses = getGrandchildren(layer, "Elements", "Ellipse");
        if (ellipses != null) {
          for (int s=0; s<ellipses.getLength(); s++, shape++) {
            Element ellipse = (Element) ellipses.item(s);

            Element geometry = getFirstNode(ellipse, "Geometry");
            Element textElements = getFirstNode(ellipse, "TextElements");
            Element attributes = getFirstNode(ellipse, "Attributes");

            store.setEllipseID(
              MetadataTools.createLSID("Shape", i, shape), i, shape);

            String radiusX = getFirstNodeValue(geometry, "RadiusX");
            String radiusY = getFirstNodeValue(geometry, "RadiusY");
            String centerX = getFirstNodeValue(geometry, "CenterX");
            String centerY = getFirstNodeValue(geometry, "CenterY");

            if (radiusX != null) {
              store.setEllipseRadiusX(new Double(radiusX), i, shape);
            }
            if (radiusY != null) {
              store.setEllipseRadiusY(new Double(radiusY), i, shape);
            }
            if (centerX != null) {
              store.setEllipseX(new Double(centerX), i, shape);
            }
            if (centerY != null) {
              store.setEllipseY(new Double(centerY), i, shape);
            }
            store.setEllipseText(
              getFirstNodeValue(textElements, "Text"), i, shape);
          }
        }

        // translate all of the circle ROIs
        NodeList circles = getGrandchildren(layer, "Elements", "Circle");
        if (circles != null) {
          shape = populateCircles(circles, i, shape);
        }
        NodeList inOutCircles =
          getGrandchildren(layer, "Elements", "InOutCircle");
        if (inOutCircles != null) {
          shape = populateCircles(inOutCircles, i, shape);
        }
        NodeList outInCircles =
          getGrandchildren(layer, "Elements", "OutInCircle");
        if (outInCircles != null) {
          shape = populateCircles(outInCircles, i, shape);
        }
        NodeList pointsCircles =
          getGrandchildren(layer, "Elements", "PointsCircle");
        if (pointsCircles != null) {
          shape = populateCircles(pointsCircles, i, shape);
        }

        NodeList polygons = getGrandchildren(layer, "Elements", "Polygon");
        if (polygons != null) {
          shape = populatePolylines(polygons, i, shape, true);
        }

        NodeList polylines = getGrandchildren(layer, "Elements", "Polyline");
        if (polylines != null) {
          shape = populatePolylines(polylines, i, shape, false);
        }

        NodeList openPolylines =
          getGrandchildren(layer, "Elements", "OpenPolyline");
        if (openPolylines != null) {
          shape = populatePolylines(openPolylines, i, shape, false);
        }

        NodeList closedPolylines =
          getGrandchildren(layer, "Elements", "ClosedPolyline");
        if (closedPolylines != null) {
          shape = populatePolylines(closedPolylines, i, shape, true);
        }

        NodeList rectRoi = getGrandchildren(layer, "Elements", "RectRoi");
        if (rectRoi != null) {
          shape = populateRectangles(rectRoi, i, shape);
        }
        NodeList textBoxes = getGrandchildren(layer, "Elements", "TextBox");
        if (textBoxes != null) {
          shape = populateRectangles(textBoxes, i, shape);
        }
        NodeList text = getGrandchildren(layer, "Elements", "Text");
        if (text != null) {
          shape = populateRectangles(text, i, shape);
        }

        if (shape > 0) {
          String roiID = MetadataTools.createLSID("ROI", i);
          store.setROIID(roiID, i);
          store.setROIName(layer.getAttribute("Name"), i);
          store.setROIDescription(getFirstNodeValue(layer, "Usage"), i);

          for (int series=0; series<getSeriesCount(); series++) {
            store.setImageROIRef(roiID, series, i);
          }
        }
      }
    }
  }

  private void translateHardwareSettings(Element root) throws FormatException {
    NodeList hardwareSettings = root.getElementsByTagName("HardwareSetting");
    if (hardwareSettings == null || hardwareSettings.getLength() == 0) {
      return;
    }

    Element hardware = (Element) hardwareSettings.item(0);

    store.setInstrumentID(MetadataTools.createLSID("Instrument", 0), 0);

    Element microscope = getFirstNode(hardware, "Microscope");
    if (microscope != null) {
      String model = microscope.getAttribute("Name");
      store.setMicroscopeModel(model, 0);
    }

    Element objectiveChanger = getFirstNode(hardware, "ObjectiveChanger");
    if (objectiveChanger != null) {
      String position = getFirstNodeValue(objectiveChanger, "Position");
      int positionIndex = -1;
      if (position != null) {
        try {
          positionIndex = Integer.parseInt(position) - 1;
        }
        catch (NumberFormatException e) {
          LOGGER.debug("Could not parse ObjectiveSettings", e);
        }
      }

      NodeList objectives = objectiveChanger.getElementsByTagName("Objective");

      if (objectives != null) {
        for (int i=0; i<objectives.getLength(); i++) {
          Element objective = (Element) objectives.item(i);

          String objectiveID = MetadataTools.createLSID("Objective", 0, i);
          if (i == positionIndex ||
            (objectives.getLength() == 1 && objectiveSettingsID != null))
          {
            objectiveSettingsID = objectiveID;
          }

          store.setObjectiveID(objectiveID, 0, i);
          store.setObjectiveModel(objective.getAttribute("Model"), 0, i);
          store.setObjectiveSerialNumber(
            objective.getAttribute("UniqueName"), 0, i);

          String immersion = getFirstNodeValue(objective, "Immersions");
          store.setObjectiveImmersion(getImmersion(immersion), 0, i);
          store.setObjectiveCorrection(getCorrection("Other"), 0, i);

          String magnification = getFirstNodeValue(objective, "Magnification");
          String na = getFirstNodeValue(objective, "NumericalAperture");
          String wd = getFirstNodeValue(objective, "WorkingDistance");

          if (magnification != null) {
            try {
              store.setObjectiveNominalMagnification(
                new Double(magnification), 0, i);
            }
            catch (NumberFormatException e) {
              LOGGER.debug("Could not parse magnification", e);
            }
          }
          if (na != null) {
            try {
              store.setObjectiveLensNA(new Double(na), 0, i);
            }
            catch (NumberFormatException e) {
              LOGGER.debug("Could not parse numerical aperture", e);
            }
          }
          if (wd != null) {
            try {
              store.setObjectiveWorkingDistance(new Length(new Double(wd), UNITS.MICROM), 0, i);
            }
            catch (NumberFormatException e) {
              LOGGER.debug("Could not parse working distance", e);
            }
          }
        }
      }
    }

  }

  private int populateRectangles(NodeList rectangles, int roi, int shape) {
    for (int s=0; s<rectangles.getLength(); s++) {
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
        store.setRectangleY(new Double(top), roi, shape);
        store.setRectangleWidth(new Double(width), roi, shape);
        store.setRectangleHeight(new Double(height), roi, shape);

        String name = getFirstNodeValue(attributes, "Name");
        String label = getFirstNodeValue(textElements, "Text");

        if (label != null) {
          store.setRectangleText(label, roi, shape);
        }
        shape++;
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

      String shapeID = MetadataTools.createLSID("Shape", roi, shape);

      if (closed) {
        store.setPolygonID(shapeID, roi, shape);
        store.setPolygonPoints(
          getFirstNodeValue(geometry, "Points"), roi, shape);
        store.setPolygonText(
          getFirstNodeValue(textElements, "Text"), roi, shape);
      }
      else {
        store.setPolylineID(shapeID, roi, shape);
        store.setPolylinePoints(
          getFirstNodeValue(geometry, "Points"), roi, shape);
        store.setPolylineText(
          getFirstNodeValue(textElements, "Text"), roi, shape);
      }
    }
    return shape;
  }

  private int populateLines(NodeList lines, int roi, int shape) {
   for (int s=0; s<lines.getLength(); s++, shape++) {
      Element line = (Element) lines.item(s);

      Element geometry = getFirstNode(line, "Geometry");
      Element textElements = getFirstNode(line, "TextElements");
      Element attributes = getFirstNode(line, "Attributes");

      String x1 = getFirstNodeValue(geometry, "X1");
      String x2 = getFirstNodeValue(geometry, "X2");
      String y1 = getFirstNodeValue(geometry, "Y1");
      String y2 = getFirstNodeValue(geometry, "Y2");

      store.setLineID(
        MetadataTools.createLSID("Shape", roi, shape), roi, shape);

      if (x1 != null) {
        store.setLineX1(new Double(x1), roi, shape);
      }
      if (x2 != null) {
        store.setLineX2(new Double(x2), roi, shape);
      }
      if (y1 != null) {
        store.setLineY1(new Double(y1), roi, shape);
      }
      if (y2 != null) {
        store.setLineY2(new Double(y2), roi, shape);
      }
      store.setLineText(getFirstNodeValue(textElements, "Text"), roi, shape);
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
      String radius = getFirstNodeValue(geometry, "Radius");
      String centerX = getFirstNodeValue(geometry, "CenterX");
      String centerY = getFirstNodeValue(geometry, "CenterY");

      if (radius != null) {
        store.setEllipseRadiusX(new Double(radius), roi, shape);
        store.setEllipseRadiusY(new Double(radius), roi, shape);
      }
      if (centerX != null) {
        store.setEllipseX(new Double(centerX), roi, shape);
      }
      if (centerY != null) {
        store.setEllipseY(new Double(centerY), roi, shape);
      }
      store.setEllipseText(getFirstNodeValue(textElements, "Text"), roi, shape);
    }
    return shape;
  }

  private void translateExperiment(Element root) throws FormatException {
    NodeList experiments = root.getElementsByTagName("Experiment");
    if (experiments == null || experiments.getLength() == 0) {
      return;
    }

    Element experimentBlock =
      getFirstNode((Element) experiments.item(0), "ExperimentBlocks");
    Element acquisition = getFirstNode(experimentBlock, "AcquisitionBlock");
    Element tilesSetup = getFirstNode(acquisition, "TilesSetup");
    NodeList groups = getGrandchildren(tilesSetup, "PositionGroup");

    positionsX = new Length[core.size()];
    positionsY = new Length[core.size()];
    positionsZ = new Length[core.size()];

    if (groups != null) {
      for (int i=0; i<groups.getLength(); i++) {
        Element group = (Element) groups.item(i);

        int tilesX = Integer.parseInt(getFirstNodeValue(group, "TilesX"));
        int tilesY = Integer.parseInt(getFirstNodeValue(group, "TilesY"));

        Element position = getFirstNode(group, "Position");

        String x = position.getAttribute("X");
        String y = position.getAttribute("Y");
        String z = position.getAttribute("Z");

        Length xPos = null;
        try {
          xPos = new Length(Double.valueOf(x), UNITS.REFERENCEFRAME);
        }
        catch (NumberFormatException e) { }
        Length yPos = null;
        try {
          yPos = new Length(Double.valueOf(y), UNITS.REFERENCEFRAME);
        }
        catch (NumberFormatException e) { }
        Length zPos = null;
        try {
          zPos = new Length(Double.valueOf(z), UNITS.REFERENCEFRAME);
        }
        catch (NumberFormatException e) { }

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
    else {
      Element regionsSetup = getFirstNode(acquisition, "RegionsSetup");

      if (regionsSetup != null) {
        Element sampleHolder = getFirstNode(regionsSetup, "SampleHolder");
        if (sampleHolder != null) {
          NodeList regions = getGrandchildren(sampleHolder,
            "SingleTileRegionArray", "SingleTileRegion");
          if (regions != null) {
            for (int i=0; i<regions.getLength(); i++) {
              Element region = (Element) regions.item(i);

              String x = getFirstNode(region, "X").getTextContent();
              String y = getFirstNode(region, "Y").getTextContent();
              String z = getFirstNode(region, "Z").getTextContent();

              // safe to assume all 3 arrays have the same length
              if (i < positionsX.length) {
                if (x == null) {
                  positionsX[i] = null;
                } else {
                  final Double number = Double.valueOf(x);
                  positionsX[i] = new Length(number, UNITS.REFERENCEFRAME);
                }
                if (y == null) {
                  positionsY[i] = null;
                } else {
                  final Double number = Double.valueOf(y);
                  positionsY[i] = new Length(number, UNITS.REFERENCEFRAME);
                }
                if (z == null) {
                  positionsZ[i] = null;
                } else {
                  final Double number = Double.valueOf(z);
                  positionsZ[i] = new Length(number, UNITS.REFERENCEFRAME);
                }
              }
            }
          }
        }
      }


    }

    NodeList detectors = getGrandchildren(acquisition, "Detector");

    Element setup = getFirstNode(acquisition, "AcquisitionModeSetup");
    String cameraModel = getFirstNodeValue(setup, "SelectedCamera");

    if (detectors != null) {
      for (int i=0; i<detectors.getLength(); i++) {
        Element detector = (Element) detectors.item(i);
        String id = MetadataTools.createLSID("Detector", 0, i);

        store.setDetectorID(id, 0, i);
        String model = detector.getAttribute("Id");
        store.setDetectorModel(model, 0, i);

        String bin = getFirstNodeValue(detector, "Binning");
        if (bin != null) {
          bin = bin.replaceAll(",", "x");
          Binning binning = getBinning(bin);

          if (model != null && model.equals(cameraModel)) {
            for (int image=0; image<getSeriesCount(); image++) {
              for (int c=0; c<getEffectiveSizeC(); c++) {
                store.setDetectorSettingsID(id, image, c);
                store.setDetectorSettingsBinning(binning, image, c);
              }
            }
            hasDetectorSettings = true;
          }
        }
      }
    }

    Element multiTrack = getFirstNode(acquisition, "MultiTrackSetup");

    if (multiTrack == null) {
      return;
    }

    detectors = getGrandchildren(multiTrack, "Detector");

    if (detectors != null && detectors.getLength() > 0) {
      Element detector = (Element) detectors.item(0);
      gain = getFirstNodeValue(detector, "Voltage");
    }

    NodeList tracks = multiTrack.getElementsByTagName("Track");

    if (tracks != null && tracks.getLength() > 0) {
      for (int i=0; i<tracks.getLength(); i++) {
        Element track = (Element) tracks.item(i);
        Element channel = getFirstNode(track, "Channel");
        String exposure = getFirstNodeValue(channel, "ExposureTime");
        String gain = getFirstNodeValue(channel, "EMGain");

        while (channels.size() <= i) {
          channels.add(new Channel());
        }

        try {
          if (exposure != null) {
            channels.get(i).exposure = new Double(exposure);
          }
        }
        catch (NumberFormatException e) {
          LOGGER.debug("Could not parse exposure time", e);
        }
        try {
          if (gain != null) {
            channels.get(i).gain = new Double(gain);
          }
        }
        catch (NumberFormatException e) {
          LOGGER.debug("Could not parse gain", e);
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
    if (children != null && children.getLength() > 0) {
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
    if (nodes != null && nodes.getLength() > 0) {
      return nodes.item(0).getTextContent();
    }
    return null;
  }

  private void populateOriginalMetadata(Element root, Stack<String> nameStack) {
    String name = root.getNodeName();
    if (name.equals("DisplaySetting")) {
      return;
    }
    nameStack.push(name);

    StringBuffer key = new StringBuffer();
    for (String k : nameStack) {
      if (!k.equals("Metadata") && (!k.endsWith("s") || k.equals(name))) {
        key.append(k);
        key.append("|");
      }
    }

    if (root.getChildNodes().getLength() == 1) {
      String value = root.getTextContent();
      if (value != null && key.length() > 0) {
        String s = key.toString();
        if (s.endsWith("|")){
          s = s.substring(0, s.length() - 1);
        }
        addGlobalMetaList(s, value);

        if (key.toString().endsWith("|Rotations|")) {
          rotationLabels = value.split(" ");
        }
        else if (key.toString().endsWith("|Phases|")) {
          phaseLabels = value.split(" ");
        }
        else if (key.toString().endsWith("|Illuminations|")) {
          illuminationLabels = value.split(" ");
        }
      }
    }
    NamedNodeMap attributes = root.getAttributes();
    for (int i=0; i<attributes.getLength(); i++) {
      Node attr = attributes.item(i);

      String attrName = attr.getNodeName();
      String attrValue = attr.getNodeValue();
      
      String keyString = key.toString();
      if (attrName.endsWith("|")){
        attrName = attrName.substring(0, attrName.length() - 1);
      }
      else if(attrName.length() == 0 && keyString.endsWith("|")) {
        keyString = keyString.substring(0, keyString.length() - 1);
      }

      addGlobalMetaList(keyString + attrName, attrValue);
    }

    NodeList children = root.getChildNodes();
    if (children != null) {
      for (int i=0; i<children.getLength(); i++) {
        Object child = children.item(i);
        if (child instanceof Element) {
          populateOriginalMetadata((Element) child, nameStack);
        }
      }
    }

    nameStack.pop();
  }

  private Segment readSegment(String filename) throws IOException {
    // align the stream to a multiple of 32 bytes
    int skip =
      (ALIGNMENT - (int) (in.getFilePointer() % ALIGNMENT)) % ALIGNMENT;
    in.skipBytes(skip);
    long startingPosition = in.getFilePointer();

    // instantiate a Segment subclass based upon the segment ID
    String segmentID = in.readString(16).trim();
    Segment segment = null;
    boolean skipData = false;

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
    else if (segmentID.equals("ZISRAWDIRECTORY")) {
      segment = new Directory();
    }
    else if (segmentID.equals("ZISRAWATTDIR")) {
      segment = new AttachmentDirectory();
    }
    else if (segmentID.equals("DELETED")) {
      segment = new Segment();
    }
    else if (segmentID.length() == 0) {
      segment = new Segment();
      skipData = true;
    }
    else {
      LOGGER.info("Unknown segment type: {}", segmentID);
      segment = new Segment();
    }
    segment.startingPosition = startingPosition;
    segment.id = segmentID;
    segment.filename = filename;
    segment.stream = in;

    if (!(segment instanceof Metadata)) {
      segment.fillInData();
    }
    else {
      ((Metadata) segment).skipData(); 
    }

    long pos = segment.startingPosition + segment.allocatedSize + HEADER_SIZE;
    if (pos < in.length()) {
      in.seek(pos);
    }
    else {
      in.seek(in.length());
    }

    if (skipData) {
      segment.close();
      return null;
    }
    return segment;
  }

  private void convertPixelType(int pixelType) throws FormatException {
    CoreMetadata ms0 = core.get(0);
    switch (pixelType) {
      case GRAY8:
        ms0.pixelType = FormatTools.UINT8;
        break;
      case GRAY16:
        ms0.pixelType = FormatTools.UINT16;
        break;
      case GRAY32:
        ms0.pixelType = FormatTools.UINT32;
        break;
      case GRAY_FLOAT:
        ms0.pixelType = FormatTools.FLOAT;
        break;
      case GRAY_DOUBLE:
        ms0.pixelType = FormatTools.DOUBLE;
        break;
      case BGR_24:
        ms0.pixelType = FormatTools.UINT8;
        ms0.sizeC *= 3;
        ms0.rgb = true;
        ms0.interleaved = true;
        break;
      case BGR_48:
        ms0.pixelType = FormatTools.UINT16;
        ms0.sizeC *= 3;
        ms0.rgb = true;
        ms0.interleaved = true;
        break;
      case BGRA_8:
        ms0.pixelType = FormatTools.UINT8;
        ms0.sizeC *= 4;
        ms0.rgb = true;
        ms0.interleaved = true;
        break;
      case BGR_FLOAT:
        ms0.pixelType = FormatTools.FLOAT;
        ms0.sizeC *= 3;
        ms0.rgb = true;
        ms0.interleaved = true;
        break;
      case COMPLEX:
      case COMPLEX_FLOAT:
        throw new FormatException("Sorry, complex pixel data not supported.");
      default:
        throw new FormatException("Unknown pixel type: " + pixelType);
    }
    ms0.interleaved = ms0.rgb;
  }

  private void parseObjectives(NodeList objectives) throws FormatException {
    if (objectives != null) {
      for (int i=0; i<objectives.getLength(); i++) {
        Element objective = (Element) objectives.item(i);
        Element manufacturerNode = getFirstNode(objective, "Manufacturer");

        String manufacturer =
          getFirstNodeValue(manufacturerNode, "Manufacturer");
        String model = getFirstNodeValue(manufacturerNode, "Model");
        String serialNumber =
          getFirstNodeValue(manufacturerNode, "SerialNumber");
        String lotNumber = getFirstNodeValue(manufacturerNode, "LotNumber");

        if (objectiveSettingsID == null) {
          objectiveSettingsID = objective.getAttribute("Id");
        }
        store.setObjectiveID(objective.getAttribute("Id"), 0, i);
        store.setObjectiveManufacturer(manufacturer, 0, i);
        store.setObjectiveModel(model, 0, i);
        store.setObjectiveSerialNumber(serialNumber, 0, i);
        store.setObjectiveLotNumber(lotNumber, 0, i);

        String correction = getFirstNodeValue(objective, "Correction");
        if (correction != null) {
          store.setObjectiveCorrection(getCorrection(correction), 0, i);
        }
        store.setObjectiveImmersion(
          getImmersion(getFirstNodeValue(objective, "Immersion")), 0, i);

        String lensNA = getFirstNodeValue(objective, "LensNA");
        if (lensNA != null) {
          store.setObjectiveLensNA(new Double(lensNA), 0, i);
        }

        String magnification =
          getFirstNodeValue(objective, "NominalMagnification");
        if (magnification == null) {
          magnification = getFirstNodeValue(objective, "Magnification");
        }
        Double mag = magnification == null ? null : new Double(magnification);

        if (mag != null) {
          store.setObjectiveNominalMagnification(mag, 0, i);
        }
        String calibratedMag =
          getFirstNodeValue(objective, "CalibratedMagnification");
        if (calibratedMag != null) {
          store.setObjectiveCalibratedMagnification(
            new Double(calibratedMag), 0, i);
        }
        String wd = getFirstNodeValue(objective, "WorkingDistance");
        if (wd != null) {
          store.setObjectiveWorkingDistance(new Length(new Double(wd), UNITS.MICROM), 0, i);
        }
        String iris = getFirstNodeValue(objective, "Iris");
        if (iris != null) {
          store.setObjectiveIris(new Boolean(iris), 0, i);
        }
      }
    }
  }

  // -- Helper classes --

  /** Top-level class that implements logic common to all types of Segment. */
  class Segment {
    public String filename;
    public long startingPosition;
    public String id;
    public long allocatedSize;
    public long usedSize;
    public RandomAccessInputStream stream;

    public Segment() {
      filename = null;
      startingPosition = 0;
      id = null;
      allocatedSize = 0;
      usedSize = 0;
      stream = null;
    }

    public Segment(Segment model) {
      super();
      this.filename = model.filename;
      this.startingPosition = model.startingPosition;
      this.id = model.id;
      this.allocatedSize = model.allocatedSize;
      this.usedSize = model.usedSize;
    }

    public void fillInData() throws IOException {
      RandomAccessInputStream s = getStream();
      try {
        s.order(isLittleEndian());
        s.seek(startingPosition + 16);
        // read the segment header
        allocatedSize = s.readLong();
        usedSize = s.readLong();

        if (usedSize == 0) {
          usedSize = allocatedSize;
        }
      }
      finally {
        if (stream == null) {
          s.close();
        }
      }
    }

    public void close() throws IOException {
      // whatever created the Segment is responsible for closing the stream
      // we just need to remove the reference
      stream = null;
    }

    public RandomAccessInputStream getStream() throws IOException {
      if (stream != null) {
        return stream;
      }
      return new RandomAccessInputStream(filename, BUFFER_SIZE);
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

    @Override
    public void fillInData() throws IOException {
      super.fillInData();

      RandomAccessInputStream s = getStream();
      try {
        s.order(isLittleEndian());
        s.seek(startingPosition + HEADER_SIZE);
        majorVersion = s.readInt();
        minorVersion = s.readInt();
        s.skipBytes(4); // reserved 1
        s.skipBytes(4); // reserved 2
        primaryFileGUID = s.readLong(); // 16
        fileGUID = s.readLong(); // 16
        filePart = s.readInt();

        directoryPosition = s.readLong();
        metadataPosition = s.readLong();
        updatePending = s.readInt() != 0;
        attachmentDirectoryPosition = s.readLong();
      }
      finally {
        if (stream == null) {
          s.close();
        }
      }
    }
  }

  /** Segment with ID "ZISRAWMETADATA". */
  class Metadata extends Segment {
    public String xml;
    public byte[] attachment;

    public void skipData() throws IOException {
      super.fillInData();
    }

    @Override
    public void fillInData() throws IOException {
      super.fillInData();

      RandomAccessInputStream s = getStream();
      try {
        s.order(true);
        s.seek(startingPosition + HEADER_SIZE);
        int xmlSize = s.readInt();
        int attachmentSize = s.readInt();

        s.skipBytes(248);

        xml = s.readString(xmlSize);
        attachment = new byte[attachmentSize];
        s.read(attachment);
      }
      finally {
        if (stream == null) {
          s.close();
        }
      }
    }

    public void clearXML() {
      xml = null;
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

    private Length stageX, stageY, stageZ;
    private Double timestamp, exposureTime;

    public int x, y;
    public int row, col;

    public SubBlock() {
      super();
    }

    public SubBlock(SubBlock model) {
      super(model);
      this.metadataSize = model.metadataSize;
      this.attachmentSize = model.attachmentSize;
      this.dataSize = model.dataSize;
      this.directoryEntry = model.directoryEntry;
      this.metadata = model.metadata;
      this.seriesIndex = model.seriesIndex;
      this.planeIndex = model.planeIndex;
      this.dataOffset = model.dataOffset;
      this.stageX = model.stageX;
      this.stageY = model.stageY;
      this.timestamp = model.timestamp;
      this.exposureTime = model.exposureTime;
      this.stageZ = model.stageZ;
      this.x = model.x;
      this.y = model.y;
    }

    @Override
    public void fillInData() throws IOException {
      super.fillInData();

      RandomAccessInputStream s = getStream();
      try {
        s.order(isLittleEndian());
        s.seek(startingPosition + HEADER_SIZE);
        long fp = s.getFilePointer();
        metadataSize = s.readInt();
        attachmentSize = s.readInt();
        dataSize = s.readLong();
        directoryEntry = new DirectoryEntry(s);
        s.skipBytes((int) Math.max(256 - (s.getFilePointer() - fp), 0));

        metadata = s.readString(metadataSize).trim();
        dataOffset = s.getFilePointer();

        if (s.getFilePointer() + dataSize + attachmentSize < s.length()) {
          s.seek(s.getFilePointer() + dataSize + attachmentSize);
          parseMetadata();
        }
      }
      finally {
        if (stream == null) {
          s.close();
        }
      }
    }

    // -- SubBlock API methods --

    public byte[] readPixelData() throws FormatException, IOException {
      RandomAccessInputStream s = new RandomAccessInputStream(filename);
      try {
        return readPixelData(s);
      } finally {
        s.close();
      }
    }

    public byte[] readPixelData(RandomAccessInputStream s) throws FormatException, IOException {
      byte[] data = new byte[(int) dataSize];
      s.order(isLittleEndian());
      s.seek(dataOffset);
        s.read(data);

      CodecOptions options = new CodecOptions();
      options.interleaved = isInterleaved();
      options.littleEndian = isLittleEndian();
      options.maxBytes = getSizeX() * getSizeY() * getRGBChannelCount() *
        FormatTools.getBytesPerPixel(getPixelType());

      switch (directoryEntry.compression) {
        case UNCOMPRESSED:
          break;
        case JPEG:
          data = new JPEGCodec().decompress(data, options);
          break;
        case LZW:
          data = new LZWCodec().decompress(data, options);
          break;
        case JPEGXR:
          throw new UnsupportedCompressionException(
            "JPEG-XR not yet supported");
        case 104: // camera-specific packed pixels
          data = decode12BitCamera(data, options.maxBytes);
          // reverse column ordering
          for (int row=0; row<getSizeY(); row++) {
            for (int col=0; col<getSizeX()/2; col++) {
              int left = row * getSizeX() * 2 + col * 2;
              int right = row * getSizeX() * 2 + (getSizeX() - col - 1) * 2;
              byte left1 = data[left];
              byte left2 = data[left + 1];
              data[left] = data[right];
              data[left + 1] = data[right + 1];
              data[right] = left1;
              data[right + 1] = left2;
            }
          }

          break;
        case 504: // camera-specific packed pixels
          data = decode12BitCamera(data, options.maxBytes);
          break;
      }
      return data;
    }

    // -- Helper methods --

    private void parseMetadata() throws IOException {
      if (metadata.length() <= 16) {
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
        metadata = null;
        return;
      }
      catch (SAXException e) {
        metadata = null;
        return;
      }

      if (root == null) {
        metadata = null;
        return;
      }

      NodeList children = root.getChildNodes();

      if (children == null) {
        metadata = null;
        return;
      }

      for (int i=0; i<children.getLength(); i++) {
        if (!(children.item(i) instanceof Element)) {
          continue;
        }
        Element child = (Element) children.item(i);

        if (child.getNodeName().equals("Tags")) {
          NodeList tags = child.getChildNodes();

          if (tags != null) {
            for (int tag=0; tag<tags.getLength(); tag++) {
              if (!(tags.item(tag) instanceof Element)) {
                continue;
              }
              Element tagNode = (Element) tags.item(tag);
              String text = tagNode.getTextContent();
              if (text != null) {
                if (tagNode.getNodeName().equals("StageXPosition")) {
                  final Double number = Double.valueOf(text);
                  stageX = new Length(number, UNITS.REFERENCEFRAME);
                }
                else if (tagNode.getNodeName().equals("StageYPosition")) {
                  final Double number = Double.valueOf(text);
                  stageY = new Length(number, UNITS.REFERENCEFRAME);
                }
                else if (tagNode.getNodeName().equals("FocusPosition")) {
                  final Double number = Double.valueOf(text);
                  stageZ = new Length(number, UNITS.REFERENCEFRAME);
                }
                else if (tagNode.getNodeName().equals("AcquisitionTime")) {
                  Timestamp t = Timestamp.valueOf(text);
                  if (t != null)
                    timestamp = t.asInstant().getMillis() / 1000d;
                }
                else if (tagNode.getNodeName().equals("ExposureTime")) {
                  exposureTime = new Double(text);
                }
              }
            }
          }
        }
      }
      metadata = null;
    }
  }

  private byte[] decode12BitCamera(byte[] data, int maxBytes) throws IOException {
    byte[] decoded = new byte[maxBytes];

    RandomAccessInputStream bb = new RandomAccessInputStream(
      new ByteArrayHandle(data));
    byte[] fourBits = new byte[(maxBytes / 2) * 3];
    int pt = 0;
    while (pt < fourBits.length) {
      fourBits[pt++] = (byte) bb.readBits(4);
    }
    bb.close();
    for (int index=0; index<fourBits.length-1; index++) {
      if ((index - 3) % 6 == 0) {
        byte middle = fourBits[index];
        byte last = fourBits[index + 1];
        byte first = fourBits[index - 1];
        fourBits[index + 1] = middle;
        fourBits[index] = first;
        fourBits[index - 1] = last;
      }
    }

    int currentByte = 0;
    for (int index=0; index<fourBits.length;) {
      if (index % 3 == 0) {
        decoded[currentByte++] = fourBits[index++];
      }
      else {
        decoded[currentByte++] =
          (byte) (fourBits[index++] << 4 | fourBits[index++]);
      }
    }

    if (isLittleEndian()) {
      core.get(0).littleEndian = false;
    }

    return decoded;
  }

  /** Segment with ID "ZISRAWDIRECTORY". */
  class Directory extends Segment {
    public DirectoryEntry[] entries;

    @Override
    public void fillInData() throws IOException {
      super.fillInData();

      RandomAccessInputStream s = getStream();
      try {
        s.order(isLittleEndian());
        s.seek(startingPosition + HEADER_SIZE);

        int entryCount = s.readInt();
        s.skipBytes(124);
        entries = new DirectoryEntry[entryCount];
        for (int i=0; i<entryCount; i++) {
          entries[i] = new DirectoryEntry(s);
        }
      }
      finally {
        if (stream == null) {
          s.close();
        }
      }
    }
  }

  /** Segment with ID "ZISRAWATTDIR". */
  class AttachmentDirectory extends Segment {
    public AttachmentEntry[] entries;

    @Override
    public void fillInData() throws IOException {
      super.fillInData();

      RandomAccessInputStream s = getStream();
      try {
        s.order(isLittleEndian());
        s.seek(startingPosition + HEADER_SIZE);

        int entryCount = s.readInt();
        s.skipBytes(252);
        entries = new AttachmentEntry[entryCount];
        for (int i=0; i<entryCount; i++) {
          entries[i] = new AttachmentEntry(s);
        }
      }
      finally {
        if (stream == null) {
          s.close();
        }
      }
    }
  }

  /** Segment with ID "ZISRAWATTACH". */
  class Attachment extends Segment {
    public int dataSize;
    public AttachmentEntry attachment;
    public byte[] attachmentData;

    @Override
    public void fillInData() throws IOException {
      super.fillInData();

      RandomAccessInputStream s = getStream();
      try {
        s.order(isLittleEndian());
        s.seek(startingPosition + HEADER_SIZE);
        dataSize = s.readInt();
        s.skipBytes(12); // reserved
        attachment = new AttachmentEntry(s);
        s.skipBytes(112); // reserved
        attachmentData = new byte[dataSize];
        s.read(attachmentData);
      }
      finally {
        if (stream == null) {
          s.close();
        }
      }
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

    public DirectoryEntry(RandomAccessInputStream s) throws IOException {
      schemaType = s.readString(2);
      pixelType = s.readInt();
      filePosition = s.readLong();
      filePart = s.readInt();
      compression = s.readInt();
      pyramidType = s.readByte();
      if (pyramidType == 1) {
        prestitched = false;
      }
      s.skipBytes(1); // reserved
      s.skipBytes(4); // reserved
      dimensionCount = s.readInt();

      dimensionEntries = new DimensionEntry[dimensionCount];
      for (int i=0; i<dimensionEntries.length; i++) {
        dimensionEntries[i] = new DimensionEntry(s);

        // invalid dimension found; ignore it and the previous one
        if (dimensionEntries[i].dimension.length() > 1) {
          dimensionEntries[i] = null;
          if (i > 0) {
            dimensionEntries[i - 1] = null;
          }
        }
      }
    }
  }

  static class DimensionEntry {
    public String dimension;
    public int start;
    public int size;
    public float startCoordinate;
    public int storedSize;

    public DimensionEntry(RandomAccessInputStream s) throws IOException {
      dimension = s.readString(4).trim();
      start = s.readInt();
      size = s.readInt();
      startCoordinate = s.readFloat();
      storedSize = s.readInt();
    }
  }

  static class AttachmentEntry {
    public String schemaType;
    public long filePosition;
    public int filePart;
    public String contentGUID;
    public String contentFileType;
    public String name;

    public AttachmentEntry(RandomAccessInputStream s) throws IOException {
      schemaType = s.readString(2);
      s.skipBytes(10); // reserved
      filePosition = s.readLong();
      filePart = s.readInt();
      contentGUID = s.readString(16);
      contentFileType = s.readString(8);
      name = s.readString(80);
    }
  }

  static class Channel {
    public String name;
    public String color;
    public IlluminationType illumination;
    public AcquisitionMode acquisitionMode;
    public String emission;
    public String excitation;
    public String pinhole;
    public Double exposure;
    public Double gain;
    public String fluor;
    public String filterSetRef;
  }

  static class Coordinate {
    public int series;
    public int plane;
    private int imageCount;

    public Coordinate(int series, int plane, int imageCount) {
      this.series = series;
      this.plane = plane;
      this.imageCount = imageCount;
    }

    @Override
    public boolean equals(Object o) {
      if (o == null || !(o instanceof Coordinate)) {
        return false;
      }
      return ((Coordinate) o).series == this.series &&
        ((Coordinate) o).plane == this.plane;
    }

    @Override
    public int hashCode() {
      return series * imageCount + plane;
    }

    @Override
    public String toString() {
      return "[series = " + series + ", plane = " + plane + "]";
    }
  }

}
