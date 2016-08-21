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
 * $RCSfile: BinaryDataInput.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:15 $
 * $State: Exp $
 *
 * Interface:           BinaryDataInput
 *
 * Description:         Stream like interface for binary
 *                      input from a stream or file.
 *
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
 *
 *
 */

package jj2000.j2k.io;

import java.io.*;

/**
 * This interface defines the input of binary data from streams and/or files.
 *
 * <P>Byte level input (i.e., for byte, int, long, float, etc.) should
 * always be byte aligned. For example, a request to read an
 * <tt>int</tt> should always realign the input at the byte level.
 *
 * <P>The implementation of this interface should clearly define if
 * multi-byte input data is read in little- or big-endian byte
 * ordering (least significant byte first or most significant byte
 * first, respectively).
 *
 * @see EndianType
 * */
public interface BinaryDataInput {

    /**
     * Should read a signed byte (i.e., 8 bit) from the input.
     * reading, the input should be realigned at the byte level.
     *
     * @return The next byte-aligned signed byte (8 bit) from the
     * input.
     *
     * @exception EOFException If the end-of file was reached before
     * getting all the necessary data.
     *
     * @exception IOException If an I/O error ocurred.
     *
     *
     * */
    public byte readByte() throws EOFException, IOException;

    /**
     * Should read an unsigned byte (i.e., 8 bit) from the input. It is
     * returned as an <tt>int</tt> since Java does not have an
     * unsigned byte type. Prior to reading, the input should be
     * realigned at the byte level.
     *
     * @return The next byte-aligned unsigned byte (8 bit) from the
     * input, as an <tt>int</tt>.
     *
     * @exception EOFException If the end-of file was reached before
     * getting all the necessary data.
     *
     * @exception IOException If an I/O error ocurred.
     *
     *
     *
     */
    public int readUnsignedByte() throws EOFException, IOException;

    /**
     * Should read a signed short (i.e., 16 bit) from the input. Prior to
     * reading, the input should be realigned at the byte level.
     *
     * @return The next byte-aligned signed short (16 bit) from the
     * input.
     *
     * @exception EOFException If the end-of file was reached before
     * getting all the necessary data.
     *
     * @exception IOException If an I/O error ocurred.
     *
     *
     *
     */
    public short readShort() throws EOFException, IOException;

    /**
     * Should read an unsigned short (i.e., 16 bit) from the input. It is
     * returned as an <tt>int</tt> since Java does not have an
     * unsigned short type. Prior to reading, the input should be
     * realigned at the byte level.
     *
     * @return The next byte-aligned unsigned short (16 bit) from the
     * input, as an <tt>int</tt>.
     *
     * @exception EOFException If the end-of file was reached before
     * getting all the necessary data.
     *
     * @exception IOException If an I/O error ocurred.
     *
     *
     *
     */
    public int readUnsignedShort() throws EOFException, IOException;

    /**
     * Should read a signed int (i.e., 32 bit) from the input. Prior to
     * reading, the input should be realigned at the byte level.
     *
     * @return The next byte-aligned signed int (32 bit) from the
     * input.
     *
     * @exception EOFException If the end-of file was reached before
     * getting all the necessary data.
     *
     * @exception IOException If an I/O error ocurred.
     *
     *
     *
     */
    public int readInt() throws EOFException, IOException;

    /**
     * Should read an unsigned int (i.e., 32 bit) from the input. It is
     * returned as a <tt>long</tt> since Java does not have an
     * unsigned short type. Prior to reading, the input should be
     * realigned at the byte level.
     *
     * @return The next byte-aligned unsigned int (32 bit) from the
     * input, as a <tt>long</tt>.
     *
     * @exception EOFException If the end-of file was reached before
     * getting all the necessary data.
     *
     * @exception IOException If an I/O error ocurred.
     *
     *
     *
     */
    public long readUnsignedInt() throws EOFException, IOException;

    /**
     * Should read a signed long (i.e., 64 bit) from the input. Prior to
     * reading, the input should be realigned at the byte level.
     *
     * @return The next byte-aligned signed long (64 bit) from the
     * input.
     *
     * @exception EOFException If the end-of file was reached before
     * getting all the necessary data.
     *
     * @exception IOException If an I/O error ocurred.
     *
     *
     *
     */
    public long readLong() throws EOFException, IOException;

    /**
     * Should read an IEEE single precision (i.e., 32 bit)
     * floating-point number from the input. Prior to reading, the
     * input should be realigned at the byte level.
     *
     * @return The next byte-aligned IEEE float (32 bit) from the
     * input.
     *
     * @exception EOFException If the end-of file was reached before
     * getting all the necessary data.
     *
     * @exception IOException If an I/O error ocurred.
     *
     *
     *
     */
    public float readFloat() throws EOFException, IOException;

    /**
     * Should read an IEEE double precision (i.e., 64 bit)
     * floating-point number from the input. Prior to reading, the
     * input should be realigned at the byte level.
     *
     * @return The next byte-aligned IEEE double (64 bit) from the
     * input.
     *
     * @exception EOFException If the end-of file was reached before
     * getting all the necessary data.
     *
     * @exception IOException If an I/O error ocurred.
     *
     *
     *
     */
    public double readDouble() throws EOFException, IOException;

    /**
     * Returns the endianess (i.e., byte ordering) of the implementing
     * class. Note that an implementing class may implement only one
     * type of endianness or both, which would be decided at creatiuon
     * time.
     *
     * @return Either <tt>EndianType.BIG_ENDIAN</tt> or
     * <tt>EndianType.LITTLE_ENDIAN</tt>
     *
     * @see EndianType
     *
     *
     *
     */
    public int getByteOrdering();

    /**
     * Skips <tt>n</tt> bytes from the input. Prior to skipping, the
     * input should be realigned at the byte level.
     *
     * @param n The number of bytes to skip
     *
     * @exception EOFException If the end-of file was reached before
     * all the bytes could be skipped.
     *
     * @exception IOException If an I/O error ocurred.
     *
     *
     *
     */
    public int skipBytes(int n)throws EOFException, IOException;

}
