//
// QTRLECodec.java
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

import java.io.IOException;
import loci.formats.*;

/**
 * Methods for compressing and decompressing data using QuickTime RLE.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/codec/QTRLECodec.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/codec/QTRLECodec.java">SVN</a></dd></dl>
 */
public class QTRLECodec extends BaseCodec implements Codec {

  /* @see Codec#compress(byte[], int, int, int[], Object) */
  public byte[] compress(byte[] data, int x, int y, int[] dims, Object options)
    throws FormatException
  {
    throw new FormatException("QTRLE compression not supported.");
  }

  /* @see Codec#decompress(byte[], Object) */
  public byte[] decompress(byte[] data, Object options) throws FormatException {
    if (options == null || !(options instanceof Object[])) return null;

    Object[] o = (Object[]) options;
    byte[] prev = (byte[]) o[1];
    int[] dims = (int[]) o[0];
    int x = dims[0];
    int y = dims[1];
    int bpp = dims[2];
    int numLines = y;

    if (data.length < 8) return prev;

    try {
      RABytes s = new RABytes(data);
      s.skipBytes(4);

      int header = s.readShort();
      int off = 0;
      int start = 0;

      byte[] output = new byte[x * y * bpp];

      if ((header & 8) == 8) {
        start = s.readShort();
        s.skipBytes(2);
        numLines = s.readShort();
        s.skipBytes(2);

        if (prev != null) {
          for (int i=0; i<start; i++) {
            off = i * x * bpp;
            System.arraycopy(prev, off, output, off, x * bpp);
          }
        }
        off += x * bpp;

        if (prev != null) {
          for (int i=start+numLines; i<y; i++) {
            int offset = i * x * bpp;
            System.arraycopy(prev, offset, output, offset, x * bpp);
          }
        }
      }
      else throw new FormatException("Unsupported header : " + header);

      // uncompress remaining lines

      int skip = 0; // number of bytes to skip
      byte rle = 0; // RLE code

      int rowPointer = start * x * bpp;

      for (int i=0; i<numLines; i++) {
        skip = s.read();
        if (skip < 0) skip += 256;

        if (prev != null) {
          try {
            System.arraycopy(prev, rowPointer, output, rowPointer,
              (skip - 1) * bpp);
          }
          catch (ArrayIndexOutOfBoundsException e) { }
        }

        off = rowPointer + ((skip - 1) * bpp);
        while (true) {
          rle = (byte) (s.read() & 0xff);

          if (rle == 0) {
            skip = s.read();

            if (prev != null) {
              try {
                System.arraycopy(prev, off, output, off, (skip - 1) * bpp);
              }
              catch (ArrayIndexOutOfBoundsException e) { }
            }

            off += (skip - 1) * bpp;
          }
          else if (rle == -1) {
            if (off < (rowPointer + (x * bpp)) && prev != null) {
              System.arraycopy(prev, off, output, off, rowPointer +
                (x * bpp) - off);
            }
            break;
          }
          else if (rle < -1) {
            // unpack next pixel and copy it to output -(rle) times
            for (int j=0; j<(-1*rle); j++) {
              if (off < output.length) {
                System.arraycopy(data, (int) s.getFilePointer(), output,
                  off, bpp);
                off += bpp;
              }
              else break;
            }
            s.skipBytes(bpp);
          }
          else {
            // copy (rle) pixels to output
            int len = rle * bpp;
            if (output.length - off < len) len = output.length - off;
            if (s.length() - s.getFilePointer() < len) {
              len = (int) (s.length() - s.getFilePointer());
            }
            if (len < 0) len = 0;
            if (off > output.length) off = output.length;
            s.read(output, off, len);
            off += len;
          }
          if (s.getFilePointer() >= s.length()) return output;
        }
        rowPointer += x * bpp;
      }
      return output;
    }
    catch (IOException e) {
      throw new FormatException(e);
    }
  }

}
