//                              -*- Mode: Java -*- 
// QDRectOpCode.java --- 
// Author          : Matthias Wiesmann
// Created On      : Fri Jul  9 12:01:54 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 11:49:56 2000
// Update Count    : 14
// Status          : OK
// 


package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.io.QDInputStream;
import java.io.IOException;

/** This class implement all Rectangle handling opcodes 
 *  @author Matthias Wiesmann
 *  @version 1.1
 */

public class QDRectOpCode extends QDShapeOpCode implements QDOpCode {
    public QDRectOpCode(short verb) {
	super(verb);
    } // QDRectOpCode

    public int read(QDInputStream stream) 
	throws IOException {
	bounds = stream.readRect();
	return(QDInputStream.RECT_SIZE) ;
    }// read
    
    public void	execute(QDPort port) throws QDException {
	port.stdRect(verb,bounds);
    } // execute
 
    public String toString() {
	return super.toString() + " "+ bounds ;
    }// toString 
    
} // QDRectOpCode
