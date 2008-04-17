//
// RAFile.java
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
 * A wrapper for RandomAccessFile that implements the IRandomAccess interface.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/RAFile.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/RAFile.java">SVN</a></dd></dl>
 *
 * @see IRandomAccess
 * @see java.io.RandomAccessFile
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class RAFile implements IRandomAccess {

  // -- Fields --

  /** The random access file object backing this RAFile. */
  protected RandomAccessFile raf;

  // -- Constructors --

  /**
   * Creates a random access file stream to read from, and
   * optionally to write to, the file specified by the File argument.
   */
  public RAFile(File file, String mode) throws FileNotFoundException {
    raf = new RandomAccessFile(file, mode);
  }

  /**
   * Creates a random access file stream to read from, and
   * optionally to write to, a file with the specified name.
   */
  public RAFile(String name, String mode) throws FileNotFoundException {
    raf = new RandomAccessFile(name, mode);
  }

  // -- RAFile API methods --

  /** Gets the random access file object backing this RAFile. */
  public RandomAccessFile getRandomAccessFile() { return raf; }

  // -- IRandomAccess API methods --

  /* @see IRandomAccess.close() */
  public void close() throws IOException {
    raf.close();
  }

  /* @see IRandomAccess.getFilePointer() */
  public long getFilePointer() throws IOException {
    return raf.getFilePointer();
  }

  /* @see IRandomAccess.length() */
  public long length() throws IOException {
    return raf.length();
  }

  /* @see IRandomAccess.read() */
  public int read() throws IOException {
    return raf.read();
  }

  /* @see IRandomAccess.read(byte[]) */
  public int read(byte[] b) throws IOException {
    return raf.read(b);
  }

  /* @see IRandomAccess.read(byte[], int, int) */
  public int read(byte[] b, int off, int len) throws IOException {
    return raf.read(b, off, len);
  }

  /* @see IRandomAccess.seek(long) */
  public void seek(long pos) throws IOException {
    raf.seek(pos);
  }

  /* @see IRandomAccess.setLength(long) */
  public void setLength(long newLength) throws IOException {
    raf.setLength(newLength);
  }

  // -- DataInput API methods --

  /* @see java.io.DataInput.readBoolean() */
  public boolean readBoolean() throws IOException {
    return raf.readBoolean();
  }

  /* @see java.io.DataInput.readByte() */
  public byte readByte() throws IOException {
    return raf.readByte();
  }

  /* @see java.io.DataInput.readChar() */
  public char readChar() throws IOException {
    return raf.readChar();
  }

  /* @see java.io.DataInput.readDouble() */
  public double readDouble() throws IOException {
    return raf.readDouble();
  }

  /* @see java.io.DataInput.readFloat() */
  public float readFloat() throws IOException {
    return raf.readFloat();
  }

  /* @see java.io.DataInput.readFully(byte[]) */
  public void readFully(byte[] b) throws IOException {
    raf.readFully(b);
  }

  /* @see java.io.DataInput.readFully(byte[], int, int) */
  public void readFully(byte[] b, int off, int len) throws IOException {
    raf.readFully(b, off, len);
  }

  /* @see java.io.DataInput.readInt() */
  public int readInt() throws IOException {
    return raf.readInt();
  }

  /* @see java.io.DataInput.readLine() */
  public String readLine() throws IOException {
    return raf.readLine();
  }

  /* @see java.io.DataInput.readLong() */
  public long readLong() throws IOException {
    return raf.readLong();
  }

  /* @see java.io.DataInput.readShort() */
  public short readShort() throws IOException {
    return raf.readShort();
  }

  /* @see java.io.DataInput.readUnsignedByte() */
  public int readUnsignedByte() throws IOException {
    return raf.readUnsignedByte();
  }

  /* @see java.io.DataInput.readUnsignedShort() */
  public int readUnsignedShort() throws IOException {
    return raf.readUnsignedShort();
  }

  /* @see java.io.DataInput.readUTF() */
  public String readUTF() throws IOException {
    return raf.readUTF();
  }

  /* @see java.io.DataInput.skipBytes(int) */
  public int skipBytes(int n) throws IOException {
    return raf.skipBytes(n);
  }

  // -- DataOutput API metthods --

  /* @see java.io.DataOutput.write(byte[]) */
  public void write(byte[] b) throws IOException {
    raf.write(b);
  }

  /* @see java.io.DataOutput.write(byte[], int, int) */
  public void write(byte[] b, int off, int len) throws IOException {
    raf.write(b, off, len);
  }

  /* @see java.io.DataOutput.write(int b) */
  public void write(int b) throws IOException {
    raf.write(b);
  }

  /* @see java.io.DataOutput.writeBoolean(boolean) */
  public void writeBoolean(boolean v) throws IOException {
    raf.writeBoolean(v);
  }

  /* @see java.io.DataOutput.writeByte(int) */
  public void writeByte(int v) throws IOException {
    raf.writeByte(v);
  }

  /* @see java.io.DataOutput.writeBytes(String) */
  public void writeBytes(String s) throws IOException {
    raf.writeBytes(s);
  }

  /* @see java.io.DataOutput.writeChar(int) */
  public void writeChar(int v) throws IOException {
    raf.writeChar(v);
  }

  /* @see java.io.DataOutput.writeChars(String) */
  public void writeChars(String s) throws IOException {
    raf.writeChars(s);
  }

  /* @see java.io.DataOutput.writeDouble(double) */
  public void writeDouble(double v) throws IOException {
    raf.writeDouble(v);
  }

  /* @see java.io.DataOutput.writeFloat(float) */
  public void writeFloat(float v) throws IOException {
    raf.writeFloat(v);
  }

  /* @see java.io.DataOutput.writeInt(int) */
  public void writeInt(int v) throws IOException {
    raf.writeInt(v);
  }

  /* @see java.io.DataOutput.writeLong(long) */
  public void writeLong(long v) throws IOException {
    raf.writeLong(v);
  }

  /* @see java.io.DataOutput.writeShort(int) */
  public void writeShort(int v) throws IOException {
    raf.writeShort(v);
  }

  /* @see java.io.DataOutput.writeUTF(String)  */
  public void writeUTF(String str) throws IOException {
    raf.writeUTF(str);
  }

}
