//                              -*- Mode: Java -*- 
// QDovSizeOP.java --- 
// Author          : Matthias Wiesmann
// Created On      : Tue Nov 21 15:04:55 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Thu Nov 30 16:30:44 2000
// Update Count    : 3
// Status          : Unknown, Use with caution!
// 

package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.utils.*;
import java.awt.*;

import ch.epfl.lse.jqd.io.QDInputStream;

public class QDOvSizeOP implements QDOpCode  {
    public Dimension ovSize ;
	public int read(QDInputStream theStream) 
	    throws java.io.IOException {
	    final Point temp= theStream.readPoint();
	    ovSize=QDUtils.point2Dim(temp);
	    return(4);
	}// read
    
    public void	execute(QDPort thePort) {
	thePort.ovalSize = this.ovSize ;
    } // execute 
    
    public String toString() {
	return("Oval "+ovSize);
    }
} // QDovSizeOP
