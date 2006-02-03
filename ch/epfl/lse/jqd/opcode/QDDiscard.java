//                              -*- Mode: Java -*- 
// QDDiscard.java --- 
// Author          : Matthias Wiesmann
// Created On      : Fri Jul  2 11:53:29 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Tue Nov 21 15:17:28 2000
// Update Count    : 3
// Status          : OK
// 

package ch.epfl.lse.jqd.opcode;

/** This interface is implemented by opcodes 
 * that may be optimized out, for instance 
 * Unimplemented comments
 * @see QDShortComment
 * @see QDNoOp
 * @author Matthias Wiesmann
 * @version 1.0
 */ 


public interface QDDiscard {
    boolean discard();
} // QDDiscard
