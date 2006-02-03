//                              -*- Mode: Java -*- 
// QDVersionOP.java --- 
// Author          : Matthias Wiesmann
// Created On      : Fri Dec  1 11:03:34 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 11:03:46 2000
// Update Count    : 1
// Status          : Unknown, Use with caution!
// 

package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.io.QDInputStream;

/** This opcode sets the picture version 1 or 2 
 * @author Matthias Wiesmann
 * @version 2.0
 */ 

public class QDVersionOP implements QDOpCode, QDHeaderExec {
    public  int version ;
	
    public int 	read(QDInputStream theStream) throws java.io.IOException {
	    version = theStream.readUnsignedByte();
	    if (version==1) return (1) ;
	    theStream.skipBytes(1);
	    return(2);
	} // load
	
    public String toString() {
	    return ("Version Opcode\t version = "+Integer.toString(version,16)) ;
	}// toString
		
    public void headerExecute(QDPicture thePicture){
	    thePicture.version = this.version ;
	} // headerExecute
		
    public void	execute(QDPort thePort) {
} // execute 
} // QDVersionOP
	



