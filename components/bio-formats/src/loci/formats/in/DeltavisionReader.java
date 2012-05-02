//
// DeltavisionReader.java
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

import java.io.IOException;
import java.util.Vector;

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

import ome.xml.model.enums.Correction;
import ome.xml.model.enums.Immersion;
import ome.xml.model.primitives.PositiveFloat;
import ome.xml.model.primitives.PositiveInteger;

/**
 * DeltavisionReader is the file format reader for Deltavision files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/DeltavisionReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/DeltavisionReader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class DeltavisionReader extends FormatReader {

  // -- Constants --

  public static final int DV_MAGIC_BYTES_1 = 0xa0c0;
  public static final int DV_MAGIC_BYTES_2 = 0xc0a0;

  public static final String DATE_FORMAT = "EEE MMM  d HH:mm:ss yyyy";

  private static final short LITTLE_ENDIAN = -16224;
  private static final int HEADER_LENGTH = 1024;

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

  /** Number of tiles in X direction. */
  private int xTiles;

  /** Number of tiles in Y direction. */
  private int yTiles;

  /** Whether or not the stage moved backwards. */
  private boolean backwardsStage = false;

  /**
   * The number of ints in each extended header section. These fields appear
   * to be all blank but need to be skipped to get to the floats afterwards
   */
  protected int numIntsPerSection;
  protected int numFloatsPerSection;

  /** Initialize an array of Extended Header Field structures. */
  protected DVExtHdrFields[][][] extHdrFields = null;

  private Double[] ndFilters;

  private int[] lengths;

  private String logFile;
  private String deconvolutionLogFile;

  // -- Constructor --

  /** Constructs a new Deltavision reader. */
  public DeltavisionReader() {
    super("Deltavision",
      new String[] {"dv", "r3d", "r3d_d3d", "dv.log", "r3d.log"});
    suffixNecessary = false;
    domains = new String[] {FormatTools.LM_DOMAIN};
    hasCompanionFiles = true;
    datasetDescription = "One .dv, .r3d, or .d3d file and up to two " +
      "optional .log files";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
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
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    final int blockLen = 98;
    if (!FormatTools.validStream(stream, blockLen, false)) return false;
    stream.seek(96);
    int magic = stream.readShort() & 0xffff;
    return magic == DV_MAGIC_BYTES_1 || magic == DV_MAGIC_BYTES_2;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    Vector<String> files = new Vector<String>();
    if (!noPixels) files.add(currentId);
    if (logFile != null) files.add(logFile);
    if (deconvolutionLogFile != null) files.add(deconvolutionLogFile);
    return files.toArray(new String[files.size()]);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    int[] coords = getZCTCoords(no);

    int[] newCoords = new int[4];
    int coordIndex = 0;
    int dimIndex = 2;

    while (coordIndex < newCoords.length) {
      char dim = getDimensionOrder().charAt(dimIndex++);

      switch (dim) {
        case 'Z':
          newCoords[coordIndex++] = coords[0];
          break;
        case 'C':
          newCoords[coordIndex++] = coords[1];
          break;
        case 'T':
          newCoords[coordIndex++] = getSeries();
          newCoords[coordIndex++] = coords[2];
          break;
      }
    }

    int planeIndex = FormatTools.positionToRaster(lengths, newCoords);

    // read the image plane's pixel data
    long planeSize = (long) FormatTools.getPlaneSize(this);
    long planeOffset = planeSize * planeIndex;
    long offset = planeOffset + HEADER_LENGTH + extSize;
    if (offset < in.length()) {
      in.seek(HEADER_LENGTH + extSize + planeOffset);
      readPlane(in, x, y, w, h, buf);
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (!fileOnly) {
      extSize = wSize = zSize = tSize = 0;
      numIntsPerSection = numFloatsPerSection = 0;
      extHdrFields = null;
      ndFilters = null;
      logFile = deconvolutionLogFile = null;
      lengths = null;
      backwardsStage = false;
      xTiles = 0;
      yTiles = 0;
    }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
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

    in.seek(180);
    int rawSizeT = in.readShort();
    int sizeT = rawSizeT == 0 ? 1 : rawSizeT;

    int sequence = in.readShort();

    in.seek(92);
    extSize = in.readInt();

    in.seek(196);
    int rawSizeC = in.readShort();
    int sizeC = rawSizeC == 0 ? 1 : rawSizeC;

    // --- compute some secondary values ---

    String imageSequence = getImageSequence(sequence);

    int sizeZ = imageCount / (sizeC * sizeT);

    // --- populate core metadata ---

    LOGGER.info("Populating core metadata");

    core[0].littleEndian = little;
    core[0].sizeX = sizeX;
    core[0].sizeY = sizeY;
    core[0].imageCount = imageCount;

    String pixel = getPixelString(filePixelType);
    core[0].pixelType = getPixelType(filePixelType);

    core[0].dimensionOrder = "XY" + imageSequence.replaceAll("W", "C");

    int planeSize =
      getSizeX() * getSizeY() * FormatTools.getBytesPerPixel(getPixelType());
    int realPlaneCount =
      (int) ((in.length() - HEADER_LENGTH - extSize) / planeSize);
    if (realPlaneCount < getImageCount()) {
      LOGGER.debug("Truncated file");
      core[0].imageCount = realPlaneCount;
      if (sizeZ == 1) {
        sizeT = realPlaneCount / sizeC;
      }
      else if (sizeT == 1) {
        sizeZ = realPlaneCount / sizeC;
        if ((realPlaneCount % sizeC) != 0) {
          sizeZ++;
          core[0].imageCount = sizeZ * sizeC;
        }
      }
      else if (getDimensionOrder().indexOf("Z") <
        getDimensionOrder().indexOf("T"))
      {
        sizeZ = realPlaneCount / (sizeC * sizeT);
        if (sizeZ == 0) {
          sizeT = 1;
          sizeZ = realPlaneCount / sizeC;
          if ((realPlaneCount % sizeC) != 0) {
            sizeZ++;
            core[0].imageCount = sizeZ * sizeC;
          }
        }
        if (getImageCount() > (sizeZ * sizeC * sizeT)) {
          core[0].imageCount = imageCount;
          sizeC = rawSizeC == 0 ? 1 : rawSizeC;
          sizeT = rawSizeT == 0 ? 1 : rawSizeT;
          sizeZ = getImageCount() / (sizeC * sizeT);
        }
      }
      else {
        sizeT = realPlaneCount / (sizeC * sizeZ);
      }
    }

    core[0].sizeT = sizeT;
    core[0].sizeC = sizeC;
    core[0].sizeZ = sizeZ;

    core[0].rgb = false;
    core[0].interleaved = false;
    core[0].metadataComplete = true;
    core[0].indexed = false;
    core[0].falseColor = false;

    // --- parse extended header ---

    in.seek(128);
    numIntsPerSection = in.readShort();
    numFloatsPerSection = in.readShort();

    LOGGER.info("Reading extended header");

    setOffsetInfo(sequence, getSizeZ(), getSizeC(), getSizeT());
    extHdrFields = new DVExtHdrFields[getSizeZ()][getSizeC()][getSizeT()];

    ndFilters = new Double[getSizeC()];

    Vector<Float> uniqueTileX = new Vector<Float>();
    Vector<Float> uniqueTileY = new Vector<Float>();

    // Run through every image and fill in the
    // Extended Header information array for that image
    int offset = HEADER_LENGTH + numIntsPerSection * 4;
    for (int i=0; i<getImageCount(); i++) {
      int[] coords = getZCTCoords(i);
      int z = coords[0];
      int w = coords[1];
      int t = coords[2];

      // -- read in the extended header data --

      in.seek(offset + getTotalOffset(z, w, t));
      DVExtHdrFields hdr = new DVExtHdrFields(in);
      extHdrFields[z][w][t] = hdr;

      if (!uniqueTileX.contains(hdr.stageXCoord)) {
        uniqueTileX.add(hdr.stageXCoord);
      }
      if (!uniqueTileY.contains(hdr.stageYCoord)) {
        uniqueTileY.add(hdr.stageYCoord);
      }
    }

    xTiles = uniqueTileX.size();
    yTiles = uniqueTileY.size();

    if (yTiles > 1) {
      if (uniqueTileY.get(1) < uniqueTileY.get(0)) {
        backwardsStage = true;
      }
    }

    int nStagePositions = xTiles * yTiles;
    if (nStagePositions > 0 && nStagePositions <= getSizeT()) {
      int t = getSizeT();
      core[0].sizeT /= nStagePositions;
      if (getSizeT() * nStagePositions != t) {
        core[0].sizeT = t;
        nStagePositions = 1;
      }
      else {
        core[0].imageCount /= nStagePositions;
      }

      if (nStagePositions > 1) {
        CoreMetadata originalCore = core[0];
        core = new CoreMetadata[nStagePositions];
        for (int i=0; i<core.length; i++) {
          core[i] = originalCore;
        }
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
          lengths[lengthIndex++] = getSeriesCount();
          lengths[lengthIndex++] = getSizeT();
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

    addGlobalMeta("Image sequence", imageSequence);

    addGlobalMeta("Number of wavelengths", rawSizeC);
    addGlobalMeta("Number of focal planes", sizeZ);

    for (int series=0; series<getSeriesCount(); series++) {
      setSeries(series);
      for (int plane=0; plane<getImageCount(); plane++) {
        int[] coords = getZCTCoords(plane);

        int tIndex = getSeriesCount() * coords[2] + series;
        DVExtHdrFields hdr = extHdrFields[coords[0]][coords[1]][tIndex];

        // -- record original metadata --

        // NB: It adds a little overhead to record the extended headers into the
        // original metadata table, but not as much as registering every header
        // field individually. With this approach it is still easy to
        // programmatically access any given extended header field.
        String prefix =
          "Extended header Z" + coords[0] + " W" + coords[1] + " T" + coords[2];
        addSeriesMeta(prefix, hdr);

        String position = " position for position #" + (series + 1);
        addGlobalMeta("X" + position, hdr.stageXCoord);
        addGlobalMeta("Y" + position, hdr.stageYCoord);
        addGlobalMeta("Z" + position, hdr.stageZCoord);
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

    float[] minWave = new float[5];
    float[] maxWave = new float[5];

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

    int type = in.readShort();
    int lensID = in.readShort();

    in.seek(172);

    minWave[4] = in.readFloat();
    maxWave[4] = in.readFloat();

    in.seek(184);

    float xTiltAngle = in.readFloat();
    float yTiltAngle = in.readFloat();
    float zTiltAngle = in.readFloat();

    in.skipBytes(2);

    short[] waves = new short[5];
    for (int i=0; i<waves.length; i++) {
      waves[i] = in.readShort();
    }

    float xOrigin = in.readFloat();
    float yOrigin = in.readFloat();
    float zOrigin = in.readFloat();

    in.skipBytes(4);

    String[] title = new String[10];
    for (int i=0; i<title.length; i++) {
      // Make sure that "null" characters are stripped out
      title[i] = in.readString(80).replaceAll("\0", "");
    }

    // --- compute some secondary values ---

    String imageType =
      type < IMAGE_TYPES.length ? IMAGE_TYPES[type] : "unknown";

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

    for (int i=0; i<title.length; i++) {
      addGlobalMeta("Title " + (i + 1), title[i]);
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

      if (pixX > 0) {
        Double x = new Double(pixX);
        store.setPixelsPhysicalSizeX(new PositiveFloat(x), series);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeX; got {}", pixX);
      }
      if (pixY > 0) {
        Double y = new Double(pixY);
        store.setPixelsPhysicalSizeY(new PositiveFloat(y), series);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeY; got {}", pixY);
      }
      if (pixZ > 0) {
        Double z = new Double(pixZ);
        store.setPixelsPhysicalSizeZ(new PositiveFloat(z), series);
      }
      else {
        LOGGER.warn("Expected positive value for PhysicalSizeZ; got {}", pixZ);
      }

      store.setImageDescription(imageDesc, series);
    }

    populateObjective(store, lensID);

    // if matching log file exists, extract key/value pairs from it
    boolean logFound = isGroupFiles() ? parseLogFile(store) : false;
    if (isGroupFiles()) parseDeconvolutionLog(store);

    if (getSeriesCount() == 1) {
      xTiles = 1;
      yTiles = 1;
      backwardsStage = false;
    }

    for (int series=0; series<getSeriesCount(); series++) {
      int seriesIndex = series;
      if (backwardsStage) {
        int x = series % xTiles;
        int y = series / xTiles;
        seriesIndex = (yTiles - y - 1) * xTiles + (xTiles - x - 1);
      }

      for (int i=0; i<getImageCount(); i++) {
        int[] coords = getZCTCoords(i);

        int tIndex = getSeriesCount() * coords[2] + seriesIndex;
        DVExtHdrFields hdr = extHdrFields[coords[0]][coords[1]][tIndex];

        // plane timing
        if (!logFound) {
          store.setPlaneDeltaT(new Double(hdr.timeStampSeconds), series, i);
        }
        store.setPlaneExposureTime(new Double(hdr.expTime), series, i);

        // stage position
        if (!logFound || getSeriesCount() > 1) {
          store.setPlanePositionX(new Double(hdr.stageXCoord), series, i);
          store.setPlanePositionY(new Double(hdr.stageYCoord), series, i);
          store.setPlanePositionZ(new Double(hdr.stageZCoord), series, i);
        }
      }

      for (int w=0; w<getSizeC(); w++) {
        DVExtHdrFields hdrC = extHdrFields[0][w][series];
        if ((int) waves[w] > 0) {
          store.setChannelEmissionWavelength(
            new PositiveInteger((int) waves[w]), series, w);
        }
        else {
          LOGGER.warn("Expected positive value for EmissionWavelength; got {}",
            waves[w]);
        }
        if ((int) hdrC.exWavelen > 0) {
          store.setChannelExcitationWavelength(
            new PositiveInteger((int) hdrC.exWavelen), series, w);
        }
        else {
          LOGGER.warn("Expected positive alue for ExcitationWavelength; got {}",
            hdrC.exWavelen);
        }
        if (ndFilters[w] == null) ndFilters[w] = new Double(hdrC.ndFilter);
        store.setChannelNDFilter(ndFilters[w], series, w);
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
    }
    return FormatTools.UINT8;
  }

  /** Get the image sequence string. */
  private String getImageSequence(int imageSequence) {
    switch (imageSequence) {
      case 0:
        return "ZTW";
      case 1:
        return "WZT";
      case 2:
        return "ZWT";
      case 65536:
        return "WZT";
    }
    return "ZTW";
  }

  /**
   * This method calculates the size of a w, t, z section depending on which
   * sequence is being used (either ZTW, WZT, or ZWT)
   * @param imgSequence
   * @param numZSections
   * @param numWaves
   * @param numTimes
   */
  private void setOffsetInfo(int imgSequence, int numZSections,
    int numWaves, int numTimes)
  {
    int smallOffset = (numIntsPerSection + numFloatsPerSection) * 4;

    switch (imgSequence) {
      // ZTW sequence
      case 0:
        zSize = smallOffset;
        tSize = zSize * numZSections;
        wSize = tSize * numTimes;
        break;

      // WZT sequence
      case 1:
        wSize = smallOffset;
        zSize = wSize * numWaves;
        tSize = zSize * numZSections;
        break;

      // ZWT sequence
      case 2:
        zSize = smallOffset;
        wSize = zSize * numZSections;
        tSize = wSize * numWaves;
        break;
    }
  }

  /**
   * Given any specific Z, W, and T of a plane, determine the totalOffset from
   * the start of the extended header.
   * @param currentZ
   * @param currentW
   * @param currentT
   */
  private int getTotalOffset(int currentZ, int currentW, int currentT) {
    return (zSize * currentZ) + (wSize * currentW) + (tSize * currentT);
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

    for (String line : lines) {
      int colon = line.indexOf(":");
      if (colon != -1) {
        if (line.startsWith("Created")) {
          key = "Created";
          colon = 6;
        }
        else key = line.substring(0, colon).trim();

        value = line.substring(colon + 1).trim();
        if (value.equals("") && !key.equals("")) prefix = key;
        addGlobalMeta(prefix + " " + key, value);

        // Objective properties
        if (key.equals("Objective")) {
          // assume first word is the manufacturer's name
          int space = value.indexOf(" ");
          if (space != -1) {
            String manufacturer = value.substring(0, space);
            String extra = value.substring(space + 1);

            String[] tokens = extra.split(",");

            store.setObjectiveManufacturer(manufacturer, 0, 0);

            String magnification = "", na = "";

            if (tokens.length >= 1) {
              int end = tokens[0].indexOf("X");
              if (end > 0) magnification = tokens[0].substring(0, end);
              int start = tokens[0].indexOf("/");
              if (start >= 0) na = tokens[0].substring(start + 1);
            }

            try {
              Integer mag = new Integer(magnification);
              if (mag > 0) {
                store.setObjectiveNominalMagnification(
                  new PositiveInteger(mag), 0, 0);
              }
              else {
                LOGGER.warn(
                  "Expected positive value for NominalMagnification; got {}",
                  mag);
              }
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
              store.setObjectiveCorrection(getCorrection(tokens[1]), 0, 0);
            }
            // TODO:  Token #2 is the microscope model name.
            if (tokens.length > 3) store.setObjectiveModel(tokens[3], 0, 0);
          }
        }
        else if (key.equalsIgnoreCase("Lens ID")) {
          if (value.indexOf(",") != -1) {
            value = value.substring(0, value.indexOf(","));
          }
          if (value.indexOf(" ") != -1) {
            value = value.substring(value.indexOf(" ") + 1);
          }
          String objectiveID = "Objective:" + value;
          store.setObjectiveID(objectiveID, 0, 0);
          for (int series=0; series<getSeriesCount(); series++) {
            store.setImageObjectiveSettingsID(objectiveID, series);
          }
          store.setObjectiveCorrection(getCorrection("Other"), 0, 0);
          store.setObjectiveImmersion(getImmersion("Other"), 0, 0);
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
            if (size == null || size <= 0) {
              LOGGER.warn("Expected positive value for PhysicalSize; got {}",
                size);
              continue;
            }
            if (q == 0) {
              for (int series=0; series<getSeriesCount(); series++) {
                store.setPixelsPhysicalSizeX(new PositiveFloat(size), series);
              }
            }
            if (q == 1) {
              for (int series=0; series<getSeriesCount(); series++) {
                store.setPixelsPhysicalSizeY(new PositiveFloat(size), series);
              }
            }
            if (q == 2) {
              for (int series=0; series<getSeriesCount(); series++) {
                store.setPixelsPhysicalSizeZ(new PositiveFloat(size), series);
              }
            }
          }
        }
        else if (key.equals("Binning")) {
          store.setDetectorType(getDetectorType("Other"), 0, 0);
          String detectorID = MetadataTools.createLSID("Detector", 0, 0);
          store.setDetectorID(detectorID, 0, 0);
          for (int series=0; series<getSeriesCount(); series++) {
            for (int c=0; c<getSizeC(); c++) {
              store.setDetectorSettingsBinning(getBinning(value), series, c);
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
            for (int series=0; series<getSeriesCount(); series++) {
              for (int c=0; c<getSizeC(); c++) {
                store.setDetectorSettingsGain(new Double(value), series, c);
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
            double mhz = Double.parseDouble(value) / 1000;
            for (int series=0; series<getSeriesCount(); series++) {
              for (int c=0; c<getSizeC(); c++) {
                store.setDetectorSettingsReadOutRate(mhz, series, c);
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
        else if (key.equals("Time Point")) {
          int space = value.indexOf(" ");
          if (space >= 0) value = value.substring(0, space);
          try {
            if (currentImage < getImageCount()) {
              for (int series=0; series<getSeriesCount(); series++) {
                store.setPlaneDeltaT(new Double(value), series, currentImage);
              }
            }
          }
          catch (NumberFormatException e) {
            LOGGER.warn("Could not parse timestamp '{}'", value);
          }
        }
        else if (key.equals("EM filter")) {
          int cIndex = 0;
          try {
            cIndex = getZCTCoords(currentImage)[1];
          }
          catch (IllegalArgumentException e) {
            LOGGER.debug("", e);
          }
          for (int series=0; series<getSeriesCount(); series++) {
            store.setChannelName(value, series, cIndex);
          }
        }
        else if (key.equals("ND filter")) {
          value = value.replaceAll("%", "");
          try {
            int cIndex = getZCTCoords(currentImage)[1];
            double nd = Double.parseDouble(value);
            ndFilters[cIndex] = new Double(nd / 100);
          }
          catch (NumberFormatException exc) {
            LOGGER.warn("Could not parse ND filter '{}'", value);
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
            Double p = null;
            try {
              p = new Double(coords[i].trim());
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
        String date = DateTools.formatDate(line, DATE_FORMAT);
        if (date != null) {
          for (int series=0; series<getSeriesCount(); series++) {
            store.setImageAcquiredDate(date, series);
          }
        }
        else {
          LOGGER.warn("Could not parse date '{}'", line);
        }
      }
    }

    return true;
  }

  /** Parse deconvolution output, if it exists. */
  private void parseDeconvolutionLog(MetadataStore store) throws IOException {
    if (deconvolutionLogFile == null ||
      !new Location(deconvolutionLogFile).exists())
    {
      return;
    }

    LOGGER.info("Parsing deconvolution log file");

    RandomAccessInputStream s =
      new RandomAccessInputStream(deconvolutionLogFile);

    boolean doStatistics = false;
    int cc = 0, tt = 0;
    String previousLine = null;

    while (s.getFilePointer() < s.length() - 1) {
      String line = s.readLine();
      if (line == null || line.length() == 0) continue;

      if (doStatistics) {
        String[] keys = line.split("  ");
        Vector<String> realKeys = new Vector<String>();
        for (int i=0; i<keys.length; i++) {
          keys[i] = keys[i].trim();
          if (keys[i].length() > 0) realKeys.add(keys[i]);
        }
        keys = realKeys.toArray(new String[0]);

        s.readLine();

        line = s.readLine().trim();
        while (line != null && line.length() != 0) {
          String[] values = line.split(" ");
          Vector<String> realValues = new Vector<String>();
          for (int i=0; i<values.length; i++) {
            values[i] = values[i].trim();
            if (values[i].length() > 0) { realValues.add(values[i]); }
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

      if (line.length() > 0 && line.indexOf(".") == -1) previousLine = line;

      doStatistics = line.endsWith("- reading image data...");
    }
    s.close();
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
  private void populateObjective(MetadataStore store, int lensID)
    throws FormatException
  {
    Double lensNA = null;
    Double workingDistance = null;
    Immersion immersion = getImmersion("Other");
    Correction correction = getCorrection("Other");
    String manufacturer = null;
    String model = null;
    int magnification = 0;
    Double calibratedMagnification = null;

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

      magnification = ((lensID % 1000) - (lensID % 100)) / 10;
      if (magnification == 0) {
        magnification = 100;
      }
    }

    switch (lensID) {
      case 2001:
        lensNA = 1.15;
        immersion = getImmersion("Water");
        break;
      case 10100:
        lensNA = 0.30;
        immersion = getImmersion("Air");
        model = "1-LP134";
        correction = getCorrection("Achromat");
        break;
      case 10101:
        lensNA = 0.40;
        immersion = getImmersion("Air");
        model = "1-LB331";
        correction = getCorrection("Apo");
        break;
      case 10102:
        lensNA = 0.30;
        immersion = getImmersion("Air");
        model = "1-LP134";
        break;
      case 10103:
        lensNA = 0.13;
        calibratedMagnification = 4.0;
        immersion = getImmersion("Air");
        model = "1-LP124";
        break;
      case 10104:
        lensNA = 0.40;
        immersion = getImmersion("Air");
        model = "1-LB331";
        break;
      case 10105:
        lensNA = 0.40;
        workingDistance = 3.10;
        immersion = getImmersion("Air");
        model = "1-LP331";
        break;
      case 10106:
        lensNA = 0.16;
        calibratedMagnification = 4.0;
        workingDistance = 13.0;
        immersion = getImmersion("Air");
        correction = getCorrection("PlanApo");
        model = "1-UB822";
        break;
      case 10107:
        lensNA = 0.40;
        immersion = getImmersion("Air");
        workingDistance = 3.10;
        model = "1-UB823";
        correction = getCorrection("PlanApo");
        break;
      case 10108:
        lensNA = 0.25;
        workingDistance = 9.8;
        immersion = getImmersion("Air");
        model = "1-UC243";
        break;
      case 10109:
        lensNA = 0.30;
        immersion = getImmersion("Air");
        model = "1-UB532";
        correction = getCorrection("PlanFluor");
        break;
      case 10110:
        lensNA = 0.13;
        calibratedMagnification = 4.0;
        immersion = getImmersion("Air");
        break;
      case 10111:
        lensNA = 0.08;
        calibratedMagnification = 2.0;
        immersion = getImmersion("Air");
        break;
      case 10112:
        lensNA = 0.13;
        calibratedMagnification = 4.0;
        immersion = getImmersion("Air");
        model = "1-UB522";
        break;
      case 10113:
        lensNA = 0.04;
        calibratedMagnification = 1.25;
        workingDistance = 5.1;
        immersion = getImmersion("Air");
        model = "1-UB920";
        correction = getCorrection("PlanApo");
        break;
      case 10114:
        lensNA = 0.08;
        calibratedMagnification = 2.0;
        workingDistance = 6.0;
        immersion = getImmersion("Air");
        model = "1-UB921";
        correction = getCorrection("PlanApo");
        break;
      case 10200:
        lensNA = 0.40;
        workingDistance = 3.0;
        immersion = getImmersion("Air");
        break;
      case 10201:
        lensNA = 0.65;
        workingDistance = 1.03;
        immersion = getImmersion("Air");
        model = "1-LB343";
        correction = getCorrection("Apo");
        break;
      case 10202:
        lensNA = 0.40;
        immersion = getImmersion("Air");
        model = "1-LP146";
        break;
      case 10203:
        lensNA = 0.80;
        immersion = getImmersion("Oil");
        model = "1-LB342";
        correction = getCorrection("PlanApo");
        break;
      case 10204:
        lensNA = 0.70;
        immersion = getImmersion("Air");
        model = "1-LB341";
        break;
      case 10205:
        lensNA = 0.75;
        workingDistance = 0.55;
        immersion = getImmersion("Air");
        model = "1-UB765";
        correction = getCorrection("Apo");
        break;
      case 10206:
        lensNA = 0.50;
        workingDistance = 0.55;
        immersion = getImmersion("Air");
        model = "1-UC525";
        break;
      case 10207:
        lensNA = 0.40;
        workingDistance = 3.0;
        immersion = getImmersion("Air");
        model = "1-UC145";
        correction = getCorrection("Achromat");
        break;
      case 10208:
        lensNA = 0.50;
        workingDistance = 0.55;
        immersion = getImmersion("Air");
        model = "1-UB525";
        break;
      case 10209:
        lensNA = 0.40;
        workingDistance = 6.9;
        immersion = getImmersion("Air");
        model = "1-UC345";
        break;
      case 10210:
        lensNA = 0.40;
        workingDistance = 6.9;
        immersion = getImmersion("Air");
        model = "1-UB345";
        break;
      case 10400:
        lensNA = 0.55;
        workingDistance = 2.04;
        immersion = getImmersion("Air");
        break;
      case 10401:
        lensNA = 0.85;
        workingDistance = 0.25;
        immersion = getImmersion("Air");
        break;
      case 10402:
        lensNA = 1.30;
        workingDistance = 0.12;
        immersion = getImmersion("Oil");
        model = "1-LB356";
        break;
      case 10403:
        lensNA = 1.35;
        workingDistance = 0.10;
        immersion = getImmersion("Oil");
        model = "1-UB768";
        break;
      case 10404:
        lensNA = 0.85;
        workingDistance = 0.20;
        immersion = getImmersion("Air");
        correction = getCorrection("PlanApo");
        model = "1-UB827";
        break;
      case 10405:
        lensNA = 0.95;
        workingDistance = 0.14;
        immersion = getImmersion("Air");
        model = "1-UB927";
        correction = getCorrection("PlanApo");
        break;
      case 10406:
        lensNA = 1.0;
        immersion = getImmersion("Oil");
        correction = getCorrection("PlanApo");
        model = "1-UB828";
        break;
      case 10407:
        lensNA = 0.75;
        correction = getCorrection("PlanFluor");
        immersion = getImmersion("Air");
        model = "1-UB527";
        break;
      case 10408:
        lensNA = 0.60;
        workingDistance = 2.15;
        immersion = getImmersion("Air");
        model = "1-UB347";
        break;
      case 10409:
        lensNA = 0.60;
        workingDistance = 2.15;
        immersion = getImmersion("Air");
        model = "1-UC347";
        break;
      case 10410:
        lensNA = 1.15;
        immersion = getImmersion("Water");
        model = "1-UB769";
        break;
      case 10411:
        lensNA = 0.75;
        immersion = getImmersion("Air");
        model = "1-UC527";
        correction = getCorrection("PlanFluor");
        break;
      case 10412:
        lensNA = 1.34;
        workingDistance = 0.10;
        immersion = getImmersion("Oil");
        correction = getCorrection("Apo");
        break;
      case 10000:
        lensNA = 1.30;
        correction = getCorrection("Apo");
        immersion = getImmersion("Oil");
        model = "1-LB393";
        break;
      case 10001:
        lensNA = 1.30;
        immersion = getImmersion("Oil");
        model = "1-LB392";
        break;
      case 10002:
        lensNA = 1.40;
        workingDistance = 0.10;
        immersion = getImmersion("Oil");
        model = "1-UB935";
        correction = getCorrection("PlanApo");
        break;
      case 10003:
        lensNA = 1.35;
        workingDistance = 0.10;
        immersion = getImmersion("Oil");
        model = "1-UB836";
        correction = getCorrection("PlanApo");
        break;
      case 10004:
        lensNA = 1.30;
        immersion = getImmersion("Oil");
        model = "1-UB535";
        break;
      case 10005:
        lensNA = 1.35;
        workingDistance = 0.10;
        immersion = getImmersion("Oil");
        correction = getCorrection("PlanApo");
        break;
      case 10006:
        lensNA = 1.40;
        workingDistance = 0.10;
        immersion = getImmersion("Oil");
        correction = getCorrection("PlanApo");
        break;
      case 10007:
        lensNA = 1.40;
        workingDistance = 0.13;
        immersion = getImmersion("Oil");;
        model = "1-U2B836";
        break;
      case 10600:
        lensNA = 1.40;
        workingDistance = 0.30;
        immersion = getImmersion("Oil");
        break;
      case 10601:
        lensNA = 1.40;
        immersion = getImmersion("Oil");
        model = "1-LB751";
        break;
      case 10602:
        lensNA = 1.40;
        workingDistance = 0.1;
        immersion = getImmersion("Oil");
        model = "1-UB932";
        correction = getCorrection("PlanApo");
        break;
      case 10603:
        lensNA = 1.20;
        workingDistance = 0.25;
        immersion = getImmersion("Water");
        model = "1-UB891";
        break;
      case 10604:
        lensNA = 1.20;
        workingDistance = 0.25;
        immersion = getImmersion("Water");
        break;
      case 10605:
        lensNA = 0.70;
        workingDistance = 1.10;
        immersion = getImmersion("Air");
        model = "1-UB351";
        break;
      case 10606:
        lensNA = 0.70;
        workingDistance = 1.10;
        immersion = getImmersion("Air");
        model = "1-UC351";
        break;
      case 10607:
        lensNA = 1.40;
        immersion = getImmersion("Oil");
        model = "1-UC932";
        correction = getCorrection("PlanApo");
        break;
      case 10608:
        lensNA = 1.25;
        immersion = getImmersion("Oil");
        model = "1-UB532";
        break;
      case 10609:
        lensNA = 1.40;
        workingDistance = 0.15;
        immersion = getImmersion("Oil");
        model = "1-UB933";
        correction = getCorrection("PlanApo");
        break;
      case 10610:
        lensNA = 1.40;
        workingDistance = 0.15;
        immersion = getImmersion("Oil");
        correction = getCorrection("PlanApo");
        break;
      case 10611:
        lensNA = 1.20;
        workingDistance = 0.25;
        immersion = getImmersion("Water");
        correction = getCorrection("PlanApo");
        break;
      case 10612:
        lensNA = 1.42;
        workingDistance = 0.15;
        immersion = getImmersion("Oil");
        model = "1-U2B933";
        correction = getCorrection("PlanApo");
        break;
      case 12201:
        lensNA = 0.75;
        immersion = getImmersion("Air");
        correction = getCorrection("PlanFluor");
        model = "93146";
        break;
      case 12203:
        lensNA = 0.45;
        workingDistance = 8.1;
        immersion = getImmersion("Air");
        correction = getCorrection("PlanFluor");
        model = "93150";
        break;
      case 12204:
        lensNA = 0.45;
        workingDistance = 4.50;
        immersion = getImmersion("Air");
        model = "MUE01200/92777";
        break;
      case 12205:
        lensNA = 0.50;
        workingDistance = 2.10;
        immersion = getImmersion("Air");
        correction = getCorrection("PlanFluor");
        model = "MRH00200/93135";
        break;
      case 12401:
        lensNA = 1.30;
        workingDistance = 0.16;
        immersion = getImmersion("Oil");
        model = "85028";
        break;
      case 12402:
        lensNA = 1.30;
        workingDistance = 0.22;
        immersion = getImmersion("Oil");
        model = "85004";
        break;
      case 12403:
        lensNA = 0.75;
        immersion = getImmersion("Air");
        model = "140508";
        break;
      case 12404:
        lensNA = 1.30;
        workingDistance = 0.22;
        immersion = getImmersion("Oil");
        break;
      case 12405:
        lensNA = 0.95;
        immersion = getImmersion("Air");
        correction = getCorrection("PlanApo");
        model = "93105";
        break;
      case 12406:
        lensNA = 1.00;
        workingDistance = 0.16;
        immersion = getImmersion("Oil");
        model = "93106";
        break;
      case 12600:
        lensNA = 1.40;
        workingDistance = 0.17;
        immersion = getImmersion("Oil");
        model = "85020";
        break;
      case 12601:
        lensNA = 1.40;
        workingDistance = 0.21;
        immersion = getImmersion("Oil");
        correction = getCorrection("PlanApo");
        model = "93108";
        break;
      case 12602:
        lensNA = 1.20;
        workingDistance = 0.22;
        immersion = getImmersion("Water");
        correction = getCorrection("PlanApo");
        model = "93109";
        break;
      case 12000:
        lensNA = 1.40;
        workingDistance = 0.1;
        immersion = getImmersion("Oil");
        correction = getCorrection("PlanApo");
        model = "85025";
        break;
      case 12001:
        lensNA = 1.30;
        workingDistance = 0.14;
        immersion = getImmersion("Oil");
        model = "85005";
        break;
      case 12002:
        lensNA = 1.30;
        workingDistance = 0.14;
        immersion = getImmersion("Oil");
        correction = getCorrection("UV");
        model = "85005";
        break;
      case 12003:
        lensNA = 1.40;
        workingDistance = 0.13;
        immersion = getImmersion("Oil");
        correction = getCorrection("PlanApo");
        model = "93110";
        break;
      case 12004:
        lensNA = 1.30;
        workingDistance = 0.20;
        immersion = getImmersion("Oil");
        model = "93129";
        break;
      case 12101:
        lensNA = 0.10;
        calibratedMagnification = 2.0;
        immersion = getImmersion("Air");
        correction = getCorrection("PlanApo");
        model = "202294";
        break;
      case 12102:
        lensNA = 0.20;
        calibratedMagnification = 4.0;
        immersion = getImmersion("Air");
        correction = getCorrection("PlanApo");
        model = "108388";
        break;
      case 12103:
        lensNA = 0.2;
        calibratedMagnification = 4.0;
        workingDistance = 15.7;
        correction = getCorrection("PlanApo");
        immersion = getImmersion("Air");
        model = "93102";
        break;
      case 12104:
        lensNA = 0.45;
        workingDistance = 4.0;
        immersion = getImmersion("Air");
        correction = getCorrection("PlanApo");
        model = "93103";
        break;
      case 12105:
        lensNA = 0.30;
        workingDistance = 16.0;
        immersion = getImmersion("Air");
        correction = getCorrection("PlanFluor");
        model = "93134";
        break;
      case 12106:
        lensNA = 0.50;
        workingDistance = 1.20;
        immersion = getImmersion("Air");
        correction = getCorrection("SuperFluor");
        model = "93126";
        break;
      case 12107:
        lensNA = 0.25;
        workingDistance = 10.5;
        immersion = getImmersion("Air");
        model = "93183";
        break;
      case 12108:
        lensNA = 0.25;
        workingDistance = 6.10;
        immersion = getImmersion("Air");
        correction = getCorrection("Achromat");
        model = "93161";
        break;
      case 14001:
        lensNA = 1.40;
        workingDistance = 0.1;
        immersion = getImmersion("Oil");
        correction = getCorrection("PlanApo");
        model = "44 07 08 (02)";
        break;
      case 14002:
        lensNA = 1.30;
        workingDistance = 0.1;
        immersion = getImmersion("Oil");
        model = "44 07 86";
        break;
      case 14003:
        lensNA = 1.40;
        immersion = getImmersion("Oil");
        correction = getCorrection("PlanApo");
        model = "44 07 80 (02)";
        break;
      case 14004:
        lensNA = 1.30;
        immersion = getImmersion("Oil");
        correction = getCorrection("PlanApo");
        model = "46 19 46 - 9903";
        break;
      case 14005:
        lensNA = 1.40;
        immersion = getImmersion("Oil");
        model = "44 07 86 (02)";
        break;
      case 14006:
        lensNA = 1.30;
        immersion = getImmersion("Oil");
        break;
      case 14601:
        lensNA = 1.40;
        calibratedMagnification = 63.0;
        workingDistance = 0.09;
        immersion = getImmersion("Oil");
        correction = getCorrection("PlanApo");
        model = "44 07 60 (03)";
        break;
      case 14602:
        lensNA = 1.40;
        calibratedMagnification = 63.0;
        workingDistance = 0.09;
        immersion = getImmersion("Oil");
        correction = getCorrection("PlanApo");
        model = "44 07 62 (02)";
        break;
      case 14603:
        lensNA = 0.90;
        calibratedMagnification = 63.0;
        immersion = getImmersion("Water");
        model = "44 00 69";
        break;
      case 14604:
        lensNA = 1.20;
        workingDistance = 0.09;
        calibratedMagnification = 63.0;
        immersion = getImmersion("Water");
        model = "44 06 68";
        break;
      case 14401:
        lensNA = 1.30;
        workingDistance = 0.14;
        immersion = getImmersion("Oil");
        correction = getCorrection("Fluar");
        model = "44 02 55 (01)";
        break;
      case 14402:
        lensNA = 1.00;
        immersion = getImmersion("Oil");
        correction = getCorrection("PlanApo");
        model = "44 07 51";
        break;
      case 14403:
        lensNA = 1.20;
        immersion = getImmersion("Water");
        correction = getCorrection("Apo");
        model = "44 00 52";
        break;
      case 14404:
        lensNA = 0.75;
        immersion = getImmersion("Air");
        correction = getCorrection("PlanNeofluar");
        model = "44 03 51";
        break;
      case 14405:
        lensNA = 1.30;
        workingDistance = 0.15;
        immersion = getImmersion("Oil");
        correction = getCorrection("PlanNeofluar");
        model = "44 04 50";
        break;
      case 14406:
        lensNA = 0.60;
        immersion = getImmersion("Air");
        model = "44 08 65";
        break;
      case 14407:
        lensNA = 1.30;
        immersion = getImmersion("Air");
        correction = getCorrection("PlanNeofluar");
        break;
      case 14301:
        lensNA = 0.80;
        calibratedMagnification = 25.0;
        workingDistance = 0.80;
        correction = getCorrection("PlanNeofluar");
        model = "44 05 44";
        break;
      case 14302:
        lensNA = 0.80;
        workingDistance = 0.80;
        calibratedMagnification = 25.0;
        correction = getCorrection("PlanNeofluar");
        model = "44 05 42";
        break;
      case 14303:
        lensNA = 0.80;
        calibratedMagnification = 25.0;
        immersion = getImmersion("Air");
        correction = getCorrection("PlanNeofluar");
        model = "44 05 45";
        break;
      case 14201:
        lensNA = 0.50;
        immersion = getImmersion("Air");
        correction = getCorrection("PlanNeofluar");
        model = "44 03 41 (01)";
        break;
      case 14202:
        lensNA = 0.60;
        immersion = getImmersion("Air");
        correction = getCorrection("PlanApo");
        model = "44 06 40";
        break;
      case 14203:
        lensNA = 0.75;
        immersion = getImmersion("Air");
        correction = getCorrection("PlanApo");
        model = "44 06 49";
        break;
      case 14204:
        lensNA = 0.75;
        immersion = getImmersion("Air");
        correction = getCorrection("Fluar");
        model = "44 01 45";
        break;
      case 14101:
        lensNA = 0.30;
        immersion = getImmersion("Air");
        correction = getCorrection("PlanNeofluar");
        model = "44 03 30";
        break;
      case 14102:
        lensNA = 0.25;
        immersion = getImmersion("Air");
        model = "44 01 31";
        break;
      case 14103:
        lensNA = 0.25;
        immersion = getImmersion("Air");
        model = "44 00 31";
        break;
      case 14104:
        lensNA = 0.45;
        immersion = getImmersion("Air");
        correction = getCorrection("PlanApo");
        model = "44 06 39";
        break;
      case 14105:
        lensNA = 0.16;
        calibratedMagnification = 5.0;
        immersion = getImmersion("Air");
        correction = getCorrection("PlanApo");
        model = "44 06 20";
        break;
      case 18101:
        lensNA = 0.20;
        workingDistance = 3.33;
        calibratedMagnification = 4.0;
        immersion = getImmersion("Air");
        correction = getCorrection("PlanApo");
        break;
      case 18102:
        lensNA = 0.20;
        workingDistance = 2.883;
        calibratedMagnification = 2.46;
        immersion = getImmersion("Air");
        correction = getCorrection("PlanApo");
        break;
      case 18103:
        lensNA = 0.20;
        workingDistance = 2.883;
        calibratedMagnification = 4.0;
        immersion = getImmersion("Air");
        correction = getCorrection("PlanApo");
        break;
      case 18104:
        lensNA = 0.45;
        calibratedMagnification = 6.15;
        workingDistance = 2.883;
        immersion = getImmersion("Air");
        correction = getCorrection("PlanApo");
        break;
      case 18105:
        lensNA = 0.45;
        workingDistance = 2.883;
        immersion = getImmersion("Air");
        correction = getCorrection("PlanApo");
        break;
      case 18106:
        lensNA = 0.1;
        workingDistance = 24.00;
        calibratedMagnification = 1.0;
        immersion = getImmersion("Air");
        break;
      case 18201:
        lensNA = 0.55;
        workingDistance = 13.00;
        immersion = getImmersion("Air");
        break;
      case 18202:
        lensNA = 0.50;
        workingDistance = 13.00;
        immersion = getImmersion("Air");
        break;
      case 18204:
        lensNA = 0.45;
        workingDistance = 4.50;
        immersion = getImmersion("Air");
        model = "MUE01200/92777";
        break;
      case 18205:
        lensNA = 0.50;
        workingDistance = 2.10;
        immersion = getImmersion("Air");
        model = "MRH00200/93135";
        break;
      case 1:
        lensNA = 0.25;
        manufacturer = "Zeiss";
        calibratedMagnification = 10.0;
        immersion = getImmersion("Air");
        break;
      case 2:
        lensNA = 0.50;
        manufacturer = "Zeiss";
        calibratedMagnification = 25.0;
        immersion = getImmersion("Air");
        break;
      case 3:
        lensNA = 1.00;
        manufacturer = "Zeiss";
        calibratedMagnification = 50.0;
        immersion = getImmersion("Oil");
        break;
      case 4:
        lensNA = 1.25;
        manufacturer = "Zeiss";
        calibratedMagnification = 63.0;
        immersion = getImmersion("Oil");
        break;
      case 5:
        lensNA = 1.30;
        manufacturer = "Zeiss";
        calibratedMagnification = 100.0;
        immersion = getImmersion("Oil");
        break;
      case 6:
        lensNA = 1.30;
        calibratedMagnification = 100.0;
        correction = getCorrection("Neofluor");
        immersion = getImmersion("Oil");
        break;
      case 7:
        lensNA = 1.40;
        calibratedMagnification = 63.0;
        manufacturer = "Leitz";
        immersion = getImmersion("Oil");
        correction = getCorrection("PlanApo");
        break;
      case 8:
        lensNA = 1.20;
        calibratedMagnification = 63.0;
        immersion = getImmersion("Water");
        break;
      case 9:
        lensNA = 0.40;
        workingDistance = 0.30;
        calibratedMagnification = 10.0;
        manufacturer = "Olympus";
        immersion = getImmersion("Air");
        break;
      case 10:
        lensNA = 0.80;
        workingDistance = 0.30;
        manufacturer = "Olympus";
        calibratedMagnification = 20.0;
        immersion = getImmersion("Oil");
        break;
      case 11:
        lensNA = 1.30;
        calibratedMagnification = 40.0;
        manufacturer = "Olympus";
        workingDistance = 0.30;
        immersion = getImmersion("Oil");
        break;
      case 12:
        lensNA = 1.40;
        workingDistance = 0.30;
        manufacturer = "Olympus";
        calibratedMagnification = 60.0;
        immersion = getImmersion("Oil");
        break;
      case 13:
        lensNA = 1.40;
        workingDistance = 0.30;
        manufacturer = "Nikon";
        calibratedMagnification = 100.0;
        immersion = getImmersion("Oil");
        break;
    }

    String objectiveID = "Objective:" + lensID;
    store.setObjectiveID(objectiveID, 0, 0);
    for (int series=0; series<getSeriesCount(); series++) {
      store.setImageObjectiveSettingsID(objectiveID, series);
    }

    store.setObjectiveLensNA(lensNA, 0, 0);
    store.setObjectiveImmersion(immersion, 0, 0);
    store.setObjectiveCorrection(correction, 0, 0);
    store.setObjectiveManufacturer(manufacturer, 0, 0);
    store.setObjectiveModel(model, 0, 0);
    if (magnification > 0) {
      store.setObjectiveNominalMagnification(
        new PositiveInteger(magnification), 0, 0);
    }
    else {
      LOGGER.warn("Expected positive value for NominalMagnification; got {}",
        magnification);
    }
    if (calibratedMagnification != null) {
      store.setObjectiveCalibratedMagnification(calibratedMagnification, 0, 0);
    }
    if (workingDistance != null) {
      store.setObjectiveWorkingDistance(workingDistance * 1000, 0, 0);
    }
  }

  // -- Helper classes --

  /**
   * This private class structure holds the details for the extended header
   * @author Brian W. Loranger
   */
  private class DVExtHdrFields {

    private int offsetWithInts;

    /** Photosensor reading. Typically in mV. */
    public float photosensorReading;

    /** Time stamp in seconds since the experiment began. */
    public float timeStampSeconds;

    /** X stage coordinates. */
    public float stageXCoord;

    /** Y stage coordinates. */
    public float stageYCoord;

    /** Z stage coordinates. */
    public float stageZCoord;

    /** Minimum intensity */
    public float minInten;

    /** Maxiumum intensity. */
    public float maxInten;

    /** Exposure time in milliseconds. */
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

    /**
     * Helper function which overrides toString, printing out the values in
     * the header section.
     */
    public String toString() {
      StringBuffer sb = new StringBuffer();
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
        stageXCoord = in.readFloat();
        stageYCoord = in.readFloat();
        stageZCoord = in.readFloat();
        minInten = in.readFloat();
        maxInten = in.readFloat();
        in.skipBytes(4);
        expTime = in.readFloat() / 1000;

        ndFilter = in.readFloat() / 100;
        exWavelen = in.readFloat();
        emWavelen = in.readFloat();
        intenScaling = in.readFloat();
        energyConvFactor = in.readFloat();
      }
      catch (IOException e) {
        LOGGER.debug("Could not parse extended header", e);
      }
    }

  }

}
