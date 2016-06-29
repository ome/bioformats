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

import java.awt.image.ColorModel;
import java.io.IOException;
import java.util.HashMap;

import ome.xml.model.primitives.PositiveInteger;

import loci.common.DataTools;
import loci.common.RandomAccessOutputStream;
import loci.common.Region;
import loci.formats.codec.CodecOptions;
import loci.formats.meta.DummyMetadata;
import loci.formats.meta.MetadataRetrieve;

/**
 * Abstract superclass of all biological file format writers.
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

  /** The options if required. */
  protected CodecOptions options;

  /**
   * Whether each plane in each series of the current file has been
   * prepped for writing.
   */
  protected boolean[][] initialized;

  /** Whether the channels in an RGB image are interleaved. */
  protected boolean interleaved;

  /** The number of valid bits per pixel. */
  protected int validBits;

  /** Current series. */
  protected int series;

  /** Whether or not we are writing planes sequentially. */
  protected boolean sequential;

  /**
   * Current metadata retrieval object. Should <b>never</b> be accessed
   * directly as the semantics of {@link #getMetadataRetrieve()}
   * prevent "null" access.
   */
  protected MetadataRetrieve metadataRetrieve = new DummyMetadata();

  /** Current file. */
  protected RandomAccessOutputStream out;

  // -- Constructors --

  /** Constructs a format writer with the given name and default suffix. */
  public FormatWriter(String format, String suffix) { super(format, suffix); }

  /** Constructs a format writer with the given name and default suffixes. */
  public FormatWriter(String format, String[] suffixes) {
    super(format, suffixes);
  }

  // -- IFormatWriter API methods --

  /* @see IFormatWriter#changeOutputFile(String) */
  @Override
  public void changeOutputFile(String id) throws FormatException, IOException {
    setId(id);
  }

  /* @see IFormatWriter#saveBytes(int, byte[]) */
  @Override
  public void saveBytes(int no, byte[] buf) throws FormatException, IOException
  {
    int width = metadataRetrieve.getPixelsSizeX(getSeries()).getValue();
    int height = metadataRetrieve.getPixelsSizeY(getSeries()).getValue();
    saveBytes(no, buf, 0, 0, width, height);
  }

  /* @see IFormatWriter#saveBytes(int, byte[], Region) */
  @Override
  public void saveBytes(int no, byte[] buf, Region tile)
    throws FormatException, IOException
  {
    saveBytes(no, buf, tile.x, tile.y, tile.width, tile.height);
  }

  /* @see IFormatWriter#savePlane(int, Object) */
  @Override
  public void savePlane(int no, Object plane)
    throws FormatException, IOException
  {
    int width = metadataRetrieve.getPixelsSizeX(getSeries()).getValue();
    int height = metadataRetrieve.getPixelsSizeY(getSeries()).getValue();
    savePlane(no, plane, 0, 0, width, height);
  }

  /* @see IFormatWriter#savePlane(int, Object, int, int, int, int) */
  @Override
  public void savePlane(int no, Object plane, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    // NB: Writers use byte arrays by default as the native type.
    if (!(plane instanceof byte[])) {
      throw new IllegalArgumentException("Object to save must be a byte[]");
    }
    saveBytes(no, (byte[]) plane, x, y, w, h);
  }

  /* @see IFormatWriter#savePlane(int, Object, Region) */
  @Override
  public void savePlane(int no, Object plane, Region tile)
    throws FormatException, IOException
  {
    savePlane(no, plane, tile.x, tile.y, tile.width, tile.height);
  }

  /* @see IFormatWriter#setSeries(int) */
  @Override
  public void setSeries(int series) throws FormatException {
    if (series < 0) throw new FormatException("Series must be > 0.");
    if (series >= metadataRetrieve.getImageCount()) {
      throw new FormatException("Series is '" + series +
        "' but MetadataRetrieve only defines " +
        metadataRetrieve.getImageCount() + " series.");
    }
    this.series = series;
  }

  /* @see IFormatWriter#getSeries() */
  @Override
  public int getSeries() {
    return series;
  }

  /* @see IFormatWriter#setInterleaved(boolean) */
  @Override
  public void setInterleaved(boolean interleaved) {
    this.interleaved = interleaved;
  }

  /* @see IFormatWriter#isInterleaved() */
  @Override
  public boolean isInterleaved() {
    return interleaved;
  }

  /* @see IFormatWriter#setValidBitsPerPixel(int) */
  @Override
  public void setValidBitsPerPixel(int bits) {
    validBits = bits;
  }

  /* @see IFormatWriter#canDoStacks() */
  @Override
  public boolean canDoStacks() { return false; }

  /* @see IFormatWriter#setMetadataRetrieve(MetadataRetrieve) */
  @Override
  public void setMetadataRetrieve(MetadataRetrieve retrieve) {
    FormatTools.assertId(currentId, false, 1);
    if (retrieve == null) {
      throw new IllegalArgumentException("Metadata object is null");
    }
    metadataRetrieve = retrieve;
  }

  /* @see IFormatWriter#getMetadataRetrieve() */
  @Override
  public MetadataRetrieve getMetadataRetrieve() {
    return metadataRetrieve;
  }

  /* @see IFormatWriter#setColorModel(ColorModel) */
  @Override
  public void setColorModel(ColorModel model) { cm = model; }

  /* @see IFormatWriter#getColorModel() */
  @Override
  public ColorModel getColorModel() { return cm; }

  /* @see IFormatWriter#setFramesPerSecond(int) */
  @Override
  public void setFramesPerSecond(int rate) { fps = rate; }

  /* @see IFormatWriter#getFramesPerSecond() */
  @Override
  public int getFramesPerSecond() { return fps; }

  /* @see IFormatWriter#getCompressionTypes() */
  @Override
  public String[] getCompressionTypes() { return compressionTypes; }

  /* @see IFormatWriter#setCompression(compress) */
  @Override
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

  /* @see IFormatWriter#setCodecOptions(CodecOptions) */
  @Override
  public void setCodecOptions(CodecOptions options) {
    this.options = options;
  }

  /* @see IFormatWriter#getCompression() */
  @Override
  public String getCompression() {
    return compression;
  }

  /* @see IFormatWriter#getPixelTypes() */
  @Override
  public int[] getPixelTypes() {
    return getPixelTypes(getCompression());
  }

  /* @see IFormatWriter#getPixelTypes(String) */
  @Override
  public int[] getPixelTypes(String codec) {
    return new int[] {FormatTools.INT8, FormatTools.UINT8, FormatTools.INT16,
      FormatTools.UINT16, FormatTools.INT32, FormatTools.UINT32,
      FormatTools.FLOAT};
  }

  /* @see IFormatWriter#isSupportedType(int) */
  @Override
  public boolean isSupportedType(int type) {
    int[] types = getPixelTypes();
    for (int i=0; i<types.length; i++) {
      if (type == types[i]) return true;
    }
    return false;
  }

  /* @see IFormatWriter#setWriteSequentially(boolean) */
  @Override
  public void setWriteSequentially(boolean sequential) {
    this.sequential = sequential;
  }

  // -- IFormatHandler API methods --

  /**
   * Initializes a writer from the input file name.
   *
   * Initializes a {@link RandomAccessOutputStream} for the output
   * file and initializes the metadata for all the series using
   * {@link #setSeries(int)}.
   *
   *  @param id a {@link String} specifying the path to the file
   */
  @Override
  public void setId(String id) throws FormatException, IOException {
    if (id.equals(currentId)) return;
    currentId = id;
    if (out != null) {
      out.close();
    }
    out = new RandomAccessOutputStream(currentId);

    MetadataRetrieve r = getMetadataRetrieve();
    initialized = new boolean[r.getImageCount()][];
    for (int i=0; i<r.getImageCount(); i++) {
      initialized[i] = new boolean[getPlaneCount(i)];
    }
  }

  /* @see IFormatHandler#close() */
  @Override
  public void close() throws IOException {
    if (out != null) out.close();
    out = null;
    currentId = null;
    initialized = null;
  }

  // -- Helper methods --

  /**
   * Ensure that the arguments that are being passed to saveBytes(...) are
   * valid.
   * @throws FormatException if any of the arguments is invalid.
   */
  protected void checkParams(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException
  {
    MetadataRetrieve r = getMetadataRetrieve();
    MetadataTools.verifyMinimumPopulated(r, series);

    if (buf == null) throw new FormatException("Buffer cannot be null.");
    int z = r.getPixelsSizeZ(series).getValue().intValue();
    int t = r.getPixelsSizeT(series).getValue().intValue();
    int c = r.getChannelCount(series);
    int planes = z * c * t;

    if (no < 0) throw new FormatException(String.format(
        "Plane index:%d must be >= 0", no));
    if (no >= planes) {
      throw new FormatException(String.format(
          "Plane index:%d must be < %d", no, planes));
    }

    int sizeX = r.getPixelsSizeX(series).getValue().intValue();
    int sizeY = r.getPixelsSizeY(series).getValue().intValue();
    if (x < 0) throw new FormatException(String.format("X:%d must be >= 0", x));
    if (y < 0) throw new FormatException(String.format("Y:%d must be >= 0", y));
    if (x >= sizeX) {
      throw new FormatException(String.format(
          "X:%d must be < %d", x, sizeX));
    }
    if (y >= sizeY) {
      throw new FormatException(String.format("Y:%d must be < %d", y, sizeY));
    }
    if (w <= 0) throw new FormatException(String.format(
        "Width:%d must be > 0", w));
    if (h <= 0) throw new FormatException(String.format(
        "Height:%d must be > 0", h));
    if (x + w > sizeX) throw new FormatException(String.format(
        "(w:%d + x:%d) must be <= %d", w, x, sizeX));
    if (y + h > sizeY) throw new FormatException(String.format(
        "(h:%d + y:%d) must be <= %d", h, y, sizeY));

    int pixelType =
      FormatTools.pixelTypeFromString(r.getPixelsType(series).toString());
    int bpp = FormatTools.getBytesPerPixel(pixelType);
    PositiveInteger samples = r.getChannelSamplesPerPixel(series, 0);
    if (samples == null) samples = new PositiveInteger(1);
    int minSize = bpp * w * h * samples.getValue();
    if (buf.length < minSize) {
      throw new FormatException("Buffer is too small; expected " + minSize +
        " bytes, got " + buf.length + " bytes.");
    }

    if (!DataTools.containsValue(getPixelTypes(compression), pixelType)) {
      throw new FormatException("Unsupported image type '" +
        FormatTools.getPixelTypeString(pixelType) + "'.");
    }
  }

  /**
   * Seek to the given (x, y) coordinate of the image that starts at
   * the given offset.
   */
  protected void seekToPlaneOffset(long baseOffset, int x, int y)
    throws IOException
  {
    out.seek(baseOffset);

    MetadataRetrieve r = getMetadataRetrieve();
    int samples = getSamplesPerPixel();
    int pixelType =
      FormatTools.pixelTypeFromString(r.getPixelsType(series).toString());
    int bpp = FormatTools.getBytesPerPixel(pixelType);

    if (interleaved) bpp *= samples;

    int sizeX = r.getPixelsSizeX(series).getValue().intValue();

    out.skipBytes(bpp * (y * sizeX + x));
  }

  /**
   * Returns true if the given rectangle coordinates correspond to a full
   * image in the given series.
   */
  protected boolean isFullPlane(int x, int y, int w, int h) {
    MetadataRetrieve r = getMetadataRetrieve();
    int sizeX = r.getPixelsSizeX(series).getValue().intValue();
    int sizeY = r.getPixelsSizeY(series).getValue().intValue();
    return x == 0 && y == 0 && w == sizeX && h == sizeY;
  }

  /** Retrieve the number of samples per pixel for the current series. */
  protected int getSamplesPerPixel() {
    return getSamplesPerPixel(series);
  }
  
  /** Retrieve the number of samples per pixel for given series. */
  protected int getSamplesPerPixel(int series) {
    MetadataRetrieve r = getMetadataRetrieve();
    PositiveInteger samples = r.getChannelSamplesPerPixel(series, 0);
    if (samples == null) {
      LOGGER.warn("SamplesPerPixel #0 is null. It is assumed to be 1.");
    }
    return samples == null ? 1 : samples.getValue();
  }
  
  /** Retrieve the total number of planes in the current series. */
  protected int getPlaneCount() {
    return getPlaneCount(series);
  }
  
  /** Retrieve the total number of planes in given series. */
  protected int getPlaneCount(int series) {
    MetadataRetrieve r = getMetadataRetrieve();
    int z = r.getPixelsSizeZ(series).getValue().intValue();
    int t = r.getPixelsSizeT(series).getValue().intValue();
    int c = r.getPixelsSizeC(series).getValue().intValue();
    c /= r.getChannelSamplesPerPixel(series, 0).getValue().intValue();
    return z * c * t;
  }

}
