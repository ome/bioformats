//
// QTReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.in;

import java.awt.image.BufferedImage;
import java.io.IOException;
import loci.common.*;
import loci.formats.*;
import loci.formats.meta.MetadataStore;

/**
 * QTReader is the file format reader for QuickTime movie files.
 * It does not read files directly, but chooses which QuickTime reader is
 * more appropriate.
 *
 * @see NativeQTReader
 * @see LegacyQTReader
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/QTReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/QTReader.java">SVN</a></dd></dl>
 */
public class QTReader extends FormatReader {

  /** Flag indicating whether to use legacy reader by default. */
  private boolean useLegacy;

  /** Native QuickTime reader. */
  private NativeQTReader nativeReader;

  /** Legacy QuickTime reader - requires QT Java. */
  private LegacyQTReader legacyReader;

  /** Flag indicating that the native reader was successfully initialized. */
  private boolean nativeReaderInitialized;

  /** Flag indicating that the legacy reader was successfully initialized. */
  private boolean legacyReaderInitialized;

  // -- Constructor --

  /** Constructs a new QuickTime reader. */
  public QTReader() {
    super("QuickTime", "mov");
    nativeReader = new NativeQTReader();
    legacyReader = new LegacyQTReader();
    nativeReaderInitialized = false;
    legacyReaderInitialized = false;
  }

  // -- QTReader API methods --

  /** Sets whether to use the legacy reader (QTJava) by default. */
  public void setLegacy(boolean legacy) { useLegacy = legacy; }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#isThisType(String, boolean) */
  public boolean isThisType(String name, boolean open) {
    return nativeReader.isThisType(name, open);
  }

  /* @see loci.formats.IFormatReader#isThisType(RandomAccessStream) */
  public boolean isThisType(RandomAccessStream stream) throws IOException {
    return nativeReader.isThisType(stream);
  }

  /* @see loci.formats.IFormatReader#setSeries(int) */
  public void setSeries(int no) {
    super.setSeries(no);
    if (nativeReaderInitialized) nativeReader.setSeries(no);
    if (legacyReaderInitialized) legacyReader.setSeries(no);
  }

  /* @see loci.formats.IFormatReader#setNormalized(boolean) */
  public void setNormalized(boolean normalize) {
    super.setNormalized(normalize);
    nativeReader.setNormalized(normalize);
    legacyReader.setNormalized(normalize);
  }

  /* @see loci.formats.IFormatReader#setMetadataCollected(boolean) */
  public void setMetadataCollected(boolean collect) {
    super.setMetadataCollected(collect);
    nativeReader.setMetadataCollected(collect);
    legacyReader.setMetadataCollected(collect);
  }

  /* @see loci.formats.IFormatReader#setOriginalMetadataPopulated(boolean) */
  public void setOriginalMetadataPopulated(boolean populate) {
    super.setOriginalMetadataPopulated(populate);
    nativeReader.setOriginalMetadataPopulated(populate);
    legacyReader.setOriginalMetadataPopulated(populate);
  }

  /* @see loci.formats.IFormatReader#setGroupFiles(boolean) */
  public void setGroupFiles(boolean group) {
    super.setGroupFiles(group);
    nativeReader.setGroupFiles(group);
    legacyReader.setGroupFiles(group);
  }

  /* @see loci.formats.IFormatReader#setMetadataFiltered(boolean) */
  public void setMetadataFiltered(boolean filter) {
    super.setMetadataFiltered(filter);
    nativeReader.setMetadataFiltered(filter);
    legacyReader.setMetadataFiltered(filter);
  }

  /* @see loci.formats.IFormatReader#setMetadataStore(MetadataStore) */
  public void setMetadataStore(MetadataStore store) {
    super.setMetadataStore(store);
    nativeReader.setMetadataStore(store);
    legacyReader.setMetadataStore(store);
  }

  /**
   * @see loci.formats.IFormatReader#openImage(int, int, int, int, int)
   */
  public BufferedImage openImage(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    if (useLegacy || (legacyReaderInitialized && !nativeReaderInitialized)) {
      return legacyReader.openImage(no, x, y, w, h);
    }
    return nativeReader.openImage(no, x, y, w, h);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    if (useLegacy || (legacyReaderInitialized && !nativeReaderInitialized)) {
      return legacyReader.openBytes(no, x, y, w, h);
    }
    return nativeReader.openBytes(no, x, y, w, h);
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    if (nativeReader != null) nativeReader.close();
    if (legacyReader != null) legacyReader.close();
    nativeReaderInitialized = legacyReaderInitialized = false;
  }

  /* @see loci.formats.IFormatHandler#setId(String) */
  public void setId(String id) throws FormatException, IOException {
    super.setId(id);
    if (useLegacy) {
      try {
        legacyReader.setId(id);
        legacyReaderInitialized = true;
        core = legacyReader.getCoreMetadata();
      }
      catch (FormatException e) {
        if (debug) LogTools.trace(e);
        nativeReader.setId(id);
        nativeReaderInitialized = true;
        core = nativeReader.getCoreMetadata();
      }
    }
    else {
      Exception exc = null;
      try {
        nativeReader.setId(id);
        nativeReaderInitialized = true;
        core = nativeReader.getCoreMetadata();
      }
      catch (FormatException e) { exc = e; }
      catch (IOException e) { exc = e; }
      if (exc != null) {
        legacyReader.setId(id);
        legacyReaderInitialized = true;
        core = legacyReader.getCoreMetadata();
      }
    }
  }

}
