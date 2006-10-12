//
// LIFReader.java
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
import java.io.*;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import loci.formats.*;

/**
 * LIFReader is the file format reader for Leica LIF files.
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class LIFReader extends FormatReader {

  // -- Fields --

  /** Current file. */
  protected RandomAccessFile in;

  /** Flag indicating whether current file is little endian. */
  protected boolean littleEndian;

  /** Number of image planes in the file. */
  protected int numImages = 0;

  /** Offsets to memory blocks, paired with their corresponding description. */
  protected Vector offsets;

  /**
   * Dimension information for each image.
   * The first index specifies the image number, and the second specifies
   * the dimension from the following list:
   * 0) width
   * 1) height
   * 2) Z
   * 3) T
   * 4) channels (1 or 3)
   * 5) bits per pixel
   * 6) extra dimensions
   */
  protected int[][] dims;

  private int width;
  private int height;
  private int c;
  private int bpp;
  private Vector xcal;
  private Vector ycal;
  private Vector zcal;
  private Vector seriesNames;

  private Vector channelMins;
  private Vector channelMaxs;

  // -- Constructor --

  /** Constructs a new Leica LIF reader. */
  public LIFReader() { super("Leica Image File Format", "lif"); }


  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for a LIF file. */
  public boolean isThisType(byte[] block) {
    return block[0] == 0x70;
  }

  /** Determines the number of images in the given LIF file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    numImages = dims[series][2] * dims[series][3];
    return numImages;
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return dims[series][4] > 1;
  }

  /** Return true if the data is in little-endian format. */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return littleEndian;
  }

  /** Returns whether or not the channels are interleaved. */
  public boolean isInterleaved(String id) throws FormatException, IOException {
    return false;
  }

  /** Return the number of series in this file. */
  public int getSeriesCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return dims.length;
  }

  /**
   * (non-Javadoc)
   * @see loci.formats.IFormatReader#getChannelGlobalMinimum(String, int)
   */
  public Double getChannelGlobalMinimum(String id, int theC)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return new Double(((Integer) channelMins.get(series)).intValue());
  }

  /**
   * (non-Javadoc)
   * @see loci.formats.IFormatReader#getChannelGlobalMaximum(String, int)
   */
  public Double getChannelGlobalMaximum(String id, int theC)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return new Double(((Integer) channelMaxs.get(series)).intValue());
  }

  /** Obtains the specified image from the given LIF file as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    // determine which dataset the plane is part of

    int z = getSizeZ(id);
    int t = getSizeT(id);

    width = dims[series][0];
    height = dims[series][1];
    c = dims[series][4];
    if (c == 2) c--;
    bpp = dims[series][5];
    while (bpp % 8 != 0) bpp++;
    int bytesPerPixel = bpp / 8;

    int offset = ((Long) offsets.get(series)).intValue();

    // get the image number within this dataset

    in.seek(offset + width * height * bytesPerPixel * no * c);

    byte[] data = new byte[(int) (width * height * bytesPerPixel * c)];
    in.read(data);
    return data;
  }

  /** Obtains the specified image from the given LIF file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    return ImageTools.makeImage(openBytes(id, no), width, height,
      !isRGB(id) ? 1 : c, false, bpp / 8, littleEndian);
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  /** Initializes the given LIF file. */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessFile(id, "r");
    offsets = new Vector();

    littleEndian = true;

    xcal = new Vector();
    ycal = new Vector();
    zcal = new Vector();
    channelMins = new Vector();
    channelMaxs = new Vector();

    // read the header

    byte checkOne = (byte) in.read();
    in.skipBytes(2);
    byte checkTwo = (byte) in.read();
    if (checkOne != 0x70 && checkTwo != 0x70) {
      throw new FormatException(id + " is not a valid Leica LIF file");
    }

    in.skipBytes(4);

    // read and parse the XML description

    if (in.read() != 0x2a) {
      throw new FormatException("Invalid XML description");
    }

    // number of Unicode characters in the XML block

    int nc = DataTools.read4SignedBytes(in, littleEndian);
    byte[] s = new byte[nc * 2];
    in.read(s);
    String xml = DataTools.stripString(new String(s));

    while (in.getFilePointer() < in.length()) {
      if (DataTools.read4SignedBytes(in, littleEndian) != 0x70) {
        throw new FormatException("Invalid Memory Block");
      }

      in.skipBytes(4);
      if (in.read() != 0x2a) {
        throw new FormatException("Invalid Memory Description");
      }

      int blockLength = DataTools.read4SignedBytes(in, littleEndian);
      if (in.read() != 0x2a) {
        throw new FormatException("Invalid Memory Description");
      }

      int descrLength = DataTools.read4SignedBytes(in, littleEndian);
      byte[] memDescr = new byte[2*descrLength];
      in.read(memDescr);

      if (blockLength > 0) {
        offsets.add(new Long(in.getFilePointer()));
      }

      in.skipBytes(blockLength);
    }
    numImages = offsets.size();
    initMetadata(xml);
  }

  // -- Helper methods --

  /** Parses a string of XML and puts the values in a Hashtable. */
  private void initMetadata(String xml) throws FormatException, IOException {
    Vector elements = new Vector();
    seriesNames = new Vector();

    // first parse each element in the XML string

    StringTokenizer st = new StringTokenizer(xml, ">");
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      elements.add(token.substring(1));
    }

    // the first element contains version information

    String token = (String) elements.get(0);
    String key = token.substring(0, token.indexOf("\""));
    String value = token.substring(token.indexOf("\"") + 1, token.length()-1);
    metadata.put(key, value);

    // what we have right now is a vector of XML elements, which need to
    // be parsed into the appropriate image dimensions

    int ndx = 1;
    numImages = 0;

    // the image data we need starts with the token "ElementName='blah'" and
    // ends with the token "/ImageDescription"

    int numDatasets = 0;
    Vector widths = new Vector();
    Vector heights = new Vector();
    Vector zs = new Vector();
    Vector ts = new Vector();
    Vector channels = new Vector();
    Vector bps = new Vector();
    Vector extraDims = new Vector();

    while (ndx < elements.size()) {
      token = (String) elements.get(ndx);

      // if the element contains a key/value pair, parse it and put it in
      // the metadata hashtable

      String tmpToken = token;
      if (token.indexOf("=") != -1) {
        while (token.length() > 2) {
          key = token.substring(0, token.indexOf("\"") - 1);
          value = token.substring(token.indexOf("\"") + 1,
            token.indexOf("\"", token.indexOf("\"") + 1));

          token = token.substring(key.length() + value.length() + 3);

          key = key.trim();
          value = value.trim();
          metadata.put(key, value);
        }
      }
      token = tmpToken;

      if (token.startsWith("ScannerSettingRecord")) {
        if (token.indexOf("csScanMode") != -1) {
          int index = token.indexOf("Variant") + 7;
          String ordering = token.substring(index + 2,
            token.indexOf("\"", index + 3));
          ordering = ordering.toLowerCase();

          if (ordering.indexOf("x") == -1 || ordering.indexOf("y") == -1 ||
            ordering.indexOf("xy") == -1)
          {
            int xPos = ordering.indexOf("x");
            int yPos = ordering.indexOf("y");
            int zPos = ordering.indexOf("z");
            int tPos = ordering.indexOf("t");

            if (xPos < 0) xPos = 0;
            if (yPos < 0) yPos = 1;
            if (zPos < 0) zPos = 2;
            if (tPos < 0) tPos = 3;

            int x = ((Integer) widths.get(widths.size() - 1)).intValue();
            int y = ((Integer) heights.get(widths.size() - 1)).intValue();
            int z = ((Integer) zs.get(widths.size() - 1)).intValue();
            int t = ((Integer) ts.get(widths.size() - 1)).intValue();

            int[] dimensions = {x, y, z, t};

            x = dimensions[xPos];
            y = dimensions[yPos];
            z = dimensions[zPos];
            t = dimensions[tPos];

            widths.setElementAt(new Integer(x), widths.size() - 1);
            heights.setElementAt(new Integer(y), heights.size() - 1);
            zs.setElementAt(new Integer(z), zs.size() - 1);
            ts.setElementAt(new Integer(t), ts.size() - 1);
          }
        }
      }
      else if (token.startsWith("Element Name")) {
        // loop until we find "/ImageDescription"
        seriesNames.add(token.substring(token.indexOf("=") + 2,
          token.length() - 1));

        numDatasets++;
        int numChannels = 0;
        int extras = 1;

        while (token.indexOf("/ImageDescription") == -1) {
          if (token.indexOf("=") != -1) {
            // create a small hashtable to store just this element's data

            Hashtable tmp = new Hashtable();
            while (token.length() > 2) {
              key = token.substring(0, token.indexOf("\"") - 1);
              value = token.substring(token.indexOf("\"") + 1,
                token.indexOf("\"", token.indexOf("\"") + 1));

              token = token.substring(key.length() + value.length() + 3);

              key = key.trim();
              value = value.trim();
              tmp.put(key, value);
            }

            if (tmp.get("ChannelDescription DataType") != null) {
              // found channel description block
              numChannels++;
              if (numChannels == 1) {
                bps.add(new Integer((String) tmp.get("Resolution")));
                try {
                  channelMins.add(new Integer((String) tmp.get("Min")));
                  channelMaxs.add(new Integer((String) tmp.get("Maxs")));
                }
                catch (Exception e) { }
              }
            }
            else if (tmp.get("DimensionDescription DimID") != null) {
              // found dimension description block

              int w = Integer.parseInt((String) tmp.get("NumberOfElements"));
              float cal = Float.parseFloat((String) tmp.get("Length"));
              cal *= 1000;
              int id = Integer.parseInt((String)
                tmp.get("DimensionDescription DimID"));

              switch (id) {
                case 1:
                  widths.add(new Integer(w));
                  xcal.add(new Float(cal));
                  break;
                case 2:
                  heights.add(new Integer(w));
                  ycal.add(new Float(cal));
                  break;
                case 3:
                  zs.add(new Integer(w));
                  zcal.add(new Float(cal));
                  break;
                case 4:
                  ts.add(new Integer(w));
                  break;
                default:
                  extras *= w;
              }
            }
          }

          ndx++;
          try {
            token = (String) elements.get(ndx);
          }
          catch (Exception e) { break; }
        }
        extraDims.add(new Integer(extras));
        if (numChannels == 2) numChannels--;
        if (numChannels == 0) numChannels++;
        channels.add(new Integer(numChannels));

        if (widths.size() < numDatasets && heights.size() < numDatasets) {
          numDatasets--;
        }
        else {
          if (widths.size() < numDatasets) widths.add(new Integer(1));
          if (heights.size() < numDatasets) heights.add(new Integer(1));
          if (zs.size() < numDatasets) zs.add(new Integer(1));
          if (ts.size() < numDatasets) ts.add(new Integer(1));
          if (bps.size() < numDatasets) bps.add(new Integer(8));
        }
      }
      ndx++;
    }

    numDatasets = widths.size();
    dims = new int[numDatasets][7];

    for (int i=0; i<numDatasets; i++) {
      dims[i][0] = ((Integer) widths.get(i)).intValue();
      dims[i][1] = ((Integer) heights.get(i)).intValue();
      dims[i][2] = ((Integer) zs.get(i)).intValue();
      dims[i][3] = ((Integer) ts.get(i)).intValue();
      dims[i][4] = ((Integer) channels.get(i)).intValue();
      dims[i][5] = ((Integer) bps.get(i)).intValue();
      dims[i][6] = ((Integer) extraDims.get(i)).intValue();

      if (dims[i][6] > 1) {
        if (dims[i][2] == 1) dims[i][2] = dims[i][6];
        else dims[i][3] *= dims[i][6];
        dims[i][6] = 1;
      }

      numImages += (dims[i][2] * dims[i][3] * dims[i][6]);
    }

    // Populate metadata store

    // The metadata store we're working with.
    MetadataStore store = getMetadataStore(currentId);

    sizeX = new int[numDatasets];
    sizeY = new int[numDatasets];
    sizeZ = new int[numDatasets];
    sizeC = new int[numDatasets];
    sizeT = new int[numDatasets];
    pixelType = new int[numDatasets];
    currentOrder = new String[numDatasets];

    for (int i=0; i<numDatasets; i++) {
      sizeX[i] = dims[i][0];
      sizeY[i] = dims[i][1];
      sizeZ[i] = dims[i][2];
      sizeC[i] = dims[i][4];
      sizeT[i] = dims[i][3];
      currentOrder[i] = (sizeZ[i] > sizeT[i]) ? "XYCZT" : "XYCTZ";

      while (dims[i][5] % 8 != 0) dims[i][5]++;
      switch (dims[i][5]) {
        case 8:
          pixelType[i] = FormatReader.UINT8;
          break;
        case 16:
          pixelType[i] = FormatReader.UINT16;
          break;
        case 32:
          pixelType[i] = FormatReader.FLOAT;
          break;
      }

      Integer ii = new Integer(i);

      store.setImage((String) seriesNames.get(i), null, null, ii);

      store.setPixels(
        new Integer(dims[i][0]), // SizeX
        new Integer(dims[i][1]), // SizeY
        new Integer(dims[i][2]), // SizeZ
        new Integer(dims[i][4]), // SizeC
        new Integer(dims[i][3]), // SizeT
        new Integer(pixelType[i]), // PixelType
        new Boolean(!littleEndian), // BigEndian
        getDimensionOrder(currentId), // DimensionOrder
        ii); // Index


      Float xf = i < xcal.size() ? (Float) xcal.get(i) : null;
      Float yf = i < ycal.size() ? (Float) ycal.get(i) : null;
      Float zf = i < zcal.size() ? (Float) zcal.get(i) : null;

      store.setDimensions(xf, yf, zf, null, null, ii);
    }
  }


  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new LIFReader().testRead(args);
  }

}
