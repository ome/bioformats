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
import java.io.*;
import java.util.Hashtable;
import java.util.Vector;
import loci.formats.*;

/**
 * OIFReader is the file format reader for Fluoview FV 1000 OIF files.
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class OIFReader extends FormatReader {

  // -- Fields --

  /** Current file. */
  protected BufferedReader reader;

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
    return metadata.get(field);
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
    return new Double((String) metadata.get("Image 0 : DataMin"));
  }

  /* @see loci.formats.IFormatReader#getChannelGlobalMaximum(String, int) */
  public Double getChannelGlobalMaximum(String id, int theC)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return new Double((String) metadata.get("Image 0: DataMax"));
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
    b = ImageTools.makeBuffered(b,
      ImageTools.makeColorModel(b.getRaster().getNumBands(),
      b.getRaster().getTransferType(), validBits));
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
    String[] s = new String[tiffs.size() + 1];
    s[0] = getMappedId(currentId);
    for (int i=1; i<s.length; i++) s[i] = getMappedId((String) tiffs.get(i-1));
    return s;
  }

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
    // check to make sure that we have the OIF file
    // if not, we need to look for it in the parent directory

    String oifFile = id;
    if (!id.toLowerCase().endsWith("oif")) {
      File current = new File(getMappedId(id));
      current = current.getAbsoluteFile();
      String parent = current.getParent();
      File tmp = new File(parent);
      parent = tmp.getParent();

      // strip off the filename

      id = current.getPath();

      oifFile = id.substring(id.lastIndexOf(File.separator));
      oifFile = parent + oifFile.substring(0, oifFile.indexOf("_")) + ".oif";

      tmp = new File(getMappedId(oifFile));
      if (!tmp.exists()) {
        oifFile = oifFile.substring(0, oifFile.lastIndexOf(".")) + ".OIF";
        tmp = new File(getMappedId(oifFile));
        if (!tmp.exists()) throw new FormatException("OIF file not found");
        currentId = oifFile;
      }
      else currentId = oifFile;
    }

    super.initFile(oifFile);
    reader = new BufferedReader(new FileReader(getMappedId(oifFile)));

    int slash = oifFile.lastIndexOf(File.separator);
    String path = slash < 0 ? "." : oifFile.substring(0, slash);

    // parse each key/value pair (one per line)

    Hashtable filenames = new Hashtable();
    String line = reader.readLine();
    while (line != null) {
      if (!line.startsWith("[") && (line.indexOf("=") > 0)) {
        String key = line.substring(0, line.indexOf("=") - 1).trim();
        String value = line.substring(line.indexOf("=") + 1).trim();
        key = DataTools.stripString(key);
        value = DataTools.stripString(value);
        if (key.startsWith("IniFileName") && key.indexOf("Thumb") == -1) {
          int pos = Integer.parseInt(key.substring(11));
          filenames.put(new Integer(pos), value);
        }
        metadata.put(key, value);
      }
      line = reader.readLine();
    }

    thumbReader = new BMPReader();
    numImages = filenames.size();
    tiffs = new Vector(numImages);

    tiffReader = new TiffReader[numImages];
    for (int i=0; i<numImages; i++) tiffReader[i] = new TiffReader();

    // open each INI file (.pty extension)

    String tiffPath;
    BufferedReader ptyReader;
    for (int i=0; i<numImages; i++) {
      String file = (String) filenames.get(new Integer(i));
      file = file.substring(1, file.length() - 1);
      file = file.replace('\\', File.separatorChar);
      file = file.replace('/', File.separatorChar);
      file = path + File.separator + file;
      tiffPath = file.substring(0, file.lastIndexOf(File.separator));

      ptyReader = new BufferedReader(new FileReader(getMappedId(file)));
      line = ptyReader.readLine();
      while (line != null) {
        if (!line.startsWith("[") && (line.indexOf("=") > 0)) {
          String key = line.substring(0, line.indexOf("=") - 1).trim();
          String value = line.substring(line.indexOf("=") + 1).trim();
          key = DataTools.stripString(key);
          value = DataTools.stripString(value);
          if (key.equals("DataName")) {
            value = value.substring(1, value.length() - 1);
            tiffs.add(i, tiffPath + File.separator + value);
          }
          metadata.put("Image " + i + " : " + key, value);
        }
        line = ptyReader.readLine();
      }
      ptyReader.close();
    }

    sizeX[0] = Integer.parseInt((String) metadata.get("ImageWidth"));
    sizeY[0] = Integer.parseInt((String) metadata.get("ImageHeight"));
    String metadataOrder = (String) metadata.get("AxisOrder");
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

    String axis = (String) metadata.get("View Max CH");
    int axisCount = 1;
    if (axis != null) {
      axis = axis.substring(1, axis.length() -1 );
      axisCount = Integer.parseInt(axis.trim());
    }
    if (isRGB(currentId)) axisCount *= 3;

    sizeC[0] = axisCount;
    int remainingImages = numImages;
    if (metadataOrder.indexOf("C") == 2) remainingImages /= axisCount;
    sizeZ[0] = metadataOrder.indexOf("Z") < metadataOrder.indexOf("T") ?
      remainingImages : 1;
    sizeT[0] = metadataOrder.indexOf("T") < metadataOrder.indexOf("Z") ?
      remainingImages : 1;

    // The metadata store we're working with.
    MetadataStore store = getMetadataStore(oifFile);

    int imageDepth = Integer.parseInt((String) metadata.get("ImageDepth"));
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
      validBits[i] = Integer.parseInt((String) metadata.get("Image " + i + 
        " : ValidBitCounts"));
    }

    store.setPixels(
      new Integer(Integer.parseInt((String) metadata.get("ImageWidth"))),
      new Integer(Integer.parseInt((String) metadata.get("ImageHeight"))),
      new Integer(sizeZ[0]),
      new Integer(getSizeC(id)),
      new Integer(sizeT[0]),
      new Integer(pixelType[0]),
      new Boolean(false),
      "XYZTC",
      null);

    Float pixX =
      new Float(metadata.get("Image 0 : WidthConvertValue").toString());
    Float pixY =
      new Float(metadata.get("Image 0 : HeightConvertValue").toString());

    store.setDimensions(pixX, pixY, null, null, null, null);
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new OIFReader().testRead(args);
  }

}
