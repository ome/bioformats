//
// ImageWriter.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats;

import java.awt.Image;
import java.io.*;
import java.util.*;

/**
 * ImageWriter is master file format writer for all supported formats.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class ImageWriter extends FormatWriter {

  // -- Static fields --

  /** List of supported file format writers. */
  protected static FormatWriter[] writers;

  /** List of supported filename suffixes. */
  protected static String[] suffixList;

  /** Index of writer with last successful identification. */
  protected static int lastGood = -1;


  // -- Static initializer --

  static {
    // add built-in writers to the list
    BufferedReader in = new BufferedReader(new InputStreamReader(
      ImageWriter.class.getResourceAsStream("writers.txt")));
    Vector v = new Vector();
    HashSet suffixSet = new HashSet();
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

      // load writer class and instantiate
      Class c = null;
      try { c = Class.forName(line); }
      catch (ClassNotFoundException exc) { }
      if (c == null || !FormatWriter.class.isAssignableFrom(c)) {
        System.err.println("Error: \"" + line +
          "\" is not a valid format writer.");
        continue;
      }
      FormatWriter writer = null;
      try { writer = (FormatWriter) c.newInstance(); }
      catch (IllegalAccessException exc) { }
      catch (InstantiationException exc) { }
      if (writer == null) {
        System.err.println("Error: \"" + line + "\" cannot be instantiated.");
        continue;
      }
      v.add(writer);
      String[] suf = writer.getSuffixes();
      for (int i=0; i<suf.length; i++) suffixSet.add(suf[i]);
    }
    try { in.close(); }
    catch (IOException exc) { exc.printStackTrace(); }
    writers = new FormatWriter[v.size()];
    v.copyInto(writers);
    suffixList = new String[suffixSet.size()];
    suffixSet.toArray(suffixList);
    Arrays.sort(suffixList);
  }


  // -- Fields --

  /** Current form index. */
  protected int index;


  // -- Constructor --

  /** Constructs a new ImageWriter. */
  public ImageWriter() { super("any image", suffixList); }


  // -- ImageWriter API methods --

  /** Gets a string describing the file format for the given file. */
  public String getFormat(String id) throws FormatException, IOException {
    if (!id.equals(currentId)) initFile(id);
    return writers[index].getFormat();
  }

  /** Gets the writer used to save the given file. */
  public FormatWriter getWriter(String id)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    return writers[index];
  }

  /** Gets the file format writer instance matching the given class. */
  public FormatWriter getWriter(Class c) {
    for (int i=0; i<writers.length; i++) {
      if (writers[i].getClass().equals(c)) return writers[i];
    }
    return null;
  }

  /** A utility method for converting a file from the command line. */
  public void testConvert(String[] args) throws FormatException, IOException {
    if (args.length > 1) {
      // check file format
      System.out.print("Checking file format ");
      System.out.println("[" + getFormat(args[1]) + "]");
    }
    super.testConvert(args);
  }


  // -- FormatWriter API methods --

  /**
   * Saves the given image to the specified (possibly already open) file.
   * If this image is the last one in the file, the last flag must be set.
   */
  public void save(String id, Image image, boolean last)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) initFile(id);
    writers[index].save(id, image, last);
  }


  // -- FormatHandler API methods --

  /** Creates JFileChooser file filters for this file format. */
  protected void createFilters() {
    Vector v = new Vector();
    for (int i=0; i<writers.length; i++) {
      javax.swing.filechooser.FileFilter[] ff = writers[i].getFileFilters();
      for (int j=0; j<ff.length; j++) v.add(ff[j]);
    }
    filters = ComboFileFilter.sortFilters(v);
  }


  // -- Internal ImageWriter API methods --

  /** Initializes the given image file. */
  protected void initFile(String id) throws FormatException, IOException {
    // try last known good type first, for efficiency
    for (int i=-1; i<writers.length; i++) {
      if (i == lastGood) continue;
      int ii = i < 0 ? lastGood : i;
      if (writers[ii].isThisType(id)) {
        index = ii;
        lastGood = ii;
        currentId = id;
        return;
      }
    }
    throw new FormatException("Unknown file format: " + id);
  }


  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    new ImageWriter().testConvert(args);
  }

}
