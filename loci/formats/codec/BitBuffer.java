//
// BitBuffer.java
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

import java.util.Random; // used in main method test
import loci.formats.LogTools;

/**
 * A class for reading arbitrary numbers of bits from a byte array.
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/codec/BitBuffer.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/codec/BitBuffer.java">SVN</a></dd></dl>
 *
 * @author Eric Kjellman egkjellman at wisc.edu
 */
public class BitBuffer {

  // Various bitmasks for the 0000xxxx side of a byte
  private static final int[] BACK_MASK = {
    0x00, 0x01, 0x03, 0x07, 0x0F, 0x1F, 0x3F, 0x7F
  };

  // Various bitmasks for the xxxx0000 side of a byte
  private static final int[] FRONT_MASK = {
    0x0000, 0x0080, 0x00C0, 0x00E0, 0x00F0, 0x00F8, 0x00FC, 0x00FE
  };

  private int currentByte;
  private int currentBit;
  private byte[] byteBuffer;
  private int eofByte;
  private boolean eofFlag;

  /**
   * Default constructor.
   */
  public BitBuffer(byte[] byteBuffer) {
    this.byteBuffer = byteBuffer;
    currentByte = 0;
    currentBit = 0;
    eofByte = byteBuffer.length;
  }

  /**
   * Skips a number of bits in the BitBuffer.
   *
   * @param bits Number of bits to skip
   */
  public void skipBits(long bits) {
    if(bits < 0) {
      throw new IllegalArgumentException("Bits to skip may not be negative");
    }

    // handles skipping past eof
    if((long) eofByte * 8 < (long) currentByte * 8 + currentBit + bits) {
      eofFlag = true;
      currentByte = eofByte;
      currentBit = 0;
      return;
    }

    int skipBytes = (int) (bits / 8);
    int skipBits = (int) (bits % 8);
    currentByte += skipBytes;
    currentBit += skipBits;
    if(currentBit >= 8) {
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
    if (bitsToRead == 0) return 0;
    if (eofFlag) return -1; // Already at end of file
    int toStore = 0;
    while (bitsToRead != 0 && !eofFlag) {
      // if we need to read from more than the current byte in the buffer...
      if (bitsToRead >= 8 - currentBit) {
        if (currentBit == 0) {
          // we can read in a whole byte, so we'll do that.
          toStore = toStore << 8;
          int cb = ((int) byteBuffer[currentByte]);
          toStore += (cb<0 ? (int) 256 + cb : (int) cb);
          bitsToRead -= 8;
          currentByte++;
        }
        else {
          // otherwise, only read the appropriate number of bits off the back
          // side of the byte, in order to "finish" the current byte in the
          // buffer.
          toStore = toStore << (8 - currentBit);
          toStore += ((int)
            byteBuffer[currentByte]) & BACK_MASK[8 - currentBit];
          bitsToRead -= (8 - currentBit);
          currentBit = 0;
          currentByte++;
        }
      }
      else {
        // We will be able to finish using the current byte.
        // read the appropriate number of bits off the front side of the byte,
        // then push them into the int.
        toStore = toStore << bitsToRead;
        int cb = ((int) byteBuffer[currentByte]);
        cb = (cb<0 ? (int) 256 + cb : (int) cb);
        toStore += ((cb) & (0x00FF - FRONT_MASK[currentBit])) >>
          (8 - (currentBit + bitsToRead));
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
    LogTools.println("Generating " + trials + " trials.");
    LogTools.println("Writing to byte array");
    // we want the trials to be able to be all possible bit lengths.
    // r.nextInt() by itself is not sufficient... in 50000 trials it would be
    // extremely unlikely to produce bit strings of 1 bit.
    // instead, we randomly choose from 0 to 2^(i % 32).
    // Except, 1 << 31 is a negative number in two's complement, so we make it
    // a random number in the entire range.
    for(int i = 0; i < trials; i++) {
      if(31 == i % 32) {
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
    LogTools.println("Reading from BitBuffer");
    // Randomly skip or read bytes
    for(int i = 0; i < trials; i++) {
      int c = r.nextInt(100);
      if(c > 50) {
        readint = bb.getBits(len[i]);
        if(readint != nums[i]) {
          LogTools.println("Error at #" + i + ": " + readint + " received, " +
            nums[i] + " expected.");
        }
      }
      else {
        bb.skipBits(len[i]);
      }
    }
    // Test reading past end of buffer.
    LogTools.println("Testing end of buffer");
    bb = new BitBuffer(bw.toByteArray());
    // The total length could be mid byte. Add one byte to test.
    bb.skipBits(totallen + 8);
    int read = bb.getBits(1);
    if(-1 != read) {
      LogTools.println("-1 expected at end of buffer, " +
                         read + " received.");
    }
  }
}
