//
// FluoviewReader.java
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

import java.io.*;
import java.util.Hashtable;
import java.util.Vector;

/**
 * FluoviewReader is the file format reader for
 * Olympus Fluoview TIFF files.
 *
 * @author Eric Kjellman egkjellman at wisc.edu
 * @author Melissa Linkert linkert at cs.wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class FluoviewReader extends BaseTiffReader {

  // -- Constants --

  /** Maximum number of bytes to check for Fluoview header information. */
  private static final int BLOCK_CHECK_LEN = 16384;

  /** String identifying a Fluoview file. */
  private static final String FLUOVIEW_MAGIC_STRING = "FLUOVIEW";

  /** Fluoview TIFF private tags */
  private static final int MMHEADER = 34361;
  private static final int MMSTAMP = 34362;
  private static final int MMUSERBLOCK = 34386;


  // -- Constructor --

  /** Constructs a new Fluoview TIFF reader. */
  public FluoviewReader() {
    super("Olympus Fluoview TIFF", new String[] {"tif", "tiff"});
  }


  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for a Fluoview TIFF file. */
  public boolean isThisType(byte[] block) {
    if (!TiffTools.isValidHeader(block)) return false;

    // if this file is a Fluoview TIFF file, it should have 42
    // for the 3rd byte, and contain the text "FLUOVIEW"
    String test = new String(block);
    return test.indexOf(FLUOVIEW_MAGIC_STRING) != -1;
  }

  /**
   * Checks if the given string is a valid filename for a Fluoview TIFF file.
   */
  public boolean isThisType(String name) {
    // just checking the filename isn't enough to differentiate between
    // Fluoview and regular TIFF; open the file and check more thoroughly
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

  /** Populates the metadata hashtable. */
  protected void initMetadata() {
    super.initMetadata();

    try {
      Hashtable ifd = ifds[0];

      // determine byte order
      boolean little = TiffTools.isLittleEndian(ifd);

      // set file pointer to start reading MM_HEAD metadata
      short[] mmHead = TiffTools.getIFDShortArray(ifd, MMHEADER, false);
      int p = 0; // pointer to next byte in mmHead

      // -- Parse standard metadata --

      put("HeaderSize", DataTools.bytesToInt(mmHead, p, 2, little));
      p += 2;
      put("Status", DataTools.bytesToString(mmHead, p, 1));
      p++;

      // change from the specs: using 257 bytes instead of 256
      put("ImageName", DataTools.bytesToString(mmHead, p, 257));
      p += 257 + 4; // there are 4 bytes that we don't need

      put("NumberOfColors", DataTools.bytesToLong(mmHead, p, 4, little));
      p += 4 + 8; // again, 8 bytes we don't need

      // don't add commentSize and commentOffset to hashtable
      // these will be used later to read in the Comment field
      // and add it to the hashtable
      long commentSize = DataTools.bytesToLong(mmHead, p, 4, little);
      p += 4;
      long commentOffset = DataTools.bytesToLong(mmHead, p, 4, little);
      p += 4;

      // dimensions info
      // there are 10 blocks of dimension info to be read,
      // each with the same structure
      // in the hashtable, the same tags in different blocks
      // are distinguished by appending the block number to the
      // tag name
      for (int j=0; j<10; j++) {
        put("DimName" + j, DataTools.bytesToString(mmHead, p, 16));
        p += 16;
        put("Size" + j, DataTools.bytesToLong(mmHead, p, 4, little));
        p += 4;
        put("Origin" + j, Double.longBitsToDouble(
          DataTools.bytesToLong(mmHead, p, little)));
        p += 8;
        put("Resolution" + j, Double.longBitsToDouble(
          DataTools.bytesToLong(mmHead, p, little)));
        p += 8;
      }

      put("MapType", DataTools.bytesToInt(mmHead, p, 2, little));
      p += 2 + 2; // 2 bytes we don't need
      put("MapMin", Double.longBitsToDouble(
        DataTools.bytesToLong(mmHead, p, little)));
      p += 8;
      put("MapMax", Double.longBitsToDouble(
        DataTools.bytesToLong(mmHead, p, little)));
      p += 8;
      put("MinValue", Double.longBitsToDouble(
        DataTools.bytesToLong(mmHead, p, little)));
      p += 8;
      put("MaxValue", Double.longBitsToDouble(
        DataTools.bytesToLong(mmHead, p, little)));
      p += 8 + 4; // skipping over 4 bytes
      put("Gamma", Double.longBitsToDouble(
        DataTools.bytesToLong(mmHead, p, little)));
      p += 8;
      put("Offset", Double.longBitsToDouble(
        DataTools.bytesToLong(mmHead, p, little)));
      p += 8;

      // get Gray dimension info

      put("DimName11", DataTools.bytesToString(mmHead, p, 16));
      p += 16;
      put("Size11", DataTools.bytesToLong(mmHead, p, 4, little));
      p += 4;
      put("Origin11", Double.longBitsToDouble(
        DataTools.bytesToLong(mmHead, p, little)));
      p += 8;
      put("Resolution11", Double.longBitsToDouble(
        DataTools.bytesToLong(mmHead, p, little)));

      // read in comments field
      if (commentSize > 0) {
        in.seek(commentOffset);
        byte[] comments = new byte[(int) commentSize];
        in.read(comments);
        put("Comments", new String(comments));
      }


      // -- Parse OME-XML metadata --

      Object off;
      String data;
      long newNum = 0;
      Object obj = new Object();
      double origin = 0;


      // set file to the right place
      off = (Object) ifd.get(new Integer(MMHEADER));
      if (off != null) {
        // read the metadata
        OMETools.setAttribute(ome, "Image", "ImageName",
          "" + metadata.get("ImageName"));
        byte[] temp2 = new byte[279];
        in.read(temp2);
        char[] dimName;
        for (int j=0; j<10; j++) {
          dimName = new char[16];
          for (int i=0; i<16; i++) {
            dimName[i] = in.readChar();
          }

          String attr = "";
          switch (j) {
            case 1: attr = "X"; break;
            case 2: attr = "Y"; break;
            case 3: attr = "Z"; break;
            case 4: attr = "T"; break;
            case 5: attr = "C"; break;
          }


          newNum = DataTools.read4SignedBytes(in, little);
          origin = DataTools.readDouble(in, little);
          if (!attr.equals("T") && !attr.equals("C") && !attr.equals("")) {
            OMETools.setAttribute(ome, "StageLabel", attr, "" + origin);
          }

          DataTools.readDouble(in, little); // skip next double
        }
      }

      String descr = "" + TiffTools.getIFDValue(ifds[0],
        TiffTools.IMAGE_DESCRIPTION, false, String.class);
      // strip LUT data from image description
      int firstIndex = descr.indexOf("[LUT Ch");
      int lastIndex = descr.lastIndexOf("[LUT Ch") + 13;
      descr = descr.substring(0, firstIndex) + descr.substring(lastIndex);
      OMETools.setAttribute(ome, "Image", "Description", descr);


      String d = (String) TiffTools.getIFDValue(ifds[0], TiffTools.PAGE_NAME);
      int strPos = d.indexOf("[Higher Dimensions]") + 19;
      d = d.substring(strPos);

      String names = d.substring(5, d.indexOf("Spatial Position"));
      String positions = d.substring(d.indexOf("Number Of Positions") + 19);
      names = names.trim();
      positions = positions.trim();

      // first parse the names
      Vector n = new Vector();
      Vector chars = new Vector();
      for(int i=0; i<names.length(); i++) {
        if (!Character.isWhitespace(names.charAt(i))) {
          chars.add(new Character(names.charAt(i)));
        }
        else {
          if (chars.size() > 0) {
            char[] dim = new char[chars.size()];
            for(int j=0; j<chars.size(); j++) {
              dim[j] = ((Character) chars.get(j)).charValue();
            }
            n.add(new String(dim));
          }
          chars.clear();
        }
      }

      if (chars.size() > 0) {
        char[] dim = new char[chars.size()];
        for (int j=0; j<chars.size(); j++) {
          dim[j] = ((Character) chars.get(j)).charValue();
        }
        n.add(new String(dim));
      }

      // now parse the number of positions for each dimension

      Vector numPlanes = new Vector();
      chars = new Vector();

      for(int i=0; i< positions.length(); i++) {
        if (!Character.isWhitespace(positions.charAt(i))) {
          chars.add(new Character(positions.charAt(i)));
        }
        else {
          if (chars.size() > 0) {
            char[] dim = new char[chars.size()];
            for (int j=0; j<chars.size(); j++) {
              dim[j] = ((Character) chars.get(j)).charValue();
            }
            numPlanes.add(new String(dim));
          }
          chars.clear();
        }
      }

      if (chars.size() > 0) {
        char[] dim = new char[chars.size()];
        for (int j=0; j<chars.size(); j++) {
          dim[j] = ((Character) chars.get(j)).charValue();
        }
        numPlanes.add(new String(dim));
      }

      // set the OME-XML dimension attributes appropriately

      // first we need to reset the dimensions
      OMETools.setAttribute(ome, "Pixels", "SizeZ", "1");
      OMETools.setAttribute(ome, "Pixels", "SizeC", "1");
      OMETools.setAttribute(ome, "Pixels", "SizeT", "1");

      for(int i=0; i<n.size(); i++) {
        String name = (String) n.get(i);
        String pos = (String) numPlanes.get(i);

        if (name.equals("Ch")) {
          OMETools.setAttribute(ome, "Pixels", "SizeC", pos);
        }
        else if (name.equals("Animation") || name.equals("T")) {
          OMETools.setAttribute(ome, "Pixels", "SizeT", pos);
        }
        else if (name.equals("Z")) {
          OMETools.setAttribute(ome, "Pixels", "SizeZ", pos);
        }
      }
    }
    catch (NullPointerException e) { /* most likely MMHEADER not found */ }
    catch (IOException e) { e.printStackTrace(); }
    catch (FormatException e) { e.printStackTrace(); }
  }


  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new FluoviewReader().testRead(args);
  }

}
