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
import loci.formats.IFormatReader;
import loci.formats.ReaderWrapper;

/**
 * A reader wrapper for reading image planes as BufferedImage objects.
 */
public class BufferedImageReader extends ReaderWrapper {

  // -- Utility methods --

  /**
   * Converts the given reader into a BufferedImageReader, wrapping if needed.
   */
  public static BufferedImageReader makeBufferedImageReader(IFormatReader r) {
    if (r instanceof BufferedImageReader) return (BufferedImageReader) r;
    return new BufferedImageReader(r);
  }

  // -- Constructors --

  /** Constructs a BufferedImageReader around a new image reader. */
  public BufferedImageReader() { super(); }

  /** Constructs a BufferedImageReader with the given reader. */
  public BufferedImageReader(IFormatReader r) { super(r); }

  // -- BufferedImageReader methods --

  /** Obtains the specified image from the current file. */
  public BufferedImage openImage(int no) throws FormatException, IOException {
    return openImage(no, 0, 0, getSizeX(), getSizeY());
  }

  /**
   * Obtains a sub-image of the specified image, whose upper-left corner is
   * given by (x, y).
   */
  public BufferedImage openImage(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    Class<?> dataType = getNativeDataType();
    if (BufferedImage.class.isAssignableFrom(dataType)) {
      // native data type is compatible with BufferedImage
      return (BufferedImage) openPlane(no, x, y, w, h);
    }
    else {
      // must construct BufferedImage from byte array
      return AWTImageTools.openImage(openBytes(no, x, y, w, h), this, w, h);
    }
  }

  /** Obtains a thumbnail for the specified image from the current file. */
  public BufferedImage openThumbImage(int no)
    throws FormatException, IOException
  {
    Class<?> dataType = getNativeDataType();
    if (BufferedImage.class.isAssignableFrom(dataType)) {
      BufferedImage img = AWTImageTools.makeUnsigned(openImage(no));
      return AWTImageTools.scale(img, getThumbSizeX(), getThumbSizeY(), false);
    }

    byte[] thumbBytes = openThumbBytes(no);
    return AWTImageTools.openImage(thumbBytes, this,
      getThumbSizeX(), getThumbSizeY());
  }

}
