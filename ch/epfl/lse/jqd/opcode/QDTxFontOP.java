//                              -*- Mode: Java -*- 
// QDTxFontOP.java --- 
// Author          : Matthias Wiesmann
// Created On      : Tue Nov 21 14:50:38 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 11:01:58 2000
// Update Count    : 2
// Status          : Unknown, Use with caution!
// 

package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.io.QDInputStream;

public class QDTxFontOP implements QDOpCode {
    protected short id ;
    public int 	read(QDInputStream theStream) 
	throws java.io.IOException {
	id=theStream.readShort() ;
	return(2);
    } // read
    public void	execute(QDPort thePort) {
	thePort.txFont = id ;
	thePort.txOperation();
    } // execute 
    public String toString() {
	return("Text Font id "+id);}
} // QDHeaderOP
