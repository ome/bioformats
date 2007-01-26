//
// OIFReader.java
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
import java.awt.image.ColorModel;
import java.io.*;
import java.util.*;
import loci.formats.*;

/**
 * OIFReader is the file format reader for Fluoview FV 1000 OIF files.
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class OIFReader extends FormatReader {

  // -- Fields --

  /** Current file. */
  protected RandomAccessStream reader;

  /** Number of image planes in the file. */
  protected int numImages = 0;

  /** Names of every TIFF file to open. */
  protected Vector tiffs;

  /** Helper reader to open TIFF files. */
  protected TiffReader[] tiffReader;

  /** Helper reader to open the thumbnail. */
  protected BMPReader thumbReader;

  /** Number of valid bits per pixel. */
  private int[] validBits;

  private Vector usedFiles = new Vector();

  // -- Constructor --

  /** Constructs a new OIF reader. */
  public OIFReader() {
    super("Fluoview FV1000 OIF",
      new String[] {"oif", "roi", "pty", "lut", "bmp"});
  }

  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for an OIF file. */
  public boolean isThisType(byte[] block) {
    return false;
  }

  /** Determines the number of images in the given OIF file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId) && !DataTools.samePrefix(id, currentId)) {
      initFile(id);
    }
    return isRGB(id) ? 3*numImages : numImages;
  }

  /**
   * Obtains the specified metadata field's value for the given file.
   *
   * @param field the name associated with the metadata field
   * @return the value, or null if the field doesn't exist
   */
  public Object getMetadataValue(String id, String field)
    throws FormatException, IOException
  {
    if (!id.equals(currentId) && !DataTools.samePrefix(id, currentId)) {
      initFile(id);
    }
    return getMeta(field);
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    if (!id.equals(currentId) && !DataTools.samePrefix(id, currentId)) {
      initFile(id);
    }
    tiffReader[0].setColorTableIgnored(ignoreColorTable);
    return tiffReader[0].isRGB((String) tiffs.get(0));
  }

  /** Return true if the data is in little-endian format. */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    return true;
  }

  /** Returns whether or not the channels are interleaved. */
  public boolean isInterleaved(String id) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#getChannelGlobalMinimum(String, int) */
  public Double getChannelGlobalMinimum(String id, int theC)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return new Double((String) getMeta("[Image Parameters] - DataMin"));
  }

  /* @see loci.formats.IFormatReader#getChannelGlobalMaximum(String, int) */
  public Double getChannelGlobalMaximum(String id, int theC)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return new Double((String) getMeta("[Image Parameters] - DataMax"));
  }

  /** Obtains the specified image from the given OIF file as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId) && !DataTools.samePrefix(id, currentId)) {
      initFile(id);
    }
    tiffReader[no].setColorTableIgnored(ignoreColorTable);
    byte[] b = tiffReader[no].openBytes((String) tiffs.get(no), 0);
    tiffReader[no].close();
    return b;
  }

  /** Obtains the specified image from the given OIF file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId) && !DataTools.samePrefix(id, currentId)) {
      initFile(id);
    }

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    tiffReader[no].setColorTableIgnored(ignoreColorTable);
    BufferedImage b = tiffReader[no].openImage((String) tiffs.get(no), 0);
    ColorModel cm = ImageTools.makeColorModel(b.getRaster().getNumBands(),
      b.getRaster().getTransferType(), validBits);
    b = ImageTools.makeBuffered(b, cm);
    tiffReader[no].close();
    return b;
  }

  /** Obtains a thumbnail for the specified image from the given file. */
  public BufferedImage openThumbImage(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId) && !DataTools.samePrefix(id, currentId)) {
      initFile(id);
    }

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }

    String thumbId = "";
    String dir = id.substring(0, id.lastIndexOf(File.separator) + 1);
    dir += id.substring(id.lastIndexOf(File.separator) + 1) + ".files" +
      File.separator;

    thumbId = dir + id.substring(id.lastIndexOf(File.separator) + 1,
      id.lastIndexOf(".")) + "_Thumb.bmp";
    thumbReader.setColorTableIgnored(ignoreColorTable);
    return thumbReader.openImage(thumbId, 0);
  }

  /** Get the size of the X dimension for the thumbnail. */
  public int getThumbSizeX(String id) throws FormatException, IOException {
    return openThumbImage(id, 0).getWidth();
  }

  /** Get the size of the Y dimension for the thumbnail. */
  public int getThumbSizeY(String id) throws FormatException, IOException {
    return openThumbImage(id, 0).getHeight();
  }

  /* @see IFormatReader#getUsedFiles(String) */
  public String[] getUsedFiles(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    String[] s = (String[]) usedFiles.toArray(new String[0]);
    return s;
  }

  /* @see IFormatReader#close(boolean) */
  /*
  public void close(boolean fileOnly) throws FormatException, IOException {
    if (fileOnly) {
      if (reader != null) reader.close();
      if (thumbReader != null) thumbReader.close(fileOnly);
      if (tiffReader != null) {
        for (int i=0; i<tiffReader.length; i++) {
          if (tiffReader[i] != null) tiffReader[i].close(fileOnly);
        }
      }
    }
    else close();
  }
  */

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    if (reader != null) reader.close();
    reader = null;
    currentId = null;
    if (thumbReader != null) thumbReader.close();
    if (tiffReader != null) {
      for (int i=0; i<tiffReader.length; i++) {
        if (tiffReader[i] != null) tiffReader[i].close();
      }
    }
    tiffs = null;
  }

  /** Initializes the given OIF file. */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("OIFReader.initFile(" + id + ")");

    // check to make sure that we have the OIF file
    // if not, we need to look for it in the parent directory

    String oifFile = id;
    if (!id.toLowerCase().endsWith("oif")) {
      Location current = new Location(id);
      current = current.getAbsoluteFile();
      String parent = current.getParent();
      Location tmp = new Location(parent);
      parent = tmp.getParent();

      // strip off the filename

      id = current.getPath();

      oifFile = id.substring(id.lastIndexOf(File.separator));
      oifFile = parent + oifFile.substring(0, oifFile.indexOf("_")) + ".oif";

      tmp = new Location(oifFile);
      if (!tmp.exists()) {
        oifFile = oifFile.substring(0, oifFile.lastIndexOf(".")) + ".OIF";
        tmp = new Location(oifFile);
        if (!tmp.exists()) throw new FormatException("OIF file not found");
        currentId = oifFile;
      }
      else currentId = oifFile;
    }

    super.initFile(oifFile);
    reader = new RandomAccessStream(oifFile);

    usedFiles.clear();
    usedFiles.add(new Location(oifFile).getAbsolutePath());

    int slash = oifFile.lastIndexOf(File.separator);
    String path = slash < 0 ? "." : oifFile.substring(0, slash);

    // parse each key/value pair (one per line)

    byte[] b = new byte[(int) reader.length()];
    reader.read(b);
    String s = new String(b);
    StringTokenizer st = new StringTokenizer(s, "\r\n");

    Hashtable filenames = new Hashtable();
    String prefix = "";
    while (st.hasMoreTokens()) {
      String line = DataTools.stripString(st.nextToken().trim());
      if (!line.startsWith("[") && (line.indexOf("=") > 0)) {
        String key = line.substring(0, line.indexOf("=")).trim();
        String value = line.substring(line.indexOf("=") + 1).trim();
        if (key.startsWith("IniFileName") && key.indexOf("Thumb") == -1) {
          int pos = Integer.parseInt(key.substring(11));
          filenames.put(new Integer(pos), value);
        }
        addMeta(prefix + key, value);
      }
      else if (line.length() > 0) {
        if (line.indexOf("[") == 2) {
          line = line.substring(2, line.length());
        }
        prefix = line + " - ";
      }
    }

    thumbReader = new BMPReader();
    numImages = filenames.size();
    tiffs = new Vector(numImages);

    tiffReader = new TiffReader[numImages];
    for (int i=0; i<numImages; i++) tiffReader[i] = new TiffReader();

    // open each INI file (.pty extension)

    String tiffPath = null;
    RandomAccessStream ptyReader;
    
    for (int i=0; i<numImages; i++) {
      String file = (String) filenames.get(new Integer(i));
      file = file.substring(1, file.length() - 1);
      file = file.replace('\\', File.separatorChar);
      file = file.replace('/', File.separatorChar);
      file = path + File.separator + file;
      tiffPath = file.substring(0, file.lastIndexOf(File.separator));

      ptyReader = new RandomAccessStream(file);
      b = new byte[(int) ptyReader.length()];
      ptyReader.read(b);
      s = new String(b);
      st = new StringTokenizer(s, "\n");

      while (st.hasMoreTokens()) {
        String line = st.nextToken().trim();
        if (!line.startsWith("[") && (line.indexOf("=") > 0)) {
          String key = line.substring(0, line.indexOf("=") - 1).trim();
          String value = line.substring(line.indexOf("=") + 1).trim();
          key = DataTools.stripString(key);
          value = DataTools.stripString(value);
          if (key.equals("DataName")) {
            value = value.substring(1, value.length() - 1);
            tiffs.add(i, tiffPath + File.separator + value);
          }
          addMeta("Image " + i + " : " + key, value);
        }
      }
      ptyReader.close();
    }

    if (tiffPath != null) {
      Location dir = new Location(tiffPath);
      String[] list = dir.list();
      for (int i=0; i<list.length; i++) usedFiles.add(list[i]);
    }

    for (int i=0; i<9; i++) {
      String pre = "[Axis " + i + " Parameters Common] - ";
      String code = (String) getMeta(pre + "AxisCode");
      String size = (String) getMeta(pre + "MaxSize");
      if (code.equals("\"X\"")) sizeX[0] = Integer.parseInt(size);
      else if (code.equals("\"Y\"")) sizeY[0] = Integer.parseInt(size);
      else if (code.equals("\"C\"")) sizeC[0] = Integer.parseInt(size);
      else if (code.equals("\"T\"")) sizeT[0] = Integer.parseInt(size);
      else if (code.equals("\"Z\"")) sizeZ[0] = Integer.parseInt(size);
    }

    if (sizeZ[0] == 0) sizeZ[0] = 1;
    if (sizeC[0] == 0) sizeC[0] = 1;
    if (sizeT[0] == 0) sizeT[0] = 1;

    while (numImages > sizeZ[0] * sizeT[0] * getEffectiveSizeC(id)) {
      if (sizeZ[0] == 1) sizeT[0]++;
      else if (sizeT[0] == 1) sizeZ[0]++;
    }

    String metadataOrder = (String)
      getMeta("[Axis Parameter Common] - AxisOrder");
    metadataOrder = metadataOrder.substring(1, metadataOrder.length() - 1);
    if (metadataOrder == null) metadataOrder = "XYZTC";
    else {
      String[] names = new String[] {"X", "Y", "Z", "C", "T"};
      if (metadataOrder.length() < 5) {
        for (int i=0; i<names.length; i++) {
          if (metadataOrder.indexOf(names[i]) == -1) metadataOrder += names[i];
        }
      }
    }
    currentOrder[0] = metadataOrder;

    // The metadata store we're working with.
    MetadataStore store = getMetadataStore(oifFile);

    int imageDepth = Integer.parseInt((String)
      getMeta("[Reference Image Parameter] - ImageDepth"));
    switch (imageDepth) {
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
          "Unknown matching for pixel depth of: " + imageDepth);
    }

    validBits = new int[sizeC[0]];
    if (validBits.length == 2) validBits = new int[3];
    for (int i=0; i<validBits.length; i++) {
      s = (String) getMeta("[Reference Image Parameter] - ValidBitCounts");
      if (s != null) {
        validBits[i] = Integer.parseInt(s);
      }
      else {
        i = validBits.length;
      }
    }

    int len = validBits.length;
    for (int i=0; i<len; i++) {
      if (validBits[i] == 0) validBits = null;
    }

    store.setPixels(
      new Integer(sizeX[0]),
      new Integer(sizeY[0]),
      new Integer(sizeZ[0]),
      new Integer(sizeC[0]),
      new Integer(sizeT[0]),
      new Integer(pixelType[0]),
      new Boolean(false),
      "XYZTC",
      null);

    Float pixX = new Float((String)
      getMeta("[Reference Image Parameter] - WidthConvertValue"));
    Float pixY = new Float((String)
      getMeta("[Reference Image Parameter] - HeightConvertValue"));

    store.setDimensions(pixX, pixY, null, null, null, null);
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new OIFReader().testRead(args);
  }

}
