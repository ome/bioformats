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

import java.io.*;
import loci.formats.*;

/**
 * MRCReader is the file format reader for MRC files.
 * Specifications available at
 * http://bio3d.colorado.edu/imod/doc/mrc_format.txt
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/MRCReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/MRCReader.java">SVN</a></dd></dl>
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

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length);
    in.seek(1024 + (no * core.sizeX[0] * core.sizeY[0] * bpp));
    in.read(buf);
    return buf;
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
      case 6:
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
      case 16:
        bpp = 2;
        core.sizeC[0] = 3;
        core.pixelType[0] = FormatTools.UINT16;
        break;
    }

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

    addMeta("Alpha angle", "" + in.readFloat());
    addMeta("Beta angle", "" + in.readFloat());
    addMeta("Gamma angle", "" + in.readFloat());

    in.skipBytes(12);

    // min, max and mean pixel values

    addMeta("Minimum pixel value", "" + in.readFloat());
    addMeta("Maximum pixel value", "" + in.readFloat());
    addMeta("Mean pixel value", "" + in.readFloat());

    in.skipBytes(4);
    extHeaderSize = in.readInt();

    in.skipBytes(64);

    int idtype = in.readShort();

    String[] types = new String[] {"mono", "tilt", "tilts", "lina", "lins"};
    String type = (idtype >= 0 && idtype < types.length) ? types[idtype] :
      "unknown";

    addMeta("Series type", type);
    addMeta("Lens", "" + in.readShort());
    addMeta("ND1", "" + in.readShort());
    addMeta("ND2", "" + in.readShort());
    addMeta("VD1", "" + in.readShort());
    addMeta("VD2", "" + in.readShort());

    float[] angles = new float[6];
    for (int i=0; i<angles.length; i++) {
      angles[i] = in.readFloat();
      addMeta("Angle " + (i+1), "" + angles[i]);
    }

    in.skipBytes(24);

    int nUsefulLabels = in.readInt();
    addMeta("Number of useful labels", "" + nUsefulLabels);

    for (int i=0; i<10; i++) {
      addMeta("Label " + (i+1), in.readString(80));
    }

    in.skipBytes(extHeaderSize);

    status("Populating metadata");

    core.sizeT[0] = 1;
    core.currentOrder[0] = "XYZTC";
    core.imageCount[0] = core.sizeZ[0];
    core.rgb[0] = false;
    core.interleaved[0] = true;
    core.indexed[0] = false;
    core.falseColor[0] = false;
    core.metadataComplete[0] = true;

    MetadataStore store = getMetadataStore();
    store.setImage(currentId, null, null, null);
    FormatTools.populatePixels(store, this);

    Float x = new Float(xlen / mx);
    Float y = new Float(ylen / my);
    Float z = new Float(zlen / mz);
    if (x.floatValue() == Float.POSITIVE_INFINITY) x = new Float(1.0);
    if (y.floatValue() == Float.POSITIVE_INFINITY) y = new Float(1.0);
    if (z.floatValue() == Float.POSITIVE_INFINITY) z = new Float(1.0);

    store.setDimensions(x, y, z, null, null, null);
    for (int i=0; i<core.sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null);
      // TODO : get channel min/max from metadata
      //store.setChannelGlobalMinMax(i, getChannelGlobalMinimum(id, i),
      //  getChannelGlobalMaximum(id, i), null);
    }
  }

}
