//
// OpenlabReader.java
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
import java.util.Arrays;
import java.util.Vector;
import loci.formats.*;

/**
 * OpenlabReader is the file format reader for Openlab LIFF files.
 *
 * @author Melissa Linkert linkert at wisc.edu
 * @author Eric Kjellman egkjellman at wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class OpenlabReader extends FormatReader {

  // -- Constants --

  /** Image types. */
  private static final int MAC_1_BIT = 1;
  private static final int MAC_256_GREYS = 5;
  private static final int MAC_256_COLORS = 6;
  private static final int MAC_24_BIT = 8;
  private static final int GREY_16_BIT = 16;

  // -- Static fields --

  /** Helper reader to read PICT data. */
  private static PictReader pict = new PictReader();

  // -- Fields --

  /** Current file. */
  private RandomAccessStream in;

  /** LIFF version (should be 2 or 5). */
  private int version;

  /** Number of images in the file. */
  private int[] numImages;

  /** Image width. */
  private int[] width;

  /** Image height. */
  private int[] height;

  /** Number of channels. */
  private int[] channelCount;

  /** Number of series. */
  private int numSeries;

  private Vector[] layerInfoList;
  private float xCal, yCal, zCal;
  private int bytesPerPixel;

  private int tag = 0, subTag = 0;
  private String fmt = "";

  // -- Constructor --

  /** Constructs a new OpenlabReader. */
  public OpenlabReader() { super("Openlab LIFF", "liff"); }

  // -- FormatReader API methods --

  /** Checks if the given block is a valid Openlab LIFF header. */
  public boolean isThisType(byte[] block) {
    return block.length >= 8 && block[0] == 0 && block[1] == 0 &&
      block[2] == -1 && block[3] == -1 && block[4] == 105 &&
      block[5] == 109 && block[6] == 112 && block[7] == 114;
  }

  /**
   * Checks if the given string is a valid filename for an Openlab file.
   * @param open If true, and the file extension is insufficient to determine
   * the file type, the (existing) file is opened for further analysis.
   */
  public boolean isThisType(String name, boolean open) {
    if (super.isThisType(name, open)) return true; // check extension

    if (open) {
      byte[] b = new byte[8];
      try {
        in = new RandomAccessStream(getMappedId(name));
        in.read(b);
      }
      catch (Exception e) { }
      return isThisType(b);
    }
    else {
      return name.indexOf(".") < 0; // file appears to have no extension
    }
  }

  /** Determines the number of images in the given file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return numImages[series];
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return channelCount[series] > 1;
  }

  /** Return the number of series in this file. */
  public int getSeriesCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return numSeries;
  }

  /** Return true if the data is in little-endian format. */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    return false;
  }

  /** Returns whether or not the channels are interleaved. */
  public boolean isInterleaved(String id) throws FormatException, IOException {
    return true;
  }

  /** Obtains the specified image from the given file as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    LayerInfo info = (LayerInfo) layerInfoList[series].get(no);
    in.seek(info.layerStart);

    readTagHeader();

    if ((tag != 67 && tag != 68) ||
      (!fmt.equals("PICT") && !fmt.equals("RAWi")))
    {
      throw new FormatException("Corrupt LIFF file.");
    }

    in.skipBytes(24);
    int volumeType = DataTools.read2SignedBytes(in, false);
    in.skipBytes(272);

    int top, left, bottom, right;

    if (version == 2) {
      in.skipBytes(2);
      top = DataTools.read2SignedBytes(in, false);
      left = DataTools.read2SignedBytes(in, false);
      bottom = DataTools.read2SignedBytes(in, false);
      right = DataTools.read2SignedBytes(in, false);

      if (width[series] == 0) width[series] = right - left;
      if (height[series] == 0) height[series] = bottom - top;
    }
    else {
      width[series] = DataTools.read4SignedBytes(in, false);
      height[series] = DataTools.read4SignedBytes(in, false);
    }

    in.seek(info.layerStart);

    byte[] b = new byte[0];

    if (version == 2) {
      long nextTag = readTagHeader();

      if ((tag != 67 && tag != 68) || !fmt.equals("PICT")) {
        throw new FormatException("Corrupt LIFF file.");
      }
      in.skipBytes(298);

      // open image using pict reader
      try {
        b = new byte[(int) (nextTag - in.getFilePointer())];
        in.read(b);
        BufferedImage img = pict.open(b);
        byte[][] tmp = ImageTools.getBytes(img);
        b = new byte[tmp.length * tmp[0].length];
        for (int i=0; i<tmp.length; i++) {
          System.arraycopy(tmp[i], 0, b, i * tmp[i].length, tmp[i].length);
        }
      }
      catch (Exception e) {
        b = null;
        in.seek(info.layerStart + 12);
        int blockSize = DataTools.read4SignedBytes(in, false);
        byte toRead = (byte) in.read();

        // right now I'm gonna skip all the header info
        // check to see whether or not this is v2 data
        if (toRead == 1) in.skipBytes(128);
        in.skipBytes(169);
        // read in the block of data
        byte[] q = new byte[blockSize];
        in.read(q);
        byte[] pixelData = new byte[blockSize];
        int pixPos = 0;

        int length = q.length;
        int num, size;
        int totalBlocks = -1; // set to allow loop to start.
        int expectedBlock = 0;
        int pos = 0;

        while (expectedBlock != totalBlocks) {

          while (pos + 7 < length &&
            (q[pos] != 73 || q[pos + 1] != 86 || q[pos + 2] != 69 ||
            q[pos + 3] != 65 || q[pos + 4] != 100 || q[pos + 5] != 98 ||
            q[pos + 6] != 112 || q[pos + 7] != 113))
          {
            pos++;
          }

          pos += 8; // skip the block type we just found

          // Read info from the iPic comment. This serves as a
          // starting point to read the rest.
          num = DataTools.bytesToInt(q, pos, 4, false);
          if (num != expectedBlock) {
            throw new FormatException("Expected iPic block not found");
          }
          expectedBlock++;
          if (totalBlocks == -1) {
            totalBlocks = DataTools.bytesToInt(q, pos + 4, 4, false);
          }
          else {
            if (DataTools.bytesToInt(q, pos + 4, 4, false) != totalBlocks) {
              throw new FormatException("Unexpected totalBlocks numbein.read");
            }
          }

          // skip to size
          pos += 16;

          size = DataTools.bytesToInt(q, pos, 4, false);
          pos += 8;

          // copy into our data array.
          System.arraycopy(q, pos, pixelData, pixPos, size);
          pixPos += size;
        }
        System.gc();
        b = new byte[pixPos];
        System.arraycopy(pixelData, 0, b, 0, b.length);
      }
    }
    else {
      readTagHeader();

      if (tag != 68 || !fmt.equals("RAWi")) {
        throw new FormatException("Corrupt LIFF file.");
      }

      if (subTag != 0) {
        throw new FormatException("Wrong compression type.");
      }

      in.skipBytes(24);
      volumeType = DataTools.read2SignedBytes(in, false);

      in.skipBytes(280);
      int size = DataTools.read4SignedBytes(in, false);
      int compressedSize = DataTools.read4SignedBytes(in, false);
      b = new byte[size];
      byte[] c = new byte[compressedSize];
      in.read(c);

      Compression.lzoUncompress(c, size, b);

      if (volumeType == MAC_24_BIT) {
        bytesPerPixel = b.length >= width[series] * height[series] * 4 ? 4 : 3;

        int destRowBytes = width[series] * bytesPerPixel;
        int srcRowBytes = b.length / height[series];

        byte[] tmp = new byte[destRowBytes * height[series]];
        int src = 0;
        int dest = 0;
        for (int y=0; y<height[series]; y++) {
          System.arraycopy(b, src, tmp, dest, destRowBytes);
          src += srcRowBytes;
          dest += destRowBytes;
        }

        // strip out alpha channel and force channel separation

        if (bytesPerPixel == 4) {
          b = new byte[(3 * tmp.length) / 4];
          dest = 0;
          for (int i=0; i<tmp.length; i+=4) {
            b[dest] = tmp[i + 1];
            b[dest + (b.length / 3)] = tmp[i + 2];
            b[dest + ((2 * b.length) / 3)] = tmp[i + 3];
            dest++;
          }
          bytesPerPixel = 3;
        }
      }
      else if (volumeType < MAC_24_BIT) {
        throw new FormatException("Unsupported image type : " + volumeType);
      }
    }
 
    int bpp = b.length / (width[series] * height[series]);
    int expected = width[series] * height[series] * bpp;
    if (b.length > expected) {
      byte[] tmp = b;
      b = new byte[expected];
      System.arraycopy(tmp, 0, b, 0, b.length);
    }
    return b;
  }

  /** Obtains the specified image from the given file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    byte[] b = openBytes(id, no);
    bytesPerPixel = b.length / (width[series] * height[series]);
    if (bytesPerPixel > 3) bytesPerPixel = 3;
    return ImageTools.makeImage(b, width[series], height[series],
      bytesPerPixel == 3 ? 3 : 1, false,
      bytesPerPixel == 3 ? 1 : bytesPerPixel, false);
  }

  /** Closes the currently open file. */
  public void close() throws FormatException, IOException {
    currentId = null;
    if (in != null) in.close();
    in = null;
    if (pict != null) pict.close();
    layerInfoList = null;
  }

  /** Initialize the given Openlab LIFF file. */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(getMappedId(id));

    in.skipBytes(4);
    byte[] b = new byte[4];
    in.read(b);
    String s = new String(b);
    if (!s.equals("impr")) throw new FormatException("Invalid LIFF file.");

    version = DataTools.read4SignedBytes(in, false);

    if (version != 2 && version != 5) {
      throw new FormatException("Invalid version : " + version);
    }

    // skip the layer count and ID seed
    in.skipBytes(4);

    // read offset to first plane
    int offset = DataTools.read4SignedBytes(in, false);
    in.seek(offset);

    layerInfoList = new Vector[2];
    for (int i=0; i<layerInfoList.length; i++) layerInfoList[i] = new Vector();
    xCal = yCal = zCal = (float) 0.0;

    // scan through the file, and read image information

    while (in.getFilePointer() < in.length()) {
      long nextTag, startPos;

      subTag = tag = 0;
      try {
        startPos = in.getFilePointer();
        nextTag = readTagHeader();
      }
      catch (Exception e) {
        if (in.getFilePointer() >= in.length()) break;
        else throw new FormatException(e.getMessage());
      }

      try {
        if (tag == 67 || tag == 68 ||
          fmt.equals("PICT") || fmt.equals("RAWi"))
        {
          LayerInfo info = new LayerInfo();
          info.layerStart = (int) startPos;
          info.zPosition = -1;
          info.wavelength = -1;

          in.skipBytes(24);
          int volumeType = DataTools.read2SignedBytes(in, false);

          if (volumeType == MAC_1_BIT || volumeType == MAC_256_GREYS ||
            volumeType == MAC_256_COLORS ||
            (volumeType >= MAC_24_BIT && volumeType <= GREY_16_BIT))
          {
            in.skipBytes(16);
            b = new byte[128];
            in.read(b);
            info.layerName = new String(b);

            if (!info.layerName.trim().equals("Original Image")) {
              info.timestamp = DataTools.read8SignedBytes(in, false);
              layerInfoList[0].add(info);
            }
          }

        }
        else if (tag == 69) {
          in.skipBytes(4);
          int units = DataTools.read2SignedBytes(in, false);
          in.skipBytes(12);

          xCal = Float.intBitsToFloat(DataTools.read4SignedBytes(in, false));
          yCal = Float.intBitsToFloat(DataTools.read4SignedBytes(in, false));
        }
        else if (tag == 72 || fmt.equals("USER")) {
          char aChar = (char) in.read();
          StringBuffer sb = new StringBuffer();
          while (aChar != 0) {
            sb = sb.append(aChar);
            aChar = (char) in.read();
          }

          String className = sb.toString();

          if (className.equals("CVariableList")) {
            aChar = (char) in.read();

            if (aChar == 1) {
              int numVars = DataTools.read2SignedBytes(in, false);
              while (numVars > 0) {
                aChar = (char) in.read();
                sb = new StringBuffer();
                while (aChar != 0) {
                  sb = sb.append(aChar);
                  aChar = (char) in.read();
                }
                //in.read();

                String varName = "";
                String varStringValue = "";
                double varNumValue = 0.0;

                className = sb.toString();

                int derivedClassVersion = in.read();
                if (derivedClassVersion != 1) {
                  throw new FormatException("Invalid revision.");
                }

                if (className.equals("CStringVariable")) {
                  int strSize = DataTools.read4SignedBytes(in, false);
                  b = new byte[strSize];
                  in.read(b);
                  varStringValue = new String(b);
                  varNumValue = Float.parseFloat(varStringValue);

                  in.skipBytes(1);
                }
                else if (className.equals("CFloatVariable")) {
                  varNumValue = Double.longBitsToDouble(
                    DataTools.read8SignedBytes(in, false));
                  varStringValue = "" + varNumValue;
                }

                int baseClassVersion = in.read();
                if (baseClassVersion == 1 || baseClassVersion == 2) {
                  int strSize = DataTools.read4SignedBytes(in, false);
                  b = new byte[strSize];
                  in.read(b);
                  varName = new String(b);
                  in.skipBytes(baseClassVersion == 1 ? 3 : 2);
                }
                else {
                  throw new FormatException("Invalid revision.");
                }

                metadata.put(varName, varStringValue);
                numVars--;
              }
            }
          }
        }

        in.seek(nextTag);
      }
      catch (Exception e) {
        in.seek(nextTag);
      }
    }

    Vector tmp = new Vector();
    for (int i=0; i<layerInfoList[0].size(); i++) {
      tmp.add(layerInfoList[0].get(i));
    }

    width = new int[1];
    height = new int[1];
    channelCount = new int[2];
    numImages = new int[2];
    numImages[0] = tmp.size();

    // determine if we have a multi-series file

    int oldChannels = openBytes(id, 0).length / (width[0] * height[0] * 3);
    int oldWidth = width[0];

    int oldSize = 0;
    for (int i=0; i<tmp.size(); i++) {
      LayerInfo layer = (LayerInfo) tmp.get(i);
      in.seek(layer.layerStart);

      long nextTag = readTagHeader();
      if (fmt.equals("PICT")) {
        in.skipBytes(298);

        // check if this is really a PICT image
        in.skipBytes(8);
        int w = DataTools.read2SignedBytes(in, false);
        int newSize = (int) (nextTag - in.getFilePointer());
        if ((w == oldWidth) && ((i % 4) == 3) && (newSize != oldSize)) {
          layerInfoList[1].add(tmp.get(i));
          layerInfoList[0].remove(tmp.get(i));
        }
        else oldSize = newSize;
      }
      else {
        in.skipBytes(24);
        int type = DataTools.read2SignedBytes(in, false);
        if (type == MAC_24_BIT) {
          layerInfoList[1].add(tmp.get(i));
          layerInfoList[0].remove(tmp.get(i));
        }
      }
    }

    if (layerInfoList[1].size() == 0 || layerInfoList[0].size() == 0) {
      channelCount = new int[1];
      channelCount[0] = layerInfoList[1].size() == 0 ? 1 : 3;
      if (channelCount[0] == 1 && oldChannels == 1) channelCount[0] = 3;

      int oldImages = numImages[0];
      numImages = new int[1];
      numImages[0] = oldImages;
    }
    else {
      numImages[0] = layerInfoList[0].size();
      numImages[1] = layerInfoList[1].size();
      channelCount[0] = 1;
      channelCount[1] = 3;
      int oldW = width[0];
      int oldH = height[0];
      width = new int[2];
      height = new int[2];
      width[0] = oldW;
      width[1] = oldW;
      height[0] = oldH;
      height[1] = oldH;
    }

    numSeries = numImages.length;

    int[] bpp = new int[numSeries];

    int oldSeries = getSeries(currentId);
    for (int i=0; i<bpp.length; i++) {
      setSeries(currentId, i);
      bpp[i] = openBytes(id, 0).length / (width[i] * height[i]);
    }
    setSeries(currentId, oldSeries);

    if (bytesPerPixel == 3) bytesPerPixel = 1;
    if (bytesPerPixel == 0) bytesPerPixel++;

    // finish populating metadata hashtable
    metadata.put("Version", new Integer(version));
    metadata.put("Number of Series", new Integer(numSeries));
    for (int i=0; i<numSeries; i++) {
      metadata.put("Width (Series " + i + ")", new Integer(width[i]));
      metadata.put("Height (Series " + i + ")", new Integer(height[i]));
      metadata.put("Bit depth (Series " + i + ")",
        new Integer(bpp[i] * 8));
      metadata.put("Number of channels (Series " + i + ")",
        new Integer(channelCount[i]));
      metadata.put("Number of images (Series " + i + ")",
        new Integer(numImages[i]));
    }

    sizeX = width;
    sizeY = height;
    sizeZ = numImages;
    sizeC = channelCount;
    sizeT = new int[numSeries];
    pixelType = new int[numSeries];
    currentOrder = new String[numSeries];
    orderCertain = new boolean[numSeries];
    Arrays.fill(orderCertain, true);

    // populate MetadataStore

    MetadataStore store = getMetadataStore(id);

    for (int i=0; i<numSeries; i++) {
      sizeT[i] += 1;
      currentOrder[i] = isRGB(id) ? "XYCZT" : "XYZCT";

      try {
        if (i != 0) {
          if (bpp[i] == bpp[0]) bpp[i] = bpp[i + 1];
        }
      }
      catch (ArrayIndexOutOfBoundsException a) { }

      switch (bpp[i]) {
        case 1:
          pixelType[i] = FormatReader.INT8;
          break;
        case 2:
          pixelType[i] = FormatReader.UINT16;
          break;
        case 3:
          pixelType[i] = FormatReader.UINT8;
          break;
        case 4:
          pixelType[i] = FormatReader.INT32;
          break;
        case 6:
          pixelType[i] = FormatReader.INT16;
          break;
      }

      store.setImage("Series " + i, null, null, new Integer(i));
      store.setPixels(
        new Integer(width[i]),
        new Integer(height[i]),
        new Integer(numImages[i]),
        new Integer(channelCount[i]),
        new Integer(1),
        new Integer(pixelType[i]),
        new Boolean(!isLittleEndian(id)),
        getDimensionOrder(id),
        new Integer(i));
      store.setDimensions(new Float(xCal), new Float(yCal), new Float(zCal),
        null, null, new Integer(i));
    }
  }

  // -- Helper methods --

  /** Read the next tag. */
  private long readTagHeader()
    throws IOException
  {
    tag = DataTools.read2SignedBytes(in, false);
    subTag = DataTools.read2SignedBytes(in, false);

    long nextTag = (version == 2 ? DataTools.read4SignedBytes(in, false) :
      DataTools.read8SignedBytes(in, false));

    byte[] b = new byte[4];
    in.read(b);
    fmt = new String(b);
    if (version == 2) in.skipBytes(4);
    else in.skipBytes(8);
    return nextTag;
  }

  /** Helper class for storing layer info. */
  protected class LayerInfo {
    protected int layerStart;
    protected int zPosition;
    protected int wavelength;
    protected String layerName;
    protected long timestamp;
  }

  // -- Main method --
  public static void main(String[] args) throws FormatException, IOException {
    new OpenlabReader().testRead(args);
  }

}
