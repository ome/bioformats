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

import java.io.*;
import loci.formats.*;

/**
 * AliconaReader is the file format reader for Alicona AL3D files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/AliconaReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/AliconaReader.java">SVN</a></dd></dl>
 */
public class AliconaReader extends FormatReader {

  // -- Fields --

  /** Image offset. */
  private int textureOffset;

  /** Number of bytes per pixel (either 1 or 2). */
  private int numBytes;

  // -- Constructor --

  /** Constructs a new Alicona reader. */
  public AliconaReader() { super("Alicona AL3D", "al3d"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return (new String(block)).indexOf("Alicona") != -1;
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length);

    int pad = (8 - (core.sizeX[0] % 8)) % 8;

    for (int i=0; i<numBytes; i++) {
      in.seek(textureOffset + (no * (core.sizeX[0] + pad)*core.sizeY[0]*(i+1)));
      for (int j=0; j<core.sizeX[0] * core.sizeY[0]; j++) {
        buf[j*numBytes + i] = in.readByte();
        if (j % core.sizeX[0] == core.sizeX[0] - 1) in.skipBytes(pad);
      }
    }

    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("AliconaReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    // check that this is a valid AL3D file
    status("Verifying Alicona format");
    String magicString = in.readString(17);
    if (!magicString.trim().equals("AliconaImaging")) {
      throw new FormatException("Invalid magic string : " +
        "expected 'AliconaImaging', got " + magicString);
    }

    // now we read a series of tags
    // each one is 52 bytes - 20 byte key + 30 byte value + 2 byte CRLF

    status("Reading tags");

    int count = 2;

    boolean hasC = false;
    String voltage = null, magnification = null;
    String pntX = null, pntY = null, pntZ = null;

    for (int i=0; i<count; i++) {
      String key = in.readString(20).trim();
      String value = in.readString(30).trim();

      addMeta(key, value);
      in.skipBytes(2);

      if (key.equals("TagCount")) count += Integer.parseInt(value);
      else if (key.equals("Rows")) core.sizeY[0] = Integer.parseInt(value);
      else if (key.equals("Cols")) core.sizeX[0] = Integer.parseInt(value);
      else if (key.equals("NumberOfPlanes")) {
        core.imageCount[0] = Integer.parseInt(value);
      }
      else if (key.equals("TextureImageOffset")) {
        textureOffset = Integer.parseInt(value);
      }
      else if (key.equals("TexturePtr") && !value.equals("7")) hasC = true;
      else if (key.equals("Voltage")) voltage = value;
      else if (key.equals("Magnification")) magnification = value;
      else if (key.equals("PlanePntX")) pntX = value;
      else if (key.equals("PlanePntY")) pntY = value;
      else if (key.equals("PlanePntZ")) pntZ = value;
    }

    status("Populating metadata");

    numBytes = (int) (in.length() - textureOffset) /
      (core.sizeX[0] * core.sizeY[0] * core.imageCount[0]);

    core.sizeC[0] = hasC ? 3 : 1;
    core.sizeZ[0] = 1;
    core.sizeT[0] = core.imageCount[0] / core.sizeC[0];
    core.rgb[0] = false;
    core.interleaved[0] = false;
    core.littleEndian[0] = true;

    core.pixelType[0] = numBytes == 2 ? FormatTools.UINT16 : FormatTools.UINT8;
    core.currentOrder[0] = "XYCTZ";
    core.metadataComplete[0] = true;
    core.indexed[0] = false;
    core.falseColor[0] = false;

    MetadataStore store = getMetadataStore();

    store.setImage(currentId, null, null, null);

    FormatTools.populatePixels(store, this);

    if (voltage != null) {
      store.setDetector(null, null, null, null, null, new Float(voltage),
        null, null, null);
    }
    if (magnification != null) {
      store.setObjective(null, null, null, null, new Float(magnification),
        null, null);
    }

    if (pntX != null && pntY != null && pntZ != null) {
      store.setDimensions(new Float(pntX.trim()), new Float(pntY.trim()),
        new Float(pntZ.trim()), null, null, null);
    }

    for (int i=0; i<core.sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null, null,
       null, null, null, null, null, null, null, null, null, null, null, null,
       null, null, null, null);
    }
  }

}
