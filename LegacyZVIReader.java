//
// LegacyZVIReader.java
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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

/**
 * LegacyZVIReader is the legacy file format reader for Zeiss ZVI files.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class LegacyZVIReader extends FormatReader {

  // -- Constants --

  /** First few bytes of every ZVI file. */
  private static final byte[] ZVI_SIG = {
    -48, -49, 17, -32, -95, -79, 26, -31
  };

  /** Block identifying start of useful header information. */
  private static final byte[] ZVI_MAGIC_BLOCK_1 = { // 41 00 10
    65, 0, 16
  };

  /** Block identifying second part of useful header information. */
  private static final byte[] ZVI_MAGIC_BLOCK_2 = { // 41 00 80
    65, 0, -128
  };

  /** Block identifying third part of useful header information. */
  private static final byte[] ZVI_MAGIC_BLOCK_3 = { // 20 00 10
    32, 0, 16
  };

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

  /** Determines the number of images in the given ZVI file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return blockList.size();
  }

  /** Obtains the specified image from the given ZVI file. */
  public BufferedImage open(String id, int no)
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
    int numZ = 0, numC = 0, numT = 0;
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
      long magic3 = findBlock(in, ZVI_MAGIC_BLOCK_3, pos);
      if (magic3 < 0) {
        throw new FormatException("Error parsing image header. " + WHINING);
      }
      pos = magic3 + ZVI_MAGIC_BLOCK_3.length;

      // read more header information
      int width = (int) DataTools.read4UnsignedBytes(in, true);
      int height = (int) DataTools.read4UnsignedBytes(in, true);
      // don't know what this is for
      int alwaysOne = (int) DataTools.read4UnsignedBytes(in, true);
      int bytesPerPixel = (int) DataTools.read4UnsignedBytes(in, true);
      // not clear what this value signifies
      int pixelType = (int) DataTools.read4UnsignedBytes(in, true);
      // doesn't always equal bytesPerPixel * 8
      int bitDepth = (int) DataTools.read4UnsignedBytes(in, true);
      pos += 24;

      String type = "";
      switch (pixelType) {
        case 1: type = "8 bit rgb tuple, 24 bpp"; break;
        case 2: type = "8 bit rgb quad, 32 bpp"; break;
        case 3: type = "8 bit grayscale"; break;
        case 4: type = "16 bit signed int, 8 bpp"; break;
        case 5: type = "32 bit int, 32 bpp"; break;
        case 6: type = "32 bit float, 32 bpp"; break;
        case 7: type = "64 bit float, 64 bpp"; break;
        case 8: type = "16 bit unsigned short triple, 48 bpp"; break;
        case 9: type = "32 bit int triple, 96 bpp"; break;
        default: type = "undefined pixel type (" + pixelType + ")";
      }

      metadata.put("Width", new Integer(width));
      metadata.put("Height", new Integer(height));
      metadata.put("PixelType", type);
      metadata.put("BPP", new Integer(bytesPerPixel));


      ZVIBlock zviBlock = new ZVIBlock(theZ, theC, theT, width, height,
        alwaysOne, bytesPerPixel, pixelType, bitDepth, pos);
      if (DEBUG) System.out.println(zviBlock);

      // perform some checks on the header info
      if (theZ >= numZ) numZ = theZ + 1;
      if (theC >= numC) numC = theC + 1;
      if (theT >= numT) numT = theT + 1;

      // save this image block's position
      blockList.add(zviBlock);
      pos += width * height * bytesPerPixel;

      // initialize the OME-XML tree

      if (ome != null) {
        OMETools.setPixels(ome,
          new Integer(width), // SizeX
          new Integer(height), // SizeY
          new Integer(numZ), // SizeZ
          new Integer(numC), // SizeC
          new Integer(numT), // SizeT
          null, // PixelType
          Boolean.FALSE, // BigEndian
          null); // DimensionOrder
      }
    }

    if (blockList.isEmpty()) {
      throw new FormatException("No image data found." + WHINING);
    }
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
    public BufferedImage readImage(RandomAccessFile in)
      throws IOException, FormatException
    {
      long fileSize = in.length();
      if (imagePos + imageSize > fileSize) {
        throw new FormatException("File is not big enough to contain the " +
          "pixels (width=" + width + "; height=" + height +
          "; bytesPerPixel=" + bytesPerPixel + "; imagePos=" + imagePos +
          "; fileSize=" + fileSize + "). " + WHINING);
      }

      // read image
      byte[] imageBytes = new byte[imageSize];
      in.seek(imagePos);
      in.readFully(imageBytes);

      // convert image bytes into BufferedImage
      int index = 0;
      short[][] samples = new short[numChannels][numPixels * bytesPerPixel];
      for (int i=0; i<numPixels; i++) {
        for (int c=numChannels-1; c>=0; c--) {
          byte[] b = new byte[bytesPerChannel];
          System.arraycopy(imageBytes, index, b, 0, bytesPerChannel);
          index += bytesPerChannel;
          samples[c][i] = DataTools.bytesToShort(b, true);
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
