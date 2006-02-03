//                              -*- Mode: Java -*- 
// QDClipRgnOP.java --- 
// Author          : Matthias Wiesmann
// Created On      : Tue Nov 21 15:15:38 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 11:39:14 2000
// Update Count    : 5
// Status          : Unknown, Use with caution!
// 

package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.io.*;

import java.io.IOException;

/** Clipping opcode 
 * @author Matthias Wiesmann
 * @version 2.0 
 */ 

public class QDClipRgnOP implements QDOpCode {
    QDRegion clipRgn ;
    public int read(QDInputStream theStream) 
	throws IOException {
	clipRgn= new QDRegion();
	final int dataSize = clipRgn.read(theStream) ;
	return(dataSize);
    } // read 
    
    public String toString() { 
	return ("Clip Region "+clipRgn); } // toString
    
    public void	execute(QDPort thePort) throws QDException {
	thePort.clip(clipRgn);
    } // execute 
} // QDOpCode
