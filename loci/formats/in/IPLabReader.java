//
// IPLabReader.java
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
 * IPLabReader is the file format reader for IPLab (.IPL) files.
 *
 * @author Melissa Linkert linkert at wisc.edu
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
  public boolean isInterleaved(String id, int subC)
    throws FormatException, IOException
  {
    return true;
  }

  /* @see loci.formats.IFormatReader#getChannelGlobalMinimum(String, int) */
  public Double getChannelGlobalMinimum(String id, int theC)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return (Double) getMeta("NormalizationMin" + theC);
  }

  /* @see loci.formats.IFormatReader#getChannelGlobalMaximum(String, int) */
  public Double getChannelGlobalMaximum(String id, int theC)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return (Double) getMeta("NormalizationMax" + theC);
  }

  /* @see loci.formats.IFormatReader#isMinMaxPopulated(String) */
  public boolean isMinMaxPopulated(String id)
    throws FormatException, IOException
  {
    return true;
  }

  /** Obtains the specified image from the given IPLab file as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    byte[] buf = new byte[width * height * bps * c];
    return openBytes(id, no, buf);
  }

  public byte[] openBytes(String id, int no, byte[] buf)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    int numPixels = width * height * c;
    if (buf.length < numPixels * bps) {
      throw new FormatException("Buffer too small.");
    }
    in.seek(numPixels * bps * (no / c) + 44);

    in.read(buf);
    updateMinMax(buf, no);
    return buf;
  }

  /** Obtains the specified image from the given IPLab file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    BufferedImage b = ImageTools.makeImage(openBytes(id, no), width, height,
      !isRGB(id) ? 1 : c, false, bps, littleEndian);
    updateMinMax(b, no);
    return b;
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws FormatException, IOException {
    if (fileOnly && in != null) in.close();
    else if (!fileOnly) close();
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  /** Initializes the given IPLab file. */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("IPLabReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    status("Populating metadata");

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

    addMeta("Width", new Long(width));
    addMeta("Height", new Long(height));
    addMeta("Channels", new Long(c));
    addMeta("ZDepth", new Long(zDepth));
    addMeta("TDepth", new Long(tDepth));

    String ptype;
    bps = 1;
    switch ((int) filePixelType) {
      case 0:
        ptype = "8 bit unsigned";
        pixelType[0] = FormatTools.UINT8;
        bps = 1;
        break;
      case 1:
        ptype = "16 bit signed short";
        pixelType[0] = FormatTools.INT16;
        bps = 2;
        break;
      case 2:
        ptype = "16 bit unsigned short";
        pixelType[0] = FormatTools.UINT16;
        bps = 2;
        break;
      case 3:
        ptype = "32 bit signed long";
        pixelType[0] = FormatTools.INT32;
        bps = 4;
        break;
      case 4:
        ptype = "32 bit single-precision float";
        pixelType[0] = FormatTools.FLOAT;
        bps = 4;
        break;
      case 5:
        ptype = "Color24";
        pixelType[0] = FormatTools.INT32;
        bps = 1;
        break;
      case 6:
        ptype = "Color48";
        pixelType[0] = FormatTools.INT32;
        bps = 2;
        break;
      case 10:
        ptype = "64 bit double-precision float";
        pixelType[0] = FormatTools.DOUBLE;
        bps = 8;
        break;
      default:
        ptype = "reserved"; // for values 7-9
    }

    addMeta("PixelType", ptype);
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
      null, // Use image index 0
      null); // Use pixels index 0

    for (int i=0; i<sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null);
    }

    status("Reading tags");

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
          addMeta("LUT type", clutType);
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
          addMeta("NormalizationSource" + i, sourceType);

          double min = in.readDouble();
          double max = in.readDouble();
          double gamma = in.readDouble();
          double black = in.readDouble();
          double white = in.readDouble();

          addMeta("NormalizationMin" + i, new Double(min));
          addMeta("NormalizationMax" + i, new Double(max));
          addMeta("NormalizationGamma" + i, new Double(gamma));
          addMeta("NormalizationBlack" + i, new Double(black));
          addMeta("NormalizationWhite" + i, new Double(white));

          store = getMetadataStore(currentId);
          store.setChannelGlobalMinMax(i, new Double(min),
            new Double(max), null);

          store.setDisplayChannel(new Integer(c), new Double(black),
            new Double(white), new Float(gamma), null);
        }
      }
      else if (tag.equals("head")) {
        // read in header labels

        int size = in.readInt();

        byte[] headerString = new byte[20];
        for (int i=0; i<size / (headerString.length + 2); i++) {
          int num = in.readShort();
          in.read(headerString);
          String name = new String(fourBytes);
          addMeta("Header" + num, name);
        }
      }
      else if (tag.equals("mmrc")) {
        in.skipBytes(in.readInt());
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
        int size = in.readInt();
        in.skipBytes(size);
      }
      else if (tag.equals("unit")) {
        // read in units
        in.readInt(); // size is 48

        for (int i=0; i<4; i++) {
          int xResStyle = in.readInt();
          int unitsPerPixel = in.readInt();
          int xUnitName = in.readInt();

          addMeta("ResolutionStyle" + i, new Long(xResStyle));
          addMeta("UnitsPerPixel" + i, new Long(unitsPerPixel));

          if (i == 0) {
            Float pixelSize = new Float(1 / (float) unitsPerPixel);
            store.setDimensions(pixelSize, pixelSize, null, null, null, null);
          }

          addMeta("UnitName" + i, new Long(xUnitName));
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
        addMeta("Descriptor", descriptor);
        addMeta("Notes", notes);

        store.setImage(id, null, notes, null);
      }

      if (in.getFilePointer() + 4 <= in.length()) {
        in.read(fourBytes);
        tag = new String(fourBytes);
      }
      else {
        tag = "fini";
      }
      if (in.getFilePointer() >= in.length() && !tag.equals("fini")) {
        tag = "fini";
      }
    }

    sizeX[0] = width;
    sizeY[0] = height;
    sizeZ[0] = (int) ((Long) getMeta("ZDepth")).longValue();
    sizeC[0] = c;
    sizeT[0] = (int) ((Long) getMeta("TDepth")).longValue();
    currentOrder[0] = order;

  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new IPLabReader().testRead(args);
  }

}
