//                              -*- Mode: Java -*- 
// QDPolyOpCode.java --- 
// Author          : Matthias Wiesmann
// Created On      : Mon Jul 12 17:03:38 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 11:04:38 2000
// Update Count    : 6
// Status          : OK
// 

package ch.epfl.lse.jqd.opcode;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.io.QDInputStream;
import ch.epfl.lse.jqd.utils.*;
import java.awt.Polygon;
import java.io.IOException;

/** This class describes a polygon drawing opcode
 *  @author Matthias Wiesmann
 *  @version 1.1
 */ 

public class QDPolyOpCode extends QDShapeOpCode implements QDOpCode 
{
    protected Polygon poly ;

    public QDPolyOpCode(short verb) {
	super(verb); 
    } // QDPolyOpCode

    public int 	read(QDInputStream stream) 
	throws IOException {
	poly = stream.readPoly();
	bounds = poly.getBounds();
	return(QDInputStream.polySize(poly)) ;
    }// read
	
    public void	execute(QDPort port) throws QDException {
	port.stdPoly(verb,poly);
    } // execute 

    public String toString() {
	return super.toString() + QDUtils.toString(poly);
    } // toString

} // QDPolyOpCode

