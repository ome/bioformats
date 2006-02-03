//                              -*- Mode: Java -*- 
// QDPort.java --- 
// Author          : Matthias Wiesmann
// Created On      : Fri Jul  2 13:47:36 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Tue Dec  5 17:26:43 2000
// Update Count    : 27
// Status          : OK
// 

package ch.epfl.lse.jqd.basics;

import java.awt.*;
import ch.epfl.lse.jqd.managers.*;
import ch.epfl.lse.jqd.image.*;

/** This class implements a Quickdraw Port 
 * A picture Object must be played into a QuickDraw Port.
 * This object mimics the standart bottleneck procedure
 * of the Quickdraw Port.<br>
 * To change them, one must derive from this class and 
 * overload the appropriate functions
 * @see QDPicture
 * @author Matthias Wiesmann
 * @version 1.0 revised
 */ 

public abstract class QDPort 
{
    /** The pen pattern index */
    public static final int PEN_PAT = 0 ;
    /** The fill pattern index */
    public static final int FILL_PAT = 1 ;
    /** The background pattern index */
    public static final int BACK_PAT = 2 ;
    /** The port's rectangle */
    protected Rectangle portRect ;
    /** The clipping region */ 
    protected QDRegion 	clipRgn ;
    protected Rectangle clipRect ;
    /** The patterns of the port 
     * The following patterns can be set:
     * <dl compact>
     * <dt>0<dd>pen pattern
     * <dt>1<dd>fill pattern
     * <dt>2<dd>background pattern
     * </dl>
     * @see #PEN_PAT
     * @see #FILL_PAT
     * @see #BACK_PAT
     * @see #patString
     */
    public	QDPattern[]	patterns = new QDPattern[3] ;
    /** The pen location.<br>
     * This value should not be changed directly 
     * but instead be chanched thru the stdPoint method
     * @see #stdPoint
     */
    protected Point	pnLoc ;
    /** The pen size */
    public	Dimension pnSize ;
    /** The pen mode */
    public short pnMode ;
    /** Font Number (Mac ID) */
    public int  txFont ;
    /** Font Style	(Mac Codes) */
    public int  txFace ;
    /** Font Size (Point) */
    public int  txSize ;
    /** Font mode (Mac Modes) */
    public short txMode ;
    /** Text Location */
    public Point txLoc ;	
    /** Additionnal text spacing (unused) */
    protected  int spExtra ;	
    /** Foreground color */
    public Color fgColor ;
    /** Background color */
    public Color bgColor ;	
    /** Hilight color */
    public Color hlColor ;
    /** Last used rectangle, for last-rect opcodes */
    public Rectangle 	lastRect ;
    /** Oval size (frames) 
     * @see #stdRRect
     */
    public Dimension	ovalSize ;
   
     /** the zoom manager */
    protected QDZoomer zoom = null ;
    /** the font manager */
    protected QDFontManager fontMgr= null ;
    /** the link manager */
    protected QDLinkManager linkMgr = null ;

    public String toString() {
	return "QuickDraw Port ["+portRect+"]" ;
    } // toString

    public QDFontManager getFontManager() {
	return fontMgr; 
    } // getFontManager

    public QDZoomer getZoomManager() {
	return zoom;
    } // getZoomManager
    
    public QDLinkManager getLinkManager() {
	return linkMgr;
    } // getLinkManager
    
    /** Sets the bounding rectangle for the drawing port */ 
    public abstract void setPortRect(Rectangle r) throws QDException ;
    /** Sets the graphics object for drawing operations */ 
    public abstract void setPortGraphics(Graphics g) throws QDException ;
    
    /** information method
     * @return the string associated with a pattern index
     * @param pat the pattern code
     */
    public static String patString(int pat) {
	switch (pat){
	case PEN_PAT: return ("Pen");
	case FILL_PAT: return ("Fill");
	case BACK_PAT: return ("Background");
	} // switch
	return("Unknown Pattern");
    } // patString
    
    /** called after each text operation */
    public abstract void txOperation(); 
    
    /** called after each color operation */
    public abstract void colorOperation(); 
    
    /** Standart pen initialisation method.
     *  Corresponds to the <code>penNormal</code> call. 
     * <ul>
     * <li>sets the foreground to black
     * <li>sets the background to white
     * <li>sets all patterns to solid black
     * <li>sets the origin to 0,0
     * <li>sets the pen size to 1,1
     * </ul>
     */
    public void penNormal() {
	fgColor = Color.black ;
	bgColor = Color.white ;
	patterns[0] = QDPattern.BLACK ;
	patterns[1] = QDPattern.BLACK ;
	patterns[2] = QDPattern.BLACK ;
	pnLoc = new Point(0,0) ;
	pnSize = new Dimension(1,1);
	pnMode = QDModes.COPY ;
	colorOperation();
    } // penNormal
    
    /** Standart text initialisation method.
     * Called when setting up the text. 
     * <ul>
     * <li>sets the text position to 0,0
     * <li>sets the text size to 10
     * <li>sets the font style to plain
     * <li>sets the font type to Helvetica
     * </ul>
     */
    public void txtNormal() {
	txLoc = new Point(0,0);
	txSize=10;
	txFace = Font.PLAIN;
	txFont = QDFontManager.HELVETICA_ID ;
	txOperation();
	linkMgr.resetText();
    } // txtNormal
    
    /** This method is called to set the graphic point 
     * @param pt the new point position */
    
    public void stdPoint(Point pt) {
	pnLoc.x = pt.x ;
	pnLoc.y = pt.y ;
    } // setPoint
    
    /** gives the pen location 
     * @return the pen location
     */
    public Point getPnLoc() {
	return pnLoc ;
    }  // getPoint 
    
    /** This method is called when the picture origin must be changed 
     *	@param d the new origin of the port 
     */
		
    public void setOrigin(Dimension d) {
	zoom.translate(d);
    } // setOrigin
    
    /** Cliping method, 
     * <strong>Warning</strong>as of AWT 1.0 Clipping 
     * does not work properly: the clipping can only shrink. 
     * <br>This method only implements boolean clipping: 
     * either there is some drawing or not. 
     * @param region   the clipping region 
     */
	    
    public abstract void clip(QDRegion region) throws QDException; 
    
    /** Color bottleneck procedure.<br>
     * This method is called by the others bottleneck procedures
     * to set the graphics fore and background colors and paint mode.
     * Does nothing if the flag <code>colorDirty</code> is not set. 
     * @param verb the Quickdraw Verb<UL><LI>frame<LI>paint<LI>fill<LI>txt</UL>
     * @see #colorDirty
     */
		
    public abstract void stdColor(int verb) throws QDException ;
    
    /** Line bottleneck procedure.<br>
     * <cite>Inside Macintosh</cite>: Imaging with Quickdraw p 3-132<br>
     * This method is called for all line drawing operations 
     * it should call the <code>stdColor</code> and 
     * <code>stdPoint</code> methods 
     * @param newPt the destination point of the line
     * @see #stdColor
     * @see #stdPoint
     */

    public abstract void stdLine(Point newPt) throws QDException ;
		
    /** Rectangle  bottleneck procedure<br>
     * <cite>Inside Macintosh</cite>: Imaging with Quickdraw p 3-132.<br>
     * This method is called for all Rectangle drawing operations 
     * it should call the <code>stdColor</code> method 
     * @param verb the Quickdraw Verb<UL><LI>frame<LI>paint<LI>fill<LI>txt</UL>
     * @param r the Rectangle to handle
     * @see #stdColor
     * @see #lastRect
     */ 

    public abstract void stdRect(int verb, Rectangle r) throws QDException ;
		
    /** Polygon bottleneck procedure<br>
     * <cite>Inside Macintosh</cite>: Imaging with Quickdraw p 3-135.<br>
     * This method is called for all polygon drawing operations 
     * it should call the <code>stdColor</code> method 
     * @param verb the Quickdraw Verb<UL><LI>frame<LI>paint<LI>fill<LI>txt</UL>
     * @param p 	the Polygon to handle
     * @see #stdColor
     */

    public abstract void stdPoly(int verb, Polygon p) throws QDException ;

    /** Region bottleneck procedure 
     * (Inside Macintosh: Imaging with Quickdraw p 3-135)
     * this method is called for all region drawing operations 
     * it should call the stdC olor method
     * <br><strong>Warning</strong>: region support is limited
     * @param verb the Quickdraw Verb<UL><LI>frame<LI>paint<LI>fill<LI>txt</UL>
     * @param rgn 	the Region to handle 
     * @see #stdColor
     */

    public abstract void stdRgn(int verb, QDRegion rgn) throws QDException;   

    /** Oval bottleneck procedure 
     * (Inside Macintosh: Imaging with Quickdraw p 3-133)
     * this method is called for all oval drawing operations 
     * it should call the stdColor method 
     * @param verb the Quickdraw Verb<UL><LI>frame<LI>paint<LI>fill<LI>txt</UL>
     * @param r the Rectangle to handle
     * @see #stdColor
     * @see #lastRect
     */

    public abstract void stdOval(int verb, Rectangle r) throws QDException;

    /** Round rect bottleneck procedure <br>
     * (Inside Macintosh: Imaging with Quickdraw p 3-133).<br>
     * This method is called for all round rect drawing operations 
     * it should call the <code>stdColor</code> method 
     * @param verb the Quickdraw Verb<UL><LI>frame<LI>paint<LI>fill<LI>txt</UL>
     * @param r 	the Rectangle to handle
     * @see #stdColor	
     * @see #lastRect
     */
    public abstract void stdRRect(int verb, Rectangle r) throws QDException;
    
    /** Arc bottleneck procedure<br>
     * (Inside Macintosh: Imaging with Quickdraw p 3-134).<br>
     * This method is called for all arc drawing operations 
     * it should call the <code>stdColor</code> method. 
     * @param verb the Quickdraw Verb<UL><LI>frame<LI>paint<LI>fill<LI>txt</UL>
     * @param r the Arc's bounding box
     * @param startAngle the starting angle
     * @param angle the angle
     * @see #stdColor 
     * @see #lastRect
     */
		
    public abstract void stdArc(int verb, Rectangle r,int startAngle, int angle) throws QDException;
    
    /** Text bottleneck procedure<br>
     * (Inside Macintosh: Text ???).<br>
     * This method is called for all text drawing operations 
     * it should call the <code>stdColor</code> method.
     * <br>The text are also added to the link manager.
     * @param text the text string
     * @exception QDException QuickDraw Problem
     * @see #stdColor
     * @see #txDirty
     * @see #linkMgr
     */
		
    public abstract void stdText(String text) throws QDException ; 
    
    /** BitMap bottleneck procedure<br>
     * (Inside Macintosh: Imaging with Quickdraw p 3-136).<br>
     * This method is called for all bitmap drawing operations.
     * <br>
     * Seems there is a problem here with Java 1.2 
     * @param bMap the bitMap to draw
     * @exception QDException bitMap problem
     */
		 
    public abstract  void stdBits(QDBitMap bMap) throws QDException ;
	
    /** Comment bottleneck procedure<br>
     * (Inside Macintosh: Imaging with Quickdraw p 3-137).<br>
     * This method is called for all comment processing.
     * @param comment the comment to be handled 
     * @exception QDException QuickDraw problem
     */
		
    public abstract void stdComment(QDComment comment) throws QDException ;
} // class QDPort











