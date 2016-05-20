/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.formats;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import loci.common.DataTools;
import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.in.DefaultMetadataOptions;
import loci.formats.in.MetadataLevel;
import loci.formats.in.MetadataOptions;
import loci.formats.meta.MetadataStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logic to stitch together files with similar names.
 * Assumes that all files have the same characteristics (e.g., dimensions).
 */
public class FileStitcher extends ReaderWrapper {

  // -- Constants --

  private static final Logger LOGGER =
    LoggerFactory.getLogger(FileStitcher.class);

  private static final int MAX_READERS = 1000;

  // -- Fields --

  /**
   * Whether string ids given should be treated
   * as file patterns rather than single file paths.
   */
  private boolean patternIds = false;

  private boolean doNotChangePattern = false;

  /** Dimensional axis lengths per file. */
  private int[] sizeZ, sizeC, sizeT;

  /** Component lengths for each axis type. */
  private int[][] lenZ, lenC, lenT;

  /** Core metadata. */
  private ArrayList<CoreMetadata> core = new ArrayList<CoreMetadata>();

  /** The number of the current series. */
  private int coreIndex;

  /** The number of the current series (non flat). */
  private int series;

  private boolean noStitch;
  private boolean group = true;

  private MetadataStore store;

  private ExternalSeries[] externals;
  private ClassList<IFormatReader> classList;
  private String currentPattern;

  // -- Constructors --

  /** Constructs a FileStitcher around a new image reader. */
  public FileStitcher() { this(new ImageReader()); }

  /**
   * Constructs a FileStitcher around a new image reader.
   * @param patternIds Whether string ids given should be treated as file
   *    patterns rather than single file paths.
   */
  public FileStitcher(boolean patternIds) {
    this(new ImageReader(), patternIds);
  }

  /**
   * Constructs a FileStitcher with the given reader.
   * @param r The reader to use for reading stitched files.
   */
  public FileStitcher(IFormatReader r) { this(r, false); }

  /**
   * Constructs a FileStitcher with the given reader.
   * @param r The reader to use for reading stitched files.
   * @param patternIds Whether string ids given should be treated as file
   *   patterns rather than single file paths.
   */
  public FileStitcher(IFormatReader r, boolean patternIds) {
    super(r);
    if (r.getClass().getPackage().getName().equals("loci.formats.in")) {
      ClassList<IFormatReader> classes =
        new ClassList<IFormatReader>(IFormatReader.class);
      classes.addClass(r.getClass());
      setReaderClassList(classes);
    }
    else {
      reader = DimensionSwapper.makeDimensionSwapper(r);
    }
    setUsingPatternIds(patternIds);
  }

  // -- FileStitcher API methods --

  /**
   * Set the ClassList object to use when constructing any helper readers.
   */
  public void setReaderClassList(ClassList<IFormatReader> classList) {
    this.classList = classList;
    reader = DimensionSwapper.makeDimensionSwapper(new ImageReader(classList));
  }

  /** Gets the wrapped reader prototype. */
  @Override
  public IFormatReader getReader() { return reader; }

  /** Sets whether the reader is using file patterns for IDs. */
  public void setUsingPatternIds(boolean patternIds) {
    this.patternIds = patternIds;
  }

  /** Gets whether the reader is using file patterns for IDs. */
  public boolean isUsingPatternIds() { return patternIds; }

  public void setCanChangePattern(boolean doChange) {
    doNotChangePattern = !doChange;
  }

  public boolean canChangePattern() {
    return !doNotChangePattern;
  }

  /** Gets the reader appropriate for use with the given image plane. */
  public IFormatReader getReader(int no) throws FormatException, IOException {
    if (noStitch) return reader;
    int[] q = computeIndices(no);
    int fno = q[0];
    return getReader(getCoreIndex(), fno);
  }

  /**
   * Gets the reader that should be used with the given series and image plane.
   */
  public DimensionSwapper getReader(int series, int no) {
    if (noStitch) return (DimensionSwapper) reader;
    DimensionSwapper r = externals[getExternalSeries(series)].getReader(no);
    initReader(series, no);
    return r;
  }

  /** Gets the local reader index for use with the given image plane. */
  public int getAdjustedIndex(int no) throws FormatException, IOException {
    if (noStitch) return no;
    int[] q = computeIndices(no);
    int ino = q[1];
    return ino;
  }

  /**
   * Gets the axis type for each dimensional block.
   * @return An array containing values from the enumeration:
   *   <ul>
   *     <li>AxisGuesser.Z_AXIS: focal planes</li>
   *     <li>AxisGuesser.T_AXIS: time points</li>
   *     <li>AxisGuesser.C_AXIS: channels</li>
   *     <li>AxisGuesser.S_AXIS: series</li>
   *   </ul>
   */
  public int[] getAxisTypes() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return externals[getExternalSeries()].getAxisGuesser().getAxisTypes();
  }

  /**
   * Sets the axis type for each dimensional block.
   * @param axes An array containing values from the enumeration:
   *   <ul>
   *     <li>AxisGuesser.Z_AXIS: focal planes</li>
   *     <li>AxisGuesser.T_AXIS: time points</li>
   *     <li>AxisGuesser.C_AXIS: channels</li>
   *     <li>AxisGuesser.S_AXIS: series</li>
   *   </ul>
   */
  public void setAxisTypes(int[] axes) throws FormatException {
    FormatTools.assertId(getCurrentFile(), true, 2);
    externals[getExternalSeries()].getAxisGuesser().setAxisTypes(axes);
    computeAxisLengths();
  }

  /** Gets the file pattern object used to build the list of files. */
  public FilePattern getFilePattern() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return noStitch ? findPattern(getCurrentFile()) :
      externals[getExternalSeries()].getFilePattern();
  }

  /**
   * Gets the axis guesser object used to guess
   * which dimensional axes are which.
   */
  public AxisGuesser getAxisGuesser() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return externals[getExternalSeries()].getAxisGuesser();
  }

  public FilePattern findPattern(String id) {
    return new FilePattern(FilePattern.findPattern(id));
  }

  /**
   * Finds the file pattern for the given ID, based on the state of the file
   * stitcher. Takes both ID map entries and the patternIds flag into account.
   */
  public String[] findPatterns(String id) {
    if (!patternIds) {
      // find the containing patterns
      HashMap<String, Object> map = Location.getIdMap();
      if (map.containsKey(id)) {
        // search ID map for pattern, rather than files on disk
        String[] idList = new String[map.size()];
        map.keySet().toArray(idList);
        return FilePattern.findSeriesPatterns(id, null, idList);
      }
      else {
        // id is an unmapped file path; look to similar files on disk
        return FilePattern.findSeriesPatterns(id);
      }
    }
    if (doNotChangePattern) {
      return new String[] {id};
    }
    patternIds = false;
    String[] patterns = findPatterns(new FilePattern(id).getFiles()[0]);
    if (patterns.length == 0) patterns = new String[] {id};
    else {
      FilePattern test = new FilePattern(patterns[0]);
      if (test.getFiles().length == 0) patterns = new String[] {id};
    }
    patternIds = true;
    return patterns;
  }

  // -- IFormatReader API methods --

  /* @see IFormatReader#getRequiredDirectories() */
  @Override
  public int getRequiredDirectories(String[] files)
    throws FormatException, IOException
  {
    return reader.getRequiredDirectories(files);
  }

  /* @see IFormatReader#getImageCount() */
  @Override
  public int getImageCount() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return noStitch ? reader.getImageCount() : core.get(getCoreIndex()).imageCount;
  }

  /* @see IFormatReader#isRGB() */
  @Override
  public boolean isRGB() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return noStitch ? reader.isRGB() : core.get(getCoreIndex()).rgb;
  }

  /* @see IFormatReader#getSizeX() */
  @Override
  public int getSizeX() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return noStitch ? reader.getSizeX() : core.get(getCoreIndex()).sizeX;
  }

  /* @see IFormatReader#getSizeY() */
  @Override
  public int getSizeY() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return noStitch ? reader.getSizeY() : core.get(getCoreIndex()).sizeY;
  }

  /* @see IFormatReader#getSizeZ() */
  @Override
  public int getSizeZ() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return noStitch ? reader.getSizeZ() : core.get(getCoreIndex()).sizeZ;
  }

  /* @see IFormatReader#getSizeC() */
  @Override
  public int getSizeC() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return noStitch ? reader.getSizeC() : core.get(getCoreIndex()).sizeC;
  }

  /* @see IFormatReader#getSizeT() */
  @Override
  public int getSizeT() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return noStitch ? reader.getSizeT() : core.get(getCoreIndex()).sizeT;
  }

  /* @see IFormatReader#getPixelType() */
  @Override
  public int getPixelType() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return noStitch ? reader.getPixelType() : core.get(getCoreIndex()).pixelType;
  }

  /* @see IFormatReader#getBitsPerPixel() */
  @Override
  public int getBitsPerPixel() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return noStitch ? reader.getBitsPerPixel() : core.get(getCoreIndex()).bitsPerPixel;
  }

  /* @see IFormatReader#isIndexed() */
  @Override
  public boolean isIndexed() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return noStitch ? reader.isIndexed() : core.get(getCoreIndex()).indexed;
  }

  /* @see IFormatReader#isFalseColor() */
  @Override
  public boolean isFalseColor() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return noStitch ? reader.isFalseColor() : core.get(getCoreIndex()).falseColor;
  }

  /* @see IFormatReader#get8BitLookupTable() */
  @Override
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return noStitch ? reader.get8BitLookupTable() :
      getReader(getCoreIndex(), 0).get8BitLookupTable();
  }

  /* @see IFormatReader#get16BitLookupTable() */
  @Override
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return noStitch ? reader.get16BitLookupTable() :
      getReader(getCoreIndex(), 0).get16BitLookupTable();
  }

  /* @see IFormatReader#getThumbSizeX() */
  @Override
  public int getThumbSizeX() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return noStitch ? reader.getThumbSizeX() :
      getReader(getCoreIndex(), 0).getThumbSizeX();
  }

  /* @see IFormatReader#getThumbSizeY() */
  @Override
  public int getThumbSizeY() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return noStitch ? reader.getThumbSizeY() :
      getReader(getCoreIndex(), 0).getThumbSizeY();
  }

  /* @see IFormatReader#isLittleEndian() */
  @Override
  public boolean isLittleEndian() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return noStitch ? reader.isLittleEndian() :
      getReader(getCoreIndex(), 0).isLittleEndian();
  }

  /* @see IFormatReader#getDimensionOrder() */
  @Override
  public String getDimensionOrder() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    if (noStitch) return reader.getDimensionOrder();
    return core.get(getCoreIndex()).dimensionOrder;
  }

  /* @see IFormatReader#isOrderCertain() */
  @Override
  public boolean isOrderCertain() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return noStitch ? reader.isOrderCertain() : core.get(getCoreIndex()).orderCertain;
  }

  /* @see IFormatReader#isThumbnailSeries() */
  @Override
  public boolean isThumbnailSeries() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return noStitch ? reader.isThumbnailSeries() : core.get(getCoreIndex()).thumbnail;
  }

  /* @see IFormatReader#isInterleaved() */
  @Override
  public boolean isInterleaved() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return noStitch ? reader.isInterleaved() :
      getReader(getCoreIndex(), 0).isInterleaved();
  }

  /* @see IFormatReader#isInterleaved(int) */
  @Override
  public boolean isInterleaved(int subC) {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return noStitch ? reader.isInterleaved(subC) :
      getReader(getCoreIndex(), 0).isInterleaved(subC);
  }

  /* @see IFormatReader#openBytes(int) */
  @Override
  public byte[] openBytes(int no) throws FormatException, IOException {
    return openBytes(no, 0, 0, getSizeX(), getSizeY());
  }

  /* @see IFormatReader#openBytes(int, byte[]) */
  @Override
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    return openBytes(no, buf, 0, 0, getSizeX(), getSizeY());
  }

  /* @see IFormatReader#openBytes(int, int, int, int, int) */
  @Override
  public byte[] openBytes(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int ch = getRGBChannelCount();
    byte[] buf = DataTools.allocate(w, h, ch, bpp);
    return openBytes(no, buf, x, y, w, h);
  }

  /* @see IFormatReader#openBytes(int, byte[], int, int, int, int) */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(getCurrentFile(), true, 2);

    int[] pos = computeIndices(no);
    IFormatReader r = getReader(getCoreIndex(), pos[0]);
    int ino = pos[1];

    if (ino < r.getImageCount()) {
      byte[] b = r.openBytes(ino, buf, x, y, w, h);
      return b;
    }

    // return a blank image to cover for the fact that
    // this file does not contain enough image planes
    Arrays.fill(buf, (byte) 0);
    return buf;
  }

  /* @see IFormatReader#openPlane(int, int, int, int, int) */
  @Override
  public Object openPlane(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(getCurrentFile(), true, 2);

    IFormatReader r = getReader(no);
    int ino = getAdjustedIndex(no);
    if (ino < r.getImageCount()) return r.openPlane(ino, x, y, w, h);

    return null;
  }

  /* @see IFormatReader#openThumbBytes(int) */
  @Override
  public byte[] openThumbBytes(int no) throws FormatException, IOException {
    FormatTools.assertId(getCurrentFile(), true, 2);

    IFormatReader r = getReader(no);
    int ino = getAdjustedIndex(no);
    if (ino < r.getImageCount()) return r.openThumbBytes(ino);

    // return a blank image to cover for the fact that
    // this file does not contain enough image planes
    return externals[getExternalSeries()].getBlankThumbBytes();
  }

  /* @see IFormatReader#close() */
  @Override
  public void close() throws IOException {
    close(false);
  }

  /* @see IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (externals != null) {
      for (ExternalSeries s : externals) {
        if (s != null && s.getReaders() != null) {
          for (DimensionSwapper r : s.getReaders()) {
            if (r != null) r.close(fileOnly);
          }
        }
      }
    }
    if (!fileOnly) {
      noStitch = false;
      externals = null;
      sizeZ = sizeC = sizeT = null;
      lenZ = lenC = lenT = null;
      core.clear();
      coreIndex = 0;
      series = 0;
      store = null;
      currentPattern = null;
    }
  }

  /* @see IFormatReader#getSeriesCount() */
  @Override
  public int getSeriesCount() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return noStitch ? reader.getSeriesCount() : core.size();
  }

  /* @see IFormatReader#setSeries(int) */
  @Override
  public void setSeries(int no) {
    FormatTools.assertId(getCurrentFile(), true, 2);
    int n = reader.getSeriesCount();
    if (n > 1 || noStitch) reader.setSeries(no);
    else {
      coreIndex = no;
      series = no;
    }
  }

  /* @see IFormatReader#getSeries() */
  @Override
  public int getSeries() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return reader.getSeries() > 0 ? reader.getSeries() : series;
  }

  /* @see IFormatReader#seriesToCoreIndex(int) */
  @Override
  public int seriesToCoreIndex(int series) {
    int n = reader.getSeriesCount();
    if (n > 1 || noStitch) return reader.seriesToCoreIndex(series);
    return series;
  }

  /* @see IFormatReader#coreIndexToSeries(int) */
  @Override
  public int coreIndexToSeries(int index) {
    int n = reader.getSeriesCount();
    if (n > 1 || noStitch) return reader.coreIndexToSeries(index);
    return index;
  }

  /* @see IFormatReader#setCoreIndex(int) */
  @Override
  public void setCoreIndex(int no) {
    FormatTools.assertId(getCurrentFile(), true, 2);
    int n = reader.getSeriesCount();
    if (n > 1 || noStitch) reader.setCoreIndex(no);
    else {
      coreIndex = no;
      series = no;
    }
  }

  /* @see IFormatReader#getCoreIndex() */
  @Override
  public int getCoreIndex() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return reader.getCoreIndex() > 0 ? reader.getCoreIndex() : coreIndex;
  }

  /* @see IFormatReader#setGroupFiles(boolean) */
  @Override
  public void setGroupFiles(boolean group) {
    this.group = group;
  }

  /* @see IFormatReader#isGroupFiles(boolean) */
  @Override
  public boolean isGroupFiles() {
    return group;
  }

  /* @see IFormatReader#setMetadataOptions(MetadataOptions) */
  @Override
  public void setMetadataOptions(MetadataOptions options) {
    super.setMetadataOptions(options);
    if (externals != null) {
      for (ExternalSeries s : externals) {
        for (DimensionSwapper r : s.getReaders()) {
          r.setMetadataOptions(options);
        }
      }
    }
  }

  /* @see IFormatReader#setNormalized(boolean) */
  @Override
  public void setNormalized(boolean normalize) {
    FormatTools.assertId(getCurrentFile(), false, 2);
    if (externals == null) reader.setNormalized(normalize);
    else {
      for (ExternalSeries s : externals) {
        for (DimensionSwapper r : s.getReaders()) {
          r.setNormalized(normalize);
        }
      }
    }
  }

  /* @see IFormatReader#setOriginalMetadataPopulated(boolean) */
  @Override
  public void setOriginalMetadataPopulated(boolean populate) {
    FormatTools.assertId(getCurrentFile(), false, 1);
    if (externals == null) reader.setOriginalMetadataPopulated(populate);
    else {
      for (ExternalSeries s : externals) {
        for (DimensionSwapper r : s.getReaders()) {
          r.setOriginalMetadataPopulated(populate);
        }
      }
    }
  }

  /* @see IFormatReader#getUsedFiles() */
  @Override
  public String[] getUsedFiles() {
    FormatTools.assertId(getCurrentFile(), true, 2);

    if (noStitch) return reader.getUsedFiles();

    // returning the files list directly here is fast, since we do not
    // have to call initFile on each constituent file; but we can only do so
    // when each constituent file does not itself have multiple used files

    Set<String> files = new LinkedHashSet<String>();
    for (ExternalSeries s : externals) {
      String[] f = s.getFiles();
      for (String file : f) {
        String path = new Location(file).getAbsolutePath();
        files.add(path);
      }

      DimensionSwapper[] readers = s.getReaders();
      for (int i=0; i<readers.length; i++) {
          String[] used = readers[i].getUsedFiles();
          for (String file : used) {
            String path = new Location(file).getAbsolutePath();
            files.add(path);
          }
      }
    }
    return files.toArray(new String[files.size()]);
  }

  /* @see IFormatReader#getUsedFiles() */
  @Override
  public String[] getUsedFiles(boolean noPixels) {
    return noPixels && noStitch ?
      reader.getUsedFiles(noPixels) : getUsedFiles();
  }

  /* @see IFormatReader#getSeriesUsedFiles() */
  @Override
  public String[] getSeriesUsedFiles() {
    return getUsedFiles();
  }

  /* @see IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    return getUsedFiles(noPixels);
  }

  /* @see IFormatReader#getAdvancedUsedFiles(boolean) */
  @Override
  public FileInfo[] getAdvancedUsedFiles(boolean noPixels) {
    if (noStitch) return reader.getAdvancedUsedFiles(noPixels);
    String[] files = getUsedFiles(noPixels);
    if (files == null) return null;
    FileInfo[] infos = new FileInfo[files.length];
    for (int i=0; i<infos.length; i++) {
      infos[i] = new FileInfo();
      infos[i].filename = files[i];
      try {
        infos[i].reader = ((DimensionSwapper) reader).unwrap().getClass();
      }
      catch (FormatException e) {
        LOGGER.debug("", e);
      }
      catch (IOException e) {
        LOGGER.debug("", e);
      }
      infos[i].usedToInitialize = files[i].endsWith(getCurrentFile());
    }
    return infos;
  }

  /* @see IFormatReader#getAdvancedSeriesUsedFiles(boolean) */
  @Override
  public FileInfo[] getAdvancedSeriesUsedFiles(boolean noPixels) {
    if (noStitch) return reader.getAdvancedSeriesUsedFiles(noPixels);
    String[] files = getSeriesUsedFiles(noPixels);
    if (files == null) return null;
    FileInfo[] infos = new FileInfo[files.length];
    for (int i=0; i<infos.length; i++) {
      infos[i] = new FileInfo();
      infos[i].filename = files[i];
      try {
        infos[i].reader = ((DimensionSwapper) reader).unwrap().getClass();
      }
      catch (FormatException e) {
        LOGGER.debug("", e);
      }
      catch (IOException e) {
        LOGGER.debug("", e);
      }
      infos[i].usedToInitialize = files[i].endsWith(getCurrentFile());
    }
    return infos;
  }

  /* @see IFormatReader#getIndex(int, int, int) */
  @Override
  public int getIndex(int z, int c, int t) {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return FormatTools.getIndex(this, z, c, t);
  }

  /* @see IFormatReader#getIndex(int, int, int, int, int, int) */
  @Override
  public int getIndex(int z, int c, int t, int moduloZ, int moduloC, int moduloT) {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return FormatTools.getIndex(this, z, c, t, moduloZ, moduloC, moduloT);
  }

  /* @see IFormatReader#getZCTCoords(int) */
  @Override
  public int[] getZCTCoords(int index) {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return noStitch ? reader.getZCTCoords(index) :
      FormatTools.getZCTCoords(core.get(getCoreIndex()).dimensionOrder,
      getSizeZ(), getEffectiveSizeC(), getSizeT(), getImageCount(), index);
  }

  /* @see IFormatReader#getZCTModuloCoords(int) */
  @Override
  public int[] getZCTModuloCoords(int index) {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return noStitch ? reader.getZCTModuloCoords(index) :
      FormatTools.getZCTCoords(core.get(getCoreIndex()).dimensionOrder,
      getSizeZ(), getEffectiveSizeC(), getSizeT(),
      getModuloZ().length(), getModuloC().length(), getModuloT().length(),
      getImageCount(), index);
  }

  /* @see IFormatReader#getSeriesMetadata() */
  @Override
  public Hashtable<String, Object> getSeriesMetadata() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return noStitch ? reader.getSeriesMetadata() :
      core.get(getCoreIndex()).seriesMetadata;
  }

  /* @see IFormatReader#getCoreMetadataList() */
  @Override
  public List<CoreMetadata> getCoreMetadataList() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return noStitch ? reader.getCoreMetadataList() : core;
  }

  /* @see IFormatReader#setMetadataStore(MetadataStore) */
  @Override
  public void setMetadataStore(MetadataStore store) {
    //FormatTools.assertId(getCurrentFile(), false, 2);
    reader.setMetadataStore(store);
    this.store = store;
  }

  /* @see IFormatReader#getMetadataStore() */
  @Override
  public MetadataStore getMetadataStore() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return noStitch ? reader.getMetadataStore() : store;
  }

  /* @see IFormatReader#getMetadataStoreRoot() */
  @Override
  public Object getMetadataStoreRoot() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return noStitch ? reader.getMetadataStoreRoot() : store.getRoot();
  }

  /* @see IFormatReader#getUnderlyingReaders() */
  @Override
  public IFormatReader[] getUnderlyingReaders() {
    List<IFormatReader> list = new ArrayList<IFormatReader>();
    for (ExternalSeries s : externals) {
      for (DimensionSwapper r : s.getReaders()) {
        list.add(r);
      }
    }
    return list.toArray(new IFormatReader[0]);
  }

  /* @see IFormatReader#reopenFile) */
  @Override
  public void reopenFile() throws IOException {
    reader.reopenFile();
    if (externals != null) {
      for (ExternalSeries s : externals) {
        for (DimensionSwapper r : s.getReaders()) {
          r.reopenFile();
        }
      }
    }
  }

  /* @see IFormatReader#setId(String) */
  @Override
  public void setId(String id) throws FormatException, IOException {
    if (getCurrentFile() != null &&
      (new Location(id).getAbsolutePath().equals(getCurrentFile()) ||
      id.equals(currentPattern)))
    {
      // already initialized this file
      return;
    }

    close();
    currentPattern = id;
    initFile(id);
  }

  // -- Internal FormatReader API methods --

  /** Initializes the given file or file pattern. */
  protected void initFile(String id) throws FormatException, IOException {
    LOGGER.debug("initFile: {}", id);

    FilePattern fp = new FilePattern(id);
    if (!patternIds) {
      patternIds = fp.isValid() && fp.getFiles().length > 1;
    }
    else if (canChangePattern()) {
      patternIds =
        !new Location(id).exists() && Location.getMappedId(id).equals(id);
    }

    boolean mustGroup = false;
    if (patternIds) {
      mustGroup = fp.isValid() &&
        reader.fileGroupOption(fp.getFiles()[0]) == FormatTools.MUST_GROUP;
    }
    else {
      mustGroup = reader.fileGroupOption(id) == FormatTools.MUST_GROUP;
    }

    if (mustGroup || !group) {
      // reader subclass is handling file grouping
      noStitch = true;
      reader.close();
      reader.setGroupFiles(group);

      if (patternIds && fp.isValid()) {
        reader.setId(fp.getFiles()[0]);
      }
      else reader.setId(id);
      return;
    }

    if (fp.isRegex()) {
      setCanChangePattern(false);
    }

    String[] patterns = findPatterns(id);
    if (patterns.length == 0) patterns = new String[] {id};
    externals = new ExternalSeries[patterns.length];

    for (int i=0; i<externals.length; i++) {
      externals[i] = new ExternalSeries(new FilePattern(patterns[i]));
    }
    fp = new FilePattern(patterns[0]);

    reader.close();
    reader.setGroupFiles(group);

    if (!fp.isValid()) {
      throw new FormatException("Invalid file pattern: " + fp.getPattern());
    }
    reader.setId(fp.getFiles()[0]);

    String msg = " Please rename your files or disable file stitching.";
    if (reader.getSeriesCount() > 1 && externals.length > 1) {
      throw new FormatException("Unsupported grouping: File pattern contains " +
        "multiple files and each file contains multiple series." + msg);
    }

    int nPixelsFiles =
      reader.getUsedFiles().length - reader.getUsedFiles(true).length;
    if (nPixelsFiles > 1 || fp.getFiles().length == 1) {
      noStitch = true;
      return;
    }

    AxisGuesser guesser = new AxisGuesser(fp, reader.getDimensionOrder(),
      reader.getSizeZ(), reader.getSizeT(), reader.getEffectiveSizeC(),
      reader.isOrderCertain());

    // use the dimension order recommended by the axis guesser
    ((DimensionSwapper) reader).swapDimensions(guesser.getAdjustedOrder());

    // if this is a multi-series dataset, we need some special logic
    int seriesCount = externals.length;
    if (externals.length == 1) {
      seriesCount = reader.getSeriesCount();
    }

    // verify that file pattern is valid and matches existing files
    if (!fp.isValid()) {
      throw new FormatException("Invalid " +
        (patternIds ? "file pattern" : "filename") +
        " (" + id + "): " + fp.getErrorMessage() + msg);
    }
    String[] files = fp.getFiles();

    if (files == null) {
      throw new FormatException("No files matching pattern (" +
        fp.getPattern() + "). " + msg);
    }

    for (int i=0; i<files.length; i++) {
      String file = files[i];

      // HACK: skip file existence check for fake files
      if (file.toLowerCase().endsWith(".fake")) continue;

      if (!new Location(file).exists()) {
        throw new FormatException("File #" + i +
          " (" + file + ") does not exist.");
      }
    }

    // determine reader type for these files; assume all are the same type
    Class<? extends IFormatReader> readerClass =
      ((DimensionSwapper) reader).unwrap(files[0]).getClass();

    sizeZ = new int[seriesCount];
    sizeC = new int[seriesCount];
    sizeT = new int[seriesCount];
    boolean[] certain = new boolean[seriesCount];
    lenZ = new int[seriesCount][];
    lenC = new int[seriesCount][];
    lenT = new int[seriesCount][];

    // analyze first file; assume each file has the same parameters
    core.clear();

    int oldSeries = getSeries();
    for (int i=0; i<seriesCount; i++) {
      IFormatReader rr = getReader(i, 0);
      CoreMetadata ms = new CoreMetadata();
      core.add(ms);

      ms.sizeX = rr.getSizeX();
      ms.sizeY = rr.getSizeY();
      // NB: core.sizeZ populated in computeAxisLengths below
      // NB: core.sizeC populated in computeAxisLengths below
      // NB: core.sizeT populated in computeAxisLengths below
      ms.pixelType = rr.getPixelType();

      ExternalSeries external = externals[getExternalSeries(i)];
      ms.imageCount = rr.getImageCount() * external.getFiles().length;
      ms.thumbSizeX = rr.getThumbSizeX();
      ms.thumbSizeY = rr.getThumbSizeY();
      ms.dimensionOrder = rr.getDimensionOrder();
      // NB: core.orderCertain[i] populated below
      ms.rgb = rr.isRGB();
      ms.littleEndian = rr.isLittleEndian();
      ms.interleaved = rr.isInterleaved();
      ms.seriesMetadata = rr.getSeriesMetadata();
      ms.indexed = rr.isIndexed();
      ms.falseColor = rr.isFalseColor();
      ms.bitsPerPixel = rr.getBitsPerPixel();
      sizeZ[i] = rr.getSizeZ();
      sizeC[i] = rr.getSizeC();
      sizeT[i] = rr.getSizeT();
      certain[i] = rr.isOrderCertain();
    }

    // order may need to be adjusted
    for (int i=0; i<seriesCount; i++) {
      setSeries(i);
      AxisGuesser ag = externals[getExternalSeries()].getAxisGuesser();
      core.get(i).dimensionOrder = ag.getAdjustedOrder();
      core.get(i).orderCertain = ag.isCertain();
      computeAxisLengths();
    }
    setSeries(oldSeries);

    // populate metadata store
    store = reader.getMetadataStore();
    // don't overwrite pixel info if files aren't actually grouped
    if (!noStitch) {
      MetadataTools.populatePixels(store, this, false, false);
      if (reader.getSeriesCount() == 1 && getSeriesCount() > 1) {
        for (int i=0; i<getSeriesCount(); i++) {
          int index = getExternalSeries(i);
          String pattern = externals[index].getFilePattern().getPattern();
          pattern = pattern.substring(pattern.lastIndexOf(File.separator) + 1);
          store.setImageName(pattern, i);
        }
      }
    }
  }

  // -- Helper methods --

  private int getExternalSeries() {
    return getExternalSeries(getCoreIndex());
  }

  private int getExternalSeries(int currentSeries) {
    if (reader.getSeriesCount() > 1) return 0;
    return currentSeries;
  }

  /** Computes axis length arrays, and total axis lengths. */
  protected void computeAxisLengths() throws FormatException {
    int sno = getCoreIndex();
    CoreMetadata ms = core.get(sno);
    ExternalSeries s = externals[getExternalSeries()];
    FilePattern p = s.getFilePattern();

    int[] count = p.getCount();

    initReader(sno, 0);

    AxisGuesser ag = s.getAxisGuesser();
    int[] axes = ag.getAxisTypes();

    int numZ = ag.getAxisCountZ();
    int numC = ag.getAxisCountC();
    int numT = ag.getAxisCountT();

    if (axes.length == 0 && s.getFiles().length > 1) {
      axes = new int[] {AxisGuesser.T_AXIS};
      count = new int[] {s.getFiles().length};
      numT++;
    }

    ms.sizeZ = sizeZ[sno];
    ms.sizeC = sizeC[sno];
    ms.sizeT = sizeT[sno];
    lenZ[sno] = new int[numZ + 1];
    lenC[sno] = new int[numC + 1];
    lenT[sno] = new int[numT + 1];
    lenZ[sno][0] = sizeZ[sno];
    lenC[sno][0] = sizeC[sno] / reader.getRGBChannelCount();
    lenT[sno][0] = sizeT[sno];

    for (int i=0, z=1, c=1, t=1; i<count.length; i++) {
      switch (axes[i]) {
        case AxisGuesser.Z_AXIS:
          ms.sizeZ *= count[i];
          lenZ[sno][z++] = count[i];
          break;
        case AxisGuesser.C_AXIS:
          ms.sizeC *= count[i];
          lenC[sno][c++] = count[i];
          break;
        case AxisGuesser.T_AXIS:
          ms.sizeT *= count[i];
          lenT[sno][t++] = count[i];
          break;
        case AxisGuesser.S_AXIS:
          break;
        default:
          throw new FormatException("Unknown axis type for axis #" +
            i + ": " + axes[i]);
      }
    }
    ms.imageCount = ms.sizeZ * ms.sizeT;
    ms.imageCount *= (ms.sizeC / reader.getRGBChannelCount());

    ms.moduloC = reader.getModuloC();
    ms.moduloZ = reader.getModuloZ();
    ms.moduloT = reader.getModuloT();

    if (ms.moduloC.length() % ms.sizeC != 0) {
      ms.moduloC.start = 0;
      ms.moduloC.step = 1;
      ms.moduloC.end = 0;
    }
    if (ms.moduloZ.length() % ms.sizeZ != 0) {
      ms.moduloZ.start = 0;
      ms.moduloZ.step = 1;
      ms.moduloZ.end = 0;
    }
    if (ms.moduloT.length() % ms.sizeT != 0) {
      ms.moduloT.start = 0;
      ms.moduloT.step = 1;
      ms.moduloT.end = 0;
    }
  }

  /**
   * Gets the file index, and image index into that file,
   * corresponding to the given global image index.
   *
   * @return An array of size 2, dimensioned {file index, image index}.
   */
  public int[] computeIndices(int no) throws FormatException, IOException {
    if (noStitch) return new int[] {0, no};
    int sno = getCoreIndex();
    ExternalSeries s = externals[getExternalSeries()];

    int[] axes = s.getAxisGuesser().getAxisTypes();
    int[] count = s.getFilePattern().getCount();

    if (axes.length == 0) {
      axes = new int[] {AxisGuesser.T_AXIS};
      count = new int[] {s.getFiles().length};
    }

    // get Z, C and T positions
    int[] zct = getZCTCoords(no);
    int[] posZ = FormatTools.rasterToPosition(lenZ[sno], zct[0]);
    int[] posC = FormatTools.rasterToPosition(lenC[sno], zct[1]);
    int[] posT = FormatTools.rasterToPosition(lenT[sno], zct[2]);

    int[] tmpZ = new int[posZ.length];
    System.arraycopy(posZ, 0, tmpZ, 0, tmpZ.length);
    int[] tmpC = new int[posC.length];
    System.arraycopy(posC, 0, tmpC, 0, tmpC.length);
    int[] tmpT = new int[posT.length];
    System.arraycopy(posT, 0, tmpT, 0, tmpT.length);

    // convert Z, C and T position lists into file index and image index
    int[] pos = new int[axes.length];
    int z = 1, c = 1, t = 1;
    for (int i=0; i<axes.length; i++) {
      if (axes[i] == AxisGuesser.Z_AXIS) pos[i] = posZ[z++];
      else if (axes[i] == AxisGuesser.C_AXIS) pos[i] = posC[c++];
      else if (axes[i] == AxisGuesser.T_AXIS) pos[i] = posT[t++];
      else if (axes[i] == AxisGuesser.S_AXIS) {
        pos[i] = 0;
      }
      else {
        throw new FormatException("Unknown axis type for axis #" +
          i + ": " + axes[i]);
      }
    }

    int fno = FormatTools.positionToRaster(count, pos);
    DimensionSwapper r = getReader(sno, fno);

    int ino;
    if (posZ[0] < r.getSizeZ() && posC[0] < r.getSizeC() &&
      posT[0] < r.getSizeT())
    {
      if (r.isRGB() && (posC[0] * r.getRGBChannelCount() >= lenC[sno][0])) {
        posC[0] /= lenC[sno][0];
      }
      ino = FormatTools.getIndex(r, posZ[0], posC[0], posT[0]);
    }
    else ino = Integer.MAX_VALUE; // coordinates out of range

    return new int[] {fno, ino};
  }

  protected void initReader(int sno, int fno) {
    int external = getExternalSeries(sno);
    DimensionSwapper r = externals[external].getReader(fno);
      if (r.getCurrentFile() == null) {
        r.setGroupFiles(false);
      }
      r.setSeries(reader.getSeriesCount() > 1 ? sno : 0);
      String newOrder = ((DimensionSwapper) reader).getInputOrder();
      if ((externals[external].getFiles().length > 1 || !r.isOrderCertain()) &&
        (r.getRGBChannelCount() == 1 ||
        newOrder.indexOf("C") == r.getDimensionOrder().indexOf("C")))
      {
        r.swapDimensions(newOrder);
      }
      r.setOutputOrder(newOrder);
  }

  // -- Helper classes --

  class ExternalSeries {
    private DimensionSwapper[] readers;
    private String[] files;
    private FilePattern pattern;
    private byte[] blankThumbBytes;
    private String originalOrder;
    private AxisGuesser ag;
    private int imagesPerFile;

    public ExternalSeries(FilePattern pattern)
      throws FormatException, IOException
    {
      this.pattern = pattern;
      files = this.pattern.getFiles();

      int nReaders = files.length > MAX_READERS ? 1 : files.length;
      readers = new DimensionSwapper[nReaders];
      for (int i=0; i<readers.length; i++) {
        if (classList != null) {
          readers[i] = new DimensionSwapper(new ImageReader(classList));
        }
        else readers[i] = new DimensionSwapper();
        readers[i].setGroupFiles(false);
        readers[i].setId(files[i]);
        readers[i].setMetadataOptions(getMetadataOptions());
      }

      ag = new AxisGuesser(this.pattern, readers[0].getDimensionOrder(),
        readers[0].getSizeZ(), readers[0].getSizeT(),
        readers[0].getSizeC(), readers[0].isOrderCertain());

      blankThumbBytes = new byte[FormatTools.getPlaneSize(readers[0],
        readers[0].getThumbSizeX(), readers[0].getThumbSizeY())];

      originalOrder = readers[0].getDimensionOrder();
      imagesPerFile = readers[0].getImageCount();
    }

    public DimensionSwapper getReader(int fno) {
      if (fno < readers.length) {
        return readers[fno];
      }
      return readers[0];
    }

    public DimensionSwapper[] getReaders() {
      return readers;
    }

    public FilePattern getFilePattern() {
      return pattern;
    }

    public String getOriginalOrder() {
      return originalOrder;
    }

    public AxisGuesser getAxisGuesser() {
      return ag;
    }

    public byte[] getBlankThumbBytes() {
      return blankThumbBytes;
    }

    public String[] getFiles() {
      return files;
    }

    public int getImagesPerFile() {
      return imagesPerFile;
    }

  }

}
