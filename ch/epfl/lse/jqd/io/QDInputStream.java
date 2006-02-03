//                              -*- Mode: Java -*- 
// QDInputStream.java --- 
// Author          : Matthias Wiesmann
// Created On      : Tue Oct  5 12:24:01 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Wed Dec  6 11:50:32 2000
// Update Count    : 49
// Status          : Renamed
// 

package ch.epfl.lse.jqd.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;

import java.awt.Point;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Font;
import java.awt.Polygon;
import java.awt.Color; 

import ch.epfl.lse.jqd.utils.QDUtils;
import ch.epfl.lse.jqd.utils.QDTextUtils;
import ch.epfl.lse.jqd.basics.QDException; 

/** This class handles all I/O for QD objects.
 *  All classes in the jqd package load using this class.
 *  It implements all the methods needed to load all basic
 *  QuickDraw types from a stream. 
 *  @author Matthias Wiesmann
 *  @version 1.0
 */ 

public class QDInputStream extends DataInputStream {

    public static final int STYLE_SIZE = 1 ;
    public static final int POINT_SIZE = 4 ;
    public static final int RECT_SIZE = 8 ;
    public static final int SHORT_DIMENSION_SIZE = 2 ;
    public static final int OLD_COLOR_SIZE = 4; 
    public static final int FRAC_SIZE = 4 ;
    public static final int DV_SIZE = 2 ;
    public static final int DH_SIZE = 2 ; 
    public static final int MODE_SIZE = 2 ;

    /** calculates the size of a polygon in QD format.
     * Usefull to know how much place a polygon takes
     * in the file. 
     * @param thePoly the polygon
     * @return the size (in bytes).
     */ 
		
    public static int polySize(Polygon thePoly) {
	return(10+thePoly.npoints*4);
    } // polySize

     /** reads a macintosh fractionnal number from a stream.<br>
     *  <strong>Warning</strong> need to be checked.
     *  @return the fraction transformed to <code>double</code>.
     */

    public double readFrac() 
	throws IOException {
	int a = readShort();
	int b = readShort();
	return (double) a / (double) b ;
    } // readFrac

    /** Constructor 
     *  @param stream the original stream
     */ 

    public QDInputStream(InputStream stream) {
	super(stream); 
    } // QDInputStream
    
     /** reads a point in QD format 
     * @return an AWT point
     * @exception IOException I/O Problem
     */

    public Point readPoint() throws IOException {
	final int y = readShort() ;
	final int x = readShort() ;
	return new Point(x,y);
    } // readPoint
    
    /** reads a dimension in QD format 
     * @return the dimension
     * @exception IOException I/O Problem
     */

    public Dimension readDimension() throws IOException {
	final int h = readShort() ;
	final int v = readShort() ;
	return new Dimension(h,v);
    } // readDimension

    /** reads a dimension in short QD format 
     * @return the dimension
     * @exception IOException I/O Problem
     */

    public Dimension readShortDimension() throws IOException {
	final int h = readByte() ;
	final int v = readByte() ;
	return new Dimension(h,v);
    } // readShortDimension

    /** reads a dimension in short relative QD format
     * @return the dimension
     * @exception IOException I/O Problem
     */

    public Dimension readDHDV() throws IOException {
	final int dh = readUnsignedByte() ;
	final int dv = readUnsignedByte() ;
	return new Dimension(dh,dv);
    } // readDHDV

     /** reads a dimension in short vertical relative QD format 
     * @return the dimension
     * @exception IOException I/O Problem
     */

    public Dimension readDV() throws IOException {
	final int dv = readUnsignedByte() ;
	return new Dimension(0,dv);
    } // readDV
    
    /** reads a dimension in short horizontal relative QD format 
     * @return the dimension
     * @exception IOException I/O Problem
     */
    
    public Dimension readDH() throws IOException {
	final int dh = readUnsignedByte() ;
	return new Dimension(dh,0);
    } // readDH

    /** reads a rectangle from a stream
     * @return the rectangle
     * @exception IOException I/O Problem
     */

    public Rectangle readRect() throws IOException {
	final Point a = readPoint() ;
	final Point b = readPoint() ;
	return QDUtils.point2Rect(a,b);
    } // loadRect

    /** reads a style identifier from a stream 
     * @return a AWT style integer selector
     * @see java.awt.Font
     * @exception IOException I/O Problem
     */ 

    public int readStyle() throws IOException {
	final int style = readUnsignedByte() ;
	int result = Font.PLAIN ;
	if ((style & 1)!=0) result+=Font.BOLD ;
	if ((style & 2)!=0) result+=Font.ITALIC ;
	return result ;
    } // readStyle	

    /** Reads a polygon from a stream.
     * @return the polygon
     * @exception IOException I/O Problem
     */

    public Polygon readPoly() throws IOException {
	final int points= (readShort()-10)/4 ;
	Polygon poly = new Polygon();
	final Rectangle bounds = readRect() ;
	for (int i=0 ; i<points ; i++) {
	    Point nextPoint = readPoint();
	    poly.addPoint(nextPoint.x,nextPoint.y);
	} // for i
	return (poly);        
    } // readString

    /** Reads a creator type (4 bytes string) 
     * Creator codes are used to indentify Macintosh Programs, 
     * They are for instance used to mark proprietary QuickDraw Comments. 
     * @return a 4 character String
     * @exception IOException I/O Problem
     * @exception QDException String conversion problem
     * @see #readString
     */

    public String readCreator()
	throws IOException, QDException {
	byte[] strdata = new byte[4] ;
	readFully(strdata);
	return QDTextUtils.translateASCII(strdata);
    } // readCreator

    /** Reads a 32 bytes Macintosh pascal string 
     * (<code>str31</code>). 
     *  Those strings are often used for short text
     *  and filenames. 
     *  @return a String, of maximum length 31 
     *  @exception IOException I/O Problem
     *  @exception QDException String conversion problem
     *  @see #readString
     */ 
     
    public String readStr31() 
	throws IOException, QDException {
	final String text = readString();
	final int l = 31 - text.length();
	if (l<0) throw new StringToLong(text.length());
	skipBytes(l);
	return text ;
    } // readStr32

    /** Reads a pascal string from the stream
     *  Pascal stream first contain the length,
     *  then the actual text data.
     *  @return the string converted from the Macintosh encoding
     *  @exception IOException I/O Problem
     *  @exception QDException ASCII transaltion Problem
     */ 

    public String readString() 
	throws IOException, QDException {
	final int l = readUnsignedByte() ;
	byte array[] = new byte[l] ;
	readFully(array);
	return QDTextUtils.translateASCII(array);
    } // readString

    /** reads a QD color from a stream 
     * @return an AWT Color
     * @exception IOException I/O Problem
     */ 

    public Color readColor() 
	throws IOException, QDException {
	final int r = readUnsignedShort() ;
	final int g = readUnsignedShort() ;
	final int b = readUnsignedShort() ;
	return new Color(r/256,g/256,b/256);
    } // readColor

    /** reads an old QD color (Quickdraw 1) from a stream
     * @return an AWT Color
     * @see ch.epfl.lse.jqd.utils.QDUtils#int2Color
     * @exception IOException I/O Problem
     * @exception QDException unknown old QuickDraw Color
     */ 
		
    public Color readOldColor() 
	throws java.io.IOException, QDException {
	final int c = readInt();
	return QDUtils.int2Color(c);
    } // readOldColor

} // QDInputStream




