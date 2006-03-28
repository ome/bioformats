//
// ImageReader.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

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
import java.util.*;
import javax.swing.JFileChooser;

/**
 * ImageReader is master file format reader for all supported formats.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class ImageReader extends FormatReader {

  // -- Static fields --

  /** List of reader classes. */
  protected static Class[] readerClasses;


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
  protected FormatReader[] readers;

  /** Current form index. */
  protected int index;


  // -- Constructor --

  /** Constructs a new ImageReader. */
  public ImageReader() {
    super("any image", (String[]) null);

    // add built-in readers to the list
    Vector v = new Vector();
    HashSet suffixSet = new HashSet();
    for (int i=0; i<readerClasses.length; i++) {
      FormatReader reader = null;
      try { reader = (FormatReader) readerClasses[i].newInstance(); }
      catch (IllegalAccessException exc) { }
      catch (InstantiationException exc) { }
      if (reader == null) {
        System.err.println("Error: " + readerClasses[i].getName() +
          " cannot be instantiated.");
        continue;
      }
      v.add(reader);
      String[] suf = reader.getSuffixes();
      for (int j=0; j<suf.length; j++) suffixSet.add(suf[j]);
    }
    readers = new FormatReader[v.size()];
    v.copyInto(readers);
    suffixes = new String[suffixSet.size()];
    suffixSet.toArray(suffixes);
    Arrays.sort(suffixes);
  }


  // -- ImageReader API methods --

  /** Gets a string describing the file format for the given file. */
  public String getFormat(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return readers[index].getFormat();
  }

  /** Gets the reader used to open the given file. */
  public FormatReader getReader(String id)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return readers[index];
  }

  /** Gets the file format reader instance matching the given class. */
  public FormatReader getReader(Class c) {
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

  /** Obtains the specified image from the given image file. */
  public BufferedImage open(String id, int no)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return readers[index].open(id, no);
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
    return readers[index].open(id);
  }

  /**
   * Obtains a loci.ome.xml.OMENode object representing the
   * file's metadata as an OME-XML DOM structure.
   *
   * @return null if the loci.ome.xml package is not present.
   */
  public Object getOMENode(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return readers[index].getOMENode(id);
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
    if (!id.equals(currentId)) initFile(id);
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
      System.out.print("Checking file format ");
      System.out.println("[" + getFormat(args[0]) + "]");
    }
    return super.testRead(args);
  }


  // -- FormatHandler API methods --

  /** Creates JFileChooser file filters for this file format. */
  protected void createFilters() {
    Vector v = new Vector();
    for (int i=0; i<readers.length; i++) {
      javax.swing.filechooser.FileFilter[] ff = readers[i].getFileFilters();
      for (int j=0; j<ff.length; j++) v.add(ff[j]);
    }
    filters = ComboFileFilter.sortFilters(v);
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
  protected void initFile(String id) throws FormatException, IOException {
    for (int i=0; i<readers.length; i++) {
      if (readers[i].isThisType(id)) {
        index = i;
        currentId = id;
        return;
      }
    }
    throw new FormatException("Unknown file format: " + id);
  }


  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    if (!new ImageReader().testRead(args)) System.exit(1);
  }

}
