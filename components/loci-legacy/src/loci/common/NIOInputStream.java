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

package loci.common;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import loci.common.adapter.IRandomAccessAdapter;
import loci.legacy.adapter.AdapterTools;

/**
 * A legacy delegator class for ome.scifio.io.NIOInputStream.
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/NIOInputStream.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/NIOInputStream.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 */
public class NIOInputStream extends InputStream implements DataInput {

  // -- Constants --

  // -- Fields --

  private ome.scifio.io.NIOInputStream stream;

  // -- Constructors --

  /** Constructs an NIOInputStream around the given file. */
  public NIOInputStream(String filename) throws IOException {
    stream = new ome.scifio.io.NIOInputStream(filename);
  }

  /** Constructs a random access stream around the given handle. */
  public NIOInputStream(IRandomAccess handle) {
    stream = new ome.scifio.io.NIOInputStream(AdapterTools.getAdapter(IRandomAccessAdapter.class).getModern(handle));
  }

  /** Constructs a random access stream around the given byte array. */
  public NIOInputStream(byte[] array) {
    stream = new ome.scifio.io.NIOInputStream(array);
  }

  // -- NIOInputStream API methods --

  /** Returns the underlying InputStream. */
  public DataInputStream getInputStream() {
    return stream.getInputStream();
  }

  /**
   * Sets the number of bytes by which to extend the stream.  This only applies
   * to InputStream API methods.
   */
  public void setExtend(int extend) {
    stream.setExtend(extend);
  }

  /** Seeks to the given offset within the stream. */
  public void seek(long pos) throws IOException {
    stream.seek(pos);
  }

  /** Alias for readByte(). */
  public int read() throws IOException {
    return stream.read();
  }

  /** Gets the number of bytes in the file. */
  public long length() throws IOException {
    return stream.length();
  }

  /** Gets the current (absolute) file pointer. */
  public long getFilePointer() {
    return stream.getFilePointer();
  }

  /** Closes the streams. */
  public void close() throws IOException {
    stream.close();
  }

  /** Sets the endianness of the stream. */
  public void order(boolean isLittleEndian) {
    stream.order(isLittleEndian);
  }

  /** Gets the endianness of the stream. */
  public boolean isLittleEndian() {
    return stream.isLittleEndian();
  }

  /**
   * Reads a string ending with one of the characters in the given string.
   *
   * @see #findString(String...)
   */
  public String readString(String lastChars) throws IOException {
    return stream.readString(lastChars);
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
    return stream.findString(terminators);
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
    return stream.findString(saveString, terminators);
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
    return stream.findString(blockSize, terminators);
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
    return stream.findString(saveString, blockSize, terminators);
  }

  // -- DataInput API methods --

  /** Read an input byte and return true if the byte is nonzero. */
  public boolean readBoolean() throws IOException {
    return stream.readBoolean();
  }

  /** Read one byte and return it. */
  public byte readByte() throws IOException {
    return stream.readByte();
  }

  /** Read an input char. */
  public char readChar() throws IOException {
    return stream.readChar();
  }

  /** Read eight bytes and return a double value. */
  public double readDouble() throws IOException {
    return stream.readDouble();
  }

  /** Read four bytes and return a float value. */
  public float readFloat() throws IOException {
    return stream.readFloat();
  }

  /** Read four input bytes and return an int value. */
  public int readInt() throws IOException {
    return stream.readInt();
  }

  /** Read the next line of text from the input stream. */
  public String readLine() throws IOException {
    return stream.readLine();
  }

  /** Read a string of arbitrary length, terminated by a null char. */
  public String readCString() throws IOException {
    return stream.readCString();
  }

  /** Read a string of length n. */
  public String readString(int n) throws IOException {
    return stream.readString(n);
  }

  /** Read eight input bytes and return a long value. */
  public long readLong() throws IOException {
    return stream.readLong();
  }

  /** Read two input bytes and return a short value. */
  public short readShort() throws IOException {
    return stream.readShort();
  }

  /** Read an input byte and zero extend it appropriately. */
  public int readUnsignedByte() throws IOException {
    return stream.readUnsignedByte();
  }

  /** Read two bytes and return an int in the range 0 through 65535. */
  public int readUnsignedShort() throws IOException {
    return stream.readUnsignedShort();
  }

  /** Read a string that has been encoded using a modified UTF-8 format. */
  public String readUTF() throws IOException {
    return stream.readUTF();
  }

  /** Skip n bytes within the stream. */
  public int skipBytes(int n) throws IOException {
    return stream.skipBytes(n);
  }

  /** Read bytes from the stream into the given array. */
  public int read(byte[] array) throws IOException {
    return stream.read(array);
  }

  /**
   * Read n bytes from the stream into the given array at the specified offset.
   */
  public int read(byte[] array, int offset, int n) throws IOException {
    return stream.read(array, offset, n);
  }

  /** Read bytes from the stream into the given array. */
  public void readFully(byte[] array) throws IOException {
    stream.readFully(array);
  }

  /**
   * Read n bytes from the stream into the given array at the specified offset.
   */
  public void readFully(byte[] array, int offset, int n) throws IOException {
    stream.readFully(array, offset, n);
  }

  // -- InputStream API methods --

  public int available() throws IOException {
    return stream.available();
  }

  public void mark(int readLimit) {
    stream.mark(readLimit);
  }

  public boolean markSupported() {
    return stream.markSupported();
  }

  public void reset() throws IOException {
    stream.reset();
  }

}