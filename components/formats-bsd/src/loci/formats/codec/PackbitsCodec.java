/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;
import loci.formats.UnsupportedCompressionException;

/**
 * This class implements packbits decompression. Compression is not yet
 * implemented.
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class PackbitsCodec extends BaseCodec {

  /* @see Codec#compress(byte[], CodecOptions) */
  @Override
  public byte[] compress(byte[] data, CodecOptions options)
    throws FormatException
  {
    // TODO: Add compression support.
    throw new UnsupportedCompressionException(
      "Packbits Compression not currently supported");
  }

  /**
   * The CodecOptions parameter should have the following fields set:
   *  {@link CodecOptions#maxBytes maxBytes}
   *
   * @see Codec#decompress(RandomAccessInputStream, CodecOptions)
   */
  @Override
  public byte[] decompress(RandomAccessInputStream in, CodecOptions options)
    throws FormatException, IOException
  {
    if (options == null) options = CodecOptions.getDefaultOptions();
    if (in == null) 
      throw new IllegalArgumentException("No data to decompress.");
    long fp = in.getFilePointer();
    // Adapted from the TIFF 6.0 specification, page 42.
    ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
    int nread = 0;
    BufferedInputStream s = new BufferedInputStream(in, 262144);
    while (output.size() < options.maxBytes) {
      byte n = (byte) (s.read() & 0xff);
      nread++;
      if (n >= 0) { // 0 <= n <= 127
        byte[] b = new byte[n + 1];
        s.read(b);
        nread += n + 1;
        output.write(b);
        b = null;
      }
      else if (n != -128) { // -127 <= n <= -1
        int len = -n + 1;
        byte inp = (byte) (s.read() & 0xff);
        nread++;
        for (int i=0; i<len; i++) output.write(inp);
      }
    }
    if (fp + nread < in.length()) in.seek(fp + nread);
    return output.toByteArray();
  }
}
