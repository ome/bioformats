//                              -*- Mode: Java -*- 
// QDHeaderOP.java --- 
// Author          : Matthias Wiesmann
// Created On      : Tue Nov 21 15:10:52 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Thu Nov 30 15:46:35 2000
// Update Count    : 4
// Status          : Unknown, Use with caution!
// 

package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.io.QDInputStream;
import java.io.IOException;

/** QuickDraw Header opcode 
 *  the Pict 2 header information is simply skipped
 *  @author Matthias Wiesmann
 *  @version 1.0
 */ 

public class QDHeaderOP implements QDOpCode {
    protected static final int HEADER_LENGTH = 24; 
    public int 	read(QDInputStream theStream) 
	throws IOException {
	theStream.skipBytes(HEADER_LENGTH);
	return(HEADER_LENGTH);
    } // read
    public void	execute(QDPort thePort) {} // execute 
    public String toString() 
	{return("Header Opcode");}
} // QDHeaderOP
