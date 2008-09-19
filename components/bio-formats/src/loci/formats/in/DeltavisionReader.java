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
import java.util.Vector;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
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

  // -- Constructor --

  /** Constructs a new Deltavision reader. */
  public DeltavisionReader() {
    super("Deltavision", new String[] {"dv", "r3d", "r3d_d3d"});
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessStream) */
  public boolean isThisType(RandomAccessStream stream) throws IOException {
    return false;
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
    DataTools.readPlane(in, x, y, w, h, this, buf);

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

    in = new RandomAccessStream(id);

    status("Reading header");

    // read in the image header data
    in.seek(96);
    in.order(true);
    core[0].littleEndian = in.readShort() == LITTLE_ENDIAN;

    in.order(isLittleEndian());
    in.seek(0);

    core[0].sizeX = in.readInt();
    core[0].sizeY = in.readInt();
    core[0].imageCount = in.readInt();
    int filePixelType = in.readInt();

    addMeta("ImageWidth", getSizeX());
    addMeta("ImageHeight", getSizeY());
    addMeta("NumberOfImages", getImageCount());
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

    addMeta("PixelType", pixel);
    addMeta("Sub-image starting point (X)", in.readInt());
    addMeta("Sub-image starting point (Y)", in.readInt());
    addMeta("Sub-image starting point (Z)", in.readInt());
    addMeta("Pixel sampling size (X)", in.readInt());
    addMeta("Pixel sampling size (Y)", in.readInt());
    addMeta("Pixel sampling size (Z)", in.readInt());

    float pixX = in.readFloat();
    float pixY = in.readFloat();
    float pixZ = in.readFloat();

    addMeta("X element length (in um)", pixX);
    addMeta("Y element length (in um)", pixY);
    addMeta("Z element length (in um)", pixZ);
    addMeta("X axis angle", in.readFloat());
    addMeta("Y axis angle", in.readFloat());
    addMeta("Z axis angle", in.readFloat());
    addMeta("Column axis sequence", in.readInt());
    addMeta("Row axis sequence", in.readInt());
    addMeta("Section axis sequence", in.readInt());
    addMeta("Wavelength 1 min. intensity", in.readFloat());
    addMeta("Wavelength 1 max. intensity", in.readFloat());
    addMeta("Wavelength 1 mean intensity", in.readFloat());
    addMeta("Space group number", in.readInt());

    extSize = in.readInt();

    in.seek(128);
    numIntsPerSection = in.readShort();
    numFloatsPerSection = in.readShort();

    addMeta("Number of Sub-resolution sets", in.readShort());
    addMeta("Z axis reduction quotient", in.readShort());
    addMeta("Wavelength 2 min. intensity", in.readFloat());
    addMeta("Wavelength 2 max. intensity", in.readFloat());
    addMeta("Wavelength 3 min. intensity", in.readFloat());
    addMeta("Wavelength 3 max. intensity", in.readFloat());
    addMeta("Wavelength 4 min. intensity", in.readFloat());
    addMeta("Wavelength 4 max. intensity", in.readFloat());

    int type = in.readShort();
    String imageType =
      type < IMAGE_TYPES.length ? IMAGE_TYPES[type] : "unknown";

    addMeta("Image Type", imageType);
    int lensID = in.readShort();
    addMeta("Lens ID Number", lensID);

    in.seek(172);
    addMeta("Wavelength 5 min. intensity", in.readFloat());
    addMeta("Wavelength 5 max. intensity", in.readFloat());

    core[0].sizeT = in.readShort();
    addMeta("Number of timepoints", getSizeT());

    int sequence = in.readShort();
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
    core[0].dimensionOrder = "XY" + imageSequence.replaceAll("W", "C");
    addMeta("Image sequence", imageSequence);

    addMeta("X axis tilt angle", in.readFloat());
    addMeta("Y axis tilt angle", in.readFloat());
    addMeta("Z axis tilt angle", in.readFloat());

    core[0].sizeC = in.readShort();
    addMeta("Number of wavelengths", getSizeC());
    if (getSizeC() == 0) core[0].sizeC = 1;
    if (getSizeT() == 0) core[0].sizeT = 1;
    core[0].sizeZ = getImageCount() / (getSizeC() * getSizeT());
    addMeta("Number of focal planes", getSizeZ());

    core[0].rgb = false;
    core[0].interleaved = false;
    core[0].metadataComplete = true;
    core[0].indexed = false;
    core[0].falseColor = false;

    short[] waves = new short[5];
    for (int i=0; i<waves.length; i++) {
      waves[i] = in.readShort();
      addMeta("Wavelength " + (i + 1) + " (in nm)", waves[i]);
    }

    addMeta("X origin (in um)", in.readFloat());
    addMeta("Y origin (in um)", in.readFloat());
    addMeta("Z origin (in um)", in.readFloat());

    // The metadata store we're working with.
    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    store.setImageName("", 0);
    MetadataTools.setDefaultCreationDate(store, id, 0);
    store.setObjectiveID(String.valueOf(lensID), 0, 0);

    in.skipBytes(4);

    String title = null;
    for (int i=1; i<=10; i++) {
      // Make sure that "null" characters are stripped out
      title = in.readString(80).replaceAll("\0", "");
      addMeta("Title " + i, title);
    }

    // ----- The Extended Header data handler begins here ------

    status("Reading extended header");

    setOffsetInfo(sequence, getSizeZ(), getSizeC(), getSizeT());
    extHdrFields = new DVExtHdrFields[getSizeZ()][getSizeC()][getSizeT()];

    MetadataTools.populatePixels(store, this);

    store.setDimensionsPhysicalSizeX(new Float(pixX), 0, 0);
    store.setDimensionsPhysicalSizeY(new Float(pixY), 0, 0);
    store.setDimensionsPhysicalSizeZ(new Float(pixZ), 0, 0);

    if (title == null) title = "";
    title = title.length() == 0 ? null : title;
    store.setImageDescription(title, 0);

    // Run through every image and fill in the
    // Extended Header information array for that image
    int offset = HEADER_LENGTH + numIntsPerSection * 4;
    for (int i=0; i<getImageCount(); i++) {
      int[] coords = getZCTCoords(i);
      int z = coords[0];
      int w = coords[1];
      int t = coords[2];
      in.seek(offset + getTotalOffset(z, w, t));
      extHdrFields[z][w][t] = new DVExtHdrFields(in);

      // plane timing
      store.setPlaneTimingDeltaT(
        new Float(extHdrFields[z][w][t].getTimeStampSeconds()), 0, 0, i);
      store.setPlaneTimingExposureTime(
        new Float(extHdrFields[z][w][t].getExpTime()), 0, 0, i);

      // stage position
      store.setStagePositionPositionX(
        new Float(extHdrFields[z][w][t].getStageXCoord()), 0, 0, i);
      store.setStagePositionPositionY(
        new Float(extHdrFields[z][w][t].getStageYCoord()), 0, 0, i);
      store.setStagePositionPositionZ(
        new Float(extHdrFields[z][w][t].getStageZCoord()), 0, 0, i);

      if (z == 0 && t == 0) {
        store.setLogicalChannelEmWave(new Integer(waves[w]), 0, w);
        store.setLogicalChannelExWave(
          new Integer((int) extHdrFields[0][w][0].getExFilter()), 0, w);
        store.setLogicalChannelNdFilter(
          new Float(extHdrFields[0][w][0].getNdFilter()), 0, w);
      }
    }

    status("Populating metadata");

    // if matching log file exists, extract key/value pairs from it
    parseLogFile(store);
    parseDeconvolutionLog(store);
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
  private void parseLogFile(MetadataStore store) throws IOException {
    // see if log file exists
    String logFile = getCurrentFile() + ".log";
    if (!new Location(logFile).exists()) return;

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
          String manufacturer = value.substring(0, value.indexOf(" "));
          String model = value.substring(value.indexOf(" ") + 1);

          store.setObjectiveManufacturer(manufacturer, 0, 0);
          store.setObjectiveModel(model, 0, 0);
        }
        else if (key.equals("Lens ID")) {
          store.setObjectiveID(value, 0, 0);
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
        // Camera properties
        else if (key.equals("Type")) {
          store.setDetectorModel(value, 0, 0);
        }
        else if (key.equals("Gain")) {
          value = value.replaceAll("X", "");
          store.setDetectorGain(new Float(value), 0, 0);
        }
        //else if (key.equals("Speed")) { }
        //else if (key.equals("Temp Setting")) { }
        // Plane properties
        //else if (key.equals("Time")) { }
        else if (key.equals("Time Point")) {
          Float time = new Float(value.substring(0, value.indexOf(" ")));
          store.setPlaneTimingDeltaT(time, 0, 0, currentImage);
        }
        //else if (key.equals("Intensity")) { }
        else if (key.equals("Exposure time")) {
          Float time = new Float(value.substring(0, value.indexOf(" ")));
          store.setPlaneTimingExposureTime(time, 0, 0, currentImage);
        }
        //else if (key.equals("EX filter")) { }
        //else if (key.equals("EM filter")) { }
        else if (key.equals("ND filter")) {
          try {
            Float nd = new Float(value);
            int cIndex = getZCTCoords(currentImage)[1];
            store.setLogicalChannelNdFilter(nd, 0, cIndex);
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
    }

    s.close();
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

          int zz = Integer.parseInt(values[0].trim()) - 1;
          int index = getIndex(zz, cc, tt);
          for (int i=1; i<keys.length; i++) {
            addMeta("Plane " + index + " " + keys[i], values[i]);
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

          if (previousLine.endsWith("Deconvolution Results:") ||
            previousLine.endsWith("open OTF"))
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

  // -- Helper classes --

  /**
   * This private class structure holds the details for the extended header
   * @author Brian W. Loranger
   */
  private class DVExtHdrFields {

    private int offsetWithInts;

    private float oDFilter;

    /** Photosensor reading. Typically in mV. */
    private float photosensorReading;

    /** Time stamp in seconds since the experiment began. */
    private float timeStampSeconds;

    /** X stage coordinates. */
    private float stageXCoord;

    /** Y stage coordinates. */
    private float stageYCoord;

    /** Z stage coordinates. */
    private float stageZCoord;

    /** Minimum intensity */
    private float minInten;

    /** Maxiumum intensity. */
    private float maxInten;

    /** Mean intesity. */
    private float meanInten;

    /** Exposure time in seconds. */
    private float expTime;

    /** Neutral density value. */
    private float ndFilter;

    /** Excitation filter number. */
    private float exFilter;

    /** Emiision filter number. */
    private float emFilter;

    /** Excitation filter wavelength. */
    private float exWavelen;

    /** Emission filter wavelength. */
    private float emWavelen;

    /** Intensity scaling factor. Usually 1. */
    private float intenScaling;

    /** Energy conversion factor. Usually 1. */
    private float energyConvFactor;

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
      sb.append("\nmeanInten: ");
      sb.append(meanInten);
      sb.append("\nexpTime: ");
      sb.append(expTime);
      sb.append("\nndFilter: ");
      sb.append(ndFilter);
      sb.append("\nexFilter: ");
      sb.append(exFilter);
      sb.append("\nemFilter: ");
      sb.append(emFilter);
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

    protected DVExtHdrFields(RandomAccessStream in) {
      try {
        photosensorReading = in.readFloat();
        timeStampSeconds = in.readFloat();
        stageXCoord = in.readFloat();
        stageYCoord = in.readFloat();
        stageZCoord = in.readFloat();
        minInten = in.readFloat();
        maxInten = in.readFloat();
        expTime = in.readFloat();
        in.skipBytes(4);

        // DV files store the ND (neuatral density) Filter
        // (normally expressed as a %T (transmittance)) as an OD
        // (optical density) rating.
        // To convert from one to the other the formula is %T = 10^(-OD) X 100.
        ndFilter = (float) Math.pow(10.0, -1 * in.readFloat());
        exFilter = in.readFloat();
        emFilter = in.readFloat();
        exWavelen = in.readFloat();
        emWavelen = in.readFloat();
        intenScaling = in.readFloat();
        energyConvFactor = in.readFloat();
      }
      catch (IOException e) {
        if (debug) LogTools.trace(e);
      }
    }

    /** Various getters for the Extended header fields. */
    public float getPhotosensorReading() { return photosensorReading; }
    public float getTimeStampSeconds() { return timeStampSeconds; }
    public float getStageXCoord() { return stageXCoord; }
    public float getStageYCoord() { return stageYCoord; }
    public float getStageZCoord() { return stageZCoord; }
    public float getMinInten() { return minInten; }
    public float getMaxInten() { return maxInten; }
    public float getMeanInten() { return meanInten; }
    public float getExpTime() { return expTime; }
    public float getNdFilter() { return ndFilter; }
    public float getExFilter() { return exFilter; }
    public float getEmFilter() { return emFilter; }
    public float getExWavelen() { return exWavelen; }
    public float getEmWavelen() { return emWavelen; }
    public float getIntenScaling() { return intenScaling; }

  }

}
