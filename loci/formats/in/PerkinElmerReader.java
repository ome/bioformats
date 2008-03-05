//
// PerkinElmerReader.java
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.*;
import java.util.*;
import loci.formats.*;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * PerkinElmerReader is the file format reader for PerkinElmer files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/in/PerkinElmerReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/in/PerkinElmerReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class PerkinElmerReader extends FormatReader {

  // -- Fields --

  /** Helper reader. */
  protected TiffReader[] tiff;

  /** Tiff files to open. */
  protected String[] files;

  /** Flag indicating that the image data is in TIFF format. */
  private boolean isTiff = true;

  /** List of all files to open */
  private Vector allFiles;

  private String details, sliceSpace;

  // -- Constructor --

  /** Constructs a new PerkinElmer reader. */
  public PerkinElmerReader() {
    super("PerkinElmer", new String[] {
      "ano", "cfg", "csv", "htm", "rec", "tim", "zpo"});
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    if (super.isThisType(name, open)) return true; // check extensions

    if (!open) return false; // not allowed to touch the file system

    String ext = name;
    if (ext.indexOf(".") != -1) ext = ext.substring(ext.lastIndexOf(".") + 1);
    boolean binFile = true;
    try {
      Integer.parseInt(ext, 16);
    }
    catch (NumberFormatException e) {
      ext = ext.toLowerCase();
      if (!ext.equals("tif") && !ext.equals("tiff")) binFile = false;
    }

    String prefix = name;
    if (prefix.indexOf(".") != -1) {
      prefix = prefix.substring(0, prefix.lastIndexOf("."));
    }
    if (prefix.indexOf("_") != -1) {
      prefix = prefix.substring(0, prefix.lastIndexOf("_"));
    }

    Location htmlFile = new Location(prefix + ".htm");
    if (!htmlFile.exists()) {
      htmlFile = new Location(prefix + ".HTM");
      while (!htmlFile.exists() && prefix.indexOf("_") != -1) {
        prefix = prefix.substring(0, prefix.indexOf("_"));
        htmlFile = new Location(prefix + ".htm");
        if (!htmlFile.exists()) htmlFile = new Location(prefix + ".HTM");
      }
    }

    return htmlFile.exists() && (binFile || super.isThisType(name, false));
  }

  /* @see loci.formats.IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) { return false; }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    FormatTools.checkPlaneNumber(this, no);
    if (isTiff) {
      tiff[no / core.sizeC[0]].setId(files[no / core.sizeC[0]]);
      return tiff[no / core.sizeC[0]].openBytes(0, buf, x, y, w, h);
    }

    FormatTools.checkBufferSize(this, buf.length, w, h);

    String file = files[no];
    RandomAccessStream ras = new RandomAccessStream(file);

    int bytes = FormatTools.getBytesPerPixel(core.pixelType[0]);
    ras.skipBytes(6 + y * core.sizeX[0] * bytes);

    for (int row=0; row<h; row++) {
      ras.skipBytes(x * bytes);
      ras.read(buf, row * w * bytes, w * bytes);
      ras.skipBytes(bytes * (core.sizeX[0] - w - x));
    }

    ras.close();
    return buf;
  }

  /* @see loci.formats.IFormatReader#getUsedFiles() */
  public String[] getUsedFiles() {
    FormatTools.assertId(currentId, true, 1);
    return (String[]) allFiles.toArray(new String[0]);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    if (fileOnly) {
      if (tiff != null) {
        for (int i=0; i<tiff.length; i++) {
          if (tiff[i] != null) tiff[i].close(fileOnly);
        }
      }
    }
    else close();
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    currentId = null;
    if (tiff != null) {
      for (int i=0; i<tiff.length; i++) {
        if (tiff[i] != null) tiff[i].close();
      }
    }
    tiff = null;
    allFiles = null;
    files = null;
    details = sliceSpace = null;
    isTiff = true;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    if (currentId != null && (id.equals(currentId) || isUsedFile(id))) return;

    status("Finding HTML companion file");

    if (debug) debug("PerkinElmerReader.initFile(" + id + ")");
    // always init on the HTML file - this prevents complications with
    // initializing the image files

    if (!id.toLowerCase().endsWith(".htm")) {
      Location parent = new Location(id).getAbsoluteFile().getParentFile();
      String[] ls = parent.list();
      for (int i=0; i<ls.length; i++) {
        if (ls[i].toLowerCase().endsWith(".htm")) {
          id = new Location(parent.getAbsolutePath(), ls[i]).getAbsolutePath();
          break;
        }
      }
    }

    super.initFile(id);

    allFiles = new Vector();

    // get the working directory
    Location tempFile = new Location(id).getAbsoluteFile();
    Location workingDir = tempFile.getParentFile();
    if (workingDir == null) workingDir = new Location(".");
    String workingDirPath = workingDir.getPath() + File.separator;
    String[] ls = workingDir.list();

    float pixelSizeX = 1f, pixelSizeY = 1f;
    String finishTime = null, startTime = null;
    float originX = 0f, originY = 0f, originZ = 0f;

    status("Searching for all metadata companion files");

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
      while (d == -1 && i < ls.length - 1) {
        i++;
        d = ls[i].lastIndexOf(".");
      }
      String s = dot < 0 ? ls[i] : ls[i].substring(0, d);

      String filename = ls[i].toLowerCase();

      if (s.startsWith(check) || check.startsWith(s) ||
        ((prefix != null) && (s.startsWith(prefix))))
      {
        if (cfgPos == -1 && filename.endsWith(".cfg")) {
          cfgPos = i;
          prefix = ls[i].substring(0, d);
        }
        if (anoPos == -1 && filename.endsWith(".ano")) {
          anoPos = i;
          prefix = ls[i].substring(0, d);
        }
        if (recPos == -1 && filename.endsWith(".rec")) {
          recPos = i;
          prefix = ls[i].substring(0, d);
        }
        if (timPos == -1 && filename.endsWith(".tim")) {
          timPos = i;
          prefix = ls[i].substring(0, d);
        }
        if (csvPos == -1 && filename.endsWith(".csv")) {
          csvPos = i;
          prefix = ls[i].substring(0, d);
        }
        if (zpoPos == -1 && filename.endsWith(".zpo")) {
          zpoPos = i;
          prefix = ls[i].substring(0, d);
        }
        if (htmPos == -1 && filename.endsWith(".htm")) {
          htmPos = i;
          prefix = ls[i].substring(0, d);
        }

        if (filename.endsWith(".tif") || filename.endsWith(".tiff")) {
          files[filesPt] = workingDirPath + ls[i];
          filesPt++;
        }

        try {
          String ext = filename.substring(filename.lastIndexOf(".") + 1);
          Integer.parseInt(ext);
          isTiff = false;
          files[filesPt] = workingDirPath + ls[i];
          filesPt++;
        }
        catch (NumberFormatException e) {
          try {
            String ext = filename.substring(filename.lastIndexOf(".") + 1);
            Integer.parseInt(ext, 16);
            isTiff = false;
            files[filesPt] = workingDirPath + ls[i];
            filesPt++;
          }
          catch (NumberFormatException exc) {
            if (debug) trace(exc);
          }
        }
      }
    }

    // re-order the files

    String[] tempFiles = files;
    files = new String[filesPt];

    // determine the number of different extensions we have

    status("Finding image files");

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

    core.imageCount[0] = files.length;
    RandomAccessStream read;
    byte[] data;
    StringTokenizer t;

    tiff = new TiffReader[core.imageCount[0]];
    for (int i=0; i<tiff.length; i++) {
      tiff[i] = new TiffReader();
      if (i > 0) tiff[i].setMetadataCollected(false);
    }

    // we always parse the .tim and .htm files if they exist, along with
    // either the .csv file or the .zpo file

    status("Parsing metadata values");

    if (cfgPos != -1) {
      tempFile = new Location(workingDir, ls[cfgPos]);
      allFiles.add(tempFile.getAbsolutePath());
    }
    if (anoPos != -1) {
      tempFile = new Location(workingDir, ls[anoPos]);
      allFiles.add(tempFile.getAbsolutePath());
    }
    if (recPos != -1) {
      tempFile = new Location(workingDir, ls[recPos]);
      allFiles.add(tempFile.getAbsolutePath());
    }

    if (timPos != -1) {
      tempFile = new Location(workingDir, ls[timPos]);
      allFiles.add(tempFile.getAbsolutePath());
      read = new RandomAccessStream(tempFile.getAbsolutePath());
      data = new byte[(int) tempFile.length()];
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
          catch (NumberFormatException e) { tNum++; }
        }
        addMeta(hashKeys[tNum], token);
        if (hashKeys[tNum].equals("Image Width")) {
          core.sizeX[0] = Integer.parseInt(token);
        }
        else if (hashKeys[tNum].equals("Image Length")) {
          core.sizeY[0] = Integer.parseInt(token);
        }
        else if (hashKeys[tNum].equals("Number of slices")) {
          core.sizeZ[0] = Integer.parseInt(token);
        }
        else if (hashKeys[tNum].equals("Experiment details:")) details = token;
        else if (hashKeys[tNum].equals("Z slice space")) sliceSpace = token;
        else if (hashKeys[tNum].equals("Pixel Size X")) {
          pixelSizeX = Float.parseFloat(token);
        }
        else if (hashKeys[tNum].equals("Pixel Size Y")) {
          pixelSizeY = Float.parseFloat(token);
        }
        else if (hashKeys[tNum].equals("Finish Time:")) finishTime = token;
        else if (hashKeys[tNum].equals("Start Time:")) startTime = token;
        else if (hashKeys[tNum].equals("Origin X")) {
          try {
            originX = Float.parseFloat(token);
          }
          catch (NumberFormatException e) { }
        }
        else if (hashKeys[tNum].equals("Origin Y")) {
          try {
            originY = Float.parseFloat(token);
          }
          catch (NumberFormatException e) { }
        }
        else if (hashKeys[tNum].equals("Origin Z")) {
          try {
            originZ = Float.parseFloat(token);
          }
          catch (NumberFormatException e) { }
        }
        tNum++;
      }
      read.close();
    }

    if (csvPos != -1) {
      tempFile = new Location(workingDir, ls[csvPos]);
      allFiles.add(tempFile.getAbsolutePath());
      read = new RandomAccessStream(tempFile.getAbsolutePath());
      data = new byte[(int) tempFile.length()];
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
          String token = t.nextToken();
          addMeta(hashKeys[pt], token);
          if (hashKeys[pt].equals("Image Width")) {
            core.sizeX[0] = Integer.parseInt(token);
          }
          else if (hashKeys[pt].equals("Image Length")) {
            core.sizeY[0] = Integer.parseInt(token);
          }
          else if (hashKeys[pt].equals("Number of slices")) {
            core.sizeZ[0] = Integer.parseInt(token);
          }
          else if (hashKeys[pt].equals("Experiment details:")) details = token;
          else if (hashKeys[pt].equals("Z slice space")) sliceSpace = token;
          else if (hashKeys[pt].equals("Pixel Size X")) {
            pixelSizeX = Float.parseFloat(token);
          }
          else if (hashKeys[pt].equals("Pixel Size Y")) {
            pixelSizeY = Float.parseFloat(token);
          }
          else if (hashKeys[pt].equals("Finish Time:")) finishTime = token;
          else if (hashKeys[pt].equals("Start Time:")) startTime = token;
          else if (hashKeys[pt].equals("Origin X")) {
            originX = Float.parseFloat(token);
          }
          else if (hashKeys[pt].equals("Origin Y")) {
            originY = Float.parseFloat(token);
          }
          else if (hashKeys[pt].equals("Origin Z")) {
            originZ = Float.parseFloat(token);
          }
          pt++;
        }
        else {
          String key = t.nextToken() + t.nextToken();
          String value = t.nextToken();
          addMeta(key, value);
          if (key.equals("Image Width")) {
            core.sizeX[0] = Integer.parseInt(value);
          }
          else if (key.equals("Image Length")) {
            core.sizeY[0] = Integer.parseInt(value);
          }
          else if (key.equals("Number of slices")) {
            core.sizeZ[0] = Integer.parseInt(value);
          }
          else if (key.equals("Experiment details:")) details = value;
          else if (key.equals("Z slice space")) sliceSpace = value;
          else if (key.equals("Pixel Size X")) {
            pixelSizeX = Float.parseFloat(value);
          }
          else if (key.equals("Pixel Size Y")) {
            pixelSizeY = Float.parseFloat(value);
          }
          else if (key.equals("Finish Time:")) finishTime = value;
          else if (key.equals("Start Time:")) startTime = value;
          else if (key.equals("Origin X")) originX = Float.parseFloat(value);
          else if (key.equals("Origin Y")) originY = Float.parseFloat(value);
          else if (key.equals("Origin Z")) originZ = Float.parseFloat(value);
        }
        tNum++;
      }
      read.close();
    }
    if (zpoPos != -1) {
      tempFile = new Location(workingDir, ls[zpoPos]);
      allFiles.add(tempFile.getAbsolutePath());
      if (csvPos < 0) {
        // parse .zpo only if no .csv is available
        read = new RandomAccessStream(tempFile.getAbsolutePath());
        data = new byte[(int) tempFile.length()];
        read.read(data);
        t = new StringTokenizer(new String(data));
        int tNum = 0;
        while (t.hasMoreTokens()) {
          addMeta("Z slice #" + tNum + " position", t.nextToken());
          tNum++;
        }
        read.close();
      }
    }

    // be aggressive about parsing the HTML file, since it's the only one that
    // explicitly defines the number of wavelengths and timepoints

    Vector exposureTimes = new Vector();
    Vector zPositions = new Vector();
    Vector emWaves = new Vector();
    Vector exWaves = new Vector();

    if (htmPos != -1) {
      tempFile = new Location(workingDir, ls[htmPos]);
      allFiles.add(tempFile.getAbsolutePath());
      read = new RandomAccessStream(tempFile.getAbsolutePath());
      data = new byte[(int) tempFile.length()];
      read.read(data);

      String regex = "<p>|</p>|<br>|<hr>|<b>|</b>|<HTML>|<HEAD>|</HTML>|" +
        "</HEAD>|<h1>|</h1>|<HR>|</body>";

      // use reflection to avoid dependency on Java 1.4-specific split method
      Class c = String.class;
      String[] tokens = new String[0];
      Throwable th = null;
      try {
        Method split = c.getMethod("split", new Class[] {c});
        tokens = (String[]) split.invoke(new String(data),
          new Object[] {regex});
      }
      catch (NoSuchMethodException exc) { if (debug) trace(exc); }
      catch (IllegalAccessException exc) { if (debug) trace(exc); }
      catch (InvocationTargetException exc) { if (debug) trace(exc); }

      for (int j=0; j<tokens.length; j++) {
        if (tokens[j].indexOf("<") != -1) tokens[j] = "";
      }

      for (int j=0; j<tokens.length-1; j+=2) {
        if (tokens[j].indexOf("Exposure") != -1) {
          addMeta("Camera Data " + tokens[j].charAt(13), tokens[j]);

          int ndx = tokens[j].indexOf("Exposure") + 9;
          String exposure =
            tokens[j].substring(ndx, tokens[j].indexOf(" ", ndx)).trim();
          if (exposure.endsWith(",")) {
            exposure = exposure.substring(0, exposure.length() - 1);
          }
          exposureTimes.add(new Float(exposure));

          if (tokens[j].indexOf("nm") != -1) {
            int nmIndex = tokens[j].indexOf("nm");
            int paren = tokens[j].lastIndexOf("(", nmIndex);
            int slash = tokens[j].lastIndexOf("/", nmIndex);
            if (slash == -1) slash = nmIndex;
            emWaves.add(
              new Integer(tokens[j].substring(paren + 1, slash).trim()));
            if (tokens[j].indexOf("nm", nmIndex + 3) != -1) {
              nmIndex = tokens[j].indexOf("nm", nmIndex + 3);
              paren = tokens[j].lastIndexOf(" ", nmIndex);
              slash = tokens[j].lastIndexOf("/", nmIndex);
              if (slash == -1) slash = nmIndex + 2;
              exWaves.add(
                new Integer(tokens[j].substring(paren + 1, slash).trim()));
            }
          }

          j--;
        }
        else if (tokens[j + 1].trim().equals("Slice Z positions")) {
          for (int q=j + 2; q<tokens.length; q++) {
            if (!tokens[q].trim().equals("")) {
              zPositions.add(new Float(tokens[q].trim()));
            }
          }
        }
        else if (!tokens[j].trim().equals("")) {
          tokens[j] = tokens[j].trim();
          tokens[j + 1] = tokens[j + 1].trim();
          addMeta(tokens[j], tokens[j + 1]);
          if (tokens[j].equals("Image Width")) {
            core.sizeX[0] = Integer.parseInt(tokens[j + 1]);
          }
          else if (tokens[j].equals("Image Length")) {
            core.sizeY[0] = Integer.parseInt(tokens[j + 1]);
          }
          else if (tokens[j].equals("Number of slices")) {
            core.sizeZ[0] = Integer.parseInt(tokens[j + 1]);
          }
          else if (tokens[j].equals("Experiment details:")) {
            details = tokens[j + 1];
          }
          else if (tokens[j].equals("Z slice space")) {
            sliceSpace = tokens[j + 1];
          }
          else if (tokens[j].equals("Pixel Size X")) {
            pixelSizeX = Float.parseFloat(tokens[j + 1]);
          }
          else if (tokens[j].equals("Pixel Size Y")) {
            pixelSizeY = Float.parseFloat(tokens[j + 1]);
          }
          else if (tokens[j].equals("Finish Time:")) finishTime = tokens[j + 1];
          else if (tokens[j].equals("Start Time:")) startTime = tokens[j + 1];
          else if (tokens[j].equals("Origin X")) {
            originX = Float.parseFloat(tokens[j + 1]);
          }
          else if (tokens[j].equals("Origin Y")) {
            originY = Float.parseFloat(tokens[j + 1]);
          }
          else if (tokens[j].equals("Origin Z")) {
            originZ = Float.parseFloat(tokens[j + 1]);
          }
        }
      }
      read.close();
    }
    else {
      throw new FormatException("Valid header files not found.");
    }

    // parse details to get number of wavelengths and timepoints

    String wavelengths = "1";
    if (details != null) {
      t = new StringTokenizer(details);
      int tokenNum = 0;
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
    }

    status("Populating metadata");

    if (core.sizeZ[0] <= 0) core.sizeZ[0] = 1;

    core.sizeC[0] = Integer.parseInt(wavelengths);

    core.sizeT[0] = getImageCount() / (core.sizeZ[0] * core.sizeC[0]);
    if (isTiff) {
      tiff[0].setId(files[0]);
      core.pixelType[0] = tiff[0].getPixelType();
    }
    else {
      RandomAccessStream tmp = new RandomAccessStream(files[0]);
      int bpp = (int) (tmp.length() - 6) / (core.sizeX[0] * core.sizeY[0]);
      tmp.close();
      switch (bpp) {
        case 1:
        case 3:
          core.pixelType[0] = FormatTools.UINT8;
          break;
        case 2:
          core.pixelType[0] = FormatTools.UINT16;
          break;
        case 4:
          core.pixelType[0] = FormatTools.UINT32;
          break;
      }
    }

    core.currentOrder[0] = "XYCTZ";

    core.rgb[0] = isTiff ? tiff[0].isRGB() : false;
    core.interleaved[0] = false;
    core.littleEndian[0] = isTiff ? tiff[0].isLittleEndian() : true;
    core.metadataComplete[0] = true;
    core.indexed[0] = isTiff ? tiff[0].isIndexed() : false;
    core.falseColor[0] = false;

    // Populate metadata store

    // The metadata store we're working with.
    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    store.setImageName("", 0);

    // populate Dimensions element
    store.setDimensionsPhysicalSizeX(new Float(pixelSizeX), 0, 0);
    store.setDimensionsPhysicalSizeY(new Float(pixelSizeY), 0, 0);

    // populate Image element
    if (finishTime != null) {
      SimpleDateFormat parse = new SimpleDateFormat("HH:mm:ss (MM/dd/yyyy)");
      Date date = parse.parse(finishTime, new ParsePosition(0));
      SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      finishTime = fmt.format(date);
    }
    store.setImageCreationDate(finishTime, 0);

    // populate Pixels element
    MetadataTools.populatePixels(store, this);

    // populate LogicalChannel element
    for (int i=0; i<core.sizeC[0]; i++) {
      if (i < emWaves.size()) {
        store.setLogicalChannelEmWave((Integer) emWaves.get(i), 0, i);
      }
      if (i < exWaves.size()) {
        store.setLogicalChannelExWave((Integer) exWaves.get(i), 0, i);
      }
    }

    // populate plane info

    long start = 0, end = 0;
    if (startTime != null) {
      SimpleDateFormat parse = new SimpleDateFormat("HH:mm:ss (MM/dd/yyyy)");
      Date date = parse.parse(startTime, new ParsePosition(0));
      start = date.getTime();
    }
    if (finishTime != null) {
      SimpleDateFormat parse = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
      Date date = parse.parse(finishTime, new ParsePosition(0));
      end = date.getTime();
    }
    long range = end - start;
    float msPerPlane = (float) range / core.imageCount[0];

    int plane = 0;
    for (int zi=0; zi<core.sizeZ[0]; zi++) {
      for (int ti=0; ti<core.sizeT[0]; ti++) {
        for (int ci=0; ci<core.sizeC[0]; ci++) {
          store.setPlaneTheZ(new Integer(zi), 0, 0, plane);
          store.setPlaneTheC(new Integer(ci), 0, 0, plane);
          store.setPlaneTheT(new Integer(ti), 0, 0, plane);
          store.setPlaneTimingDeltaT(new Float((plane * msPerPlane) / 1000),
            0, 0, plane);
          store.setPlaneTimingExposureTime(
            (Float) exposureTimes.get(ci), 0, 0, plane);
          if (zi < zPositions.size()) {
            store.setStagePositionPositionX(new Float(0.0), 0, 0, plane);
            store.setStagePositionPositionY(new Float(0.0), 0, 0, plane);
            store.setStagePositionPositionZ((Float) zPositions.get(zi),
              0, 0, plane);
          }
          plane++;
        }
      }
    }

    // populate StageLabel element
    /*
    store.setStageLabelX(new Float(originX), 0);
    store.setStageLabelY(new Float(originY), 0);
    store.setStageLabelZ(new Float(originZ), 0);
    */

  }

}
