//
// LZOCodec.java
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

import loci.formats.FormatException;

/**
 * This class implements LZO decompression. Compression is not yet
 * implemented.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/codec/LZOCodec.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/codec/LZOCodec.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class LZOCodec extends BaseCodec implements Codec {

  // LZO compression codes
  private static final int LZO_OVERRUN = -6;

  /**
   * Compresses a block of lzo data. Currently not supported.
   *
   * @param data the data to be compressed
   * @param x length of the x dimension of the image data, if appropriate
   * @param y length of the y dimension of the image data, if appropriate
   * @param dims the dimensions of the image data, if appropriate
   * @param options options to be used during compression, if appropriate
   * @return The compressed data
   * @throws FormatException If input is not an LZO-compressed data block.
   */
  public byte[] compress(byte[] data, int x, int y,
      int[] dims, Object options) throws FormatException
  {
    // TODO: Add LZO compression support.
    throw new FormatException("LZO Compression not currently supported");
  }

  /**
   * Decodes an LZO-compressed array.
   * Adapted from LZO for Java, available at
   * http://www.oberhumer.com/opensource/lzo/
   *
   * @param src the data to be decompressed
   * @return The decompressed data
   * @throws FormatException if data is not valid compressed data for this
   *                         decompressor
   */
  public byte[] decompress(byte[] src, Object options) throws FormatException {
    int ip = 0;
    int op = 0;
    byte[] dst = new byte[src.length];
    int t = src[ip++] & 0xff;
    int mPos;

    if (t > 17) {
      t -= 17;
      // do dst[op++] = src[ip++]; while (--t > 0);
      do {
        dst[op++] = src[ip++];
        if(op == dst.length) {
          byte[] newdst = new byte[dst.length * 2];
          System.arraycopy(dst, 0, newdst, 0, dst.length);
          dst = newdst;
        }
      } while (--t > 0);
      t = src[ip++] & 0xff;
//      if (t < 16) return;
      if(t < 16) {
        byte[] newdst = new byte[op];
        System.arraycopy(dst, 0, newdst, 0, op);
        return newdst;
      }
    }

  loop:
    for (;; t = src[ip++] & 0xff) {
      if (t < 16) {
        if (t == 0) {
          while (src[ip] == 0) {
            t += 255;
            ip++;
          }
          t += 15 + (src[ip++] & 0xff);
        }
        t += 3;
        // do dst[op++] = src[ip++]; while (--t > 0);
        do {
          dst[op++] = src[ip++];
          if(op == dst.length) {
            byte[] newdst = new byte[dst.length * 2];
            System.arraycopy(dst, 0, newdst, 0, dst.length);
            dst = newdst;
          }
        } while (--t > 0);
        t = src[ip++] & 0xff;
        if (t < 16) {
          mPos = op - 0x801 - (t >> 2) - ((src[ip++] & 0xff) << 2);
          if (mPos < 0) {
            t = LZO_OVERRUN;
            break loop;
          }
          t = 3;
          do {
            dst[op++] = dst[mPos++];
            if(op == dst.length || mPos == dst.length) {
              byte[] newdst = new byte[dst.length * 2];
              System.arraycopy(dst, 0, newdst, 0, dst.length);
              dst = newdst;
            }
          } while (--t > 0);
//          do dst[op++] = dst[mPos++]; while (--t > 0);
          t = src[ip - 2] & 3;
          if (t == 0) continue;
//          do dst[op++] = src[ip++]; while (--t > 0);
          do {
            dst[op++] = src[ip++];
            if(op == dst.length) {
              byte[] newdst = new byte[dst.length * 2];
              System.arraycopy(dst, 0, newdst, 0, dst.length);
              dst = newdst;
            }
          } while (--t > 0);
          t = src[ip++] & 0xff;
        }
      }
      for (;; t = src[ip++] & 0xff) {
        if (t >= 64) {
          mPos = op - 1 - ((t >> 2) & 7) - ((src[ip++] & 0xff) << 3);
          t = (t >> 5) - 1;
        }
        else if (t >= 32) {
          t &= 31;
          if (t == 0) {
            while (src[ip] == 0) {
              t += 255;
              ip++;
            }
            t += 31 + (src[ip++] & 0xff);
          }
          mPos = op - 1 - ((src[ip++] & 0xff) >> 2);
          mPos -= ((src[ip++] & 0xff) << 6);
        }
        else if (t >= 16) {
          mPos = op - ((t & 8) << 11);
          t &= 7;
          if (t == 0) {
            while (src[ip] == 0) {
              t += 255;
              ip++;
            }
            t += 7 + (src[ip++] & 0xff);
          }
          mPos -= ((src[ip++] & 0xff) >> 2);
          mPos -= ((src[ip++] & 0xff) << 6);
          if (mPos == op) break loop;
          mPos -= 0x4000;
        }
        else {
          mPos = op - 1 - (t >> 2) - ((src[ip++] & 0xff) << 2);
          t = 0;
        }

        if (mPos < 0) {
          t = LZO_OVERRUN;
          break loop;
        }

        t += 2;
//        do dst[op++] = dst[mPos++]; while (--t > 0);
        do {
          dst[op++] = dst[mPos++];
          if(op == dst.length || mPos == dst.length) {
            byte[] newdst = new byte[dst.length * 2];
            System.arraycopy(dst, 0, newdst, 0, dst.length);
            dst = newdst;
          }
        } while (--t > 0);
        t = src[ip - 2] & 3;
        if (t == 0) break;
//        do dst[op++] = src[ip++]; while (--t > 0);
        do {
          dst[op++] = src[ip++];
          if(op == dst.length) {
            byte[] newdst = new byte[dst.length * 2];
            System.arraycopy(dst, 0, newdst, 0, dst.length);
            dst = newdst;
          }
        } while (--t > 0);
      }
    }
    byte[] newdst = new byte[op];
    System.arraycopy(dst, 0, newdst, 0, op);
    return newdst;
  }
}
