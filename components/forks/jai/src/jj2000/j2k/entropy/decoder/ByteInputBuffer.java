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
 * $RCSfile: ByteInputBuffer.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:05 $
 * $State: Exp $
 *
 * Class:                   ByteInputBuffer
 *
 * Description:             Provides buffering for byte based input, similar
 *                          to the standard class ByteArrayInputStream
 *
 *                          the old jj2000.j2k.io.ByteArrayInput class by
 *                          Diego SANTA CRUZ, Apr-26-1999
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


package jj2000.j2k.entropy.decoder;

import java.io.*;

/**
 * This class provides a byte input facility from byte buffers. It is similar
 * to the ByteArrayInputStream class, but adds the possibility to add data to
 * the stream after the creation of the object.
 *
 * <P>Unlike the ByteArrayInputStream this class is not thread safe (i.e. no
 * two threads can use the same object at the same time, but different objects
 * may be used in different threads).
 *
 * <P>This class can modify the contents of the buffer given to the
 * constructor, when the addByteArray() method is called.
 *
 * @see InputStream
 * */
public class ByteInputBuffer {

    /** The byte array containing the data */
    private byte buf[];

    /** The index one greater than the last valid character in the input
     *  stream buffer */
    private int count;

    /** The index of the next character to read from the input stream buffer
     * */
    private int pos;

    /**
     * Creates a new byte array input stream that reads data from the
     * specified byte array. The byte array is not copied.
     *
     * @param buf the input buffer.
     */
    public ByteInputBuffer(byte buf[]){
        this.buf = buf;
        count = buf.length;
    }

    /**
     * Creates a new byte array input stream that reads data from the
     * specified byte array. Up to length characters are to be read
     * from the byte array, starting at the indicated offset.
     *
     * <P>The byte array is not copied.
     *
     * @param buf the input buffer.
     *
     * @param offset the offset in the buffer of the first byte to
     * read.
     *
     * @param length the maximum number of bytes to read from the
     * buffer.
     */
    public ByteInputBuffer(byte buf[], int offset, int length) {
        this.buf = buf;
        pos = offset;
        count = offset+length;
    }

    /**
     * Sets the underlying buffer byte array to the given one, with the given
     * offset and length. If 'buf' is null then the current byte buffer is
     * assumed. If 'offset' is negative, then it will be assumed to be
     * 'off+len', where 'off' and 'len' are the offset and length of the
     * current byte buffer.
     *
     * <P>The byte array is not copied.
     *
     * @param buf the input buffer. If null it is the current input buffer.
     *
     * @param offset the offset in the buffer of the first byte to read. If
     * negative it is assumed to be the byte just after the end of the current
     * input buffer, only permitted if 'buf' is null.
     *
     * @param length the maximum number of bytes to read frmo the buffer.
     */
    public void setByteArray(byte buf[], int offset, int length) {
        // In same buffer?
        if (buf == null) {
            if (length < 0 || count+length>this.buf.length) {
                throw new IllegalArgumentException();
            }
            if (offset < 0) {
                pos = count;
                count += length;
            } else {
                count = offset+length;
                pos = offset;
            }
        } else { // New input buffer
            if (offset < 0 || length < 0 || offset+length > buf.length) {
                throw new IllegalArgumentException();
            }
            this.buf = buf;
            count = offset+length;
            pos = offset;
        }
    }

    /**
     * Adds the specified data to the end of the byte array stream. This
     * method modifies the byte array buffer. It can also discard the already
     * read input.
     *
     * @param data The data to add. The data is copied.
     *
     * @param off The index, in data, of the first element to add to
     * the stream.
     *
     * @param len The number of elements to add to the array.
     *
     *
     * */
    public synchronized void addByteArray(byte data[], int off, int len) {
        // Check integrity
        if (len < 0 || off < 0 || len+off > buf.length) {
            throw new IllegalArgumentException();
        }
        // Copy new data
        if (count+len <= buf.length) { // Enough place in 'buf'
            System.arraycopy(data,off,buf,count,len);
            count += len;
        } else {
            if (count-pos+len <= buf.length) {
                // Enough place in 'buf' if we move input data
                // Move buffer
                System.arraycopy(buf,pos,buf,0,count-pos);
            } else { // Not enough place in 'buf', use new buffer
                byte [] oldbuf = buf;
                buf = new byte[count-pos+len];
                // Copy buffer
                System.arraycopy(oldbuf,count,buf,0,count-pos);
            }
            count -= pos;
            pos = 0;
            // Copy new data
            System.arraycopy(data,off,buf,count,len);
            count += len;
        }
    }

    /**
     * Reads the next byte of data from this input stream. The value
     * byte is returned as an int in the range 0 to 255. If no byte is
     * available because the end of the stream has been reached, the
     * EOFException exception is thrown.
     *
     * <P>This method is not synchronized, so it is not thread safe.
     *
     * @return The byte read in the range 0-255.
     *
     * @exception EOFException If the end of the stream is reached.
     *
     *
     * */
    public int readChecked() throws IOException {
        if (pos < count) {
            return (int)buf[pos++] & 0xFF;
        } else {
            throw new EOFException();
        }
    }

    /**
     * Reads the next byte of data from this input stream. The value byte is
     * returned as an int in the range 0 to 255. If no byte is available
     * because the end of the stream has been reached, -1 is returned.
     *
     * <P>This method is not synchronized, so it is not thread safe.
     *
     * @return The byte read in the range 0-255, or -1 if the end of stream
     * has been reached.
     *
     *
     * */
    public int read() {
        if (pos < count) {
            return (int)buf[pos++] & 0xFF;
        } else {
            return -1;
        }
    }

}
