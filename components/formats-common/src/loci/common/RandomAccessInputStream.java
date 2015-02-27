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
import java.io.DataInput;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Top-level class for reading from various data sources.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/RandomAccessInputStream.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/RandomAccessInputStream.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class RandomAccessInputStream extends InputStream implements DataInput, Closeable, KryoSerializable {

  // -- Constants --

  /** Maximum size of the buffer used by the DataInputStream. */
  protected static final int MAX_OVERHEAD = 1048576;

  /** Logger for this class. */
  private static final Logger LOGGER =
    LoggerFactory.getLogger(RandomAccessInputStream.class);

  /**
   * Block size to use when searching through the stream.
   */
  protected static final int DEFAULT_BLOCK_SIZE = 256 * 1024; // 256 KB

  /** Maximum number of bytes to search when searching through the stream. */
  protected static final int MAX_SEARCH_SIZE = 512 * 1024 * 1024; // 512 MB

  // -- Fields --

  protected IRandomAccess raf;

  /** The file name. */
  protected String file;

  protected long length = -1;

  protected long markedPos = -1;

  protected String encoding = Constants.ENCODING;

  // -- Constructors --

  /**
   * Constructs a hybrid RandomAccessFile/DataInputStream
   * around the given file.
   */
  public RandomAccessInputStream(String file) throws IOException {
    this(Location.getHandle(file), file);
  }

  /**
   * Constructs a hybrid RandomAccessFile/DataInputStream
   * around the given file.
   */
  public RandomAccessInputStream(String file, int bufferSize) throws IOException
  {
    this(Location.getHandle(file, false, true, bufferSize), file);
  }

  /** Constructs a random access stream around the given handle. */
  public RandomAccessInputStream(IRandomAccess handle) throws IOException {
    this(handle, null);
  }

  /**
   * Constructs a random access stream around the given handle,
   * and with the associated file path.
   */
  public RandomAccessInputStream(IRandomAccess handle, String file)
    throws IOException
  {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("RandomAccessInputStream {} OPEN", hashCode());
    }
    raf = handle;
    raf.setOrder(ByteOrder.BIG_ENDIAN);
    this.file = file;
    seek(0);
    length = -1;
  }

  /** Constructs a random access stream around the given byte array. */
  public RandomAccessInputStream(byte[] array) throws IOException {
    this(new ByteArrayHandle(array));
  }

  // -- RandomAccessInputStream API methods --

  /**
   * Sets the native encoding of the stream.
   *
   * @see loci.common.Constants#ENCODING
   */
  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  /** Seeks to the given offset within the stream. */
  public void seek(long pos) throws IOException {
    raf.seek(pos);
  }

  /** Gets the number of bytes in the file. */
  public long length() throws IOException {
    return length < 0 ? raf.length() : length;
  }

  /**
   * Sets the length of the stream.
   * The new length must be less than the real length of the stream.
   * This allows us to work with a truncated view of a file, without modifying
   * the file itself.
   *
   * Passing in a negative value will reset the length to the stream's real length.
   */
  public void setLength(long newLength) throws IOException {
    if (newLength < length()) {
      this.length = newLength;
    }
  }

  /** Gets the current (absolute) file pointer. */
  public long getFilePointer() throws IOException {
    return raf.getFilePointer();
  }

  /** Closes the streams. */
  public void close() throws IOException {
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("RandomAccessInputStream {} CLOSE", hashCode());
    }
    if (Location.getMappedFile(file) != null) return;
    if (raf != null) raf.close();
    raf = null;
    markedPos = -1;
  }

  /** Sets the endianness of the stream. */
  public void order(boolean little) {
    if (raf != null) {
      raf.setOrder(little ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
    }
  }

  /** Gets the endianness of the stream. */
  public boolean isLittleEndian() {
    return raf.getOrder() == ByteOrder.LITTLE_ENDIAN;
  }

  /**
   * Reads a string ending with one of the characters in the given string.
   *
   * @see #findString(String...)
   */
  public String readString(String lastChars) throws IOException {
    if (lastChars.length() == 1) return findString(lastChars);
    String[] terminators = new String[lastChars.length()];
    for (int i=0; i<terminators.length; i++) {
      terminators[i] = lastChars.substring(i, i + 1);
    }
    return findString(terminators);
  }

  /**
   * Reads a string ending with one of the given terminating substrings.
   *
   * @param terminators The strings for which to search.
   *
   * @return The string from the initial position through the end of the
   *   terminating sequence, or through the end of the stream if no
   *   terminating sequence is found.
   */
  public String findString(String... terminators) throws IOException {
    return findString(true, DEFAULT_BLOCK_SIZE, terminators);
  }

  /**
   * Reads or skips a string ending with
   * one of the given terminating substrings.
   *
   * @param saveString Whether to collect the string from the current file
   *   pointer to the terminating bytes, and return it. If false, returns null.
   * @param terminators The strings for which to search.
   *
   * @throws IOException If saveString flag is set
   *   and the maximum search length (512 MB) is exceeded.
   *
   * @return The string from the initial position through the end of the
   *   terminating sequence, or through the end of the stream if no
   *   terminating sequence is found, or null if saveString flag is unset.
   */
  public String findString(boolean saveString, String... terminators)
    throws IOException
  {
    return findString(saveString, DEFAULT_BLOCK_SIZE, terminators);
  }

  /**
   * Reads a string ending with one of the given terminating
   * substrings, using the specified block size for buffering.
   *
   * @param blockSize The block size to use when reading bytes in chunks.
   * @param terminators The strings for which to search.
   *
   * @return The string from the initial position through the end of the
   *   terminating sequence, or through the end of the stream if no
   *   terminating sequence is found.
   */
  public String findString(int blockSize, String... terminators)
    throws IOException
  {
    return findString(true, blockSize, terminators);
  }

  /**
   * Reads or skips a string ending with one of the given terminating
   * substrings, using the specified block size for buffering.
   *
   * @param saveString Whether to collect the string from the current file
   *   pointer to the terminating bytes, and return it. If false, returns null.
   * @param blockSize The block size to use when reading bytes in chunks.
   * @param terminators The strings for which to search.
   *
   * @throws IOException If saveString flag is set
   *   and the maximum search length (512 MB) is exceeded.
   *
   * @return The string from the initial position through the end of the
   *   terminating sequence, or through the end of the stream if no
   *   terminating sequence is found, or null if saveString flag is unset.
   */
  public String findString(boolean saveString, int blockSize,
    String... terminators) throws IOException
  {
    StringBuilder out = new StringBuilder();
    long startPos = getFilePointer();
    long bytesDropped = 0;
    long inputLen = length();
    long maxLen = inputLen - startPos;
    boolean tooLong = saveString && maxLen > MAX_SEARCH_SIZE;
    if (tooLong) maxLen = MAX_SEARCH_SIZE;
    boolean match = false;
    int maxTermLen = 0;
    for (String term : terminators) {
      int len = term.length();
      if (len > maxTermLen) maxTermLen = len;
    }

    InputStreamReader in = new InputStreamReader(this, encoding);
    char[] buf = new char[blockSize];
    long loc = 0;
    while (loc < maxLen && getFilePointer() < length() - 1) {
      // if we're not saving the string, drop any old, unnecessary output
      if (!saveString) {
        int outLen = out.length();
        if (outLen >= maxTermLen) {
          int dropIndex = outLen - maxTermLen + 1;
          String last = out.substring(dropIndex, outLen);
          out.setLength(0);
          out.append(last);
          bytesDropped += dropIndex;
        }
      }

      // read block from stream
      int r = in.read(buf, 0, blockSize);
      if (r <= 0) throw new IOException("Cannot read from stream: " + r);

      // append block to output
      out.append(buf, 0, r);

      // check output, returning smallest possible string
      int min = Integer.MAX_VALUE, tagLen = 0;
      for (String t : terminators) {
        int len = t.length();
        int start = (int) (loc - bytesDropped - len);
        int value = out.indexOf(t, start < 0 ? 0 : start);
        if (value >= 0 && value < min) {
          match = true;
          min = value;
          tagLen = len;
        }
      }

      if (match) {
        // reset stream to proper location
        seek(startPos + bytesDropped + min + tagLen);

        // trim output string
        if (saveString) {
          out.setLength(min + tagLen);
          return out.toString();
        }
        return null;
      }

      loc += r;
    }

    // no match
    if (tooLong) throw new IOException("Maximum search length reached.");
    return saveString ? out.toString() : null;
  }

  // -- DataInput API methods --

  /** Read an input byte and return true if the byte is nonzero. */
  public boolean readBoolean() throws IOException {
    return raf.readBoolean();
  }

  /** Read one byte and return it. */
  public byte readByte() throws IOException {
    return raf.readByte();
  }

  /** Read an input char. */
  public char readChar() throws IOException {
    return raf.readChar();
  }

  /** Read eight bytes and return a double value. */
  public double readDouble() throws IOException {
    return raf.readDouble();
  }

  /** Read four bytes and return a float value. */
  public float readFloat() throws IOException {
    return raf.readFloat();
  }

  /** Read four input bytes and return an int value. */
  public int readInt() throws IOException {
    return raf.readInt();
  }

  /** Read the next line of text from the input stream. */
  public String readLine() throws IOException {
    String line = findString("\n");
    return line.length() == 0 ? null : line;
  }

  /** Read a string of arbitrary length, terminated by a null char. */
  public String readCString() throws IOException {
    String line = findString("\0");
    return line.length() == 0 ? null : line;
  }

  /** Read a string of up to length n. */
  public String readString(int n) throws IOException {
    int avail = available();
    if (n > avail) n = avail;
    byte[] b = new byte[n];
    readFully(b);
    return new String(b, encoding);
  }

  /** Read eight input bytes and return a long value. */
  public long readLong() throws IOException {
    return raf.readLong();
  }

  /** Read two input bytes and return a short value. */
  public short readShort() throws IOException {
    return raf.readShort();
  }

  /** Read an input byte and zero extend it appropriately. */
  public int readUnsignedByte() throws IOException {
    return raf.readUnsignedByte();
  }

  /** Read two bytes and return an int in the range 0 through 65535. */
  public int readUnsignedShort() throws IOException {
    return raf.readUnsignedShort();
  }

  /** Read a string that has been encoded using a modified UTF-8 format. */
  public String readUTF() throws IOException {
    return raf.readUTF();
  }

  /** Skip n bytes within the stream. */
  public int skipBytes(int n) throws IOException {
    return raf.skipBytes(n);
  }

  /** Read bytes from the stream into the given array. */
  public int read(byte[] array) throws IOException {
    int rtn = raf.read(array);
    if (rtn == 0 && raf.getFilePointer() >= raf.length() - 1) rtn = -1;
    return rtn;
  }

  /**
   * Read n bytes from the stream into the given array at the specified offset.
   */
  public int read(byte[] array, int offset, int n) throws IOException {
    int rtn = raf.read(array, offset, n);
    if (rtn == 0 && raf.getFilePointer() >= raf.length() - 1) rtn = -1;
    return rtn;
  }

  /** Read bytes from the stream into the given buffer. */
  public int read(ByteBuffer buf) throws IOException {
    return raf.read(buf);
  }

  /**
   * Read n bytes from the stream into the given buffer at the specified offset.
   */
  public int read(ByteBuffer buf, int offset, int n) throws IOException {
    return raf.read(buf, offset, n);
  }

  /** Read bytes from the stream into the given array. */
  public void readFully(byte[] array) throws IOException {
    raf.readFully(array);
  }

  /**
   * Read n bytes from the stream into the given array at the specified offset.
   */
  public void readFully(byte[] array, int offset, int n) throws IOException {
    raf.readFully(array, offset, n);
  }

  // -- InputStream API methods --

  public int read() throws IOException {
    int b = readByte();
    if (b == -1 && (getFilePointer() >= length())) return 0;
    return b;
  }

  public int available() throws IOException {
    long remain = length() - getFilePointer();
    if (remain > Integer.MAX_VALUE) remain = Integer.MAX_VALUE;
    return (int) remain;
  }

  public void mark(int readLimit) {
    try {
      markedPos = getFilePointer();
    }
    catch (IOException exc) {
      LOGGER.warn("Cannot set mark", exc);
    }
  }

  public boolean markSupported() {
    return true;
  }

  public void reset() throws IOException {
    if (markedPos < 0) throw new IOException("No mark set");
    seek(markedPos);
  }

  // -- Externalizable API methods --

  public void read(Kryo kryo, Input in) {
    raf = (IRandomAccess) kryo.readClassAndObject(in);
    file = kryo.readObjectOrNull(in, String.class);
    if (file != null) {
      try {
        raf = Location.getHandle(file);
      }
      catch (IOException e) {
        LOGGER.warn("Failed to reopen file", e);
      }
    }
    length = kryo.readObject(in, Long.class);
    markedPos = kryo.readObject(in, Long.class);
    encoding = kryo.readObject(in, String.class);
  }

  public void write(Kryo kryo, Output out) {
    kryo.writeClassAndObject(out, raf);
    kryo.writeObjectOrNull(out, file, String.class);
    kryo.writeObject(out, length);
    kryo.writeObject(out, markedPos);
    kryo.writeObject(out, encoding);
  }

}
