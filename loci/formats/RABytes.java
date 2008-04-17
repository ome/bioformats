//
// RABytes.java
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

package loci.formats;

import java.io.*;

/**
 * A wrapper for a byte array that implements the IRandomAccess interface.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/RABytes.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/RABytes.java">SVN</a></dd></dl>
 *
 * @see IRandomAccess
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class RABytes implements IRandomAccess {

  // -- Fields --

  /** The byte array backing this RABytes. */
  protected byte[] array;

  /** The file pointer. */
  protected int fp;

  // -- Constructors --

  /**
   * Creates a random access byte stream to read from, and
   * write to, the bytes specified by the byte[] argument.
   */
  public RABytes(byte[] bytes) {
    array = bytes;
    fp = 0;
  }

  // -- RABytes API methods --

  /** Gets the byte array backing this RAFile. */
  public byte[] getBytes() { return array; }

  // -- IRandomAccess API methods --

  /* @see IRandomAccess.close() */
  public void close() { }

  /* @see IRandomAccess.getFilePointer() */
  public long getFilePointer() {
    return fp;
  }

  /* @see IRandomAccess.length() */
  public long length() {
    return array.length;
  }

  /* @see IRandomAccess.read() */
  public int read() {
    return fp < array.length ? array[fp++] : 0;
  }

  /* @see IRandomAccess.read(byte[]) */
  public int read(byte[] b) throws IOException {
    return read(b, 0, b.length);
  }

  /* @see IRandomAccess.read(byte[], int, int) */
  public int read(byte[] b, int off, int len) throws IOException {
    if (fp + len > array.length) len = array.length - fp;
    System.arraycopy(array, fp, b, off, len);
    fp += len;
    return len;
  }

  /* @see IRandomAccess.seek(long) */
  public void seek(long pos) throws IOException {
    if (pos < 0) throw new IOException("pos < 0");
    if (pos > Integer.MAX_VALUE) throw new IOException("pos is too large");
    fp = (int) pos;
  }

  /* @see IRandomAccess.setLength(long) */
  public void setLength(long newLength) throws IOException {
    if (newLength > Integer.MAX_VALUE) {
      throw new IOException("newLength is too large");
    }
    int len = (int) newLength;
    byte[] bytes = new byte[len];
    System.arraycopy(array, 0, bytes, 0,
      array.length < len ? array.length : len);
    array = bytes;
  }

  // -- DataInput API methods --

  /* @see java.io.DataInput.readBoolean() */
  public boolean readBoolean() throws IOException {
    return readByte() != 0;
  }

  /* @see java.io.DataInput.readByte() */
  public byte readByte() throws IOException {
    if (fp + 1 > array.length) throw new EOFException();
    return array[fp++];
  }

  /* @see java.io.DataInput.readChar() */
  public char readChar() throws IOException {
    if (fp + 2 > array.length) throw new EOFException();
    char c = (char) DataTools.bytesToShort(array, fp, false);
    fp += 2;
    return c;
  }

  /* @see java.io.DataInput.readDouble() */
  public double readDouble() throws IOException {
    if (fp + 8 > array.length) throw new EOFException();
    double d = Double.longBitsToDouble(DataTools.bytesToLong(array, fp, false));
    fp += 8;
    return d;
  }

  /* @see java.io.DataInput.readFloat() */
  public float readFloat() throws IOException {
    if (fp + 4 > array.length) throw new EOFException();
    float f = Float.intBitsToFloat(DataTools.bytesToInt(array, fp, false));
    fp += 4;
    return f;
  }

  /* @see java.io.DataInput.readFully(byte[]) */
  public void readFully(byte[] b) throws IOException {
    readFully(b, 0, b.length);
  }

  /* @see java.io.DataInput.readFully(byte[], int, int) */
  public void readFully(byte[] b, int off, int len) throws IOException {
    if (fp + len > array.length) throw new EOFException();
    System.arraycopy(array, fp, b, off, len);
    fp += len;
  }

  /* @see java.io.DataInput.readInt() */
  public int readInt() throws IOException {
    if (fp + 4 > array.length) throw new EOFException();
    int i = DataTools.bytesToInt(array, fp, false);
    fp += 4;
    return i;
  }

  /* @see java.io.DataInput.readLine() */
  public String readLine() throws IOException {
    throw new IOException("Unimplemented");
  }

  /* @see java.io.DataInput.readLong() */
  public long readLong() throws IOException {
    if (fp + 8 > array.length) throw new EOFException();
    long l = DataTools.bytesToLong(array, fp, false);
    fp += 8;
    return l;
  }

  /* @see java.io.DataInput.readShort() */
  public short readShort() throws IOException {
    if (fp + 2 > array.length) throw new EOFException();
    short s = DataTools.bytesToShort(array, fp, false);
    fp += 2;
    return s;
  }

  /* @see java.io.DataInput.readUnsignedByte() */
  public int readUnsignedByte() throws IOException {
    if (fp + 1 > array.length) throw new EOFException();
    return DataTools.bytesToInt(array, fp++, 1, false);
  }

  /* @see java.io.DataInput.readUnsignedShort() */
  public int readUnsignedShort() throws IOException {
    if (fp + 2 > array.length) throw new EOFException();
    int i = DataTools.bytesToInt(array, fp, 2, false);
    fp += 2;
    return i;
  }

  /* @see java.io.DataInput.readUTF() */
  public String readUTF() throws IOException {
    throw new IOException("Unimplemented");
  }

  /* @see java.io.DataInput.skipBytes(int) */
  public int skipBytes(int n) {
    if (n < 0) n = 0;
    if (fp + n > array.length) n = array.length - fp;
    fp += n;
    return n;
  }

  // -- DataOutput API metthods --

  /* @see java.io.DataOutput.write(byte[]) */
  public void write(byte[] b) throws IOException {
    write(b, 0, b.length);
  }

  /* @see java.io.DataOutput.write(byte[], int, int) */
  public void write(byte[] b, int off, int len) throws IOException {
    if (fp + len > array.length) setLength(fp + len);
    System.arraycopy(b, off, array, fp, len);
    fp += b.length;
  }

  /* @see java.io.DataOutput.write(int b) */
  public void write(int b) throws IOException {
    if (fp + 1 > array.length) setLength(fp + 1);
    array[fp++] = (byte) b;
  }

  /* @see java.io.DataOutput.writeBoolean(boolean) */
  public void writeBoolean(boolean v) throws IOException {
    write(v ? 1 : 0);
  }

  /* @see java.io.DataOutput.writeByte(int) */
  public void writeByte(int v) throws IOException {
    write(v);
  }

  /* @see java.io.DataOutput.writeBytes(String) */
  public void writeBytes(String s) throws IOException {
    char[] c = s.toCharArray();
    byte[] b = new byte[c.length];
    for (int i=0; i<c.length; i++) b[i] = (byte) c[i];
    write(b);
  }

  /* @see java.io.DataOutput.writeChar(int) */
  public void writeChar(int v) throws IOException {
    if (fp + 2 > array.length) setLength(fp + 2);
    array[fp++] = (byte) (0xff & (v >> 8));
    array[fp++] = (byte) (0xff & v);
  }

  /* @see java.io.DataOutput.writeChars(String) */
  public void writeChars(String s) throws IOException {
    int len = 2 * s.length();
    if (fp + len > array.length) setLength(fp + len);
    char[] c = s.toCharArray();
    for (int i=0; i<c.length; i++) {
      char v = c[i];
      array[fp++] = (byte) (0xff & (v >> 8));
      array[fp++] = (byte) (0xff & v);
    }
  }

  /* @see java.io.DataOutput.writeDouble(double) */
  public void writeDouble(double v) throws IOException {
    writeLong(Double.doubleToLongBits(v));
  }

  /* @see java.io.DataOutput.writeFloat(float) */
  public void writeFloat(float v) throws IOException {
    writeInt(Float.floatToIntBits(v));
  }

  /* @see java.io.DataOutput.writeInt(int) */
  public void writeInt(int v) throws IOException {
    if (fp + 4 > array.length) setLength(fp + 4);
    array[fp++] = (byte) (0xff & (v >> 24));
    array[fp++] = (byte) (0xff & (v >> 16));
    array[fp++] = (byte) (0xff & (v >> 8));
    array[fp++] = (byte) (0xff & v);
  }

  /* @see java.io.DataOutput.writeLong(long) */
  public void writeLong(long v) throws IOException {
    if (fp + 8 > array.length) setLength(fp + 8);
    array[fp++] = (byte) (0xff & (v >> 56));
    array[fp++] = (byte) (0xff & (v >> 48));
    array[fp++] = (byte) (0xff & (v >> 40));
    array[fp++] = (byte) (0xff & (v >> 32));
    array[fp++] = (byte) (0xff & (v >> 24));
    array[fp++] = (byte) (0xff & (v >> 16));
    array[fp++] = (byte) (0xff & (v >> 8));
    array[fp++] = (byte) (0xff & v);
  }

  /* @see java.io.DataOutput.writeShort(int) */
  public void writeShort(int v) throws IOException {
    if (fp + 2 > array.length) setLength(fp + 2);
    array[fp++] = (byte) (0xff & (v >> 24));
    array[fp++] = (byte) (0xff & (v >> 16));
    array[fp++] = (byte) (0xff & (v >> 8));
    array[fp++] = (byte) (0xff & v);
  }

  /* @see java.io.DataOutput.writeUTF(String)  */
  public void writeUTF(String str) throws IOException {
    throw new IOException("Unimplemented");
  }

}
