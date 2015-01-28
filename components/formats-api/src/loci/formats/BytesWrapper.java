/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2015 Open Microscopy Environment:
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

import loci.common.DataTools;

/**
 * A {@link ReaderWrapper} helper which intercepts all
 * openBytes-like methods to ease subclassing. The primary
 * difficulty is that internally the various methods call one
 * another. If one method is not intercepted, then the raw
 * implementation might be accessed. The implementations here
 * are copied from {@link FormatReader} as the base for most
 * classes.
 */
public abstract class BytesWrapper extends ReaderWrapper
{

  // -- Constants --

  // -- Fields --

  // -- Constructors --

  public BytesWrapper() {
      super();
  }

  public BytesWrapper(IFormatReader reader) {
    super(reader);
  }

  // -- Internal FormatReader API methods --

  /* @see IFormatReader#openBytes(int) */
  @Override
  public byte[] openBytes(int no) throws FormatException, IOException {
    return openBytes(no, 0, 0, getSizeX(), getSizeY());
  }

  /* @see IFormatReader#openBytes(int, byte[]) */
  @Override
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    return openBytes(no, buf, 0, 0, getSizeX(), getSizeY());
  }

  /* @see IFormatReader#openBytes(int, int, int, int, int) */
  @Override
  public byte[] openBytes(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    int ch = getRGBChannelCount();
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    byte[] newBuffer;
    try {
      newBuffer = DataTools.allocate(w, h, ch, bpp);
    }
    catch (IllegalArgumentException e) {
      throw new FormatException("Image plane too large. Only 2GB of data can " +
        "be extracted at one time. You can workaround the problem by opening " +
        "the plane in tiles; for further details, see: " +
        "http://www.openmicroscopy.org/site/support/faq/bio-formats/" +
        "i-see-an-outofmemory-or-negativearraysize-error-message-when-" +
        "attempting-to-open-an-svs-or-jpeg-2000-file.-what-does-this-mean", e);
    }
    return openBytes(no, newBuffer, x, y, w, h);
  }

  /* @see IFormatReader#openBytes(int, byte[], int, int, int, int) */
  @Override
  public abstract byte[] openBytes(int no, byte[] buf, int x, int y,
    int w, int h) throws FormatException, IOException;

  /* @see IFormatReader#openPlane(int, int, int, int, int int) */
  @Override
  public Object openPlane(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    // NB: Readers use byte arrays by default as the native type.
    return openBytes(no, x, y, w, h);
  }

  /* @see IFormatReader#openThumbBytes(int) */
  @Override
  public byte[] openThumbBytes(int no) throws FormatException, IOException {
    // openThumbBytes delegates to the openBytes method of the passed reader.
    return FormatTools.openThumbBytes(this, no);
  }

}
