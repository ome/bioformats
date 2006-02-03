//                              -*- Mode: Java -*- 
// QDShapeOpCode.java --- 
// Author          : Matthias Wiesmann
// Created On      : Mon Jul 12 16:33:42 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 10:42:49 2000
// Update Count    : 11
// Status          : OK
// 

package ch.epfl.lse.jqd.opcode;

import java.awt.Rectangle;
import ch.epfl.lse.jqd.basics.QDVerbs;

/** This class serves as super-class for all opcodes 
 *  dealing with shapes drawing opcodes 
 *  
 *  @author Matthias Wiesmann
 *  @version 1.0
 */ 

public abstract class QDShapeOpCode 
{
    /** the bounds of the shape */
    protected Rectangle bounds ;
    /** the verb to use for drawing */ 
    protected final short verb ;
    
    public QDShapeOpCode(short verb) {
	this.verb = verb ;
    } // QDShapeOpCode
    
    public Rectangle getBounds() {
	return bounds ;
    } // getBounds

    public String toString() {
	return QDVerbs.toString(verb) ;
    } // toString
    
    
} // QDShapeOpCode
