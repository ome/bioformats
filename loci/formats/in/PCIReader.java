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

  // -- Constants --

  private static final String NO_POI_MSG =
    "Jakarta POI is required to read SimplePCI files. Please " +
    "obtain poi-loci.jar from http://loci.wisc.edu/ome/formats.html";

  // -- Static fields --

  private static boolean noPOI = false;
  private static ReflectedUniverse r = createReflectedUniverse();

  private static ReflectedUniverse createReflectedUniverse() {
    r = null;
    try {
      r = new ReflectedUniverse();
      r.exec("import org.apache.poi.poifs.filesystem.POIFSFileSystem");
      r.exec("import org.apache.poi.poifs.filesystem.DirectoryEntry");
      r.exec("import org.apache.poi.poifs.filesystem.DocumentEntry");
      r.exec("import org.apache.poi.poifs.filesystem.DocumentInputStream");
      r.exec("import org.apache.poi.util.RandomAccessStream");
      r.exec("import java.util.Iterator");
    }
    catch (ReflectException exc) {
      noPOI = true;
      if (debug) LogTools.trace(exc);
    }
    return r;
  }

  // -- Fields --

  private Hashtable imageDirectories;
  private Hashtable imageFiles;
  private String currentParent;

  // -- Constructor --

  /** Constructs a new SimplePCI reader. */
  public PCIReader() { super("Simple-PCI (Compix)", "cxd"); }

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
    if (noPOI) throw new FormatException(NO_POI_MSG);
    FormatTools.checkPlaneNumber(this, no);
    FormatTools.checkBufferSize(this, buf.length, w, h);

    Integer ii = new Integer(no);
    Object directory = imageDirectories.get(ii);
    String name = (String) imageFiles.get(ii);

    try {
      r.setVar("dir", directory);
      r.setVar("entryName", name);
      r.exec("document = dir.getEntry(entryName)");
      r.exec("dis = new DocumentInputStream(document, fis)");
      r.exec("numBytes = dis.available()");
      r.setVar("data", buf);

      int bpp = FormatTools.getBytesPerPixel(core.pixelType[series]);

      int rowLen = w * bpp;
      r.setVar("rowLen", rowLen);

      long skip = y * core.sizeX[series] * bpp;

      long beginSkip = bpp * x;
      long endSkip = bpp * (core.sizeX[0] - w - x);

      r.setVar("skip", skip);
      r.setVar("beginSkip", beginSkip);
      r.setVar("endSkip", endSkip);

      for (int c=0; c<core.sizeC[series]; c++) {
        if (skip > 0) r.exec("dis.skip(skip)");
        for (int row=0; row<h; row++) {
          if (beginSkip > 0) r.exec("dis.skip(beginSkip)");
          r.setVar("offset", c * h * rowLen + row * rowLen);
          r.exec("dis.read(data, offset, rowLen)");
          if (endSkip > 0) r.exec("dis.skip(endSkip)");
        }
        if (h + y < core.sizeY[series]) {
          r.setVar("s",
            (long) (core.sizeY[series] - h - y) * core.sizeX[series] * bpp);
          r.exec("dis.skip(s)");
        }
      }
    }
    catch (ReflectException e) {
      throw new FormatException(NO_POI_MSG, e);
    }

    if (core.pixelType[0] == FormatTools.UINT16) {
      for (int i=0; i<buf.length; i+=2) {
        byte b = buf[i];
        buf[i] = buf[i + 1];
        buf[i + 1] = b;
      }
    }
    else if (core.pixelType[0] == FormatTools.UINT32) {
      for (int i=0; i<buf.length; i+=4) {
        byte b = buf[i];
        buf[i] = buf[i + 3];
        buf[i + 3] = b;
        b = buf[i + 1];
        buf[i + 1] = buf[i + 2];
        buf[i + 2] = b;
      }
    }

    return buf;
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    imageDirectories = imageFiles = null;
    currentParent = null;
    try { r.exec("fis.close()"); }
    catch (ReflectException e) { }
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("PCIReader.initFile(" + id + ")");
    if (noPOI) throw new FormatException(NO_POI_MSG);

    super.initFile(id);

    imageDirectories = new Hashtable();
    imageFiles = new Hashtable();

    try {
      in = new RandomAccessStream(id);
      in.order(true);
      in.seek(30);
      int size = (int) Math.pow(2, in.readShort());
      in.close();

      r.setVar("file", currentId);
      r.setVar("size", size);
      r.exec("fis = new RandomAccessStream(file)");
      r.setVar("littleEndian", true);
      r.exec("fis.order(littleEndian)");
      r.exec("fs = new POIFSFileSystem(fis, size)");
      r.exec("dir = fs.getRoot()");
      parseDir(0, r.getVar("dir"));

      core.sizeZ[0] = core.imageCount[0];
      core.sizeT[0] = 1;
      core.rgb[0] = core.sizeC[0] > 1;
      core.interleaved[0] = false;
      core.currentOrder[0] = "XYCZT";
      core.littleEndian[0] = false;
      core.indexed[0] = false;
      core.falseColor[0] = false;
      core.metadataComplete[0] = true;

      MetadataStore store = getMetadataStore();
      store.setImageName("", 0);
      store.setImageCreationDate(
        DataTools.convertDate(System.currentTimeMillis(), DataTools.UNIX), 0);
      MetadataTools.populatePixels(store, this);
      // CTR CHECK
//      for (int i=0; i<core.sizeC[0]; i++) {
//        store.setLogicalChannel(i, null, null, null, null, null, null, null,
//          null, null, null, null, null, null, null, null, null, null, null,
//          null, null, null, null, null, null);
//      }
    }
    catch (ReflectException exc) {
      noPOI = true;
    }
  }

  // -- Helper methods --

  private void parseDir(int depth, Object dir)
    throws IOException, FormatException, ReflectException
  {
    r.setVar("dir", dir);
    r.exec("dirName = dir.getName()");
    r.setVar("depth", depth);
    r.exec("iter = dir.getEntries()");
    Iterator iter = (Iterator) r.getVar("iter");
    while (iter.hasNext()) {
      r.setVar("entry", iter.next());
      r.exec("isInstance = entry.isDirectoryEntry()");
      r.exec("isDocument = entry.isDocumentEntry()");
      boolean isInstance = ((Boolean) r.getVar("isInstance")).booleanValue();
      boolean isDocument = ((Boolean) r.getVar("isDocument")).booleanValue();
      r.setVar("dir", dir);
      r.exec("dirName = dir.getName()");

      if (isInstance) {
        if (debug) print(depth, (String) r.getVar("dirName"));
        if (((String) r.getVar("dirName")).trim().startsWith("Field")) {
          currentParent = ((String) r.getVar("dirName")).trim();
        }
        parseDir(depth + 1, r.getVar("entry"));
      }
      else if (isDocument) {
        r.exec("entryName = entry.getName()");
        if (debug) print(depth + 1, (String) r.getVar("entryName"));
        r.exec("dis = new DocumentInputStream(entry, fis)");
        r.exec("numBytes = dis.available()");
        int numBytes = ((Integer) r.getVar("numBytes")).intValue();

        String entryName = (String) r.getVar("entryName");
        String dirName = (String) r.getVar("dirName");

        if (entryName.equals("Field Count")) {
          byte[] b = new byte[4];
          r.setVar("data", b);
          r.exec("dis.read(data)");
          core.imageCount[0] = DataTools.bytesToInt(b, 0, true);
        }
        else if (entryName.equals("File Has Image")) {
          byte[] b = new byte[2];
          r.setVar("data", b);
          r.exec("dis.read(data)");
          if (DataTools.bytesToInt(b, 0, 2, true) == 0) {
            throw new FormatException("This file does not contain image data.");
          }
        }
        else if (entryName.equals("Comments")) {
          byte[] b = new byte[numBytes];
          r.setVar("data", b);
          r.exec("dis.read(data)");
          String comments = new String(b).trim();
          StringTokenizer st = new StringTokenizer(comments, "\n");
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
        else if (entryName.startsWith("Bitmap")) {
          int space = currentParent.indexOf(" ") + 1;
          int num = Integer.parseInt(currentParent.substring(space)) - 1;
          Integer ii = new Integer(num);
          imageDirectories.put(ii, r.getVar("dir"));
          imageFiles.put(ii, entryName);

          if (core.sizeX[0] != 0 && core.sizeY[0] != 0) {
            core.sizeC[0] = numBytes / (core.sizeX[0] * core.sizeY[0] *
              FormatTools.getBytesPerPixel(core.pixelType[0]));
          }
        }
        else if (entryName.indexOf("Image_Depth") != -1) {
          byte[] b = new byte[8];
          r.setVar("data", b);
          r.exec("dis.read(data)");
          int bits =
            (int) (DataTools.bytesToLong(b, 0, 8, false) & 0x1f00) >> 8;
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
        else if (entryName.indexOf("Image_Height") != -1) {
          byte[] b = new byte[8];
          r.setVar("data", b);
          r.exec("dis.read(data)");
          core.sizeY[0] = (int)
            ((DataTools.bytesToLong(b, 0, 8, false) & 0x1f00) >> 8) * 64;
        }
        else if (entryName.indexOf("Image_Width") != -1) {
          byte[] b = new byte[8];
          r.setVar("data", b);
          r.exec("dis.read(data)");
          core.sizeX[0] = (int)
            ((DataTools.bytesToLong(b, 0, 8, false) & 0x1f00) >> 8) * 64;
        }
        else if (entryName.indexOf("Time_From_Start") != -1) {
        }
        else if (entryName.indexOf("Event_Marker") != -1) {

        }
        else if (entryName.indexOf("Time_From_Last") != -1) {

        }
        else {

        }

        r.exec("dis.close()");
      }
    }
  }

  private void print(int depth, String s) {
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<depth; i++) sb.append("  ");
    sb.append(s);
    debug(sb.toString());
  }

}
