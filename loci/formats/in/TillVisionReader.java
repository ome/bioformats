//
// TillVisionReader.java
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
import java.util.StringTokenizer;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * TillVisionReader is the file format reader for TillVision files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/TillVisionReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/TillVisionReader.java">SVN</a></dd></dl>
 */
public class TillVisionReader extends FormatReader {

  // -- Fields --

  private RandomAccessStream pixelsFile;

  // -- Constructor --

  /** Constructs a new TillVision reader. */
  public TillVisionReader() {
    super("TillVision", new String[] {"inf", "pst"});
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return false;
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

    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int plane = getSizeX() * getSizeY() * bpp;
    pixelsFile.seek(plane * no + y * getSizeX() * bpp);

    if (x == 0) {
      pixelsFile.read(buf);
    }
    else {
      for (int row=0; row<h; row++) {
        pixelsFile.skipBytes(x * bpp);
        pixelsFile.read(buf, row * w * bpp, w * bpp);
        pixelsFile.skipBytes((getSizeX() - w - x) * bpp);
      }
    }

    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    if (pixelsFile != null) pixelsFile.close();
    pixelsFile = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("AliconaReader.initFile(" + id + ")");
    super.initFile(id);
    in = new RandomAccessStream(id);

    if (id.toLowerCase().endsWith(".inf")) {
      // find the .pst file
      String base = new Location(id).getAbsolutePath();
      base = base.substring(0, base.lastIndexOf(".") + 1);
      pixelsFile = new RandomAccessStream(base + "pst");
      pixelsFile.order(true);
    }
    else if (id.toLowerCase().endsWith(".pst")) {
      pixelsFile = new RandomAccessStream(id);
      pixelsFile.order(true);

      // find the .inf file
      String base = new Location(id).getAbsolutePath();
      base = base.substring(0, base.lastIndexOf(".") + 1);
      setId(base + "inf");
      return;
    }

    // read key/value pairs from .inf file

    String data = in.readString((int) in.length());
    StringTokenizer lines = new StringTokenizer(data);

    while (lines.hasMoreTokens()) {
      String line = lines.nextToken().trim();
      if (line.startsWith("[") || line.indexOf("=") == -1) {
        continue;
      }

      int equal = line.indexOf("=");
      String key = line.substring(0, equal).trim();
      String value = line.substring(equal + 1).trim();

      addMeta(key, value);

      if (key.equals("Width")) core.sizeX[0] = Integer.parseInt(value);
      else if (key.equals("Height")) core.sizeY[0] = Integer.parseInt(value);
      else if (key.equals("Bands")) core.sizeC[0] = Integer.parseInt(value);
      else if (key.equals("Slices")) core.sizeZ[0] = Integer.parseInt(value);
      else if (key.equals("Frames")) core.sizeT[0] = Integer.parseInt(value);
      else if (key.equals("Datatype")) {
        int type = Integer.parseInt(value);
        switch (type) {
          case 1:
            core.pixelType[0] = FormatTools.INT8;
            break;
          case 2:
            core.pixelType[0] = FormatTools.UINT8;
            break;
          case 3:
            core.pixelType[0] = FormatTools.INT16;
            break;
          case 4:
            core.pixelType[0] = FormatTools.UINT16;
            break;
          default:
            throw new FormatException("Unsupported data type: " + type);
        }
      }
    }

    core.imageCount[0] = core.sizeZ[0] * core.sizeC[0] * core.sizeT[0];
    core.rgb[0] = false;
    core.littleEndian[0] = true;
    core.currentOrder[0] = "XYCZT";

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.setDefaultCreationDate(store, id, 0);
    store.setImageName("", 0);
    MetadataTools.populatePixels(store, this);
  }

}
