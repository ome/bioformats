//                              -*- Mode: Java -*- 
// QDRoundRectOpCode.java --- 
// Author          : Matthias Wiesmann
// Created On      : Mon Jul 12 16:52:57 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 15:04:49 2000
// Update Count    : 5
// Status          : Unknown, Use with caution!
// 
package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;

/** This class implements the round rectangle handling opcode
 *  @author Matthias Wiesmann
 *  @version 1.1
 */ 

public class QDRoundRectOpCode extends QDRectOpCode {	
    public QDRoundRectOpCode(short verb) {
	super(verb);
    } // QDRoundRectOpCode
    public void	execute(QDPort port ) throws QDException {
	port.stdRRect(verb,bounds);
    } // execute 

    public String toString() {
	return super.toString() + "(oval)" ;
    } // toString
	
} // QDRoundRectOpCode

