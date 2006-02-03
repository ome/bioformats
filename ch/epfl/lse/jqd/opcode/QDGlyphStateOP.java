//                              -*- Mode: Java -*- 
// QDGlyphStateOP.java --- 
// Author          : Matthias Wiesmann
// Created On      : Fri Jul  2 11:59:46 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Thu Nov 30 15:45:45 2000
// Update Count    : 4
// Status          : OK
// 

package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.io.QDInputStream;

import java.io.IOException;

/** This opcode defines the glyph state.
 * <strong>Warning</strong>: not implemented, does nothing
 * @author Matthias Wiesmann
 * @version 1.0
 */ 

public class QDGlyphStateOP extends QDNoOp {
    protected int dataSize ; 
    public int 	read(QDInputStream theStream) 
	throws IOException {
	    dataSize = theStream.readShort();
	    theStream.skipBytes(dataSize);
	    return(2+dataSize);
	} // read

    public String toString() 
	{return("Glyph State "+dataSize);}
} // QDHeaderOP
