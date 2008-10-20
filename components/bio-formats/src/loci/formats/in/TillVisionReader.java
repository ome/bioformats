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
import java.util.*;
import loci.common.*;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * TillVisionReader is the file format reader for TillVision files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/TillVisionReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/TillVisionReader.java">SVN</a></dd></dl>
 */
public class TillVisionReader extends FormatReader {

  // -- Fields --

  private RandomAccessStream pixelsFile;

  // -- Constructor --

  /** Constructs a new TillVision reader. */
  public TillVisionReader() {
    super("TillVision", "vws");
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessStream) */
  public boolean isThisType(RandomAccessStream stream) throws IOException {
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

    POITools poi = new POITools(id);
    Vector documents = poi.getDocumentList();

    for (int i=0; i<documents.size(); i++) {
      String name = (String) documents.get(i);
      System.out.println(name + " (" + poi.getFileSize(name) + ")");
      byte[] data = poi.getDocumentBytes(name);
      name = name.replaceAll("/", "-");
      RandomAccessFile out = new RandomAccessFile(name, "rw");
      out.write(data);
      out.close();
    }

    if (true) return;

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

      if (key.equals("Width")) core[0].sizeX = Integer.parseInt(value);
      else if (key.equals("Height")) core[0].sizeY = Integer.parseInt(value);
      else if (key.equals("Bands")) core[0].sizeC = Integer.parseInt(value);
      else if (key.equals("Slices")) core[0].sizeZ = Integer.parseInt(value);
      else if (key.equals("Frames")) core[0].sizeT = Integer.parseInt(value);
      else if (key.equals("Datatype")) {
        int type = Integer.parseInt(value);
        switch (type) {
          case 1:
            core[0].pixelType = FormatTools.INT8;
            break;
          case 2:
            core[0].pixelType = FormatTools.UINT8;
            break;
          case 3:
            core[0].pixelType = FormatTools.INT16;
            break;
          case 4:
            core[0].pixelType = FormatTools.UINT16;
            break;
          default:
            throw new FormatException("Unsupported data type: " + type);
        }
      }
    }

    core[0].imageCount = core[0].sizeZ * core[0].sizeC * core[0].sizeT;
    core[0].rgb = false;
    core[0].littleEndian = true;
    core[0].dimensionOrder = "XYCZT";

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.setDefaultCreationDate(store, id, 0);
    store.setImageName("", 0);
    MetadataTools.populatePixels(store, this);
  }

}
