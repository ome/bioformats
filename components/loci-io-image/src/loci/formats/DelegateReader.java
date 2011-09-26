//
// DelegateReader.java
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

import java.io.IOException;
import java.util.Set;

import loci.common.RandomAccessInputStream;
import loci.formats.in.MetadataLevel;
import loci.formats.in.MetadataOptions;
import loci.formats.meta.MetadataStore;

/**
 * DelegateReader is a file format reader that selects which reader to use
 * for a format if there are two readers which handle the same format.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/DelegateReader.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/DelegateReader.java;hb=HEAD">Gitweb</a></dd></dl>
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
  public Set<MetadataLevel> getSupportedMetadataLevels() {
    return nativeReader.getSupportedMetadataLevels();
  }

  /* @see IMetadataConfigurable#getMetadataOptions() */
  public MetadataOptions getMetadataOptions() {
    return nativeReader.getMetadataOptions();
  }

  /* @see IMetadataConfigurable#setMetadataOptions(MetadataOptions) */
  public void setMetadataOptions(MetadataOptions options) {
    nativeReader.setMetadataOptions(options);
    legacyReader.setMetadataOptions(options);
  }

  // -- IFormatReader API methods --

  /* @see IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    return nativeReader.isThisType(name, open);
  }

  /* @see IFormatReader#isThisType(RandomAccessInputStream) */
  public boolean isThisType(RandomAccessInputStream stream) throws IOException {
    return nativeReader.isThisType(stream);
  }

  /* @see IFormatReader#setSeries(int) */
  public void setSeries(int no) {
    super.setSeries(no);
    if (nativeReaderInitialized) nativeReader.setSeries(no);
    if (legacyReaderInitialized) legacyReader.setSeries(no);
  }

  /* @see IFormatReader#setNormalized(boolean) */
  public void setNormalized(boolean normalize) {
    super.setNormalized(normalize);
    nativeReader.setNormalized(normalize);
    legacyReader.setNormalized(normalize);
  }

  /**
   * @deprecated
   * @see IFormatReader#setMetadataCollected(boolean)
   */
  public void setMetadataCollected(boolean collect) {
    super.setMetadataCollected(collect);
    nativeReader.setMetadataCollected(collect);
    legacyReader.setMetadataCollected(collect);
  }

  /* @see IFormatReader#setOriginalMetadataPopulated(boolean) */
  public void setOriginalMetadataPopulated(boolean populate) {
    super.setOriginalMetadataPopulated(populate);
    nativeReader.setOriginalMetadataPopulated(populate);
    legacyReader.setOriginalMetadataPopulated(populate);
  }

  /* @see IFormatReader#setGroupFiles(boolean) */
  public void setGroupFiles(boolean group) {
    super.setGroupFiles(group);
    nativeReader.setGroupFiles(group);
    legacyReader.setGroupFiles(group);
  }

  /* @see IFormatReader#setMetadataFiltered(boolean) */
  public void setMetadataFiltered(boolean filter) {
    super.setMetadataFiltered(filter);
    nativeReader.setMetadataFiltered(filter);
    legacyReader.setMetadataFiltered(filter);
  }

  /* @see IFormatReader#setMetadataStore(MetadataStore) */
  public void setMetadataStore(MetadataStore store) {
    super.setMetadataStore(store);
    nativeReader.setMetadataStore(store);
    legacyReader.setMetadataStore(store);
  }

  /* @see IFormatReader#get8BitLookupTable() */
  public byte[][] get8BitLookupTable() throws FormatException, IOException {
    if (useLegacy || (legacyReaderInitialized && !nativeReaderInitialized)) {
      return legacyReader.get8BitLookupTable();
    }
    return nativeReader.get8BitLookupTable();
  }

  /* @see IFormatReader#get16BitLookupTable() */
  public short[][] get16BitLookupTable() throws FormatException, IOException {
    if (useLegacy || (legacyReaderInitialized && !nativeReaderInitialized)) {
      return legacyReader.get16BitLookupTable();
    }
    return nativeReader.get16BitLookupTable();
  }

  /* @see IFormatReader#openBytes(int, byte[], int, int, int, int) */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    if (useLegacy || (legacyReaderInitialized && !nativeReaderInitialized)) {
      return legacyReader.openBytes(no, buf, x, y, w, h);
    }
    return nativeReader.openBytes(no, buf, x, y, w, h);
  }

  /* @see IFormatReader#close(boolean) */
  public void close(boolean fileOnly) throws IOException {
    super.close(fileOnly);
    if (nativeReader != null) nativeReader.close(fileOnly);
    if (legacyReader != null) legacyReader.close(fileOnly);
    if (!fileOnly) {
      nativeReaderInitialized = legacyReaderInitialized = false;
    }
  }

  /* @see IFormatReader#getOptimalTileWidth() */
  public int getOptimalTileWidth() {
    if (useLegacy || (legacyReaderInitialized && !nativeReaderInitialized)) {
      return legacyReader.getOptimalTileWidth();
    }
    return nativeReader.getOptimalTileWidth();
  }

  /* @see IFormatReader#getOptimalTileHeight() */
  public int getOptimalTileHeight() {
    if (useLegacy || (legacyReaderInitialized && !nativeReaderInitialized)) {
      return legacyReader.getOptimalTileHeight();
    }
    return nativeReader.getOptimalTileHeight();
  }

  // -- IFormatHandler API methods --

  /* @see IFormatHandler#setId(String) */
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
        LOGGER.debug("", exc);
        legacyReader.setId(id);
        legacyReaderInitialized = true;
      }
    }
    if (nativeReaderInitialized) {
      core = nativeReader.getCoreMetadata();
      metadata = nativeReader.getGlobalMetadata();
      metadataStore = nativeReader.getMetadataStore();
    }
    if (legacyReaderInitialized) {
      core = legacyReader.getCoreMetadata();
      metadata = legacyReader.getGlobalMetadata();
      metadataStore = legacyReader.getMetadataStore();
    }
  }

}
