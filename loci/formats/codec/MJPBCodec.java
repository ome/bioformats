//
// MJPBCodec.java
//

package loci.formats.codec;

import java.io.*;
//import java.util.Arrays;
import loci.formats.*;

/**
 * Methods for compressing and decompressing QuickTime Motion JPEG-B data.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/codec/MJPBCodec.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/codec/MJPBCodec.java">SVN</a></dd></dl>
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

  /* @see Codec#decompress(byte[], Object) */
  public byte[] decompress(byte[] data, Object options) throws FormatException {
    if (options == null || !(options instanceof int[])) return null;
    int[] o = (int[]) options;
    int x = o[0];
    int y = o[1];
    int bits = o[2];
    boolean interlaced = o[3] == 1;

    byte[] raw = null;
    byte[] raw2 = null;

    try {
      RandomAccessStream ras = new RandomAccessStream(data);
      ras.order(false);
      ras.skipBytes(20);

      byte[] lumDcBits = null, lumAcBits = null, lumDc = null, lumAc = null;
      byte[] quant = null;

      byte[] a = new byte[4];
      ras.read(a);
      String s1 = new String(a);
      ras.seek(ras.getFilePointer() - 20);
      ras.read(a);
      String s2 = new String(a);
      ras.skipBytes(12);
      if (s1.equals("mjpg") || s2.equals("mjpg")) {
        int extra = 16;
        if (s2.startsWith("m")) {
          extra = 0;
          ras.seek(4);
        }
        ras.skipBytes(12);

        int offset = ras.readInt() + extra;
        int quantOffset = ras.readInt() + extra;
        int huffmanOffset = ras.readInt() + extra;
        int sof = ras.readInt() + extra;
        int sos = ras.readInt() + extra;
        int sod = ras.readInt() + extra;

        if (quantOffset != 0) {
          ras.seek(quantOffset);
          int len = ras.readShort();
          ras.skipBytes(1);
          quant = new byte[64];
          ras.read(quant);
        }

        if (huffmanOffset != 0) {
          ras.seek(huffmanOffset);
          int len = ras.readShort();
          ras.skipBytes(1);
          lumDcBits = new byte[16];
          ras.read(lumDcBits);
          lumDc = new byte[12];
          ras.read(lumDc);
          ras.skipBytes(1);
          lumAcBits = new byte[16];
          ras.read(lumAcBits);

          int sum = 0;

          for (int i=0; i<lumAcBits.length; i++) {
            sum += lumAcBits[i] & 0xff;
          }

          lumAc = new byte[sum];
          ras.read(lumAc);
          /*
          if (sum == 162) ras.read(lumAc);
          else {
            byte[] tmp = new byte[162];
            ras.read(tmp);

            ByteVector v = new ByteVector(sum);
            int[] count = new int[lumAcBits.length];
            Arrays.fill(count, (byte) 0);
            for (int i=0; i<tmp.length; i++) {
              int size = 0;
              int val = tmp[i] & 0xff;
              while (Math.pow(2, size) < val) size++;
              if (count[size] < lumAcBits[size]) {
                v.add(tmp[i]);
                count[size]++;
              }
              else if (size == 8) {
                for (int j=size+1; j<lumAcBits.length; j++) {
                  if (count[j] < lumAcBits[j]) {
                    v.add(tmp[i]);
                    count[j]++;
                  }
                }
              }
            }

            lumAc = v.toByteArray();
          }
          */
        }

        ras.seek(sof + 7);

        int channels = ras.read();

        int[] sampling = new int[channels];
        for (int i=0; i<channels; i++) {
          ras.skipBytes(1);
          sampling[i] = ras.read();
          ras.skipBytes(1);
        }

        ras.seek(sos + 3);
        int[] tables = new int[channels];
        for (int i=0; i<channels; i++) {
          ras.skipBytes(1);
          tables[i] = ras.read();
        }

        ras.seek(sod);
        int numBytes = (int) (offset - ras.getFilePointer());
        if (offset == 0) numBytes = (int) (ras.length() - ras.getFilePointer());
        raw = new byte[numBytes];
        ras.read(raw);

        if (offset != 0) {
          ras.seek(offset + 36);
          int n = ras.readInt();
          ras.skipBytes(n);
          ras.seek(ras.getFilePointer() - 40);

          numBytes = (int) (ras.length() - ras.getFilePointer());
          raw2 = new byte[numBytes];
          ras.read(raw2);
        }
      }

      if (raw == null) raw = data;

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
        byte[] top = jpeg.decompress(v.toByteArray());
        byte[] bottom = jpeg.decompress(v2.toByteArray());

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
        return new JPEGCodec().decompress(v.toByteArray());
      }
    }
    catch (IOException e) {
      throw new FormatException(e);
    }
  }

}
