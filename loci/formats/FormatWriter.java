//
// FormatWriter.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats;

import java.awt.Image;
import java.awt.image.*;
import java.io.IOException;

/**
 * Abstract superclass of all biological file format writers.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/FormatWriter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/FormatWriter.java">SVN</a></dd></dl>
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

  /* @see IFormatWriter#saveBytes(byte[], int, boolean, boolean) */
  public void saveBytes(byte[] bytes, int series, boolean lastInSeries,
    boolean last) throws FormatException, IOException
  {
    FormatTools.assertId(currentId, true, 1);
    MetadataRetrieve r = getMetadataRetrieve();
    if (r == null) throw new FormatException("MetadataRetrieve cannot be null");
    Integer ss = new Integer(series);
    int width = r.getSizeX(ss).intValue();
    int height = r.getSizeY(ss).intValue();
    int channels = r.getSizeC(ss).intValue();
    int type = FormatTools.pixelTypeFromString(r.getPixelType(ss));
    boolean littleEndian = !r.getBigEndian(ss).booleanValue();

    BufferedImage img = ImageTools.makeImage(bytes, width, height, channels,
      true, FormatTools.getBytesPerPixel(type), littleEndian);
    saveImage(img, series, lastInSeries, last);
  }

  /* @see IFormatWriter#saveImage(Image, int, boolean, boolean) */
  public void saveImage(Image image, int series, boolean lastInSeries,
    boolean last) throws FormatException, IOException
  {
    throw new FormatException("Not implemented yet.");
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

  /* @see IFormatWriter#getPixelTypes() */
  public int[] getPixelTypes() {
    return new int[] {FormatTools.UINT8, FormatTools.UINT16,
      FormatTools.UINT32, FormatTools.FLOAT};
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

  /* @see IFormatHandler#setId(String, boolean) */
  public void setId(String id, boolean force)
    throws FormatException, IOException
  {
    if (id.equals(currentId) && !force) return;
    close();
    currentId = id;
    initialized = false;
  }

  // -- Deprecated IFormatWriter API methods --

  /** @deprecated Replaced by {@link #canDoStacks()} */
  public boolean canDoStacks(String id) throws FormatException {
    try {
      setId(id);
    }
    catch (IOException exc) {
      // NB: should never happen
      throw new FormatException(exc);
    }
    return canDoStacks(id);
  }

  /** @deprecated Replaced by {@link #getPixelTypes()} */
  public int[] getPixelTypes(String id) throws FormatException, IOException {
    setId(id);
    return getPixelTypes(id);
  }

  /** @deprecated Replaced by {@link #isSupportedType(int type)} */
  public boolean isSupportedType(String id, int type)
    throws FormatException, IOException
  {
    setId(id);
    return isSupportedType(type);
  }

  /** @deprecated Replaced by {@link #saveImage(Image, boolean)} */
  public void save(String id, Image image, boolean last)
    throws FormatException, IOException
  {
    setId(id);
    saveImage(image, last);
  }

}
