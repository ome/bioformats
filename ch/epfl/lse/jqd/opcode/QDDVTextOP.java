//                              -*- Mode: Java -*- 
// QDDVTextOP.java --- 
// Author          : Matthias Wiesmann
// Created On      : Tue Nov 21 15:13:46 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 12:13:57 2000
// Update Count    : 3
// Status          : Unknown, Use with caution!
// 

package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;

import ch.epfl.lse.jqd.io.QDInputStream;

/** This opcode draws text with a vertical delta
 *  @see QDDHTextOP
 *  @version 1.0 revised
 *  @author Matthias Wiesmann
 */ 

public class QDDVTextOP extends QDDHDVTextOP{
    public int read(QDInputStream theStream) 
	throws java.io.IOException, QDException {
	delta = theStream.readDV();
	text = theStream.readString();
	return(QDInputStream.DV_SIZE+text.length());
    } // read

    protected String opName() {
	return("DV");
    } // opName
    
} // QDHeaderOP
