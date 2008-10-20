//
// MJPBCodec.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
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

import java.io.*;
//import java.util.Arrays;
import loci.common.*;
import loci.formats.FormatException;

/**
 * Methods for compressing and decompressing QuickTime Motion JPEG-B data.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/codec/MJPBCodec.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/codec/MJPBCodec.java">SVN</a></dd></dl>
 */
public class MJPBCodec extends BaseCodec implements Codec {

  // -- Constants --

  private static final byte[] HEADER = new byte[] {
    (byte) 0xff, (byte) 0xd8, 0, 16, 0x4a, 0x46, 0x49, 0x46, 0,
    1, 1, 0, 0x48, 0x48, 0, 0
  };

  // -- Codec API methods --

  /* @see Codec#compress(byte[], int, int, int[], Object) */
  public byte[] compress(byte[] data, int x, int y, int[] dims, Object options)
    throws FormatException
  {
    throw new FormatException("Motion JPEG-B compression not supported.");
  }

  /* @see Codec#decompress(RandomAccessStream, Object) */
  public byte[] decompress(RandomAccessStream in, Object options)
    throws FormatException, IOException
  {
    if (options == null || !(options instanceof int[])) return null;
    int[] o = (int[]) options;
    int x = o[0];
    int y = o[1];
    int bits = o[2];
    boolean interlaced = o[3] == 1;

    byte[] raw = null;
    byte[] raw2 = null;

    long fp = in.getFilePointer();

    try {
      in.skipBytes(4);

      byte[] lumDcBits = null, lumAcBits = null, lumDc = null, lumAc = null;
      byte[] quant = null;

      String s1 = in.readString(4);
      in.skipBytes(12);
      String s2 = in.readString(4);
      in.seek(in.getFilePointer() - 4);
      if (s1.equals("mjpg") || s2.equals("mjpg")) {
        int extra = 16;
        if (s1.startsWith("m")) {
          extra = 0;
          in.seek(fp + 4);
        }
        in.skipBytes(12);

        int offset = in.readInt() + extra;
        int quantOffset = in.readInt() + extra;
        int huffmanOffset = in.readInt() + extra;
        int sof = in.readInt() + extra;
        int sos = in.readInt() + extra;
        int sod = in.readInt() + extra;

        if (quantOffset != 0) {
          in.seek(fp + quantOffset);
          in.skipBytes(3);
          quant = new byte[64];
          in.read(quant);
        }
        else {
          quant = new byte[] {
            7, 5, 5, 6, 5, 5, 7, 6, 6, 6, 8, 7, 7, 8, 10, 17, 11, 10, 9, 9, 10,
            20, 15, 15, 12, 17, 24, 21, 25, 25, 23, 21, 23, 23, 26, 29, 37, 32,
            26, 28, 35, 28, 23, 23, 33, 44, 33, 35, 39, 40, 42, 42, 42, 25, 31,
            46, 49, 45, 41, 49, 37, 41, 42, 40
          };
        }

        if (huffmanOffset != 0) {
          in.seek(fp + huffmanOffset);
          in.skipBytes(3);
          lumDcBits = new byte[16];
          in.read(lumDcBits);
          lumDc = new byte[12];
          in.read(lumDc);
          in.skipBytes(1);
          lumAcBits = new byte[16];
          in.read(lumAcBits);

          int sum = 0;

          for (int i=0; i<lumAcBits.length; i++) {
            sum += lumAcBits[i] & 0xff;
          }

          lumAc = new byte[sum];
          in.read(lumAc);
        }

        in.seek(fp + sof + 7);

        int channels = in.read();

        int[] sampling = new int[channels];
        for (int i=0; i<channels; i++) {
          in.skipBytes(1);
          sampling[i] = in.read();
          in.skipBytes(1);
        }

        in.seek(fp + sos + 3);
        int[] tables = new int[channels];
        for (int i=0; i<channels; i++) {
          in.skipBytes(1);
          tables[i] = in.read();
        }

        in.seek(fp + sod);
        int numBytes = (int) (offset - in.getFilePointer());
        if (offset == 0) numBytes = (int) (in.length() - in.getFilePointer());
        raw = new byte[numBytes];
        in.read(raw);

        if (offset != 0) {
          in.seek(fp + offset + 36);
          int n = in.readInt();
          in.skipBytes(n);
          in.seek(in.getFilePointer() - 40);

          numBytes = (int) (in.length() - in.getFilePointer());
          raw2 = new byte[numBytes];
          in.read(raw2);
        }
      }

      // insert zero after each byte equal to 0xff
      ByteVector b = new ByteVector();
      for (int i=0; i<raw.length; i++) {
        b.add((byte) raw[i]);
        if (raw[i] == (byte) 0xff) {
          b.add((byte) 0);
        }
      }

      if (raw2 == null) raw2 = new byte[0];
      ByteVector b2 = new ByteVector();
      for (int i=0; i<raw2.length; i++) {
        b2.add((byte) raw2[i]);
        if (raw2[i] == (byte) 0xff) {
          b2.add((byte) 0);
        }
      }

      // assemble fake JPEG plane

      ByteVector v = new ByteVector(1000);
      v.add(HEADER);

      v.add(new byte[] {(byte) 0xff, (byte) 0xdb});

      int length = 4 + quant.length*2;
      v.add((byte) ((length >>> 8) & 0xff));
      v.add((byte) (length & 0xff));
      v.add((byte) 0);
      v.add(quant);

      v.add((byte) 1);
      v.add(quant);

      v.add(new byte[] {(byte) 0xff, (byte) 0xc4});
      length = (lumDcBits.length + lumDc.length + lumAcBits.length +
        lumAc.length)*2 + 6;
      v.add((byte) ((length >>> 8) & 0xff));
      v.add((byte) (length & 0xff));

      v.add((byte) 0);
      v.add(lumDcBits);
      v.add(lumDc);
      v.add((byte) 1);
      v.add(lumDcBits);
      v.add(lumDc);
      v.add((byte) 16);
      v.add(lumAcBits);
      v.add(lumAc);
      v.add((byte) 17);
      v.add(lumAcBits);
      v.add(lumAc);

      v.add((byte) 0xff);
      v.add((byte) 0xc0);

      length = (bits >= 40) ? 11 : 17;
      v.add((byte) ((length >>> 8) & 0xff));
      v.add((byte) (length & 0xff));

      int fieldHeight = y;
      if (interlaced) fieldHeight /= 2;
      if (y % 2 == 1) fieldHeight++;

      int c = bits == 24 ? 3 : (bits == 32 ? 4 : 1);

      v.add(bits >= 40 ? (byte) (bits - 32) : (byte) (bits / c));
      v.add((byte) ((fieldHeight >>> 8) & 0xff));
      v.add((byte) (fieldHeight & 0xff));
      v.add((byte) ((x >>> 8) & 0xff));
      v.add((byte) (x & 0xff));
      v.add((bits >= 40) ? (byte) 1 : (byte) 3);

      v.add((byte) 1);
      v.add((byte) 33);
      v.add((byte) 0);

      if (bits < 40) {
        v.add((byte) 2);
        v.add((byte) 17);
        v.add((byte) 1);
        v.add((byte) 3);
        v.add((byte) 17);
        v.add((byte) 1);
      }

      v.add((byte) 0xff);
      v.add((byte) 0xda);

      length = (bits >= 40) ? 8 : 12;
      v.add((byte) ((length >>> 8) & 0xff));
      v.add((byte) (length & 0xff));

      v.add((bits >= 40) ? (byte) 1 : (byte) 3);
      v.add((byte) 1);
      v.add((byte) 0);

      if (bits < 40) {
        v.add((byte) 2);
        v.add((byte) 1);
        v.add((byte) 3);
        v.add((byte) 1);
      }

      v.add((byte) 0);
      v.add((byte) 0x3f);
      v.add((byte) 0);

      if (interlaced) {
        ByteVector v2 = new ByteVector(v.size());
        v2.add(v.toByteArray());

        v.add(b.toByteArray());
        v.add((byte) 0xff);
        v.add((byte) 0xd9);
        v2.add(b2.toByteArray());
        v2.add((byte) 0xff);
        v2.add((byte) 0xd9);

        JPEGCodec jpeg = new JPEGCodec();
        byte[] top = jpeg.decompress(v.toByteArray(), Boolean.FALSE);
        byte[] bottom = jpeg.decompress(v2.toByteArray(), Boolean.FALSE);

        int bpp = bits < 40 ? bits / 8 : (bits - 32) / 8;
        int ch = bits < 40 ? 3 : 1;
        byte[] result = new byte[x * y * bpp * ch];

        int topNdx = 0;
        int bottomNdx = 0;

        for (int yy=0; yy<y; yy++) {
          if (yy % 2 == 0) {
            System.arraycopy(top, topNdx*x*bpp, result, yy*x*bpp, x*bpp);
            topNdx++;
          }
          else {
            System.arraycopy(bottom, bottomNdx*x*bpp, result, yy*x*bpp, x*bpp);
            bottomNdx++;
          }
        }
        return result;
      }
      else {
        v.add(b.toByteArray());
        v.add((byte) 0xff);
        v.add((byte) 0xd9);
        return new JPEGCodec().decompress(v.toByteArray(), Boolean.FALSE);
      }
    }
    catch (IOException e) {
      throw new FormatException(e);
    }
  }

}
