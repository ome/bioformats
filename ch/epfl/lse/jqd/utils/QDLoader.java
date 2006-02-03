//                              -*- Mode: Java -*- 
// QDLoader.java --- 
// Author          : Matthias Wiesmann
// Created On      : Thu Dec 16 16:46:29 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Thu Dec 16 16:46:35 1999
// Update Count    : 1
// Status          : Renamed
// 

package ch.epfl.lse.jqd.utils;

import ch.epfl.lse.jqd.basics.QDRegion; 
import ch.epfl.lse.jqd.basics.QDException;

import ch.epfl.lse.jqd.io.QDInputStream;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Polygon; 
import java.awt.Color;
import java.awt.Font;

import java.io.DataInputStream;
import java.io.IOException;


/** This abstract class contains methods to load the 
 *  following Macintosh Data from a Stream:
 *  <ul>
 *  <li>QuickDraw types: 
 *      <ul>
 *      <li>points
 *      <li>dimensions
 *      <li>rectangles
 *      <li>polygons
 *      <li>regions
 *      </ul>
 *  <li>Macintosh Strings
 *  <li>Macintosh fixed point numbers
 *  <li>Macintosh fractionnal numbers
 *  </ul>
 *  @author Matthias Wiesmann
 *  @version 1.1
 *  @deprecated use QDInputStream
 */ 

public final class QDLoader
{
    protected QDLoader() {}

    /** reads a point in QD format from a stream 
     * @param theStream where to read from
     * @return the point
     * @exception IOException I/O Problem
     */
	
    public static Point readPoint(DataInputStream theStream) 
	throws IOException {
	final int y = theStream.readShort() ;
	final int x = theStream.readShort() ;
	return new Point(x,y);
    } // loadPoint
		
    /** reads a dimension in QD format from a stream
     * @param theStream where to read from
     * @return the dimension
     * @exception IOException I/O Problem
     */
    
    public static Dimension readDimension(DataInputStream theStream) 
	throws IOException
	{
	    final int h = theStream.readShort() ;
	    final int v = theStream.readShort() ;
	    return new Dimension(h,v);
	} // readDimension
		
    /** reads a dimension in short QD format from a stream 
     * @param theStream where to read from
     * @return the dimension
     * @exception IOException I/O Problem
     */
		
    public static Dimension readDHDV(DataInputStream theStream) 
	throws IOException {
	final int dh = theStream.readUnsignedByte() ;
	final int dv = theStream.readUnsignedByte() ;
	return new Dimension(dh,dv);
    } // readDHDV
    
    /** reads a dimension in short DV QD format from a stream 
     * @param theStream where to read from
     * @return the dimension
     * @exception IOException I/O Problem
     */
    
    public static Dimension readDV(DataInputStream theStream) 
	throws IOException {
	final int dv = theStream.readUnsignedByte() ;
	return new Dimension(0,dv);
    } // readDV
		
    /** reads a dimension in short DH QD format from a stream 
     * @param theStream where to read from
     * @return the dimension
     * @exception IOException I/O Problem
     */
    
    public static Dimension readDH(DataInputStream theStream) 
	throws IOException {
	final int dh = theStream.readUnsignedByte() ;
	return new Dimension(dh,0);
    } // readDH
		
    /** reads a short dimension from a stream 
     * @param theStream where to read from
     * @return the dimension
     * @exception IOException I/O Problem
     */
    
    public static Dimension readShortDimension(DataInputStream theStream) 
	throws IOException {
	final int h = theStream.readByte() ;
	final int v = theStream.readByte() ;
	return new Dimension(h,v);
    } // readShortDimension
    
    /** reads a style identifier from a stream 
     * @param theStream where to read from
     * @return a AWT style integer selector
     * @see java.awt.Font
     * @exception IOException I/O Problem
     */ 
		
    public static int readStyle(DataInputStream theStream) 
	throws IOException {
	final int style = theStream.readUnsignedByte() ;
	int result = Font.PLAIN ;
	if ((style & 1)!=0) result+=Font.BOLD ;
	if ((style & 2)!=0) result+=Font.ITALIC ;
	return result ;
    } // readStyle	
    
   
    /** reads a rectangle from a stream
     * @param theStream where to read from
     * @return the rectangle
     * @exception IOException I/O Problem
     */
	
    public static  Rectangle readRect(DataInputStream theStream) 
	throws IOException {
	final Point a = readPoint(theStream) ;
	final Point b = readPoint(theStream) ;
	return QDUtils.point2Rect(a,b);
    } // loadRect
		
    /** reads a polygon from a stream 
     * @param theStream where to read from
     * @return the polygon
     * @exception IOException I/O Problem
     */
		
    public static Polygon readPoly(DataInputStream theStream) 
	throws IOException {
	final int points=  (theStream.readShort()-10)/4 ;
	Polygon poly = new Polygon();
	    final Rectangle bounds = readRect(theStream) ;
	    for (int i=0 ; i<points ; i++) {
		Point nextPoint = readPoint(theStream);
		poly.addPoint(nextPoint.x,nextPoint.y);
	    } // for i
	    return (poly);
    } // readPolygon
    
    /** calculates the size of a polygon in QD format.
     * Usefull to know how much place a polygon takes
     * in the file. 
     * @param thePoly the polygon
     * @return the size (in bytes).
     */ 
		
    public static int polySize(Polygon thePoly) {
	return(10+thePoly.npoints*4);
    } // polySize
    
    /** reads a region from a stream 
     * does noting serious with the data, as regions are not implemented 
     * @see jqd.basics.QDRegion#read
     * @param theStream where to read from
     * @return the region
     * @exception IOException I/O Problem
     */
		
    public static QDRegion readRegion(QDInputStream theStream) 
	throws IOException {
	QDRegion rgn = new QDRegion();
	rgn.read(theStream) ;
	return(rgn);
    } // readRegion
		
    /** reads a QD color from a stream 
     * @param theStream where to read from
     * @return an AWT Color
     * @exception IOException I/O Problem
     */ 
		
    public static Color readColor(DataInputStream theStream) 
	throws IOException {
	final int r = theStream.readUnsignedShort() ;
	final int g = theStream.readUnsignedShort() ;
	final int b = theStream.readUnsignedShort() ;
	return new Color(r/256,g/256,b/256);
    } // readColor
		
    /** reads an old QD color (Quickdraw 1) from a stream
     * @param theStream where to read from
     * @return an AWT Color
     * @see jqd.utils.QDUtils#int2Color
     * @exception IOException I/O Problem
     * @exception QDException unknown old QuickDraw Color
     */ 
		
    public static Color readOldColor(DataInputStream theStream) 
	throws java.io.IOException, QDException {
	final int c = theStream.readInt();
	return QDUtils.int2Color(c);
    } // readOldColor
    
    /** reads a string from a stream
     * @see jqd.utils.QDTextUtils#translateASCII
     * @param theStream where to read from
     * @return a String
     * @exception IOException I/O Problem
     * @exception QDException String conversion problem
     */ 
		
    public static String readString(DataInputStream theStream) 
	throws IOException , QDException {
	int strlen = theStream.readUnsignedByte() ;
	byte[] strdata = new byte[strlen] ;
	theStream.readFully(strdata) ; 
	return QDTextUtils.translateASCII(strdata);
    } // readString
    
    /** Reads a creator type ( 4 bytes string) 
     * @param theStream where to read from
     * @return a 4 character String
     * @exception IOException I/O Problem
     * @exception QDException String conversion problem
     * @see #readString
     */

    public static String readCreator(DataInputStream theStream)
	throws IOException, QDException {
	byte[] strdata = new byte[4] ;
	theStream.readFully(strdata);
	return QDTextUtils.translateASCII(strdata);
    } // readCreator

    /** Reads a 32 bytes Macintosh pascal string 
     * (<code>str31</code>). 
     *  Those strings are often used for short text
     *  and filenames. 
     *  @param theStream where to read from
     *  @return a String, of maximum length 31 
     *  @exception IOException I/O Problem
     *  @exception QDException String conversion problem
     *  @see #readString
     */ 
     
    public static String readStr31(DataInputStream stream) 
	throws IOException, QDException {
	final String text = readString(stream);
	final int l = 31 - text.length();
	if (l<0) throw new StringToLong(text.length());
	stream.skipBytes(l);
	return text ;
    } // readStr32

    /** reads a macintosh fixed point number from a stream 
     * @param theStream where to read from
     * @return the fixed point converted to <code>double</code>.
     * @exception IOException I/O Problem
     */ 
		
    public static double readFixed(DataInputStream theStream) 
	throws IOException {
	int a = theStream.readInt() ;
	return (double) a / (double) 0xFFFF ;
    } // readFixed
    
    /** reads a macintosh fractionnal number from a stream.<br>
     *  <strong>Warning</strong> need to be checked.
     *  @param theStream where to read from
     *  @return the fraction transformed to <code>double</code>.
     */

    public static double readFrac(DataInputStream theStream) 
	throws IOException {
	int a = theStream.readShort();
	int b = theStream.readShort();
	return (double) a / (double) b ;
    } // readFrac
		
} // QDLoader
	
class StringToLong extends QDException 
{
    protected int length ;
    StringToLong(int len) {
	length=len ; }
    
    public String toString(){ 
	return "String to long: "+length ;
    } // toString

} 
