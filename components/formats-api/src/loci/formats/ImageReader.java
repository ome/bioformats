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
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.common.xml.XMLTools;
import loci.formats.in.MetadataLevel;
import loci.formats.in.MetadataOptions;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.services.OMEXMLService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ImageReader is the master file format reader for all supported formats.
 * It uses one instance of each reader subclass (specified in readers.txt,
 * or other class list source) to identify file formats and read data.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class ImageReader implements IFormatReader {

  // -- Constants --

  private static final Logger LOGGER =
    LoggerFactory.getLogger(ImageReader.class);

  // -- Static fields --
  
  private ServiceFactory factory;
  private OMEXMLService service;
  
  /** Whether or not to validate files upon reading. */
  protected boolean validate = true;

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
  @Override
  public Set<MetadataLevel> getSupportedMetadataLevels() {
    return getReaders()[0].getSupportedMetadataLevels();
  }

  /* @see loci.formats.IMetadataConfigurable#getMetadataOptions() */
  @Override
  public MetadataOptions getMetadataOptions() {
    return getReaders()[0].getMetadataOptions();
  }

  /**
   * @see loci.formats.IMetadataConfigurable#setMetadataOptions(MetadataOptions)
   */
  @Override
  public void setMetadataOptions(MetadataOptions options) {
    for (IFormatReader reader : readers) {
      reader.setMetadataOptions(options);
    }
  }

  // -- IFormatReader API methods --

  /* @see IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String name, boolean open) {
    for (int i=0; i<readers.length; i++) {
      if (readers[i].isThisType(name, open)) return true;
    }
    return false;
  }

  /* @see IFormatReader.isThisType(byte[]) */
  @Override
  public boolean isThisType(byte[] block) {
    for (int i=0; i<readers.length; i++) {
      if (readers[i].isThisType(block)) return true;
    }
    return false;
  }

  /* @see IFormatReader.isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    for (int i=0; i<readers.length; i++) {
      if (readers[i].isThisType(stream)) return true;
    }
    return false;
  }

  /* @see IFormatReader#getImageCount() */
  @Override
  public int getImageCount() {
    return getReader().getImageCount();
  }

  /* @see IFormatReader#isRGB() */
  @Override
  public boolean isRGB() {
    return getReader().isRGB();
  }

  /* @see IFormatReader#getSizeX() */
  @Override
  public int getSizeX() {
    return getReader().getSizeX();
  }

  /* @see IFormatReader#getSizeY() */
  @Override
  public int getSizeY() {
    return getReader().getSizeY();
  }

  /* @see IFormatReader#getSizeC() */
  @Override
  public int getSizeC() {
    return getReader().getSizeC();
  }

  /* @see IFormatReader#getSizeZ() */
  @Override
  public int getSizeZ() {
    return getReader().getSizeZ();
  }

  /* @see IFormatReader#getSizeT() */
  @Override
  public int getSizeT() {
    return getReader().getSizeT();
  }

  /* @see IFormatReader#getPixelType() */
  @Override
  public int getPixelType() {
    return getReader().getPixelType();
  }

  /* @see IFormatReader#getBitsPerPixel() */
  @Override
  public int getBitsPerPixel() {
    return getReader().getBitsPerPixel();
  }

  /* @see IFormatReader#getEffectiveSizeC() */
  @Override
  public int getEffectiveSizeC() {
    return getReader().getEffectiveSizeC();
  }

  /* @see IFormatReader#getRGBChannelCount() */
  @Override
  public int getRGBChannelCount() {
    return getReader().getRGBChannelCount();
  }

  /* @see IFormatReader#isIndexed() */
  @Override
  public boolean isIndexed() {
    return getReader().isIndexed();
  }

  /* @see IFormatReader#isFalseColor() */
  @Override
  public boolean isFalseColor() {
    return getReader().isFalseColor();
  }

  /* @see IFormatReader#get8BitLookupTable() */
  @Override
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    return getReader().get8BitLookupTable();
  }

  /* @see IFormatReader#get16BitLookupTable() */
  @Override
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    return getReader().get16BitLookupTable();
  }

  /* @see IFormatReader#getModuloZ() */
  @Override
  public Modulo getModuloZ() {
    return getReader().getModuloZ();
  }

  /* @see IFormatReader#getModuloC() */
  @Override
  public Modulo getModuloC() {
    return getReader().getModuloC();
  }

  /* @see IFormatReader#getModuloT() */
  @Override
  public Modulo getModuloT() {
    return getReader().getModuloT();
  }

  /* @see IFormatReader#getThumbSizeX() */
  @Override
  public int getThumbSizeX() {
    return getReader().getThumbSizeX();
  }

  /* @see IFormatReader#getThumbSizeY() */
  @Override
  public int getThumbSizeY() {
    return getReader().getThumbSizeY();
  }

  /* @see IFormatReader#isLittleEndian() */
  @Override
  public boolean isLittleEndian() {
    return getReader().isLittleEndian();
  }

  /* @see IFormatReader#getDimensionOrder() */
  @Override
  public String getDimensionOrder() {
    return getReader().getDimensionOrder();
  }

  /* @see IFormatReader#isOrderCertain() */
  @Override
  public boolean isOrderCertain() {
    return getReader().isOrderCertain();
  }

  /* @see IFormatReader#isThumbnailSeries() */
  @Override
  public boolean isThumbnailSeries() {
    return getReader().isThumbnailSeries();
  }

  /* @see IFormatReader#isInterleaved() */
  @Override
  public boolean isInterleaved() {
    return getReader().isInterleaved();
  }

  /* @see IFormatReader#isInterleaved(int) */
  @Override
  public boolean isInterleaved(int subC) {
    return getReader().isInterleaved(subC);
  }

  /* @see IFormatReader#openBytes(int) */
  @Override
  public byte[] openBytes(int no) throws FormatException, IOException {
    return getReader().openBytes(no);
  }

  /* @see IFormatReader#openBytes(int, int, int, int, int) */
  @Override
  public byte[] openBytes(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    return getReader().openBytes(no, x, y, w, h);
  }

  /* @see IFormatReader#openBytes(int, byte[]) */
  @Override
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    return getReader().openBytes(no, buf);
  }

  /* @see IFormatReader#openBytes(int, byte[], int, int, int, int) */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    return getReader().openBytes(no, buf, x, y, w, h);
  }

  /* @see IFormatReader#openPlane(int, int, int, int, int) */
  @Override
  public Object openPlane(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    return getReader().openPlane(no, x, y, w, h);
  }

  /* @see IFormatReader#openThumbBytes(int) */
  @Override
  public byte[] openThumbBytes(int no) throws FormatException, IOException {
    return getReader().openThumbBytes(no);
  }

  /* @see IFormatReader#getSeriesCount() */
  @Override
  public int getSeriesCount() {
    return getReader().getSeriesCount();
  }

  /* @see IFormatReader#setSeries(int) */
  @Override
  public void setSeries(int no) {
    getReader().setSeries(no);
  }

  /* @see IFormatReader#getSeries() */
  @Override
  public int getSeries() {
    return getReader().getSeries();
  }

  /* @see IFormatReader#getUsedFiles() */
  @Override
  public String[] getUsedFiles() {
    return getReader().getUsedFiles();
  }

  /* @see IFormatReader#getUsedFiles(boolean) */
  @Override
  public String[] getUsedFiles(boolean noPixels) {
    return getReader().getUsedFiles(noPixels);
  }

  /* @see IFormatReader#getSeriesUsedFiles() */
  @Override
  public String[] getSeriesUsedFiles() {
    return getReader().getSeriesUsedFiles();
  }

  /* @see IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    return getReader().getSeriesUsedFiles(noPixels);
  }

  /* @see IFormatReader#getAdvancedUsedFiles(boolean) */
  @Override
  public FileInfo[] getAdvancedUsedFiles(boolean noPixels) {
    return getReader().getAdvancedUsedFiles(noPixels);
  }

  /* @see IFormatReader#getAdvancedSeriesUsedFiles(boolean) */
  @Override
  public FileInfo[] getAdvancedSeriesUsedFiles(boolean noPixels) {
    return getReader().getAdvancedSeriesUsedFiles(noPixels);
  }

  /* @see IFormatReader#getIndex(int, int, int) */
  @Override
  public int getIndex(int z, int c, int t) {
    return getReader().getIndex(z, c, t);
  }

  /* @see IFormatReader#getIndex(int, int, int, int, int, int) */
  @Override
  public int getIndex(int z, int c, int t, int moduloZ, int moduloC, int moduloT) {
    return getReader().getIndex(z, c, t, moduloZ, moduloC, moduloT);
  }

  /* @see IFormatReader#getZCTCoords(int) */
  @Override
  public int[] getZCTCoords(int index) {
    return getReader().getZCTCoords(index);
  }

  /* @see IFormatReader#getZCTModuloCoords(int) */
  @Override
  public int[] getZCTModuloCoords(int index) {
    return getReader().getZCTModuloCoords(index);
  }

  /* @see IFormatReader#getMetadataValue(String) */
  @Override
  public Object getMetadataValue(String field) {
    return getReader().getMetadataValue(field);
  }

  /* @see IFormatReader#getSeriesMetadataValue(String) */
  @Override
  public Object getSeriesMetadataValue(String field) {
    return getReader().getSeriesMetadataValue(field);
  }

  /* @see IFormatReader#getGlobalMetadata() */
  @Override
  public Hashtable<String, Object> getGlobalMetadata() {
    return getReader().getGlobalMetadata();
  }

  /* @see IFormatReader#getSeriesMetadata() */
  @Override
  public Hashtable<String, Object> getSeriesMetadata() {
    return getReader().getSeriesMetadata();
  }

  /* @see IFormatReader#getCoreMetadataList() */
  @Override
  public List<CoreMetadata> getCoreMetadataList() {
    return getReader().getCoreMetadataList();
  }

  /* @see IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    for (int i=0; i<readers.length; i++) readers[i].close(fileOnly);
    if (!fileOnly) currentId = null;
  }

  /* @see IFormatReader#setGroupFiles(boolean) */
  @Override
  public void setGroupFiles(boolean group) {
    FormatTools.assertId(currentId, false, 2);
    for (int i=0; i<readers.length; i++) readers[i].setGroupFiles(group);
  }

  /* @see IFormatReader#isGroupFiles() */
  @Override
  public boolean isGroupFiles() {
    // all readers should have same file grouping setting
    return readers[0].isGroupFiles();
  }

  /* @see IFormatReader#fileGroupOption(String) */
  @Override
  public int fileGroupOption(String id) throws FormatException, IOException {
    return getReader(id).fileGroupOption(id);
  }

  /* @see IFormatReader#isMetadataComplete() */
  @Override
  public boolean isMetadataComplete() {
    return getReader().isMetadataComplete();
  }

  /* @see IFormatReader#setNormalized(boolean) */
  @Override
  public void setNormalized(boolean normalize) {
    FormatTools.assertId(currentId, false, 2);
    for (int i=0; i<readers.length; i++) readers[i].setNormalized(normalize);
  }

  /* @see IFormatReader#isNormalized() */
  @Override
  public boolean isNormalized() {
    // NB: all readers should have the same normalization setting
    return readers[0].isNormalized();
  }

  /* @see IFormatReader#setOriginalMetadataPopulated(boolean) */
  @Override
  public void setOriginalMetadataPopulated(boolean populate) {
    FormatTools.assertId(currentId, false, 1);
    for (int i=0; i<readers.length; i++) {
      readers[i].setOriginalMetadataPopulated(populate);
    }
  }

  /* @see IFormatReader#isOriginalMetadataPopulated() */
  @Override
  public boolean isOriginalMetadataPopulated() {
    return readers[0].isOriginalMetadataPopulated();
  }

  /* @see IFormatReader#getCurrentFile() */
  @Override
  public String getCurrentFile() {
    if (currentId == null) return null;
    return getReader().getCurrentFile();
  }

  /* @see IFormatReader#setMetadataFiltered(boolean) */
  @Override
  public void setMetadataFiltered(boolean filter) {
    FormatTools.assertId(currentId, false, 2);
    for (int i=0; i<readers.length; i++) readers[i].setMetadataFiltered(filter);
  }

  /* @see IFormatReader#isMetadataFiltered() */
  @Override
  public boolean isMetadataFiltered() {
    // NB: all readers should have the same metadata filtering setting
    return readers[0].isMetadataFiltered();
  }

  /* @see IFormatReader#setMetadataStore(MetadataStore) */
  @Override
  public void setMetadataStore(MetadataStore store) {
    FormatTools.assertId(currentId, false, 2);
    for (int i=0; i<readers.length; i++) readers[i].setMetadataStore(store);
  }

  /* @see IFormatReader#getMetadataStore() */
  @Override
  public MetadataStore getMetadataStore() {
    return getReader().getMetadataStore();
  }

  /* @see IFormatReader#getMetadataStoreRoot() */
  @Override
  public Object getMetadataStoreRoot() {
    return getReader().getMetadataStoreRoot();
  }

  /* @see IFormatReader#getUnderlyingReaders() */
  @Override
  public IFormatReader[] getUnderlyingReaders() {
    return getReaders();
  }

  /* @see IFormatReader#isSingleFile(String) */
  @Override
  public boolean isSingleFile(String id) throws FormatException, IOException {
    return getReader(id).isSingleFile(id);
  }

  /* @see IFormatReader#getRequiredDirectories(String[]) */
  @Override
  public int getRequiredDirectories(String[] files)
    throws FormatException, IOException
  {
    return getReader(files[0]).getRequiredDirectories(files);
  }

  /* @see IFormatReader#getDatasetStructureDescription() */
  @Override
  public String getDatasetStructureDescription() {
    return getReader().getDatasetStructureDescription();
  }

  /* @see IFormatReader#hasCompanionFiles() */
  @Override
  public boolean hasCompanionFiles() {
    return getReader().hasCompanionFiles();
  }

  /* @see IFormatReader#getPossibleDomains(String) */
  @Override
  public String[] getPossibleDomains(String id)
    throws FormatException, IOException
  {
    return getReader(id).getPossibleDomains(id);
  }

  /* @see IFormatReader#getDomains() */
  @Override
  public String[] getDomains() {
    return getReader().getDomains();
  }

  /* @see IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    return getReader().getOptimalTileWidth();
  }

  /* @see IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    return getReader().getOptimalTileHeight();
  }

  /* @see IFormatReader#getCoreIndex() */
  @Override
  public int getCoreIndex() {
    return getReader().getCoreIndex();
  }

  /* @see IFormatReader#setCoreIndex(int) */
  @Override
  public void setCoreIndex(int no) {
    getReader().setCoreIndex(no);
  }

  @Override
  public int seriesToCoreIndex(int series) {
    return getReader().seriesToCoreIndex(series);
  }

  @Override
  public int coreIndexToSeries(int index) {
    return getReader().coreIndexToSeries(index);
  }

  /* @see IFormatReader#getResolutionCount() */
  @Override
  public int getResolutionCount() {
    return getReader().getResolutionCount();
  }

  /* @see IFormatReader#setResolution(int) */
  @Override
  public void setResolution(int no) {
    getReader().setResolution(no);
  }

  /* @see IFormatReader#getResolution() */
  @Override
  public int getResolution() {
    return getReader().getResolution();
  }

  /* @see IFormatReader#hasFlattenedResolutions() */
  @Override
  public boolean hasFlattenedResolutions() {
    // all readers should have the same flattened setting
    return readers[0].hasFlattenedResolutions();
  }

  /* @see IFormatReader#setFlattenedResolutions(boolean) */
  @Override
  public void setFlattenedResolutions(boolean flattened) {
    for (IFormatReader reader : readers) {
      reader.setFlattenedResolutions(flattened);
    }
  }

  // -- IFormatHandler API methods --

  /* @see IFormatHandler#isThisType(String) */
  @Override
  public boolean isThisType(String name) {
    // if necessary, open the file for further analysis
    // but check isThisType(name, false) first, for efficiency
    return isThisType(name, false) || isThisType(name, true);
  }

  /* @see IFormatHandler#getFormat() */
  @Override
  public String getFormat() { return getReader().getFormat(); }

  /* @see IFormatHandler#getSuffixes() */
  @Override
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
  @Override
  public Class<?> getNativeDataType() {
    return getReader().getNativeDataType();
  }

  /* @see IFormatHandler#setId(String) */
  @Override
  public void setId(String id) throws FormatException, IOException {
    IFormatReader currentReader = getReader(id);
    LOGGER.info("{} initializing {}",
      currentReader.getClass().getSimpleName(), id);
    currentReader.setId(id);
    if (validate) {
      setupService();
      try {
        String omexml = service.getOMEXML((MetadataRetrieve)currentReader.getMetadataStore());
        if (!XMLTools.validateXML(omexml)) {
          throw new FormatException("Invalid XML when retrieving OME-XML from OMEXMLMetadata object.");
        }
        if (!service.validateOMEXML(omexml)) {
          throw new FormatException("Invalid OME-XML when retrieving OME-XML from OMEXMLMetadata object.");
        }
      } catch (ServiceException e) {
        throw new FormatException("OMEXMLService unable to create OME-XML metadata object.", e);
      }
    }
  }

  /* @see IFormatReader#reopenFile() */
  @Override
  public void reopenFile() throws IOException {
    getReader().reopenFile();
  }

  /* @see IFormatHandler#close() */
  @Override
  public void close() throws IOException { close(false); }

  /* @see IFormatReader#setValidate(boolean) */
  @Override
  public void setValidate(boolean validateFile) {
    validate = validateFile;
  }

  /* @see IFormatReader#isValidate() */
  @Override
  public boolean isValidate() {
    return validate;
  }
  
  /** Initialize the OMEXMLService needed by {@link #setId(String)} */
  private void setupService() {
    try {
      if (factory == null) factory = new ServiceFactory();
      if (service == null) {
        service = factory.getInstance(OMEXMLService.class);
      }
    }
    catch (DependencyException e) {
      LOGGER.warn("OMEXMLService not available.", e);
    }
  }

}
