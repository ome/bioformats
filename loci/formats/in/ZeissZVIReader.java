//
// ZeissZVIReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden, Chris Allan
and Eric Kjellman.

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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import loci.formats.*;

/**
 * ZeissZVIReader is the file format reader for Zeiss ZVI files.
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class ZeissZVIReader extends FormatReader {

  // -- Constants --

  private static final String NO_POI_MSG =
    "You need to install Jakarta POI from http://jakarta.apache.org/poi/";

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
    if (noPOI || needLegacy) return legacy.openBytes(id, no);
    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    try {
      Object directory = pixels.get(new Integer(no));
      String name = (String) names.get(new Integer(no));

      r.setVar("dir", directory);
      r.setVar("entryName", name);
      r.exec("document = dir.getEntry(entryName)");
      r.exec("dis = new DocumentInputStream(document)");
      r.exec("numBytes = dis.available()");
      int numbytes = ((Integer) r.getVar("numBytes")).intValue();
      byte[] b = new byte[numbytes + 4]; // append 0 for final offset
      r.setVar("data", b);
      r.exec("dis.read(data)");

      // remove extra bytes

      int offset = ((Integer) offsets.get(new Integer(no))).intValue();
      byte[] a = new byte[b.length - offset];
      System.arraycopy(b, b.length - a.length, a, 0, a.length);

      if (bpp > 6) bpp = 1;

      if (bpp == 3) {
        // reverse bytes in groups of 3 to account for BGR storage

        byte[] tmp = a;
        a = new byte[tmp.length];

        for (int i=0; i<tmp.length; i+=3) {
          if (i + 2 < tmp.length) {
            a[i] = tmp[i + 2];
            a[i + 1] = tmp[i + 1];
            a[i + 2] = tmp[i];
          }
        }
      }
      b = null;

      if (a.length != bpp * sizeX[0] * sizeY[0]) {
        byte[] tmp = a;
        a = new byte[bpp * sizeX[0] * sizeY[0]];
        System.arraycopy(tmp, 0, a, 0, a.length);
      }
      return a;
    }
    catch (ReflectException e) {
      needLegacy = true;
      return openBytes(id, no);
    }
  }

  /** Obtains the specified image from the given ZVI file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (noPOI || needLegacy) return legacy.openImage(id, no);
    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    return ImageTools.makeImage(openBytes(id, no), width, height,
      isRGB(id) ? 3 : 1, true, bpp == 3 ? 1 : bpp, true, validBits);
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
    if (debug) debug("initFile(" + id + ")");

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

      String s = (String) metadata.get("Acquisition Bit Depth");
      if (s != null && s.trim().length() > 0) {
        validBits = new int[nChannels];
        if (nChannels == 2) validBits = new int[3];
        for (int i=0; i<nChannels; i++) {
          validBits[i] = Integer.parseInt(s.trim());
        }
      }
      else validBits = null;

      Object check = metadata.get("Image Channel Index");
      if (check != null && !check.toString().trim().equals("")) {
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
        if (sizeZ[0] == sizeC[0] && sizeC[0] == sizeT[0]) {
          currentOrder[0] = legacy.getDimensionOrder(id);
        }
      }
      else if (metadata.get("MultiChannel Color") != null) {
        currentOrder[0] = (zSize > tSize) ? "XYCZT" : "XYCTZ";
      }
      else currentOrder[0] = (zSize > tSize) ? "XYZTC" : "XYTZC";
    }
    catch (Throwable t) {
      // CTR TODO - eliminate catch-all exception handling
      needLegacy = true;
      if (debug) t.printStackTrace();
      initFile(id);
    }

    try {
      initMetadata();
    }
    catch (Exception e) {
      // CTR TODO - eliminate catch-all exception handling
      if (debug) e.printStackTrace();
    }
  }

  // -- Helper methods --

  /** Initialize metadata hashtable and OME-XML structure. */
  private void initMetadata() throws FormatException, IOException {
    MetadataStore store = getMetadataStore(currentId);
    store.setImage((String) metadata.get("File Name"), null, null, null);

    if (bpp == 1 || bpp == 3) pixelType[0] = FormatReader.UINT8;
    else if (bpp == 2 || bpp == 6) pixelType[0] = FormatReader.UINT16;

    store.setPixels(
      new Integer(getSizeX(currentId)),
      new Integer(getSizeY(currentId)),
      new Integer(getSizeZ(currentId)),
      new Integer(getSizeC(currentId)),
      new Integer(getSizeT(currentId)),
      new Integer(pixelType[0]),
      new Boolean(false),
      getDimensionOrder(currentId),
      null);

    store.setDimensions(
      new Float((String) metadata.get("Scale Factor for X")),
      new Float((String) metadata.get("Scale Factor for Y")),
      new Float((String) metadata.get("Scale Factor for Z")),
      null, null, null);
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
                  int len = DataTools.bytesToInt(data, pt, 2, true);
                  pt += 2;
                  value = new String(data, pt - 2, len + 2);
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
                int len = DataTools.bytesToInt(data, pt, 2, true);
                pt += 2;
                try {
                  value = new String(data, pt - 2, len + 2);
                  pt += len;
                }
                catch (Exception e) {
                  // CTR TODO - eliminate catch-all exception handling
                  if (debug) e.printStackTrace();
                  return;
                }
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
                try {
                  value = new String(data, oldPt - 2, pt - oldPt + 2);
                }
                catch (Exception e) {
                  // CTR TODO - eliminate catch-all exception handling
                  if (debug) e.printStackTrace();
                  return;
                }
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
          try {
            String typeDescription = new String(data, pt, len);
            pt += len;
          }
          catch (Exception e) {
            // CTR TODO - eliminate catch-all exception handling
            if (debug) e.printStackTrace();
            break;
          }

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
          try {
            while ((!foundWidth || !foundHeight) && pt < data.length) {
              pt++;
              foundWidth = DataTools.bytesToInt(data, pt, 4, true) == width;
              foundHeight =
                DataTools.bytesToInt(data, pt + 4, 4, true) == height;
            }
          }
          catch (Exception e) {
            // CTR TODO - eliminate catch-all exception handling
            if (debug) e.printStackTrace();
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
        metadata.put("Compression", data);
        break;
      case 258:
        metadata.put("BlackValue", data);
        break;
      case 259:
        metadata.put("WhiteValue", data);
        break;
      case 260:
        metadata.put("ImageDataMappingAutoRange", data);
        break;
      case 261:
        metadata.put("Thumbnail", data);
        break;
      case 262:
        metadata.put("GammaValue", data);
        break;
      case 264:
        metadata.put("ImageOverExposure", data);
        break;
      case 265:
        metadata.put("ImageRelativeTime1", data);
        break;
      case 266:
        metadata.put("ImageRelativeTime2", data);
        break;
      case 267:
        metadata.put("ImageRelativeTime3", data);
        break;
      case 268:
        metadata.put("ImageRelativeTime4", data);
        break;
      case 333:
        metadata.put("RelFocusPosition1", data);
        break;
      case 334:
        metadata.put("RelFocusPosition2", data);
        break;
      case 513:
        metadata.put("tagID_513", data);
        break;
      case 515:
        metadata.put("ImageWidth", data);
        break;
      case 516:
        metadata.put("ImageHeight", data);
        break;
      case 517:
        metadata.put("tagID_517", data);
        break;
      case 518:
        metadata.put("PixelType", data);
        break;
      case 519:
        metadata.put("NumberOfRawImages", data);
        break;
      case 520:
        metadata.put("ImageSize", data);
        break;
      case 523:
        metadata.put("Acquisition pause annotation", data);
        break;
      case 530:
        metadata.put("Document Subtype", data);
        break;
      case 531:
        metadata.put("Acquisition Bit Depth", data);
        break;
      case 532:
        metadata.put("Image Memory Usage (RAM)", data);
        break;
      case 534:
        metadata.put("Z-Stack single representative", data);
        break;
      case 769:
        metadata.put("Scale Factor for X", data);
        break;
      case 770:
        metadata.put("Scale Unit for X", data);
        break;
      case 771:
        metadata.put("Scale Width", data);
        break;
      case 772:
        metadata.put("Scale Factor for Y", data);
        break;
      case 773:
        metadata.put("Scale Unit for Y", data);
        break;
      case 774:
        metadata.put("Scale Height", data);
        break;
      case 775:
        metadata.put("Scale Factor for Z", data);
        break;
      case 776:
        metadata.put("Scale Unit for Z", data);
        break;
      case 777:
        metadata.put("Scale Depth", data);
        break;
      case 778:
        metadata.put("Scaling Parent", data);
        break;
      case 1001:
        metadata.put("Date", data);
        break;
      case 1002:
        metadata.put("code", data);
        break;
      case 1003:
        metadata.put("Source", data);
        break;
      case 1004:
        metadata.put("Message", data);
        break;
      case 1025:
        metadata.put("Acquisition Date", data);
        break;
      case 1026:
        metadata.put("8-bit acquisition", data);
        break;
      case 1027:
        metadata.put("Camera Bit Depth", data);
        break;
      case 1029:
        metadata.put("MonoReferenceLow", data);
        break;
      case 1030:
        metadata.put("MonoReferenceHigh", data);
        break;
      case 1031:
        metadata.put("RedReferenceLow", data);
        break;
      case 1032:
        metadata.put("RedReferenceHigh", data);
        break;
      case 1033:
        metadata.put("GreenReferenceLow", data);
        break;
      case 1034:
        metadata.put("GreenReferenceHigh", data);
        break;
      case 1035:
        metadata.put("BlueReferenceLow", data);
        break;
      case 1036:
        metadata.put("BlueReferenceHigh", data);
        break;
      case 1041:
        metadata.put("FrameGrabber Name", data);
        break;
      case 1042:
        metadata.put("Camera", data);
        break;
      case 1044:
        metadata.put("CameraTriggerSignalType", data);
        break;
      case 1045:
        metadata.put("CameraTriggerEnable", data);
        break;
      case 1046:
        metadata.put("GrabberTimeout", data);
        break;
      case 1281:
        metadata.put("MultiChannelEnabled", data);
        break;
      case 1282:
        metadata.put("MultiChannel Color", data);
        break;
      case 1283:
        metadata.put("MultiChannel Weight", data);
        break;
      case 1284:
        metadata.put("Channel Name", data);
        break;
      case 1536:
        metadata.put("DocumentInformationGroup", data);
        break;
      case 1537:
        metadata.put("Title", data);
        break;
      case 1538:
        metadata.put("Author", data);
        break;
      case 1539:
        metadata.put("Keywords", data);
        break;
      case 1540:
        metadata.put("Comments", data);
        break;
      case 1541:
        metadata.put("SampleID", data);
        break;
      case 1542:
        metadata.put("Subject", data);
        break;
      case 1543:
        metadata.put("RevisionNumber", data);
        break;
      case 1544:
        metadata.put("Save Folder", data);
        break;
      case 1545:
        metadata.put("FileLink", data);
        break;
      case 1546:
        metadata.put("Document Type", data);
        break;
      case 1547:
        metadata.put("Storage Media", data);
        break;
      case 1548:
        metadata.put("File ID", data);
        break;
      case 1549:
        metadata.put("Reference", data);
        break;
      case 1550:
        metadata.put("File Date", data);
        break;
      case 1551:
        metadata.put("File Size", data);
        break;
      case 1553:
        metadata.put("Filename", data);
        break;
      case 1792:
        metadata.put("ProjectGroup", data);
        break;
      case 1793:
        metadata.put("Acquisition Date", data);
        break;
      case 1794:
        metadata.put("Last modified by", data);
        break;
      case 1795:
        metadata.put("User company", data);
        break;
      case 1796:
        metadata.put("User company logo", data);
        break;
      case 1797:
        metadata.put("Image", data);
        break;
      case 1800:
        metadata.put("User ID", data);
        break;
      case 1801:
        metadata.put("User Name", data);
        break;
      case 1802:
        metadata.put("User City", data);
        break;
      case 1803:
        metadata.put("User Address", data);
        break;
      case 1804:
        metadata.put("User Country", data);
        break;
      case 1805:
        metadata.put("User Phone", data);
        break;
      case 1806:
        metadata.put("User Fax", data);
        break;
      case 2049:
        metadata.put("Objective Name", data);
        break;
      case 2050:
        metadata.put("Optovar", data);
        break;
      case 2051:
        metadata.put("Reflector", data);
        break;
      case 2052:
        metadata.put("Condenser Contrast", data);
        break;
      case 2053:
        metadata.put("Transmitted Light Filter 1", data);
        break;
      case 2054:
        metadata.put("Transmitted Light Filter 2", data);
        break;
      case 2055:
        metadata.put("Reflected Light Shutter", data);
        break;
      case 2056:
        metadata.put("Condenser Front Lens", data);
        break;
      case 2057:
        metadata.put("Excitation Filter Name", data);
        break;
      case 2060:
        metadata.put("Transmitted Light Fieldstop Aperture", data);
        break;
      case 2061:
        metadata.put("Reflected Light Aperture", data);
        break;
      case 2062:
        metadata.put("Condenser N.A.", data);
        break;
      case 2063:
        metadata.put("Light Path", data);
        break;
      case 2064:
        metadata.put("HalogenLampOn", data);
        break;
      case 2065:
        metadata.put("Halogen Lamp Mode", data);
        break;
      case 2066:
        metadata.put("Halogen Lamp Voltage", data);
        break;
      case 2068:
        metadata.put("Fluorescence Lamp Level", data);
        break;
      case 2069:
        metadata.put("Fluorescence Lamp Intensity", data);
        break;
      case 2070:
        metadata.put("LightManagerEnabled", data);
        break;
      case 2071:
        metadata.put("tag_ID_2071", data);
        break;
      case 2072:
        metadata.put("Focus Position", data);
        break;
      case 2073:
        metadata.put("Stage Position X", data);
        break;
      case 2074:
        metadata.put("Stage Position Y", data);
        break;
      case 2075:
        metadata.put("Microscope Name", data);
        break;
      case 2076:
        metadata.put("Objective Magnification", data);
        break;
      case 2077:
        metadata.put("Objective N.A.", data);
        break;
      case 2078:
        metadata.put("MicroscopeIllumination", data);
        break;
      case 2079:
        metadata.put("External Shutter 1", data);
        break;
      case 2080:
        metadata.put("External Shutter 2", data);
        break;
      case 2081:
        metadata.put("External Shutter 3", data);
        break;
      case 2082:
        metadata.put("External Filter Wheel 1 Name", data);
        break;
      case 2083:
        metadata.put("External Filter Wheel 2 Name", data);
        break;
      case 2084:
        metadata.put("Parfocal Correction", data);
        break;
      case 2086:
        metadata.put("External Shutter 4", data);
        break;
      case 2087:
        metadata.put("External Shutter 5", data);
        break;
      case 2088:
        metadata.put("External Shutter 6", data);
        break;
      case 2089:
        metadata.put("External Filter Wheel 3 Name", data);
        break;
      case 2090:
        metadata.put("External Filter Wheel 4 Name", data);
        break;
      case 2103:
        metadata.put("Objective Turret Position", data);
        break;
      case 2104:
        metadata.put("Objective Contrast Method", data);
        break;
      case 2105:
        metadata.put("Objective Immersion Type", data);
        break;
      case 2107:
        metadata.put("Reflector Position", data);
        break;
      case 2109:
        metadata.put("Transmitted Light Filter 1 Position", data);
        break;
      case 2110:
        metadata.put("Transmitted Light Filter 2 Position", data);
        break;
      case 2112:
        metadata.put("Excitation Filter Position", data);
        break;
      case 2113:
        metadata.put("Lamp Mirror Position", data);
        break;
      case 2114:
        metadata.put("External Filter Wheel 1 Position", data);
        break;
      case 2115:
        metadata.put("External Filter Wheel 2 Position", data);
        break;
      case 2116:
        metadata.put("External Filter Wheel 3 Position", data);
        break;
      case 2117:
        metadata.put("External Filter Wheel 4 Position", data);
        break;
      case 2118:
        metadata.put("Lightmanager Mode", data);
        break;
      case 2119:
        metadata.put("Halogen Lamp Calibration", data);
        break;
      case 2120:
        metadata.put("CondenserNAGoSpeed", data);
        break;
      case 2121:
        metadata.put("TransmittedLightFieldstopGoSpeed", data);
        break;
      case 2122:
        metadata.put("OptovarGoSpeed", data);
        break;
      case 2123:
        metadata.put("Focus calibrated", data);
        break;
      case 2124:
        metadata.put("FocusBasicPosition", data);
        break;
      case 2125:
        metadata.put("FocusPower", data);
        break;
      case 2126:
        metadata.put("FocusBacklash", data);
        break;
      case 2127:
        metadata.put("FocusMeasurementOrigin", data);
        break;
      case 2128:
        metadata.put("FocusMeasurementDistance", data);
        break;
      case 2129:
        metadata.put("FocusSpeed", data);
        break;
      case 2130:
        metadata.put("FocusGoSpeed", data);
        break;
      case 2131:
        metadata.put("FocusDistance", data);
        break;
      case 2132:
        metadata.put("FocusInitPosition", data);
        break;
      case 2133:
        metadata.put("Stage calibrated", data);
        break;
      case 2134:
        metadata.put("StagePower", data);
        break;
      case 2135:
        metadata.put("StageXBacklash", data);
        break;
      case 2136:
        metadata.put("StageYBacklash", data);
        break;
      case 2137:
        metadata.put("StageSpeedX", data);
        break;
      case 2138:
        metadata.put("StageSpeedY", data);
        break;
      case 2139:
        metadata.put("StageSpeed", data);
        break;
      case 2140:
        metadata.put("StageGoSpeedX", data);
        break;
      case 2141:
        metadata.put("StageGoSpeedY", data);
        break;
      case 2142:
        metadata.put("StageStepDistanceX", data);
        break;
      case 2143:
        metadata.put("StageStepDistanceY", data);
        break;
      case 2144:
        metadata.put("StageInitialisationPositionX", data);
        break;
      case 2145:
        metadata.put("StageInitialisationPositionY", data);
        break;
      case 2146:
        metadata.put("MicroscopeMagnification", data);
        break;
      case 2147:
        metadata.put("ReflectorMagnification", data);
        break;
      case 2148:
        metadata.put("LampMirrorPosition", data);
        break;
      case 2149:
        metadata.put("FocusDepth", data);
        break;
      case 2150:
        metadata.put("MicroscopeType", data);
        break;
      case 2151:
        metadata.put("Objective Working Distance", data);
        break;
      case 2152:
        metadata.put("ReflectedLightApertureGoSpeed", data);
        break;
      case 2153:
        metadata.put("External Shutter", data);
        break;
      case 2154:
        metadata.put("ObjectiveImmersionStop", data);
        break;
      case 2155:
        metadata.put("Focus Start Speed", data);
        break;
      case 2156:
        metadata.put("Focus Acceleration", data);
        break;
      case 2157:
        metadata.put("ReflectedLightFieldstop", data);
        break;
      case 2158:
        metadata.put("ReflectedLightFieldstopGoSpeed", data);
        break;
      case 2159:
        metadata.put("ReflectedLightFilter 1", data);
        break;
      case 2160:
        metadata.put("ReflectedLightFilter 2", data);
        break;
      case 2161:
        metadata.put("ReflectedLightFilter1Position", data);
        break;
      case 2162:
        metadata.put("ReflectedLightFilter2Position", data);
        break;
      case 2163:
        metadata.put("TransmittedLightAttenuator", data);
        break;
      case 2164:
        metadata.put("ReflectedLightAttenuator", data);
        break;
      case 2165:
        metadata.put("Transmitted Light Shutter", data);
        break;
      case 2166:
        metadata.put("TransmittedLightAttenuatorGoSpeed", data);
        break;
      case 2167:
        metadata.put("ReflectedLightAttenuatorGoSpeed", data);
        break;
      case 2176:
        metadata.put("TransmittedLightVirtualFilterPosition", data);
        break;
      case 2177:
        metadata.put("TransmittedLightVirtualFilter", data);
        break;
      case 2178:
        metadata.put("ReflectedLightVirtualFilterPosition", data);
        break;
      case 2179:
        metadata.put("ReflectedLightVirtualFilter", data);
        break;
      case 2180:
        metadata.put("ReflectedLightHalogenLampMode", data);
        break;
      case 2181:
        metadata.put("ReflectedLightHalogenLampVoltage", data);
        break;
      case 2182:
        metadata.put("ReflectedLightHalogenLampColorTemperature", data);
        break;
      case 2183:
        metadata.put("ContrastManagerMode", data);
        break;
      case 2184:
        metadata.put("Dazzle Protection Active", data);
        break;
      case 2195:
        metadata.put("Zoom", data);
        break;
      case 2196:
        metadata.put("ZoomGoSpeed", data);
        break;
      case 2197:
        metadata.put("LightZoom", data);
        break;
      case 2198:
        metadata.put("LightZoomGoSpeed", data);
        break;
      case 2199:
        metadata.put("LightZoomCoupled", data);
        break;
      case 2200:
        metadata.put("TransmittedLightHalogenLampMode", data);
        break;
      case 2201:
        metadata.put("TransmittedLightHalogenLampVoltage", data);
        break;
      case 2202:
        metadata.put("TransmittedLightHalogenLampColorTemperature", data);
        break;
      case 2203:
        metadata.put("Reflected Coldlight Mode", data);
        break;
      case 2204:
        metadata.put("Reflected Coldlight Intensity", data);
        break;
      case 2205:
        metadata.put("Reflected Coldlight Color Temperature", data);
        break;
      case 2206:
        metadata.put("Transmitted Coldlight Mode", data);
        break;
      case 2207:
        metadata.put("Transmitted Coldlight Intensity", data);
        break;
      case 2208:
        metadata.put("Transmitted Coldlight Color Temperature", data);
        break;
      case 2209:
        metadata.put("Infinityspace Portchanger Position", data);
        break;
      case 2210:
        metadata.put("Beamsplitter Infinity Space", data);
        break;
      case 2211:
        metadata.put("TwoTv VisCamChanger Position", data);
        break;
      case 2212:
        metadata.put("Beamsplitter Ocular", data);
        break;
      case 2213:
        metadata.put("TwoTv CamerasChanger Position", data);
        break;
      case 2214:
        metadata.put("Beamsplitter Cameras", data);
        break;
      case 2215:
        metadata.put("Ocular Shutter", data);
        break;
      case 2216:
        metadata.put("TwoTv CamerasChangerCube", data);
        break;
      case 2218:
        metadata.put("Ocular Magnification", data);
        break;
      case 2219:
        metadata.put("Camera Adapter Magnification", data);
        break;
      case 2220:
        metadata.put("Microscope Port", data);
        break;
      case 2221:
        metadata.put("Ocular Total Magnification", data);
        break;
      case 2222:
        metadata.put("Field of View", data);
        break;
      case 2223:
        metadata.put("Ocular", data);
        break;
      case 2224:
        metadata.put("CameraAdapter", data);
        break;
      case 2225:
        metadata.put("StageJoystickEnabled", data);
        break;
      case 2226:
        metadata.put("ContrastManager Contrast Method", data);
        break;
      case 2229:
        metadata.put("CamerasChanger Beamsplitter Type", data);
        break;
      case 2235:
        metadata.put("Rearport Slider Position", data);
        break;
      case 2236:
        metadata.put("Rearport Source", data);
        break;
      case 2237:
        metadata.put("Beamsplitter Type Infinity Space", data);
        break;
      case 2238:
        metadata.put("Fluorescence Attenuator", data);
        break;
      case 2239:
        metadata.put("Fluorescence Attenuator Position", data);
        break;
      case 2261:
        metadata.put("Objective ID", data);
        break;
      case 2262:
        metadata.put("Reflector ID", data);
        break;
      case 2307:
        metadata.put("Camera Framestart Left", data);
        break;
      case 2308:
        metadata.put("Camera Framestart Top", data);
        break;
      case 2309:
        metadata.put("Camera Frame Width", data);
        break;
      case 2310:
        metadata.put("Camera Frame Height", data);
        break;
      case 2311:
        metadata.put("Camera Binning", data);
        break;
      case 2312:
        metadata.put("CameraFrameFull", data);
        break;
      case 2313:
        metadata.put("CameraFramePixelDistance", data);
        break;
      case 2318:
        metadata.put("DataFormatUseScaling", data);
        break;
      case 2319:
        metadata.put("CameraFrameImageOrientation", data);
        break;
      case 2320:
        metadata.put("VideoMonochromeSignalType", data);
        break;
      case 2321:
        metadata.put("VideoColorSignalType", data);
        break;
      case 2322:
        metadata.put("MeteorChannelInput", data);
        break;
      case 2323:
        metadata.put("MeteorChannelSync", data);
        break;
      case 2324:
        metadata.put("WhiteBalanceEnabled", data);
        break;
      case 2325:
        metadata.put("CameraWhiteBalanceRed", data);
        break;
      case 2326:
        metadata.put("CameraWhiteBalanceGreen", data);
        break;
      case 2327:
        metadata.put("CameraWhiteBalanceBlue", data);
        break;
      case 2331:
        metadata.put("CameraFrameScalingFactor", data);
        break;
      case 2562:
        metadata.put("Meteor Camera Type", data);
        break;
      case 2564:
        metadata.put("Exposure Time [ms]", data);
        break;
      case 2568:
        metadata.put("CameraExposureTimeAutoCalculate", data);
        break;
      case 2569:
        metadata.put("Meteor Gain Value", data);
        break;
      case 2571:
        metadata.put("Meteor Gain Automatic", data);
        break;
      case 2572:
        metadata.put("MeteorAdjustHue", data);
        break;
      case 2573:
        metadata.put("MeteorAdjustSaturation", data);
        break;
      case 2574:
        metadata.put("MeteorAdjustRedLow", data);
        break;
      case 2575:
        metadata.put("MeteorAdjustGreenLow", data);
        break;
      case 2576:
        metadata.put("Meteor Blue Low", data);
        break;
      case 2577:
        metadata.put("MeteorAdjustRedHigh", data);
        break;
      case 2578:
        metadata.put("MeteorAdjustGreenHigh", data);
        break;
      case 2579:
        metadata.put("MeteorBlue High", data);
        break;
      case 2582:
        metadata.put("CameraExposureTimeCalculationControl", data);
        break;
      case 2585:
        metadata.put("AxioCamFadingCorrectionEnable", data);
        break;
      case 2587:
        metadata.put("CameraLiveImage", data);
        break;
      case 2588:
        metadata.put("CameraLiveEnabled", data);
        break;
      case 2589:
        metadata.put("LiveImageSyncObjectName", data);
        break;
      case 2590:
        metadata.put("CameraLiveSpeed", data);
        break;
      case 2591:
        metadata.put("CameraImage", data);
        break;
      case 2592:
        metadata.put("CameraImageWidth", data);
        break;
      case 2593:
        metadata.put("CameraImageHeight", data);
        break;
      case 2594:
        metadata.put("CameraImagePixelType", data);
        break;
      case 2595:
        metadata.put("CameraImageShMemoryName", data);
        break;
      case 2596:
        metadata.put("CameraLiveImageWidth", data);
        break;
      case 2597:
        metadata.put("CameraLiveImageHeight", data);
        break;
      case 2598:
        metadata.put("CameraLiveImagePixelType", data);
        break;
      case 2599:
        metadata.put("CameraLiveImageShMemoryName", data);
        break;
      case 2600:
        metadata.put("CameraLiveMaximumSpeed", data);
        break;
      case 2601:
        metadata.put("CameraLiveBinning", data);
        break;
      case 2602:
        metadata.put("CameraLiveGainValue", data);
        break;
      case 2603:
        metadata.put("CameraLiveExposureTimeValue", data);
        break;
      case 2604:
        metadata.put("CameraLiveScalingFactor", data);
        break;
      case 2819:
        metadata.put("Image Index Z", data);
        break;
      case 2820:
        metadata.put("Image Channel Index", data);
        break;
      case 2821:
        metadata.put("Image Index T", data);
        break;
      case 2822:
        metadata.put("ImageTile Index", data);
        break;
      case 2823:
        metadata.put("Image acquisition Index", data);
        break;
      case 2827:
        metadata.put("Image IndexS", data);
        break;
      case 2841:
        metadata.put("Original Stage Position X", data);
        break;
      case 2842:
        metadata.put("Original Stage Position Y", data);
        break;
      case 3088:
        metadata.put("LayerDrawFlags", data);
        break;
      case 3334:
        metadata.put("RemainingTime", data);
        break;
      case 3585:
        metadata.put("User Field 1", data);
        break;
      case 3586:
        metadata.put("User Field 2", data);
        break;
      case 3587:
        metadata.put("User Field 3", data);
        break;
      case 3588:
        metadata.put("User Field 4", data);
        break;
      case 3589:
        metadata.put("User Field 5", data);
        break;
      case 3590:
        metadata.put("User Field 6", data);
        break;
      case 3591:
        metadata.put("User Field 7", data);
        break;
      case 3592:
        metadata.put("User Field 8", data);
        break;
      case 3593:
        metadata.put("User Field 9", data);
        break;
      case 3594:
        metadata.put("User Field 10", data);
        break;
      case 3840:
        metadata.put("ID", data);
        break;
      case 3841:
        metadata.put("Name", data);
        break;
      case 3842:
        metadata.put("Value", data);
        break;
      case 5501:
        metadata.put("PvCamClockingMode", data);
        break;
      case 8193:
        metadata.put("Autofocus Status Report", data);
        break;
      case 8194:
        metadata.put("Autofocus Position", data);
        break;
      case 8195:
        metadata.put("Autofocus Position Offset", data);
        break;
      case 8196:
        metadata.put("Autofocus Empty Field Threshold", data);
        break;
      case 8197:
        metadata.put("Autofocus Calibration Name", data);
        break;
      case 8198:
        metadata.put("Autofocus Current Calibration Item", data);
        break;
      case 20478:
        metadata.put("tag_ID_20478", data);
        break;
      case 65537:
        metadata.put("CameraFrameFullWidth", data);
        break;
      case 65538:
        metadata.put("CameraFrameFullHeight", data);
        break;
      case 65541:
        metadata.put("AxioCam Shutter Signal", data);
        break;
      case 65542:
        metadata.put("AxioCam Delay Time", data);
        break;
      case 65543:
        metadata.put("AxioCam Shutter Control", data);
        break;
      case 65544:
        metadata.put("AxioCam BlackRefIsCalculated", data);
        break;
      case 65545:
        metadata.put("AxioCam Black Reference", data);
        break;
      case 65547:
        metadata.put("Camera Shading Correction", data);
        break;
      case 65550:
        metadata.put("AxioCam Enhance Color", data);
        break;
      case 65551:
        metadata.put("AxioCam NIR Mode", data);
        break;
      case 65552:
        metadata.put("CameraShutterCloseDelay", data);
        break;
      case 65553:
        metadata.put("CameraWhiteBalanceAutoCalculate", data);
        break;
      case 65556:
        metadata.put("AxioCam NIR Mode Available", data);
        break;
      case 65557:
        metadata.put("AxioCam Fading Correction Available", data);
        break;
      case 65559:
        metadata.put("AxioCam Enhance Color Available", data);
        break;
      case 65565:
        metadata.put("MeteorVideoNorm", data);
        break;
      case 65566:
        metadata.put("MeteorAdjustWhiteReference", data);
        break;
      case 65567:
        metadata.put("MeteorBlackReference", data);
        break;
      case 65568:
        metadata.put("MeteorChannelInputCountMono", data);
        break;
      case 65570:
        metadata.put("MeteorChannelInputCountRGB", data);
        break;
      case 65571:
        metadata.put("MeteorEnableVCR", data);
        break;
      case 65572:
        metadata.put("Meteor Brightness", data);
        break;
      case 65573:
        metadata.put("Meteor Contrast", data);
        break;
      case 65575:
        metadata.put("AxioCam Selector", data);
        break;
      case 65576:
        metadata.put("AxioCam Type", data);
        break;
      case 65577:
        metadata.put("AxioCam Info", data);
        break;
      case 65580:
        metadata.put("AxioCam Resolution", data);
        break;
      case 65581:
        metadata.put("AxioCam Color Model", data);
        break;
      case 65582:
        metadata.put("AxioCam MicroScanning", data);
        break;
      case 65585:
        metadata.put("Amplification Index", data);
        break;
      case 65586:
        metadata.put("Device Command", data);
        break;
      case 65587:
        metadata.put("BeamLocation", data);
        break;
      case 65588:
        metadata.put("ComponentType", data);
        break;
      case 65589:
        metadata.put("ControllerType", data);
        break;
      case 65590:
        metadata.put("CameraWhiteBalanceCalculationRedPaint", data);
        break;
      case 65591:
        metadata.put("CameraWhiteBalanceCalculationBluePaint", data);
        break;
      case 65592:
        metadata.put("CameraWhiteBalanceSetRed", data);
        break;
      case 65593:
        metadata.put("CameraWhiteBalanceSetGreen", data);
        break;
      case 65594:
        metadata.put("CameraWhiteBalanceSetBlue", data);
        break;
      case 65595:
        metadata.put("CameraWhiteBalanceSetTargetRed", data);
        break;
      case 65596:
        metadata.put("CameraWhiteBalanceSetTargetGreen", data);
        break;
      case 65597:
        metadata.put("CameraWhiteBalanceSetTargetBlue", data);
        break;
      case 65598:
        metadata.put("ApotomeCamCalibrationMode", data);
        break;
      case 65599:
        metadata.put("ApoTome Grid Position", data);
        break;
      case 65600:
        metadata.put("ApotomeCamScannerPosition", data);
        break;
      case 65601:
        metadata.put("ApoTome Full Phase Shift", data);
        break;
      case 65602:
        metadata.put("ApoTome Grid Name", data);
        break;
      case 65603:
        metadata.put("ApoTome Staining", data);
        break;
      case 65604:
        metadata.put("ApoTome Processing Mode", data);
        break;
      case 65605:
        metadata.put("ApotmeCamLiveCombineMode", data);
        break;
      case 65606:
        metadata.put("ApoTome Filter Name", data);
        break;
      case 65607:
        metadata.put("Apotome Filter Strength", data);
        break;
      case 65608:
        metadata.put("ApotomeCamFilterHarmonics", data);
        break;
      case 65609:
        metadata.put("ApoTome Grating Period", data);
        break;
      case 65610:
        metadata.put("ApoTome Auto Shutter Used", data);
        break;
      case 65611:
        metadata.put("Apotome Cam Status", data);
        break;
      case 65612:
        metadata.put("ApotomeCamNormalize", data);
        break;
      case 65613:
        metadata.put("ApotomeCamSettingsManager", data);
        break;
      case 65614:
        metadata.put("DeepviewCamSupervisorMode", data);
        break;
      case 65615:
        metadata.put("DeepView Processing", data);
        break;
      case 65616:
        metadata.put("DeepviewCamFilterName", data);
        break;
      case 65617:
        metadata.put("DeepviewCamStatus", data);
        break;
      case 65618:
        metadata.put("DeepviewCamSettingsManager", data);
        break;
      case 65619:
        metadata.put("DeviceScalingName", data);
        break;
      case 65620:
        metadata.put("CameraShadingIsCalculated", data);
        break;
      case 65621:
        metadata.put("CameraShadingCalculationName", data);
        break;
      case 65622:
        metadata.put("CameraShadingAutoCalculate", data);
        break;
      case 65623:
        metadata.put("CameraTriggerAvailable", data);
        break;
      case 65626:
        metadata.put("CameraShutterAvailable", data);
        break;
      case 65627:
        metadata.put("AxioCam ShutterMicroScanningEnable", data);
        break;
      case 65628:
        metadata.put("ApotomeCamLiveFocus", data);
        break;
      case 65629:
        metadata.put("DeviceInitStatus", data);
        break;
      case 65630:
        metadata.put("DeviceErrorStatus", data);
        break;
      case 65631:
        metadata.put("ApotomeCamSliderInGridPosition", data);
        break;
      case 65632:
        metadata.put("Orca NIR Mode Used", data);
        break;
      case 65633:
        metadata.put("Orca Analog Gain", data);
        break;
      case 65634:
        metadata.put("Orca Analog Offset", data);
        break;
      case 65635:
        metadata.put("Orca Binning", data);
        break;
      case 65636:
        metadata.put("Orca Bit Depth", data);
        break;
      case 65637:
        metadata.put("ApoTome Averaging Count", data);
        break;
      case 65638:
        metadata.put("DeepView DoF", data);
        break;
      case 65639:
        metadata.put("DeepView EDoF", data);
        break;
      case 65643:
        metadata.put("DeepView Slider Name", data);
        break;
      case 65655:
        metadata.put("DeepView Slider Name", data);
        break;
      case 5439491:
        metadata.put("Acquisition Sofware", data);
        break;
      case 16777488:
        metadata.put("Excitation Wavelength", data);
        break;
      case 16777489:
        metadata.put("Emission Wavelength", data);
        break;
      case 101515267:
        metadata.put("File Name", data);
        break;
      case 101253123:
      case 101777411:
        metadata.put("Image Name", data);
        break;
      default:
        metadata.put("" + tagID, data);
    }
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new ZeissZVIReader().testRead(args);
  }

}
