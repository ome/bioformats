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
 * Implements encoding and decoding methods for Apple RPZA.  This code was
 * adapted from the RPZA codec for ffmpeg - see http://ffmpeg.mplayerhq.hu
 */
public class RPZACodec extends BaseCodec {

  // -- Fields --

  private int totalBlocks, pixelPtr, rowPtr, stride;

  /* @see Codec#compress(byte[], CodecOptions) */
  @Override
  public byte[] compress(byte[] input, CodecOptions options)
    throws FormatException
  {
    throw new UnsupportedCompressionException("RPZA compression not supported.");
  }

  /**
   * The CodecOptions parameter should have the following fields set:
   *  {@link CodecOptions#width width}
   *  {@link CodecOptions#height height}
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

    in.skipBytes(8);

    int plane = options.width * options.height;

    stride = options.width;
    int rowInc = stride - 4;
    short opcode;
    int nBlocks;
    int colorA = 0, colorB;
    int[] color4 = new int[4];
    int index, idx;
    int ta, tb;
    int blockPtr = 0;
    rowPtr = pixelPtr = 0;
    int pixelX, pixelY;

    int[] pixels = new int[plane];
    byte[] rtn = new byte[plane * 3];

    while (in.read() != (byte) 0xe1);
    in.skipBytes(3);

    totalBlocks = ((options.width + 3) / 4) * ((options.height + 3) / 4);

    while (in.getFilePointer() + 2 < in.length()) {
      opcode = in.readByte();
      nBlocks = (opcode & 0x1f) + 1;

      if ((opcode & 0x80) == 0) {
        if (in.getFilePointer() >= in.length()) break;
        colorA = (opcode << 8) | in.read();
        opcode = 0;
        if (in.getFilePointer() >= in.length()) break;
        if ((in.read() & 0x80) != 0) {
          opcode = 0x20;
          nBlocks = 1;
        }
        in.seek(in.getFilePointer() - 1);
      }

      switch (opcode & 0xe0) {
        case 0x80:
          while (nBlocks-- > 0) {
            updateBlock(options.width);
          }
          break;
        case 0xa0:
          if (in.getFilePointer() + 2 >= in.length()) break;
          colorA = in.readShort();
          while (nBlocks-- > 0) {
            blockPtr = rowPtr + pixelPtr;
            for (pixelY=0; pixelY<4; pixelY++) {
              for (pixelX=0; pixelX<4; pixelX++) {
                if (blockPtr >= pixels.length) break;
                pixels[blockPtr] = colorA;

                short s = (short) (pixels[blockPtr] & 0x7fff);
                unpack(s, rtn, blockPtr, pixels.length);
                blockPtr++;
              }
              blockPtr += rowInc;
            }
            updateBlock(options.width);
          }
          break;
        case 0xc0:
        case 0x20:
          if (in.getFilePointer() + 2 >= in.length()) break;
          if ((opcode & 0xe0) == 0xc0) {
            colorA = in.readShort();
          }

          colorB = in.readShort();

          color4[0] = colorB;
          color4[1] = 0;
          color4[2] = 0;
          color4[3] = colorA;

          ta = (colorA >> 10) & 0x1f;
          tb = (colorB >> 10) & 0x1f;
          color4[1] |= ((11*ta + 21*tb) >> 5) << 10;
          color4[2] |= ((21*ta + 11*tb) >> 5) << 10;

          ta = (colorA >> 5) & 0x1f;
          tb = (colorB >> 5) & 0x1f;
          color4[1] |= ((11*ta + 21*tb) >> 5) << 5;
          color4[2] |= ((21*ta + 11*tb) >> 5) << 5;

          ta = colorA & 0x1f;
          tb = colorB & 0x1f;
          color4[1] |= (11*ta + 21*tb) >> 5;
          color4[2] |= (21*ta + 11*tb) >> 5;

          while (nBlocks-- > 0) {
            blockPtr = rowPtr + pixelPtr;
            for (pixelY=0; pixelY<4; pixelY++) {
              if (in.getFilePointer() >= in.length()) break;
              index = in.read();
              for (pixelX=0; pixelX<4; pixelX++) {
                idx = (index >> (2*(3 - pixelX))) & 3;
                if (blockPtr >= pixels.length) break;
                pixels[blockPtr] = color4[idx];

                short s = (short) (pixels[blockPtr] & 0x7fff);
                unpack(s, rtn, blockPtr, pixels.length);
                blockPtr++;
              }
              blockPtr += rowInc;
            }
            updateBlock(options.width);
          }
          break;
        case 0x00:
          blockPtr = rowPtr + pixelPtr;
          for (pixelY=0; pixelY<4; pixelY++) {
            for (pixelX=0; pixelX<4; pixelX++) {
              if ((pixelY != 0) || (pixelX != 0)) {
                if (in.getFilePointer() + 2 >= in.length()) break;
                colorA = in.readShort();
              }
              if (blockPtr >= pixels.length) break;
              pixels[blockPtr] = colorA;

              short s = (short) (pixels[blockPtr] & 0x7fff);
              unpack(s, rtn, blockPtr, pixels.length);
              blockPtr++;
            }
            blockPtr += rowInc;
          }
          updateBlock(options.width);
          break;
      }
    }
    return rtn;
  }

  // -- Helper methods --

  private void unpack(short s, byte[] array, int offset, int len) {
    array[offset] = (byte) (255 - ((s & 0x7c00) >> 10));
    array[offset + len] = (byte) (255 - ((s & 0x3e0) >> 5));
    array[offset + 2*len] = (byte) (255 - (s & 0x1f));
  }

  private void updateBlock(int width) {
    pixelPtr += 4;
    if (pixelPtr >= width) {
      pixelPtr = 0;
      rowPtr += stride * 4;
    }
    totalBlocks--;
  }

}
