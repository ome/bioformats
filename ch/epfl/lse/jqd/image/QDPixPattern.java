//                              -*- Mode: Java -*- 
// QDPixPattern.java --- 
// Author          : Matthias Wiesmann
// Created On      : Fri Jul  9 11:37:39 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Tue Nov 21 15:24:32 2000
// Update Count    : 11
// Status          : OK
// 

package ch.epfl.lse.jqd.image;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.utils.*;

import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;

/** This class represents a color pattern.<br>
 *  True color pattern are not supported, 
 *  dither patterns are mapped back to their original
 *  RGB Color.
 *  @author Matthias Wiesmann
 *  @version 1.1
 */


public class QDPixPattern extends QDPattern {
    
    protected Color color ;
    protected boolean dither ;

    public QDPixPattern() {
    } // QDPixPattern

    public int read(DataInputStream stream) 
	throws IOException, QDException {
	final int isDither = stream.readShort();
	int length=2 ;
	dither= (isDither==2);
	length+=super.read(stream);
	if (dither) {
	    color = QDLoader.readColor(stream);
	    length+=4 ;
	} else {
	    throw new QDException("color patterns not supported");

	} // if / else
	return length ;
    } // read

    public Color getColor() {
	return color ;
    } // getColor

    public Color patColor(Color front, Color back) {
	return color ;
    } // patColor 

} // QDPixPattern


