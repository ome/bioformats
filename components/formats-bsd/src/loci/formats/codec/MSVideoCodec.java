/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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
 * Methods for compressing and decompressing data using Microsoft Video 1.
 *
 * See http://wiki.multimedia.cx/index.php?title=Microsoft_Video_1 for an
 * excellent description of MSV1.
 */
public class MSVideoCodec extends BaseCodec {

  /* @see Codec#compress(byte[], CodecOptions) */
  @Override
  public byte[] compress(byte[] data, CodecOptions options)
    throws FormatException
  {
    throw new UnsupportedCompressionException(
      "MS Video 1 compression not supported.");
  }

  /**
   * The CodecOptions parameter should have the following fields set:
   *  {@link CodecOptions#width width}
   *  {@link CodecOptions#height height}
   *  {@link CodecOptions#bitsPerSample bitsPerSample}
   *  {@link CodecOptions#previousImage previousImage}
   *
   * @see Codec#decompress(RandomAccessInputStream, CodecOptions)
   */
  @Override
  public byte[] decompress(RandomAccessInputStream in, CodecOptions options)
    throws FormatException, IOException
  {
    if (in == null) 
      throw new IllegalArgumentException("No data to decompress.");
    if (options == null) options = CodecOptions.getDefaultOptions();

    in.order(true);

    int row = 0;
    int column = 0;

    int plane = options.width * options.height;

    byte[] bytes = new byte[plane];
    short[] shorts = new short[plane];

    while (true) {
      if (in.getFilePointer() >= in.length() || row >= options.width ||
        column >= options.height)
      {
        break;
      }
      short a = (short) (in.read() & 0xff);
      short b = (short) (in.read() & 0xff);
      if (a == 0 && b == 0 && in.getFilePointer() >= in.length()) break;
      if (b >= 0x84 && b < 0x88) {
        // indicates that we are skipping some blocks

        int skip = (b - 0x84) * 256 + a;
        for (int i=0; i<skip; i++) {
          if (options.previousImage != null) {
            for (int y=0; y<4; y++) {
              for (int x=0; x<4; x++) {
                if (row + x >= options.width) break;
                if (column + y >= options.height) break;
                int ndx = options.width*(column + y) + row + x;
                int oldNdx =
                  options.width*(options.height - 1 - y - column) + row + x;
                if (options.bitsPerSample == 8) {
                  bytes[ndx] = options.previousImage[oldNdx];
                }
                else {
                  byte red = options.previousImage[oldNdx];
                  byte green = options.previousImage[oldNdx + plane];
                  byte blue = options.previousImage[oldNdx + 2*plane];
                  shorts[ndx] = (short) (((blue & 0x1f) << 10) |
                    ((green & 0x1f) << 5) | (red & 0x1f));
                }
              }
            }
          }

          row += 4;
          if (row >= options.width) {
            row = 0;
            column += 4;
          }
        }
      }
      else if (b >= 0 && b < 0x80) {
        if (options.bitsPerSample == 8) {
          byte colorA = in.readByte();
          byte colorB = in.readByte();

          for (int y=0; y<4; y++) {
            for (int x=3; x>=0; x--) {
              int ndx = options.width*(column + (3 - y)) + row + x;
              short flag = y < 2 ? b : a;
              int shift = 4 - 4*(y % 2) + x;
              int cmp = 1 << shift;
              if ((flag & cmp) == cmp) bytes[ndx] = colorA;
              else bytes[ndx] = colorB;
            }
          }
        }
        else {
          short check1 = in.readShort();
          short check2 = in.readShort();

          if ((check1 & 0x8000) == 0x8000) {
            // 8 color encoding
            short q1a = check1;
            short q1b = check2;
            short q2a = in.readShort();
            short q2b = in.readShort();
            short q3a = in.readShort();
            short q3b = in.readShort();
            short q4a = in.readShort();
            short q4b = in.readShort();

            for (int y=0; y<4; y++) {
              for (int x=3; x>=0; x--) {
                int ndx = options.width*(column + (3 - y)) + row + x;

                short colorA =
                  x < 2 ? (y < 2 ? q3a : q1a) : (y < 2 ? q4a : q2a);
                short colorB =
                  x < 2 ? (y < 2 ? q3b : q1b) : (y < 2 ? q4b : q2b);

                short flag = y < 2 ? b : a;
                int shift = 4 - 4*(y % 2) + x;
                int cmp = 1 << shift;
                if (ndx < shorts.length) {
                  if ((flag & cmp) == cmp) shorts[ndx] = colorA;
                  else shorts[ndx] = colorB;
                }
              }
            }
          }
          else {
            // 2 color encoding

            short colorA = check1;
            short colorB = check2;

            for (int y=0; y<4; y++) {
              for (int x=3; x>=0; x--) {
                int ndx = options.width*(column + (3 - y)) + row + x;
                if (ndx >= shorts.length) break;
                short flag = y < 2 ? b : a;
                int shift = 4 - 4*(y % 2) + x;
                int cmp = 1 << shift;
                if ((flag & cmp) == cmp) shorts[ndx] = colorA;
                else shorts[ndx] = colorB;
              }
            }
          }
        }

        row += 4;
        if (row >= options.width) {
          row = 0;
          column += 4;
        }
      }
      else if (options.bitsPerSample == 8 && 0x90 < b) {
        byte[] colors = new byte[8];
        in.read(colors);

        for (int y=0; y<4; y++) {
          for (int x=3; x>=0; x--) {
            int ndx = options.width*(column + (3 - y)) + row + x;
            byte colorA = y < 2 ? (x < 2 ? colors[4] : colors[6]) :
              (x < 2 ? colors[0] : colors[2]);
            byte colorB = y < 2 ? (x < 2 ? colors[5] : colors[7]) :
              (x < 2 ? colors[1] : colors[3]);

            short flag = y < 2 ? b : a;
            int shift = 4 - 4*(y % 2) + x;
            int cmp = 1 << shift;
            if ((flag & cmp) == cmp) bytes[ndx] = colorA;
            else bytes[ndx] = colorB;
          }
        }

        row += 4;
        if (row >= options.width) {
          row = 0;
          column += 4;
        }
      }
      else {
        for (int y=0; y<4; y++) {
          for (int x=0; x<4; x++) {
            int ndx = options.width*(column + (3 - y)) + row + x;
            if (options.bitsPerSample == 8) {
              if (ndx < bytes.length) {
                bytes[ndx] = (byte) (a & 0xff);
              }
            }
            else {
              if (ndx < shorts.length) {
                shorts[ndx] = (short) (((b << 8) | a) & 0xffff);
              }
            }
          }
        }
        row += 4;
        if (row >= options.width) {
          row = 0;
          column += 4;
        }
      }
    }

    if (options.bitsPerSample == 8) {
      byte[] tmp = bytes;
      bytes = new byte[tmp.length];
      for (int y=0; y<options.height; y++) {
        System.arraycopy(tmp, y*options.width, bytes,
          (options.height-y-1)*options.width, options.width);
      }
      return bytes;
    }

    byte[] b = new byte[plane * 3];
    // expand RGB 5-5-5 to 3 byte tuple

    for (int y=0; y<options.height; y++) {
      for (int x=0; x<options.width; x++) {
        int off = y*options.width + x;
        int dest = (options.height - y - 1)*options.width + x;
        b[dest + 2*plane] = (byte) ((shorts[off] & 0x7c00) >> 10);
        b[dest + plane] = (byte) ((shorts[off] & 0x3e0) >> 5);
        b[dest] = (byte) (shorts[off] & 0x1f);
      }
    }

    return b;
  }

}
