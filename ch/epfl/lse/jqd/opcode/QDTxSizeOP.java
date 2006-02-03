//                              -*- Mode: Java -*- 
// QDTxSizeOP.java --- 
// Author          : Matthias Wiesmann
// Created On      : Fri Dec  1 11:02:38 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 11:02:51 2000
// Update Count    : 2
// Status          : Unknown, Use with caution!
// 

package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.io.QDInputStream;

/** This operation sets the size of text */ 

public class QDTxSizeOP implements QDOpCode {
	protected short size ;
    public int 	read(QDInputStream theStream) throws java.io.IOException {
	size=theStream.readShort() ;
	return(2);
    } // read
    public void	execute(QDPort thePort) {
	thePort.txSize=size ;
	thePort.txOperation();
    } // execute 
    public String toString()  {
	return("Text size "+size);}
} // QDHeaderOP
