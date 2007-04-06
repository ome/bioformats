//
// ImageWriter.java
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

import java.awt.Image;
import java.awt.image.ColorModel;
import java.io.IOException;
import java.util.*;

/**
 * ImageWriter is the master file format writer for all supported formats.
 * It uses one instance of each writer subclass (specified in writers.txt,
 * or other class list source) to identify file formats and write data.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class ImageWriter implements IFormatWriter {

  // -- Static fields --

  /** List of writer classes. */
  private static ClassList writerClasses;

  // -- Static initializer --

  static {
    // load built-in writer classes from writers.txt file
    try {
      writerClasses = new ClassList("writers.txt", IFormatWriter.class);
    }
    catch (IOException exc) {
      exc.printStackTrace();
      writerClasses = new ClassList(IFormatWriter.class);
    }
  }

  // -- Fields --

  /** List of supported file format writers. */
  protected IFormatWriter[] writers;

  /**
   * Valid suffixes for all file format writers.
   * Populated the first time getSuffixes() is called.
   */
  private String[] suffixes;

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

  /**
   * Constructs a new ImageWriter with the default
   * list of writer classes from writers.txt.
   */
  public ImageWriter() {
    this(writerClasses);
  }

  /** Constructs a new ImageWriter from the given list of writer classes. */
  public ImageWriter(ClassList classList) {
    // add writers to the list
    Vector v = new Vector();
    Class[] c = classList.getClasses();
    for (int i=0; i<c.length; i++) {
      IFormatWriter writer = null;
      try {
        writer = (IFormatWriter) c[i].newInstance();
      }
      catch (IllegalAccessException exc) { }
      catch (InstantiationException exc) { }
      if (writer == null) {
        System.err.println("Error: " + c[i].getName() +
          " cannot be instantiated.");
        continue;
      }
      v.add(writer);
    }
    writers = new IFormatWriter[v.size()];
    v.copyInto(writers);
  }

  // -- ImageWriter API methods --

  /** Gets a string describing the file format for the given file. */
  public String getFormat(String id) throws FormatException, IOException {
    return getWriter(id).getFormat();
  }

  /** Gets the writer used to save the given file. */
  public IFormatWriter getWriter(String id) throws FormatException {
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
  public IFormatWriter getWriter(Class c) {
    for (int i=0; i<writers.length; i++) {
      if (writers[i].getClass().equals(c)) return writers[i];
    }
    return null;
  }

  /** Gets all constituent file format writers. */
  public IFormatWriter[] getWriters() {
    IFormatWriter[] w = new IFormatWriter[writers.length];
    System.arraycopy(writers, 0, w, 0, writers.length);
    return w;
  }

  // -- IFormatWriter API methods --

  /* @see IFormatWriter#saveImage(String, Image, boolean) */
  public void saveImage(String id, Image image, boolean last)
    throws FormatException, IOException
  {
    getWriter(id).saveImage(id, image, last);
  }

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
        if (s != null) {
          for (int j=0; j<s.length; j++) set.add(s[j]);
        }
      }
      compressionTypes = new String[set.size()];
      set.toArray(compressionTypes);
      Arrays.sort(compressionTypes);
    }
    return compressionTypes;
  }

  /* @see IFormatWriter#getPixelTypes(String) */
  public int[] getPixelTypes(String id) throws FormatException, IOException {
    return getWriter(id).getPixelTypes(id);
  }

  /* @see IFormatWriter#isSupportedType(String, int) */
  public boolean isSupportedType(String id, int type)
    throws FormatException, IOException
  {
    return getWriter(id).isSupportedType(id, type);
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
    return FormatTools.testConvert(this, args);
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

  // -- StatusReporter API methods --

  /* @see IFormatHandler#addStatusListener(StatusListener) */
  public void addStatusListener(StatusListener l) {
    for (int i=0; i<writers.length; i++) writers[i].addStatusListener(l);
  }

  /* @see IFormatHandler#removeStatusListener(StatusListener) */
  public void removeStatusListener(StatusListener l) {
    for (int i=0; i<writers.length; i++) writers[i].removeStatusListener(l);
  }

  /* @see IFormatHandler#getStatusListeners() */
  public StatusListener[] getStatusListeners() {
    // NB: all writers should have the same status listeners
    return writers[0].getStatusListeners();
  }

  // -- Main method --

  public static void main(String[] args) throws FormatException, IOException {
    if (!new ImageWriter().testConvert(args)) System.exit(1);
  }

}
