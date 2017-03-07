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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
import loci.formats.meta.MetadataStore;
import loci.formats.services.OMEXMLService;
import ome.xml.model.enums.DetectorType;
import ome.xml.model.enums.LaserMedium;
import ome.xml.model.enums.LaserType;
import ome.xml.model.primitives.Color;
import ome.xml.model.primitives.PercentFraction;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.Timestamp;
import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.UNITS;

import org.xml.sax.SAXException;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * LIFReader is the file format reader for Leica LIF files.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class LIFReader extends FormatReader {

  // -- Constants --

  public static final String OLD_PHYSICAL_SIZE_KEY =
    "leicalif.old_physical_size";
  public static final boolean OLD_PHYSICAL_SIZE_DEFAULT = false;

  public static final byte LIF_MAGIC_BYTE = 0x70;
  public static final byte LIF_MEMORY_BYTE = 0x2a;

  /** The encoding used in this file.*/
  private static final String ENCODING = "ISO-8859-1";

  private static final ImmutableMap<String, Integer> CHANNEL_PRIORITIES =
    createChannelPriorities();

  private static ImmutableMap<String, Integer> createChannelPriorities() {
    final Builder<String, Integer> h = ImmutableMap.builder();

    h.put("red", 0);
    h.put("green", 1);
    h.put("blue", 2);
    h.put("cyan", 3);
    h.put("magenta", 4);
    h.put("yellow", 5);
    h.put("black", 6);
    h.put("gray", 7);
    h.put("", 8);

    return h.build();
  }

  // -- Fields --

  /** Offsets to memory blocks, paired with their corresponding description. */
  private List<Long> offsets;

  private int[][] realChannel;
  private int lastChannel = 0;

  private List<String> lutNames = new ArrayList<String>();
  private List<Double> physicalSizeXs = new ArrayList<Double>();
  private List<Double> physicalSizeYs = new ArrayList<Double>();
  private List<Length> fieldPosX = new ArrayList<Length>();
  private List<Length> fieldPosY = new ArrayList<Length>();

  private String[] descriptions, microscopeModels, serialNumber;
  private Double[] pinholes, zooms, zSteps, tSteps, lensNA;
  private Double[][] expTimes, gains, detectorOffsets;
  private String[][] channelNames;
  private List[] detectorModels;
  private Double[][] exWaves;
  private List[] activeDetector;
  private HashMap[] detectorIndexes;

  private String[] immersions, corrections, objectiveModels;
  private Double[] magnification;
  private Length[] posX, posY, posZ;
  private Double[] refractiveIndex;
  private List[] cutIns, cutOuts, filterModels;
  private Double[][] timestamps;
  private List[] laserWavelength, laserIntensity, laserActive, laserFrap;

  private ROI[][] imageROIs;
  private boolean alternateCenter = false;
  private String[] imageNames;
  private double[] acquiredDate;

  private int[] tileCount;
  private long[] tileBytesInc;
  private long endPointer;

  // -- Constructor --

  /** Constructs a new Leica LIF reader. */
  public LIFReader() {
    super("Leica Image File Format", "lif");
    suffixNecessary = false;
    domains = new String[] {FormatTools.LM_DOMAIN};
  }

  public boolean useOldPhysicalSizeCalculation() {
    MetadataOptions options = getMetadataOptions();
    if (options instanceof DynamicMetadataOptions) {
      return ((DynamicMetadataOptions) options).getBoolean(
        OLD_PHYSICAL_SIZE_KEY, OLD_PHYSICAL_SIZE_DEFAULT);
    }
    return OLD_PHYSICAL_SIZE_DEFAULT;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    return getSizeY();
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 1;
    if (!FormatTools.validStream(stream, blockLen, true)) return false;
    return stream.read() == LIF_MAGIC_BYTE;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  @Override
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
  @Override
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
  @Override
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

    seekStartOfPlane(no, offset, planeSize);

    if (bytesToSkip == 0) {
      readPlane(in, x, y, w, h, buf);
    }
    else {
      in.skipBytes(bytesToSkip * getSizeY() * no);
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
  
  private void seekStartOfPlane(int no, long dataOffset, long planeSize)
    throws IOException
  {
    int index = getTileIndex(series);
    long posInFile;

    int numberOfTiles = tileCount[index];
    if (numberOfTiles > 1) {
      // LAS AF treats tiles just like any other dimension, while we do not.
      // Hence we need to take the tiles into account for a frame's position.
      long bytesIncPerTile = tileBytesInc[index];
      long framesPerTile = bytesIncPerTile / planeSize;

      if (framesPerTile > Integer.MAX_VALUE) {
        throw new IOException("Could not read frame due to int overflow");
      }

      int noOutsideTiles = no / (int) framesPerTile;
      int noInsideTiles = no % (int) framesPerTile;

      int tile = series;
      for (int i = 0; i < index; i++) {
        tile -= tileCount[i];
      }

      posInFile = dataOffset;
      posInFile += noOutsideTiles * bytesIncPerTile * numberOfTiles;
      posInFile += tile * bytesIncPerTile;
      posInFile += noInsideTiles * planeSize;
    }
    else {
      posInFile = dataOffset + no * planeSize;
    }
    
    // seek instead of skipBytes to prevent dangerous int cast
    in.seek(posInFile);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
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
      laserWavelength = laserIntensity = laserActive = laserFrap = null;
      imageROIs = null;
      alternateCenter = false;
      imageNames = null;
      acquiredDate = null;
      detectorIndexes = null;
      tileCount = null;
      tileBytesInc = null;
      fieldPosX.clear();
      fieldPosY.clear();
      endPointer = 0;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessInputStream(id);
    in.setEncoding(ENCODING);
    offsets = new ArrayList<Long>();

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
        offsets.add(in.getFilePointer() + descrLength);
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
    try {
      ServiceFactory factory = new ServiceFactory();
      OMEXMLService service = factory.getInstance(OMEXMLService.class);
      service.createOMEXMLMetadata();
    }
    catch (DependencyException exc) {
      throw new FormatException("Could not create OME-XML store.", exc);
    }
    catch (ServiceException exc) {
      throw new FormatException("Could not create OME-XML store.", exc);
    }
    MetadataStore store = makeFilterMetadata();

    // the XML blocks stored in a LIF file are invalid,
    // because they don't have a root node

    xml = "<?xml version=\"1.0\" encoding=\""+ENCODING+"\"?><LEICA>" + xml +
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
            (Length) cutIns[index].get(filter), i, filter);
          store.setTransmittanceRangeCutOut(
            (Length) cutOuts[index].get(filter), i, filter);
        }
      }

      final List<Double> lasers = laserWavelength[index];
      final List<Double> laserIntensities = laserIntensity[index];

      final List<Boolean> active = laserActive[index];
      final List<Boolean> frap = laserFrap[index];
      int nextChannel = 0;

      if (lasers != null) {
        int laserIndex = 0;
        while (laserIndex < lasers.size()) {
          if ((Double) lasers.get(laserIndex) == 0) {
            lasers.remove(laserIndex);
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
          Double wavelength = (Double) lasers.get(laser);
          Length wave = FormatTools.getWavelength(wavelength);
          if (wave != null) {
            store.setLaserWavelength(wave, i, laser);
          }
        }

        Set<Integer> ignoredChannels = new HashSet<Integer>();
        final List<Integer> validIntensities = new ArrayList<Integer>();
        int size = lasers.size();
        int channel = 0;
        Set<Integer> channels = new HashSet<Integer>();

        for (int laser=0; laser<laserIntensities.size(); laser++) {
          double intensity = (Double) laserIntensities.get(laser);
          channel = laser/size;
          if (intensity < 100) {
            validIntensities.add(laser);
            channels.add(channel);
          }
          ignoredChannels.add(channel);
        }
        //remove channels w/o valid intensities
        ignoredChannels.removeAll(channels);
        //remove entries if channel has 2 wavelengths
        //e.g. 30% 458 70% 633
        int s = validIntensities.size();

        int jj;
        Set<Integer> toRemove = new HashSet<Integer>();

        int as = active.size();
        for (int j = 0; j < s; j++) {
          if (j < as && !(Boolean) active.get(j)) {
            toRemove.add(validIntensities.get(j));
          }
          jj = j+1;
          if (jj < s) {
            int v = validIntensities.get(j)/size;
            int vv = validIntensities.get(jj)/size;
            if (vv == v) {//do not consider that channel.
              toRemove.add(validIntensities.get(j));
              toRemove.add(validIntensities.get(jj));
              ignoredChannels.add(j);
            }
          }
        }
        if (toRemove.size() > 0) {
          validIntensities.removeAll(toRemove);
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
        if (!noNames && frap != null) { //only use name for frap.
          for (int k = 0; k < frap.size(); k++) {
            if (!frap.get(k)) {
              noNames = true;
              break;
            }
          }
        }

        int nextFilter = 0;
        //int nextFilter = cutIns[i].size() - getEffectiveSizeC();
        for (int k=0; k<validIntensities.size(); k++, nextChannel++) {
          int laserArrayIndex = validIntensities.get(k);
          double intensity = (Double) laserIntensities.get(laserArrayIndex);
          int laser = laserArrayIndex % lasers.size();
          Double wavelength = (Double) lasers.get(laser);
          if (wavelength != 0) {
            while (ignoredChannels.contains(nextChannel)) {
              nextChannel++;
            }
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

              Length ex = FormatTools.getExcitationWavelength(wavelength);
              if (ex != null) {
                store.setChannelExcitationWavelength(ex, i, nextChannel);
              }

              if (wavelength > 0) {
                if (cutIns[index] == null || nextFilter >= cutIns[index].size())
                {
                  continue;
                }
                Double cutIn =
                  ((Length) cutIns[index].get(nextFilter)).value(UNITS.NANOMETER).doubleValue();
                while (cutIn - wavelength > 20) {
                  nextFilter++;
                  if (nextFilter < cutIns[index].size()) {
                    cutIn = ((Length)
                      cutIns[index].get(nextFilter)).value(UNITS.NANOMETER).doubleValue();
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
          DateTools.ISO8601_FORMAT, false)), i);
      }
      store.setImageName(imageNames[index].trim(), i);

      Length sizeX =
        FormatTools.getPhysicalSizeX(physicalSizeXs.get(index));
      Length sizeY =
        FormatTools.getPhysicalSizeY(physicalSizeYs.get(index));
      Length sizeZ = FormatTools.getPhysicalSizeZ(zSteps[index]);

      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, i);
      }
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, i);
      }
      if (sizeZ != null) {
        store.setPixelsPhysicalSizeZ(sizeZ, i);
      }
      if (tSteps[index] != null) {
        store.setPixelsTimeIncrement(new Time(tSteps[index], UNITS.SECOND), i);
      }

      final List<String> detectors = detectorModels[index];
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

      final List<Boolean> activeDetectors = activeDetector[index];
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
        if (pinholes[index] != null) {
          store.setChannelPinholeSize(new Length(pinholes[index], UNITS.MICROMETER), i, c);
        }
        if (exWaves[index] != null) {
          if (exWaves[index][c] != null && exWaves[index][c] > 1) {
            Length ex =
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
        Length xPos = posX[index];
        Length yPos = posY[index];
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
          if (timestamps[index][image] != null) {
            double timestamp = timestamps[index][image];
            if (timestamps[index][0] == acquiredDate[index]) {
              timestamp -= acquiredDate[index];
            }
            else if (timestamp == acquiredDate[index] && image > 0) {
              timestamp = timestamps[index][0];
            }
            store.setPlaneDeltaT(new Time(timestamp, UNITS.SECOND), i, image);
          }
        }

        if (expTimes[index] != null) {
          int c = getZCTCoords(image)[1];
          if (expTimes[index][c] != null)
          {
            store.setPlaneExposureTime(new Time(expTimes[index][c], UNITS.SECOND), i, image);
          }
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
    ByteArrayInputStream s = null;
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder parser = factory.newDocumentBuilder();
      s = new ByteArrayInputStream(xml.getBytes(ENCODING));
      return parser.parse(s).getDocumentElement();
    }
    catch (ParserConfigurationException e) {
      throw new FormatException(e);
    }
    catch (SAXException e) {
      throw new FormatException(e);
    } finally {
        if (s != null) s.close();
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

    NodeList images = getNodes(realRoot, "Image");
    List<Element> imageNodes = new ArrayList<Element>();
    Long[] oldOffsets = null;
    if (images.getLength() > offsets.size()) {
      oldOffsets = offsets.toArray(new Long[offsets.size()]);
      offsets.clear();
    }

    int nextOffset = 0;
    for (int i=0; i<images.getLength(); i++) {
      Element image = (Element) images.item(i);
      Element grandparent = (Element) image.getParentNode();
      if (grandparent == null) {
        continue;
      }
      grandparent = (Element) grandparent.getParentNode();
      if (grandparent == null) {
        continue;
      }
      if (!"ProcessingHistory".equals(grandparent.getNodeName())) {
        // image is being referenced from an event list
        imageNodes.add(image);
        if (oldOffsets != null && nextOffset < oldOffsets.length) {
          offsets.add(oldOffsets[nextOffset]);
        }
      }
      grandparent = (Element) grandparent.getParentNode();
      if (grandparent == null) {
        continue;
      }
      grandparent = (Element) grandparent.getParentNode();
      if (grandparent != null) {
        if (!"Image".equals(grandparent.getNodeName())) {
          nextOffset++;
        }
      }
    }

    tileCount = new int[imageNodes.size()];
    Arrays.fill(tileCount, 1);
    tileBytesInc = new long[imageNodes.size()];
    core = new ArrayList<CoreMetadata>(imageNodes.size());
    acquiredDate = new double[imageNodes.size()];
    descriptions = new String[imageNodes.size()];
    laserWavelength = new List[imageNodes.size()];
    laserIntensity = new List[imageNodes.size()];
    laserActive = new List[imageNodes.size()];
    laserFrap = new List[imageNodes.size()];
    timestamps = new Double[imageNodes.size()][];
    activeDetector = new List[imageNodes.size()];
    serialNumber = new String[imageNodes.size()];
    lensNA = new Double[imageNodes.size()];
    magnification = new Double[imageNodes.size()];
    immersions = new String[imageNodes.size()];
    corrections = new String[imageNodes.size()];
    objectiveModels = new String[imageNodes.size()];
    posX = new Length[imageNodes.size()];
    posY = new Length[imageNodes.size()];
    posZ = new Length[imageNodes.size()];
    refractiveIndex = new Double[imageNodes.size()];
    cutIns = new List[imageNodes.size()];
    cutOuts = new List[imageNodes.size()];
    filterModels = new List[imageNodes.size()];
    microscopeModels = new String[imageNodes.size()];
    detectorModels = new List[imageNodes.size()];
    detectorIndexes = new HashMap[imageNodes.size()];
    zSteps = new Double[imageNodes.size()];
    tSteps = new Double[imageNodes.size()];
    pinholes = new Double[imageNodes.size()];
    zooms = new Double[imageNodes.size()];

    expTimes = new Double[imageNodes.size()][];
    gains = new Double[imageNodes.size()][];
    detectorOffsets = new Double[imageNodes.size()][];
    channelNames = new String[imageNodes.size()][];
    exWaves = new Double[imageNodes.size()][];
    imageROIs = new ROI[imageNodes.size()][];
    imageNames = new String[imageNodes.size()];

    core.clear();
    for (int i=0; i<imageNodes.size(); i++) {
      Element image = imageNodes.get(i);

      CoreMetadata ms = new CoreMetadata();
      core.add(ms);

      int index = core.size() - 1;
      setSeries(index);

      translateImageNames(image, index);
      translateImageNodes(image, index);
      translateAttachmentNodes(image, index);
      translateScannerSettings(image, index);
      translateFilterSettings(image, index);
      translateTimestamps(image, index);
      translateLaserLines(image, index);
      translateROIs(image, index);
      translateSingleROIs(image, index);
      translateDetectors(image, index);

      final Deque<String> nameStack = new ArrayDeque<String>();
      populateOriginalMetadata(image, nameStack);
      addUserCommentMeta(image, i);
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

  private void populateOriginalMetadata(Element root, Deque<String> nameStack) {
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
      final StringBuilder key = new StringBuilder();
      final Iterator<String> nameStackIterator = nameStack.descendingIterator();
      while (nameStackIterator.hasNext()) {
        final String k = nameStackIterator.next();
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
    final List<String> names = new ArrayList<String>();
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
      if (i > 0) imageNames[image] += '/';
    }
  }

  private void translateDetectors(Element imageNode, int image)
    throws FormatException
  {
    NodeList definitions = getNodes(imageNode, "ATLConfocalSettingDefinition");
    if (definitions == null) return;

    final List<String> channels = new ArrayList<String>();
    laserActive[image] = new ArrayList<Boolean>();
    int nextChannel = 0;
    for (int definition=0; definition<definitions.getLength(); definition++) {
      Element definitionNode = (Element) definitions.item(definition);
      String parentName = definitionNode.getParentNode().getNodeName();
      boolean isMaster = parentName.endsWith("Master");
      NodeList detectors = getNodes(definitionNode, "Detector");
      if (detectors == null) return;
      int count = 0;
      for (int d=0; d<detectors.getLength(); d++) {
        Element detector = (Element) detectors.item(d);
        NodeList multibands = null;
        if (!isMaster) {
          multibands = getNodes(definitionNode, "MultiBand");
        }

        String v = detector.getAttribute("Gain");
        Double gain =
          v == null || v.trim().isEmpty() ? null : new Double(v.trim());
        v = detector.getAttribute("Offset");
        Double offset =
          v == null || v.trim().isEmpty() ? null : new Double(v.trim());

        boolean active = "1".equals(detector.getAttribute("IsActive"));
        String c = detector.getAttribute("Channel");
        int channel = (c == null || c.trim().length() == 0) ? 0 : Integer.parseInt(c);
        if (active) {
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
                cutIns[image] = new ArrayList<PositiveFloat>();
              }
              Length in =
                FormatTools.getCutIn((double) Math.round(cutIn));
              if (in != null) {
                cutIns[image].add(in);
              }
            }
            if ((int) cutOut > 0) {
              if (cutOuts[image] == null) {
                cutOuts[image] = new ArrayList<PositiveFloat>();
              }
              Length out =
                FormatTools.getCutOut((double) Math.round(cutOut));
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
        } else {
          count++;
        }
        if (active && activeDetector[image] != null) {
          activeDetector[image].add(active);
        }
      }
      //Store values to check if actually it is active.
      if (!isMaster) {
        laserActive[image].add(count < detectors.getLength());
      }
    }

    if (channels != null && channelNames[image] != null) {
      for (int i=0; i<getEffectiveSizeC(); i++) {
        int index = i + channels.size() - getEffectiveSizeC();
        if (index >= 0 && index < channels.size()) {
          if (channelNames[image][i] == null ||
            channelNames[image][i].trim().isEmpty())
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
      if (type != null && !type.trim().isEmpty()) {
        roi.type = Integer.parseInt(type.trim());
      }
      String color = roiNode.getAttribute("color");
      if (color != null && !color.trim().isEmpty()) {
        roi.color = Long.parseLong(color.trim());
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
      try {
        if (linewidth != null && !linewidth.trim().isEmpty()) {
          roi.linewidth = Integer.parseInt(linewidth.trim());
        }
      }
      catch (NumberFormatException e) { }

      roi.text = roiNode.getAttribute("text");

      NodeList vertices = getNodes(roiNode, "Vertex");
      if (vertices == null) {
        continue;
      }

      for (int v=0; v<vertices.getLength(); v++) {
        Element vertex = (Element) vertices.item(v);
        String xx = vertex.getAttribute("x");
        String yy = vertex.getAttribute("y");

        if (xx != null && !xx.trim().isEmpty()) {
          roi.x.add(parseDouble(xx.trim()));
        }
        if (yy != null && !yy.trim().isEmpty()) {
          roi.y.add(parseDouble(yy.trim()));
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
      if (type != null && !type.trim().isEmpty()) {
        roi.type = Integer.parseInt(type.trim());
      }
      String color = roiNode.getAttribute("Color");
      if (color != null && !color.trim().isEmpty()) {
        roi.color = Long.parseLong(color.trim());
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

        if (xx != null && !xx.trim().isEmpty()) {
          roi.x.add(parseDouble(xx.trim()) / sizeX);
        }
        if (yy != null && !yy.trim().isEmpty()) {
          roi.y.add(parseDouble(yy.trim()) / sizeY);
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
    if (aotfLists == null || aotfLists.getLength() == 0) return;

    laserWavelength[image] = new ArrayList<Double>();
    laserIntensity[image] = new ArrayList<Double>();
    laserFrap[image] = new ArrayList<Boolean>();

    int baseIntensityIndex = 0;

    for (int channel=0; channel<aotfLists.getLength(); channel++) {
      Element aotf = (Element) aotfLists.item(channel);
      NodeList laserLines = getNodes(aotf, "LaserLineSetting");
      if (laserLines == null) return;
      String gpName = aotf.getParentNode().getParentNode().getNodeName();
      //might need parent for attachment
      boolean isMaster = gpName.endsWith("Sequential_Master") ||
              gpName.endsWith("Attachment");
      laserFrap[image].add(gpName.endsWith("FRAP_Master"));
      for (int laser=0; laser<laserLines.getLength(); laser++) {
        Element laserLine = (Element) laserLines.item(laser);
        
        if (isMaster) {
          continue;
        }
        String lineIndex = laserLine.getAttribute("LineIndex");
        String qual = laserLine.getAttribute("Qualifier");
        int index =
          lineIndex == null || lineIndex.trim().isEmpty() ? 0 :
          Integer.parseInt(lineIndex.trim());
        int qualifier =
          qual == null || qual.trim().isEmpty() ? 0:
          Integer.parseInt(qual.trim());

        index += (2 - (qualifier / 10));
        if (index < 0) {
            continue;
            //index = 0;
        }

        String v = laserLine.getAttribute("LaserLine");
        Double wavelength = 0d;
        if (v != null && !v.trim().isEmpty()) {
            wavelength = new Double(v.trim());
        }
        if (index < laserWavelength[image].size()) {
          laserWavelength[image].set(index, wavelength);
        }
        else {
          for (int i=laserWavelength[image].size(); i<index; i++) {
            laserWavelength[image].add(Double.valueOf(0));
          }
          laserWavelength[image].add(wavelength);
        }

        String intensity = laserLine.getAttribute("IntensityDev");
        double realIntensity =
          intensity == null || intensity.trim().isEmpty() ? 0d :
          new Double(intensity.trim());
        realIntensity = 100d - realIntensity;

        int realIndex = baseIntensityIndex + index;

        if (realIndex < laserIntensity[image].size()) {
          laserIntensity[image].set(realIndex, realIntensity);
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
    NodeList timeStampLists = getNodes(imageNode, "TimeStampList");
    if (timeStampLists == null) return;

    Element timeStampList = (Element)timeStampLists.item(0);
    timestamps[image] = new Double[getImageCount()];
    
    // probe if timestamps are saved in the format of LAS AF 3.1 or newer
    String numberOfTimeStamps = timeStampList.getAttribute("NumberOfTimeStamps");
    if (numberOfTimeStamps != null && !numberOfTimeStamps.isEmpty()) {
      // LAS AF 3.1 (or newer) timestamps are available
      String timeStampsRaw = timeStampList.getTextContent();
      List<String> timeStamps = Arrays.asList(timeStampsRaw.split(" "));
      for (int stamp = 0; stamp < timeStamps.size(); stamp++) {
        if (stamp < getImageCount()) {
          String timestamp = timeStamps.get(stamp);
          timestamps[image][stamp] = translateSingleTimestamp(timestamp);
        }
      }
    }
    else {
      // probe if timestamps are saved in the format of LAS AF 3.0 or older
      NodeList timestampNodes = getNodes(imageNode, "TimeStamp");
      if (timestampNodes != null) {
        // LAS AF 3.0 (or older) timestamps are available
        for (int stamp = 0; stamp < timestampNodes.getLength(); stamp++) {
          if (stamp < getImageCount()) {
            Element timestamp = (Element) timestampNodes.item(stamp);
            timestamps[image][stamp] = translateSingleTimestamp(timestamp);
          }
        }
      }
      else {
        return;
      }
    }
    
    acquiredDate[image] = timestamps[image][0];
  }

  private double translateSingleTimestamp(String timestamp) {
    timestamp = timestamp.trim();
    int stampLowStart = Math.max(0, timestamp.length() - 8);
    int stampHighEnd = Math.max(0, stampLowStart);
    String stampHigh = timestamp.substring(0, stampHighEnd);
    String stampLow = timestamp.substring(stampLowStart, timestamp.length());
    long high
            = stampHigh == null || stampHigh.trim().isEmpty() ? 0
                    : Long.parseLong(stampHigh.trim(), 16);
    long low
            = stampLow == null || stampLow.trim().isEmpty() ? 0
                    : Long.parseLong(stampLow.trim(), 16);
    long milliseconds = DateTools.getMillisFromTicks(high, low);
    double seconds = (double)milliseconds / 1000;
    return seconds;
  }
  
  private double translateSingleTimestamp(Element timestamp) {
    String stampHigh = timestamp.getAttribute("HighInteger");
    String stampLow = timestamp.getAttribute("LowInteger");
    long high
            = stampHigh == null || stampHigh.trim().isEmpty() ? 0
                    : Long.parseLong(stampHigh.trim());
    long low
            = stampLow == null || stampLow.trim().isEmpty() ? 0
                    : Long.parseLong(stampLow.trim());

    long milliseconds = DateTools.getMillisFromTicks(high, low);
    double seconds = (double)milliseconds / 1000;
    return seconds;
  }

  private void translateFilterSettings(Element imageNode, int image)
    throws FormatException
  {
    NodeList filterSettings = getNodes(imageNode, "FilterSettingRecord");
    if (filterSettings == null) return;

    activeDetector[image] = new ArrayList<Boolean>();
    cutIns[image] = new ArrayList<PositiveFloat>();
    cutOuts[image] = new ArrayList<PositiveFloat>();
    filterModels[image] = new ArrayList<String>();
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
        if (variant != null && !variant.trim().isEmpty()) {
          lensNA[image] = new Double(variant.trim());
        }
      }
      else if (attribute.equals("OrderNumber")) {
        if (variant != null && !variant.trim().isEmpty()) {
          serialNumber[image] = variant.trim();
        }
      }
      else if (objectClass.equals("CDetectionUnit")) {
        if (attribute.equals("State")) {
          int channel = getChannelIndex(filterSetting);
          if (channel < 0) continue;

          detectorIndexes[image].put(new Integer(data), object);
          activeDetector[image].add("Active".equals(variant.trim()));
        }
      }
      else if (attribute.equals("Objective")) {
        StringTokenizer tokens = new StringTokenizer(variant, " ");
        boolean foundMag = false;
        final StringBuilder model = new StringBuilder();
        while (!foundMag) {
          String token = tokens.nextToken();
          int x = token.indexOf('x');
          if (x != -1) {
            foundMag = true;

            String na = token.substring(x + 1);

            if (na != null && !na.trim().isEmpty()) {
              lensNA[image] = new Double(na.trim());
            }
            na = token.substring(0, x);
            if (na != null && !na.trim().isEmpty()) {
              magnification[image] = new Double(na.trim());
            }
          }
          else {
            model.append(token);
            model.append(" ");
          }
        }

        String immersion = "Other";
        if (tokens.hasMoreTokens()) {
          immersion = tokens.nextToken();
          if (immersion == null || immersion.trim().isEmpty()) {
            immersion = "Other";
          }
        }
        immersions[image] = immersion;

        String correction = "Other";
        if (tokens.hasMoreTokens()) {
          correction = tokens.nextToken();
          if (correction == null || correction.trim().isEmpty()) {
            correction = "Other";
          }
        }
        corrections[image] = correction;

        objectiveModels[image] = model.toString().trim();
      }
      else if (attribute.equals("RefractionIndex")) {
        if (variant != null && !variant.trim().isEmpty()) {
          refractiveIndex[image] = new Double(variant.trim());
        }
      }
      else if (attribute.equals("XPos")) {
        if (variant != null && !variant.trim().isEmpty()) {
          final Double number = Double.valueOf(variant.trim());
          posX[image] = new Length(number, UNITS.REFERENCEFRAME);
        }
      }
      else if (attribute.equals("YPos")) {
        if (variant != null && !variant.trim().isEmpty()) {
          final Double number = Double.valueOf(variant.trim());
          posY[image] = new Length(number, UNITS.REFERENCEFRAME);
        }
      }
      else if (attribute.equals("ZPos")) {
        if (variant != null && !variant.trim().isEmpty()) {
          final Double number = Double.valueOf(variant.trim());
          posZ[image] = new Length(number, UNITS.REFERENCEFRAME);
        }
      }
      else if (objectClass.equals("CSpectrophotometerUnit")) {
        Double v = null;
        try {
          v = Double.parseDouble(variant);
        }
        catch (NumberFormatException e) { }
        String description = filterSetting.getAttribute("Description");
        if (description.endsWith("(left)")) {
          filterModels[image].add(object);
          if (v != null && v > 0) {
            Length in = FormatTools.getCutIn(v);
            if (in != null) {
              cutIns[image].add(in);
            }
          }
        }
        else if (description.endsWith("(right)")) {
          if (v != null && v > 0) {
            Length out = FormatTools.getCutOut(v);
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
    exWaves[image] = new Double[getEffectiveSizeC()];
    detectorModels[image] = new ArrayList<String>();

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
        if (value != null && !value.trim().isEmpty()) {
          pinholes[image] = Double.parseDouble(value.trim()) * 1000000;
        }
      }
      else if (id.equals("dblZoom")) {
        if (value != null && !value.trim().isEmpty()) {
          zooms[image] = new Double(value.trim());
        }
      }
      else if (id.equals("dblStepSize")) {
        if (value != null && !value.trim().isEmpty()) {
          zSteps[image] = Double.parseDouble(value.trim()) * 1000000;
        }
      }
      else if (id.equals("nDelayTime_s")) {
        if (value != null && !value.trim().isEmpty()) {
          tSteps[image] = new Double(value.trim());
        }
      }
      else if (id.equals("CameraName")) {
        detectorModels[image].add(value);
      }
      else if (id.equals("eDirectional")) {
        addSeriesMeta("Reverse X orientation", "1".equals(value.trim()));
      }
      else if (id.equals("eDirectionalY")) {
        addSeriesMeta("Reverse Y orientation", "1".equals(value.trim()));
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
          if (value != null && !value.trim().isEmpty()) {
            expTimes[image][c] = new Double(value.trim());
          }
        }
        else if (id.endsWith("Gain")) {
          if (value != null && !value.trim().isEmpty()) {
            gains[image][c] = new Double(value.trim());
          }
        }
        else if (id.endsWith("WaveLength")) {
          if (value != null && !value.trim().isEmpty()) {
            Double exWave = new Double(value.trim());
            if (exWave > 0) {
              exWaves[image][c] = exWave;
            }
          }
        }
        // NB: "UesrDefName" is not a typo.
        else if ((id.endsWith("UesrDefName") || id.endsWith("UserDefName")) &&
          !value.equals("None"))
        {
          if (channelNames[image][c] == null ||
            channelNames[image][c].trim().isEmpty())
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
              final Double number = Double.valueOf(posX);
              fieldPosX.add(new Length(number, UNITS.REFERENCEFRAME));
            }
            catch (NumberFormatException e) {
              LOGGER.debug("", e);
              fieldPosX.add(null);
            }
          }
          if (posY != null) {
            try {
              final Double number = Double.valueOf(posY);
              fieldPosY.add(new Length(number, UNITS.REFERENCEFRAME));
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

  private void addUserCommentMeta(Element imageNode, int image)
    throws FormatException
  {
    NodeList attachmentNodes = getNodes(imageNode, "User-Comment");
    if (attachmentNodes == null) return;
    for (int i=0; i<attachmentNodes.getLength(); i++) {
      Node attachment = attachmentNodes.item(i);
      addSeriesMeta("User-Comment[" + i + "]", attachment.getTextContent());
      if (i == 0 && descriptions[image] == null) {
        descriptions[image] = attachment.getTextContent();
      }
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
      long bytes =
        bytesInc == null || bytesInc.trim().isEmpty() ? 0 :
            Long.parseLong(bytesInc.trim());
      if (bytes > 0) {
        bytesPerAxis.put(bytes, "C");
      }
    }

    int extras = 1;

    for (int dim=0; dim<dimensions.getLength(); dim++) {
      Element dimension = (Element) dimensions.item(dim);

      String v = dimension.getAttribute("DimID");
      int id = v == null || v.trim().isEmpty() ? 0 : Integer.parseInt(v.trim());
      v = dimension.getAttribute("NumberOfElements");
      int len = v == null || v.trim().isEmpty() ? 0 : Integer.parseInt(v.trim());
      v = dimension.getAttribute("BytesInc");
      long nBytes = v == null || v.trim().isEmpty() ? 0 : Long.parseLong(v.trim());
      v = dimension.getAttribute("Length");
      Double physicalLen;
      if (StringUtils.isBlank(v)) {
        physicalLen = 0d;
      } else {
        physicalLen = new Double(v.trim());
      }
      String unit = dimension.getAttribute("Unit");

      double offByOnePhysicalLen = 0d;
      if (len > 1) {
        offByOnePhysicalLen = physicalLen / len;
        physicalLen /= (len - 1);
      }
      else {
        physicalLen = 0d;
      }
      
      if (unit.equals("Ks")) {
        physicalLen /= 1000;
        offByOnePhysicalLen /= 1000;
      }
      else if (unit.equals("m")) {
        physicalLen *= 1000000;
        offByOnePhysicalLen *= 1000000;
      }

      boolean oldPhysicalSize = useOldPhysicalSizeCalculation();
      switch (id) {
        case 1: // X axis
          ms.sizeX = len;
          ms.rgb = (nBytes % 3) == 0;
          if (ms.rgb) nBytes /= 3;
          ms.pixelType =
            FormatTools.pixelTypeFromBytes((int) nBytes, false, true);
          physicalSizeX = oldPhysicalSize ? offByOnePhysicalLen : physicalLen;
          break;
        case 2: // Y axis
          if (ms.sizeY != 0) {
            if (ms.sizeZ == 1) {
              ms.sizeZ = len;
              bytesPerAxis.put(nBytes, "Z");
              physicalSizeZ = physicalLen;
            }
            else if (ms.sizeT == 1) {
              ms.sizeT = len;
              bytesPerAxis.put(nBytes, "T");
            }
          }
          else {
            ms.sizeY = len;
            physicalSizeY = oldPhysicalSize ? offByOnePhysicalLen : physicalLen;
          }
          break;
        case 3: // Z axis
          if (ms.sizeY == 0) {
            // XZ scan - swap Y and Z
            ms.sizeY = len;
            ms.sizeZ = 1;
            bytesPerAxis.put(nBytes, "Y");
            physicalSizeY = oldPhysicalSize ? offByOnePhysicalLen : physicalLen;
          }
          else {
            ms.sizeZ = len;
            bytesPerAxis.put(nBytes, "Z");
            physicalSizeZ = physicalLen;
          }
          break;
        case 4: // T axis
          if (ms.sizeY == 0) {
            // XT scan - swap Y and T
            ms.sizeY = len;
            ms.sizeT = 1;
            bytesPerAxis.put(nBytes, "Y");
            physicalSizeY = oldPhysicalSize ? offByOnePhysicalLen : physicalLen;
          }
          else {
            ms.sizeT = len;
            bytesPerAxis.put(nBytes, "T");
          }
          break;
        case 10: // tile axis
          tileCount[i] *= len;
          tileBytesInc[i] = nBytes;
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

    if (ms.sizeX == 0) ms.sizeX = 1;
    if (ms.sizeY == 0) ms.sizeY = 1;

    ms.interleaved = ms.rgb;
    ms.indexed = !ms.rgb;
    ms.imageCount = ms.sizeZ * ms.sizeT;
    if (!ms.rgb) ms.imageCount *= ms.sizeC;
    else {
      ms.imageCount *= (ms.sizeC / 3);
    }

    Long[] bytes = bytesPerAxis.keySet().toArray(new Long[0]);
    Arrays.sort(bytes);
    ms.dimensionOrder = "XY";
    if (getRGBChannelCount() == 1 || getRGBChannelCount() == getSizeC()) {
      if (getSizeC() > 1 && getSizeT() > 1) {
        ms.dimensionOrder += 'C';
      }
      for (Long nBytes : bytes) {
        String axis = bytesPerAxis.get(nBytes);
        if (ms.dimensionOrder.indexOf(axis) == -1) {
          ms.dimensionOrder += axis;
        }
      }
    }

    if (ms.dimensionOrder.indexOf('Z') == -1) {
      ms.dimensionOrder += 'Z';
    }
    if (ms.dimensionOrder.indexOf('C') == -1) {
      ms.dimensionOrder += 'C';
    }
    if (ms.dimensionOrder.indexOf('T') == -1) {
      ms.dimensionOrder += 'T';
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

    public List<Double> x = new ArrayList<Double>();
    public List<Double> y = new ArrayList<Double>();

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
          Length fontSize = FormatTools.getFontSize(size);
          if (fontSize != null) {
            store.setLabelFontSize(fontSize, roi, 0);
          }
        }
        catch (NumberFormatException e) { }
      }
      Length l = new Length((double) linewidth, UNITS.PIXEL);
      store.setLabelStrokeWidth(l, roi, 0);

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
          final StringBuilder points = new StringBuilder();
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
        x.set(i, coordinate);
      }

      for (int i=0; i<y.size(); i++) {
        double coordinate = y.get(i).doubleValue() * 1000000;
        coordinate *= 1;
        y.set(i, coordinate);
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
