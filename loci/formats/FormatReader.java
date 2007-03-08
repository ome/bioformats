//
// FormatReader.java
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

package loci.formats;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import javax.swing.filechooser.FileFilter;

/** Abstract superclass of all biological file format readers. */
public abstract class FormatReader extends FormatHandler
  implements IFormatReader
{

  // -- Constants --

  /** Indices into the channel min/max array. */
  protected static final int MIN = 0;
  protected static final int MAX = 1;

  /** Default thumbnail width and height. */
  protected static final int THUMBNAIL_DIMENSION = 128;

  /** Identifies the <i>INT8</i> data type used to store pixel values. */
  public static final int INT8 = 0;

  /** Identifies the <i>UINT8</i> data type used to store pixel values. */
  public static final int UINT8 = 1;

  /** Identifies the <i>INT16</i> data type used to store pixel values. */
  public static final int INT16 = 2;

  /** Identifies the <i>UINT16</i> data type used to store pixel values. */
  public static final int UINT16 = 3;

  /** Identifies the <i>INT32</i> data type used to store pixel values. */
  public static final int INT32 = 4;

  /** Identifies the <i>UINT32</i> data type used to store pixel values. */
  public static final int UINT32 = 5;

  /** Identifies the <i>FLOAT</i> data type used to store pixel values. */
  public static final int FLOAT = 6;

  /** Identifies the <i>DOUBLE</i> data type used to store pixel values. */
  public static final int DOUBLE = 7;

  /** Human readable pixel type. */
  private static String[] pixelTypes;
  static {
    pixelTypes = new String[8];
    pixelTypes[FormatReader.INT8] = "int8";
    pixelTypes[FormatReader.UINT8] = "uint8";
    pixelTypes[FormatReader.INT16] = "int16";
    pixelTypes[FormatReader.UINT16] = "uint16";
    pixelTypes[FormatReader.INT32] = "int32";
    pixelTypes[FormatReader.UINT32] = "uint32";
    pixelTypes[FormatReader.FLOAT] = "float";
    pixelTypes[FormatReader.DOUBLE] = "double";
  }

  // -- Static fields --

  /** Debugging flag. */
  public static boolean debug = false;

  /** Debugging level. 1=basic, 2=extended, 3=everything, 4=insane. */
  public static int debugLevel = 1;

  // -- Fields --

  /** Hashtable containing metadata key/value pairs. */
  protected Hashtable metadata;

  /** The number of the current series. */
  protected int series = 0;

  /** Dimension fields. */
  protected int[] sizeX, sizeY, sizeZ, sizeC, sizeT, pixelType, rgbChannelCount;
  protected String[] currentOrder;
  protected boolean[] orderCertain;
  protected double[][][] channelMinMax;

  /** Min and max values for each plane. */
  protected double[][][] planeMinMax;

  /** Whether or not we're doing channel stat calculation (no by default). */
  protected boolean enableChannelStatCalculation = false;

  /** List of image indices that have been read. */
  protected Vector[] imagesRead;

  /** List of minimum values for each image. */
  protected Vector[] minimumValues;

  /** List of maximum values for each image. */
  protected Vector[] maximumValues;

  /** Whether or not min/max computation has finished. */
  protected boolean[] minMaxFinished;

  /** Whether or not to ignore color tables, if present. */
  protected boolean ignoreColorTable = false;

  /** Whether or not to normalize float data. */
  protected boolean normalizeData;

  /** Whether or not to filter out invalid metadata. */
  protected boolean filterMetadata;

  /**
   * Current metadata store. Should <b>never</b> be accessed directly as the
   * semantics of {@link #getMetadataStore(String)} prevent "null" access.
   */
  protected MetadataStore metadataStore = new DummyMetadataStore();

  // -- Constructors --

  /** Constructs a format reader with the given name and default suffix. */
  public FormatReader(String format, String suffix) { super(format, suffix); }

  /** Constructs a format reader with the given name and default suffixes. */
  public FormatReader(String format, String[] suffixes) {
    super(format, suffixes);
  }

  // -- Internal FormatReader API methods --

  /**
   * Initializes the given file (parsing header information, etc.).
   * Most subclasses should override this method to perform
   * initialization operations such as parsing metadata.
   */
  protected void initFile(String id) throws FormatException, IOException {
    if (currentId != null) {
      String[] s = getUsedFiles(currentId);
      for (int i=0; i<s.length; i++) {
        if (id.equals(s[i])) return;
      }
    }

    close();
    currentId = id;
    metadata = new Hashtable();
    imagesRead = new Vector[1];
    minimumValues = new Vector[1];
    maximumValues = new Vector[1];
    Arrays.fill(imagesRead, new Vector());
    Arrays.fill(minimumValues, new Vector());
    Arrays.fill(maximumValues, new Vector());
    minMaxFinished = new boolean[1];
    Arrays.fill(minMaxFinished, false);

    sizeX = new int[1];
    sizeY = new int[1];
    sizeZ = new int[1];
    sizeC = new int[1];
    sizeT = new int[1];
    rgbChannelCount = new int[1];
    pixelType = new int[1];
    currentOrder = new String[1];
    orderCertain = new boolean[] {true};

    // reinitialize the MetadataStore
    getMetadataStore(id).createRoot();
  }

  /**
   * Opens the given file, reads in the first few KB and calls
   * isThisType(byte[]) to check whether it matches this format.
   */
  protected boolean checkBytes(String name, int maxLen) {
    try {
      RandomAccessStream ras = new RandomAccessStream(name);
      long len = ras.length();
      byte[] buf = new byte[len < maxLen ? (int) len : maxLen];
      ras.readFully(buf);
      ras.close();
      return isThisType(buf);
    }
    catch (IOException exc) { return false; }
  }

  /** Adds an entry to the metadata table. */
  protected void addMeta(String key, Object value) {
    if (key == null || value == null) return;
    if (filterMetadata) {
      // verify key & value are not empty
      if (key.length() == 0) return;
      String val = value.toString();
      if (val.length() == 0) return;

      // verify key & value are reasonable length
      int maxLen = 8192;
      if (key.length() > maxLen) return;
      if (val.length() > maxLen) return;

      // verify key & value start with printable characters
      if (key.charAt(0) < 32) return;
      if (val.charAt(0) < 32) return;

      // verify key contains at least one alphabetic character
      if (!key.matches(".*[a-zA-Z].*")) return;
    }
    metadata.put(key, value);
  }

  /** Gets a value from the metadata table. */
  protected Object getMeta(String key) {
    return metadata.get(key);
  }

  /** Issues a debugging statement. */
  protected void debug(String s) {
    // NB: could use a logger class or other means of output here, if desired
    String name = getClass().getName();
    String prefix = "loci.formats.in.";
    if (name.startsWith(prefix)) name = name.substring(prefix.length());
    String msg = System.currentTimeMillis() + ": " + name + ": " + s;
    if (debugLevel > 3) new Exception(msg).printStackTrace();
    else System.out.println(msg);
  }

  // -- IFormatReader API methods --

  /* @see IFormatReader#isThisType(byte[]) */
  public abstract boolean isThisType(byte[] block);

  /* @see IFormatReader#getImageCount(String) */
  public abstract int getImageCount(String id)
    throws FormatException, IOException;

  /* @see IFormatReader#isRGB(String) */
  public boolean isRGB(String id) throws FormatException, IOException {
    return getRGBChannelCount(id) > 1;
  }

  /* @see IFormatReader#getSizeX(String) */
  public int getSizeX(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return sizeX[series];
  }

  /* @see IFormatReader#getSizeY(String) */
  public int getSizeY(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return sizeY[series];
  }

  /* @see IFormatReader#getSizeZ(String) */
  public int getSizeZ(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return sizeZ[series];
  }

  /* @see IFormatReader#getSizeC(String) */
  public int getSizeC(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return sizeC[series];
  }

  /* @see IFormatReader#getSizeT(String) */
  public int getSizeT(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return sizeT[series];
  }

  /* @see IFormatReader#getPixelType(String) */
  public int getPixelType(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return pixelType[series];
  }

  /* @see IFormatReader#getEffectiveSizeC(String) */
  public int getEffectiveSizeC(String id) throws FormatException, IOException {
    // NB: by definition, imageCount == effectiveSizeC * sizeZ * sizeT
    return getImageCount(id) / (getSizeZ(id) * getSizeT(id));
  }

  /* @see IFormatReader#getRGBChannelCount(String) */
  public int getRGBChannelCount(String id) throws FormatException, IOException {
    return getSizeC(id) / getEffectiveSizeC(id);
  }

  /* @see IFormatReader#getChannelGlobalMinimum(String, int) */
  public Double getChannelGlobalMinimum(String id, int theC)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (theC < 0 || theC >= getSizeC(id)) {
      throw new FormatException("Invalid channel index: " + theC);
    }
    if (enableChannelStatCalculation && minMaxFinished[series]) {
      return new Double(channelMinMax[series][theC][MIN]);
    }
    return null;
  }

  /* @see IFormatReader#getChannelGlobalMaximum(String, int) */
  public Double getChannelGlobalMaximum(String id, int theC)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (theC < 0 || theC >= getSizeC(id)) {
      throw new FormatException("Invalid channel index: " + theC);
    }
    if (enableChannelStatCalculation && minMaxFinished[series]) {
      return new Double(channelMinMax[series][theC][MAX]);
    }
    return null;
  }

  /* @see IFormatReader#getChannelKnownMinimum(String, int) */
  public Double getChannelKnownMinimum(String id, int theC)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (enableChannelStatCalculation) {
      return new Double(channelMinMax[series][theC][MIN]);
    }
    return null;
  }

  /* @see IFormatReader#getChannelKnownMaximum(String, int) */
  public Double getChannelKnownMaximum(String id, int theC)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (enableChannelStatCalculation) {
      return new Double(channelMinMax[series][theC][MAX]);
    }
    return null;
  }

  /* @see IFormatReader#getPlaneMinimum(String, int) */
  public Double getPlaneMinimum(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (enableChannelStatCalculation &&
      imagesRead[series].contains(new Integer(no)))
    {
      int ndx = no * getRGBChannelCount(id);
      double min = Double.MAX_VALUE;
      for (int i=ndx; i<ndx+getRGBChannelCount(id); i++) {
        if (planeMinMax[series][i][MIN] < min) {
          min = planeMinMax[series][i][MIN];
        }
      }
      return new Double(min);
    }
    return null;
  }

  /* @see IFormatReader#getPlaneMaximum(String, int) */
  public Double getPlaneMaximum(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (enableChannelStatCalculation &&
      imagesRead[series].contains(new Integer(no)))
    {
      int ndx = no * getRGBChannelCount(id);
      double max = Double.MIN_VALUE;
      for (int i=ndx; i<ndx+getRGBChannelCount(id); i++) {
        if (planeMinMax[series][i][MAX] > max) {
          max = planeMinMax[series][i][MAX];
        }
      }
      return new Double(max);
    }
    return null;
  }

  /* @see IFormatReader#isMinMaxPopulated(String) */
  public boolean isMinMaxPopulated(String id)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return minMaxFinished[series];
  }

  /* @see IFormatReader#getThumbSizeX(String) */
  public int getThumbSizeX(String id) throws FormatException, IOException {
    int sx = getSizeX(id);
    int sy = getSizeY(id);
    return sx > sy ? THUMBNAIL_DIMENSION : sx * THUMBNAIL_DIMENSION / sy;
  }

  /* @see IFormatReader#getThumbSizeY(String) */
  public int getThumbSizeY(String id) throws FormatException, IOException {
    int sx = getSizeX(id);
    int sy = getSizeY(id);
    return sy > sx ? THUMBNAIL_DIMENSION : sy * THUMBNAIL_DIMENSION / sx;
  }

  /* @see IFormatReader.isLittleEndian(String) */
  public abstract boolean isLittleEndian(String id)
    throws FormatException, IOException;

  /* @see IFormatReader#getDimensionOrder(String) */
  public String getDimensionOrder(String id)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return currentOrder[series];
  }

  /* @see IFormatReader.isOrderCertain(String) */
  public boolean isOrderCertain(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return orderCertain[series];
  }

  /* @see IFormatReader#setChannelStatCalculationStatus(boolean) */
  public void setChannelStatCalculationStatus(boolean on) {
    if (currentId != null) {
      System.err.println(
        "Warning: setChannelStatCalculation called with open file.");
    }
    enableChannelStatCalculation = on;
  }

  /* @see IFormatReader#getChannelStatCalculationStatus() */
  public boolean getChannelStatCalculationStatus() {
    return enableChannelStatCalculation;
  }

  /* @see IFormatReader#isInterleaved(String) */
  public abstract boolean isInterleaved(String id)
    throws FormatException, IOException;

  /* @see IFormatReader#openImage(String, int) */
  public abstract BufferedImage openImage(String id, int no)
    throws FormatException, IOException;

  /* @see IFormatReader#openBytes(String, int) */
  public abstract byte[] openBytes(String id, int no)
    throws FormatException, IOException;

  /* @see IFormatReader#openBytes(String, int, byte[]) */
  public byte[] openBytes(String id, int no, byte[] buf)
    throws FormatException, IOException
  {
    buf = openBytes(id, no);
    return buf;
  }

  /* @see IFormatReader#openThumbImage(String, int) */
  public BufferedImage openThumbImage(String id, int no)
    throws FormatException, IOException
  {
    return ImageTools.scale(openImage(id, no),
      getThumbSizeX(id), getThumbSizeY(id), false);
  }

  /* @see IFormatReader#openThumbBytes(String, int) */
  public byte[] openThumbBytes(String id, int no)
    throws FormatException, IOException
  {
    BufferedImage img = openThumbImage(id, no);
    byte[][] bytes = ImageTools.getBytes(img);
    if (bytes.length == 1) return bytes[0];
    byte[] rtn = new byte[getRGBChannelCount(id) * bytes[0].length];
    for (int i=0; i<getRGBChannelCount(id); i++) {
      System.arraycopy(bytes[i], 0, rtn, bytes[0].length * i, bytes[i].length);
    }
    return rtn;
  }

  /* @see IFormatReader#close() */
  public abstract void close() throws FormatException, IOException;

  /* @see IFormatReader#getSeriesCount(String) */
  public int getSeriesCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return 1;
  }

  /* @see IFormatReader#setSeries(String, int) */
  public void setSeries(String id, int no) throws FormatException, IOException {
    if (no < 0 || no >= getSeriesCount(id)) {
      throw new FormatException("Invalid series: " + no);
    }
    series = no;
  }

  /* @see IFormatReader#getSeries(String) */
  public int getSeries(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return series;
  }

  /* @see IFormatReader#setColorTableIgnored(boolean) */
  public void setColorTableIgnored(boolean ignore) {
    if (currentId != null) {
      System.err.println(
        "Warning: setColorTableIgnored called with open file.");
      Exception e = new Exception();
      e.printStackTrace();
    }
    ignoreColorTable = ignore;
  }

  /* @see IFormatReader#isColorTableIgnored() */
  public boolean isColorTableIgnored() {
    return ignoreColorTable;
  }

  /* @see IFormatReader#setNormalized(boolean) */
  public void setNormalized(boolean normalize) {
    if (currentId != null) {
      System.err.println("Warning: setNormalized called with open file.");
    }
    normalizeData = normalize;
  }

  /* @see IFormatReader#isNormalized() */
  public boolean isNormalized() {
    return normalizeData;
  }

  /* @see IFormatReader#getUsedFiles(String) */
  public String[] getUsedFiles(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return new String[] {id};
  }

  /* @see IFormatReader#getCurrentFile() */
  public String getCurrentFile() {
    return currentId == null ? "" : currentId;
  }

  /* @see IFormatReader#swapDimensions(String, String) */
  public void swapDimensions(String id, String order)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (order == null) return;
    if (order.equals(currentOrder[series])) return;

    int[] dims = new int[5];

    int xndx = currentOrder[series].indexOf("X");
    int yndx = currentOrder[series].indexOf("Y");
    int zndx = currentOrder[series].indexOf("Z");
    int cndx = currentOrder[series].indexOf("C");
    int tndx = currentOrder[series].indexOf("T");

    dims[xndx] = sizeX[series];
    dims[yndx] = sizeY[series];
    dims[zndx] = sizeZ[series];
    dims[cndx] = sizeC[series];
    dims[tndx] = sizeT[series];

    sizeX[series] = dims[order.indexOf("X")];
    sizeY[series] = dims[order.indexOf("Y")];
    sizeZ[series] = dims[order.indexOf("Z")];
    sizeC[series] = dims[order.indexOf("C")];
    sizeT[series] = dims[order.indexOf("T")];
    currentOrder[series] = order;

    MetadataStore store = getMetadataStore(id);
    store.setPixels(new Integer(sizeX[series]), new Integer(sizeY[series]),
      new Integer(sizeZ[series]), new Integer(sizeC[series]),
      new Integer(sizeT[series]), null, null, order, new Integer(series), null);
  }

  /* @see IFormatReader#getIndex(String, int, int, int) */
  public int getIndex(String id, int z, int c, int t)
    throws FormatException, IOException
  {
    return getIndex(this, id, z, c, t);
  }

  /* @see IFormatReader#getZCTCoords(String, int) */
  public int[] getZCTCoords(String id, int index)
    throws FormatException, IOException
  {
    return getZCTCoords(this, id, index);
  }

  /* @see IFormatReader#getMetadataValue(String, String) */
  public Object getMetadataValue(String id, String field)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return getMeta(field);
  }

  /* @see IFormatReader#getMetadata */
  public Hashtable getMetadata(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return metadata;
  }

  /* @see IFormatReader#setMetadataFiltered(boolean) */
  public void setMetadataFiltered(boolean filter) {
    if (currentId != null) {
      System.err.println("Warning: setMetadataFiltered called with open file.");
    }
    filterMetadata = filter;
  }

  /* @see IFormatReader#isMetadataFiltered() */
  public boolean isMetadataFiltered() {
    return filterMetadata;
  }

  /* @see IFormatReader#setMetadataStore(MetadataStore) */
  public void setMetadataStore(MetadataStore store) {
    if (currentId != null) {
      System.err.println("Warning: setMetadataStore called with open file.");
    }
    metadataStore = store;
  }

  /* @see IFormatReader#getMetadataStore(String) */
  public MetadataStore getMetadataStore(String id)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return metadataStore;
  }

  /* @see IFormatReader#getMetadataStoreRoot(String) */
  public Object getMetadataStoreRoot(String id)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return getMetadataStore(id).getRoot();
  }

  /* @see FormatReader#testRead(String[]) */
  public boolean testRead(String[] args) throws FormatException, IOException {
    return testRead(this, args);
  }

  // -- IFormatHandler API methods --

  /* @see IFormatHandler#getFileFilters() */
  public FileFilter[] getFileFilters() {
    if (filters == null) {
      filters = new FileFilter[] {new FormatFileFilter(this)};
    }
    return filters;
  }

  // -- Utility methods --

  /**
   * A utility method for test reading a file from the command line,
   * and displaying the results in a simple display.
   */
  public static boolean testRead(IFormatReader reader, String[] args)
    throws FormatException, IOException
  {
    String id = null;
    boolean pixels = true;
    boolean doMeta = true;
    boolean thumbs = false;
    boolean merge = false;
    boolean stitch = false;
    boolean separate = false;
    boolean omexml = false;
    boolean ignoreColors = false;
    boolean normalize = false;
    boolean fastBlit = false;
    int start = 0;
    int end = Integer.MAX_VALUE;
    int series = 0;
    String map = null;
    if (args != null) {
      for (int i=0; i<args.length; i++) {
        if (args[i].startsWith("-") && args.length > 1) {
          if (args[i].equals("-nopix")) pixels = false;
          else if (args[i].equals("-nometa")) doMeta = false;
          else if (args[i].equals("-thumbs")) thumbs = true;
          else if (args[i].equals("-merge")) merge = true;
          else if (args[i].equals("-stitch")) stitch = true;
          else if (args[i].equals("-separate")) separate = true;
          else if (args[i].equals("-nocolors")) ignoreColors = true;
          else if (args[i].equals("-omexml")) omexml = true;
          else if (args[i].equals("-normalize")) normalize = true;
          else if (args[i].equals("-fast")) fastBlit = true;
          else if (args[i].equals("-debug")) debug = true;
          else if (args[i].equals("-level")) {
            try {
              debugLevel = Integer.parseInt(args[++i]);
            }
            catch (Exception exc) { }
          }
          else if (args[i].equals("-range")) {
            try {
              start = Integer.parseInt(args[++i]);
              end = Integer.parseInt(args[++i]);
            }
            catch (Exception exc) { }
          }
          else if (args[i].equals("-series")) {
            try {
              series = Integer.parseInt(args[++i]);
            }
            catch (Exception exc) { }
          }
          else if (args[i].equals("-map")) map = args[++i];
          else System.out.println("Ignoring unknown command flag: " + args[i]);
        }
        else {
          if (id == null) id = args[i];
          else System.out.println("Ignoring unknown argument: " + args[i]);
        }
      }
    }
    if (debug) System.out.println("Debugging at level " + debugLevel);
    if (id == null) {
      String className = reader.getClass().getName();
      String format = reader.getFormat();
      String[] s = {
        "To test read a file in " + format + " format, run:",
        "  java " + className + " [-nopix] [-nometa] [-thumbs] [-merge]",
        "    [-stitch] [-separate] [-nocolors] [-omexml] [-normalize] [-fast]",
        "    [-debug] [-range start end] [-series num] [-map id] file",
        "",
        "      file: the image file to read",
        "    -nopix: read metadata only, not pixels",
        "   -nometa: output only core metadata",
        "   -thumbs: read thumbnails instead of normal pixels",
        "    -merge: combine separate channels into RGB image",
        "   -stitch: stitch files with similar names",
        " -separate: split RGB image into separate channels",
        " -nocolors: ignore color lookup tables, if present",
        "   -omexml: populate OME-XML metadata",
        "-normalize: normalize floating point images*",
        "     -fast: paint RGB images as quickly as possible*",
        "    -debug: turn on debugging output",
        "    -range: specify range of planes to read (inclusive)",
        "   -series: specify which image series to read",
        "      -map: specify file on disk to which name should be mapped",
        "",
        "* = may result in loss of precision",
        ""
      };
      for (int i=0; i<s.length; i++) System.out.println(s[i]);
      return false;
    }
    if (map != null) Location.mapId(id, map);
    if (omexml) {
      try {
        Class c = Class.forName("loci.formats.ome.OMEXMLMetadataStore");
        MetadataStore ms = (MetadataStore) c.newInstance();
        reader.setMetadataStore(ms);
      }
      catch (Throwable t) {
        // NB: error messages for missing OME-Java are printed later
      }
    }

    // check file format
    if (reader instanceof ImageReader) {
      // determine format
      ImageReader ir = (ImageReader) reader;
      System.out.print("Checking file format ");
      System.out.println("[" + ir.getFormat(id) + "]");
    }
    else {
      // verify format
      System.out.print("Checking " + reader.getFormat() + " format ");
      System.out.println(reader.isThisType(id) ? "[yes]" : "[no]");
    }

    if (stitch) {
      reader = new FileStitcher(reader, true);
      String pat = FilePattern.findPattern(new Location(id));
      if (pat != null) id = pat;
    }
    if (separate) reader = new ChannelSeparator(reader);
    if (merge) reader = new ChannelMerger(reader);

    reader.setColorTableIgnored(ignoreColors);
    reader.setNormalized(normalize);
    reader.setMetadataFiltered(true);

    if (!normalize && reader.getPixelType(id) == FLOAT) {
      throw new FormatException("Sorry, unnormalized floating point " +
        "data is not supported. Please use the '-normalize' option.");
    }

    // read basic metadata
    System.out.println();
    System.out.println("Reading core metadata");
    System.out.println(stitch ?
      "File pattern = " + id : "Filename = " + reader.getCurrentFile());
    if (map != null) System.out.println("Mapped filename = " + map);
    String[] used = reader.getUsedFiles(id);
    boolean usedValid = used != null && used.length > 0;
    if (usedValid) {
      for (int u=0; u<used.length; u++) {
        if (used[u] == null) {
          usedValid = false;
          break;
        }
      }
    }
    if (!usedValid) {
      System.out.println(
        "************ Warning: invalid used files list ************");
    }
    if (used == null) {
      System.out.println("Used files = null");
    }
    else if (used.length == 0) {
      System.out.println("Used files = []");
    }
    else if (used.length > 1) {
      System.out.println("Used files:");
      for (int u=0; u<used.length; u++) System.out.println("\t" + used[u]);
    }
    else if (!id.equals(used[0])) {
      System.out.println("Used files = [" + used[0] + "]");
    }
    int seriesCount = reader.getSeriesCount(id);
    System.out.println("Series count = " + seriesCount);
    for (int j=0; j<seriesCount; j++) {
      reader.setSeries(id, j);

      // read basic metadata for series #i
      int imageCount = reader.getImageCount(id);
      boolean rgb = reader.isRGB(id);
      int rgbChanCount = reader.getRGBChannelCount(id);
      boolean interleaved = reader.isInterleaved(id);
      int sizeX = reader.getSizeX(id);
      int sizeY = reader.getSizeY(id);
      int sizeZ = reader.getSizeZ(id);
      int sizeC = reader.getSizeC(id);
      int effSizeC = reader.getEffectiveSizeC(id);
      int sizeT = reader.getSizeT(id);
      int thumbSizeX = reader.getThumbSizeX(id);
      int thumbSizeY = reader.getThumbSizeY(id);
      boolean little = reader.isLittleEndian(id);
      String dimOrder = reader.getDimensionOrder(id);
      boolean orderCertain = reader.isOrderCertain(id);
      int pixelType = reader.getPixelType(id);

      // output basic metadata for series #i
      System.out.println("Series #" + j + ":");
      System.out.println("\tImage count = " + imageCount);
      System.out.print("\tRGB = " + rgb + " (" + rgbChanCount + ")");
      if (merge) System.out.print(" (merged)");
      else if (separate) System.out.print(" (separated)");
      if (rgb != (rgbChanCount != 1)) {
        System.out.println("\t************ Warning: RGB mismatch ************");
      }
      System.out.println();
      System.out.println("\tInterleaved = " + interleaved);
      System.out.println("\tWidth = " + sizeX);
      System.out.println("\tHeight = " + sizeY);
      System.out.println("\tSizeZ = " + sizeZ);
      System.out.print("\tSizeC = " + sizeC);
      if (sizeC != effSizeC) {
        System.out.print(" (effectively " + effSizeC + ")");
      }
      System.out.println();
      System.out.println("\tSizeT = " + sizeT);
      if (imageCount != sizeZ * effSizeC * sizeT) {
        System.out.println("\t************ Warning: ZCT mismatch ************");
      }
      System.out.println("\tThumbnail size = " +
        thumbSizeX + " x " + thumbSizeY);
      System.out.println("\tEndianness = " +
        (little ? "intel (little)" : "motorola (big)"));
      System.out.println("\tDimension order = " + dimOrder +
        (orderCertain ? " (certain)" : " (uncertain)"));
      System.out.println("\tPixel type = " + getPixelTypeString(pixelType));
      if (doMeta) {
        System.out.println("\t-----");
        int[] indices;
        if (imageCount > 6) {
          int q = imageCount / 2;
          indices = new int[] {
            0, q - 2, q - 1, q, q + 1, q + 2, imageCount - 1
          };
        }
        else if (imageCount > 2) {
          indices = new int[] {0, imageCount / 2, imageCount - 1};
        }
        else if (imageCount > 1) indices = new int[] {0, 1};
        else indices = new int[] {0};
        int[][] zct = new int[indices.length][];
        int[] indices2 = new int[indices.length];
        for (int i=0; i<indices.length; i++) {
          zct[i] = reader.getZCTCoords(id, indices[i]);
          indices2[i] = reader.getIndex(id, zct[i][0], zct[i][1], zct[i][2]);
          System.out.print("\tPlane #" + indices[i] + " <=> Z " + zct[i][0] +
            ", C " + zct[i][1] + ", T " + zct[i][2]);
          if (indices[i] != indices2[i]) {
            System.out.println(" [mismatch: " + indices2[i] + "]");
          }
          else System.out.println();
        }
      }
    }
    reader.setSeries(id, series);
    String s = seriesCount > 1 ? (" series #" + series) : "";
    int pixelType = reader.getPixelType(id);

    // read pixels
    if (pixels) {
      System.out.println();
      System.out.print("Reading" + s + " pixel data ");
      long s1 = System.currentTimeMillis();
      int num = reader.getImageCount(id);
      if (start < 0) start = 0;
      if (start >= num) start = num - 1;
      if (end < 0) end = 0;
      if (end >= num) end = num - 1;
      if (end < start) end = start;

      System.out.print("(" + start + "-" + end + ") ");
      long e1 = System.currentTimeMillis();
      BufferedImage[] images = new BufferedImage[end - start + 1];
      long s2 = System.currentTimeMillis();
      boolean mismatch = false;
      for (int i=start; i<=end; i++) {
        if (!fastBlit) {
          images[i - start] = thumbs ?
            reader.openThumbImage(id, i) : reader.openImage(id, i);
        }
        else {
          int x = reader.getSizeX(id);
          int y = reader.getSizeY(id);
          byte[] b = thumbs ? reader.openThumbBytes(id, i) :
            reader.openBytes(id, i);
          Object pix = DataTools.makeDataArray(b,
            FormatReader.getBytesPerPixel(reader.getPixelType(id)),
            reader.getPixelType(id) == FormatReader.FLOAT,
            reader.isLittleEndian(id));
          images[i - start] =
            ImageTools.makeImage(ImageTools.make24Bits(pix, x, y,
              false, false), x, y);
        }

        // check for pixel type mismatch
        int pixType = ImageTools.getPixelType(images[i - start]);
        if (pixType != pixelType && !fastBlit) {
          if (!mismatch) {
            System.out.println();
            mismatch = true;
          }
          System.out.println("\tPlane #" + i + ": pixel type mismatch: " +
            getPixelTypeString(pixType) + "/" + getPixelTypeString(pixelType));
        }
        else {
          mismatch = false;
          System.out.print(".");
        }
      }
      long e2 = System.currentTimeMillis();
      if (!mismatch) System.out.print(" ");
      System.out.println("[done]");

      // output timing results
      float sec = (e2 - s1) / 1000f;
      float avg = (float) (e2 - s2) / images.length;
      long initial = e1 - s1;
      System.out.println(sec + "s elapsed (" +
        avg + "ms per image, " + initial + "ms overhead)");

      // display pixels in image viewer
      ImageViewer viewer = new ImageViewer();
      viewer.setImages(id, reader, images);
      viewer.setVisible(true);
    }

    // read format-specific metadata table
    if (doMeta) {
      System.out.println();
      System.out.println("Reading" + s + " metadata");
      Hashtable meta = reader.getMetadata(id);
      String[] keys = (String[]) meta.keySet().toArray(new String[0]);
      Arrays.sort(keys);
      for (int i=0; i<keys.length; i++) {
        System.out.print(keys[i] + ": ");
        System.out.println(reader.getMetadataValue(id, keys[i]));
      }
    }

    // output OME-XML
    if (omexml) {
      System.out.println();
      System.out.println("Generating OME-XML");
      MetadataStore ms = reader.getMetadataStore(id);

      if (ms.getClass().getName().equals(
        "loci.formats.ome.OMEXMLMetadataStore"))
      {
        try {
          Method m = ms.getClass().getMethod("dumpXML", (Class[]) null);
          System.out.println(m.invoke(ms, (Object[]) null));
          System.out.println();
        }
        catch (Throwable t) {
          System.out.println("Error generating OME-XML:");
          t.printStackTrace();
        }
      }
      else {
        System.out.println("OME-Java library not found; no OME-XML available");
      }
    }

    return true;
  }

  /**
   * Gets the rasterized index corresponding
   * to the given Z, C and T coordinates.
   */
  public static int getIndex(IFormatReader reader, String id,
    int z, int c, int t) throws FormatException, IOException
  {
    String order = reader.getDimensionOrder(id);
    int zSize = reader.getSizeZ(id);
    int cSize = reader.getEffectiveSizeC(id);
    int tSize = reader.getSizeT(id);
    int num = reader.getImageCount(id);
    return getIndex(order, zSize, cSize, tSize, num, z, c, t);
  }

  /**
   * Gets the rasterized index corresponding
   * to the given Z, C and T coordinates.
   */
  public static int getIndex(String order, int zSize, int cSize, int tSize,
    int num, int z, int c, int t)
    throws FormatException, IOException
  {
    // check DimensionOrder
    if (order == null) throw new FormatException("Dimension order is null");
    if (!order.startsWith("XY")) {
      throw new FormatException("Invalid dimension order: " + order);
    }
    int iz = order.indexOf("Z") - 2;
    int ic = order.indexOf("C") - 2;
    int it = order.indexOf("T") - 2;
    if (iz < 0 || iz > 2 || ic < 0 || ic > 2 || it < 0 || it > 2) {
      throw new FormatException("Invalid dimension order: " + order);
    }

    // check SizeZ
    if (zSize <= 0) throw new FormatException("Invalid Z size: " + zSize);
    if (z < 0 || z >= zSize) {
      throw new FormatException("Invalid Z index: " + z + "/" + zSize);
    }

    // check SizeC
    if (cSize <= 0) throw new FormatException("Invalid C size: " + cSize);
    if (c < 0 || c >= cSize) {
      throw new FormatException("Invalid C index: " + c + "/" + cSize);
    }

    // check SizeT
    if (tSize <= 0) throw new FormatException("Invalid T size: " + tSize);
    if (t < 0 || t >= tSize) {
      throw new FormatException("Invalid T index: " + t + "/" + tSize);
    }

    // check image count
    if (num <= 0) throw new FormatException("Invalid image count: " + num);
    if (num != zSize * cSize * tSize) {
      // if this happens, there is probably a bug in metadata population --
      // either one of the ZCT sizes, or the total number of images --
      // or else the input file is invalid
      throw new FormatException("ZCT size vs image count mismatch (sizeZ=" +
        zSize + ", sizeC=" + cSize + ", sizeT=" + tSize + ", total=" + num +
        ")");
    }

    // assign rasterization order
    int v0 = iz == 0 ? z : (ic == 0 ? c : t);
    int v1 = iz == 1 ? z : (ic == 1 ? c : t);
    int v2 = iz == 2 ? z : (ic == 2 ? c : t);
    int len0 = iz == 0 ? zSize : (ic == 0 ? cSize : tSize);
    int len1 = iz == 1 ? zSize : (ic == 1 ? cSize : tSize);
    int len2 = iz == 2 ? zSize : (ic == 2 ? cSize : tSize);

    return v0 + v1 * len0 + v2 * len0 * len1;
  }

  /**
   * Gets the Z, C and T coordinates corresponding
   * to the given rasterized index value.
   */
  public static int[] getZCTCoords(IFormatReader reader,
    String id, int index) throws FormatException, IOException
  {
    String order = reader.getDimensionOrder(id);
    int zSize = reader.getSizeZ(id);
    int cSize = reader.getEffectiveSizeC(id);
    int tSize = reader.getSizeT(id);
    int num = reader.getImageCount(id);
    return getZCTCoords(order, zSize, cSize, tSize, num, index);
  }

  /**
   * Gets the Z, C and T coordinates corresponding to the given rasterized
   * index value.
   */
  public static int[] getZCTCoords(String order,
    int zSize, int cSize, int tSize, int num, int index)
    throws FormatException, IOException
  {
    // check DimensionOrder
    if (order == null) throw new FormatException("Dimension order is null");
    if (!order.startsWith("XY")) {
      throw new FormatException("Invalid dimension order: " + order);
    }
    int iz = order.indexOf("Z") - 2;
    int ic = order.indexOf("C") - 2;
    int it = order.indexOf("T") - 2;
    if (iz < 0 || iz > 2 || ic < 0 || ic > 2 || it < 0 || it > 2) {
      throw new FormatException("Invalid dimension order: " + order);
    }

    // check SizeZ
    if (zSize <= 0) throw new FormatException("Invalid Z size: " + zSize);

    // check SizeC
    if (cSize <= 0) throw new FormatException("Invalid C size: " + cSize);

    // check SizeT
    if (tSize <= 0) throw new FormatException("Invalid T size: " + tSize);

    // check image count
    if (num <= 0) throw new FormatException("Invalid image count: " + num);
    if (num != zSize * cSize * tSize) {
      // if this happens, there is probably a bug in metadata population --
      // either one of the ZCT sizes, or the total number of images --
      // or else the input file is invalid
      throw new FormatException("ZCT size vs image count mismatch (sizeZ=" +
        zSize + ", sizeC=" + cSize + ", sizeT=" + tSize + ", total=" + num +
        ")");
    }
    if (index < 0 || index >= num) {
      throw new FormatException("Invalid image index: " + index + "/" + num);
    }

    // assign rasterization order
    int len0 = iz == 0 ? zSize : (ic == 0 ? cSize : tSize);
    int len1 = iz == 1 ? zSize : (ic == 1 ? cSize : tSize);
    //int len2 = iz == 2 ? sizeZ : (ic == 2 ? sizeC : sizeT);
    int v0 = index % len0;
    int v1 = index / len0 % len1;
    int v2 = index / len0 / len1;
    int z = iz == 0 ? v0 : (iz == 1 ? v1 : v2);
    int c = ic == 0 ? v0 : (ic == 1 ? v1 : v2);
    int t = it == 0 ? v0 : (it == 1 ? v1 : v2);

    return new int[] {z, c, t};
  }

  /** Returns true if the given file name is in the used files list. */
  public boolean isUsedFile(String id, String file)
    throws FormatException, IOException
  {
    String[] usedFiles = getUsedFiles(id);
    for (int i=0; i<usedFiles.length; i++) {
      if (usedFiles[i].equals(file) ||
        usedFiles[i].equals(new Location(file).getAbsolutePath()))
      {
        return true;
      }
    }
    return false;
  }

  /**
   * Updates min/max values based on the given BufferedImage.
   * Should be called by each format reader's openImage method.
   */
  public void updateMinMax(BufferedImage b, int ndx)
    throws FormatException, IOException
  {
    Integer ii = new Integer(ndx);
    if (!imagesRead[series].contains(ii) && enableChannelStatCalculation) {
      if (channelMinMax == null) {
        channelMinMax =
          new double[getSeriesCount(currentId)][getSizeC(currentId)][2];
      }
      if (planeMinMax == null) {
        planeMinMax = new double[getSeriesCount(currentId)][
          getSizeZ(currentId) * getSizeC(currentId) * getSizeT(currentId)][2];
      }

      imagesRead[series].add(ii);
      int[] coords = getZCTCoords(currentId, ndx);
      int numRGB = getRGBChannelCount(currentId);
      WritableRaster pixels = b.getRaster();
      for (int x=0; x<b.getWidth(); x++) {
        for (int y=0; y<b.getHeight(); y++) {
          for (int c=0; c<numRGB; c++) {
            double value = pixels.getSampleDouble(x, y, c);
            if (value > channelMinMax[series][coords[1]*numRGB + c][MAX]) {
              channelMinMax[series][coords[1]*numRGB + c][MAX] = value;
            }
            if (value < channelMinMax[series][coords[1]*numRGB + c][MIN]) {
              channelMinMax[series][coords[1]*numRGB + c][MIN] = value;
            }
            if (value > planeMinMax[series][ndx*numRGB + c][MAX]) {
              planeMinMax[series][ndx*numRGB + c][MAX] = value;
            }
            if (value < planeMinMax[series][ndx*numRGB + c][MIN]) {
              planeMinMax[series][ndx*numRGB + c][MIN] = value;
            }
          }
        }
      }

      if (imagesRead[series].size() == getImageCount(currentId)) {
        minMaxFinished[series] = true;
      }
    }
  }

  /**
   * Updates min/max values based on given byte array.
   * Should be called by each format reader's openBytes method.
   */
  public void updateMinMax(byte[] b, int ndx)
    throws FormatException, IOException
  {
    if (b == null) return;
    Integer ii = new Integer(ndx);
    if (!imagesRead[series].contains(ii) && enableChannelStatCalculation) {
      if (channelMinMax == null) {
        channelMinMax =
          new double[getSeriesCount(currentId)][getSizeC(currentId)][2];
      }
      if (planeMinMax == null) {
        planeMinMax = new double[getSeriesCount(currentId)][
          getSizeZ(currentId) * getSizeC(currentId) * getSizeT(currentId)][2];
      }

      boolean little = isLittleEndian(currentId);
      int bytes = getBytesPerPixel(getPixelType(currentId));
      int numRGB = getRGBChannelCount(currentId);
      int pixels = getSizeX(currentId) * getSizeY(currentId);
      boolean interleaved = isInterleaved(currentId);

      byte[] value = new byte[bytes];
      int[] coords = getZCTCoords(currentId, ndx);

      for (int i=0; i<pixels; i++) {
        for (int c=0; c<numRGB; c++) {
          int idx = interleaved ? i*numRGB + c : c*pixels + i;
          idx *= bytes;
          System.arraycopy(b, idx, value, 0, bytes);
          double v =
            Double.longBitsToDouble(DataTools.bytesToLong(value, little));
          if (v > channelMinMax[series][coords[1]*numRGB + c][MAX]) {
            channelMinMax[series][coords[1]*numRGB + c][MAX] = v;
          }
          if (v < channelMinMax[series][coords[1]*numRGB + c][MIN]) {
            channelMinMax[series][coords[1]*numRGB + c][MIN] = v;
          }
          if (v > planeMinMax[series][ndx*numRGB + c][MAX]) {
            planeMinMax[series][ndx*numRGB + c][MAX] = v;
          }
          if (v < planeMinMax[series][ndx*numRGB + c][MIN]) {
            planeMinMax[series][ndx*numRGB + c][MIN] = v;
          }
        }
      }
    }
  }

  /**
   * Takes a string value and maps it to one of the pixel type enumerations.
   * @param pixelTypeAsString the pixel type as a string.
   * @return type enumeration value for use with class constants.
   */
  public static int pixelTypeFromString(String pixelTypeAsString) {
    String lowercaseTypeAsString = pixelTypeAsString.toLowerCase();
    for (int i = 0; i < pixelTypes.length; i++) {
      if (pixelTypes[i].equals(lowercaseTypeAsString)) return i;
    }
    throw new RuntimeException("Unknown type: '" + pixelTypeAsString + "'");
  }

  /**
   * Takes a pixel type value and gets a corresponding string representation.
   * @param pixelType the pixel type.
   * @return string value for human-readable output.
   */
  public static String getPixelTypeString(int pixelType) {
    return pixelType < 0 || pixelType >= pixelTypes.length ?
      "unknown (" + pixelType + ")" : pixelTypes[pixelType];
  }

  /**
   * Retrieves how many bytes per pixel the current plane or section has.
   * @param type the pixel type as retrieved from
   *   {@link IFormatReader#getPixelType(String)}.
   * @return the number of bytes per pixel.
   * @see IFormatReader#getPixelType(String)
   */
  public static int getBytesPerPixel(int type) {
    switch (type) {
      case FormatReader.INT8:
      case FormatReader.UINT8:
        return 1;
      case FormatReader.INT16:
      case FormatReader.UINT16:
        return 2;
      case FormatReader.INT32:
      case FormatReader.UINT32:
      case FormatReader.FLOAT:
        return 4;
      case FormatReader.DOUBLE:
        return 8;
    }
    throw new RuntimeException("Unknown type with id: '" + type + "'");
  }

  /** Toggles debug mode (more verbose output and error messages). */
  public static void setDebug(boolean debug) {
    FormatReader.debug = debug;
  }

  /**
   * Toggles debug mode verbosity (which kinds of output are produced).
   * @param debugLevel 1=basic, 2=extended, 3=everything.
   */
  public static void setDebugLevel(int debugLevel) {
    FormatReader.debugLevel = debugLevel;
  }
}
