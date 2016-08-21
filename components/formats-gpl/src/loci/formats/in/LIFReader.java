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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import loci.common.Constants;
import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.common.xml.XMLTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.ImageTools;
import loci.formats.MetadataTools;
import loci.formats.meta.IMetadata;
import loci.formats.meta.MetadataStore;
import loci.formats.services.OMEXMLService;

import ome.xml.model.enums.DetectorType;
import ome.xml.model.enums.LaserMedium;
import ome.xml.model.enums.LaserType;
import ome.xml.model.primitives.Color;
import ome.xml.model.primitives.NonNegativeInteger;
import ome.xml.model.primitives.PercentFraction;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

import org.xml.sax.SAXException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * LIFReader is the file format reader for Leica LIF files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/LIFReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/LIFReader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class LIFReader extends FormatReader {

  // -- Constants --

  public static final byte LIF_MAGIC_BYTE = 0x70;
  public static final byte LIF_MEMORY_BYTE = 0x2a;

  private static final HashMap<String, Integer> CHANNEL_PRIORITIES =
    createChannelPriorities();

  private static HashMap<String, Integer> createChannelPriorities() {
    HashMap<String, Integer> h = new HashMap<String, Integer>();

    h.put("red", new Integer(0));
    h.put("green", new Integer(1));
    h.put("blue", new Integer(2));
    h.put("cyan", new Integer(3));
    h.put("magenta", new Integer(4));
    h.put("yellow", new Integer(5));
    h.put("black", new Integer(6));
    h.put("gray", new Integer(7));
    h.put("", new Integer(8));

    return h;
  }

  // -- Fields --

  /** Offsets to memory blocks, paired with their corresponding description. */
  private Vector<Long> offsets;

  private int[][] realChannel;
  private int lastChannel = 0;

  private Vector<String> lutNames = new Vector<String>();
  private Vector<Double> physicalSizeXs = new Vector<Double>();
  private Vector<Double> physicalSizeYs = new Vector<Double>();
  private Vector<Double> fieldPosX = new Vector<Double>();
  private Vector<Double> fieldPosY = new Vector<Double>();

  private String[] descriptions, microscopeModels, serialNumber;
  private Double[] pinholes, zooms, zSteps, tSteps, lensNA;
  private Double[][] expTimes, gains, detectorOffsets;
  private String[][] channelNames;
  private Vector[] detectorModels;
  private Integer[][] exWaves;
  private Vector[] activeDetector;
  private HashMap[] detectorIndexes;

  private String[] immersions, corrections, objectiveModels;
  private Double[] magnification;
  private Double[] posX, posY, posZ;
  private Double[] refractiveIndex;
  private Vector[] cutIns, cutOuts, filterModels;
  private double[][] timestamps;
  private Vector[] laserWavelength, laserIntensity;
  private ROI[][] imageROIs;
  private boolean alternateCenter = false;
  private String[] imageNames;
  private double[] acquiredDate;

  private int[] tileCount;
  private long endPointer;

  // -- Constructor --

  /** Constructs a new Leica LIF reader. */
  public LIFReader() {
    super("Leica Image File Format", "lif");
    suffixNecessary = false;
    domains = new String[] {FormatTools.LM_DOMAIN};
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    return getSizeY();
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 1;
    if (!FormatTools.validStream(stream, blockLen, true)) return false;
    return stream.read() == LIF_MAGIC_BYTE;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() {
    FormatTools.assertId(currentId, true, 1);
    if (getPixelType() != FormatTools.UINT8 || !isIndexed()) return null;

    if (lastChannel < 0 || lastChannel >= 9) {
      return null;
    }

    byte[][] lut = new byte[3][256];
    for (int i=0; i<256; i++) {
      switch (lastChannel) {
        case 0:
          // red
          lut[0][i] = (byte) (i & 0xff);
          break;
        case 1:
          // green
          lut[1][i] = (byte) (i & 0xff);
          break;
        case 2:
          // blue
          lut[2][i] = (byte) (i & 0xff);
          break;
        case 3:
          // cyan
          lut[1][i] = (byte) (i & 0xff);
          lut[2][i] = (byte) (i & 0xff);
          break;
        case 4:
          // magenta
          lut[0][i] = (byte) (i & 0xff);
          lut[2][i] = (byte) (i & 0xff);
          break;
        case 5:
          // yellow
          lut[0][i] = (byte) (i & 0xff);
          lut[1][i] = (byte) (i & 0xff);
          break;
        default:
          // gray
          lut[0][i] = (byte) (i & 0xff);
          lut[1][i] = (byte) (i & 0xff);
          lut[2][i] = (byte) (i & 0xff);
      }
    }
    return lut;
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  public short[][] get16BitLookupTable() {
    FormatTools.assertId(currentId, true, 1);
    if (getPixelType() != FormatTools.UINT16 || !isIndexed()) return null;

    if (lastChannel < 0 || lastChannel >= 9) {
      return null;
    }

    short[][] lut = new short[3][65536];
    for (int i=0; i<65536; i++) {
      switch (lastChannel) {
        case 0:
          // red
          lut[0][i] = (short) (i & 0xffff);
          break;
        case 1:
          // green
          lut[1][i] = (short) (i & 0xffff);
          break;
        case 2:
          // blue
          lut[2][i] = (short) (i & 0xffff);
          break;
        case 3:
          // cyan
          lut[1][i] = (short) (i & 0xffff);
          lut[2][i] = (short) (i & 0xffff);
          break;
        case 4:
          // magenta
          lut[0][i] = (short) (i & 0xffff);
          lut[2][i] = (short) (i & 0xffff);
          break;
        case 5:
          // yellow
          lut[0][i] = (short) (i & 0xffff);
          lut[1][i] = (short) (i & 0xffff);
          break;
        default:
          // gray
          lut[0][i] = (short) (i & 0xffff);
          lut[1][i] = (short) (i & 0xffff);
          lut[2][i] = (short) (i & 0xffff);
      }
    }
    return lut;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    if (!isRGB()) {
      int[] pos = getZCTCoords(no);
      lastChannel = realChannel[getTileIndex(series)][pos[1]];
    }

    int index = getTileIndex(series);
    if (index >= offsets.size()) {
      // truncated file; imitate LAS AF and return black planes
      Arrays.fill(buf, (byte) 0);
      return buf;
    }

    long offset = offsets.get(index).longValue();
    int bytes = FormatTools.getBytesPerPixel(getPixelType());
    int bpp = bytes * getRGBChannelCount();

    long planeSize = (long) getSizeX() * getSizeY() * bpp;
    long nextOffset = index + 1 < offsets.size() ?
      offsets.get(index + 1).longValue() : endPointer;
    int bytesToSkip = (int) (nextOffset - offset - planeSize * getImageCount());
    bytesToSkip /= getSizeY();
    if ((getSizeX() % 4) == 0) bytesToSkip = 0;

    if (offset + (planeSize + bytesToSkip * getSizeY()) * no >= in.length()) {
      // truncated file; imitate LAS AF and return black planes
      Arrays.fill(buf, (byte) 0);
      return buf;
    }

    in.seek(offset + planeSize * no);

    int tile = series;
    for (int i=0; i<index; i++) {
      tile -= tileCount[i];
    }

    in.skipBytes((int) (tile * planeSize * getImageCount()));
    in.skipBytes(bytesToSkip * getSizeY() * no);

    if (bytesToSkip == 0) {
      readPlane(in, x, y, w, h, buf);
    }
    else {
      in.skipBytes(y * (getSizeX() * bpp + bytesToSkip));
      for (int row=0; row<h; row++) {
        in.skipBytes(x * bpp);
        in.read(buf, row * w * bpp, w * bpp);
        in.skipBytes(bpp * (getSizeX() - w - x) + bytesToSkip);
      }
    }

    // color planes are stored in BGR order
    if (getRGBChannelCount() == 3) {
      ImageTools.bgrToRgb(buf, isInterleaved(), bytes, getRGBChannelCount());
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      offsets = null;
      realChannel = null;
      lastChannel = 0;
      lutNames.clear();
      physicalSizeXs.clear();
      physicalSizeYs.clear();
      descriptions = microscopeModels = serialNumber = null;
      pinholes = zooms = lensNA = null;
      zSteps = tSteps = null;
      expTimes = gains = null;
      detectorOffsets = null;
      channelNames = null;
      detectorModels = null;
      exWaves = null;
      activeDetector = null;
      immersions = corrections = null;
      magnification = null;
      objectiveModels = null;
      posX = posY = posZ = null;
      refractiveIndex = null;
      cutIns = cutOuts = filterModels = null;
      timestamps = null;
      laserWavelength = laserIntensity = null;
      imageROIs = null;
      alternateCenter = false;
      imageNames = null;
      acquiredDate = null;
      detectorIndexes = null;
      tileCount = null;
      fieldPosX.clear();
      fieldPosY.clear();
      endPointer = 0;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    offsets = new Vector<Long>();

    in.order(true);

    // read the header

    LOGGER.info("Reading header");

    byte checkOne = in.readByte();
    in.skipBytes(2);
    byte checkTwo = in.readByte();
    if (checkOne != LIF_MAGIC_BYTE && checkTwo != LIF_MAGIC_BYTE) {
      throw new FormatException(id + " is not a valid Leica LIF file");
    }

    in.skipBytes(4);

    // read and parse the XML description

    if (in.read() != LIF_MEMORY_BYTE) {
      throw new FormatException("Invalid XML description");
    }

    // number of Unicode characters in the XML block
    int nc = in.readInt();
    String xml = DataTools.stripString(in.readString(nc * 2));

    LOGGER.info("Finding image offsets");

    while (in.getFilePointer() < in.length()) {
      LOGGER.debug("Looking for a block at {}; {} blocks read",
        in.getFilePointer(), offsets.size());
      int check = in.readInt();
      if (check != LIF_MAGIC_BYTE) {
        if (check == 0 && offsets.size() > 0) {
          // newer .lif file; the remainder of the file is all 0s
          endPointer = in.getFilePointer();
          break;
        }
        throw new FormatException("Invalid Memory Block: found magic bytes " +
          check + ", expected " + LIF_MAGIC_BYTE);
      }

      in.skipBytes(4);
      check = in.read();
      if (check != LIF_MEMORY_BYTE) {
        throw new FormatException("Invalid Memory Description: found magic " +
          "byte " + check + ", expected " + LIF_MEMORY_BYTE);
      }

      long blockLength = in.readInt();
      if (in.read() != LIF_MEMORY_BYTE) {
        in.seek(in.getFilePointer() - 5);
        blockLength = in.readLong();
        check = in.read();
        if (check != LIF_MEMORY_BYTE) {
          throw new FormatException("Invalid Memory Description: found magic " +
            "byte " + check + ", expected " + LIF_MEMORY_BYTE);
        }
      }

      int descrLength = in.readInt() * 2;

      if (blockLength > 0) {
        offsets.add(new Long(in.getFilePointer() + descrLength));
      }

      in.seek(in.getFilePointer() + descrLength + blockLength);
    }
    initMetadata(xml);
    xml = null;

    if (endPointer == 0) {
      endPointer = in.length();
    }

    // correct offsets, if necessary
    if (offsets.size() > getSeriesCount()) {
      Long[] storedOffsets = offsets.toArray(new Long[offsets.size()]);
      offsets.clear();
      int index = 0;
      for (int i=0; i<getSeriesCount(); i++) {
        setSeries(i);
        long nBytes = (long) FormatTools.getPlaneSize(this) * getImageCount();
        long start = storedOffsets[index];
        long end = index == storedOffsets.length - 1 ? in.length() :
          storedOffsets[index + 1];
        while (end - start < nBytes && ((end - start) / nBytes) != 1) {
          index++;
          start = storedOffsets[index];
          end = index == storedOffsets.length - 1 ? in.length() :
            storedOffsets[index + 1];
        }
        offsets.add(storedOffsets[index]);
        index++;
      }
      setSeries(0);
    }
  }

  // -- Helper methods --

  /** Parses a string of XML and puts the values in a Hashtable. */
  private void initMetadata(String xml) throws FormatException, IOException {
    IMetadata omexml = null;
    try {
      ServiceFactory factory = new ServiceFactory();
      OMEXMLService service = factory.getInstance(OMEXMLService.class);
      omexml = service.createOMEXMLMetadata();
    }
    catch (DependencyException exc) {
      throw new FormatException("Could not create OME-XML store.", exc);
    }
    catch (ServiceException exc) {
      throw new FormatException("Could not create OME-XML store.", exc);
    }
    MetadataStore store = makeFilterMetadata();
    MetadataLevel level = getMetadataOptions().getMetadataLevel();

    // the XML blocks stored in a LIF file are invalid,
    // because they don't have a root node

    xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><LEICA>" + xml +
      "</LEICA>";

    xml = XMLTools.sanitizeXML(xml);

    translateMetadata(getMetadataRoot(xml));

    for (int i=0; i<imageNames.length; i++) {
      setSeries(i);
      addSeriesMeta("Image name", imageNames[i]);
    }
    setSeries(0);

    // set up mapping to rearrange channels
    // for instance, the green channel may be #0, and the red channel may be #1
    realChannel = new int[tileCount.length][];
    int nextLut = 0;

    for (int i=0; i<core.size(); i++) {
      int index = getTileIndex(i);
      if (realChannel[index] != null) {
        continue;
      }
      CoreMetadata ms = core.get(i);
      realChannel[index] = new int[ms.sizeC];

      for (int q=0; q<ms.sizeC; q++) {
        String lut = "";
        if (nextLut < lutNames.size()) {
          lut = lutNames.get(nextLut++).toLowerCase();
        }
        if (!CHANNEL_PRIORITIES.containsKey(lut)) lut = "";
        realChannel[index][q] = CHANNEL_PRIORITIES.get(lut).intValue();
      }

      int[] sorted = new int[ms.sizeC];
      Arrays.fill(sorted, -1);

      for (int q=0; q<sorted.length; q++) {
        int min = Integer.MAX_VALUE;
        int minIndex = -1;
        for (int n=0; n<ms.sizeC; n++) {
          if (realChannel[index][n] < min &&
            !DataTools.containsValue(sorted, n))
          {
            min = realChannel[index][n];
            minIndex = n;
          }
        }

        sorted[q] = minIndex;
      }
    }

    MetadataTools.populatePixels(store, this, true, false);

    int roiCount = 0;
    for (int i=0; i<getSeriesCount(); i++) {
      setSeries(i);

      String instrumentID = MetadataTools.createLSID("Instrument", i);
      store.setInstrumentID(instrumentID, i);

      int index = getTileIndex(i);

      store.setMicroscopeModel(microscopeModels[index], i);
      store.setMicroscopeType(getMicroscopeType("Other"), i);

      String objectiveID = MetadataTools.createLSID("Objective", i, 0);
      store.setObjectiveID(objectiveID, i, 0);
      store.setObjectiveLensNA(lensNA[index], i, 0);
      store.setObjectiveSerialNumber(serialNumber[index], i, 0);
      if (magnification[index] != null) {
        store.setObjectiveNominalMagnification(magnification[index], i, 0);
      }
      store.setObjectiveImmersion(getImmersion(immersions[index]), i, 0);
      store.setObjectiveCorrection(getCorrection(corrections[index]), i, 0);
      store.setObjectiveModel(objectiveModels[index], i, 0);

      if (cutIns[index] != null && filterModels[index] != null) {
        int channel = 0;
        if (cutIns[index].size() >= filterModels[index].size() * 2) {
          int diff = cutIns[index].size() - filterModels[index].size();
          for (int q=0; q<diff; q++) {
            cutIns[index].remove(filterModels[index].size());
          }
        }
        for (int filter=0; filter<cutIns[index].size(); filter++) {
          String filterID = MetadataTools.createLSID("Filter", i, filter);
          store.setFilterID(filterID, i, filter);
          if (filterModels[index] != null &&
            filter < filterModels[index].size())
          {
            store.setFilterModel(
              (String) filterModels[index].get(filter), i, filter);
          }
          store.setTransmittanceRangeCutIn(
            (PositiveInteger) cutIns[index].get(filter), i, filter);
          store.setTransmittanceRangeCutOut(
            (PositiveInteger) cutOuts[index].get(filter), i, filter);
        }
      }

      Vector lasers = laserWavelength[index];
      Vector laserIntensities = laserIntensity[index];
      int nextChannel = 0;

      if (lasers != null) {
        int laserIndex = 0;
        while (laserIndex < lasers.size()) {
          if ((Integer) lasers.get(laserIndex) == 0) {
            lasers.removeElementAt(laserIndex);
          }
          else {
            laserIndex++;
          }
        }

        for (int laser=0; laser<lasers.size(); laser++) {
          String id = MetadataTools.createLSID("LightSource", i, laser);
          store.setLaserID(id, i, laser);
          store.setLaserType(LaserType.OTHER, i, laser);
          store.setLaserLaserMedium(LaserMedium.OTHER, i, laser);
          Integer wavelength = (Integer) lasers.get(laser);
          PositiveInteger wave = FormatTools.getWavelength(wavelength);
          if (wave != null) {
            store.setLaserWavelength(wave, i, laser);
          }
        }

        Vector<Integer> validIntensities = new Vector<Integer>();
        for (int laser=0; laser<laserIntensities.size(); laser++) {
          double intensity = (Double) laserIntensities.get(laser);
          if (intensity < 100) {
            validIntensities.add(laser);
          }
        }

        if (validIntensities.size() == getEffectiveSizeC() + 1) {
          validIntensities.remove(1);
        }

        int start = validIntensities.size() - getEffectiveSizeC();
        if (start < 0) {
          start = 0;
        }

        boolean noNames = true;
        if (channelNames[index] != null) {
          for (String name : channelNames[index]) {
            if (name != null && !name.equals("")) {
              noNames = false;
              break;
            }
          }
        }

        int nextFilter = 0;
        //int nextFilter = cutIns[i].size() - getEffectiveSizeC();
        for (int k=start; k<validIntensities.size(); k++, nextChannel++) {
          int laserArrayIndex = validIntensities.get(k);
          double intensity = (Double) laserIntensities.get(laserArrayIndex);
          int laser = laserArrayIndex % lasers.size();
          Integer wavelength = (Integer) lasers.get(laser);
          if (wavelength != 0) {
            while (channelNames != null && nextChannel < getEffectiveSizeC() &&
              channelNames[index] != null &&
              ((channelNames[index][nextChannel] == null ||
              channelNames[index][nextChannel].equals("")) && !noNames))
            {
              nextChannel++;
            }
            if (nextChannel < getEffectiveSizeC()) {
              String id = MetadataTools.createLSID("LightSource", i, laser);
              store.setChannelLightSourceSettingsID(id, i, nextChannel);
              store.setChannelLightSourceSettingsAttenuation(
                new PercentFraction((float) intensity / 100f), i, nextChannel);

              PositiveInteger ex =
                FormatTools.getExcitationWavelength(wavelength);
              if (ex != null) {
                store.setChannelExcitationWavelength(ex, i, nextChannel);
              }

              if (wavelength > 0) {
                if (cutIns[index] == null || nextFilter >= cutIns[index].size())
                {
                  continue;
                }
                Integer cutIn =
                  ((PositiveInteger) cutIns[index].get(nextFilter)).getValue();
                while (cutIn - wavelength > 20) {
                  nextFilter++;
                  if (nextFilter < cutIns[index].size()) {
                    cutIn = ((PositiveInteger)
                      cutIns[index].get(nextFilter)).getValue();
                  }
                  else {
                    break;
                  }
                }
                if (nextFilter < cutIns[index].size()) {
                  String fid =
                    MetadataTools.createLSID("Filter", i, nextFilter);
                  //store.setLightPathEmissionFilterRef(fid, i, nextChannel, 0);
                  nextFilter++;
                }
              }
            }
          }
        }
      }

      store.setImageInstrumentRef(instrumentID, i);
      store.setObjectiveSettingsID(objectiveID, i);
      store.setObjectiveSettingsRefractiveIndex(refractiveIndex[index], i);

      store.setImageDescription(descriptions[index], i);
      if (acquiredDate[index] > 0) {
        store.setImageAcquisitionDate(new Timestamp(DateTools.convertDate(
          (long) (acquiredDate[index] * 1000), DateTools.COBOL,
          DateTools.ISO8601_FORMAT, true)), i);
      }
      store.setImageName(imageNames[index].trim(), i);

      PositiveFloat sizeX =
        FormatTools.getPhysicalSizeX(physicalSizeXs.get(index));
      PositiveFloat sizeY =
        FormatTools.getPhysicalSizeY(physicalSizeYs.get(index));
      PositiveFloat sizeZ = FormatTools.getPhysicalSizeZ(zSteps[index]);

      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, i);
      }
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, i);
      }
      if (sizeZ != null) {
        store.setPixelsPhysicalSizeZ(sizeZ, i);
      }
      store.setPixelsTimeIncrement(tSteps[index], i);

      Vector detectors = detectorModels[index];
      if (detectors != null) {
        nextChannel = 0;
        int start = detectors.size() - getEffectiveSizeC();
        if (start < 0) {
          start = 0;
        }
        for (int detector=start; detector<detectors.size(); detector++) {
          int dIndex = detector - start;
          String detectorID = MetadataTools.createLSID("Detector", i, dIndex);
          store.setDetectorID(detectorID, i, dIndex);
          store.setDetectorModel((String) detectors.get(detector), i, dIndex);

          store.setDetectorZoom(zooms[index], i, dIndex);
          store.setDetectorType(DetectorType.PMT, i, dIndex);

          if (activeDetector[index] != null) {
            int detectorIndex =
              activeDetector[index].size() - getEffectiveSizeC() + dIndex;
            if (detectorIndex >= 0 &&
              detectorIndex < activeDetector[index].size() &&
              (Boolean) activeDetector[index].get(detectorIndex) &&
              detectorOffsets[index] != null &&
              nextChannel < detectorOffsets[index].length)
            {
              store.setDetectorOffset(
                detectorOffsets[index][nextChannel++], i, dIndex);
            }
          }
        }
      }

      Vector activeDetectors = activeDetector[index];
      int firstDetector = activeDetectors == null ? 0 :
        activeDetectors.size() - getEffectiveSizeC();
      int nextDetector = firstDetector;

      int nextFilter = 0;
      int nextFilterDetector = 0;

      if (activeDetectors != null &&
        activeDetectors.size() > cutIns[index].size() &&
        (Boolean) activeDetectors.get(activeDetectors.size() - 1) &&
        (Boolean) activeDetectors.get(activeDetectors.size() - 2))
      {
        nextFilterDetector = activeDetectors.size() - cutIns[index].size();

        if (cutIns[index].size() > filterModels[index].size()) {
          nextFilterDetector += filterModels[index].size();
          nextFilter += filterModels[index].size();
        }
      }

      for (int c=0; c<getEffectiveSizeC(); c++) {
        if (activeDetectors != null) {
          while (nextDetector >= 0 && nextDetector < activeDetectors.size() &&
            !(Boolean) activeDetectors.get(nextDetector))
          {
            nextDetector++;
          }
          if (nextDetector < activeDetectors.size() && detectors != null &&
            nextDetector - firstDetector < detectors.size())
          {
            String detectorID = MetadataTools.createLSID(
              "Detector", i, nextDetector - firstDetector);
            store.setDetectorSettingsID(detectorID, i, c);
            nextDetector++;

            if (detectorOffsets[index] != null &&
              c < detectorOffsets[index].length)
            {
              store.setDetectorSettingsOffset(detectorOffsets[index][c], i, c);
            }

            if (gains[index] != null) {
              store.setDetectorSettingsGain(gains[index][c], i, c);
            }
          }
        }

        if (channelNames[index] != null) {
          store.setChannelName(channelNames[index][c], i, c);
        }
        store.setChannelPinholeSize(pinholes[index], i, c);
        if (exWaves[index] != null) {
          if (exWaves[index][c] != null && exWaves[index][c] > 1) {
            PositiveInteger ex =
              FormatTools.getExcitationWavelength(exWaves[index][c]);
            if (ex != null) {
              store.setChannelExcitationWavelength(ex, i, c);
            }
          }
        }

        // channel coloring is implicit if the image is stored as RGB
        Color channelColor = getChannelColor(realChannel[index][c]);
        if (!isRGB()) {
          store.setChannelColor(channelColor, i, c);
        }

        if (channelColor.getValue() != -1 && nextFilter >= 0) {
          if (nextDetector - firstDetector != getSizeC() &&
            cutIns[index] != null && nextDetector >= cutIns[index].size())
          {
            while (nextFilterDetector < firstDetector) {
              String filterID =
                MetadataTools.createLSID("Filter", i, nextFilter);
              store.setFilterID(filterID, i, nextFilter);

              nextFilterDetector++;
              nextFilter++;
            }
          }
          while (activeDetectors != null &&
            nextFilterDetector < activeDetectors.size() &&
            !(Boolean) activeDetectors.get(nextFilterDetector))
          {
            String filterID = MetadataTools.createLSID("Filter", i, nextFilter);
            store.setFilterID(filterID, i, nextFilter);
            nextFilterDetector++;
            nextFilter++;
          }
          String filterID = MetadataTools.createLSID("Filter", i, nextFilter);
          store.setFilterID(filterID, i, nextFilter);
          store.setLightPathEmissionFilterRef(filterID, i, c, 0);
          nextFilterDetector++;
          nextFilter++;
        }
      }

      for (int image=0; image<getImageCount(); image++) {
        Double xPos = posX[index];
        Double yPos = posY[index];
        if (i < fieldPosX.size() && fieldPosX.get(i) != null) {
          xPos = fieldPosX.get(i);
        }
        if (i < fieldPosY.size() && fieldPosY.get(i) != null) {
          yPos = fieldPosY.get(i);
        }
        if (xPos != null) {
          store.setPlanePositionX(xPos, i, image);
        }
        if (yPos != null) {
          store.setPlanePositionY(yPos, i, image);
        }
        store.setPlanePositionZ(posZ[index], i, image);
        if (timestamps[index] != null) {
          double timestamp = timestamps[index][image];
          if (timestamps[index][0] == acquiredDate[index]) {
            timestamp -= acquiredDate[index];
          }
          else if (timestamp == acquiredDate[index] && image > 0) {
            timestamp = timestamps[index][0];
          }
          store.setPlaneDeltaT(timestamp, i, image);
        }

        if (expTimes[index] != null) {
          int c = getZCTCoords(image)[1];
          store.setPlaneExposureTime(expTimes[index][c], i, image);
        }
      }

      if (imageROIs[index] != null) {
        for (int roi=0; roi<imageROIs[index].length; roi++) {
          if (imageROIs[index][roi] != null) {
            imageROIs[index][roi].storeROI(store, i, roiCount++, roi);
          }
        }
      }
    }
  }

  private Element getMetadataRoot(String xml)
    throws FormatException, IOException
  {
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder parser = factory.newDocumentBuilder();
      ByteArrayInputStream s =
        new ByteArrayInputStream(xml.getBytes(Constants.ENCODING));
      Element root = parser.parse(s).getDocumentElement();
      s.close();
      return root;
    }
    catch (ParserConfigurationException e) {
      throw new FormatException(e);
    }
    catch (SAXException e) {
      throw new FormatException(e);
    }
  }

  private void translateMetadata(Element root) throws FormatException {
    Element realRoot = (Element) root.getChildNodes().item(0);

    NodeList toPrune = getNodes(realRoot, "LDM_Block_Sequential_Master");
    if (toPrune != null) {
      for (int i=0; i<toPrune.getLength(); i++) {
        Element prune = (Element) toPrune.item(i);
        Element parent = (Element) prune.getParentNode();
        parent.removeChild(prune);
      }
    }

    NodeList imageNodes = getNodes(realRoot, "Image");

    tileCount = new int[imageNodes.getLength()];
    Arrays.fill(tileCount, 1);
    core = new ArrayList<CoreMetadata>(imageNodes.getLength());
    acquiredDate = new double[imageNodes.getLength()];
    descriptions = new String[imageNodes.getLength()];
    laserWavelength = new Vector[imageNodes.getLength()];
    laserIntensity = new Vector[imageNodes.getLength()];
    timestamps = new double[imageNodes.getLength()][];
    activeDetector = new Vector[imageNodes.getLength()];
    serialNumber = new String[imageNodes.getLength()];
    lensNA = new Double[imageNodes.getLength()];
    magnification = new Double[imageNodes.getLength()];
    immersions = new String[imageNodes.getLength()];
    corrections = new String[imageNodes.getLength()];
    objectiveModels = new String[imageNodes.getLength()];
    posX = new Double[imageNodes.getLength()];
    posY = new Double[imageNodes.getLength()];
    posZ = new Double[imageNodes.getLength()];
    refractiveIndex = new Double[imageNodes.getLength()];
    cutIns = new Vector[imageNodes.getLength()];
    cutOuts = new Vector[imageNodes.getLength()];
    filterModels = new Vector[imageNodes.getLength()];
    microscopeModels = new String[imageNodes.getLength()];
    detectorModels = new Vector[imageNodes.getLength()];
    detectorIndexes = new HashMap[imageNodes.getLength()];
    zSteps = new Double[imageNodes.getLength()];
    tSteps = new Double[imageNodes.getLength()];
    pinholes = new Double[imageNodes.getLength()];
    zooms = new Double[imageNodes.getLength()];

    expTimes = new Double[imageNodes.getLength()][];
    gains = new Double[imageNodes.getLength()][];
    detectorOffsets = new Double[imageNodes.getLength()][];
    channelNames = new String[imageNodes.getLength()][];
    exWaves = new Integer[imageNodes.getLength()][];
    imageROIs = new ROI[imageNodes.getLength()][];
    imageNames = new String[imageNodes.getLength()];

    core.clear();
    for (int i=0; i<imageNodes.getLength(); i++) {
      Element image = (Element) imageNodes.item(i);

      CoreMetadata ms = new CoreMetadata();
      core.add(ms);

      setSeries(i);

      translateImageNames(image, i);
      translateImageNodes(image, i);
      translateAttachmentNodes(image, i);
      translateScannerSettings(image, i);
      translateFilterSettings(image, i);
      translateTimestamps(image, i);
      translateLaserLines(image, i);
      translateROIs(image, i);
      translateSingleROIs(image, i);
      translateDetectors(image, i);

      Stack<String> nameStack = new Stack<String>();
      populateOriginalMetadata(image, nameStack);
      addUserCommentMeta(image);
    }
    setSeries(0);

    int totalSeries = 0;
    for (int count : tileCount) {
      totalSeries += count;
    }
    ArrayList<CoreMetadata> newCore = new ArrayList<CoreMetadata>();
    for (int i=0; i<core.size(); i++) {
      for (int tile=0; tile<tileCount[i]; tile++) {
        newCore.add(core.get(i));
      }
    }
    core = newCore;
  }

  private void populateOriginalMetadata(Element root, Stack<String> nameStack) {
    String name = root.getNodeName();
    if (root.hasAttributes() && !name.equals("Element") &&
      !name.equals("Attachment") && !name.equals("LMSDataContainerHeader"))
    {
      nameStack.push(name);

      String suffix = root.getAttribute("Identifier");
      String value = root.getAttribute("Variant");
      if (suffix == null || suffix.trim().length() == 0) {
        suffix = root.getAttribute("Description");
      }
      StringBuffer key = new StringBuffer();
      for (String k : nameStack) {
        key.append(k);
        key.append("|");
      }
      if (suffix != null && value != null && suffix.length() > 0 &&
        value.length() > 0 && !suffix.equals("HighInteger") &&
        !suffix.equals("LowInteger"))
      {
        addSeriesMetaList(key.toString() + suffix, value);
      }
      else {
        NamedNodeMap attributes = root.getAttributes();
        for (int i=0; i<attributes.getLength(); i++) {
          Attr attr = (Attr) attributes.item(i);
          if (!attr.getName().equals("HighInteger") &&
            !attr.getName().equals("LowInteger"))
          {
            addSeriesMeta(key.toString() + attr.getName(), attr.getValue());
          }
        }
      }
    }

    NodeList children = root.getChildNodes();
    for (int i=0; i<children.getLength(); i++) {
      Object child = children.item(i);
      if (child instanceof Element) {
        populateOriginalMetadata((Element) child, nameStack);
      }
    }

    if (root.hasAttributes() && !name.equals("Element") &&
      !name.equals("Attachment") && !name.equals("LMSDataContainerHeader"))
    {
      nameStack.pop();
    }
  }

  private void translateImageNames(Element imageNode, int image) {
    Vector<String> names = new Vector<String>();
    Element parent = imageNode;
    while (true) {
      parent = (Element) parent.getParentNode();
      if (parent == null || parent.getNodeName().equals("LEICA")) {
        break;
      }
      if (parent.getNodeName().equals("Element")) {
        names.add(parent.getAttribute("Name"));
      }
    }
    imageNames[image] = "";
    for (int i=names.size() - 2; i>=0; i--) {
      imageNames[image] += names.get(i);
      if (i > 0) imageNames[image] += "/";
    }
  }

  private void translateDetectors(Element imageNode, int image)
    throws FormatException
  {
    NodeList definitions = getNodes(imageNode, "ATLConfocalSettingDefinition");
    if (definitions == null) return;

    Vector<String> channels = new Vector<String>();
    int nextChannel = 0;
    for (int definition=0; definition<definitions.getLength(); definition++) {
      Element definitionNode = (Element) definitions.item(definition);
      String parentName = definitionNode.getParentNode().getNodeName();
      boolean isMaster = parentName.endsWith("Master");
      NodeList detectors = getNodes(definitionNode, "Detector");
      if (detectors == null) return;

      for (int d=0; d<detectors.getLength(); d++) {
        Element detector = (Element) detectors.item(d);
        NodeList multibands = getNodes(definitionNode, "MultiBand");

        String v = detector.getAttribute("Gain");
        Double gain =
          v == null || v.trim().length() == 0 ? null : new Double(v);
        v = detector.getAttribute("Offset");
        Double offset =
          v == null || v.trim().length() == 0 ? null : new Double(v);

        boolean active = "1".equals(detector.getAttribute("IsActive"));

        if (active) {
          String c = detector.getAttribute("Channel");
          int channel = c == null ? 0 : Integer.parseInt(c);

          if (detectorIndexes[image] != null && detectorModels[image] != null) {
            detectorModels[image].add(detectorIndexes[image].get(channel));
          }

          Element multiband = null;

          if (multibands != null) {
            for (int i=0; i<multibands.getLength(); i++) {
              Element mb = (Element) multibands.item(i);
              if (channel == Integer.parseInt(mb.getAttribute("Channel"))) {
                multiband = mb;
                break;
              }
            }
          }

          if (multiband != null) {
            String dye = multiband.getAttribute("DyeName");
            if (!channels.contains(dye)) {
              channels.add(dye);
            }

            double cutIn = new Double(multiband.getAttribute("LeftWorld"));
            double cutOut = new Double(multiband.getAttribute("RightWorld"));
            if ((int) cutIn > 0) {
              if (cutIns[image] == null) {
                cutIns[image] = new Vector<PositiveInteger>();
              }
              PositiveInteger in =
                FormatTools.getCutIn((int) Math.round(cutIn));
              if (in != null) {
                cutIns[image].add(in);
              }
            }
            if ((int) cutOut > 0) {
              if (cutOuts[image] == null) {
                cutOuts[image] = new Vector<PositiveInteger>();
              }
              PositiveInteger out =
                FormatTools.getCutOut((int) Math.round(cutOut));
              if (out != null) {
                cutOuts[image].add(out);
              }
            }
          }
          else {
            channels.add("");
          }

          if (!isMaster) {
            if (channel < nextChannel) {
              nextChannel = 0;
            }

            if (nextChannel < getEffectiveSizeC()) {
              if (gains[image] != null) {
                gains[image][nextChannel] = gain;
              }
              if (detectorOffsets[image] != null) {
                detectorOffsets[image][nextChannel] = offset;
              }
            }

            nextChannel++;
          }
        }

        if (active && activeDetector[image] != null) {
          activeDetector[image].add(active);
        }
      }
    }

    if (channels != null && channelNames[image] != null) {
      for (int i=0; i<getEffectiveSizeC(); i++) {
        int index = i + channels.size() - getEffectiveSizeC();
        if (index >= 0 && index < channels.size()) {
          if (channelNames[image][i] == null ||
            channelNames[image][i].trim().length() == 0)
          {
            channelNames[image][i] = channels.get(index);
          }
        }
      }
    }
  }

  private void translateROIs(Element imageNode, int image)
    throws FormatException
  {
    NodeList rois = getNodes(imageNode, "Annotation");
    if (rois == null) return;
    imageROIs[image] = new ROI[rois.getLength()];

    for (int r=0; r<rois.getLength(); r++) {
      Element roiNode = (Element) rois.item(r);

      ROI roi = new ROI();

      String type = roiNode.getAttribute("type");
      if (type != null) {
        roi.type = Integer.parseInt(type);
      }
      String color = roiNode.getAttribute("color");
      if (color != null) {
        roi.color = Long.parseLong(color);
      }
      roi.name = roiNode.getAttribute("name");
      roi.fontName = roiNode.getAttribute("fontName");
      roi.fontSize = roiNode.getAttribute("fontSize");
      roi.transX = parseDouble(roiNode.getAttribute("transTransX"));
      roi.transY = parseDouble(roiNode.getAttribute("transTransY"));
      roi.scaleX = parseDouble(roiNode.getAttribute("transScalingX"));
      roi.scaleY = parseDouble(roiNode.getAttribute("transScalingY"));
      roi.rotation = parseDouble(roiNode.getAttribute("transRotation"));
      String linewidth = roiNode.getAttribute("linewidth");
      if (linewidth != null) {
        try {
          roi.linewidth = Integer.parseInt(linewidth);
        }
        catch (NumberFormatException e) { }
      }
      roi.text = roiNode.getAttribute("text");

      NodeList vertices = getNodes(roiNode, "Vertex");
      if (vertices == null) {
        continue;
      }

      for (int v=0; v<vertices.getLength(); v++) {
        Element vertex = (Element) vertices.item(v);
        String xx = vertex.getAttribute("x");
        String yy = vertex.getAttribute("y");

        if (xx != null) {
          roi.x.add(parseDouble(xx));
        }
        if (yy != null) {
          roi.y.add(parseDouble(yy));
        }
      }
      imageROIs[image][r] = roi;

      if (getNodes(imageNode, "ROI") != null) {
        alternateCenter = true;
      }
    }
  }

  private void translateSingleROIs(Element imageNode, int image)
    throws FormatException
  {
    if (imageROIs[image] != null) return;
    NodeList children = getNodes(imageNode, "ROI");
    if (children == null) return;
    children = getNodes((Element) children.item(0), "Children");
    if (children == null) return;
    children = getNodes((Element) children.item(0), "Element");
    if (children == null) return;
    imageROIs[image] = new ROI[children.getLength()];

    for (int r=0; r<children.getLength(); r++) {
      NodeList rois = getNodes((Element) children.item(r), "ROISingle");

      Element roiNode = (Element) rois.item(0);
      ROI roi = new ROI();

      String type = roiNode.getAttribute("RoiType");
      if (type != null) {
        roi.type = Integer.parseInt(type);
      }
      String color = roiNode.getAttribute("Color");
      if (color != null) {
        roi.color = Long.parseLong(color);
      }
      Element parent = (Element) roiNode.getParentNode();
      parent = (Element) parent.getParentNode();
      roi.name = parent.getAttribute("Name");

      NodeList vertices = getNodes(roiNode, "P");

      double sizeX = physicalSizeXs.get(image);
      double sizeY = physicalSizeYs.get(image);

      for (int v=0; v<vertices.getLength(); v++) {
        Element vertex = (Element) vertices.item(v);
        String xx = vertex.getAttribute("X");
        String yy = vertex.getAttribute("Y");

        if (xx != null) {
          roi.x.add(parseDouble(xx) / sizeX);
        }
        if (yy != null) {
          roi.y.add(parseDouble(yy) / sizeY);
        }
      }

      Element transform = (Element) getNodes(roiNode, "Transformation").item(0);

      roi.rotation = parseDouble(transform.getAttribute("Rotation"));

      Element scaling = (Element) getNodes(transform, "Scaling").item(0);
      roi.scaleX = parseDouble(scaling.getAttribute("XScale"));
      roi.scaleY = parseDouble(scaling.getAttribute("YScale"));

      Element translation =
        (Element) getNodes(transform, "Translation").item(0);
      roi.transX = parseDouble(translation.getAttribute("X")) / sizeX;
      roi.transY = parseDouble(translation.getAttribute("Y")) / sizeY;

      imageROIs[image][r] = roi;
    }
  }

  private void translateLaserLines(Element imageNode, int image)
    throws FormatException
  {
    NodeList aotfLists = getNodes(imageNode, "AotfList");
    if (aotfLists == null) return;

    laserWavelength[image] = new Vector<Integer>();
    laserIntensity[image] = new Vector<Double>();

    int baseIntensityIndex = 0;

    for (int channel=0; channel<aotfLists.getLength(); channel++) {
      Element aotf = (Element) aotfLists.item(channel);
      NodeList laserLines = getNodes(aotf, "LaserLineSetting");
      if (laserLines == null) return;

      for (int laser=0; laser<laserLines.getLength(); laser++) {
        Element laserLine = (Element) laserLines.item(laser);

        String lineIndex = laserLine.getAttribute("LineIndex");
        String qual = laserLine.getAttribute("Qualifier");
        int index = lineIndex == null ? 0 : Integer.parseInt(lineIndex);
        int qualifier = qual == null ? 0: Integer.parseInt(qual);

        index += (2 - (qualifier / 10));
        if (index < 0) index = 0;

        Integer wavelength = new Integer(laserLine.getAttribute("LaserLine"));
        if (index < laserWavelength[image].size()) {
          laserWavelength[image].setElementAt(wavelength, index);
        }
        else {
          for (int i=laserWavelength[image].size(); i<index; i++) {
            laserWavelength[image].add(new Integer(0));
          }
          laserWavelength[image].add(wavelength);
        }

        String intensity = laserLine.getAttribute("IntensityDev");
        double realIntensity = intensity == null ? 0d : new Double(intensity);
        realIntensity = 100d - realIntensity;

        int realIndex = baseIntensityIndex + index;

        if (realIndex < laserIntensity[image].size()) {
          laserIntensity[image].setElementAt(realIntensity, realIndex);
        }
        else {
          while (realIndex < laserIntensity[image].size()) {
            laserIntensity[image].add(100d);
          }
          laserIntensity[image].add(realIntensity);
        }
      }

      baseIntensityIndex += laserWavelength[image].size();
    }
  }

  private void translateTimestamps(Element imageNode, int image)
    throws FormatException
  {
    NodeList timestampNodes = getNodes(imageNode, "TimeStamp");
    if (timestampNodes == null) return;

    timestamps[image] = new double[getImageCount()];

    if (timestampNodes != null) {
      for (int stamp=0; stamp<timestampNodes.getLength(); stamp++) {
        if (stamp < getImageCount()) {
          Element timestamp = (Element) timestampNodes.item(stamp);
          String stampHigh = timestamp.getAttribute("HighInteger");
          String stampLow = timestamp.getAttribute("LowInteger");
          long high = stampHigh == null ? 0 : Long.parseLong(stampHigh);
          long low = stampLow == null ? 0 : Long.parseLong(stampLow);

          long ms = DateTools.getMillisFromTicks(high, low);

          timestamps[image][stamp] = ms / 1000.0;
        }
      }
    }
    acquiredDate[image] = timestamps[image][0];

    NodeList relTimestampNodes = getNodes(imageNode, "RelTimeStamp");
    if (relTimestampNodes != null) {
      for (int stamp=0; stamp<relTimestampNodes.getLength(); stamp++) {
        if (stamp < getImageCount()) {
          Element timestamp = (Element) relTimestampNodes.item(stamp);
          timestamps[image][stamp] =
            new Double(timestamp.getAttribute("Time"));
        }
      }
    }
  }

  private void translateFilterSettings(Element imageNode, int image)
    throws FormatException
  {
    NodeList filterSettings = getNodes(imageNode, "FilterSettingRecord");
    if (filterSettings == null) return;

    activeDetector[image] = new Vector<Boolean>();
    cutIns[image] = new Vector<PositiveInteger>();
    cutOuts[image] = new Vector<PositiveInteger>();
    filterModels[image] = new Vector<String>();
    detectorIndexes[image] = new HashMap<Integer, String>();

    int nextChannel = 0;

    for (int i=0; i<filterSettings.getLength(); i++) {
      Element filterSetting = (Element) filterSettings.item(i);

      String object = filterSetting.getAttribute("ObjectName");
      String attribute = filterSetting.getAttribute("Attribute");
      String objectClass = filterSetting.getAttribute("ClassName");
      String variant = filterSetting.getAttribute("Variant");
      String data = filterSetting.getAttribute("Data");

      if (attribute.equals("NumericalAperture")) {
        lensNA[image] = new Double(variant);
      }
      else if (attribute.equals("OrderNumber")) {
        serialNumber[image] = variant;
      }
      else if (objectClass.equals("CDetectionUnit")) {
        if (attribute.equals("State")) {
          int channel = getChannelIndex(filterSetting);
          if (channel < 0) continue;

          detectorIndexes[image].put(new Integer(data), object);
          activeDetector[image].add(variant.equals("Active"));
        }
      }
      else if (attribute.equals("Objective")) {
        StringTokenizer tokens = new StringTokenizer(variant, " ");
        boolean foundMag = false;
        StringBuffer model = new StringBuffer();
        while (!foundMag) {
          String token = tokens.nextToken();
          int x = token.indexOf("x");
          if (x != -1) {
            foundMag = true;

            String na = token.substring(x + 1);

            magnification[image] = new Double(token.substring(0, x));
            lensNA[image] = new Double(na);
          }
          else {
            model.append(token);
            model.append(" ");
          }
        }

        String immersion = "Other";
        if (tokens.hasMoreTokens()) {
          immersion = tokens.nextToken();
          if (immersion == null || immersion.trim().equals("")) {
            immersion = "Other";
          }
        }
        immersions[image] = immersion;

        String correction = "Other";
        if (tokens.hasMoreTokens()) {
          correction = tokens.nextToken();
          if (correction == null || correction.trim().equals("")) {
            correction = "Other";
          }
        }
        corrections[image] = correction;

        objectiveModels[image] = model.toString().trim();
      }
      else if (attribute.equals("RefractionIndex")) {
        refractiveIndex[image] = new Double(variant);
      }
      else if (attribute.equals("XPos")) {
        posX[image] = new Double(variant);
      }
      else if (attribute.equals("YPos")) {
        posY[image] = new Double(variant);
      }
      else if (attribute.equals("ZPos")) {
        posZ[image] = new Double(variant);
      }
      else if (objectClass.equals("CSpectrophotometerUnit")) {
        Integer v = null;
        try {
          v = new Integer((int) Double.parseDouble(variant));
        }
        catch (NumberFormatException e) { }
        String description = filterSetting.getAttribute("Description");
        if (description.endsWith("(left)")) {
          filterModels[image].add(object);
          if (v != null && v > 0) {
            PositiveInteger in = FormatTools.getCutIn(v);
            if (in != null) {
              cutIns[image].add(in);
            }
          }
        }
        else if (description.endsWith("(right)")) {
          if (v != null && v > 0) {
            PositiveInteger out = FormatTools.getCutOut(v);
            if (out != null) {
              cutOuts[image].add(out);
            }
          }
        }
        else if (attribute.equals("Stain")) {
          if (nextChannel < channelNames[image].length) {
            channelNames[image][nextChannel++] = variant;
          }
        }
      }
    }
  }

  private void translateScannerSettings(Element imageNode, int image)
    throws FormatException
  {
    NodeList scannerSettings = getNodes(imageNode, "ScannerSettingRecord");
    if (scannerSettings == null) return;

    expTimes[image] = new Double[getEffectiveSizeC()];
    gains[image] = new Double[getEffectiveSizeC()];
    detectorOffsets[image] = new Double[getEffectiveSizeC()];
    channelNames[image] = new String[getEffectiveSizeC()];
    exWaves[image] = new Integer[getEffectiveSizeC()];
    detectorModels[image] = new Vector<String>();

    for (int i=0; i<scannerSettings.getLength(); i++) {
      Element scannerSetting = (Element) scannerSettings.item(i);
      String id = scannerSetting.getAttribute("Identifier");
      if (id == null) id = "";
      String suffix = scannerSetting.getAttribute("Identifier");
      String value = scannerSetting.getAttribute("Variant");

      if (id.equals("SystemType")) {
        microscopeModels[image] = value;
      }
      else if (id.equals("dblPinhole")) {
        pinholes[image] = Double.parseDouble(value) * 1000000;
      }
      else if (id.equals("dblZoom")) {
        zooms[image] = new Double(value);
      }
      else if (id.equals("dblStepSize")) {
        zSteps[image] = Double.parseDouble(value) * 1000000;
      }
      else if (id.equals("nDelayTime_s")) {
        tSteps[image] = new Double(value);
      }
      else if (id.equals("CameraName")) {
        detectorModels[image].add(value);
      }
      else if (id.equals("eDirectional")) {
        addSeriesMeta("Reverse X orientation", value.equals("1"));
      }
      else if (id.equals("eDirectionalY")) {
        addSeriesMeta("Reverse Y orientation", value.equals("1"));
      }
      else if (id.indexOf("WFC") == 1) {
        int c = 0;
        try {
          c = Integer.parseInt(id.replaceAll("\\D", ""));
        }
        catch (NumberFormatException e) { }
        if (c < 0 || c >= getEffectiveSizeC()) {
          continue;
        }

        if (id.endsWith("ExposureTime")) {
          expTimes[image][c] = new Double(value);
        }
        else if (id.endsWith("Gain")) {
          gains[image][c] = new Double(value);
        }
        else if (id.endsWith("WaveLength")) {
          Integer exWave = new Integer(value);
          if (exWave > 0) {
            exWaves[image][c] = exWave;
          }
        }
        // NB: "UesrDefName" is not a typo.
        else if (id.endsWith("UesrDefName") && !value.equals("None")) {
          if (channelNames[image][c] == null ||
            channelNames[image][c].trim().length() == 0)
          {
            channelNames[image][c] = value;
          }
        }
      }
    }
  }

  private void translateAttachmentNodes(Element imageNode, int image)
    throws FormatException
  {
    NodeList attachmentNodes = getNodes(imageNode, "Attachment");
    if (attachmentNodes == null) return;
    for (int i=0; i<attachmentNodes.getLength(); i++) {
      Element attachment = (Element) attachmentNodes.item(i);

      String attachmentName = attachment.getAttribute("Name");

      if ("ContextDescription".equals(attachmentName)) {
        descriptions[image] = attachment.getAttribute("Content");
      }
      else if ("TileScanInfo".equals(attachmentName)) {
        NodeList tiles = getNodes(attachment, "Tile");

        for (int tile=0; tile<tiles.getLength(); tile++) {
          Element tileNode = (Element) tiles.item(tile);
          String posX = tileNode.getAttribute("PosX");
          String posY = tileNode.getAttribute("PosY");

          if (posX != null) {
            try {
              fieldPosX.add(new Double(posX));
            }
            catch (NumberFormatException e) {
              LOGGER.debug("", e);
              fieldPosX.add(null);
            }
          }
          if (posY != null) {
            try {
              fieldPosY.add(new Double(posY));
            }
            catch (NumberFormatException e) {
              LOGGER.debug("", e);
              fieldPosY.add(null);
            }
          }
        }
      }
    }
  }

  private void addUserCommentMeta(Element imageNode)
    throws FormatException
  {
    NodeList attachmentNodes = getNodes(imageNode, "User-Comment");
    if (attachmentNodes == null) return;
    for (int i=0; i<attachmentNodes.getLength(); i++) {
      Node attachment = attachmentNodes.item(i);
      addSeriesMeta("User-Comment[" + i + "]", attachment.getTextContent());
    }
  }

  private void translateImageNodes(Element imageNode, int i)
    throws FormatException
  {
    CoreMetadata ms = core.get(i);
    ms.orderCertain = true;
    ms.metadataComplete = true;
    ms.littleEndian = true;
    ms.falseColor = true;

    NodeList channels = getChannelDescriptionNodes(imageNode);
    NodeList dimensions = getDimensionDescriptionNodes(imageNode);

    HashMap<Long, String> bytesPerAxis = new HashMap<Long, String>();

    Double physicalSizeX = null;
    Double physicalSizeY = null;
    Double physicalSizeZ = null;

    ms.sizeC = channels.getLength();
    for (int ch=0; ch<channels.getLength(); ch++) {
      Element channel = (Element) channels.item(ch);

      lutNames.add(channel.getAttribute("LUTName"));
      String bytesInc = channel.getAttribute("BytesInc");
      long bytes = bytesInc == null ? 0 : Long.parseLong(bytesInc);
      if (bytes > 0) {
        bytesPerAxis.put(bytes, "C");
      }
    }

    int extras = 1;

    for (int dim=0; dim<dimensions.getLength(); dim++) {
      Element dimension = (Element) dimensions.item(dim);

      int id = Integer.parseInt(dimension.getAttribute("DimID"));
      int len = Integer.parseInt(dimension.getAttribute("NumberOfElements"));
      long nBytes = Long.parseLong(dimension.getAttribute("BytesInc"));
      Double physicalLen = new Double(dimension.getAttribute("Length"));
      String unit = dimension.getAttribute("Unit");

      physicalLen /= len;
      if (unit.equals("Ks")) {
        physicalLen /= 1000;
      }
      else if (unit.equals("m")) {
        physicalLen *= 1000000;
      }

      switch (id) {
        case 1: // X axis
          ms.sizeX = len;
          ms.rgb = (nBytes % 3) == 0;
          if (ms.rgb) nBytes /= 3;
          ms.pixelType =
            FormatTools.pixelTypeFromBytes((int) nBytes, false, true);
          physicalSizeX = physicalLen;
          break;
        case 2: // Y axis
          if (ms.sizeY != 0) {
            if (ms.sizeZ == 1) {
              ms.sizeZ = len;
              bytesPerAxis.put(nBytes, "Z");
              physicalSizeZ = (physicalLen * len) / (len - 1);
            }
            else if (ms.sizeT == 1) {
              ms.sizeT = len;
              bytesPerAxis.put(nBytes, "T");
            }
          }
          else {
            ms.sizeY = len;
            physicalSizeY = physicalLen;
          }
          break;
        case 3: // Z axis
          if (ms.sizeY == 0) {
            // XZ scan - swap Y and Z
            ms.sizeY = len;
            ms.sizeZ = 1;
            bytesPerAxis.put(nBytes, "Y");
            physicalSizeY = physicalLen;
          }
          else {
            ms.sizeZ = len;
            bytesPerAxis.put(nBytes, "Z");
            physicalSizeZ = (physicalLen * len) / (len - 1);
          }
          break;
        case 4: // T axis
          if (ms.sizeY == 0) {
            // XT scan - swap Y and T
            ms.sizeY = len;
            ms.sizeT = 1;
            bytesPerAxis.put(nBytes, "Y");
            physicalSizeY = physicalLen;
          }
          else {
            ms.sizeT = len;
            bytesPerAxis.put(nBytes, "T");
          }
          break;
        case 10: // tile axis
          tileCount[i] *= len;
          break;
        default:
          extras *= len;
      }
    }

    physicalSizeXs.add(physicalSizeX);
    physicalSizeYs.add(physicalSizeY);

    if (zSteps[i] == null && physicalSizeZ != null) {
      zSteps[i] = Math.abs(physicalSizeZ);
    }

    if (extras > 1) {
      if (ms.sizeZ == 1) ms.sizeZ = extras;
      else {
        if (ms.sizeT == 0) ms.sizeT = extras;
        else ms.sizeT *= extras;
      }
    }

    if (ms.sizeC == 0) ms.sizeC = 1;
    if (ms.sizeZ == 0) ms.sizeZ = 1;
    if (ms.sizeT == 0) ms.sizeT = 1;

    ms.interleaved = ms.rgb;
    ms.indexed = !ms.rgb;
    ms.imageCount = ms.sizeZ * ms.sizeT;
    if (!ms.rgb) ms.imageCount *= ms.sizeC;

    Long[] bytes = bytesPerAxis.keySet().toArray(new Long[0]);
    Arrays.sort(bytes);
    ms.dimensionOrder = "XY";
    if (getSizeC() > 1 && getSizeT() > 1) {
      ms.dimensionOrder += "C";
    }
    for (Long nBytes : bytes) {
      String axis = bytesPerAxis.get(nBytes);
      if (ms.dimensionOrder.indexOf(axis) == -1) {
        ms.dimensionOrder += axis;
      }
    }

    if (ms.dimensionOrder.indexOf("Z") == -1) {
      ms.dimensionOrder += "Z";
    }
    if (ms.dimensionOrder.indexOf("C") == -1) {
      ms.dimensionOrder += "C";
    }
    if (ms.dimensionOrder.indexOf("T") == -1) {
      ms.dimensionOrder += "T";
    }
  }

  private NodeList getNodes(Element root, String nodeName) {
    NodeList nodes = root.getElementsByTagName(nodeName);
    if (nodes.getLength() == 0) {
      NodeList children = root.getChildNodes();
      for (int i=0; i<children.getLength(); i++) {
        Object child = children.item(i);
        if (child instanceof Element) {
          NodeList childNodes = getNodes((Element) child, nodeName);
          if (childNodes != null) {
            return childNodes;
          }
        }
      }
      return null;
    }
    else return nodes;
  }

  private Element getImageDescription(Element root) {
    return (Element) root.getElementsByTagName("ImageDescription").item(0);
  }

  private NodeList getChannelDescriptionNodes(Element root) {
    Element imageDescription = getImageDescription(root);
    Element channels =
      (Element) imageDescription.getElementsByTagName("Channels").item(0);
    return channels.getElementsByTagName("ChannelDescription");
  }

  private NodeList getDimensionDescriptionNodes(Element root) {
    Element imageDescription = getImageDescription(root);
    Element channels =
      (Element) imageDescription.getElementsByTagName("Dimensions").item(0);
    return channels.getElementsByTagName("DimensionDescription");
  }

  private int getChannelIndex(Element filterSetting) {
    String data = filterSetting.getAttribute("data");
    if (data == null || data.equals("")) {
      data = filterSetting.getAttribute("Data");
    }
    int channel = data == null || data.equals("") ? 0 : Integer.parseInt(data);
    if (channel < 0) return -1;
    return channel - 1;
  }

  // -- Helper class --

  class ROI {
    // -- Constants --

    public static final int TEXT = 512;
    public static final int SCALE_BAR = 8192;
    public static final int POLYGON = 32;
    public static final int RECTANGLE = 16;
    public static final int LINE = 256;
    public static final int ARROW = 2;

    // -- Fields --
    public int type;

    public Vector<Double> x = new Vector<Double>();
    public Vector<Double> y = new Vector<Double>();

    // center point of the ROI
    public double transX, transY;

    // transformation parameters
    public double scaleX, scaleY;
    public double rotation;

    public long color;
    public int linewidth;

    public String text;
    public String fontName;
    public String fontSize;
    public String name;

    private boolean normalized = false;

    // -- ROI API methods --

    public void storeROI(MetadataStore store, int series, int roi, int roiIndex)
    {
      MetadataLevel level = getMetadataOptions().getMetadataLevel();
      if (level == MetadataLevel.NO_OVERLAYS || level == MetadataLevel.MINIMUM)
      {
        return;
      }

      // keep in mind that vertices are given relative to the center
      // point of the ROI and the transX/transY values are relative to
      // the center point of the image

      String roiID = MetadataTools.createLSID("ROI", roi);
      store.setImageROIRef(roiID, series, roiIndex);
      store.setROIID(roiID, roi);
      store.setLabelID(MetadataTools.createLSID("Shape", roi, 0), roi, 0);
      if (text == null) {
        text = name;
      }
      store.setLabelText(text, roi, 0);
      if (fontSize != null) {
        try {
          int size = (int) Double.parseDouble(fontSize);
          NonNegativeInteger fontSize = FormatTools.getFontSize(size);
          if (fontSize != null) {
            store.setLabelFontSize(fontSize, roi, 0);
          }
        }
        catch (NumberFormatException e) { }
      }
      store.setLabelStrokeWidth(new Double(linewidth), roi, 0);

      if (!normalized) normalize();

      double cornerX = x.get(0).doubleValue();
      double cornerY = y.get(0).doubleValue();

      store.setLabelX(cornerX, roi, 0);
      store.setLabelY(cornerY, roi, 0);

      int centerX = (core.get(series).sizeX / 2) - 1;
      int centerY = (core.get(series).sizeY / 2) - 1;

      double roiX = centerX + transX;
      double roiY = centerY + transY;

      if (alternateCenter) {
        roiX = transX - 2 * cornerX;
        roiY = transY - 2 * cornerY;
      }

      // TODO : rotation/scaling not populated

      String shapeID = MetadataTools.createLSID("Shape", roi, 1);
      switch (type) {
        case POLYGON:
          StringBuffer points = new StringBuffer();
          for (int i=0; i<x.size(); i++) {
            points.append(x.get(i).doubleValue() + roiX);
            points.append(",");
            points.append(y.get(i).doubleValue() + roiY);
            if (i < x.size() - 1) points.append(" ");
          }
          store.setPolygonID(shapeID, roi, 1);
          store.setPolygonPoints(points.toString(), roi, 1);

          break;
        case TEXT:
        case RECTANGLE:
          store.setRectangleID(shapeID, roi, 1);
          store.setRectangleX(roiX - Math.abs(cornerX), roi, 1);
          store.setRectangleY(roiY - Math.abs(cornerY), roi, 1);
          double width = 2 * Math.abs(cornerX);
          double height = 2 * Math.abs(cornerY);
          store.setRectangleWidth(width, roi, 1);
          store.setRectangleHeight(height, roi, 1);

          break;
        case SCALE_BAR:
        case ARROW:
        case LINE:
          store.setLineID(shapeID, roi, 1);
          store.setLineX1(roiX + x.get(0), roi, 1);
          store.setLineY1(roiY + y.get(0), roi, 1);
          store.setLineX2(roiX + x.get(1), roi, 1);
          store.setLineY2(roiY + y.get(1), roi, 1);
          break;
      }
    }

    // -- Helper methods --

    /**
     * Vertices and transformation values are not stored in pixel coordinates.
     * We need to convert them from physical coordinates to pixel coordinates
     * so that they can be stored in a MetadataStore.
     */
    private void normalize() {
      if (normalized) return;

      // coordinates are in meters

      transX *= 1000000;
      transY *= 1000000;
      transX *= 1;
      transY *= 1;

      for (int i=0; i<x.size(); i++) {
        double coordinate = x.get(i).doubleValue() * 1000000;
        coordinate *= 1;
        x.setElementAt(coordinate, i);
      }

      for (int i=0; i<y.size(); i++) {
        double coordinate = y.get(i).doubleValue() * 1000000;
        coordinate *= 1;
        y.setElementAt(coordinate, i);
      }

      normalized = true;
    }
  }

  private double parseDouble(String number) {
    if (number != null) {
      number = number.replaceAll(",", ".");
      try {
        return Double.parseDouble(number);
      }
      catch (NumberFormatException e) { }
    }
    return 0;
  }

  private Color getChannelColor(int colorCode) {
    switch (colorCode) {
      case 0: // red
        return new Color(255, 0, 0, 255);
      case 1: // green
        return new Color(0, 255, 0, 255);
      case 2: // blue
        return new Color(0, 0, 255, 255);
      case 3: // cyan
        return new Color(0, 255, 255, 255);
      case 4: // magenta
        return new Color(255, 0, 255, 255);
      case 5: // yellow
        return new Color(255, 255, 0, 255);
    }
    return new Color(255, 255, 255, 255);
  }

  private int getTileIndex(int coreIndex) {
    int count = 0;
    for (int tile=0; tile<tileCount.length; tile++) {
      if (coreIndex < count + tileCount[tile]) {
        return tile;
      }
      count += tileCount[tile];
    }
    return -1;
  }

}
