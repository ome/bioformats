//
// RPZACodec.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.codec;

import loci.formats.*;

/**
 * Implements encoding and decoding methods for Apple RPZA.  This code was
 * adapted from the RPZA codec for ffmpeg - see http://ffmpeg.mplayerhq.hu
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/codec/RPZACodec.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/codec/RPZACodec.java">SVN</a></dd></dl>
 */
public class RPZACodec extends BaseCodec implements Codec {

  /* @see Codec#compress(byte[], int, int, int[], Object) */
  public byte[] compress(byte[] input, int x, int y, int[] dims,
    Object options) throws FormatException
  {
    throw new FormatException("RPZA compression not supported.");
  }

  /* @see Codec#decompress(byte[], Object) */
  public byte[] decompress(byte[] data, Object options) throws FormatException {
    if (options == null || !(options instanceof int[])) return null;

    int[] o = (int[]) options;
    int x = o[0];
    int y = o[1];

    int stride = x;
    int rowInc = stride - 4;
    int streamPtr = 8;
    short opcode;
    int nBlocks;
    int colorA = 0, colorB;
    int[] color4 = new int[4];
    int index, idx;
    int ta, tb;
    int rowPtr = 0, pixelPtr = 0, blockPtr = 0;
    int pixelX, pixelY;
    int totalBlocks;

    int[] pixels = new int[x * y];
    byte[] rtn = new byte[x * y * 3];

    while (data[streamPtr] != (byte) 0xe1) streamPtr++;
    streamPtr += 4;

    totalBlocks = ((x + 3) / 4) * ((y + 3) / 4);

    while (streamPtr < data.length) {
      opcode = data[streamPtr++];
      nBlocks = (opcode & 0x1f) + 1;

      if ((opcode & 0x80) == 0) {
        if (streamPtr >= data.length) break;
        colorA = (opcode << 8) | data[streamPtr++];
        opcode = 0;
        if (streamPtr >= data.length) break;
        if ((data[streamPtr] & 0x80) != 0) {
          opcode = 0x20;
          nBlocks = 1;
        }
      }

      switch (opcode & 0xe0) {
        case 0x80:
          while (nBlocks-- > 0) {
            pixelPtr += 4;
            if (pixelPtr >= x) {
              pixelPtr = 0;
              rowPtr += stride * 4;
            }
            totalBlocks--;
          }
          break;
        case 0xa0:
          colorA = DataTools.bytesToInt(data, streamPtr, 2, false);
          streamPtr += 2;
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
            pixelPtr += 4;
            if (pixelPtr >= x) {
              pixelPtr = 0;
              rowPtr += stride * 4;
            }
            totalBlocks--;
          }
          break;
        case 0xc0:
        case 0x20:
          if ((opcode & 0xe0) == 0xc0) {
            colorA = DataTools.bytesToInt(data, streamPtr, 2, false);
            streamPtr += 2;
          }

          colorB = DataTools.bytesToInt(data, streamPtr, 2, false);
          streamPtr += 2;

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
              if (streamPtr >= data.length) break;
              index = data[streamPtr++];
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
            pixelPtr += 4;
            if (pixelPtr >= x) {
              pixelPtr = 0;
              rowPtr += stride * 4;
            }
            totalBlocks--;
          }
          break;
        case 0x00:
          blockPtr = rowPtr + pixelPtr;
          for (pixelY=0; pixelY<4; pixelY++) {
            for (pixelX=0; pixelX<4; pixelX++) {
              if ((pixelY != 0) || (pixelX != 0)) {
                colorA = DataTools.bytesToInt(data, streamPtr, 2, false);
                streamPtr += 2;
              }
              if (blockPtr >= pixels.length) break;
              pixels[blockPtr] = colorA;

              short s = (short) (pixels[blockPtr] & 0x7fff);
              unpack(s, rtn, blockPtr, pixels.length);
              blockPtr++;
            }
            blockPtr += rowInc;
          }
          pixelPtr += 4;
          if (pixelPtr >= x) {
            pixelPtr = 0;
            rowPtr += stride * 4;
          }
          totalBlocks--;
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
}
