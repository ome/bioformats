//
// PassthroughCodec.java
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

/**
 * A codec which just returns the exact data it was given, performing no
 * compression or decompression.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/codec/PassthroughCodec.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/codec/PassthroughCodec.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class PassthroughCodec extends BaseCodec {

  /* (non-Javadoc)
   * @see loci.formats.codec.BaseCodec#decompress(byte[], loci.formats.codec.CodecOptions)
   */
  @Override
  public byte[] decompress(byte[] data, CodecOptions options)
      throws FormatException {
    return data;
  }

  /* (non-Javadoc)
   * @see loci.formats.codec.BaseCodec#decompress(loci.common.RandomAccessInputStream, loci.formats.codec.CodecOptions)
   */
  @Override
  public byte[] decompress(RandomAccessInputStream in, CodecOptions options)
      throws FormatException, IOException {
    throw new RuntimeException("Not implemented.");
  }

  /* (non-Javadoc)
   * @see loci.formats.codec.Codec#compress(byte[], loci.formats.codec.CodecOptions)
   */
  public byte[] compress(byte[] data, CodecOptions options)
      throws FormatException {
    return data;
  }

}
