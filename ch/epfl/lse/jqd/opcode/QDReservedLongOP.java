//                              -*- Mode: Java -*- 
// QDReservedLongOP.java --- 
// Author          : Matthias Wiesmann
// Created On      : Wed Jul 14 14:07:58 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 12:03:26 2000
// Update Count    : 4
// Status          : OK
// 

package ch.epfl.lse.jqd.opcode;

import java.io.IOException;
import ch.epfl.lse.jqd.io.QDInputStream;
/** This opcode is a variable length reserved opcode 
 *  with the length given in a Macintosh long.
 *  @author Matthias Wiesmann
 *  @version 1.0
 */ 

public class QDReservedLongOP extends QDReservedIntOP 
{
    /** @exception IOException I/O problem */ 
    public int read(QDInputStream stream)
	throws IOException {
	length = stream.readInt();
	stream.skipBytes(length);
	return length + 4; 
    } // read

} // QDReservedIntOP
