//                              -*- Mode: Java -*- 
// QDDHDVTextOP.java --- 
// Author          : Matthias Wiesmann
// Created On      : Thu Dec 16 16:59:38 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Thu Nov 30 15:41:49 2000
// Update Count    : 2
// Status          : Renamed
// 

package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.utils.*;
import java.awt.Dimension;

import ch.epfl.lse.jqd.io.QDInputStream;

/** This opcode draws text at a given position 
 *  @author Matthias Wiesmann
 *  @version 2.0 revised
 */ 

public class QDDHDVTextOP implements QDOpCode 
{
    /** delta from current text location */
    protected Dimension delta ;
    /** the text to draw */ 
    protected String	text ;
    
    public int read(QDInputStream theStream) 
	throws java.io.IOException, QDException {
	delta = theStream.readDHDV();
	text = theStream.readString();
	return(3+text.length());
    } // read

    public void	execute(QDPort thePort) 
	throws QDException {
	thePort.txLoc=QDUtils.addDimension(thePort.txLoc,delta);
	thePort.stdText(text);
    } // execute
 
    protected String opName() {
	return("DH DV");
    } // opName
    
    public String toString()  {
	return opName()+" Text "+delta+
	    "\t"+text
	    ;}
} // QDHeaderOP

