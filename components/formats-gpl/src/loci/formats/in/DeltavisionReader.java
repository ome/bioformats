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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.IMinMaxStore;
import loci.formats.meta.MetadataStore;
import loci.formats.tiff.TiffParser;

import ome.xml.model.enums.Correction;
import ome.xml.model.enums.Immersion;
import ome.xml.model.primitives.Timestamp;

import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.UNITS;

/**
 * DeltavisionReader is the file format reader for Deltavision files.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class DeltavisionReader extends FormatReader {

  // -- Constants --

  public static final int DV_MAGIC_BYTES_1 = 0xa0c0;
  public static final int DV_MAGIC_BYTES_2 = 0xc0a0;

 /**
  * @deprecated Use {@link #DATE_FORMATS} instead
  */
  @Deprecated
  public static final String DATE_FORMAT = "E MMM d HH:mm:ss yyyy";
  public static final String[] DATE_FORMATS = {
    "E MMM d HH:mm:ss yyyy",
    "E MMM  d HH:mm:ss yyyy",
  };

  private static final short LITTLE_ENDIAN = -16224;
  private static final int HEADER_LENGTH = 1024;

  private static final int NEW_TYPE = 100;
  private static final int MAX_CHANNELS = 12;

  private static final String[] IMAGE_TYPES = new String[] {
    "normal", "Tilt-series", "Stereo tilt-series", "Averaged images",
    "Averaged stereo pairs"
  };

  // -- Fields --

  /** Size of extended header. */
  private int extSize;

  /** Size of one wave in the extended header. */
  protected int wSize;

  /** Size of one z section in the extended header. */
  protected int zSize;

  /** Size of one time element in the extended header. */
  protected int tSize;

  protected int panelSize;

  /** Number of tiles in X direction. */
  private int xTiles;

  /** Number of tiles in Y direction. */
  private int yTiles;

  /** Whether or not the stage moved backwards. */
  private boolean backwardsStageX = false;
  private boolean backwardsStageY = false;

  /**
   * The number of ints in each extended header section. These fields appear
   * to be all blank but need to be skipped to get to the floats afterwards
   */
  protected int numIntsPerSection;
  protected int numFloatsPerSection;

  /** Initialize an array of Extended Header Field structures. */
  protected DVExtHdrFields[] extHdrFields = null;

  private Double[] ndFilters;

  private int[] lengths;

  private String logFile;
  private String deconvolutionLogFile;

  private boolean truncatedFileFlag = false;

  private String imageSequence;
  private boolean newFileType = false;

  // toggle whether to treat timepoints as positions
  protected boolean positionInT = false;

  // -- Constructor --

  /** Constructs a new Deltavision reader. */
  public DeltavisionReader() {
    super("Deltavision",
      new String[] {"dv", "r3d", "r3d_d3d", "dv.log", "r3d.log"});
    suffixNecessary = false;
    suffixSufficient = false;
    domains = new String[] {FormatTools.LM_DOMAIN};
    hasCompanionFiles = true;
    datasetDescription = "One .dv, .r3d, or .d3d file and up to two " +
      "optional .log files";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String name, boolean open) {
    if (checkSuffix(name, "dv.log") || checkSuffix(name, "r3d.log") ||
      name.endsWith("_log.txt"))
    {
      return true;
    }
    if (checkSuffix(name, "pnl")) return false;
    return super.isThisType(name, open);
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    // Extra sanity check; if it's a TIFF, return false immediately, to avoid
    // false negatives.
    if(new TiffParser(stream).isValidHeader()) {
      return false;
    }
    final int blockLen = 212;
    if (!FormatTools.validStream(stream, blockLen, true)) return false;
    stream.seek(96);
    stream.order(true);
    int magic = stream.readShort() & 0xffff;
    boolean valid = magic == DV_MAGIC_BYTES_1 || magic == DV_MAGIC_BYTES_2;
    if (!valid) {
      return false;
    }
    stream.order(magic == (LITTLE_ENDIAN & 0xffff));
    stream.seek(208);
    boolean isMap = stream.readString(4).trim().equals("MAP");
    if (isMap) {
      return false;
    }
    stream.seek(0);
    int x = stream.readInt();
    int y = stream.readInt();
    int count = stream.readInt();
    // don't compare x * y * count to stream length,
    // as that will reject any truncated files
    // instead just check that at least one plane plus a header
    // could be read
    return x > 0 && y > 0 && count > 0 &&
      (HEADER_LENGTH + ((long) x * (long) y) <= stream.length());
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    final List<String> files = new ArrayList<String>();
    if (!noPixels) files.add(currentId);
    if (logFile != null) files.add(logFile);
    if (deconvolutionLogFile != null) files.add(deconvolutionLogFile);
    return files.toArray(new String[files.size()]);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int[] coords = getZCTCoords(no);
    long offset = getPlaneByteOffset(coords[0], coords[1], coords[2]);
    if (offset < in.length()) {
      in.seek(offset);
      readPlane(in, x, getSizeY() - h - y, w, h, buf);

      // reverse the order of the rows
      // planes are stored with the origin in the lower-left corner
      byte[] tmp = new byte[w * FormatTools.getBytesPerPixel(getPixelType())];
      for (int row=0; row<h/2; row++) {
        int src = row * tmp.length;
        int dest = (h - row - 1) * tmp.length;
        System.arraycopy(buf, src, tmp, 0, tmp.length);
        System.arraycopy(buf, dest, buf, src, tmp.length);
        System.arraycopy(tmp, 0, buf, dest, tmp.length);
      }
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      extSize = wSize = zSize = tSize = 0;
      panelSize = 0;
      numIntsPerSection = numFloatsPerSection = 0;
      extHdrFields = null;
      ndFilters = null;
      logFile = deconvolutionLogFile = null;
      lengths = null;
      backwardsStageX = false;
      backwardsStageY = false;
      xTiles = 0;
      yTiles = 0;
      imageSequence = null;
      newFileType = false;
    }
  }

  /**
   * This flag indicates if the file is known to be truncated.
   * If true, the dimension size information is kept
   * as it is specified in the file header, otherwise the length
   * of the file is used to override size information.
   *
   * This flag must be set before {@link #setId(String)} is called.
   *
   * Default is false.
   */
  public void setTruncatedFileFlag(boolean truncatedFileFlag) {
      FormatTools.assertId(currentId, false, 1);
      this.truncatedFileFlag = truncatedFileFlag;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    if (!checkSuffix(id, "dv")) {
      if (checkSuffix(id, "dv.log") || checkSuffix(id, "r3d.log")) {
        id = id.substring(0, id.lastIndexOf("."));
      }
      else if (id.endsWith("_log.txt")) {
        id = id.substring(0, id.lastIndexOf("_")) + ".dv";
      }
      Location file = new Location(id).getAbsoluteFile();
      if (!file.exists()) {
        Location dir = file.getParentFile();
        String[] list = dir.list(true);
        String name = file.getName();
        name = name.substring(0, name.lastIndexOf("."));
        for (String f : list) {
          if (checkSuffix(f, "dv") && f.startsWith(name)) {
            id = new Location(dir, f).getAbsolutePath();
            break;
          }
        }
      }
    }

    super.initFile(id);
    findLogFiles();

    if (in != null) {
      in.close();
    }
    in = new RandomAccessInputStream(currentId);
    initPixels();

    MetadataLevel metadataLevel = metadataOptions.getMetadataLevel();
    if (metadataLevel != MetadataLevel.MINIMUM) {
      initExtraMetadata();
    }
  }

  protected void initPixels() throws FormatException, IOException {
    LOGGER.info("Reading header");

    MetadataStore store = makeFilterMetadata();

    in.seek(96);
    in.order(true);

    boolean little = in.readShort() == LITTLE_ENDIAN;
    in.order(little);
    in.seek(0);

    int sizeX = in.readInt();
    int sizeY = in.readInt();
    int imageCount = in.readInt();
    int filePixelType = in.readInt();

    in.seek(160);
    int fileType = in.readShort();

    in.seek(180);
    int rawSizeT = in.readUnsignedShort();
    int numPanels = 0;
    if (fileType == NEW_TYPE) {
      in.seek(852);
      int secondaryT = in.readInt();
      if (secondaryT > 0 && (rawSizeT <= 0 || rawSizeT == 65535)) {
        rawSizeT = secondaryT;
      }
      in.seek(880);
      numPanels = in.readInt();
      in.seek(182);
    }
    int sizeT = rawSizeT == 0 ? 1 : rawSizeT;

    int sequence = in.readShort();

    in.seek(92);
    extSize = in.readInt();

    in.seek(196);
    int rawSizeC = in.readShort();
    int sizeC = rawSizeC == 0 ? 1 : rawSizeC;

    // --- compute some secondary values ---

    imageSequence = getImageSequence(sequence);

    int sizeZ = imageCount / (sizeC * sizeT);

    // --- populate core metadata ---

    LOGGER.info("Populating core metadata");

    CoreMetadata m = core.get(0);

    m.littleEndian = little;
    m.sizeX = sizeX;
    m.sizeY = sizeY;
    m.imageCount = imageCount;
    if (numPanels > 0) {
      sizeZ /= numPanels;
      m.imageCount /= numPanels;
    }

    String pixel = getPixelString(filePixelType);
    m.pixelType = getPixelType(filePixelType);

    m.dimensionOrder =
      "XY" + imageSequence.replaceAll("W", "C").replaceAll("P", "");

    int planeSize =
      getSizeX() * getSizeY() * FormatTools.getBytesPerPixel(getPixelType());
    int realPlaneCount =
      (int) ((in.length() - HEADER_LENGTH - extSize) / planeSize);
    if (realPlaneCount < getImageCount() && !truncatedFileFlag) {
      LOGGER.debug("Truncated file");
      m.imageCount = realPlaneCount;
      if (sizeZ == 1) {
        sizeT = realPlaneCount / sizeC;
      }
      else if (sizeT == 1) {
        sizeZ = realPlaneCount / sizeC;
        if ((realPlaneCount % sizeC) != 0) {
          sizeZ++;
          m.imageCount = sizeZ * sizeC;
        }
      }
      else if (getDimensionOrder().indexOf('Z') <
        getDimensionOrder().indexOf('T'))
      {
        sizeZ = realPlaneCount / (sizeC * sizeT);
        if (sizeZ == 0) {
          sizeT = 1;
          sizeZ = realPlaneCount / sizeC;
          if ((realPlaneCount % sizeC) != 0) {
            sizeZ++;
            m.imageCount = sizeZ * sizeC;
          }
        }
        if (getImageCount() > (sizeZ * sizeC * sizeT)) {
          m.imageCount = imageCount;
          sizeC = rawSizeC == 0 ? 1 : rawSizeC;
          sizeT = rawSizeT == 0 ? 1 : rawSizeT;
          sizeZ = getImageCount() / (sizeC * sizeT);
        }
      }
      else {
        sizeT = realPlaneCount / (sizeC * sizeZ);
      }
    }

    m.sizeT = sizeT;
    m.sizeC = sizeC;
    m.sizeZ = sizeZ;

    m.rgb = false;
    m.interleaved = false;
    m.metadataComplete = true;
    m.indexed = false;
    m.falseColor = false;

    // --- parse extended header ---

    in.seek(128);
    numIntsPerSection = in.readShort();
    numFloatsPerSection = in.readShort();

    LOGGER.info("Reading extended header");

    setOffsetInfo(getSizeZ(), getSizeC(), getSizeT(), numPanels == 0 ? 1 : numPanels);
    extHdrFields = new DVExtHdrFields[imageCount];

    ndFilters = new Double[getSizeC()];

    final List<Length> uniqueTileX = new ArrayList<Length>();
    final List<Length> uniqueTileY = new ArrayList<Length>();

    // Run through every image and fill in the
    // Extended Header information array for that image
    int offset = HEADER_LENGTH + numIntsPerSection * 4;
    boolean hasZeroX = false;
    boolean hasZeroY = false;
    for (int i=0; i<imageCount; i++) {
      // -- read in the extended header data --

      in.seek(offset + getTotalPlaneHeaderSize() * i);
      DVExtHdrFields hdr = new DVExtHdrFields(in);
      extHdrFields[i] = hdr;

      if (!uniqueTileX.contains(hdr.stageXCoord) &&
              hdr.stageXCoord.value().floatValue() != 0) {
        uniqueTileX.add(hdr.stageXCoord);
      }
      else if (hdr.stageXCoord.value().floatValue() == 0) {
        hasZeroX = true;
      }

      if (!uniqueTileY.contains(hdr.stageYCoord) &&
              hdr.stageYCoord.value().floatValue() != 0) {
        uniqueTileY.add(hdr.stageYCoord);
      }
      else if (hdr.stageYCoord.value().floatValue() == 0) {
        hasZeroY = true;
      }
    }

    xTiles = uniqueTileX.size();
    yTiles = uniqueTileY.size();

    if (xTiles > 1 || yTiles > 1) {
      if (hasZeroX) {
        xTiles++;
      }
      if (hasZeroY) {
        yTiles++;
      }
    }

    if (xTiles > 1) {
      final Number x0 = uniqueTileX.get(0).value(UNITS.REFERENCEFRAME);
      final Number x1 = uniqueTileX.get(1).value(UNITS.REFERENCEFRAME);
      if (x1.floatValue() < x0.floatValue()) {
        backwardsStageX = true;
      }
    }

    if (yTiles > 1) {
       // TODO: use compareTo once Length implements Comparable
      final Number y0 = uniqueTileY.get(0).value(UNITS.REFERENCEFRAME);
      final Number y1 = uniqueTileY.get(1).value(UNITS.REFERENCEFRAME);
      if (y1.floatValue() < y0.floatValue()) {
        backwardsStageY = true;
      }
    }

    int nStagePositions = xTiles * yTiles;

    if (positionInT) {
      nStagePositions = getSizeT();
      if (xTiles * yTiles != nStagePositions) {
        // if positions are not uniform, we can't reliably determine
        // the size of the grid or the direction in which the stage moved
        xTiles = nStagePositions;
        yTiles = 1;
        backwardsStageX = false;
        backwardsStageY = false;
      }
    }

    // older NEW_TYPE files may have 0 recorded panels but
    // multiple series are still expected
    if ((fileType != NEW_TYPE || numPanels == 0) &&
      nStagePositions > 0 && nStagePositions <= getSizeT())
    {
      int t = getSizeT();
      m.sizeT /= nStagePositions;
      if (getSizeT() * nStagePositions != t) {
        m.sizeT = t;
        nStagePositions = 1;
      }
      else {
        m.imageCount /= nStagePositions;
      }
    }
    else {
      newFileType = true;
      nStagePositions = numPanels == 0 ? 1 : numPanels;
    }

    if (nStagePositions > 1) {
      CoreMetadata originalCore = core.get(0);
      core.clear();
      for (int i=0; i<nStagePositions; i++) {
        core.add(new CoreMetadata(originalCore));
      }
    }

    lengths = new int[4];
    int lengthIndex = 0;
    int dimIndex = 0;

    while (lengthIndex < lengths.length) {
      char dim = imageSequence.charAt(dimIndex++);

      switch (dim) {
        case 'Z':
          lengths[lengthIndex++] = getSizeZ();
          break;
        case 'W':
          lengths[lengthIndex++] = getSizeC();
          break;
        case 'T':
          if (!newFileType) {
            lengths[lengthIndex++] = getSeriesCount();
          }
          lengths[lengthIndex++] = getSizeT();
          break;
        case 'P':
          if (newFileType) {
            lengths[lengthIndex++] = getSeriesCount();
          }
          break;
      }
    }

    // --- populate original metadata ---

    LOGGER.info("Populating original metadata");

    addGlobalMeta("ImageWidth", sizeX);
    addGlobalMeta("ImageHeight", sizeY);
    addGlobalMeta("NumberOfImages", imageCount);

    addGlobalMeta("PixelType", pixel);

    addGlobalMeta("Number of timepoints", rawSizeT);
    addGlobalMeta("Number of panels", numPanels);

    addGlobalMeta("Image sequence", imageSequence);

    addGlobalMeta("Number of wavelengths", rawSizeC);
    addGlobalMeta("Number of focal planes", sizeZ);

    for (int series=0; series<getSeriesCount(); series++) {
      setSeries(series);
      for (int plane=0; plane<getImageCount(); plane++) {
        int[] coords = getZCTCoords(plane);

        DVExtHdrFields hdr = extHdrFields[getPlaneIndex(series, plane)];

        // -- record original metadata --

        String prefix =
          "Extended header Z" + coords[0] + " W" + coords[1] + " T" + coords[2];
        for (Map.Entry<String, Object> entry : hdr.asMap().entrySet()) {
          addSeriesMeta(prefix + ":" + entry.getKey(), entry.getValue());
        }

        addGlobalMetaList("X position for position", hdr.stageXCoord);
        addGlobalMetaList("Y position for position", hdr.stageYCoord);
        addGlobalMetaList("Z position for position", hdr.stageZCoord);
      }
    }
    setSeries(0);

    // --- populate OME metadata ---

    LOGGER.info("Populating OME metadata");

    MetadataTools.populatePixels(store, this, true);

    // link Instrument and Image
    String instrumentID = MetadataTools.createLSID("Instrument", 0);
    store.setInstrumentID(instrumentID, 0);

    for (int i=0; i<getSeriesCount(); i++) {
      store.setImageInstrumentRef(instrumentID, i);
    }
  }

  protected void initExtraMetadata() throws FormatException, IOException {
    MetadataStore store = makeFilterMetadata();

    // --- read in the image header data ---

    LOGGER.info("Reading header");

    in.seek(16);

    int subImageStartX = in.readInt();
    int subImageStartY = in.readInt();
    int subImageStartZ = in.readInt();
    int pixelSamplingX = in.readInt();
    int pixelSamplingY = in.readInt();
    int pixelSamplingZ = in.readInt();

    float pixX = in.readFloat();
    float pixY = in.readFloat();
    float pixZ = in.readFloat();
    float xAxisAngle = in.readFloat();
    float yAxisAngle = in.readFloat();
    float zAxisAngle = in.readFloat();
    int xAxisSeq = in.readInt();
    int yAxisSeq = in.readInt();
    int zAxisSeq = in.readInt();

    float[] minWave = new float[MAX_CHANNELS];
    float[] maxWave = new float[MAX_CHANNELS];

    minWave[0] = in.readFloat();
    maxWave[0] = in.readFloat();

    float meanIntensity = in.readFloat();
    int spaceGroupNumber = in.readInt();

    in.seek(132);

    short numSubResSets = in.readShort();
    short zAxisReductionQuotient = in.readShort();

    for (int i=1; i<=3; i++) {
      minWave[i] = in.readFloat();
      maxWave[i] = in.readFloat();
    }

    int fileType = in.readShort();
    int lensID = in.readShort();

    in.seek(172);

    minWave[4] = in.readFloat();
    maxWave[4] = in.readFloat();

    in.seek(184);

    float xTiltAngle = in.readFloat();
    float yTiltAngle = in.readFloat();
    float zTiltAngle = in.readFloat();

    in.skipBytes(2);

    short[] waves = new short[MAX_CHANNELS];
    // only first 5 wavelengths are here (for compatibility with old DV files)
    for (int i=0; i<5; i++) {
      waves[i] = in.readShort();
    }

    float xOrigin = in.readFloat();
    float yOrigin = in.readFloat();
    float zOrigin = in.readFloat();

    // documentation suggests that the title count uses 4 bytes
    // but 2 bytes is correct in practice as valid values are
    // in the range [0, 10]
    in.skipBytes(2);
    int numTitles = in.readShort();

    // "new" type DV files limit the number of titles so
    // that metadata describing additional channels can be
    // packed into the same size header
    int titleCount = fileType < NEW_TYPE ? 10 : 4;
    String[] title = new String[titleCount];
    for (int i=0; i<title.length; i++) {
      // Make sure that "null" characters are stripped out
      title[i] = in.readByteToString(80).replaceAll("\0", "");
    }

    // intensity and wavelength data for channels 6-12 follows titles
    String timestamp = null;
    if (fileType == NEW_TYPE) {
      for (int i=5; i<MAX_CHANNELS; i++) {
        minWave[i] = in.readFloat();
      }
      in.skipBytes(16); // undefined
      for (int i=5; i<MAX_CHANNELS; i++) {
        maxWave[i] = in.readFloat();
      }
      in.skipBytes(16); // undefined
      // skip mean intensities for now
      in.skipBytes(4 * (MAX_CHANNELS - 5));
      in.skipBytes(16); // undefined

      for (int i=5; i<MAX_CHANNELS; i++) {
        waves[i] = in.readShort();
      }

      in.seek(844);
      // timestamp stored in seconds; DateTools expects milliseconds
      long acqDate = (long) (in.readDouble() * 1000);
      if (acqDate > 0) {
        timestamp = DateTools.convertDate(acqDate, DateTools.UNIX);
      }
    }

    // --- compute some secondary values ---

    String imageType =
      fileType < IMAGE_TYPES.length ? IMAGE_TYPES[fileType] : "unknown";

    String imageDesc = title[0];
    if (imageDesc != null && imageDesc.length() == 0) imageDesc = null;

    // --- populate original metadata ---

    LOGGER.info("Populating original metadata");

    addGlobalMeta("Sub-image starting point (X)", subImageStartX);
    addGlobalMeta("Sub-image starting point (Y)", subImageStartY);
    addGlobalMeta("Sub-image starting point (Z)", subImageStartZ);
    addGlobalMeta("Pixel sampling size (X)", pixelSamplingX);
    addGlobalMeta("Pixel sampling size (Y)", pixelSamplingY);
    addGlobalMeta("Pixel sampling size (Z)", pixelSamplingZ);

    addGlobalMeta("X element length (in um)", pixX);
    addGlobalMeta("Y element length (in um)", pixY);
    addGlobalMeta("Z element length (in um)", pixZ);
    addGlobalMeta("X axis angle", xAxisAngle);
    addGlobalMeta("Y axis angle", yAxisAngle);
    addGlobalMeta("Z axis angle", zAxisAngle);
    addGlobalMeta("Column axis sequence", xAxisSeq);
    addGlobalMeta("Row axis sequence", yAxisSeq);
    addGlobalMeta("Section axis sequence", zAxisSeq);

    addGlobalMeta("Image Type", imageType);
    addGlobalMeta("Lens ID Number", lensID);

    addGlobalMeta("X axis tilt angle", xTiltAngle);
    addGlobalMeta("Y axis tilt angle", yTiltAngle);
    addGlobalMeta("Z axis tilt angle", zTiltAngle);

    for (int i=0; i<waves.length; i++) {
      addGlobalMeta("Wavelength " + (i + 1) + " (in nm)", waves[i]);
    }

    addGlobalMeta("X origin (in um)", xOrigin);
    addGlobalMeta("Y origin (in um)", yOrigin);
    addGlobalMeta("Z origin (in um)", zOrigin);

    addGlobalMeta("Valid titles", numTitles);
    for (String t : title) {
      addGlobalMetaList("Title", t);
    }

    for (int i=0; i<minWave.length; i++) {
      addGlobalMeta("Wavelength " + (i + 1) + " min. intensity", minWave[i]);
      addGlobalMeta("Wavelength " + (i + 1) + " max. intensity", maxWave[i]);
    }

    addGlobalMeta("Wavelength 1 mean intensity", meanIntensity);
    addGlobalMeta("Space group number", spaceGroupNumber);

    addGlobalMeta("Number of Sub-resolution sets", numSubResSets);
    addGlobalMeta("Z axis reduction quotient", zAxisReductionQuotient);

    // --- populate OME metadata ---

    LOGGER.info("Populating OME metadata");

    for (int series=0; series<getSeriesCount(); series++) {
      if (store instanceof IMinMaxStore) {
        IMinMaxStore minMaxStore = (IMinMaxStore) store;
        for (int i=0; i<minWave.length; i++) {
          if (i < getEffectiveSizeC()) {
            minMaxStore.setChannelGlobalMinMax(
              i, minWave[i], maxWave[i], series);
          }
        }
      }

      Double x = new Double(pixX);
      Length sizeX = FormatTools.getPhysicalSizeX(x);
      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, series);
      }
      Double y = new Double(pixY);
      Length sizeY = FormatTools.getPhysicalSizeY(y);
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, series);
      }
      Double z = new Double(pixZ);
      Length sizeZ = FormatTools.getPhysicalSizeZ(z);
      if (sizeZ != null) {
        store.setPixelsPhysicalSizeZ(sizeZ, series);
      }

      store.setImageDescription(imageDesc, series);
    }

    populateObjective(store, lensID);

    // if matching log file exists, extract key/value pairs from it
    boolean logFound = isGroupFiles() ? parseLogFile(store) : false;
    if (isGroupFiles()) {
        if (deconvolutionLogFile != null &&
                new Location(deconvolutionLogFile).exists()) {
            try (RandomAccessInputStream s = new RandomAccessInputStream(deconvolutionLogFile)) {
              parseDeconvolutionLog(s, store);
            }
        }
    }

    // timestamp stored in the .dv file takes precedence
    // over the timestamp in the log file
    if (timestamp != null) {
      for (int series=0; series<getSeriesCount(); series++) {
        store.setImageAcquisitionDate(new Timestamp(timestamp), series);
      }
    }

    if (xTiles * yTiles > getSeriesCount()) {
      if (xTiles == getSeriesCount()) {
        yTiles = 1;
      }
      else if (yTiles == getSeriesCount()) {
        xTiles = 1;
      }
      else {
        xTiles = 1;
        yTiles = 1;
      }
    }

    if (getSeriesCount() == 1) {
      xTiles = 1;
      yTiles = 1;
      backwardsStageX = false;
      backwardsStageY = false;
    }

    for (int series=0; series<getSeriesCount(); series++) {
      int seriesIndex = series;
      if (backwardsStageX || backwardsStageY) {
        int x = series % xTiles;
        int y = series / xTiles;
        int xIndex = backwardsStageX ? xTiles - x - 1 : x;
        int yIndex = backwardsStageY ? yTiles - y - 1 : y;
        seriesIndex = yIndex * xTiles + xIndex;
      }

      Double[] expTime = new Double[getSizeC()];
      for (int i=0; i<getImageCount(); i++) {
        int[] coords = getZCTCoords(i);

        DVExtHdrFields hdr = extHdrFields[getPlaneIndex(seriesIndex, i)];
        if (expTime[coords[1]] == null) {
          expTime[coords[1]] = new Double(hdr.expTime);
        }

        // plane timing
        store.setPlaneDeltaT(
          new Time(new Double(hdr.timeStampSeconds), UNITS.SECOND), series, i);
        store.setPlaneExposureTime(new Time(expTime[coords[1]], UNITS.SECOND), series, i);

        // stage position
        if (!logFound || getSeriesCount() > 1) {
          store.setPlanePositionX(hdr.stageXCoord, series, i);
          store.setPlanePositionY(hdr.stageYCoord, series, i);
          store.setPlanePositionZ(hdr.stageZCoord, series, i);
        }

        if (coords[0] == 0 && coords[2] == 0) {
          int w = coords[1];

          Length emission =
            FormatTools.getEmissionWavelength(new Double(waves[w]));
          Length excitation =
            FormatTools.getExcitationWavelength(new Double(hdr.exWavelen));

          if (emission != null) {
            store.setChannelEmissionWavelength(emission, series, w);
          }
          if (excitation != null) {
            store.setChannelExcitationWavelength(excitation, series, w);
          }
          if (ndFilters[w] == null) ndFilters[w] = new Double(hdr.ndFilter);
          store.setChannelNDFilter(ndFilters[w], series, w);
        }
      }
    }
  }

  // -- Helper methods --

  /** Get a descriptive string representing the pixel type. */
  private String getPixelString(int filePixelType) {
     switch (filePixelType) {
      case 0:
        return "8 bit unsigned integer";
      case 1:
        return "16 bit signed integer";
      case 2:
        return "32 bit floating point";
      case 3:
        return "16 bit complex";
      case 4:
        return "64 bit complex";
      case 6:
        return "16 bit unsigned integer";
    }
    return "unknown";
  }

  /** Get the OME pixel type from the pixel type stored in the file. */
  private int getPixelType(int filePixelType) {
     switch (filePixelType) {
      case 0:
        return FormatTools.UINT8;
      case 1:
        return FormatTools.INT16;
      case 2:
        return FormatTools.FLOAT;
      case 3:
        return FormatTools.INT16;
      case 4:
        return FormatTools.FLOAT;
      case 6:
        return FormatTools.UINT16;
      case 7:
        return FormatTools.INT32;
      case 8:
        return FormatTools.DOUBLE;
    }
    return FormatTools.UINT8;
  }

  /** Get the image sequence string. */
  private String getImageSequence(int imageSequence) {
    switch (imageSequence) {
      case 0:
        return "ZTWP";
      case 1:
        return "WZTP";
      case 2:
        return "ZWTP";
      case 3:
        return "ZPWT";
      case 4:
        return "ZWPT";
      case 5:
        return "WZPT";
      case 6:
        return "WPTZ";
      case 7:
        return "PWTZ";
      case 8:
        return "PTWZ";
      case 9:
        return "PZWT";
      case 10:
        return "PWZT";
      case 11:
        return "WPZT";
      case 12:
        return "WTPZ";
      case 13:
        return "TWPZ";
      case 14:
        return "TPWZ";
      case 65536:
        return "WZTP";
    }
    return "ZTWP";
  }

  /**
   * This method calculates the size of a w, t, z section depending on which
   * sequence is being used
   * @param numZSections
   * @param numWaves
   * @param numTimes
   * @param numPanels
   */
  private void setOffsetInfo(int numZSections,
    int numWaves, int numTimes, int numPanels)
  {
    int smallOffset = getTotalPlaneHeaderSize();

    int[] lens = new int[4];

    int zIndex = imageSequence.indexOf('Z');
    int wIndex = imageSequence.indexOf('W');
    int tIndex = imageSequence.indexOf('T');
    int pIndex = imageSequence.indexOf('P');

    lens[zIndex] = numZSections;
    lens[wIndex] = numWaves;
    lens[tIndex] = numTimes;
    lens[pIndex] = numPanels;

    int[] sizes = new int[4];
    sizes[0] = smallOffset;
    for (int i=1; i<sizes.length; i++) {
      sizes[i] = sizes[i - 1] * lens[i - 1];
    }

    zSize = sizes[zIndex];
    wSize = sizes[wIndex];
    tSize = sizes[tIndex];
    panelSize = sizes[pIndex];
  }

  private int getTotalPlaneHeaderSize() {
    return (numIntsPerSection + numFloatsPerSection) * 4;
  }

  private int getPlaneIndex(int series, int plane) {
    int zIndex = imageSequence.indexOf('Z');
    int wIndex = imageSequence.indexOf('W');
    int tIndex = imageSequence.indexOf('T');
    int pIndex = imageSequence.indexOf('P');
    int[] lens = new int[4];
    lens[zIndex] = getSizeZ();
    lens[wIndex] = getSizeC();
    lens[tIndex] = getSizeT();
    lens[pIndex] = getSeriesCount();

    int[] coords = getZCTCoords(plane);
    int[] realCoords = new int[4];
    realCoords[zIndex] = coords[0];
    realCoords[wIndex] = coords[1];
    realCoords[tIndex] = coords[2];
    realCoords[pIndex] = series;
    return FormatTools.positionToRaster(lens, realCoords);
  }

  /**
   * Given any specific Z, W, and T of a plane, determine the absolute
   * byte offset of the plane in the file.
   * @param currentZ
   * @param currentW
   * @param currentT
   */
  public long getPlaneByteOffset(int currentZ, int currentW, int currentT) {
      FormatTools.assertId(currentId, true, 1);
      int[] newCoords = new int[4];
      int coordIndex = 0;
      int dimIndex = 2;

      String order = getDimensionOrder();
      int index = imageSequence.indexOf("P") + 2;
      String start = order.substring(0, index);
      String end = "";
      if (index < order.length()) {
        end = order.substring(index);
      }
      order = start + "P" + end;

      while (coordIndex < newCoords.length) {
        char dim = order.charAt(dimIndex++);

        switch (dim) {
          case 'Z':
            newCoords[coordIndex++] = currentZ;
            break;
          case 'C':
            newCoords[coordIndex++] = currentW;
            break;
          case 'T':
            if (!newFileType) {
              newCoords[coordIndex++] = getSeries();
            }
            newCoords[coordIndex++] = currentT;
            break;
          case 'P':
            if (newFileType) {
              newCoords[coordIndex++] = getSeries();
            }
            break;
        }
      }

      int planeIndex = FormatTools.positionToRaster(lengths, newCoords);
      long planeSize = (long) FormatTools.getPlaneSize(this);
      long planeOffset = planeSize * planeIndex;
      long offset = planeOffset + HEADER_LENGTH + extSize;
      return offset;
  }

  /** Find the log files. */
  private void findLogFiles() throws IOException {
    if (getCurrentFile().lastIndexOf(".") == -1) {
      // The current file name has no extension, skip trying to find the
      // log file(s).
      logFile = null;
      deconvolutionLogFile = null;
      return;
    }
    if (getCurrentFile().endsWith("_D3D.dv")) {
      logFile = getCurrentFile();
      logFile = logFile.substring(0, logFile.indexOf("_D3D.dv")) + ".dv.log";
    }
    else {
      logFile = getCurrentFile() + ".log";
      if (!new Location(logFile).exists()) {
        logFile = getCurrentFile();
        logFile = logFile.substring(0, logFile.lastIndexOf(".")) + ".log";
      }
    }
    if (!new Location(logFile).exists()) logFile = null;

    int dot = getCurrentFile().lastIndexOf(".");
    String base = getCurrentFile().substring(0, dot);
    deconvolutionLogFile = base + "_log.txt";
    if (!new Location(deconvolutionLogFile).exists()) {
      deconvolutionLogFile = null;
    }
  }

  /** Extract metadata from associated log file, if it exists. */
  private boolean parseLogFile(MetadataStore store)
    throws FormatException, IOException
  {
    if (logFile == null || !new Location(logFile).exists()) {
      logFile = null;
      return false;
    }

    LOGGER.info("Parsing log file");

    String[] lines = DataTools.readFile(logFile).split("[\r\n]");

    String key, value = "", prefix = "";

    int currentImage = 0;

    List<String> channelNames = new ArrayList<String>();
    List<Double> filters = new ArrayList<Double>();

    for (String line : lines) {
      int colon = line.indexOf(':');
      if (colon >= 0 && colon < line.length() - 1 && !line.startsWith("Created")) {
        key = line.substring(0, colon).trim();

        value = line.substring(colon + 1).trim();
        if (value.equals("") && !key.equals("")) prefix = key;
        addGlobalMeta(prefix + " " + key, value);

        // Objective properties
        if (key.equals("Objective")) {
          // assume first word is the manufacturer's name
          int space = value.indexOf(' ');
          if (space != -1) {
            String manufacturer = value.substring(0, space);
            String extra = value.substring(space + 1);

            String[] tokens = extra.split(",");

            store.setObjectiveManufacturer(manufacturer, 0, 0);

            String magnification = "", na = "";

            if (tokens.length >= 1) {
              int end = tokens[0].indexOf('X');
              if (end > 0) magnification = tokens[0].substring(0, end);
              int start = tokens[0].indexOf('/');
              if (start >= 0) na = tokens[0].substring(start + 1);
            }

            try {
              Double mag = new Double(magnification);
              store.setObjectiveNominalMagnification(mag, 0, 0);
            }
            catch (NumberFormatException e) {
              LOGGER.warn("Could not parse magnification '{}'", magnification);
            }
            try {
              store.setObjectiveLensNA(new Double(na), 0, 0);
            }
            catch (NumberFormatException e) {
              LOGGER.warn("Could not parse N.A. '{}'", na);
            }
            if (tokens.length >= 2) {
              store.setObjectiveCorrection(
                MetadataTools.getCorrection(tokens[1]), 0, 0);
            }
            // TODO:  Token #2 is the microscope model name.
            if (tokens.length > 3) store.setObjectiveModel(tokens[3], 0, 0);
          }
        }
        else if (key.equalsIgnoreCase("Lens ID")) {
          if (value.indexOf(',') != -1) {
            value = value.substring(0, value.indexOf(','));
          }
          if (value.indexOf(' ') != -1) {
            value = value.substring(value.indexOf(' ') + 1);
          }
          if (!value.equals("null")) {
            String objectiveID = "Objective:" + value;
            store.setObjectiveID(objectiveID, 0, 0);
            for (int series=0; series<getSeriesCount(); series++) {
              store.setObjectiveSettingsID(objectiveID, series);
            }
            store.setObjectiveCorrection(
              MetadataTools.getCorrection("Other"), 0, 0);
            store.setObjectiveImmersion(
              MetadataTools.getImmersion("Other"), 0, 0);
          }
        }
        // Image properties
        else if (key.equals("Pixel Size")) {
          String[] pixelSizes = value.split(" ");

          for (int q=0; q<pixelSizes.length; q++) {
            Double size = null;
            try {
              size = new Double(pixelSizes[q].trim());
            }
            catch (NumberFormatException e) {
              LOGGER.warn("Could not parse pixel size '{}'",
                pixelSizes[q].trim());
            }
            if (q == 0) {
              Length sizeX = FormatTools.getPhysicalSizeX(size);
              if (sizeX != null) {
                for (int series=0; series<getSeriesCount(); series++) {
                  store.setPixelsPhysicalSizeX(sizeX, series);
                }
              }
            }
            if (q == 1) {
              Length sizeY = FormatTools.getPhysicalSizeY(size);
              if (sizeY != null) {
                for (int series=0; series<getSeriesCount(); series++) {
                  store.setPixelsPhysicalSizeY(sizeY, series);
                }
              }
            }
            if (q == 2) {
              Length sizeZ = FormatTools.getPhysicalSizeZ(size);
              if (sizeZ != null) {
                for (int series=0; series<getSeriesCount(); series++) {
                  store.setPixelsPhysicalSizeZ(sizeZ, series);
                }
              }
            }
          }
        }
        else if (key.equals("Binning")) {
          store.setDetectorType(MetadataTools.getDetectorType("Other"), 0, 0);
          String detectorID = MetadataTools.createLSID("Detector", 0, 0);
          store.setDetectorID(detectorID, 0, 0);
          for (int series=0; series<getSeriesCount(); series++) {
            for (int c=0; c<getSizeC(); c++) {
              store.setDetectorSettingsBinning(
                MetadataTools.getBinning(value), series, c);
              // link DetectorSettings to an actual Detector
              store.setDetectorSettingsID(detectorID, series, c);
            }
          }
        }
        // Camera properties
        else if (key.equals("Type")) {
          store.setDetectorModel(value, 0, 0);
        }
        else if (key.equals("Gain")) {
          value = value.replaceAll("X", "");
          try {
            String detectorID = MetadataTools.createLSID("Detector", 0, 0);
            store.setDetectorID(detectorID, 0, 0);
            for (int series=0; series<getSeriesCount(); series++) {
              for (int c=0; c<getSizeC(); c++) {
                store.setDetectorSettingsGain(new Double(value), series, c);
                store.setDetectorSettingsID(detectorID, series, c);
              }
            }
          }
          catch (NumberFormatException e) {
            LOGGER.warn("Could not parse gain '{}'", value);
          }
        }
        else if (key.equals("Speed")) {
          value = value.replaceAll("KHz", "");
          try {
            double khz = Double.parseDouble(value);
            String detectorID = MetadataTools.createLSID("Detector", 0, 0);
            store.setDetectorID(detectorID, 0, 0);
            for (int series=0; series<getSeriesCount(); series++) {
              for (int c=0; c<getSizeC(); c++) {
                store.setDetectorSettingsReadOutRate(
                        new Frequency(khz, UNITS.KILOHERTZ), series, c);
                store.setDetectorSettingsID(detectorID, series, c);
              }
            }
          }
          catch (NumberFormatException e) {
            LOGGER.warn("Could not parse read-out rate '{}'", value);
          }
        }
        else if (key.equals("Temp Setting")) {
          value = value.replaceAll("C", "").trim();
          try {
            // this is the camera temperature, not the environment temperature
            //store.setImagingEnvironmentTemperature(value, 0);
          }
          catch (NumberFormatException e) {
            LOGGER.warn("Could not parse temperature '{}'", value);
          }
        }
        // Plane properties
        else if (key.equals("EM filter")) {
          if (!channelNames.contains(value)) {
            channelNames.add(value);
          }
        }
        else if (key.equals("ND filter")) {
          value = value.replaceAll("%", "");
          try {
            double nd = Double.parseDouble(value) / 100;
            if (!filters.contains(nd)) {
              filters.add(nd);
            }
          }
          catch (NumberFormatException exc) {
            // "BLANK" is the default (e.g. for deconvolved data),
            // so no need to log it explicitly
            if (!value.equals("BLANK")) {
              LOGGER.warn("Could not parse ND filter '{}'", value);
            }
            filters.add(null);
          }
          catch (IllegalArgumentException e) {
            LOGGER.debug("", e);
          }
        }
        else if (key.equals("Stage coordinates")) {
          if (value.length() > 1) {
            value = value.substring(1, value.length() - 1);
          }
          String[] coords = value.split(",");
          for (int i=0; i<coords.length; i++) {
            Length p = null;
            try {
              final Double number = Double.valueOf(coords[i]);
              p = new Length(number, UNITS.REFERENCEFRAME);
            }
            catch (NumberFormatException e) {
              LOGGER.warn("Could not parse stage coordinate '{}'", coords[i]);
            }

            if (currentImage < getImageCount() && getSeriesCount() == 1) {
              // NB: the positions are intentionally only populated for the
              //     first series.  Positions for the other series are parsed
              //     from the extended header.
              if (i == 0) {
                store.setPlanePositionX(p, 0, currentImage);
              }
              if (i == 1) {
                store.setPlanePositionY(p, 0, currentImage);
              }
              if (i == 2) {
                store.setPlanePositionZ(p, 0, currentImage);
              }
            }
          }

          currentImage++;
        }
      }
      else if (line.startsWith("Image")) prefix = line;
      else if (line.startsWith("Created")) {
        if (line.length() > 8) line = line.substring(8).trim();
        String date = DateTools.formatDate(line, DATE_FORMATS);
        if (date != null) {
          for (int series=0; series<getSeriesCount(); series++) {
            store.setImageAcquisitionDate(new Timestamp(date), series);
          }
        }
        else {
          LOGGER.warn("Could not parse date '{}'", line);
        }
      }
      else if (line.startsWith("#KEY")) {
        line = line.substring(line.indexOf(" ")).trim();
        int split = line.indexOf(":");
        if (split < 0) {
          split = line.indexOf(" ");
        }
        key = line.substring(0, split).trim();
        value = line.substring(split + 1).trim();
        addGlobalMeta(key, value);
      }
      else if (line.endsWith(":")) {
        prefix = line.substring(0, line.length() - 1);
      }
      else if (!line.startsWith("#") && !line.replace("-", "").isEmpty() &&
        !prefix.isEmpty())
      {
        addGlobalMetaList(prefix, line);
      }
    }

    for (int series=0; series<getSeriesCount(); series++) {
      for (int c=0; c<getEffectiveSizeC(); c++) {
        if (c < channelNames.size()) {
          store.setChannelName(channelNames.get(c), series, c);
        }
        if (c < filters.size()) {
          ndFilters[c] = filters.get(c);
        }
      }
    }

    return true;
  }

  /** Parse deconvolution output, if it exists. */
  private void parseDeconvolutionLog(RandomAccessInputStream s, MetadataStore store) throws IOException {

    LOGGER.info("Parsing deconvolution log file");

    boolean doStatistics = false;
    int cc = 0, tt = 0;
    String previousLine = null;

    while (s.getFilePointer() < s.length() - 1) {
      String line = s.readLine();
      if (line == null || line.length() == 0) continue;

      if (doStatistics) {
        String[] keys = line.split("  ");
        final List<String> realKeys = new ArrayList<String>();
        for (int i=0; i<keys.length; i++) {
          keys[i] = keys[i].trim();
          if (!keys[i].isEmpty()) realKeys.add(keys[i]);
        }
        keys = realKeys.toArray(new String[0]);

        s.readLine();

        line = s.readLine().trim();
        while (line != null && line.length() != 0) {
          String[] values = line.split(" ");
          final List<String> realValues = new ArrayList<String>();
          for (int i=0; i<values.length; i++) {
            values[i] = values[i].trim();
            if (!values[i].isEmpty()) { realValues.add(values[i]); }
          }
          values = realValues.toArray(new String[0]);

          try {
            if (values.length > 0) {
              int zz = Integer.parseInt(values[0]) - 1;
              int index = getIndex(zz, cc, tt);
              for (int i=1; i<keys.length; i++) {
                addGlobalMeta("Plane " + index + " " + keys[i], values[i]);
              }
            }
          }
          catch (NumberFormatException e) {
            LOGGER.warn("Could not parse Z position '{}'", values[0]);
          }
          catch (IllegalArgumentException iae) {
            LOGGER.debug("", iae);
          }
          line = s.readLine().trim();
        }
      }
      else {
        int index = line.indexOf(".\t");
        if (index != -1) {
          String key = line.substring(0, index).trim();
          String value = line.substring(index + 2).trim();

          // remove trailing dots from key
          while (key.endsWith(".")) {
            key = key.substring(0, key.length() - 1);
          }

          if (previousLine != null &&
            (previousLine.endsWith("Deconvolution Results:") ||
            previousLine.endsWith("open OTF")))
          {
            addGlobalMeta(previousLine + " " +  key, value);
          }
          else addGlobalMeta(key, value);
        }
      }

      if (line.indexOf("correcting time point\t") != -1) {
        int index = line.indexOf("time point\t") + 11;
        if (index > 10) {
          String t = line.substring(index, line.indexOf(",", index));
          try {
            tt = Integer.parseInt(t) - 1;
          }
          catch (NumberFormatException e) {
            LOGGER.warn("Could not parse timepoint '{}'", t);
          }
          index = line.indexOf("wavelength\t") + 11;
          if (index > 10) {
            String c = line.substring(index, line.indexOf(".", index));
            try {
              cc = Integer.parseInt(c) - 1;
            }
            catch (NumberFormatException e) {
              LOGGER.warn("Could not parse channel position '{}'", c);
            }
          }
        }
      }

      if (line.length() > 0 && line.indexOf('.') == -1) previousLine = line;

      doStatistics = line.endsWith("- reading image data...");
    }
  }

  private void readWavelength(int channel, MetadataStore store)
    throws FormatException, IOException
  {
    float min = in.readFloat();
    float max = in.readFloat();
    addGlobalMeta("Wavelength " + (channel + 1) + " min. intensity", min);
    addGlobalMeta("Wavelength " + (channel + 1) + " max. intensity", max);
    if (store instanceof IMinMaxStore) {
      ((IMinMaxStore) store).setChannelGlobalMinMax(channel, min, max, 0);
    }
  }

  /**
   * Populate an Objective based upon the lens ID.
   * This is based upon information received from Applied Precision.
   */
  protected void populateObjective(MetadataStore store, int lensID)
    throws FormatException
  {
    Double lensNA = null;
    Double workingDistance = null;
    Immersion immersion = MetadataTools.getImmersion("Other");
    Correction correction = MetadataTools.getCorrection("Other");
    String manufacturer = null;
    String model = null;
    double magnification = 0;

    if (lensID >= 10000 && lensID <= 32000) {
      if (lensID < 12000) {
        manufacturer = "Olympus";
      }
      else if (lensID < 14000) {
        manufacturer = "Nikon";
      }
      else if (lensID < 16000) {
        manufacturer = "Zeiss";
      }
      else if (lensID < 18000) {
        manufacturer = "Leica";
      }
      else if (lensID < 20000) {
        manufacturer = "APLLC";
      }

      magnification = ((lensID % 1000) - (lensID % 100)) / 10.0;
      if (magnification == 0) {
        magnification = 100;
      }
    }

    switch (lensID) {
      case 2001: // test
        lensNA = 1.15;
        immersion = MetadataTools.getImmersion("Water");
        break;
      case 10100: // Olympus 10X/0.30, S Plan Achromat, phase, IMT2
        lensNA = 0.30;
        magnification = 10.0;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-LP134";
        correction = MetadataTools.getCorrection("Achromat");
        break;
      case 10101: // Olympus 10X/0.40, DApo/340, IMT2
        lensNA = 0.40;
        magnification = 10.0;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-LB331";
        correction = MetadataTools.getCorrection("Apo");
        break;
      case 10102: // Olympus 10X/0.30, SPlan 10, Positive Low, phase, IMT2
        lensNA = 0.30;
        magnification = 10.0;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-LP134";
        break;
      case 10103: // Olympus 4X/0.13, SPlan 4, Positive Low, phase, IMT2
        lensNA = 0.13;
        magnification = 4.0;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-LP124";
        break;
      case 10104: // Olympus 10X/0.40, D Plan Apo UV, IMT2
        lensNA = 0.40;
        magnification = 10.0;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-LB331";
        correction = MetadataTools.getCorrection("UV");
        break;
      case 10105: // Olympus 10X/0.40, D Plan Apo UV, phase, fluor free, IMT2
        lensNA = 0.40;
        magnification = 10.0;
        workingDistance = 3.10;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-LP331";
        correction = MetadataTools.getCorrection("UV");
        break;
      case 10106: // Olympus 4X/0.16, U Plan Apo, Infinity/-, IX70
        lensNA = 0.16;
        magnification = 4.0;
        workingDistance = 13.0;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-UB822";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 10107: // Olympus 10X/0.40, U Plan Apo, IX70
        lensNA = 0.40;
        magnification = 10.0;
        workingDistance = 3.10;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-UB823";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 10108: // Olympus 10X/0.25,C Plan Achromat, phase, IX70
        lensNA = 0.25;
        magnification = 10.0;
        workingDistance = 9.8;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-UC243";
        correction = MetadataTools.getCorrection("Achromat");
        break;
      case 10109: // Olympus 10X/0.30, UPlanFl, IX70
        lensNA = 0.30;
        magnification = 10.0;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-UB532";
        correction = MetadataTools.getCorrection("PlanFluor");
        break;
      case 10110: // Olympus 4X/0.13
        lensNA = 0.13;
        magnification = 4.0;
        immersion = MetadataTools.getImmersion("Air");
        break;
      case 10111: // Olympus 2X/0.08, SPlan FL 2
        lensNA = 0.08;
        magnification = 2.0;
        immersion = MetadataTools.getImmersion("Air");
        break;
      case 10112: // Olympus 4X/0.13, UPlanFL, Infinity/170, IX70
        lensNA = 0.13;
        magnification = 4.0;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-UB522";
        correction = MetadataTools.getCorrection("PlanFluor");
        break;
      case 10113: // Olympus 1.25X/0.04, PlanApo, IX70
        lensNA = 0.04;
        magnification = 1.25;
        workingDistance = 5.1;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-UB920";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 10114: // Olympus 2X/0.08, PlanApo, IX70
        lensNA = 0.08;
        magnification = 2.0;
        workingDistance = 6.0;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-UB921";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 10115: // Olympus U-Plan S-Apo 10X/0.4
        lensNA = 0.40;
        magnification = 10.0;
        workingDistance = 3.1;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-U2B823";
        break;
      case 10116: // Olympus 4X/0.16, U-Plan S-Apo, UIS2, 1-U2B822
        lensNA = 0.16;
        magnification = 4.0;
        workingDistance = 13.;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-U2B822";
        break;
      case 10117: // Olympus 10X/0.40, U-Plan S-Apo, UIS2, 1-U2B824
        lensNA = 0.40;
        magnification = 10.0;
        workingDistance = 3.1;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-U2B824";
        break;
      case 10200: // Olympus 20X/0.40, LWD, CD Plan Achromat, phase, IMT2
        lensNA = 0.40;
        magnification = 20.0;
        workingDistance = 3.0;
        immersion = MetadataTools.getImmersion("Air");
        correction = MetadataTools.getCorrection("Achromat");
        break;
      case 10201: // Olympus 20X/0.65, DApo/340, IMT2, 1-LB343
        lensNA = 0.65;
        magnification = 20.0;
        workingDistance = 1.03;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-LB343";
        correction = MetadataTools.getCorrection("Apo");
        break;
      case 10202: // Olympus 20X/0.40, ULWD, CDPlan 20PL, phase, IMT2, 1-LP146
        lensNA = 0.40;
        magnification = 20.0;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-LP146";
        break;
      case 10203: // Olympus 20X/0.80, D Plan Apo/340, IMT2, 1-LB342
        lensNA = 0.80;
        magnification = 20.0;
        immersion = MetadataTools.getImmersion("Oil");
        model = "1-LB342";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 10204: // Olympus 20X/0.70, D Plan Apo UV, IMT2, 1-LB341
        lensNA = 0.70;
        magnification = 20.0;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-LB341";
        correction = MetadataTools.getCorrection("UV");
        break;
      case 10205: // Olympus 20X/0.75, U Apo 340, IX70, 1-UB765
        lensNA = 0.75;
        magnification = 20.0;
        workingDistance = 0.55;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-UB765";
        correction = MetadataTools.getCorrection("Apo");
        break;
      case 10206: // Olympus 20X/0.50, Phase, UPLFL, IX70, 1-UC525
        lensNA = 0.50;
        magnification = 20.0;
        workingDistance = 0.55;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-UC525";
        break;
      case 10207: // Olympus 20X/0.40, LWD, C Achromat, phase, IX70
        lensNA = 0.40;
        magnification = 20.0;
        workingDistance = 3.0;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-UC145";
        correction = MetadataTools.getCorrection("Achromat");
        break;
      case 10208: // Olympus 20X/0.50, Phase, UPLFL, IX70, 1-UB525
        lensNA = 0.50;
        magnification = 20.0;
        workingDistance = 0.55;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-UB525";
        break;
      case 10209: // Olympus 20X/0.40, LCPLFL, Phase, IX70
        lensNA = 0.40;
        magnification = 20.0;
        workingDistance = 6.9;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-UC345";
        break;
      case 10210: // Olympus 20X/0.40, LCPLFL, IX70
        lensNA = 0.40;
        magnification = 20.0;
        workingDistance = 6.9;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-UB345";
        break;
      case 10211: // Olympus U-Plan S-Apo 20X/0.75
        lensNA = 0.75;
        magnification = 20.0;
        workingDistance = 0.65;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-U2B825";
        break;
      case 10212: // Olympus U-Plan Fluorite PH1 20X/0.45 w/Corr. Collar
        lensNA = 0.45;
        magnification = 20.0;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-U2C375";
        break;
      case 10213: // Olympus U-Plan Fluorite PH1 20X/0.50
        lensNA = 0.50;
        magnification = 20.0;
        workingDistance = 2.0;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-U2C525";
        break;
      case 10214: // Olympus U-Apo 340nm 20X/0.75
        lensNA = 0.75;
        magnification = 20.0;
        workingDistance = 0.55;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-UB765R";
        break;
      case 10215: // Olympus 20X/0.45, LWD U-Plan FL, w/Corr. Collar, UIS2, 1-U2B375
        lensNA = 0.45;
        magnification = 20.0;
        workingDistance = 6.6;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-U2B375";
        correction = MetadataTools.getCorrection("PlanFluor");
        break;
      case 10400: // Olympus 40X/0.55, LWD, CDPlan Achromat, phase, IMT2
        lensNA = 0.55;
        magnification = 40.0;
        workingDistance = 2.04;
        immersion = MetadataTools.getImmersion("Air");
        correction = MetadataTools.getCorrection("Achromat");
        break;
      case 10401: // Olympus 40X/0.85, IMT2
        lensNA = 0.85;
        magnification = 40.0;
        workingDistance = 0.25;
        immersion = MetadataTools.getImmersion("Air");
        break;
      case 10402: // Olympus 40X/1.30, DApo/340, IMT2, 1-LB356
        lensNA = 1.30;
        magnification = 40.0;
        workingDistance = 0.12;
        immersion = MetadataTools.getImmersion("Oil");
        model = "1-LB356";
        correction = MetadataTools.getCorrection("Apo");
        break;
      case 10403: // Olympus 40X/1.35, UApo/340, IX70, 1-UB768
        lensNA = 1.35;
        magnification = 40.0;
        workingDistance = 0.10;
        immersion = MetadataTools.getImmersion("Oil");
        model = "1-UB768";
        correction = MetadataTools.getCorrection("Apo");
        break;
      case 10404: // Olympus 40X/0.85, U Plan Apo, IX70, 1-UB827
        lensNA = 0.85;
        magnification = 40.0;
        workingDistance = 0.20;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-UB827";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 10405: // Olympus 40X/0.95, Plan Apo, IX70
        lensNA = 0.95;
        magnification = 40.0;
        workingDistance = 0.14;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-UB927";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 10406: // Olympus 40X/1.0, U Plan Apo, Iris
        lensNA = 1.0;
        magnification = 40.;
        immersion = MetadataTools.getImmersion("Oil");
        model = "1-UB828";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 10407: // Olympus 40X/0.75, UPlanFl, IX70, 1-UB527
        lensNA = 0.75;
        magnification = 40;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-UB527";
        correction = MetadataTools.getCorrection("PlanFluor");
        break;
      case 10408: // Olympus 40X/0.60, LCPLFL, IX70
        lensNA = 0.60;
        magnification = 40.0;
        workingDistance = 2.15;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-UB347";
        break;
      case 10409: // Olympus 40X/0.60, LCPLFL, Phase, IX70
        lensNA = 0.60;
        magnification = 40.0;
        workingDistance = 2.15;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-UC347";
        break;
      case 10410: // Olympus 40X/1.15, UApo340, IX70
        lensNA = 1.15;
        magnification = 40.0;
        immersion = MetadataTools.getImmersion("Water");
        model = "1-UB769";
        break;
      case 10411: // Olympus 40X/0.75, UPlanFl, IX70, Ph2
        lensNA = 0.75;
        magnification = 40;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-UC527";
        correction = MetadataTools.getCorrection("PlanFluor");
        break;
      case 10412: // Olympus 40X/1.35, UApo/340, PSF, IX70
        lensNA = 1.35;
        magnification = 40.0;
        workingDistance = 0.10;
        immersion = MetadataTools.getImmersion("Oil");
        correction = MetadataTools.getCorrection("Apo");
        break;
      case 10413: // Olympus U-Apo 340nm 40X/1.15 Water w/Corr. Collar
        lensNA = 1.15;
        magnification = 40.0;
        workingDistance = 0.26;
        immersion = MetadataTools.getImmersion("Water");
        model = "1-UB769R";
        break;
      case 10414: // Olympus 40X/0.60, LWD U-Plan FL, w/Corr. Collar, UIS2, 1-U2B377
        lensNA = 0.60;
        magnification = 40.0;
        workingDistance = 2.7;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-U2B377";
        correction = MetadataTools.getCorrection("PlanFluor");
        break;
      case 10415: // Olympus 40X/1.35, U-Apo O340, UIS2, 1-U2B768
        lensNA = 1.35;
        magnification = 40.0;
        workingDistance = 0.1;
        immersion = MetadataTools.getImmersion("Oil");
        model = "1-U2B768";
        break;
      case 10416: // Olympus 40X/1.30, UPLFLN 40XO, U PLAN Fluorite 40X Oil, 1-U2B530
        lensNA = 1.30;
        magnification = 40.0;
        workingDistance = 0.2;
        immersion = MetadataTools.getImmersion("Oil");
        model = "1-U2B530";
        break;
      case 10000: // Olympus 100X/1.30, DApo/340, IMT2
        lensNA = 1.30;
        magnification = 100.0;
        immersion = MetadataTools.getImmersion("Oil");
        model = "1-LB393";
        correction = MetadataTools.getCorrection("Apo");
        break;
      case 10001: // Olympus 100X/1.30, D Plan Apo UV, IMT2
        lensNA = 1.30;
        magnification = 100.0;
        immersion = MetadataTools.getImmersion("Oil");
        model = "1-LB392";
        correction = MetadataTools.getCorrection("UV");
        break;
      case 10002: // Olympus 100X/1.40, Plan Apo, IX70
        lensNA = 1.40;
        magnification = 100.0;
        workingDistance = 0.10;
        immersion = MetadataTools.getImmersion("Oil");
        model = "1-UB935";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 10003: // Olympus 100X/1.35, U Plan Apo, IX70
        lensNA = 1.35;
        magnification = 100.0;
        workingDistance = 0.10;
        immersion = MetadataTools.getImmersion("Oil");
        model = "1-UB836";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 10004: // Olympus 100X/1.30, UPLFL
        lensNA = 1.30;
        magnification = 100.0;
        immersion = MetadataTools.getImmersion("Oil");
        model = "1-UB535";
        break;
      case 10005: // Olympus 100X/1.35, U Plan Apo, PSF, IX70
        lensNA = 1.35;
        magnification = 100.0;
        workingDistance = 0.10;
        immersion = MetadataTools.getImmersion("Oil");
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 10006: // Olympus 100X/1.40, Plan Apo, PSF, IX70
        lensNA = 1.40;
        magnification = 100.0;
        workingDistance = 0.10;
        immersion = MetadataTools.getImmersion("Oil");
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 10007: // Olympus 100X/1.40, UPLS Apo, UIS2, 1-U2B836
        lensNA = 1.40;
        magnification = 100.0;
        workingDistance = 0.13;
        immersion = MetadataTools.getImmersion("Oil");
        model = "1-U2B836";
        break;
      case 10008: // Olympus U-Plan Fluorite 100X/1.30
        lensNA = 1.30;
        magnification = 100.0;
        workingDistance = 0.20;
        immersion = MetadataTools.getImmersion("Oil");
        model = "1-U2B5352";
        break;
      case 10009: // Olympus U-Plan Fluorite 100X/0.55-1.30 w/Corr. Collar
        lensNA = 1.30;
        magnification = 100.0;
        workingDistance = 0.20;
        immersion = MetadataTools.getImmersion("Oil");
        model = "1-U2B5362";
        break;
      case 10010: // Olympus PlanApo TIRFM 100X/1.45
        lensNA = 1.45;
        magnification = 100.0;
        workingDistance = 0.10;
        immersion = MetadataTools.getImmersion("Oil");
        model = "1-UB617R";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 10011: // Olympus U-Apo N TIRF 100X/1.49 w/Corr. Collar
        lensNA = 1.49;
        magnification = 100.0;
        workingDistance = 0.10;
        immersion = MetadataTools.getImmersion("Oil");
        model = "1-U2B725";
        break;
      case 10012: // Olympus 100X/0.95, PLFL, Corr Collar 0.14-0.20, Iris
        lensNA = 0.95;
        magnification = 100.0;
        workingDistance = 0.20;
        immersion = MetadataTools.getImmersion("Air");
        model = "XXXXXXXX";
        break;
      case 11500: // Olympus U-Apo TIRFM 150X/1.45 w/Corr. Collar
        lensNA = 1.45;
        magnification = 150.0;
        workingDistance = 0.10;
        immersion = MetadataTools.getImmersion("Oil");
        model = "1-U2B618";
        break;
      case 10600: // Olympus 60X/1.40, DApo/340, IMT2
        lensNA = 1.40;
        magnification = 60.0;
        workingDistance = 0.30;
        immersion = MetadataTools.getImmersion("Oil");
        correction = MetadataTools.getCorrection("Apo");
        break;
      case 10601: // Olympus 60X/1.40, S Plan Apo, UV/340, IMT2
        lensNA = 1.40;
        magnification = 60.0;
        immersion = MetadataTools.getImmersion("Oil");
        model = "1-LB751";
        correction = MetadataTools.getCorrection("UV");
        break;
      case 10602: // Olympus 60X/1.40, Plan Apo, IX70
        lensNA = 1.40;
        magnification = 60.0;
        workingDistance = 0.1;
        immersion = MetadataTools.getImmersion("Oil");
        model = "1-UB932";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 10603: // Olympus 60X/1.20, U Plan Apo, IX70
        lensNA = 1.20;
        magnification = 60.0;
        workingDistance = 0.25;
        immersion = MetadataTools.getImmersion("Water");
        model = "1-UB891";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 10604: // Olympus 60X/1.20, IMT2
        lensNA = 1.20;
        magnification = 60.0;
        workingDistance = 0.25;
        immersion = MetadataTools.getImmersion("Water");
        break;
      case 10605: // Olympus 60X/0.70, LCPLPL, IX70
        lensNA = 0.70;
        magnification = 60.0;
        workingDistance = 1.10;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-UB351";
        break;
      case 10606: // Olympus 60X/0.70, LCPLPL, Phase, IX70
        lensNA = 0.70;
        magnification = 60.0;
        workingDistance = 1.10;
        immersion = MetadataTools.getImmersion("Air");
        model = "1-UC351";
        break;
      case 10607: // Olympus 60X/1.40, Plan Apo, Ph3
        lensNA = 1.40;
        magnification = 60.0;
        immersion = MetadataTools.getImmersion("Oil");
        model = "1-UC932";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 10608: // Olympus 60X/1.25, UPLFL
        lensNA = 1.25;
        magnification = 60.0;
        immersion = MetadataTools.getImmersion("Oil");
        model = "1-UB532";
        break;
      case 10609: // Olympus 60X/1.40, Plan Apo, BFP-1, IX70
        lensNA = 1.40;
        magnification = 60.0;
        workingDistance = 0.15;
        immersion = MetadataTools.getImmersion("Oil");
        model = "1-UB933";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 10610: // Olympus 60X/1.40, Plan Apo, PSF, IX70
        lensNA = 1.40;
        magnification = 60.0;
        workingDistance = 0.15;
        immersion = MetadataTools.getImmersion("Oil");
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 10611: // Olympus 60X/1.20, U Plan Apo, Water, PSF, IX70
        lensNA = 1.20;
        magnification = 60.0;
        workingDistance = 0.25;
        immersion = MetadataTools.getImmersion("Water");
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 10612: // Olympus 60X/1.42, Plan Apo N, UIS2, 1-U2B933
        lensNA = 1.42;
        magnification = 60.0;
        workingDistance = 0.15;
        immersion = MetadataTools.getImmersion("Oil");
        model = "1-U2B933";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 10613: // Olympus PlanApo TIRFM 60X/1.45 w/Corr. Collar
        lensNA = 1.45;
        magnification = 60.0;
        workingDistance = 0.10;
        immersion = MetadataTools.getImmersion("Oil");
        model = "1-U2B616";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 10614: // Olympus Apo TIRFM 60X/1.49 w/Corr. Collar
        lensNA = 1.49;
        magnification = 60.0;
        workingDistance = 0.10;
        immersion = MetadataTools.getImmersion("Oil");
        model = "1-U2B617";
        break;
      case 10615: // Olympus 60X/1.30, U-Apo N, Sil FV UIS2, PSF, w/Corr. Collar
        lensNA = 1.30;
        magnification = 60.0;
        workingDistance = 0.30;
        immersion = MetadataTools.getImmersion("Other");
        break;
      case 10616: // Olympus 60X/1.20, U-Plan S-Apo PSF, UIS2, 1-U2B893S
        lensNA = 1.20;
        magnification = 60.0;
        workingDistance = 0.28;
        immersion = MetadataTools.getImmersion("Water");
        model = "1-U2B893S";
        break;
      case 10617: // Olympus 60X/1.49, APON 60XOTIRF, UIS2, 1-U2B720
        lensNA = 1.49;
        magnification = 60.0;
        workingDistance = 0.10;
        immersion = MetadataTools.getImmersion("Oil");
        model = "1-U2B720";
        break;
      case 12201: // Nikon 20X/0.75, Plan Apo, CFI/60
        lensNA = 0.75;
        magnification = 20.;
        workingDistance = 1.0;
        immersion = MetadataTools.getImmersion("Air");
        model = "93104";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 12202: // Nikon 20X/0.75, Plan Fluor, DIC M, CFI/60
        lensNA = 0.75;
        magnification = 20.;
        immersion = MetadataTools.getImmersion("Multi");
        model = "93146";
        correction = MetadataTools.getCorrection("PlanFluor");
        break;
      case 12203: // Nikon 20X/0.45, Plan Fluor ELWD, CFI/60
        lensNA = 0.45;
        magnification = 20.;
        workingDistance = 7.4;
        immersion = MetadataTools.getImmersion("Air");
        model = "93150";
        correction = MetadataTools.getCorrection("PlanFluor");
        break;
      case 12204: // Nikon 20X/0.45, LU Plan EPI P, CFI/60
        lensNA = 0.45;
        magnification = 20.;
        workingDistance = 4.50;
        immersion = MetadataTools.getImmersion("Air");
        model = "MUE01200/92777";
        break;
      case 12205: // Nikon 20X/0.50, Plan Fluor, CFI/60
        lensNA = 0.50;
        magnification = 20.;
        workingDistance = 2.10;
        immersion = MetadataTools.getImmersion("Air");
        model = "MRH00200/93135";
        correction = MetadataTools.getCorrection("PlanFluor");
        break;
      case 12206: // Nikon 20X/0.45, Plan Fluor, ELWD, Corr Collar 0-2.0, CFI/60
        lensNA = 0.45;
        magnification = 20.;
        workingDistance = 7.00;
        immersion = MetadataTools.getImmersion("Air");
        model = "MRH08230";
        correction = MetadataTools.getCorrection("PlanFluor");
        break;
      case 12207: // Nikon 20X/0.75, Plan Apo, CFI/60
        lensNA = 0.75;
        magnification = 20.;
        workingDistance = 1.00;
        immersion = MetadataTools.getImmersion("Air");
        model = "MRD00201, MRD00205";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 12208: // Nikon 20X/0.75, Super Fluor, CFI/60
        lensNA = 0.75;
        magnification = 20.;
        workingDistance = 1.00;
        immersion = MetadataTools.getImmersion("Air");
        correction = MetadataTools.getCorrection("SuperFluor");
        break;
      case 12401: // Nikon 40X/1.30, PlanFluar, 160mm tube length
        lensNA = 1.30;
        magnification = 40.0;
        workingDistance = 0.16;
        immersion = MetadataTools.getImmersion("Oil");
        model = "85028";
        break;
      case 12402: // Nikon 40X/1.30, CF Fluor, 160mm tube length
        lensNA = 1.30;
        magnification = 40.0;
        workingDistance = 0.22;
        immersion = MetadataTools.getImmersion("Oil");
        model = "85004";
        break;
      case 12403: // Nikon 40X/0.75, CF Fluor, 160mm tube length
        lensNA = 0.75;
        magnification = 40.0;
        immersion = MetadataTools.getImmersion("Air");
        model = "140508";
        break;
      case 12404: // Nikon 40X/1.30, S Fluor, CFI/60
        lensNA = 1.30;
        magnification = 40.;
        workingDistance = 0.22;
        immersion = MetadataTools.getImmersion("Oil");
        break;
      case 12405: // Nikon 40X/0.95, Plan Apo, DIC M, CFI/60
        lensNA = 0.95;
        magnification = 40.;
        immersion = MetadataTools.getImmersion("Air");
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 12406: // Nikon 40X/1.00, DIC H, CFI/60
        lensNA = 1.00;
        magnification = 40.;
        workingDistance = 0.16;
        immersion = MetadataTools.getImmersion("Oil");
        break;
      case 12407: // Nikon 40X/0.60, Plan Fluor, ELWD, Corr Collar, CFI/60
        lensNA = 0.60;
        magnification = 40.0;
        workingDistance = 3.2;
        immersion = MetadataTools.getImmersion("Air");
        correction = MetadataTools.getCorrection("PlanFluor");
        break;
      case 12408: // Nikon 40X/0.60, Plan Fluor, ELWD, Corr Collar 0-2.0, CFI/60
        lensNA = 0.60;
        magnification = 40.0;
        workingDistance = 2.7;
        immersion = MetadataTools.getImmersion("Air");
        model = "MRH08430";
        correction = MetadataTools.getCorrection("PlanFluor");
        break;
      case 12409: // Nikon 40X/0.95, Plan Apo, Corr Collar 0.11-0.23, CFI/60
        lensNA = 0.95;
        magnification = 40.0;
        workingDistance = 0.14;
        immersion = MetadataTools.getImmersion("Air");
        model = "MRD00400";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 12410: // Nikon 40X/0.65, Plan Apo, NCG, CFI/60
        lensNA = 0.65;
        magnification = 40.0;
        workingDistance = 0.48;
        immersion = MetadataTools.getImmersion("Air");
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 12411: // Nikon 40X/0.95, Plan Apo, Corr Collar 0.11-0.23, CFI/60 Lambda
        lensNA = 0.95;
        magnification = 40.0;
        workingDistance = 0.14;
        immersion = MetadataTools.getImmersion("Air");
        model = "MRD00405";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 12412: // Nikon 40X/0.90, S Fluor, Corr Collar 0.11-0.23, CFI/60 Lambda
        lensNA = 0.90;
        magnification = 40.0;
        workingDistance = 0.30;
        immersion = MetadataTools.getImmersion("Air");
        model = "MRF00400";
        break;
      case 12413: // Nikon 40X/0.75, Plan Fluor, CFI/60 Lambda
        lensNA = 0.75;
        magnification = 40.0;
        workingDistance = 0.66;
        immersion = MetadataTools.getImmersion("Air");
        correction = MetadataTools.getCorrection("PlanFluor");
        break;
      case 12600: // Nikon 60X/1.40, CF N Plan Apochromat, 160mm tube length
        lensNA = 1.40;
        magnification = 60.0;
        workingDistance = 0.17;
        immersion = MetadataTools.getImmersion("Oil");
        model = "85020";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 12601: // Nikon 60X/1.40, Plan Apo, DIC H
        lensNA = 1.40;
        magnification = 60.;
        workingDistance = 0.21;
        immersion = MetadataTools.getImmersion("Oil");
        model = "93108";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 12602: // Nikon 60X/1.20, Plan Apo, WI (Water), DIC H, CFI/60
        lensNA = 1.20;
        magnification = 60.;
        workingDistance = 0.22;
        immersion = MetadataTools.getImmersion("Water");
        model = "93109";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 12603: // Nikon 60X/0.95, Plan Apo, Corr Collar 0.11-0.23, CFI/60
        lensNA = 0.95;
        magnification = 60.;
        workingDistance = 0.15;
        immersion = MetadataTools.getImmersion("Air");
        model = "MRD00600";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 12604: // Nikon 60X/0.70, Plan Fluor, ELWD, Corr Collar 0.5-1.5, CFI/60
        lensNA = 0.70;
        magnification = 60.;
        workingDistance = 1.5;
        immersion = MetadataTools.getImmersion("Air");
        model = "MRH08630";
        correction = MetadataTools.getCorrection("PlanFluor");
        break;
      case 12605: // Nikon 60X/0.95, Plan Apo, Corr Collar 0.11-0.23, CFI/60 Lambda
        lensNA = 0.95;
        magnification = 60.;
        workingDistance = 0.15;
        immersion = MetadataTools.getImmersion("Air");
        model = "MRD00605";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 12606: // Nikon 60X/0.85, Plan Fluor, Corr Collar 0.11-0.23, CFI/60 Lambda
        lensNA = 0.85;
        magnification = 60.;
        workingDistance = 0.30;
        immersion = MetadataTools.getImmersion("Air");
        model = "MRH00602";
        correction = MetadataTools.getCorrection("PlanFluor");
        break;
      case 12000: // Nikon 100X/1.40, CF N Plan Apochromat, 160mm tube length
        lensNA = 1.40;
        magnification = 100.0;
        workingDistance = 0.1;
        immersion = MetadataTools.getImmersion("Oil");
        model = "85025";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 12001: // Nikon 100X/1.30, CF Fluor, 160mm tube length
        lensNA = 1.30;
        magnification = 100.0;
        workingDistance = 0.14;
        immersion = MetadataTools.getImmersion("Oil");
        model = "85005";
        break;
      case 12002: // Nikon 100X/1.30, CF UV F, 160mm tube length
        lensNA = 1.30;
        magnification = 100.0;
        workingDistance = 0.13;
        immersion = MetadataTools.getImmersion("Glycerol");
        model = "78821";
        correction = MetadataTools.getCorrection("UV");
        break;
      case 12003: // Nikon 100X/1.40, Plan Apo, DIC H, CFI/60
        lensNA = 1.40;
        magnification = 100.;
        workingDistance = 0.13;
        immersion = MetadataTools.getImmersion("Oil");
        model = "93110";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 12004: // Nikon 100X/0.5-1.30, S Fluor, Oil Iris, CFI 60
        lensNA = 1.30;
        magnification = 100.;
        workingDistance = 0.20;
        immersion = MetadataTools.getImmersion("Oil");
        model = "93129";
        break;
      case 12005: // Nikon 100X/0.90, Plan Fluor, Corr Collar 0.14-0.20, CFI 60
        lensNA = 0.90;
        magnification = 100.;
        workingDistance = 0.30;
        immersion = MetadataTools.getImmersion("Air");
        model = "MRH00900";
        correction = MetadataTools.getCorrection("PlanFluor");
        break;
      case 12101: // Nikon 2X/0.10, Plan Apo, 160mm tube length
        lensNA = 0.10;
        magnification = 2.;
        immersion = MetadataTools.getImmersion("Air");
        model = "202294";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 12102: // Nikon 4X/0.20, Plan Apo, 160mm tube length
        lensNA = 0.20;
        magnification = 4.;
        immersion = MetadataTools.getImmersion("Air");
        model = "108388";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 12103: // Nikon 4X/0.20, Plan Apo, CFI/60
        lensNA = 0.2;
        magnification = 4.;
        workingDistance = 15.7;
        immersion = MetadataTools.getImmersion("Air");
        model = "93102";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 12104: // Nikon 10X/0.45, Plan Apo, CFI/60
        lensNA = 0.45;
        magnification = 10.;
        workingDistance = 4.0;
        immersion = MetadataTools.getImmersion("Air");
        model = "93103";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 12105: // Nikon 10X/0.30, Plan Fluor, CFI/60
        lensNA = 0.30;
        magnification = 10.;
        workingDistance = 16.0;
        immersion = MetadataTools.getImmersion("Air");
        model = "93134";
        correction = MetadataTools.getCorrection("PlanFluor");
        break;
      case 12106: // Nikon 10X/0.50, Super Fluor, CFI/60
        lensNA = 0.50;
        magnification = 10.;
        workingDistance = 1.20;
        immersion = MetadataTools.getImmersion("Air");
        model = "MRF00100/93126";
        correction = MetadataTools.getCorrection("SuperFluor");
        break;
      case 12107: // Nikon 10X/0.25, Plan Achromat, CFI/60
        lensNA = 0.25;
        magnification = 10.;
        workingDistance = 10.5;
        immersion = MetadataTools.getImmersion("Air");
        model = "93183";
        correction = MetadataTools.getCorrection("Achromat");
        break;
      case 12108: // Nikon 10X/0.25, Achromat Flatfield, CFI/60
        lensNA = 0.25;
        magnification = 10.;
        workingDistance = 6.10;
        immersion = MetadataTools.getImmersion("Air");
        model = "93161";
        correction = MetadataTools.getCorrection("Achromat");
        break;
      case 12109: // Nikon 2X/0.10, Plan Apo, CFI/60
        lensNA = 0.1;
        magnification = 2.;
        workingDistance = 8.50;
        immersion = MetadataTools.getImmersion("Air");
        model = "MRD00020, MRD00025";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 12110: // Nikon 4X/0.20, Plan Apo, CFI/60
        lensNA = 0.2;
        magnification = 4.;
        workingDistance = 20.0;
        immersion = MetadataTools.getImmersion("Air");
        model = "MRD00041, MRD00045";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 12111: // Nikon 10X/0.45, Plan Apo, CFI/60
        lensNA = 0.45;
        magnification = 10.;
        workingDistance = 4.00;
        immersion = MetadataTools.getImmersion("Air");
        model = "MRD00101, MRD00105";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 14001: // Zeiss 100X/1.40, Plan - APOCHROMAT
        lensNA = 1.40;
        magnification = 100.0;
        workingDistance = 0.1;
        immersion = MetadataTools.getImmersion("Oil");
        model = "44 07 08 (02)";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 14002: // Zeiss 100X/1.30, Oil Iris (0.8 - 1.30)
        lensNA = 1.30;
        magnification = 100.0;
        workingDistance = 0.1;
        immersion = MetadataTools.getImmersion("Oil");
        model = "44 07 86";
        break;
      case 14003: // Zeiss 100X/1.40, Plan - APOCHROMAT
        lensNA = 1.40;
        magnification = 100;
        immersion = MetadataTools.getImmersion("Oil");
        model = "44 07 80 (02)";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 14004: // Zeiss 100X/1.30, Planapo, 160mm tube length
        lensNA = 1.30;
        magnification = 100.0;
        immersion = MetadataTools.getImmersion("Oil");
        model = "46 19 46 - 9903";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 14005: // Zeiss 100X/1.40, Oil Iris (0.7 - 1.40)
        lensNA = 1.40;
        magnification = 100.0;
        immersion = MetadataTools.getImmersion("Oil");
        model = "44 07 86 (02)";
        break;
      case 14006: // Zeiss 100X/1.30
        lensNA = 1.30;
        magnification = 100.0;
        immersion = MetadataTools.getImmersion("Oil");
        break;
      case 14601: // Zeiss 63X/1.40, Plan - APOCHROMAT
        lensNA = 1.40;
        magnification = 63.0;
        workingDistance = 0.09;
        immersion = MetadataTools.getImmersion("Oil");
        model = "44 07 60 (03)";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 14602: // Zeiss 63X/1.40, Plan - APOCHROMAT, DIC
        lensNA = 1.40;
        magnification = 63.0;
        workingDistance = 0.09;
        immersion = MetadataTools.getImmersion("Oil");
        model = "44 07 62 (02)";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 14603: // Zeiss 63X/0.90, Achroplan, W, Ph3
        lensNA = 0.90;
        magnification = 63.0;
        immersion = MetadataTools.getImmersion("Water");
        model = "44 00 69";
        break;
      case 14604: // Zeiss 63X/1.20, C - APOCHROMAT, Water
        lensNA = 1.20;
        magnification = 63.0;
        workingDistance = 0.09;
        immersion = MetadataTools.getImmersion("Water");
        model = "44 06 68";
        correction = MetadataTools.getCorrection("Apo");
        break;
      case 14401: // Zeiss 40X/1.30, FLUAR
        lensNA = 1.30;
        magnification = 40.0;
        workingDistance = 0.14;
        immersion = MetadataTools.getImmersion("Oil");
        model = "44 02 55 (01)";
        correction = MetadataTools.getCorrection("Fluar");
        break;
      case 14402: // Zeiss 40X/1.00, Plan - APOCHROMAT, Phase3
        lensNA = 1.00;
        magnification = 40.0;
        immersion = MetadataTools.getImmersion("Oil");
        model = "44 07 51";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 14403: // Zeiss 40X/1.20, Water Immersion, Corr Collar, C - APOCHROMAT
        lensNA = 1.20;
        magnification = 40.;
        immersion = MetadataTools.getImmersion("Water");
        model = "44 00 52";
        correction = MetadataTools.getCorrection("Apo");
        break;
      case 14404: // Zeiss 40X/0.75, Plan - NEOFLUAR, Phase2
        lensNA = 0.75;
        magnification = 40.0;
        immersion = MetadataTools.getImmersion("Air");
        model = "44 03 51";
        correction = MetadataTools.getCorrection("PlanNeofluar");
        break;
      case 14405: // Zeiss 40X/1.30, Plan - NEOFLUAR
        lensNA = 1.30;
        magnification = 40.0;
        workingDistance = 0.15;
        immersion = MetadataTools.getImmersion("Oil");
        model = "44 04 50";
        correction = MetadataTools.getCorrection("PlanNeofluar");
        break;
      case 14406: // Zeiss 40X/0.60, LD ACHROPLAN, Phase2
        lensNA = 0.60;
        magnification = 40.0;
        immersion = MetadataTools.getImmersion("Air");
        model = "44 08 65";
        break;
      case 14407: // Zeiss 40X/1.30, Plan - NEOFLUAR, DIC, Strainfree
        lensNA = 1.30;
        magnification = 40.0;
        immersion = MetadataTools.getImmersion("Oil");
        correction = MetadataTools.getCorrection("PlanNeofluar");
        break;
      case 14301: // Zeiss 25X/0.80, Plan - NEOFLUAR
        lensNA = 0.80;
        magnification = 25.0;
        workingDistance = 0.80;
        immersion = MetadataTools.getImmersion("Multi");
        model = "44 05 44";
        correction = MetadataTools.getCorrection("PlanNeofluar");
        break;
      case 14302: // Zeiss 25X/0.80, Plan - NEOFLUAR, DIC
        lensNA = 0.80;
        magnification = 25.0;
        workingDistance = 0.80;
        immersion = MetadataTools.getImmersion("Multi");
        model = "44 05 42";
        correction = MetadataTools.getCorrection("PlanNeofluar");
        break;
      case 14303: // Zeiss 25X/0.80, Plan - NEOFLUAR, Phase2
        lensNA = 0.80;
        magnification = 25.0;
        immersion = MetadataTools.getImmersion("Air");
        model = "44 05 45";
        correction = MetadataTools.getCorrection("PlanNeofluar");
        break;
      case 14201: // Zeiss 20X/0.50, Plan - NEOFLUAR, Phase2
        lensNA = 0.50;
        magnification = 20.0;
        immersion = MetadataTools.getImmersion("Air");
        model = "44 03 41 (01)";
        correction = MetadataTools.getCorrection("PlanNeofluar");
        break;
      case 14202: // Zeiss 20X/0.60, Plan - APOCHROMAT
        lensNA = 0.60;
        magnification = 20.0;
        immersion = MetadataTools.getImmersion("Air");
        model = "44 06 40";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 14203: // Zeiss 20X/0.75, PLAN APO
        lensNA = 0.75;
        magnification = 20.0;
        immersion = MetadataTools.getImmersion("Air");
        model = "44 06 49";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 14204: // Zeiss 20X/0.75, Fluar
        lensNA = 0.75;
        magnification = 20.0;
        immersion = MetadataTools.getImmersion("Air");
        model = "44 01 45";
        correction = MetadataTools.getCorrection("Fluar");
        break;
      case 14101: // Zeiss 10X/0.30, Plan - NEOFLUAR
        lensNA = 0.30;
        magnification = 10.0;
        immersion = MetadataTools.getImmersion("Air");
        model = "44 03 30";
        correction = MetadataTools.getCorrection("PlanNeofluar");
        break;
      case 14102: // Zeiss 10X/0.25, Acrostigmat
        lensNA = 0.25;
        magnification = 10.0;
        immersion = MetadataTools.getImmersion("Air");
        model = "44 01 31";
        break;
      case 14103: // Zeiss 10X/0.25, Achroplan, Phase1
        lensNA = 0.25;
        magnification = 10.;
        immersion = MetadataTools.getImmersion("Air");
        model = "44 00 31";
        break;
      case 14104: // Zeiss 10X/0.45, Plan - ApoChromat
        lensNA = 0.45;
        magnification = 10.0;
        immersion = MetadataTools.getImmersion("Air");
        model = "44 06 39";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 14105: // Zeiss 5X/0.16, Plan - ApoChromat
        lensNA = 0.16;
        magnification = 5.0;
        immersion = MetadataTools.getImmersion("Air");
        model = "44 06 20";
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 18101: // API 4X, Plan Apo, AWCE
        lensNA = 0.20;
        magnification = 4.;
        workingDistance = 3.33;
        immersion = MetadataTools.getImmersion("Air");
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 18102: // API 2.5X, Plan Apo, AWe
        lensNA = 0.20;
        magnification = 2.46;
        workingDistance = 2.883;
        immersion = MetadataTools.getImmersion("Air");
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 18103: // API 4X, Plan Apo, AWe
        lensNA = 0.20;
        magnification = 4.;
        workingDistance = 2.883;
        immersion = MetadataTools.getImmersion("Air");
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 18104: // API 6X, Plan Apo, AWe
        lensNA = 0.45;
        magnification = 6.15;
        workingDistance = 2.883;
        immersion = MetadataTools.getImmersion("Air");
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 18105: // API 10X, Plan Apo, AWe, CW
        lensNA = 0.45;
        magnification = 10.;
        workingDistance = 2.883;
        immersion = MetadataTools.getImmersion("Air");
        correction = MetadataTools.getCorrection("PlanApo");
        break;
      case 18106: // API 1X, DIGE
        lensNA = 0.1;
        magnification = 1.0;
        workingDistance = 24.00;
        immersion = MetadataTools.getImmersion("Air");
        break;
      case 18201: // API 20X, HiRes A, CW
        lensNA = 0.55;
        magnification = 20.;
        workingDistance = 13.00;
        immersion = MetadataTools.getImmersion("Air");
        break;
      case 18202: // API 20X, HiRes B, CW
        lensNA = 0.50;
        magnification = 20.;
        workingDistance = 13.00;
        immersion = MetadataTools.getImmersion("Air");
        break;
      case 18204: // API 20X, HiRes C, CW
        lensNA = 0.45;
        magnification = 20.;
        workingDistance = 4.50;
        immersion = MetadataTools.getImmersion("Air");
        model = "MUE01200/92777";
        break;
      case 18205: // API 20X, HiRes D, CW
        lensNA = 0.50;
        magnification = 20.;
        workingDistance = 2.10;
        immersion = MetadataTools.getImmersion("Air");
        model = "MRH00200/93135";
        break;
      case 18206: // API 20X, HiRes, MF
        lensNA = 0.55;
        magnification = 20.;
        workingDistance = 2.10;
        immersion = MetadataTools.getImmersion("Air");
        break;
      case 18207: // API 20X, DF
        lensNA = 0.45;
        magnification = 20.;
        workingDistance = 7.40;
        immersion = MetadataTools.getImmersion("Air");
        break;
      case 18208: // API 20X
        lensNA = 0.75;
        magnification = 20.;
        workingDistance = 1.00;
        immersion = MetadataTools.getImmersion("Air");
        break;
      case 18401: // API 40X, NCG
        lensNA = 0.65;
        magnification = 40.0;
        workingDistance = 0.48;
        immersion = MetadataTools.getImmersion("Air");
        break;
      case 20007: // Applied Precision 100X/1.4
        lensNA = 1.4;
        magnification = 100.0;
        immersion = MetadataTools.getImmersion("Oil");
        manufacturer = "Applied Precision";
        break;
      case 1: // Zeiss 10X/.25
        lensNA = 0.25;
        magnification = 10.0;
        immersion = MetadataTools.getImmersion("Air");
        manufacturer = "Zeiss";
        break;
      case 2: // Zeiss 25X/.50
        lensNA = 0.50;
        magnification = 25.0;
        immersion = MetadataTools.getImmersion("Air");
        manufacturer = "Zeiss";
        break;
      case 3: // Zeiss 50X/1.00
        lensNA = 1.00;
        magnification = 50.0;
        immersion = MetadataTools.getImmersion("Oil");
        manufacturer = "Zeiss";
        break;
      case 4: // Zeiss 63X/1.25
        lensNA = 1.25;
        magnification = 63.0;
        immersion = MetadataTools.getImmersion("Oil");
        manufacturer = "Zeiss";
        break;
      case 5: // Zeiss 100X/1.30
        lensNA = 1.30;
        magnification = 100.0;
        immersion = MetadataTools.getImmersion("Oil");
        manufacturer = "Zeiss";
        break;
      case 6: // Neofluor 100X/1.30
        lensNA = 1.30;
        magnification = 100.0;
        immersion = MetadataTools.getImmersion("Oil");
        correction = MetadataTools.getCorrection("Neofluor");
        break;
      case 7: // Leitz Plan Apo Brightfield
        lensNA = 1.40;
        magnification = 63.0;
        immersion = MetadataTools.getImmersion("Oil");
        correction = MetadataTools.getCorrection("PlanApo");
        manufacturer = "Leitz";
        break;
      case 8: // Coverslip-less water immersion
        lensNA = 1.20;
        magnification = 63.0;
        immersion = MetadataTools.getImmersion("Water");
        break;
      case 9: // Olympus 10X/0.40
        lensNA = 0.40;
        magnification = 10.0;
        workingDistance = 0.30;
        immersion = MetadataTools.getImmersion("Air");
        manufacturer = "Olympus";
        break;
      case 10: // Olympus 20X/0.80
        lensNA = 0.80;
        magnification = 20.0;
        workingDistance = 0.30;
        immersion = MetadataTools.getImmersion("Oil");
        manufacturer = "Olympus";
        break;
      case 11: // Olympus 40X/1.30
        lensNA = 1.30;
        magnification = 40.0;
        workingDistance = 0.30;
        immersion = MetadataTools.getImmersion("Oil");
        manufacturer = "Olympus";
        break;
      case 12: // Olympus 60X/1.40
        lensNA = 1.40;
        magnification = 60.0;
        workingDistance = 0.30;
        immersion = MetadataTools.getImmersion("Oil");
        manufacturer = "Olympus";
        break;
      case 13: // Nikon 100X/1.40
        lensNA = 1.40;
        magnification = 100.0;
        workingDistance = 0.30;
        immersion = MetadataTools.getImmersion("Oil");
        manufacturer = "Nikon";
        break;
      default:
        LOGGER.warn(
          "Unrecognized lens ID {}; objective information may be incorrect",
          lensID);
    }

    String objectiveID = "Objective:" + lensID;
    store.setObjectiveID(objectiveID, 0, 0);
    for (int series=0; series<getSeriesCount(); series++) {
      store.setObjectiveSettingsID(objectiveID, series);
    }

    store.setObjectiveLensNA(lensNA, 0, 0);
    store.setObjectiveImmersion(immersion, 0, 0);
    store.setObjectiveCorrection(correction, 0, 0);
    store.setObjectiveManufacturer(manufacturer, 0, 0);
    store.setObjectiveModel(model, 0, 0);
    store.setObjectiveNominalMagnification(magnification, 0, 0);
    if (workingDistance != null) {
      store.setObjectiveWorkingDistance(new Length(workingDistance, UNITS.MILLIMETER), 0, 0);
    }
  }

  // -- Helper classes --

  /**
   * This private class structure holds the details for the extended header.
   * Instances of the class should <em>NOT</em> leak out of the containing
   * instance.
   * @author Brian W. Loranger
   */
  private static class DVExtHdrFields {

    /** Photosensor reading. Typically in mV. */
    public float photosensorReading;

    /** Time stamp in seconds since the experiment began. */
    public float timeStampSeconds;

    /** X stage coordinates. */
    public Length stageXCoord;

    /** Y stage coordinates. */
    public Length stageYCoord;

    /** Z stage coordinates. */
    public Length stageZCoord;

    /** Minimum intensity */
    public float minInten;

    /** Maxiumum intensity. */
    public float maxInten;

    /** Exposure time in seconds. */
    public float expTime;

    /** Neutral density value. */
    public float ndFilter;

    /** Excitation filter wavelength. */
    public float exWavelen;

    /** Emission filter wavelength. */
    public float emWavelen;

    /** Intensity scaling factor. Usually 1. */
    public float intenScaling;

    /** Energy conversion factor. Usually 1. */
    public float energyConvFactor;

    public Map<String, Object> asMap() {
      Map<String, Object> rv = new HashMap<String, Object>();
      rv.put("photosensorReading", photosensorReading);
      rv.put("timeStampSeconds", timeStampSeconds);
      rv.put("stageXCoord", stageXCoord);
      rv.put("stageYCoord", stageYCoord);
      rv.put("stageZCoord", stageZCoord);
      rv.put("minInten", minInten);
      rv.put("maxInten", maxInten);
      rv.put("expTime", expTime);
      rv.put("ndFilter", ndFilter);
      rv.put("exWavelen", exWavelen);
      rv.put("emWavelen", emWavelen);
      rv.put("intenScaling", intenScaling);
      rv.put("energyConvFactor", energyConvFactor);
      return rv;
    }

    /**
     * Helper function which overrides toString, printing out the values in
     * the header section.
     */
    @Override
    public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append("photosensorReading: ");
      sb.append(photosensorReading);
      sb.append("\ntimeStampSeconds: ");
      sb.append(timeStampSeconds);
      sb.append("\nstageXCoord: ");
      sb.append(stageXCoord);
      sb.append("\nstageYCoord: ");
      sb.append(stageYCoord);
      sb.append("\nstageZCoord: ");
      sb.append(stageZCoord);
      sb.append("\nminInten: ");
      sb.append(minInten);
      sb.append("\nmaxInten: ");
      sb.append(maxInten);
      sb.append("\nexpTime: ");
      sb.append(expTime);
      sb.append("\nndFilter: ");
      sb.append(ndFilter);
      sb.append("\nexWavelen: ");
      sb.append(exWavelen);
      sb.append("\nemWavelen: ");
      sb.append(emWavelen);
      sb.append("\nintenScaling: ");
      sb.append(intenScaling);
      sb.append("\nenergyConvFactor: ");
      sb.append(energyConvFactor);
      return sb.toString();
    }

    private DVExtHdrFields(RandomAccessInputStream in) {
      try {
        // NB: this is consistent with the Deltavision Opener plugin
        // for ImageJ (http://rsb.info.nih.gov/ij/plugins/track/delta.html)
        photosensorReading = in.readFloat();
        timeStampSeconds = in.readFloat();
        stageXCoord = new Length(in.readFloat(), UNITS.REFERENCEFRAME);
        stageYCoord = new Length(in.readFloat(), UNITS.REFERENCEFRAME);
        stageZCoord = new Length(in.readFloat(), UNITS.REFERENCEFRAME);
        minInten = in.readFloat();
        maxInten = in.readFloat();
        in.skipBytes(4);
        expTime = in.readFloat();

        ndFilter = in.readFloat();
        exWavelen = in.readFloat();
        emWavelen = in.readFloat();
        intenScaling = in.readFloat();
        energyConvFactor = in.readFloat();

        // the stored value could be a percent fraction or a percentage
        if (ndFilter >= 1) {
          ndFilter /= 100;
        }
      }
      catch (IOException e) {
        LOGGER.debug("Could not parse extended header", e);
      }
    }

  }

}
