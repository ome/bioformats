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

package loci.formats.gui;

import java.awt.image.BufferedImage;
import java.io.IOException;

import loci.formats.FormatException;
import loci.formats.FormatTools;
import loci.formats.IFormatWriter;
import loci.formats.WriterWrapper;
import loci.formats.meta.MetadataRetrieve;

/**
 * A writer wrapper for writing image planes from BufferedImage objects.
 */
public class BufferedImageWriter extends WriterWrapper {

  // -- Utility methods --

  /**
   * Converts the given writer into a BufferedImageWriter, wrapping if needed.
   */
  public static BufferedImageWriter makeBufferedImageWriter(IFormatWriter w) {
    if (w instanceof BufferedImageWriter) return (BufferedImageWriter) w;
    return new BufferedImageWriter(w);
  }

  // -- Constructors --

  /** Constructs a BufferedImageWriter around a new image writer. */
  public BufferedImageWriter() { super(); }

  /** Constructs a BufferedImageWriter with the given writer. */
  public BufferedImageWriter(IFormatWriter r) { super(r); }

  // -- BufferedImageWriter methods --

  /**
   * Saves the given BufferedImage to the current file.
   *
   * @param no the image index within the current file, starting from 0.
   * @param image the BufferedImage to save.
   */
  public void saveImage(int no, BufferedImage image)
    throws FormatException, IOException
  {
    saveImage(no, image, 0, 0, image.getWidth(), image.getHeight());
  }

  /**
   * Saves the given BufferedImage to the current file.  The BufferedImage
   * may represent a subsection of the full image to be saved.
   *
   * @param no the image index within the current file, starting from 0.
   * @param image the BufferedImage to save.
   * @param x the X coordinate of the upper-left corner of the image.
   * @param y the Y coordinate of the upper-left corner of the image.
   * @param w the width (in pixels) of the image.
   * @param h the height (in pixels) of the image.
   */
  public void saveImage(int no, BufferedImage image, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    Class<?> dataType = getNativeDataType();
    if (BufferedImage.class.isAssignableFrom(dataType)) {
      // native data type is compatible with BufferedImage
      savePlane(no, image, x, y, w, h);
    }
    else {
      // must convert BufferedImage to byte array
      byte[] buf = toBytes(image, this);

      saveBytes(no, buf, x, y, w, h);
    }
  }

  // -- Utility methods --

  public static byte[] toBytes(BufferedImage image, IFormatWriter writer) {
    boolean littleEndian = false;
    int bpp = FormatTools.getBytesPerPixel(AWTImageTools.getPixelType(image));

    MetadataRetrieve r = writer.getMetadataRetrieve();
    if (r != null) {
      Boolean bigEndian = false;
      if (r.getPixelsBigEndian(writer.getSeries()) != null)
      {
        bigEndian = r.getPixelsBigEndian(writer.getSeries()).booleanValue();
      }
      else if (r.getPixelsBinDataCount(writer.getSeries()) == 0) {
        bigEndian = r.getPixelsBinDataBigEndian(writer.getSeries(), 0).booleanValue();
      }
      if (bigEndian != null) littleEndian = !bigEndian.booleanValue();
    }

    byte[][] pixelBytes = AWTImageTools.getPixelBytes(image, littleEndian);
    byte[] buf = new byte[pixelBytes.length * pixelBytes[0].length];
    if (writer.isInterleaved()) {
      for (int i=0; i<pixelBytes[0].length; i+=bpp) {
        for (int j=0; j<pixelBytes.length; j++) {
          System.arraycopy(pixelBytes[j], i, buf,
            i * pixelBytes.length + j * bpp, bpp);
        }
      }
    }
    else {
      for (int i=0; i<pixelBytes.length; i++) {
        System.arraycopy(pixelBytes[i], 0, buf,
          i * pixelBytes[0].length, pixelBytes[i].length);
      }
    }
    pixelBytes = null;
    return buf;
  }

}
