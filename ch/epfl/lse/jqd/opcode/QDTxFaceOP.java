//                              -*- Mode: Java -*- 
// QDTxFaceOP.java --- 
// Author          : Matthias Wiesmann
// Created On      : Fri Dec  1 11:00:34 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 11:00:56 2000
// Update Count    : 1
// Status          : Unknown, Use with caution!
// 

package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.io.QDInputStream;

public class QDTxFaceOP implements QDOpCode {
    protected int style ;
    public int 	read(QDInputStream theStream) throws java.io.IOException {
	style=theStream.readStyle() ;
	return(QDInputStream.STYLE_SIZE);
    } // read
    public void	execute(QDPort thePort) {
	thePort.txFace=style ;
	thePort.txOperation();
    } // execute 
    public String toString()  {
	return("Text style "+style);}
} // QDTxFaceOP
