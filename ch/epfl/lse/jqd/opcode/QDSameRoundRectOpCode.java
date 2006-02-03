//                              -*- Mode: Java -*- 
// QDSameRoundRectOpCode.java --- 
// Author          : Matthias Wiesmann
// Created On      : Tue Nov 21 14:53:12 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 10:54:21 2000
// Update Count    : 3
// Status          : Unknown, Use with caution!
// 


package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.io.QDInputStream;

/* 
Quickdraw Rectangle OpCodes Definition 
*/

public class QDSameRoundRectOpCode implements QDOpCode {
    short theVerb ;
    public int read(QDInputStream theStream) 
	throws java.io.IOException{
	return(0) ;
    }// read
    
    public QDSameRoundRectOpCode(short verb) { 
	this.theVerb = verb ; 
    } // QDPolyOpCode
    
    public void	execute(QDPort thePort) throws QDException {
	thePort.stdRRect(theVerb,thePort.lastRect);
    } // execute 

    public String toString() {
	return(QDVerbs.toString(theVerb)+"\tSame Oval");
    } // toString 
} // QDSameOvalOpCode
