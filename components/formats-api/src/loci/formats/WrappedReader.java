/*
 * #%L
 * Top-level reader and writer APIs
 * %%
 * Copyright (C) 2005 - 2018 Open Microscopy Environment:
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
import java.util.Hashtable;
import java.util.List;

/**
 * Helper class for readers which wrap other readers.
 *
 * Some methods can only be called before initFile/setId, others can only be called after.
 * This class assumes the helper object is only available after initFile() has been called.
 *
 * Instead the inherited FormatReader and IMetadataConfigurable methods are kept since the properties
 * they set are inherited. These properties can be set on the helper class in initFile() by calling
 * callDeferredSetters().
 *
 * The following setters and corresponding getters are not passed to the helper.
 * If you initialise the helper in the constructor instead of at runtime during initFile()
 * you may wish to override them.
 *
 * - getSupportedMetadataLevels
 * - setMetadataOptions, getMetadataOptions
 * - setGroupFiles, isGroupFiles
 * - setNormalized, isNormalized
 * - setOriginalMetadataPopulated, isOriginalMetadataPopulated
 * - setMetadataFiltered, isMetadataFiltered
 * - setMetadataStore, getMetadataStore
 * - setFlattenedResolutions, hasFlattenedResolutions
 *
 * Developer note: the list of methods are those in IMetadataConfigurable plus FormatReader methods
 * which contain `FormatTools.assertId(currentId, false, 1)`
 */
public abstract class WrappedReader extends FormatReader {

  // -- Constructor --

  /**
   * Constructs a new wrapped reader with the given name and suffixes.
   */
  protected WrappedReader(String format, String[] suffixes) {
    super(format, suffixes);
  }

  /** Get the helper class that reads images */
  protected abstract ReaderWrapper getHelper();

  // -- IMetadataConfigurable methods --

  // Not overridden: getSupportedMetadataLevels()

  // Not overridden: setMetadataOptions(MetadataOptions options)

  // Not overridden: getMetadataOptions()

  // -- IFormatReader methods --

  @Override
  public void reopenFile() throws IOException {
    getHelper().reopenFile();
  }

  @Override
  public int getImageCount() {
    return getHelper().getImageCount();
  }

  @Override
  public boolean isRGB() {
    return getHelper().isRGB();
  }

  @Override
  public int getSizeX() {
    return getHelper().getSizeX();
  }

  @Override
  public int getSizeY() {
    return getHelper().getSizeY();
  }

  @Override
  public int getSizeZ() {
    return getHelper().getSizeZ();
  }

  @Override
  public int getSizeC() {
    return getHelper().getSizeC();
  }

  @Override
  public int getSizeT() {
    return getHelper().getSizeT();
  }

  @Override
  public int getPixelType() {
    return getHelper().getPixelType();
  }

  @Override
  public int getBitsPerPixel() {
    return getHelper().getBitsPerPixel();
  }

  @Override
  public int getEffectiveSizeC() {
    return getHelper().getEffectiveSizeC();
  }

  @Override
  public int getRGBChannelCount() {
    return getHelper().getRGBChannelCount();
  }

  @Override
  public boolean isIndexed() {
    return getHelper().isIndexed();
  }

  @Override
  public boolean isFalseColor() {
    return getHelper().isFalseColor();
  }

  @Override
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    return getHelper().get8BitLookupTable();
  }

  @Override
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    return getHelper().get16BitLookupTable();
  }

  @Override
  public Modulo getModuloZ() {
    return getHelper().getModuloZ();
  }

  @Override
  public Modulo getModuloC() {
    return getHelper().getModuloC();
  }

  @Override
  public Modulo getModuloT() {
    return getHelper().getModuloT();
  }

  @Override
  public int getThumbSizeX() {
    return getHelper().getThumbSizeX();
  }

  @Override
  public int getThumbSizeY() {
    return getHelper().getThumbSizeY();
  }

  @Override
  public boolean isLittleEndian() {
    return getHelper().isLittleEndian();
  }

  @Override
  public String getDimensionOrder() {
    return getHelper().getDimensionOrder();
  }

  @Override
  public boolean isOrderCertain() {
    return getHelper().isOrderCertain();
  }

  @Override
  public boolean isThumbnailSeries() {
    return getHelper().isThumbnailSeries();
  }

  @Override
  public boolean isInterleaved() {
    return getHelper().isInterleaved();
  }

  @Override
  public boolean isInterleaved(int subC) {
    return getHelper().isInterleaved(subC);
  }

  @Override
  public byte[] openBytes(int no) throws FormatException, IOException {
    return getHelper().openBytes(no);
  }

  @Override
  public byte[] openBytes(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    return getHelper().openBytes(no, x, y, w, h);
  }

  @Override
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    return getHelper().openBytes(no, buf);
  }

  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    return getHelper().openBytes(no, buf, x, y, w, h);
  }

  @Override
  public Object openPlane(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    return getHelper().openPlane(no, x, y, w, h);
  }

  @Override
  public byte[] openThumbBytes(int no) throws FormatException, IOException {
    return getHelper().openThumbBytes(no);
  }

  @Override
  public void close(boolean fileOnly) throws IOException {
    ReaderWrapper helper = getHelper();
    if (helper != null) {
      helper.close(fileOnly);
    }
  }

  @Override
  public int getSeriesCount() {
    return getHelper().getSeriesCount();
  }

  @Override
  public void setSeries(int no) {
    getHelper().setSeries(no);
  }

  @Override
  public int getSeries() {
    return getHelper().getSeries();
  }

  // Not overridden: setGroupFiles()

  // Not overridden: isGroupFiles()

  @Override
  public boolean isMetadataComplete() {
    return getHelper().isMetadataComplete();
  }

  // Not overridden: setNormalized(boolean normalize)

  // Not overridden: isNormalized()

  // Not overridden: setOriginalMetadataPopulated(boolean populate)

  // Not overridden: isOriginalMetadataPopulated()

  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    return getHelper().getSeriesUsedFiles(noPixels);
  }

  @Override
  public String[] getUsedFiles(boolean noPixels) {
    return getHelper().getUsedFiles(noPixels);
  }

  @Override
  public int getIndex(int z, int c, int t) {
    return getHelper().getIndex(z, c, t);
  }

  @Override
  public int[] getZCTCoords(int index) {
    return getHelper().getZCTCoords(index);
  }

  @Override
  public Object getMetadataValue(String field) {
    return getHelper().getMetadataValue(field);
  }

  @Override
  public Object getSeriesMetadataValue(String field) {
    return getHelper().getSeriesMetadataValue(field);
  }

  @Override
  public Hashtable<String, Object> getGlobalMetadata() {
    return getHelper().getGlobalMetadata();
  }

  @Override
  public Hashtable<String, Object> getSeriesMetadata() {
    return getHelper().getSeriesMetadata();
  }

  @Override
  public List<CoreMetadata> getCoreMetadataList() {
    return getHelper().getCoreMetadataList();
  }

  // Not overridden: setMetadataFiltered(boolean filter)

  // Not overridden: isMetadataFiltered()

  // Not overridden: setMetadataStore(MetadataStore store)

  // Not overridden: getMetadataStore()

  @Override
  public Object getMetadataStoreRoot() {
    return getHelper().getMetadataStoreRoot();
  }

  @Override
  public IFormatReader[] getUnderlyingReaders() {
    return new IFormatReader[] {getHelper()};
  }

  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return getHelper().isSingleFile(id);
  }

  @Override
  public String getDatasetStructureDescription() {
    return getHelper().getDatasetStructureDescription();
  }

  @Override
  public boolean hasCompanionFiles() {
    return getHelper().hasCompanionFiles();
  }

  @Override
  public String[] getPossibleDomains(String id)
    throws FormatException, IOException
  {
    return getHelper().getPossibleDomains(id);
  }

  @Override
  public String[] getDomains() {
    return getHelper().getDomains();
  }

  @Override
  public int getOptimalTileWidth() {
    return getHelper().getOptimalTileWidth();
  }

  @Override
  public int getOptimalTileHeight() {
    return getHelper().getOptimalTileHeight();
  }

  @Override
  public int getCoreIndex() {
    return getHelper().getCoreIndex();
  }

  @Override
  public void setCoreIndex(int no) {
    getHelper().setCoreIndex(no);
  }

  @Override
  public int seriesToCoreIndex(int series) {
    return getHelper().seriesToCoreIndex(series);
  }

  @Override
  public int coreIndexToSeries(int index) {
    return getHelper().coreIndexToSeries(index);
  }

  @Override
  public int getResolutionCount() {
    return getHelper().getResolutionCount();
  }

  @Override
  public void setResolution(int no) {
    getHelper().setResolution(no);
  }

  @Override
  public int getResolution() {
    return getHelper().getResolution();
  }

  // Not overridden: hasFlattenedResolutions()

  // Not overridden: setFlattenedResolutions(boolean flattened)

  // -- IFormatHandler API methods --

  @Override
  public Class<?> getNativeDataType() {
    return getHelper().getNativeDataType();
  }

  @Override
  public void close() throws IOException {
    ReaderWrapper helper = getHelper();
    if (helper != null) {
      helper.close();
    }
  }

  /**
   * A helper may not exist before initFile() is called. This mean setters that would otherwise be
   * called on the helper are instead called on this wrapper, and can be passed to the helper by
   * calling this method
   * @param helper The wrapped helper
   */
  protected void callDeferredSetters(ReaderWrapper helper) {
    // FormatHandler vars
    helper.setMetadataOptions(metadataOptions);
    // FormatReader vars
    helper.setGroupFiles(group);
    helper.setNormalized(normalizeData);
    helper.setOriginalMetadataPopulated(saveOriginalMetadata);
    helper.setMetadataFiltered(filterMetadata);
    helper.setMetadataStore(metadataStore);
    helper.setFlattenedResolutions(flattenedResolutions);
  }
}
