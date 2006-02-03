//                              -*- Mode: Java -*- 
// QDLineOP.java --- 
// Author          : Matthias Wiesmann
// Created On      : Tue Nov 21 15:08:58 2000
// Last Modified By: Matthias Wiesmann
// Last Modified On: Thu Nov 30 15:49:28 2000
// Update Count    : 4
// Status          : Unknown, Use with caution!
// 

package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import java.awt.*;
import java.io.IOException;
import ch.epfl.lse.jqd.io.QDInputStream;

/** This opcode draws a line 
 *  @version 1.0 revised
 *  @author Matthias Wiesmann
 *  @see ch.epfl.lse.jqd.basics.QDPort#stdPoint
 *  @see ch.epfl.lse.jqd.basics.QDPort#stdLine
 */ 

public class QDLineOP implements QDOpCode {
    /** start of the line */ 
    protected Point start ;
    /** end of the line */ 
    protected Point end ;

    /** reads the line (2 points). 
     *  @param theStream input stream
     *  @return number of bytes read 
     */

    public int 	read(QDInputStream theStream) 
	throws IOException {
	start = theStream.readPoint();
	end = theStream.readPoint();
	return(8);
    } // read

    /** Draws the line using the standart 
     *  port bottleneck functions 
     *  @param thePort the destination port
     */ 

    public void	execute(QDPort thePort) 
	throws QDException {
	thePort.stdPoint(start);
	thePort.stdLine(end);
    } // execute 

    public String toString() {
	return("Line "+start+" -"+end);
    } // toString
} // QDpnSizeOP
