//
// BitBuffer.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2006 Melissa Linkert, Curtis Rueden and Eric Kjellman.

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

package loci.formats;

/**
 * A class for reading arbitrary numbers of bits from a byte array.
 * @author Eric Kjellman egkjellman at wisc.edu
 */
public class BitBuffer {

  private static final int[] BACK_MASK =
    {0x0000, 0x0001, 0x0003, 0x0007, 0x000F, 0x001F, 0x003F, 0x007F};

  private static final int[] FRONT_MASK =
    {0x0000, 0x0080, 0x00C0, 0x00E0, 0x00F0, 0x00F8, 0x00FC, 0x00FE};

  private int currentByte;
  private int currentBit;
  private byte[] byteBuffer;
  private int eofByte;
  private boolean eofFlag;

  public BitBuffer(byte[] byteBuffer) {
    this.byteBuffer = byteBuffer;
    currentByte = 0;
    currentBit = 0;
    eofByte = byteBuffer.length;
  }

  public int getBits(int bitsToRead) {
    if (bitsToRead == 0) return 0;
    if (eofFlag) return -1; // Already at end of file
    int toStore = 0;
    while (bitsToRead != 0 && !eofFlag) {
      if (bitsToRead >= 8 - currentBit) {
        if (currentBit == 0) { // special
          toStore = toStore << 8;
          int cb = ((int) byteBuffer[currentByte]);
          toStore += (cb<0 ? (int) 256 + cb : (int) cb);
          bitsToRead -= 8;
          currentByte++;
        }
        else {
          toStore = toStore << (8 - currentBit);
          toStore += ((int)
            byteBuffer[currentByte]) & BACK_MASK[8 - currentBit];
          bitsToRead -= (8 - currentBit);
          currentBit = 0;
          currentByte++;
        }
      }
      else {
        toStore = toStore << bitsToRead;
        int cb = ((int) byteBuffer[currentByte]);
        cb = (cb<0 ? (int) 256 + cb : (int) cb);
        toStore += ((cb) & (0x00FF - FRONT_MASK[currentBit])) >>
          (8 - (currentBit + bitsToRead));
        currentBit += bitsToRead;
        bitsToRead = 0;
      }
      if (currentByte == eofByte) {
        eofFlag = true;
        return toStore;
      }
    }
    return toStore;
  }

}
