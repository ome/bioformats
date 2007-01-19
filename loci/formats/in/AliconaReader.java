//
// AliconaReader.java
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

/** AliconaReader is the file format reader for Alicona AL3D files. */
public class AliconaReader extends FormatReader {

  // -- Fields --

  /** Current file. */
  protected RandomAccessStream in;

  /** Number of image planes in the file. */
  protected int numImages = 0;

  /** Image width. */
  private int width;

  /** Image height. */
  private int height;

  /** Number of channels. */
  private int channels;

  /** Image offset. */
  private int textureOffset;

  /** Number of bytes per pixel (either 1 or 2). */
  private int numBytes;

  // -- Constructor --

  /** Constructs a new Alicona reader. */
  public AliconaReader() { super("Alicona AL3D", "al3d"); }

  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for an Alicona file. */
  public boolean isThisType(byte[] block) {
    return (new String(block)).indexOf("Alicona") != -1;
  }

  /** Determines the number of images in the given Alicona file. */
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

  /**
   * Obtains the specified image from the
   * given Alicona file as a byte array.
   */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    int pad = 8 - (width % 8);
    if (pad == 8) pad = 0;

    in.seek(textureOffset +
      (no * (width + pad) * height * numBytes));

    byte[] plane = new byte[(width + pad) * height * numBytes];
    in.read(plane);

    byte[] lsbData = new byte[width * height];
    byte[] msbData = new byte[width * height];

    for (int i=0; i<height * numBytes; i++) {
      if (i < height) {
        System.arraycopy(plane, i*(width+pad), lsbData, i*width, width);
      }
      else {
        System.arraycopy(plane, (i % height)*(width + pad), msbData,
          (i % height)*width, width);
      }
    }

    if (numBytes == 2) {
      byte[] data = new byte[width * height * 2];
      for (int i=0; i<width*height; i++) {
        data[i*2] = lsbData[i];
        data[i*2 + 1] = msbData[i];
      }
      return data;
    }
    return lsbData;
  }

  /** Obtains the specified image from the given Alicona file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    return ImageTools.makeImage(openBytes(id, no), width, height, 1, false,
      numBytes, true);
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  /** Initializes the given Alicona file. */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    // check that this is a valid AL3D file

    byte[] check = new byte[17];
    in.read(check);
    String magicString = new String(check);
    if (!magicString.trim().equals("AliconaImaging")) {
      throw new FormatException("Invalid magic string : " +
        "expected 'AliconaImaging', got " + magicString);
    }

    // now we read a series of tags
    // each one is 52 bytes - 20 byte key + 30 byte value + 2 byte CRLF

    byte[] keyBytes = new byte[20];
    byte[] valueBytes = new byte[30];

    int count = 2;

    for (int i=0; i<count; i++) {
      in.read(keyBytes);
      in.read(valueBytes);
      in.skipBytes(2);

      String key = new String(keyBytes);
      String value = new String(valueBytes);
      key = key.trim();
      value = value.trim();

      addMeta(key, value);

      if (key.equals("TagCount")) count += Integer.parseInt(value);
      else if (key.equals("Rows")) height = Integer.parseInt(value);
      else if (key.equals("Cols")) width = Integer.parseInt(value);
      else if (key.equals("NumberOfPlanes")) {
        numImages = Integer.parseInt(value);
      }
      else if (key.equals("TextureImageOffset")) {
        textureOffset = Integer.parseInt(value);
      }
    }

    numBytes =
      (int) (in.length() - textureOffset) / (width * height * numImages);

    boolean hasC = !((String) getMeta("TexturePtr")).trim().equals("7");

    sizeX[0] = width;
    sizeY[0] = height;
    sizeC[0] = hasC ? 3 : 1;
    sizeZ[0] = 1;
    sizeT[0] = numImages / sizeC[0];

    pixelType[0] = numBytes == 2 ? FormatReader.UINT16 : FormatReader.UINT8;
    currentOrder[0] = "XYCTZ";

    MetadataStore store = getMetadataStore(id);
    store.setPixels(
      new Integer(width),
      new Integer(height),
      new Integer(sizeZ[0]),
      new Integer(sizeC[0]),
      new Integer(sizeT[0]),
      new Integer(pixelType[0]),
      new Boolean(true),
      "XYCTZ",
      null
    );

    store.setDimensions(
      new Float(((String) getMeta("PlanePntX")).trim()),
      new Float(((String) getMeta("PlanePntY")).trim()),
      new Float(((String) getMeta("PlanePntZ")).trim()), null, null, null);
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new AliconaReader().testRead(args);
  }

}
