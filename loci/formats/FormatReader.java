//
// FormatReader.java
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
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Hashtable;
import javax.swing.filechooser.FileFilter;

/** Abstract superclass of all biological file format readers. */
public abstract class FormatReader extends FormatHandler
  implements IFormatReader
{

  // -- Constants --

  /** Debugging flag. */
  protected static final boolean DEBUG = false;

  /** Debugging level. 1=basic, 2=extended, 3=everything. */
  protected static final int DEBUG_LEVEL = 1;

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

  // -- Fields --

  /** Hashtable containing metadata key/value pairs. */
  protected Hashtable metadata;

  /** The number of the current series. */
  protected int series = 0;

  /** Dimension fields. */
  protected int[] sizeX, sizeY, sizeZ, sizeC, sizeT, pixelType;
  protected String[] currentOrder;

  /** Whether or not we're doing channel stat calculation (no by default). */
  protected boolean enableChannelStatCalculation = false;

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
    close();
    currentId = id;
    metadata = new Hashtable();

    sizeX = new int[1];
    sizeY = new int[1];
    sizeZ = new int[1];
    sizeC = new int[1];
    sizeT = new int[1];
    pixelType = new int[1];
    currentOrder = new String[1];

    // reinitialize the MetadataStore
    getMetadataStore(id).createRoot();
  }

  /**
   * Opens the given file, reads in the first few KB and calls
   * isThisType(byte[]) to check whether it matches this format.
   */
  protected boolean checkBytes(String name, int maxLen) {
    long len = new File(name).length();
    byte[] buf = new byte[len < maxLen ? (int) len : maxLen];
    try {
      DataInputStream fin = new DataInputStream(new FileInputStream(name));
      fin.readFully(buf);
      fin.close();
      return isThisType(buf);
    }
    catch (IOException e) { return false; }
  }

  // -- IFormatReader API methods --

  /** Checks if the given block is a valid header for this file format. */
  public abstract boolean isThisType(byte[] block);

  /** Determines the number of images in the given file. */
  public abstract int getImageCount(String id)
    throws FormatException, IOException;

  /** Checks if the images in the file are RGB. */
  public abstract boolean isRGB(String id)
    throws FormatException, IOException;

  /** Get the size of the X dimension. */
  public int getSizeX(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return sizeX[series];
  }

  /** Get the size of the Y dimension. */
  public int getSizeY(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return sizeY[series];
  }

  /** Get the size of the Z dimension. */
  public int getSizeZ(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return sizeZ[series];
  }

  /** Get the size of the C dimension. */
  public int getSizeC(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return sizeC[series];
  }

  /** Get the size of the T dimension. */
  public int getSizeT(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return sizeT[series];
  }

  /* @see loci.formats.IFormatReader#getPixelType(String) */
  public int getPixelType(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return pixelType[series];
  }

  /* @see loci.formats.IFormatReader#getChannelGlobalMinimum(String, int) */
  public Double getChannelGlobalMinimum(String id, int theC)
    throws FormatException, IOException
  {
    return null;
  }

  /* @see loci.formats.IFormatReader#getChannelGlobalMaximum(String, int) */
  public Double getChannelGlobalMaximum(String id, int theC)
    throws FormatException, IOException
  {
    return null;
  }

  /** Get the size of the X dimension for the thumbnail. */
  public int getThumbSizeX(String id) throws FormatException, IOException {
    return THUMBNAIL_DIMENSION;
  }

  /** Get the size of the Y dimension for the thumbnail. */
  public int getThumbSizeY(String id) throws FormatException, IOException {
    return THUMBNAIL_DIMENSION;
  }

  /** Return true if the data is in little-endian format. */
  public abstract boolean isLittleEndian(String id)
    throws FormatException, IOException;

  /**
   * Return a five-character string representing the dimension order
   * within the file.
   */
  public String getDimensionOrder(String id)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return currentOrder[series];
  }

  /* @see loci.formats.IFormatReader#setChannelStatCalculationStatus(boolean) */
  public void setChannelStatCalculationStatus(boolean on) {
    enableChannelStatCalculation = on;
  }

  /* @see loci.formats.IFormatReader#getChannelStatCalculationStatus() */
  public boolean getChannelStatCalculationStatus() {
    return enableChannelStatCalculation;
  }

  /** Returns whether or not the channels are interleaved. */
  public abstract boolean isInterleaved(String id)
    throws FormatException, IOException;

  /** Obtains the specified image from the given file. */
  public abstract BufferedImage openImage(String id, int no)
    throws FormatException, IOException;

  /**
   * Obtains the specified image from the given file as a byte array.
   */
  public abstract byte[] openBytes(String id, int no)
    throws FormatException, IOException;

  /* @see loci.formats.IFormatReader#openBytes(java.lang.String, int, byte[]) */
  public byte[] openBytes(String id, int no, byte[] buf)
    throws FormatException, IOException
  {
    buf = openBytes(id, no);
    return buf;
  }

  /** Obtains a thumbnail for the specified image from the given file. */
  public BufferedImage openThumbImage(String id, int no)
    throws FormatException, IOException
  {
    return ImageTools.scale(openImage(id, no),
      getThumbSizeX(id), getThumbSizeY(id), true, true);
  }

  /**
   * Obtains a thumbnail for the specified image from the given file,
   * as a byte array.  We assume that the thumbnail has the same number of
   * channels as the original image.  If there is more than one channel, then
   * the resulting byte array will be of the format "RRR...BBB...GGG...".
   */
  public byte[] openThumbBytes(String id, int no)
    throws FormatException, IOException
  {
    BufferedImage img = openThumbImage(id, no);
    byte[][] bytes = ImageTools.getBytes(img);
    if (bytes.length == 1) return bytes[0];
    byte[] rtn = new byte[bytes.length * bytes[0].length];
    for (int i=0; i<bytes.length; i++) {
      System.arraycopy(bytes[i], 0, rtn, bytes[0].length * i, bytes[i].length);
    }
    return rtn;
  }

  /** Closes the currently open file. */
  public abstract void close() throws FormatException, IOException;

  /**
   * Opens an existing file from the given filename.
   *
   * @return Java Images containing pixel data
   */
  public BufferedImage[] openImage(String id)
    throws FormatException, IOException
  {
    int nImages = getImageCount(id);
    BufferedImage[] images = new BufferedImage[nImages];
    for (int i=0; i<nImages; i++) images[i] = openImage(id, i);
    close();
    return images;
  }

  /** Return the number of series in this file. */
  public int getSeriesCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return 1;
  }

  /** Activates the specified series. */
  public void setSeries(String id, int no) throws FormatException, IOException {
    if (no < 0 || no >= getSeriesCount(id)) {
      throw new FormatException("Invalid series: " + no);
    }
    series = no;
  }

  /** Returns the currently active series. */
  public int getSeries(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return series;
  }

  /**
   * Swaps the dimensions according to the given dimension order.  If the given
   * order is identical to the file's native order, then nothing happens.
   */
  public void swapDimensions(String id, String order)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
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
      new Integer(sizeT[series]), null, null, order, new Integer(series));
  }

  /**
   * Gets the rasterized index corresponding
   * to the given Z, C and T coordinates.
   */
  public int getIndex(String id, int z, int c, int t)
    throws FormatException, IOException
  {
    return getIndex(this, id, z, c, t);
  }

  /**
   * Gets the Z, C and T coordinates corresponding
   * to the given rasterized index value.
   */
  public int[] getZCTCoords(String id, int index)
    throws FormatException, IOException
  {
    return getZCTCoords(this, id, index);
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
    if (!id.equals(currentId)) initFile(id);
    return metadata.get(field);
  }

  /**
   * Obtains the hashtable containing the metadata field/value pairs from
   * the given file.
   *
   * @param id the filename
   * @return the hashtable containing all metadata from the file
   */
  public Hashtable getMetadata(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return metadata;
  }

  /**
   * Sets the default metadata store for this reader.
   *
   * @param store a metadata store implementation.
   */
  public void setMetadataStore(MetadataStore store) { metadataStore = store; }

  /**
   * Retrieves the current metadata store for this reader. You can be
   * assured that this method will <b>never</b> return a <code>null</code>
   * metadata store.
   * @return a metadata store implementation.
   */
  public MetadataStore getMetadataStore(String id)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return metadataStore;
  }

  /**
   * Retrieves the current metadata store's root object. It is guaranteed that
   * all file parsing has been performed by the reader prior to retrieval.
   * Requests for a full populated root object should be made using this method.
   * @param id a fully qualified path to the file.
   * @return current metadata store's root object fully populated.
   * @throws IOException if there is an IO error when reading the file specified
   * by <code>path</code>.
   * @throws FormatException if the file specified by <code>path</code> is of an
   * unsupported type.
   */
  public Object getMetadataStoreRoot(String id)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return getMetadataStore(id).getRoot();
  }

  /* @see loci.formats.IFormatReader#testRead(String[]) */
  public boolean testRead(String[] args) throws FormatException, IOException {
    return testRead(this, args);
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#getFileFilters() */
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
    int start = 0;
    int end = 0;
    int series = 0;
    if (args != null) {
      for (int i=0; i<args.length; i++) {
        if (args[i].startsWith("-")) {
          if (args[i].equals("-nopix")) pixels = false;
          else if (args[i].equals("-nometa")) doMeta = false;
          else if (args[i].equals("-thumbs")) thumbs = true;
          else if (args[i].equals("-merge")) merge = true;
          else if (args[i].equals("-stitch")) stitch = true;
          else if (args[i].equals("-separate")) separate = true;
          else if (args[i].equals("-omexml")) omexml = true;
          else if (args[i].equals("-range")) {
            try {
              start = Integer.parseInt(args[++i]);
              end = Integer.parseInt(args[++i]);
            }
            catch (Exception e) { }
          }
          else if (args[i].equals("-series")) {
            try {
              series = Integer.parseInt(args[++i]);
            }
            catch (Exception e) { }
          }
          else System.out.println("Ignoring unknown command flag: " + args[i]);
        }
        else {
          if (id == null) id = args[i];
          else System.out.println("Ignoring unknown argument: " + args[i]);
        }
      }
    }
    if (id == null) {
      String className = reader.getClass().getName();
      System.out.println("To test read a file in " +
        reader.getFormat() + " format, run:");
      System.out.println("  java " + className +
        " [-nopix] [-merge] [-stitch]");
      System.out.println("    [-separate] [-omexml] " +
        "[-range start end] [-series num] file");
      System.out.println();
      System.out.println("     file: the image file to read");
      System.out.println("   -nopix: read metadata only, not pixels");
      System.out.println("  -nometa: output only core metadata");
      System.out.println("  -thumbs: read thumbnails instead of normal pixels");
      System.out.println("   -merge: combine separate channels into RGB image");
      System.out.println("  -stitch: stitch files with similar names");
      System.out.println("-separate: split RGB image into separate channels");
      System.out.println("  -omexml: populate OME-XML metadata");
      System.out.println("   -range: specify range of planes to read");
      System.out.println("  -series: specify which image series to read");
      System.out.println();
      return false;
    }
    if (omexml) {
      try {
        Class c = Class.forName("loci.formats.OMEXMLMetadataStore");
        MetadataStore ms = (MetadataStore) c.newInstance();
        ms.createRoot();
        reader.setMetadataStore(ms);
      }
      catch (Exception exc) { }
    }

    // check type
    System.out.print("Checking " + reader.getFormat() + " format ");
    System.out.println(reader.isThisType(id) ? "[yes]" : "[no]");

    if (stitch) reader = new FileStitcher(reader);
    if (separate) reader = new ChannelSeparator(reader);
    if (merge) reader = new ChannelMerger(reader);

    // read basic metadata
    System.out.println();
    System.out.println("Reading core metadata");
    System.out.println(stitch ?
      ("File pattern = " + FilePattern.findPattern(new File(id))) :
      ("Filename = " + id));
    int seriesCount = reader.getSeriesCount(id);
    System.out.println("Series count = " + seriesCount);
    for (int j=0; j<seriesCount; j++) {
      reader.setSeries(id, j);

      // read basic metadata for series #i
      int imageCount = reader.getImageCount(id);
      boolean rgb = reader.isRGB(id);
      int sizeX = reader.getSizeX(id);
      int sizeY = reader.getSizeY(id);
      int sizeZ = reader.getSizeZ(id);
      int sizeC = reader.getSizeC(id);
      int sizeT = reader.getSizeT(id);
      int thumbSizeX = reader.getThumbSizeX(id);
      int thumbSizeY = reader.getThumbSizeY(id);
      boolean little = reader.isLittleEndian(id);
      String dimOrder = reader.getDimensionOrder(id);
      int pixelType = reader.getPixelType(id);

      // output basic metadata for series #i
      System.out.println("Series #" + j + ":");
      System.out.println("\tImage count = " + imageCount);
      System.out.print("\tRGB = " + rgb);
      if (merge) System.out.print(" (merged)");
      else if (separate) System.out.print(" (separated)");
      System.out.println();
      System.out.println("\tWidth = " + sizeX);
      System.out.println("\tHeight = " + sizeY);
      System.out.println("\tSizeZ = " + sizeZ);
      System.out.println("\tSizeC = " + sizeC);
      System.out.println("\tSizeT = " + sizeT);
      if (imageCount != sizeZ * sizeC * sizeT /
        ((rgb || (sizeC > 1 && merge)) ? sizeC : 1))
      {
        System.out.println("\t************ Warning: ZCT mismatch ************");
      }
      System.out.println("\tThumbnail size = " +
        thumbSizeX + " x " + thumbSizeY);
      System.out.println("\tEndianness = " +
        (little ? "intel (little)" : "motorola (big)"));
      System.out.println("\tDimension order = " + dimOrder);
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
      if (end == 0 || end > num) end = num;
      if (end < 0) end = 0;
      if (start < 0) start = 0;
      if (start >= num) start = num - 1;

      System.out.print("(" + (end - start) + ") ");
      long e1 = System.currentTimeMillis();
      BufferedImage[] images = new BufferedImage[end - start];
      long s2 = System.currentTimeMillis();
      boolean mismatch = false;
      for (int i=start; i<end; i++) {
        images[i - start] = thumbs ?
          reader.openThumbImage(id, i) : reader.openImage(id, i);

        // check for pixel type mismatch
        int pixType = ImageTools.getPixelType(images[i - start]);
        if (pixType != pixelType) {
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
      float avg = (float) (e2 - s2) / (end - start);
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
      try {
        Method m = ms.getClass().getMethod("dumpXML", (Class[]) null);
        System.out.println(m.invoke(ms, (Object[]) null));
        System.out.println();
      }
      catch (Exception exc) {
        System.err.println("OME-XML functionality not available:");
        exc.printStackTrace();
      }
    }

    return true;
  }

  /**
   * Gets the rasterized index corresponding
   * to the given Z, C and T coordinates.
   */
  public static int getIndex(IFormatReader reader,
    String id, int z, int c, int t) throws FormatException, IOException
  {
    // get DimensionOrder
    String order = reader.getDimensionOrder(id);
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

    // get SizeZ
    int zSize = reader.getSizeZ(id);
    if (zSize <= 0) throw new FormatException("Invalid Z size: " + zSize);
    if (z < 0 || z >= zSize) {
      throw new FormatException("Invalid Z index: " + z + "/" + zSize);
    }

    // get SizeC
    int cSize = reader.getSizeC(id);
    if (cSize <= 0) throw new FormatException("Invalid C size: " + cSize);
    if (c < 0 || c >= cSize) {
      throw new FormatException("Invalid C index: " + c + "/" + cSize);
    }
    int origSizeC = cSize;
    boolean rgb = reader.isRGB(id);
    if (rgb) cSize = 1;

    // get SizeT
    int tSize = reader.getSizeT(id);
    if (tSize <= 0) throw new FormatException("Invalid T size: " + tSize);
    if (t < 0 || t >= tSize) {
      throw new FormatException("Invalid T index: " + t + "/" + tSize);
    }

    // get image count
    int num = reader.getImageCount(id);
    if (num <= 0) throw new FormatException("Invalid image count: " + num);
    if (num != zSize * cSize * tSize) {
      // if this happens, there is probably a bug in metadata population --
      // either one of the ZCT sizes, or the total number of images --
      // or else the input file is invalid
      throw new FormatException("ZCT size vs image count mismatch (rgb=" +
        rgb + "; sizeZ=" + zSize + ", sizeC=" +
        origSizeC + ", sizeT=" + tSize + ", total=" + num);
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
    // get DimensionOrder
    String order = reader.getDimensionOrder(id);
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

    // get SizeZ
    int zSize = reader.getSizeZ(id);
    if (zSize <= 0) throw new FormatException("Invalid Z size: " + zSize);

    // get SizeC
    int cSize = reader.getSizeC(id);
    if (cSize <= 0) throw new FormatException("Invalid C size: " + cSize);
    int origSizeC = cSize;
    boolean rgb = reader.isRGB(id);
    if (rgb) cSize = 1;

    // get SizeT
    int tSize = reader.getSizeT(id);
    if (tSize <= 0) throw new FormatException("Invalid T size: " + tSize);

    // get image count
    int num = reader.getImageCount(id);
    if (num <= 0) throw new FormatException("Invalid image count: " + num);

    if (num != zSize * cSize * tSize) {
      // if this happens, there is probably a bug in metadata population --
      // either one of the ZCT sizes, or the total number of images --
      // or else the input file is invalid
      throw new FormatException("ZCT size vs image count mismatch (rgb=" +
        rgb + "; sizeZ=" + zSize + ", sizeC=" +
        origSizeC + ", sizeT=" + tSize + ", total=" + num);
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

}
