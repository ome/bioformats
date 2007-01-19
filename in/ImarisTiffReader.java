//
// ImarisTiffReader.java
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

import java.io.*;
import java.util.*;
import loci.formats.*;

/**
 * ImarisTiffReader is the file format reader for
 * Imaris 5 files (TIFF variant).
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class ImarisTiffReader extends BaseTiffReader {

  // -- Constructor --

  /** Constructs a new Imaris TIFF reader. */
  public ImarisTiffReader() {
    super("Imaris 5 (TIFF)", "ims");
  }

  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for an Imaris TIFF file. */
  public boolean isThisType(byte[] block) {
    // adapted from MetamorphReader.isThisType(byte[])
    if (block.length < 3) return false;
    if (block.length < 8) {
      return true; // we have no way of verifying further
    }

    boolean little = (block[0] == 0x49 && block[1] == 0x49);

    int ifdlocation = DataTools.bytesToInt(block, 4, little);
    if (ifdlocation < 0) return false;
    else if (ifdlocation + 1 > block.length) return true;
    else {
      int ifdnumber = DataTools.bytesToInt(block, ifdlocation, 2, little);
      for (int i=0; i<ifdnumber; i++) {
        if (ifdlocation + 3 + (i*12) > block.length) {
          return false;
        }
        else {
          int ifdtag = DataTools.bytesToInt(block,
            ifdlocation + 2 + (i*12), 2, little);
          if (ifdtag == TiffTools.TILE_WIDTH) {
            return true;
          }
        }
      }
      return false;
    }
  }

  // -- FormatHandler API methods --

  /**
   * Checks if the given string is a valid filename for an Imaris TIFF file.
   * @param open If true, and the file extension is insufficient to determine
   *  the file type, the (existing) file is opened for further analysis.
   */
  public boolean isThisType(String name, boolean open) {
    if (!super.isThisType(name, open)) return false; // check extension

    // just checking the filename isn't enough to differentiate between
    // Andor and regular TIFF; open the file and check more thoroughly
    return open ? checkBytes(name, 1024) : true;
  }

  // -- Internal BaseTiffReader API methods --

  /* @see BaseTiffReader#getImageCount(String) */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return numImages;
  }

  /* @see BaseTiffReader#isInterleaved(String) */
  public boolean isInterleaved(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return false;
  }

  /* @see BaseTiffReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("initFile(" + id + ")");
    super.initFile(id);
    super.initMetadata();

    close();
    currentId = id;
    //metadata = new Hashtable();

    sizeX = new int[1];
    sizeY = new int[1];
    sizeZ = new int[1];
    sizeC = new int[1];
    sizeT = new int[1];
    pixelType = new int[1];
    currentOrder = new String[1];
    orderCertain = new boolean[] {true};
    getMetadataStore(id).createRoot();

    channelMinMax = null;
    in = new RandomAccessStream(id);
    if (in.readShort() == 0x4949) in.order(true);

    ifds = TiffTools.getIFDs(in);
    if (ifds == null) throw new FormatException("No IFDs found");

    // hack up the IFDs
    //
    // Imaris TIFFs store a thumbnail in the first IFD; each of the remaining
    // IFDs defines a stack of tiled planes.

    Vector tmp = new Vector();

    for (int i=1; i<ifds.length; i++) {
      long[] byteCounts = TiffTools.getIFDLongArray(ifds[i],
        TiffTools.TILE_BYTE_COUNTS, false);
      long[] offsets = TiffTools.getIFDLongArray(ifds[i],
        TiffTools.TILE_OFFSETS, false);

      for (int j=0; j<byteCounts.length; j++) {
        Hashtable t = (Hashtable) ifds[i].clone();
        TiffTools.putIFDValue(t, TiffTools.TILE_BYTE_COUNTS, byteCounts[j]);
        TiffTools.putIFDValue(t, TiffTools.TILE_OFFSETS, offsets[j]);
        tmp.add(t);
      }
    }

    sizeC[0] = ifds.length - 1;
    sizeZ[0] = tmp.size() / sizeC[0];
    sizeT[0] = 1;
    sizeX[0] =
      TiffTools.getIFDIntValue(ifds[1], TiffTools.IMAGE_WIDTH, false, 0);
    sizeY[0] =
      TiffTools.getIFDIntValue(ifds[1], TiffTools.IMAGE_LENGTH, false, 0);

    ifds = (Hashtable[]) tmp.toArray(new Hashtable[0]);
    numImages = sizeC[0] * sizeZ[0];
    currentOrder[0] = "XYZCT";

    int bitsPerSample = TiffTools.getIFDIntValue(ifds[0],
      TiffTools.BITS_PER_SAMPLE);
    int bitFormat = TiffTools.getIFDIntValue(ifds[0], TiffTools.SAMPLE_FORMAT);

    while (bitsPerSample % 8 != 0) bitsPerSample++;
    if (bitsPerSample == 24 || bitsPerSample == 48) bitsPerSample /= 3;

    if (bitFormat == 3) pixelType[0] = FormatReader.FLOAT;
    else if (bitFormat == 2) {
      switch (bitsPerSample) {
        case 8:
          pixelType[0] = FormatReader.INT8;
          break;
        case 16:
          pixelType[0] = FormatReader.INT16;
          break;
        case 32:
          pixelType[0] = FormatReader.INT32;
          break;
      }
    }
    else {
      switch (bitsPerSample) {
        case 8:
          pixelType[0] = FormatReader.UINT8;
          break;
        case 16:
          pixelType[0] = FormatReader.UINT16;
          break;
        case 32:
          pixelType[0] = FormatReader.UINT32;
          break;
      }
    }

    String comment = (String) getMeta("Comment");

    // likely an INI-style comment, although we can't be sure

    if (comment != null && comment.startsWith("[")) {
      // parse key/value pairs
      StringTokenizer st = new StringTokenizer(comment, "\n");
      while (st.hasMoreTokens()) {
        String line = st.nextToken();
        int equals = line.indexOf("=");
        if (equals < 0) continue;
        String key = line.substring(0, equals);
        String value = line.substring(equals + 1);
        addMeta(key.trim(), value.trim());
      }
      metadata.remove("Comment");
    }

  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new ImarisTiffReader().testRead(args);
  }

}
