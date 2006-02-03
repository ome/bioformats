//                              -*- Mode: Java -*- 
// QDDirectBitsRectOP.java --- 
// Author          : Matthias Wiesmann
// Created On      : Tue Nov 21 15:14:19 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 12:11:39 2000
// Update Count    : 4
// Status          : Unknown, Use with caution!
// 

/*
Quickdraw QDBitsRectOP OpCodes 
*/
package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.image.*;

import ch.epfl.lse.jqd.io.QDInputStream;

/** This opcodes does a direct bitmap copy 
 *  @author Matthias Wiesmann
 *  @version 1.0 revised
 */ 

public class QDDirectBitsRectOP extends QDBitsRectOP {
    public int read(QDInputStream theStream) 
	throws java.io.IOException, QDException {
	bMap = new QDDirectPixMap();
	return bMap.read(theStream);
    }// read

    public void	execute(QDPort thePort) 
	throws QDException {
	thePort.stdBits(bMap);
    } // execute 

    public String toString() {
	return("Direct Bit Rect "+bMap);
    } // toString
} // QDDirectBitsRectOP

