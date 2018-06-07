/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;

import loci.common.RandomAccessInputStream;
import loci.formats.FormatException;

/**
 * This is an optimized LZW codec for use with TIFF files.
 * Most of the code is inlined, and specifics of TIFF usage of LZW
 * (known size of decompressor output; possible lengths of LZW codes; specified
 * values for <code>CLEAR</code> and <code>END_OF_INFORMATION</code> codes)
 * are taken in account.
 * <p>
 * Estimating the worst-case size of compressor output:
 * <ul>
 * <li> The worst case means that there is no compression at all, and every
 *      input byte generates code to output.
 * <li> This means that the LZW table will be full (and reset) after reading
 *      each portion of 4096-256-2-1=3837 bytes of input
 *      (first 256 codes are preallocated, 2 codes are used for CLEAR and
 *      END_OF_IFORMATION, 1 code is lost due to original bug in TIFF library
 *      that now is a feature).
 * <li> Each full portion of 3837 byte will produce in output:
 * <ul>
 * <li> 9 bits for CLEAR code;
 * <li> 9*253 + 10*512 + 11*1024 + 12*2048 = 43237 bits for character codes.
 * </ul>
 * <li> Let n=3837, m=(number of bytes in the last incomplete portion),
 *      N=(number of bytes in compressed complete portion with CLEAR code),
 *      M=(number of bytes in compressed last incomplete portion).
 *      We have inequalities:
 * <ul>
 * <li> N <= 1.41 * n
 * <li> M <= 1.41 * m
 * <li> The last incomplete portion should also include CLEAR and
 *      END_OF_INFORMATION codes; they occupy less than 3 bytes.
 * </ul>
 * Thus, we can claim than the number of bytes in compressed output never
 * exceeds 1.41*(number of input bytes)+3.
 * <p>
 *
 * @author Mikhail Kovtun mikhail.kovtun at duke.edu
 */
public class LZWCodec extends WrappedCodec {
  public LZWCodec() {
    super(new ome.codecs.LZWCodec());
  }
}
