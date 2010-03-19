//
// RandomAccessOutputStream.java
//

/*
LOCI Common package: utilities for I/O, reflection and miscellaneous tasks.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden and Chris Allan.

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

package loci.common;

import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * RandomAccessOutputStream provides methods for writing to files and
 * byte arrays.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/common/src/loci/common/RandomAccessOutputStream.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/common/src/loci/common/RandomAccessOutputStream.java">SVN</a></dd></dl>
 */
public class RandomAccessOutputStream extends OutputStream implements DataOutput
{
  // -- Fields --

  private IRandomAccess outputFile;

  // -- Constructor --

  /**
   * Constructs a random access stream around the given file.
   * @param file Filename to open the stream for.
   * @throws IOException If there is a problem opening the file.
   */
  public RandomAccessOutputStream(String file) throws IOException {
    outputFile = Location.getHandle(file, true);
  }

  /**
   * Constructs a random access stream around the given handle.
   * @param handle Handle to open the stream for.
   */
  public RandomAccessOutputStream(IRandomAccess handle) {
    outputFile = handle;
  }

  // -- RandomAccessOutputStream API methods --

  /** Seeks to the given offset within the stream. */
  public void seek(long pos) throws IOException {
    outputFile.seek(pos);
  }

  /** Returns the current offset within the stream. */
  public long getFilePointer() throws IOException {
    return outputFile.getFilePointer();
  }

  /** Returns the length of the file. */
  public long length() throws IOException {
    return outputFile.length();
  }

  /** Advances the current offset by the given number of bytes. */
  public void skipBytes(int skip) throws IOException {
    outputFile.seek(outputFile.getFilePointer() + skip);
  }

  /** Sets the endianness of the stream. */
  public void order(boolean little) {
    outputFile.setOrder(
        little? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
  }

  /** Gets the endianness of the stream. */
  public boolean isLittleEndian() {
    return outputFile.getOrder() == ByteOrder.LITTLE_ENDIAN;
  }

  // -- DataOutput API methods --

  /* @see java.io.DataOutput#write(byte[]) */
  public void write(byte[] b) throws IOException {
    outputFile.write(b);
  }

  /* @see java.io.DataOutput#write(byte[], int, int) */
  public void write(byte[] b, int off, int len) throws IOException {
    outputFile.write(b, off, len);
  }

  /**
   * Writes bytes to the stream from the given buffer.
   * @param b Source buffer to read data from.
   * @throws IOException If there is an error writing to the stream.
   */
  public void write(ByteBuffer b) throws IOException {
    outputFile.write(b);
  }

  /**
   * @param b Source buffer to read data from.
   * @param off Offset within the buffer to start reading from.
   * @param len Number of bytes to read.
   * @throws IOException If there is an error writing to the stream.
   */
  public void write(ByteBuffer b, int off, int len) throws IOException {
    outputFile.write(b, off, len);
  }

  /* @see java.io.DataOutput#write(int) */
  public void write(int b) throws IOException {
    outputFile.write(b);
  }

  /* @see java.io.DataOutput#writeBoolean(boolean) */
  public void writeBoolean(boolean v) throws IOException {
    outputFile.writeBoolean(v);
  }

  /* @see java.io.DataOutput#writeByte(int) */
  public void writeByte(int v) throws IOException {
    outputFile.writeByte(v);
  }

  /* @see java.io.DataOutput#writeBytes(String) */
  public void writeBytes(String s) throws IOException {
    outputFile.writeBytes(s);
  }

  /* @see java.io.DataOutput#writeChar(int) */
  public void writeChar(int v) throws IOException {
    outputFile.writeChar(v);
  }

  /* @see java.io.DataOutput#writeChars(String) */
  public void writeChars(String s) throws IOException {
    outputFile.writeChars(s);
  }

  /* @see java.io.DataOutput#writeDouble(double) */
  public void writeDouble(double v) throws IOException {
    outputFile.writeDouble(v);
  }

  /* @see java.io.DataOutput#writeFloat(float) */
  public void writeFloat(float v) throws IOException {
    outputFile.writeFloat(v);
  }

  /* @see java.io.DataOutput#writeInt(int) */
  public void writeInt(int v) throws IOException {
    outputFile.writeInt(v);
  }

  /* @see java.io.DataOutput#writeLong(long) */
  public void writeLong(long v) throws IOException {
    outputFile.writeLong(v);
  }

  /* @see java.io.DataOutput#writeShort(int) */
  public void writeShort(int v) throws IOException {
    outputFile.writeShort(v);
  }

  /* @see java.io.DataOutput#writeUTF(String) */
  public void writeUTF(String str) throws IOException {
    outputFile.writeUTF(str);
  }

  // -- OutputStream API methods --

  /* @see java.io.OutputStream#close() */
  public void close() throws IOException {
    outputFile.close();
  }

  /* @see java.io.OutputStream#flush() */
  public void flush() throws IOException { }

}
