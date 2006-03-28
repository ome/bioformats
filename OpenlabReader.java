//
// OpenlabReader.java
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

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Vector;

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
  protected RandomAccessFile in;

  /** Number of blocks for current Openlab LIFF. */
  private int numBlocks;

  /** Offset for each block of current Openlab LIFF file. */
  private int[] offsets;

  /** Image type for each block of current Openlab LIFF file. */
  private int[] imageType;

  /** Flag indicating whether current file is little endian. */
  protected boolean little = true;


  // -- Constructor --

  /** Constructs a new Openlab reader. */
  public OpenlabReader() {
    super("Openlab LIFF", new String[] {"liff", "lif"});
  }


  // -- FormatReader API methods --

  /** Checks if the given string is a valid filename for an Openlab file. */
  public boolean isThisType(String name) {
    // Since we can't always determine it from the name alone (blank
    // extensions), we open the file and call the block verifier.
    long len = new File(name).length();
    int count = len < 16384 ? (int) len : 16384;
    byte[] buf = new byte[count];
    try {
      FileInputStream fin = new FileInputStream(name);
      int read = 0;
      while (read < count) {
        read += fin.read(buf, read, count-read);
      }
      fin.close();
      return isThisType(buf);
    }
    catch (IOException e) {
      return false;
    }
  }

  /** Checks if the given block is a valid header for an Openlab file. */
  public boolean isThisType(byte[] block) {
    return block.length >= 8 && block[0] == 0 && block[1] == 0 &&
      block[2] == -1 && block[3] == -1 && block[4] == 105 &&
      block[5] == 109 && block[6] == 112 && block[7] == 114;
  }

  /** Determines the number of images in the given Openlab file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return numBlocks;
  }

  /** Obtains the specified image from the given Openlab file. */
  public BufferedImage open(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    // First initialize:
    in.seek(offsets[no] + 12);
    byte[] toRead = new byte[4];
    in.read(toRead);
    int blockSize = batoi(toRead);
    toRead = new byte[1];
    in.read(toRead);
    // right now I'm gonna skip all the header info
    // check to see whether or not this is v2 data
    if (toRead[0] == 1) in.skipBytes(128);
    in.skipBytes(169);
    // read in the block of data
    toRead = new byte[blockSize];
    int read = 0;
    int left = blockSize;
    while (left > 0) {
      int i = in.read(toRead, read, left);
      read += i;
      left -= i;
    }
    byte[] pixelData = new byte[blockSize];
    int pixPos = 0;

    Dimension dim;
    try { dim = pictReader.getDimensions(toRead); }
    catch (Exception e) { dim = new Dimension(0, 0); }

    int length = toRead.length;
    int num, size, blockEnd;
    int totalBlocks = -1; // set to allow loop to start.
    int expectedBlock = 0;
    int pos = 0;
    int imagePos = 0;
    int imageSize = dim.width * dim.height;
    short[] flatSamples = new short[imageSize];
    byte[] temp;
    boolean skipflag;

    // read in deep gray pixel data into an array, and create a
    // BufferedImage out of it
    //
    // First, checks the existence of a deep gray block. If it doesn't exist,
    // assume it is PICT data, and attempt to read it.

    // check whether or not there is deep gray data
    while (expectedBlock != totalBlocks) {
      skipflag = false;
      while (pos + 7 < length &&
        (toRead[pos] != 73 || toRead[pos + 1] != 86 ||
        toRead[pos + 2] != 69 || toRead[pos + 3] != 65 ||
        toRead[pos + 4] != 100 || toRead[pos + 5] != 98 ||
        toRead[pos + 6] != 112 || toRead[pos + 7] != 113))
      {
        pos++;
      }
      if (pos + 32 > length) { // The header is 32 bytes long.
        if (expectedBlock == 0 && imageType[no] < 9) {
          // there has been no deep gray data, and it is supposed
          // to be a pict... *crosses fingers*
          try { return pictReader.openBytes(toRead); }
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
      temp = new byte[] {
        toRead[pos], toRead[pos+1], toRead[pos+2], toRead[pos+3]
      };
      num = batoi(temp);
      if (num != expectedBlock) {
        throw new FormatException("Expected iPic block not found");
      }
      expectedBlock++;
      temp = new byte[] {
        toRead[pos+4], toRead[pos+5], toRead[pos+6], toRead[pos+7]
      };
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
      temp = new byte[] {
        toRead[pos], toRead[pos+1], toRead[pos+2], toRead[pos+3]
      };
      size = batoi(temp);
      pos += 8;
      blockEnd = pos + size;

      // copy into our data array.
      System.arraycopy(toRead, pos, pixelData, pixPos, size);
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
    in = new RandomAccessFile(id, "r");

    // initialize an array containing tag offsets, so we can
    // use an O(1) search instead of O(n) later.
    // Also determine whether we will be reading color or grayscale
    // images

    //in.seek(0);
    byte[] toRead = new byte[4];
    in.read(toRead);
    long order = batoi(toRead);  // byte ordering
    little = toRead[2] != 0xff || toRead[3] != 0xff;

    toRead = new byte[4];
    Vector v = new Vector(); // a temp vector containing offsets.

    // Get first offset.
    in.seek(16);
    in.read(toRead);
    int nextOffset = batoi(toRead);
    int nextOffsetTemp;

    boolean first = true;
    while(nextOffset != 0) {
      in.seek(nextOffset + 4);
      in.read(toRead);
      // get next tag, but still need this one
      nextOffsetTemp = batoi(toRead);
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
    toRead = new byte[2];
    imageType = new int[numBlocks];
    for (int i = 0; i < numBlocks; i++) {
      in.seek(offsets[i]);
      in.skipBytes(40);
      in.read(toRead);
      imageType[i] = batoi(toRead);
    }

    initMetadata();
  }

  // -- Helper methods --

  /** Populates the metadata hashtable. */
  private void initMetadata() throws IOException {

    // start by reading the file header
    in.seek(0);
    byte[] toRead = new byte[4];
    in.read(toRead);
    long order = batoi(toRead);  // byte ordering
    little = toRead[2] != 0xff || toRead[3] != 0xff;
    metadata.put("Byte Order", new Boolean(little));

    in.skipBytes(4);
    in.read(toRead);
    long version = DataTools.bytesToLong(toRead, little);
    metadata.put("Version", new Long(version));

    byte[] er = new byte[2];
    in.read(er);
    short count = DataTools.bytesToShort(er, little);
    metadata.put("Count", new Short(count));

    in.skipBytes(2);

    in.read(toRead);
    long offset = DataTools.bytesToLong(toRead, little);

    // skip to first tag
    in.seek(offset);

    // read in each tag and its data

    for (int i=0; i<count; i++) {
      in.read(er);
      short tag = DataTools.bytesToShort(er, little);
      in.skipBytes(2);

      in.read(toRead);
      offset = DataTools.bytesToLong(toRead, little);

      in.read(toRead);
      long fmt = DataTools.bytesToLong(toRead, little);
      metadata.put("Format", new Long(fmt));

      in.read(toRead);
      long numBytes = DataTools.bytesToLong(toRead, little);
      metadata.put("NumBytes", new Long(numBytes));

      if (tag == 67 || tag == 68) {
        byte[] b = new byte[1];
        in.read(b);
        boolean isOpenlab2;
        if (b[0] == '0') isOpenlab2 = false;
        else isOpenlab2 = true;
        metadata.put("isOpenlab2", new Boolean(isOpenlab2));

        in.skipBytes(2);
        in.read(er);
        short layerId = DataTools.bytesToShort(er, little);
        metadata.put("LayerID", new Short(layerId));

        in.read(er);
        short layerType = DataTools.bytesToShort(er, little);
        metadata.put("LayerType", new Short(layerType));

        in.read(er);
        short bitDepth = DataTools.bytesToShort(er, little);
        metadata.put("BitDepth", new Short(bitDepth));

        in.read(er);
        short opacity = DataTools.bytesToShort(er, little);
        metadata.put("Opacity", new Short(opacity));

        // not sure how many bytes to skip here
        in.skipBytes(10);

        in.read(toRead);
        long type = DataTools.bytesToLong(toRead, little);
        metadata.put("ImageType", new Long(type));

        // not sure how many bytes to skip
        in.skipBytes(10);

        in.read(toRead);
        long timestamp = DataTools.bytesToLong(toRead, little);
        metadata.put("Timestamp", new Long(timestamp));

        in.skipBytes(2);

        if (isOpenlab2 == true) {
          byte[] layerName = new byte[127];
          in.read(layerName);
          metadata.put("LayerName", new String(layerName));

          in.read(toRead);
          long timestampMS = DataTools.bytesToLong(toRead, little);
          metadata.put("Timestamp-MS", new Long(timestampMS));

          in.skipBytes(1);
          byte[] notes = new byte[118];
          in.read(notes);
          metadata.put("Notes", new String(notes));
        }
        else in.skipBytes(123);
      }
      else if (tag == 69) {
        in.read(toRead);
        long platform = DataTools.bytesToLong(toRead, little);
        metadata.put("Platform", new Long(platform));

        in.read(er);
        short units = DataTools.bytesToShort(er, little);
        metadata.put("Units", new Short(units));

        in.read(er);
        short imageId = DataTools.bytesToShort(er, little);
        metadata.put("ID", new Short(imageId));
        in.skipBytes(1);

        byte[] toRead2 = new byte[8];
        double xOrigin = DataTools.readDouble(in, little);
        metadata.put("XOrigin", new Double(xOrigin));
        double yOrigin = DataTools.readDouble(in, little);
        metadata.put("YOrigin", new Double(yOrigin));
        double xScale = DataTools.readDouble(in, little);
        metadata.put("XScale", new Double(xScale));
        double yScale = DataTools.readDouble(in, little);
        metadata.put("YScale", new Double(yScale));
        in.skipBytes(1);

        byte[] other = new byte[31];
        in.read(other);
        metadata.put("Other", new String(other));
      }

      // Initialize OME metadata

      if (ome != null) {
        OMETools.setBigEndian(ome, !little);
        if (metadata.get("BitDepth") != null) {
          int bitDepth = ((Integer) metadata.get("BitDepth")).intValue();
          String type;

          if (bitDepth <= 8) type = "int8";
          else if (bitDepth <= 16) type = "int16";
          else type = "int32";

          OMETools.setPixelType(ome, type);
        }
        if (metadata.get("Timestamp") != null) {
          OMETools.setCreationDate(ome, (String) metadata.get("Timestamp"));
        }

        if (metadata.get("XOrigin") != null) {
          Double xOrigin = (Double) metadata.get("XOrigin");
          OMETools.setStageX(ome, xOrigin.floatValue());
        }

        if (metadata.get("YOrigin") != null) {
          Double yOrigin = (Double) metadata.get("YOrigin");
          OMETools.setStageY(ome, yOrigin.floatValue());
        }

        if (metadata.get("XScale") != null) {
          Double xScale = (Double) metadata.get("XScale");
          OMETools.setPixelSizeX(ome, xScale.floatValue());
        }

        if (metadata.get("YScale") != null) {
          Double yScale = (Double) metadata.get("YScale");
          OMETools.setPixelSizeY(ome, yScale.floatValue());
        }
      }
      in.seek(offset);
    }
  }

  // -- Helper method --

  /** Translates up to the first 4 bytes of a byte array to an integer. */
  private static int batoi(byte[] inp) {
    int len = inp.length>4?4:inp.length;
    int total = 0;
    for (int i = 0; i < len; i++) {
      total += (inp[i]<0?256+inp[i]:(int)inp[i]) << (((len - 1) - i) * 8);
    }
    return total;
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new OpenlabReader().testRead(args);
  }

}
