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
import loci.formats.in.PrairieMetadata.Frame;
import loci.formats.in.PrairieMetadata.PFile;
import loci.formats.in.PrairieMetadata.Sequence;
import loci.formats.in.PrairieMetadata.ValueTable;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.TiffParser;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.Timestamp;

import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.units.quantity.Time;
import ome.units.UNITS;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * PrairieReader is the file format reader for
 * Prairie Technologies' TIFF variant.
 *
 * @author Curtis Rueden
 * @author Melissa Linkert
 */
public class PrairieReader extends FormatReader {

  // -- Constants --

  public static final String[] CFG_SUFFIX = {"cfg"};
  public static final String[] ENV_SUFFIX = {"env"};
  public static final String[] XML_SUFFIX = {"xml"};
  public static final String[] PRAIRIE_SUFFIXES = {"cfg", "env", "xml"};

  // Private tags present in Prairie TIFF files
  // IMPORTANT NOTE: these are the same as Metamorph's private tags - therefore,
  //                 it is likely that Prairie TIFF files will be incorrectly
  //                 identified unless the XML or CFG file is specified
  private static final int PRAIRIE_TAG_1 = 33628;
  private static final int PRAIRIE_TAG_2 = 33629;
  private static final int PRAIRIE_TAG_3 = 33630;

  private static final String DATE_FORMAT = "MM/dd/yyyy h:mm:ss a";

  // -- Fields --

  /** Helper reader for opening images. */
  private TiffReader tiff;

  /** The associated XML files. */
  private Location xmlFile, cfgFile, envFile;

  /** Format-specific metadata. */
  private PrairieMetadata meta;

  /** List of Prairie metadata {@code Sequence}s, ordered by cycle. */
  private ArrayList<Sequence> sequences;

  /** List of active channels. */
  private int[] channels;

  /**
   * Whether a series uses {@code Frame}s as time points rather than focal
   * planes (i.e., sizeZ and sizeT values inverted).
   * <p>
   * This situation occurs when the series's first {@code Sequence} is labeled
   * as a "TSeries" (i.e., {@link Sequence#isTimeSeries()} returns true), but
   * there is only one {@code Sequence}.
   * </p>
   * <p>
   * The array length equals the number of series; i.e., it is a parallel array
   * to {@link #core}.
   * </p>
   */
  private boolean[] framesAreTime;

  /**
   * Flag indicating that the reader is operating in a mode where grouping of
   * files is disallowed. In the case of Prairie, this happens if a TIFF file is
   * passed to {@link #setId} while {@link #isGroupFiles()} is {@code false}.
   */
  private boolean singleTiffMode;

  // -- Constructor --

  /** Constructs a new Prairie TIFF reader. */
  public PrairieReader() {
    super("Prairie TIFF", new String[] {"tif", "tiff", "cfg", "env", "xml"});
    domains = new String[] {FormatTools.LM_DOMAIN};
    hasCompanionFiles = true;
    datasetDescription = "One .xml file, one .cfg file, and one or more " +
      ".tif/.tiff files";
  }

  // -- IFormatReader API methods --

  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  @Override
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

  @Override
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

  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    if (singleTiffMode) return tiff.getSeriesUsedFiles(noPixels);

    // add metadata files to the used files list
    final ArrayList<String> usedFiles = new ArrayList<String>();
    if (xmlFile != null) usedFiles.add(xmlFile.getAbsolutePath());
    if (cfgFile != null) usedFiles.add(cfgFile.getAbsolutePath());
    if (envFile != null) usedFiles.add(envFile.getAbsolutePath());

    if (!noPixels) {
      // add TIFF files to the used files list
      final int s = getSeries();
      for (int t = 0; t < getSizeT(); t++) {
        final Sequence sequence = sequence(t, s);
        for (int z = 0; z < getSizeZ(); z++) {
          final int index = frameIndex(sequence, z, t, s);
          final Frame frame = sequence.getFrame(index);
          if (frame == null) {
            warnFrame(sequence, index);
            continue;
          }
          for (int c = 0; c < getSizeC(); c++) {
            final int channel = channels[c];
            final PFile file = frame.getFile(channel);
            if (file == null) {
              warnFile(sequence, index, channel);
              continue;
            }
            final String filename = file.getFilename();
            if (filename == null) {
              warnFilename(sequence, index, channel);
              continue;
            }
            usedFiles.add(getPath(file));
          }
        }
      }
    }

    return usedFiles.toArray(new String[usedFiles.size()]);
  }

  @Override
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    return tiff.getOptimalTileWidth();
  }

  @Override
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    return tiff.getOptimalTileHeight();
  }

  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
    if (singleTiffMode) return tiff.openBytes(no, buf, x, y, w, h);

    // convert 1D index to (sequence, index, channel) coordinates.
    final int[] zct = getZCTCoords(no);
    final int z = zct[0], c = zct[1], t = zct[2];

    final Sequence sequence = sequence(t, getSeries());

    final int index = frameIndex(sequence, z, t, getSeries());
    final Frame frame = sequence.getFrame(index);
    if (frame == null) {
      warnFrame(sequence, index);
      return blank(buf);
    }

    final int channel = channels[c];
    final PFile file = frame.getFile(channel);
    if (file == null) {
      warnFile(sequence, index, channel);
      return blank(buf);
    }

    tiff.setId(getPath(file));
    return tiff.openBytes(0, buf, x, y, w, h);
  }

  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (tiff != null) tiff.close(fileOnly);
    if (!fileOnly) {
      xmlFile = cfgFile = envFile = null;
      tiff = null;
      meta = null;
      sequences = null;
      channels = null;
      framesAreTime = null;
      singleTiffMode = false;
    }
  }

  // -- Internal FormatReader API methods --

  @Override
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    tiff = new TiffReader();

    if (checkSuffix(id, XML_SUFFIX)) {
      xmlFile = new Location(id);
      findMetadataFiles();
    }
    else if (checkSuffix(id, CFG_SUFFIX)) {
      cfgFile = new Location(id);
      findMetadataFiles();
    }
    else if (checkSuffix(id, ENV_SUFFIX)) {
      envFile = new Location(id);
      findMetadataFiles();
    }
    else {
      // we have been given a TIFF file
      if (isGroupFiles()) {
        findMetadataFiles();
      }
      else {
        // NB: File grouping is not allowed, so we enter a special mode,
        // which delegates to the TIFF reader for everything.
        singleTiffMode = true;
        tiff.setId(id);
        return;
      }
    }

    currentId = xmlFile.getAbsolutePath();

    parsePrairieMetadata();
    populateCoreMetadata();
    populateOriginalMetadata();
    populateOMEMetadata();
  }

  // -- Helper methods --

  private void findMetadataFiles() {
    LOGGER.info("Finding metadata files");
    if (xmlFile == null) xmlFile = find(XML_SUFFIX);
    if (cfgFile == null) cfgFile = find(CFG_SUFFIX);
    if (envFile == null) envFile = find(ENV_SUFFIX);
  }

  /**
   * This step parses the Prairie metadata files into the Prairie-specific
   * metadata structure, {@link #meta}.
   */
  private void parsePrairieMetadata() throws FormatException, IOException {
    LOGGER.info("Parsing Prairie metadata");

    final Document xml, cfg, env;
    try {
      xml = parseDOM(xmlFile);
      cfg = parseDOM(cfgFile);
      env = parseDOM(envFile);
    }
    catch (ParserConfigurationException exc) {
      throw new FormatException(exc);
    }
    catch (SAXException exc) {
      throw new FormatException(exc);
    }

    meta = new PrairieMetadata(xml, cfg, env);
    sequences = meta.getSequences();
    channels = meta.getActiveChannels();
    if (channels == null || channels.length == 0) {
      throw new FormatException("No active channels found");
    }
  }

  /**
   * This step populates the {@link CoreMetadata} by extracting relevant values
   * from the parsed {@link #meta} structure.
   */
  private void populateCoreMetadata() throws FormatException, IOException {
    LOGGER.info("Populating core metadata");

    // NB: Both stage positions and time points are rasterized into the list
    // of Sequences. So by definition: sequenceCount = sizeT * seriesCount.
    final int sequenceCount = sequences.size();
    final int sizeT = computeSizeT(sequenceCount);
    final int seriesCount = sequenceCount / sizeT;

    final Integer bitDepth = meta.getBitDepth();
    int bpp = bitDepth == null ? -1 : bitDepth;

    core.clear();
    framesAreTime = new boolean[seriesCount];
    for (int s = 0; s < seriesCount; s++) {
      final Sequence sequence = sequence(0, s, seriesCount);
      final Frame frame = sequence.getFirstFrame();
      final PFile file = frame == null ? null : frame.getFirstFile();
      if (frame == null || file == null) {
        throw new FormatException("No metadata for series #" + s);
      }

      // NB: We initialize the TIFF reader with the first available file of the
      // series. For performance, we initialize only the first series, and then
      // assume that subsequent series have the same TIFF properties
      // (endianness, etc.). In our experience, all Prairie datasets conform to
      // this assumption, but if not, removing the "if (s == 0)" test here
      // should remedy any resultant inaccuracies in the metadata.
      if (s == 0) {
        tiff.setId(getPath(file));
        if (bpp <= 0) bpp = tiff.getBitsPerPixel();
      }

      final Integer linesPerFrame = frame.getLinesPerFrame();
      final Integer pixelsPerLine = frame.getPixelsPerLine();
      final int indexCount = sequence.getIndexCount();

      final int sizeX = pixelsPerLine == null ? tiff.getSizeX() : pixelsPerLine;
      final int sizeY = linesPerFrame == null ? tiff.getSizeY() : linesPerFrame;
      framesAreTime[s] = sequence.isTimeSeries() && sizeT == 1;

      final CoreMetadata cm = new CoreMetadata();
      cm.sizeX = sizeX;
      cm.sizeY = sizeY;
      cm.sizeZ = framesAreTime[s] ? 1 : indexCount;
      cm.sizeC = channels.length;
      cm.sizeT = framesAreTime[s] ? indexCount : sizeT;
      cm.pixelType = tiff.getPixelType();
      cm.bitsPerPixel = bpp;
      cm.imageCount = cm.sizeZ * cm.sizeC * cm.sizeT;
      cm.dimensionOrder = "XYCZT";
      cm.orderCertain = true;
      cm.rgb = false;
      cm.littleEndian = tiff.isLittleEndian();
      cm.interleaved = false;
      cm.indexed = tiff.isIndexed();
      cm.falseColor = false;
      core.add(cm);
    }
  }

  /**
   * This steps populates the original metadata table (the tables returned by
   * {@link #getGlobalMetadata()} and {@link #getSeriesMetadata()}).
   */
  private void populateOriginalMetadata() {
    final boolean minimumMetadata = isMinimumMetadata();
    if (minimumMetadata) return;

    // populate global metadata
    addGlobalMeta("cycleCount", meta.getCycleCount());
    addGlobalMeta("date", meta.getDate());
    addGlobalMeta("waitTime", meta.getWaitTime());
    addGlobalMeta("sequenceCount", sequences.size());

    final ValueTable config = meta.getConfig();
    for (final String key : config.keySet()) {
      addGlobalMeta(key, config.get(key).toString());
    }

    addGlobalMeta("meta", meta);

    // populate series metadata
    final int seriesCount = getSeriesCount();
    for (int s = 0; s < seriesCount; s++) {
      setSeries(s);
      final Sequence sequence = sequence(s);
      addSeriesMeta("cycle", sequence.getCycle());
      addSeriesMeta("indexCount", sequence.getIndexCount());
      addSeriesMeta("type", sequence.getType());
    }
    setSeries(0);
  }

  /**
   * This step populates the OME {@link MetadataStore} by extracting relevant
   * values from the parsed {@link #meta} structure.
   */
  private void populateOMEMetadata() throws FormatException {
    LOGGER.info("Populating OME metadata");

    // populate required Pixels metadata
    final boolean minimumMetadata = isMinimumMetadata();
    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, !minimumMetadata);

    // populate required AcquisitionDate
    final String date = DateTools.formatDate(meta.getDate(), DATE_FORMAT);
    final Timestamp acquisitionDate = Timestamp.valueOf(date);
    final int seriesCount = getSeriesCount();
    for (int s = 0; s < seriesCount; s++) {
      setSeries(s);
      if (date != null) store.setImageAcquisitionDate(acquisitionDate, s);
    }

    if (minimumMetadata) return;

    // create an Instrument
    final String instrumentID = MetadataTools.createLSID("Instrument", 0);
    store.setInstrumentID(instrumentID, 0);

    // populate Laser Power, if available
    final Double laserPower = meta.getLaserPower();
    if (laserPower != null) {
      // create a Laser
      final String laserID = MetadataTools.createLSID("LightSource", 0, 0);
      store.setLaserID(laserID, 0, 0);

      store.setLaserPower(new Power(laserPower, UNITS.MILLIWATT), 0, 0);
    }

    String objectiveID = null;
    for (int s = 0; s < seriesCount; s++) {
      setSeries(s);
      final Sequence sequence = sequence(s);
      final Frame firstFrame = sequence.getFirstFrame();

      // link Instrument and Image
      store.setImageInstrumentRef(instrumentID, s);

      // populate PhysicalSizeX
      final PositiveFloat physicalSizeX =
        pf(firstFrame.getMicronsPerPixelX(), "PhysicalSizeX");
      if (physicalSizeX != null) {
        store.setPixelsPhysicalSizeX(FormatTools.createLength(physicalSizeX, UNITS.MICROMETER), s);
      }
    
      // populate PhysicalSizeY
      final PositiveFloat physicalSizeY =
        pf(firstFrame.getMicronsPerPixelY(), "PhysicalSizeY");
      if (physicalSizeY != null) {
        store.setPixelsPhysicalSizeY(FormatTools.createLength(physicalSizeY, UNITS.MICROMETER), s);
      }
      // populate TimeIncrement
      final Double waitTime = meta.getWaitTime();
      if (waitTime != null) store.setPixelsTimeIncrement(new Time(waitTime, UNITS.SECOND), s);

      final String[] detectorIDs = new String[channels.length];

      for (int c = 0; c < channels.length; c++) {
        final int channel = channels[c];
        final PFile file = firstFrame.getFile(channel);

        // populate channel name
        final String channelName = file == null ? null : file.getChannelName();
        if (channelName != null) store.setChannelName(channelName, s, c);

        // populate emission wavelength
        if (file != null) {
          final Double waveMin = file.getWavelengthMin();
          final Double waveMax = file.getWavelengthMax();
          if (waveMin != null && waveMax != null) {
            final double waveAvg = (waveMin + waveMax) / 2;
            final Length wavelength =
              FormatTools.getEmissionWavelength(waveAvg);
            store.setChannelEmissionWavelength(wavelength, s, c);
          }
        }

        if (detectorIDs[c] == null) {
          // create a Detector for this channel
          detectorIDs[c] = MetadataTools.createLSID("Detector", 0, c);
          store.setDetectorID(detectorIDs[c], 0, c);
          store.setDetectorType(getDetectorType("Other"), 0, c);

          // NB: Ideally we would populate the detector zoom differently for
          // each Image, rather than globally for the Detector, but
          // unfortunately it is a property of Detector, not DetectorSettings.
          final Double zoom = firstFrame.getOpticalZoom();
          if (zoom != null) store.setDetectorZoom(zoom, 0, c);
        }

        // link DetectorSettings and Detector
        store.setDetectorSettingsID(detectorIDs[c], s, c);

        // populate Offset
        final Double offset = firstFrame.getOffset(c);
        if (offset != null) store.setDetectorSettingsOffset(offset, s, c);

        // populate Gain
        final Double gain = firstFrame.getGain(c);
        if (gain != null) store.setDetectorSettingsGain(gain, s, c);
      }

      if (objectiveID == null) {
        // create an Objective
        objectiveID = MetadataTools.createLSID("Objective", 0, 0);
        store.setObjectiveID(objectiveID, 0, 0);
        store.setObjectiveCorrection(getCorrection("Other"), 0, 0);

        // populate Objective NominalMagnification
        final Double magnification = firstFrame.getMagnification();
        if (magnification != null) {
          store.setObjectiveNominalMagnification(magnification, 0, 0);
        }

        // populate Objective Manufacturer
        final String objectiveManufacturer =
          firstFrame.getObjectiveManufacturer();
        store.setObjectiveManufacturer(objectiveManufacturer, 0, 0);

        // populate Objective Immersion
        final String immersion = firstFrame.getImmersion();
        store.setObjectiveImmersion(getImmersion(immersion), 0, 0);

        // populate Objective LensNA
        final Double lensNA = firstFrame.getObjectiveLensNA();
        if (lensNA != null) store.setObjectiveLensNA(lensNA, 0, 0);

        // populate Microscope Model
        final String microscopeModel = firstFrame.getImagingDevice();
        store.setMicroscopeModel(microscopeModel, 0);
      }

      // link ObjectiveSettings and Objective
      store.setObjectiveSettingsID(objectiveID, s);

      // populate stage position coordinates
      for (int t = 0; t < getSizeT(); t++) {
        final Sequence tSequence = sequence(t, s);
        for (int z = 0; z < getSizeZ(); z++) {
          final int index = frameIndex(tSequence, z, t, s);
          final Frame zFrame = tSequence.getFrame(index);
          if (zFrame == null) {
            warnFrame(sequence, index);
            continue;
          }
          final Length posX = zFrame.getPositionX();
          final Length posY = zFrame.getPositionY();
          final Length posZ = zFrame.getPositionZ();
          final Double deltaT = zFrame.getRelativeTime();
          for (int c = 0; c < getSizeC(); c++) {
            final int i = getIndex(z, c, t);
            if (posX != null) store.setPlanePositionX(posX, s, i);
            if (posY != null) store.setPlanePositionY(posY, s, i);
            if (posZ != null) store.setPlanePositionZ(posZ, s, i);
            if (deltaT != null) store.setPlaneDeltaT(new Time(deltaT, UNITS.SECOND), s, i);
          }
        }
      }
    }
    setSeries(0);
  }

  /** Gets whether to populate only the minimum required metadata. */
  private boolean isMinimumMetadata() {
    return getMetadataOptions().getMetadataLevel() == MetadataLevel.MINIMUM;
  }

  /** Parses a {@link Document} from the data in the given file. */
  private Document parseDOM(final Location file)
    throws ParserConfigurationException, SAXException, IOException
  {
    if (file == null) return null;

    // NB: The simplest approach here would be to call XMLTools.parseDOM(file)
    // directly, but we cannot do that because Prairie XML files are technically
    // invalid and must be preprocessed in order for Java to parse them.
    //
    // Specifically, Prairie XML files describe themselves as
    // <?xml version="1.0" encoding="utf-8"?>
    //
    // but some of them contain invalid characters in the XML 1.0 specification.

    // One way to supposedly hack around this is to manually adjust the XML
    // version to 1.1, which is a superset of 1.0 with support for an expanded
    // character set. We tried it, but unfortunately the XML parsing gets
    // mangled (e.g., XML attributes have invalid values).
    //
    // So we hack around it another way: by filtering out all invalid characters
    // manually, so the data becomes valid XML version 1.0.
    //
    // For details, see:
    // http://stackoverflow.com/questions/2997255

    // read entire XML document into a giant byte array
    final byte[] buf = new byte[(int) file.length()];
    final RandomAccessInputStream is =
      new RandomAccessInputStream(file.getAbsolutePath());
    is.readFully(buf);
    is.close();

    // filter out invalid characters from the XML
    final String xml =
      XMLTools.sanitizeXML(new String(buf, Constants.ENCODING));

    return XMLTools.parseDOM(xml);
  }

  /** Emits a warning about a missing {@code <Frame>}. */
  private void warnFrame(final Sequence sequence, final int index) {
    LOGGER.warn("No Frame at cycle #{}, index #{}", sequence.getCycle(), index);
  }

  /** Emits a warning about a missing {@code <File>}. */
  private void warnFile(final Sequence sequence, final int index,
    final int channel)
  {
    LOGGER.warn("No File at cycle #" + sequence.getCycle() +
      ", index #{}, channel #{}", index, channel);
  }

  /** Emits a warning about a {@code <File>}'s missing {@code filename}. */
  private void warnFilename(final Sequence sequence, final int index,
    final int channel)
  {
    LOGGER.warn("File at cycle #" + sequence.getCycle() +
      ", index #{}, channel #{} has null filename", index, channel);
  }

  /** Gets the absolute path to the filename of the given {@link PFile}. */
  private String getPath(final PFile file) {
    final Location f = new Location(xmlFile.getParent(), file.getFilename());
    return f.getAbsolutePath();
  }

  /** Blanks out and returns the given buffer. */
  private byte[] blank(final byte[] buf) {
    // missing data; return empty plane
    Arrays.fill(buf, (byte) 0);
    return buf;
  }

  /**
   * Converts the given {@code double} to a {@link PositiveFloat}, or
   * {@code null} if incompatible.
   */
  private PositiveFloat pf(final Double value, final String name) {
    if (value == null) return null;
    try {
      return new PositiveFloat(value);
    }
    catch (IllegalArgumentException e) {
      LOGGER.debug("Expected positive value for {}; got {}", name, value);
    }
    return null;
  }

  /** Finds the first file with one of the given suffixes. */
  private Location find(final String[] suffix) {
    final Location file = new Location(currentId).getAbsoluteFile();
    final Location parent = file.getParentFile();
    final String[] listing = parent.list();
    for (final String name : listing) {
      if (checkSuffix(name, suffix)) {
        return new Location(parent, name);
      }
    }
    return null;
  }

  /**
   * Scans the parsed metadata to determine the number of actual time points
   * versus the number of actual stage positions. The Prairie file format makes
   * no distinction between the two, referring to both as "Sequences", so we
   * must compare XYZ stage positions to differentiate them.
   */
  private int computeSizeT(final int sequenceCount) {
    // NB: Guess at different possible "spans" for the rasterization.
    for (int sizeP = 1; sizeP <= sequenceCount; sizeP++) {
      if (sequenceCount % sizeP != 0) continue; // not a valid combo
      final int sizeT = sequenceCount / sizeP;
      if (positionsMatch(sizeT, sizeP)) return sizeT;
    }
    return 1;
  }

  /** Verifies that stage coordinates match for all (P, Z) across time. */
  private boolean positionsMatch(int sizeT, int sizeP) {
    // NB: Rasterization order is XYCZpT, where p is the stage position.
    for (int p = 0; p < sizeP; p++) {
      final Sequence initialSequence = sequence(0, p, sizeP);

      final int indexMin = initialSequence.getIndexMin();
      final int indexCount = initialSequence.getIndexCount();
      for (int z = 0; z < indexCount; z++) {
        final int index = z + indexMin;
        final Frame initialFrame = initialSequence.getFrame(index);
        if (initialFrame == null) {
          warnFrame(initialSequence, index);
          break;
        }

        // obtain the initial XYZ stage coordinates for this position
        final Length xInitial = initialFrame.getPositionX();
        final Length yInitial = initialFrame.getPositionY();
        final Length zInitial = initialFrame.getPositionZ();

        // verify that the initial coordinates match all subsequent time points
        for (int t = 1; t < sizeT; t++) {
          final Sequence sequence = sequence(t, p, sizeP);
          final Frame frame = sequence.getFrame(index);
          if (frame == null) {
            warnFrame(sequence, index);
            continue;
          }

          final Length xPos = frame.getPositionX();
          final Length yPos = frame.getPositionY();
          final Length zPos = frame.getPositionZ();

          if (!equal(xPos, xInitial) || !equal(yPos, yInitial) ||
            !equal(zPos, zInitial))
          {
            return false;
          }
        }
      }
    }
    return true;
  }

  /**
   * Gets the first sequence associated with the given series.
   * 
   * @param s The series (i.e., stage position).
   * @return The first associated {@code Sequence}.
   */
  private Sequence sequence(final int s) {
    return sequence(0, s);
  }

  /**
   * Gets the sequence associated with the given series and time point.
   * 
   * @param t The time point.
   * @param s The series (i.e., stage position).
   * @return The associated {@code Sequence}.
   */
  private Sequence sequence(final int t, final int s) {
    final int actualT = framesAreTime[s] ? 0 : t;
    return sequence(actualT, s, getSeriesCount());
  }

  /**
   * Gets the sequence associated with the given time point and stage position.
   * 
   * @param t The time point.
   * @param p The stage position.
   * @param sizeP The number of stage positions.
   * @return The associated {@code Sequence}.
   */
  private Sequence sequence(final int t, final int p, final int sizeP) {
    return sequences.get(sizeP * t + p);
  }

  /**
   * Gets the frame index associated with the given (Z, T) position of the
   * specified series.
   * 
   * @param sequence The sequence from which to extract the frame.
   * @param z The focal plane.
   * @param t The time point.
   * @param s The series (i.e., stage position).
   * @return The frame index which can be passed to {@link Sequence#getFrame}.
   */
  private int frameIndex(final Sequence sequence, int z, int t, int s) {
    return (framesAreTime[s] ? t : z) + sequence.getIndexMin();
  }


  /** Determines whether the two {@link Length} values are equal. */
  private static boolean equal(final Length xPos, final Length xInitial) {
    if (xPos == null && xInitial == null) return true;
    if (xPos == null) return false;
    return xPos.equals(xInitial);
  }
}
