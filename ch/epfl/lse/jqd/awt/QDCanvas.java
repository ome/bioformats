//                              -*- Mode: Java -*- 
// QDCanvas.java --- 
// Author          : Matthias Wiesmann
// Created On      : Thu Dec 16 16:50:59 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Wed Dec  6 11:33:11 2000
// Update Count    : 2
// Status          : Renamed
// 

package ch.epfl.lse.jqd.awt;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Cursor;

import java.io.IOException; 

import ch.epfl.lse.jqd.basics.QDPicture;
import ch.epfl.lse.jqd.basics.QDPort;
import ch.epfl.lse.jqd.basics.QDException;

/** This class represents a QuickDraw Canvas.
 *  This component simply displays a QuickDraw picture.
 *  
 *  @author Matthias Wiesmann
 *  @version 2.0
 */

public class QDCanvas extends Canvas {
    /** The picture to be displayed */
    protected final QDPicture pict ;
    /** The port to display in */ 
    protected final QDPort port ;
    /** The cursor used to mark the fact that the picture is drawing */ 
    protected final Cursor processing = 
    Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
    /** The cursor used to mark the fact that the picture is idle */ 
    protected final Cursor idle = 
    Cursor.getDefaultCursor();
    
    /** Constructor 
     *  @param pict the picture to display 
     *  @exception IOException problem while reading the picture 
     *  @exception QDException problem while displaying the picture
     */ 
    public QDCanvas(QDPicture pict)                
	throws IOException, QDException {
	super();
	this.pict = pict ;
	this.port = new QDAwtPort(this); 
	Rectangle bounds = pict.getBounds();
	setBounds(bounds);
    } // QDCanvas
    
    /** Basic component method - draw the picture
     *  The code is not fault-tolerant at all. 
     *  @param g the AWT graphic object 
     */

    public void paint(Graphics g) {
	try {
	    port.setPortGraphics(g);
	    setCursor(processing);
	    pict.execute(port);
	    setCursor(idle);
	} catch(Exception e) { e.printStackTrace(); System.exit(0); }
    } // paint
    
    /** Finds if some text is situated in some point
     *  Checks if the point fills within the box of some
     *  Quickdraw text. If this is the case, said text is 
     *  returned. 
     *  @param p the point choosen
     *  @result the string associated to this point
     */ 

    public String getText(Point p) {
	return port.getLinkManager().getText(p);
    } // getText
    
} // QDCanvas

