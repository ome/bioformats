//                              -*- Mode: Java -*- 
// QDGraphUtils.java --- 
// Author          : Matthias Wiesmann
// Created On      : Thu Dec 16 16:38:31 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Thu Dec 16 16:39:01 1999
// Update Count    : 1
// Status          : Renamed
// 

package ch.epfl.lse.jqd.utils;

import java.awt.*;

/** this class holds differents drawing utilities 
 * mostly methods to draw shape with a pen size.
 * This is in fact <em>glue</em> code for compatibility with AWT 1.1.
 * @version 1.1
 * @author Matthias Wiesmann
 */
	
public final class QDGraphUtils 
{
    protected QDGraphUtils() {} 

    /** Draws a line with a certain pen width
     * @param g the graphics to draw into
     * @param a the starting point
     * @param b the ending point
     * @param d the size of the pen
     * @see QDUtils#line2Poly
     */ 
    public static void drawLine(Graphics g, Point a, Point b, Dimension d) {
	if (QDUtils.isThin(d)) g.drawLine(a.x,a.y,b.x,b.y) ;
	else {
	    Polygon poly = QDUtils.line2Poly(a,b,d);
	    g.fillPolygon(poly);
	} // else 
    } // drawLine
    
    /** Frames a rectangle with a certain pen width
     *  @param g the graphics to draw into
     *  @param r the rectangle to frame
     *  @param d the size of the pen
     *  @see QDUtils#rectFrame2Poly
     */
    
    public static void frameRect(Graphics g, Rectangle r, Dimension d){
	if (QDUtils.isThin(d)) g.drawRect(r.x,r.y,r.width,r.height) ;
	else 	{
	    Polygon poly= QDUtils.rectFrame2Poly(r,d);
	    g.fillPolygon(poly);
	} // else
    } // frameRect
    
    /** Frames a polygon with a certain pen width
     * <br><strong>Warning</strong>: This is done using 
     * mutliples lines - the result in invert mode 
     * is not stable
     * @param g the graphics to draw into
     * @param p the polygon to frame
     * @param d the size of the pen
     */

    public static void framePoly(Graphics g,Polygon p, Dimension d) {
	Point a = new Point(p.xpoints[0],p.ypoints[0]);
	for (int i=1;i<p.npoints;i++) {
	    Point b= new Point(p.xpoints[i],p.ypoints[i]);
	    drawLine(g,a,b,d);
	    a=b ;
	}// for
    } // framePoly
    
    /** Frames an oval with a certain pen width
     *  <br><strong>Warning</strong>: this is done drawing multiple
     *  steps, it very approximate.
     * @param g the graphics to draw into
     * @param r the bounding box of the oval
     * @param d the size of the pen
     */
    
    public static void frameOval(Graphics g,Rectangle r, Dimension d) {
	if (QDUtils.isThin(d)) g.drawOval(r.x,r.y,r.width,r.height) ;
	else {
	    int steps = Math.max(d.height,d.width) ;
	    for(int i=0;i<steps;i++) {
		int dx = (i*d.width) / steps ;
		int dy = (i*d.height) / steps ;
		int x = r.x + dx ;
		int y = r.y + dy ;
		int w = r.width - (2 * dx) ;
		int h = r.height - (2 * dy) ;
		g.drawOval(x,y,w,h) ;
	    } // for
	} // else
    } // frameOval
    
    /** Frames an round rectangle with a certain pen width
     *  <br><strong>Warning</strong>: this is done drawing multiple
     *  steps, it very approximate.
     * @param g the graphics to draw into
     * @param r the rectangle
     * @param a the rounding dimension
     * @param d the size of the pen
     */

    public static void frameRRect(Graphics g,Rectangle r,Dimension a, Dimension d) {
	if (QDUtils.isThin(d)) 
	    g.drawRoundRect(r.x,r.y,r.width,r.height,a.width,a.height) ;
	else {
	    int steps = Math.max(d.height,d.width) ;
	    for(int i=0;i<steps;i++) {
		int dx = (i*d.width) / steps ;
		int dy = (i*d.height) / steps ;
		int x = r.x + dx ;
		int y = r.y + dy ;
		int w = r.width - (2 * dx) ;
		int h = r.height - (2 * dy) ;
		g.drawRoundRect(x,y,w,h,a.width,a.height) ;
	    } // for
	} // else
    } // frameRRect
    
    /** Frames an arc with a certain pen width
     *  <br><strong>Warning</strong>: this is done drawing multiple
     *  steps, it very approximate.
     * @param g the graphics to draw into
     * @param r the bounding rectangle of the arc 
     * @param start the starting angle
     * @param end the ending angle
     * @param d the size of the pen
     */

    public static void frameArc(Graphics g,Rectangle r,Dimension d,int start, int angle) {
	if (QDUtils.isThin(d)) g.drawArc(r.x,r.y,r.width,r.height,start,angle) ;
	else {
	    int steps = Math.max(d.height,d.width) ;
	    for(int i=0;i<steps;i++) {
		int dx = (i*d.width) / steps ;
		int dy = (i*d.height) / steps ;
		int x = r.x + dx ;
		int y = r.y + dy ;
		int w = r.width - (2 * dx) ;
		int h = r.height - (2 * dy) ;
		g.drawArc(x,y,w,h,start,angle) ;
	    } // for
	} // else
	} // frameArc
    
    /** Calculates the bounding box of some text
     *  @param g the graphics
     *  @param text the text
     *  @param p the point the text is drawn
     *  @return the bounding box
     */ 

    public static Rectangle textRect(Graphics g, Point p, String text) {
	final FontMetrics metrics = g.getFontMetrics();
	final int width = metrics.stringWidth(text);
	final int height = metrics.getHeight();
	final Dimension dim = new Dimension(width,height);
	final int ascent = metrics.getMaxAscent();
	final Point upLeft = new Point(p.x,p.y-ascent);
	return new Rectangle(upLeft,dim);
	} // textRect

    /** Marks a box as invalid 
     * (i.e. a rendering error occured inside this box)
     * @param g the graphics to draw into
     * @param r the rectangle to invalidate
     */ 

    public static void invalBox(Graphics g, Rectangle r) {
	final Color c = g.getColor();
	g.setColor(Color.red);
	g.drawRect(r.x,r.y,r.width,r.height);
	int y = r.y ;
	while(y<r.y+r.height) {
	    g.drawLine(r.x,y,r.x+r.width,y);
	    y+=10 ;
	} // while
	g.setColor(c);
    } // fuckBox
    
} // QDGraphUtils

