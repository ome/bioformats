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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Legacy delegator class for ome.scifio.io.StreamHandle.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/StreamHandle.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/StreamHandle.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @see IRandomAccess
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public abstract class StreamHandle implements IRandomAccess {

  // -- Fields --

  protected ome.scifio.io.StreamHandle sHandle;

  // -- Constructor --

  // -- IRandomAccess API methods --

  /* @see IRandomAccess#close() */
  public void close() throws IOException {
    sHandle.close();
  }

  /* @see IRandomAccess#getFilePointer() */
  public long getFilePointer() throws IOException {
    return sHandle.getFilePointer();
  }

  /* @see IRandomAccess#length() */
  public long length() throws IOException {
    return sHandle.length();
  }

  /* @see IRandomAccess#read(byte[]) */
  public int read(byte[] b) throws IOException {
    return sHandle.read(b);
  }

  /* @see IRandomAccess#read(byte[], int, int) */
  public int read(byte[] b, int off, int len) throws IOException {
    return sHandle.read(b, off, len);
  }

  /* @see IRandomAccess#read(ByteBuffer) */
  public int read(ByteBuffer buffer) throws IOException {
    return sHandle.read(buffer);
  }

  /* @see IRandomAccess#read(ByteBuffer, int, int) */
  public int read(ByteBuffer buffer, int off, int len) throws IOException {
    return sHandle.read(buffer, off, len);
  }

  /* @see IRandomAccess#seek(long) */
  public void seek(long pos) throws IOException {
    sHandle.seek(pos);
  }

  /* @see IRandomAccess.write(ByteBuffer) */
  public void write(ByteBuffer buf) throws IOException {
    sHandle.write(buf);
  }

  /* @see IRandomAccess.write(ByteBuffer, int, int) */
  public void write(ByteBuffer buf, int off, int len) throws IOException {
    sHandle.write(buf, off, len);
  }

  /* @see IRandomAccess.getOrder() */
  public ByteOrder getOrder() {
    return sHandle.getOrder();
  }

  /* @see IRandomAccess.setOrder(ByteOrder) */
  public void setOrder(ByteOrder order) {
    sHandle.setOrder(order);
  }

  // -- DataInput API methods --

  /* @see java.io.DataInput#readBoolean() */
  public boolean readBoolean() throws IOException {
    return sHandle.readBoolean();
  }

  /* @see java.io.DataInput#readByte() */
  public byte readByte() throws IOException {
    return sHandle.readByte();
  }

  /* @see java.io.DataInput#readChar() */
  public char readChar() throws IOException {
    return sHandle.readChar();
  }

  /* @see java.io.DataInput#readDouble() */
  public double readDouble() throws IOException {
    return sHandle.readDouble();
  }

  /* @see java.io.DataInput#readFloat() */
  public float readFloat() throws IOException {
    return sHandle.readFloat();
  }

  /* @see java.io.DataInput#readFully(byte[]) */
  public void readFully(byte[] b) throws IOException {
    sHandle.readFully(b);
  }

  /* @see java.io.DataInput#readFully(byte[], int, int) */
  public void readFully(byte[] b, int off, int len) throws IOException {
    sHandle.readFully(b, off, len);
  }

  /* @see java.io.DataInput#readInt() */
  public int readInt() throws IOException {
    return sHandle.readInt();
  }

  /* @see java.io.DataInput#readLine() */
  public String readLine() throws IOException {
    return sHandle.readLine();
  }

  /* @see java.io.DataInput#readLong() */
  public long readLong() throws IOException {
    return sHandle.readLong();
  }

  /* @see java.io.DataInput#readShort() */
  public short readShort() throws IOException {
    return sHandle.readShort();
  }

  /* @see java.io.DataInput#readUnsignedByte() */
  public int readUnsignedByte() throws IOException {
    return sHandle.readUnsignedByte();
  }

  /* @see java.io.DataInput#readUnsignedShort() */
  public int readUnsignedShort() throws IOException {
    return sHandle.readUnsignedShort();
  }

  /* @see java.io.DataInput#readUTF() */
  public String readUTF() throws IOException {
    return sHandle.readUTF();
  }

  /* @see java.io.DataInput#skipBytes(int) */
  public int skipBytes(int n) throws IOException {
    return sHandle.skipBytes(n);
  }

  // -- DataOutput API methods --

  /* @see java.io.DataOutput#write(byte[]) */
  public void write(byte[] b) throws IOException {
    sHandle.write(b);
  }

  /* @see java.io.DataOutput#write(byte[], int, int) */
  public void write(byte[] b, int off, int len) throws IOException {
    sHandle.write(b, off, len);
  }

  /* @see java.io.DataOutput#write(int) */
  public void write(int b) throws IOException {
    sHandle.write(b);
  }

  /* @see java.io.DataOutput#writeBoolean(boolean) */
  public void writeBoolean(boolean v) throws IOException {
    sHandle.writeBoolean(v);
  }

  /* @see java.io.DataOutput#writeByte(int) */
  public void writeByte(int v) throws IOException {
    sHandle.writeByte(v);
  }

  /* @see java.io.DataOutput#writeBytes(String) */
  public void writeBytes(String s) throws IOException {
    sHandle.writeBytes(s);
  }

  /* @see java.io.DataOutput#writeChar(int) */
  public void writeChar(int v) throws IOException {
    sHandle.writeChar(v);
  }

  /* @see java.io.DataOutput#writeChars(String) */
  public void writeChars(String s) throws IOException {
    sHandle.writeChars(s);
  }

  /* @see java.io.DataOutput#writeDouble(double) */
  public void writeDouble(double v) throws IOException {
    sHandle.writeDouble(v);
  }

  /* @see java.io.DataOutput#writeFloat(float) */
  public void writeFloat(float v) throws IOException {
    sHandle.writeFloat(v);
  }

  /* @see java.io.DataOutput#writeInt(int) */
  public void writeInt(int v) throws IOException {
    sHandle.writeInt(v);
  }

  /* @see java.io.DataOutput#writeLong(long) */
  public void writeLong(long v) throws IOException {
    sHandle.writeLong(v);
  }

  /* @see java.io.DataOutput#writeShort(int) */
  public void writeShort(int v) throws IOException {
    sHandle.writeShort(v);
  }

  /* @see java.io.DataOutput#writeUTF(String) */
  public void writeUTF(String str) throws IOException {
    sHandle.writeUTF(str);
  }
  
  // -- Object Delegators --
  
  @Override
  public boolean equals(Object obj) {
    return sHandle.equals(obj);
  }
  
  @Override
  public int hashCode() {
    return sHandle.hashCode();
  }
  
  @Override
  public String toString() {
    return sHandle.toString();
  }

  // -- Helper methods --

  /**
   * Close and reopen the stream; the stream pointer and mark should be
   * reset to 0.  This method is called if we need to seek backwards within
   * the stream.
   */
  protected abstract void resetStream() throws IOException;

}
