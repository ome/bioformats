/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2016 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.formats.codec;

import java.io.IOException;

import loci.common.RandomAccessInputStream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatException;
import loci.formats.ImageTools;
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
   *
   * @see Codec#decompress(byte[], CodecOptions)
   */
  @Override
  public byte[] decompress(byte[] buf, CodecOptions options)
    throws FormatException
  {
    initialize();

    byte[] uncompressed = service.decompress(buf);

    if (options.interleaved) {
      return uncompressed;
    }

    int bpp = options.bitsPerSample / 8;
    int pixels = options.width * options.height;
    int c = uncompressed.length / (pixels * bpp);

    if (c == 1) {
      return uncompressed;
    }

    byte[] deinterleaved = new byte[uncompressed.length];

    for (int p=0; p<pixels; p++) {
      for (int channel=0; channel<c; channel++) {
        for (int b=0; b<bpp; b++) {
          int bb = options.littleEndian ? b : bpp - b - 1;
          deinterleaved[bpp * (channel * pixels + p) + bb] =
            uncompressed[bpp * (p * c + channel) + b];
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
