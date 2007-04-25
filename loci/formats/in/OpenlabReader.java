//
// OpenlabReader.java
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
import java.io.*;
import java.util.Arrays;
import java.util.Vector;
import loci.formats.*;
import loci.formats.codec.LZOCodec;

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

  /** LIFF version (should be 2 or 5). */
  private int version;

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

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return block.length >= 8 && block[0] == 0 && block[1] == 0 &&
      block[2] == -1 && block[3] == -1 && block[4] == 105 &&
      block[5] == 109 && block[6] == 112 && block[7] == 114;
  }

  /* @see loci.formats.IFormatReader#openBytes(int) */
  public byte[] openBytes(int no) throws FormatException, IOException {
    if (no < 0 || no >= getImageCount()) {
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

      if (core.sizeX[series] == 0) core.sizeX[series] = right - left;
      if (core.sizeY[series] == 0) core.sizeY[series] = bottom - top;
    }
    else {
      core.sizeX[series] = DataTools.read4SignedBytes(in, false);
      core.sizeY[series] = DataTools.read4SignedBytes(in, false);
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
        // CTR TODO - eliminate catch-all exception handling
        if (debug) e.printStackTrace();

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

      LZOCodec lzoc = new LZOCodec();
      b = lzoc.decompress(c);
      if (b.length != size) {
        System.err.println("LZOCodec failed to predict image size");
        System.err.println(size + " expected, got " + b.length +
          ". The image displayed may not be correct.");
      }

      if (volumeType == MAC_24_BIT) {
        bytesPerPixel =
          b.length >= core.sizeX[series] * core.sizeY[series] * 4 ? 4 : 3;

        int destRowBytes = core.sizeX[series] * bytesPerPixel;
        int srcRowBytes = b.length / core.sizeY[series];

        byte[] tmp = new byte[destRowBytes * core.sizeY[series]];
        int src = 0;
        int dest = 0;
        for (int y=0; y<core.sizeY[series]; y++) {
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

    int bpp = b.length / (core.sizeX[series] * core.sizeY[series]);
    int expected = core.sizeX[series] * core.sizeY[series] * bpp;
    if (b.length > expected) {
      byte[] tmp = b;
      b = new byte[expected];
      System.arraycopy(tmp, 0, b, 0, b.length);
    }
    return b;
  }

  /* @see loci.formats.IFormatReader#openImage(int) */
  public BufferedImage openImage(int no) throws FormatException, IOException {
    if (no < 0 || no >= getImageCount()) {
      throw new FormatException("Invalid image number: " + no);
    }

    byte[] b = openBytes(no);
    bytesPerPixel = b.length / (core.sizeX[series] * core.sizeY[series]);
    if (bytesPerPixel > 3) bytesPerPixel = 3;
    return ImageTools.makeImage(b, core.sizeX[series], core.sizeY[series],
      bytesPerPixel == 3 ? 3 : 1, false,
      bytesPerPixel == 3 ? 1 : bytesPerPixel, false);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    if (fileOnly) {
      if (in != null) in.close();
      if (pict != null) pict.close(fileOnly);
    }
    else close();
  }

  /* @see loci.formats.IFormatReader#close() */
  public void close() throws IOException {
    super.close(); 
    if (pict != null) pict.close();
    layerInfoList = null;
  }

  /** Initialize the given Openlab LIFF file. */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("OpenlabReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    status("Verifying Openlab LIFF format");

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

    status("Finding image offsets");

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
      catch (IOException e) {
        if (debug) e.printStackTrace();

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

                addMeta(varName, varStringValue);
                numVars--;
              }
            }
          }
        }

        in.seek(nextTag);
      }
      catch (Exception e) {
        // CTR TODO - eliminate catch-all exception handling
        if (debug) e.printStackTrace();
        in.seek(nextTag);
      }
    }

    Vector tmp = new Vector();
    for (int i=0; i<layerInfoList[0].size(); i++) {
      tmp.add(layerInfoList[0].get(i));
    }

    core = new CoreMetadata(2);

    core.imageCount[0] = tmp.size();

    // determine if we have a multi-series file

    status("Determining series count");

    int oldChannels = openBytes(0).length / (core.sizeX[0] * core.sizeY[0] * 3);
    int oldWidth = core.sizeX[0];

    int oldSize = 0;
    for (int i=0; i<tmp.size(); i++) {
      LayerInfo layer = (LayerInfo) tmp.get(i);
      in.seek(layer.layerStart);

      long nextTag = readTagHeader();
      if (fmt.equals("PICT")) {
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

          if (core.sizeX[series] == 0) core.sizeX[series] = right - left;
          if (core.sizeY[series] == 0) core.sizeY[series] = bottom - top;
        }
        else {
          core.sizeX[series] = DataTools.read4SignedBytes(in, false);
          core.sizeY[series] = DataTools.read4SignedBytes(in, false);
        }

        in.seek(layer.layerStart);

        b = new byte[0];

        if (version == 2) {
          nextTag = readTagHeader();

          if ((tag != 67 && tag != 68) || !fmt.equals("PICT")) {
            throw new FormatException("Corrupt LIFF file.");
          }
          in.skipBytes(298);

          // open image using pict reader
          try {
            b = new byte[(int) (nextTag - in.getFilePointer())];
            in.read(b);
            BufferedImage img = pict.open(b);
            if (img.getRaster().getNumBands() != oldChannels ||
              img.getWidth() != oldWidth)
            {
              layerInfoList[1].add(tmp.get(i));
              layerInfoList[0].remove(tmp.get(i));
            }
          }
          catch (FormatException e) {
          }
        }
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
      core.sizeC = new int[1];
      core.sizeC[0] = layerInfoList[1].size() == 0 ? 1 : 3;
      if (core.sizeC[0] == 1 && oldChannels == 1) core.sizeC[0] = 3;

      int oldImages = core.imageCount[0];
      core.imageCount = new int[1];
      core.imageCount[0] = oldImages;
      if (layerInfoList[0].size() == 0) layerInfoList[0] = layerInfoList[1];
    }
    else {
      core.imageCount[0] = layerInfoList[0].size();
      core.imageCount[1] = layerInfoList[1].size();
      core.sizeC[0] = 1;
      core.sizeC[1] = 3;
      int oldW = core.sizeX[0];
      int oldH = core.sizeY[0];
      core.sizeX = new int[2];
      core.sizeY = new int[2];
      core.sizeX[0] = oldW;
      core.sizeX[1] = oldW;
      core.sizeY[0] = oldH;
      core.sizeY[1] = oldH;
    }

    status("Populating metadata");

    numSeries = core.imageCount.length;

    int[] bpp = new int[numSeries];

    Arrays.fill(core.orderCertain, true);

    int oldSeries = getSeries();
    for (int i=0; i<bpp.length; i++) {
      setSeries(i);
      bpp[i] = openBytes(0).length / (core.sizeX[i] * core.sizeY[i]);
    }
    setSeries(oldSeries);

    if (bytesPerPixel == 3) bytesPerPixel = 1;
    if (bytesPerPixel == 0) bytesPerPixel++;

    // finish populating metadata hashtable
    addMeta("Version", new Integer(version));
    addMeta("Number of Series", new Integer(numSeries));
    for (int i=0; i<numSeries; i++) {
      addMeta("Width (Series " + i + ")", new Integer(core.sizeX[i]));
      addMeta("Height (Series " + i + ")", new Integer(core.sizeY[i]));
      addMeta("Bit depth (Series " + i + ")",
        new Integer(bpp[i] * 8));
      addMeta("Number of channels (Series " + i + ")",
        new Integer(core.sizeC[i]));
      addMeta("Number of images (Series " + i + ")",
        new Integer(core.imageCount[i]));
    }

    // populate MetadataStore

    MetadataStore store = getMetadataStore();

    for (int i=0; i<numSeries; i++) {
      core.sizeT[i] += 1;
      core.sizeZ[i] = core.imageCount[i] / core.sizeT[i];
      core.currentOrder[i] = isRGB() ? "XYCZT" : "XYZCT";
      core.rgb[i] = core.sizeC[i] > 1;
      core.interleaved[i] = true;
      core.littleEndian[i] = false;

      try {
        if (i != 0) {
          if (bpp[i] == bpp[0]) bpp[i] = bpp[i + 1];
        }
      }
      catch (ArrayIndexOutOfBoundsException a) { }

      switch (bpp[i]) {
        case 1:
          core.pixelType[i] = FormatTools.INT8;
          break;
        case 2:
          core.pixelType[i] = FormatTools.UINT16;
          break;
        case 3:
          core.pixelType[i] = FormatTools.UINT8;
          break;
        case 4:
          core.pixelType[i] = FormatTools.INT32;
          break;
        case 6:
          core.pixelType[i] = FormatTools.INT16;
          break;
      }

      store.setImage("Series " + i, null, null, new Integer(i));
      store.setPixels(
        new Integer(core.sizeX[i]),
        new Integer(core.sizeY[i]),
        new Integer(core.sizeZ[i]),
        new Integer(core.sizeC[i]),
        new Integer(core.sizeT[i]),
        new Integer(core.pixelType[i]),
        new Boolean(!isLittleEndian()),
        core.currentOrder[i],
        new Integer(i),
        null);
      store.setDimensions(new Float(xCal), new Float(yCal), new Float(zCal),
        null, null, new Integer(i));
      for (int j=0; j<core.sizeC[0]; j++) {
        store.setLogicalChannel(j, null, null, null, null, null,
          null, new Integer(i));
      }
    }
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    if (super.isThisType(name, open)) return true; // check extension

    if (open) {
      byte[] b = new byte[8];
      try {
        in = new RandomAccessStream(name);
        in.read(b);
      }
      catch (IOException e) {
        if (debug) e.printStackTrace();
        return false;
      }
      return isThisType(b);
    }
    else {
      return name.indexOf(".") < 0; // file appears to have no extension
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

}
