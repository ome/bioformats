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
 * $RCSfile: BufferedRandomAccessFile.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:16 $
 * $State: Exp $
 *
 * Interface:           RandomAccessIO.java
 *
 * Description:         Abstract class for buffered random access I/O.
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
 * This class defines a Buffered Random Access File.  It implements the
 * <tt>BinaryDataInput</tt> and <tt>BinaryDataOutput</tt> interfaces so that
 * binary data input/output can be performed. This class is abstract since no
 * assumption is done about the byte ordering type (little Endian, big
 * Endian). So subclasses will have to implement methods like
 * <tt>readShort()</tt>, <tt>writeShort()</tt>, <tt>readFloat()</tt>, ...
 *
 * <P><tt>BufferedRandomAccessFile</tt> (BRAF for short) is a
 * <tt>RandomAccessFile</tt> containing an extra buffer. When the BRAF is
 * accessed, it checks if the requested part of the file is in the buffer or
 * not. If that is the case, the read/write is done on the buffer. If not, the
 * file is uppdated to reflect the current status of the buffer and the file
 * is then accessed for a new buffer containing the requested byte/bit.
 *
 * @see RandomAccessIO
 * @see BinaryDataOutput
 * @see BinaryDataInput
 * @see BEBufferedRandomAccessFile
 * */
public abstract class BufferedRandomAccessFile
    implements RandomAccessIO, EndianType {

    /**
     * The name of the current file
     * */
    private String fileName;

    /**
     * Whether the opened file is read only or not (defined by the constructor
     * arguments)
     * */
    private boolean isReadOnly = true;

    /**
     * The RandomAccessFile associated with the buffer
     * */
    private RandomAccessFile theFile;

    /**
     * Buffer of bytes containing the part of the file that is currently being
     * accessed
     * */
    protected byte[] byteBuffer;

    /**
     * Boolean keeping track of whether the byte buffer has been changed since
     * it was read.
     * */
    protected boolean byteBufferChanged;

    /**
     * The current offset of the buffer (which will differ from the offset of
     * the file)
     * */
    protected int offset;

    /**
     * The current position in the byte-buffer
     * */
    protected int pos;

    /**
     * The maximum number of bytes that can be read from the buffer
     * */
    protected int maxByte;

    /**
     * Whether the end of the file is in the current buffer or not
     * */
    protected boolean isEOFInBuffer;

    /* The endianess of the class */
    protected int byteOrdering;

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
    protected BufferedRandomAccessFile(File file,
				       String mode,
				       int bufferSize) throws IOException{

	fileName = file.getName();
	if(mode.equals("rw") || mode.equals("rw+")){ // mode read / write
	    isReadOnly = false;
	    if(mode.equals("rw")){ // mode read / (over)write
		if(file.exists()) // Output file already exists
		    file.delete();
	    }
	    mode = "rw";
	}
	theFile=new RandomAccessFile(file,mode);
	byteBuffer=new byte[bufferSize];
	readNewBuffer(0);
    }

    /**
     * Constructor. Uses the default value for the byte-buffer
     * size (512 bytes).
     *
     * @param file The file associated with the buffer
     *
     * @param mode "r" for read, "rw" or "rw+" for read and write mode
     *             ("rw+" opens the file for update whereas "rw" removes
     *             it before. So the 2 modes are different only if the
     *             file already exists).
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    protected BufferedRandomAccessFile(File file,
				       String mode ) throws IOException{

	this(file, mode, 512);
    }

    /**
     * Constructor. Always needs a size for the buffer.
     *
     * @param name The name of the file associated with the buffer
     *
     * @param mode "r" for read, "rw" or "rw+" for read and write mode
     *             ("rw+" opens the file for update whereas "rw" removes
     *             it before. So the 2 modes are different only if the
     *             file already exists).
     *
     * @param bufferSize The number of bytes to buffer
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    protected BufferedRandomAccessFile(String name,
				       String mode,
				       int bufferSize) throws IOException{
	this(new File(name), mode, bufferSize);
    }

    /**
     * Constructor. Uses the default value for the byte-buffer
     * size (512 bytes).
     *
     * @param name The name of the file associated with the buffer
     *
     * @param mode "r" for read, "rw" or "rw+" for read and write mode
     *             ("rw+" opens the file for update whereas "rw" removes
     *             it before. So the 2 modes are different only if the
     *             file already exists).
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    protected BufferedRandomAccessFile(String name,
				       String mode ) throws IOException{

	this(name, mode, 512);
    }

    /**
     * Reads a new buffer from the file. If there has been any
     * changes made since the buffer was read, the buffer is
     * first written to the file.
     *
     * @param off The offset where to move to.
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    protected final void readNewBuffer(int off) throws IOException{

	/* If the buffer have changed. We need to write it to
	 * the file before reading a new buffer.
	 */
	if(byteBufferChanged){
	    flush();
	}
        // Don't allow to seek beyond end of file if reading only
        if (isReadOnly && off >= theFile.length()) {
            throw new EOFException();
        }
        // Set new offset
	offset = off;

        theFile.seek(offset);

	maxByte = theFile.read(byteBuffer,0,byteBuffer.length);
	pos=0;

	if(maxByte<byteBuffer.length){ // Not enough data in input file.
	    isEOFInBuffer = true;
	    if(maxByte==-1){
		maxByte++;
	    }
	}else{
	    isEOFInBuffer = false;
	}
    }

    /**
     * Closes the buffered random access file
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    public void close() throws IOException{
	/* If the buffer has been changed, it need to be saved before
	 * closing
	 */
	flush();
	byteBuffer = null; // Release the byte-buffer reference
	theFile.close();
    }

    /**
     * Returns the current offset in the file
     * */
    public int getPos(){
	return (offset+pos);
    }

    /**
     * Returns the current length of the stream, in bytes, taking into
     * account any buffering.
     *
     * @return The length of the stream, in bytes.
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    public int length() throws IOException{
	int len;

	len = (int)theFile.length();

	// If the position in the buffer is not past the end of the file,
	// the length of theFile is the length of the stream
	if( (offset+maxByte)<=len ){
	    return(len);
	}
	else{ // If not, the file is extended due to the buffering
	    return (offset+maxByte);
	}
    }

    /**
     * Moves the current position to the given offset at which the
     * next read or write occurs. The offset is measured from the
     * beginning of the stream.
     *
     * @param off The offset where to move to.
     *
     * @exception EOFException If in read-only and seeking beyond EOF.
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    public void seek(int off) throws IOException{
	/* If the new offset is within the buffer, only the pos value needs
	 * to be modified. Else, the buffer must be moved. */
	if( (off>=offset)&&(off<(offset+byteBuffer.length)) ){
            if (isReadOnly && isEOFInBuffer && off > offset+maxByte) {
                // We are seeking beyond EOF in read-only mode!
                throw new EOFException();
            }
	    pos = off-offset;
	}
	else{
	    readNewBuffer(off);
	}
    }

    /**
     * Reads an unsigned byte of data from the stream. Prior to reading, the
     * stream is realigned at the byte level.
     *
     * @return The byte read.
     *
     * @exception java.io.IOException If an I/O error ocurred.
     *
     * @exception java.io.EOFException If the end of file was reached
     * */
    public final int read() throws IOException, EOFException{
	if(pos<maxByte){ // The byte can be read from the buffer
	    // In Java, the bytes are always signed.
            return (byteBuffer[pos++]&0xFF);
	}
	else if(isEOFInBuffer){ // EOF is reached
            pos = maxByte+1; // Set position to EOF
	    throw new EOFException();
	}
	else { // End of the buffer is reached
	    readNewBuffer(offset+pos);
	    return read();
	}
    }

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
    public final void readFully(byte b[], int off, int len)
        throws IOException {
        int clen; // current length to read
        while (len > 0) {
            // There still is some data to read
            if (pos<maxByte) { // We can read some data from buffer
                clen = maxByte-pos;
                if (clen > len) clen = len;
                System.arraycopy(byteBuffer,pos,b,off,clen);
                pos += clen;
                off += clen;
                len -= clen;
            }
            else if (isEOFInBuffer) {
                pos = maxByte+1; // Set position to EOF
                throw new EOFException();
            }
            else { // Buffer empty => get more data
                readNewBuffer(offset+pos);
            }
        }
    }

    /**
     * Writes a byte to the stream. Prior to writing, the stream is
     * realigned at the byte level.
     *
     * @param b The byte to write. The lower 8 bits of <tt>b</tt> are
     * written.
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    public final void write(int b) throws IOException{
	// As long as pos is less than the length of the buffer we can write
	// to the buffer. If the position is after the buffer a new buffer is
	// needed
	if(pos<byteBuffer.length){
	    if(isReadOnly)
		throw new IOException("File is read only");
	    byteBuffer[pos]=(byte)b;
	    if(pos>=maxByte){
		maxByte=pos+1;
	    }
	    pos++;
	    byteBufferChanged =true;
	}
	else{
	    readNewBuffer(offset+pos);
	    write(b);
	}
    }

    /**
     * Writes a byte to the stream. Prior to writing, the stream is
     * realigned at the byte level.
     *
     * @param b The byte to write.
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    public final void write(byte b) throws IOException{
	// As long as pos is less than the length of the buffer we can write
	// to the buffer. If the position is after the buffer a new buffer is
	// needed
	if(pos<byteBuffer.length){
	    if(isReadOnly)
		throw new IOException("File is read only");
	    byteBuffer[pos]=b;
	    if(pos>=maxByte){
		maxByte=pos+1;
	    }
	    pos++;
	    byteBufferChanged =true;
	}
	else{
	    readNewBuffer(offset+pos);
	    write(b);
	}
    }

    /**
     * Writes aan array of bytes to the stream. Prior to writing, the stream is
     * realigned at the byte level.
     *
     * @param b The array of bytes to write.
     *
     * @param offset The first byte in b to write
     *
     * @param length The number of bytes from b to write
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    public final void write(byte[] b, int offset, int length)
        throws IOException{
        int i,stop;
        stop = offset+length;
        if(stop > b.length)
            throw new ArrayIndexOutOfBoundsException(b.length);
        for(i=offset ; i<stop ; i++){
            write(b[i]);
        }
    }

    /**
     * Writes the byte value of <tt>v</tt> (i.e., 8 least
     * significant bits) to the output. Prior to writing, the output
     * should be realigned at the byte level.
     *
     * <P>Signed or unsigned data can be written. To write a signed
     * value just pass the <tt>byte</tt> value as an argument. To
     * write unsigned data pass the <tt>int</tt> value as an argument
     * (it will be automatically casted, and only the 8 least
     * significant bits will be written).
     *
     * @param v The value to write to the output
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    public final void writeByte(int v) throws IOException{
	write(v);
    }

    /**
     * Any data that has been buffered must be written (including
     * buffering at the bit level), and the stream should be realigned
     * at the byte level.
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    public final void flush() throws IOException{
        if(byteBufferChanged){
	    theFile.seek(offset);
	    theFile.write(byteBuffer,0,maxByte);
	    byteBufferChanged = false;
        }
    }

    /**
     * Reads a signed byte (i.e., 8 bit) from the input. Prior to
     * reading, the input should be realigned at the byte level.
     *
     * @return The next byte-aligned signed byte (8 bit) from the
     * input.
     *
     * @exception java.io.EOFException If the end-of file was reached before
     * getting all the necessary data.
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    public final byte readByte() throws EOFException, IOException {
	if(pos<maxByte){ // The byte can be read from the buffer
	    // In Java, the bytes are always signed.
            return byteBuffer[pos++];
	}
	else if(isEOFInBuffer){ // EOF is reached
            pos = maxByte+1; // Set position to EOF
	    throw new EOFException();
	}
	else { // End of the buffer is reached
	    readNewBuffer(offset+pos);
	    return readByte();
	}
    }

    /**
     * Reads an unsigned byte (i.e., 8 bit) from the input. It is
     * returned as an <tt>int</tt> since Java does not have an
     * unsigned byte type. Prior to reading, the input should be
     * realigned at the byte level.
     *
     * @return The next byte-aligned unsigned byte (8 bit) from the
     * input, as an <tt>int</tt>.
     *
     * @exception java.io.EOFException If the end-of file was reached before
     * getting all the necessary data.
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    public final int readUnsignedByte() throws EOFException, IOException{
        return read();
    }

    /**
     * Returns the endianess (i.e., byte ordering) of the implementing
     * class. Note that an implementing class may implement only one
     * type of endianness or both, which would be decided at creation
     * time.
     *
     * @return Either <tt>EndianType.BIG_ENDIAN</tt> or
     * <tt>EndianType.LITTLE_ENDIAN</tt>
     *
     * @see EndianType
     * */
    public int getByteOrdering(){
	return byteOrdering;
    }

    /**
     * Skips <tt>n</tt> bytes from the input. Prior to skipping, the
     * input should be realigned at the byte level.
     *
     * @param n The number of bytes to skip
     *
     * @exception java.io.EOFException If the end-of file was reached before
     * all the bytes could be skipped.
     *
     * @exception java.io.IOException If an I/O error ocurred.
     * */
    public int skipBytes(int n)throws EOFException, IOException{
	if(n<0)
	    throw new IllegalArgumentException("Can not skip negative number "+
					       "of bytes");
	if(n <= (maxByte-pos)){
	    pos += n;
	    return n;
	}
	else{
	    seek(offset+pos+n);
	    return n;
	}
    }

    /**
     * Returns a string of information about the file
     * */
    public String toString(){
	return "BufferedRandomAccessFile: "+fileName+" ("+
	    ((isReadOnly)?"read only":"read/write")+
	    ")";
    }
}
