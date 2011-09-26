//
// ZlibCodec.java
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

import java.io.EOFException;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.InflaterInputStream;

import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;

/**
 * This class implements ZLIB decompression.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/codec/ZlibCodec.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/codec/ZlibCodec.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class ZlibCodec extends BaseCodec {

  /* @see Codec#compress(byte[], CodecOptions) */
  public byte[] compress(byte[] data, CodecOptions options)
    throws FormatException
  {
    if (data == null || data.length == 0)
      throw new IllegalArgumentException("No data to compress");
    Deflater deflater = new Deflater();
    deflater.setInput(data);
    deflater.finish();
    byte[] buf = new byte[8192];
    ByteVector bytes = new ByteVector();
    int r = 0;
    // compress until eof reached
    while ((r = deflater.deflate(buf, 0, buf.length)) > 0) {
      bytes.add(buf, 0, r);
    }
    return bytes.toByteArray();
  }

  /* @see Codec#decompress(RandomAccessInputStream, CodecOptions) */
  public byte[] decompress(RandomAccessInputStream in, CodecOptions options)
    throws FormatException, IOException
  {
    InflaterInputStream i = new InflaterInputStream(in);
    ByteVector bytes = new ByteVector();
    byte[] buf = new byte[8192];
    int r = 0;
    // read until eof reached
    try {
      while ((r = i.read(buf, 0, buf.length)) > 0) bytes.add(buf, 0, r);
    }
    catch (EOFException e) { }
    return bytes.toByteArray();
  }

}
