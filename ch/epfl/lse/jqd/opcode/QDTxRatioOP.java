//                              -*- Mode: Java -*- 
// QDTxRatioOP.java --- 
// Author          : Matthias Wiesmann
// Created On      : Mon Jul 12 18:00:17 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 13:46:40 2000
// Update Count    : 4
// Status          : OK
// 


package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.io.QDInputStream;
import java.io.IOException;
 
/** Text Ratio Control Opcode.<br> 
 *  <strong>Warning</strong>: does nothing for the moment
 *  @author Matthias Wiesmann
 *  @version 1.1 
 */ 


public class QDTxRatioOP extends QDNoOp implements QDOpCode  {
    protected double x ;
    protected double y ; 

    public int read(QDInputStream theStream) 
	throws IOException {
	x = theStream.readFrac();
	y = theStream.readFrac();
	return(QDInputStream.FRAC_SIZE*2);
    } // read

    public String toString()  {
	return "Text ratio"+
	       "[x="+x+",y="+y+"]" ;
    }
} // QDTxRatioOP





