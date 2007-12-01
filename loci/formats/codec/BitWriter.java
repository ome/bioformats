//
// BitWriter.java
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

import java.util.*;
import loci.formats.LogTools;

/**
 * A class for writing arbitrary numbers of bits to a byte array.
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/codec/BitWriter.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/codec/BitWriter.java">SVN</a></dd></dl>
 *
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
  public BitWriter() { this(10); }

  /** Constructs a new bit writer with the given initial buffer size. */
  public BitWriter(int size) { buf = new byte[size]; }

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

  /**
   *  Writes the bits represented by a bit string to the buffer.
   *  All characters in the string must be 0 or 1, or this will
   *  throw an IllegalArgumentException */

  public void write(String bitString) {
    for (int i = 0; i < bitString.length(); i++) {
      if ('1' == bitString.charAt(i)) {
        int b = 1 << (7 - bit);
        buf[index] |= b;
      }
      else if ('0' != bitString.charAt(i)) {
        throw new IllegalArgumentException(bitString.charAt(i) +
          "found at character " + i +
          "; 0 or 1 expected. Write only partially completed.");
      }
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
    LogTools.println("Generating random list of " + max + " values");
    int[] values = new int[max];
    int[] bits = new int[max];
    double log2 = Math.log(2);
    for (int i=0; i<values.length; i++) {
      values[i] = (int) (50000 * Math.random()) + 1;
      int minBits = (int) Math.ceil(Math.log(values[i] + 1) / log2);
      bits[i] = (int) (10 * Math.random()) + minBits;
    }

    // write values out
    LogTools.println("Writing values to byte array");
    BitWriter out = new BitWriter();
    for (int i=0; i<values.length; i++) out.write(values[i], bits[i]);

    // read values back in
    LogTools.println("Reading values from byte array");
    BitBuffer bb = new BitBuffer(out.toByteArray());
    for (int i=0; i<values.length; i++) {
      int value = bb.getBits(bits[i]);
      if (value != values[i]) {
        LogTools.println("Value #" + i + " does not match (got " +
          value + "; expected " + values[i] + "; " + bits[i] + " bits)");
      }
    }

    // Testing string functionality
    Random r = new Random();
    LogTools.println("Generating 5000 random bits for String test");
    StringBuffer sb = new StringBuffer(5000);
    for (int i = 0; i < 5000; i++) {
      sb.append(r.nextInt(2));
    }
    out = new BitWriter();
    LogTools.println("Writing values to byte array");
    out.write(sb.toString());
    LogTools.println("Reading values from byte array");
    bb = new BitBuffer(out.toByteArray());
    for (int i = 0; i < 5000; i++) {
      int value = bb.getBits(1);
      int expected = (sb.charAt(i) == '1') ? 1 : 0;
      if (value != expected) {
        LogTools.println("Bit #" + i + " does not match (got " + value +
          "; expected " + expected + ".");
      }
    }
  }

}
