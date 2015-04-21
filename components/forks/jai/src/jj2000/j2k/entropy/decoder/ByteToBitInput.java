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
 * $RCSfile: ByteToBitInput.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:06 $
 * $State: Exp $
 *
 * Class:                   ByteToBitInput
 *
 * Description:             Adapter to perform bit based input from a byte
 *                          based one.
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
package jj2000.j2k.entropy.decoder;

import jj2000.j2k.io.*;

/**
 * This class provides an adapter to perform bit based input on byte based
 * output obejcts that inherit from a 'ByteInputBuffer' class. This class also
 * performs the bit unstuffing procedure specified for the 'selective
 * arithmetic coding bypass' mode of the JPEG 2000 entropy coder.
 * */
class ByteToBitInput {

    /** The byte based input */
    ByteInputBuffer in;

    /** The bit buffer */
    int bbuf;

    /** The position of the next bit to get from the byte buffer. When it is
     * -1 the bit buffer is empty. */
    int bpos = -1;

    /** Instantiates a new 'ByteToBitInput' object that uses 'in' as the
     * underlying byte based input.
     *
     * @param in The underlying byte based input.
     * */
    ByteToBitInput(ByteInputBuffer in) {
        this.in = in;
    }

    /**
     * Reads from the bit stream one bit. If 'bpos' is -1 then a byte is read
     * and loaded into the bit buffer, from where the bit is read. If
     * necessary the bit unstuffing will be applied.
     *
     * @return The read bit (0 or 1).
     * */
    final int readBit() {
        if (bpos < 0) {
            if ((bbuf&0xFF) != 0xFF) { // Normal byte to read
                bbuf = in.read();
                bpos = 7;
            }
            else { // Previous byte is 0xFF => there was bit stuffing
                bbuf = in.read();
                bpos = 6;
            }
        }
        return (bbuf>>bpos--)&0x01;
    }

    /**
     * Checks for past errors in the decoding process by verifying the byte
     * padding with an alternating sequence of 0's and 1's. If an error is
     * detected it means that the raw bit stream has been wrongly decoded or
     * that the raw terminated segment length is too long. If no errors are
     * detected it does not necessarily mean that the raw bit stream has been
     * correctly decoded.
     *
     * @return True if errors are found, false otherwise.
     * */
    public boolean checkBytePadding() {
        int seq; // Byte padding sequence in last byte

        // If there are no spare bits and bbuf is 0xFF (not EOF), then there
        // is a next byte with bit stuffing that we must load.
        if (bpos < 0 && (bbuf & 0xFF) == 0xFF) {
            bbuf = in.read();
            bpos = 6;
        }

        // 1) Not yet read bits in the last byte must be an alternating
        // sequence of 0s and 1s, starting with 0.
        if (bpos>=0) {
            seq = bbuf&((1<<(bpos+1))-1);
            if (seq != (0x55>>(7-bpos))) return true;
        }

        // 2) We must have already reached the last byte in the terminated
        // segment, unless last bit read is LSB of FF in which case an encoder
        // can output an extra byte which is smaller than 0x80.
        if (bbuf != -1) {
            if (bbuf == 0xFF && bpos == 0) {
                if ((in.read()&0xFF) >= 0x80) return true;
            }
            else {
                if (in.read() != -1) return true;
            }
        }

        // Nothing detected
        return false;
    }

    /**
     * Flushes (i.e. empties) the bit buffer, without loading any new
     * bytes. This realigns the input at the next byte boundary, if not
     * already at one.
     * */
    final void flush() {
        bbuf = 0; // reset any bit stuffing state
        bpos = -1;
    }

    /**
     * Resets the underlying byte input to start a new segment. The bit buffer
     * is flushed.
     *
     * @param buf The byte array containing the byte data. If null the
     * current byte array is assumed.
     *
     * @param off The index of the first element in 'buf' to be decoded. If
     * negative the byte just after the previous segment is assumed, only
     * valid if 'buf' is null.
     *
     * @param len The number of bytes in 'buf' to be decoded. Any subsequent
     * bytes are taken to be 0xFF.
     * */
    final void setByteArray(byte buf[], int off, int len) {
        in.setByteArray(buf,off,len);
        bbuf = 0; // reset any bit stuffing state
        bpos = -1;
    }

}
