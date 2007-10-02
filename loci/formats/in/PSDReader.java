//
// PSDReader.java
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

import java.io.IOException;
import loci.formats.*;
import loci.formats.codec.PackbitsCodec;

/** PSDReader is the file format reader for Photoshop PSD files. */
public class PSDReader extends FormatReader {

  // -- Fields --

  /** Lookup table. */
  private byte[][] lut;

  // -- Constructor --

  /** Constructs a new PSD reader. */
  public PSDReader() { super("Adobe Photoshop", "psd"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return new String(block).startsWith("8BPS");
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() {
    FormatTools.assertId(currentId, true, 1);
    return lut;
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length);

    if (in.getFilePointer() % 2 == 1) in.skipBytes(1);
    in.skipBytes(4);
    while (in.read() != '8');
    in.skipBytes(7);
    int len = in.readInt();
    in.skipBytes(len);

    while (in.readString(4).equals("8BIM")) {
      in.skipBytes(4);
      len = in.readInt();
      in.skipBytes(len);
    }
    in.seek(in.getFilePointer() - 4);

    int plane = core.sizeX[0] * core.sizeY[0] *
      FormatTools.getBytesPerPixel(core.pixelType[0]);
    int[][] lens = new int[core.sizeC[0]][core.sizeY[0]];
    boolean compressed = in.readShort() == 1;

    if (compressed) {
      int pt = 0;
      PackbitsCodec codec = new PackbitsCodec();
      for (int c=0; c<core.sizeC[0]; c++) {
        for (int y=0; y<core.sizeY[0]; y++) {
          lens[c][y] = in.readShort();
        }
      }

      for (int c=0; c<core.sizeC[0]; c++) {
        for (int y=0; y<core.sizeY[0]; y++) {
          byte[] b = new byte[lens[c][y]];
          in.read(b);
          b = codec.decompress(b);
          System.arraycopy(b, 0, buf, pt, b.length);
          pt += b.length;
        }
      }
    }
    else in.read(buf);
    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug ("PSDReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);
    core.littleEndian[0] = false;

    if (!in.readString(4).equals("8BPS")) {
      throw new FormatException("Not a valid Photoshop file.");
    }

    int version = in.readShort();
    addMeta("Version", new Integer(version));

    in.skipBytes(6); // reserved, set to 0
    core.sizeC[0] = in.readShort();
    core.sizeY[0] = in.readInt();
    core.sizeX[0] = in.readInt();

    int bits = in.readShort();
    addMeta("Bits per pixel", new Integer(bits));
    switch (bits) {
      case 16:
        core.pixelType[0] = FormatTools.UINT16;
        break;
      default: core.pixelType[0] = FormatTools.UINT8;
    }

    int colorMode = in.readShort();
    String modeString = null;
    switch (colorMode) {
      case 0:
        modeString = "monochrome";
        break;
      case 1:
        modeString = "gray-scale";
        break;
      case 2:
        modeString = "palette color";
        break;
      case 3:
        modeString = "RGB";
        break;
      case 4:
        modeString = "CMYK";
        break;
      case 6:
        modeString = "Duotone";
        break;
      case 7:
        modeString = "Multichannel color";
        break;
      case 8:
        modeString = "Duotone";
        break;
      case 9:
        modeString = "LAB color";
        break;
    }
    addMeta("Color mode", modeString);

    // read color mode block, if present

    int modeDataLength = in.readInt();
    long fp = in.getFilePointer();
    if (modeDataLength != 0) {
      if (colorMode == 2) {
        lut = new byte[3][256];
        for (int i=0; i<lut.length; i++) {
          in.read(lut[i]);
        }
      }
      in.seek(fp + modeDataLength);
    }

    // read image resources block

    in.skipBytes(4);

    while (in.readString(4).equals("8BIM")) {
      int tag = in.readShort();
      int read = 1;
      while (in.read() != 0) read++;
      if (read % 2 == 1) in.skipBytes(1);

      int size = in.readInt();
      if (size % 2 == 1) size++;
      byte[] data = new byte[size];
      in.read(data);
    }
    in.seek(in.getFilePointer() - 4);

    int blockLen = in.readInt();
    int layerLen = in.readInt();
    int layerCount = in.readShort();
    int[] w = new int[layerCount];
    int[] h = new int[layerCount];
    int[] c = new int[layerCount];
    for (int i=0; i<layerCount; i++) {
      int top = in.readInt();
      int left = in.readInt();
      int bottom = in.readInt();
      int right = in.readInt();
      w[i] = right - left;
      h[i] = bottom - top;
      c[i] = in.readShort();
      in.skipBytes(c[i] * 6);
      in.skipBytes(4 + 4 + 4);
      int len = in.readInt();
      if (len % 2 == 1) len++;
      in.skipBytes(len);
    }

    // skip over pixel data for each layer
    for (int i=0; i<layerCount; i++) {
      int[] lens = new int[h[i]];
      for (int cc=0; cc<c[i]; cc++) {
        boolean compressed = in.readShort() == 1;
        if (!compressed) in.skipBytes(w[i] * h[i]);
        else {
          for (int y=0; y<h[i]; y++) {
            lens[y] = in.readShort();
          }
          for (int y=0; y<h[i]; y++) {
            in.skipBytes(lens[y]);
          }
        }
      }
    }

    core.sizeZ[0] = 1;
    core.sizeT[0] = 1;
    core.rgb[0] = modeString.equals("RGB");
    core.imageCount[0] = core.sizeC[0] / (core.rgb[0] ? 3 : 1);
    core.indexed[0] = modeString.equals("palette color");
    core.falseColor[0] = false;
    core.currentOrder[0] = "XYCZT";
    core.interleaved[0] = false;
    core.littleEndian[0] = true;
    core.metadataComplete[0] = true;

    MetadataStore store = getMetadataStore();
    FormatTools.populatePixels(store, this);
    store.setImage(currentId, null, null, null);
    for (int i=0; i<core.sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null);
    }
  }

}
