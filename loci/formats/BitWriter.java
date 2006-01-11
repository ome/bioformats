//
// BitWriter.java
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
 * A class for writing arbitrary numbers of bits to a byte array.
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class BitWriter {

  // -- Fields --

  /** Buffer storing all bits written thus far. */
  private byte[] buf;

  /** Byte index into the buffer. */
  private int index;

  /** Bit index into current byte of the buffer. */
  private int bit;


  // -- Constructors --

  /** Constructs a new bit writer. */
  public BitWriter() {
    this(10);
  }

  /** Constructs a new bit writer with the given initial buffer size. */
  public BitWriter(int size) {
    buf = new byte[size];
  }


  // -- BitWriter API methods --

  /** Writes the given value using the given number of bits. */
  public void write(int value, int numBits) {
    byte[] bits = new byte[numBits];
    for (int i=0; i<numBits; i++) {
      bits[i] = (byte) (value & 0x0001);
      value >>= 1;
    }
    for (int i=numBits-1; i>=0; i--) {
      int b = bits[i] << (7 - bit);
      buf[index] |= b;
      bit++;
      if (bit > 7) {
        bit = 0;
        index++;
        if (index >= buf.length) {
          // buffer is full; increase the size
          byte[] newBuf = new byte[buf.length * 2];
          System.arraycopy(buf, 0, newBuf, 0, buf.length);
          buf = newBuf;
        }
      }
    }
  }

  /** Gets an array containing all bits written thus far. */
  public byte[] toByteArray() {
    int size = index;
    if (bit > 0) size++;
    byte[] b = new byte[size];
    System.arraycopy(buf, 0, b, 0, size);
    return b;
  }


  // -- Main method --

  /** Tests the BitWriter class. */
  public static void main(String[] args) {
    int max = 50000;

    // randomize values
    System.out.println("Generating random list of " + max + " values");
    int[] values = new int[max];
    int[] bits = new int[max];
    double log2 = Math.log(2);
    for (int i=0; i<values.length; i++) {
      values[i] = (int) (50000 * Math.random()) + 1;
      int minBits = (int) Math.ceil(Math.log(values[i] + 1) / log2);
      bits[i] = (int) (10 * Math.random()) + minBits;
    }

    // write values out
    System.out.println("Writing values to byte array");
    BitWriter out = new BitWriter();
    for (int i=0; i<values.length; i++) out.write(values[i], bits[i]);

    // read values back in
    System.out.println("Reading values from byte array");
    BitBuffer bb = new BitBuffer(out.toByteArray());
    for (int i=0; i<values.length; i++) {
      int value = bb.getBits(bits[i]);
      if (value != values[i]) {
        System.out.println("Value #" + i + " does not match (got " +
          value + "; expected " + values[i] + "; " + bits[i] + " bits)");
      }
    }
  }
}
