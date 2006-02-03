//                              -*- Mode: Java -*- 
// QDZoomer.java --- 
// Author          : Matthias Wiesmann
// Created On      : Thu Dec 16 16:48:39 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Thu Dec 16 16:49:17 1999
// Update Count    : 2
// Status          : Renamed
// 

package ch.epfl.lse.jqd.managers;

import ch.epfl.lse.jqd.utils.*;
import java.awt.*;

/** This class handles all the zooming and transformation operations.
 * Immuable object - you have to build a new one for each transformation.
 * This class is used heavily, as all drawing call pass thru it. 
 * Primary candidate for optimization. 
 * @author Matthias Wiesmann
 * @version 1.1
 */

public class QDZoomer
{
    protected final Point origin ;
    protected final double fx ;
    protected final double fy ;
    
    /** Default Consrtuctor.<br>
     * Sets up a default zoom situation. 
     * <UL><LI>origin=[0,0]<LI>zoom=1.5</UL>
     */
    
    public QDZoomer() {
	    origin = new Point(0,0);
	    fx = 1.5 ;
	    fy = 1.5 ;
	} // QDZoomer
		
    /** Constructor fit a rectangle inside another
     *  @dest the rectangle to fit
     *  @bounds the rectangle to fit in 
     */ 
	
    public QDZoomer(Rectangle dest, Rectangle bounds) {
	    origin = new Point(0,0);
	    double d = minFactor(dest,bounds);
	    fx=d ;
	    fy=d ;
	    // System.err.println(this);
	} // QDZoomer

    /** Calculates the minimum factor two rectangles
     *  @param dest the rectangle to fit
     *  @param bounds the rectangle to fit into
     *  @return the minimum zoom factor needed 
     */ 
       
    protected static double minFactor(Rectangle dest, Rectangle bounds) {
	double x = (double) dest.width / (double) (bounds.x + bounds.width) ;
	double y = (double) dest.height / (double) (bounds.y +bounds.height) ;
	return Math.min(x,y);
    } // minFactor
    
    /** Returns informations string
     * @return information string
     */ 

    public String toString() {
	return ("QD Zoomer {xzoom="+fx+" yzoom="+fy+" origin="+origin+"}");
    } //toString
    
    /** Resets the origin */
    
    public void zeroOrigin() {
	origin.x = 0 ;
	origin.y = 0 ;
    } // zeroOrigin
    
    /** Translate the origin
     *  @param d the dimension to translate with
     */
		
    public void translate(Dimension d) {
	origin.x -= d.width ;
	origin.y -= d.height ;
    } // translate
    
    /** Transforms a point
     *  @param p the point to transform
     *  @return the transformed point
     */ 
	
    public final Point transform(Point p) {
	final int x = (int) ((p.x + origin.x)*fx );
	final int y = (int) ((p.y + origin.y)*fy );
	return new Point(x,y);
    } // transform
    
    /** Transforms a coordinate couple
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the transformed point
     */ 
		
    public Point transformPoint(int x, int y) {
	return transform (new Point (x,y));
    } //transform
    
    /** Transforms a dimension (no translation)
     * @param d the dimension to transform
     * @return the transformed dimension
     */ 
		
    public Dimension transform(Dimension d) {
	int w = (int) (d.width * fx) ;
	int h = (int) (d.height * fy) ;
	if (h==0) h=1 ;
	if (w==0) w=1 ;
	return new Dimension (w,h);
	} // transform
		
    /** Transforms a rectangle 
     * @param r the rectangle to transform
     * @return the transformed rectangle
     */ 
	
    public Rectangle transform(Rectangle r) {
	final Point p = transform(QDUtils.rect2Point(r));
	final Dimension d = transform(QDUtils.rect2Dim(r));
	final Rectangle dest  =  new Rectangle(p,d);
	return dest ;
    } // transform
    
    /** Transforms a text size
     * @param s the size in points
     * @return the transformed size
     */ 
		
    public int transformTextSize(int s) {
	int result = (int)(s*fy);
	return result ;
    } //transformTextSize
    
    /** Transforms a polyon
     * @param poly the polygon to transform
     * @return the transformed polygon
     */
		
    public Polygon transform(Polygon poly) {
	Polygon dest = new Polygon();
	for(int i=0 ; i<poly.npoints ; i++) {
	    final int x = poly.xpoints[i] ;
	    final int y = poly.ypoints[i] ;
	    Point p = transformPoint(x,y) ;
	    dest.addPoint(p.x,p.y);
	} // for
	    return dest ;
    } // transform	
} // QDZoomer
