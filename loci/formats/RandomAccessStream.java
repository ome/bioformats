//
// RandomAccessStream.java
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
import java.util.*;

/**
 * RandomAccessStream provides methods for "intelligent" reading of files and
 * byte arrays.  It also automagically deals with closing and reopening files
 * to prevent an IOException caused by too many open files.
 *
 * @author Melissa Linkert, linkert at wisc.edu
 */
public class RandomAccessStream extends InputStream implements DataInput {

  // -- Constants --

  /** Maximum size of the buffer used by the DataInputStream. */
  // 256 KB - please don't change this
  protected static final int MAX_OVERHEAD = 262144;

  /** Maximum number of buffer sizes to keep. */
  protected static final int MAX_HISTORY = 50;

  /** Maximum number of open files. */
  protected static final int MAX_FILES = 128;

  /** Indicators for most efficient method of reading. */
  protected static final int DIS = 0;
  protected static final int RAF = 1;
  protected static final int ARRAY = 2;

  // -- Static fields --

  /** Hashtable of all files that have been opened at some point. */
  private static Hashtable fileCache = new Hashtable();

  /** Number of currently open files. */
  private static int openFiles = 0;

  // -- Fields --

  protected IRandomAccess raf;
  protected DataInputStream dis;

  /** The file pointer within the DIS. */
  protected int fp;

  /** The "absolute" file pointer. */
  protected int afp;

  /** Most recent mark. */
  protected int mark;

  /** Next place to mark. */
  protected int nextMark;

  /** The file name. */
  protected String file;

  /** Starting buffer. */
  protected byte[] buf;

  /** Recent buffer sizes. */
  protected Vector recent;

  /** Endianness of the stream. */
  protected boolean littleEndian = false;

  /** Number of bytes by which to extend the stream. */
  protected int ext = 0;

  // -- Constructors --

  /**
   * Constructs a hybrid RandomAccessFile/DataInputStream
   * around the given file.
   */
  public RandomAccessStream(String file) throws IOException {
    File f = new File(Location.getMappedId(file));
    f = f.getAbsoluteFile();
    if (f.exists()) {
      raf = new RAFile(f, "r");
      dis = new DataInputStream(new BufferedInputStream(
        new FileInputStream(Location.getMappedId(file)), MAX_OVERHEAD));
      int len = (int) raf.length();
      buf = new byte[len < MAX_OVERHEAD ? len : MAX_OVERHEAD];
      raf.readFully(buf);
      raf.seek(0);
      recent = new Vector();
      recent.add(new Integer(MAX_OVERHEAD / 2));
      nextMark = MAX_OVERHEAD;
    }
    else {
      raf = new RAUrl(Location.getMappedId(file), "r");
    }
    this.file = file;
    fp = 0;
    afp = 0;
    fileCache.put(this, Boolean.TRUE);
    openFiles++;
    if (openFiles > MAX_FILES) cleanCache();
  }

  /** Constructs a random access stream around the given byte array. */
  public RandomAccessStream(byte[] array) throws IOException {
    // this doesn't use a file descriptor, so we don't need to add it to the
    // file cache
    raf = new RABytes(array);
    fp = 0;
    afp = 0;
  }

  // -- RandomAccessStream API methods --

  /** Return the underlying InputStream */
  public DataInputStream getInputStream() { 
    try {
      if (fileCache.get(this) == Boolean.FALSE) reopen();
    }
    catch (IOException e) {
      return null;
    }
    return dis; 
  }

  /**
   * Sets the number of bytes by which to extend the stream.  This only applies
   * to InputStream API methods.
   */
  public void setExtend(int extend) { ext = extend; }

  /** Seeks to the given offset within the stream. */
  public void seek(long pos) throws IOException { afp = (int) pos; }

  /** Alias for readByte(). */
  public int read() throws IOException {
    int b = (int) readByte();
    if (b == -1 && (afp >= length()) && ext > 0) return 0;
    return b;
  }

  /** Gets the number of bytes in the file. */
  public long length() throws IOException { 
    if (fileCache.get(this) == Boolean.FALSE) reopen();
    return raf.length(); 
  }

  /** Gets the current (absolute) file pointer. */
  public int getFilePointer() { return afp; }

  /** Closes the streams. */
  public void close() throws IOException {
    if (raf != null) raf.close();
    raf = null;
    if (dis != null) dis.close();
    dis = null;
    buf = null;
    fileCache.put(this, Boolean.FALSE);
    openFiles--;
  }

  /** Sets the endianness of the stream. */
  public void order(boolean little) { littleEndian = little; }

  /** Gets the endianness of the stream. */
  public boolean isLittleEndian() { return littleEndian; }

  // -- DataInput API methods --

  /** Read an input byte and return true if the byte is nonzero. */
  public boolean readBoolean() throws IOException {
    return (readByte() != 0);
  }

  /** Read one byte and return it. */
  public byte readByte() throws IOException {
    int status = checkEfficiency(1);

    int oldAFP = afp;
    if (afp < raf.length() - 1) afp++;

    if (status == DIS) {
      byte b = dis.readByte();
      fp++;
      return b;
    }
    else if (status == ARRAY) {
      return buf[oldAFP];
    }
    else {
      byte b = raf.readByte();
      return b;
    }
  }

  /** Read an input char. */
  public char readChar() throws IOException {
    return (char) readByte();
  }

  /** Read eight bytes and return a double value. */
  public double readDouble() throws IOException {
    return Double.longBitsToDouble(readLong());
  }

  /** Read four bytes and return a float value. */
  public float readFloat() throws IOException {
    return Float.intBitsToFloat(readInt());
  }

  /** Read four input bytes and return an int value. */
  public int readInt() throws IOException {
    return DataTools.read4SignedBytes(this, littleEndian);
  }

  /** Read the next line of text from the input stream. */
  public String readLine() throws IOException {
    StringBuffer sb = new StringBuffer();
    char c = readChar();
    while (c != '\n') {
      sb = sb.append(c);
      c = readChar();
    }
    return sb.toString();
  }

  /** Read eight input bytes and return a long value. */
  public long readLong() throws IOException {
    return DataTools.read8SignedBytes(this, littleEndian);
  }

  /** Read two input bytes and return a short value. */
  public short readShort() throws IOException {
    return DataTools.read2SignedBytes(this, littleEndian);
  }

  /** Read an input byte and zero extend it appropriately. */
  public int readUnsignedByte() throws IOException {
    return DataTools.readUnsignedByte(this);
  }

  /** Read two bytes and return an int in the range 0 through 65535. */
  public int readUnsignedShort() throws IOException {
    return DataTools.read2UnsignedBytes(this, littleEndian);
  }

  /** Read a string that has been encoded using a modified UTF-8 format. */
  public String readUTF() throws IOException {
    return null;  // not implemented yet...we don't really need this
  }

  /** Skip n bytes within the stream. */
  public int skipBytes(int n) throws IOException {
    afp += n;
    return n;
  }

  /** Read bytes from the stream into the given array. */
  public int read(byte[] array) throws IOException {
    int status = checkEfficiency(array.length);
    int n = 0;

    if (status == DIS) { n = dis.read(array); }
    else if (status == ARRAY) {
      n = array.length;
      if ((buf.length - afp) < array.length) {
        n = buf.length - afp;
      }
      System.arraycopy(buf, afp, array, 0, n);
    }
    else n = raf.read(array);

    afp += n;
    if (status == DIS) fp += n;
    if (n < array.length && ext > 0) {
      while (n < array.length && ext > 0) {
        n++;
        ext--;
      }
    }
    return n;
  }

  /**
   * Read n bytes from the stream into the given array at the specified offset.
   */
  public int read(byte[] array, int offset, int n) throws IOException {
    int toRead = n;
    int status = checkEfficiency(n);

    if (status == DIS) {
      n = dis.read(array, offset, n);
    }
    else if (status == ARRAY) {
      if ((buf.length - afp) < n) n = buf.length - afp;
      System.arraycopy(buf, afp, array, offset, n);
    }
    else {
      n = raf.read(array, offset, n);
    }
    afp += n;
    if (status == DIS) fp += n;
    if (n < toRead && ext > 0) {
      while (n < array.length && ext > 0) {
        n++;
        ext--;
      }
    }

    return n;
  }

  /** Read bytes from the stream into the given array. */
  public void readFully(byte[] array) throws IOException {
    int status = checkEfficiency(array.length);

    if (status == DIS) {
      dis.readFully(array, 0, array.length);
    }
    else if (status == ARRAY) {
      System.arraycopy(buf, afp, array, 0, array.length);
    }
    else {
      raf.readFully(array, 0, array.length);
    }
    afp += array.length;
    if (status == DIS) fp += array.length;
  }

  /**
   * Read n bytes from the stream into the given array at the specified offset.
   */
  public void readFully(byte[] array, int offset, int n) throws IOException {
    int status = checkEfficiency(n);

    if (status == DIS) {
      dis.readFully(array, offset, n);
    }
    else if (status == ARRAY) {
      System.arraycopy(buf, afp, array, offset, n);
    }
    else {
      raf.readFully(array, offset, n);
    }
    afp += n;
    if (status == DIS) fp += n;
  }

  // -- InputStream API methods --

  public int available() throws IOException {
    if (fileCache.get(this) == Boolean.FALSE) reopen();
    return dis != null ? dis.available() + ext :
      (int) (length() - getFilePointer());
  }

  public void mark(int readLimit) {
    try {
      if (fileCache.get(this) == Boolean.FALSE) reopen();
    }
    catch (IOException e) { }
    dis.mark(readLimit);
  }

  public boolean markSupported() { return true; }

  public void reset() throws IOException {
    if (fileCache.get(this) == Boolean.FALSE) reopen();
    dis.reset();
    fp = (int) (length() - dis.available());
  }

  // -- Helper methods - I/O --

  /** Naive heuristic for determining a "good" buffer size for the DIS. */
  protected int determineBuffer() {
    // first we want the weighted average of previous buffer sizes

    int sum = 0;
    int div = 0;
    int ndx = 0;

    while ((ndx < recent.size()) && (ndx < MAX_HISTORY)) {
      int size = ((Integer) recent.get(ndx)).intValue();
      sum += (size * ((ndx / (MAX_HISTORY / 5)) + 1));
      div += (ndx / (MAX_HISTORY / 5)) + 1;
      ndx++;
    }

    int newSize = sum / div;
    if (newSize > MAX_OVERHEAD) newSize = MAX_OVERHEAD;
    if (recent.size() < MAX_HISTORY) recent.add(new Integer(newSize));
    else {
      recent.remove(0);
      recent.add(new Integer(newSize));
    }

    return newSize;
  }

  /**
   * Determine whether it is more efficient to use the DataInputStream or
   * RandomAccessFile for reading (based on the current file pointers).
   * Returns 0 if we should use the DataInputStream, 1 if we should use the
   * RandomAccessFile, and 2 for a direct array access.
   */
  protected int checkEfficiency(int toRead) throws IOException {
    if (fileCache.get(this) == Boolean.FALSE) reopen();
    int oldBufferSize = 0;
    if (recent != null) {
      oldBufferSize = ((Integer) recent.get(recent.size() - 1)).intValue();
    }

    if (dis != null) {
      while (fp > (length() - dis.available())) {
        dis.skipBytes((int) (fp - (length() - dis.available())));
      }
    }

    if (dis != null && raf != null &&
      afp + toRead < MAX_OVERHEAD && afp + toRead < raf.length())
    {
      // this is a really special case that allows us to read directly from
      // an array when working with the first MAX_OVERHEAD bytes of the file
      // ** also note that it doesn't change the stream
      return ARRAY;
    }
    else if (afp >= fp && dis != null) {
      while (fp < afp) {
        int skip = dis.skipBytes(afp - fp);
        if (skip == 0) break;
        fp += skip;
      }

      if (recent.size() < MAX_HISTORY) recent.add(new Integer(MAX_OVERHEAD));
      else {
        recent.remove(0);
        recent.add(new Integer(MAX_OVERHEAD));
      }

      if (fp >= nextMark) {
        dis.mark(MAX_OVERHEAD);
      }
      nextMark = fp + MAX_OVERHEAD;
      mark = fp;

      return DIS;
    }
    else {
      if (dis != null && afp >= mark && fp < mark + oldBufferSize) {
        int newBufferSize = determineBuffer();

        boolean valid = true;

        try {
          dis.reset();
        }
        catch (IOException io) {
          valid = false;
        }

        if (valid) {
          dis.mark(newBufferSize);
          //fp = mark;

          fp = (int) (length() - dis.available());
          while (fp < afp) {
            int skip = dis.skipBytes(afp - fp);
            if (skip == 0) break;
            fp += skip;
          }

          if (fp >= nextMark) {
            dis.mark(newBufferSize);
          }
          nextMark = fp + newBufferSize;
          mark = fp;

          return DIS;
        }
        else {
          raf.seek(afp);
          return RAF;
        }
      }
      else {
        // we don't want this to happen very often
        raf.seek(afp);
        return RAF;
      }
    }
  }

  // -- Helper methods - cache management --

  /** Re-open a file that has been closed */
  private void reopen() throws IOException {
    File f = new File(Location.getMappedId(file));
    f = f.getAbsoluteFile();
    if (f.exists()) {
      raf = new RAFile(f, "r");
      dis = new DataInputStream(new BufferedInputStream(
        new FileInputStream(Location.getMappedId(file)), MAX_OVERHEAD));
      int len = (int) raf.length();
      buf = new byte[len < MAX_OVERHEAD ? len : MAX_OVERHEAD];
      raf.readFully(buf);
      raf.seek(0);
    }
    else {
      raf = new RAUrl(Location.getMappedId(file), "r");
    }
    fileCache.put(this, Boolean.TRUE);
    openFiles++;
    if (openFiles > MAX_FILES) cleanCache();
  }

  /** If we have too many open files, close most of them. */
  private void cleanCache() {
    int toClose = MAX_FILES - 10;
    RandomAccessStream[] files = (RandomAccessStream[]) 
      fileCache.keySet().toArray(new RandomAccessStream[0]);
    int closed = 0;
    int ndx = 0;
    int blahCounter = 0;
    int oldOpen = openFiles;
    
    while (closed < toClose) {
      if (!this.equals(files[ndx]) && 
        !fileCache.get(files[ndx]).equals(Boolean.FALSE)) 
      {
        try { files[ndx].close(); }
        catch (IOException e) { e.printStackTrace(); }
        blahCounter++;
        closed++;
      }
      ndx++;
    }
  }

}
