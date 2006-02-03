//                              -*- Mode: Java -*- 
// QDHiliteOP.java --- 
// Author          : Matthias Wiesmann
// Created On      : Fri Jul  9 12:54:48 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Thu Nov 30 15:47:16 2000
// Update Count    : 5
// Status          : OK
// 

package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.io.QDInputStream;

/** This opcode marks that next operation is a highlight 
 *  operation.<br>
 *  Works more or less, because it depends on AWT invert
 *  operation, that are far from perfect.
 *  @author Matthias Wiesmann
 *  @version 1.0
 */ 

public class QDHiliteOP implements QDOpCode {
    public int read(QDInputStream stream) {
	return 0 ;
    } // read

    public void execute(QDPort thePort) throws QDException {
	thePort.colorOperation();
	thePort.stdColor(QDVerbs.HIGH_VERB);
    } // execute

    public String toString() {
	return "Hilight Operation" ;
    } // 

} // QDHiliteOP
