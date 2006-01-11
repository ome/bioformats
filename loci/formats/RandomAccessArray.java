//
// RandomAccessArray.java
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

import java.io.*;

/**
 * An extension of RandomAccessFile providing random access into an array of
 * bytes directly from memory. The implemention is very straightforward except
 * for the copyArray methods, which replace readFully, and exist solely because
 * readFully is declared final and thus cannot be overridden.
 *
 * @author Melissa Linkert linkert at cs.wisc.edu
 */
public class RandomAccessArray extends RandomAccessFile {

  // -- Fields --

  private byte[] stream;
  private long fp; // the file pointer
  private long length; // array length
  private static final String EOF = "End of file reached";


  // -- Constructor --

  public RandomAccessArray(String name, String mode)
    throws FileNotFoundException
  {
    super(name, mode);
  }


  // -- Utility methods --

  public void setStream(byte[] b) {
    stream = b;
    fp = 0;
    length = b.length;
  }

  public void close() {
  }

  public long getFilePointer() {
    return fp;
  }

  public long length() {
    return length;
  }

  /** Sets the file pointer. */
  public void seek(long pos) {
    fp = pos;
  }

  /** Sets the array length. */
  public void setLength(long newLength) {
    length = newLength;
  }

  public int skipBytes(int n) {
    int skipped = n;
    if ((n > 0) && (fp < length - n)) {
      fp += n;
    }
    else if (n < 0) {
      skipped = 0;
    }
    else if (fp >= length - n) {
      skipped = (int) (length - fp);
      fp = length;
    }
    return skipped;
  }


  // -- Reading methods --

  /** Reads a byte of data from the array. */
  public int read() {
    if (fp < length) {
      fp++;
      return 0;
    }
    else return -1;
  }

  /** Reads up to b.length bytes of data from the array. */
  public int read(byte[] b) throws IOException {
    if (b == null) throw new NullPointerException();
    if (fp >= length) return -1;
    for (int i=0; i<b.length; i++) {
      if (fp < length) {
        b[i] = stream[(int) fp];
        fp++;
      }
      else return i;
    }
    return b.length;
  }

  /** Reads up to len bytes from the array. */
  public int read(byte[] b, int off, int len) throws IOException {
    if (b == null) throw new NullPointerException();
    if (off >= length) return -1;
    for (int i=0; i<len; i++) {
      if (off < length) {
        b[i] = stream[off+i];
      }
      else return i;
    }
    return len;
  }

  /**
   * This has the same functionality as RandomAccessFile's readFully. The
   * readFully signature is not used because it's final in RandomAccessFile.
   */
  public void copyArray(byte[] b) throws IOException {
    if (fp >= length - b.length) throw new EOFException(EOF);
    if (b == null) throw new NullPointerException();
    System.arraycopy(stream, (int) fp, b, 0, b.length);
    fp += b.length;
  }

  public void copyArray(byte[] b, int off, int len) throws IOException {
    if (off >= length - len) throw new EOFException(EOF);
    if (b == null) throw new NullPointerException();
    System.arraycopy(stream, off, b, 0, b.length);
  }


  // -- Writing methods --

  public void write(int b) {
    if (fp < length) {
      stream[(int) fp] = (byte) b;
    }
    else {
      byte[] temp = new byte[(int) length+1];
      System.arraycopy(stream, 0, temp, 0, (int) length);
      temp[(int) length] = (byte) b;
      stream = new byte[temp.length];
      length = stream.length;
      System.arraycopy(temp, 0, stream, 0, (int) length);
    }
  }

  public void write(byte[] b) {
    if (fp < length - b.length) {
      for (int i=0; i<b.length; i++) {
        stream[(int) fp] = b[i];
        fp++;
      }
    }
    else {
      byte[] temp = new byte[(int) fp+b.length];
      System.arraycopy(stream, 0, temp, 0, (int) length);
      stream = new byte[temp.length];
      length = temp.length;
      System.arraycopy(temp, 0, stream, 0, (int) length);
      for (int i=0; i<b.length; i++) {
        stream[(int) fp] = b[i];
        fp++;
      }
    }
  }

  public void write(byte[] b, int off, int len) {
    if (off < length - len) {
      for (int i=0; i<len; i++) {
        stream[off+i] = b[i];
      }
    }
    else {
      byte[] temp = new byte[off+b.length];
      System.arraycopy(stream, 0, temp, 0, (int) length);
      stream = new byte[temp.length];
      length = temp.length;
      System.arraycopy(temp, 0, stream, 0, (int) length);
      for (int i=0; i<len; i++) {
        stream[off+i] = b[i];
      }
    }
  }

}
