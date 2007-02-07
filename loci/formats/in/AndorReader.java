//
// AndorReader.java
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

import java.io.*;
import java.util.Vector;
import loci.formats.*;

/**
 * AndorReader is the file format reader for
 * Andor Bio-imaging Division (ABD) TIFF files.
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class AndorReader extends BaseTiffReader {

  // -- Constants --

  /** Maximum number of bytes to check for Andor header information. */
  private static final int BLOCK_CHECK_LEN = 16384;

  /** Andor TIFF private IFD tags. */
  private static final int MMHEADER = 34361;
  private static final int MMSTAMP = 34362;

  /** The dimension order of the file */
  private String order;

  /** Valid bits per pixel */
  private int[] validBits;

  // -- Constructor --

  /** Constructs a new Andor reader. */
  public AndorReader() {
    super("Andor Bio-imaging Division TIFF", new String[] {"tif", "tiff"});
  }

  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for an Andor TIFF file. */
  public boolean isThisType(byte[] block) {
    // adapted from MetamorphReader.isThisType(byte[])

    if (block.length < 3) return false;
    if (block.length < 8) return true; // we have no way of verifying further

    int ifdlocation = DataTools.bytesToInt(block, 4 , true);
    if (ifdlocation < 0 || ifdlocation + 1 > block.length) return false;
    else {
      int ifdnumber = DataTools.bytesToInt(block, ifdlocation, 2, true);
      for (int i=0; i<ifdnumber; i++) {
        if (ifdlocation + 3 + (i*12) > block.length) return false;
        else {
          int ifdtag = DataTools.bytesToInt(block,
            ifdlocation + 2 + (i*12), 2, true);
          if (ifdtag == MMHEADER || ifdtag == MMSTAMP) return true;
        }
      }
      return false;
    }
  }

  // -- FormatHandler API methods --

  /**
   * Checks if the given string is a valid filename for an Andor TIFF file.
   * @param open If true, the (existing) file is opened for further analysis,
   *   since the file extension is insufficient to confirm that the file is in
   *   Andor TIFF format.
   */
  public boolean isThisType(String name, boolean open) {
    if (!super.isThisType(name, open)) return false; // check extension

    // just checking the filename isn't enough to differentiate between
    // Andor and regular TIFF; open the file and check more thoroughly
    return open ? checkBytes(name, BLOCK_CHECK_LEN) : true;
  }

  // -- Internal BaseTiffReader API methods --

  /* @see loci.formats.BaseTiffReader#initStandardMetadata() */
  protected void initStandardMetadata() throws FormatException {
    super.initStandardMetadata();

    boolean little = false;
    try {
      little = isLittleEndian(currentId);
    }
    catch (IOException e) {
      throw new FormatException(e);
    }

    int vb = 0; // number of valid bits per pixel

    // look for MMHEADER
    short[] header = (short[]) TiffTools.getIFDValue(ifds[0], MMHEADER);

    // The following is now initialized before the super-class "initMetadata()"
    // method because of the need for overridden "getSizeZ()", "getSizeC()",
    // "getSizeT()" and "getDimensionOrder()".
    if (header != null) {
      int pos = 3;
      addMeta("Name", DataTools.bytesToString(header, pos, 256));
      pos += 256;
      addMeta("Data flag", "" + DataTools.bytesToInt(header, pos, 4, little));
      pos += 4;  // data flag
      addMeta("Number of colors",
        "" + DataTools.bytesToInt(header, pos, 4, little));
      if (debug && debugLevel >= 2) {
        debug("bytes for 'number of colors'");
        StringBuffer sb = new StringBuffer();
        for (int i=pos; i<pos+4; i++) sb.append(header[i] + " ");
        debug(sb.toString());
      }
      pos += 4;
      if (debug && debugLevel >= 2) {
        debug("bytes for color and comment flags");
        StringBuffer sb = new StringBuffer();
        for (int i=pos; i<pos+16; i++) sb.append(header[i] + " ");
        debug(sb.toString());
      }
      pos += 8;  // color flags
      pos += 8;  // comment flags
      int type = (int) header[pos];
      pos++;
      String imgType;

      switch (type) {
        case 1:
          imgType = "1 bit binary";
          vb = 1;
          break;
        case 2:
          imgType = "4 bit binary";
          vb = 4;
          break;
        case 3:
          imgType = "8 bit binary";
          vb = 8;
          break;
        case 4:
          imgType = "8 bit greyscale";
          vb = 8;
          break;
        case 5:
          imgType = "12 bit greyscale";
          vb = 12;
          break;
        case 6:
          imgType = "16 bit greyscale";
          vb = 16;
          break;
        case 7:
          imgType = "32 bit greyscale";
          vb = 32;
          break;
        case 8:
          imgType = "64 bit greyscale";
          vb = 64;
          break;
        case 9:
          imgType = "24 bit color";
          vb = 8;
          break;
        case 10:
          imgType = "32 bit float";
          vb = 32;
          break;
        case 11:
          imgType = "64 bit float";
          vb = 64;
          break;
        default:
          imgType = "unknown";
      }
      addMeta("Image type", imgType);

      sizeT[0] = 1;

      for (int i=1; i<=10; i++) {
        if (debug && debugLevel >= 2) {
          debug("bytes for dimension " + i + " name");
          StringBuffer sb = new StringBuffer();
          for (int j=pos; j<pos+64; j++) sb.append(header[j] + " ");
          debug(sb.toString());
          debug("remainder of MM_DIM_INFO bytes for dim. " + i);
          sb.setLength(0);
          for (int j=pos+64; j<pos+100; j++) sb.append(header[j] + " ");
          debug(sb.toString());
        }

        // name is supposed to be 64 bytes but in practice appears to be 16
        String name = DataTools.bytesToString(header, pos, 16);
        addMeta("Dimension " + i + " Name", name);
        pos += 16;
        int size = DataTools.bytesToInt(header, pos, little);
        addMeta("Dimension " + i + " Size", "" + size);
        pos += 4;
        addMeta("Dimension " + i + " Origin", "" +
          Double.longBitsToDouble(DataTools.bytesToLong(header, pos, little)));
        pos += 8;
        addMeta("Dimension " + i + " Resolution", "" +
          Double.longBitsToDouble(DataTools.bytesToLong(header, pos, little)));
        pos += 8;
        addMeta("Dimension " + i + " Calibration units",
          "" + DataTools.bytesToString(header, pos, 16));
        pos += 16;
        // skip last 48 bytes (adjust for name discrepancy)
        pos += 48;

        // set OME-XML dimensions appropriately

        name = name.trim();
        if (name.equals("Z")) sizeZ[series] = size;
        else if (name.equals("Time")) sizeT[series] *= size;
        else if (name.equals("Wavelength")) sizeC[series] = size;
        else if (!name.trim().equals("") && !name.toLowerCase().equals("x") &&
          !name.toLowerCase().equals("y"))
        {
          sizeT[series] *= size;
        }
      }
    }

    // parse stamp value, a sequence of 8 doubles representing the
    // spatial position (3rd to 10th dimension) of the image plane
    for (int j=0; j<ifds.length; j++) {
      short[] stamp = (short[]) TiffTools.getIFDValue(ifds[j], MMSTAMP);
      if (stamp == null || stamp.length != 64) continue;
      StringBuffer dataStamp = new StringBuffer();
      for (int i=0; i<8; i++) {
        if (i > 0) dataStamp.append(", ");
        String name = (String) getMeta("Dimension " + (i + 3) + " Name");
        if (name == null || name.equals("")) break; // no more dimensions
        dataStamp.append(name);
        dataStamp.append("=");
        dataStamp.append(Double.longBitsToDouble(
          DataTools.bytesToLong(stamp, 8*i, little)));
      }
      addMeta("Data Stamp for plane #" + (j + 1), dataStamp.toString());
    }

    // compute the dimension order from the data stamps

    // we should be able to get a pretty accurate result by looking at no more
    // than the first 10 planes

    int numPlanes = 10;
    if (ifds.length < 10) numPlanes = ifds.length;
    String[] values = new String[numPlanes];
    for (int i=0; i<numPlanes; i++) {
      values[i] = (String) getMeta("Data Stamp for plane #" + (i+1));
    }

    // determine the number of '=' characters in a data stamp
    int numDimensions = 0;
    for (int i=0; i<values[0].length(); i++) {
      if(values[0].charAt(i) == '=') numDimensions++;
    }

    float[][] dims = new float[numPlanes][numDimensions];
    String[] names = new String[numDimensions];
    String val = "";
    // parse the dimension coordinates for each plane
    for (int i=0; i<numPlanes; i++) {
      for (int j=0; j<numDimensions; j++) {
        if(i==0) names[j] = values[i].substring(0, values[i].indexOf("="));
        val = values[i].substring(values[i].indexOf("=")+1,
          values[i].indexOf(","));
        dims[i][j] = Float.parseFloat(val);
        values[i] = values[i].substring(values[i].indexOf(",") + 1);
      }
    }

    // determine the average difference in each dimension's coordinates
    Vector diff = new Vector();
    for (int i=0; i<numDimensions-1; i++) {
      float differences = 0;
      for (int j=0; j<numPlanes; j++) {
        differences += (dims[j][i+1] - dims[j][i]);
      }
      diff.add(new Float(differences / numPlanes));
    }

    int[] dimOrder = new int[diff.size()];
    boolean[] added = new boolean[diff.size()];
    for (int i=0; i<dimOrder.length; i++) {
      float min = 0;
      int index = 0;
      for (int j=0; j<diff.size(); j++) {
        // find the minimum non-zero difference
        float difference = ((Float) diff.get(i)).floatValue();
        if (difference != 0 && difference < min && !added[i]) {
          min = difference;
          index = i;
        }
      }
      dimOrder[i] = index;
      added[index] = true;
    }

    order = "XY";
    if (sizeC[0] > 1) order += "C";
    if (sizeZ[0] > sizeT[0]) order += "TZ";
    else order += "ZT";
    if (order.length() == 4) order += "C";

    currentOrder[series] = order;

    orderCertain[series] = true;

    validBits = new int[sizeC[0]];
    if (validBits.length == 2) validBits = new int[3];
    for (int i=0; i<validBits.length; i++) validBits[i] = vb;
    for (int i=0; i<ifds.length; i++) {
      ifds[i].put(new Integer(TiffTools.VALID_BITS), validBits);
    }
  }

  protected void initMetadataStore() {
    super.initMetadataStore();

    float pixelSizeX = 0.0f, pixelSizeY = 0.0f, pixelSizeZ = 0.0f;

    for (int i=1; i<10; i++) {
      String name = (String) getMeta("Dimension " + i + " Name");
      String size = (String) getMeta("Dimension " + i + " Resolution");

      if (name != null && size != null) {
        if (name.equals("x")) pixelSizeX = Float.parseFloat(size);
        else if (name.equals("y")) pixelSizeY = Float.parseFloat(size);
        else if (name.equals("z")) pixelSizeZ = Float.parseFloat(size);
      }
    }

    try {
      MetadataStore store = getMetadataStore(currentId);
      store.setDimensions(new Float(pixelSizeX), new Float(pixelSizeY),
        new Float(pixelSizeZ), null, null, null);
    }
    catch (FormatException e) {
      if (debug) e.printStackTrace();
    }
    catch (IOException e) {
      if (debug) e.printStackTrace();
    }
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new AndorReader().testRead(args);
  }

}
