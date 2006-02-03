//                              -*- Mode: Java -*- 
// QDReservedOP.java --- 
// Author          : Matthias Wiesmann
// Created On      : Wed Jul 14 11:00:06 1999
// Last Modified By: Matthias Wiesmann
// Last Modified On: Fri Dec  1 12:03:51 2000
// Update Count    : 34
// Status          : Unknown, Use with caution!
// 
package ch.epfl.lse.jqd.opcode;

import java.io.IOException;
import ch.epfl.lse.jqd.io.QDInputStream;

/** This class represents reserved opcodes.
 *  Thoses codes have not yet been defined by Apple
 *  (and probably never will), but still must be parsed
 *  correctly so as to be ignored.
 *  This class implementes a ignored opcode with a fixed length.
 *  It also offers static instances for certain classic lengths:
 *  <ul><li>no additionnal length (0 bytes)
 *  <li>Macintosh integer (2 bytes)
 *  <li>Macintosh long integer (4 bytes)
 *  <li>Macintosh point (4 integers / 8 bytes)
 *  <li>6 Macinstosh integers 
 *  </ul>
 *  @author Matthias Wiesmann
 *  @version 1.0
 */ 

public class QDReservedOP extends QDNoOp {

    protected static final QDReservedOP RESERVED_0 = new QDReservedOP(0);
    protected static final QDReservedOP RESERVED_2 = new QDReservedOP(2);
    protected static final QDReservedOP RESERVED_4 = new QDReservedOP(4);
    protected static final QDReservedOP RESERVED_8 = new QDReservedOP(8);
    protected static final QDReservedOP RESERVED_12 = new QDReservedOP(12);

    protected int length  ;

    /** protected constructor 
     *  the length field is not valid
     */ 

    protected QDReservedOP() {
	this(-1);
    } // QDReservedOP

    /** builds a reserved opcode for a constant 
     *  length
     *  @var length the length (in bytes) of the opcode
     */

    public QDReservedOP(int length) {
	this.length = length;
    } // QDReservedOP

    /** skips the right amount of data 
     *  @param stream the input stream
     *  @exception IOException I/O problem
     */ 

    public int read(QDInputStream stream)
	throws IOException {
	stream.skipBytes(length);
	return length; 
    } // read

    public String toString() {
	return "reserved operation "+length;
    }// toString

    /** Factory method, builds the right reserved opcode.<br>
     *  <strong>Warning</strong> this method does not check 
     *  for existing (non reserved) opcodes. 
     *  Because this method works using ranges, existing, 
     *  non reserved opcodes migth be returned as reserved
     *  opcodes.<br>
     *  This method should only be called if no opcode 
     *  was found for the number
     *  @param code the number of the opcode
     *  @return the right reserved opcode or null if none was found
     */ 
    
    public static final QDReservedOP factory(int code) {
	switch(code) {
	case 0x24: case 0x25: case 0x26: case 0x27: case 0x2f:
	    return new QDReservedIntOP();
	case 0x3D: case 0x3e: case 0x3f:
	case 0x4d: case 0x4e: case 0x4f:
	case 0x7d: case 0x7e: case 0x7f:
	case 0x8d: case 0x8e: case 0x8f:
	    return RESERVED_0;
	case 0x6d: case 0x6e: case 0x6f:
	    return RESERVED_4;
	case 0x35: case 0x36: case 0x37:
	case 0x45: case 0x46: case 0x47:
	case 0x55: case 0x56: case 0x57:
	    return RESERVED_8;
	case 0x65: case 0x66: case 0x67:
	    return RESERVED_12;
	case 0x85: case 0x86: case 0x87:
	    return new QDRegionReservedOP();
	} // switch
	if ((code >0x90) && (code<0xB0))
	    return new QDReservedIntOP();
	if ((code>=0xB0) && (code<0xD0))
	    return RESERVED_0;
	if ((code>=0xD0) && (code<0xFF))
	    return new QDReservedLongOP();
	if ((code>0x100) && (code<0x8000)){
	    final int size = 2* code / 0x100 ;
	    return new QDReservedOP(size);
	} // if
	if ((code>=0x8000) && (code<0x8100))
	    return RESERVED_0;
	if (code>=0x8100) 
	    return new QDReservedLongOP();
	return  null ;
    } // factory
} // QDReservedOP
