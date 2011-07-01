//
// MSRLECodec.java
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
 * Methods for compressing and decompressing data using Microsoft RLE.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/codec/MSRLECodec.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/codec/MSRLECodec.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class MSRLECodec extends BaseCodec {

  /* @see Codec#compress(byte[], CodecOptions) */
  public byte[] compress(byte[] data, CodecOptions options)
    throws FormatException
  {
    throw new UnsupportedCompressionException(
      "MSRLE compression not supported.");
  }

  /**
   * The CodecOptions parameter should have the following fields set:
   *  {@link CodecOptions#width width}
   *  {@link CodecOptions#height height}
   *  {@link CodecOptions#previousImage previousImage}
   *
   * @see Codec#decompress(RandomAccessInputStream, CodecOptions)
   */
  public byte[] decompress(RandomAccessInputStream in, CodecOptions options)
    throws FormatException, IOException
  {
    if (in == null) 
      throw new IllegalArgumentException("No data to decompress.");
    if (options == null) options = CodecOptions.getDefaultOptions();

    int code = 0;
    short extra = 0;
    int stream = 0;

    int pixelPt = 0;
    int rowPt = (options.height - 1) * options.width;
    int frameSize = options.height * options.width;

    if (options.previousImage == null) {
      options.previousImage = new byte[frameSize];
    }

    while (rowPt >= 0 && in.getFilePointer() < in.length() &&
      pixelPt < options.previousImage.length)
    {
      stream = in.read() & 0xff;
      code = stream;

      if (code == 0) {
        stream = in.read() & 0xff;
        if (stream == 0) {
          rowPt -= options.width;
          pixelPt = 0;
        }
        else if (stream == 1) return options.previousImage;
        else if (stream == 2) {
          stream = in.read() & 0xff;
          pixelPt += stream;
          stream = in.read() & 0xff;
          rowPt -= stream * options.width;
        }
        else {
          if ((rowPt + pixelPt + stream > frameSize) || (rowPt < 0)) {
            return options.previousImage;
          }

          code = stream;
          extra = (short) (stream & 0x01);
          if (stream + code + extra > in.length()) return options.previousImage;

          while (code-- > 0) {
            stream = in.read();
            options.previousImage[rowPt + pixelPt] = (byte) stream;
            pixelPt++;
          }
          if (extra != 0) in.skipBytes(1);
        }
      }
      else {
        if ((rowPt + pixelPt + stream > frameSize) || (rowPt < 0)) {
          return options.previousImage;
        }

        stream = in.read();

        while (code-- > 0) {
          options.previousImage[rowPt + pixelPt] = (byte) stream;
          pixelPt++;
        }
      }
    }

    return options.previousImage;
  }

}
