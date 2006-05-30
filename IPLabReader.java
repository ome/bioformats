//
// IPLabReader.java
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
 * IPLabReader is the file format reader for IPLab (.IPL) files.
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class IPLabReader extends FormatReader {

  // -- Fields --

  /** Current file. */
  protected RandomAccessStream in;

  /** Flag indicating whether current file is little endian. */
  protected boolean littleEndian;

  /** Number of images in the file. */
  private int numImages;

  /** Image width. */
  private int width;

  /** Image height. */
  private int height;

  /** Pixel type. */
  private int pixelType;

  /** Bytes per pixel. */
  private int bps;

  /** Number of channels. */
  private int c;

  /** Total number of pixel bytes. */
  private int dataSize;

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
    return (!isRGB(id) || !separated) ? numImages : 3 * numImages;
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return c > 1;
  }

  /** Obtains the specified image from the given IPLab file as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    int numPixels = width * height * c;
    in.seek(numPixels * bps * (no / c) + 44);

    byte[] rawData = new byte[numPixels * bps];
    in.readFully(rawData);

    if (isRGB(id) && separated) {
      return ImageTools.splitChannels(rawData, c, false, true)[no % 3];
    }
    else return rawData;
  }

  /** Obtains the specified image from the given IPLab file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    return ImageTools.makeImage(openBytes(id, no), width, height,
      (!isRGB(id) || separated) ? 1 : c, false, bps, littleEndian);
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
    in = new RandomAccessStream(id);

    byte[] fourBytes = new byte[4];
    in.read(fourBytes);
    littleEndian = new String(fourBytes).equals("iiii");

    // populate standard metadata hashtable and OME root node
    in.skipBytes(12);

    dataSize = (int) DataTools.read4UnsignedBytes(in, littleEndian);
    dataSize -= 28; // size of raw image data, in bytes
    width = (int) DataTools.read4UnsignedBytes(in, littleEndian);
    height = (int) DataTools.read4UnsignedBytes(in, littleEndian);
    c = (int) DataTools.read4UnsignedBytes(in, littleEndian);
    long zDepth = DataTools.read4UnsignedBytes(in, littleEndian);
    long tDepth = DataTools.read4UnsignedBytes(in, littleEndian);
    pixelType = (int) DataTools.read4UnsignedBytes(in, littleEndian);

    numImages = (int) (zDepth * tDepth);

    metadata.put("Width", new Long(width));
    metadata.put("Height", new Long(height));
    metadata.put("Channels", new Long(c));
    metadata.put("ZDepth", new Long(zDepth));
    metadata.put("TDepth", new Long(tDepth));

    String ptype;
    bps = 1;
    switch ((int) pixelType) {
      case 0: ptype = "8 bit unsigned";
              bps = 1;
              break;
      case 1: ptype = "16 bit signed short";
              bps = 2;
              break;
      case 2: ptype = "16 bit unsigned short";
              bps = 2;
              break;
      case 3: ptype = "32 bit signed long";
              bps = 4;
              break;
      case 4: ptype = "32 bit single-precision float";
              bps = 4;
              break;
      case 5: ptype = "Color24";
              bps = 1;
              break;
      case 6: ptype = "Color48";
              bps = 2;
              break;
      case 10: ptype = "64 bit double-precision float";
               bps = 8;
               break;
      default: ptype = "reserved";    // for values 7-9
    }

    metadata.put("PixelType", ptype);
    in.skipBytes((int) dataSize);

    if (ome != null) {
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
      OMETools.setPixels(ome,
        new Integer((int) width), // SizeX
        new Integer((int) height), // SizeY
        new Integer((int) zDepth), // SizeZ
        new Integer((int) c), // SizeC
        new Integer((int) tDepth), // SizeT
        type, // PixelType
        new Boolean(!littleEndian), // BigEndian
        "XYZTC"); // DimensionOrder
      OMETools.setImageName(ome, id);
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

        if (size != (44 * c)) {
          throw new FormatException("Bad normalization settings");
        }

        for (int i=0; i<c; i++) {
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
          Integer x0 = new Integer((int) roiLeft);
          Integer x1 = new Integer((int) roiRight);
          Integer y0 = new Integer((int) roiBottom);
          Integer y1 = new Integer((int) roiTop);
          OMETools.setDisplayROI(ome,
            x0, y0, null, x1, y1, null, null, null, null);
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
            Float pixelSize = new Float(unitsPerPixel);
            OMETools.setDimensions(ome,
              pixelSize, pixelSize, null, null, null);
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
          OMETools.setDescription(ome, notes);
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
