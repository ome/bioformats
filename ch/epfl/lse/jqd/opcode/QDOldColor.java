//                              -*- Mode: Java -*- 
// QDOldColor.java --- 
// Author          : Matthias Wiesmann
// Created On      : Mon Jul 12 17:37:58 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 15:03:52 2000
// Update Count    : 7
// Status          : OK
// 

package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.io.QDInputStream;

/** Old (QuickDraw 1) Color selection method 
 *  @see QDColorOp
 *  @version 1.1
 *  @author Matthias Wiesmann
 */ 

public class QDOldColor extends QDColorOP
{
    public QDOldColor(short type) {
	super(type);
    } // QD1Color
    
    /** @exception java.io.IOException I/O problem 
     *  @exception QDException unrecognized color code
     */

    public int 	read(QDInputStream theStream) 
	throws java.io.IOException, QDException {
	theColor = theStream.readOldColor();
	return(QDInputStream.OLD_COLOR_SIZE);
    } // read
    
} // QDOldColor









