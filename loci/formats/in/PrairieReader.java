//
// PrairieReader.java
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

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import loci.formats.*;

/**
 * PrairieReader is the file format reader for
 * Prairie Technologies' TIFF variant.
 */
public class PrairieReader extends FormatReader {

  // -- Constants --

  // Private tags present in Prairie TIFF files
  // IMPORTANT NOTE: these are the same as Metamorph's private tags - therefore,
  //                 it is likely that Prairie TIFF files will be incorrectly
  //                 identified unless the XML or CFG file is specified
  private static final int PRAIRIE_TAG_1 = 33628;
  private static final int PRAIRIE_TAG_2 = 33629;
  private static final int PRAIRIE_TAG_3 = 33630;

  // -- Fields --

  /** Number of images */
  private int numImages;

  /** List of files in the current dataset */
  private String[] files;

  /** Helper reader for opening images */
  private TiffReader tiff;

  /** Names of the associated XML files */
  private String xmlFile, cfgFile;
  private boolean readXML = false, readCFG = false;

  // -- Constructor --

  /** Constructs a new Prairie TIFF reader. */
  public PrairieReader() {
    super("Prairie (TIFF)", new String[] {"tif", "tiff", "cfg", "xml"});
  }

  // -- FormatHandler API methods --

  /**
   * Checks if the given string is a valid filename for a Prairie TIFF file.
   * @param open If true, and the file extension is insufficient to determine
   *  the file type, the (existing) file is opened for further analysis.
   */
  public boolean isThisType(String name, boolean open) {
    if (!super.isThisType(name, open)) return false; // check extension

    // check if there is an XML file in the same directory
    Location  f = new Location(name);
    f = f.getAbsoluteFile();
    Location parent = f.getParentFile();
    String[] listing = parent.list();
    int xmlCount = 0;
    for (int i=0; i<listing.length; i++) {
      if (listing[i].toLowerCase().endsWith(".xml")) xmlCount++;
    }

    boolean xml = xmlCount > 0;

    // just checking the filename isn't enough to differentiate between
    // Prairie and regular TIFF; open the file and check more thoroughly
    return open ? checkBytes(name, 524304) && xml : xml;
  }

  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for a Prairie TIFF file. */
  public boolean isThisType(byte[] block) {
    // adapted from MetamorphReader.isThisType(byte[])
    if (block.length < 3) return false;
    if (block.length < 8) {
      return true; // we have no way of verifying further
    }

    boolean little = (block[0] == 0x49 && block[1] == 0x49);

    int ifdlocation = DataTools.bytesToInt(block, 4, little);

    if (ifdlocation < 0) return false;
    else if (ifdlocation + 1 > block.length) return true;
    else {
      int ifdnumber = DataTools.bytesToInt(block, ifdlocation, 2, little);
      for (int i=0; i<ifdnumber; i++) {
        if (ifdlocation + 3 + (i*12) > block.length) {
          return false;
        }
        else {
          int ifdtag = DataTools.bytesToInt(block,
            ifdlocation + 2 + (i*12), 2, little);
          if (ifdtag == PRAIRIE_TAG_1 || ifdtag == PRAIRIE_TAG_2 ||
            ifdtag == PRAIRIE_TAG_3)
          {
            return true;
          }
        }
      }
      return false;
    }
  }

  /* @see IFormatReader#getUsedFiles(String) */
  public String[] getUsedFiles(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    String[] s = new String[files.length + 2];
    System.arraycopy(files, 0, s, 0, files.length);
    s[files.length] = xmlFile;
    s[files.length + 1] = cfgFile;
    return s;
  }

  /* @see IFormatReader#getImageCount(String) */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return numImages;
  }

  /* @see IFormatReader#isRGB(String) */
  public boolean isRGB(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return false;
  }

  /* @see IFormatReader#isLittleEndian(String) */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return tiff.isLittleEndian(files[0]);
  }

  /* @see IFormatReader#isInterleaved(String) */
  public boolean isInterleaved(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return false;
  }

  /* @see IFormatReader#openBytes(String, int) */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }
    byte[] b = tiff.openBytes(files[no], 0);
    updateMinMax(b, no);
    return b;
  }

  public byte[] openBytes(String id, int no, byte[] buf)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }
    tiff.openBytes(files[no], 0, buf);
    updateMinMax(buf, no);
    return buf; 
  }

  /* @see IFormatReader#openImage(String, int) */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }
    BufferedImage b = tiff.openImage(files[no], 0);
    updateMinMax(b, no);
    return b;
  }

  /* @see IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws FormatException, IOException {
    if (fileOnly && tiff != null) tiff.close(fileOnly);
    else if (!fileOnly) close();
  }

  /* @see IFormatReader#close() */
  public void close() throws FormatException, IOException {
    files = null;
    if (tiff != null) tiff.close();
    tiff = null;
    currentId = null;
    readXML = false;
    readCFG = false;
  }

  /* @see IFormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (debug) debug("PrairieReader.initFile(" + id + ")");

    if (id.endsWith("xml") || id.endsWith("cfg")) {
      // we have been given the XML file that lists TIFF files (best case)

      if (id.endsWith("xml")) {
        super.initFile(id);
        tiff = new TiffReader();
        xmlFile = id;
        readXML = true;
      }
      else if (id.endsWith("cfg")) {
        if (metadata == null) metadata = new Hashtable();
        cfgFile = id;
        readCFG = true;
      }

      RandomAccessStream is = new RandomAccessStream(id);
      byte[] b = new byte[(int) is.length()];
      is.read(b);
      is.close();
      String s = new String(b);

      Vector elements = new Vector();

      while (s.length() > 0) {
        int ndx = s.indexOf("<");
        int val1 = s.indexOf(">", ndx);
        if (val1 != -1 && val1 > ndx) {
          String sub = s.substring(ndx + 1, val1);
          s = s.substring(val1 + 1);
          elements.add(sub);
        }
      }

      int zt = 0;
      Vector f = new Vector();
      int fileIndex = 1;
      if (id.endsWith(".xml")) numImages = 0;

      String pastPrefix = "";
      for (int i=1; i<elements.size(); i++) {
        String el = (String) elements.get(i);
        if (el.indexOf(" ") != -1) {
          boolean closed = el.endsWith("/");

          String prefix = el.substring(0, el.indexOf(" "));
          if (prefix.equals("File")) numImages++;
          if (prefix.equals("Frame")) {
            zt++;
            fileIndex = 1;
          }

          if (!prefix.equals("Key") && !prefix.equals("Frame")) {
            el = el.substring(el.indexOf(" ") + 1);
            while (el.indexOf("=") != -1) {
              int eq = el.indexOf("=");
              String key = el.substring(0, eq);
              String value = el.substring(eq + 2, el.indexOf("\"", eq + 2));
              if (prefix.equals("File")) {
                addMeta(pastPrefix + " " + prefix + " " + fileIndex +
                  " " + key, value);
                if (key.equals("filename")) fileIndex++;
              }
              else addMeta(pastPrefix + " " + prefix + " " + key, value);
              el = el.substring(el.indexOf("\"", eq + 2) + 1).trim();
              if (prefix.equals("File") && key.equals("filename")) {
                Location current = new Location(id);
                current = current.getAbsoluteFile();
                f.add(current.getParent() + "/" + value);
              }
            }
          }
          else if (prefix.equals("Key")) {
            int keyIndex = el.indexOf("key") + 5;
            int valueIndex = el.indexOf("value") + 7;
            String key = el.substring(keyIndex, el.indexOf("\"", keyIndex));
            String value =
              el.substring(valueIndex, el.indexOf("\"", valueIndex));
            addMeta(key, value);
          }
          if (!closed) {
            pastPrefix = prefix;
            if (prefix.equals("Frame")) {
              int index = el.indexOf("index") + 7;
              String idx = el.substring(index, el.indexOf("\"", index));
              pastPrefix += " " + idx;
            }
          }
        }
      }

      if (id.endsWith("xml")) {
        files = new String[f.size()];
        f.copyInto(files);

        boolean isZ =
          ((String) getMeta("PVScan Sequence type")).equals("ZSeries");
        if (zt == 0) zt = 1;

        sizeX[0] = Integer.parseInt((String) getMeta("pixelsPerLine"));
        sizeY[0] = Integer.parseInt((String) getMeta("linesPerFrame"));
        sizeZ[0] = isZ ? zt : 1;
        sizeT[0] = isZ ? 1 : zt;
        sizeC[0] = numImages / (sizeZ[0] * sizeT[0]);
        currentOrder[0] = "XYC" + (isZ ? "ZT" : "TZ");
        pixelType[0] = FormatReader.UINT16;

        float pixSizeX =
          Float.parseFloat((String) getMeta("micronsPerPixel_XAxis"));
        float pixSizeY =
          Float.parseFloat((String) getMeta("micronsPerPixel_YAxis"));

        MetadataStore store = getMetadataStore(id);

        store.setPixels(
          new Integer(sizeX[0]),
          new Integer(sizeY[0]),
          new Integer(sizeZ[0]),
          new Integer(sizeC[0]),
          new Integer(sizeT[0]),
          new Integer(pixelType[0]),
          new Boolean(isLittleEndian(id)),
          currentOrder[0],
          null,
          null);
        store.setDimensions(new Float(pixSizeX), new Float(pixSizeY), null,
          null, null, null);
        for (int i=0; i<sizeC[0]; i++) {
          store.setLogicalChannel(i, null, null, null, null, null, null, null);
        }
      
        String date = (String) getMeta(" PVScan date");
       
        store.setImage(null, date, null, null);
        
        String laserPower = (String) getMeta("laserPower_0");
        
        store.setLaser(null, null, null, null, null, null, 
          laserPower == null ? null : new Float(laserPower), 
          null, null, null, null); 
        
        for (int i=0; i<4; i++) {
          String gain = (String) getMeta("pmtGain_" + i);
          String offset = (String) getMeta("pmtOffset_" + i);
          store.setDetector(null, null, null, null, 
            gain == null ? null : new Float(gain), null, 
            offset == null ? null : new Float(offset), null, new Integer(i)); 
        }
   
        String zoom = (String) getMeta("opticalZoom");
        if (zoom != null) {
          store.setDisplayOptions(new Float(zoom), new Boolean(sizeC[0] > 1),
            new Boolean(sizeC[0] > 1), new Boolean(sizeC[0] > 2), Boolean.FALSE,
            null, null, null, null, null, null, null, null, null, null, null);
        }
      }

      if (!readXML || !readCFG) {
        Location file = new Location(id);
        file = file.getAbsoluteFile();
        Location parent = file.getParentFile();
        String[] listing = parent.list();
        Location next = null;
        for (int i=0; i<listing.length; i++) {
          String path = listing[i].toLowerCase();
          if ((!readXML && path.endsWith(".xml")) ||
            (readXML && path.endsWith(".cfg")))
          {
            next = new Location(parent, path);
          }
        }
        if (next != null) initFile(next.getAbsolutePath());
      }
    }
    else {
      // we have been given a TIFF file - reinitialize with the proper XML file

      Location f = new Location(id);
      f = f.getAbsoluteFile();
      Location parent = f.getParentFile();
      String[] listing = parent.list();
      for (int i=0; i<listing.length; i++) {
        String path = listing[i].toLowerCase();
        if (path.endsWith(".xml") || path.endsWith(".cfg")) {
          initFile(new Location(path).getAbsolutePath());
        }
      }
    }
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new PrairieReader().testRead(args);
  }

}
