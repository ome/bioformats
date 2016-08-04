/*
 * #%L
 * Fork of JAI Image I/O Tools.
 * %%
 * Copyright (C) 2008 - 2015 Open Microscopy Environment:
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
 * $RCSfile: BitOutputBuffer.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:02 $
 * $State: Exp $
 *
 * Class:                   BitOutputBuffer
 *
 * Description:             <short description of class>
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
 * */
package jj2000.j2k.codestream.writer;

import jj2000.j2k.util.*;

/**
 * This class implements a buffer for writing bits, with the required bit
 * stuffing policy for the packet headers. The bits are stored in a byte array
 * in the order in which they are written. The byte array is automatically
 * reallocated and enlarged whenever necessary. A BitOutputBuffer object may
 * be reused by calling its 'reset()' method.
 *
 * <P>NOTE: The methods implemented in this class are intended to be used only
 * in writing packet heads, since a special bit stuffing procedure is used, as
 * required for the packet heads.
 * */
public class BitOutputBuffer {

    /** The buffer where we store the data */
    byte buf[];

    /** The position of the current byte to write */
    int curbyte;

    /** The number of available bits in the current byte */
    int avbits = 8;

    /** The increment size for the buffer, 16 bytes. This is the
     * number of bytes that are added to the buffer each time it is
     * needed to enlarge it.*/
    // This must be always 6 or larger.
    public final static int SZ_INCR = 16;

    /** The initial size for the buffer, 32 bytes. */
    public final static int SZ_INIT = 32;

    /**
     * Creates a new BitOutputBuffer width a buffer of length
     * 'SZ_INIT'.
     * */
    public BitOutputBuffer() {
        buf = new byte[SZ_INIT];
    }

    /**
     * Resets the buffer. This rewinds the current position to the start of
     * the buffer and sets all tha data to 0. Note that no new buffer is
     * allocated, so this will affect any data that was returned by the
     * 'getBuffer()' method.
     * */
    public void reset() {
        int i;
        // Reinit pointers
        curbyte = 0;
        avbits = 8;
        ArrayUtil.byteArraySet(buf,(byte)0);
    }

    /**
     * Writes a bit to the buffer at the current position. The value 'bit'
     * must be either 0 or 1, otherwise it corrupts the bits that have been
     * already written. The buffer is enlarged, by 'SZ_INCR' bytes, if
     * necessary.
     *
     * <P>This method is declared final to increase performance.
     *
     * @param bit The bit to write, 0 or 1.
     * */
    public final void writeBit(int bit) {
        buf[curbyte] |= bit << --avbits;
        if (avbits > 0) {
            // There is still place in current byte for next bit
            return;
        }
        else { // End of current byte => goto next
            if (buf[curbyte] != (byte) 0xFF) { // We don't need bit stuffing
                avbits = 8;
            }
            else { // We need to stuff a bit (next MSBit is 0)
                avbits = 7;
            }
            curbyte++;
            if (curbyte == buf.length) {
                // We are at end of 'buf' => extend it
                byte oldbuf[] = buf;
                buf = new byte[oldbuf.length+SZ_INCR];
                System.arraycopy(oldbuf,0,buf,0,oldbuf.length);
            }
        }
    }

    /**
     * Writes the n least significant bits of 'bits' to the buffer at the
     * current position. The least significant bit is written last. The 32-n
     * most significant bits of 'bits' must be 0, otherwise corruption of the
     * buffer will result. The buffer is enlarged, by 'SZ_INCR' bytes, if
     * necessary.
     *
     * <P>This method is declared final to increase performance.
     *
     * @param bits The bits to write.
     *
     * @param n The number of LSBs in 'bits' to write.
     * */
    public final void writeBits(int bits, int n) {
        // Check that we have enough place in 'buf' for n bits, and that we do
        // not fill last byte, taking into account possibly stuffed bits (max
        // 2)
        if (((buf.length-curbyte)<<3)-8+avbits <= n+2) {
            // Not enough place, extend it
            byte oldbuf[] = buf;
            buf = new byte[oldbuf.length+SZ_INCR];
            System.arraycopy(oldbuf,0,buf,0,oldbuf.length);
            // SZ_INCR is always 6 or more, so it is enough to hold all the
            // new bits plus the ones to come after
        }
        // Now write the bits
        if (n >= avbits) {
            // Complete the current byte
            n -= avbits;
            buf[curbyte] |= bits >> n;
            if (buf[curbyte] != (byte) 0xFF) { // We don't need bit stuffing
                avbits = 8;
            }
            else { // We need to stuff a bit (next MSBit is 0)
                avbits = 7;
            }
            curbyte++;
            // Write whole bytes
            while (n >= avbits) {
                n -= avbits;
                buf[curbyte] |= (bits >> n) & (~(1 << avbits));
                if (buf[curbyte] != (byte) 0xFF) { // We don't need bit
                    // stuffing
                    avbits = 8;
                }
                else { // We need to stuff a bit (next MSBit is 0)
                    avbits = 7;
                }
                curbyte++;
            }
        }
        // Finish last byte (we know that now n < avbits)
        if (n > 0) {
            avbits -= n;
            buf[curbyte] |= (bits & ((1<<n)-1)) << avbits;
        }
        if (avbits == 0) { // Last byte is full
            if (buf[curbyte] != (byte) 0xFF) { // We don't need bit stuffing
                avbits = 8;
            }
            else { // We need to stuff a bit (next MSBit is 0)
                avbits = 7;
            }
            curbyte++; // We already ensured that we have enough place
        }
    }

    /**
     * Returns the current length of the buffer, in bytes.
     *
     * <P>This method is declared final to increase performance.
     *
     * @return The currebt length of the buffer in bytes.
     * */
    public final int getLength() {
        if (avbits == 8) { // A integral number of bytes
            return curbyte;
        }
        else { // Some bits in last byte
            return curbyte+1;
        }
    }

    /**
     * Returns the byte buffer. This is the internal byte buffer so it should
     * not be modified. Only the first N elements have valid data, where N is
     * the value returned by 'getLength()'
     *
     * <P>This method is declared final to increase performance.
     *
     * @return The internal byte buffer.
     * */
    public final byte[] getBuffer() {
        return buf;
    }

    /**
     * Returns the byte buffer data in a new array. This is a copy of the
     * internal byte buffer. If 'data' is non-null it is used to return the
     * data. This array should be large enough to contain all the data,
     * otherwise a IndexOutOfBoundsException is thrown by the Java system. The
     * number of elements returned is what 'getLength()' returns.
     *
     * @param data If non-null this array is used to return the data, which
     * mus be large enough. Otherwise a new one is created and returned.
     *
     * @return The byte buffer data.
     * */
    public byte[] toByteArray(byte data[]) {
        if (data == null) {
            data = new byte[(avbits==8)?curbyte:curbyte+1];
        }
        System.arraycopy(buf,0,data,0,(avbits==8)?curbyte:curbyte+1);
        return data;
    }

    /**
     * Prints information about this object for debugging purposes
     *
     * @return Information about the object.
     * */
    public String toString() {
        return "bits written = "+(curbyte*8+(8-avbits))+
            ", curbyte = "+curbyte+", avbits = "+avbits;
    }
}
