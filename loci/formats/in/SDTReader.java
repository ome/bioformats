//
// SDTReader.java
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
import loci.formats.*;

/**
 * SDTReader is the file format reader for
 * Becker &amp; Hickl SPC-Image SDT files.
 *
 * This importer is based on MATLAB code originally by Long Yan.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class SDTReader extends FormatReader {

  // -- Constants --

  protected static final short BH_HEADER_CHKSUM = 0x55aa;
  protected static final short BH_HEADER_NOT_VALID = 0x1111;
  protected static final short BH_HEADER_VALID = 0x5555;

  /** For .set files (setup only). */
  protected static final String SETUP_IDENTIFIER = "SPC Setup Script File";

  /** For normal .sdt files (setup + data). */
  protected static final String DATA_IDENTIFIER = "SPC Setup & Data File";

  /**
   * For .sdt files created automatically in Continuous Flow mode measurement
   * (no setup, only data).
   */
  protected static final String FLOW_DATA_IDENTIFIER = "SPC Flow Data File";

  /**
   * For .sdt files created using DLL function SPC_save_data_to_sdtfile
   * (no setup, only data).
   */
  protected static final String DLL_DATA_IDENTIFIER = "SPC DLL Data File";

  /**
   * For .sdt files created in FIFO mode
   * (setup, data blocks = Decay, FCS, FIDA, FILDA &amp; MCS curves
   * for each used routing channel).
   */
  protected static final String FCS_DATA_IDENTIFIER = "SPC FCS Data File";


  /** Number of time bins in the decay curve. */
  protected static final int TIME_BINS = 64;


  // -- Fields --

  /** Current file. */
  protected RandomAccessStream in;

  /** Length in bytes of current file. */
  protected int fileLen;

  /** Number of images in current SDT file. */
  protected int numImages;

  /** Offset to binary data. */
  protected int offset;

  /** Dimensions of the current SDT file's images. */
  protected int width = 128, height = 128;

  protected int timeBins = 1; // TODO
  protected int channels = 1; // TODO

  // -- Constructor --

  /** Constructs a new SDT reader. */
  public SDTReader() { super("SPCImage Data", "sdt"); }

  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for an SDT file. */
  public boolean isThisType(byte[] block) { return false; }

  /** Determines the number of images in the given SDT file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return numImages;
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    return false;
  }

  /** Return true if the data is in little-endian format. */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    return true;
  }

  /** Returns whether or not the channels are interleaved. */
  public boolean isInterleaved(String id) throws FormatException, IOException {
    return false;
  }

  /** Obtains the specified image from the given SDT file as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);

    if (no < 0 || no >= numImages) {
      throw new FormatException("Invalid image number: " + no);
    }

    in.seek(offset + 2 * width * height * TIME_BINS * no);
    short[] data = new short[width * height];

    for (int y=0; y<height; y++) {
      for (int x=0; x<width; x++) {
        int ndx = width * y + x;
        int sum = 0;
        for (int decay=0; decay<TIME_BINS; decay++) {
          sum += in.readShort();
        }
        data[ndx] = (short) sum;
      }
    }

    byte[] p = new byte[data.length * 2];
    for (int i=0; i<data.length; i++) {
      byte[] b = DataTools.shortToBytes(data[i], true);
      p[2*i] = b[0];
      p[2*i + 1] = b[1];
    }
    return p;
  }

  /** Obtains the specified image from the given SDT file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    return ImageTools.makeImage(openBytes(id, no), width, height, 1, false,
      2, true);
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  /** Initializes the given SDT file. */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessStream(id);
    in.order(true);

    // read file header

    // software revision number (lower 4 bits >= 10(decimal))
    short revision = in.readShort();

    // offset of the info part which contains general text
    // information (Title, date, time, contents etc.)
    int infoOffs = in.readInt();

    // length of the info part
    short infoLength = in.readShort();

    // offset of the setup text data
    // (system parameters, display parameters, trace parameters etc.)
    int setupOffs = in.readInt();

    // length of the setup data
    short setupLength = in.readShort();

    // offset of the first data block
    int dataBlockOffs = in.readInt();

    // no_of_data_blocks valid only when in 0 .. 0x7ffe range,
    // if equal to 0x7fff  the  field 'reserved1' contains
    // valid no_of_data_blocks
    short noOfDataBlocks = in.readShort();

    // length of the longest block in the file
    int dataBlockLength = in.readInt();

    // offset to 1st. measurement description block
    // (system parameters connected to data blocks)
    int measDescBlockOffs = in.readInt();

    // number of measurement description blocks
    short noOfMeasDescBlocks = in.readShort();

    // length of the measurement description blocks
    short measDescBlockLength = in.readShort();

    // valid: 0x5555, not valid: 0x1111
    short headerValid = in.readShort(); // unsigned

    // reserved1 now contains noOfDataBlocks
    int reserved1 = in.readInt(); // unsigned

    short reserved2 = in.readShort(); // unsigned

    // checksum of file header
    short chksum = in.readShort(); // unsigned

    if (DEBUG) {
      System.err.println("SDT header:");
      System.err.println("\trevision = " + revision);
      System.err.println("\tinfoOffs = " + infoOffs);
      System.err.println("\tinfoLength = " + infoLength);
      System.err.println("\tsetupOffs = " + setupOffs);
      System.err.println("\tsetupLength = " + setupLength);
      System.err.println("\tdataBlockOffs = " + dataBlockOffs);
      System.err.println("\tnoOfDataBlocks = " + noOfDataBlocks);
      System.err.println("\tdataBlockLength = " + dataBlockLength);
      System.err.println("\tmeasDescBlockOffs = " + measDescBlockOffs);
      System.err.println("\tnoOfMeasDescBlocks = " + noOfMeasDescBlocks);
      System.err.println("\tmeasDescBlockLength = " + measDescBlockLength);
      System.err.println("\theaderValid = " + (0xffff & headerValid));
      System.err.println("\treserved1 = " + (0xffffffff & reserved1));
      System.err.println("\treserved2 = " + (0xffff & reserved2));
      System.err.println("\tchksum = " + (0xffff & chksum));
    }

    // read file info
    in.seek(infoOffs);
    byte[] infoBytes = new byte[infoLength];
    in.readFully(infoBytes);
    String info = new String(infoBytes);
    if (DEBUG) {
      System.out.println("SDT file info:");
      System.out.println(info);
    }

    // read setup
    in.seek(setupOffs);
    byte[] setupBytes = new byte[setupLength];
    in.readFully(setupBytes);
    String setup = new String(setupBytes);
    if (DEBUG) {
      System.out.println("SDT setup:");
      System.out.println(setup);
    }

    // skip to data
    offset = dataBlockOffs + 22;
    in.seek(offset);

    // compute number of image planes
    numImages = (int) ((in.length() - offset) / (2 * 64 * width * height));

    sizeX[0] = width;
    sizeY[0] = height;
    sizeZ[0] = 1;
    sizeC[0] = numImages;
    sizeT[0] = 1;
    currentOrder[0] = "XYZTC";

    MetadataStore store = getMetadataStore(id);
    store.setPixels(new Integer(getSizeX(id)), new Integer(getSizeY(id)),
      new Integer(getSizeZ(id)), new Integer(getSizeC(id)),
      new Integer(getSizeT(id)), "int16", new Boolean(!isLittleEndian(id)),
      getDimensionOrder(id), null);
  }


  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new SDTReader().testRead(args);
  }

}
