//                              -*- Mode: Java -*- 
// QDSameArcOpCode.java --- 
// Author          : Matthias Wiesmann
// Created On      : Tue Nov 21 14:55:43 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 10:52:32 2000
// Update Count    : 5
// Status          : Unknown, Use with caution!
// 


package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.io.QDInputStream;

public class QDSameArcOpCode implements QDOpCode {
    int startAngle ;
    int angle ;
    final short theVerb ;

    public int 	read(QDInputStream theStream) throws java.io.IOException {	
	startAngle=theStream.readShort();
	angle=theStream.readShort();
	return(4);
    }// read

    public QDSameArcOpCode(short verb){
	this.theVerb = verb ; 
    } // QDPolyOpCode
	
    public void	execute(QDPort thePort) throws QDException {
	thePort.stdArc(theVerb,thePort.lastRect,startAngle+90,angle);
    } // execute 

    public String toString() {
	return("Arc "+"\t"+QDVerbs.toString(theVerb)+" start"+startAngle+" angle "+angle);
    } 
} // QDSameArcOpCode
