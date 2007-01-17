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
import java.util.*;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * ImageWriter is the master file format writer for all supported formats.
 * It uses one instance of each writer subclass (specified in writers.txt)
 * to identify file formats based on extension and write data.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class ImageWriter implements IFormatWriter {

  // -- Static fields --

  /** List of writer classes. */
  protected static Vector writerClasses;

  // -- Static initializer --

  static {
    // read built-in writer classes from writers.txt file
    BufferedReader in = new BufferedReader(new InputStreamReader(
      ImageWriter.class.getResourceAsStream("writers.txt")));
    writerClasses = new Vector();
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
      writerClasses.add(c);
    }
    try { in.close(); }
    catch (IOException exc) { exc.printStackTrace(); }
  }

  // -- Fields --

  /** List of supported file format writers. */
  protected FormatWriter[] writers;

  /**
   * Valid suffixes for all file format writers.
   * Populated the first time getSuffixes() is called.
   */
  private String[] suffixes;

  /**
   * File filters for all file format writers, for use with a JFileChooser.
   * Populated the first time getFileFilters() is called.
   */
  protected FileFilter[] filters;

  /**
   * File chooser for all file format writers.
   * Created the first time getFileChooser() is called.
   */
  protected JFileChooser chooser;

  /**
   * Compression types for all file format writers.
   * Populated the first time getCompressionTypes() is called.
   */
  protected String[] compressionTypes;

  /** Name of current file. */
  private String currentId;

  /** Current form index. */
  protected int current;

  // -- Constructor --

  /** Constructs a new ImageWriter. */
  public ImageWriter() {
    // add built-in writers to the list
    Vector v = new Vector();
    for (int i=0; i<writerClasses.size(); i++) {
      Class writerClass = (Class) writerClasses.elementAt(i);
      FormatWriter writer = null;
      try {
        writer = (FormatWriter) writerClass.newInstance();
      }
      catch (IllegalAccessException exc) { }
      catch (InstantiationException exc) { }
      if (writer == null) {
        System.err.println("Error: " + writerClass.getName() +
          " cannot be instantiated.");
        continue;
      }
      v.add(writer);
    }
    writers = new FormatWriter[v.size()];
    v.copyInto(writers);
  }

  // -- ImageWriter API methods --

  /** Gets a string describing the file format for the given file. */
  public String getFormat(String id) throws FormatException, IOException {
    return getWriter(id).getFormat();
  }

  /** Gets the writer used to save the given file. */
  public FormatWriter getWriter(String id) throws FormatException {
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

  /* @see IFormatWriter#save(String, Image, boolean) */
  public void save(String id, Image image, boolean last)
    throws FormatException, IOException
  {
    getWriter(id).save(id, image, last);
  }

  /* @see IFormatWriter#close() */
  public void close() throws FormatException, IOException {
    getWriter(currentId).close();
  }

  /* @see IFormatWriter#canDoStacks(String) */
  public boolean canDoStacks(String id) throws FormatException {
    return getWriter(id).canDoStacks(id);
  }

  /* @see IFormatWriter#setColorModel(ColorModel) */
  public void setColorModel(ColorModel cm) {
    for (int i=0; i<writers.length; i++) writers[i].setColorModel(cm);
  }

  /* @see IFormatWriter#getColorModel() */
  public ColorModel getColorModel() {
    // NB: all writers should have the same color model
    return writers[0].getColorModel();
  }

  /* @see IFormatWriter#setFramesPerSecond(int) */
  public void setFramesPerSecond(int rate) {
    for (int i=0; i<writers.length; i++) writers[i].setFramesPerSecond(rate);
  }

  /* @see IFormatWriter#getFramesPerSecond() */
  public int getFramesPerSecond() {
    // NB: all writers should have the same frames per second
    return writers[0].getFramesPerSecond();
  }

  /* @see IFormatWriter#getCompressionTypes() */
  public String[] getCompressionTypes() {
    if (compressionTypes == null) {
      HashSet set = new HashSet();
      for (int i=0; i<writers.length; i++) {
        String[] s = writers[i].getCompressionTypes();
        for (int j=0; j<s.length; j++) set.add(s[j]);
      }
      compressionTypes = new String[set.size()];
      set.toArray(compressionTypes);
      Arrays.sort(compressionTypes);
    }
    return compressionTypes;
  }

  /* @see IFormatWriter#setCompression(String) */
  public void setCompression(String compress) throws FormatException {
    boolean ok = false;
    for (int i=0; i<writers.length; i++) {
      String[] s = writers[i].getCompressionTypes();
      for (int j=0; j<s.length; j++) {
        if (s[j].equals(compress)) {
          // valid compression type for this format
          writers[i].setCompression(compress);
          ok = true;
        }
      }
    }
    if (!ok) throw new FormatException("Invalid compression type: " + compress);
  }

  /* @see IFormatWriter#testConvert(String[]) */
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

  /* @see IFormatHandler#isThisType(String) */
  public boolean isThisType(String name) {
    // NB: Unlike individual format writers, ImageWriter defaults to *not*
    // allowing files to be opened to analyze type, because doing so is
    // quite slow with the large number of supported formats.
    return isThisType(name, false);
  }

  /* @see IFormatHandler#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    for (int i=0; i<writers.length; i++) {
      if (writers[i].isThisType(name, open)) return true;
    }
    return false;
  }

  /* @see IFormatHandler#getFormat() */
  public String getFormat() { return "image"; }

  /* @see IFormatHandler#getSuffixes() */
  public String[] getSuffixes() {
    if (suffixes == null) {
      HashSet set = new HashSet();
      for (int i=0; i<writers.length; i++) {
        String[] s = writers[i].getSuffixes();
        for (int j=0; j<s.length; j++) set.add(s[j]);
      }
      suffixes = new String[set.size()];
      set.toArray(suffixes);
      Arrays.sort(suffixes);
    }
    return suffixes;
  }

  /* @see IFormatHandler#getFileFilters() */
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

  /* @see IFormatHandler#getFileChooser() */
  public JFileChooser getFileChooser() {
    if (chooser == null) {
      chooser = FormatHandler.buildFileChooser(getFileFilters());
    }
    return chooser;
  }

  // -- Static ImageWriter API methods --

  /**
   * Adds the given class, which must implement IFormatWriter,
   * to the writer list.
   *
   * @throws FormatException if the class does not implement
   *   the IFormatWriter interface.
   */
  public static void addWriterType(Class c) throws FormatException {
    if (!IFormatWriter.class.isAssignableFrom(c)) {
      throw new FormatException(
        "Writer class must implement IFormatWriter interface");
    }
    writerClasses.add(c);
  }

  /** Removes the given class from the writer list. */
  public static void removeWriterType(Class c) {
    writerClasses.remove(c);
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    if (!new ImageWriter().testConvert(args)) System.exit(1);
  }

}
