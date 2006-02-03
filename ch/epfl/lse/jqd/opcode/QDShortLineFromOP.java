//                              -*- Mode: Java -*- 
// QDShortLineFromOP.java --- 
// Author          : Matthias Wiesmann
// Created On      : Tue Nov 21 14:48:31 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 10:59:30 2000
// Update Count    : 6
// Status          : Unknown, Use with caution!
// 


package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.utils.*;
import java.awt.*;

import ch.epfl.lse.jqd.io.QDInputStream;

public class QDShortLineFromOP implements QDOpCode {
    Dimension delta ;
    public int 	read(QDInputStream theStream) 
	throws java.io.IOException {
	delta = theStream.readShortDimension();
	return(QDInputStream.SHORT_DIMENSION_SIZE);
    }// read
    public void	execute(QDPort thePort) throws QDException {
	final Point end = QDUtils.addDimension(thePort.getPnLoc(),delta);
	thePort.stdLine(end);
    } // execute 
    public String toString() {return("ShortLineFrom "+delta);}
} // QDpnSizeOP

