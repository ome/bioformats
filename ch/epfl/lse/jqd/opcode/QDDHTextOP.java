//                              -*- Mode: Java -*- 
// QDDHTextOP.java --- 
// Author          : Matthias Wiesmann
// Created On      : Tue Nov 21 15:14:51 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 12:28:57 2000
// Update Count    : 4
// Status          : Unknown, Use with caution!
// 

package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;

import ch.epfl.lse.jqd.io.QDInputStream;

/** This opcode draws text with only a horizontal delta. 
 *  @author Matthias Wiesmann
 *  @version 1.0 revised
 */ 

public class QDDHTextOP extends QDDHDVTextOP{
    public int 	read(QDInputStream theStream) 
	throws java.io.IOException, QDException {
	delta = theStream.readDH();
	text =  theStream.readString();
	return(QDInputStream.DH_SIZE+text.length());
    } // read
    
    protected String opName() {
	return("DH");
    } // opName
} // QDHeaderOP


