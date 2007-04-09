//
// DeltavisionReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.in;

import java.awt.image.BufferedImage;
import java.io.IOException;
import loci.formats.*;

/**
 * DeltavisionReader is the file format reader for Deltavision files.
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class DeltavisionReader extends FormatReader {

  // -- Constants --

  private static final short LITTLE_ENDIAN = -16224;

  // -- Fields --

  /** Current file. */
  protected RandomAccessStream in;

  /** Number of images in the current file. */
  protected int numImages;

  /** Flag indicating whether current file is little endian. */
  protected boolean little;

  /** Byte array containing basic image header data. */
  protected byte[] header;

  /** Byte array containing extended header data. */
  protected byte[] extHeader;

  /** Bytes per pixel. */
  private int bytesPerPixel;

  /** Offset where the ExtHdr starts. */
  protected int initExtHdrOffset = 1024;

  /** Size of one wave in the extended header. */
  protected int wSize;

  /** Size of one z section in the extended header. */
  protected int zSize;

  /** Size of one time element in the extended header. */
  protected int tSize;

  /**
   * the Number of ints in each extended header section. These fields appear
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

  // -- FormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */ 
  public boolean isThisType(byte[] block) {
    return (DataTools.bytesToShort(block, 0, 2, little) == LITTLE_ENDIAN);
  }

  /* @see loci.formats.IFormatReader#getImageCount() */ 
  public int getImageCount() throws FormatException, IOException {
    return numImages;
  }

  /* @see loci.formats.IFormatReader#isRGB() */
  public boolean isRGB() throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#isLittleEndian() */ 
  public boolean isLittleEndian() throws FormatException, IOException {
    return little;
  }

  /* @see loci.formats.IFormatReader#isInterleaved(int) */ 
  public boolean isInterleaved(int subC) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#openBytes(int) */ 
  public byte[] openBytes(int no)
    throws FormatException, IOException
  {
    byte[] buf = new byte[core.sizeX[0] * core.sizeY[0] * bytesPerPixel];
    return openBytes(no, buf);
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    if (no < 0 || no >= numImages) {
      throw new FormatException("Invalid image number: " + no);
    }

    // read the image plane's pixel data
    int offset = header.length + extHeader.length;
    offset += core.sizeX[0] * core.sizeY[0] * bytesPerPixel * no;

    in.seek(offset);
    in.read(buf);
    return buf;
  }

  /* @see loci.formats.IFormatReader#openImage(int) */ 
  public BufferedImage openImage(int no)
    throws FormatException, IOException
  {
    return ImageTools.makeImage(openBytes(no), core.sizeX[0], 
      core.sizeY[0], 1, false, bytesPerPixel, little);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws FormatException, IOException {
    if (fileOnly && in != null) in.close();
    else if (!fileOnly) close();
  }

  /* @see loci.formats.IFormatReader#close() */ 
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  /** Initializes the given Deltavision file. */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("DeltavisionReader.initFile(" + id + ")");
    super.initFile(id);

    in = new RandomAccessStream(id);

    status("Reading header");

    // read in the image header data
    header = new byte[1024];
    in.read(header);

    int endian = DataTools.bytesToShort(header, 96, 2, true);
    little = endian == LITTLE_ENDIAN;
    numImages = DataTools.bytesToInt(header, 8, 4, little);

    int extSize = DataTools.bytesToInt(header, 92, 4, little);
    extHeader = new byte[extSize];
    in.read(extHeader);

    core.sizeX[0] = DataTools.bytesToInt(header, 0, 4, little);
    core.sizeY[0] = DataTools.bytesToInt(header, 4, 4, little);

    Integer xSize = new Integer(core.sizeX[0]);
    Integer ySize = new Integer(core.sizeY[0]);
    addMeta("ImageWidth", xSize);
    addMeta("ImageHeight", ySize);
    addMeta("NumberOfImages", new Integer(DataTools.bytesToInt(header,
      8, 4, little)));
    int filePixelType = DataTools.bytesToInt(header, 12, 4, little);
    String pixel;

    switch (filePixelType) {
      case 0:
        pixel = "8 bit unsigned integer";
        core.pixelType[0] = FormatTools.UINT8;
        bytesPerPixel = 1;
        break;
      case 1:
        pixel = "16 bit signed integer";
        core.pixelType[0] = FormatTools.UINT16;
        bytesPerPixel = 2;
        break;
      case 2:
        pixel = "32 bit floating point";
        core.pixelType[0] = FormatTools.FLOAT;
        bytesPerPixel = 4;
        break;
      case 3:
        pixel = "32 bit complex";
        core.pixelType[0] = FormatTools.UINT32;
        bytesPerPixel = 4;
        break;
      case 4:
        pixel = "64 bit complex";
        core.pixelType[0] = FormatTools.FLOAT;
        bytesPerPixel = 8;
        break;
      case 6:
        pixel = "16 bit unsigned integer";
        core.pixelType[0] = FormatTools.UINT16;
        bytesPerPixel = 2;
        break;
      default:
        pixel = "unknown";
        core.pixelType[0] = FormatTools.UINT8;
        bytesPerPixel = 1;
    }

    addMeta("PixelType", pixel);
    addMeta("Sub-image starting point (X)", new Integer(
      DataTools.bytesToInt(header, 16, 4, little)));
    addMeta("Sub-image starting point (Y)", new Integer(
      DataTools.bytesToInt(header, 20, 4, little)));
    addMeta("Sub-image starting point (Z)", new Integer(
      DataTools.bytesToInt(header, 24, 4, little)));
    addMeta("Pixel sampling size (X)", new Integer(
      DataTools.bytesToInt(header, 28, 4, little)));
    addMeta("Pixel sampling size (Y)", new Integer(
      DataTools.bytesToInt(header, 32, 4, little)));
    addMeta("Pixel sampling size (Z)", new Integer(
      DataTools.bytesToInt(header, 36, 4, little)));
    addMeta("X element length (in um)", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 40, 4, little))));
    addMeta("Y element length (in um)", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 44, 4, little))));
    addMeta("Z element length (in um)", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 48, 4, little))));
    addMeta("X axis angle", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 52, 4, little))));
    addMeta("Y axis angle", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 56, 4, little))));
    addMeta("Z axis angle", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 60, 4, little))));
    addMeta("Column axis sequence", new Integer(
      DataTools.bytesToInt(header, 64, 4, little)));
    addMeta("Row axis sequence", new Integer(
      DataTools.bytesToInt(header, 68, 4, little)));
    addMeta("Section axis sequence", new Integer(
      DataTools.bytesToInt(header, 72, 4, little)));
    Float wave1Min = new Float(Float.intBitsToFloat(
        DataTools.bytesToInt(header, 76, 4, little)));
    addMeta("Wavelength 1 min. intensity", wave1Min);
    Float wave1Max = new Float(Float.intBitsToFloat(
        DataTools.bytesToInt(header, 80, 4, little)));
    addMeta("Wavelength 1 max. intensity", wave1Max);
    addMeta("Wavelength 1 mean intensity", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 84, 4, little))));
    addMeta("Space group number", new Integer(
      DataTools.bytesToInt(header, 88, 4, little)));
    addMeta("Number of Sub-resolution sets", new Integer(
      DataTools.bytesToInt(header, 132, 2, little)));
    addMeta("Z axis reduction quotient", new Integer(
      DataTools.bytesToInt(header, 134, 2, little)));
    Float wave2Min = new Float(
      Float.intBitsToFloat(DataTools.bytesToInt(header, 136, 4, little)));
    addMeta("Wavelength 2 min. intensity", wave2Min);
    Float wave2Max = new Float(
      Float.intBitsToFloat(DataTools.bytesToInt(header, 140, 4, little)));
    addMeta("Wavelength 2 max. intensity", wave2Max);

    Float wave3Min = new Float(
      Float.intBitsToFloat(DataTools.bytesToInt(header, 144, 4, little)));
    addMeta("Wavelength 3 min. intensity", wave3Min);

    Float wave3Max = new Float(
      Float.intBitsToFloat(DataTools.bytesToInt(header, 148, 4, little)));
    addMeta("Wavelength 3 max. intensity", wave3Max);

    Float wave4Min = new Float(
      Float.intBitsToFloat(DataTools.bytesToInt(header, 152, 4, little)));
    addMeta("Wavelength 4 min. intensity", wave4Min);

    Float wave4Max = new Float(
      Float.intBitsToFloat(DataTools.bytesToInt(header, 156, 4, little)));
    addMeta("Wavelength 4 max. intensity", wave4Max);

    int type = DataTools.bytesToShort(header, 160, 2, little);
    String imageType;
    switch (type) {
      case 0:
        imageType = "normal";
        break;
      case 1:
        imageType = "Tilt-series";
        break;
      case 2:
        imageType = "Stereo tilt-series";
        break;
      case 3:
        imageType = "Averaged images";
        break;
      case 4:
        imageType = "Averaged stereo pairs";
        break;
      default:
        imageType = "unknown";
    }

    addMeta("Image Type", imageType);
    addMeta("Lens ID Number", new Integer(DataTools.bytesToShort(
      header, 162, 2, little)));
    Float wave5Min = new Float(
      Float.intBitsToFloat(DataTools.bytesToInt(header, 172, 4, little)));
    addMeta("Wavelength 5 min. intensity", wave5Min);

    Float wave5Max = new Float(
      Float.intBitsToFloat(DataTools.bytesToInt(header, 176, 4, little)));
    addMeta("Wavelength 5 max. intensity", wave5Max);

    core.sizeT[0] = DataTools.bytesToShort(header, 180, 2, little);
    addMeta("Number of timepoints", new Integer(core.sizeT[0]));

    int sequence = DataTools.bytesToInt(header, 182, 4, little);
    String imageSequence;
    switch (sequence) {
      case 0:
        imageSequence = "ZTW"; core.currentOrder[0] = "XYZTC";
        break;
      case 1:
        imageSequence = "WZT"; core.currentOrder[0] = "XYCZT";
        break;
      case 2:
        imageSequence = "ZWT"; core.currentOrder[0] = "XYZCT";
        break;
      case 65536:
        imageSequence = "WZT"; core.currentOrder[0] = "XYCZT";
        break;
      default:
        imageSequence = "unknown"; core.currentOrder[0] = "XYZTC";
    }
    addMeta("Image sequence", imageSequence);

    addMeta("X axis tilt angle", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 184, 4, little))));
    addMeta("Y axis tilt angle", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 188, 4, little))));
    addMeta("Z axis tilt angle", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 192, 4, little))));

    core.sizeC[0] = DataTools.bytesToShort(header, 196, 2, little);
    addMeta("Number of wavelengths", new Integer(core.sizeC[0]));
    core.sizeZ[0] = numImages / (core.sizeC[0] * core.sizeT[0]);
    addMeta("Number of focal planes", new Integer(core.sizeZ[0]));

    addMeta("Wavelength 1 (in nm)", new Integer(DataTools.bytesToShort(
      header, 198, 2, little)));
    addMeta("Wavelength 2 (in nm)", new Integer(DataTools.bytesToShort(
      header, 200, 2, little)));
    addMeta("Wavelength 3 (in nm)", new Integer(DataTools.bytesToShort(
      header, 202, 2, little)));
    addMeta("Wavelength 4 (in nm)", new Integer(DataTools.bytesToShort(
      header, 204, 2, little)));
    addMeta("Wavelength 5 (in nm)", new Integer(DataTools.bytesToShort(
      header, 206, 2, little)));
    addMeta("X origin (in um)", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 208, 4, little))));
    addMeta("Y origin (in um)", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 212, 4, little))));
    addMeta("Z origin (in um)", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 216, 4, little))));

    // The metadata store we're working with.
    MetadataStore store = getMetadataStore();

    String title;
    for (int i=1; i<=10; i++) {
      // Make sure that "null" characters are stripped out
      title = new String(header, 224 + 80*(i-1), 80).replaceAll("\0", "");
      addMeta("Title " + i, title);
    }

    // ----- The Extended Header data handler begins here ------

    status("Reading extended header");

    numIntsPerSection = DataTools.bytesToInt(header, 128, 2, little);
    numFloatsPerSection = DataTools.bytesToInt(header, 130, 2, little);
    setOffsetInfo(sequence, core.sizeZ[0], core.sizeC[0], core.sizeT[0]);
    extHdrFields = 
      new DVExtHdrFields[core.sizeZ[0]][core.sizeC[0]][core.sizeT[0]];

    store.setPixels(new Integer(core.sizeX[0]), new Integer(core.sizeY[0]), 
      new Integer(core.sizeZ[0]), new Integer(core.sizeC[0]), 
      new Integer(core.sizeT[0]), new Integer(core.pixelType[0]),
      new Boolean(!little), core.currentOrder[0], null, null);

    store.setDimensions(
      (Float) getMeta("X element length (in um)"),
      (Float) getMeta("Y element length (in um)"),
      (Float) getMeta("Z element length (in um)"),
      null, null, null);

    String description = (String) getMeta("Title 1");
    if (description == null) description = "";
    description = description.length() == 0 ? null : description;
    store.setImage(id, null, description, null);

    // Run through every timeslice, for each wavelength, for each z section
    // and fill in the Extended Header information array for that image
    for (int z = 0; z < core.sizeZ[0]; z++) {
      for (int t = 0; t < core.sizeT[0]; t++) {
        for (int w = 0; w < core.sizeC[0]; w++) {
          extHdrFields[z][w][t] = new DVExtHdrFields(getTotalOffset(z, w, t),
            numIntsPerSection, extHeader, little);

          store.setPlaneInfo(z, w, t,
            new Float(extHdrFields[z][w][t].getTimeStampSeconds()),
            new Float(extHdrFields[z][w][t].getExpTime()), null);
        }
      }
    }

    status("Populating metadata");

    for (int w=0; w<core.sizeC[0]; w++) {
      store.setLogicalChannel(w, null,
        new Float(extHdrFields[0][w][0].getNdFilter()),
        (Integer) getMeta("Wavelength " + (w+1) + " (in nm)"),
        new Integer((int) extHdrFields[0][w][0].getExFilter()),
        "Monochrome", "Wide-field", null);
    }

    store.setStageLabel("ome",
      new Float(extHdrFields[0][0][0].getStageXCoord()),
      new Float(extHdrFields[0][0][0].getStageYCoord()),
      new Float(extHdrFields[0][0][0].getStageZCoord()), null);

    if (core.sizeC[0] > 0) {
      store.setChannelGlobalMinMax(0, new Double(wave1Min.floatValue()),
        new Double(wave1Max.floatValue()), null);
    }
    if (core.sizeC[0] > 1) {
      store.setChannelGlobalMinMax(1, new Double(wave2Min.floatValue()),
        new Double(wave2Max.floatValue()), null);
    }
    if (core.sizeC[0] > 2) {
      store.setChannelGlobalMinMax(2, new Double(wave3Min.floatValue()),
        new Double(wave3Max.floatValue()), null);
    }
    if (core.sizeC[0] > 3) {
      store.setChannelGlobalMinMax(3, new Double(wave4Min.floatValue()),
        new Double(wave4Max.floatValue()), null);
    }
    if (core.sizeC[0] > 4) {
      store.setChannelGlobalMinMax(4, new Double(wave5Min.floatValue()),
        new Double(wave5Max.floatValue()), null);
    }

    store.setDefaultDisplaySettings(null);
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
  public int getTotalOffset(int currentZ, int currentW, int currentT) {
    return (zSize * currentZ) + (wSize * currentW) + (tSize * currentT);
  }

  /**
   * This method returns the a plane number from when given a Z, W
   * and T offsets.
   * @param currentZ
   * @param currentW
   * @param currentT
   */
  public int getPlaneNumber(int currentZ, int currentW, int currentT) {
    int smallOffset = (numIntsPerSection + numFloatsPerSection) * 4;
    return getTotalOffset(currentZ, currentW, currentT) / smallOffset;
  }

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
      String s = new String();

      s += "photosensorReading: " + photosensorReading + "\n";
      s += "timeStampSeconds: " + timeStampSeconds + "\n";
      s += "stageXCoord: " + stageXCoord + "\n";
      s += "stageYCoord: " + stageYCoord + "\n";
      s += "stageZCoord: " + stageZCoord + "\n";
      s += "minInten: " + minInten + "\n";
      s += "maxInten: " + maxInten + "\n";
      s += "meanInten: " + meanInten + "\n";
      s += "expTime: " + expTime + "\n";
      s += "ndFilter: " + ndFilter + "\n";
      s += "exFilter: " + exFilter + "\n";
      s += "emFilter: " + emFilter + "\n";
      s += "exWavelen: " + exWavelen + "\n";
      s += "emWavelen: " + emWavelen + "\n";
      s += "intenScaling: " + intenScaling + "\n";
      s += "energyConvFactor: " + energyConvFactor + "\n";

      return s;
    }

    /**
     * Given the starting offset of a specific entry in the extended header
     * this method will go through each element in the entry and fill each
     * element's variable with its extended header value.
     * @param startingOffset
     * @param numIntsPerSection
     * @param extHeader
     * @param little
     */
    protected DVExtHdrFields(int startingOffset, int numIntsPerSection,
      byte[] extHeader, boolean little)
    {
      // skip over the int values that have nothing in them
      offsetWithInts = startingOffset + (numIntsPerSection * 4);

      // DV files store the ND (neuatral density) Filter (normally expressed as
      // a %T (transmittance)) as an OD (optical density) rating.
      // To convert from one to the other the formula is %T = 10^(-OD) X 100.
      oDFilter = Float.intBitsToFloat(
        DataTools.bytesToInt(extHeader, offsetWithInts + 36, 4, little));

      // fill in the extended header information for the floats
      photosensorReading =
        Float.intBitsToFloat(
          DataTools.bytesToInt(extHeader, offsetWithInts, 4, little));
      timeStampSeconds =
        Float.intBitsToFloat(
          DataTools.bytesToInt(extHeader, offsetWithInts + 4, 4, little));
      stageXCoord =
        Float.intBitsToFloat(
          DataTools.bytesToInt(extHeader, offsetWithInts + 8, 4, little));
      stageYCoord =
        Float.intBitsToFloat(
          DataTools.bytesToInt(extHeader, offsetWithInts + 12, 4, little));
      stageZCoord =
        Float.intBitsToFloat(
          DataTools.bytesToInt(extHeader, offsetWithInts + 16, 4, little));
      minInten =
        Float.intBitsToFloat(
          DataTools.bytesToInt(extHeader, offsetWithInts + 20, 4, little));
      maxInten =
        Float.intBitsToFloat(
          DataTools.bytesToInt(extHeader, offsetWithInts + 24, 4, little));
      meanInten =
        Float.intBitsToFloat(
          DataTools.bytesToInt(extHeader, offsetWithInts + 28, 4, little));
      expTime =
        Float.intBitsToFloat(
          DataTools.bytesToInt(extHeader, offsetWithInts + 32, 4, little));
      ndFilter = (float) Math.pow(10.0, -oDFilter);
      exFilter =
        Float.intBitsToFloat(
          DataTools.bytesToInt(extHeader, offsetWithInts + 40, 4, little));
      emFilter =
        Float.intBitsToFloat(
          DataTools.bytesToInt(extHeader, offsetWithInts + 44, 4, little));
      exWavelen =
        Float.intBitsToFloat(
          DataTools.bytesToInt(extHeader, offsetWithInts + 48, 4, little));
      emWavelen =
        Float.intBitsToFloat(
          DataTools.bytesToInt(extHeader, offsetWithInts + 52, 4, little));
      intenScaling =
        Float.intBitsToFloat(
          DataTools.bytesToInt(extHeader, offsetWithInts + 56, 4, little));
      energyConvFactor =
        Float.intBitsToFloat(
          DataTools.bytesToInt(extHeader, offsetWithInts + 60, 4, little));
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
