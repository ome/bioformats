//                              -*- Mode: Java -*- 
// QDPackedBitMap.java --- 
// Author          : Matthias Wiesmann
// Created On      : Tue Nov 21 15:24:48 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Wed Dec  6 11:53:13 2000
// Update Count    : 2
// Status          : Unknown, Use with caution!
// 


package ch.epfl.lse.jqd.image;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.utils.*;
import java.awt.image.*;

import ch.epfl.lse.jqd.io.QDInputStream;
import java.io.IOException;

/** Quickdraw Packed BitMap 
 *  @version 1.0 revised 
 *  @author Matthias Wiesmann
 */ 

public class QDPackedBitMap extends QDBitMap
{
    public QDPackedBitMap(int rowBytes) {
	super(rowBytes);
    } // QDBitMap
    
    /** Reads the compacted data 
     */ 

    protected int readData(QDInputStream theStream) 
	throws IOException, QDException {
	int dataLen = 0;
	byte data[][] = new byte[bounds.height][rowBytes] ;
	for (int line=0;line<bounds.height;line++) {
	    int lineCount ;
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
	byte[] pixData  ;
	pixData = QDBitUtils.bit2Byte(data,QDUtils.rect2Dim(bounds),rowBytes) ;
	ColorModel twoBits = QDColorTable.get1BitModel(mode);
	imageProd = new MemoryImageSource(bounds.width,
					   bounds.height,
					   twoBits,pixData,0,bounds.width);
	return(dataLen);
    } // readData
    
    /** Factory method, builds either a packed or unpacked bitmap 
     *  @param rb the rowbytes value 
     *  @return the right BitMap kind
     */ 

    public static QDBitMap newMap(short rb) {
	if (rb >0) return new QDPackedBitMap(rb);
	else return new QDPackedPixMap(rb);
    } // newMap
    
} // QDPackedBitMap
