//                              -*- Mode: Java -*- 
// QDPixPatOp.java --- 
// Author          : Matthias Wiesmann
// Created On      : Tue Nov 21 15:02:02 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 15:04:13 2000
// Update Count    : 3
// Status          : Unknown, Use with caution!
// 

package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.image.QDPixPattern;
import ch.epfl.lse.jqd.io.QDInputStream;
import java.io.IOException;

/** This opcode describes a color pattern opcode.
 *  This class inherits most of it capacities from
 *  it's superclass <code>QDPatOp</code>.
 *  The main difference is that <code>thePattern</code>
 *  is an instance of <code>QDPixPattern</code>
 *  @author Matthias Wiesmann
 *  @version 1.1
 */ 


public class QDPixPatOp extends QDPatOP {

    public QDPixPatOp(int select) {
	super(select); 
    } // QDPixPatOp

    public int read(QDInputStream stream) 
	throws IOException, QDException {
	thePattern = new QDPixPattern();
	final int len= thePattern.read(stream);
	return len ;
    } // read


} // QDPixPatOp
