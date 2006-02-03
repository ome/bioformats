//                              -*- Mode: Java -*- 
// QDRegion.java --- 
// Author          : Matthias Wiesmann
// Created On      : Mon Jul 12 16:29:41 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 12:08:47 2000
// Update Count    : 40
// Status          : Partial Support
// 

package ch.epfl.lse.jqd.basics;

import java.awt.*;

import ch.epfl.lse.jqd.io.QDInputStream;
import java.io.IOException;

/**
 * Quickdraw Region Definition 
 * This object only contains the bounding box. 
 * This object implements all operation of Java 1.1 Shape Object. 
 * No actual operations on regions are implemented for the moment.<br>
 * Regions <em>seem</em> to be packed bitmaps. 
 * @author Matthias Wiesmann
 * @version 1.0 revised
 */

public class QDRegion 
{
    /** size of the minimal region */
    public static final short MIN_RGN_SIZE = 10 ;
    public int rgnSize ;
    protected int restData ;
    public Rectangle rgnBox ;
    protected QDRegionRun[] runs = null; 

    /** reads the region from the stream.
     *  discards most of the data
     *  @param theStream where to read from
     *  @return the number of bytes read 
     *  (minimum 10). 
     */ 
    public int read(QDInputStream theStream) 
	throws IOException {
	rgnSize=theStream.readShort();
	if (rgnSize<0) throw new IOException("negative region size "+rgnSize);
	rgnBox=theStream.readRect();
	restData = rgnSize - MIN_RGN_SIZE ;
	if (restData>0) { 
	    // theStream.skipBytes(restData);
	    runs = QDRegionRun.factory(theStream,restData);
	} // if restData
	if (restData<0) throw new IOException("negative rest data "+restData);
	return(rgnSize);
    } // read

    public QDRegion() {
    } // QDRegion

    public QDRegion(QDInputStream theStream) 
	throws IOException {
	read(theStream);
    } // QDRegion

    public boolean isRectangle() {
	return (restData == 0) ;
    } // isRectangle

    public String toString() {
	if (rgnSize==10) return rgnBox.toString();
	return rgnBox.toString() + 
	    "data size "+restData ;
    } //toString
    
    /** Shape Interface Methode
     *  @return the bounding box of the region
     */ 

    public Rectangle getBounds() { return rgnBox ; } 

    public QDRegionRun[] getRuns() {
	return runs ;
    } // getRuns
    
    public void frame(QDPort port) throws QDException {
	for(int i=0;i<runs.length;i++) {
	    port.stdRect(QDVerbs.FILL_VERB,runs[i].getBounds());
	} // for
    } // frame

    public void paint(QDPort port) throws QDException {
	int i=0 ;
	if (runs== null) {
	    return ;
	} // if no runs
	while(i<runs.length) {
	    final QDRegionRun a = runs[i] ;
	    Rectangle r ; 
	    if (a.isBorder()) {
	       r = a.getBounds(runs[i+1]);
	       i++; }
	    else r = a.getBounds();
	    port.stdRect(QDVerbs.FILL_VERB,r);
	    i++; 
	} //whilw
    } // paint
    
} // QDPattern


