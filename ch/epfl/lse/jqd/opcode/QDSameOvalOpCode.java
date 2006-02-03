//                              -*- Mode: Java -*- 
// QDSameOvalOpCode.java --- 
// Author          : Matthias Wiesmann
// Created On      : Tue Nov 21 14:54:41 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 10:53:06 2000
// Update Count    : 4
// Status          : Unknown, Use with caution!
// 


package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.io.QDInputStream;
import java.awt.*;

public class QDSameOvalOpCode implements QDOpCode  {
    Rectangle theRect ;
    final short theVerb ;
    public int 	read(QDInputStream theStream) 
	throws java.io.IOException {
	return(0) ;
    }// read
    
    public QDSameOvalOpCode(short verb) { 
	this.theVerb = verb ; 
    } // QDPolyOpCode
    
    public void	execute(QDPort thePort) throws QDException {
	thePort.stdOval(theVerb,thePort.lastRect);
    } // execute 
    
    public String toString() {
	return(QDVerbs.toString(theVerb)+"\tSame Oval\t"+theRect);
    } 
} // QDSameOvalOpCode
