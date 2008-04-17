//
// PCIReader.java
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
import java.util.*;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * PCIReader is the file format reader for SimplePCI (Compix) .cxd files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/PCIReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/PCIReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class PCIReader extends FormatReader {

  // -- Fields --

  private Vector imageFiles;
  private POITools poi;

  // -- Constructor --

  /** Constructs a new SimplePCI reader. */
  public PCIReader() { super("Compix Simple-PCI", "cxd"); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return (block[0] == 0xd0 && block[1] == 0xcf && block[2] == 0x11 &&
      block[3] == 0xe0);
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

    RandomAccessStream s = poi.getDocumentStream((String) imageFiles.get(no));

    int bpp = FormatTools.getBytesPerPixel(core.pixelType[series]);

    int rowLen = w * bpp;
    int skip = y * core.sizeX[series] * bpp;
    int beginSkip = bpp * x;
    int endSkip = bpp * (core.sizeX[0] - w - x);

    for (int c=0; c<core.sizeC[series]; c++) {
      s.skipBytes(skip);
      for (int row=0; row<h; row++) {
        s.skipBytes(beginSkip);
        s.read(buf, c * h * rowLen + row * rowLen, rowLen);
        s.skipBytes(endSkip);
      }
      s.skipBytes((core.sizeY[series] - h - y) * core.sizeX[series] * bpp);
    }

    s.close();

    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    imageFiles = null;
    if (poi != null) poi.close();
    poi = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("PCIReader.initFile(" + id + ")");

    super.initFile(id);

    imageFiles = new Vector();

    poi = new POITools(Location.getMappedId(currentId));

    Vector allFiles = poi.getDocumentList();

    for (int i=0; i<allFiles.size(); i++) {
      String name = (String) allFiles.get(i);
      String relativePath =
        name.substring(name.lastIndexOf(File.separator) + 1);

      if (relativePath.equals("Field Count")) {
        byte[] b = poi.getDocumentBytes(name, 4);
        core.imageCount[0] = DataTools.bytesToInt(b, 0, true);
      }
      else if (relativePath.equals("File Has Image")) {
        byte[] b = poi.getDocumentBytes(name, 2);
        if (DataTools.bytesToInt(b, 0, true) == 0) {
          throw new FormatException("This file does not contain image data.");
        }
      }
      else if (relativePath.equals("Comments")) {
        String comments = new String(poi.getDocumentBytes(name));
        StringTokenizer st = new StringTokenizer(comments.trim(), "\n");
        while (st.hasMoreTokens()) {
          String token = st.nextToken().trim();
          if (token.indexOf("=") != -1) {
            int idx = token.indexOf("=");
            String key = token.substring(0, idx).trim();
            String value = token.substring(idx + 1).trim();
            addMeta(key, value);
          }
        }
      }
      else if (relativePath.startsWith("Bitmap")) {
        String parent = name.substring(0, name.lastIndexOf(File.separator));
        int space = parent.lastIndexOf(" ") + 1;
        int num = Integer.parseInt(parent.substring(space,
          parent.indexOf(File.separator, space))) - 1;
        if (num < imageFiles.size()) imageFiles.setElementAt(name, num);
        else {
          int diff = num - imageFiles.size();
          for (int q=0; q<diff; q++) {
            imageFiles.add("");
          }
          imageFiles.add(name);
        }

        if (core.sizeX[0] != 0 && core.sizeY[0] != 0) {
          core.sizeC[0] = poi.getFileSize(name) /
            (core.sizeX[0] * core.sizeY[0] *
            FormatTools.getBytesPerPixel(core.pixelType[0]));
        }
      }
      else if (relativePath.indexOf("Image_Depth") != -1) {
        byte[] b = poi.getDocumentBytes(name, 8);
        int bits = (int) (DataTools.bytesToLong(b, 0, false) & 0xff) >> 2;
        while (bits % 8 != 0) bits++;
        switch (bits) {
          case 8:
            core.pixelType[0] = FormatTools.UINT8;
            break;
          case 16:
            core.pixelType[0] = FormatTools.UINT16;
            break;
          case 32:
            core.pixelType[0] = FormatTools.UINT32;
            break;
          default:
            throw new FormatException("Unsupported bits per pixel : " + bits);
        }
      }
      else if (relativePath.indexOf("Image_Height") != -1) {
        byte[] b = poi.getDocumentBytes(name, 8);
        core.sizeY[0] =
          (int) ((DataTools.bytesToLong(b, 0, false) & 0x1f00) >> 8) * 64;
      }
      else if (relativePath.indexOf("Image_Width") != -1) {
        byte[] b = poi.getDocumentBytes(name, 8);
        core.sizeX[0] =
          (int) ((DataTools.bytesToLong(b, 0, false) & 0x1f00) >> 8) * 64;
      }
    }

    core.sizeZ[0] = core.imageCount[0];
    core.sizeT[0] = 1;
    core.rgb[0] = core.sizeC[0] > 1;
    core.interleaved[0] = false;
    core.currentOrder[0] = "XYCZT";
    core.littleEndian[0] = true;
    core.indexed[0] = false;
    core.falseColor[0] = false;
    core.metadataComplete[0] = true;

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    store.setImageName("", 0);
    store.setImageCreationDate(
      DataTools.convertDate(System.currentTimeMillis(), DataTools.UNIX), 0);
    MetadataTools.populatePixels(store, this);
  }

}
