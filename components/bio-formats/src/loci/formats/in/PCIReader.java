//
// PCIReader.java
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
 * PCIReader is the file format reader for SimplePCI (Compix) .cxd files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/PCIReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/PCIReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class PCIReader extends FormatReader {

  // -- Fields --

  private Vector imageFiles;
  private POITools poi;
  private Vector timestamps;

  // -- Constructor --

  /** Constructs a new SimplePCI reader. */
  public PCIReader() {
    super("Compix Simple-PCI", "cxd");
    blockCheckLen = 4;
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessStream) */
  public boolean isThisType(RandomAccessStream stream) throws IOException {
    if (!FormatTools.validStream(stream, blockCheckLen, false)) return false;
    return stream.readInt() == 0xd0cf11e0;
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
    int planeSize = getSizeX() * getSizeY() * getRGBChannelCount() *
      FormatTools.getBytesPerPixel(getPixelType());
    s.skipBytes((int) (s.length() - planeSize));
    readPlane(s, x, y, w, h, buf);
    s.close();

    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    imageFiles = null;
    timestamps = null;
    if (poi != null) poi.close();
    poi = null;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("PCIReader.initFile(" + id + ")");

    super.initFile(id);

    imageFiles = new Vector();
    timestamps = new Vector();

    poi = new POITools(Location.getMappedId(currentId));

    float scaleFactor = 1f;

    Vector allFiles = poi.getDocumentList();

    for (int i=0; i<allFiles.size(); i++) {
      String name = (String) allFiles.get(i);
      String relativePath =
        name.substring(name.lastIndexOf(File.separator) + 1);

      if (relativePath.equals("Field Count")) {
        byte[] b = poi.getDocumentBytes(name, 4);
        core[0].imageCount = DataTools.bytesToInt(b, 0, true);
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

            if (key.equals("factor")) {
              if (value.indexOf(";") != -1) {
                value = value.substring(0, value.indexOf(";"));
              }
              scaleFactor = Float.parseFloat(value.trim());
            }
          }
        }
      }
      else if (relativePath.startsWith("Bitmap") || relativePath.equals("Data"))
      {
        String parent = name.substring(0, name.lastIndexOf(File.separator));
        int space = parent.lastIndexOf(" ") + 1;
        if (space >= parent.length()) continue;
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

        if (getSizeX() != 0 && getSizeY() != 0) {
          int bpp = FormatTools.getBytesPerPixel(getPixelType());
          int plane = getSizeX() * getSizeY() * bpp;
          core[0].sizeC = poi.getFileSize(name) / plane;
          if (getSizeC() == 0) {
            core[0].sizeX /= 16;
            core[0].sizeY /= 16;
            core[0].sizeC = poi.getFileSize(name) / plane;
          }
        }
      }
      else if (relativePath.indexOf("Image_Depth") != -1) {
        byte[] b = poi.getDocumentBytes(name, 8);
        int bits =
          (int) Double.longBitsToDouble(DataTools.bytesToLong(b, 0, true));
        while (bits % 8 != 0 || bits == 0) bits++;
        switch (bits) {
          case 8:
            core[0].pixelType = FormatTools.UINT8;
            break;
          case 16:
            core[0].pixelType = FormatTools.UINT16;
            break;
          case 32:
            core[0].pixelType = FormatTools.UINT32;
            break;
          case 48:
            core[0].pixelType = FormatTools.UINT16;
            break;
          default:
            throw new FormatException("Unsupported bits per pixel : " + bits);
        }
      }
      else if (relativePath.indexOf("Image_Height") != -1 && getSizeY() == 0) {
        byte[] b = poi.getDocumentBytes(name, 8);
        core[0].sizeY =
          (int) Double.longBitsToDouble(DataTools.bytesToLong(b, 0, true));
      }
      else if (relativePath.indexOf("Image_Width") != -1 && getSizeX() == 0) {
        byte[] b = poi.getDocumentBytes(name, 8);
        core[0].sizeX =
          (int) Double.longBitsToDouble(DataTools.bytesToLong(b, 0, true));
      }
      else if (relativePath.indexOf("Time_From_Start") != -1) {
        byte[] b = poi.getDocumentBytes(name, 8);
        double v = Double.longBitsToDouble(DataTools.bytesToLong(b, 0, true));
        if (!timestamps.contains(new Double(v))) timestamps.add(new Double(v));
      }
    }

    if (getSizeC() == 0) core[0].sizeC = 1;

    if (timestamps.size() > 0) {
      core[0].sizeZ = getImageCount() / timestamps.size();
      core[0].sizeT = timestamps.size();
    }
    if (timestamps.size() == 0 || getSizeZ() * getSizeT() != getImageCount()) {
      core[0].sizeZ = getImageCount();
      core[0].sizeT = 1;
    }
    core[0].rgb = getSizeC() > 1;
    core[0].interleaved = false;
    core[0].dimensionOrder = "XYCTZ";
    core[0].littleEndian = true;
    core[0].indexed = false;
    core[0].falseColor = false;
    core[0].metadataComplete = true;

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this);
    store.setImageName("", 0);
    MetadataTools.setDefaultCreationDate(store, id, 0);
    store.setDimensionsPhysicalSizeX(new Float(scaleFactor), 0, 0);
    store.setDimensionsPhysicalSizeY(new Float(scaleFactor), 0, 0);
  }

}
