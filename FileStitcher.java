//
// FileStitcher.java
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

package loci.formats;

import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * Logic to stitch together files with similar names. Stitches based on the
 * first series for each file, and assumes that all files have the same
 * dimensions.
 */
public class FileStitcher implements IFormatReader {

  // -- Fields --

  /** FormatReader to use as a template for constituent readers. */
  private IFormatReader reader;

  /**
   * Whether string ids given should be treated
   * as file patterns rather than single file paths.
   */
  private boolean patternIds = false;

  /** Current file pattern string. */
  private String currentId;

  /** File pattern object used to build the list of files. */
  private FilePattern fp;

  /** Axis guesser object used to guess which dimensional axes are which. */
  private AxisGuesser[] ag;

  /** The matching files. */
  private String[] files;

  /** Reader used for each file. */
  private IFormatReader[] readers;

  /** Blank buffered image, for use when image counts vary between files. */
  private BufferedImage[] blankImage;

  /** Blank image bytes, for use when image counts vary between files. */
  private byte[][] blankBytes;

  /** Image dimensions. */
  private int[] width, height;

  /** Number of images per file. */
  private int[] imagesPerFile;

  /** Total number of image planes. */
  private int[] totalImages;

  /** Dimension order. */
  private String[] order;

  /** Dimensional axis lengths per file. */
  private int[] sizeZ, sizeC, sizeT;

  /** Total dimensional axis lengths. */
  private int[] totalSizeZ, totalSizeC, totalSizeT;

  /** Component lengths for each axis type. */
  private int[][] lenZ, lenC, lenT;

  /** Whether or not we're doing channel stat calculation (no by default). */
  protected boolean enableChannelStatCalculation = false;

  /** Whether or not to ignore color tables, if present. */
  protected boolean ignoreColorTable = false;

  /** Whether or not to normalize float data. */
  protected boolean normalizeData = false;

  // -- Constructors --

  /** Constructs a FileStitcher around a new image reader. */
  public FileStitcher() { this(new ImageReader()); }

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
    reader = r;
    this.patternIds = patternIds;
  }

  // -- FileStitcher API methods --

  /**
   * Gets the axis type for each dimensional block.
   * @return An array containing values from the enumeration:
   *   <ul>
   *     <li>AxisGuesser.Z_AXIS: focal planes</li>
   *     <li>AxisGuesser.T_AXIS: time points</li>
   *     <li>AxisGuesser.C_AXIS: channels</li>
   *   </ul>
   */
  public int[] getAxisTypes(String id)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return ag[getSeries(id)].getAxisTypes();
  }

  /**
   * Sets the axis type for each dimensional block.
   * @param axes An array containing values from the enumeration:
   *   <ul>
   *     <li>AxisGuesser.Z_AXIS: focal planes</li>
   *     <li>AxisGuesser.T_AXIS: time points</li>
   *     <li>AxisGuesser.C_AXIS: channels</li>
   *   </ul>
   */
  public void setAxisTypes(String id, int[] axes)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    ag[getSeries(id)].setAxisTypes(axes);
    computeAxisLengths();
  }

  /** Gets the file pattern object used to build the list of files. */
  public FilePattern getFilePattern(String id)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return fp;
  }

  /**
   * Gets the axis guesser object used to guess
   * which dimensional axes are which.
   */
  public AxisGuesser getAxisGuesser(String id)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return ag[getSeries(id)];
  }

  /**
   * Finds the file pattern for the given ID, based on the state of the file
   * stitcher. Takes both ID map entries and the patternIds flag into account.
   */
  public FilePattern findPattern(String id) {
    if (!patternIds) {
      // find the containing pattern
      Hashtable map = getIdMap();
      String pattern = null;
      if (map.containsKey(id)) {
        // search ID map for pattern, rather than files on disk
        String[] idList = new String[map.size()];
        Enumeration en = map.keys();
        for (int i=0; i<idList.length; i++) {
          idList[i] = (String) en.nextElement();
        }
        pattern = FilePattern.findPattern(id, null, idList);
      }
      else {
        // id is an unmapped file path; look to similar files on disk

        // id == getMapped(id)
        pattern = FilePattern.findPattern(new FileWrapper(id));
      }
      if (pattern != null) id = pattern;
    }
    return new FilePattern(id);
  }

  // -- IFormatReader API methods --

  /* @see IFormatReader#isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    return reader.isThisType(block);
  }

  /* @see IFormatReader#getImageCount(String) */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return totalImages[getSeries(id)];
  }

  /* @see IFormatReader#isRGB(String) */
  public boolean isRGB(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return reader.isRGB(files[0]);
  }

  /* @see IFormatReader#getSizeX(String) */
  public int getSizeX(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return width[getSeries(id)];
  }

  /* @see IFormatReader#getSizeY(String) */
  public int getSizeY(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return height[getSeries(id)];
  }

  /* @see IFormatReader#getSizeZ(String) */
  public int getSizeZ(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return totalSizeZ[getSeries(id)];
  }

  /* @see IFormatReader#getSizeC(String) */
  public int getSizeC(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return totalSizeC[getSeries(id)];
  }

  /* @see IFormatReader#getSizeT(String) */
  public int getSizeT(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return totalSizeT[getSeries(id)];
  }

  /* @see IFormatReader#getPixelType(String) */
  public int getPixelType(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return reader.getPixelType(files[0]);
  }

  /* @see IFormatReader#getEffectiveSizeC(String) */
  public int getEffectiveSizeC(String id) throws FormatException, IOException {
    return FormatReader.getEffectiveSizeC(isRGB(id), getSizeC(id));
  }

  /* @see IFormatReader#getChannelGlobalMinimum(String, int) */
  public Double getChannelGlobalMinimum(String id, int theC)
    throws FormatException, IOException
  {
    int[] include = getIncludeList(id, theC);
    Double min = new Double(Double.POSITIVE_INFINITY);
    for (int i=0; i<readers.length; i++) {
      if (include[i] >= 0) {
        Double d = readers[i].getChannelGlobalMinimum(files[i], include[i]);
        if (d.compareTo(min) < 0) min = d;
      }
    }
    return min;
  }

  /* @see IFormatReader#getChannelGlobalMaximum(String, int) */
  public Double getChannelGlobalMaximum(String id, int theC)
    throws FormatException, IOException
  {
    int[] include = getIncludeList(id, theC);
    Double max = new Double(Double.NEGATIVE_INFINITY);
    for (int i=0; i<readers.length; i++) {
      if (include[i] >= 0) {
        Double d = readers[i].getChannelGlobalMaximum(files[i], include[i]);
        if (d.compareTo(max) > 0) max = d;
      }
    }
    return max;
  }

  /* @see IFormatReader#getThumbSizeX(String) */
  public int getThumbSizeX(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return reader.getThumbSizeX(files[0]);
  }

  /* @see IFormatReader#getThumbSizeY(String) */
  public int getThumbSizeY(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return reader.getThumbSizeY(files[0]);
  }

  /* @see IFormatReader#isLittleEndian(String) */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return reader.isLittleEndian(files[0]);
  }

  /**
   * Gets a five-character string representing the
   * dimension order across the file series.
   */
  public String getDimensionOrder(String id)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return order[getSeries(id)];
  }

  /* @see IFormatReader#isOrderCertain(String) */
  public boolean isOrderCertain(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return ag[getSeries(id)].isCertain();
  }

  /* @see IFormatReader#setChannelStatCalculationStatus(boolean) */
  public void setChannelStatCalculationStatus(boolean on) {
    enableChannelStatCalculation = on;
  }

  /* @see IFormatReader#getChannelStatCalculationStatus */
  public boolean getChannelStatCalculationStatus() {
    return enableChannelStatCalculation;
  }

  /* @see IFormatReader#isInterleaved(String) */
  public boolean isInterleaved(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return reader.isInterleaved(files[0]);
  }

  /** Obtains the specified image from the given file series. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    int[] q = computeIndices(id, no);
    int fno = q[0], ino = q[1];
    if (ino < readers[fno].getImageCount(files[fno])) {
      return readers[fno].openImage(files[fno], ino);
    }
    // return a blank image to cover for the fact that
    // this file does not contain enough image planes
    int sno = getSeries(id);
    if (blankImage[sno] == null) {
      blankImage[sno] = ImageTools.blankImage(width[sno], height[sno],
        sizeC[sno], getPixelType(currentId));
    }
    return blankImage[sno];
  }

  /** Obtains the specified image from the given file series as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    int[] q = computeIndices(id, no);
    int fno = q[0], ino = q[1];
    if (ino < readers[fno].getImageCount(files[fno])) {
      return readers[fno].openBytes(files[fno], ino);
    }
    // return a blank image to cover for the fact that
    // this file does not contain enough image planes
    int sno = getSeries(id);
    if (blankBytes[sno] == null) {
      int bytes = FormatReader.getBytesPerPixel(getPixelType(currentId));
      blankBytes[sno] = new byte[width[sno] * height[sno] *
        bytes * (isRGB(id) ? sizeC[sno] : 1)];
    }
    return blankBytes[sno];
  }

  /* @see IFormatReader#openBytes(String, int, byte[]) */
  public byte[] openBytes(String id, int no, byte[] buf)
    throws FormatException, IOException
  {
    int[] q = computeIndices(id, no);
    int fno = q[0], ino = q[1];
    return readers[fno].openBytes(files[fno], ino, buf);
  }

  /* @see IFormatReader#openThumbImage(String, int) */
  public BufferedImage openThumbImage(String id, int no)
    throws FormatException, IOException
  {
    int[] q = computeIndices(id, no);
    int fno = q[0], ino = q[1];
    return readers[fno].openThumbImage(files[fno], ino);
  }

  /* @see IFormatReader#openThumbImage(String, int) */
  public byte[] openThumbBytes(String id, int no)
    throws FormatException, IOException
  {
    int[] q = computeIndices(id, no);
    int fno = q[0], ino = q[1];
    return readers[fno].openThumbBytes(files[fno], ino);
  }

  /* @see IFormatReader#close() */
  public void close() throws FormatException, IOException {
    if (readers != null) {
      for (int i=0; i<readers.length; i++) readers[i].close();
    }
    readers = null;
    blankImage = null;
    blankBytes = null;
    currentId = null;
  }

  /* @see IFormatReader#getSeriesCount(String) */
  public int getSeriesCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return reader.getSeriesCount(files[0]);
  }

  /* @see IFormatReader#setSeries(String, int) */
  public void setSeries(String id, int no) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    reader.setSeries(files[0], no);
  }

  /* @see IFormatReader#getSeries(String) */
  public int getSeries(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return reader.getSeries(files[0]);
  }

  /* @see IFormatReader#setColorTableIgnored(boolean) */
  public void setColorTableIgnored(boolean ignore) {
    ignoreColorTable = ignore;
    reader.setColorTableIgnored(ignore);
    if (readers != null) {
      for (int i=0; i<readers.length; i++) {
        readers[i].setColorTableIgnored(ignore);
      }
    }
  }

  /* @see IFormatReader#isColorTableIgnored() */
  public boolean isColorTableIgnored() { return ignoreColorTable; }

  /* @see IFormatReader#setNormalized(boolean) */
  public void setNormalized(boolean normalize) {
    normalizeData = normalize;
  }

  /* @see IFormatReader#isNormalized() */
  public boolean isNormalized() { return normalizeData; }

  /* @see IFormatReader#getUsedFiles() */
  public String[] getUsedFiles(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return files;
  }

  /* @see IFormatReader#getCurrentFile() */
  public String getCurrentFile() { return currentId; }

  /* @see IFormatReader#swapDimensions(String, String) */
  public void swapDimensions(String id, String dimOrder)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    order[getSeries(id)] = dimOrder;
    String f0 = files[0];
    reader.swapDimensions(f0, dimOrder);
    sizeZ[getSeries(id)] = reader.getSizeZ(f0);
    sizeC[getSeries(id)] = reader.getSizeC(f0);
    sizeT[getSeries(id)] = reader.getSizeT(f0);
    computeAxisLengths();
  }

  /* @see IFormatReader#getIndex(String, int, int, int) */
  public int getIndex(String id, int z, int c, int t)
    throws FormatException, IOException
  {
    return FormatReader.getIndex(this, id, z, c, t);
  }

  /* @see IFormatReader#getZCTCoords(String, int) */
  public int[] getZCTCoords(String id, int index)
    throws FormatException, IOException
  {
    return FormatReader.getZCTCoords(this, id, index);
  }

  /* @see IFormatReader#getMetadataValue(String, String) */
  public Object getMetadataValue(String id, String field)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return reader.getMetadataValue(files[0], field);
  }

  /* @see IFormatReader#getMetadata(String) */
  public Hashtable getMetadata(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return reader.getMetadata(files[0]);
  }

  /* @see IFormatReader#setMetadataStore(MetadataStore) */
  public void setMetadataStore(MetadataStore store) {
    reader.setMetadataStore(store);
  }

  /* @see IFormatReader#getMetadataStore(String) */
  public MetadataStore getMetadataStore(String id)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return reader.getMetadataStore(files[0]);
  }

  /* @see IFormatReader#getMetadataStoreRoot(String) */
  public Object getMetadataStoreRoot(String id)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return reader.getMetadataStoreRoot(files[0]);
  }

  /* @see IFormatReader#testRead(String[]) */
  public boolean testRead(String[] args) throws FormatException, IOException {
    return FormatReader.testRead(this, args);
  }

  // -- IFormatHandler API methods --

  /* @see IFormatHandler#isThisType(String) */
  public boolean isThisType(String name) {
    return reader.isThisType(name);
  }

  /* @see IFormatHandler#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    return reader.isThisType(name, open);
  }

  /* @see IFormatHandler#getFormat() */
  public String getFormat() {
    return reader.getFormat();
  }

  /* @see IFormatHandler#getSuffixes() */
  public String[] getSuffixes() {
    return reader.getSuffixes();
  }

  /* @see IFormatHandler#getFileFilters() */
  public FileFilter[] getFileFilters() {
    return reader.getFileFilters();
  }

  /* @see IFormatHandler#getFileChooser() */
  public JFileChooser getFileChooser() {
    return reader.getFileChooser();
  }

  /* @see IFormatHandler#mapId(String, String) */
  public void mapId(String id, String filename) {
    // NB: all readers share the same ID map
    reader.mapId(id, filename);
  }

  /* @see IFormatHandler#getMappedId(String) */
  public String getMappedId(String id) {
    return reader.getMappedId(id);
  }

  /* @see IFormatHandler#getIdMap() */
  public Hashtable getIdMap() {
    return reader.getIdMap();
  }

  /* @see IFormatHandler#setIdMap(Hashtable) */
  public void setIdMap(Hashtable map) {
    for (int i=0; i<readers.length; i++) readers[i].setIdMap(map);
  }

  // -- Helper methods --

  /** Initializes the given file. */
  protected void initFile(String id) throws FormatException, IOException {
    if (FormatReader.debug) {
      System.out.println("calling FileStitcher.initFile(" + id + ")");
    }

    currentId = id;
    fp = findPattern(id);

    // verify that file pattern is valid and matches existing files
    String msg = " Please rename your files or disable file stitching.";
    if (!fp.isValid()) {
      throw new FormatException("Invalid " +
        (patternIds ? "file pattern" : "filename") +
        " (" + currentId + "): " + fp.getErrorMessage() + msg);
    }
    files = fp.getFiles();
    if (files == null) {
      throw new FormatException("No files matching pattern (" +
        fp.getPattern() + "). " + msg);
    }
    for (int i=0; i<files.length; i++) {
      if (!new FileWrapper(getMappedId(files[i])).exists()) {
        throw new FormatException("File #" + i +
          " (" + files[i] + ") does not exist.");
      }
    }

    // determine reader type for these files; assume all are the same type
    Vector classes = new Vector();
    IFormatReader r = reader;
    while (r instanceof ReaderWrapper) {
      classes.add(r.getClass());
      r = ((ReaderWrapper) r).getReader();
    }
    if (r instanceof ImageReader) r = ((ImageReader) r).getReader(files[0]);
    classes.add(r.getClass());

    // construct list of readers for all files
    readers = new IFormatReader[files.length];
    readers[0] = reader;
    Hashtable map = reader.getIdMap();
    for (int i=1; i<readers.length; i++) {
      // use crazy reflection to instantiate a reader of the proper type
      try {
        r = null;
        for (int j=classes.size()-1; j>=0; j--) {
          Class c = (Class) classes.elementAt(j);
          if (r == null) r = (IFormatReader) c.newInstance();
          else {
            r = (IFormatReader) c.getConstructor(
              new Class[] {IFormatReader.class}).newInstance(new Object[] {r});
          }
        }
        readers[i] = (IFormatReader) r;
        // NB: ensure all readers share the same ID map
        readers[i].setIdMap(map);
      }
      catch (InstantiationException exc) { exc.printStackTrace(); }
      catch (IllegalAccessException exc) { exc.printStackTrace(); }
      catch (NoSuchMethodException exc) { exc.printStackTrace(); }
      catch (InvocationTargetException exc) { exc.printStackTrace(); }
    }
    String f0 = files[0];

    int seriesCount = reader.getSeriesCount(f0);
    ag = new AxisGuesser[seriesCount];
    blankImage = new BufferedImage[seriesCount];
    blankBytes = new byte[seriesCount][];
    width = new int[seriesCount];
    height = new int[seriesCount];
    imagesPerFile = new int[seriesCount];
    totalImages = new int[seriesCount];
    order = new String[seriesCount];
    sizeZ = new int[seriesCount];
    sizeC = new int[seriesCount];
    sizeT = new int[seriesCount];
    boolean[] certain = new boolean[seriesCount];
    totalSizeZ = new int[seriesCount];
    totalSizeC = new int[seriesCount];
    totalSizeT = new int[seriesCount];
    lenZ = new int[seriesCount][];
    lenC = new int[seriesCount][];
    lenT = new int[seriesCount][];

    // analyze first file; assume each file has the same parameters

    int oldSeries = reader.getSeries(f0);
    for (int i=0; i<seriesCount; i++) {
      reader.setSeries(f0, i);
      width[i] = reader.getSizeX(f0);
      height[i] = reader.getSizeY(f0);
      imagesPerFile[i] = reader.getImageCount(f0);
      totalImages[i] = files.length * imagesPerFile[i];
      order[i] = reader.getDimensionOrder(f0);
      sizeZ[i] = reader.getSizeZ(f0);
      sizeC[i] = reader.getSizeC(f0);
      sizeT[i] = reader.getSizeT(f0);
      certain[i] = reader.isOrderCertain(f0);
    }
    reader.setSeries(f0, oldSeries);

    // guess at dimensions corresponding to file numbering
    for (int i=0; i<seriesCount; i++) {
      ag[i] = new AxisGuesser(fp, order[i],
        sizeZ[i], sizeT[i], sizeC[i], certain[i]);
    }

    // order may need to be adjusted
    for (int i=0; i<seriesCount; i++) {
      setSeries(currentId, i);
      order[i] = ag[i].getAdjustedOrder();
      swapDimensions(currentId, order[i]);
    }
    setSeries(currentId, oldSeries);
  }

  /** Computes axis length arrays, and total axis lengths. */
  protected void computeAxisLengths() throws FormatException, IOException {
    int sno = getSeries(currentId);

    int[] count = fp.getCount();
    int[] axes = ag[sno].getAxisTypes();
    int numZ = ag[sno].getAxisCountZ();
    int numC = ag[sno].getAxisCountC();
    int numT = ag[sno].getAxisCountT();

    totalSizeZ[sno] = sizeZ[sno];
    totalSizeC[sno] = sizeC[sno];
    totalSizeT[sno] = sizeT[sno];
    lenZ[sno] = new int[numZ + 1];
    lenC[sno] = new int[numC + 1];
    lenT[sno] = new int[numT + 1];
    lenZ[sno][0] = sizeZ[sno];
    lenC[sno][0] = sizeC[sno];
    lenT[sno][0] = sizeT[sno];
    int z = 1, c = 1, t = 1;
    for (int i=0; i<axes.length; i++) {
      switch (axes[i]) {
        case AxisGuesser.Z_AXIS:
          totalSizeZ[sno] *= count[i];
          lenZ[sno][z++] = count[i];
          break;
        case AxisGuesser.C_AXIS:
          totalSizeC[sno] *= count[i];
          lenC[sno][c++] = count[i];
          break;
        case AxisGuesser.T_AXIS:
          totalSizeT[sno] *= count[i];
          lenT[sno][t++] = count[i];
          break;
        default:
          throw new FormatException("Unknown axis type for axis #" +
            i + ": " + axes[i]);
      }
    }

    // populate metadata store
    String f0 = files[0];
    int pixelType = getPixelType(currentId);
    boolean little = reader.isLittleEndian(f0);
    MetadataStore s = reader.getMetadataStore(f0);
    s.setPixels(new Integer(width[sno]), new Integer(height[sno]),
      new Integer(totalSizeZ[sno]), new Integer(totalSizeC[sno]),
      new Integer(totalSizeT[sno]), new Integer(pixelType),
      new Boolean(!little), order[sno], new Integer(sno));
  }

  /**
   * Gets the file index, and image index into that file,
   * corresponding to the given global image index.
   *
   * @return An array of size 2, dimensioned {file index, image index}.
   */
  protected int[] computeIndices(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    int sno = getSeries(id);

    int[] axes = ag[sno].getAxisTypes();
    int[] count = fp.getCount();

    // get Z, C and T positions
    int[] zct = getZCTCoords(id, no);
    int[] posZ = rasterToPosition(lenZ[sno], zct[0]);
    int[] posC = rasterToPosition(lenC[sno], zct[1]);
    int[] posT = rasterToPosition(lenT[sno], zct[2]);

    // convert Z, C and T position lists into file index and image index
    int[] pos = new int[axes.length];
    int z = 1, c = 1, t = 1;
    for (int i=0; i<axes.length; i++) {
      if (axes[i] == AxisGuesser.Z_AXIS) pos[i] = posZ[z++];
      else if (axes[i] == AxisGuesser.C_AXIS) pos[i] = posC[c++];
      else if (axes[i] == AxisGuesser.T_AXIS) pos[i] = posT[t++];
      else {
        throw new FormatException("Unknown axis type for axis #" +
          i + ": " + axes[i]);
      }
    }
    int fno = positionToRaster(count, pos);
    int ino = FormatReader.getIndex(order[sno], sizeZ[sno], sizeC[sno],
      sizeT[sno], imagesPerFile[sno], isRGB(id), posZ[0], posC[0], posT[0]);

    // configure the reader, in case we haven't done this one yet
    readers[fno].setChannelStatCalculationStatus(enableChannelStatCalculation);
    readers[fno].setSeries(files[fno], reader.getSeries(files[0]));
    readers[fno].setColorTableIgnored(ignoreColorTable);
    readers[fno].swapDimensions(files[fno], order[sno]);

    return new int[] {fno, ino};
  }

  /**
   * Gets a list of readers to include in relation to the given C position.
   * @return Array with indices corresponding to the list of readers, and
   *   values indicating the internal channel index to use for that reader.
   */
  protected int[] getIncludeList(String id, int theC)
    throws FormatException, IOException
  {
    int[] include = new int[readers.length];
    Arrays.fill(include, -1);
    for (int t=0; t<sizeT[getSeries(id)]; t++) {
      for (int z=0; z<sizeZ[getSeries(id)]; z++) {
        int no = getIndex(id, z, theC, t);
        int[] q = computeIndices(id, no);
        int fno = q[0], ino = q[1];
        include[fno] = ino;
      }
    }
    return include;
  }

  // -- Utility methods --

  /**
   * Computes a unique 1-D index corresponding to the multidimensional
   * position given in the pos array, using the specified lengths array
   * as the maximum value at each positional dimension.
   */
  public static int positionToRaster(int[] lengths, int[] pos) {
    int[] offsets = new int[lengths.length];
    if (offsets.length > 0) offsets[0] = 1;
    for (int i=1; i<offsets.length; i++) {
      offsets[i] = offsets[i - 1] * lengths[i - 1];
    }
    int raster = 0;
    for (int i=0; i<pos.length; i++) raster += offsets[i] * pos[i];
    return raster;
  }

  /**
   * Computes a unique 3-D position corresponding to the given raster
   * value, using the specified lengths array as the maximum value at
   * each positional dimension.
   */
  public static int[] rasterToPosition(int[] lengths, int raster) {
    int[] offsets = new int[lengths.length];
    if (offsets.length > 0) offsets[0] = 1;
    for (int i=1; i<offsets.length; i++) {
      offsets[i] = offsets[i - 1] * lengths[i - 1];
    }
    int[] pos = new int[lengths.length];
    for (int i=0; i<pos.length; i++) {
      int q = i < pos.length - 1 ? raster % offsets[i + 1] : raster;
      pos[i] = q / offsets[i];
      raster -= q;
    }
    return pos;
  }

  /**
   * Computes the maximum raster value of a positional array with
   * the given maximum values.
   */
  public static int getRasterLength(int[] lengths) {
    int len = 1;
    for (int i=0; i<lengths.length; i++) len *= lengths[i];
    return len;
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    if (!new FileStitcher().testRead(args)) System.exit(1);
  }

}
