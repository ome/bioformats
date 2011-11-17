//
// WriterWrapper.java
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
import java.lang.reflect.InvocationTargetException;

import loci.common.Region;
import loci.formats.codec.CodecOptions;
import loci.formats.meta.MetadataRetrieve;

/**
 * Abstract superclass of writer logic that wraps other writers.
 * All methods are simply delegated to the wrapped writer.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/WriterWrapper.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/WriterWrapper.java;hb=HEAD">Gitweb</a></dd></dl>
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
    wrapperCopy.setInterleaved(interleaved);
    wrapperCopy.setColorModel(cm);
    wrapperCopy.setFramesPerSecond(rate);
    wrapperCopy.setCompression(compress);
    return wrapperCopy;
  }

  // -- IFormatWriter API methods --

  public void changeOutputFile(String id) throws FormatException, IOException {
    writer.changeOutputFile(id);
  }

  public void saveBytes(int no, byte[] buf) throws FormatException, IOException
  {
    writer.saveBytes(no, buf);
  }

  public void saveBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    writer.saveBytes(no, buf, x, y, w, h);
  }

  public void saveBytes(int no, byte[] buf, Region tile)
    throws FormatException, IOException
  {
    writer.saveBytes(no, buf, tile);
  }

  public void savePlane(int no, Object plane)
    throws FormatException, IOException
  {
    writer.savePlane(no, plane);
  }

  public void savePlane(int no, Object plane, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    writer.savePlane(no, plane, x, y, w, h);
  }

  public void savePlane(int no, Object plane, Region tile)
    throws FormatException, IOException
  {
    writer.savePlane(no, plane, tile);
  }

  public void setSeries(int series) throws FormatException {
    writer.setSeries(series);
  }

  public int getSeries() {
    return writer.getSeries();
  }

  public void setInterleaved(boolean interleaved) {
    writer.setInterleaved(interleaved);
  }

  public boolean isInterleaved() {
    return writer.isInterleaved();
  }

  public void setValidBitsPerPixel(int bits) {
    writer.setValidBitsPerPixel(bits);
  }

  public boolean canDoStacks() {
    return writer.canDoStacks();
  }

  public void setMetadataRetrieve(MetadataRetrieve r) {
    writer.setMetadataRetrieve(r);
  }

  public MetadataRetrieve getMetadataRetrieve() {
    return writer.getMetadataRetrieve();
  }

  public void setColorModel(ColorModel cm) {
    writer.setColorModel(cm);
  }

  public ColorModel getColorModel() {
    return writer.getColorModel();
  }

  public void setFramesPerSecond(int rate) {
    writer.setFramesPerSecond(rate);
  }

  public int getFramesPerSecond() {
    return writer.getFramesPerSecond();
  }

  public String[] getCompressionTypes() {
    return writer.getCompressionTypes();
  }

  public int[] getPixelTypes() {
    return writer.getPixelTypes();
  }

  public int[] getPixelTypes(String codec) {
    return writer.getPixelTypes(codec);
  }

  public boolean isSupportedType(int type) {
    return writer.isSupportedType(type);
  }

  public void setCompression(String compress) throws FormatException {
    writer.setCompression(compress);
  }

  /* @see IFormatWriter#setCodecOptions(CodecOptions) */
  public void setCodecOptions(CodecOptions options) {
    writer.setCodecOptions(options);
  }
  
  public String getCompression() {
    return writer.getCompression();
  }

  public void setWriteSequentially(boolean sequential) {
    writer.setWriteSequentially(sequential);
  }

  // -- Deprecated IFormatWriter methods --

  /** @deprecated */
  public void saveBytes(byte[] bytes, boolean last)
    throws FormatException, IOException
  {
    writer.saveBytes(bytes, last);
  }

  /** @deprecated */
  public void saveBytes(byte[] bytes, int series,
    boolean lastInSeries, boolean last) throws FormatException, IOException
  {
    writer.saveBytes(bytes, series, lastInSeries, last);
  }

  /** @deprecated */
  public void savePlane(Object plane, boolean last)
    throws FormatException, IOException
  {
    writer.savePlane(plane, last);
  }

  /** @deprecated */
  public void savePlane(Object plane, int series,
    boolean lastInSeries, boolean last) throws FormatException, IOException
  {
    writer.savePlane(plane, series, lastInSeries, last);
  }

  // -- IFormatHandler API methods --

  public boolean isThisType(String name) {
    return writer.isThisType(name);
  }

  public String getFormat() {
    return writer.getFormat();
  }

  public String[] getSuffixes() {
    return writer.getSuffixes();
  }

  public Class<?> getNativeDataType() {
    return writer.getNativeDataType();
  }

  public void setId(String id) throws FormatException, IOException {
    writer.setId(id);
  }

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
