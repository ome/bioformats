/*
 * #%L
 * Top-level reader and writer APIs
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import loci.common.RandomAccessInputStream;
import loci.formats.in.MetadataLevel;
import loci.formats.in.MetadataOptions;
import loci.formats.meta.MetadataStore;

/**
 * Abstract superclass of reader logic that wraps other readers.
 * All methods are simply delegated to the wrapped reader.
 */
public abstract class ReaderWrapper implements IFormatReader {

  // -- Fields --

  /** FormatReader used to read the file. */
  protected IFormatReader reader;

  // -- Constructors --

  /** Constructs a reader wrapper around a new image reader. */
  public ReaderWrapper() { this(new ImageReader()); }

  /** Constructs a reader wrapper around the given reader. */
  public ReaderWrapper(IFormatReader r) {
    if (r == null) {
      throw new IllegalArgumentException("Format reader cannot be null");
    }
    reader = r;
  }

  // -- ReaderWrapper API methods --

  /** Gets the wrapped reader. */
  public IFormatReader getReader() { return reader; }

  /**
   * Unwraps nested wrapped readers until the core reader (i.e., not
   * a {@link ReaderWrapper} or {@link ImageReader}) is found.
   */
  public IFormatReader unwrap() throws FormatException, IOException {
    return unwrap(null, null);
  }

  /**
   * Unwraps nested wrapped readers until the core reader (i.e., not
   * a {@link ReaderWrapper} or {@link ImageReader}) is found.
   *
   * @param id Id to use as a basis when unwrapping any nested
   *   {@link ImageReader}s. If null, the current id is used.
   */
  public IFormatReader unwrap(String id)
    throws FormatException, IOException
  {
    return unwrap(null, id);
  }

  /**
   * Unwraps nested wrapped readers until the given reader class is found.
   *
   * @param readerClass Class of the desired nested reader. If null, the
   *   core reader (i.e., deepest wrapped reader) will be returned.
   * @param id Id to use as a basis when unwrapping any nested
   *   {@link ImageReader}s. If null, the current id is used.
   */
  public IFormatReader unwrap(Class<? extends IFormatReader> readerClass,
    String id) throws FormatException, IOException
  {
    IFormatReader r = this;
    while (r instanceof ReaderWrapper || r instanceof ImageReader) {
      if (readerClass != null && readerClass.isInstance(r)) break;
      if (r instanceof ImageReader) {
        ImageReader ir = (ImageReader) r;
        r = id == null ? ir.getReader() : ir.getReader(id);
      }
      else r = ((ReaderWrapper) r).getReader();
    }
    if (readerClass != null && !readerClass.isInstance(r)) return null;
    return r;
  }

  /**
   * Performs a deep copy of the reader, including nested wrapped readers.
   * Most of the reader state is preserved as well, including:<ul>
   *   <li>{@link #isNormalized()}</li>
   *   <li>{@link #isMetadataFiltered()}</li>
   *   <li>{@link DelegateReader#isLegacy()}</li>
   * </ul>
   *
   * @param imageReaderClass If non-null, any {@link ImageReader}s in the
   *   reader stack will be replaced with instances of the given class.
   * @throws FormatException If something goes wrong during the duplication.
   */
  public ReaderWrapper duplicate(
    Class<? extends IFormatReader> imageReaderClass) throws FormatException
  {
    ReaderWrapper wrapperCopy = duplicateRecurse(imageReaderClass);

    // sync top-level configuration with original reader
    boolean normalized = isNormalized();
    boolean metadataFiltered = isMetadataFiltered();
    wrapperCopy.setNormalized(normalized);
    wrapperCopy.setMetadataFiltered(metadataFiltered);
    return wrapperCopy;
  }

  // -- IMetadataConfigurable API methods --

  /* (non-Javadoc)
   * @see loci.formats.IMetadataConfigurable#getSupportedMetadataLevels()
   */
  @Override
  public Set<MetadataLevel> getSupportedMetadataLevels() {
    return reader.getSupportedMetadataLevels();
  }

  /* (non-Javadoc)
   * @see loci.formats.IMetadataConfigurable#getMetadataOptions()
   */
  @Override
  public MetadataOptions getMetadataOptions() {
    return reader.getMetadataOptions();
  }

  /* (non-Javadoc)
   * @see loci.formats.IMetadataConfigurable#setMetadataOptions(loci.formats.in.MetadataOptions)
   */
  @Override
  public void setMetadataOptions(MetadataOptions options) {
    reader.setMetadataOptions(options);
  }

  // -- IFormatReader API methods --

  @Override
  public boolean isThisType(String name, boolean open) {
    return reader.isThisType(name, open);
  }

  @Override
  public boolean isThisType(byte[] block) {
    return reader.isThisType(block);
  }

  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException{
    return reader.isThisType(stream);
  }

  @Override
  public int getImageCount() {
    return reader.getImageCount();
  }

  @Override
  public boolean isRGB() {
    return reader.isRGB();
  }

  @Override
  public int getSizeX() {
    return reader.getSizeX();
  }

  @Override
  public int getSizeY() {
    return reader.getSizeY();
  }

  @Override
  public int getSizeZ() {
    return reader.getSizeZ();
  }

  @Override
  public int getSizeC() {
    return reader.getSizeC();
  }

  @Override
  public int getSizeT() {
    return reader.getSizeT();
  }

  @Override
  public int getPixelType() {
    return reader.getPixelType();
  }

  @Override
  public int getBitsPerPixel() {
    return reader.getBitsPerPixel();
  }

  @Override
  public int getEffectiveSizeC() {
    //return reader.getEffectiveSizeC();
    int sizeZT = getSizeZ() * getSizeT();
    if (sizeZT == 0) return 0;
    return getImageCount() / sizeZT;
  }

  @Override
  public int getRGBChannelCount() {
    //return reader.getRGBChannelCount();
    int effSizeC = getEffectiveSizeC();
    if (effSizeC == 0) return 0;
    return getSizeC() / effSizeC;
  }

  @Override
  public boolean isIndexed() {
    return reader.isIndexed();
  }

  @Override
  public boolean isFalseColor() {
    return reader.isFalseColor();
  }

  @Override
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    return reader.get8BitLookupTable();
  }

  @Override
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    return reader.get16BitLookupTable();
  }

  @Override
  public Modulo getModuloZ() {
    return reader.getModuloZ();
  }

  @Override
  public Modulo getModuloC() {
    return reader.getModuloC();
  }

  @Override
  public Modulo getModuloT() {
    return reader.getModuloT();
  }

  @Override
  public int getThumbSizeX() {
    return reader.getThumbSizeX();
  }

  @Override
  public int getThumbSizeY() {
    return reader.getThumbSizeY();
  }

  @Override
  public boolean isLittleEndian() {
    return reader.isLittleEndian();
  }

  @Override
  public String getDimensionOrder() {
    return reader.getDimensionOrder();
  }

  @Override
  public boolean isOrderCertain() {
    return reader.isOrderCertain();
  }

  @Override
  public boolean isThumbnailSeries() {
    return reader.isThumbnailSeries();
  }

  @Override
  public boolean isInterleaved() {
    return reader.isInterleaved();
  }

  @Override
  public boolean isInterleaved(int subC) {
    return reader.isInterleaved(subC);
  }

  @Override
  public byte[] openBytes(int no) throws FormatException, IOException {
    return reader.openBytes(no);
  }

  @Override
  public byte[] openBytes(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    return reader.openBytes(no, x, y, w, h);
  }

  @Override
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    return reader.openBytes(no, buf);
  }

  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    return reader.openBytes(no, buf, x, y, w, h);
  }

  @Override
  public byte[] openBytes(byte[] buf, int [] shape, int [] offsets)
    throws FormatException, IOException
  {
    return reader.openBytes(buf, shape, offsets);
  }

  @Override
  public Object openPlane(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    return reader.openPlane(no, x, y, w, h);
  }

  @Override
  public byte[] openThumbBytes(int no) throws FormatException, IOException {
    return reader.openThumbBytes(no);
  }

  @Override
  public void close(boolean fileOnly) throws IOException {
    reader.close(fileOnly);
  }

  @Override
  public int getSeriesCount() {
    return reader.getSeriesCount();
  }

  @Override
  public void setSeries(int no) {
    reader.setSeries(no);
  }

  @Override
  public int getSeries() {
    return reader.getSeries();
  }

  @Override
  public void setGroupFiles(boolean group) {
    reader.setGroupFiles(group);
  }

  @Override
  public boolean isGroupFiles() {
    return reader.isGroupFiles();
  }

  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    return reader.fileGroupOption(id);
  }

  @Override
  public boolean isMetadataComplete() {
    return reader.isMetadataComplete();
  }

  @Override
  public void setNormalized(boolean normalize) {
    reader.setNormalized(normalize);
  }

  @Override
  public boolean isNormalized() { return reader.isNormalized(); }

  @Override
  public void setOriginalMetadataPopulated(boolean populate) {
    reader.setOriginalMetadataPopulated(populate);
  }

  @Override
  public boolean isOriginalMetadataPopulated() {
    return reader.isOriginalMetadataPopulated();
  }

  @Override
  public String[] getUsedFiles() {
    return reader.getUsedFiles();
  }

  @Override
  public String[] getUsedFiles(boolean noPixels) {
    return reader.getUsedFiles(noPixels);
  }

  @Override
  public String[] getSeriesUsedFiles() {
    return reader.getSeriesUsedFiles();
  }

  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    return reader.getSeriesUsedFiles(noPixels);
  }

  @Override
  public FileInfo[] getAdvancedUsedFiles(boolean noPixels) {
    return reader.getAdvancedUsedFiles(noPixels);
  }

  @Override
  public FileInfo[] getAdvancedSeriesUsedFiles(boolean noPixels) {
    return reader.getAdvancedSeriesUsedFiles(noPixels);
  }

  @Override
  public String getCurrentFile() { return reader.getCurrentFile(); }

  @Override
  public int getIndex(int z, int c, int t) {
    return reader.getIndex(z, c, t);
  }

  @Override
  public int getIndex(int z, int c, int t, int moduloZ, int moduloC, int moduloT) {
    return reader.getIndex(z, c, t, moduloZ, moduloC, moduloT);
  }

  @Override
  public int[] getZCTCoords(int index) {
    return reader.getZCTCoords(index);
  }

  @Override
  public int[] getZCTModuloCoords(int index) {
    return reader.getZCTModuloCoords(index);
  }

  @Override
  public Object getMetadataValue(String field) {
    return reader.getMetadataValue(field);
  }

  @Override
  public Object getSeriesMetadataValue(String field) {
    return reader.getSeriesMetadataValue(field);
  }

  @Override
  public Hashtable<String, Object> getGlobalMetadata() {
    return reader.getGlobalMetadata();
  }

  @Override
  public Hashtable<String, Object> getSeriesMetadata() {
    return reader.getSeriesMetadata();
  }

  @Override
  public List<CoreMetadata> getCoreMetadataList() {
    // Only used for determining the object type.
    List<CoreMetadata> oldcore = reader.getCoreMetadataList();
    List<CoreMetadata> newcore = new ArrayList<CoreMetadata>();

    for (int s=0; s<oldcore.size(); s++) {
      CoreMetadata newMeta = oldcore.get(s).clone(this, s);
      newMeta.resolutionCount = oldcore.get(s).resolutionCount;
      newcore.add(newMeta);
    }

    return newcore;
  }

  @Override
  public void setMetadataFiltered(boolean filter) {
    reader.setMetadataFiltered(filter);
  }

  @Override
  public boolean isMetadataFiltered() { return reader.isMetadataFiltered(); }

  @Override
  public void setMetadataStore(MetadataStore store) {
    reader.setMetadataStore(store);
  }

  @Override
  public MetadataStore getMetadataStore() {
    return reader.getMetadataStore();
  }

  @Override
  public Object getMetadataStoreRoot() {
    return reader.getMetadataStoreRoot();
  }

  @Override
  public IFormatReader[] getUnderlyingReaders() {
    return new IFormatReader[] {reader};
  }

  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return reader.isSingleFile(id);
  }

  @Override
  public int getRequiredDirectories(String[] files)
    throws FormatException, IOException
  {
    return reader.getRequiredDirectories(files);
  }

  @Override
  public String getDatasetStructureDescription() {
    return reader.getDatasetStructureDescription();
  }

  @Override
  public boolean hasCompanionFiles() {
    return reader.hasCompanionFiles();
  }

  @Override
  public String[] getPossibleDomains(String id)
    throws FormatException, IOException
  {
    return reader.getPossibleDomains(id);
  }

  @Override
  public String[] getDomains() {
    return reader.getDomains();
  }

  @Override
  public int getOptimalTileWidth() {
    return reader.getOptimalTileWidth();
  }

  @Override
  public int getOptimalTileHeight() {
    return reader.getOptimalTileHeight();
  }

  @Override
  public int[] getOptimalChunkSize() {
    return reader.getOptimalChunkSize();
  }

  @Override
  public int getCoreIndex() {
    return reader.getCoreIndex();
  }

  @Override
  public void setCoreIndex(int no) {
    reader.setCoreIndex(no);
  }

  @Override
  public int seriesToCoreIndex(int series) {
    return reader.seriesToCoreIndex(series);
  }

  @Override
  public int coreIndexToSeries(int index) {
    return reader.coreIndexToSeries(index);
  }

  @Override
  public int getResolutionCount() {
    return reader.getResolutionCount();
  }

  @Override
  public void setResolution(int no) {
    reader.setResolution(no);
  }

  @Override
  public int getResolution() {
    return reader.getResolution();
  }

  @Override
  public boolean hasFlattenedResolutions() {
    return reader.hasFlattenedResolutions();
  }

  @Override
  public void setFlattenedResolutions(boolean flattened) {
    reader.setFlattenedResolutions(flattened);
  }

  // -- IFormatHandler API methods --

  @Override
  public boolean isThisType(String name) {
    return reader.isThisType(name);
  }

  @Override
  public String getFormat() {
    return reader.getFormat();
  }

  @Override
  public String[] getSuffixes() {
    return reader.getSuffixes();
  }

  @Override
  public Class<?> getNativeDataType() {
    return reader.getNativeDataType();
  }

  @Override
  public void setId(String id) throws FormatException, IOException {
    reader.setId(id);
  }

  @Override
  public void reopenFile() throws IOException {
    reader.reopenFile();
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

  // -- Helper methods --

  private ReaderWrapper duplicateRecurse(
    Class<? extends IFormatReader> imageReaderClass) throws FormatException
  {
    IFormatReader childCopy = null;
    if (reader instanceof ReaderWrapper) {
      // found a nested reader layer; duplicate via recursion
      childCopy = ((ReaderWrapper) reader).duplicateRecurse(imageReaderClass);
    }
    else {
      Class<? extends IFormatReader> c = null;
      if (reader instanceof ImageReader) {
        // found an image reader; if given, substitute the reader class
        c = imageReaderClass == null ? ImageReader.class : imageReaderClass;
      }
      else {
        // bottom of the reader stack; duplicate the core reader
        c = reader.getClass();
      }
      try {
        childCopy = c.newInstance();
      }
      catch (IllegalAccessException exc) { throw new FormatException(exc); }
      catch (InstantiationException exc) { throw new FormatException(exc); }

      // preserve reader-specific configuration with original reader
      if (reader instanceof DelegateReader) {
        DelegateReader delegateOriginal = (DelegateReader) reader;
        DelegateReader delegateCopy = (DelegateReader) childCopy;
        delegateCopy.setLegacy(delegateOriginal.isLegacy());
      }
    }

    // use crazy reflection to instantiate a reader of the proper type
    Class<? extends ReaderWrapper> wrapperClass = getClass();
    ReaderWrapper wrapperCopy = null;
    try {
      wrapperCopy = wrapperClass.getConstructor(new Class[]
        {IFormatReader.class}).newInstance(new Object[] {childCopy});
    }
    catch (InstantiationException exc) { throw new FormatException(exc); }
    catch (IllegalAccessException exc) { throw new FormatException(exc); }
    catch (NoSuchMethodException exc) { throw new FormatException(exc); }
    catch (InvocationTargetException exc) { throw new FormatException(exc); }

    return wrapperCopy;
  }

}
