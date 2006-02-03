//                              -*- Mode: Java -*- 
// QDOriginOP.java --- 
// Author          : Matthias Wiesmann
// Created On      : Tue Nov 21 15:06:48 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Thu Nov 30 15:53:34 2000
// Update Count    : 3
// Status          : Unknown, Use with caution!
// 

package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import java.awt.*;
import ch.epfl.lse.jqd.io.QDInputStream;

/** Quickdraw Origin OpCodes 
*/

public class QDOriginOP implements QDOpCode {
    Dimension theOrigin ;
    
    public int 	read(QDInputStream theStream) 
	throws java.io.IOException {
	theOrigin = theStream.readDimension();
	return(4) ;
    }// read
	
    public void	execute(QDPort thePort) {
	thePort.setOrigin(theOrigin);
    } // execute 
		
    public String toString() {
	return("Origin "+theOrigin);
    } // toString 
} // QDOriginOP
