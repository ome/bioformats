//
// DeltavisionReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

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

package loci.formats;

import java.awt.Image;
import java.io.*;

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
  protected RandomAccessFile in;

  /** Number of images in the current file. */
  protected int numImages;

  /** Flag indicating whether current file is little endian. */
  protected boolean little;

  /** Byte array containing basic image header data. */
  protected byte[] header;

  /** Byte array containing extended header data. */
  protected byte[] extHeader;


  // -- Constructor --

  /** Constructs a new Deltavision reader. */
  public DeltavisionReader() { super("Deltavision", "dv"); }


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

  /** Obtains the specified image from the given Deltavision file. */
  public Image open(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    int width = DataTools.bytesToInt(header, 0, 4, little);
    int height = DataTools.bytesToInt(header, 4, 4, little);
    int numTimes = DataTools.bytesToInt(header, 180, 2, little);
    int numWaves = DataTools.bytesToInt(header, 196, 2, little);
    int numZs = numImages / (numWaves * numTimes);
    int pixelType = DataTools.bytesToInt(header, 12, 4, little);
    int dimOrder = DataTools.bytesToInt(header, 182, 2, little);
    int imageSequence = DataTools.bytesToInt(header, 182, 2, little);
    int bytesPerPixel = 0;

    switch (pixelType) {
      case 0: bytesPerPixel = 1; break;
      case 1: bytesPerPixel = 2; break;
      case 2: bytesPerPixel = 4; break;
      case 3: bytesPerPixel = 4; break; // not well supported
      case 4: bytesPerPixel = 8; break; // not supported
      case 6: bytesPerPixel = 2; break;
    }

    // read the image plane's pixel data

    int offset = header.length + extHeader.length;
    offset += width * height * bytesPerPixel * no;

    int channels = 1;
    int numSamples = (int) (width * height);
    byte[] rawData = new byte[width * height * bytesPerPixel];
    in.seek(offset);
    in.read(rawData);

    if (bytesPerPixel == 1) {
      return ImageTools.makeImage(rawData, width, height, channels, false);
    }
    else if (bytesPerPixel == 2) {
      short[] data = new short[numSamples];
      short q;
      for (int i=0; i<rawData.length; i+=2) {
        q = DataTools.bytesToShort(rawData, i, 2, little);
        data[i/2] = q;
      }
      return ImageTools.makeImage(data, width, height, channels, false);
    }
    else if (bytesPerPixel == 4) {
      int[] data = new int[numSamples];
      int q;
      for (int i=0; i<rawData.length; i+=4) {
        q = DataTools.bytesToInt(rawData, i, little);
        data[i/4] = q;
      }
      return ImageTools.makeImage(data, width, height, channels, false);
    }
    else if (bytesPerPixel == 8) {
      // Applied Precision doesn't provide support for 64 bit data,
      // so we won't either
      throw new FormatException("Sorry, 64 bit pixel data not supported.");
    }
    else {
      // this should never happen
      throw new FormatException("Unknown pixel depth : " + bytesPerPixel +
        " bytes per pixel.");
    }
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
    in = new RandomAccessFile(id, "r");

    // read in the image header data
    header = new byte[1024];
    in.read(header);

    int endian = DataTools.bytesToShort(header, 96, 2, true);
    little = endian == LITTLE_ENDIAN;
    numImages = DataTools.bytesToInt(header, 8, 4, little);

    int extSize = DataTools.bytesToInt(header, 92, 4, little);
    extHeader = new byte[extSize];
    in.read(extHeader);

    metadata.put("ImageWidth", new Integer(DataTools.bytesToInt(header, 0, 4,
      little)));
    metadata.put("ImageHeight", new Integer(DataTools.bytesToInt(header,
      4, 4, little)));
    metadata.put("NumberOfImages", new Integer(DataTools.bytesToInt(header,
      8, 4, little)));
    int pixelType = DataTools.bytesToInt(header, 12, 4, little);
    String pixel;
    String omePixel;

    switch (pixelType) {
      case 0: pixel = "8 bit unsigned integer"; omePixel = "Uint8"; break;
      case 1: pixel = "16 bit signed integer"; omePixel = "int16"; break;
      case 2: pixel = "32 bit floating point"; omePixel = "float"; break;
      case 3: pixel = "32 bit complex"; omePixel = "Uint32"; break;
      case 4: pixel = "64 bit complex"; omePixel = "float"; break;
      case 6: pixel = "16 bit unsigned integer"; omePixel = "Uint16"; break;
      default: pixel = "unknown"; omePixel = "Uint8";
    }

    if (ome != null) {
      OMETools.setAttribute(ome, "Pixels", "SizeX",
        "" + metadata.get("ImageWidth"));
      OMETools.setAttribute(ome, "Pixels", "SizeY",
        "" + metadata.get("ImageHeight"));
      OMETools.setAttribute(ome, "Pixels", "PixelType", "" + omePixel);
      OMETools.setAttribute(ome, "Pixels", "BigEndian", "" + !little);
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
    if (ome != null) OMETools.setAttribute(ome, "Pixels", "SizeT", "" + numT);

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
    if (ome != null) {
      OMETools.setAttribute(ome, "Pixels", "DimensionOrder", dimOrder);
    }

    metadata.put("X axis tilt angle", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 184, 4, little))));
    metadata.put("Y axis tilt angle", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 188, 4, little))));
    metadata.put("Z axis tilt angle", new Float(Float.intBitsToFloat(
      DataTools.bytesToInt(header, 192, 4, little))));

    int numW = DataTools.bytesToShort(header, 196, 2, little);
    metadata.put("Number of wavelengths", new Integer(numW));
    if (ome != null) OMETools.setAttribute(ome, "Pixels", "SizeC", "" + numW);
    int numZ = numImages / (numW * numT);
    metadata.put("Number of focal planes", new Integer(numZ));
    if (ome != null) OMETools.setAttribute(ome, "Pixels", "SizeZ", "" + numZ);

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
      OMETools.setAttribute(ome, "StageLabel", "X",
        "" + metadata.get("X origin (in um)"));
      OMETools.setAttribute(ome, "StageLabel", "Y",
        "" + metadata.get("Y origin (in um)"));
      OMETools.setAttribute(ome, "StageLabel", "Z",
        "" + metadata.get("Z origin (in um)"));
    }

    for (int i=1; i<=10; i++) {
      metadata.put("Title " + i, new String(header, 224 + 80*(i-1), 80));
      if (i == 1 && ome != null) {
        OMETools.setAttribute(ome, "Image", "Description",
          "" + metadata.get("Title 1"));
      }
    }
  }


  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new DeltavisionReader().testRead(args);
  }
}
