/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
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

import loci.formats.in.MetadataLevel;
import loci.formats.in.MetadataOptions;
import loci.formats.meta.MetadataStore;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

/**
 * Helper class for readers which wrap other readers.
 *
 * getHelper() must return a valid object once the constructor has completed unless the reader is
 * closed. returning null will cause strange errors.
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

  @Override
  public Set<MetadataLevel> getSupportedMetadataLevels() {
    return getHelper().getSupportedMetadataLevels();
  }

  @Override
  public void setMetadataOptions(MetadataOptions options) {
    getHelper().setMetadataOptions(options);
  }

  @Override
  public MetadataOptions getMetadataOptions() {
    return getHelper().getMetadataOptions();
  }

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

  @Override
  public void setGroupFiles(boolean group) {
    getHelper().setGroupFiles(group);
  }

  @Override
  public boolean isGroupFiles() {
    return getHelper().isGroupFiles();
  }

  @Override
  public boolean isMetadataComplete() {
    return getHelper().isMetadataComplete();
  }

  @Override
  public void setNormalized(boolean normalize) {
    getHelper().setNormalized(normalize);
  }

  @Override
  public boolean isNormalized() { return getHelper().isNormalized(); }

  @Override
  public void setOriginalMetadataPopulated(boolean populate) {
    getHelper().setOriginalMetadataPopulated(populate);
  }

  @Override
  public boolean isOriginalMetadataPopulated() {
    return getHelper().isOriginalMetadataPopulated();
  }

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

  @Override
  public void setMetadataFiltered(boolean filter) {
    getHelper().setMetadataFiltered(filter);
  }

  @Override
  public boolean isMetadataFiltered() { return getHelper().isMetadataFiltered(); }

  @Override
  public void setMetadataStore(MetadataStore store) {
    getHelper().setMetadataStore(store);
  }

  @Override
  public MetadataStore getMetadataStore() {
    return getHelper().getMetadataStore();
  }

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

  @Override
  public boolean hasFlattenedResolutions() {
    return getHelper().hasFlattenedResolutions();
  }

  @Override
  public void setFlattenedResolutions(boolean flattened) {
    getHelper().setFlattenedResolutions(flattened);
  }

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
}
