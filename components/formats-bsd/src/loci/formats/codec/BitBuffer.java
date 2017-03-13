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

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class for reading arbitrary numbers of bits from a byte array.
 *
 * @author Eric Kjellman egkjellman at wisc.edu
 * @deprecated Use loci.common.RandomAccessInputStream instead
 */
public class BitBuffer {

  // -- Constants --

  private static final Logger LOGGER = LoggerFactory.getLogger(BitBuffer.class);

  /** Various bitmasks for the 0000xxxx side of a byte. */
  private static final int[] BACK_MASK = {
    0x00, // 00000000
    0x01, // 00000001
    0x03, // 00000011
    0x07, // 00000111
    0x0F, // 00001111
    0x1F, // 00011111
    0x3F, // 00111111
    0x7F  // 01111111
  };

  /** Various bitmasks for the xxxx0000 side of a byte. */
  private static final int[] FRONT_MASK = {
    0x0000, // 00000000
    0x0080, // 10000000
    0x00C0, // 11000000
    0x00E0, // 11100000
    0x00F0, // 11110000
    0x00F8, // 11111000
    0x00FC, // 11111100
    0x00FE  // 11111110
  };

  private byte[] byteBuffer;
  private int currentByte;
  private int currentBit;
  private int eofByte;
  private boolean eofFlag;

  /** Default constructor. */
  public BitBuffer(byte[] byteBuffer) {
    this.byteBuffer = byteBuffer;
    currentByte = 0;
    currentBit = 0;
    eofByte = byteBuffer.length;
  }

  /** Return the backing byte array. */
  public byte[] getByteBuffer() {
    return byteBuffer;
  }

  /**
   * Skips a number of bits in the BitBuffer.
   *
   * @param bits Number of bits to skip
   */
  public void skipBits(long bits) {
    if (bits < 0) {
      throw new IllegalArgumentException("Bits to skip may not be negative");
    }

    // handles skipping past eof
    if ((long) eofByte * 8 < (long) currentByte * 8 + currentBit + bits) {
      eofFlag = true;
      currentByte = eofByte;
      currentBit = 0;
      return;
    }

    int skipBytes = (int) (bits / 8);
    int skipBits = (int) (bits % 8);
    currentByte += skipBytes;
    currentBit += skipBits;
    while (currentBit >= 8) {
      currentByte++;
      currentBit -= 8;
    }
  }

  /**
   * Returns an int value representing the value of the bits read from
   * the byte array, from the current position. Bits are extracted from the
   * "left side" or high side of the byte.<p>
   * The current position is modified by this call.<p>
   * Bits are pushed into the int from the right, endianness is not
   * considered by the method on its own. So, if 5 bits were read from the
   * buffer "10101", the int would be the integer representation of
   * 000...0010101 on the target machine. <p>
   * In general, this also means the result will be positive unless a full
   * 32 bits are read. <p>
   * Requesting more than 32 bits is allowed, but only up to 32 bits worth of
   * data will be returned (the last 32 bits read). <p>
   *
   * @param bitsToRead the number of bits to read from the bit buffer
   * @return the value of the bits read
   */
  public int getBits(int bitsToRead) {
    if (bitsToRead < 0) {
      throw new IllegalArgumentException("Bits to read may not be negative");
    }
    if (bitsToRead == 0) return 0;
    if (eofFlag) return -1; // Already at end of file
    int toStore = 0;
    while (bitsToRead != 0 && !eofFlag) {
      if (currentBit < 0 || currentBit > 7) {
        throw new IllegalStateException("byte=" +
          currentByte + ", bit = " + currentBit);
      }

      // if we need to read from more than the current byte in the buffer...
      int bitsLeft = 8 - currentBit;
      if (bitsToRead >= bitsLeft) {
        toStore <<= bitsLeft;
        bitsToRead -= bitsLeft;
        int cb = (int) byteBuffer[currentByte];
        if (currentBit == 0) {
          // we can read in a whole byte, so we'll do that.
          toStore += cb & 0xff;
        }
        else {
          // otherwise, only read the appropriate number of bits off the back
          // side of the byte, in order to "finish" the current byte in the
          // buffer.
          toStore += cb & BACK_MASK[bitsLeft];
          currentBit = 0;
        }
        currentByte++;
      }
      else {
        // We will be able to finish using the current byte.
        // read the appropriate number of bits off the front side of the byte,
        // then push them into the int.
        toStore = toStore << bitsToRead;
        int cb = byteBuffer[currentByte] & 0xff;
        toStore += (cb & (0x00FF - FRONT_MASK[currentBit])) >>
          (bitsLeft - bitsToRead);
        currentBit += bitsToRead;
        bitsToRead = 0;
      }
      // If we reach the end of the buffer, return what we currently have.
      if (currentByte == eofByte) {
        eofFlag = true;
        return toStore;
      }
    }
    return toStore;
  }

  /**
   * Checks if the current position is on a byte boundary, that is the next
   * bit in the byte array is the first bit in a byte.
   *
   * @return true if bit is on byte boundary, false otherwise.
   */
  public boolean isBitOnByteBoundary() {
    return currentBit % 8 == 0 ? true : false;
  }

  /**
   * Testing method.
   * @param args Ignored.
   */
  public static void main(String[] args) {
    int trials = 50000;
    int[] nums = new int[trials];
    int[] len = new int[trials];
    BitWriter bw = new BitWriter();
    int totallen = 0;

    Random r = new Random();
    LOGGER.info("Generating {} trials.", trials);
    LOGGER.info("Writing to byte array");
    // we want the trials to be able to be all possible bit lengths.
    // r.nextInt() by itself is not sufficient... in 50000 trials it would be
    // extremely unlikely to produce bit strings of 1 bit.
    // instead, we randomly choose from 0 to 2^(i % 32).
    // Except, 1 << 31 is a negative number in two's complement, so we make it
    // a random number in the entire range.
    for (int i = 0; i < trials; i++) {
      if (31 == i % 32) {
        nums[i] = r.nextInt();
      }
      else {
        nums[i] = r.nextInt(1 << (i % 32));
      }
      // How many bits are required to represent this number?
      len[i] = (Integer.toBinaryString(nums[i])).length();
      totallen += len[i];
      bw.write(nums[i], len[i]);
    }
    BitBuffer bb = new BitBuffer(bw.toByteArray());
    int readint;
    LOGGER.info("Reading from BitBuffer");
    // Randomly skip or read bytes
    for (int i = 0; i < trials; i++) {
      int c = r.nextInt(100);
      if (c > 50) {
        readint = bb.getBits(len[i]);
        if (readint != nums[i]) {
          LOGGER.info("Error at #{}: {} received, {} expected.",
            new Object[] {i, readint, nums[i]});
        }
      }
      else {
        bb.skipBits(len[i]);
      }
    }

    // Test reading past end of buffer.
    LOGGER.info("Testing end of buffer");
    bb = new BitBuffer(bw.toByteArray());
    // The total length could be mid byte. Add one byte to test.
    bb.skipBits(totallen + 8);
    int read = bb.getBits(1);
    if (-1 != read) {
      LOGGER.info("-1 expected at end of buffer, {} received.", read);
    }

    // Test byte boundary detection
    LOGGER.info("Testing byte boundary detection");
    bb = new BitBuffer(bw.toByteArray());
    for (int i = 0; i < trials; i++) {
      int c = r.nextInt(100);
      if (c > 50) {
        if (len[i] > 8) {
          bb.getBits(8);
          if (!bb.isBitOnByteBoundary()){
            LOGGER.info("Bit on byte boundary expected, but not returned.");
          }
        }
      }
    }
  }
}
