//                              -*- Mode: Java -*- 
// QDOvalOpCode.java --- 
// Author          : Matthias Wiesmann
// Created On      : Tue Nov 21 15:05:36 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Thu Nov 30 16:29:23 2000
// Update Count    : 3
// Status          : Unknown, Use with caution!
// 

package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import java.awt.*;

import ch.epfl.lse.jqd.io.QDInputStream;

public class QDOvalOpCode implements QDOpCode {
    Rectangle theRect ;
    final short theVerb ;
    public int 	read(QDInputStream theStream) 
	throws java.io.IOException {
	theRect = theStream.readRect();
	return(8) ;
    }// read
	
    public QDOvalOpCode(short verb) { 
	this.theVerb = verb ; 
    } // QDPolyOpCode
	
    public void	execute(QDPort thePort) throws QDException  {
	thePort.stdOval(theVerb,theRect);
    } // execute
    
    public String toString() {
	return(QDVerbs.toString(theVerb)+"\tOval\t"+theRect);
    } // toString 
} // QDRectOpCode
