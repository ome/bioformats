//
// LIFReader.java
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
import java.util.*;
import loci.formats.*;

/**
 * LIFReader is the file format reader for Leica LIF files.
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class LIFReader extends FormatReader {

  // -- Fields --

  /** Offsets to memory blocks, paired with their corresponding description. */
  protected Vector offsets;

  /** Bits per pixel. */
  private int[] bitsPerPixel;

  /** Extra dimensions. */
  private int[] extraDimensions;

  /** Number of valid bits per pixel */
  private int[][] validBits;

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

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return block[0] == 0x70;
  }

  /* @see loci.formats.IFormatReader#getSeriesCount() */
  public int getSeriesCount() throws FormatException, IOException {
    return core.sizeX.length;
  }

  /* @see loci.formats.IFormatReader#openBytes(int) */
  public byte[] openBytes(int no) throws FormatException, IOException {
    bpp = bitsPerPixel[series];
    while (bpp % 8 != 0) bpp++;
    byte[] buf = new byte[core.sizeX[series] * core.sizeY[series] *
      (bpp / 8) * getRGBChannelCount()];
    return openBytes(no, buf);
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    if (no < 0 || no >= getImageCount()) {
      throw new FormatException("Invalid image number: " + no);
    }
    bpp = bitsPerPixel[series];
    while (bpp % 8 != 0) bpp++;
    int bytes = bpp / 8;
    if (buf.length < core.sizeX[series] * core.sizeY[series] * bytes *
      getRGBChannelCount())
    {
      throw new FormatException("Buffer too small.");
    }

    long offset = ((Long) offsets.get(series)).longValue();
    in.seek(offset + core.sizeX[series] * core.sizeY[series] *
      bytes * no * getRGBChannelCount());

    in.read(buf);
    return buf;
  }

  /* @see loci.formats.IFormatReader#openImage(int) */
  public BufferedImage openImage(int no) throws FormatException, IOException {
    return ImageTools.makeImage(openBytes(no), core.sizeX[series],
      core.sizeY[series], isRGB() ? core.sizeC[series] : 1, false, bpp / 8,
      core.littleEndian[series], validBits[series]);
  }

  /** Initializes the given LIF file. */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("LIFReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);
    offsets = new Vector();

    core.littleEndian[0] = true;

    xcal = new Vector();
    ycal = new Vector();
    zcal = new Vector();
    channelMins = new Vector();
    channelMaxs = new Vector();

    // read the header

    status("Reading header");

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

    int nc = DataTools.read4SignedBytes(in, core.littleEndian[0]);
    byte[] s = new byte[nc * 2];
    in.read(s);
    String xml = DataTools.stripString(new String(s));

    status("Finding image offsets");

    while (in.getFilePointer() < in.length()) {
      if (DataTools.read4SignedBytes(in, core.littleEndian[0]) != 0x70) {
        throw new FormatException("Invalid Memory Block");
      }

      in.skipBytes(4);
      if (in.read() != 0x2a) {
        throw new FormatException("Invalid Memory Description");
      }

      int blockLength = DataTools.read4SignedBytes(in, core.littleEndian[0]);
      if (in.read() != 0x2a) {
        throw new FormatException("Invalid Memory Description");
      }

      int descrLength = DataTools.read4SignedBytes(in, core.littleEndian[0]);
      byte[] memDescr = new byte[2*descrLength];
      in.read(memDescr);

      if (blockLength > 0) {
        offsets.add(new Long(in.getFilePointer()));
      }

      in.skipBytes(blockLength);
    }
    initMetadata(xml);
  }

  // -- Helper methods --

  /** Parses a string of XML and puts the values in a Hashtable. */
  private void initMetadata(String xml) throws FormatException, IOException {
    Vector elements = new Vector();
    seriesNames = new Vector();

    status("Populating native metadata");

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
    addMeta(key, value);

    // what we have right now is a vector of XML elements, which need to
    // be parsed into the appropriate image dimensions

    int ndx = 1;

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

    String prefix = "";

    while (ndx < elements.size()) {
      token = (String) elements.get(ndx);

      // if the element contains a key/value pair, parse it and put it in
      // the metadata hashtable

      String tmpToken = token;
      if (token.indexOf("=") != -1) {
        int idx = token.indexOf("Identifier") + 12;
        key = token.substring(idx, token.indexOf("\"", idx + 1));
        idx = token.indexOf("Variant") + 9;
        value = token.substring(idx, token.indexOf("\"", idx + 1));
        if (key.indexOf("=") == -1) addMeta(prefix + " - " + key, value);
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
        else if (token.indexOf("dblVoxel") != -1) {
          int index = token.indexOf("Variant") + 7;
          String size = token.substring(index + 2,
            token.indexOf("\"", index + 3));
          float cal = Float.parseFloat(size) * 1000000;
          if (token.indexOf("X") != -1) xcal.add(new Float(cal));
          else if (token.indexOf("Y") != -1) ycal.add(new Float(cal));
          else if (token.indexOf("Z") != -1) zcal.add(new Float(cal));
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

            if (token.startsWith("Element Name")) {
              // hack to override first series name
              seriesNames.setElementAt(token.substring(token.indexOf("=") + 2,
                token.length() - 1), seriesNames.size() - 1);
              prefix = (String) seriesNames.get(seriesNames.size() - 1);
            }

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
                String sMin = (String) tmp.get("Min");
                String sMax = (String) tmp.get("Max");
                if (sMin != null && sMax != null) {
                  double min = Double.parseDouble(sMin);
                  double max = Double.parseDouble(sMax);
                  channelMins.add(new Integer((int) min));
                  channelMaxs.add(new Integer((int) max));
                }
              }
            }
            else if (tmp.get("DimensionDescription DimID") != null) {
              // found dimension description block

              int w = Integer.parseInt((String) tmp.get("NumberOfElements"));
              int id = Integer.parseInt((String)
                tmp.get("DimensionDescription DimID"));

              switch (id) {
                case 1:
                  widths.add(new Integer(w));
                  break;
                case 2:
                  heights.add(new Integer(w));
                  break;
                case 3:
                  zs.add(new Integer(w));
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
          if (elements != null && ndx < elements.size()) {
            token = (String) elements.get(ndx);
          }
          else break;
        }
        extraDims.add(new Integer(extras));
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

    bitsPerPixel = new int[numDatasets];
    extraDimensions = new int[numDatasets];

    // Populate metadata store

    status("Populating metadata");

    // The metadata store we're working with.
    MetadataStore store = getMetadataStore();

    core = new CoreMetadata(numDatasets);
    Arrays.fill(core.orderCertain, true);
    validBits = new int[numDatasets][];

    for (int i=0; i<numDatasets; i++) {
      core.sizeX[i] = ((Integer) widths.get(i)).intValue();
      core.sizeY[i] = ((Integer) heights.get(i)).intValue();
      core.sizeZ[i] = ((Integer) zs.get(i)).intValue();
      core.sizeC[i] = ((Integer) channels.get(i)).intValue();
      core.sizeT[i] = ((Integer) ts.get(i)).intValue();
      core.currentOrder[i] =
        (core.sizeZ[i] > core.sizeT[i]) ? "XYCZT" : "XYCTZ";

      bitsPerPixel[i] = ((Integer) bps.get(i)).intValue();
      extraDimensions[i] = ((Integer) extraDims.get(i)).intValue();

      if (extraDimensions[i] > 1) {
        if (core.sizeZ[i] == 1) core.sizeZ[i] = extraDimensions[i];
        else core.sizeT[i] *= extraDimensions[i];
        extraDimensions[i] = 1;
      }

      core.littleEndian[i] = true;
      core.rgb[i] = core.sizeC[i] > 1 && core.sizeC[i] < 4;
      core.interleaved[i] = true;
      core.imageCount[i] = core.sizeZ[i] * core.sizeT[i];
      if (!core.rgb[i]) core.imageCount[i] *= core.sizeC[i];

      validBits[i] = new int[core.sizeC[i] != 2 ? core.sizeC[i] : 3];
      for (int j=0; j<validBits[i].length; j++) {
        validBits[i][j] = bitsPerPixel[i];
      }

      while (bitsPerPixel[i] % 8 != 0) bitsPerPixel[i]++;
      switch (bitsPerPixel[i]) {
        case 8:
          core.pixelType[i] = FormatTools.UINT8;
          break;
        case 16:
          core.pixelType[i] = FormatTools.UINT16;
          break;
        case 32:
          core.pixelType[i] = FormatTools.FLOAT;
          break;
      }

      Integer ii = new Integer(i);

      store.setImage((String) seriesNames.get(i), null, null, ii);

      store.setPixels(
        new Integer(core.sizeX[i]), // SizeX
        new Integer(core.sizeY[i]), // SizeY
        new Integer(core.sizeZ[i]), // SizeZ
        new Integer(core.sizeC[i]), // SizeC
        new Integer(core.sizeT[i]), // SizeT
        new Integer(core.pixelType[i]), // PixelType
        new Boolean(!core.littleEndian[i]), // BigEndian
        core.currentOrder[i], // DimensionOrder
        ii, // Image index
        null); // Pixels index

      Float xf = i < xcal.size() ? (Float) xcal.get(i) : null;
      Float yf = i < ycal.size() ? (Float) ycal.get(i) : null;
      Float zf = i < zcal.size() ? (Float) zcal.get(i) : null;

      store.setDimensions(xf, yf, zf, null, null, ii);
      for (int j=0; j<core.sizeC[i]; j++) {
        store.setLogicalChannel(j, null, null, null, null, null, null, ii);
      }

      String zoom =
        (String) getMeta((String) seriesNames.get(i) + " - dblZoom");
      store.setDisplayOptions(zoom == null ? null : new Float(zoom),
        new Boolean(core.sizeC[i] > 1), new Boolean(core.sizeC[i] > 1),
        new Boolean(core.sizeC[i] > 2), new Boolean(isRGB()), null,
        null, null, null, null, ii, null, null, null, null, null);
    }
  }

}
