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

package loci.formats.codec;

import java.io.IOException;

import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.UnsupportedCompressionException;

/**
 * This class implements LZO decompression. Compression is not yet
 * implemented.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class LZOCodec extends BaseCodec {

  // LZO compression codes
  private static final int LZO_OVERRUN = -6;

  /* @see Codec#compress(byte[], CodecOptions) */
  @Override
  public byte[] compress(byte[] data, CodecOptions options)
    throws FormatException
  {
    // TODO: Add LZO compression support.
    throw new UnsupportedCompressionException(
      "LZO Compression not currently supported");
  }

  /* @see Codec#decompress(RandomAccessInputStream, CodecOptions) */
  @Override
  public byte[] decompress(RandomAccessInputStream in, CodecOptions options)
    throws FormatException, IOException
  {
    if (in == null) 
      throw new IllegalArgumentException("No data to decompress.");
    // Adapted from LZO for Java, available at
    // http://www.oberhumer.com/opensource/lzo/
    ByteVector dst = new ByteVector();
    int t = in.read() & 0xff;
    int mPos;

    if (t > 17) {
      t -= 17;
      // do dst[op++] = src[ip++]; while (--t > 0);
      byte[] b = new byte[t];
      in.read(b);
      dst.add(b);
      t = in.read() & 0xff;
//      if (t < 16) return;
      if(t < 16) {
        return dst.toByteArray();
      }
    }

  loop:
    for (;; t = in.read() & 0xff) {
      if (t < 16) {
        if (t == 0) {
          byte f = in.readByte();
          while (f == 0) {
            t += 255;
            f = in.readByte();
          }
          t += 15 + (f & 0xff);
        }
        t += 3;
        // do dst[op++] = src[ip++]; while (--t > 0);
        byte[] b = new byte[t];
        in.read(b);
        dst.add(b);
        t = in.read() & 0xff;
        if (t < 16) {
          mPos = dst.size() - 0x801 - (t >> 2) - ((in.read() & 0xff) << 2);
          if (mPos < 0) {
            t = LZO_OVERRUN;
            break loop;
          }
          t = 3;
          do {
            dst.add(dst.get(mPos++));
          } while (--t > 0);
//          do dst[op++] = dst[mPos++]; while (--t > 0);
          in.seek(in.getFilePointer() - 2);
          t = in.read() & 3;
          in.skipBytes(1);
          if (t == 0) continue;
//          do dst[op++] = src[ip++]; while (--t > 0);
          b = new byte[t];
          in.read(b);
          dst.add(b);
          t = in.read() & 0xff;
        }
      }
      for (;; t = in.read() & 0xff) {
        if (t >= 64) {
          mPos = dst.size() - 1 - ((t >> 2) & 7) - ((in.read() & 0xff) << 3);
          t = (t >> 5) - 1;
        }
        else if (t >= 32) {
          t &= 31;
          if (t == 0) {
            byte f = in.readByte();
            while (f == 0) {
              t += 255;
              f = in.readByte();
            }
            t += 31 + (f & 0xff);
          }
          mPos = dst.size() - 1 - ((in.read() & 0xff) >> 2);
          mPos -= ((in.read() & 0xff) << 6);
        }
        else if (t >= 16) {
          mPos = dst.size() - ((t & 8) << 11);
          t &= 7;
          if (t == 0) {
            byte f = in.readByte();
            while (f == 0) {
              t += 255;
              f = in.readByte();
            }
            t += 7 + (f & 0xff);
          }
          mPos -= ((in.read() & 0xff) >> 2);
          mPos -= ((in.read() & 0xff) << 6);
          if (mPos == dst.size()) break loop;
          mPos -= 0x4000;
        }
        else {
          mPos = dst.size() - 1 - (t >> 2) - ((in.read() & 0xff) << 2);
          t = 0;
        }

        if (mPos < 0) {
          t = LZO_OVERRUN;
          break loop;
        }

        t += 2;
//        do dst[op++] = dst[mPos++]; while (--t > 0);
        do {
          dst.add(dst.get(mPos++));
        } while (--t > 0);
        in.seek(in.getFilePointer() - 2);
        t = in.read() & 3;
        in.skipBytes(1);
        if (t == 0) break;
//        do dst[op++] = src[ip++]; while (--t > 0);
        byte[] b = new byte[t];
        in.read(b);
        dst.add(b);
        t = 0;
      }
    }
    return dst.toByteArray();
  }
}
