//                              -*- Mode: Java -*- 
// QDPatOP.java --- 
// Author          : Matthias Wiesmann
// Created On      : Fri Jul  9 11:33:33 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 10:34:44 2000
// Update Count    : 6
// Status          : OK
// 

package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.image.*;
import ch.epfl.lse.jqd.io.QDInputStream;

/** This opcode controls the patterns of the port
 *  @author Matthias Wiesmann
 *  @version 1.1 revised
 */


public class QDPatOP implements QDOpCode {
    QDPattern thePattern ;
    final int patternSelect ;
	
    public QDPatOP(int select) {
	this.patternSelect = select ;
    } // QDPatOP
    
    public int 	read(QDInputStream theStream) 
	throws java.io.IOException, QDException {
	thePattern = new QDPattern() ;
	return(thePattern.read(theStream));
    }// read
    
    public void	execute(QDPort thePort) {
	thePort.patterns[patternSelect]=thePattern ;
	thePort.colorOperation();
    } // execute 
    
    public String toString() {
	return QDPort.patString(patternSelect)+" pattern "+thePattern ;
    } // toString 
} // QDPnPatOP
