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
 * $RCSfile: BEBufferedRandomAccessFile.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:15 $
 * $State: Exp $
 *
 * Interface:           RandomAccessIO.java
 *
 * Description:         Class for random access I/O (big-endian ordering).
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
 * This class defines a Buffered Random Access File, where all I/O is
 * considered to be big-endian, and extends the
 * <tt>BufferedRandomAccessFile</tt> class.
 *
 * @see RandomAccessIO
 * @see BinaryDataOutput
 * @see BinaryDataInput
 * @see BufferedRandomAccessFile */
public class BEBufferedRandomAccessFile extends BufferedRandomAccessFile
    implements RandomAccessIO, EndianType{

    /**
     * Constructor. Always needs a size for the buffer.
     *
     * @param file The file associated with the buffer
     *
     * @param mode "r" for read, "rw" or "rw+" for read and write mode ("rw+"
     *             opens the file for update whereas "rw" removes it
     *             before. So the 2 modes are different only if the file
     *             already exists).
     *
     * @param bufferSize The number of bytes to buffer
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    public BEBufferedRandomAccessFile(File file,
				      String mode,
				      int bufferSize) throws IOException{
	super(file, mode, bufferSize);
	byteOrdering = BIG_ENDIAN;
    }

    /**
     * Constructor. Uses the default value for the byte-buffer size (512
     * bytes).
     *
     * @param file The file associated with the buffer
     *
     * @param mode "r" for read, "rw" or "rw+" for read and write mode ("rw+"
     *             opens the file for update whereas "rw" removes it
     *             before. So the 2 modes are different only if the file
     *             already exists).
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    public BEBufferedRandomAccessFile(File file,
				      String mode ) throws IOException{
	super(file, mode);
	byteOrdering = BIG_ENDIAN;
    }

    /**
     * Constructor. Always needs a size for the buffer.
     *
     * @param name The name of the file associated with the buffer
     *
     * @param mode "r" for read, "rw" or "rw+" for read and write mode ("rw+"
     *             opens the file for update whereas "rw" removes it
     *             before. So the 2 modes are different only if the file
     *             already exists).
     *
     * @param bufferSize The number of bytes to buffer
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    public BEBufferedRandomAccessFile(String name,
				      String mode,
				      int bufferSize) throws IOException{
	super(name, mode, bufferSize);
	byteOrdering = BIG_ENDIAN;
    }

    /**
     * Constructor. Uses the default value for the byte-buffer size (512
     * bytes).
     *
     * @param name The name of the file associated with the buffer
     *
     * @param mode "r" for read, "rw" or "rw+" for read and write mode ("rw+"
     *             opens the file for update whereas "rw" removes it
     *             before. So the 2 modes are different only if the file
     *             already exists).
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    public BEBufferedRandomAccessFile(String name,
				      String mode ) throws IOException{
	super(name, mode);
	byteOrdering = BIG_ENDIAN;
    }

    /**
     * Writes the short value of <tt>v</tt> (i.e., 16 least significant bits)
     * to the output. Prior to writing, the output should be realigned at the
     * byte level.
     *
     * <P>Signed or unsigned data can be written. To write a signed value just
     * pass the <tt>short</tt> value as an argument. To write unsigned data
     * pass the <tt>int</tt> value as an argument (it will be automatically
     * casted, and only the 16 least significant bits will be written).
     *
     * @param v The value to write to the output
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    public final void writeShort(int v) throws IOException{
	write(v>>>8);
	write(v);
    }

    /**
     * Writes the int value of <tt>v</tt> (i.e., the 32 bits) to the
     * output. Prior to writing, the output should be realigned at the byte
     * level.
     *
     * @param v The value to write to the output
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    public final void writeInt(int v)throws IOException{
 	write(v>>>24);
 	write(v>>>16);
	write(v>>>8);
	write(v);
    }

    /**
     * Writes the long value of <tt>v</tt> (i.e., the 64 bits) to the
     * output. Prior to writing, the output should be realigned at the byte
     * level.
     *
     * @param v The value to write to the output
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    public final void writeLong(long v)throws IOException{
	write((int)(v>>>56));
	write((int)(v>>>48));
 	write((int)(v>>>40));
	write((int)(v>>>32));
	write((int)(v>>>24));
	write((int)(v>>>16));
 	write((int)(v>>>8));
	write((int)v);
    }

    /**
     * Writes the IEEE float value <tt>v</tt> (i.e., 32 bits) to the
     * output. Prior to writing, the output should be realigned at the byte
     * level.
     *
     * @param v The value to write to the output
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    public final void writeFloat(float v) throws IOException{
	int intV = Float.floatToIntBits(v);

	write(intV>>>24);
	write(intV>>>16);
	write(intV>>>8);
	write(intV);
    }

    /**
     * Writes the IEEE double value <tt>v</tt> (i.e., 64 bits) to the
     * output. Prior to writing, the output should be realigned at the byte
     * level.
     *
     * @param v The value to write to the output
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    public final void writeDouble(double v)throws IOException{
	long longV = Double.doubleToLongBits(v);

	write((int)(longV>>>56));
	write((int)(longV>>>48));
	write((int)(longV>>>40));
	write((int)(longV>>>32));
	write((int)(longV>>>24));
	write((int)(longV>>>16));
	write((int)(longV>>>8));
	write((int)(longV));
    }

    /**
     * Reads a signed short (i.e., 16 bit) from the input. Prior to reading,
     * the input should be realigned at the byte level.
     *
     * @return The next byte-aligned signed short (16 bit) from the
     * input.
     *
     * @exception java.io.EOFException If the end-of file was reached before
     * getting all the necessary data.
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    public final short readShort() throws IOException, EOFException{
	return (short)(
		       (read()<<8)|
		       (read())
		       );
    }

    /**
     * Reads an unsigned short (i.e., 16 bit) from the input. It is returned
     * as an <tt>int</tt> since Java does not have an unsigned short
     * type. Prior to reading, the input should be realigned at the byte
     * level.
     *
     * @return The next byte-aligned unsigned short (16 bit) from the
     * input, as an <tt>int</tt>.
     *
     * @exception java.io.EOFException If the end-of file was reached before
     * getting all the necessary data.
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    public final int readUnsignedShort() throws IOException, EOFException{
	return (
		(read()<<8)|
		read()
		);
    }

    /**
     * Reads a signed int (i.e., 32 bit) from the input. Prior to reading, the
     * input should be realigned at the byte level.
     *
     * @return The next byte-aligned signed int (32 bit) from the
     * input.
     *
     * @exception java.io.EOFException If the end-of file was reached before
     * getting all the necessary data.
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    public final int readInt() throws IOException, EOFException{
	return (
		(read()<<24)|
		(read()<<16)|
		(read()<<8)|
		read()
		);
    }

    /**
     * Reads an unsigned int (i.e., 32 bit) from the input. It is returned as
     * a <tt>long</tt> since Java does not have an unsigned short type. Prior
     * to reading, the input should be realigned at the byte level.
     *
     * @return The next byte-aligned unsigned int (32 bit) from the
     * input, as a <tt>long</tt>.
     *
     * @exception java.io.EOFException If the end-of file was reached before
     * getting all the necessary data.
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    public final long readUnsignedInt() throws IOException, EOFException{
	return (long)(
		      (read()<<24)|
		      (read()<<16)|
		      (read()<<8)|
		      read()
		      );
    }

    /**
     * Reads a signed long (i.e., 64 bit) from the input. Prior to reading,
     * the input should be realigned at the byte level.
     *
     * @return The next byte-aligned signed long (64 bit) from the
     * input.
     *
     * @exception java.io.EOFException If the end-of file was reached before
     * getting all the necessary data.
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    public final long readLong() throws IOException, EOFException{
	return (
		((long)read()<<56)|
		((long)read()<<48)|
		((long)read()<<40)|
		((long)read()<<32)|
		((long)read()<<24)|
		((long)read()<<16)|
		((long)read()<<8)|
		((long)read())
		);
    }

    /**
     * Reads an IEEE single precision (i.e., 32 bit) floating-point number
     * from the input. Prior to reading, the input should be realigned at the
     * byte level.
     *
     * @return The next byte-aligned IEEE float (32 bit) from the
     * input.
     *
     * @exception java.io.EOFException If the end-of file was reached before
     * getting all the necessary data.
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    public final float readFloat() throws EOFException, IOException{
	return Float.intBitsToFloat(
				    (read()<<24)|
				    (read()<<16)|
				    (read()<<8)|
				    (read())
				    );
    }

    /**
     * Reads an IEEE double precision (i.e., 64 bit) floating-point number
     * from the input. Prior to reading, the input should be realigned at the
     * byte level.
     *
     * @return The next byte-aligned IEEE double (64 bit) from the
     * input.
     *
     * @exception java.io.EOFException If the end-of file was reached before
     * getting all the necessary data.
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    public final double readDouble() throws IOException, EOFException{
	return Double.longBitsToDouble(
				       ((long)read()<<56)|
				       ((long)read()<<48)|
				       ((long)read()<<40)|
				       ((long)read()<<32)|
				       ((long)read()<<24)|
				       ((long)read()<<16)|
				       ((long)read()<<8)|
				       ((long)read())
				       );
    }

    /**
     * Returns a string of information about the file and the endianess
     */
    public String toString(){
	return super.toString()+"\nBig-Endian ordering";
    }
}
