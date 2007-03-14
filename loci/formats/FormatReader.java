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
import java.io.IOException;
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
  protected boolean enableChannelStats = false;

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

  /**
   * Updates min/max values based on the given BufferedImage.
   * Should be called by each format reader's openImage method.
   */
  protected void updateMinMax(BufferedImage b, int ndx)
    throws FormatException, IOException
  {
    if (b == null || !enableChannelStats) return;
    Integer ii = new Integer(ndx);
    if (imagesRead[series].contains(ii)) return;
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

  /**
   * Updates min/max values based on given byte array.
   * Should be called by each format reader's openBytes method.
   */
  protected void updateMinMax(byte[] b, int ndx)
    throws FormatException, IOException
  {
    if (b == null || !enableChannelStats) return;
    Integer ii = new Integer(ndx);
    if (imagesRead[series].contains(ii)) return;
    if (channelMinMax == null) {
      channelMinMax =
        new double[getSeriesCount(currentId)][getSizeC(currentId)][2];
    }
    if (planeMinMax == null) {
      planeMinMax = new double[getSeriesCount(currentId)][
        getSizeZ(currentId) * getSizeC(currentId) * getSizeT(currentId)][2];
    }

    boolean little = isLittleEndian(currentId);
    int bytes = FormatTools.getBytesPerPixel(getPixelType(currentId));
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

  /** Returns true if the given file name is in the used files list. */
  protected boolean isUsedFile(String id, String file)
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
    if (enableChannelStats && minMaxFinished[series]) {
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
    if (enableChannelStats && minMaxFinished[series]) {
      return new Double(channelMinMax[series][theC][MAX]);
    }
    return null;
  }

  /* @see IFormatReader#getChannelKnownMinimum(String, int) */
  public Double getChannelKnownMinimum(String id, int theC)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (enableChannelStats) {
      return new Double(channelMinMax[series][theC][MIN]);
    }
    return null;
  }

  /* @see IFormatReader#getChannelKnownMaximum(String, int) */
  public Double getChannelKnownMaximum(String id, int theC)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (enableChannelStats) {
      return new Double(channelMinMax[series][theC][MAX]);
    }
    return null;
  }

  /* @see IFormatReader#getPlaneMinimum(String, int) */
  public Double getPlaneMinimum(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    if (enableChannelStats &&
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
    if (enableChannelStats &&
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
    enableChannelStats = on;
  }

  /* @see IFormatReader#getChannelStatCalculationStatus() */
  public boolean getChannelStatCalculationStatus() {
    return enableChannelStats;
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
    return FormatTools.getIndex(this, id, z, c, t);
  }

  /* @see IFormatReader#getZCTCoords(String, int) */
  public int[] getZCTCoords(String id, int index)
    throws FormatException, IOException
  {
    return FormatTools.getZCTCoords(this, id, index);
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
    return FormatTools.testRead(this, args);
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
