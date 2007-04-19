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
    return (DataTools.bytesToShort(block, 0, 2, core.littleEndian[0]) ==
      LITTLE_ENDIAN);
  }

  /* @see loci.formats.IFormatReader#openBytes(int) */
  public byte[] openBytes(int no) throws FormatException, IOException {
    byte[] buf = new byte[core.sizeX[0] * core.sizeY[0] * bytesPerPixel];
    return openBytes(no, buf);
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    if (no < 0 || no >= core.imageCount[0]) {
      throw new FormatException("Invalid image number: " + no);
    }

    // read the image plane's pixel data
    long offset = header.length + extHeader.length;
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
      core.sizeY[0], 1, false, bytesPerPixel, core.littleEndian[0]);
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
    core.littleEndian[0] = endian == LITTLE_ENDIAN;
    RandomAccessStream hstream = new RandomAccessStream(header);
    hstream.order(core.littleEndian[0]);

    hstream.skipBytes(8);
    core.imageCount[0] = hstream.readInt();

    hstream.seek(92);
    int extSize = hstream.readInt();
    extHeader = new byte[extSize];
    in.read(extHeader);

    hstream.seek(0);
    core.sizeX[0] = hstream.readInt();
    core.sizeY[0] = hstream.readInt();

    Integer xSize = new Integer(core.sizeX[0]);
    Integer ySize = new Integer(core.sizeY[0]);
    addMeta("ImageWidth", xSize);
    addMeta("ImageHeight", ySize);
    addMeta("NumberOfImages", new Integer(hstream.readInt()));
    int filePixelType = hstream.readInt();
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
    addMeta("Sub-image starting point (X)", new Integer(hstream.readInt()));
    addMeta("Sub-image starting point (Y)", new Integer(hstream.readInt()));
    addMeta("Sub-image starting point (Z)", new Integer(hstream.readInt()));
    addMeta("Pixel sampling size (X)", new Integer(hstream.readInt()));
    addMeta("Pixel sampling size (Y)", new Integer(hstream.readInt()));
    addMeta("Pixel sampling size (Z)", new Integer(hstream.readInt()));
    
    float pixX = hstream.readFloat();
    float pixY = hstream.readFloat();
    float pixZ = hstream.readFloat();
    
    addMeta("X element length (in um)", new Float(pixX));
    addMeta("Y element length (in um)", new Float(pixY));
    addMeta("Z element length (in um)", new Float(pixZ));
    addMeta("X axis angle", new Float(hstream.readFloat()));
    addMeta("Y axis angle", new Float(hstream.readFloat()));
    addMeta("Z axis angle", new Float(hstream.readFloat()));
    addMeta("Column axis sequence", new Integer(hstream.readInt()));
    addMeta("Row axis sequence", new Integer(hstream.readInt()));
    addMeta("Section axis sequence", new Integer(hstream.readInt()));
    Float wave1Min = new Float(hstream.readFloat());
    addMeta("Wavelength 1 min. intensity", wave1Min);
    Float wave1Max = new Float(hstream.readFloat());
    addMeta("Wavelength 1 max. intensity", wave1Max);
    addMeta("Wavelength 1 mean intensity", new Float(hstream.readFloat()));
    addMeta("Space group number", new Integer(hstream.readInt()));

    hstream.seek(132);
    addMeta("Number of Sub-resolution sets", new Integer(hstream.readShort()));
    addMeta("Z axis reduction quotient", new Integer(hstream.readShort()));
    Float wave2Min = new Float(hstream.readFloat());
    addMeta("Wavelength 2 min. intensity", wave2Min);
    Float wave2Max = new Float(hstream.readFloat());
    addMeta("Wavelength 2 max. intensity", wave2Max);

    Float wave3Min = new Float(hstream.readFloat());
    addMeta("Wavelength 3 min. intensity", wave3Min);

    Float wave3Max = new Float(hstream.readFloat());
    addMeta("Wavelength 3 max. intensity", wave3Max);

    Float wave4Min = new Float(hstream.readFloat());
    addMeta("Wavelength 4 min. intensity", wave4Min);

    Float wave4Max = new Float(hstream.readFloat());
    addMeta("Wavelength 4 max. intensity", wave4Max);

    int type = hstream.readShort();
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
    addMeta("Lens ID Number", new Integer(hstream.readShort()));

    hstream.seek(172);
    Float wave5Min = new Float(hstream.readFloat());
    addMeta("Wavelength 5 min. intensity", wave5Min);

    Float wave5Max = new Float(hstream.readFloat());
    addMeta("Wavelength 5 max. intensity", wave5Max);

    core.sizeT[0] = hstream.readShort();
    addMeta("Number of timepoints", new Integer(core.sizeT[0]));

    int sequence = hstream.readShort();
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

    addMeta("X axis tilt angle", new Float(hstream.readFloat()));
    addMeta("Y axis tilt angle", new Float(hstream.readFloat()));
    addMeta("Z axis tilt angle", new Float(hstream.readFloat()));

    core.sizeC[0] = hstream.readShort();
    addMeta("Number of wavelengths", new Integer(core.sizeC[0]));
    core.sizeZ[0] = core.imageCount[0] / (core.sizeC[0] * core.sizeT[0]);
    addMeta("Number of focal planes", new Integer(core.sizeZ[0]));

    core.rgb[0] = false;
    core.interleaved[0] = false;

    short[] waves = new short[5];
    for (int i=0; i<waves.length; i++) waves[i] = hstream.readShort();

    addMeta("Wavelength 1 (in nm)", new Integer(waves[0]));
    addMeta("Wavelength 2 (in nm)", new Integer(waves[1]));
    addMeta("Wavelength 3 (in nm)", new Integer(waves[2]));
    addMeta("Wavelength 4 (in nm)", new Integer(waves[3]));
    addMeta("Wavelength 5 (in nm)", new Integer(waves[4]));
    addMeta("X origin (in um)", new Float(hstream.readFloat()));
    addMeta("Y origin (in um)", new Float(hstream.readFloat()));
    addMeta("Z origin (in um)", new Float(hstream.readFloat()));

    // The metadata store we're working with.
    MetadataStore store = getMetadataStore();

    String title = null;
    for (int i=10; i>=1; i--) {
      // Make sure that "null" characters are stripped out
      title = new String(header, 224 + 80*(i-1), 80).replaceAll("\0", "");
      addMeta("Title " + i, title);
    }

    // ----- The Extended Header data handler begins here ------

    status("Reading extended header");

    hstream.seek(128);
    numIntsPerSection = hstream.readShort();
    numFloatsPerSection = hstream.readShort();
    setOffsetInfo(sequence, core.sizeZ[0], core.sizeC[0], core.sizeT[0]);
    extHdrFields =
      new DVExtHdrFields[core.sizeZ[0]][core.sizeC[0]][core.sizeT[0]];

    store.setPixels(new Integer(core.sizeX[0]), new Integer(core.sizeY[0]),
      new Integer(core.sizeZ[0]), new Integer(core.sizeC[0]),
      new Integer(core.sizeT[0]), new Integer(core.pixelType[0]),
      new Boolean(!core.littleEndian[0]), core.currentOrder[0], null, null);

    store.setDimensions(new Float(pixX), new Float(pixY), new Float(pixZ),
      null, null, null);

    if (title == null) title = "";
    title = title.length() == 0 ? null : title;
    store.setImage(id, null, title, null);

    // Run through every timeslice, for each wavelength, for each z section
    // and fill in the Extended Header information array for that image
    for (int z = 0; z < core.sizeZ[0]; z++) {
      for (int t = 0; t < core.sizeT[0]; t++) {
        for (int w = 0; w < core.sizeC[0]; w++) {
          extHdrFields[z][w][t] = new DVExtHdrFields(getTotalOffset(z, w, t),
            numIntsPerSection, extHeader, core.littleEndian[0]);

          store.setPlaneInfo(z, w, t,
            new Float(extHdrFields[z][w][t].getTimeStampSeconds()),
            new Float(extHdrFields[z][w][t].getExpTime()), null);
        }
      }
    }

    status("Populating metadata");

    for (int w=0; w<core.sizeC[0]; w++) {
      store.setLogicalChannel(w, null,
        new Float(extHdrFields[0][w][0].getNdFilter()), new Integer(waves[w]), 
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
