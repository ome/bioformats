//
// OpenlabReader.java
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

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.Vector;
import loci.formats.*;

/**
 * OpenlabReader is the file format reader for Openlab LIFF files.
 *
 * @author Eric Kjellman egkjellman at wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class OpenlabReader extends FormatReader {

  // -- Static fields --

  /** Helper reader to decode PICT data. */
  private static PictReader pictReader = new PictReader();

  // -- Fields --

  /** Current file. */
  protected RandomAccessStream in;

  /** Number of blocks for current Openlab LIFF. */
  private int numBlocks;

  /** Offset for each block of current Openlab LIFF file. */
  private int[] offsets;

  /** Image type for each block of current Openlab LIFF file. */
  private int[] imageType;

  /** Flag indicating whether current file is little endian. */
  protected boolean little = true;

  /** Size of a pixel plane/section's X-axis */
  private Integer sizeX;

  /** Size of a pixel plane/section's Y-axis */
  private Integer sizeY;

  /** First plane. */
  private BufferedImage firstPlane;

  // -- Constructor --

  /** Constructs a new Openlab reader. */
  public OpenlabReader() {
    super("Openlab LIFF", "liff");
  }

  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for an Openlab file. */
  public boolean isThisType(byte[] block) {
    return block.length >= 8 && block[0] == 0 && block[1] == 0 &&
      block[2] == -1 && block[3] == -1 && block[4] == 105 &&
      block[5] == 109 && block[6] == 112 && block[7] == 114;
  }

  /**
   * Checks if the given string is a valid filename for an Openlab file.
   * @param open If true, and the file extension is insufficient to determine
   *  the file type, the (existing) file is opened for further analysis.
   */
  public boolean isThisType(String name, boolean open) {
    if (super.isThisType(name, open)) return true;

    if (open) {
      // since we can't always determine it from the name alone (blank
      // extensions), we open the file and call the block verifier
      return checkBytes(name, 8);
    }
    else {
      String lname = name.toLowerCase();
      return lname.indexOf(".") < 0; // file appears to have no extension
    }
  }

  /** Determines the number of images in the given Openlab file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return (!separated) ? numBlocks : 3*numBlocks;
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    int type = checkType();
    return type == 2 || type == 3;
  }

  /** Get the size of the X dimension. */
  public int getSizeX(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return sizeX.intValue();
  }

  /** Get the size of the Y dimension. */
  public int getSizeY(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return sizeY.intValue();
  }

  /** Get the size of the Z dimension. */
  public int getSizeZ(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return numBlocks;
  }

  /** Get the size of the C dimension. */
  public int getSizeC(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return isRGB(id) ? 3 : 1;
  }

  /** Get the size of the T dimension. */
  public int getSizeT(String id) throws FormatException, IOException {
    return 1;
  }

  /** Return true if the data is in little-endian format. */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return little;
  }

  /**
   * Return a five-character string representing the dimension order
   * within the file.
   */
  public String getDimensionOrder(String id)
    throws FormatException, IOException
  {
    return "XYCZT";
  }

  /** Obtains the specified image from the given file as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    // kinda inefficient...should probably be changed to "natively" use
    // openBytes, instead of calling openImage and then converting

    BufferedImage img = openImage(id, no);
    if (separated) {
      return ImageTools.getBytes(img)[0];
    }
    else {
      byte[][] p = ImageTools.getBytes(img);
      byte[] rtn = new byte[p.length * p[0].length];
      for (int i=0; i<p.length; i++) {
        System.arraycopy(p[i], 0, rtn, i*p[0].length, p[i].length);
      }
      return rtn;
    }
  }

  /** Obtains the specified image from the given Openlab file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    // First initialize:
    if (separated) no /= 3;
    in.seek(offsets[no] + 12);

    int blockSize = in.readInt();

    byte toRead = (byte) in.read();

    // right now I'm gonna skip all the header info
    // check to see whether or not this is v2 data
    if (toRead == 1) in.skipBytes(128);
    in.skipBytes(169);
    // read in the block of data
    byte[] b = new byte[blockSize];
    in.read(b);
    byte[] pixelData = new byte[blockSize];
    int pixPos = 0;

    Dimension dim;
    try { dim = pictReader.getDimensions(b); }
    catch (Exception e) { dim = new Dimension(0, 0); }

    int length = b.length;
    int num, size;
    int totalBlocks = -1; // set to allow loop to start.
    int expectedBlock = 0;
    int pos = 0;
    int imagePos = 0;
    int imageSize = dim.width * dim.height;
    short[] flatSamples = new short[imageSize];
    byte[] temp;

    sizeX = new Integer(dim.width);
    sizeY = new Integer(dim.height);

    // read in deep gray pixel data into an array, and create a
    // BufferedImage out of it
    //
    // First, checks the existence of a deep gray block. If it doesn't exist,
    // assume it is PICT data, and attempt to read it.

    // check whether or not there is deep gray data
    while (expectedBlock != totalBlocks) {

      while (pos + 7 < length &&
        (b[pos] != 73 || b[pos + 1] != 86 || b[pos + 2] != 69 ||
        b[pos + 3] != 65 || b[pos + 4] != 100 || b[pos + 5] != 98 ||
        b[pos + 6] != 112 || b[pos + 7] != 113))
      {
        pos++;
      }

      if (pos + 32 > length) { // The header is 32 bytes long.
        if (expectedBlock == 0 && imageType[no] < 9) {
          // there has been no deep gray data, and it is supposed
          // to be a pict... *crosses fingers*
          try {
            if (!separated) {
              return pictReader.open(b);
            }
            else {
              return ImageTools.splitChannels(pictReader.open(b))[no%3];
            }
          }
          catch (Exception e) {
            e.printStackTrace();
            throw new FormatException("No iPic comment block found", e);
          }
        }
        else {
          throw new FormatException("Expected iPic comment block not found");
        }
      }

      pos += 8; // skip the block type we just found

      // Read info from the iPic comment. This serves as a
      // starting point to read the rest.
      temp = new byte[] {b[pos], b[pos+1], b[pos+2], b[pos+3]};
      num = batoi(temp);
      if (num != expectedBlock) {
        throw new FormatException("Expected iPic block not found");
      }
      expectedBlock++;
      temp = new byte[] {b[pos+4], b[pos+5], b[pos+6], b[pos+7]};
      if (totalBlocks == -1) {
        totalBlocks = batoi(temp);
      }
      else {
        if (batoi(temp) != totalBlocks) {
          throw new FormatException("Unexpected totalBlocks numbein.read");
        }
      }

      // skip to size
      pos += 16;
      temp = new byte[] {b[pos], b[pos+1], b[pos+2], b[pos+3]};
      size = batoi(temp);
      pos += 8;

      // copy into our data array.
      System.arraycopy(b, pos, pixelData, pixPos, size);
      pixPos += size;
    }
    int pixelValue = 0;
    pos = 0;

    // Now read the data and wrap it in a BufferedImage

    while (true) {
      if (pos + 1 < pixelData.length) {
        pixelValue = pixelData[pos] < 0 ? 256 + pixelData[pos] :
          (int) pixelData[pos] << 8;
        pixelValue += pixelData[pos + 1] < 0 ? 256 + pixelData[pos + 1] :
          (int) pixelData[pos + 1];
        pos += 2;
      }
      else throw new FormatException("Malformed LIFF data");
      flatSamples[imagePos] = (short) pixelValue;
      imagePos++;
      if (imagePos == imageSize) {  // done, return it
        return ImageTools.makeImage(flatSamples,
          dim.width, dim.height, 1, false);
      }
    }
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  /** Initializes the given Openlab file. */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessStream(id);

    // initialize an array containing tag offsets, so we can
    // use an O(1) search instead of O(n) later.
    // Also determine whether we will be reading color or grayscale
    // images

    long order = in.readInt();
    little = order == 0x0000ffff;

    Vector v = new Vector(); // a temp vector containing offsets.

    // Get first offset.

    in.skipBytes(12);
    int nextOffset = in.readInt();
    int nextOffsetTemp;

    boolean first = true;
    while(nextOffset != 0 && nextOffset < in.length()) {
      // get next tag, but still need this one

      in.seek(nextOffset + 4);
      nextOffsetTemp = in.readInt();

      byte[] toRead = new byte[4];
      in.read(toRead);
      if ((new String(toRead)).equals("PICT")) {
        boolean ok = true;
        if (first) {
          // ignore first image if it is called "Original Image" (pure white)
          first = false;
          in.skipBytes(47);
          byte[] layerNameBytes = new byte[127];
          in.read(layerNameBytes);
          String layerName = new String(layerNameBytes);
          if (layerName.startsWith("Original Image")) ok = false;
        }
        if (nextOffset == nextOffsetTemp) {
          v.remove(v.size() - 1);
          break;
        }
        if (ok) v.add(new Integer(nextOffset)); // add THIS tag offset
      }
      if (nextOffset == nextOffsetTemp) break;
      nextOffset = nextOffsetTemp;
    }

    in.seek(((Integer) v.firstElement()).intValue());

    // create and populate the array of offsets from the vector
    numBlocks = v.size();
    offsets = new int[numBlocks];
    for (int i = 0; i < numBlocks; i++) {
      offsets[i] = ((Integer) v.get(i)).intValue();
    }

    // populate the imageTypes that the file uses
    imageType = new int[numBlocks];
    for (int i = 0; i < numBlocks; i++) {
      in.seek(offsets[i] + 40);
      imageType[i] = in.readShort();
    }

    initMetadata();
  }

  // -- Helper methods --

  /** Populates the metadata hashtable. */
  private void initMetadata() throws FormatException, IOException {
    in.seek(8);

    in.order(false);

    // start by reading the file header
    metadata.put("Byte Order", new Boolean(little));

    long version = in.readInt();
    metadata.put("Version", new Long(version));

    short count = in.readShort();
    metadata.put("Count", new Short(count));

    in.readShort();

    int offset = in.readInt();

    // skip to first tag
    in.seek(offset);

    // read in each tag and its data

    for (int i=0; i<count; i++) {
      short tag = in.readShort();
      in.readShort();

      offset = in.readInt();
      long fmt = in.readInt();
      metadata.put("Format", new Long(fmt));

      long numBytes = in.readInt();
      metadata.put("NumBytes", new Long(numBytes));

      if (tag == 67 || tag == 68) {
        boolean isOpenlab2 = in.read() == 0;
        metadata.put("isOpenlab2", new Boolean(isOpenlab2));

        in.readShort();
        short layerId = in.readShort();
        metadata.put("LayerID", new Short(layerId));

        short layerType = in.readShort();
        metadata.put("LayerType", new Short(layerType));

        short bitDepth = in.readShort();
        metadata.put("BitDepth", new Short(bitDepth));

        short opacity = in.readShort();
        metadata.put("Opacity", new Short(opacity));

        // not sure how many bytes to skip here
        in.skipBytes(10);

        long type = in.readInt();
        metadata.put("ImageType", new Long(type));

        // not sure how many bytes to skip
        in.skipBytes(10);

        long timestamp = in.readInt();
        metadata.put("Timestamp", new Long(timestamp));

        in.skipBytes(2);

        if (isOpenlab2) {
          byte[] layerName = new byte[127];
          in.read(layerName);
          metadata.put("LayerName", new String(layerName));

          long timestampMS = in.readInt();
          metadata.put("Timestamp-MS", new Long(timestampMS));

          in.read();
          byte[] notes = new byte[118];
          in.read(notes);
          metadata.put("Notes", new String(notes));
        }
        else in.skipBytes(123);
      }
      else if (tag == 69) {
        long platform = in.readInt();
        metadata.put("Platform", new Long(platform));

        short units = in.readShort();
        metadata.put("Units", new Short(units));

        short imageId = in.readShort();
        metadata.put("ID", new Short(imageId));
        in.read();

        double xOrigin = in.readDouble();
        metadata.put("XOrigin", new Double(xOrigin));
        double yOrigin = in.readDouble();
        metadata.put("YOrigin", new Double(yOrigin));
        double xScale = in.readDouble();
        metadata.put("XScale", new Double(xScale));
        double yScale = in.readDouble();
        metadata.put("YScale", new Double(yScale));
        in.read();

        byte[] other = new byte[31];
        in.read(other);
        metadata.put("Other", new String(other));
      }

      // Populate metadata store

      // The metadata store we're working with.
      MetadataStore store = getMetadataStore(currentId);

      String type = "int8";
      if (metadata.get("BitDepth") != null) {
        int bitDepth = ((Short) metadata.get("BitDepth")).intValue();

        if (bitDepth <= 8) type = "int8";
        else if (bitDepth <= 16) type = "int16";
        else type = "int32";
      }

      store.setImage(null, metadata.get("Timestamp").toString(), null, null);

      // FIXME: There is a loss of precision here as we are down-casting from
      // double to float.
      store.setStageLabel(null, (Float) metadata.get("XOrigin"),
        (Float) metadata.get("YOrigin"), null, null);

      // FIXME: There is a loss of precision here as we are down-casting from
      // double to float.
      store.setDimensions((Float) metadata.get("XScale"),
        (Float) metadata.get("YScale"), null, null, null, null);

      in.seek(offset);

      // We need to poke at least one plane so that we can get "sizeX" and
      // "sizeY" set. to populate the pixels set.
      try { firstPlane = openImage(currentId, 0); }
      catch (FormatException e) { e.printStackTrace(); }

      store.setPixels(sizeX, sizeY, new Integer(numBlocks),
        new Integer(getSizeC(currentId)), new Integer(1), type,
        new Boolean(!little), "XYCZT", null);
    }
  }

  /**
   * Checks which type of Openlab file this is.
   * 1 =&gt; all planes are greyscale.
   * 2 =&gt; all planes are RGB.
   * 3 =&gt; every 4th plane is RGB, remaining planes are greyscale.
   */
  private int checkType() throws FormatException, IOException {
    if (firstPlane == null) firstPlane = openImage(currentId, 0);
    WritableRaster r = firstPlane.getRaster();
    int b = r.getNumBands();
    if (b == 3) return 2;

    if (offsets.length <= 3) return 1;

    in.seek(offsets[3] + 12);

    int blockSize = in.readInt();

    // right now I'm gonna skip all the header info
    // check to see whether or not this is v2 data
    if (in.read() == 1) in.skipBytes(128);
    in.skipBytes(169);
    // read in the block of data
    byte[] toRead = new byte[blockSize];
    in.read(toRead);

    Dimension dim;
    try { dim = pictReader.getDimensions(toRead); }
    catch (Exception e) { dim = new Dimension(0, 0); }

    int length = toRead.length;
    int totalBlocks = -1; // set to allow loop to start.
    int expectedBlock = 0;
    int pos = 0;

    sizeX = new Integer(dim.width);
    sizeY = new Integer(dim.height);

    if (expectedBlock != totalBlocks) {

      while (pos + 7 < length &&
        (toRead[pos] != 73 || toRead[pos + 1] != 86 ||
        toRead[pos + 2] != 69 || toRead[pos + 3] != 65 ||
        toRead[pos + 4] != 100 || toRead[pos + 5] != 98 ||
        toRead[pos + 6] != 112 || toRead[pos + 7] != 113))
      {
        pos++;
      }

      if (pos + 32 > length) { // The header is 32 bytes long.
        if (expectedBlock == 0 && imageType[3] < 9) {
          return 3;
        }
        return 1;
      }
      return 1;
    }
    return 1;
  }

  // -- Utility methods --

  /** Translates up to the first 4 bytes of a byte array to an integer. */
  private static int batoi(byte[] inp) {
    int len = inp.length>4?4:inp.length;
    int total = 0;
    for (int i = 0; i < len; i++) {
      total += (inp[i]<0 ? 256+inp[i] : (int) inp[i]) << (((len - 1) - i) * 8);
    }
    return total;
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new OpenlabReader().testRead(args);
  }

}
