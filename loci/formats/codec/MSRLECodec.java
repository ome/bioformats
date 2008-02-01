//
// MSRLECodec.java
//

package loci.formats.codec;

import loci.formats.*;

/**
 * Methods for compressing and decompressing data using Microsoft RLE.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/codec/MSRLECodec.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/codec/MSRLECodec.java">SVN</a></dd></dl>
 */
public class MSRLECodec extends BaseCodec implements Codec {

  /* @see Codec#compress(byte[], int, int, int[], Object) */
  public byte[] compress(byte[] data, int x, int y, int[] dims, Object options)
    throws FormatException
  {
    throw new FormatException("MSRLE compression not supported.");
  }

  /* @see Codec#decompress(byte[], Object) */
  public byte[] decompress(byte[] data, Object options) throws FormatException {
    if (options == null || !(options instanceof Object[])) return null;

    Object[] o = (Object[]) options;
    byte[] prev = (byte[]) o[1];
    int[] dims = (int[]) o[0];
    int x = dims[0];
    int y = dims[1];

    int pt = 0;
    short code = 0;
    short extra = 0;
    short stream = 0;

    int pixelPt = 0;
    int row = x;
    int rowPt = (y - 1) * row;
    int frameSize = y * row;

    if (prev == null) prev = new byte[frameSize];

    while (rowPt >= 0 && pt < data.length && pixelPt < prev.length) {
      stream = data[pt++];
      if (stream < 0) stream += 256;
      code = stream;

      if (code == 0) {
        stream = data[pt++];
        if (stream < 0) stream += 256;
        if (stream == 0) {
          rowPt -= row;
          pixelPt = 0;
        }
        else if (stream == 1) return prev;
        else if (stream == 2) {
          stream = data[pt++];
          if (stream < 0) stream += 256;
          pixelPt += stream;
          stream = data[pt++];
          if (stream < 0) stream += 256;
          rowPt -= stream * row;
        }
        else {
          if ((rowPt + pixelPt + stream > frameSize) || (rowPt < 0)) {
            return prev;
          }

          code = stream;
          extra = (short) (stream & 0x01);
          if (stream + code + extra > data.length) return prev;

          while (code-- > 0) {
            stream = data[pt++];
            prev[rowPt + pixelPt] = (byte) stream;
            pixelPt++;
          }
          if (extra != 0) pt++;
        }
      }
      else {
        if ((rowPt + pixelPt + stream > frameSize) || (rowPt < 0)) {
          return prev;
        }

        stream = data[pt++];

        while (code-- > 0) {
          prev[rowPt + pixelPt] = (byte) stream;
          pixelPt++;
        }
      }
    }

    return prev;
  }

}
