/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
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

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import loci.common.DataTools;
import loci.common.DateTools;
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

import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.PositiveInteger;
import ome.xml.model.primitives.Timestamp;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * PrairieReader is the file format reader for
 * Prairie Technologies' TIFF variant.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/PrairieReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/PrairieReader.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class PrairieReader extends FormatReader {

  // -- Constants --

  public static final String[] CFG_SUFFIX = {"cfg"};
  public static final String[] XML_SUFFIX = {"xml"};
  public static final String[] PRAIRIE_SUFFIXES = {"cfg", "xml"};

  // Private tags present in Prairie TIFF files
  // IMPORTANT NOTE: these are the same as Metamorph's private tags - therefore,
  //                 it is likely that Prairie TIFF files will be incorrectly
  //                 identified unless the XML or CFG file is specified
  private static final int PRAIRIE_TAG_1 = 33628;
  private static final int PRAIRIE_TAG_2 = 33629;
  private static final int PRAIRIE_TAG_3 = 33630;

  // -- Fields --

  /** List of files in the current dataset */
  private String[] files;

  /** Helper reader for opening images */
  private TiffReader tiff;

  /** Names of the associated XML files */
  private String xmlFile, cfgFile;

  private Vector<String> f, gains, offsets;
  private double pixelSizeX, pixelSizeY;
  private String date, laserPower;

  private String microscopeModel;
  private String objectiveManufacturer;
  private PositiveInteger magnification;
  private String immersion;
  private Double lensNA;
  private Double waitTime;

  private Vector<Double> positionX = new Vector<Double>();
  private Vector<Double> positionY = new Vector<Double>();
  private Vector<Double> positionZ = new Vector<Double>();
  private Vector<String> channels = new Vector<String>();

  private boolean invertX, invertY;

  private Hashtable<String, Double> relativeTimes =
    new Hashtable<String, Double>();

  private Double zoom;

  private boolean timeSeries = false;

  // -- Constructor --

  /** Constructs a new Prairie TIFF reader. */
  public PrairieReader() {
    super("Prairie TIFF", new String[] {"tif", "tiff", "cfg", "xml"});
    domains = new String[] {FormatTools.LM_DOMAIN};
    hasCompanionFiles = true;
    datasetDescription = "One .xml file, one .cfg file, and one or more " +
      ".tif/.tiff files";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    if (!open) return false; // not allowed to touch the file system

    Location file = new Location(name).getAbsoluteFile();
    Location parent = file.getParentFile();

    String prefix = file.getName();
    if (prefix.indexOf(".") != -1) {
      prefix = prefix.substring(0, prefix.lastIndexOf("."));
    }

    if (checkSuffix(name, CFG_SUFFIX)) {
      if (prefix.lastIndexOf("Config") == -1) return false;
      prefix = prefix.substring(0, prefix.lastIndexOf("Config"));
    }

    // check for appropriately named XML file

    Location xml = new Location(parent, prefix + ".xml");
    while (!xml.exists() && prefix.indexOf("_") != -1) {
      prefix = prefix.substring(0, prefix.lastIndexOf("_"));
      xml = new Location(parent, prefix + ".xml");
    }

    boolean validXML = false;
    try {
      RandomAccessInputStream xmlStream =
        new RandomAccessInputStream(xml.getAbsolutePath());
      validXML = isThisType(xmlStream);
      xmlStream.close();
    }
    catch (IOException e) {
      LOGGER.trace("Failed to check XML file's type", e);
    }

    return xml.exists() && super.isThisType(name, false) && validXML;
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = (int) Math.min(1048608, stream.length());
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    String s = stream.readString(blockLen);
    if (s.indexOf("xml") != -1 && s.indexOf("PV") != -1) return true;

    TiffParser tp = new TiffParser(stream);
    IFD ifd = tp.getFirstIFD();
    if (ifd == null) return false;
    String software = null;
    try {
      software = ifd.getIFDStringValue(IFD.SOFTWARE);
    }
    catch (FormatException exc) {
      return false; // no software tag, or tag is wrong type
    }
    if (software == null) return false;
    if (software.indexOf("Prairie") < 0) return false; // not Prairie software
    return ifd.containsKey(new Integer(PRAIRIE_TAG_1)) &&
      ifd.containsKey(new Integer(PRAIRIE_TAG_2)) &&
      ifd.containsKey(new Integer(PRAIRIE_TAG_3));
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    if (noPixels) {
      return new String[] {xmlFile, cfgFile};
    }
    Vector<String> s = new Vector<String>();
    if (files != null) {
      for (String file : files) {
        s.add(file);
      }
    }
    if (xmlFile != null) s.add(xmlFile);
    if (cfgFile != null) s.add(cfgFile);
    return s.toArray(new String[s.size()]);
  }

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    return tiff.getOptimalTileWidth();
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    return tiff.getOptimalTileHeight();
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
    tiff.setId(files[no]);
    return tiff.openBytes(0, buf, x, y, w, h);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (tiff != null) tiff.close(fileOnly);
    if (!fileOnly) {
      xmlFile = cfgFile = null;
      tiff = null;
      files = null;
      f = gains = offsets = null;
      pixelSizeX = pixelSizeY = 0;
      date = laserPower = null;
      microscopeModel = null;
      objectiveManufacturer = null;
      magnification = null;
      immersion = null;
      lensNA = null;
      positionX.clear();
      positionY.clear();
      positionZ.clear();
      channels.clear();
      zoom = null;
      waitTime = null;
      relativeTimes.clear();
      timeSeries = false;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.IFormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    tiff = new TiffReader();
    f = new Vector<String>();
    gains = new Vector<String>();
    offsets = new Vector<String>();

    if (checkSuffix(id, XML_SUFFIX)) {
      xmlFile = id;
      initXML();
      findAndParseCFG();
    }
    else if (checkSuffix(id, CFG_SUFFIX)) {
      cfgFile = id;
      initCFG();
      findAndParseXML();
    }
    else {
      // we have been given a TIFF file - reinitialize with the proper XML file

      if (isGroupFiles()) {
        LOGGER.info("Finding XML file");

        Location file = new Location(id).getAbsoluteFile();
        Location parent = file.getParentFile();
        String[] listing = parent.list();
        for (String name : listing) {
          if (checkSuffix(name, PRAIRIE_SUFFIXES)) {
            initFile(new Location(parent, name).getAbsolutePath());
            return;
          }
        }
      }
      else {
        files = new String[] {id};
        tiff.setId(files[0]);
        core = tiff.getCoreMetadata();
        metadataStore = tiff.getMetadataStore();

        Hashtable globalMetadata = tiff.getGlobalMetadata();
        for (Object key : globalMetadata.keySet()) {
          addGlobalMeta(key.toString(), globalMetadata.get(key));
        }
      }
    }
    currentId = xmlFile;

    populateMetadataStore();
  }

  // -- Helper methods --

  private void populateMetadataStore() throws FormatException {
    LOGGER.info("Populating OME metadata");

    boolean minimumMetadata =
      getMetadataOptions().getMetadataLevel() == MetadataLevel.MINIMUM;
    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, !minimumMetadata);

    if (date != null) {
      date = DateTools.formatDate(date, "MM/dd/yyyy h:mm:ss a");
    }
    if (date != null) store.setImageAcquisitionDate(new Timestamp(date), 0);

    if (!minimumMetadata) {
      // link Instrument and Image
      String instrumentID = MetadataTools.createLSID("Instrument", 0);
      store.setInstrumentID(instrumentID, 0);
      store.setImageInstrumentRef(instrumentID, 0);

      if (pixelSizeX > 0) {
        store.setPixelsPhysicalSizeX(new PositiveFloat(pixelSizeX), 0);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeX; got {}",
          pixelSizeX);
      }
      if (pixelSizeY > 0) {
        store.setPixelsPhysicalSizeY(new PositiveFloat(pixelSizeY), 0);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeY; got {}",
          pixelSizeY);
      }

      store.setPixelsTimeIncrement(waitTime, 0);

      for (int i=0; i<getSizeC(); i++) {
        String gain = i < gains.size() ? gains.get(i) : null;
        String offset = i < offsets.size() ? offsets.get(i) : null;

        if (offset != null) {
          try {
            store.setDetectorSettingsOffset(new Double(offset), 0, i);
          }
          catch (NumberFormatException e) { }
        }
        if (gain != null) {
          try {
            store.setDetectorSettingsGain(new Double(gain), 0, i);
          }
          catch (NumberFormatException e) { }
        }

        // link DetectorSettings to an actual Detector
        String detectorID = MetadataTools.createLSID("Detector", 0, i);
        store.setDetectorID(detectorID, 0, i);
        store.setDetectorSettingsID(detectorID, 0, i);
        store.setDetectorType(getDetectorType("Other"), 0, i);
        store.setDetectorZoom(zoom, 0, i);

        if (i < channels.size()) {
          store.setChannelName(channels.get(i), 0, i);
        }
      }

      for (int i=0; i<getImageCount(); i++) {
        int[] zct = getZCTCoords(i);
        int index = FormatTools.getIndex(getDimensionOrder(), getSizeZ(),
          1, getSizeT(), getImageCount() / getSizeC(), zct[0], 0, zct[2]);

        double xPos = positionX.get(index);
        double yPos = positionY.get(index);
        double zPos = positionZ.get(index);
        if (!Double.isNaN(xPos)) store.setPlanePositionX(xPos, 0, i);
        if (!Double.isNaN(yPos)) store.setPlanePositionY(yPos, 0, i);
        if (!Double.isNaN(zPos)) store.setPlanePositionZ(zPos, 0, i);

        store.setPlaneDeltaT(relativeTimes.get(String.valueOf(i + 1)), 0, i);
      }

      if (microscopeModel != null) {
        store.setMicroscopeModel(microscopeModel, 0);
      }

      String objective = MetadataTools.createLSID("Objective", 0, 0);
      store.setObjectiveID(objective, 0, 0);
      store.setObjectiveSettingsID(objective, 0);

      if (magnification != null) {
        store.setObjectiveNominalMagnification(magnification, 0, 0);
      }
      store.setObjectiveManufacturer(objectiveManufacturer, 0, 0);
      store.setObjectiveImmersion(getImmersion(immersion), 0, 0);
      store.setObjectiveCorrection(getCorrection("Other"), 0, 0);
      store.setObjectiveLensNA(lensNA, 0, 0);

      if (laserPower != null) {
        String laser = MetadataTools.createLSID("LightSource", 0 ,0);
        store.setLaserID(laser, 0, 0);
        try {
          store.setLaserPower(new Double(laserPower), 0, 0);
        }
        catch (NumberFormatException e) { }
      }
    }
  }

  private void initXML() throws FormatException, IOException {
    LOGGER.info("Parsing XML file");

    String xml = XMLTools.sanitizeXML(DataTools.readFile(xmlFile)).trim();

    DefaultHandler handler = new PrairieHandler();
    XMLTools.parseXML(xml, handler);

    core[0].sizeT = getImageCount() / (getSizeZ() * getSizeC());

    if (timeSeries && getSizeT() == 1 && getSizeZ() > 1) {
      core[0].sizeT = getSizeZ();
      core[0].sizeZ = 1;
    }

    files = new String[f.size()];
    f.copyInto(files);
    tiff.setId(files[0]);

    LOGGER.info("Populating metadata");

    if (getSizeZ() == 0) core[0].sizeZ = 1;
    if (getSizeT() == 0) core[0].sizeT = 1;

    core[0].dimensionOrder = "XYCZT";
    core[0].pixelType = FormatTools.UINT16;
    core[0].rgb = false;
    core[0].interleaved = false;
    core[0].littleEndian = tiff.isLittleEndian();
    core[0].indexed = tiff.isIndexed();
    core[0].falseColor = false;
  }

  private void initCFG() throws FormatException, IOException {
    LOGGER.info("Parsing CFG file");

    String xml = XMLTools.sanitizeXML(DataTools.readFile(cfgFile)).trim();

    DefaultHandler handler = new PrairieHandler();
    XMLTools.parseXML(xml, handler);
  }

  private String find(String[] suffix) {
    File file = new File(currentId).getAbsoluteFile();
    File parent = file.getParentFile();
    String[] listing = file.exists() ? parent.list() :
      Location.getIdMap().keySet().toArray(new String[0]);
    for (String name : listing) {
      if (checkSuffix(name, suffix)) {
        String dir = "";
        if (file.exists()) {
          dir = parent.getPath();
          if (!dir.endsWith(File.separator)) dir += File.separator;
        }
        return dir + name;
      }
    }
    return null;
  }

  private void findAndParseCFG() throws FormatException, IOException {
    cfgFile = find(CFG_SUFFIX);
    initCFG();
  }

  private void findAndParseXML() throws FormatException, IOException {
    xmlFile = find(XML_SUFFIX);
    initXML();
  }


  // -- Helper classes --

  /** SAX handler for parsing XML. */
  public class PrairieHandler extends BaseHandler {
    public void startElement(String uri, String localName, String qName,
      Attributes attributes)
    {
      if (qName.equals("PVScan")) {
        date = attributes.getValue("date");
      }
      else if (qName.equals("Sequence")) {
        String type = attributes.getValue("type");
        timeSeries = "TSeries Timed Element".equals(type);
      }
      else if (qName.equals("Frame")) {
        String index = attributes.getValue("index");
        if (index != null) {
          int zIndex = Integer.parseInt(index);
          if (zIndex > getSizeZ()) core[0].sizeZ++;
        }

        relativeTimes.put(index,
          new Double(attributes.getValue("relativeTime")));
      }
      else if (qName.equals("File")) {
        core[0].imageCount++;
        File current = new File(currentId).getAbsoluteFile();
        String dir = "";
        if (current.exists()) {
          dir = current.getPath();
          dir = dir.substring(0, dir.lastIndexOf(File.separator) + 1);
        }
        f.add(dir + attributes.getValue("filename"));

        String ch = attributes.getValue("channel");
        String channelName = attributes.getValue("channelName");
        if (channelName == null) channelName = ch;
        if (ch != null) {
          int cIndex = Integer.parseInt(ch);
          if (cIndex > getSizeC() && !channels.contains(channelName)) {
            core[0].sizeC++;
            channels.add(channelName);
          }
        }
      }
      else if (qName.equals("Key")) {
        String key = attributes.getValue("key");
        String value = attributes.getValue("value");
        addGlobalMeta(key, value);

        if (key.equals("pixelsPerLine")) {
          core[0].sizeX = Integer.parseInt(value);
        }
        else if (key.equals("linesPerFrame")) {
          core[0].sizeY = Integer.parseInt(value);
        }
        else if (key.equals("micronsPerPixel_XAxis")) {
          try {
            pixelSizeX = Double.parseDouble(value);
          }
          catch (NumberFormatException e) { }
        }
        else if (key.equals("micronsPerPixel_YAxis")) {
          try {
            pixelSizeY = Double.parseDouble(value);
          }
          catch (NumberFormatException e) { }
        }
        else if (key.equals("objectiveLens")) {
          String[] tokens = value.split(" ");
          if (tokens.length > 0) {
            objectiveManufacturer = tokens[0];
          }
          if (tokens.length > 1) {
            String mag = tokens[1].toLowerCase().replaceAll("x", "");
            try {
              Integer m = new Integer(mag);
              if (m > 0) {
                magnification = new PositiveInteger(m);
              }
              else {
                LOGGER.warn(
                  "Expected positive value for NominalMagnification; got {}",
                  m);
              }
            }
            catch (NumberFormatException e) { }
          }
          if (tokens.length > 2) {
            immersion = tokens[2];
          }
        }
        else if (key.equals("objectiveLensNA")) {
          try {
            lensNA = new Double(value);
          }
          catch (NumberFormatException e) { }
        }
        else if (key.equals("imagingDevice")) {
          microscopeModel = value;
        }
        else if (key.startsWith("pmtGain_")) gains.add(value);
        else if (key.startsWith("pmtOffset_")) offsets.add(value);
        else if (key.equals("laserPower_0")) laserPower = value;
        else if (key.equals("positionCurrent_XAxis")) {
          try {
            Double xPos = new Double(value);
            positionX.add(invertX ? -xPos : xPos);
            addGlobalMeta("X position for position #" + positionX.size(), xPos);
          }
          catch (NumberFormatException e) {
            positionX.add(Double.NaN);
          }
        }
        else if (key.equals("positionCurrent_YAxis")) {
          try {
            Double yPos = new Double(value);
            positionY.add(invertY ? -yPos : yPos);
            addGlobalMeta("Y position for position #" + positionY.size(), yPos);
          }
          catch (NumberFormatException e) {
            positionY.add(Double.NaN);
          }
        }
        else if (key.equals("positionCurrent_ZAxis")) {
          try {
            Double zPos = new Double(value);
            positionZ.add(zPos);
            addGlobalMeta("Z position for position #" + positionZ.size(), zPos);
          }
          catch (NumberFormatException e) {
            positionZ.add(Double.NaN);
          }
        }
        else if (key.equals("opticalZoom")) {
          try {
            zoom = new Double(value);
          }
          catch (NumberFormatException e) { }
        }
        else if (key.equals("bitDepth")) {
          core[0].bitsPerPixel = Integer.parseInt(value);
        }
        else if (key.equals("xYStageXPositionIncreasesLeftToRight")) {
          invertX = value.equals("True");
          if (invertX) {
            // invert already-parsed X positions
            for (int i=0; i<positionX.size(); i++) {
              Double xPos = positionX.get(i);
              if (xPos != null && !xPos.isNaN()) positionX.set(i, -xPos);
            }
          }
        }
        else if (key.equals("xYStageYPositionIncreasesBottomToTop")) {
          invertY = value.equals("True");
          if (invertY) {
            // invert already-parsed Y positions
            for (int i=0; i<positionY.size(); i++) {
              Double yPos = positionY.get(i);
              if (yPos != null && !yPos.isNaN()) positionY.set(i, -yPos);
            }
          }
        }
      }
      else if (qName.equals("PVTSeriesElementWait")) {
        try {
          waitTime = new Double(attributes.getValue("waitTime"));
        }
        catch (NumberFormatException e) { }
      }
    }
  }

}
