//
// PGMReader.java
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
import java.util.StringTokenizer;
import loci.formats.*;

/**
 * PGMReader is the file format reader for Portable Gray Map (PGM) images.
 *
 * Much of this code was adapted from ImageJ (http://rsb.info.nih.gov/ij).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/PGMReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/PGMReader.java">SVN</a></dd></dl>
 */
public class PGMReader extends FormatReader {

  // -- Fields --

  private boolean rawBits;

  /** Offset to pixel data. */
  private long offset;

  // -- Constructor --

  /** Constructs a new PGMReader. */
  public PGMReader() { super("Portable Gray Map", "pgm"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return block[0] == 'P';
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length);

    in.seek(offset);
    if (rawBits) in.read(buf);
    else {
      int pt = 0;
      while (pt < buf.length) {
        String line = in.readLine().trim();
        line = line.replaceAll("[^0-9]", " ");
        StringTokenizer t = new StringTokenizer(line, " ");
        while (t.hasMoreTokens()) {
          int q = Integer.parseInt(t.nextToken().trim());
          if (core.pixelType[0] == FormatTools.UINT16) {
            short s = (short) q;
            buf[pt] = (byte) ((s & 0xff00) >> 8);
            buf[pt + 1] = (byte) (s & 0xff);
            pt += 2;
          }
          else {
            buf[pt] = (byte) q;
            pt++;
          }
        }
      }
    }

    return buf;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);
    in = new RandomAccessStream(id);

    String magic = in.readLine().trim();

    boolean isBlackAndWhite = false;

    rawBits = magic.equals("P4") || magic.equals("P5") || magic.equals("P6");
    core.sizeC[0] = (magic.equals("P3") || magic.equals("P6")) ? 3 : 1;
    isBlackAndWhite = magic.equals("P1") || magic.equals("P4");

    String line = in.readLine().trim();
    while (line.startsWith("#") || line.length() == 0) line = in.readLine();

    line = line.replaceAll("[^0-9]", " ");
    core.sizeX[0] =
      Integer.parseInt(line.substring(0, line.indexOf(" ")).trim());
    core.sizeY[0] =
      Integer.parseInt(line.substring(line.indexOf(" ") + 1).trim());

    if (!isBlackAndWhite) {
      int max = Integer.parseInt(in.readLine().trim());
      if (max > 255) core.pixelType[0] = FormatTools.UINT16;
      else core.pixelType[0] = FormatTools.UINT8;
    }

    offset = in.getFilePointer();

    core.rgb[0] = core.sizeC[0] == 3;
    core.currentOrder[0] = "XYCZT";
    core.littleEndian[0] = true;
    core.interleaved[0] = false;
    core.sizeZ[0] = 1;
    core.sizeT[0] = 1;
    core.imageCount[0] = 1;
    core.indexed[0] = false;
    core.falseColor[0] = false;
    core.metadataComplete[0] = true;

    MetadataStore store = getMetadataStore();
    store.setImage(currentId, null, null, null);
    FormatTools.populatePixels(store, this);

    for (int i=0; i<core.sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null);
    }
  }

}
