//                              -*- Mode: Java -*- 
// QDUtils.java    --- 
// Author          : Matthias Wiesmann
// Created On      : Thu Dec 16 16:39:45 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Thu Dec 16 16:43:35 1999
// Update Count    : 2
// Status          : Renamed
// 

package ch.epfl.lse.jqd.utils;

import ch.epfl.lse.jqd.basics.*;
import java.awt.*;

/** This abstract class contains different static methods
 *  Mostly method to compensate AWT shortcomming:
 *  adding rects, manipulation dimensions etc...
 *  @author Matthias Wiesmann
 *  @version 1.1
 */ 

public final class QDUtils 
{
    public static final int WHITE_COLOR = 30 ;
    public static final int BLACK_COLOR = 33 ;
    public static final int YELLOW_COLOR = 69 ;
    public static final int MAGENTA_COLOR = 137 ;
    public static final int RED_COLOR = 205 ;
    public static final int CYAN_COLOR = 273 ;
    public static final int GREEN_COLOR = 341 ;
    public static final int BLUE_COLOR = 409 ;
	
    /** Builds a point using a dimension as delta
     *  @param a the original point
     *  @param d the delta dimension
     *  @return the resulting point
     */ 

    public static final Point addDimension(Point a, Dimension d) {
	final int x= a.x + d.width ;
	final int y= a.y + d.height ;
	return new Point(x,y);
    } // addDimension
    
    /** Builds a rect from two points 
     * @param a the upper left point
     * @param b the lower right point
     * @return the rectangle 
     */ 
    
    public static final Rectangle point2Rect(Point a,  Point b) {
	final Dimension d = new Dimension(b.x - a.x,b.y - a.y) ;
	return new Rectangle(a,d) ;
    } // Point2Rect
    
    /** Transforms a point into a dimension 
     * @param p the point
     * @return the dimension
     */ 
    
    public static final Dimension point2Dim(Point p) {
	return new Dimension(p.x,p.y);
    } // point2Dim
    
    /** Finds the dimension of a rectangle
     *  @param rect the rectangle
     *  @return the rect's dimension
     */ 
    
    public static final Dimension rect2Dim(Rectangle rect) {
	return new Dimension(rect.width,rect.height);
    } // rect2Dim
    
    /** Finds the upper-left point of a rect 
     *  @para rect the rectangle
     */ 
    
    public static final Point rect2Point(Rectangle rect) {
	return new Point(rect.x,rect.y);
    } // react2Point
    
    /** Returns text about a polygon 
     * @return text information
     */ 

    public static final String toString(Polygon thePoly) {
	return("Polygon "+thePoly.getBounds());
    } // toString
    
    /** Builds a color from a QuickDraw 1 color index 
     *  @param i the QuickDraw 1 color spec
     *  @return the corresponding AWT Color
     *  @exception QDException unknown color
     */ 
    
    public static final Color int2Color(int i) 
	throws QDException {
	switch(i) {
	case WHITE_COLOR:    return Color.white ;
	case BLACK_COLOR:    return Color.black ;
	case YELLOW_COLOR:   return Color.yellow ;
	case MAGENTA_COLOR:  return Color.magenta ;
	case RED_COLOR:      return Color.red ;
	case CYAN_COLOR:     return Color.cyan ;
	case GREEN_COLOR:    return Color.green;
	case BLUE_COLOR:     return Color.blue;
	default:
	    throw new QDUnknownColor(i);
	} // switch
    } //int2Color
    
    /** Checks if a certain pen dimension can draw 
     *  (i.e if both of it's dimension are &gt; 0)
     * @param pen the dimension 
     * @return <code>true</code> if the pen can draw
     */ 
	
    public static final boolean canDraw(Dimension pen) {
	if ((pen.width==0) | (pen.height==0)) return false ;
	else return true ;
    } // doesDraw
    
    /** Checks if a certain pen dimension is (1,1)
     *  in this case, direct awt graphic methods can be used
     * @param pen the pen to check 
     * @return <code>true</code> if dimension is (1,1)
     */

    public static final boolean isThin(Dimension pen) {
	if ((pen.width==1) & (pen.height==1)) return true ;
	else return false ;
    } // isThin
    
    /** Checks if a certain dimension is (0,0)
     * @param the delta to check
     * @return <code>true</code> if the dimension is (0,0)
     */ 

    public static final boolean isNoDelta(Dimension delta) {
	return ((delta.width==0) & (delta.height==0)) ;
    } // isNoDelta
    
    /** Builds a polygon represented by a line with a given width 
     * @param a the starting point of the line
     * @param b the ending point of the line
     * @param pen the width of the line
     * @return the resulting polygon (closed)
     */ 

    public static final Polygon line2Poly(Point a, Point b, Dimension pen) {
	Polygon poly= new Polygon() ;
	Point start ;
	Point end ;
	if (a.x < b.x) 
	    { start=a ; end = b;} // if
	else
	    { start = b ; end = a ;} // else
	if (start.y < end.y) { // \ line 
	    poly.addPoint(start.x,start.y+pen.height);
	    poly.addPoint(start.x,start.y);
	    poly.addPoint(start.x + pen.width,start.y) ;
	    poly.addPoint(end.x+pen.width,end.y);
	    poly.addPoint(end.x+pen.width,end.y+pen.height);
	    poly.addPoint(end.x,end.y+pen.height);
	} else { // / Line
	    poly.addPoint(start.x+pen.width,start.y+pen.height) ;
	    poly.addPoint(start.x,start.y+pen.height) ;
	    poly.addPoint(start.x,start.y) ;
	    poly.addPoint(end.x,end.y);
	    poly.addPoint(end.x+pen.width,end.y);
	    poly.addPoint(end.x+pen.width,end.y+pen.height);
	} // / Line
	return(poly);
    } // Line2Poly
    
    /** Builds a polygon from a rectangle frame 
     * @param rect the rectangle
     * @param pen the width of the frame
     * @return Polygon
     */

    public static final  Polygon rectFrame2Poly(Rectangle rect, Dimension pen) {
	Polygon poly= new Polygon() ;
	poly.addPoint(rect.x,rect.y+pen.height) ;
	poly.addPoint(rect.x,rect.y);
	poly.addPoint(rect.x+rect.width,rect.y);
	poly.addPoint(rect.x+rect.width,rect.y+rect.height);
	poly.addPoint(rect.x,rect.y+rect.height);
	poly.addPoint(rect.x,rect.y+pen.height); // inner loop
	poly.addPoint(rect.x+pen.width,rect.y+pen.height);
	poly.addPoint(rect.x+pen.width,rect.y+rect.height-pen.height);
	poly.addPoint(rect.x+rect.width-pen.width,rect.y+rect.height-pen.height);
	poly.addPoint(rect.x+rect.width-pen.width,rect.y+pen.height);
	poly.addPoint(rect.x,rect.y+pen.height);
	return(poly);
    } // rectFrame2Poly
} // QDUtils

/** This Exception is thrown when an unknown color is required
 * @QDUtils#int2Color
 */ 

class QDUnknownColor extends QDException
{
    protected int color ;
    public QDUnknownColor(int col)
	{ this.color = col ; } // QDUnknownColor
    public String toString()
	{return("Unknow Color "+color);}
} //QDUnknownColor
