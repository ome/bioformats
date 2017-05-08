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

package loci.formats.in;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import loci.common.DataTools;
import loci.common.Location;
import loci.formats.ClassList;
import loci.formats.CoreMetadata;
import loci.formats.FileStitcher;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.Modulo;
import loci.formats.meta.MetadataStore;

/**
 *
 */
public class FilePatternReader extends FormatReader {

  // -- Fields --

  private FileStitcher helper;

  // -- Constructor --

  /** Constructs a new pattern reader. */
  public FilePatternReader() {
    super("File pattern", new String[] {"pattern"});

    ClassList<IFormatReader> classes = ImageReader.getDefaultReaderClasses();
    Class<? extends IFormatReader>[] classArray = classes.getClasses();
    ClassList<IFormatReader> newClasses =
      new ClassList<IFormatReader>(IFormatReader.class);
    for (Class<? extends IFormatReader> c : classArray) {
      if (!c.equals(FilePatternReader.class)) {
        newClasses.addClass(c);
      }
    }
    helper = new FileStitcher(new ImageReader(newClasses));

    suffixSufficient = true;
  }

  // -- IFormatReader methods --

  @Override
  public int getImageCount() {
    return helper.getImageCount();
  }

  @Override
  public boolean isRGB() {
    return helper.isRGB();
  }

  @Override
  public int getSizeX() {
    return helper.getSizeX();
  }

  @Override
  public int getSizeY() {
    return helper.getSizeY();
  }

  @Override
  public int getSizeZ() {
    return helper.getSizeZ();
  }

  @Override
  public int getSizeC() {
    return helper.getSizeC();
  }

  @Override
  public int getSizeT() {
    return helper.getSizeT();
  }

  @Override
  public int getPixelType() {
    return helper.getPixelType();
  }

  @Override
  public int getBitsPerPixel() {
    return helper.getBitsPerPixel();
  }

  @Override
  public int getEffectiveSizeC() {
    return helper.getEffectiveSizeC();
  }

  @Override
  public int getRGBChannelCount() {
    return helper.getRGBChannelCount();
  }

  @Override
  public boolean isIndexed() {
    return helper.isIndexed();
  }

  @Override
  public boolean isFalseColor() {
    return helper.isFalseColor();
  }

  @Override
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    if (getCurrentFile() == null) {
      return null;
    }
    return helper.get8BitLookupTable();
  }

  @Override
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    if (getCurrentFile() == null) {
      return null;
    }
    return helper.get16BitLookupTable();
  }

  @Override
  public Modulo getModuloZ() {
    return helper.getModuloZ();
  }

  @Override
  public Modulo getModuloC() {
    return helper.getModuloC();
  }

  @Override
  public Modulo getModuloT() {
    return helper.getModuloT();
  }

  @Override
  public int getThumbSizeX() {
    return helper.getThumbSizeX();
  }

  @Override
  public int getThumbSizeY() {
    return helper.getThumbSizeY();
  }

  @Override
  public boolean isLittleEndian() {
    return helper.isLittleEndian();
  }

  @Override
  public String getDimensionOrder() {
    return helper.getDimensionOrder();
  }

  @Override
  public boolean isOrderCertain() {
    return helper.isOrderCertain();
  }

  @Override
  public boolean isThumbnailSeries() {
    return helper.isThumbnailSeries();
  }

  @Override
  public boolean isInterleaved() {
    return helper.isInterleaved();
  }

  @Override
  public boolean isInterleaved(int subC) {
    return helper.isInterleaved(subC);
  }

  @Override
  public byte[] openBytes(int no) throws FormatException, IOException {
    return helper.openBytes(no);
  }

  @Override
  public byte[] openBytes(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    return helper.openBytes(no, x, y, w, h);
  }

  @Override
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    return helper.openBytes(no, buf);
  }

  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    return helper.openBytes(no, buf, x, y, w, h);
  }

  @Override
  public Object openPlane(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    return helper.openPlane(no, x, y, w, h);
  }

  @Override
  public byte[] openThumbBytes(int no) throws FormatException, IOException {
    return helper.openThumbBytes(no);
  }

  @Override
  public void close(boolean fileOnly) throws IOException {
    helper.close(fileOnly);
  }

  @Override
  public int getSeriesCount() {
    return helper.getSeriesCount();
  }

  @Override
  public void setSeries(int no) {
    helper.setSeries(no);
  }

  @Override
  public int getSeries() {
    return helper.getSeries();
  }

  @Override
  public void setGroupFiles(boolean group) {
    helper.setGroupFiles(group);
  }

  @Override
  public boolean isGroupFiles() {
    return helper.isGroupFiles();
  }

  @Override
  public boolean isMetadataComplete() {
    return helper.isMetadataComplete();
  }

  @Override
  public void setNormalized(boolean normalize) {
    helper.setNormalized(normalize);
  }

  @Override
  public boolean isNormalized() { return helper.isNormalized(); }

  @Override
  public void setOriginalMetadataPopulated(boolean populate) {
    helper.setOriginalMetadataPopulated(populate);
  }

  @Override
  public boolean isOriginalMetadataPopulated() {
    return helper.isOriginalMetadataPopulated();
  }

  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    if (noPixels) {
      return new String[] {currentId};
    }
    String[] helperFiles = helper.getSeriesUsedFiles(noPixels);
    String[] allFiles = new String[helperFiles.length + 1];
    allFiles[0] = currentId;
    System.arraycopy(helperFiles, 0, allFiles, 1, helperFiles.length);
    return allFiles;
  }

  @Override
  public String[] getUsedFiles(boolean noPixels) {
    if (noPixels) {
      return new String[] {currentId};
    }
    String[] helperFiles = helper.getUsedFiles(noPixels);
    String[] allFiles = new String[helperFiles.length + 1];
    allFiles[0] = currentId;
    System.arraycopy(helperFiles, 0, allFiles, 1, helperFiles.length);
    return allFiles;
  }

  @Override
  public int getIndex(int z, int c, int t) {
    return helper.getIndex(z, c, t);
  }

  @Override
  public int[] getZCTCoords(int index) {
    return helper.getZCTCoords(index);
  }

  @Override
  public Object getMetadataValue(String field) {
    return helper.getMetadataValue(field);
  }

  @Override
  public Object getSeriesMetadataValue(String field) {
    return helper.getSeriesMetadataValue(field);
  }

  @Override
  public Hashtable<String, Object> getGlobalMetadata() {
    return helper.getGlobalMetadata();
  }

  @Override
  public Hashtable<String, Object> getSeriesMetadata() {
    return helper.getSeriesMetadata();
  }

  @Override
  public List<CoreMetadata> getCoreMetadataList() {
    // Only used for determining the object type.
    List<CoreMetadata> oldcore = helper.getCoreMetadataList();
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
    helper.setMetadataFiltered(filter);
  }

  @Override
  public boolean isMetadataFiltered() { return helper.isMetadataFiltered(); }

  @Override
  public void setMetadataStore(MetadataStore store) {
    helper.setMetadataStore(store);
  }

  @Override
  public MetadataStore getMetadataStore() {
    return helper.getMetadataStore();
  }

  @Override
  public Object getMetadataStoreRoot() {
    return helper.getMetadataStoreRoot();
  }

  @Override
  public IFormatReader[] getUnderlyingReaders() {
    return new IFormatReader[] {helper};
  }

  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  @Override
  public String getDatasetStructureDescription() {
    return helper.getDatasetStructureDescription();
  }

  @Override
  public boolean hasCompanionFiles() {
    return true;
  }

  @Override
  public String[] getPossibleDomains(String id)
    throws FormatException, IOException
  {
    return helper.getPossibleDomains(id);
  }

  @Override
  public String[] getDomains() {
    return helper.getDomains();
  }

  @Override
  public int getOptimalTileWidth() {
    return helper.getOptimalTileWidth();
  }

  @Override
  public int getOptimalTileHeight() {
    return helper.getOptimalTileHeight();
  }

  @Override
  public int getCoreIndex() {
    return helper.getCoreIndex();
  }

  @Override
  public void setCoreIndex(int no) {
    helper.setCoreIndex(no);
  }

  @Override
  public int seriesToCoreIndex(int series) {
    return helper.seriesToCoreIndex(series);
  }

  @Override
  public int coreIndexToSeries(int index) {
    return helper.coreIndexToSeries(index);
  }

  @Override
  public int getResolutionCount() {
    return helper.getResolutionCount();
  }

  @Override
  public void setResolution(int no) {
    helper.setResolution(no);
  }

  @Override
  public int getResolution() {
    return helper.getResolution();
  }

  @Override
  public boolean hasFlattenedResolutions() {
    return helper.hasFlattenedResolutions();
  }

  @Override
  public void setFlattenedResolutions(boolean flattened) {
    helper.setFlattenedResolutions(flattened);
  }

  // -- IFormatHandler API methods --

  @Override
  public Class<?> getNativeDataType() {
    return helper.getNativeDataType();
  }

  @Override
  public void close() throws IOException {
    if (helper != null) {
      helper.close();
    }
  }

  // -- Internal FormatReader methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  @Override
  protected void initFile(String id) throws FormatException, IOException {
    // read the pattern from the file
    // the file should just contain a single line with the relative or
    // absolute file pattern

    currentId = new Location(id).getAbsolutePath();
    String pattern = DataTools.readFile(id).trim();
    String dir = new Location(id).getAbsoluteFile().getParent();
    if (new Location(pattern).getParent() == null) {
      pattern = dir + File.separator + pattern;
    }

    helper.setUsingPatternIds(true);
    helper.setCanChangePattern(false);
    helper.setId(pattern);
    core = helper.getCoreMetadataList();
  }

}
