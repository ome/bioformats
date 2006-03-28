//
// AndorReader.java
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

import java.io.*;
import java.util.Vector;

/**
 * Reader is the file format reader for
 * Andor Bio-imaging Division (ABD) TIFF files.
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class AndorReader extends BaseTiffReader {

  // -- Constants --

  /** Maximum number of bytes to check for Andor header information. */
  private static final int BLOCK_CHECK_LEN = 16384;

  /** Andor TIFF private IFD tags. */
  private static final int MMHEADER = 34361;
  private static final int MMSTAMP = 34362;


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
    if (ifdlocation < 0 || ifdlocation + 1 > block.length) { return false; }
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

  /** Checks if the given string is a valid filename for an Andor TIFF file. */
  public boolean isThisType(String name) {
    // just checking the filename isn't enough to differentiate between
    // Andor and regular TIFF; open the file and check more thoroughly
    long len = new File(name).length();
    int size = len < BLOCK_CHECK_LEN ? (int) len : BLOCK_CHECK_LEN;
    byte[] buf = new byte[size];
    try {
      FileInputStream fin = new FileInputStream(name);
      int r = 0;
      while (r < size) r += fin.read(buf, r, size - r);
      fin.close();
      return isThisType(buf);
    }
    catch (IOException e) { return false; }
  }


  // -- Internal BaseTiffReader API methods --

  /** Populate the metadata hashtable. */
  protected void initMetadata() {
    super.initMetadata();
    boolean little = true;
    try {
      little= TiffTools.isLittleEndian(ifds[0]);
    }
    catch (FormatException e) { }

    // look for MMHEADER
    short[] header = (short[]) TiffTools.getIFDValue(ifds[0], MMHEADER);

    if (header != null) {
      int pos = 3;
      metadata.put("Name", DataTools.bytesToString(header, pos, 256));
      pos += 256;
      metadata.put("Data flag",
        "" + DataTools.bytesToInt(header, pos, 4, little));
      pos += 4;  // data flag
      metadata.put("Number of colors",
        "" + DataTools.bytesToInt(header, pos, 4, little));
      if (DEBUG) {
        System.out.println("bytes for 'number of colors'");
        for (int i=pos; i<pos+4; i++) { System.out.print(header[i] + " "); }
        System.out.println();
      }
      pos += 4;
      if (DEBUG) {
        System.out.println("bytes for color and comment flags");
        for (int i=pos; i<pos+16; i++) { System.out.print(header[i] + " "); }
        System.out.println();
      }
      pos += 8;  // color flags
      pos += 8;  // comment flags
      int type = (int) header[pos];
      pos++;
      String imgType;
      switch (type) {
        case 1: imgType = "1 bit binary"; break;
        case 2: imgType = "4 bit binary"; break;
        case 3: imgType = "8 bit binary"; break;
        case 4: imgType = "8 bit greyscale"; break;
        case 5: imgType = "12 bit greyscale"; break;
        case 6: imgType = "16 bit greyscale"; break;
        case 7: imgType = "32 bit greyscale"; break;
        case 8: imgType = "64 bit greyscale"; break;
        case 9: imgType = "24 bit color"; break;
        case 10: imgType = "32 bit float"; break;
        case 11: imgType = "64 bit float"; break;
        default: imgType = "unknown";
      }
      metadata.put("Image type", imgType);

      // clear OME-XML dimension info for Z, C and T axes
      OMETools.setSizeZ(ome, 1);
      OMETools.setSizeC(ome, 1);
      OMETools.setSizeT(ome, 1);

      int sizeC = 1;

      for (int i=1; i<=10; i++) {
        if (DEBUG) {
          System.out.println("bytes for dimension " + i + " name");
          for (int j=pos; j<pos+64; j++) { System.out.print(header[j] + " "); }
          System.out.println();
          System.out.println("remainder of MM_DIM_INFO bytes for dim. " +i);
          for (int j=pos+64; j<pos+100; j++) {
            System.out.print(header[j] + " ");
          }
          System.out.println();
        }

        // name is supposed to be 64 bytes but in practice appears to be 16
        String name = DataTools.bytesToString(header, pos, 16);
        metadata.put("Dimension " + i + " Name", name);
        pos += 16;
        int size = DataTools.bytesToInt(header, pos, little);
        metadata.put("Dimension " + i + " Size", "" + size);
        pos += 4;
        metadata.put("Dimension " + i + " Origin",
          "" + Double.longBitsToDouble(
          DataTools.bytesToLong(header, pos, little)));
        pos += 8;
        metadata.put("Dimension " + i + " Resolution",
          "" + Double.longBitsToDouble(
          DataTools.bytesToLong(header, pos, little)));
        pos += 8;
        metadata.put("Dimension " + i + " Calibration units",
          "" + DataTools.bytesToString(header, pos, 16));
        pos += 16;
        // skip last 48 bytes (adjust for name discrepancy)
        pos += 48;

        // set OME-XML dimensions appropriately

        if (name.equals("Z")) OMETools.setSizeZ(ome, size);
        else if (name.equals("Time")) OMETools.setSizeT(ome, size);
        else if (!name.trim().equals("") && !name.equals("x") &&
          !name.equals("y"))
        {
          sizeC *= size;
        }
      }
      OMETools.setSizeC(ome, sizeC);
    }

    // parse stamp value, a sequence of 8 doubles representing the
    // spatial position (3rd to 10th dimension) of the image plane
    for (int j=0; j<ifds.length; j++) {
      short[] stamp = (short[]) TiffTools.getIFDValue(ifds[j], MMSTAMP);
      if (stamp == null || stamp.length != 64) continue;
      StringBuffer dataStamp = new StringBuffer();
      for (int i=0; i<8; i++) {
        if (i > 0) dataStamp.append(", ");
        String name = (String) metadata.get("Dimension " + (i + 3) + " Name");
        if (name == null || name.equals("")) break; // no more dimensions
        dataStamp.append(name);
        dataStamp.append("=");
        dataStamp.append(Double.longBitsToDouble(
          DataTools.bytesToLong(stamp, 8*i, little)));
      }
      metadata.put("Data Stamp for plane #" + (j + 1), dataStamp.toString());
    }

    // compute the dimension order from the data stamps

    // we should be able to get a pretty accurate result by looking at no more
    // than the first 10 planes

    int numPlanes = 10;
    if (ifds.length < 10) numPlanes = ifds.length;
    String[] values = new String[numPlanes];
    for (int i=0; i<numPlanes; i++) {
      values[i] = (String) metadata.get("Data Stamp for plane #" + (i+1));
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

    String order = "XY";
    int[] dimOrder = new int[diff.size()];
    boolean[] added = new boolean[diff.size()];
    for (int i=0; i<dimOrder.length; i++) {
      float min = 0;
      int index = 0;
      for (int j=0; j<diff.size(); j++) {
        // find the minimum non-zero difference
        float difference = ((Float) diff.get(i)).floatValue();
        if (difference != 0 && difference < min && added[i] == false) {
          min = difference;
          index = i;
        }
      }
      dimOrder[i] = index;
      added[index] = true;
    }

    for (int i=0; i<dimOrder.length; i++) {
      String name = names[dimOrder[i]].trim();
      if (name.equals("Z") && order.indexOf("Z") < 0) order = order + "Z";
      else if (name.equals("Time") && order.indexOf("T") < 0) order=order+"T";
      else if (order.indexOf("C") < 0) order = order + "C";
    }

    if (order.length() == 4) {
      if (order.indexOf("Z") < 0) order = order + "Z";
      else if (order.indexOf("T") < 0) order = order + "T";
      else if (order.indexOf("C") < 0) order = order + "C";
    }

    OMETools.setDimensionOrder(ome, order);
  }


  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new AndorReader().testRead(args);
  }

}
