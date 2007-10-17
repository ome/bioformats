//
// LegacyZVIReader.java
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
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import loci.formats.*;

/**
 * LegacyZVIReader is the legacy file format reader for Zeiss ZVI files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/LegacyZVIReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/LegacyZVIReader.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
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

  /** List of image blocks. */
  private Vector blockList;

  /** Bytes per pixel. */
  private int bytesPerPixel;

  /** Counters of image elements */
  private int numZ = 0, numC = 0, numT = 0;

  private int cFlag = 0, zFlag = 0, tFlag = 0;

  // -- Constructor --

  /** Constructs a new legacy ZVI reader. */
  public LegacyZVIReader() { super("Legacy ZVI", "zvi"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    if (block == null) return false;
    int len = block.length < ZVI_SIG.length ? block.length : ZVI_SIG.length;
    for (int i=0; i<len; i++) {
      if (block[i] != ZVI_SIG[i]) return false;
    }
    return true;
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length);

    ZVIBlock zviBlock = (ZVIBlock) blockList.elementAt(no);
    zviBlock.readBytes(in, buf);
    return buf;
  }

  /* @see loci.formats.IFormatReader#openImage(int) */
  public BufferedImage openImage(int no) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);

    if (debug) debug("Reading image #" + no + "...");
    ZVIBlock zviBlock = (ZVIBlock) blockList.elementAt(no);
    return zviBlock.readImage(in);
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("LegacyZVIReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);
    in.order(true);

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
      status("Searching for next image");
      long header = findBlock(in, ZVI_MAGIC_BLOCK_1, pos);

      if (header < 0) {
        // no more potential headers found; we're done
        break;
      }
      pos = header + ZVI_MAGIC_BLOCK_1.length;

      if (debug) debug("Found potential image block: " + header);

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
      int theZ = in.readInt();
      int theC = in.readInt();
      int theT = in.readInt();
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

      status("Reading image header");

      // read more header information
      core.sizeX[0] = in.readInt();
      core.sizeY[0] = in.readInt();
      // don't know what this is for
      int alwaysOne = in.readInt();
      bytesPerPixel = in.readInt();
      // not clear what this value signifies
      int pixType = in.readInt();
      // doesn't always equal bytesPerPixel * 8
      int bitDepth = in.readInt();
      pos += 24;

      String type = "";
      switch (pixType) {
        case 1:
          type = "8 bit rgb tuple, 24 bpp";
          core.pixelType[0] = FormatTools.UINT8;
          break;
        case 2:
          type = "8 bit rgb quad, 32 bpp";
          core.pixelType[0] = FormatTools.UINT8;
          break;
        case 3:
          type = "8 bit grayscale";
          core.pixelType[0] = FormatTools.UINT8;
          break;
        case 4:
          type = "16 bit signed int, 16 bpp";
          core.pixelType[0] = FormatTools.UINT16;
          break;
        case 5:
          type = "32 bit int, 32 bpp";
          core.pixelType[0] = FormatTools.UINT32;
          break;
        case 6:
          type = "32 bit float, 32 bpp";
          core.pixelType[0] = FormatTools.FLOAT;
          break;
        case 7:
          type = "64 bit float, 64 bpp";
          core.pixelType[0] = FormatTools.DOUBLE;
          break;
        case 8:
          type = "16 bit unsigned short triple, 48 bpp";
          core.pixelType[0] = FormatTools.UINT16;
          break;
        case 9:
          type = "32 bit int triple, 96 bpp";
          core.pixelType[0] = FormatTools.UINT32;
          break;
        default:
          type = "undefined pixel type (" + pixType + ")";
      }

      addMeta("Width", new Integer(core.sizeX[0]));
      addMeta("Height", new Integer(core.sizeY[0]));
      addMeta("PixelType", type);
      addMeta("BPP", new Integer(bytesPerPixel));

      ZVIBlock zviBlock = new ZVIBlock(theZ, theC, theT, core.sizeX[0],
        core.sizeY[0], alwaysOne, bytesPerPixel, pixType, bitDepth, pos);
      if (debug) debug(zviBlock.toString());

      // perform some checks on the header info
      // populate Z, C and T index collections
      zSet.add(new Integer(theZ));
      cSet.add(new Integer(theC));
      tSet.add(new Integer(theT));
      numI++;
      // sorry not a very clever way to find dimension order

      if ((numI == 2) && (cSet.size() == 2)) cFlag = 1;
      if ((numI == 2) && (zSet.size() == 2)) zFlag = 1;
      if ((numI == 2) && (tSet.size() == 2)) tFlag = 1;

      if ((numI % 3 == 0) && (zSet.size() > 1) && (cFlag == 1)) {
        core.currentOrder[0] = "XYCZT";
      }
      if ((numI % 3 == 0) && (tSet.size() > 1) && (cFlag == 1)) {
        core.currentOrder[0] = "XYCTZ";
      }
      if ((numI % 3 == 0) && (cSet.size() > 1) && (zFlag == 1)) {
        core.currentOrder[0] = "XYZCT";
      }
      if ((numI % 3 == 0) && (tSet.size() > 1) && (zFlag == 1)) {
        core.currentOrder[0] = "XYZTC";
      }
      if ((numI % 3 == 0) && (cSet.size() > 1) && (tFlag == 1)) {
        core.currentOrder[0] = "XYTCZ";
      }
      if ((numI % 3 == 0) && (zSet.size() > 1) && (tFlag == 1)) {
        core.currentOrder[0] = "XYTZC";
      }

      if (core.currentOrder[0] == null) core.currentOrder[0] = "XYZCT";

      // save this image block's position
      blockList.add(zviBlock);
      pos += core.sizeX[0] * core.sizeY[0] * bytesPerPixel;

      core.imageCount[0] = blockList.size();
      core.sizeX[0] = openImage(0).getWidth();
      core.sizeY[0] = openImage(0).getHeight();
      core.sizeZ[0] = zSet.size();
      core.sizeC[0] = cSet.size();
      core.sizeT[0] = tSet.size();
      core.rgb[0] = bytesPerPixel == 3 || bytesPerPixel > 4;
      core.interleaved[0] = false;
      core.littleEndian[0] = true;
      core.indexed[0] = false;
      core.falseColor[0] = false;

      // Populate metadata store

      MetadataStore store = getMetadataStore();
      store.setImage(currentId, null, null, null);

      FormatTools.populatePixels(store, this);

      for (int i=0; i<core.sizeC[0]; i++) {
        store.setLogicalChannel(i, null, null, null, null, null, null, null,
          null, null, null, null, null, null, null, null, null, null, null,
          null, null, null, null, null, null);
      }
    }

    status("Verifying image count");

    if (blockList.isEmpty()) {
      throw new FormatException("No image data found." + WHINING);
    }
    // number of Z, C and T index
    numZ = zSet.size();
    numC = cSet.size();
    numT = tSet.size();
    if (numZ * numC * numT != blockList.size()) {
      LogTools.println("Warning: image counts do not match. " + WHINING);
    }
  }

  // -- Utility methods --

  /**
   * Finds the first occurence of the given byte block within the file,
   * starting from the given file position.
   */
  private static long findBlock(RandomAccessStream in, byte[] block, long start)
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
        LogTools.println("Warning: incompatible bytesPerPixel (" +
          bytesPerPixel + ") and numChannels (" + numChannels +
          "). Assuming grayscale data. " + WHINING);
        numChannels = 1;
      }
      bytesPerChannel = bytesPerPixel / numChannels;
    }

    /** Reads in this block's image bytes from the given file. */
    public byte[] readBytes(RandomAccessStream raf, byte[] buf)
      throws IOException, FormatException
    {
      long fileSize = raf.length();
      if (imagePos + imageSize > fileSize) {
        throw new FormatException("File is not big enough to contain the " +
          "pixels (width=" + width + "; height=" + height +
          "; bytesPerPixel=" + bytesPerPixel + "; imagePos=" + imagePos +
          "; fileSize=" + fileSize + "). " + WHINING);
      }
      if (buf.length < imageSize) throw new FormatException("Buffer too small");

      // read image
      raf.seek(imagePos);
      raf.readFully(buf);
      return buf;
    }

    /** Reads in this block's image data from the given file. */
    public BufferedImage readImage(RandomAccessStream raf)
      throws IOException, FormatException
    {
      byte[] imageBytes = readBytes(raf, new byte[imageSize]);

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

}
