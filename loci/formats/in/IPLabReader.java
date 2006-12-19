//
// IPLabReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan
and Eric Kjellman.

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

  /** Bytes per pixel. */
  private int bps;

  /** Number of channels. */
  private int c;

  /** Total number of pixel bytes. */
  private int dataSize;

  /** Dimension order. */
  private String order;

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
    return numImages;
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return c > 1;
  }

  /** Return true if the data is in little-endian format. */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return littleEndian;
  }

  /** Returns whether or not the channels are interleaved. */
  public boolean isInterleaved(String id) throws FormatException, IOException {
    return true;
  }

  /* @see loci.formats.IFormatReader#getChannelGlobalMinimum(String, int) */
  public Double getChannelGlobalMinimum(String id, int theC)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return (Double) metadata.get("NormalizationMin" + theC);
  }

  /* @see loci.formats.IFormatReader#getChannelGlobalMaximum(String, int) */
  public Double getChannelGlobalMaximum(String id, int theC)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return (Double) metadata.get("NormalizationMax" + theC);
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
    in.read(rawData);
    return rawData;
  }

  /** Obtains the specified image from the given IPLab file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    return ImageTools.makeImage(openBytes(id, no), width, height,
      !isRGB(id) ? 1 : c, false, bps, littleEndian);
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  /** Initializes the given IPLab file. */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(getMappedId(id));

    byte[] fourBytes = new byte[4];
    in.read(fourBytes);
    littleEndian = new String(fourBytes).equals("iiii");

    in.order(littleEndian);

    // populate standard metadata hashtable and OME root node
    in.skipBytes(12);

    dataSize = in.readInt() - 28;
    width = in.readInt();
    height = in.readInt();
    c = in.readInt();
    int zDepth = in.readInt();
    int tDepth = in.readInt();
    int filePixelType = in.readInt();

    numImages = zDepth * tDepth;

    metadata.put("Width", new Long(width));
    metadata.put("Height", new Long(height));
    metadata.put("Channels", new Long(c));
    metadata.put("ZDepth", new Long(zDepth));
    metadata.put("TDepth", new Long(tDepth));

    String ptype;
    bps = 1;
    switch ((int) filePixelType) {
      case 0:
        ptype = "8 bit unsigned";
        pixelType[0] = FormatReader.UINT8;
        bps = 1;
        break;
      case 1:
        ptype = "16 bit signed short";
        pixelType[0] = FormatReader.INT16;
        bps = 2;
        break;
      case 2:
        ptype = "16 bit unsigned short";
        pixelType[0] = FormatReader.UINT16;
        bps = 2;
        break;
      case 3:
        ptype = "32 bit signed long";
        pixelType[0] = FormatReader.INT32;
        bps = 4;
        break;
      case 4:
        ptype = "32 bit single-precision float";
        pixelType[0] = FormatReader.FLOAT;
        bps = 4;
        break;
      case 5:
        ptype = "Color24";
        pixelType[0] = FormatReader.INT32;
        bps = 1;
        break;
      case 6:
        ptype = "Color48";
        pixelType[0] = FormatReader.INT32;
        bps = 2;
        break;
      case 10:
        ptype = "64 bit double-precision float";
        pixelType[0] = FormatReader.DOUBLE;
        bps = 8;
        break;
      default:
        ptype = "reserved"; // for values 7-9
    }

    metadata.put("PixelType", ptype);
    in.skipBytes(dataSize);

    order = "XY";
    if (c > 1) order += "CZT";
    else order += "ZTC";

    // The metadata store we're working with.
    MetadataStore store = getMetadataStore(id);

    store.setPixels(
      new Integer((int) width), // SizeX
      new Integer((int) height), // SizeY
      new Integer((int) zDepth), // SizeZ
      new Integer((int) c), // SizeC
      new Integer((int) tDepth), // SizeT
      new Integer(pixelType[0]), // PixelType
      new Boolean(!littleEndian), // BigEndian
      order, // DimensionOrder
      null); // Use index 0

    in.read(fourBytes);
    String tag = new String(fourBytes);
    while (!tag.equals("fini") && in.getFilePointer() < in.length() - 4) {
      if (tag.equals("clut")) {
        // read in Color Lookup Table
        int size = in.readInt();
        if (size == 8) {
          // indexed lookup table
          in.readInt();
          int type = in.readInt();

          String clutType = "unknown";
          switch ((int) type) {
            case 0:
              clutType = "monochrome";
              break;
            case 1:
              clutType = "reverse monochrome";
              break;
            case 2:
              clutType = "BGR";
              break;
            case 3:
              clutType = "classify";
              break;
            case 4:
              clutType = "rainbow";
              break;
            case 5:
              clutType = "red";
              break;
            case 6:
              clutType = "green";
              break;
            case 7:
              clutType = "blue";
              break;
            case 8:
              clutType = "cyan";
              break;
            case 9:
              clutType = "magenta";
              break;
            case 10:
              clutType = "yellow";
              break;
            case 11:
              clutType = "saturated pixels";
              break;
          }
          metadata.put("LUT type", clutType);
        }
        else {
          // explicitly defined lookup table
          // length is 772
          in.readInt();
          byte[] colorTable = new byte[256*3];
          in.read(colorTable);
        }
      }
      else if (tag.equals("norm")) {
        // read in normalization information

        int size = in.readInt();
        // error checking

        if (size != (44 * c)) {
          throw new FormatException("Bad normalization settings");
        }

        for (int i=0; i<c; i++) {
          long source = in.readInt();

          String sourceType;
          switch ((int) source) {
            case 0:
              sourceType = "user";
              break;
            case 1:
              sourceType = "plane";
              break;
            case 2:
              sourceType = "sequence";
              break;
            case 3:
              sourceType = "saturated plane";
              break;
            case 4:
              sourceType = "saturated sequence";
              break;
            case 5:
              sourceType = "ROI";
              break;
            default:
              sourceType = "user";
          }
          metadata.put("NormalizationSource" + i, sourceType);

          double min = in.readDouble();
          double max = in.readDouble();
          double gamma = in.readDouble();
          double black = in.readDouble();
          double white = in.readDouble();

          metadata.put("NormalizationMin" + i, new Double(min));
          metadata.put("NormalizationMax" + i, new Double(max));
          metadata.put("NormalizationGamma" + i, new Double(gamma));
          metadata.put("NormalizationBlack" + i, new Double(black));
          metadata.put("NormalizationWhite" + i, new Double(white));
        }
      }
      else if (tag.equals("head")) {
        // read in header labels

        in.readInt(); // size is defined to 2200

        for (int i=0; i<100; i++) {
          int num = in.readShort();
          in.read(fourBytes);
          String name = new String(fourBytes);
          metadata.put("Header" + num, name);
        }
      }
      else if (tag.equals("roi ")) {
        // read in ROI information

        in.skipBytes(8);
        int roiLeft = in.readInt();
        int roiTop = in.readInt();
        int roiRight = in.readInt();
        int roiBottom = in.readInt();
        int numRoiPts = in.readInt();

        Integer x0 = new Integer((int) roiLeft);
        Integer x1 = new Integer((int) roiRight);
        Integer y0 = new Integer((int) roiBottom);
        Integer y1 = new Integer((int) roiTop);
        store.setDisplayROI(
          x0, y0, null, x1, y1, null, null, null, null, null);

        in.skipBytes(8 * numRoiPts);
      }
      else if (tag.equals("mask")) {
        // read in Segmentation Mask
      }
      else if (tag.equals("unit")) {
        // read in units
        in.readInt(); // size is 48

        for (int i=0; i<4; i++) {
          int xResStyle = in.readInt();
          int unitsPerPixel = in.readInt();
          int xUnitName = in.readInt();

          metadata.put("ResolutionStyle" + i, new Long(xResStyle));
          metadata.put("UnitsPerPixel" + i, new Long(unitsPerPixel));

          if (i == 0) {
            Float pixelSize = new Float(unitsPerPixel);
            store.setDimensions(pixelSize, pixelSize, null, null, null, null);
          }

          metadata.put("UnitName" + i, new Long(xUnitName));
        }
      }
      else if (tag.equals("view")) {
        // read in view
        in.readInt();
      }
      else if (tag.equals("plot")) {
        // read in plot
        // skipping this field for the moment
        in.skipBytes(2512);
      }
      else if (tag.equals("notes")) {
        // read in notes (image info)
        in.readInt(); // size is 576
        byte[] temp = new byte[64];
        in.read(temp);
        String descriptor = new String(temp);
        temp = new byte[512];
        in.read(temp);
        String notes = new String(temp);
        metadata.put("Descriptor", descriptor);
        metadata.put("Notes", notes);

        store.setImage(id, null, notes, null);
      }
      try {
        in.read(fourBytes);
        tag = new String(fourBytes);
      }
      catch (Exception e) { tag = "fini"; }

    }

    sizeX[0] = width;
    sizeY[0] = height;
    sizeZ[0] = (int) ((Long) metadata.get("ZDepth")).longValue();
    sizeC[0] = c;
    sizeT[0] = (int) ((Long) metadata.get("TDepth")).longValue();
    currentOrder[0] = order;

  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new IPLabReader().testRead(args);
  }

}
