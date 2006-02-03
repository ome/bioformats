//                              -*- Mode: Java -*- 
// QDReservedIntOP.java --- 
// Author          : Matthias Wiesmann
// Created On      : Wed Jul 14 11:32:11 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 12:03:04 2000
// Update Count    : 7
// Status          : OK
// 

package ch.epfl.lse.jqd.opcode;

import java.io.IOException;
import ch.epfl.lse.jqd.io.QDInputStream;

/** This opcode is a variable length reserved opcode 
 *  with the length given in a Macintosh integer.
 *  @author Matthias Wiesmann
 *  @version 1.0
 */ 

public class QDReservedIntOP extends QDReservedOP 
{
     /** @exception IOException I/O problem */ 
    public int read(QDInputStream stream)
	throws IOException {
	length = stream.readShort();
	stream.skipBytes(length);
	return length + 2; 
    } // read

} // QDReservedIntOP
