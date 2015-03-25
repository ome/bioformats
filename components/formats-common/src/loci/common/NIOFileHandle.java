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

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A wrapper for buffered NIO logic that implements the IRandomAccess interface.
 *
 * @see IRandomAccess
 * @see java.io.RandomAccessFile
 *
 * @author Chris Allan <callan at blackcat dot ca>
 */
public class NIOFileHandle extends AbstractNIOHandle {

  // -- Constants --

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(NIOFileHandle.class);

  //-- Static fields --

  /** Default NIO buffer size to facilitate buffered I/O. */
  protected static int defaultBufferSize = 1048576;

  /**
   * Default NIO buffer size to facilitate buffered I/O for read/write streams.
   */
  protected static int defaultRWBufferSize = 8192;

  // -- Fields --

  /** The random access file object backing this FileHandle. */
  protected RandomAccessFile raf;

  /** The file channel backed by the random access file. */
  protected FileChannel channel;

  /** The absolute position within the file. */
  protected long position = 0;

  /** The absolute position of the start of the buffer. */
  protected long bufferStartPosition = 0;

  /** The buffer size. */
  protected int bufferSize;

  /** The buffer itself. */
  protected ByteBuffer buffer;

  /** Whether or not the file is opened read/write. */
  protected boolean isReadWrite = false;

  /** The default map mode for the file. */
  protected FileChannel.MapMode mapMode = FileChannel.MapMode.READ_ONLY;

  /** The buffer's byte ordering. */
  protected ByteOrder order;

  /** Provider class for NIO byte buffers, allocated or memory mapped. */
  protected NIOByteBufferProvider byteBufferProvider;

  /** The original length of the file. */
  private Long defaultLength;

  // -- Constructors --

  /**
   * Creates a random access file stream to read from, and
   * optionally to write to, the file specified by the File argument.
   */
  public NIOFileHandle(File file, String mode, int bufferSize)
    throws IOException
  {
    this.bufferSize = bufferSize;
    validateMode(mode);
    if (mode.equals("rw")) {
      isReadWrite = true;
      mapMode = FileChannel.MapMode.READ_WRITE;
    }
    raf = new RandomAccessFile(file, mode);
    channel = raf.getChannel();
    byteBufferProvider = new NIOByteBufferProvider(channel, mapMode);
    buffer(position, 0);

    // if we know the length won't change, cache the original length
    if (mode.equals("r")) {
      defaultLength = raf.length();
    }
  }

  /**
   * Creates a random access file stream to read from, and
   * optionally to write to, the file specified by the File argument.
   */
  public NIOFileHandle(File file, String mode) throws IOException {
    this(file, mode,
      mode.equals("rw") ? defaultRWBufferSize : defaultBufferSize);
  }

  /**
   * Creates a random access file stream to read from, and
   * optionally to write to, a file with the specified name.
   */
  public NIOFileHandle(String name, String mode) throws IOException {
    this(new File(name), mode);
  }

  // -- NIOFileHandle API methods --

  /**
   * Set the default buffer size for read-only files.
   *
   * Subsequent uses of the NIOFileHandle(String, String) and
   * NIOFileHandle(File, String) constructors will use this buffer size.
   */
  public static void setDefaultBufferSize(int size) {
    defaultBufferSize = size;
  }

  /**
   * Set the default buffer size for read/write files.
   *
   * Subsequent uses of the NIOFileHandle(String, String) and
   * NIOFileHandle(File, String) constructors will use this buffer size.
   */
  public static void setDefaultReadWriteBufferSize(int size) {
    defaultRWBufferSize = size;
  }

  // -- FileHandle and Channel API methods --

  /** Gets the random access file object backing this FileHandle. */
  public RandomAccessFile getRandomAccessFile() {
    return raf;
  }

  /** Gets the FileChannel from this FileHandle. */
  public FileChannel getFileChannel() {
    try {
      channel.position(position);
    }
    catch (IOException e) {
      LOGGER.warn("FileChannel.position failed", e);
    }
    return channel;
  }

  /** Gets the current buffer size. */
  public int getBufferSize() {
    return bufferSize;
  }

  // -- AbstractNIOHandle API methods --

  /* @see AbstractNIOHandle.setLength(long) */
  @Override
  public void setLength(long length) throws IOException {
    raf.seek(length - 1);
    raf.write((byte) 0);
    buffer = null;
  }

  // -- IRandomAccess API methods --

  /* @see IRandomAccess.close() */
  @Override
  public void close() throws IOException {
    raf.close();
  }

  /* @see IRandomAccess.getFilePointer() */
  @Override
  public long getFilePointer() {
    return position;
  }

  /* @see IRandomAccess.length() */
  @Override
  public long length() throws IOException {
    if (defaultLength != null) {
      return defaultLength;
    }
    return raf.length();
  }

  /* @see IRandomAccess.getOrder() */
  @Override
  public ByteOrder getOrder() {
    return buffer == null ? order : buffer.order();
  }

  /* @see IRandomAccess.setOrder(ByteOrder) */
  @Override
  public void setOrder(ByteOrder order) {
    this.order = order;
    if (buffer != null) {
      buffer.order(order);
    }
  }

  /* @see IRandomAccess.read(byte[]) */
  @Override
  public int read(byte[] b) throws IOException {
    return read(ByteBuffer.wrap(b));
  }

  /* @see IRandomAccess.read(byte[], int, int) */
  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    return read(ByteBuffer.wrap(b), off, len);
  }

  /* @see IRandomAccess.read(ByteBuffer) */
  @Override
  public int read(ByteBuffer buf) throws IOException {
    return read(buf, 0, buf.capacity());
  }

  /* @see IRandomAccess.read(ByteBuffer, int, int) */
  @Override
  public int read(ByteBuffer buf, int off, int len) throws IOException {
    buf.position(off);
    int realLength = (int) Math.min(len, length() - position);
    if (realLength < 0) {
      return -1;
    }
    buf.limit(off + realLength);
    buffer(position, realLength);
    position += realLength;
    while (buf.hasRemaining()) {
      try {
        buf.put(buffer.get());
      } catch (BufferUnderflowException e) {
        EOFException eof = new EOFException(EOF_ERROR_MSG);
        eof.initCause(e);
        throw eof;
      }
    }
    return realLength;
  }

  /* @see IRandomAccess.seek(long) */
  @Override
  public void seek(long pos) throws IOException {
    if (mapMode == FileChannel.MapMode.READ_WRITE && pos > length()) {
      setLength(pos);
    }
    buffer(pos, 0);
  }

  /* @see java.io.DataInput.readBoolean() */
  @Override
  public boolean readBoolean() throws IOException {
    return readByte() == 1;
  }

  /* @see java.io.DataInput.readByte() */
  @Override
  public byte readByte() throws IOException {
    buffer(position, 1);
    position += 1;
    try {
      return buffer.get();
    } catch (BufferUnderflowException e) {
      EOFException eof = new EOFException(EOF_ERROR_MSG);
      eof.initCause(e);
      throw eof;
    }
  }

  /* @see java.io.DataInput.readChar() */
  @Override
  public char readChar() throws IOException {
    buffer(position, 2);
    position += 2;
    try {
      return buffer.getChar();
    } catch (BufferUnderflowException e) {
      EOFException eof = new EOFException(EOF_ERROR_MSG);
      eof.initCause(e);
      throw eof;
    }
  }

  /* @see java.io.DataInput.readDouble() */
  @Override
  public double readDouble() throws IOException {
    buffer(position, 8);
    position += 8;
    try {
      return buffer.getDouble();
    } catch (BufferUnderflowException e) {
      EOFException eof = new EOFException(EOF_ERROR_MSG);
      eof.initCause(e);
      throw eof;
    }
  }

  /* @see java.io.DataInput.readFloat() */
  @Override
  public float readFloat() throws IOException {
    buffer(position, 4);
    position += 4;
    try {
      return buffer.getFloat();
    } catch (BufferUnderflowException e) {
      EOFException eof = new EOFException(EOF_ERROR_MSG);
      eof.initCause(e);
      throw eof;
    }
  }

  /* @see java.io.DataInput.readFully(byte[]) */
  @Override
  public void readFully(byte[] b) throws IOException {
    read(b);
  }

  /* @see java.io.DataInput.readFully(byte[], int, int) */
  @Override
  public void readFully(byte[] b, int off, int len) throws IOException {
    read(b, off, len);
  }

  /* @see java.io.DataInput.readInt() */
  @Override
  public int readInt() throws IOException {
    buffer(position, 4);
    position += 4;
    try {
      return buffer.getInt();
    } catch (BufferUnderflowException e) {
      EOFException eof = new EOFException(EOF_ERROR_MSG);
      eof.initCause(e);
      throw eof;
    }
  }

  /* @see java.io.DataInput.readLine() */
  @Override
  public String readLine() throws IOException {
    raf.seek(position);
    String line = raf.readLine();
    buffer(raf.getFilePointer(), 0);
    return line;
  }

  /* @see java.io.DataInput.readLong() */
  @Override
  public long readLong() throws IOException {
    buffer(position, 8);
    position += 8;
    try {
      return buffer.getLong();
    } catch (BufferUnderflowException e) {
      EOFException eof = new EOFException(EOF_ERROR_MSG);
      eof.initCause(e);
      throw eof;
    }
  }

  /* @see java.io.DataInput.readShort() */
  @Override
  public short readShort() throws IOException {
    buffer(position, 2);
    position += 2;
    try {
      return buffer.getShort();
    } catch (BufferUnderflowException e) {
      EOFException eof = new EOFException(EOF_ERROR_MSG);
      eof.initCause(e);
      throw eof;
    }
  }

  /* @see java.io.DataInput.readUnsignedByte() */
  @Override
  public int readUnsignedByte() throws IOException {
    return readByte() & 0xFF;
  }

  /* @see java.io.DataInput.readUnsignedShort() */
  @Override
  public int readUnsignedShort() throws IOException {
    return readShort() & 0xFFFF;
  }

  /* @see java.io.DataInput.readUTF() */
  @Override
  public String readUTF() throws IOException {
    raf.seek(position);
    String utf8 = raf.readUTF();
    buffer(raf.getFilePointer(), 0);
    return utf8;
  }

  /* @see java.io.DataInput.skipBytes(int) */
  @Override
  public int skipBytes(int n) throws IOException {
    if (n < 1) {
      return 0;
    }
    long oldPosition = position;
    long newPosition = oldPosition + Math.min(n, length());

    buffer(newPosition, 0);
    return (int) (position - oldPosition);
  }

  // -- DataOutput API methods --

  /* @see java.io.DataOutput.write(byte[]) */
  @Override
  public void write(byte[] b) throws IOException {
    write(ByteBuffer.wrap(b));
  }

  /* @see java.io.DataOutput.write(byte[], int, int) */
  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    write(ByteBuffer.wrap(b), off, len);
  }

  /* @see IRandomAccess.write(ByteBuffer) */
  @Override
  public void write(ByteBuffer buf) throws IOException {
    write(buf, 0, buf.capacity());
  }

  /* @see IRandomAccess.write(ByteBuffer, int, int) */
  @Override
  public void write(ByteBuffer buf, int off, int len) throws IOException {
    writeSetup(len);
    buf.limit(off + len);
    buf.position(off);
    position += channel.write(buf, position);
    buffer = null;
  }

  /* @see java.io.DataOutput.write(int b) */
  @Override
  public void write(int b) throws IOException {
    writeByte(b);
  }

  /* @see java.io.DataOutput.writeBoolean(boolean) */
  @Override
  public void writeBoolean(boolean v) throws IOException {
    writeByte(v ? 1 : 0);
  }

  /* @see java.io.DataOutput.writeByte(int) */
  @Override
  public void writeByte(int v) throws IOException {
    writeSetup(1);
    buffer.put((byte) v);
    doWrite(1);
  }

  /* @see java.io.DataOutput.writeBytes(String) */
  @Override
  public void writeBytes(String s) throws IOException {
    write(s.getBytes(Constants.ENCODING));
  }

  /* @see java.io.DataOutput.writeChar(int) */
  @Override
  public void writeChar(int v) throws IOException {
    writeSetup(2);
    buffer.putChar((char) v);
    doWrite(2);
  }

  /* @see java.io.DataOutput.writeChars(String) */
  @Override
  public void writeChars(String s) throws IOException {
    write(s.getBytes("UTF-16BE"));
  }

  /* @see java.io.DataOutput.writeDouble(double) */
  @Override
  public void writeDouble(double v) throws IOException {
    writeSetup(8);
    buffer.putDouble(v);
    doWrite(8);
  }

  /* @see java.io.DataOutput.writeFloat(float) */
  @Override
  public void writeFloat(float v) throws IOException {
    writeSetup(4);
    buffer.putFloat(v);
    doWrite(4);
  }

  /* @see java.io.DataOutput.writeInt(int) */
  @Override
  public void writeInt(int v) throws IOException {
    writeSetup(4);
    buffer.putInt(v);
    doWrite(4);
  }

  /* @see java.io.DataOutput.writeLong(long) */
  @Override
  public void writeLong(long v) throws IOException {
    writeSetup(8);
    buffer.putLong(v);
    doWrite(8);
  }

  /* @see java.io.DataOutput.writeShort(int) */
  @Override
  public void writeShort(int v) throws IOException {
    writeSetup(2);
    buffer.putShort((short) v);
    doWrite(2);
  }

  /* @see java.io.DataOutput.writeUTF(String)  */
  @Override
  public void writeUTF(String str) throws IOException {
    // NB: number of bytes written is greater than the length of the string
    int strlen = str.getBytes(Constants.ENCODING).length + 2;
    writeSetup(strlen);
    raf.seek(position);
    raf.writeUTF(str);
    position += strlen;
    buffer = null;
  }

  /**
   * Aligns the NIO buffer, maps it if it is not currently and sets all
   * relevant positions and offsets.
   * @param offset The location within the file to read from.
   * @param size The requested read length.
   * @throws IOException If there is an issue mapping, aligning or allocating
   * the buffer.
   */
  private void buffer(long offset, int size) throws IOException {
    position = offset;
    long newPosition = offset + size;
    if (newPosition < bufferStartPosition ||
      newPosition > bufferStartPosition + bufferSize || buffer == null)
    {
      bufferStartPosition = offset;
      if (length() > 0 && length() - 1 < bufferStartPosition) {
        bufferStartPosition = length() - 1;
      }
      long newSize = Math.min(length() - bufferStartPosition, bufferSize);
      if (newSize < size && newSize == bufferSize) newSize = size;
      if (newSize + bufferStartPosition > length()) {
        newSize = length() - bufferStartPosition;
      }
      offset = bufferStartPosition;
      ByteOrder byteOrder = buffer == null ? order : getOrder();
      buffer = byteBufferProvider.allocate(bufferStartPosition, (int) newSize);
      if (byteOrder != null) setOrder(byteOrder);
    }
    buffer.position((int) (offset - bufferStartPosition));
    if (buffer.position() + size > buffer.limit() &&
      mapMode == FileChannel.MapMode.READ_WRITE)
    {
      buffer.limit(buffer.position() + size);
    }
  }

  private void writeSetup(int length) throws IOException {
    validateLength(length);
    buffer(position, length);
  }

  private void doWrite(int length) throws IOException {
    buffer.position(buffer.position() - length);
    channel.write(buffer, position);
    position += length;
  }

}
