/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2016 - 2017 Open Microscopy Environment:
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

import java.io.IOException;

import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.MissingLibraryException;
import loci.formats.UnsupportedCompressionException;
import loci.formats.services.JPEGXRService;

/**
 */
public class JPEGXRCodec extends BaseCodec {

  // -- Fields --

  private JPEGXRService service;

  // -- Codec API methods --

  /* @see Codec#compress(byte[], CodecOptions) */
  @Override
  public byte[] compress(byte[] data, CodecOptions options)
    throws FormatException
  {
    throw new UnsupportedCompressionException(
      "JPEG-XR compression not supported");
  }

  /* @see Codec#decompress(RandomAccessInputStream, CodecOptions) */
  @Override
  public byte[] decompress(RandomAccessInputStream in, CodecOptions options)
    throws FormatException, IOException
  {
    byte[] buf = new byte[(int) in.length()];
    in.read(buf);
    return decompress(buf, options);
  }

  /**
   * The CodecOptions parameter should have the following fields set:
   *  {@link CodecOptions#maxBytes maxBytes}
   *  {@link CodecOptions#interleaved interleaved}
   *  {@link CodecOptions#bitsPerSample bitsPerSample}
   *  {@link CodecOptions#littleEndian littleEndian}
   *  {@link CodecOptions#width width}
   *  {@link CodecOptions#height height}
   *
   * @see Codec#decompress(byte[], CodecOptions)
   */
  @Override
  public byte[] decompress(byte[] buf, CodecOptions options)
    throws FormatException
  {
    initialize();

    boolean bgr = service.isBGR(buf);
    byte[] uncompressed = service.decompress(buf);
    int bpp = options.bitsPerSample / 8;
    int pixels = options.width * options.height;
    int channels = uncompressed.length / (pixels * bpp);

    if (channels == 1) {
      return uncompressed;
    }

    if (options.interleaved) {
      if (bgr && channels >= 3) {
        for (int p=0; p<uncompressed.length; p+=channels*bpp) {
          for (int b=0; b<bpp; b++) {
            int index = p + b;
            byte tmp = uncompressed[index];
            uncompressed[index] = uncompressed[index + 2 * bpp];
            uncompressed[index + 2 * bpp] = tmp;
          }
        }
      }
      return uncompressed;
    }

    byte[] deinterleaved = new byte[uncompressed.length];

    for (int p=0; p<pixels; p++) {
      for (int c=0; c<channels; c++) {
        for (int b=0; b<bpp; b++) {
          int bb = options.littleEndian ? b : bpp - b - 1;
          int src = bpp * (p * channels + c) + b;
          int dest = bpp * (c * pixels + p) + bb;

          if (bgr) {
            int swapChannel = c == 2 ? 0 : c == 0 ? 2 : c;
            src = bpp * (p * channels + swapChannel) + b;
          }

          deinterleaved[dest] = uncompressed[src];
        }
      }
    }

    return deinterleaved;
  }

  // -- Helper methods --

  /**
   * Initializes the JPEG-XR dependency service. This is called at the
   * beginning of the {@link #decompress} method to avoid having the
   * constructor's method definition contain a checked exception.
   *
   * @throws FormatException If there is an error initializing JPEG-XR
   * services.
   */
  private void initialize() throws FormatException {
    if (service != null) return;
    try {
      ServiceFactory factory = new ServiceFactory();
      service = factory.getInstance(JPEGXRService.class);
    }
    catch (DependencyException e) {
      throw new MissingLibraryException("JPEG-XR library not available", e);
    }
  }

}
