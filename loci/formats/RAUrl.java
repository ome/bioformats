//
// RAUrl.java
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
import java.net.*;

/**
 * Provides random access to data over HTTP using the IRandomAccess interface.
 * This is slow, but functional.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/RAUrl.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/RAUrl.java">SVN</a></dd></dl>
 *
 * @see IRandomAccess
 * @see java.net.HttpURLConnection
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class RAUrl implements IRandomAccess {

  // -- Fields --

  /** URL of open socket */
  private String url;

  /** Socket underlying this stream */
  private HttpURLConnection conn;

  /** Input stream */
  private DataInputStream is;

  /** Output stream */
  private DataOutputStream os;

  /** Stream pointer */
  private long fp;

  /** Number of bytes in the stream */
  private long length;

  /** Reset marker */
  private long mark;

  // -- Constructors --

  public RAUrl(String url, String mode) throws IOException {
    if (!url.startsWith("http")) url = "http://" + url;
    conn = (HttpURLConnection) (new URL(url)).openConnection();
    if (mode.equals("r")) {
      is = new DataInputStream(new BufferedInputStream(
        conn.getInputStream(), 65536));
    }
    else if (mode.equals("w")) {
      conn.setDoOutput(true);
      os = new DataOutputStream(conn.getOutputStream());
    }
    fp = 0;
    length = conn.getContentLength();
    if (is != null) is.mark((int) length);
    this.url = url;
  }

  // -- IRandomAccess API methods --

  /* @see IRandomAccess#close() */
  public void close() throws IOException {
    if (is != null) is.close();
    if (os != null) os.close();
    conn.disconnect();
  }

  /* @see IRandomAccess#getFilePointer() */
  public long getFilePointer() throws IOException {
    return fp;
  }

  /* @see IRandomAccess#length() */
  public long length() throws IOException { return length; }

  /* @see IRandomAccess#read() */
  public int read() throws IOException {
    int value = is.read();
    while (value == -1 && fp < length()) value = is.read();
    if (value != -1) fp++;
    markManager();
    return value;
  }

  /* @see IRandomAccess#read(byte[]) */
  public int read(byte[] b) throws IOException {
    return read(b, 0, b.length);
  }

  /* @see IRandomAccess#read(byte[], int, int) */
  public int read(byte[] b, int off, int len) throws IOException {
    int read = is.read(b, off, len);
    if (read != -1) fp += read;
    if (read == -1) read = 0;
    markManager();
    while (read < len && fp < length()) {
      int oldRead = read;
      read += read(b, off + read, len - read);
      if (read < oldRead) read = oldRead;
    }
    return read == 0 ? -1 : read;
  }

  /* @see IRandomAccess#seek(long) */
  public void seek(long pos) throws IOException {
    if (pos >= fp) {
      skipBytes((int) (pos - fp));
      return;
    }
    else if (pos >= mark) {
      try {
        is.reset();
        fp = mark;
        skipBytes((int) (pos - fp));
        return;
      }
      catch (IOException e) { }
    }

    close();
    conn = (HttpURLConnection) (new URL(url)).openConnection();
    conn.setDoOutput(true);
    if (is != null) {
      is = new DataInputStream(new BufferedInputStream(
        conn.getInputStream(), 65536));
      is.mark((int) length());
      mark = 0;
    }
    if (os != null) os = new DataOutputStream(conn.getOutputStream());
    this.url = url;
    fp = 0;
    skipBytes((int) pos);
  }

  /* @see IRandomAccess#setLength(long) */
  public void setLength(long newLength) throws IOException {
    length = newLength;
  }

  // -- DataInput API methods --

  /* @see java.io.DataInput#readBoolean() */
  public boolean readBoolean() throws IOException {
    fp++;
    return is.readBoolean();
  }

  /* @see java.io.DataInput#readByte() */
  public byte readByte() throws IOException {
    fp++;
    return is.readByte();
  }

  /* @see java.io.DataInput#readChar() */
  public char readChar() throws IOException {
    fp++;
    return is.readChar();
  }

  /* @see java.io.DataInput#readDouble() */
  public double readDouble() throws IOException {
    fp += 8;
    return is.readDouble();
  }

  /* @see java.io.DataInput#readFloat() */
  public float readFloat() throws IOException {
    fp += 4;
    return is.readFloat();
  }

  /* @see java.io.DataInput#readFully(byte[]) */
  public void readFully(byte[] b) throws IOException {
    fp += b.length;
    is.readFully(b);
  }

  /* @see java.io.DataInput#readFully(byte[], int, int) */
  public void readFully(byte[] b, int off, int len) throws IOException {
    fp += len;
    is.readFully(b, off, len);
  }

  /* @see java.io.DataInput#readInt() */
  public int readInt() throws IOException {
    fp += 4;
    return is.readInt();
  }

  /* @see java.io.DataInput#readLine() */
  public String readLine() throws IOException {
    throw new IOException("Unimplemented");
  }

  /* @see java.io.DataInput#readLong() */
  public long readLong() throws IOException {
    fp += 8;
    return is.readLong();
  }

  /* @see java.io.DataInput#readShort() */
  public short readShort() throws IOException {
    fp += 2;
    return is.readShort();
  }

  /* @see java.io.DataInput#readUnsignedByte() */
  public int readUnsignedByte() throws IOException {
    fp++;
    return is.readUnsignedByte();
  }

  /* @see java.io.DataInput#readUnsignedShort() */
  public int readUnsignedShort() throws IOException {
    fp += 2;
    return is.readUnsignedShort();
  }

  /* @see java.io.DataInput#readUTF() */
  public String readUTF() throws IOException {
    fp += 2;
    return is.readUTF();
  }

  /* @see java.io.DataInput#skipBytes(int) */
  public int skipBytes(int n) throws IOException {
    int skipped = 0;
    for (int i=0; i<n; i++) {
      if (read() != -1) skipped++;
      markManager();
    }
    return skipped;
  }

  // -- DataOutput API methods --

  /* @see java.io.DataOutput#write(byte[]) */
  public void write(byte[] b) throws IOException {
    os.write(b);
  }

  /* @see java.io.DataOutput#write(byte[], int, int) */
  public void write(byte[] b, int off, int len) throws IOException {
    os.write(b, off, len);
  }

  /* @see java.io.DataOutput#write(int b) */
  public void write(int b) throws IOException {
    os.write(b);
  }

  /* @see java.io.DataOutput#writeBoolean(boolean) */
  public void writeBoolean(boolean v) throws IOException {
    os.writeBoolean(v);
  }

  /* @see java.io.DataOutput#writeByte(int) */
  public void writeByte(int v) throws IOException {
    os.writeByte(v);
  }

  /* @see java.io.DataOutput#writeBytes(String) */
  public void writeBytes(String s) throws IOException {
    os.writeBytes(s);
  }

  /* @see java.io.DataOutput#writeChar(int) */
  public void writeChar(int v) throws IOException {
    os.writeChar(v);
  }

  /* @see java.io.DataOutput#writeChars(String) */
  public void writeChars(String s) throws IOException {
    os.writeChars(s);
  }

  /* @see java.io.DataOutput#writeDouble(double) */
  public void writeDouble(double v) throws IOException {
    os.writeDouble(v);
  }

  /* @see java.io.DataOutput#writeFloat(float) */
  public void writeFloat(float v) throws IOException {
    os.writeFloat(v);
  }

  /* @see java.io.DataOutput#writeInt(int) */
  public void writeInt(int v) throws IOException {
    os.writeInt(v);
  }

  /* @see java.io.DataOutput#writeLong(long) */
  public void writeLong(long v) throws IOException {
    os.writeLong(v);
  }

  /* @see java.io.DataOutput#writeShort(int) */
  public void writeShort(int v) throws IOException {
    os.writeShort(v);
  }

  /* @see java.io.DataOutput#writeUTF(String) */
  public void writeUTF(String str) throws IOException {
    os.writeUTF(str);
  }

  // -- Helper methods --

  private void markManager() throws IOException {
    if (fp >= mark + 65535) {
      mark = fp;
      is.mark((int) length());
    }
  }
}
