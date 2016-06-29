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
 * $RCSfile: ISRandomAccessIO.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:25 $
 * $State: Exp $
 *
 * Class:                   ISRandomAccessIO
 *
 * Description:             Turns an InsputStream into a read-only
 *                          RandomAccessIO, using buffering.
 *
 *
 * COPYRIGHT:
 *
 * This software module was originally developed by Raphaël Grosbois and
 * Diego Santa Cruz (Swiss Federal Institute of Technology-EPFL); Joel
 * Askelöf (Ericsson Radio Systems AB); and Bertrand Berthelot, David
 * Bouchard, Félix Henry, Gerard Mozelle and Patrice Onno (Canon Research
 * Centre France S.A) in the course of development of the JPEG2000
 * standard as specified by ISO/IEC 15444 (JPEG 2000 Standard). This
 * software module is an implementation of a part of the JPEG 2000
 * Standard. Swiss Federal Institute of Technology-EPFL, Ericsson Radio
 * Systems AB and Canon Research Centre France S.A (collectively JJ2000
 * Partners) agree not to assert against ISO/IEC and users of the JPEG
 * 2000 Standard (Users) any of their rights under the copyright, not
 * including other intellectual property rights, for this software module
 * with respect to the usage by ISO/IEC and Users of this software module
 * or modifications thereof for use in hardware or software products
 * claiming conformance to the JPEG 2000 Standard. Those intending to use
 * this software module in hardware or software products are advised that
 * their use may infringe existing patents. The original developers of
 * this software module, JJ2000 Partners and ISO/IEC assume no liability
 * for use of this software module or modifications thereof. No license
 * or right to this software module is granted for non JPEG 2000 Standard
 * conforming products. JJ2000 Partners have full right to use this
 * software module for his/her own purpose, assign or donate this
 * software module to any third party and to inhibit third parties from
 * using this software module for non JPEG 2000 Standard conforming
 * products. This copyright notice must be included in all copies or
 * derivative works of this software module.
 *
 * Copyright (c) 1999/2000 JJ2000 Partners.
 *
 */
package jj2000.j2k.util;

import java.io.*;
import jj2000.j2k.io.*;

/**
 * This class implements a wrapper to turn an InputStream into a
 * RandomAccessIO. To provide random access the input data from the
 * InputStream is cached in an in-memory buffer. The in-memory buffer size can
 * be limited to a specified size. The data is read into the cache on a as
 * needed basis, blocking only when necessary.
 *
 * <P>The cache grows automatically as necessary. However, if the data length
 * is known prior to the creation of a ISRandomAccessIO object, it is best to
 * specify that as the initial in-memory buffer size. That will minimize data
 * copying and multiple allocation.
 *
 * <P>Multi-byte data is read in big-endian order. The in-memory buffer
 * storage is released when 'close()' is called. This class can only be used
 * for data input, not output. The wrapped InputStream is closed when all the
 * input data is cached or when 'close()' is called.
 *
 * <P>If an out of memory condition is encountered when growing the
 * in-memory buffer an IOException is thrown instead of an
 * OutOfMemoryError. The exception message is "Out of memory to cache input
 * data".
 *
 * <P>This class is intended for use as a "quick and dirty" way to give
 * network connectivity to RandomAccessIO based classes. It is not intended as
 * an efficient means of implementing network connectivity. Doing such
 * requires reimplementing the RandomAccessIO based classes to directly use
 * network connections.
 *
 * <P>This class does not use temporary files as buffers, because that would
 * preclude the use in unsigned applets.
 * */
public class ISRandomAccessIO implements RandomAccessIO {

    /** The InputStream that is wrapped */
    private InputStream is;

    /* Tha maximum size, in bytes, of the in memory buffer. The maximum size
     * includes the EOF. */
    private int maxsize;

    /* The increment, in bytes, for the in-memory buffer size */
    private int inc;

    /* The in-memory buffer to cache received data */
    private byte buf[];

    /* The length of the already received data */
    private int len;

    /* The position of the next byte to be read from the in-memory buffer */
    private int pos;

    /* Flag to indicate if all the data has been received. That is, if the EOF
     * has been reached. */
    private boolean complete;

    /**
     * Creates a new RandomAccessIO wrapper for the given InputStream
     * 'is'. The internal cache buffer will have size 'size' and will
     * increment by 'inc' each time it is needed. The maximum buffer size is
     * limited to 'maxsize'.
     *
     * @param is The input from where to get the data.
     *
     * @param size The initial size for the cache buffer, in bytes.
     *
     * @param inc The size increment for the cache buffer, in bytes.
     *
     * @param maxsize The maximum size for the cache buffer, in bytes.
     */
    public ISRandomAccessIO(InputStream is, int size, int inc, int maxsize) {
        if (size < 0 || inc <= 0 || maxsize <= 0 || is == null) {
            throw new IllegalArgumentException();
        }
        this.is = is;
        // Increase size by one to count in EOF
        if (size < Integer.MAX_VALUE) size++;
        buf = new byte[size];
        this.inc = inc;
        // The maximum size is one byte more, to allow reading the EOF.
        if (maxsize < Integer.MAX_VALUE) maxsize++;
        this.maxsize = maxsize;
        pos = 0;
        len = 0;
        complete = false;
    }

    /**
     * Creates a new RandomAccessIO wrapper for the given InputStream
     * 'is'. The internal cache buffer size and increment is to to 256 kB. The
     * maximum buffer size is set to Integer.MAX_VALUE (2 GB).
     *
     * @param is The input from where to get the data.
     *
     */
    public ISRandomAccessIO(InputStream is) {
        this(is,1<<18,1<<18,Integer.MAX_VALUE);
    }

    /**
     * Grows the cache buffer by 'inc', upto a maximum of 'maxsize'. The
     * buffer size will be increased by at least one byte, if no exception is
     * thrown.
     *
     * @exception IOException If the maximum cache size is reached or if not
     * enough memory is available to grow the buffer.
     */
    private void growBuffer() throws IOException {
        byte newbuf[];
        int effinc;           // effective increment

        effinc = inc;
        if (buf.length+effinc > maxsize) effinc = maxsize-buf.length;
        if (effinc <= 0) {
            throw new IOException("Reached maximum cache size ("+maxsize+")");
        }
        try {
            newbuf = new byte[buf.length+inc];
        } catch (OutOfMemoryError e) {
            throw new IOException("Out of memory to cache input data");
        }
        System.arraycopy(buf,0,newbuf,0,len);
        buf = newbuf;
    }

    /**
     * Reads data from the wrapped InputStream and places it in the cache
     * buffer. Reads all input data that will not cause it to block, but at
     * least on byte is read (even if it blocks), unless EOF is reached. This
     * method can not be called if EOF has been already reached
     * (i.e. 'complete' is true).
     *
     * @exception IOException An I/O error occurred, out of meory to grow
     * cache or maximum cache size reached.
     * */
    private void readInput() throws IOException {
        int n;
        int b;
        int k;

        if (complete) {
            throw new IllegalArgumentException("Already reached EOF");
        }
//        may need reflection to call this method
//        n = is.available(); /* how much can we read without blocking? */
        n = 0; /* how much can we read without blocking? */
        if (n == 0) n = 1;  /* read at least one byte (even if it blocks) */
        while (len+n > buf.length) { /* Ensure buffer size */
            growBuffer();
        }
        /* Read the data. Loop to be sure that we do read 'n' bytes */
        do {
            k = is.read(buf,len,n);
            if (k > 0) { /* Some data was read */
                len += k;
                n -= k;
            }
        } while (n > 0 && k > 0);
        if (k <= 0) { /* we reached EOF */
            complete = true;
            is = null;
        }
    }

    /**
     * Closes this object for reading as well as the wrapped InputStream, if
     * not already closed. The memory used by the cache is released.
     *
     * @exception IOException If an I/O error occurs while closing the
     * underlying InputStream.  */
    public void close() throws IOException {
        buf = null;
        if (!complete) {
            is.close();
            is = null;
        }
    }

    /**
     * Returns the current position in the stream, which is the
     * position from where the next byte of data would be read. The first
     * byte in the stream is in position 0.
     *
     * @exception IOException If an I/O error occurred.
     * */
    public int getPos() throws IOException {
        return pos;
    }

    /**
     * Moves the current position for the next read operation to
     * offset. The offset is measured from the beginning of the stream. If the
     * offset is set beyond the currently cached data, the missing data will
     * be read only when a read operation is performed. Setting
     * the offset beyond the end of the data will cause an EOFException only
     * if the data length is currently known, otherwise an IOException will
     * occur when a read operation is attempted at that position.
     *
     * @param off The offset where to move to.
     *
     * @exception EOFException If seeking beyond EOF and the data length is
     * known.
     *
     * @exception IOException If an I/O error ocurred.
     * */
    public void seek(int off) throws IOException {
        if (complete) { /* we know the length, check seek is within length */
            if (off > len) {
                throw new EOFException();
            }
        }
        pos = off;
    }

    /**
     * Returns the length of the stream. This will cause all the data to be
     * read. This method will block until all the data is read, which can be
     * lengthy across the network.
     *
     * @return The length of the stream, in bytes.
     *
     * @exception IOException If an I/O error ocurred.
     */
    public int length() throws IOException {
        if (Integer.MAX_VALUE != maxsize)
            return maxsize - 1;
        while (!complete) { // read until we reach EOF
            readInput();
        }
        return len;
    }

    /**
     * Reads a byte of data from the stream.
     *
     * @return The byte read, as an int in the range [0-255].
     *
     * @exception EOFException If the end-of file was reached.
     *
     * @exception IOException If an I/O error ocurred.
     *
     */
    public int read() throws IOException {
        if (pos < len) { // common, fast case
            return 0xFF & (int)buf[pos++];
        }
        // general case
        while (!complete && pos >= len) {
            readInput();
        }
        if (pos == len) {
            throw new EOFException();
        } else if (pos > len) {
            throw new IOException("Position beyond EOF");
        }
        return 0xFF & (int)buf[pos++];
    }

    /**
     * Reads 'len' bytes of data from this file into an array of bytes. This
     * method reads repeatedly from the stream until all the bytes are
     * read. This method blocks until all the bytes are read, the end of the
     * stream is detected, or an exception is thrown.
     *
     * @param b The buffer into which the data is to be read. It must be long
     * enough.
     *
     * @param off The index in 'b' where to place the first byte read.
     *
     * @param n The number of bytes to read.
     *
     * @exception EOFException If the end-of file was reached before
     * getting all the necessary data.
     *
     * @exception IOException If an I/O error ocurred.
     *
     * */
    public void readFully(byte b[], int off, int n) throws IOException {
        if (pos+n <= len) { // common, fast case
            System.arraycopy(buf,pos,b,off,n);
            pos += n;
            return;
        }
        // general case
        while (!complete && pos+n > len) {
            readInput();
        }
        if (pos+n > len) {
            throw new EOFException();
        }
        System.arraycopy(buf,pos,b,off,n);
        pos += n;
    }

    /**
     * Returns the endianess (i.e., byte ordering) of multi-byte I/O
     * operations. Always EndianType.BIG_ENDIAN since this class implements
     * only big-endian.
     *
     * @return Always EndianType.BIG_ENDIAN.
     *
     * @see EndianType
     *
     */
    public int getByteOrdering() {
        return EndianType.BIG_ENDIAN;
    }

    /**
     * Reads a signed byte (8 bit) from the input.
     *
     * @return The next byte-aligned signed byte (8 bit) from the
     * input.
     *
     * @exception EOFException If the end-of file was reached before
     * getting all the necessary data.
     *
     * @exception IOException If an I/O error ocurred.
     * */
    public byte readByte() throws IOException {
        if (pos < len) { // common, fast case
            return buf[pos++];
        }
        // general case
        return (byte) read();
    }

    /**
     * Reads an unsigned byte (8 bit) from the input.
     *
     * @return The next byte-aligned unsigned byte (8 bit) from the
     * input.
     *
     * @exception EOFException If the end-of file was reached before
     * getting all the necessary data.
     *
     * @exception IOException If an I/O error ocurred.
     * */
    public int readUnsignedByte() throws IOException {
        if (pos < len) { // common, fast case
            return 0xFF & buf[pos++];
        }
        // general case
        return read();
    }

    /**
     * Reads a signed short (16 bit) from the input.
     *
     * @return The next byte-aligned signed short (16 bit) from the
     * input.
     *
     * @exception EOFException If the end-of file was reached before
     * getting all the necessary data.
     *
     * @exception IOException If an I/O error ocurred.
     * */
    public short readShort() throws IOException {
        if (pos+1 < len) { // common, fast case
            return (short) ((buf[pos++]<<8) | (0xFF & buf[pos++]));
        }
        // general case
        return (short) ((read()<<8) | read());
    }

    /**
     * Reads an unsigned short (16 bit) from the input.
     *
     * @return The next byte-aligned unsigned short (16 bit) from the
     * input.
     *
     * @exception EOFException If the end-of file was reached before
     * getting all the necessary data.
     *
     * @exception IOException If an I/O error ocurred.
     * */
    public int readUnsignedShort() throws IOException {
        if (pos+1 < len) { // common, fast case
            return ((0xFF & buf[pos++])<<8) | (0xFF & buf[pos++]);
        }
        // general case
        return (read()<<8) | read();
    }

    /**
     * Reads a signed int (32 bit) from the input.
     *
     * @return The next byte-aligned signed int (32 bit) from the
     * input.
     *
     * @exception EOFException If the end-of file was reached before
     * getting all the necessary data.
     *
     * @exception IOException If an I/O error ocurred.
     * */
    public int readInt() throws IOException {
        if (pos+3 < len) { // common, fast case
            return ((buf[pos++]<<24) | ((0xFF & buf[pos++])<<16)
                    | ((0xFF & buf[pos++])<<8) | (0xFF & buf[pos++]));
        }
        // general case
        return (read()<<24) | (read()<<16) | (read()<<8) | read();
    }

    /**
     * Reads a unsigned int (32 bit) from the input.
     *
     * @return The next byte-aligned unsigned int (32 bit) from the
     * input.
     *
     * @exception EOFException If the end-of file was reached before
     * getting all the necessary data.
     *
     * @exception IOException If an I/O error ocurred.
     * */
    public long readUnsignedInt() throws IOException {
        if (pos+3 < len) { // common, fast case
            return (0xFFFFFFFFL
                    & (long)((buf[pos++]<<24) | ((0xFF & buf[pos++])<<16)
                             | ((0xFF & buf[pos++])<<8) | (0xFF & buf[pos++])));
        }
        // general case
        return (0xFFFFFFFFL
                & (long)((read()<<24) | (read()<<16) | (read()<<8) | read()));
    }

    /**
     * Reads a signed long (64 bit) from the input.
     *
     * @return The next byte-aligned signed long (64 bit) from the
     * input.
     *
     * @exception EOFException If the end-of file was reached before
     * getting all the necessary data.
     *
     * @exception IOException If an I/O error ocurred.
     * */
    public long readLong() throws IOException {
        if (pos+7 < len) { // common, fast case
            return (((long)buf[pos++]<<56)
                    | ((long)(0xFF&buf[pos++])<<48)
                    | ((long)(0xFF&buf[pos++])<<40)
                    | ((long)(0xFF&buf[pos++])<<32)
                    | ((long)(0xFF&buf[pos++])<<24)
                    | ((long)(0xFF&buf[pos++])<<16)
                    | ((long)(0xFF&buf[pos++])<<8)
                    | (long)(0xFF&buf[pos++]));
        }
        // general case
        return (((long)read()<<56)
                | ((long)read()<<48)
                | ((long)read()<<40)
                | ((long)read()<<32)
                | ((long)read()<<24)
                | ((long)read()<<16)
                | ((long)read()<<8)
                | (long)read());
    }

    /**
     * Reads an IEEE single precision (i.e., 32 bit) floating-point number
     * from the input.
     *
     * @return The next byte-aligned IEEE float (32 bit) from the input.
     *
     * @exception EOFException If the end-of file was reached before
     * getting all the necessary data.
     *
     * @exception IOException If an I/O error ocurred.
     * */
    public float readFloat() throws IOException {
        if (pos+3 < len) { // common, fast case
            return Float.intBitsToFloat((buf[pos++]<<24)
                                        | ((0xFF & buf[pos++])<<16)
                                        | ((0xFF & buf[pos++])<<8)
                                        | (0xFF & buf[pos++]));
        }
        // general case
        return Float.intBitsToFloat((read()<<24) | (read()<<16)
                                    | (read()<<8) | read());
    }

    /**
     * Reads an IEEE double precision (i.e., 64 bit) floating-point number
     * from the input.
     *
     * @return The next byte-aligned IEEE double (64 bit) from the input.
     *
     * @exception EOFException If the end-of file was reached before
     * getting all the necessary data.
     *
     * @exception IOException If an I/O error ocurred.
     * */
    public double readDouble() throws IOException {
        if (pos+7 < len) { // common, fast case
            return Double.longBitsToDouble(((long)buf[pos++]<<56)
                                           | ((long)(0xFF&buf[pos++])<<48)
                                           | ((long)(0xFF&buf[pos++])<<40)
                                           | ((long)(0xFF&buf[pos++])<<32)
                                           | ((long)(0xFF&buf[pos++])<<24)
                                           | ((long)(0xFF&buf[pos++])<<16)
                                           | ((long)(0xFF&buf[pos++])<<8)
                                           | (long)(0xFF&buf[pos++]));
        }
        // general case
        return Double.longBitsToDouble(((long)read()<<56)
                                       | ((long)read()<<48)
                                       | ((long)read()<<40)
                                       | ((long)read()<<32)
                                       | ((long)read()<<24)
                                       | ((long)read()<<16)
                                       | ((long)read()<<8)
                                       | (long)read());
    }

    /**
     * Skips 'n' bytes from the input.
     *
     * @param n The number of bytes to skip
     *
     * @return Always n.
     *
     * @exception EOFException If the end-of file was reached before
     * all the bytes could be skipped.
     *
     * @exception IOException If an I/O error ocurred.
     * */
    public int skipBytes(int n) throws IOException {
        if (complete) { /* we know the length, check skip is within length */
            if (pos+n > len) {
                throw new EOFException();
            }
        }
        pos += n;
        return n;
    }

    /**
     * Does nothing since this class does not implement data output.
     */
    public void flush() { /* no-op */
    }

    /**
     * Throws an IOException since this class does not implement data output.
     */
    public void write(int b) throws IOException {
        throw new IOException("read-only");
    }

    /**
     * Throws an IOException since this class does not implement data output.
     */
    public void writeByte(int v) throws IOException {
        throw new IOException("read-only");
    }

    /**
     * Throws an IOException since this class does not implement data output.
     */
    public void writeShort(int v) throws IOException {
        throw new IOException("read-only");
    }

    /**
     * Throws an IOException since this class does not implement data output.
     */
    public void writeInt(int v) throws IOException {
        throw new IOException("read-only");
    }

    /**
     * Throws an IOException since this class does not implement data output.
     */
    public void writeLong(long v) throws IOException {
        throw new IOException("read-only");
    }

    /**
     * Throws an IOException since this class does not implement data output.
     */
    public void writeFloat(float v) throws IOException {
        throw new IOException("read-only");
    }

    /**
     * Throws an IOException since this class does not implement data output.
     */
    public void writeDouble(double v) throws IOException {
        throw new IOException("read-only");
    }
}
