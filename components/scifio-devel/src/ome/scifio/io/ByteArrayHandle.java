/*
 * #%L
 * OME SCIFIO package for reading and converting scientific file formats.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package ome.scifio.io;

import java.io.EOFException;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ome.scifio.common.Constants;


/**
 * A wrapper for a byte array that implements the IRandomAccess interface.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/ByteArrayHandle.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/ByteArrayHandle.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see IRandomAccess
 */
public class ByteArrayHandle extends AbstractNIOHandle {

  // -- Constants --

  /** Initial length of a new file. */
  protected static final int INITIAL_LENGTH = 1000000;

  // -- Fields --

  /** Backing ByteBuffer. */
  protected ByteBuffer buffer;

  /** Length of the file. */
  //protected long length;

  // -- Constructors --

  /**
   * Creates a random access byte stream to read from, and
   * write to, the bytes specified by the byte[] argument.
   */
  public ByteArrayHandle(byte[] bytes) {
    buffer = ByteBuffer.wrap(bytes);
  }

  public ByteArrayHandle(ByteBuffer bytes) {
    buffer = bytes;
  }

  /**
   * Creates a random access byte stream to read from, and write to.
   * @param capacity Number of bytes to initially allocate.
   */
  public ByteArrayHandle(int capacity) {
    buffer = ByteBuffer.allocate(capacity);
    buffer.limit(capacity);
  }

  /** Creates a random access byte stream to write to a byte array. */
  public ByteArrayHandle() {
    buffer = ByteBuffer.allocate(INITIAL_LENGTH);
    buffer.limit(0);
  }

  // -- ByteArrayHandle API methods --

  /** Gets the byte array backing this FileHandle. */
  public byte[] getBytes() {
    return buffer.array();
  }

  /**
   * Gets the byte buffer backing this handle. <b>NOTE:</b> This is the
   * backing buffer. Any modifications to this buffer including position,
   * length and capacity will affect subsequent calls upon its source handle.
   * @return Backing buffer of this handle.
   */
  public ByteBuffer getByteBuffer() {
    return buffer;
  }

  // -- AbstractNIOHandle API methods --

  /* @see AbstractNIOHandle.setLength(long) */
  public void setLength(long length) throws IOException {
    if (length > buffer.capacity()) {
      long fp = getFilePointer();
      ByteBuffer tmp = ByteBuffer.allocate((int) (length * 2));
      ByteOrder order = buffer == null ? null : getOrder();
      seek(0);
      buffer = tmp.put(buffer);
      if (order != null) setOrder(order);
      seek(fp);
    }
    buffer.limit((int) length);
  }

  // -- IRandomAccess API methods --

  /* @see IRandomAccess.close() */
  public void close() { }

  /* @see IRandomAccess.getFilePointer() */
  public long getFilePointer() {
    return buffer.position();
  }

  /* @see IRandomAccess.length() */
  public long length() {
    return buffer.limit();
  }

  /* @see IRandomAccess.read(byte[]) */
  public int read(byte[] b) throws IOException {
    return read(b, 0, b.length);
  }

  /* @see IRandomAccess.read(byte[], int, int) */
  public int read(byte[] b, int off, int len) throws IOException {
    if (getFilePointer() + len > length()) {
      len = (int) (length() - getFilePointer());
    }
    buffer.get(b, off, len);
    return len;
  }

  /* @see IRandomAccess.read(ByteBuffer) */
  public int read(ByteBuffer buf) throws IOException {
    return read(buf, 0, buf.capacity());
  }

  /* @see IRandomAccess.read(ByteBuffer, int, int) */
  public int read(ByteBuffer buf, int off, int len) throws IOException {
    if (buf.hasArray()) {
      buffer.get(buf.array(), off, len);
      return len;
    }

    byte[] b = new byte[len];
    read(b);
    buf.put(b, 0, len);
    return len;
  }

  /* @see IRandomAccess.seek(long) */
  public void seek(long pos) throws IOException {
    if (pos > length()) setLength(pos);
    buffer.position((int) pos);
  }

  /* @see IRandomAccess.getOrder() */
  public ByteOrder getOrder() {
    return buffer.order();
  }

  /* @see IRandomAccess.setOrder(ByteOrder) */
  public void setOrder(ByteOrder order) {
    buffer.order(order);
  }

  // -- DataInput API methods --

  /* @see java.io.DataInput.readBoolean() */
  public boolean readBoolean() throws IOException {
    return readByte() != 0;
  }

  /* @see java.io.DataInput.readByte() */
  public byte readByte() throws IOException {
    if (getFilePointer() + 1 > length()) {
      throw new EOFException(EOF_ERROR_MSG);
    }
    try {
      return buffer.get();
    }
    catch (BufferUnderflowException e) {
      EOFException eof = new EOFException();
      eof.initCause(e);
      throw eof;
    }
  }

  /* @see java.io.DataInput.readChar() */
  public char readChar() throws IOException {
    if (getFilePointer() + 2 > length()) {
      throw new EOFException(EOF_ERROR_MSG);
    }
    try {
      return buffer.getChar();
    }
    catch (BufferUnderflowException e) {
      EOFException eof = new EOFException();
      eof.initCause(e);
      throw eof;
    }
  }

  /* @see java.io.DataInput.readDouble() */
  public double readDouble() throws IOException {
    if (getFilePointer() + 8 > length()) {
      throw new EOFException(EOF_ERROR_MSG);
    }
    try {
      return buffer.getDouble();
    }
    catch (BufferUnderflowException e) {
      EOFException eof = new EOFException();
      eof.initCause(e);
      throw eof;
    }
  }

  /* @see java.io.DataInput.readFloat() */
  public float readFloat() throws IOException {
    if (getFilePointer() + 4 > length()) {
      throw new EOFException(EOF_ERROR_MSG);
    }
    try {
      return buffer.getFloat();
    }
    catch (BufferUnderflowException e) {
      EOFException eof = new EOFException();
      eof.initCause(e);
      throw eof;
    }
  }

  /* @see java.io.DataInput.readFully(byte[]) */
  public void readFully(byte[] b) throws IOException {
    readFully(b, 0, b.length);
  }

  /* @see java.io.DataInput.readFully(byte[], int, int) */
  public void readFully(byte[] b, int off, int len) throws IOException {
    if (getFilePointer() + len > length()) {
      throw new EOFException(EOF_ERROR_MSG);
    }
    try {
      buffer.get(b, off, len);
    }
    catch (BufferUnderflowException e) {
      EOFException eof = new EOFException();
      eof.initCause(e);
      throw eof;
    }
  }

  /* @see java.io.DataInput.readInt() */
  public int readInt() throws IOException {
    if (getFilePointer() + 4 > length()) {
      throw new EOFException(EOF_ERROR_MSG);
    }
    try {
      return buffer.getInt();
    }
    catch (BufferUnderflowException e) {
      EOFException eof = new EOFException();
      eof.initCause(e);
      throw eof;
    }
  }

  /* @see java.io.DataInput.readLine() */
  public String readLine() throws IOException {
    throw new IOException("Unimplemented");
  }

  /* @see java.io.DataInput.readLong() */
  public long readLong() throws IOException {
    if (getFilePointer() + 8 > length()) {
      throw new EOFException(EOF_ERROR_MSG);
    }
    try {
      return buffer.getLong();
    }
    catch (BufferUnderflowException e) {
      EOFException eof = new EOFException();
      eof.initCause(e);
      throw eof;
    }
  }

  /* @see java.io.DataInput.readShort() */
  public short readShort() throws IOException {
    if (getFilePointer() + 2 > length()) {
      throw new EOFException(EOF_ERROR_MSG);
    }
    try {
      return buffer.getShort();
    }
    catch (BufferUnderflowException e) {
      EOFException eof = new EOFException();
      eof.initCause(e);
      throw eof;
    }
  }

  /* @see java.io.DataInput.readUnsignedByte() */
  public int readUnsignedByte() throws IOException {
    return readByte() & 0xff;
  }

  /* @see java.io.DataInput.readUnsignedShort() */
  public int readUnsignedShort() throws IOException {
    return readShort() & 0xffff;
  }

  /* @see java.io.DataInput.readUTF() */
  public String readUTF() throws IOException {
    int length = readUnsignedShort();
    byte[] b = new byte[length];
    read(b);
    return new String(b, Constants.ENCODING);
  }

  /* @see java.io.DataInput.skipBytes(int) */
  public int skipBytes(int n) throws IOException {
    int skipped = (int) Math.min(n, length() - getFilePointer());
    if (skipped < 0) return 0;
    seek(getFilePointer() + skipped);
    return skipped;
  }

  // -- DataOutput API methods --

  /* @see java.io.DataOutput.write(byte[]) */
  public void write(byte[] b) throws IOException {
    write(b, 0, b.length);
  }

  /* @see java.io.DataOutput.write(byte[], int, int) */
  public void write(byte[] b, int off, int len) throws IOException {
    validateLength(len);
    buffer.put(b, off, len);
  }

  /* @see IRandomAccess.write(ByteBuffer) */
  public void write(ByteBuffer buf) throws IOException {
    write(buf, 0, buf.capacity());
  }

  /* @see IRandomAccess.write(ByteBuffer, int, int) */
  public void write(ByteBuffer buf, int off, int len) throws IOException {
    validateLength(len);
    buf.position(off);
    buf.limit(off + len);
    buffer.put(buf);
  }

  /* @see java.io.DataOutput.write(int b) */
  public void write(int b) throws IOException {
    validateLength(1);
    buffer.put((byte) b);
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
    write(s.getBytes(Constants.ENCODING));
  }

  /* @see java.io.DataOutput.writeChar(int) */
  public void writeChar(int v) throws IOException {
    validateLength(2);
    buffer.putChar((char) v);
  }

  /* @see java.io.DataOutput.writeChars(String) */
  public void writeChars(String s) throws IOException {
    int len = 2 * s.length();
    validateLength(len);
    char[] c = s.toCharArray();
    for (int i=0; i<c.length; i++) {
      writeChar(c[i]);
    }
  }

  /* @see java.io.DataOutput.writeDouble(double) */
  public void writeDouble(double v) throws IOException {
    validateLength(8);
    buffer.putDouble(v);
  }

  /* @see java.io.DataOutput.writeFloat(float) */
  public void writeFloat(float v) throws IOException {
    validateLength(4);
    buffer.putFloat(v);
  }

  /* @see java.io.DataOutput.writeInt(int) */
  public void writeInt(int v) throws IOException {
    validateLength(4);
    buffer.putInt(v);
  }

  /* @see java.io.DataOutput.writeLong(long) */
  public void writeLong(long v) throws IOException {
    validateLength(8);
    buffer.putLong(v);
  }

  /* @see java.io.DataOutput.writeShort(int) */
  public void writeShort(int v) throws IOException {
    validateLength(2);
    buffer.putShort((short) v);
  }

  /* @see java.io.DataOutput.writeUTF(String)  */
  public void writeUTF(String str) throws IOException {
    byte[] b = str.getBytes(Constants.ENCODING);
    writeShort(b.length);
    write(b);
  }

}
