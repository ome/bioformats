/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2014 Open Microscopy Environment:
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
import loci.formats.DelegateReader;
import loci.formats.FileInfo;
import loci.formats.FileStitcher;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.Modulo;
import loci.formats.meta.MetadataStore;

/**
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/in/FilePatternReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/in/FilePatternReader.java;hb=HEAD">Gitweb</a></dd></dl>
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

  public int getImageCount() {
    return helper.getImageCount();
  }

  public boolean isRGB() {
    return helper.isRGB();
  }

  public int getSizeX() {
    return helper.getSizeX();
  }

  public int getSizeY() {
    return helper.getSizeY();
  }

  public int getSizeZ() {
    return helper.getSizeZ();
  }

  public int getSizeC() {
    return helper.getSizeC();
  }

  public int getSizeT() {
    return helper.getSizeT();
  }

  public int getPixelType() {
    return helper.getPixelType();
  }

  public int getBitsPerPixel() {
    return helper.getBitsPerPixel();
  }

  public int getEffectiveSizeC() {
    return helper.getEffectiveSizeC();
  }

  public int getRGBChannelCount() {
    return helper.getRGBChannelCount();
  }

  public boolean isIndexed() {
    return helper.isIndexed();
  }

  public boolean isFalseColor() {
    return helper.isFalseColor();
  }

  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    if (getCurrentFile() == null) {
      return null;
    }
    return helper.get8BitLookupTable();
  }

  public short[][] get16BitLookupTable() throws FormatException, IOException {
    if (getCurrentFile() == null) {
      return null;
    }
    return helper.get16BitLookupTable();
  }

  public Modulo getModuloZ() {
    return helper.getModuloZ();
  }

  public Modulo getModuloC() {
    return helper.getModuloC();
  }

  public Modulo getModuloT() {
    return helper.getModuloT();
  }

  public int[] getChannelDimLengths() {
    return helper.getChannelDimLengths();
  }

  public String[] getChannelDimTypes() {
    return helper.getChannelDimTypes();
  }

  public int getThumbSizeX() {
    return helper.getThumbSizeX();
  }

  public int getThumbSizeY() {
    return helper.getThumbSizeY();
  }

  public boolean isLittleEndian() {
    return helper.isLittleEndian();
  }

  public String getDimensionOrder() {
    return helper.getDimensionOrder();
  }

  public boolean isOrderCertain() {
    return helper.isOrderCertain();
  }

  public boolean isThumbnailSeries() {
    return helper.isThumbnailSeries();
  }

  public boolean isInterleaved() {
    return helper.isInterleaved();
  }

  public boolean isInterleaved(int subC) {
    return helper.isInterleaved(subC);
  }

  public byte[] openBytes(int no) throws FormatException, IOException {
    return helper.openBytes(no);
  }

  public byte[] openBytes(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    return helper.openBytes(no, x, y, w, h);
  }

  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    return helper.openBytes(no, buf);
  }

  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    return helper.openBytes(no, buf, x, y, w, h);
  }

  public Object openPlane(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    return helper.openPlane(no, x, y, w, h);
  }

  public byte[] openThumbBytes(int no) throws FormatException, IOException {
    return helper.openThumbBytes(no);
  }

  public void close(boolean fileOnly) throws IOException {
    helper.close(fileOnly);
  }

  public int getSeriesCount() {
    return helper.getSeriesCount();
  }

  public void setSeries(int no) {
    helper.setSeries(no);
  }

  public int getSeries() {
    return helper.getSeries();
  }

  public void setGroupFiles(boolean group) {
    helper.setGroupFiles(group);
  }

  public boolean isGroupFiles() {
    return helper.isGroupFiles();
  }

  public boolean isMetadataComplete() {
    return helper.isMetadataComplete();
  }

  public void setNormalized(boolean normalize) {
    helper.setNormalized(normalize);
  }

  public boolean isNormalized() { return helper.isNormalized(); }

  public void setOriginalMetadataPopulated(boolean populate) {
    helper.setOriginalMetadataPopulated(populate);
  }

  public boolean isOriginalMetadataPopulated() {
    return helper.isOriginalMetadataPopulated();
  }

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

  public int getIndex(int z, int c, int t) {
    return helper.getIndex(z, c, t);
  }

  public int[] getZCTCoords(int index) {
    return helper.getZCTCoords(index);
  }

  public Object getMetadataValue(String field) {
    return helper.getMetadataValue(field);
  }

  public Object getSeriesMetadataValue(String field) {
    return helper.getSeriesMetadataValue(field);
  }

  public Hashtable<String, Object> getGlobalMetadata() {
    return helper.getGlobalMetadata();
  }

  public Hashtable<String, Object> getSeriesMetadata() {
    return helper.getSeriesMetadata();
  }

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

  public void setMetadataFiltered(boolean filter) {
    helper.setMetadataFiltered(filter);
  }

  public boolean isMetadataFiltered() { return helper.isMetadataFiltered(); }

  public void setMetadataStore(MetadataStore store) {
    helper.setMetadataStore(store);
  }

  public MetadataStore getMetadataStore() {
    return helper.getMetadataStore();
  }

  public Object getMetadataStoreRoot() {
    return helper.getMetadataStoreRoot();
  }

  public IFormatReader[] getUnderlyingReaders() {
    return new IFormatReader[] {helper};
  }

  public boolean isSingleFile(String id) throws FormatException, IOException {
    return false;
  }

  public String getDatasetStructureDescription() {
    return helper.getDatasetStructureDescription();
  }

  public boolean hasCompanionFiles() {
    return true;
  }

  public String[] getPossibleDomains(String id)
    throws FormatException, IOException
  {
    return helper.getPossibleDomains(id);
  }

  public String[] getDomains() {
    return helper.getDomains();
  }

  public int getOptimalTileWidth() {
    return helper.getOptimalTileWidth();
  }

  public int getOptimalTileHeight() {
    return helper.getOptimalTileHeight();
  }

  public int getCoreIndex() {
    return helper.getCoreIndex();
  }

  public void setCoreIndex(int no) {
    helper.setCoreIndex(no);
  }

  public int seriesToCoreIndex(int series) {
    return helper.seriesToCoreIndex(series);
  }

  public int coreIndexToSeries(int index) {
    return helper.coreIndexToSeries(index);
  }

  public int getResolutionCount() {
    return helper.getResolutionCount();
  }

  public void setResolution(int no) {
    helper.setResolution(no);
  }

  public int getResolution() {
    return helper.getResolution();
  }

  public boolean hasFlattenedResolutions() {
    return helper.hasFlattenedResolutions();
  }

  public void setFlattenedResolutions(boolean flattened) {
    helper.setFlattenedResolutions(flattened);
  }

  // -- IFormatHandler API methods --

  public Class<?> getNativeDataType() {
    return helper.getNativeDataType();
  }

  public void close() throws IOException {
    if (helper != null) {
      helper.close();
    }
  }

  // -- Internal FormatReader methods --

  /* @see loci.formats.FormatReader#initFile(String) */
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
