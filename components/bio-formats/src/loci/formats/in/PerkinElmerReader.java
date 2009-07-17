//
// PerkinElmerReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Vector;

import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * PerkinElmerReader is the file format reader for PerkinElmer files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/PerkinElmerReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/PerkinElmerReader.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class PerkinElmerReader extends FormatReader {

  // -- Constants --

  public static final String[] CFG_SUFFIX = {"cfg"};
  public static final String[] ANO_SUFFIX = {"ano"};
  public static final String[] REC_SUFFIX = {"rec"};
  public static final String[] TIM_SUFFIX = {"tim"};
  public static final String[] CSV_SUFFIX = {"csv"};
  public static final String[] ZPO_SUFFIX = {"zpo"};
  public static final String[] HTM_SUFFIX = {"htm"};

  public static final String HTML_REGEX =
    "<p>|</p>|<br>|<hr>|<b>|</b>|<HTML>|<HEAD>|</HTML>|" +
    "</HEAD>|<h1>|</h1>|<HR>|</body>";

  public static final String DATE_FORMAT = "HH:mm:ss (MM/dd/yyyy)";

  // -- Fields --

  /** Helper reader. */
  protected MinimalTiffReader tiff;

  /** Tiff files to open. */
  protected String[] files;

  /** Flag indicating that the image data is in TIFF format. */
  private boolean isTiff = true;

  /** List of all files to open */
  private Vector<String> allFiles;

  private String details, sliceSpace;

  private float pixelSizeX = 1f, pixelSizeY = 1f;
  private String finishTime = null, startTime = null;
  private float originX = 0f, originY = 0f, originZ = 0f;

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

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    return false;
  }

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
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);
    if (isTiff) {
      tiff.setId(files[no / getSizeC()]);
      return tiff.openBytes(0, buf, x, y, w, h);
    }

    RandomAccessInputStream ras = new RandomAccessInputStream(files[no]);
    ras.seek(6);

    readPlane(ras, x, y, w, h, buf);
    ras.close();
    return buf;
  }

  /* @see loci.formats.IFormatReader#getUsedFiles(boolean) */
  public String[] getUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    if (noPixels) {
      Vector<String> files = new Vector<String>();
      if (isTiff) {
        for (int i=0; i<allFiles.size(); i++) {
          String f = allFiles.get(i).toLowerCase();
          if (!f.endsWith(".tif") && !f.endsWith(".tiff")) {
            files.add(allFiles.get(i));
          }
        }
      }
      else {
        for (int i=0; i<allFiles.size(); i++) {
          String f = allFiles.get(i);
          String ext = f.substring(f.lastIndexOf(".") + 1);
          try {
            Integer.parseInt(ext, 16);
          }
          catch (NumberFormatException e) { files.add(f); }
        }
      }
      return files.toArray(new String[0]);
    }
    return allFiles.toArray(new String[0]);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    if (fileOnly) {
      if (tiff != null) tiff.close();
    }
    else close();
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    currentId = null;
    if (tiff != null) tiff.close();
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

    debug("PerkinElmerReader.initFile(" + id + ")");
    // always init on the HTML file - this prevents complications with
    // initializing the image files

    if (!checkSuffix(id, HTM_SUFFIX)) {
      Location parent = new Location(id).getAbsoluteFile().getParentFile();
      String[] ls = parent.list();
      for (String file : ls) {
        if (checkSuffix(file, HTM_SUFFIX) && !file.startsWith(".")) {
          id = new Location(parent.getAbsolutePath(), file).getAbsolutePath();
          break;
        }
      }
    }

    super.initFile(id);

    allFiles = new Vector<String>();

    // get the working directory
    Location tmpFile = new Location(id).getAbsoluteFile();
    Location workingDir = tmpFile.getParentFile();
    if (workingDir == null) workingDir = new Location(".");
    String workingDirPath = workingDir.getPath();
    if (!workingDirPath.equals("")) workingDirPath += File.separator;
    String[] ls = workingDir.list();
    if (!new Location(id).exists()) {
      ls = (String[]) Location.getIdMap().keySet().toArray(new String[0]);
      workingDirPath = "";
    }

    // remove files that start with '.'

    Vector<String> v = new Vector<String>();
    for (int i=0; i<ls.length; i++) {
      String file = ls[i];
      if (file.indexOf(File.separator) != -1) {
        file = file.substring(file.lastIndexOf(File.separator) + 1);
      }
      if (!file.startsWith(".")) {
        v.add(ls[i]);
      }
    }
    ls = v.toArray(new String[0]);

    status("Searching for all metadata companion files");

    // check if we have any of the required header file types

    String cfgFile = null, anoFile = null, recFile = null;
    String timFile = null, csvFile = null, zpoFile = null;
    String htmFile = null;

    int filesPt = 0;
    files = new String[ls.length];

    int dot = id.lastIndexOf(".");
    String check = dot < 0 ? id : id.substring(0, dot);
    check = check.substring(check.lastIndexOf(File.separator) + 1);

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
      String s = d < 0 ? ls[i] : ls[i].substring(0, d);

      if (s.startsWith(check) || check.startsWith(s) ||
        ((prefix != null) && (s.startsWith(prefix))))
      {
        prefix = ls[i].substring(0, d);
        if (cfgFile == null && checkSuffix(ls[i], CFG_SUFFIX)) cfgFile = ls[i];
        if (anoFile == null && checkSuffix(ls[i], ANO_SUFFIX)) anoFile = ls[i];
        if (recFile == null && checkSuffix(ls[i], REC_SUFFIX)) recFile = ls[i];
        if (timFile == null && checkSuffix(ls[i], TIM_SUFFIX)) timFile = ls[i];
        if (csvFile == null && checkSuffix(ls[i], CSV_SUFFIX)) csvFile = ls[i];
        if (zpoFile == null && checkSuffix(ls[i], ZPO_SUFFIX)) zpoFile = ls[i];
        if (htmFile == null && checkSuffix(ls[i], HTM_SUFFIX)) htmFile = ls[i];

        if (checkSuffix(ls[i], TiffReader.TIFF_SUFFIXES)) {
          files[filesPt++] = workingDirPath + ls[i];
        }

        try {
          String ext = ls[i].substring(ls[i].lastIndexOf(".") + 1);
          Integer.parseInt(ext, 16);
          isTiff = false;
          files[filesPt++] = workingDirPath + ls[i];
        }
        catch (NumberFormatException exc) {
          traceDebug(exc);
        }
      }
    }

    // re-order the files

    String[] tempFiles = files;
    files = new String[filesPt];

    // determine the number of different extensions we have

    status("Finding image files");

    int extCount = 0;
    Vector<String> foundExts = new Vector<String>();
    for (int i=0; i<filesPt; i++) {
      String ext = tempFiles[i].substring(tempFiles[i].lastIndexOf(".") + 1);
      if (!foundExts.contains(ext)) {
        extCount++;
        foundExts.add(ext);
      }
    }
    foundExts = null;

    for (int i=0; i<filesPt; i+=extCount) {
      Vector<String> extSet = new Vector<String>();
      for (int j=0; j<extCount; j++) {
        String file = tempFiles[i + j];
        if (extSet.size() == 0) extSet.add(file);
        else {
          if (file == null) continue;
          String ext = file.substring(file.lastIndexOf(".") + 1);
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

      int length = (int) Math.min(extCount, extSet.size());
      for (int j=0; j<length; j++) {
        files[i+j] = (String) extSet.get(j);
      }
    }

    for (int i=0; i<files.length; i++) allFiles.add(files[i]);
    if (isTiff) Arrays.sort(files);
    else {
      Comparator c = new Comparator() {
        public int compare(Object o1, Object o2) {
          String s1 = (String) o1;
          String s2 = (String) o2;
          String prefix1 = s1, prefix2 = s2, suffix1 = s1, suffix2 = s2;
          if (s1.indexOf(".") != -1) {
            prefix1 = s1.substring(0, s1.lastIndexOf("."));
            suffix1 = s1.substring(s1.lastIndexOf(".") + 1);
          }
          if (s2.indexOf(".") != -1) {
            prefix2 = s2.substring(0, s2.lastIndexOf("."));
            suffix2 = s2.substring(s2.lastIndexOf(".") + 1);
          }
          int cmp = prefix1.compareTo(prefix2);
          if (cmp != 0) return cmp;
          return Integer.parseInt(suffix1, 16) - Integer.parseInt(suffix2, 16);
        }
      };
      Arrays.sort(files, c);
    }

    core[0].imageCount = files.length;
    RandomAccessInputStream read;
    byte[] data;

    tiff = new MinimalTiffReader();

    // we always parse the .tim and .htm files if they exist, along with
    // either the .csv file or the .zpo file

    status("Parsing metadata values");

    addUsedFile(workingDirPath, cfgFile);
    addUsedFile(workingDirPath, anoFile);
    addUsedFile(workingDirPath, recFile);
    addUsedFile(workingDirPath, timFile);
    if (timFile != null) timFile = allFiles.get(allFiles.size() - 1);
    addUsedFile(workingDirPath, csvFile);
    if (csvFile != null) csvFile = allFiles.get(allFiles.size() - 1);
    addUsedFile(workingDirPath, zpoFile);
    if (zpoFile != null) zpoFile = allFiles.get(allFiles.size() - 1);
    addUsedFile(workingDirPath, htmFile);
    if (htmFile != null) htmFile = allFiles.get(allFiles.size() - 1);

    if (timFile != null) {
      read = new RandomAccessInputStream(timFile);
      String[] tokens = read.readString((int) read.length()).split("\\s");
      int tNum = 0;
      // can ignore "Zero x" and "Extra int"
      String[] hashKeys = {"Number of Wavelengths/Timepoints", "Zero 1",
        "Zero 2", "Number of slices", "Extra int", "Calibration Unit",
        "Pixel Size Y", "Pixel Size X", "Image Width", "Image Length",
        "Origin X", "SubfileType X", "Dimension Label X", "Origin Y",
        "SubfileType Y", "Dimension Label Y", "Origin Z",
        "SubfileType Z", "Dimension Label Z"};

      // there are 9 additional tokens, but I don't know what they're for

      for (String token : tokens) {
        if (token.trim().length() == 0) continue;
        if (tNum >= hashKeys.length) break;
        if (token.equals("um")) tNum = 5;
        while ((tNum == 1 || tNum == 2) && !token.trim().equals("0")) {
          tNum++;
        }
        if (tNum == 4) {
          try {
            Integer.parseInt(token);
          }
          catch (NumberFormatException e) {
            tNum++;
          }
        }
        parseKeyValue(hashKeys[tNum++], token);
      }

      read.close();
    }

    if (csvFile != null) {
      read = new RandomAccessInputStream(csvFile);
      String[] tokens = read.readString((int) read.length()).split("\\s");
      Vector<String> tmp = new Vector<String>();
      for (String token : tokens) {
        if (token.trim().length() > 0) tmp.add(token.trim());
      }
      tokens = tmp.toArray(new String[0]);
      read.close();

      int tNum = 0;
      String[] hashKeys = {"Calibration Unit", "Pixel Size X", "Pixel Size Y",
        "Z slice space"};
      int pt = 0;
      for (int j=0; j<tokens.length;) {
        String key = null, value = null;
        if (tNum < 7) { j++; }
        else if ((tNum > 7 && tNum < 12) ||
          (tNum > 12 && tNum < 18) || (tNum > 18 && tNum < 22))
        {
          j++;
        }
        else if (pt < hashKeys.length) {
          key = hashKeys[pt++];
          value = tokens[j++];
        }
        else {
          key = tokens[j++] + tokens[j++];
          value = tokens[j++];
        }

        parseKeyValue(key, value);
        tNum++;
      }
    }

    if (zpoFile != null && csvFile == null) {
      // parse .zpo only if no .csv is available
      read = new RandomAccessInputStream(zpoFile);
      String[] tokens = read.readString((int) read.length()).split("\\s");
      for (int t=0; t<tokens.length; t++) {
        addGlobalMeta("Z slice #" + t + " position", tokens[t]);
      }
      read.close();
    }

    // be aggressive about parsing the HTML file, since it's the only one that
    // explicitly defines the number of wavelengths and timepoints

    Vector<Float> exposureTimes = new Vector<Float>();
    Vector<Float> zPositions = new Vector<Float>();
    Vector<Integer> emWaves = new Vector<Integer>();
    Vector<Integer> exWaves = new Vector<Integer>();

    if (htmFile != null) {
      read = new RandomAccessInputStream(htmFile);
      String[] tokens = read.readString((int) read.length()).split(HTML_REGEX);
      read.close();

      for (int j=0; j<tokens.length; j++) {
        if (tokens[j].indexOf("<") != -1) tokens[j] = "";
      }

      for (int j=0; j<tokens.length-1; j+=2) {
        if (tokens[j].indexOf("Exposure") != -1) {
          addGlobalMeta("Camera Data " + tokens[j].charAt(13), tokens[j]);

          int ndx = tokens[j].indexOf("Exposure") + 9;
          String exposure =
            tokens[j].substring(ndx, tokens[j].indexOf(" ", ndx)).trim();
          if (exposure.endsWith(",")) {
            exposure = exposure.substring(0, exposure.length() - 1);
          }
          exposureTimes.add(new Float(Float.parseFloat(exposure) / 1000));

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
          parseKeyValue(tokens[j], tokens[j + 1]);
        }
      }
    }
    else {
      throw new FormatException("Valid header files not found.");
    }

    // parse details to get number of wavelengths and timepoints

    if (details != null) {
      String[] tokens = details.split("\\s");
      int n = 0;
      for (String token : tokens) {
        if (token.equals("Wavelengths")) core[0].sizeC = n;
        else if (token.equals("Frames")) core[0].sizeT = n;
        else if (token.equals("Slices")) core[0].sizeZ = n;
        try {
          n = Integer.parseInt(token);
        }
        catch (NumberFormatException e) { n = 0; }
      }
    }

    status("Populating metadata");

    if (isTiff) {
      tiff.setId(files[0]);
      core[0].pixelType = tiff.getPixelType();
    }
    else {
      RandomAccessInputStream tmp = new RandomAccessInputStream(files[0]);
      int bpp = (int) (tmp.length() - 6) / (getSizeX() * getSizeY());
      tmp.close();
      switch (bpp) {
        case 1:
        case 3:
          core[0].pixelType = FormatTools.UINT8;
          break;
        case 2:
          core[0].pixelType = FormatTools.UINT16;
          break;
        case 4:
          core[0].pixelType = FormatTools.UINT32;
          break;
      }
    }

    if (getSizeZ() <= 0) core[0].sizeZ = 1;
    if (getSizeC() <= 0) core[0].sizeC = 1;

    if (getSizeT() <= 0) {
      core[0].sizeT = getImageCount() / (getSizeZ() * getSizeC());
    }
    else {
      core[0].imageCount = getSizeZ() * getSizeC() * getSizeT();
      if (getImageCount() > files.length) {
        core[0].imageCount = files.length;
        core[0].sizeT = getImageCount() / (getSizeZ() * getSizeC());
      }
    }

    // throw away files, if necessary

    int calcCount = getSizeZ() * getEffectiveSizeC() * getSizeT();
    if (files.length > getImageCount() || getImageCount() != calcCount) {
      status("Removing extraneous files");
      String[] tmpFiles = files;
      int imageCount = (int) Math.min(getImageCount(), calcCount);
      files = new String[imageCount];

      Hashtable<String, Integer> zSections = new Hashtable<String, Integer>();
      for (int i=0; i<tmpFiles.length; i++) {
        int underscore = tmpFiles[i].lastIndexOf("_");
        int dotIndex = tmpFiles[i].lastIndexOf(".");
        String z = tmpFiles[i].substring(underscore + 1, dotIndex);
        if (zSections.get(z) == null) zSections.put(z, new Integer(1));
        else {
          int count = zSections.get(z).intValue() + 1;
          zSections.put(z, new Integer(count));
        }
      }

      int nextFile = 0;
      int oldFile = 0;
      Arrays.sort(tmpFiles, new PEComparator());
      String[] keys = zSections.keySet().toArray(new String[0]);
      Arrays.sort(keys);
      for (int i=0; i<keys.length; i++) {
        int oldCount = zSections.get(keys[i]).intValue();
        int nPlanes =
          (isTiff ? tiff.getEffectiveSizeC() : getSizeC()) * getSizeT();
        int count = (int) Math.min(oldCount, nPlanes);
        for (int j=0; j<count; j++) {
          files[nextFile++] = tmpFiles[oldFile++];
        }
        if (count < oldCount) oldFile += (oldCount - count);
      }
      core[0].imageCount = getSizeZ() * getEffectiveSizeC() * getSizeT();
    }

    core[0].dimensionOrder = "XYCTZ";
    core[0].rgb = isTiff ? tiff.isRGB() : false;
    core[0].interleaved = false;
    core[0].littleEndian = isTiff ? tiff.isLittleEndian() : true;
    core[0].metadataComplete = true;
    core[0].indexed = isTiff ? tiff.isIndexed() : false;
    core[0].falseColor = false;

    // Populate metadata store

    // The metadata store we're working with.
    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this, true);

    // populate Dimensions element
    store.setDimensionsPhysicalSizeX(new Float(pixelSizeX), 0, 0);
    store.setDimensionsPhysicalSizeY(new Float(pixelSizeY), 0, 0);

    // populate Image element
    store.setImageName("", 0);
    if (finishTime != null) {
      finishTime = DateTools.formatDate(finishTime, DATE_FORMAT);
      store.setImageCreationDate(finishTime, 0);
    }
    else MetadataTools.setDefaultCreationDate(store, id, 0);

    // link Instrument and Image
    store.setInstrumentID("Instrument:0", 0);
    store.setImageInstrumentRef("Instrument:0", 0);

    // populate LogicalChannel element
    for (int i=0; i<getEffectiveSizeC(); i++) {
      if (i < emWaves.size()) {
        store.setLogicalChannelEmWave(emWaves.get(i), 0, i);
      }
      if (i < exWaves.size()) {
        store.setLogicalChannelExWave(exWaves.get(i), 0, i);
      }
    }

    // populate PlaneTiming and StagePosition

    long start = 0, end = 0;
    if (startTime != null) {
      start = DateTools.getTime(startTime, DATE_FORMAT);
    }
    if (finishTime != null) {
      end = DateTools.getTime(finishTime, DateTools.ISO8601_FORMAT);
    }

    float secondsPerPlane = ((float) (end - start) / getImageCount()) / 1000f;

    for (int i=0; i<getImageCount(); i++) {
      int[] zct = getZCTCoords(i);
      store.setPlaneTimingDeltaT(new Float(i * secondsPerPlane), 0, 0, i);
      if (zct[1] < exposureTimes.size()) {
        store.setPlaneTimingExposureTime(exposureTimes.get(zct[1]), 0, 0, i);
      }

      if (zct[0] < zPositions.size()) {
        store.setStagePositionPositionX(new Float(0.0), 0, 0, i);
        store.setStagePositionPositionY(new Float(0.0), 0, 0, i);
        store.setStagePositionPositionZ(zPositions.get(zct[0]), 0, 0, i);
      }
    }
  }

  // -- Helper methods --

  private void parseKeyValue(String key, String value) {
    if (key == null || value == null) return;
    addGlobalMeta(key, value);
    try {
      if (key.equals("Image Width")) {
        core[0].sizeX = Integer.parseInt(value);
      }
      else if (key.equals("Image Length")) {
        core[0].sizeY = Integer.parseInt(value);
      }
      else if (key.equals("Number of slices")) {
        core[0].sizeZ = Integer.parseInt(value);
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
      else if (key.equals("Origin X")) {
        originX = Float.parseFloat(value);
      }
      else if (key.equals("Origin Y")) {
        originY = Float.parseFloat(value);
      }
      else if (key.equals("Origin Z")) {
        originZ = Float.parseFloat(value);
      }
    }
    catch (NumberFormatException exc) {
      traceDebug(exc);
    }
  }

  /** Add the given file to the used files list. */
  private void addUsedFile(String workingDirPath, String file) {
    if (file == null) return;
    Location f = new Location(workingDirPath, file);
    if (!workingDirPath.equals("")) allFiles.add(f.getAbsolutePath());
    else allFiles.add(file);
  }

  // -- Helper class --

  class PEComparator implements Comparator {
    public int compare(Object o1, Object o2) {
      String s1 = o1.toString();
      String s2 = o2.toString();

      if (s1.equals(s2)) return 0;

      int underscore1 = (int) Math.max(s1.lastIndexOf("_"), 0);
      int underscore2 = (int) Math.max(s2.lastIndexOf("_"), 0);
      int dot1 = (int) Math.max(s1.lastIndexOf("."), 0);
      int dot2 = (int) Math.max(s2.lastIndexOf("."), 0);

      String prefix1 = s1.substring(0, underscore1);
      String prefix2 = s2.substring(0, underscore2);

      if (!prefix1.equals(prefix2)) return prefix1.compareTo(prefix2);

      try {
        int z1 = Integer.parseInt(s1.substring(underscore1 + 1, dot1));
        int z2 = Integer.parseInt(s2.substring(underscore2 + 1, dot2));

        if (z1 < z2) return -1;
        if (z2 < z1) return 1;
      }
      catch (NumberFormatException e) { }

      try {
        int ext1 = Integer.parseInt(s1.substring(dot1 + 1), 16);
        int ext2 = Integer.parseInt(s2.substring(dot2 + 1), 16);

        if (ext1 < ext2) return -1;
        if (ext1 > ext2) return 1;
      }
      catch (NumberFormatException e) { }
      return 0;
    }

    public boolean equals(Object o) {
      return compare(this, o) == 0;
    }
  }

}
