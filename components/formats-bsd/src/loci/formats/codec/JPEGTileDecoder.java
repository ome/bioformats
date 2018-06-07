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

package loci.formats.codec;

import loci.common.RandomAccessInputStream;

/**
 */
public class JPEGTileDecoder implements AutoCloseable {
  private ome.codecs.JPEGTileDecoder decoder;

  public JPEGTileDecoder() {
    decoder = new ome.codecs.JPEGTileDecoder();
  }

  // -- JPEGTileDecoder API methods --

  public void initialize(String id, int imageWidth) {
    this.decoder.initialize(id, imageWidth);
  }

  public void initialize(RandomAccessInputStream in, int imageWidth) {
    this.decoder.initialize(in, imageWidth);
  }

  public void initialize(RandomAccessInputStream in, int y, int h,
    int imageWidth)
  {
    this.decoder.initialize(in, y, h, imageWidth);
  }

  /**
    * Pre-process the stream to make sure that the
    * image width and height are non-zero.  Returns an array containing
    * the image width and height.
    */
  public int[] preprocess(RandomAccessInputStream in) {
    return this.decoder.preprocess(in);
  }

  public byte[] getScanline(int y) {
    return this.decoder.getScanline(y);
  }

  public int getWidth() {
    return this.decoder.getWidth();
  }

  public int getHeight() {
    return this.decoder.getHeight();
  }

  public void close() {
    this.decoder.close();
  }

  ome.codecs.JPEGTileDecoder getWrapped() {
    return this.decoder;
  }

}
