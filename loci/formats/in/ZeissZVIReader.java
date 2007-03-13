//
// ZeissZVIReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden, Chris Allan,
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
import java.util.*;
import loci.formats.*;

/**
 * ZeissZVIReader is the file format reader for Zeiss ZVI files.
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class ZeissZVIReader extends FormatReader {

  // -- Constants --

  private static final String NO_POI_MSG =
    "Jakarta POI is required to read ZVI files. Please " +
    "obtain poi-loci.jar from http://loci.wisc.edu/ome/formats.html";

  // -- Static fields --

  private LegacyZVIReader legacy = new LegacyZVIReader();
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
      r.exec("import java.util.Iterator");
    }
    catch (Throwable t) {
      noPOI = true;
    }
    return r;
  }

  // -- Fields --

  /** Current file. */
  private RandomAccessStream ras;

  /** Flag set to true if we need to use the legacy reader. */
  private boolean needLegacy = false;

  /** Number of images. */
  private int nImages;

  /** Image width. */
  private int width;

  /** Image height. */
  private int height;

  /** Number of channels. */
  private int nChannels = 1;

  /** Number of timepoints. */
  private int tSize;

  /** Number of Z slices. */
  private int zSize;

  /** Number of bytes per pixel. */
  private int bpp;

  /** Thumbnail width. */
  private int thumbWidth;

  /** Thumbnail height. */
  private int thumbHeight;

  /** Hashtable containing the directory entry for each plane. */
  private Hashtable pixels;

  /**
   * Hashtable containing the document name for each plane,
   * indexed by the plane number.
   */
  private Hashtable names;

  /** Vector containing Z indices. */
  private Vector zIndices;

  /** Vector containing C indices. */
  private Vector cIndices;

  /** Vector containing T indices. */
  private Vector tIndices;

  /** Valid bits per pixel */
  private int[] validBits;

  private Hashtable offsets;

  // -- Constructor --

  /** Constructs a new ZeissZVI reader. */
  public ZeissZVIReader() { super("Zeiss Vision Image (ZVI)", "zvi"); }

  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for an Zeiss ZVI file. */
  public boolean isThisType(byte[] block) {
    // all of our samples begin with 0xd0cf11e0
    return (block[0] == 0xd0 && block[1] == 0xcf &&
      block[2] == 0x11 && block[3] == 0xe0);
  }

  /** Determines the number of images in the given Zeiss ZVI file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    if (noPOI || needLegacy) return legacy.getImageCount(id);
    return nImages;
  }

  /** Get the size of the X dimension. */
  public int getSizeX(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    if (noPOI || needLegacy) return legacy.getSizeX(id);
    return super.getSizeX(id);
  }

  /** Get the size of the Y dimension. */
  public int getSizeY(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    if (noPOI || needLegacy) return legacy.getSizeY(id);
    return super.getSizeY(id);
  }

  /** Get the size of the Z dimension. */
  public int getSizeZ(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    if (noPOI || needLegacy) return legacy.getSizeZ(id);
    return super.getSizeZ(id);
  }

  /** Get the size of the C dimension. */
  public int getSizeC(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    if (noPOI || needLegacy) return legacy.getSizeC(id);
    return super.getSizeC(id);
  }

  /** Get the size of the T dimension. */
  public int getSizeT(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    if (noPOI || needLegacy) return legacy.getSizeT(id);
    return super.getSizeT(id);
  }

  /* @see loci.formats.IFormatReader#getPixelType(String) */
  public int getPixelType(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    if (noPOI || needLegacy) return legacy.getPixelType(id);
    return super.getPixelType(id);
  }

  /**
   * Return a five-character string representing the dimension order
   * within the file.
   */
  public String getDimensionOrder(String id)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (noPOI || needLegacy) return legacy.getDimensionOrder(id);
    return super.getDimensionOrder(id);
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    if (noPOI || needLegacy) return legacy.isRGB(id);
    return nChannels > 1 && (nChannels * zSize * tSize != nImages);
  }

  /** Return true if the data is in little-endian format. */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    return true;
  }

  /** Returns whether or not the channels are interleaved. */
  public boolean isInterleaved(String id) throws FormatException, IOException {
    return false;
  }

  /* @see FormatReader#setMetadataStore(MetadataStore) */
  public void setMetadataStore(MetadataStore store) {
    super.setMetadataStore(store);
    if (noPOI || needLegacy) legacy.setMetadataStore(store);
  }

  /* @see FormatReader#swapDimensions(String, String) */
  public void swapDimensions(String id, String order)
    throws FormatException, IOException
  {
    if (noPOI || needLegacy) legacy.swapDimensions(id, order);
    else super.swapDimensions(id, order);
  }

  /** Obtains the specified image from the given ZVI file, as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    byte[] buf = new byte[sizeX[0] * sizeY[0] * bpp];
    return openBytes(id, no, buf);
  }

  public byte[] openBytes(String id, int no, byte[] buf)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (noPOI || needLegacy) return legacy.openBytes(id, no, buf);
    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    if (buf.length < sizeX[0] * sizeY[0] * bpp) {
      throw new FormatException("Buffer too small.");
    }

    try {
      Object directory = pixels.get(new Integer(no));
      String name = (String) names.get(new Integer(no));

      r.setVar("dir", directory);
      r.setVar("entryName", name);
      r.exec("document = dir.getEntry(entryName)");
      r.exec("dis = new DocumentInputStream(document)");
      r.exec("numBytes = dis.available()");
      int numBytes = ((Integer) r.getVar("numBytes")).intValue();
      r.setVar("skipBytes",
        ((Integer) offsets.get(new Integer(no))).longValue());
      r.exec("blah = dis.skip(skipBytes)");
      r.setVar("data", buf);
      r.exec("dis.read(data)");

      if (bpp > 6) bpp = 1;

      if (bpp == 3) {
        // reverse bytes in groups of 3 to account for BGR storage
        for (int i=0; i<buf.length; i+=3) {
          byte r = buf[i + 2];
          buf[i + 2] = buf[i];
          buf[i] = r;
        }
      }

      updateMinMax(buf, no);
      return buf;
    }
    catch (ReflectException e) {
      needLegacy = true;
      return openBytes(id, no, buf);
    }
  }

  /** Obtains the specified image from the given ZVI file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    BufferedImage b = ImageTools.makeImage(openBytes(id, no), width, height,
      isRGB(id) ? 3 : 1, true, bpp == 3 ? 1 : bpp, true, validBits);
    updateMinMax(b, no);
    return b;
  }

  /* @see IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws FormatException, IOException {
    if (fileOnly) {
      if (ras != null) ras.close();
      if (legacy != null) legacy.close(fileOnly);
    }
    else close();
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    currentId = null;
    needLegacy = false;
    if (ras != null) ras.close();
    ras = null;

    if (legacy != null) legacy.close();
    pixels = null;
    names = null;
    offsets = null;

    String[] vars = {"dirName", "root", "dir", "document", "dis",
      "numBytes", "data", "fis", "fs", "iter", "isInstance", "isDocument",
      "entry", "documentName", "entryName"};
    for (int i=0; i<vars.length; i++) r.setVar(vars[i], null);
  }

  /** Initializes the given ZVI file. */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("ZeissZVIReader.initFile(" + id + ")");

    if (noPOI || needLegacy) {
      legacy.initFile(id);
      return;
    }

    close();

    currentId = id;

    metadata = new Hashtable();
    pixels = new Hashtable();
    names = new Hashtable();
    offsets = new Hashtable();
    zIndices = new Vector();
    cIndices = new Vector();
    tIndices = new Vector();

    sizeX = new int[1];
    sizeY = new int[1];
    sizeZ = new int[1];
    sizeC = new int[1];
    sizeT = new int[1];
    pixelType = new int[1];
    currentOrder = new String[1];
    orderCertain = new boolean[] {true};
    imagesRead = new Vector[] {new Vector()};
    minimumValues = new Vector[] {new Vector()};
    maximumValues = new Vector[] {new Vector()};
    minMaxFinished = new boolean[] {false};

    nImages = 0;

    try {
      ras = new RandomAccessStream(id);

      // Don't uncomment this block.  Even though OIBReader has something
      // like this, it's really a bad idea here.  Every ZVI file we have *will*
      // break if you uncomment it.
      //
      //if (ras.length() % 4096 != 0) {
      //  ras.setExtend((4096 - (int) (ras.length() % 4096)));
      //}

      r.setVar("fis", ras);
      r.exec("fs = new POIFSFileSystem(fis)");
      r.exec("dir = fs.getRoot()");
      parseDir(0, r.getVar("dir"));

      zSize = zIndices.size();
      tSize = tIndices.size();
      if (nChannels != cIndices.size()) nChannels *= cIndices.size();

      sizeX[0] = width;
      sizeY[0] = height;
      sizeZ[0] = zSize;
      sizeC[0] = nChannels;
      sizeT[0] = tSize;

      nImages = sizeZ[0] * sizeT[0] * getEffectiveSizeC(id);

      String s = (String) getMeta("Acquisition Bit Depth");
      if (s != null && s.trim().length() > 0) {
        validBits = new int[nChannels];
        if (nChannels == 2) validBits = new int[3];
        for (int i=0; i<nChannels; i++) {
          validBits[i] = Integer.parseInt(s.trim());
        }
      }
      else validBits = null;

      Object check = getMeta("Image Channel Index");
      if (check != null && !check.toString().trim().equals("")) {
        String zIndex = (String) getMeta("Image Index Z");
        String tIndex = (String) getMeta("Image Index T");

        int[] dims = {sizeZ[0], sizeC[0], sizeT[0]};
        int max = 0, min = Integer.MAX_VALUE, maxNdx = 0, minNdx = 0;
        String[] axes = {"Z", "C", "T"};

        for (int i=0; i<dims.length; i++) {
          if (dims[i] > max) {
            max = dims[i];
            maxNdx = i;
          }
          if (dims[i] < min) {
            min = dims[i];
            minNdx = i;
          }
        }

        int medNdx = 0;
        for (int i=0; i<3; i++) {
          if (i != maxNdx && i != minNdx) medNdx = i;
        }

        currentOrder[0] = "XY" + axes[maxNdx] + axes[medNdx] + axes[minNdx];

        if (zIndex != null && tIndex != null) {
          int z = Integer.parseInt(DataTools.stripString(zIndex));
          int t = Integer.parseInt(DataTools.stripString(tIndex));

          if (z != sizeZ[0]) {
            if (sizeZ[0] != 1) {
              currentOrder[0] = currentOrder[0].replaceAll("Z", "") + "Z";
            }
            else currentOrder[0] = currentOrder[0].replaceAll("T", "") + "T";
          }
        }

        if (sizeZ[0] == sizeC[0] && sizeC[0] == sizeT[0]) {
          currentOrder[0] = legacy.getDimensionOrder(id);
        }
      }
      else if (getMeta("MultiChannel Color") != null) {
        currentOrder[0] = (zSize > tSize) ? "XYCZT" : "XYCTZ";
      }
      else currentOrder[0] = (zSize > tSize) ? "XYZTC" : "XYTZC";
    }
    catch (ReflectException e) {
      needLegacy = true;
      if (debug) e.printStackTrace();
      initFile(id);
    }

    try {
      initMetadata();
    }
    catch (FormatException e) {
      if (debug) e.printStackTrace();
    }
    catch (IOException e) {
      if (debug) e.printStackTrace();
    }
  }

  // -- Helper methods --

  /** Initialize metadata hashtable and OME-XML structure. */
  private void initMetadata() throws FormatException, IOException {
    MetadataStore store = getMetadataStore(currentId);

    store.setImage((String) getMeta("Title"), null, null, null);

    if (bpp == 1 || bpp == 3) pixelType[0] = FormatTools.UINT8;
    else if (bpp == 2 || bpp == 6) pixelType[0] = FormatTools.UINT16;

    store.setPixels(
      new Integer(getSizeX(currentId)),
      new Integer(getSizeY(currentId)),
      new Integer(getSizeZ(currentId)),
      new Integer(getSizeC(currentId)),
      new Integer(getSizeT(currentId)),
      new Integer(pixelType[0]),
      new Boolean(false),
      getDimensionOrder(currentId),
      null,
      null);

    String pixX = (String) getMeta("Scale Factor for X");
    String pixY = (String) getMeta("Scale Factor for Y");
    String pixZ = (String) getMeta("Scale Factor for Z");

    store.setDimensions(
      pixX == null ? null : new Float(pixX),
      pixY == null ? null : new Float(pixY),
      pixZ == null ? null : new Float(pixZ),
      null, null, null);

    for (int i=0; i<sizeC[0]; i++) {
      store.setLogicalChannel(i, null, null, null, null, null, null, null);
    }
  }

  protected void parseDir(int depth, Object dir)
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

      if (isInstance)  {
        parseDir(depth + 1, r.getVar("entry"));
      }
      else if (isDocument) {
        r.exec("entryName = entry.getName()");
        if (debug) {
          print(depth + 1, "Found document: " + r.getVar("entryName"));
        }
        r.exec("dis = new DocumentInputStream(entry)");
        r.exec("numBytes = dis.available()");
        int numbytes = ((Integer) r.getVar("numBytes")).intValue();
        byte[] data = new byte[numbytes + 4]; // append 0 for final offset
        r.setVar("data", data);

        // Suppressing an exception here looks like poor style.
        // However, this at least gives us a chance at reading files with
        // corrupt blocks.
        try {
          r.exec("dis.read(data)");
        }
        catch (ReflectException e) {
          if (debug) e.printStackTrace();
        }

        String entryName = (String) r.getVar("entryName");
        String dirName = (String) r.getVar("dirName");

        boolean isContents = entryName.toUpperCase().equals("CONTENTS");
        Object directory = r.getVar("dir");

        int pt = 0;

        if (dirName.toUpperCase().equals("ROOT ENTRY") ||
          dirName.toUpperCase().equals("ROOTENTRY"))
        {
          if (entryName.equals("Tags")) {
            pt += 18;
            int version = DataTools.bytesToInt(data, pt, 4, true);
            pt += 4;

            pt += 2;
            int count = DataTools.bytesToInt(data, pt, 4, true); // # of tags
            pt += 4;

            // limit count to 4096
            if (count > 4096) count = 4096;

            for (int i=0; i<count; i++) {
              int type = DataTools.bytesToInt(data, pt, 2, true);
              pt += 2;

              String value = "";
              switch (type) {
                case 0:
                  break;
                case 1:
                  break;
                case 2:
                  value = "" + DataTools.bytesToInt(data, pt, 2, true);
                  pt += 2;
                  break;
                case 3:
                  value = "" + DataTools.bytesToInt(data, pt, 4, true);
                  pt += 4;
                  break;
                case 4:
                  value = "" + Float.intBitsToFloat(
                    DataTools.bytesToInt(data, pt, 4, true));
                  pt += 4;
                  break;
                case 5:
                  value = "" + Double.longBitsToDouble(
                    DataTools.bytesToLong(data, pt, 8, true));
                  pt += 8;
                  break;
                case 7:
                  value = "" + DataTools.bytesToLong(data, pt, 8, true);
                  pt += 8;
                  break;
                case 69:
                case 8:
                  int len = DataTools.bytesToInt(data, pt, 4, true);
                  pt += 4;
                  value = new String(data, pt, len);
                  pt += len;
                  break;
                case 20:
                case 21:
                  value = "" + DataTools.bytesToLong(data, pt, 8, true);
                  pt += 8;
                  break;
                case 22:
                case 23:
                  value = "" + DataTools.bytesToInt(data, pt, 4, true);
                  pt += 4;
                  break;
                case 66:
                  int l = DataTools.bytesToInt(data, pt, 2, true);
                  pt += 2;
                  value = new String(data, pt - 2, l + 2);
                  pt += l;
                  break;
                default:
                  int oldPt = pt;
                  while (DataTools.bytesToInt(data, pt, 2, true) != 3 &&
                    pt < data.length)
                  {
                    pt += 2;
                  }
                  value = new String(data, oldPt - 2, pt - oldPt + 2);
              }

              pt += 2;
              int tagID = DataTools.bytesToInt(data, pt, 4, true);
              pt += 4;
              pt += 2;
              int attribute = DataTools.bytesToInt(data, pt, 4, true);
              pt += 4;
              parseTag(value, tagID, attribute);
            }
          }
        }
        else if (dirName.equals("Tags") && isContents) {
          pt += 18;
          int version = DataTools.bytesToInt(data, pt, 4, true);
          pt += 4;

          pt += 2;
          int count = DataTools.bytesToInt(data, pt, 4, true); // # of tags
          pt += 4;

          // limit count to 4096
          if (count > 4096) count = 4096;

          for (int i=0; i<count; i++) {
            int type = DataTools.bytesToInt(data, pt, 2, true);
            pt += 2;

            String value = "";
            switch (type) {
              case 0:
                break;
              case 1:
                break;
              case 2:
                value = "" + DataTools.bytesToInt(data, pt, 2, true);
                pt += 2;
                break;
              case 3:
                value = "" + DataTools.bytesToInt(data, pt, 4, true);
                pt += 4;
                break;
              case 4:
                value = "" + Float.intBitsToFloat(
                  DataTools.bytesToInt(data, pt, 4, true));
                pt += 4;
                break;
              case 5:
                value = "" + Double.longBitsToDouble(
                  DataTools.bytesToLong(data, pt, 8, true));
                pt += 8;
                break;
              case 7:
                value = "" + DataTools.bytesToLong(data, pt, 8, true);
                pt += 8;
                break;
              case 69:
              case 8:
                int len = DataTools.bytesToInt(data, pt, 4, true);
                pt += 4;
                if (pt + len < data.length) {
                  value = new String(data, pt, len);
                  pt += len;
                }
                else return;
                break;
              case 20:
              case 21:
                value = "" + DataTools.bytesToLong(data, pt, 8, true);
                pt += 8;
                break;
              case 22:
              case 23:
                value = "" + DataTools.bytesToInt(data, pt, 4, true);
                pt += 4;
                break;
              case 66:
                int l = DataTools.bytesToInt(data, pt, 2, true);
                pt += 2;
                value = new String(data, pt - 2, l + 2);
                pt += l;
                break;
              default:
                int oldPt = pt;
                while (DataTools.bytesToInt(data, pt, 2, true) != 3 &&
                  pt < data.length)
                {
                  pt += 2;
                }
                if (oldPt - 2 > 0 && pt < data.length) {
                  value = new String(data, oldPt - 2, pt - oldPt + 2);
                }
                else return;
            }

            pt += 2;
            int tagID = DataTools.bytesToInt(data, pt, 4, true);
            pt += 4;
            pt += 2;
            int attribute = DataTools.bytesToInt(data, pt, 4, true);
            pt += 4;
            parseTag(value, tagID, attribute);
          }
        }
        else if (isContents && (dirName.equals("Image") ||
          dirName.toUpperCase().indexOf("ITEM") != -1) &&
          (data.length > width*height))
        {
          pt += 2;
          int version = DataTools.bytesToInt(data, pt, 4, true);
          pt += 4;

          int vt = DataTools.bytesToInt(data, pt, 2, true);
          pt += 2;
          if (vt == 3) {
            int type = DataTools.bytesToInt(data, pt, 4, true);
            pt += 6;
          }
          else if (vt == 8) {
            int l = DataTools.bytesToInt(data, pt, 2, true);
            pt += 4 + l;
          }
          int len = DataTools.bytesToInt(data, pt, 2, true);
          pt += 2;
          if (data[pt] == 0 && data[pt + 1] == 0) pt += 2;

          String typeDescription = "";
          if (pt + len <= data.length) {
            typeDescription = new String(data, pt, len);
            pt += len;
          }
          else break;

          vt = DataTools.bytesToInt(data, pt, 2, true);
          pt += 2;
          if (vt == 8) {
            len = DataTools.bytesToInt(data, pt, 4, true);
            pt += 6 + len;
          }

          int tw = DataTools.bytesToInt(data, pt, 4, true);
          if (width == 0 || (tw < width && tw > 0)) width = tw;
          pt += 6;
          int th = DataTools.bytesToInt(data, pt, 4, true);
          if (height == 0 || (th < height && th > 0)) height = th;
          pt += 6;

          int zDepth = DataTools.bytesToInt(data, pt, 4, true);
          pt += 6;
          int pixelFormat = DataTools.bytesToInt(data, pt, 4, true);
          pt += 6;
          int numImageContainers = DataTools.bytesToInt(data, pt, 4, true);
          pt += 6;
          int validBitsPerPixel = DataTools.bytesToInt(data, pt, 4, true);
          pt += 4;

          // VT_CLSID - PluginCLSID
          while (DataTools.bytesToInt(data, pt, 2, true) != 65) {
            pt += 2;
          }

          // VT_BLOB - Others
          pt += 2;
          len = DataTools.bytesToInt(data, pt, 4, true);
          pt += len + 4;

          // VT_STORED_OBJECT - Layers
          pt += 2;
          int oldPt = pt;
          len = DataTools.bytesToInt(data, pt, 4, true);
          pt += 4;

          pt += 8;

          int tIndex = DataTools.bytesToInt(data, pt, 4, true);
          pt += 4;
          int cIndex = DataTools.bytesToInt(data, pt, 4, true);
          pt += 4;
          int zIndex = DataTools.bytesToInt(data, pt, 4, true);
          pt += 4;

          Integer zndx = new Integer(zIndex);
          Integer cndx = new Integer(cIndex);
          Integer tndx = new Integer(tIndex);

          if (!zIndices.contains(zndx)) zIndices.add(zndx);
          if (!cIndices.contains(cndx)) cIndices.add(cndx);
          if (!tIndices.contains(tndx)) tIndices.add(tndx);

          pt = oldPt + 4 + len;

          boolean foundWidth = DataTools.bytesToInt(data, pt, 4, true) == width;
          boolean foundHeight =
            DataTools.bytesToInt(data, pt + 4, 4, true) == height;
          boolean findFailed = false;
          while ((!foundWidth || !foundHeight) && pt + 9 < data.length) {
            pt++;
            foundWidth = DataTools.bytesToInt(data, pt, 4, true) == width;
            foundHeight =
              DataTools.bytesToInt(data, pt + 4, 4, true) == height;
          }
          pt -= 8;
          findFailed = !foundWidth && !foundHeight;

          // image header and data

          if (dirName.toUpperCase().indexOf("ITEM") != -1 ||
            (dirName.equals("Image") && numImageContainers == 0))
          {
            if (findFailed) pt = oldPt + 4 + len + 88;
            byte[] o = new byte[data.length - pt];
            System.arraycopy(data, pt, o, 0, o.length);

            int imageNum = 0;
            if (dirName.toUpperCase().indexOf("ITEM") != -1) {
              String num = dirName.substring(5);
              num = num.substring(0, num.length() - 1);
              imageNum = Integer.parseInt(num);
            }

            offsets.put(new Integer(imageNum), new Integer(pt + 32));
            parsePlane(o, imageNum, directory, entryName);
          }
        }

        data = null;
        r.exec("dis.close()");
      }
    }
  }

  /** Debugging helper method. */
  protected void print(int depth, String s) {
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<depth; i++) sb.append("  ");
    sb.append(s);
    debug(sb.toString());
  }

  /** Parse a plane of data. */
  private void parsePlane(byte[] data, int num, Object directory, String entry)
  {
    int pt = 2;

    int version = DataTools.bytesToInt(data, pt, 4, true);
    pt += 6;
    width = DataTools.bytesToInt(data, pt, 4, true);
    pt += 4;
    height = DataTools.bytesToInt(data, pt, 4, true);
    pt += 4;
    int depth = DataTools.bytesToInt(data, pt, 4, true); // z depth
    pt += 4;
    bpp = DataTools.bytesToInt(data, pt, 4, true);
    pt += 4;
    int pixelFormat = DataTools.bytesToInt(data, pt, 4, true); // ignore here
    pt += 4;
    int validBitsPerPixel = DataTools.bytesToInt(data, pt, 4, true);
    pt += 4;

    pixels.put(new Integer(num), directory);
    names.put(new Integer(num), entry);
    nImages++;
    if (bpp % 3 == 0) nChannels = 3;
  }

  /** Parse a tag and place it in the metadata hashtable. */
  private void parseTag(String data, int tagID, int attribute) {
    switch (tagID) {
      case 222:
        addMeta("Compression", data);
        break;
      case 258:
        addMeta("BlackValue", data);
        break;
      case 259:
        addMeta("WhiteValue", data);
        break;
      case 260:
        addMeta("ImageDataMappingAutoRange", data);
        break;
      case 261:
        addMeta("Thumbnail", data);
        break;
      case 262:
        addMeta("GammaValue", data);
        break;
      case 264:
        addMeta("ImageOverExposure", data);
        break;
      case 265:
        addMeta("ImageRelativeTime1", data);
        break;
      case 266:
        addMeta("ImageRelativeTime2", data);
        break;
      case 267:
        addMeta("ImageRelativeTime3", data);
        break;
      case 268:
        addMeta("ImageRelativeTime4", data);
        break;
      case 333:
        addMeta("RelFocusPosition1", data);
        break;
      case 334:
        addMeta("RelFocusPosition2", data);
        break;
      case 513:
        addMeta("ObjectType", data);
        break;
      case 515:
        addMeta("ImageWidth", data);
        break;
      case 516:
        addMeta("ImageHeight", data);
        break;
      case 517:
        addMeta("Number Raw Count", data);
        break;
      case 518:
        addMeta("PixelType", data);
        break;
      case 519:
        addMeta("NumberOfRawImages", data);
        break;
      case 520:
        addMeta("ImageSize", data);
        break;
      case 523:
        addMeta("Acquisition pause annotation", data);
        break;
      case 530:
        addMeta("Document Subtype", data);
        break;
      case 531:
        addMeta("Acquisition Bit Depth", data);
        break;
      case 532:
        addMeta("Image Memory Usage (RAM)", data);
        break;
      case 534:
        addMeta("Z-Stack single representative", data);
        break;
      case 769:
        addMeta("Scale Factor for X", data);
        break;
      case 770:
        addMeta("Scale Unit for X", data);
        break;
      case 771:
        addMeta("Scale Width", data);
        break;
      case 772:
        addMeta("Scale Factor for Y", data);
        break;
      case 773:
        addMeta("Scale Unit for Y", data);
        break;
      case 774:
        addMeta("Scale Height", data);
        break;
      case 775:
        addMeta("Scale Factor for Z", data);
        break;
      case 776:
        addMeta("Scale Unit for Z", data);
        break;
      case 777:
        addMeta("Scale Depth", data);
        break;
      case 778:
        addMeta("Scaling Parent", data);
        break;
      case 1001:
        addMeta("Date", data);
        break;
      case 1002:
        addMeta("code", data);
        break;
      case 1003:
        addMeta("Source", data);
        break;
      case 1004:
        addMeta("Message", data);
        break;
      case 1025:
        addMeta("Acquisition Date", data);
        break;
      case 1026:
        addMeta("8-bit acquisition", data);
        break;
      case 1027:
        addMeta("Camera Bit Depth", data);
        break;
      case 1029:
        addMeta("MonoReferenceLow", data);
        break;
      case 1030:
        addMeta("MonoReferenceHigh", data);
        break;
      case 1031:
        addMeta("RedReferenceLow", data);
        break;
      case 1032:
        addMeta("RedReferenceHigh", data);
        break;
      case 1033:
        addMeta("GreenReferenceLow", data);
        break;
      case 1034:
        addMeta("GreenReferenceHigh", data);
        break;
      case 1035:
        addMeta("BlueReferenceLow", data);
        break;
      case 1036:
        addMeta("BlueReferenceHigh", data);
        break;
      case 1041:
        addMeta("FrameGrabber Name", data);
        break;
      case 1042:
        addMeta("Camera", data);
        break;
      case 1044:
        addMeta("CameraTriggerSignalType", data);
        break;
      case 1045:
        addMeta("CameraTriggerEnable", data);
        break;
      case 1046:
        addMeta("GrabberTimeout", data);
        break;
      case 1281:
        addMeta("MultiChannelEnabled", data);
        break;
      case 1282:
        addMeta("MultiChannel Color", data);
        break;
      case 1283:
        addMeta("MultiChannel Weight", data);
        break;
      case 1284:
        addMeta("Channel Name", data);
        break;
      case 1536:
        addMeta("DocumentInformationGroup", data);
        break;
      case 1537:
        addMeta("Title", data);
        break;
      case 1538:
        addMeta("Author", data);
        break;
      case 1539:
        addMeta("Keywords", data);
        break;
      case 1540:
        addMeta("Comments", data);
        break;
      case 1541:
        addMeta("SampleID", data);
        break;
      case 1542:
        addMeta("Subject", data);
        break;
      case 1543:
        addMeta("RevisionNumber", data);
        break;
      case 1544:
        addMeta("Save Folder", data);
        break;
      case 1545:
        addMeta("FileLink", data);
        break;
      case 1546:
        addMeta("Document Type", data);
        break;
      case 1547:
        addMeta("Storage Media", data);
        break;
      case 1548:
        addMeta("File ID", data);
        break;
      case 1549:
        addMeta("Reference", data);
        break;
      case 1550:
        addMeta("File Date", data);
        break;
      case 1551:
        addMeta("File Size", data);
        break;
      case 1553:
        addMeta("Filename", data);
        break;
      case 1792:
        addMeta("ProjectGroup", data);
        break;
      case 1793:
        addMeta("Acquisition Date", data);
        break;
      case 1794:
        addMeta("Last modified by", data);
        break;
      case 1795:
        addMeta("User company", data);
        break;
      case 1796:
        addMeta("User company logo", data);
        break;
      case 1797:
        addMeta("Image", data);
        break;
      case 1800:
        addMeta("User ID", data);
        break;
      case 1801:
        addMeta("User Name", data);
        break;
      case 1802:
        addMeta("User City", data);
        break;
      case 1803:
        addMeta("User Address", data);
        break;
      case 1804:
        addMeta("User Country", data);
        break;
      case 1805:
        addMeta("User Phone", data);
        break;
      case 1806:
        addMeta("User Fax", data);
        break;
      case 2049:
        addMeta("Objective Name", data);
        break;
      case 2050:
        addMeta("Optovar", data);
        break;
      case 2051:
        addMeta("Reflector", data);
        break;
      case 2052:
        addMeta("Condenser Contrast", data);
        break;
      case 2053:
        addMeta("Transmitted Light Filter 1", data);
        break;
      case 2054:
        addMeta("Transmitted Light Filter 2", data);
        break;
      case 2055:
        addMeta("Reflected Light Shutter", data);
        break;
      case 2056:
        addMeta("Condenser Front Lens", data);
        break;
      case 2057:
        addMeta("Excitation Filter Name", data);
        break;
      case 2060:
        addMeta("Transmitted Light Fieldstop Aperture", data);
        break;
      case 2061:
        addMeta("Reflected Light Aperture", data);
        break;
      case 2062:
        addMeta("Condenser N.A.", data);
        break;
      case 2063:
        addMeta("Light Path", data);
        break;
      case 2064:
        addMeta("HalogenLampOn", data);
        break;
      case 2065:
        addMeta("Halogen Lamp Mode", data);
        break;
      case 2066:
        addMeta("Halogen Lamp Voltage", data);
        break;
      case 2068:
        addMeta("Fluorescence Lamp Level", data);
        break;
      case 2069:
        addMeta("Fluorescence Lamp Intensity", data);
        break;
      case 2070:
        addMeta("LightManagerEnabled", data);
        break;
      case 2071:
        addMeta("tag_ID_2071", data);
        break;
      case 2072:
        addMeta("Focus Position", data);
        break;
      case 2073:
        addMeta("Stage Position X", data);
        break;
      case 2074:
        addMeta("Stage Position Y", data);
        break;
      case 2075:
        addMeta("Microscope Name", data);
        break;
      case 2076:
        addMeta("Objective Magnification", data);
        break;
      case 2077:
        addMeta("Objective N.A.", data);
        break;
      case 2078:
        addMeta("MicroscopeIllumination", data);
        break;
      case 2079:
        addMeta("External Shutter 1", data);
        break;
      case 2080:
        addMeta("External Shutter 2", data);
        break;
      case 2081:
        addMeta("External Shutter 3", data);
        break;
      case 2082:
        addMeta("External Filter Wheel 1 Name", data);
        break;
      case 2083:
        addMeta("External Filter Wheel 2 Name", data);
        break;
      case 2084:
        addMeta("Parfocal Correction", data);
        break;
      case 2086:
        addMeta("External Shutter 4", data);
        break;
      case 2087:
        addMeta("External Shutter 5", data);
        break;
      case 2088:
        addMeta("External Shutter 6", data);
        break;
      case 2089:
        addMeta("External Filter Wheel 3 Name", data);
        break;
      case 2090:
        addMeta("External Filter Wheel 4 Name", data);
        break;
      case 2103:
        addMeta("Objective Turret Position", data);
        break;
      case 2104:
        addMeta("Objective Contrast Method", data);
        break;
      case 2105:
        addMeta("Objective Immersion Type", data);
        break;
      case 2107:
        addMeta("Reflector Position", data);
        break;
      case 2109:
        addMeta("Transmitted Light Filter 1 Position", data);
        break;
      case 2110:
        addMeta("Transmitted Light Filter 2 Position", data);
        break;
      case 2112:
        addMeta("Excitation Filter Position", data);
        break;
      case 2113:
        addMeta("Lamp Mirror Position", data);
        break;
      case 2114:
        addMeta("External Filter Wheel 1 Position", data);
        break;
      case 2115:
        addMeta("External Filter Wheel 2 Position", data);
        break;
      case 2116:
        addMeta("External Filter Wheel 3 Position", data);
        break;
      case 2117:
        addMeta("External Filter Wheel 4 Position", data);
        break;
      case 2118:
        addMeta("Lightmanager Mode", data);
        break;
      case 2119:
        addMeta("Halogen Lamp Calibration", data);
        break;
      case 2120:
        addMeta("CondenserNAGoSpeed", data);
        break;
      case 2121:
        addMeta("TransmittedLightFieldstopGoSpeed", data);
        break;
      case 2122:
        addMeta("OptovarGoSpeed", data);
        break;
      case 2123:
        addMeta("Focus calibrated", data);
        break;
      case 2124:
        addMeta("FocusBasicPosition", data);
        break;
      case 2125:
        addMeta("FocusPower", data);
        break;
      case 2126:
        addMeta("FocusBacklash", data);
        break;
      case 2127:
        addMeta("FocusMeasurementOrigin", data);
        break;
      case 2128:
        addMeta("FocusMeasurementDistance", data);
        break;
      case 2129:
        addMeta("FocusSpeed", data);
        break;
      case 2130:
        addMeta("FocusGoSpeed", data);
        break;
      case 2131:
        addMeta("FocusDistance", data);
        break;
      case 2132:
        addMeta("FocusInitPosition", data);
        break;
      case 2133:
        addMeta("Stage calibrated", data);
        break;
      case 2134:
        addMeta("StagePower", data);
        break;
      case 2135:
        addMeta("StageXBacklash", data);
        break;
      case 2136:
        addMeta("StageYBacklash", data);
        break;
      case 2137:
        addMeta("StageSpeedX", data);
        break;
      case 2138:
        addMeta("StageSpeedY", data);
        break;
      case 2139:
        addMeta("StageSpeed", data);
        break;
      case 2140:
        addMeta("StageGoSpeedX", data);
        break;
      case 2141:
        addMeta("StageGoSpeedY", data);
        break;
      case 2142:
        addMeta("StageStepDistanceX", data);
        break;
      case 2143:
        addMeta("StageStepDistanceY", data);
        break;
      case 2144:
        addMeta("StageInitialisationPositionX", data);
        break;
      case 2145:
        addMeta("StageInitialisationPositionY", data);
        break;
      case 2146:
        addMeta("MicroscopeMagnification", data);
        break;
      case 2147:
        addMeta("ReflectorMagnification", data);
        break;
      case 2148:
        addMeta("LampMirrorPosition", data);
        break;
      case 2149:
        addMeta("FocusDepth", data);
        break;
      case 2150:
        addMeta("MicroscopeType", data);
        break;
      case 2151:
        addMeta("Objective Working Distance", data);
        break;
      case 2152:
        addMeta("ReflectedLightApertureGoSpeed", data);
        break;
      case 2153:
        addMeta("External Shutter", data);
        break;
      case 2154:
        addMeta("ObjectiveImmersionStop", data);
        break;
      case 2155:
        addMeta("Focus Start Speed", data);
        break;
      case 2156:
        addMeta("Focus Acceleration", data);
        break;
      case 2157:
        addMeta("ReflectedLightFieldstop", data);
        break;
      case 2158:
        addMeta("ReflectedLightFieldstopGoSpeed", data);
        break;
      case 2159:
        addMeta("ReflectedLightFilter 1", data);
        break;
      case 2160:
        addMeta("ReflectedLightFilter 2", data);
        break;
      case 2161:
        addMeta("ReflectedLightFilter1Position", data);
        break;
      case 2162:
        addMeta("ReflectedLightFilter2Position", data);
        break;
      case 2163:
        addMeta("TransmittedLightAttenuator", data);
        break;
      case 2164:
        addMeta("ReflectedLightAttenuator", data);
        break;
      case 2165:
        addMeta("Transmitted Light Shutter", data);
        break;
      case 2166:
        addMeta("TransmittedLightAttenuatorGoSpeed", data);
        break;
      case 2167:
        addMeta("ReflectedLightAttenuatorGoSpeed", data);
        break;
      case 2176:
        addMeta("TransmittedLightVirtualFilterPosition", data);
        break;
      case 2177:
        addMeta("TransmittedLightVirtualFilter", data);
        break;
      case 2178:
        addMeta("ReflectedLightVirtualFilterPosition", data);
        break;
      case 2179:
        addMeta("ReflectedLightVirtualFilter", data);
        break;
      case 2180:
        addMeta("ReflectedLightHalogenLampMode", data);
        break;
      case 2181:
        addMeta("ReflectedLightHalogenLampVoltage", data);
        break;
      case 2182:
        addMeta("ReflectedLightHalogenLampColorTemperature", data);
        break;
      case 2183:
        addMeta("ContrastManagerMode", data);
        break;
      case 2184:
        addMeta("Dazzle Protection Active", data);
        break;
      case 2195:
        addMeta("Zoom", data);
        break;
      case 2196:
        addMeta("ZoomGoSpeed", data);
        break;
      case 2197:
        addMeta("LightZoom", data);
        break;
      case 2198:
        addMeta("LightZoomGoSpeed", data);
        break;
      case 2199:
        addMeta("LightZoomCoupled", data);
        break;
      case 2200:
        addMeta("TransmittedLightHalogenLampMode", data);
        break;
      case 2201:
        addMeta("TransmittedLightHalogenLampVoltage", data);
        break;
      case 2202:
        addMeta("TransmittedLightHalogenLampColorTemperature", data);
        break;
      case 2203:
        addMeta("Reflected Coldlight Mode", data);
        break;
      case 2204:
        addMeta("Reflected Coldlight Intensity", data);
        break;
      case 2205:
        addMeta("Reflected Coldlight Color Temperature", data);
        break;
      case 2206:
        addMeta("Transmitted Coldlight Mode", data);
        break;
      case 2207:
        addMeta("Transmitted Coldlight Intensity", data);
        break;
      case 2208:
        addMeta("Transmitted Coldlight Color Temperature", data);
        break;
      case 2209:
        addMeta("Infinityspace Portchanger Position", data);
        break;
      case 2210:
        addMeta("Beamsplitter Infinity Space", data);
        break;
      case 2211:
        addMeta("TwoTv VisCamChanger Position", data);
        break;
      case 2212:
        addMeta("Beamsplitter Ocular", data);
        break;
      case 2213:
        addMeta("TwoTv CamerasChanger Position", data);
        break;
      case 2214:
        addMeta("Beamsplitter Cameras", data);
        break;
      case 2215:
        addMeta("Ocular Shutter", data);
        break;
      case 2216:
        addMeta("TwoTv CamerasChangerCube", data);
        break;
      case 2218:
        addMeta("Ocular Magnification", data);
        break;
      case 2219:
        addMeta("Camera Adapter Magnification", data);
        break;
      case 2220:
        addMeta("Microscope Port", data);
        break;
      case 2221:
        addMeta("Ocular Total Magnification", data);
        break;
      case 2222:
        addMeta("Field of View", data);
        break;
      case 2223:
        addMeta("Ocular", data);
        break;
      case 2224:
        addMeta("CameraAdapter", data);
        break;
      case 2225:
        addMeta("StageJoystickEnabled", data);
        break;
      case 2226:
        addMeta("ContrastManager Contrast Method", data);
        break;
      case 2229:
        addMeta("CamerasChanger Beamsplitter Type", data);
        break;
      case 2235:
        addMeta("Rearport Slider Position", data);
        break;
      case 2236:
        addMeta("Rearport Source", data);
        break;
      case 2237:
        addMeta("Beamsplitter Type Infinity Space", data);
        break;
      case 2238:
        addMeta("Fluorescence Attenuator", data);
        break;
      case 2239:
        addMeta("Fluorescence Attenuator Position", data);
        break;
      case 2261:
        addMeta("Objective ID", data);
        break;
      case 2262:
        addMeta("Reflector ID", data);
        break;
      case 2307:
        addMeta("Camera Framestart Left", data);
        break;
      case 2308:
        addMeta("Camera Framestart Top", data);
        break;
      case 2309:
        addMeta("Camera Frame Width", data);
        break;
      case 2310:
        addMeta("Camera Frame Height", data);
        break;
      case 2311:
        addMeta("Camera Binning", data);
        break;
      case 2312:
        addMeta("CameraFrameFull", data);
        break;
      case 2313:
        addMeta("CameraFramePixelDistance", data);
        break;
      case 2318:
        addMeta("DataFormatUseScaling", data);
        break;
      case 2319:
        addMeta("CameraFrameImageOrientation", data);
        break;
      case 2320:
        addMeta("VideoMonochromeSignalType", data);
        break;
      case 2321:
        addMeta("VideoColorSignalType", data);
        break;
      case 2322:
        addMeta("MeteorChannelInput", data);
        break;
      case 2323:
        addMeta("MeteorChannelSync", data);
        break;
      case 2324:
        addMeta("WhiteBalanceEnabled", data);
        break;
      case 2325:
        addMeta("CameraWhiteBalanceRed", data);
        break;
      case 2326:
        addMeta("CameraWhiteBalanceGreen", data);
        break;
      case 2327:
        addMeta("CameraWhiteBalanceBlue", data);
        break;
      case 2331:
        addMeta("CameraFrameScalingFactor", data);
        break;
      case 2562:
        addMeta("Meteor Camera Type", data);
        break;
      case 2564:
        addMeta("Exposure Time [ms]", data);
        break;
      case 2568:
        addMeta("CameraExposureTimeAutoCalculate", data);
        break;
      case 2569:
        addMeta("Meteor Gain Value", data);
        break;
      case 2571:
        addMeta("Meteor Gain Automatic", data);
        break;
      case 2572:
        addMeta("MeteorAdjustHue", data);
        break;
      case 2573:
        addMeta("MeteorAdjustSaturation", data);
        break;
      case 2574:
        addMeta("MeteorAdjustRedLow", data);
        break;
      case 2575:
        addMeta("MeteorAdjustGreenLow", data);
        break;
      case 2576:
        addMeta("Meteor Blue Low", data);
        break;
      case 2577:
        addMeta("MeteorAdjustRedHigh", data);
        break;
      case 2578:
        addMeta("MeteorAdjustGreenHigh", data);
        break;
      case 2579:
        addMeta("MeteorBlue High", data);
        break;
      case 2582:
        addMeta("CameraExposureTimeCalculationControl", data);
        break;
      case 2585:
        addMeta("AxioCamFadingCorrectionEnable", data);
        break;
      case 2587:
        addMeta("CameraLiveImage", data);
        break;
      case 2588:
        addMeta("CameraLiveEnabled", data);
        break;
      case 2589:
        addMeta("LiveImageSyncObjectName", data);
        break;
      case 2590:
        addMeta("CameraLiveSpeed", data);
        break;
      case 2591:
        addMeta("CameraImage", data);
        break;
      case 2592:
        addMeta("CameraImageWidth", data);
        break;
      case 2593:
        addMeta("CameraImageHeight", data);
        break;
      case 2594:
        addMeta("CameraImagePixelType", data);
        break;
      case 2595:
        addMeta("CameraImageShMemoryName", data);
        break;
      case 2596:
        addMeta("CameraLiveImageWidth", data);
        break;
      case 2597:
        addMeta("CameraLiveImageHeight", data);
        break;
      case 2598:
        addMeta("CameraLiveImagePixelType", data);
        break;
      case 2599:
        addMeta("CameraLiveImageShMemoryName", data);
        break;
      case 2600:
        addMeta("CameraLiveMaximumSpeed", data);
        break;
      case 2601:
        addMeta("CameraLiveBinning", data);
        break;
      case 2602:
        addMeta("CameraLiveGainValue", data);
        break;
      case 2603:
        addMeta("CameraLiveExposureTimeValue", data);
        break;
      case 2604:
        addMeta("CameraLiveScalingFactor", data);
        break;
      case 2819:
        addMeta("Image Index Z", data);
        break;
      case 2820:
        addMeta("Image Channel Index", data);
        break;
      case 2821:
        addMeta("Image Index T", data);
        break;
      case 2822:
        addMeta("ImageTile Index", data);
        break;
      case 2823:
        addMeta("Image acquisition Index", data);
        break;
      case 2827:
        addMeta("Image IndexS", data);
        break;
      case 2841:
        addMeta("Original Stage Position X", data);
        break;
      case 2842:
        addMeta("Original Stage Position Y", data);
        break;
      case 3088:
        addMeta("LayerDrawFlags", data);
        break;
      case 3334:
        addMeta("RemainingTime", data);
        break;
      case 3585:
        addMeta("User Field 1", data);
        break;
      case 3586:
        addMeta("User Field 2", data);
        break;
      case 3587:
        addMeta("User Field 3", data);
        break;
      case 3588:
        addMeta("User Field 4", data);
        break;
      case 3589:
        addMeta("User Field 5", data);
        break;
      case 3590:
        addMeta("User Field 6", data);
        break;
      case 3591:
        addMeta("User Field 7", data);
        break;
      case 3592:
        addMeta("User Field 8", data);
        break;
      case 3593:
        addMeta("User Field 9", data);
        break;
      case 3594:
        addMeta("User Field 10", data);
        break;
      case 3840:
        addMeta("ID", data);
        break;
      case 3841:
        addMeta("Name", data);
        break;
      case 3842:
        addMeta("Value", data);
        break;
      case 5501:
        addMeta("PvCamClockingMode", data);
        break;
      case 8193:
        addMeta("Autofocus Status Report", data);
        break;
      case 8194:
        addMeta("Autofocus Position", data);
        break;
      case 8195:
        addMeta("Autofocus Position Offset", data);
        break;
      case 8196:
        addMeta("Autofocus Empty Field Threshold", data);
        break;
      case 8197:
        addMeta("Autofocus Calibration Name", data);
        break;
      case 8198:
        addMeta("Autofocus Current Calibration Item", data);
        break;
      case 20478:
        addMeta("tag_ID_20478", data);
        break;
      case 65537:
        addMeta("CameraFrameFullWidth", data);
        break;
      case 65538:
        addMeta("CameraFrameFullHeight", data);
        break;
      case 65541:
        addMeta("AxioCam Shutter Signal", data);
        break;
      case 65542:
        addMeta("AxioCam Delay Time", data);
        break;
      case 65543:
        addMeta("AxioCam Shutter Control", data);
        break;
      case 65544:
        addMeta("AxioCam BlackRefIsCalculated", data);
        break;
      case 65545:
        addMeta("AxioCam Black Reference", data);
        break;
      case 65547:
        addMeta("Camera Shading Correction", data);
        break;
      case 65550:
        addMeta("AxioCam Enhance Color", data);
        break;
      case 65551:
        addMeta("AxioCam NIR Mode", data);
        break;
      case 65552:
        addMeta("CameraShutterCloseDelay", data);
        break;
      case 65553:
        addMeta("CameraWhiteBalanceAutoCalculate", data);
        break;
      case 65556:
        addMeta("AxioCam NIR Mode Available", data);
        break;
      case 65557:
        addMeta("AxioCam Fading Correction Available", data);
        break;
      case 65559:
        addMeta("AxioCam Enhance Color Available", data);
        break;
      case 65565:
        addMeta("MeteorVideoNorm", data);
        break;
      case 65566:
        addMeta("MeteorAdjustWhiteReference", data);
        break;
      case 65567:
        addMeta("MeteorBlackReference", data);
        break;
      case 65568:
        addMeta("MeteorChannelInputCountMono", data);
        break;
      case 65570:
        addMeta("MeteorChannelInputCountRGB", data);
        break;
      case 65571:
        addMeta("MeteorEnableVCR", data);
        break;
      case 65572:
        addMeta("Meteor Brightness", data);
        break;
      case 65573:
        addMeta("Meteor Contrast", data);
        break;
      case 65575:
        addMeta("AxioCam Selector", data);
        break;
      case 65576:
        addMeta("AxioCam Type", data);
        break;
      case 65577:
        addMeta("AxioCam Info", data);
        break;
      case 65580:
        addMeta("AxioCam Resolution", data);
        break;
      case 65581:
        addMeta("AxioCam Color Model", data);
        break;
      case 65582:
        addMeta("AxioCam MicroScanning", data);
        break;
      case 65585:
        addMeta("Amplification Index", data);
        break;
      case 65586:
        addMeta("Device Command", data);
        break;
      case 65587:
        addMeta("BeamLocation", data);
        break;
      case 65588:
        addMeta("ComponentType", data);
        break;
      case 65589:
        addMeta("ControllerType", data);
        break;
      case 65590:
        addMeta("CameraWhiteBalanceCalculationRedPaint", data);
        break;
      case 65591:
        addMeta("CameraWhiteBalanceCalculationBluePaint", data);
        break;
      case 65592:
        addMeta("CameraWhiteBalanceSetRed", data);
        break;
      case 65593:
        addMeta("CameraWhiteBalanceSetGreen", data);
        break;
      case 65594:
        addMeta("CameraWhiteBalanceSetBlue", data);
        break;
      case 65595:
        addMeta("CameraWhiteBalanceSetTargetRed", data);
        break;
      case 65596:
        addMeta("CameraWhiteBalanceSetTargetGreen", data);
        break;
      case 65597:
        addMeta("CameraWhiteBalanceSetTargetBlue", data);
        break;
      case 65598:
        addMeta("ApotomeCamCalibrationMode", data);
        break;
      case 65599:
        addMeta("ApoTome Grid Position", data);
        break;
      case 65600:
        addMeta("ApotomeCamScannerPosition", data);
        break;
      case 65601:
        addMeta("ApoTome Full Phase Shift", data);
        break;
      case 65602:
        addMeta("ApoTome Grid Name", data);
        break;
      case 65603:
        addMeta("ApoTome Staining", data);
        break;
      case 65604:
        addMeta("ApoTome Processing Mode", data);
        break;
      case 65605:
        addMeta("ApotmeCamLiveCombineMode", data);
        break;
      case 65606:
        addMeta("ApoTome Filter Name", data);
        break;
      case 65607:
        addMeta("Apotome Filter Strength", data);
        break;
      case 65608:
        addMeta("ApotomeCamFilterHarmonics", data);
        break;
      case 65609:
        addMeta("ApoTome Grating Period", data);
        break;
      case 65610:
        addMeta("ApoTome Auto Shutter Used", data);
        break;
      case 65611:
        addMeta("Apotome Cam Status", data);
        break;
      case 65612:
        addMeta("ApotomeCamNormalize", data);
        break;
      case 65613:
        addMeta("ApotomeCamSettingsManager", data);
        break;
      case 65614:
        addMeta("DeepviewCamSupervisorMode", data);
        break;
      case 65615:
        addMeta("DeepView Processing", data);
        break;
      case 65616:
        addMeta("DeepviewCamFilterName", data);
        break;
      case 65617:
        addMeta("DeepviewCamStatus", data);
        break;
      case 65618:
        addMeta("DeepviewCamSettingsManager", data);
        break;
      case 65619:
        addMeta("DeviceScalingName", data);
        break;
      case 65620:
        addMeta("CameraShadingIsCalculated", data);
        break;
      case 65621:
        addMeta("CameraShadingCalculationName", data);
        break;
      case 65622:
        addMeta("CameraShadingAutoCalculate", data);
        break;
      case 65623:
        addMeta("CameraTriggerAvailable", data);
        break;
      case 65626:
        addMeta("CameraShutterAvailable", data);
        break;
      case 65627:
        addMeta("AxioCam ShutterMicroScanningEnable", data);
        break;
      case 65628:
        addMeta("ApotomeCamLiveFocus", data);
        break;
      case 65629:
        addMeta("DeviceInitStatus", data);
        break;
      case 65630:
        addMeta("DeviceErrorStatus", data);
        break;
      case 65631:
        addMeta("ApotomeCamSliderInGridPosition", data);
        break;
      case 65632:
        addMeta("Orca NIR Mode Used", data);
        break;
      case 65633:
        addMeta("Orca Analog Gain", data);
        break;
      case 65634:
        addMeta("Orca Analog Offset", data);
        break;
      case 65635:
        addMeta("Orca Binning", data);
        break;
      case 65636:
        addMeta("Orca Bit Depth", data);
        break;
      case 65637:
        addMeta("ApoTome Averaging Count", data);
        break;
      case 65638:
        addMeta("DeepView DoF", data);
        break;
      case 65639:
        addMeta("DeepView EDoF", data);
        break;
      case 65643:
        addMeta("DeepView Slider Name", data);
        break;
      case 65655:
        addMeta("DeepView Slider Name", data);
        break;
      case 5439491:
        addMeta("Acquisition Sofware", data);
        break;
      case 16777488:
        addMeta("Excitation Wavelength", data);
        break;
      case 16777489:
        addMeta("Emission Wavelength", data);
        break;
      case 101515267:
        addMeta("File Name", data);
        break;
      case 101253123:
      case 101777411:
        addMeta("Image Name", data);
        break;
      default:
        addMeta("" + tagID, data);
    }
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new ZeissZVIReader().testRead(args);
  }

}
