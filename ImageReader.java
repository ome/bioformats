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

  /** Name of current file. */
  private String currentId;

  /** Valid suffixes for this file format. */
  private String[] suffixes;

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

  /** Current form index. */
  private int index;

  // -- Constructor --

  /** Constructs a new ImageReader. */
  public ImageReader() {
    initializeReaders();
  }

  /**
   * Constructs a new ImageReader with a MetadataStore.
   * @param store the default metadata store.
   */
  public ImageReader(MetadataStore store) {
    initializeReaders();
    setMetadataStore(store);
  }

  /**
   * Initializes all the built-in file format readers (classes in
   * <code>readerClasses</code>).
   */
  private void initializeReaders()
  {
    // add built-in readers to the list
    Vector v = new Vector();
    HashSet suffixSet = new HashSet();
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
      //reader.setSeparated(true);  // Add this line to separate RGB planes
      v.add(reader);
      String[] suf = reader.getSuffixes();
      for (int j=0; j<suf.length; j++) suffixSet.add(suf[j]);
    }
    readers = new IFormatReader[v.size()];
    v.copyInto(readers);
    suffixes = new String[suffixSet.size()];
    suffixSet.toArray(suffixes);
    Arrays.sort(suffixes);
  }

  /* (non-Javadoc)
   * @see loci.formats.FormatReader#setMetadataStore(loci.formats.MetadataStore)
   */
  public void setMetadataStore(MetadataStore store) {
    for (int i = 0; i < readers.length; i++)
      readers[i].setMetadataStore(store);
  }

  // -- ImageReader API methods --

  /** Gets a string describing the file format for the given file. */
  public String getFormat(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return readers[index].getFormat();
  }

  /** Gets the reader used to open the given file. */
  public IFormatReader getReader(String id)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return readers[index];
  }

  /** Gets the file format reader instance matching the given class. */
  public IFormatReader getReader(Class c) {
    for (int i=0; i<readers.length; i++) {
      if (readers[i].getClass().equals(c)) return readers[i];
    }
    return null;
  }

  // -- FormatReader API methods --

  /** Checks if the given block is a valid header for an image file. */
  public boolean isThisType(byte[] block) {
    for (int i=0; i<readers.length; i++) {
      if (readers[i].isThisType(block)) return true;
    }
    return false;
  }

  /** Determines the number of images in the given image file. */
  public int getImageCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return readers[index].getImageCount(id);
  }

  /** Checks if the images in the file are RGB. */
  public boolean isRGB(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return readers[index].isRGB(id);
  }

  /** Get the size of the X dimension. */
  public int getSizeX(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return readers[index].getSizeX(id);
  }

  /** Get the size of the Y dimension. */
  public int getSizeY(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return readers[index].getSizeY(id);
  }

  /** Get the size of the Z dimension. */
  public int getSizeZ(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return readers[index].getSizeZ(id);
  }

  /** Get the size of the C dimension. */
  public int getSizeC(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return readers[index].getSizeC(id);
  }

  /** Get the size of the T dimension. */
  public int getSizeT(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return readers[index].getSizeT(id);
  }

  /** Return true if the data is in little-endian format. */
  public boolean isLittleEndian(String id) throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return readers[index].isLittleEndian(id);
  }

  /**
   * Return a five-character string representing the dimension order
   * within the file.
   */
  public String getDimensionOrder(String id) throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return readers[index].getDimensionOrder(id);
  }
 
  /** Return the number of series in the file. */
  public int getSeriesCount(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return readers[index].getSeriesCount(id);
  }

  /** Activates the specified series. */
  public void setSeries(String id, int no) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    readers[index].setSeries(id, no);
  }

  /**
   * Allows the client to specify whether or not to separate channels.
   * By default, channels are left unseparated; thus if we encounter an RGB
   * image plane, it will be left as RGB and not split into 3 separate planes.
   */
  public void setSeparated(boolean separate)
  {
    for (int i = 0; i < readers.length; i++)
      readers[i].setSeparated(separate);
  }

  /** Obtains the specified image from the given image file as a byte array. */
  public byte[] openBytes(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return readers[index].openBytes(id, no);
  }

  /** Obtains the specified image from the given image file. */
  public BufferedImage openImage(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return readers[index].openImage(id, no);
  }

  /** Closes any open files. */
  public void close() throws FormatException, IOException {
    for (int i=0; i<readers.length; i++) readers[i].close();
  }

  /**
   * Opens an existing file from the given filename.
   *
   * @return Java Images containing pixel data
   */
  public BufferedImage[] open(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return readers[index].openImage(id);
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
    //if (!id.equals(currentId)) initFile(id);
    return readers[index].getMetadataValue(id, field);
  }

  /**
   * Obtains the hashtable containing the metadata field/value pairs from
   * the given file.
   *
   * @param id the filename
   * @return the hashtable containing all metadata from the file
   */
  public Hashtable getMetadata(String id) throws FormatException, IOException {
    //if (!id.equals(currentId)) initFile(id);
    return readers[index].getMetadata(id);
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
        catch (Exception e ) { }
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

  /* (non-Javadoc)
   * @see loci.formats.IFormatReader#getIndex(java.lang.String, int, int, int)
   */
  public int getIndex(String id, int z, int c, int t)
    throws FormatException, IOException
  {
    return currentReader(id).getIndex(id, z, c, t);
  }

  /* (non-Javadoc)
   * @see loci.formats.IFormatReader#getMetadataStore(java.lang.String)
   */
  public MetadataStore getMetadataStore(String id)
    throws FormatException, IOException
  {
    return currentReader(id).getMetadataStore(id);
  }

  /* (non-Javadoc)
   * @see loci.formats.IFormatReader#getMetadataStoreRoot(java.lang.String)
   */
  public Object getMetadataStoreRoot(String id)
    throws FormatException, IOException
  {
    return currentReader(id).getMetadataStoreRoot(id);
  }

  /* (non-Javadoc)
   * @see loci.formats.IFormatReader#getZCTCoords(java.lang.String, int)
   */
  public int[] getZCTCoords(String id, int index)
    throws FormatException, IOException
  {
    return currentReader(id).getZCTCoords(id, index);
  }

  /* (non-Javadoc)
   * @see loci.formats.IFormatReader#isSeparated()
   */
  public boolean isSeparated() {
    return currentReader().isSeparated();
  }

  /* (non-Javadoc)
   * @see loci.formats.IFormatReader#openImage(java.lang.String)
   */
  public BufferedImage[] openImage(String id)
    throws FormatException, IOException
  {
    return currentReader(id).openImage(id);
  }

  // -- FormatHandler API methods --

  /* (non-Javadoc)
   * @see loci.formats.IFormatHandler#getFileChooser()
   */
  public JFileChooser getFileChooser() {
    return FormatHandler.buildFileChooser(getFileFilters());
  }

  /* (non-Javadoc)
   * @see loci.formats.IFormatHandler#getFileFilters()
   */
  public FileFilter[] getFileFilters() {
    Vector v = new Vector();
    for (int i=0; i<readers.length; i++) {
      javax.swing.filechooser.FileFilter[] ff = readers[i].getFileFilters();
      for (int j=0; j<ff.length; j++) v.add(ff[j]);
    }
    return ComboFileFilter.sortFilters(v);
  }

  /* (non-Javadoc)
   * @see loci.formats.IFormatHandler#getFormat()
   */
  public String getFormat() {
    return readers[index].getFormat();
  }

  /* (non-Javadoc)
   * @see loci.formats.IFormatHandler#getSuffixes()
   */
  public String[] getSuffixes() {
    return suffixes;
  }

  /**
   * Checks if the given string is a valid filename for any supported format.
   */
  public boolean isThisType(String name) {
    for (int i=0; i<readers.length; i++) {
      if (readers[i].isThisType(name)) return true;
    }
    return false;
  }

  // -- Internal ImageReader API methods --

  /** Initializes the given image file. */
  private void initFile(String id) throws FormatException, IOException {
    for (int i=0; i<readers.length; i++) {
      if (readers[i].isThisType(id)) {
        index = i;
        currentId = id;
        return;
      }
    }
    throw new FormatException("Unknown file format: " + id);
  }

  /**
   * Retrieves the reader that the ImageReader proxy is currently using or the
   * valid reader for a given file.
   *
   * @param id path to a file.
   * @return the current active reader unless <i>id</i> is not the current
   * <i>id</i>, in which case the active reader will be switched and that
   * reader returned.
   * @throws FormatException
   * @throws IOException
   */
  private IFormatReader currentReader(String id)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return currentReader();
  }

  /**
   * Retrieves the reader that the ImageReader proxy is currently using.
   *
   * @return the current active reader.
   */
  private IFormatReader currentReader()
  {
    return readers[index];
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    if (!new ImageReader().testRead(args)) System.exit(1);
  }
}
