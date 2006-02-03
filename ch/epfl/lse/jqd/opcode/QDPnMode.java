//                              -*- Mode: Java -*- 
// QDPnMode.java --- 
// Author          : Matthias Wiesmann
// Created On      : Tue Nov 21 15:00:03 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 10:38:19 2000
// Update Count    : 2
// Status          : Unknown, Use with caution!
// 


package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.io.QDInputStream;

/**
Quickdraw Pen OpCodes 
*/

public class QDPnMode implements QDOpCode  {
    protected  short mode ;
    
    public int read(QDInputStream theStream) 
	throws java.io.IOException {
	mode = theStream.readShort();
	return(2);
    }// read
    
    public void	execute(QDPort thePort) {
	thePort.pnMode = mode ;
	thePort.colorOperation();
    } // execute 
    
    public String toString() {
	return("Pen Mode "+QDModes.toString(mode));
    } 
} // QDPnPatOP
