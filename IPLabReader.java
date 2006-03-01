//
// IPLabReader.java
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
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * IPLabReader is the file format reader for IPLab (.IPL) files.
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class IPLabReader extends FormatReader {

  // -- Fields --

  /** Current file. */
  protected RandomAccessFile in;

  /** Flag indicating whether current file is little endian. */
  protected boolean littleEndian;


  // -- Constructor --

  /** Constructs a new IPLab reader. */
  public IPLabReader() { super("IPLab", "ipl"); }


  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for an IPLab file. */
  public boolean isThisType(byte[] block) {
    if (block.length < 12) return false; // block length too short
    String s = new String(block, 0, 4);
    boolean big = s.equals("iiii");
    boolean little = s.equals("mmmm");
    if (!big && !little) return false;
    int size = DataTools.bytesToInt(block, 4, 4, little);
    if (size != 4) return false; // first block size should be 4
    int version = DataTools.bytesToInt(block, 8, 4, little);
    if (version < 0x100e) return false; // invalid version
    return true;
  }

  /** Determines the number of images in the given IPLab file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);

    in.seek(0);
    in.skipBytes(32);
    long zDepth = DataTools.read4UnsignedBytes(in, littleEndian);
    long tDepth = DataTools.read4UnsignedBytes(in, littleEndian);

    int numImages = (int) (zDepth * tDepth);
    return numImages;
  }

  /** Obtains the specified image from the given IPLab file. */
  public Image open(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    // read image bytes and convert to floats
    in.seek(0);
    in.skipBytes(16);

    long dataSize = DataTools.read4UnsignedBytes(in, littleEndian);
    dataSize -= 28; // size of raw image data, in bytes
    long width = DataTools.read4UnsignedBytes(in, littleEndian);
    long height = DataTools.read4UnsignedBytes(in, littleEndian);
    long channels = DataTools.read4UnsignedBytes(in, littleEndian);
    long zDepth = DataTools.read4UnsignedBytes(in, littleEndian);
    long tDepth = DataTools.read4UnsignedBytes(in, littleEndian);
    long pixelType = DataTools.read4UnsignedBytes(in, littleEndian);
    byte[] rawData = new byte[(int) dataSize];
    in.readFully(rawData);

    int[] bitsPerSample = new int[1];
    // bitsPerSample is dependent on the pixel type

    switch ((int) pixelType) {
      case 0: bitsPerSample[0] = 8; break;
      case 1: bitsPerSample[0] = 16; break;
      case 2: bitsPerSample[0] = 16; break;
      case 3: bitsPerSample[0] = 32; break;
      case 4: bitsPerSample[0] = 32; break;
      case 10: bitsPerSample[0] = 64; break;
    }

    int w = (int) width, h = (int) height, c = (int) channels;
    int numPixels = w * h;
    BufferedImage image = null;

    if (bitsPerSample[0] == 8) {
      // case for 8 bit data
      image = ImageTools.makeImage(rawData, w, h, c, false);
    }

    else if (bitsPerSample[0] == 16) {
      // case for 16 bit data
      short[] data = new short[c * numPixels];
      for (int i=0; i<data.length; i++) {
        data[i] = DataTools.bytesToShort(rawData, 2 * i, littleEndian);
      }
      image = ImageTools.makeImage(data, w, h, c, false);
    }

    else throw new FormatException("Sorry, " +
      bitsPerSample[0] + " bits per sample is not supported");

    return image;
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  /** Initializes the given IPLab file. */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessFile(id, "r");

    byte[] fourBytes = new byte[4];
    in.read(fourBytes);
    littleEndian = new String(fourBytes).equals("iiii");

    // populate standard metadata hashtable and OME root node
    in.seek(0);
    in.skipBytes(16);

    long dataSize = DataTools.read4UnsignedBytes(in, littleEndian);
    dataSize -= 28; // size of raw image data, in bytes
    long width = DataTools.read4UnsignedBytes(in, littleEndian);
    long height = DataTools.read4UnsignedBytes(in, littleEndian);
    long channels = DataTools.read4UnsignedBytes(in, littleEndian);
    long zDepth = DataTools.read4UnsignedBytes(in, littleEndian);
    long tDepth = DataTools.read4UnsignedBytes(in, littleEndian);
    long pixelType = DataTools.read4UnsignedBytes(in, littleEndian);

    metadata.put("Width", new Long(width));
    metadata.put("Height", new Long(height));
    metadata.put("Channels", new Long(channels));
    metadata.put("ZDepth", new Long(zDepth));
    metadata.put("TDepth", new Long(tDepth));

    String ptype;
    switch ((int) pixelType) {
      case 0: ptype = "8 bit unsigned"; break;
      case 1: ptype = "16 bit signed short"; break;
      case 2: ptype = "16 bit unsigned short"; break;
      case 3: ptype = "32 bit signed long"; break;
      case 4: ptype = "32 bit single-precision float"; break;
      case 5: ptype = "Color24"; break;
      case 6: ptype = "Color48"; break;
      case 10: ptype = "64 bit double-precision float"; break;
      default: ptype = "reserved";    // for values 7-9
    }

    metadata.put("PixelType", ptype);
    in.skipBytes((int) dataSize);

    if (ome != null) {
      OMETools.setAttribute(ome, "Pixels", "SizeX", "" + width);
      OMETools.setAttribute(ome, "Pixels", "SizeY", "" + height);
      OMETools.setAttribute(ome, "Pixels", "SizeZ", "" + zDepth);
      OMETools.setAttribute(ome, "Pixels", "SizeC", "" + channels);
      OMETools.setAttribute(ome, "Pixels", "SizeT", "" + tDepth);
      OMETools.setAttribute(ome, "Pixels", "BigEndian",
        littleEndian ? "false" : "true");
      OMETools.setAttribute(ome, "Pixels", "DimensionOrder", "XYZTC");

      // set the pixel type
      String type;
      switch ((int) pixelType) {
        case 0: type = "Uint8"; break;
        case 1: type = "int16"; break;
        case 2: type = "Uint16"; break;
        case 3: type = "Uint32"; break;
        case 4: type = "float"; break;
        case 5: type = "Uint32"; break;
        case 6: type = "Uint32"; break;
        case 10: type = "float"; break;
        default: type = "Uint8";
      }

      OMETools.setAttribute(ome, "Pixels", "PixelType", type);
      OMETools.setAttribute(ome, "Image", "Name", id);
    }

    in.read(fourBytes);
    String tag = new String(fourBytes);
    while (!tag.equals("fini")) {
      if (tag.equals("clut")) {
        // read in Color Lookup Table
        long size = DataTools.read4UnsignedBytes(in, littleEndian);
        if (size == 8) {
          // indexed lookup table
          in.skipBytes(4);
          long type=DataTools.read4UnsignedBytes(in, littleEndian);
          String clutType;
          switch ((int) type) {
            case 0: clutType = "monochrome"; break;
            case 1: clutType = "reverse monochrome"; break;
            case 2: clutType = "BGR"; break;
            case 3: clutType = "classify"; break;
            case 4: clutType = "rainbow"; break;
            case 5: clutType = "red"; break;
            case 6: clutType = "green"; break;
            case 7: clutType = "blue"; break;
            case 8: clutType = "cyan"; break;
            case 9: clutType = "magenta"; break;
            case 10: clutType = "yellow"; break;
            case 11: clutType = "saturated pixels"; break;
          }
        }
        else {
          // explicitly defined lookup table
          // length is 772
          in.skipBytes(4);
          byte[] colorTable = new byte[256*3];
          in.read(colorTable);
        }
      }
      else if (tag.equals("norm")) {
        // read in normalization information

        long size = DataTools.read4UnsignedBytes(in, littleEndian);
        // error checking

        if (size != (44 * channels)) {
          throw new FormatException("Bad normalization settings");
        }

        for (int i=0; i<channels; i++) {
          long source = DataTools.read4UnsignedBytes(in, littleEndian);

          String sourceType;
          switch ((int) source) {
            case 0: sourceType = "user"; break;
            case 1: sourceType = "plane"; break;
            case 2: sourceType = "sequence"; break;
            case 3: sourceType = "saturated plane"; break;
            case 4: sourceType = "saturated sequence"; break;
            case 5: sourceType = "ROI"; break;
            default: sourceType = "user";
          }
          metadata.put("NormalizationSource" + i, sourceType);

          double min=DataTools.read8SignedBytes(in, littleEndian);
          double max=DataTools.read8SignedBytes(in, littleEndian);
          double gamma=DataTools.read8SignedBytes(in, littleEndian);
          double black=DataTools.read8SignedBytes(in, littleEndian);
          double white=DataTools.read8SignedBytes(in, littleEndian);

          metadata.put("NormalizationMin" + i, new Double(min));
          metadata.put("NormalizationMax" + i, new Double(max));
          metadata.put("NormalizationGamma" + i, new Double(gamma));
          metadata.put("NormalizationBlack" + i, new Double(black));
          metadata.put("NormalizationWhite" + i, new Double(white));
        }
      }
      else if (tag.equals("head")) {
        // read in header labels

        in.skipBytes(4);  // size is defined to 2200

        for (int i=0; i<100; i++) {
          int num = DataTools.read2UnsignedBytes(in, littleEndian);
          in.read(fourBytes);
          String name = new String(fourBytes);
          metadata.put("Header" + num, name);
        }
      }
      else if (tag.equals("roi ")) {
        // read in ROI information

        long size = DataTools.read4UnsignedBytes(in, littleEndian);
        long roiType = DataTools.read4UnsignedBytes(in, littleEndian);
        long roiLeft = DataTools.read4UnsignedBytes(in, littleEndian);
        long roiTop = DataTools.read4UnsignedBytes(in, littleEndian);
        long roiRight = DataTools.read4UnsignedBytes(in, littleEndian);
        long roiBottom = DataTools.read4UnsignedBytes(in, littleEndian);
        long numRoiPts = DataTools.read4UnsignedBytes(in, littleEndian);

        if (ome != null) {
          OMETools.setAttribute(ome, "ROI", "X0",
            new Long(roiLeft).toString());
          OMETools.setAttribute(ome, "ROI", "X1",
            new Long(roiRight).toString());
          OMETools.setAttribute(ome, "ROI", "Y0",
            new Long(roiBottom).toString());
          OMETools.setAttribute(ome, "ROI", "Y1",
            new Long(roiTop).toString());
        }

        for (int i=0; i<numRoiPts; i++) {
          long ptX = DataTools.read4UnsignedBytes(in, littleEndian);
          long ptY = DataTools.read4UnsignedBytes(in, littleEndian);
        }
      }
      else if (tag.equals("mask")) {
        // read in Segmentation Mask
      }
      else if (tag.equals("unit")) {
        // read in units
        in.skipBytes(4); // size is 48

        for (int i=0; i<4; i++) {
          long xResStyle = DataTools.read4UnsignedBytes(in, littleEndian);
          long unitsPerPixel = DataTools.read4UnsignedBytes(in, littleEndian);
          long xUnitName = DataTools.read4UnsignedBytes(in, littleEndian);

          metadata.put("ResolutionStyle" + i, new Long(xResStyle));
          metadata.put("UnitsPerPixel" + i, new Long(unitsPerPixel));

          if (i == 0 && ome != null) {
            OMETools.setAttribute(ome,
              "Image", "PixelSizeX", "" + unitsPerPixel);
            OMETools.setAttribute(ome,
              "Image", "PixelSizeY", "" + unitsPerPixel);
          }

          metadata.put("UnitName" + i, new Long(xUnitName));
        }
      }
      else if (tag.equals("view")) {
        // read in view
        in.skipBytes(4);
      }
      else if (tag.equals("plot")) {
        // read in plot
        // skipping this field for the moment
        in.skipBytes(4); // size is 2508
        in.skipBytes(2508);
      }
      else if (tag.equals("notes")) {
        // read in notes (image info)
        in.skipBytes(4); // size is 576
        byte[] temp = new byte[64];
        in.read(temp);
        String descriptor = new String(temp);
        temp = new byte[512];
        in.read(temp);
        String notes = new String(temp);
        metadata.put("Descriptor", descriptor);
        metadata.put("Notes", notes);

        if (ome != null) {
          OMETools.setAttribute(ome, "Image", "Description", notes);
        }
      }
      int r = in.read(fourBytes);
      if (r > 0) {
        tag = new String(fourBytes);
      }
      else { // eof
        tag = "fini";
      }
    }
  }


  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new IPLabReader().testRead(args);
  }

}
