/*
 * #%L
 * Top-level reader and writer APIs
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
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import loci.common.Region;
import loci.formats.codec.CodecOptions;
import loci.formats.in.MetadataLevel;
import loci.formats.in.MetadataOptions;
import loci.formats.meta.MetadataRetrieve;

/**
 * Abstract superclass of writer logic that wraps other writers.
 * All methods are simply delegated to the wrapped writer.
 */
public abstract class WriterWrapper implements IFormatWriter {

  // -- Fields --

  /** FormatWriter used to write the file. */
  protected IFormatWriter writer;

  // -- Constructors --

  /** Constructs a writer wrapper around a new image writer. */
  public WriterWrapper() { this(new ImageWriter()); }

  /** Constructs a writer wrapper around the given writer. */
  public WriterWrapper(IFormatWriter w) {
    if (w == null) {
      throw new IllegalArgumentException("Format writer cannot be null");
    }
    writer = w;
  }

  // -- WriterWrapper API methods --

  /** Gets the wrapped writer. */
  public IFormatWriter getWriter() { return writer; }

  /**
   * Unwraps nested wrapped writers until the core writer (i.e., not
   * a {@link WriterWrapper} or {@link ImageWriter}) is found.
   */
  public IFormatWriter unwrap() throws FormatException, IOException {
    return unwrap(null, null);
  }

  /**
   * Unwraps nested wrapped writers until the core writer (i.e., not
   * a {@link WriterWrapper} or {@link ImageWriter}) is found.
   *
   * @param id Id to use as a basis when unwrapping any nested
   *   {@link ImageWriter}s. If null, the current id is used.
   */
  public IFormatWriter unwrap(String id)
    throws FormatException, IOException
  {
    return unwrap(null, id);
  }

  /**
   * Unwraps nested wrapped writers until the given writer class is found.
   *
   * @param writerClass Class of the desired nested writer. If null, the
   *   core writer (i.e., deepest wrapped writer) will be returned.
   * @param id Id to use as a basis when unwrapping any nested
   *   {@link ImageWriter}s. If null, the current id is used.
   */
  public IFormatWriter unwrap(Class<? extends IFormatWriter> writerClass,
    String id) throws FormatException, IOException
  {
    IFormatWriter w = this;
    while (w instanceof WriterWrapper || w instanceof ImageWriter) {
      if (writerClass != null && writerClass.isInstance(w)) break;
      if (w instanceof ImageWriter) {
        ImageWriter iw = (ImageWriter) w;
        w = id == null ? iw.getWriter() : iw.getWriter(id);
      }
      else w = ((WriterWrapper) w).getWriter();
    }
    if (writerClass != null && !writerClass.isInstance(w)) return null;
    return w;
  }

  /**
   * Performs a deep copy of the writer, including nested wrapped writers.
   * Most of the writer state is preserved as well, including:<ul>
   *   <li>{@link #isInterleaved()}</li>
   *   <li>{@link #getColorModel()}</li>
   *   <li>{@link #getFramesPerSecond()}</li>
   *   <li>{@link #getCompression()}</li>
   * </ul>
   *
   * @param imageWriterClass If non-null, any {@link ImageWriter}s in the
   *   writer stack will be replaced with instances of the given class.
   * @throws FormatException If something goes wrong during the duplication.
   */
  public WriterWrapper duplicate(
    Class<? extends IFormatWriter> imageWriterClass) throws FormatException
  {
    WriterWrapper wrapperCopy = duplicateRecurse(imageWriterClass);

    // sync top-level configuration with original writer
    boolean interleaved = isInterleaved();
    ColorModel cm = getColorModel();
    int rate = getFramesPerSecond();
    String compress = getCompression();
    int tileSizeX = getTileSizeX();
    int tileSizeY = getTileSizeY();
    wrapperCopy.setInterleaved(interleaved);
    wrapperCopy.setColorModel(cm);
    wrapperCopy.setFramesPerSecond(rate);
    wrapperCopy.setCompression(compress);
    wrapperCopy.setTileSizeX(tileSizeX);
    wrapperCopy.setTileSizeY(tileSizeY);
    return wrapperCopy;
  }

  // -- IMetadataConfigurable API methods --

  /* (non-Javadoc)
   * @see loci.formats.IMetadataConfigurable#getSupportedMetadataLevels()
   */
  @Override
  public Set<MetadataLevel> getSupportedMetadataLevels() {
    return writer.getSupportedMetadataLevels();
  }

  /* (non-Javadoc)
   * @see loci.formats.IMetadataConfigurable#getMetadataOptions()
   */
  @Override
  public MetadataOptions getMetadataOptions() {
    return writer.getMetadataOptions();
  }

  /* (non-Javadoc)
   * @see loci.formats.IMetadataConfigurable#setMetadataOptions(loci.formats.in.MetadataOptions)
   */
  @Override
  public void setMetadataOptions(MetadataOptions options) {
    writer.setMetadataOptions(options);
  }

  // -- IFormatWriter API methods --

  @Override
  public void changeOutputFile(String id) throws FormatException, IOException {
    writer.changeOutputFile(id);
  }

  @Override
  public void saveBytes(int no, byte[] buf) throws FormatException, IOException
  {
    writer.saveBytes(no, buf);
  }

  @Override
  public void saveBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    writer.saveBytes(no, buf, x, y, w, h);
  }

  @Override
  public void saveBytes(int no, byte[] buf, Region tile)
    throws FormatException, IOException
  {
    writer.saveBytes(no, buf, tile);
  }

  @Override
  public void savePlane(int no, Object plane)
    throws FormatException, IOException
  {
    writer.savePlane(no, plane);
  }

  @Override
  public void savePlane(int no, Object plane, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    writer.savePlane(no, plane, x, y, w, h);
  }

  @Override
  public void savePlane(int no, Object plane, Region tile)
    throws FormatException, IOException
  {
    writer.savePlane(no, plane, tile);
  }

  @Override
  public void setSeries(int series) throws FormatException {
    writer.setSeries(series);
  }

  @Override
  public int getSeries() {
    return writer.getSeries();
  }

  @Override
  public void setInterleaved(boolean interleaved) {
    writer.setInterleaved(interleaved);
  }

  @Override
  public boolean isInterleaved() {
    return writer.isInterleaved();
  }

  @Override
  public void setValidBitsPerPixel(int bits) {
    writer.setValidBitsPerPixel(bits);
  }

  @Override
  public boolean canDoStacks() {
    return writer.canDoStacks();
  }

  @Override
  public void setMetadataRetrieve(MetadataRetrieve r) {
    writer.setMetadataRetrieve(r);
  }

  @Override
  public MetadataRetrieve getMetadataRetrieve() {
    return writer.getMetadataRetrieve();
  }

  @Override
  public void setColorModel(ColorModel cm) {
    writer.setColorModel(cm);
  }

  @Override
  public ColorModel getColorModel() {
    return writer.getColorModel();
  }

  @Override
  public void setFramesPerSecond(int rate) {
    writer.setFramesPerSecond(rate);
  }

  @Override
  public int getFramesPerSecond() {
    return writer.getFramesPerSecond();
  }

  @Override
  public String[] getCompressionTypes() {
    return writer.getCompressionTypes();
  }

  @Override
  public int[] getPixelTypes() {
    return writer.getPixelTypes();
  }

  @Override
  public int[] getPixelTypes(String codec) {
    return writer.getPixelTypes(codec);
  }

  @Override
  public boolean isSupportedType(int type) {
    return writer.isSupportedType(type);
  }

  @Override
  public void setCompression(String compress) throws FormatException {
    writer.setCompression(compress);
  }

  /* @see IFormatWriter#setCodecOptions(CodecOptions) */
  @Override
  public void setCodecOptions(CodecOptions options) {
    writer.setCodecOptions(options);
  }

  @Override
  public String getCompression() {
    return writer.getCompression();
  }

  @Override
  public void setWriteSequentially(boolean sequential) {
    writer.setWriteSequentially(sequential);
  }
  
  /* @see IFormatWriter#getTileSizeX() */
  @Override
  public int getTileSizeX() throws FormatException {
    return writer.getTileSizeX();
  }

  /* @see IFormatWriter#setTileSizeX(int) */
  @Override
  public int setTileSizeX(int tileSize) throws FormatException {
    return writer.setTileSizeX(tileSize);
  }

  /* @see IFormatWriter#getTileSizeY() */
  @Override
  public int getTileSizeY() throws FormatException {
    return writer.getTileSizeY();
  }

  /* @see IFormatWriter#setTileSizeY(int) */
  @Override
  public int setTileSizeY(int tileSize) throws FormatException {
    return writer.setTileSizeY(tileSize);
  }

  // -- IFormatHandler API methods --

  @Override
  public boolean isThisType(String name) {
    return writer.isThisType(name);
  }

  @Override
  public String getFormat() {
    return writer.getFormat();
  }

  @Override
  public String[] getSuffixes() {
    return writer.getSuffixes();
  }

  @Override
  public Class<?> getNativeDataType() {
    return writer.getNativeDataType();
  }

  @Override
  public void setId(String id) throws FormatException, IOException {
    writer.setId(id);
  }

  @Override
  public void close() throws IOException {
    writer.close();
  }

  // -- Helper methods --

  private WriterWrapper duplicateRecurse(
    Class<? extends IFormatWriter> imageWriterClass) throws FormatException
  {
    IFormatWriter childCopy = null;
    if (writer instanceof WriterWrapper) {
      // found a nested writer layer; duplicate via recursion
      childCopy = ((WriterWrapper) writer).duplicateRecurse(imageWriterClass);
    }
    else {
      Class<? extends IFormatWriter> c = null;
      if (writer instanceof ImageWriter) {
        // found an image writer; if given, substitute the writer class
        c = imageWriterClass == null ? ImageWriter.class : imageWriterClass;
      }
      else {
        // bottom of the writer stack; duplicate the core writer
        c = writer.getClass();
      }
      try {
        childCopy = (IFormatWriter) c.newInstance();
      }
      catch (IllegalAccessException exc) { throw new FormatException(exc); }
      catch (InstantiationException exc) { throw new FormatException(exc); }
    }

    // use crazy reflection to instantiate a writer of the proper type
    Class<? extends WriterWrapper> wrapperClass = getClass();
    WriterWrapper wrapperCopy = null;
    try {
      wrapperCopy = wrapperClass.getConstructor(new Class[]
        {IFormatWriter.class}).newInstance(new Object[] {childCopy});
    }
    catch (InstantiationException exc) { throw new FormatException(exc); }
    catch (IllegalAccessException exc) { throw new FormatException(exc); }
    catch (NoSuchMethodException exc) { throw new FormatException(exc); }
    catch (InvocationTargetException exc) { throw new FormatException(exc); }

    return wrapperCopy;
  }

}
