/*
 * #%L
 * Common package for lookup tables
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

package loci.common.lut.ij;

import loci.common.lut.AbstractLutSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link LutSource} which makes use of the {@link loci.commons.lut.ij.LutLoader}
 * class to parse a string input into individual colors.
 *
 * @author Josh Moore, josh at glencoesoftware.com
 */
public class ImageJLutSource extends AbstractLutSource {

    /** Logger for this class. */
    private static final Logger LOGGER =
      LoggerFactory.getLogger(ImageJLutSource.class);

    private final byte[] reds;
    private final byte[] greens;
    private final byte[] blues;

    public ImageJLutSource(String ijArg) {
        LutLoader loader = new LutLoader();
        loader.run(ijArg);
        int size = loader.getLutSize();
        if (size == 0) {
            throw new IllegalArgumentException("Unknown LUT: " + ijArg);
        }
        this.reds = loader.getReds();
        this.greens = loader.getGreens();
        this.blues = loader.getBlues();
    }

    public ImageJLutSource(byte[] reds, byte[] greens, byte[] blues) {
        this.reds = reds;
        this.greens = greens;
        this.blues = blues;
    }

    @Override
    public byte[] applyLut(int no, byte[] buf, int x, int y, int w, int h) {

      // copied from ImageTools which is in bsd
      byte[][] lut = new byte[][] { reds, greens, blues };
      byte[] rtn = new byte[lut.length * buf.length];

      // currently assumes 8-bit data only
      for (int i=0; i<buf.length; i++) {
        for (int j=0; j<lut.length; j++) {
          rtn[i * lut.length + j] = lut[j][buf[i] & 0xff];
        }
      }

      return rtn;
    }
}
