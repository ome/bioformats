//
// AliconaReader.java
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
import java.io.*;

import loci.formats.*;

/** AliconaReader is the file format reader for Alicona AL3D files. */
public class AliconaReader extends FormatReader {

  // -- Fields --

  /** Current file. */
  protected RandomAccessStream in;

  /** Number of image planes in the file. */
  protected int numImages = 0;

  /** Image offset. */
  private int textureOffset;

  /** Number of bytes per pixel (either 1 or 2). */
  private int numBytes;

  // -- Constructor --

  /** Constructs a new Alicona reader. */
  public AliconaReader() { super("Alicona AL3D", "al3d"); }

  // -- FormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */ 
  public boolean isThisType(byte[] block) {
    return (new String(block)).indexOf("Alicona") != -1;
  }
 
  /* @see loci.formats.IFormatReader#getImageCount(String) */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return numImages;
  }

  /* @see loci.formats.IFormatReader#isRGB(String) */
  public boolean isRGB(String id) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#isLittleEndian(String) */ 
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    return true;
  }

  /* @see loci.formats.IFormatReader#isInterleaved(String, int) */ 
  public boolean isInterleaved(String id, int subC)
    throws FormatException, IOException
  {
    return false;
  }

  /* @see loci.formats.IFormatReader#openBytes(String, int) */ 
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    byte[] buf = new byte[core.sizeX[0] * core.sizeY[0] * numBytes];
    return openBytes(id, no, buf);
  }

  /* @see loci.formats.IFormatReader#openBytes(String, int, byte[]) */
  public byte[] openBytes(String id, int no, byte[] buf)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    int pad = (8 - (core.sizeX[0] % 8)) % 8;

    if (buf.length < core.sizeX[0] * core.sizeY[0] * numBytes) {
      throw new FormatException("Buffer to small.");
    }

    for (int i=0; i<numBytes; i++) {
      in.seek(textureOffset + (no * (core.sizeX[0] + pad)*core.sizeY[0]*(i+1)));
      for (int j=0; j<core.sizeX[0] * core.sizeY[0]; j++) {
        buf[j*numBytes + i] = (byte) in.read();
        if (j % core.sizeX[0] == core.sizeX[0] - 1) in.skipBytes(pad);
      }
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#openImage(String, int) */ 
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    return ImageTools.makeImage(openBytes(id, no), core.sizeX[0], core.sizeY[0],
      1, false, numBytes, true);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws FormatException, IOException {
    if (fileOnly && in != null) in.close();
    else if (!fileOnly) close();
  }

  /* @see loci.formats.IFormatReader#close() */ 
  public void close() throws FormatException, IOException {
    if (in != null) in.close();
    in = null;
    currentId = null;
  }

  /** Initializes the given Alicona file. */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("AliconaReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    // check that this is a valid AL3D file
    status("Verifying Alicona format");
    byte[] check = new byte[17];
    in.read(check);
    String magicString = new String(check);
    if (!magicString.trim().equals("AliconaImaging")) {
      throw new FormatException("Invalid magic string : " +
        "expected 'AliconaImaging', got " + magicString);
    }

    // now we read a series of tags
    // each one is 52 bytes - 20 byte key + 30 byte value + 2 byte CRLF

    status("Reading tags");

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
      else if (key.equals("Rows")) core.sizeY[0] = Integer.parseInt(value);
      else if (key.equals("Cols")) core.sizeX[0] = Integer.parseInt(value);
      else if (key.equals("NumberOfPlanes")) {
        numImages = Integer.parseInt(value);
      }
      else if (key.equals("TextureImageOffset")) {
        textureOffset = Integer.parseInt(value);
      }
    }

    status("Populating metadata");

    numBytes = (int) (in.length() - textureOffset) / 
      (core.sizeX[0] * core.sizeY[0] * numImages);

    boolean hasC = !((String) getMeta("TexturePtr")).trim().equals("7");

    core.sizeC[0] = hasC ? 3 : 1;
    core.sizeZ[0] = 1;
    core.sizeT[0] = numImages / core.sizeC[0];

    core.pixelType[0] = numBytes == 2 ? FormatTools.UINT16 : FormatTools.UINT8;
    core.currentOrder[0] = "XYCTZ";

    MetadataStore store = getMetadataStore(id);
    store.setPixels(
      new Integer(core.sizeX[0]),
      new Integer(core.sizeY[0]),
      new Integer(core.sizeZ[0]),
      new Integer(core.sizeC[0]),
      new Integer(core.sizeT[0]),
      new Integer(core.pixelType[0]),
      new Boolean(true),
      core.currentOrder[0],
      null,
      null
    );

    if (getMeta("Voltage") != null) {
      store.setDetector(null, null, null, null, null,
        new Float((String) getMeta("Voltage")), null, null, null);
    }
    if (getMeta("Magnification") != null) {
      store.setObjective(null, null, null, null,
        new Float((String) getMeta("Magnification")), null, null);
    }

    if (getMeta("PlanePntX") != null && getMeta("PlanePntY") != null &&
      getMeta("PlanePntZ") != null)
    {
      store.setDimensions(
        new Float(((String) getMeta("PlanePntX")).trim()),
        new Float(((String) getMeta("PlanePntY")).trim()),
        new Float(((String) getMeta("PlanePntZ")).trim()), null, null, null);
    }

    for (int i=0; i<core.sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null);
    }
  }

}
