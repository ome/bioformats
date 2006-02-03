//                              -*- Mode: Java -*- 
// QDColorOP.java --- 
// Author          : Matthias Wiesmann
// Created On      : Fri Jul  9 11:48:52 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Thu Nov 30 15:35:34 2000
// Update Count    : 8
// Status          : Renamed
// 

package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;

import java.awt.Color;

import java.io.IOException;
import ch.epfl.lse.jqd.io.QDInputStream;

/** This opcode selects the color mode.
 * @author Matthias Wiesmann
 * @version 1.1
 */

public class QDColorOP implements QDOpCode 
{
    public static final short BACK_COLOR_VERB = 0 ;
    public static final short FRONT_COLOR_VERB  = 1 ;
    public static final short OP_COLOR_VERB = 2 ;
    public static final short HILITE_COLOR_VERB = 3 ;
	
    protected Color theColor ;
    protected final short type ;
	
    public int 	read(QDInputStream theStream) 
	throws IOException, QDException {
	    theColor = theStream.readColor();
	    return(6);
	} // read

    public QDColorOP(short a_type) {
	    this.type = a_type ;
	} // QDColorOP
		
    /** Transforms a QuickDraw verb into text
     * @param a_type QuickDraw verb 
     * @return text
     */

    protected static String verb2String(short a_type) {
	switch (a_type) {
	case BACK_COLOR_VERB:   return("back");
	case FRONT_COLOR_VERB:  return("front");
	case OP_COLOR_VERB:     return("operation");
	case HILITE_COLOR_VERB: return("hilite");
	} // switch
	return("");
    } // toString
    
   

    /** Executes the opcode.<br>
     *  <strong>Warning</strong>:
     * the <code>operation</code> color verb is not
     * handled
     * @param thePort the port to execute in
     */ 

    public void	execute(QDPort thePort) {
	switch (type) {
	case FRONT_COLOR_VERB:  
	    thePort.fgColor = theColor ; 
	    break ;
	case BACK_COLOR_VERB:   
	    thePort.bgColor = theColor ; 
	    break ;
	case HILITE_COLOR_VERB: 
	    thePort.hlColor = theColor ;
	    break ;
	} // switch
	thePort.colorOperation();
    } // execute 
    
    
    public String toString() {
	return(verb2String(type)+" color opcode"+theColor);}
} // QDHeaderOP
