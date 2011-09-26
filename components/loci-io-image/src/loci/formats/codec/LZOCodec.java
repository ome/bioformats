//
// LZOCodec.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
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
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/codec/LZOCodec.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/codec/LZOCodec.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class LZOCodec extends BaseCodec {

  // LZO compression codes
  private static final int LZO_OVERRUN = -6;

  /* @see Codec#compress(byte[], CodecOptions) */
  public byte[] compress(byte[] data, CodecOptions options)
    throws FormatException
  {
    // TODO: Add LZO compression support.
    throw new UnsupportedCompressionException(
      "LZO Compression not currently supported");
  }

  /* @see Codec#decompress(RandomAccessInputStream, CodecOptions) */
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
