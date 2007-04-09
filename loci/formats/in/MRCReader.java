//
// MRCReader.java
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
 * MRCReader is the file format reader for MRC files.
 * Specifications available at
 * http://bio3d.colorado.edu/imod/doc/mrc_format.txt
 */
public class MRCReader extends FormatReader {

  // -- Fields --

  /** Current file. */
  protected RandomAccessStream in;

  /** Number of image planes in the file. */
  protected int numImages = 0;

  /** Number of bytes per pixel */
  private int bpp = 0;

  /** Size of extended header */
  private int extHeaderSize = 0;

  /** Flag set to true if we are using float data. */
  private boolean isFloat = false;

  /** Flag set to true if data is little-endian. */
  private boolean little;

  // -- Constructor --

  /** Constructs a new MRC reader. */
  public MRCReader() {
    super("Medical Research Council (MRC)", "mrc");
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */ 
  public boolean isThisType(byte[] block) {
    return false; // no way to tell if this is an MRC file or not
  }

  /* @see loci.formats.IFormatReader#getImageCount() */ 
  public int getImageCount() throws FormatException, IOException {
    return numImages;
  }

  /* @see loci.formats.IFormatReader#isRGB() */ 
  public boolean isRGB() throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#isLittleEndian() */ 
  public boolean isLittleEndian() throws FormatException, IOException {
    return little;
  }

  /* @see loci.formats.IFormatReader#isInterleaved(int) */ 
  public boolean isInterleaved(int subC) throws FormatException, IOException {
    return true;
  }

  /* @see loci.formats.IFormatReader#openBytes(int) */ 
  public byte[] openBytes(int no) throws FormatException, IOException {
    byte[] buf = new byte[core.sizeX[0] * core.sizeY[0] * bpp];
    return openBytes(no, buf);
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    if (no < 0 || no >= getImageCount()) {
      throw new FormatException("Invalid image number: " + no);
    }
    if (buf.length < core.sizeX[0] * core.sizeY[0] * bpp) {
      throw new FormatException("Buffer too small.");
    }
    in.seek(1024 + extHeaderSize + (no * core.sizeX[0] * core.sizeY[0] * bpp));
    in.read(buf);
    return buf;
  }

  /* @see loci.formats.IFormatReader#openImage(int) */ 
  public BufferedImage openImage(int no) throws FormatException, IOException {
    return ImageTools.makeImage(openBytes(no), core.sizeX[0],
      core.sizeY[0], 1, true, bpp, little);
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

  /** Initializes the given MRC file. */
  public void initFile(String id) throws FormatException, IOException {
    if (debug) debug("MRCReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    status("Reading header");

    // check endianness

    in.seek(213);
    little = in.read() == 68;

    // read 1024 byte header

    in.seek(0);
    byte[] b = new byte[4];

    in.read(b);
    core.sizeX[0] = DataTools.bytesToInt(b, little);
    in.read(b);
    core.sizeY[0] = DataTools.bytesToInt(b, little);
    in.read(b);
    core.sizeZ[0] = DataTools.bytesToInt(b, little);

    core.sizeC[0] = 1;

    in.read(b);
    int mode = DataTools.bytesToInt(b, little);
    switch (mode) {
      case 0:
        bpp = 1;
        core.pixelType[0] = FormatTools.UINT8;
        break;
      case 1:
        bpp = 2;
        core.pixelType[0] = FormatTools.UINT16;
        break;
      case 2:
        bpp = 4;
        isFloat = true;
        core.pixelType[0] = FormatTools.FLOAT;
        break;
      case 3:
        bpp = 4;
        core.pixelType[0] = FormatTools.UINT32;
        break;
      case 4:
        bpp = 8;
        isFloat = true;
        core.pixelType[0] = FormatTools.DOUBLE;
        break;
      case 6:
        bpp = 2;
        core.pixelType[0] = FormatTools.UINT16;
        break;
      case 16:
        bpp = 2;
        core.sizeC[0] = 3;
        core.pixelType[0] = FormatTools.UINT16;
        break;
    }

    in.read(b);
    int thumbX = DataTools.bytesToInt(b, little);
    in.read(b);
    int thumbY = DataTools.bytesToInt(b, little);
    in.read(b);
    int thumbZ = DataTools.bytesToInt(b, little);

    // pixel size = xlen / mx

    in.read(b);
    int mx = DataTools.bytesToInt(b, little);
    in.read(b);
    int my = DataTools.bytesToInt(b, little);
    in.read(b);
    int mz = DataTools.bytesToInt(b, little);

    in.read(b);
    float xlen = Float.intBitsToFloat(DataTools.bytesToInt(b, little));
    in.read(b);
    float ylen = Float.intBitsToFloat(DataTools.bytesToInt(b, little));
    in.read(b);
    float zlen = Float.intBitsToFloat(DataTools.bytesToInt(b, little));

    addMeta("Pixel size (X)", "" + (xlen / mx));
    addMeta("Pixel size (Y)", "" + (ylen / my));
    addMeta("Pixel size (Z)", "" + (zlen / mz));

    in.read(b);
    float alpha = Float.intBitsToFloat(DataTools.bytesToInt(b, little));
    in.read(b);
    float beta = Float.intBitsToFloat(DataTools.bytesToInt(b, little));
    in.read(b);
    float gamma = Float.intBitsToFloat(DataTools.bytesToInt(b, little));

    addMeta("Alpha angle", "" + alpha);
    addMeta("Beta angle", "" + beta);
    addMeta("Gamma angle", "" + gamma);

    in.skipBytes(12);

    // min, max and mean pixel values

    in.read(b);
    float min = Float.intBitsToFloat(DataTools.bytesToInt(b, little));
    in.read(b);
    float max = Float.intBitsToFloat(DataTools.bytesToInt(b, little));
    in.read(b);
    float mean = Float.intBitsToFloat(DataTools.bytesToInt(b, little));

    addMeta("Minimum pixel value", "" + min);
    addMeta("Maximum pixel value", "" + max);
    addMeta("Mean pixel value", "" + mean);

    in.skipBytes(4);
    in.read(b);

    extHeaderSize = DataTools.bytesToInt(b, little);
    b = new byte[2];
    in.read(b);
    int creator = DataTools.bytesToInt(b, little);

    in.skipBytes(30);

    in.read(b);
    int nint = DataTools.bytesToInt(b, little);
    in.read(b);
    int nreal = DataTools.bytesToInt(b, little);

    in.skipBytes(28);

    in.read(b);
    int idtype = DataTools.bytesToInt(b, little);
    in.read(b);
    int lens = DataTools.bytesToInt(b, little);
    in.read(b);
    int nd1 = DataTools.bytesToInt(b, little);
    in.read(b);
    int nd2 = DataTools.bytesToInt(b, little);
    in.read(b);
    int vd1 = DataTools.bytesToInt(b, little);
    in.read(b);
    int vd2 = DataTools.bytesToInt(b, little);

    String type = "";
    switch (idtype) {
      case 0:
        type = "mono";
        break;
      case 1:
        type = "tilt";
        break;
      case 2:
        type = "tilts";
        break;
      case 3:
        type = "lina";
        break;
      case 4:
        type = "lins";
        break;
      default:
        type = "unknown";
    }

    addMeta("Series type", type);
    addMeta("Lens", "" + lens);
    addMeta("ND1", "" + nd1);
    addMeta("ND2", "" + nd2);
    addMeta("VD1", "" + vd1);
    addMeta("VD2", "" + vd2);

    b = new byte[4];
    float[] angles = new float[6];
    for (int i=0; i<angles.length; i++) {
      in.read(b);
      angles[i] = Float.intBitsToFloat(DataTools.bytesToInt(b, little));
      addMeta("Angle " + (i+1), "" + angles[i]);
    }

    in.skipBytes(24);

    in.read(b);
    int nUsefulLabels = DataTools.bytesToInt(b, little);
    addMeta("Number of useful labels", "" + nUsefulLabels);

    b = new byte[80];
    for (int i=0; i<10; i++) {
      in.read(b);
      addMeta("Label " + (i+1), new String(b));
    }

    in.skipBytes(extHeaderSize);

    status("Populating metadata");

    core.sizeT[0] = 1;
    core.currentOrder[0] = "XYZTC";
    numImages = core.sizeZ[0];

    MetadataStore store = getMetadataStore();
    store.setPixels(
      new Integer(core.sizeX[0]),
      new Integer(core.sizeY[0]),
      new Integer(core.sizeZ[0]),
      new Integer(core.sizeC[0]),
      new Integer(core.sizeT[0]),
      new Integer(core.pixelType[0]),
      new Boolean(!little),
      core.currentOrder[0],
      null,
      null);

    store.setDimensions(new Float(xlen / mx), new Float(ylen / my),
      new Float(zlen / mz), null, null, null);
    for (int i=0; i<core.sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null);
      // TODO : get channel min/max from metadata 
      //store.setChannelGlobalMinMax(i, getChannelGlobalMinimum(id, i),
      //  getChannelGlobalMaximum(id, i), null);
    }
  }

}
