//
// LegacyPictReader.java
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
 * LegacyPictReader is the old file format reader for Apple PICT files.
 * To use it, QuickTime for Java must be installed.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/LegacyPictReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/LegacyPictReader.java">SVN</a></dd></dl>
 */
public class LegacyPictReader extends FormatReader {

  // -- Static fields --

  /** Helper for reading PICT data with QTJava library. */
  private static LegacyQTTools qtTools = new LegacyQTTools();

  // -- Constructor --

  /** Constructs a new PICT reader. */
  public LegacyPictReader() { super("PICT", "pict"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return false;
  }

  /* @see loci.formats.IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    buf = ImageTools.getBytes(openImage(no), false, 3);
    return buf;
  }

  /* @see loci.formats.IFormatReader#openImage(int) */
  public BufferedImage openImage(int no) throws FormatException, IOException {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);

    // read in PICT data
    RandomAccessStream fin = new RandomAccessStream(currentId);
    int len = (int) (fin.length() - 512);
    byte[] bytes = new byte[len];
    fin.skip(512);  // skip 512 byte PICT header
    int read = 0;
    int left = len;
    while (left > 0) {
      int r = fin.read(bytes, read, left);
      read += r;
      left -= r;
    }
    fin.close();
    return ImageTools.makeBuffered(qtTools.pictToImage(bytes));
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException { }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("LegacyPictReader.initFile(" + id + ")");
    super.initFile(id);
    status("Populating metadata");
    BufferedImage img = openImage(0);
    core.sizeX[0] = img.getWidth();
    core.sizeY[0] = img.getHeight();
    core.sizeZ[0] = 1;
    core.sizeC[0] = img.getRaster().getNumBands();
    core.sizeT[0] = 1;
    core.pixelType[0] = FormatTools.INT8;
    core.currentOrder[0] = "XYCZT";
    core.rgb[0] = core.sizeC[0] > 1;
    core.interleaved[0] = false;
    core.imageCount[0] = 1;
    core.littleEndian[0] = false;
    core.indexed[0] = false;
    core.falseColor[0] = false;

    MetadataStore store = getMetadataStore();
    store.setImage(currentId, null, null, null);
    FormatTools.populatePixels(store, this);

    for (int i=0; i<core.sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null, null,
        null, null, null, null, null, null, null, null, null, null, null, null,
        null, null, null, null);
    }
  }

}
