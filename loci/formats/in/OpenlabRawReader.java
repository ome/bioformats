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
  public OpenlabRawReader() { super("Openlab RAW", "raw"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return (block[0] == 'O') && (block[1] == 'L') && (block[2] == 'R') &&
      (block[3] == 'W');
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length);

    in.seek(offsets[no / core.sizeC[0]] + 288);
    in.read(buf);

    if (bytesPerPixel == 1) {
      // need to invert the pixels
      for (int i=0; i<buf.length; i++) {
        buf[i] = (byte) (255 - buf[i]);
      }
    }
    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("OpenlabRawReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    // read the 12 byte file header

    status("Verifying Openlab RAW format");

    byte[] header = new byte[4];
    in.read(header);
    String check = new String(header);
    if (!check.equals("OLRW")) {
      throw new FormatException("Openlab RAW magic string not found.");
    }

    status("Populating metadata");

    int version = in.readInt();
    addMeta("Version", new Integer(version));

    core.imageCount[0] = in.readInt();
    offsets = new int[core.imageCount[0]];
    offsets[0] = 12;

    in.readLong();
    core.sizeX[0] = in.readInt();
    core.sizeY[0] = in.readInt();
    in.read();
    core.sizeC[0] = in.read();
    bytesPerPixel = in.read();
    in.read();

    long stamp = in.readLong();
    Date timestamp = null;
    SimpleDateFormat sdf = null;
    if (stamp > 0) {
      stamp /= 1000000;
      stamp -= (67 * 365.25 * 24 * 60 * 60);

      timestamp = new Date(stamp);
      sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      addMeta("Timestamp", sdf.format(timestamp));
    }
    in.skipBytes(4);
    byte[] s = new byte[256];
    in.read(s);
    int len = s[0] > 0 ? s[0] : (s[0] + 256);
    addMeta("Image name", new String(s, 1, len).trim());

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
    MetadataStore store = getMetadataStore();

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

    store.setImage((String) getMeta("Image name"),
      timestamp == null ? null : sdf.format(timestamp), null, null);
    FormatTools.populatePixels(store, this);
    for (int i=0; i<core.sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null);
    }
  }

}
