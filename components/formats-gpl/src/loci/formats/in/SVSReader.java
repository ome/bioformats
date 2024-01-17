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
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import loci.common.Constants;
import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.codec.Codec;
import loci.formats.codec.CodecOptions;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.IFD;
import loci.formats.tiff.PhotoInterp;
import loci.formats.tiff.TiffIFDEntry;
import loci.formats.tiff.TiffParser;
import ome.xml.model.primitives.Color;
import ome.xml.model.primitives.Timestamp;

import ome.units.quantity.Length;
import ome.units.UNITS;

class SVSCoreMetadata extends CoreMetadata {
  int[] ifdIndex;
  String comment;
  Length pixelSize;

  SVSCoreMetadata() {
    super();
  }
}

/**
 * SVSReader is the file format reader for Aperio SVS TIFF files.
 */
public class SVSReader extends BaseTiffReader {

  // -- Constants --

  public static final String REMOVE_THUMBNAIL_KEY = "svs.remove_thumbnail";
  public static final boolean REMOVE_THUMBNAIL_DEFAULT = true;

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(SVSReader.class);

  /** TIFF image description prefix for Aperio SVS files. */
  private static final String APERIO_IMAGE_DESCRIPTION_PREFIX = "Aperio Image";

  private static final String DATE_FORMAT = "MM/dd/yy HH:mm:ss";

  // -- Fields --

  private Double[] zPosition;
  private String[] comments;

  private Double emissionWavelength, excitationWavelength;
  private Double exposureTime, exposureScale;
  private Double magnification;
  private String date, time;
  private ArrayList<String> dyeNames = new ArrayList<String>();

  private transient Color displayColor = null;

  // explicitly record the series and IFD indexes
  // for the label and macro images
  // this makes it easier to calculate IFD mappings
  private int labelIndex = -1;
  private int macroIndex = -1;

  // total number of extra (label and macro) images
  private int extraImages = 0;

  private transient Double physicalDistanceFromLeftEdge;
  private transient Double physicalDistanceFromTopEdge;

  // -- Constructor --

  /** Constructs a new SVS reader. */
  public SVSReader() {
    super("Aperio SVS", new String[] {"svs"});
    domains = new String[] {FormatTools.HISTOLOGY_DOMAIN};
    suffixNecessary = true;
    noSubresolutions = true;
    canSeparateSeries = false;
  }

  public SVSReader(String name, String[] suffixes) {
    super(name, suffixes);
  }

  // -- SVSReader API methods --

  public boolean removeThumbnail() {
    MetadataOptions options = getMetadataOptions();
    if (options instanceof DynamicMetadataOptions) {
      return ((DynamicMetadataOptions) options).getBoolean(
        REMOVE_THUMBNAIL_KEY, REMOVE_THUMBNAIL_DEFAULT);
    }
    return REMOVE_THUMBNAIL_DEFAULT;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* (non-Javadoc)
   * @see loci.formats.FormatReader#isThisType(java.lang.String, boolean)
   */
  @Override
  public boolean isThisType(String name, boolean open) {
    boolean isThisType = super.isThisType(name, open);
    if (!isThisType && open) {
      try (RandomAccessInputStream stream = new RandomAccessInputStream(name)) {
        TiffParser tiffParser = new TiffParser(stream);
        tiffParser.setDoCaching(false);
        if (!tiffParser.isValidHeader()) {
          return false;
        }
        IFD ifd = tiffParser.getFirstIFD();
        if (ifd == null) {
          return false;
        }
        Object description = ifd.get(IFD.IMAGE_DESCRIPTION);
        if (description != null) {
          String imageDescription = null;

          if (description instanceof TiffIFDEntry) {
            Object value = tiffParser.getIFDValue((TiffIFDEntry) description);
            if (value != null) {
              imageDescription = value.toString();
            }
          }
          else if (description instanceof String) {
            imageDescription = (String) description;
          }
          if (imageDescription != null
              && imageDescription.startsWith(APERIO_IMAGE_DESCRIPTION_PREFIX)) {
            return true;
          }
        }
        return false;
      }
      catch (IOException e) {
        LOGGER.debug("I/O exception during isThisType() evaluation.", e);
        return false;
      }
    }
    return isThisType;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
    IFD ifd = getIFD(no);
    tiffParser.getSamples(ifd, buf, x, y, w, h);
    return buf;
  }

  /* @see loci.formats.IFormatReader#openThumbBytes(int) */
  @Override
  public byte[] openThumbBytes(int no) throws FormatException, IOException {
    if (core.size() == 1 || getSeries() >= getSeriesCount() - extraImages) {
      return FormatTools.openThumbBytes(this, no);
    }

    int smallestSeries = getSeriesCount() - extraImages - 1;
    if (smallestSeries >= 0) {
      int thisSeries = getSeries();
      int resolution = getResolution();
      setSeries(smallestSeries);
      if (!hasFlattenedResolutions()) {
        setResolution(1);
      }
      byte[] thumb = FormatTools.openThumbBytes(this, no);
      setSeries(thisSeries);
      setResolution(resolution);
      return thumb;
    }
    return super.openThumbBytes(no);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      zPosition = null;
      comments = null;

      emissionWavelength = null;
      excitationWavelength = null;
      exposureTime = null;
      exposureScale = null;
      magnification = null;
      date = null;
      time = null;
      dyeNames.clear();
      displayColor = null;
      extraImages = 0;
      labelIndex = -1;
      macroIndex = -1;
    }
  }

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    try {
      IFD ifd = getIFD(0);
      return (int) ifd.getTileWidth();
    }
    catch (FormatException e) {
      LOGGER.debug("", e);
    }
    return super.getOptimalTileWidth();
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    try {
      IFD ifd = getIFD(0);
      return (int) ifd.getTileLength();
    }
    catch (FormatException e) {
      LOGGER.debug("", e);
    }
    return super.getOptimalTileHeight();
  }

  // -- ICompressedTileReader API methods --

  @Override
  public int getTileRows(int no) {
    FormatTools.assertId(currentId, true, 1);
    try {
      IFD ifd = getIFD(no);
      return (int) ifd.getTilesPerColumn();
    }
    catch (FormatException e) {
      LOGGER.debug("Could not get tile row count", e);
    }
    return super.getTileRows(no);
  }

  @Override
  public int getTileColumns(int no) {
    FormatTools.assertId(currentId, true, 1);
    try {
      IFD ifd = getIFD(no);
      return (int) ifd.getTilesPerRow();
    }
    catch (FormatException e) {
      LOGGER.debug("Could not get tile column count", e);
    }
    return super.getTileColumns(no);
  }

  @Override
  public byte[] openCompressedBytes(int no, int x, int y) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    IFD ifd = getIFD(no);
    long[] byteCounts = ifd.getStripByteCounts();
    int tileIndex = getTileIndex(ifd, x, y);

    byte[] jpegTable = (byte[]) ifd.getIFDValue(IFD.JPEG_TABLES);
    int jpegTableBytes = jpegTable == null ? 0 : jpegTable.length - 2;
    long expectedBytes = byteCounts[tileIndex];
    if (expectedBytes > 0) {
      expectedBytes += jpegTableBytes;
    }

    if (expectedBytes < 0 || expectedBytes > Integer.MAX_VALUE) {
      throw new IOException("Invalid compressed tile size: " + expectedBytes);
    }

    byte[] buf = new byte[(int) expectedBytes];
    return openCompressedBytes(no, buf, x, y);
  }

  @Override
  public byte[] openCompressedBytes(int no, byte[] buf, int x, int y) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    IFD ifd = getIFD(no);
    long[] offsets = ifd.getStripOffsets();
    long[] byteCounts = ifd.getStripByteCounts();

    int tileIndex = getTileIndex(ifd, x, y);

    byte[] jpegTable = (byte[]) ifd.getIFDValue(IFD.JPEG_TABLES);
    int jpegTableBytes = jpegTable == null ? 0 : jpegTable.length - 2;
    long expectedBytes = byteCounts[tileIndex];
    if (expectedBytes > 0) {
      expectedBytes += jpegTableBytes;
    }

    if (buf.length < expectedBytes) {
      throw new IllegalArgumentException("Tile buffer too small: expected >=" +
        expectedBytes + ", got " + buf.length);
    }
    else if (expectedBytes < 0 || expectedBytes > Integer.MAX_VALUE) {
      throw new IOException("Invalid compressed tile size: " + expectedBytes);
    }

    if (jpegTable != null && expectedBytes > 0) {
      System.arraycopy(jpegTable, 0, buf, 0, jpegTable.length - 2);
      // skip over the duplicate SOI marker
      tiffParser.getStream().seek(offsets[tileIndex] + 2);
      tiffParser.getStream().readFully(buf, jpegTable.length - 2, (int) byteCounts[tileIndex]);
    }
    else if (byteCounts[tileIndex] > 0) {
      tiffParser.getStream().seek(offsets[tileIndex]);
      tiffParser.getStream().readFully(buf, 0, (int) byteCounts[tileIndex]);
    }

    return buf;
  }

  @Override
  public Codec getTileCodec(int no) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    IFD ifd = getIFD(no);
    return ifd.getCompression().getCodec();
  }

  @Override
  public CodecOptions getTileCodecOptions(int no, int x, int y) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    IFD ifd = getIFD(no);
    CodecOptions options = ifd.getCompression().getCompressionCodecOptions(ifd);
    options.width = (int) ifd.getTileWidth();
    options.height = (int) ifd.getTileLength();
    return options;
  }

  // -- Internal BaseTiffReader API methods --

  /* @see loci.formats.BaseTiffReader#initStandardMetadata() */
  @Override
  protected void initStandardMetadata() throws FormatException, IOException {
    super.initStandardMetadata();

    ifds = tiffParser.getMainIFDs();

    int seriesCount = ifds.size();

    core.clear();
    for (int i=0; i<seriesCount; i++) {
      core.add(new SVSCoreMetadata());
    }

    zPosition = new Double[seriesCount];
    comments = new String[seriesCount];

    HashSet<Double> uniqueZ = new HashSet<Double>();

    for (int i=0; i<seriesCount; i++) {
      setSeries(i);
      int index = i;

      IFD currentIFD = ifds.get(index);
      tiffParser.fillInIFD(currentIFD);

      String comment = currentIFD.getComment();
      int subfileType = currentIFD.getIFDIntValue(IFD.NEW_SUBFILE_TYPE);
      if (comment == null || subfileType != 0) {
        if (labelIndex == -1) {
          labelIndex = i;
        }
        else if (macroIndex == -1) {
          macroIndex = i;
        }
        continue;
      }
      comments[i] = comment;
      String[] lines = comment.split("\n");
      String[] tokens;
      String key, value;
      for (String line : lines) {
        tokens = line.split("[|]");
        for (String t : tokens) {
          if (t.indexOf('=') >= 0) {
            key = t.substring(0, t.indexOf('=')).trim();
            value = t.substring(t.indexOf('=') + 1).trim();
            if (key.equals("TotalDepth")) {
              zPosition[index] = new Double(0);
            }
            else if (key.equals("OffsetZ")) {
              zPosition[index] = DataTools.parseDouble(value);
            }
          }
          else if (t.toLowerCase().indexOf("label") >= 0) {
            labelIndex = i;
          }
          else if (t.toLowerCase().indexOf("macro") >= 0) {
            macroIndex = i;
          }
        }
      }
      if (zPosition[index] != null) {
        uniqueZ.add(zPosition[index]);
      }
    }
    setSeries(0);

    // based on whether label and/or macro are present,
    // determine how many resolutions in pyramid

    int resolutions = getSeriesCount();
    if (labelIndex >= 0) {
      resolutions--;
    }
    if (macroIndex >= 0) {
      resolutions--;
    }
    extraImages = getSeriesCount() - resolutions;

    // repopulate core metadata

    // remove any invalid pyramid resolutions
    IFD firstIFD = ifds.get(getIFDIndex(0, 0));
    for (int s=1; s<resolutions; s++) {
      int index = getIFDIndex(s, 0);
      IFD ifd = ifds.get(index);
      tiffParser.fillInIFD(ifd);
      if (ifd.getPixelType() != firstIFD.getPixelType()) {
        ifds.set(index, null);
      }
    }
    for (int s=0; s<ifds.size(); ) {
      if (ifds.get(s) != null) {
        s++;
      }
      else {
        ifds.remove(s);
        if (s < labelIndex) {
          labelIndex--;
        }
        if (s < macroIndex) {
          macroIndex--;
        }
      }
    }
    if (uniqueZ.size() == 0) {
      uniqueZ.add(0d);
    }
    zPosition = uniqueZ.toArray(new Double[uniqueZ.size()]);
    Arrays.sort(zPosition);
    seriesCount = ((ifds.size() - extraImages) / uniqueZ.size()) + extraImages;

    core.clear();
    if (seriesCount > extraImages) {
      core.add();
      for (int r=0; r < seriesCount - extraImages; r++) {
        core.add(0, new SVSCoreMetadata());
      }
      for (int extra=0; extra<extraImages; extra++) {
        core.add(new SVSCoreMetadata());
      }
    }
    else {
      // Should never happen unless the SVS is corrupt?
      for (int s=0; s<seriesCount; s++) {
        core.add(new SVSCoreMetadata());
      }
    }

    for (int s=0; s<seriesCount; s++) {
      int[] pos = core.flattenedIndexes(s);
      setCoreIndex(s);

      SVSCoreMetadata ms = (SVSCoreMetadata) core.get(pos[0], pos[1]);

      if (s == 0 && seriesCount > extraImages) {
        ms.resolutionCount = seriesCount - extraImages;
      }

      IFD ifd = ifds.get(getIFDIndex(s, 0));
      tiffParser.fillInIFD(ifd);
      PhotoInterp p = ifd.getPhotometricInterpretation();
      int samples = ifd.getSamplesPerPixel();
      ms.rgb = samples > 1 || p == PhotoInterp.RGB;

      ms.sizeX = (int) ifd.getImageWidth();
      ms.sizeY = (int) ifd.getImageLength();
      if (s < seriesCount - extraImages) {
        ms.sizeZ = uniqueZ.size();
      }
      else {
        ms.sizeZ = 1;
      }
      ms.ifdIndex = new int[ms.sizeZ];
      for (int z=0; z<ms.sizeZ; z++) {
        ms.ifdIndex[z] = getIFDIndex(s, z);
      }
      ms.sizeT = 1;
      ms.sizeC = ms.rgb ? samples : 1;
      ms.littleEndian = ifd.isLittleEndian();
      ms.indexed = p == PhotoInterp.RGB_PALETTE &&
        (get8BitLookupTable() != null || get16BitLookupTable() != null);
      ms.imageCount = ms.sizeZ * ms.sizeT;
      ms.pixelType = ifd.getPixelType();
      ms.metadataComplete = true;
      ms.interleaved = false;
      ms.falseColor = false;
      ms.dimensionOrder = "XYCZT";
      ms.thumbnail = s != 0;

      if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
        String comment = ifds.get(ms.ifdIndex[0]).getComment();
        if (comment == null) {
          continue;
        }
        String[] lines = comment.split("\n");
        String[] tokens;
        String key, value;
        for (String line : lines) {
          tokens = line.split("[|]");
          for (String t : tokens) {
            if (t.indexOf('=') == -1) {
              addGlobalMeta("Comment", t);
              ((SVSCoreMetadata) getCurrentCore()).comment = t;
            }
            else {
              key = t.substring(0, t.indexOf('=')).trim();
              value = t.substring(t.indexOf('=') + 1).trim();
              addSeriesMeta(key, value);
              switch (key) {
                case "MPP":
                  ((SVSCoreMetadata) getCurrentCore()).pixelSize =
                    FormatTools.getPhysicalSizeX(DataTools.parseDouble(value));
                  break;
                case "Date":
                  date = value;
                  break;
                case "Time":
                  time = value;
                  break;
                case "Emission Wavelength":
                  emissionWavelength = DataTools.parseDouble(value);
                  break;
                case "Excitation Wavelength":
                  excitationWavelength = DataTools.parseDouble(value);
                  break;
                case "Exposure Time":
                  exposureTime = DataTools.parseDouble(value);
                  break;
                case "Exposure Scale":
                  exposureScale = DataTools.parseDouble(value);
                  break;
                case "AppMag":
                  magnification = DataTools.parseDouble(value);
                  break;
                case "Dye":
                  dyeNames.add(value);
                  break;
                case "DisplayColor":
                  // stored color is RGB, Color expects RGBA
                  int color = Integer.parseInt(value);
                  displayColor = new Color((color << 8) | 0xff);
                  break;
                case "Left":
                  physicalDistanceFromLeftEdge = DataTools.parseDouble(value);
                  break;
                case "Top":
                  physicalDistanceFromTopEdge = DataTools.parseDouble(value);
                  break;
              }
            }
          }
        }
      }
    }
    setSeries(0);

    core.reorder();

    if (removeThumbnail()) {
      // if the smallest resolution uses strips instead of tiles
      // then it's a "thumbnail" image instead of a real resolution
      // remove it by default, see https://github.com/ome/bioformats/issues/3757
      IFD lastResolution = ifds.get(getIFDIndex(core.size(0) - 1, 0));
      if (lastResolution.get(IFD.STRIP_BYTE_COUNTS) != null) {
        core.remove(0, core.size(0) - 1);
      }
    }
  }

  /* @see loci.formats.BaseTiffReader#initMetadataStore() */
  @Override
  protected void initMetadataStore() throws FormatException {
    super.initMetadataStore();

    MetadataStore store = makeFilterMetadata();
    boolean populatePlaneData = getImageCount() > 1 ||
      physicalDistanceFromTopEdge != null ||
      physicalDistanceFromLeftEdge != null;
    MetadataTools.populatePixels(store, this, populatePlaneData);

    String instrument = MetadataTools.createLSID("Instrument", 0);
    String objective = MetadataTools.createLSID("Objective", 0, 0);
    store.setInstrumentID(instrument, 0);
    store.setObjectiveID(objective, 0, 0);
    store.setObjectiveNominalMagnification(magnification, 0, 0);

    int lastImage = core.size() - 1;
    for (int i=0; i<getSeriesCount(); i++) {
      setSeries(i);

      store.setImageInstrumentRef(instrument, i);
      store.setObjectiveSettingsID(objective, i);

      if (i == 0) {
        if (physicalDistanceFromTopEdge != null) {
          Length yPos = FormatTools.getStagePosition(physicalDistanceFromTopEdge, UNITS.MM);
          for (int p=0; p<getImageCount(); p++) {
            store.setPlanePositionY(yPos, i, p);
          }
        }
        if (physicalDistanceFromLeftEdge != null) {
          Length xPos = FormatTools.getStagePosition(physicalDistanceFromLeftEdge, UNITS.MM);
          for (int p=0; p<getImageCount(); p++) {
            store.setPlanePositionX(xPos, i, p);
          }
        }
      }

      if (hasFlattenedResolutions() || i > extraImages) {
        store.setImageName("Series " + (i + 1), i);
      }
      else {
        if (i == 0) {
          store.setImageName("", i);
        }
        else if (core.flattenedIndex(i, 0) == labelIndex) {
          store.setImageName("label image", i);
        }
        else if (core.flattenedIndex(i, 0) == macroIndex) {
          store.setImageName("macro image", i);
        }
      }
      String comment = ((SVSCoreMetadata) getCurrentCore()).comment;
      store.setImageDescription(comment, i);

      if (getDatestamp() != null) {
        store.setImageAcquisitionDate(getDatestamp(), i);
      }

      for (int c=0; c<getEffectiveSizeC(); c++) {
        if (getEmission() != null) {
          store.setChannelEmissionWavelength(getEmission(), i, c);
        }

        if (getExcitation() != null) {
          store.setChannelExcitationWavelength(getExcitation(), i, c);
        }

        // display color not set here as investigation with Aperio ImageScope
        // indicates that the display color is only used when there is an
        // .afi file (see AFIReader)

        if (c < dyeNames.size()) {
          store.setChannelName(dyeNames.get(c), i, c);
        }
      }

      if (getImageCount() > 1) {
        for (int p=0; p<getImageCount(); p++) {
          if (p < zPosition.length && zPosition[p] != null) {
            store.setPlanePositionZ(FormatTools.createLength(zPosition[p], UNITS.REFERENCEFRAME), i, p);
          }
        }
      }

      Length pixelSize = ((SVSCoreMetadata) getCurrentCore()).pixelSize;
      if (pixelSize != null && pixelSize.value(UNITS.MICROMETER).doubleValue() - Constants.EPSILON > 0) {
        store.setPixelsPhysicalSizeX(pixelSize, i);
        store.setPixelsPhysicalSizeY(pixelSize, i);
      }
    }
    setSeries(0);
  }

  private int getIFDIndex(int coreIndex, int no) {
    int index = coreIndex;
    // coreCount is the number of pyramid resolutions (independent of flattening)
    int coreCount = core.flattenedSize() - extraImages;

    // this is the case where the requested IFD is within the pyramid
    if (coreIndex > 0 && coreIndex < coreCount) {
      if (core.get(0, 0).imageCount > 1) {
        index++;
      }
      else {
        index = coreCount - coreIndex;
      }
    }

    if ((coreIndex > 0 && coreIndex < coreCount) || no > 0) {
      for (int i=0; i<no; i++) {
        index += coreCount;
      }
      if (coreIndex == 0) {
        index++;
      }
    }
    else if (coreIndex >= coreCount) {
      if (core.get(0, 0).imageCount > 1) {
        for (int i=0; i<coreCount; i++) {
          index += core.get(0, i).imageCount;
        }
        index -= (coreCount - 1);
      }
      else {
        if (coreIndex == labelIndex) {
          return labelIndex;
        }
        if (coreIndex == macroIndex) {
          return macroIndex;
        }
      }
    }
    return index;
  }

  protected Length getEmission() {
    if (emissionWavelength != null && emissionWavelength > 0) {
      return FormatTools.getEmissionWavelength(emissionWavelength);
    }
    return null;
  }

  protected Length getExcitation() {
    if (excitationWavelength != null && excitationWavelength > 0) {
      return FormatTools.getExcitationWavelength(excitationWavelength);
    }
    return null;
  }

  protected Double getExposureTime() {
    if (exposureTime == null || exposureScale == null) {
      LOGGER.debug("Ignoring exposure time = {}, scale = {}",
        exposureTime, exposureScale);
      return null;
    }
    return exposureTime * exposureScale * 1000;
  }

  protected Timestamp getDatestamp() {
    if (date != null && time != null) {
      try {
        return new Timestamp(
          DateTools.formatDate(date + " " + time, DATE_FORMAT));
      }
      catch (Exception e) {
        LOGGER.debug("Failed to parse '" + date + " " + time + "'", e);
      }
    }
    return null;
  }

  protected Length[] getPhysicalSizes() {
    Length psizes[] = new Length[getSeriesCount()];
    for(int i = 0; i < getSeriesCount(); i++) {
      int[] pos = core.flattenedIndexes(i);
      SVSCoreMetadata c = (SVSCoreMetadata) core.get(pos[0], pos[1]);
      psizes[i] = c.pixelSize;
    }
    return psizes;
  }

  protected double getMagnification() {
    return magnification == null ? Double.NaN : magnification;
  }

  protected ArrayList<String> getDyeNames() {
    return dyeNames;
  }

  protected Color getDisplayColor() {
    return displayColor;
  }

  /**
   * Get the IFD corresponding to the given plane in the current series.
   * Initializes the underlying TiffParser if necessary.
   *
   * @param plane index
   * @return corresponding IFD
   */
  protected IFD getIFD(int no) {
    if (tiffParser == null) {
      initTiffParser();
    }
    int ifd = ((SVSCoreMetadata) getCurrentCore()).ifdIndex[no];
    return ifds.get(ifd);
  }

  /**
   * Get the index of the tile corresponding to given IFD (plane)
   * and tile XY indexes.
   *
   * @param ifd IFD for the requested tile's plane
   * @param x tile X index
   * @param y tile Y index
   * @return corresponding tile index
   */
  protected int getTileIndex(IFD ifd, int x, int y) throws FormatException {
    int rows = (int) ifd.getTilesPerColumn();
    int cols = (int) ifd.getTilesPerRow();

    if (x < 0 || x >= cols) {
      throw new IllegalArgumentException("X index " + x + " not in range [0, " + cols + ")");
    }
    if (y < 0 || y >= rows) {
      throw new IllegalArgumentException("Y index " + y + " not in range [0, " + rows + ")");
    }

    return (cols * y) + x;
  }

}
