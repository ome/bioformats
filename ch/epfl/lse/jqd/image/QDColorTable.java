//                              -*- Mode: Java -*- 
// QDColorTable.java --- 
// Author          : Matthias Wiesmann
// Created On      : Tue Nov 21 15:21:52 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Wed Dec  6 12:03:13 2000
// Update Count    : 3
// Status          : Unknown, Use with caution!
// 

package ch.epfl.lse.jqd.image;

import ch.epfl.lse.jqd.basics.*;
import java.awt.image.*;

import java.io.IOException;
import ch.epfl.lse.jqd.io.QDInputStream;

/** This class describes a QuickDraw Color Table
 * @author Matthias Wiesmann
 * @version 2.0 revised
 */

public class QDColorTable
{
    protected byte[] r ;
    protected byte[] g ;
    protected byte[] b ;
    protected int ctSize ;

    /** reads the color table
     *  @param theStream the stream to load from
     *  @exception IOException I/O problem
     *  @exception QDException corrupt or unknown color table
     */

    public int read(QDInputStream theStream) 
	throws IOException, QDException {
	final int ctSeed=theStream.readInt();
	final int ctFlags=theStream.readUnsignedShort();
	ctSize=theStream.readShort()+1;
	if (ctSize<0) throw new QDNegativeColorTable(ctSize) ;
	r = new byte[ctSize] ;
	g = new byte[ctSize] ;
	b = new byte[ctSize] ;
	for (int i=0;i<ctSize;i++){
	    theStream.skipBytes(2); // the reverse index 
	    // we skip the index byte 
	    r[i]=theStream.readByte() ; theStream.skipBytes(1) ; 
	    g[i]=theStream.readByte() ; theStream.skipBytes(1) ; 
	    b[i]=theStream.readByte() ; theStream.skipBytes(1) ; 
	} // for
	return(ctSize*8+4);
    } // read
    
    /** Transforms the color table into a color model for a given mode
     *  @param mode the mode to build the color table on
     *  the following modes are supported:
     *  <ul><li>or<li>copy</ul>
     *  other modes are mapped onto the or model
     *  @exception QDException Unsupported mode
     */ 
    
    public ColorModel toModel(short mode) 
	throws QDException {
	switch (mode){
	case QDModes.OR:
	    return new IndexColorModel(8,ctSize,r,g,b,0);
	case QDModes.COPY:
	    return  new IndexColorModel(8,ctSize,r,g,b,256);
	default: 
		// throw new QDCannotbuildModel(mode);
	    System.out.println("Warning: "+QDCannotbuildModel.toString(mode));
	    return  new IndexColorModel(8,ctSize,r,g,b,0);
	} // switch
    } // ColorModel
    
    public String toString()
	{return("Color Table "+ctSize+" entries");}
    
    /** Builds the color model for 1 bit color table.<br>
     *  Bitmap colorizing code should act here. 
     *  @param mode the mode of the 1 bit color table
     *  following modes are supported:
     *  <ul><li>bic<li>copy<li>or</ul>
     *  @exception QDException unsupported mode
     */ 
	
    public static ColorModel get1BitModel(short mode) 
	throws QDException {
	byte blackByte ;
	byte whiteByte ;
	byte blackAlpha ;
	byte whiteAlpha ;
	switch(mode){
	    
	case QDModes.BIC:
	    blackByte= (byte) 0xff ;
	    whiteByte= (byte) 0xff ;
	    blackAlpha= (byte) 0xff ;
	    whiteAlpha= (byte) 0x00 ;
	    break ;
  case QDModes.COPY:
	    blackByte= (byte) 0x00 ;
	    whiteByte= (byte) 0xff ;
	    blackAlpha= (byte) 0xff ;
	    whiteAlpha= (byte) 0xff ;
	    break ;
	case QDModes.OR:
	    blackByte= (byte) 0x00 ;
	    whiteByte= (byte) 0xff ;
	    blackAlpha= (byte) 0xff ;
	    whiteAlpha= (byte) 0x00 ;
	    break ;
	default: throw 
		     new QDCannotbuildModel(mode);
	} // switch		
	final byte red[] = {whiteByte,blackByte} ;
	final byte blue[] = {whiteByte,blackByte} ;
	final byte green[] = {whiteByte,blackByte} ;
	final byte alpha[] = {whiteAlpha,blackAlpha} ;
	return new IndexColorModel(1,2,red,blue,green,alpha);
    } // get1BitModel	
    
} // QDColorTable

/** The size of the color table is negative 
 *  file is corrupt
 *  @author Matthias Wiesmann
 *  @version 1.0
 */ 

class QDNegativeColorTable extends QDException
{
    protected int size ;
    public QDNegativeColorTable(int size)
	{this.size=size ;} // QDNegativeColorTable
    public String toString()
	{return("Negative Color Table "+size);}
} // QDNegativeColorTable

/** Unable to build the color model for requested mode
 *  @author Matthias Wiesmann
 *  @version 1.0
 */ 
	
class QDCannotbuildModel extends QDException
{
    static final String MESSAGE = 
    "cannot change color table to color model with mode " ;
    protected short theMode ;
    public QDCannotbuildModel(short mode) {	
	this.theMode=mode ; }
    
    protected static String toString(short mode) { 
	return MESSAGE+QDModes.toString(mode);
    } // toString
    
    public String toString() { 
	return toString(theMode);
    } // toString
    
} // QDCannotbuildModel






