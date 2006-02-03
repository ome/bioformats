//                              -*- Mode: Java -*- 
// QDLongCommentOP.java --- 
// Author          : Matthias Wiesmann
// Created On      : Fri Jul  2 11:59:32 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Thu Nov 30 15:50:43 2000
// Update Count    : 4
// Status          : OK
// 

package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.io.QDInputStream;

/** This opcode defines a long comment 
 *  @author Matthias Wiesmann
 *  @version 1.0 revised 
 */

public class QDLongCommentOP extends QDShortCommentOP
{
    int size ;
    public int 	read(QDInputStream theStream) 
	throws java.io.IOException {
	final int s = super.read(theStream);
	size = theStream.readShort();
	theStream.skipBytes(size);
	return(s+2+size);
    } // read

    public String toString() {
	return "Long Comment size="+size+
	    "\t kind= "+toString(kind);
    }
} // QDLongComment

