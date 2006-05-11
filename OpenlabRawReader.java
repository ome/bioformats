//
// OpenlabRawReader.java
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
import java.io.*;

/**
 * OpenlabRawReader is the file format reader for Openlab RAW files.
 * Specifications available at
 * http://www.improvision.com/support/tech_notes/detail.php?id=344
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class OpenlabRawReader extends FormatReader {

  // -- Fields --

  /** Current file. */
  protected DataInputStream in;

  /** Number of image planes in the file. */
  protected int numImages = 0;

  /** Offset to each image's pixel data. */
  protected int[] offsets;

  /** File length. */
  private int fileLength;

  /** Image width. */
  private int width;

  /** Image height. */
  private int height;

  /** Number of channels. */
  private int channels;
 
  /** Number of bytes per pixel. */
  private int bpp;
  
  // -- Constructor --

  /** Constructs a new RAW reader. */
  public OpenlabRawReader() { super("Openlab RAW", "raw"); }


  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for a RAW file. */
  public boolean isThisType(byte[] block) {
    return (block[0] == 'O') && (block[1] == 'L') && (block[2] == 'R') &&
      (block[3] == 'W');
  }

  /** Determines the number of images in the given RAW file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return numImages;
  }

  /** Obtains the specified image from the given RAW file as a byte array. */
  public byte[] openBytes(String id, int no) 
    throws FormatException, IOException
  {
    throw new FormatException("OpenlabRawReader.openBytes(String, int) " +
      "not implemented");
  }

  /** Obtains the specified image from the given RAW file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    in.reset();
    if ((fileLength - in.available()) < offsets[no]) {
      in.skipBytes((int) (in.available() - fileLength + offsets[no]));
    }        
 
    in.skipBytes(288);
    
    byte[] data = new byte[width*height*bpp];
    in.read(data);
    in.mark(100);
    
    if (bpp == 1) {
      // need to invert the pixels
      for (int i=0; i<data.length; i++) {
        data[i] = (byte) (255 - data[i]);
      }
      return ImageTools.makeImage(data, width, height, channels, false);
    }
    else if (bpp == 2) {
      short[] shortData = new short[width*height];
      for (int i=0; i<data.length; i+=2) {
        shortData[i/2] = DataTools.bytesToShort(data, i, 2, false);
      }
      return ImageTools.makeImage(shortData, width, height, channels, false);
    }
    else if (bpp == 4) {
      float[] floatData = new float[width*height];
      for (int i=0; i<data.length; i+=4) {
        floatData[i/2] =
          Float.intBitsToFloat(DataTools.bytesToInt(data, i, 4, false));
      }
      return ImageTools.makeImage(floatData, width, height, channels, false);
    }
    else throw new FormatException("Unsupported bytes per pixel : " + bpp);
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  /** Initializes the given RAW file. */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new DataInputStream(
      new BufferedInputStream(new FileInputStream(id), 4096));
    fileLength = in.available();
    
    // read the 12 byte file header

    byte[] header = new byte[12];
    in.read(header);
    if (header[0] != 'O' || header[1] != 'L' ||
      header[2] != 'R' || header[3] != 'W')
    {
      throw new FormatException("Openlab RAW magic string not found.");
    }

    int version = DataTools.bytesToInt(header, 4, 4, false);
    metadata.put("Version", new Integer(version));

    numImages = DataTools.bytesToInt(header, 8, 4, false);
    in.mark(100);
    offsets = new int[numImages];
    offsets[0] = 12;

    in.skipBytes(8);
    width = DataTools.read4SignedBytes(in, false);
    height = DataTools.read4SignedBytes(in, false);
    in.skipBytes(2);
    bpp = DataTools.readUnsignedByte(in);
    channels = DataTools.readUnsignedByte(in);
    if (channels <= 1) channels = 1;
    else channels = 3;
    metadata.put("Width", new Integer(width));
    metadata.put("Height", new Integer(height));
    metadata.put("Bytes per pixel", new Integer(bpp));

    for (int i=1; i<numImages; i++) {
      offsets[i] = offsets[i-1] + 288 + width*height*bpp;
    }

    if (ome != null) {
      bpp = ((Integer) metadata.get("Bytes per pixel")).intValue();

      OMETools.setPixels(ome,
        (Integer) metadata.get("Width"),
        (Integer) metadata.get("Height"),
        new Integer(numImages),
        new Integer(channels),
        new Integer(1),
        (bpp < 4) ? ("int" + (8*bpp)) : "float",
        new Boolean(true),
        "XYZTC");
    }
  }


  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new OpenlabRawReader().testRead(args);
  }

}
