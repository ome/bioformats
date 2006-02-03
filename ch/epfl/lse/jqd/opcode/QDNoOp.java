//                              -*- Mode: Java -*- 
// QDNoOp.java --- 
// Author          : Matthias Wiesmann
// Created On      : Fri Jul  2 11:53:12 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 13:46:51 2000
// Update Count    : 6
// Status          : OK
// 

package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import java.io.IOException;
import ch.epfl.lse.jqd.io.QDInputStream;

/** This class represents a <code>NoOp</code> opcode,
 *  that is an opcode that does nothing.
 *  (astonishing eh!). 
 *  They are manily used in QucikDraw for padding purpose,
 *  as so may be optimised out.<br>
 *  Opcodes doing nothing (for instance when not implemented) 
 *  can inherit from this class, so they can be optimised out.
 *  @author Matthias Wiesmann
 *  @version 1.1
 */ 

public class QDNoOp implements QDOpCode, QDDiscard
{
    /** reads nothing - this opcode has no parameter 
     *  @exception QDException never thrown by this class, 
     *  subclasses may use it to signal QuickDraw parsing problems
     *  @exception IOException never throw by this class, 
     *  subclasses may use it to signal I/O problems
     *  @return the number of bytes read, in this case 0
     */ 
    public int read(QDInputStream theStream) 
	throws IOException, QDException {
	return 0;
    } // read 

    /** does nothing
     *  @exception QDException never thrown by this class, 
     *  subclasses may use it to signal QuickDraw execution problems
     *  @param thePort the port to execute into
     */ 

    public void execute(QDPort  thePort) 
	throws QDException {
    } // execute

    public String toString() {
	return "No Operation";
    } // toString

    /** Method of the <code>QDDiscard</code> interface.
     *  @return <code>true</code> NoOps can always be discarded.
     *  @see QDDiscard
     */ 

    public boolean discard() {
	return true ;
    } // discard
} // NoOp
