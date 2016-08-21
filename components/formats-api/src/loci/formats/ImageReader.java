/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.in.MetadataLevel;
import loci.formats.in.MetadataOptions;
import loci.formats.meta.MetadataStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ImageReader is the master file format reader for all supported formats.
 * It uses one instance of each reader subclass (specified in readers.txt,
 * or other class list source) to identify file formats and read data.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/ImageReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/ImageReader.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class ImageReader implements IFormatReader {

  // -- Constants --

  private static final Logger LOGGER =
    LoggerFactory.getLogger(ImageReader.class);

  // -- Static fields --

  /** Default list of reader classes, for use with noargs constructor. */
  private static ClassList<IFormatReader> defaultClasses;

  // -- Static utility methods --

  public static ClassList<IFormatReader> getDefaultReaderClasses() {
    if (defaultClasses == null) {
      // load built-in reader classes from readers.txt file
      try {
        defaultClasses =
          new ClassList<IFormatReader>("readers.txt", IFormatReader.class);
      }
      catch (IOException exc) {
        defaultClasses = new ClassList<IFormatReader>(IFormatReader.class);
        LOGGER.info("Could not parse class list; using default classes", exc);
      }
    }
    return defaultClasses;
  }

  // -- Fields --

  /** List of supported file format readers. */
  private IFormatReader[] readers;

  /**
   * Valid suffixes for this file format.
   * Populated the first time getSuffixes() is called.
   */
  private String[] suffixes;

  /** Name of current file. */
  private String currentId;

  /** Current form index. */
  private int current;

  private boolean allowOpen = true;

  // -- Constructors --

  /**
   * Constructs a new ImageReader with the default
   * list of reader classes from readers.txt.
   */
  public ImageReader() {
    this(getDefaultReaderClasses());
  }

  /** Constructs a new ImageReader from the given list of reader classes. */
  public ImageReader(ClassList<IFormatReader> classList) {
    // add readers to the list
    List<IFormatReader> list = new ArrayList<IFormatReader>();
    Class<? extends IFormatReader>[] c = classList.getClasses();
    for (int i=0; i<c.length; i++) {
      IFormatReader reader = null;
      try {
        reader = c[i].newInstance();
      }
      catch (IllegalAccessException exc) { }
      catch (InstantiationException exc) { }
      if (reader == null) {
        LOGGER.error("{} cannot be instantiated.", c[i].getName());
        continue;
      }
      list.add(reader);
    }
    readers = new IFormatReader[list.size()];
    list.toArray(readers);
  }

  // -- ImageReader API methods --

  /**
   * Toggles whether or not file system access is allowed when doing type
   * detection.  By default, file system access is allowed.
   */
  public void setAllowOpenFiles(boolean allowOpen) {
    this.allowOpen = allowOpen;
  }

  /** Gets a string describing the file format for the given file. */
  public String getFormat(String id) throws FormatException, IOException {
    return getReader(id).getFormat();
  }

  /** Gets the reader used to open the given file. */
  public IFormatReader getReader(String id)
    throws FormatException, IOException
  {
   // HACK: skip file existence check for fake files
   if (id.endsWith(File.separator)) {
    id = id.substring(0, id.length() - 1);
   }
   boolean fake = id != null && id.toLowerCase().endsWith(".fake");
   boolean omero = id != null && id.toLowerCase().startsWith("omero:") &&
    id.indexOf("\n") > 0;

   // blacklist temporary files that are being copied e.g. by WinSCP
   boolean invalid = id != null && id.toLowerCase().endsWith(".filepart");

   // NB: Check that we can generate a valid handle for the ID;
   // e.g., for files, this will throw an exception if the file is missing.
   if (!fake && !omero) {
     Location.checkValidId(id);
   }

    if (!id.equals(currentId)) {
      // initialize file
      boolean success = false;
      if (!invalid) {
        for (int i=0; i<readers.length; i++) {
          if (readers[i].isThisType(id, allowOpen)) {
            current = i;
            currentId = id;
            success = true;
            break;
          }
        }
      }
      if (!success) {
        throw new UnknownFormatException("Unknown file format: " + id);
      }
    }
    return getReader();
  }

  /** Gets the reader used to open the current file. */
  public IFormatReader getReader() {
    FormatTools.assertId(currentId, true, 2);
    return readers[current];
  }

  /** Gets the file format reader instance matching the given class. */
  public IFormatReader getReader(Class<? extends IFormatReader> c) {
    for (int i=0; i<readers.length; i++) {
      if (readers[i].getClass().equals(c)) return readers[i];
    }
    return null;
  }

  /** Gets all constituent file format readers. */
  public IFormatReader[] getReaders() {
    IFormatReader[] r = new IFormatReader[readers.length];
    System.arraycopy(readers, 0, r, 0, readers.length);
    return r;
  }

  // -- IMetadataConfigurable API methods --

  /* @see loci.formats.IMetadataConfigurable#getSupportedMetadataLevels() */
  public Set<MetadataLevel> getSupportedMetadataLevels() {
    return getReaders()[0].getSupportedMetadataLevels();
  }

  /* @see loci.formats.IMetadataConfigurable#getMetadataOptions() */
  public MetadataOptions getMetadataOptions() {
    return getReaders()[0].getMetadataOptions();
  }

  /**
   * @see loci.formats.IMetadataConfigurable#setMetadataOptions(MetadataOptions)
   */
  public void setMetadataOptions(MetadataOptions options) {
    for (IFormatReader reader : readers) {
      reader.setMetadataOptions(options);
    }
  }

  // -- IFormatReader API methods --

  /* @see IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    for (int i=0; i<readers.length; i++) {
      if (readers[i].isThisType(name, open)) return true;
    }
    return false;
  }

  /* @see IFormatReader.isThisType(byte[]) */
  public boolean isThisType(byte[] block) {
    for (int i=0; i<readers.length; i++) {
      if (readers[i].isThisType(block)) return true;
    }
    return false;
  }

  /* @see IFormatReader.isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    for (int i=0; i<readers.length; i++) {
      if (readers[i].isThisType(stream)) return true;
    }
    return false;
  }

  /* @see IFormatReader#getImageCount() */
  public int getImageCount() {
    return getReader().getImageCount();
  }

  /* @see IFormatReader#isRGB() */
  public boolean isRGB() {
    return getReader().isRGB();
  }

  /* @see IFormatReader#getSizeX() */
  public int getSizeX() {
    return getReader().getSizeX();
  }

  /* @see IFormatReader#getSizeY() */
  public int getSizeY() {
    return getReader().getSizeY();
  }

  /* @see IFormatReader#getSizeC() */
  public int getSizeC() {
    return getReader().getSizeC();
  }

  /* @see IFormatReader#getSizeZ() */
  public int getSizeZ() {
    return getReader().getSizeZ();
  }

  /* @see IFormatReader#getSizeT() */
  public int getSizeT() {
    return getReader().getSizeT();
  }

  /* @see IFormatReader#getPixelType() */
  public int getPixelType() {
    return getReader().getPixelType();
  }

  /* @see IFormatReader#getBitsPerPixel() */
  public int getBitsPerPixel() {
    return getReader().getBitsPerPixel();
  }

  /* @see IFormatReader#getEffectiveSizeC() */
  public int getEffectiveSizeC() {
    return getReader().getEffectiveSizeC();
  }

  /* @see IFormatReader#getRGBChannelCount() */
  public int getRGBChannelCount() {
    return getReader().getRGBChannelCount();
  }

  /* @see IFormatReader#isIndexed() */
  public boolean isIndexed() {
    return getReader().isIndexed();
  }

  /* @see IFormatReader#isFalseColor() */
  public boolean isFalseColor() {
    return getReader().isFalseColor();
  }

  /* @see IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    return getReader().get8BitLookupTable();
  }

  /* @see IFormatReader#get16BitLookupTable() */
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    return getReader().get16BitLookupTable();
  }

  /* @see IFormatReader#getModuloZ() */
  public Modulo getModuloZ() {
    return getReader().getModuloZ();
  }

  /* @see IFormatReader#getModuloC() */
  public Modulo getModuloC() {
    return getReader().getModuloC();
  }

  /* @see IFormatReader#getModuloT() */
  public Modulo getModuloT() {
    return getReader().getModuloT();
  }

  /* @see IFormatReader#getChannelDimLengths() */
  public int[] getChannelDimLengths() {
    return getReader().getChannelDimLengths();
  }

  /* @see IFormatReader#getChannelDimTypes() */
  public String[] getChannelDimTypes() {
    return getReader().getChannelDimTypes();
  }

  /* @see IFormatReader#getThumbSizeX() */
  public int getThumbSizeX() {
    return getReader().getThumbSizeX();
  }

  /* @see IFormatReader#getThumbSizeY() */
  public int getThumbSizeY() {
    return getReader().getThumbSizeY();
  }

  /* @see IFormatReader#isLittleEndian() */
  public boolean isLittleEndian() {
    return getReader().isLittleEndian();
  }

  /* @see IFormatReader#getDimensionOrder() */
  public String getDimensionOrder() {
    return getReader().getDimensionOrder();
  }

  /* @see IFormatReader#isOrderCertain() */
  public boolean isOrderCertain() {
    return getReader().isOrderCertain();
  }

  /* @see IFormatReader#isThumbnailSeries() */
  public boolean isThumbnailSeries() {
    return getReader().isThumbnailSeries();
  }

  /* @see IFormatReader#isInterleaved() */
  public boolean isInterleaved() {
    return getReader().isInterleaved();
  }

  /* @see IFormatReader#isInterleaved(int) */
  public boolean isInterleaved(int subC) {
    return getReader().isInterleaved(subC);
  }

  /* @see IFormatReader#openBytes(int) */
  public byte[] openBytes(int no) throws FormatException, IOException {
    return getReader().openBytes(no);
  }

  /* @see IFormatReader#openBytes(int, int, int, int, int) */
  public byte[] openBytes(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    return getReader().openBytes(no, x, y, w, h);
  }

  /* @see IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    return getReader().openBytes(no, buf);
  }

  /* @see IFormatReader#openBytes(int, byte[], int, int, int, int) */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    return getReader().openBytes(no, buf, x, y, w, h);
  }

  /* @see IFormatReader#openPlane(int, int, int, int, int) */
  public Object openPlane(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    return getReader().openPlane(no, x, y, w, h);
  }

  /* @see IFormatReader#openThumbBytes(int) */
  public byte[] openThumbBytes(int no) throws FormatException, IOException {
    return getReader().openThumbBytes(no);
  }

  /* @see IFormatReader#getSeriesCount() */
  public int getSeriesCount() {
    return getReader().getSeriesCount();
  }

  /* @see IFormatReader#setSeries(int) */
  public void setSeries(int no) {
    getReader().setSeries(no);
  }

  /* @see IFormatReader#getSeries() */
  public int getSeries() {
    return getReader().getSeries();
  }

  /* @see IFormatReader#getUsedFiles() */
  public String[] getUsedFiles() {
    return getReader().getUsedFiles();
  }

  /* @see IFormatReader#getUsedFiles(boolean) */
  public String[] getUsedFiles(boolean noPixels) {
    return getReader().getUsedFiles(noPixels);
  }

  /* @see IFormatReader#getSeriesUsedFiles() */
  public String[] getSeriesUsedFiles() {
    return getReader().getSeriesUsedFiles();
  }

  /* @see IFormatReader#getSeriesUsedFiles(boolean) */
  public String[] getSeriesUsedFiles(boolean noPixels) {
    return getReader().getSeriesUsedFiles(noPixels);
  }

  /* @see IFormatReader#getAdvancedUsedFiles(boolean) */
  public FileInfo[] getAdvancedUsedFiles(boolean noPixels) {
    return getReader().getAdvancedUsedFiles(noPixels);
  }

  /* @see IFormatReader#getAdvancedSeriesUsedFiles(boolean) */
  public FileInfo[] getAdvancedSeriesUsedFiles(boolean noPixels) {
    return getReader().getAdvancedSeriesUsedFiles(noPixels);
  }

  /* @see IFormatReader#getIndex(int, int, int) */
  public int getIndex(int z, int c, int t) {
    return getReader().getIndex(z, c, t);
  }

  /* @see IFormatReader#getZCTCoords(int) */
  public int[] getZCTCoords(int index) {
    return getReader().getZCTCoords(index);
  }

  /* @see IFormatReader#getMetadataValue(String) */
  public Object getMetadataValue(String field) {
    return getReader().getMetadataValue(field);
  }

  /* @see IFormatReader#getSeriesMetadataValue(String) */
  public Object getSeriesMetadataValue(String field) {
    return getReader().getSeriesMetadataValue(field);
  }

  /* @see IFormatReader#getGlobalMetadata() */
  public Hashtable<String, Object> getGlobalMetadata() {
    return getReader().getGlobalMetadata();
  }

  /* @see IFormatReader#getSeriesMetadata() */
  public Hashtable<String, Object> getSeriesMetadata() {
    return getReader().getSeriesMetadata();
  }

  /**
   * @deprecated
   * @see IFormatReader#getCoreMetadata()
   */
  public CoreMetadata[] getCoreMetadata() {
    return getCoreMetadataList().toArray(new CoreMetadata[0]);
  }

  /* @see IFormatReader#getCoreMetadataList() */
  public List<CoreMetadata> getCoreMetadataList() {
    return getReader().getCoreMetadataList();
  }

  /* @see IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    for (int i=0; i<readers.length; i++) readers[i].close(fileOnly);
    if (!fileOnly) currentId = null;
  }

  /* @see IFormatReader#setGroupFiles(boolean) */
  public void setGroupFiles(boolean group) {
    FormatTools.assertId(currentId, false, 2);
    for (int i=0; i<readers.length; i++) readers[i].setGroupFiles(group);
  }

  /* @see IFormatReader#isGroupFiles() */
  public boolean isGroupFiles() {
    // all readers should have same file grouping setting
    return readers[0].isGroupFiles();
  }

  /* @see IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return getReader(id).fileGroupOption(id);
  }

  /* @see IFormatReader#isMetadataComplete() */
  public boolean isMetadataComplete() {
    return getReader().isMetadataComplete();
  }

  /* @see IFormatReader#setNormalized(boolean) */
  public void setNormalized(boolean normalize) {
    FormatTools.assertId(currentId, false, 2);
    for (int i=0; i<readers.length; i++) readers[i].setNormalized(normalize);
  }

  /* @see IFormatReader#isNormalized() */
  public boolean isNormalized() {
    // NB: all readers should have the same normalization setting
    return readers[0].isNormalized();
  }

  /* @see IFormatReader#setOriginalMetadataPopulated(boolean) */
  public void setOriginalMetadataPopulated(boolean populate) {
    FormatTools.assertId(currentId, false, 1);
    for (int i=0; i<readers.length; i++) {
      readers[i].setOriginalMetadataPopulated(populate);
    }
  }

  /* @see IFormatReader#isOriginalMetadataPopulated() */
  public boolean isOriginalMetadataPopulated() {
    return readers[0].isOriginalMetadataPopulated();
  }

  /* @see IFormatReader#getCurrentFile() */
  public String getCurrentFile() {
    if (currentId == null) return null;
    return getReader().getCurrentFile();
  }

  /* @see IFormatReader#setMetadataFiltered(boolean) */
  public void setMetadataFiltered(boolean filter) {
    FormatTools.assertId(currentId, false, 2);
    for (int i=0; i<readers.length; i++) readers[i].setMetadataFiltered(filter);
  }

  /* @see IFormatReader#isMetadataFiltered() */
  public boolean isMetadataFiltered() {
    // NB: all readers should have the same metadata filtering setting
    return readers[0].isMetadataFiltered();
  }

  /* @see IFormatReader#setMetadataStore(MetadataStore) */
  public void setMetadataStore(MetadataStore store) {
    FormatTools.assertId(currentId, false, 2);
    for (int i=0; i<readers.length; i++) readers[i].setMetadataStore(store);
  }

  /* @see IFormatReader#getMetadataStore() */
  public MetadataStore getMetadataStore() {
    return getReader().getMetadataStore();
  }

  /* @see IFormatReader#getMetadataStoreRoot() */
  public Object getMetadataStoreRoot() {
    return getReader().getMetadataStoreRoot();
  }

  /* @see IFormatReader#getUnderlyingReaders() */
  public IFormatReader[] getUnderlyingReaders() {
    return getReaders();
  }

  /* @see IFormatReader#isSingleFile(String) */
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return getReader(id).isSingleFile(id);
  }

  /* @see IFormatReader#getRequiredDirectories(String[]) */
  public int getRequiredDirectories(String[] files)
    throws FormatException, IOException
  {
    return getReader(files[0]).getRequiredDirectories(files);
  }

  /* @see IFormatReader#getDatasetStructureDescription() */
  public String getDatasetStructureDescription() {
    return getReader().getDatasetStructureDescription();
  }

  /* @see IFormatReader#hasCompanionFiles() */
  public boolean hasCompanionFiles() {
    return getReader().hasCompanionFiles();
  }

  /* @see IFormatReader#getPossibleDomains(String) */
  public String[] getPossibleDomains(String id)
    throws FormatException, IOException
  {
    return getReader(id).getPossibleDomains(id);
  }

  /* @see IFormatReader#getDomains() */
  public String[] getDomains() {
    return getReader().getDomains();
  }

  /* @see IFormatReader#getOptimalTileWidth() */
  public int getOptimalTileWidth() {
    return getReader().getOptimalTileWidth();
  }

  /* @see IFormatReader#getOptimalTileHeight() */
  public int getOptimalTileHeight() {
    return getReader().getOptimalTileHeight();
  }

  /* @see IFormatReader#getCoreIndex() */
  public int getCoreIndex() {
    return getReader().getCoreIndex();
  }

  /* @see IFormatReader#setCoreIndex(int) */
  public void setCoreIndex(int no) {
    getReader().setCoreIndex(no);
  }

  public int seriesToCoreIndex(int series) {
    return getReader().seriesToCoreIndex(series);
  }

  public int coreIndexToSeries(int index) {
    return getReader().coreIndexToSeries(index);
  }

  /* @see IFormatReader#getResolutionCount() */
  public int getResolutionCount() {
    return getReader().getResolutionCount();
  }

  /* @see IFormatReader#setResolution(int) */
  public void setResolution(int no) {
    getReader().setResolution(no);
  }

  /* @see IFormatReader#getResolution() */
  public int getResolution() {
    return getReader().getResolution();
  }

  /* @see IFormatReader#hasFlattenedResolutions() */
  public boolean hasFlattenedResolutions() {
    // all readers should have the same flattened setting
    return readers[0].hasFlattenedResolutions();
  }

  /* @see IFormatReader#setFlattenedResolutions(boolean) */
  public void setFlattenedResolutions(boolean flattened) {
    for (IFormatReader reader : readers) {
      reader.setFlattenedResolutions(flattened);
    }
  }

  // -- IFormatHandler API methods --

  /* @see IFormatHandler#isThisType(String) */
  public boolean isThisType(String name) {
    // if necessary, open the file for further analysis
    // but check isThisType(name, false) first, for efficiency
    return isThisType(name, false) || isThisType(name, true);
  }

  /* @see IFormatHandler#getFormat() */
  public String getFormat() { return getReader().getFormat(); }

  /* @see IFormatHandler#getSuffixes() */
  public String[] getSuffixes() {
    if (suffixes == null) {
      HashSet<String> suffixSet = new HashSet<String>();
      for (int i=0; i<readers.length; i++) {
        String[] suf = readers[i].getSuffixes();
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
    return getReader().getNativeDataType();
  }

  /* @see IFormatHandler#setId(String) */
  public void setId(String id) throws FormatException, IOException {
    IFormatReader currentReader = getReader(id);
    LOGGER.info("{} initializing {}",
      currentReader.getClass().getSimpleName(), id);
    currentReader.setId(id);
  }

  /* @see IFormatHandler#close() */
  public void close() throws IOException { close(false); }

}
