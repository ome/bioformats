//
// DeltavisionReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
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
import java.text.*;
import java.util.Date;
import java.util.Vector;
import loci.common.*;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.IMinMaxStore;
import loci.formats.meta.MetadataStore;

/**
 * DeltavisionReader is the file format reader for Deltavision files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/DeltavisionReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/DeltavisionReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class DeltavisionReader extends FormatReader {

  // -- Constants --

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

  /**
   * The number of ints in each extended header section. These fields appear
   * to be all blank but need to be skipped to get to the floats afterwards
   */
  protected int numIntsPerSection;
  protected int numFloatsPerSection;

  /** Initialize an array of Extended Header Field structures. */
  protected DVExtHdrFields[][][] extHdrFields = null;

  private Float[] ndFilters;

  // -- Constructor --

  /** Constructs a new Deltavision reader. */
  public DeltavisionReader() {
    super("Deltavision", new String[] {"dv", "r3d", "r3d_d3d"});
    suffixSufficient = false;
    blockCheckLen = 98;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessStream) */
  public boolean isThisType(RandomAccessStream stream) throws IOException {
    if (!FormatTools.validStream(stream, blockCheckLen, false)) return false;
    stream.seek(96);
    int magic = stream.readShort() & 0xffff;
    return magic == 0xa0c0 || magic == 0xc0a0;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length, w, h);

    // read the image plane's pixel data
    int bytesPerPixel = FormatTools.getBytesPerPixel(getPixelType());
    long offset = HEADER_LENGTH + extSize;
    long planeOffset = (long) getSizeX() * getSizeY() * bytesPerPixel * no;
    in.seek(offset + planeOffset);
    readPlane(in, x, y, w, h, buf);

    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    extSize = wSize = zSize = tSize = 0;
    numIntsPerSection = numFloatsPerSection = 0;
    extHdrFields = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("DeltavisionReader.initFile(" + id + ")");
    super.initFile(id);

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());

    // --- read in the image header data ---

    status("Reading header");

    in = new RandomAccessStream(id);

    in.seek(96);
    in.order(true);

    boolean little = in.readShort() == LITTLE_ENDIAN;
    in.order(little);
    in.seek(0);

    int sizeX = in.readInt();
    int sizeY = in.readInt();
    int imageCount = in.readInt();
    int filePixelType = in.readInt();

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

    in.seek(160);

    int type = in.readShort();

    int lensID = in.readShort();

    in.seek(180);

    int rawSizeT = in.readShort();
    int sizeT = rawSizeT == 0 ? 1 : rawSizeT;

    int sequence = in.readShort();

    float xTiltAngle = in.readFloat();
    float yTiltAngle = in.readFloat();
    float zTiltAngle = in.readFloat();

    int rawSizeC = in.readShort();
    int sizeC = rawSizeC == 0 ? 1 : rawSizeC;

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

    long fp = in.getFilePointer();

    in.seek(76);

    float[] minWave = new float[5];
    float[] maxWave = new float[5];

    minWave[0] = in.readFloat();
    maxWave[0] = in.readFloat();

    float meanIntensity = in.readFloat();
    int spaceGroupNumber = in.readInt();

    extSize = in.readInt();

    in.seek(128);
    numIntsPerSection = in.readShort();
    numFloatsPerSection = in.readShort();

    short numSubResSets = in.readShort();
    short zAxisReductionQuotient = in.readShort();

    for (int i=1; i<=3; i++) {
      minWave[i] = in.readFloat();
      maxWave[i] = in.readFloat();
    }

    in.seek(172);

    minWave[4] = in.readFloat();
    maxWave[4] = in.readFloat();

    // --- compute some secondary values ---

    String imageType =
      type < IMAGE_TYPES.length ? IMAGE_TYPES[type] : "unknown";

    String imageSequence;
    switch (sequence) {
      case 0:
        imageSequence = "ZTW";
        break;
      case 1:
        imageSequence = "WZT";
        break;
      case 2:
        imageSequence = "ZWT";
        break;
      case 65536:
        imageSequence = "WZT";
        break;
      default:
        imageSequence = "ZTW";
    }

    int sizeZ = imageCount / (sizeC * sizeT);

    String imageDesc = title[0];
    if (imageDesc != null && imageDesc.length() == 0) imageDesc = null;

    // --- populate core metadata ---

    status("Populating core metadata");

    core[0].littleEndian = little;
    core[0].sizeX = sizeX;
    core[0].sizeY = sizeY;
    core[0].imageCount = imageCount;

    String pixel;
    switch (filePixelType) {
      case 0:
        pixel = "8 bit unsigned integer";
        core[0].pixelType = FormatTools.UINT8;
        break;
      case 1:
        pixel = "16 bit signed integer";
        core[0].pixelType = FormatTools.INT16;
        break;
      case 2:
        pixel = "32 bit floating point";
        core[0].pixelType = FormatTools.FLOAT;
        break;
      case 3:
        pixel = "16 bit complex";
        core[0].pixelType = FormatTools.INT16;
        break;
      case 4:
        pixel = "64 bit complex";
        core[0].pixelType = FormatTools.FLOAT;
        break;
      case 6:
        pixel = "16 bit unsigned integer";
        core[0].pixelType = FormatTools.UINT16;
        break;
      default:
        pixel = "unknown";
        core[0].pixelType = FormatTools.UINT8;
    }

    core[0].sizeT = sizeT;

    core[0].dimensionOrder = "XY" + imageSequence.replaceAll("W", "C");

    core[0].sizeC = sizeC;
    core[0].sizeZ = sizeZ;

    core[0].rgb = false;
    core[0].interleaved = false;
    core[0].metadataComplete = true;
    core[0].indexed = false;
    core[0].falseColor = false;

    // --- populate original metadata ---

    status("Populating original metadata");

    addMeta("ImageWidth", sizeX);
    addMeta("ImageHeight", sizeY);
    addMeta("NumberOfImages", imageCount);

    addMeta("PixelType", pixel);
    addMeta("Sub-image starting point (X)", subImageStartX);
    addMeta("Sub-image starting point (Y)", subImageStartY);
    addMeta("Sub-image starting point (Z)", subImageStartZ);
    addMeta("Pixel sampling size (X)", pixelSamplingX);
    addMeta("Pixel sampling size (Y)", pixelSamplingY);
    addMeta("Pixel sampling size (Z)", pixelSamplingZ);

    addMeta("X element length (in um)", pixX);
    addMeta("Y element length (in um)", pixY);
    addMeta("Z element length (in um)", pixZ);
    addMeta("X axis angle", xAxisAngle);
    addMeta("Y axis angle", yAxisAngle);
    addMeta("Z axis angle", zAxisAngle);
    addMeta("Column axis sequence", xAxisSeq);
    addMeta("Row axis sequence", yAxisSeq);
    addMeta("Section axis sequence", zAxisSeq);

    addMeta("Image Type", imageType);
    addMeta("Lens ID Number", lensID);

    addMeta("Number of timepoints", rawSizeT);

    addMeta("Image sequence", imageSequence);

    addMeta("X axis tilt angle", xTiltAngle);
    addMeta("Y axis tilt angle", yTiltAngle);
    addMeta("Z axis tilt angle", zTiltAngle);

    addMeta("Number of wavelengths", rawSizeC);
    addMeta("Number of focal planes", sizeZ);

    for (int i=0; i<waves.length; i++) {
      addMeta("Wavelength " + (i + 1) + " (in nm)", waves[i]);
    }

    addMeta("X origin (in um)", xOrigin);
    addMeta("Y origin (in um)", yOrigin);
    addMeta("Z origin (in um)", zOrigin);

    for (int i=0; i<title.length; i++) addMeta("Title " + (i + 1), title[i]);

    for (int i=0; i<minWave.length; i++) {
      addMeta("Wavelength " + (i + 1) + " min. intensity", minWave[i]);
      addMeta("Wavelength " + (i + 1) + " max. intensity", maxWave[i]);
    }

    addMeta("Wavelength 1 mean intensity", meanIntensity);
    addMeta("Space group number", spaceGroupNumber);

    addMeta("Number of Sub-resolution sets", numSubResSets);
    addMeta("Z axis reduction quotient", zAxisReductionQuotient);

    // --- populate OME metadata ---

    status("Populating OME metadata");

    MetadataTools.populatePixels(store, this, true);

    MetadataTools.setDefaultCreationDate(store, id, 0);

    // link Instrument and Image
    store.setInstrumentID("Instrument:0", 0);
    store.setImageInstrumentRef("Instrument:0", 0);

    String objectiveID = "Objective:" + lensID;
    store.setObjectiveID(objectiveID, 0, 0);
    store.setObjectiveSettingsObjective(objectiveID, 0);
    store.setObjectiveCorrection("Unknown", 0, 0);
    store.setObjectiveImmersion("Unknown", 0, 0);

    if (store instanceof IMinMaxStore) {
      IMinMaxStore minMaxStore = (IMinMaxStore) store;
      for (int i=0; i<minWave.length; i++) {
        minMaxStore.setChannelGlobalMinMax(0, minWave[i], maxWave[i], i);
      }
    }

    store.setDimensionsPhysicalSizeX(new Float(pixX), 0, 0);
    store.setDimensionsPhysicalSizeY(new Float(pixY), 0, 0);
    store.setDimensionsPhysicalSizeZ(new Float(pixZ), 0, 0);

    store.setImageName("", 0);

    store.setImageDescription(imageDesc, 0);

    // --- parse extended header ---

    // TODO - Refactor this section into those above, to maintain
    // the division between header I/O, core metadata population,
    // original metadata population, and OME metadata population.

    status("Reading extended header");

    in.seek(fp);

    setOffsetInfo(sequence, sizeZ, sizeC, sizeT);
    extHdrFields = new DVExtHdrFields[sizeZ][sizeC][sizeT];

    ndFilters = new Float[sizeC];

    // if matching log file exists, extract key/value pairs from it
    boolean logFound = parseLogFile(store);
    parseDeconvolutionLog(store);

    // Run through every image and fill in the
    // Extended Header information array for that image
    int offset = HEADER_LENGTH + numIntsPerSection * 4;
    for (int i=0; i<imageCount; i++) {
      int[] coords = getZCTCoords(i);
      int z = coords[0];
      int w = coords[1];
      int t = coords[2];

      // -- read in the extended header data --

      in.seek(offset + getTotalOffset(z, w, t));
      DVExtHdrFields hdr = new DVExtHdrFields(in);
      extHdrFields[z][w][t] = hdr;

      // -- record original metadata --

      // NB: It adds a little overhead to record the extended headers into the
      // original metadata table, but not as much as registering every header
      // field individually. With this approach it is still easy to
      // programmatically access any given extended header field.
      String prefix = "Extended header Z" + z + " W" + w + " T" + t;
      addMeta(prefix, hdr);
      //addMeta(prefix + " - Photosensor reading", hdr.photosensorReading);
      //addMeta(prefix + " - Time stamp (in s)", hdr.timeStampSeconds);
      //addMeta(prefix + " - X stage coordinates", hdr.stageXCoord);
      //addMeta(prefix + " - Y stage coordinates", hdr.stageYCoord);
      //addMeta(prefix + " - Z stage coordinates", hdr.stageZCoord);
      //addMeta(prefix + " - Minimum intensity", hdr.minInten);
      //addMeta(prefix + " - Maximum intensity", hdr.maxInten);
      //addMeta(prefix + " - Exposure time (in ms)", hdr.expTime);
      //addMeta(prefix + " - Neutral density value", hdr.ndFilter);
      //addMeta(prefix + " - Excitation filter wavelength", hdr.exWavelen);
      //addMeta(prefix + " - Emission filter wavelength", hdr.emWavelen);
      //addMeta(prefix + " - Intensity scaling factor", hdr.intenScaling);
      //addMeta(prefix + " - Energy conversion factor", hdr.energyConvFactor);

      // -- record OME metadata --

      // plane timing
      if (!logFound) {
        store.setPlaneTimingDeltaT(new Float(hdr.timeStampSeconds), 0, 0, i);
      }
      store.setPlaneTimingExposureTime(new Float(hdr.expTime), 0, 0, i);

      // stage position
      if (!logFound) {
        store.setStagePositionPositionX(new Float(hdr.stageXCoord), 0, 0, i);
        store.setStagePositionPositionY(new Float(hdr.stageYCoord), 0, 0, i);
        store.setStagePositionPositionZ(new Float(hdr.stageZCoord), 0, 0, i);
      }
    }

    for (int w=0; w<sizeC; w++) {
      DVExtHdrFields hdrC = extHdrFields[0][w][0];
      store.setLogicalChannelEmWave(new Integer(waves[w]), 0, w);
      store.setLogicalChannelExWave(new Integer((int) hdrC.exWavelen), 0, w);
      if (ndFilters[w] == null) ndFilters[w] = new Float(hdrC.ndFilter);
      store.setLogicalChannelNdFilter(ndFilters[w], 0, w);
    }
  }

  // -- Helper methods --

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

  /** Extract metadata from associated log file, if it exists. */
  private boolean parseLogFile(MetadataStore store) throws IOException {
    // see if log file exists
    String logFile = getCurrentFile() + ".log";
    if (!new Location(logFile).exists()) return false;

    RandomAccessStream s = new RandomAccessStream(logFile);

    String line, key, value = "", prefix = "";

    int currentImage = 0;

    while (s.getFilePointer() < s.length() - 1) {
      line = s.readLine().trim();
      int colon = line.indexOf(":");
      if (colon != -1) {
        if (line.startsWith("Created")) {
          key = "Created";
          value = line.substring(7).trim();
        }
        else {
          key = line.substring(0, colon).trim();
          value = line.substring(colon + 1).trim();
        }
        if (value.equals("") && !key.equals("")) prefix = key;
        addMeta(prefix + " " + key, value);

        // Objective properties
        if (key.equals("Objective")) {
          // assume first word is the manufacturer's name
          int space = value.indexOf(" ");
          if (space != -1) {
            String manufacturer = value.substring(0, space);
            String extra = value.substring(space + 1);

            String[] tokens = extra.split(",");

            store.setObjectiveManufacturer(manufacturer, 0, 0);

            String magnification =
              tokens[0].substring(0, tokens[0].indexOf("X"));
            String na = tokens[0].substring(tokens[0].indexOf("/") + 1);

            store.setObjectiveNominalMagnification(new Integer(magnification),
              0, 0);
            store.setObjectiveLensNA(new Float(na), 0, 0);
            store.setObjectiveCorrection(tokens[1], 0, 0);
            // TODO:  Token #2 is the microscope model name.
            if (tokens.length > 3) store.setObjectiveModel(tokens[3], 0, 0);
          }
        }
        else if (key.equals("Lens ID")) {
          store.setObjectiveID("Objective:" + value, 0, 0);
          store.setObjectiveSettingsObjective("Objective:" + value, 0);
        }
        //else if (key.equals("Aux Magn")) { }
        // Image properties
        else if (key.equals("Pixel Size")) {
          String[] pixelSizes = value.split(" ");
          Float x = new Float(pixelSizes[0].trim());
          Float y = new Float(pixelSizes[1].trim());
          Float z = new Float(pixelSizes[2].trim());

          store.setDimensionsPhysicalSizeX(x, 0, 0);
          store.setDimensionsPhysicalSizeY(y, 0, 0);
          store.setDimensionsPhysicalSizeZ(z, 0, 0);
        }
        else if (key.equals("Binning")) {
          for (int c=0; c<getSizeC(); c++) {
            store.setDetectorSettingsBinning(value, 0, c);

            store.setDetectorType("Unknown", 0, c);

            // link DetectorSettings to an actual Detector
            store.setDetectorID("Detector:" + c, 0, c);
            store.setDetectorSettingsDetector("Detector:" + c, 0, c);
          }
        }
        // Camera properties
        else if (key.equals("Type")) {
          store.setDetectorModel(value, 0, 0);
        }
        else if (key.equals("Gain")) {
          value = value.replaceAll("X", "");
          store.setDetectorSettingsGain(new Float(value), 0, 0);
        }
        else if (key.equals("Speed")) {
          value = value.replaceAll("KHz", "");
          float mhz = Float.parseFloat(value) / 1000;
          store.setDetectorSettingsReadOutRate(new Float(mhz), 0, 0);
        }
        else if (key.equals("Temp Setting")) {
          value = value.replaceAll("C", "").trim();
          store.setImagingEnvironmentTemperature(new Float(value), 0);
        }
        // Plane properties
        //else if (key.equals("Time")) { }
        else if (key.equals("Time Point")) {
          Float time = new Float(value.substring(0, value.indexOf(" ")));
          store.setPlaneTimingDeltaT(time, 0, 0, currentImage);
        }
        //else if (key.equals("Intensity")) { }
        //else if (key.equals("EX filter")) { }
        else if (key.equals("EM filter")) {
          int cIndex = getZCTCoords(currentImage)[1];
          store.setLogicalChannelName(value, 0, cIndex);
        }
        else if (key.equals("ND filter")) {
          try {
            float nd = Float.parseFloat(value.replaceAll("%", ""));
            nd = (float) Math.pow(10, -1 * nd);
            int cIndex = getZCTCoords(currentImage)[1];
            ndFilters[cIndex] = new Float(nd);
          }
          catch (NumberFormatException exc) { }
        }
        else if (key.equals("Stage coordinates")) {
          value = value.substring(1, value.length() - 1);
          String[] coords = value.split(",");
          Float x = new Float(coords[0].trim());
          Float y = new Float(coords[1].trim());
          Float z = new Float(coords[2].trim());

          store.setStagePositionPositionX(x, 0, 0, currentImage);
          store.setStagePositionPositionY(y, 0, 0, currentImage);
          store.setStagePositionPositionZ(z, 0, 0, currentImage);

          currentImage++;
        }
      }
      else if (line.startsWith("Image")) prefix = line;
      else if (line.startsWith("Created")) {
        SimpleDateFormat parse =
          new SimpleDateFormat("EEE MMM  d HH:mm:ss yyyy");
        Date date = parse.parse(line.substring(8).trim(), new ParsePosition(0));
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        store.setImageCreationDate(fmt.format(date), 0);
      }
    }

    s.close();
    return true;
  }

  /** Parse deconvolution output, if it exists. */
  private void parseDeconvolutionLog(MetadataStore store) throws IOException {
    int dot = getCurrentFile().lastIndexOf(".");
    String base = getCurrentFile().substring(0, dot);
    if (! new Location(base + "_log.txt").exists()) return;

    RandomAccessStream s = new RandomAccessStream(base + "_log.txt");

    boolean doStatistics = false;
    int cc = 0, tt = 0;
    String previousLine = null;

    while (s.getFilePointer() < s.length() - 1) {
      String line = s.readLine().trim();
      if (line == null || line.length() == 0) continue;

      if (doStatistics) {
        String[] keys = line.split("  ");
        Vector realKeys = new Vector();
        for (int i=0; i<keys.length; i++) {
          if (keys[i].trim().length() > 0) realKeys.add(keys[i].trim());
        }
        keys = (String[]) realKeys.toArray(new String[0]);

        s.readLine();

        line = s.readLine().trim();
        while (line.length() != 0) {
          String[] values = line.split(" ");
          Vector realValues = new Vector();
          for (int i=0; i<values.length; i++) {
            if (values[i].trim().length() > 0) {
              realValues.add(values[i].trim());
            }
          }
          values = (String[]) realValues.toArray(new String[0]);

          try {
            int zz = Integer.parseInt(values[0].trim()) - 1;
            int index = getIndex(zz, cc, tt);
            for (int i=1; i<keys.length; i++) {
              addMeta("Plane " + index + " " + keys[i], values[i]);
            }
          }
          catch (NumberFormatException e) { }
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
            addMeta(previousLine + " " +  key, value);
          }
          else addMeta(key, value);
        }
      }

      if (line.indexOf("correcting time point") != -1) {
        int index = line.indexOf("time point\t") + 11;
        if (index > 10) {
          String t = line.substring(index, line.indexOf(",", index));
          tt = Integer.parseInt(t) - 1;
          index = line.indexOf("wavelength\t") + 11;
          if (index > 10) {
            String c = line.substring(index, line.indexOf(".", index));
            cc = Integer.parseInt(c) - 1;
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
    addMeta("Wavelength " + (channel + 1) + " min. intensity", min);
    addMeta("Wavelength " + (channel + 1) + " max. intensity", max);
    if (store instanceof IMinMaxStore) {
      ((IMinMaxStore) store).setChannelGlobalMinMax(0, min, max, channel);
    }
  }

  // -- Helper classes --

  /**
   * This private class structure holds the details for the extended header
   * @author Brian W. Loranger
   */
  private class DVExtHdrFields {

    private int offsetWithInts;

    private float oDFilter;

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

    ///** Mean intesity. */
    //public float meanInten;

    /** Exposure time in milliseconds. */
    public float expTime;

    /** Neutral density value. */
    public float ndFilter;

    ///** Excitation filter number. */
    //public float exFilter;

    ///** Emission filter number. */
    //public float emFilter;

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
      //sb.append("\nmeanInten: ");
      //sb.append(meanInten);
      sb.append("\nexpTime: ");
      sb.append(expTime);
      sb.append("\nndFilter: ");
      sb.append(ndFilter);
      //sb.append("\nexFilter: ");
      //sb.append(exFilter);
      //sb.append("\nemFilter: ");
      //sb.append(emFilter);
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

    private DVExtHdrFields(RandomAccessStream in) {
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
        expTime = in.readFloat();

        ndFilter = in.readFloat();
        exWavelen = in.readFloat();
        emWavelen = in.readFloat();
        intenScaling = in.readFloat();
        energyConvFactor = in.readFloat();
      }
      catch (IOException e) {
        if (debug) LogTools.trace(e);
      }
    }

  }

}
