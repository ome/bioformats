//                              -*- Mode: Java -*- 
// QDBitMap.java --- 
// Author          : Matthias Wiesmann
// Created On      : Tue Nov 21 15:21:25 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Wed Dec  6 11:42:25 2000
// Update Count    : 6
// Status          : OK
// 

package ch.epfl.lse.jqd.image;

import java.awt.*;
import java.awt.image.*;
import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.utils.*;

import java.io.IOException;
import ch.epfl.lse.jqd.io.QDInputStream;


/** This class describes a basic QuickDraw Bitmap 
 *  @version 2.0 revised
 *  @author Matthias Wiesmann
 *  @see PixMap
 */ 

public class QDBitMap {
    /** the bounding box of the bitmap */ 
    public Rectangle bounds ;
    /** the transfer mode */ 
    protected short mode ;
    /** the number of bytes per row */
    protected int   rowBytes ;
    /** the AWT image producer */ 
    protected ImageProducer imageProd ;
    /** the AWT image object. */
    protected Image image ;
    /** the source rectangle (i.e. the rectangle of the bitmap ) */
    public Rectangle src ;
    /** the destination rectangle (i.e the rectangle in the picture ) */
    public Rectangle dest ;
    
    /** protected constructor */ 
    protected QDBitMap() {} // QDBitMap
    
    /** constructor 
     * @param rowBytes the number of bytes per row 
     */

    public QDBitMap(int rowBytes) 	{
	this();
	this.rowBytes = rowBytes ;
    } // QDBitMap
    
    /** Reads the starting information about the bitmap.
     *  This information is the bounding box of the bitmap
     *  @param theStream the stream to read the data from
     *  @return the number of bytes read
     */ 

    protected int readStart(QDInputStream theStream) 
	throws IOException, QDException	{
	bounds = theStream.readRect();
	return(QDInputStream.RECT_SIZE);
    } // readStart
    
    /** Reads the ending information about the bitmap.<br>
     *  This information is the following:<br>
     *  <ul>
     *  <li>the source rectangle 
     *  <li>the dest rectangle 
     *  <li>the mode 
     *  </ul>
     *  @param theStream the stream to read the data from
     *  @return the number of bytes read
     */

    protected int readEnd(QDInputStream theStream) 
	throws IOException, QDException {
	src = theStream.readRect();
	dest = theStream.readRect();
	mode = theStream.readShort();
	return(2*QDInputStream.RECT_SIZE+QDInputStream.MODE_SIZE);
    } // readEnd
    
    /** Reads the actual data of the BitMap 
     *  @param theStream the stream to read the data from
     *  @return the number of bytes read
     */ 

    protected int readData(QDInputStream theStream) 
	throws IOException, QDException {
	byte data[][] = new byte[bounds.height][rowBytes] ;
	for (int line=0;line<bounds.height;line++)
	    theStream.readFully(data[line]);
	byte[] pixData = QDBitUtils.bit2Byte(data,QDUtils.rect2Dim(bounds),rowBytes) ;
	ColorModel twoBits = QDColorTable.get1BitModel(mode);
	imageProd = new MemoryImageSource(bounds.width,
					   bounds.height,
					   twoBits,
					   pixData,
					   0,
					   bounds.width);
	return(bounds.height*rowBytes);
    } // readData
    
    /** Reads the BitMap from the stream 
     *  and builds the image structure. 
     *  This reading is done in multiple phases:
     *  <ol>
     *  <li><code>readStart</code>
     *  <li><code>readEnd</code>
     *  <li><code>readData</code>
     *  </ol>
     *  @param theStream the stream to read the data from
     *  @return the number of bytes read
     */

    public int read(QDInputStream theStream) 
	throws IOException, QDException {
	final int startLen=readStart(theStream);
	final int endLen =readEnd(theStream);
	final int dataLen =readData(theStream);
	buildImage();
	return(startLen+endLen+dataLen);
    } // read
    
    /** Utility Method, builds either a BitMap or a PixMap 
     *  depeding of the <code>rowbytes information</code>
     *  @param rb rowbytes
     *  @return either a new <code>BitMap</code> or 
     *  a new <code>PixMap</code>. 
     */ 

    public static QDBitMap newMap(short rb) {
	if (rb >0) return new QDBitMap(rb);
	else return new QDPixMap(rb);
    } // newMap
    
    /** Information method, used by to <code>toString</code>
     *  it is overloaded by the PixMap object 
     *  @return the text <code>bitmap</code>
     */ 

    protected String nameString() { 
	return("bitmap");}
    
    /** @eturn information about the object */ 

    public String toString() {
	return(nameString()+" "+
	       QDModes.toString(mode)+
	       "\trowbytes = "+rowBytes+
	       "\tbounds"+ bounds+
	       "\tsrc "+src+
	       "\tdest "+dest);
    } //toString
    
    /** builds the <code>AWT</code> image object 
     *  The cropping and filtering is done here 
     */ 

    protected void buildImage() {
	int x = src.x - bounds.x ;
	int y = src.y - bounds.y ;
	CropImageFilter f = new CropImageFilter(x,y,src.width,src.height);
	ImageProducer temp = new FilteredImageSource(imageProd,f);
	image = Toolkit.getDefaultToolkit().createImage(temp);
    } // buildImage
		
    /** @return the <code>AWT</code> image object associated 
	to the bitmap */ 

    public Image getImage() {
	return(image);} // getImage
    
} // QDBitMap
