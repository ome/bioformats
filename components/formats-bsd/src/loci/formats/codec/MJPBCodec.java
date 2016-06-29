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
 * Methods for compressing and decompressing QuickTime Motion JPEG-B data.
 */
public class MJPBCodec extends BaseCodec {

  // -- Constants --

  private static final byte[] HEADER = new byte[] {
    (byte) 0xff, (byte) 0xd8, 0, 16, 0x4a, 0x46, 0x49, 0x46, 0,
    1, 1, 0, 0x48, 0x48, 0, 0
  };

  // -- Codec API methods --

  /* @see Codec#compress(byte[], CodecOptions) */
  @Override
  public byte[] compress(byte[] data, CodecOptions options)
    throws FormatException
  {
    throw new UnsupportedCompressionException(
      "Motion JPEG-B compression not supported.");
  }

  /**
   * The CodecOptions parameter must be an instance of {@link MJPBCodecOptions},
   * and should have the following fields set:
   *  {@link MJPBCodecOptions#interlaced interlaced}
   *  {@link CodecOptions#width width}
   *  {@link CodecOptions#height height}
   *  {@link CodecOptions#bitsPerSample bitsPerSample}
   *  {@link CodecOptions#littleEndian littleEndian}
   *  {@link CodecOptions#interleaved interleaved}
   *
   * @see Codec#decompress(RandomAccessInputStream, CodecOptions)
   */
  @Override
  public byte[] decompress(RandomAccessInputStream in, CodecOptions options)
    throws FormatException, IOException
  {
    if (options == null) options = CodecOptions.getDefaultOptions();
    if (!(options instanceof MJPBCodecOptions)) {
      throw new FormatException("Options must be an instance of " +
        "loci.formats.codec.MJPBCodecOptions");
    }

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

        if (quantOffset != 0 && quantOffset + fp < in.length()) {
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

        if (huffmanOffset != 0 && huffmanOffset + fp < in.length()) {
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

        int channels = in.read() & 0xff;

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

      length = (options.bitsPerSample >= 40) ? 11 : 17;
      v.add((byte) ((length >>> 8) & 0xff));
      v.add((byte) (length & 0xff));

      int fieldHeight = options.height;
      if (((MJPBCodecOptions) options).interlaced) fieldHeight /= 2;
      if (options.height % 2 == 1) fieldHeight++;

      int c = options.bitsPerSample == 24 ? 3 :
        (options.bitsPerSample == 32 ? 4 : 1);

      v.add(options.bitsPerSample >= 40 ? (byte) (options.bitsPerSample - 32) :
        (byte) (options.bitsPerSample / c));
      v.add((byte) ((fieldHeight >>> 8) & 0xff));
      v.add((byte) (fieldHeight & 0xff));
      v.add((byte) ((options.width >>> 8) & 0xff));
      v.add((byte) (options.width & 0xff));
      v.add((options.bitsPerSample >= 40) ? (byte) 1 : (byte) 3);

      v.add((byte) 1);
      v.add((byte) 33);
      v.add((byte) 0);

      if (options.bitsPerSample < 40) {
        v.add((byte) 2);
        v.add((byte) 17);
        v.add((byte) 1);
        v.add((byte) 3);
        v.add((byte) 17);
        v.add((byte) 1);
      }

      v.add((byte) 0xff);
      v.add((byte) 0xda);

      length = (options.bitsPerSample >= 40) ? 8 : 12;
      v.add((byte) ((length >>> 8) & 0xff));
      v.add((byte) (length & 0xff));

      v.add((options.bitsPerSample >= 40) ? (byte) 1 : (byte) 3);
      v.add((byte) 1);
      v.add((byte) 0);

      if (options.bitsPerSample < 40) {
        v.add((byte) 2);
        v.add((byte) 1);
        v.add((byte) 3);
        v.add((byte) 1);
      }

      v.add((byte) 0);
      v.add((byte) 0x3f);
      v.add((byte) 0);

      if (((MJPBCodecOptions) options).interlaced) {
        ByteVector v2 = new ByteVector(v.size());
        v2.add(v.toByteArray());

        v.add(b.toByteArray());
        v.add((byte) 0xff);
        v.add((byte) 0xd9);
        v2.add(b2.toByteArray());
        v2.add((byte) 0xff);
        v2.add((byte) 0xd9);

        JPEGCodec jpeg = new JPEGCodec();
        byte[] top = jpeg.decompress(v.toByteArray(), options);
        byte[] bottom = jpeg.decompress(v2.toByteArray(), options);

        int bpp = options.bitsPerSample < 40 ? options.bitsPerSample / 8 :
          (options.bitsPerSample - 32) / 8;
        int ch = options.bitsPerSample < 40 ? 3 : 1;
        byte[] result = new byte[options.width * options.height * bpp * ch];

        int topNdx = 0;
        int bottomNdx = 0;

        int row = options.width * bpp;

        for (int yy=0; yy<options.height; yy++) {
          if (yy % 2 == 0 && (topNdx + 1) * row <= top.length) {
            System.arraycopy(top, topNdx*row, result, yy*row, row);
            topNdx++;
          }
          else {
            if ((bottomNdx + 1) * row <= bottom.length) {
              System.arraycopy(bottom, bottomNdx*row, result, yy*row, row);
            }
            bottomNdx++;
          }
        }
        return result;
      }
      else {
        v.add(b.toByteArray());
        v.add((byte) 0xff);
        v.add((byte) 0xd9);
        return new JPEGCodec().decompress(v.toByteArray(), options);
      }
    }
    catch (IOException e) {
      throw new FormatException(e);
    }
  }

}
