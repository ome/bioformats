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

package loci.formats.out;

import java.io.IOException;
import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.FormatWriter;
import loci.formats.codec.CompressionType;
import loci.formats.codec.JPEG2000Codec;
import loci.formats.codec.JPEG2000CodecOptions;
import loci.formats.meta.MetadataRetrieve;

/**
 * JPEG2000Writer is the file format writer for JPEG2000 files.
 */
public class JPEG2000Writer extends FormatWriter {

  // -- Fields --

  // -- Constructor --

  /** Creates a new instance. */
  public JPEG2000Writer() {
    super("JPEG-2000", "jp2");
    compressionTypes = new String[] {CompressionType.J2K_LOSSY.getCompression(),
        CompressionType.J2K.getCompression()};
    //The default codec options
    options = JPEG2000CodecOptions.getDefaultOptions();
  }

  // -- IFormatWriter API methods --

  /**
   * @see loci.formats.IFormatWriter#saveBytes(int, byte[], int, int, int, int)
   */
  @Override
  public void saveBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    checkParams(no, buf, x, y, w, h);

    /*
    if (!isFullPlane(x, y, w, h)) {
      throw new FormatException(
        "JPEG2000Writer does not yet support saving image tiles.");
    }
    */
    //MetadataRetrieve retrieve = getMetadataRetrieve();
    //int width = retrieve.getPixelsSizeX(series).getValue().intValue();
    //int height = retrieve.getPixelsSizeY(series).getValue().intValue();

    out.write(compressBuffer(no, buf, x, y, w, h));
  }

  /**
   * Compresses the buffer.
   *
   * @param no the image index within the current file, starting from 0.
   * @param buf the byte array that represents the image tile.
   * @param x the X coordinate of the upper-left corner of the image tile.
   * @param y the Y coordinate of the upper-left corner of the image tile.
   * @param w the width (in pixels) of the image tile.
   * @param h the height (in pixels) of the image tile.
   * @throws FormatException if one of the parameters is invalid.
   * @throws IOException if there was a problem writing to the file.
   */
  public byte[] compressBuffer(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    checkParams(no, buf, x, y, w, h);
    MetadataRetrieve retrieve = getMetadataRetrieve();
    boolean littleEndian = false;
    if (retrieve.getPixelsBigEndian(series) != null) {
      littleEndian = !retrieve.getPixelsBigEndian(series).booleanValue();
    }
    else if (retrieve.getPixelsBinDataCount(series) == 0) {
      littleEndian = !retrieve.getPixelsBinDataBigEndian(series, 0).booleanValue();
    }
    int bytesPerPixel = FormatTools.getBytesPerPixel(
      FormatTools.pixelTypeFromString(
      retrieve.getPixelsType(series).toString()));
    int nChannels = getSamplesPerPixel();

    //To be on the save-side
    if (options == null) options = JPEG2000CodecOptions.getDefaultOptions();
    options = new JPEG2000CodecOptions(options);
    options.width = w;
    options.height = h;
    options.channels = nChannels;
    options.bitsPerSample = bytesPerPixel * 8;
    options.littleEndian = littleEndian;
    options.interleaved = interleaved;
    options.lossless = compression == null ||
    compression.equals(CompressionType.J2K.getCompression());
    options.colorModel = getColorModel();

    return new JPEG2000Codec().compress(buf, options);
  }

  /**
   * Overridden to indicate that stacks are not supported.
   * @see loci.formats.IFormatWriter#canDoStacks()
   */
  @Override
  public boolean canDoStacks() { return false; }

  /**
   * Overridden to return the formats supported by the writer.
   * @see loci.formats.IFormatWriter#getPixelTypes(String)
   */
  @Override
  public int[] getPixelTypes(String codec) {
    return new int[] {FormatTools.INT8, FormatTools.UINT8, FormatTools.INT16,
      FormatTools.UINT16, FormatTools.INT32, FormatTools.UINT32};
  }

}
