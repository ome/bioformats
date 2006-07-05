//
// DeltavisionReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

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

package loci.formats;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * DeltavisionReader is the file format reader for Deltavision files.
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
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

  /** Image width. */
  private int width;

  /** Image height. */
  private int height;

  /** Bytes per pixel. */
  private int bytesPerPixel;

  /** Offset where the ExtHdr starts */
  protected int initExtHdrOffset = 1024;

  /** Size of one wave, z section, or time element in the extended header */
  protected int wSize;
  protected int zSize;
  protected int tSize;

  protected int numT;
  protected int numW;
  protected int numZ;
  /** the Number of ints in each extended header section. These fields appear
   * to be all blank but need to be skipped to get to the floats afterwards
   */
  protected int numIntsPerSection;
  protected int numFloatsPerSection;

  /** Initialize an array of Extended Header Field structures */
  DVExtHdrFields[][][] ExtHdrFields = null;

  // -- Constructor --

  /** Constructs a new Deltavision reader. */
  public DeltavisionReader() {
    super("Deltavision", new String[] {"dv", "r3d", "r3d_d3d"});
  }

  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for a Deltavision file. */
  public boolean isThisType(byte[] block) {
    return (DataTools.bytesToShort(block, 0, 2, little) == LITTLE_ENDIAN);
  }

  /** Determines the number of images in the given Deltavision file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return numImages;
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    return false;
  }

  /** Returns the number of channels in the file. */
  public int getChannelCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return ((Integer) metadata.get("Number of wavelengths")).intValue();
  }

  /** Obtains the specified image from the given file as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);

    if (no < 0 || no >= numImages) {
      throw new FormatException("Invalid image number: " + no);
    }

    // read the image plane's pixel data

    int offset = header.length + extHeader.length;
    offset += width * height * bytesPerPixel * no;

    int channels = 1;
    int numSamples = (int) (width * height);
    byte[] rawData = new byte[width * height * bytesPerPixel];

    in.seek(offset);

    in.read(rawData);
    return rawData;
  }

  /** Obtains the specified image from the given Deltavision file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    return ImageTools.makeImage(openBytes(id, no), width, height, 1,
      false, bytesPerPixel, little);
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  /** Initializes the given Deltavision file. */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    in = new RandomAccessStream(id);

    // read in the image header data
    header = new byte[1024];
    in.read(header);

    int endian = DataTools.bytesToShort(header, 96, 2, true);
    little = endian == LITTLE_ENDIAN;
    numImages = DataTools.bytesToInt(header, 8, 4, little);

    int extSize = DataTools.bytesToInt(header, 92, 4, little);
    extHeader = new byte[extSize];
    in.read(extHeader);

    width = DataTools.bytesToInt(header, 0, 4, little);
    height = DataTools.bytesToInt(header, 4, 4, little);

    Integer sizeX = new Integer(width);
    Integer sizeY = new Integer(height);
    metadata.put("ImageWidth", sizeX);
    metadata.put("ImageHeight", sizeY);
    metadata.put("NumberOfImages", new Integer(DataTools.bytesToInt(header,
      8, 4, little)));
    int pixelType = DataTools.bytesToInt(header, 12, 4, little);
    String pixel;
    String omePixel;

    switch (pixelType) {
      case 0: pixel = "8 bit unsigned integer"; omePixel = "Uint8";
              bytesPerPixel = 1;
              break;
      case 1: pixel = "16 bit signed integer"; omePixel = "int16";
              bytesPerPixel = 2;
              break;
      case 2: pixel = "32 bit floating point"; omePixel = "float";
              bytesPerPixel = 4;
              break;
      case 3: pixel = "32 bit complex"; omePixel = "Uint32";
              bytesPerPixel = 4;
              break;
      case 4: pixel = "64 bit complex"; omePixel = "float";
              bytesPerPixel = 8;
              break;
      case 6: pixel = "16 bit unsigned integer"; omePixel = "Uint16";
              bytesPerPixel = 2;
              break;
      default: pixel = "unknown"; omePixel = "Uint8"; bytesPerPixel = 1;
    }

    if (ome != null) {
      OMETools.setPixels(ome, sizeX, sizeY, null, null, null, omePixel,
        new Boolean(!little), null);
    }

    metadata.put("PixelType", pixel);
    metadata.put("Sub-image starting point (X)", new Integer(
      DataTools.bytesToInt(header, 16, 4, little)));
    metadata.put("Sub-image starting point (Y)", new Integer(
      DataTools.bytesToInt(header, 20, 4, little)));
    metadata.put("Sub-image starting point (Z)", new Integer(
      DataTools.bytesToInt(header, 24, 4, little)));
    metadata.put("Pixel sampling size (X)", new Integer(
      DataTools.bytesToInt(header, 28, 4, little)));
    metadata.put("Pixel sampling size (Y)", new Integer(
      DataTools.bytesToInt(header, 32, 4, little)));
    metadata.put("Pixel sampling size (Z)", new Integer(
      DataTools.bytesToInt(header, 36, 4, little)));
    metadata.put("X element length (in um)", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 40, 4, little))));
    metadata.put("Y element length (in um)", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 44, 4, little))));
    metadata.put("Z element length (in um)", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 48, 4, little))));
    metadata.put("X axis angle", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 52, 4, little))));
    metadata.put("Y axis angle", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 56, 4, little))));
    metadata.put("Z axis angle", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 60, 4, little))));
    metadata.put("Column axis sequence", new Integer(
      DataTools.bytesToInt(header, 64, 4, little)));
    metadata.put("Row axis sequence", new Integer(
      DataTools.bytesToInt(header, 68, 4, little)));
    metadata.put("Section axis sequence", new Integer(
      DataTools.bytesToInt(header, 72, 4, little)));
    metadata.put("Wavelength 1 min. intensity", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 76, 4, little))));
    metadata.put("Wavelength 1 max. intensity", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 80, 4, little))));
    metadata.put("Wavelength 1 mean intensity", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 84, 4, little))));
    metadata.put("Space group number", new Integer(
      DataTools.bytesToInt(header, 88, 4, little)));
    metadata.put("Number of Sub-resolution sets", new Integer(
      DataTools.bytesToInt(header, 132, 2, little)));
    metadata.put("Z axis reduction quotient", new Integer(
      DataTools.bytesToInt(header, 134, 2, little)));
    metadata.put("Wavelength 2 min. intensity", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 136, 4, little))));
    metadata.put("Wavelength 2 max. intensity", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 140, 4, little))));
    metadata.put("Wavelength 3 min. intensity", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 144, 4, little))));
    metadata.put("Wavelength 3 max. intensity", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 148, 4, little))));
    metadata.put("Wavelength 4 min. intensity", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 152, 4, little))));
    metadata.put("Wavelength 4 max. intensity", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 156, 4, little))));

    int type = DataTools.bytesToShort(header, 160, 2, little);
    String imageType;
    switch (type) {
      case 0: imageType = "normal"; break;
      case 1: imageType = "Tilt-series"; break;
      case 2: imageType = "Stereo tilt-series"; break;
      case 3: imageType = "Averaged images"; break;
      case 4: imageType = "Averaged stereo pairs"; break;
      default: imageType = "unknown";
    }

    metadata.put("Image Type", imageType);
    metadata.put("Lens ID Number", new Integer(DataTools.bytesToShort(
      header, 162, 2, little)));
    metadata.put("Wavelength 5 min. intensity", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 172, 4, little))));
    metadata.put("Wavelength 5 max. intensity", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 176, 4, little))));

    int numT = DataTools.bytesToShort(header, 180, 2, little);
    metadata.put("Number of timepoints", new Integer(numT));
    if (ome != null) OMETools.setSizeT(ome, numT);

    int sequence = DataTools.bytesToInt(header, 182, 4, little);
    String imageSequence;
    String dimOrder;
    switch (sequence) {
      case 0: imageSequence = "ZTW"; dimOrder = "XYZTC"; break;
      case 1: imageSequence = "WZT"; dimOrder = "XYCZT"; break;
      case 2: imageSequence = "ZWT"; dimOrder = "XYZCT"; break;
      default: imageSequence = "unknown"; dimOrder = "XYZTC";
    }
    metadata.put("Image sequence", imageSequence);
    if (ome != null) OMETools.setDimensionOrder(ome, dimOrder);

    metadata.put("X axis tilt angle", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 184, 4, little))));
    metadata.put("Y axis tilt angle", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 188, 4, little))));
    metadata.put("Z axis tilt angle", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 192, 4, little))));

    int numW = DataTools.bytesToShort(header, 196, 2, little);
    metadata.put("Number of wavelengths", new Integer(numW));
    if (ome != null) OMETools.setSizeC(ome, numW);
    int numZ = numImages / (numW * numT);
    metadata.put("Number of focal planes", new Integer(numZ));
    if (ome != null) OMETools.setSizeZ(ome, numZ);

    metadata.put("Wavelength 1 (in nm)", new Integer(DataTools.bytesToShort(
      header, 198, 2, little)));
    metadata.put("Wavelength 2 (in nm)", new Integer(DataTools.bytesToShort(
      header, 200, 2, little)));
    metadata.put("Wavelength 3 (in nm)", new Integer(DataTools.bytesToShort(
      header, 202, 2, little)));
    metadata.put("Wavelength 4 (in nm)", new Integer(DataTools.bytesToShort(
      header, 204, 2, little)));
    metadata.put("Wavelength 5 (in nm)", new Integer(DataTools.bytesToShort(
      header, 206, 2, little)));
    metadata.put("X origin (in um)", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 208, 4, little))));
    metadata.put("Y origin (in um)", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 212, 4, little))));
    metadata.put("Z origin (in um)", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 216, 4, little))));
    int numTitles = DataTools.bytesToInt(header, 220, 4, little);

    if (ome != null) {
      Float x = (Float) metadata.get("X origin (in um)");
      Float y = (Float) metadata.get("Y origin (in um)");
      Float z = (Float) metadata.get("Z origin (in um)");
      OMETools.setStageLabel(ome, null, x, y, z);
    }

    String title;
    for (int i=1; i<=10; i++) {
      // Make sure that "null" characters are stripped out
      title = new String(header, 224 + 80*(i-1), 80).replaceAll("\0", "");
      metadata.put("Title " + i, title);
    }

    if (ome != null) {
      OMETools.setDescription(ome, (String) metadata.get("Title 1"));
    }

    // ----- The Extended Header data handler begins here ------

    numIntsPerSection = DataTools.bytesToInt(header, 128, 2, little);
    numFloatsPerSection = DataTools.bytesToInt(header, 130, 2, little);
    setOffsetInfo(sequence, numZ, numW, numT);
    ExtHdrFields = new DVExtHdrFields[numZ][numW][numT];

    // Run through every timeslice, for each wavelength, for each z section
    // and fill in the Extended Header information array for that image
    for (int z = 0; z < numZ; z++) {
      for (int t = 0; t < numT; t++) {
        for (int w = 0; w < numW; w++) {
          ExtHdrFields[z][w][t] = new DVExtHdrFields(getTotalOffset(z,w,t), 
              numIntsPerSection, extHeader, little);
        }
      }           
    } 
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
    switch (imgSequence)
    {
    // ZTW sequence
    case 0: zSize = smallOffset;
    tSize = zSize * numZSections;
    wSize = tSize * numTimes;
    break;

    // WZT sequence
    case 1: wSize = smallOffset;
    zSize = wSize * numWaves;
    tSize = zSize * numZSections;
    break;

    // ZWT sequence
    case 2: zSize = smallOffset;
    wSize = zSize * numZSections;
    tSize = wSize * numWaves;
    break;
    }
  }

  /**
   * Given any specific Z, W, and T of a plane, determine the totalOffset from 
   * the start of the extended header
   * @param currentZ
   * @param currentW
   * @param currentT
   * @return
   */
  public int getTotalOffset(int currentZ, int currentW, int currentT)
  {
    return (zSize * currentZ) + (wSize * currentW) + (tSize * currentT);
  }

  /**
   * This method returns the a plane number from when given a Z, W 
   * and T offsets
   * @param currentZ
   * @param currentW
   * @param currentT
   * @return
   */
  public int getPlaneNumber(int currentZ, int currentW, int currentT)
  {
    int smallOffset = (numIntsPerSection + numFloatsPerSection) * 4;
    return getTotalOffset(currentZ, currentW, currentT) / smallOffset;
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new DeltavisionReader().testRead(args);
  }
}

/**
 * This private class structure holds the details for the extended header
 * @author Brian W. Loranger
 *
 */
class DVExtHdrFields {

  private int offsetWithInts;

  private float ODFilter;
  
  //  Photosensor reading. Typically in mV.
  private float photosensorReading;
  //    Time stamp in seconds since the experiment began.
  private float timeStampSeconds;
  // X stage coordinates.
  private float stageXCoord;
  // Y stage coordinates.
  private float stageYCoord;
  // Z stage coordinates.
  private float stageZCoord;
  // Minimum intensity
  private float minInten;
  // Maxiumum intensity.
  private float maxInten;
  // Mean intesity.
  private float meanInten;
  // Exposure time in seconds.
  private float expTime;
  // Neutral density value.
  private float ndFilter;
  // Excitation filter number.
  private float exFilter;
  // Emiision filter number.
  private float emFilter;
  // Excitation filter wavelength.
  private float exWavelen;
  // Emission filter wavelength.
  private float emWavelen;
  // Intensity scaling factor. Usually 1.
  private float intenScaling;
  // Energy conversion factor. Usually 1.
  private float energyConvFactor;

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   * Helper function which overrides toString, printing out the values in 
   * the header section.
   */
  public String toString()
  {
    String s = new String();

    s+= "photosensorReading: " + photosensorReading + "\n";
    s+= "timeStampSeconds: " + timeStampSeconds + "\n";
    s+= "stageXCoord: " + stageXCoord + "\n";
    s+= "stageYCoord: " + stageYCoord + "\n";
    s+= "stageZCoord: " + stageZCoord + "\n";
    s+= "minInten: " + minInten + "\n";
    s+= "maxInten: " + maxInten + "\n";
    s+= "meanInten: " + meanInten + "\n";
    s+= "expTime: " + expTime + "\n";
    s+= "ndFilter: " + ndFilter + "\n";
    s+= "exFilter: " + exFilter + "\n";
    s+= "emFilter: " + emFilter + "\n";
    s+= "exWavelen: " + exWavelen + "\n";
    s+= "emWavelen: " + emWavelen + "\n";
    s+= "intenScaling: " + intenScaling + "\n";
    s+= "energyConvFactor: " + energyConvFactor + "\n";

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
  DVExtHdrFields(int startingOffset, int numIntsPerSection, 
      byte[] extHeader, boolean little)
      {
    // skip over the int values that have nothing in them
    offsetWithInts = startingOffset + (numIntsPerSection * 4);

    // DV files store the ND (neuatral density) Filter (normally expressed as
    // a %T (transmittance)) as an OD (optical density) rating. To convert from
    // one to the other the formula is %T = 10^(-OD) X 100.
    ODFilter = Float.intBitsToFloat(
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
    ndFilter = (float) java.lang.Math.pow(10.0, -ODFilter);
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

  
  /**
   * Various getters for the Extended header fields.
   * @return
   */
  public float getPhotosensorReading()
  {
    return photosensorReading;
  }

  public float getTimeStampSeconds()
  {
    return timeStampSeconds;
  }

  public float getStageXCoord()
  {
    return stageXCoord;
  }

  public float getStageYCoord()
  {
    return stageYCoord;
  }

  public float getStageZCoord()
  {
    return stageZCoord;
  }      

  public float getMinInten()
  {
    return minInten;
  }

  public float getMaxInten()
  {
    return maxInten;
  }

  public float getMeanInten()
  {
    return meanInten;
  }

  public float getExpTime()
  {
    return expTime;
  }

  public float getNdFilter()
  {
    return ndFilter;
  }

  public float getExFilter()
  {
    return exFilter;
  }

  public float getEmFilter()
  {
    return emFilter;
  }

  public float getExWavelen()
  {
    return exWavelen;
  }

  public float getEmWavelen()
  {
    return emWavelen;
  }

  public float getIntenScaling()
  {
    return intenScaling;
  }

}