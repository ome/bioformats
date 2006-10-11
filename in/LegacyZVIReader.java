//
// LegacyZVIReader.java
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
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;
import java.util.HashSet;
import java.util.Set;
import loci.formats.*;

/**
 * LegacyZVIReader is the legacy file format reader for Zeiss ZVI files.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at cs.wisc.edu
 * @author Michel Boudinot Michel dot boudinot at iaf.cnrs-gif.fr
 */
public class LegacyZVIReader extends FormatReader {

  // -- Constants --

  /** First few bytes of every ZVI file. */
  private static final byte[] ZVI_SIG = {
    -48, -49, 17, -32, -95, -79, 26, -31
  };

  /** Block identifying start of useful header information. */
  private static final byte[] ZVI_MAGIC_BLOCK_1 = {65, 0, 16}; // 41 00 10

  /** Block identifying second part of useful header information. */
  private static final byte[] ZVI_MAGIC_BLOCK_2 = {65, 0, -128}; // 41 00 80

  /** Block identifying third part of useful header information. */
  private static final byte[] ZVI_MAGIC_BLOCK_3 = {32, 0, 16}; // 20 00 10

  /** Memory buffer size in bytes, for reading from disk. */
  private static final int BUFFER_SIZE = 8192;

  /** String apologizing for the fact that this ZVI support still sucks. */
  private static final String WHINING = "Sorry, " +
    "ZVI support is still preliminary.  It will be improved as time permits.";


  // -- Fields --

  /** Current file. */
  protected RandomAccessFile in;

  /** List of image blocks. */
  private Vector blockList;

  /** Bytes per pixel. */
  private int bytesPerPixel;

  /** Counters of image elements */
  private int numZ = 0, numC = 0, numT = 0;

  /** dimension order within file */
  private String dimensionOrder  = "XYCZT";
  private int cFlag = 0, zFlag = 0, tFlag = 0;

  // -- Constructor --

  /** Constructs a new legacy ZVI reader. */
  public LegacyZVIReader() { super("Legacy ZVI", "zvi"); }


  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for a ZVI file. */
  public boolean isThisType(byte[] block) {
    if (block == null) return false;
    int len = block.length < ZVI_SIG.length ? block.length : ZVI_SIG.length;
    for (int i=0; i<len; i++) {
      if (block[i] != ZVI_SIG[i]) return false;
    }
    return true;
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return (bytesPerPixel == 3) || (bytesPerPixel > 4);
  }

  /** Return true if the data is in little-endian format. */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    return true;
  }

  /** Returns whether or not the channels are interleaved. */
  public boolean isInterleaved(String id) throws FormatException, IOException {
    return false;
  }

  /** Obtains the specified image from the given ZVI file, as a byte array. */
  public byte[] openBytes(String id, int no) throws FormatException, IOException
  {
    return ImageTools.getBytes(openImage(id, no), false, no);
  }

  /** Determines the number of images in the given ZVI file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return blockList.size();
  }

  /** Obtains the specified image from the given ZVI file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    if (DEBUG) System.out.println("Reading image #" + no + "...");

    ZVIBlock zviBlock = (ZVIBlock) blockList.elementAt(no);

    return zviBlock.readImage(in);
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  /** Initializes the given ZVI file. */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessFile(id, "r");

    // Highly questionable decoding strategy:
    //
    // Note that all byte ordering is little endian, includeing 4-byte header
    // fields. Other examples: 16-bit data is LSB MSB, and 3-channel data is
    // BGR instead of RGB.
    //
    // 1) Find image header byte sequence:
    //    A) Find 41 00 10. (ZVI_MAGIC_BLOCK_1)
    //    B) Skip 19 bytes of stuff.
    //    C) Read 41 00 80. (ZVI_MAGIC_BLOCK_2)
    //    D) Read 11 bytes of 00
    //    E) Read potential header information:
    //       - Z-slice (4 bytes)
    //       - channel (4 bytes)
    //       - timestep (4 bytes)
    //    F) Read 108 bytes of 00.
    //
    // 2) If byte sequence is not as expected at any point (e.g.,
    //    stuff that is supposed to be 00 isn't), start over at 1A.
    //
    // 3) Find 20 00 10. (ZVI_MAGIC_BLOCK_3)
    //
    // 4) Read more header information:
    //    - width (4 bytes)
    //    - height (4 bytes)
    //    - ? (4 bytes; always 1)
    //    - bytesPerPixel (4 bytes)
    //    - pixelType (this is what the AxioVision software calls it)
    //       - 1=24-bit (3 color components, 8-bit each)
    //       - 3=8-bit (1 color component, 8-bit)
    //       - 4=16-bit (1 color component, 16-bit)
    //    - bitDepth (4 bytes--usually, but not always, bytesPerPixel * 8)
    //
    // 5) Read image data (width * height * bytesPerPixel)
    //
    // 6) Repeat the entire process until no more headers are identified.

    long pos = 0;
    blockList = new Vector();
    Set zSet = new HashSet(); // to hold Z plan index collection.
    Set cSet = new HashSet(); // to hold C channel index collection
    Set tSet = new HashSet(); // to hold T time index collection.
    numZ = 0;
    numC = 0;
    numT = 0;
    int numI = 0;

    while (true) {
      // search for start of next image header
      long header = findBlock(in, ZVI_MAGIC_BLOCK_1, pos);

      if (header < 0) {
        // no more potential headers found; we're done
        break;
      }
      pos = header + ZVI_MAGIC_BLOCK_1.length;

      if (DEBUG) System.err.println("Found potential image block: " + header);

      // these byte don't matter
      in.skipBytes(19);
      pos += 19;

      // these bytes should match ZVI_MAGIC_BLOCK_2
      byte[] b = new byte[ZVI_MAGIC_BLOCK_2.length];
      in.readFully(b);
      boolean ok = true;
      for (int i=0; i<b.length; i++) {
        if (b[i] != ZVI_MAGIC_BLOCK_2[i]) {
          ok = false;
          break;
        }
        pos++;
      }
      if (!ok) continue;

      // these bytes should be 00
      b = new byte[11];
      in.readFully(b);
      for (int i=0; i<b.length; i++) {
        if (b[i] != 0) {
          ok = false;
          break;
        }
        pos++;
      }
      if (!ok) continue;

      // read potential header information
      int theZ = (int) DataTools.read4UnsignedBytes(in, true);
      int theC = (int) DataTools.read4UnsignedBytes(in, true);
      int theT = (int) DataTools.read4UnsignedBytes(in, true);
      pos += 12;

      // these byte should be 00
      b = new byte[108];
      in.readFully(b);
      for (int i=0; i<b.length; i++) {
        if (b[i] != 0) {
          ok = false;
          break;
        }
        pos++;
      }
      if (!ok) continue;

      // everything checks out; looks like an image header to me
//+ (mb) decoding strategy modification
      //      Some zvi images have the following structure:
      //        ZVI_SIG                    Decoding:
      //        ZVI_MAGIC_BLOCK_1
      //        ZVI_MAGIC_BLOCK_2      <== Start of header information
      //        - Z-slice (4 bytes)     -> theZ = 0
      //        - channel (4 bytes)     -> theC = 0
      //        - timestep (4 bytes)    -> theT = 0
      //        ZVI_MAGIC_BLOCK_2      <==  Start of header information
      //        - Z-slice (4 bytes)     -> theZ actual value
      //        - channel (4 bytes)     -> theC actual value
      //        - timestep (4 bytes)    -> theT actual value
      //        ZVI_MAGIC_BLOCK_3      <== End of header information
      //        ...
      //
      //        Two consecutive Start of header information ZVI_MAGIC_BLOCK_2
      //        make test 3) of original decoding strategy fail. The first
      //        null values are taken as theZ, theC and theT values, the
      //        following actual values are ignored.
      //        Parsing the rest of the file appears to be ok.
      //
      //        New decoding strategy looks for the last header information
      //        ZVI_MAGIC_BLOCK_2 / ZVI_MAGIC_BLOCK_3 to get proper image
      //        slice theZ, theC and theT values.

      // these bytes don't matter
      in.skipBytes(89);
      pos += 89;

      byte[] magic3 = new byte[ZVI_MAGIC_BLOCK_3.length];
      in.readFully(magic3);
      for (int i=0; i<magic3.length; i++) {
        if (magic3[i] != ZVI_MAGIC_BLOCK_3[i]) {
          ok = false;
          break;
        }
      }
      if (!ok) continue;
      pos += ZVI_MAGIC_BLOCK_3.length;

      // read more header information
      int width = (int) DataTools.read4UnsignedBytes(in, true);
      int height = (int) DataTools.read4UnsignedBytes(in, true);
      // don't know what this is for
      int alwaysOne = (int) DataTools.read4UnsignedBytes(in, true);
      bytesPerPixel = (int) DataTools.read4UnsignedBytes(in, true);
      // not clear what this value signifies
      int pixType = (int) DataTools.read4UnsignedBytes(in, true);
      // doesn't always equal bytesPerPixel * 8
      int bitDepth = (int) DataTools.read4UnsignedBytes(in, true);
      pos += 24;

      String type = "";
      switch (pixType) {
        case 1:
          type = "8 bit rgb tuple, 24 bpp";
          pixelType[0] = FormatReader.INT8;
          break;
        case 2:
          type = "8 bit rgb quad, 32 bpp";
          pixelType[0] = FormatReader.INT8;
          break;
        case 3:
          type = "8 bit grayscale";
          pixelType[0] = FormatReader.INT8;
          break;
        case 4:
          type = "16 bit signed int, 16 bpp";
          pixelType[0] = FormatReader.INT16;
          break;
        case 5:
          type = "32 bit int, 32 bpp";
          pixelType[0] = FormatReader.INT32;
          break;
        case 6:
          type = "32 bit float, 32 bpp";
          pixelType[0] = FormatReader.FLOAT;
          break;
        case 7:
          type = "64 bit float, 64 bpp";
          pixelType[0] = FormatReader.DOUBLE;
          break;
        case 8:
          type = "16 bit unsigned short triple, 48 bpp";
          pixelType[0] = FormatReader.INT16;
          break;
        case 9:
          type = "32 bit int triple, 96 bpp";
          pixelType[0] = FormatReader.INT32;
          break;
        default:
          type = "undefined pixel type (" + pixType + ")";
      }

      metadata.put("Width", new Integer(width));
      metadata.put("Height", new Integer(height));
      metadata.put("PixelType", type);
      metadata.put("BPP", new Integer(bytesPerPixel));


      ZVIBlock zviBlock = new ZVIBlock(theZ, theC, theT, width, height,
        alwaysOne, bytesPerPixel, pixType, bitDepth, pos);
      if (DEBUG) System.out.println(zviBlock);

      // perform some checks on the header info
      // populate Z, C and T index collections
      zSet.add(new Integer(theZ));
      cSet.add(new Integer(theC));
      tSet.add(new Integer(theT));
      numI++;
      // sorry not a very clever way to find dimension order
     
      if ((numI == 2) && (cSet.size() == 2))  cFlag = 1;
      if ((numI == 2) && (zSet.size() == 2))  zFlag = 1;
      if ((numI == 2) && (tSet.size() == 2))  tFlag = 1;

      if ((numI % 3 == 0) && (zSet.size() > 1) && (cFlag == 1)) {
        dimensionOrder = "XYCZT";
      }
      if ((numI % 3 == 0) && (tSet.size() > 1) && (cFlag == 1)) {
        dimensionOrder = "XYCTZ";
      }
      if ((numI % 3 == 0) && (cSet.size() > 1) && (zFlag == 1)) {
        dimensionOrder = "XYZCT";
      }
      if ((numI % 3 == 0) && (tSet.size() > 1) && (zFlag == 1)) {
        dimensionOrder = "XYZTC";
      }
      if ((numI % 3 == 0) && (cSet.size() > 1) && (tFlag == 1)) {
        dimensionOrder = "XYTCZ";
      }
      if ((numI % 3 == 0) && (zSet.size() > 1) && (tFlag == 1)) {
        dimensionOrder = "XYTZC";
      }

      // save this image block's position
      blockList.add(zviBlock);
      pos += width * height * bytesPerPixel;

      sizeX[0] = openImage(id, 0).getWidth();
      sizeY[0] = openImage(id, 0).getHeight();
      sizeZ[0] = zSet.size();
      sizeC[0] = cSet.size();
      sizeT[0] = tSet.size();
      currentOrder[0] = dimensionOrder;

      // Populate metadata store

      // The metadata store we're working with.
      MetadataStore store = getMetadataStore(id);

      store.setPixels(
        new Integer(width), // SizeX
        new Integer(height), // SizeY
        new Integer(getSizeZ(id)), // SizeZ
        new Integer(getSizeC(id)), // SizeC
        new Integer(getSizeT(id)), // SizeT
        new Integer(pixelType[0]), // PixelType
        Boolean.FALSE, // BigEndian
        new String(dimensionOrder), // DimensionOrder
        null); // Use index 0
    }

    if (blockList.isEmpty()) {
      throw new FormatException("No image data found." + WHINING);
    }
    // number of Z, C and T index
    numZ = zSet.size();
    numC = cSet.size();
    numT = tSet.size();
    if (numZ * numC * numT != blockList.size()) {
      System.err.println("Warning: image counts do not match. " + WHINING);
    }
  }

  // -- Utility methods --

  /**
   * Finds the first occurence of the given byte block within the file,
   * starting from the given file position.
   */
  private static long findBlock(RandomAccessFile in, byte[] block, long start)
    throws IOException
  {
    long filePos = start;
    long fileSize = in.length();
    byte[] buf = new byte[BUFFER_SIZE];
    long spot = -1;
    int step = 0;
    boolean found = false;
    in.seek(start);

    while (true) {
      int len = (int) (fileSize - filePos);
      if (len < 0) break;
      if (len > buf.length) len = buf.length;
      in.readFully(buf, 0, len);

      for(int i=0; i<len; i++) {
        if (buf[i] == block[step]) {
          if (step == 0) {
            // could be a match; flag this spot
            spot = filePos + i;
          }
          step++;
          if (step == block.length) {
            // found complete match; done searching
            found = true;
            break;
          }
        }
        else {
          // no match; reset step indicator
          spot = -1;
          step = 0;
        }
      }
      if (found) break;  // found a match; we're done
      if (len < buf.length) break;  // EOF reached; we're done

      filePos += len;
    }

    // set file pointer to byte immediately following pattern
    if (spot >= 0) in.seek(spot + block.length);
    return spot;
  }


  // -- Helper classes --

  /** Contains information collected from a ZVI image header. */
  private class ZVIBlock {
    private int theZ, theC, theT;
    private int width, height;
    private int alwaysOne;
    private int bytesPerPixel;
    private int pixelType;
    private int bitDepth;
    private long imagePos;

    private int numPixels;
    private int imageSize;
    private int numChannels;
    private int bytesPerChannel;

    public ZVIBlock(int theZ, int theC, int theT, int width, int height,
      int alwaysOne, int bytesPerPixel, int pixelType, int bitDepth,
      long imagePos)
    {
      this.theZ = theZ;
      this.theC = theC;
      this.theT = theT;
      this.width = width;
      this.height = height;
      this.alwaysOne = alwaysOne;
      this.bytesPerPixel = bytesPerPixel;
      this.pixelType = pixelType;
      this.bitDepth = bitDepth;
      this.imagePos = imagePos;

      numPixels = width * height;
      imageSize = numPixels * bytesPerPixel;
      numChannels = pixelType == 1 ? 3 : 1;  // a total shot in the dark
      if (bytesPerPixel % numChannels != 0) {
        System.err.println("Warning: incompatible bytesPerPixel (" +
          bytesPerPixel + ") and numChannels (" + numChannels +
          "). Assuming grayscale data. " + WHINING);
        numChannels = 1;
      }
      bytesPerChannel = bytesPerPixel / numChannels;
    }

    /** Reads in this block's image data from the given file. */
    public BufferedImage readImage(RandomAccessFile raf)
      throws IOException, FormatException
    {
      long fileSize = raf.length();
      if (imagePos + imageSize > fileSize) {
        throw new FormatException("File is not big enough to contain the " +
          "pixels (width=" + width + "; height=" + height +
          "; bytesPerPixel=" + bytesPerPixel + "; imagePos=" + imagePos +
          "; fileSize=" + fileSize + "). " + WHINING);
      }

      // read image
      byte[] imageBytes = new byte[imageSize];
      raf.seek(imagePos);
      raf.readFully(imageBytes);

      // convert image bytes into BufferedImage
      if (bytesPerPixel > 4) {
        numChannels = bytesPerPixel / 2;
        bytesPerPixel /= numChannels;
        bytesPerChannel = bytesPerPixel;
      }
      int index = 0;
      short[][] samples = new short[numChannels][numPixels * bytesPerPixel];
      for (int i=0; i<numPixels; i++) {
        for (int c=numChannels-1; c>=0; c--) {
          byte[] b = new byte[bytesPerChannel];
          System.arraycopy(imageBytes, index, b, 0, bytesPerChannel);
          index += bytesPerChannel;
          // our zvi images are 16 bit per pixel (BitsPerPixel) but
          // with an Acquisition Bit Depth of 12
          samples[c][i] = (short) (DataTools.bytesToShort(b, true) * 8);
        }
      }
      return ImageTools.makeImage(samples, width, height);
    }

    public String toString() {
      return "Image header block:\n" +
        "  theZ = " + theZ + "\n" + "  theC = " + theC + "\n" +
        "  theT = " + theT + "\n" + "  width = " + width + "\n" +
        "  height = " + height + "\n" + "  alwaysOne = " + alwaysOne + "\n" +
        "  bytesPerPixel = " + bytesPerPixel + "\n" +
        "  pixelType = " + pixelType + "\n" + "  bitDepth = " + bitDepth;
    }
  }


  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new LegacyZVIReader().testRead(args);
  }

}
