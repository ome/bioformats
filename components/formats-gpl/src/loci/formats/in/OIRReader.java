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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import javax.xml.parsers.ParserConfigurationException;

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.common.xml.XMLTools;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;
import ome.xml.model.enums.Immersion;
import ome.xml.model.primitives.Color;
import ome.xml.model.primitives.Timestamp;
import ome.units.UNITS;
import ome.units.quantity.Length;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * OIRReader is the file format reader for Olympus .oir files.
 */
public class OIRReader extends FormatReader {

  // -- Constants --

  private static final String IDENTIFIER = "OLYMPUSRAWFORMAT";
  private static final int BUFFER_SIZE = 8192;

  // -- Fields --

  private HashMap<String, PixelBlock> pixelBlocks = new HashMap<String, PixelBlock>();
  private ArrayList<Channel> channels = new ArrayList<Channel>();
  private ArrayList<Laser> lasers = new ArrayList<Laser>();
  private ArrayList<Detector> detectors = new ArrayList<Detector>();
  private ArrayList<Objective> objectives = new ArrayList<Objective>();
  private Length physicalSizeX;
  private Length physicalSizeY;
  private Length physicalSizeZ;
  private Timestamp acquisitionDate;
  private int defaultXMLSkip = 36;
  private int blocksPerPlane = 0;
  private String[] pixelUIDs = null;
  private String baseName;
  private int lastChannel = -1;

  private int minZ = Integer.MAX_VALUE;
  private int minT = Integer.MAX_VALUE;

  // -- Constructor --

  /** Constructs a new OIR reader. */
  public OIRReader() {
    super("Olympus OIR", "oir");
    domains = new String[] {FormatTools.LM_DOMAIN};
    suffixNecessary = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    return isSingleFile(id) ? FormatTools.CAN_GROUP : FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    // files with .oir extension that are less than 1 GB should be self-contained
    Location file = new Location(id);
    return checkSuffix(id, "oir") && file.length() < 1024 * 1024 * 1024;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true ,1);

    HashSet<String> files = new HashSet<String>();
    for (String key : pixelBlocks.keySet()) {
      files.add(pixelBlocks.get(key).file);
    }
    String[] allFiles = files.toArray(new String[files.size()]);
    Arrays.sort(allFiles);
    return allFiles;
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int length = 16;
    if (!FormatTools.validStream(stream, length, true)) return false;
    return stream.readString(length).equals(IDENTIFIER);
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  @Override
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    int pixelType = getPixelType();
    if ((pixelType != FormatTools.INT8 && pixelType != FormatTools.UINT8) ||
      !isIndexed())
    {
      return null;
    }
    if (lastChannel < 0 || lastChannel >= channels.size()) {
      return null;
    }
    return (byte[][]) channels.get(lastChannel).lut;
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  @Override
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    int pixelType = getPixelType();
    if ((pixelType != FormatTools.INT16 && pixelType != FormatTools.UINT16) ||
      !isIndexed())
    {
      return null;
    }
    if (lastChannel < 0 || lastChannel >= channels.size()) {
      return null;
    }
    return (short[][]) channels.get(lastChannel).lut;
  }


  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int[] zct = getZCTCoords(no);
    lastChannel = zct[1];

    int first = 0;
    int startIndex = -1;
    int end = 0;
    while (first < pixelUIDs.length) {
      if (getZ(pixelUIDs[first]) - minZ == zct[0] &&
        getT(pixelUIDs[first]) - minT == zct[2] &&
        pixelUIDs[first].indexOf(channels.get(zct[1] % channels.size()).id) > 0)
      {
        if (startIndex < 0) {
          startIndex = first;
        }
        end = first;
      }
      first++;
    }

    if (startIndex < 0) {
      LOGGER.warn("No pixel blocks for plane #{}", no);
      Arrays.fill(buf, (byte) 0);
      return buf;
    }

    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int bufferOffset = bpp * ((y * getSizeX()) + x);
    int rowLen = bpp * w;
    int imageWidth = bpp * getSizeX();
    int bufferEnd = bpp * (((y + h) * getSizeX()) + x + w);
    int bufferPointer = 0;

    RandomAccessInputStream s = null;
    String openFile = null;
    try {
      for (int i=startIndex; i<=end; i++) {
        PixelBlock block = pixelBlocks.get(pixelUIDs[i]);

        if (bufferPointer + block.length < bufferOffset ||
          bufferPointer >= bufferEnd)
        {
          bufferPointer += block.length;
          continue;
        }

        byte[] pixels = null;
        if (s == null || !block.file.equals(openFile)) {
          if (s != null) {
            s.close();
          }
          s = new RandomAccessInputStream(block.file, BUFFER_SIZE);
          openFile = block.file;
        }
        pixels = readPixelBlock(s, block.offset);
        if (pixels != null) {
          int blockY = bufferPointer / imageWidth;
          int blockH = pixels.length / imageWidth;

          for (int yy=blockY; yy<blockY+blockH; yy++) {
            if (yy < y || yy >= y + h) {
              continue;
            }
            int blockOffset = (yy - blockY) * imageWidth + x * bpp;
            int bufOffset = (yy - y) * rowLen;
            System.arraycopy(pixels, blockOffset, buf, bufOffset, rowLen);
          }
        }
        bufferPointer += block.length;
      }
    }
    finally {
      if (s != null) {
        s.close();
      }
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      pixelBlocks.clear();
      channels.clear();
      lasers.clear();
      detectors.clear();
      objectives.clear();
      physicalSizeX = null;
      physicalSizeY = null;
      physicalSizeZ = null;
      acquisitionDate = null;
      defaultXMLSkip = 36;
      blocksPerPlane = 0;
      pixelUIDs = null;
      baseName = null;
      lastChannel = -1;
      minZ = Integer.MAX_VALUE;
      minT = Integer.MAX_VALUE;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    Location current = new Location(id).getAbsoluteFile();
    Location parent = current.getParentFile();
    ArrayList<String> extraFiles = new ArrayList<String>();
    if (parent != null) {
      String[] fileList = parent.list();
      Arrays.sort(fileList);
      String prefix = current.getName();
      if (prefix.indexOf(".") > 0) {
        prefix = prefix.substring(0, prefix.lastIndexOf("."));
      }
      else if (!checkSuffix(id, "oir")) {
        prefix = prefix.substring(0, prefix.lastIndexOf("_"));
      }

      for (String file : fileList) {
        // look for files ending with an underscore plus a 5 digit number
        if (file.startsWith(prefix + "_") && file.length() == prefix.length() + 6) {
          try {
            int index = Integer.parseInt(file.substring(file.lastIndexOf("_") + 1));
            if (index != extraFiles.size() + 1) {
              LOGGER.warn("Found file {} with index {}; expected index {}", file, index, extraFiles.size() + 1);
            }
            extraFiles.add(new Location(parent, file).getAbsolutePath());
          }
          catch (NumberFormatException e) {
          }
        }
        else if (file.startsWith(prefix) && checkSuffix(file, "oir")) {
          current = new Location(parent, file);
          currentId = current.getAbsolutePath();
        }
      }
    }

    CoreMetadata m = core.get(0);
    m.littleEndian = true;
    m.sizeZ = 1;
    m.sizeC = 1;
    m.sizeT = 1;
    m.indexed = true;
    m.falseColor = true;

    try (RandomAccessInputStream s = new RandomAccessInputStream(currentId, BUFFER_SIZE)) {
      readPixelsFile(current.getAbsolutePath(), s);
    }

    for (String file : extraFiles) {
      try (RandomAccessInputStream s = new RandomAccessInputStream(file, BUFFER_SIZE)) {
        readPixelsFile(file, s);
      }
    }

    m.sizeC *= channels.size();
    m.imageCount = getSizeC() * getSizeZ() * getSizeT();

    LOGGER.debug("blocks per plane = {}", blocksPerPlane);
    LOGGER.debug("number of pixel blocks = {}", pixelBlocks.size());

    if (blocksPerPlane * getImageCount() != pixelBlocks.size()) {
      // if blocks are missing, set the Z and T dimensions to match the last
      // plane for which at least one block exists
      int maxZ = 0;
      int maxT = 0;
      for (String uid : pixelBlocks.keySet()) {
        LOGGER.debug("  checking uid = {}", uid);
        int thisZ = getZ(uid);
        int thisT = getT(uid);
        int block = getBlock(uid);
        // only count through last full plane
        if (block == blocksPerPlane - 1) {
          if (thisZ > maxZ) {
            maxZ = thisZ;
          }
          if (thisT > maxT) {
            maxT = thisT;
          }
          if (thisZ < minZ) {
            minZ = thisZ;
          }
          if (thisT < minT) {
            minT = thisT;
          }
        }
      }
      m.sizeZ = (maxZ - minZ) + 1;
      m.sizeT = (maxT - minT) + 1;
      m.imageCount = getSizeC() * getSizeZ() * getSizeT();
    }
    else {
      minZ = 0;
      minT = 0;
    }

    m.dimensionOrder = "XYC";
    if (getSizeZ() == 1 || getSizeT() == 1) {
      m.dimensionOrder += "ZT";
    }
    else {
      int zIndex = baseName.toLowerCase().indexOf("z");
      int tIndex = baseName.toLowerCase().indexOf("t");
      if (zIndex < tIndex) {
        m.dimensionOrder += "TZ";
      }
      else {
        m.dimensionOrder += "ZT";
      }
    }

    pixelUIDs = pixelBlocks.keySet().toArray(new String[pixelBlocks.size()]);
    Arrays.sort(pixelUIDs, new Comparator<String>() {
      @Override
      public int compare(String s1, String s2) {
        int lastUnderscore1 = s1.lastIndexOf("_");
        int lastUnderscore2 = s2.lastIndexOf("_");

        Integer block1 = new Integer(s1.substring(lastUnderscore1 + 1));
        Integer block2 = new Integer(s2.substring(lastUnderscore2 + 1));

        int underscore1 = s1.lastIndexOf("_", lastUnderscore1 - 1);
        int underscore2 = s2.lastIndexOf("_", lastUnderscore2 - 1);

        String prefix1 = s1.substring(0, underscore1);
        String prefix2 = s2.substring(0, underscore2);

        String channel1 = s1.substring(underscore1 + 1, lastUnderscore1);
        String channel2 = s2.substring(underscore2 + 1, lastUnderscore2);

        if (!prefix1.equals(prefix2)) {
          return s1.compareTo(s2);
        }

        if (!channel1.equals(channel2)) {
          Integer index1 = -1;
          Integer index2 = -2;
          for (int i=0; i<channels.size(); i++) {
            if (channels.get(i).id.equals(channel1)) {
              index1 = i;
            }
            if (channels.get(i).id.equals(channel2)) {
              index2 = i;
            }
          }
          return index1.compareTo(index2);
        }

        return block1.compareTo(block2);
      }
    });

    if (LOGGER.isTraceEnabled()) {
      for (int i=0; i<pixelUIDs.length; i++) {
        LOGGER.trace("pixel UID #{} = {}", i, pixelUIDs[i]);
      }
    }

    // populate original metadata

    Hashtable<String, Object> tmpMeta = new Hashtable<String, Object>();
    addMeta("Creation date", acquisitionDate, tmpMeta);
    addMeta("Pixel Length X", physicalSizeX, tmpMeta);
    addMeta("Pixel Length Y", physicalSizeY, tmpMeta);
    addMeta("Z step", physicalSizeZ, tmpMeta);

    for (Channel channel : channels) {
      String prefix = "Channel " + channel.name + " ";
      addMetaList(prefix + "ID", channel.id, tmpMeta);
      addMetaList(prefix + "color", channel.color, tmpMeta);
      addMetaList(prefix + "pinhole", channel.pinhole, tmpMeta);
      addMetaList(prefix + "start wavelength", channel.excitation, tmpMeta);
      addMetaList(prefix + "end wavelength", channel.emission, tmpMeta);
      addMetaList(prefix + "linked laser index", channel.laserIndex, tmpMeta);
    }

    for (Objective objective : objectives) {
      addMetaList("Objective Lens name", objective.name, tmpMeta);
      addMetaList("Objective Lens magnification", objective.magnification, tmpMeta);
      addMetaList("Objective Lens na", objective.na, tmpMeta);
      addMetaList("Objective Lens wd", objective.wd, tmpMeta);
      addMetaList("Objective Lens refractive index", objective.ri, tmpMeta);
      addMetaList("Objective Lens immersion", objective.immersion, tmpMeta);
    }

    for (Laser laser : lasers) {
      String prefix = "Laser " + laser.name + " ";
      addMetaList(prefix + "ID", laser.id, tmpMeta);
      addMetaList(prefix + "data ID", laser.dataId, tmpMeta);
      addMetaList(prefix + "power", laser.power, tmpMeta);
      addMetaList(prefix + "transmissivity", laser.transmissivity, tmpMeta);
      addMetaList(prefix + "wavelength", laser.wavelength, tmpMeta);
    }

    for (Detector detector : detectors) {
      addMetaList("Detector ID", detector.id, tmpMeta);
      addMetaList("Detector linked channel ID", detector.channelId, tmpMeta);
      addMetaList("Detector voltage", detector.voltage, tmpMeta);
      addMetaList("Detector offset", detector.offset, tmpMeta);
      addMetaList("Detector gain", detector.gain, tmpMeta);
    }
    // this ensures that global metadata keys will be sorted before series keys
    MetadataTools.merge(tmpMeta, metadata, "- ");

    // populate MetadataStore

    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this);

    String instrumentID = MetadataTools.createLSID("Instrument", 0);
    store.setInstrumentID(instrumentID, 0);
    store.setImageInstrumentRef(instrumentID, 0);

    for (int i=0; i<lasers.size(); i++) {
      Laser l = lasers.get(i);
      String lsid = MetadataTools.createLSID("LightSource", 0, i);
      store.setLaserID(lsid, 0, i);
      store.setLaserModel(l.id, 0, i);
      if (l.wavelength != null) {
        store.setLaserWavelength(FormatTools.getWavelength(l.wavelength, null), 0, i);
      }
    }

    for (int i=0; i<detectors.size(); i++) {
      Detector detector = detectors.get(i);
      String lsid = MetadataTools.createLSID("Detector", 0, i);
      store.setDetectorID(lsid, 0, i);
      store.setDetectorOffset(detector.offset, 0, i);
      store.setDetectorVoltage(FormatTools.createElectricPotential(detector.voltage, UNITS.VOLT), 0, i);
      store.setDetectorGain(detector.gain, 0, i);
    }

    for (int i=0; i<objectives.size(); i++) {
      Objective objective = objectives.get(i);
      String lsid = MetadataTools.createLSID("Objective", 0, i);
      store.setObjectiveID(lsid, 0, i);
      store.setObjectiveModel(objective.name, 0, i);
      store.setObjectiveNominalMagnification(objective.magnification, 0, i);
      store.setObjectiveLensNA(objective.na, 0, i);
      store.setObjectiveWorkingDistance(FormatTools.createLength(objective.wd, UNITS.MILLIMETRE), 0, i);
      store.setObjectiveImmersion(objective.immersion, 0, i);

      if (i == 0) {
        store.setObjectiveSettingsID(lsid, 0);
        store.setObjectiveSettingsRefractiveIndex(objective.ri, 0);
      }
    }

    if (acquisitionDate != null) {
      store.setImageAcquisitionDate(acquisitionDate, 0);
    }

    if (physicalSizeX != null) {
      store.setPixelsPhysicalSizeX(physicalSizeX, 0);
    }
    if (physicalSizeY != null) {
      store.setPixelsPhysicalSizeY(physicalSizeY, 0);
    }
    if (physicalSizeZ != null) {
      store.setPixelsPhysicalSizeZ(physicalSizeZ, 0);
    }

    for (int c=0; c<channels.size(); c++) {
      Channel ch = channels.get(c);
      store.setChannelName(ch.name, 0, c);
      if (ch.color != null) {
        store.setChannelColor(ch.color, 0, c);
      }

      if (ch.pinhole != null) {
        store.setChannelPinholeSize(ch.pinhole, 0, c);
      }

      if (ch.emission != null) {
        store.setChannelEmissionWavelength(ch.emission, 0, c);
      }

      if (ch.excitation != null) {
        store.setChannelExcitationWavelength(ch.excitation, 0, c);
      }

      for (int d=0; d<detectors.size(); d++) {
        if (detectors.get(d).channelId.equals(ch.id)) {
          store.setDetectorSettingsID(MetadataTools.createLSID("Detector", 0, d), 0, c);
        }
      }

      if (ch.laserIndex >= 0 && ch.laserIndex < lasers.size()) {
        String laserId = MetadataTools.createLSID("LightSource", 0, ch.laserIndex);
        store.setChannelLightSourceSettingsID(laserId, 0, c);
      }
    }
  }

  // -- Helper methods --

  private void readPixelsFile(String file, RandomAccessInputStream s) throws FormatException, IOException {
    // pixel data is stored raw in multiple blocks
    // each block is preceded by a UID indicating the plane and block index, e.g.
    // z001t001_<channel ID #0>_0
    // z001t001_<channel ID #0>_1
    // z001t001_<channel ID #1>_0
    // ...
    // z010t010_<channel ID #1>_1
    //
    // blocks of XML are interspersed, which define the acquisition parameters
    // including image dimensions and color lookup tables

    LOGGER.info("Reading pixel blocks from {}", file);

    s.order(true);
    defaultXMLSkip = 36;

    long baseOffset = 16;
    s.seek(baseOffset);
    while (s.readInt() != 0xffffffff);
    s.skipBytes(4);

    long pixelStart = s.getFilePointer();
    // seek past reference image blocks
    while (skipPixelBlock(file, s, false));

    if (s.getFilePointer() == pixelStart && !file.endsWith(currentId)) {
      while (s.readInt() != 0xffffffff);
      s.skipBytes(4);
    }
    readXMLBlock(file, s);

    while (skipPixelBlock(file, s, true));

    readXMLBlock(file, s);

    while (s.getFilePointer() < s.length() - 16) {
      s.findString("<?xml");
      s.seek(s.getFilePointer() - 9);
      int length = s.readInt();
      if (length < 0 || length + s.getFilePointer() > s.length()) {
        break;
      }
      long fp = s.getFilePointer();
      String xml = s.readString(length);
      if (!xml.startsWith("<?xml")) {
        s.seek(fp - 2);
        continue;
      }
      LOGGER.trace("xml = {}", xml);
      if (((channels.size() == 0 || getSizeX() == 0 || getSizeY() == 0) &&
        file.endsWith(currentId)) || xml.indexOf("lut:LUT") > 0)
      {
        parseXML(s, xml, fp);
      }
      boolean expectPixelBlock = xml.endsWith(":frameProperties>");
      if (expectPixelBlock) {
        while (skipPixelBlock(file, s, true));
      }
    }
  }

  private void readXMLBlock(String file, RandomAccessInputStream s) throws FormatException, IOException {
    long offset = s.getFilePointer();
    LOGGER.debug("reading XML block from {}", offset);
    if (s.getFilePointer() + 8 >= s.length()) {
      return;
    }
    // total block length could include multiple strings of XML
    int totalBlockLength = s.readInt();
    long end = s.getFilePointer() + totalBlockLength - 4;
    s.skipBytes(4);

    while (s.getFilePointer() < end) {
      long start = s.getFilePointer();
      s.skipBytes(defaultXMLSkip);
      int xmlLength = s.readInt();
      if (xmlLength <= 32) {
        xmlLength = s.readInt();
        String uid = s.readString(xmlLength);
        xmlLength = s.readInt();
      }
      else if (xmlLength < 0 || xmlLength >= (s.length() - s.getFilePointer())) {
        s.seek(s.getFilePointer() - 40);
        xmlLength = s.readInt();
        if (xmlLength <= 0 || xmlLength + s.getFilePointer() > s.length()) {
          s.seek(start);
          xmlLength = s.readInt();
          if (xmlLength <= 0 || xmlLength + s.getFilePointer() > s.length()) {
            return;
          }
        }
        if (xmlLength > 1024) {
          return;
        }
        String uid = s.readString(xmlLength);
        xmlLength = s.readInt();
        if (xmlLength < 0 || xmlLength > totalBlockLength) {
          s.seek(offset + 4);
          int skipped = 0;
          while (s.readInt() != totalBlockLength - skipped) {
            skipped += 4;
            if (s.getFilePointer() + 4 >= s.length()) {
              s.seek(end);
              return;
            }
          }
          xmlLength = totalBlockLength - skipped;
          defaultXMLSkip = skipped - 4;
        }
      }

      if (xmlLength <= 32 || s.getFilePointer() + xmlLength > end + 8) {
        break;
      }
      long fp = s.getFilePointer();
      String xml = s.readString(xmlLength).trim();
      LOGGER.trace("xml = {}", xml);
      if (file.endsWith(currentId) || xml.indexOf("lut:LUT") > 0) {
        parseXML(s, xml, fp);
      }
    }
  }

  private void parseXML(RandomAccessInputStream s, String xml, long startFilePointer) throws FormatException, IOException {
    Element root = null;
    try {
      root = XMLTools.parseDOM(xml).getDocumentElement();
    }
    catch (ParserConfigurationException e) {
      LOGGER.debug("Could not parse XML", e);
      return;
    }
    catch (SAXException e) {
      LOGGER.debug("Could not parse XML", e);
      return;
    }

    if (root != null) {
      String name = root.getNodeName();
      if ("lsmimage:imageProperties".equals(name)) {
        parseImageProperties(root);
      }
      else if ("lsmframe:frameProperties".equals(name)) {
        parseFrameProperties(root);
      }
      else if ("lut:LUT".equals(name)) {
        long fp = s.getFilePointer();
        s.seek(startFilePointer - 44);
        int uidLength = s.readInt();
        String uid = s.readString(uidLength);
        s.seek(fp);

        parseLUT(root, uid);
      }
      parseOriginalMetadata(root);
    }
  }

  private void parseLUT(Element root, String uid) throws FormatException {
    Element linear = getFirstChild(root, "lut:linear");
    boolean isLinear = false;
    if (linear != null) {
      isLinear = Boolean.valueOf(linear.getTextContent());
    }

    Element data = getFirstChild(root, "lut:data");
    if (data != null) {
      String lutContent = data.getTextContent();
      for (int i=0; i<channels.size(); i++) {
        if (channels.get(i).lut == null && channels.get(i).id.equals(uid)) {
          if (getPixelType() == FormatTools.UINT8) {
            channels.get(i).lut = new byte[3][lutContent.length() / 8];
          }
          else if (getPixelType() == FormatTools.UINT16) {
            channels.get(i).lut = new short[3][lutContent.length() / 8];
          }
          else {
            LOGGER.warn("Skipping LUTs for pixel type {}", getPixelType());
            return;
          }

          for (int q=0; q<lutContent.length(); q+=8) {
            int r = Integer.parseInt(lutContent.substring(q, q + 2), 16);
            int g = Integer.parseInt(lutContent.substring(q + 2, q + 4), 16);
            int b = Integer.parseInt(lutContent.substring(q + 4, q + 6), 16);
            if (channels.get(i).lut instanceof byte[][]) {
              ((byte[][]) channels.get(i).lut)[0][q / 8] = (byte) (r & 0xff);
              ((byte[][]) channels.get(i).lut)[1][q / 8] = (byte) (g & 0xff);
              ((byte[][]) channels.get(i).lut)[2][q / 8] = (byte) (b & 0xff);
            }
            else if (channels.get(i).lut instanceof short[][]) {
              ((short[][]) channels.get(i).lut)[0][q / 8] = (short) ((r & 0xffff) * 256);
              ((short[][]) channels.get(i).lut)[1][q / 8] = (short) ((g & 0xffff) * 256);
              ((short[][]) channels.get(i).lut)[2][q / 8] = (short) ((b & 0xffff) * 256);
            }
            if (isLinear && q == lutContent.length() - 8) {
              channels.get(i).color = new Color(r, g, b, 255);
            }
          }
          break;
        }
      }
    }
  }

  private void parseFrameProperties(Element root) throws FormatException {
    CoreMetadata m = core.get(0);

    Element imageDefinition = getFirstChild(root, "commonframe:imageDefinition");
    if (imageDefinition != null) {
      Element width = getFirstChild(imageDefinition, "base:width");
      Element height = getFirstChild(imageDefinition, "base:height");
      Element depth = getFirstChild(imageDefinition, "base:depth");
      Element bitCount = getFirstChild(imageDefinition, "base:bitCounts");

      if (width != null) {
        m.sizeX = Integer.parseInt(width.getTextContent());
      }
      if (height != null) {
        m.sizeY = Integer.parseInt(height.getTextContent());
      }
      if (depth != null) {
        int bytes = Integer.parseInt(depth.getTextContent());
        m.pixelType = FormatTools.pixelTypeFromBytes(bytes, false, false);
      }
      if (bitCount != null) {
        m.bitsPerPixel = Integer.parseInt(bitCount.getTextContent());
      }
    }

    Element general = getFirstChild(root, "commonframe:general");
    if (general != null) {
      Element name = getFirstChild(general, "base:name");
      if (name != null) {
        baseName = name.getTextContent();
      }
    }
  }

  private void parseImageProperties(Element root) throws FormatException {
    CoreMetadata m = core.get(0);

    Element general = getFirstChild(root, "commonimage:general");
    if (general != null) {
      Element creationDate = getFirstChild(general, "base:creationDateTime");
      if (creationDate != null) {
        String date = creationDate.getTextContent();
        acquisitionDate = new Timestamp(date);
      }
    }

    Element lsm = getFirstChild(root, "commonimage:lsm");
    if (lsm != null) {
      NodeList laserNodes = lsm.getElementsByTagName("commonimage:laser");
      for (int i=0; i<laserNodes.getLength(); i++) {
        Element laser = (Element) laserNodes.item(i);
        Element idNode = getFirstChild(laser, "commonimage:id");
        Element nameNode = getFirstChild(laser, "commonimage:name");
        Laser l = new Laser();
        if (idNode != null) {
          l.id = idNode.getTextContent();
        }
        if (nameNode != null) {
          l.name = nameNode.getTextContent();
        }
        lasers.add(l);
      }
    }

    Element imageInfo = getFirstChild(root, "commonimage:imageInfo");
    if (imageInfo != null) {
      Element width = getFirstChild(imageInfo, "commonimage:width");
      Element height = getFirstChild(imageInfo, "commonimage:height");

      if (width != null && getSizeX() == 0) {
        m.sizeX = Integer.parseInt(width.getTextContent());
      }
      if (height != null && getSizeY() == 0) {
        m.sizeY = Integer.parseInt(height.getTextContent());
      }

      NodeList axisNodes = imageInfo.getElementsByTagName("commonimage:axis");
      if (axisNodes != null) {
        for (int i=0; i<axisNodes.getLength(); i++) {
          parseAxis((Element) axisNodes.item(i));
        }
      }

      NodeList channelNodes = imageInfo.getElementsByTagName("commonphase:channel");
      for (int i=0; i<channelNodes.getLength(); i++) {
        Channel c = new Channel();
        Element channelNode = (Element) channelNodes.item(i);

        c.id = channelNode.getAttribute("id");
        int index = Integer.parseInt(channelNode.getAttribute("order")) - 1;

        Element name = getFirstChild(channelNode, "commonphase:name");
        if (name != null) {
          c.name = name.getTextContent();
        }

        Element pinhole = getFirstChild(channelNode, "fvCommonphase:pinholeDiameter");
        if (pinhole != null) {
          Double pinholeSize = DataTools.parseDouble(pinhole.getTextContent());
          if (pinholeSize != null) {
            c.pinhole = new Length(pinholeSize, UNITS.MICROMETER);
          }
        }

        Element startWavelength = getFirstChild(channelNode, "opticalelement:startWavelength");
        Element endWavelength = getFirstChild(channelNode, "opticalelement:endWavelength");
        if (startWavelength != null) {
          Double wave = DataTools.parseDouble(startWavelength.getTextContent());
          if (wave != null) {
            c.excitation = FormatTools.getExcitationWavelength(wave);
          }
        }
        if (endWavelength != null) {
          Double wave = DataTools.parseDouble(endWavelength.getTextContent());
          if (wave != null) {
            c.emission = FormatTools.getEmissionWavelength(wave);
          }
        }

        Element imageDefinition = getFirstChild(channelNode, "commonphase:imageDefinition");
        if (imageDefinition != null) {
          Element depth = getFirstChild(imageDefinition, "commonphase:depth");
          Element bitCount = getFirstChild(imageDefinition, "commonphase:bitCounts");

          if (depth != null) {
            int bytes = Integer.parseInt(depth.getTextContent());
            m.pixelType = FormatTools.pixelTypeFromBytes(bytes, false, false);
          }
          if (bitCount != null) {
            m.bitsPerPixel = Integer.parseInt(bitCount.getTextContent());
          }
        }

        Element length = getFirstChild(channelNode, "commonphase:length");
        Element pixelUnit = getFirstChild(channelNode, "commonphase:pixelUnit");
        if (length != null) {
          Element xLength = getFirstChild(length, "commonparam:x");
          Element xUnit = getFirstChild(pixelUnit, "commonphase:x");

          if (xLength != null) {
            Double x = DataTools.parseDouble(xLength.getTextContent());
            String unit = null;
            if (xUnit != null) {
              unit = xUnit.getTextContent();
            }
            physicalSizeX = FormatTools.getPhysicalSize(x, unit);
          }

          Element yLength = getFirstChild(length, "commonparam:y");
          Element yUnit = getFirstChild(pixelUnit, "commonphase:y");

          if (yLength != null) {
            Double y = DataTools.parseDouble(yLength.getTextContent());
            String unit = null;
            if (yUnit != null) {
              unit = yUnit.getTextContent();
            }
            physicalSizeY = FormatTools.getPhysicalSize(y, unit);
          }

          Element zLength = getFirstChild(length, "commonparam:z");
          Element zUnit = getFirstChild(pixelUnit, "commonphase:z");

          if (zLength != null) {
            Double z = DataTools.parseDouble(zLength.getTextContent());
            String unit = null;
            if (zUnit != null) {
              unit = zUnit.getTextContent();
            }
            physicalSizeZ = FormatTools.getPhysicalSize(z, unit);
          }
        }

        while (index > channels.size()) {
          channels.add(null);
        }
        if (index == channels.size()) {
          channels.add(c);
        }
        else {
          channels.set(index, c);
        }
      }
    }

    for (int i=0; i<channels.size(); i++) {
      if (channels.get(i) == null) {
        channels.remove(i);
        i--;
      }
    }

    Element acquisition = getFirstChild(root, "commonimage:acquisition");
    if (acquisition == null) {
      acquisition = getFirstChild(root, "lsmimage:acquisition");
    }
    if (acquisition != null) {
      Element microscopeConfiguration = getFirstChild(acquisition, "commonimage:microscopeConfiguration");
      if (microscopeConfiguration != null) {
        NodeList objectiveLenses = microscopeConfiguration.getElementsByTagName("commonimage:objectiveLens");

        if (objectiveLenses != null) {
          for (int i=0; i<objectiveLenses.getLength(); i++) {
            Element lens = (Element) objectiveLenses.item(i);
            Objective objective = new Objective();

            Element lensName = getFirstChild(lens, "opticalelement:displayName");
            Element magnification = getFirstChild(lens, "opticalelement:magnification");
            Element na = getFirstChild(lens, "opticalelement:naValue");
            Element wd = getFirstChild(lens, "opticalelement:wdValue");
            Element refraction = getFirstChild(lens, "opticalelement:refraction");
            Element immersion = getFirstChild(lens, "opticalelement:immersion");

            if (lensName != null) {
              objective.name = lensName.getTextContent();
            }
            if (magnification != null) {
              objective.magnification = DataTools.parseDouble(magnification.getTextContent());
            }
            if (na != null) {
              objective.na = DataTools.parseDouble(na.getTextContent());
            }
            if (wd != null) {
              objective.wd = DataTools.parseDouble(wd.getTextContent());
            }
            if (refraction != null) {
              objective.ri = DataTools.parseDouble(refraction.getTextContent());
            }
            if (immersion != null) {
              objective.immersion = getImmersion(immersion.getTextContent());
            }

            objectives.add(objective);
          }
        }
      }


      Element imagingParam = getFirstChild(acquisition, "commonimage:imagingParam");
      if (imagingParam == null) {
        imagingParam = getFirstChild(acquisition, "lsmimage:imagingParam");
      }

      if (imagingParam != null) {
        NodeList axes = imagingParam.getElementsByTagName("commonparam:axis");

        if (axes != null) {
          for (int i=0; i<axes.getLength(); i++) {
            Element dimensionAxis = (Element) axes.item(i);
            if (dimensionAxis.hasAttribute("enable") &&
              dimensionAxis.getAttribute("enable").equals("true") &&
              (!dimensionAxis.hasAttribute("paramEnable") ||
              dimensionAxis.getAttribute("paramEnable").equals("true")))
            {
              parseAxis(dimensionAxis);
            }
          }
        }

        NodeList pmts = imagingParam.getElementsByTagName("lsmparam:pmt");
        if (pmts != null) {
          for (int i=0; i<pmts.getLength(); i++) {
            Element pmt = (Element) pmts.item(i);
            Detector detector = new Detector();

            detector.id = pmt.getAttribute("detectorId");
            detector.channelId = pmt.getAttribute("channelId");

            Element voltage = getFirstChild(pmt, "lsmparam:voltage");
            Element offset = getFirstChild(pmt, "lsmparam:offset");
            Element gain = getFirstChild(pmt,  "lsmparam:gain");

            if (voltage != null) {
              detector.voltage = DataTools.parseDouble(voltage.getTextContent());
            }
            if (offset != null) {
              detector.offset = DataTools.parseDouble(offset.getTextContent());
            }
            if (gain != null) {
              detector.gain = DataTools.parseDouble(gain.getTextContent());
            }

            detectors.add(detector);
          }
        }

        NodeList mainLasers = imagingParam.getElementsByTagName("lsmparam:mainLaser");
        if (mainLasers != null) {
          for (int i=0; i<mainLasers.getLength(); i++) {
            Element mainLaser = (Element) mainLasers.item(i);

            String id = mainLaser.getAttribute("laserDataId");
            Laser currentLaser = null;
            for (int laser=0; laser<lasers.size(); laser++) {
              if (id.startsWith(lasers.get(laser).id)) {
                currentLaser = lasers.get(laser);
                break;
              }
            }
            if (currentLaser == null) {
              continue;
            }

            currentLaser.dataId = id;

            Element power = getFirstChild(mainLaser, "commonparam:power");
            Element transmissivity = getFirstChild(mainLaser, "commonparam:transmissivity");

            if (power != null) {
              currentLaser.power = DataTools.parseDouble(power.getTextContent());
            }
            if (transmissivity != null) {
              currentLaser.transmissivity = DataTools.parseDouble(transmissivity.getTextContent());
            }
          }
        }

        Element resolution = getFirstChild(imagingParam, "commonparam:pixelResolution");
        if (resolution != null) {
          Element x = getFirstChild(resolution, "commonparam:x");
          Element y = getFirstChild(resolution, "commonparam:y");
          Element z = getFirstChild(resolution, "commonparam:z");

          if (x != null && physicalSizeX == null) {
            Double xValue = DataTools.parseDouble(x.getTextContent());
            physicalSizeX = FormatTools.getPhysicalSize(xValue, null);
          }
          if (y != null && physicalSizeY == null) {
            Double yValue = DataTools.parseDouble(y.getTextContent());
            physicalSizeY = FormatTools.getPhysicalSize(yValue, null);
          }
          if (z != null && physicalSizeZ == null) {
            Double zValue = DataTools.parseDouble(z.getTextContent());
            physicalSizeZ = FormatTools.getPhysicalSize(zValue, null);
          }
        }

      }

      NodeList imagingMainLasers = acquisition.getElementsByTagName("lsmimage:imagingMainLaser");
      if (imagingMainLasers != null) {
        for (int i=0; i<imagingMainLasers.getLength(); i++) {
          Element mainLaser = (Element) imagingMainLasers.item(i);

          String id = mainLaser.getAttribute("id");
          String enable = mainLaser.getAttribute("enable");

          if ("true".equals(enable)) {
            Element wavelength = getFirstChild(mainLaser, "commonimage:wavelength");
            if (wavelength != null) {
              for (Laser l : lasers) {
                if (id.equals(l.dataId)) {
                  l.wavelength = DataTools.parseDouble(wavelength.getTextContent());
                }
              }
            }
          }
        }
      }

      NodeList channelLinkages = acquisition.getElementsByTagName("commonphase:channel");

      // check if each existing channel has at least one pixel block associated
      // if not, clear and re-populate the channel list
      for (int c=0; c<channels.size(); c++) {
        String id = channels.get(c).id;
        boolean hasUID = false;
        for (String uid : pixelBlocks.keySet()) {
          if (uid.indexOf(id) >= 0) {
            hasUID = true;
            break;
          }
        }
        if (!hasUID) {
          channels.remove(c);
          c--;
        }
      }

      boolean appendChannels = channels.size() == 0;
      if (channelLinkages != null && channelLinkages.getLength() > 0) {
        for (int i=0; i<channelLinkages.getLength(); i++) {
          Element channel = (Element) channelLinkages.item(i);
          parseChannel(channel, appendChannels);
        }
      }
      else {
        // so far seems to only be needed for the oldest (software version 1.2.x) files
        channelLinkages = acquisition.getElementsByTagName("lsmimage:channel");
        for (int i=0; i<channelLinkages.getLength(); i++) {
          Element channel = (Element) channelLinkages.item(i);
          parseChannel(channel, appendChannels);
        }
      }
    }
  }

  private void parseChannel(Element channel, boolean appendChannels) {
    if (channel.hasAttribute("enable") && !channel.getAttribute("enable").equals("true")) {
      return;
    }
    String id = channel.getAttribute("id");
    NodeList laserIds = channel.getElementsByTagName("lsmimage:laserDataId");
    if (laserIds == null) {
      laserIds = channel.getElementsByTagName("fvLsmimage:laserDataId");
    }
    // do not link the laser to the channel if multiple linkages are present (e.g. lambda)
    if ((laserIds == null || laserIds.getLength() > 1) && !appendChannels) {
      return;
    }

    String type = channel.getAttribute("xsi:type");
    if (type != null && !type.endsWith("NormalChannel")) {
      return;
    }
    Element laserId = (Element) laserIds.item(0);
    Element name = getFirstChild(channel, "commonimage:name");
    String channelName = null;
    if (name != null) {
      channelName = name.getTextContent();
    }

    if (id != null) {
      boolean foundChannel = false;
      for (Channel ch : channels) {
        if (ch.id.equals(id)) {
          foundChannel = true;
          if (laserId != null) {
            for (int l=0; l<lasers.size(); l++) {
              Laser laser = lasers.get(l);
              if (laser.dataId.equals(laserId.getTextContent())) {
                ch.laserIndex = l;
                break;
              }
            }
          }
        }
      }
      if (!foundChannel && appendChannels) {
        Channel c = new Channel();
        c.id = id;
        c.name = channelName;
        if (laserId != null) {
          for (int l=0; l<lasers.size(); l++) {
            Laser laser = lasers.get(l);
            if (laser.dataId.equals(laserId.getTextContent())) {
              c.laserIndex = l;
              break;
            }
          }
        }
        channels.add(c);
      }
    }
  }

  private void parseAxis(Element dimensionAxis) {
    CoreMetadata m = core.get(0);
    Element axis = getFirstChild(dimensionAxis, "commonparam:axis");
    Element size = getFirstChild(dimensionAxis, "commonparam:maxSize");
    Element start = getFirstChild(dimensionAxis, "commonparam:startPosition");
    Element end = getFirstChild(dimensionAxis, "commonparam:endPosition");
    Element step = getFirstChild(dimensionAxis, "commonparam:step");

    if (axis != null && size != null) {
      String name = axis.getTextContent();

      if (name.equals("ZSTACK")) {
        if (m.sizeZ <= 1) {
          m.sizeZ = Integer.parseInt(size.getTextContent());
        }
      }
      else if (name.equals("TIMELAPSE")) {
        if (m.sizeT <= 1) {
          m.sizeT = Integer.parseInt(size.getTextContent());
        }
      }
      else if (name.equals("LAMBDA")) {
        m.sizeC = Integer.parseInt(size.getTextContent());
        m.moduloC.type = FormatTools.SPECTRA;
        m.moduloC.start = DataTools.parseDouble(start.getTextContent());
        m.moduloC.step = DataTools.parseDouble(step.getTextContent());
        // calculate the end point, as the stored value may be too large
        m.moduloC.end = m.moduloC.start + m.moduloC.step * (m.sizeC - 1);
      }
      else {
        LOGGER.warn("Unhandled axis '{}'", name);
      }
    }
  }

  private void parseOriginalMetadata(Node root) {
    String value = root.getNodeValue();
    if (value != null && value.trim().length() > 0) {
      value = value.trim();
      String key = "";

      Node parent = root.getParentNode();
      if (parent != null) {
        String name = parent.getNodeName();
        key = name.substring(name.indexOf(":") + 1);
      }
      Node grandparent = parent.getParentNode();
      if (grandparent != null) {
        String name = grandparent.getNodeName();
        name = name.substring(name.indexOf(":") + 1);
        key = name + " " + key;
      }
      addSeriesMetaList(key, value);
    }
    else {
      NodeList children = root.getChildNodes();
      for (int i=0; i<children.getLength(); i++) {
        parseOriginalMetadata(children.item(i));
      }
    }
  }

  private Element getFirstChild(Element root, String tag) {
    if (root == null || tag == null) {
      return null;
    }
    NodeList list = root.getElementsByTagName(tag);
    if (list == null || list.getLength() == 0) {
      return null;
    }
    return (Element) list.item(0);
  }

  private boolean skipPixelBlock(String currentFile, RandomAccessInputStream s, boolean store) throws IOException {
    long offset = s.getFilePointer();
    LOGGER.debug("reading pixel block from {}", offset);
    if (offset + 8 >= s.length()) {
      return false;
    }
    int checkLength = s.readInt();
    int check = s.readInt();
    if (check != 3) {
      s.seek(offset);
      if (check == 2) {
        s.seek(offset + checkLength + 8);
        return true;
      }
      return false;
    }

    s.skipBytes(8);
    int uidLength = s.readInt();
    if (checkLength != uidLength + 12) {
      s.seek(offset);
      return false;
    }
    String uid = s.readString(uidLength);
    if (store) {
      LOGGER.debug("pixel uid = {} @ {}", uid, offset);
    }
    if (s.getFilePointer() + 4 >= s.length()) {
      return false;
    }
    int pixelBytes = s.readInt();
    s.skipBytes(4);

    if (store && pixelBytes > 0) {
      PixelBlock block = new PixelBlock();
      block.file = currentFile;
      block.offset = offset;
      block.length = pixelBytes;
      pixelBlocks.put(uid, block);
      int blockIndex = Integer.parseInt(uid.substring(uid.lastIndexOf("_") + 1));
      if (blockIndex >= blocksPerPlane) {
        blocksPerPlane = blockIndex + 1;
      }
      LOGGER.debug("added pixel block @ {}, size = {}", offset, pixelBlocks.size());
    }
    else if (pixelBytes <= 0) {
      return false;
    }
    s.skipBytes(pixelBytes);
    return true;
  }

  private byte[] readPixelBlock(RandomAccessInputStream s, long offset) throws IOException {
    s.order(true);
    s.seek(offset);

    int checkLength = s.readInt();
    int check = s.readInt();
    if (check != 3) {
      s.seek(offset);
      return null;
    }

    s.skipBytes(8); // currently unknown

    int uidLength = s.readInt();
    String uid = s.readString(uidLength);
    LOGGER.debug("reading pixel block with uid = {}", uid);
    if (checkLength != uidLength + 12) {
      s.seek(offset);
      return null;
    }

    int pixelBytes = s.readInt();

    s.skipBytes(4); // currently unknown

    byte[] pixels = new byte[pixelBytes];
    s.readFully(pixels);
    return pixels;
  }

  private int getZ(String uid) {
    int zIndex = uid.indexOf("z");
    if (zIndex < 0) {
      return 0;
    }
    // assumes 3 digits
    return Integer.parseInt(uid.substring(zIndex + 1, zIndex + 4)) - 1;
  }

  private int getT(String uid) {
    int tIndex = uid.indexOf("t");
    if (tIndex < 0) {
      return 0;
    }
    // assumes 3 digits
    return Integer.parseInt(uid.substring(tIndex + 1, tIndex + 4)) - 1;
  }

  private int getBlock(String uid) {
    int index = uid.lastIndexOf("_");
    if (index < 0) {
      return 0;
    }
    return Integer.parseInt(uid.substring(index + 1));
  }

  // -- Helper classes --

  class Channel {
    public String id;
    public String name;
    public int laserIndex = -1;
    public Object lut;
    public Color color;
    public Length pinhole;
    public Length excitation;
    public Length emission;
  }

  class Laser {
    public String id;
    public String dataId;
    public String name;
    public Double power;
    public Double transmissivity;
    public Double wavelength;
  }

  class Detector {
    public String id;
    public String channelId;
    public Double voltage;
    public Double offset;
    public Double gain;
  }

  class Objective {
    public String name;
    public Double magnification;
    public Double na;
    public Double wd;
    public Double ri;
    public Immersion immersion;
  }

  class PixelBlock {
    public String file;
    public long offset;
    public int length;
  }

}
