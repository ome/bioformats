//
// KhorosReader.java
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
 * Reader for Khoros XV files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/KhorosReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/KhorosReader.java">SVN</a></dd></dl>
 */
public class KhorosReader extends FormatReader {

  // -- Fields --

  /** Global lookup table. */
  private byte[] lut;

  /** Image offset. */
  private long offset;

  // -- Constructor --

  /** Constructs a new Khoros reader. */
  public KhorosReader() { super("Khoros XV", "xv"); }

  // -- FormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return block[0] == (byte) 0xab && block[1] == 1;
  }

  /* @see loci.formats.IFormatReader#openBytes(int) */
  public byte[] openBytes(int no) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    byte[] buf = new byte[core.sizeX[0] * core.sizeY[0] * core.sizeC[0] *
      FormatTools.getBytesPerPixel(core.pixelType[0])];
    return openBytes(no, buf);
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    if (no < 0 || no >= core.imageCount[0]) {
      throw new FormatException("Invalid image number: " + no);
    }

    in.seek(offset +
      no * (core.sizeX[0] * core.sizeY[0] * getRGBChannelCount()));
    if (lut == null) in.read(buf);
    else {
      int plane = core.sizeX[0] * core.sizeY[0] *
        FormatTools.getBytesPerPixel(core.pixelType[0]);

      for (int i=0; i<plane; i++) {
        int ndx = in.read();
        if (ndx < 0) ndx += 256;
        buf[i] = lut[ndx*3];
        buf[i + plane] = lut[ndx*3 + 1];
        buf[i + 2*plane] = lut[ndx*3 + 2];
      }
    }

    return buf;
  }

  /* @see loci.formats.IFormatReader#openImage(int) */
  public BufferedImage openImage(int no) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);

    if (core.pixelType[0] == FormatTools.FLOAT) {
      byte[] b = openBytes(no);
      float[] f = new float[core.sizeX[0] * core.sizeY[0] * core.sizeC[0]];
      for (int i=0; i<f.length; i++) {
        f[i] = Float.intBitsToFloat(DataTools.bytesToInt(b,
          i*4, !core.littleEndian[0]));
      }

      return ImageTools.makeImage(f, core.sizeX[0], core.sizeY[0],
        core.sizeC[0], core.interleaved[0]);
    }

    return ImageTools.makeImage(openBytes(no), core.sizeX[0], core.sizeY[0],
      core.sizeC[0], core.interleaved[0],
      FormatTools.getBytesPerPixel(core.pixelType[0]), core.littleEndian[0]);
  }

  /* @see loci.formats.IFormatReader#close() */
  public void close() throws IOException {
    super.close();
    lut = null;
    offset = 0;
  }

  /** Initialize the given file. */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessStream(id);

    in.skipBytes(4);
    in.order(true);
    int dependency = in.readInt();

    addMeta("Comment", in.readString(512));

    in.order(dependency == 4 || dependency == 8);

    core.sizeX[0] = in.readInt();
    core.sizeY[0] = in.readInt();
    in.skipBytes(28);
    core.imageCount[0] = in.readInt();
    if (core.imageCount[0] == 0) core.imageCount[0] = 1;
    core.sizeC[0] = in.readInt();

    int type = in.readInt();

    switch (type) {
      case 0:
      case 1:
        core.pixelType[0] = FormatTools.UINT8;
        break;
      case 2:
        core.pixelType[0] = FormatTools.UINT16;
        break;
      case 4:
        core.pixelType[0] = FormatTools.UINT32;
        break;
      case 5:
        core.pixelType[0] = FormatTools.FLOAT;
        break;
      case 9:
        core.pixelType[0] = FormatTools.DOUBLE;
        break;
      default: throw new FormatException("Unsupported pixel type : " + type);
    }

    in.skipBytes(12);
    int c = in.readInt();
    if (c > 1) {
      core.sizeC[0] = c;
      int n = in.readInt();
      lut = new byte[n * c];
      in.skipBytes(436);

      for (int i=0; i<lut.length; i++) {
        int value = in.read();
        if (i < n) {
          lut[i*3] = (byte) value;
          lut[i*3 + 1] = (byte) value;
          lut[i*3 + 2] = (byte) value;
        }
        else if (i < n*2) {
          lut[(i % n)*3 + 1] = (byte) value;
        }
        else if (i < n*3) {
          lut[(i % n)*3 + 2] = (byte) value;
        }
      }
    }
    else in.skipBytes(440);
    offset = in.getFilePointer();

    core.sizeZ[0] = core.imageCount[0];
    core.sizeT[0] = 1;
    core.rgb[0] = core.sizeC[0] > 1;
    core.interleaved[0] = false;
    core.littleEndian[0] = dependency == 4 || dependency == 8;
    core.currentOrder[0] = "XYCZT";

    MetadataStore store = getMetadataStore();
    store.setImage(currentId, null, null, null);
    store.setPixels(new Integer(core.sizeX[0]), new Integer(core.sizeY[0]),
      new Integer(core.sizeZ[0]), new Integer(core.sizeC[0]),
      new Integer(core.sizeT[0]), new Integer(core.pixelType[0]),
      new Boolean(core.littleEndian[0]), core.currentOrder[0], null, null);

    for (int i=0; i<core.sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null, null,
        null, null, null, null, core.sizeC[0] == 1 ? "monochrome" : "RGB", null,
        null, null, null, null, null, null, null, null, null, null);
    }
  }

}
