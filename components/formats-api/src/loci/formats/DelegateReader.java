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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import loci.common.RandomAccessInputStream;
import loci.formats.in.MetadataLevel;
import loci.formats.in.MetadataOptions;
import loci.formats.meta.MetadataStore;

/**
 * DelegateReader is a file format reader that selects which reader to use
 * for a format if there are two readers which handle the same format.
 */
public abstract class DelegateReader extends FormatReader {

  /** Flag indicating whether to use legacy reader by default. */
  protected boolean useLegacy;

  /** Native reader. */
  protected IFormatReader nativeReader;

  /** Legacy reader. */
  protected IFormatReader legacyReader;

  /** Flag indicating that the native reader was successfully initialized. */
  protected boolean nativeReaderInitialized;

  /** Flag indicating that the legacy reader was successfully initialized. */
  protected boolean legacyReaderInitialized;

  // -- Constructor --

  /** Constructs a new delegate reader. */
  public DelegateReader(String format, String suffix) {
    super(format, suffix);
  }

  /** Constructs a new delegate reader. */
  public DelegateReader(String format, String[] suffixes) {
    super(format, suffixes);
  }

  // -- DelegateReader API methods --

  /** Sets whether to use the legacy reader by default. */
  public void setLegacy(boolean legacy) { useLegacy = legacy; }

  /** Gets whether to use the legacy reader by default. */
  public boolean isLegacy() { return useLegacy; }

  // -- IMetadataConfigurable API methods --

  /* @see IMetadataConfigurable#getSupportedMetadataLevels() */
  @Override
  public Set<MetadataLevel> getSupportedMetadataLevels() {
    return nativeReader.getSupportedMetadataLevels();
  }

  /* @see IMetadataConfigurable#getMetadataOptions() */
  @Override
  public MetadataOptions getMetadataOptions() {
    return nativeReader.getMetadataOptions();
  }

  /* @see IMetadataConfigurable#setMetadataOptions(MetadataOptions) */
  @Override
  public void setMetadataOptions(MetadataOptions options) {
    nativeReader.setMetadataOptions(options);
    legacyReader.setMetadataOptions(options);
  }

  // -- IFormatReader API methods --

  /* @see IFormatReader#isThisType(String, boolean) */
  @Override
  public boolean isThisType(String name, boolean open) {
    return nativeReader.isThisType(name, open);
  }

  /* @see IFormatReader#isThisType(RandomAccessInputStream) */
  @Override
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    return nativeReader.isThisType(stream);
  }

  /* @see IFormatReader#setSeries(int) */
  @Override
  public void setSeries(int no) {
    super.setSeries(no);
    if (nativeReaderInitialized) nativeReader.setSeries(no);
    if (legacyReaderInitialized) legacyReader.setSeries(no);
  }

  /* @see IFormatReader#setCoreIndex(int) */
  @Override
  public void setCoreIndex(int no) {
    super.setCoreIndex(no);
    if (nativeReaderInitialized) nativeReader.setCoreIndex(no);
    if (legacyReaderInitialized) legacyReader.setCoreIndex(no);
  }

  /* @see IFormatReader#setResolution(int) */
  @Override
  public void setResolution(int resolution) {
    super.setResolution(resolution);
    if (nativeReaderInitialized) nativeReader.setResolution(resolution);
    if (legacyReaderInitialized) legacyReader.setResolution(resolution);
  }

  /* @see IFormatReader#setNormalized(boolean) */
  @Override
  public void setNormalized(boolean normalize) {
    super.setNormalized(normalize);
    nativeReader.setNormalized(normalize);
    legacyReader.setNormalized(normalize);
  }

  /* @see IFormatReader#setOriginalMetadataPopulated(boolean) */
  @Override
  public void setOriginalMetadataPopulated(boolean populate) {
    super.setOriginalMetadataPopulated(populate);
    nativeReader.setOriginalMetadataPopulated(populate);
    legacyReader.setOriginalMetadataPopulated(populate);
  }

  /* @see IFormatReader#setGroupFiles(boolean) */
  @Override
  public void setGroupFiles(boolean group) {
    super.setGroupFiles(group);
    nativeReader.setGroupFiles(group);
    legacyReader.setGroupFiles(group);
  }

  /* @see IFormatReader#setFlattenedResolutions(boolean) */
  @Override
  public void setFlattenedResolutions(boolean flattened) {
    super.setFlattenedResolutions(flattened);
    nativeReader.setFlattenedResolutions(flattened);
    legacyReader.setFlattenedResolutions(flattened);
  }

  /* @see IFormatReader#setMetadataFiltered(boolean) */
  @Override
  public void setMetadataFiltered(boolean filter) {
    super.setMetadataFiltered(filter);
    nativeReader.setMetadataFiltered(filter);
    legacyReader.setMetadataFiltered(filter);
  }

  /* @see IFormatReader#setMetadataStore(MetadataStore) */
  @Override
  public void setMetadataStore(MetadataStore store) {
    super.setMetadataStore(store);
    nativeReader.setMetadataStore(store);
    legacyReader.setMetadataStore(store);
  }

  /* @see IFormatReader#get8BitLookupTable() */
  @Override
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    if (useLegacy || (legacyReaderInitialized && !nativeReaderInitialized)) {
      return legacyReader.get8BitLookupTable();
    }
    return nativeReader.get8BitLookupTable();
  }

  /* @see IFormatReader#get16BitLookupTable() */
  @Override
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    if (useLegacy || (legacyReaderInitialized && !nativeReaderInitialized)) {
      return legacyReader.get16BitLookupTable();
    }
    return nativeReader.get16BitLookupTable();
  }

  /* @see IFormatReader#getSeriesUsedFiles(boolean) */
  @Override
  public String[] getSeriesUsedFiles(boolean noPixels) {
    if (useLegacy || (legacyReaderInitialized && !nativeReaderInitialized)) {
      return legacyReader.getSeriesUsedFiles(noPixels);
    }
    return nativeReader.getSeriesUsedFiles(noPixels);
  }

  /* @see IFormatReader#openBytes(int, byte[], int, int, int, int) */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    if (useLegacy || (legacyReaderInitialized && !nativeReaderInitialized)) {
      return legacyReader.openBytes(no, buf, x, y, w, h);
    }
    return nativeReader.openBytes(no, buf, x, y, w, h);
  }

  /* @see IFormatReader#close(boolean) */
  @Override
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (nativeReader != null) nativeReader.close(fileOnly);
    if (legacyReader != null) legacyReader.close(fileOnly);
    if (!fileOnly) {
      nativeReaderInitialized = legacyReaderInitialized = false;
    }
  }

  /* @see IFormatReader#getOptimalTileWidth() */
  @Override
  public int getOptimalTileWidth() {
    if (useLegacy || (legacyReaderInitialized && !nativeReaderInitialized)) {
      return legacyReader.getOptimalTileWidth();
    }
    return nativeReader.getOptimalTileWidth();
  }

  /* @see IFormatReader#getOptimalTileHeight() */
  @Override
  public int getOptimalTileHeight() {
    if (useLegacy || (legacyReaderInitialized && !nativeReaderInitialized)) {
      return legacyReader.getOptimalTileHeight();
    }
    return nativeReader.getOptimalTileHeight();
  }

  /* @see IFormatReader#reopenFile() */
  @Override
  public void reopenFile() throws IOException {
    if (useLegacy) {
      legacyReader.reopenFile();
    }
    else {
      nativeReader.reopenFile();
    }
  }

  // -- IFormatHandler API methods --

  /* @see IFormatHandler#setId(String) */
  @Override
  public void setId(String id) throws FormatException, IOException {
    super.setId(id);
    if (useLegacy) {
      try {
        legacyReader.setId(id);
        legacyReaderInitialized = true;
      }
      catch (FormatException e) {
        LOGGER.debug("", e);
        nativeReader.setId(id);
        nativeReaderInitialized = true;
      }
    }
    else {
      Exception exc = null;
      try {
        nativeReader.setId(id);
        nativeReaderInitialized = true;
      }
      catch (FormatException e) { exc = e; }
      catch (IOException e) { exc = e; }
      if (exc != null) {
        nativeReaderInitialized = false;
        LOGGER.info("", exc);
        legacyReader.setId(id);
        legacyReaderInitialized = true;
      }
      if (legacyReaderInitialized) {
        nativeReaderInitialized = false;
      }
    }
    if (nativeReaderInitialized) {
      core = new ArrayList<CoreMetadata>(nativeReader.getCoreMetadataList());
      metadata = nativeReader.getGlobalMetadata();
      metadataStore = nativeReader.getMetadataStore();
    }
    if (legacyReaderInitialized) {
      core = new ArrayList<CoreMetadata>(legacyReader.getCoreMetadataList());
      metadata = legacyReader.getGlobalMetadata();
      metadataStore = legacyReader.getMetadataStore();
    }
  }

}
