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


/**
 * {@link LutSource} which makes use of the {@link loci.commons.lut.ij.LutLoader}
 * class to parse a string input into individual colors.
 *
 * @author Josh Moore, josh at glencoesoftware.com
 */
public class ImageJLutSource extends AbstractLutSource {

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

    public byte[] applyLut(int no, byte[] buf, int x, int y, int w, int h) {

      // FIXME The following is non-functional and only for testing purposes.

      // copied from ImageTools which is in bsd
      byte[][] lut = new byte[][] { reds, greens, blues };
      byte[][] rtn = new byte[lut.length][buf.length];

      for (int i=0; i<buf.length; i++) {
        for (int j=0; j<lut.length; j++) {
          rtn[j][i] = lut[j][buf[i] & 0xff];
        }
      }

      // partial copy from ChannelFiller. Needs refactoring.
      byte[][] b = rtn; // ImageTools.indexedToRGB(new byte[][]{reds, greens, blues}, buf);
      // non-interleaved
      if (false) {
        System.out.println("b.length=" + b.length);
        System.out.println("buf.length=" + buf.length);
        System.out.println("lut.length=" + lut.length);
        System.out.println("lut[0].length=" + lut[0].length);
        for (int i=0; i<b.length; i++) {
          System.out.println("b[" + i + "]=" + b[i].length);
          System.arraycopy(b[i], 0, buf, i*b[i].length, b[i].length);
        }
        return buf;
      }

      // interleaved
      int pt = 0;
      for (int i=0; i<b[0].length; i++) {
        //for (int j=0; j<b.length; j++) {
        for (int j=0; j<1; j++) { // FIXME
          buf[pt++] = b[j][i];
        }
      }
      return buf;
    }

}
