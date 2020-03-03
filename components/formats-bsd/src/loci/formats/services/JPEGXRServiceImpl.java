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

package loci.formats.services;

import java.nio.ByteBuffer;

import loci.common.services.AbstractService;
import loci.formats.FormatException;

import ome.jxrlib.Decode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interface defining methods for working with JPEG-XR data
 */
public class JPEGXRServiceImpl extends AbstractService implements JPEGXRService {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(JPEGXRServiceImpl.class);

  public JPEGXRServiceImpl() {
    checkClassDependency(ome.jxrlib.Decode.class);
  }

  /**
   * @see JPEGXRServiceImpl#decompress(byte[])
   */
  public byte[] decompress(byte[] compressed) throws FormatException {
      LOGGER.trace("begin tile decode; compressed size = {}", compressed.length);
      try {
        Decode decode = new Decode(compressed);
        boolean bgr = decode.isBGR();
        int bpp = (int) decode.getBytesPerPixel();
        int pixels = (int) (decode.getWidth() * decode.getHeight());
        ByteBuffer output = ByteBuffer.allocateDirect(pixels * bpp);
        decode.toBytes(output);

        if (bgr) {
          // only happens with 8 bits per channel,
          // 3 (BGR) or 4 (BGRA) channel data
          for (int p=0; p<output.capacity(); p+=bpp) {
            byte tmp = output.get(p);
            output.put(p, output.get(p + 2));
            output.put(p + 2, tmp);
          }
        }
        byte[] raw = new byte[output.capacity()];
        output.get(raw);
        return raw;
      }
      // really only want to catch ome.jxrlib.FormatError, but that doesn't compile
      catch (Exception e) {
        throw new FormatException(e);
      }
  }

}
