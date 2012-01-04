//
// NIOInputStream.java
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

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.Channel;

/**
 * NIOInputStream provides methods for "intelligent" reading of files
 * and byte arrays.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/NIOInputStream.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/NIOInputStream.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 */
public class NIOInputStream extends InputStream implements DataInput {

  // -- Constants --

  /**
   * Block size to use when searching through the stream.
   * This value should not exceed MAX_OVERHEAD!
   */
  protected static final int DEFAULT_BLOCK_SIZE = 256 * 1024; // 256 KB

  /** Maximum number of bytes to search when searching through the stream. */
  protected static final int MAX_SEARCH_SIZE = 512 * 1024 * 1024; // 512 MB

  // -- Fields --

  protected IRandomAccess raf;

  /** The file name. */
  protected String filename;

  /** The file. */
  protected File file;

  /** The file channel backed by the random access file. */
  protected Channel channel;

  /** Endianness of the stream. */
  protected boolean isLittleEndian;

  // -- Constructors --

  /** Constructs an NIOInputStream around the given file. */
  public NIOInputStream(String filename) throws IOException {
    this.filename = filename;
    file = new File(filename);
    raf = new FileHandle(file, "r");
  }

  /** Constructs a random access stream around the given handle. */
  public NIOInputStream(IRandomAccess handle) {
    raf = handle;
  }

  /** Constructs a random access stream around the given byte array. */
  public NIOInputStream(byte[] array) {
    this(new ByteArrayHandle(array));
  }

  // -- NIOInputStream API methods --

  /** Returns the underlying InputStream. */
  public DataInputStream getInputStream() {
    // FIXME: To be implemented.
    return null;
  }

  /**
   * Sets the number of bytes by which to extend the stream.  This only applies
   * to InputStream API methods.
   */
  public void setExtend(int extend) {
    // FIXME: WTF? To be implemented?
  }

  /** Seeks to the given offset within the stream. */
  public void seek(long pos) throws IOException {
    raf.seek(pos);
  }

  /** Alias for readByte(). */
  public int read() throws IOException {
    return raf.readUnsignedByte();
  }

  /** Gets the number of bytes in the file. */
  public long length() throws IOException {
    return raf.length();
  }

  /** Gets the current (absolute) file pointer. */
  public long getFilePointer() {
    // FIXME: Change interface?
    try {
      return raf.getFilePointer();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /** Closes the streams. */
  public void close() throws IOException {
    // FIXME: To be implemented.
  }

  /** Sets the endianness of the stream. */
  public void order(boolean isLittleEndian) {
    this.isLittleEndian = isLittleEndian;
  }

  /** Gets the endianness of the stream. */
  public boolean isLittleEndian() {
    return isLittleEndian;
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

    InputStreamReader in = new InputStreamReader(this, "UTF-8");
    char[] buf = new char[blockSize];
    long loc = 0;
    while (loc < maxLen) {
      long pos = startPos + loc;

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
      int num = blockSize;
      if (pos + blockSize > inputLen) num = (int) (inputLen - pos);
      int r = in.read(buf, 0, num);
      if (r <= 0) throw new IOException("Cannot read from stream: " + r);

      // append block to output
      out.append(buf, 0, r);

      // check output, returning smallest possible string
      int min = Integer.MAX_VALUE, tagLen = 0;
      for (int t=0; t<terminators.length; t++) {
        int len = terminators[t].length();
        int start = (int) (loc - bytesDropped - len);
        int value = out.indexOf(terminators[t], start < 0 ? 0 : start);
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
    return null;
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
    return findString("\n");
  }

  /** Read a string of arbitrary length, terminated by a null char. */
  public String readCString() throws IOException {
    return findString("\0");
  }

  /** Read a string of length n. */
  public String readString(int n) throws IOException {
    byte[] b = new byte[n];
    readFully(b);
    return new String(b, "UTF-8");
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
    return null;  // not implemented yet...we don't really need this
  }

  /** Skip n bytes within the stream. */
  public int skipBytes(int n) throws IOException {
    return raf.skipBytes(n);
  }

  /** Read bytes from the stream into the given array. */
  public int read(byte[] array) throws IOException {
    return read(array, 0, array.length);
  }

  /**
   * Read n bytes from the stream into the given array at the specified offset.
   */
  public int read(byte[] array, int offset, int n) throws IOException {
    return raf.read(array, offset, n);
  }

  /** Read bytes from the stream into the given array. */
  public void readFully(byte[] array) throws IOException {
    readFully(array, 0, array.length);
  }

  /**
   * Read n bytes from the stream into the given array at the specified offset.
   */
  public void readFully(byte[] array, int offset, int n) throws IOException {
    raf.readFully(array, offset, n);
  }

  // -- InputStream API methods --

  public int available() throws IOException {
    // FIXME: Need to implement this in sub-classes of IRandomAccess.
    return 0;
  }

  public void mark(int readLimit) {
    // XXX: No-op
  }

  public boolean markSupported() {
    return false;
  }

  public void reset() throws IOException {
    raf.seek(0);
  }

}
