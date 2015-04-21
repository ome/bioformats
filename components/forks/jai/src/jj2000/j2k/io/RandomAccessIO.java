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
 * $RCSfile: RandomAccessIO.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:16 $
 * $State: Exp $
 *
 * Interface:           RandomAccessIO.java
 *
 * Description:         Interface definition for random access I/O.
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
 */

package jj2000.j2k.io;

import java.io.*;

/**
 * This abstract class defines the interface to perform random access I/O. It
 * implements the <tt>BinaryDataInput</tt> and <tt>BinaryDataOutput</tt>
 * interfaces so that binary data input/output can be performed.
 *
 * <P>This interface supports streams of up to 2 GB in length.
 *
 * @see BinaryDataInput
 * @see BinaryDataOutput
 * */
public interface RandomAccessIO
    extends BinaryDataInput, BinaryDataOutput {

    /**
     * Closes the I/O stream. Prior to closing the stream, any buffered data
     * (at the bit and byte level) should be written.
     *
     * @exception IOException If an I/O error ocurred.
     * */
    public void close() throws IOException;

    /**
     * Returns the current position in the stream, which is the position from
     * where the next byte of data would be read. The first byte in the stream
     * is in position <tt>0</tt>.
     *
     * @return The offset of the current position, in bytes.
     *
     * @exception IOException If an I/O error ocurred.
     * */
    public int getPos() throws IOException;

    /**
     * Returns the current length of the stream, in bytes, taking into account
     * any buffering.
     *
     * @return The length of the stream, in bytes.
     *
     * @exception IOException If an I/O error ocurred.
     * */
    public int length() throws IOException;

    /**
     * Moves the current position for the next read or write operation to
     * offset. The offset is measured from the beginning of the stream. The
     * offset may be set beyond the end of the file, if in write mode. Setting
     * the offset beyond the end of the file does not change the file
     * length. The file length will change only by writing after the offset
     * has been set beyond the end of the file.
     *
     * @param off The offset where to move to.
     *
     * @exception EOFException If in read-only and seeking beyond EOF.
     *
     * @exception IOException If an I/O error ocurred.
     * */
    public void seek(int off) throws IOException;

    /**
     * Reads a byte of data from the stream. Prior to reading, the stream is
     * realigned at the byte level.
     *
     * @return The byte read, as an int.
     *
     * @exception EOFException If the end-of file was reached.
     *
     * @exception IOException If an I/O error ocurred.
     * */
    public int read() throws EOFException, IOException;

    /**
     * Reads up to len bytes of data from this file into an array of
     * bytes. This method reads repeatedly from the stream until all the bytes
     * are read. This method blocks until all the bytes are read, the end of
     * the stream is detected, or an exception is thrown.
     *
     * @param b The buffer into which the data is to be read. It must be long
     * enough.
     *
     * @param off The index in 'b' where to place the first byte read.
     *
     * @param len The number of bytes to read.
     *
     * @exception EOFException If the end-of file was reached before
     * getting all the necessary data.
     *
     * @exception IOException If an I/O error ocurred.
     * */
    public void readFully(byte b[], int off, int len) throws IOException;

    /**
     * Writes a byte to the stream. Prior to writing, the stream is realigned
     * at the byte level.
     *
     * @param b The byte to write. The lower 8 bits of <tt>b</tt> are
     * written.
     *
     * @exception IOException If an I/O error ocurred.
     * */
    public void write(int b) throws IOException;
}
