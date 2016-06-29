/*
 * #%L
 * Fork of JAI Image I/O Tools.
 * %%
 * Copyright (C) 2008 - 2016 Open Microscopy Environment:
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
 * $RCSfile: FileChannelImageOutputStream.java,v $
 *
 * 
 * Copyright (c) 2005 Sun Microsystems, Inc. All  Rights Reserved.
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
 * $Date: 2005/02/11 05:01:20 $
 * $State: Exp $
 */
package com.sun.media.imageio.stream;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStreamImpl;

/**
 * A class which implements <code>ImageOutputStream</code> using a
 * <code>FileChannel</code> as the eventual data destination.
 *
 * <p>Memory mapping is used for reading and direct buffers for writing.
 * Only methods which provide significant performance improvement with
 * respect to the superclass implementation are overridden.  Overridden
 * methods are not commented individually unless some noteworthy aspect
 * of the implementation must be described.</p>
 *
 * <p>The methods of this class are <b>not</b> synchronized.</p>
 *
 * @see javax.imageio.stream.ImageOutputStream
 * @see java.nio
 * @see java.nio.channels.FileChannel
 */
public class FileChannelImageOutputStream extends ImageOutputStreamImpl {

    /** The size of the write buffer. */
    // XXX Should this be a different value? Smaller?
    // Should there be an instance variable with public
    // accessor/mutator methods?
    private static final int DEFAULT_WRITE_BUFFER_SIZE = 1048576;

    /** The <code>FileChannel</code> data destination. */
    private FileChannel channel;

    /** A <code>ByteBuffer</code> used for writing to the channel. */
    private ByteBuffer byteBuffer;

    /** An <code>ImageInputStream</code> used for reading. */
    private ImageInputStream readStream = null;

    /**
     * Test method.
     *
     * @param args Command line arguments (ignored).
     * @throws Throwable for any Exception or Error.
     */
    /* XXX
    public static void main(String[] args) throws Throwable {
        // Create files.
        java.io.RandomAccessFile fileFIOS =
            new java.io.RandomAccessFile("fios.tmp", "rw");
        java.io.RandomAccessFile fileFCIOS =
            new java.io.RandomAccessFile("fcios.tmp", "rw");

        // Create streams.
        javax.imageio.stream.ImageOutputStream fios =
            new javax.imageio.stream.FileImageOutputStream(fileFIOS);
        javax.imageio.stream.ImageOutputStream fcios =
            new FileChannelImageOutputStream(fileFCIOS.getChannel());

        int datumSize = 4;

        // Write some ints (257, 258)
        fios.writeInts(new int[] {(int)256, (int)257,
                                    (int)258, (int)59},
                        1, 2);
        fcios.writeInts(new int[] {(int)256, (int)257,
                                     (int)258, (int)259},
                         1, 2);

        // Write a byte.
        fios.write(127);
        fcios.write(127);

        // Write some more ints (262, 263, 264).
        fios.writeInts(new int[] {(int)260, (int)261, (int)262,
                                    (int)263, (int)264, (int)265}, 2, 3);
        fcios.writeInts(new int[] {(int)260, (int)261, (int)262,
                                     (int)263, (int)264, (int)265}, 2, 3);

        // Seek to byte location.
        fios.seek(2*datumSize);
        fcios.seek(2*datumSize);

        // Read and print the byte.
        System.out.println(fios.read());
        System.out.println(fcios.read());

        // Seek to beginning.
        fios.seek(0);
        fcios.seek(0);

        int[] fiosInts = new int[10];
        int[] fciosInts = new int[10];

        // Read the ints.
        fios.readFully(fiosInts, 1, 2);
        fcios.readFully(fciosInts, 1, 2);

        // Print the ints
        for(int i = 0; i < 2; i++) {
            System.out.println((int)fiosInts[i+1]+" "+(int)fciosInts[i+1]);
        }

        // Seek to second set of ints
        fios.seek(2*datumSize+1);
        fcios.seek(2*datumSize+1);

        // Read the ints.
        fios.readFully(fiosInts, 1, 3);
        fcios.readFully(fciosInts, 1, 3);

        // Print the ints
        for(int i = 0; i < 3; i++) {
            System.out.println((int)fiosInts[i+1]+" "+(int)fciosInts[i+1]);
        }
    }
    */

    /**
     * Constructs a <code>FileChannelImageOutputStream</code> from a
     * <code>FileChannel</code>.  The initial position of the stream
     * stream is taken to be the position of the <code>FileChannel</code>
     * parameter when this constructor is invoked.  The stream and flushed
     * positions are therefore both initialized to
     * <code>channel.position()</code>.
     *
     * @param channel the destination <code>FileChannel</code>.
     *
     * @throws IllegalArgumentException if <code>channel</code> is
     *         <code>null</code> or is not open.
     * @throws IOException if a method invoked on <code>channel</code>
     *         throws an <code>IOException</code>.
     */
    public FileChannelImageOutputStream(FileChannel channel)
	throws IOException {

        // Check the parameter.
        if(channel == null) {
            throw new IllegalArgumentException("channel == null");
        } else if(!channel.isOpen()) {
            throw new IllegalArgumentException("channel.isOpen() == false");
        }

        // Save the channel reference.
        this.channel = channel;

        // Set stream and flushed positions to initial channel position.
        this.streamPos = this.flushedPos = channel.position();

        // Allocate the write buffer.
	byteBuffer = ByteBuffer.allocateDirect(DEFAULT_WRITE_BUFFER_SIZE);

        // Create the read stream (initially zero-sized).
        readStream = new FileChannelImageInputStream(channel);
    }

    /**
     * Returns an <code>ImageInputStream</code> for reading.  The
     * returned stream has byte order, stream and flushed positions,
     * and bit offset set to the current values for this stream.
     */
    private ImageInputStream getImageInputStream() throws IOException {
        // Write any unwritten bytes.
        flushBuffer();

        // Sync input stream state to state of this stream.
        readStream.setByteOrder(byteOrder);
        readStream.seek(streamPos);
        readStream.flushBefore(flushedPos);
        readStream.setBitOffset(bitOffset);

        return readStream;
    }

    /**
     * Write to the <code>FileChannel</code> any remaining bytes in
     * the byte output buffer.
     */
    private void flushBuffer() throws IOException {
        if(byteBuffer.position() != 0) {
            // Set the limit to the position.
            byteBuffer.limit(byteBuffer.position());

            // Set the position to zero.
            byteBuffer.position(0);

            // Write all bytes between zero and the previous position.
            channel.write(byteBuffer);

            // Prepare for subsequent put() calls if any.
            byteBuffer.clear();
        }
    }

    // --- Implementation of superclass abstract methods. ---

    public int read() throws IOException {
        checkClosed();
        bitOffset = 0;

        ImageInputStream inputStream = getImageInputStream();

        streamPos++;

	return inputStream.read();
    }

    public int read(byte[] b, int off, int len) throws IOException {
        // Check parameters.
        if(off < 0 || len < 0 || off + len > b.length) {
            throw new IndexOutOfBoundsException
                ("off < 0 || len < 0 || off + len > b.length");
        } else if(len == 0) {
            return 0;
        }

        checkClosed();
        bitOffset = 0;

        ImageInputStream inputStream = getImageInputStream();

	int numBytesRead = inputStream.read(b, off, len);

	streamPos += numBytesRead;

	return numBytesRead;
    }

    public void write(int b) throws IOException {
        write(new byte[] {(byte)(b&0xff)}, 0, 1);
    }

    public void write(byte[] b, int off, int len) throws IOException {

        // Check parameters.
        if(off < 0 || len < 0 || off + len > b.length) {
            // NullPointerException will be thrown before this if b is null.
            throw new IndexOutOfBoundsException
                ("off < 0 || len < 0 || off + len > b.length");
        } else if(len == 0) {
            return;
        }

        // Flush any bits in the current byte.
        flushBits();

        // Zero the number of bytes to put.
        int numPut = 0;

        // Loop until all bytes have been put.
        do {
            // Determine number of bytes to put.
            int numToPut = Math.min(len - numPut,
                                    byteBuffer.remaining());

            // If no bytes to put, the buffer has to be full as len
            // is always greater than numPut so flush it and return
            // to start of loop.
            if(numToPut == 0) {
                flushBuffer();
                continue;
            }

            // Put the bytes in the buffer.
            byteBuffer.put(b, off + numPut, numToPut);

            // Increment the put counter.
            numPut += numToPut;
        } while(numPut < len);

        // Increment the stream position.
        streamPos += len;
    }

    // --- Overriding of superclass methods. ---

    // --- Bulk read methods ---

    public void readFully(char[] c, int off, int len) throws IOException {
        getImageInputStream().readFully(c, off, len);
        streamPos += 2*len;
    }

    public void readFully(short[] s, int off, int len) throws IOException {
        getImageInputStream().readFully(s, off, len);
        streamPos += 2*len;
    }

    public void readFully(int[] i, int off, int len) throws IOException {
        getImageInputStream().readFully(i, off, len);
        streamPos += 4*len;
    }

    public void readFully(long[] l, int off, int len) throws IOException {
        getImageInputStream().readFully(l, off, len);
        streamPos += 8*len;
    }

    public void readFully(float[] f, int off, int len) throws IOException {
        getImageInputStream().readFully(f, off, len);
        streamPos += 4*len;
    }

    public void readFully(double[] d, int off, int len) throws IOException {
        getImageInputStream().readFully(d, off, len);
        streamPos += 8*len;
    }

    // --- Bulk write methods ---

    public void writeChars(char[] c, int off, int len) throws IOException {

        // Check parameters.
        if(off < 0 || len < 0 || off + len > c.length) {
            // NullPointerException will be thrown before this if c is null.
            throw new IndexOutOfBoundsException
                ("off < 0 || len < 0 || off + len > c.length");
        } else if(len == 0) {
            return;
        }

        // Flush any bits in the current byte.
        flushBits();

        // Zero the number of chars put.
        int numPut = 0;

        // Get view buffer.
        CharBuffer viewBuffer = byteBuffer.asCharBuffer();

        // Loop until all chars have been put.
        do {
            // Determine number of chars to put.
            int numToPut = Math.min(len - numPut,
                                    viewBuffer.remaining());

            // If no chars to put, the buffer has to be full as len
            // is always greater than numPut so flush it and return
            // to start of loop.
            if(numToPut == 0) {
                flushBuffer();
                continue;
            }

            // Put the chars in the buffer.
            viewBuffer.put(c, off + numPut, numToPut);

            // Sync the ByteBuffer position.
            byteBuffer.position(byteBuffer.position() + 2*numToPut);

            // Increment the put counter.
            numPut += numToPut;
        } while(numPut < len);

        // Increment the stream position.
        streamPos += 2*len;
    }

    public void writeShorts(short[] s, int off, int len) throws IOException {

        // Check parameters.
        if(off < 0 || len < 0 || off + len > s.length) {
            // NullPointerException will be thrown before this if s is null.
            throw new IndexOutOfBoundsException
                ("off < 0 || len < 0 || off + len > c.length");
        } else if(len == 0) {
            return;
        }

        // Flush any bits in the current byte.
        flushBits();

        // Zero the number of shorts put.
        int numPut = 0;

        // Get view buffer.
        ShortBuffer viewBuffer = byteBuffer.asShortBuffer();

        // Loop until all shorts have been put.
        do {
            // Determine number of shorts to put.
            int numToPut = Math.min(len - numPut,
                                    viewBuffer.remaining());

            // If no shorts to put, the buffer has to be full as len
            // is always greater than numPut so flush it and return
            // to start of loop.
            if(numToPut == 0) {
                flushBuffer();
                continue;
            }

            // Put the shorts in the buffer.
            viewBuffer.put(s, off + numPut, numToPut);

            // Sync the ByteBuffer position.
            byteBuffer.position(byteBuffer.position() + 2*numToPut);

            // Increment the put counter.
            numPut += numToPut;
        } while(numPut < len);

        // Increment the stream position.
        streamPos += 2*len;
    }

    public void writeInts(int[] i, int off, int len) throws IOException {

        // Check parameters.
        if(off < 0 || len < 0 || off + len > i.length) {
            // NullPointerException will be thrown before this if i is null.
            throw new IndexOutOfBoundsException
                ("off < 0 || len < 0 || off + len > c.length");
        } else if(len == 0) {
            return;
        }

        // Flush any bits in the current byte.
        flushBits();

        // Zero the number of ints put.
        int numPut = 0;

        // Get view buffer.
        IntBuffer viewBuffer = byteBuffer.asIntBuffer();

        // Loop until all ints have been put.
        do {
            // Determine number of ints to put.
            int numToPut = Math.min(len - numPut,
                                    viewBuffer.remaining());

            // If no ints to put, the buffer has to be full as len
            // is always greater than numPut so flush it and return
            // to start of loop.
            if(numToPut == 0) {
                flushBuffer();
                continue;
            }

            // Put the ints in the buffer.
            viewBuffer.put(i, off + numPut, numToPut);

            // Sync the ByteBuffer position.
            byteBuffer.position(byteBuffer.position() + 4*numToPut);

            // Increment the put counter.
            numPut += numToPut;
        } while(numPut < len);

        // Increment the stream position.
        streamPos += 4*len;
    }

    public void writeLongs(long[] l, int off, int len) throws IOException {

        // Check parameters.
        if(off < 0 || len < 0 || off + len > l.length) {
            // NullPointerException will be thrown before this if l is null.
            throw new IndexOutOfBoundsException
                ("off < 0 || len < 0 || off + len > c.length");
        } else if(len == 0) {
            return;
        }

        // Flush any bits in the current byte.
        flushBits();

        // Zero the number of longs put.
        int numPut = 0;

        // Get view buffer.
        LongBuffer viewBuffer = byteBuffer.asLongBuffer();

        // Loop until all longs have been put.
        do {
            // Determine number of longs to put.
            int numToPut = Math.min(len - numPut,
                                    viewBuffer.remaining());

            // If no longs to put, the buffer has to be full as len
            // is always greater than numPut so flush it and return
            // to start of loop.
            if(numToPut == 0) {
                flushBuffer();
                continue;
            }

            // Put the longs in the buffer.
            viewBuffer.put(l, off + numPut, numToPut);

            // Sync the ByteBuffer position.
            byteBuffer.position(byteBuffer.position() + 8*numToPut);

            // Increment the put counter.
            numPut += numToPut;
        } while(numPut < len);

        // Increment the stream position.
        streamPos += 8*len;
    }

    public void writeFloats(float[] f, int off, int len) throws IOException {

        // Check parameters.
        if(off < 0 || len < 0 || off + len > f.length) {
            // NullPointerException will be thrown before this if c is null.
            throw new IndexOutOfBoundsException
                ("off < 0 || len < 0 || off + len > f.length");
        } else if(len == 0) {
            return;
        }

        // Flush any bits in the current byte.
        flushBits();

        // Zero the number of floats put.
        int numPut = 0;

        // Get view buffer.
        FloatBuffer viewBuffer = byteBuffer.asFloatBuffer();

        // Loop until all floats have been put.
        do {
            // Determine number of floats to put.
            int numToPut = Math.min(len - numPut,
                                    viewBuffer.remaining());

            // If no floats to put, the buffer has to be full as len
            // is always greater than numPut so flush it and return
            // to start of loop.
            if(numToPut == 0) {
                flushBuffer();
                continue;
            }

            // Put the floats in the buffer.
            viewBuffer.put(f, off + numPut, numToPut);

            // Sync the ByteBuffer position.
            byteBuffer.position(byteBuffer.position() + 4*numToPut);

            // Increment the put counter.
            numPut += numToPut;
        } while(numPut < len);

        // Increment the stream position.
        streamPos += 4*len;
    }

    public void writeDoubles(double[] d, int off, int len) throws IOException {

        // Check parameters.
        if(off < 0 || len < 0 || off + len > d.length) {
            // NullPointerException will be thrown before this if d is null.
            throw new IndexOutOfBoundsException
                ("off < 0 || len < 0 || off + len > d.length");
        } else if(len == 0) {
            return;
        }

        // Flush any bits in the current byte.
        flushBits();

        // Zero the number of doubles put.
        int numPut = 0;

        // Get view buffer.
        DoubleBuffer viewBuffer = byteBuffer.asDoubleBuffer();

        // Loop until all doubles have been put.
        do {
            // Determine number of doubles to put.
            int numToPut = Math.min(len - numPut,
                                    viewBuffer.remaining());

            // If no doubles to put, the buffer has to be full as len
            // is always greater than numPut so flush it and return
            // to start of loop.
            if(numToPut == 0) {
                flushBuffer();
                continue;
            }

            // Put the doubles in the buffer.
            viewBuffer.put(d, off + numPut, numToPut);

            // Sync the ByteBuffer position.
            byteBuffer.position(byteBuffer.position() + 8*numToPut);

            // Increment the put counter.
            numPut += numToPut;
        } while(numPut < len);

        // Increment the stream position.
        streamPos += 8*len;
    }

    // --- Other methods ---

    /**
     * Invokes the superclass method, writes any unwritten data, and
     * sets the internal reference to the source <code>FileChannel</code>
     * to <code>null</code>.  The source <code>FileChannel</code> is not
     * closed.
     *
     * @exception IOException if an error occurs.
     */
    // Note that this method is called by the superclass finalize()
    // so this class does not need to implement finalize().
    public void close() throws IOException {
        // Flush any unwritten data in the buffer.
        flushBuffer();

        // Close the read channel and clear the reference to it.
        readStream.close();
        readStream = null;

        // Clear reference to the channel.
        channel = null;

        // Clear reference to the internal ByteBuffer.
        byteBuffer = null;

        // Chain to the superclass.
        super.close();
    }

    /**
     * Returns the number of bytes currently in the <code>FileChannel</code>.
     * If an <code>IOException</code> is encountered when querying the
     * channel's size, -1L will be returned.
     *
     * @return The number of bytes in the channel
     * -1L to indicate unknown length.
     */
    public long length() {
        // Initialize to value indicating unknown length.
        long length = -1L;

        // Set length to current size with respect to initial position.
        try {
            length = channel.size();
        } catch(IOException e) {
            // Default to unknown length.
        }

        return length;
    }

    /**
     * Invokes the superclass method, writes any unwritten data,
     * and sets the channel position to the supplied parameter.
     */
    public void seek(long pos) throws IOException {
        super.seek(pos);

        // Flush any unwritten data in the buffer.
        flushBuffer();

        // Set the FileChannel position for WritableByteChannel.write().
	channel.position(pos);
    }

    public void setByteOrder(ByteOrder networkByteOrder) {
        super.setByteOrder(networkByteOrder);
        byteBuffer.order(networkByteOrder);
    }
}
