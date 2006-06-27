//
// ImarisReader.java
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
 * ImarisReader is the file format reader for Bitplane Imaris files.
 * Specifications available at
 * http://flash.bitplane.com/support/faqs/faqsview.cfm?inCat=6&inQuestionID=104
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class ImarisReader extends FormatReader {

  // -- Constants --

  /** Magic number; present in all files. */
  private static final int IMARIS_MAGIC_NUMBER = 5021964;

  /** Specifies endianness. */
  private static final boolean IS_LITTLE = false;


  // -- Fields --

  /** Current file. */
  protected RandomAccessStream in;

  /** Number of image planes in the file. */
  protected int numImages = 0;

  /** Image dimensions: width, height, z, channels. */
  private int[] dims;

  /** Offsets to each image. */
  private int[] offsets;


  // -- Constructor --

  /** Constructs a new Imaris reader. */
  public ImarisReader() { super("Bitplane Imaris", "ims"); }


  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for an Imaris file. */
  public boolean isThisType(byte[] block) {
    return DataTools.bytesToInt(block, 0, 4, IS_LITTLE) == IMARIS_MAGIC_NUMBER;
  }

  /** Determines the number of images in the given Imaris file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return numImages;
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    return false;
  }

  /** Returns the number of channels in the file. */
  public int getChannelCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return dims[3];
  }

  /**
   * Obtains the specified image from the
   * given Imaris file as a byte array.
   */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    in.seek(offsets[no]);
    byte[] data = new byte[dims[0] * dims[1]];

    int row = dims[1] - 1;
    for (int i=0; i<dims[1]; i++) {
      in.read(data, row*dims[0], dims[0]);
      row--;
    }

    return data;
  }

  /** Obtains the specified image from the given Imaris file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    return ImageTools.makeImage(openBytes(id, no), dims[0], dims[1], 1, false);
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  /** Initializes the given Imaris file. */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessStream(id);
    dims = new int[4];

    long magic = DataTools.read4UnsignedBytes(in, IS_LITTLE);
    if (magic != IMARIS_MAGIC_NUMBER) {
      throw new FormatException("Imaris magic number not found.");
    }

    int version = DataTools.read4SignedBytes(in, IS_LITTLE);
    metadata.put("Version", new Integer(version));
    in.skipBytes(4);

    byte[] name = new byte[128];
    in.read(name);
    String iName = new String(name);
    metadata.put("Image name", iName);

    dims[0] = DataTools.read2SignedBytes(in, IS_LITTLE);
    dims[1] = DataTools.read2SignedBytes(in, IS_LITTLE);
    dims[2] = DataTools.read2SignedBytes(in, IS_LITTLE);

    in.skipBytes(2); // data type, ignore for now

    dims[3] = DataTools.read4SignedBytes(in, IS_LITTLE);
    in.skipBytes(2);

    byte[] date = new byte[32];
    in.read(date);
    String origDate = new String(date);
    metadata.put("Original date", origDate);

    float dx = DataTools.readFloat(in, IS_LITTLE);
    float dy = DataTools.readFloat(in, IS_LITTLE);
    float dz = DataTools.readFloat(in, IS_LITTLE);
    short mag = DataTools.read2SignedBytes(in, IS_LITTLE);

    byte[] com = new byte[128];
    in.read(com);
    String comment = new String(com);
    metadata.put("Image comment", comment);
    int isSurvey = DataTools.read4SignedBytes(in, IS_LITTLE);
    metadata.put("Survey performed", isSurvey == 0 ? "true" : "false");

    numImages = dims[2] * dims[3];
    offsets = new int[numImages];

    for (int i=0; i<dims[3]; i++) {
      int offset = 332 + ((i + 1) * 168) + (i * dims[0] * dims[1] * dims[2]);
      for (int j=0; j<dims[2]; j++) {
        offsets[i*dims[2] + j] = offset + (j * dims[0] * dims[1]);
      }
    }

    if (ome != null) {
      OMETools.setPixels(ome,
        new Integer(dims[0]),
        new Integer(dims[1]),
        new Integer(dims[2]),
        new Integer(dims[3]),
        new Integer(1),
        "int8",
        new Boolean(!IS_LITTLE),
        "XYZCT");

      OMETools.setImage(ome,
        (String) metadata.get("Image name"),
        (String) metadata.get("Original date"),
        (String) metadata.get("Image comment"));

      OMETools.setDimensions(ome,
        new Float(dx),
        new Float(dy),
        new Float(dz),
        new Float(1),
        new Float(1));
    }
  }


  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new ImarisReader().testRead(args);
  }

}
