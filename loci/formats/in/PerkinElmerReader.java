//
// PerkinElmerReader.java
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
import java.lang.reflect.Method;
import java.util.StringTokenizer;
import java.util.Vector;
import loci.formats.*;

/**
 * PerkinElmerReader is the file format reader for PerkinElmer files.
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class PerkinElmerReader extends FormatReader {

  // -- Fields --

  /** Number of images. */
  protected int numImages;

  /** Helper reader. */
  protected TiffReader[] tiff;

  /** Tiff files to open. */
  protected String[] files;

  /** Number of channels. */
  private int channels;

  /** Flag indicating that the image data is in TIFF format. */
  private boolean isTiff = true;

  /** List of all files to open */
  private Vector allFiles;

  // -- Constructor --

  /** Constructs a new PerkinElmer reader. */
  public PerkinElmerReader() {
    super("PerkinElmer", new String[] {"rec", "cfg", "ano", "2", "3", "4",
      "csv", "htm", "tim", "zpo"});
  }

  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for a PerkinElmer file. */
  public boolean isThisType(byte[] block) { return false; }

  /** Determines the number of images in the given PerkinElmer file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId) && !DataTools.samePrefix(id, currentId)) {
      initFile(id);
    }
    return numImages;
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    if (!id.equals(currentId) && !DataTools.samePrefix(id, currentId)) {
      initFile(id);
    }
    if (isTiff) {
      tiff[0].setColorTableIgnored(ignoreColorTable);
      return tiff[0].isRGB(files[0]);
    }
    return false;
  }

  /** Return true if the data is in little-endian format. */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    if (isTiff) return tiff[0].isLittleEndian(files[0]);
    return true;
  }

  /** Returns whether or not the channels are interleaved. */
  public boolean isInterleaved(String id) throws FormatException, IOException {
    return false;
  }

  /** Obtains the specified image from the given file as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId) && !DataTools.samePrefix(id, currentId)) {
      initFile(id);
    }
    if (isTiff) {
      tiff[no / channels].setColorTableIgnored(ignoreColorTable);
      return tiff[no / channels].openBytes(files[no / channels], 0);
    }

    String file = files[no];
    RandomAccessStream ras = new RandomAccessStream(getMappedId(file));
    byte[] b = new byte[(int) ras.length() - 6]; // each file has 6 magic bytes
    ras.skipBytes(6);
    ras.read(b);
    ras.close();
    return b;
  }

  /** Obtains the specified image from the given PerkinElmer file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId) && !DataTools.samePrefix(id, currentId)) {
      initFile(id);
    }

    if (no < 0 || no >= getImageCount(id)) {
      throw new FormatException("Invalid image number: " + no);
    }
    if (isTiff) {
      tiff[no / channels].setColorTableIgnored(ignoreColorTable);
      return tiff[no / channels].openImage(files[no / channels], 0);
    }

    byte[] b = openBytes(id, no);
    int bpp = b.length / (sizeX[0] * sizeY[0]);
    return ImageTools.makeImage(b, sizeX[0], sizeY[0], 1, false, bpp, true);
  }

  /* @see IFormatReader#getUsedFiles(String) */
  public String[] getUsedFiles(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return (String[]) allFiles.toArray(new String[0]);
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    currentId = null;
    files = null;
    if (tiff != null) {
      for (int i=0; i<tiff.length; i++) {
        if (tiff[i] != null) tiff[i].close();
      }
    }
  }

  /** Initializes the given PerkinElmer file. */
  protected void initFile(String id) throws FormatException, IOException {
    super.initFile(id);

    allFiles = new Vector();

    // get the working directory
    File tempFile = new File(getMappedId(id));
    File workingDir = tempFile.getParentFile();
    if (workingDir == null) workingDir = new File(".");
    String workingDirPath = workingDir.getPath() + File.separator;
    String[] ls = workingDir.list();

    allFiles.add(getMappedId(id));

    // check if we have any of the required header file types

    int cfgPos = -1;
    int anoPos = -1;
    int recPos = -1;
    int timPos = -1;
    int csvPos = -1;
    int zpoPos = -1;
    int htmPos = -1;
    int filesPt = 0;
    files = new String[ls.length];

    String tempFileName = tempFile.getName();
    int dot = tempFileName.lastIndexOf(".");
    String check = dot < 0 ? tempFileName : tempFileName.substring(0, dot);

    // locate appropriate .tim, .csv, .zpo, .htm and .tif files

    String prefix = null;

    for (int i=0; i<ls.length; i++) {
      // make sure that the file has a name similar to the name of the
      // specified file

      int d = ls[i].lastIndexOf(".");
      while (d == -1 && i < ls.length) {
        i++;
        d = ls[i].lastIndexOf(".");
      }
      String s = dot < 0 ? ls[i] : ls[i].substring(0, d);

      String filename = ls[i].toLowerCase();

      if (s.startsWith(check) || check.startsWith(s) ||
        ((prefix != null) && (s.startsWith(prefix))))
      {
        if (cfgPos == -1) {
          if (filename.endsWith(".cfg")) {
            cfgPos = i;
            prefix = ls[i].substring(0, d);
          }
        }

        if (anoPos == -1) {
          if (filename.endsWith(".ano")) {
            anoPos = i;
            prefix = ls[i].substring(0, d);
          }
        }

        if (recPos == -1) {
          if (filename.endsWith(".rec")) {
            recPos = i;
            prefix = ls[i].substring(0, d);
          }
        }

        if (timPos == -1) {
          if (filename.endsWith(".tim")) {
            timPos = i;
            prefix = ls[i].substring(0, d);
          }
        }
        if (csvPos == -1) {
          if (filename.endsWith(".csv")) {
            csvPos = i;
            prefix = ls[i].substring(0, d);
          }
        }
        if (zpoPos == -1) {
          if (filename.endsWith(".zpo")) {
            zpoPos = i;
            prefix = ls[i].substring(0, d);
          }
        }
        if (htmPos == -1) {
          if (filename.endsWith(".htm")) {
            htmPos = i;
            prefix = ls[i].substring(0, d);
          }
        }

        if (filename.endsWith(".tif") || filename.endsWith(".tiff")) {
          files[filesPt] = workingDirPath + ls[i];
          filesPt++;
        }

        try {
          String ext = filename.substring(filename.lastIndexOf(".") + 1);
          int num = Integer.parseInt(ext);
          isTiff = false;
          files[filesPt] = workingDirPath + ls[i];
          filesPt++;
        }
        catch (Exception e) {
          try {
            String ext = filename.substring(filename.lastIndexOf(".") + 1);
            int num = Integer.parseInt(ext, 16);
            isTiff = false;
            files[filesPt] = workingDirPath + ls[i];
            filesPt++;
          }
          catch (Exception f) {
          }
        }
      }
    }

    // re-order the files

    String[] tempFiles = files;
    files = new String[filesPt];
    //System.arraycopy(tempFiles, 0, files, 0, filesPt);

    // determine the number of different extensions we have

    int extCount = 0;
    Vector foundExts = new Vector();
    for (int i=0; i<filesPt; i++) {
      String ext = tempFiles[i].substring(tempFiles[i].lastIndexOf(".") + 1);
      if (!foundExts.contains(ext)) {
        extCount++;
        foundExts.add(ext);
      }
    }

    for (int i=0; i<filesPt; i+=extCount) {
      Vector extSet = new Vector();
      for (int j=0; j<extCount; j++) {
        if (extSet.size() == 0) extSet.add(tempFiles[i + j]);
        else {
          String ext =
            tempFiles[i+j].substring(tempFiles[i+j].lastIndexOf(".") + 1);
          int extNum = Integer.parseInt(ext, 16);

          int insert = -1;
          int pos = 0;
          while (insert == -1 && pos < extSet.size()) {
            String posString = (String) extSet.get(pos);
            posString = posString.substring(posString.lastIndexOf(".") + 1);
            int posNum = Integer.parseInt(posString, 16);

            if (extNum < posNum) insert = pos;
            pos++;
          }
          if (insert == -1) extSet.add(tempFiles[i+j]);
          else extSet.add(insert, tempFiles[i + j]);
        }
      }

      for (int j=0; j<extCount; j++) {
        files[i+j] = (String) extSet.get(j);
      }
    }

    for (int i=0; i<files.length; i++) allFiles.add(files[i]);

    numImages = files.length;
    BufferedReader read;
    char[] data;
    StringTokenizer t;

    tiff = new TiffReader[numImages];
    for (int i=0; i<tiff.length; i++) tiff[i] = new TiffReader();

    // highly questionable metadata parsing

    // we always parse the .tim and .htm files if they exist, along with
    // either the .csv file or the .zpo file

    if (timPos != -1) {
      tempFile = new File(workingDir, ls[timPos]);
      allFiles.add(tempFile.getAbsolutePath());
      read = new BufferedReader(new FileReader(tempFile));
      data = new char[(int) tempFile.length()];
      read.read(data);
      t = new StringTokenizer(new String(data));
      int tNum = 0;
      // can ignore "Zero x" and "Extra int"
      String[] hashKeys = {"Number of Wavelengths/Timepoints", "Zero 1",
        "Zero 2", "Number of slices", "Extra int", "Calibration Unit",
        "Pixel Size Y", "Pixel Size X", "Image Width", "Image Length",
        "Origin X", "SubfileType X", "Dimension Label X", "Origin Y",
        "SubfileType Y", "Dimension Label Y", "Origin Z",
        "SubfileType Z", "Dimension Label Z"};

      // there are 9 additional tokens, but I don't know what they're for

      while (t.hasMoreTokens() && tNum<hashKeys.length) {
        String token = t.nextToken();
        while ((tNum == 1 || tNum == 2) && !token.trim().equals("0")) {
          tNum++;
        }

        if (tNum == 4) {
          try { Integer.parseInt(token); }
          catch (Exception e) { tNum++; }
        }
        metadata.put(hashKeys[tNum], token);
        tNum++;
      }
    }

    if (csvPos != -1) {
      tempFile = new File(workingDir, ls[csvPos]);
      allFiles.add(tempFile.getAbsolutePath());
      read = new BufferedReader(new FileReader(tempFile));
      data = new char[(int) tempFile.length()];
      read.read(data);
      t = new StringTokenizer(new String(data));
      int tNum = 0;
      String[] hashKeys = {"Calibration Unit", "Pixel Size X", "Pixel Size Y",
        "Z slice space"};
      int pt = 0;
      while (t.hasMoreTokens()) {
        if (tNum < 7) { t.nextToken(); }
        else if ((tNum > 7 && tNum < 12) ||
          (tNum > 12 && tNum < 18) || (tNum > 18 && tNum < 22))
        {
          t.nextToken();
        }
        else if (pt < hashKeys.length) {
          metadata.put(hashKeys[pt], t.nextToken());
          pt++;
        }
        else {
          metadata.put(t.nextToken() + t.nextToken(),
            t.nextToken());
        }
        tNum++;
      }
    }
    else if (zpoPos != -1) {
      tempFile = new File(workingDir, ls[zpoPos]);
      allFiles.add(tempFile.getAbsolutePath());
      read = new BufferedReader(new FileReader(tempFile));
      data = new char[(int) tempFile.length()];
      read.read(data);
      t = new StringTokenizer(new String(data));
      int tNum = 0;
      while (t.hasMoreTokens()) {
        metadata.put("Z slice #" + tNum + " position", t.nextToken());
        tNum++;
      }
    }

    // be aggressive about parsing the HTML file, since it's the only one that
    // explicitly defines the number of wavelengths and timepoints

    if (htmPos != -1) {
      tempFile = new File(workingDir, ls[htmPos]);
      allFiles.add(tempFile.getAbsolutePath());
      read = new BufferedReader(new FileReader(tempFile));
      data = new char[(int) tempFile.length()];
      read.read(data);

      String regex = "<p>|</p>|<br>|<hr>|<b>|</b>|<HTML>|<HEAD>|</HTML>|" +
        "</HEAD>|<h1>|</h1>|<HR>|</body>";

      // use reflection to avoid dependency on Java 1.4-specific split method
      Class c = String.class;
      String[] tokens = new String[0];
      try {
        Method split = c.getMethod("split", new Class[] {c});
        tokens = (String[]) split.invoke(new String(data),
          new Object[] {regex});
      }
      catch (Throwable e) { }

      for (int j=0; j<tokens.length; j++) {
        if (tokens[j].indexOf("<") != -1) tokens[j] = "";
      }

      for (int j=0; j<tokens.length-1; j+=2) {
        if (tokens[j].indexOf("Wavelength") != -1) {
          metadata.put("Camera Data " + tokens[j].charAt(13), tokens[j]);
          j--;
        }
        else if (!tokens[j].trim().equals("")) {
          metadata.put(tokens[j], tokens[j+1]);
        }
      }
    }
    else {
      throw new FormatException("Valid header files not found.");
    }

    String details = (String) metadata.get("Experiment details:");
    // parse details to get number of wavelengths and timepoints

    t = new StringTokenizer(details);
    int tokenNum = 0;
    String wavelengths = "1";
    int numTokens = t.countTokens();
    boolean foundId = false;
    String prevToken = "";
    while (t.hasMoreTokens()) {
      String token = t.nextToken();
      foundId = token.equals("Wavelengths");
      if (foundId) {
        wavelengths = prevToken;
      }
      tokenNum++;
      prevToken = token;
    }

    channels = Integer.parseInt(wavelengths);

    sizeX[0] = Integer.parseInt((String) metadata.get("Image Width"));
    sizeY[0] = Integer.parseInt((String) metadata.get("Image Length"));
    sizeZ[0] = Integer.parseInt((String) metadata.get("Number of slices"));
    sizeC[0] = channels;
    sizeT[0] = getImageCount(currentId) / (sizeZ[0] * sizeC[0]);
    if (isTiff) pixelType[0] = tiff[0].getPixelType(files[0]);
    else {
      int bpp = openBytes(id, 0).length / (sizeX[0] * sizeY[0]);
      switch (bpp) {
        case 1:
          pixelType[0] = FormatReader.INT8;
          break;
        case 2:
          pixelType[0] = FormatReader.UINT16;
          break;
        case 3:
          pixelType[0] = FormatReader.INT8;
          break;
        case 4:
          pixelType[0] = FormatReader.INT32;
          break;
      }
    }

    currentOrder[0] = "XYC";

    if (sizeZ[0] <= 0) {
      sizeZ[0] = 1;
      sizeT[0] = getImageCount(currentId) / (sizeZ[0] * sizeC[0]);
    }
    if (sizeC[0] <= 0) {
      sizeC[0] = 1;
      sizeT[0] = getImageCount(currentId) / (sizeZ[0] * sizeC[0]);
    }
    if (sizeT[0] <= 0) sizeT[0] = 1;

    Object o = metadata.get("Z slice space");
    if (o != null) {
      float spacing = Float.parseFloat(o.toString());
      if (spacing <= 1f) currentOrder[0] += "TZ";
      else currentOrder[0] += "ZT";
    }
    else currentOrder[0] += "ZT"; // doesn't matter, since Z = T = 1 

    // Populate metadata store

    // The metadata store we're working with.
    MetadataStore store = getMetadataStore(id);

    // populate Dimensions element
    String pixelSizeX = (String) metadata.get("Pixel Size X");
    String pixelSizeY = (String) metadata.get("Pixel Size Y");
    store.setDimensions(new Float(pixelSizeX),
        new Float(pixelSizeY), null, null, null, null);

    // populate Image element
    String time = (String) metadata.get("Finish Time:");
    time = time.substring(1).trim();
    store.setImage(null, time, null, null);

    // populate Pixels element
    String x = (String) metadata.get("Image Width");
    String y = (String) metadata.get("Image Length");
    String z = (String) metadata.get("Number of slices");
    store.setPixels(
      new Integer(x), // SizeX
      new Integer(y), // SizeY
      new Integer(z), // SizeZ
      new Integer(wavelengths), // SizeC
      new Integer(getSizeT(id)), // SizeT
      new Integer(pixelType[0]), // PixelType
      null, // BigEndian
      "XYCTZ", // DimensionOrder
      null); // Use index 0

    // populate StageLabel element
    String originX = (String) metadata.get("Origin X");
    String originY = (String) metadata.get("Origin Y");
    String originZ = (String) metadata.get("Origin Z");
    try {
      store.setStageLabel(null, new Float(originX), new Float(originY),
                        new Float(originZ), null);
    }
    catch (Exception e) { }
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new PerkinElmerReader().testRead(args);
  }

}
