//                              -*- Mode: Java -*- 
// QDDirectPixMap.java --- 
// Author          : Matthias Wiesmann
// Created On      : Tue Nov 21 15:22:07 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Wed Dec  6 11:55:43 2000
// Update Count    : 7
// Status          : Unknown, Use with caution!
// 

package ch.epfl.lse.jqd.image;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.utils.*;
import java.awt.image.*;

import ch.epfl.lse.jqd.io.QDInputStream;
import java.io.IOException;

/** QuickDraw Direct Pix Map
 *  This class describes a RGB described pixmap
 *  @author Matthias Wiesmann
 *  @version 1.0 revised
 */ 

public class QDDirectPixMap extends QDPixMap
{
    /** Base address of the PixMap in memory.
     *  Not used / neither usefull
     */ 
    protected long baseAddr ;

    public QDDirectPixMap()
	{} // QDDirectPixMap
    /** The color model for direct 24 bits images */ 
    protected static final ColorModel DIRECT_24_MODEL = new DirectColorModel(24,0xff0000,0xff00,0xff);
    /** The color model for direct 16 bits images */ 
    protected static final ColorModel DIRECT_16_MODEL = new DirectColorModel(15,0x7C00,0x3e0,0x1f);
    
    public int 	readStart(QDInputStream theStream) 
	throws IOException, QDException {
	baseAddr=theStream.readInt();
	rowBytes = convertRowBytes(theStream.readShort());
	bounds = theStream.readRect();
	pmVersion = theStream.readShort() ;
	packType = theStream.readShort() ;
	packSize=theStream.readInt();
	hRes = theStream.readFrac() ;
	vRes = theStream.readFrac() ;
	pixelType=theStream.readShort();
	pixelSize=theStream.readShort();
	cmpCount=theStream.readShort();
	cmpSize=theStream.readShort();
	planeByte=theStream.readInt();
	colorTableID=theStream.readInt();
	theStream.skipBytes(4);
	return(40);
    } // read
   
    /** Reads data for packing mode 1?
     *  Not implemented. 
     */
    public int readPack1(QDInputStream theStream) 
	throws IOException, QDException {
	int dataSize = rowBytes * bounds.height ;
	return(dataSize);
    } // readPack1
    
    /** Reads data for packing mode 3 
     *  (16-bit RLE compressed pixmaps). 
     *  @param theStream the data stream
     *  @return the number of byte read
     *  @exception QDException illegal component number
     */ 
    public int readPack3(QDInputStream theStream) 
	throws IOException, QDException {
	int dataLen = 0;
	short data[][] = new short[bounds.height][bounds.width] ;
	for (int line=0;line<bounds.height;line++) {
	    int lineCount ;
	    if (rowBytes>250)	{ // line Count is Byte
		lineCount=theStream.readUnsignedShort();
		dataLen+=(2+lineCount);
	    } else	{ // line count is word
		lineCount=theStream.readUnsignedByte() ;
		dataLen+=(1+lineCount);
	    } // line count is word
	    byte[] packed = new byte[lineCount] ;
	    theStream.readFully(packed);
	    QDBitUtils.unpackLine(packed,data[line]);
	} // for
	int pixData[] = QDBitUtils.short2RGB(data,QDUtils.rect2Dim(bounds));
	imageProd = new MemoryImageSource(bounds.width,bounds.height,
					   DIRECT_16_MODEL,
					   pixData,0,bounds.width);
	return(dataLen);
    } // readPack3

    /** Reads data for packing mode 4 
     *  (24-bit RLE compressed pixmap). 
     *  @param theStream the data stream
     *  @return the number of byte read
     *  @exception QDException illegal component number
     */ 

    public int readPack4(QDInputStream theStream) 
	throws IOException, QDException {
	int dataLen = 0;
	if (cmpCount!=3) throw new QDWrongComponentNumber(cmpCount);
	byte data[][] = new byte[bounds.height][bounds.width*cmpCount] ;
	for (int line=0;line<bounds.height;line++) {
	    int lineCount; 
	    if (rowBytes>250)	{ // line Count is Byte
		lineCount=theStream.readUnsignedShort();
		dataLen+=(2+lineCount);
	    } else { // line count is word
		lineCount=theStream.readUnsignedByte() ;
		dataLen+=(1+lineCount);
	    } // line count is word
	    byte[] packed = new byte[lineCount] ;
	    theStream.readFully(packed);
	    QDBitUtils.unpackLine(packed,data[line]);
	} // for line
	int pixData[] = QDBitUtils.byte2RGB(data,QDUtils.rect2Dim(bounds));
	imageProd = new MemoryImageSource(bounds.width,bounds.height,DIRECT_24_MODEL,pixData,0,bounds.width);
	return (dataLen);
    } // readPack4
    
    /** This method reads the data according to the packing type
     *  @param theStream input Stream
     *  @exception IOException I/O problem
     *  @exception QDException <ul>
     *             <li>unknown pack type
     *             <li>wrong number of components
     *             </ul>
     *  @see #readPack4
     *  @see #readPack3
     */ 

    public int 	readData(QDInputStream theStream) 
	throws IOException, QDException {
	// System.err.println(this.toString());
	int dataLen = 0 ;
	switch (packType) {
	case 3: dataLen+=readPack3(theStream); break ;
	case 4: dataLen+=readPack4(theStream); break ;
	default: throw  new QDUnknownPackException(packType);
	} //
	return dataLen ; 
    } // readData
	
    protected String nameString()
	{ return("Direct Pixmap");}
    
} // QDDirectPixMap
	
	



