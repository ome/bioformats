//
// FormatWriter.java
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

import java.awt.image.ColorModel;
import java.io.IOException;

import loci.formats.meta.DummyMetadata;
import loci.formats.meta.MetadataRetrieve;

/**
 * Abstract superclass of all biological file format writers.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/FormatWriter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/FormatWriter.java">SVN</a></dd></dl>
 */
public abstract class FormatWriter extends FormatHandler
  implements IFormatWriter
{

  // -- Fields --

  /** Frame rate to use when writing in frames per second, if applicable. */
  protected int fps = 10;

  /** Default color model. */
  protected ColorModel cm;

  /** Available compression types. */
  protected String[] compressionTypes;

  /** Current compression type. */
  protected String compression;

  /** Whether the current file has been prepped for writing. */
  protected boolean initialized;

  /** Whether the channels in an RGB image are interleaved. */
  protected boolean interleaved;

  /**
   * Current metadata retrieval object. Should <b>never</b> be accessed
   * directly as the semantics of {@link #getMetadataRetrieve()}
   * prevent "null" access.
   */
  protected MetadataRetrieve metadataRetrieve = new DummyMetadata();

  // -- Constructors --

  /** Constructs a format writer with the given name and default suffix. */
  public FormatWriter(String format, String suffix) { super(format, suffix); }

  /** Constructs a format writer with the given name and default suffixes. */
  public FormatWriter(String format, String[] suffixes) {
    super(format, suffixes);
  }

  // -- IFormatWriter API methods --

  /* @see IFormatWriter#saveBytes(byte[], boolean) */
  public void saveBytes(byte[] bytes, boolean last)
    throws FormatException, IOException
  {
    saveBytes(bytes, 0, last, last);
  }

  /* @see IFormatWriter#savePlane(Object, boolean) */
  public void savePlane(Object plane, boolean last)
    throws FormatException, IOException
  {
    savePlane(plane, 0, last, last);
  }

  /* @see IFormatWriter#savePlane(Object, int, boolean, boolean) */
  public void savePlane(Object plane, int series, boolean lastInSeries,
    boolean last) throws FormatException, IOException
  {
    // NB: Writers use byte arrays by default as the native type.
    if (!(plane instanceof byte[])) {
      throw new IllegalArgumentException("Object to save must be a byte[]");
    }
    saveBytes((byte[]) plane, series, lastInSeries, last);
  }

  /* @see IFormatWriter#setInterleaved(boolean) */
  public void setInterleaved(boolean interleaved) {
    this.interleaved = interleaved;
  }

  /* @see IFormatWriter#isInterleaved() */
  public boolean isInterleaved() {
    return interleaved;
  }

  /* @see IFormatWriter#canDoStacks() */
  public boolean canDoStacks() { return false; }

  /* @see IFormatWriter#setMetadataRetrieve(MetadataRetrieve) */
  public void setMetadataRetrieve(MetadataRetrieve retrieve) {
    FormatTools.assertId(currentId, false, 1);
    if (retrieve == null) {
      throw new IllegalArgumentException("Metadata object is null");
    }
    metadataRetrieve = retrieve;
  }

  /* @see IFormatWriter#getMetadataRetrieve() */
  public MetadataRetrieve getMetadataRetrieve() {
    return metadataRetrieve;
  }

  /* @see IFormatWriter#setColorModel(ColorModel) */
  public void setColorModel(ColorModel model) { cm = model; }

  /* @see IFormatWriter#getColorModel() */
  public ColorModel getColorModel() { return cm; }

  /* @see IFormatWriter#setFramesPerSecond(int) */
  public void setFramesPerSecond(int rate) { fps = rate; }

  /* @see IFormatWriter#getFramesPerSecond() */
  public int getFramesPerSecond() { return fps; }

  /* @see IFormatWriter#getCompressionTypes() */
  public String[] getCompressionTypes() { return compressionTypes; }

  /* @see IFormatWriter#setCompression(compress) */
  public void setCompression(String compress) throws FormatException {
    // check that this is a valid type
    for (int i=0; i<compressionTypes.length; i++) {
      if (compressionTypes[i].equals(compress)) {
        compression = compress;
        return;
      }
    }
    throw new FormatException("Invalid compression type: " + compress);
  }

  /* @see IFormatWriter#getCompression() */
  public String getCompression() {
    return compression;
  }

  /* @see IFormatWriter#getPixelTypes() */
  public int[] getPixelTypes() {
    return new int[] {FormatTools.INT8, FormatTools.UINT8, FormatTools.INT16,
      FormatTools.UINT16, FormatTools.INT32, FormatTools.UINT32,
      FormatTools.FLOAT};
  }

  /* @see IFormatWriter#getPixelTypes(String) */
  public int[] getPixelTypes(String codec) {
    return getPixelTypes();
  }

  /* @see IFormatWriter#isSupportedType(int) */
  public boolean isSupportedType(int type) {
    int[] types = getPixelTypes();
    for (int i=0; i<types.length; i++) {
      if (type == types[i]) return true;
    }
    return false;
  }

  // -- IFormatHandler API methods --

  /* @see IFormatHandler#setId(String) */
  public void setId(String id) throws FormatException, IOException {
    if (id.equals(currentId)) return;
    close();
    currentId = id;
    initialized = false;
  }

}
