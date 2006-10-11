//
// ImageWriter.java
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

import java.awt.Image;
import java.awt.image.ColorModel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * ImageWriter is master file format writer for all supported formats.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class ImageWriter implements IFormatWriter {

  // -- Static fields --

  /** List of writer classes. */
  protected static Class[] writerClasses;

  // -- Static initializer --

  static {
    // read built-in writer classes from writers.txt file
    BufferedReader in = new BufferedReader(new InputStreamReader(
      ImageWriter.class.getResourceAsStream("writers.txt")));
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

      // load writer class
      Class c = null;
      try { c = Class.forName(line); }
      catch (ClassNotFoundException exc) { }
      if (c == null || !FormatWriter.class.isAssignableFrom(c)) {
        System.err.println("Error: \"" + line +
          "\" is not a valid format writer.");
        continue;
      }
      v.add(c);
    }
    try { in.close(); }
    catch (IOException exc) { exc.printStackTrace(); }
    writerClasses = new Class[v.size()];
    v.copyInto(writerClasses);
  }

  // -- Fields --

  /** List of supported file format writers. */
  protected FormatWriter[] writers;

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
  protected int current;

  // -- Constructor --

  /** Constructs a new ImageWriter. */
  public ImageWriter() {
    // add built-in writers to the list
    Vector v = new Vector();
    HashSet suffixSet = new HashSet();
    for (int i=0; i<writerClasses.length; i++) {
      FormatWriter writer = null;
      try { writer = (FormatWriter) writerClasses[i].newInstance(); }
      catch (IllegalAccessException exc) { }
      catch (InstantiationException exc) { }
      if (writer == null) {
        System.err.println("Error: " + writerClasses[i].getName() +
          " cannot be instantiated.");
        continue;
      }
      v.add(writer);
      String[] suf = writer.getSuffixes();
      for (int j=0; j<suf.length; j++) suffixSet.add(suf[j]);
    }
    writers = new FormatWriter[v.size()];
    v.copyInto(writers);
    suffixes = new String[suffixSet.size()];
    suffixSet.toArray(suffixes);
    Arrays.sort(suffixes);
  }

  // -- ImageWriter API methods --

  /** Gets a string describing the file format for the given file. */
  public String getFormat(String id) throws FormatException, IOException {
    return getWriter(id).getFormat();
  }

  /** Gets the writer used to save the given file. */
  public FormatWriter getWriter(String id)
    throws FormatException, IOException
  {
    if (!id.equals(currentId)) {
      // initialize file
      boolean success = false;
      for (int i=0; i<writers.length; i++) {
        if (writers[i].isThisType(id)) {
          current = i;
          currentId = id;
          success = true;
          break;
        }
      }
      if (!success) throw new FormatException("Unknown file format: " + id);
    }
    return writers[current];
  }

  /** Gets the file format writer instance matching the given class. */
  public FormatWriter getWriter(Class c) {
    for (int i=0; i<writers.length; i++) {
      if (writers[i].getClass().equals(c)) return writers[i];
    }
    return null;
  }

  // -- IFormatWriter API methods --

  /**
   * Saves the given image to the specified (possibly already open) file.
   * If this image is the last one in the file, the last flag must be set.
   */
  public void save(String id, Image image, boolean last)
    throws FormatException, IOException
  {
    getWriter(id).save(id, image, last);
  }

  /** Reports whether the writer can save multiple images to a single file. */
  public boolean canDoStacks(String id) throws FormatException, IOException {
    return getWriter(id).canDoStacks(id);
  }

  /** Sets the color model. */
  public void setColorModel(ColorModel cm) {
    for (int i=0; i<writers.length; i++) writers[i].setColorModel(cm);
  }

  /** Gets the color model. */
  public ColorModel getColorModel() {
    // NB: all writers should have the same color model
    return writers[0].getColorModel();
  }

  /** Sets the frames per second to use when writing. */
  public void setFramesPerSecond(int fps) {
    for (int i=0; i<writers.length; i++) writers[i].setFramesPerSecond(fps);
  }

  /** Gets the frames per second to use when writing. */
  public int getFramesPerSecond() {
    // NB: all writers should have the same frames per second
    return writers[0].getFramesPerSecond();
  }

  /** Gets the available compression types. */
  public String[] getCompressionTypes() {
    // CTR TODO - fix this API
    return null;
  }

  /** Sets the current compression type. */
  public void setCompression(String compress) throws FormatException {
    // CTR TODO - fix this API
  }

  /** A utility method for converting a file from the command line. */
  public boolean testConvert(String[] args)
    throws FormatException, IOException
  {
    if (args.length > 1) {
      // check file format
      System.out.print("Checking file format ");
      System.out.println("[" + getFormat(args[1]) + "]");
    }
    return FormatWriter.testConvert(this, args);
  }

  // -- IFormatHandler API methods --

  /** Checks if the given string is a valid filename for this file format. */
  public boolean isThisType(String name) {
    // NB: Unlike individual format writers, ImageWriter defaults to *not*
    // allowing files to be opened to analyze type, because doing so is
    // quite slow with the large number of supported formats.
    return isThisType(name, false);
  }

  /**
   * Checks if the given string is a valid filename for this file format.
   * @param open If true, and the file extension is insufficient to determine
   *  the file type, the (existing) file is opened for further analysis.
   */
  public boolean isThisType(String name, boolean open) {
    for (int i=0; i<writers.length; i++) {
      if (writers[i].isThisType(name, open)) return true;
    }
    return false;
  }

  /** Gets the name of this file format. */
  public String getFormat() { return "image"; }

  /** Gets the default file suffixes for this file format. */
  public String[] getSuffixes() {
    if (suffixes == null) {
      HashSet suffixSet = new HashSet();
      for (int i=0; i<writers.length; i++) {
        String[] suf = writers[i].getSuffixes();
        for (int j=0; j<suf.length; j++) suffixSet.add(suf[j]);
      }
      suffixes = new String[suffixSet.size()];
      suffixSet.toArray(suffixes);
      Arrays.sort(suffixes);
    }
    return suffixes;
  }

  /** Gets file filters for this file format, for use with a JFileChooser. */
  public FileFilter[] getFileFilters() {
    if (filters == null) {
      Vector v = new Vector();
      for (int i=0; i<writers.length; i++) {
        FileFilter[] ff = writers[i].getFileFilters();
        for (int j=0; j<ff.length; j++) v.add(ff[j]);
      }
      filters = ComboFileFilter.sortFilters(v);
    }
    return filters;
  }

  /** Gets a JFileChooser that recognizes accepted file types. */
  public JFileChooser getFileChooser() {
    if (chooser == null) {
      chooser = FormatHandler.buildFileChooser(getFileFilters());
    }
    return chooser;
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    if (!new ImageWriter().testConvert(args)) System.exit(1);
  }

}
