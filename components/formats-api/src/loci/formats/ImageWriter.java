/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.formats;

import java.awt.image.ColorModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import loci.common.Region;
import loci.formats.codec.CodecOptions;
import loci.formats.in.MetadataLevel;
import loci.formats.in.MetadataOptions;
import loci.formats.meta.MetadataRetrieve;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ImageWriter is the master file format writer for all supported formats.
 * It uses one instance of each writer subclass (specified in writers.txt,
 * or other class list source) to identify file formats and write data.
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
  
  /**
   * Retrieves the current tile width
   * Defaults to full image width if not supported
   * @return The current tile width being used
   */
  @Override
  public int getTileSizeX() {
    return getWriter().getTileSizeX();
  }

  /**
   * Will attempt to set the tile width to the desired value and return the actual value which will be used
   * @param tileSize - The tile width you wish to use
   * @return The tile width which will actually be used, this may differ from the value attempted to set
   * @throws FormatException - Tile size must be greater than 0 and less than the image width
   */
  @Override
  public int setTileSizeX(int tileSize) throws FormatException {
    return getWriter().setTileSizeX(tileSize);
  }

  /**
   * Retrieves the current tile height
   * Defaults to full image height if not supported
   * @return The current tile height being used
   */
  @Override
  public int getTileSizeY() {
    return getWriter().getTileSizeY();
  }

  /**
   * Will attempt to set the tile height to the desired value and return the actual value which will be used
   * @param tileSize - The tile height you wish to use
   * @return The tile height which will actually be used, this may differ from the value attempted to set
   * @throws FormatException - Tile size must be greater than 0 and less than the image width
   */
  @Override
  public int setTileSizeY(int tileSize) throws FormatException {
    return getWriter().setTileSizeY(tileSize);
  }

  // -- IMetadataConfigurable API methods --

  /* @see loci.formats.IMetadataConfigurable#getSupportedMetadataLevels() */
  @Override
  public Set<MetadataLevel> getSupportedMetadataLevels() {
    return getWriters()[0].getSupportedMetadataLevels();
  }

  /* @see loci.formats.IMetadataConfigurable#getMetadataOptions() */
  @Override
  public MetadataOptions getMetadataOptions() {
    return getWriters()[0].getMetadataOptions();
  }

  /**
   * @see loci.formats.IMetadataConfigurable#setMetadataOptions(MetadataOptions)
   */
  @Override
  public void setMetadataOptions(MetadataOptions options) {
    for (IFormatWriter writer : writers) {
      writer.setMetadataOptions(options);
    }
  }

  // -- IFormatWriter API methods --

  /* @see IFormatWriter#changeOutputFile(String) */
  @Override
  public void changeOutputFile(String id) throws FormatException, IOException {
    getWriter().changeOutputFile(id);
  }

  /* @see IFormatWriter#saveBytes(int, byte[]) */
  @Override
  public void saveBytes(int no, byte[] buf) throws FormatException, IOException
  {
    getWriter().saveBytes(no, buf);
  }

  /* @see IFormatWriter#saveBytes(int, byte[], int, int, int, int) */
  @Override
  public void saveBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    getWriter().saveBytes(no, buf, x, y, w, h);
  }

  /* @see IFormatWriter#saveBytes(int, byte[], Region) */
  @Override
  public void saveBytes(int no, byte[] buf, Region tile)
    throws FormatException, IOException
  {
    getWriter().saveBytes(no, buf, tile);
  }

  /* @see IFormatWriter#savePlane(int, Object) */
  @Override
  public void savePlane(int no, Object plane)
    throws FormatException, IOException
  {
    getWriter().savePlane(no, plane);
  }

  /* @see IFormatWriter#savePlane(int, Object, int, int, int, int) */
  @Override
  public void savePlane(int no, Object plane, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    getWriter().savePlane(no, plane, x, y, w, h);
  }

  /* @see IFormatWriter#savePlane(int, Object, Region) */
  @Override
  public void savePlane(int no, Object plane, Region tile)
    throws FormatException, IOException
  {
    getWriter().savePlane(no, plane, tile);
  }

  /* @see IFormatWriter#setSeries(int) */
  @Override
  public void setSeries(int series) throws FormatException {
    getWriter().setSeries(series);
  }

  /* @see IFormatWriter#getSeries() */
  @Override
  public int getSeries() {
    return getWriter().getSeries();
  }

  /* @see IFormatWriter#setInterleaved(boolean) */
  @Override
  public void setInterleaved(boolean interleaved) {
    for (int i=0; i<writers.length; i++) writers[i].setInterleaved(interleaved);
  }

  /* @see IFormatWriter#isInterleaved() */
  @Override
  public boolean isInterleaved() {
    // NB: all writers should have the same interleaved status
    return writers[0].isInterleaved();
  }

  /* @see IFormatWriter#setValidBitsPerPixel(int) */
  @Override
  public void setValidBitsPerPixel(int bits) {
    for (IFormatWriter writer : writers) {
      writer.setValidBitsPerPixel(bits);
    }
  }

  /* @see IFormatWriter#canDoStacks() */
  @Override
  public boolean canDoStacks() {
    return getWriter().canDoStacks();
  }

  /* @see IFormatWriter#setMetadataRetrieve(MetadataRetrieve) */
  @Override
  public void setMetadataRetrieve(MetadataRetrieve r) {
    for (int i=0; i<writers.length; i++) writers[i].setMetadataRetrieve(r);
  }

  /* @see IFormatReader#getMetadataStore() */
  @Override
  public MetadataRetrieve getMetadataRetrieve() {
    return getWriter().getMetadataRetrieve();
  }

  /* @see IFormatWriter#setColorModel(ColorModel) */
  @Override
  public void setColorModel(ColorModel cm) {
    for (int i=0; i<writers.length; i++) writers[i].setColorModel(cm);
  }

  /* @see IFormatWriter#getColorModel() */
  @Override
  public ColorModel getColorModel() {
    // NB: all writers should have the same color model
    return writers[0].getColorModel();
  }

  /* @see IFormatWriter#setFramesPerSecond(int) */
  @Override
  public void setFramesPerSecond(int rate) {
    for (int i=0; i<writers.length; i++) writers[i].setFramesPerSecond(rate);
  }

  /* @see IFormatWriter#getFramesPerSecond() */
  @Override
  public int getFramesPerSecond() {
    // NB: all writers should have the same frames per second
    return writers[0].getFramesPerSecond();
  }

  /* @see IFormatWriter#getCompressionTypes() */
  @Override
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
  @Override
  public int[] getPixelTypes() {
    return getWriter().getPixelTypes();
  }

  /* @see IFormatWriter#getPixelTypes(String) */
  @Override
  public int[] getPixelTypes(String codec) {
    return getWriter().getPixelTypes(codec);
  }

  /* @see IFormatWriter#isSupportedType(int) */
  @Override
  public boolean isSupportedType(int type) {
    return getWriter().isSupportedType(type);
  }

  /* @see IFormatWriter#setCompression(String) */
  @Override
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
  @Override
  public String getCompression() {
    return getWriter().getCompression();
  }

  /* @see IFormatWriter#setWriteSequentially(boolean) */
  @Override
  public void setWriteSequentially(boolean sequential) {
    for (IFormatWriter writer : writers) {
      writer.setWriteSequentially(sequential);
    }
  }

  /* @see IFormatWriter#setCodecOptions(CodecOptions) */
  @Override
  public void setCodecOptions(CodecOptions options) {
    getWriter().setCodecOptions(options);
  }

  // -- IFormatHandler API methods --

  /* @see IFormatHandler#isThisType(String) */
  @Override
  public boolean isThisType(String name) {
    for (int i=0; i<writers.length; i++) {
      if (writers[i].isThisType(name)) return true;
    }
    return false;
  }

  /* @see IFormatHandler#getFormat() */
  @Override
  public String getFormat() { return getWriter().getFormat(); }

  /* @see IFormatHandler#getSuffixes() */
  @Override
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
  @Override
  public Class<?> getNativeDataType() {
    return getWriter().getNativeDataType();
  }

  /* @see IFormatHandler#setId(String) */
  @Override
  public void setId(String id) throws FormatException, IOException {
    IFormatWriter writer = getWriter(id);
    writer.setId(id);
  }

  /* @see IFormatHandler#close() */
  @Override
  public void close() throws IOException {
    getWriter().close();
  }

}
