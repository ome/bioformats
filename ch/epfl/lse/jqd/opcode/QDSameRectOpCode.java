//                              -*- Mode: Java -*- 
// QDSameRectOpCode.java --- 
// Author          : Matthias Wiesmann
// Created On      : Tue Nov 21 14:51:44 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 10:53:40 2000
// Update Count    : 4
// Status          : Renamed
// 


package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.io.QDInputStream;

/* 
Quickdraw Rectangle OpCodes Definition 
*/

public class QDSameRectOpCode implements QDOpCode {
    final short theVerb ;
    public int 	read(QDInputStream theStream) 
	throws java.io.IOException {
	return(0) ;
    }// read
    
    public QDSameRectOpCode(short verb) { 
	this.theVerb = verb ; 
    } // QDSameRectOpCode
    
    public void	execute(QDPort thePort) throws QDException {
	thePort.stdRect(theVerb,thePort.lastRect);
    } // execute 
    
    public String toString() {return(QDVerbs.toString(theVerb)+"same Rect");} 
} // QDSameRectOpCode
