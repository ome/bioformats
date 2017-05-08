/*
 * #%L
 * Fork of JAI Image I/O Tools.
 * %%
 * Copyright (C) 2008 - 2017 Open Microscopy Environment:
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

/*
 * $RCSfile: IISRandomAccessIO.java,v $
 *
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc. All  Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 
 * 
 * - Redistribution of source code must retain the above copyright 
 *   notice, this  list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in 
 *   the documentation and/or other materials provided with the
 *   distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of 
 * contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any 
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND 
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL 
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF 
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR 
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES. 
 * 
 * You acknowledge that this software is not designed or intended for 
 * use in the design, construction, operation or maintenance of any 
 * nuclear facility. 
 *
 * $Revision: 1.1 $
 * $Date: 2006/08/08 00:31:47 $
 * $State: Exp $
 */
package com.sun.media.imageioimpl.plugins.jpeg2000;

import java.io.IOException;
import java.nio.ByteOrder;
import javax.imageio.stream.ImageInputStream;
import jj2000.j2k.io.EndianType;
import jj2000.j2k.io.RandomAccessIO;

/**
 * A wrapper for converting an <code>ImageInputStream</code> into a
 * <code>RandomAccessIO</code>. The resulting class is read-only.
 */
public class IISRandomAccessIO implements RandomAccessIO {

    /** The <code>ImageInputStream</code> that is wrapped */
    private ImageInputStream iis;

    /**
     * Creates a <code>RandomAccessIO</code> instance from the supplied
     * <code>ImageInputStream</code>.
     *
     * @param iis The source <code>ImageInputStream</code>.
     */
    public IISRandomAccessIO(ImageInputStream iis) {
        if (iis == null) {
            throw new IllegalArgumentException("iis == null!");
        }
        this.iis = iis;
    }

    public void close() throws IOException {
        iis.close();
    }

    /**
     * Returns the stream position clamped to a maximum of
     * <code>Integer.MAX_VALUE</code>.
     */
    public int getPos() throws IOException {
        long pos = iis.getStreamPosition();
        return pos > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)pos;
    }

    public void seek(int off) throws IOException {
        iis.seek(off);
    }

    /**
     * Returns the length of the data stream.
     *
     * <p>If the length of the <code>ImageInputStream</code> is not
     * <code>-1</code>, then it is returned after being clamped to
     * a maximum value of <code>Integer.MAX_VALUE</code>. If the
     * <code>ImageInputStream</code> is <code>-1</code>, the stream
     * is read to a maximum position of <code>Integer.MAX_VALUE</code>
     * and its final position is returned. The position of the stream
     * is unchanged from the value it had prior to the call.</p>
     */
    public int length() throws IOException {
        long len = iis.length();

        // If the length is non-negative, use it.
        if(len != -1L) {
            return len > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)len;
        }

        // If the length is negative, read until the stream ends.
        iis.mark();
        int bufLen = 1024;
        byte[] buf = new byte[bufLen];
        long pos = iis.getStreamPosition();
        while(pos < Integer.MAX_VALUE) {
            int numRead = iis.read(buf, 0, bufLen);
            if(numRead == -1) break; // EOF
            pos += numRead;
        }
        iis.reset();

        // Return the last position.
        return pos > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)pos;
    }

    public int read() throws IOException {
        return iis.read();
    }

    public void readFully(byte b[], int off, int n) throws IOException {
        iis.readFully(b, off, n);
    }

    public int getByteOrdering() {
        return iis.getByteOrder() == ByteOrder.BIG_ENDIAN ?
            EndianType.BIG_ENDIAN : EndianType.LITTLE_ENDIAN;
    }

    public byte readByte() throws IOException {
        return iis.readByte();
    }

    public int readUnsignedByte() throws IOException {
        return iis.readUnsignedByte();
    }

    public short readShort() throws IOException {
        return iis.readShort();
    }

    public int readUnsignedShort() throws IOException {
        return iis.readUnsignedShort();
    }

    public int readInt() throws IOException {
        return iis.readInt();
    }

    public long readUnsignedInt() throws IOException {
        return iis.readUnsignedInt();
    }

    public long readLong() throws IOException {
        return iis.readLong();
    }

    public float readFloat() throws IOException {
        return iis.readFloat();
    }

    public double readDouble() throws IOException {
        return iis.readDouble();
    }

    public int skipBytes(int n) throws IOException {
        return iis.skipBytes(n);
    }

    /**
     * A null operation as writing is not supported.
     */
    public void flush() {
        // Intentionally empty.
    }

    /**
     * Throws an <code>IOException</code> as writing is not supported.
     */
    public void write(int b) throws IOException {
        throw new IOException("Writing is not supported!");
    }

    /**
     * Throws an <code>IOException</code> as writing is not supported.
     */
    public void writeByte(int v) throws IOException {
        throw new IOException("Writing is not supported!");
    }

    /**
     * Throws an <code>IOException</code> as writing is not supported.
     */
    public void writeShort(int v) throws IOException {
        throw new IOException("Writing is not supported!");
    }

    /**
     * Throws an <code>IOException</code> as writing is not supported.
     */
    public void writeInt(int v) throws IOException {
        throw new IOException("Writing is not supported!");
    }

    /**
     * Throws an <code>IOException</code> as writing is not supported.
     */
    public void writeLong(long v) throws IOException {
        throw new IOException("Writing is not supported!");
    }

    /**
     * Throws an <code>IOException</code> as writing is not supported.
     */
    public void writeFloat(float v) throws IOException {
        throw new IOException("Writing is not supported!");
    }

    /**
     * Throws an <code>IOException</code> as writing is not supported.
     */
    public void writeDouble(double v) throws IOException {
        throw new IOException("Writing is not supported!");
    }
}
