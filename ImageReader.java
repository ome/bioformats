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
import java.util.*;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * ImageReader is the master file format reader for all supported formats.
 * It uses one instance of each reader subclass (specified in readers.txt)
 * to identify file formats and read data.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class ImageReader implements IFormatReader {

  // -- Static fields --

  /** List of reader classes. */
  private static Vector readerClasses;

  // -- Static initializer --

  static {
    // read built-in reader classes from readers.txt file
    BufferedReader in = new BufferedReader(new InputStreamReader(
      ImageReader.class.getResourceAsStream("readers.txt")));
    readerClasses = new Vector();
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
      readerClasses.add(c);
    }
    try { in.close(); }
    catch (IOException exc) { exc.printStackTrace(); }
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
    Hashtable map = null;
    for (int i=0; i<readerClasses.size(); i++) {
      Class readerClass = (Class) readerClasses.elementAt(i);
      IFormatReader reader = null;
      try {
        reader = (IFormatReader) readerClass.newInstance();
        // NB: ensure all readers share the same ID map
        if (i == 0) map = reader.getIdMap();
        else reader.setIdMap(map);
      }
      catch (IllegalAccessException exc) { }
      catch (InstantiationException exc) { }
      if (reader == null) {
        System.err.println("Error: " + readerClass.getName() +
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

  /* @see IFormatReader.isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    for (int i=0; i<readers.length; i++) {
      if (readers[i].isThisType(block)) return true;
    }
    return false;
  }

  /* @see IFormatReader.getImageCount(String) */
  public int getImageCount(String id) throws FormatException, IOException {
    return getReader(id).getImageCount(id);
  }

  /* @see IFormatReader.isRGB(String) */
  public boolean isRGB(String id) throws FormatException, IOException {
    return getReader(id).isRGB(id);
  }

  /* @see IFormatReader.getSizeX(String) */
  public int getSizeX(String id) throws FormatException, IOException {
    return getReader(id).getSizeX(id);
  }

  /* @see IFormatReader.getSizeY(String) */
  public int getSizeY(String id) throws FormatException, IOException {
    return getReader(id).getSizeY(id);
  }

  /* @see IFormatReader.getSizeZ(String) */
  public int getSizeZ(String id) throws FormatException, IOException {
    return getReader(id).getSizeZ(id);
  }

  /* @see IFormatReader.getSizeC(String) */
  public int getSizeC(String id) throws FormatException, IOException {
    return getReader(id).getSizeC(id);
  }

  /* @see IFormatReader.getSizeT(String) */
  public int getSizeT(String id) throws FormatException, IOException {
    return getReader(id).getSizeT(id);
  }

  /* @see IFormatReader#getPixelType(String) */
  public int getPixelType(String id) throws FormatException, IOException {
    return getReader(id).getPixelType(id);
  }

  /* @see IFormatReader#getEffectiveSizeC(String) */
  public int getEffectiveSizeC(String id) throws FormatException, IOException {
    return FormatReader.getEffectiveSizeC(isRGB(id), getSizeC(id));
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

  /* @see IFormatReader#getThumbSizeX(String) */
  public int getThumbSizeX(String id) throws FormatException, IOException {
    return getReader(id).getThumbSizeX(id);
  }

  /* @see IFormatReader#getThumbSizeY(String) */
  public int getThumbSizeY(String id) throws FormatException, IOException {
    return getReader(id).getThumbSizeY(id);
  }

  /* @see IFormatReader#isLittleEndian(String) */
  public boolean isLittleEndian(String id) throws FormatException, IOException {
    return getReader(id).isLittleEndian(id);
  }

  /* @see IFormatReader#getDimensionOrder(String) */
  public String getDimensionOrder(String id)
    throws FormatException, IOException
  {
    return getReader(id).getDimensionOrder(id);
  }

  /* @see IFormatReader#isOrderCertain(String) */
  public boolean isOrderCertain(String id) throws FormatException, IOException {
    return getReader(id).isOrderCertain(id);
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

  /* @see IFormatReader#isInterleaved(String) */
  public boolean isInterleaved(String id) throws FormatException, IOException {
    return getReader(id).isInterleaved(id);
  }

  /* @see IFormatReader#openImage(String, int) */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    return getReader(id).openImage(id, no);
  }

  /* @see IFormatReader#openBytes(String, int) */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    return getReader(id).openBytes(id, no);
  }

  /* @see IFormatReader#openBytes(String, int, byte[]) */
  public byte[] openBytes(String id, int no, byte[] buf)
    throws FormatException, IOException
  {
    return getReader(id).openBytes(id, no, buf);
  }

  /* @see IFormatReader#openThumbImage(String, int) */
  public BufferedImage openThumbImage(String id, int no)
    throws FormatException, IOException
  {
    return getReader(id).openThumbImage(id, no);
  }

  /* @see IFormatReader#openThumbBytes(String, int) */
  public byte[] openThumbBytes(String id, int no)
    throws FormatException, IOException
  {
    return getReader(id).openThumbBytes(id, no);
  }

  /* @see IFormatReader#close() */
  public void close() throws FormatException, IOException {
    for (int i=0; i<readers.length; i++) readers[i].close();
  }

  /* @see IFormatReader#getSeriesCount(String) */
  public int getSeriesCount(String id) throws FormatException, IOException {
    return getReader(id).getSeriesCount(id);
  }

  /* @see IFormatReader#setSeries(String, int) */
  public void setSeries(String id, int no) throws FormatException, IOException {
    getReader(id).setSeries(id, no);
  }

  /* @see IFormatReader#getSeries(String) */
  public int getSeries(String id) throws FormatException, IOException {
    return getReader(id).getSeries(id);
  }

  /* @see IFormatReader#setColorTableIgnored(boolean) */
  public void setColorTableIgnored(boolean ignore) {
    for (int i=0; i<readers.length; i++) {
      readers[i].setColorTableIgnored(ignore);
    }
  }

  /* @see IFormatReader#isColorTableIgnored() */
  public boolean isColorTableIgnored() {
    return readers[0].isColorTableIgnored();
  }

  /* @see IFormatReader#setNormalized(boolean) */
  public void setNormalized(boolean normalize) {
    for (int i=0; i<readers.length; i++) readers[i].setNormalized(normalize);
  }

  /* @see IFormatReader#isNormalized() */
  public boolean isNormalized() {
    return readers[0].isNormalized();
  }

  /* @see IFormatReader#getUsedFiles(String) */
  public String[] getUsedFiles(String id) throws FormatException, IOException {
    return getReader(id).getUsedFiles(id);
  }

  /* @see IFormatReader#getCurrentFile() */
  public String getCurrentFile() {
    try {
      return getReader(currentId).getCurrentFile();
    }
    catch (FormatException e) { e.printStackTrace(); }
    catch (IOException e) { e.printStackTrace(); }
    return null;
  }

  /* @see IFormatReader#swapDimensions(String, String) */
  public void swapDimensions(String id, String order)
    throws FormatException, IOException
  {
    getReader(id).swapDimensions(id, order);
  }

  /* @see IFormatReader#getIndex(String, int, int, int) */
  public int getIndex(String id, int z, int c, int t)
    throws FormatException, IOException
  {
    return getReader(id).getIndex(id, z, c, t);
  }

  /* @see IFormatReader#getZCTCoords(String, int) */
  public int[] getZCTCoords(String id, int index)
    throws FormatException, IOException
  {
    return getReader(id).getZCTCoords(id, index);
  }

  /* @see IFormatReader#getMetadataValue(String, String) */
  public Object getMetadataValue(String id, String field)
    throws FormatException, IOException
  {
    return getReader(id).getMetadataValue(id, field);
  }

  /* @see IFormatReader#getMetadata(String) */
  public Hashtable getMetadata(String id) throws FormatException, IOException {
    return getReader(id).getMetadata(id);
  }

  /* @see FormatReader#setMetadataStore(MetadataStore) */
  public void setMetadataStore(MetadataStore store) {
    for (int i=0; i<readers.length; i++) {
      readers[i].setMetadataStore(store);
    }
  }

  /* @see IFormatReader#getMetadataStore(String) */
  public MetadataStore getMetadataStore(String id)
    throws FormatException, IOException
  {
    return getReader(id).getMetadataStore(id);
  }

  /* @see IFormatReader#getMetadataStoreRoot(String) */
  public Object getMetadataStoreRoot(String id)
    throws FormatException, IOException
  {
    return getReader(id).getMetadataStoreRoot(id);
  }

  /* @see IFormatReader#testRead(String[]) */
  public boolean testRead(String[] args) throws FormatException, IOException {
    if (args.length == 0) {
      JFileChooser box = getFileChooser();
      int rval = box.showOpenDialog(null);
      if (rval == JFileChooser.APPROVE_OPTION) {
        File file = box.getSelectedFile();
        if (file != null) args = new String[] {file.getPath()};
      }
    }
    return FormatReader.testRead(this, args);
  }

  // -- IFormatHandler API methods --

  /* @see IFormatHandler#isThisType(String) */
  public boolean isThisType(String name) {
    // NB: Unlike individual format readers, ImageReader defaults to *not*
    // allowing files to be opened to analyze type, because doing so is
    // quite slow with the large number of supported formats.
    return isThisType(name, false);
  }

  /* @see IFormatHandler#isThisType(String, boolean) */
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

  /* @see IFormatHandler#mapId(String, String) */
  public void mapId(String id, String filename) {
    // NB: all readers share the same ID map
    readers[0].mapId(id, filename);
  }

  /* @see IFormatHandler#getMappedId(String) */
  public String getMappedId(String id) {
    return readers[0].getMappedId(id);
  }

  /* @see IFormatHandler.getIdMap() */
  public Hashtable getIdMap() {
    return readers[0].getIdMap();
  }

  /* @see IFormatHandler.setIdMap(Hashtable) */
  public void setIdMap(Hashtable map) {
    for (int i=0; i<readers.length; i++) readers[i].setIdMap(map);
  }

  // -- Static ImageReader API methods --

  /**
   * Adds the given class, which must implement IFormatReader,
   * to the reader list.
   *
   * @throws FormatException if the class does not implement
   *   the IFormatReader interface.
   */
  public static void addReaderType(Class c) throws FormatException {
    if (!IFormatReader.class.isAssignableFrom(c)) {
      throw new FormatException(
        "Reader class must implement IFormatReader interface");
    }
    readerClasses.add(c);
  }

  /** Removes the given class from the reader list. */
  public static void removeReaderType(Class c) {
    readerClasses.remove(c);
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    if (!new ImageReader().testRead(args)) System.exit(1);
  }

}
