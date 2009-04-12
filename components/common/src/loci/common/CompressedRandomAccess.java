//
// CompressedRandomAccess.java
//

/*
LOCI Common package: utilities for I/O, reflection and miscellaneous tasks.
Copyright (C) 2005-@year@ Melissa Linkert and Curtis Rueden.

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

import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * Abstract IRandomAccess implementation for reading from compressed files
 * or byte arrays.
 *
 * @see IRandomAccess
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/common/src/loci/common/CompressedRandomAccess.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/common/src/loci/common/CompressedRandomAccess.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public abstract class CompressedRandomAccess implements IRandomAccess {

  // -- Constants --

  protected static final int MAX_OVERHEAD = 1048576;

  // -- Fields --

  protected DataInputStream stream;
  protected long length;
  protected long fp;

  // -- Constructor --

  public CompressedRandomAccess() throws IOException {
    fp = 0;
  }

  // -- IRandomAccess API methods --

  /* @see IRandomAccess#close() */
  public void close() throws IOException {
    length = fp = 0;
    if (stream != null) stream.close();
    stream = null;
  }

  /* @see IRandomAccess#getFilePointer() */
  public long getFilePointer() throws IOException {
    return fp;
  }

  /* @see IRandomAccess#length() */
  public long length() throws IOException {
    return length;
  }

  /* @see IRandomAccess#read() */
  public int read() throws IOException {
    int v = stream.read();
    fp++;
    return v;
  }

  /* @see IRandomAccess#read(byte[]) */
  public int read(byte[] b) throws IOException {
    return read(b, 0, b.length);
  }

  /* @see IRandomAccess#read(byte[], int, int) */
  public int read(byte[] b, int off, int len) throws IOException {
    int n = stream.read(b, off, len);
    fp += n;
    while (n < len && fp < length()) {
      int s = stream.read(b, off + n, len - n);
      fp += s;
      n += s;
    }
    return n;
  }

  /* @see IRandomAccess#seek(long) */
  public abstract void seek(long pos) throws IOException;

  /* @see IRandomAccess#setLength(long) */
  public void setLength(long newLength) throws IOException {
    length = newLength;
  }

  // -- DataInput API methods --

  /* @see java.io.DataInput#readBoolean() */
  public boolean readBoolean() throws IOException {
    fp++;
    return stream.readBoolean();
  }

  /* @see java.io.DataInput#readByte() */
  public byte readByte() throws IOException {
    fp++;
    return stream.readByte();
  }

  /* @see java.io.DataInput#readChar() */
  public char readChar() throws IOException {
    fp++;
    return stream.readChar();
  }

  /* @see java.io.DataInput#readDouble() */
  public double readDouble() throws IOException {
    fp += 8;
    return stream.readDouble();
  }

  /* @see java.io.DataInput#readFloat() */
  public float readFloat() throws IOException {
    fp += 4;
    return stream.readFloat();
  }

  /* @see java.io.DataInput#readFully(byte[]) */
  public void readFully(byte[] b) throws IOException {
    stream.readFully(b);
    fp += b.length;
  }

  /* @see java.io.DataInput#readFully(byte[], int, int) */
  public void readFully(byte[] b, int off, int len) throws IOException {
    stream.readFully(b, off, len);
    fp += len;
  }

  /* @see java.io.DataInput#readInt() */
  public int readInt() throws IOException {
    fp += 4;
    return stream.readInt();
  }

  /* @see java.io.DataInput#readLine() */
  public String readLine() throws IOException {
    throw new IOException("Unimplemented");
  }

  /* @see java.io.DataInput#readLong() */
  public long readLong() throws IOException {
    fp += 8;
    return stream.readLong();
  }

  /* @see java.io.DataInput#readShort() */
  public short readShort() throws IOException {
    fp += 2;
    return stream.readShort();
  }

  /* @see java.io.DataInput#readUnsignedByte() */
  public int readUnsignedByte() throws IOException {
    fp++;
    return stream.readUnsignedByte();
  }

  /* @see java.io.DataInput#readUnsignedShort() */
  public int readUnsignedShort() throws IOException {
    fp += 2;
    return stream.readUnsignedShort();
  }

  /* @see java.io.DataInput#readUTF() */
  public String readUTF() throws IOException {
    String s = stream.readUTF();
    fp += s.length();
    return s;
  }

  /* @see java.io.DataInput#skipBytes(int) */
  public int skipBytes(int n) throws IOException {
    int s = stream.skipBytes(n);
    fp += s;
    return s;
  }

  // -- DataOutput API methods --

  /* @see java.io.DataOutput#write(byte[]) */
  public void write(byte[] b) throws IOException {
    throw new IOException("Unimplemented");
  }

  /* @see java.io.DataOutput#write(byte[], int, int) */
  public void write(byte[] b, int off, int len) throws IOException {
    throw new IOException("Unimplemented");
  }

  /* @see java.io.DataOutput#write(int) */
  public void write(int b) throws IOException {
    throw new IOException("Unimplemented");
  }

  /* @see java.io.DataOutput#writeBoolean(boolean) */
  public void writeBoolean(boolean v) throws IOException {
    throw new IOException("Unimplemented");
  }

  /* @see java.io.DataOutput#writeByte(int) */
  public void writeByte(int v) throws IOException {
    throw new IOException("Unimplemented");
  }

  /* @see java.io.DataOutput#writeBytes(String) */
  public void writeBytes(String s) throws IOException {
    throw new IOException("Unimplemented");
  }

  /* @see java.io.DataOutput#writeChar(int) */
  public void writeChar(int v) throws IOException {
    throw new IOException("Unimplemented");
  }

  /* @see java.io.DataOutput#writeChars(String) */
  public void writeChars(String s) throws IOException {
    throw new IOException("Unimplemented");
  }

  /* @see java.io.DataOutput#writeDouble(double) */
  public void writeDouble(double v) throws IOException {
    throw new IOException("Unimplemented");
  }

  /* @see java.io.DataOutput#writeFloat(float) */
  public void writeFloat(float v) throws IOException {
    throw new IOException("Unimplemented");
  }

  /* @see java.io.DataOutput#writeInt(int) */
  public void writeInt(int v) throws IOException {
    throw new IOException("Unimplemented");
  }

  /* @see java.io.DataOutput#writeLong(long) */
  public void writeLong(long v) throws IOException {
    throw new IOException("Unimplemented");
  }

  /* @see java.io.DataOutput#writeShort(int) */
  public void writeShort(int v) throws IOException {
    throw new IOException("Unimplemented");
  }

  /* @see java.io.DataOutput#writeUTF(String) */
  public void writeUTF(String str) throws IOException {
    throw new IOException("Unimplemented");
  }

}
