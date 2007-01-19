//
// OpenlabRawReader.java
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
 * OpenlabRawReader is the file format reader for Openlab RAW files.
 * Specifications available at
 * http://www.improvision.com/support/tech_notes/detail.php?id=344
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class OpenlabRawReader extends FormatReader {

  // -- Fields --

  /** Current file. */
  protected RandomAccessStream in;

  /** Number of image planes in the file. */
  protected int numImages = 0;

  /** Offset to each image's pixel data. */
  protected int[] offsets;

  /** Image width. */
  private int width;

  /** Image height. */
  private int height;

  /** Number of channels. */
  private int channels;

  /** Number of bytes per pixel. */
  private int bytesPerPixel;

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

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return channels > 1;
  }

  /** Return true if the data is in little-endian format. */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    return false;
  }

  /** Returns whether or not the channels are interleaved. */
  public boolean isInterleaved(String id) throws FormatException, IOException {
    return false;
  }

  /** Obtains the specified image from the given RAW file as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    in.seek(offsets[no / channels] + 288);

    byte[] data = new byte[width*height*bytesPerPixel];
    in.read(data);

    if (bytesPerPixel == 1) {
      // need to invert the pixels
      for (int i=0; i<data.length; i++) {
        data[i] = (byte) (255 - data[i]);
      }
    }

    return data;
  }

  /** Obtains the specified image from the given RAW file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    return ImageTools.makeImage(openBytes(id, no), width, height,
      !isRGB(id) ? 1 : channels, false, bytesPerPixel, false);
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  /** Initializes the given RAW file. */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("OpenlabRawReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    // read the 12 byte file header

    byte[] header = new byte[4];
    in.read(header);
    String check = new String(header);
    if (!check.equals("OLRW")) {
      throw new FormatException("Openlab RAW magic string not found.");
    }

    int version = in.readInt();
    addMeta("Version", new Integer(version));

    numImages = in.readInt();
    offsets = new int[numImages];
    offsets[0] = 12;

    in.readLong();
    width = in.readInt();
    height = in.readInt();
    in.readShort();
    bytesPerPixel = in.read();
    channels = in.read();

    if (channels <= 1) channels = 1;
    else channels = 3;
    addMeta("Width", new Integer(width));
    addMeta("Height", new Integer(height));
    addMeta("Bytes per pixel", new Integer(bytesPerPixel));

    for (int i=1; i<numImages; i++) {
      offsets[i] = offsets[i-1] + 288 + width*height*bytesPerPixel;
    }

    bytesPerPixel = ((Integer) getMeta("Bytes per pixel")).intValue();

    sizeX[0] = width;
    sizeY[0] = height;
    sizeZ[0] = numImages;
    sizeC[0] = channels;
    sizeT[0] = 1;
    currentOrder[0] = "XYZTC";

    // The metadata store we're working with.
    MetadataStore store = getMetadataStore(id);

    switch (bytesPerPixel) {
      case 1:
        pixelType[0] = FormatReader.UINT8;
        break;
      case 2:
        pixelType[0] = FormatReader.UINT16;
        break;
      case 3:
        pixelType[0] = FormatReader.INT8;
        break;
      default:
        pixelType[0] = FormatReader.FLOAT;
    }

    store.setPixels(
      (Integer) getMeta("Width"),
      (Integer) getMeta("Height"),
      new Integer(numImages),
      new Integer(channels),
      new Integer(1),
      new Integer(pixelType[0]),
      new Boolean(true),
      "XYZTC",
      null);
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new OpenlabRawReader().testRead(args);
  }

}
