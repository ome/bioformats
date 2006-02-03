//                              -*- Mode: Java -*- 
// QDArcOpCode.java --- 
// Author          : Matthias Wiesmann
// Created On      : Mon Jul 12 16:58:08 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Thu Nov 30 15:33:21 2000
// Update Count    : 6
// Status          : OK
// 

package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.io.*;

import java.io.IOException;

/** This opcode draws an arc 
 * @version 1.1 
 * @author Matthias Wiesmann
 */ 

public class QDArcOpCode extends QDShapeOpCode implements QDOpCode 
{
    int startAngle ;
    int angle ;
    
    public QDArcOpCode(short verb) { 
	super(verb);
    } // QDPolyOpCode
    
    public int read(QDInputStream stream) 
	throws IOException {
	bounds = stream.readRect();
	startAngle=stream.readShort();
	angle=stream.readShort();
	return(12);
    }// read
	
    public void	execute(QDPort port) throws QDException {
	port.stdArc(verb,bounds,startAngle+90,angle);
    } // execute 

    public String toString() {
	return "Arc "+"\t"+super.toString()+
	    "\t"+bounds+" start"+startAngle+" angle "+angle;
    } // toString

} // QDArcOpCode
