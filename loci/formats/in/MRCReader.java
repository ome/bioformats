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

  /** Number of bytes per pixel */
  private int bpp = 0;

  /** Size of extended header */
  private int extHeaderSize = 0;

  /** Flag set to true if we are using float data. */
  private boolean isFloat = false;

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

  /* @see loci.formats.IFormatReader#openBytes(int) */
  public byte[] openBytes(int no) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1); 
    byte[] buf = new byte[core.sizeX[0] * core.sizeY[0] * bpp];
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
    if (buf.length < core.sizeX[0] * core.sizeY[0] * bpp) {
      throw new FormatException("Buffer too small.");
    }
    in.seek(1024 + extHeaderSize + (no * core.sizeX[0] * core.sizeY[0] * bpp));
    in.read(buf);
    return buf;
  }

  /* @see loci.formats.IFormatReader#openImage(int) */
  public BufferedImage openImage(int no) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1); 
    return ImageTools.makeImage(openBytes(no), core.sizeX[0],
      core.sizeY[0], 1, true, bpp, core.littleEndian[0]);
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  public void initFile(String id) throws FormatException, IOException {
    if (debug) debug("MRCReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    status("Reading header");

    // check endianness

    in.seek(213);
    core.littleEndian[0] = in.read() == 68;

    // read 1024 byte header

    in.seek(0);
    in.order(core.littleEndian[0]);

    core.sizeX[0] = in.readInt();
    core.sizeY[0] = in.readInt();
    core.sizeZ[0] = in.readInt();

    core.sizeC[0] = 1;

    int mode = in.readInt();
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

    int thumbX = in.readInt();
    int thumbY = in.readInt();
    int thumbZ = in.readInt();

    // pixel size = xlen / mx

    int mx = in.readInt();
    int my = in.readInt();
    int mz = in.readInt();

    float xlen = in.readFloat();
    float ylen = in.readFloat();
    float zlen = in.readFloat();

    addMeta("Pixel size (X)", "" + (xlen / mx));
    addMeta("Pixel size (Y)", "" + (ylen / my));
    addMeta("Pixel size (Z)", "" + (zlen / mz));

    float alpha = in.readFloat();
    float beta = in.readFloat();
    float gamma = in.readFloat();

    addMeta("Alpha angle", "" + alpha);
    addMeta("Beta angle", "" + beta);
    addMeta("Gamma angle", "" + gamma);

    in.skipBytes(12);

    // min, max and mean pixel values

    float min = in.readFloat();
    float max = in.readFloat();
    float mean = in.readFloat();

    addMeta("Minimum pixel value", "" + min);
    addMeta("Maximum pixel value", "" + max);
    addMeta("Mean pixel value", "" + mean);

    in.skipBytes(4);

    extHeaderSize = in.readInt();
    int creator = in.readShort();

    in.skipBytes(30);

    int nint = in.readShort();
    int nreal = in.readShort();

    in.skipBytes(28);

    int idtype = in.readShort();
    int lens = in.readShort();
    int nd1 = in.readShort();
    int nd2 = in.readShort();
    int vd1 = in.readShort();
    int vd2 = in.readShort();

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

    float[] angles = new float[6];
    for (int i=0; i<angles.length; i++) {
      angles[i] = in.readFloat();
      addMeta("Angle " + (i+1), "" + angles[i]);
    }

    in.skipBytes(24);

    int nUsefulLabels = in.readInt();
    addMeta("Number of useful labels", "" + nUsefulLabels);

    byte[] b = new byte[80];
    for (int i=0; i<10; i++) {
      in.read(b);
      addMeta("Label " + (i+1), new String(b));
    }

    in.skipBytes(extHeaderSize);

    status("Populating metadata");

    core.sizeT[0] = 1;
    core.currentOrder[0] = "XYZTC";
    core.imageCount[0] = core.sizeZ[0];
    core.rgb[0] = false;
    core.interleaved[0] = true;

    MetadataStore store = getMetadataStore();
    store.setPixels(
      new Integer(core.sizeX[0]),
      new Integer(core.sizeY[0]),
      new Integer(core.sizeZ[0]),
      new Integer(core.sizeC[0]),
      new Integer(core.sizeT[0]),
      new Integer(core.pixelType[0]),
      new Boolean(!core.littleEndian[0]),
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
