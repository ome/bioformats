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
 * $RCSfile: ImgReaderPGM.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/02/11 05:02:14 $
 * $State: Exp $
 *
 * Class:                   ImageWriterRawPGM
 *
 * Description:             Image writer for unsigned 8 bit data in
 *                          PGM files.
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
package jj2000.j2k.image.input;

import jj2000.j2k.image.*;
import jj2000.j2k.*;
import java.io.*;

/**
 * This class implements the ImgData interface for reading 8 bit unsigned data
 * from a binary PGM file.
 *
 * <p>After being read the coefficients are level shifted by subtracting
 * 2^(nominal bit range-1)</p>
 *
 * <p>The TransferType (see ImgData) of this class is TYPE_INT.</p>
 *
 * <P>NOTE: This class is not thread safe, for reasons of internal buffering.
 *
 * @see jj2000.j2k.image.ImgData
 * */
public class ImgReaderPGM extends ImgReader {

    /** DC offset value used when reading image */
    public static int DC_OFFSET = 128;

    /** Where to read the data from */
    private RandomAccessFile in;
    
    /** The offset of the raw pixel data in the PGM file */
    private int offset;

    /** The number of bits that determine the nominal dynamic range */
    private int rb;
    
    /** The line buffer. */
    // This makes the class not thrad safe
    // (but it is not the only one making it so)
    private byte buf[];

    /** Temporary DataBlkInt object (needed when encoder uses floating-point
        filters). This avoid allocating new DataBlk at each time */
    private DataBlkInt intBlk;

    /**
     * Creates a new PGM file reader from the specified file.
     *
     * @param file The input file.
     *
     * @exception IOException If an error occurs while opening the file.
     * */
    public ImgReaderPGM(File file) throws IOException {
        this(new RandomAccessFile(file,"r"));
    }

    /**
     * Creates a new PGM file reader from the specified file name.
     *
     * @param fname The input file name.
     *
     * @exception IOException If an error occurs while opening the file.
     * */
    public ImgReaderPGM(String fname) throws IOException {
        this(new RandomAccessFile(fname,"r"));
    }

    /**
     * Creates a new PGM file reader from the specified RandomAccessFile
     * object. The file header is read to acquire the image size.
     *
     * @param in From where to read the data 
     *
     * @exception EOFException if an EOF is read
     * @exception IOException if an error occurs when opening the file
     * */
    public ImgReaderPGM(RandomAccessFile in) throws EOFException, IOException {
        this.in = in;

        confirmFileType();
        skipCommentAndWhiteSpace();
        this.w = readHeaderInt();
        skipCommentAndWhiteSpace();
        this.h = readHeaderInt();
        skipCommentAndWhiteSpace();
        /*Read the highest pixel value from header (not used)*/
        readHeaderInt(); 
        this.nc=1;
        this.rb=8;
    }
                

    /**
     * Closes the underlying RandomAccessFile from where the image data is
     * being read. No operations are possible after a call to this method.
     *
     * @exception IOException If an I/O error occurs.
     * */
    public void close() throws IOException {
        in.close();
        in = null;
    }

    /**
     * Returns the number of bits corresponding to the nominal range of the
     * data in the specified component. This is the value rb (range bits) that
     * was specified in the constructor, which normally is 8 for non bilevel
     * data, and 1 for bilevel data.
     *
     * <P>If this number is <i>b</b> then the nominal range is between
     * -2^(b-1) and 2^(b-1)-1, since unsigned data is level shifted to have a
     * nominal average of 0.
     *
     * @param c The index of the component.
     *
     * @return The number of bits corresponding to the nominal range of the
     * data. Fro floating-point data this value is not applicable and the
     * return value is undefined.
     * */
    public int getNomRangeBits(int c) {
        // Check component index
        if (c != 0)
            throw new IllegalArgumentException();

        return rb;
    }

    
    /**
     * Returns the position of the fixed point in the specified component
     * (i.e. the number of fractional bits), which is always 0 for this
     * ImgReader.
     *
     * @param c The index of the component.
     *
     * @return The position of the fixed-point (i.e. the number of fractional
     * bits). Always 0 for this ImgReader.
     * */
    public int getFixedPoint(int c) {
        // Check component index
        if (c != 0)
            throw new IllegalArgumentException();
        return 0;
    }
  
    
    /**
     * Returns, in the blk argument, the block of image data containing the
     * specifed rectangular area, in the specified component. The data is
     * returned, as a reference to the internal data, if any, instead of as a
     * copy, therefore the returned data should not be modified.
     *
     * <P> After being read the coefficients are level shifted by subtracting
     * 2^(nominal bit range - 1)
     *
     * <P>The rectangular area to return is specified by the 'ulx', 'uly', 'w'
     * and 'h' members of the 'blk' argument, relative to the current
     * tile. These members are not modified by this method. The 'offset' and
     * 'scanw' of the returned data can be arbitrary. See the 'DataBlk' class.
     *
     * <P>If the data array in <tt>blk</tt> is <tt>null</tt>, then a new one
     * is created if necessary. The implementation of this interface may
     * choose to return the same array or a new one, depending on what is more
     * efficient. Therefore, the data array in <tt>blk</tt> prior to the
     * method call should not be considered to contain the returned data, a
     * new array may have been created. Instead, get the array from
     * <tt>blk</tt> after the method has returned.
     *
     * <P>The returned data always has its 'progressive' attribute unset
     * (i.e. false).
     *
     * <P>When an I/O exception is encountered the JJ2KExceptionHandler is
     * used. The exception is passed to its handleException method. The action
     * that is taken depends on the action that has been registered in
     * JJ2KExceptionHandler. See JJ2KExceptionHandler for details.
     *
     * @param blk Its coordinates and dimensions specify the area to
     * return. Some fields in this object are modified to return the data.
     *
     * @param c The index of the component from which to get the data. Only 0
     * is valid.
     *
     * @return The requested DataBlk
     *
     * @see #getCompData
     *
     * @see JJ2KExceptionHandler
     * */
    public final DataBlk getInternCompData(DataBlk blk, int c) {
        int k,j,i,mi;
        int barr[];

        // Check component index
        if (c != 0)
            throw new IllegalArgumentException();

	// Check type of block provided as an argument
	if(blk.getDataType()!=DataBlk.TYPE_INT){
	    if(intBlk==null)
		intBlk = new DataBlkInt(blk.ulx,blk.uly,blk.w,blk.h);
	    else{
		intBlk.ulx = blk.ulx;
		intBlk.uly = blk.uly;
		intBlk.w = blk.w;
		intBlk.h = blk.h;
	    }
	    blk = intBlk;
	}
	
	// Get data array
	barr = (int[]) blk.getData();
	if (barr == null || barr.length < blk.w*blk.h) {
	    barr = new int[blk.w*blk.h];
	    blk.setData(barr); 
	}
       
        // Check line buffer
        if (buf == null || buf.length < blk.w) {
            buf = new byte[blk.w];
        }

        try {
            // Read line by line
            mi = blk.uly + blk.h;
            for (i = blk.uly; i < mi; i++) {
                // Reposition in input
                in.seek(offset+i*w+blk.ulx);
                in.read(buf,0,blk.w);
                for (k = (i-blk.uly)*blk.w+blk.w-1, j = blk.w-1;
                     j >= 0; j--, k--) {
                    barr[k] = (((int)buf[j])&0xFF)-DC_OFFSET;
                }
            }
        }
        catch (IOException e) {
            JJ2KExceptionHandler.handleException(e);
        }

        // Turn off the progressive attribute
        blk.progressive = false;
        // Set buffer attributes
        blk.offset = 0;
        blk.scanw = blk.w;
	return blk;
    }

    /**
     * Returns, in the blk argument, a block of image data containing the
     * specifed rectangular area, in the specified component. The data is
     * returned, as a copy of the internal data, therefore the returned data
     * can be modified "in place".
     *
     * <P> After being read the coefficients are level shifted by subtracting
     * 2^(nominal bit range - 1)
     *
     * <P>The rectangular area to return is specified by the 'ulx', 'uly', 'w'
     * and 'h' members of the 'blk' argument, relative to the current
     * tile. These members are not modified by this method. The 'offset' of
     * the returned data is 0, and the 'scanw' is the same as the block's
     * width. See the 'DataBlk' class.
     *
     * <P>If the data array in 'blk' is 'null', then a new one is created. If
     * the data array is not 'null' then it is reused, and it must be large
     * enough to contain the block's data. Otherwise an 'ArrayStoreException'
     * or an 'IndexOutOfBoundsException' is thrown by the Java system.
     *
     * <P>The returned data has its 'progressive' attribute unset
     * (i.e. false).
     *
     * <P>This method just calls 'getInternCompData(blk, n)'.
     *
     * <P>When an I/O exception is encountered the JJ2KExceptionHandler is
     * used. The exception is passed to its handleException method. The action
     * that is taken depends on the action that has been registered in
     * JJ2KExceptionHandler. See JJ2KExceptionHandler for details.
     *
     * @param blk Its coordinates and dimensions specify the area to
     * return. If it contains a non-null data array, then it must have the
     * correct dimensions. If it contains a null data array a new one is
     * created. The fields in this object are modified to return the data.
     *
     * @param c The index of the component from which to get the data. Only 0
     * is valid.
     *
     * @return The requested DataBlk
     *
     * @see #getInternCompData
     *
     * @see JJ2KExceptionHandler
     * */
    public DataBlk getCompData(DataBlk blk, int c) {
        return getInternCompData(blk,c);
    }

    /**
     * Returns a byte read from the RandomAccessIO. The number of read byted
     * are counted to keep track of the offset of the pixel data in the PGM
     * file
     *
     * @return One byte read from the header of the PGM file.
     *
     * @exception IOException If an I/O error occurs.
     *
     * @exception EOFException If an EOF is read 
     * */
     private byte countedByteRead() throws IOException, EOFException{
        offset++;
        return in.readByte();
    }
    
    /**
     * Checks that the RandomAccessIO begins with 'P5'
     *
     * @exception IOException If an I/O error occurs.
     * @exception EOFException If an EOF is read
     * */        
    private void confirmFileType() throws IOException, EOFException{
        byte[] type={80,53}; // 'P5'
        int i;
        byte b;

        for(i=0;i<2;i++){
            b = countedByteRead();
            if(b!=type[i]){
                if( i==1 && b==50 )  { //i.e 'P2'
                    throw new 
                        IllegalArgumentException("JJ2000 does not support"+
                                                 " ascii-PGM files. Use "+
                                                 " raw-PGM file instead. ");
                } else {
                    throw new IllegalArgumentException("Not a raw-PGM file");
                }
            }
        }
    }
    
    /**
     * Skips any line in the header starting with '#' and any space, tab, line
     * feed or carriage return.
     *
     * @exception IOException If an I/O error occurs.  
     * @exception EOFException if an EOF is read
     * */
    private void skipCommentAndWhiteSpace() throws IOException, EOFException {

        boolean done=false;
        byte b;
        
        while(!done){
            b=countedByteRead();
            if(b==35){ // Comment start
                while(b!=10 && b!=13){ // Comment ends in end of line
                    b=countedByteRead();
                }
            }else if(!(b==9||b==10||b==13||b==32)){ // If not whitespace
                done=true;
            }
        }
        // Put last valid byte in
        offset--;
        in.seek(offset);
    }
    
   
    /**
     * Returns an int read from the header of the PGM file.
     * 
     * @return One int read from the header of the PGM file.
     *
     * @exception IOException If an I/O error occurs.
     * @exception EOFException If an EOF is read 
     * */
    private int readHeaderInt() throws IOException, EOFException{
        int res=0;
        byte b=0;
        
        b=countedByteRead();   
        while(b!=32&&b!=10&&b!=9&&b!=13){ // While not whitespace
            res=res*10+b-48; // Covert ASCII to numerical value
            b=countedByteRead();    
        }
        return res;
    }
    
    /**
     * Returns true if the data read was originally signed in the specified
     * component, false if not. This method returns always false since PGM
     * data is always unsigned.
     *
     * @param c The index of the component, from 0 to N-1.
     *
     * @return always false, since PGM data is always unsigned.
     * */
    public boolean isOrigSigned(int c) {
        // Check component index
        if (c != 0)
            throw new IllegalArgumentException();
        return false;
    }

    /**
     * Returns a string of information about the object, more than 1 line
     * long. The information string includes information from the underlying
     * RandomAccessIO (its toString() method is called in turn).
     *
     * @return A string of information about the object.  
     * */
    public String toString() {
        return "ImgReaderPGM: WxH = " + w + "x" + h + ", Component = 0" +
            "\nUnderlying RandomAccessIO:\n" + in.toString();
    }
}
