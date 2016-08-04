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
 * $RCSfile: PktHeaderBitReader.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:01 $
 * $State: Exp $
 *
 * Class:                   PktHeaderBitReader
 *
 * Description:             Bit based reader for packet headers
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


package jj2000.j2k.codestream.reader;

import jj2000.j2k.io.*;
import java.io.*;

/**
 * This class provides a bit based reading facility from a byte based one,
 * applying the bit unstuffing procedure as required by the packet headers.
 */
class PktHeaderBitReader {

    /** The byte based source of data */
    RandomAccessIO in;

    /** The byte array that is the source of data if the PktHeaderBitReader
     * is instantiated with a buffer instead of a RandomAccessIO*/
    ByteArrayInputStream bais;

    /** Flag indicating whether the data should be read from the buffer */
    boolean usebais;

    /** The current bit buffer */
    int bbuf;

    /** The position of the next bit to read in the bit buffer (0 means
     *  empty, 8 full) */
    int bpos;

    /** The next bit buffer, if bit stuffing occurred (i.e. current bit
     *  buffer holds 0xFF) */
    int nextbbuf;

    /**
     * Instantiates a 'PktHeaderBitReader' that gets the byte data from the
     * given source.
     *
     * @param in The source of byte data
     *
     * */
    PktHeaderBitReader(RandomAccessIO in) {
        this.in = in;
        usebais=false;
    }

    /**
     * Instantiates a 'PktHeaderBitReader' that gets the byte data from the
     * given source.
     *
     * @param bais The source of byte data
     *
     * */
    PktHeaderBitReader(ByteArrayInputStream bais) {
        this.bais = bais;
        usebais=true;
    }

    /**
     * Reads a single bit from the input.
     *
     * @return The read bit (0 or 1)
     *
     * @exception IOException If an I/O error occurred
     *
     * @exception EOFException If teh end of file has been reached
     *
     * */
    final int readBit() throws IOException {
        if (bpos == 0) { // Is bit buffer empty?
            if (bbuf != 0xFF) { // No bit stuffing
                if(usebais)
                    bbuf = bais.read();
                else
                    bbuf = in.read();
                bpos = 8;
                if (bbuf == 0xFF) { // If new bit stuffing get next byte
                    if(usebais)
                        nextbbuf = bais.read();
                    else
                        nextbbuf = in.read();
                }
            }
            else { // We had bit stuffing, nextbuf can not be 0xFF
                bbuf = nextbbuf;
                bpos = 7;
            }
        }
        return (bbuf >> --bpos) & 0x01;
    }

    /**
     * Reads a specified number of bits and returns them in a single
     * integer. The bits are returned in the 'n' least significant bits of the
     * returned integer. The maximum number of bits that can be read is 31.
     *
     * @param n The number of bits to read
     *
     * @return The read bits, packed in the 'n' LSBs.
     * @exception IOException If an I/O error occurred
     *
     * @exception EOFException If teh end of file has been reached
     *
     * */
    final int readBits(int n) throws IOException {
        int bits; // The read bits

        // Can we get all bits from the bit buffer?
        if (n <= bpos) {
            return (bbuf >> (bpos-=n)) & ((1<<n)-1);
        }
        else {
            // NOTE: The implementation need not be recursive but the not
            // recursive one exploits a bug in the IBM x86 JIT and caused
            // incorrect decoding (Diego Santa Cruz).
            bits = 0;
            do {
                // Get all the bits we can from the bit buffer
                bits <<= bpos;
                n -= bpos;
                bits |= readBits(bpos);
                // Get an extra bit to load next byte (here bpos is 0)
                if (bbuf != 0xFF) { // No bit stuffing
                    if(usebais)
                        bbuf = bais.read();
                    else
                        bbuf = in.read();

                    bpos = 8;
                    if (bbuf == 0xFF) { // If new bit stuffing get next byte
                        if(usebais)
                            nextbbuf = bais.read();
                        else
                            nextbbuf = in.read();
                    }
                }
                else { // We had bit stuffing, nextbuf can not be 0xFF
                    bbuf = nextbbuf;
                    bpos = 7;
                }
            } while (n > bpos);
            // Get the last bits, if any
            bits <<= n;
            bits |= (bbuf >> (bpos-=n)) & ((1<<n)-1);
            // Return result
            return bits;
        }
    }

    /**
     * Synchronizes this object with the underlying byte based input. It
     * discards and buffered bits and gets ready to read bits from the current
     * position in the underlying byte based input.
     *
     * <P>This method should always be called when some data has been read
     * directly from the underlying byte based input since the last call to
     * 'readBits()' or 'readBit()' before a new call to any of those methods.
     *
     * */
    void sync() {
        bbuf = 0;
        bpos = 0;
    }

    /**
     * Sets the underlying byte based input to the given object. This method
     * discards any currently buffered bits and gets ready to start reading
     * bits from 'in'.
     *
     * <P>This method is equivalent to creating a new 'PktHeaderBitReader'
     * object.
     *
     * @param in The source of byte data
     *
     * */
    void setInput(RandomAccessIO in) {
        this.in = in;
        bbuf = 0;
        bpos = 0;
    }

    /**
     * Sets the underlying byte based input to the given object. This method
     * discards any currently buffered bits and gets ready to start reading
     * bits from 'in'.
     *
     * <P>This method is equivalent to creating a new 'PktHeaderBitReader'
     * object.
     *
     * @param bais The source of byte data
     *
     * */
    void setInput(ByteArrayInputStream bais) {
        this.bais = bais;
        bbuf = 0;
        bpos = 0;
    }
}
