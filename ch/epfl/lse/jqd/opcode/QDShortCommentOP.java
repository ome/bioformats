//                              -*- Mode: Java -*- 
// QDShortCommentOP.java --- 
// Author          : Matthias Wiesmann
// Created On      : Tue Nov 21 14:49:36 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Thu Nov 30 15:50:04 2000
// Update Count    : 3
// Status          : Unknown, Use with caution!
// 

package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.io.QDInputStream;

/** This opcode is a short comment 
 *  Mot of the comment code is in the <code>QDComment</code> class.
 *  @see java.basics.QDComment
 *  @author Matthias Wiesmann
 *  @version 1.1
 */ 

public class QDShortCommentOP extends QDComment implements QDOpCode, QDDiscard  {
    public int 	read(QDInputStream theStream) 
	throws java.io.IOException {
	kind=theStream.readShort();
	return(2);
    } // read
    
    public String toString() {
	return ("Comment OpCode\t kind = "+QDComment.toString(kind)) ;
    }// toString
    
    /** executes the comment 
     *  @see jqd.basics.QDPort#stdComment
     */ 

    public void	execute(QDPort thePort) throws QDException  {
	thePort.stdComment(this);
    } // execute 
    
    /** Optimization method: most PostScript related comments and
     *  the proprietary comments are discarded.
     *  @return <code>true</code> if the comment can be discarded 
     */ 

    public boolean discard() {
	if (kind==PROPRIETARY) return true;
	if (kind>=190 && kind<197) return true;
	if (kind>220) return true ;
	return false;
    } // discard

} // QDShortComment

