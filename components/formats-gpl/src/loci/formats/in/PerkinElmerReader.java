/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.formats.in;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import loci.common.DataTools;
import loci.common.DateTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.MetadataStore;

import ome.xml.model.primitives.Timestamp;

import ome.units.quantity.Length;
import ome.units.quantity.Time;
import ome.units.UNITS;

/**
 * PerkinElmerReader is the file format reader for PerkinElmer files.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
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

  /** List of files to open. */
  protected PixelsFile[] files;

  /** Flag indicating that the image data is in TIFF format. */
  private boolean isTiff = true;

  /** List of all files to open */
  private List<String> allFiles;

  private int extCount;

  private String details, sliceSpace;

  private double pixelSizeX = 1, pixelSizeY = 1;
  private String finishTime = null, startTime = null;
  private double originX = 0, originY = 0, originZ = 0;

  // -- Constructor --

  /** Constructs a new PerkinElmer reader. */
  public PerkinElmerReader() {
    super("PerkinElmer", new String[] {
      "ano", "cfg", "csv", "htm", "rec", "tim", "zpo", "tif"});
    domains = new String[] {FormatTools.LM_DOMAIN};
    hasCompanionFiles = true;
    datasetDescription = "One .htm file, several other metadata files " +
      "(.tim, .ano, .csv, …) and either .tif files or .2, .3, .4, etc. files";
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isSingleFile(String) */
  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String name, boolean open) {
    if (!open) return false; // not allowed to touch the file system

    if (checkSuffix(name, "cfg")) {
      // must contain the word "Ultraview"
      try {
        String check = DataTools.readFile(name);
        if (check.indexOf("Ultraview") == -1) return false;
      }
      catch (IOException e) { }
    }

    String ext = name;
    if (ext.indexOf('.') != -1) ext = ext.substring(ext.lastIndexOf(".") + 1);
    boolean binFile = true;
    try {
      Integer.parseInt(ext, 16);
    }
    catch (NumberFormatException e) {
      ext = ext.toLowerCase();
      if (!ext.equals("tif") && !ext.equals("tiff")) binFile = false;
    }

    Location baseFile = new Location(name).getAbsoluteFile();
    String prefix = baseFile.getParent() + File.separator;

    String namePrefix = baseFile.getName();
    if (namePrefix.indexOf('.') != -1) {
      namePrefix = namePrefix.substring(0, namePrefix.lastIndexOf("."));
    }
    if (namePrefix.indexOf('_') != -1 && binFile) {
      namePrefix = namePrefix.substring(0, namePrefix.lastIndexOf("_"));
    }
    prefix += namePrefix;

    Location htmlFile = new Location(prefix + ".htm");
    if (ext.toLowerCase().equals("htm")) {
      htmlFile = new Location(name).getAbsoluteFile();
    }
    if (!htmlFile.exists()) {
      htmlFile = new Location(prefix + ".HTM");
      while (!htmlFile.exists() && prefix.indexOf('_') != -1) {
        prefix = prefix.substring(0, prefix.lastIndexOf("_"));
        htmlFile = new Location(prefix + ".htm");
        if (!htmlFile.exists()) htmlFile = new Location(prefix + ".HTM");
      }
    }

    return htmlFile.exists() && (binFile || super.isThisType(name, false));
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  /* @see loci.formats.IFormatReader#get8BitLookupTable() */
  @Override
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    if (isTiff && tiff != null) {
      return tiff.get8BitLookupTable();
    }
    return null;
  }

  /* @see loci.formats.IFormatReader#get16BitLookupTable() */
  @Override
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    if (isTiff && tiff != null) {
      return tiff.get16BitLookupTable();
    }
    return null;
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.checkPlaneParameters(this, no, buf.length, x, y, w, h);

    String file = getFile(no);
    int index = getFileIndex(no);

    if (isTiff) {
      tiff.setId(file);
      return tiff.openBytes(index, buf, x, y, w, h);
    }

    RandomAccessInputStream ras = new RandomAccessInputStream(file);
    if (6 + index * FormatTools.getPlaneSize(this) < ras.length()) {
      ras.seek(6 + index * FormatTools.getPlaneSize(this));
      readPlane(ras, x, y, w, h, buf);
    }
    ras.close();
    return buf;
  }

  /* @see loci.formats.IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    if (noPixels) {
      final List<String> files = new ArrayList<String>();
      if (isTiff) {
        for (String f : allFiles) {
          if (!checkSuffix(f, new String[] {"tif", "tiff"})) {
            files.add(f);
          }
        }
      }
      else {
        for (String f : allFiles) {
          String ext = f.substring(f.lastIndexOf(".") + 1);
          try {
            Integer.parseInt(ext, 16);
          }
          catch (NumberFormatException e) { files.add(f); }
        }
      }
      return files.toArray(new String[files.size()]);
    }
    return allFiles.toArray(new String[allFiles.size()]);
  }

  /* @see loci.formats.IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (tiff != null) tiff.close(fileOnly);
    if (!fileOnly) {
      tiff = null;
      allFiles = null;
      files = null;
      details = sliceSpace = null;
      isTiff = true;
      pixelSizeX = pixelSizeY = 1f;
      finishTime = startTime = null;
      originX = originY = originZ = 0f;
    }
  }

  /* @see loci.formats.IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    FormatTools.assertId(currentId, true, 1);
    if (isTiff) {
      return tiff.getOptimalTileWidth();
    }
    return super.getOptimalTileWidth();
  }

  /* @see loci.formats.IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    FormatTools.assertId(currentId, true, 1);
    if (isTiff) {
      return tiff.getOptimalTileHeight();
    }
    return super.getOptimalTileHeight();
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    if (currentId != null && (id.equals(currentId) || isUsedFile(id))) return;

    LOGGER.info("Finding HTML companion file");

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

    allFiles = new ArrayList<String>();

    // get the working directory
    Location tmpFile = new Location(id).getAbsoluteFile();
    Location workingDir = tmpFile.getParentFile();
    if (workingDir == null) workingDir = new Location(".");
    String workingDirPath = workingDir.getPath();
    if (!workingDirPath.equals("")) workingDirPath += File.separator;
    String[] ls = workingDir.list(true);
    if (!new Location(id).exists()) {
      ls = Location.getIdMap().keySet().toArray(new String[0]);
      workingDirPath = "";
    }

    LOGGER.info("Searching for all metadata companion files");

    // check if we have any of the required header file types

    String cfgFile = null, anoFile = null, recFile = null;
    String timFile = null, csvFile = null, zpoFile = null;
    String htmFile = null;

    final List<PixelsFile> tempFiles = new ArrayList<PixelsFile>();

    int dot = id.lastIndexOf(".");
    String check = dot < 0 ? id : id.substring(0, dot);
    check = check.substring(check.lastIndexOf(File.separator) + 1);

    // locate appropriate .tim, .csv, .zpo, .htm and .tif files

    String prefix = null;

    Arrays.sort(ls);

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

        dot = ls[i].lastIndexOf(".");

        PixelsFile f = new PixelsFile();
        f.path = workingDirPath + ls[i];

        if (checkSuffix(ls[i], TiffReader.TIFF_SUFFIXES)) {
          if (dot - 4 >= 0 && dot - 4 < ls[i].length() &&
            ls[i].charAt(dot - 4) == '_')
          {
            f.firstIndex = Integer.parseInt(ls[i].substring(dot - 3, dot));
          }
          else {
            f.firstIndex = -1;
          }

          if (dot - 9 >= 0 && dot - 9 < ls[i].length() &&
            ls[i].charAt(dot - 9) == '_')
          {
            f.extIndex = Integer.parseInt(ls[i].substring(dot - 8, dot - 4));
          }
          else {
            f.firstIndex = i;
            f.extIndex = 0;
          }

          tempFiles.add(f);
        }
        else {
          try {
            if (dot - 4 >= 0 && dot - 4 < ls[i].length() &&
              ls[i].charAt(dot - 4) == '_')
            {
              f.firstIndex = Integer.parseInt(ls[i].substring(dot - 3, dot));
            }
            else {
              f.firstIndex = -1;
            }

            String ext =
              dot + 1 < ls[i].length() ? ls[i].substring(dot + 1) : "";
            f.extIndex = Integer.parseInt(ext, 16);
            isTiff = false;

            tempFiles.add(f);
          }
          catch (NumberFormatException exc) {
            LOGGER.debug("Failed to parse file extension", exc);
          }
        }
      }
    }

    files = tempFiles.toArray(new PixelsFile[tempFiles.size()]);

    // determine the number of different extensions we have

    LOGGER.info("Finding image files");

    List<Integer> foundExts = new ArrayList<Integer>();
    for (PixelsFile f : files) {
      if (!foundExts.contains(f.extIndex)) {
        foundExts.add(f.extIndex);
      }
    }
    extCount = foundExts.size();
    foundExts = null;

    CoreMetadata ms0 = core.get(0);

    ms0.imageCount = 0;
    for (PixelsFile f : files) {
      allFiles.add(f.path);

      ms0.imageCount++;
      if (f.firstIndex < 0 && files.length > extCount) {
        ms0.imageCount += ((files.length - 1) / (extCount - 1)) - 1;
      }
    }

    tiff = new MinimalTiffReader();

    // we always parse the .tim and .htm files if they exist, along with
    // either the .csv file or the .zpo file

    LOGGER.info("Parsing metadata values");

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

    if (timFile != null) parseTimFile(timFile);
    if (csvFile != null) parseCSVFile(csvFile);
    if (zpoFile != null && csvFile == null) parseZpoFile(zpoFile);

    // be aggressive about parsing the HTML file, since it's the only one that
    // explicitly defines the number of wavelengths and timepoints

    final List<Double> exposureTimes = new ArrayList<Double>();
    final List<Double> zPositions = new ArrayList<Double>();
    final List<Double> emWaves = new ArrayList<Double>();
    final List<Double> exWaves = new ArrayList<Double>();

    if (htmFile != null) {
      String[] tokens = DataTools.readFile(htmFile).split(HTML_REGEX);

      for (int j=0; j<tokens.length; j++) {
        if (tokens[j].indexOf('<') != -1) tokens[j] = "";
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
          exposureTimes.add(new Double(Double.parseDouble(exposure) / 1000));

          if (tokens[j].indexOf("nm") != -1) {
            int nmIndex = tokens[j].indexOf("nm");
            int paren = tokens[j].lastIndexOf("(", nmIndex);
            int slash = tokens[j].lastIndexOf("/", nmIndex);
            if (slash == -1) slash = nmIndex;
            emWaves.add(
              new Double(tokens[j].substring(paren + 1, slash).trim()));
            if (tokens[j].indexOf("nm", nmIndex + 3) != -1) {
              nmIndex = tokens[j].indexOf("nm", nmIndex + 3);
              paren = tokens[j].lastIndexOf(" ", nmIndex);
              slash = tokens[j].lastIndexOf("/", nmIndex);
              if (slash == -1) slash = nmIndex + 2;
              exWaves.add(
                new Double(tokens[j].substring(paren + 1, slash).trim()));
            }
          }

          j--;
        }
        else if (tokens[j + 1].trim().equals("Slice Z positions")) {
          for (int q=j + 2; q<tokens.length; q++) {
            if (!tokens[q].trim().equals("")) {
              try {
                zPositions.add(new Double(tokens[q].trim()));
              }
              catch (NumberFormatException e) { }
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
        if (token.equals("Wavelengths")) ms0.sizeC = n;
        else if (token.equals("Frames")) ms0.sizeT = n;
        else if (token.equals("Slices")) ms0.sizeZ = n;
        try {
          n = Integer.parseInt(token);
        }
        catch (NumberFormatException e) { n = 0; }
      }
    }

    LOGGER.info("Populating metadata");

    if (files.length == 0) {
      throw new FormatException("TIFF files not found.");
    }

    if (isTiff) {
      tiff.setId(getFile(0));
      ms0.pixelType = tiff.getPixelType();
    }
    else {
      RandomAccessInputStream tmp = new RandomAccessInputStream(getFile(0));
      int bpp = (int) (tmp.length() - 6) / (getSizeX() * getSizeY());
      tmp.close();
      if (bpp % 3 == 0) bpp /= 3;
      ms0.pixelType = FormatTools.pixelTypeFromBytes(bpp, false, false);
    }

    if (getSizeZ() <= 0) ms0.sizeZ = 1;
    if (getSizeC() <= 0) ms0.sizeC = 1;

    if (getSizeT() <= 0 || getImageCount() % (getSizeZ() * getSizeC()) == 0) {
      ms0.sizeT = getImageCount() / (getSizeZ() * getSizeC());
    }
    else {
      ms0.imageCount = getSizeZ() * getSizeC() * getSizeT();
      if (getImageCount() > files.length) {
        ms0.imageCount = files.length;
        ms0.sizeT = getImageCount() / (getSizeZ() * getSizeC());
      }
    }

    ms0.dimensionOrder = "XYCTZ";
    ms0.rgb = isTiff ? tiff.isRGB() : false;
    ms0.interleaved = false;
    ms0.littleEndian = isTiff ? tiff.isLittleEndian() : true;
    ms0.metadataComplete = true;
    ms0.indexed = isTiff ? tiff.isIndexed() : false;
    ms0.falseColor = false;

    if (getImageCount() != getSizeZ() * getSizeC() * getSizeT()) {
      ms0.imageCount = getSizeZ() * getSizeC() * getSizeT();
    }

    if (!isTiff && extCount > getSizeT()) {
      extCount = getSizeT() * getSizeC();
    }

    // Populate metadata store

    // The metadata store we're working with.
    MetadataStore store = makeFilterMetadata();
    MetadataTools.populatePixels(store, this, true);

    // populate Image element
    if (finishTime != null) {
      Timestamp timestamp = Timestamp.valueOf(DateTools.formatDate(finishTime, DATE_FORMAT));
      if (timestamp != null)
        store.setImageAcquisitionDate(timestamp, 0);
    }

    if (getMetadataOptions().getMetadataLevel() != MetadataLevel.MINIMUM) {
      // populate Dimensions element
      Length sizeX = FormatTools.getPhysicalSizeX(pixelSizeX);
      Length sizeY = FormatTools.getPhysicalSizeY(pixelSizeY);

      if (sizeX != null) {
        store.setPixelsPhysicalSizeX(sizeX, 0);
      }
      if (sizeY != null) {
        store.setPixelsPhysicalSizeY(sizeY, 0);
      }

      // link Instrument and Image
      String instrumentID = MetadataTools.createLSID("Instrument", 0);
      store.setInstrumentID(instrumentID, 0);
      store.setImageInstrumentRef(instrumentID, 0);

      // populate LogicalChannel element
      for (int i=0; i<getEffectiveSizeC(); i++) {
        if (i < emWaves.size()) {
          Length em = FormatTools.getEmissionWavelength(emWaves.get(i));
          if (em != null) {
            store.setChannelEmissionWavelength(em, 0, i);
          }
        }
        if (i < exWaves.size()) {
          Length ex = FormatTools.getExcitationWavelength(exWaves.get(i));
          if (ex != null) {
            store.setChannelExcitationWavelength(ex, 0, i);
          }
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

      double secondsPerPlane = (double) (end - start) / getImageCount() / 1000;

      for (int i=0; i<getImageCount(); i++) {
        int[] zct = getZCTCoords(i);
        store.setPlaneDeltaT(new Time(i * secondsPerPlane, UNITS.SECOND), 0, i);
        if (zct[1] < exposureTimes.size() && exposureTimes.get(zct[1]) != null) {
          store.setPlaneExposureTime(new Time(exposureTimes.get(zct[1]), UNITS.SECOND), 0, i);
        }

        if (zct[0] < zPositions.size()) {
          final Double zPosition = zPositions.get(zct[0]);
          final Length xl = new Length(0d, UNITS.REFERENCEFRAME);
          final Length yl = new Length(0d, UNITS.REFERENCEFRAME);
          final Length zl;
          if (zPosition == null) {
              zl = null;
          } else {
              zl = new Length(zPosition, UNITS.REFERENCEFRAME);
          }
          store.setPlanePositionX(xl, 0, i);
          store.setPlanePositionY(yl, 0, i);
          store.setPlanePositionZ(zl, 0, i);
        }
      }
    }
  }

  // -- Helper methods --

  private PixelsFile lookupFile(int no) {
    int minExtIndex = Integer.MAX_VALUE;
    int minFirstIndex = Integer.MAX_VALUE;
    for (PixelsFile f : files) {
      if (f.extIndex < minExtIndex) {
        minExtIndex = f.extIndex;
      }
      if (f.firstIndex >= 0 && f.firstIndex < minFirstIndex) {
        minFirstIndex = f.firstIndex;
      }
    }

    for (int ext=minExtIndex; ext<=extCount+minExtIndex; ext++) {
      for (PixelsFile f : files) {
        if (f.extIndex == ext) {
          if (f.firstIndex < 0) {
            if ((no % extCount) == ext - minExtIndex) {
              return f;
            }
          }
          else if (no ==
            (f.firstIndex - minFirstIndex) * extCount + ext - minExtIndex)
          {
            return f;
          }
        }
      }
    }
    return null;
  }

  private String getFile(int no) {
    PixelsFile f = lookupFile(no);
    return f.path;
  }

  private int getFileIndex(int no) {
    PixelsFile f = lookupFile(no);
    return f.firstIndex >= 0 ? 0 : no / extCount;
  }

  private void parseKeyValue(String key, String value) {
    if (key == null || value == null) return;
    addGlobalMeta(key, value);

    CoreMetadata m = core.get(0);

    try {
      if (key.equals("Image Width")) {
        m.sizeX = Integer.parseInt(value);
      }
      else if (key.equals("Image Length")) {
        m.sizeY = Integer.parseInt(value);
      }
      else if (key.equals("Number of slices")) {
        m.sizeZ = Integer.parseInt(value);
      }
      else if (key.equals("Experiment details:")) details = value;
      else if (key.equals("Z slice space")) sliceSpace = value;
      else if (key.equals("Pixel Size X")) {
        pixelSizeX = Double.parseDouble(value);
      }
      else if (key.equals("Pixel Size Y")) {
        pixelSizeY = Double.parseDouble(value);
      }
      else if (key.equals("Finish Time:")) finishTime = value;
      else if (key.equals("Start Time:")) startTime = value;
      else if (key.equals("Origin X")) {
        originX = Double.parseDouble(value);
      }
      else if (key.equals("Origin Y")) {
        originY = Double.parseDouble(value);
      }
      else if (key.equals("Origin Z")) {
        originZ = Double.parseDouble(value);
      }
      else if (key.equals("SubfileType X")) {
        m.bitsPerPixel = Integer.parseInt(value);
      }
    }
    catch (NumberFormatException exc) {
      LOGGER.debug("", exc);
    }
  }

  /** Add the given file to the used files list. */
  private void addUsedFile(String workingDirPath, String file) {
    if (file == null) return;
    Location f = new Location(workingDirPath, file);
    if (!workingDirPath.equals("")) allFiles.add(f.getAbsolutePath());
    else allFiles.add(file);
  }

  private void parseTimFile(String timFile) throws IOException {
    String[] tokens = DataTools.readFile(timFile).split("\\s");
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
  }

  private void parseCSVFile(String csvFile) throws IOException {
    if (getMetadataOptions().getMetadataLevel() == MetadataLevel.MINIMUM) {
      return;
    }
    String[] tokens = DataTools.readFile(csvFile).split("\\s");
    final List<String> tmp = new ArrayList<String>();
    for (String token : tokens) {
      if (token.trim().length() > 0) tmp.add(token.trim());
    }
    tokens = tmp.toArray(new String[0]);

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

  private void parseZpoFile(String zpoFile) throws IOException {
    if (getMetadataOptions().getMetadataLevel() == MetadataLevel.MINIMUM) {
      return;
    }
    String[] tokens = DataTools.readFile(zpoFile).split("\\s");
    for (int t=0; t<tokens.length; t++) {
      addGlobalMetaList("Z slice position", tokens[t]);
    }
  }

  // -- Helper class --

  class PixelsFile {
    public int firstIndex;
    public int extIndex;
    public String path;
  }

}
