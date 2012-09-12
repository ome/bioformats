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

package loci.common.adapter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import loci.common.IRandomAccess;
import loci.legacy.adapter.AbstractLegacyAdapter;
import loci.legacy.adapter.Wrapper;

/**
 * As interfaces can not contain implementation, this class manages
 * interface-level delegation between {@link loci.common.IRandomAccess} and
 * {@link ome.scifio.io.IRandomAccess}
 * <p>
 * Delegation is maintained by two WeakHashTables. See {@link AbstractLegacyAdapter}
 * </p>
 * <p>
 * Functionally, the delegation is handled in the nested classes - one for
 * wrapping from ome.scifio.io.IRandomAccess to loci.common.IRandomAccess,
 * and one for the reverse direction.
 * </p>
 * @author Mark Hiner
 *
 */
public class IRandomAccessAdapter extends 
  AbstractLegacyAdapter<IRandomAccess, ome.scifio.io.IRandomAccess> {
  
  // -- LegacyAdapter API Methods --

  @Override
  protected IRandomAccess wrapToLegacy(ome.scifio.io.IRandomAccess modern) {
    return new ModernWrapper(modern);
  }

  @Override
  protected ome.scifio.io.IRandomAccess wrapToModern(IRandomAccess legacy) {
    return new LegacyWrapper(legacy);
  }
  
  // -- Delegation Classes --

  /**
   * This class can be used to wrap loci.common.IRandomAccess
   * objects and be passed to API expecting an ome.scifio.io.IRandomAccess
   * object.
   * <p>
   * All functionality is delegated to the loci-common implementation.
   * </p>
   * 
   * @author Mark Hiner
   */
  public static class LegacyWrapper
    implements ome.scifio.io.IRandomAccess, Wrapper<IRandomAccess> {
    
    // -- Fields --
    
    private IRandomAccess ira;
    
    // -- Constructor --
    
    public LegacyWrapper(IRandomAccess ira) {
      this.ira = ira;
    }
    
    // -- Wrapper API Methods --
    
    /* @see Wrapper#unwrap() */
    public IRandomAccess unwrap() {
      return ira;
    }
    
    // -- IRandomAccess API --

    public void close() throws IOException {
      ira.close();
    }

    public long getFilePointer() throws IOException {
      return ira.getFilePointer();
    }

    public long length() throws IOException {
      return ira.length();
    }

    public ByteOrder getOrder() {
      return ira.getOrder();
    }

    public int read(byte[] b) throws IOException {
      return ira.read(b);
    }

    public int read(byte[] b, int off, int len) throws IOException {
      return ira.read(b, off, len);
    }

    public int read(ByteBuffer buffer) throws IOException {
      return ira.read(buffer);
    }

    public int read(ByteBuffer buffer, int offset, int len) throws IOException {
      return ira.read(buffer, offset, len);
    }

    public boolean readBoolean() throws IOException {
      return ira.readBoolean();
    }

    public byte readByte() throws IOException {
      return ira.readByte();
    }

    public char readChar() throws IOException {
      return ira.readChar();
    }

    public double readDouble() throws IOException {
      return ira.readDouble();
    }

    public float readFloat() throws IOException {
      return ira.readFloat();
    }

    public void readFully(byte[] b, int off, int len) throws IOException {
      ira.readFully(b, off, len);
    }

    public void readFully(byte[] b) throws IOException {
      ira.readFully(b);
    }

    public int readInt() throws IOException {
      return ira.readInt();
    }

    public String readLine() throws IOException {
      return ira.readLine();
    }

    public long readLong() throws IOException {
      return ira.readLong();
    }

    public short readShort() throws IOException {
      return ira.readShort();
    }

    public String readUTF() throws IOException {
      return ira.readUTF();
    }

    public int readUnsignedByte() throws IOException {
      return ira.readUnsignedByte();
    }

    public int readUnsignedShort() throws IOException {
      return ira.readUnsignedShort();
    }

    public void setOrder(ByteOrder order) {
      ira.setOrder(order);
    }

    public void seek(long pos) throws IOException {
      ira.seek(pos);
    }

    public int skipBytes(int n) throws IOException {
      return ira.skipBytes(n);
    }

    public void write(byte[] b, int off, int len) throws IOException {
      ira.write(b, off, len);
    }

    public void write(byte[] b) throws IOException {
      ira.write(b);
    }

    public void write(ByteBuffer buf) throws IOException {
      ira.write(buf);
    }

    public void write(ByteBuffer buf, int off, int len) throws IOException {
      ira.write(buf, off, len);
    }

    public void write(int b) throws IOException {
      ira.write(b);
    }

    public void writeBoolean(boolean v) throws IOException {
      ira.writeBoolean(v);
    }

    public void writeByte(int v) throws IOException {
      ira.writeByte(v);
    }

    public void writeBytes(String s) throws IOException {
      ira.writeBytes(s);
    }

    public void writeChar(int v) throws IOException {
      ira.writeChar(v);
    }

    public void writeChars(String s) throws IOException {
      ira.writeChars(s);
    }

    public void writeDouble(double v) throws IOException {
      ira.writeDouble(v);
    }

    public void writeFloat(float v) throws IOException {
      ira.writeFloat(v);
    }

    public void writeInt(int v) throws IOException {
      ira.writeInt(v);
    }

    public void writeLong(long v) throws IOException {
      ira.writeLong(v);
    }

    public void writeShort(int v) throws IOException {
      ira.writeShort(v);
    }

    public void writeUTF(String s) throws IOException {
      ira.writeUTF(s);
    }
    
    // -- Object delegators --

    @Override
    public boolean equals(Object obj) {
      return ira.equals(obj);
    }
    
    @Override
    public int hashCode() {
      return ira.hashCode();
    }
    
    @Override
    public String toString() {
      return ira.toString();
    }
  }
  
  /**
   * This class can be used to wrap ome.scifio.io.IRandomAccess
   * objects and be passed to API expecting a loci.common.IRandomAccess
   * object.
   * <p>
   * All functionality is delegated to the scifio implementation.
   * </p>
   * 
   * @author Mark Hiner
   */
  public static class ModernWrapper implements IRandomAccess, Wrapper<ome.scifio.io.IRandomAccess> {
    
    // -- Fields --

    private ome.scifio.io.IRandomAccess ira;

    // -- Constructor --

    public ModernWrapper(ome.scifio.io.IRandomAccess ira) {
      this.ira = ira;
    }
    
    // -- Wrapper API Methods --

    /* @see Wrapper#unwrap() */
    public ome.scifio.io.IRandomAccess unwrap() {
      return ira;
    }

    // -- IRandomAccess API Methods --

    public boolean readBoolean() throws IOException {
      return ira.readBoolean();
    }

    public byte readByte() throws IOException {
      return ira.readByte();
    }

    public char readChar() throws IOException {
      return ira.readChar();
    }

    public double readDouble() throws IOException {
      return ira.readDouble();
    }

    public float readFloat() throws IOException {
      return ira.readFloat();
    }

    public void readFully(byte[] b) throws IOException {
      ira.readFully(b);
    }

    public void readFully(byte[] b, int off, int len) throws IOException {
      ira.readFully(b, off, len);
    }

    public int readInt() throws IOException {
      return ira.readInt();
    }

    public String readLine() throws IOException {
      return ira.readLine();
    }

    public long readLong() throws IOException {
      return ira.readLong();
    }

    public short readShort() throws IOException {
      return ira.readShort();
    }

    public String readUTF() throws IOException {
      return ira.readUTF();
    }

    public int readUnsignedByte() throws IOException {
      return ira.readUnsignedByte();
    }

    public int readUnsignedShort() throws IOException {
      return ira.readUnsignedShort();
    }

    public int skipBytes(int n) throws IOException {
      return ira.skipBytes(n);
    }

    public void write(int b) throws IOException {
      ira.write(b);
    }

    public void write(byte[] b) throws IOException {
      ira.write(b);
    }

    public void write(byte[] b, int off, int len) throws IOException {
      ira.write(b, off, len);
    }

    public void writeBoolean(boolean v) throws IOException {
      ira.writeBoolean(v);
    }

    public void writeByte(int v) throws IOException {
      ira.writeByte(v);
    }

    public void writeBytes(String s) throws IOException {
      ira.writeBytes(s);
    }

    public void writeChar(int v) throws IOException {
      ira.writeChar(v);
    }

    public void writeChars(String s) throws IOException {
      ira.writeChars(s);
    }

    public void writeDouble(double v) throws IOException {
      ira.writeDouble(v);
    }

    public void writeFloat(float v) throws IOException {
      ira.writeFloat(v);
    }

    public void writeInt(int v) throws IOException {
      ira.writeInt(v);
    }

    public void writeLong(long v) throws IOException {
      ira.writeLong(v);
    }

    public void writeShort(int v) throws IOException {
      ira.writeShort(v);
    }

    public void writeUTF(String s) throws IOException {
      ira.writeUTF(s);
    }

    public void close() throws IOException {
      ira.close();
    }

    public long getFilePointer() throws IOException {
      return ira.getFilePointer();
    }

    public long length() throws IOException {
      return ira.length();
    }

    public ByteOrder getOrder() {
      return ira.getOrder();
    }

    public void setOrder(ByteOrder order) {
      ira.setOrder(order);
    }

    public int read(byte[] b) throws IOException {
      return ira.read(b);
    }

    public int read(byte[] b, int off, int len) throws IOException {
      return ira.read(b, off, len);
    }

    public int read(ByteBuffer buffer) throws IOException {
      return ira.read(buffer);
    }

    public int read(ByteBuffer buffer, int offset, int len) throws IOException {
      return ira.read(buffer, offset, len);
    }

    public void seek(long pos) throws IOException {
      ira.seek(pos);
    }

    public void write(ByteBuffer buf) throws IOException {
      ira.write(buf);
    }

    public void write(ByteBuffer buf, int off, int len) throws IOException {
      ira.write(buf, off, len);
    }
    
    // -- Object delegators --

    @Override
    public boolean equals(Object obj) {
      return ira.equals(obj);
    }
    
    @Override
    public int hashCode() {
      return ira.hashCode();
    }
    
    @Override
    public String toString() {
      return ira.toString();
    }
  }
}
