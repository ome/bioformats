//
// MSVideoCodec.java
//

package loci.formats.codec;

import loci.formats.DataTools;
import loci.formats.FormatException;

/**
 * Methods for compressing and decompressing data using Microsoft Video 1.
 *
 * See http://wiki.multimedia.cx/index.php?title=Microsoft_Video_1 for an
 * excellent description of MSV1.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/codec/MSVideoCodec.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/codec/MSVideoCodec.java">SVN</a></dd></dl>
 */
public class MSVideoCodec extends BaseCodec implements Codec {

  /* @see Codec#compress(byte[], int, int, int[], Object) */
  public byte[] compress(byte[] data, int x, int y, int[] dims, Object options)
    throws FormatException
  {
    throw new FormatException("MS Video 1 compression not supported.");
  }

  /* @see Codec#decompress(byte[], Object) */
  public byte[] decompress(byte[] data, Object options) throws FormatException {
    if (options == null || !(options instanceof Object[])) return null;
    Object[] optionsArray = (Object[]) options;
    int bitsPerPixel = ((Integer) optionsArray[0]).intValue();
    int width = ((Integer) optionsArray[1]).intValue();
    int height = ((Integer) optionsArray[2]).intValue();
    byte[] lastImage = (byte[]) optionsArray[3];

    int pt = 0;

    int blocksPerRow = (width + 3) / 4;
    int blocksPerColumn = (height + 3) / 4;

    int row = 0;
    int column = 0;

    byte[] bytes = new byte[width * height];
    short[] shorts = new short[width * height];

    while (true) {
      if (pt >= data.length || row >= width || column >= height) break;
      short a = (short) (data[pt++] & 0xff);
      short b = (short) (data[pt++] & 0xff);
      if (a == 0 && b == 0 && pt >= data.length) break;
      if (b >= 0x84 && b < 0x88) {
        // indicates that we are skipping some blocks

        int skip = (b - 0x84) * 256 + a;
        for (int i=0; i<skip; i++) {
          if (lastImage != null) {
            for (int y=0; y<4; y++) {
              for (int x=0; x<4; x++) {
                if (row + x >= width) break;
                if (column + y >= height) break;
                int ndx = width*(column + y) + row + x;
                int oldNdx = width*(height - 1 - y - column) + row + x;
                if (bitsPerPixel == 8) {
                  bytes[ndx] = lastImage[oldNdx];
                }
                else {
                  byte red = lastImage[oldNdx];
                  byte green = lastImage[oldNdx + width*height];
                  byte blue = lastImage[oldNdx + 2*width*height];
                  shorts[ndx] = (short) (((blue & 0x1f) << 10) |
                    ((green & 0x1f) << 5) | (red & 0x1f));
                }
              }
            }
          }

          row += 4;
          if (row >= width) {
            row = 0;
            column += 4;
          }
        }
      }
      else if (b >= 0 && b < 0x80) {
        if (bitsPerPixel == 8) {
          byte colorA = data[pt++];
          byte colorB = data[pt++];

          for (int y=0; y<4; y++) {
            for (int x=3; x>=0; x--) {
              int ndx = width*(column + y) + row + x;
              short flag = y < 2 ? b : a;
              int shift = 4 - 4*(y % 2) + x;
              int cmp = 1 << shift;
              if ((flag & cmp) == cmp) bytes[ndx] = colorA;
              else bytes[ndx] = colorB;
            }
          }
        }
        else {
          short check1 = DataTools.bytesToShort(data, pt, true);
          pt += 2;
          short check2 = DataTools.bytesToShort(data, pt, true);
          pt += 2;

          if ((check1 & 0x8000) == 0x8000) {
            // 8 color encoding
            short q1a = check1;
            short q1b = check2;
            short q2a = DataTools.bytesToShort(data, pt, true);
            pt += 2;
            short q2b = DataTools.bytesToShort(data, pt, true);
            pt += 2;
            short q3a = DataTools.bytesToShort(data, pt, true);
            pt += 2;
            short q3b = DataTools.bytesToShort(data, pt, true);
            pt += 2;
            short q4a = DataTools.bytesToShort(data, pt, true);
            pt += 2;
            short q4b = DataTools.bytesToShort(data, pt, true);
            pt += 2;

            for (int y=0; y<4; y++) {
              for (int x=3; x>= 0; x--) {
                int ndx = width*(column + y) + row + x;

                short colorA =
                  x < 2 ? (y < 2 ? q3a : q1a) : (y < 2 ? q4a : q2a);
                short colorB =
                  x < 2 ? (y < 2 ? q3b : q1b) : (y < 2 ? q4b : q2b);

                short flag = y < 2 ? b : a;
                int shift = 4 - 4*(y % 2) + x;
                int cmp = 1 << shift;
                if ((flag & cmp) == cmp) shorts[ndx] = colorA;
                else shorts[ndx] = colorB;
              }
            }
          }
          else {
            // 2 color encoding

            short colorA = check1;
            short colorB = check2;

            for (int y=0; y<4; y++) {
              for (int x=3; x>=0; x--) {
                int ndx = width*(column + y) + row + x;
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
        if (row >= width) {
          row = 0;
          column += 4;
        }
      }
      else if (bitsPerPixel == 8 && 0x90 < b) {
        byte[] colors = new byte[8];
        System.arraycopy(data, pt, colors, 0, colors.length);
        pt += colors.length;

        for (int y=0; y<4; y++) {
          for (int x=3; x>=0; x--) {
            int ndx = width*(column + y) + row + x;
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
      }
      else {
        for (int y=0; y<4; y++) {
          for (int x=0; x<4; x++) {
            int ndx = width*(column + y) + row + x;
            if (bitsPerPixel == 8) bytes[ndx] = (byte) (a & 0xff);
            else shorts[ndx] = (short) (((b << 8) | a) & 0xffff);
          }
        }
        row += 4;
        if (row >= width) {
          row = 0;
          column += 4;
        }
      }
    }

    if (bitsPerPixel == 8) {
      byte[] tmp = bytes;
      bytes = new byte[tmp.length];
      for (int y=0; y<height; y++) {
        System.arraycopy(tmp, y*width, bytes, (height-y-1)*width, width);
      }
      return bytes;
    }

    byte[] b = new byte[width * height * 3];
    // expand RGB 5-5-5 to 3 byte tuple

    for (int y=0; y<height; y++) {
      for (int x=0; x<width; x++) {
        int off = y*width + x;
        int dest = (height - y - 1)*width + x;
        b[dest + 2*width*height] = (byte) ((shorts[off] & 0x7c00) >> 10);
        b[dest + width*height] = (byte) ((shorts[off] & 0x3e0) >> 5);
        b[dest] = (byte) (shorts[off] & 0x1f);
      }
    }

    return b;
  }

}
