//
// ImarisReader.java
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

/**
 * ImarisReader is the file format reader for Bitplane Imaris files.
 * Specifications available at
 * http://flash.bitplane.com/support/faqs/faqsview.cfm?inCat=6&inQuestionID=104
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class ImarisReader extends FormatReader {

  // -- Constants --

  /** Magic number; present in all files. */
  private static final int IMARIS_MAGIC_NUMBER = 5021964;

  /** Specifies endianness. */
  private static final boolean IS_LITTLE = false;

  // -- Fields --

  /** Offsets to each image. */
  private int[] offsets;

  // -- Constructor --

  /** Constructs a new Imaris reader. */
  public ImarisReader() { super("Bitplane Imaris", "ims"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return DataTools.bytesToInt(block, 0, 4, IS_LITTLE) == IMARIS_MAGIC_NUMBER;
  }

  /* @see loci.formats.IFormatReader#openBytes(int) */
  public byte[] openBytes(int no) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    byte[] buf = new byte[core.sizeX[0] * core.sizeY[0]];
    return openBytes(no, buf);
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    if (no < 0 || no >= getImageCount()) {
      throw new FormatException("Invalid image number: " + no);
    }
    if (buf.length < core.sizeX[0] * core.sizeY[0]) {
      throw new FormatException("Buffer too small.");
    }

    in.seek(offsets[no]);
    int row = core.sizeY[0] - 1;
    for (int i=0; i<core.sizeY[0]; i++) {
      in.read(buf, row*core.sizeX[0], core.sizeX[0]);
      row--;
    }
    return buf;
  }

  /* @see loci.formats.IFormatReader#openImage(int) */
  public BufferedImage openImage(int no) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    return ImageTools.makeImage(openBytes(no), core.sizeX[0],
      core.sizeY[0], 1, false);
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    if (!super.isThisType(name, open)) return false; // check extension
    if (!open) return true; // not allowed to check the file contents
    try {
      RandomAccessStream ras = new RandomAccessStream(name);
      byte[] b = new byte[4];
      ras.readFully(b);
      ras.close();
      return isThisType(b);
    }
    catch (IOException e) { return false; }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("ImarisReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    status("Verifying Imaris RAW format");

    in.order(IS_LITTLE);

    long magic = in.readInt();
    if (magic != IMARIS_MAGIC_NUMBER) {
      throw new FormatException("Imaris magic number not found.");
    }

    status("Reading header");

    int version = in.readInt();
    addMeta("Version", new Integer(version));
    in.readInt();

    byte[] name = new byte[128];
    in.read(name);
    String iName = new String(name);
    addMeta("Image name", iName);

    core.sizeX[0] = in.readShort();
    core.sizeY[0] = in.readShort();
    core.sizeZ[0] = in.readShort();

    in.skipBytes(2);

    core.sizeC[0] = in.readInt();
    in.skipBytes(2);

    byte[] date = new byte[32];
    in.read(date);
    String origDate = new String(date);
    addMeta("Original date", origDate);

    float dx = in.readFloat();
    float dy = in.readFloat();
    float dz = in.readFloat();
    int mag = in.readShort();

    byte[] com = new byte[128];
    in.read(com);
    String comment = new String(com);
    addMeta("Image comment", comment);
    int isSurvey = in.readInt();
    addMeta("Survey performed", isSurvey == 0 ? "true" : "false");

    status("Calculating image offsets");

    core.imageCount[0] = core.sizeZ[0] * core.sizeC[0];
    offsets = new int[core.imageCount[0]];

    for (int i=0; i<core.sizeC[0]; i++) {
      int offset = 332 + ((i + 1) * 168) + (i * core.sizeX[0] *
        core.sizeY[0] * core.sizeZ[0]);
      for (int j=0; j<core.sizeZ[0]; j++) {
        offsets[i*core.sizeZ[0] + j] =
          offset + (j * core.sizeX[0] * core.sizeY[0]);
      }
    }

    status("Populating metadata");

    core.sizeT[0] = core.imageCount[0] / (core.sizeC[0] * core.sizeZ[0]);
    core.currentOrder[0] = "XYZCT";
    core.rgb[0] = false;
    core.interleaved[0] = false;
    core.littleEndian[0] = IS_LITTLE;

    // The metadata store we're working with.
    MetadataStore store = getMetadataStore();

    core.pixelType[0] = FormatTools.UINT8;
    store.setPixels(
      new Integer(core.sizeX[0]),
      new Integer(core.sizeY[0]),
      new Integer(core.sizeZ[0]),
      new Integer(core.sizeC[0]),
      new Integer(core.sizeT[0]),
      new Integer(core.pixelType[0]),
      new Boolean(!IS_LITTLE),
      core.currentOrder[0],
      null,
      null);

    store.setImage(
      (String) getMeta("Image name"),
      (String) getMeta("Original date"),
      (String) getMeta("Image comment"),
      null);

    store.setDimensions(
      new Float(dx),
      new Float(dy),
      new Float(dz),
      new Float(1),
      new Float(1),
      null);

    store.setObjective(null, null, null, null, new Float(mag), null, null);

    for (int i=0; i<core.sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null);
    }
  }

}
