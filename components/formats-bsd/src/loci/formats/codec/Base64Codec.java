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

import java.io.IOException;

import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;

/**
 * Implements encoding (compress) and decoding (decompress) methods
 * for Base64.  This code was adapted from the Jakarta Commons Codec source,
 * http://jakarta.apache.org/commons
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class Base64Codec extends BaseCodec {

  // Base64 alphabet and codes

  private static final byte PAD = (byte) '=';

  private static byte[] base64Alphabet = new byte[255];
  private static byte[] lookupBase64Alphabet = new byte[255];

  static {
    for (int i=0; i<255; i++) {
      base64Alphabet[i] = (byte) -1;
    }
    for (int i = 'Z'; i >= 'A'; i--) {
      base64Alphabet[i] = (byte) (i - 'A');
      lookupBase64Alphabet[i - 'A'] = (byte) i;
    }
    for (int i = 'z'; i >= 'a'; i--) {
      base64Alphabet[i] = (byte) (i - 'a' + 26);
      lookupBase64Alphabet[i - 'a' + 26] = (byte) i;
    }
    for (int i = '9'; i >= '0'; i--) {
      base64Alphabet[i] = (byte) (i - '0' + 52);
      lookupBase64Alphabet[i - '0' + 52] = (byte) i;
    }

    base64Alphabet['+'] = 62;
    base64Alphabet['/'] = 63;

    lookupBase64Alphabet[62] = (byte) '+';
    lookupBase64Alphabet[63] = (byte) '/';
  }

  /* @see Codec#compress(byte[], CodecOptions) */
  @Override
  public byte[] compress(byte[] input, CodecOptions options)
    throws FormatException
  {
    if (input == null || input.length == 0) return null;
    int dataBits = input.length * 8;
    int fewerThan24 = dataBits % 24;
    int numTriples = dataBits / 24;
    ByteVector encoded = new ByteVector();

    byte k, l, b1, b2, b3;

    int dataIndex = 0;

    for (int i=0; i<numTriples; i++) {
      dataIndex = i * 3;
      b1 = input[dataIndex];
      b2 = input[dataIndex + 1];
      b3 = input[dataIndex + 2];

      l = (byte) (b2 & 0x0f);
      k = (byte) (b1 & 0x03);

      byte v1 = ((b1 & -128) == 0) ? (byte) (b1 >> 2) :
        (byte) ((b1) >> 2 ^ 0xc0);
      byte v2 = ((b2 & -128) == 0) ? (byte) (b2 >> 4) :
        (byte) ((b2) >> 4 ^ 0xf0);
      byte v3 = ((b3 & -128) == 0) ? (byte) (b3 >> 6) :
        (byte) ((b3) >> 6 ^ 0xfc);

      encoded.add(lookupBase64Alphabet[v1]);
      encoded.add(lookupBase64Alphabet[v2 | (k << 4)]);
      encoded.add(lookupBase64Alphabet[(l << 2) | v3]);
      encoded.add(lookupBase64Alphabet[b3 & 0x3f]);
    }

    dataIndex = numTriples * 3;

    if (fewerThan24 == 8) {
      b1 = input[dataIndex];
      k = (byte) (b1 & 0x03);
      byte v = ((b1 & -128) == 0) ? (byte) (b1 >> 2) :
        (byte) ((b1) >> 2 ^ 0xc0);
      encoded.add(lookupBase64Alphabet[v]);
      encoded.add(lookupBase64Alphabet[k << 4]);
      encoded.add(PAD);
      encoded.add(PAD);
    }
    else if (fewerThan24 == 16) {
      b1 = input[dataIndex];
      b2 = input[dataIndex + 1];
      l = (byte) (b2 & 0x0f);
      k = (byte) (b1 & 0x03);

      byte v1 = ((b1 & -128) == 0) ? (byte) (b1 >> 2) :
        (byte) ((b1) >> 2 ^ 0xc0);
      byte v2 = ((b2 & -128) == 0) ? (byte) (b2 >> 4) :
        (byte) ((b2) >> 4 ^ 0xf0);

      encoded.add(lookupBase64Alphabet[v1]);
      encoded.add(lookupBase64Alphabet[v2 | (k << 4)]);
      encoded.add(lookupBase64Alphabet[l << 2]);
      encoded.add(PAD);
    }

    return encoded.toByteArray();
  }

  /* @see Codec#decompress(RandomAccessInputStream, CodecOptions) */
  @Override
  public byte[] decompress(RandomAccessInputStream in, CodecOptions options)
    throws FormatException, IOException
  {
    if (in == null)
      throw new IllegalArgumentException("No data to decompress.");
    if (in.length() == 0) return new byte[0];

    byte b3 = 0, b4 = 0, marker0 = 0, marker1 = 0;

    ByteVector decodedData = new ByteVector();

    byte[] block = new byte[8192];
    int nRead = in.read(block);
    int p = 0;
    byte b1 = base64Alphabet[block[p++]];
    byte b2 = base64Alphabet[block[p++]];

    while (b1 != -1 && b2 != -1 &&
      (in.getFilePointer() - nRead + p < in.length()))
    {
      marker0 = block[p++];
      marker1 = block[p++];

      if (p == block.length) {
        nRead = in.read(block);
        p = 0;
      }

      decodedData.add((byte) (b1 << 2 | b2 >> 4));
      if (p >= nRead && in.getFilePointer() >= in.length()) break;
      if (marker0 != PAD && marker1 != PAD) {
        b3 = base64Alphabet[marker0];
        b4 = base64Alphabet[marker1];

        decodedData.add((byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf)));
        decodedData.add((byte) (b3 << 6 | b4));
      }
      else if (marker0 == PAD) {
        decodedData.add((byte) 0);
        decodedData.add((byte) 0);
      }
      else if (marker1 == PAD) {
        b3 = base64Alphabet[marker0];

        decodedData.add((byte) (((b2 & 0xf) << 4) | ((b3 >> 2) & 0xf)));
        decodedData.add((byte) 0);
      }
      b1 = base64Alphabet[block[p++]];
      b2 = base64Alphabet[block[p++]];
    }
    return decodedData.toByteArray();
  }

}
