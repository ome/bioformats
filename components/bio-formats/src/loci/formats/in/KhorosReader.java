//
// KhorosReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.in;

import java.io.*;
import loci.common.*;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * Reader for Khoros XV files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/KhorosReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/KhorosReader.java">SVN</a></dd></dl>
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

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessStream) */
  public boolean isThisType(RandomAccessStream stream) throws IOException {
    if (!FormatTools.validStream(stream, blockCheckLen, false)) return false;
    return stream.readShort() == 0xab01;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    if (lut == null) return null;
    byte[][] table = new byte[3][lut.length / 3];
    int next = 0;
    for (int i=0; i<table[0].length; i++) {
      for (int j=0; j<table.length; j++) {
        table[j][i] = lut[next++];
      }
    }
    return table;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length, w, h);

    int bytes = FormatTools.getBytesPerPixel(getPixelType());
    int bufSize = getSizeX() * getSizeY() * bytes;
    in.seek(offset + no * bufSize);
    readPlane(in, x, y, w, h, buf);

    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    lut = null;
    offset = 0;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessStream(id);

    in.skipBytes(4);
    in.order(true);
    int dependency = in.readInt();

    addMeta("Comment", in.readString(512));

    in.order(dependency == 4 || dependency == 8);

    core[0].sizeX = in.readInt();
    core[0].sizeY = in.readInt();
    in.skipBytes(28);
    core[0].imageCount = in.readInt();
    if (getImageCount() == 0) core[0].imageCount = 1;
    core[0].sizeC = in.readInt();

    int type = in.readInt();

    switch (type) {
      case 0:
        core[0].pixelType = FormatTools.INT8;
        break;
      case 1:
        core[0].pixelType = FormatTools.UINT8;
        break;
      case 2:
        core[0].pixelType = FormatTools.UINT16;
        break;
      case 4:
        core[0].pixelType = FormatTools.INT32;
        break;
      case 5:
        core[0].pixelType = FormatTools.FLOAT;
        break;
      case 9:
        core[0].pixelType = FormatTools.DOUBLE;
        break;
      default: throw new FormatException("Unsupported pixel type : " + type);
    }

    // read lookup table

    in.skipBytes(12);
    int c = in.readInt();
    if (c > 1) {
      core[0].sizeC = c;
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

    core[0].sizeZ = getImageCount();
    core[0].sizeT = 1;
    core[0].rgb = getSizeC() > 1;
    core[0].interleaved = false;
    core[0].littleEndian = dependency == 4 || dependency == 8;
    core[0].dimensionOrder = "XYCZT";
    core[0].indexed = lut != null;
    core[0].falseColor = false;
    core[0].metadataComplete = true;

    if (isIndexed()) {
      core[0].sizeC = 1;
      core[0].rgb = false;
    }

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    store.setImageName("", 0);
    MetadataTools.setDefaultCreationDate(store, id, 0);
    MetadataTools.populatePixels(store, this);
  }

}
