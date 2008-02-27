//
// OpenlabRawReader.java
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
import java.text.SimpleDateFormat;
import java.util.Date;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * OpenlabRawReader is the file format reader for Openlab RAW files.
 * Specifications available at
 * http://www.improvision.com/support/tech_notes/detail.php?id=344
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/OpenlabRawReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/OpenlabRawReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class OpenlabRawReader extends FormatReader {

  // -- Fields --

  /** Offset to each image's pixel data. */
  protected int[] offsets;

  /** Number of bytes per pixel. */
  private int bytesPerPixel;

  // -- Constructor --

  /** Constructs a new RAW reader. */
  public OpenlabRawReader() {
    super("Openlab RAW", "raw");
    blockCheckLen = 4;
    suffixSufficient = false;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    if (block.length < blockCheckLen) return false;
    new Exception("OpenlabRawReader.isThisType").printStackTrace();//TEMP
    return block[0] == 'O' && block[1] == 'L' &&
      block[2] == 'R' && block[3] == 'W';
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

    in.seek(offsets[no / core.sizeC[0]] + 288);

    int bpp = FormatTools.getBytesPerPixel(core.pixelType[0]);
    in.skipBytes(y * core.sizeX[0] * bpp * core.sizeC[0]);

    int rowLen = w * bpp * core.sizeC[0];
    for (int row=0; row<h; row++) {
      in.skipBytes(x * bpp * core.sizeC[0]);
      in.read(buf, row * rowLen, rowLen);
      in.skipBytes(bpp * core.sizeC[0] * (core.sizeX[0] - w - x));
    }

    if (bytesPerPixel == 1) {
      // need to invert the pixels
      for (int i=0; i<buf.length; i++) {
        buf[i] = (byte) (255 - buf[i]);
      }
    }
    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    offsets = null;
    bytesPerPixel = 0;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("OpenlabRawReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    // read the 12 byte file header

    status("Verifying Openlab RAW format");

    if (!in.readString(4).equals("OLRW")) {
      throw new FormatException("Openlab RAW magic string not found.");
    }

    status("Populating metadata");

    int version = in.readInt();
    addMeta("Version", new Integer(version));

    core.imageCount[0] = in.readInt();
    offsets = new int[core.imageCount[0]];
    offsets[0] = 12;

    in.skipBytes(8);
    core.sizeX[0] = in.readInt();
    core.sizeY[0] = in.readInt();
    in.skipBytes(1);
    core.sizeC[0] = in.read();
    bytesPerPixel = in.read();
    in.skipBytes(1);

    long stampMs = in.readLong();
    Date timestamp = null;
    String stamp = null;
    SimpleDateFormat sdf = null;
    if (stampMs > 0) {
      stampMs /= 1000000;
      stampMs -= (67 * 365.25 * 24 * 60 * 60);

      timestamp = new Date(stampMs);
      sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      stamp = sdf.format(timestamp);
      addMeta("Timestamp", stamp);
    }
    if (stamp == null) {
      stamp = DataTools.convertDate(System.currentTimeMillis(), DataTools.UNIX);
    }

    in.skipBytes(4);
    int len = in.read() & 0xff;
    addMeta("Image name", in.readString(len - 1).trim());

    if (core.sizeC[0] <= 1) core.sizeC[0] = 1;
    else core.sizeC[0] = 3;
    addMeta("Width", new Integer(core.sizeX[0]));
    addMeta("Height", new Integer(core.sizeY[0]));
    addMeta("Bytes per pixel", new Integer(bytesPerPixel));

    int plane = core.sizeX[0] * core.sizeY[0] * bytesPerPixel;
    for (int i=1; i<core.imageCount[0]; i++) {
      offsets[i] = offsets[i - 1] + 288 + plane;
    }

    core.sizeZ[0] = core.imageCount[0];
    core.sizeT[0] = 1;
    core.currentOrder[0] = "XYZTC";
    core.rgb[0] = core.sizeC[0] > 1;
    core.interleaved[0] = false;
    core.littleEndian[0] = false;
    core.metadataComplete[0] = true;
    core.indexed[0] = false;
    core.falseColor[0] = false;

    // The metadata store we're working with.
    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    store.setImageName("", 0);

    switch (bytesPerPixel) {
      case 1:
      case 3:
        core.pixelType[0] = FormatTools.UINT8;
        break;
      case 2:
        core.pixelType[0] = FormatTools.UINT16;
        break;
      default:
        core.pixelType[0] = FormatTools.FLOAT;
    }

    store.setImageCreationDate(stamp, 0);
    MetadataTools.populatePixels(store, this);
  }

}
