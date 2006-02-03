//                              -*- Mode: Java -*- 
// QDShortLineOP.java --- 
// Author          : Matthias Wiesmann
// Created On      : Tue Nov 21 14:48:07 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 10:58:25 2000
// Update Count    : 5
// Status          : Renamed
// 


package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.utils.*;
import java.awt.*;

import ch.epfl.lse.jqd.io.QDInputStream;

/** Draws a short line 
 * (the parameters are short not the line)
 * @author Matthias Wiesmann
 * @version 1.0 revised
 */ 

public class QDShortLineOP implements QDOpCode {
    protected Point penLoc ;
    protected Dimension delta ;
    public int 	read(QDInputStream theStream) 
	throws java.io.IOException {
	penLoc = theStream.readPoint();
	delta = theStream.readShortDimension();
	return(QDInputStream.POINT_SIZE + QDInputStream.SHORT_DIMENSION_SIZE);
    }// read
    public void	execute(QDPort thePort) throws QDException {
	thePort.stdPoint(penLoc);
	Point end = QDUtils.addDimension(penLoc,delta);
	thePort.stdLine(end);
    } // execute 
    public String toString() {return("Short Line "+penLoc+" -"+delta);}
} // QDpnSizeOP
