//                              -*- Mode: Java -*- 
// QDLineFromOP.java --- 
// Author          : Matthias Wiesmann
// Created On      : Tue Nov 21 15:09:23 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Thu Nov 30 15:48:33 2000
// Update Count    : 3
// Status          : Unknown, Use with caution!
// 


package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import java.awt.*;
import ch.epfl.lse.jqd.io.QDInputStream;

/** Quickdraw LineFrom OpCode
*/

public class QDLineFromOP implements QDOpCode  {
    Point newLoc ;
    public int 	read(QDInputStream theStream) throws java.io.IOException {
	newLoc = theStream.readPoint();
	return(4);
    }// read
    
    public void	execute(QDPort thePort) throws QDException {
	thePort.stdLine(newLoc);
    } // execute 
    
    public String toString() {
	return("Line From"+newLoc);
    } // toString
} // QDpnSizeOP
