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

/**
 * A class for writing arbitrary numbers of bits to a byte array.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @deprecated Use loci.common.RandomAccessOutputStream instead
 */
public class BitWriter {
  private ome.codecs.BitWriter writer;

  // -- Constructors --

  /** Constructs a new bit writer. */
  public BitWriter() {
    writer = new ome.codecs.BitWriter();
  }

  /** Constructs a new bit writer with the given initial buffer size. */
  public BitWriter(int size) {
    writer = new ome.codecs.BitWriter(size);
  }

  // -- BitWriter API methods --

  /** Writes the given value using the given number of bits. */
  public void write(int value, int numBits) {
    this.writer.write(value, numBits);
  }

  /**
   * Writes the bits represented by a bit string to the buffer.
   *
   * @throws IllegalArgumentException If any characters other than
   *   '0' and '1' appear in the string.
   */
  public void write(String bitString) {
    this.writer.write(bitString);
  }

  /** Gets an array containing all bits written thus far. */
  public byte[] toByteArray() {
    return this.writer.toByteArray();
  }

  ome.codecs.BitWriter getWrapped() {
    return this.writer;
  }

  // -- Main method --

  /** Tests the BitWriter class. */
  public static void main(String[] args) {
    ome.codecs.BitWriter.main(args);
  }

}
