//                              -*- Mode: Java -*- 
// QDPattern.java --- 
// Author          : Matthias Wiesmann
// Created On      : Fri Jul  9 11:36:48 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Tue Nov 21 15:22:50 2000
// Update Count    : 3
// Status          : Unknown, Use with caution!
// 

package ch.epfl.lse.jqd.image;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.utils.*;

import java.awt.*;

import java.io.IOException;
import java.io.DataInputStream;

/** This class describes a QuickDraw pattern.<br>
 *  Patterns are not suppored as of Java 1.1
 *  therefor patterns are changed into equivalent colors.<br>
 *  For instance a Macintosh <code>gray</code> pattern with 
 *  A black foreground and white background will result in a 
 *  50% gray. 
 *  @author Matthias Wiesmann
 *  @version 1.0 revised
 */ 

public class QDPattern 
{
    /** standard Macintosh grayscale pattern (100%) */ 
    public static final QDPattern BLACK = new QDPattern(1) ;
    /** standard Macintosh grayscale pattern (0%) */ 
    public static final QDPattern WHITE = new QDPattern(0) ;
     /** standard Macintosh grayscale pattern (50%) */ 
    public static final QDPattern GRAY  = new QDPattern(0.5) ;
     /** standard Macintosh grayscale pattern (25%) */ 
    public static final QDPattern LT_GRAY = new QDPattern(0.25) ;
     /** standard Macintosh grayscale pattern (75%) */ 
    public static final QDPattern DK_GRAY = new QDPattern(0.75) ;
    
    /** opacity ratio for the pattern */ 
    protected double value ;
    
    /** builds a opaque pattern */ 
    public QDPattern() {
	this.value = 1.0 ;}

    /** builds a pattern with given opacity */ 
    public QDPattern(double v) {
	this.value = v ;}
    
    public int read(DataInputStream theStream) 
	throws IOException, QDException {
	int total= 0 ;
	for (int i=0; i<4; i++) {
	    int data=theStream.readUnsignedShort();
	    total+=QDBitUtils.countBits(data,16) ;
	} // for
	value= (double) total / (double) (8*8) ;
	return(8);
    } // read
    
    /** Builds a color from a pattern 
     *  @param front foreground color 
     *  @param back  background color 
     *  @return a Color that is a mix between foreground &amp; background color 
     *  the mix depends of the <code>value</code> field
     *  @see #field 
     */

    public Color patColor(Color front, Color back) {
	final int r = (int) (value*front.getRed() + (1-value)*back.getRed()) ;
	final int g = (int) (value*front.getGreen() + (1-value)*back.getGreen()) ;
	final int b = (int) (value*front.getBlue() + (1-value)*back.getBlue()) ;
	return new Color(r,g,b);
    } // patColor 
    
    public String toString() {
	return "Pattern - value ="+value;
    } // toString
} // QDPattern

