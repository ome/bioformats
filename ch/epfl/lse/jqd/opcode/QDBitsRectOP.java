//                              -*- Mode: Java -*- 
// QDBitsRectOP.java --- 
// Author          : Matthias Wiesmann
// Created On      : Thu Dec 16 16:53:08 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Thu Dec 16 16:55:18 1999
// Update Count    : 3
// Status          : Renamed
// 


package ch.epfl.lse.jqd.opcode;

import java.io.IOException;

import ch.epfl.lse.jqd.basics.*;
import ch.epfl.lse.jqd.image.*;
import ch.epfl.lse.jqd.io.QDInputStream;

/** This class does a bitmap copy 
 *  @author Matthias Wiesmann
 *  @version 2.0
 */ 

public class QDBitsRectOP implements QDOpCode 
{
    protected QDBitMap bMap ;
    
    public int read(QDInputStream theStream) 
	throws IOException, QDException {
	short rowbytes = theStream.readShort();
	bMap = QDBitMap.newMap(rowbytes);
	int bSize = bMap.read(theStream);
	return(bSize+2);
    }// read

    public void	execute(QDPort thePort)
	throws QDException {
	thePort.stdBits(bMap);
    } // execute 

    public String toString() {
	return "Bit Rect "+bMap;
    } // toString
} // QDBitsRectOP


