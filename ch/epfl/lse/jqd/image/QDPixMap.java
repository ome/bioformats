//                              -*- Mode: Java -*- 
// QDPixMap.java --- 
// Author          : Matthias Wiesmann
// Created On      : Mon Jul 12 17:34:01 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Wed Dec  6 12:01:48 2000
// Update Count    : 12
// Status          : OK
// 

package ch.epfl.lse.jqd.image;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.utils.*;
import java.awt.image.*;
import ch.epfl.lse.jqd.io.QDInputStream;
import java.io.IOException;

/** This class describes a QuickDraw Pixmap 
 *  @see QDBitMap
 *  @author Matthias Wiesmann
 *  @version 2.0 
 */ 

public class QDPixMap extends QDBitMap 
{
    protected short pmVersion ;
    /** Packing algorithm type 
     *  The value can be the following 
     *  (Inside Macintosh Quickdraw p A-16) 
     * <dl compact>
     * <dt>0<dd>Default
     * <dt>1<dd>No packing
     * <dt>2<dd>Remove Pad Byte (32 bit pixels / 24 bit data).
     * <dt>3<dd>RLE for <code>pixelSize</code> Chunks,
     * one scan line at time - supported only for 16-bit pixels. 
     * <dt>4<dd>RLE one component at a time, one scan line at a time, 
     * red first, supported only for 32-bit pixels (24 bit image data).
     * </dl>
     */ 
    protected short  packType ;
    protected int    packSize ;
    protected short  pixelType ;
    /** Number of bits per pixel */ 
    protected short  pixelSize ;
    /** Number of components (?)*/
    protected short  cmpCount ;
    /** Number of bits per component (?) */ 
    protected short  cmpSize ;
    /** Number of bytes per plane (?)*/ 
    protected int    planeByte ;
    /** Id of the color table, if needed */
    protected int    colorTableID ;
    /** horizontal resolution */
    protected double hRes ;
    /** vertical resolution */ 
    protected double vRes ;
    /** color table associated to the pix-map */
    protected QDColorTable colorTable ;
    
    protected QDPixMap()
	{} // QDPixMap
    
    /** Transforms the rowbytes into the <cite>real</cite> value.
     *  The rowbytes field is used to signal both the size of a row
     *  and the type of the bitmap. 
     *  @param r the recorded row bytes value
     *  @return the real value of a row of bytes
     */ 

    protected static int convertRowBytes(int r) {
	return (r+32768);
    } // convertRowBytes
    
    /** Constructor.
     *  The value rowbytes is the unconverted value
     *  e.g. this value must be negative
     */ 

    public QDPixMap(int rowBytes) {
	this.rowBytes = convertRowBytes(rowBytes) ;
    } // QDPixMap
    
    
    protected  int readStart(QDInputStream theStream) 
	throws IOException, QDException 	{
	int total= super.readStart(theStream);
	pmVersion=theStream.readShort();
	packType=theStream.readShort();
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
	total+=36 ;
	colorTable = new QDColorTable() ;
	total+=colorTable.read(theStream);
	return(total);
    } // readStart
    
    protected int readData(QDInputStream theStream) 
	throws IOException, QDException {
	byte data[][] = new byte[bounds.height][rowBytes] ;
	for (int line=0;line<bounds.height;line++)
	    theStream.readFully(data[line]);
	byte[] pixData  ;
	switch (pixelSize) {
	case 1: pixData = QDBitUtils.bit2Byte(data,QDUtils.rect2Dim(bounds),rowBytes); 
	    break ;
	case 2: pixData = QDBitUtils.twobit2Byte(data,QDUtils.rect2Dim(bounds),rowBytes); 
	    break ;
	case 4: pixData = QDBitUtils.fourbit2Byte(data,QDUtils.rect2Dim(bounds),rowBytes);
	    break ;
	case 8: pixData = QDBitUtils.byte2byte(data,QDUtils.rect2Dim(bounds),rowBytes) ; 
	    break ;
	default: throw new QDPixMapNotSupported(pixelSize,pixelType,packType);
	} // switch
	imageProd = new MemoryImageSource(bounds.width,
					   bounds.height,
					   colorTable.toModel(mode),
					   pixData,0,bounds.width);
	return(bounds.height*rowBytes);
    } // readData
    
    /** Overload the with text <code>pixmap</code> */ 

    protected String nameString()
	{ return("pixmap");}
    
    public String toString() { 
	return(super.toString()+
	       " pixel type "+pixelType+ 
	       " pixel size "+pixelSize+
	       " cmpCount "+cmpCount+
	       " cmpSize "+cmpSize+
	       " planeByte "+planeByte+
	       " packing " +packType+
	       " packSize "+packSize) ; 
    } // toString
} // QDPixMap

