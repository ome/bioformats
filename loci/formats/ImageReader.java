//
// ImageReader.java
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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * ImageReader is the master file format reader for all supported formats.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class ImageReader implements IFormatReader {

  // -- Static fields --

  /** List of reader classes. */
  private static Class[] readerClasses;

  // -- Static initializer --

  static {
    // read built-in reader classes from readers.txt file
    BufferedReader in = new BufferedReader(new InputStreamReader(
      ImageReader.class.getResourceAsStream("readers.txt")));
    Vector v = new Vector();
    while (true) {
      String line = null;
      try { line = in.readLine(); }
      catch (IOException exc) { exc.printStackTrace(); }
      if (line == null) break;

      // ignore characters following # sign (comments)
      int ndx = line.indexOf("#");
      if (ndx >= 0) line = line.substring(0, ndx);
      line = line.trim();
      if (line.equals("")) continue;

      // load reader class
      Class c = null;
      try { c = Class.forName(line); }
      catch (ClassNotFoundException exc) { }
      if (c == null || !FormatReader.class.isAssignableFrom(c)) {
        System.err.println("Error: \"" + line +
          "\" is not a valid format reader.");
        continue;
      }
      v.add(c);
    }
    try { in.close(); }
    catch (IOException exc) { exc.printStackTrace(); }
    readerClasses = new Class[v.size()];
    v.copyInto(readerClasses);
  }

  // -- Fields --

  /** List of supported file format readers. */
  private IFormatReader[] readers;

  /**
   * Valid suffixes for this file format.
   * Populated the first time getSuffixes() is called.
   */
  private String[] suffixes;

  /**
   * File filters for this file format, for use with a JFileChooser.
   * Populated the first time getFileFilters() is called.
   */
  protected FileFilter[] filters;

  /**
   * File chooser for this file format.
   * Created the first time getFileChooser() is called.
   */
  protected JFileChooser chooser;

  /** Name of current file. */
  private String currentId;

  /** Current form index. */
  private int current;

  // -- Constructors --

  /** Constructs a new ImageReader. */
  public ImageReader() { this(null); }

  /**
   * Constructs a new ImageReader with a MetadataStore.
   * @param store the default metadata store.
   */
  public ImageReader(MetadataStore store) {
    // add built-in readers to the list
    Vector v = new Vector();
    for (int i=0; i<readerClasses.length; i++) {
      IFormatReader reader = null;
      try { reader = (IFormatReader) readerClasses[i].newInstance(); }
      catch (IllegalAccessException exc) { }
      catch (InstantiationException exc) { }
      if (reader == null) {
        System.err.println("Error: " + readerClasses[i].getName() +
          " cannot be instantiated.");
        continue;
      }
      v.add(reader);
    }
    readers = new IFormatReader[v.size()];
    v.copyInto(readers);

    if (store != null) setMetadataStore(store);
  }

  // -- ImageReader API methods --

  /** Gets a string describing the file format for the given file. */
  public String getFormat(String id) throws FormatException, IOException {
    return getReader(id).getFormat();
  }

  /** Gets the reader used to open the given file. */
  public IFormatReader getReader(String id)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) {
      // initialize file
      boolean success = false;
      for (int i=0; i<readers.length; i++) {
        if (readers[i].isThisType(id)) {
          current = i;
          currentId = id;
          success = true;
          break;
        }
      }
      if (!success) throw new FormatException("Unknown file format: " + id);
    }
    return readers[current];
  }

  /** Gets the file format reader instance matching the given class. */
  public IFormatReader getReader(Class c) {
    for (int i=0; i<readers.length; i++) {
      if (readers[i].getClass().equals(c)) return readers[i];
    }
    return null;
  }

  // -- IFormatReader API methods --

  /** Checks if the given block is a valid header for an image file. */
  public boolean isThisType(byte[] block) {
    for (int i=0; i<readers.length; i++) {
      if (readers[i].isThisType(block)) return true;
    }
    return false;
  }

  /** Determines the number of images in the given image file. */
  public int getImageCount(String id) throws FormatException, IOException {
    return getReader(id).getImageCount(id);
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    return getReader(id).isRGB(id);
  }

  /** Get the size of the X dimension. */
  public int getSizeX(String id) throws FormatException, IOException {
    return getReader(id).getSizeX(id);
  }

  /** Get the size of the Y dimension. */
  public int getSizeY(String id) throws FormatException, IOException {
    return getReader(id).getSizeY(id);
  }

  /** Get the size of the Z dimension. */
  public int getSizeZ(String id) throws FormatException, IOException {
    return getReader(id).getSizeZ(id);
  }

  /** Get the size of the C dimension. */
  public int getSizeC(String id) throws FormatException, IOException {
    return getReader(id).getSizeC(id);
  }

  /** Get the size of the T dimension. */
  public int getSizeT(String id) throws FormatException, IOException {
    return getReader(id).getSizeT(id);
  }

  /* @see IFormatReader#getPixelType() */
  public int getPixelType(String id) throws FormatException, IOException {
    return getReader(id).getPixelType(id);
  }

  /* @see IFormatReader#getChannelGlobalMinimum(int) */
  public Double getChannelGlobalMinimum(String id, int theC)
    throws FormatException, IOException
  {
    return getReader(id).getChannelGlobalMinimum(id, theC);
  }

  /* @see IFormatReader#getChannelGlobalMaximum(int) */
  public Double getChannelGlobalMaximum(String id, int theC)
    throws FormatException, IOException
  {
    return getReader(id).getChannelGlobalMaximum(id, theC);
  }

  /** Get the size of the X dimension for the thumbnail. */
  public int getThumbSizeX(String id) throws FormatException, IOException {
    return getReader(id).getThumbSizeX(id);
  }

  /** Get the size of the Y dimension for the thumbnail. */
  public int getThumbSizeY(String id) throws FormatException, IOException {
    return getReader(id).getThumbSizeY(id);
  }

  /** Return true if the data is in little-endian format. */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    return getReader(id).isLittleEndian(id);
  }

  /**
   * Return a five-character string representing the dimension order
   * within the file.
   */
  public String getDimensionOrder(String id)
    throws FormatException, IOException
  {
    return getReader(id).getDimensionOrder(id);
  }

  /* @see IFormatReader#setChannelStatCalculationStatus(boolean) */
  public void setChannelStatCalculationStatus(boolean on) {
    for (int i=0; i<readers.length; i++) {
      readers[i].setChannelStatCalculationStatus(on);
    }
  }

  /* @see IFormatReader#getChannelStatCalculationStatus() */
  public boolean getChannelStatCalculationStatus() {
    // NB: all readers should have the same status
    return readers[0].getChannelStatCalculationStatus();
  }

  /** Returns whether or not the channels are interleaved. */
  public boolean isInterleaved(String id) throws FormatException, IOException {
    return getReader(id).isInterleaved(id);
  }

  /** Obtains the specified image from the given image file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    return getReader(id).openImage(id, no);
  }

  /** Obtains the specified image from the given image file as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    return getReader(id).openBytes(id, no);
  }

  /* @see IFormatReader#openBytes(java.lang.String, int, byte[]) */
  public byte[] openBytes(String id, int no, byte[] buf)
    throws FormatException, IOException
  {
    return getReader(id).openBytes(id, no, buf);
  }

  /** Obtains a thumbnail for the specified image from the given file. */
  public BufferedImage openThumbImage(String id, int no)
    throws FormatException, IOException
  {
    return getReader(id).openThumbImage(id, no);
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
    return getReader(id).openThumbBytes(id, no);
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    for (int i=0; i<readers.length; i++) readers[i].close();
  }

  /** Gets the number of series in the file. */
  public int getSeriesCount(String id) throws FormatException, IOException {
    return getReader(id).getSeriesCount(id);
  }

  /** Activates the specified series. */
  public void setSeries(String id, int no) throws FormatException, IOException {
    getReader(id).setSeries(id, no);
  }

  /** Gets the currently active series. */
  public int getSeries(String id) throws FormatException, IOException {
    return getReader(id).getSeries(id);
  }

  /* @see IFormatReader#setIgnoreColorTable(boolean) */
  public void setIgnoreColorTable(boolean ignore) {
    for (int i=0; i<readers.length; i++) readers[i].setIgnoreColorTable(ignore);
  }

  /**
   * Swaps the dimensions according to the given dimension order.  If the given
   * order is identical to the file's native order, then nothing happens.
   */
  public void swapDimensions(String id, String order)
    throws FormatException, IOException
  {
    getReader(id).swapDimensions(id, order);
  }

  /*
   * @see IFormatReader#getIndex(java.lang.String, int, int, int)
   */
  public int getIndex(String id, int z, int c, int t)
    throws FormatException, IOException
  {
    return getReader(id).getIndex(id, z, c, t);
  }

  /* @see IFormatReader#getZCTCoords(java.lang.String, int) */
  public int[] getZCTCoords(String id, int index)
    throws FormatException, IOException
  {
    return getReader(id).getZCTCoords(id, index);
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
    return getReader(id).getMetadataValue(id, field);
  }

  /**
   * Obtains the hashtable containing the metadata field/value pairs from
   * the given file.
   *
   * @param id the filename
   * @return the hashtable containing all metadata from the file
   */
  public Hashtable getMetadata(String id) throws FormatException, IOException {
    return getReader(id).getMetadata(id);
  }

  /*
   * @see FormatReader#setMetadataStore(loci.formats.MetadataStore)
   */
  public void setMetadataStore(MetadataStore store) {
    for (int i = 0; i < readers.length; i++)
      readers[i].setMetadataStore(store);
  }

  /* @see IFormatReader#getMetadataStore(java.lang.String) */
  public MetadataStore getMetadataStore(String id)
    throws FormatException, IOException
  {
    return getReader(id).getMetadataStore(id);
  }

  /* @see IFormatReader#getMetadataStoreRoot(java.lang.String) */
  public Object getMetadataStoreRoot(String id)
    throws FormatException, IOException
  {
    return getReader(id).getMetadataStoreRoot(id);
  }

  /**
   * A utility method for test reading a file from the command line,
   * and displaying the results in a simple display.
   */
  public boolean testRead(String[] args) throws FormatException, IOException {
    if (args.length == 0) {
      JFileChooser box = getFileChooser();
      int rval = box.showOpenDialog(null);
      if (rval == JFileChooser.APPROVE_OPTION) {
        File file = box.getSelectedFile();
        if (file != null) args = new String[] {file.getPath()};
      }
    }
    if (args.length > 0) {
      // check file format
      for (int i=0; i<args.length; i++) {
        String arg = args[i];
        if (arg.startsWith("-")) continue;
        try {
          Integer.parseInt(arg);
          continue;
        }
        catch (Exception e) { }
        System.out.print("Checking file format ");
        System.out.println("[" + getFormat(arg) + "]");
        File f = new File(arg);
        arg = f.getAbsolutePath();
        args[i] = arg;
        break;
      }
    }
    return FormatReader.testRead(this, args);
  }

  // -- IFormatHandler API methods --

  /**
   * Checks if the given string is a valid filename for any supported format.
   */
  public boolean isThisType(String name) {
    // NB: Unlike individual format readers, ImageReader defaults to *not*
    // allowing files to be opened to analyze type, because doing so is
    // quite slow with the large number of supported formats.
    return isThisType(name, false);
  }

  /**
   * Checks if the given string is a valid filename for any supported format.
   * @param open If true, and the file extension is insufficient to determine
   *   the file type, the (existing) file is opened for further analysis.
   */
  public boolean isThisType(String name, boolean open) {
    for (int i=0; i<readers.length; i++) {
      if (readers[i].isThisType(name, open)) return true;
    }
    return false;
  }

  /* @see IFormatHandler#getFormat() */
  public String getFormat() { return "image"; }

  /* @see IFormatHandler#getSuffixes() */
  public String[] getSuffixes() {
    if (suffixes == null) {
      HashSet suffixSet = new HashSet();
      for (int i=0; i<readers.length; i++) {
        String[] suf = readers[i].getSuffixes();
        for (int j=0; j<suf.length; j++) suffixSet.add(suf[j]);
      }
      suffixes = new String[suffixSet.size()];
      suffixSet.toArray(suffixes);
      Arrays.sort(suffixes);
    }
    return suffixes;
  }

  /* @see IFormatHandler#getFileFilters() */
  public FileFilter[] getFileFilters() {
    if (filters == null) {
      Vector v = new Vector();
      for (int i=0; i<readers.length; i++) {
        javax.swing.filechooser.FileFilter[] ff = readers[i].getFileFilters();
        for (int j=0; j<ff.length; j++) v.add(ff[j]);
      }
      filters = ComboFileFilter.sortFilters(v);
    }
    return filters;
  }

  /* @see IFormatHandler#getFileChooser() */
  public JFileChooser getFileChooser() {
    if (chooser == null) {
      chooser = FormatHandler.buildFileChooser(getFileFilters());
    }
    return chooser;
  }

  // -- Utility methods --

  /**
   * Retrieves how many bytes per pixel the current plane or section has.
   * @param type the pixel type as retrieved from
   *   {@link IFormatReader#getPixelType(String)}.
   * @return the number of bytes per pixel.
   * @see IFormatReader#getPixelType(String)
   */
  public static int getBytesPerPixel(int type) {
    switch(type) {
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

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    if (!new ImageReader().testRead(args)) System.exit(1);
  }

}
