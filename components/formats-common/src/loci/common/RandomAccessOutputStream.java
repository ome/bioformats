/*
 * #%L
 * Common package for I/O and related utilities
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
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

package loci.common;

import java.io.Closeable;
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
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/RandomAccessOutputStream.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/RandomAccessOutputStream.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class RandomAccessOutputStream extends OutputStream implements DataOutput, Closeable
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
      little ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
  }

  /** Gets the endianness of the stream. */
  public boolean isLittleEndian() {
    return outputFile.getOrder() == ByteOrder.LITTLE_ENDIAN;
  }

  /** Writes the given string followed by a newline character. */
  public void writeLine(String s) throws IOException {
    writeBytes(s);
    writeBytes("\n");
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
  public void flush() throws IOException {
  }

}
