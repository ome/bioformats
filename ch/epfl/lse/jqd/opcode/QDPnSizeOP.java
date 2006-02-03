//                              -*- Mode: Java -*- 
// QDpnSizeOP.java --- 
// Author          : Matthias Wiesmann
// Created On      : Tue Nov 21 14:58:08 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 10:38:52 2000
// Update Count    : 4
// Status          : Unknown, Use with caution!
// 




package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.io.QDInputStream;
import ch.epfl.lse.jqd.utils.*;
import java.awt.*;

/** Quickdraw PenSize OpCodes 
*/

public class QDPnSizeOP implements QDOpCode {
    public Dimension pnSize ;
    
    public int 	read(QDInputStream theStream) 
	throws java.io.IOException{
	Point temp=theStream.readPoint();
	pnSize=QDUtils.point2Dim(temp);
	return(4);
    }// read
	
    public void	execute(QDPort thePort) {
	thePort.pnSize = this.pnSize ;
    } // execute 
    public String toString() {return("Pen "+pnSize);}
} // QDpnSizeOP
