//
// ImageWriter.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

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

import java.awt.image.ColorModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import loci.common.Region;
import loci.formats.codec.CodecOptions;
import loci.formats.meta.MetadataRetrieve;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ImageWriter is the master file format writer for all supported formats.
 * It uses one instance of each writer subclass (specified in writers.txt,
 * or other class list source) to identify file formats and write data.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/ImageWriter.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/ImageWriter.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class ImageWriter implements IFormatWriter {

  // -- Constants --

  private static final Logger LOGGER =
    LoggerFactory.getLogger(ImageWriter.class);

  // -- Static fields --

  /** Default list of writer classes, for use with noargs constructor. */
  private static ClassList<IFormatWriter> defaultClasses;

  // -- Static utility methods --

  public static ClassList<IFormatWriter> getDefaultWriterClasses() {
    if (defaultClasses == null) {
      // load built-in writer classes from writers.txt file
      try {
        defaultClasses =
          new ClassList<IFormatWriter>("writers.txt", IFormatWriter.class);
      }
      catch (IOException exc) {
        defaultClasses = new ClassList<IFormatWriter>(IFormatWriter.class);
        LOGGER.info("Could not parse class list; using default classes", exc);
      }
    }
    return defaultClasses;
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
  protected String currentId;

  /** Current form index. */
  protected int current;

  // -- Constructor --

  /**
   * Constructs a new ImageWriter with the default
   * list of writer classes from writers.txt.
   */
  public ImageWriter() {
    this(getDefaultWriterClasses());
  }

  /** Constructs a new ImageWriter from the given list of writer classes. */
  public ImageWriter(ClassList<IFormatWriter> classList) {
    // add writers to the list
    List<IFormatWriter> list = new ArrayList<IFormatWriter>();
    Class<? extends IFormatWriter>[] c = classList.getClasses();
    for (int i=0; i<c.length; i++) {
      IFormatWriter writer = null;
      try {
        writer = c[i].newInstance();
      }
      catch (IllegalAccessException exc) { }
      catch (InstantiationException exc) { }
      if (writer == null) {
        LOGGER.error("{} cannot be instantiated.", c[i].getName());
        continue;
      }
      list.add(writer);
    }
    writers = new IFormatWriter[list.size()];
    list.toArray(writers);
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
      if (!success) {
        throw new UnknownFormatException("Unknown file format: " + id);
      }
    }
    return writers[current];
  }

  /** Gets the writer used to save the current file. */
  public IFormatWriter getWriter() {
    return writers[current];
  }

  /** Gets the file format writer instance matching the given class. */
  public IFormatWriter getWriter(Class<? extends IFormatWriter> c) {
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

  /* @see IFormatWriter#changeOutputFile(String) */
  public void changeOutputFile(String id) throws FormatException, IOException {
    getWriter().changeOutputFile(id);
  }

  /* @see IFormatWriter#saveBytes(int, byte[]) */
  public void saveBytes(int no, byte[] buf) throws FormatException, IOException
  {
    getWriter().saveBytes(no, buf);
  }

  /* @see IFormatWriter#saveBytes(int, byte[], int, int, int, int) */
  public void saveBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    getWriter().saveBytes(no, buf, x, y, w, h);
  }

  /* @see IFormatWriter#saveBytes(int, byte[], Region) */
  public void saveBytes(int no, byte[] buf, Region tile)
    throws FormatException, IOException
  {
    getWriter().saveBytes(no, buf, tile);
  }

  /* @see IFormatWriter#savePlane(int, Object) */
  public void savePlane(int no, Object plane)
    throws FormatException, IOException
  {
    getWriter().savePlane(no, plane);
  }

  /* @see IFormatWriter#savePlane(int, Object, int, int, int, int) */
  public void savePlane(int no, Object plane, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    getWriter().savePlane(no, plane, x, y, w, h);
  }

  /* @see IFormatWriter#savePlane(int, Object, Region) */
  public void savePlane(int no, Object plane, Region tile)
    throws FormatException, IOException
  {
    getWriter().savePlane(no, plane, tile);
  }

  /* @see IFormatWriter#setSeries(int) */
  public void setSeries(int series) throws FormatException {
    getWriter().setSeries(series);
  }

  /* @see IFormatWriter#getSeries() */
  public int getSeries() {
    return getWriter().getSeries();
  }

  /* @see IFormatWriter#setInterleaved(boolean) */
  public void setInterleaved(boolean interleaved) {
    for (int i=0; i<writers.length; i++) writers[i].setInterleaved(interleaved);
  }

  /* @see IFormatWriter#isInterleaved() */
  public boolean isInterleaved() {
    // NB: all writers should have the same interleaved status
    return writers[0].isInterleaved();
  }

  /* @see IFormatWriter#setValidBitsPerPixel(int) */
  public void setValidBitsPerPixel(int bits) {
    for (IFormatWriter writer : writers) {
      writer.setValidBitsPerPixel(bits);
    }
  }

  /* @see IFormatWriter#canDoStacks() */
  public boolean canDoStacks() {
    return getWriter().canDoStacks();
  }

  /* @see IFormatWriter#setMetadataRetrieve(MetadataRetrieve) */
  public void setMetadataRetrieve(MetadataRetrieve r) {
    for (int i=0; i<writers.length; i++) writers[i].setMetadataRetrieve(r);
  }

  /* @see IFormatReader#getMetadataStore() */
  public MetadataRetrieve getMetadataRetrieve() {
    return getWriter().getMetadataRetrieve();
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
      HashSet<String> set = new HashSet<String>();
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

  /* @see IFormatWriter#getPixelTypes() */
  public int[] getPixelTypes() {
    return getWriter().getPixelTypes();
  }

  /* @see IFormatWriter#getPixelTypes(String) */
  public int[] getPixelTypes(String codec) {
    return getWriter().getPixelTypes(codec);
  }

  /* @see IFormatWriter#isSupportedType(int) */
  public boolean isSupportedType(int type) {
    return getWriter().isSupportedType(type);
  }

  /* @see IFormatWriter#setCompression(String) */
  public void setCompression(String compress) throws FormatException {
    boolean ok = false;
    for (int i=0; i<writers.length; i++) {
      String[] s = writers[i].getCompressionTypes();
      if (s == null) continue;
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

  /* @see IFormatWriter#getCompression() */
  public String getCompression() {
    return getWriter().getCompression();
  }

  /* @see IFormatWriter#setWriteSequentially(boolean) */
  public void setWriteSequentially(boolean sequential) {
    for (IFormatWriter writer : writers) {
      writer.setWriteSequentially(sequential);
    }
  }
  
  /* @see IFormatWriter#setCodecOptions(CodecOptions) */
  public void setCodecOptions(CodecOptions options) {
    getWriter().setCodecOptions(options);
  }

  // -- Deprecated IFormatWriter API methods --

  /**
   * @deprecated
   * @see IFormatWriter#saveBytes(byte[], boolean)
   */
  public void saveBytes(byte[] bytes, boolean last)
    throws FormatException, IOException
  {
    getWriter().saveBytes(bytes, last);
  }

  /**
   * @deprecated
   * @see IFormatWriter#saveBytes(byte[], int, boolean, boolean)
   */
  public void saveBytes(byte[] bytes, int series, boolean lastInSeries,
    boolean last) throws FormatException, IOException
  {
    getWriter().saveBytes(bytes, series, lastInSeries, last);
  }

  /**
   * @deprecated
   * @see IFormatWriter#savePlane(Object, boolean)
   */
  public void savePlane(Object plane, boolean last)
    throws FormatException, IOException
  {
    getWriter().savePlane(plane, last);
  }

  /**
   * @deprecated
   * @see IFormatWriter#savePlane(Object, int, boolean, boolean)
   */
  public void savePlane(Object plane, int series, boolean lastInSeries,
    boolean last) throws FormatException, IOException
  {
    getWriter().savePlane(plane, series, lastInSeries, last);
  }

  // -- IFormatHandler API methods --

  /* @see IFormatHandler#isThisType(String) */
  public boolean isThisType(String name) {
    for (int i=0; i<writers.length; i++) {
      if (writers[i].isThisType(name)) return true;
    }
    return false;
  }

  /* @see IFormatHandler#getFormat() */
  public String getFormat() { return getWriter().getFormat(); }

  /* @see IFormatHandler#getSuffixes() */
  public String[] getSuffixes() {
    if (suffixes == null) {
      HashSet<String> suffixSet = new HashSet<String>();
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

  /* @see IFormatHandler#getNativeDataType() */
  public Class<?> getNativeDataType() {
    return getWriter().getNativeDataType();
  }

  /* @see IFormatHandler#setId(String) */
  public void setId(String id) throws FormatException, IOException {
    getWriter(id).setId(id);
  }

  /* @see IFormatHandler#close() */
  public void close() throws IOException {
    getWriter().close();
  }

}
