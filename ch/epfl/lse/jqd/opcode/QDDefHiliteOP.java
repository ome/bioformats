//                              -*- Mode: Java -*- 
// QDDefHiliteOP.java --- 
// Author          : Matthias Wiesmann
// Created On      : Fri Jul  2 12:00:10 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Thu Nov 30 15:40:13 2000
// Update Count    : 13
// Status          : OK
// 

package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.io.*;

import java.awt.Color;
import java.awt.SystemColor;

/** This opcode marks that the next operation 
 *  uses the highlight color.<br>
 *  Support is little bit flacky, as it uses the 
 *  java inversion mode. 
 * @author Matthias Wiesmann
 * @version 1.0 revised
 */

public class QDDefHiliteOP implements QDOpCode {
    public int 	read(QDInputStream theStream) 
	throws java.io.IOException {
	return(0);
    } // read

    /** Finds the highlight color.<br>
     *  Uses the <code>SystemColor.textHighlight</code> color
     *  @return the highlight color
     *  @see java.awt.SystemColor#textHighlight
     */ 
 
    protected static Color getHilite() {
	try {
	    return SystemColor.textHighlight ;
	} catch(RuntimeException e) {
	    return Color.white ;
	} // try - catch
    } // getHilite

    public void	execute(QDPort thePort) {
	thePort.hlColor = getHilite() ;
    } // execute 

    public String toString()  {
	    return "Define Hilight: "+getHilite() ;
    } // toString
} // QDHeaderOP
