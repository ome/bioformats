//
// OIBReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan
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
import java.util.*;
import loci.formats.*;

/**
 * OIBReader is the file format reader for Fluoview FV1000 OIB files.
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class OIBReader extends FormatReader {

  // -- Constants --

  private static final String NO_POI_MSG = "You need to install Jakarta POI " +
    "from http://jakarta.apache.org/poi/";

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
      r.exec("import java.util.Iterator");
    }
    catch (Throwable exc) {
      noPOI = true;
    }
    return r;
  }

  // -- Fields --

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

  /* Number of Z slices. */
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

  private boolean littleEndian;

  // -- Constructor --

  /** Constructs a new OIB reader. */
  public OIBReader() { super("Fluoview FV1000 OIB", "oib"); }

  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for an OIB file. */
  public boolean isThisType(byte[] block) {
    return (block[0] == 0xd0 && block[1] == 0xcf &&
      block[2] == 0x11 && block[3] == 0xe0);
  }

  /** Determines the number of images in the given OIB file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return nImages;
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return nChannels > 1 && (nChannels * zSize * tSize != nImages);
  }

  /** Return true if the data is in little-endian format. */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    return false;
  }

  /** Returns whether or not the channels are interleaved. */
  public boolean isInterleaved(String id) throws FormatException, IOException {
    return true;
  }

  /* @see loci.formats.IFormatReader#getChannelGlobalMinimum(String, int) */
  public Double getChannelGlobalMinimum(String id, int theC)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    String s = (String) metadata.get("[Image Parameters] - DataMin");
    try { return new Double(s); }
    catch (NumberFormatException exc) { return null; }
  }

  /* @see loci.formats.IFormatReader#getChannelGlobalMaximum(String, int) */
  public Double getChannelGlobalMaximum(String id, int theC)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    String s = (String) metadata.get("[Image Parameters] - DataMax");
    try { return new Double(s); }
    catch (NumberFormatException exc) { return null; }
  }

  /** Obtains the specified image from the given ZVI file, as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    try {
      String directory = (String) pixels.get(new Integer(no));
      String name = (String) names.get(new Integer(no));

      r.setVar("dirName", directory);
      r.exec("root = fs.getRoot()");
      r.exec("dir = root.getEntry(dirName)");
      r.setVar("entryName", name);
      r.exec("document = dir.getEntry(entryName)");
      r.exec("dis = new DocumentInputStream(document)");
      r.exec("numBytes = dis.available()");
      int numbytes = ((Integer) r.getVar("numBytes")).intValue();
      byte[] b = new byte[numbytes + 4]; // append 0 for final offset
      r.setVar("data", b);
      r.exec("dis.read(data)");

      RandomAccessStream stream = new RandomAccessStream(b);
      Hashtable[] ifds = TiffTools.getIFDs(stream);
      littleEndian = TiffTools.isLittleEndian(ifds[0]);
      byte[][] samples = TiffTools.getSamples(ifds[0], stream);

      byte[] rtn = new byte[samples.length * samples[0].length];
      for (int i=0; i<samples.length; i++) {
        System.arraycopy(samples[i], 0, rtn, i*samples[i].length,
          samples[i].length);
      }
      stream.close();
      return rtn;
    }
    catch (ReflectException e) {
      noPOI = true;
      return new byte[0];
    }
  }

  /** Obtains the specified image from the given ZVI file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    byte[] b = openBytes(id, no);
    int bytes = b.length / (width * height);
    return ImageTools.makeImage(b, width, height, bytes == 3 ? 3 : 1,
      false, bytes == 3 ? 1 : bytes, !littleEndian);
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    currentId = null;

    String[] vars = {"dirName", "root", "dir", "document", "dis",
      "numBytes", "data", "fis", "fs", "iter", "isInstance", "isDocument",
      "entry", "documentName", "entryName"};
    for (int i=0; i<vars.length; i++) r.setVar(vars[i], null);
  }

  /** Initializes the given ZVI file. */
  protected void initFile(String id) throws FormatException, IOException {
    if (noPOI) throw new FormatException(NO_POI_MSG);
    currentId = id;

    metadata = new Hashtable();
    pixels = new Hashtable();
    names = new Hashtable();
    zIndices = new Vector();
    cIndices = new Vector();
    tIndices = new Vector();

    nImages = 0;

    try {
      RandomAccessStream ras = new RandomAccessStream(getMappedId(id));
      if (ras.length() % 4096 != 0) {
        ras.setExtend(4096 - (int) (ras.length() % 4096));
      }
      r.setVar("fis", ras);
      r.exec("fs = new POIFSFileSystem(fis)");
      r.exec("dir = fs.getRoot()");
      parseDir(0, r.getVar("dir"));

      // sort names

      Vector newKeys = new Vector();
      Vector comps = new Vector();
      Enumeration keys = names.keys();
      while (keys.hasMoreElements()) {
        Object key = keys.nextElement();
        String value = (String) names.get(key);
        int comp = Integer.parseInt(value.substring(value.indexOf("0")));
        int size = newKeys.size();
        for (int i=0; i<size; i++) {
          if (comp < ((Integer) comps.get(i)).intValue()) {
            newKeys.add(i, value);
            comps.add(i, new Integer(comp));
            i = size;
          }
          else if (i == size - 1) {
            newKeys.add(value);
            comps.add(new Integer(comp));
            i = size;
          }
        }
        if (newKeys.size() == 0) {
          newKeys.add(value);
          comps.add(new Integer(comp));
        }
      }

      Hashtable newNames = new Hashtable();
      for (int i=0; i<newKeys.size(); i++) {
        newNames.put(new Integer(i), newKeys.get(i));
      }
      names = newNames;

      String[] labels = new String[9];
      String[] dims = new String[9];

      for (int i=0; i<labels.length; i++) {
        labels[i] = (String)
          metadata.get("[Axis " + i + " Parameters Common] - AxisCode");
        dims[i] =
          (String) metadata.get("[Axis " + i + " Parameters Common] - MaxSize");
      }

      if (nChannels == 0) nChannels++;

      for (int i=0; i<labels.length; i++) {
        if (labels[i].equals("\"X\"")) width = Integer.parseInt(dims[i]);
        else if (labels[i].equals("\"Y\"")) height = Integer.parseInt(dims[i]);
        else if (labels[i].equals("\"C\"")) {
          nChannels = Integer.parseInt(dims[i]);
        }
        else if (labels[i].equals("\"Z\"")) zSize = Integer.parseInt(dims[i]);
        else if (labels[i].equals("\"T\"")) tSize = Integer.parseInt(dims[i]);
        else if (!dims[i].equals("0")) nChannels *= Integer.parseInt(dims[i]);
      }

      if (zSize == 0) zSize++;
      if (tSize == 0) tSize++;

      sizeX = new int[1];
      sizeY = new int[1];
      sizeZ = new int[1];
      sizeC = new int[1];
      sizeT = new int[1];
      pixelType = new int[1];
      currentOrder = new String[1];

      sizeX[0] = width;
      sizeY[0] = height;
      sizeZ[0] = zSize;
      sizeC[0] = nChannels;
      sizeT[0] = tSize;
      currentOrder[0] = (zSize > tSize) ? "XYCZT" : "XYCTZ";

      if (nImages == zSize * tSize * nChannels + 1) nImages--;

      if (nImages > zSize * tSize * nChannels) {
        int diff = nImages - (zSize * tSize * nChannels);

        if (diff % zSize == 0 && zSize > 1) {
          while (nImages > zSize * tSize * nChannels) tSize++;
        }
        else if (diff % tSize == 0 && tSize > 1) {
          while (nImages > zSize * tSize * nChannels) zSize++;
        }
        else if (diff % nChannels == 0) {
          if (zSize > tSize) {
            while (nImages > zSize * tSize * nChannels) zSize++;
          }
          else {
            while (nImages > zSize * tSize * nChannels) tSize++;
          }
        }
      }

      sizeZ[0] = zSize;
      sizeC[0] = nChannels;
      sizeT[0] = tSize;
    }
    catch (Throwable t) {
      noPOI = true;
      if (DEBUG) t.printStackTrace();
      initFile(id);
    }

    try { initMetadata(); }
    catch (Exception e) { }
  }

  // -- Helper methods --

  /** Initialize metadata hashtable and OME-XML structure. */
  private void initMetadata() throws FormatException, IOException {
    MetadataStore store = getMetadataStore(currentId);
    store.setImage((String) metadata.get("DataName"), null, null, null);

    switch (bpp % 3) {
      case 0:
      case 1:
        pixelType[0] = FormatReader.UINT8;
        break;
      case 2:
        pixelType[0] = FormatReader.UINT16;
        break;
      case 4:
        pixelType[0] = FormatReader.UINT32;
        break;
      default:
        throw new RuntimeException(
          "Unknown matching for pixel byte width of: " + bpp);
    }

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

    Float pixX = new Float(metadata.get(
      "[Reference Image Parameter] - WidthConvertValue").toString());
    Float pixY = new Float(metadata.get(
      "[Reference Image Parameter] - HeightConvertValue").toString());
    store.setDimensions(pixX, pixY, null, null, null, null);
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
        if (DEBUG) {
          print(depth + 1, "Found document: " + r.getVar("entryName"));
        }
        r.exec("dis = new DocumentInputStream(entry)");
        r.exec("numBytes = dis.available()");
        int numbytes = ((Integer) r.getVar("numBytes")).intValue();
        byte[] data = new byte[numbytes + 4]; // append 0 for final offset
        r.setVar("data", data);
        r.exec("dis.read(data)");

        String entryName = (String) r.getVar("entryName");
        String dirName = (String) r.getVar("dirName");

        boolean isContents = entryName.toUpperCase().equals("CONTENTS");
        Object directory = r.getVar("dir");

        int pt = 0;

        // check the first 2 bytes of the stream

        byte[] b = {data[0], data[1], data[2], data[3]};

        if (data[0] == 0x42 && data[1] == 0x4d) {
          // this is the thumbnail
        }
        else if (TiffTools.checkHeader(b) != null) {
          // this is an actual image plane
          Integer num = new Integer(nImages);
          pixels.put(num, dirName);
          names.put(num, entryName);
          nImages++;
        }
        else if (entryName.equals("OibInfo.txt")) { /* ignore this */ }
        else {
          // INI-style metadata

          String ini = new String(data);
          if (ini.indexOf("????????") != -1) ini = "";

          StringTokenizer st = new StringTokenizer(ini, "\n");

          String prefix = "";

          while (st.hasMoreTokens()) {
            String token = st.nextToken();
            token = DataTools.stripString(token);
            if (token.indexOf("!!!!!!") != -1) token = "";
            if (token.indexOf("=") != -1) {
              String key = token.substring(0, token.indexOf("="));
              String value = token.substring(token.indexOf("=") + 1);
              String s = (prefix + key).trim();
              if (s.equals("[FileInformation] - Resolution")) {
                int max = Integer.parseInt(value.trim());
                while (Math.pow(2, bpp) < max) bpp++;
                bpp /= 8;
              }
              metadata.put(prefix + key.trim(), value.trim());
            }
            else {
              prefix = token.trim() + " - ";
              if (prefix.charAt(2) == '[') prefix = prefix.substring(2);
            }
          }

          data = null;
        }

        r.exec("dis.close()");
      }
    }
  }

  /** Debugging utility method. */
  public static final void print(int depth, String s) {
    StringBuffer sb = new StringBuffer();
    for (int i=0; i<depth; i++) sb.append("  ");
    sb.append(s);
    System.out.println(sb.toString());
  }

  // -- Main method --
  public static void main(String[] args) throws FormatException, IOException {
    new OIBReader().testRead(args);
  }
}
